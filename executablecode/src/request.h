#ifndef REQUEST_H
#define REQUEST_H

#include <time.h>
#include <pthread.h>

struct request;

#include "syncchannel.h"
#include "asyncchannel.h"

#define SEND_SYNC_REQUEST 0
#define RECEIVE_SYNC_REQUEST 2
#define SEND_ASYNC_REQUEST 4
#define RECEIVE_ASYNC_REQUEST 6
#define DELAY 8

#define IMMEDIATE 10

typedef struct timespec timespec;

struct setOfRequests {
  struct request *head;
  timespec startTime;
  timespec completionTime;
  pthread_cond_t *wakeupCondition;
  pthread_mutex_t *mutex;
};

typedef struct setOfRequests setOfRequests;

struct request {
  struct request *next;
  struct setOfRequests* listOfRequests;
  struct request* nextRequestInList;
  struct syncchannel *syncChannel;
  struct asyncchannel *asyncChannel;
  int type;
  int hasDelay;
  long minDelay;
  long maxDelay;
  int selected;
  int nbOfParams;
  int **params;
};

typedef struct request request;

request *getNewRequest(int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params);

void makeNewRequest(request *req, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params);
void destroyRequest(request *req);
extern int isRequestSelected(request *req);

int nbOfRequests(setOfRequests *list);
request *getRequestAtIndex(setOfRequests *list, int index);

request * addToRequestQueue(request *list, request *requestToAdd);
request * removeRequestFromList(request *list, request *requestToRemove);

void copyParameters(request *src, request *dst);


#endif
