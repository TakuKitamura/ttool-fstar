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

#include <TMLTask.h>
#include <TMLCommand.h>
#include <TMLStopCommand.h>
#include <CPU.h>

unsigned int TMLTask::_instanceCount=1;

TMLTask::TMLTask(ID iID, Priority iPriority, std::string iName, CPU* iCPU): WorkloadSource(iPriority), _ID(iID), _name(iName), _endLastTransaction(0), _currCommand(0), _firstCommand(0), _cpu(iCPU), _comment(0), _busyCycles(0), _CPUContentionDelay(0), _noCPUTransactions(0), _justStarted(true), _myInstance(_instanceCount) {
	_cpu->registerTask(this);
#ifdef ADD_COMMENTS
	_commentList.reserve(BLOCK_SIZE);
#endif
	_transactList.reserve(BLOCK_SIZE);
	_instanceCount++;
}

TMLTask::~TMLTask(){
#ifdef ADD_COMMENTS
	for(CommentList::iterator i=_commentList.begin(); i != _commentList.end(); ++i){
		delete *i;
	}
#endif
	if (_comment!=0) delete [] _comment;
}

Priority TMLTask::getPriority() const{
	return _priority;
}

TMLTime TMLTask::getEndLastTransaction() const{
	return _endLastTransaction;
}

TMLCommand* TMLTask::getCurrCommand() const{
	return _currCommand;
}

void TMLTask::setCurrCommand(TMLCommand* iCurrCommand){
	//std::cout << _name << "currentcommand: " << iCurrCommand->toString() << std::endl;
	_currCommand=iCurrCommand;
}

CPU* TMLTask::getCPU() const{
	return _cpu;
}

std::string TMLTask::toString() const{
	return _name;
}

std::string TMLTask::toShortString() const{
	std::ostringstream outp;
	outp << "ta" << _ID;
	return outp.str();
}

ID TMLTask::getID() const{
	return _ID;
}

#ifdef ADD_COMMENTS
void TMLTask::addComment(Comment* iComment){
	_commentList.push_back(iComment);
}

std::string TMLTask::getNextComment(bool iInit, Comment*& oComment){
	if (iInit) _posCommentList=_commentList.begin();
	if (_posCommentList == _commentList.end()){
		//std::cout << "ret0\n";
		oComment=0;
		return std::string("no more comment");
	}
	//std::cout << "NON0\n";
	oComment=*_posCommentList;
	_posCommentList++;
	return ((oComment->_command==0)?_comment[oComment->_actionCode]:oComment->_command->getCommentString(oComment));
}
#endif

void TMLTask::addTransaction(TMLTransaction* iTrans){
	_transactList.push_back(iTrans);
	_endLastTransaction=iTrans->getEndTime();
	_busyCycles+=iTrans->getOperationLength();
	//FOR_EACH_TASKLISTENER (*i)->transExecuted(iTrans);
#ifdef LISTENERS_ENABLED
	NOTIFY_TASK_TRANS_EXECUTED(iTrans);
#endif
	if (_justStarted){
#ifdef LISTENERS_ENABLED
		NOTIFY_TASK_STARTED(iTrans);
#endif
		_justStarted=false;	
	}
	if(iTrans->getChannel()==0){
		_noCPUTransactions++;
		_CPUContentionDelay+=iTrans->getStartTime()-iTrans->getRunnableTime();
	}
}

