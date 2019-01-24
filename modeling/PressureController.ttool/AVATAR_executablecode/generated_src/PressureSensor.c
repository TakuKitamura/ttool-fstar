#include "PressureSensor.h"


// Header code defined in the model
extern int pressure;

int __userImplemented__PressureSensor__readingPressure() {
    return pressure;
}

bool __userImplemented__PressureSensor__isInCode() {
    return 1;
}

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__WaitingForNextCycle 1
#define STATE__SensingPressure 2
#define STATE__choice__0 3
#define STATE__SendingPressure 4
#define STATE__STOP__STATE 5

int PressureSensor__readingPressure() {
  traceFunctionCall("PressureSensor", "readingPressure", "-");
  return __userImplemented__PressureSensor__readingPressure();
}


bool PressureSensor__isInCode() {
  traceFunctionCall("PressureSensor", "isInCode", "-");
  return __userImplemented__PressureSensor__isInCode();
}


void *mainFunc__PressureSensor(void *arg){
  int pressure = 0;
  bool branchToUse = false;
  
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
      __currentState = STATE__WaitingForNextCycle;
      break;
      
      case STATE__WaitingForNextCycle: 
      traceStateEntering(__myname, "WaitingForNextCycle");
      waitFor((1)*1000000, (1)*1000000);
      __currentState = STATE__SensingPressure;
      break;
      
      case STATE__SensingPressure: 
      traceStateEntering(__myname, "SensingPressure");
      branchToUse = PressureSensor__isInCode();
      traceVariableModification("PressureSensor", "branchToUse", branchToUse,1);
      __currentState = STATE__choice__0;
      break;
      
      case STATE__choice__0: 
      traceStateEntering(__myname, "choice__0");
      if (!(branchToUse)) {
        makeNewRequest(&__req0, 63, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (branchToUse) {
        makeNewRequest(&__req1, 67, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        pressure = computeRandom(19, 21);
        __currentState = STATE__SendingPressure;
        
      }
      else  if (__returnRequest == &__req1) {
        pressure = PressureSensor__readingPressure();
        traceVariableModification("PressureSensor", "pressure", pressure,0);
        __currentState = STATE__SendingPressure;
        
      }
      break;
      
      case STATE__SendingPressure: 
      traceStateEntering(__myname, "SendingPressure");
      __params0[0] = &pressure;
      makeNewRequest(&__req0, 59, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__PressureSensor_pressureValue__MainController_pressureValue;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForNextCycle;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

