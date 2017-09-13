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

#include <SingleCoreCPU.h>
#include <TMLTask.h>
#include <TMLCommand.h>
#include <TMLTransaction.h>
#include <Bus.h>
#include <Slave.h>
#include <TMLChannel.h>
//#include <TransactionListener.h>


SingleCoreCPU::SingleCoreCPU(	ID iID,
								std::string iName,
								WorkloadSource* iScheduler,
								TMLTime iTimePerCycle,
								unsigned int iCyclesPerExeci,
								unsigned int iCyclesPerExecc,
								unsigned int iPipelineSize,
								unsigned int iTaskSwitchingCycles,
								unsigned int iBranchingMissrate,
								unsigned int iChangeIdleModeCycles,
								unsigned int iCyclesBeforeIdle,
								unsigned int ibyteDataSize ) : CPU( iID, iName, iScheduler ), /*_lastTransaction(0),*/ _masterNextTransaction(0), _timePerCycle(iTimePerCycle)
#ifdef PENALTIES_ENABLED
                                                                                                                                                                                                                                                                                                                                                                      , _pipelineSize(iPipelineSize), _taskSwitchingCycles(iTaskSwitchingCycles),_brachingMissrate(iBranchingMissrate)
                                                                                                                                                                                                                                                                                                                                                                      , _changeIdleModeCycles(iChangeIdleModeCycles), _cyclesBeforeIdle(iCyclesBeforeIdle)
#endif
                                                                                                                                                                                                                                                                                                                                                                      , _cyclesPerExeci(iCyclesPerExeci) /*, _busyCycles(0)*/
#ifdef PENALTIES_ENABLED
                                                                                                                                                                                                                                                                                                                                                                      , _timePerExeci(_cyclesPerExeci * _timePerCycle * (_pipelineSize *  _brachingMissrate + 100 - _brachingMissrate) /100.0)
                                                                                                                                                                                                                                                                                                                                                                      ,_taskSwitchingTime(_taskSwitchingCycles*_timePerCycle)
                                                                                                                                                                                                                                                                                                                                                                      , _timeBeforeIdle(_cyclesBeforeIdle*_timePerCycle)
                                                                                                                                                                                                                                                                                                                                                                      , _changeIdleModeTime(_changeIdleModeCycles*_timePerCycle)
#else
                                                                                                                                                                                                                                                                                                                                                                      , _timePerExeci(_cyclesPerExeci*_timePerCycle)
#endif
                                                                                                                                                                                                                                                                                                                                                                        //, _pipelineSizeTimesExeci(_pipelineSize * _timePerExeci)
                                                                                                                                                                                                                                                                                                                                                                        //,_missrateTimesPipelinesize(_brachingMissrate*_pipelineSize)
{
  //std::cout << "Time per EXECIiiiiiiiiiiiiiiiiiiiiii: " << _timePerExeci << "\n";
  //_transactList.reserve(BLOCK_SIZE);
}

SingleCoreCPU::~SingleCoreCPU(){
  //std::cout << _transactList.size() << " elements in List of " << _name << ", busy cycles: " << _busyCycles << std::endl;
  //std::cout << " consumption value " << ((_simulatedTime/_timePerCycle)*_static_consumPerCycle) + ((_busyCycles/_timePerCycle)*_dynamic_consumPerCycle)<< std::endl;

  //delete _scheduler;
}

TMLTransaction* SingleCoreCPU::getNextTransaction(){
#ifdef BUS_ENABLED
  if (_masterNextTransaction==0 || _nextTransaction==0){
    return _nextTransaction;
  }else{
#ifdef DEBUG_CPU
    std::cout << "CPU:getNT: " << _name << " has bus transaction on master " << _masterNextTransaction->toString() << std::endl;
#endif
    //std::cout << "CRASH Trans:" << _nextTransaction->toString() << std::endl << "Channel: " << _nextTransaction->getChannel() << "\n";
    BusMaster* aTempMaster = getMasterForBus(_nextTransaction->getChannel()->getFirstMaster(_nextTransaction));
    //std::cout << "1  aTempMaster: " << aTempMaster << std::endl;
    bool aResult = aTempMaster->accessGranted();
    //std::cout << "2" << std::endl;
    while (aResult && aTempMaster!=_masterNextTransaction){
      //std::cout << "3" << std::endl;
      aTempMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
      //std::cout << "4" << std::endl;
      aResult = aTempMaster->accessGranted();
      //std::cout << "5" << std::endl;
    }
    return (aResult)?_nextTransaction:0;
  }
#else
  return _nextTransaction;
#endif
}

