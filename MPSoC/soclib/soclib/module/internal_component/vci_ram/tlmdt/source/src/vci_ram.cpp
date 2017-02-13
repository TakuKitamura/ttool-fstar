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

#include "vci_ram.h"

namespace soclib { namespace tlmdt {

#define tmpl(x) template<typename vci_param> x VciRam<vci_param>

//////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////
tmpl(/**/)::VciRam
( sc_core::sc_module_name name,
  const soclib::common::IntTab &index,
  const soclib::common::MappingTable &mt,
  soclib::common::Loader &loader)
  : sc_module(name),
    m_mt(mt),
    m_loader(new soclib::common::Loader(loader)),
    m_atomic(4096), 	//  maximal number of initiator
    p_vci("socket")
{
  // bind target
  p_vci(*this);                     

  //PDES local time
  m_pdes_local_time = new pdes_local_time(sc_core::SC_ZERO_TIME);

  // segments
  m_segments = m_mt.getSegmentList(index);
  m_contents = new ram_t*[m_segments.size()];
  size_t word_size = sizeof(typename vci_param::data_t);
  std::list<soclib::common::Segment>::iterator seg;
  size_t i;
  for (i=0, seg = m_segments.begin(); seg != m_segments.end(); ++i, ++seg ) {
    soclib::common::Segment &s = *seg;
    m_contents[i] = new ram_t[(s.size()+word_size-1)/word_size];
    memset(m_contents[i], 0, s.size());
  }
  
  if ( m_loader ){
    for (i=0, seg = m_segments.begin(); seg != m_segments.end(); ++i, ++seg ) {
      soclib::common::Segment &s = *seg;
      m_loader->load(&m_contents[i][0], s.baseAddress(), s.size());
      for (size_t addr = 0; addr < s.size()/word_size; ++addr )
	m_contents[i][addr] = le_to_machine(m_contents[i][addr]);
    }
  }
  
  //initialize the control table LL/SC
  m_atomic.clearAll();

  //counters
  m_cpt_read = 0;
  m_cpt_write = 0;
}

tmpl(/**/)::~VciRam(){}

tmpl(void)::print_stats(){
  std::cout << name() << std::endl;
  std::cout << "- READ               = " << m_cpt_read << std::endl;
  std::cout << "- WRITE              = " << m_cpt_write << std::endl;
}

/////////////////////////////////////////////////////////////////////////////////////
// Virtual Fuctions  tlm::tlm_fw_transport_if VCI SOCKET
/////////////////////////////////////////////////////////////////////////////////////
tmpl(tlm::tlm_sync_enum)::nb_transport_fw
( tlm::tlm_generic_payload &payload,
  tlm::tlm_phase           &phase,  
  sc_core::sc_time         &time)   
{
  soclib_payload_extension *extension_pointer;
  payload.get_extension(extension_pointer);

  //this target does not treat the null message
  if(extension_pointer->is_null_message()){
    return tlm::TLM_COMPLETED;
  }

  //update the message time
  if ( time < m_pdes_local_time->get() ){
    time = m_pdes_local_time->get();
  }

  // First, find the right segment using the first address of the packet
  std::list<soclib::common::Segment>::iterator seg;	
  size_t segIndex;

  uint32_t nwords = (uint32_t)(payload.get_data_length() / vci_param::nbytes);
  uint32_t srcid  = extension_pointer->get_src_id();
#ifdef SOCLIB_MODULE_DEBUG
  uint32_t pktid  = extension_pointer->get_pkt_id();
#endif

  for (segIndex=0,seg = m_segments.begin(); seg != m_segments.end(); ++segIndex, ++seg ) {
    soclib::common::Segment &s = *seg;
    if (!s.contains(payload.get_address()))
      continue;
    
    switch(extension_pointer->get_command()){
    case VCI_READ_COMMAND:
      {
	typename vci_param::addr_t address;
	for (size_t i=0;i<nwords;i++){
	  //if (payload.contig) 
	  address = (payload.get_address()+(i*vci_param::nbytes)) - s.baseAddress(); //XXX contig = TRUE always
	  //else
	  //address = payload.get_address() - s.baseAddress(); //always the same address

	  utoa(m_contents[segIndex][address / vci_param::nbytes], payload.get_data_ptr(),(i * vci_param::nbytes));

#ifdef SOCLIB_MODULE_DEBUG
	  printf("[%s] time %d read %d address = 0x%09x data = 0x%09x\n", name(), (int)time.value(), (int)m_cpt_read, (int)address, (int)m_contents[segIndex][address / vci_param::nbytes]);
#endif

	  m_cpt_read++;
	  time = time + UNIT_TIME;
	}
	
	payload.set_response_status(tlm::TLM_OK_RESPONSE);
      }
      break;
    case VCI_WRITE_COMMAND:
      {
	typename vci_param::addr_t address;
	for (size_t i=0; i<nwords; i++){
	  //if(payload.contig)
	  address = (payload.get_address()+(i*vci_param::nbytes)) - s.baseAddress();//XXX contig = TRUE always
	  //else
	  //address = payload.get_address() - s.baseAddress();
	  
	  m_atomic.accessDone(address);
	  
	  uint32_t index   = address / vci_param::nbytes;
	  ram_t *tab       = m_contents[segIndex];
	  unsigned int cur = tab[index];
	  uint32_t mask    = atou(payload.get_byte_enable_ptr(), (i * vci_param::nbytes));
	  
	  tab[index] = (cur & ~mask) | (atou( payload.get_data_ptr(), (i * vci_param::nbytes) ) & mask);

#ifdef SOCLIB_MODULE_DEBUG
	  if(tab[index]==0)
	    printf("[%s] time %d write %d address = 0x%09x data = 0x%09x tab[%d] = %x\n",name(), (int)time.value(), (int)m_cpt_write, (int)address, (int)(atou( payload.get_data_ptr(), (i * vci_param::nbytes))), (int)index, (int)tab[index]);
	  else
	    printf("[%s] time %d write %d address = 0x%09x data = 0x%09x tab[%d] = 0x%x\n",name(), (int)time.value(), (int)m_cpt_write, (int)address, (int)(atou( payload.get_data_ptr(), (i * vci_param::nbytes))), (int)index, (int)tab[index]);
#endif
	    
	  m_cpt_write++;
	  time = time + UNIT_TIME;
	}
	
	payload.set_response_status(tlm::TLM_OK_RESPONSE);
      }
      break;
    case VCI_LINKED_READ_COMMAND:
      {
        m_cpt_read+=nwords;

	typename vci_param::addr_t address;
        for (size_t i=0; i<nwords; i++){
          //if(payload.contig)
          address = (payload.get_address()+(i*vci_param::nbytes)) - s.baseAddress();//XXX contig = TRUE always
          //else
          //address = payload.get_address() - s.baseAddress();

          utoa(m_contents[segIndex][address / vci_param::nbytes], payload.get_data_ptr(),(i * vci_param::nbytes));

#ifdef SOCLIB_MODULE_DEBUG
	  printf("[%s] time %d read %d address = 0x%09x data = 0x%09x\n", name(), (int)time.value(), (int)m_cpt_read, (int)address, (int)(m_contents[segIndex][address / vci_param::nbytes]));
#endif
	  
          m_atomic.doLoadLinked(address, srcid);
	  m_cpt_read++;
	  time = time + UNIT_TIME;
        }

        payload.set_response_status(tlm::TLM_OK_RESPONSE);
      }
      break;
    case VCI_STORE_COND_COMMAND:
      {
	typename vci_param::addr_t address;
        for (size_t i=0; i<nwords; i++){
          //if(payload.contig)
          address = (payload.get_address()+(i*vci_param::nbytes)) - s.baseAddress();//XXX contig = TRUE always
          //else
          //address = payload.get_address() - s.baseAddress();

          if(m_atomic.isAtomic(address,srcid)){
            m_atomic.accessDone(address);

            uint32_t index   = address / vci_param::nbytes;
            ram_t *tab       = m_contents[segIndex];
            unsigned int cur = tab[index];
	    uint32_t mask    = atou(payload.get_byte_enable_ptr(), (i * vci_param::nbytes));

	    tab[index] = (cur & ~mask) | (atou(payload.get_data_ptr(), (i * vci_param::nbytes)) & mask);

#ifdef SOCLIB_MODULE_DEBUG
	    if(tab[index]==0)
	      printf("[%s] time %d write %d address = 0x%09x data = 0x%09x tab[%d] = %x\n",name(), (int)time.value(), (int)m_cpt_write, (int)address, (int)(atou( payload.get_data_ptr(), (i * vci_param::nbytes))), (int)index, (int)tab[index]);
	    else
	      printf("[%s] time %d write %d address = 0x%09x data = 0x%09x tab[%d] = 0x%x\n",name(), (int)time.value(), (int)m_cpt_write, (int)address, (int)(atou( payload.get_data_ptr(), (i * vci_param::nbytes))), (int)index, (int)tab[index]);
#endif

	    utoa(0, payload.get_data_ptr(),(i * vci_param::nbytes));
          }
          else{
            utoa(1, payload.get_data_ptr(),(i * vci_param::nbytes));
          }
	  m_cpt_write++;
	  time = time + UNIT_TIME;
        }

        payload.set_response_status(tlm::TLM_OK_RESPONSE);
      }
      break;
    default:
      //send error message
      payload.set_response_status(tlm::TLM_COMMAND_ERROR_RESPONSE);
      
#ifdef SOCLIB_MODULE_DEBUG
      std::cout << "[" << name() << "] Command is not supported" << std::endl;
#endif

      break;
    }

    m_pdes_local_time->set(time + UNIT_TIME);
  
    phase = tlm::BEGIN_RESP;

    p_vci->nb_transport_bw(payload, phase, time);
    return tlm::TLM_COMPLETED;


  }//end switch
  
  //send error message
  payload.set_response_status(tlm::TLM_ADDRESS_ERROR_RESPONSE);
  time = time + (nwords * UNIT_TIME);
  m_pdes_local_time->set(time + UNIT_TIME);
  phase = tlm::BEGIN_RESP;
  
#ifdef SOCLIB_MODULE_DEBUG
  std::cout << "[" << name() << "] Address " << std::hex << payload.get_address() << std::dec << " does not match any segment " << std::endl;
  std::cout << "[" << name() << "] Send to source "<< srcid << " a error packet with time = "  << time.value() << std::endl;
#endif
  p_vci->nb_transport_bw(payload, phase, time);
  return tlm::TLM_COMPLETED;
}

// Not implemented for this example but required by interface
tmpl(void)::b_transport
( tlm::tlm_generic_payload &payload,                // payload
  sc_core::sc_time         &_time)                  //time
{
  return;
}

// Not implemented for this example but required by interface
tmpl(bool)::get_direct_mem_ptr
( tlm::tlm_generic_payload &payload,                // address + extensions
  tlm::tlm_dmi             &dmi_data)               // DMI data
{ 
  return false;
}
    
// Not implemented for this example but required by interface
tmpl(unsigned int):: transport_dbg                            
( tlm::tlm_generic_payload &payload)                // debug payload
{
  return false;
}

}}
