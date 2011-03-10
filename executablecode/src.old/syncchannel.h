

#ifndef SYNC_CHANNEL_H
#define SYNC_CHANNEL_H

#include "transactions.h"

synccell * addSyncRequest(int myid, int channel_id, int *params[], int nParams, int type);

void makeSenderSynchronization(int myid, int channel_id, int *params[], int nParams, synccell *receiver);
void makeReceiverSynchronization(int myid, int channel_id, int *params[], int nParams, synccell *sender);

void sendSync(int myid, int channel_id);
void sendSyncParams(int myid, int channel_id, int *param[], int nParams);

void receiveSync(int myid, int channel_id);
void receiveSyncParams(int myid, int channel_id, int *param[], int nParams);


#endif


