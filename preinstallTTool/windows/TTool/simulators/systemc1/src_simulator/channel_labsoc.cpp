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
#include "math.h"
#include "parameters.h"
#include "utils_labsoc.h"

#include "node_labsoc.h"
#include "channel_labsoc.h"
#include "bus_labsoc.h"


int NBRNBW_Channel::write(int nb){
  //cout<<"writting in channels:"<<nb<<"\n";
  int maxWrite;
  int nbOfBytesOneCycle = writingNode->getByteDataSize();
  if (nbOfBytesOneCycle == -1) {
	  nbOfBytesOneCycle = width;
  }
  //cout<<"writting in channels:"<<nb<<" node size:"<<nbOfBytesOneCycle<<"\n";
  if (nb <nbOfBytesOneCycle) {
    maxWrite = nb;
  } else {
    maxWrite = nbOfBytesOneCycle;
  }
  
  return writeIf->write(maxWrite, this);

}

int NBRNBW_Channel::read(int nb){
  int maxRead;
  int nbOfBytesOneCycle = readingNode->getByteDataSize();
  if (nbOfBytesOneCycle == -1) {
	  nbOfBytesOneCycle = width;
  }
  if (nb <nbOfBytesOneCycle) {
    maxRead = nb;
  } else {
    maxRead = nbOfBytesOneCycle;
  }
  
  return readIf->read(maxRead, this);
}



// Cristian Macario 11/12/07
// DEBUG
// including bus
// method has been modified
int BRBW_Channel::write(int nb){
//  mutex.lock();
  // Must determine how many can be written without being blocked
  int ret, maxWrite;

  if (currentNbOfSamples == maxNbOfSamples) {
    // mutex.unlock();
    return 0;
  }

  //cout<<"writting in channels:"<<nb<<"\n";
  int nbOfBytesOneCycle = writingNode->getByteDataSize();
  if (nbOfBytesOneCycle == -1) {
	  nbOfBytesOneCycle = width;
  }
  //cout<<"writting in channels:"<<nb<<" node size:"<<nbOfBytesOneCycle<<"\n";
  if (nb <nbOfBytesOneCycle) {
    maxWrite = nb;
  } else {
    maxWrite = nbOfBytesOneCycle;
  }

  if ((maxWrite + currentNbOfSamples) > maxNbOfSamples) {
    maxWrite = maxNbOfSamples - currentNbOfSamples;
  }
  
  ret = writeIf->write(maxWrite, this);
  
  mutex.lock();
  currentNbOfSamples += ret;
  mutex.unlock();
  
  if (blockedReadTask != NULL) {
    //cout<<"unblocking a blocked task\n";
    readingNode->setUnblockedTask(blockedReadTask);
    blockedReadTask = NULL;
  }


//  mutex.unlock();
  return ret;

}

int BRBW_Channel::read(int nb){
  // Must determine how many can be written without being blocked
  int ret, maxRead;
  

//  mutex.lock();

  if (currentNbOfSamples == 0) {
//    mutex.unlock();
    return 0;
  }

  //cout<<"reading in channels BRBW:"<<nb<<"\n";
  int nbOfBytesOneCycle = readingNode->getByteDataSize();
  if (nbOfBytesOneCycle == -1) {
	  nbOfBytesOneCycle = width;
  }
  //cout<<"reading in channels BRBW:"<<nb<<" node size:"<<nbOfBytesOneCycle<<" samples="<<currentNbOfSamples<<"\n";
  if (nb <nbOfBytesOneCycle) {
    maxRead = nb;
  } else {
    maxRead = nbOfBytesOneCycle;
  }

  if ((currentNbOfSamples - maxRead) < 0) {
    maxRead =  currentNbOfSamples;
  }
  
  ret = readIf->read(maxRead, this);
  
  mutex.lock();
  currentNbOfSamples -= ret;
  mutex.unlock();

  
  if (blockedWriteTask != NULL) {
    writingNode->setUnblockedTask(blockedWriteTask);
    blockedWriteTask = NULL;
  }
  
//  mutex.unlock();
  return ret;
}

int BRNBW_Channel::write(int nb){
  // Must determine how many can be written without being blocked
  int ret, maxWrite;
  
  //cout<<"BRBW: write\n";
  
//  mutex.lock();

  //cout<<"BRBW: locked\n";

  //cout<<"writting in channels:"<<nb<<"\n";
  int nbOfBytesOneCycle = writingNode->getByteDataSize();
  if (nbOfBytesOneCycle == -1) {
	  nbOfBytesOneCycle = width;
  }
  // cout<<"writting in channels:"<<nb<<" node size:"<<nbOfBytesOneCycle<<"\n";
  if (nb <nbOfBytesOneCycle) {
    maxWrite = nb;
  } else {
    maxWrite = nbOfBytesOneCycle;
  }


  ret = writeIf->write(maxWrite, this);
  
  mutex.lock();
  currentNbOfSamples += ret;
  mutex.unlock();
  
  if (blockedReadTask != NULL) {
    //cout<<"Unblocking a blocked task\n";
    readingNode->setUnblockedTask(blockedReadTask);
    blockedReadTask = NULL;
  }

//  mutex.unlock();
  return ret;

}

int BRNBW_Channel::read(int nb){
  // Must determine how many can be written without being blocked
  int ret, maxRead;
  
  //cout<<"BRBW: read\n";

//  mutex.lock();
  
  //cout<<"BRBW: locked\n";

  if (currentNbOfSamples == 0) {
//    mutex.unlock();
    return 0;
  }

  //cout<<"reading in channels BRBW:"<<nb<<"\n";
  int nbOfBytesOneCycle = readingNode->getByteDataSize();
  if (nbOfBytesOneCycle == -1) {
	  nbOfBytesOneCycle = width;
  }
  //cout<<"reading in channels BRBW:"<<nb<<" node size:"<<nbOfBytesOneCycle<<" samples="<<currentNbOfSamples<<"\n";
  if (nb <nbOfBytesOneCycle) {
    maxRead = nb;
  } else {
    maxRead = nbOfBytesOneCycle;
  }

  if ((currentNbOfSamples - maxRead) < 0) {
    maxRead = currentNbOfSamples;
  }
  
  
  ret = readIf->read(maxRead, this);

  mutex.lock();
  currentNbOfSamples -= ret;
  mutex.unlock();
  
//  mutex.unlock();

  return ret;
}




  
void TMLChannel::initialize() {
    currentNbOfSamples = 0;
    blockedReadTask = NULL;
    blockedWriteTask = NULL;
  }



