#ifndef MESSAGE_H
#define MESSAGE_H


struct message;

struct setOfMessages {
  struct message *head;
};

typedef struct setOfMessages setOfMessages;

struct message {
  int nbOfParams;
  int *params;
};

typedef struct message message;

message *getNewMessageWithParams(int nbOfParams);
message * getNewMessage(int nbOfParams, int *params);
void destroyMessageWithParams(message *msg);
void destroyMessage(message *msg);



#endif
