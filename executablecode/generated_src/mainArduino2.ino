#include <Arduino_FreeRTOS.h>
#include <frt.h>


#define SEND_SYNC_REQUEST 0
#define RECEIVE_SYNC_REQUEST 2
#define SEND_ASYNC_REQUEST 4
#define RECEIVE_ASYNC_REQUEST 6
#define DELAY 8
#define IMMEDIATE 10
#define SEND_BROADCAST_REQUEST 12
#define RECEIVE_BROADCAST_REQUEST 14

#define DEBUG_ON 1
#define DEBUG_OFF 2

int debug = DEBUG_ON;

struct timespec
{
    int tv_sec;    /* Seconds.  */
    int tv_nsec;  /* Nanoseconds.  */
};

struct message {
    struct message *next;
    int nbOfParams;
    int *params;
    long id;
};

long __id_message = 0;
frt::Mutex __message_mutex;
frt::Mutex mutex ;
int mainConditionaVariable = 0;
struct request;

struct setOfRequests {
    char* owner;
    struct request *head;
    timespec startTime;
    timespec completionTime;
    int *conditionVariable;
    frt::Mutex *mutex;
  
    int hasATimeRequest; // Means that at least on request of the list hasn't completed yet its time delay
    timespec minTimeToWait;
    struct request *selectedRequest;
};

struct request {
    struct request *next;
    struct setOfRequests* listOfRequests;
    struct request* nextRequestInList;
    struct request* relatedRequest; // For synchro and broadcast
    struct syncchannel *syncChannel;
    struct asyncchannel *asyncChannel;
  
    int type;
    int ID;
    int hasDelay;;
    timespec delay;
    int nbOfParams; // synchronous com.
    int **params;  // synchronous com.
    message *msg; // Asynchronous comm.
  
  
    // Filled by the request manager
    int executable;
    int selected;
    int alreadyPending; // Whether it has been taken into account for execution or not
    int delayElapsed;
    timespec myStartTime; // Time at which the delay has expired
};

struct asyncchannel {
    char *outname;
    char *inname;
    int isBlocking; // In writing. Reading is always blocking
    int maxNbOfMessages; //
    struct request* outWaitQueue;
    struct request* inWaitQueue;
    message *pendingMessages;
    int currentNbOfMessages;
};

struct syncchannel {
    char *outname;
    char *inname;
    struct request* inWaitQueue;
    struct request* outWaitQueue;
    bool isBroadcast;
};
/* User code */
void __user_init() {
}

/* End of User code */

// Header code defined in the model

void __userImplemented__MainBlock__ledOn(){
  digitalWrite(13,HIGH) ; 
  Serial.println("Led ON from Task1");
}

void __userImplemented__MainBlock__ledOff(){
  digitalWrite(13,LOW) ; 
  Serial.println("Led OFF from Task1");
}
void __userImplemented__MainBlock__initLed(){
  pinMode(13,OUTPUT) ;
}

// End of header code defined in the model


void MainBlock__ledOn() {
  __userImplemented__MainBlock__ledOn();
}


void MainBlock__ledOff() {
  __userImplemented__MainBlock__ledOff();
}


void MainBlock__initLed() {
  __userImplemented__MainBlock__initLed();
}


#define MainBlock_STATE__START__STATE 0
#define MainBlock_STATE__waitingForPeriodOn 1
#define MainBlock_STATE__onPeriodOK 2
#define MainBlock_STATE__waitingForPeriodOff 3
#define MainBlock_STATE__offPeriodOK 4
#define MainBlock_STATE__STOP__STATE 5
void Task_MainBlock( void *pvParameters );

int MainBlock__currentState = MainBlock_STATE__START__STATE;
request MainBlock__req0;
int *MainBlock__params0[0];
request MainBlock__req1;
int *MainBlock__params1[0];
setOfRequests MainBlock__list;
request *MainBlock__returnRequest;

// Header code defined in the model
const int trigPin = 2;
const int echoPin = 3;

long duration;
int distance;


void __userImplemented__MainBlock_0__initPins(){
    pinMode(trigPin, OUTPUT); 
    pinMode(echoPin, INPUT);
}

void __userImplemented__MainBlock_0__getDistance(){
  digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    // Sets the trigPin on HIGH state for 10 micro seconds
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
    // Reads the echoPin, returns the sound wave travel time in microseconds
    duration = pulseIn(echoPin, HIGH);
    // Calculating the distance
    distance = duration * 0.034 / 2;
    // Prints the distance on the Serial Monitor
    Serial.print("Distance from Task 2: ");
    Serial.println(distance);
}

// End of header code defined in the model


void MainBlock_0__getDistance() {
  __userImplemented__MainBlock_0__getDistance();
}


void MainBlock_0__initPins() {
  __userImplemented__MainBlock_0__initPins();
}


