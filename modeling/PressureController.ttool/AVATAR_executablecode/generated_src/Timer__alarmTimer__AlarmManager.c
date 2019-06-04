#include "Timer__alarmTimer__AlarmManager.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__wait4set 1
#define STATE__wait4expire 2
#define STATE__STOP__STATE 3

void *mainFunc__Timer__alarmTimer__AlarmManager(void *arg){
  int value = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[1];
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
      __currentState = STATE__wait4set;
      break;
      
      case STATE__wait4set: 
      traceStateEntering(__myname, "wait4set");
      __params0[0] = &value;
      makeNewRequest(&__req0, 138, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set;
      addRequestToList(&__list, &__req0);
      makeNewRequest(&__req1, 140, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__AlarmManager_reset__alarmTimer__Timer__alarmTimer__AlarmManager_reset;
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
        __currentState = STATE__wait4expire;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__wait4set;
        
      }
      break;
      
      case STATE__wait4expire: 
      traceStateEntering(__myname, "wait4expire");
      __params0[0] = &value;
      makeNewRequest(&__req0, 139, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set;
      addRequestToList(&__list, &__req0);
      makeNewRequest(&__req1, 142, SEND_SYNC_REQUEST, 1, (value)*1000000, (value)*1000000, 0, __params1);
      __req1.syncChannel = &__AlarmManager_expire__alarmTimer__Timer__alarmTimer__AlarmManager_expire;
      addRequestToList(&__list, &__req1);
      makeNewRequest(&__req2, 141, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params2);
      __req2.syncChannel = &__AlarmManager_reset__alarmTimer__Timer__alarmTimer__AlarmManager_reset;
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
        __currentState = STATE__wait4expire;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__wait4set;
        
      }
      else  if (__returnRequest == &__req2) {
        __currentState = STATE__wait4set;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

