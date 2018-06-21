#include "Block1.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _val;

#define STATE__START__STATE 0
#define STATE__state0 1
#define STATE__STOP__STATE 2

void *mainFunc__Block1(struct mwmr_s *channels_Block1[]){
  
  struct mwmr_s *Block0_val__Block1_val= channels_Block1[0];
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Block1";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__state0;
      break;
      
      case STATE__state0: 
      makeNewRequest(&__req0, 11, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.asyncChannel = &__Block0_val__Block1_val;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __currentState = STATE__STOP__STATE;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

