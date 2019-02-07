#ifndef MainBlock_H
#define MainBlock_H
#include <mbed.h>
#include <rtos.h>

#include "request.h"
#include "syncchannel.h"
#include "asyncchannel.h"
#include "message.h"
#include "request_manager.h"
#include "debug.h"
#include "defs.h"
#include "mytimelib.h"
#include "random.h"
#include "tracemanager.h"
#include "main.h"

extern void mainFunc__MainBlock();

#endif
