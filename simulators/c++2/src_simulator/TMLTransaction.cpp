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

MemPool<TMLTransaction> TMLTransaction::memPool;

TMLTransaction::TMLTransaction(TMLCommand* iCommand, TMLLength iVirtualLength, TMLTime iRunnableTime, TMLChannel* iChannel):_runnableTime(iRunnableTime), _startTime(0), _length(-1), _virtualLength(iVirtualLength), _command(iCommand),
#ifdef PENALTIES_ENABLED
 _idlePenalty(0), _taskSwitchingPenalty(0), _branchingPenalty(0),
#endif
 /*_terminated(false),*/ _channel(iChannel) {
}

TMLTime TMLTransaction::getRunnableTime() const{
	return _runnableTime;
}

void TMLTransaction::setRunnableTime(TMLTime iRunnableTime){
	_runnableTime = max(_runnableTime,iRunnableTime);
	//if (_runnableTimeSet){
	//	std::cout << "ERROR: runnable time set twice\n";
	//}else{
		//_runnableTimeSet=true;
#ifdef REGISTER_TRANS_AT_CPU 
	_command->getTask()->getCPU()->registerTransaction(this,0);
#endif
	//}
}

TMLTime TMLTransaction::getStartTime() const{
	return _startTime;
}

TMLTime TMLTransaction::getStartTimeOperation() const{
#ifdef PENALTIES_ENABLED
	return _startTime + _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
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
	return _length + _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
#else
	return _length;
#endif
}

TMLTime TMLTransaction::getPenalties() const{
#ifdef PENALTIES_ENABLED
	return _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
#else
	return 0;
#endif
}

TMLLength TMLTransaction::getVirtualLength() const{
	return _virtualLength;
}

void TMLTransaction::setVirtualLength(TMLLength iLength){
	_virtualLength=iLength;
}

TMLCommand* TMLTransaction::getCommand() const{
	return _command;
}

TMLTime TMLTransaction::getEndTime() const{
#ifdef PENALTIES_ENABLED
	return _startTime  + _length + _idlePenalty + _taskSwitchingPenalty + _branchingPenalty;
#else
	return _startTime  + _length;
#endif
	//return _startTime  + _length;
}

unsigned int TMLTransaction::getIdlePenalty() const{
#ifdef PENALTIES_ENABLED
	return _idlePenalty;
#else
	return 0;
#endif
}

void TMLTransaction::setIdlePenalty(unsigned int iIdlePenalty){
#ifdef PENALTIES_ENABLED
	_idlePenalty=iIdlePenalty;
#endif
}

unsigned int TMLTransaction::getTaskSwitchingPenalty() const{
#ifdef PENALTIES_ENABLED
	return _taskSwitchingPenalty;
#else
	return 0;
#endif
}

void TMLTransaction::setTaskSwitchingPenalty(unsigned int iTaskSwitchingPenalty){
#ifdef PENALTIES_ENABLED
	_taskSwitchingPenalty=iTaskSwitchingPenalty;
#endif	
}

unsigned int TMLTransaction::getBranchingPenalty() const{
#ifdef PENALTIES_ENABLED
	return _branchingPenalty;
#else
	return 0;
#endif
}

void TMLTransaction::setBranchingPenalty(unsigned int iBranchingPenalty){
#ifdef PENALTIES_ENABLED
	_branchingPenalty=iBranchingPenalty;
#endif
}

/*bool TMLTransaction::getTerminatedFlag() const{
	return _terminated;
}

void TMLTransaction::setTerminatedFlag(){
	_terminated=true;
	//std::cout << "TERMINATED FLAG SET!!!!!!!!!!!!!!!!!!!!!  " << this << std::endl;
}*/

std::string TMLTransaction::toString() const{
	std::ostringstream outp;	
	outp << _command->toString() << std::endl << "Transaction runnable:" << _runnableTime << " len:" << _length << " start:" << _startTime << " vLength:" << _virtualLength;
	return outp.str();
}

std::string TMLTransaction::toShortString() const{
	std::ostringstream outp;	
	outp << _command->toShortString() << " t:" << _startTime << " l:" << _length << " (vl:"<<  _virtualLength << ")";
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
