#include "AlarmManager.h"


// Header code defined in the model

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__AlarmIsOff 1
#define STATE__AlarmIsOn 2
#define STATE__STOP__STATE 3

void *mainFunc__AlarmManager(void *arg){
  int alarmDuration = 5;
  int __timerValue = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
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
      __currentState = STATE__AlarmIsOff;
      break;
      
      case STATE__AlarmIsOff: 
      traceStateEntering(__myname, "AlarmIsOff");
      makeNewRequest(&__req0, 81, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__MainController_highPressure__AlarmManager_highPressure;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __timerValue = alarmDuration;
      traceVariableModification("AlarmManager", "__timerValue", __timerValue,0);
      __params0[0] = &__timerValue;
      makeNewRequest(&__req0, 156, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      makeNewRequest(&__req0, 83, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__AlarmManager_alarmOn__AlarmActuator_alarmOn;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__AlarmIsOn;
      break;
      
      case STATE__AlarmIsOn: 
      traceStateEntering(__myname, "AlarmIsOn");
      makeNewRequest(&__req0, 163, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__AlarmManager_expire__alarmTimer__Timer__alarmTimer__AlarmManager_expire;
      addRequestToList(&__list, &__req0);
      makeNewRequest(&__req1, 85, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__MainController_highPressure__AlarmManager_highPressure;
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
        makeNewRequest(&__req0, 89, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__AlarmManager_alarmOff__AlarmActuator_alarmOff;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__AlarmIsOff;
        
      }
      else  if (__returnRequest == &__req1) {
        makeNewRequest(&__req0, 159, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__AlarmManager_reset__alarmTimer__Timer__alarmTimer__AlarmManager_reset;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __timerValue = alarmDuration;
        traceVariableModification("AlarmManager", "__timerValue", __timerValue,0);
        __params0[0] = &__timerValue;
        makeNewRequest(&__req0, 160, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
        __req0.syncChannel = &__AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set;
        __returnRequest = executeOneRequest(&__list, &__req0);
        clearListOfRequests(&__list);
        traceRequest(__myname, __returnRequest);
        __currentState = STATE__AlarmIsOn;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

