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

SimComponents::SimComponents():_stopFlag(false){
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
	_serList.push_back(dynamic_cast<Serializable*>(iTask));
	_taskList.push_back(iTask);
	//std::cout << iTask->toString() << std::endl;
}

void SimComponents::addChannel(TMLChannel* iChan){
	_serList.push_back(dynamic_cast<Serializable*>(iChan));
	_channelList.push_back(iChan);
}

void SimComponents::addEvent(TMLEventChannel* iEvt){
	_serList.push_back(dynamic_cast<Serializable*>(iEvt));
}

void SimComponents::addRequest(TMLEventBChannel* iReq){
	_serList.push_back(dynamic_cast<Serializable*>(iReq));
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
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
		(*i)->writeObject(s);
	}
	return s;
}

std::istream& SimComponents::readObject(std::istream& s){
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
		(*i)->readObject(s);
	}
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

SchedulableDevice* SimComponents::getCPUByName(std::string iCPU) const{
	for(SchedulingList::const_iterator i=_cpuList.begin(); i != _cpuList.end(); ++i){
		if ((*i)->toString()==iCPU) return (*i);
	}
	return NULL;
}

TMLTask* SimComponents::getTaskByName(std::string iTask) const{
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		if ((*i)->toString()==iTask) return (*i);
	}
	return NULL;
}

SchedulableCommDevice* SimComponents::getBusByName(std::string iBus) const{
	for(BusList::const_iterator i=_busList.begin(); i != _busList.end(); ++i){
		if ((*i)->toString()==iBus) return (*i);
	}
	return NULL;
}

Slave* SimComponents::getSlaveByName(std::string iSlave) const{
	for(SlaveList::const_iterator i=_slList.begin(); i != _slList.end(); ++i){
		if ((*i)->toString()==iSlave) return (*i);
	}
	return NULL;
}

TMLChannel* SimComponents::getChannelByName(std::string iChannel) const{
	for(ChannelList::const_iterator i=_channelList.begin(); i != _channelList.end(); ++i){
		if ((*i)->toShortString()==iChannel) return (*i);
	}
	return NULL;
}
