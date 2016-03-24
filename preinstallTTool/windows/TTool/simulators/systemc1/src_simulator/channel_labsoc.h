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


#ifndef CHANNEL_LABSOC__H
#define CHANNEL_LABSOC__H


#include "event_labsoc.h"
#include "interface_labsoc.h"

class Task;
class Node;
class memory;

#include "task_labsoc.h"
#include "node_labsoc.h"
#include "comm_labsoc.h"
#include "mem_labsoc.h"


// Cristian Macario 11/12/07
// DEBUG
// including bus
//#include "interface_labsoc.h"


class TMLChannel:public TMLComm{
 public:
 
  // Cristian Macario 21/11/07
  // signals not used
  /*
  sc_signal<bool> wr;
  sc_signal<bool> rd;
  */
  memory * mem;

protected:

  // Cristian Macario
  // adding bus and cache emulation
  int currentNbOfSamples;
//  int writtenSamples;
//  int readableSamples;
  

  
  int width;
  int maxNbOfSamples;
  Node *readingNode;
  Node *writingNode;
  Task *blockedReadTask;
  Task *blockedWriteTask;
  sc_mutex mutex;
  
  // Cristian Macario 11/12/07
  // DEBUG
  // including bus
  TMLcpuIf * readIf;
  TMLcpuIf * writeIf;
  
  

 public:
 TMLChannel() {}


// Cristian Macario 13/12/07
// adding bus
/*
 TMLChannel(int _width, Node *_node) {
    width = _width;
    node = _node;
  }
*/  
TMLChannel(int _width) {
    width = _width;
  }

// Cristian Macario 13/12/07
// adding bus
/*
  void setNode(Node *_node) {
    node = _node;
  }
*/

  void setReadingNode(Node *_node) {
    readingNode = _node;
  }
  
    void setWritingNode(Node *_node) {
    writingNode = _node;
  }
  
  
  Node * getReadingNode() {
    return readingNode;
  }
  
  Node * getWritingNode() {
    return writingNode;
  }
  
  void setWidth(int _width) {
    width = _width;
  }
  

  
  // Width must be set first
  // Input is in number of samples. Output is in byte.
  void setMaxNbOfSamples(int _maxNbOfSamples) {
    maxNbOfSamples = _maxNbOfSamples * width;
  }
  
  // Width must be set first
  void setCurrentNbOfSamples(int _currentNbOfSamples) {
    currentNbOfSamples = _currentNbOfSamples;
  }
  
  
  void initialize();

  // Returns the number of cycles it takes
  virtual int getNbToWrite(int nb){return nb*width;}
  virtual int getNbToRead(int nb){return nb*width;}

  // Effectively make the operation in one cycle
  // Returns the number of non-written samples
  virtual int write(int nb){return 1;}
  virtual int read(int rd) {return 1;}
  
  void setBlockedWriteTask(Task *t) {
    blockedWriteTask = t;
  }
  
  void setBlockedReadTask(Task *t) {
    blockedReadTask = t;
  }
  
  
  // Cristian Macario 11/12/07
  // DEBUG
  // including bus
  void setReadIf(TMLcpuIf * _readIf) {
    readIf = _readIf;
  }
  
  TMLcpuIf *getReadIf(){
    return readIf;
  }
  
  void setWriteIf(TMLcpuIf * _writeIf) {
    writeIf = _writeIf;
  }
  
  TMLcpuIf *getWriteIf(){
    return writeIf;
  }
  
  void setMem(memory * _mem) {
    mem = _mem;
  }
  

/*
  // Cristian Macario
  // adding bus and cache emulation
  void updateReadableSamples(int n);
*/


  virtual int isWriteBlocking() { return 0;}
  virtual int isReadBlocking() {return 0;}

  virtual ~TMLChannel() {}
  

  };

class NBRNBW_Channel: public TMLChannel {
public:
  virtual int write(int nb);
  virtual int read(int rd);

  virtual ~NBRNBW_Channel() {}

};

class BRBW_Channel: public TMLChannel {
public:
  virtual int write(int nb);
  virtual int read(int rd);


  virtual ~BRBW_Channel() {}

};

class BRNBW_Channel: public TMLChannel {
public:
  virtual int write(int nb);
  virtual int read(int rd);

  // Cristian Macario 21/11/07
  // distructor removed because not useful
  //virtual ~BRNBW_Channel() {currentNbOfSamples=0;}

};

#endif


