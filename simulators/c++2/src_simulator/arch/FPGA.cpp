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
	       TMLTime iReconfigTime,
	       TMLTime iTimePerCycle,
	       unsigned int iChangeIdleModeCycles, 
	       unsigned int iCyclesBeforeIdle,
	       unsigned int iCyclesPerExeci, 
	       unsigned int iCyclesPerExecc ) : SchedulableDevice(iID, iName, iScheduler)
					      ,_reconfigTime(iReconfigTime)
					      ,_timePerCycle(iTimePerCycle)
					      ,_masterNextTransaction(0)
					      ,_lastTransaction(0)
					      ,_changeIdleModeCycles(iChangeIdleModeCycles * _timePerCycle)
					      ,_cyclesBeforeIdle(iCyclesBeforeIdle * _timePerCycle)
					      ,_cyclesPerExeci(iCyclesPerExeci * _timePerCycle)
					      ,_cyclesPerExecc(iCyclesPerExecc * _timePerCycle)
					      ,_reconfigNumber(0)
					      ,_maxEndTime(0)
					     
					     
{}

FPGA::~FPGA(){}


void FPGA::streamBenchmarks(std::ostream& s) const{
  std::cout<<"test fpga stramBenchmarks"<<std::endl;
  s << TAG_FPGAo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl;
  if (_simulatedTime!=0) s << TAG_UTILo << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << TAG_UTILc;
//  s << TAG_ENERGYo << ( (_simulatedTime)*_static_consumPerCycle) + ((_busyCycles)*_dynamic_consumPerCycle) << TAG_ENERGYc;
  std::cout<< "power consumption "<< ((_simulatedTime)*_static_consumPerCycle) + ((_busyCycles)*_dynamic_consumPerCycle)<< std::endl;
  for(BusMasterList::const_iterator i=_busMasterList.begin(); i != _busMasterList.end(); ++i) (*i)->streamBenchmarks(s);
  s << TAG_FPGAc;
}

TMLTransaction* FPGA::getNextTransaction(){
#ifdef DEBUG_FPGA
 std::cout<<"fpga getNextTransaction"<<_name<<" ";
#endif
#ifdef BUS_ENABLED
  if (_masterNextTransaction==0 || _nextTransaction==0){
#ifdef DEBUG_FPGA
    if(_masterNextTransaction == 0) std::cout<<"master is 0"<<std::endl;
    if(_nextTransaction==0) std::cout<<"nexttrans is 0"<<std::endl;
#endif
    //if(_nextTransaction)  std::cout<<_nextTransaction->toString()<<std::endl;
     return _nextTransaction;
     //return 0;
  }else{
    //std::cout << "CRASH Trans:" << _nextTransaction->toString() << std::endl << "Channel: " << _nextTransaction->getChannel() << "\n";
    BusMaster* aTempMaster = getMasterForBus(_nextTransaction->getChannel()->getFirstMaster(_nextTransaction));
    std::cout << "1  aTempMaster: " << aTempMaster << std::endl;
    bool aResult = aTempMaster->accessGranted();
    // std::cout << "2" << std::endl;
    while (aResult && aTempMaster!=_masterNextTransaction){
      // std::cout << "3" << std::endl;
      aTempMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
      // std::cout << "4" << std::endl;
      aResult = aTempMaster->accessGranted();
      // std::cout << "5" << std::endl;
    }
#ifdef DEBUG_FPGA
    if(_nextTransaction)std::cout<<"haha1"<<_nextTransaction->toString()<<std::endl;
#endif
    return (aResult)?_nextTransaction:0;
  }
#else
  if(_nextTransaction)std::cout<<"haha2"<<_nextTransaction->toString()<<std::endl;

  return _nextTransaction;
#endif
 }

void FPGA::calcStartTimeLength(){
#ifdef DEBUG_FPGA
  std::cout<<"fpga calStartTimeLength "<<std::endl;
#endif
  
#ifdef BUS_ENABLED
  
  std::cout << "FPGA:calcSTL: scheduling decision of FPGA " << _name << ": " << _nextTransaction->toString() << std::endl;
  TMLChannel* aChannel=_nextTransaction->getCommand()->getChannel(0);
  if (aChannel==0) {
    _masterNextTransaction=0;
  } else {
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
  //round to full cycles!!!

  TMLTime aStartTime = max(_endSchedule,_nextTransaction->getRunnableTime());
#ifdef DEBUG_FPGA
  std::cout<<"start time !!!!!!!!!!"<<_nextTransaction->toShortString()<<"is "<<aStartTime<<std::endl;
#endif
  //or setStartTime(0)???
  _nextTransaction->setStartTime(aStartTime);
#ifdef BUS_ENABLED
  if (_masterNextTransaction==0){
#endif  
    _nextTransaction->setLength(max(_nextTransaction->getVirtualLength() * _cyclesPerExeci,(TMLTime)1));
#ifdef BUS_ENABLED
  }
#endif
}


void FPGA::truncateAndAddNextTransAt(TMLTime iTime){
std::cout<<"fpga truncateAndAddNextTransAt"<<std::endl;
  //std::cout << "CPU:schedule BEGIN " << _name << "+++++++++++++++++++++++++++++++++\n";
  //return truncateNextTransAt(iTime);
  //not a problem if scheduling does not take place at time when transaction is actually truncated, tested
  //std::cout << "CPU:truncateAndAddNextTransAt " << _name << "time: +++++++++++++++++++++" << iTime << "\n";
//  TMLTime aTimeSlice = _scheduler->schedule(iTime);
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
    if (_nextTransaction!=0) calcStartTimeLength();
  }
  //std::cout << "CPU:schedule END " << _name << "+++++++++++++++++++++++++++++++++\n";
}

