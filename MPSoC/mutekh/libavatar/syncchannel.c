#include <stdlib.h>


#include "syncchannel.h"
#include "request.h"
#include "myerrors.h"
#include "debug.h"


syncchannel *getNewSyncchannel(char *outname, char *inname) {
  syncchannel * syncch = (syncchannel *)(malloc(sizeof(struct syncchannel)));
  if (syncch == NULL) {
    criticalError("Allocation of request failed");
  }
  syncch->inname = inname;
  syncch->outname = outname;
  syncch->inWaitQueue = NULL;
  syncch->outWaitQueue = NULL;
  syncch->isBroadcast = false;
  return syncch;
}

void setBroadcast(syncchannel *syncch, bool b) {
  syncch->isBroadcast = b;
}



/*request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]) {
  request *req = getNewRequest(SEND_SYNC_REQUEST, hasDelay, delay, nbOfParams, params);
  return req;
}

request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]) {
  request *req = getNewRequest(RECEIVE_SYNC_REQUEST, hasDelay, delay, nbOfParams, params);
  return req;
  }*/

void destroySyncchannel(syncchannel *syncch) {
  free(syncch);
}
