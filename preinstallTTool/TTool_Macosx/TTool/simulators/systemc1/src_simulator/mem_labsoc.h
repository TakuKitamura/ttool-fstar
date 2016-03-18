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


#ifndef MEM_LABSOC_HEADER
#define MEM_LABSOC_HEADER

#include "systemc.h"
#include "bus_types.h"
#include "basic_protocol.h"
#include "basic_slave_base.h"
#include "interface_labsoc.h"

using basic_protocol::basic_status;
using basic_protocol::basic_slave_base;
using tlm::tlm_transport_if;

using basic_protocol::basic_request;
using basic_protocol::basic_response;

//extends the base class basic_slave_base of TLM, we just redefine the read and write function
class memory 
{
public:

  typedef basic_request< ADDRESS_TYPE , DATA_TYPE > REQ;
  typedef basic_response< DATA_TYPE > RSP;


  int latency;//could be divided into read and write latency if needed

  sc_signal<bool> rd_mem, wr_mem;
  
  TMLmemIf * If;
  
  memory();
  void go ();
  basic_status write( const ADDRESS_TYPE & , const DATA_TYPE & );
  basic_status read( const ADDRESS_TYPE & , DATA_TYPE & );
  

  void setIf(TMLmemIf * _If){
  If=_If;
  }

void setLatency(int lat){
latency=lat;
}

  ~memory();



};




#endif
