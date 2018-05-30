#include "B.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t sendReq;

#define STATE__START__STATE 0
#define STATE__STOP__STATE 1

void *mainFunc__B(struct mwmr_s *canaux_B[]){
  
  
  int __currentState = STATE__START__STATE;
  
  char * __myname;
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__STOP__STATE;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

