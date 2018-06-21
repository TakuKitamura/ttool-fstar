#include "Scheduling.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _from_queue_low;
static uint32_t _from_queue_medium;
static uint32_t _from_queue_high;
static uint32_t _to_scheduler0;
static uint32_t _to_scheduler1;
static uint32_t _packet;
static uint32_t _scheduledPacket0;
static uint32_t _scheduledPacket1;

#define STATE__START__STATE 0
#define STATE__Dispatch 1
#define STATE__SendToOutput 2
#define STATE__Waiting 3
#define STATE__Read 4
#define STATE__STOP__STATE 5

void *mainFunc__Scheduling(struct mwmr_s *channels_Scheduling[]){
  
  struct mwmr_s *Scheduling_packet__OutputEngine_packet= channels_Scheduling[0];
  struct mwmr_s *Sched0_scheduledPacket0__Scheduling_scheduledPacket0= channels_Scheduling[1];
  struct mwmr_s *Sched1_scheduledPacket1__Scheduling_scheduledPacket1= channels_Scheduling[2];
  struct mwmr_s *Classification_queue_high__Scheduling_from_queue_high= channels_Scheduling[3];
  struct mwmr_s *Classification_queue_medium__Scheduling_from_queue_medium= channels_Scheduling[4];
  struct mwmr_s *Classification_queue_low__Scheduling_from_queue_low= channels_Scheduling[5];
  struct mwmr_s *Sched0_toScheduler0__Scheduling_to_scheduler0= channels_Scheduling[6];
  struct mwmr_s *Sched1_toScheduler1__Scheduling_to_scheduler1= channels_Scheduling[7];
  int packet__address = 0;
  int packet__date = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[2];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[2];
  __attribute__((unused)) request __req3;
  __attribute__((unused))int *__params3[2];
  __attribute__((unused)) request __req4;
  __attribute__((unused))int *__params4[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Scheduling";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__Waiting;
      break;
      
      case STATE__Dispatch: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 971, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Sched1_toScheduler1__Scheduling_to_scheduler1;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req1);
      makeNewRequest(&__req1, 970, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req1.asyncChannel = &__Sched0_toScheduler0__Scheduling_to_scheduler0;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__Waiting;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__Waiting;
        
      }
      break;
      
      case STATE__SendToOutput: 
      waitFor((50)*1000, (50)*1000);
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 962, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Scheduling_packet__OutputEngine_packet;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __currentState = STATE__Waiting;
      break;
      
      case STATE__Waiting: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      makeNewRequest(&__req0, 964, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__Sched1_scheduledPacket1__Scheduling_scheduledPacket1;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      makeNewRequest(&__req1, 963, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      __req1.asyncChannel = &__Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
      addRequestToList(&__list, &__req1);
      __params2[0] = &packet__address;
      __params2[1] = &packet__date;
      makeNewRequest(&__req2, 966, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params2);
      __req2.asyncChannel = &__Classification_queue_high__Scheduling_from_queue_high;
      addRequestToList(&__list, &__req2);
      __params3[0] = &packet__address;
      __params3[1] = &packet__date;
      makeNewRequest(&__req3, 968, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params3);
      __req3.asyncChannel = &__Classification_queue_low__Scheduling_from_queue_low;
      addRequestToList(&__list, &__req3);
      __params4[0] = &packet__address;
      __params4[1] = &packet__date;
      makeNewRequest(&__req4, 967, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params4);
      __req4.asyncChannel = &__Classification_queue_medium__Scheduling_from_queue_medium;
      addRequestToList(&__list, &__req4);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__SendToOutput;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__SendToOutput;
        
      }
      else  if (__returnRequest == &__req2) {
        __currentState = STATE__Read;
        
      }
      else  if (__returnRequest == &__req3) {
        __currentState = STATE__Read;
        
      }
      else  if (__returnRequest == &__req4) {
        __currentState = STATE__Read;
        
      }
      break;
      
      case STATE__Read: 
      waitFor((50)*1000, (50)*1000);
      __currentState = STATE__Dispatch;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

