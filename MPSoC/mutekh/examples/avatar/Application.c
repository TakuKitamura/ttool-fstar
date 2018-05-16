#include "Application.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _open;
static uint32_t _sendTCP;
static uint32_t _close;
static uint32_t _abort;
static uint32_t _startApplication;
static uint32_t _receiveTCP;

#define STATE__START__STATE 0
#define STATE__ReadingPackets 1
#define STATE__ClosingOrAborting 2
#define STATE__STOP__STATE 3

void *mainFunc__Application(struct mwmr_s *channels_Application[]){
  
  struct mwmr_s *TCPIP_receiveTCP__Application_receiveTCP= channels_Application[0];
  struct mwmr_s *SmartCardController_start_Application__Application_startApplication= channels_Application[1];
  struct mwmr_s *Application_abort__TCPIP_abort= channels_Application[2];
  struct mwmr_s *Application_close__TCPIP_close= channels_Application[3];
  struct mwmr_s *Application_sendTCP__TCPIP_send_TCP= channels_Application[4];
  struct mwmr_s *Application_open__TCPIP_open= channels_Application[5];
  int val = 7;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Application";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) test SmartCardController_start_Application__Application_startApplication");
      makeNewRequest(&__req0, 505, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_start_Application__Application_startApplication;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test Application_open__TCPIP_open");
      makeNewRequest(&__req0, 509, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Application_open__TCPIP_open;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __params0[0] = &val;
      debug2Msg(__myname, "-> (=====) test Application_sendTCP__TCPIP_send_TCP");
      makeNewRequest(&__req0, 508, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__Application_sendTCP__TCPIP_send_TCP;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      waitFor((5)*1000, (5)*1000);
      debug2Msg(__myname, "-> (=====) Entering state + ClosingOrAborting");
      __currentState = STATE__ClosingOrAborting;
      break;
      
      case STATE__ReadingPackets: 
      __params0[0] = &val;
      makeNewRequest(&__req0, 502, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.asyncChannel = &__TCPIP_receiveTCP__Application_receiveTCP;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + ReadingPackets");
      __currentState = STATE__ReadingPackets;
      break;
      
      case STATE__ClosingOrAborting: 
      debug2Msg(__myname, "-> (=====) test Application_close__TCPIP_close");
      makeNewRequest(&__req0, 507, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Application_close__TCPIP_close;
      addRequestToList(&__list, &__req0);
      debug2Msg(__myname, "-> (=====) test Application_abort__TCPIP_abort");
      makeNewRequest(&__req1, 506, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__Application_abort__TCPIP_abort;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + ReadingPackets");
        __currentState = STATE__ReadingPackets;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + ReadingPackets");
        __currentState = STATE__ReadingPackets;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

