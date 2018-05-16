#ifndef Bell__H
#define Bell_H

#include <stdio.h>
#include "pthread.h"
#include <unistd.h>
#include <stdlib.h>
#include "request_manager.h"
#include "debug.h"
#include "mytimelib.h"
#include "request.h"
#include "asyncchannel.h"
#include "defs.h"
#include "random.h"
#include "tracemanager.h"
#include "main.h"
#include "mwmr.h"

extern void *mainFunc__Bell(void *arg);

#endif
