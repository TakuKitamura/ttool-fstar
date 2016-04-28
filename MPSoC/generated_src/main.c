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
 

#define NB_PROC 1
#define WIDTH 4
#define DEPTH 16

void __user_init() {
}

#include "TestBench.h"
#include "EmergencySimulator.h"
#include "CarPositionSimulator.h"
#include "GPSSensor.h"
#include "RadarSensor.h"
#include "SpeedSensor.h"
#include "Communication.h"
#include "CorrectnessChecking.h"
#include "NeighbourhoodTableManagement.h"
#include "DSRSC_Management.h"
#include "PTC.h"
#include "DrivingPowerReductionStrategy.h"
#include "BCU.h"
#include "BrakeManagement.h"
#include "DangerAvoidanceStrategy.h"
#include "CSCU.h"
#include "VehiculeDynamicsManagement.h"
#include "PlausibilityCheck.h"
#include "ObjectListManagement.h"

/* Main mutex */
pthread_barrier_t barrier ;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

#define MWMRADDR 0x20200000
#define LOCKSADDR 0x30200000
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

/* Synchronous channels */
syncchannel __DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency;
syncchannel __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
syncchannel __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
syncchannel __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
syncchannel __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
syncchannel __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed;
syncchannel __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed;
syncchannel __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
syncchannel __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
syncchannel __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
syncchannel __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
syncchannel __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
syncchannel __CarPositionSimulator_carPosition__DSRSC_Management_carPosition;
syncchannel __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
syncchannel __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;

