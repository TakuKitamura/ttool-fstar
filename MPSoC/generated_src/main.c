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

#include "B2.h"
#include "B0.h"
#include "B1.h"

/* Main mutex */
pthread_barrier_t barrier ;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

#define CHANNEL0 __attribute__((section("section_channel0")))
#define LOCK0 __attribute__((section("section_lock0")))
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

/* Synchronous channels */
syncchannel __B_sendReq__B2_receiveReq;
uint32_t const B_sendReq__B2_receiveReq_lock LOCK0;
struct mwmr_status_s B_sendReq__B2_receiveReq_status CHANNEL0;
uint8_t B_sendReq__B2_receiveReq_data[32] CHANNEL0;
struct mwmr_s B_sendReq__B2_receiveReq CHANNEL0;


int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  /* Synchronous channels */
  B_sendReq__B2_receiveReq_status.rptr = 0;
  B_sendReq__B2_receiveReq_status.wptr = 0;
  B_sendReq__B2_receiveReq_status.usage = 0;
  B_sendReq__B2_receiveReq_status.lock = 0;
  
  B_sendReq__B2_receiveReq.width = 1;
  B_sendReq__B2_receiveReq.depth = 1;
  B_sendReq__B2_receiveReq.gdepth = 1;
  B_sendReq__B2_receiveReq.buffer = B_sendReq__B2_receiveReq_data;
  B_sendReq__B2_receiveReq.status = &B_sendReq__B2_receiveReq_status;
  
  __B_sendReq__B2_receiveReq.inname ="receiveReq";
  __B_sendReq__B2_receiveReq.outname ="sendReq";
  __B_sendReq__B2_receiveReq.mwmr_fifo = &B_sendReq__B2_receiveReq;
  B_sendReq__B2_receiveReq.status =&B_sendReq__B2_receiveReq_status;
  B_sendReq__B2_receiveReq.status->lock=0;
  B_sendReq__B2_receiveReq.status->rptr=0;
  B_sendReq__B2_receiveReq.status->usage=0;
  B_sendReq__B2_receiveReq.status->wptr =0;
  
  /* Threads of tasks */
  pthread_t thread__B2;
  pthread_t thread__B0;
  pthread_t thread__B1;
  /* Activating tracing  */
  if (argc>1){
    activeTracingInFile(argv[1]);
  } else {
    activeTracingInConsole();
  }
  /* Activating debug messages */
  activeDebug();
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* User initialization */
  __user_init();
  
  
  debugMsg("Starting tasks");
  struct mwmr_s *channels_array_B2[1];
  channels_array_B2[0]=&B_sendReq__B2_receiveReq;
  
  ptr =malloc(sizeof(pthread_t));
  thread__B2= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__B2, attr_t, mainFunc__B2, (void *)channels_array_B2);
  
  struct mwmr_s *channels_array_B0;
  ptr =malloc(sizeof(pthread_t));
  thread__B0= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 1);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__B0, attr_t, mainFunc__B0, (void *)channels_array_B0);
  
  struct mwmr_s *channels_array_B1;
  ptr =malloc(sizeof(pthread_t));
  thread__B1= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 1);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__B1, attr_t, mainFunc__B1, (void *)channels_array_B1);
  
  
  
  debugMsg("Joining tasks");
  pthread_join(thread__B2, NULL);
  pthread_join(thread__B0, NULL);
  pthread_join(thread__B1, NULL);
  
  
  debugMsg("Application terminated");
  return 0;
  
}
