#ifndef SYNCCHANNEL_H
#define SYNCCHANNEL_H

#include "request.h"

struct syncchannel {
  char *outname;
  char *inname;
  request* inWaitQueue;
  request* outWaitQueue; 
};

typedef struct syncchannel syncchannel;



syncchannel *getNewSyncchannel(char *inname, char *outname);
//request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]);
//request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]);
void destroySyncchannel(syncchannel *syncch);


#endif