#define MainBlock_0_STATE__START__STATE 0
#define MainBlock_0_STATE__waitingForPeriodOn 1
#define MainBlock_0_STATE__onPeriodOK 2
#define MainBlock_0_STATE__waitingForPeriodOff 3
#define MainBlock_0_STATE__offPeriodOK 4
#define MainBlock_0_STATE__STOP__STATE 5
void Task_MainBlock_0( void *pvParameters );

int MainBlock_0__currentState = MainBlock_0_STATE__START__STATE;
request MainBlock_0__req0;
int *MainBlock_0__params0[0];
request MainBlock_0__req1;
int *MainBlock_0__params1[0];
setOfRequests MainBlock_0__list;
request *MainBlock_0__returnRequest;


void setup() { 
   Serial.begin(9600);
  /* Activating randomness */
  initRandom(0);
  char __myname[] = "";
  fillListOfRequests(&MainBlock__list, __myname, &mainConditionaVariable, &mutex);
  xTaskCreate (Task_MainBlock, (const portCHAR *)"MainBlock", 128, NULL, 1, NULL);
  fillListOfRequests(&MainBlock_0__list, __myname, &mainConditionaVariable, &mutex);
  xTaskCreate (Task_MainBlock_0, (const portCHAR *)"MainBlock_0", 128, NULL, 1, NULL);
  
}

void loop() { 
  
}

void Task_MainBlock( void *pvParameters )
{
  (void) pvParameters;
  int period = 1000;
  int x = 0;
  int period2 = 100;
  
  mutex.unlock();
  
  /* Main loop on states */
  while(MainBlock__currentState != MainBlock_STATE__STOP__STATE) {
    switch(MainBlock__currentState) {
      case MainBlock_STATE__START__STATE: 
      MainBlock__initLed();
      MainBlock__currentState = MainBlock_STATE__waitingForPeriodOn;
      break;
      
      case MainBlock_STATE__waitingForPeriodOn: 
      waitFor(period,period);
      MainBlock__currentState = MainBlock_STATE__onPeriodOK;
      break;
      
      case MainBlock_STATE__onPeriodOK: 
      MainBlock__ledOn();
      x = x+1;
      MainBlock__currentState = MainBlock_STATE__waitingForPeriodOff;
      break;
      
      case MainBlock_STATE__waitingForPeriodOff: 
      waitFor(period,period);
      MainBlock__currentState = MainBlock_STATE__offPeriodOK;
      break;
      
      case MainBlock_STATE__offPeriodOK: 
      if (!(x > 5)) {
        makeNewRequest(&MainBlock__req0, 210, IMMEDIATE, 0, 0, 0, 0, MainBlock__params0);
        addRequestToList(&MainBlock__list, &MainBlock__req0);
      }
      if (x > 5) {
        makeNewRequest(&MainBlock__req1, 227, IMMEDIATE, 0, 0, 0, 0, MainBlock__params1);
        addRequestToList(&MainBlock__list, &MainBlock__req1);
      }
      if (nbOfRequests(&MainBlock__list) == 0) {
        debug2Msg("No possible request");
        MainBlock__currentState = MainBlock_STATE__STOP__STATE;
        break;
      }
      MainBlock__returnRequest = executeListOfRequests(&MainBlock__list);
      clearListOfRequests(&MainBlock__list);
       if (MainBlock__returnRequest == &MainBlock__req0) {
        MainBlock__ledOff();
        MainBlock__currentState = MainBlock_STATE__waitingForPeriodOn;
        
      }
      else  if (MainBlock__returnRequest == &MainBlock__req1) {
        MainBlock__ledOff();
        MainBlock__currentState = MainBlock_STATE__STOP__STATE;
        
      }
      break;
      
    }
  }
while(1){};
}
void Task_MainBlock_0( void *pvParameters )
{
  (void) pvParameters;
  int period = 2000;
  int x = 0;
  int period2 = 100;
  
  mutex.unlock();
  
  /* Main loop on states */
  while(MainBlock_0__currentState != MainBlock_0_STATE__STOP__STATE) {
    switch(MainBlock_0__currentState) {
      case MainBlock_0_STATE__START__STATE: 
      waitFor(500,500);
      MainBlock_0__initPins();
      MainBlock_0__currentState = MainBlock_0_STATE__waitingForPeriodOn;
      break;
      
      case MainBlock_0_STATE__waitingForPeriodOn: 
      waitFor(period,period);
      MainBlock_0__currentState = MainBlock_0_STATE__onPeriodOK;
      break;
      
      case MainBlock_0_STATE__onPeriodOK: 
      x = x+1;
      MainBlock_0__getDistance();
      MainBlock_0__currentState = MainBlock_0_STATE__waitingForPeriodOff;
      break;
      
      case MainBlock_0_STATE__waitingForPeriodOff: 
      MainBlock_0__currentState = MainBlock_0_STATE__offPeriodOK;
      break;
      
      case MainBlock_0_STATE__offPeriodOK: 
      if (x > 10) {
        makeNewRequest(&MainBlock_0__req0, 239, IMMEDIATE, 0, 0, 0, 0, MainBlock_0__params0);
        addRequestToList(&MainBlock_0__list, &MainBlock_0__req0);
      }
      if (!(x > 10)) {
        makeNewRequest(&MainBlock_0__req1, 243, IMMEDIATE, 0, 0, 0, 0, MainBlock_0__params1);
        addRequestToList(&MainBlock_0__list, &MainBlock_0__req1);
      }
      if (nbOfRequests(&MainBlock_0__list) == 0) {
        debug2Msg("No possible request");
        MainBlock_0__currentState = MainBlock_0_STATE__STOP__STATE;
        break;
      }
      MainBlock_0__returnRequest = executeListOfRequests(&MainBlock_0__list);
      clearListOfRequests(&MainBlock_0__list);
       if (MainBlock_0__returnRequest == &MainBlock_0__req0) {
        MainBlock_0__currentState = MainBlock_0_STATE__STOP__STATE;
        
      }
      else  if (MainBlock_0__returnRequest == &MainBlock_0__req1) {
        MainBlock_0__currentState = MainBlock_0_STATE__waitingForPeriodOn;
        
      }
      break;
      
    }
  }
while(1){};
}

