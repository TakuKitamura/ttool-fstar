#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "myerrors.h"
#include "message.h"
#include "syncchannel.h"
#include "asyncchannel.h"
#include "mytimelib.h"
#include "request_manager.h"
#include "defs.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h" 
#include "mwmr.h" 
 

#define NB_PROC 2
#define WIDTH 4
#define DEPTH 16

void __user_init() {
}

#include "Block1.h"
#include "Block0.h"

/* Main mutex */
pthread_barrier_t barrier ;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

#define CHANNEL0 __attribute__((section("section_channel0")))
#define LOCK0 __attribute__((section("section_lock0")))
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

/* Synchronous channels */
/* Asynchronous channels */
asyncchannel __Block0_val__Block1_val;
uint32_t const Block0_val__Block1_val_lock LOCK0;
struct mwmr_status_s Block0_val__Block1_val_status CHANNEL0;
uint8_t Block0_val__Block1_val_data[32] CHANNEL0;
struct mwmr_s Block0_val__Block1_val CHANNEL0;


int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  int sizeParams;
  
  /* Synchronous channels */
  /* Asynchronous channels */
  Block0_val__Block1_val_status.rptr = 0;
  Block0_val__Block1_val_status.wptr = 0;
  Block0_val__Block1_val_status.usage = 0;
  Block0_val__Block1_val_status.lock = 0;
  
  Block0_val__Block1_val.width = 4;
  Block0_val__Block1_val.depth = 4;
  Block0_val__Block1_val.gdepth = Block0_val__Block1_val.depth;
  Block0_val__Block1_val.buffer = Block0_val__Block1_val_data;
  Block0_val__Block1_val.status = &Block0_val__Block1_val_status;
  __Block0_val__Block1_val.inname ="val";
  __Block0_val__Block1_val.outname ="val";
  __Block0_val__Block1_val.isBlocking = 0;
  __Block0_val__Block1_val.maxNbOfMessages = 4;
  __Block0_val__Block1_val.mwmr_fifo = &Block0_val__Block1_val;
  Block0_val__Block1_val.status =&Block0_val__Block1_val_status;
  Block0_val__Block1_val.status->lock=0;
  Block0_val__Block1_val.status->rptr=0;
  Block0_val__Block1_val.status->usage=0;
  Block0_val__Block1_val.status->wptr=0;
  
  /* Threads of tasks */
  pthread_t thread__Block1;
  pthread_t thread__Block0;
  /* Activating tracing  */
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* User initialization */
  __user_init();
  
  
  struct mwmr_s *channels_array_Block1[1];
  channels_array_Block1[0]=&Block0_val__Block1_val;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Block1= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Block1, attr_t, mainFunc__Block1, (void *)channels_array_Block1);
  
  struct mwmr_s *channels_array_Block0[1];
  channels_array_Block0[0]=&Block0_val__Block1_val;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Block0= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Block0, attr_t, mainFunc__Block0, (void *)channels_array_Block0);
  
  
  
  pthread_join(thread__Block1, NULL);
  pthread_join(thread__Block0, NULL);
  
  
  return 0;
  
}
