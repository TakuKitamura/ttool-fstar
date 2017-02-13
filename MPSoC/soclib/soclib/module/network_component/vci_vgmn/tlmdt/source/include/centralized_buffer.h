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
 * Maintainers: fpecheux, alinevieiramello@hotmail.com
 *
 * Copyright (c) UPMC / Lip6, 2008
 *     François Pêcheux <francois.pecheux@lip6.fr>
 *     Aline Vieira de Mello <aline.vieira-de-mello@lip6.fr>
 */

#ifndef CENTRALIZED_BUFFER_H
#define CENTRALIZED_BUFFER_H

#include <tlmdt>	             // TLM-DT headers
#include "circular_buffer.h"

namespace soclib { namespace tlmdt {

class _command;

class centralized_buffer
  : public sc_core::sc_module        // inherit from SC module base clase
{
  const size_t        m_slots;
  _command           *m_centralized_struct;

  int                 m_count_push;
  int                 m_count_pop;

public:
  centralized_buffer
  ( sc_core::sc_module_name module_name,  // SC module name
    size_t                  max);

  ~centralized_buffer();

  bool push
  ( size_t                    from,
    tlm::tlm_generic_payload &payload,
    tlm::tlm_phase           &phase,
    sc_core::sc_time         &time);

  bool pop
  ( size_t                    &from,
    tlm::tlm_generic_payload *&payload,
    tlm::tlm_phase           *&phase,
    sc_core::sc_time         *&time);

  circular_buffer get_buffer(int i);

  const size_t get_nslots();

  const size_t get_free_slots();

  sc_core::sc_time get_delta_time(unsigned int index);

  void set_activity(unsigned int index, bool b);

  void set_delta_time(unsigned int index, sc_core::sc_time t);
};

}}

#endif
