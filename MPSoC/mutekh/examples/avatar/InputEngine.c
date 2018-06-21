#include "InputEngine.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _packet;
static uint32_t _address;
static uint32_t _bootstrap;

#define STATE__START__STATE 0
#define STATE__Waiting 1
#define STATE__ProcessPacket 2
#define STATE__STOP__STATE 3

void *mainFunc__InputEngine(struct mwmr_s *channels_InputEngine[]){
  
  struct mwmr_s *OutputEngine_address__InputEngine_address= channels_InputEngine[0];
  struct mwmr_s *Bootstrap_address__InputEngine_bootstrap= channels_InputEngine[1];
  struct mwmr_s *InputEngine_packet__Classification_from_IE= channels_InputEngine[2];
  int packet__address = 0;
  int packet__date = 0;
  int address = 0;
  int frequency = 24;
  int priority = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "InputEngine";
  
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
      __params0[0] = &address;
      makeNewRequest(&__req0, 753, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.asyncChannel = &__Bootstrap_address__InputEngine_bootstrap;
      addRequestToList(&__list, &__req0);
      __params1[0] = &address;
      makeNewRequest(&__req1, 752, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params1);
      __req1.asyncChannel = &__OutputEngine_address__InputEngine_address;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__ProcessPacket;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__ProcessPacket;
        
      }
      break;
      
      case STATE__ProcessPacket: 
      waitFor((100)*1000, (100)*1000);
      priority = computeRandom(0, 2);
      packet__date = priority;
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 757, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__InputEngine_packet__Classification_from_IE;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __currentState = STATE__Waiting;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

