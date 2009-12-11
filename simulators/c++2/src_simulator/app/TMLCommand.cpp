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
#include <CommandListener.h>
#include <Parameter.h>
#include <TMLChoiceCommand.h>
#include <TMLActionCommand.h>
#include <TMLNotifiedCommand.h>
#include <TMLWaitCommand.h>
#include <SimComponents.h>

std::list<TMLCommand*> TMLCommand::_instanceList;
SimComponents* TMLCommand::_simComp=0;

TMLCommand::TMLCommand(unsigned int iID, TMLTask* iTask, TMLLength iLength, unsigned int iNbOfNextCmds): _ID(iID), _length(iLength), _progress(0), _currTransaction(0), _task(iTask), _nextCommand(0), /*_paramFunc(iParamFunc),*/ _nbOfNextCmds(iNbOfNextCmds), _breakpoint(0), _justStarted(true){
	_instanceList.push_back(this);
	_task->addCommand(iID, this);
}

TMLCommand::~TMLCommand(){
	if (_currTransaction!=0) delete _currTransaction;
	//if (_currTransaction!=0) std::cout << "transaction not yet deleted: " << getCommandStr() << std::endl;
	if (_nextCommand!=0) delete[] _nextCommand;
	//if (_param!=0) delete _param;
	_instanceList.remove(this);
	removeBreakpoint();
}

TMLCommand* TMLCommand::prepare(bool iInit){
	//Do not set _currTransaction=0 as specialized commands access the variable in the scope of the execute method (set terminated flag) 
	//std::cout << "Prepare command ID: " << _ID << "\n";
	if(_length==_progress){
		TMLCommand* aNextCommand;
		//std::cout << "COMMAND FINISHED!!n";
#ifdef LISTENERS_ENABLED
		NOTIFY_CMD_FINISHED(this);
		if (_justStarted) NOTIFY_CMD_STARTED(this);
#endif
		_progress=0;
		_currTransaction=0;  //NEW!!!!!!!!!!!
		//std::cout << "Prepare command, get next command" << std::endl;
		aNextCommand=getNextCommand();
		//std::cout << "Prepare command, to next command" << std::endl;
		_task->setCurrCommand(aNextCommand);
		if (aNextCommand==0){
			return 0;
		}else{
			//std::cout << "Prepare command, prepare next command" << std::endl;
			return aNextCommand->prepare(false);
		}
	}else{
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
#endif
				_justStarted=true;
			}else{
#ifdef LISTENERS_ENABLED
				NOTIFY_CMD_EXECUTED(this);
#endif
				if (_justStarted){
#ifdef LISTENERS_ENABLED
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

TMLTask* TMLCommand::getTask() const{
	return _task;
}

void TMLCommand::setNextCommand(TMLCommand** iNextCommand){
	_nextCommand=iNextCommand;
}

TMLCommand* TMLCommand::getNextCommand() const{
	return (_nextCommand==0)?0:_nextCommand[0];
}

TMLCommand** TMLCommand::getNextCommands(unsigned int& oNbOfCmd) const{
	//returned number is not correct for composite choice/choice commands and composite action/choice commands !!!!
	oNbOfCmd=_nbOfNextCmds;
	return _nextCommand;
}

TMLTransaction* TMLCommand::getCurrTransaction() const{
	return _currTransaction;
}

std::string TMLCommand::toString() const{
	std::ostringstream outp;	
	outp << _task->toString() << " len:" << _length << " progress:" << _progress;
	return outp.str();
}

TMLChannel* TMLCommand::getChannel() const{
	return 0;
}

//bool TMLCommand::channelUnknown() const{
//	return false;
//}

//ParamFuncPointer TMLCommand::getParamFuncPointer() const{
//	return 0;
//}

void TMLCommand::setParams(Parameter<ParamType>& ioParam){
}

#ifdef ADD_COMMENTS
std::string TMLCommand::getCommentString(Comment* iCom) const{
	return "no comment available";
}
#endif

void TMLCommand::setBreakpoint(CommandListener* iBreakp){
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
	return s;
}

std::istream& TMLCommand::readObject(std::istream& s){
	READ_STREAM(s,_progress);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLCommand " << _ID << " progress: " << _progress << std::endl;
#endif
	//std::cout << "End Read Object TMLCommand " << _ID << std::endl;
	return s;
}

void TMLCommand::reset(){
	_progress=0;
	if (_currTransaction!=0) delete _currTransaction;
	_currTransaction=0;
}

void TMLCommand::registerGlobalListener(CommandListener* iListener){
	std::cout << "Global cmd listener created \n";
	for(std::list<TMLCommand*>::iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
		(*i)->registerListener(iListener);
	}
}

template<typename T>
void TMLCommand::registerGlobalListenerForType(CommandListener* iListener, TMLTask* aTask){
	//std::cout << "Global cmd listener created \n";
	for(std::list<TMLCommand*>::iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
		if (dynamic_cast<T*>(*i)!=0 && (aTask==0 || (*i)->getTask()==aTask)) (*i)->registerListener(iListener);
	}
}

void TMLCommand::removeGlobalListener(CommandListener* iListener){
	for(std::list<TMLCommand*>::iterator i=_instanceList.begin(); i != _instanceList.end(); ++i){
		(*i)->removeListener(iListener);
	}
}

unsigned int TMLCommand::getID() const{
	return _ID;
}

TMLLength TMLCommand::getProgress() const{
	return _progress;
}

void TMLCommand::setSimComponents(SimComponents* iSimComp){
	_simComp=iSimComp;
}

template void TMLCommand::registerGlobalListenerForType<TMLChoiceCommand>(CommandListener* iListener, TMLTask* aTask);
template void TMLCommand::registerGlobalListenerForType<TMLActionCommand>(CommandListener* iListener, TMLTask* aTask);
template void TMLCommand::registerGlobalListenerForType<TMLNotifiedCommand>(CommandListener* iListener, TMLTask* aTask);
template void TMLCommand::registerGlobalListenerForType<TMLWaitCommand>(CommandListener* iListener, TMLTask* aTask);

