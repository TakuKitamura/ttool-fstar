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
#include "task_labsoc.h"
#include "channel_labsoc.h"
#include "utils_labsoc.h"


void Task::initialize() {
  initWrite = 0;
  initRead = 0;
}

int Task::run() {
  cout<<"Task is running -> bad function has been called!\n";
  return 0;
}

void Task::setNode(Node *_node) {
  node = _node;
}


int Task::EXECI(int nbOfIntOp) {
  int nbOp = node->applyEXECIPolicy(nbOfIntOp);
  node->setCurrentTaskSignal(&execi);
  return nbOp;
}

void Task::END_EXECI() {
	node->unsetCurrentTaskSignal(&execi);
}

void Task::TERMINATE(){
  node->setTerminatedTask(this);
  runnable = 0;
  running = 0;
  terminated = 1;
  runnable = 0;
}

void Task::BLOCK(){
  //cout<<"blocking\n";
  node->setBlockedTask(this);
  running = 0;
  blocked = 1;
  runnable = 0;
  terminated = 0;
  //cout<<"end_blocking\n";
}

void Task::UNBLOCK(){
  //node->setUnblockedTask(this);
  
  // Cristian Macario 22/11/07
  // When a task is blocked by many events and
  // then it is unblocked by one of them,
  // we call the unblockWaitTask() method of the
  // the other blocking events.
  for(int i = 0; i < nbOfReceivedEvents; i++) {
    if(receivedEvents[i]->getBlockedWaitTask() == this) {
      receivedEvents[i]->unblockWaitTask();
    }
  }
  running = 0;
  blocked = 0;
  runnable = 1;
  terminated = 0;
}

int Task::WRITE(TMLChannel *ch, int nbOfSamples, sc_signal<bool> *wr_sig, int endFunction) {
  // Write initialization
  //cout<<"write0\n";
  //currentSig = wr_sig;
  if (initWrite == 0) {
     initWrite = 1;
     //cout<<"write01\n";
     nbToWrite = ch->getNbToWrite(nbOfSamples);
     //cout<<"write02\n";
  }

  //cout<<"write1\n";
  
  // Test if write has to continue
  if (nbToWrite == 0) {
    currentFunction  = endFunction;
    node->unsetCurrentTaskSignal(wr_sig);
    initWrite = 0;
    return run();
  }

  //cout<<"write2\n";
  
  // Cristian Macario 08/02/08
  // When communication between 2 different CPUs this function returns 0
  // so the branch miss rate must be emulated here 
  if(ch->getReadingNode() != ch->getWritingNode()) {
    if(Utils::myrand(0, 99) < node->getBranchingPredictionMissRate()) {
      node->branchMiss();
    }
  }


  // Write is not over: calculate the number of samples that can be written
  
  // Cristian Macario 13/12/07
  // adding bus
  // added set and reset signal
  // because now during the write operation there can be wait()
  *wr_sig = 1;
  nbWritten = ch->write(nbToWrite);
  *wr_sig = 0;
  
  
  if (nbWritten == 0) {
  
    // Task must block
    ch->setBlockedWriteTask(this);
    //cout<<"task is blocked: cannot write";
    BLOCK();
    *wr_sig = 0;
    return 0;
  }

  //cout<<"write3\n";
  // A one-cycle Write operation is performed
  nbToWrite = nbToWrite - nbWritten;
  node->setCurrentTaskSignal(wr_sig);
  //cout<<"Nb written="<<nbWritten<<" nb to write="<<nbToWrite<<"\n";
  
  
  // Cristian Macario 06/01/08
  // adding bus
  // When channel between 2 CPUs the wait() is done on the interface
  if(ch->getReadingNode() != ch->getWritingNode()) {
    return 0;
  }   
  return 1;
}


// Cristian Macario 21/11/07
// The following instruction were already executed during
// last time WRITE() was called
void Task::END_WRITE(sc_signal<bool> *wr_sig) {
//  node->unsetCurrentTaskSignal(wr_sig);
//  initWrite = 0;
}

