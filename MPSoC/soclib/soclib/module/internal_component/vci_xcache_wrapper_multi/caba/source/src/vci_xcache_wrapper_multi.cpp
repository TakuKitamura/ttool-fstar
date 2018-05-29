/* -*- c++ -*-
 *
 * SOCLIB_LGPL_HEADER_BEGIN
 *
 * This file is part of SoCLib, GNU LGPLv2.1.
 *
 * SoCLib is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; version 2.1 of the License.
 *
 * SoCLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SoCLib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * SOCLIB_LGPL_HEADER_END
 *
 * Copyright (c) UPMC, Lip6, SoC
 *         Alain Greiner <alain.greiner@lip6.fr>
 *
 * Maintainers: alain eric.guthmuller@polytechnique.edu nipo
 */

////////////////////////////////////////////////////////////////////////////////////
// History
// - 01/06/2008
//   The VCI_XCACHE and the ISS_WRAPPER components have been merged, in order
//   to increase the simulation speed: this VCI_XCACHE_WRAPPER component
//   is directly wrapping the processsor ISS, allowing a direct communication
//   between the processor and the cache.
//   The number of associativity levels is now a parameter for both the data
//   and the instruction cache.
// - 15/07/2008
//   The data & instruction caches implementation have been modified, in order
//   to use the GenericCache object. Similarly, the write buffer implementation
//   has been modified to use the WriteBuffer object.
//   A VCI write burst is constructed when two conditions are satisfied :
//   The processor make strictly successive write requests, and they are
//   in the same cache line. The write buffer performs re-ordering, to
//   respect the contiguous addresses VCI constraint. In case of several
//   WRITE_WORD requests in the same word, only the last request is conserved.
//   In case of several WRITE_HALF or WRITE_BYTE requests in the same word,
//   the requests are merged in the same word. In case of uncachable write
//   requests, each request is transmited as a single flit VCI transaction.
//   Both the data & instruction caches can be flushed in one single cycle.
//   Finally, new activity counters have been introduced for instrumentation.
// - 19/08/2008
//   The VCI CMD FSM has been modified to send single cell packet in case of MISS.
//   The uncachable mode (using the cacheability table) has been introduced  in the
//   ICACHE FSM.
// - 26/12/2009
//   The VCI_XCACHE_WRAPPER_MULTI has been derived from the VCI_XCACHE_WRAPPER
//   to support several concurrent VCI transactions, in order to improve the CPI.
//   A new write buffer supporting simultaneous write transactions has been 
//   introduced. The VCI command & response FSMs are not synchronized anymore:
//   The read requests can be transmitted before previous write requests
//   (if the missing address does not match a pending write in the write buffer).
//   The transactions can complete in any order, depending on the network.
//   Two new flip-flops have been introduced to signal completion of the
//   read transactions from the RSP FSM to the DCACHE & ICACHE FSMs: 
//   r_rsp_data_ok, r_rsp_ins_ok
//   As simultaneous VCI transactions are supported, the PKTID & RPKTID fields
//   are used to transport the transaction index.
//   The transaction index has an odd value for a write transaction, depending
//   on the line index in the write buffer: PKTID = 2*wbuf_index + 1
//   The transaction index has an even value for a read transaction, with only
//   only four possible values, depending on cachable/uncachable & data/instruction.
// - 22/05/2010
//   The DCACHE FSM has been modified to enforce a well defined consistency model:
//   1) All uncachable acces (both read and write) are now blocking
//      the processor until the VCI response is received.
//      Uncachable access are considered as I/O access and must respect
//      a strict sequencial policy.
//   2) For cachable access, the write buffer supports the "read after write"
//      rule, and the "write after write" rule : registered write requests
//      are tested before handling a read miss, and before locking a buffer line.
//   The MultiWriteBuffer component has been modified, to support
//   these rules, and to have an associative behavior: it can exist several
//   open lines, with a private time-out for each open line.
//   A printTrace() method has been defined.
// - 12/09/2010
//   The VCI RSP FSM has been modified to accept single flit packets
//   in case of read bus errors. 
// - 08/01/2012
//   The deprecated methods to access the generic_cache have been replaced
//   by methods that does not require an associative search to modify cache content.
//   This required new states ICACHE_MISS_SELECT, DCACHE_MISS_SELECT, DCACHE_INVAL_GO 
//   and new registers to save the modified  way, set and word.
/////////////////////////////////////////////////////////////////////////////////////

#include <cassert>
#include <limits>
#include "arithmetics.h"
#include "../include/vci_xcache_wrapper_multi.h"

namespace soclib {
namespace caba {

namespace {
const char *dcache_fsm_state_str[] = {
        "DCACHE_IDLE       ",
        "DCACHE_WRITE_UPDT ",
        "DCACHE_WRITE_REQ  ",
        "DCACHE_MISS_SELECT",
        "DCACHE_MISS_WAIT  ",
        "DCACHE_MISS_UPDT  ",
        "DCACHE_UNC_WAIT   ",
        "DCACHE_UNC_GO     ",
        "DCACHE_INVAL      ",
        "DCACHE_INVAL_GO   ",
        "DCACHE_SYNC       ",
        "DCACHE_ERROR      ",
    };
const char *icache_fsm_state_str[] = {
        "ICACHE_IDLE       ",
        "ICACHE_MISS_SELECT",
        "ICACHE_MISS_WAIT  ",
        "ICACHE_MISS_UPDT  ",
        "ICACHE_UNC_WAIT   ",
        "ICACHE_UNC_GO     ",
        "ICACHE_ERROR      ",
    };
const char *cmd_fsm_state_str[] = {
        "CMD_IDLE      ",
        "CMD_INS_MISS  ",
        "CMD_INS_UNC   ",
        "CMD_DATA_MISS ",
        "CMD_DATA_UNC  ",
        "CMD_DATA_WRITE",
    };
const char *rsp_fsm_state_str[] = {
        "RSP_IDLE      ",
        "RSP_INS_MISS  ",
        "RSP_INS_UNC   ",
        "RSP_DATA_MISS ",
        "RSP_DATA_UNC  ",
        "RSP_DATA_WRITE",
    };
}

#define tmpl(...)  template<typename vci_param, typename iss_t> __VA_ARGS__ VciXcacheWrapperMulti<vci_param, iss_t>

using soclib::common::uint32_log2;

////////////////////////////////////
tmpl(/**/)::VciXcacheWrapperMulti(
    sc_module_name name,
    int proc_id,
    const soclib::common::MappingTable &mt,
    const soclib::common::IntTab &index,
    size_t icache_ways,
    size_t icache_sets,
    size_t icache_words,
    size_t dcache_ways,
    size_t dcache_sets,
    size_t dcache_words, 
    size_t wbuf_nwords,
    size_t wbuf_nlines)
    :
      soclib::caba::BaseModule(name),

