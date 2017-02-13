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
 * Maintainers: fpecheux, alain, aline
 *
 * Copyright (c) UPMC / Lip6, 2008
 *     François Pêcheux <francois.pecheux@lip6.fr>
 *     Aline Vieira de Mello <aline.vieira-de-mello@lip6.fr>
 *     Alain Greiner <alain.greiner@lip6.fr>
 */

#ifndef __INTERCONNECT_H__ 
#define __INTERCONNECT_H__

#include <tlmdt>	                                        // TLM-DT headers
#include "mapping_table.h"                                      // mapping table
#include "centralized_buffer.h"                                 // centralized buffer

namespace soclib { namespace tlmdt {

class Interconnect                                              // Interconnect
  : public sc_core::sc_module           	                // inherit from SC module base clase
{
private:
 
  typedef soclib::common::AddressDecodingTable<uint32_t, int>  routing_table_t;     
  typedef soclib::common::AddressDecodingTable<uint32_t, bool> locality_table_t;   
  typedef soclib::common::AddressMaskingTable<uint32_t>        resp_routing_table_t; 
  typedef soclib::common::AddressDecodingTable<uint32_t, bool> resp_locality_table_t;

  /////////////////////////////////////////////////////////////////////////////////////
  // Member Variables
  /////////////////////////////////////////////////////////////////////////////////////
  int 				m_id;                 	// identifier
  int 				m_inits;              	// number of initiiators
  int 				m_targets;            	// number of targets
  size_t 			m_delay;              	// interconnect delay
  size_t 			m_local_delta_time;	// minimal time between send & response LOCAL
  size_t  			m_no_local_delta_time;  // minimal time between send & response NOT LOCAL
  bool                          m_is_local_crossbar;  	// true if the module is a loca interconnect

  centralized_buffer 		m_centralized_buffer; 	// centralized buffer
  const routing_table_t         m_routing_table;      	// routing table
  const locality_table_t        m_locality_table;     	// locality table
  const resp_routing_table_t    m_resp_routing_table; 	// response routing table
  const resp_locality_table_t   m_resp_locality_table;	// response locality table
  pdes_local_time*              m_pdes_local_time;    	// local time

  // instrumentation counters
  size_t                        m_msg_count;
  size_t                        m_local_msg_count;
  size_t                        m_non_local_msg_count;
  size_t                        m_token_msg_count;
  size_t                        m_wait_count;
  size_t                        m_notify_count;
  size_t                        m_pop_count;

  // FIELDS OF TOKEN TRANSACTION
  tlm::tlm_generic_payload      m_payload_token;
  tlm::tlm_phase                m_phase_token;
  sc_core::sc_time              m_time_token;
  soclib_payload_extension      m_extension_token;

  // FIELDS OF NULL TRANSACTION
  tlm::tlm_generic_payload      m_null_payload;
  tlm::tlm_phase                m_null_phase;
  sc_core::sc_time              m_null_time;
  soclib_payload_extension      m_null_extension;

  /////////////////////////////////////////////////////////////////////////////////////
  // Functions
  /////////////////////////////////////////////////////////////////////////////////////
  void init();

  void behavior(void);

  void routing
  ( size_t                   from,      // port source
    tlm::tlm_generic_payload &payload,   // payload
    tlm::tlm_phase           &phase,     // phase
    sc_core::sc_time         &time);     // time

  void create_token();

  /////////////////////////////////////////////////////////////////////////////////////
  // Fuction  tlm::tlm_fw_transport_if (VCI TARGET SOCKET)
  /////////////////////////////////////////////////////////////////////////////////////
  tlm::tlm_sync_enum nb_transport_fw     // receive command from initiator
  ( int                       id,        // socket id
    tlm::tlm_generic_payload &payload,   // payload
    tlm::tlm_phase           &phase,     // phase
    sc_core::sc_time         &time);     // time
 
  /////////////////////////////////////////////////////////////////////////////////////
  // Virtual Fuctions  tlm::tlm_bw_transport_if (VCI INITIATOR SOCKET)
  /////////////////////////////////////////////////////////////////////////////////////
  tlm::tlm_sync_enum nb_transport_bw     // receive answer from target
  ( int                       id,        // socket id
    tlm::tlm_generic_payload &payload,   // payload
    tlm::tlm_phase           &phase,     // phase
    sc_core::sc_time         &time);     // time

 protected:
  SC_HAS_PROCESS(Interconnect);
public:  

  std::vector<tlm_utils::simple_target_socket_tagged<Interconnect,32,tlm::tlm_base_protocol_types> *> p_to_initiator;
  std::vector<tlm_utils::simple_initiator_socket_tagged<Interconnect,32,tlm::tlm_base_protocol_types> *> p_to_target;

  Interconnect(                                                // constructor
	       sc_core::sc_module_name module_name             // SC module name
	       , int id                                        // identifier
	       , const routing_table_t &rt                     // routing table
	       , const resp_routing_table_t &rrt               // response routing table
	       , size_t n_inits                                // number of inits
	       , size_t n_targets                              // number of targets
	       , size_t delay);                                // interconnect delay

  Interconnect(                                                // constructor
	       sc_core::sc_module_name module_name             // SC module name
	       , const routing_table_t &rt                     // routing table
	       , const resp_routing_table_t &rrt               // response routing table
	       , size_t n_inits                                // number of inits
	       , size_t n_targets                              // number of targets
	       , size_t delay);                                // interconnect delay
  
  Interconnect(                                                // constructor
	       sc_core::sc_module_name module_name             // SC module name
	       , int id                                        // identifier
	       , const routing_table_t &rt                     // routing table
	       , const locality_table_t &lt                    // locality table
	       , const resp_routing_table_t &rrt               // response routing table
	       , const resp_locality_table_t &rlt              // response locality table
	       , size_t n_inits                                // number of inits
	       , size_t n_targets                              // number of targets
	       , size_t delay);                                // interconnect delay

  Interconnect(                                                // constructor
	       sc_core::sc_module_name module_name             // SC module name
	       , const routing_table_t &rt                     // routing table
	       , const locality_table_t &lt                    // locality table
	       , const resp_routing_table_t &rrt               // response routing table
	       , const resp_locality_table_t &rlt              // response locality table
	       , size_t n_inits                                // number of inits
	       , size_t n_targets                              // number of targets
	       , size_t delay);                                // interconnect delay

  ~Interconnect();

  uint32_t getLocalMsgCounter();

  uint32_t getNonLocalMsgCounter();

  uint32_t getTokenMsgCounter();

  void print();

};

}}

#endif /* __INTERCONNECT__ */
