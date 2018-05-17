#ifndef SYNCCHANNEL_H
#define SYNCCHANNEL_H

struct syncchannel;

#include "request.h"
#include "defs.h"

struct syncchannel {
  char *outname;
  char *inname;
  struct request* inWaitQueue;
  struct request* outWaitQueue; 
  bool isBroadcast;
  struct mwmr_s *mwmr_fifo;
  int ok_send; 
  int ok_receive;
};

typedef struct syncchannel syncchannel;


void setBroadcast(syncchannel *syncch, bool b);

syncchannel *getNewSyncchannel(char *inname, char *outname, struct mwmr_s *fifo);
//request *makeNewSendSync(int hasDelay, long delay, int nbOfParams, int *params[]);
//request *makeNewReceiveSync(int hasDelay, long delay, int nbOfParams, int *params[]);
void destroySyncchannel(syncchannel *syncch);

int sync_read( struct mwmr_s *fifo, void *_ptr, int lensw );

int sync_write( struct mwmr_s *fifo, void *_ptr, int lensw );

void mwmr_sync_flush(struct mwmr_s *fifo);
#endif
