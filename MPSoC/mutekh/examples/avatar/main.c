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
 

#define NB_PROC 6
#define WIDTH 4
#define DEPTH 16

void __user_init() {
}

#include "Bootstrap.h"
#include "OutputEngine.h"
#include "InputEngine.h"
#include "Classification.h"
#include "Classif0.h"
#include "Classif1.h"
#include "Classif2.h"
#include "Scheduling.h"
#include "Sched1.h"
#include "Sched0.h"

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
#define CHANNEL4 __attribute__((section("section_channel4")))
#define LOCK4 __attribute__((section("section_lock4")))
#define CHANNEL5 __attribute__((section("section_channel5")))
#define LOCK5 __attribute__((section("section_lock5")))
#define CHANNEL6 __attribute__((section("section_channel6")))
#define LOCK6 __attribute__((section("section_lock6")))
#define CHANNEL7 __attribute__((section("section_channel7")))
#define LOCK7 __attribute__((section("section_lock7")))
#define CHANNEL8 __attribute__((section("section_channel8")))
#define LOCK8 __attribute__((section("section_lock8")))
#define CHANNEL9 __attribute__((section("section_channel9")))
#define LOCK9 __attribute__((section("section_lock9")))
#define CHANNEL10 __attribute__((section("section_channel10")))
#define LOCK10 __attribute__((section("section_lock10")))
#define CHANNEL11 __attribute__((section("section_channel11")))
#define LOCK11 __attribute__((section("section_lock11")))
#define CHANNEL12 __attribute__((section("section_channel12")))
#define LOCK12 __attribute__((section("section_lock12")))
#define CHANNEL13 __attribute__((section("section_channel13")))
#define LOCK13 __attribute__((section("section_lock13")))
#define CHANNEL14 __attribute__((section("section_channel14")))
#define LOCK14 __attribute__((section("section_lock14")))
#define CHANNEL15 __attribute__((section("section_channel15")))
#define LOCK15 __attribute__((section("section_lock15")))
#define CHANNEL16 __attribute__((section("section_channel16")))
#define LOCK16 __attribute__((section("section_lock16")))
#define CHANNEL17 __attribute__((section("section_channel17")))
#define LOCK17 __attribute__((section("section_lock17")))
#define CHANNEL18 __attribute__((section("section_channel18")))
#define LOCK18 __attribute__((section("section_lock18")))
#define CHANNEL19 __attribute__((section("section_channel19")))
#define LOCK19 __attribute__((section("section_lock19")))
#define CHANNEL20 __attribute__((section("section_channel20")))
#define LOCK20 __attribute__((section("section_lock20")))
#define CHANNEL21 __attribute__((section("section_channel21")))
#define LOCK21 __attribute__((section("section_lock21")))
#define CHANNEL22 __attribute__((section("section_channel22")))
#define LOCK22 __attribute__((section("section_lock22")))
#define CHANNEL23 __attribute__((section("section_channel23")))
#define LOCK23 __attribute__((section("section_lock23")))
#define CHANNEL24 __attribute__((section("section_channel24")))
#define LOCK24 __attribute__((section("section_lock24")))
#define CHANNEL25 __attribute__((section("section_channel25")))
#define LOCK25 __attribute__((section("section_lock25")))
#define CHANNEL26 __attribute__((section("section_channel26")))
#define LOCK26 __attribute__((section("section_lock26")))
#define CHANNEL27 __attribute__((section("section_channel27")))
#define LOCK27 __attribute__((section("section_lock27")))
#define CHANNEL28 __attribute__((section("section_channel28")))
#define LOCK28 __attribute__((section("section_lock28")))
#define CHANNEL29 __attribute__((section("section_channel29")))
#define LOCK29 __attribute__((section("section_lock29")))
#define CHANNEL30 __attribute__((section("section_channel30")))
#define LOCK30 __attribute__((section("section_lock30")))
#define CHANNEL31 __attribute__((section("section_channel31")))
#define LOCK31 __attribute__((section("section_lock31")))
#define CHANNEL32 __attribute__((section("section_channel32")))
#define LOCK32 __attribute__((section("section_lock32")))
#define CHANNEL33 __attribute__((section("section_channel33")))
#define LOCK33 __attribute__((section("section_lock33")))
#define CHANNEL34 __attribute__((section("section_channel34")))
#define LOCK34 __attribute__((section("section_lock34")))
#define CHANNEL35 __attribute__((section("section_channel35")))
#define LOCK35 __attribute__((section("section_lock35")))
#define CHANNEL36 __attribute__((section("section_channel36")))
#define LOCK36 __attribute__((section("section_lock36")))
#define CHANNEL37 __attribute__((section("section_channel37")))
#define LOCK37 __attribute__((section("section_lock37")))
#define CHANNEL38 __attribute__((section("section_channel38")))
#define LOCK38 __attribute__((section("section_lock38")))
#define CHANNEL39 __attribute__((section("section_channel39")))
#define LOCK39 __attribute__((section("section_lock39")))
#define CHANNEL40 __attribute__((section("section_channel40")))
#define LOCK40 __attribute__((section("section_lock40")))
#define CHANNEL41 __attribute__((section("section_channel41")))
#define LOCK41 __attribute__((section("section_lock41")))
#define CHANNEL42 __attribute__((section("section_channel42")))
#define LOCK42 __attribute__((section("section_lock42")))
#define CHANNEL43 __attribute__((section("section_channel43")))
#define LOCK43 __attribute__((section("section_lock43")))
#define CHANNEL44 __attribute__((section("section_channel44")))
#define LOCK44 __attribute__((section("section_lock44")))
#define CHANNEL45 __attribute__((section("section_channel45")))
#define LOCK45 __attribute__((section("section_lock45")))
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

/* Synchronous channels */
/* Asynchronous channels */
asyncchannel __InputEngine_packet__Classification_from_IE;
uint32_t const InputEngine_packet__Classification_from_IE_lock LOCK0;
struct mwmr_status_s InputEngine_packet__Classification_from_IE_status CHANNEL0;
uint8_t InputEngine_packet__Classification_from_IE_data[32] CHANNEL0;
struct mwmr_s InputEngine_packet__Classification_from_IE CHANNEL0;

asyncchannel __Bootstrap_address__InputEngine_bootstrap;
uint32_t const Bootstrap_address__InputEngine_bootstrap_lock LOCK1;
struct mwmr_status_s Bootstrap_address__InputEngine_bootstrap_status CHANNEL1;
uint8_t Bootstrap_address__InputEngine_bootstrap_data[32] CHANNEL1;
struct mwmr_s Bootstrap_address__InputEngine_bootstrap CHANNEL1;

asyncchannel __OutputEngine_address__InputEngine_address;
uint32_t const OutputEngine_address__InputEngine_address_lock LOCK2;
struct mwmr_status_s OutputEngine_address__InputEngine_address_status CHANNEL2;
uint8_t OutputEngine_address__InputEngine_address_data[32] CHANNEL2;
struct mwmr_s OutputEngine_address__InputEngine_address CHANNEL2;

asyncchannel __Scheduling_packet__OutputEngine_packet;
uint32_t const Scheduling_packet__OutputEngine_packet_lock LOCK3;
struct mwmr_status_s Scheduling_packet__OutputEngine_packet_status CHANNEL3;
uint8_t Scheduling_packet__OutputEngine_packet_data[32] CHANNEL3;
struct mwmr_s Scheduling_packet__OutputEngine_packet CHANNEL3;

asyncchannel __Classif2_from_classif__Classification_to_c2;
uint32_t const Classif2_from_classif__Classification_to_c2_lock LOCK4;
struct mwmr_status_s Classif2_from_classif__Classification_to_c2_status CHANNEL4;
uint8_t Classif2_from_classif__Classification_to_c2_data[32] CHANNEL4;
struct mwmr_s Classif2_from_classif__Classification_to_c2 CHANNEL4;

