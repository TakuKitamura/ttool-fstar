#include "Door.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _closed;
static uint32_t _open;
static uint32_t _okDoor;

#define STATE__START__STATE 0
#define STATE__DoorIsOpened 1
#define STATE__IDLE 2
#define STATE__STOP__STATE 3

void *mainFunc__Door(struct mwmr_s *channels_Door[]){
  
  struct mwmr_s *Door_okDoor__Controller_okDoor= channels_Door[0];
  struct mwmr_s *Door_closed__Controller_closed= channels_Door[1];
  struct mwmr_s *Door_open__Controller_open= channels_Door[2];
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Door";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + IDLE");
      __currentState = STATE__IDLE;
      break;
      
      case STATE__DoorIsOpened: 
      waitFor((2)*1000, (4)*1000);
      debug2Msg(__myname, "-> (=====) test Door_closed__Controller_closed");
      makeNewRequest(&__req0, 50, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_closed__Controller_closed;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test Door_okDoor__Controller_okDoor");
      makeNewRequest(&__req0, 47, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_okDoor__Controller_okDoor;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      waitFor((2)*1000, (4)*1000);
      debug2Msg(__myname, "-> (=====) Entering state + IDLE");
      __currentState = STATE__IDLE;
      break;
      
      case STATE__IDLE: 
      debug2Msg(__myname, "-> (=====) test Door_open__Controller_open");
      makeNewRequest(&__req0, 51, SEND_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_open__Controller_open;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) test Door_okDoor__Controller_okDoor");
      makeNewRequest(&__req0, 48, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Door_okDoor__Controller_okDoor;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + DoorIsOpened");
      __currentState = STATE__DoorIsOpened;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

