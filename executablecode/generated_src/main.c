#include <stdio.h>
#include <pthread.h>
#include <unistd.h>

#include "transactions.h"
#include "syncchannel.h"
#include "timers.h"
#include "debug.h"




void *send(void *arg) {
  int x = 2;
  int y = 3;

  int *p[2];

  debugMsg("Setting timer 12 ...");
  setTimerMs(12, 10000);
  debugMsg("Setting timer 12 done");

  // timer reset
  //sleep(5);
  //debugMsg("------- Timer reset");
  //resetTimer(12);
  //debugMsg("------- Timer reset done");

  debugMsg("Waiting for timerExpiration");
  waitForTimerExpiration(12);
  debugMsg("Timer has expired");

  debugTwoInts("Trying to send", x, y);
  p[0] = &x;
  p[1] = &y;
  sendSyncParams(1, p, 2);
  debugMsg("Send OK");

  return NULL;
}

void *receive(void *arg) {
  int myid = (int)arg;
  int x;
  int y;
  int *p[2];

  debugInt("Setting timer ", 13 + myid);
  setTimerMs(13+myid, 1000);
  debugInt("Setting timer done", 13 + myid);

  debugInt("Trying to receive id", myid);
  p[0] = &x;
  p[1] = &y;
  receiveSyncParams(1, p ,2);
  debugThreeInts("Receive OK", x, y, myid);

  return NULL;
}


int main(int argc, char * argv[]) {
  pthread_t sender;
  pthread_t receiver0, receiver1;

  activeDebug();

  debugMsg("Timer management initialization ...");
  initTimerManagement();
  debugMsg("Timer management initialization done");
  
  debugMsg("Creating threads");
  pthread_create(&sender, NULL, send, NULL);
  pthread_create(&receiver0, NULL, receive, (void *)1);
  pthread_create(&receiver1, NULL, receive, (void *)2);
  //pthread_create(&sender, NULL, send, NULL);

  debugMsg("Starting threads");
  pthread_join(sender, NULL);
  pthread_join(receiver0, NULL);
  pthread_join(receiver1, NULL);
  
  debugMsg("All done");

  return 0;
}
