#include "Door.h"
//#include "asyncchannel.h"
//#include "mwmr.h"

#define STATE__START__STATE 0
#define STATE__DoorIsOpened 1
#define STATE__IDLE 2
#define STATE__STOP__STATE 3

static uint32_t okDoor;
static uint32_t closed ;
static uint32_t ouvert;
const float freq = 300;
//canaux_door[] : est un tableau de canaux, unique paramètre du pththread
void *mainFunc__Door(struct mwmr_s *canaux_door[]){
	
 struct mwmr_s  *Door_open__Controller_open=canaux_door[0];
 struct mwmr_s  *Door_closed__Controller_closed=canaux_door[1];
 struct mwmr_s  *Door_okDoor__Controller_okDoor=canaux_door[2];

 int __currentState = STATE__START__STATE;
 char * __myname ;

 int borne_minimale = 2;	
 int borne_maximale = 4;
 uint32_t delay;

  
	
while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
		
/*******************************STATE__START__STATE***************************/
      case STATE__START__STATE:  
         __currentState = STATE__IDLE;
      break;

/*********************************STATE__DoorIsOpened*************************/
     case STATE__DoorIsOpened: 
	
		delay = rand()%(borne_maximale - borne_minimale) + borne_minimale;
		sleep (delay);
		
        // envoi du signal closed avec la semantique du write dans le canal non bloquant modifiee
	   	async_write( Door_closed__Controller_closed,&closed,1);
                
                  //traceRequest(__myname, __returnRequest);

		printf("Reading the output data");
	   	putchar('\n');
	   	
	   	// reception du signal okDoor
	  	async_read( Door_okDoor__Controller_okDoor,&okDoor, 1);

	   	printf("Data read OK");
	 	//traceRequest(__myname, __returnRequest);
	 	//debug2Msg(__myname, "-> (=====) Entering state + DoorIsOpened");
		
		// generation d un nombre aleatoire entre 2 et 4
		delay = rand()%(borne_maximale - borne_minimale) + borne_minimale;
		// temporisation en fonction de la frequence du processeur 
		delay = delay * (1/(freq));   //  sans multiplier par 10^6
		sleep (delay);

		//changement d'etat (STATE__IDLE)
		__currentState = STATE__IDLE;
      break;   

/********************************STATE__IDLE*****************************/
    case STATE__IDLE: 
		//traceStateEntering(__myname, "IDLE");
		//envoi du signal open
		async_write(Door_open__Controller_open, &ouvert, 1);
   		//traceRequest(__myname, __returnRequest);
		printf("Reading the output data");
		putchar('\n');
		// reception du signal okDoor
		async_read(Door_okDoor__Controller_okDoor, &okDoor,1);
  		printf("Door_okDoor__Controller_okDoor =", Door_okDoor__Controller_okDoor);
      	printf("Data read OK");
		putchar('\n');
   
		//traceRequest(__myname, __returnRequest);
		//debug2Msg(__myname, "-> (=====) Entering state + IDILE");
	
		//changement d'etat
      	__currentState = STATE__DoorIsOpened;
	break;   
    }
  }
  return NULL;
}

