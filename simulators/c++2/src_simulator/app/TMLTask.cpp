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
#include <FPGA.h>

//#define RESET_SCHEDULING {_isScheduled=false;if (_noOfCPUs>1) _currentCPU=0;}

unsigned int TMLTask::_instanceCount=1;

TMLTask::TMLTask(ID iID, Priority iPriority, std::string iName, CPU** iCPU, unsigned int iNoOfCPUs, bool isDaemon): WorkloadSource(iPriority), _ID(iID), _name(iName), _endLastTransaction(0), _currCommand(0), _firstCommand(0), _currentCPU(0), _cpus(iCPU), _noOfCPUs(iNoOfCPUs), _isDaemon(isDaemon), _nextCellIndex(0), _comment(0), _busyCycles(0), _CPUContentionDelay(0), _noCPUTransactions(0), _justStarted(true), _myInstance(_instanceCount), /*_isScheduled(false),*/ _stateHash(0, 30) , _liveVarList(0), _hashInvalidated(true), _isFirstTranExecuted(false), _isCPUExist(true), _isFPGAExist(false){
	for (unsigned int i=0; i< _noOfCPUs; i++)
		_cpus[i]->registerTask(this);
#ifdef ADD_COMMENTS
	_commentList.reserve(BLOCK_SIZE_TRANS);
#endif
	_transactList.reserve(BLOCK_SIZE_TRANS);
	_instanceCount++;
	if (_noOfCPUs==1) _currentCPU = _cpus[0];
}

TMLTask::TMLTask(ID iID, Priority iPriority, std::string iName, FPGA** iFPGA, unsigned int iNoOfFPGAs, bool isDaemon): WorkloadSource(iPriority), _ID(iID), _name(iName), _endLastTransaction(0), _currCommand(0), _firstCommand(0), _currentFPGA(0), _fpgas(iFPGA), _noOfFPGAs(iNoOfFPGAs), _isDaemon(isDaemon), _nextCellIndex(0),_comment(0), _busyCycles(0), _FPGAContentionDelay(0), _noFPGATransactions(0), _justStarted(true), _myInstance(_instanceCount), _stateHash(0, 30) , _liveVarList(0), _hashInvalidated(true), _isFirstTranExecuted(false), _isCPUExist(false), _isFPGAExist(true){
	for (unsigned int i=0; i< _noOfFPGAs; i++)
		_fpgas[i]->registerTask(this);
#ifdef ADD_COMMENTS
	_commentList.reserve(BLOCK_SIZE_TRANS);
#endif
	_transactList.reserve(BLOCK_SIZE_TRANS);
	_instanceCount++;
	if (_noOfFPGAs==1) _currentFPGA = _fpgas[0];
}

TMLTask::~TMLTask(){
#ifdef ADD_COMMENTS
	for(CommentList::iterator i=_commentList.begin(); i != _commentList.end(); ++i){
		delete *i;
	}
#endif
	if (_comment!=0) delete [] _comment;
	if(_isCPUExist) delete[] _cpus; // free the allocation of cpu array
	if(_isFPGAExist) delete[] _fpgas; // free the allocation of fpga array
}

std::string TMLTask::toShortString() const{
	std::ostringstream outp;
	outp << "ta" << _ID;
	return outp.str();
}

#ifdef ADD_COMMENTS
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
	//RESET_SCHEDULING; 
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

