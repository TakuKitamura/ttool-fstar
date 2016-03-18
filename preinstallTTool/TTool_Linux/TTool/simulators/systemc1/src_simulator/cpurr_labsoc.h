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

#ifndef CPURR_LABSOC__H
#define CPURR_LABSOC__H

#include "parameters.h"
#include "node_labsoc.h"
#include "cpu_labsoc.h"
#include "task_labsoc.h"
#include "taskcpu_labsoc.h"



class CPURR : public CPU {
 protected:
  TaskCPURR *taskscpurr[MAX_TASK_PER_CPU];
  TaskCPURR *nextRunnable;
  TaskCPURR *nextRunning;
  TaskCPURR *nextBlocked;
  TaskCPURR *nextTerminated;
  
  // Cristian Macario 11/12/07
  // DEBUG
  // including bus
  // property moved to Node class
  // int terminated;
  
  
public:

  // Crsitian Macario 05/12/07
  // This initialization is now done in the constructor of class node
  // (nbOfTasks is a property of class node)
/*
  CPURR() {
    nbOfTasks = 0;
  }
*/

  virtual ~CPURR(){}

public:
  void initialize();
  void initialize(int randomTasks);
  virtual void go();
  void addTask(Task *t);
protected:
  virtual void selectTaskToRun();
  void switchTasks();
  void randomTasks(TaskCPURR *taskscpurr[], int index);
  void linkRunnableTasks();
  void addTerminated(TaskCPURR *t);
  void addRunnable(TaskCPURR *t);
  void addBlocked(TaskCPURR *t);
  void removeRunnable(TaskCPURR *t);
  void removeBlocked(TaskCPURR *t);
  void runIdle();
  TaskCPURR *getTaskCPURR(Task *t);
  

};

#endif


