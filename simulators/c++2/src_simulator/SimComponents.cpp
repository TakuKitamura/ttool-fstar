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

#include <SimComponents.h>
#include <TraceableDevice.h>
#include <TMLTransaction.h>
#include <TMLTask.h>
#include <CPU.h>
#include <TMLChannel.h>
#include <TMLEventChannel.h>
#include <TMLEventBChannel.h>
#include <Slave.h>
#include <Memory.h>
#include <Bridge.h>
#include <TMLChoiceCommand.h>
#include <ListenersSimCmd.h>

SimComponents::SimComponents(int iHashValue):_stopFlag(false), _hashValue(iHashValue){
}

SimComponents::~SimComponents(){
	for(SerializableList::iterator i=_serList.begin(); i != _serList.end(); ++i){
		delete (*i);
	}
	for(SlaveList::iterator i=_slList.begin(); i != _slList.end(); ++i){
		delete (*i);
	}
}

void SimComponents::addTask(TMLTask* iTask){
	_vcdList.push_back(dynamic_cast<TraceableDevice*>(iTask));
	//_serList.push_back(dynamic_cast<Serializable*>(iTask));
	_taskList.push_back(iTask);
	//std::cout << iTask->toString() << std::endl;
}

void SimComponents::addChannel(TMLChannel* iChan){
	_serList.push_back(dynamic_cast<Serializable*>(iChan));
	_channelList.push_back(iChan);
}

void SimComponents::addEvent(TMLEventChannel* iEvt){
	_serList.push_back(dynamic_cast<Serializable*>(iEvt));
	_channelList.push_back(iEvt);
}

void SimComponents::addRequest(TMLEventBChannel* iReq){
	_serList.push_back(dynamic_cast<Serializable*>(iReq));
	_channelList.push_back(iReq);
}

void SimComponents::addCPU(CPU* iCPU){
	//TraceableDevice* a = dynamic_cast<TraceableDevice*> (iCPU);
	//std::cout << "CPU added: " << iCPU << "    " << iCPU->toString() << "   " << iCPU->toShortString() << std::endl;
	//std::cout << "TraceableDevice added: " << a << "    " << a->toString() << "   " << a->toShortString() << std::endl;
	_cpuList.push_back(dynamic_cast<SchedulableDevice*>(iCPU));
	_vcdList.push_back(dynamic_cast<TraceableDevice*>(iCPU));
	_serList.push_back(dynamic_cast<Serializable*>(iCPU));
}

void SimComponents::addBus(SchedulableCommDevice* iBus){
	_busList.push_back(iBus);
	_vcdList.push_back(dynamic_cast<TraceableDevice*>(iBus));
	_serList.push_back(dynamic_cast<Serializable*>(iBus));
}

void SimComponents::addBridge(Bridge* iBridge){
	_slList.push_back(dynamic_cast<Slave*>(iBridge));
}

void SimComponents::addMem(Memory* iMem){
	_slList.push_back(dynamic_cast<Slave*>(iMem));
}

void SimComponents::streamBenchmarks(std::ostream& s) const{
	for (TraceableDeviceList::const_iterator i=_vcdList.begin(); i!= _vcdList.end(); ++i){
		(*i)->streamBenchmarks(s);
	}
}

std::ostream& SimComponents::writeObject(std::ostream& s){
	std::cout << "WRITE ----------------------------------------------------\n";
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
		(*i)->writeObject(s);
	}
	TMLTime aSimulatedTime = SchedulableDevice::getSimulatedTime();
	WRITE_STREAM(s, aSimulatedTime);
	std::cout << "Write: SimComponents simulatedTime: " << aSimulatedTime << std::endl;
	std::cout << "----------------------------------------------------\n";
	return s;
}

std::istream& SimComponents::readObject(std::istream& s){
	std::cout << "READ ----------------------------------------------------\n";
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
		//std::cout << "SimComponents --> next Device" << std::endl;
		(*i)->readObject(s);
	}
	TMLTime aSimulatedTime;
	READ_STREAM(s, aSimulatedTime);
	SchedulableDevice::setSimulatedTime(aSimulatedTime);
	std::cout << "Read: SimComponents simulatedTime: " << aSimulatedTime << std::endl;
	std::cout << "----------------------------------------------------\n";
	return s;
}

