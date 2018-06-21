#include "Classification.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _queue_low;
static uint32_t _queue_medium;
static uint32_t _queue_high;
static uint32_t _c0_to_queue_low;
static uint32_t _c1_to_queue_low;
static uint32_t _c2_to_queue_low;
static uint32_t _c0_to_queue_medium;
static uint32_t _c1_to_queue_medium;
static uint32_t _c2_to_queue_medium;
static uint32_t _c0_to_queue_high;
static uint32_t _c1_to_queue_high;
static uint32_t _c2_to_queue_high;
static uint32_t _from_IE;
static uint32_t _to_c0;
static uint32_t _to_c1;
static uint32_t _to_c2;

#define STATE__START__STATE 0
#define STATE__recQueue 1
#define STATE__sendPacket 2
#define STATE__state1 3
#define STATE__WaitForPacket 4
#define STATE__High 5
#define STATE__Medium 6
#define STATE__Waiting 7
#define STATE__Low 8
#define STATE__STOP__STATE 9

void *mainFunc__Classification(struct mwmr_s *channels_Classification[]){
  
  struct mwmr_s *Classif2_from_classif__Classification_to_c2= channels_Classification[0];
  struct mwmr_s *Classif1_from_classif__Classification_to_c1= channels_Classification[1];
  struct mwmr_s *Classif0_from_classif__Classification_to_c0= channels_Classification[2];
  struct mwmr_s *InputEngine_packet__Classification_from_IE= channels_Classification[3];
  struct mwmr_s *Classification_queue_low__Scheduling_from_queue_low= channels_Classification[4];
  struct mwmr_s *Classif2_to_queue_high__Classification_c2_to_queue_high= channels_Classification[5];
  struct mwmr_s *Classif1_to_queue_high__Classification_c1_to_queue_high= channels_Classification[6];
  struct mwmr_s *Classif0_to_queue_high__Classification_c0_to_queue_high= channels_Classification[7];
  struct mwmr_s *Classif1_to_queue_medium__Classification_c1_to_queue_medium= channels_Classification[8];
  struct mwmr_s *Classif2_to_queue_medium__Classification_c2_to_queue_medium= channels_Classification[9];
  struct mwmr_s *Classif0_to_queue_medium__Classification_c0_to_queue_medium= channels_Classification[10];
  struct mwmr_s *Classification_queue_high__Scheduling_from_queue_high= channels_Classification[11];
  struct mwmr_s *Classif0_to_queue_low__Classification_c0_to_queue_low= channels_Classification[12];
  struct mwmr_s *Classification_queue_medium__Scheduling_from_queue_medium= channels_Classification[13];
  struct mwmr_s *Classif1_to_queue_low__Classification_c1_to_queue_low= channels_Classification[14];
  struct mwmr_s *Classif2_to_queue_low__Classification_c2_to_queue_low= channels_Classification[15];
  int packet__address = 0;
  int packet__date = 0;
  bool f1 = true;
  bool f0 = true;
  bool f2 = true;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[2];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[2];
  __attribute__((unused)) request __req3;
  __attribute__((unused))int *__params3[2];
  __attribute__((unused)) request __req4;
  __attribute__((unused))int *__params4[2];
  __attribute__((unused)) request __req5;
  __attribute__((unused))int *__params5[2];
  __attribute__((unused)) request __req6;
  __attribute__((unused))int *__params6[2];
  __attribute__((unused)) request __req7;
  __attribute__((unused))int *__params7[2];
  __attribute__((unused)) request __req8;
  __attribute__((unused))int *__params8[2];
  __attribute__((unused)) request __req9;
  __attribute__((unused))int *__params9[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Classification";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__Waiting;
      break;
      
      case STATE__recQueue: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      makeNewRequest(&__req0, 777, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__Classif2_to_queue_high__Classification_c2_to_queue_high;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      makeNewRequest(&__req1, 778, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      __req1.asyncChannel = &__Classif1_to_queue_high__Classification_c1_to_queue_high;
      addRequestToList(&__list, &__req1);
      __params2[0] = &packet__address;
      __params2[1] = &packet__date;
      makeNewRequest(&__req2, 779, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params2);
      __req2.asyncChannel = &__Classif0_to_queue_high__Classification_c0_to_queue_high;
      addRequestToList(&__list, &__req2);
      __params3[0] = &packet__address;
      __params3[1] = &packet__date;
      makeNewRequest(&__req3, 782, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params3);
      __req3.asyncChannel = &__Classif0_to_queue_medium__Classification_c0_to_queue_medium;
      addRequestToList(&__list, &__req3);
      __params4[0] = &packet__address;
      __params4[1] = &packet__date;
      makeNewRequest(&__req4, 789, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params4);
      __req4.asyncChannel = &__Classif0_to_queue_low__Classification_c0_to_queue_low;
      addRequestToList(&__list, &__req4);
      __params5[0] = &packet__address;
      __params5[1] = &packet__date;
      makeNewRequest(&__req5, 781, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params5);
      __req5.asyncChannel = &__Classif2_to_queue_medium__Classification_c2_to_queue_medium;
      addRequestToList(&__list, &__req5);
      __params6[0] = &packet__address;
      __params6[1] = &packet__date;
      makeNewRequest(&__req6, 787, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params6);
      __req6.asyncChannel = &__Classif2_to_queue_low__Classification_c2_to_queue_low;
      addRequestToList(&__list, &__req6);
      __params7[0] = &packet__address;
      __params7[1] = &packet__date;
      makeNewRequest(&__req7, 780, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params7);
      __req7.asyncChannel = &__Classif1_to_queue_medium__Classification_c1_to_queue_medium;
      addRequestToList(&__list, &__req7);
      __params8[0] = &packet__address;
      __params8[1] = &packet__date;
      makeNewRequest(&__req8, 788, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params8);
      __req8.asyncChannel = &__Classif1_to_queue_low__Classification_c1_to_queue_low;
      addRequestToList(&__list, &__req8);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        f2 = true;
        __currentState = STATE__High;
        
      }
      else  if (__returnRequest == &__req1) {
        f1 = true;
        __currentState = STATE__High;
        
      }
      else  if (__returnRequest == &__req2) {
        f0 = true;
        __currentState = STATE__High;
        
      }
      else  if (__returnRequest == &__req3) {
        f0 = true;
        __currentState = STATE__Medium;
        
      }
      else  if (__returnRequest == &__req4) {
        f0 = true;
        __currentState = STATE__Low;
        
      }
      else  if (__returnRequest == &__req5) {
        f2 = true;
        __currentState = STATE__Medium;
        
      }
      else  if (__returnRequest == &__req6) {
        f2 = true;
        __currentState = STATE__Low;
        
      }
      else  if (__returnRequest == &__req7) {
        f1 = true;
        __currentState = STATE__Medium;
        
      }
      else  if (__returnRequest == &__req8) {
        f1 = true;
        __currentState = STATE__Low;
        
      }
      break;
      
      case STATE__sendPacket: 
      if (f0) {
        __params0[0] = &packet__address;
        __params0[1] = &packet__date;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 773, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__Classif0_from_classif__Classification_to_c0;
        addRequestToList(&__list, &__req0);
      }
      if (f2) {
        __params1[0] = &packet__address;
        __params1[1] = &packet__date;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req1);
        makeNewRequest(&__req1, 770, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req1.asyncChannel = &__Classif2_from_classif__Classification_to_c2;
        addRequestToList(&__list, &__req1);
      }
      if (f1) {
        __params2[0] = &packet__address;
        __params2[1] = &packet__date;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req2);
        makeNewRequest(&__req2, 772, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params2);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req2.asyncChannel = &__Classif1_from_classif__Classification_to_c1;
        addRequestToList(&__list, &__req2);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        f0 = false;
        __currentState = STATE__state1;
        
      }
      else  if (__returnRequest == &__req1) {
        f2 = false;
        __currentState = STATE__state1;
        
      }
      else  if (__returnRequest == &__req2) {
        f1 = false;
        __currentState = STATE__state1;
        
      }
      break;
      
      case STATE__state1: 
      __currentState = STATE__Waiting;
      break;
      
      case STATE__WaitForPacket: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      makeNewRequest(&__req0, 793, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__Classif2_to_queue_high__Classification_c2_to_queue_high;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      makeNewRequest(&__req1, 794, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      __req1.asyncChannel = &__Classif1_to_queue_high__Classification_c1_to_queue_high;
      addRequestToList(&__list, &__req1);
      __params2[0] = &packet__address;
      __params2[1] = &packet__date;
      makeNewRequest(&__req2, 795, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params2);
      __req2.asyncChannel = &__Classif0_to_queue_high__Classification_c0_to_queue_high;
      addRequestToList(&__list, &__req2);
      __params3[0] = &packet__address;
      __params3[1] = &packet__date;
      makeNewRequest(&__req3, 797, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params3);
      __req3.asyncChannel = &__Classif2_to_queue_medium__Classification_c2_to_queue_medium;
      addRequestToList(&__list, &__req3);
      __params4[0] = &packet__address;
      __params4[1] = &packet__date;
      makeNewRequest(&__req4, 796, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params4);
      __req4.asyncChannel = &__Classif1_to_queue_medium__Classification_c1_to_queue_medium;
      addRequestToList(&__list, &__req4);
      __params5[0] = &packet__address;
      __params5[1] = &packet__date;
      makeNewRequest(&__req5, 798, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params5);
      __req5.asyncChannel = &__Classif0_to_queue_medium__Classification_c0_to_queue_medium;
      addRequestToList(&__list, &__req5);
      __params6[0] = &packet__address;
      __params6[1] = &packet__date;
      makeNewRequest(&__req6, 801, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params6);
      __req6.asyncChannel = &__Classif2_to_queue_low__Classification_c2_to_queue_low;
      addRequestToList(&__list, &__req6);
      __params7[0] = &packet__address;
      __params7[1] = &packet__date;
      makeNewRequest(&__req7, 802, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params7);
      __req7.asyncChannel = &__Classif1_to_queue_low__Classification_c1_to_queue_low;
      addRequestToList(&__list, &__req7);
      __params8[0] = &packet__address;
      __params8[1] = &packet__date;
      makeNewRequest(&__req8, 803, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params8);
      __req8.asyncChannel = &__Classif0_to_queue_low__Classification_c0_to_queue_low;
      addRequestToList(&__list, &__req8);
      __params9[0] = &packet__address;
      __params9[1] = &packet__date;
      makeNewRequest(&__req9, 775, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params9);
      __req9.asyncChannel = &__InputEngine_packet__Classification_from_IE;
      addRequestToList(&__list, &__req9);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        f2 = true;
        __currentState = STATE__High;
        
      }
      else  if (__returnRequest == &__req1) {
        f1 = true;
        __currentState = STATE__High;
        
      }
      else  if (__returnRequest == &__req2) {
        f0 = true;
        __currentState = STATE__High;
        
      }
      else  if (__returnRequest == &__req3) {
        f2 = true;
        __currentState = STATE__Medium;
        
      }
      else  if (__returnRequest == &__req4) {
        f1 = true;
        __currentState = STATE__Medium;
        
      }
      else  if (__returnRequest == &__req5) {
        f0 = true;
        __currentState = STATE__Medium;
        
      }
      else  if (__returnRequest == &__req6) {
        f2 = true;
        __currentState = STATE__Low;
        
      }
      else  if (__returnRequest == &__req7) {
        f1 = true;
        __currentState = STATE__Low;
        
      }
      else  if (__returnRequest == &__req8) {
        f0 = true;
        __currentState = STATE__Low;
        
      }
      else  if (__returnRequest == &__req9) {
        waitFor((10)*1000, (20)*1000);
        __currentState = STATE__sendPacket;
        
      }
      break;
      
      case STATE__High: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 799, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Classification_queue_high__Scheduling_from_queue_high;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req1);
      makeNewRequest(&__req1, 783, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req1.asyncChannel = &__Classification_queue_high__Scheduling_from_queue_high;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__Waiting;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__Waiting;
        
      }
      break;
      
      case STATE__Medium: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 800, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Classification_queue_medium__Scheduling_from_queue_medium;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req1);
      makeNewRequest(&__req1, 785, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req1.asyncChannel = &__Classification_queue_medium__Scheduling_from_queue_medium;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__Waiting;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__Waiting;
        
      }
      break;
      
      case STATE__Waiting: 
      if (!(f0||f1||f2)) {
        makeNewRequest(&__req0, 847, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (f0||f1||f2) {
        makeNewRequest(&__req1, 869, IMMEDIATE, 0, 0, 0, 0, __params1);
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
        __currentState = STATE__recQueue;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__WaitForPacket;
        
      }
      break;
      
      case STATE__Low: 
      __params0[0] = &packet__address;
      __params0[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 804, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__Classification_queue_low__Scheduling_from_queue_low;
      addRequestToList(&__list, &__req0);
      __params1[0] = &packet__address;
      __params1[1] = &packet__date;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req1);
      makeNewRequest(&__req1, 791, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params1);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req1.asyncChannel = &__Classification_queue_low__Scheduling_from_queue_low;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__Waiting;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__Waiting;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

