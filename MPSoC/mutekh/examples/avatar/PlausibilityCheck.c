#include "PlausibilityCheck.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _getEmergencyMessageToVerify;
static uint32_t _getInfoOnObstacle;
static uint32_t _getInfoOnSpeed;
static uint32_t _brake;
static uint32_t _getNodeList;

#define STATE__START__STATE 0
#define STATE__choice__0 1
#define STATE__choice__1 2
#define STATE__EmergencyTakenIntoAccount 3
#define STATE__choice__2 4
#define STATE__EmergencyIgnored 5
#define STATE__choice__3 6
#define STATE__WaitingForEmergencyMessage 7
#define STATE__STOP__STATE 8

void PlausibilityCheck__emergencyIgnored() {
  traceFunctionCall("PlausibilityCheck", "emergencyIgnored", "-");
}


void PlausibilityCheck__emergencyTakenIntoAccount() {
  traceFunctionCall("PlausibilityCheck", "emergencyTakenIntoAccount", "-");
}


void PlausibilityCheck__getNodeList(int currentPosition, int list__id0, int list__id1, int list__id2, int list__position0, int list__position1, int list__position2) {
  char my__attr[CHAR_ALLOC_SIZE];
  sprintf(my__attr, "%d,%d,%d,%d,%d,%d,%d",currentPosition,list__id0,list__id1,list__id2,list__position0,list__position1,list__position2);
  traceFunctionCall("PlausibilityCheck", "getNodeList", my__attr);
}


void *mainFunc__PlausibilityCheck(struct mwmr_s *channels_PlausibilityCheck[]){
  
  struct mwmr_s *NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList= channels_PlausibilityCheck[0];
  struct mwmr_s *DangerAvoidanceStrategy_brake__PlausibilityCheck_brake= channels_PlausibilityCheck[1];
  struct mwmr_s *PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle= channels_PlausibilityCheck[2];
  struct mwmr_s *PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed= channels_PlausibilityCheck[3];
  struct mwmr_s *CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify= channels_PlausibilityCheck[4];
  int id = 0;
  int position = 0;
  int speed = 0;
  int obstacle = 0;
  int list__id0 = 0;
  int list__id1 = 0;
  int list__id2 = 0;
  int list__position0 = 0;
  int list__position1 = 0;
  int list__position2 = 0;
  int currentPosition = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[6];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[6];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "PlausibilityCheck";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForEmergencyMessage;
      break;
      
      case STATE__choice__0: 
      if (!(position>currentPosition)) {
        makeNewRequest(&__req0, 681, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (position>currentPosition) {
        makeNewRequest(&__req1, 729, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__EmergencyIgnored;
        
      }
      else  if (__returnRequest == &__req1) {
        PlausibilityCheck__emergencyTakenIntoAccount ();
        __currentState = STATE__EmergencyTakenIntoAccount;
        
      }
      break;
      
      case STATE__choice__1: 
      if (!((list__id0 == id)||(list__id1 == id)||(list__id2 == id))) {
        makeNewRequest(&__req0, 682, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if ((list__id0 == id)||(list__id1 == id)||(list__id2 == id)) {
        makeNewRequest(&__req1, 703, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__EmergencyIgnored;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__choice__0;
        
      }
      break;
      
      case STATE__EmergencyTakenIntoAccount: 
      __params0[0] = &speed;
      __params0[1] = &currentPosition;
      __params0[2] = &position;
      makeNewRequest(&__req0, 669, SEND_SYNC_REQUEST, 0, 0, 0, 3, __params0);
      __req0.syncChannel = &__DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForEmergencyMessage;
      break;
      
      case STATE__choice__2: 
      if (!(speed<3)) {
        makeNewRequest(&__req0, 684, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (speed<3) {
        makeNewRequest(&__req1, 685, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__choice__1;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__EmergencyIgnored;
        
      }
      break;
      
      case STATE__EmergencyIgnored: 
      PlausibilityCheck__emergencyIgnored ();
      __currentState = STATE__WaitingForEmergencyMessage;
      break;
      
      case STATE__choice__3: 
      if (obstacle>0) {
        makeNewRequest(&__req0, 688, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (obstacle == 0) {
        makeNewRequest(&__req1, 694, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__choice__2;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__EmergencyIgnored;
        
      }
      break;
      
      case STATE__WaitingForEmergencyMessage: 
      __params0[0] = &id;
      __params0[1] = &position;
      makeNewRequest(&__req0, 677, RECEIVE_SYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.syncChannel = &__CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __params0[0] = &speed;
      makeNewRequest(&__req0, 676, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __params0[0] = &obstacle;
      makeNewRequest(&__req0, 675, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __params0[0] = &list__id0;
      __params0[1] = &list__id1;
      __params0[2] = &list__id2;
      __params0[3] = &list__position0;
      __params0[4] = &list__position1;
      __params0[5] = &list__position2;
      makeNewRequest(&__req0, 667, RECEIVE_SYNC_REQUEST, 0, 0, 0, 6, __params0);
      __req0.syncChannel = &__NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__choice__3;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

