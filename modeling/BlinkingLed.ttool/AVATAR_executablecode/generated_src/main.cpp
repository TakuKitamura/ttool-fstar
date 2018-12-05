#include <mbed.h>
#include <rtos.h>



/* User code */
void __user_init() {
    printf("Initializing...\n");
}
/* End of User code */

/* Main mutex */
rtos::Mutex __mainMutex;

/* ConcurrencyMutex mutex */
rtos::Mutex __concurrencyMutex;


#include "MainBlock.h"


int main(int argc, char *argv[]) {
  
  /* disable buffering on stdout */
  setvbuf(stdout, (char*)NULL, _IONBF, 0);
  
  
  /* Threads of tasks */
  rtos::Thread thread__MainBlock;
  /* Activating tracing  */
  /* Activating randomness */
  initRandom();
  /* User initialization */
  __user_init();
  
  
  thread__MainBlock.start(mainFunc__MainBlock);
  
  
  thread__MainBlock.join();
  
  
  return 0;
  
}
