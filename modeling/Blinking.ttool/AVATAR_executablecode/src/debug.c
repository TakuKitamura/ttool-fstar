#include <stdlib.h>
#include <stdio.h>
#include <time.h>

#include "debug.h"


#define DEBUG_ON 1
#define DEBUG_OFF 2

int debug = DEBUG_OFF;

void activeDebug() {
  debug = DEBUG_ON;
}

void unactiveDebug() {
  debug = DEBUG_OFF;
}

void debugThreeInts(char *msg, int value1, int value2, int value3) {
  if (debug == DEBUG_OFF) {
    return;
  }
  
  if (msg != NULL) {
    printf("DT> %s: %d, %d, %d\n", msg, value1, value2, value3);
  }
}

void debugTwoInts(char *msg, int value1, int value2) {
  if (debug == DEBUG_OFF) {
    return;
  }
  
  if (msg != NULL) {
    printf("DT> %s: %d, %d\n", msg, value1, value2);
  }
}

void debugInt(char *msg, int value) {
  if (debug == DEBUG_OFF) {
    return;
  }
  
  if (msg != NULL) {
    printf("DT> %s: %d\n", msg, value);
  }
}

void debugLong(char *msg, long value) {
  if (debug == DEBUG_OFF) {
    return;
  }
  
  if (msg != NULL) {
    printf("DT> %s: %ld\n", msg, value);
  }
}

void debugMsg(char *msg) {
  if (debug == DEBUG_OFF) {
    return;
  }

  if (msg != NULL) {
    printf("DT> %s\n", msg);
  }
}

void debug2Msg(char *name, char *msg) {
  if (debug == DEBUG_OFF) {
    return;
  }

  if ((name != NULL) && (msg != NULL)) {
    printf("DT - %s -> %s\n", name, msg);
  }
}

void debugTime(char *msg, struct timespec *ts) {
  if (debug == DEBUG_OFF) {
    return;
  }
  printf("DT> (-------t------->) %s sec=%ld nsec=%ld\n", msg, ts->tv_sec, ts->tv_nsec);
}
