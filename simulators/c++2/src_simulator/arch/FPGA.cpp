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
	       unsigned int iChangeIdleModeCycles, 
	       unsigned int iCyclesBeforeIdle,
	       unsigned int iCyclesPerExeci, 
	       unsigned int iCyclesPerExecc ) : SchedulableDevice(iID, iName, iScheduler)
					      ,_reconfigTime(iReconfigTime)
					      ,_lastTransaction(0)
					      ,_changeIdleModeCycles(iChangeIdleModeCycles)
					      ,_cyclesBeforeIdle(iCyclesBeforeIdle)
					      ,_cyclesPerExeci(iCyclesPerExeci)
					      ,_cyclesPerExecc(iCyclesPerExecc)
					      ,_taskNumber(0)
					      ,_currTaskNumber(0)
					     
{}

FPGA::~FPGA(){}


void FPGA::streamBenchmarks(std::ostream& s) const{
  std::cout<<"test fpga stramBenchmarks"<<std::endl;
  s << TAG_FPGAo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl;
  if (_simulatedTime!=0) s << TAG_UTILo << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << TAG_UTILc;
  s << TAG_ENERGYo << ( (_simulatedTime)*_static_consumPerCycle) + ((_busyCycles)*_dynamic_consumPerCycle) << TAG_ENERGYc;
  std::cout<< "power consumption "<< ((_simulatedTime)*_static_consumPerCycle) + ((_busyCycles)*_dynamic_consumPerCycle)<< std::endl;
  for(BusMasterList::const_iterator i=_busMasterList.begin(); i != _busMasterList.end(); ++i) (*i)->streamBenchmarks(s);
  s << TAG_FPGAc;
}

TMLTransaction* FPGA::getNextTransaction(){
 std::cout<<"fpga getNextTransaction"<<_name<<" ";
  #ifdef BUS_ENABLED
  if (_masterNextTransaction==0 || _nextTransaction==0){
    if(_masterNextTransaction == 0) std::cout<<"master is 0"<<std::endl;
    if(_nextTransaction==0) std::cout<<"nexttrans is 0"<<std::endl;
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
    if(_nextTransaction)std::cout<<"haha1"<<_nextTransaction->toString()<<std::endl;
    return (aResult)?_nextTransaction:0;
  }
#else
  if(_nextTransaction)std::cout<<"haha2"<<_nextTransaction->toString()<<std::endl;

  return _nextTransaction;
#endif
 }

void FPGA::calcStartTimeLength(){
  std::cout<<"fpga calStartTimeLength "<<std::endl;
  
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

  TMLTime aStartTime = _nextTransaction->getRunnableTime();
  std::cout<<"start time !!!!!!!!!!"<<_nextTransaction->toShortString()<<"is "<<aStartTime<<std::endl;
  //or setStartTime(0)???
  _nextTransaction->setStartTime(aStartTime);

#ifdef BUS_ENABLED
  if (_masterNextTransaction==0){
#endif  
    _nextTransaction->setLength(max(_nextTransaction->getVirtualLength(),(TMLTime)1));
  }
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
    _nextTransaction->setLength(_nextTransaction->getVirtualLength());
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
    //std::cout<<"I am in finish!!!"<<std::endl;
    _endSchedule=0;
    _simulatedTime=max(_simulatedTime,_endSchedule);
    _overallTransNo++; //NEW!!!!!!!!
    _overallTransSize+=_nextTransaction->getOperationLength();  //NEW!!!!!!!!
    //std::cout << "lets crash execute\n";

    // std::cout<<_nextTransaction->toString()<<std::endl;
    if(_nextTransaction->getCommand()==0) std::cout<<"d"<<std::endl;
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
  _scheduler->schedule(_endSchedule);
  TMLTransaction* aOldTransaction = _nextTransaction;
  _nextTransaction=_scheduler->getNextTransaction(_endSchedule);
  /* TaskList::const_iterator iter_task=_taskList.begin();
  std::advance(iter_task,_transNumber);
   if(iter_task!=_taskList.end()){    
     _nextTransaction=(*iter_task)->getNextTransaction(_endSchedule);
    if(_nextTransaction!=0 && _nextTransaction->getVirtualLength()==0){
      _nextTransaction=0;
      _transNumber=0;
    }
    else if(++iter_task==_taskList.end())
      _transNumber=0;
    else if(_nextTransaction->getCommand()->getProgress()==_nextTransaction->getLength())
      _transNumber++;
   }
  */
  if (aOldTransaction!=0 && aOldTransaction!=_nextTransaction){ //NEW 
    if (_masterNextTransaction!=0) {
      _masterNextTransaction->registerTransaction(0);

    }
  }
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
          
	  std::cout<<"END_TASK_CPU"<<std::endl;
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
	  std::cout<<"END_IDLE_CPU"<<std::endl;
	  
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
  std::cout<<"fpga draw pie chart"<<std::endl;
   TMLTime _maxEndTime=0;
   
   for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
     if( (*i)-> getCommand()->getTask() ==  _htmlCurrTask ){
       TMLTime _endTime= (*i)->getEndTime();
       _maxEndTime=max(_maxEndTime,_endTime);
     }
   }
   std::cout<<"max end time is "<<_maxEndTime<<std::endl;
   std::map <TMLTask*, double > transPercentage;
 
   for( TransactionList::const_iterator i = _transactList.begin(); i!= _transactList.end(); ++i){
     if( (*i)-> getCommand()->getTask() ==  _htmlCurrTask ){
       transPercentage[(*i)-> getCommand()->getTask()]+=(double)((*i)->getEndTime()-(*i)->getStartTime())/_maxEndTime;      
     }
   }
  
   std::map <TMLTask*, double>::iterator iter = transPercentage.begin();
   myfile << "     var chart" << _ID << "_" <<  _htmlCurrTask->toShortString() << "= new CanvasJS.Chart(\"chartContainer" << _ID << "_" <<   _htmlCurrTask->toShortString() <<"\"," << std::endl;
   myfile <<  SCHED_HTML_JS_CONTENT2 << "Average load is " << averageLoad( _htmlCurrTask) <<  SCHED_HTML_JS_CONTENT3 << std::endl;
   double idle=1;
   while( iter != transPercentage.end()){
     myfile << "                { y:" << (iter->second)*100 << ", indexLabel: \"" << iter->first->toString() << "\" }," << std::endl;
     idle-=iter->second;
     ++iter;  
   }
   myfile << "                { y:" << idle*100 << ", indexLabel: \"idle time\"" << " }" << std::endl;
   myfile << std::endl;
   myfile << SCHED_HTML_PIE_END;
   myfile << "chart" << _ID << "_" <<   _htmlCurrTask->toShortString() << ".render();" << std::endl;
   
  
}


