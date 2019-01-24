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
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <strings.h>
#include <string.h>
#include <errno.h>

const char* hostname="localhost";
const char* portname="8374";
int fd;
struct addrinfo* res;

#define MAX_DGRAM_SIZE  549

pthread_t thread__Datagram;

// Handling pressure datagrams
int pressure = 1;

void pressureDatagram(char *buf) {
    pressure = atoi(buf);
  printf("Pressure=%d\n", atoi(buf));
}

// Assumes fd is valid
void* receiveDatagram(void *arg) {
    printf("Thread receive datagram started\n");
  
    char buffer[MAX_DGRAM_SIZE];
    struct sockaddr_storage src_addr;
    socklen_t src_addr_len=sizeof(src_addr);
  
    while(1) {
        printf("Waiting for datagram packet\n");
        ssize_t count=recvfrom(fd,buffer,sizeof(buffer),0,(struct sockaddr*)&src_addr,&src_addr_len);
        if (count==-1) {
            perror("recv failed");
        } else if (count==sizeof(buffer)) {
            perror("datagram too large for buffer: truncated");
        } else {
            //printf("Datagram size: %d.\n", (int)(count));
            if (strncmp(buffer, "PRESSURE=", 9) == 0) {
                printf("+++++++++++++++++++++++ PRESSURE\n");
                pressureDatagram(buffer+9);
            }
        }
    }
}

void sendDatagram(char * data, int size) {
    printf("data=%s fd=%d size=%d\n", data, fd, size);
    if (sendto(fd,data,size, 0, res->ai_addr,res->ai_addrlen)==-1) {
            printf("Error when sending datagram");
            exit(-1);
      }
}

void __user_init() { 
    const char* content = "salut";
    struct addrinfo hints;
  
    memset(&hints,0,sizeof(hints));
    hints.ai_family=AF_UNSPEC;
    hints.ai_socktype=SOCK_DGRAM;
    hints.ai_protocol=0;
    hints.ai_flags=AI_ADDRCONFIG;
   
    int err=getaddrinfo(hostname,portname,&hints,&res);
    if (err!=0) {
        printf("failed to resolve remote socket address (err=%d)",err);
        exit(-1);
    }
    fd=socket(res->ai_family,res->ai_socktype,res->ai_protocol);
    if (fd==-1) {
        printf("%s",strerror(errno));
        exit(-1);
    }
    if (sendto(fd,content,sizeof(content),0,
        res->ai_addr,res->ai_addrlen)==-1) {
        printf("%s",strerror(errno));
        exit(-1);
    }
  
  // Start a thread to receive datagrams
    pthread_create(&thread__Datagram, NULL, receiveDatagram, NULL);
  
}

/* End of User code */

/* Main mutex */
pthread_mutex_t __mainMutex;

/* Synchronous channels */
syncchannel __AlarmManager_alarmOff__AlarmActuator_alarmOff;
syncchannel __AlarmManager_alarmOn__AlarmActuator_alarmOn;
syncchannel __MainController_highPressure__AlarmManager_highPressure;
syncchannel __PressureSensor_pressureValue__MainController_pressureValue;
syncchannel __AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set;
syncchannel __AlarmManager_reset__alarmTimer__Timer__alarmTimer__AlarmManager_reset;
syncchannel __AlarmManager_expire__alarmTimer__Timer__alarmTimer__AlarmManager_expire;
/* Asynchronous channels */

#include "AlarmActuator.h"
#include "PressureSensor.h"
#include "PressureController.h"
#include "AlarmManager.h"
#include "MainController.h"
#include "Timer__alarmTimer__AlarmManager.h"


int main(int argc, char *argv[]) {
  
  /* disable buffering on stdout */
  setvbuf(stdout, NULL, _IONBF, 0);
  
  /* Synchronous channels */
  __AlarmManager_alarmOff__AlarmActuator_alarmOff.inname ="alarmOff";
  __AlarmManager_alarmOff__AlarmActuator_alarmOff.outname ="alarmOff";
  __AlarmManager_alarmOn__AlarmActuator_alarmOn.inname ="alarmOn";
  __AlarmManager_alarmOn__AlarmActuator_alarmOn.outname ="alarmOn";
  __MainController_highPressure__AlarmManager_highPressure.inname ="highPressure";
  __MainController_highPressure__AlarmManager_highPressure.outname ="highPressure";
  __PressureSensor_pressureValue__MainController_pressureValue.inname ="pressureValue";
  __PressureSensor_pressureValue__MainController_pressureValue.outname ="pressureValue";
  __AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set.inname ="set";
  __AlarmManager_set__alarmTimer__Timer__alarmTimer__AlarmManager_set.outname ="set__alarmTimer";
  __AlarmManager_reset__alarmTimer__Timer__alarmTimer__AlarmManager_reset.inname ="reset";
  __AlarmManager_reset__alarmTimer__Timer__alarmTimer__AlarmManager_reset.outname ="reset__alarmTimer";
  __AlarmManager_expire__alarmTimer__Timer__alarmTimer__AlarmManager_expire.inname ="expire__alarmTimer";
  __AlarmManager_expire__alarmTimer__Timer__alarmTimer__AlarmManager_expire.outname ="expire";
  /* Asynchronous channels */
  
  /* Threads of tasks */
  pthread_t thread__AlarmActuator;
  pthread_t thread__PressureSensor;
  pthread_t thread__PressureController;
  pthread_t thread__AlarmManager;
  pthread_t thread__MainController;
  pthread_t thread__Timer__alarmTimer__AlarmManager;
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
  
  
  pthread_create(&thread__AlarmActuator, NULL, mainFunc__AlarmActuator, (void *)"AlarmActuator");
  pthread_create(&thread__PressureSensor, NULL, mainFunc__PressureSensor, (void *)"PressureSensor");
  pthread_create(&thread__PressureController, NULL, mainFunc__PressureController, (void *)"PressureController");
  pthread_create(&thread__AlarmManager, NULL, mainFunc__AlarmManager, (void *)"AlarmManager");
  pthread_create(&thread__MainController, NULL, mainFunc__MainController, (void *)"MainController");
  pthread_create(&thread__Timer__alarmTimer__AlarmManager, NULL, mainFunc__Timer__alarmTimer__AlarmManager, (void *)"Timer__alarmTimer__AlarmManager");
  
  
  pthread_join(thread__AlarmActuator, NULL);
  pthread_join(thread__PressureSensor, NULL);
  pthread_join(thread__PressureController, NULL);
  pthread_join(thread__AlarmManager, NULL);
  pthread_join(thread__MainController, NULL);
  pthread_join(thread__Timer__alarmTimer__AlarmManager, NULL);
  
  
  return 0;
  
}
