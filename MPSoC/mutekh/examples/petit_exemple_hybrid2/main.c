#include <stdio.h>
//#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "asyncchannel.h"
#include "syncchannel.h"
#include "request_manager.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h"

#include "B2.h"
#include "B.h"
#include "B0.h"
#include "B1.h"

//ajoute 18.0.2
#define NB_PROC 1
#define WIDTH 4
#define DEPTH 16 

#define ADDR 0x6f000000
#define ADDR2 0x30000000
#define base(arg) arg

/* Synchronous channels */
/* Asynchronous channels */

//the channel is mapped explicitly to the memory segment

asyncchannel __B_sendReq__B2_receiveReq;
//asyncchannel* __B_sendReq__B2_receiveReq* __attribute__((section("section_mwmr0"))) = ADDR;

pthread_barrier_t barrier;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

void __user_init() {
}

int main(int argc, char *argv[]) {
  
//ajoute 18.02.
  void *ptr;
 pthread_barrier_init(&barrier,NULL, NB_PROC);    
 pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
 pthread_attr_init(attr_t);
 pthread_mutex_init(&__mainMutex, NULL);
//fin ajoute

  /* disable buffering on stdout */
  setvbuf(stdout, NULL, _IONBF, 0);
  
  /* Synchronous channels */
  /* Asynchronous channels */
  __B_sendReq__B2_receiveReq.inname ="receiveReq";
  __B_sendReq__B2_receiveReq.outname ="sendReq";
  __B_sendReq__B2_receiveReq.isBlocking = 0;
  __B_sendReq__B2_receiveReq.maxNbOfMessages = 1;
  
  /* Threads of tasks */
  pthread_t thread__B2;
  pthread_t thread__B;
  pthread_t thread__B0;
  pthread_t thread__B1;
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
  
  //ajoute 18.02. s'occupe uniquement du mapping des taches sur les processeurs

  ptr =malloc(sizeof(pthread_t));
  thread__B = (pthread_t)ptr; 
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   
  pthread_create(&thread__B, attr_t, mainFunc__B, (void *)"B");
 
  ptr =malloc(sizeof(pthread_t));
  thread__B0 = (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   
  pthread_create(&thread__B0, NULL, mainFunc__B0, (void *)"B0");

  ptr =malloc(sizeof(pthread_t));
  thread__B1 = (pthread_t)ptr; 
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   
  pthread_create(&thread__B1, attr_t, mainFunc__B1, (void *)"B1");

  ptr =malloc(sizeof(pthread_t));
  thread__B2 = (pthread_t)ptr; 
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   
  pthread_create(&thread__B2, attr_t, mainFunc__B2, (void *)"B2");

  //fin ajoute
  
  //supprime 18.02.
 
  //pthread_create(&thread__B2, NULL, mainFunc__B2, (void *)"B2");
  //pthread_create(&thread__B, NULL, mainFunc__B, (void *)"B");
  //pthread_create(&thread__B0, NULL, mainFunc__B0, (void *)"B0");
  //pthread_create(&thread__B1, NULL, mainFunc__B1, (void *)"B1");
  
  
  pthread_join(thread__B2, NULL);
  pthread_join(thread__B, NULL);
  pthread_join(thread__B0, NULL);
  pthread_join(thread__B1, NULL);
  
  
  return 0;
  
}
