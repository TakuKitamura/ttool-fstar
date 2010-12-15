/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
Ludovic Apvrille, Renaud Pacalet
 *
 * ludovic.apvrille AT telecom-paristech.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */ 
#include<Simulator.h>
#include<AvBlock.h>
#include<EventQueueCallback.h>
#include<AvCheckpoint.h>

Simulator::Simulator(): _simTime(0){
}

Simulator::~Simulator(){
}

//void Simulator::registerEvent(EventQueueCallback* iCallBack, AVTTime iTime, unsigned int iEvtID){
//}

void Simulator::registerEvent(const EvtQueueNode &iNode){
	//std::cout << "evt queue register event for time: " << iNode.time << "\n";
	_evtQueue.push(iNode);
}

//void Simulator::cancelEvent(EventQueueCallback* iCallBack){
//}

void Simulator::run(){
	EnabledTransList aTransList;
	unsigned int aUserChoice=1;
	while(aUserChoice>0 && advanceSimulationTime(aTransList)){
		displaySystemState();
		std::cout << "*** Enabled System Transitions ***\n";
		for (unsigned int i=0; i< aTransList.size(); ++i){
			std::cout << "(" << i+1 << ") in Block " << aTransList[i].block->toString() << " " << aTransList[i].text << "\n";
		}
		std::cout << "(0) Quit simulation\n";
		aUserChoice=0;
		do{
			std::cout << "Please enter a transition no: ";
			std::cin >> aUserChoice;
			std::cout << "\n";
		}while(aUserChoice>aTransList.size());
		if (aUserChoice!=0){
			aTransList[aUserChoice-1].block->execute(aTransList[aUserChoice-1]);
			aTransList.clear();
			//std::cout << "run: is enabled\n";
		}
	}
	displaySystemState();
}

void Simulator::displaySystemState(){
	std::cout << "*** Current System State at time "<< _simTime << " ***\n";
	for (BlockList::const_iterator i=_blockList.begin(); i!= _blockList.end(); ++i){
		std::cout << "Block " << (*i)->toString() << ": " << (*i)->getCurrCommand()->toString() << "\n";
	}
}

bool Simulator::advanceSimulationTime(EnabledTransList& iTransList){
	for (BlockList::const_iterator i=_blockList.begin(); i!= _blockList.end(); ++i){
		(*i)->isEnabled(iTransList);
	}
	while(iTransList.empty() && !_evtQueue.empty()){
		_simTime = _evtQueue.top().time;
		while(!_evtQueue.empty() && _simTime == _evtQueue.top().time){
			_evtQueue.top().callBack->notifyEvent(_evtQueue.top().evtID);
			_evtQueue.pop();
		}
		for (BlockList::const_iterator i=_blockList.begin(); i!= _blockList.end(); ++i){
			(*i)->isEnabled(iTransList);
		}
	}
	return !(iTransList.empty());
}

AVTTime Simulator::getSimulationTime(){
	return _simTime;
}

void Simulator::addBlock(AvBlock* iBlock){
	_blockList.push_back(iBlock);
}
