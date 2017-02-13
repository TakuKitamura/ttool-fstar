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
 * Copyright (c) UPMC, Lip6
 *         Alain Greiner <alain.greiner@lip6.fr> November 2010
 */

//////////////////////////////////////////////////////////////////////////////////
//  This component is a multi-channels DMA controller supporting chained buffers.
//  It can be used to move a stream from one set of chained buffers (src_chbuf) 
//  to another set of chained buffers (dst_chbuf), without involving software.
//
//  A "chbuf descriptor" is an array of "buffer descriptors", stored in main
//  memory. Each buffer descriptor contains two 32 bits words:
//  - STATUS[31:0] : buffer status (buffer full when STATUS non zero)
//  - PADDR[31:0]  : buffer base address
//  The buffer size must be the same for src_chbuf and dst_chbuf.
//
//  As this DMA controller uses a polling policy to access both the src_chbuf
//  and the dst chbuf, it introduces a delay between to access to a chbuf.
//  This delay is an optional constructor argument. Default value is 1000 cycles.
//  
//  This component makes the assumption that the VCI RDATA & WDATA fields
//  have 32 bits. The number of channels and the max size of a data burst are
//  constructor parameters. 
//
//  The chbuf descriptor address (CHBUF_DESC), and the number of chained 
//  buffers (CHBUF_NBUFS), as well as the elementary buffer size (BUF_SIZE)
//  are software parameters that must be  written in addressable registers 
//  when launching a transfer between two chbufs.
//
//  - The number of channels (simultaneous transfers) cannot be larger than 8.
//  - The burst length (in bytes) must be a power of 2 no larger than 128,
//    and is typically equal to the system cache line width.
//  - The elementary buffer size and all buffers base addresses must be multiple 
//    of 4 bytes. If the source and destination buffers are not aligned on a burst 
//    boundary, the DMA controler split the burst in two VCI transactions.
//
//  In order to support various protection mechanisms, each channel
//  takes 4K bytes in the address space, and the segment size is 32 K bytes. 
//  Only 8 address bits are decoded :
//  - The 5 bits ADDRESS[4:Ø] define the target register (see chbuf_dma.h)
//  - The 3 bits ADDRESS[14:12] define the selected channel.
//
//  For each channel, the relevant values for the channel status are:
//  - CHANNEL_IDLE           : 0   / channel not running
//  - CHANNEL_SRC_DESC_ERROR : 1   / bus error accessing SRC CHBUF descriptor
//  - CHANNEL_DST_DESC_ERROR : 2   / bus error accessing DST CHBUF descriptor
//  - CHANNEL_SRC_DATA_ERROR : 3   / bus error accessing SRC CHBUF data
//  - CHANNEL_DST_DATA_ERROR : 4   / bus error accessing DST CHBUF data
//  - CHANNEL_BUSY           : > 4 / channel running
// 
//  There is one private IRQ line for each channel, that is only used
//  for bus error signaling, and is activated when channel[k] enters
//  an error state. The channel can be reset by writing a nul value
//  in register CHBUF_RUN[k], focing channel[k] to IDLE state.
//
//  In order to support multiple simultaneous transactions, the channel
//  index is transmited in the VCI TRDID field.
//  As the LSB bit of the TRDID is used to indicate a non-cachable access,
//  the channel index is encoded in the next 3 bits, and the TRDID width
//  must be at least 4 bits.
//  
//////////////////////////////////////////////////////////////////////////////////
//  Implementation note:
// 
//  The tgt_fsm controls the configuration commands and responses 
//  on the VCI target ports.
//
//  The cmd_fsm controls the read and write data transfer commands 
//  on the VCI initiator port. It uses four registers : 
//  - r_cmd fsm 
//  - r_cmd_count                counter of bytes in VCI transaction (shared)
//  - r_cmd_channel              selected channel
//  - r_cmd_bytes                VCI PLEN
//
//  The rsp_fsm controls the read and write data transfer responses 
//  on the VCI initiator port. It uses four registers : 
//  - r_rsp fsm 
//  - r_rsp_count                counter of bytes in local buffer (one per channel)
//  - r_rsp_channel              selected channel
//  - r_rsp_bytes                VCI PLEN
//
//  Each channel [k] is controled by a channel FSM using the following registers: 
//
//  - r_channel_fsm[k]
//  - r_channel_run[k]	         channel active <=> transfer requested   (W)
//  - r_channel_buf_size[k]      buffer size (bytes) for SRC and DST     (R/W)
//
//  - r_channel_src_desc[k]      address of source chbuf descriptor      (R/W)
//  - r_channel_src_nbufs[k]     number of buffers in source chbuf       (R/W)
//  - r_channel_src_index[k]     current buffer index in source chbuf
//  - r_channel_src_addr[k]      current address in source buffer
//  - r_channel_src_offset[k]    number of non aligned bytes for source buffer
//  - r_channel_src_full[k]      current source buffer status
//
//  - r_channel_dst_desc[k]      address of destination chbuf descriptor (R/W)
//  - r_channel_dst_nbufs[k]     number of buffers in dest chbuf         (R/W)
//  - r_channel_dst_index[k]     current buffer index in dest chbuf
//  - r_channel_dst_addr[k]      current address in dest buffer
//  - r_channel_dst_offset[k]    number of non aligned bytes for dest buffer
//  - r_channel_dst_full[k]      current destination buffer status
//
//  - r_channel_timer[k]         cycle counter for status polling
//  - r_channel_period[k]        status polling period
//  - r_channel_todo_bytes[k]    number of words not yet transfered in a buffer 
//  - r_channel_vci_req[k]       valid request from CHANNEL FSM to CMD FSM
//  - r_channel_vci_type[k]      request type  from CHANNEL FSM to CMD FSM
//  - r_channel_vci_rsp[k]       valid response from RSP FSM to CHANNEL FSM
//  - r_channel_vci_error[k]     error signaled by RSP FSM to CHANNEL FSM
//  - r_channel_bytes_first[k]   number of bytes for first data VCI transaction
//  - r_channel_bytes_second[k]  number of bytes for second data VCI transaction
//  - r_channel_last[k]          last read/write DMA transaction
//  - r_channel_buf[k][word]	 local burst buffer 
//
///////////////////////////////////i///////////////////////////////////////////////

#include <stdint.h>
#include <cassert>

#include "alloc_elems.h"
#include "../include/vci_chbuf_dma.h"
#include "../../../include/soclib/chbuf_dma.h"