TMLTime FPGA::truncateNextTransAt(TMLTime iTime){
std::cout<<"fpga truncateNextTransAt"<<std::endl;
  if (_masterNextTransaction==0){
    if (iTime <= _nextTransaction->getStartTime()) return 0;  //before: <=
    TMLTime aNewDuration = iTime - _nextTransaction->getStartTime();
    _nextTransaction->setVirtualLength(max((TMLTime)(aNewDuration), (TMLTime)1));
    _nextTransaction->setLength(_nextTransaction->getVirtualLength() * _cyclesPerExeci);
  }
  return _nextTransaction->getOverallLength();
}



bool FPGA::addTransaction(TMLTransaction* iTransToBeAdded){
#ifdef DEBUG_FPGA
std::cout<<"fpga addTransaction"<<std::endl;
#endif
  bool aFinish;
  std::cout << "*************** LOOKING for master of" << _nextTransaction->toString() << std::endl;
  if (_masterNextTransaction==0){
    aFinish=true;
  }else{
    BusMaster* aFollowingMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
    if (aFollowingMaster==0){
      //std::cout << "1\n";
      aFinish=true;
      BusMaster* aTempMaster = getMasterForBus(_nextTransaction->getChannel()->getFirstMaster(_nextTransaction));
      // std::cout << "2\n";
      Slave* aTempSlave= _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
      //std::cout << "3\n";
      while (aTempMaster!=0){
	// std::cout << "3a\n";
        aTempMaster->addTransaction(_nextTransaction);
	// std::cout << "3b\n";
        //if (aTempSlave!=0) aTempSlave->addTransaction(_nextTransaction);
        if (aTempSlave!=0) aTempSlave->addTransaction(_nextTransaction);  //NEW
        //std::cout << "4\n";
        aTempMaster =_nextTransaction->getChannel()->getNextMaster(_nextTransaction);
        //std::cout << "5\n";
        aTempSlave= _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
      }
      // std::cout << "6\n";
    } else {
      std::cout << _name << " bus transaction next round" << std::endl;
      _masterNextTransaction=aFollowingMaster;
      // std::cout << "7\n";
      _masterNextTransaction->registerTransaction(_nextTransaction);
      aFinish=false;
    }
    //std::cout << "8\n";
  }
 
  if (aFinish){
#ifdef DEBUG_FPGA
    std::cout<<"I am in finish!!!"<<std::endl;
#endif
    //_endSchedule=0;
    // _maxEndTime=max(_maxEndTime,_nextTransaction->getEndTime())
    //std::cout<<"end schedule is ~~~~~~~"<<_endSchedule<<std::endl;
    if(_endSchedule == 0 && (!(_nextTransaction->getCommand()->getTask()->getIsDaemon()==true && _nextTransaction->getCommand()->getTask()->getNextTransaction(0)==0))) 
      _maxEndTime=max(_maxEndTime,_nextTransaction->getEndTime());

    unsigned int _highestRank = 1000;
    for(TaskList::const_iterator i = _taskList.begin(); i!= _taskList.end(); ++i){
          if((*i)->getPriority() < _highestRank) {
              _highestRank = (*i)->getPriority();
              if(_highestRank == 0)
                  break;
          }
    }
    unsigned int _tempReconfigNumber = _reconfigNumber - _highestRank;
    if(_tempReconfigNumber == 0 && _reconfigNumber > 0) {
        std::string _tempTranName = _nextTransaction->toShortString();
        if(!_nextTransaction->getCommand()->getTask()->getIsFirstTranExecuted() && (_tempTranName.find("Read") == std::string::npos
        && _tempTranName.find("Wait") == std::string::npos && _tempTranName.find("Notified") == std::string::npos)) {
            unsigned int _tempStartTime = _nextTransaction->getStartTime();
            _nextTransaction->setStartTime(_tempStartTime + _reconfigNumber * _reconfigTime * _cyclesPerExeci);
            _maxEndTime=max(_maxEndTime,_nextTransaction->getEndTime());
            _transactListReconfig.push_back(_nextTransaction);
        }
        _nextTransaction->getCommand()->getTask()->setIsFirstTranExecuted(true);
    } else if(_tempReconfigNumber>0) {
        if(!_nextTransaction->getCommand()->getTask()->getIsFirstTranExecuted()) {
            _nextTransaction->setStartTime(_maxEndTime + _tempReconfigNumber * _reconfigTime * _cyclesPerExeci);
            _nextTransaction->getCommand()->getTask()->setIsFirstTranExecuted(true);
            _transactListReconfig.push_back(_nextTransaction);
        }
    }
    else{
      _endSchedule=0;
      
    }
#ifdef DEBUG_FPGA
    
    std::cout<<"_maxEndTime is "<<_maxEndTime<<std::endl;
    
    std::cout<<"endschedule is!! "<<_endSchedule<<std::endl;
    if(_nextTransaction==0) std::cout<<"000"<<std::endl;
#endif
    _simulatedTime=max(_simulatedTime,_nextTransaction->getEndTime());
    _overallTransNo++; //NEW!!!!!!!!
    _overallTransSize+=_nextTransaction->getOperationLength();  //NEW!!!!!!!!
    //std::cout << "lets crash execute\n";
    // std::cout<<_nextTransaction->toString()<<std::endl;
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
  _reconfigNumber=_scheduler->schedule(_endSchedule);
  TMLTransaction* aOldTransaction = _nextTransaction; 
  _nextTransaction=_scheduler->getNextTransaction(_endSchedule);
  
  if (aOldTransaction!=0 && aOldTransaction!=_nextTransaction){ //NEW 
    if (_masterNextTransaction!=0) {
      _masterNextTransaction->registerTransaction(0);

    }
  }
  //if(_nextTransaction!=0) std::cout<<"nextTransaction is~~~~ "<< _nextTransaction->toShortString()<<std::endl;
  if (_nextTransaction!=0 && aOldTransaction != _nextTransaction)  calcStartTimeLength();
  std::cout << "fpga:schedule END " << _name << "+++++++++++++++++++++++++++++++++\n";
}

void FPGA::getNextSignalChange(bool iInit, SignalChangeData* oSigData){
  std::cout<<"getNextSignalChangemulticore!!!---------"<<std::endl;
  for( TransactionList::iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
    
    // std::cout<<"transaction core number is "<<  (*i)->getTransFpgaNumber()<<std::endl;
    // std::cout<<"cpu core number "<< oSigData->_transNumberVcd<<std::endl;
    if((*i)-> getCommand()->getTask() == oSigData->_taskFPGA){
      
      std::cout<<"bingo!!"<<(*i)->toShortString()<<std::endl;

      if (iInit){
	_posTrasactListVCD= i;
	_previousTransEndTime=0;
	std::cout<<"init start time "<<(*_posTrasactListVCD)->getStartTime()<<std::endl;
	if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTime()!=0){
	  std::cout<<"next idle"<<std::endl;
	  new (oSigData) SignalChangeData(END_IDLE_TRANS, 0, this);
	  (*i)->setTransVcdOutPutState(END_IDLE_TRANS);
	  return;
	}
      }
      if ((*i)->getEndState() == true){
        std::cout<<"end trans"<<(*i)->getEndTime()<<std::endl;
	new (oSigData) SignalChangeData(END_IDLE_TRANS, (*i)->getEndTime(), this);
        break;
      }else{
	_posTrasactListVCD = i;
	TMLTransaction* aCurrTrans=*_posTrasactListVCD;
       switch (aCurrTrans->getTransVcdOutPutState()){
	case END_TASK_TRANS: 
          
	  std::cout<<"END_TASK_FPGA"<<std::endl;
	  do{
	    _previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
	    _posTrasactListVCD++;	  
	    while(_posTrasactListVCD != _transactList.end()){
	      if((*_posTrasactListVCD)->getCommand()->getTask() == oSigData->_taskFPGA)
		  break;
		else
		  _posTrasactListVCD++;
	      }
	  }while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime);
	 
	  aCurrTrans->setTransVcdOutPutState(END_IDLE_TRANS);
	  std::cout<<"what is previous time "<<_previousTransEndTime<<std::endl;
	  std::cout<<"and this??"<<oSigData->_time<<std::endl;
	  new (oSigData) SignalChangeData(END_IDLE_TRANS, _previousTransEndTime, this); 
	  if (_posTrasactListVCD == _transactList.end()) {aCurrTrans->setEndState(true);std::cout<<"hahaha"<<std::endl;}
	  
          _transactList.erase(i);
	  break;
	case END_IDLE_TRANS:
	  std::cout<<"END_IDLE_FPGA"<<std::endl;
	  
	  aCurrTrans->setTransVcdOutPutState(END_TASK_TRANS);
	  new (oSigData) SignalChangeData(END_TASK_TRANS, aCurrTrans->getStartTime(), this);
	
	  break;
       }
      }
      break;
    }
   
  }
   
}


