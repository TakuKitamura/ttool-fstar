#include<time.h>

#include "mytimelib.h"
#include "random.h"
#include "debug.h"

#ifndef CLOCK_REALTIME
#define CLOCK_REALTIME

int clock_gettime(struct timespec *ts) {
  struct timeval tv;
  gettimeofday(&tv, NULL);
  ts->tv_sec = tv.tv_sec;
  ts->tv_nsec = tv.tv_usec * 1000;
  return 0;
}

int my_clock_gettime(struct timespec *tp) {
  return clock_gettime(tp);
}

#else

int my_clock_gettime(struct timespec *tp) {
  return clock_gettime(CLOCK_REALTIME, tp);
}

#endif



void addTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
  dest->tv_nsec = src1->tv_nsec + src2->tv_nsec;
  dest->tv_sec = src1->tv_sec + src2->tv_sec;
  if (dest->tv_nsec > 1000000000) {
    dest->tv_sec = dest->tv_sec + (dest->tv_nsec / 1000000000);
    dest->tv_nsec = dest->tv_nsec % 1000000000;
  }
}

void diffTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
  int diff = 0;
  if (src1->tv_nsec > src2->tv_nsec) {
    diff ++;
    dest->tv_nsec = src2->tv_nsec - src1->tv_nsec + 1000000000;
  } else {
    dest->tv_nsec = src2->tv_nsec - src1->tv_nsec;
  }

  dest->tv_sec = src2->tv_sec - src1->tv_sec - diff;
}



int isBefore(struct timespec *src1, struct timespec *src2) {
  if (src1->tv_sec > src2->tv_sec) {
    return 0;
  }

  if (src1->tv_sec < src2->tv_sec) {
    return 1;
  }

  if (src1->tv_nsec < src2->tv_nsec) {
    return 1;
  }
  return 0;
}

void minTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
  debugMsg("MIN TIME COMPUTATION");
  if (isBefore(src1,src2)) {
    dest->tv_nsec = src1->tv_nsec;
    dest->tv_sec = src1->tv_sec;
  } else {
    dest->tv_nsec = src2->tv_nsec;
    dest->tv_sec = src2->tv_sec;
  }
  
}


void delayToTimeSpec(struct timespec *ts, long delay) {
  ts->tv_nsec = (delay % 1000000)*1000;
  ts->tv_sec = (delay / 1000000);
}

void waitFor(long minDelay, long maxDelay) {
  struct timespec tssrc;
  struct timespec tsret;
  int delay;


  
  debugMsg("Computing random delay");
  //debugLong("Min delay", minDelay);
  //debugLong("Max delay", maxDelay);
  delay = computeLongRandom(minDelay, maxDelay);

  debugLong("Random delay=", delay);

  delayToTimeSpec(&tssrc, delay);

  debugLong("............. waiting For", delay);
  nanosleep(&tssrc, &tsret);
  debugLong("............. waiting Done for: ", delay);
}