TMLTime TMLTask::getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans){
	std::ostringstream outp;
	if (iInit){
		//std::cout << "Init" << std::endl; 
		_posTrasactListVCD=_transactList.begin();
		//std::cout << "Init2" << std::endl; 
		_previousTransEndTime=0;
		_vcdOutputState=END_TRANS;
	}
	if (_posTrasactListVCD == _transactList.end()){
		//if (iInit || _transactList.back()->getTerminatedFlag()){
			//outp << VCD_PREFIX << vcdValConvert(TERMINATED) << " ta" << _ID;
		//}else{
			//outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << " ta" << _ID;
		//}
		//if (iInit || (! _transactList.back()->getTerminatedFlag())){
		if (iInit || dynamic_cast<TMLStopCommand*>(_currCommand)==0){
			outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << " ta" << _ID;	
		}else{
			outp << VCD_PREFIX << vcdValConvert(TERMINATED) << " ta" << _ID;
		}
		oSigChange=outp.str();
		oNoMoreTrans=true;
		return _previousTransEndTime;
	}else{
		//std::cout << "VCD out trans: " << (*_posTrasactListVCD)->toShortString() << std::endl;
		TMLTransaction* aCurrTrans=*_posTrasactListVCD;
		oNoMoreTrans=false;
		switch (_vcdOutputState){
			case END_TRANS:
				if (aCurrTrans->getRunnableTime()==_previousTransEndTime){
					outp << VCD_PREFIX << vcdValConvert(RUNNABLE) << " ta" << _ID;
					_vcdOutputState=START_TRANS;
				}else{
					outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << " ta" << _ID;
					if (aCurrTrans->getRunnableTime()==aCurrTrans->getStartTimeOperation()){
						_vcdOutputState=START_TRANS;
					}else{
						_vcdOutputState=BETWEEN_TRANS;
					}
				}
				oSigChange=outp.str();
				return _previousTransEndTime;
			break;
			case BETWEEN_TRANS:
				outp << VCD_PREFIX << vcdValConvert(RUNNABLE) << " ta" << _ID;
				oSigChange=outp.str();
				_vcdOutputState=START_TRANS;
				return aCurrTrans->getRunnableTime();
			break;
			case START_TRANS:
				outp << VCD_PREFIX << vcdValConvert(RUNNING) << " ta" << _ID;
				oSigChange=outp.str();
				do{
					_previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
					_posTrasactListVCD++;
				}while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime);
				_vcdOutputState=END_TRANS;
				return aCurrTrans->getStartTimeOperation();
			break;
		}
	}
	return 0;
}

std::ostream& TMLTask::writeObject(std::ostream& s){
	ID aCurrCmd;
	WRITE_STREAM(s,_endLastTransaction);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: TMLTask " << _name << " endLastTransaction: " << _endLastTransaction << std::endl;
#endif
	if (_currCommand==0){
		aCurrCmd=0;
		WRITE_STREAM(s,aCurrCmd);
#ifdef DEBUG_SERIALIZE
		std::cout << "Write: TMLTask " << _name << " aCurrCmd: " << aCurrCmd << std::endl;
#endif
	}else{
		aCurrCmd=_currCommand->getID();
		WRITE_STREAM(s,aCurrCmd);
#ifdef DEBUG_SERIALIZE
		std::cout << "Write: TMLTask " << _name << " aCurrCmd: " << aCurrCmd << std::endl;
#endif
		_currCommand->writeObject(s);
	}
#ifdef SAVE_BENCHMARK_VARS
	WRITE_STREAM(s, _busyCycles);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: TMLTask " << _name << " busyCycles: " << _busyCycles << std::endl;
#endif
	WRITE_STREAM(s, _CPUContentionDelay);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: TMLTask " << _name << " CPUContentionDelay: " << _CPUContentionDelay << std::endl;
#endif
	WRITE_STREAM(s, _noCPUTransactions);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: TMLTask " << _name << " noCPUTransactions: " << _noCPUTransactions << std::endl;
#endif
#endif
	return s;
}

std::istream& TMLTask::readObject(std::istream& s){
	ID aCurrCmd;
	//_previousTransEndTime=0; _busyCycles=0; _CPUContentionDelay=0; _noCPUTransactions=0;
	READ_STREAM(s, _endLastTransaction);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLTask " << _name << " endLastTransaction: " << _endLastTransaction << std::endl;
#endif
	READ_STREAM(s, aCurrCmd);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLTask " << _name << " aCurrCmd: " << aCurrCmd << std::endl;
#endif
	if (aCurrCmd==0){
		_currCommand=0;
	}else{
		//std::cout << "cmd ID: " << aCurrCmd << std::endl;
		_currCommand=getCommandByID(aCurrCmd);
		//std::cout << "cmd adr: " << _currCommand << std::endl;
		//std::cout << "before read cmd " << std::endl;
		 _currCommand->readObject(s);
		//_currCommand->prepare();
	}
	//std::cout << "End Read Object TMLTask " << _name << std::endl;
#ifdef SAVE_BENCHMARK_VARS
	READ_STREAM(s, _busyCycles);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLTask " << _name << " busyCycles: " << _busyCycles << std::endl;
#endif
	READ_STREAM(s, _CPUContentionDelay);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLTask " << _name << " CPUContentionDelay: " << _CPUContentionDelay << std::endl;
#endif
	READ_STREAM(s, _noCPUTransactions);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLTask " << _name << " noCPUTransactions: " << _noCPUTransactions << std::endl;
#endif
#endif
	_justStarted=false;
	return s;
}

