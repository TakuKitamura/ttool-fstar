#include "MainController.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__WaitFirstHighPressure 1
#define STATE__choice__0 2
#define STATE__WaitSecondHighPressure 3
#define STATE__choice__1 4
#define STATE__STOP__STATE 5

void *mainFunc__MainController(void *arg){
  int threshold = 20;
  int currentPressure = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
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
      __currentState = STATE__WaitFirstHighPressure;
      break;
      
      case STATE__WaitFirstHighPressure: 
      traceStateEntering(__myname, "WaitFirstHighPressure");
      __params0[0] = &currentPressure;
      makeNewRequest(&__req0, 104, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__PressureSensor_pressureValue__MainController_pressureValue;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__choice__0;
      break;
      
      case STATE__choice__0: 
      traceStateEntering(__myname, "choice__0");
      if (currentPressure < threshold) {
        makeNewRequest(&__req0, 115, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (!(currentPressure < threshold)) {
        makeNewRequest(&__req1, 121, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
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
        __currentState = STATE__WaitFirstHighPressure;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__WaitSecondHighPressure;
        
      }
      break;
      
      case STATE__WaitSecondHighPressure: 
      traceStateEntering(__myname, "WaitSecondHighPressure");
      __params0[0] = &currentPressure;
      makeNewRequest(&__req0, 107, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__PressureSensor_pressureValue__MainController_pressureValue;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__choice__1;
      break;
      
      case STATE__choice__1: 
      traceStateEntering(__myname, "choice__1");
      if (currentPressure < threshold) {
        makeNewRequest(&__req0, 112, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (!(currentPressure < threshold)) {
        makeNewRequest(&__req1, 109, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params1);
        __req1.syncChannel = &__MainController_highPressure__AlarmManager_highPressure;
        addRequestToList(&__list, &__req1);
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
        __currentState = STATE__WaitFirstHighPressure;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__WaitFirstHighPressure;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

