#include "TemperatureSensor.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _control;
static uint32_t _tempData;

#define STATE__START__STATE 0
#define STATE__start 1
#define STATE__STOP__STATE 2

void *mainFunc__TemperatureSensor(struct mwmr_s *channels_TemperatureSensor[]){
  
  struct mwmr_s *MainControl_control__TemperatureSensor_control= channels_TemperatureSensor[0];
  struct mwmr_s *MainControl_tempData__TemperatureSensor_tempData= channels_TemperatureSensor[1];
  bool sensorOn = false;
  int temp = 0;
  int samplingRate = 5;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "TemperatureSensor";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__start;
      break;
      
      case STATE__start: 
      if (sensorOn) {
        makeNewRequest(&__req0, 79, IMMEDIATE, 1, (samplingRate)*1000, (samplingRate)*1000, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      __params1[0] = &sensorOn;
      makeNewRequest(&__req1, 73, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params1);
      __req1.asyncChannel = &__MainControl_control__TemperatureSensor_control;
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        temp = computeRandom(0, 10);
        __params0[0] = &temp;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 74, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__MainControl_tempData__TemperatureSensor_tempData;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __currentState = STATE__start;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__start;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

