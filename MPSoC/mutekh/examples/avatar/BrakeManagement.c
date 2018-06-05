#include "BrakeManagement.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _brake;

#define STATE__START__STATE 0
#define STATE__WaitingforBrakingToBeCompleted 1
#define STATE__WaitForBrakingOrder 2
#define STATE__STOP__STATE 3

void BrakeManagement__applyBraking(int value) {
  char my__attr[CHAR_ALLOC_SIZE];
  sprintf(my__attr, "%d",value);
  traceFunctionCall("BrakeManagement", "applyBraking", my__attr);
}


void BrakeManagement__brakingDone() {
  traceFunctionCall("BrakeManagement", "brakingDone", "-");
}


void *mainFunc__BrakeManagement(struct mwmr_s *channels_BrakeManagement[]){
  
  struct mwmr_s *DangerAvoidanceStrategy_brakePower__BrakeManagement_brake= channels_BrakeManagement[0];
  int value = 0;
  int deltaBrake = 0;
  int brakeMaxDuration = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "BrakeManagement";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitForBrakingOrder;
      break;
      
      case STATE__WaitingforBrakingToBeCompleted: 
      waitFor((value)*1000, (brakeMaxDuration)*1000);
      BrakeManagement__brakingDone ();
      __currentState = STATE__WaitForBrakingOrder;
      break;
      
      case STATE__WaitForBrakingOrder: 
      __params0[0] = &value;
      makeNewRequest(&__req0, 635, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      BrakeManagement__applyBraking (value);
      value = value * 10;
      traceVariableModification("BrakeManagement", "value", value,0);
      brakeMaxDuration = value+deltaBrake;
      traceVariableModification("BrakeManagement", "brakeMaxDuration", brakeMaxDuration,0);
      __currentState = STATE__WaitingforBrakingToBeCompleted;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

