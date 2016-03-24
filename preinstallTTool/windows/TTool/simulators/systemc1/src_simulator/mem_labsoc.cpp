
/*****************************************************************************

  The following code is derived, directly or indirectly, from the SystemC
  source code Copyright (c) 1996-2004 by all Contributors.
  All Rights reserved.

  The contents of this file are subject to the restrictions and limitations
  set forth in the SystemC Open Source License Version 2.4 (the "License");
  You may not use this file except in compliance with such restrictions and
  limitations. You may obtain instructions on how to receive a copy of the
  License at http://www.systemc.org/. Software distributed by Contributors
  under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
  ANY KIND, either express or implied. See the License for the specific
  language governing rights and limitations under the License.

 *****************************************************************************/


#include "mem_labsoc.h"

#include "systemc.h"
#include "interface_labsoc.h"
#include "bus_types.h"
#include "basic_protocol.h"
#include "basic_slave_base.h"

using basic_protocol::basic_status;
using basic_protocol::basic_slave_base;
using tlm::tlm_transport_if;

using basic_protocol::basic_request;
using basic_protocol::basic_response;

using basic_protocol::basic_status;

memory::memory()
{
  wr_mem=0;
  rd_mem=0;
  latency=3;
}


void memory::go(){
  REQ req;
  RSP rsp;

  for (;;){
    cout << sc_time_stamp() << " memory looking for requests " << endl;
    req=If->in_port->get();
    cout << sc_time_stamp() << " memory found the request " <<req.d <<" on " << req.a << endl;
   // if (req.d>0){
    
	    cout << sc_time_stamp() << " memory treating the  request " << endl;  
      switch( req.type ) {
	      case basic_protocol::READ :
          rsp.status = this->read( req.a , rsp.d );
          break; 
	      case basic_protocol::WRITE:
          rsp.status = write( req.a , req.d );
          break;
        default :
		rsp.status = basic_protocol::ERROR;
        break; 

        }
	cout << sc_time_stamp() << " memory sending back the request " << endl;
    If->out_port->put(rsp);

    //}
  // wait(1, SC_NS);
  }
}


basic_status memory::write( const ADDRESS_TYPE &a , const DATA_TYPE &d )
{
  wr_mem=1;
  cout << " writing at " << a << " value " << d << endl; 
  //latency of the memory 
  wait(latency, SC_NS);
  wr_mem=0;
  return basic_protocol::SUCCESS;
}

basic_status memory::read( const ADDRESS_TYPE &a , DATA_TYPE &d )
{

  rd_mem=1;
  cout  << " reading from " << a << " value " << "we do not care" << endl;
  //latency of the memory 
  wait(latency, SC_NS);
  rd_mem=0;
return basic_protocol::SUCCESS;
}
 
memory::~memory() {

}
