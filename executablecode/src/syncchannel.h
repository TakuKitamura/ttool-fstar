#ifndef SYNCCHANNEL_H
#define SYNCCHANNEL_H

#include <pthread.h>

#include "request.h"

struct syncchannel {
  char *outname;
  char *inname;
  request* inWaitQueue;
  request* outWaitQueue; 
  pthread_mutex_t *mutex;
  pthread_cond_t *sendCondition;
  pthread_cond_t *receiveCondition;
};

typedef struct syncchannel syncchannel;



syncchannel *getNewSyncchannel(char *inname, char *outname, pthread_mutex_t *mutex,  pthread_cond_t *condSend,  pthread_cond_t *condReceive);
request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]);
request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]);
void destroySyncchannel(syncchannel *syncch);

#endif
