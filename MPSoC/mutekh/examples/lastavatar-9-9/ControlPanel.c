#include "ControlPanel.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__Active 1
#define STATE__STOP__STATE 2

void *mainFunc__ControlPanel(void *arg){
  int duration = 5;
  
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
      __currentState = STATE__Active;
      break;
      
      case STATE__Active: 
      traceStateEntering(__myname, "Active");
      __params0[0] = &duration;
      makeNewRequest(&__req0, 52, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.asyncChannel = &__ControlPanel_startButton__Controller_start;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__Active;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

