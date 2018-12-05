//#include <stdlib.h>
//#include <stdio.h>

#include "myerrors.h"
#include <mbed.h>
Serial pc(USBTX,USBRX);


void criticalErrorInt(char *msg, int value) {
  if (msg != NULL) {
    pc.printf("\nCritical error: %s, %d\n", msg, value);
  }

  exit(-1);
}


void criticalError(char *msg) {
  if (msg != NULL) {
    pc.printf("\nCritical error: %s\n", msg);
  }

  exit(-1);
}
