#ifndef TRACEMANAGER_H
#define TRACEMANANER_H

#include "request.h"

void activeTracingInFile();
void unactiveTracing();
void traceRequest(char *myname, request *req);
void traceFunctionCall(char *block, char *func);
void traceStateEntering(char *myname, char *statename);

#endif


