/*
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
 * Copyright (c) UPMC, Lip6, Asim
 *         Alain Greiner <alain.greiner@lip6.fr>, 2008
 *
 * Maintainers: alain
 */

#include <iostream>
#include <cstring>
#include "arithmetics.h"
#include "vci_simple_rom.h"

namespace soclib {
namespace caba {

using namespace soclib;

#define tmpl(x) template<typename vci_param> x VciSimpleRom<vci_param>

//////////////////////////
tmpl(/**/)::VciSimpleRom(
    sc_module_name name,
    const soclib::common::IntTab index,
    const soclib::common::MappingTable &mt,
    const soclib::common::Loader &loader)
	: caba::BaseModule(name),
      m_loader(loader),
      m_seglist(mt.getSegmentList(index)),

      r_fsm_state("r_fsm_state"),
      r_flit_count("r_flit_count"),
      r_nb_words("r_nb_words"),
      r_seg_index("r_seg_index"),
      r_rom_index("r_rom_index"),
      r_srcid("r_srcid"),
      r_trdid("r_trdid"),
      r_pktid("r_pktid"),

      p_resetn("p_resetn"),
      p_clk("p_clk"),
      p_vci("p_vci")
{
    std::cout << "  - Building SimpleRom " << name << std::endl;

    size_t nsegs = 0;

    assert( (m_seglist.empty() == false) and
    "VCI_SIMPLE_ROM error : no segment allocated");

    std::list<soclib::common::Segment>::iterator seg;
    for ( seg = m_seglist.begin() ; seg != m_seglist.end() ; seg++ )
    {
        std::cout << "    => segment " << seg->name()
                  << " / base = " << std::hex << seg->baseAddress()
                  << " / size = " << seg->size() << std::endl; 
        nsegs++;
    }

    m_nbseg = nsegs;

    assert( ((vci_param::B == 4) or (vci_param::B == 8)) and
    "VCI_SIMPLE_ROM error : The VCI DATA field must be 32 or 64 bits");

    // actual memory allocation
    m_rom = new uint32_t*[m_nbseg];
    m_seg = new soclib::common::Segment*[m_nbseg];

    size_t i = 0;
    for ( seg = m_seglist.begin() ; seg != m_seglist.end() ; seg++ ) 
    { 
        m_rom[i] = new uint32_t[ (seg->size()+3)/4 ];
        m_seg[i] = &(*seg);
        i++;
    }

    SC_METHOD(transition);
    dont_initialize();
    sensitive << p_clk.pos();

    SC_METHOD(genMoore);
    dont_initialize();
    sensitive << p_clk.neg();
}

///////////////////////////
tmpl(/**/)::~VciSimpleRom()
{
    for (size_t i=0 ; i<m_nbseg ; ++i) delete [] m_rom[i];
    delete [] m_rom;
    delete [] m_seg;
}

/////////////////////
tmpl(void)::reload()
{
    for ( size_t i=0 ; i<m_nbseg ; ++i ) 
    {
        m_loader.load(&m_rom[i][0], m_seg[i]->baseAddress(), m_seg[i]->size());
        for ( size_t addr = 0 ; addr < m_seg[i]->size()/vci_param::B ; ++addr )
            m_rom[i][addr] = le_to_machine(m_rom[i][addr]);
    }
}

////////////////////
tmpl(void)::reset()
{
    for ( size_t i=0 ; i<m_nbseg ; ++i ) std::memset(&m_rom[i][0], 0, m_seg[i]->size()); 
 	r_fsm_state = FSM_IDLE;
}

//////////////////////////
tmpl(void)::print_trace()
{
    const char* state_str[] = { "IDLE", "READ", "ERROR" };
    std::cout << "SIMPLE_ROM " << name() 
              << " : state = " << state_str[r_fsm_state] 
              << " / flit_count = " << std::dec << r_flit_count << std::endl;
}

/////////////////////////
tmpl(void)::transition()
{
    if (!p_resetn) 
    {
        reset();
        reload();
        return;
    }

    switch ( r_fsm_state ) 
    {
        //////////////
        case FSM_IDLE: 	// waiting a VCI command 
        {
            if ( p_vci.cmdval.read() ) 
            {
                bool error = true;

                assert( ((p_vci.address.read() & 0x3) == 0) and 
                "VCI_SIMPLE_ROM ERROR : The VCI ADDRESS must be multiple of 4");

                assert( ((p_vci.plen.read() & 0x3) == 0) and 
                "VCI_SIMPLE_ROM ERROR : The VCI PLEN must be multiple of 4");

                assert( (p_vci.plen.read() != 0) and
                "VCI_SIMPLE_ROM ERROR : The VCI PLEN should be != 0");

                assert( (p_vci.cmd.read() == vci_param::CMD_READ) and
                "VCI_SIMPLE_ROM ERROR : The VCI command must be a READ");
            
                assert( p_vci.eop.read() and
                "VCI_SIMPLE_ROM ERROR : The VCI command packet must be 1 flit");

                assert( (((vci_param::B == 4) and (p_vci.be.read() == 0xF)) or 
                         ((vci_param::B == 8) and (p_vci.be.read() == 0xFF)) or
                         ((vci_param::B == 8) and (p_vci.be.read() == 0x0F))) and
                "VCI_SIMPLE_ROM ERROR : The VCI BE field must be 0xF or 0xFF");
            
                for ( size_t index = 0 ; index<m_nbseg  && error ; ++index) 
                {
                    if ( (m_seg[index]->contains(p_vci.address.read())) and
                         (m_seg[index]->contains(p_vci.address.read()+p_vci.plen.read()-1)) ) 
                    {
                        error = false;
                        r_seg_index  = index;
                    }
                } 

                if ( error )   
                {
                    r_fsm_state = FSM_RSP_ERROR;
                }
                else
                {
                    r_fsm_state = FSM_RSP_READ;
                    r_srcid     = p_vci.srcid.read();
                    r_trdid     = p_vci.trdid.read();
                    r_pktid     = p_vci.pktid.read();
                    r_rom_index = (size_t)((p_vci.address.read() -
                                            m_seg[r_seg_index.read()]->baseAddress())>>2);

                    if ( (vci_param::B == 8) and (p_vci.be.read() == 0xFF) )
                    {
                        r_flit_count = p_vci.plen.read()>>3;
                        r_nb_words   = 2;
                    }
                    else
                    {
                        r_flit_count = p_vci.plen.read()>>2;
                        r_nb_words   = 1;
                    }
                }
            }
            break;
        }
        //////////////////
        case FSM_RSP_READ:  // send one response flit 
        {
            if ( p_vci.rspack.read() )
            {
                r_flit_count = r_flit_count - 1;
                r_rom_index  = r_rom_index.read() + r_nb_words.read();
                if ( r_flit_count.read() == 1) 	 r_fsm_state = FSM_IDLE;
            }
            break;
        }
        ///////////////////
        case FSM_RSP_ERROR: // waits lat flit of a VCI CMD erroneous packet 
        {
            if ( p_vci.rspack.read() && p_vci.eop.read() )
            {
                r_fsm_state = FSM_IDLE;
            }
            break;
        }
    } // end switch fsm_state

} // end transition()

///////////////////////
tmpl(void)::genMoore()
{
    switch ( r_fsm_state.read() ) 
    {
        case FSM_IDLE:
        {
            p_vci.cmdack  = true;
            p_vci.rspval  = false;
            p_vci.rdata   = 0;
            p_vci.rsrcid  = 0;
            p_vci.rtrdid  = 0;
            p_vci.rpktid  = 0;
            p_vci.rerror  = 0;
            p_vci.reop    = false;
            break;
        }
        case FSM_RSP_READ:
        {
            vci_data_t rdata;
            size_t     seg_index = r_seg_index.read();
            size_t     rom_index = r_rom_index.read();

            if ( r_nb_words.read() == 1 )
            {
                rdata = (uint32_t)m_rom[seg_index][rom_index];
            }
            else  // r_nb_words == 2
            {
                rdata = (uint64_t)m_rom[seg_index][rom_index] | 
                        (((uint64_t)m_rom[seg_index][rom_index+1]) << 32);
            }
            
            p_vci.cmdack  = false;
            p_vci.rspval  = true;
            p_vci.rdata   = rdata;
            p_vci.rsrcid  = r_srcid.read();
            p_vci.rtrdid  = r_trdid.read();
            p_vci.rpktid  = r_pktid.read();
            p_vci.rerror  = vci_param::ERR_NORMAL;
            p_vci.reop   = (r_flit_count.read() == 1);
            break;
        }
        case FSM_RSP_ERROR:
        {
            p_vci.cmdack  = false;
            p_vci.rspval  = true;
            p_vci.rdata   = 0;
            p_vci.rsrcid  = r_srcid.read();
            p_vci.rtrdid  = r_trdid.read();
            p_vci.rpktid  = r_pktid.read();
            p_vci.rerror  = vci_param::ERR_GENERAL_DATA_ERROR;
            p_vci.reop    = true;
            break;
        }
    } // end switch fsm_state
} // end genMoore()

}} 

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

