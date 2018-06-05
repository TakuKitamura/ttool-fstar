#ifndef ObjectListManagement_H
#define ObjectListManagement_H
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
 

extern void *mainFunc__ObjectListManagement(struct mwmr_s *channels_ObjectListManagement[]);

#endif
