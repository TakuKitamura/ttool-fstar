#include "Door.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__DoorIsOpened 1
#define STATE__IDLE 2
#define STATE__STOP__STATE 3

void *mainFunc__Door(void *arg){
  
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
      __currentState = STATE__IDLE;
      break;
      
      case STATE__DoorIsOpened: 
      traceStateEntering(__myname, "DoorIsOpened");
      waitFor((2)*1000, (4)*1000);
      makeNewRequest(&__req0, 113, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_closed__Controller_closed;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      makeNewRequest(&__req0, 110, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_okDoor__Controller_okDoor;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      waitFor((2)*1000, (4)*1000);
      __currentState = STATE__IDLE;
      break;
      
      case STATE__IDLE: 
      traceStateEntering(__myname, "IDLE");
      makeNewRequest(&__req0, 114, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_open__Controller_open;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      makeNewRequest(&__req0, 111, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_okDoor__Controller_okDoor;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__DoorIsOpened;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

