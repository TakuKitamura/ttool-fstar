#include <stdlib.h>
#include <pthread.h>

#include "request_manager.h"
#include "request.h"
#include "myerrors.h"


void executeSendSyncTransaction(request *req, syncchannel *channel) {
  // Search for an available transaction
  // If not return
  //Otherwise, select the transactions, and broadcast the new condition!
}


void executeSendSyncRequest(request *req, syncchannel *channel) {
  pthread_mutex_lock(channel->mutex);

  executeSendSyncTransaction(req, channel);

  while (isRequestSelected(req) == 0) {
    pthread_cond_wait(channel->sendCondition, channel->mutex);
  }

  pthread_mutex_unlock(channel->mutex);

}

