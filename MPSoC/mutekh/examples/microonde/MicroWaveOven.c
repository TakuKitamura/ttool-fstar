#include "MicroWaveOven.h"
//#include "asyncchannel.h"
#define STATE__START__STATE 0
#define STATE__Running 1
#define STATE__STOP__STATE 2

void *mainFunc__MicroWaveOven(void *arg){

  
  int __currentState = STATE__START__STATE;
  char * __myname = (char *)arg;

  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      //traceStateEntering(__myname, "__StartState");
      __currentState = STATE__Running;
      break;
      
      case STATE__Running: 
      //traceStateEntering(__myname, "Running");
      __currentState = STATE__STOP__STATE;
      break;
      
    }
  }

  return NULL;
}

