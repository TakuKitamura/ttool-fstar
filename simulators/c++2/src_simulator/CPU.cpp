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

#include <CPU.h>
#include <TMLTask.h>
#include <TMLCommand.h>
#include <TMLTransaction.h>
#include <Bus.h>
#include <Slave.h>
#include <TMLChannel.h>

CPU::CPU(std::string iName, TMLTime iTimePerCycle, unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc, unsigned int iPipelineSize, unsigned int iTaskSwitchingCycles, unsigned int iBranchingMissrate, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle, unsigned int ibyteDataSize):_name(iName), _nextTransaction(0), _lastTransaction(0), _busNextTransaction(0), _timePerCycle(iTimePerCycle),_pipelineSize(iPipelineSize), _taskSwitchingCycles(iTaskSwitchingCycles),_brachingMissrate(iBranchingMissrate), _changeIdleModeCycles(iChangeIdleModeCycles), _cyclesBeforeIdle(iCyclesBeforeIdle), _cyclesPerExeci(iCyclesPerExeci), _busyCycles(0), _contentionDelay(0), _noBusTransactions(0),  _timePerExeci(_cyclesPerExeci*_timePerCycle), _taskSwitchingTime(_taskSwitchingCycles*_timePerCycle), _timeBeforeIdle(_cyclesBeforeIdle*_timePerCycle), _changeIdleModeTime(_changeIdleModeCycles*_timePerCycle), _pipelineSizeTimesExeci(_pipelineSize * _timePerExeci),_missrateTimesPipelinesize(_brachingMissrate*_pipelineSize), _branchMissReminder(0), _branchMissTempReminder(0){
	_myid=++_id;
	_transactList.reserve(BLOCK_SIZE);
}

CPU::~CPU(){  
	//unsigned int a=0;
	TransactionList::iterator i;
	std::cout << _transactList.size() << " elements in List of " << _name << std::endl;
	/*for(i=_transactList.begin(); i != _transactList.end(); ++i){
		//std::cout << a++ << ", ";
		//delete (*i);
	}
	std::cout << std::endl;*/
}

void CPU::registerTask(TMLTask* iTask){
	_taskList.push_back(iTask);
}

TMLTransaction* CPU::getNextTransaction(){
#ifdef BUS_ENABLED
	if (_busNextTransaction==0){
		return _nextTransaction;
	}else{
		return (_busNextTransaction->getNextTransaction()==_nextTransaction)?_nextTransaction:0;
	}
#else
	return _nextTransaction;
#endif
}

void CPU::calcStartTimeLength(){
#ifdef DEBUG_CPU	
	std::cout << "CPU:calcSTL: scheduling decision of CPU " << _name << ": " << _nextTransaction->toString() << std::endl;
#endif
#ifdef BUS_ENABLED
	//std::cout << "get channel " << std::endl;
	TMLChannel* aChannel=_nextTransaction->getCommand()->getChannel();
	//std::cout << "after get channel " << std::endl;
	if(aChannel==0){
		//std::cout << "no channel " << std::endl;
		_busNextTransaction=0;
	}else{
		//std::cout << "get bus " << std::endl;
		_busNextTransaction=aChannel->getFirstBus(_nextTransaction);
		//std::cout << "after get first bus " << std::endl;
		if (_busNextTransaction!=0){
			//std::cout << "before register transaction at bus " << std::endl;
			_busNextTransaction->registerTransaction(_nextTransaction,this);
			//std::cout << "Transaction registered at bus " << std::endl;
		}
	}
#endif		
	//round to full cycles!!!
	TMLTime aStartTime = max(_endSchedule,_nextTransaction->getRunnableTime());
	TMLTime aReminder = aStartTime % _timePerCycle;
	if (aReminder!=0) aStartTime+=_timePerCycle - aReminder; 
	_nextTransaction->setStartTime(aStartTime);
	
#ifdef BUS_ENABLED
	if (_busNextTransaction==0){
		_nextTransaction->setLength(_nextTransaction->getVirtualLength()*_timePerExeci);
	}else{
		_busNextTransaction->truncateToBurst(_nextTransaction);
	}
#else
	_nextTransaction->setLength(_nextTransaction->getVirtualLength()*_timePerExeci);
#endif
	if (_lastTransaction==0 || _lastTransaction->getCommand()->getTask()!=_nextTransaction->getCommand()->getTask()){
		_nextTransaction->setTaskSwitchingPenalty(_taskSwitchingTime);
	}
	if ((_nextTransaction->getStartTime()-_endSchedule) >=_timeBeforeIdle){
		_nextTransaction->setIdlePenalty(_changeIdleModeTime);
	} 
	
	if (_brachingMissrate!=0){
		_nextTransaction->setBranchingPenalty((_nextTransaction->getVirtualLength()+_branchMissReminder) * _brachingMissrate / 100 *_pipelineSizeTimesExeci);
		_branchMissTempReminder = (_nextTransaction->getVirtualLength()+_branchMissReminder) % (100/_brachingMissrate);
	}
}

