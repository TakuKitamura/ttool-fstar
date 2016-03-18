#ifndef REQUEST_H
#define REQUEST_H

#include <time.h>
#include <pthread.h>

struct request;

#include "syncchannel.h"
#include "asyncchannel.h"
#include "message.h"

#define SEND_SYNC_REQUEST 0
#define RECEIVE_SYNC_REQUEST 2
#define SEND_ASYNC_REQUEST 4
#define RECEIVE_ASYNC_REQUEST 6
#define DELAY 8
#define IMMEDIATE 10
#define SEND_BROADCAST_REQUEST 12
#define RECEIVE_BROADCAST_REQUEST 14

typedef struct timespec timespec;

struct setOfRequests {
  char* owner;
  struct request *head;
  timespec startTime;
  timespec completionTime;
  pthread_cond_t *wakeupCondition;
  pthread_mutex_t *mutex;

  int hasATimeRequest; // Means that at least on request of the list hasn't completed yet its time delay
  timespec minTimeToWait;
  struct request *selectedRequest;
};

typedef struct setOfRequests setOfRequests;

struct request {
  struct request *next;
  struct setOfRequests* listOfRequests;
  struct request* nextRequestInList;
  struct request* relatedRequest; // For synchro and broadcast
  struct syncchannel *syncChannel;
  struct asyncchannel *asyncChannel;
  
  int type;
  int ID;
  int hasDelay;;
  timespec delay;
  int nbOfParams; // synchronous com.
  int **params;  // synchronous com.
  message *msg; // Asynchronous comm.


  // Filled by the request manager
  int executable;
  int selected;
  int alreadyPending; // Whether it has been taken into account for execution or not
  int delayElapsed;
  timespec myStartTime; // Time at which the delay has expired
};

typedef struct request request;

void makeNewRequest(request *req, int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params);
request *getNewRequest(int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params);
void destroyRequest(request *req);
extern int isRequestSelected(request *req);

int nbOfRequests(setOfRequests *list);
request *getRequestAtIndex(setOfRequests *list, int index);

request * addToRequestQueue(request *list, request *requestToAdd);
request * removeRequestFromList(request *list, request *requestToRemove);

void copyParameters(request *src, request *dst);

setOfRequests *newListOfRequests(pthread_cond_t *wakeupCondition, pthread_mutex_t *mutex);
void addRequestToList(setOfRequests *list, request* req);
void clearListOfRequests(setOfRequests *list);
void fillListOfRequests(setOfRequests *list, char *name, pthread_cond_t *wakeupCondition, pthread_mutex_t *mutex);

void removeAllPendingRequestsFromPendingLists(request *req, int apartThisOne);
request *hasIdenticalRequestInListOfSelectedRequests(request *req, request *list);
request* replaceInListOfSelectedRequests(request *oldRequest, request *newRequest, request *list);
int nbOfRelatedRequests(request *list);

#endif