int Task::READ(TMLChannel *ch, int nbOfSamples, sc_signal<bool> *rd_sig, int endFunction) {
  // Read initialization
  if (initRead == 0) {
     initRead = 1;
     nbToRead = ch->getNbToRead(nbOfSamples);
  }
  
  // Test if read has to continue
  if (nbToRead == 0) {
    //cout<<"Well, nothing to be read";
    currentFunction  = endFunction;
    node->unsetCurrentTaskSignal(rd_sig);
    initRead = 0;
    return run();
  }
  
  
  // Cristian Macario 08/02/08
  // When communication between 2 different CPUs this function returns 0
  // so the branch miss rate must be emulated here 
  if(ch->getReadingNode() != ch->getWritingNode()) {
    if(Utils::myrand(0, 99) < node->getBranchingPredictionMissRate()) {
      node->branchMiss();
    }
  }

  // Read is not over: calculate the number of samples that can be read
  
  // Cristian Macario 13/12/07
  // adding bus
  // added set and reset signal
  // because now during the read operation there can be wait()
  *rd_sig = 1;
  nbRead = ch->read(nbToRead);
  *rd_sig = 0;
  
  if (nbRead == 0) {
    // Task must block
    ch->setBlockedReadTask(this);
    //cout<<"task is blocked: cannot read";
    BLOCK();
    *rd_sig = 0;
    return 0;
  }

  // A one-cycle Write operation is performed
  nbToRead = nbToRead - nbRead;
  node->setCurrentTaskSignal(rd_sig);
  //cout<<"Nb read="<<nbRead<<" nb to read="<<nbToRead<<"\n";
  
  // Cristian Macario 14/01/08
  // adding bus
  // When channel between 2 CPUs the wait() is done on the interface
  if(ch->getReadingNode() != ch->getWritingNode()) {
    return 0;
  } 

  return 1;
}


// Cristian Macario 21/11/07
// The following instruction were already executed during
// last time WRITE() was called
void Task::END_READ(sc_signal<bool> *rd_sig) {
//  node->unsetCurrentTaskSignal(rd_sig);
//  initRead = 0;
}


int Task::WAIT_EVENT(TMLEvent *evt, int *param0, int *param1, int *param2, sc_signal<bool> *wait_sig, int endFunction) {
    Event *event;
    
    
  // Cristian Macario 08/02/08
  // When communication between 2 different CPUs this function returns 0
  // so the branch miss rate must be emulated here 
  if(evt->getNotifyingNode() != evt->getWaitingNode()) {
    if(Utils::myrand(0, 99) < node->getBranchingPredictionMissRate()) {
      node->branchMiss();
    }
  }
     
     *wait_sig = 1;
     event = evt->wait();
     *wait_sig = 0;

    if (event == NULL) {
      //cout<<"null event 0\n";
      node->unsetCurrentTaskSignal(wait_sig);
      //cout<<"null event 1\n";
      evt->setBlockedWaitTask(this);
      //cout<<"null event 2\n";
      //cout<<"task is blocked: cannot get event (wait op)\n";
      BLOCK();
      //cout<<"null event 3\n";
      return 0;
    }

    *param0 = event->param0;
    *param1 = event->param1;
    *param2 = event->param2;
    free(event);
    node->setCurrentTaskSignal(wait_sig);
    currentFunction = endFunction;
    
  // Cristian Macario 04/02/08
  // adding bus
  // When event between 2 CPUs the wait() is done on the interface
  if(evt->getNotifyingNode() != evt->getWaitingNode()) {
    return 0;
  } 

    return 1;
}

void Task::END_WAIT(sc_signal<bool> *wait_sig) {
	node->unsetCurrentTaskSignal(wait_sig);
}


