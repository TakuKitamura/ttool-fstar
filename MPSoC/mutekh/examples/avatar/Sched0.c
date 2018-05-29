#include "Sched0.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _scheduledPacket0;
static uint32_t _toScheduler0;

#define STATE__START__STATE 0
#define STATE__PriorityLow 1
#define STATE__PriorityMedium 2
#define STATE__PriorityHigh 3
#define STATE__choice__0 4
#define STATE__Enqueue 5
#define STATE__Waiting 6
#define STATE__STOP__STATE 7

void *mainFunc__Sched0(struct mwmr_s *channels_Sched0[]){
  
  struct mwmr_s *Sched0_toScheduler0__Scheduling_to_scheduler0= channels_Sched0[0];
  struct mwmr_s *Sched0_scheduledPacket0__Scheduling_scheduledPacket0= channels_Sched0[1];
  int packet__address = 0;
  int packet__date = 0;
  
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
  
  char * __myname = "Sched0";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + Waiting");
      __currentState = STATE__Waiting;
      break;
      
      case STATE__PriorityLow: 
      waitFor((100)*1000, (100)*1000);
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 1031, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Waiting");
      __currentState = STATE__Waiting;
      break;
      
      case STATE__PriorityMedium: 
      waitFor((50)*1000, (50)*1000);
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 1030, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Waiting");
      __currentState = STATE__Waiting;
      break;
      
      case STATE__PriorityHigh: 
      waitFor((10)*1000, (10)*1000);
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 1029, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Waiting");
      __currentState = STATE__Waiting;
      break;
      
      case STATE__choice__0: 
      if (packet__date == 2) {
        makeNewRequest(&__req0, 1038, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (packet__date == 1) {
        makeNewRequest(&__req1, 1045, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (packet__date == 0) {
        makeNewRequest(&__req2, 1048, IMMEDIATE, 0, 0, 0, 0, __params2);
        addRequestToList(&__list, &__req2);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + PriorityHigh");
        __currentState = STATE__PriorityHigh;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + PriorityMedium");
        __currentState = STATE__PriorityMedium;
        
      }
      else  if (__returnRequest == &__req2) {
        debug2Msg(__myname, "-> (=====) Entering state + PriorityLow");
        __currentState = STATE__PriorityLow;
        
      }
      break;
      
      case STATE__Enqueue: 
      debug2Msg(__myname, "-> (=====) Entering state + choice__0");
      __currentState = STATE__choice__0;
      break;
      
      case STATE__Waiting: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      makeNewRequest(&__req0, 1028, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__Sched0_toScheduler0__Scheduling_to_scheduler0;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Enqueue");
      __currentState = STATE__Enqueue;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

