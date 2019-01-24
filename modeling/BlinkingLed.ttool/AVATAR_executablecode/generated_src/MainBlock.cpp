#include "MainBlock.h"


// Header code defined in the model
DigitalOut myled1(LED1);

void __userImplemented__MainBlock__LED1on() {
   myled1 = 1;
   printf("Led1 on.\n");
}
void __userImplemented__MainBlock__LED1off(int val){
   myled1 = 0;
   printf("Led1 off.\n");
   printf("value of x=%d\n",val);
}

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__Waiting_For_Value 1
#define STATE__choice__0 2
#define STATE__Led1On 3
#define STATE__Led1Off 4
#define STATE__STOP__STATE 5

void MainBlock__LED1on() {
  traceFunctionCall("MainBlock", "LED1on", "-");
  __userImplemented__MainBlock__LED1on();
}


void MainBlock__LED1off(int x) {
  char my__attr[CHAR_ALLOC_SIZE];
  sprintf(my__attr, "%d",x);
  traceFunctionCall("MainBlock", "LED1off", my__attr);
  __userImplemented__MainBlock__LED1off(x);
}


void mainFunc__MainBlock(){
  int period = 2;
  int x = 0;
  int currentRand = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[1];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[1];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))size_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "mainFunc__MainBlock";
  
  fillListOfRequests(&__list, __myname, NULL, __myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      traceStateEntering(__myname, "__StartState");
      __currentState = STATE__Waiting_For_Value;
      break;
      
      case STATE__Waiting_For_Value: 
      traceStateEntering(__myname, "Waiting_For_Value");
      __params0[0] = &currentRand;
      makeNewRequest(&__req0, 163, RECEIVE_SYNC_REQUEST, 0, 0, 0, 1, __params0);
      __req0.syncChannel = &__MainBlock_RandVal__SecondaryBlock_RanVal;
      __returnRequest = executeOneRequest(&__list, &__req0);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
      __currentState = STATE__choice__0;
      break;
      
      case STATE__choice__0: 
      traceStateEntering(__myname, "choice__0");
      if (!(currentRand > 5)) {
        makeNewRequest(&__req0, 168, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (currentRand > 5) {
        makeNewRequest(&__req1, 173, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
       if (__returnRequest == &__req0) {
        __currentState = STATE__Waiting_For_Value;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__Led1On;
        
      }
      break;
      
      case STATE__Led1On: 
      traceStateEntering(__myname, "Led1On");
      wait_us((period)*1000000);
      x = x+1;
      traceVariableModification("MainBlock", "x", x,0);
      MainBlock__LED1off(x);
      __currentState = STATE__Led1Off;
      break;
      
      case STATE__Led1Off: 
      traceStateEntering(__myname, "Led1Off");
      if (!(x > 10)) {
        makeNewRequest(&__req0, 169, IMMEDIATE, 1, (period)*1000000, (period)*1000000, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (x > 10) {
        makeNewRequest(&__req1, 187, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
      traceRequest(__myname, __returnRequest);
       if (__returnRequest == &__req0) {
        __currentState = STATE__Waiting_For_Value;
        
      }
      else  if (__returnRequest == &__req1) {
        __currentState = STATE__STOP__STATE;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return ;
}