namespace soclib { namespace caba {

#define tmpl(t) template<typename vci_param> t VciChbufDma<vci_param>

/////////////////////////
tmpl(void)::transition()
{
    if (!p_resetn) 
    {
        r_tgt_fsm    = TGT_IDLE;
        r_cmd_fsm    = CMD_IDLE;
        r_rsp_fsm    = RSP_IDLE;
        r_cmd_channel  = 0;
        r_rsp_channel  = 0;
        for ( uint32_t k = 0 ; k < m_channels ; k++ )
        {
            r_channel_fsm[k] 	   = CHANNEL_IDLE;
            r_channel_period[k]    = 1000;
            r_channel_run[k]	   = false;
            r_channel_vci_req[k]   = false;   
            r_channel_vci_rsp[k]   = false;   
        }
        return;
    }

    ///////////////////////////////////////////////////////////////////////////////
    // This TGT_FSM controls the VCI TARGET port
    // It access the following registers:
    //  - r_channel_run[k]	         channel active => transfer requested    (W)
    //  - r_channel_buf_size[k]      buffer size (bytes) for both SRC & DST  (R/W)
    //  - r_channel_src_desc[k]      address of source chbuf descriptor      (R/W)
    //  - r_channel_src_nbufs[k]     number of buffers in source chbuf       (R/W)
    //  - r_channel_dst_desc[k]      address of destination chbuf descriptor (R/W)
    //  - r_channel_dst_nbufs[k]     number of buffers in dest chbuf         (R/W)
    //  - r_channel_period[k]        status polling period                   (R/W)
    ///////////////////////////////////////////////////////////////////////////////

    switch(r_tgt_fsm.read()) 
    {
        case TGT_IDLE:
        {
            if (p_vci_target.cmdval.read() )
            {
                typename vci_param::fast_addr_t	address = p_vci_target.address.read();
                typename vci_param::data_t	wdata   = (uint32_t)p_vci_target.wdata.read();
                typename vci_param::cmd_t	cmd     = p_vci_target.cmd.read();

                bool found = false;
                std::list<soclib::common::Segment>::iterator seg;
                for ( seg = m_seglist.begin() ; seg != m_seglist.end() ; seg++ ) 
                {
                    if ( seg->contains(address) ) found = true;
                }

                assert ( found  and
                "ERROR in VCI_CHBUF_DMA : VCI address is out of segment");
               
                r_tgt_srcid	= p_vci_target.srcid.read();
                r_tgt_trdid	= p_vci_target.trdid.read();
                r_tgt_pktid	= p_vci_target.pktid.read();
                
                int 	  cell = (int)((address & 0x3C) >> 2);
                uint32_t  k    = (uint32_t)((address & 0x7000) >> 12);

                assert( (k < m_channels) and 
                "VCI_CHBUF_DMA error : The channel index (ADDR[14:12] is too large");

                assert( p_vci_target.eop.read() and
                "VCI_CHBUF_DMA error : A configuration request must be one single flit");

                assert(((vci_param::B == 4) || (vci_param::B == 8 && p_vci_target.be.read() == 0x0f)) &&
                "VCI_CHBUF_DMA error : In configuration request data must be on 32 bits");
                
                //////////////////////////////////////////////////////////
	            if ( (cell == CHBUF_RUN) and (cmd == vci_param::CMD_WRITE) )
                {
                    r_channel_run[k] = (wdata != 0);
                    r_tgt_fsm              = TGT_WRITE;
                }
                /////////////////////////////////////////////////////////////////
	            else if ( (cell == CHBUF_STATUS) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = r_channel_fsm[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                ////////////////////////////////////////////////////////////////////
	            else if ( (cell == CHBUF_SRC_DESC) and (cmd == vci_param::CMD_WRITE) )
                {
                    assert( (not r_channel_run[k].read()) and
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");

                    assert( (wdata%4 == 0) and
                    "VCI_CHBUF_DMA error : SRC descriptor address not multiple of 4");

                    r_channel_src_desc[k]  = wdata;
                    r_channel_src_index[k] = 0;
                    r_tgt_fsm                    = TGT_WRITE;
                }
                ///////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_SRC_EXT) and (cmd == vci_param::CMD_WRITE) )
                {               
                    assert( (not r_channel_run[k].read()) and 
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");
                   
                    r_channel_src_desc[k] = (r_channel_src_desc[k].read() & 0XFFFFFFFF) + 
                                      ((uint64_t)wdata << 32) ;
                    r_tgt_fsm                    = TGT_WRITE;
                }
                ///////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_SRC_DESC) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = (uint32_t)r_channel_src_desc[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                ///////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_SRC_EXT) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = (uint32_t)(r_channel_src_desc[k].read()>>32);
                    r_tgt_fsm   = TGT_READ;
                }
                ////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_DST_DESC) and (cmd == vci_param::CMD_WRITE) )
                {
                    assert( (not r_channel_run[k].read()) and
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");

                    assert( (wdata%4 == 0) and
                    "VCI_CHBUF_DMA error : DST descriptor address not multiple of 4");

                    r_channel_dst_desc[k]  = wdata;
                    r_channel_dst_index[k] = 0;
                    r_tgt_fsm                    = TGT_WRITE;
                }
                ///////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_DST_EXT) and (cmd == vci_param::CMD_WRITE) )
                {               
                    assert( (not r_channel_run[k].read()) and 
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");
                   
                    r_channel_dst_desc[k] = (r_channel_dst_desc[k].read() & 0XFFFFFFFF) + 
                                      ((typename vci_param::fast_addr_t)wdata << 32);
                    r_tgt_fsm                    = TGT_WRITE;
                }
                ///////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_DST_DESC) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = r_channel_dst_desc[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                ///////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_DST_EXT) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = (uint32_t)(r_channel_dst_desc[k].read()>>32);
                    r_tgt_fsm   = TGT_READ;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_BUF_SIZE) and (cmd == vci_param::CMD_WRITE) )
                {
                    assert( not r_channel_run[k].read() and
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");

                    assert( (wdata%4 == 0) and
                    "VCI_CHBUF_DMA error : buffer size not multiple of 4");

                    r_channel_buf_size[k] = wdata;
                    r_tgt_fsm                   = TGT_WRITE;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_BUF_SIZE) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = r_channel_buf_size[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_SRC_NBUFS) and (cmd == vci_param::CMD_WRITE) )
                {
                    assert( not r_channel_run[k].read() and
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");

                    r_channel_src_nbufs[k] = wdata;
                    r_tgt_fsm                     = TGT_WRITE;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_SRC_NBUFS) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = r_channel_src_nbufs[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_DST_NBUFS) and (cmd == vci_param::CMD_WRITE) )
                {
                    assert( not r_channel_run[k].read() and
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");

                    r_channel_dst_nbufs[k] = wdata;
                    r_tgt_fsm                    = TGT_WRITE;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_DST_NBUFS) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = r_channel_dst_nbufs[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_PERIOD) and (cmd == vci_param::CMD_WRITE) )
                {
                    assert( not r_channel_run[k].read() and
                    "VCI_CHBUF_DMA error : Configuration request for an active channel");

                    r_channel_period[k] = wdata;
                    r_tgt_fsm                 = TGT_WRITE;
                }
                /////////////////////////////////////////////////////////////////////
                else if ( (cell == CHBUF_PERIOD) and (cmd == vci_param::CMD_READ) )
                {
                    r_tgt_rdata = r_channel_period[k].read();
                    r_tgt_fsm   = TGT_READ;
                }
                /////
                else
                {
                    r_tgt_fsm = TGT_ERROR;
                }
            }
            break;
        }
        case TGT_WRITE:
        case TGT_READ:
        case TGT_ERROR:
        {
            if ( p_vci_target.rspack.read() )
            {
                r_tgt_fsm = TGT_IDLE;
            }
            break;
        }
    } // end switch tgt_fsm

    /////////////////////////////////////////////////////////////////////
    // These CHANNEL_FSM define the transfer state for each channel.
    // Each channel FSM implements two nested loops:
    //
    // - In external loop, we get the SRC buffer address, and the
    //   DST buffer address from the chbufs descriptors, we transfer
    //   a full buffer (internal loop), and release SRC & DST buffers.
    // - In the internal loop, a burst of size m_burst_max_length
    //   (corresponding to the storage capacity of the local buffer)
    //   is transfered at each iteration.
    //   The read transaction is split in two VCI transactions
    //   if the source buffer is not aligned on a burst boundary.
    //   The write transaction is split in two VCI transactions
    //   if the destination buffer is not aligned on a burst boundary.
    //
    // Each channel FSM set the r_channel_vci_req[k] register to
    // request a VCI transaction to the CMD FSM. The CMD FSM analyse
    // the request type to build the relevant VCI command.
    // the RSP FSM analyse the request type to write the data in
    // the relevant register, and reset the r_channel_vci_req[k] register
    // to signal the VCI transaction completion.
    /////////////////////////////////////////////////////////////////////

    for ( uint32_t k=0 ; k<m_channels ; k++ )
    {
        switch( r_channel_fsm[k].read() )
        {
            //////////////////
            case CHANNEL_IDLE:
            {
                if ( r_channel_run[k] ) r_channel_fsm[k] = CHANNEL_READ_SRC_STATUS;
                break;
            }

            // get SRC buffer base address from SRC chbuf descriptor

            /////////////////////////////
            case CHANNEL_READ_SRC_STATUS:   // request VCI READ for SRC buffer status 
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_READ_SRC_STATUS;
                r_channel_fsm[k]      = CHANNEL_READ_SRC_STATUS_WAIT;
                break;
            }
            //////////////////////////////////
            case CHANNEL_READ_SRC_STATUS_WAIT:  // wait response for SRC buffer status
            {
                if ( r_channel_vci_rsp[k].read() ) 
                {
                    if ( r_channel_vci_error[k].read() )   
                    {
                        r_channel_fsm[k] = CHANNEL_SRC_DESC_ERROR;
                        break;
                    }
                    r_channel_vci_rsp[k] = false;
                    if ( not r_channel_src_full[k].read() ) // buffer not full
                    {
                        r_channel_fsm[k]   = CHANNEL_READ_SRC_STATUS_DELAY;
                        r_channel_timer[k] = r_channel_period[k];
                    }
                    else                                  // buffer full
                    {
                        r_channel_fsm[k] = CHANNEL_READ_SRC_BUFADDR;
                    }
                }
                break;
            }
            ///////////////////////////////////
            case CHANNEL_READ_SRC_STATUS_DELAY:  // delay to access SRC buffer status
            {
                if ( r_channel_timer[k].read() == 0 ) 
                {
                    r_channel_fsm[k]   = CHANNEL_READ_SRC_STATUS;
                }
                else
                {
                    r_channel_timer[k] = r_channel_timer[k].read() - 1;
                }
                break;
            }
            //////////////////////////////
            case CHANNEL_READ_SRC_BUFADDR:   // request VCI READ for SRC buffer address
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_READ_SRC_BUFADDR;
                r_channel_fsm[k]      = CHANNEL_READ_SRC_BUFADDR_WAIT;
                break;
            }
            ///////////////////////////////////
            case CHANNEL_READ_SRC_BUFADDR_WAIT:  // wait response for SRC buffer address
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    if ( r_channel_vci_error[k].read() )   
                    {
                        r_channel_fsm[k] = CHANNEL_SRC_DESC_ERROR;
                        break;
                    }
                    r_channel_vci_rsp[k] = false;
                    r_channel_fsm[k]     = CHANNEL_READ_DST_STATUS;
                }
                break;
            }

            // get DST buffer base address from DST chbuf descriptor

            /////////////////////////////
            case CHANNEL_READ_DST_STATUS:   // request VCI READ for DST buffer status 
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_READ_DST_STATUS;
                r_channel_fsm[k]      = CHANNEL_READ_DST_STATUS_WAIT;
                break;
            }
            //////////////////////////////////
            case CHANNEL_READ_DST_STATUS_WAIT:  // wait response for DST buffer status
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    if ( r_channel_vci_error[k] )   
                    {
                        r_channel_fsm[k] = CHANNEL_DST_DESC_ERROR;
                        break;
                    }
                    r_channel_vci_rsp[k] = false;

                    if ( r_channel_dst_full[k].read() ) // buffer full
                    {
                        r_channel_fsm[k]   = CHANNEL_READ_DST_STATUS_DELAY;
                        r_channel_timer[k] = r_channel_period[k];
                    }
                    else                                  // buffer not full
                    {
                        r_channel_fsm[k] = CHANNEL_READ_DST_BUFADDR;
                    }
                }
                break;
            }
            ///////////////////////////////////
            case CHANNEL_READ_DST_STATUS_DELAY:  // delay to access DST buffer status
            {
                if ( r_channel_timer[k].read() == 0 ) 
                {
                    r_channel_fsm[k]   = CHANNEL_READ_DST_STATUS;
                }
                else
                {
                    r_channel_timer[k] = r_channel_timer[k].read() - 1;
                }
                break;
            }
            //////////////////////////////
            case CHANNEL_READ_DST_BUFADDR:   // request VCI READ for DST buffer address
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_READ_DST_BUFADDR;
                r_channel_fsm[k]      = CHANNEL_READ_DST_BUFADDR_WAIT;
                break;
            }
            ///////////////////////////////////
            case CHANNEL_READ_DST_BUFADDR_WAIT:  // wait response for DST buffer address
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    if ( r_channel_vci_error[k] )   
                    {
                        r_channel_fsm[k] = CHANNEL_DST_DESC_ERROR;
                        break;
                    }
                    r_channel_vci_rsp[k]    = false;
                    r_channel_todo_bytes[k] = r_channel_buf_size[k].read();
                    r_channel_fsm[k]        = CHANNEL_READ_BURST;
                }
                break;
            }

            // move data from SRC buffer to DST buffer (internal loop)

            ///////////////////////////////
            case CHANNEL_READ_BURST:    // prepare the VCI READ burst
            {
                uint32_t first  = m_burst_max_length - r_channel_src_offset[k].read();
                uint32_t second = r_channel_src_offset[k].read();
                uint32_t length = r_channel_todo_bytes[k].read();

                if ( length > (first + second) ) 
                {
                    r_channel_bytes_first[k] = first;
                    r_channel_bytes_second[k] = second;
                }
                else if ( length > first  )
                {
                    r_channel_bytes_first[k] = first;
                    r_channel_bytes_second[k] = length - first;
                }
                else   // length <= first
                {
                    r_channel_bytes_first[k] = length;
                    r_channel_bytes_second[k] = 0;
                }
                r_channel_fsm[k] = CHANNEL_READ_REQ_FIRST;
                break;
            }
            ////////////////////////////
            case CHANNEL_READ_REQ_FIRST:	// request first VCI READ transaction
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_READ_FIRST_DATA;
                r_channel_fsm[k]      = CHANNEL_READ_WAIT_FIRST;
                break;
            }
            /////////////////////////////
            case CHANNEL_READ_WAIT_FIRST: 	// wait response for first VCI READ
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    r_channel_vci_rsp[k]  = false;

                    if ( r_channel_vci_error[k] )   
                    {
                        r_channel_fsm[k] = CHANNEL_SRC_DATA_ERROR;
                    }
                    else
                    {
                        r_channel_src_addr[k] = r_channel_src_addr[k].read() +
                                                r_channel_bytes_first[k].read();

                        if ( r_channel_bytes_second[k].read() == 0 ) 
                            r_channel_fsm[k] = CHANNEL_WRITE_BURST;
                        else            
                            r_channel_fsm[k] = CHANNEL_READ_REQ_SECOND;
                    }
                }
                break;
            }
            /////////////////////////////
            case CHANNEL_READ_REQ_SECOND:   // request second VCI READ transaction
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_READ_SECOND_DATA;
                r_channel_fsm[k]      = CHANNEL_READ_WAIT_SECOND;
                break;
            }
            //////////////////////////////
            case CHANNEL_READ_WAIT_SECOND:  // wait response for second VCI READ
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    r_channel_vci_rsp[k]  = false;

                    if ( r_channel_vci_error[k] )   
                    {
                        r_channel_fsm[k] = CHANNEL_SRC_DATA_ERROR;
                    }
                    else
                    {
                        r_channel_src_addr[k] = r_channel_src_addr[k].read() +
                                                r_channel_bytes_second[k].read();
                        r_channel_fsm[k] = CHANNEL_WRITE_BURST;
                    }
                }
                break;
            }
            /////////////////////////
            case CHANNEL_WRITE_BURST:	// prepare the VCI WRITE transaction(s)
            {
                uint32_t first  = m_burst_max_length - r_channel_dst_offset[k].read();
                uint32_t second = r_channel_dst_offset[k].read();
                uint32_t length = r_channel_todo_bytes[k].read();
                if ( length > (first + second) ) 
                {
                    r_channel_bytes_first[k] = first;
                    r_channel_bytes_second[k] = second;
                    r_channel_last[k]   = false;
                }
                else if ( length > first  )
                {
                    r_channel_bytes_first[k] = first;
                    r_channel_bytes_second[k] = length - first;
                    r_channel_last[k]   = true;
                }
                else   // length <= first
                {
                    r_channel_bytes_first[k] = length;
                    r_channel_bytes_second[k] = 0;
                    r_channel_last[k]   = true;
                }
                r_channel_fsm[k] = CHANNEL_WRITE_REQ_FIRST;
                break;
              }
            /////////////////////////////
            case CHANNEL_WRITE_REQ_FIRST:	// request first VCI WRITE transaction
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_WRITE_FIRST_DATA;
                r_channel_fsm[k]      = CHANNEL_WRITE_WAIT_FIRST;
                break;
            }
            //////////////////////////////
            case CHANNEL_WRITE_WAIT_FIRST:	// wait response for first VCI WRITE
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    r_channel_vci_rsp[k] = false;

                    if ( r_channel_vci_error[k] )
                    {
                        r_channel_fsm[k] = CHANNEL_DST_DATA_ERROR;
                        break;
                    }

                    r_channel_dst_addr[k] = r_channel_dst_addr[k].read() +
                                            r_channel_bytes_first[k].read();

                    if ( r_channel_bytes_second[k].read() != 0 ) 
                    {
                            r_channel_fsm[k] = CHANNEL_WRITE_REQ_SECOND;
                    }
                    else          
                    {
                        if ( r_channel_last[k].read() ) // buffer completed
                        {
                            r_channel_fsm[k] = CHANNEL_SRC_STATUS_WRITE;
                        }
                        else
                        {
                            r_channel_todo_bytes[k] = r_channel_todo_bytes[k].read()
                                                      - m_burst_max_length;
                            r_channel_fsm[k]        = CHANNEL_READ_BURST;
                        }
                    }
                }
                break;
            }
            //////////////////////////////
            case CHANNEL_WRITE_REQ_SECOND:	// request second VCI WRITE transaction
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_WRITE_SECOND_DATA;
                r_channel_fsm[k]      = CHANNEL_WRITE_WAIT_SECOND;
                break;
            }
            ///////////////////////////////
            case CHANNEL_WRITE_WAIT_SECOND:	// wait response for second VCI WRITE
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    r_channel_vci_rsp[k] = false;

                    if ( r_channel_vci_error[k] )
                    {
                        r_channel_fsm[k] = CHANNEL_DST_DATA_ERROR;
                        break;
                    }

                    r_channel_dst_addr[k] = r_channel_dst_addr[k].read() +
                                            r_channel_bytes_second[k].read();

                    if ( r_channel_last[k].read() ) 
                    {
                        r_channel_fsm[k] = CHANNEL_SRC_STATUS_WRITE;
                    }
                    else
                    {
                        r_channel_todo_bytes[k] = r_channel_todo_bytes[k].read()
                                                   - m_burst_max_length;
                        r_channel_fsm[k]        = CHANNEL_READ_BURST;
                    }
                }
                break;
            }

            // Release SRC & DST buffers and increment buffer indexes 

            /////////////////////////////
            case CHANNEL_SRC_STATUS_WRITE:
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_WRITE_SRC_STATUS;
                r_channel_fsm[k]      = CHANNEL_SRC_STATUS_WRITE_WAIT;
                break;
            }
            ///////////////////////////////////
            case CHANNEL_SRC_STATUS_WRITE_WAIT:
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    r_channel_vci_rsp[k] = false;
                    if ( r_channel_vci_error[k].read() ) 
                        r_channel_fsm[k] = CHANNEL_SRC_DESC_ERROR;
                    else                      
                        r_channel_fsm[k] = CHANNEL_DST_STATUS_WRITE;
                }
                break;
            }
            //////////////////////////////
            case CHANNEL_DST_STATUS_WRITE:
            {
                r_channel_vci_req[k]  = true;
                r_channel_vci_type[k] = REQ_WRITE_DST_STATUS;
                r_channel_fsm[k]      = CHANNEL_DST_STATUS_WRITE_WAIT;
                break;
            }
            ///////////////////////////////////
            case CHANNEL_DST_STATUS_WRITE_WAIT:
            {
                if ( r_channel_vci_rsp[k].read() )
                {
                    r_channel_vci_rsp[k] = false;
                    if ( r_channel_vci_error[k].read() ) 
                        r_channel_fsm[k] = CHANNEL_DST_DESC_ERROR;
                    else
                        r_channel_fsm[k] = CHANNEL_SRC_NEXT_BUFFER;
                }
                break;
            }
            /////////////////////////////
            case CHANNEL_SRC_NEXT_BUFFER:  // update SRC buffer descriptor index
            {
                if ( r_channel_src_index[k].read() == (r_channel_src_nbufs[k].read()-1) )
                {
                    r_channel_src_index[k] = 0;
                }
                else
                {
                    r_channel_src_index[k] = r_channel_src_index[k].read()+1;
                }
                r_channel_fsm[k] = CHANNEL_DST_NEXT_BUFFER;
                break;
            }
            /////////////////////////////
            case CHANNEL_DST_NEXT_BUFFER:  // update DST buffer descriptor index
            {
                if ( r_channel_dst_index[k].read() == (r_channel_dst_nbufs[k].read()-1) )
                {
                    r_channel_dst_index[k] = 0;
                }
                else
                {
                    r_channel_dst_index[k] = r_channel_dst_index[k].read()+1;
                }
                r_channel_fsm[k] = CHANNEL_IDLE;
                break;
            }

            // errors states
            ////////////////////////
            case CHANNEL_SRC_DATA_ERROR:
            case CHANNEL_DST_DATA_ERROR:
            case CHANNEL_SRC_DESC_ERROR:
            case CHANNEL_DST_DESC_ERROR:
            {
                if ( not r_channel_run[k] )	r_channel_fsm[k] = CHANNEL_IDLE;
                break; 
            }
        } // end switch r_channel_fsm[k]
    }
                
    ////////////////////////////////////////////////////////////////////////////
    // This CMD_FSM controls the VCI INIT command port
    ////////////////////////////////////////////////////////////////////////////

    switch(r_cmd_fsm.read()) 
    {
        //////////////
        case CMD_IDLE:
        {
            // round-robin arbitration between channels to send a command
            bool not_found = true;

            for( uint32_t n = 0 ; (n < m_channels) and not_found ; n++ )
            {
                uint32_t k = (r_cmd_channel.read() + n) % m_channels;
                if ( r_channel_vci_req[k].read() )
                {
                    not_found      = false;
                    r_cmd_channel  = k;
                    r_cmd_count    = 0;

                    switch ( r_channel_vci_type[k].read() )
                    {
                        case REQ_READ_SRC_STATUS:
                        {
                            r_cmd_address = r_channel_src_desc[k].read() + 4 + 
                                            (r_channel_src_index[k].read() << 3);
                            r_cmd_bytes   = 4;
                            r_cmd_fsm     = CMD_READ;
                            break;
                        }
                        case REQ_READ_DST_STATUS:
                        {
                            r_cmd_address = r_channel_dst_desc[k].read() + 4 +
                                            (r_channel_dst_index[k].read() << 3);
                            r_cmd_bytes   = 4;
                            r_cmd_fsm     = CMD_READ;
                            break;
                        }
                        case REQ_READ_SRC_BUFADDR:
                        {
                            r_cmd_address = r_channel_src_desc[k].read() + 
                                            (r_channel_src_index[k].read() << 3);
                            r_cmd_bytes   = 4;
                            r_cmd_fsm     = CMD_READ;
                            break;
                        }
                        case REQ_READ_DST_BUFADDR:
                        {
                            r_cmd_address = r_channel_dst_desc[k].read() +
                                            (r_channel_dst_index[k].read() << 3);
                            r_cmd_bytes   = 4;
                            r_cmd_fsm     = CMD_READ;
                            break;
                        }
                        case REQ_READ_FIRST_DATA:
                        {
                            r_cmd_address         = r_channel_src_addr[k].read();
                            r_cmd_bytes           = r_channel_bytes_first[k].read();
                            r_cmd_fsm             = CMD_READ;
                            break;
                        }
                        case REQ_READ_SECOND_DATA:
                        {
                            r_cmd_address         = r_channel_src_addr[k].read();
                            r_cmd_bytes           = r_channel_bytes_second[k].read();
                            r_cmd_fsm             = CMD_READ;
                            break;
                        }
                        case REQ_WRITE_FIRST_DATA:
                        {
                            r_cmd_address         = r_channel_dst_addr[k].read();
                            r_cmd_bytes           = r_channel_bytes_first[k].read();
                            r_cmd_fsm             = CMD_WRITE;
                            break;
                        }
                        case REQ_WRITE_SECOND_DATA:
                        {
                            r_cmd_address         = r_channel_dst_addr[k].read();
                            r_cmd_bytes           = r_channel_bytes_second[k].read();
                            r_cmd_fsm             = CMD_WRITE;
                            break;
                        }
                        case REQ_WRITE_SRC_STATUS:
                        {
                            r_cmd_address = r_channel_src_desc[k].read() + 4 +
                                            (r_channel_src_index[k].read() << 3);
                            r_cmd_bytes   = 4;
                            r_cmd_fsm     = CMD_WRITE;
                            break;
                        }
                        case REQ_WRITE_DST_STATUS:
                        {
                            r_cmd_address = r_channel_dst_desc[k].read() + 4 +
                                            (r_channel_dst_index[k].read() << 3);
                            r_cmd_bytes   = 4;
                            r_cmd_fsm     = CMD_WRITE;
                            break;
                        }
                    } // end switch
                    r_channel_vci_req[k]= false;
                } // end if
            }
            break;
        }
        //////////////
        case CMD_READ:
        {
            if ( p_vci_initiator.cmdack.read() )
            {
                r_cmd_fsm = CMD_IDLE;
            }
            break;
        }
        ///////////////
        case CMD_WRITE:
        {
            if ( p_vci_initiator.cmdack.read() )
            {
                if(vci_param::B==4)
                {
                    if ( r_cmd_count.read() == (r_cmd_bytes.read() - 4) )
                    {
                        r_cmd_fsm = CMD_IDLE;
                    }
                    r_cmd_count = r_cmd_count.read() + 4;
                }
                else
                {
                    if ( r_cmd_count.read() == (r_cmd_bytes.read() - 4) )
                    {
                        r_cmd_fsm = CMD_IDLE;
                        r_cmd_count = r_cmd_count.read() + 4;
                    }
                    else
                    {
                        if ( r_cmd_count.read() == (r_cmd_bytes.read() - 8))
                        { 
                           r_cmd_fsm = CMD_IDLE; 
                        }
                        r_cmd_count = r_cmd_count.read() + 8;
                    }
                    
                }
             }
            break;
        }
    } // end switch cmd_fsm

    ///////////////////////////////////////////////////////////////////////////
    // This RSP_FSM controls the VCI INIT response port
    // It writes in the relevant register, depending on the transaction type
    // defined by the r_channel_vci_type[k] registers.
    // It set r_channel_vci_rsp[k], and set r_channel_vci_error[k]
    // to signal completion of the read / write VCI transaction.
    ///////////////////////////////////////////////////////////////////////////
    switch(r_rsp_fsm.read()) 
    {
        //////////////
        case RSP_IDLE:
        {
            if ( p_vci_initiator.rspval.read() )
            {
                uint32_t k      = (uint32_t)p_vci_initiator.rtrdid.read()>>1;
                switch ( r_channel_vci_type[k].read() ) 
                {
                    case REQ_READ_SRC_STATUS:
                        r_rsp_count[k] = 0;
                        r_rsp_fsm = RSP_READ_SRC_STATUS;
                        break;
                    case REQ_READ_SRC_BUFADDR:
                        r_rsp_count[k] = 0;
                        r_rsp_fsm = RSP_READ_SRC_BUFADDR;
                        break;
                    case REQ_READ_DST_STATUS:
                        r_rsp_count[k] = 0;
                        r_rsp_fsm = RSP_READ_DST_STATUS;
                        break;
                    case REQ_READ_DST_BUFADDR:
                        r_rsp_count[k] = 0;
                        r_rsp_fsm = RSP_READ_DST_BUFADDR;
                        break;
                    case REQ_READ_FIRST_DATA:
                        r_rsp_count[k] = 0;
                        r_rsp_fsm = RSP_READ_DATA; 
                        break;
                    case REQ_READ_SECOND_DATA:
                        r_rsp_fsm = RSP_READ_DATA; 
                        break;
                    case REQ_WRITE_FIRST_DATA:
                    case REQ_WRITE_SECOND_DATA:
                    case REQ_WRITE_SRC_STATUS:
                    case REQ_WRITE_DST_STATUS:
                        r_rsp_count[k] = 0;
                        r_rsp_fsm = RSP_WRITE;
                        break;
                } // end switch
                r_rsp_channel = k;
            }
            break;
        } 
        /////////////////////////
        case RSP_READ_SRC_STATUS:   // set both SRC status and SRC buffer address MSB 
        {
            uint32_t k              = r_rsp_channel.read();
            uint32_t rdata          = (uint32_t)p_vci_initiator.rdata.read();
            r_channel_src_full[k]   = ((rdata>> 31) != 0);
            r_channel_src_addr[k]   = (r_channel_src_addr[k].read() & 0xFFFFFFFF) + 
                                      ((uint64_t)(rdata & 0x7FFFFFFF) << 32);
            r_channel_vci_rsp[k]    = true;
            r_channel_vci_error[k]  = ((p_vci_initiator.rerror.read()&0x1) != 0);
            r_rsp_fsm               = RSP_IDLE;
            break;
        }
        //////////////////////////
        case RSP_READ_SRC_BUFADDR:  // set SRC buffer address LSB
        {
            uint32_t k              = r_rsp_channel.read();
            uint32_t rdata          = (uint32_t)p_vci_initiator.rdata.read();
            r_channel_src_addr[k]   = (((r_channel_src_addr[k].read()>>32) & 0xFFFFFFFF)<<32) + rdata;
            r_channel_src_offset[k] = rdata % m_burst_max_length;
            r_channel_vci_rsp[k]    = true;
            r_channel_vci_error[k]  = ((p_vci_initiator.rerror.read()&0x1) != 0);
            r_rsp_fsm               = RSP_IDLE;
            break;
        }
        /////////////////////////
        case RSP_READ_DST_STATUS:
        {
            uint32_t k              = r_rsp_channel.read();
            uint32_t rdata          = (uint32_t)p_vci_initiator.rdata.read();
            r_channel_dst_full[k]   = ((rdata >> 31) != 0);
            r_channel_dst_addr[k]   = (r_channel_dst_addr[k].read() & 0xFFFFFFFF )+ ((typename vci_param::fast_addr_t)(rdata &0x7FFFFFFF) << 32);
            r_channel_vci_rsp[k]    = true;
            r_channel_vci_error[k]  = ((p_vci_initiator.rerror.read()&0x1) != 0);
            r_rsp_fsm               = RSP_IDLE;
            break;
        }
        //////////////////////////
        case RSP_READ_DST_BUFADDR:
        {
            uint32_t k              = r_rsp_channel.read();
            uint32_t rdata          = (uint32_t)p_vci_initiator.rdata.read();
            r_channel_dst_addr[k]   = (((r_channel_dst_addr[k].read()>>32) & 0xFFFFFFFF)<<32) + rdata;
            r_channel_dst_offset[k] = rdata % m_burst_max_length;
            r_channel_vci_rsp[k]    = true;
            r_channel_vci_error[k]  = ((p_vci_initiator.rerror.read()&0x1) != 0);
            r_rsp_fsm               = RSP_IDLE;
        }
        ///////////////////
        case RSP_READ_DATA:
        {
            if ( p_vci_initiator.rspval.read() )
            {
                uint32_t k    = r_rsp_channel.read();
                uint32_t word = r_rsp_count[k].read()>>2;
                if(vci_param::B==4)
                {
                    r_channel_buf[k][word] = p_vci_initiator.rdata.read(); 
                    r_rsp_count[k] = r_rsp_count[k].read() + 4;
                }
                else 
                {
                    r_channel_buf[k][word]   = (uint32_t)p_vci_initiator.rdata.read();
                    r_channel_buf[k][word+1] = (uint32_t)(p_vci_initiator.rdata.read()>>32); 
                    r_rsp_count[k] = r_rsp_count[k].read() + 8;
                }

                if ( p_vci_initiator.reop.read() )
                {
                    assert( (r_rsp_count[k].read() < m_burst_max_length) and
                    "VCI_CHBUF_DMA error : wrong number of flits for a read response");

                    r_channel_vci_rsp[k]    = true;
                    r_channel_vci_error[k] = ((p_vci_initiator.rerror.read()&0x1) != 0);
                    r_rsp_fsm = RSP_IDLE;
                } 
            }
            break;
        } 
        ///////////////
        case RSP_WRITE:
        {
            assert( (p_vci_initiator.reop.read() == true) and
             "VCI_CHBUF_DMA error : write response packed contains more than one flit");  

            uint32_t k             = r_rsp_channel.read();
            r_channel_vci_rsp[k]   = true;
            r_channel_vci_error[k] = ((p_vci_initiator.rerror.read()&0x1) != 0);
            r_rsp_fsm              = RSP_IDLE;
            break;
        } 
    } // end switch rsp_fsm
} // end transition

