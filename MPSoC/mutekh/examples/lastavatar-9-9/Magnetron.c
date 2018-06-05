#include "Magnetron.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__Running 1
#define STATE__WaitForStart 2
#define STATE__STOP__STATE 3

void *mainFunc__Magnetron(void *arg){
  int power = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
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
      __currentState = STATE__WaitForStart;
      break;
      
      case STATE__Running: 
      traceStateEntering(__myname, "Running");
      makeNewRequest(&__req0, 100, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Controller_stopMagnetron__Magnetron_stopM;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitForStart;
      break;
      
      case STATE__WaitForStart: 
      traceStateEntering(__myname, "WaitForStart");
      makeNewRequest(&__req0, 102, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Controller_startMagnetron__Magnetron_startM;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__Running;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

