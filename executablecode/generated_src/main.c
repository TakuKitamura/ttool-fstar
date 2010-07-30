#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "transactions.h"
#include "syncchannel.h"
#include "timers.h"
#include "debug.h"
#include "storeevents.h"




void *send(void *arg) {
  int myid = (int)arg;

  int x = 2;
  int y = 3;
  
  int index;

  int *p[2];

  synccell *requests[2];

  debugMsg("Setting timer 12 ...");
  setTimerMs(myid, 12, 10000);
  debugMsg("Setting timer 12 done");

  // timer reset
  //sleep(5);
  //debugMsg("------- Timer reset");
  //resetTimer(12);
  //debugMsg("------- Timer reset done");

  debugMsg("Waiting for timerExpiration");
  waitForTimerExpiration(myid, 12);
  debugMsg("Timer has expired");

  debugTwoInts("Trying to send", x, y);
  p[0] = &x;
  p[1] = &y;
  sendSyncParams(myid, 1, p, 2);
  debugMsg("Send OK");

  
  // Testing multirequest
  // Trying to send with a timer of 10 seconds
  debugMsg("Setting timer 12 ...");
  setTimerMs(myid, 12, 10000);
  debugMsg("Setting timer 12 done");

  requests[0] = (synccell *)(malloc(sizeof(synccell) + 2*sizeof(int *)));
  requests[0]->ID = 1;
  requests[0]->type = SENDING;
  requests[0]->nParams = 2;
  requests[0]->params[0] = &x;
  requests[0]->params[1] = &y;
  requests[0]->taskID = myid;
  
  requests[1] = (synccell *)(malloc(sizeof(synccell)));
  requests[1]->ID = 12;
  requests[1]->type = TIMER_EXPIRATION;
  requests[1]->taskID = myid;
  
  debugMsg(" -------------- Making requests");

  x = 31;
  y = 51;
  index = makeRequests(requests, 2);

  debugInt("---------------- Request completed", index);

  return NULL;
}

void *receive(void *arg) {
  int myid = (int)arg;
  int x;
  int y;
  int *p[2];
  int sleepTime;

  debugInt("Setting timer ", 13 + myid);
  setTimerMs(myid, 13+myid, 1000);
  debugInt("Setting timer done", 13 + myid);

  debugInt("Trying to receive id", myid);
  p[0] = &x;
  p[1] = &y;
  receiveSyncParams(myid, 1, p ,2);
  debugThreeInts("Receive OK", x, y, myid);
  
  resetTimer(myid, 13+myid);
  // random wait between 5 and 15 seconds
  // Trying to receive

  sleepTime = 5 + (rand() % 10);
  debugTwoInts("---------- Waiting for seconds:", sleepTime, myid);
  setTimerMs(myid, 13+myid, sleepTime * 1000);

  debugInt("-------------- Waiting for timerExpiration", myid);
  waitForTimerExpiration(myid, 13+myid);
  debugInt("-------------- Timer has expired", myid);
  
  
  receiveSyncParams(myid, 1, p ,2);
  debugThreeInts("--------------- Second receive OK", x, y, myid);
  


  return NULL;
}


int main(int argc, char * argv[]) {
  pthread_t sender;
  pthread_t receiver0, receiver1;

  activeDebug();
  initStoreEvents();

  debugMsg("Timer management initialization ...");
  initTimerManagement();
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
