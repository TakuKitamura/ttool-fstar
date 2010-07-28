
#include <stdlib.h>
#include <pthread.h> 
#include <signal.h>
#include <time.h>
#include <stdio.h>

#include "transactions.h"
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



void makeRequests(synccell cells[]) {
}