TMLTime CPU::truncateNextTransAt(TMLTime iTime){	
	if (_busNextTransaction==0){
		if (iTime < _nextTransaction->getStartTime()) return 0;
		TMLTime aNewDuration = iTime - _nextTransaction->getStartTime();
		TMLTime aStaticPenalty = _nextTransaction->getIdlePenalty() + _nextTransaction->getTaskSwitchingPenalty();
		if (aNewDuration<=aStaticPenalty){
			_nextTransaction->setLength(_timePerExeci);
			_nextTransaction->setVirtualLength(1);
			if (_brachingMissrate!=0){
				_nextTransaction->setBranchingPenalty((1+ _branchMissReminder) * _brachingMissrate / 100 *_pipelineSizeTimesExeci);
				_branchMissTempReminder = (1 +_branchMissReminder) % (100/_brachingMissrate);
			}
//#ifdef DEBUG_CPU
			std::cout << "CPU:truncateNTA: transaction truncated once\n";
//#endif
		}else{
			int test=0;
			aNewDuration-=aStaticPenalty;
			std::cout << _name << " virtual length before cut: " << _nextTransaction->getVirtualLength() << std::endl;
			_nextTransaction->setVirtualLength(100* aNewDuration /((_missrateTimesPipelinesize+100) * _timePerExeci));
			_nextTransaction->setLength(_nextTransaction->getVirtualLength() *_timePerExeci);
			if (_brachingMissrate!=0){
				_nextTransaction->setBranchingPenalty((_nextTransaction->getVirtualLength()+_branchMissReminder) * _brachingMissrate / 100 *_pipelineSizeTimesExeci);
				_branchMissTempReminder = (_nextTransaction->getVirtualLength()+_branchMissReminder) % (100/_brachingMissrate);
				//std::cout << _name << " wants to cut transaction: " << _nextTransaction->toShortString() << std::endl;
				//std::cout << "While loop begin, new duration: " << aNewDuration << " iTime: " << iTime << "  startTime: " << _nextTransaction->getStartTime() << std::endl;
				while (_nextTransaction->getOperationLength()+_nextTransaction->getBranchingPenalty() < aNewDuration){
					test++;
					_nextTransaction->setVirtualLength(_nextTransaction->getVirtualLength()+1);
					_nextTransaction->setLength(_nextTransaction->getOperationLength() +_timePerExeci);
					_nextTransaction->setBranchingPenalty((_nextTransaction->getVirtualLength()+_branchMissReminder) * _brachingMissrate / 100 *_pipelineSizeTimesExeci);
					_branchMissTempReminder = (_nextTransaction->getVirtualLength()+_branchMissReminder) % (100/_brachingMissrate);
				}
			}
//#ifdef DEBUG_CPU
			std::cout << "CPU:truncateNTA: truncate loop executed: " << test << " times.\n";
//#endif
		}
#ifdef DEBUG_CPU
		std::cout << "CPU:truncateNTA: ### cut transaction at " << _nextTransaction->getVirtualLength() << std::endl;
#endif
	}
	return _nextTransaction->getOverallLength();
}

