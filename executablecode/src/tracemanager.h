#ifndef TRACEMANAGER_H
#define TRACEMANANER_H

#include "request.h"

#define CHAR_ALLOC_SIZE 1024

void activeTracingInFile();
void unactiveTracing();
void traceRequest(char *myname, request *req);
void traceFunctionCall(char *block, char *func, char* params);
void traceVariableModification(char *block, char *var, int value, int type); // type=0: int type = 1:bool
void traceStateEntering(char *myname, char *statename);



#endif


