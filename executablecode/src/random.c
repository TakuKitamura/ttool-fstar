
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include <limits.h>

#include "random.h"
#include "debug.h"
#include <math.h>

int computeRandom(int min, int max) {
  if (min == max) {
    return min;
  }
  return (rand() % (max - min)) + min;
}

long computeLongRandom(long min, long max) {

  if (min == max) {
    return min;
  }

  long rand0 = (((long)(rand()))*powl(2, ((((sizeof(long)-2))*8)-1)));
  long rand1 = rand0 % (max - min);
  //debugLong("min=", min);
  //debugLong("max=", max);
  //debugLong("rand0", rand0);
  //debugLong("rand1", rand1);
  debugLong("Random long", rand1 + min);
  return rand1 + min;
}

void initRandom() {
  struct timespec ts;

  clock_gettime(CLOCK_REALTIME, &ts);

  srand((int)(ts.tv_nsec));
}