void criticalErrorInt(String msg, int Int) {
    Serial.print("Critical error : ") ;
    Serial.print(msg) ;
    Serial.print(", ") ;
    Serial.println(Int) ;
}

void criticalError(String msg) {
    Serial.print("Critical error : ") ;
    Serial.println(msg) ;
}

int computeRandom(int min, int max) {
    return random(min, max);
}

long computeLongRandom(long min, long max) {
  
    return (long)random(min, max);
}

void initRandom(int pin) {
    randomSeed(analogRead(pin));
}

int my_clock_gettime(struct timespec *ts) {
    ts->tv_sec = millis() / 1000;
    ts->tv_nsec = micros();
    return 0;
}


void addTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
    dest->tv_nsec = src1->tv_nsec + src2->tv_nsec;
    dest->tv_sec = src1->tv_sec + src2->tv_sec;
    if (dest->tv_nsec > 1000000000) {
        dest->tv_sec = dest->tv_sec + (dest->tv_nsec / 1000000000);
        dest->tv_nsec = dest->tv_nsec % 1000000000;
    }
}

void diffTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
    int diff = 0;
    if (src1->tv_nsec > src2->tv_nsec) {
        diff ++;
        dest->tv_nsec = src2->tv_nsec - src1->tv_nsec + 1000000000;
    } else {
        dest->tv_nsec = src2->tv_nsec - src1->tv_nsec;
    }
  
    dest->tv_sec = src2->tv_sec - src1->tv_sec - diff;
}



int isBefore(struct timespec *src1, struct timespec *src2) {
    if (src1->tv_sec > src2->tv_sec) {
        return 0;
    }
  
    if (src1->tv_sec < src2->tv_sec) {
        return 1;
    }
  
    if (src1->tv_nsec < src2->tv_nsec) {
        return 1;
    }
    return 0;
}

void minTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
  
    if (isBefore(src1, src2)) {
        dest->tv_nsec = src1->tv_nsec;
        dest->tv_sec = src1->tv_sec;
    } else {
        dest->tv_nsec = src2->tv_nsec;
        dest->tv_sec = src2->tv_sec;
    }
  
}

void delayToTimeSpec(struct timespec *ts, long delay) {
    ts->tv_nsec = (delay % 1000000) * 1000;
    ts->tv_sec = (delay / 1000000);
}


void waitFor(long minDelay, long maxDelay) {
    long delay;
    delay = computeLongRandom(minDelay, maxDelay);
    vTaskDelay( delay / portTICK_PERIOD_MS );
}

long getMessageID() {
    long tmp;
    __message_mutex.lock();
    tmp = __id_message;
    __id_message ++;
    __message_mutex.unlock();
    return tmp;
}

struct message *getNewMessageWithParams(int nbOfParams) {
  
    message *msg = (message *)(malloc(sizeof(struct message)));
    if (msg == NULL) {
        criticalError("Allocation of request failed");
    }
    msg->nbOfParams = nbOfParams;
    msg->params = (int *)(malloc(sizeof(int) * nbOfParams));
    msg->id = getMessageID();
    return msg;
}

struct message *getNewMessage(int nbOfParams, int *params) {
  
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

struct asyncchannel *getNewAsyncchannel(char *outname, char *inname, int isBlocking, int maxNbOfMessages) {
    asyncchannel * asyncch = (asyncchannel *)(malloc(sizeof(struct asyncchannel)));
    if (asyncch == NULL) {
        criticalError("Allocation of asyncchannel failed");
    }
    asyncch->inname = inname;
    asyncch->outname = outname;
    asyncch->isBlocking = isBlocking;
    asyncch->maxNbOfMessages = maxNbOfMessages;
  
