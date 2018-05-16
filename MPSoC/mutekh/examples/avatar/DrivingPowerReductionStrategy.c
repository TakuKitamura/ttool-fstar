#include "DrivingPowerReductionStrategy.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _getReducePowerOrder;

#define STATE__START__STATE 0
#define STATE__WaitForReducePowerToBePerformed 1
#define STATE__WaitForReducePowerOrder 2
#define STATE__STOP__STATE 3

void DrivingPowerReductionStrategy__applyReducePower(int value) {
  char my__attr[CHAR_ALLOC_SIZE];
  sprintf(my__attr, "%d",value);
  traceFunctionCall("DrivingPowerReductionStrategy", "applyReducePower", my__attr);
}


void DrivingPowerReductionStrategy__reducePowerDone() {
  traceFunctionCall("DrivingPowerReductionStrategy", "reducePowerDone", "-");
}


void *mainFunc__DrivingPowerReductionStrategy(struct mwmr_s *channels_DrivingPowerReductionStrategy[]){
  
  struct mwmr_s *DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder= channels_DrivingPowerReductionStrategy[0];
  int value = 0;
  int minReducePowerTime = 10;
  int maxReducePowerTime = 20;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "DrivingPowerReductionStrategy";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitForReducePowerOrder;
      break;
      
      case STATE__WaitForReducePowerToBePerformed: 
      waitFor((minReducePowerTime)*1000, (maxReducePowerTime)*1000);
      DrivingPowerReductionStrategy__reducePowerDone ();
      __currentState = STATE__WaitForReducePowerOrder;
      break;
      
      case STATE__WaitForReducePowerOrder: 
      __params0[0] = &value;
      makeNewRequest(&__req0, 585, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.asyncChannel = &__DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      DrivingPowerReductionStrategy__applyReducePower (value);
      __currentState = STATE__WaitForReducePowerToBePerformed;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