void TMLTask::getNextSignalChange(bool iInit, SignalChangeData* oSigData){
	//std::ostringstream outp;
	if (iInit){
		_posTrasactListVCD=_transactList.begin();
		_previousTransEndTime=0;
		_vcdOutputState=END_TRANS;
	}
	if (_posTrasactListVCD == _transactList.end()){
		if (iInit || dynamic_cast<TMLStopCommand*>(_currCommand)==0){
			//outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << "ta" << _ID;
			new (oSigData) SignalChangeData(SUSPENDED, _previousTransEndTime, this);
		}else{
			//outp << VCD_PREFIX << vcdValConvert(TERMINATED) << "ta" << _ID;
			new (oSigData) SignalChangeData(TERMINATED, _previousTransEndTime, this);
		}
		//oSigChange=outp.str();
		//oNoMoreTrans=true;
		//return _previousTransEndTime;
	}else{
		TMLTransaction* aCurrTrans=*_posTrasactListVCD;
		//oNoMoreTrans=false;
		switch (_vcdOutputState){
		  	case START_TRANS:
				//outp << VCD_PREFIX << vcdValConvert(RUNNING) << "ta" << _ID;
				//oSigChange=outp.str();
				do{
					_previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
					_posTrasactListVCD++;
				}while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime);
				_vcdOutputState=END_TRANS;
				//return aCurrTrans->getStartTimeOperation();
				if( aCurrTrans->getStartTimeOperation() ){
				  new (oSigData) SignalChangeData(RUNNING, aCurrTrans->getStartTimeOperation(), this);
				  break;
				}
			case END_TRANS:
				if (aCurrTrans->getRunnableTime()==_previousTransEndTime){
					//outp << VCD_PREFIX << vcdValConvert(RUNNABLE) << "ta" << _ID;
					_vcdOutputState=START_TRANS;
					new (oSigData) SignalChangeData(RUNNABLE, _previousTransEndTime, this);
				}else{
					//outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << "ta" << _ID;
					new (oSigData) SignalChangeData(SUSPENDED, _previousTransEndTime, this);
					if (aCurrTrans->getRunnableTime()==aCurrTrans->getStartTimeOperation()){
						_vcdOutputState=START_TRANS;
						
					}else{
						_vcdOutputState=BETWEEN_TRANS;
					}
				}
				//oSigChange=outp.str();
				//return _previousTransEndTime;
			break;
			case BETWEEN_TRANS:
				//outp << VCD_PREFIX << vcdValConvert(RUNNABLE) << "ta" << _ID;
				//oSigChange=outp.str();
				_vcdOutputState=START_TRANS;
				//return aCurrTrans->getRunnableTime();
				new (oSigData) SignalChangeData(RUNNABLE, aCurrTrans->getRunnableTime(), this);
			break;
		
		}
	}
	//return 0;
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
	if (_noFPGATransactions!=0) s << TAG_CONTDELo << ">" << (static_cast<float>(_FPGAContentionDelay)/static_cast<float>(_noFPGATransactions)) << TAG_CONTDELc;
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
	_hashInvalidated=true;
	//RESET_SCHEDULING;
}

ParamType* TMLTask::getVariableByName(const std::string& iVarName ,bool& oIsId){
	if (iVarName[0]>='0' && iVarName[0]<='9'){
		oIsId=true;
		return getVariableByID(StringToNum<ID>(iVarName));
	}
	oIsId=false;
	return _varLookUpName[iVarName.c_str()];
}

void TMLTask::finished(){
	_justStarted=true;
#ifdef LISTENERS_ENABLED
	if (!_transactList.empty()) NOTIFY_TASK_FINISHED(_transactList.front());
#endif
}

unsigned int TMLTask::getState() const{
	/*if (!_transactList.empty() && _transactList.back()->getEndTime()==SchedulableDevice::getSimulatedTime()){
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
	}*/
	if(_endLastTransaction == 0) { // when user starts the simulator the status should be RUNABLE, not TERMINATED
	    return RUNNABLE;
	} else if (_currCommand==0 || _currCommand->getCurrTransaction()==0 || dynamic_cast<TMLStopCommand*>(_currCommand)!=0){
		return TERMINATED;
	} else if (_currCommand->getCurrTransaction()->getVirtualLength()==0){
		return SUSPENDED;
	} else if (_isCPUExist && _currentCPU !=0 && _currentCPU->SchedulableDevice::getNextTransaction()==_currCommand->getCurrTransaction()){
		return RUNNING;
	} else if (_isFPGAExist && _currentFPGA !=0 && _currentFPGA->SchedulableDevice::getNextTransaction()==_currCommand->getCurrTransaction()){
		return RUNNING;
	} else {
		return RUNNABLE;
	}
	return UNKNOWN;
}