    return asyncch;
}

void destroyAsyncchannel(asyncchannel *asyncch) {
    free(asyncch);
}

message* getAndRemoveOldestMessageFromAsyncChannel(asyncchannel *channel) {
    message *msg;
    message *previous;
  
    if (channel->currentNbOfMessages == 0) {
        return NULL;
    }
  
    if (channel->currentNbOfMessages == 1) {
        channel->currentNbOfMessages = 0;
        msg = channel->pendingMessages;
        channel->pendingMessages = NULL;
        return msg;
    }
  
    msg = channel->pendingMessages;
    previous = msg;
    while (msg->next != NULL) {
        previous = msg;
        msg = msg->next;
    }
  
    channel->currentNbOfMessages = channel->currentNbOfMessages - 1;
    previous->next = NULL;
    return msg;
}

void addMessageToAsyncChannel(asyncchannel *channel, message *msg) {
    msg->next = channel->pendingMessages;
    channel->pendingMessages = msg;
    channel->currentNbOfMessages = channel->currentNbOfMessages + 1;
}

struct syncchannel *getNewSyncchannel(char *outname, char *inname) {
    syncchannel * syncch = (syncchannel *)(malloc(sizeof(struct syncchannel)));
    if (syncch == NULL) {
        criticalError("Allocation of request failed");
    }
    syncch->inname = inname;
    syncch->outname = outname;
    syncch->inWaitQueue = NULL;
    syncch->outWaitQueue = NULL;
    syncch->isBroadcast = false;
    return syncch;
}

void setBroadcast(syncchannel *syncch, bool b) {
    syncch->isBroadcast = b;
}


void destroySyncchannel(syncchannel *syncch) {
    free(syncch);
}

struct request *getNewRequest(int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params) {
    request *req = (request *)(malloc(sizeof(struct request)));
  
    if (req == NULL) {
        criticalError("Allocation of request failed");
    }
  
    makeNewRequest(req,  ID, type, hasDelay, minDelay, maxDelay, nbOfParams, params);
    return req;
}


// Delays are in microseconds
void makeNewRequest(request *req, int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params) {
    long delay;
    int i;
  
    req->next = NULL;
    req->listOfRequests = NULL;
    req->nextRequestInList = NULL;
  
    req->type = type;
    req->ID = ID;
    req->hasDelay = hasDelay;
  
    if (req->hasDelay > 0) {
        delay = computeLongRandom(minDelay, maxDelay);
        delayToTimeSpec(&(req->delay), delay);
    }
  
    req->selected = 0;
    req->nbOfParams = nbOfParams;
    req->params = params;
  
    req->alreadyPending = 0;
    req->delayElapsed = 0;
  
    req->relatedRequest = NULL;
  
    if (type == SEND_ASYNC_REQUEST) {
        // Must create a new message
        req->msg = getNewMessageWithParams(nbOfParams);
        for (i = 0; i < nbOfParams; i++) {
            req->msg->params[i] = *(params[i]);
        }
    }
  
}




void destroyRequest(request *req) {
    free((void *)req);
}

int isRequestSelected(request *req) {
    return req->selected;
}

int nbOfRequests(struct setOfRequests *list) {
    int cpt = 0;
    request *req;
  
    req = list->head;
  
    while (req != NULL) {
        cpt ++;
        req = req->nextRequestInList;
    }
  
    return cpt;
}

struct request *getRequestAtIndex(struct setOfRequests *list, int index) {
    int cpt = 0;
    request * req = list->head;
  
    while (cpt < index) {
        req = req->nextRequestInList;
        cpt ++;
    }
  
    return req;
  
}


struct request * addToRequestQueue(request *list, request *requestToAdd) {
    request *origin = list;
  
    if (list == NULL) {
        return requestToAdd;
    }
  
    while (list->next != NULL) {
        list = list->next;
    }
  
    list->next = requestToAdd;
  
    requestToAdd->next = NULL;
  
    return origin;
}

struct request * removeRequestFromList(request *list, request *requestToRemove) {
    request *origin = list;
  
    if (list == requestToRemove) {
        return list->next;
    }
  
  
    while (list->next != requestToRemove) {
        list = list->next;
    }
  
    list->next = requestToRemove->next;
  
    return origin;
}


void copyParameters(request *src, request *dst) {
    int i;
    for (i = 0; i < dst->nbOfParams; i++) {
        *(dst->params[i]) = *(src->params[i]);
    }
}


void clearListOfRequests(struct setOfRequests *list) {
    list->head = NULL;
}

struct setOfRequests *newListOfRequests(int variable, frt::Mutex *mutex) {
    setOfRequests *list = (setOfRequests *)(malloc(sizeof(setOfRequests)));
    list->head = NULL;
    *(list->conditionVariable) = variable;
    list->mutex = mutex;
  
