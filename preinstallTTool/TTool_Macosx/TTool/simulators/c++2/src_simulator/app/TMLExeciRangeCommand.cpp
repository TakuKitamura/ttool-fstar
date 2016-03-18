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

#include <TMLExeciRangeCommand.h>
#include <TMLTask.h>
#include <TMLTransaction.h>


TMLExeciRangeCommand::TMLExeciRangeCommand(ID iID, TMLTask* iTask, RangeFuncPointer iRangeFunc, unsigned int iType, const char* iLiveVarList, bool iCheckpoint): TMLCommand(iID, iTask, 1, 1, iLiveVarList, iCheckpoint), _rangeFunc(iRangeFunc), _minRange(0), _type(iType){
	_type=EXE;
}

void TMLExeciRangeCommand::execute(){
	_progress+=_currTransaction->getVirtualLength();
	_task->addTransaction(_currTransaction);
	prepare(false);
}

TMLCommand* TMLExeciRangeCommand::prepareNextTransaction(){
	//std::cout << "Prepare\n";
	if (_progress==0){
		ParamType aMax;
		_length = (_task->*_rangeFunc)(_minRange, aMax);
		//_length = myrand(aMin, aMax);
		if (_length==0){
			TMLCommand* aNextCommand=getNextCommand();
			_task->setCurrCommand(aNextCommand);
			if (aNextCommand!=0) return aNextCommand->prepare(false);
		}
	}
	_currTransaction = new TMLTransaction(this, _length-_progress,_task->getEndLastTransaction());
	//_currTransaction = ::new (&transBuffer) TMLTransaction(this, _length-_progress,_task->getEndLastTransaction());
	//std::cout << "new fails? " << _currTransaction->toString() << std::endl;
	return this;
}

std::string TMLExeciRangeCommand::toString() const{
	std::ostringstream outp;
	outp << "Execi in " << TMLCommand::toString();
	return outp.str();
}

std::string TMLExeciRangeCommand::toShortString() const{
	std::ostringstream outp;
	outp << _task->toString() << ": Execi " << _length;
	return outp.str();
}

unsigned int TMLExeciRangeCommand::getRandomRange(){
	ParamType aMax, aMin;
	(_task->*_rangeFunc)(aMin, aMax);
	//std::cout << "Got amin: " << aMin << " got amax: " << aMax << "\n";
	return aMax-aMin+1;
}

void TMLExeciRangeCommand::setRandomValue(unsigned int iValue){
	//std::cout << "Set random value\n";
	//ParamType aMax, aMin;
	//(_task->*_rangeFunc)(aMin, aMax);
	_length= _minRange + iValue;
	//_currTransaction = :: new (&transBuffer) TMLTransaction(this, _length,_task->getEndLastTransaction());
	_currTransaction = new TMLTransaction(this, _length,_task->getEndLastTransaction());
}
