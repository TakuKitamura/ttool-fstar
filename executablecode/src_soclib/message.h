#ifndef MESSAGE_H
#define MESSAGE_H


struct message {
  struct message *next;
  int nbOfParams;
  int *params;
  long id;
};

typedef struct message message;

void initMessages();
message *getNewMessageWithParams(int nbOfParams);
message *getNewMessage(int nbOfParams, int *params);
void destroyMessageWithParams(message *msg);
void destroyMessage(message *msg);



#endif
