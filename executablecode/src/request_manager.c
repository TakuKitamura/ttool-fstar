#include <stdlib.h>
#include <pthread.h>

#include "request_manager.h"
#include "request.h"
#include "myerrors.h"
#include "debug.h"


request * addToRequestQueue(request *list, request *requestToAdd) {
  request *origin = list;

  if (list == NULL) {
    return requestToAdd;
  }

  while(list->next != NULL) {
    list = list->next;
  }
  
  list->next = requestToAdd;

  return origin;
}

request * removeRequestFromList(request *list, request *requestToRemove) {
  request *origin = list;

  if (list == requestToRemove) {
    return list->next;
  }



  while(list->next != requestToRemove) {
    list = list->next;
  }

  list->next = requestToRemove->next;

  return origin;
} 

void executeSendSyncTransaction(request *req, syncchannel *channel) {
  /*int cpt;
  request *selectedReq;

  // Search for an available transaction
  request* currentReq = channel->inWaitQueue;

  debugMsg("Execute send sync tr");
  
  if (currentReq == NULL) {
    // No transaction available
    channel->outWaitQueue = addToRequestQueue(channel->outWaitQueue, req);
    return;
  }
  
  // At least one transaction available -> must select one randomly
  // First: count how many of them are available
  // Then, select one
  // Broadcast the new condition!

  cpt = 0;
  while(currentReq != NULL) {
    cpt ++;
    currentReq = currentReq->next;
  }

  cpt = random() % cpt;

  // Head of the list?
  selectedReq = channel->inWaitQueue;
  while (cpt > 0) {
    selectedReq = selectedReq->next;
    cpt --;
  } 

  channel->inWaitQueue = removeRequestFromList(channel->inWaitQueue, selectedReq);

  // Select the two requests, and broadcast the information in the channel
  
  selectedReq->selected = 1;
  req->selected = 1;

  pthread_cond_broadcast(selectedReq->listOfRequests->wakeupCondition);*/
}

void executeReceiveSyncTransaction(request *req, syncchannel *channel) {
  /*int cpt;
  request *selectedReq;

  // Search for an available transaction
  request* currentReq = channel->outWaitQueue;

  debugMsg("Execute receive sync tr");
  
  if (currentReq == NULL) {
    // No transaction available
    channel->inWaitQueue = addToRequestQueue(channel->inWaitQueue, req);
    return;
  }
  
  // At least one transaction available -> must select one randomly
  // First: count how many of them are available
  // Then, select one
  // Broadcast the new condition!

  cpt = 0;
  while(currentReq != NULL) {
    cpt ++;
    currentReq = currentReq->next;
  }
  cpt = random() % cpt;
  selectedReq = channel->outWaitQueue;
  while (cpt > 0) {
    selectedReq = selectedReq->next;
    cpt --;
  } 

  channel->outWaitQueue = removeRequestFromList(channel->outWaitQueue, selectedReq);

  // Select the two requests, and broadcast the information in the channel
  selectedReq->selected = 1;
  req->selected = 1;

  pthread_cond_broadcast(selectedReq->listOfRequests->wakeupCondition);*/
}


void executeSendSyncRequest(request *req, syncchannel *channel) {
  /*debugMsg("Locking mutex");

  if(channel == NULL) {
    debugMsg("NULL channel");
    exit(-1);
  }

  if(channel->mutex == NULL) {
    debugMsg("NULL mutex");
    exit(-1);
  }

  pthread_mutex_lock(channel->mutex);

  debugMsg("Execute");
  executeSendSyncTransaction(req, channel);

  while (isRequestSelected(req) == 0) {
    debugMsg("Stuck waiting for a receive request");
    pthread_cond_wait(channel->sendCondition, channel->mutex);
    debugMsg("Woke up from waiting for a receive request");
  }

  pthread_mutex_unlock(channel->mutex);
  debugMsg("Mutex unlocked");*/

}

void executeReceiveSyncRequest(request *req, syncchannel *channel) {
  /*debugMsg("Locking mutex");

  pthread_mutex_lock(channel->mutex);

  debugMsg("Execute");
  executeReceiveSyncTransaction(req, channel);

  while (isRequestSelected(req) == 0) {
    debugMsg("Stuck waiting for a send request");
    pthread_cond_wait(req->listOfRequests->wakeupCondition, channel->mutex);
    debugMsg("Woke up from waiting for a send request");
  }

  pthread_mutex_unlock(channel->mutex);
  debugMsg("Mutex unlocked");*/
}


void executeListOfRequests(setOfRequests *list) {



}

setOfRequests *newListOfRequests(pthread_cond_t *wakeupCondition, pthread_mutex_t *mutex) {
  setOfRequests *list = (setOfRequests *)(malloc(sizeof(setOfRequests)));
  list->head = NULL;
  list->wakeupCondition = wakeupCondition;
  list->mutex = mutex;

  return list;
}


void addRequestToList(setOfRequests *list, request* req) {
  request *tmpreq;

  if (list == NULL) {
    criticalError("NULL List in addRequestToList");
  }

  if (req == NULL) {
    criticalError("NULL req in addRequestToList");
  }

  req->listOfRequests = list;

  if (list->head == NULL) {
    list->head = req;
    return;
  }

  tmpreq = list->head;
  while(tmpreq->nextRequestInList != NULL) {
    tmpreq = tmpreq->nextRequestInList;
  }

  tmpreq->nextRequestInList = req;
}
