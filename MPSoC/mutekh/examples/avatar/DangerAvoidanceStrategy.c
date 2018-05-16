#include "DangerAvoidanceStrategy.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _brake;
static uint32_t _reducePower;
static uint32_t _brakePower;
static uint32_t _forwardEmergency;

#define STATE__START__STATE 0
#define STATE__ForwardManagement 1
#define STATE__BrakingManagement 2
#define STATE__choice__0 3
#define STATE__WaitingForActionsToTake 4
#define STATE__STOP__STATE 5

void *mainFunc__DangerAvoidanceStrategy(struct mwmr_s *channels_DangerAvoidanceStrategy[]){
  
  struct mwmr_s *DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency= channels_DangerAvoidanceStrategy[0];
  struct mwmr_s *DangerAvoidanceStrategy_brakePower__BrakeManagement_brake= channels_DangerAvoidanceStrategy[1];
  struct mwmr_s *DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder= channels_DangerAvoidanceStrategy[2];
  struct mwmr_s *DangerAvoidanceStrategy_brake__PlausibilityCheck_brake= channels_DangerAvoidanceStrategy[3];
  int speed = 0;
  int position = 0;
  int currentPosition = 0;
  int carinfo__minID = 1;
  int carinfo__maxID = 5;
  int carinfo__minPosition = 3;
  int carinfo__maxPosition = 10;
  int carinfo__minSpeed = 1;
  int carinfo__maxSpeed = 10;
  int carinfo__myID = 11;
  int value = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[3];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[3];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "DangerAvoidanceStrategy";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForActionsToTake;
      break;
      
      case STATE__ForwardManagement: 
      value = carinfo__myID;
      traceVariableModification("DangerAvoidanceStrategy", "value", value,0);
      __params0[0] = &value;
      __params0[1] = &currentPosition;
      makeNewRequest(&__req0, 600, SEND_SYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.syncChannel = &__DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForActionsToTake;
      break;
      
      case STATE__BrakingManagement: 
      value = (position-currentPosition);
      traceVariableModification("DangerAvoidanceStrategy", "value", value,0);
      __params0[0] = &value;
      makeNewRequest(&__req0, 601, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__ForwardManagement;
      break;
      
      case STATE__choice__0: 
      if (!(speed>5)) {
        makeNewRequest(&__req0, 623, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (speed>5) {
        __params1[0] = &value;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req1);
        makeNewRequest(&__req1, 604, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params1);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req1.asyncChannel = &__DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
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
        __currentState = STATE__BrakingManagement;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__BrakingManagement;
        
      }
      break;
      
      case STATE__WaitingForActionsToTake: 
      __params0[0] = &speed;
      __params0[1] = &currentPosition;
      __params0[2] = &position;
      makeNewRequest(&__req0, 606, RECEIVE_SYNC_REQUEST, 0, 0, 0, 3, __params0);
      __req0.syncChannel = &__DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__choice__0;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

