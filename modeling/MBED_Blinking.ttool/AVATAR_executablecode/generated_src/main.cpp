#include <mbed.h>
#include <rtos.h>

#include "request.h"
#include "syncchannel.h"
#include "request_manager.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h"

/* User code */
void __user_init() {
}

/* End of User code */

/* Main mutex */
rtos::Mutex __mainMutex;

/* ConcurrencyMutex mutex */
rtos::Mutex __concurrencyMutex;

/* Synchronous channels */
/* Asynchronous channels */

#include "MainBlock.h"


int main(int argc, char *argv[]) {
  
  /* disable buffering on stdout */
  setvbuf(stdout, (char*)NULL, _IONBF, 0);
  
  /* Synchronous channels */
  /* Asynchronous channels */
  
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
