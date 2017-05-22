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
//#include <TMLEventBChannel.h>
#include <Slave.h>
#include <Memory.h>
#include <Bridge.h>
#include <IndeterminismSource.h>
#include <ListenersSimCmd.h>
#include <Simulator.h>
#ifdef EBRDD_ENABLED
#include <EBRDD.h>
#endif

SimComponents::SimComponents(int iHashValue /*, Simulator* iSimulator*/): _simulator(0), _stopFlag(false), _hashValue(iHashValue), _stoppedOnAction(false),  _systemHash(), _knownStateReached(0), _onKnownPath(false) {
}

SimComponents::~SimComponents(){
	//std::cerr << "b1\n";
	for(TEPEListenerList::iterator i=_tepeListenerList.begin(); i != _tepeListenerList.end(); ++i){
		//std::cerr << "b...\n";
		delete (*i);
		//std::cerr << "after b...\n";
	}
	//std::cerr << "b2\n";
	for(SerializableList::iterator i=_serList.begin(); i != _serList.end(); ++i){
		delete (*i);
	}
	for(SlaveList::iterator i=_slList.begin(); i != _slList.end(); ++i){
		delete (*i);
	}
#ifdef EBRDD_ENABLED
	for(EBRDDList::iterator i=_ebrddList.begin(); i != _ebrddList.end(); ++i){
		delete (*i);
	}
#endif
	//_myfile.close();
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

void SimComponents::addRequest(TMLEventChannel* iReq){
	_serList.push_back(dynamic_cast<Serializable*>(iReq));
	_channelList.push_back(iReq);
}

void SimComponents::addCPU(CPU* iCPU){
	//TraceableDevice* a = dynamic_cast<TraceableDevice*> (iCPU);
	//std::cout << "CPU added: " << iCPU << "    " << iCPU->toString() << "   " << iCPU->toShortString() << std::endl;
	//std::cout << "TraceableDevice added: " << a << "    " << a->toString() << "   " << a->toShortString() << std::endl;
	//_cpuList.push_back(dynamic_cast<SchedulableDevice*>(iCPU));
	_cpuList.push_back(iCPU);
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

#ifdef EBRDD_ENABLED
void SimComponents::addEBRDD(EBRDD* iEBRDD){
	_ebrddList.push_back(iEBRDD);
}
#endif

void SimComponents::addTEPEListener(GeneralListener* iTEPEListener){
	//std::cout << "before add\n";
	_tepeListenerList.push_back(iTEPEListener);
	//std::cout << "after add\n";
}

void SimComponents::setTEPEEntryPoint(TEPEFloatingSigListener* iTEPEEntryPoint){
	_serList.push_back(dynamic_cast<Serializable*>(iTEPEEntryPoint));
}

void SimComponents::streamBenchmarks(std::ostream& s) const{
	s << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl;
	for (TraceableDeviceList::const_iterator i=_vcdList.begin(); i!= _vcdList.end(); ++i){
		(*i)->streamBenchmarks(s);
		s << std::endl;
	}
	std::ostringstream msg;
	msg << "" << std::endl;
	 _simulator->latencies2XML(msg, 71, 57);
	s << TAG_SIMDURo << _simulator->getSimDuration() << TAG_SIMDURc << std::endl;
	s << "<LatencyInfos>" << msg.str() << "</LatencyInfos>" << std::endl;
	s << "<EndTime>" << _simulator->getEnd() << "</EndTime>"  << TAG_GLOBALc << TAG_STARTc <<std::endl;
}

std::ostream& SimComponents::writeObject(std::ostream& s){
#ifdef DEBUG_SERIALIZE
	std::cout << "WRITE ----------------------------------------------------\n";
#endif
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
		(*i)->writeObject(s);
	}
#ifdef EBRDD_ENABLED
	for(EBRDDList::const_iterator i=_ebrddList.begin(); i != _ebrddList.end(); ++i){
		(*i)->writeObject(s);
	}
#endif
	TMLTime aSimulatedTime = SchedulableDevice::getSimulatedTime();
	WRITE_STREAM(s, aSimulatedTime);
	WRITE_STREAM(s, _onKnownPath);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: SimComponents simulatedTime: " << aSimulatedTime << std::endl;
	std::cout << "----------------------------------------------------\n";
#endif
	return s;
}

std::istream& SimComponents::readObject(std::istream& s){
#ifdef DEBUG_SERIALIZE
	std::cout << "READ ----------------------------------------------------\n";
#endif
	for(SerializableList::const_iterator i=_serList.begin(); i != _serList.end(); ++i){
		//std::cout << "SimComponents --> next Device" << std::endl;
		(*i)->readObject(s);
	}
#ifdef EBRDD_ENABLED
	for(EBRDDList::const_iterator i=_ebrddList.begin(); i != _ebrddList.end(); ++i){
		(*i)->readObject(s);
	}
#endif
	TMLTime aSimulatedTime;
	READ_STREAM(s, aSimulatedTime);
	SchedulableDevice::setSimulatedTime(aSimulatedTime);
	READ_STREAM(s, _onKnownPath);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: SimComponents simulatedTime: " << aSimulatedTime << std::endl;
	std::cout << "----------------------------------------------------\n";
#endif
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
#ifdef EBRDD_ENABLED
	for(EBRDDList::const_iterator i=_ebrddList.begin(); i != _ebrddList.end(); ++i){
		(*i)->reset();
	}
#endif
	//std::cout << "----------------------------------------------- RESET\n";
	_knownStateReached = false;
}

void SimComponents::resetStateHash(){
	_systemHashTable.clear();
	TMLTransaction::resetID();
	_onKnownPath=false;
	TMLCommand::clearCoverageVars();
}

SchedulableDevice* SimComponents::getCPUByName(const std::string& iCPU) const{
	if (iCPU[0]>='0' && iCPU[0]<='9') return getCPUByID(StringToNum<ID>(iCPU));
	for(CPUList::const_iterator i=_cpuList.begin(); i != _cpuList.end(); ++i){
		if ((*i)->toString()==iCPU) return (*i);
	}
	return NULL;
}

TMLTask* SimComponents::getTaskByName(const std::string& iTask) const{
	if (iTask[0]>='0' && iTask[0]<='9') return getTaskByID(StringToNum<ID>(iTask));
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		if ((*i)->toString()==iTask) return (*i);
	}
	std::cerr << "aborting, task " << iTask << " does not exist\n";
	return NULL;
}

