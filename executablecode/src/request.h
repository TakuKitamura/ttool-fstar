#ifndef REQUEST_H
#define REQUEST_H

#define SYNC_REQUEST 0
#define SYNC_REQUEST_WITH_DELAY 1
#define ASYNC_REQUEST 2
#define ASYNC_REQUEST_WITH_DELAY 3
#define DELAY 4

struct request;

struct setOfRequests {
  struct request *head;
};

typedef struct setOfRequests setOfRequests;

struct request {
  int type;
  setOfRequests* listOfRequests;
  int hasDelay;
  long delay;
  int delayElapsed;
  int nbOfParams;
  int *params[];
};

typedef struct request request;

request * getNewRequest(int type);
void *destroyRequest(request *req);

#endif
