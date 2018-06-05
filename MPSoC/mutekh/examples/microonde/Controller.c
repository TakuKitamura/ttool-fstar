#include "Controller.h"
//#include "asyncchannel.h"

#define STATE__START__STATE 0
#define STATE__Starting 1
#define STATE__Heating 2
#define STATE__Idle 3
#define STATE__DoorOpened 4
#define STATE__DoorOpenedWhileHeating 5
#define STATE__STOP__STATE 6

static uint32_t startMagnetron;
static uint32_t start;
static uint32_t stopMagnetron;
static uint32_t okDoor;
static uint32_t Bell_ring;
static uint32_t closed ; 
static uint32_t ouvert ; 
static uint32_t  stopM ;
static uint32_t req0;
static uint32_t req1;
static uint32_t req2;

int duration = 5;
int remainingTime = 0;

int __currentState = STATE__START__STATE;
char * __myname ;
//int *__params0[0];
int *params1[];

int choix;;
    // declaration des canaux
  struct  mwmr_s * Controller_ringBell__Bell_ring ;
  struct  mwmr_s * Door_okDoor__Controller_okDoor  ;
  struct  mwmr_s * Door_open__Controller_open;
  struct  mwmr_s * Door_closed__Controller_closed ;
  struct  mwmr_s * Controller_startMagnetron__Magnetron_startM   ;
  struct  mwmr_s * Controller_stopMagnetron__Magnetron_stopM  ;
  

  struct  mwmr_s *  ControlPanel_LEDOn__Controller_startCooking  ;
  struct  mwmr_s *  ControlPanel_LEDoff__Controller_stopCooking ;
  struct  mwmr_s *  ControlPanel_startButton__Controller_start;
	
void req0_time() {
		remainingTime= remainingTime -1;
		//traceVariableModification("Controller", "remainingTime", remainingTime,0);
		__currentState = STATE__Heating;

		}

void req1_open() {
		//// envoi du signal stopM
		mwmr_write(Controller_stopMagnetron__Magnetron_stopM, &stopM, 1);
		//traceRequest(__myname, __returnRequest);
		// envoi du signal okDoor
		mwmr_write(Door_okDoor__Controller_okDoor, &okDoor, 1);
		//traceRequest(__myname, __returnRequest);
		__currentState = STATE__DoorOpenedWhileHeating;
	 }

void req2_stopM() {
		// envoi du signal Bell_ring
		mwmr_write(Controller_ringBell__Bell_ring, &Bell_ring, 1);
		//traceRequest(__myname, __returnRequest);
		__currentState = STATE__Idle;
	 } 
	 
	 

