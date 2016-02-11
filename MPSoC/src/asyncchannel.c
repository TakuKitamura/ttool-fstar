#include <stdlib.h>

#include "message.h"
#include "asyncchannel.h"
#include "myerrors.h"
// ajoute DG
#include "mwmr.h"
//fin ajoute DG


//ajoute DG

/* this function tries to read one message from the channel and returns either 
 1 or 0*/

int async_read_nonblocking( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i;
  i = mwmr_try_read(fifo,_ptr,lensw);
  return i;
}

/* this function tries to write one message to the channel and throws it away if unsuccessful */

int async_write_nonblocking( struct mwmr_s *fifo, void *_ptr, int lensw ){
  int i; 
  i = mwmr_try_write(fifo,_ptr,lensw);
  if (i<lensw){
    /* the data item is thrown away */
    //printf("data thrown away");
    return i;
  }
  else{
    //printf("data transmitted");
  }
  return i;
}



void async_read( struct mwmr_s *fifo, void *_ptr, int lensw ){
    mwmr_read(fifo,_ptr,lensw);
}

void async_write( struct mwmr_s *fifo, void *_ptr, int lensw ){
  mwmr_write(fifo,_ptr,lensw);
  }

//fin ajoute DG

//DG 7.9. add MWMR as parameter
asyncchannel *getNewAsyncchannel(char *outname, char *inname, int isBlocking, int maxNbOfMessages, struct mwmr_s *fifo) {
  asyncchannel * asyncch = (asyncchannel *)(malloc(sizeof(struct asyncchannel)));
  if (asyncch == NULL) {
    criticalError("Allocation of asyncchannel failed");
  }
  asyncch->inname = inname;
  asyncch->outname = outname;
  asyncch->isBlocking = isBlocking;
  asyncch->maxNbOfMessages = maxNbOfMessages;
  //DG 7.9. add MWMR
  // DG 08.12. ici bug : voir CAVE
  asyncch->mwmr_fifo=fifo;
  asyncch->mwmr_fifo->depth=fifo->depth;
  asyncch->mwmr_fifo->width=fifo->width;
printf("asyncchannel getNew %x \n",asyncch->mwmr_fifo);
printf("asyncchannel %x \n",asyncch->mwmr_fifo->depth);
printf("asyncchannel %x \n",asyncch->mwmr_fifo->width);

  return asyncch;
}

void destroyAsyncchannel(asyncchannel *asyncch) {
  free(asyncch);
}

/* DG il faut en meme temps gerer le manager central et les canaux MWMR */

  message* getAndRemoveOldestMessageFromAsyncChannel(asyncchannel *channel) {
  message *msg;
  message *previous;

  if (channel->currentNbOfMessages == 0) {
    return NULL;
  }

  if (channel->currentNbOfMessages == 1) {
    channel->currentNbOfMessages = 0;
    msg = channel->pendingMessages;//DG 08.12. p.e. plus besoin de cela?
    channel->pendingMessages = NULL;
   
  printf("£££££££££££££££££££££££££££\n");  printf("before async read 0\n");printf("£££££££££££££££££££££££££££\n");
    async_read(channel->mwmr_fifo, &msg, 1);
 printf("£££££££££££££££££££££££££££\n");printf("after async read 0\n");printf("£££££££££££££££££££££££££££\n");
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
  //async_read(channel->mwmr_fifo, msg, 1);
printf("£££££££££££££££££££££££££££\n");printf("before async read 1");printf("£££££££££££££££££££££££££££\n");
  async_read(channel->mwmr_fifo, &msg, 1);//DG 08.12.
printf("£££££££££££££££££££££££££££\n");printf("after async read 1\n");printf("£££££££££££££££££££££££££££\n");
  return msg;
}

void addMessageToAsyncChannel(asyncchannel *channel, message *msg) {
 
  /* DG on ajoute un champ a la structure channel */ 
  msg->next = channel->pendingMessages;
  channel->pendingMessages = msg;
  channel->currentNbOfMessages = channel->currentNbOfMessages+1;
printf("asyncchannel address %x \n",channel->mwmr_fifo);
printf("asyncchannel %x \n",channel->mwmr_fifo->depth);
printf("asyncchannel %x \n",channel->mwmr_fifo->width);

printf("£££££££££££££££££££££££££££\n");printf("before async write 0");printf("£££££££££££££££££££££££££££\n");
printf("channel->mwmr_fifo: %x \n",channel->mwmr_fifo);
async_write(channel->mwmr_fifo, &msg, 1 );
 printf("£££££££££££££££££££££££££££\n");
printf("after async write 0\n");printf("£££££££££££££££££££££££££££\n");
}
