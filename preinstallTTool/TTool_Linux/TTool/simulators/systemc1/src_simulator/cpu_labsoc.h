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

#ifndef CPU_LABSOC__H
#define CPU_LABSOC__H

#include "parameters.h"
#include "node_labsoc.h"
#include "task_labsoc.h"
#include "taskcpu_labsoc.h"



class CPU : public Node {
 public:
  sc_signal<bool> runningTask;
  sc_signal<bool> taskSwitching;
  sc_signal<bool> idle;
  sc_signal<bool> goIdle;
  sc_signal<bool> pipelineLatency;
  sc_signal<bool> branchingError;
  
  //Cristian Macario 08/02/08
  sc_signal<bool> wakingUp;


protected:
  int pipelineSize;
  int taskSwitchingTime;
  int goIdleTime;
  
  // Cristian Macario 08/02/08
  int wakingUpTime;

  // Cristian Macario 05/12/07
  // now the following variable is 
  // a static variable of the method CPURR::runIdle()
  // this make the code more readable
  long cycle;
  
  int branchingPredictionMissRate;
  

  int missedInstructions;
  long executedInstructions;
  
  // Cristian Macario 21/11/2007
  // Added this variable
  // When the cpu runs in idle state for maxConsecutiveIdleCycles
  // consecutive cycles, it stop running
  int maxConsecutiveIdleCycles;
  
  



 public:
 
  void initialize();
  
  // Cristian Macario 05/12/07
  // this funcion should be virtual
  virtual void go() = 0;
  void addTask(Task *t);
  void setPipelineSize(int _pipelineSize);
  void setTaskSwitchingTime(int _taskSwitchingTime);
  void setGoIdleTime(int _goIdleTime);
  
  // Cristian Macario 08/02/08
  void setWakingUpTime(int _wakingUpTime);
  
  void setBranchingPredictionMissRate(int _branchingPredictionMissRate);
  int getBranchingPredictionMissRate();
  
  // Cristian Macario 21/11/07
  // Added the setter for maxConsecutiveIdleCycles
  void setMaxConsecutiveIdleCycles(unsigned _maxConsecutiveIdleCycles);
  void waitForInstructions(int _nb);
  void branchMiss();
  
  virtual ~CPU(){}

};

#endif


