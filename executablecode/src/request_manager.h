#ifndef REQUEST_MANAGER_H
#define REQUEST_MANAGER_H


#include "request.h"
#include "syncchannel.h"


void executeSendSyncRequest(request *req, syncchannel *channel);
void executeReceiveSyncRequest(request *req, syncchannel *channel);

request *executeOneRequest(setOfRequests *list, request *req);
request *executeListOfRequests(setOfRequests *list);
#endif
