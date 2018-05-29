#include "Bootstrap.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _address;

#define STATE__START__STATE 0
#define STATE__choice__0 1
#define STATE__bootstrap 2
#define STATE__STOP__STATE 3

void *mainFunc__Bootstrap(struct mwmr_s *channels_Bootstrap[]){
  
  struct mwmr_s *Bootstrap_address__InputEngine_bootstrap= channels_Bootstrap[0];
  int address = 0;
  int counter = 16;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Bootstrap";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + bootstrap");
      __currentState = STATE__bootstrap;
      break;
      
      case STATE__choice__0: 
      if (counter>0) {
        makeNewRequest(&__req0, 725, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (counter == 0) {
        makeNewRequest(&__req1, 729, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + bootstrap");
        __currentState = STATE__bootstrap;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__STOP__STATE;
        
      }
      break;
      
      case STATE__bootstrap: 
      __params0[0] = &address;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 720, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Bootstrap_address__InputEngine_bootstrap;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      counter = counter-1;
      debug2Msg(__myname, "-> (=====) Entering state + choice__0");
      __currentState = STATE__choice__0;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

