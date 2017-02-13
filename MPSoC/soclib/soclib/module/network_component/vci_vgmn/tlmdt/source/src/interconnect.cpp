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

#include "interconnect.h"                           // our header
                    
//#define SOCLIB_MODULE_DEBUG 1

namespace soclib { namespace tlmdt {

#define tmpl(x) x Interconnect

/////////////////////////////////////////////////////////////////////////////////////
// Constructor
/////////////////////////////////////////////////////////////////////////////////////
tmpl(/**/)::Interconnect
( sc_core::sc_module_name module_name               // module name
  , int id                                          // identifier
  , const routing_table_t &rt                       // routing table
  , const locality_table_t &lt                      // locality table
  , const resp_routing_table_t &rrt                 // response routing table
  , const resp_locality_table_t &rlt                // response locality table
  , size_t n_inits                                  // number of inits
  , size_t n_targets                                // number of targets
  , size_t delay                                    // interconnect delay
  ) 
  : sc_module(module_name)
  , m_id(id)
  , m_inits(n_inits)
  , m_targets(n_targets)
  , m_delay(delay)
  , m_is_local_crossbar(true)
  , m_centralized_buffer("centralized_buffer", n_inits)
  , m_routing_table(rt)
  , m_locality_table(lt)
  , m_resp_routing_table(rrt)
  , m_resp_locality_table(rlt)
  , m_msg_count(0)
  , m_local_msg_count(0)
  , m_non_local_msg_count(0)
  , m_token_msg_count(0)
{
  init();
}

tmpl(/**/)::Interconnect
( sc_core::sc_module_name module_name               // module name
  , const routing_table_t &rt                       // routing table
  , const locality_table_t &lt                      // locality table
  , const resp_routing_table_t &rrt                 // response routing table
  , const resp_locality_table_t &rlt                // response locality table
  , size_t n_inits                                  // number of inits
  , size_t n_targets                                // number of targets
  , size_t delay                                    // interconnect delay
  ) 
  : sc_module(module_name)
  , m_id(0)
  , m_inits(n_inits)
  , m_targets(n_targets)
  , m_delay(delay)
  , m_is_local_crossbar(true)
  , m_centralized_buffer("centralized_buffer", n_inits)
  , m_routing_table(rt)
  , m_locality_table(lt)
  , m_resp_routing_table(rrt)
  , m_resp_locality_table(rlt)
  , m_msg_count(0)
  , m_local_msg_count(0)
  , m_non_local_msg_count(0)
  , m_token_msg_count(0)
{
  init();
}

tmpl(/**/)::Interconnect
( sc_core::sc_module_name module_name               // module name
  , int id                                          // identifier
  , const routing_table_t &rt                       // routing table
  , const resp_routing_table_t &rrt                 // response routing table
  , size_t n_inits                                  // number of inits
  , size_t n_targets                                // number of targets
  , size_t delay                                    // interconnect delay
  ) 
  : sc_module(module_name)
  , m_id(id)
  , m_inits(n_inits)
  , m_targets(n_targets)
  , m_delay(delay)
  , m_is_local_crossbar(false)
  , m_centralized_buffer("centralized_buffer", n_inits)
  , m_routing_table(rt)
  , m_resp_routing_table(rrt)
  , m_msg_count(0)
  , m_local_msg_count(0)
  , m_non_local_msg_count(0)
  , m_token_msg_count(0)
{
  init();
}

tmpl(/**/)::Interconnect
( sc_core::sc_module_name module_name               // module name
  , const routing_table_t &rt                       // routing table
  , const resp_routing_table_t &rrt                 // response routing table
  , size_t n_inits                                  // number of inits
  , size_t n_targets                                // number of targets
  , size_t delay                                    // interconnect delay
  ) 
  : sc_module(module_name)
  , m_id(0)
  , m_inits(n_inits)
  , m_targets(n_targets)
  , m_delay(delay)
  , m_is_local_crossbar(false)
  , m_centralized_buffer("centralized_buffer", n_inits)
  , m_routing_table(rt)
  , m_resp_routing_table(rrt)
  , m_msg_count(0)
  , m_local_msg_count(0)
  , m_non_local_msg_count(0)
  , m_token_msg_count(0)
{
  init();
}

tmpl(/**/)::~Interconnect(){
}

///////////////////
tmpl(void)::init()
{
    // bind VCI TARGET SOCKETS
    for(int i=0;i<m_inits;i++)
    {
        std::ostringstream target_name;
        target_name << "target" << i;
        p_to_initiator.push_back(new tlm_utils::simple_target_socket_tagged
           <Interconnect,32,tlm::tlm_base_protocol_types>(target_name.str().c_str()));
        p_to_initiator[i]->register_nb_transport_fw(this, &Interconnect::nb_transport_fw, i);
    }

    // bind VCI INITIATOR SOCKETS
    for(int i=0;i<m_targets;i++)
    {
        std::ostringstream init_name;
        init_name << "init" << i;
        p_to_target.push_back(new tlm_utils::simple_initiator_socket_tagged
           <Interconnect,32,tlm::tlm_base_protocol_types>(init_name.str().c_str()));
        p_to_target[i]->register_nb_transport_bw(this, &Interconnect::nb_transport_bw, i);
    }

    // minimal local latency
    m_local_delta_time = 2*m_delay;

    // minimal non local delay
    m_no_local_delta_time = 4*m_delay;

    // PDES local time
    m_pdes_local_time = new pdes_local_time(100*UNIT_TIME);

    if(m_is_local_crossbar)
    {
        //create token at the beginning of simulation
        create_token();

        // initialises payload, phase and extension for null messages
        // generated by a local interconnect
        m_null_payload.set_extension(&m_null_extension);
        m_null_extension.set_null_message();
        m_null_phase = tlm::BEGIN_REQ;
    }

    // register thread process
    SC_THREAD(behavior);                  
}

/////////////////////////////////////////////////////////////////////////////////////
// Functions
/////////////////////////////////////////////////////////////////////////////////////
tmpl(uint32_t)::getLocalMsgCounter(){
  return m_local_msg_count;
}

tmpl(uint32_t)::getNonLocalMsgCounter(){
  return m_non_local_msg_count;
}

tmpl(uint32_t)::getTokenMsgCounter(){
  return m_token_msg_count;
}

tmpl(void)::print(){
  uint32_t local_msg_count     = getLocalMsgCounter();
  uint32_t non_local_msg_count = getNonLocalMsgCounter();
  uint32_t token_msg_count     = getTokenMsgCounter();
  uint32_t total_count         = local_msg_count + non_local_msg_count + token_msg_count;
 
  std::cout << "[" << name() << "] Total messages       = " << total_count << std::endl;
  std::cout << "Total of Local Messages     = " << local_msg_count << " " << (local_msg_count*100)/total_count << "%" << std::endl;
  std::cout << "Total of Non Local Messages = " << non_local_msg_count << " " << (non_local_msg_count*100)/total_count << "%" << std::endl;
  std::cout << "Total of Token Messages     = " << token_msg_count << " " << (token_msg_count*100)/total_count << "%" << std::endl;

}

///////////////////////////////////////////////////////
tmpl(void)::routing( size_t                       from,      
                     tlm::tlm_generic_payload    &payload, 
                     tlm::tlm_phase              &phase,  
                     sc_core::sc_time            &time)  
{
    bool 	send;
    int 	dest;

    // get payload extension
    soclib_payload_extension *extension_ptr;
    payload.get_extension(extension_ptr);

    ///////////////////////////////////////////////////////////////
    // if transaction command is activation/deactivation
    // the source is actived or desactived and no transaction is sent
    if(extension_ptr->is_active() || extension_ptr->is_inactive())
    {

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d  HANDLING ACTIVITY MSG from = %d\n", 
       name(), (int)time.value(), (int)from);
#endif

        send = false;
        m_centralized_buffer.set_activity(from, extension_ptr->is_active());
    }

