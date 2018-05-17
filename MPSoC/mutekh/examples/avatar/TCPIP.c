#include "TCPIP.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _start;
static uint32_t _abort;
static uint32_t _open;
static uint32_t _close;
static uint32_t _send_TCP;
static uint32_t _receiveTCP;
static uint32_t _fromPtoT;
static uint32_t _fromTtoP;
static uint32_t _addPacket;
static uint32_t _timeoutPacket;
static uint32_t _emptyListOfPackets;
static uint32_t _ackPacket;
static uint32_t _set__mainTimer;
static uint32_t _reset__mainTimer;
static uint32_t _expire__mainTimer;

#define STATE__START__STATE 0
#define STATE__choice__0 1
#define STATE__OpenStateUpdated 2
#define STATE__MainLoop 3
#define STATE__WaitingForEvent 4
#define STATE__STOP__STATE 5

void *mainFunc__TCPIP(struct mwmr_s *channels_TCPIP[]){
  
  struct mwmr_s *TCPIP_ackPacket__TCPPacketManager_ackPacket= channels_TCPIP[0];
  struct mwmr_s *TCPIP_receiveTCP__Application_receiveTCP= channels_TCPIP[1];
  struct mwmr_s *SmartCardController_fromPtoT__TCPIP_fromPtoT= channels_TCPIP[2];
  struct mwmr_s *TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket= channels_TCPIP[3];
  struct mwmr_s *Application_close__TCPIP_close= channels_TCPIP[4];
  struct mwmr_s *TCPIP_addPacket__TCPPacketManager_addPacket= channels_TCPIP[5];
  struct mwmr_s *Application_sendTCP__TCPIP_send_TCP= channels_TCPIP[6];
  struct mwmr_s *Application_open__TCPIP_open= channels_TCPIP[7];
  struct mwmr_s *Application_abort__TCPIP_abort= channels_TCPIP[8];
  struct mwmr_s *SmartCardController_start_TCPIP__TCPIP_start= channels_TCPIP[9];
  struct mwmr_s *SmartCardController_fromTtoP__TCPIP_fromTtoP= channels_TCPIP[10];
  struct mwmr_s *TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset= channels_TCPIP[11];
  struct mwmr_s *TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set= channels_TCPIP[12];
  int tcp__state = 0;
  int tcp__seqNum = 1;
  int tcp__wind = 1;
  int timerValue = 2;
  int val = 0;
  int STATES__CLOSED = 0;
  int STATES__OPENED = 1;
  int STATES__FOUR = 4;
  int STATES__NINE = 0;
  int computation = 3;
  int packet__srcdest = 0;
  int packet__seqNum = 0;
  int packet__ackNum = 0;
  int packet__control = 0;
  int packet__management = 0;
  int packet__checksum = 0;
  int packet__othersAndPadding = 0;
  int packet__data = 0;
  int __timerValue = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[8];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[8];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[8];
  __attribute__((unused)) request __req3;
  __attribute__((unused))int *__params3[8];
  __attribute__((unused)) request __req4;
  __attribute__((unused))int *__params4[8];
  __attribute__((unused)) request __req5;
  __attribute__((unused))int *__params5[8];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "TCPIP";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) test SmartCardController_start_TCPIP__TCPIP_start");
      makeNewRequest(&__req0, 329, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_start_TCPIP__TCPIP_start;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
      __currentState = STATE__WaitingForEvent;
      break;
      
      case STATE__choice__0: 
      if (packet__control == 0) {
        __params0[0] = &val;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 317, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__TCPIP_receiveTCP__Application_receiveTCP;
        addRequestToList(&__list, &__req0);
      }
      if (!(packet__control == 0)) {
        __params1[0] = &packet__srcdest;
        __params1[1] = &packet__seqNum;
        __params1[2] = &packet__ackNum;
        __params1[3] = &packet__control;
        __params1[4] = &packet__management;
        __params1[5] = &packet__checksum;
        __params1[6] = &packet__othersAndPadding;
        __params1[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test TCPIP_ackPacket__TCPPacketManager_ackPacket");
        makeNewRequest(&__req1, 315, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params1);
        __req1.syncChannel = &__TCPIP_ackPacket__TCPPacketManager_ackPacket;
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
        debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
        __currentState = STATE__WaitingForEvent;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
        __currentState = STATE__WaitingForEvent;
        
      }
      break;
      
      case STATE__OpenStateUpdated: 
      debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
      __currentState = STATE__WaitingForEvent;
      break;
      
      case STATE__MainLoop: 
      __currentState = STATE__STOP__STATE;
      break;
      
      case STATE__WaitingForEvent: 
      __params0[0] = &packet__srcdest;
      __params0[1] = &packet__seqNum;
      __params0[2] = &packet__ackNum;
      __params0[3] = &packet__control;
      __params0[4] = &packet__management;
      __params0[5] = &packet__checksum;
      __params0[6] = &packet__othersAndPadding;
      __params0[7] = &packet__data;
      debug2Msg(__myname, "-> (=====) test SmartCardController_fromPtoT__TCPIP_fromPtoT");
      makeNewRequest(&__req0, 318, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params0);
      __req0.syncChannel = &__SmartCardController_fromPtoT__TCPIP_fromPtoT;
      addRequestToList(&__list, &__req0);
      if (tcp__state == STATES__OPENED) {
        __params1[0] = &val;
        debug2Msg(__myname, "-> (=====) test Application_sendTCP__TCPIP_send_TCP");
        makeNewRequest(&__req1, 324, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params1);
        __req1.syncChannel = &__Application_sendTCP__TCPIP_send_TCP;
        addRequestToList(&__list, &__req1);
      }
      debug2Msg(__myname, "-> (=====) test Application_abort__TCPIP_abort");
      makeNewRequest(&__req2, 327, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params2);
      __req2.syncChannel = &__Application_abort__TCPIP_abort;
      addRequestToList(&__list, &__req2);
      if (tcp__state == STATES__CLOSED) {
        debug2Msg(__myname, "-> (=====) test Application_open__TCPIP_open");
        makeNewRequest(&__req3, 326, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params3);
        __req3.syncChannel = &__Application_open__TCPIP_open;
        addRequestToList(&__list, &__req3);
      }
      debug2Msg(__myname, "-> (=====) test Application_close__TCPIP_close");
      makeNewRequest(&__req4, 321, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params4);
      __req4.syncChannel = &__Application_close__TCPIP_close;
      addRequestToList(&__list, &__req4);
      __params5[0] = &packet__srcdest;
      __params5[1] = &packet__seqNum;
      __params5[2] = &packet__ackNum;
      __params5[3] = &packet__control;
      __params5[4] = &packet__management;
      __params5[5] = &packet__checksum;
      __params5[6] = &packet__othersAndPadding;
      __params5[7] = &packet__data;
      debug2Msg(__myname, "-> (=====) test TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket");
      makeNewRequest(&__req5, 319, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params5);
      __req5.syncChannel = &__TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket;
      addRequestToList(&__list, &__req5);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + choice__0");
        __currentState = STATE__choice__0;
        
      }
      else  if (__returnRequest == &__req1) {
        waitFor((computation)*1000, (computation)*1000);
        packet__seqNum = tcp__seqNum;
        tcp__seqNum = tcp__seqNum+tcp__wind;
        packet__data = val;
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test SmartCardController_fromTtoP__TCPIP_fromTtoP");
        makeNewRequest(&__req0, 333, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__SmartCardController_fromTtoP__TCPIP_fromTtoP;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test TCPIP_addPacket__TCPPacketManager_addPacket");
        makeNewRequest(&__req0, 323, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__TCPIP_addPacket__TCPPacketManager_addPacket;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __timerValue = timerValue;
        __params0[0] = &__timerValue;
        debug2Msg(__myname, "-> (=====) test TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set");
        makeNewRequest(&__req0, 595, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
        __req0.syncChannel = &__TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
        __currentState = STATE__WaitingForEvent;
        
      }
      else  if (__returnRequest == &__req2) {
        tcp__state = STATES__CLOSED;
        debug2Msg(__myname, "-> (=====) test TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset");
        makeNewRequest(&__req0, 594, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
        __currentState = STATE__WaitingForEvent;
        
      }
      else  if (__returnRequest == &__req3) {
        if (!tcp__state == STATES__CLOSED) {
          debug2Msg(__myname, "Guard failed: tcp__state == STATES__CLOSED");
          __currentState = STATE__STOP__STATE;
          break;
        }
        tcp__state = STATES__OPENED;
        tcp__seqNum = 0;
        debug2Msg(__myname, "-> (=====) Entering state + OpenStateUpdated");
        __currentState = STATE__OpenStateUpdated;
        
      }
      else  if (__returnRequest == &__req4) {
        tcp__state = STATES__CLOSED;
        debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
        __currentState = STATE__WaitingForEvent;
        
      }
      else  if (__returnRequest == &__req5) {
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test SmartCardController_fromTtoP__TCPIP_fromTtoP");
        makeNewRequest(&__req0, 332, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__SmartCardController_fromTtoP__TCPIP_fromTtoP;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test TCPIP_addPacket__TCPPacketManager_addPacket");
        makeNewRequest(&__req0, 335, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__TCPIP_addPacket__TCPPacketManager_addPacket;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __timerValue = timerValue;
        __params0[0] = &__timerValue;
        debug2Msg(__myname, "-> (=====) test TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set");
        makeNewRequest(&__req0, 598, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
        __req0.syncChannel = &__TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + WaitingForEvent");
        __currentState = STATE__WaitingForEvent;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

