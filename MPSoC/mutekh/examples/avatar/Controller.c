#include "Controller.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _start;
static uint32_t _closed;
static uint32_t _open;
static uint32_t _ringBell;
static uint32_t _startMagnetron;
static uint32_t _stopMagnetron;
static uint32_t _startCooking;
static uint32_t _stopCooking;
static uint32_t _okDoor;

#define STATE__START__STATE 0
#define STATE__Starting 1
#define STATE__Heating 2
#define STATE__Idle 3
#define STATE__DoorOpened 4
#define STATE__DoorOpenedWhileHeating 5
#define STATE__STOP__STATE 6

void *mainFunc__Controller(struct mwmr_s *channels_Controller[]){
  
  struct mwmr_s *Door_okDoor__Controller_okDoor= channels_Controller[0];
  struct mwmr_s *Door_open__Controller_open= channels_Controller[1];
  struct mwmr_s *Controller_ringBell__Bell_ring= channels_Controller[2];
  struct mwmr_s *ControlPanel_startButton__Controller_start= channels_Controller[3];
  struct mwmr_s *Controller_stopMagnetron__Magnetron_stopM= channels_Controller[4];
  struct mwmr_s *Controller_startMagnetron__Magnetron_startM= channels_Controller[5];
  struct mwmr_s *Door_closed__Controller_closed= channels_Controller[6];
  int duration = 5;
  int remainingTime = 0;
  
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
  
  char * __myname = "Controller";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + Idle");
      __currentState = STATE__Idle;
      break;
      
      case STATE__Starting: 
      debug2Msg(__myname, "-> (=====) test Controller_startMagnetron__Magnetron_startM");
      makeNewRequest(&__req0, 86, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Controller_startMagnetron__Magnetron_startM;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      remainingTime = duration;
      debug2Msg(__myname, "-> (=====) Entering state + Heating");
      __currentState = STATE__Heating;
      break;
      
      case STATE__Heating: 
      if (remainingTime>0) {
        makeNewRequest(&__req0, 101, IMMEDIATE, 1, (1)*1000, (1)*1000, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      debug2Msg(__myname, "-> (=====) test Door_open__Controller_open");
      makeNewRequest(&__req1, 76, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__Door_open__Controller_open;
      addRequestToList(&__list, &__req1);
      if (remainingTime == 0) {
        debug2Msg(__myname, "-> (=====) test Controller_stopMagnetron__Magnetron_stopM");
        makeNewRequest(&__req2, 84, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params2);
        __req2.syncChannel = &__Controller_stopMagnetron__Magnetron_stopM;
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
        remainingTime = remainingTime-1;
        debug2Msg(__myname, "-> (=====) Entering state + Heating");
        __currentState = STATE__Heating;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) test Controller_stopMagnetron__Magnetron_stopM");
        makeNewRequest(&__req0, 83, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__Controller_stopMagnetron__Magnetron_stopM;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) test Door_okDoor__Controller_okDoor");
        makeNewRequest(&__req0, 72, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__Door_okDoor__Controller_okDoor;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + DoorOpenedWhileHeating");
        __currentState = STATE__DoorOpenedWhileHeating;
        
      }
      else  if (__returnRequest == &__req2) {
        debug2Msg(__myname, "-> (=====) test Controller_ringBell__Bell_ring");
        makeNewRequest(&__req0, 78, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__Controller_ringBell__Bell_ring;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + Idle");
        __currentState = STATE__Idle;
        
      }
      break;
      
      case STATE__Idle: 
      debug2Msg(__myname, "-> (=====) test Door_open__Controller_open");
      makeNewRequest(&__req0, 79, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_open__Controller_open;
      addRequestToList(&__list, &__req0);
      __params1[0] = &duration;
      debug2Msg(__myname, "-> (=====) test ControlPanel_startButton__Controller_start");
      makeNewRequest(&__req1, 81, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params1);
      __req1.syncChannel = &__ControlPanel_startButton__Controller_start;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) test Door_okDoor__Controller_okDoor");
        makeNewRequest(&__req0, 74, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
        __req0.syncChannel = &__Door_okDoor__Controller_okDoor;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + DoorOpened");
        __currentState = STATE__DoorOpened;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + Starting");
        __currentState = STATE__Starting;
        
      }
      break;
      
      case STATE__DoorOpened: 
      debug2Msg(__myname, "-> (=====) test Door_closed__Controller_closed");
      makeNewRequest(&__req0, 88, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_closed__Controller_closed;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test Door_okDoor__Controller_okDoor");
      makeNewRequest(&__req0, 73, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_okDoor__Controller_okDoor;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Idle");
      __currentState = STATE__Idle;
      break;
      
      case STATE__DoorOpenedWhileHeating: 
      debug2Msg(__myname, "-> (=====) test Door_closed__Controller_closed");
      makeNewRequest(&__req0, 87, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_closed__Controller_closed;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test Door_okDoor__Controller_okDoor");
      makeNewRequest(&__req0, 71, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_okDoor__Controller_okDoor;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test Controller_startMagnetron__Magnetron_startM");
      makeNewRequest(&__req0, 85, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Controller_startMagnetron__Magnetron_startM;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Heating");
      __currentState = STATE__Heating;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

