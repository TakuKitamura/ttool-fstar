


#include <time.h>
#include <sys/time.h>

#include "storeevents.h"
#include "transactions.h"


FILE *file;
hrtime_t startTime;

void initStoreEvents() {
  file = fopen("events.txt", "w");
  startTime = gethrtime();
}

void addEvent(synccell *cell) {
  fprintf(file, "task %d, transaction %d @t0+%lldms\n", cell->taskID, cell->ID, (cell->completionTime - startTime)/1000000); 
}