TMLTransaction* TMLTask::getNextTransaction(TMLTime iEndSchedule) const{
  //std::cout<<"TMLTask get next trans"<<std::endl;
	return (_currCommand==0)?0:_currCommand->getCurrTransaction();
	//return (_currCommand==0 || _isScheduled)?0:_currCommand->getCurrTransaction();
}

/*void TMLTask::transWasScheduled(SchedulableDevice* iCPU){
	_isScheduled=true;
	if (_noOfCPUs>1) _currentCPU = dynamic_cast<CPU*>(iCPU);
}

void TMLTask::resetScheduledFlag(){
	RESET_SCHEDULING;
}

void TMLTask::setRescheduleFlagForCores(){
	RESET_SCHEDULING;
	for (unsigned int i=0; i< _noOfCPUs; i++){
		//std::cout << "in Task " << _name << " next CPU\n";
		_cpus[i]->setRescheduleFlag();
	}
}*/

void TMLTask::schedule2TXT(std::ostream& myfile) const{
	myfile << "========= Scheduling for device: "<< _name << " =========\n" ;
	for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
		myfile << (*i)->toShortString() << std::endl;
	}
}

void TMLTask::refreshStateHash(const char* iLiveVarList){
	//if (iLiveVarList!=0){
		//_hashInvalidated = (_liveVarList!=iLiveVarList);
		_hashInvalidated = true;
		_liveVarList = iLiveVarList;
	//}
}

int TMLTask::hasRunnableTrans(CPU* iCPU){
	bool aIsMappedOnCPU=false;
	for (unsigned int i=0; i< _noOfCPUs; i++){
		aIsMappedOnCPU |= (_cpus[i]==iCPU);
	}
	if (!aIsMappedOnCPU || _currCommand==0) return 0;
	TMLTransaction* aCurrTrans = _currCommand->getCurrTransaction();
	if (aCurrTrans==0 || aCurrTrans->getVirtualLength()==0) return 0;
	if (aCurrTrans->getChannel()!=0 && aCurrTrans->getChannel()->mappedOnBus()) return 2;
	//std::cout << "There would be: " << _currCommand->getCurrTransaction()->toString() << "\n";
	return 1;
}

int TMLTask::hasRunnableTrans(FPGA* iFPGA){
	bool aIsMappedOnFPGA=false;
	for (unsigned int i=0; i< _noOfFPGAs; i++){
		aIsMappedOnFPGA |= (_fpgas[i]==iFPGA);
	}
	if (!aIsMappedOnFPGA || _currCommand==0) return 0;
	TMLTransaction* aCurrTrans = _currCommand->getCurrTransaction();
	if (aCurrTrans==0 || aCurrTrans->getVirtualLength()==0) return 0;
	if (aCurrTrans->getChannel()!=0 && aCurrTrans->getChannel()->mappedOnBus()) return 2;
	//std::cout << "There would be: " << _currCommand->getCurrTransaction()->toString() << "\n";
	return 1;
}

void TMLTask::writeHTMLColumn(	std::ofstream& myfile,
				const unsigned int colSpan,
				const std::string cellClass ) {
	writeHTMLColumn( myfile, colSpan, cellClass, "" );
}



void TMLTask::writeHTMLColumn(	std::ofstream& myfile,
				const unsigned int colSpan,
				const std::string cellClass,
				const std::string title ) {
	writeHTMLColumn( myfile, colSpan, cellClass, title, "", true );
}

void TMLTask::writeHTMLColumn(	std::ofstream& myfile,
				const unsigned int colSpan,
				const std::string cellClass,
				const std::string title,
				const std::string content) {
	writeHTMLColumn( myfile, colSpan, cellClass, title, content, true );
}