asyncchannel __Classif2_to_queue_low__Classification_c2_to_queue_low;
uint32_t const Classif2_to_queue_low__Classification_c2_to_queue_low_lock LOCK5;
struct mwmr_status_s Classif2_to_queue_low__Classification_c2_to_queue_low_status CHANNEL5;
uint8_t Classif2_to_queue_low__Classification_c2_to_queue_low_data[32] CHANNEL5;
struct mwmr_s Classif2_to_queue_low__Classification_c2_to_queue_low CHANNEL5;

asyncchannel __Classif2_to_queue_medium__Classification_c2_to_queue_medium;
uint32_t const Classif2_to_queue_medium__Classification_c2_to_queue_medium_lock LOCK6;
struct mwmr_status_s Classif2_to_queue_medium__Classification_c2_to_queue_medium_status CHANNEL6;
uint8_t Classif2_to_queue_medium__Classification_c2_to_queue_medium_data[32] CHANNEL6;
struct mwmr_s Classif2_to_queue_medium__Classification_c2_to_queue_medium CHANNEL6;

asyncchannel __Classif2_to_queue_high__Classification_c2_to_queue_high;
uint32_t const Classif2_to_queue_high__Classification_c2_to_queue_high_lock LOCK7;
struct mwmr_status_s Classif2_to_queue_high__Classification_c2_to_queue_high_status CHANNEL7;
uint8_t Classif2_to_queue_high__Classification_c2_to_queue_high_data[32] CHANNEL7;
struct mwmr_s Classif2_to_queue_high__Classification_c2_to_queue_high CHANNEL7;

asyncchannel __Classif0_from_classif__Classification_to_c0;
uint32_t const Classif0_from_classif__Classification_to_c0_lock LOCK8;
struct mwmr_status_s Classif0_from_classif__Classification_to_c0_status CHANNEL8;
uint8_t Classif0_from_classif__Classification_to_c0_data[32] CHANNEL8;
struct mwmr_s Classif0_from_classif__Classification_to_c0 CHANNEL8;

asyncchannel __Classif0_to_queue_low__Classification_c0_to_queue_low;
uint32_t const Classif0_to_queue_low__Classification_c0_to_queue_low_lock LOCK9;
struct mwmr_status_s Classif0_to_queue_low__Classification_c0_to_queue_low_status CHANNEL9;
uint8_t Classif0_to_queue_low__Classification_c0_to_queue_low_data[32] CHANNEL9;
struct mwmr_s Classif0_to_queue_low__Classification_c0_to_queue_low CHANNEL9;

asyncchannel __Classif0_to_queue_medium__Classification_c0_to_queue_medium;
uint32_t const Classif0_to_queue_medium__Classification_c0_to_queue_medium_lock LOCK10;
struct mwmr_status_s Classif0_to_queue_medium__Classification_c0_to_queue_medium_status CHANNEL10;
uint8_t Classif0_to_queue_medium__Classification_c0_to_queue_medium_data[32] CHANNEL10;
struct mwmr_s Classif0_to_queue_medium__Classification_c0_to_queue_medium CHANNEL10;

asyncchannel __Classif0_to_queue_high__Classification_c0_to_queue_high;
uint32_t const Classif0_to_queue_high__Classification_c0_to_queue_high_lock LOCK11;
struct mwmr_status_s Classif0_to_queue_high__Classification_c0_to_queue_high_status CHANNEL11;
uint8_t Classif0_to_queue_high__Classification_c0_to_queue_high_data[32] CHANNEL11;
struct mwmr_s Classif0_to_queue_high__Classification_c0_to_queue_high CHANNEL11;

asyncchannel __Classif1_from_classif__Classification_to_c1;
uint32_t const Classif1_from_classif__Classification_to_c1_lock LOCK12;
struct mwmr_status_s Classif1_from_classif__Classification_to_c1_status CHANNEL12;
uint8_t Classif1_from_classif__Classification_to_c1_data[32] CHANNEL12;
struct mwmr_s Classif1_from_classif__Classification_to_c1 CHANNEL12;

asyncchannel __Classif1_to_queue_low__Classification_c1_to_queue_low;
uint32_t const Classif1_to_queue_low__Classification_c1_to_queue_low_lock LOCK13;
struct mwmr_status_s Classif1_to_queue_low__Classification_c1_to_queue_low_status CHANNEL13;
uint8_t Classif1_to_queue_low__Classification_c1_to_queue_low_data[32] CHANNEL13;
struct mwmr_s Classif1_to_queue_low__Classification_c1_to_queue_low CHANNEL13;

asyncchannel __Classif1_to_queue_medium__Classification_c1_to_queue_medium;
uint32_t const Classif1_to_queue_medium__Classification_c1_to_queue_medium_lock LOCK14;
struct mwmr_status_s Classif1_to_queue_medium__Classification_c1_to_queue_medium_status CHANNEL14;
uint8_t Classif1_to_queue_medium__Classification_c1_to_queue_medium_data[32] CHANNEL14;
struct mwmr_s Classif1_to_queue_medium__Classification_c1_to_queue_medium CHANNEL14;

asyncchannel __Classif1_to_queue_high__Classification_c1_to_queue_high;
uint32_t const Classif1_to_queue_high__Classification_c1_to_queue_high_lock LOCK15;
struct mwmr_status_s Classif1_to_queue_high__Classification_c1_to_queue_high_status CHANNEL15;
uint8_t Classif1_to_queue_high__Classification_c1_to_queue_high_data[32] CHANNEL15;
struct mwmr_s Classif1_to_queue_high__Classification_c1_to_queue_high CHANNEL15;

asyncchannel __Classification_queue_low__Scheduling_from_queue_low;
uint32_t const Classification_queue_low__Scheduling_from_queue_low_lock LOCK16;
struct mwmr_status_s Classification_queue_low__Scheduling_from_queue_low_status CHANNEL16;
uint8_t Classification_queue_low__Scheduling_from_queue_low_data[32] CHANNEL16;
struct mwmr_s Classification_queue_low__Scheduling_from_queue_low CHANNEL16;

asyncchannel __Classification_queue_medium__Scheduling_from_queue_medium;
uint32_t const Classification_queue_medium__Scheduling_from_queue_medium_lock LOCK17;
struct mwmr_status_s Classification_queue_medium__Scheduling_from_queue_medium_status CHANNEL17;
uint8_t Classification_queue_medium__Scheduling_from_queue_medium_data[32] CHANNEL17;
struct mwmr_s Classification_queue_medium__Scheduling_from_queue_medium CHANNEL17;

asyncchannel __Classification_queue_high__Scheduling_from_queue_high;
uint32_t const Classification_queue_high__Scheduling_from_queue_high_lock LOCK18;
struct mwmr_status_s Classification_queue_high__Scheduling_from_queue_high_status CHANNEL18;
uint8_t Classification_queue_high__Scheduling_from_queue_high_data[32] CHANNEL18;
struct mwmr_s Classification_queue_high__Scheduling_from_queue_high CHANNEL18;

asyncchannel __Sched0_toScheduler0__Scheduling_to_scheduler0;
uint32_t const Sched0_toScheduler0__Scheduling_to_scheduler0_lock LOCK19;
struct mwmr_status_s Sched0_toScheduler0__Scheduling_to_scheduler0_status CHANNEL19;
uint8_t Sched0_toScheduler0__Scheduling_to_scheduler0_data[32] CHANNEL19;
struct mwmr_s Sched0_toScheduler0__Scheduling_to_scheduler0 CHANNEL19;

asyncchannel __Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
uint32_t const Sched0_scheduledPacket0__Scheduling_scheduledPacket0_lock LOCK20;
struct mwmr_status_s Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status CHANNEL20;
uint8_t Sched0_scheduledPacket0__Scheduling_scheduledPacket0_data[32] CHANNEL20;
struct mwmr_s Sched0_scheduledPacket0__Scheduling_scheduledPacket0 CHANNEL20;

