#ifndef REQUEST_MANAGER_H
#define REQUEST_MANAGER_H


#include "request.h"
#include "syncchannel.h"


void executeSendSyncRequest(request *req, syncchannel *channel);
void executeReceiveSyncRequest(request *req, syncchannel *channel);

void executeListOfRequests(setOfRequests *list);
setOfRequests *newListOfRequests(pthread_cond_t *wakeupCondition, pthread_mutex_t *mutex);
void addRequestToList(setOfRequests *list, request* req);

#endif
