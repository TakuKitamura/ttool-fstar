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

/* all synchronous communications use MWMR channels of size 1, enforcing synchronization */

int sync_read( struct mwmr_s *fifo, void *_ptr, int lensw ){  
  debugMsg("###before read ");
  mwmr_read(fifo,_ptr,1);
  debugMsg("###after read ");
}

int sync_write( struct mwmr_s *fifo, void *_ptr, int lensw ){
  debugMsg("###mwmr channel before write ");
  mwmr_write(fifo,_ptr,1);
  debugMsg("####mwmr channel  after write: "); 
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
  return syncch;
}

void setBroadcast(syncchannel *syncch, bool b) {
  syncch->isBroadcast = b;
}

void destroySyncchannel(syncchannel *syncch) {
  free(syncch);




}