void SingleCoreCPU::calcStartTimeLength(TMLTime iTimeSlice){
#ifdef DEBUG_CPU
  std::cout << "CPU:calcSTL: scheduling decision of CPU " << _name << ": " << _nextTransaction->toString() << std::endl;
#endif
#ifdef BUS_ENABLED
  //std::cout << "get channel " << std::endl;
  TMLChannel* aChannel=_nextTransaction->getCommand()->getChannel(0);
  //std::cout << "after get channel " << std::endl;
  if(aChannel==0){
    //std::cout << "no channel " << std::endl;
    _masterNextTransaction=0;
  }else{
    //std::cout << "get bus " << std::endl;
    _masterNextTransaction= getMasterForBus(aChannel->getFirstMaster(_nextTransaction));
    //std::cout << "after get first bus " << std::endl;
    if (_masterNextTransaction!=0){
      //std::cout << "before register transaction at bus " << std::endl;
      _masterNextTransaction->registerTransaction(_nextTransaction);
      //std::cout << "Transaction registered at bus " << std::endl;
    }
  }
#endif
  //round to full cycles!!!
  TMLTime aStartTime = max(_endSchedule,_nextTransaction->getRunnableTime());
  TMLTime aReminder = aStartTime % _timePerCycle;
  if (aReminder!=0) aStartTime+=_timePerCycle - aReminder;
  //std::cout << "CPU: set starttime in CPU=" << aStartTime << "\n"; 

  _nextTransaction->setStartTime(aStartTime);

#ifdef BUS_ENABLED
  if (_masterNextTransaction==0){
#endif
    //calculate length of transaction
    //if (_nextTransaction->getOperationLength()!=-1){
    if (iTimeSlice!=0){
      _nextTransaction->setVirtualLength(max(min(_nextTransaction->getVirtualLength(), (TMLLength)(iTimeSlice /_timePerExeci)), (TMLTime)1));
    }
    _nextTransaction->setLength(_nextTransaction->getVirtualLength()*_timePerExeci);

#ifdef BUS_ENABLED
  }
#endif
#ifdef PENALTIES_ENABLED
  if (_lastTransaction==0 || _lastTransaction->getCommand()->getTask()!=_nextTransaction->getCommand()->getTask()){
    _nextTransaction->setTaskSwitchingPenalty(_taskSwitchingTime);
  }

  //std::cout << "starttime=" <<  _nextTransaction->getStartTime() << "\n"; 
  if ((_nextTransaction->getStartTime()-_endSchedule) >=_timeBeforeIdle){
    _nextTransaction->setIdlePenalty(_changeIdleModeTime);
  }
#endif
}

void SingleCoreCPU::truncateAndAddNextTransAt(TMLTime iTime){
  //std::cout << "CPU:schedule BEGIN " << _name << "+++++++++++++++++++++++++++++++++\n";
  //return truncateNextTransAt(iTime);
  //not a problem if scheduling does not take place at time when transaction is actually truncated, tested
  //std::cout << "CPU:truncateAndAddNextTransAt " << _name << "time: +++++++++++++++++++++" << iTime << "\n";
  TMLTime aTimeSlice = _scheduler->schedule(iTime);
  //_schedulingNeeded=false;  05/05/11
  TMLTransaction* aNewTransaction =_scheduler->getNextTransaction(iTime);
  //std::cout << "before if\n";

  //_scheduler->transWasScheduled(this); //NEW  was in if before 05/05/11

  if (aNewTransaction!=_nextTransaction){
    //std::cout << "in if\n";
    if (truncateNextTransAt(iTime)!=0) addTransaction(0);
    //if (_nextTransaction!=0 && truncateNextTransAt(iTime)!=0) addTransaction(); //NEW!!!!
    if (_nextTransaction!=0 && _masterNextTransaction!=0) _masterNextTransaction->registerTransaction(0);
    _nextTransaction = aNewTransaction;
    if (_nextTransaction!=0) calcStartTimeLength(aTimeSlice);
  }
  //std::cout << "CPU:schedule END " << _name << "+++++++++++++++++++++++++++++++++\n";
}

