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

#include "node_labsoc.h"
#include "task_labsoc.h"
#include "cpu_labsoc.h"
#include "taskcpu_labsoc.h"
#include "utils_labsoc.h"


// Cristian Macario 05/12/07
// removed constructor because not useful anymore
void CPU::initialize() {
	executedInstructions = 0;
	missedInstructions = 0;
}



// Cristian Macario 06/12/07
// This method must be redefined
// in the child classes.
/*
void CPU::go() {
    runningTask = 1;
    //wait(1, SC_NS);
    runningTask = 0;
}
*/

void CPU::addTask(Task *t) {
    tasks[nbOfTasks] = t;
    
    // Cristian Macario 21/11/07
    // Added the -1 in order to avoid buffer overflow
    if (nbOfTasks < MAX_TASK_PER_CPU - 1) {
      nbOfTasks ++;
    }
}


// Pipeline size cannot be less than 1
void CPU::setPipelineSize(int _pipelineSize) {
  pipelineSize = _pipelineSize;
  if (pipelineSize < 1) {
	  pipelineSize = 1;
  }
}

void CPU::setTaskSwitchingTime(int _taskSwitchingTime) {
  taskSwitchingTime = _taskSwitchingTime;
}

void CPU::setGoIdleTime(int _goIdleTime) {
  goIdleTime = _goIdleTime;
}

void CPU::setWakingUpTime(int _wakingUpTime) {
  wakingUpTime = _wakingUpTime;
}

void CPU::setBranchingPredictionMissRate(int _branchingPredictionMissRate) {
	branchingPredictionMissRate = _branchingPredictionMissRate;
}

int CPU::getBranchingPredictionMissRate() {
   return branchingPredictionMissRate;
}



// Cristian Macario 21/11/07
// added the setter of maxConsecutiveIdleCycles
void CPU::setMaxConsecutiveIdleCycles(unsigned _maxConsecutiveIdleCycles) {
  maxConsecutiveIdleCycles = _maxConsecutiveIdleCycles;
}


// Cristian Macario 30/11/07
// Improved performance of this method
void CPU::waitForInstructions(int _nb) {
  //cout<<"waiting\n";
  int rand;
  int i;
  int nbMiss;
  int nbExec;
  int firstTime;
  
  
  if ((_nb >0) && (branchingPredictionMissRate > 0)) {
  
  
    // When branchingPredictionMissRate > 0 and
    // number of instructions is small, use this algorithm
    if(_nb <= (100 / branchingPredictionMissRate)) {
      // Branching prediction
      for(i=0; i<_nb; i++) {
        if (executedInstructions  == 0) {
          missedInstructions = 0;
        }
      
        if (missedInstructions < branchingPredictionMissRate) {
          rand = Utils::myrand(0, 99);
          if (rand < branchingPredictionMissRate) {
	    //cout<<"BRANCHING ERROR"<<endl;
            
            if (currentTaskSignal != NULL) {
              *currentTaskSignal = 0;
            }	
            
            branchMiss();
            missedInstructions ++;
          }
        }
        if (currentTaskSignal != NULL) {
          *currentTaskSignal = 1;
        }
        wait(1);
        executedInstructions ++;
        if (executedInstructions == 100){
          executedInstructions = 0;
        }
      }
      
      
    // When branchingPredictionMissRate > 0 and
    // number of instructions is big, use this algorithm
    } else {
      nbMiss = (int)((_nb * branchingPredictionMissRate)/100.0);
      nbExec = 0;
      firstTime = 1;
      for(i = 0; (i < nbMiss) && (nbExec < _nb); i++) {
        // The first time the random range is divided by 2
        // just because
        // --x----x----x----x--
        // (where x represent branch miss and - no branch miss)
        // is more uniform than
        // ----x----x----x----x
        rand = Utils::myrand(1, ((200/branchingPredictionMissRate)>>firstTime) -1);
        firstTime = 0;
        if((nbExec + rand) > _nb) {
          rand = _nb - nbExec;
        }
        nbExec += rand;
        
        if (currentTaskSignal != NULL) {
          *currentTaskSignal = 1;
          wait(rand);        
          *currentTaskSignal = 0;
        } else {
          wait(rand);
        }
        
        branchMiss();
        
      }
      
      if(nbExec < _nb) {
        if (currentTaskSignal != NULL) {
          *currentTaskSignal = 1;
          wait(_nb - nbExec);
          *currentTaskSignal = 0;
        } else {
          wait(_nb - nbExec);
        }
      }
    }
    
    
    
  // when branchingPredictionMissRate == 0
  } else if (_nb > 0) {
      
      // Cristian Macario 21/11/07
      // added the if: set the signal before waiting
      // if branchingPredictionMissRate == 0
      if (currentTaskSignal != NULL) {
	*currentTaskSignal = 1;
      }	
    wait(_nb);
  }
  //cout<<"DONE\n";
  
}



void CPU::branchMiss() {
            branchingError = 1;
            wait(1); 
            branchingError = 0;
            pipelineLatency = 1;
            if (pipelineSize > 1) {
              wait(pipelineSize-1);
            }
            pipelineLatency = 0; 
}
