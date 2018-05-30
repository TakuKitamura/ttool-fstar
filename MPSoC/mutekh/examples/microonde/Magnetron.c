#include "Magnetron.h"

#define STATE__START__STATE 0
#define STATE__Running 1
#define STATE__WaitForStart 2
#define STATE__STOP__STATE 3



static uint32_t stopM;
static uint32_t startM ;


void *mainFunc__Magnetron (struct mwmr_s *canaux_magnetron[]){

 int __currentState = STATE__START__STATE;
 char * __myname ;

 struct mwmr_s *  Controller_startMagnetron__Magnetron_startM =canaux_magnetron[0];
 struct mwmr_s *  Controller_stopMagnetron__Magnetron_stopM=canaux_magnetron[1];
 
   
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {

/***************************************STATE__START__STATE********************************/
      case STATE__START__STATE:
       			__currentState =STATE__WaitForStart;
	  break;


/***************************************STATE__Running**************************************/
	  case STATE__Running: 
			//reception du signal stopM
			async_read(Controller_stopMagnetron__Magnetron_stopM, &stopM,1);

			__currentState = STATE__WaitForStart;
		break;


/***************************************STATE__WaitForStart********************************/

      case STATE__WaitForStart: 
      
			
			//reception du signal startM
			async_read(Controller_startMagnetron__Magnetron_startM, &startM,1);
			
			//changement d'etat (STATE__Running)
			__currentState = STATE__Running;
	break;
    }
  }
  return NULL;
}