TMLTime SingleCoreCPU::truncateNextTransAt(TMLTime iTime){
  if (_masterNextTransaction==0){
#ifdef PENALTIES_ENABLED

    std::cout << "CPU:nt.startTime: " << _nextTransaction->getStartTime() << std::endl;
    if (iTime < _nextTransaction->getStartTime()) {
      return 0;
    }

    TMLTime aNewDuration = iTime - _nextTransaction->getStartTime();
    TMLTime aStaticPenalty = _nextTransaction->getIdlePenalty() + _nextTransaction->getTaskSwitchingPenalty();
    if (aNewDuration<=aStaticPenalty){
      _nextTransaction->setLength(_timePerExeci);
      _nextTransaction->setVirtualLength(1);
#ifdef DEBUG_CPU
      std::cout << "CPU:truncateNTA: transaction truncated\n";
#endif
    }else{
      aNewDuration-=aStaticPenalty;
      _nextTransaction->setVirtualLength(max((TMLTime)(aNewDuration /_timePerExeci),(TMLTime)1));
      _nextTransaction->setLength(_nextTransaction->getVirtualLength() *_timePerExeci);
    }
#else
    if (iTime <= _nextTransaction->getStartTime()) return 0;  //before: <=
    TMLTime aNewDuration = iTime - _nextTransaction->getStartTime();
    _nextTransaction->setVirtualLength(max((TMLTime)(aNewDuration /_timePerExeci), (TMLTime)1));
    _nextTransaction->setLength(_nextTransaction->getVirtualLength() *_timePerExeci);
#endif
#ifdef DEBUG_CPU
    std::cout << "aNewDuration: " << aNewDuration << std::endl;
    std::cout << "CPU:truncateNTA: ### cut transaction at " << _nextTransaction->getVirtualLength() << std::endl;
#endif
  }
  return _nextTransaction->getOverallLength();
}

bool SingleCoreCPU::addTransaction(TMLTransaction* iTransToBeAdded){
  bool aFinish;
  //TMLTransaction* aTransCopy=0;
  if (_masterNextTransaction==0){
    aFinish=true;
#ifdef DEBUG_CPU
    std::cout << _name << "CPU:addT: non bus transaction added" << std::endl;
#endif
  }else{
#ifdef DEBUG_CPU
    std::cout << _name << "CPU:addT: handling bus transaction" << std::endl;
#endif
    //Slave* aLastSlave=_nextTransaction->getChannel()->getNextSlave(_nextTransaction);
    BusMaster* aFollowingMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
    if (aFollowingMaster==0){
      //std::cout << "1\n";
      aFinish=true;
      //aTransCopy = new TMLTransaction(*_nextTransaction);
      //_nextTransaction = aTransCopy;
      BusMaster* aTempMaster = getMasterForBus(_nextTransaction->getChannel()->getFirstMaster(_nextTransaction));
      //std::cout << "2\n";
      Slave* aTempSlave= _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
      //std::cout << "3\n";
      aTempMaster->addBusContention(_nextTransaction->getStartTime()-max(_endSchedule,_nextTransaction->getRunnableTime()));
      while (aTempMaster!=0){
        //std::cout << "3a\n";
        aTempMaster->addTransaction(_nextTransaction);
        //std::cout << "3b\n";
        //if (aTempSlave!=0) aTempSlave->addTransaction(_nextTransaction);
        if (aTempSlave!=0) aTempSlave->addTransaction(_nextTransaction);  //NEW
        //std::cout << "4\n";
        aTempMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
        //std::cout << "5\n";
        aTempSlave= _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
      }
      //std::cout << "6\n";
    }else{
      //std::cout << _name << " bus transaction next round" << std::endl;
      _masterNextTransaction=aFollowingMaster;
      //std::cout << "7\n";
      _masterNextTransaction->registerTransaction(_nextTransaction);
      aFinish=false;
    }
    //std::cout << "8\n";
  }
  if (aFinish){
#ifdef DEBUG_CPU
    std::cout << "CPU:addt: " << _name << " finalizing transaction " << _nextTransaction->toString() << std::endl;
#endif
    //_nextTransaction->getCommand()->execute();  //NEW!!!!
    _endSchedule=_nextTransaction->getEndTime();
    //std::cout << "set end schedule CPU: " << _endSchedule << "\n";
    _simulatedTime=max(_simulatedTime,_endSchedule);
    _overallTransNo++; //NEW!!!!!!!!
    _overallTransSize+=_nextTransaction->getOperationLength();  //NEW!!!!!!!!
    //std::cout << "lets crash execute\n";
    _nextTransaction->getCommand()->execute();  //NEW!!!!
    //std::cout << "not crashed\n";
#ifdef TRANSLIST_ENABLED
    _transactList.push_back(_nextTransaction);
#endif
    _lastTransaction=_nextTransaction;
    _busyCycles+=_nextTransaction->getOverallLength();
#ifdef LISTENERS_ENABLED
    NOTIFY_TRANS_EXECUTED(_nextTransaction);
#endif
    _nextTransaction=0;
    //std::cout << "this is not the reason\n";
    return true;
  }else return false;
}

