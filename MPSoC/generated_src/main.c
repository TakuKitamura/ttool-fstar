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
 

#define NB_PROC 5
#define WIDTH 4
#define DEPTH 16

void __user_init() {
}

#include "TestBench.h"
#include "SpeedSensor.h"
#include "RadarSensor.h"
#include "GPSSensor.h"
#include "CarPositionSimulator.h"
#include "EmergencySimulator.h"
#include "Communication.h"
#include "DSRSC_Management.h"
#include "NeighbourhoodTableManagement.h"
#include "CorrectnessChecking.h"
#include "PTC.h"
#include "DrivingPowerReductionStrategy.h"
#include "BCU.h"
#include "DangerAvoidanceStrategy.h"
#include "BrakeManagement.h"
#include "CSCU.h"
#include "ObjectListManagement.h"
#include "PlausibilityCheck.h"
#include "VehiculeDynamicsManagement.h"

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
uint32_t *const DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency_lock= LOCKSADDR+0x0;
struct mwmr_status_s DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency_data[32];
struct mwmr_s DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency= MWMR_INITIALIZER(1, 1, DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency_data,&DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency_status,"DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency",DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency_lock);

syncchannel __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
uint32_t *const NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList_lock= LOCKSADDR+0x0;
struct mwmr_status_s NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList_data[32];
struct mwmr_s NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList= MWMR_INITIALIZER(1, 1, NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList_data,&NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList_status,"NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList",NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList_lock);

syncchannel __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
uint32_t *const DangerAvoidanceStrategy_brakePower__BrakeManagement_brake_lock= LOCKSADDR+0x0;
struct mwmr_status_s DangerAvoidanceStrategy_brakePower__BrakeManagement_brake_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t DangerAvoidanceStrategy_brakePower__BrakeManagement_brake_data[32];
struct mwmr_s DangerAvoidanceStrategy_brakePower__BrakeManagement_brake= MWMR_INITIALIZER(1, 1, DangerAvoidanceStrategy_brakePower__BrakeManagement_brake_data,&DangerAvoidanceStrategy_brakePower__BrakeManagement_brake_status,"DangerAvoidanceStrategy_brakePower__BrakeManagement_brake",DangerAvoidanceStrategy_brakePower__BrakeManagement_brake_lock);

syncchannel __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
uint32_t *const DangerAvoidanceStrategy_brake__PlausibilityCheck_brake_lock= LOCKSADDR+0x0;
struct mwmr_status_s DangerAvoidanceStrategy_brake__PlausibilityCheck_brake_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t DangerAvoidanceStrategy_brake__PlausibilityCheck_brake_data[32];
struct mwmr_s DangerAvoidanceStrategy_brake__PlausibilityCheck_brake= MWMR_INITIALIZER(1, 1, DangerAvoidanceStrategy_brake__PlausibilityCheck_brake_data,&DangerAvoidanceStrategy_brake__PlausibilityCheck_brake_status,"DangerAvoidanceStrategy_brake__PlausibilityCheck_brake",DangerAvoidanceStrategy_brake__PlausibilityCheck_brake_lock);

syncchannel __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed;
uint32_t *const PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed_lock= LOCKSADDR+0x0;
struct mwmr_status_s PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed_data[32];
struct mwmr_s PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed= MWMR_INITIALIZER(1, 1, PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed_data,&PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed_status,"PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed",PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed_lock);

syncchannel __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed;
uint32_t *const SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed_lock= LOCKSADDR+0x0;
struct mwmr_status_s SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed_data[32];
struct mwmr_s SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed= MWMR_INITIALIZER(1, 1, SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed_data,&SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed_status,"SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed",SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed_lock);

syncchannel __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
uint32_t *const PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle_lock= LOCKSADDR+0x0;
struct mwmr_status_s PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle_data[32];
struct mwmr_s PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle= MWMR_INITIALIZER(1, 1, PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle_data,&PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle_status,"PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle",PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle_lock);

