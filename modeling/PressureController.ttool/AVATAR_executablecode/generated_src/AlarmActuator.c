#include "AlarmActuator.h"


// Header code defined in the model


void __userImplemented__AlarmActuator__setAlarm(bool att1) {
    if (att1) {
        printf("Alarm ON\n");
        sendDatagram("+", 1);
    } else {
        printf("Alarm OFF\n");
        sendDatagram("-", 1);
    }
}

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__WaitingForAlarmCommand 1
#define STATE__STOP__STATE 2

void AlarmActuator__setAlarm(bool onoff) {
  char my__attr[CHAR_ALLOC_SIZE];
  sprintf(my__attr, "%d",onoff);
  traceFunctionCall("AlarmActuator", "setAlarm", my__attr);
}


void *mainFunc__AlarmActuator(void *arg){
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[0];
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
      __currentState = STATE__WaitingForAlarmCommand;
      break;
      
      case STATE__WaitingForAlarmCommand: 
      traceStateEntering(__myname, "WaitingForAlarmCommand");
      makeNewRequest(&__req0, 41, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__AlarmManager_alarmOn__AlarmActuator_alarmOn;
      addRequestToList(&__list, &__req0);
      makeNewRequest(&__req1, 42, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__AlarmManager_alarmOff__AlarmActuator_alarmOff;
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
        AlarmActuator__setAlarm(true);
        __currentState = STATE__WaitingForAlarmCommand;
        
      }
      else  if (__returnRequest == &__req1) {
        AlarmActuator__setAlarm(false);
        __currentState = STATE__WaitingForAlarmCommand;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

