#include "GPSSensor.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _setPosition;

#define STATE__START__STATE 0
#define STATE__WaitingForGPSInfo 1
#define STATE__STOP__STATE 2

void *mainFunc__GPSSensor(struct mwmr_s *channels_GPSSensor[]){
  
  struct mwmr_s *GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition= channels_GPSSensor[0];
  int minGPSUpdate = 100;
  int maxGPSUpdate = 100;
  int position = 0;
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
  
  char * __myname = "GPSSensor";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__WaitingForGPSInfo;
      break;
      
      case STATE__WaitingForGPSInfo: 
      waitFor((minGPSUpdate)*1000, (maxGPSUpdate)*1000);
      position = computeRandom(carinfo__minPosition, carinfo__maxPosition);
      __params0[0] = &position;
      makeNewRequest(&__req0, 286, SEND_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__WaitingForGPSInfo;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

