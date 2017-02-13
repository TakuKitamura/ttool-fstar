#ifndef TestBench_H
#define TestBench_H
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "syncchannel.h"
#include "request_manager.h"
#include "debug.h"
#include "defs.h"
#include "mytimelib.h"
#include "random.h"
#include "tracemanager.h"
#include "main.h"
#include "mwmr.h"
 

extern void *mainFunc__TestBench(struct mwmr_s *channels_TestBench[]);

#endif
