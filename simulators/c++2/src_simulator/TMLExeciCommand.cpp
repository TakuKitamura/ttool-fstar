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

#include <TMLExeciCommand.h>
#include <TMLTask.h>
#include <TMLTransaction.h>


TMLExeciCommand::TMLExeciCommand(TMLTask* iTask, LengthFuncPointer iLengthFunc, unsigned int iType): TMLCommand(iTask,1,0), _lengthFunc(iLengthFunc), _type(iType){
}

//TMLExeciCommand::TMLExeciCommand(TMLTask* iTask, const TMLLength& iMinLen, const TMLLength& iMaxLen, unsigned int iType):TMLCommand(iTask,1,0), _pMaxLen(&_cMaxLen), _cMaxLen(iMaxLen), _pMinLen(&_cMinLen), _cMinLen(iMinLen), _type(iType){
//}

//TMLExeciCommand::TMLExeciCommand(TMLTask* iTask, const TMLLength& iMinLen, TMLLength& iMaxLen, unsigned int iType):TMLCommand(iTask,1,0),_pMaxLen(&iMaxLen),_cMaxLen(0), _pMinLen(&_cMinLen), _cMinLen(iMinLen), _type(iType){
//}

//TMLExeciCommand::TMLExeciCommand(TMLTask* iTask, TMLLength& iMinLen, const TMLLength& iMaxLen, unsigned int iType):TMLCommand(iTask,1,0), _pMaxLen(&_cMaxLen), _cMaxLen(iMaxLen), _pMinLen(&iMinLen), _cMinLen(0), _type(iType){
//}

//TMLExeciCommand::TMLExeciCommand(TMLTask* iTask, TMLLength& iMinLen, TMLLength& iMaxLen, unsigned int iType):TMLCommand(iTask,1,0),_pMaxLen(&iMaxLen),_cMaxLen(0), _pMinLen(&iMinLen), _cMinLen(0), _type(iType){
//}

void TMLExeciCommand::execute(){
	//std::cout << "Execi execute get virt len " << _currTransaction;
	//std::cout << _currTransaction->toShortString() << std::endl;
	_progress+=_currTransaction->getVirtualLength();
	//std::cout << "Execi execute set end" << std::endl;
	_task->setEndLastTransaction(_currTransaction->getEndTime());
	//_randLen=0;
	//std::cout << "Execi execute prepare" << std::endl;
	if (!prepare()) _currTransaction->setTerminatedFlag();
	if (_progress==0) _currTransaction=0;
}

bool TMLExeciCommand::prepareNextTransaction(){
	//std::cout << "ExeciCommand prepare " << toString() << std::endl;
	//if (_randLen==0){
	if (_progress==0) _length = (_task->*_lengthFunc)();
		//_cLength=(*_pMaxLen==0)?*_pMinLen : (unsigned int) myrand((int)*_pMinLen,(int)*_pMaxLen);
		//_pLength=&_cLength;
		//std::cout << "Length is random ----------------!-!-!------------- "<< _cLength << "    "<< _progress << " "<< toString() << " " << this;
		
	//}
	//std::cout << "length of execi: "<< *_pLength << std::endl;
	//_currTransaction=new TMLTransaction(this,_progress,_cLength-_progress,_task->getEndLastTransaction());
	_currTransaction=new TMLTransaction(this,_progress,_length-_progress,_task->getEndLastTransaction());
	//std::cout << "new fails? " << _currTransaction->toString() << std::endl;
	return true;
}

TMLTask* TMLExeciCommand::getDependentTask() const{
	return 0;
}

std::string TMLExeciCommand::toString(){
	std::ostringstream outp;
	outp << "Execi in " << TMLCommand::toString();
	return outp.str();
}

std::string TMLExeciCommand::toShortString(){
	std::ostringstream outp;
	outp << _task->toString() << ": Execi " << _length;
	return outp.str();
}

std::string TMLExeciCommand::getCommandStr(){
	return "EXECI";
}
