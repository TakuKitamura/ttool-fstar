#include "SpeedSensor.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _updateOnSpeed;

#define STATE__START__STATE 0
#define STATE__WaitingForSpeedUpdate 1
#define STATE__STOP__STATE 2

void *mainFunc__SpeedSensor(struct mwmr_s *channels_SpeedSensor[]){
  
  struct mwmr_s *SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed= channels_SpeedSensor[0];
  int minSpeedUpdate = 150;
  int maxSpeedUpdate = 150;
  int speed = 0;
  int carinfo__minID = 1;
  int carinfo__maxID = 5;
  int carinfo__minPosition = 3;
  int carinfo__maxPosition = 10;
  int carinfo__minSpeed = 1;
  int carinfo__maxSpeed = 10;
  int carinfo__myID = 11;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "SpeedSensor";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForSpeedUpdate;
      break;
      
      case STATE__WaitingForSpeedUpdate: 
      waitFor((minSpeedUpdate)*1000, (maxSpeedUpdate)*1000);
      speed = computeRandom(carinfo__minSpeed, carinfo__maxSpeed);
      speed = carinfo__maxSpeed;
      traceVariableModification("SpeedSensor", "speed", speed,0);
      __params0[0] = &speed;
      makeNewRequest(&__req0, 260, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForSpeedUpdate;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

