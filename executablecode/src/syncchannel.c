
#include <stdlib.h>
#include <stdio.h>

#include "syncchannel.h"
#include "transactions.h"
#include "myerrors.h"
#include "debug.h"







// Private

synccell * addSyncRequest(int channel_id, int *params[], int nParams, int type) {
  int i;
  synccell *cell = (synccell *)(malloc(sizeof(synccell) + nParams*sizeof(int *)));

  if (cell == NULL) {
    criticalError("Malloc in addRequest");
  }
  
  cell->ID = channel_id;
  cell-> type = type;
  cell->transactionDone = RUNNING;
  cell->nParams = nParams;
  cell->timer = -1;
  for(i=0; i<nParams; i++) {
    cell->params[i] = params[i];
  }

  cell->next = head;
  head = cell;

  nbOfCells ++;

  debugInt("Nb Of elements", nbOfCells);

  return cell;
}


void waitForSendingCompletion(int channel_id, int *params[], int nParams) {
  synccell * cell;

  cell = addSyncRequest(channel_id, params, nParams, SENDING);

  while(cell->transactionDone != DONE) {
    pthread_cond_wait(&waitingForSending, &syncmutex);
  }

  removeRequest(cell);
}

void waitForReceivingCompletion(int channel_id, int *params[], int nParams) {
  synccell * cell;

  cell = addSyncRequest(channel_id, params, nParams, RECEIVING);

  while(cell->transactionDone != DONE) {
    pthread_cond_wait(&waitingForReceiving, &syncmutex);
  }

  removeRequest(cell);
}



void makeSenderSynchronization(int channel_id, int *params[], int nParams, synccell *receiver) {
  int i;

  receiver->transactionDone = DONE;
  
  for(i=0; i<nParams; i++) {
    *(receiver->params[i])=*params[i];
  }

  pthread_cond_broadcast(&waitingForReceiving);
  pthread_cond_broadcast(&multiType);
}

void makeReceiverSynchronization(int channel_id, int *params[], int nParams, synccell *sender) {
  int i;

  sender->transactionDone = DONE;
  
  for(i=0; i<nParams; i++) {
    *params[i] = *(sender->params[i]);
  }

  pthread_cond_broadcast(&waitingForSending);
  pthread_cond_broadcast(&multiType);
}


// public elements


void sendSync(int channel_id) {
  sendSyncParams(channel_id, NULL, 0);
}

void sendSyncParams(int channel_id, int *params[], int nParams) {
  
  synccell *cell;
   pthread_mutex_lock(&syncmutex);  

   // See whether a receiving is pending
   cell = getPending(channel_id, RECEIVING);
   if (cell != NULL) {
     makeSenderSynchronization(channel_id, params, nParams, cell);
   } else {
     // Otherwise: add the request, and wait;
     waitForSendingCompletion(channel_id, params, nParams);
   } 
   pthread_mutex_unlock(&syncmutex); 
}


void receiveSync(int channel_id) {
  receiveSyncParams(channel_id, NULL, 0);
}


void receiveSyncParams(int channel_id, int *params[], int nParams) {
  
  synccell *cell;
   pthread_mutex_lock(&syncmutex);  

   // See whether a sending is pending
   cell = getPending(channel_id, SENDING);
   if (cell != NULL) {
     makeReceiverSynchronization(channel_id, params, nParams, cell);
   } else {
     // Otherwise: add the request, and wait;
     waitForReceivingCompletion(channel_id, params, nParams);
   } 
   pthread_mutex_unlock(&syncmutex); 
}





