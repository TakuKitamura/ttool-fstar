#include <mbed.h>
#include <rtos.h>



/* User code */
void __user_init() {
}

/* End of User code */

/* Main mutex */
rtos::Mutex __mainMutex;

/* ConcurrencyMutex mutex */
rtos::Mutex __concurrencyMutex;

/* Synchronous channels */
syncchannel __MainBlock_RandVal__SecondaryBlock_RanVal;
/* Asynchronous channels */

#include "SecondaryBlock.h"
#include "MainBlock.h"


int main(int argc, char *argv[]) {
  
  /* disable buffering on stdout */
  setvbuf(stdout, (char*)NULL, _IONBF, 0);
  
  /* Synchronous channels */
  __MainBlock_RandVal__SecondaryBlock_RanVal.inname ="RandVal";
  __MainBlock_RandVal__SecondaryBlock_RanVal.outname ="RanVal";
  /* Asynchronous channels */
  
  /* Threads of tasks */
  rtos::Thread thread__SecondaryBlock;
  rtos::Thread thread__MainBlock;
  /* Activating tracing  */
  /* Activating randomness */
  initRandom();
  /* User initialization */
  __user_init();
  
  
  thread__SecondaryBlock.start(mainFunc__SecondaryBlock);
  thread__MainBlock.start(mainFunc__MainBlock);
  
  
  thread__SecondaryBlock.join();
  thread__MainBlock.join();
  
  
  return 0;
  
}
