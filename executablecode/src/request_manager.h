#ifndef REQUEST_MANAGER_H
#define REQUEST_MANAGER_H


#include "request.h"
#include "syncchannel.h"


void executeSendSyncRequest(request *req, syncchannel *channel);

#endif
