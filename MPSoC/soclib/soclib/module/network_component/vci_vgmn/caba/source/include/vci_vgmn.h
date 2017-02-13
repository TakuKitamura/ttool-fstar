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
 *         Nicolas Pouillon <nipo@ssji.net>, 2007
 *
 * Based on previous works by Laurent Mortiez & Alain Greiner, 2005
 *
 * Maintainers: nipo
 */
#ifndef SOCLIB_CABA_VCI_VGMN_H_
#define SOCLIB_CABA_VCI_VGMN_H_

#include <systemc>
#include "caba_base_module.h"
#include "vci_initiator.h"
#include "vci_target.h"
#include "vci_buffers.h" 
//#include "vci_buffers_vgmn.h" //DG 31.08. we require a different kind of buffer than the vci_local_crossbar
#include "address_decoding_table.h"
#include "address_masking_table.h"
#include "mapping_table.h"
#include "generic_fifo.h"

namespace soclib { namespace caba {

using namespace sc_core;
using namespace soclib::caba;
using namespace soclib::common;

template<typename vci_flit_t, 
         typename vci_input_t,
         typename vci_output_t> class VgmnMicroNetwork;

template<typename vci_param>
class VciVgmn
    : public soclib::caba::BaseModule
{
public:
    sc_in<bool>                                 p_clk;
    sc_in<bool>                                 p_resetn;

    VciInitiator<vci_param>                     *p_to_target;
    VciTarget<vci_param>                        *p_to_initiator;

private:
    const size_t                                m_nb_initiat;
    const size_t                                m_nb_target;

    AddressDecodingTable<uint32_t, int>         m_cmd_routing_table;
    AddressMaskingTable<uint32_t>               m_rsp_routing_table;
 
    VgmnMicroNetwork<VciCmdBuffer<vci_param>,
                     VciTarget<vci_param>,
                     VciInitiator<vci_param> >  *m_cmd_mn;

    VgmnMicroNetwork<VciRspBuffer<vci_param>,
                     VciInitiator<vci_param>,
                     VciTarget<vci_param> >     *m_rsp_mn;

    void transition();
    void genMoore();

protected:
    SC_HAS_PROCESS(VciVgmn);

public:
    void print_trace();

    VciVgmn( sc_module_name name,
              const soclib::common::MappingTable &mt,
              size_t nb_initiat,
              size_t nb_target,
              size_t min_latency,
              size_t fifo_depth,
              const soclib::common::IntTab &default_index = 0);

    ~VciVgmn();
};

}}

#endif /* SOCLIB_CABA_VCI_VGMN_H_ */

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4
