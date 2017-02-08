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

#include <TMLCommand.h>
#include <TMLTask.h>
#include <TMLTransaction.h>
#include <CPU.h>
//#include <CommandListener.h>
#include <Parameter.h>
#include <TMLChoiceCommand.h>
#include <TMLRandomChoiceCommand.h>
#include <TMLActionCommand.h>
#include <TMLNotifiedCommand.h>
#include <TMLWaitCommand.h>
#include <SimComponents.h>
#include <TMLStopCommand.h>
#include <TMLRandomCommand.h>

std::list<TMLCommand*> TMLCommand::_instanceList;
SimComponents* TMLCommand::_simComp=0;
unsigned int TMLCommand::_branchNo=0;

TMLCommand::TMLCommand(ID iID, TMLTask* iTask, TMLLength iLength, unsigned int iNbOfNextCmds, const char* iLiveVarList, bool iCheckpoint): _ID(iID), _length(iLength), _type(NONE), _progress(0), _currTransaction(0), _task(iTask), _nextCommand(0), /*_paramFunc(iParamFunc),*/ _nbOfNextCmds(iNbOfNextCmds), _breakpoint(0), _justStarted(true), _commandStartTime(-1), _liveVarList(iLiveVarList), _checkpoint(iCheckpoint), _execTimes(0), _coveredBranchMap(0){
  if (dynamic_cast<TMLStopCommand*>(this)==0){
    _instanceList.push_back(this);
    _task->addCommand(iID, this);
    if (_nbOfNextCmds>1){
      //std::cout << "** " << this->toShortString() << " has " << _nbOfNextCmds << " branches.\n";
      _branchNo+=_nbOfNextCmds;
    }

  }
}

TMLCommand::~TMLCommand(){
  //if (_currTransaction!=0) delete _currTransaction;  NEW
  //if (_currTransaction!=0) std::cout << "transaction not yet deleted: " << getCommandStr() << std::endl;
  if (_nextCommand!=0) delete[] _nextCommand;
  _instanceList.remove(this);
  removeBreakpoint();
}

