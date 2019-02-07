package avatartranslator.toexecutable;

import myutil.Plugin;
import myutil.TraceManager;


/**
 * Class MainFile_Arduino
 * Creation: 05/11/2017 by Berkey Koksal
 *
 * Developed by : Dhiaeddine ALIOUI
 * @version 3 15/11/2018
 */

public class MainFile_Arduino {

	private final static String SETUP_CODE = "void setup() { \n Serial.begin(9600);\n";
    private final static String LOOP_CODE = "void loop() { \n";

    private final static String INCLUDE_HEADER = "#include <Arduino_FreeRTOS.h>\n#include <frt.h>\n";
    ;
    private final static String LOCAL_INCLUDE_HEADER = "";

     private final static String CR = "\n";

    
    
    private String name;
    private String beforeSetupCode;
    private String setupCode;
    private String loopCode;
    private String afterLoopCode;
    private String functionsCode;
    private String stucturesCode;




    public MainFile_Arduino(String _name) {
        name = _name;
        beforeSetupCode="";
        setupCode="";
        loopCode="";
        afterLoopCode="";

        functionsCode="void criticalErrorInt(String msg, int Int) {\n" +
                "  Serial.print(\"Critical error : \") ;\n" +
                "  Serial.print(msg) ;\n" +
                "  Serial.print(\", \") ;\n" +
                "  Serial.println(Int) ;\n" +
                "}\n" +
                "\n" +
                "void criticalError(String msg) {\n" +
                "  Serial.print(\"Critical error : \") ;\n" +
                "  Serial.println(msg) ;\n" +
                "}\n" +
                "\n" +
                "int computeRandom(int min, int max) {\n" +
                "  return random(min, max);\n" +
                "}\n" +
                "\n" +
                "long computeLongRandom(long min, long max) {\n" +
                "\n" +
                "  return (long)random(min, max);\n" +
                "}\n" +
                "\n" +
                "void initRandom(int pin) {\n" +
                "  randomSeed(analogRead(pin));\n" +
                "}\n" +
                "\n" +
                "int my_clock_gettime(struct timespec *ts) {\n" +
                "  ts->tv_sec = millis() / 1000;\n" +
                "  ts->tv_nsec = micros();\n" +
                "  return 0;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void addTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {\n" +
                "  dest->tv_nsec = src1->tv_nsec + src2->tv_nsec;\n" +
                "  dest->tv_sec = src1->tv_sec + src2->tv_sec;\n" +
                "  if (dest->tv_nsec > 1000000000) {\n" +
                "    dest->tv_sec = dest->tv_sec + (dest->tv_nsec / 1000000000);\n" +
                "    dest->tv_nsec = dest->tv_nsec % 1000000000;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "void diffTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {\n" +
                "  int diff = 0;\n" +
                "  if (src1->tv_nsec > src2->tv_nsec) {\n" +
                "    diff ++;\n" +
                "    dest->tv_nsec = src2->tv_nsec - src1->tv_nsec + 1000000000;\n" +
                "  } else {\n" +
                "    dest->tv_nsec = src2->tv_nsec - src1->tv_nsec;\n" +
                "  }\n" +
                "\n" +
                "  dest->tv_sec = src2->tv_sec - src1->tv_sec - diff;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "int isBefore(struct timespec *src1, struct timespec *src2) {\n" +
                "  if (src1->tv_sec > src2->tv_sec) {\n" +
                "    return 0;\n" +
                "  }\n" +
                "\n" +
                "  if (src1->tv_sec < src2->tv_sec) {\n" +
                "    return 1;\n" +
                "  }\n" +
                "\n" +
                "  if (src1->tv_nsec < src2->tv_nsec) {\n" +
                "    return 1;\n" +
                "  }\n" +
                "  return 0;\n" +
                "}\n" +
                "\n" +
                "void minTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {\n" +
                "\n" +
                "  if (isBefore(src1, src2)) {\n" +
                "    dest->tv_nsec = src1->tv_nsec;\n" +
                "    dest->tv_sec = src1->tv_sec;\n" +
                "  } else {\n" +
                "    dest->tv_nsec = src2->tv_nsec;\n" +
                "    dest->tv_sec = src2->tv_sec;\n" +
                "  }\n" +
                "\n" +
                "}\n" +
                "\n" +
                "void delayToTimeSpec(struct timespec *ts, long delay) {\n" +
                "  ts->tv_nsec = (delay % 1000000) * 1000;\n" +
                "  ts->tv_sec = (delay / 1000000);\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void waitFor(long minDelay, long maxDelay) {\n" +
                "  long delay;\n" +
                "  delay = computeLongRandom(minDelay, maxDelay);\n" +
                "  vTaskDelay( delay / portTICK_PERIOD_MS );\n" +
                "}\n" +
                "\n" +
                "long getMessageID() {\n" +
                "  long tmp;\n" +
                "  __message_mutex.lock();\n" +
                "  tmp = __id_message;\n" +
                "  __id_message ++;\n" +
                "  __message_mutex.unlock();\n" +
                "  return tmp;\n" +
                "}\n" +
                "\n" +
                "struct message *getNewMessageWithParams(int nbOfParams) {\n" +
                "\n" +
                "  message *msg = (message *)(malloc(sizeof(struct message)));\n" +
                "  if (msg == NULL) {\n" +
                "    criticalError(\"Allocation of request failed\");\n" +
                "  }\n" +
                "  msg->nbOfParams = nbOfParams;\n" +
                "  msg->params = (int *)(malloc(sizeof(int) * nbOfParams));\n" +
                "  msg->id = getMessageID();\n" +
                "  return msg;\n" +
                "}\n" +
                "\n" +
                "struct message *getNewMessage(int nbOfParams, int *params) {\n" +
                "\n" +
                "  message *msg = (message *)(malloc(sizeof(struct message)));\n" +
                "  if (msg == NULL) {\n" +
                "    criticalError(\"Allocation of request failed\");\n" +
                "  }\n" +
                "  msg->nbOfParams = nbOfParams;\n" +
                "  msg->params = params;\n" +
                "  msg->id = getMessageID();\n" +
                "  return msg;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "void destroyMessageWithParams(message *msg) {\n" +
                "  free(msg->params);\n" +
                "  free(msg);\n" +
                "}\n" +
                "\n" +
                "void destroyMessage(message *msg) {\n" +
                "  free(msg);\n" +
                "}\n" +
                "\n" +
                "struct asyncchannel *getNewAsyncchannel(char *outname, char *inname, int isBlocking, int maxNbOfMessages) {\n" +
                "  asyncchannel * asyncch = (asyncchannel *)(malloc(sizeof(struct asyncchannel)));\n" +
                "  if (asyncch == NULL) {\n" +
                "    criticalError(\"Allocation of asyncchannel failed\");\n" +
                "  }\n" +
                "  asyncch->inname = inname;\n" +
                "  asyncch->outname = outname;\n" +
                "  asyncch->isBlocking = isBlocking;\n" +
                "  asyncch->maxNbOfMessages = maxNbOfMessages;\n" +
                "\n" +
                "  return asyncch;\n" +
                "}\n" +
                "\n" +
                "void destroyAsyncchannel(asyncchannel *asyncch) {\n" +
                "  free(asyncch);\n" +
                "}\n" +
                "\n" +
                "message* getAndRemoveOldestMessageFromAsyncChannel(asyncchannel *channel) {\n" +
                "  message *msg;\n" +
                "  message *previous;\n" +
                "\n" +
                "  if (channel->currentNbOfMessages == 0) {\n" +
                "    return NULL;\n" +
                "  }\n" +
                "\n" +
                "  if (channel->currentNbOfMessages == 1) {\n" +
                "    channel->currentNbOfMessages = 0;\n" +
                "    msg = channel->pendingMessages;\n" +
                "    channel->pendingMessages = NULL;\n" +
                "    return msg;\n" +
                "  }\n" +
                "\n" +
                "  msg = channel->pendingMessages;\n" +
                "  previous = msg;\n" +
                "  while (msg->next != NULL) {\n" +
                "    previous = msg;\n" +
                "    msg = msg->next;\n" +
                "  }\n" +
                "\n" +
                "  channel->currentNbOfMessages = channel->currentNbOfMessages - 1;\n" +
                "  previous->next = NULL;\n" +
                "  return msg;\n" +
                "}\n" +
                "\n" +
                "void addMessageToAsyncChannel(asyncchannel *channel, message *msg) {\n" +
                "  msg->next = channel->pendingMessages;\n" +
                "  channel->pendingMessages = msg;\n" +
                "  channel->currentNbOfMessages = channel->currentNbOfMessages + 1;\n" +
                "}\n" +
                "\n" +
                "struct syncchannel *getNewSyncchannel(char *outname, char *inname) {\n" +
                "  syncchannel * syncch = (syncchannel *)(malloc(sizeof(struct syncchannel)));\n" +
                "  if (syncch == NULL) {\n" +
                "    criticalError(\"Allocation of request failed\");\n" +
                "  }\n" +
                "  syncch->inname = inname;\n" +
                "  syncch->outname = outname;\n" +
                "  syncch->inWaitQueue = NULL;\n" +
                "  syncch->outWaitQueue = NULL;\n" +
                "  syncch->isBroadcast = false;\n" +
                "  return syncch;\n" +
                "}\n" +
                "\n" +
                "void setBroadcast(syncchannel *syncch, bool b) {\n" +
                "  syncch->isBroadcast = b;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void destroySyncchannel(syncchannel *syncch) {\n" +
                "  free(syncch);\n" +
                "}\n" +
                "\n" +
                "struct request *getNewRequest(int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params) {\n" +
                "  request *req = (request *)(malloc(sizeof(struct request)));\n" +
                "\n" +
                "  if (req == NULL) {\n" +
                "    criticalError(\"Allocation of request failed\");\n" +
                "  }\n" +
                "\n" +
                "  makeNewRequest(req,  ID, type, hasDelay, minDelay, maxDelay, nbOfParams, params);\n" +
                "  return req;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// Delays are in microseconds\n" +
                "void makeNewRequest(request *req, int ID, int type, int hasDelay, long minDelay, long maxDelay, int nbOfParams, int **params) {\n" +
                "  long delay;\n" +
                "  int i;\n" +
                "\n" +
                "  req->next = NULL;\n" +
                "  req->listOfRequests = NULL;\n" +
                "  req->nextRequestInList = NULL;\n" +
                "\n" +
                "  req->type = type;\n" +
                "  req->ID = ID;\n" +
                "  req->hasDelay = hasDelay;\n" +
                "\n" +
                "  if (req->hasDelay > 0) {\n" +
                "    delay = computeLongRandom(minDelay, maxDelay);\n" +
                "    delayToTimeSpec(&(req->delay), delay);\n" +
                "  }\n" +
                "\n" +
                "  req->selected = 0;\n" +
                "  req->nbOfParams = nbOfParams;\n" +
                "  req->params = params;\n" +
                "\n" +
                "  req->alreadyPending = 0;\n" +
                "  req->delayElapsed = 0;\n" +
                "\n" +
                "  req->relatedRequest = NULL;\n" +
                "\n" +
                "  if (type == SEND_ASYNC_REQUEST) {\n" +
                "    // Must create a new message\n" +
                "    req->msg = getNewMessageWithParams(nbOfParams);\n" +
                "    for (i = 0; i < nbOfParams; i++) {\n" +
                "      req->msg->params[i] = *(params[i]);\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "void destroyRequest(request *req) {\n" +
                "  free((void *)req);\n" +
                "}\n" +
                "\n" +
                "int isRequestSelected(request *req) {\n" +
                "  return req->selected;\n" +
                "}\n" +
                "\n" +
                "int nbOfRequests(struct setOfRequests *list) {\n" +
                "  int cpt = 0;\n" +
                "  request *req;\n" +
                "\n" +
                "  req = list->head;\n" +
                "\n" +
                "  while (req != NULL) {\n" +
                "    cpt ++;\n" +
                "    req = req->nextRequestInList;\n" +
                "  }\n" +
                "\n" +
                "  return cpt;\n" +
                "}\n" +
                "\n" +
                "struct request *getRequestAtIndex(struct setOfRequests *list, int index) {\n" +
                "  int cpt = 0;\n" +
                "  request * req = list->head;\n" +
                "\n" +
                "  while (cpt < index) {\n" +
                "    req = req->nextRequestInList;\n" +
                "    cpt ++;\n" +
                "  }\n" +
                "\n" +
                "  return req;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "struct request * addToRequestQueue(request *list, request *requestToAdd) {\n" +
                "  request *origin = list;\n" +
                "\n" +
                "  if (list == NULL) {\n" +
                "    return requestToAdd;\n" +
                "  }\n" +
                "\n" +
                "  while (list->next != NULL) {\n" +
                "    list = list->next;\n" +
                "  }\n" +
                "\n" +
                "  list->next = requestToAdd;\n" +
                "\n" +
                "  requestToAdd->next = NULL;\n" +
                "\n" +
                "  return origin;\n" +
                "}\n" +
                "\n" +
                "struct request * removeRequestFromList(request *list, request *requestToRemove) {\n" +
                "  request *origin = list;\n" +
                "\n" +
                "  if (list == requestToRemove) {\n" +
                "    return list->next;\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "  while (list->next != requestToRemove) {\n" +
                "    list = list->next;\n" +
                "  }\n" +
                "\n" +
                "  list->next = requestToRemove->next;\n" +
                "\n" +
                "  return origin;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void copyParameters(request *src, request *dst) {\n" +
                "  int i;\n" +
                "  for (i = 0; i < dst->nbOfParams; i++) {\n" +
                "    *(dst->params[i]) = *(src->params[i]);\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void clearListOfRequests(struct setOfRequests *list) {\n" +
                "  list->head = NULL;\n" +
                "}\n" +
                "\n" +
                "struct setOfRequests *newListOfRequests(int variable, frt::Mutex *mutex) {\n" +
                "  setOfRequests *list = (setOfRequests *)(malloc(sizeof(setOfRequests)));\n" +
                "  list->head = NULL;\n" +
                "  *(list->conditionVariable) = variable;\n" +
                "  list->mutex = mutex;\n" +
                "\n" +
                "  return list;\n" +
                "}\n" +
                "\n" +
                "void fillListOfRequests(struct setOfRequests *list, char* name, int* variable, frt::Mutex *mutex) {\n" +
                "  list->head = NULL;\n" +
                "  list->owner = name;\n" +
                "  list->conditionVariable = variable;\n" +
                "  list->mutex = mutex;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void addRequestToList(struct setOfRequests *list, request* req) {\n" +
                "  request *tmpreq;\n" +
                "\n" +
                "  if (list == NULL) {\n" +
                "    criticalError(\"NULL List in addRequestToList\");\n" +
                "  }\n" +
                "\n" +
                "  if (req == NULL) {\n" +
                "    criticalError(\"NULL req in addRequestToList\");\n" +
                "  }\n" +
                "\n" +
                "  req->listOfRequests = list;\n" +
                "\n" +
                "  if (list->head == NULL) {\n" +
                "    list->head = req;\n" +
                "    req->nextRequestInList = NULL;\n" +
                "    return;\n" +
                "  }\n" +
                "\n" +
                "  tmpreq = list->head;\n" +
                "  while (tmpreq->nextRequestInList != NULL) {\n" +
                "    tmpreq = tmpreq->nextRequestInList;\n" +
                "  }\n" +
                "\n" +
                "  tmpreq->nextRequestInList = req;\n" +
                "  req->nextRequestInList = NULL;\n" +
                "}\n" +
                "\n" +
                "void removeAllPendingRequestsFromPendingLists(request *req, int apartThisOne) {\n" +
                "  setOfRequests *list = req->listOfRequests;\n" +
                "  request *reqtmp;\n" +
                "\n" +
                "  if (list == NULL) {\n" +
                "    return;\n" +
                "  }\n" +
                "\n" +
                "  reqtmp = list->head;\n" +
                "\n" +
                "  while (reqtmp != NULL) {\n" +
                "\n" +
                "    if (reqtmp->alreadyPending) {\n" +
                "      if (reqtmp->type ==  RECEIVE_SYNC_REQUEST) {\n" +
                "        debug2Msg(\"Removing send sync request from inWaitQueue\");\n" +
                "        reqtmp->syncChannel->inWaitQueue = removeRequestFromList(reqtmp->syncChannel->inWaitQueue, reqtmp);\n" +
                "\n" +
                "      }\n" +
                "\n" +
                "      if (reqtmp->type ==  SEND_SYNC_REQUEST) {\n" +
                "        debug2Msg(\"Removing receive sync request from outWaitQueue\");\n" +
                "        reqtmp->syncChannel->outWaitQueue = removeRequestFromList(reqtmp->syncChannel->outWaitQueue, reqtmp);\n" +
                "\n" +
                "      }\n" +
                "\n" +
                "      if (reqtmp->type ==  RECEIVE_BROADCAST_REQUEST) {\n" +
                "        debug2Msg(\"Removing broadcast receive request from inWaitQueue\");\n" +
                "        reqtmp->syncChannel->inWaitQueue = removeRequestFromList(reqtmp->syncChannel->inWaitQueue, reqtmp);\n" +
                "\n" +
                "      }\n" +
                "    }\n" +
                "    reqtmp = reqtmp->nextRequestInList;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// Identical means belonging to the same ListOfRequest\n" +
                "// Returns the identical request if found, otherwise, null\n" +
                "struct request *hasIdenticalRequestInListOfSelectedRequests(request *req, request *list) {\n" +
                "\n" +
                "  while (list != NULL) {\n" +
                "    if (list->listOfRequests == req->listOfRequests) {\n" +
                "      return list;\n" +
                "    }\n" +
                "    list = list->relatedRequest;\n" +
                "  }\n" +
                "\n" +
                "  return NULL;\n" +
                "}\n" +
                "\n" +
                "struct request* replaceInListOfSelectedRequests(request *oldRequest, request *newRequest, request *list) {\n" +
                "  request *head = list;\n" +
                "\n" +
                "  if (list == oldRequest) {\n" +
                "    newRequest->relatedRequest = oldRequest->relatedRequest;\n" +
                "    return newRequest;\n" +
                "  }\n" +
                "\n" +
                "  //list=list->relatedRequest;\n" +
                "  while (list->relatedRequest != oldRequest) {\n" +
                "    list = list->relatedRequest;\n" +
                "  }\n" +
                "\n" +
                "  list->relatedRequest = newRequest;\n" +
                "  newRequest->relatedRequest = oldRequest->relatedRequest;\n" +
                "\n" +
                "  return head;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "int nbOfRelatedRequests(request *list) {\n" +
                "  int cpt = 0;\n" +
                "  while (list->relatedRequest != NULL) {\n" +
                "    cpt ++;\n" +
                "    list = list->relatedRequest;\n" +
                "  }\n" +
                "\n" +
                "  return cpt;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void executeSendSyncTransaction(request *req) {\n" +
                "  int cpt;\n" +
                "  request *selectedReq;\n" +
                "\n" +
                "  // At least one transaction available -> must select one randomly\n" +
                "  // First: count how many of them are available\n" +
                "  // Then, select one\n" +
                "  // Broadcast the new condition!\n" +
                "\n" +
                "  cpt = 0;\n" +
                "  request* currentReq = req->syncChannel->inWaitQueue;\n" +
                "  debug2Msg(\"Execute send sync tr\");\n" +
                "\n" +
                "  while (currentReq != NULL) {\n" +
                "    cpt ++;\n" +
                "    currentReq = currentReq->next;\n" +
                "  }\n" +
                "\n" +
                "  cpt = random() % cpt;\n" +
                "\n" +
                "  // Head of the list?\n" +
                "  selectedReq = req->syncChannel->inWaitQueue;\n" +
                "  while (cpt > 0) {\n" +
                "    selectedReq = selectedReq->next;\n" +
                "    cpt --;\n" +
                "  }\n" +
                "\n" +
                "  // Remove all related request from list requests\n" +
                "  //req->syncChannel->inWaitQueue = removeRequestFromList(req->syncChannel->inWaitQueue, selectedReq);\n" +
                "  debug2Msg(\"Setting related request\");\n" +
                "  req->relatedRequest = selectedReq;\n" +
                "\n" +
                "  // Select the selected request, and notify the information\n" +
                "  selectedReq->selected = 1;\n" +
                "  selectedReq->listOfRequests->selectedRequest = selectedReq;\n" +
                "\n" +
                "  // Handle parameters\n" +
                "  copyParameters(req, selectedReq);\n" +
                "  debug2Msg(\"Signaling\");\n" +
                "\n" +
                "  *(selectedReq->listOfRequests->conditionVariable) = 1 ;\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "void executeReceiveSyncTransaction(request *req) {\n" +
                "  int cpt;\n" +
                "  request *selectedReq;\n" +
                "\n" +
                "  // At least one transaction available -> must select one randomly\n" +
                "  // First: count how many of them are available\n" +
                "  // Then, select one\n" +
                "  // Broadcast the new condition!\n" +
                "\n" +
                "  request* currentReq = req->syncChannel->outWaitQueue;\n" +
                "  cpt = 0;\n" +
                "  debug2Msg(\"Execute receive sync tr\");\n" +
                "\n" +
                "  while (currentReq != NULL) {\n" +
                "    cpt ++;\n" +
                "    //debugInt(\"cpt\", cpt);\n" +
                "    currentReq = currentReq->next;\n" +
                "  }\n" +
                "  cpt = random() % cpt;\n" +
                "  selectedReq = req->syncChannel->outWaitQueue;\n" +
                "  while (cpt > 0) {\n" +
                "    selectedReq = selectedReq->next;\n" +
                "    cpt --;\n" +
                "  }\n" +
                "\n" +
                "  //req->syncChannel->outWaitQueue = removeRequestFromList(req->syncChannel->outWaitQueue, selectedReq);\n" +
                "  debug2Msg(\"Setting related request\");\n" +
                "  req->relatedRequest = selectedReq;\n" +
                "\n" +
                "  // Select the request, and notify the information in the channel\n" +
                "  selectedReq->selected = 1;\n" +
                "  selectedReq->listOfRequests->selectedRequest = selectedReq;\n" +
                "\n" +
                "  // Handle parameters\n" +
                "  copyParameters(selectedReq, req);\n" +
                "  debug2Msg(\"Signaling\");\n" +
                "  *(selectedReq->listOfRequests->conditionVariable) = 1;\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void executeSendAsyncTransaction(request *req) {\n" +
                "  request *selectedReq;\n" +
                "\n" +
                "  // Full FIFO?\n" +
                "  if (req->asyncChannel->currentNbOfMessages == req->asyncChannel->maxNbOfMessages) {\n" +
                "    // Must remove the oldest  message\n" +
                "    getAndRemoveOldestMessageFromAsyncChannel(req->asyncChannel);\n" +
                "  }\n" +
                "\n" +
                "  addMessageToAsyncChannel(req->asyncChannel, req->msg);\n" +
                "  debug2Msg(\"Signaling async write to all requests waiting \");\n" +
                "  selectedReq = req->asyncChannel->inWaitQueue;\n" +
                "  while (selectedReq != NULL) {\n" +
                "\n" +
                "    *(selectedReq->listOfRequests->conditionVariable) = 1;\n" +
                "    selectedReq = selectedReq->next;\n" +
                "  }\n" +
                "  debug2Msg(\"Signaling done\");\n" +
                "\n" +
                "}\n" +
                "\n" +
                "void executeReceiveAsyncTransaction(request *req) {\n" +
                "  int i;\n" +
                "  request *selectedReq;\n" +
                "\n" +
                "  req->msg = getAndRemoveOldestMessageFromAsyncChannel(req->asyncChannel);\n" +
                "\n" +
                "  selectedReq = req->asyncChannel->outWaitQueue;\n" +
                "\n" +
                "  // Must recopy parameters\n" +
                "  for (i = 0; i < req->nbOfParams; i++) {\n" +
                "    *(req->params[i]) = req->msg->params[i];\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "  // unallocate message\n" +
                "  destroyMessageWithParams(req->msg);\n" +
                "  debug2Msg(\"Signaling async read to all requests waiting \");\n" +
                "  while (selectedReq != NULL) {\n" +
                "    *(selectedReq->listOfRequests->conditionVariable) = 1;\n" +
                "    selectedReq = selectedReq->next;\n" +
                "  }\n" +
                "  debug2Msg(\"Signaling done\");\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void executeSendBroadcastTransaction(request *req) {\n" +
                "  int cpt;\n" +
                "  request *tmpreq;\n" +
                "\n" +
                "  // At least one transaction available -> must select all of them\n" +
                "  // but at most one per task\n" +
                "  // Then, broadcast the new condition!\n" +
                "\n" +
                "  request* currentReq = req->syncChannel->inWaitQueue;\n" +
                "  request* currentLastReq = req;\n" +
                "  debug2Msg(\"Execute broadcast sync tr\");\n" +
                "\n" +
                "\n" +
                "  while (currentReq != NULL) {\n" +
                "    tmpreq = hasIdenticalRequestInListOfSelectedRequests(currentReq, req->relatedRequest);\n" +
                "    if (tmpreq != NULL) {\n" +
                "      // Must select one of the two\n" +
                "      // If =1, replace, otherwise, just do nothing\n" +
                "      cpt = random() % 2;\n" +
                "      if (cpt == 1) {\n" +
                "        debug2Msg(\"Replacing broadcast request\");\n" +
                "        req->relatedRequest = replaceInListOfSelectedRequests(tmpreq, currentReq, req->relatedRequest);\n" +
                "        currentReq->listOfRequests->selectedRequest = currentReq;\n" +
                "        copyParameters(req, currentReq);\n" +
                "        currentReq->selected = 1;\n" +
                "        currentLastReq = req;\n" +
                "        while (currentLastReq->relatedRequest != NULL) {\n" +
                "          currentLastReq = currentLastReq->relatedRequest;\n" +
                "        }\n" +
                "      }\n" +
                "    } else {\n" +
                "      currentLastReq->relatedRequest = currentReq;\n" +
                "      currentReq->relatedRequest = NULL;\n" +
                "      currentReq->selected = 1;\n" +
                "      currentReq->listOfRequests->selectedRequest = currentReq;\n" +
                "      copyParameters(req, currentReq);\n" +
                "      currentLastReq = currentReq;\n" +
                "    }\n" +
                "\n" +
                "    currentReq = currentReq->next;\n" +
                "\n" +
                "\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "  currentReq = req->relatedRequest;\n" +
                "  cpt = 0;\n" +
                "  while (currentReq != NULL) {\n" +
                "    cpt ++;\n" +
                "    *(currentReq->listOfRequests->conditionVariable) = 1;\n" +
                "    currentReq = currentReq->relatedRequest;\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "int executable(setOfRequests *list, int nb) {\n" +
                "  int cpt = 0;\n" +
                "  //int index = 0;\n" +
                "  request *req = list->head;\n" +
                "  timespec ts;\n" +
                "  int tsDone = 0;\n" +
                "  debug2Msg(\"Starting loop\");\n" +
                "  list->hasATimeRequest = 0;\n" +
                "\n" +
                "  while (req != NULL) {\n" +
                "    if (!(req->delayElapsed)) {\n" +
                "      if (req->hasDelay) {\n" +
                "        // Is the delay elapsed???\n" +
                "        if (tsDone == 0) {\n" +
                "          my_clock_gettime(&ts);\n" +
                "          tsDone = 1;\n" +
                "        }\n" +
                "\n" +
                "        if (isBefore(&ts, &(req->myStartTime)) == 1) {\n" +
                "          // Delay not elapsed\n" +
                "          debug2Msg(\"---------t--------> delay NOT elapsed\");\n" +
                "          if (list->hasATimeRequest == 0) {\n" +
                "            list->hasATimeRequest = 1;\n" +
                "            list->minTimeToWait.tv_nsec = req->myStartTime.tv_nsec;\n" +
                "            list->minTimeToWait.tv_sec = req->myStartTime.tv_sec;\n" +
                "          } else {\n" +
                "            minTime(&(req->myStartTime), &(list->minTimeToWait), &(list->minTimeToWait));\n" +
                "          }\n" +
                "        }  else {\n" +
                "          // Delay elapsed\n" +
                "          debug2Msg(\"---------t--------> delay elapsed\");\n" +
                "          req->delayElapsed = 1;\n" +
                "        }\n" +
                "      } else {\n" +
                "        req->delayElapsed = 1;\n" +
                "      }\n" +
                "    }\n" +
                "    req = req->nextRequestInList;\n" +
                "  }\n" +
                "\n" +
                "  req = list->head;\n" +
                "  while ((req != NULL) && (cpt < nb)) {\n" +
                "    req->executable = 0;\n" +
                "    if (req->delayElapsed) {\n" +
                "      if (req->type == SEND_SYNC_REQUEST) {\n" +
                "        debug2Msg(\"Send sync\");\n" +
                "        if (req->syncChannel->inWaitQueue != NULL) {\n" +
                "          debug2Msg(\"Send sync executable\");\n" +
                "          req->executable = 1;\n" +
                "          cpt ++;\n" +
                "        }  else {\n" +
                "          debug2Msg(\"Send sync not executable\");\n" +
                "        }\n" +
                "        //index ++;\n" +
                "      }\n" +
                "\n" +
                "      if (req->type == RECEIVE_SYNC_REQUEST) {\n" +
                "        debug2Msg(\"receive sync\");\n" +
                "        if (req->syncChannel->outWaitQueue != NULL) {\n" +
                "          req->executable = 1;\n" +
                "          cpt ++;\n" +
                "        }\n" +
                "        //index ++;\n" +
                "      }\n" +
                "\n" +
                "      if (req->type == SEND_ASYNC_REQUEST) {\n" +
                "        debug2Msg(\"Send async\");\n" +
                "        if (!(req->asyncChannel->isBlocking)) {\n" +
                "          // Can always add a message -> executable\n" +
                "          debug2Msg(\"Send async executable since non blocking\");\n" +
                "          req->executable = 1;\n" +
                "          cpt ++;\n" +
                "\n" +
                "          //blocking case ... channel full?\n" +
                "        } else {\n" +
                "          if (req->asyncChannel->currentNbOfMessages < req->asyncChannel->maxNbOfMessages) {\n" +
                "            // Not full!\n" +
                "            debug2Msg(\"Send async executable since channel not full\");\n" +
                "            req->executable = 1;\n" +
                "            cpt ++;\n" +
                "          } else {\n" +
                "            debug2Msg(\"Send async not executable: full, and channel is blocking\");\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "\n" +
                "      if (req->type == RECEIVE_ASYNC_REQUEST) {\n" +
                "        debug2Msg(\"receive async\");\n" +
                "        if (req->asyncChannel->currentNbOfMessages > 0) {\n" +
                "          debug2Msg(\"Receive async executable: not empty\");\n" +
                "          req->executable = 1;\n" +
                "          cpt ++;\n" +
                "        } else {\n" +
                "          debug2Msg(\"Receive async not executable: empty\");\n" +
                "        }\n" +
                "        //index ++;\n" +
                "      }\n" +
                "\n" +
                "\n" +
                "      if (req->type == SEND_BROADCAST_REQUEST) {\n" +
                "        debug2Msg(\"send broadcast\");\n" +
                "        req->executable = 1;\n" +
                "        cpt ++;\n" +
                "      }\n" +
                "\n" +
                "      if (req->type == RECEIVE_BROADCAST_REQUEST) {\n" +
                "        debug2Msg(\"receive broadcast\");\n" +
                "        // A receive broadcast is never executable\n" +
                "        req->executable = 0;\n" +
                "        //index ++;\n" +
                "      }\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "      if (req->type == IMMEDIATE) {\n" +
                "        debug2Msg(\"immediate\");\n" +
                "        req->executable = 1;\n" +
                "        cpt ++;\n" +
                "      }\n" +
                "    }\n" +
                "\n" +
                "    req = req->nextRequestInList;\n" +
                "\n" +
                "  }\n" +
                "\n" +
                "  return cpt;\n" +
                "}\n" +
                "\n" +
                "void private__makeRequestPending(setOfRequests *list) {\n" +
                "  request *req = list->head;\n" +
                "  while (req != NULL) {\n" +
                "    if ((req->delayElapsed) && (!(req->alreadyPending))) {\n" +
                "      if (req->type == SEND_SYNC_REQUEST) {\n" +
                "        debug2Msg(\"Adding pending request in outWaitqueue\");\n" +
                "        req->syncChannel->outWaitQueue = addToRequestQueue(req->syncChannel->outWaitQueue, req);\n" +
                "        req->alreadyPending = 1;\n" +
                "      }\n" +
                "\n" +
                "      if (req->type ==  RECEIVE_SYNC_REQUEST) {\n" +
                "        debug2Msg(\"Adding pending request in inWaitqueue\");\n" +
                "        req->alreadyPending = 1;\n" +
                "        req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);\n" +
                "      }\n" +
                "\n" +
                "      if (req->type == SEND_ASYNC_REQUEST) {\n" +
                "        debug2Msg(\"Adding pending request in outWaitqueue\");\n" +
                "        req->asyncChannel->outWaitQueue = addToRequestQueue(req->asyncChannel->outWaitQueue, req);\n" +
                "        req->alreadyPending = 1;\n" +
                "      }\n" +
                "\n" +
                "      if (req->type ==  RECEIVE_ASYNC_REQUEST) {\n" +
                "        debug2Msg(\"Adding pending request in inWaitqueue\");\n" +
                "        req->alreadyPending = 1;\n" +
                "        req->asyncChannel->inWaitQueue = addToRequestQueue(req->asyncChannel->inWaitQueue, req);\n" +
                "      }\n" +
                "\n" +
                "      if (req->type ==  RECEIVE_BROADCAST_REQUEST) {\n" +
                "        debug2Msg(\"Adding pending broadcast request in inWaitqueue\");\n" +
                "        req->alreadyPending = 1;\n" +
                "        req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);\n" +
                "      }\n" +
                "\n" +
                "      if (req->type ==  SEND_BROADCAST_REQUEST) {\n" +
                "        debug2Msg(\"Adding pending broadcast request in outWaitqueue\");\n" +
                "        req->alreadyPending = 1;\n" +
                "        req->syncChannel->outWaitQueue = addToRequestQueue(req->syncChannel->outWaitQueue, req);\n" +
                "      }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    req = req->nextRequestInList;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "void private__makeRequest(request *req) {\n" +
                "  if (req->type == SEND_SYNC_REQUEST) {\n" +
                "    executeSendSyncTransaction(req);\n" +
                "  }\n" +
                "\n" +
                "  if (req->type == RECEIVE_SYNC_REQUEST) {\n" +
                "    executeReceiveSyncTransaction(req);\n" +
                "  }\n" +
                "\n" +
                "  if (req->type == SEND_ASYNC_REQUEST) {\n" +
                "    executeSendAsyncTransaction(req);\n" +
                "  }\n" +
                "\n" +
                "  if (req->type == RECEIVE_ASYNC_REQUEST) {\n" +
                "    executeReceiveAsyncTransaction(req);\n" +
                "  }\n" +
                "\n" +
                "  if (req->type == SEND_BROADCAST_REQUEST) {\n" +
                "    executeSendBroadcastTransaction(req);\n" +
                "  }\n" +
                "  // IMMEDIATE: Nothing to do\n" +
                "\n" +
                "  // In all cases: remove other requests of the same list from their pending form1\n" +
                "  debug2Msg(\"Removing original req\");\n" +
                "  removeAllPendingRequestsFromPendingLists(req, 1);\n" +
                "  removeAllPendingRequestsFromPendingListsRelatedRequests(req);\n" +
                "  if (req->relatedRequest != NULL) {\n" +
                "    debug2Msg(\"Removing related req\");\n" +
                "    removeAllPendingRequestsFromPendingLists(req->relatedRequest, 0);\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "void removeAllPendingRequestsFromPendingListsRelatedRequests(request *req) {\n" +
                "  if (req->relatedRequest != NULL) {\n" +
                "    debug2Msg(\"Removing related req\");\n" +
                "    removeAllPendingRequestsFromPendingLists(req->relatedRequest, 0);\n" +
                "    // Recursive call\n" +
                "    removeAllPendingRequestsFromPendingListsRelatedRequests(req->relatedRequest);\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "request *private__executeRequests0(setOfRequests *list, int nb) {\n" +
                "  int howMany, found;\n" +
                "  int selectedIndex, realIndex;\n" +
                "  request *selectedReq;\n" +
                "  request *req;\n" +
                "\n" +
                "  // Compute which requests can be executed\n" +
                "  debug2Msg(\"Counting requests\");\n" +
                "  howMany = executable(list, nb);\n" +
                "\n" +
                "\n" +
                "\n" +
                "  if (howMany == 0) {\n" +
                "    debug2Msg(\"No pending requests\");\n" +
                "    // Must make them pending\n" +
                "\n" +
                "    private__makeRequestPending(list);\n" +
                "\n" +
                "    return NULL;\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "\n" +
                "  // Select a request\n" +
                "  req = list->head;\n" +
                "  selectedIndex = (rand() % howMany) + 1;\n" +
                "\n" +
                "  realIndex = 0;\n" +
                "  found = 0;\n" +
                "  while (req != NULL) {\n" +
                "    if (req->executable == 1) {\n" +
                "      found ++;\n" +
                "      if (found == selectedIndex) {\n" +
                "        break;\n" +
                "      }\n" +
                "    }\n" +
                "    realIndex ++;\n" +
                "    req = req->nextRequestInList;\n" +
                "  }\n" +
                "\n" +
                "  selectedReq = getRequestAtIndex(list, realIndex);\n" +
                "  selectedReq->selected = 1;\n" +
                "  selectedReq->listOfRequests->selectedRequest = selectedReq;\n" +
                "\n" +
                "  // Execute that request\n" +
                "  private__makeRequest(selectedReq);\n" +
                "\n" +
                "  return selectedReq;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "request *private__executeRequests(setOfRequests *list) {\n" +
                "  // Is a request already selected?\n" +
                "\n" +
                "  if (list->selectedRequest != NULL) {\n" +
                "    return list->selectedRequest;\n" +
                "  }\n" +
                "  debug2Msg(\"No request selected -> looking for one!\");\n" +
                "  return private__executeRequests0(list, nbOfRequests(list));\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "request *executeOneRequest(setOfRequests *list, request *req) {\n" +
                "  req->nextRequestInList = NULL;\n" +
                "  req->listOfRequests = list;\n" +
                "  list->head = req;\n" +
                "  return executeListOfRequests(list);\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void setLocalStartTime(setOfRequests *list) {\n" +
                "\n" +
                "  request *req = list->head;\n" +
                "\n" +
                "  while (req != NULL) {\n" +
                "    if (req->hasDelay) {\n" +
                "      req->delayElapsed = 0;\n" +
                "      addTime(&(list->startTime), &(req->delay), &(req->myStartTime));\n" +
                "      debug2Msg(\" -----t------>: Request with delay\");\n" +
                "    } else {\n" +
                "      debug2Msg(\" -----t------>: Request without delay\");\n" +
                "      req->delayElapsed = 1;\n" +
                "      req->myStartTime.tv_nsec = list->startTime.tv_nsec;\n" +
                "      req->myStartTime.tv_sec = list->startTime.tv_sec;\n" +
                "    }\n" +
                "    req = req->nextRequestInList;\n" +
                "    list->mutex->unlock();\n" +
                "    list->mutex->lock();\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// Return the executed request\n" +
                "struct request *executeListOfRequests(struct setOfRequests *list) {\n" +
                "  request *req;\n" +
                "\n" +
                "  my_clock_gettime(&list->startTime);\n" +
                "  list->selectedRequest = NULL;\n" +
                "  setLocalStartTime(list);\n" +
                "\n" +
                "  // Try to find a request that could be executed\n" +
                "  debug2Msg(\"Locking mutex\");\n" +
                "  list->mutex->unlock();\n" +
                "  list->mutex->lock();\n" +
                "  debug2Msg(\"Mutex locked\");\n" +
                "  debug2Msg(\"Going to execute request\");\n" +
                "  while ((req = private__executeRequests(list)) == NULL) {\n" +
                "    debug2Msg(\"Waiting for request!\");\n" +
                "    if (list->hasATimeRequest == 1) {\n" +
                "      debug2Msg(\"Waiting for a request and at most for a given time\");\n" +
                "      list->mutex->unlock();\n" +
                "      waitFor(list->minTimeToWait.tv_sec * 1000, list->minTimeToWait.tv_sec * 1000);\n" +
                "      *(list->conditionVariable) = 0 ;\n" +
                "      while (*(list->conditionVariable) != 1) {\n" +
                "        vTaskDelay( portTICK_PERIOD_MS );\n" +
                "      }\n" +
                "\n" +
                "      list->mutex->lock();\n" +
                "\n" +
                "    } else {\n" +
                "      debug2Msg(\"Releasing mutex\");\n" +
                "      list->mutex->unlock();\n" +
                "      *(list->conditionVariable) = 0 ;\n" +
                "      while (*(list->conditionVariable) != 1) {\n" +
                "        vTaskDelay( portTICK_PERIOD_MS );\n" +
                "      }\n" +
                "\n" +
                "      list->mutex->lock();\n" +
                "    }\n" +
                "    debug2Msg(\"Waking up for requests! -> getting mutex\");\n" +
                "  }\n" +
                "  debug2Msg(\"Request selected!\");\n" +
                "  my_clock_gettime(&list->completionTime);\n" +
                "  list->mutex->unlock();\n" +
                "  debug2Msg(\"Mutex unlocked\");\n" +
                "  return req;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "void debug2Msg(char* val ) {\n" +
                "  if (debug == DEBUG_ON) {\n" +
                "    Serial.println(val) ;\n" +
                "  }\n" +
                "}";

        stucturesCode="#define SEND_SYNC_REQUEST 0\n" +
                "#define RECEIVE_SYNC_REQUEST 2\n" +
                "#define SEND_ASYNC_REQUEST 4\n" +
                "#define RECEIVE_ASYNC_REQUEST 6\n" +
                "#define DELAY 8\n" +
                "#define IMMEDIATE 10\n" +
                "#define SEND_BROADCAST_REQUEST 12\n" +
                "#define RECEIVE_BROADCAST_REQUEST 14\n" +
                "\n" +
                "#define DEBUG_ON 1\n" +
                "#define DEBUG_OFF 2\n" +
                "\n" +
                "int debug = DEBUG_ON;\n" +
                "\n" +
                "struct timespec\n" +
                "{\n" +
                "  int tv_sec;    /* Seconds.  */\n" +
                "  int tv_nsec;  /* Nanoseconds.  */\n" +
                "};\n" +
                "\n" +
                "struct message {\n" +
                "  struct message *next;\n" +
                "  int nbOfParams;\n" +
                "  int *params;\n" +
                "  long id;\n" +
                "};\n" +
                "\n" +
                "long __id_message = 0;\n" +
                "frt::Mutex __message_mutex;\n" +
                "frt::Mutex mutex ;\n" +
                "int mainConditionaVariable = 0;\n" +
                "struct request;\n" +
                "\n" +
                "struct setOfRequests {\n" +
                "  char* owner;\n" +
                "  struct request *head;\n" +
                "  timespec startTime;\n" +
                "  timespec completionTime;\n" +
                "  int *conditionVariable;\n" +
                "  frt::Mutex *mutex;\n" +
                "\n" +
                "  int hasATimeRequest; // Means that at least on request of the list hasn't completed yet its time delay\n" +
                "  timespec minTimeToWait;\n" +
                "  struct request *selectedRequest;\n" +
                "};\n" +
                "\n" +
                "struct request {\n" +
                "  struct request *next;\n" +
                "  struct setOfRequests* listOfRequests;\n" +
                "  struct request* nextRequestInList;\n" +
                "  struct request* relatedRequest; // For synchro and broadcast\n" +
                "  struct syncchannel *syncChannel;\n" +
                "  struct asyncchannel *asyncChannel;\n" +
                "\n" +
                "  int type;\n" +
                "  int ID;\n" +
                "  int hasDelay;;\n" +
                "  timespec delay;\n" +
                "  int nbOfParams; // synchronous com.\n" +
                "  int **params;  // synchronous com.\n" +
                "  message *msg; // Asynchronous comm.\n" +
                "\n" +
                "\n" +
                "  // Filled by the request manager\n" +
                "  int executable;\n" +
                "  int selected;\n" +
                "  int alreadyPending; // Whether it has been taken into account for execution or not\n" +
                "  int delayElapsed;\n" +
                "  timespec myStartTime; // Time at which the delay has expired\n" +
                "};\n" +
                "\n" +
                "struct asyncchannel {\n" +
                "  char *outname;\n" +
                "  char *inname;\n" +
                "  int isBlocking; // In writing. Reading is always blocking\n" +
                "  int maxNbOfMessages; //\n" +
                "  struct request* outWaitQueue;\n" +
                "  struct request* inWaitQueue;\n" +
                "  message *pendingMessages;\n" +
                "  int currentNbOfMessages;\n" +
                "};\n" +
                "\n" +
                "struct syncchannel {\n" +
                "  char *outname;\n" +
                "  char *inname;\n" +
                "  struct request* inWaitQueue;\n" +
                "  struct request* outWaitQueue;\n" +
                "  bool isBroadcast;\n" +
                "};";

    }

    public String getName() {
        return name;
    }

    public void appendToSetupCode(String _code) {
        setupCode += _code;
    }

    public void appendToBeforeSetupCode(String _code) {
        beforeSetupCode += _code;
    }

    public void appendToLoopCode(String _code) {
        loopCode += _code;
    }

    public void appendToAfterLoopCode(String _code) {afterLoopCode += _code; }

    public String getBeforeSetupCode() { return INCLUDE_HEADER+CR+LOCAL_INCLUDE_HEADER+CR+stucturesCode+CR+beforeSetupCode+CR ; }

    public String getSetupCode() {
        return SETUP_CODE + setupCode + CR + "}"+CR;
    }

    public String getLoopCode() { return LOOP_CODE + loopCode + CR + "}"+CR; }

    public String getAfterLoopCode() { return afterLoopCode; }

    public String getFunctionsCode() { return functionsCode; }

    public String getStucturesCode() {
        return stucturesCode;
    }

    public String getAllCode() {return getBeforeSetupCode()+CR+getSetupCode()+CR+getLoopCode()+CR+getAfterLoopCode()+CR+getFunctionsCode();}
}