syncchannel __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
uint32_t *const RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead_lock= LOCKSADDR+0x0;
struct mwmr_status_s RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead_data[32];
struct mwmr_s RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead= MWMR_INITIALIZER(1, 1, RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead_data,&RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead_status,"RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead",RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead_lock);

syncchannel __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
uint32_t *const CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify_lock= LOCKSADDR+0x0;
struct mwmr_status_s CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify_data[32];
struct mwmr_s CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify= MWMR_INITIALIZER(1, 1, CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify_data,&CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify_status,"CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify",CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify_lock);

syncchannel __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
uint32_t *const DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage_lock= LOCKSADDR+0x0;
struct mwmr_status_s DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage_data[32];
struct mwmr_s DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage= MWMR_INITIALIZER(1, 1, DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage_data,&DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage_status,"DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage",DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage_lock);

syncchannel __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
uint32_t *const DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode_lock= LOCKSADDR+0x0;
struct mwmr_status_s DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode_data[32];
struct mwmr_s DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode= MWMR_INITIALIZER(1, 1, DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode_data,&DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode_status,"DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode",DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode_lock);

syncchannel __CarPositionSimulator_carPosition__DSRSC_Management_carPosition;
uint32_t *const CarPositionSimulator_carPosition__DSRSC_Management_carPosition_lock= LOCKSADDR+0x0;
struct mwmr_status_s CarPositionSimulator_carPosition__DSRSC_Management_carPosition_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t CarPositionSimulator_carPosition__DSRSC_Management_carPosition_data[32];
struct mwmr_s CarPositionSimulator_carPosition__DSRSC_Management_carPosition= MWMR_INITIALIZER(1, 1, CarPositionSimulator_carPosition__DSRSC_Management_carPosition_data,&CarPositionSimulator_carPosition__DSRSC_Management_carPosition_status,"CarPositionSimulator_carPosition__DSRSC_Management_carPosition",CarPositionSimulator_carPosition__DSRSC_Management_carPosition_lock);

syncchannel __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
uint32_t *const EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected_lock= LOCKSADDR+0x0;
struct mwmr_status_s EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected_data[32];
struct mwmr_s EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected= MWMR_INITIALIZER(1, 1, EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected_data,&EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected_status,"EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected",EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected_lock);

syncchannel __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;
uint32_t *const GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition_lock= LOCKSADDR+0x0;
struct mwmr_status_s GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition_data[32];
struct mwmr_s GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition= MWMR_INITIALIZER(1, 1, GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition_data,&GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition_status,"GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition",GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition_lock);

/* Asynchronous channels */
asyncchannel __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
uint32_t *const DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder_lock= LOCKSADDR+0x0;
struct mwmr_status_s DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder_status =  MWMR_STATUS_INITIALIZER(1, 1);
uint8_t DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder_data[32*2];
struct mwmr_s DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder= MWMR_INITIALIZER(1, 1, DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder_data,&DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder_status,"DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder",DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder_lock);