bool CPU::addTransaction(){
	bool aFinish;
	//flag=false;
	if (_busNextTransaction==0){
		aFinish=true;
#ifdef DEBUG_CPU
		std::cout << _name << "CPU:addT: non bus transaction added" << std::endl;
#endif
	}else{
#ifdef DEBUG_CPU
		std::cout << _name << "CPU:addT: handling bus transaction" << std::endl;
#endif
		Slave* aLastSlave=_nextTransaction->getChannel()->getNextSlave(_nextTransaction);
		_busNextTransaction=_nextTransaction->getChannel()->getNextBus(_nextTransaction);
		if (_busNextTransaction==0){
			//std::cout << _name << " bus transaction finished" << std::endl;
			aFinish=true;
			//std::cout << _name << " before loop" << std::endl;
			_contentionDelay+=_nextTransaction->getStartTime()-_nextTransaction->getRunnableTime();
			_noBusTransactions++;
			SchedulableCommDevice* aTempBus =_nextTransaction->getChannel()->getFirstBus(_nextTransaction);
			while (aTempBus!=0){
				aTempBus->addTransaction();
				aTempBus =_nextTransaction->getChannel()->getNextBus(_nextTransaction);
			}
			//std::cout << _name << " after loop" << std::endl;
		}else{
			//std::cout << _name << " bus transaction next round" << std::endl;
			_busNextTransaction->registerTransaction(_nextTransaction,aLastSlave->getConnectedMaster());
			aFinish=false;
		}
	}
	if (aFinish){
#ifdef DEBUG_CPU
		std::cout << "CPU:addt: " << _name << " finalizing transaction " << _nextTransaction->toString() << std::endl;
#endif
		_nextTransaction->getCommand()->execute();
		_endSchedule=_nextTransaction->getEndTime();
		_simulatedTime=max(_simulatedTime,_endSchedule);
		_transactList.push_back(_nextTransaction);
		_lastTransaction=_nextTransaction;
		_branchMissReminder=_branchMissTempReminder;
		_busyCycles+=_nextTransaction->getOverallLength();
		//std::cout << "busyCycles: " <<  _busyCycles << std::endl;
		_nextTransaction=0;
		return true;
	}else return false;
}

unsigned int CPU::getID(){
	return _myid;
}

std::string CPU::toString(){
	return _name;
}

std::string CPU::toShortString(){
	std::ostringstream outp;
	outp << "cpu" << _myid;
	return outp.str();
}

void CPU::schedule2HTML(std::ofstream& myfile){
	TransactionList::iterator i;
	TaskList::iterator j;
	TMLTime aCurrTime=0;
	TMLTransaction* aCurrTrans;
	unsigned int aBlanks,aLength,aColor;
	Comment* aComment;
	std::string aCommentString;
	bool aMoreComments=true, aInit=true;
	//if (_transactList.empty()) return;
	myfile << "<h2><span>Scheduling for device: "<< _name <<"</span></h2>\n<table>\n<tr>";
	for(i=_transactList.begin(); i != _transactList.end(); ++i){
		aCurrTrans=*i;
		//if (aCurrTrans->getVirtualLength()==0) continue;
		aBlanks=aCurrTrans->getStartTime()-aCurrTime;
		if (aBlanks>0){
			if (aBlanks==1)
				myfile << "<td title=\"idle time\" class=\"not\"></td>\n";
			else
				myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time\" class=\"not\"></td>\n";
		}
		aLength=aCurrTrans->getPenalties();
		if (aLength!=0){
			if (aLength==1){
				//myfile << "<td title=\""<< aCurrTrans->toShortString() << "\" class=\"t15\"></td>\n";
				myfile << "<td title=\" idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty() << " bran:" << aCurrTrans->getBranchingPenalty() << "\" class=\"t15\"></td>\n";
			}else{
				myfile << "<td colspan=\"" << aLength << "\" title=\" idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty() << " bran:" << aCurrTrans->getBranchingPenalty() << "\" class=\"t15\"></td>\n";
			}
		}
		aLength=aCurrTrans->getOperationLength();
		aColor=aCurrTrans->getCommand()->getTask()->getID() & 15;
		if (aLength==1)
			myfile << "<td title=\""<< aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
		else
			myfile << "<td colspan=\"" << aLength << "\" title=\"" << aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";


		aCurrTime=aCurrTrans->getEndTime();
		//std::cout << "end time: " << aCurrTrans->getEndTime() << std::endl;
	}
	//std::cout << "acurrTime: " << aCurrTime << std::endl;
	myfile << "</tr>\n<tr>";
	for(aLength=0;aLength<aCurrTime;aLength++) myfile << "<th></th>";
	myfile << "</tr>\n<tr>";
	for(aLength=0;aLength<aCurrTime;aLength+=5) myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
	myfile << "</tr>\n</table>\n<table>\n<tr>";
	for(j=_taskList.begin(); j != _taskList.end(); ++j){
		aColor=(*j)->getID() & 15;
		myfile << "<td class=\"t"<< aColor <<"\"></td><td>"<< (*j)->toString() << "</td><td class=\"space\"></td>\n";
	}
	myfile << "</tr>";
	while(aMoreComments){
		aMoreComments=false;
		myfile << "<tr>";
		for(j=_taskList.begin(); j != _taskList.end(); ++j){
			aCommentString = (*j)->getNextComment(aInit, aComment);
			if (aComment==0){
				myfile << "<td></td><td></td><td class=\"space\"></td>";
			}else{
				replaceAll(aCommentString,"<","&lt;");
				replaceAll(aCommentString,">","&gt;");
				aMoreComments=true;
				myfile << "<td>" << aComment->_time << "</td><td><pre>" << aCommentString << "</pre></td><td class=\"space\"></td>";
			}	
		}
		aInit=false;
		myfile << "</tr>\n";
	}
	myfile << "</table>\n";
}