void SingleCoreCPU::schedule(){
  //std::cout <<"Hello\n";
  //std::cout << "CPU:schedule BEGIN " << _name << "+++++++++++++++++++++++++++++++++\n";
  TMLTime aTimeSlice = _scheduler->schedule(_endSchedule);
  //_schedulingNeeded=false;  05/05/11
  //std::cout << "1\n";
  TMLTransaction* aOldTransaction = _nextTransaction;
  _nextTransaction=_scheduler->getNextTransaction(_endSchedule);
  //std::cout << "2\n";

  //_scheduler->transWasScheduled(this); //NEW 05/05/11

  //if (aOldTransaction!=0){
  if (aOldTransaction!=0 && aOldTransaction!=_nextTransaction){ //NEW
    //std::cout << "3\n";
    //aOldTransaction->getCommand()->getTask()->resetScheduledFlag();  TO BE OMITTED????????????????????????????????
    //std::cout << "4\n";
    //if (aOldTransaction!=_nextTransaction && _masterNextTransaction!=0) _masterNextTransaction->registerTransaction(0);
    if (_masterNextTransaction!=0) _masterNextTransaction->registerTransaction(0);
  }
  //std::cout << "5\n";
  if (_nextTransaction!=0 && aOldTransaction != _nextTransaction) calcStartTimeLength(aTimeSlice);
  //std::cout << "CPU:schedule END " << _name << "+++++++++++++++++++++++++++++++++\n";
}

//std::string SingleCoreCPU::toString() const{
//      return _name;
//}

std::string SingleCoreCPU::toShortString() const{
  std::ostringstream outp;
  outp << "cpu" << _ID;
  return outp.str();
}

