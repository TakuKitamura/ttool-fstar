#include <stdlib.h>


#include "syncchannel.h"
#include "request.h"
#include "myerrors.h"
#include "debug.h"
#include "mwmr.h"
//#include "random.h"

/* this function empties a channel and is called after one send or receive transaction
 */

void mwmr_sync_flush(struct mwmr_s *fifo){
  int i=1;
  while(i){ 
    i = mwmr_try_read(fifo,NULL,1);
  }
}

/* all synchronous communications use MWMR channels of size 1, enforcing synchronization */

/*void sync_read( struct mwmr_s *fifo, void *_ptr){
  int in;
  while(1){
     
    if(!(in=mwmr_try_read(fifo,_ptr,1))) continue;  
    }
  return;
  }*/
//DG 7.2.2017
int sync_read( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i;
  i = mwmr_try_read(fifo,_ptr,lensw);
  return i;
}

/* in the case of multi_writer one channel per writer */
/* we choose ramdomly one of the channels */
/* the problem is to identify the channels on the side of the writers which can be in different blocks */

void sync_read_random( struct mwmr_s *fifo[], void *_ptr, int nb_writers){
  int in;
  int rand = computeRandom(0, nb_writers-1);
  while(1){
    /* loop until one single message has been read successfully */  
    rand = computeRandom(0, nb_writers-1); 
    if(!(in=mwmr_try_read(fifo[rand],_ptr,1))) continue;   
    }
  return;
}

/*void sync_write(struct mwmr_s *fifo, void *_ptr){ 
  int out;   
    out=mwmr_try_write(fifo,NULL,1);
    if(out==0){      
     
      printf("message lost\n");
    }  
   return; 
   }*/
//DG 7.2.2017
int sync_write( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i; 
  i = mwmr_try_write(fifo,_ptr,lensw);
  if (i<lensw){
    /* the data item is thrown away */
    //debugInt("data thrown away");
    return i;
  }
  else{
    //debugInt("data transmitted");
  }
  return i;
}

/* the task issueing the message does not continue until THIS PARTICULAR message has been successfully taken by another task; an additional empty sync message is issued for that purpose, in a busy waiting loop; once synchronization has been achieved, this message is flushed and a blocking write initiated */

/*void sync_write_random(struct mwmr_s *fifo[], void *_ptr, int id_writer){ 
  int out;   
  while(1){  
    mwmr_lock(fifo[id_writer]);
    out=mwmr_try_write(fifo[id_writer],NULL,1);
    if(out==0){  
      mwmr_unlock(fifo[id_writer]); 
      continue;
    }
    mwmr_sync_flush(fifo[id_writer]);        
    mwmr_write(fifo[id_writer],_ptr,1);    
    mwmr_unlock(fifo[id_writer]); 
  }
   return; 
   }*/

//DG 7.9. add MWMR as parameter
//syncchannel *getNewSyncchannel(char *outname, char *inname, struct mwmr_s *fifo) {
syncchannel *getNewSyncchannel(char *outname, char *inname, struct mwmr_s *fifo) {
  syncchannel * syncch = (syncchannel *)(malloc(sizeof(struct syncchannel)));
  if (syncch == NULL) {
    criticalError("Allocation of request failed");
  }
  syncch->inname = inname;
  syncch->outname = outname;
  syncch->inWaitQueue = NULL;
  syncch->outWaitQueue = NULL;
  syncch->isBroadcast = false;
  //DG 7.9. add MWMR
  syncch->mwmr_fifo=fifo;
  return syncch;
}

void setBroadcast(syncchannel *syncch, bool b) {
  syncch->isBroadcast = b;
}

/*request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]) {
  request *req = getNewRequest(SEND_SYNC_REQUEST, hasDelay, delay, nbOfParams, params);
  return req;
}

request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]) {
  request *req = getNewRequest(RECEIVE_SYNC_REQUEST, hasDelay, delay, nbOfParams, params);
  return req;
  }*/

void destroySyncchannel(syncchannel *syncch) {
  free(syncch);




}
