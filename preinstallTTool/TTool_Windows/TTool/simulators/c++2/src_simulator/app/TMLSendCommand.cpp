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

#include <TMLSendCommand.h>
//#include <TMLEventChannel.h>
#include <TMLTask.h>
#include <TMLTransaction.h>
#include <Bus.h>

TMLSendCommand::TMLSendCommand(ID iID, TMLTask* iTask, TMLEventChannel* iChannel, ParamFuncPointer iParamFunc, const char* iLiveVarList, bool iCheckpoint/*, Parameter* iStatParam*/): TMLCommand(iID, iTask, WAIT_SEND_VLEN, 1, iLiveVarList, iCheckpoint), _channel(iChannel), _paramFunc(iParamFunc)/*, _statParam(iStatParam)*/ {
	_type=SND;
}

void TMLSendCommand::execute(){
	//std::cout << "TMLSendCommand::execute()\n";
	_channel->write();
	//std::cout << "TMLSendCommand::execute() after write: " << _currTransaction << "\n";
	//std::cout << "Dependent Task: " << _channel->getBlockedReadTask()->toString() << std::endl;
	_progress+=_currTransaction->getVirtualLength();
	//_task->setEndLastTransaction(_currTransaction->getEndTime());
	//std::cout << "TMLSendCommand::execute() before add\n";
	_task->addTransaction(_currTransaction);
	//std::cout << "TMLSendCommand::execute() after add\n";
	prepare(false);
	//if (aNextCommand==0) _currTransaction->setTerminatedFlag();
	//if (_progress==0 && aNextCommand!=this) _currTransaction=0;
}

TMLCommand* TMLSendCommand::prepareNextTransaction(){
	//_currTransaction = ::new (&transBuffer) TMLTransaction(this, _length-_progress, _task->getEndLastTransaction(), _channel);
	_currTransaction = new TMLTransaction(this, _length-_progress, _task->getEndLastTransaction(), _channel);
	_channel->testWrite(_currTransaction);
	return this;
}

std::string TMLSendCommand::toString() const{
	std::ostringstream outp;
	outp << "Send in " << TMLCommand::toString() << " " << _channel->toString();
	return outp.str();
}

std::string TMLSendCommand::toShortString() const{
	std::ostringstream outp;
	outp << _task->toString() << ": Send " << _channel->toShortString();
	return outp.str();
}

