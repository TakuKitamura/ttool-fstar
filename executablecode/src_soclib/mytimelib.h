#ifndef MYTIMELIB_H
#define MYTIMELIB_H

#include <time.h>
#include <sys/time.h>



// in usec

int my_clock_gettime(struct timespec *tp);
void addTime(struct timespec *src1, struct timespec *src2, struct timespec *dest);
void diffTime(struct timespec *src1, struct timespec *src2, struct timespec *dest);
int isBefore(struct timespec *src1, struct timespec *src2);
void minTime(struct timespec *src1, struct timespec *src2, struct timespec *dest);
void delayToTimeSpec(struct timespec *ts, long delay);
extern void waitFor(long minDelay, long maxDelay);

#endif
