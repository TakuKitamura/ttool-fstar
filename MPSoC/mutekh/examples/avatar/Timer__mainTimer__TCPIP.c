#include "Timer__mainTimer__TCPIP.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _set;
static uint32_t _reset;
static uint32_t _expire;

#define STATE__START__STATE 0
#define STATE__wait4set 1
#define STATE__wait4expire 2
#define STATE__STOP__STATE 3

void *mainFunc__Timer__mainTimer__TCPIP(struct mwmr_s *channels_Timer__mainTimer__TCPIP[]){
  
  struct mwmr_s *TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set= channels_Timer__mainTimer__TCPIP[0];
  struct mwmr_s *TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset= channels_Timer__mainTimer__TCPIP[1];
  struct mwmr_s *TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire= channels_Timer__mainTimer__TCPIP[2];
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
  
  char * __myname = "Timer__mainTimer__TCPIP";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + wait4set");
      __currentState = STATE__wait4set;
      break;
      
      case STATE__wait4set: 
      __params0[0] = &value;
      debug2Msg(__myname, "-> (=====) test TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set");
      makeNewRequest(&__req0, 576, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
      addRequestToList(&__list, &__req0);
      debug2Msg(__myname, "-> (=====) test TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset");
      makeNewRequest(&__req1, 578, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + wait4expire");
        __currentState = STATE__wait4expire;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + wait4set");
        __currentState = STATE__wait4set;
        
      }
      break;
      
      case STATE__wait4expire: 
      __params0[0] = &value;
      debug2Msg(__myname, "-> (=====) test TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set");
      makeNewRequest(&__req0, 577, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
      addRequestToList(&__list, &__req0);
      debug2Msg(__myname, "-> (=====) test TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire");
      makeNewRequest(&__req1, 580, SEND_SYNC_REQUEST, 1, (value)*1000, (value)*1000, 0, __params1);
      __req1.syncChannel = &__TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire;
      addRequestToList(&__list, &__req1);
      debug2Msg(__myname, "-> (=====) test TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset");
      makeNewRequest(&__req2, 579, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params2);
      __req2.syncChannel = &__TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset;
      addRequestToList(&__list, &__req2);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + wait4expire");
        __currentState = STATE__wait4expire;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + wait4set");
        __currentState = STATE__wait4set;
        
      }
      else  if (__returnRequest == &__req2) {
        debug2Msg(__myname, "-> (=====) Entering state + wait4set");
        __currentState = STATE__wait4set;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