// Issue #4: Moved to SchedulableDevice for easier maintenance
//void SingleCoreCPU::schedule2HTML(std::ofstream& myfile) const{
//  TMLTime aCurrTime=0;
//  TMLTransaction* aCurrTrans;
//  unsigned int aBlanks,aLength,aColor;
//  std::string aCommentString;
//  //if (_transactList.empty()) return;
//  //std::cout << "0. size: " << _transactList.size() << '\n';
//  myfile << "<h2><span>Scheduling for device: "<< _name <<"</span></h2>\n<table>\n<tr>";
//  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
//    aCurrTrans=*i;
//    //if (aCurrTrans->getVirtualLength()==0) continue;
//    aBlanks=aCurrTrans->getStartTime()-aCurrTime;
//    if (aBlanks>0){
//      if (aBlanks==1)
//        myfile << "<td title=\"idle time\" class=\"not\"></td>\n";
//      else
//        myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time\" class=\"not\"></td>\n";
//    }
//    aLength=aCurrTrans->getPenalties();
//    if (aLength!=0){
//      if (aLength==1){
//        //myfile << "<td title=\""<< aCurrTrans->toShortString() << "\" class=\"t15\"></td>\n";
//        //myfile << "<td title=\" idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty() << " bran:" << aCurrTrans->getBranchingPenalty() << "\" class=\"t15\"></td>\n";
//        myfile << "<td title=\" idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty() << "\" class=\"t15\"></td>\n";
//      }else{
//        //myfile << "<td colspan=\"" << aLength << "\" title=\" idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty() << " bran:" << aCurrTrans->getBranchingPenalty() << "\" class=\"t15\"></td>\n";
//        myfile << "<td colspan=\"" << aLength << "\" title=\" idle:" << aCurrTrans->getIdlePenalty() << " switch:" << aCurrTrans->getTaskSwitchingPenalty() << "\" class=\"t15\"></td>\n";
//      }
//    }
//    aLength=aCurrTrans->getOperationLength();
//    aColor=aCurrTrans->getCommand()->getTask()->getInstanceNo() & 15;
//    if (aLength==1)
//      myfile << "<td title=\""<< aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
//    else
//      myfile << "<td colspan=\"" << aLength << "\" title=\"" << aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
//
//
//    aCurrTime=aCurrTrans->getEndTime();
//    //std::cout << "end time: " << aCurrTrans->getEndTime() << std::endl;
//  }
//  //std::cout << "acurrTime: " << aCurrTime << std::endl;
//  myfile << "</tr>\n<tr>";
//  for(aLength=0;aLength<aCurrTime;aLength++) myfile << "<th></th>";
//  myfile << "</tr>\n<tr>";
//  for(aLength=0;aLength<aCurrTime;aLength+=5) myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
//  myfile << "</tr>\n</table>\n<table>\n<tr>";
//  for(TaskList::const_iterator j=_taskList.begin(); j != _taskList.end(); ++j){
//    aColor=(*j)->getInstanceNo() & 15;
//    myfile << "<td class=\"t"<< aColor <<"\"></td><td>"<< (*j)->toString() << "</td><td class=\"space\"></td>\n";
//  }
//  myfile << "</tr>";
//#ifdef ADD_COMMENTS
//  bool aMoreComments=true, aInit=true;
//  Comment* aComment;
//  while(aMoreComments){
//    aMoreComments=false;
//    myfile << "<tr>";
//    for(TaskList::const_iterator j=_taskList.begin(); j != _taskList.end(); ++j){
//      aCommentString = (*j)->getNextComment(aInit, aComment);
//      if (aComment==0){
//        myfile << "<td></td><td></td><td class=\"space\"></td>";
//      } else{
//        replaceAll(aCommentString,"<","&lt;");
//        replaceAll(aCommentString,">","&gt;");
//        aMoreComments=true;
//        myfile << "<td>" << aComment->_time << "</td><td><pre>" << aCommentString << "</pre></td><td class=\"space\"></td>";
//      }
//    }
//    aInit=false;
//    myfile << "</tr>\n";
//  }
//#endif
//  myfile << "</table>\n";
//}

void SingleCoreCPU::schedule2TXT(std::ofstream& myfile) const{
  myfile << "========= Scheduling for device: "<< _name << " =========\n" ;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
    myfile << (*i)->toShortString() << std::endl;
  }
}

int SingleCoreCPU::allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const {
  int size = _transactList.size();
  int begining = size - maxNbOfTrans;
  if (begining <0) {
    begining = 0;
  }
  int cpt =0;
  int total = 0;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
    if (cpt >= begining) {
      (*i)->toXML(glob, 1, _name);
      total ++;
    }
    cpt ++;
  }
  return total;
}

void SingleCoreCPU::latencies2XML(std::ostringstream& glob, unsigned int id1, unsigned int id2) {
	for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
		if ((*i)->getCommand() !=NULL){
			if ((*i)->getCommand()->getID() == id1 || (*i)->getCommand()->getID() == id2){
				(*i)->toXML(glob, 1, _name);
			}
		}
	}

	return;
}