void TMLTask::writeHTMLColumn(	std::ofstream& myfile,
				const unsigned int colSpan,
				const std::string cellClass,
				const std::string title,
				const std::string content,
				const bool endline ) {
	std::string begLine( START_TD );

	if ( !title.empty() ) {
		begLine.append( " title=\"" );
		begLine.append( title );
		begLine.append( "\"" );
	}

	begLine.append( " class=\"" );

	if ( colSpan == 1) {
		begLine.append( cellClass );
		begLine.append( "\"" );
		myfile << begLine << ">" << END_TD;

		if ( endline ) {
			myfile << std::endl;
		}
	}
	else {
		int actualLength = colSpan;
		bool first = true;
		bool last = false;

		do {
			last = actualLength <= MAX_COL_SPAN;
			std::string clasVal( cellClass );

			if ( first && !last ) {
				clasVal.append( "first" );
				first = false;
			}
			else if ( last && !first ) {
				clasVal.append( "last" );
			}
			else if ( !last && !first ) {
				clasVal.append( "mid" );
			}

			clasVal.append( "\"" );

			std::string colSpan( " colspan=\"" );
			std::ostringstream spanVal;
			spanVal << std::min( MAX_COL_SPAN, actualLength ) <<  "\"";
			colSpan.append( spanVal.str() );

			myfile << begLine << clasVal << colSpan << ">" << content << END_TD;

			if ( last && endline ) {
				myfile << std::endl;
			}

			actualLength -= MAX_COL_SPAN;
		} while ( !last );
	}
}

std::string TMLTask::determineHTMLCellClass( 	std::map<TMLTask*, std::string> &taskColors,
														TMLTask* task,
														unsigned int &nextColor ) {
	std::map<TMLTask*, std::string>::const_iterator it = taskColors.find( task );

	if ( it == taskColors.end() ) {
		unsigned int aColor = nextColor % NB_HTML_COLORS;
		std::ostringstream cellClass;
		cellClass << "t" << aColor;
		taskColors[ task ] = cellClass.str();
		nextColor++;
	}

	return taskColors[ task ];
}

void TMLTask::removeTrans(int numberOfTrans) {
    if (numberOfTrans == 1) {
        _transactList.clear();
    }
}