    /////////////////////////////////////////////////////////////////////////
    // if transaction command is a token, it must be sent to the target[from]
    else if(extension_ptr->is_token_message())
    {

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d  HANDLING TOKEN MSG from = %d\n", 
       name(), (int)time.value(), (int)from);
#endif

        // set the delta_time which this init wont send another message
        m_centralized_buffer.set_delta_time(from, time);

        send = true;

        m_msg_count++;
        m_token_msg_count++;

        if ( m_is_local_crossbar )
        {
            dest = m_targets - 1;
            extension_ptr->set_pkt_id(extension_ptr->get_pkt_id()+1);
        }
        else
        {
            dest = from;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    // if transaction command is a null message, the response is sent to the initiator
    // to synchronize it, but this null message is not transmited
    else if(extension_ptr->is_null_message())
    {

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d  HANDLING NULL MSG from = %d\n", 
       name(), (int)time.value(), (int)from);
#endif

        // set the delta_time which this init wont send another message
        m_centralized_buffer.set_delta_time(from, time);

        // send the response
        (*p_to_initiator[from])->nb_transport_bw(payload, phase, time);

        send = false;
    }

    ///////////////////////////////////////////////////////////////////////////////
    // if transaction is a VCI command, it must be sent to appropriated target
    // no response is sent to the initiator
    else
    {
        send = true;
        if(m_is_local_crossbar)  // local interconnect
        {
            if (!m_locality_table[payload.get_address()]) // non local target
            {
	        if(from == m_centralized_buffer.get_nslots()-1)
                {
	            // set the delta_time which this init wont send another message
	            m_centralized_buffer.set_delta_time(from, time);
	        }
	        else
                {
	            // set the delta_time which this init wont send another message
	            m_centralized_buffer.set_delta_time(from, time + (m_no_local_delta_time*UNIT_TIME));
	        }
	
	        m_msg_count++;
	        m_non_local_msg_count++;
	        dest = m_targets - 1;
            }
            else  // local target
            {
	        if(from == m_centralized_buffer.get_nslots()-1)
                {
	            //set the delta_time which this init wont send another message
	            m_centralized_buffer.set_delta_time(from, time);
	        }
	        else
                {
	            //set the delta_time which this init wont send another message
	            m_centralized_buffer.set_delta_time(from, time + (m_local_delta_time*UNIT_TIME));
	        }
	
	        m_msg_count++;
	        m_local_msg_count++;
	        dest = m_routing_table[payload.get_address()];
	        assert( dest >= 0 && dest < m_targets );
            }
        }
        else  // global interconnect
        {
            // set the delta_time which this init wont send another message
            m_centralized_buffer.set_delta_time(from, time);
            dest = m_routing_table[payload.get_address()];
            assert( dest >= 0 && dest < m_targets );
	
            m_msg_count++;
            m_local_msg_count++;
        }

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d  ROUTING VCI MSG from = %d to %d\n", 
       name(), (int)time.value(), (int)from, (int)dest);
#endif
	
    }

