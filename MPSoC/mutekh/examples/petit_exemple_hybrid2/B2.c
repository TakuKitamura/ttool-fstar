#include "B2.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__WaitingForReqs 1
#define STATE__STOP__STATE 2

void *mainFunc__B2(void *arg){
  int val = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = (char *)arg;
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      waitFor((10)*1000, (10)*1000);
      __currentState = STATE__WaitingForReqs;
      break;
      
      case STATE__WaitingForReqs: 
      traceStateEntering(__myname, "WaitingForReqs");
      __params0[0] = &val;
      makeNewRequest(&__req0, 18, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.asyncChannel = &__B_sendReq__B2_receiveReq;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForReqs;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