int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  /* Synchronous channels */
  __DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency.inname ="broadcastEmergencyBrakingMessage";
  __DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency.outname ="forwardEmergency";
  __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList.inname ="getNodeList";
  __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList.outname ="sendTable";
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.inname ="getReducePowerOrder";
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.outname ="reducePower";
  __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake.inname ="brake";
  __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake.outname ="brakePower";
  __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake.inname ="brake";
  __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake.outname ="brake";
  __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed.inname ="getInfoOnSpeed";
  __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed.outname ="getInfoOnSpeed";
  __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed.inname ="updateOnSpeed";
  __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed.outname ="updateOnSpeed";
  __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle.inname ="getInfoOnObstacle";
  __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle.outname ="getInfoOnObstacle";
  __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead.inname ="isObstacleAhead";
  __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead.outname ="obstacleAhead";
  __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify.inname ="getEmergencyMessageToVerify";
  __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify.outname ="toPlausibityCheckMessage";
  __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage.inname ="getEmergencyBrakingMessage";
  __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage.outname ="forwardEmergencyBrakingMessage";
  __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode.inname ="addANode";
  __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode.outname ="setCarPosition";
  __CarPositionSimulator_carPosition__DSRSC_Management_carPosition.inname ="carPosition";
  __CarPositionSimulator_carPosition__DSRSC_Management_carPosition.outname ="carPosition";
  __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected.inname ="obstacleDetected";
  __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected.outname ="obstacleDetected";
  __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition.inname ="setPosition";
  __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition.outname ="setPosition";
  
  /* Threads of tasks */
  pthread_t thread__TestBench;
  pthread_t thread__EmergencySimulator;
  pthread_t thread__CarPositionSimulator;
  pthread_t thread__GPSSensor;
  pthread_t thread__RadarSensor;
  pthread_t thread__SpeedSensor;
  pthread_t thread__Communication;
  pthread_t thread__CorrectnessChecking;
  pthread_t thread__NeighbourhoodTableManagement;
  pthread_t thread__DSRSC_Management;
  pthread_t thread__PTC;
  pthread_t thread__DrivingPowerReductionStrategy;
  pthread_t thread__BCU;
  pthread_t thread__BrakeManagement;
  pthread_t thread__DangerAvoidanceStrategy;
  pthread_t thread__CSCU;
  pthread_t thread__VehiculeDynamicsManagement;
  pthread_t thread__PlausibilityCheck;
  pthread_t thread__ObjectListManagement;
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
  struct mwmr_s *channels_array_TestBench;
  ptr =malloc(sizeof(pthread_t));
  thread__TestBench= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__TestBench, NULL, mainFunc__TestBench, (void *)channels_array_TestBench);
  
  struct mwmr_s *channels_array_EmergencySimulator;
  ptr =malloc(sizeof(pthread_t));
  thread__EmergencySimulator= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__EmergencySimulator, NULL, mainFunc__EmergencySimulator, (void *)channels_array_EmergencySimulator);
  
  struct mwmr_s *channels_array_CarPositionSimulator;
  ptr =malloc(sizeof(pthread_t));
  thread__CarPositionSimulator= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__CarPositionSimulator, NULL, mainFunc__CarPositionSimulator, (void *)channels_array_CarPositionSimulator);
  
  struct mwmr_s *channels_array_GPSSensor;
  ptr =malloc(sizeof(pthread_t));
  thread__GPSSensor= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__GPSSensor, NULL, mainFunc__GPSSensor, (void *)channels_array_GPSSensor);
  
  struct mwmr_s *channels_array_RadarSensor;
  ptr =malloc(sizeof(pthread_t));
  thread__RadarSensor= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__RadarSensor, NULL, mainFunc__RadarSensor, (void *)channels_array_RadarSensor);
  
  struct mwmr_s *channels_array_SpeedSensor;
  ptr =malloc(sizeof(pthread_t));
  thread__SpeedSensor= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__SpeedSensor, NULL, mainFunc__SpeedSensor, (void *)channels_array_SpeedSensor);
  
  struct mwmr_s *channels_array_Communication;
  ptr =malloc(sizeof(pthread_t));
  thread__Communication= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__Communication, NULL, mainFunc__Communication, (void *)channels_array_Communication);
  
  struct mwmr_s *channels_array_CorrectnessChecking;
  ptr =malloc(sizeof(pthread_t));
  thread__CorrectnessChecking= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__CorrectnessChecking, NULL, mainFunc__CorrectnessChecking, (void *)channels_array_CorrectnessChecking);
  
  struct mwmr_s *channels_array_NeighbourhoodTableManagement;
  ptr =malloc(sizeof(pthread_t));
  thread__NeighbourhoodTableManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__NeighbourhoodTableManagement, NULL, mainFunc__NeighbourhoodTableManagement, (void *)channels_array_NeighbourhoodTableManagement);
  
  struct mwmr_s *channels_array_DSRSC_Management;
  ptr =malloc(sizeof(pthread_t));
  thread__DSRSC_Management= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__DSRSC_Management, NULL, mainFunc__DSRSC_Management, (void *)channels_array_DSRSC_Management);
  
  struct mwmr_s *channels_array_PTC;
  ptr =malloc(sizeof(pthread_t));
  thread__PTC= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__PTC, NULL, mainFunc__PTC, (void *)channels_array_PTC);
  
  struct mwmr_s *channels_array_DrivingPowerReductionStrategy;
  ptr =malloc(sizeof(pthread_t));
  thread__DrivingPowerReductionStrategy= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__DrivingPowerReductionStrategy, NULL, mainFunc__DrivingPowerReductionStrategy, (void *)channels_array_DrivingPowerReductionStrategy);
  
  struct mwmr_s *channels_array_BCU;
  ptr =malloc(sizeof(pthread_t));
  thread__BCU= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__BCU, NULL, mainFunc__BCU, (void *)channels_array_BCU);
  
  struct mwmr_s *channels_array_BrakeManagement;
  ptr =malloc(sizeof(pthread_t));
  thread__BrakeManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__BrakeManagement, NULL, mainFunc__BrakeManagement, (void *)channels_array_BrakeManagement);
  
  struct mwmr_s *channels_array_DangerAvoidanceStrategy;
  ptr =malloc(sizeof(pthread_t));
  thread__DangerAvoidanceStrategy= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__DangerAvoidanceStrategy, NULL, mainFunc__DangerAvoidanceStrategy, (void *)channels_array_DangerAvoidanceStrategy);
  
  struct mwmr_s *channels_array_CSCU;
  ptr =malloc(sizeof(pthread_t));
  thread__CSCU= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__CSCU, NULL, mainFunc__CSCU, (void *)channels_array_CSCU);
  
  struct mwmr_s *channels_array_VehiculeDynamicsManagement;
  ptr =malloc(sizeof(pthread_t));
  thread__VehiculeDynamicsManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__VehiculeDynamicsManagement, NULL, mainFunc__VehiculeDynamicsManagement, (void *)channels_array_VehiculeDynamicsManagement);
  
  struct mwmr_s *channels_array_PlausibilityCheck;
  ptr =malloc(sizeof(pthread_t));
  thread__PlausibilityCheck= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__PlausibilityCheck, NULL, mainFunc__PlausibilityCheck, (void *)channels_array_PlausibilityCheck);
  
  struct mwmr_s *channels_array_ObjectListManagement;
  ptr =malloc(sizeof(pthread_t));
  thread__ObjectListManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__ObjectListManagement, NULL, mainFunc__ObjectListManagement, (void *)channels_array_ObjectListManagement);
  
  
  
  debugMsg("Joining tasks");
  pthread_join(thread__TestBench, NULL);
  pthread_join(thread__EmergencySimulator, NULL);
  pthread_join(thread__CarPositionSimulator, NULL);
  pthread_join(thread__GPSSensor, NULL);
  pthread_join(thread__RadarSensor, NULL);
  pthread_join(thread__SpeedSensor, NULL);
  pthread_join(thread__Communication, NULL);
  pthread_join(thread__CorrectnessChecking, NULL);
  pthread_join(thread__NeighbourhoodTableManagement, NULL);
  pthread_join(thread__DSRSC_Management, NULL);
  pthread_join(thread__PTC, NULL);
  pthread_join(thread__DrivingPowerReductionStrategy, NULL);
  pthread_join(thread__BCU, NULL);
  pthread_join(thread__BrakeManagement, NULL);
  pthread_join(thread__DangerAvoidanceStrategy, NULL);
  pthread_join(thread__CSCU, NULL);
  pthread_join(thread__VehiculeDynamicsManagement, NULL);
  pthread_join(thread__PlausibilityCheck, NULL);
  pthread_join(thread__ObjectListManagement, NULL);
  
  
  debugMsg("Application terminated");
  return 0;
  
}