SchedulableCommDevice* SimComponents::getBusByName(const std::string& iBus) const{
	if (iBus[0]>='0' && iBus[0]<='9') return getBusByID(StringToNum<ID>(iBus));
	for(BusList::const_iterator i=_busList.begin(); i != _busList.end(); ++i){
		if ((*i)->toString()==iBus) return (*i);
	}
	return NULL;
}

Slave* SimComponents::getSlaveByName(const std::string& iSlave) const{
	if (iSlave[0]>='0' && iSlave[0]<='9') return getSlaveByID(StringToNum<ID>(iSlave));
	for(SlaveList::const_iterator i=_slList.begin(); i != _slList.end(); ++i){
		if ((*i)->toString()==iSlave) return (*i);
	}
	return NULL;
}

TMLChannel* SimComponents::getChannelByName(const std::string& iChannel) const{
	if (iChannel[0]>='0' && iChannel[0]<='9') return getChannelByID(StringToNum<ID>(iChannel));
	for(ChannelList::const_iterator i=_channelList.begin(); i != _channelList.end(); ++i){
		if ((*i)->toShortString()==iChannel) return (*i);
	}
	return NULL;
}

SchedulableDevice* SimComponents::getCPUByID(ID iID) const{
	//std::cerr << "getCPUByID " << iID << "\n";
	//CPUList::const_iterator i=_cpuList.begin();
	//std::cerr << "getCPUByID after i=_cpuList.begin()" << iID << "\n";
	for(CPUList::const_iterator i=_cpuList.begin(); i != _cpuList.end(); ++i){
		//std::cout << "CPU x\n";
		if ((*i)->getID()==iID) return (*i);
	}
	//std::cout << "End CPU\n";
	return NULL;
}

