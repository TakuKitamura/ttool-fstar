#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "myerrors.h"
#include "message.h"
#include "syncchannel.h"
#include "asyncchannel.h"
#include "mytimelib.h"
#include "request_manager.h"
#include "defs.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h" 
#include "mwmr.h" 
 

#define NB_PROC 2
#define WIDTH 4
#define DEPTH 16

void __user_init() {
}

#include "MicroWaveOven.h"
#include "Bell.h"
#include "ControlPanel.h"
#include "Controller.h"
#include "Magnetron.h"
#include "Door.h"

/* Main mutex */
pthread_barrier_t barrier ;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

#define CHANNEL0 __attribute__((section("section_channel0")))
#define LOCK0 __attribute__((section("section_lock0")))
#define CHANNEL1 __attribute__((section("section_channel1")))
#define LOCK1 __attribute__((section("section_lock1")))
#define CHANNEL2 __attribute__((section("section_channel2")))
#define LOCK2 __attribute__((section("section_lock2")))
#define CHANNEL3 __attribute__((section("section_channel3")))
#define LOCK3 __attribute__((section("section_lock3")))
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

/* Synchronous channels */
syncchannel __Controller_ringBell__Bell_ring;
uint32_t const Controller_ringBell__Bell_ring_lock LOCK0;
struct mwmr_status_s Controller_ringBell__Bell_ring_status CHANNEL0;
uint8_t Controller_ringBell__Bell_ring_data[32] CHANNEL0;
struct mwmr_s Controller_ringBell__Bell_ring CHANNEL0;

syncchannel __Door_okDoor__Controller_okDoor;
uint32_t const Door_okDoor__Controller_okDoor_lock LOCK0;
struct mwmr_status_s Door_okDoor__Controller_okDoor_status CHANNEL0;
uint8_t Door_okDoor__Controller_okDoor_data[32] CHANNEL0;
struct mwmr_s Door_okDoor__Controller_okDoor CHANNEL0;

syncchannel __Door_open__Controller_open;
uint32_t const Door_open__Controller_open_lock LOCK1;
struct mwmr_status_s Door_open__Controller_open_status CHANNEL1;
uint8_t Door_open__Controller_open_data[32] CHANNEL1;
struct mwmr_s Door_open__Controller_open CHANNEL1;

syncchannel __Door_closed__Controller_closed;
uint32_t const Door_closed__Controller_closed_lock LOCK2;
struct mwmr_status_s Door_closed__Controller_closed_status CHANNEL2;
uint8_t Door_closed__Controller_closed_data[32] CHANNEL2;
struct mwmr_s Door_closed__Controller_closed CHANNEL2;

syncchannel __Controller_startMagnetron__Magnetron_startM;
uint32_t const Controller_startMagnetron__Magnetron_startM_lock LOCK0;
struct mwmr_status_s Controller_startMagnetron__Magnetron_startM_status CHANNEL0;
uint8_t Controller_startMagnetron__Magnetron_startM_data[32] CHANNEL0;
struct mwmr_s Controller_startMagnetron__Magnetron_startM CHANNEL0;

syncchannel __Controller_stopMagnetron__Magnetron_stopM;
uint32_t const Controller_stopMagnetron__Magnetron_stopM_lock LOCK1;
struct mwmr_status_s Controller_stopMagnetron__Magnetron_stopM_status CHANNEL1;
uint8_t Controller_stopMagnetron__Magnetron_stopM_data[32] CHANNEL1;
struct mwmr_s Controller_stopMagnetron__Magnetron_stopM CHANNEL1;

syncchannel __ControlPanel_LEDOn__Controller_startCooking;
uint32_t const ControlPanel_LEDOn__Controller_startCooking_lock LOCK0;
struct mwmr_status_s ControlPanel_LEDOn__Controller_startCooking_status CHANNEL0;
uint8_t ControlPanel_LEDOn__Controller_startCooking_data[32] CHANNEL0;
struct mwmr_s ControlPanel_LEDOn__Controller_startCooking CHANNEL0;