      p_clk("clk"),
      p_resetn("resetn"),
      p_vci("vci"),

      m_cacheability_table(mt.getCacheabilityTable()),
      m_srcid(mt.indexForId(index)),

      m_dcache_ways(dcache_ways),
      m_dcache_words(dcache_words),
      m_dcache_yzmask((~0)<<(uint32_log2(dcache_words) + 2)),
      m_icache_ways(icache_ways),
      m_icache_words(icache_words),
      m_icache_yzmask((~0)<<(uint32_log2(icache_words) + 2)),

      m_iss(this->name(), proc_id),

      r_dcache_fsm("r_dcache_fsm"),
      r_dcache_addr_save("r_dcache_addr_save"),
      r_dcache_wdata_save("r_dcache_wdata_save"),
      r_dcache_rdata_save("r_dcache_rdata_save"),
      r_dcache_type_save("r_dcache_type_save"),
      r_dcache_be_save("r_dcache_be_save"),
      r_dcache_way_save("r_dcache_way_save"),
      r_dcache_set_save("r_dcache_set_save"),
      r_dcache_word_save("r_dcache_word_save"),
      r_dcache_miss_req("r_dcache_miss_req"),
      r_dcache_unc_req("r_dcache_unc_req"),

      r_icache_fsm("r_icache_fsm"),
      r_icache_addr_save("r_icache_addr_save"),
      r_icache_miss_req("r_icache_miss_req"),
      r_icache_unc_req("r_icache_unc_req"),

      r_cmd_fsm("r_cmd_fsm"),
      r_cmd_min("r_cmd_min"),
      r_cmd_max("r_cmd_max"),
      r_cmd_cpt("r_cmd_cpt"),

      r_rsp_fsm("r_rsp_fsm"),
      r_rsp_ins_error("r_rsp_ins_error"),
      r_rsp_data_error("r_rsp_data_error"),
      r_rsp_cpt("r_rsp_cpt"),
      r_rsp_ins_ok("r_rsp_ins_ok"),
      r_rsp_data_ok("r_rsp_data_ok"),

