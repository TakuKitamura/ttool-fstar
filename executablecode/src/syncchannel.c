#include <stdlib.h>

#include "syncchannel.h"
#include "myerrors.h"


syncchannel *getNewSyncchannel(char *outname, char *inname) {
  syncchannel * syncch = (syncchannel *)(malloc(sizeof(struct syncchannel)));
  if (syncch == NULL) {
    criticalError("Allocation of request failed");
  }
  syncch->inname = inname;
  syncch->outname = outname;
  return syncch;
}

void destroySyncchannel(syncchannel *syncch) {
  free(syncch);
}
