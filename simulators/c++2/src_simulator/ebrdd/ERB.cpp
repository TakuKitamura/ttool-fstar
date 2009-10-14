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

#include <ERB.h>
#include <SimComponents.h>
#include <SchedulableDevice.h>
#include <SchedulableCommDevice.h>
#include <Slave.h>
#include <TMLTask.h>
#include <NotifyIF.h>
#include <TMLChannel.h>

#define NOTIFY_ANCESTOR {_nbOfNotific++; _ancestorNode->notifyEvent(_ID);}

SimComponents* ERB::_simComp=0;

ERB::ERB(NotifyIF* iAncestorNode, bool iNegated, const std::string& iName, unsigned int iSourceClass, unsigned int iSourceID, unsigned int iEvtID): EventIF(iAncestorNode, iNegated), _active(false), _name(iName), _sourceClass(iSourceClass), _sourceID(iSourceID), _evtID(iEvtID){
}

void ERB::timeTick(TMLTime iNewTime){
}

void ERB::activate(){
	_active=true;
	std::cout << "activate event: " << _name << "\n";
	switch (_sourceClass){
		//CPU
		case 0:{
			SchedulableDevice* aCPU = _simComp->getCPUByID(_sourceID);
			if (aCPU!=0) aCPU->registerListener(this);
			break;
		}
		//Bus
		case 1:{
			SchedulableCommDevice* aBus = _simComp->getBusByID(_sourceID);
			if (aBus!=0) aBus->registerListener(this);
			break;
		}
		//Mem:
		case 2:
		//Bridge
		case 3:{
			Slave* aSlave = _simComp->getSlaveByID(_sourceID);
			if (aSlave!=0) aSlave->registerListener(this);
			break;
		}
		//Channel
		case 4:{
			TMLChannel* aChannel = _simComp->getChannelByID(_sourceID);
			if (aChannel!=0) aChannel->registerListener(this);
			break;
		}
		//Task
		case 5:{
			TMLTask* aTask = _simComp->getTaskByID(_sourceID);
			if (aTask!=0) aTask->registerListener(this);
			break;
		}
	}
}

void ERB::deactivate(){
	_active=false;
	std::cout << "deactivate event: " << _name << "\n";
	switch (_sourceClass){
		//CPU
		case 0:{
			SchedulableDevice* aCPU = _simComp->getCPUByID(_sourceID);
			if (aCPU!=0) aCPU->removeListener(this);
			break;
		}
		//Bus
		case 1:{
			SchedulableCommDevice* aBus = _simComp->getBusByID(_sourceID);
			if (aBus!=0) aBus->removeListener(this);
			break;
		}
		//Mem:
		case 2:
		//Bridge
		case 3:{
			Slave* aSlave = _simComp->getSlaveByID(_sourceID);
			if (aSlave!=0) aSlave->removeListener(this);
			break;
		}
		//Channel
		case 4:{
			TMLChannel* aChannel = _simComp->getChannelByID(_sourceID);
			if (aChannel!=0) aChannel->removeListener(this);
			break;
		}
		//Task
		case 5:{
			TMLTask* aTask = _simComp->getTaskByID(_sourceID);
			if (aTask!=0) aTask->removeListener(this);
			break;
		}
	}
}

void ERB::transExecuted(TMLTransaction* iTrans){
	if (_evtID==1 && _active) NOTIFY_ANCESTOR;
}

void ERB::commandEntered(TMLCommand* iComm){
	if (_evtID==2 && _active) NOTIFY_ANCESTOR;
}

void ERB::commandStarted(TMLCommand* iComm){
	if (_evtID==3 && _active) NOTIFY_ANCESTOR;
}
	
void ERB::commandExecuted(TMLCommand* iComm){
	if (_evtID==4 && _active) NOTIFY_ANCESTOR;
}

void ERB::commandFinished(TMLCommand* iComm){
	if (_evtID==5 && _active) NOTIFY_ANCESTOR;
}

void ERB::taskStarted(TMLTransaction* iTrans){
	if (_evtID==6 && _active) NOTIFY_ANCESTOR;
}

void ERB::taskFinished(TMLTransaction* iTrans){
	if (_evtID==7 && _active) NOTIFY_ANCESTOR;
}

void ERB::readTrans(TMLTransaction* iTrans){
	if (_evtID==8 && _active) NOTIFY_ANCESTOR;
}

void ERB::writeTrans(TMLTransaction* iTrans){
	if (_evtID==9 && _active) NOTIFY_ANCESTOR;
}

void ERB::simulationStarted(){
	if (_evtID==10 && _active) NOTIFY_ANCESTOR;
}

void ERB::simulationStopped(){
	if (_evtID==11 && _active) NOTIFY_ANCESTOR;
}

void ERB::setSimComponents(SimComponents* iSimComp){
	_simComp=iSimComp;
}
/*void notify(){
	if (conditionFunction) 
		action;
	else
		abort;
	NOTIFY_ANCESTOR;
}*/

/*void abort(){
	if (_active){
		_aborted=true;
		_ancestorNode->notifyAbort(_ID);
	}
}*/
