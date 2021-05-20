

#ifndef TRANSACTIONS_H
#define TRANSACTIONS_H

#include <pthread.h>
#include <signal.h>
#include <time.h>
#include <sys/time.h>

#define RECEIVING 1
#define SENDING 0
#define TIMER 2
#define TIMER_EXPIRATION 3
#define TIMER_RESET 4

// For transaction done
#define DEFINED 0
#define RUNNING 1
#define DONE 2
#define CANCELLED 3

#define MIN_TIMER_VALUE 1000 // in nanoseconds

struct synccell
{
  struct synccell *next;
  int ID;
  int taskID;
  int type; /* RECEIVING, SENDING, TIMER */
  char transactionDone;
  long long timerValue; // in nanoseconds
  hrtime_t completionTime;
  timer_t timer;
  int nParams;
  int *params[];
};

typedef struct synccell synccell;

/* Mutex management */
extern pthread_mutex_t syncmutex;
extern pthread_cond_t waitingForReceiving, waitingForSending, waitingForTimer, multiType;

/* Linked list management */
extern synccell *head;
extern int nbOfCells;

void addCell(synccell *cell);
synccell *getRandomCell();
synccell *getPending(int channel_id, int type);
void removeRequest(synccell *cell);

int makeRequests(synccell *cells[], int nbOfRequests);

#endif
