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


TMLCommand::TMLCommand(TMLTask* iTask, TMLLength iLength, Parameter<ParamType>* iParam): _length(iLength), _progress(0), _currTransaction(0), _task(iTask), _nextCommand(0), _param(iParam){
}

TMLCommand::~TMLCommand(){
	if (_currTransaction!=0) delete _currTransaction;
	//if (_currTransaction!=0) std::cout << "transaction not yet deleted: " << getCommandStr() << std::endl;
	if (_nextCommand!=0) delete[] _nextCommand;
	if (_param!=0) delete _param;
}

TMLCommand* TMLCommand::prepare(void){
	TMLCommand* aNextCommand;
	//Do not set _currTransaction=0 as specialized commands access the variable in the scope of the execute method (set terminated flag) 
	if(_length==_progress){
		//std::cout << "COMMAND FINISHED!!!!!!!!!!!!!!!!!!!\n";
		_progress=0;
		//std::cout << "Prepare command, get next command" << std::endl;
		aNextCommand=getNextCommand();
		//std::cout << "Prepare command, to next command" << std::endl;
		if (aNextCommand==0){
			return 0;
		}else{
			_task->setCurrCommand(aNextCommand);			
			//std::cout << "Prepare command, prepare next command" << std::endl;
			return aNextCommand->prepare();
		}
	}else{
		//std::cout << "Prepare next transaction" << std::endl;
		TMLCommand* result = prepareNextTransaction();
		//if (_length==0) std::cout << "create trans with length 0: " << toString() << std::endl;
		if (_currTransaction!=0 && _currTransaction->getVirtualLength()!=0){
			_task->getCPU()->registerTransaction(_currTransaction,0);
		}
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

TMLTransaction* TMLCommand::getCurrTransaction() const{
	return _currTransaction;
}

std::string TMLCommand::toString(){
	std::ostringstream outp;	
	outp << _task->toString() << " len:" << _length << " progress:" << _progress;
	return outp.str();
}

TMLChannel* TMLCommand::getChannel() const{
	return 0;
}

bool TMLCommand::channelUnknown(){
	return false;
}

Parameter<ParamType>* TMLCommand::getParam(){
	return _param;
}

std::string TMLCommand::getCommentString(Comment* iCom){
	return "no comment available";
}

std::ostream& TMLCommand::writeObject(std::ostream& s){
	WRITE_STREAM(s,_progress);
	return s;
}

std::istream& TMLCommand::readObject(std::istream& s){
	READ_STREAM(s,_progress);
	return s;
}
