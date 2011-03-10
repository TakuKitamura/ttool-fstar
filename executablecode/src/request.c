
#include <stdlib.h>
#include <unistd.h>

#include "request.h"
#include "myerrors.h"


request *getNewRequest(int type) {
  request *req = (request *)(malloc(sizeof(struct request)));
  if (req == NULL) {
    criticalError("Allocation of request failed");
  }
  req->type = type;
  return req;
}

void *destroyRequest(request *req) {
  free(req);
}
