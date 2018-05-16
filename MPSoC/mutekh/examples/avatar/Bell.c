#include "Bell.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _ring;

#define STATE__START__STATE 0
#define STATE__Active 1
#define STATE__STOP__STATE 2

void *mainFunc__Bell(struct mwmr_s *channels_Bell[]){
  
  struct mwmr_s *Controller_ringBell__Bell_ring= channels_Bell[0];
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Bell";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + Active");
      __currentState = STATE__Active;
      break;
      
      case STATE__Active: 
      debug2Msg(__myname, "-> (=====) test Controller_ringBell__Bell_ring");
      makeNewRequest(&__req0, 133, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Controller_ringBell__Bell_ring;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Active");
      __currentState = STATE__Active;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

