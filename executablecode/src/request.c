
#include <stdlib.h>
#include <unistd.h>

#include "request.h"
#include "myerrors.h"


request *getNewRequest(int type, int hasDelay, long delay, int nbOfParams, int *params[]) {
  int i;
  request *req = (request *)(malloc(sizeof(struct request)+nbOfParams*sizeof(int)));
  
  if (req == NULL) {
    criticalError("Allocation of request failed");
  }
  req->next = NULL;
  req->listOfRequests = NULL;
  req->type = type;
  req->hasDelay = hasDelay;
  req->delay = delay;
  req->selected = 0;
  req->nbOfParams = nbOfParams;

  for(i=0; i<nbOfParams; i++) {
    req->params[i] = params[i];
  }
  return req;
}

void destroyRequest(request *req) {
  free((void *)req);
}

int isRequestSelected(request *req) {
  return req->selected;
}