std::string FPGA::toShortString() const{
  std::ostringstream outp;
  outp << "fpga" << _ID;
  return outp.str();
}

void FPGA::reset(){
  SchedulableDevice::reset();
  _scheduler->reset();
  _transactList.clear();
  if (!_transactListReconfig.empty()) _transactListReconfig.clear();
  _nextTransaction=0;
  _lastTransaction=0;
  _masterNextTransaction=0;
  _busyCycles=0;
  _maxEndTime = 0;
  maxScale = 0;
  for(TaskList::const_iterator i = _taskList.begin(); i!= _taskList.end(); ++i){
    (*i)->setIsFirstTranExecuted(false);
  }
}

void FPGA::schedule2TXT(std::ofstream& myfile) const{
  myfile << "========= Scheduling for device: "<< _name << " =========\n" ;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
   std::cout<<"my transaction================================="<<std::endl;
    myfile << (*i)->toShortString() << std::endl;
  }
  std::cout<<"txt end========================"<<std::endl;
}

void FPGA::schedule2XML(std::ostringstream& glob,std::ofstream& myfile) const{
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
      (*i)->toXML(glob, 0, _name, _ID);
   //   myfile << glob.str() << std::endl;

  }
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

int FPGA::allTrans2XMLByTask(std::ostringstream& glob, std::string taskName) const {
  int total = 0;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
      (*i)->toXMLByTask(glob, 0, _name, _ID, taskName);
      total ++;
  }
  return total;
}