    if (send)  // transmit the command to the selected target
    {
        time = time + (m_delay*UNIT_TIME);
        (*p_to_target[dest])->nb_transport_fw(payload, phase, time);
    }
}  // end routing()
  
//////////////////////////
tmpl(void)::create_token()
{
  // create token message in beginning of simulation
  m_extension_token.set_token_message();
  m_extension_token.set_src_id(m_id);
  m_extension_token.set_pkt_id(0);
  m_payload_token.set_extension(&m_extension_token);
  m_phase_token = tlm::BEGIN_REQ;
  m_time_token = UNIT_TIME;

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] send Token time = %d\n", name(), (int)m_time_token.value());
#endif

  //push a token in the centralized buffer
  m_centralized_buffer.push(m_inits-1, m_payload_token, m_phase_token, m_time_token);
#ifdef SOCLIB_MODULE_DEBUG
  printf("[%s] send Token time = %d\n", name(), (int)m_time_token.value());
#endif
}

/////////////////////////////////////////////////////////////////////////////////////
//  consumer thread 
/////////////////////////////////////////////////////////////////////////////////////
tmpl(void)::behavior()  
{
    size_t                    	from;
    tlm::tlm_generic_payload* 	payload_ptr;
    tlm::tlm_phase*           	phase_ptr;
    sc_core::sc_time*         	time_ptr;

    while (true)
    {

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] WHILE CONSUMER\n", name());
#endif

        // pop the earliest transaction from centralized buffer
        while(m_centralized_buffer.pop(from, payload_ptr, phase_ptr, time_ptr))
        {
            m_pop_count++;

            assert(!(*time_ptr < m_pdes_local_time->get()) 
                   && "Transaction time must not be smaller than the interconnect time");
 
            // update local time
            m_pdes_local_time->set(*time_ptr);

            // process the transaction
            routing(from, *payload_ptr, *phase_ptr, *time_ptr);
        } // end while buffer not empty
    
        // send periodically NULL messages to all local targets 
        // if it is a local interconnect
        if ( m_is_local_crossbar && m_pdes_local_time->need_sync() )
        {
            m_pdes_local_time->reset_sync();
            m_null_time = m_pdes_local_time->get();
            for ( int i=0 ; i<(m_targets-1) ; i++ )
                (*p_to_target[i])->nb_transport_fw(m_null_payload, 
                                                   m_null_phase, 
                                                   m_null_time);
        }

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] CONSUMER WAITING id = %d\n", name(), (int)from);
#endif

        sc_core::wait(sc_core::SC_ZERO_TIME);

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] CONSUMER WAKE-UP\n", name());
#endif

    } // end while thread
}
    
