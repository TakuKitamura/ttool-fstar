
#include <stdlib.h>
#include <unistd.h>

#include "message.h"
#include "myerrors.h"


message *getNewMessage(int nbOfParams, int *params[]) {
  int i;

  message *msg = (message *)(malloc(sizeof(struct message) + nbOfParams*sizeof(int *) ));
  if (msg == NULL) {
    criticalError("Allocation of request failed");
  }
  msg->nbOfParams = nbOfParams;
  for(i=0; i<nbOfParams; i++) {
    msg->params[i] = params[i];
  }
  return msg;
}

void destroyMessage(message *msg) {
  free(msg);
}
