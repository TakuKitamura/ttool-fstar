


#include <time.h>
#include <sys/time.h>

#include "storeevents.h"
#include "transactions.h"
#include "debug.h"


FILE *file;
hrtime_t startTime;

void initStoreEvents() {
  file = fopen("events.txt", "w");
  if (file == NULL) {
    debugMsg(" *** File could not be opened");
  }

  startTime = gethrtime();
}

void addEvent(synccell *cell) {
  if (file == NULL) {
    debugMsg("No file to store events");
  }
  debugMsg("Adding event");
  if (cell == NULL) {
    debugMsg("NULL event");
    return;
  }

  cell->completionTime = gethrtime();

  fprintf(file, "task %d, transaction %d %d %d @t0+%lldms\n", cell->taskID, cell->ID, cell->type, cell->transactionDone, (cell->completionTime - startTime)/1000000); 
}