void CPU::schedule2TXT(std::ofstream& myfile){
	TransactionList::iterator i;
	myfile << "========================================\nScheduling for device: "<< _name << "\n========================================\n" ;
	for(i=_transactList.begin(); i != _transactList.end(); ++i){
		myfile << (*i)->toShortString() << std::endl;
	}
}

TMLTime CPU::getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans){
	std::ostringstream outp;
	oNoMoreTrans=false;
	if (iInit){
		 _posTrasactListVCD=_transactList.begin();
		_previousTransEndTime=0;
		_vcdOutputState=END_IDLE_CPU;
		//if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()==0) _vcdOutputState=END_IDLE_CPU; else _vcdOutputState=END_TASK_CPU;
		if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()!=0){
				outp << "r" << END_IDLE_CPU << " cpu" << _myid;
				oSigChange=outp.str();
				return 0;
		} 
	}
	if (_posTrasactListVCD == _transactList.end()){
		outp << "r" << END_IDLE_CPU << " cpu" << _myid;
		oSigChange=outp.str();
		oNoMoreTrans=true;
		return _previousTransEndTime;
	}else{
		//std::cout << "VCD out trans: " << (*_posTrasactListVCD)->toShortString() << std::endl;
		TMLTransaction* aCurrTrans=*_posTrasactListVCD;
		switch (_vcdOutputState){
			case END_TASK_CPU:
				do{
					_previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
					_posTrasactListVCD++;
				}while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime);
				if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()==_previousTransEndTime){
					outp << "r" << END_PENALTY_CPU << " cpu" << _myid;
					_vcdOutputState=END_PENALTY_CPU;
				}else{
					outp << "r" << END_IDLE_CPU << " cpu" << _myid;
					_vcdOutputState=END_IDLE_CPU;
					if (_posTrasactListVCD == _transactList.end()) oNoMoreTrans=true;						
				}
				oSigChange=outp.str();
				return _previousTransEndTime;
			break;
			case END_PENALTY_CPU:
				outp << "r" << END_TASK_CPU << " cpu" << _myid;
				oSigChange=outp.str();
				_vcdOutputState=END_TASK_CPU;
				return aCurrTrans->getStartTimeOperation();
			break;
			case END_IDLE_CPU:
				if (aCurrTrans->getPenalties()==0){
					outp << "r" << END_TASK_CPU << " cpu" << _myid;
					_vcdOutputState=END_TASK_CPU;
				}else{
					outp << "r" << END_PENALTY_CPU << " cpu" << _myid;
					_vcdOutputState=END_PENALTY_CPU;
				}
				oSigChange=outp.str();
				return aCurrTrans->getStartTime();
			break;
		}
	}
}

TMLTransaction* CPU::getTransactions1By1(bool iInit){
	if (iInit) _posTrasactListGraph=_transactList.begin();
	if (_posTrasactListGraph == _transactList.end()) return 0; 
	TMLTransaction* aTrans = *_posTrasactListGraph;
	_posTrasactListGraph++;
	return aTrans;
	
}

/*unsigned int CPU::getBusyCycles(){
	return _busyCycles;
}*/

void CPU::streamBenchmarks(std::ostream& s){
	s << "*** CPU " << _name << " ***\n"; 
	if (_simulatedTime!=0) s << "Utilization: " << ((float)_busyCycles)/((float)_simulatedTime) << std::endl;
	if (_noBusTransactions!=0) s << "Average contention delay: " << ((float)_contentionDelay)/((float)_noBusTransactions) << std::endl;
}
