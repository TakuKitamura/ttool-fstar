#include "systemc.h"

#ifndef INTERFACE_LABSOC__H
#define INTERFACE_LABSOC__H


class Node;
class TMLChannel;
class TMLEvent;
#include "node_labsoc.h"
#include "channel_labsoc.h"
#include "event_labsoc.h"
#include "tlm.h"
#include "bus_types.h"
#include "basic_initiator_port.h"
#include "basic_protocol.h"


using basic_protocol::basic_initiator_port;

using basic_protocol::basic_request;
using basic_protocol::basic_response;

using tlm::tlm_transport_channel;

using tlm::tlm_blocking_get_if;
using tlm::tlm_blocking_put_if;

// max buffer size in words of width-bit
#define MAX_BUFFER_SIZE 1024




class TMLIf{
	public :
	
	//a channel (in the TLM sense, more like a wire) to connect the interface to the bus
	typedef tlm_transport_channel< basic_request< ADDRESS_TYPE , DATA_TYPE > , basic_response< DATA_TYPE > > arb_channel_type;
	arb_channel_type wire;
		
	virtual ~TMLIf() {}
};



/**Memory interface*/
class TMLmemIf: public TMLIf,  sc_module  {
public:

  typedef sc_port<  tlm_blocking_get_if < basic_request< ADDRESS_TYPE , DATA_TYPE > > > In_port;
  In_port in_port;
  
  typedef sc_port< tlm_blocking_put_if < basic_response< DATA_TYPE > > > Out_port;
  Out_port out_port;
  
  TMLmemIf(sc_module_name module_name):  sc_module( module_name ) , 
  in_port("in_port"), out_port("out_port"){
    //plug the wire into the port
    in_port( wire.get_request_export );
    out_port(wire.put_response_export);

  }

  

};

/**CPU interface*/
class TMLcpuIf: public TMLIf, sc_module{
protected:

  int busWidth;
  
  Node *node;
  sc_mutex mutex;
  


public:

//a port to produce request on the bus
basic_initiator_port <ADDRESS_TYPE,DATA_TYPE> initiator_port;
sc_port<  tlm_blocking_get_if < basic_request< ADDRESS_TYPE , DATA_TYPE > > > in_port;


//constructor
  TMLcpuIf(sc_module_name module_name):  sc_module( module_name ) , 
  initiator_port("iport"), in_port("inport")
{
    //plug the wire into the port
   initiator_port( wire.target_export );
   in_port( wire.get_request_export );

    rd_bus = wr_bus = wr_evt_bus = 0;

  }

// These signals represent (when the Interface is used) what it is doing)
  sc_signal<bool> rd_bus, wr_bus, wr_evt_bus;
  
  
 
  void setBusWidth(int _width){
    busWidth = _width;
  }
  
  int getBusWidth() {
    return busWidth;
  }
  
  

  void setNode(Node *n){
    node = n;
  }
  
  Node * getNode(){
    return node;
  }
  

  // These methods are used from the channels and the events
  int write(int nb, TMLChannel *ch);
  int read(int nb, TMLChannel *ch);
  void notifyEvtOnBus(int n_param, TMLEvent * dest);

  
  
protected:
  void writeBackCacheLine(TMLChannel *);
  void loadCacheLine(TMLChannel * ch);
  
  // These method are the ones that communicate with the bus
  void readFromBus(int nb, TMLChannel *ch);
  void writeOnBus(int nb, TMLChannel *ch);
  

  
  
  
};







#endif











