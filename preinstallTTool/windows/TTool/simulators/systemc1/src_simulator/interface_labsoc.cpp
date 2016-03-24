#include "systemc"
#include "parameters.h"
#include "node_labsoc.h"
#include "channel_labsoc.h"
#include "utils_labsoc.h" 
#include "bus_labsoc.h"
#include "mem_labsoc.h"
#include "comm_labsoc.h"



int TMLcpuIf::write(int nb, TMLChannel *ch){
  TMLcpuIf *readIf = ch->getReadIf();
  
  
  // If tasks on the same CPU
  if(readIf->getNode() == node) {
    // If cache miss
    if(Utils::myrand(0, 999) < node->getCacheMissRate()) {
      node->setCacheMiss();
      if(Utils::myrand(0, 99) < node->getCacheWriteBackRate()){
        writeBackCacheLine(ch);
      }
      loadCacheLine(ch);
      node->unsetCacheMiss();
    } 
    
    return nb;
  }
  
  
  // Inter CPU communication
  writeOnBus(nb, ch);
  
  return nb;
 
}

void TMLcpuIf::writeBackCacheLine(TMLChannel * ch) {
  node->setCacheWriteBackLine();
  writeOnBus(node->getCacheLineSize(), ch);
  node->unsetCacheWriteBackLine();
}

void TMLcpuIf::loadCacheLine(TMLChannel * ch){
  node->setCacheLoadLine();
  readFromBus(node->getCacheLineSize(),ch );
  node->unsetCacheLoadLine();
}


int TMLcpuIf::read(int nb, TMLChannel *ch) {
  TMLcpuIf *writeIf = ch->getWriteIf();

  // If tasks on the same CPU
  if(writeIf->getNode() == node) {
    // Cache emulation
    if(Utils::myrand(0, 999) < node->getCacheMissRate()) {
      node->setCacheMiss();
      if(Utils::myrand(0, 99) < node->getCacheWriteBackRate()){
        writeBackCacheLine(ch);
      }
      loadCacheLine(ch);
      node->unsetCacheMiss();
    }
    return nb;
  }
  
  // Tasks on different cpus
  readFromBus(nb, ch);
  return nb;
}




void TMLcpuIf::readFromBus(int nb, TMLChannel *ch){
  while(nb > 0) {
    rd_bus = 1;
    initiator_port.read(ch->mem->If, nb);
    rd_bus = 0;
    nb -= busWidth;
  }
}


void TMLcpuIf::writeOnBus(int nb, TMLChannel * ch){
  while(nb > 0) {
    wr_bus = 1;
    initiator_port.write( ch->mem->If, nb);
    wr_bus = 0;
    nb -= busWidth;
  }
}




void TMLcpuIf::notifyEvtOnBus(int n_param, TMLEvent * dest){
  int i;
  i = 1 + (n_param * 4) / busWidth;
  if((n_param * 4) % busWidth)
    i++;
  for(; i > 0; i--) {
    wr_evt_bus = 1;
    initiator_port.write(NULL, i);
    wr_evt_bus = 0;
  }
}