void FPGA::removeTrans(int numberOfTrans) {
    if (numberOfTrans == 1) {
        _transactList.clear();
    }
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


double FPGA::averageLoad (TMLTask* currTask) const{
  double _averageLoad=0;
  TMLTime _maxEndTime=0;
  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
    if( (*i)-> getCommand()->getTask() == currTask ){
      TMLTime _endTime= (*i)->getEndTime();
      _maxEndTime=max(_maxEndTime,_endTime);
    }
  }
  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
     if( (*i)-> getCommand()->getTask() == currTask ){
      _averageLoad += (*i)->getEndTime() - (*i)->getStartTime();
    }
  }
  if(_maxEndTime == 0)
    return 0;
  else {
    _averageLoad = (double)_averageLoad/_maxEndTime;
    return _averageLoad;
  }
  /*if( _maxEndTime == 0 ) 
    myfile << "average load is 0" << "<br>";
  else
  myfile<<" average load is "<<(double)_averageLoad/_maxEndTime<<"<br>";*/
 
}


void FPGA::drawPieChart(std::ofstream& myfile) const {      
  TMLTime _maxEndTime=0;

  for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
    if( (*i)-> getCommand()->getTask() ==  _htmlCurrTask ){
      TMLTime _endTime= (*i)->getEndTime();
      _maxEndTime=max(_maxEndTime,_endTime);
    }
  }
  std::map <TMLTask*, double > transPercentage;
  for( TransactionList::const_iterator i = _transactList.begin(); i!= _transactList.end(); ++i){
    if( (*i)-> getCommand()->getTask() ==  _htmlCurrTask ){
      transPercentage[(*i)-> getCommand()->getTask()]+=(double)((*i)->getEndTime()-(*i)->getStartTime())/_maxEndTime;      
    }
  }
  std::map <TMLTask*, double>::iterator iter = transPercentage.begin();
  myfile << "   var ctx" << _ID << "_"  << _htmlCurrTask->toShortString() << "= $(\"#pie-chartcanvas-" << _ID << "_" << _htmlCurrTask->toShortString() << "\");\n";
    
  double idle=1;
  myfile << "   var data" << _ID << "_" << _htmlCurrTask->toShortString() << " = new Array (";
  while( iter != transPercentage.end()){
    myfile << "\"" << iter->second << "\",";
    idle-=iter->second;
    ++iter;
  }
  myfile << "\"" << idle << "\");\n";
    
  myfile << "    var efficiency" << _ID << "_" << _htmlCurrTask->toShortString() << " = [];" << std::endl;
  myfile << "    var coloR" << _ID << "_" << _htmlCurrTask->toShortString() << " = [];" << std::endl;
  myfile << "    var dynamicColors" << _ID << "_" << _htmlCurrTask->toShortString() << SCHED_HTML_JS_FUNCTION;
    
  myfile << "    for (var i in data" << _ID << "_" << _htmlCurrTask->toShortString() << "){\n";
  myfile << "             efficiency" << _ID << "_" << _htmlCurrTask->toShortString() << ".push(data" << _ID << "_" << _htmlCurrTask->toShortString() << "[i]);\n";
  myfile << "             coloR" << _ID << "_" << _htmlCurrTask->toShortString() << ".push(dynamicColors" << _ID << "_" << _htmlCurrTask->toShortString() << "());\n";
  myfile << "}" << std::endl;
    
  myfile << "   var data" << _ID << "_" << _htmlCurrTask->toShortString() << " = { \n";
  myfile << "           labels : [";
  iter = transPercentage.begin();
  while( iter != transPercentage.end()){
    myfile << " \"" << iter->first->toString() << "\",";
    idle-=iter->second;
    ++iter;
  }        
  myfile << "\"idle time\"],\n";
  myfile << "          datasets : [\n \
                                     {\n \
                                           data : efficiency" << _ID << "_" << _htmlCurrTask->toShortString() << ",\n";
  myfile << "                            backgroundColor : coloR" << _ID << "_" << _htmlCurrTask->toShortString() << std::endl;
  // myfile << SCHED_HTML_JS_CONTENT1 << "Average load is " << averageLoad(_htmlCurrTask) << SCHED_HTML_JS_CONTENT2 << std::endl; 
  myfile << SCHED_HTML_JS_CONTENT1;
  myfile << "  var options" << _ID << "_" << _htmlCurrTask->toShortString() << SCHED_HTML_JS_CONTENT3;
  myfile << _name << "_" << _htmlCurrTask->toShortString() << ": Average load is " << std::setprecision(2) << averageLoad(_htmlCurrTask) << SCHED_HTML_JS_CONTENT2 << std::endl; 
     
}

void FPGA::buttonPieChart(std::ofstream& myfile) const{
  //  myfile << "$(\"#" << _ID << "\").click(function() {\n";
  for(TaskList::const_iterator i = _taskList.begin(); i!= _taskList.end(); ++i){ 
    myfile << "    var chart" << _ID << "_" << (*i)->toShortString() << " = new Chart( "<<
      "ctx" << _ID << "_" << (*i)->toShortString() << ", {\n \
              type : \"pie\",\n";
    myfile << "               data : data" << _ID << "_" << (*i)->toShortString() <<",\n";
    myfile << "               options : options" << _ID << "_" << (*i)->toShortString() << std::endl;
    myfile << "                   });" << std::endl;
    // myfile << "   chart" << _ID << "_" << (*i)->toShortString() << SCHED_HTML_JS_HIDE;
    myfile << "   if(!" << SHOW_PIE_CHART << "){\n \t"
        << "      document.getElementById(\"pie-chartcanvas-" << _ID << "_" << (*i)->toShortString() << "\"" << ").style.display = \"none\";\n \t"
        << "   }\n \t"
        << "   else {\n \t"
        << "      document.getElementById(\"pie-chartcanvas-" << _ID << "_" << (*i)->toShortString() << "\"" << ").style.display = \"block\";\n \t"
        << "   }\n \t";
    myfile << "   chart" << _ID << "_" << (*i)->toShortString() << ".update();" << std::endl;
  
  }
}