//canaux_controller[] : est un tableau de canaux, unique paramètre du pththread
  void *mainFunc__Controller(struct mwmr_s *canaux_controller[]){

    // definition des canaux
  Controller_ringBell__Bell_ring    = canaux_controller[0];
  Door_okDoor__Controller_okDoor    =canaux_controller[1] ;
  Door_open__Controller_open   = canaux_controller[2];
  Door_closed__Controller_closed   = canaux_controller[3] ;
  Controller_startMagnetron__Magnetron_startM   = canaux_controller[4] ;
  Controller_stopMagnetron__Magnetron_stopM   = canaux_controller[5] ;
  

   ControlPanel_LEDOn__Controller_startCooking = canaux_controller[6];
   ControlPanel_LEDoff__Controller_stopCooking =canaux_controller[7];
   ControlPanel_startButton__Controller_start =canaux_controller[8];
    
	

 /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {

/***************************************STATE__START__STATE********************************/
      case STATE__START__STATE: 
      //traceStateEntering(__myname, "__StartState");
      __currentState = STATE__Idle;
      break;
/***************************************STATE__Starting************************************/
      case STATE__Starting: 
      //traceStateEntering(__myname, "Starting");
      //envoi de signal (open)
     async_write(Controller_startMagnetron__Magnetron_startM, &startMagnetron, 1);
      //traceRequest(__myname, __returnRequest);
      remainingTime=duration;
      //traceVariableModification("Controller", "remainingTime", remainingTime,0);
      __currentState = STATE__Heating;
      break;

/***************************************STATE__heating************************************/

     case STATE__Heating: 
			  //traceStateEntering(__myname, "Heating");
			  if (( remainingTime>0 )) {
					req0 = 1;
			  }
			   // req 1 :reception du signal open
					req1 = async_read_nonblocking(Door_open__Controller_open, &ouvert, 1);
			  
			  if (( remainingTime==0 )) {
				  //req 2 : reception du signal stopM
					req2 = async_write_nonblocking(Controller_stopMagnetron__Magnetron_stopM, &stopM, 1);
			  }
			
	 ////////////// / Les 3 sont non réalisables 
			//  if ((req0 == 0) && (req1 == 0) && (req2 == 0) ) {
				  	//debug2Msg(__myname, "No possible request");

		    
	//////////////// Les 3 sont réalisables : choix non déterministe avec probabilité égale à 1/3
		    if ((req0 == 1) && (req1 == 1) && (req2 == 1) ) {
					
					choix = rand() ;   
					choix = choix * 3 ;
					if (choix < 1){
							req0_time(); } 
					
					else  if (choix <2) {
							req1_open(); }
				
					else {
							req2_stopM(); }
			}
			
	//////////////// Il y'a deux réalisables : choix non déterministe avec probabilité égale à 1/2
			if  ((req0 == 1) && (req1 == 1) && (req2 == 0) ){
				choix = rand();   
				//choix = round (choix);
				if (choix<0.5) {
						req0_time() ;
							} 
					  
					else {
						req1_open() ;
						}
				}
     
			
			if  ((req0 == 1) && (req1 == 0) && (req2 == 1) ){
				choix = rand();   
				//choix = round (choix);
					if (choix<0.5) {
						req0_time() ;
							} 
					  
					else {
						req2_stopM() ;
						}
				}
		
						
			if  ((req0 == 0) && (req1 == 1) && (req2 == 1) ){
				choix = rand();   
				//choix = round (choix);
					if (choix<0.5) {
						req1_open() ; 
						} 
					  
					else {
						req2_stopM() ;
						}
				}
    
    
    /////////////////Le cas ou une seule est réalisable 
		
			if ((req0 == 1) && (req1 == 0) && (req2 == 0) ) {
				 req0_time(); } 
				 
			if ((req0 == 0) && (req1 == 1) && (req2 == 0) ) {
				req1_open(); } 
	    
			if ((req0 == 0) && (req1 == 0) && (req2 == 1) ) {
				req2_stopM(); }
			
    

      break;


/***************************************STATE__Idle************************************/

	case STATE__Idle: 
			  //traceStateEntering(__myname, "Idle");
			  req0 =  async_read_nonblocking(Door_open__Controller_open, &ouvert, 1);
			  params1[0] = &duration;

			  req1 =  async_read_nonblocking(ControlPanel_startButton__Controller_start, &start, 1);
			  
			  
		///////////////// aucune n'est réalisable 
		
			  if ((req0 == 0) && (req1 == 0)){
				//debug2Msg(__myname, "No possible request");
				__currentState = STATE__STOP__STATE;
			   }
		//////////////// les deux sont réalisables :choix non déterministe avec probabilité égale à 1/2
		
			choix = rand();   
			//choix = round (choix);
			 if  ((req0 == 1) && (req1 == 1)){
				if (choix<0.5) {
						// envoi signal okDoor
						async_write(Door_okDoor__Controller_okDoor, &okDoor, 1);
						//changement d'etat
						__currentState = STATE__DoorOpened;
					}
				  
				else  if (choix>0.5){
						//changement d'etat
						__currentState = STATE__Starting;
					 }
			}
		/////////////////Le cas ou l'une des deux est non réalisable 
		
			else if (req1 == 0) {
				// envoi signal okDoor
				async_write(Door_okDoor__Controller_okDoor, &okDoor, 1);
				//changement d'etat
				__currentState = STATE__DoorOpened;
			} 
			
			else if (req0 == 0) {
				//changement d'etat
				__currentState = STATE__Starting;
			} 
			 
	break;
      
/************************************STATE__DoorOpened********************************/
	 case STATE__DoorOpened: 
	 
		  //traceStateEntering(__myname, "DoorOpened");
		  // reception du signal closed
		  async_read( Door_closed__Controller_closed,&closed,1);
		 // traceRequest(__myname, __returnRequest);
		  // envoi du signal okDoor
		  async_write(Door_okDoor__Controller_okDoor, &okDoor, 1);
		  //traceRequest(__myname, __returnRequest);
		  // changement d etat 
		  __currentState = STATE__Idle;
		  
	break;

/******************************DoorOpenedWhileHeating******************************/

	 case STATE__DoorOpenedWhileHeating: 
		  //traceStateEntering(__myname, "DoorOpenedWhileHeating");
		  // reception du signal closed
		  async_read( Door_closed__Controller_closed,&closed,1);
		  //traceRequest(__myname, __returnRequest);
		  // envoi du signal okDoor
		  async_write(Door_okDoor__Controller_okDoor, &okDoor, 1);
		  //traceRequest(__myname, __returnRequest);
		  // envoi du signal startMagnetron
		 async_write(Controller_startMagnetron__Magnetron_startM, &startMagnetron, 1);
		  //traceRequest(__myname, __returnRequest);
		  // changement d etat 
		  __currentState = STATE__Heating;
	break;
 }
  }
  return NULL;
}





