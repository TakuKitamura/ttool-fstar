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

#include "cpurrpb_labsoc.h"
#include "utils_labsoc.h"
#include "task_labsoc.h"

void CPURRPB::go() {
  int nb = 0;
  Task *tmp;
  TaskCPURR *tcpurr;

  initialize();
  while(terminated == 0) {
    if (nextRunning == NULL) {
      //cout << "Selecting task to run\n";
      selectTaskToRun();
    }
    
    if (nextRunning != NULL) {
      //cout << "Running task\n";
      runningTask = 1;
      idle = 0;
      taskSwitching = 0;

      // A task is running
      // Call that task
      

      nb = nextRunning->task->run();
      
      //cout<<"nb=" << nb<<endl;
      
      // Apply penalty due to branching prediction
      //cout<<"Applying time to wait="<<nb<<"\n";
      
      
      if (nb == 0) {
        //wait(1);
      } else {
	//cout << "task must wait for " << nb<<"\n";
         //Setting task signal
	if (nb > 0) {
	  waitForInstructions(nb);
	}
         //cout<<"waiting done\n";
      }
      
      // Task has been unblocked?
      //cout<<"Checking for unblocked task\n";
      tmp = NULL;
      if (taskHasBeenUnblocked()) {
        tmp = getUnblockedTask();
        tmp->UNBLOCK();
        tcpurr = getTaskCPURR(tmp);
        removeBlocked(tcpurr);
        addRunnable(tcpurr);
        //cout << "A task has been unblocked\n";
      }

      

      // Is current task blocked?
      if (taskHasBeenBlocked()) {
        //cout << "task is blocked \n";
        tmp = getBlockedTask();
        if (tmp != nextRunning->task) {
          cout << "ERROR 005";
          exit(0);
        }
        removeRunnable(nextRunning);
        addBlocked(nextRunning);
        nextRunning = NULL;
        if (nextRunnable != NULL) {
          switchTasks();
        } else {
          runIdle();
        }
      } else {
	if (tmp != NULL) { // Task has been unblocked
	  if (tmp->getPriority() > nextRunning->task->getPriority()) {
	    // Nullify current signals
	    //nextRunning->task->currentSig = 0;
	    unsetCurrentTaskSignal();
	    nextRunning->task->running = 0;
	    nextRunning = NULL;
	    switchTasks();
	  }
	}
      }

      if (taskHasBeenTerminated()) {
	//cout << "task is terminated \n";
           tmp = getTerminatedTask();
           if (tmp != nextRunning->task) {
             cout << "ERROR 006";
             exit(0);
           }
           removeRunnable(nextRunning);
           addTerminated(nextRunning);
           nextRunning = NULL;
           //if ((nextRunnable == NULL) && (nextBlocked == NULL)) { For CPU with external interfaces
           if ((nextRunnable == NULL) && (nextBlocked == NULL)) {
             terminated = 1;
           } else {
             if (nextRunnable != NULL) {
               switchTasks();
             } else {
               runIdle();
             }
           }
      }
    } else {
      runIdle();
    }
  }
  wait(1);
  cout << "Ending CPURRPB node " << nb <<"\n";

}


void CPURRPB::selectTaskToRun() {
  TaskCPURR *task = NULL;
  TaskCPURR *tmp = NULL;
  int priority = 0;
  int index = 0;

  if (nextRunnable != NULL) {
    task = nextRunnable;
    priority = task->task->getPriority();
    tmp = task;
    while(tmp->next != NULL) {
      tmp = tmp->next;
      index ++;
      if (tmp->task->getPriority() > priority) {
	task = tmp;
	priority = task->task->getPriority();
      }
    }
    
  } 
  nextRunning = task;
  if (nextRunning != NULL) {
    nextRunning->task->running = 1;
    nextRunning->nbOfInstInPipeline = 0;
  }
  cout<<"Task selected prio="<<priority<<endl;
}