      r_wbuf("wbuf", wbuf_nwords, wbuf_nlines, dcache_words),
      r_icache("icache", icache_ways, icache_sets, icache_words),
      r_dcache("dcache", dcache_ways, dcache_sets, dcache_words)
{
    r_icache_miss_buf = new data_t[icache_words];
    r_dcache_miss_buf = new data_t[dcache_words];

    assert( (icache_words*vci_param::B) < (1<<vci_param::K) && 
            "I need more PLEN bits");

    assert( (vci_param::T > 2) && ((1<<(vci_param::T-1)) >= wbuf_nlines) &&
            "I need more TRDID bits");

    SC_METHOD(transition);
    dont_initialize();
    sensitive << p_clk.pos();

    SC_METHOD(genMoore);
    dont_initialize();
    sensitive << p_clk.neg();

    typename iss_t::CacheInfo cache_info;
    cache_info.has_mmu = false;
    cache_info.icache_line_size = icache_words*sizeof(data_t);
    cache_info.icache_assoc = icache_ways;
    cache_info.icache_n_lines = icache_sets;
    cache_info.dcache_line_size = dcache_words*sizeof(data_t);
    cache_info.dcache_assoc = dcache_ways;
    cache_info.dcache_n_lines = dcache_sets;
    m_iss.setCacheInfo(cache_info);
}

////////////////////////////////////
tmpl(/**/)::~VciXcacheWrapperMulti()
{
    delete [] r_icache_miss_buf;
    delete [] r_dcache_miss_buf;
}

//////////////////////////////////
tmpl(void)::file_trace(FILE* file)
{
    if(r_dcache_updated) 
    {
        fprintf(file, "*******  DATA  **************  cycle %d\n", m_cpt_total_cycles);
        r_dcache.fileTrace(file);
    }
    if(r_icache_updated) 
    {
        fprintf(file, "*******  INSTRUCTION  *******  cycle %d\n", m_cpt_total_cycles);
        r_icache.fileTrace(file);
    }
}

//////////////////////////////////
tmpl(void)::file_stats(FILE* file)
{
    float imiss_rate = (float)m_cpt_ins_miss / (float)(m_cpt_exec_ins);
    float dmiss_rate = (float)m_cpt_data_miss / (float)(m_cpt_read - m_cpt_data_unc);
    float cpi		 = (float)m_cpt_total_cycles / (float)(m_cpt_exec_ins);

    fprintf(file,"%8d %8d %8d %8d %8d    %f    %f    %f \n", 
            m_cpt_total_cycles, 
            m_cpt_exec_ins,
            m_cpt_ins_miss,
            m_cpt_read - m_cpt_data_unc,
            m_cpt_data_miss,
            imiss_rate, 
            dmiss_rate,
            cpi);
}


////////////////////////////////////
tmpl(void)::print_trace(size_t mode)
{
    std::cout << std::dec << "PROC " << name() << std::endl;

    std::cout << "  " << m_ireq << std::endl;
    std::cout << "  " << m_irsp << std::endl;
    std::cout << "  " << m_dreq << std::endl;
    std::cout << "  " << m_drsp << std::endl;

    std::cout << "  " << icache_fsm_state_str[r_icache_fsm.read()]
              << " | " << dcache_fsm_state_str[r_dcache_fsm.read()]
              << " | " << cmd_fsm_state_str[r_cmd_fsm.read()]
              << " | " << rsp_fsm_state_str[r_rsp_fsm.read()] << std::endl;

    if(mode & 0x1)
    {
        r_wbuf.printTrace();
    }

    if(((mode & 0x2) != 0) && r_dcache_updated)
    {
        std::cout << "  Data cache" << std::endl;
        r_dcache.printTrace();
    }

    if(((mode & 0x4) != 0) && r_icache_updated)
    {
        std::cout << "  Instruction cache" << std::endl;
        r_icache.printTrace();
    }
}

/////////////////////////
tmpl(void)::print_stats()
{
    std::cout << "------------------------------------" << std:: dec << std::endl
    << "CPU " << m_srcid << " / cycles = " << m_cpt_total_cycles << std::endl
    << "- CPI               = " << (float)m_cpt_total_cycles/(float)m_cpt_exec_ins << std::endl
    << "- READ RATE         = " << (float)m_cpt_read/(float)m_cpt_exec_ins << std::endl
    << "- WRITE RATE        = " << (float)m_cpt_write/(float)m_cpt_exec_ins << std::endl
    << "- CACHED WRITE RATE = " << (float)m_cpt_write_cached/m_cpt_write << std::endl
    << "- UNC RATE          = " << (float)m_cpt_data_unc/(float)m_cpt_exec_ins << std::endl
    << "- LL RATE           = " << (float)m_cpt_ll/(float)m_cpt_exec_ins << std::endl
    << "- SC RATE           = " << (float)m_cpt_sc/(float)m_cpt_exec_ins << std::endl
    << "- IMISS_RATE        = " << (float)m_cpt_ins_miss/(float)m_cpt_exec_ins << std::endl
    << "- IMISS COST        = " << (float)m_cost_ins_miss_frz/m_cpt_ins_miss << std::endl
    << "- DMISS RATE        = " << (float)m_cpt_data_miss/m_cpt_read << std::endl
    << "- DMISS COST        = " << (float)m_cost_data_miss_frz/m_cpt_data_miss << std::endl
    << "- UNC COST          = " << (float)m_cost_unc_frz/m_cpt_data_unc << std::endl
    << "- WRITE COST        = " << (float)m_cost_write_frz/m_cpt_write << std::endl
    << "- WRITE LENGTH      = " << (float)m_length_write_transaction/m_cpt_write_transaction
    << std::endl;
}

////////////////////////
tmpl(void)::transition()
{
    if ( ! p_resetn.read() ) 
    {
        m_iss.reset();

        // FSM states
        r_dcache_fsm 	= DCACHE_IDLE;
        r_icache_fsm 	= ICACHE_IDLE;
        r_cmd_fsm 	    = CMD_IDLE;
        r_rsp_fsm 	    = RSP_IDLE;

        // write buffer & caches
        r_wbuf.reset();
        r_icache.reset();
        r_dcache.reset();

        // synchronisation flip-flops from ICACHE & DCACHE FSMs to CMD  FSM
        r_icache_miss_req    = false;
        r_dcache_miss_req    = false;
        r_icache_unc_req     = false;
        r_dcache_unc_req     = false;

        // synchronisation flip-flops from the RSP FSM to the ICACHE or DCACHE FSMs
        r_rsp_data_ok        = false;
        r_rsp_ins_ok         = false;
        r_rsp_data_error     = false;
        r_rsp_ins_error      = false;

        // activity counters
        m_cpt_dcache_data_read     = 0;
        m_cpt_dcache_data_write    = 0;
        m_cpt_dcache_dir_read      = 0;
        m_cpt_dcache_dir_write     = 0;

        m_cpt_icache_data_read     = 0;
        m_cpt_icache_data_write    = 0;
        m_cpt_icache_dir_read      = 0;
        m_cpt_icache_dir_write     = 0;

        m_cpt_exec_ins             = 0;
        m_cpt_total_cycles         = 0;

        m_cpt_read                 = 0;
        m_cpt_write                = 0;
        m_cpt_write_cached         = 0;
        m_cpt_data_unc             = 0;
        m_cpt_ins_unc              = 0;
        m_cpt_ll                   = 0;
        m_cpt_sc                   = 0;
        m_cpt_data_miss            = 0;
        m_cpt_ins_miss             = 0;

        m_cost_write_frz           = 0;
        m_cost_data_miss_frz       = 0;
        m_cost_unc_frz             = 0;
        m_cost_ins_miss_frz        = 0;

        m_cpt_imiss_transaction    = 0;
        m_cpt_dmiss_transaction    = 0;
        m_cpt_data_unc_transaction = 0;
        m_cpt_ins_unc_transaction  = 0;
        m_cpt_write_transaction    = 0;

        m_length_write_transaction = 0;

        return;
    }

#ifdef SOCLIB_MODULE_DEBUG
std::cout << std::dec << "Xcache " << m_srcid << " / Time = " << m_cpt_total_cycles << std::endl
          << "  " << dcache_fsm_state_str[r_dcache_fsm]
          << "  " << icache_fsm_state_str[r_icache_fsm]
          << "  " << cmd_fsm_state_str[r_cmd_fsm]
          << "  " << rsp_fsm_state_str[r_rsp_fsm] << std::endl;
#endif

    m_cpt_total_cycles++;

    r_icache_updated = (r_icache_fsm.read() == ICACHE_MISS_UPDT);
    r_dcache_updated = ((r_dcache_fsm.read() == DCACHE_MISS_UPDT) ||
                        (r_dcache_fsm.read() == DCACHE_WRITE_UPDT));

    m_iss.getRequests( m_ireq, m_dreq );

    m_irsp.valid = false;
    m_irsp.error = false;
    m_drsp.valid = false;
    m_drsp.error = false;

    /////////////////////////////////////////////////////////////////////
    // The ICACHE FSM controls the following ressources:
    // - r_icache_fsm
    // - r_icache (instruction cache access)
    // - r_icache_addr_save
    // - r_icache_way_save
    // - r_icache_set_save
    // - r_icache_miss_req set
    // - r_icache_unc_req set
    // - r_rsp_ins_ok reset
    // - r_rsp_ins_error reset
    // - m_ireq & m_irsp structures for communication with the processor
    //
    // Processor requests are taken into account only in the IDLE state.
    // In case of MISS or in case of uncachable request, the FSM 
    // writes the missing address line in the  r_icache_addr_save register 
    // and sets the r_icache_miss_req (or the r_icache_unc_req) flip-flop.
    // The request flip-flop is reset by the CMD FSM when the VCI command
    // has been send. 
    // The RSP FSM sets the r_rsp_ins_ok flip-flop to signal the availability 
    // of data in the r_icache_buf buffer.
    // These two flip-flops are reset by the ICACHE_FSM. 
    // In case of bus error, the VCI_RSP FSM sets the r_rsp_ins_error
    // flip-flop. It is reset by the ICACHE FSM.
    ///////////////////////////////////////////////////////////////////////

    switch(r_icache_fsm) {

    case ICACHE_IDLE:
    {
        if ( m_ireq.valid ) 
        {
            data_t  icache_ins;
            bool    icache_cachable = m_cacheability_table[m_ireq.addr];
            m_cpt_icache_dir_read += m_icache_ways;
            m_cpt_icache_data_read += m_icache_ways;
            if ( icache_cachable ) 
            {
                if ( r_icache.read(m_ireq.addr, &icache_ins) ) 	// hit
                {
                    r_icache_fsm	= ICACHE_IDLE;
                    m_irsp.valid        = true;
                    m_irsp.instruction  = icache_ins;
                } 
                else 						// miss
                {
                    m_cpt_ins_miss++;
                    m_cost_ins_miss_frz++;
                    r_icache_addr_save 	= m_ireq.addr;
                    r_icache_fsm 	    = ICACHE_MISS_SELECT;
                    r_icache_miss_req 	= true;
                    r_rsp_ins_ok 	    = false;
                }
            }
            else			// uncachable instruction
            {	
                m_cpt_ins_unc++;
                m_cost_ins_miss_frz++;
                r_icache_addr_save 	= m_ireq.addr;
                r_icache_fsm 		= ICACHE_UNC_WAIT;
                r_icache_unc_req 	= true;
                r_rsp_ins_ok 		= false;
            }
        }
        break;
    }
    case ICACHE_MISS_SELECT:
    {
        m_cost_ins_miss_frz++;

        addr_t  victim; // unused
        size_t  way;
        size_t  set;
        r_icache.victim_select( r_icache_addr_save.read(),
                                &victim,
                                &way,
                                &set );

        r_icache_way_save = way;
        r_icache_set_save = set;
        r_icache_fsm      = ICACHE_MISS_WAIT;
        break;
    }
    case ICACHE_MISS_WAIT:
    {
        m_cost_ins_miss_frz++;
        if ( r_rsp_ins_ok ) 
        {
            if ( r_rsp_ins_error ) 	r_icache_fsm = ICACHE_ERROR;
            else 			        r_icache_fsm = ICACHE_MISS_UPDT;
        }
        break;
    }
    case ICACHE_MISS_UPDT:
    {
        m_cost_ins_miss_frz++;
        m_cpt_icache_dir_write++;
        m_cpt_icache_data_write++;
        m_cost_ins_miss_frz++;

        r_icache.update( r_icache_addr_save.read(), 
                         r_icache_way_save.read(),
                         r_icache_set_save.read(),
                         r_icache_miss_buf );

        r_icache_fsm = ICACHE_IDLE;
        break;
    }
    case ICACHE_UNC_WAIT:
    {
        m_cost_ins_miss_frz++;
        if ( r_rsp_ins_ok ) 
        {
            if ( r_rsp_ins_error )  r_icache_fsm = ICACHE_ERROR;  
            else  			        r_icache_fsm = ICACHE_UNC_GO; 
        }
        break;
    }
    case ICACHE_UNC_GO:
    {
        if( m_ireq.addr == r_icache_addr_save )
        {
            m_irsp.valid		= true;
            m_irsp.instruction 	= r_icache_miss_buf[0];
        }
        r_icache_fsm	= ICACHE_IDLE;
        break;
    }
    case ICACHE_ERROR:
    {
        r_icache_fsm = ICACHE_IDLE;
        r_rsp_ins_error       = false;
        m_irsp.valid          = true;
        m_irsp.error          = true;
        break;
    }
    } // end switch r_icache_fsm

    ///////////////////////////////////////////////////////////////////////////////////
    // The DCACHE FSM controls the following ressources:
    // - r_dcache_fsm
    // - r_dcache (data cache access)
    // - r_dcache_addr_save
    // - r_dcache_wdata_save
    // - r_dcache_rdata_save
    // - r_dcache_type_save
    // - r_dcache_be_save
    // - r_dcache_way_save
    // - r_dcache_set_save
    // - r_dcache_word_save
    // - r_dcache_miss_req set
    // - r_dcache_unc_req set
    // - r_rsp_data_ok reset
    // - r_rsp_data_error reset
    // - r_wbuf write()
    // - m_dreq & m_drsp structures for communication with the processor
    //
    // - In order to support write burst, the processor requests are taken 
    //   into account in the WRITE_REQ state as well as in the IDLE state.
    // - In IDLE state, the request is satisfied if it is a cachable read hit, 
    //   an XTN request, or a cachable write. 
    // - In WRITE_REQ state, the request is satisfied if it is a cachable read hit,
    //   an XTN request, or a write when the write buffer is not full.
    // - Both the uncachable read and the uncachable write requests block the processor
    //   until the corresponding VCI transaction is completed.
    //
    // In case of processor request, there is six conditions to exit the IDLE state:
    //   - CACHED READ MISS => to the MISS_SELECT state, then to the MISS_WAIT state,
    //     then to the MISS_UPDT state, and finally to the IDLE state.
    //   - UNCACHED READ or WRITE => to the UNC_WAIT state,
    //     then to the UNC_GO state, and finally to the IDLE state.
    //   - XTN_INVAL => to the INVAL state for one cycle, then to IDLE state.
    //   - XTN_SYNC  => to the SYNC state until write buffer empty, then to IDLE state.
    //   - WRITE MISS => directly to the WRITE_REQ state to access the write buffer.
    //   - WRITE HIT => to the WRITE_UPDT state, then to the WRITE_REQ state.
    //
    // All LL or SC requests are handled as uncachable.
    //
    // Error handling :  Read Bus Errors are synchronous events, but
    // Write Bus Errors are asynchronous events (processor is not frozen).
    // - If a Read Bus Error is detected, the VCI_RSP FSM sets the
    //   r_rsp_data_error flip-flop, and the synchronous error is signaled
    //   by the DCACHE FSM.
    // - If a Write Bus Error is detected, the VCI_RSP FSM  signals
    //   the asynchronous error using the setWriteBerr() method.
    ////////////////////////////////////////////////////////////////////////

    switch ( r_dcache_fsm ) {

    case DCACHE_WRITE_REQ:
    {
        if( !r_wbuf.write(r_dcache_addr_save.read(), 
                          r_dcache_be_save.read(), 
                          r_dcache_wdata_save.read(),
                          true) )  
        {
            //  stay in DCACHE_WRITEREQ state if the request is not accepted 
            m_cost_write_frz++;
            break;     
        }
        // If the write request is accepted, 
        // the next state and the processor request parameters are computed
        // as in the DCACHE_IDLE state  below ...
    }
    case DCACHE_IDLE:
    {
        if ( m_dreq.valid ) 
        {
            bool        dcache_hit;
            data_t      dcache_rdata;
            size_t      dcache_way;
            size_t      dcache_set;
            size_t      dcache_word;
            bool        dcache_cachable;

            // dcache_cachable, dcache_hit & dcache_rdata evaluation
            m_cpt_dcache_data_read += m_dcache_ways;
            m_cpt_dcache_dir_read += m_dcache_ways;

            dcache_cachable	= m_cacheability_table[m_dreq.addr];

            dcache_hit 		= r_dcache.read( m_dreq.addr, 
                                             &dcache_rdata,
                                             &dcache_way,
                                             &dcache_set,
                                             &dcache_word );

            // Save data request and cache response
            r_dcache_addr_save      = m_dreq.addr;
            r_dcache_type_save      = m_dreq.type;
            r_dcache_wdata_save     = m_dreq.wdata;
            r_dcache_be_save        = m_dreq.be;
            r_dcache_rdata_save     = dcache_rdata;
            r_dcache_way_save       = dcache_way;
            r_dcache_set_save       = dcache_set;
            r_dcache_word_save      = dcache_word;

            // reset r_rsp_data_ok
            r_rsp_data_ok = false;

            // next FSM state, request to CMD_FSM, and processor response 
            if(m_dreq.type  == iss_t::DATA_READ)
            {
                if(!dcache_cachable) 			// uncachable read 
                {
                    m_cpt_data_unc++;
                    m_cost_unc_frz++;

                    r_dcache_unc_req    = true;
                    r_dcache_fsm 		= DCACHE_UNC_WAIT;
	            }
                else
                {
                    m_cpt_read++;

                    if(dcache_hit)				// cachable read hit
                    {
                        m_drsp.valid 	= true;
                        m_drsp.rdata	= dcache_rdata;
                        r_dcache_fsm	= DCACHE_IDLE;
                    }
                    else 						// cachable read miss
                    {
                        m_cpt_data_miss++;
                        m_cost_data_miss_frz++;

                        r_dcache_miss_req  	= true;
                        r_dcache_fsm 		= DCACHE_MISS_SELECT;
                    }
                }
            }
            else if(m_dreq.type == iss_t::DATA_WRITE)   // write
            {
                m_cpt_write++;
           
                if( dcache_cachable && dcache_hit ) 	// cachable && hit
                {
                    m_cpt_write_cached++;
                    r_dcache_fsm 		= DCACHE_WRITE_UPDT;
                }
                else                                    // not cached
                {
                    r_dcache_fsm 		= DCACHE_WRITE_REQ;
                }
                m_drsp.rdata 		= 0;
                m_drsp.valid 		= true;
            }
            else if(m_dreq.type == iss_t::DATA_LL) 		// linked read
            //  LL  requests are handled as uncachable	
            {
                m_cpt_ll++;
                m_cost_unc_frz++;

                r_dcache_unc_req   		= true;
                r_dcache_fsm 			= DCACHE_UNC_WAIT;
            }
            else if(m_dreq.type == iss_t::DATA_SC)		// conditional write
            //  SC requests are handled as uncachable	
            {
                m_cpt_sc++;
                m_cost_unc_frz++;
                r_dcache_unc_req   		= true;
                r_dcache_fsm 			= DCACHE_UNC_WAIT;
            }
            else if((m_dreq.type == iss_t::XTN_WRITE) 
                 || (m_dreq.type == iss_t::XTN_READ))  // XTN access
            // only INVAL & SYNC requests are supported
            {
                m_drsp.valid = true;
                m_drsp.rdata = 0;
                if ( m_dreq.addr/4 == iss_t::XTN_DCACHE_INVAL ) 
                {
                    r_dcache_fsm = DCACHE_INVAL;
                }
                else if ( m_dreq.addr/4 == iss_t::XTN_SYNC ) 	
                {
                    r_dcache_fsm = DCACHE_SYNC;
                }
                else
                {
                    std::cout << "error in VCI_XCACHE_WRAPPER " << name() << std::endl;
                    std::cout << "unsupported  external access type = " << m_dreq.addr/4 << std::endl;
                    std::cout << "only XTN_DCACHE_INVAL & XTN_SYNC are supported" << std::endl;
                    exit(0);
                }
            }
        }
        else // no m_dreq.valid
        {
            r_dcache_fsm = DCACHE_IDLE;
            m_drsp.valid = true;
            m_drsp.rdata = 0;
        }
        break;
    }
    case DCACHE_WRITE_UPDT:
    {
        m_cpt_dcache_data_write++;

        r_dcache.write( r_dcache_way_save.read(),
                        r_dcache_set_save.read(),
                        r_dcache_word_save.read(),
                        r_dcache_wdata_save.read(),
                        r_dcache_be_save.read() );
        r_dcache_fsm = DCACHE_WRITE_REQ;
        break;
    }
    case DCACHE_MISS_SELECT:
    {
        m_cost_data_miss_frz++;

        addr_t  victim; // unused
        size_t  way;
        size_t  set;
        r_dcache.victim_select( r_dcache_addr_save.read(),
                                &victim,
                                &way,
                                &set );

        r_dcache_way_save = way;
        r_dcache_set_save = set;
        r_dcache_fsm      = DCACHE_MISS_WAIT;
        break;
    }
    case DCACHE_MISS_WAIT:
    {
        m_cost_data_miss_frz++;
        if ( r_rsp_data_ok ) 
        {
            if ( r_rsp_data_error ) r_dcache_fsm = DCACHE_ERROR;
            else                    r_dcache_fsm = DCACHE_MISS_UPDT;
        }
        break;
    }
    case DCACHE_MISS_UPDT:
    {
        m_cost_data_miss_frz++;
        m_cpt_dcache_data_write++;
        m_cpt_dcache_dir_write++;

        r_dcache.update( r_dcache_addr_save.read(), 
                         r_dcache_way_save.read(),
                         r_dcache_set_save.read(),
                         r_dcache_miss_buf );

        r_dcache_fsm = DCACHE_IDLE;
        break;
    }

    case DCACHE_UNC_WAIT:
    {
        m_cost_unc_frz++;
        if ( r_rsp_data_ok ) 
        {
            if ( r_rsp_data_error )   	r_dcache_fsm = DCACHE_ERROR;
            else			r_dcache_fsm = DCACHE_UNC_GO; 
        }
        break;
    }
    case DCACHE_UNC_GO:
    {
        r_dcache_fsm = DCACHE_IDLE;
        m_drsp.valid = true;
        m_drsp.rdata = r_dcache_miss_buf[0];
        break;
    }
    case DCACHE_ERROR:
    {
        r_dcache_fsm = DCACHE_IDLE;
        r_rsp_data_error = false;
        m_drsp.error = true;
        m_drsp.valid = true;
        break;
    }
    case DCACHE_INVAL:  
    {
        size_t  word;   // unused
        size_t  way;
        size_t  set;
        bool hit = r_dcache.hit( r_dcache_wdata_save.read(),
                                 &way,
                                 &set,
                                 &word );
        if ( hit )
        {
            r_dcache_way_save = way;
            r_dcache_set_save = set;
            r_dcache_fsm      = DCACHE_INVAL_GO;
        }
        else
        {
            r_dcache_fsm = DCACHE_IDLE;
            m_drsp.valid = true;
        }
        break;
    }
    case DCACHE_INVAL_GO:
    {
        addr_t nline;
        r_dcache.inval( r_dcache_way_save.read(),
                        r_dcache_set_save.read(),
                        &nline );

        m_drsp.valid = true;
        r_dcache_fsm = DCACHE_IDLE;
        break;
    }
    case DCACHE_SYNC:
    {
        if ( r_wbuf.empty() ) 
        {
            r_dcache_fsm = DCACHE_IDLE;
            m_drsp.valid = true;
        }
        break;
    }
    } // end DCACHE_FSM switch

    ////////// write buffer state update ////////////////////////////////////////////
	r_wbuf.update();

    /////////// execute one iss cycle /////////////////////////////////
    {
        uint32_t it = 0;
        for (size_t i=0; i<(size_t)iss_t::n_irq; i++)
            if(p_irq[i].read())
                it |= (1<<i);

        m_iss.executeNCycles(1, m_irsp, m_drsp, it);
    }

    if ( m_ireq.valid && m_irsp.valid && 
         (!m_dreq.valid || m_drsp.valid) && (m_ireq.addr != m_cpt_pc_previous) )
    { 
        m_cpt_exec_ins++;
        m_cpt_pc_previous = m_ireq.addr;
    }

    /////////////////////////////////////////////////////////////////////////////////
    // The CMD FSM controls the following ressources:
    // - r_cmd_fsm
    // - r_cmd_min
    // - r_cmd_max
    // - wbuf.wok()
    // - wbuf.sent()
    //
    // This FSM handles requests from both the DCACHE FSM & the ICACHE FSM.
    // There is 5 request types, with the following priorities :
    // 1 - Data Read Miss         : r_dcache_miss_req & wbuf.miss
    // 2 - Instruction Miss       : r_icache_miss_req & wbuf.miss
    // 3 - Data Write             : r_wbuf.rok()
    // 3 - Data Uncachable        : r_dcache_unc_req
    // 5 - Instruction Uncachable : r_icache_unc_req
    //
    // VCI formats:
    // According to the VCI advanced specification, all read command packets
    // (uncachable read or cachable miss) are one word packets.
    // For write burst packets, all words must be in the same write buffer line,
    // and addresses must be contiguous (the BE field is 0 in case of "holes").
    // The PLEN VCI field is always documented.
    // As simultaneous VCI transactions are supported, the TRDID field is used:
    // - Write transactions : TRDID = wbuf_index + (1<<(trdid_size-1))
    // - Read transactions  : TRDID = 2*cachable + instruction
    ///////////////////////////////////////////////////////////////////////////////////

    switch (r_cmd_fsm) 
    {
    case CMD_IDLE:
    {
        size_t min;
        size_t max;
        if ( (r_dcache_miss_req) && (r_wbuf.miss(r_dcache_addr_save)) )	
        {
            r_cmd_fsm = CMD_DATA_MISS;
            r_dcache_miss_req = false;
            m_cpt_dmiss_transaction++;
        } 
        else if ( (r_icache_miss_req) && (r_wbuf.miss(r_icache_addr_save)) )	
        {
            r_cmd_fsm = CMD_INS_MISS;
            r_icache_miss_req = false;
            m_cpt_imiss_transaction++;
        } 
        else if ( r_wbuf.rok(&min, &max) ) 
        {
            r_cmd_fsm   = CMD_DATA_WRITE;
            r_cmd_min   = min;
            r_cmd_max   = max;
            r_cmd_cpt   = min;
            m_cpt_write_transaction++;
            m_length_write_transaction += (max-min+1);
        }
        else if ( r_dcache_unc_req ) 
        {
            r_cmd_fsm = CMD_DATA_UNC;
            r_dcache_unc_req = false;
            m_cpt_data_unc_transaction++;
        }
        else if ( r_icache_unc_req ) 
        {
            r_cmd_fsm = CMD_INS_UNC;
            r_icache_unc_req = false;
            m_cpt_ins_unc_transaction++;
        } 
        break;
    }
    case CMD_DATA_WRITE:
    {
        if ( p_vci.cmdack.read() ) 
        {
            r_cmd_cpt = r_cmd_cpt + 1;
            if (r_cmd_cpt == r_cmd_max) 
            {
                r_cmd_fsm = CMD_IDLE ;
                r_wbuf.sent() ;
            }
        }
        break;
    }
    case CMD_DATA_MISS:
    case CMD_DATA_UNC:
    case CMD_INS_MISS:
    case CMD_INS_UNC:
    {
        if ( p_vci.cmdack.read() )  r_cmd_fsm = CMD_IDLE;
        break;
    } 
    } // end  switch r_cmd_fsm

    //////////////////////////////////////////////////////////////////////////
    // The RSP FSM controls the following ressources:
    // - r_rsp_fsm:
    // - r_icache_miss_buf[m_icache_words]
    // - r_dcache_miss_buf[m_dcache_words]
    // - r_rsp_data_ok set
    // - r_rsp_ins_ok set
    // - r_rsp_data_error set
    // - r_rsp_ins_error set
    // - r_rsp_cpt
    //
    // VCI formats:
    // This component accepts single word or multi-word response packets for
    // write response packets.
    // As simultaneous VCI transactions are supported, the TRDID field is used:
    // - Write transactions : TRDID = wbuf_index + (1<<(trdid_size-1))
    // - Read transactions  : TRDID = 2*cachable + instruction
    //
    // Error handling:
    // - In case of Write error, the error is directly signaled by the RSP FSM.
    // - In case of Read Data Error, the VCI_RSP FSM sets the r_rsp_data_error 
    //   flip_flop and the error is signaled by the DCACHE FSM.  
    // - In case of Instruction Error, the VCI_RSP FSM sets the r_rsp_ins_error 
    //   flip_flop and the error is signaled by the ICACHE FSM.  
    //////////////////////////////////////////////////////////////////////////

    switch (r_rsp_fsm) 
    {
    case RSP_IDLE:
    {
        if( p_vci.rspval.read() ) 
        {
            r_rsp_cpt = 0;
            if ( (p_vci.rtrdid.read()>>(vci_param::T-1)) != 0 ) r_rsp_fsm = RSP_DATA_WRITE;
            else if ( p_vci.rtrdid.read() == TYPE_DATA_MISS ) 	r_rsp_fsm = RSP_DATA_MISS;
            else if ( p_vci.rtrdid.read() == TYPE_DATA_UNC ) 	r_rsp_fsm = RSP_DATA_UNC;
            else if ( p_vci.rtrdid.read() == TYPE_INS_MISS ) 	r_rsp_fsm = RSP_INS_MISS;
            else if ( p_vci.rtrdid.read() == TYPE_INS_UNC ) 	r_rsp_fsm = RSP_INS_UNC;
        } 
        break;
    }
    case RSP_DATA_WRITE:
    {
        if ( p_vci.rspval.read() )
        {
            assert( p_vci.reop.read() && 
               "A VCI response packet must contain one flit for a write transaction" ); 
            r_rsp_fsm = RSP_IDLE;
            r_wbuf.completed( p_vci.rtrdid.read() - (1<<(vci_param::T-1)) );
            if ( (p_vci.rerror.read() & 0x1) == 0x1 )  m_iss.setWriteBerr();
        }
        break;
    }
    case RSP_INS_MISS:
    {
        if ( p_vci.rspval.read() )
        {
            assert( (r_rsp_cpt < m_icache_words) &&
               "The VCI response packet for instruction miss is too long" );
            r_rsp_cpt = r_rsp_cpt + 1;
            r_icache_miss_buf[r_rsp_cpt] = (data_t)p_vci.rdata.read();
            if ( p_vci.reop.read() ) 
            {
                if ( (p_vci.rerror.read() & 0x1) == 0x0 ) // no error
                    assert( (r_rsp_cpt == m_icache_words - 1) &&
                    "The VCI response packet for instruction miss is too short");
                r_rsp_ins_ok = true;
                r_rsp_fsm = RSP_IDLE;
            }
            if ( (p_vci.rerror.read() & 0x1) == 0x1 ) r_rsp_ins_error = true;
        }
        break;
    }
    case RSP_INS_UNC:
    {
        if ( p_vci.rspval.read() )
        {
            assert(p_vci.reop.read() &&
               "illegal VCI response packet for uncachable instruction");
            r_icache_miss_buf[0] = (data_t)p_vci.rdata.read();
            r_rsp_ins_ok = true;
            r_rsp_fsm = RSP_IDLE;
            if ( (p_vci.rerror.read() & 0x1) == 0x1 ) r_rsp_ins_error = true;
        }
        break;
    }
    case RSP_DATA_MISS:
    {
        if ( p_vci.rspval.read() )
        {
            assert( (r_rsp_cpt < m_dcache_words) &&
                    "The VCI response packet for data miss is too long" );
            r_rsp_cpt = r_rsp_cpt + 1;
            r_dcache_miss_buf[r_rsp_cpt] = (data_t)p_vci.rdata.read();
            if ( p_vci.reop.read() )
            {
                if ( (p_vci.rerror.read() & 0x1) == 0x0 ) // no error
                    assert(r_rsp_cpt == m_dcache_words - 1 &&
                    "The VCI response packet for data miss is too short" );
                r_rsp_data_ok = true;
                r_rsp_fsm = RSP_IDLE;
            }
            if ( (p_vci.rerror.read() & 0x1) == 0x1 ) r_rsp_data_error = true;
        }
        break;
    }
    case RSP_DATA_UNC:
    {
        if ( p_vci.rspval.read() )
        {
            assert(p_vci.reop.read() &&
               "illegal VCI response packet for uncachable read data");
            r_dcache_miss_buf[0] = (data_t)p_vci.rdata.read();
            r_rsp_data_ok = true;
            r_rsp_fsm = RSP_IDLE;
            if ( (p_vci.rerror.read() & 0x1) == 0x1 ) r_rsp_data_error = true;
        }
        break;
    }
    } // end switch r_rsp_fsm

} // end transition()

//////////////////////
tmpl(void)::genMoore()
{
    // VCI initiator response

    p_vci.rspack = ( r_rsp_fsm != RSP_IDLE );

    // VCI initiator command

    switch (r_cmd_fsm.read() ) 
    {
    case CMD_IDLE:
        p_vci.cmdval  = false;
        p_vci.address = 0;
        p_vci.wdata   = 0;
        p_vci.be      = 0;
        p_vci.plen    = 0;
        p_vci.cmd     = vci_param::CMD_WRITE;
        p_vci.trdid   = 0;
        p_vci.pktid   = 0;
        p_vci.srcid   = 0;
        p_vci.cons    = false;
        p_vci.wrap    = false;
        p_vci.contig  = false;
        p_vci.clen    = 0;
        p_vci.cfixed  = false;
        p_vci.eop     = false;
        break;

    case CMD_DATA_WRITE:
        p_vci.cmdval  = true;
        p_vci.address = r_wbuf.getAddress(r_cmd_cpt);
        p_vci.wdata   = r_wbuf.getData(r_cmd_cpt);
        p_vci.be      = r_wbuf.getBe(r_cmd_cpt);
        p_vci.plen    = (r_cmd_max - r_cmd_min + 1)<<2;
        p_vci.cmd     = vci_param::CMD_WRITE;
        p_vci.pktid   = 0;
        p_vci.trdid   = r_wbuf.getIndex() + (1<<(vci_param::T-1));
        p_vci.srcid   = m_srcid;
        p_vci.cons    = false;
        p_vci.wrap    = false;
        p_vci.contig  = true;
        p_vci.clen    = 0;
        p_vci.cfixed  = false;
        p_vci.eop     = (r_cmd_cpt == r_cmd_max);
        break;

    case CMD_DATA_MISS:
        p_vci.cmdval = true;
        p_vci.address = r_dcache_addr_save & m_dcache_yzmask;
        p_vci.be     = 0xF;
        p_vci.plen   = m_dcache_words << 2;
        p_vci.cmd    = vci_param::CMD_READ;
        p_vci.pktid  = 0;
        p_vci.trdid  = TYPE_DATA_MISS;
        p_vci.srcid  = m_srcid;
        p_vci.cons   = false;
        p_vci.wrap   = false;
        p_vci.contig = true;
        p_vci.clen   = 0;
        p_vci.cfixed = false;
        p_vci.eop    = true;
        break;

    case CMD_DATA_UNC:
        p_vci.cmdval = true;
        p_vci.address = r_dcache_addr_save & ~0x3;
        switch( r_dcache_type_save.read() ) 
        {
        case iss_t::DATA_READ:
            p_vci.wdata = 0;
            p_vci.be  = r_dcache_be_save.read();
            p_vci.cmd = vci_param::CMD_READ;
            break;
        case iss_t::DATA_LL:
            p_vci.wdata = 0;
            p_vci.be  = 0xF;
            p_vci.cmd = vci_param::CMD_LOCKED_READ;
            break;
        case iss_t::DATA_SC:
            p_vci.wdata = r_dcache_wdata_save.read();
            p_vci.be  = 0xF;
            p_vci.cmd = vci_param::CMD_STORE_COND;
            break;
        default:
            assert("The DCACHE_FSM should not enter the CMD_DATA_UNC in case of write");
        }
        p_vci.plen   = 4;
        p_vci.pktid  = 0;
        p_vci.trdid  = TYPE_DATA_UNC;
        p_vci.srcid  = m_srcid;
        p_vci.cons   = false;
        p_vci.wrap   = false;
        p_vci.contig = true;
        p_vci.clen   = 0;
        p_vci.cfixed = false;
        p_vci.eop    = true;
        break;

    case CMD_INS_MISS:
        p_vci.cmdval = true;
        p_vci.address = r_icache_addr_save & m_icache_yzmask;
        p_vci.be     = 0xF;
        p_vci.plen   = m_icache_words << 2;
        p_vci.cmd    = vci_param::CMD_READ;
        p_vci.pktid  = 0;
        p_vci.trdid  = TYPE_INS_MISS;
        p_vci.srcid  = m_srcid;
        p_vci.cons   = false;
        p_vci.wrap   = false;
        p_vci.contig = true;
        p_vci.clen   = 0;
        p_vci.cfixed = false;
        p_vci.eop = true;
        break;

    case CMD_INS_UNC:
        p_vci.cmdval = true;
        p_vci.address = r_icache_addr_save & ~0x3;
        p_vci.be     = 0xF;
        p_vci.plen   = 4;
        p_vci.cmd    = vci_param::CMD_READ;
        p_vci.pktid  = 0;
        p_vci.trdid  = TYPE_INS_UNC;
        p_vci.srcid  = m_srcid;
        p_vci.cons   = false;
        p_vci.wrap   = false;
        p_vci.contig = true;
        p_vci.clen   = 0;
        p_vci.cfixed = false;
        p_vci.eop = true;
        break;
    } // end switch r_cmd_fsm

} // end genMoore()

}} // end namespace

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4




