#include "ControlPanel.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _LEDOn;
static uint32_t _LEDoff;
static uint32_t _startButton;

#define STATE__START__STATE 0
#define STATE__Active 1
#define STATE__STOP__STATE 2

void *mainFunc__ControlPanel(struct mwmr_s *channels_ControlPanel[]){
  
  struct mwmr_s *ControlPanel_startButton__Controller_start= channels_ControlPanel[0];
  int duration = 5;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "ControlPanel";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + Active");
      __currentState = STATE__Active;
      break;
      
      case STATE__Active: 
      __params0[0] = &duration;
      debug2Msg(__myname, "-> (=====) test ControlPanel_startButton__Controller_start");
      makeNewRequest(&__req0, 128, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__ControlPanel_startButton__Controller_start;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      debug2Msg(__myname, "-> (=====) Entering state + Active");
      __currentState = STATE__Active;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

