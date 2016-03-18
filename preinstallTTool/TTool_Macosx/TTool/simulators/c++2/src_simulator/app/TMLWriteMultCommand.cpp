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

#include <TMLWriteMultCommand.h>
#include <TMLTask.h>
#include <TMLTransaction.h>
#include <Bus.h>

TMLWriteMultCommand::TMLWriteMultCommand(ID iID, TMLTask* iTask, LengthFuncPointer iLengthFunc, TMLChannel** iChannels, unsigned int iNbOfChannels, const char* iLiveVarList, bool iCheckpoint, TMLLength iStatLength): TMLCommand(iID, iTask, 1, 1, iLiveVarList, iCheckpoint), _lengthFunc(iLengthFunc), _channels(iChannels), _nbOfChannels(iNbOfChannels){
	_length=iStatLength * _channels[0]->getWidth();
	_type = WR;
}

void TMLWriteMultCommand::execute(){
	//std::cout << "--begin-- TMLWriteMultCommand::execute\n"; 
	for (unsigned int i=0; i< _nbOfChannels; i++){
		_channels[i]->write();
	}
	//std::cout << "--end-- TMLWriteMultCommand::execute\n"; 
	_progress+=_currTransaction->getVirtualLength();
	_task->addTransaction(_currTransaction);
	prepare(false);
}

TMLCommand* TMLWriteMultCommand::prepareNextTransaction(){
	if (_progress==0){
		if (_lengthFunc!=0) _length = (_task->*_lengthFunc)() * _channels[0]->getWidth();
		if (_length==0){
			TMLCommand* aNextCommand=getNextCommand();
			_task->setCurrCommand(aNextCommand);
			if (aNextCommand!=0) return aNextCommand->prepare(false);
		}
	}
	TMLLength unitsLeft =_length-_progress;
	//TMLLength minLength;
	//_currTransaction = ::new (&transBuffer) TMLTransaction(this, unitsLeft, _task->getEndLastTransaction(), _channels[0]);
	_currTransaction = new TMLTransaction(this, unitsLeft, _task->getEndLastTransaction(), _channels[0]);
	//_channels[0]->testWrite(_currTransaction);
	//minLength=_currTransaction->getVirtualLength();
	//std::cout << "--begin-- TMLWriteMultCommand::prepareNextTransaction\n"; 
	for (unsigned int i=0; i< _nbOfChannels; i++){
		//_currTransaction->setVirtualLength(unitsLeft);
		_channels[i]->testWrite(_currTransaction);
		//minLength=min(minLength,_currTransaction->getVirtualLength());
	}
	//std::cout << "--end-- TMLWriteMultCommand::prepareNextTransaction\n";
	//_currTransaction->setVirtualLength(minLength);
	return this;
}

std::string TMLWriteMultCommand::toString() const{
	std::ostringstream outp;
	outp << "WriteMult in " << TMLCommand::toString() << " " << _channels[0]->toString();
	return outp.str();
}

std::string TMLWriteMultCommand::toShortString() const{
	std::ostringstream outp;
	outp << _task->toString() << ": WriteMult" << _length << "," << _channels[0]->toShortString();
	return outp.str();
}