    return list;
}

void fillListOfRequests(struct setOfRequests *list, char* name, int* variable, frt::Mutex *mutex) {
    list->head = NULL;
    list->owner = name;
    list->conditionVariable = variable;
    list->mutex = mutex;
}


void addRequestToList(struct setOfRequests *list, request* req) {
    request *tmpreq;
  
    if (list == NULL) {
        criticalError("NULL List in addRequestToList");
    }
  
    if (req == NULL) {
        criticalError("NULL req in addRequestToList");
    }
  
    req->listOfRequests = list;
  
    if (list->head == NULL) {
        list->head = req;
        req->nextRequestInList = NULL;
        return;
    }
  
    tmpreq = list->head;
    while (tmpreq->nextRequestInList != NULL) {
        tmpreq = tmpreq->nextRequestInList;
    }
  
    tmpreq->nextRequestInList = req;
    req->nextRequestInList = NULL;
}

void removeAllPendingRequestsFromPendingLists(request *req, int apartThisOne) {
    setOfRequests *list = req->listOfRequests;
    request *reqtmp;
  
    if (list == NULL) {
        return;
    }
  
    reqtmp = list->head;
  
    while (reqtmp != NULL) {
    
        if (reqtmp->alreadyPending) {
            if (reqtmp->type ==  RECEIVE_SYNC_REQUEST) {
                debug2Msg("Removing send sync request from inWaitQueue");
                reqtmp->syncChannel->inWaitQueue = removeRequestFromList(reqtmp->syncChannel->inWaitQueue, reqtmp);
        
            }
      
            if (reqtmp->type ==  SEND_SYNC_REQUEST) {
                debug2Msg("Removing receive sync request from outWaitQueue");
                reqtmp->syncChannel->outWaitQueue = removeRequestFromList(reqtmp->syncChannel->outWaitQueue, reqtmp);
        
            }
      
            if (reqtmp->type ==  RECEIVE_BROADCAST_REQUEST) {
                debug2Msg("Removing broadcast receive request from inWaitQueue");
                reqtmp->syncChannel->inWaitQueue = removeRequestFromList(reqtmp->syncChannel->inWaitQueue, reqtmp);
        
            }
        }
        reqtmp = reqtmp->nextRequestInList;
    }
}


// Identical means belonging to the same ListOfRequest
// Returns the identical request if found, otherwise, null
struct request *hasIdenticalRequestInListOfSelectedRequests(request *req, request *list) {
  
    while (list != NULL) {
        if (list->listOfRequests == req->listOfRequests) {
            return list;
        }
        list = list->relatedRequest;
    }
  
    return NULL;
}

struct request* replaceInListOfSelectedRequests(request *oldRequest, request *newRequest, request *list) {
    request *head = list;
  
    if (list == oldRequest) {
        newRequest->relatedRequest = oldRequest->relatedRequest;
        return newRequest;
    }
  
    //list=list->relatedRequest;
    while (list->relatedRequest != oldRequest) {
        list = list->relatedRequest;
    }
  
    list->relatedRequest = newRequest;
    newRequest->relatedRequest = oldRequest->relatedRequest;
  
