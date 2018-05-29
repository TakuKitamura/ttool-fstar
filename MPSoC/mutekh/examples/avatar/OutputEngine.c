#include "OutputEngine.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _address;
static uint32_t _packet;

#define STATE__START__STATE 0
#define STATE__FreePacket 1
#define STATE__Waiting 2
#define STATE__STOP__STATE 3

void *mainFunc__OutputEngine(struct mwmr_s *channels_OutputEngine[]){
  
  struct mwmr_s *OutputEngine_address__InputEngine_address= channels_OutputEngine[0];
  struct mwmr_s *Scheduling_packet__OutputEngine_packet= channels_OutputEngine[1];
  int packet__address = 0;
  int packet__date = 0;
  int address = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "OutputEngine";
  
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
      
      case STATE__FreePacket: 
      waitFor((10)*1000, (10)*1000);
      __params0[0] = &address;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 742, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__OutputEngine_address__InputEngine_address;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Waiting");
      __currentState = STATE__Waiting;
      break;
      
      case STATE__Waiting: 
      waitFor((50)*1000, (50)*1000);
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      makeNewRequest(&__req0, 745, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__Scheduling_packet__OutputEngine_packet;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + FreePacket");
      __currentState = STATE__FreePacket;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