// Critian Macario 27/11/07
// modified the definition of the function:
// we need end function as an argument
int Task::NOTIFY_EVENT(TMLEvent *evt, int param0, int param1, int param2, sc_signal<bool> *notify_sig, int endFunction) {
  //cout<<"Notify Event!\n";
    Event *event = new Event();
    event->param0 = param0;
    event->param1 = param1;
    event->param2 = param2;
    //cout<<"Calling notify()\n";
    
    
    
  // Cristian Macario 08/02/08
  // When communication between 2 different CPUs this function returns 0
  // so the branch miss rate must be emulated here 
  if(evt->getNotifyingNode() != evt->getWaitingNode()) {
    if(Utils::myrand(0, 99) < node->getBranchingPredictionMissRate()) {
      node->branchMiss();
    }
  }
    
    
    // Cristian Macario 05/02/08
    // Need to set the signal becuase there
    // can be wait() while notifying
    *notify_sig = 1;
    // Cristian Macario 22/11/07
    // if finite blocking fifo, event may not be notified
    // and block the task
    if(!evt->notify(event)) {
      *notify_sig = 0;
      node->unsetCurrentTaskSignal(notify_sig);
      evt->setBlockedNotifyTask(this);
      BLOCK();
      return 0;
    }
    *notify_sig = 0;
    
    // Cristian Macario 27/11/07
    // Update current function if event has been notified
    currentFunction = endFunction;
    
    //cout<<"Notify done!\n";
    node->setCurrentTaskSignal(notify_sig);
    
  // Cristian Macario 04/02/08
  // adding bus
  // When event between 2 CPUs the wait() is done on the interface
  if(evt->getNotifyingNode() != evt->getWaitingNode()) {
    return 0;
  } 

    
    return 1;
}

void Task::END_NOTIFY(sc_signal<bool> *notify_sig) {
     node->unsetCurrentTaskSignal(notify_sig);
}

int Task::NOTIFIED_EVENT(TMLEvent *evt, int *notified, sc_signal<bool> *notified_sig) {
    *notified = evt->notified();
    node->setCurrentTaskSignal(notified_sig);
    return 1;
}

void Task::END_NOTIFIED(sc_signal<bool> *notified_sig) {
	node->unsetCurrentTaskSignal(notified_sig);
}

int Task::SELECT_EVENT(TMLEvent *evt[], int nbEvt, int *param0, int *param1, int *param2, sc_signal<bool> *wait_sig[], int endFunction[]) {
  int i = 0, index=0, j, k;
  Event *event = NULL;
  int found = 0;

  if (nbEvt == 0) {
    return 0;
  }

  //cout<<"select0\n";
  index = Utils::myrand(0, nbEvt-1);
  for(i=0; i<nbEvt; i++) {
    j =(index+i)%nbEvt;
    event = evt[j]->wait();
    if (event == NULL) {
      *wait_sig[j] = 0;
      evt[j]->setBlockedWaitTask(this);
    } else if (found == 0) {
      found = 1;
      for(k=0; k<nbEvt; k++) {
        *wait_sig[k] = 0;
        evt[k]->unblockWaitTask();
      }
      //*wait_sig[j] = 1;
      node->setCurrentTaskSignal(wait_sig[j]);
      currentFunction = endFunction[j];
      break;
    }
  }

  //cout<<"select1\n";
  
  if (found == 0) {
    //cout<<"task is blocked: cannot get event (select op)";
     BLOCK();
     return 0;
  }

  //cout<<"select2\n";
  
  *param0 = event->param0;
  *param1 = event->param1;
  *param2 = event->param2;
  free(event);

  //cout<<"select3\n";

  return 1;
}


void Task::END_SELECT(sc_signal<bool> *wait_sig) {
      node->unsetCurrentTaskSignal(wait_sig);
}


int Task::getPriority() {
  return priority;
}

void Task::setPriority(int _priority) {
  priority = _priority;
}


// Cristian Macario 22/11/07
// Definition of the method
void Task::addReceivedEvent(TMLEvent * evt) {
  if(nbOfReceivedEvents < MAX_EVENT) {
    receivedEvents[nbOfReceivedEvents++] = evt;
  } else {
    cout<<"Too many events connected to the same task"<<endl;
    exit(0);
  }
}