//////////////////////
tmpl(void)::genMoore()
{
    /////// VCI INIT CMD ports ////// 
    switch( r_cmd_fsm.read() ) 
    {
        case CMD_IDLE:
        {   
            p_vci_initiator.cmdval  = false;
            p_vci_initiator.address = 0;
            p_vci_initiator.wdata   = 0;
            p_vci_initiator.be      = 0;
            p_vci_initiator.plen    = 0;
            p_vci_initiator.cmd     = vci_param::CMD_WRITE;
            p_vci_initiator.trdid   = 0;
            p_vci_initiator.pktid   = 0;
            p_vci_initiator.srcid   = 0;
            p_vci_initiator.cons    = false;
            p_vci_initiator.wrap    = false;
            p_vci_initiator.contig  = false;
            p_vci_initiator.clen    = 0;
            p_vci_initiator.cfixed  = false;
            p_vci_initiator.eop     = false;
            break;
        }
        case CMD_READ:
        {   
            uint32_t k    = r_cmd_channel.read();
            typename vci_param::be_t r_be;
            if(vci_param::B == 4) r_be  = 0xF;
            else 
            {
                if       ( (r_channel_vci_type[k] == REQ_READ_SRC_STATUS) or 
                           (r_channel_vci_type[k] == REQ_READ_DST_STATUS) or 
                           (r_channel_vci_type[k] == REQ_READ_SRC_BUFADDR) or 
                           (r_channel_vci_type[k] == REQ_READ_DST_BUFADDR) ) r_be = 0x0F;
                else  if ( r_cmd_bytes.read() - r_cmd_count.read() == 4)     r_be = 0x0F;
                else                                                         r_be = 0xFF;
            }    
            p_vci_initiator.cmdval  = true;
            p_vci_initiator.address = (typename vci_param::fast_addr_t)r_cmd_address.read();
            p_vci_initiator.wdata   = 0;
            p_vci_initiator.be      = r_be;      
            p_vci_initiator.plen    = r_cmd_bytes.read();
            p_vci_initiator.cmd     = vci_param::CMD_READ;
            p_vci_initiator.trdid   = r_cmd_channel.read()<<1;	
            p_vci_initiator.pktid   = 0;
            p_vci_initiator.srcid   = m_srcid;
            p_vci_initiator.cons    = false;
            p_vci_initiator.wrap    = false;
            p_vci_initiator.contig  = true;
            p_vci_initiator.clen    = 0;
            p_vci_initiator.cfixed  = false;
            p_vci_initiator.eop     = true;
            break;
        }
        case CMD_WRITE:
        {
            uint32_t k    = r_cmd_channel.read();

            if (vci_param::B ==4)     // Data width 32 bits
            {  
                uint32_t wdata;

                if      ( r_channel_vci_type[k] == REQ_WRITE_SRC_STATUS ) 
                {
                    wdata = (uint32_t)(r_channel_src_addr[k].read()>>32);
                }
                else if ( r_channel_vci_type[k] == REQ_WRITE_DST_STATUS ) 
                {
                    wdata = (1<<31) + (uint32_t)(r_channel_src_addr[k].read()>>32);
                }
                else   
                {
                    size_t n; // word index in local buffer
                    if ( r_channel_vci_type[k].read() == REQ_WRITE_SECOND_DATA )
                        n = (r_cmd_count.read() + r_channel_bytes_first[k].read()) / 4;
                    else
                        n = (r_cmd_count.read() / 4);
                    wdata = r_channel_buf[k][n].read();
                }

                p_vci_initiator.cmdval  = true;
                p_vci_initiator.address = (typename vci_param::fast_addr_t)r_cmd_address.read() + 
                                             r_cmd_count.read();
                p_vci_initiator.wdata   = wdata;
                p_vci_initiator.be      = 0xF;
                p_vci_initiator.plen    = r_cmd_bytes.read();
                p_vci_initiator.cmd     = vci_param::CMD_WRITE;
                p_vci_initiator.trdid   = r_cmd_channel.read()<<1;	
                p_vci_initiator.pktid   = 0x4;
                p_vci_initiator.srcid   = m_srcid;
                p_vci_initiator.cons    = false;
                p_vci_initiator.wrap    = false;
                p_vci_initiator.contig  = true;
                p_vci_initiator.clen    = 0;
                p_vci_initiator.cfixed  = false;
                p_vci_initiator.eop     = ( r_cmd_count.read() == r_cmd_bytes.read() - 4 );
                
            }
            else          // Data width 64 bits
            {
                uint32_t wdata_low;
                uint32_t wdata_high;
                typename vci_param::be_t be;

                if       ( r_channel_vci_type[k] == REQ_WRITE_SRC_STATUS )
                {   
                    be         = 0x0F;
                    wdata_low  = (uint32_t)(r_channel_src_addr[k].read()>>32);
                    wdata_high = 0;
                }
                else if ( r_channel_vci_type[k] == REQ_WRITE_DST_STATUS ) 
                {   
                    be         = 0x0F;
                    wdata_low  = (1<<31) + (uint32_t)(r_channel_src_addr[k].read()>>32);
                    wdata_high = 0;
                }
                else  
                {
                    size_t n; // word index in local buffer
                    if ( r_channel_vci_type[k].read() == REQ_WRITE_SECOND_DATA )
                        n = (r_cmd_count.read() + r_channel_bytes_first[k].read()) / 4;
                    else
                        n = (r_cmd_count.read() / 4);

                    if ( r_cmd_bytes.read() - r_cmd_count.read() == 4)
                    {
                        be = 0x0F;
                        wdata_low  = r_channel_buf[k][n].read();
                        wdata_high = 0;                   
                    }
                    else 
                    {
                        be         = 0xFF;
                        wdata_low  = r_channel_buf[k][n].read();
                        wdata_high = r_channel_buf[k][n+1].read();
                    } 
                }
                
                p_vci_initiator.cmdval  = true;
                p_vci_initiator.address = (typename vci_param::fast_addr_t)r_cmd_address.read() 
                                           + r_cmd_count.read();
                p_vci_initiator.wdata   = ((uint64_t)wdata_high)<<32 | wdata_low ;
                p_vci_initiator.be      = be;
                p_vci_initiator.plen    = r_cmd_bytes.read();
                p_vci_initiator.cmd     = vci_param::CMD_WRITE;
                p_vci_initiator.trdid   = r_cmd_channel.read()<<1;	
                p_vci_initiator.pktid   = 0x4;
                p_vci_initiator.srcid   = m_srcid;
                p_vci_initiator.cons    = false;
                p_vci_initiator.wrap    = false;
                p_vci_initiator.contig  = true;
                p_vci_initiator.clen    = 0;
                p_vci_initiator.cfixed  = false;
                p_vci_initiator.eop     = (( r_cmd_count.read() == r_cmd_bytes.read() - 4 ) 
                                         || ( r_cmd_count.read() == r_cmd_bytes.read() - 8 ));
            break;
           
            }
        }
    } // end switch cmd_fsm

    /////// VCI INIT RSP port ////// 
    if ( r_rsp_fsm.read() == RSP_IDLE )  p_vci_initiator.rspack = false;
    else                                 p_vci_initiator.rspack = true;

    ////// VCI TARGET port /////// 
    switch( r_tgt_fsm.read() ) {
        case TGT_IDLE:
        {
            p_vci_target.cmdack = true;
            p_vci_target.rspval = false;
            p_vci_target.reop   = false;
            p_vci_target.rdata  = 0;
            break;
        }
        case TGT_WRITE:
        {
            p_vci_target.cmdack = false;
            p_vci_target.rspval = true;
            p_vci_target.rdata  = 0;
            p_vci_target.rerror = vci_param::ERR_NORMAL;
            p_vci_target.rsrcid = r_tgt_srcid.read();
            p_vci_target.rtrdid = r_tgt_trdid.read();
            p_vci_target.rpktid = r_tgt_pktid.read();
            p_vci_target.reop   = true;
            break;
        }
        case TGT_READ:
        {
            p_vci_target.cmdack = false;
            p_vci_target.rspval = true;
            p_vci_target.rdata  = r_tgt_rdata.read();
            p_vci_target.rerror = vci_param::ERR_NORMAL;
            p_vci_target.rsrcid = r_tgt_srcid.read();
            p_vci_target.rtrdid = r_tgt_trdid.read();
            p_vci_target.rpktid = r_tgt_pktid.read();
            p_vci_target.reop   = true;
            break;
        }
        case TGT_ERROR:
        {
            p_vci_target.cmdack = false;
            p_vci_target.rspval = true;
            p_vci_target.rdata  = 0;
            p_vci_target.rerror = vci_param::ERR_GENERAL_DATA_ERROR;
            p_vci_target.rsrcid = r_tgt_srcid.read();
            p_vci_target.rtrdid = r_tgt_trdid.read();
            p_vci_target.rpktid = r_tgt_pktid.read();
            p_vci_target.reop   = true;
            break;
        }
        default:
        {
            assert(false);
        }
    } // end switch rsp_fsm

    /////// IRQ ports //////////
    for ( uint32_t k = 0 ; k < m_channels ; k++ )
    {
	    p_irq[k] = (r_channel_fsm[k] == CHANNEL_SRC_DESC_ERROR) || 
                   (r_channel_fsm[k] == CHANNEL_DST_DESC_ERROR) ||
                   (r_channel_fsm[k] == CHANNEL_SRC_DATA_ERROR) ||
                   (r_channel_fsm[k] == CHANNEL_DST_DATA_ERROR);
    }
} // end genMoore

