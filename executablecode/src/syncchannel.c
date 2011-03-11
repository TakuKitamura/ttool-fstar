#include <stdlib.h>


#include "syncchannel.h"
#include "request.h"
#include "myerrors.h"


syncchannel *getNewSyncchannel(char *outname, char *inname, pthread_mutex_t *mutex, pthread_cond_t *condSend,  pthread_cond_t *condReceive) {
  syncchannel * syncch = (syncchannel *)(malloc(sizeof(struct syncchannel)));
  if (syncch == NULL) {
    criticalError("Allocation of request failed");
  }
  syncch->inname = inname;
  syncch->outname = outname;
  syncch->inWaitQueue = NULL;
  syncch->outWaitQueue = NULL;
  syncch->mutex = mutex;
  syncch->sendCondition = condSend;
  syncch->receiveCondition = condReceive;
  return syncch;
}


request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]) {
  request *req = getNewRequest(SEND_SYNC_REQUEST, hasDelay, delay, nbOfParams, params);
  return req;
}

request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]) {
  request *req = getNewRequest(RECEIVE_SYNC_REQUEST, hasDelay, delay, nbOfParams, params);
  return req;
}

void destroySyncchannel(syncchannel *syncch) {
  free(syncch);
}
