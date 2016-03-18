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


#ifndef BUS_LABSOC_HEADER
#define BUS_LABSOC_HEADER

//
// arbitration is done using a many to one multimap
//
// The key to the multimap is the priority. With each key we may in general
// have many sc_port<> * data elements.
//
// virtual get_next_request scans thru the masters, getting the highest 
// port with a request pending.
//
// run() calls get_next_request and forwards the request to the slave 
// and passes the response back to the appropriate master.
//
// This has the downside that we have to specify the number of master ports
//

#include <map>
#include <vector>
using std::multimap;
using std::pair;
using std::vector;

#include "tlm.h"
#include "event_labsoc.h"

using tlm::tlm_transport_if;
using tlm::tlm_slave_if;

using basic_protocol::basic_request;
using basic_protocol::basic_response;

using tlm::tlm_blocking_get_if;
using tlm::tlm_blocking_put_if;



class bus : public sc_module{
public:

// definition of the req and response
  typedef  basic_request< ADDRESS_TYPE , DATA_TYPE > REQ;
  typedef basic_response< DATA_TYPE > RSP;

//signal to tell if the bus is busy or not
  sc_signal<bool> bus_busy;
  
  int latency, evt_latency;
// definition of the array of ports to connect the masters (cpu interfaces) to the bus


  typedef sc_port< tlm_slave_if< REQ , RSP > , 1 > in_port;
  in_port *in_ports;

  typedef sc_port< tlm_transport_if< REQ , RSP > , 1 > out_port;
  out_port *out_ports;

  TMLIf **Ifs;

  //counters for the arrays of Ports
  int nbOfIfPorts, nbOfOutPorts;

  SC_HAS_PROCESS( bus );

//constructor of the module
  bus( sc_module_name module_name  ) :
    sc_module( module_name ) {
    nbOfIfPorts/*=nbOfOutPorts*/=0;
    latency=evt_latency=1;
    bus_busy=0;
    SC_THREAD( run );

  }


void setNbIf(int I){
in_ports= new in_port[I];
out_ports= new out_port[I];
Ifs=new TMLIf*[I];
nbOfOutPorts=I;
}


//add a cpu interface
void addIf(TMLIf * If, int priority=1){
    in_ports[nbOfIfPorts]( If->wire.slave_export ); 
    out_ports[nbOfIfPorts]( If->wire.target_export );
    
    add_in_interface( &in_ports[nbOfIfPorts] , priority );
    Ifs[nbOfIfPorts]=If;
    nbOfIfPorts++; 
    	
}

void setLatency(int lat){
latency=lat;
}

void setEvtLatency(int lat){
evt_latency=lat;
}
//add an interface (event or cpu) into the map where the arbiter lok at to find requests
void add_in_interface( in_port *port , const int priority ) {

    if_map.insert(  multimap_type::value_type( priority , port ) );
    

  }


protected:
//map where the TLM interfaces are stored
  typedef multimap< int , in_port * > multimap_type;
  multimap_type if_map;


  virtual void run() {

//port selected (the one which has been granted the access)
    in_port *tmp_in_port;
    //out_port *tmp_out_port;
    multimap_type::iterator i;
    REQ req;
    RSP rsp;

    for( ;; ) {
// find the next request
      if( (tmp_in_port = get_next_request( i , req ) ) ) {
// treating the request
	//cout << name() << " " << sc_time_stamp() << " sending req "   << endl;
	bus_busy=1;
//if the request has an adress, it is routed to the corresponding memory with the transport function
	if (req.a!=NULL){
	
	//cout << name() << " " << sc_time_stamp() << " req.a not empty "  << endl;
	rsp = out_ports[get_out_port(req)]->transport( req );
	
	//cout << name() << " " << sc_time_stamp() << " rsp given " << endl;
	//put the response in the requesting port
	(*tmp_in_port)->put( rsp );
	//cout << name() << " " << sc_time_stamp() << " rsp sent " << endl;
	
	}
	else /*the request does not have an adress, it is an event*/{
	
//create the response, wait and send in back
	rsp.status=basic_protocol::SUCCESS;
	rsp.d=req.d;

	wait(evt_latency, SC_NS);
	(*tmp_in_port)->put(rsp);
	

	} 

	bus_busy=0;
      }
//latency of the bus between two request
      wait( latency, SC_NS );

    }

  }

//the arbitration function
  virtual in_port *get_next_request( multimap_type::iterator &i , REQ &req ) {


    //
    // this starvation inducing algorithm always starts from the highest priority
    // you may want to do something else in a subclass
    //

    in_port *p;

    for( i = if_map.begin(); i != if_map.end(); ++i ) {
//       cout <<"debug " << "arb next req 1" << endl;
      p = (*i).second;
//    for (int k=0; k<B; k++){
      if( (*p)->nb_get( req )  ) {
// 	cout <<"debug " << "arb next req 2" << endl;
	cout << name() << " " << sc_time_stamp() << " found pending request on port " << p->name() << " priority " << (*i).first << "data ="<<req.d << endl;
	return p;

      }

      else
	cout << name() << " " << sc_time_stamp() << " no pending request on port " << p->name() << " priority " << (*i).first << endl;
// 	cout <<"debug " << "arb next req 3" << endl;
    
  }
    return NULL;
  }

//the routing to memory function
virtual int get_out_port(REQ req){
	
	for (int i=0; i<nbOfOutPorts; i++){
		//cout << name() << " " << sc_time_stamp() << " out port n° "<< i << " If " << Ifs[i] << "==" << req.a<<endl;
		if (req.a==Ifs[i]) return i;
		//cout << "not this one"<<endl;
		}
	return -1;
}

};


#endif
