
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

#include "message.h"
#include "myerrors.h"

long __id_message = 0;
pthread_mutex_t __message_mutex;


void initMessages() {
  if (pthread_mutex_init(&__message_mutex, NULL) < 0) { exit(-1);}
}

long getMessageID() {
 
  long tmp; 
 
  //pthread_mutex_lock(&__message_mutex);
  
  tmp = __id_message; 
  __id_message ++;
  //pthread_mutex_unlock(&__message_mutex);
 
  return tmp;
}

message *getNewMessageWithParams(int nbOfParams) {
	debugMsg("getNewMessageWithParams");
	message *msg = (message *)(malloc(sizeof(struct message)));
	if (msg == NULL) {
		criticalError("Allocation of request failed");
	}
	msg->nbOfParams = nbOfParams;	

	msg->params = (int *)(malloc(sizeof(int) * nbOfParams));

	//debugMsg("mutex: %x",&__message_mutex);
	msg->id = getMessageID();	

	printf("msg->id: %d\n",msg->id);

	return msg;
}

message *getNewMessage(int nbOfParams, int *params) {

  message *msg = (message *)(malloc(sizeof(struct message)));
  if (msg == NULL) {
    criticalError("Allocation of request failed");
  }
  msg->nbOfParams = nbOfParams;
  msg->params = params;
  msg->id = getMessageID();
  return msg;
}



void destroyMessageWithParams(message *msg) {
  free(msg->params);
  free(msg);
}

void destroyMessage(message *msg) {
  free(msg);
}
