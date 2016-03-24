#ifndef REQUEST_MANAGER_H
#define REQUEST_MANAGER_H


#include "request.h"
#include "syncchannel.h"


request *executeOneRequest(setOfRequests *list, request *req);
request *executeListOfRequests(setOfRequests *list);

void removeAllPendingRequestsFromPendingListsRelatedRequests(request *req);

#endif
