#include "Classif0.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _to_queue_low;
static uint32_t _to_queue_medium;
static uint32_t _to_queue_high;
static uint32_t _from_classif;

#define STATE__START__STATE 0
#define STATE__Waiting 1
#define STATE__Classify 2
#define STATE__STOP__STATE 3

void *mainFunc__Classif0(struct mwmr_s *channels_Classif0[]){
  
  struct mwmr_s *Classif0_from_classif__Classification_to_c0= channels_Classif0[0];
  struct mwmr_s *Classif0_to_queue_high__Classification_c0_to_queue_high= channels_Classif0[1];
  struct mwmr_s *Classif0_to_queue_medium__Classification_c0_to_queue_medium= channels_Classif0[2];
  struct mwmr_s *Classif0_to_queue_low__Classification_c0_to_queue_low= channels_Classif0[3];
  int packet__address = 0;
  int packet__date = 0;
  int nbPackets = 0;
  
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
  
  char * __myname = "Classif0";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__Waiting;
      break;
      
      case STATE__Waiting: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      makeNewRequest(&__req0, 914, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__Classif0_from_classif__Classification_to_c0;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __currentState = STATE__Classify;
      break;
      
      case STATE__Classify: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 915, SEND_ASYNC_REQUEST, 1, (50)*1000, (100)*1000, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Classif0_to_queue_high__Classification_c0_to_queue_high;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req1);
      makeNewRequest(&__req1, 917, SEND_ASYNC_REQUEST, 1, (50)*1000, (100)*1000, 2, __params1);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req1.asyncChannel = &__Classif0_to_queue_low__Classification_c0_to_queue_low;
      addRequestToList(&__list, &__req1);
      __params2[0] = &packet__address;
      __params2[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req2);
      makeNewRequest(&__req2, 916, SEND_ASYNC_REQUEST, 1, (50)*1000, (100)*1000, 2, __params2);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req2.asyncChannel = &__Classif0_to_queue_medium__Classification_c0_to_queue_medium;
      addRequestToList(&__list, &__req2);
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
      else  if (__returnRequest == &__req2) {
        __currentState = STATE__Waiting;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

