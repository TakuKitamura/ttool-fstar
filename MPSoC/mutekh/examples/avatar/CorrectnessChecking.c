#include "CorrectnessChecking.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _getEmergencyBrakingMessage;
static uint32_t _toPlausibityCheckMessage;

#define STATE__START__STATE 0
#define STATE__choice__0 1
#define STATE__WaitingForMessageToAnalyze 2
#define STATE__STOP__STATE 3

void CorrectnessChecking__checkingMessage() {
  traceFunctionCall("CorrectnessChecking", "checkingMessage", "-");
}


void CorrectnessChecking__invalidMessage() {
  traceFunctionCall("CorrectnessChecking", "invalidMessage", "-");
}


void CorrectnessChecking__validMessage() {
  traceFunctionCall("CorrectnessChecking", "validMessage", "-");
}


void *mainFunc__CorrectnessChecking(struct mwmr_s *channels_CorrectnessChecking[]){
  
  struct mwmr_s *CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify= channels_CorrectnessChecking[0];
  struct mwmr_s *DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage= channels_CorrectnessChecking[1];
  int id = 0;
  int position = 0;
  bool canHaveInvalid = false;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "CorrectnessChecking";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForMessageToAnalyze;
      break;
      
      case STATE__choice__0: 
      if (canHaveInvalid) {
        makeNewRequest(&__req0, 568, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      makeNewRequest(&__req1, 578, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        CorrectnessChecking__invalidMessage ();
        __currentState = STATE__WaitingForMessageToAnalyze;
        
      }
      else  if (__returnRequest == &__req1) {
        CorrectnessChecking__validMessage ();
        __params0[0] = &id;
        __params0[1] = &position;
        makeNewRequest(&__req0, 563, SEND_SYNC_REQUEST, 0, 0, 0, 2, __params0);
        __req0.syncChannel = &__CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__WaitingForMessageToAnalyze;
        
      }
      break;
      
      case STATE__WaitingForMessageToAnalyze: 
      __params0[0] = &id;
      __params0[1] = &position;
      makeNewRequest(&__req0, 565, RECEIVE_SYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.syncChannel = &__DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      CorrectnessChecking__checkingMessage ();
      __currentState = STATE__choice__0;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

