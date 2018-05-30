#ifndef MAIN_H
#define MAIN_H
/* Main mutex */
extern pthread_mutex_t __mainMutex;

/* Synchronous channels */
/* Asynchronous channels */
extern asyncchannel __Controller_ringBell__Bell_ring;
extern asyncchannel __Door_okDoor__Controller_okDoor;
extern asyncchannel __Door_open__Controller_open;
extern asyncchannel __Door_closed__Controller_closed;
extern asyncchannel __Controller_startMagnetron__Magnetron_startM;
extern asyncchannel __Controller_stopMagnetron__Magnetron_stopM;
extern asyncchannel __ControlPanel_LEDOn__Controller_startCooking;
extern asyncchannel __ControlPanel_LEDoff__Controller_stopCooking;
extern asyncchannel __ControlPanel_startButton__Controller_start;
#endif