TMLCommand* TMLCommand::prepare(bool iInit){
  //Do not set _currTransaction=0 as specialized commands access the variable in the scope of the execute method (set terminated flag)
  //std::cout << "Prepare command ID: " << _ID << "\n";
  if(_length==_progress){
    TMLCommand* aNextCommand;
#ifdef STATE_HASH_ENABLED
    if (_liveVarList!=0) _task->refreshStateHash(_liveVarList);
    if(!_simComp->getOnKnownPath()){
      //_task->refreshStateHash(_liveVarList);
      if(_checkpoint){
        ID aStateID=0;
        aStateID = _simComp->checkForRecurringSystemState();
        if (_currTransaction!=0) _currTransaction->setStateID(aStateID);
      }
    }
#endif
    //std::cout << "COMMAND FINISHED!!n";
#ifdef LISTENERS_ENABLED
    NOTIFY_CMD_FINISHED(this);
    //NOTIFY_CMD_FINISHED(_currTransaction);
    if (_justStarted) NOTIFY_CMD_STARTED(this);
    //if (_justStarted) NOTIFY_CMD_STARTED(_currTransaction);
#endif
    _progress=0;
    _currTransaction=0;  //NEW!!!!!!!!!!!
    _commandStartTime=-1; //NEW
    _execTimes++;
    //std::cerr << "Prepare command, get next command 1" << std::endl;
    aNextCommand=getNextCommand();
    //std::cerr << "Prepare command, get next command 2" << std::endl;
    //std::cout << "Prepare command, to next command" << std::endl;
    _task->setCurrCommand(aNextCommand);
    if (aNextCommand==0){
      return 0;
    }else{
      //std::cout << "Prepare command, prepare next command" << std::endl;
      return aNextCommand->prepare(false);
    }
  }else{
    if (_commandStartTime==((TMLTime)-1)){
      _commandStartTime = SchedulableDevice::getSimulatedTime();
    }
    //std::cout << "Prepare next transaction TMLCmd " << _listeners.size() << std::endl;
    TMLCommand* result;
    if (iInit){
      //if (_currTransaction!=0) delete _currTransaction;   NEW!!!!!!!!!!!!!!!!!!
      if (_currTransaction==0){
        //std::cout << "currTrans==0 " << std::endl;
        result = prepareNextTransaction();  //NEW!!!!!!!!!!!!!!!!!!!!!!!!
        //std::cout << "end prepare " << std::endl;
      }else{
        //std::cout << "currTrans!=0 " << std::endl;
        result = _currTransaction->getCommand();
        //std::cout << "end get cmd " << std::endl;
      }
      if (_progress==0) _justStarted=true;
      //result=0; ///////////NEW
    }else{
      if (_progress==0){
#ifdef LISTENERS_ENABLED
        NOTIFY_CMD_ENTERED(this);
#else
#ifdef EXPLO_ENABLED
        if (dynamic_cast<IndeterminismSource*>(this)!=0) NOTIFY_CMD_ENTERED(this);
        //if (dynamic_cast<TMLRandomCommand*>(this)!=0) NOTIFY_CMD_ENTERED(this);
#endif
#endif
        _justStarted=true;
      }else{
#ifdef LISTENERS_ENABLED
        //NOTIFY_CMD_EXECUTED(this);
        NOTIFY_CMD_EXECUTED(_currTransaction);
#endif
        if (_justStarted){
#ifdef LISTENERS_ENABLED
          //NOTIFY_CMD_STARTED(_currTransaction);
          NOTIFY_CMD_STARTED(this);
#endif
          _justStarted=false;
        }
      }
      //std::cout << "Prepare next transaction" << std::endl;
      result = prepareNextTransaction(); //NEW!!!!!!!!!!!!!!!!!!!!!!!!
    }
    //TMLCommand* result = prepareNextTransaction();   NEW!!!!!!!!!!!!!!!!!
    /*#ifdef REGISTER_TRANS_AT_CPU
      if (_currTransaction!=0 && _currTransaction->getVirtualLength()!=0){
      _task->getCPU()->registerTransaction(_currTransaction,0);
      }
      #endif*/
    return result;
  }
    return 0;
  }

    TMLCommand** TMLCommand::getNextCommands(unsigned int& oNbOfCmd) const{
    //returned number is not correct for composite choice/choice commands and composite action/choice commands !!!!
    oNbOfCmd=_nbOfNextCmds;
    return _nextCommand;
  }

    std::string TMLCommand::toString() const{
    std::ostringstream outp;
    outp << _task->toString() << " len:" << _length << " progress:" << _progress << " ID:" << _ID;
    return outp.str();
  }

    void TMLCommand::setBreakpoint(GeneralListener* iBreakp){
    removeBreakpoint();
    _breakpoint=iBreakp;
    registerListener(iBreakp);
  }

    void TMLCommand::removeBreakpoint(){
    if (_breakpoint!=0){
    removeListener(_breakpoint);
    delete _breakpoint;
    _breakpoint=0;
  }
  }

    std::ostream& TMLCommand::writeObject(std::ostream& s){
    WRITE_STREAM(s,_progress);
#ifdef DEBUG_SERIALIZE
    std::cout << "Write: TMLCommand " << _ID << " progress: " << _progress << std::endl;
#endif
    /*#ifdef SAVE_BENCHMARK_VARS
      WRITE_STREAM(s, _execTimes);
      #endif*/
    return s;
  }

    std::istream& TMLCommand::readObject(std::istream& s){
    READ_STREAM(s,_progress);
#ifdef DEBUG_SERIALIZE
    std::cout << "Read: TMLCommand " << _ID << " progress: " << _progress << std::endl;
#endif
    /*#ifdef SAVE_BENCHMARK_VARS
      READ_STREAM(s, _execTimes);
      #endif*/
#ifdef STATE_HASH_ENABLED
    if (_liveVarList!=0) _task->refreshStateHash(_liveVarList);
#endif
    //std::cout << "End Read Object TMLCommand " << _ID << std::endl;
    return s;
  }

    void TMLCommand::reset(){
    _progress=0;
    //if (_currTransaction!=0) delete _currTransaction; NEW
    _currTransaction=0;
    _commandStartTime=-1;
    //_execTimes=0;
    //_stateHashes.clear();
  }

    void TMLCommand::registerGlobalListener(GeneralListener* iListener){
    std::cout << "Global cmd listener created \n";
    for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
    (*i)->registerListener(iListener);
  }
  }

    template<typename T>
      void TMLCommand::registerGlobalListenerForType(GeneralListener* iListener, TMLTask* aTask){
    //std::cout << "Global cmd listener created \n";
    for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
    if (dynamic_cast<T*>(*i)!=0 && (aTask==0 || (*i)->getTask()==aTask)) (*i)->registerListener(iListener);
  }
  }

    void TMLCommand::removeGlobalListener(GeneralListener* iListener){
    for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
    (*i)->removeListener(iListener);
  }
  }

    void TMLCommand::streamStateXML(std::ostream& s){
    for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
    s << TAG_CMDo << " id=\"" << (*i)->_ID << "\">" << TAG_EXECTIMESo << (*i)->_execTimes << TAG_EXECTIMESc << TAG_CMDc << "\n";
  }
  }

    TMLCommand* TMLCommand::getCommandByID(ID iID){
    for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
    if ((*i)->_ID == iID) return *i;
  }
    return 0;
  }

    unsigned int TMLCommand::getCmdCoverage(){
      unsigned int aCoveredCmds=0;
      for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
	if ((*i)->_execTimes>0) aCoveredCmds++; //else std::cout << "Not covered: " << (*i)->toShortString() << "\n";
      }
      //std::cout << "Total no of commands: " << _instanceList.size() << "\n";
      return aCoveredCmds * 100 / _instanceList.size();
    }
    
    unsigned int TMLCommand::getBranchCoverage(){
      unsigned int aCoveredBranchNo=0;
      //std::cout << "Total branch no: " << _branchNo << "\n";
      for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
	//if ((*i)->_nbOfNextCmds>1) std::cout << "** " << (*i)->toShortString() << " ID " << (*i)->_ID << " has " << (*i)->_nbOfNextCmds << " branches, covered map : " << (*i)->_coveredBranchMap << "\n";
	long unsigned int aCoveredBranchMap = (*i)->_coveredBranchMap;
	while (aCoveredBranchMap>0){
	  aCoveredBranchNo++;
	  aCoveredBranchMap >>=1;
	}
      }
      return (_branchNo==0)? 100: aCoveredBranchNo * 100 / _branchNo;
    }
    
    void TMLCommand::clearCoverageVars(){
      for(std::list<TMLCommand*>::const_iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
	(*i)->_execTimes=0;
	(*i)->_coveredBranchMap=0;
      }
    }
    
    template void TMLCommand::registerGlobalListenerForType<IndeterminismSource>(GeneralListener* iListener, TMLTask* aTask);
    template void TMLCommand::registerGlobalListenerForType<TMLChoiceCommand>(GeneralListener* iListener, TMLTask* aTask);
    template void TMLCommand::registerGlobalListenerForType<TMLActionCommand>(GeneralListener* iListener, TMLTask* aTask);
    template void TMLCommand::registerGlobalListenerForType<TMLNotifiedCommand>(GeneralListener* iListener, TMLTask* aTask);
    template void TMLCommand::registerGlobalListenerForType<TMLWaitCommand>(GeneralListener* iListener, TMLTask* aTask);
    
