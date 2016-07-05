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
  struct mwmr_s *mwmr_fifo; 
};

typedef struct syncchannel syncchannel;


void setBroadcast(syncchannel *syncch, bool b);

syncchannel *getNewSyncchannel(char *inname, char *outname, struct mwmr_s *fifo);
//request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]);
//request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]);
void destroySyncchannel(syncchannel *syncch);


#endif