void SingleCoreCPU::getNextSignalChange(bool iInit, SignalChangeData* oSigData){
  if (iInit){
    _posTrasactListVCD=_transactList.begin();
    _previousTransEndTime=0;
    _vcdOutputState = END_IDLE_CPU;
    if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()!=0){
      new (oSigData) SignalChangeData(END_IDLE_CPU, 0, this);
      return;
    }
  }

  if (_posTrasactListVCD == _transactList.end()){
    new (oSigData) SignalChangeData(END_IDLE_CPU, _previousTransEndTime, this);
  }
  else{
    TMLTransaction* aCurrTrans=*_posTrasactListVCD;
    switch (_vcdOutputState){
    case END_TASK_CPU:
      do{
        _previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
        _posTrasactListVCD++;
      }while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime);
      if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()==_previousTransEndTime){
        //outp << VCD_PREFIX << vcdValConvert(END_PENALTY_CPU) << "cpu" << _ID;
        _vcdOutputState=END_PENALTY_CPU;
        new (oSigData) SignalChangeData(END_PENALTY_CPU, _previousTransEndTime, this);
      }else{
        //outp << VCD_PREFIX << vcdValConvert(END_IDLE_CPU) << "cpu" << _ID;
        _vcdOutputState=END_IDLE_CPU;
        //if (_posTrasactListVCD == _transactList.end()) oNoMoreTrans=true;
        new (oSigData) SignalChangeData(END_IDLE_CPU, _previousTransEndTime, this);
      }
      //oSigChange=outp.str();
      //return _previousTransEndTime;
      break;
    case END_PENALTY_CPU:
      //outp << VCD_PREFIX << vcdValConvert(END_TASK_CPU) << "cpu" << _ID;
      //oSigChange=outp.str();
      _vcdOutputState=END_TASK_CPU;
      //return aCurrTrans->getStartTimeOperation();
      new (oSigData) SignalChangeData(END_TASK_CPU, aCurrTrans->getStartTimeOperation(), this);
      break;
    case END_IDLE_CPU:
      if (aCurrTrans->getPenalties()==0){
        //outp << VCD_PREFIX << vcdValConvert(END_TASK_CPU) << "cpu" << _ID;
        _vcdOutputState=END_TASK_CPU;
        new (oSigData) SignalChangeData(END_TASK_CPU, aCurrTrans->getStartTime(), this);
      }else{
        //outp << VCD_PREFIX << vcdValConvert(END_PENALTY_CPU) << "cpu" << _ID;
        _vcdOutputState=END_PENALTY_CPU;
        new (oSigData) SignalChangeData(END_PENALTY_CPU, aCurrTrans->getStartTime(), this);
      }
      //oSigChange=outp.str();
      //return aCurrTrans->getStartTime();
      break;
    }
  }
  //return 0;
}

void SingleCoreCPU::reset(){
  CPU::reset();
  _scheduler->reset();
  _transactList.clear();
  _nextTransaction=0;
  _lastTransaction=0;
  _masterNextTransaction=0;
  _busyCycles=0;
}

void SingleCoreCPU::streamBenchmarks(std::ostream& s) const{
  s << TAG_CPUo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl;
  if (_simulatedTime!=0) s << TAG_UTILo << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << TAG_UTILc;
  s << TAG_ENERGYo << ( (_simulatedTime/_timePerCycle)*_static_consumPerCycle) + ((_busyCycles/_timePerCycle)*_dynamic_consumPerCycle) << TAG_ENERGYc;
  std::cout<< "power consumption "<< ((_simulatedTime/_timePerCycle)*_static_consumPerCycle) + ((_busyCycles/_timePerCycle)*_dynamic_consumPerCycle)<< std::endl;
  for(BusMasterList::const_iterator i=_busMasterList.begin(); i != _busMasterList.end(); ++i) (*i)->streamBenchmarks(s);

  s << TAG_CPUc;
}

BusMaster* SingleCoreCPU::getMasterForBus(BusMaster* iDummy){
  if (iDummy!=0){
    SchedulableCommDevice* aBus = iDummy->getBus();
    for(BusMasterList::iterator i=_busMasterList.begin(); i != _busMasterList.end(); ++i){
      if ((*i)->getBus()==aBus) return *i;
    }
    std::cout << "cry!!!!!!!!!!!!! no bus master found\n";
    exit(1);
  }
  return 0;
}

std::istream& SingleCoreCPU::readObject(std::istream &is){
  CPU::readObject(is);
  _scheduler->readObject(is);
#ifdef SAVE_BENCHMARK_VARS
  READ_STREAM(is,_busyCycles);
#ifdef DEBUG_SERIALIZE
  std::cout << "Read: CPU " << _name << " busy cycles: " << _busyCycles << std::endl;
#endif
#endif
  return is;
}
std::ostream& SingleCoreCPU::writeObject(std::ostream &os){
  CPU::writeObject(os);
  _scheduler->writeObject(os);
#ifdef SAVE_BENCHMARK_VARS
  WRITE_STREAM(os,_busyCycles);
#ifdef DEBUG_SERIALIZE
  std::cout << "Write: CPU " << _name << " busy cycles: " << _busyCycles << std::endl;
#endif
#endif
  return os;
}
