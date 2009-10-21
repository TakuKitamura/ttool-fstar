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
#include <ERC.h>
#include <Simulator.h>

//#define NOTIFY_ANCESTOR {_nbOfNotific++; _ancestorNode->notifyEvent(_ID);}


SimComponents* ERB::_simComp=0;

ERB::ERB(ERC* iContainer, NotifyIF* iAncestorNode, bool iNegated, const std::string& iName, unsigned int iEvtID, unsigned int iSourceClass, unsigned int* iArrayOfSources, unsigned int iNbOfSources, EBRDDFuncPointer iEbrddFunc): EventIF(iAncestorNode, iNegated), _container(iContainer), _name(iName), _evtID(iEvtID), _sourceClass(iSourceClass), _arrayOfSources(iArrayOfSources), _nbOfSources(iNbOfSources), _ebrddFunc(iEbrddFunc){
}

ERB::~ERB(){
	if (_arrayOfSources!=0) delete[] _arrayOfSources;
}

void ERB::timeTick(TMLTime iNewTime){
}

void ERB::notifyAncestor(){
	std::cout << "*** event notified: " << _name << "\n";
	_nbOfNotific++;
	if (_ebrddFunc!=0 && !(_container->getEBRDD()->*_ebrddFunc)()){
		//Alert!!!
		std::cout << "ALERT!\n";
	}
	_ancestorNode->notifyEvent(_ID);
	//std::cout << "end ERB event notified: " << _name << "\n";
}

void ERB::activate(){
	_active=true;
	//std::cout << "activate event: " << _name << "\n";
	switch (_sourceClass){
		//CPU
		case 0:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				SchedulableDevice* aCPU = _simComp->getCPUByID(_arrayOfSources[i]);
				if (aCPU!=0) aCPU->registerListener(this);
				else std::cout << "register listener FAILED!!!!: " << _name << "\n";
			}
			break;
		}
		//Bus
		case 1:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				SchedulableCommDevice* aBus = _simComp->getBusByID(_arrayOfSources[i]);
				if (aBus!=0) aBus->registerListener(this);
				else std::cout << "register listener FAILED!!!!: " << _name << "\n";
			}
			break;
		}
		//Mem:
		case 2:
		//Bridge
		case 3:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				Slave* aSlave = _simComp->getSlaveByID(_arrayOfSources[i]);
				if (aSlave!=0) aSlave->registerListener(this);
				else std::cout << "register listener FAILED!!!!: " << _name << "\n";
			}
			break;
		}
		//Channel
		case 4:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				TMLChannel* aChannel = _simComp->getChannelByID(_arrayOfSources[i]);
				if (aChannel!=0) aChannel->registerListener(this);
				else std::cout << "register listener FAILED!!!!: " << _name << "\n";
			}
			break;
		}
		//Task
		case 5:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				TMLTask* aTask = _simComp->getTaskByID(_arrayOfSources[i]);
				if (aTask!=0) aTask->registerListener(this);
				else std::cout << "register listener FAILED!!!!: " << _name << "\n";
			}
			break;
		}
		//HWA
		case 6:
			break;
		//kernel
		case 7:
			 _simComp->getSimulator()->registerListener(this);
			break;
	}
	//std::cout << "end activate event: " << _name << "\n";
}

void ERB::deactivate(){
	_active=false;
	//std::cout << "deactivate event: " << _name << "\n";
	switch (_sourceClass){
		//CPU
		case 0:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				SchedulableDevice* aCPU = _simComp->getCPUByID(_arrayOfSources[i]);
				if (aCPU!=0) aCPU->removeListener(this);
			}
			break;
		}
		//Bus
		case 1:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				SchedulableCommDevice* aBus = _simComp->getBusByID(_arrayOfSources[i]);
				if (aBus!=0) aBus->removeListener(this);
			}
			break;
		}
		//Mem:
		case 2:
		//Bridge
		case 3:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				Slave* aSlave = _simComp->getSlaveByID(_arrayOfSources[i]);
				if (aSlave!=0) aSlave->removeListener(this);
			}
			break;
		}
		//Channel
		case 4:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				TMLChannel* aChannel = _simComp->getChannelByID(_arrayOfSources[i]);
				if (aChannel!=0) aChannel->removeListener(this);
			}
			break;
		}
		//Task
		case 5:{
			for (unsigned int i=0; i< _nbOfSources; i++){
				TMLTask* aTask = _simComp->getTaskByID(_arrayOfSources[i]);
				if (aTask!=0) aTask->removeListener(this);
			}
			break;
		}
		//HWA
		case 6:
			break;
		//kernel
		case 7:
			 _simComp->getSimulator()->removeListener(this);
			break;
		//std::cout << "end deactivate event: " << _name << "\n";
	}
}

void ERB::transExecuted(TMLTransaction* iTrans){
	//std::cout << "transExecuted notified: " << _name << "\n";
	if (_evtID==0 && _active) notifyAncestor();
}

void ERB::commandEntered(TMLCommand* iComm){
	//std::cout << "commandEntered notified: " << _name << "\n";
	if (_evtID==1 && _active) notifyAncestor();
}

void ERB::commandStarted(TMLCommand* iComm){
	//std::cout << "commandStarted notified: " << _name << "\n";
	if (_evtID==2 && _active) notifyAncestor();
}
	
void ERB::commandExecuted(TMLCommand* iComm){
	//std::cout << "commandExecuted: " << _name << "\n";
	if (_evtID==3 && _active) notifyAncestor();
}

void ERB::commandFinished(TMLCommand* iComm){
	//std::cout << "commandFinished notified: " << _name << "\n";
	if (_evtID==4 && _active) notifyAncestor();
}

void ERB::taskStarted(TMLTransaction* iTrans){
	//std::cout << "taskStarted notified: " << _name << "\n";
	if (_evtID==5 && _active) notifyAncestor();
}

void ERB::taskFinished(TMLTransaction* iTrans){
	//std::cout << "taskFinished notified: " << _name << "\n";
	if (_evtID==6 && _active) notifyAncestor();
}

void ERB::readTrans(TMLTransaction* iTrans){
	//std::cout << "readTrans notified: " << _name << "\n";
	if (_evtID==7 && _active) notifyAncestor();
}

void ERB::writeTrans(TMLTransaction* iTrans){
	//std::cout << "writeTrans notified: " << _name << "\n";
	if (_evtID==8 && _active) notifyAncestor();
}

void ERB::simulationStarted(){
	//std::cout << "simStarted notified: " << _name << "\n";
	if (_evtID==9 && _active) notifyAncestor();
}

void ERB::simulationStopped(){
	//std::cout << "simStopped notified: " << _name << "\n";
	if (_evtID==10 && _active) notifyAncestor();
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
