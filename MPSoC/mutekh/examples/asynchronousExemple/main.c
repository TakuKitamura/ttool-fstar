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
/* AvatarAsynchronousExampleDeploy */
/* quatre tasks mappe sur deux processeurs, un canal */

#include "random.h"
#include "tracemanager.h"

#include "B.h"
#include "B0.h"
#include "B1.h"
#include "B2.h"

#define NB_PROC 2
#define WIDTH 4
#define DEPTH 16 

pthread_barrier_t barrier;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

void __user_init() {
}

#define MWMRd 0x20000000
#define LOCKS 0x30000000
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

//un seul channel, entre B et B0

  mwmr_t *B_sendReq_B2_receiveReq   = (mwmr_t*)(base(MWMRd)+0x3000);
//mwmr_t *channel   = __attribute__((section("section_mwmr1")))  = (mwmr_t*)(base(MWMRd)+0x3000);

   uint32_t * data  = (uint32_t*)(base(MWMRd)+0x1000); //0x20200200;

/*****************************Main****************************/

int main(int argc, char *argv[]) {
	void *ptr;
    pthread_barrier_init(&barrier,NULL, NB_PROC);
    pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
    pthread_attr_init(attr_t);
    pthread_mutex_init(&__mainMutex, NULL);

    /* Threads of tasks */ 
	
    pthread_t thread__B;
    pthread_t thread__B0;
    pthread_t thread__B1;
    pthread_t thread__B2;   
	/* ici on initialise tous les canaux de l'application */

    static struct mwmr_status_s channel_status = MWMR_STATUS_INITIALIZER(  32,    2);
   
   struct mwmr_s  channel  = MWMR_INITIALIZER( 32,2,B_sendReq_B2_receiveReq, &channel_status, "channel", MWMR_LOCK_INITIALIZER);
  
  /* Initializing the main mutex */

  if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}

/* pour ce thread englobant, qui ne lit ni ecrit les canaux, il est OK de transmettre uniquement son nom */

  ptr =malloc(sizeof(pthread_t));
  thread__B = (pthread_t)ptr;
  
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;   

  pthread_create(&thread__B, attr_t, mainFunc__B, (void *)"B");


struct mwmr_s  *canaux_array_B0[1];
  canaux_array_B0[0]=B_sendReq_B2_receiveReq;
  
  ptr =malloc(sizeof(pthread_t));
  thread__B0 = (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;   
 
  pthread_create(&thread__B0, attr_t, mainFunc__B0, (void *)canaux_array_B0);

 
  struct mwmr_s  *canaux_array_B1[1];
  canaux_array_B1[0]=B_sendReq_B2_receiveReq;
  
  ptr =malloc(sizeof(pthread_t));
  thread__B1 = (pthread_t)ptr;

  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;   
  pthread_create(&thread__B1, attr_t, (void *)mainFunc__B1, (void *)canaux_array_B1);


 struct mwmr_s  *canaux_array_B2[3];
  canaux_array_B2[0]=B_sendReq_B2_receiveReq;
  
  ptr =malloc(sizeof(pthread_t));
  thread__B2 = (pthread_t)ptr;

  attr_t = malloc(sizeof(pthread_attr_t));

  attr_t->cpucount = 1;  
  pthread_create(&thread__B2, attr_t, (void *)mainFunc__B2, (void *)canaux_array_B2);
 
  pthread_join(thread__B, NULL);
  pthread_join(thread__B1, NULL);
  pthread_join(thread__B2, NULL);
  pthread_join(thread__B0, NULL);
 
  return 0;
}

