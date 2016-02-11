#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

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
pthread_mutex_t __mainMutex;

/* Synchronous channels */
/* Asynchronous channels */
asyncchannel __Block0_val__Block1_val;

#include "Block1.h"
#include "Block0.h"


int main(int argc, char *argv[]) {
  
  /* disable buffering on stdout */
  setvbuf(stdout, NULL, _IONBF, 0);
  
  /* Synchronous channels */
  /* Asynchronous channels */
  __Block0_val__Block1_val.inname ="val";
  __Block0_val__Block1_val.outname ="val";
  __Block0_val__Block1_val.isBlocking = 0;
  __Block0_val__Block1_val.maxNbOfMessages = 1;
  
  /* Threads of tasks */
  pthread_t thread__Block1;
  pthread_t thread__Block0;
  /* Activating tracing  */
  if (argc>1){
    activeTracingInFile(argv[1]);
  } else {
    activeTracingInConsole();
  }
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* Initializing mutex of messages */
  initMessages();
  /* User initialization */
  __user_init();
  
  
  pthread_create(&thread__Block1, NULL, mainFunc__Block1, (void *)"Block1");
  pthread_create(&thread__Block0, NULL, mainFunc__Block0, (void *)"Block0");
  
  
  pthread_join(thread__Block1, NULL);
  pthread_join(thread__Block0, NULL);
  
  
  return 0;
  
}