void FPGA::showPieChart(std::ofstream& myfile) const{
  myfile << SCHED_HTML_JS_DIV_BEGIN2 << std::endl;
  myfile << SCHED_HTML_JS_BEGIN_CANVAS << _ID << "_" << _htmlCurrTask->toShortString() << SCHED_HTML_JS_END_CANVAS <<std::endl;
  myfile << SCHED_HTML_JS_DIV_END << std::endl;
}

std::string FPGA::determineHTMLCellClass(unsigned int &nextColor ) {
	std::map<TMLTask*, std::string>::const_iterator it = taskCellClasses.find( _htmlCurrTask );

	if ( it == taskCellClasses.end() ) {
		unsigned int aColor = nextColor % NB_HTML_COLORS;
		std::ostringstream cellClass;
		cellClass << "t" << aColor;
		taskCellClasses[  _htmlCurrTask ] = cellClass.str();
		nextColor++;
	}

	return taskCellClasses[  _htmlCurrTask ];
}

std::string FPGA::determineHTMLCellClass(std::map<TMLTask*, std::string> &taskColors, TMLTask* task, unsigned int &nextColor) {
	std::map<TMLTask*, std::string>::const_iterator it = taskColors.find( task );

	if ( it == taskColors.end() ) {
		unsigned int aColor = nextColor % NB_HTML_COLORS;
		std::ostringstream cellClass;
		cellClass << "t" << aColor;
		taskColors[ task ] = cellClass.str();
		nextColor++;
	}

	return taskColors[ task ];
}

