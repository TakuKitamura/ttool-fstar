#include <stdlib.h>

#include "message.h"
#include "asyncchannel.h"
#include "myerrors.h"

#include "mwmr.h"

/* this function tries to read one message from the channel and returns either 
 1 or 0*/

int async_read_nonblocking( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i;
  i = mwmr_try_read(fifo,_ptr,lensw);
  debugInt("debug  bytes read \n", i);
  return i;
}

/* this function tries to write one message to the channel and throws it away if unsuccessful */

int async_write_nonblocking( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i; 
  i = mwmr_try_write(fifo,_ptr,lensw);
  if (i<lensw){
    /* the data item is thrown away */
    debugInt("debug  bytes written \n", i);
    debugInt("data discarded",lensw-i);
    return i;
  }
  else{
    //debugInt("data transmitted");
  }
  return i;
}


void async_read( struct mwmr_s *fifo, void *_ptr, int lensw ){
  debugInt("debug fifo read \n",fifo);
  debugInt("debug ptr \n",_ptr);
  debugInt("debug  lensw \n", lensw);
  debugInt("debug  fifo status address \n", &(fifo->status));
  debugInt("debug  fifo status \n", fifo->status);
  debugInt("debug  fifo lock address\n", &(fifo->status->lock));
  debugInt("debug  fifo lock \n", fifo->status->lock);
  mwmr_read(fifo,_ptr,lensw);
}

void async_write( struct mwmr_s *fifo, void *_ptr, int lensw ){
  debugInt("debug fifo write\n",fifo);
  debugInt("debug ptr \n",_ptr);
  debugInt("debug  lensw \n", lensw);
  debugInt("debug  fifo status address \n", &(fifo->status));
  debugInt("debug  fifo status \n", fifo->status);
  debugInt("debug  fifo lock address\n", &(fifo->status->lock));
  debugInt("debug  fifo lock \n", fifo->status->lock);
  mwmr_write(fifo,_ptr,lensw);
  }

asyncchannel *getNewAsyncchannel(char *outname, char *inname, int isBlocking, int maxNbOfMessages, struct mwmr_s *fifo) {
  asyncchannel * asyncch = (asyncchannel *)(malloc(sizeof(struct asyncchannel)));
  if (asyncch == NULL) {
    criticalError("Allocation of asyncchannel failed");
  }
  asyncch->inname = inname;
  asyncch->outname = outname;
  asyncch->isBlocking = isBlocking;
  asyncch->maxNbOfMessages = maxNbOfMessages;
  
  asyncch->mwmr_fifo=fifo;
  asyncch->mwmr_fifo->depth=fifo->depth;
  asyncch->mwmr_fifo->width=fifo->width;
  debugInt("asyncchannel address \n",asyncch->mwmr_fifo);
  debugInt("asyncchannel depth \n",asyncch->mwmr_fifo->depth);
  debugInt("asyncchannel width \n",asyncch->mwmr_fifo->width);

  return asyncch;
}

void destroyAsyncchannel(asyncchannel *asyncch) {
  free(asyncch);
}

  message* getAndRemoveOldestMessageFromAsyncChannel(asyncchannel *channel) {
  message *msg;
  message *previous;

  if (channel->currentNbOfMessages == 0) {
    return NULL;
  }

  if (channel->currentNbOfMessages == 1) {
    channel->currentNbOfMessages = 0;
    msg = channel->pendingMessages;
    channel->pendingMessages = NULL;
  
    debugInt("asyncchannel read: address \n",channel->mwmr_fifo);
    debugInt("asyncchannel fifo->depth \n",channel->mwmr_fifo->depth);
    debugInt("asyncchannel fifo->width \n",channel->mwmr_fifo->width);
    debugInt("asyncchannel msg size \n",channel->mwmr_fifo->width);
    // async_read(channel->mwmr_fifo, &msg, 1);
    async_read(channel->mwmr_fifo, &msg, channel->mwmr_fifo->width);
    return msg;
  }

  msg = channel->pendingMessages;
  previous = msg;
  while(msg->next != NULL) {
    previous = msg;
    msg = msg->next;
  }

  channel->currentNbOfMessages = channel->currentNbOfMessages -1;
  previous->next = NULL;

  debugInt("asyncchannel address \n",channel->mwmr_fifo);
  debugInt("asyncchannel fifo->depth \n",channel->mwmr_fifo->depth);
  debugInt("asyncchannel fifo->width \n",channel->mwmr_fifo->width);
  debugInt("asyncchannel msg size \n", channel->mwmr_fifo->width);
  async_read(channel->mwmr_fifo, &msg, channel->mwmr_fifo->width);
  
  return msg;
}

void addMessageToAsyncChannel(asyncchannel *channel, message *msg) {
  msg->next = channel->pendingMessages;
  channel->pendingMessages = msg;
  channel->currentNbOfMessages = channel->currentNbOfMessages+1;
  debugInt("asyncchannel write: address \n",channel->mwmr_fifo);
  debugInt("asyncchannel \n",channel->mwmr_fifo->depth);
  debugInt("asyncchannel  \n",channel->mwmr_fifo->width); 
  debugInt("asyncchannel->mwmr_fifo: \n",channel->mwmr_fifo);  
  debugInt("asyncchannel->fifo status address \n", &(channel->mwmr_fifo->status));
  debugInt("asyncchannel->fifo lock address\n", &(channel->mwmr_fifo->status->lock));
  debugInt("asyncchannel->fifo lock \n", channel->mwmr_fifo->status->lock);
  debugInt("asyncchannel->fifo usage \n", channel->mwmr_fifo->status->usage);
  debugInt("asyncchannel->fifo rptr \n", channel->mwmr_fifo->status->rptr);
  debugInt("asyncchannel->fifo wptr \n", channel->mwmr_fifo->status->wptr);
  //async_write(channel->mwmr_fifo, &msg, 1 );
  debugInt("asyncchannel fifo->width \n",channel->mwmr_fifo->width);
  debugInt("asyncchannel msg size \n", channel->mwmr_fifo->width);
  async_write(channel->mwmr_fifo, &msg, channel->mwmr_fifo->width);//DG 13.6. *msg au lieu de msg//DG 18.07. fifo width
}
