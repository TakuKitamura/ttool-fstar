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

#include "cpurr_labsoc.h"
#include "utils_labsoc.h"

// Randomizes tasks;
void CPURR::initialize() {
  initialize(1);
  
  // Cristian Macario 05/12/07
  // these two variables are now satic vars of the 
  // CPU::waitForInstructions() method 
  //executedInstructions = 0;
  //missedInstructions = 0;
}

void CPURR::initialize(int randomTask) {
  terminated = 0;

  nextBlocked = NULL;
  nextRunning = NULL;
  nextTerminated = NULL;
  
  // Default values
  //taskSwitchingTime = 10;
  //cyclesEXECI = 1;
  //goIdleTime = 5;
  
  if (nbOfTasks == 0) {
    terminated = 1;
    //idle = 1;
    return;
  }
  
  // Randomizes runnable tasks
  if (randomTask > 0) {
     randomTasks(taskscpurr,nbOfTasks);
  }
  linkRunnableTasks();
}

void CPURR::go() {
  int nb = 0;
  Task *tmp;
  TaskCPURR *tcpurr;
  
  
  initialize();
  
  while(terminated == 0) {
  
  
    // Cristian Macario 14/12/07
    // adding bus
    // now a task can be unblocked by an operation performed by another cpu
    if (taskHasBeenUnblocked()) {
      tmp = getUnblockedTask();
      tmp->UNBLOCK();
      tcpurr = getTaskCPURR(tmp);
      removeBlocked(tcpurr);
      addRunnable(tcpurr);
      //cout << "A task has been unblocked\n";
    }  
  

    if (nextRunning == NULL) {
      //cout << "Selecting task to run\n";
      selectTaskToRun();
    }
    
    if (nextRunning != NULL) {
      //cout << "Running task\n";
      
      // Cristian Macario 08/02/08
      // adding Waking up time
      if(idle == 1) {
        idle = 0;
        nextRunning->task->running = 0;
        wakingUp = 1;
        if(wakingUpTime > 0) {
          wait(wakingUpTime);
        }
        wakingUp = 0;
        nextRunning->task->running = 1;
      }
      
      
      runningTask = 1;
      idle = 0;
      taskSwitching = 0;

      // A task is running
      // Call that task
      nb = nextRunning->task->run();
      
      if (nb > 0) {
        waitForInstructions(nb);
      }
     
      // Task has been unblocked?
      //cout<<"Checking for unblocked task\n";
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
      }

      if (taskHasBeenTerminated()) {
	   // cout << "task is terminated \n";
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
             runningTask = 0;
       // This wait is needed to update the value of the signal terminated
       // whose value will be checked on the go() method (this method),
       // on the line  "while(terminated == 0)"
             wait(1);
             //idle = 1;
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
//  cout << "Ending CPURR node " << nb <<"\n";

}

void CPURR::selectTaskToRun() {
  if (nextRunnable != NULL) {
    nextRunning = nextRunnable;
    
    // Cristian Macario 21/11/07
    // if() not useful
    //if (nextRunning != NULL) {
      nextRunning->task->running = 1;
      nextRunning->nbOfInstInPipeline = 0;
    //}
  }
  //cout<<"task selected in cpurr"<<endl;
}

void CPURR::runIdle() {

  
  if (idle == 0) {
    goIdle = 1;
    runningTask = 0;
    
    // Cristian Macario 21/11/07
    // cycle has to be initialized
    cycle = 0;
    if (goIdleTime > 0) {
      wait(goIdleTime);
    }
    goIdle = 0;
  }
  //cout << "idle!\n";
     runningTask = 0;
     idle = 1;
     taskSwitching = 0;
     wait(1);
     cycle ++;
     if (cycle > maxConsecutiveIdleCycles) {
       idle = 0;
       terminated = 1;
       // This wait is needed to update the value of the signal terminated
       // whose value will be checked on the go() method,
       // on the line  "while(terminated == 0)"
       wait(1);
     }
}

void CPURR::switchTasks() {
     runningTask = 0;
     idle = 0;
     taskSwitching = 1;
     waitForInstructions(taskSwitchingTime);
}


// Manipulate tasks


void CPURR::addTask(Task *t) {
  //cout << "adding task\n"<<nbOfTasks << "max=" << MAX_TASK_PER_CPU;
  TaskCPURR *tcpu = new TaskCPURR();
  tcpu->nbOfInstInPipeline = 0;
  tcpu->task = t;
  tasks[nbOfTasks] = t;
  taskscpurr[nbOfTasks] = tcpu;
  if (nbOfTasks < MAX_TASK_PER_CPU) {
    nbOfTasks ++;
  }
}

void CPURR::randomTasks(TaskCPURR *taskscpurr[], int index) {
  int chosen;

  if (index <2 ) {
    return;
  }
  TaskCPURR *tmp;
  chosen = Utils::myrand(0, index-1);
  if (chosen == index-1) {
    return randomTasks(taskscpurr, index-1);
  }
  
  tmp = taskscpurr[index-1];
  taskscpurr[index-1] = taskscpurr[chosen];
  taskscpurr[chosen] = tmp;
  return randomTasks(taskscpurr, index-1);
}

void CPURR::linkRunnableTasks() {
  int i;
  for(i=0; i<nbOfTasks; i++) {
    if (i != (nbOfTasks-1)) {
      taskscpurr[i]->next = taskscpurr[i+1];
    } else {
      taskscpurr[i]->next = NULL;
    }
    taskscpurr[i]->task->runnable = 1;
  }
  nextRunnable = taskscpurr[0];
}

 void CPURR::addTerminated(TaskCPURR *t) {
   // First terminated task ?
   if (nextTerminated == NULL) {
      nextTerminated = t;
      t->next = NULL;
      return;
   }
   
   // Task is added at the beginning of the list
   t->next = nextTerminated;
   nextTerminated = t;
 }
 
 void CPURR::addBlocked(TaskCPURR *t) {
   // First blocked task ?
   if (nextBlocked == NULL) {
      nextBlocked = t;
      t->next = NULL;
      return;
   }
   
   // Task is added at the beginning of the list
   t->next = nextBlocked;
   nextBlocked = t;
 }
 
 void CPURR::addRunnable(TaskCPURR *t) {
   TaskCPURR *tmp;

   // First runnable task ?
   if (nextRunnable == NULL) {
      nextRunnable = t;
      t->next = NULL;
      return;
   }
   
   // Task is added at the end of the list
   tmp = nextRunnable;
   while(tmp->next != NULL) {
     
     // Cristian Macario 22/11/07
     // If the task we want to insert is already on the list
     // there is an error
     if(tmp == t) {
       cout<<"ERROR 007"<<endl;
       return;
     }
     tmp = tmp->next;
   }
   tmp->next = t;
   t->next = NULL;
 }

 void CPURR::removeRunnable(TaskCPURR *t) {
   TaskCPURR *tmp;

   if (nextRunnable == NULL) {
      cout << "ERROR 001";
      return;
   }

   // t first in list?
   if (nextRunnable == t) {
     nextRunnable = t->next;
     t->next = NULL;
     return;
   }

   // Must search for the task just before t;
   tmp = nextRunnable;
   while((tmp != NULL) && (tmp->next != t)) {
     tmp = tmp->next;
   }
   if (tmp == NULL) {
      cout << "ERROR 002";
      return;
   }
   tmp->next = t->next;
   t->next = NULL;
 }
 
void CPURR::removeBlocked(TaskCPURR *t) {
   TaskCPURR *tmp;

   if (nextBlocked == NULL) {
      cout << "ERROR 003";
      return;
   }

   // t first in list?
   if (nextBlocked == t) {
     nextBlocked = t->next;
     t->next = NULL;
     return;
   }

   // Must search for the task just before t;
   tmp = nextBlocked;
   while((tmp != NULL) && (tmp->next != t)) {
     tmp = tmp->next;
   }
   if (tmp == NULL) {
     
     // Cristian Macario 21/11/07
     // Uncommented error messages:
     // Trying to unblock a task that is not blocked
     // anymore is an error. Got an example of bug on the 
     // simulation result.
     cout<<"Task was already not blocked anymore\n";
     cout << "ERROR 004\n";
     return;
   }
   tmp->next = t->next;
   t->next = NULL;
 }
 
 TaskCPURR *CPURR::getTaskCPURR(Task *t) {
   int i;
   for(i=0; i<nbOfTasks; i++) {
     if (taskscpurr[i]->task == t) {
       return taskscpurr[i];
     }
   }
   return NULL;
 }
 

