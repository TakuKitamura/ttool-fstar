
#include <stdlib.h>
#include <pthread.h> 
#include <signal.h>
#include <time.h>
#include <stdio.h>

#include "timers.h"
#include "transactions.h"
#include "myerrors.h"
#include "debug.h"



synccell *getTimerCell(int timer_id) {
  synccell* cell = head;

  while(cell != NULL) {
    if ((cell->ID == timer_id) && (cell->type == TIMER)) {
      return cell;
    }
    cell = cell->next;
  }

  return cell;
}

synccell *getTimerCellByTimerT(timer_t timer) {
  synccell* cell = head;

  while(cell != NULL) {
    if (cell->timer == timer) {
      return cell;
    }
    cell = cell->next;
  }

  return cell;
}

void timerTransactionDone(long *timer_id) {
  synccell* cell = getTimerCellByTimerT(*timer_id);
  
  debugInt("Transaction done for timer", *timer_id);
  if (timer_delete(*timer_id) <0) {
    criticalError("timer delete");
  }

  if (cell == NULL) {
    // Timer has probably been cancelled / resetted
    debugInt("Timer was resetted", *timer_id);
    return;
  }


  debugTwoInts("Timer done", *timer_id, cell->ID);
  cell->transactionDone = DONE;
  
  pthread_cond_broadcast(&waitingForTimer);
  pthread_cond_broadcast(&multiType);
  
}


synccell *makeTimerCell(int timer_id, long long value) {
  synccell *cell = getTimerCell(timer_id);

  if (cell == NULL) {
    debugMsg("*** Making a new timer cell");
    cell = (synccell *)(malloc(sizeof(synccell)));

    if (cell == NULL) {
      criticalError("Malloc in makeTimerCell");
    }
  
    cell->ID = timer_id;
    cell-> type = TIMER;
    cell->transactionDone = DEFINED;
    cell->nParams = 0;
    cell->timerValue = value;

    addCell(cell);
  } else {
    debugMsg("*** Reusing a timer cell");
    // If timer is expired
    cell->transactionDone = DEFINED;
    // If timer is set but not yet expired -> must stop the timer!
    timer_delete(cell->timer);
  }
  cell->timerValue = value;
  
  return cell;
}


#define CLOCKID CLOCK_REALTIME
#define SIG SIGRTMIN


static void print_siginfo(siginfo_t *si)
{
  timer_t *tidp;
  int or;
  
  tidp = si->si_value.sival_ptr;
  
  printf("    sival_ptr = %p; ", si->si_value.sival_ptr);
  printf("    *sival_ptr = 0x%lx\n", (long) *tidp);
  
  or = timer_getoverrun(*tidp);
  if (or == -1)
    debugMsg("timer_getoverrun");
  else
    debugInt("    overrun count =", or);
}

static void handler(int sig, siginfo_t *si, void *uc)
{
  /* Note: calling printf() from a signal handler is not
     strictly correct, since printf() is not async-signal-safe;
     see signal(7) */
  debugInt("Caught signal:", sig);
  pthread_mutex_lock(&syncmutex); 

  //debugInt("Caught signal:", sig);

  timerTransactionDone(si->si_value.sival_ptr);
  //print_siginfo(si);
  //signal(sig, SIG_IGN);

  pthread_mutex_unlock(&syncmutex); 
}

void startTimer(synccell *cell) {
  //timer_t timerid;
           struct sigevent sev;
           struct itimerspec its;
           long long freq_nanosecs;
           //sigset_t mask;
           //struct sigaction sa;

           sev.sigev_notify = SIGEV_SIGNAL;
           sev.sigev_signo = SIG;
           sev.sigev_value.sival_ptr = &(cell->timer);
           if (timer_create(CLOCKID, &sev, &(cell->timer)) == -1)
               criticalError("timer_create");

           printf("timer ID is 0x%lx internal_id=%d value=%lld\n", (long unsigned int)(cell->timer), cell->ID, cell->timerValue);
	   //printf("Timervalue %lld \n", cell->timerValue);
           /* Start the timer */

           freq_nanosecs = cell->timerValue;
           its.it_value.tv_sec = freq_nanosecs / 1000000000;
           its.it_value.tv_nsec = freq_nanosecs % 1000000000;
           its.it_interval.tv_sec = its.it_value.tv_sec;
           its.it_interval.tv_nsec = its.it_value.tv_nsec;

	   //debugMsg("timer set");
           if (timer_settime(cell->timer, 0, &its, NULL) == -1)
                criticalError("timer_settime");
	   cell->transactionDone = RUNNING;
	   debugMsg("OK TIMER");
       }



// public elements

void initTimerManagement() {
  struct sigaction sa;

  debugInt("Establishing handler for signal:", SIG);
  sa.sa_flags = SA_SIGINFO;
  sa.sa_sigaction = handler;
  sigemptyset(&sa.sa_mask);
  
if (sigaction(SIG, &sa, NULL) == -1) {
    criticalError("sigaction");
  }

}

void setTimer(int timer_id, long long value) {
  pthread_mutex_lock(&syncmutex); 

  debugInt("make timer cell", timer_id);
  synccell *cell = makeTimerCell(timer_id, value);
  debugInt("start timer cell", timer_id);
  startTimer(cell);
  debugInt("start timer cell done", timer_id);

  pthread_mutex_unlock(&syncmutex); 
}


// value is provided in ms



void setTimerMs(int timer_id, long long value) {
  setTimer(timer_id, value * 1000000);
}

void waitForTimerExpiration(int timer_id) {
  synccell *cell;
  
  pthread_mutex_lock(&syncmutex); 

  cell = getTimerCell(timer_id);

  if (cell == NULL) {
    criticalErrorInt("Unknown Timer", timer_id);
  }

  while (cell->transactionDone != DONE) {
    // Must wait for the transaction to complete
    pthread_cond_wait(&waitingForTimer, &syncmutex);
  }
  
  pthread_mutex_unlock(&syncmutex); 
}

void resetTimer(int timer_id) {
  synccell *cell;

  pthread_mutex_lock(&syncmutex); 

  cell = getTimerCell(timer_id);

  if (cell == NULL) {
    criticalErrorInt("Unknown Timer", timer_id);
  }

  cell->transactionDone = CANCELLED;
  timer_delete(cell->timer);
  
  pthread_mutex_unlock(&syncmutex); 
  
}





