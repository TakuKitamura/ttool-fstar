#include "test2.h"
//#include "asyncchannel.h"
#define STATE__START__STATE 0
#define STATE__Running 1
#define STATE__STOP__STATE 2

static uint32_t signal;

void *mainFunc__test2(struct mwmr_s *canaux[]){
  struct mwmr_s  *canal=canaux[0];
  
  int __currentState = STATE__START__STATE;
  //char * __myname = (char *)arg;
    printf ("************");
    async_read(canal,&signal, 1);
  /* Main loop on states */
    while(__currentState != STATE__STOP__STATE) {
      switch(__currentState) {
        case STATE__START__STATE: 
          printf ("############");
       __currentState = STATE__Running;
      break;
         
     case STATE__Running: 
    
      __currentState = STATE__STOP__STATE;
      break;
      
     }
   }

  return NULL;
}