void SimComponents::reset(){
	//std::cout << "SimComponents:reset" << std::endl;
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
			//std::cout << "loop reset before" << std::endl;
			(*i)->reset();
			//std::cout << "loop reset after" << std::endl;
	}
	TMLTransaction::reset();
#ifdef ADD_COMMENTS
	Comment::reset();
#endif
}

SchedulableDevice* SimComponents::getCPUByName(const std::string& iCPU) const{
	if (iCPU[0]>='0' && iCPU[0]<='9') return getCPUByID(StringToNum<unsigned int>(iCPU));
	for(SchedulingList::const_iterator i=_cpuList.begin(); i != _cpuList.end(); ++i){
		if ((*i)->toString()==iCPU) return (*i);
	}
	return NULL;
}

TMLTask* SimComponents::getTaskByName(const std::string& iTask) const{
	if (iTask[0]>='0' && iTask[0]<='9') return getTaskByID(StringToNum<unsigned int>(iTask));
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		if ((*i)->toString()==iTask) return (*i);
	}
	return NULL;
}

SchedulableCommDevice* SimComponents::getBusByName(const std::string& iBus) const{
	if (iBus[0]>='0' && iBus[0]<='9') return getBusByID(StringToNum<unsigned int>(iBus));
	for(BusList::const_iterator i=_busList.begin(); i != _busList.end(); ++i){
		if ((*i)->toString()==iBus) return (*i);
	}
	return NULL;
}

Slave* SimComponents::getSlaveByName(const std::string& iSlave) const{
	if (iSlave[0]>='0' && iSlave[0]<='9') return getSlaveByID(StringToNum<unsigned int>(iSlave));
	for(SlaveList::const_iterator i=_slList.begin(); i != _slList.end(); ++i){
		if ((*i)->toString()==iSlave) return (*i);
	}
	return NULL;
}

TMLChannel* SimComponents::getChannelByName(const std::string& iChannel) const{
	if (iChannel[0]>='0' && iChannel[0]<='9') return getChannelByID(StringToNum<unsigned int>(iChannel));
	for(ChannelList::const_iterator i=_channelList.begin(); i != _channelList.end(); ++i){
		if ((*i)->toShortString()==iChannel) return (*i);
	}
	return NULL;
}

SchedulableDevice* SimComponents::getCPUByID(unsigned int iID) const{
	for(SchedulingList::const_iterator i=_cpuList.begin(); i != _cpuList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

TMLTask* SimComponents::getTaskByID(unsigned int iID) const{
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

SchedulableCommDevice* SimComponents::getBusByID(unsigned int iID) const{
	for(BusList::const_iterator i=_busList.begin(); i != _busList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

Slave* SimComponents::getSlaveByID(unsigned int iID) const{
	for(SlaveList::const_iterator i=_slList.begin(); i != _slList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

TMLChannel* SimComponents::getChannelByID(unsigned int iID) const{
	for(ChannelList::const_iterator i=_channelList.begin(); i != _channelList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

TMLChoiceCommand* SimComponents::getCurrentChoiceCmd(){
	TMLChoiceCommand* aResult;
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		aResult = dynamic_cast<TMLChoiceCommand*>((*i)->getCurrCommand());
		if (aResult!=0) return aResult;
	}
	return 0;
	
}

int SimComponents::getHashValue(){
	return _hashValue;
}

TaskList::const_iterator SimComponents::getTaskIterator(bool iEnd) const{
	return (iEnd)? _taskList.end():_taskList.begin();
}

/*void SimComponents::setBreakpointOnChoiceCmds(){
	TMLChoiceCommand* aResult;
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		aResult = dynamic_cast<TMLChoiceCommand*>((*i)->getCurrCommand());
		if (aResult!=0) aResult->setBreakpoint(new Breakpoint(this));	
	}
}*/