syncchannel __ControlPanel_LEDoff__Controller_stopCooking;
uint32_t const ControlPanel_LEDoff__Controller_stopCooking_lock LOCK1;
struct mwmr_status_s ControlPanel_LEDoff__Controller_stopCooking_status CHANNEL1;
uint8_t ControlPanel_LEDoff__Controller_stopCooking_data[32] CHANNEL1;
struct mwmr_s ControlPanel_LEDoff__Controller_stopCooking CHANNEL1;

syncchannel __ControlPanel_startButton__Controller_start;
uint32_t const ControlPanel_startButton__Controller_start_lock LOCK2;
struct mwmr_status_s ControlPanel_startButton__Controller_start_status CHANNEL2;
uint8_t ControlPanel_startButton__Controller_start_data[32] CHANNEL2;
struct mwmr_s ControlPanel_startButton__Controller_start CHANNEL2;


int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  /* Synchronous channels */
  Controller_ringBell__Bell_ring_status.rptr = 0;
  Controller_ringBell__Bell_ring_status.wptr = 0;
  Controller_ringBell__Bell_ring_status.usage = 0;
  Controller_ringBell__Bell_ring_status.lock = 0;
  
  Controller_ringBell__Bell_ring.width = 1;
  Controller_ringBell__Bell_ring.depth = 1;
  Controller_ringBell__Bell_ring.gdepth = 1;
  Controller_ringBell__Bell_ring.buffer = Controller_ringBell__Bell_ring_data;
  Controller_ringBell__Bell_ring.status = &Controller_ringBell__Bell_ring_status;
  
  __Controller_ringBell__Bell_ring.inname ="ring";
  __Controller_ringBell__Bell_ring.outname ="ringBell";
  __Controller_ringBell__Bell_ring.mwmr_fifo = &Controller_ringBell__Bell_ring;
  Controller_ringBell__Bell_ring.status =&Controller_ringBell__Bell_ring_status;
  Controller_ringBell__Bell_ring.status->lock=0;
  Controller_ringBell__Bell_ring.status->rptr=0;
  Controller_ringBell__Bell_ring.status->usage=0;
  Controller_ringBell__Bell_ring.status->wptr =0;
  Door_okDoor__Controller_okDoor_status.rptr = 0;
  Door_okDoor__Controller_okDoor_status.wptr = 0;
  Door_okDoor__Controller_okDoor_status.usage = 0;
  Door_okDoor__Controller_okDoor_status.lock = 0;
  
  Door_okDoor__Controller_okDoor.width = 1;
  Door_okDoor__Controller_okDoor.depth = 1;
  Door_okDoor__Controller_okDoor.gdepth = 1;
  Door_okDoor__Controller_okDoor.buffer = Door_okDoor__Controller_okDoor_data;
  Door_okDoor__Controller_okDoor.status = &Door_okDoor__Controller_okDoor_status;
  
  __Door_okDoor__Controller_okDoor.inname ="okDoor";
  __Door_okDoor__Controller_okDoor.outname ="okDoor";
  __Door_okDoor__Controller_okDoor.mwmr_fifo = &Door_okDoor__Controller_okDoor;
  Door_okDoor__Controller_okDoor.status =&Door_okDoor__Controller_okDoor_status;
  Door_okDoor__Controller_okDoor.status->lock=0;
  Door_okDoor__Controller_okDoor.status->rptr=0;
  Door_okDoor__Controller_okDoor.status->usage=0;
  Door_okDoor__Controller_okDoor.status->wptr =0;
  Door_open__Controller_open_status.rptr = 0;
  Door_open__Controller_open_status.wptr = 0;
  Door_open__Controller_open_status.usage = 0;
  Door_open__Controller_open_status.lock = 0;
  
  Door_open__Controller_open.width = 1;
  Door_open__Controller_open.depth = 1;
  Door_open__Controller_open.gdepth = 1;
  Door_open__Controller_open.buffer = Door_open__Controller_open_data;
  Door_open__Controller_open.status = &Door_open__Controller_open_status;
  
  __Door_open__Controller_open.inname ="open";
  __Door_open__Controller_open.outname ="open";
  __Door_open__Controller_open.mwmr_fifo = &Door_open__Controller_open;
  Door_open__Controller_open.status =&Door_open__Controller_open_status;
  Door_open__Controller_open.status->lock=0;
  Door_open__Controller_open.status->rptr=0;
  Door_open__Controller_open.status->usage=0;
  Door_open__Controller_open.status->wptr =0;
  Door_closed__Controller_closed_status.rptr = 0;
  Door_closed__Controller_closed_status.wptr = 0;
  Door_closed__Controller_closed_status.usage = 0;
  Door_closed__Controller_closed_status.lock = 0;
  
  Door_closed__Controller_closed.width = 1;
  Door_closed__Controller_closed.depth = 1;
  Door_closed__Controller_closed.gdepth = 1;
  Door_closed__Controller_closed.buffer = Door_closed__Controller_closed_data;
  Door_closed__Controller_closed.status = &Door_closed__Controller_closed_status;
  
  __Door_closed__Controller_closed.inname ="closed";
  __Door_closed__Controller_closed.outname ="closed";
  __Door_closed__Controller_closed.mwmr_fifo = &Door_closed__Controller_closed;
  Door_closed__Controller_closed.status =&Door_closed__Controller_closed_status;
  Door_closed__Controller_closed.status->lock=0;
  Door_closed__Controller_closed.status->rptr=0;
  Door_closed__Controller_closed.status->usage=0;
  Door_closed__Controller_closed.status->wptr =0;
  Controller_startMagnetron__Magnetron_startM_status.rptr = 0;
  Controller_startMagnetron__Magnetron_startM_status.wptr = 0;
  Controller_startMagnetron__Magnetron_startM_status.usage = 0;
  Controller_startMagnetron__Magnetron_startM_status.lock = 0;
  
  Controller_startMagnetron__Magnetron_startM.width = 1;
  Controller_startMagnetron__Magnetron_startM.depth = 1;
  Controller_startMagnetron__Magnetron_startM.gdepth = 1;
  Controller_startMagnetron__Magnetron_startM.buffer = Controller_startMagnetron__Magnetron_startM_data;
  Controller_startMagnetron__Magnetron_startM.status = &Controller_startMagnetron__Magnetron_startM_status;
  
  __Controller_startMagnetron__Magnetron_startM.inname ="startM";
  __Controller_startMagnetron__Magnetron_startM.outname ="startMagnetron";
  __Controller_startMagnetron__Magnetron_startM.mwmr_fifo = &Controller_startMagnetron__Magnetron_startM;
  Controller_startMagnetron__Magnetron_startM.status =&Controller_startMagnetron__Magnetron_startM_status;
  Controller_startMagnetron__Magnetron_startM.status->lock=0;
  Controller_startMagnetron__Magnetron_startM.status->rptr=0;
  Controller_startMagnetron__Magnetron_startM.status->usage=0;
  Controller_startMagnetron__Magnetron_startM.status->wptr =0;
  Controller_stopMagnetron__Magnetron_stopM_status.rptr = 0;
  Controller_stopMagnetron__Magnetron_stopM_status.wptr = 0;
  Controller_stopMagnetron__Magnetron_stopM_status.usage = 0;
  Controller_stopMagnetron__Magnetron_stopM_status.lock = 0;
  
  Controller_stopMagnetron__Magnetron_stopM.width = 1;
  Controller_stopMagnetron__Magnetron_stopM.depth = 1;
  Controller_stopMagnetron__Magnetron_stopM.gdepth = 1;
  Controller_stopMagnetron__Magnetron_stopM.buffer = Controller_stopMagnetron__Magnetron_stopM_data;
  Controller_stopMagnetron__Magnetron_stopM.status = &Controller_stopMagnetron__Magnetron_stopM_status;
  
  __Controller_stopMagnetron__Magnetron_stopM.inname ="stopM";
  __Controller_stopMagnetron__Magnetron_stopM.outname ="stopMagnetron";
  __Controller_stopMagnetron__Magnetron_stopM.mwmr_fifo = &Controller_stopMagnetron__Magnetron_stopM;
  Controller_stopMagnetron__Magnetron_stopM.status =&Controller_stopMagnetron__Magnetron_stopM_status;
  Controller_stopMagnetron__Magnetron_stopM.status->lock=0;
  Controller_stopMagnetron__Magnetron_stopM.status->rptr=0;
  Controller_stopMagnetron__Magnetron_stopM.status->usage=0;
  Controller_stopMagnetron__Magnetron_stopM.status->wptr =0;
  ControlPanel_LEDOn__Controller_startCooking_status.rptr = 0;
  ControlPanel_LEDOn__Controller_startCooking_status.wptr = 0;
  ControlPanel_LEDOn__Controller_startCooking_status.usage = 0;
  ControlPanel_LEDOn__Controller_startCooking_status.lock = 0;
  
  ControlPanel_LEDOn__Controller_startCooking.width = 1;
  ControlPanel_LEDOn__Controller_startCooking.depth = 1;
  ControlPanel_LEDOn__Controller_startCooking.gdepth = 1;
  ControlPanel_LEDOn__Controller_startCooking.buffer = ControlPanel_LEDOn__Controller_startCooking_data;
  ControlPanel_LEDOn__Controller_startCooking.status = &ControlPanel_LEDOn__Controller_startCooking_status;
  
  __ControlPanel_LEDOn__Controller_startCooking.inname ="LEDOn";
  __ControlPanel_LEDOn__Controller_startCooking.outname ="startCooking";
  __ControlPanel_LEDOn__Controller_startCooking.mwmr_fifo = &ControlPanel_LEDOn__Controller_startCooking;
  ControlPanel_LEDOn__Controller_startCooking.status =&ControlPanel_LEDOn__Controller_startCooking_status;
  ControlPanel_LEDOn__Controller_startCooking.status->lock=0;
  ControlPanel_LEDOn__Controller_startCooking.status->rptr=0;
  ControlPanel_LEDOn__Controller_startCooking.status->usage=0;
  ControlPanel_LEDOn__Controller_startCooking.status->wptr =0;
  ControlPanel_LEDoff__Controller_stopCooking_status.rptr = 0;
  ControlPanel_LEDoff__Controller_stopCooking_status.wptr = 0;
  ControlPanel_LEDoff__Controller_stopCooking_status.usage = 0;
  ControlPanel_LEDoff__Controller_stopCooking_status.lock = 0;
  
  ControlPanel_LEDoff__Controller_stopCooking.width = 1;
  ControlPanel_LEDoff__Controller_stopCooking.depth = 1;
  ControlPanel_LEDoff__Controller_stopCooking.gdepth = 1;
  ControlPanel_LEDoff__Controller_stopCooking.buffer = ControlPanel_LEDoff__Controller_stopCooking_data;
  ControlPanel_LEDoff__Controller_stopCooking.status = &ControlPanel_LEDoff__Controller_stopCooking_status;
  
  __ControlPanel_LEDoff__Controller_stopCooking.inname ="LEDoff";
  __ControlPanel_LEDoff__Controller_stopCooking.outname ="stopCooking";
  __ControlPanel_LEDoff__Controller_stopCooking.mwmr_fifo = &ControlPanel_LEDoff__Controller_stopCooking;
  ControlPanel_LEDoff__Controller_stopCooking.status =&ControlPanel_LEDoff__Controller_stopCooking_status;
  ControlPanel_LEDoff__Controller_stopCooking.status->lock=0;
  ControlPanel_LEDoff__Controller_stopCooking.status->rptr=0;
  ControlPanel_LEDoff__Controller_stopCooking.status->usage=0;
  ControlPanel_LEDoff__Controller_stopCooking.status->wptr =0;
  ControlPanel_startButton__Controller_start_status.rptr = 0;
  ControlPanel_startButton__Controller_start_status.wptr = 0;
  ControlPanel_startButton__Controller_start_status.usage = 0;
  ControlPanel_startButton__Controller_start_status.lock = 0;
  
  ControlPanel_startButton__Controller_start.width = 1;
  ControlPanel_startButton__Controller_start.depth = 1;
  ControlPanel_startButton__Controller_start.gdepth = 1;
  ControlPanel_startButton__Controller_start.buffer = ControlPanel_startButton__Controller_start_data;
  ControlPanel_startButton__Controller_start.status = &ControlPanel_startButton__Controller_start_status;
  
  __ControlPanel_startButton__Controller_start.inname ="start";
  __ControlPanel_startButton__Controller_start.outname ="startButton";
  __ControlPanel_startButton__Controller_start.mwmr_fifo = &ControlPanel_startButton__Controller_start;
  ControlPanel_startButton__Controller_start.status =&ControlPanel_startButton__Controller_start_status;
  ControlPanel_startButton__Controller_start.status->lock=0;
  ControlPanel_startButton__Controller_start.status->rptr=0;
  ControlPanel_startButton__Controller_start.status->usage=0;
  ControlPanel_startButton__Controller_start.status->wptr =0;
  
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
  /* Activating debug messages */
  activeDebug();
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* User initialization */
  __user_init();
  
  
  debugMsg("Starting tasks");
  struct mwmr_s *channels_array_MicroWaveOven;
  ptr =malloc(sizeof(pthread_t));
  thread__MicroWaveOven= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 1);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__MicroWaveOven, attr_t, mainFunc__MicroWaveOven, (void *)channels_array_MicroWaveOven);
  
  struct mwmr_s *channels_array_Bell[1];
  channels_array_Bell[0]=&Controller_ringBell__Bell_ring;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Bell= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__Bell, attr_t, mainFunc__Bell, (void *)channels_array_Bell);
  
  struct mwmr_s *channels_array_ControlPanel[3];
  channels_array_ControlPanel[0]=&ControlPanel_LEDOn__Controller_startCooking;
  channels_array_ControlPanel[1]=&ControlPanel_LEDoff__Controller_stopCooking;
  channels_array_ControlPanel[2]=&ControlPanel_startButton__Controller_start;
  
  ptr =malloc(sizeof(pthread_t));
  thread__ControlPanel= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__ControlPanel, attr_t, mainFunc__ControlPanel, (void *)channels_array_ControlPanel);
  
  struct mwmr_s *channels_array_Controller[9];
  channels_array_Controller[0]=&Controller_ringBell__Bell_ring;
  channels_array_Controller[1]=&Door_okDoor__Controller_okDoor;
  channels_array_Controller[2]=&Door_open__Controller_open;
  channels_array_Controller[3]=&Door_closed__Controller_closed;
  channels_array_Controller[4]=&Controller_startMagnetron__Magnetron_startM;
  channels_array_Controller[5]=&Controller_stopMagnetron__Magnetron_stopM;
  channels_array_Controller[6]=&ControlPanel_LEDOn__Controller_startCooking;
  channels_array_Controller[7]=&ControlPanel_LEDoff__Controller_stopCooking;
  channels_array_Controller[8]=&ControlPanel_startButton__Controller_start;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Controller= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 1);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__Controller, attr_t, mainFunc__Controller, (void *)channels_array_Controller);
  
  struct mwmr_s *channels_array_Magnetron[2];
  channels_array_Magnetron[0]=&Controller_startMagnetron__Magnetron_startM;
  channels_array_Magnetron[1]=&Controller_stopMagnetron__Magnetron_stopM;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Magnetron= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 1);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__Magnetron, attr_t, mainFunc__Magnetron, (void *)channels_array_Magnetron);
  
  struct mwmr_s *channels_array_Door[3];
  channels_array_Door[0]=&Door_okDoor__Controller_okDoor;
  channels_array_Door[1]=&Door_open__Controller_open;
  channels_array_Door[2]=&Door_closed__Controller_closed;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Door= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__Door, attr_t, mainFunc__Door, (void *)channels_array_Door);
  
  
  
  debugMsg("Joining tasks");
  pthread_join(thread__MicroWaveOven, NULL);
  pthread_join(thread__Bell, NULL);
  pthread_join(thread__ControlPanel, NULL);
  pthread_join(thread__Controller, NULL);
  pthread_join(thread__Magnetron, NULL);
  pthread_join(thread__Door, NULL);
  
  
  debugMsg("Application terminated");
  return 0;
  
}
