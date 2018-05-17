#include "Controller.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__Starting 1
#define STATE__Heating 2
#define STATE__Idle 3
#define STATE__DoorOpened 4
#define STATE__DoorOpenedWhileHeating 5
#define STATE__STOP__STATE 6

void *mainFunc__Controller(void *arg){
  int duration = 5;
  int remainingTime = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[1];
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
      __currentState = STATE__Idle;
      break;
      
      case STATE__Starting: 
      traceStateEntering(__myname, "Starting");
      makeNewRequest(&__req0, 72, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Controller_startMagnetron__Magnetron_startM;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      remainingTime=duration;
      traceVariableModification("Controller", "remainingTime", remainingTime,0);
      __currentState = STATE__Heating;
      break;
      
      case STATE__Heating: 
      traceStateEntering(__myname, "Heating");
      if (( remainingTime>0 )) {
        makeNewRequest(&__req0, 87, IMMEDIATE, 1, (1)*1000, (1)*1000, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      makeNewRequest(&__req1, 62, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.asyncChannel = &__Door_open__Controller_open;
      addRequestToList(&__list, &__req1);
      if (( remainingTime==0 )) {
        makeNewRequest(&__req2, 70, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params2);
        __req2.asyncChannel = &__Controller_stopMagnetron__Magnetron_stopM;
        addRequestToList(&__list, &__req2);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
       if (__returnRequest == &__req0) {
        remainingTime= remainingTime -1;
        traceVariableModification("Controller", "remainingTime", remainingTime,0);
        __currentState = STATE__Heating;
        
      }
      else  if (__returnRequest == &__req1) {
        makeNewRequest(&__req0, 69, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.asyncChannel = &__Controller_stopMagnetron__Magnetron_stopM;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        makeNewRequest(&__req0, 58, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.asyncChannel = &__Door_okDoor__Controller_okDoor;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__DoorOpenedWhileHeating;
        
      }
      else  if (__returnRequest == &__req2) {
        makeNewRequest(&__req0, 64, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.asyncChannel = &__Controller_ringBell__Bell_ring;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__Idle;
        
      }
      break;
      
      case STATE__Idle: 
      traceStateEntering(__myname, "Idle");
      makeNewRequest(&__req0, 65, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_open__Controller_open;
      addRequestToList(&__list, &__req0);
      __params1[0] = &duration;
      makeNewRequest(&__req1, 67, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params1);
      __req1.asyncChannel = &__ControlPanel_startButton__Controller_start;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
       if (__returnRequest == &__req0) {
        makeNewRequest(&__req0, 60, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.asyncChannel = &__Door_okDoor__Controller_okDoor;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__DoorOpened;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__Starting;
        
      }
      break;
      
      case STATE__DoorOpened: 
      traceStateEntering(__myname, "DoorOpened");
      makeNewRequest(&__req0, 74, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_closed__Controller_closed;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      makeNewRequest(&__req0, 59, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_okDoor__Controller_okDoor;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__Idle;
      break;
      
      case STATE__DoorOpenedWhileHeating: 
      traceStateEntering(__myname, "DoorOpenedWhileHeating");
      makeNewRequest(&__req0, 73, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_closed__Controller_closed;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      makeNewRequest(&__req0, 57, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Door_okDoor__Controller_okDoor;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      makeNewRequest(&__req0, 71, SEND_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Controller_startMagnetron__Magnetron_startM;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__Heating;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

