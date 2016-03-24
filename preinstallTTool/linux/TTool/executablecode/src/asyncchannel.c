#include <stdlib.h>

#include "message.h"
#include "asyncchannel.h"
#include "myerrors.h"


asyncchannel *getNewAsyncchannel(char *outname, char *inname, int isBlocking, int maxNbOfMessages) {
  asyncchannel * asyncch = (asyncchannel *)(malloc(sizeof(struct asyncchannel)));
  if (asyncch == NULL) {
    criticalError("Allocation of asyncchannel failed");
  }
  asyncch->inname = inname;
  asyncch->outname = outname;
  asyncch->isBlocking = isBlocking;
  asyncch->maxNbOfMessages = maxNbOfMessages;
  
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
  return msg;
}

void addMessageToAsyncChannel(asyncchannel *channel, message *msg) {
  msg->next = channel->pendingMessages;
  channel->pendingMessages = msg;
  channel->currentNbOfMessages = channel->currentNbOfMessages+1;
}
