#include "MicroWaveOven.h"


// Header code defined in the model

// End of header code defined in the model


#define STATE__START__STATE 0
#define STATE__Running 1
#define STATE__STOP__STATE 2

void *mainFunc__MicroWaveOven(struct mwmr_s *channels_MicroWaveOven[]){
  
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[0];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "MicroWaveOven";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + Running");
      __currentState = STATE__Running;
      break;
      
      case STATE__Running: 
      __currentState = STATE__STOP__STATE;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

