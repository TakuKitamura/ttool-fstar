#ifndef MYTIMELIB_H
#define MYTIMELIB_H

#include <time.h>
#include <sys/time.h>

#ifndef CLOCK_REALTIME
#define CLOCK_REALTIME 0

void clock_gettime(int x, struct timespec *ts) {
  struct timeval tv;
  gettimeofday(&tv, NULL);
  ts->tv_sec = tv.tv_sec;
  ts->tv_nsec = tv.tv_usec * 1000;
}

#endif


// in usec
void addTime(struct timespec *src1, struct timespec *src2, struct timespec *dest);
void diffTime(struct timespec *src1, struct timespec *src2, struct timespec *dest);
int isBefore(struct timespec *src1, struct timespec *src2);
void minTime(struct timespec *src1, struct timespec *src2, struct timespec *dest);
void delayToTimeSpec(struct timespec *ts, long delay);
extern void waitFor(long minDelay, long maxDelay);

#endif
