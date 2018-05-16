#ifndef Timer__mainTimer__TCPIP_H
#define Timer__mainTimer__TCPIP_H
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
 

extern void *mainFunc__Timer__mainTimer__TCPIP(struct mwmr_s *channels_Timer__mainTimer__TCPIP[]);

#endif
