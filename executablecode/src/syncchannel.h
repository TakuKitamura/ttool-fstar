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
void destroySyncchannel(syncchannel *syncch);

#endif
