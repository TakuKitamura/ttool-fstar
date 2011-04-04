
#include <stdlib.h>
#include <unistd.h>
#include <time.h>

#include "random.h"


int computeRandom(int min, int max) {
  return (rand() % (max - min)) + min;
}

void initRandom() {
  struct timespec ts;

  clock_gettime(CLOCK_REALTIME, &ts);

  srand((int)(ts.tv_nsec));
}
