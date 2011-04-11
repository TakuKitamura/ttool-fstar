#include <stdlib.h>
#include <stdio.h>
#include <time.h>

#include "tracemanager.h"
#include "debug.h"
#include "mytimelib.h"


#define TRACE_OFF 0
#define TRACE_IN_FILE 1

#define TRACE_FILE_NAME "Trace.txt"


//pthread_mutex_t traceMutex;
//pthread_cond_t wakeupTraceManager;

int trace = TRACE_OFF;
int id = 0;

FILE *file;

struct timespec begints;



void addInfo(char *dest, char *info) {
  char s1[10];
  long tmp;
  long tmp1;
  int i;
  struct timespec ts, ts1;
  my_clock_gettime(&ts);
  
  debugMsg("DIFF TIME");
  diffTime(&begints, &ts, &ts1);

  tmp = ts1.tv_nsec;

  if (tmp < 0) {
    tmp = -tmp;
  }

  tmp1 = 100000000;

  for(i=0; i<9; i++) {
    s1[i] = 48 + (tmp / tmp1);
    tmp = tmp % tmp1;
    tmp1 = tmp1 / 10;
  }
  s1[9] = '\0';
  
  sprintf(dest, "#%d @%ld.%s %s", id, ts1.tv_sec, s1, info);
  id ++;
}


void writeInTrace(char *info) {
  char s[1024];
  addInfo(s, info);
		 //printf("Write in file\n");
  if (file != NULL) {
    

    debug2Msg("Saving in file", s);
    fprintf(file, s);
    fflush(file);
  }
}


void activeTracingInFile(char *fileName) {
  char *name;
  trace = TRACE_IN_FILE;
  my_clock_gettime(&begints); 
  if (fileName == NULL) {
    name = TRACE_FILE_NAME;
  } else {
    name  = fileName;
  }
  file = fopen(name,"w");
}

void unactiveTracing() {
  trace = TRACE_OFF;
}


void traceStateEntering(char *myname, char *statename) {
  char s[1024];

  debugMsg("Trace function");

  if (trace == TRACE_OFF) {
    return;
  }

  sprintf(s, "block=%s type=state_entering state=%s\n", myname, statename);

  // Saving trace
  writeInTrace(s);
}

void traceFunctionCall(char *block, char *func) {
  char s[1024];

  debugMsg("Trace function");

  if (trace == TRACE_OFF) {
    return;
  }

  sprintf(s, "block=%s type=function_call func=%s\n", block, func);

  // Saving trace
  writeInTrace(s);
}

void traceRequest(char *myname, request *req) {
  char s[1024];

  debugMsg("Trace request");


  if (trace == TRACE_OFF) {
    return;
  }

  // Build corresponding char*;

  switch(req->type) {
  case SEND_SYNC_REQUEST:
    debug2Msg("Sync channel", req->syncChannel->outname);
    sprintf(s, "block=%s type=send_synchro channel=%s\n", myname, req->syncChannel->outname);
    break;
  case RECEIVE_SYNC_REQUEST:
    sprintf(s, "block=%s type=receive_synchro channel=%s\n", myname, req->syncChannel->inname);
    break;
   case IMMEDIATE:
     sprintf(s, "block=%s type=action\n", myname);
    break;
  default:
    sprintf(s, "block=%s type=unknown\n", myname);
  }

  debugMsg("Trace request 2");
  

  // Saving trace
  writeInTrace(s);
}
