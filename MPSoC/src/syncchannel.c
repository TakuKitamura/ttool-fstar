#include <stdlib.h>
#include "syncchannel.h"
#include "request.h"
#include "myerrors.h"
#include "debug.h"
#include "mwmr.h"

/* this function empties a channel and is called after one send or receive transaction
 */
void mwmr_sync_flush(struct mwmr_s *fifo){
  int i=1;
  while(i){ 
    i = mwmr_try_read(fifo,NULL,1);
  }
}

int sync_read( struct mwmr_s *fifo, void *_ptr, int lensw ){  
  int i;
  debugInt("read debug fifo read\n",fifo);
  debugInt("read debug ptr \n",_ptr);
  debugInt("read debug  lensw \n", lensw);
  debugInt("read debug  fifo status address \n", &(fifo->status));
  debugInt("read debug  fifo status \n", (fifo->status));
  debugInt("read debug  fifo lock address\n", &(fifo->status->lock));
  debugInt("read debug  fifo rptr address\n", &(fifo->status->rptr));
  debugInt("read debug  fifo wptr address\n", &(fifo->status->rptr));
  debugInt("read debug  fifo lock \n", fifo->status->lock);
  i=mwmr_try_read(fifo,_ptr,lensw);
  //debugInt("debug i \n", i);
  //mwmr_read(fifo,_ptr,lensw);
  return i;
  //return lensw;
}

int sync_write( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i;
  debugInt("debug fifo write\n",fifo);
  debugInt("debug ptr \n",_ptr);
  debugInt("debug  lensw \n", lensw);
  debugInt("debug  fifo status address \n", &(fifo->status));
  debugInt("debug  fifo status \n", (fifo->status));
  debugInt("debug  fifo lock address\n", &(fifo->status->lock));
  debugInt("debug  fifo rptr address\n", &(fifo->status->rptr));
  debugInt("debug  fifo wptr address\n", &(fifo->status->rptr));
  debugInt("debug  fifo lock \n", fifo->status->lock);
  i=mwmr_try_write(fifo,_ptr,lensw);
  //mwmr_write(fifo,_ptr,lensw);
  //debugInt("debug i \n", i);
  return i;
  //return lensw;
}

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
  syncch->mwmr_fifo=fifo;
  debugInt("syncchannel address \n",syncch->mwmr_fifo);
  debugInt("syncchannel depth \n", syncch->mwmr_fifo->depth);
  debugInt("syncchannel width \n",syncch->mwmr_fifo->width);
  return syncch;
}

void setBroadcast(syncchannel *syncch, bool b) {
  syncch->isBroadcast = b;
}

void destroySyncchannel(syncchannel *syncch) {
  free(syncch);




}
