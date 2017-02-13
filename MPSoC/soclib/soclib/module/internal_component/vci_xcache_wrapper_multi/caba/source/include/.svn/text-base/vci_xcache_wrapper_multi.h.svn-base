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
 *         Alain Greiner <alain.greiner@lip6.fr>, 2008
 *         Nicolas Pouillon <nipo@ssji.net>, 2008
 *
 * Maintainers: alain
 */

#ifndef SOCLIB_CABA_VCI_XCACHE_WRAPPER_MULTI_H
#define SOCLIB_CABA_VCI_XCACHE_WRAPPER_MULTI_H

#include <inttypes.h>
#include <systemc>
#include "caba_base_module.h"
#include "multi_write_buffer.h"
#include "generic_cache.h"
#include "vci_initiator.h"
#include "mapping_table.h"
#include "static_assert.h"

namespace soclib { namespace caba {

using namespace sc_core;

//////////////////////////////////////////////
template<typename vci_param, typename iss_t>
class VciXcacheWrapperMulti
//////////////////////////////////////////////
    : public soclib::caba::BaseModule
{
    typedef uint32_t    addr_t;
    typedef uint32_t    data_t;
    typedef uint32_t    tag_t;
    typedef uint32_t    be_t;

    enum dcache_fsm_state_e {
        DCACHE_IDLE,
        DCACHE_WRITE_UPDT,
        DCACHE_WRITE_REQ,
        DCACHE_MISS_SELECT,
        DCACHE_MISS_WAIT,
        DCACHE_MISS_UPDT,
        DCACHE_UNC_WAIT,
        DCACHE_UNC_GO,
        DCACHE_INVAL,
        DCACHE_INVAL_GO,
        DCACHE_SYNC,
        DCACHE_ERROR,
    };

    enum icache_fsm_state_e {
        ICACHE_IDLE,
        ICACHE_MISS_SELECT,
        ICACHE_MISS_WAIT,
        ICACHE_MISS_UPDT,
        ICACHE_UNC_WAIT,
        ICACHE_UNC_GO,
        ICACHE_ERROR,
    };

    enum cmd_fsm_state_e {
        CMD_IDLE,
        CMD_INS_MISS,
        CMD_INS_UNC,
        CMD_DATA_MISS,
        CMD_DATA_UNC,
        CMD_DATA_WRITE,
    };

    enum rsp_fsm_state_e {
        RSP_IDLE,
        RSP_INS_MISS,
        RSP_INS_UNC,
        RSP_DATA_MISS,
        RSP_DATA_UNC,
        RSP_DATA_WRITE,
    };

    enum transaction_type_e {
        TYPE_DATA_UNC  = 0,
        TYPE_DATA_MISS = 1,
        TYPE_INS_UNC   = 2,
        TYPE_INS_MISS  = 3,
    };

public:

    // PORTS
    sc_in<bool>                             p_clk;
    sc_in<bool>                             p_resetn;
    sc_in<bool>                             p_irq[iss_t::n_irq];
    soclib::caba::VciInitiator<vci_param>   p_vci;

private:

    // STRUCTURAL PARAMETERS
    const soclib::common::AddressDecodingTable<uint32_t, bool>  m_cacheability_table;
    const uint32_t                                              m_srcid;

    const size_t                                                m_dcache_ways;
    const size_t                                                m_dcache_words;
    const addr_t                                                m_dcache_yzmask;
    const size_t                                                m_icache_ways;
    const size_t                                                m_icache_words;
    const addr_t                                                m_icache_yzmask;

    // ISS 
    iss_t                                                       m_iss;

    // REGISTERS
    sc_signal<int>          r_dcache_fsm;
    sc_signal<addr_t>       r_dcache_addr_save;
    sc_signal<data_t>       r_dcache_wdata_save;
    sc_signal<data_t>       r_dcache_rdata_save;
    sc_signal<int>          r_dcache_type_save;
    sc_signal<be_t>         r_dcache_be_save;
    sc_signal<size_t>       r_dcache_way_save;
    sc_signal<size_t>       r_dcache_set_save;
    sc_signal<size_t>       r_dcache_word_save;
    sc_signal<bool>         r_dcache_miss_req;
    sc_signal<bool>         r_dcache_unc_req;
    sc_signal<bool> 	    r_dcache_updated;	// dcache modified at previous cycle

    sc_signal<int>          r_icache_fsm;
    sc_signal<addr_t>       r_icache_addr_save;
    sc_signal<size_t>       r_icache_way_save;
    sc_signal<size_t>       r_icache_set_save;
    sc_signal<bool>         r_icache_miss_req;
    sc_signal<bool>         r_icache_unc_req;
    sc_signal<bool> 	    r_icache_updated;	// icache modified at previous cycle

    sc_signal<int>          r_cmd_fsm;
    sc_signal<size_t>       r_cmd_min;
    sc_signal<size_t>       r_cmd_max;
    sc_signal<size_t>       r_cmd_cpt;

    sc_signal<int>          r_rsp_fsm;
    sc_signal<bool>         r_rsp_ins_error;
    sc_signal<bool>         r_rsp_data_error;
    sc_signal<size_t>       r_rsp_cpt;
    sc_signal<bool>         r_rsp_ins_ok;
    sc_signal<bool>         r_rsp_data_ok;

    data_t                  *r_icache_miss_buf;
    data_t                  *r_dcache_miss_buf;

    MultiWriteBuffer<addr_t>	r_wbuf;
    GenericCache<addr_t>    	r_icache;
    GenericCache<addr_t>    	r_dcache;

    // Processor interface
    typename iss_t::InstructionRequest   m_ireq;
    typename iss_t::InstructionResponse  m_irsp;
    typename iss_t::DataRequest          m_dreq;
    typename iss_t::DataResponse         m_drsp;

    // Activity counters 
    uint32_t m_cpt_dcache_data_read;        // DCACHE DATA READ
    uint32_t m_cpt_dcache_data_write;       // DCACHE DATA WRITE
    uint32_t m_cpt_dcache_dir_read;         // DCACHE DIR READ
    uint32_t m_cpt_dcache_dir_write;        // DCACHE DIR WRITE

    uint32_t m_cpt_icache_data_read;        // ICACHE DATA READ
    uint32_t m_cpt_icache_data_write;       // ICACHE DATA WRITE
    uint32_t m_cpt_icache_dir_read;         // ICACHE DIR READ
    uint32_t m_cpt_icache_dir_write;        // ICACHE DIR WRITE

    addr_t   m_cpt_pc_previous;             // previous valid instruction address
    uint32_t m_cpt_exec_ins;	            // number of executed instructions
    uint32_t m_cpt_total_cycles;	        // total number of cycles

    uint32_t m_cpt_read;                    // total number of read requests
    uint32_t m_cpt_write;                   // total number of write requests
    uint32_t m_cpt_write_cached;            // number of cached write
    uint32_t m_cpt_data_unc;                // number of uncachable data requests
    uint32_t m_cpt_ins_unc;                 // number of uncachable instruction requests
    uint32_t m_cpt_ll;			            // number of ll requests
    uint32_t m_cpt_sc;			            // number of sc requests
    uint32_t m_cpt_data_miss;               // number of data miss
    uint32_t m_cpt_ins_miss;                // number of instruction miss

    uint32_t m_cost_write_frz;              // frozen cycles related to write buffer
    uint32_t m_cost_data_miss_frz;          // frozen cycles related to data miss
    uint32_t m_cost_unc_frz;                // frozen cycles related to uncached data
    uint32_t m_cost_ins_miss_frz;           // frozen cycles related to ins miss

    uint32_t m_cpt_imiss_transaction;       // VCI instruction miss transactions
    uint32_t m_cpt_dmiss_transaction;       // VCI data miss transactions
    uint32_t m_cpt_data_unc_transaction;    // VCI uncachable data transactions
    uint32_t m_cpt_ins_unc_transaction;     // VCI uncachable instruction transactions
    uint32_t m_cpt_write_transaction;       // VCI write transactions

    uint32_t m_length_write_transaction;    // cumulated length VCI write transactions

protected:
    SC_HAS_PROCESS(VciXcacheWrapperMulti);

public:

    VciXcacheWrapperMulti(
        sc_module_name insname,
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
        size_t wbuf_nlines);

    ~VciXcacheWrapperMulti();

    void print_stats();
    void print_trace(size_t mode = 0);

    void file_stats(FILE* file);
    void file_trace(FILE* file);

private:

    void transition();
    void genMoore();

    soclib_static_assert((int)iss_t::SC_ATOMIC == (int)vci_param::STORE_COND_ATOMIC);
    soclib_static_assert((int)iss_t::SC_NOT_ATOMIC == (int)vci_param::STORE_COND_NOT_ATOMIC);
};

}}

#endif /* SOCLIB_CABA_VCI_XCACHE_WRAPPER_MULTI_H */

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4


