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
"²software by the user in light of its specific status of free software,
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
#include "task_labsoc.h"

#ifndef TASKCPU_LABSOC__H
#define TASKCPU_LABSOC__H


// Cristian Macario 06/12/07
// Modified data structure to make code more readable
/*
class TaskCPURR {
  public:
  Task *task;
  int nbOfInstInPipeline;
  TaskCPURR *nextRunnable;
  TaskCPURR *nextTerminated;
  TaskCPURR *nextBlocked;
};
*/
class TaskCPURR {
  public:
  Task *task;
  int nbOfInstInPipeline;
  TaskCPURR *next;
};


class TaskOneNode {
  public:
  Task *task;
  TaskOneNode *nextRunnable;
  TaskOneNode *nextTerminated;
  TaskOneNode *nextBlocked;
};

#endif


