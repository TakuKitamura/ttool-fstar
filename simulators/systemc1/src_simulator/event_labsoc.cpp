/**Copyright GET / ENST / Ludovic Apvrille

ludovic.apvrille at enst.fr

This software is a computer program whose purpose is to edit TURTLE
diagrams, generate RT-LOTOS code from these TURTLE diagrams, and at
last to analyse results provided from externalm formal validation tools.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.*/

#include "systemc.h"
#include "math.h"
#include "parameters.h"

#include "node_labsoc.h"
#include "event_labsoc.h"

void TMLEvent::addEvent(Event *event) {
  EventCell *cell = new EventCell();
  cell->next = NULL;
  cell->event = event;

  //cout<<"adding event\n";
  currentNbOfEvents ++;
  //cout<<"Nb of events="<<currentNbOfEvents<<"\n";

  // No element in list
  if (tail == NULL) {
    head = cell;
    tail = cell;
  } else {
    tail->next = cell;
    tail = cell;
  }
}

Event *TMLEvent::getFirstEvent() {
  return head->event;
}

void TMLEvent::removeFirstEvent() {
  EventCell *tmp = head;

  if (head == NULL) {
    return;
  }

  currentNbOfEvents --;
  if (currentNbOfEvents == 0) {
    tail = NULL;
  }

  head = head->next;
  free(tmp);
  return;
}


// Cristian Macario 22/11/07
// Type of this method becomes int
int TMLEvent::notify(Event *event) {
  //cout<<"step01\n";
  
  //mutex.lock();
  
  if(notifyingNode != waitingNode) {
    notifyIf->notifyEvtOnBus(nParam, this);
  }
  
  mutex.lock();
  addEvent(event);

  //cout<<"step03\n";
  //cout<<"NbOfEvents="<<currentNbOfEvents<<"\n";
 
 
  // Cristian Macario 21/11/07
  // Default event: Infinite Fifo
  // We don't have to check this
  // morever events are stored in a list, can't overflow
  /*
  if (currentNbOfEvents > maxNbOfEvents) {
    event = getFirstEvent();
    removeFirstEvent();
    free(event);
    //cout<<"Freeing events="<<currentNbOfEvents<<"\n";
  }
  */
  
  if (blockedWaitTask != NULL) {
    //cout<<"Unblocking a blocked task (event)\n";
    waitingNode->setUnblockedTask(blockedWaitTask);
    blockedWaitTask = NULL;
  }

  mutex.unlock();
  
  // Cristian Macario 22/11/07
  return 1;
}



Event *TMLEvent::wait() {
  Event *evt;
  //mutex.lock();

  //cout<<"wait evt step 01\n";

  if (currentNbOfEvents == 0) {
    // Block current task
    //mutex.unlock();
    //cout<<"returning null\n";
    return NULL;
  }
  
  // Cristian Macario 04/02/08
  // event between 2 cpus support
  if(notifyingNode != waitingNode) {
      ::sc_core::wait(1);
  }
  
  
  //cout<<"wait evt step 02\n";
  mutex.lock();
  evt = getFirstEvent();
  removeFirstEvent();

  //cout<<"wait evt step 02\n";
  
  // Cristian Macario 27/11/07
  // if there is a blocked notifying task
  // it has to be unblocked
  if(blockedNotifyTask != NULL) {
    notifyingNode->setUnblockedTask(blockedNotifyTask);
    blockedNotifyTask = NULL;
  }


  mutex.unlock();
  return evt;
}

int TMLEvent::notified() {
  return currentNbOfEvents;
}


// Cristian Macario 22/11/07
// Type of this method becomes int
int FiniteFIFO_Event::notify(Event *event) {
  //mutex.lock();
  
  if(notifyingNode != waitingNode) {
    notifyIf->notifyEvtOnBus(nParam, this);
  }
  
  mutex.lock();
  addEvent(event);
  

  // erase first evt if too many evts
  if (currentNbOfEvents > maxNbOfEvents) {
    mutex.lock();
    event = getFirstEvent();
    removeFirstEvent();
    free(event);
    mutex.unlock();
  }
  
  if (blockedWaitTask != NULL) {
    //cout<<"Unblocking a blocked task (evt)\n";
    waitingNode->setUnblockedTask(blockedWaitTask);
    blockedWaitTask = NULL;
  }

  mutex.unlock();
  
  // Cristian Macario 22/11/07
  return 1;
}


// Cristian Macario 22/11/07
// Type of this method becomes int
int FiniteBlockingFIFO_Event::notify(Event *event) {
  //mutex.lock();

  //cout<<"notify evt step 01\n";

  if (currentNbOfEvents == maxNbOfEvents) {
    // Block current task
    //mutex.unlock();
    
    // Cristian Macario 22/11/07
    //return;
    return 0;
  }
  
  //cout<<"notify evt step 02\n";
  if(notifyingNode != waitingNode) {
    notifyIf->notifyEvtOnBus(nParam, this);
  }

  mutex.lock();
  addEvent(event);
  
  if (blockedWaitTask != NULL) {
    //cout<<"Unblocking a blocked task (evt)\n";
    waitingNode->setUnblockedTask(blockedWaitTask);
    blockedWaitTask = NULL;
  }

  mutex.unlock();
  
  //Cristian Macario 22/11/07
  return 1;
}



