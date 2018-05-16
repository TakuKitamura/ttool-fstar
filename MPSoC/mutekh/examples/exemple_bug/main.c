#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

//ajoute DG
//#include <mutek/printk.h>
//#include <pthread.h>
//fin ajoute DG

//#include "pthread.h"
#include "mwmr.h"
#include "request.h"
#include "syncchannel.h"
#include "request_manager.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h"
//#include "srl.h"
//ajoute DG
//#include "srl_private_types.h"
//fin ajoute DG
#include "test.h"
#include "test2.h"
//#include <cpu.h>
//#include "segmentation.h"

#define NB_PROC 2
#define WIDTH 4
#define DEPTH 16 

pthread_barrier_t barrier;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

//ajoute DG
//typedef struct srl_mwmr_status_s srl_mwmr_status_s;
//typedef struct srl_mwmr_lock_s srl_mwmr_lock_t;
//fin ajoute DG

void __user_init() {
}

#define MWMRd 0x20000000
//#define MWMRd 0x60000000
#define LOCKS 0x30000000
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

  mwmr_t *canal    = (mwmr_t*)(base(MWMRd)+0x2000);
  uint32_t *signal    = (uint32_t*)(base(MWMRd)+0x0000); //0x20200000;
 
/*****************************Main****************************/

int main(int argc, char *argv[]) {
	void *ptr;
    pthread_barrier_init(&barrier,NULL, NB_PROC);
    pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
    pthread_attr_init(attr_t);
    pthread_mutex_init(&__mainMutex, NULL);

    /* Threads of tasks */ 
	
    pthread_t thread__test;
    pthread_t thread__test2;
  
 static struct mwmr_status_s status = MWMR_STATUS_INITIALIZER(  32,    2);
//struct mwmr_s  fifo  = MWMR_INITIALIZER( 32,2,canal, &status, "fifo", MWMR_LOCK_INITIALIZER);
  struct mwmr_s  fifo  = MWMR_INITIALIZER( 32,2,canal, &status, "fifo", MWMR_LOCK_INITIALIZER);

  /* Initializing the main mutex */

  if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}


/* pour ce thread, qui ne lit ni ecrit les canaux, il est OK de transmettre uniquement son nom */

  ptr =malloc(sizeof(pthread_t));
  thread__test = (pthread_t)ptr;
  
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   

  pthread_create(&thread__test, attr_t, mainFunc__test, (void *)"test");

  struct mwmr_s  *canaux[1];
  canaux[0]=canal; 
 
  ptr =malloc(sizeof(pthread_t));
  thread__test2 = (pthread_t)ptr;
  
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;   

  pthread_create(&thread__test2, attr_t, mainFunc__test2, (void *)canaux);

 
  pthread_join(thread__test, NULL);
  pthread_join(thread__test2, NULL);
  return 0;
}

