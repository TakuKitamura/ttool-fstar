/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Niu Siyuan,
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

#include <FPGA.h>
#include <TMLTask.h>
#include <TMLCommand.h>
#include <TMLTransaction.h>
#include <Bus.h>
#include <Slave.h>
#include <TMLChannel.h>


FPGA::FPGA(    ID iID, 
	       std::string iName,
	       WorkloadSource* iScheduler,
 	       TMLTime iTimePerCycle, 
	       TMLTime iReconfigTime, 
	       unsigned int iChangeIdleModeCycles, 
	       unsigned int iCyclesBeforeIdle,
	       unsigned int iCyclesPerExeci, 
	       unsigned int iCyclesPerExecc ) : SchedulableDevice(iID, iName, iScheduler)
					      ,_timePerCycle(iTimePerCycle)
					      ,_reconfigTime(iReconfigTime)
					      ,_lastTransaction(0)
#ifdef PENALTIES_ENABLED
					      ,_changeIdleModeCycles(iChangeIdleModeCycles), _cyclesBeforeIdle(iCyclesBeforeIdle)
#endif 
#ifdef PENALTIES_ENABLED
					      , _timePerExeci(_cyclesPerExeci * _timePerCycle /100.0)
					      , _timeBeforeIdle(_cyclesBeforeIdle*_timePerCycle)
					      , _changeIdleModeTime(_changeIdleModeCycles*_timePerCycle)
#else
					      , _timePerExeci(_cyclesPerExeci*_timePerCycle)
#endif
{}

FPGA::~FPGA(){}


void FPGA::streamBenchmarks(std::ostream& s) const{
  std::cout<<"test fpga stramBenchmarks"<<std::endl;
  s << TAG_FPGAo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl;
  if (_simulatedTime!=0) s << TAG_UTILo << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << TAG_UTILc;
  s << TAG_ENERGYo << ( (_simulatedTime/_timePerCycle)*_static_consumPerCycle) + ((_busyCycles/_timePerCycle)*_dynamic_consumPerCycle) << TAG_ENERGYc;
  std::cout<< "power consumption "<< ((_simulatedTime/_timePerCycle)*_static_consumPerCycle) + ((_busyCycles/_timePerCycle)*_dynamic_consumPerCycle)<< std::endl;
  for(BusMasterList::const_iterator i=_busMasterList.begin(); i != _busMasterList.end(); ++i) (*i)->streamBenchmarks(s);
  s << TAG_FPGAc;
}



TMLTransaction* FPGA::getNextTransaction(){
std::cout<<"fpga getNextTransaction"<<std::endl;
#ifdef BUS_ENABLE
  if(_masterNextTransaction==0 || _nextTransaction==0){
    return _nextTransaction;
   }
  else{
    BusMaster* aTempMaster = getMasterForBus(_nextTransaction->getChannel()->getFirstMaster(_nextTransaction));
    bool aResult = aTempMaster->accessGranted();
 
    while (aResult && aTempMaster!=_masterNextTransaction){
      
      aTempMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
      
      aResult = aTempMaster->accessGranted();
 
    }
    return (aResult)?_nextTransaction:0;
  }
#else
  return _nextTransaction;
#endif
 }

void FPGA::calcStartTimeLength(TMLTime iTimeSlice){
std::cout<<"fpga calStartTimeLength"<<std::endl;
  
#ifdef BUS_ENABLED
  
  std::cout << "FPGA:calcSTL: scheduling decision of FPGA " << _name << ": " << _nextTransaction->toString() << std::endl;
  std::cout << "get channel " << std::endl;
  TMLChannel* aChannel=_nextTransaction->getCommand()->getChannel(0);
  std::cout << "after get channel " << std::endl;
  if (aChannel==0) {
    std::cout<<"test111"<<std::endl;
    //std::cout << "no channel " << std::endl;
    _masterNextTransaction=0;
  } else {
    std::cout << "get bus " << std::endl;
    _masterNextTransaction= getMasterForBus(aChannel->getFirstMaster(_nextTransaction));
    if (_masterNextTransaction!=0){
      std::cout << "before register transaction at bus " << _masterNextTransaction->toString() << std::endl;
      _masterNextTransaction->registerTransaction(_nextTransaction);
      std::cout << "Transaction registered at bus " << _masterNextTransaction->toString() << std::endl;
    } else {
      std::cout << "                          NO MASTER NEXT TRANSACTION " << std::endl;
    }
  }
#endif
  std::cout<<"test222"<<std::endl;
  //round to full cycles!!!
  std::cout<<"time per cycle is "<<_timePerCycle<<std::endl;
  std::cout<<"test333"<<std::endl;
  TMLTime aStartTime = _nextTransaction->getRunnableTime();
  TMLTime aReminder = aStartTime % _timePerCycle;
  if (aReminder!=0) aStartTime+=_timePerCycle - aReminder;
  std::cout << "FPGA: set start time in FPGA=" << aStartTime << " Reminder=" << aReminder <<"\n";

  _nextTransaction->setStartTime(aStartTime);

#ifdef BUS_ENABLED
  if (_masterNextTransaction==0){
#endif
    //calculate length of transaction
    //if (_nextTransaction->getOperationLength()!=-1){
    std::cout<<"at first virtual length "<<_nextTransaction->getVirtualLength()<<std::endl;
    std::cout<<"another "<<(TMLLength)(iTimeSlice /_timePerExeci)<<std::endl;
    if (iTimeSlice!=0){
      _nextTransaction->setVirtualLength(max(min(_nextTransaction->getVirtualLength(), (TMLLength)(iTimeSlice /_timePerExeci)), (TMLTime)1));
    }
    _nextTransaction->setLength(_nextTransaction->getVirtualLength()*_timePerExeci);
    std::cout<<"!!!!!virtual length is "<<_nextTransaction->getVirtualLength()<<std::endl;
#ifdef BUS_ENABLED
  }
#endif
#ifdef PENALTIES_ENABLED
  //std::cout << "starttime=" <<  _nextTransaction->getStartTime() << "\n";
  if ((_nextTransaction->getStartTime()-_endSchedule) >=_timeBeforeIdle){
    _nextTransaction->setIdlePenalty(_changeIdleModeTime);
  }
#endif
}