/////////////////////////////////////////////////////////////////////////////////////
// Interface function executed when receiving a command on an initiator port
// It registers the command in the central buffer
/////////////////////////////////////////////////////////////////////////////////////
tmpl(tlm::tlm_sync_enum)::nb_transport_fw   (int                         id,          
                                             tlm::tlm_generic_payload    &payload,    
                                             tlm::tlm_phase              &phase,     
                                             sc_core::sc_time            &time)     
{
    bool push = false;
    int try_push = 0;
    do
    {

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d RECEIVE a COMMAND on port %d\n", name(), (int)time.value(), id);
#endif

        //push a transaction in the centralized buffer
        push = m_centralized_buffer.push(id, payload, phase, time);

        if(!push)
        {
            try_push++;

#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] PRODUCER id = %d <<<<<<<<< NOT PUSH >>>>>>>> try_push = %d \n",name(),id, try_push);
#endif

            sc_core::wait(sc_core::SC_ZERO_TIME);
        }
    } while (!push);

    return  tlm::TLM_COMPLETED;

} //end nb_transport_fw

/////////////////////////////////////////////////////////////////////////////////////
// Interface function executed when receiving a response on a target port
// It directly routes the response to the proper initiator
/////////////////////////////////////////////////////////////////////////////////////
tmpl(tlm::tlm_sync_enum)::nb_transport_bw ( int                        id,   
                                            tlm::tlm_generic_payload   &payload,  
                                            tlm::tlm_phase             &phase,   
                                            sc_core::sc_time           &time)  
{
    unsigned int	srcid;
    unsigned int	dest;

    // get message SRCID
    soclib_payload_extension *resp_extension_ptr;
    payload.get_extension(resp_extension_ptr);
    srcid = resp_extension_ptr->get_src_id();
  
#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d  RECEIVE RESPONSE on port %d / srcid = %d\n", 
        name(), (int)time.value(), id, srcid);
#endif

    if(m_is_local_crossbar)
    {
        if (!m_resp_locality_table[srcid])  	dest = m_inits - 1;
        else  					dest = m_resp_routing_table[srcid]; 
    }
    else	// global interconnect
    {
    						dest = m_resp_routing_table[srcid];
    }

    // update the transaction time
    time = time + (m_delay*UNIT_TIME);
  
#ifdef SOCLIB_MODULE_DEBUG
printf("[%s] time = %d  SEND RESPONSE on port %d / srcid = %d\n", 
        name(), (int)time.value(), dest, srcid);
#endif

    (*p_to_initiator[dest])->nb_transport_bw(payload, phase, time);
    return tlm::TLM_COMPLETED;
} // end nb_transport_bw 

}}
