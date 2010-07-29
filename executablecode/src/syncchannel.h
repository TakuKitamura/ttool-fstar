

#ifndef SYNC_CHANNEL_H
#define SYNC_CHANNEL_H

#include "transactions.h"

synccell * addSyncRequest(int channel_id, int *params[], int nParams, int type);

void sendSync(int channel_id);
void sendSyncParams(int channel_id, int *param[], int nParams);

void receiveSync(int channel_id);
void receiveSyncParams(int channel_id, int *param[], int nParams);


#endif


