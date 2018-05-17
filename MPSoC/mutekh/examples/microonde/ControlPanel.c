#include "ControlPanel.h"
//#include "asyncchannel.h"
#define STATE__START__STATE 0
#define STATE__Active 1  
#define STATE__STOP__STATE 2

static uint32_t startButton;

//canaux_panel[] : est un tableau de canaux, unique paramètre du pththread
void *mainFunc__ControlPanel(struct mwmr_s *canaux_panel[]){
	
   struct mwmr_s * ControlPanel_LEDOn__Controller_startCooking = canaux_panel[0];
   struct mwmr_s * ControlPanel_LEDoff__Controller_stopCooking = canaux_panel[1];
   struct mwmr_s * ControlPanel_startButton__Controller_start = canaux_panel[2];
 
 
   int __currentState = STATE__START__STATE;
   //char * __myname = (char *)arg;
   char * __myname ;
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
		

/***************************************STATE__START__STATE********************************/
      case STATE__START__STATE: 
			//traceStateEntering(__myname, "__StartState");
			//debug2Msg(__myname, "-> (=====) Entering state + Active");
			__currentState = STATE__Active;
      break;



/***************************************STATE__ACTIVE*************************************/      
      case STATE__Active: 
			//traceStateEntering(__myname, "Active");
			printf("Reading the output data");
			putchar('\n');
			// envoi du signal startButton
			async_write(ControlPanel_startButton__Controller_start, &startButton,1);
		
			printf("Controller_ringBell__Bell_ring =", startButton);
			printf("Data read OK");
			putchar('\n');

			//traceRequest(__myname, __returnRequest);
			//debug2Msg(__myname, "-> (=====) Entering state + Active");
			__currentState = STATE__Active;
     break;
      
    }
  }
  return NULL;
}



