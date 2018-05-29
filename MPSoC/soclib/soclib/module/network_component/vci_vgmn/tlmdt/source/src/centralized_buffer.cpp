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

#include <systemc>
#include "centralized_buffer.h"

namespace soclib { namespace tlmdt {

class _command
{
  friend class centralized_buffer;
  
  circular_buffer         buffer;
  sc_core::sc_time        delta_time;
  bool                    active;

public:
  _command()
    : buffer()
    , delta_time(sc_core::SC_ZERO_TIME)
  {
    active = true;
  }

};

centralized_buffer::centralized_buffer
( sc_core::sc_module_name module_name,               // module name
  size_t nslots )
  : sc_module(module_name)
  , m_slots(nslots)
  , m_centralized_struct(new _command[nslots])
{
  for(unsigned int i=0; i<nslots; i++){
    std::ostringstream buf_name;
    buf_name << name() << "_buf" << i;
    m_centralized_struct[i].buffer.set_name(buf_name.str());
  }
}

centralized_buffer::~centralized_buffer()
{
  delete [] m_centralized_struct;
}

bool centralized_buffer::push
( size_t                    from,
  tlm::tlm_generic_payload &payload,
  tlm::tlm_phase           &phase,
  sc_core::sc_time         &time)
{
#if SOCLIB_MODULE_DEBUG
  std::cout << "[" << name() << "] PUSH [" << from <<"] " << std::endl;
#endif

  assert(!(time < m_centralized_struct[from].delta_time) && "PUSH transaction with the time smaller than the precedent");

  return m_centralized_struct[from].buffer.push(payload, phase, time);
}

    
bool centralized_buffer::pop
( size_t                    &from,
  tlm::tlm_generic_payload *&payload,
  tlm::tlm_phase           *&phase,
  sc_core::sc_time         *&time)
{
  bool ok = false;
  int min_idx = -1;
  uint64_t min_time = MAX_TIME;
  uint64_t time_value;
  
  for(unsigned int i=0; i<m_slots; i++){
    if(m_centralized_struct[i].active){
      if(m_centralized_struct[i].buffer.is_empty()){
	time = &m_centralized_struct[i].delta_time;
	time_value = (*time).value();
#if SOCLIB_MODULE_DEBUG
	std::cout << "[" << name() << "] MD FOR POP " << i << " IS EMPTY time = " << time_value << std::endl;
#endif
	if(time_value < min_time){
	  min_idx = i;
	  min_time = time_value;
	  ok = false;
	}
      }
      else{
	bool header = m_centralized_struct[i].buffer.get_front(payload, phase, time);
	assert(header);
	time_value = (*time).value();
	  
#if SOCLIB_MODULE_DEBUG
	std::cout << "[" << name() << "] MD FOR POP " << i << " NOT EMPTY time = " << time_value << std::endl;
#endif

	if(time_value < min_time || time_value == min_time){
	  min_idx = i;
	  min_time = time_value;
	  ok = true;
	}
      }
    }
  }

  from = min_idx;

  if(ok){
#if SOCLIB_MODULE_DEBUG
  std::cout << "[" << name() << "] POP from " << min_idx << std::endl;
#endif
    bool pop = m_centralized_struct[min_idx].buffer.pop(payload, phase, time);
    assert(pop);
  }
  else{
#if SOCLIB_MODULE_DEBUG
    std::cout << "[" << name() << "] NOT POP from " << min_idx << " IS EMPTY" << std::endl;
#endif
  }

  return ok;
}
   

circular_buffer centralized_buffer::get_buffer(int i)
{
  return m_centralized_struct[i].buffer;
}

const size_t centralized_buffer::get_nslots()
{
  return m_slots;
}

sc_core::sc_time centralized_buffer::get_delta_time(unsigned int index)
{
  return m_centralized_struct[index].delta_time;
}

void centralized_buffer::set_delta_time(unsigned int index, sc_core::sc_time t)
{
  m_centralized_struct[index].delta_time = t;
#if SOCLIB_MODULE_DEBUG
  std::cout << "[" << name() << "] DELTA_TIME[" << index <<"] = " << t.value() << std::endl;
#endif
}

void centralized_buffer::set_activity(unsigned int index, bool b)
{
  m_centralized_struct[index].active = b;
#if SOCLIB_MODULE_DEBUG
  std::cout << "[" << name() << "] ACTIVE[" << index <<"] = " << b << std::endl;
#endif
}

}}