void TMLTask::streamBenchmarks(std::ostream& s) const{
	s << TAG_TASKo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl;
	s << TAG_EXTIMEo << _busyCycles << TAG_EXTIMEc;
	if (_noCPUTransactions!=0) s << TAG_CONTDELo << ">" << (static_cast<float>(_CPUContentionDelay)/static_cast<float>(_noCPUTransactions)) << TAG_CONTDELc;
	s << TAG_TSKSTATEo;
	//unsigned int aState=getState();
	switch (getState()){
		case RUNNING:
			s << "running";
			break;
		case RUNNABLE:
			s << "runnable";
			break;
		case SUSPENDED:
			s << "suspended";
			break;
		case TERMINATED:
			s << "terminated";
			break;
		case UNKNOWN:
			s << "unknown";
	}
	s << TAG_TSKSTATEc << TAG_TASKc << std::endl; 
	//std::cout << "Stream benchmarks task finished\n";
}

void TMLTask::reset(){
	//std::cout << "task reset" << std::endl;
	_endLastTransaction=0;
	if (_currCommand!=0) _currCommand->reset();
	_currCommand=_firstCommand;
	if (_currCommand!=0) _currCommand->reset();
#ifdef ADD_COMMENTS
	_commentList.clear();
#endif
	_transactList.clear();
	_busyCycles=0;
	_CPUContentionDelay=0;
	_noCPUTransactions=0;
	_justStarted=true;
}

ParamType* TMLTask::getVariableByName(std::string& iVarName ,bool& oIsId){
	if (iVarName[0]>='0' && iVarName[0]<='9'){
		oIsId=true;
		return getVariableByID(StringToNum<ID>(iVarName));
	}
	oIsId=false;
	return _varLookUpName[iVarName.c_str()];
}

ParamType* TMLTask::getVariableByID(ID iVarID){
	return _varLookUpID[iVarID];
}

void TMLTask::addCommand(ID iID, TMLCommand* iCmd){
	_commandHash[iID]=iCmd;
}

TMLCommand* TMLTask::getCommandByID(ID iID){
	return _commandHash[iID];
}

void TMLTask::streamStateXML(std::ostream& s) const{
	streamBenchmarks(s);
}

VariableLookUpTableID::const_iterator TMLTask::getVariableIteratorID(bool iEnd) const{
	return (iEnd)?_varLookUpID.end():_varLookUpID.begin();
}

VariableLookUpTableName::const_iterator TMLTask::getVariableIteratorName(bool iEnd) const{
	return (iEnd)?_varLookUpName.end():_varLookUpName.begin();
}

void TMLTask::finished(){
	_justStarted=true;
#ifdef LISTENERS_ENABLED
	if (!_transactList.empty()) NOTIFY_TASK_FINISHED(_transactList.front());
#endif
}

unsigned int TMLTask::getState() const{
	if (!_transactList.empty() && _transactList.back()->getEndTime()==SchedulableDevice::getSimulatedTime()){
		return RUNNING;
	}else{
		if (_currCommand==0) return TERMINATED;
		if (_currCommand->getCurrTransaction()==0){
			if (dynamic_cast<TMLStopCommand*>(_currCommand)==0)
				return UNKNOWN;
			else
				return TERMINATED;
		}else{
				if (_currCommand->getCurrTransaction()->getRunnableTime()>=SchedulableDevice::getSimulatedTime())
					return SUSPENDED;
				else
					return RUNNABLE;
		
		}
	}	 
}

TMLTransaction* TMLTask::getNextTransaction() const{
	//std::cout << "Task::getNextTransaction\n";
	return (_currCommand==0)?0:_currCommand->getCurrTransaction();
}

unsigned int TMLTask::getInstanceNo(){
	return _myInstance;
}
