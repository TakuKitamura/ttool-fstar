#ifndef SYNCCHANNEL_H
#define SYNCCHANNEL_H

struct syncchannel;

#include "request.h"
#include "defs.h"

struct syncchannel {
  char *outname;
  char *inname;
  struct request* inWaitQueue;
  struct request* outWaitQueue; 
  bool isBroadcast;
};

typedef struct syncchannel syncchannel;


void setBroadcast(syncchannel *syncch, bool b);
syncchannel *getNewSyncchannel(char *inname, char *outname);
//request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]);
//request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]);
void destroySyncchannel(syncchannel *syncch);


#endif
