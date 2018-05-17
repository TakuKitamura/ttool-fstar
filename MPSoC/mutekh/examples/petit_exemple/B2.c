#include "B2.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t receiveReq;

#define STATE__START__STATE 0
#define STATE__WaitingForReqs 1
#define STATE__STOP__STATE 2

void *mainFunc__B2(struct mwmr_s *canaux_B2[]){
  
  struct mwmr_s *B_sendReq__B2_receiveReq= canaux_B2[0];
  int val = 0;
  
  int __currentState = STATE__START__STATE;
  
  char * __myname;
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      waitFor((10)*1000, (10)*1000);
      __currentState = STATE__WaitingForReqs;
      break;
      
      case STATE__WaitingForReqs: 
      __currentState = STATE__WaitingForReqs;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

