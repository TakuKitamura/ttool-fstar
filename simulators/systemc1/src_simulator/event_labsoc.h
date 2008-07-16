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


#ifndef EVENT_LABSOC__H
#define EVENT_LABSOC__H

class Node;
class Task;
class TMLcpuIf;
#include "task_labsoc.h"
#include "interface_labsoc.h"


class Event {
  public:
  int param0;
  int param1;
  int param2;
};

class EventCell {
  public:
 Event *event;
 EventCell *next;
};


class TMLEvent{
 public:
 
  // Cristian Macario 21/11/07
  // following signals were not used
  /*
  sc_signal<bool> notifyEvent;
  sc_signal<bool> waitEvent;
  sc_signal<bool> notifiedEvent;
  */
  TMLcpuIf * waitIf;
  TMLcpuIf * notifyIf;


protected:
  int currentNbOfEvents; // All data are in Byte
  int maxNbOfEvents;
  int nParam;
  
  // Cristian Macario 14/01/08
  // adding bus
  // Node *node;
  Node *notifyingNode;
  Node *waitingNode;
  
  
  // Cristian Macario 04/02/08
  // including bus

  
  Task *blockedWaitTask;
  
  // Cristian Macario 22/11/07
  // in a finite blocking fifo a task may be blocked also
  // when it is notifying an event
  Task *blockedNotifyTask;
  
  sc_mutex mutex;
  
  EventCell *head;
  EventCell *tail;

 public:
 TMLEvent() {}

/*
  TMLEvent(Node *_node) {
    node = _node;
  }
*/  
  // Cristian Macario 14/01/08
  // adding bus
/*
  void setNode(Node *_node) {
    node = _node;
  }
*/

  void setNotifyingNode(Node *node) {
    notifyingNode = node;
  }
  
  Node *getNotifyingNode() {
    return notifyingNode;
  }

  void setWaitingNode(Node *node) {
    waitingNode = node;
  }
  
  Node *getWaitingNode() {
    return waitingNode;
  }
  
  void setWaitIf(TMLcpuIf * iface){
    waitIf = iface;
  }
  
  TMLcpuIf * getWaitIf(){
    return waitIf;
  }
  
  void setNotifyIf(TMLcpuIf * iface) {
    notifyIf = iface;
  }
  
  TMLcpuIf * getNotifyIf() {
    return notifyIf;
  }


  void setMaxNbOfEvents(int _maxNbOfEvents) {
    maxNbOfEvents = _maxNbOfEvents;
  }
  
  void setCurrentNbOfEvents(int _currentNbOfEvents) {
    currentNbOfEvents = _currentNbOfEvents;
  }
  
  
  // Cristian Macario 04/02/2008
  // support for event between 2 different cpus
  void setNParam(int n) {
    nParam = n;
  }
  
  int getNParam() {
    return nParam;
  }
  
  void initialize() {
    currentNbOfEvents = 0;
    blockedWaitTask = NULL;
    
    // Cristian Macario 22/11/07
    // initialization of added property
    blockedNotifyTask = NULL;
    
    head = NULL;
    tail = NULL;
  }

  // Effectively make the operation in one cycle
  // Returns the number of non-written samples
  
  // Cristian Macario 22/11/07
  // modified notify method: it returns 0 if
  // the task has to be blocked and 1 if not
  //virtual void notify(Event *event);
  virtual int notify(Event *event);
  
  virtual Event *wait();
  virtual int notified();

public:
  void setBlockedWaitTask(Task *t) {
    blockedWaitTask = t;
  }
  
  void unblockWaitTask() {
    blockedWaitTask = NULL;
  }
  
  // Cristian Macario 22/11/07
  // Added this method
  Task * getBlockedWaitTask() {
    return blockedWaitTask;
  }
  
  // Cristian Macario 22/11/07
  // added these two methods
  void setBlockedNotifyTask(Task *t) {
    blockedNotifyTask = t;
  }
  
  void unblockNotifyTask() {
    blockedNotifyTask = NULL;
  }
  
protected:
  void addEvent(Event *event); // Added at the end of the list
  Event *getFirstEvent();
  void removeFirstEvent();


  virtual ~TMLEvent() {}



};

class InfiniteFIFO_Event: public TMLEvent {
public:

  virtual ~InfiniteFIFO_Event() {}

};

class TMLRequest: public TMLEvent {
public:

  virtual ~TMLRequest() {}

};

class FiniteFIFO_Event: public TMLEvent {
public:

   // Cristian Macario 22/11/07
   // modified notify method: it returns 0 if
   // the task has to be blocked and 1 if not
   //virtual void notify(Event *event);
   virtual int notify(Event *event);
   
   virtual ~FiniteFIFO_Event() {}

};

class FiniteBlockingFIFO_Event: public TMLEvent {
public:

   // Cristian Macario 22/11/07
   // modified notify method: it returns 0 if
   // the task has to be blocked and 1 if not
   //virtual void notify(Event *event);
   virtual int notify(Event *event);
   
   virtual ~FiniteBlockingFIFO_Event() {}

};



#endif