    return head;
}


int nbOfRelatedRequests(request *list) {
    int cpt = 0;
    while (list->relatedRequest != NULL) {
        cpt ++;
        list = list->relatedRequest;
    }
  
    return cpt;
}


void executeSendSyncTransaction(request *req) {
    int cpt;
    request *selectedReq;
  
    // At least one transaction available -> must select one randomly
    // First: count how many of them are available
    // Then, select one
    // Broadcast the new condition!
  
    cpt = 0;
    request* currentReq = req->syncChannel->inWaitQueue;
    debug2Msg("Execute send sync tr");
  
    while (currentReq != NULL) {
        cpt ++;
        currentReq = currentReq->next;
    }
  
    cpt = random() % cpt;
  
    // Head of the list?
    selectedReq = req->syncChannel->inWaitQueue;
    while (cpt > 0) {
        selectedReq = selectedReq->next;
        cpt --;
    }
  
    // Remove all related request from list requests
    //req->syncChannel->inWaitQueue = removeRequestFromList(req->syncChannel->inWaitQueue, selectedReq);
    debug2Msg("Setting related request");
    req->relatedRequest = selectedReq;
  
    // Select the selected request, and notify the information
    selectedReq->selected = 1;
    selectedReq->listOfRequests->selectedRequest = selectedReq;
  
    // Handle parameters
    copyParameters(req, selectedReq);
    debug2Msg("Signaling");
  
    *(selectedReq->listOfRequests->conditionVariable) = 1 ;
  
  
}

void executeReceiveSyncTransaction(request *req) {
    int cpt;
    request *selectedReq;
  
    // At least one transaction available -> must select one randomly
    // First: count how many of them are available
    // Then, select one
    // Broadcast the new condition!
  
    request* currentReq = req->syncChannel->outWaitQueue;
    cpt = 0;
    debug2Msg("Execute receive sync tr");
  
    while (currentReq != NULL) {
        cpt ++;
        //debugInt("cpt", cpt);
        currentReq = currentReq->next;
    }
    cpt = random() % cpt;
    selectedReq = req->syncChannel->outWaitQueue;
    while (cpt > 0) {
        selectedReq = selectedReq->next;
        cpt --;
    }
  
    //req->syncChannel->outWaitQueue = removeRequestFromList(req->syncChannel->outWaitQueue, selectedReq);
    debug2Msg("Setting related request");
    req->relatedRequest = selectedReq;
  
    // Select the request, and notify the information in the channel
    selectedReq->selected = 1;
    selectedReq->listOfRequests->selectedRequest = selectedReq;
  
    // Handle parameters
    copyParameters(selectedReq, req);
    debug2Msg("Signaling");
    *(selectedReq->listOfRequests->conditionVariable) = 1;
  
  
}


void executeSendAsyncTransaction(request *req) {
    request *selectedReq;
  
    // Full FIFO?
    if (req->asyncChannel->currentNbOfMessages == req->asyncChannel->maxNbOfMessages) {
        // Must remove the oldest  message
        getAndRemoveOldestMessageFromAsyncChannel(req->asyncChannel);
    }
  
    addMessageToAsyncChannel(req->asyncChannel, req->msg);
    debug2Msg("Signaling async write to all requests waiting ");
    selectedReq = req->asyncChannel->inWaitQueue;
    while (selectedReq != NULL) {
    
        *(selectedReq->listOfRequests->conditionVariable) = 1;
        selectedReq = selectedReq->next;
    }
    debug2Msg("Signaling done");
  
}

void executeReceiveAsyncTransaction(request *req) {
    int i;
    request *selectedReq;
  
    req->msg = getAndRemoveOldestMessageFromAsyncChannel(req->asyncChannel);
  
    selectedReq = req->asyncChannel->outWaitQueue;
  
    // Must recopy parameters
    for (i = 0; i < req->nbOfParams; i++) {
        *(req->params[i]) = req->msg->params[i];
    }
  
  
    // unallocate message
    destroyMessageWithParams(req->msg);
    debug2Msg("Signaling async read to all requests waiting ");
    while (selectedReq != NULL) {
        *(selectedReq->listOfRequests->conditionVariable) = 1;
        selectedReq = selectedReq->next;
    }
    debug2Msg("Signaling done");
}


void executeSendBroadcastTransaction(request *req) {
    int cpt;
    request *tmpreq;
  
    // At least one transaction available -> must select all of them
    // but at most one per task
    // Then, broadcast the new condition!
  
    request* currentReq = req->syncChannel->inWaitQueue;
    request* currentLastReq = req;
    debug2Msg("Execute broadcast sync tr");
  
  
    while (currentReq != NULL) {
        tmpreq = hasIdenticalRequestInListOfSelectedRequests(currentReq, req->relatedRequest);
        if (tmpreq != NULL) {
            // Must select one of the two
            // If =1, replace, otherwise, just do nothing
            cpt = random() % 2;
            if (cpt == 1) {
                debug2Msg("Replacing broadcast request");
                req->relatedRequest = replaceInListOfSelectedRequests(tmpreq, currentReq, req->relatedRequest);
                currentReq->listOfRequests->selectedRequest = currentReq;
                copyParameters(req, currentReq);
                currentReq->selected = 1;
                currentLastReq = req;
                while (currentLastReq->relatedRequest != NULL) {
                    currentLastReq = currentLastReq->relatedRequest;
                }
            }
        } else {
            currentLastReq->relatedRequest = currentReq;
            currentReq->relatedRequest = NULL;
            currentReq->selected = 1;
            currentReq->listOfRequests->selectedRequest = currentReq;
            copyParameters(req, currentReq);
            currentLastReq = currentReq;
        }
    
        currentReq = currentReq->next;
    
    
    }
  
  
    currentReq = req->relatedRequest;
    cpt = 0;
    while (currentReq != NULL) {
        cpt ++;
        *(currentReq->listOfRequests->conditionVariable) = 1;
        currentReq = currentReq->relatedRequest;
    }
  
  
}


int executable(setOfRequests *list, int nb) {
    int cpt = 0;
    //int index = 0;
    request *req = list->head;
    timespec ts;
    int tsDone = 0;
    debug2Msg("Starting loop");
    list->hasATimeRequest = 0;
  
    while (req != NULL) {
        if (!(req->delayElapsed)) {
            if (req->hasDelay) {
                // Is the delay elapsed???
                if (tsDone == 0) {
                    my_clock_gettime(&ts);
                    tsDone = 1;
                }
        
                if (isBefore(&ts, &(req->myStartTime)) == 1) {
                    // Delay not elapsed
                    debug2Msg("---------t--------> delay NOT elapsed");
                    if (list->hasATimeRequest == 0) {
                        list->hasATimeRequest = 1;
                        list->minTimeToWait.tv_nsec = req->myStartTime.tv_nsec;
                        list->minTimeToWait.tv_sec = req->myStartTime.tv_sec;
                    } else {
                        minTime(&(req->myStartTime), &(list->minTimeToWait), &(list->minTimeToWait));
                    }
                }  else {
                    // Delay elapsed
                    debug2Msg("---------t--------> delay elapsed");
                    req->delayElapsed = 1;
                }
            } else {
                req->delayElapsed = 1;
            }
        }
        req = req->nextRequestInList;
    }
  
    req = list->head;
    while ((req != NULL) && (cpt < nb)) {
        req->executable = 0;
        if (req->delayElapsed) {
            if (req->type == SEND_SYNC_REQUEST) {
                debug2Msg("Send sync");
                if (req->syncChannel->inWaitQueue != NULL) {
                    debug2Msg("Send sync executable");
                    req->executable = 1;
                    cpt ++;
                }  else {
                    debug2Msg("Send sync not executable");
                }
                //index ++;
            }
      
            if (req->type == RECEIVE_SYNC_REQUEST) {
                debug2Msg("receive sync");
                if (req->syncChannel->outWaitQueue != NULL) {
                    req->executable = 1;
                    cpt ++;
                }
                //index ++;
            }
      
            if (req->type == SEND_ASYNC_REQUEST) {
                debug2Msg("Send async");
                if (!(req->asyncChannel->isBlocking)) {
                    // Can always add a message -> executable
                    debug2Msg("Send async executable since non blocking");
                    req->executable = 1;
                    cpt ++;
          
                    //blocking case ... channel full?
                } else {
                    if (req->asyncChannel->currentNbOfMessages < req->asyncChannel->maxNbOfMessages) {
                        // Not full!
                        debug2Msg("Send async executable since channel not full");
                        req->executable = 1;
                        cpt ++;
                    } else {
                        debug2Msg("Send async not executable: full, and channel is blocking");
                    }
                }
            }
      
            if (req->type == RECEIVE_ASYNC_REQUEST) {
                debug2Msg("receive async");
                if (req->asyncChannel->currentNbOfMessages > 0) {
                    debug2Msg("Receive async executable: not empty");
                    req->executable = 1;
                    cpt ++;
                } else {
                    debug2Msg("Receive async not executable: empty");
                }
                //index ++;
            }
      
      
            if (req->type == SEND_BROADCAST_REQUEST) {
                debug2Msg("send broadcast");
                req->executable = 1;
                cpt ++;
            }
      
            if (req->type == RECEIVE_BROADCAST_REQUEST) {
                debug2Msg("receive broadcast");
                // A receive broadcast is never executable
                req->executable = 0;
                //index ++;
            }
      
      
      
      
            if (req->type == IMMEDIATE) {
                debug2Msg("immediate");
                req->executable = 1;
                cpt ++;
            }
        }
    
        req = req->nextRequestInList;
    
    }
  
    return cpt;
}

void private__makeRequestPending(setOfRequests *list) {
    request *req = list->head;
    while (req != NULL) {
        if ((req->delayElapsed) && (!(req->alreadyPending))) {
            if (req->type == SEND_SYNC_REQUEST) {
                debug2Msg("Adding pending request in outWaitqueue");
                req->syncChannel->outWaitQueue = addToRequestQueue(req->syncChannel->outWaitQueue, req);
                req->alreadyPending = 1;
            }
      
            if (req->type ==  RECEIVE_SYNC_REQUEST) {
                debug2Msg("Adding pending request in inWaitqueue");
                req->alreadyPending = 1;
                req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);
            }
      
            if (req->type == SEND_ASYNC_REQUEST) {
                debug2Msg("Adding pending request in outWaitqueue");
                req->asyncChannel->outWaitQueue = addToRequestQueue(req->asyncChannel->outWaitQueue, req);
                req->alreadyPending = 1;
            }
      
            if (req->type ==  RECEIVE_ASYNC_REQUEST) {
                debug2Msg("Adding pending request in inWaitqueue");
                req->alreadyPending = 1;
                req->asyncChannel->inWaitQueue = addToRequestQueue(req->asyncChannel->inWaitQueue, req);
            }
      
            if (req->type ==  RECEIVE_BROADCAST_REQUEST) {
                debug2Msg("Adding pending broadcast request in inWaitqueue");
                req->alreadyPending = 1;
                req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);
            }
      
            if (req->type ==  SEND_BROADCAST_REQUEST) {
                debug2Msg("Adding pending broadcast request in outWaitqueue");
                req->alreadyPending = 1;
                req->syncChannel->outWaitQueue = addToRequestQueue(req->syncChannel->outWaitQueue, req);
            }
      
        }
    