int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  /* Synchronous channels */
  __DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency.inname ="broadcastEmergencyBrakingMessage";
  __DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency.outname ="forwardEmergency";
  __DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency.mwmr_fifo = &DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency;
  __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList.inname ="getNodeList";
  __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList.outname ="sendTable";
  __NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList.mwmr_fifo = &NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
  __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake.inname ="brake";
  __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake.outname ="brakePower";
  __DangerAvoidanceStrategy_brakePower__BrakeManagement_brake.mwmr_fifo = &DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
  __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake.inname ="brake";
  __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake.outname ="brake";
  __DangerAvoidanceStrategy_brake__PlausibilityCheck_brake.mwmr_fifo = &DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
  __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed.inname ="getInfoOnSpeed";
  __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed.outname ="getInfoOnSpeed";
  __PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed.mwmr_fifo = &PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed;
  __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed.inname ="updateOnSpeed";
  __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed.outname ="updateOnSpeed";
  __SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed.mwmr_fifo = &SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed;
  __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle.inname ="getInfoOnObstacle";
  __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle.outname ="getInfoOnObstacle";
  __PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle.mwmr_fifo = &PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
  __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead.inname ="isObstacleAhead";
  __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead.outname ="obstacleAhead";
  __RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead.mwmr_fifo = &RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
  __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify.inname ="getEmergencyMessageToVerify";
  __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify.outname ="toPlausibityCheckMessage";
  __CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify.mwmr_fifo = &CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
  __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage.inname ="getEmergencyBrakingMessage";
  __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage.outname ="forwardEmergencyBrakingMessage";
  __DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage.mwmr_fifo = &DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
  __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode.inname ="addANode";
  __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode.outname ="setCarPosition";
  __DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode.mwmr_fifo = &DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
  __CarPositionSimulator_carPosition__DSRSC_Management_carPosition.inname ="carPosition";
  __CarPositionSimulator_carPosition__DSRSC_Management_carPosition.outname ="carPosition";
  __CarPositionSimulator_carPosition__DSRSC_Management_carPosition.mwmr_fifo = &CarPositionSimulator_carPosition__DSRSC_Management_carPosition;
  __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected.inname ="obstacleDetected";
  __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected.outname ="obstacleDetected";
  __EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected.mwmr_fifo = &EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
  __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition.inname ="setPosition";
  __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition.outname ="setPosition";
  __GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition.mwmr_fifo = &GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;
  /* Asynchronous channels */
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.inname ="getReducePowerOrder";
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.outname ="reducePower";
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.isBlocking = 0;
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.maxNbOfMessages = 1;
  __DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder.mwmr_fifo = &DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
  
  /* Threads of tasks */
  pthread_t thread__TestBench;
  pthread_t thread__SpeedSensor;
  pthread_t thread__RadarSensor;
  pthread_t thread__GPSSensor;
  pthread_t thread__CarPositionSimulator;
  pthread_t thread__EmergencySimulator;
  pthread_t thread__Communication;
  pthread_t thread__DSRSC_Management;
  pthread_t thread__NeighbourhoodTableManagement;
  pthread_t thread__CorrectnessChecking;
  pthread_t thread__PTC;
  pthread_t thread__DrivingPowerReductionStrategy;
  pthread_t thread__BCU;
  pthread_t thread__DangerAvoidanceStrategy;
  pthread_t thread__BrakeManagement;
  pthread_t thread__CSCU;
  pthread_t thread__ObjectListManagement;
  pthread_t thread__PlausibilityCheck;
  pthread_t thread__VehiculeDynamicsManagement;
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
  attr_t->cpucount = 4;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__TestBench, NULL, mainFunc__TestBench, (void *)channels_array_TestBench);
  
  struct mwmr_s *channels_array_SpeedSensor[1];
  channels_array_SpeedSensor[0]=&SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed;
  
  ptr =malloc(sizeof(pthread_t));
  thread__SpeedSensor= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 4;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__SpeedSensor, NULL, mainFunc__SpeedSensor, (void *)channels_array_SpeedSensor);
  
  struct mwmr_s *channels_array_RadarSensor[1];
  channels_array_RadarSensor[0]=&RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
  
  ptr =malloc(sizeof(pthread_t));
  thread__RadarSensor= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 4;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__RadarSensor, NULL, mainFunc__RadarSensor, (void *)channels_array_RadarSensor);
  
  struct mwmr_s *channels_array_GPSSensor[1];
  channels_array_GPSSensor[0]=&GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;
  
  ptr =malloc(sizeof(pthread_t));
  thread__GPSSensor= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 4;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__GPSSensor, NULL, mainFunc__GPSSensor, (void *)channels_array_GPSSensor);
  
  struct mwmr_s *channels_array_CarPositionSimulator[1];
  channels_array_CarPositionSimulator[0]=&CarPositionSimulator_carPosition__DSRSC_Management_carPosition;
  
  ptr =malloc(sizeof(pthread_t));
  thread__CarPositionSimulator= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 4;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__CarPositionSimulator, NULL, mainFunc__CarPositionSimulator, (void *)channels_array_CarPositionSimulator);
  
  struct mwmr_s *channels_array_EmergencySimulator[1];
  channels_array_EmergencySimulator[0]=&EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
  
  ptr =malloc(sizeof(pthread_t));
  thread__EmergencySimulator= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 4;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__EmergencySimulator, NULL, mainFunc__EmergencySimulator, (void *)channels_array_EmergencySimulator);
  
  struct mwmr_s *channels_array_Communication;
  ptr =malloc(sizeof(pthread_t));
  thread__Communication= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__Communication, NULL, mainFunc__Communication, (void *)channels_array_Communication);
  
  struct mwmr_s *channels_array_DSRSC_Management[5];
  channels_array_DSRSC_Management[0]=&DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency;
  channels_array_DSRSC_Management[1]=&DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
  channels_array_DSRSC_Management[2]=&DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
  channels_array_DSRSC_Management[3]=&CarPositionSimulator_carPosition__DSRSC_Management_carPosition;
  channels_array_DSRSC_Management[4]=&EmergencySimulator_obstacleDetected__DSRSC_Management_obstacleDetected;
  
  ptr =malloc(sizeof(pthread_t));
  thread__DSRSC_Management= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__DSRSC_Management, NULL, mainFunc__DSRSC_Management, (void *)channels_array_DSRSC_Management);
  
  struct mwmr_s *channels_array_NeighbourhoodTableManagement[3];
  channels_array_NeighbourhoodTableManagement[0]=&NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
  channels_array_NeighbourhoodTableManagement[1]=&DSRSC_Management_setCarPosition__NeighbourhoodTableManagement_addANode;
  channels_array_NeighbourhoodTableManagement[2]=&GPSSensor_setPosition__NeighbourhoodTableManagement_setPosition;
  
  ptr =malloc(sizeof(pthread_t));
  thread__NeighbourhoodTableManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__NeighbourhoodTableManagement, NULL, mainFunc__NeighbourhoodTableManagement, (void *)channels_array_NeighbourhoodTableManagement);
  
  struct mwmr_s *channels_array_CorrectnessChecking[2];
  channels_array_CorrectnessChecking[0]=&CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
  channels_array_CorrectnessChecking[1]=&DSRSC_Management_forwardEmergencyBrakingMessage__CorrectnessChecking_getEmergencyBrakingMessage;
  
  ptr =malloc(sizeof(pthread_t));
  thread__CorrectnessChecking= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 1;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__CorrectnessChecking, NULL, mainFunc__CorrectnessChecking, (void *)channels_array_CorrectnessChecking);
  
  struct mwmr_s *channels_array_PTC;
  ptr =malloc(sizeof(pthread_t));
  thread__PTC= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 2;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__PTC, NULL, mainFunc__PTC, (void *)channels_array_PTC);
  
  struct mwmr_s *channels_array_DrivingPowerReductionStrategy[1];
  channels_array_DrivingPowerReductionStrategy[0]=&DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
  
  ptr =malloc(sizeof(pthread_t));
  thread__DrivingPowerReductionStrategy= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 2;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__DrivingPowerReductionStrategy, NULL, mainFunc__DrivingPowerReductionStrategy, (void *)channels_array_DrivingPowerReductionStrategy);
  
  struct mwmr_s *channels_array_BCU;
  ptr =malloc(sizeof(pthread_t));
  thread__BCU= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 3;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__BCU, NULL, mainFunc__BCU, (void *)channels_array_BCU);
  
  struct mwmr_s *channels_array_DangerAvoidanceStrategy[4];
  channels_array_DangerAvoidanceStrategy[0]=&DSRSC_Management_broadcastEmergencyBrakingMessage__DangerAvoidanceStrategy_forwardEmergency;
  channels_array_DangerAvoidanceStrategy[1]=&DangerAvoidanceStrategy_reducePower__DrivingPowerReductionStrategy_getReducePowerOrder;
  channels_array_DangerAvoidanceStrategy[2]=&DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
  channels_array_DangerAvoidanceStrategy[3]=&DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
  
  ptr =malloc(sizeof(pthread_t));
  thread__DangerAvoidanceStrategy= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 3;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__DangerAvoidanceStrategy, NULL, mainFunc__DangerAvoidanceStrategy, (void *)channels_array_DangerAvoidanceStrategy);
  
  struct mwmr_s *channels_array_BrakeManagement[1];
  channels_array_BrakeManagement[0]=&DangerAvoidanceStrategy_brakePower__BrakeManagement_brake;
  
  ptr =malloc(sizeof(pthread_t));
  thread__BrakeManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 3;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__BrakeManagement, NULL, mainFunc__BrakeManagement, (void *)channels_array_BrakeManagement);
  
  struct mwmr_s *channels_array_CSCU;
  ptr =malloc(sizeof(pthread_t));
  thread__CSCU= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__CSCU, NULL, mainFunc__CSCU, (void *)channels_array_CSCU);
  
  struct mwmr_s *channels_array_ObjectListManagement[2];
  channels_array_ObjectListManagement[0]=&PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
  channels_array_ObjectListManagement[1]=&RadarSensor_obstacleAhead__ObjectListManagement_isObstacleAhead;
  
  ptr =malloc(sizeof(pthread_t));
  thread__ObjectListManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__ObjectListManagement, NULL, mainFunc__ObjectListManagement, (void *)channels_array_ObjectListManagement);
  
  struct mwmr_s *channels_array_PlausibilityCheck[5];
  channels_array_PlausibilityCheck[0]=&NeighbourhoodTableManagement_sendTable__PlausibilityCheck_getNodeList;
  channels_array_PlausibilityCheck[1]=&DangerAvoidanceStrategy_brake__PlausibilityCheck_brake;
  channels_array_PlausibilityCheck[2]=&PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed;
  channels_array_PlausibilityCheck[3]=&PlausibilityCheck_getInfoOnObstacle__ObjectListManagement_getInfoOnObstacle;
  channels_array_PlausibilityCheck[4]=&CorrectnessChecking_toPlausibityCheckMessage__PlausibilityCheck_getEmergencyMessageToVerify;
  
  ptr =malloc(sizeof(pthread_t));
  thread__PlausibilityCheck= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__PlausibilityCheck, NULL, mainFunc__PlausibilityCheck, (void *)channels_array_PlausibilityCheck);
  
  struct mwmr_s *channels_array_VehiculeDynamicsManagement[2];
  channels_array_VehiculeDynamicsManagement[0]=&PlausibilityCheck_getInfoOnSpeed__VehiculeDynamicsManagement_getInfoOnSpeed;
  channels_array_VehiculeDynamicsManagement[1]=&SpeedSensor_updateOnSpeed__VehiculeDynamicsManagement_updateOnSpeed;
  
  ptr =malloc(sizeof(pthread_t));
  thread__VehiculeDynamicsManagement= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  attr_t->cpucount = 0;  
  
  
  debugMsg("Starting tasks");
  pthread_create(&thread__VehiculeDynamicsManagement, NULL, mainFunc__VehiculeDynamicsManagement, (void *)channels_array_VehiculeDynamicsManagement);
  
  
  
  debugMsg("Joining tasks");
  pthread_join(thread__TestBench, NULL);
  pthread_join(thread__SpeedSensor, NULL);
  pthread_join(thread__RadarSensor, NULL);
  pthread_join(thread__GPSSensor, NULL);
  pthread_join(thread__CarPositionSimulator, NULL);
  pthread_join(thread__EmergencySimulator, NULL);
  pthread_join(thread__Communication, NULL);
  pthread_join(thread__DSRSC_Management, NULL);
  pthread_join(thread__NeighbourhoodTableManagement, NULL);
  pthread_join(thread__CorrectnessChecking, NULL);
  pthread_join(thread__PTC, NULL);
  pthread_join(thread__DrivingPowerReductionStrategy, NULL);
  pthread_join(thread__BCU, NULL);
  pthread_join(thread__DangerAvoidanceStrategy, NULL);
  pthread_join(thread__BrakeManagement, NULL);
  pthread_join(thread__CSCU, NULL);
  pthread_join(thread__ObjectListManagement, NULL);
  pthread_join(thread__PlausibilityCheck, NULL);
  pthread_join(thread__VehiculeDynamicsManagement, NULL);
  
  
  debugMsg("Application terminated");
  return 0;
  
}
