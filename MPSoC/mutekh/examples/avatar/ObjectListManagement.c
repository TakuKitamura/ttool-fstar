#include "ObjectListManagement.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _isObstacleAhead;
static uint32_t _getInfoOnObstacle;

#define STATE__START__STATE 0
#define STATE__WaitingForUpdateOrRequestOnObstacle 1
#define STATE__STOP__STATE 2

void *mainFunc__ObjectListManagement(struct mwmr_s *channels_ObjectListManagement[]){
  
  struct mwmr_s *PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle= channels_ObjectListManagement[0];
  struct mwmr_s *RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead= channels_ObjectListManagement[1];
  int obstacle = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "ObjectListManagement";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForUpdateOrRequestOnObstacle;
      break;
      
      case STATE__WaitingForUpdateOrRequestOnObstacle: 
      __params0[0] = &obstacle;
      makeNewRequest(&__req0, 659, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
      addRequestToList(&__list, &__req0);
      __params1[0] = &obstacle;
      makeNewRequest(&__req1, 658, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params1);
      __req1.syncChannel = &__PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
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
        __currentState = STATE__WaitingForUpdateOrRequestOnObstacle;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__WaitingForUpdateOrRequestOnObstacle;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

