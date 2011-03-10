#include <stdlib.h>

#include "asyncchannel.h"
#include "myerrors.h"


asyncchannel *getNewAsyncchannel(char *outname, char *inname, int nbOfParams) {
  asyncchannel * asyncch = (asyncchannel *)(malloc(sizeof(struct asyncchannel)));
  if (asyncch == NULL) {
    criticalError("Allocation of asyncchannel failed");
  }
  asyncch->inname = inname;
  asyncch->outname = outname;
  return asyncch;
}

void destroyAsyncchannel(asyncchannel *asyncch) {
  free(asyncch);
}
