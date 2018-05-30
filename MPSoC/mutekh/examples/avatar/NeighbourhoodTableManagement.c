#include "NeighbourhoodTableManagement.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _addANode;
static uint32_t _setPosition;
static uint32_t _sendTable;

#define STATE__START__STATE 0
#define STATE__choice__0 1
#define STATE__choice__1 2
#define STATE__choice__2 3
#define STATE__IDAdded 4
#define STATE__PositionUpdated 5
#define STATE__choice__3 6
#define STATE__UpdatingPosition 7
#define STATE__IsKnownID 8
#define STATE__choice__4 9
#define STATE__AddingNewID 10
#define STATE__RemovingOldIDs 11
#define STATE__WaitingForNewNodesOrPosition 12
#define STATE__STOP__STATE 13

void *mainFunc__NeighbourhoodTableManagement(struct mwmr_s *channels_NeighbourhoodTableManagement[]){
  
  struct mwmr_s *NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList= channels_NeighbourhoodTableManagement[0];
  struct mwmr_s *GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition= channels_NeighbourhoodTableManagement[1];
  struct mwmr_s *DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode= channels_NeighbourhoodTableManagement[2];
  int id = 0;
  int position = 0;
  int time_id = 0;
  int listOfNodes__id0 = 0;
  int listOfNodes__id1 = 0;
  int listOfNodes__id2 = 0;
  int listOfNodes__position0 = 0;
  int listOfNodes__position1 = 0;
  int listOfNodes__position2 = 0;
  int currentPosition = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[7];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[7];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[7];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "NeighbourhoodTableManagement";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      listOfNodes__id0 = 0;
      traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id0", listOfNodes__id0,0);
      listOfNodes__id1 = 0;
      traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id1", listOfNodes__id1,0);
      listOfNodes__id2 = 0;
      traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id2", listOfNodes__id2,0);
      __currentState = STATE__WaitingForNewNodesOrPosition;
      break;
      
      case STATE__choice__0: 
      if (!(listOfNodes__id1 == id)) {
        makeNewRequest(&__req0, 455, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (listOfNodes__id1 == id) {
        makeNewRequest(&__req1, 556, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        listOfNodes__position2 = position;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__position2", listOfNodes__position2,0);
        __currentState = STATE__PositionUpdated;
        
      }
      else  if (__returnRequest == &__req1) {
        listOfNodes__position1 = position;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__position1", listOfNodes__position1,0);
        __currentState = STATE__PositionUpdated;
        
      }
      break;
      
      case STATE__choice__1: 
      if ((listOfNodes__position2>=listOfNodes__position1)&&(listOfNodes__position2>=listOfNodes__position1)) {
        makeNewRequest(&__req0, 353, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if ((listOfNodes__position1>=listOfNodes__position0)&&(listOfNodes__position1>=listOfNodes__position2)) {
        makeNewRequest(&__req1, 372, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if ((listOfNodes__position0>=listOfNodes__position1)&&(listOfNodes__position0>=listOfNodes__position2)) {
        makeNewRequest(&__req2, 390, IMMEDIATE, 0, 0, 0, 0, __params2);
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
        listOfNodes__id2 = 0;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id2", listOfNodes__id2,0);
        __currentState = STATE__AddingNewID;
        
      }
      else  if (__returnRequest == &__req1) {
        listOfNodes__id1 = 0;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id1", listOfNodes__id1,0);
        __currentState = STATE__AddingNewID;
        
      }
      else  if (__returnRequest == &__req2) {
        listOfNodes__id0 = 0;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id0", listOfNodes__id0,0);
        __currentState = STATE__AddingNewID;
        
      }
      break;
      
      case STATE__choice__2: 
      if ((listOfNodes__position2<currentPosition)) {
        makeNewRequest(&__req0, 408, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if ((listOfNodes__position1<currentPosition)) {
        makeNewRequest(&__req1, 416, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if ((listOfNodes__position0<currentPosition)) {
        makeNewRequest(&__req2, 424, IMMEDIATE, 0, 0, 0, 0, __params2);
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
        listOfNodes__id2 = 0;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id2", listOfNodes__id2,0);
        __currentState = STATE__AddingNewID;
        
      }
      else  if (__returnRequest == &__req1) {
        listOfNodes__id1 = 0;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id1", listOfNodes__id1,0);
        __currentState = STATE__AddingNewID;
        
      }
      else  if (__returnRequest == &__req2) {
        listOfNodes__id0 = 0;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id0", listOfNodes__id0,0);
        __currentState = STATE__AddingNewID;
        
      }
      break;
      
      case STATE__IDAdded: 
      __currentState = STATE__WaitingForNewNodesOrPosition;
      break;
      
      case STATE__PositionUpdated: 
      __currentState = STATE__WaitingForNewNodesOrPosition;
      break;
      
      case STATE__choice__3: 
      if (listOfNodes__id0 == id) {
        makeNewRequest(&__req0, 458, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (!(listOfNodes__id0 == id)) {
        makeNewRequest(&__req1, 562, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        listOfNodes__position0 = position;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__position0", listOfNodes__position0,0);
        __currentState = STATE__PositionUpdated;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__choice__0;
        
      }
      break;
      
      case STATE__UpdatingPosition: 
      __currentState = STATE__choice__3;
      break;
      
      case STATE__IsKnownID: 
      if (!((listOfNodes__id0 == id)||(listOfNodes__id1 == id)||(listOfNodes__id2 == id))) {
        makeNewRequest(&__req0, 464, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if ((listOfNodes__id0 == id)||(listOfNodes__id1 == id)||(listOfNodes__id2 == id)) {
        makeNewRequest(&__req1, 529, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__RemovingOldIDs;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__UpdatingPosition;
        
      }
      break;
      
      case STATE__choice__4: 
      if (!((listOfNodes__position0<currentPosition)||(listOfNodes__position1<currentPosition)||(listOfNodes__position2<currentPosition))) {
        makeNewRequest(&__req0, 432, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if ((listOfNodes__position0<currentPosition)||(listOfNodes__position1<currentPosition)||(listOfNodes__position2<currentPosition)) {
        makeNewRequest(&__req1, 503, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__choice__2;
        
      }
      break;
      
      case STATE__AddingNewID: 
      if (listOfNodes__id2 == 0) {
        makeNewRequest(&__req0, 433, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (listOfNodes__id1 == 0) {
        makeNewRequest(&__req1, 440, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (listOfNodes__id0 == 0) {
        makeNewRequest(&__req2, 447, IMMEDIATE, 0, 0, 0, 0, __params2);
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
        listOfNodes__id2 = id;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id2", listOfNodes__id2,0);
        listOfNodes__position2 = position;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__position2", listOfNodes__position2,0);
        __currentState = STATE__IDAdded;
        
      }
      else  if (__returnRequest == &__req1) {
        listOfNodes__id1 = id;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id1", listOfNodes__id1,0);
        listOfNodes__position1 = position;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__position1", listOfNodes__position1,0);
        __currentState = STATE__IDAdded;
        
      }
      else  if (__returnRequest == &__req2) {
        listOfNodes__id0 = id;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__id0", listOfNodes__id0,0);
        listOfNodes__position0 = position;
        traceVariableModification("NeighbourhoodTableManagement", "listOfNodes__position0", listOfNodes__position0,0);
        __currentState = STATE__IDAdded;
        
      }
      break;
      
      case STATE__RemovingOldIDs: 
      if (!((listOfNodes__id0>0)&&(listOfNodes__id1>0)&&(listOfNodes__id2>0))) {
        makeNewRequest(&__req0, 467, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if ((listOfNodes__id0>0)&&(listOfNodes__id1>0)&&(listOfNodes__id2>0)) {
        makeNewRequest(&__req1, 477, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__AddingNewID;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__choice__4;
        
      }
      break;
      
      case STATE__WaitingForNewNodesOrPosition: 
      __params0[0] = &currentPosition;
      makeNewRequest(&__req0, 345, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;
      addRequestToList(&__list, &__req0);
      __params1[0] = &id;
      __params1[1] = &position;
      makeNewRequest(&__req1, 350, RECEIVE_SYNC_REQUEST, 0, 0, 0, 2, __params1);
      __req1.syncChannel = &__DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
      addRequestToList(&__list, &__req1);
      __params2[0] = &currentPosition;
      __params2[1] = &listOfNodes__id0;
      __params2[2] = &listOfNodes__id1;
      __params2[3] = &listOfNodes__id2;
      __params2[4] = &listOfNodes__position0;
      __params2[5] = &listOfNodes__position1;
      __params2[6] = &listOfNodes__position2;
      makeNewRequest(&__req2, 337, SEND_SYNC_REQUEST, 0, 0, 0, 7, __params2);
      __req2.syncChannel = &__NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
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
        __currentState = STATE__WaitingForNewNodesOrPosition;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__IsKnownID;
        
      }
      else  if (__returnRequest == &__req2) {
        __currentState = STATE__WaitingForNewNodesOrPosition;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

