#ifndef ASYNCCHANNEL_H
#define ASYNCCHANNEL_H

struct asyncchannel;

#include "message.h"
#include "request.h"


struct asyncchannel {
  char *outname;
  char *inname;
  int isInfinite;
  int isBlocking;
  int maxNbOfMssages;
  struct request* outWaitQueue;
  struct request* inWaitQueue;
  setOfMessages *pendingMessages;
  int nbOfParams;
};

typedef struct asyncchannel asyncchannel;

asyncchannel *getNewAsyncchannel(char *inname, char *outname, int nbOfParams);
void destroyAsyncchannel(asyncchannel *syncch);


#endif
