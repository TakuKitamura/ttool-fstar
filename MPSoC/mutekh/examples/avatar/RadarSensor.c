#include "RadarSensor.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _obstacleAhead;

#define STATE__START__STATE 0
#define STATE__sendingUpdate 1
#define STATE__choice__0 2
#define STATE__WaitingForRadarInfo 3
#define STATE__STOP__STATE 4

void *mainFunc__RadarSensor(struct mwmr_s *channels_RadarSensor[]){
  
  struct mwmr_s *RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead= channels_RadarSensor[0];
  int minRadarUpdate = 100;
  int maxRadarUpdate = 150;
  int obstacleAhead = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "RadarSensor";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForRadarInfo;
      break;
      
      case STATE__sendingUpdate: 
      __params0[0] = &obstacleAhead;
      makeNewRequest(&__req0, 271, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForRadarInfo;
      break;
      
      case STATE__choice__0: 
      makeNewRequest(&__req0, 274, IMMEDIATE, 0, 0, 0, 0, __params0);
      addRequestToList(&__list, &__req0);
      makeNewRequest(&__req1, 281, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        obstacleAhead = 1;
        traceVariableModification("RadarSensor", "obstacleAhead", obstacleAhead,0);
        __currentState = STATE__sendingUpdate;
        
      }
      else  if (__returnRequest == &__req1) {
        obstacleAhead = 1;
        traceVariableModification("RadarSensor", "obstacleAhead", obstacleAhead,0);
        __currentState = STATE__sendingUpdate;
        
      }
      break;
      
      case STATE__WaitingForRadarInfo: 
      waitFor((minRadarUpdate)*1000, (maxRadarUpdate)*1000);
      __currentState = STATE__choice__0;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

