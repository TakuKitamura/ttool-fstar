

#ifndef SYNC_CHANNEL_H
#define SYNC_CHANNEL_H

void sendSync(int channel_id);
void sendSyncParams(int channel_id, int *param[], int nParams);

void receiveSync(int channel_id);
void receiveSyncParams(int channel_id, int *param[], int nParams);


#endif