asyncchannel __Sched1_toScheduler1__Scheduling_to_scheduler1;
uint32_t const Sched1_toScheduler1__Scheduling_to_scheduler1_lock LOCK21;
struct mwmr_status_s Sched1_toScheduler1__Scheduling_to_scheduler1_status CHANNEL21;
uint8_t Sched1_toScheduler1__Scheduling_to_scheduler1_data[32] CHANNEL21;
struct mwmr_s Sched1_toScheduler1__Scheduling_to_scheduler1 CHANNEL21;

asyncchannel __Sched1_scheduledPacket1__Scheduling_scheduledPacket1;
uint32_t const Sched1_scheduledPacket1__Scheduling_scheduledPacket1_lock LOCK22;
struct mwmr_status_s Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status CHANNEL22;
uint8_t Sched1_scheduledPacket1__Scheduling_scheduledPacket1_data[32] CHANNEL22;
struct mwmr_s Sched1_scheduledPacket1__Scheduling_scheduledPacket1 CHANNEL22;


int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  int sizeParams;
  
  /* Synchronous channels */
  /* Asynchronous channels */
  InputEngine_packet__Classification_from_IE_status.rptr = 0;
  InputEngine_packet__Classification_from_IE_status.wptr = 0;
  InputEngine_packet__Classification_from_IE_status.usage = 0;
  InputEngine_packet__Classification_from_IE_status.lock = 0;
  
  InputEngine_packet__Classification_from_IE.width = 8;
  InputEngine_packet__Classification_from_IE.depth = 1024;
  InputEngine_packet__Classification_from_IE.gdepth = InputEngine_packet__Classification_from_IE.depth;
  InputEngine_packet__Classification_from_IE.buffer = InputEngine_packet__Classification_from_IE_data;
  InputEngine_packet__Classification_from_IE.status = &InputEngine_packet__Classification_from_IE_status;
  __InputEngine_packet__Classification_from_IE.inname ="from_IE";
  __InputEngine_packet__Classification_from_IE.outname ="packet";
  __InputEngine_packet__Classification_from_IE.isBlocking = 0;
  __InputEngine_packet__Classification_from_IE.maxNbOfMessages = 1024;
  __InputEngine_packet__Classification_from_IE.mwmr_fifo = &InputEngine_packet__Classification_from_IE;
  InputEngine_packet__Classification_from_IE.status =&InputEngine_packet__Classification_from_IE_status;
  InputEngine_packet__Classification_from_IE.status->lock=0;
  InputEngine_packet__Classification_from_IE.status->rptr=0;
  InputEngine_packet__Classification_from_IE.status->usage=0;
  InputEngine_packet__Classification_from_IE.status->wptr=0;
  Bootstrap_address__InputEngine_bootstrap_status.rptr = 0;
  Bootstrap_address__InputEngine_bootstrap_status.wptr = 0;
  Bootstrap_address__InputEngine_bootstrap_status.usage = 0;
  Bootstrap_address__InputEngine_bootstrap_status.lock = 0;
  
  Bootstrap_address__InputEngine_bootstrap.width = 4;
  Bootstrap_address__InputEngine_bootstrap.depth = 1024;
  Bootstrap_address__InputEngine_bootstrap.gdepth = Bootstrap_address__InputEngine_bootstrap.depth;
  Bootstrap_address__InputEngine_bootstrap.buffer = Bootstrap_address__InputEngine_bootstrap_data;
  Bootstrap_address__InputEngine_bootstrap.status = &Bootstrap_address__InputEngine_bootstrap_status;
  __Bootstrap_address__InputEngine_bootstrap.inname ="bootstrap";
  __Bootstrap_address__InputEngine_bootstrap.outname ="address";
  __Bootstrap_address__InputEngine_bootstrap.isBlocking = 0;
  __Bootstrap_address__InputEngine_bootstrap.maxNbOfMessages = 1024;
  __Bootstrap_address__InputEngine_bootstrap.mwmr_fifo = &Bootstrap_address__InputEngine_bootstrap;
  Bootstrap_address__InputEngine_bootstrap.status =&Bootstrap_address__InputEngine_bootstrap_status;
  Bootstrap_address__InputEngine_bootstrap.status->lock=0;
  Bootstrap_address__InputEngine_bootstrap.status->rptr=0;
  Bootstrap_address__InputEngine_bootstrap.status->usage=0;
  Bootstrap_address__InputEngine_bootstrap.status->wptr=0;
  OutputEngine_address__InputEngine_address_status.rptr = 0;
  OutputEngine_address__InputEngine_address_status.wptr = 0;
  OutputEngine_address__InputEngine_address_status.usage = 0;
  OutputEngine_address__InputEngine_address_status.lock = 0;
  
  OutputEngine_address__InputEngine_address.width = 4;
  OutputEngine_address__InputEngine_address.depth = 1024;
  OutputEngine_address__InputEngine_address.gdepth = OutputEngine_address__InputEngine_address.depth;
  OutputEngine_address__InputEngine_address.buffer = OutputEngine_address__InputEngine_address_data;
  OutputEngine_address__InputEngine_address.status = &OutputEngine_address__InputEngine_address_status;
  __OutputEngine_address__InputEngine_address.inname ="address";
  __OutputEngine_address__InputEngine_address.outname ="address";
  __OutputEngine_address__InputEngine_address.isBlocking = 0;
  __OutputEngine_address__InputEngine_address.maxNbOfMessages = 1024;
  __OutputEngine_address__InputEngine_address.mwmr_fifo = &OutputEngine_address__InputEngine_address;
  OutputEngine_address__InputEngine_address.status =&OutputEngine_address__InputEngine_address_status;
  OutputEngine_address__InputEngine_address.status->lock=0;
  OutputEngine_address__InputEngine_address.status->rptr=0;
  OutputEngine_address__InputEngine_address.status->usage=0;
  OutputEngine_address__InputEngine_address.status->wptr=0;
  Scheduling_packet__OutputEngine_packet_status.rptr = 0;
  Scheduling_packet__OutputEngine_packet_status.wptr = 0;
  Scheduling_packet__OutputEngine_packet_status.usage = 0;
  Scheduling_packet__OutputEngine_packet_status.lock = 0;
  
  Scheduling_packet__OutputEngine_packet.width = 8;
  Scheduling_packet__OutputEngine_packet.depth = 1024;
  Scheduling_packet__OutputEngine_packet.gdepth = Scheduling_packet__OutputEngine_packet.depth;
  Scheduling_packet__OutputEngine_packet.buffer = Scheduling_packet__OutputEngine_packet_data;
  Scheduling_packet__OutputEngine_packet.status = &Scheduling_packet__OutputEngine_packet_status;
  __Scheduling_packet__OutputEngine_packet.inname ="packet";
  __Scheduling_packet__OutputEngine_packet.outname ="packet";
  __Scheduling_packet__OutputEngine_packet.isBlocking = 0;
  __Scheduling_packet__OutputEngine_packet.maxNbOfMessages = 1024;
  __Scheduling_packet__OutputEngine_packet.mwmr_fifo = &Scheduling_packet__OutputEngine_packet;
  Scheduling_packet__OutputEngine_packet.status =&Scheduling_packet__OutputEngine_packet_status;
  Scheduling_packet__OutputEngine_packet.status->lock=0;
  Scheduling_packet__OutputEngine_packet.status->rptr=0;
  Scheduling_packet__OutputEngine_packet.status->usage=0;
  Scheduling_packet__OutputEngine_packet.status->wptr=0;
  Classif2_from_classif__Classification_to_c2_status.rptr = 0;
  Classif2_from_classif__Classification_to_c2_status.wptr = 0;
  Classif2_from_classif__Classification_to_c2_status.usage = 0;
  Classif2_from_classif__Classification_to_c2_status.lock = 0;
  
  Classif2_from_classif__Classification_to_c2.width = 8;
  Classif2_from_classif__Classification_to_c2.depth = 8;
  Classif2_from_classif__Classification_to_c2.gdepth = Classif2_from_classif__Classification_to_c2.depth;
  Classif2_from_classif__Classification_to_c2.buffer = Classif2_from_classif__Classification_to_c2_data;
  Classif2_from_classif__Classification_to_c2.status = &Classif2_from_classif__Classification_to_c2_status;
  __Classif2_from_classif__Classification_to_c2.inname ="from_classif";
  __Classif2_from_classif__Classification_to_c2.outname ="to_c2";
  __Classif2_from_classif__Classification_to_c2.isBlocking = 0;
  __Classif2_from_classif__Classification_to_c2.maxNbOfMessages = 8;
  __Classif2_from_classif__Classification_to_c2.mwmr_fifo = &Classif2_from_classif__Classification_to_c2;
  Classif2_from_classif__Classification_to_c2.status =&Classif2_from_classif__Classification_to_c2_status;
  Classif2_from_classif__Classification_to_c2.status->lock=0;
  Classif2_from_classif__Classification_to_c2.status->rptr=0;
  Classif2_from_classif__Classification_to_c2.status->usage=0;
  Classif2_from_classif__Classification_to_c2.status->wptr=0;
  Classif2_to_queue_low__Classification_c2_to_queue_low_status.rptr = 0;
  Classif2_to_queue_low__Classification_c2_to_queue_low_status.wptr = 0;
  Classif2_to_queue_low__Classification_c2_to_queue_low_status.usage = 0;
  Classif2_to_queue_low__Classification_c2_to_queue_low_status.lock = 0;
  
  Classif2_to_queue_low__Classification_c2_to_queue_low.width = 8;
  Classif2_to_queue_low__Classification_c2_to_queue_low.depth = 8;
  Classif2_to_queue_low__Classification_c2_to_queue_low.gdepth = Classif2_to_queue_low__Classification_c2_to_queue_low.depth;
  Classif2_to_queue_low__Classification_c2_to_queue_low.buffer = Classif2_to_queue_low__Classification_c2_to_queue_low_data;
  Classif2_to_queue_low__Classification_c2_to_queue_low.status = &Classif2_to_queue_low__Classification_c2_to_queue_low_status;
  __Classif2_to_queue_low__Classification_c2_to_queue_low.inname ="c2_to_queue_low";
  __Classif2_to_queue_low__Classification_c2_to_queue_low.outname ="to_queue_low";
  __Classif2_to_queue_low__Classification_c2_to_queue_low.isBlocking = 0;
  __Classif2_to_queue_low__Classification_c2_to_queue_low.maxNbOfMessages = 8;
  __Classif2_to_queue_low__Classification_c2_to_queue_low.mwmr_fifo = &Classif2_to_queue_low__Classification_c2_to_queue_low;
  Classif2_to_queue_low__Classification_c2_to_queue_low.status =&Classif2_to_queue_low__Classification_c2_to_queue_low_status;
  Classif2_to_queue_low__Classification_c2_to_queue_low.status->lock=0;
  Classif2_to_queue_low__Classification_c2_to_queue_low.status->rptr=0;
  Classif2_to_queue_low__Classification_c2_to_queue_low.status->usage=0;
  Classif2_to_queue_low__Classification_c2_to_queue_low.status->wptr=0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium_status.rptr = 0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium_status.wptr = 0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium_status.usage = 0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium_status.lock = 0;
  
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.width = 8;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.depth = 8;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.gdepth = Classif2_to_queue_medium__Classification_c2_to_queue_medium.depth;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.buffer = Classif2_to_queue_medium__Classification_c2_to_queue_medium_data;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.status = &Classif2_to_queue_medium__Classification_c2_to_queue_medium_status;
  __Classif2_to_queue_medium__Classification_c2_to_queue_medium.inname ="c2_to_queue_medium";
  __Classif2_to_queue_medium__Classification_c2_to_queue_medium.outname ="to_queue_medium";
  __Classif2_to_queue_medium__Classification_c2_to_queue_medium.isBlocking = 0;
  __Classif2_to_queue_medium__Classification_c2_to_queue_medium.maxNbOfMessages = 8;
  __Classif2_to_queue_medium__Classification_c2_to_queue_medium.mwmr_fifo = &Classif2_to_queue_medium__Classification_c2_to_queue_medium;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.status =&Classif2_to_queue_medium__Classification_c2_to_queue_medium_status;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.status->lock=0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.status->rptr=0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.status->usage=0;
  Classif2_to_queue_medium__Classification_c2_to_queue_medium.status->wptr=0;
  Classif2_to_queue_high__Classification_c2_to_queue_high_status.rptr = 0;
  Classif2_to_queue_high__Classification_c2_to_queue_high_status.wptr = 0;
  Classif2_to_queue_high__Classification_c2_to_queue_high_status.usage = 0;
  Classif2_to_queue_high__Classification_c2_to_queue_high_status.lock = 0;
  
  Classif2_to_queue_high__Classification_c2_to_queue_high.width = 8;
  Classif2_to_queue_high__Classification_c2_to_queue_high.depth = 8;
  Classif2_to_queue_high__Classification_c2_to_queue_high.gdepth = Classif2_to_queue_high__Classification_c2_to_queue_high.depth;
  Classif2_to_queue_high__Classification_c2_to_queue_high.buffer = Classif2_to_queue_high__Classification_c2_to_queue_high_data;
  Classif2_to_queue_high__Classification_c2_to_queue_high.status = &Classif2_to_queue_high__Classification_c2_to_queue_high_status;
  __Classif2_to_queue_high__Classification_c2_to_queue_high.inname ="c2_to_queue_high";
  __Classif2_to_queue_high__Classification_c2_to_queue_high.outname ="to_queue_high";
  __Classif2_to_queue_high__Classification_c2_to_queue_high.isBlocking = 0;
  __Classif2_to_queue_high__Classification_c2_to_queue_high.maxNbOfMessages = 8;
  __Classif2_to_queue_high__Classification_c2_to_queue_high.mwmr_fifo = &Classif2_to_queue_high__Classification_c2_to_queue_high;
  Classif2_to_queue_high__Classification_c2_to_queue_high.status =&Classif2_to_queue_high__Classification_c2_to_queue_high_status;
  Classif2_to_queue_high__Classification_c2_to_queue_high.status->lock=0;
  Classif2_to_queue_high__Classification_c2_to_queue_high.status->rptr=0;
  Classif2_to_queue_high__Classification_c2_to_queue_high.status->usage=0;
  Classif2_to_queue_high__Classification_c2_to_queue_high.status->wptr=0;
  Classif0_from_classif__Classification_to_c0_status.rptr = 0;
  Classif0_from_classif__Classification_to_c0_status.wptr = 0;
  Classif0_from_classif__Classification_to_c0_status.usage = 0;
  Classif0_from_classif__Classification_to_c0_status.lock = 0;
  
  Classif0_from_classif__Classification_to_c0.width = 8;
  Classif0_from_classif__Classification_to_c0.depth = 8;
  Classif0_from_classif__Classification_to_c0.gdepth = Classif0_from_classif__Classification_to_c0.depth;
  Classif0_from_classif__Classification_to_c0.buffer = Classif0_from_classif__Classification_to_c0_data;
  Classif0_from_classif__Classification_to_c0.status = &Classif0_from_classif__Classification_to_c0_status;
  __Classif0_from_classif__Classification_to_c0.inname ="from_classif";
  __Classif0_from_classif__Classification_to_c0.outname ="to_c0";
  __Classif0_from_classif__Classification_to_c0.isBlocking = 0;
  __Classif0_from_classif__Classification_to_c0.maxNbOfMessages = 8;
  __Classif0_from_classif__Classification_to_c0.mwmr_fifo = &Classif0_from_classif__Classification_to_c0;
  Classif0_from_classif__Classification_to_c0.status =&Classif0_from_classif__Classification_to_c0_status;
  Classif0_from_classif__Classification_to_c0.status->lock=0;
  Classif0_from_classif__Classification_to_c0.status->rptr=0;
  Classif0_from_classif__Classification_to_c0.status->usage=0;
  Classif0_from_classif__Classification_to_c0.status->wptr=0;
  Classif0_to_queue_low__Classification_c0_to_queue_low_status.rptr = 0;
  Classif0_to_queue_low__Classification_c0_to_queue_low_status.wptr = 0;
  Classif0_to_queue_low__Classification_c0_to_queue_low_status.usage = 0;
  Classif0_to_queue_low__Classification_c0_to_queue_low_status.lock = 0;
  
  Classif0_to_queue_low__Classification_c0_to_queue_low.width = 8;
  Classif0_to_queue_low__Classification_c0_to_queue_low.depth = 8;
  Classif0_to_queue_low__Classification_c0_to_queue_low.gdepth = Classif0_to_queue_low__Classification_c0_to_queue_low.depth;
  Classif0_to_queue_low__Classification_c0_to_queue_low.buffer = Classif0_to_queue_low__Classification_c0_to_queue_low_data;
  Classif0_to_queue_low__Classification_c0_to_queue_low.status = &Classif0_to_queue_low__Classification_c0_to_queue_low_status;
  __Classif0_to_queue_low__Classification_c0_to_queue_low.inname ="c0_to_queue_low";
  __Classif0_to_queue_low__Classification_c0_to_queue_low.outname ="to_queue_low";
  __Classif0_to_queue_low__Classification_c0_to_queue_low.isBlocking = 0;
  __Classif0_to_queue_low__Classification_c0_to_queue_low.maxNbOfMessages = 8;
  __Classif0_to_queue_low__Classification_c0_to_queue_low.mwmr_fifo = &Classif0_to_queue_low__Classification_c0_to_queue_low;
  Classif0_to_queue_low__Classification_c0_to_queue_low.status =&Classif0_to_queue_low__Classification_c0_to_queue_low_status;
  Classif0_to_queue_low__Classification_c0_to_queue_low.status->lock=0;
  Classif0_to_queue_low__Classification_c0_to_queue_low.status->rptr=0;
  Classif0_to_queue_low__Classification_c0_to_queue_low.status->usage=0;
  Classif0_to_queue_low__Classification_c0_to_queue_low.status->wptr=0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium_status.rptr = 0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium_status.wptr = 0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium_status.usage = 0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium_status.lock = 0;
  
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.width = 8;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.depth = 8;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.gdepth = Classif0_to_queue_medium__Classification_c0_to_queue_medium.depth;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.buffer = Classif0_to_queue_medium__Classification_c0_to_queue_medium_data;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.status = &Classif0_to_queue_medium__Classification_c0_to_queue_medium_status;
  __Classif0_to_queue_medium__Classification_c0_to_queue_medium.inname ="c0_to_queue_medium";
  __Classif0_to_queue_medium__Classification_c0_to_queue_medium.outname ="to_queue_medium";
  __Classif0_to_queue_medium__Classification_c0_to_queue_medium.isBlocking = 0;
  __Classif0_to_queue_medium__Classification_c0_to_queue_medium.maxNbOfMessages = 8;
  __Classif0_to_queue_medium__Classification_c0_to_queue_medium.mwmr_fifo = &Classif0_to_queue_medium__Classification_c0_to_queue_medium;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.status =&Classif0_to_queue_medium__Classification_c0_to_queue_medium_status;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.status->lock=0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.status->rptr=0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.status->usage=0;
  Classif0_to_queue_medium__Classification_c0_to_queue_medium.status->wptr=0;
  Classif0_to_queue_high__Classification_c0_to_queue_high_status.rptr = 0;
  Classif0_to_queue_high__Classification_c0_to_queue_high_status.wptr = 0;
  Classif0_to_queue_high__Classification_c0_to_queue_high_status.usage = 0;
  Classif0_to_queue_high__Classification_c0_to_queue_high_status.lock = 0;
  
  Classif0_to_queue_high__Classification_c0_to_queue_high.width = 8;
  Classif0_to_queue_high__Classification_c0_to_queue_high.depth = 8;
  Classif0_to_queue_high__Classification_c0_to_queue_high.gdepth = Classif0_to_queue_high__Classification_c0_to_queue_high.depth;
  Classif0_to_queue_high__Classification_c0_to_queue_high.buffer = Classif0_to_queue_high__Classification_c0_to_queue_high_data;
  Classif0_to_queue_high__Classification_c0_to_queue_high.status = &Classif0_to_queue_high__Classification_c0_to_queue_high_status;
  __Classif0_to_queue_high__Classification_c0_to_queue_high.inname ="c0_to_queue_high";
  __Classif0_to_queue_high__Classification_c0_to_queue_high.outname ="to_queue_high";
  __Classif0_to_queue_high__Classification_c0_to_queue_high.isBlocking = 0;
  __Classif0_to_queue_high__Classification_c0_to_queue_high.maxNbOfMessages = 8;
  __Classif0_to_queue_high__Classification_c0_to_queue_high.mwmr_fifo = &Classif0_to_queue_high__Classification_c0_to_queue_high;
  Classif0_to_queue_high__Classification_c0_to_queue_high.status =&Classif0_to_queue_high__Classification_c0_to_queue_high_status;
  Classif0_to_queue_high__Classification_c0_to_queue_high.status->lock=0;
  Classif0_to_queue_high__Classification_c0_to_queue_high.status->rptr=0;
  Classif0_to_queue_high__Classification_c0_to_queue_high.status->usage=0;
  Classif0_to_queue_high__Classification_c0_to_queue_high.status->wptr=0;
  Classif1_from_classif__Classification_to_c1_status.rptr = 0;
  Classif1_from_classif__Classification_to_c1_status.wptr = 0;
  Classif1_from_classif__Classification_to_c1_status.usage = 0;
  Classif1_from_classif__Classification_to_c1_status.lock = 0;
  
  Classif1_from_classif__Classification_to_c1.width = 8;
  Classif1_from_classif__Classification_to_c1.depth = 8;
  Classif1_from_classif__Classification_to_c1.gdepth = Classif1_from_classif__Classification_to_c1.depth;
  Classif1_from_classif__Classification_to_c1.buffer = Classif1_from_classif__Classification_to_c1_data;
  Classif1_from_classif__Classification_to_c1.status = &Classif1_from_classif__Classification_to_c1_status;
  __Classif1_from_classif__Classification_to_c1.inname ="from_classif";
  __Classif1_from_classif__Classification_to_c1.outname ="to_c1";
  __Classif1_from_classif__Classification_to_c1.isBlocking = 0;
  __Classif1_from_classif__Classification_to_c1.maxNbOfMessages = 8;
  __Classif1_from_classif__Classification_to_c1.mwmr_fifo = &Classif1_from_classif__Classification_to_c1;
  Classif1_from_classif__Classification_to_c1.status =&Classif1_from_classif__Classification_to_c1_status;
  Classif1_from_classif__Classification_to_c1.status->lock=0;
  Classif1_from_classif__Classification_to_c1.status->rptr=0;
  Classif1_from_classif__Classification_to_c1.status->usage=0;
  Classif1_from_classif__Classification_to_c1.status->wptr=0;
  Classif1_to_queue_low__Classification_c1_to_queue_low_status.rptr = 0;
  Classif1_to_queue_low__Classification_c1_to_queue_low_status.wptr = 0;
  Classif1_to_queue_low__Classification_c1_to_queue_low_status.usage = 0;
  Classif1_to_queue_low__Classification_c1_to_queue_low_status.lock = 0;
  
  Classif1_to_queue_low__Classification_c1_to_queue_low.width = 8;
  Classif1_to_queue_low__Classification_c1_to_queue_low.depth = 8;
  Classif1_to_queue_low__Classification_c1_to_queue_low.gdepth = Classif1_to_queue_low__Classification_c1_to_queue_low.depth;
  Classif1_to_queue_low__Classification_c1_to_queue_low.buffer = Classif1_to_queue_low__Classification_c1_to_queue_low_data;
  Classif1_to_queue_low__Classification_c1_to_queue_low.status = &Classif1_to_queue_low__Classification_c1_to_queue_low_status;
  __Classif1_to_queue_low__Classification_c1_to_queue_low.inname ="c1_to_queue_low";
  __Classif1_to_queue_low__Classification_c1_to_queue_low.outname ="to_queue_low";
  __Classif1_to_queue_low__Classification_c1_to_queue_low.isBlocking = 0;
  __Classif1_to_queue_low__Classification_c1_to_queue_low.maxNbOfMessages = 8;
  __Classif1_to_queue_low__Classification_c1_to_queue_low.mwmr_fifo = &Classif1_to_queue_low__Classification_c1_to_queue_low;
  Classif1_to_queue_low__Classification_c1_to_queue_low.status =&Classif1_to_queue_low__Classification_c1_to_queue_low_status;
  Classif1_to_queue_low__Classification_c1_to_queue_low.status->lock=0;
  Classif1_to_queue_low__Classification_c1_to_queue_low.status->rptr=0;
  Classif1_to_queue_low__Classification_c1_to_queue_low.status->usage=0;
  Classif1_to_queue_low__Classification_c1_to_queue_low.status->wptr=0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium_status.rptr = 0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium_status.wptr = 0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium_status.usage = 0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium_status.lock = 0;
  
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.width = 8;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.depth = 8;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.gdepth = Classif1_to_queue_medium__Classification_c1_to_queue_medium.depth;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.buffer = Classif1_to_queue_medium__Classification_c1_to_queue_medium_data;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.status = &Classif1_to_queue_medium__Classification_c1_to_queue_medium_status;
  __Classif1_to_queue_medium__Classification_c1_to_queue_medium.inname ="c1_to_queue_medium";
  __Classif1_to_queue_medium__Classification_c1_to_queue_medium.outname ="to_queue_medium";
  __Classif1_to_queue_medium__Classification_c1_to_queue_medium.isBlocking = 0;
  __Classif1_to_queue_medium__Classification_c1_to_queue_medium.maxNbOfMessages = 8;
  __Classif1_to_queue_medium__Classification_c1_to_queue_medium.mwmr_fifo = &Classif1_to_queue_medium__Classification_c1_to_queue_medium;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.status =&Classif1_to_queue_medium__Classification_c1_to_queue_medium_status;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.status->lock=0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.status->rptr=0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.status->usage=0;
  Classif1_to_queue_medium__Classification_c1_to_queue_medium.status->wptr=0;
  Classif1_to_queue_high__Classification_c1_to_queue_high_status.rptr = 0;
  Classif1_to_queue_high__Classification_c1_to_queue_high_status.wptr = 0;
  Classif1_to_queue_high__Classification_c1_to_queue_high_status.usage = 0;
  Classif1_to_queue_high__Classification_c1_to_queue_high_status.lock = 0;
  
  Classif1_to_queue_high__Classification_c1_to_queue_high.width = 8;
  Classif1_to_queue_high__Classification_c1_to_queue_high.depth = 8;
  Classif1_to_queue_high__Classification_c1_to_queue_high.gdepth = Classif1_to_queue_high__Classification_c1_to_queue_high.depth;
  Classif1_to_queue_high__Classification_c1_to_queue_high.buffer = Classif1_to_queue_high__Classification_c1_to_queue_high_data;
  Classif1_to_queue_high__Classification_c1_to_queue_high.status = &Classif1_to_queue_high__Classification_c1_to_queue_high_status;
  __Classif1_to_queue_high__Classification_c1_to_queue_high.inname ="c1_to_queue_high";
  __Classif1_to_queue_high__Classification_c1_to_queue_high.outname ="to_queue_high";
  __Classif1_to_queue_high__Classification_c1_to_queue_high.isBlocking = 0;
  __Classif1_to_queue_high__Classification_c1_to_queue_high.maxNbOfMessages = 8;
  __Classif1_to_queue_high__Classification_c1_to_queue_high.mwmr_fifo = &Classif1_to_queue_high__Classification_c1_to_queue_high;
  Classif1_to_queue_high__Classification_c1_to_queue_high.status =&Classif1_to_queue_high__Classification_c1_to_queue_high_status;
  Classif1_to_queue_high__Classification_c1_to_queue_high.status->lock=0;
  Classif1_to_queue_high__Classification_c1_to_queue_high.status->rptr=0;
  Classif1_to_queue_high__Classification_c1_to_queue_high.status->usage=0;
  Classif1_to_queue_high__Classification_c1_to_queue_high.status->wptr=0;
  Classification_queue_low__Scheduling_from_queue_low_status.rptr = 0;
  Classification_queue_low__Scheduling_from_queue_low_status.wptr = 0;
  Classification_queue_low__Scheduling_from_queue_low_status.usage = 0;
  Classification_queue_low__Scheduling_from_queue_low_status.lock = 0;
  
  Classification_queue_low__Scheduling_from_queue_low.width = 8;
  Classification_queue_low__Scheduling_from_queue_low.depth = 1024;
  Classification_queue_low__Scheduling_from_queue_low.gdepth = Classification_queue_low__Scheduling_from_queue_low.depth;
  Classification_queue_low__Scheduling_from_queue_low.buffer = Classification_queue_low__Scheduling_from_queue_low_data;
  Classification_queue_low__Scheduling_from_queue_low.status = &Classification_queue_low__Scheduling_from_queue_low_status;
  __Classification_queue_low__Scheduling_from_queue_low.inname ="from_queue_low";
  __Classification_queue_low__Scheduling_from_queue_low.outname ="queue_low";
  __Classification_queue_low__Scheduling_from_queue_low.isBlocking = 0;
  __Classification_queue_low__Scheduling_from_queue_low.maxNbOfMessages = 1024;
  __Classification_queue_low__Scheduling_from_queue_low.mwmr_fifo = &Classification_queue_low__Scheduling_from_queue_low;
  Classification_queue_low__Scheduling_from_queue_low.status =&Classification_queue_low__Scheduling_from_queue_low_status;
  Classification_queue_low__Scheduling_from_queue_low.status->lock=0;
  Classification_queue_low__Scheduling_from_queue_low.status->rptr=0;
  Classification_queue_low__Scheduling_from_queue_low.status->usage=0;
  Classification_queue_low__Scheduling_from_queue_low.status->wptr=0;
  Classification_queue_medium__Scheduling_from_queue_medium_status.rptr = 0;
  Classification_queue_medium__Scheduling_from_queue_medium_status.wptr = 0;
  Classification_queue_medium__Scheduling_from_queue_medium_status.usage = 0;
  Classification_queue_medium__Scheduling_from_queue_medium_status.lock = 0;
  
  Classification_queue_medium__Scheduling_from_queue_medium.width = 8;
  Classification_queue_medium__Scheduling_from_queue_medium.depth = 1024;
  Classification_queue_medium__Scheduling_from_queue_medium.gdepth = Classification_queue_medium__Scheduling_from_queue_medium.depth;
  Classification_queue_medium__Scheduling_from_queue_medium.buffer = Classification_queue_medium__Scheduling_from_queue_medium_data;
  Classification_queue_medium__Scheduling_from_queue_medium.status = &Classification_queue_medium__Scheduling_from_queue_medium_status;
  __Classification_queue_medium__Scheduling_from_queue_medium.inname ="from_queue_medium";
  __Classification_queue_medium__Scheduling_from_queue_medium.outname ="queue_medium";
  __Classification_queue_medium__Scheduling_from_queue_medium.isBlocking = 0;
  __Classification_queue_medium__Scheduling_from_queue_medium.maxNbOfMessages = 1024;
  __Classification_queue_medium__Scheduling_from_queue_medium.mwmr_fifo = &Classification_queue_medium__Scheduling_from_queue_medium;
  Classification_queue_medium__Scheduling_from_queue_medium.status =&Classification_queue_medium__Scheduling_from_queue_medium_status;
  Classification_queue_medium__Scheduling_from_queue_medium.status->lock=0;
  Classification_queue_medium__Scheduling_from_queue_medium.status->rptr=0;
  Classification_queue_medium__Scheduling_from_queue_medium.status->usage=0;
  Classification_queue_medium__Scheduling_from_queue_medium.status->wptr=0;
  Classification_queue_high__Scheduling_from_queue_high_status.rptr = 0;
  Classification_queue_high__Scheduling_from_queue_high_status.wptr = 0;
  Classification_queue_high__Scheduling_from_queue_high_status.usage = 0;
  Classification_queue_high__Scheduling_from_queue_high_status.lock = 0;
  
  Classification_queue_high__Scheduling_from_queue_high.width = 8;
  Classification_queue_high__Scheduling_from_queue_high.depth = 1024;
  Classification_queue_high__Scheduling_from_queue_high.gdepth = Classification_queue_high__Scheduling_from_queue_high.depth;
  Classification_queue_high__Scheduling_from_queue_high.buffer = Classification_queue_high__Scheduling_from_queue_high_data;
  Classification_queue_high__Scheduling_from_queue_high.status = &Classification_queue_high__Scheduling_from_queue_high_status;
  __Classification_queue_high__Scheduling_from_queue_high.inname ="from_queue_high";
  __Classification_queue_high__Scheduling_from_queue_high.outname ="queue_high";
  __Classification_queue_high__Scheduling_from_queue_high.isBlocking = 0;
  __Classification_queue_high__Scheduling_from_queue_high.maxNbOfMessages = 1024;
  __Classification_queue_high__Scheduling_from_queue_high.mwmr_fifo = &Classification_queue_high__Scheduling_from_queue_high;
  Classification_queue_high__Scheduling_from_queue_high.status =&Classification_queue_high__Scheduling_from_queue_high_status;
  Classification_queue_high__Scheduling_from_queue_high.status->lock=0;
  Classification_queue_high__Scheduling_from_queue_high.status->rptr=0;
  Classification_queue_high__Scheduling_from_queue_high.status->usage=0;
  Classification_queue_high__Scheduling_from_queue_high.status->wptr=0;
  Sched0_toScheduler0__Scheduling_to_scheduler0_status.rptr = 0;
  Sched0_toScheduler0__Scheduling_to_scheduler0_status.wptr = 0;
  Sched0_toScheduler0__Scheduling_to_scheduler0_status.usage = 0;
  Sched0_toScheduler0__Scheduling_to_scheduler0_status.lock = 0;
  
  Sched0_toScheduler0__Scheduling_to_scheduler0.width = 8;
  Sched0_toScheduler0__Scheduling_to_scheduler0.depth = 8;
  Sched0_toScheduler0__Scheduling_to_scheduler0.gdepth = Sched0_toScheduler0__Scheduling_to_scheduler0.depth;
  Sched0_toScheduler0__Scheduling_to_scheduler0.buffer = Sched0_toScheduler0__Scheduling_to_scheduler0_data;
  Sched0_toScheduler0__Scheduling_to_scheduler0.status = &Sched0_toScheduler0__Scheduling_to_scheduler0_status;
  __Sched0_toScheduler0__Scheduling_to_scheduler0.inname ="toScheduler0";
  __Sched0_toScheduler0__Scheduling_to_scheduler0.outname ="to_scheduler0";
  __Sched0_toScheduler0__Scheduling_to_scheduler0.isBlocking = 0;
  __Sched0_toScheduler0__Scheduling_to_scheduler0.maxNbOfMessages = 8;
  __Sched0_toScheduler0__Scheduling_to_scheduler0.mwmr_fifo = &Sched0_toScheduler0__Scheduling_to_scheduler0;
  Sched0_toScheduler0__Scheduling_to_scheduler0.status =&Sched0_toScheduler0__Scheduling_to_scheduler0_status;
  Sched0_toScheduler0__Scheduling_to_scheduler0.status->lock=0;
  Sched0_toScheduler0__Scheduling_to_scheduler0.status->rptr=0;
  Sched0_toScheduler0__Scheduling_to_scheduler0.status->usage=0;
  Sched0_toScheduler0__Scheduling_to_scheduler0.status->wptr=0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status.rptr = 0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status.wptr = 0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status.usage = 0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status.lock = 0;
  
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.width = 8;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.depth = 8;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.gdepth = Sched0_scheduledPacket0__Scheduling_scheduledPacket0.depth;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.buffer = Sched0_scheduledPacket0__Scheduling_scheduledPacket0_data;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.status = &Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status;
  __Sched0_scheduledPacket0__Scheduling_scheduledPacket0.inname ="scheduledPacket0";
  __Sched0_scheduledPacket0__Scheduling_scheduledPacket0.outname ="scheduledPacket0";
  __Sched0_scheduledPacket0__Scheduling_scheduledPacket0.isBlocking = 0;
  __Sched0_scheduledPacket0__Scheduling_scheduledPacket0.maxNbOfMessages = 8;
  __Sched0_scheduledPacket0__Scheduling_scheduledPacket0.mwmr_fifo = &Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.status =&Sched0_scheduledPacket0__Scheduling_scheduledPacket0_status;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.status->lock=0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.status->rptr=0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.status->usage=0;
  Sched0_scheduledPacket0__Scheduling_scheduledPacket0.status->wptr=0;
  Sched1_toScheduler1__Scheduling_to_scheduler1_status.rptr = 0;
  Sched1_toScheduler1__Scheduling_to_scheduler1_status.wptr = 0;
  Sched1_toScheduler1__Scheduling_to_scheduler1_status.usage = 0;
  Sched1_toScheduler1__Scheduling_to_scheduler1_status.lock = 0;
  
  Sched1_toScheduler1__Scheduling_to_scheduler1.width = 8;
  Sched1_toScheduler1__Scheduling_to_scheduler1.depth = 8;
  Sched1_toScheduler1__Scheduling_to_scheduler1.gdepth = Sched1_toScheduler1__Scheduling_to_scheduler1.depth;
  Sched1_toScheduler1__Scheduling_to_scheduler1.buffer = Sched1_toScheduler1__Scheduling_to_scheduler1_data;
  Sched1_toScheduler1__Scheduling_to_scheduler1.status = &Sched1_toScheduler1__Scheduling_to_scheduler1_status;
  __Sched1_toScheduler1__Scheduling_to_scheduler1.inname ="toScheduler1";
  __Sched1_toScheduler1__Scheduling_to_scheduler1.outname ="to_scheduler1";
  __Sched1_toScheduler1__Scheduling_to_scheduler1.isBlocking = 0;
  __Sched1_toScheduler1__Scheduling_to_scheduler1.maxNbOfMessages = 8;
  __Sched1_toScheduler1__Scheduling_to_scheduler1.mwmr_fifo = &Sched1_toScheduler1__Scheduling_to_scheduler1;
  Sched1_toScheduler1__Scheduling_to_scheduler1.status =&Sched1_toScheduler1__Scheduling_to_scheduler1_status;
  Sched1_toScheduler1__Scheduling_to_scheduler1.status->lock=0;
  Sched1_toScheduler1__Scheduling_to_scheduler1.status->rptr=0;
  Sched1_toScheduler1__Scheduling_to_scheduler1.status->usage=0;
  Sched1_toScheduler1__Scheduling_to_scheduler1.status->wptr=0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status.rptr = 0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status.wptr = 0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status.usage = 0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status.lock = 0;
  
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.width = 8;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.depth = 8;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.gdepth = Sched1_scheduledPacket1__Scheduling_scheduledPacket1.depth;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.buffer = Sched1_scheduledPacket1__Scheduling_scheduledPacket1_data;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.status = &Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status;
  __Sched1_scheduledPacket1__Scheduling_scheduledPacket1.inname ="scheduledPacket1";
  __Sched1_scheduledPacket1__Scheduling_scheduledPacket1.outname ="scheduledPacket1";
  __Sched1_scheduledPacket1__Scheduling_scheduledPacket1.isBlocking = 0;
  __Sched1_scheduledPacket1__Scheduling_scheduledPacket1.maxNbOfMessages = 8;
  __Sched1_scheduledPacket1__Scheduling_scheduledPacket1.mwmr_fifo = &Sched1_scheduledPacket1__Scheduling_scheduledPacket1;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.status =&Sched1_scheduledPacket1__Scheduling_scheduledPacket1_status;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.status->lock=0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.status->rptr=0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.status->usage=0;
  Sched1_scheduledPacket1__Scheduling_scheduledPacket1.status->wptr=0;
  
  /* Threads of tasks */
  pthread_t thread__Bootstrap;
  pthread_t thread__OutputEngine;
  pthread_t thread__InputEngine;
  pthread_t thread__Classification;
  pthread_t thread__Classif0;
  pthread_t thread__Classif1;
  pthread_t thread__Classif2;
  pthread_t thread__Scheduling;
  pthread_t thread__Sched1;
  pthread_t thread__Sched0;
  /* Activating tracing  */
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* User initialization */
  __user_init();
  
  
  struct mwmr_s *channels_array_Bootstrap[1];
  channels_array_Bootstrap[0]=&Bootstrap_address__InputEngine_bootstrap;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Bootstrap= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Bootstrap, attr_t, mainFunc__Bootstrap, (void *)channels_array_Bootstrap);
  
  struct mwmr_s *channels_array_OutputEngine[2];
  channels_array_OutputEngine[0]=&OutputEngine_address__InputEngine_address;
  channels_array_OutputEngine[1]=&Scheduling_packet__OutputEngine_packet;
  
  ptr =malloc(sizeof(pthread_t));
  thread__OutputEngine= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__OutputEngine, attr_t, mainFunc__OutputEngine, (void *)channels_array_OutputEngine);
  
  struct mwmr_s *channels_array_InputEngine[3];
  channels_array_InputEngine[0]=&InputEngine_packet__Classification_from_IE;
  channels_array_InputEngine[1]=&Bootstrap_address__InputEngine_bootstrap;
  channels_array_InputEngine[2]=&OutputEngine_address__InputEngine_address;
  
  ptr =malloc(sizeof(pthread_t));
  thread__InputEngine= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__InputEngine, attr_t, mainFunc__InputEngine, (void *)channels_array_InputEngine);
  
  struct mwmr_s *channels_array_Classification[16];
  channels_array_Classification[0]=&InputEngine_packet__Classification_from_IE;
  channels_array_Classification[1]=&Classif2_from_classif__Classification_to_c2;
  channels_array_Classification[2]=&Classif2_to_queue_low__Classification_c2_to_queue_low;
  channels_array_Classification[3]=&Classif2_to_queue_medium__Classification_c2_to_queue_medium;
  channels_array_Classification[4]=&Classif2_to_queue_high__Classification_c2_to_queue_high;
  channels_array_Classification[5]=&Classif0_from_classif__Classification_to_c0;
  channels_array_Classification[6]=&Classif0_to_queue_low__Classification_c0_to_queue_low;
  channels_array_Classification[7]=&Classif0_to_queue_medium__Classification_c0_to_queue_medium;
  channels_array_Classification[8]=&Classif0_to_queue_high__Classification_c0_to_queue_high;
  channels_array_Classification[9]=&Classif1_from_classif__Classification_to_c1;
  channels_array_Classification[10]=&Classif1_to_queue_low__Classification_c1_to_queue_low;
  channels_array_Classification[11]=&Classif1_to_queue_medium__Classification_c1_to_queue_medium;
  channels_array_Classification[12]=&Classif1_to_queue_high__Classification_c1_to_queue_high;
  channels_array_Classification[13]=&Classification_queue_low__Scheduling_from_queue_low;
  channels_array_Classification[14]=&Classification_queue_medium__Scheduling_from_queue_medium;
  channels_array_Classification[15]=&Classification_queue_high__Scheduling_from_queue_high;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Classification= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Classification, attr_t, mainFunc__Classification, (void *)channels_array_Classification);
  
  struct mwmr_s *channels_array_Classif0[4];
  channels_array_Classif0[0]=&Classif0_from_classif__Classification_to_c0;
  channels_array_Classif0[1]=&Classif0_to_queue_low__Classification_c0_to_queue_low;
  channels_array_Classif0[2]=&Classif0_to_queue_medium__Classification_c0_to_queue_medium;
  channels_array_Classif0[3]=&Classif0_to_queue_high__Classification_c0_to_queue_high;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Classif0= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Classif0, attr_t, mainFunc__Classif0, (void *)channels_array_Classif0);
  
  struct mwmr_s *channels_array_Classif1[4];
  channels_array_Classif1[0]=&Classif1_from_classif__Classification_to_c1;
  channels_array_Classif1[1]=&Classif1_to_queue_low__Classification_c1_to_queue_low;
  channels_array_Classif1[2]=&Classif1_to_queue_medium__Classification_c1_to_queue_medium;
  channels_array_Classif1[3]=&Classif1_to_queue_high__Classification_c1_to_queue_high;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Classif1= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Classif1, attr_t, mainFunc__Classif1, (void *)channels_array_Classif1);
  
  struct mwmr_s *channels_array_Classif2[4];
  channels_array_Classif2[0]=&Classif2_from_classif__Classification_to_c2;
  channels_array_Classif2[1]=&Classif2_to_queue_low__Classification_c2_to_queue_low;
  channels_array_Classif2[2]=&Classif2_to_queue_medium__Classification_c2_to_queue_medium;
  channels_array_Classif2[3]=&Classif2_to_queue_high__Classification_c2_to_queue_high;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Classif2= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Classif2, attr_t, mainFunc__Classif2, (void *)channels_array_Classif2);
  
  struct mwmr_s *channels_array_Scheduling[8];
  channels_array_Scheduling[0]=&Scheduling_packet__OutputEngine_packet;
  channels_array_Scheduling[1]=&Classification_queue_low__Scheduling_from_queue_low;
  channels_array_Scheduling[2]=&Classification_queue_medium__Scheduling_from_queue_medium;
  channels_array_Scheduling[3]=&Classification_queue_high__Scheduling_from_queue_high;
  channels_array_Scheduling[4]=&Sched0_toScheduler0__Scheduling_to_scheduler0;
  channels_array_Scheduling[5]=&Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
  channels_array_Scheduling[6]=&Sched1_toScheduler1__Scheduling_to_scheduler1;
  channels_array_Scheduling[7]=&Sched1_scheduledPacket1__Scheduling_scheduledPacket1;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Scheduling= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Scheduling, attr_t, mainFunc__Scheduling, (void *)channels_array_Scheduling);
  
  struct mwmr_s *channels_array_Sched1[2];
  channels_array_Sched1[0]=&Sched1_toScheduler1__Scheduling_to_scheduler1;
  channels_array_Sched1[1]=&Sched1_scheduledPacket1__Scheduling_scheduledPacket1;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Sched1= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Sched1, attr_t, mainFunc__Sched1, (void *)channels_array_Sched1);
  
  struct mwmr_s *channels_array_Sched0[2];
  channels_array_Sched0[0]=&Sched0_toScheduler0__Scheduling_to_scheduler0;
  channels_array_Sched0[1]=&Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Sched0= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Sched0, attr_t, mainFunc__Sched0, (void *)channels_array_Sched0);
  
  
  
  pthread_join(thread__Bootstrap, NULL);
  pthread_join(thread__OutputEngine, NULL);
  pthread_join(thread__InputEngine, NULL);
  pthread_join(thread__Classification, NULL);
  pthread_join(thread__Classif0, NULL);
  pthread_join(thread__Classif1, NULL);
  pthread_join(thread__Classif2, NULL);
  pthread_join(thread__Scheduling, NULL);
  pthread_join(thread__Sched1, NULL);
  pthread_join(thread__Sched0, NULL);
  
  
  return 0;
  
}