        req = req->nextRequestInList;
    }
}

void private__makeRequest(request *req) {
    if (req->type == SEND_SYNC_REQUEST) {
        executeSendSyncTransaction(req);
    }
  
    if (req->type == RECEIVE_SYNC_REQUEST) {
        executeReceiveSyncTransaction(req);
    }
  
    if (req->type == SEND_ASYNC_REQUEST) {
        executeSendAsyncTransaction(req);
    }
  
    if (req->type == RECEIVE_ASYNC_REQUEST) {
        executeReceiveAsyncTransaction(req);
    }
  
    if (req->type == SEND_BROADCAST_REQUEST) {
        executeSendBroadcastTransaction(req);
    }
    // IMMEDIATE: Nothing to do
  
    // In all cases: remove other requests of the same list from their pending form1
    debug2Msg("Removing original req");
    removeAllPendingRequestsFromPendingLists(req, 1);
    removeAllPendingRequestsFromPendingListsRelatedRequests(req);
    if (req->relatedRequest != NULL) {
        debug2Msg("Removing related req");
        removeAllPendingRequestsFromPendingLists(req->relatedRequest, 0);
    }
  
  
}

void removeAllPendingRequestsFromPendingListsRelatedRequests(request *req) {
    if (req->relatedRequest != NULL) {
        debug2Msg("Removing related req");
        removeAllPendingRequestsFromPendingLists(req->relatedRequest, 0);
        // Recursive call
        removeAllPendingRequestsFromPendingListsRelatedRequests(req->relatedRequest);
    }
}


