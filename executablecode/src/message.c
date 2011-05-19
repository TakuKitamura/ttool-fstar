
#include <stdlib.h>
#include <unistd.h>

#include "message.h"
#include "myerrors.h"


message *getNewMessageWithParams(int nbOfParams) {
	
	message *msg = (message *)(malloc(sizeof(struct message)));
	if (msg == NULL) {
		criticalError("Allocation of request failed");
	}
	msg->nbOfParams = nbOfParams;
	msg->params = (int *)(malloc(sizeof(int) * nbOfParams));;
	return msg;
}

message *getNewMessage(int nbOfParams, int *params) {

  message *msg = (message *)(malloc(sizeof(struct message)));
  if (msg == NULL) {
    criticalError("Allocation of request failed");
  }
  msg->nbOfParams = nbOfParams;
  msg->params = params;
  return msg;
}



void destroyMessageWithParams(message *msg) {
  free(msg->params);
  free(msg);
}

void destroyMessage(message *msg) {
  free(msg);
}
