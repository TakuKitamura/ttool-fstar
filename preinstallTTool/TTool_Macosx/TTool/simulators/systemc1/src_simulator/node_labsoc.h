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

#include "systemc"

#ifndef NODE_LABSOC__H
#define NODE_LABSOC__H


class Task;
#include "task_labsoc.h"



class Node {
  protected:
  
  int nbOfTasks;
  Task *tasks[MAX_TASK_PER_CPU];

  int byteDataSize;
  int cyclesEXECI;

  Task *unblockedTask;
  Task *blockedTask;
  Task *terminatedTask;
  
  sc_signal<bool> *currentTaskSignal;
  
  
  // Cristian Macario 11/12/07
  int cacheMissRate;  // In %o
  int cacheLineSize; // In bytes
  int cacheWriteBackRate; // In %
  
  // Cristian Macario 11/12/07
  // DEBUG
  // including bus
  // moved this property from CPURR class to this class
//  int terminated;
  // terminated has become a signal and it is traced




  public:
  
  // Cristian Macario 12/12/07
  // DEBUG
  // including bus
  sc_signal<bool> cacheMiss;
  sc_signal<bool> cacheWriteBackLine;
  sc_signal<bool> cacheLoadLine;
  sc_signal<bool> terminated;
  
  
  // Cristian Macario 05/12/07
  // Defined constructor
  // Before nbOfTasks was initialized in the CPURR class constructor
  // given that nbOfTasks is a property of this class, it's better
  // to do it here.
  Node(){
    nbOfTasks = 0;
    terminated = 0;
    cacheMissRate = 0;
    cacheWriteBackRate = 0;
  }
  
  void initialize();
  int applyEXECIPolicy(int nbEXECI);

  void setByteDataSize(int _byteDataSize);
  void setCyclePerEXECIOp(int _cyclesEXECI);
  int getByteDataSize();

  void setUnblockedTask(Task *);
  int taskHasBeenUnblocked();
  Task *getUnblockedTask();
  void setBlockedTask(Task *);
  int taskHasBeenBlocked();
  Task *getBlockedTask();
  void setTerminatedTask(Task *);
  int taskHasBeenTerminated();
  Task *getTerminatedTask();
  
  void setCurrentTaskSignal(sc_signal<bool> *_currentTaskSignal);
  void unsetCurrentTaskSignal(sc_signal<bool> *_currentTaskSignal);
  void unsetCurrentTaskSignal();
  
  // Cristian Macario 11/12/07
  // DEBUG
  // including bus
  void setCacheMissRate(int _cacheMissRate);
  int getCacheMissRate();
  void setCacheLineSize(int _cacheLineSize);
  int getCacheLineSize();
  void setCacheWriteBackRate(int _cacheWriteBackRate);
  int getCacheWriteBackRate();
  int hasTerminated();
  void setCacheMiss();
  void unsetCacheMiss();
  void setCacheWriteBackLine();
  void unsetCacheWriteBackLine();
  void setCacheLoadLine();
  void unsetCacheLoadLine();
  
  // Cristian Macario 08/02/08
  virtual int getBranchingPredictionMissRate() {return 0;}
  virtual void branchMiss(){};


};

#endif
