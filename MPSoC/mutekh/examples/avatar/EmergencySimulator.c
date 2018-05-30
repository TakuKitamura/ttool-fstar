#include "EmergencySimulator.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _obstacleDetected;

#define STATE__START__STATE 0
#define STATE__WaitingforEmergency 1
#define STATE__STOP__STATE 2

void *mainFunc__EmergencySimulator(struct mwmr_s *channels_EmergencySimulator[]){
  
  struct mwmr_s *EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected= channels_EmergencySimulator[0];
  int minEmergencyInterval = 1000;
  int maxEmergencyInterval = 1000;
  int carid__minID = 1;
  int carid__maxID = 5;
  int carid__minPosition = 3;
  int carid__maxPosition = 10;
  int carid__minSpeed = 1;
  int carid__maxSpeed = 10;
  int carid__myID = 11;
  int id = 0;
  int position = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[2];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "EmergencySimulator";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingforEmergency;
      break;
      
      case STATE__WaitingforEmergency: 
      waitFor((minEmergencyInterval)*1000, (maxEmergencyInterval)*1000);
      id = computeRandom(carid__minID, carid__maxID);
      position = computeRandom(carid__minPosition, carid__maxPosition);
      __params0[0] = &id;
      __params0[1] = &position;
      makeNewRequest(&__req0, 303, SEND_SYNC_REQUEST, 0, 0, 0, 2, __params0);
      __req0.syncChannel = &__EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingforEmergency;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

