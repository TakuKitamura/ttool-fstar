#include "SmartCardController.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _reset;
static uint32_t _pTS;
static uint32_t _fromTtoP;
static uint32_t _dataReady;
static uint32_t _fromDtoSC;
static uint32_t _answerToReset;
static uint32_t _pTSCConfirm;
static uint32_t _start_Application;
static uint32_t _data_Ready_SC;
static uint32_t _fromSCtoD;
static uint32_t _receive;
static uint32_t _fromPtoT;
static uint32_t _send;
static uint32_t _activation;
static uint32_t _start_TCPIP;

#define STATE__START__STATE 0
#define STATE__MainLoop 1
#define STATE__STOP__STATE 2

void *mainFunc__SmartCardController(struct mwmr_s *channels_SmartCardController[]){
  
  struct mwmr_s *SmartCardController_reset__InterfaceDevice_reset= channels_SmartCardController[0];
  struct mwmr_s *SmartCardController_activation__InterfaceDevice_activation= channels_SmartCardController[1];
  struct mwmr_s *SmartCardController_fromPtoT__TCPIP_fromPtoT= channels_SmartCardController[2];
  struct mwmr_s *SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD= channels_SmartCardController[3];
  struct mwmr_s *SmartCardController_data_Ready_SC__InterfaceDevice_dataReady= channels_SmartCardController[4];
  struct mwmr_s *SmartCardController_fromTtoP__TCPIP_fromTtoP= channels_SmartCardController[5];
  struct mwmr_s *SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC= channels_SmartCardController[6];
  struct mwmr_s *SmartCardController_start_Application__Application_startApplication= channels_SmartCardController[7];
  struct mwmr_s *SmartCardController_start_TCPIP__TCPIP_start= channels_SmartCardController[8];
  struct mwmr_s *SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm= channels_SmartCardController[9];
  struct mwmr_s *SmartCardController_pTS__InterfaceDevice_pTS= channels_SmartCardController[10];
  struct mwmr_s *SmartCardController_answerToReset__InterfaceDevice_answerToReset= channels_SmartCardController[11];
  bool packetIn = false;
  bool packetOut = false;
  int packet1__srcdest = 0;
  int packet1__seqNum = 0;
  int packet1__ackNum = 0;
  int packet1__control = 0;
  int packet1__management = 0;
  int packet1__checksum = 0;
  int packet1__othersAndPadding = 0;
  int packet1__data = 0;
  int packet2__srcdest = 0;
  int packet2__seqNum = 0;
  int packet2__ackNum = 0;
  int packet2__control = 0;
  int packet2__management = 0;
  int packet2__checksum = 0;
  int packet2__othersAndPadding = 0;
  int packet2__data = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[8];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[8];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[8];
  __attribute__((unused)) request __req3;
  __attribute__((unused))int *__params3[8];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "SmartCardController";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) test SmartCardController_activation__InterfaceDevice_activation");
      makeNewRequest(&__req0, 522, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_activation__InterfaceDevice_activation;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_reset__InterfaceDevice_reset");
      makeNewRequest(&__req0, 521, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_reset__InterfaceDevice_reset;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_answerToReset__InterfaceDevice_answerToReset");
      makeNewRequest(&__req0, 533, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_answerToReset__InterfaceDevice_answerToReset;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_pTS__InterfaceDevice_pTS");
      makeNewRequest(&__req0, 532, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_pTS__InterfaceDevice_pTS;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm");
      makeNewRequest(&__req0, 531, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_start_TCPIP__TCPIP_start");
      makeNewRequest(&__req0, 530, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_start_TCPIP__TCPIP_start;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_start_Application__Application_startApplication");
      makeNewRequest(&__req0, 529, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_start_Application__Application_startApplication;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + MainLoop");
      __currentState = STATE__MainLoop;
      break;
      
      case STATE__MainLoop: 
      if (packetOut) {
        debug2Msg(__myname, "-> (=====) test SmartCardController_data_Ready_SC__InterfaceDevice_dataReady");
        makeNewRequest(&__req0, 526, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__SmartCardController_data_Ready_SC__InterfaceDevice_dataReady;
        addRequestToList(&__list, &__req0);
      }
      if (packetIn) {
        __params1[0] = &packet1__srcdest;
        __params1[1] = &packet1__seqNum;
        __params1[2] = &packet1__ackNum;
        __params1[3] = &packet1__control;
        __params1[4] = &packet1__management;
        __params1[5] = &packet1__checksum;
        __params1[6] = &packet1__othersAndPadding;
        __params1[7] = &packet1__data;
        debug2Msg(__myname, "-> (=====) test SmartCardController_fromPtoT__TCPIP_fromPtoT");
        makeNewRequest(&__req1, 524, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params1);
        __req1.syncChannel = &__SmartCardController_fromPtoT__TCPIP_fromPtoT;
        addRequestToList(&__list, &__req1);
      }
      __params2[0] = &packet2__srcdest;
      __params2[1] = &packet2__seqNum;
      __params2[2] = &packet2__ackNum;
      __params2[3] = &packet2__control;
      __params2[4] = &packet2__management;
      __params2[5] = &packet2__checksum;
      __params2[6] = &packet2__othersAndPadding;
      __params2[7] = &packet2__data;
      debug2Msg(__myname, "-> (=====) test SmartCardController_fromTtoP__TCPIP_fromTtoP");
      makeNewRequest(&__req2, 527, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params2);
      __req2.syncChannel = &__SmartCardController_fromTtoP__TCPIP_fromTtoP;
      addRequestToList(&__list, &__req2);
      __params3[0] = &packet1__srcdest;
      __params3[1] = &packet1__seqNum;
      __params3[2] = &packet1__ackNum;
      __params3[3] = &packet1__control;
      __params3[4] = &packet1__management;
      __params3[5] = &packet1__checksum;
      __params3[6] = &packet1__othersAndPadding;
      __params3[7] = &packet1__data;
      debug2Msg(__myname, "-> (=====) test SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC");
      makeNewRequest(&__req3, 528, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params3);
      __req3.syncChannel = &__SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC;
      addRequestToList(&__list, &__req3);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __params0[0] = &packet2__srcdest;
        __params0[1] = &packet2__seqNum;
        __params0[2] = &packet2__ackNum;
        __params0[3] = &packet2__control;
        __params0[4] = &packet2__management;
        __params0[5] = &packet2__checksum;
        __params0[6] = &packet2__othersAndPadding;
        __params0[7] = &packet2__data;
        debug2Msg(__myname, "-> (=====) test SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD");
        makeNewRequest(&__req0, 525, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        packetOut = false;
        debug2Msg(__myname, "-> (=====) Entering state + MainLoop");
        __currentState = STATE__MainLoop;
        
      }
      else  if (__returnRequest == &__req1) {
        packetIn = false;
        debug2Msg(__myname, "-> (=====) Entering state + MainLoop");
        __currentState = STATE__MainLoop;
        
      }
      else  if (__returnRequest == &__req2) {
        packetOut = true;
        debug2Msg(__myname, "-> (=====) Entering state + MainLoop");
        __currentState = STATE__MainLoop;
        
      }
      else  if (__returnRequest == &__req3) {
        packetIn = true;
        debug2Msg(__myname, "-> (=====) Entering state + MainLoop");
        __currentState = STATE__MainLoop;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