void FPGA::truncateAndAddNextTransAt(TMLTime iTime){
std::cout<<"fpga truncateAndAddNextTransAt"<<std::endl;
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

TMLTime FPGA::truncateNextTransAt(TMLTime iTime){
std::cout<<"fpga truncateNextTransAt"<<std::endl;
  if (_masterNextTransaction==0){
#ifdef PENALTIES_ENABLED

    //std::cout << "CPU:nt.startTime: " << _nextTransaction->getStartTime() << std::endl;
    if (iTime < _nextTransaction->getStartTime()) {
      return 0;
    }

    TMLTime aNewDuration = iTime - _nextTransaction->getStartTime();
    TMLTime aStaticPenalty = _nextTransaction->getIdlePenalty() + _nextTransaction->getTaskSwitchingPenalty();
    if (aNewDuration<=aStaticPenalty){
      _nextTransaction->setLength(_timePerExeci);
      _nextTransaction->setVirtualLength(1);

    } else{
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
  }
  return _nextTransaction->getOverallLength();
}



bool FPGA::addTransaction(TMLTransaction* iTransToBeAdded){
std::cout<<"fpga addTransaction"<<std::endl;
  bool aFinish;
  std::cout << "*************** LOOKING for master of" << _nextTransaction->toString() << std::endl;
  if (_masterNextTransaction==0){
    aFinish=true;
  }else{
    BusMaster* aFollowingMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
    if (aFollowingMaster==0){
      std::cout << "1\n";
      aFinish=true;
      BusMaster* aTempMaster = getMasterForBus(_nextTransaction->getChannel()->getFirstMaster(_nextTransaction));
      std::cout << "2\n";
      Slave* aTempSlave= _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
      std::cout << "3\n";
      aTempMaster->addBusContention(_nextTransaction->getStartTime()-max(_endSchedule,_nextTransaction->getRunnableTime()));
      while (aTempMaster!=0){
        std::cout << "3a\n";
        aTempMaster->addTransaction(_nextTransaction);
        std::cout << "3b\n";
        //if (aTempSlave!=0) aTempSlave->addTransaction(_nextTransaction);
        if (aTempSlave!=0) aTempSlave->addTransaction(_nextTransaction);  //NEW
        std::cout << "4\n";
        aTempMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
        std::cout << "5\n";
        aTempSlave= _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
      }
      std::cout << "6\n";
    } else {
      std::cout << _name << " bus transaction next round" << std::endl;
      _masterNextTransaction=aFollowingMaster;
      std::cout << "7\n";
      _masterNextTransaction->registerTransaction(_nextTransaction);
      aFinish=false;
    }
    //std::cout << "8\n";
  }
  if (aFinish){
    _endSchedule=0;
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
    return true;
  } else return false;
}

void FPGA::schedule(){ 
  
  std::cout << "fpga:schedule BEGIN " << _name << "+++++++++++++++++++++++++++++++++\n";
  
  TMLTime aTimeSlice = _scheduler->schedule(_endSchedule);
  
  TMLTransaction* aOldTransaction = _nextTransaction;
  _nextTransaction=_scheduler->getNextTransaction(_endSchedule);

  if (aOldTransaction!=0 && aOldTransaction!=_nextTransaction){ //NEW
  
    if (_masterNextTransaction!=0) {
      _masterNextTransaction->registerTransaction(0);

    }
  }

  if (_nextTransaction!=0 && aOldTransaction != _nextTransaction) calcStartTimeLength(aTimeSlice);
  std::cout << "fpga:schedule END " << _name << "+++++++++++++++++++++++++++++++++\n";
}

void FPGA::getNextSignalChange(bool iInit, SignalChangeData* oSigData){
  if (iInit){
   
    _posTrasactListVCD=_transactList.begin();
    std::cout<<"init "<<(*_posTrasactListVCD)->toShortString()<<std::endl;
    _previousTransEndTime=0;
    _vcdOutputState = END_IDLE_FPGA;
    if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()!=0){
      new (oSigData) SignalChangeData(END_IDLE_FPGA, 0, this);
      return;
    }
  }

  if (_posTrasactListVCD == _transactList.end()){
    std::cout<<"end trans"<<std::endl;
    new (oSigData) SignalChangeData(END_IDLE_FPGA, _previousTransEndTime, this);
  }
  else{
    TMLTransaction* aCurrTrans=*_posTrasactListVCD;
    std::cout<<"current trans is "<<aCurrTrans->toShortString()<<std::endl;
    switch (_vcdOutputState){
    case END_TASK_FPGA:
      std::cout<<"END_TASK_FPGA"<<std::endl;
      do{
        _previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
        _posTrasactListVCD++;
      }while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime);
      if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()==_previousTransEndTime){
        //outp << VCD_PREFIX << vcdValConvert(END_PENALTY_FPGA) << "FPGA" << _ID;
        _vcdOutputState=END_PENALTY_FPGA;
	std::cout<<"why???"<<std::endl;
        new (oSigData) SignalChangeData(END_PENALTY_FPGA, _previousTransEndTime, this);
      }else{
        //outp << VCD_PREFIX << vcdValConvert(END_IDLE_FPGA) << "FPGA" << _ID;
        _vcdOutputState=END_IDLE_FPGA;
        //if (_posTrasactListVCD == _transactList.end()) oNoMoreTrans=true;
        new (oSigData) SignalChangeData(END_IDLE_FPGA, _previousTransEndTime, this);
      }
      //oSigChange=outp.str();
      //return _previousTransEndTime;
      break;
    case END_PENALTY_FPGA:
      std::cout<<"END_PENALTY_FPGA"<<std::endl;
      //outp << VCD_PREFIX << vcdValConvert(END_TASK_FPGA) << "FPGA" << _ID;
      //oSigChange=outp.str();
      _vcdOutputState=END_TASK_FPGA;
      //return aCurrTrans->getStartTimeOperation();
      new (oSigData) SignalChangeData(END_TASK_FPGA, aCurrTrans->getStartTimeOperation(), this);
      break;
    case END_IDLE_FPGA:
      std::cout<<"END_IDLE_FPGA"<<std::endl;
      if (aCurrTrans->getPenalties()==0){
        //outp << VCD_PREFIX << vcdValConvert(END_TASK_FPGA) << "FPGA" << _ID;
        _vcdOutputState=END_TASK_FPGA;
        new (oSigData) SignalChangeData(END_TASK_FPGA, aCurrTrans->getStartTime(), this);
      }else{
        //outp << VCD_PREFIX << vcdValConvert(END_PENALTY_FPGA) << "FPGA" << _ID;
        _vcdOutputState=END_PENALTY_FPGA;
        new (oSigData) SignalChangeData(END_PENALTY_FPGA, aCurrTrans->getStartTime(), this);
      }
      //oSigChange=outp.str();
      //return aCurrTrans->getStartTime();
      break;
    }
  
  }
  //return 0;
}


std::string FPGA::toShortString() const{
  std::ostringstream outp;
  outp << "fpga" << _ID;
  return outp.str();
}

void FPGA::schedule2TXT(std::ofstream& myfile) const{
  myfile << "========= Scheduling for device: "<< _name << " =========\n" ;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
   std::cout<<"my transaction================================="<<std::endl;
    myfile << (*i)->toShortString() << std::endl;
  }
  std::cout<<"txt end========================"<<std::endl;
}

BusMaster* FPGA::getMasterForBus(BusMaster* iDummy){
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

int FPGA::allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const {
  int size = _transactList.size();
  int begining = size - maxNbOfTrans;
  if (begining <0) {
    begining = 0;
  }
  int cpt =0;
  int total = 0;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
    if (cpt >= begining) {
      (*i)->toXML(glob, 0, _name, _ID);
      total ++;
    }
    cpt ++;
  }
  return total;
}


void FPGA::latencies2XML(std::ostringstream& glob, unsigned int id1, unsigned int id2) {
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
    if ((*i)->getCommand() !=NULL){
      if ((*i)->getCommand()->getID() == id1 || (*i)->getCommand()->getID() == id2){
        (*i)->toXML(glob, 0, _name, _ID);
      }
    }
  }

  return;
}



