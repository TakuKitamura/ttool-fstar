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

#include <TMLTransaction.h>
#include <TMLTask.h>
#include <CPU.h>
#include <TMLCommand.h>

MemPool<TMLTransaction> TMLTransaction::memPool(BLOCK_SIZE_TRANS);


TMLTransaction::TMLTransaction():_runnableTime(0), _startTime(0), _length(0), _virtualLength(0), _command(0),
#ifdef PENALTIES_ENABLED
 _idlePenalty(0), _taskSwitchingPenalty(0), //, _branchingPenalty(0),
#endif
_channel(0),_stateID(0) {
}

TMLTransaction::TMLTransaction(TMLCommand* iCommand, TMLLength iVirtualLength, TMLTime iRunnableTime, TMLChannel* iChannel):_runnableTime(iRunnableTime), _startTime(0), _length(0), _virtualLength(iVirtualLength), _command(iCommand),
#ifdef PENALTIES_ENABLED
 _idlePenalty(0), _taskSwitchingPenalty(0), //, _branchingPenalty(0),
#endif
_channel(iChannel),_stateID(0) {
	//if (_virtualLength!=0) std::cout << "Trans runnable: " << toString() << "\n";
}

TMLTime TMLTransaction::getRunnableTime() const{
	return _runnableTime;
}

void TMLTransaction::setRunnableTime(TMLTime iRunnableTime){
	_runnableTime = max(_runnableTime,iRunnableTime);
	/*if (_runnableTimeSet){
		std::cout << "ERROR: runnable time set twice\n";
	}else{
		_runnableTimeSet=true;
	}*/
}

TMLTime TMLTransaction::getStartTime() const{
	return _startTime;
}

TMLTime TMLTransaction::getStartTimeOperation() const{
#ifdef PENALTIES_ENABLED
	//return _startTime + _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
	return _startTime + _idlePenalty + _taskSwitchingPenalty;
#else
	return _startTime;
#endif
}

void TMLTransaction::setStartTime(TMLTime iStartTime){
	_startTime=iStartTime;
}

TMLTime TMLTransaction::getOperationLength() const{
	return _length;
}

void TMLTransaction::setLength(TMLTime iLength){
	_length=iLength;
}

TMLTime TMLTransaction::getOverallLength() const{
#ifdef PENALTIES_ENABLED
	//return _length + _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
	return _length + _idlePenalty + _taskSwitchingPenalty;
#else
	return _length;
#endif
}

TMLTime TMLTransaction::getPenalties() const{
#ifdef PENALTIES_ENABLED
	//return _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
	return _idlePenalty + _taskSwitchingPenalty;
#else
	return 0;
#endif
}

TMLLength TMLTransaction::getVirtualLength() const{
	return _virtualLength;
}

void TMLTransaction::setVirtualLength(TMLLength iLength){
	//if (iLength!=0 && _virtualLength==0) std::cout << "Trans runnable: " << toString() << "\n";
	_virtualLength=iLength;
}

TMLCommand* TMLTransaction::getCommand() const{
	return _command;
}

TMLTime TMLTransaction::getEndTime() const{
#ifdef PENALTIES_ENABLED
	//return _startTime  + _length + _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
	return _startTime  + _length + _idlePenalty + _taskSwitchingPenalty;
#else
	return _startTime  + _length;
#endif
}

TMLTime TMLTransaction::getIdlePenalty() const{
#ifdef PENALTIES_ENABLED
	return _idlePenalty;
#else
	return 0;
#endif
}

void TMLTransaction::setIdlePenalty(TMLTime iIdlePenalty){
#ifdef PENALTIES_ENABLED
	_idlePenalty=iIdlePenalty;
#endif
}

TMLTime TMLTransaction::getTaskSwitchingPenalty() const{
#ifdef PENALTIES_ENABLED
	return _taskSwitchingPenalty;
#else
	return 0;
#endif
}

void TMLTransaction::setTaskSwitchingPenalty(TMLTime iTaskSwitchingPenalty){
#ifdef PENALTIES_ENABLED
	_taskSwitchingPenalty=iTaskSwitchingPenalty;
#endif	
}

/*TMLTime TMLTransaction::getBranchingPenalty() const{
#ifdef PENALTIES_ENABLED
	return _branchingPenalty;
#else
	return 0;
#endif
}*/

/*void TMLTransaction::setBranchingPenalty(TMLTime iBranchingPenalty){
#ifdef PENALTIES_ENABLED
	_branchingPenalty=iBranchingPenalty;
#endif
}*/

std::string TMLTransaction::toString() const{
	std::ostringstream outp;	
	outp << _command->toString() << std::endl << "Transaction runnable:" << _runnableTime << " len:" << _length << " start:" << _startTime << " vLength:" << _virtualLength;
	if (_channel!=0) outp << " Ch: " << _channel->toShortString();
	return outp.str();
}

std::string TMLTransaction::toShortString() const{
	std::ostringstream outp;
	if (_command==0)
		outp << "Sytem State ID: " <<  _virtualLength;
	else{
		outp << _command->toShortString() << " t:" << _startTime << " l:" << _length << " (vl:"<<  _virtualLength << ")";
		if (_channel!=0) outp << " Ch: " << _channel->toShortString();
	}	
	return outp.str();
}

void TMLTransaction::setChannel(TMLChannel* iChannel){
	_channel=iChannel;
}

TMLChannel* TMLTransaction::getChannel() const{
	return _channel;
}

void * TMLTransaction::operator new(size_t size){
	return memPool.pmalloc(size);
}

void TMLTransaction::operator delete(void *p, size_t size){
	memPool.pfree(p, size);
}

void TMLTransaction::reset(){
	memPool.reset();
}

void TMLTransaction::incID(){
	_ID++;
}

ID TMLTransaction::getID(){
	return _ID;
}

void TMLTransaction::resetID(){
	_ID=1;
}

void TMLTransaction::setStateID(ID iID){
	_stateID=iID;
}

ID TMLTransaction::getStateID(){
	return _stateID;
}
