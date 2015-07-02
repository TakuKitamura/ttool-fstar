#include <stdlib.h>
#include <pthread.h>
#include <time.h>

#include "request_manager.h"
#include "request.h"
#include "myerrors.h"
#include "debug.h"
#include "mytimelib.h"
#include "random.h"
#include "asyncchannel.h"
#include "tracemanager.h"



void executeSendSyncTransaction(request *req) {
  int cpt;
  request *selectedReq;

  // At least one transaction available -> must select one randomly
  // First: count how many of them are available
  // Then, select one
  // Broadcast the new condition!

  cpt = 0;
  request* currentReq = req->syncChannel->inWaitQueue;
  debugMsg("Execute send sync tr");

  while(currentReq != NULL) {
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
  debugMsg("Setting related request");
  req->relatedRequest = selectedReq;

  // Select the selected request, and notify the information
  selectedReq->selected = 1;
  selectedReq->listOfRequests->selectedRequest = selectedReq;

  // Handle parameters
  copyParameters(req, selectedReq);

  debugMsg("Signaling");
  pthread_cond_signal(selectedReq->listOfRequests->wakeupCondition);

  traceSynchroRequest(req, selectedReq);
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
  debugMsg("Execute receive sync tr");

  while(currentReq != NULL) {
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
  debugMsg("Setting related request");
  req->relatedRequest = selectedReq;

  // Select the request, and notify the information in the channel
  selectedReq->selected = 1;
  selectedReq->listOfRequests->selectedRequest = selectedReq;

  // Handle parameters
  copyParameters(selectedReq, req);

  debugMsg("Signaling");
  pthread_cond_signal(selectedReq->listOfRequests->wakeupCondition);

  traceSynchroRequest(selectedReq, req);
}


void executeSendAsyncTransaction(request *req) {
  request *selectedReq;

  // Full FIFO?
  if (req->asyncChannel->currentNbOfMessages == req->asyncChannel->maxNbOfMessages) {
    // Must remove the oldest  message
    getAndRemoveOldestMessageFromAsyncChannel(req->asyncChannel);
  }

  addMessageToAsyncChannel(req->asyncChannel, req->msg);
  
  debugMsg("Signaling async write to all requests waiting ");
  selectedReq = req->asyncChannel->inWaitQueue;
  while (selectedReq != NULL) {
    pthread_cond_signal(selectedReq->listOfRequests->wakeupCondition);
    selectedReq = selectedReq->next;
  }
  debugMsg("Signaling done");

  traceAsynchronousSendRequest(req);
}

void executeReceiveAsyncTransaction(request *req) {
  int i;
  request *selectedReq;

  req->msg = getAndRemoveOldestMessageFromAsyncChannel(req->asyncChannel);
    
  selectedReq = req->asyncChannel->outWaitQueue;

  // Must recopy parameters
  for(i=0; i<req->nbOfParams; i++) {
    *(req->params[i]) = req->msg->params[i];
  }

  traceAsynchronousReceiveRequest(req);

  // unallocate message
  destroyMessageWithParams(req->msg);

  debugMsg("Signaling async read to all requests waiting ");
  while (selectedReq != NULL) {
    pthread_cond_signal(selectedReq->listOfRequests->wakeupCondition);
    selectedReq = selectedReq->next;
  }
  debugMsg("Signaling done");
}


void executeSendBroadcastTransaction(request *req) {
  int cpt;
  request *tmpreq;

  // At least one transaction available -> must select all of them
  // but at most one per task
  // Then, broadcast the new condition!

  request* currentReq = req->syncChannel->inWaitQueue;
  request* currentLastReq = req;
  debugMsg("Execute broadcast sync tr");

  
  while(currentReq != NULL) {
    tmpreq = hasIdenticalRequestInListOfSelectedRequests(currentReq, req->relatedRequest);
    if (tmpreq != NULL) {
      // Must select one of the two
      // If =1, replace, otherwise, just do nothing
      cpt = random() % 2;
      if (cpt == 1) {
	debugMsg("Replacing broadcast request");
	req->relatedRequest = replaceInListOfSelectedRequests(tmpreq, currentReq, req->relatedRequest);
	currentReq->listOfRequests->selectedRequest = currentReq;
	copyParameters(req, currentReq);
	currentReq->selected = 1;
	currentLastReq = req;
	while(currentLastReq->relatedRequest != NULL) {
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
    
    debugInt("Nb of requests selected:", nbOfRelatedRequests(req));
  }


  debugMsg("Signaling");
  currentReq = req->relatedRequest;
  cpt = 0;
  while(currentReq != NULL) {
    cpt ++;
    pthread_cond_signal(currentReq->listOfRequests->wakeupCondition);
    traceSynchroRequest(req, currentReq);
    currentReq = currentReq->relatedRequest;
  }

  debugInt("NUMBER of broadcast Requests", cpt);
}


int executable(setOfRequests *list, int nb) {
  int cpt = 0;
  //int index = 0;
  request *req = list->head;
  timespec ts;
  int tsDone = 0;

  debugMsg("Starting loop");

  list->hasATimeRequest = 0;

  while(req != NULL) {
    if (!(req->delayElapsed)) {
      if (req->hasDelay) {
	// Is the delay elapsed???
	debugTime("begin time of list of request", &list->startTime);
	debugTime("start time of this request", &req->myStartTime);
	if (tsDone == 0) {
	  my_clock_gettime(&ts);
	  debugTime("Current time", &ts);
	  tsDone = 1;
	}

	if (isBefore(&ts, &(req->myStartTime)) == 1) {
	  // Delay not elapsed
	  debugMsg("---------t--------> delay NOT elapsed");
	  if (list->hasATimeRequest == 0) {
	    list->hasATimeRequest = 1;
	    list->minTimeToWait.tv_nsec = req->myStartTime.tv_nsec;
	    list->minTimeToWait.tv_sec = req->myStartTime.tv_sec;
	  } else {
	    minTime(&(req->myStartTime), &(list->minTimeToWait),&(list->minTimeToWait));
	  }
	}  else {
	  // Delay elapsed
	  debugMsg("---------t--------> delay elapsed");
	  req->delayElapsed = 1;
	}
      } else {
	req->delayElapsed = 1;
      }
    }
    req = req->nextRequestInList;
  }
  
  req = list->head;
  while((req != NULL) && (cpt < nb)) {
    req->executable = 0;
    if (req->delayElapsed) {
      if (req->type == SEND_SYNC_REQUEST) {
	debugMsg("Send sync");

	if (req->syncChannel->inWaitQueue != NULL) {
	  debugMsg("Send sync executable");
	  req->executable = 1;
	  cpt ++;
	}  else {
	  debugMsg("Send sync not executable");
	}
	//index ++;
      }

      if (req->type == RECEIVE_SYNC_REQUEST) {
	debugMsg("receive sync");
	if (req->syncChannel->outWaitQueue != NULL) {
	  req->executable = 1;
	  cpt ++;
	}
	//index ++;
      }

      if (req->type == SEND_ASYNC_REQUEST) {
	debugMsg("Send async");

	if (!(req->asyncChannel->isBlocking)) {
	  // Can always add a message -> executable
	  debugMsg("Send async executable since non blocking");
	  req->executable = 1;
	  cpt ++;

	  //blocking case ... channel full?
	} else {
	  if (req->asyncChannel->currentNbOfMessages < req->asyncChannel->maxNbOfMessages) {
	    // Not full!
	    debugMsg("Send async executable since channel not full");
	    req->executable = 1;
	    cpt ++;
	  } else {
	    debugMsg("Send async not executable: full, and channel is blocking");
	  }
	}
      }

      if (req->type == RECEIVE_ASYNC_REQUEST) {
	debugMsg("receive async");
	if (req->asyncChannel->currentNbOfMessages >0) {
	  debugMsg("Receive async executable: not empty");
	  req->executable = 1;
	  cpt ++;
	} else {
	  debugMsg("Receive async not executable: empty");
	}
	//index ++;
      }
      

      if (req->type == SEND_BROADCAST_REQUEST) {
	debugMsg("send broadcast");
	req->executable = 1;
	cpt ++;
      }

      if (req->type == RECEIVE_BROADCAST_REQUEST) {
	debugMsg("receive broadcast");
	// A receive broadcast is never executable
	req->executable = 0;
	//index ++;
      }

      
      

      if (req->type == IMMEDIATE) {
	debugMsg("immediate");
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
  while(req != NULL) {
    if ((req->delayElapsed) && (!(req->alreadyPending))) {
      if (req->type == SEND_SYNC_REQUEST) {
	debugMsg("Adding pending request in outWaitqueue");
	req->syncChannel->outWaitQueue = addToRequestQueue(req->syncChannel->outWaitQueue, req);
	req->alreadyPending = 1;
      }

      if (req->type ==  RECEIVE_SYNC_REQUEST) {
	debugMsg("Adding pending request in inWaitqueue");
	req->alreadyPending = 1;
	req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);
      }

      if (req->type == SEND_ASYNC_REQUEST) {
	debugMsg("Adding pending request in outWaitqueue");
	req->asyncChannel->outWaitQueue = addToRequestQueue(req->asyncChannel->outWaitQueue, req);
	req->alreadyPending = 1;
      }

      if (req->type ==  RECEIVE_ASYNC_REQUEST) {
	debugMsg("Adding pending request in inWaitqueue");
	req->alreadyPending = 1;
	req->asyncChannel->inWaitQueue = addToRequestQueue(req->asyncChannel->inWaitQueue, req);
      }

      if (req->type ==  RECEIVE_BROADCAST_REQUEST) {
	debugMsg("Adding pending broadcast request in inWaitqueue");
	req->alreadyPending = 1;
	req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);
      }

      if (req->type ==  SEND_BROADCAST_REQUEST) {
	debugMsg("Adding pending broadcast request in outWaitqueue");
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
  
  // In all cases: remove other requests of the same list from their pending form
  debugMsg("Removing original req");
  removeAllPendingRequestsFromPendingLists(req, 1);
  removeAllPendingRequestsFromPendingListsRelatedRequests(req);
  /*if (req->relatedRequest != NULL) {
    debugMsg("Removing related req");
    removeAllPendingRequestsFromPendingLists(req->relatedRequest, 0);
    }*/
  
}

void removeAllPendingRequestsFromPendingListsRelatedRequests(request *req) {
  if (req->relatedRequest != NULL) {
    debugMsg("Removing related req");
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
  debugMsg("Counting requests");
  howMany = executable(list, nb);

  debugInt("Counting requests=", howMany);

  if (howMany == 0) {
    debugMsg("No pending requests");
    // Must make them pending
    
    private__makeRequestPending(list);

    return NULL;
  }
  
  debugInt("At least one pending request is executable", howMany);

  
  // Select a request
  req = list->head;
  selectedIndex = (rand() % howMany)+1;
  debugInt("selectedIndex=", selectedIndex);
  realIndex = 0;
  found = 0;
  while(req != NULL) {
    if (req->executable == 1) {
      found ++;
      if (found == selectedIndex) {
	break;
      }
    }
    realIndex ++;
    req = req->nextRequestInList;
  }

  debugInt("Getting request at index", realIndex);
  selectedReq = getRequestAtIndex(list, realIndex);
  selectedReq->selected = 1;
  selectedReq->listOfRequests->selectedRequest = selectedReq;

  debugInt("Selected request of type", selectedReq->type);

  // Execute that request
  private__makeRequest(selectedReq);

  return selectedReq;  
}


request *private__executeRequests(setOfRequests *list) {
  // Is a request already selected?

  if (list->selectedRequest != NULL) {
    return list->selectedRequest;
  }

  debugMsg("No request selected -> looking for one!");

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

  while(req != NULL) {
    if (req->hasDelay) {
      req->delayElapsed = 0;
      addTime(&(list->startTime), &(req->delay), &(req->myStartTime));
      debug2Msg(list->owner, " -----t------>: Request with delay");
    } else {
      req->delayElapsed = 1;
      req->myStartTime.tv_nsec = list->startTime.tv_nsec;
      req->myStartTime.tv_sec = list->startTime.tv_sec;
    }
    req = req->nextRequestInList;
  }
}


// Return the executed request
request *executeListOfRequests(setOfRequests *list) {
  request *req;

  my_clock_gettime(&list->startTime);
  list->selectedRequest = NULL;
  setLocalStartTime(list);
  
  // Try to find a request that could be executed
  debug2Msg(list->owner, "Locking mutex");
  pthread_mutex_lock(list->mutex);
  debug2Msg(list->owner, "Mutex locked");

  debug2Msg(list->owner, "Going to execute request");

  while((req = private__executeRequests(list)) == NULL) {
    debug2Msg(list->owner, "Waiting for request!");
    if (list->hasATimeRequest == 1) {
      debug2Msg(list->owner, "Waiting for a request and at most for a given time");
      debugTime("Min time to wait=", &(list->minTimeToWait));
      pthread_cond_timedwait(list->wakeupCondition, list->mutex, &(list->minTimeToWait));
    } else {
      debug2Msg(list->owner, "Releasing mutex");
      pthread_cond_wait(list->wakeupCondition, list->mutex);
    }
    debug2Msg(list->owner, "Waking up for requests! -> getting mutex");
  }

  debug2Msg(list->owner, "Request selected!");

  my_clock_gettime(&list->completionTime);

  pthread_mutex_unlock(list->mutex); 
  debug2Msg(list->owner, "Mutex unlocked");
  return req;
}

