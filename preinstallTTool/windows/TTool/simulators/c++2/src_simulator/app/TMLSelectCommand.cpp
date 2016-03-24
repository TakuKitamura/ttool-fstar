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

#include <TMLSelectCommand.h>
//#include <TMLEventChannel.h>
#include <TMLTask.h>
#include <TMLTransaction.h>
#include <Bus.h>

TMLSelectCommand::TMLSelectCommand(ID iID, TMLTask* iTask, TMLEventChannel** iChannel, unsigned int iNumbChannels, const char* iLiveVarList, bool iCheckpoint, ParamFuncPointer* iParamFuncs):TMLCommand(iID, iTask, WAIT_SEND_VLEN, iNumbChannels, iLiveVarList, iCheckpoint), _channel(iChannel), _paramFuncs(iParamFuncs), /*_numbChannels(iNumbChannels),*/ _indexNextCommand(0) {
	_type=SEL;
}

TMLSelectCommand::~TMLSelectCommand(){
	if (_channel!=0) delete[] _channel;
	if (_paramFuncs!=0){
		delete [] _paramFuncs;
	}
}

/*void TMLSelectCommand::execute(){
	unsigned int aFinalIndex=0, aLoopLimit=(_maxChannelIndex==0)?_nbOfNextCmds:_maxChannelIndex;
	//i
	bool aReadDone=false;
	//std::cout << "LoopLimit: " << aLoopLimit << std::endl;
	//for (i=0;i<aLoopLimit;i++){
	for (_indexNextCommand=0;_indexNextCommand<aLoopLimit;_indexNextCommand++){
		if (aReadDone){
			//_channel[i]->cancelReadTransaction();
			_channel[_indexNextCommand]->cancelReadTransaction();
			//std::cout << "Channel " << _channel[i]->toString() << " cancelled read transaction.\n";
		}else{
			//if (_channel[i]->read()){
			if (_channel[_indexNextCommand]->read()){
				aReadDone=true;
				aFinalIndex=_indexNextCommand;
				//_indexNextCommand=i;
				//std::cout << "Read executed in channel " << _channel[i]->toString() << "\n";
			}else{
				//_channel[i]->cancelReadTransaction();
				_channel[_indexNextCommand]->cancelReadTransaction();
				//std::cout << "Channel " << _channel[i]->toString() << " cancelled read transaction.\n";
			}
		}
	}
	_indexNextCommand = aFinalIndex;
	_currTransaction->setChannel(_channel[_indexNextCommand]);
	_progress+=_currTransaction->getVirtualLength();
	//_task->setEndLastTransaction(_currTransaction->getEndTime());
	_task->addTransaction(_currTransaction);
#ifdef ADD_COMMENTS
	_task->addComment(new Comment(_task->getEndLastTransaction(), this, _indexNextCommand));
#endif
	_maxChannelIndex=0;
	prepare(false);
	//if (aNextCommand==0) _currTransaction->setTerminatedFlag();
	//if (_progress==0 && aNextCommand!=this) _currTransaction=0;
}*/

void TMLSelectCommand::execute(){
	unsigned int aFinalIndex=0, aLoopLimit=(_maxChannelIndex==0)?_nbOfNextCmds:_maxChannelIndex;
	TMLChannel* aReadChannel=_currTransaction->getChannel();
	//std::cout << "Select is executing\n";
	//bool check=false;
	for (_indexNextCommand=0;_indexNextCommand<aLoopLimit;_indexNextCommand++){
		if(_channel[_indexNextCommand]==aReadChannel){
			if (!_channel[_indexNextCommand]->read()) std::cout <<"Fatal error read!!!!\n" ;
			aFinalIndex=_indexNextCommand;
			//check=true;
		}else{
		 	_channel[_indexNextCommand]->cancelReadTransaction();
		}
	}
	//if (!check ) std::cout << "Fehler no read at all!!!!!!!!!!!!\n";
	_indexNextCommand = aFinalIndex;
	_progress+=_currTransaction->getVirtualLength();
	_task->addTransaction(_currTransaction);
#ifdef ADD_COMMENTS
	_task->addComment(new Comment(_task->getEndLastTransaction(), this, _indexNextCommand));
#endif
	_maxChannelIndex=0;
	prepare(false);
}

TMLCommand* TMLSelectCommand::prepareNextTransaction(){
	unsigned int i;
	//std::cout << "SC: New transaction."<< std::endl;
	//_currTransaction = ::new (&transBuffer) TMLTransaction(this, _length-_progress,_task->getEndLastTransaction());
	_currTransaction = new TMLTransaction(this, _length-_progress,_task->getEndLastTransaction());
	//std::cout << "SC: loop."<< std::endl;
	for (i=0;i<_nbOfNextCmds && _maxChannelIndex==0;i++){
		//std::cout << "SC: inner."<< i<< std::endl;
		_currTransaction->setVirtualLength(_length-_progress);
		_channel[i]->testRead(_currTransaction);
		if (_currTransaction->getVirtualLength()!=0) _maxChannelIndex=i+1;
	}
	//std::cout << "Max channel index:" << _maxChannelIndex <<  std::endl;
	return this;
}


TMLChannel* TMLSelectCommand::getChannel(unsigned int iIndex) const{
	if (_currTransaction==0) 
		return _channel[_indexNextCommand];
	else
		return _currTransaction->getChannel();
}

std::string TMLSelectCommand::toString() const{
	std::ostringstream outp;
	outp << "SelectEvent in " << TMLCommand::toString() << " " << _channel[_indexNextCommand]->toString();
	return outp.str();
}

std::string TMLSelectCommand::toShortString() const{
	std::ostringstream outp;
	outp << _task->toString() << ": SelectEvent";
	return outp.str();
}
