#include "DistanceSensor.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _sensorData;
static uint32_t _changeRate;

#define STATE__START__STATE 0
#define STATE__startSensor 1
#define STATE__STOP__STATE 2

void *mainFunc__DistanceSensor(struct mwmr_s *channels_DistanceSensor[]){
  
  struct mwmr_s *MainControl_sensorData__DistanceSensor_sensorData= channels_DistanceSensor[0];
  struct mwmr_s *MainControl_changeRate__DistanceSensor_changeRate= channels_DistanceSensor[1];
  int samplingRate = 10;
  int distance = 0;
  int distanceFront = 0;
  int distanceLeft = 0;
  int distanceRight = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[3];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[3];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "DistanceSensor";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__startSensor;
      break;
      
      case STATE__startSensor: 
      makeNewRequest(&__req0, 91, IMMEDIATE, 1, (samplingRate)*1000, (samplingRate)*1000, 0, __params0);
      addRequestToList(&__list, &__req0);
      __params1[0] = &samplingRate;
      makeNewRequest(&__req1, 85, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params1);
      __req1.asyncChannel = &__MainControl_changeRate__DistanceSensor_changeRate;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        distanceLeft = computeRandom(0, 10);
        waitFor((1)*1000, (2)*1000);
        distanceFront = computeRandom(0, 10);
        waitFor((1)*1000, (2)*1000);
        distanceRight = computeRandom(0, 10);
        waitFor((1)*1000, (2)*1000);
        __params0[0] = &distanceLeft;
        __params0[1] = &distanceFront;
        __params0[2] = &distanceRight;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 84, SEND_ASYNC_REQUEST, 0, 0, 0, 3, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__MainControl_sensorData__DistanceSensor_sensorData;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __currentState = STATE__startSensor;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__startSensor;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

