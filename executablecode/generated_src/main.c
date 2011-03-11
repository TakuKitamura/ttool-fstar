#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "syncchannel.h"
#include "debug.h"


syncchannel * syncchannels[1];
pthread_mutex_t syncmutex[1];
pthread_cond_t sendConditions[1];
pthread_cond_t receiveConditions[1];


void *send(void *arg) {
  int myid = (int)arg;

  int x = 2;
  int y = 3;
  
  int index;

  int *p[2];

  request* req;

  p[0] = &x;
  p[1] = &y;
  req = makeNewSendSync(0, 0, 2, p);

  return NULL;
}

void *receive(void *arg) {
  int myid = (int)arg;
  int x;
  int y;
  int *p[2];
  int sleepTime;

  debugInt("Setting timer ", 13 + myid);
  //setTimerMs(myid, 13+myid, 1000);
  debugInt("Setting timer done", 13 + myid);

  debugInt("Trying to receive id", myid);
  p[0] = &x;
  p[1] = &y;
  //receiveSyncParams(myid, 1, p ,2);
  //debugThreeInts("Receive OK", x, y, myid);
  
  //resetTimer(myid, 13+myid);
  // random wait between 5 and 15 seconds
  // Trying to receive

  //sleepTime = 5 + (rand() % 10);
  //debugTwoInts("---------- Waiting for seconds:", sleepTime, myid);
  //setTimerMs(myid, 13+myid, sleepTime * 1000);

  //debugInt("-------------- Waiting for timerExpiration", myid);
  //waitForTimerExpiration(myid, 13+myid);
  debugInt("-------------- Timer has expired", myid);
  
  
  //receiveSyncParams(myid, 1, p ,2);
  debugThreeInts("--------------- Second receive OK", x, y, myid);
  


  return NULL;
}


int main(int argc, char * argv[]) {

  syncchannels[0] = getNewSyncchannel("outch", "inch", &(syncmutex[0]), &(sendConditions[0]), &(receiveConditions[0]));

  pthread_t sender;
  pthread_t receiver0, receiver1;

  activeDebug();
  //initStoreEvents();

  debugMsg("Timer management initialization ...");
  //initTimerManagement();
  debugMsg("Timer management initialization done");
  
  debugMsg("Creating threads");
  pthread_create(&sender, NULL, send, (void *)1);
  pthread_create(&receiver0, NULL, receive, (void *)3);
  //pthread_create(&receiver1, NULL, receive, (void *)2);
  //pthread_create(&sender, NULL, send, NULL);

  debugMsg("Starting threads");
  pthread_join(sender, NULL);
  pthread_join(receiver0, NULL);
  //pthread_join(receiver1, NULL);
  
  debugMsg("All done");

  return 0;
}
