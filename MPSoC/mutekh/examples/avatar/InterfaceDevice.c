#include "InterfaceDevice.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _activation;
static uint32_t _reset;
static uint32_t _pTS;
static uint32_t _fromDtoSC;
static uint32_t _data_Ready;
static uint32_t _answerToReset;
static uint32_t _pTSConfirm;
static uint32_t _fromSCtoD;
static uint32_t _dataReady;

#define STATE__START__STATE 0
#define STATE__ChoiceBetweenInputOutput 1
#define STATE__DataTransferLoop 2
#define STATE__STOP__STATE 3

void *mainFunc__InterfaceDevice(struct mwmr_s *channels_InterfaceDevice[]){
  
  struct mwmr_s *SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD= channels_InterfaceDevice[0];
  struct mwmr_s *SmartCardController_data_Ready_SC__InterfaceDevice_dataReady= channels_InterfaceDevice[1];
  struct mwmr_s *SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC= channels_InterfaceDevice[2];
  struct mwmr_s *SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm= channels_InterfaceDevice[3];
  struct mwmr_s *SmartCardController_pTS__InterfaceDevice_pTS= channels_InterfaceDevice[4];
  struct mwmr_s *SmartCardController_answerToReset__InterfaceDevice_answerToReset= channels_InterfaceDevice[5];
  struct mwmr_s *SmartCardController_reset__InterfaceDevice_reset= channels_InterfaceDevice[6];
  struct mwmr_s *SmartCardController_activation__InterfaceDevice_activation= channels_InterfaceDevice[7];
  int val = 5;
  int cpt = 0;
  int nbOfComputedPackets = 1;
  int packet__srcdest = 0;
  int packet__seqNum = 0;
  int packet__ackNum = 0;
  int packet__control = 0;
  int packet__management = 0;
  int packet__checksum = 0;
  int packet__othersAndPadding = 0;
  int packet__data = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[8];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[8];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "InterfaceDevice";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) test SmartCardController_activation__InterfaceDevice_activation");
      makeNewRequest(&__req0, 284, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_activation__InterfaceDevice_activation;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_reset__InterfaceDevice_reset");
      makeNewRequest(&__req0, 283, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_reset__InterfaceDevice_reset;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_answerToReset__InterfaceDevice_answerToReset");
      makeNewRequest(&__req0, 282, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_answerToReset__InterfaceDevice_answerToReset;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_pTS__InterfaceDevice_pTS");
      makeNewRequest(&__req0, 281, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_pTS__InterfaceDevice_pTS;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm");
      makeNewRequest(&__req0, 280, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      cpt = 0;
      debug2Msg(__myname, "-> (=====) Entering state + DataTransferLoop");
      __currentState = STATE__DataTransferLoop;
      break;
      
      case STATE__ChoiceBetweenInputOutput: 
      debug2Msg(__myname, "-> (=====) test SmartCardController_data_Ready_SC__InterfaceDevice_dataReady");
      makeNewRequest(&__req0, 277, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__SmartCardController_data_Ready_SC__InterfaceDevice_dataReady;
      addRequestToList(&__list, &__req0);
      makeNewRequest(&__req1, 307, IMMEDIATE, 0, 0, 0, 0, __params1);
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD");
        makeNewRequest(&__req0, 276, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + DataTransferLoop");
        __currentState = STATE__DataTransferLoop;
        
      }
      else  if (__returnRequest == &__req1) {
        packet__seqNum = cpt;
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC");
        makeNewRequest(&__req0, 278, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.syncChannel = &__SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + DataTransferLoop");
        __currentState = STATE__DataTransferLoop;
        
      }
      break;
      
      case STATE__DataTransferLoop: 
      if (!(cpt<nbOfComputedPackets)) {
        makeNewRequest(&__req0, 297, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (cpt<nbOfComputedPackets) {
        makeNewRequest(&__req1, 298, IMMEDIATE, 1, (3)*1000, (3)*1000, 0, __params1);
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
        __currentState = STATE__STOP__STATE;
        
      }
      else  if (__returnRequest == &__req1) {
        cpt = cpt+1;
        debug2Msg(__myname, "-> (=====) Entering state + ChoiceBetweenInputOutput");
        __currentState = STATE__ChoiceBetweenInputOutput;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

