#ifndef ASYNCCHANNEL_H
#define ASYNCCHANNEL_H


#include "request.h"
#include "message.h"

struct asyncchannel;


struct asyncchannel {
  char *outname;
  char *inname;
  int isInfinite;
  int isBlocking;
  int maxNbOfMssages;
  request* outWaitQueue;
  request* inWaitQueue;
  setOfMessages *pendingMessages;
  int nbOfParams;
};

typedef struct asyncchannel asyncchannel;

asyncchannel *getNewAsyncchannel(char *inname, char *outname, int nbOfParams);
void destroyAsyncchannel(asyncchannel *syncch);


#endif
