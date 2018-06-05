#include "B0.h"

static uint32_t receiveReq;
// Header code defined in the model

// End of header code defined in the model


#define STATE__START__STATE 0
#define STATE__STOP__STATE 1

void *mainFunc__B0(struct mwmr_s *canaux_B0[]){
  
  struct mwmr_s *B_sendReq__B2_receiveReq= canaux_B0[0];
  int val0 = 0;
  
  int __currentState = STATE__START__STATE;
  
  char * __myname;
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      async_read(B_sendReq__B2_receiveReq , &receiveReq , 1);
      __currentState = STATE__STOP__STATE;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