/////////////////////////
tmpl(void)::print_trace()
{
    const char* tgt_state_str[] = 
    {
        "  TGT_IDLE",
        "  TGT_READ",
        "  TGT_WRITE",
        "  TGT_ERROR"
    };
    const char* cmd_state_str[] = 
    {
        "  CMD_IDLE",
        "  CMD_READ",
        "  CMD_WRITE"
    };
    const char* rsp_state_str[] = 
    {
        "  RSP_IDLE",
        "  RSP_READ_SRC_STATUS",
        "  RSP_READ_SRC_BUFADDR",
        "  RSP_READ_DST_STATUS",
        "  RSP_READ_DST_BUFADDR",
        "  RSP_READ_DATA",
        "  RSP_WRITE"
    };
    const char* channel_state_str[] = 
    {
        "  CHANNEL_IDLE",

        "  CHANNEL_SRC_DATA_ERROR",
        "  CHANNEL_DST_DATA_ERROR",
        "  CHANNEL_SRC_DESC_ERROR",
        "  CHANNEL_DST_DESC_ERROR",

        "  CHANNEL_READ_SRC_STATUS",
        "  CHANNEL_READ_SRC_STATUS_WAIT",
        "  CHANNEL_READ_SRC_STATUS_DELAY",
        "  CHANNEL_READ_SRC_BUFADDR",
        "  CHANNEL_READ_SRC_BUFADDR_WAIT",

        "  CHANNEL_READ_DST_STATUS",
        "  CHANNEL_READ_DST_STATUS_WAIT",
        "  CHANNEL_READ_DST_STATUS_DELAY",
        "  CHANNEL_READ_DST_BUFADDR",
        "  CHANNEL_READ_DST_BUFADDR_WAIT",

        "  CHANNEL_READ_BURST",
        "  CHANNEL_READ_REQ_FIRST",
        "  CHANNEL_READ_WAIT_FIRST",
        "  CHANNEL_READ_REQ_SECOND",
        "  CHANNEL_READ_WAIT_SECOND",

        "  CHANNEL_WRITE_BURST",
        "  CHANNEL_WRITE_REQ_FIRST",
        "  CHANNEL_WRITE_WAIT_FIRST",
        "  CHANNEL_WRITE_REQ_SECOND",
        "  CHANNEL_WRITE_WAIT_SECOND",

        "  CHANNEL_SRC_STATUS_WRITE",
        "  CHANNEL_SRC_STATUS_WRITE_WAIT",
        "  CHANNEL_DST_STATUS_WRITE",
        "  CHANNEL_DST_STATUS_WRITE_WAIT",
        "  CHANNEL_SRC_NEXT_BUFFER",
        "  CHANNEL_DST_NEXT_BUFFER",
    };

    std::cout << "CHBUF_DMA " << name() << " : " 
              << tgt_state_str[r_tgt_fsm.read()] << std::endl;
    for ( uint32_t k = 0 ; k < m_channels ; k++ )
    {
        if ( r_channel_run[k].read() )
        {
            std::cout << "  CHANNEL[" << std::dec << k << "] : "
                      << channel_state_str[r_channel_fsm[k].read()]
                      << " / src_addr = " << std::hex << r_channel_src_addr[k].read()
                      << " / src_buf_id = " << std::dec << r_channel_src_index[k].read()
                      << " / dst_addr = " << std::hex << r_channel_dst_addr[k].read() 
                      << " / dst_buf_id = " << std::dec << r_channel_dst_index[k].read()
                      << " / todo_bytes = " << std::dec << r_channel_todo_bytes[k].read()
                      << std::endl;
        }
    }
    std::cout << cmd_state_str[r_cmd_fsm.read()] << std::dec 
              << " / channel = " << r_cmd_channel.read()
              << " / length = " << r_cmd_bytes.read()
              << " / count = " << r_cmd_count.read()/4 << std::endl;
    std::cout << rsp_state_str[r_rsp_fsm.read()] << std::dec 
              << " / channel = " << r_rsp_channel.read()
              << " / length = " << r_rsp_bytes.read()
              << " / count = " << r_rsp_count[r_rsp_channel.read()].read()/4 << std::endl;
}