std::map<TMLTask*, std::string> FPGA::HWTIMELINE2HTML(std::ostringstream& myfile,std::map<TMLTask*, std::string> taskCellClasses1,unsigned int nextCellClassIndex1, std::string& iTracetaskList) {
    TransactionList _transactListClone;
    std::string taskList = iTracetaskList.c_str();
    for (int z = 0; z < _transactList.size(); z++) {
        std::string taskName = _transactList[z]->getCommand()->getTask()->toString();
        std::size_t pos = taskList.find(taskName); /*pos1 = position of "bin" if we working with open model*/
        if(pos != std::string::npos){
            _transactListClone.push_back(_transactList[z]);
        }
    }
    if ( _transactListClone.size() == 0 ) {
        std::cout << "Device never activated" << std::endl;
    } else {
        if(_startFlagHTML == true){
            myfile << "<tr><td title = \"Average load: " << std::setprecision(2) << averageLoad(_htmlCurrTask) << "; Utilization: " << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << "\" width=\"170px\" style=\"max-width: unset;min-width: 170px;background-color: aqua;\">" <<  _name << "</td><td class=\"notfirst\"></td><td class=\"notlast\"></td>";
        } else if (_htmlCurrTask->getEndLastTransaction() > 0) {
            myfile << "<tr><td title = \"Average load: " << std::setprecision(2) << averageLoad(_htmlCurrTask) << "; Utilization: " << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << "\" width=\"170px\" style=\"max-width: unset;min-width: 170px;border-style: none none none none;\"></td><td class=\"notfirst\"></td><td class=\"notlast\"></td>";
        }
        TMLTime aCurrTime = 0;
        unsigned int taskOccurTime = 0;
        unsigned int tempReduce = 0;
        std::vector<unsigned int> listScale;
        std::vector<unsigned int> listScaleTime;
        listScale.push_back(0);
        listScaleTime.push_back(0);
        bool changeCssClass = false;
        for( TransactionList::const_iterator i = _transactListClone.begin(); i != _transactListClone.end(); ++i ) {
        #ifdef DEBUG_FPGA
          std::cout <<  (*i)-> getCommand()->getTask()->toString() <<std::endl;
          std::cout<< _htmlCurrTask->toString()<<std::endl;
        #endif
          if( (*i)-> getCommand()->getTask() == _htmlCurrTask ){
        if(taskOccurTime==0){
          taskOccurTime++;
        }
        #ifdef DEBUG_FPGA
        std::cout<<"in!!"<<_htmlCurrTask->toString()<<std::endl;
        #endif
        TMLTransaction* aCurrTrans = *i;
        bool reconfigCheck = false;
        if (!_transactListReconfig.empty()) {
            std::vector<TMLTransaction*>::iterator it = std::find (_transactListReconfig.begin(), _transactListReconfig.end(), aCurrTrans);
                if (it != _transactListReconfig.end()) {
                    reconfigCheck = true;
                }
        }

        unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
        bool isBlankTooBig = false;
        std::ostringstream tempString, tempReconfigIdle;
        int tempBlanks;
        if(_htmlCurrTask->getEndLastTransaction() >= MIN_RESIZE_THRESHOLD && aBlanks > MIN_RESIZE_TRANS) {
            int newBlanks = 0;
            if (aBlanks > 100000) {
                newBlanks = (int) aBlanks/100;
            } else if (aBlanks > 250) {
                newBlanks = (int) aBlanks/20;
            } else {
                newBlanks = 10;
            }
            tempBlanks = aBlanks;
            tempReduce += aBlanks - newBlanks;
            aBlanks = newBlanks;
            isBlankTooBig = true;
            changeCssClass = true;
        }
        if (reconfigCheck) {
            tempReconfigIdle << "dynamic reconfiguration";
        } else {
            tempReconfigIdle << " ";
        }
        if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
            listScale.push_back(aBlanks+1);
            tempString << tempBlanks+1;
            if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
                listScaleTime.push_back(aCurrTrans->getStartTime()+1);
            }
            if (isBlankTooBig){
                myfile << "<td colspan=\""<< aBlanks+1 <<"\" title=\"idle time " + tempReconfigIdle.str() + "\" class=\"not\">" << "<- idle " + tempString.str() + " ->" << "</td>";
            } else {
                myfile << "<td colspan=\""<< aBlanks+1 <<"\" title=\"idle time " + tempReconfigIdle.str() + "\" class=\"not\"></td>";
            }
        } else if ( aBlanks > 0 ){
            listScale.push_back(aBlanks);
            tempString << tempBlanks;
            if(aCurrTrans->getStartTime() > listScaleTime.back()){
                listScaleTime.push_back(aCurrTrans->getStartTime());
            }
            if (isBlankTooBig){
                myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time " + tempReconfigIdle.str() + "\" class=\"not\">" << "<- idle " + tempString.str() + " ->" << "</td>";
            } else {
                myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time " + tempReconfigIdle.str() + "\" class=\"not\"></td>";
            }
        }

        unsigned int aLength = aCurrTrans->getOperationLength();
        const std::string cellClass = determineHTMLCellClass( taskCellClasses1, _htmlCurrTask, nextCellClassIndex1);
        std::string aCurrTransName=aCurrTrans->toShortString();
        unsigned int indexTrans=aCurrTransName.find_first_of(":");
        std::string aCurrContent=aCurrTransName.substr(indexTrans+1,2);
        if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
          if(_htmlCurrTask->getEndLastTransaction() >= MIN_RESIZE_THRESHOLD && aLength > MIN_RESIZE_TRANS){
              int tempLength = 0;
              if (aLength > 100000) {
                  tempLength = (int) aLength/100;
              } else if (aLength > 250) {
                  tempLength = (int) aLength/20;
              } else {
                  tempLength = 10;
              }
              tempReduce += aLength - tempLength;
              aLength = tempLength;
          }
          std::string aCurrFullTransName = aCurrTrans->toString();
          unsigned int indexTrans1 = aCurrFullTransName.find("len:");
          unsigned int indexTrans2 = aCurrFullTransName.find("params");
          std::string transName = aCurrTransName.substr(0, indexTrans + 1) + " " + aCurrFullTransName.substr(0, indexTrans1) + " --Attributes " + aCurrFullTransName.substr(indexTrans2, aCurrFullTransName.length());
          myfile << "<td colspan=\"" << aLength << "\" title=\"" << transName << "\" class=\""<< cellClass <<"\">"<< aCurrContent <<"</td>";
//          writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString(), aCurrContent );
          listScale.push_back(aLength);
          if(aCurrTrans->getStartTime() > listScaleTime.back()){
             listScaleTime.push_back(aCurrTrans->getStartTime());
          }
          if(aCurrTrans->getEndTime() > listScaleTime.back()){
             listScaleTime.push_back(aCurrTrans->getEndTime());
          }
        }
        if(aCurrTrans->getCommand()->getTask()->getIsDaemon() == true && aCurrTrans->getEndTime() > _simulatedTime)
          aCurrTime = _simulatedTime;
        else
          aCurrTime = aCurrTrans->getEndTime();
          }
        }
        if(listScale.size() > 1) {
                    myfile << "</tr>" << "<tr>";
                    myfile << "<td width=\"170px\" style=\"max-width: unset;min-width: 170px;border-style: none none none none;\"></td><td class=\"notfirst\"></td><td class=\"notlast\"></td>";

                    for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
                      std::ostringstream spanVal;
                      if(aLength < listScaleTime.size())
                        spanVal << listScaleTime[aLength];
                      else
                        spanVal << "";
                      if(aLength+1 >= listScale.size()){

                        if(changeCssClass){
                             myfile << "<td colspan=\"5\" class=\"sc1\">" << spanVal.str() << "</td>";
            //                 writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
                        } else
                             myfile << "<td colspan=\"5\" class=\"sc\">" << spanVal.str() << "</td>";
            //                 writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
                      } else {
                            if(changeCssClass){
                                myfile << "<td colspan=\"" << listScale[aLength+1] << "\" class=\"sc1\">" << spanVal.str() << "</td>";
            //                    writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
                            } else
                                myfile << "<td colspan=\"" << listScale[aLength+1] << "\" class=\"sc\">" << spanVal.str() << "</td>";
            //                     writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
                      }
                    }
                    myfile << "</tr>" << "<tr>";

                    for ( unsigned int aLength = 0; aLength < 3; aLength++ ) {
                        if( aLength == 1) {
                          myfile << "<th class=\"notfirst\">";
                        } else {
                          myfile << "<th></th>";
                        }
                    }
                    if (aCurrTime - tempReduce + 2 > maxScale) {
                        maxScale = aCurrTime - tempReduce + 2;
                    }
                    myfile << "</tr>";
                    myfile << SCHED_HTML_JS_CLEAR << std::endl;
        }

    }
    return taskCellClasses1;
}

