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
 *         Nicolas Pouillon <nipo@ssji.net>, 2008
 *
 * Based on previous works by Alain Greiner, 2005
 *
 * Maintainers: nipo
 */
#ifndef SOCLIB_CABA_VCI_LOCAL_CROSSBAR_H_
#define SOCLIB_CABA_VCI_LOCAL_CROSSBAR_H_

#include <systemc>
#include "caba_base_module.h"
#include "vci_initiator.h"
#include "vci_target.h"
#include "vci_buffers.h"
#include "mapping_table.h"

namespace soclib { namespace caba {

namespace _local_crossbar {
template<typename pkt_t> class Crossbar;
}

template<typename vci_param>
class VciLocalCrossbar
    : public BaseModule
{
public:
    sc_in<bool> p_clk;
    sc_in<bool> p_resetn;

    VciInitiator<vci_param> *p_to_target;
    VciTarget<vci_param> *p_to_initiator;
    VciTarget<vci_param> p_target_to_up;
    VciInitiator<vci_param> p_initiator_to_up;

private:
    size_t m_nb_attached_initiat;
    size_t m_nb_attached_target;

    VciInitiator<vci_param> **m_ports_to_target;
    VciTarget<vci_param> **m_ports_to_initiator;

    typedef _local_crossbar::Crossbar<VciCmdBuffer<vci_param> > cmd_crossbar_t;
    typedef _local_crossbar::Crossbar<VciRspBuffer<vci_param> > rsp_crossbar_t;

    void transition();
    void genMealy();

	cmd_crossbar_t *m_cmd_crossbar;
	rsp_crossbar_t *m_rsp_crossbar;

protected:
    SC_HAS_PROCESS(VciLocalCrossbar);

public:
    void print_trace();

    VciLocalCrossbar( sc_module_name name,
					  const soclib::common::MappingTable &mt,
					  const soclib::common::IntTab &srcid,
					  const soclib::common::IntTab &tgtid,
					  size_t nb_attached_initiat,
					  size_t nb_attached_target );
    ~VciLocalCrossbar();
};

}}

#endif /* SOCLIB_CABA_VCI_LOCAL_CROSSBAR_H_ */

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4
