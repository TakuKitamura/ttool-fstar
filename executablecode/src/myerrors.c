#include <stdlib.h>
#include <stdio.h>

#include "myerrors.h"

void criticalError(char *msg) {
  if (msg != NULL) {
    printf("\nCritical error: %s\n", msg);
  }

  exit(-1);
}
