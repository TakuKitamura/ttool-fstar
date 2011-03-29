
#include <stdlib.h>
#include <unistd.h>

#include "random.h"

int computeRandom(int min, int max) {
  return (rand() % (max - min)) + min;
}