request *private__executeRequests0(setOfRequests *list, int nb) {
    int howMany, found;
    int selectedIndex, realIndex;
    request *selectedReq;
    request *req;
  
    // Compute which requests can be executed
    debug2Msg("Counting requests");
    howMany = executable(list, nb);
  
  
  
    if (howMany == 0) {
        debug2Msg("No pending requests");
        // Must make them pending
    
        private__makeRequestPending(list);
    
        return NULL;
    }
  
  
  
    // Select a request
    req = list->head;
    selectedIndex = (rand() % howMany) + 1;
  
    realIndex = 0;
    found = 0;
    while (req != NULL) {
        if (req->executable == 1) {
            found ++;
            if (found == selectedIndex) {
                break;
            }
        }
        realIndex ++;
        req = req->nextRequestInList;
    }
  
    selectedReq = getRequestAtIndex(list, realIndex);
    selectedReq->selected = 1;
    selectedReq->listOfRequests->selectedRequest = selectedReq;
  
    // Execute that request
    private__makeRequest(selectedReq);
  
    return selectedReq;
}


request *private__executeRequests(setOfRequests *list) {
    // Is a request already selected?
  
    if (list->selectedRequest != NULL) {
        return list->selectedRequest;
    }
    debug2Msg("No request selected -> looking for one!");
    return private__executeRequests0(list, nbOfRequests(list));
}




request *executeOneRequest(setOfRequests *list, request *req) {
    req->nextRequestInList = NULL;
    req->listOfRequests = list;
    list->head = req;
    return executeListOfRequests(list);
}


void setLocalStartTime(setOfRequests *list) {
  
    request *req = list->head;
  
    while (req != NULL) {
        if (req->hasDelay) {
            req->delayElapsed = 0;
            addTime(&(list->startTime), &(req->delay), &(req->myStartTime));
            debug2Msg(" -----t------>: Request with delay");
        } else {
            debug2Msg(" -----t------>: Request without delay");
            req->delayElapsed = 1;
            req->myStartTime.tv_nsec = list->startTime.tv_nsec;
            req->myStartTime.tv_sec = list->startTime.tv_sec;
        }
        req = req->nextRequestInList;
        list->mutex->unlock();
        list->mutex->lock();
    }
}


// Return the executed request
struct request *executeListOfRequests(struct setOfRequests *list) {
    request *req;
  
    my_clock_gettime(&list->startTime);
    list->selectedRequest = NULL;
    setLocalStartTime(list);
  
    // Try to find a request that could be executed
    debug2Msg("Locking mutex");
    list->mutex->unlock();
    list->mutex->lock();
    debug2Msg("Mutex locked");
    debug2Msg("Going to execute request");
    while ((req = private__executeRequests(list)) == NULL) {
        debug2Msg("Waiting for request!");
        if (list->hasATimeRequest == 1) {
            debug2Msg("Waiting for a request and at most for a given time");
            list->mutex->unlock();
            waitFor(list->minTimeToWait.tv_sec * 1000, list->minTimeToWait.tv_sec * 1000);
            *(list->conditionVariable) = 0 ;
            while (*(list->conditionVariable) != 1) {
                vTaskDelay( portTICK_PERIOD_MS );
            }
      
            list->mutex->lock();
      
        } else {
            debug2Msg("Releasing mutex");
            list->mutex->unlock();
            *(list->conditionVariable) = 0 ;
            while (*(list->conditionVariable) != 1) {
                vTaskDelay( portTICK_PERIOD_MS );
            }
      
            list->mutex->lock();
        }
        debug2Msg("Waking up for requests! -> getting mutex");
    }
    debug2Msg("Request selected!");
    my_clock_gettime(&list->completionTime);
    list->mutex->unlock();
    debug2Msg("Mutex unlocked");
    return req;
}


void debug2Msg(char* val ) {
    if (debug == DEBUG_ON) {
        Serial.println(val) ;
    }
}