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
 * Copyright (c) UPMC, Lip6, Asim
 *         alain.greiner@lip6.fr
 *         Clement Devigne <clement.devigne@etu.upmc.fr>
 *         Sylvain Leroy <sylvain.leroy@lip6.fr>
 *         Cassio Fraga <cassio.fraga@lip6.fr>
 *
 * Maintainers: alain
 */

#ifndef SOCLIB_VCI_MULTI_NIC_H
#define SOCLIB_VCI_MULTI_NIC_H

#include <stdint.h>
#include <systemc>
#include "vci_target.h"
#include "caba_base_module.h"
#include "mapping_table.h"
#include "fifo_multi_buffer.h"
#include "nic_rx_chbuf.h"
#include "nic_tx_chbuf.h"
#include "nic_rx_gmii.h"
#include "nic_tx_gmii.h"
#include "generic_fifo.h"

namespace soclib {
namespace caba {

using namespace sc_core;


template<typename vci_param>
class VciMultiNic
	: public caba::BaseModule
{
private:

    // methods
    void        transition();
    void        genMoore();
    uint32_t    read_register(uint32_t addr);
    // int32_t     open_tap_fd();

    // Instrumentation registers
    sc_signal<uint32_t>         r_total_len_rx_gmii;       // Bytes receive by RX_GMII
    sc_signal<uint32_t>         r_total_len_rx_chan;       // Bytes receive by RX_CHAN
    sc_signal<uint32_t>         r_total_len_tx_chan;       // Bytes send    by TX_CHAN
    sc_signal<uint32_t>         r_total_len_tx_gmii;       // Bytes send    by TX_GMII

    // Global registers
    sc_signal<bool>             r_global_bc_enable;        // broadcast enabled
    sc_signal<bool>             r_global_nic_on;           // NIC component activated
    sc_signal<uint32_t>         r_global_nic_nb_chan;      // number of channel present in this NIC
    sc_signal<uint32_t>         r_global_active_channels;  // bitfield: disabled if 0
    sc_signal<bool>             r_global_tdm_enable;       // TDM scheduling enabled
    sc_signal<uint32_t>         r_global_tdm_period;       // TDM time slot (hardcoded as a define)
    sc_signal<uint32_t>         r_global_tdm_timer;        // TDM timer (all cycles)
    sc_signal<uint32_t>         r_global_tdm_channel;      // current channel for TDM
    sc_signal<bool>             r_global_bypass_enable;    // current channel for TDM

    // Channel CONFIGURATION registers
    sc_signal<uint32_t>                     *r_channel_mac_4;        // MAC address (first 4 bytes)
    sc_signal<uint32_t>                     *r_channel_mac_2;        // MAC address (last 2 bytes)
    sc_signal<typename vci_param::addr_t>   *r_channel_rx_bufaddr_0; // base address of RX buffer[0]
    sc_signal<typename vci_param::addr_t>   *r_channel_rx_bufaddr_1; // base address of RX buffer[1]
    sc_signal<bool>                         *r_channel_rx_run;       // RX chbuf activated          
    sc_signal<typename vci_param::addr_t>   *r_channel_tx_bufaddr_0; // base address of TX buffer[0]
    sc_signal<typename vci_param::addr_t>   *r_channel_tx_bufaddr_1; // base address of TX buffer[1]
    sc_signal<bool>                         *r_channel_tx_run;       // TX chbuf activated          


    // VCI FSM registers
    sc_signal<int>			    r_vci_fsm;
    sc_signal<typename vci_param::srcid_t>	r_vci_srcid;   // for rsrcid
    sc_signal<typename vci_param::trdid_t>	r_vci_trdid;   // for rtrdid
    sc_signal<typename vci_param::pktid_t>	r_vci_pktid;   // for rpktid
    sc_signal<typename vci_param::data_t>	r_vci_wdata;   // for write burst
    sc_signal<typename vci_param::be_t>	    r_vci_be;      // for write burst in 64 bits data
    sc_signal<size_t>           r_vci_nwords;              // word counter
    sc_signal<uint32_t>         r_vci_address;             // used for bursts

    // RX_G2S FSM registers
    sc_signal<int>              r_rx_g2s_fsm;
    sc_signal<uint32_t>         r_rx_g2s_checksum;         // packet checksum
    sc_signal<uint8_t>          r_rx_g2s_dt0;              // local data buffer
    sc_signal<uint8_t>          r_rx_g2s_dt1;              // local data buffer
    sc_signal<uint8_t>          r_rx_g2s_dt2;              // local data buffer
    sc_signal<uint8_t>          r_rx_g2s_dt3;              // local data buffer
    sc_signal<uint8_t>          r_rx_g2s_dt4;              // local data buffer
    sc_signal<uint8_t>          r_rx_g2s_dt5;              // local data buffer
    sc_signal<size_t>           r_rx_g2s_delay;            // delay cycle counter

    sc_signal<uint32_t>         r_rx_g2s_npkt_received;    // number of received packets
    sc_signal<uint32_t>         r_rx_g2s_npkt_discarded;   // number of discarded packets

    // RX_DES FSM registers
    sc_signal<int>              r_rx_des_fsm;
    sc_signal<uint32_t>         r_rx_des_counter_bytes;    // nb bytes in one packet
    sc_signal<uint32_t>         r_rx_des_padding;          // padding
    sc_signal<uint8_t>*         r_rx_des_data;             // array[4]

    sc_signal<uint32_t>         r_rx_des_npkt_success;           // transmit packets
    sc_signal<uint32_t>         r_rx_des_npkt_too_small;         // discarded packets
    sc_signal<uint32_t>         r_rx_des_npkt_too_big;           // discarded packets
    sc_signal<uint32_t>         r_rx_des_npkt_mfifo_full;        // discarded packets
    sc_signal<uint32_t>         r_rx_des_npkt_cs_fail;           // discarded packets

    // RX_DISPATCH registers
    sc_signal<int>              r_rx_dispatch_fsm;
    sc_signal<bool>             r_rx_dispatch_bp;          // previouly allocated to bp
    sc_signal<uint32_t>         r_rx_dispatch_data0;       // first word mac address
    sc_signal<uint32_t>         r_rx_dispatch_data1;       // second word mac address
    sc_signal<uint32_t>         r_rx_dispatch_nbytes;      // number of bytes to be written
    sc_signal<uint32_t>         r_rx_dispatch_dest;        // bit vector: 1 bit per channel

    sc_signal<uint32_t>         r_rx_dispatch_npkt_received;     // received packets
    sc_signal<uint32_t>         r_rx_dispatch_npkt_broadcast;    // broadcast packets
    sc_signal<uint32_t>         r_rx_dispatch_npkt_dst_fail;     // discarded packets
    sc_signal<uint32_t>         r_rx_dispatch_npkt_channel_full; // discarded packets

    // TX_DISPATCH registers
    sc_signal<int>              r_tx_dispatch_fsm;
    sc_signal<size_t>           r_tx_dispatch_channel;     // selected channel index
    sc_signal<uint32_t>         r_tx_dispatch_data0;       // first word value
    sc_signal<uint32_t>         r_tx_dispatch_data1;       // second word value
    sc_signal<uint32_t>         r_tx_dispatch_packets;     // number of packets to send
    sc_signal<uint32_t>         r_tx_dispatch_words;       // words counter in packet
    sc_signal<uint32_t>         r_tx_dispatch_bytes;       // bytes in last word
    sc_signal<bool>             r_tx_dispatch_write_bp;    // to be written in bp fifo
    sc_signal<bool>             r_tx_dispatch_write_tx;    // to be written in tx fifo

    sc_signal<uint32_t>         r_tx_dispatch_npkt_received;     // received packets
    sc_signal<uint32_t>         r_tx_dispatch_npkt_too_small;    // discarded packets
    sc_signal<uint32_t>         r_tx_dispatch_npkt_too_big;      // discarded packets
    sc_signal<uint32_t>         r_tx_dispatch_npkt_src_fail;     // discarded packets
    sc_signal<uint32_t>         r_tx_dispatch_npkt_broadcast;    // broadcast packets
    sc_signal<uint32_t>         r_tx_dispatch_npkt_bypass;       // bypass packets
    sc_signal<uint32_t>         r_tx_dispatch_npkt_transmit;     // transmit packets

    // TX_SER registers
    sc_signal<int>              r_tx_ser_fsm;
    sc_signal<uint32_t>         r_tx_ser_words;            // number of words
    sc_signal<uint32_t>         r_tx_ser_bytes;            // bytes in last word
    sc_signal<bool>             r_tx_ser_first;            // first word
    sc_signal<uint32_t>         r_tx_ser_ifg;              // inter-frame-gap counter
    sc_signal<uint32_t>         r_tx_ser_data;             // 32 bits buffer


    // TX_S2G registers
    sc_signal<int>              r_tx_s2g_fsm;
    sc_signal<uint32_t>         r_tx_s2g_checksum;         // packet checksum
    sc_signal<uint8_t>          r_tx_s2g_data;             // local data buffer
    sc_signal<size_t>           r_tx_s2g_index;            // checksum byte index

    // channels
    NicRxChbuf*                 r_rx_chbuf;                // array[m_channels]
    NicTxChbuf*                 r_tx_chbuf;                // array[m_channels]

    // fifos
    GenericFifo<uint16_t>       r_rx_fifo_stream;
    GenericFifo<uint16_t>       r_tx_fifo_stream;
    FifoMultiBuffer             r_rx_fifo_multi;
    FifoMultiBuffer             r_tx_fifo_multi;
    FifoMultiBuffer             r_bp_fifo_multi;

    // Packets in and out
    NicRxGmii                   r_gmii_rx;
    NicTxGmii                   r_gmii_tx;
    // RX                                      r_backend_rx;
    // TX                                      r_backend_tx;

    // sructural parameters
    std::list<soclib::common::Segment>  m_seglist;          // allocated segment(s)
    const size_t				        m_channels;		    // no more than 8 channels
    const uint32_t                      m_default_mac_4;    // MAC address 32 LSB bits
    const uint32_t                      m_default_mac_2;    // MAC address 16 MSB bits
    // struct ifreq                            m_tap_ifr;

protected:

    SC_HAS_PROCESS(VciMultiNic);

public:

    // FSM states
    enum vci_tgt_fsm_state_e {
        VCI_IDLE,
        VCI_WRITE_TX_BURST,
        VCI_WRITE_TX_LAST,
        VCI_WRITE_HYPER_REG,
        VCI_WRITE_CHANNEL_REG,
        VCI_READ_RX_BURST,
        VCI_READ_HYPER_REG,
        VCI_READ_CHANNEL_REG,
        VCI_ERROR,
    };
    enum rx_g2s_fsm_state_e {
        RX_G2S_IDLE,
        RX_G2S_DELAY,
        RX_G2S_LOAD,
        RX_G2S_SOS,
        RX_G2S_LOOP,
        RX_G2S_END,
        RX_G2S_ERR,
        RX_G2S_FAIL,
    };
    enum rx_des_fsm_state_e {
    	RX_DES_IDLE,
	    RX_DES_READ_1,
	    RX_DES_READ_2,
	    RX_DES_READ_3,
	    RX_DES_READ_WRITE_0,
	    RX_DES_READ_WRITE_1,
	    RX_DES_READ_WRITE_2,
	    RX_DES_READ_WRITE_3,
	    RX_DES_WRITE_LAST,
	    RX_DES_WRITE_CLEAR,
    };
    enum rx_dispatch_fsm_state_e {
        RX_DISPATCH_IDLE,
        RX_DISPATCH_GET_PLEN,
        RX_DISPATCH_READ_FIRST,
        RX_DISPATCH_READ_SECOND,
        RX_DISPATCH_CHECK_BC,
        RX_DISPATCH_SELECT,
        RX_DISPATCH_SELECT_BC,
        RX_DISPATCH_PACKET_SKIP,
        RX_DISPATCH_CLOSE_CONT,
        RX_DISPATCH_WRITE_FIRST,
        RX_DISPATCH_READ_WRITE,
        RX_DISPATCH_WRITE_LAST,
    };
    enum tx_dispatch_fsm_state_e {
        TX_DISPATCH_IDLE,
        TX_DISPATCH_GET_NPKT,
        TX_DISPATCH_GET_PLEN,
        TX_DISPATCH_SKIP_PKT,
        TX_DISPATCH_READ_FIRST,
        TX_DISPATCH_READ_SECOND,
        TX_DISPATCH_FIFO_SELECT,
        TX_DISPATCH_WRITE_FIRST,
        TX_DISPATCH_READ_WRITE,
        TX_DISPATCH_WRITE_LAST,
        TX_DISPATCH_RELEASE_CONT,
    };
    enum tx_ser_fsm_state_e {
        TX_SER_IDLE,
        TX_SER_READ_FIRST,
        TX_SER_WRITE_B0,
        TX_SER_WRITE_B1,
        TX_SER_WRITE_B2,
        TX_SER_READ_WRITE,
        TX_SER_GAP,
    };
    enum tx_s2g_fsm_state_e {
        TX_S2G_IDLE,
        TX_S2G_WRITE_DATA,
        TX_S2G_WRITE_LAST_DATA,
        TX_S2G_WRITE_CS,
    };

    // Stream types
    enum stream_type_e {
        STREAM_TYPE_SOS,     // start of stream
        STREAM_TYPE_EOS,     // end of stream
        STREAM_TYPE_ERR,     // corrupted end of stream
        STREAM_TYPE_NEV,     // no special event
    };

    enum masks {
        DESC_STATUS_MASK = 0x80000000,  // most significant bit of the DESC_HI word
    };

    // ports
    sc_in<bool> 				            p_clk;
    sc_in<bool> 				            p_resetn;
    soclib::caba::VciTarget<vci_param> 		p_vci;
    sc_out<bool>* 				            p_rx_irq;
    sc_out<bool>* 				            p_tx_irq;

    void print_trace(uint32_t option = 1);

    uint32_t get_total_len_gmii();
    uint32_t get_total_len_rx_chan();
    uint32_t get_total_len_tx_chan();
    uint32_t get_total_len_tx_gmii();

    uint32_t read_hyper_register(uint32_t);
    uint32_t read_channel_register(uint32_t);

    VciMultiNic(sc_module_name           			name,
                const soclib::common::IntTab 		&tgtid,
                const soclib::common::MappingTable 	&mt,
                const size_t				        channels,           // number of channels
                const char*                         rx_file_pathname,   // received packets
                const char*                         tx_file_pathname,   // transmitted packets
                const uint32_t                      mac_4,              // default mac address
                const uint32_t                      mac_2);             // default mac address
    ~VciMultiNic();

};

}}

#endif

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