void FPGA::HW2HTML(std::ofstream& myfile)  {    
  if(_startFlagHTML == true){
    //myfile << "<h2><span>Scheduling for device: "<< _name << "</span></h2>" << std::endl;
    myfile << SCHED_HTML_BOARD;
    myfile << _name << END_TD << "</tr>" << std::endl;
    myfile << SCHED_HTML_JS_TABLE_END << std::endl;
    myfile << SCHED_HTML_JS_DIV_BEGIN3  << std::endl;
  }

  if ( _transactList.size() == 0 ) {
    myfile << "<h4>Device never activated</h4>" << std::endl;
    myfile << SCHED_HTML_JS_TABLE_END << std::endl << SCHED_HTML_JS_CLEAR << std::endl;
  }
   else {
     myfile << "<table>" << std::endl << "<tr>";
    TMLTime aCurrTime = 0;
    unsigned int taskOccurTime = 0;
    unsigned int tempReduce = 0;
    std::vector<unsigned int> listScale;
    std::vector<unsigned int> listScaleTime;
    listScale.push_back(0);
    listScaleTime.push_back(0);
    bool changeCssClass = false;
    for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
#ifdef DEBUG_FPGA
      std::cout <<  (*i)-> getCommand()->getTask()->toString() <<std::endl;
      std::cout<< _htmlCurrTask->toString()<<std::endl;
#endif
      if( (*i)-> getCommand()->getTask() == _htmlCurrTask ){
	if(taskOccurTime==0){ 
	  taskOccurTime++;
	}
#ifdef DEBUG_FPGA
	std::cout<<"in!!"<<_htmlCurrTask->toString()<<std::endl;
#endif
	TMLTransaction* aCurrTrans = *i;
	unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
	bool isBlankTooBig = false;
	std::ostringstream tempString;
	int tempBlanks;
	if(_htmlCurrTask->getEndLastTransaction() >= 250 && aBlanks >10) {
	    int newBlanks = 10;
	    tempBlanks = aBlanks;
	    tempReduce += aBlanks - newBlanks;
	    aBlanks = newBlanks;
	    isBlankTooBig = true;
	    changeCssClass = true;
	}
	if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
	    listScale.push_back(aBlanks+1);
	    tempString << tempBlanks+1;
	    if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime()+1);
        }
	    if (isBlankTooBig){
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
	    } else {
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time" );
	    }
	}
	else if ( aBlanks > 0 ){
	    listScale.push_back(aBlanks);
	    tempString << tempBlanks;
	    if(aCurrTrans->getStartTime() > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime());
        }
	    if (isBlankTooBig){
            writeHTMLColumn( myfile, aBlanks, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
        } else {
            writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
        }
	}

	unsigned int aLength = aCurrTrans->getOperationLength();


	// Issue #4
	//	std::cout<<"what is this task?"<<task->toString()<<std::endl;
	const std::string cellClass = determineHTMLCellClass(  nextCellClassIndex );
	std::string aCurrTransName=aCurrTrans->toShortString();
	unsigned int indexTrans=aCurrTransName.find_first_of(":");
	std::string aCurrContent=aCurrTransName.substr(indexTrans+1,2);
	if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
      if(_htmlCurrTask->getEndLastTransaction() >= 250 && aLength >10){
          tempReduce += aLength - 10;
          aLength = 10;
      }
      writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString(), aCurrContent );
      listScale.push_back(aLength);
      if(aCurrTrans->getStartTime() > listScaleTime.back()){
         listScaleTime.push_back(aCurrTrans->getStartTime());
      }
      if(aCurrTrans->getEndTime() > listScaleTime.back()){
         listScaleTime.push_back(aCurrTrans->getEndTime());
      }
    }
	if(aCurrTrans->getCommand()->getTask()->getIsDaemon() == true && aCurrTrans->getEndTime() > _simulatedTime)
	  aCurrTime = _simulatedTime;
	else
	  aCurrTime = aCurrTrans->getEndTime();
      }
    }

    myfile << "</tr>" << std::endl << "<tr>";
    for ( unsigned int aLength = 0; aLength < aCurrTime - tempReduce; aLength++ ) {
      myfile << "<th></th>";
    }

    myfile << "</tr>" << std::endl << "<tr>";
    for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
      std::ostringstream spanVal;
      if(aLength < listScaleTime.size())
        spanVal << listScaleTime[aLength];
      else
        spanVal << "";
      if(aLength+1 >= listScale.size()){

        if(changeCssClass){
             writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
        } else
             writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
      }else {
        if(changeCssClass){
            writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
        } else
             writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
      }
      //myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
    }
   }
}

