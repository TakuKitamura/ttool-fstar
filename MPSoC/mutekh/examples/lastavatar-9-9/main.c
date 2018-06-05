#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "syncchannel.h"
#include "request_manager.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h"

/* User code */
void __user_init() {
}

/* End of User code */

/* Main mutex */
pthread_mutex_t __mainMutex;

/* Synchronous channels */
/* Asynchronous channels */
asyncchannel __Controller_ringBell__Bell_ring;
asyncchannel __Door_okDoor__Controller_okDoor;
asyncchannel __Door_open__Controller_open;
asyncchannel __Door_closed__Controller_closed;
asyncchannel __Controller_startMagnetron__Magnetron_startM;
asyncchannel __Controller_stopMagnetron__Magnetron_stopM;
asyncchannel __ControlPanel_LEDOn__Controller_startCooking;
asyncchannel __ControlPanel_LEDoff__Controller_stopCooking;
asyncchannel __ControlPanel_startButton__Controller_start;

#include "MicroWaveOven.h"
#include "Bell.h"
#include "ControlPanel.h"
#include "Controller.h"
#include "Magnetron.h"
#include "Door.h"


int main(int argc, char *argv[]) {
  
  /* disable buffering on stdout */
  setvbuf(stdout, NULL, _IONBF, 0);
  
  /* Synchronous channels */
  /* Asynchronous channels */
  __Controller_ringBell__Bell_ring.inname ="ring";
  __Controller_ringBell__Bell_ring.outname ="ringBell";
  __Controller_ringBell__Bell_ring.isBlocking = 0;
  __Controller_ringBell__Bell_ring.maxNbOfMessages = 4;
  __Door_okDoor__Controller_okDoor.inname ="okDoor";
  __Door_okDoor__Controller_okDoor.outname ="okDoor";
  __Door_okDoor__Controller_okDoor.isBlocking = 0;
  __Door_okDoor__Controller_okDoor.maxNbOfMessages = 4;
  __Door_open__Controller_open.inname ="open";
  __Door_open__Controller_open.outname ="open";
  __Door_open__Controller_open.isBlocking = 0;
  __Door_open__Controller_open.maxNbOfMessages = 4;
  __Door_closed__Controller_closed.inname ="closed";
  __Door_closed__Controller_closed.outname ="closed";
  __Door_closed__Controller_closed.isBlocking = 0;
  __Door_closed__Controller_closed.maxNbOfMessages = 4;
  __Controller_startMagnetron__Magnetron_startM.inname ="startM";
  __Controller_startMagnetron__Magnetron_startM.outname ="startMagnetron";
  __Controller_startMagnetron__Magnetron_startM.isBlocking = 0;
  __Controller_startMagnetron__Magnetron_startM.maxNbOfMessages = 4;
  __Controller_stopMagnetron__Magnetron_stopM.inname ="stopM";
  __Controller_stopMagnetron__Magnetron_stopM.outname ="stopMagnetron";
  __Controller_stopMagnetron__Magnetron_stopM.isBlocking = 0;
  __Controller_stopMagnetron__Magnetron_stopM.maxNbOfMessages = 4;
  __ControlPanel_LEDOn__Controller_startCooking.inname ="LEDOn";
  __ControlPanel_LEDOn__Controller_startCooking.outname ="startCooking";
  __ControlPanel_LEDOn__Controller_startCooking.isBlocking = 1;
  __ControlPanel_LEDOn__Controller_startCooking.maxNbOfMessages = 1;
  __ControlPanel_LEDoff__Controller_stopCooking.inname ="LEDoff";
  __ControlPanel_LEDoff__Controller_stopCooking.outname ="stopCooking";
  __ControlPanel_LEDoff__Controller_stopCooking.isBlocking = 1;
  __ControlPanel_LEDoff__Controller_stopCooking.maxNbOfMessages = 1;
  __ControlPanel_startButton__Controller_start.inname ="start";
  __ControlPanel_startButton__Controller_start.outname ="startButton";
  __ControlPanel_startButton__Controller_start.isBlocking = 1;
  __ControlPanel_startButton__Controller_start.maxNbOfMessages = 1;
  
  /* Threads of tasks */
  pthread_t thread__MicroWaveOven;
  pthread_t thread__Bell;
  pthread_t thread__ControlPanel;
  pthread_t thread__Controller;
  pthread_t thread__Magnetron;
  pthread_t thread__Door;
  /* Activating tracing  */
  if (argc>1){
    activeTracingInFile(argv[1]);
  } else {
    activeTracingInConsole();
  }
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* Initializing mutex of messages */
  initMessages();
  /* User initialization */
  __user_init();
  
  
  pthread_create(&thread__MicroWaveOven, NULL, mainFunc__MicroWaveOven, (void *)"MicroWaveOven");
  pthread_create(&thread__Bell, NULL, mainFunc__Bell, (void *)"Bell");
  pthread_create(&thread__ControlPanel, NULL, mainFunc__ControlPanel, (void *)"ControlPanel");
  pthread_create(&thread__Controller, NULL, mainFunc__Controller, (void *)"Controller");
  pthread_create(&thread__Magnetron, NULL, mainFunc__Magnetron, (void *)"Magnetron");
  pthread_create(&thread__Door, NULL, mainFunc__Door, (void *)"Door");
  
  
  pthread_join(thread__MicroWaveOven, NULL);
  pthread_join(thread__Bell, NULL);
  pthread_join(thread__ControlPanel, NULL);
  pthread_join(thread__Controller, NULL);
  pthread_join(thread__Magnetron, NULL);
  pthread_join(thread__Door, NULL);
  
  
  return 0;
  
}
