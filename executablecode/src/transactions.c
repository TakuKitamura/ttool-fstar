
#include <stdlib.h>
#include <pthread.h> 
#include <signal.h>
#include <time.h>
#include <stdio.h>

#include "transactions.h"
#include "syncchannel.h"
#include "timers.h"
#include "myerrors.h"
#include "debug.h"


/* Mutex management */
pthread_mutex_t syncmutex;
pthread_cond_t waitingForReceiving, waitingForSending, waitingForTimer, multiType;

/* Linked list management */
synccell* head;
int nbOfCells = 0;


void addCell(synccell *cell) {
  cell->next = head;
  head = cell;
  nbOfCells ++;

  debugInt("Nb Of elements", nbOfCells);
}


synccell * getRandomCell() {
  int random_integer = rand() % nbOfCells; 
  synccell * cell = head;

  if (head == NULL) {
    return NULL;
  }

  while(random_integer >0) {
    cell = cell->next;
    random_integer --;
  }

  return cell;
  

}



// Returns NULL in case no pending
// A cell otherwise;
synccell * getPending(int channel_id, int type) {
  synccell* cell = getRandomCell();
  int index = 0;

  if (nbOfCells == 0) {
    return 0;
  }


  while(index < nbOfCells) {
    if (cell != NULL) {
      if ((cell->ID == channel_id) && (cell->type == type) && (cell->transactionDone == RUNNING)) {
	return cell;
      }
    }
    cell = cell->next;
    if (cell == NULL) {
      cell = head;
    }
    index ++;
  }

  return NULL;
} 


void removeRequest(synccell *cell) {
  synccell *tmp;
  int found = 0;

  if (cell == NULL) {
    return;
  }
  
  if (cell == head) {
    head = cell->next;
  } else {
    // Must find the previous cell;
    tmp = head;
    while(found ==0) {
      if (tmp == NULL) {
	criticalError("Remove Request");
      }

      if (tmp->next == cell) {
	found = 1;
      } else {
	tmp = tmp->next;
      }
    }

    tmp->next = cell->next;
  }

  nbOfCells --;

  free(cell);

  debugInt("Nb Of elements", nbOfCells);
}


int RequestsDone(synccell *cells[], int nbOfRequests) {
  int i;

  for(i=0; i<nbOfRequests; i++) {
    if (cells[i] != NULL) {
      if (cells[i]->transactionDone  == DONE)  {
	return i;
      }
    }
  }
  
  return -1;
}


// Sending, receiving, timer_expiration
int WaitAndStoreRequests(synccell *cells[], int nbOfRequests) {
  int i;
  synccell *newcells[nbOfRequests];
  int index;

  for(i=0; i<nbOfRequests; i++) {
    if ((cells[i]->type == SENDING) || (cells[i]->type == RECEIVING)) {
      newcells[i] = addSyncRequest(cells[i]->ID, cells[i]->params, cells[i]->nParams, cells[i]->type);
    } else if (cells[i]->type == TIMER_EXPIRATION){
      newcells[i] = getTimerCell(cells[i]->ID);
    } else {
      newcells[i] = NULL;
    }
  }

  
  while((index = RequestsDone(newcells, nbOfRequests)) == -1) {
    pthread_cond_wait(&multiType, &syncmutex);
  }

  return index;
}


// Returns the completed request
int makeRequests(synccell *cells[], int nbOfRequests) {
  int i;
  int completed = -1;
  synccell *cell = NULL;
  int random_integer = rand() % nbOfRequests;
  int index;

  pthread_mutex_lock(&syncmutex);

  // See whether a request can be immediatly completed
  for(i=0; i<nbOfRequests; i++) {
    index = (i + random_integer) % nbOfRequests;
    
    if (cells[index]->type == SENDING) {
      cell = getPending(cells[index]->ID, RECEIVING);
      if (cell != NULL) {
	completed = index;
	break;
      }
    } 

    if (cells[index]->type == RECEIVING) {
       cell = getPending(cells[index]->ID, RECEIVING);
      if (cell != NULL) {
	completed = index;
	break;
      }
    }

    if (cells[index]->type == TIMER) {
      if (cells[index]->timerValue < MIN_TIMER_VALUE) {
	completed = index;
	break;
      } else {
	// Can set the timer
	setTimer(cells[index]->ID, cells[index]->timerValue);
      }
    }

    if (cells[index]->type == TIMER_EXPIRATION) {
      cell = getTimerCell(cells[index]->ID);
      
      if (cell == NULL) {
	criticalErrorInt("Unknown Timer", cells[index]->ID);
      }
      
      if (cell->transactionDone == DONE) {
	completed = index;
	break;
      }
      
    }

    if (cells[index]->type == TIMER_RESET) {
      resetTimer(cells[index]->ID);
      completed = index;
      break;
    }
  }

  if (completed == -1) {
    // Requests must be stored, and we wait for a request to be served
    completed = WaitAndStoreRequests(cells, nbOfRequests);
  }  

  pthread_mutex_unlock(&syncmutex);
  
  return completed;
  }