void FPGA::schedule2HTML(std::ofstream& myfile)  {    
  std::cout<<"*********FPGA scheduling***********"<<std::endl;
  if(_startFlagHTML == true){
    myfile << "<h2><span>Scheduling for device: "<< _name << "</span></h2>" << std::endl;
  }

  if ( _transactList.size() == 0 ) {
    myfile << "<h4>Device never activated</h4>" << std::endl;
  }
   else {
    myfile << "<table>" << std::endl << "<tr>";

    TMLTime aCurrTime = 0;
    unsigned int taskOccurTime = 0;
    unsigned int tempReduce = 0;
    std::vector<unsigned int> listScale;
    std::vector<unsigned int> listScaleTime;
    listScale.push_back(0);
    listScaleTime.push_back(0);
    bool changeCssClass = false;
    for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
      //#ifdef DEBUG_FPGA
      std::cout <<  (*i)-> getCommand()->getTask()->toString() <<std::endl;
      std::cout<< _htmlCurrTask->toString()<<std::endl;
      //#endif
      if( (*i)-> getCommand()->getTask() == _htmlCurrTask ){
	if(taskOccurTime==0){
	  taskOccurTime++;
	}
#ifdef DEBUG_FPGA
	std::cout<<"in!!"<<_htmlCurrTask->toString()<<std::endl;
#endif
	TMLTransaction* aCurrTrans = *i;
	unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
	//std::cout<<"blank is "<<aBlanks<<std::endl;
    bool isBlankTooBig = false;
    std::ostringstream tempString;
    int tempBlanks;
    if(_htmlCurrTask->getEndLastTransaction() >= 250 && aBlanks >10) {
        int newBlanks = 10;
        tempBlanks = aBlanks;
        tempReduce += aBlanks - newBlanks;
        aBlanks = newBlanks;
        isBlankTooBig = true;
        changeCssClass = true;
    }

	if ( aBlanks >= 0 && (!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction()) ){
	    listScale.push_back(aBlanks+1);
	    tempString << tempBlanks+1;
	    if(aCurrTrans->getStartTime()+1 > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime()+1);
        }
	    if (isBlankTooBig){
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
	    } else {
	        writeHTMLColumn( myfile, aBlanks+1, "not", "idle time" );
	    }
	}
	else if ( aBlanks > 0 ){
	    listScale.push_back(aBlanks);
	    tempString << tempBlanks;
	    if(aCurrTrans->getStartTime() > listScaleTime.back()){
            listScaleTime.push_back(aCurrTrans->getStartTime());
        }
	    if (isBlankTooBig){
            writeHTMLColumn( myfile, aBlanks, "not", "idle time", "<- idle " + tempString.str() + " ->", false );
        } else {
            writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
        }
	}

	unsigned int aLength = aCurrTrans->getOperationLength();


	// Issue #4
	TMLTask* task = aCurrTrans->getCommand()->getTask();
	//	std::cout<<"what is this task?"<<task->toString()<<std::endl;
	const std::string cellClass = determineHTMLCellClass(  nextCellClassIndex );
    if(!(!(aCurrTrans->getCommand()->getActiveDelay()) && aCurrTrans->getCommand()->isDelayTransaction())){
      if(_htmlCurrTask->getEndLastTransaction() >= 250 && aLength >10){
          tempReduce += aLength - 10;
          aLength = 10;
      }
      writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString() );
      listScale.push_back(aLength);
      if(aCurrTrans->getStartTime() > listScaleTime.back()){
         listScaleTime.push_back(aCurrTrans->getStartTime());
      }
      if(aCurrTrans->getEndTime() > listScaleTime.back()){
        listScaleTime.push_back(aCurrTrans->getEndTime());
      }
    }

	if(aCurrTrans->getCommand()->getTask()->getIsDaemon() == true && aCurrTrans->getEndTime() > _simulatedTime)
	  aCurrTime = _simulatedTime;
	else
	  aCurrTime = aCurrTrans->getEndTime();
      }
    }
		

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength < aCurrTime - tempReduce; aLength++ ) {
      myfile << "<th></th>";
    }

    myfile << "</tr>" << std::endl << "<tr>";
    for ( unsigned int aLength = 0; aLength < listScale.size(); aLength += 1 ) {
      std::ostringstream spanVal;
      if(aLength < listScaleTime.size())
        spanVal << listScaleTime[aLength];
      else
        spanVal << "";
      if(aLength+1 >= listScale.size()){

        if(changeCssClass){
            writeHTMLColumn( myfile, 5, "sc1",  spanVal.str(), spanVal.str(), false );
        } else
         writeHTMLColumn( myfile, 5, "sc", spanVal.str(), spanVal.str(), false );
      }else {
        if(changeCssClass){
            writeHTMLColumn( myfile, listScale[aLength+1], "sc1", spanVal.str(), spanVal.str(), false );
        } else
           writeHTMLColumn( myfile, listScale[aLength+1], "sc", spanVal.str(), spanVal.str(), false );
        }
      //myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
    }

    myfile << "</tr>" << std::endl << "</table>" << std::endl;
   
   }
#ifdef DEBUG_FPGA
  std::cout<<"end in!!!"<<std::endl;
#endif
}

void FPGA::scheduleBlank(std::ofstream& myfile){ 
  myfile  << "<table>" << std::endl << "<tr>" << std::endl;
  for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
    TMLTask* task = (*taskColIt).first;
    // Unset the default td max-width of 5px. For some reason setting the max-with on a specific t style does not work
    myfile << "<td class=\"" << taskCellClasses[ task ] << "\"></td><td style=\"max-width: unset;\">" << task->toString() << "</td><td class=\"space\"></td>";
  }
  myfile << "</tr>" << std::endl;
  myfile << "</table>" << std::endl;
}
