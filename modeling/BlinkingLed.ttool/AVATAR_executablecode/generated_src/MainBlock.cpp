#include "MainBlock.h"


// Header code defined in the model
DigitalOut myled1(LED1);
DigitalOut myled2(LED2);

void __userImplemented__MainBlock__LED1onLED2off() {
  	myled1 = 1;
  	myled2 = 0;
  	printf("Led1 encendido y Led2 apagado.\n");
}
void __userImplemented__MainBlock__LED1offLED2on(){
  	myled1 = 0;
  	myled2 = 1;
  	printf("Led1 apagado y Led2 encendido.\n");
}

// End of header code defined in the model

#define STATE__START__STATE 0
#define STATE__Led1onLed2off 1
#define STATE__Led1offLed2on 2
#define STATE__STOP__STATE 3

void MainBlock__LED1onLED2off() {
  __userImplemented__MainBlock__LED1onLED2off();
}


void MainBlock__LED1offLED2on() {
  __userImplemented__MainBlock__LED1offLED2on();
}


void mainFunc__MainBlock(){
  int period = 2;
  
  int __currentState = STATE__START__STATE;
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      __currentState = STATE__Led1onLed2off;
      break;
      
      case STATE__Led1onLed2off: 
      wait_us((period)*1000000);
      MainBlock__LED1offLED2on();
      __currentState = STATE__Led1offLed2on;
      break;
      
      case STATE__Led1offLed2on: 
      wait_us((period)*1000000);
      MainBlock__LED1onLED2off();
      __currentState = STATE__Led1onLed2off;
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return ;
}

