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
#include "parameters.h"

#ifndef TASK_LABSOC__H
#define TASK_LABSOC__H

#include "node_labsoc.h"
#include "channel_labsoc.h"
#include "event_labsoc.h"


class Task {
 private:
  Node *node;

 public:
  sc_signal<bool> runnable;
  sc_signal<bool> running;
  sc_signal<bool> terminated;
  sc_signal<bool> blocked;

  sc_signal<bool> execi;
  
  // Cristian Macario 21/11/2007
  // the following signals were not used
  /*
  sc_signal<bool> wr;
  sc_signal<bool> rd;
  */

  //sc_signal<bool> currentSig;


protected:
  int currentFunction;
  int nbToWrite;
  int nbToRead;
  int nbWritten;
  int nbRead;
  int initWrite;
  int initRead;
  int initWait;

  int priority;
  
  // Cristian Macario 22/11/07
  // List of the events received by the task
  TMLEvent * receivedEvents[MAX_EVENT];
  int nbOfReceivedEvents;


public:
  Task() {
    currentFunction = 0;
    
    // Cristian Macario 22/11/07
    // Initialize the number of received events
    nbOfReceivedEvents = 0;
  }

  void initialize();
  virtual int run();
  void setNode(Node *_node);
  int EXECI(int nbOfIntOp);
  void END_EXECI();
  void TERMINATE();
  void BLOCK();
  void UNBLOCK();
  int WRITE(TMLChannel *ch, int nbOfSamples, sc_signal<bool> *wr_sig, int endFunction);
  void END_WRITE(sc_signal<bool> *wr_sig);
  int READ(TMLChannel *ch, int nbOfSamples, sc_signal<bool> *rd_sig, int endFunction);
  void END_READ(sc_signal<bool> *rd_sig);
  
  // Cristian Macario 27/11/07
  // Modified the prototype of the method: added the endFunction parameter
  int NOTIFY_EVENT(TMLEvent *evt, int param0, int param1, int param2, sc_signal<bool> *wait_sig, int endFunction);
  
  void END_NOTIFY(sc_signal<bool> *wait_sig);
  int WAIT_EVENT(TMLEvent *evt, int *param0, int *param1, int *param2, sc_signal<bool> *wait_sig, int endFunction);
  void END_WAIT(sc_signal<bool> *wait_sig);
  int NOTIFIED_EVENT(TMLEvent *evt, int *notified, sc_signal<bool> *notified_sig);
  void END_NOTIFIED(sc_signal<bool> *notified_sig);
  int SELECT_EVENT(TMLEvent *evt[], int nbEvt, int *param0, int *param1, int *param2, sc_signal<bool> *wait_sig[], int endFunction[]);
  void END_SELECT(sc_signal<bool> *wait_sig);

  int getPriority();
  void setPriority(int _priority);
  
  // Cristian Macario 22/11/07
  // Added method to add received events
  void addReceivedEvent(TMLEvent * evt);
  
  virtual ~Task() {}
};

#endif


