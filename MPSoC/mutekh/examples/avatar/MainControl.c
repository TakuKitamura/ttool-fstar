#include "MainControl.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _motorCommand;
static uint32_t _control;
static uint32_t _tempData;
static uint32_t _sensorData;
static uint32_t _changeRate;

#define STATE__START__STATE 0
#define STATE__turnRight 1
#define STATE__turnLeft 2
#define STATE__choice__0 3
#define STATE__sendMotorCommand 4
#define STATE__dodgeObstacle 5
#define STATE__choice__1 6
#define STATE__setVelocity 7
#define STATE__choice__2 8
#define STATE__controlTempSensor 9
#define STATE__choice__3 10
#define STATE__changeRate 11
#define STATE__calculateDistance 12
#define STATE__choice__4 13
#define STATE__measureTemp 14
#define STATE__state1 15
#define STATE__state2 16
#define STATE__state0 17
#define STATE__choice__5 18
#define STATE__startController 19
#define STATE__STOP__STATE 20

void *mainFunc__MainControl(struct mwmr_s *channels_MainControl[]){
  
  struct mwmr_s *MainControl_motorCommand__MotorControl_motorCommand= channels_MainControl[0];
  struct mwmr_s *MainControl_changeRate__DistanceSensor_changeRate= channels_MainControl[1];
  struct mwmr_s *MainControl_tempData__TemperatureSensor_tempData= channels_MainControl[2];
  struct mwmr_s *MainControl_sensorData__DistanceSensor_sensorData= channels_MainControl[3];
  struct mwmr_s *MainControl_control__TemperatureSensor_control= channels_MainControl[4];
  int state = 0;
  bool sensorOn = false;
  int newRate = 0;
  int samplingRate = 1;
  int rateLow = 10;
  int rateMed = 4;
  int rateHigh = 1;
  int temp = 0;
  int leftVelocity = 0;
  int rightVelocity = 0;
  int distanceLeft = 0;
  int distanceRight = 0;
  int distanceFront = 0;
  int speedLow = 2;
  int speedNormal = 5;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[3];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[3];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[3];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "MainControl";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      waitFor((20)*1000, (30)*1000);
      __currentState = STATE__startController;
      break;
      
      case STATE__turnRight: 
      rightVelocity = 1;
      leftVelocity = speedLow;
      __currentState = STATE__sendMotorCommand;
      break;
      
      case STATE__turnLeft: 
      leftVelocity = 1;
      rightVelocity = speedLow;
      __currentState = STATE__sendMotorCommand;
      break;
      
      case STATE__choice__0: 
      if (distanceLeft>distanceRight) {
        makeNewRequest(&__req0, 196, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (!(distanceLeft>distanceRight)) {
        makeNewRequest(&__req1, 199, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__turnLeft;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__turnRight;
        
      }
      break;
      
      case STATE__sendMotorCommand: 
      __params0[0] = &leftVelocity;
      __params0[1] = &rightVelocity;
      debug2Msg(__myname, "-> (=====)before MakeNewRequest");
      debugInt("channel address", &__req0);
      makeNewRequest(&__req0, 105, SEND_ASYNC_REQUEST, 0, 0, 0, 2, __params0);
      debug2Msg(__myname, "-> (=====)after MakeNewRequest");
      __req0.asyncChannel = &__MainControl_motorCommand__MotorControl_motorCommand;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      waitFor((2)*1000, (10)*1000);
      __currentState = STATE__startController;
      break;
      
      case STATE__dodgeObstacle: 
      __currentState = STATE__choice__0;
      break;
      
      case STATE__choice__1: 
      if (state != 2) {
        makeNewRequest(&__req0, 190, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (!(state != 2)) {
        makeNewRequest(&__req1, 194, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__sendMotorCommand;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__dodgeObstacle;
        
      }
      break;
      
      case STATE__setVelocity: 
      __currentState = STATE__choice__1;
      break;
      
      case STATE__choice__2: 
      if ((!(state<2))&&(!(state == 2))) {
        makeNewRequest(&__req0, 127, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (state<2) {
        makeNewRequest(&__req1, 129, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (state == 2) {
        makeNewRequest(&__req2, 184, IMMEDIATE, 0, 0, 0, 0, __params2);
        addRequestToList(&__list, &__req2);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__setVelocity;
        
      }
      else  if (__returnRequest == &__req1) {
        sensorOn = false;
        __params0[0] = &sensorOn;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 123, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__MainControl_control__TemperatureSensor_control;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __currentState = STATE__setVelocity;
        
      }
      else  if (__returnRequest == &__req2) {
        sensorOn = true;
        __params0[0] = &sensorOn;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 124, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__MainControl_control__TemperatureSensor_control;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        __currentState = STATE__setVelocity;
        
      }
      break;
      
      case STATE__controlTempSensor: 
      waitFor((1)*1000, (5)*1000);
      __currentState = STATE__choice__2;
      break;
      
      case STATE__choice__3: 
      makeNewRequest(&__req0, 135, IMMEDIATE, 0, 0, 0, 0, __params0);
      addRequestToList(&__list, &__req0);
      if (samplingRate != newRate) {
        __params1[0] = &samplingRate;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req1);
        makeNewRequest(&__req1, 109, SEND_ASYNC_REQUEST, 0, 0, 0, 1, __params1);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req1.asyncChannel = &__MainControl_changeRate__DistanceSensor_changeRate;
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__controlTempSensor;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__controlTempSensor;
        
      }
      break;
      
      case STATE__changeRate: 
      waitFor((10)*1000, (20)*1000);
      __currentState = STATE__choice__3;
      break;
      
      case STATE__calculateDistance: 
      __currentState = STATE__choice__5;
      break;
      
      case STATE__choice__4: 
      makeNewRequest(&__req0, 158, IMMEDIATE, 0, 0, 0, 0, __params0);
      addRequestToList(&__list, &__req0);
      if (state == 2) {
        makeNewRequest(&__req1, 159, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__calculateDistance;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__measureTemp;
        
      }
      break;
      
      case STATE__measureTemp: 
      __params0[0] = &temp;
      makeNewRequest(&__req0, 115, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.asyncChannel = &__MainControl_tempData__TemperatureSensor_tempData;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __currentState = STATE__calculateDistance;
      break;
      
      case STATE__state1: 
      state = 1;
      newRate = rateMed;
      leftVelocity = speedLow;
      rightVelocity = speedLow;
      __currentState = STATE__changeRate;
      break;
      
      case STATE__state2: 
      state = 2;
      newRate = rateHigh;
      __currentState = STATE__changeRate;
      break;
      
      case STATE__state0: 
      state = 0;
      newRate = rateLow;
      leftVelocity = speedNormal;
      rightVelocity = speedNormal;
      __currentState = STATE__changeRate;
      break;
      
      case STATE__choice__5: 
      if (distanceFront>8) {
        makeNewRequest(&__req0, 165, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (distanceFront<3) {
        makeNewRequest(&__req1, 168, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if ((!(distanceFront>8))&&(!(distanceFront<3))) {
        makeNewRequest(&__req2, 171, IMMEDIATE, 0, 0, 0, 0, __params2);
        addRequestToList(&__list, &__req2);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        __currentState = STATE__state0;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__state2;
        
      }
      else  if (__returnRequest == &__req2) {
        __currentState = STATE__state1;
        
      }
      break;
      
      case STATE__startController: 
      __params0[0] = &distanceLeft;
      __params0[1] = &distanceFront;
      __params0[2] = &distanceLeft;
      makeNewRequest(&__req0, 120, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 3, __params0);
      __req0.asyncChannel = &__MainControl_sensorData__DistanceSensor_sensorData;
      debug2Msg(__myname, "-> (=====)before executeOneRequest");
      __returnRequest = executeOneRequest(&__list, &__req0);
      debug2Msg(__myname, "-> (=====)after executeOneRequest");
      clearListOfRequests(&__list);
      __currentState = STATE__choice__4;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

