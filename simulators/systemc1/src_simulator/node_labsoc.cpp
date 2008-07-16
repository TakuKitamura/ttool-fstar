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
#include "parameters.h"
#include "node_labsoc.h"

void Node::initialize() {
  unblockedTask = NULL;  
  blockedTask = NULL;
  terminatedTask = NULL;
}

int Node::applyEXECIPolicy(int nbEXECI){
    return (int)(cyclesEXECI * nbEXECI);
}

void Node::setByteDataSize(int _byteDataSize) {
    byteDataSize = _byteDataSize;
}

void Node::setCyclePerEXECIOp(int _cyclesEXECI) {
    cyclesEXECI = _cyclesEXECI;
}

int Node::getByteDataSize() {
    return byteDataSize;
}

int Node::taskHasBeenUnblocked() {
    return (unblockedTask != NULL);
}

void Node::setUnblockedTask(Task *t) {
  unblockedTask = t;
}

Task *Node::getUnblockedTask() {
     Task *tmp = unblockedTask;
     unblockedTask = NULL;
     return tmp;
}

int Node::taskHasBeenBlocked() {
    return (blockedTask != NULL);
}

void Node::setBlockedTask(Task *t) {
  blockedTask = t;
  //cout<<"ici\n";
  unsetCurrentTaskSignal(currentTaskSignal);
  //cout<<"la\n";
}

Task *Node::getBlockedTask() {
     Task *tmp = blockedTask;
     blockedTask = NULL;
     return tmp;
}

int Node::taskHasBeenTerminated() {
    return (terminatedTask != NULL);
}

void Node::setTerminatedTask(Task *t) {
  terminatedTask = t;
}

Task *Node::getTerminatedTask() {
     Task *tmp = terminatedTask;
     terminatedTask = NULL;
     return tmp;
}

void Node::setCurrentTaskSignal(sc_signal<bool> *_currentTaskSignal) {
  currentTaskSignal = _currentTaskSignal;
}

void Node::unsetCurrentTaskSignal(sc_signal<bool> *_currentTaskSignal) {
  if (currentTaskSignal != NULL) {
    *_currentTaskSignal = 0;
  }
  currentTaskSignal = NULL;
}

void Node::unsetCurrentTaskSignal() {
  if (currentTaskSignal != NULL) {
    *currentTaskSignal = 0;
  }
  currentTaskSignal = NULL;
}


// Cristian Macario 11/12/07
void Node::setCacheMissRate(int _cacheMissRate) {
  cacheMissRate = _cacheMissRate;
}

int Node::getCacheMissRate() {
  return cacheMissRate;
}

void Node::setCacheLineSize(int _cacheLineSize) {
  cacheLineSize = _cacheLineSize;
}

int Node::getCacheLineSize() {
  return cacheLineSize;
}

void Node::setCacheWriteBackRate(int _cacheWriteBackRate) {
  cacheWriteBackRate = _cacheWriteBackRate;
}

int Node::getCacheWriteBackRate() {
  return cacheWriteBackRate;
}

void Node::setCacheMiss(){
  cacheMiss = 1;
}

void Node::unsetCacheMiss(){
  cacheMiss = 0;
}

void Node::setCacheWriteBackLine(){
  cacheWriteBackLine = 1;
}

void Node::unsetCacheWriteBackLine(){
  cacheWriteBackLine = 0;
}

void Node::setCacheLoadLine(){
  cacheLoadLine = 1;
}

void Node::unsetCacheLoadLine(){
  cacheLoadLine = 0;
}



// Cristian Macario 11/12/07
// DEBUG
// including bus
int Node::hasTerminated(){
  return terminated;
}