void FPGA::showPieChart(std::ofstream& myfile) const{
  if(_taskNumber==1)
    myfile << SCHED_HTML_JS_DIV_ID << _ID << "_" << _htmlCurrTask->toShortString() << SCHED_HTML_JS_DIV_ID_END <<std::endl;
  else
    myfile << SCHED_HTML_JS_DIV_ID << _ID << "_" << _htmlCurrTask->toShortString() << SCHED_HTML_JS_DIV_ID_END_FPGA <<std::endl;
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

void FPGA::schedule2HTML(std::ofstream& myfile)  {    
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
    for( TransactionList::const_iterator i = _transactList.begin(); i != _transactList.end(); ++i ) {
      std::cout <<  (*i)-> getCommand()->getTask()->toString() <<std::endl;
      std::cout<< _htmlCurrTask->toString()<<std::endl;
      if( (*i)-> getCommand()->getTask() == _htmlCurrTask ){
	if(taskOccurTime==0){
	  _currTaskNumber++;
	  taskOccurTime++;
	}
	std::cout<<"in!!"<<_htmlCurrTask->toString()<<std::endl;
	TMLTransaction* aCurrTrans = *i;
	unsigned int aBlanks = aCurrTrans->getStartTime() - aCurrTime;
	std::cout<<"blank is "<<aBlanks<<std::endl;
	if ( aBlanks > 0 ) {
	  writeHTMLColumn( myfile, aBlanks, "not", "idle time" );
	}

	unsigned int aLength = aCurrTrans->getOperationLength();


	// Issue #4
	TMLTask* task = aCurrTrans->getCommand()->getTask();
	std::cout<<"what is this task?"<<task->toString()<<std::endl;
	const std::string cellClass = determineHTMLCellClass(  nextCellClassIndex );

	writeHTMLColumn( myfile, aLength, cellClass, aCurrTrans->toShortString() );

	aCurrTime = aCurrTrans->getEndTime();
      }
    }
		

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength < aCurrTime; aLength++ ) {
      myfile << "<th></th>";
    }

    myfile << "</tr>" << std::endl << "<tr>";

    for ( unsigned int aLength = 0; aLength <= aCurrTime; aLength += 5 ) {
      std::ostringstream spanVal;
      spanVal << aLength;
      writeHTMLColumn( myfile, 5, "sc", "", spanVal.str(), false );
      //myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
    }

    myfile << "</tr>" << std::endl << "</table>" << std::endl;
    std::cout<<"_taskNumer is ------------------------------------"<<_taskNumber<<std::endl;
    std::cout<<"curr task number is ------------------------"<<_currTaskNumber<<std::endl;
    if(_currTaskNumber == _taskNumber){
      std::cout<<" i am showing the name of tasks!!!----------------------------------------------------------------------------------!"<<std::endl;
      myfile  << "<table>" << std::endl << "<tr>" << std::endl;
      for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
	TMLTask* task = (*taskColIt).first;
	// Unset the default td max-width of 5px. For some reason setting the max-with on a specific t style does not work
	myfile << "<td class=\"" << taskCellClasses[ task ] << "\"></td><td style=\"max-width: unset;\">" << task->toString() << "</td><td class=\"space\"></td>";
      }
      myfile << "</tr>" << std::endl;
      myfile << "</table>" << std::endl;
    }

   
   }
  std::cout<<"end in!!!"<<std::endl;
}