void TMLTask::schedule2HTML(std::ofstream& myfile) const {    
  //	myfile << "<h2><span>Scheduling for device: "<< _name << "</span></h2>" << std::endl;
  myfile << SCHED_HTML_DIV << SCHED_HTML_BOARD;
  myfile << _name  << END_TD << "</tr>" << std::endl;
  myfile << SCHED_HTML_JS_TABLE_END << std::endl;
  myfile << SCHED_HTML_BOARD2 << std::endl;
  if ( _transactList.size() == 0 ) {
    myfile << "<h4>Task never executed</h4>" << std::endl;
    myfile << SCHED_HTML_JS_TABLE_END << std::endl << SCHED_HTML_JS_CLEAR << std::endl;
  }
  else {
    //myfile << "<table>" << std::endl << "<tr>";
    myfile << "<tr>";
    std::map<TMLTask*, std::string> taskCellClasses;
    unsigned int nextCellClassIndex = 0;
    TMLTime aCurrTime = 0;
    unsigned int tempReduce = 0;
    std::vector<unsigned int> listScale;
    std::vector<unsigned int> listScaleTime;
    listScale.push_back(0);
    listScaleTime.push_back(0);
    bool changeCssClass = false;
    TMLTransaction* checkLastTime = _transactList.back();
    for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
      
      //if( (*i)->getTransactCoreNumber() == this->_cycleTime ){
      TMLTransaction* aCurrTrans = *i;
      unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
    bool isBlankTooBig = false;
    std::ostringstream tempString;
    int tempBlanks;
    if(checkLastTime->getEndTime() >= 250 && aBlanks > 10) {
        int newBlanks = 10;
        tempBlanks = aBlanks;
        tempReduce += aBlanks - newBlanks;
        aBlanks = newBlanks;
        isBlankTooBig = true;
        changeCssClass = true;
    }
	if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
	    listScale.push_back(aBlanks+1);
	    tempString << tempBlanks+1;
	    if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime()+1);
        }
	    if (isBlankTooBig){
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
	    } else {
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time" );
	    }
	}
	else if ( aBlanks > 0 ){
	    listScale.push_back(aBlanks);
	    tempString << tempBlanks;
	    if(aCurrTrans->getStartTime() > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime());
        }
	    if (isBlankTooBig){
            writeHTMLColumn( myfile, aBlanks, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
        } else {
            writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
        }
	}

      unsigned int aLength = aCurrTrans->getPenalties();

      if ( aLength != 0 ) {
	std::ostringstream title;
    listScaleTime.push_back(listScaleTime.back()+aLength);
    if(checkLastTime->getEndTime() >= 250 && aLength > 10){
      tempReduce += aLength - 10;
      aLength = 10;
    }
    listScale.push_back(aLength);
	title << "idle:" << aCurrTrans->getIdlePenalty() << " switching penalty:" << aCurrTrans->getTaskSwitchingPenalty();
	writeHTMLColumn( myfile, aLength, "not", title.str() );
      }

      aLength = aCurrTrans->getOperationLength();

      // Issue #4
      TMLTask* task = aCurrTrans->getCommand()->getTask();
      const std::string cellClass = determineHTMLCellClass( taskCellClasses, task, nextCellClassIndex );
      std::string aCurrTransName=aCurrTrans->toShortString();
      unsigned int indexTrans=aCurrTransName.find_first_of(":");
      std::string aCurrContent=aCurrTransName.substr(indexTrans+1,2);
      if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
        if(checkLastTime->getEndTime() >= 250 && aLength > 10){
          tempReduce += aLength - 10;
          aLength = 10;
        }
        writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString(), aCurrContent );
        listScale.push_back(aLength);
        if(aCurrTrans->getStartTime() > listScaleTime.back()){
           listScaleTime.push_back(aCurrTrans->getStartTime());
        }
        if(aCurrTrans->getEndTime() > listScaleTime.back()){
          listScaleTime.push_back(aCurrTrans->getEndTime());
        }
      }

      aCurrTime = aCurrTrans->getEndTime();
      // }
    }
		

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength < aCurrTime - tempReduce; aLength++ ) {
      myfile << "<th></th>";
    }

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
      std::ostringstream spanVal;
      if(aLength < listScaleTime.size())
        spanVal << listScaleTime[aLength];
      else
        spanVal << "";
      if(aLength+1 >= listScale.size()){

        if(changeCssClass){
            writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
        } else
            writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
      }else {
        if(changeCssClass){
            writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
        } else
            writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
        }
      //myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
    }

    myfile << "</tr>" << std::endl << "</table>" << std::endl << SCHED_HTML_JS_DIV_END << std::endl;
    myfile << SCHED_HTML_JS_CLEAR << std::endl;
    
    //  myfile << "</tr>" << std::endl << "</table>" << std::endl << "<table>" << std::endl << "<tr>";
    /* for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
      TMLTask* task = (*taskColIt).first;
      // Unset the default td max-width of 5px. For some reason setting the max-with on a specific t style does not work
      myfile << "<td class=\"" << taskCellClasses[ task ] << "\"></td><td style=\"max-width: unset;\">" << task->toString() << "</td><td class=\"space\"></td>";
      }*/

    //myfile << "</tr>" << std::endl;

#ifdef ADD_COMMENTS
    bool aMoreComments = true, aInit = true;
    Comment* aComment;

    while ( aMoreComments ) {
      aMoreComments = false;
      myfile << "<tr>";

      for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
	//for(TaskList::const_iterator j=_taskList.begin(); j != _taskList.end(); ++j){
	TMLTask* task = (*taskColIt).first;
	std::string aCommentString = task->getNextComment( aInit, aComment );

	if ( aComment == 0 ) {
	  myfile << "<td></td><td></td><td class=\"space\"></td>";
	}
	else {
	  replaceAll(aCommentString,"<","&lt;");
	  replaceAll(aCommentString,">","&gt;");
	  aMoreComments = true;
	  myfile << "<td style=\"max-width: unset;\">" << aComment->_time << "</td><td><pre>" << aCommentString << "</pre></td><td class=\"space\"></td>";
	}
      }

      aInit = false;
      myfile << "</tr>" << std::endl;
    }
#endif
    // myfile << "</table>" << std::endl;
  }
}
