#include "Bell.h"
//#include "asyncchannel.h"
#define STATE__START__STATE 0
#define STATE__Active 1
#define STATE__STOP__STATE 2

static uint32_t Bell_ring;


void *mainFunc__Bell(void *arg){
	
 struct mwmr_s * Controller_ringBell__Bell_ring  ; 
 
 int __currentState = STATE__START__STATE;
 char * __myname = (char *)arg;

    while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {

/*********************STATE__START__STATE*************************/

      case STATE__START__STATE: 
      //traceStateEntering(__myname, "__StartState");
      __currentState = STATE__Active;
      break;

/*****************************STATE__Active*************************/   
      case STATE__Active: 
      //traceStateEntering(__myname, "Active");
      //async_read( Controller_ringBell__Bell_ring,&Bell_ring, 1);
       async_read( Controller_ringBell__Bell_ring,&Bell_ring, 1);
     // traceRequest(__myname, __returnRequest);
      __currentState = STATE__Active;
      break;
      
    }
  }
  return NULL;
}


