#include "Magnetron.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _startM;
static uint32_t _stopM;

#define STATE__START__STATE 0
#define STATE__Running 1
#define STATE__WaitForStart 2
#define STATE__STOP__STATE 3

void *mainFunc__Magnetron(struct mwmr_s *channels_Magnetron[]){
  
  struct mwmr_s *Controller_startMagnetron__Magnetron_startM= channels_Magnetron[0];
  struct mwmr_s *Controller_stopMagnetron__Magnetron_stopM= channels_Magnetron[1];
  int power = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "Magnetron";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + WaitForStart");
      __currentState = STATE__WaitForStart;
      break;
      
      case STATE__Running: 
      debug2Msg(__myname, "-> (=====) test Controller_stopMagnetron__Magnetron_stopM");
      makeNewRequest(&__req0, 62, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Controller_stopMagnetron__Magnetron_stopM;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + WaitForStart");
      __currentState = STATE__WaitForStart;
      break;
      
      case STATE__WaitForStart: 
      debug2Msg(__myname, "-> (=====) test Controller_startMagnetron__Magnetron_startM");
      makeNewRequest(&__req0, 61, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params0);
      __req0.syncChannel = &__Controller_startMagnetron__Magnetron_startM;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Running");
      __currentState = STATE__Running;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

