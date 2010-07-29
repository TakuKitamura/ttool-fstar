

#ifndef TIMERS_H
#define TIMERS_H

#include "transactions.h"

void initTimerManagement();

synccell *getTimerCell(int timer_id);
void setTimer(int timer_id, long long value); // in nanoseconds
void setTimerMs(int timer_id, long long value);
void waitForTimerExpiration(int timer_id);
void resetTimer(int timer_id);

#endif