TMLTask* SimComponents::getTaskByID(ID iID) const{
	std::cout << "Task " << iID << "\n";
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

SchedulableCommDevice* SimComponents::getBusByID(ID iID) const{
	for(BusList::const_iterator i=_busList.begin(); i != _busList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

Slave* SimComponents::getSlaveByID(ID iID) const{
	for(SlaveList::const_iterator i=_slList.begin(); i != _slList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

TMLChannel* SimComponents::getChannelByID(ID iID) const{
	for(ChannelList::const_iterator i=_channelList.begin(); i != _channelList.end(); ++i){
		if ((*i)->getID()==iID) return (*i);
	}
	return NULL;
}

/*TMLChoiceCommand* SimComponents::getCurrentChoiceCmd(){
	TMLChoiceCommand* aResult;
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		aResult = dynamic_cast<TMLChoiceCommand*>((*i)->getCurrCommand());
		if (aResult!=0) return aResult;
	}
	return 0;
}*/

IndeterminismSource* SimComponents::getCurrentRandomCmd(){
	IndeterminismSource* aResult;
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		aResult = dynamic_cast<IndeterminismSource*>((*i)->getCurrCommand());
		if (aResult!=0) return aResult;
	}
	return 0;
}


std::string SimComponents::getCmpNameByID(ID iID){
	SchedulableDevice* aSched = getCPUByID(iID);
	if (aSched!=0) return aSched->toString();
	TMLTask* aTask = getTaskByID(iID);
	if (aTask!=0) return aTask->toString();
	SchedulableCommDevice* aBus = getBusByID(iID);
	if (aBus!=0) return aBus->toString();
	Slave* aSlave = getSlaveByID(iID);
	if (aSlave!=0) return aSlave->toString();
	TMLChannel* aChan = getChannelByID(iID);
	if (aChan!=0) return aChan->toShortString();
	return std::string("unknown");
}

//ListenerSubject <TransactionListener>* SimComponents::getListenerByID(ID iID){
ListenerSubject <GeneralListener>* SimComponents::getListenerByID(ID iID){
	//std::cerr << "Hello 1\n";
	ListenerSubject <GeneralListener>* aListener = getCPUByID(iID);
	if (aListener!=0) return aListener;
	//std::cerr << "Hello 2\n";
	aListener = TMLCommand::getCommandByID(iID);
	if (aListener!=0) return aListener;
	//std::cerr << "Hello 3\n";
	aListener = getTaskByID(iID);
	if (aListener!=0) return aListener;
	//std::cerr << "Hello 4\n";
	aListener = getBusByID(iID);
	if (aListener!=0) return aListener;
	//std::cerr << "Hello 5\n";
	aListener = getSlaveByID(iID);
	if (aListener!=0) return aListener;
	//std::cerr << "Hello 6\n";
	return getChannelByID(iID);
}

/*void SimComponents::setBreakpointOnChoiceCmds(){
	TMLChoiceCommand* aResult;
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		aResult = dynamic_cast<TMLChoiceCommand*>((*i)->getCurrCommand());
		if (aResult!=0) aResult->setBreakpoint(new Breakpoint(this));	
	}
}*/

void SimComponents::setStopFlag(bool iStopFlag, const std::string& iStopReason){ 
	_stopFlag=iStopFlag;
	if (iStopFlag){
		_stopReason+= " " + iStopReason;
	}else{
		_stopReason="";
		_stoppedOnAction=false;
		//std::cout << "_stoppedOnAction=false\n";
	}
}

ID SimComponents::checkForRecurringSystemState(){
	//std::cout << "Recurring Sys State\n";
	_systemHash.init((HashValueType)0xabcd, _taskList.size());
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		//std::cout << "add Task Hash " << (*i)->toString() << "\n";
		_systemHash.addValue((*i)->getStateHash());
		TMLCommand* aCurrCmd = (*i)->getCurrCommand();
		if (aCurrCmd!=0){
			//std::cout << "add curr cmd and progress Task " << (*i)->toString() << "\n";
			//_systemHash.addValue((HashValueType)aCurrCmd);
			_systemHash.addValue((HashValueType)aCurrCmd->getID());
			//std::cout << "cmd ID: " << aCurrCmd->getID() << "\n";
			//_systemHash.addValue((HashValueType)(aCurrCmd->getLength()-aCurrCmd->getProgress()));
			_systemHash.addValue((HashValueType)(aCurrCmd->getProgress()));
		}
	}
	//std::cout << " *** New channel list: ***\n";
	for(ChannelList::const_iterator i=_channelList.begin(); i != _channelList.end(); ++i){
		//std::cout << "add channel " << (*i)->toString() << "\n";
		(*i)->getStateHash(&_systemHash);
		/*if ((*i)->getSignificance()) std::cout  << (*i)->toShortString() << " has sig. content: " << (*i)->getContent() << "\n";
		else
			std::cout << (*i)->toShortString() << " is not significant in 2nd step!!!!!!!!!!!!!!\n";*/
	}
	//std::cout  << "-> Hash Value: " << _systemHash.getHash() << "\n";
	ID aRetVal;
	std::pair<StateHashSet::const_iterator,bool> aRes = _systemHashTable.insert(std::pair<HashValueType,ID>(_systemHash.getHash(),TMLTransaction::getID()));
	if (aRes.second){
		aRetVal=TMLTransaction::getID();
		TMLTransaction::incID();
		//std::cout  << "*** Added as " << aRetVal << "***\n";
		//std::cout << "STATE CREATED "<< TMLTransaction::getID() << " +++++++++++++++++++++++++++++\n";
		_knownStateReached = 0;
		if (_onKnownPath) std::cout << "YOU SHOULD NOT SEE THIS\n";
	}else{
		_onKnownPath=true;
		setStopFlag(true, "Recurring system state");   //to be restablished!!!!!!!!!!!!
		//std::cout << "KNOWN STATE REACHED "<< aRes.first->second << " ***************************\n";
		_knownStateReached= aRes.first->second;
		aRetVal = aRes.first->second;
		//std::cout  << "*** Merged with " << aRetVal << "***\n";
	}
	//return _knownStateReached;
	return aRetVal;
}
	
void SimComponents::showTaskStates(){
	static int iCount=0;
	static ParamType *aDatalen, *aStandard;
	static SchedulableDevice *aFEP, *aDeint, *aChDec;
	if (iCount==0){
		bool oIsID;
		aDatalen = getTaskByName("PacketManagerDesign__PacketGenerator")->getVariableByName("datalen", oIsID);
		aStandard = getTaskByName("PacketManagerDesign__PacketGenerator")->getVariableByName("standard", oIsID);
		aFEP = getCPUByName("FEP_0");
		aDeint = getCPUByName("Deinterleaver_0");
		aChDec = getCPUByName("ChannelDecoder_0");
	}
	
	/*std::cout<< "***** NEW entry " << iCount++ << "   Sim Time " << SchedulableDevice::getSimulatedTime() <<  " *****\n";
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		std::cout << "State of " << (*i)->toString() << ": " ;
		if ((*i)->getCurrCommand()==0 || (*i)->getCurrCommand()->getCurrTransaction()==0){
			std::cout << "has no transaction\n";
		}else{
			if ((*i)->getCurrCommand()->getCurrTransaction()->getVirtualLength()==0)
				std::cout << "has NOT runnable transaction\n";
			else
				std::cout << "has runnable transaction: " << (*i)->getCurrCommand()->getCurrTransaction()->toString() << "\n";
		}
	}
	for(BusList::const_iterator i=_busList.begin(); i != _busList.end(); ++i){
		TMLTransaction* nextBusTrans = dynamic_cast<SchedulableDevice*>(*i)->getNextTransaction();
		if (nextBusTrans!=0){
			std::cout << "Bus " << (*i)->toString() << " has next trans: " << nextBusTrans->toString() << "\n";
		}
	}*/
	std::cout << *aDatalen << "," << *aStandard << "," << SchedulableDevice::getSimulatedTime() << "," <<
	aFEP->getBusyCycles() << "," << aDeint->getBusyCycles() << "," << aChDec->getBusyCycles() << "\n";
	
}

bool SimComponents::couldCPUBeIdle(CPU* iCPU){
	int aRunFlag =0;
	for(TaskList::const_iterator i=_taskList.begin(); i != _taskList.end(); ++i){
		aRunFlag |= (*i)->hasRunnableTrans(iCPU);
		if ((aRunFlag & 2)!=0) return true;
	}
	return ((aRunFlag & 1)==0);
}
