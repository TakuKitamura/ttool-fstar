
#include <stdlib.h>
#include <stdio.h>

#include "syncchannel.h"
#include "transactions.h"
#include "myerrors.h"
#include "debug.h"
#include "storeevents.h"







// Private

synccell * addSyncRequest(int myid, int channel_id, int *params[], int nParams, int type) {
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
  cell->taskID = myid;
  for(i=0; i<nParams; i++) {
    cell->params[i] = params[i];
  }

  cell->next = head;
  head = cell;

  nbOfCells ++;

  addEvent(cell);

  debugInt("Nb Of elements", nbOfCells);

  return cell;
}


void waitForSendingCompletion(int myid, int channel_id, int *params[], int nParams) {
  synccell * cell;

  cell = addSyncRequest(myid, channel_id, params, nParams, SENDING);

  while(cell->transactionDone != DONE) {
    pthread_cond_wait(&waitingForSending, &syncmutex);
  }

  addEvent(cell);

  removeRequest(cell);
}

void waitForReceivingCompletion(int myid, int channel_id, int *params[], int nParams) {
  synccell * cell;

  cell = addSyncRequest(myid, channel_id, params, nParams, RECEIVING);

  while(cell->transactionDone != DONE) {
    pthread_cond_wait(&waitingForReceiving, &syncmutex);
  }

  addEvent(cell);

  removeRequest(cell);
}



void makeSenderSynchronization(int myid, int channel_id, int *params[], int nParams, synccell *receiver) {
  int i;
  synccell cell;

  receiver->transactionDone = DONE;
  
  for(i=0; i<nParams; i++) {
    *(receiver->params[i])=*params[i];
  }

  cell.ID = channel_id;
  cell.taskID = myid;
  cell.type = SENDING;
  cell.transactionDone = DONE;

  addEvent(&cell);

  pthread_cond_broadcast(&waitingForReceiving);
  pthread_cond_broadcast(&multiType);
}

void makeReceiverSynchronization(int myid, int channel_id, int *params[], int nParams, synccell *sender) {
  int i;
  synccell cell;

  sender->transactionDone = DONE;
  
  for(i=0; i<nParams; i++) {
    *params[i] = *(sender->params[i]);
  }

  cell.ID = channel_id;
  cell.taskID = myid;
  cell.type = RECEIVING;
  cell.transactionDone = DONE;

  addEvent(&cell);

  pthread_cond_broadcast(&waitingForSending);
  pthread_cond_broadcast(&multiType);
}


// public elements


void sendSync(int myid, int channel_id) {
  sendSyncParams(myid, channel_id, NULL, 0);
}

void sendSyncParams(int myid, int channel_id, int *params[], int nParams) {
  
  synccell *cell;
   pthread_mutex_lock(&syncmutex);  

   // See whether a receiving is pending
   cell = getPending(channel_id, RECEIVING);
   if (cell != NULL) {
     makeSenderSynchronization(myid, channel_id, params, nParams, cell);
   } else {
     // Otherwise: add the request, and wait;
     waitForSendingCompletion(myid, channel_id, params, nParams);
   } 
   pthread_mutex_unlock(&syncmutex); 
}


void receiveSync(int myid, int channel_id) {
  receiveSyncParams(myid, channel_id, NULL, 0);
}


void receiveSyncParams(int myid, int channel_id, int *params[], int nParams) {
  
  synccell *cell;
   pthread_mutex_lock(&syncmutex);  

   // See whether a sending is pending
   cell = getPending(channel_id, SENDING);
   if (cell != NULL) {
     makeReceiverSynchronization(myid, channel_id, params, nParams, cell);
   } else {
     // Otherwise: add the request, and wait;
     waitForReceivingCompletion(myid, channel_id, params, nParams);
   } 
   pthread_mutex_unlock(&syncmutex); 
}





