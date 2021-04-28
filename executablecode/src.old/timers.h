

#ifndef TIMERS_H
#define TIMERS_H

#include "transactions.h"

void initTimerManagement();

synccell *getTimerCell(int timer_id);
void setTimer(int myid, int timer_id, long long value); // in nanoseconds
void setTimerMs(int myid, int timer_id, long long value);
void waitForTimerExpiration(int myid, int timer_id);
void resetTimer(int myid, int timer_id);

#endif


