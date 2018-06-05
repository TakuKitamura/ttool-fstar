#include "DSRSC_Management.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _obstacleDetected;
static uint32_t _carPosition;
static uint32_t _setCarPosition;
static uint32_t _forwardEmergencyBrakingMessage;
static uint32_t _broadcastEmergencyBrakingMessage;

#define STATE__START__STATE 0
#define STATE__WaitingForEnvironmentInput 1
#define STATE__STOP__STATE 2

void DSRSC_Management__sendMessage(int id, int position) {
  char my__attr[CHAR_ALLOC_SIZE];
  sprintf(my__attr, "%d,%d",id,position);
  traceFunctionCall("DSRSC_Management", "sendMessage", my__attr);
}


void *mainFunc__DSRSC_Management(struct mwmr_s *channels_DSRSC_Management[]){
  
  struct mwmr_s *DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage= channels_DSRSC_Management[0];
  struct mwmr_s *DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency= channels_DSRSC_Management[1];
  struct mwmr_s *EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected= channels_DSRSC_Management[2];
  struct mwmr_s *DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode= channels_DSRSC_Management[3];
  struct mwmr_s *CarPositionSimulator_carPosition__DSRSC_Management_carPosition= channels_DSRSC_Management[4];
  int id = 0;
  int position = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[2];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "DSRSC_Management";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForEnvironmentInput;
      break;
      
      case STATE__WaitingForEnvironmentInput: 
      __params0[0] = &id;
      __params0[1] = &position;
      makeNewRequest(&__req0, 317, RECEIVE_SYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.syncChannel = &__DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency;
      addRequestToList(&__list, &__req0);
      __params1[0] = &id;
      __params1[1] = &position;
      makeNewRequest(&__req1, 320, RECEIVE_SYNC_REQUEST, 0, 0, 0, 2, __params1);
      __req1.syncChannel = &__CarPositionSimulator_carPosition__DSRSC_Management_carPosition;
      addRequestToList(&__list, &__req1);
      __params2[0] = &id;
      __params2[1] = &position;
      makeNewRequest(&__req2, 318, RECEIVE_SYNC_REQUEST, 0, 0, 0, 2, __params2);
      __req2.syncChannel = &__EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
      addRequestToList(&__list, &__req2);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
       if (__returnRequest == &__req0) {
        DSRSC_Management__sendMessage (id, position);
        __currentState = STATE__WaitingForEnvironmentInput;
        
      }
      else  if (__returnRequest == &__req1) {
        __params0[0] = &id;
        __params0[1] = &position;
        makeNewRequest(&__req0, 319, SEND_SYNC_REQUEST, 0, 0, 0, 2, __params0);
        __req0.syncChannel = &__DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__WaitingForEnvironmentInput;
        
      }
      else  if (__returnRequest == &__req2) {
        __params0[0] = &id;
        __params0[1] = &position;
        makeNewRequest(&__req0, 316, SEND_SYNC_REQUEST, 0, 0, 0, 2, __params0);
        __req0.syncChannel = &__DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__WaitingForEnvironmentInput;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