////////////////////////////////////////////////////////////////////
tmpl(/**/)::VciChbufDma( sc_core::sc_module_name 		        name,
                         const soclib::common::MappingTable 	&mt,
                         const soclib::common::IntTab 		    &srcid,
                         const soclib::common::IntTab 		    &tgtid,
	                     const uint32_t 				        burst_max_length,
                         const uint32_t 				        channels )
	: caba::BaseModule(name),

          r_tgt_fsm("r_tgt_fsm"),
          r_tgt_srcid("r_tgt_srcid"),
          r_tgt_trdid("r_tgt_trdid"),
          r_tgt_pktid("r_tgt_pktid"),
          r_tgt_rdata("r_tgt_rdata"),

          r_channel_fsm(soclib::common::alloc_elems<sc_signal<int> >
                    ("r_channel_fsm", channels)),
          r_channel_run(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_run", channels)),
          r_channel_buf_size(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_buf_size", channels)),

          r_channel_src_desc(soclib::common::alloc_elems<sc_signal<uint64_t> >
                    ("r_channel_src_desc", channels)),
          r_channel_src_nbufs(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_src_nbufs", channels)),
          r_channel_src_addr(soclib::common::alloc_elems<sc_signal<uint64_t> >
                    ("r_channel_src_addr", channels)),
          r_channel_src_index(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_src_index", channels)),
          r_channel_src_offset(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_src_offset", channels)),
          r_channel_src_full(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_src_full", channels)),

          r_channel_dst_desc(soclib::common::alloc_elems<sc_signal<uint64_t> >
                    ("r_channel_dst_desc", channels)),
          r_channel_dst_nbufs(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_dst_nbufs", channels)),
          r_channel_dst_addr(soclib::common::alloc_elems<sc_signal<uint64_t> >
                    ("r_channel_dst_addr", channels)),
          r_channel_dst_index(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_dst_index", channels)),
          r_channel_dst_offset(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_dst_offset", channels)),
          r_channel_dst_full(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_dst_full", channels)),

          r_channel_timer(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_timer", channels)),
          r_channel_period(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_period", channels)),
          r_channel_todo_bytes(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_todo_bytes", channels)),
          r_channel_bytes_first(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_bytes_first", channels)),
          r_channel_bytes_second(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_bytes_second", channels)),
          r_channel_vci_req(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_vci_req", channels)),
          r_channel_vci_type(soclib::common::alloc_elems<sc_signal<int> >
                    ("r_channel_vci_type", channels)),
          r_channel_vci_rsp(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_vci_rsp", channels)),
          r_channel_vci_error(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_vci_error", channels)),
          r_channel_last(soclib::common::alloc_elems<sc_signal<bool> >
                    ("r_channel_last", channels)),
          r_channel_buf(soclib::common::alloc_elems<sc_signal<uint32_t> >
                    ("r_channel_buf", channels, burst_max_length/4)),

          r_cmd_fsm("r_cmd_fsm"),
          r_cmd_count("r_cmd_count"),
          r_cmd_address("r_cmd_address"),
          r_cmd_channel("r_cmd_channel"),
          r_cmd_bytes("r_cmd_bytes"),

          r_rsp_fsm("r_rsp_fsm"),
          r_rsp_count(soclib::common::alloc_elems<sc_signal<size_t> >
                    ("r_rsp_count", channels)),
          r_rsp_channel("r_rsp_channel"),
          r_rsp_bytes("r_rsp_bytes"),

          m_seglist(mt.getSegmentList(tgtid)),
          m_burst_max_length(burst_max_length),
          m_channels(channels),
          m_srcid(mt.indexForId(srcid)),

          p_clk("p_clk"),
          p_resetn("p_resetn"),
          p_vci_target("p_vci_target"),
          p_vci_initiator("p_vci_initiator"),
          p_irq(soclib::common::alloc_elems<sc_core::sc_out<bool> >("p_irq", channels))
{
    std::cout << "  - Building VciChbufDma : " << name << std::endl;

    assert( (m_seglist.empty() == false) and 
    "VCI_CHBUF_DMA error : No segment allocated");

    std::list<soclib::common::Segment>::iterator seg;
    for ( seg = m_seglist.begin() ; seg != m_seglist.end() ; seg++ ) 
    {
	    assert( ( (seg->baseAddress() & 0xFFF) == 0 ) and 
		"VCI_CHBUF_DMA Error : The segment base address must be multiple of 4 Kbytes"); 

	    assert( ( seg->size() >= (m_channels<<12) ) and 
		"VCI_CHBUF_DMA Error : The segment size cannot be smaller than 4K * channels"); 

        std::cout << "    => segmesnt " << seg->name()
                  << " / base = " << std::hex << seg->baseAddress()
                  << " / size = " << seg->size() << std::endl; 
    }

    assert( (vci_param::T >= 4) and 
    "VCI_CHBUF_DMA error : The VCI TRDID field must be at least 4 bits");

    assert( ((vci_param::B == 4) or ( vci_param::B == 8 ) ) and 
    "VCI_CHBUF_DMA error : The VCI DATA field must be 32 or 64 bits");

    assert( (burst_max_length < (1<<vci_param::K)) and 
    "VCI_CHBUF_DMA error : The VCI PLEN size is too small for requested burst length");

    assert( (((burst_max_length==4)or(burst_max_length==8)or(burst_max_length==16)or 
             (burst_max_length==32)or(burst_max_length==64))) and
    "VCI_CHBUF_DMA error : The burst length must be 4, 8, 16, 32, 64 bytes");
    
    assert( (channels <= 8)  and
    "VCI_CHBUF_DMA error : The number of channels cannot be larger than 8");

    SC_METHOD(transition);
    dont_initialize();
    sensitive << p_clk.pos();

    SC_METHOD(genMoore);
    dont_initialize();
    sensitive << p_clk.neg();
}


tmpl(/**/)::~VciChbufDma() {
    soclib::common::dealloc_elems<sc_signal<int> >(r_channel_fsm, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_run, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_buf_size, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint64_t> >(r_channel_src_desc, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_src_nbufs, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint64_t> >(r_channel_src_addr, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_src_index, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_src_offset, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_src_full, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint64_t> >(r_channel_dst_desc, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_dst_nbufs, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint64_t> >(r_channel_dst_addr, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_dst_index, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_dst_offset, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_dst_full, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_timer, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_period, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_todo_bytes, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_bytes_first, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_bytes_second, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_vci_req, m_channels);
    soclib::common::dealloc_elems<sc_signal<int> >(r_channel_vci_type, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_vci_rsp, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_vci_error, m_channels);
    soclib::common::dealloc_elems<sc_signal<bool> >(r_channel_last, m_channels);
    soclib::common::dealloc_elems<sc_signal<uint32_t> >(r_channel_buf, m_channels, m_burst_max_length / 4);
    soclib::common::dealloc_elems<sc_signal<size_t> >(r_rsp_count, m_channels);
    soclib::common::dealloc_elems<sc_core::sc_out<bool> >(p_irq, m_channels);
}



}}

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

