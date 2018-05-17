#include "TestBench.h"


// Header code defined in the model

// End of header code defined in the model


#define STATE__START__STATE 0
#define STATE__STOP__STATE 1

void *mainFunc__TestBench(struct mwmr_s *channels_TestBench[]){
  
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "TestBench";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__STOP__STATE;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

