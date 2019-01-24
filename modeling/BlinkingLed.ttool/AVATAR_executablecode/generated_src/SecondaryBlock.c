#include "SecondaryBlock.h"


// Header code defined in the model
extern int val;

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__Waiting_Cycle 1
#define STATE__CalculatingRand 2
#define STATE__SendingRand 3
#define STATE__STOP__STATE 4

void *mainFunc__SecondaryBlock(void *arg){
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
      __currentState = STATE__Waiting_Cycle;
      break;
      
      case STATE__Waiting_Cycle: 
      traceStateEntering(__myname, "Waiting_Cycle");
      waitFor((4)*1000000, (4)*1000000);
      __currentState = STATE__CalculatingRand;
      break;
      
      case STATE__CalculatingRand: 
      traceStateEntering(__myname, "CalculatingRand");
      val = computeRandom(1, 10);
      __currentState = STATE__SendingRand;
      break;
      
      case STATE__SendingRand: 
      traceStateEntering(__myname, "SendingRand");
      __params0[0] = &val;
      makeNewRequest(&__req0, 272, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__MainBlock_RandVal__SecondaryBlock_RanVal;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__Waiting_Cycle;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

