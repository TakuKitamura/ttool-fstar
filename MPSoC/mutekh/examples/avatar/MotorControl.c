#include "MotorControl.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _motorCommand;

#define STATE__START__STATE 0
#define STATE__startMotor 1
#define STATE__STOP__STATE 2

void *mainFunc__MotorControl(struct mwmr_s *channels_MotorControl[]){
  
  struct mwmr_s *MainControl_motorCommand__MotorControl_motorCommand= channels_MotorControl[0];
  int rightVelocity = 0;
  int leftVelocity = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "MotorControl";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__startMotor;
      break;
      
      case STATE__startMotor: 
      waitFor((5)*1000, (10)*1000);
      __params0[0] = &leftVelocity;
      __params0[1] = &rightVelocity;
      makeNewRequest(&__req0, 66, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.asyncChannel = &__MainControl_motorCommand__MotorControl_motorCommand;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      waitFor((10)*1000, (20)*1000);
      __currentState = STATE__startMotor;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

