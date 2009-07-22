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

#include <CPURR.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLChannel.h>
#include <Bus.h>
#include <TransactionListener.h>

CPURR::CPURR(unsigned int iID, std::string iName, TMLTime iTimePerCycle, unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc, unsigned int iPipelineSize, unsigned int iTaskSwitchingCycles, unsigned int iBranchingMissrate, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle, unsigned int ibyteDataSize): CPU(iID, iName, iTimePerCycle, iCyclesPerExeci, iCyclesPerExecc, iPipelineSize, iTaskSwitchingCycles, iBranchingMissrate, iChangeIdleModeCycles, iCyclesBeforeIdle, ibyteDataSize), _timeQuantum(100), _taskStartTime(0), _taskChanged(true) {
}

CPURR::~CPURR(){  
}

void CPURR::schedule(){
	//std::cout << "Schedule RR..." << std::endl; 
	_taskChanged=true;
	_nextTransaction=0;
	if( _lastTransaction!=0 && _endSchedule-_taskStartTime < _timeQuantum && !_pastTransQueue.empty() && _pastTransQueue.back()->getCommand()->getTask() ==_lastTransaction->getCommand()->getTask()){
		_nextTransaction=_pastTransQueue.back();
		_taskChanged=false;
	}else{
		if (_pastTransQueue.empty()){
			if (!_futureTransQueue.empty()) _nextTransaction=_futureTransQueue.top();
		}else{
			_nextTransaction = _pastTransQueue.front();
		}
	}
	
	if (_nextTransaction!=0){
		calcStartTimeLength();
		if (_taskChanged) _taskStartTime = _nextTransaction->getStartTime() +  _nextTransaction->getIdlePenalty() + _nextTransaction->getTaskSwitchingPenalty();
		if (_busNextTransaction==0 && _nextTransaction->getEndTime() > _taskStartTime + _timeQuantum) truncateNextTransAt(_taskStartTime + _timeQuantum);
		//FOR_EACH_TRANSLISTENER (*i)->transScheduled(_nextTransaction);
	}
}

void CPURR::registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice){
	//std::cout << "Register Transaction RR..." << std::endl; 
	if (iTrans->getRunnableTime() > _endSchedule){
		_futureTransQueue.push(iTrans);
	}else{
		_pastTransQueue.push_back(iTrans);
	}
}

bool CPURR::addTransaction(){
	if (CPU::addTransaction()){
		TMLTransaction* aToPastTrans;
		if (_pastTransQueue.empty())
			_futureTransQueue.pop(); 
		else{
			if (_taskChanged){
				//_pastTransQueue.pop_front();
				_pastTransQueue.erase(_pastTransQueue.begin());
			}else{
				_pastTransQueue.pop_back();
			}
		}
		while (!_futureTransQueue.empty()){
			aToPastTrans=_futureTransQueue.top();
			if (aToPastTrans->getRunnableTime()>_endSchedule) break;
			_pastTransQueue.push_back(aToPastTrans);
			_futureTransQueue.pop();	
		}
		return true;
	}else 
		return false;
}

void CPURR::reset(){
	CPU::reset();
	_futureTransQueue= FutureTransactionQueue();
	_pastTransQueue.clear();
	_taskStartTime=0;
	_taskChanged=true;
}
