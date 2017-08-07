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
#include <Simulator.h>
//#include <Server.h>
//#include <ServerLocal.h>
#include <SimServSyncInfo.h>
#include <TMLCommand.h>
#include <TMLTransaction.h>
#include <SimComponents.h>
#include <IndeterminismSource.h>
#include <CPU.h>
#include <TMLTask.h>
#include <TMLChannel.h>
#include <ServerIF.h>
#include <Server.h>
#include <ServerLocal.h>
#include <TMLEventChannel.h>
#ifdef EBRDD_ENABLED
#include <EBRDD.h>
#include <EBRDDCommand.h>
#include <ERC.h>
#endif


Simulator::Simulator(SimServSyncInfo* iSyncInfo):_syncInfo(iSyncInfo), _simComp(_syncInfo->_simComponents), _busy(false), _simTerm(false),  _randChoiceBreak(_syncInfo->_simComponents), _wasReset(true), _longRunTime(0), _shortRunTime(-1), _replyToServer(true), _branchCoverage(60), _commandCoverage(100), _terminateExplore(false), _simDuration(0){
}

Simulator::~Simulator(){
  //if (_currCmdListener!=0) delete _currCmdListener;
  //if (_randChoiceBreak!=0) delete _randChoiceBreak;
}

TMLTransaction* Simulator::getTransLowestEndTime(SchedulableDevice*& oResultDevice) const{
  //int tmp=0;
  TMLTransaction *aMarker=0, *aTempTrans;
  TMLTime aLowestTime=-1;
  SchedulableDevice* aTempDevice;
  
  //static unsigned int aTransitionNo=0;
#ifdef DEBUG_KERNEL
  std::cout << "kernel:getTLET: before loop" << std::endl;
#endif
  //for(SchedulingList::const_iterator i=_simComp->_cpuList.begin(); i != _simComp->_cpuList.end(); ++i){
  //for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    aTempDevice=*i;
    aTempTrans=aTempDevice->getNextTransaction();
    if (aTempTrans!=0 && aTempTrans->getVirtualLength()>0){
#ifdef DEBUG_KERNEL
      std::cout << "kernel:getTLET: transaction found on " << aTempDevice->toString() << ": " << aTempTrans->toString() << std::endl;
#endif
      //tmp++;
      if (aTempTrans->getEndTime() < aLowestTime){
        aMarker=aTempTrans;
        aLowestTime=aTempTrans->getEndTime();
        oResultDevice=aTempDevice;
      }
    }
    //#ifdef DEBUG_KERNEL
    else {
      /*if (!_simComp->couldCPUBeIdle(*i)){
        std::cout << "kernel:getTLET: no transaction found on " << aTempDevice->toString() << std::endl;
        std::cout << "Cry !!!!!!!!";
        //exit(1);
        }*/
    }
    //#endif
  }
  //if (tmp==1) std::cout << "trans only on one CPU " << oResultDevice->toString() << "\n";
  return aMarker;
}


ID Simulator::schedule2GraphAUT(std::ostream& iAUTFile, ID iStartState, unsigned int& oTransCounter) const{
  CPUList::iterator i;
  //std::cout << "entry graph output\n";
  GraphTransactionQueue aQueue;
  TMLTransaction* aTrans, *aTopElement;
  ID aStartState=iStartState, aEndState=0;
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    aTrans = (*i)->getTransactions1By1(true);
    if (aTrans!=0) aQueue.push(aTrans);
  }
  //std::ostringstream aOutp;
  while (!aQueue.empty()){
    CPU* aCPU;
    aTopElement = aQueue.top();
    aCPU = aTopElement->getCommand()->getTask()->getCPU();
    aEndState = aTopElement->getStateID();
    if (aEndState==0){
      aEndState=TMLTransaction::getID();
      TMLTransaction::incID();
    }
    //13 -> 17 [label = "i(CPU0__test1__TMLTask_1__wro__test1__ch<4 ,4>)"];
    oTransCounter++;
    //(20,"i(CPU0__test1__TMLTask_1__wr__test1__ch<4 ,4>)", 24)
    //std::cout << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
    iAUTFile << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
    if (aTopElement->getChannel()!=0){
      iAUTFile << "__" << aTopElement->getChannel()->toShortString();
      //std::cout << "__" << aTopElement->getChannel()->toShortString();
    }
    iAUTFile << "<" << aTopElement->getVirtualLength() << ">)\"," << aEndState <<")\n";
    //std::cout << "<" << aTopElement->getVirtualLength() << ">)\"," << aEndState <<")\n";
    aStartState = aEndState;
    aQueue.pop();
    aTrans = aCPU->getTransactions1By1(false);
    if (aTrans!=0) aQueue.push(aTrans);
  }
  //std::cout << "exit graph output\n";
  return aStartState;
}


ID Simulator::schedule2GraphDOT(std::ostream& iDOTFile, std::ostream& iAUTFile, ID iStartState, unsigned int& oTransCounter) const{
  CPUList::iterator i;
  //std::cout << "entry graph output\n";
  GraphTransactionQueue aQueue;
  TMLTransaction* aTrans, *aTopElement;
  ID aStartState=iStartState, aEndState=0;
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    aTrans = (*i)->getTransactions1By1(true);
    if (aTrans!=0) aQueue.push(aTrans);
  }
  //std::ostringstream aOutp;
  while (!aQueue.empty()){
    CPU* aCPU;
    aTopElement = aQueue.top();
    aCPU = aTopElement->getCommand()->getTask()->getCPU();
    aEndState = aTopElement->getStateID();
    if (aEndState==0){
      aEndState=TMLTransaction::getID();
      TMLTransaction::incID();
    }
    //13 -> 17 [label = "i(CPU0__test1__TMLTask_1__wro__test1__ch<4 ,4>)"];
    oTransCounter++;
    iDOTFile << aStartState << " -> " << aEndState << " [label = \"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
    if (aTopElement->getChannel()!=0){
      iDOTFile << "__" << aTopElement->getChannel()->toShortString();
    }
    iDOTFile << "<" << aTopElement->getVirtualLength() << ">)\"]\n";
    //(20,"i(CPU0__test1__TMLTask_1__wr__test1__ch<4 ,4>)", 24)
    iAUTFile << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
    if (aTopElement->getChannel()!=0){
      iAUTFile << "__" << aTopElement->getChannel()->toShortString();
    }
    iAUTFile << "<" << aTopElement->getVirtualLength() << ">)\"," << aEndState <<")\n";
    aStartState = aEndState;
    aQueue.pop();
    aTrans = aCPU->getTransactions1By1(false);
    if (aTrans!=0) aQueue.push(aTrans);
  }
  //std::cout << "exit graph output\n";
  return aStartState;
}

void Simulator::schedule2Graph(std::string& iTraceFileName) const{
  struct timeval aBegin,aEnd;
  gettimeofday(&aBegin,NULL);
  std::ofstream myfile (iTraceFileName.c_str());
  if (myfile.is_open()){
    CPUList::iterator i;
    GraphTransactionQueue aQueue;
    TMLTransaction* aTrans, *aTopElement;
    unsigned int aTransitionNo=0;
    //for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      aTrans = (*i)->getTransactions1By1(true);
      if (aTrans!=0) aQueue.push(aTrans);
    }
    std::ostringstream aOutp;
    while (!aQueue.empty()){
      //std::ostringstream aTempStr;
      CPU* aCPU;
      aTopElement = aQueue.top();
      aCPU = aTopElement->getCommand()->getTask()->getCPU();
      for (TMLLength a=0; a < aTopElement->getVirtualLength(); a++){
        aOutp << "(" << aTransitionNo << ",\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
        if (aTopElement->getChannel()!=0){
          aOutp << "__" << aTopElement->getChannel()->toShortString();
          //if (dynamic_cast<TMLEventChannel*>(aTopElement->getChannel())==0) aOutp << "<" << aTopElement->getVirtualLength() << ", " << ">";
        }
        aOutp << ")\"," << ++aTransitionNo << ")\n";

        //aOutp << aTempStr.str() << ++aTransitionNo << ")\n";
      }
      //myfile << aTempStr.str();
      aQueue.pop();
      aTrans = aCPU->getTransactions1By1(false);
      if (aTrans!=0) aQueue.push(aTrans);
    }
    myfile << "des (0, " << aTransitionNo+1 << ", " << aTransitionNo+2 << ")\n";
    myfile <<  aOutp.str() << "(" << aTransitionNo << ",\"i(exit)\", " << aTransitionNo+1 << ")\n";
    myfile.close();
  }
  else
    std::cout << "Unable to open Graph output file" << std::endl;
  gettimeofday(&aEnd,NULL);
  std::cout << "The Graph output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << iTraceFileName << std::endl;
}

void Simulator::schedule2TXT(std::string& iTraceFileName) const{
  struct timeval aBegin,aEnd;
  gettimeofday(&aBegin,NULL);

  if ( !ends_with( iTraceFileName, EXT_TXT ) ) {
	  iTraceFileName.append( EXT_TXT );
  }

  std::ofstream myfile(iTraceFileName.c_str());
  if (myfile.is_open()){
    //for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      (*i)->schedule2TXT(myfile);
    }
    //for(BusList::const_iterator j=_simComp->getBusIterator(false); j != _simComp->getBusIterator(true); ++j){
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->schedule2TXT(myfile);
    }
    myfile.close();
  }
  else {
    std::cout << "Unable to open text output file." << std::endl;
  }

  gettimeofday(&aEnd,NULL);
  std::cout << "The text output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << iTraceFileName << std::endl;
}

int Simulator::allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const{
  int total = 0;
  //glob << TAG_TRANSo << "Transaction" << TAG_TRANSc << std::endl;
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    total += (*i)->allTrans2XML(glob, maxNbOfTrans);
  }

  for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
    total += (*j)->allTrans2XML(glob, maxNbOfTrans);
  }

  return total;
}

void Simulator::latencies2XML(std::ostringstream& glob, int id1, int id2) {
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    (*i)->latencies2XML(glob, id1, id2);
  }

  for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
    (*j)->latencies2XML(glob, id1,id2);
  }
}

void Simulator::schedule2HTML(std::string& iTraceFileName) const {
  struct timeval aBegin,aEnd;
  gettimeofday(&aBegin,NULL);

  if ( !ends_with( iTraceFileName, EXT_HTML ) ) {
	  iTraceFileName.append( EXT_HTML );
  }

  std::ofstream myfile(iTraceFileName.c_str());

  if (myfile.is_open()) {
  	// DB: Issue #4
    myfile << SCHED_HTML_DOC; // <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n
    myfile << SCHED_HTML_BEG_HTML; // <html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n
    myfile << SCHED_HTML_BEG_HEAD; // <head>\n

    const std::string::size_type findSlash = iTraceFileName.find_last_of( "/" );
    unsigned int indexSlash;

    if ( findSlash == std::string::npos ) {
    	indexSlash = 0;
    }
    else {
    	indexSlash = findSlash;
    }

    const std::string ext( EXT_HTML );
    const std::string cssFileName = iTraceFileName.substr( indexSlash + 1, iTraceFileName.length() - indexSlash - ext.length() - 1 ) + EXT_CSS;

    const std::string cssFullFileName = iTraceFileName.substr( 0, indexSlash + 1 ) + cssFileName;
    std::ofstream cssfile( cssFullFileName.c_str() );

    if ( cssfile.is_open() ) {
    	cssfile << SCHED_HTML_CSS_CONTENT;
        cssfile.close();

        myfile << SCHED_HTML_CSS_BEG_LINK;
        myfile << cssFileName;
        myfile << SCHED_HTML_CSS_END_LINK;
    }
    else {
        myfile << SCHED_HTML_BEG_STYLE; // <style>\n";
        myfile << SCHED_HTML_CSS_CONTENT;
        myfile << SCHED_HTML_END_STYLE; // <style>\n";
    }

    myfile << SCHED_HTML_META; // <meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\" />\n
    myfile << SCHED_HTML_BEG_TITLE; // <title>
    myfile << "Scheduling";
    myfile << SCHED_HTML_END_TITLE; // </title>\n
    myfile << SCHED_HTML_END_HEAD; // </head>\n
    myfile << SCHED_HTML_BEG_BODY; // <body>\n

    //for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      (*i)->schedule2HTML(myfile);
    }
    //for(BusList::const_iterator j=_simComp->getBusIterator(false); j != _simComp->getBusIterator(true); ++j){
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->schedule2HTML(myfile);
    }
    //for_each(iCPUlist.begin(), iCPUlist.end(),std::bind2nd(std::mem_fun(&CPU::schedule2HTML),myfile));

    myfile << SCHED_HTML_END_BODY; // </body>\n
    myfile << SCHED_HTML_END_HTML; // </html>\n

    myfile.close();
  }
  else {
    std::cout << "Unable to open HTML output file." << std::endl;
  }

  gettimeofday(&aEnd,NULL);
  std::cout << "The HTML output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << iTraceFileName << std::endl;
}

void Simulator::schedule2VCD(std::string& iTraceFileName) const{
  time_t aRawtime;
  struct tm * aTimeinfo;
  struct timeval aBegin,aEnd;
  gettimeofday(&aBegin,NULL);
  time(&aRawtime);
  aTimeinfo=localtime(&aRawtime);

  if ( !ends_with( iTraceFileName, EXT_VCD ) ) {
	  iTraceFileName.append( EXT_VCD );
  }

  std::ofstream myfile(iTraceFileName.c_str());

  if (myfile.is_open()){
    //std::cout << "File is open" << std::endl;
    SignalChangeQueue aQueue;
    std::string aSigString;
    //bool aNoMoreTrans;
    //TraceableDevice* actDevice;
    TMLTime aCurrTime=-1;
    SignalChangeData* aTopElement;
    TMLTime aNextClockEvent=0;
    myfile << "$date\n" << asctime(aTimeinfo) << "$end\n\n$version\nDaniel's TML simulator\n$end\n\n";
    myfile << "$timescale\n5 ns\n$end\n\n$scope module Simulation $end\n";
    //std::cout << "Before 1st loop" << std::endl;
    //for (TraceableDeviceList::const_iterator i=_simComp->getVCDIterator(false); i!= _simComp->getVCDIterator(true); ++i){
    for (TraceableDeviceList::const_iterator i=_simComp->getVCDList().begin(); i!= _simComp->getVCDList().end(); ++i){
      //TraceableDevice* a=*i;
      //                        a->streamBenchmarks(std::cout);
      //                        a->toString();
      //std::cout << "in 1st loop " << a << std::endl;
      //std::cout << "device: " << (*i)->toString() << std::endl;
      //myfile << "$var integer 3 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
      myfile << "$var wire 1 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
      //std::cout << "get next signal change" << std::endl;
      //aTime = (*i)->getNextSignalChange(true, aSigString, aNoMoreTrans);
      aTopElement = new SignalChangeData();
      (*i)->getNextSignalChange(true, aTopElement);
      aQueue.push(aTopElement);
      //std::cout << "push" << std::endl;
      //aQueue.push(new SignalChangeData(aSigString, aTime, (aNoMoreTrans)?0:(*i)));
    }
    myfile << "$var integer 32 clk Clock $end\n";
    myfile << "$upscope $end\n$enddefinitions  $end\n\n";
    //std::cout << "Before 2nd loop" << std::endl;
    while (!aQueue.empty()){
      aTopElement=aQueue.top();
      while (aNextClockEvent < aTopElement->_time){
        myfile << "#" << aNextClockEvent << "\nr" << aNextClockEvent << " clk\n";
        aNextClockEvent+=CLOCK_INC;
      }
      if (aCurrTime!=aTopElement->_time){
        aCurrTime=aTopElement->_time;
        myfile << "#" << aCurrTime << "\n";
      }
      if (aNextClockEvent == aTopElement->_time){
        myfile << "b" << vcdTimeConvert(aNextClockEvent) << " clk\n";
        aNextClockEvent+=CLOCK_INC;
      }
      //myfile << aTopElement->_sigChange << "\n";
      myfile << vcdValConvert(aTopElement->_sigChange) << aTopElement->_device->toShortString() << "\n";
      aQueue.pop();
      TMLTime aTime = aTopElement->_time;
      aTopElement->_device->getNextSignalChange(false, aTopElement);
      if (aTopElement->_time == aTime)
        delete aTopElement;
      else
        aQueue.push(aTopElement);
      //actDevice=aTopElement->_device;
      //if (actDevice!=0) aTime = actDevice->getNextSignalChange(false, aSigString, aNoMoreTrans);
      //delete aTopElement;
      //aQueue.pop();
      //if (actDevice!=0) aQueue.push(new SignalChangeData(aSigString, aTime, (aNoMoreTrans)?0:actDevice));
    }
    myfile << "#" << aCurrTime+1 << "\n";
    std::cout << "Simulated cycles: " << aCurrTime << std::endl;
    //for (TraceableDeviceList::const_iterator i=_simComp->getVCDIterator(false); i!= _simComp->getVCDIterator(true); ++i){
    for (TraceableDeviceList::const_iterator i=_simComp->getVCDList().begin(); i!= _simComp->getVCDList().end(); ++i){
      //myfile << VCD_PREFIX << "100 " << (*i)->toShortString() << "\n";
      myfile << "0" << (*i)->toShortString() << "\n";
      //std::cout << "Utilization of component " << (*i)->toString() << ": " << ((float)(*i)->getBusyCycles()) / ((float)aCurrTime) << std::endl;
    }

    myfile.close();
  }
  else
    std::cout << "Unable to open VCD output file." << std::endl;
  gettimeofday(&aEnd,NULL);
  std::cout << "The VCD output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << iTraceFileName << std::endl;

}

bool Simulator::channelImpactsCommand(TMLChannel* iCh, TMLCommand* iCmd){
  unsigned int nbOfChannels = iCmd->getNbOfChannels();
  for (unsigned int i=0; i<nbOfChannels; i++)
    if (iCh==iCmd->getChannel(i)) return true;
  return false;
}

bool Simulator::simulate(TMLTransaction*& oLastTrans){
  TMLTransaction* depTransaction,*depCPUnextTrans,*transLET;
  TMLCommand* commandLET,*depCommand,*depCPUnextCommand;
  TMLTask* depTask;
  SchedulableDevice* cpuLET;
  CPU* depCPU;
#ifdef DEBUG_KERNEL
  std::cout << "kernel:simulate: first schedule" << std::endl;
#endif
  _simComp->setStopFlag(false,"");
  //std::cout << "before loop " << std::endl;
  //for(TaskList::const_iterator i=_simComp->getTaskIterator(false); i!=_simComp->getTaskIterator(true);i++){
  for(TaskList::const_iterator i=_simComp->getTaskList().begin(); i!=_simComp->getTaskList().end();i++){
    //std::cout << "loop it " << (*i)->toString() << std::endl;
    if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare(true);
    //std::cout << "loop it end" << (*i)->toString() << std::endl;
  }
  //std::cout << "after loop1" << std::endl;
#ifdef EBRDD_ENABLED
  for(EBRDDList::const_iterator i=_simComp->getEBRDDIterator(false); i!=_simComp->getEBRDDIterator(true);i++){
    if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare();
  }
#endif
  //std::cout << "after loop2" << std::endl;
  //for_each(_simComp->getCPUIterator(false), _simComp->getCPUIterator(true),std::mem_fun(&CPU::setRescheduleFlag));
  //for_each(_simComp->getCPUIterator(false), _simComp->getCPUIterator(true),std::mem_fun(&CPU::schedule));
  for_each(_simComp->getCPUList().begin(), _simComp->getCPUList().end(),std::mem_fun(&CPU::schedule));
  //std::cout << "after schedule" << std::endl;
  transLET=getTransLowestEndTime(cpuLET);
  //std::cout << "after getTLET" << std::endl;
#ifdef LISTENERS_ENABLED
  if (_wasReset) NOTIFY_SIM_STARTED();
  _wasReset=false;
#endif
  while (transLET!=0 && !_simComp->getStopFlag()){
#ifdef DEBUG_KERNEL
    std::cout << "kernel:simulate: scheduling decision: " <<  transLET->toString() << std::endl;
#endif
    commandLET=transLET->getCommand();
    //if (depTask!=NULL){
    //  transLET->setTaskID(commandLET->getID());
    //}
#ifdef DEBUG_KERNEL
    std::cout << "kernel:simulate: add trans " << commandLET->toString() << std::endl;
#endif
    if (cpuLET->addTransaction(0)){
      unsigned int nbOfChannels = commandLET->getNbOfChannels();
      //bool aRescheduleCoresFlag=false;
      for (unsigned int i=0;i<nbOfChannels; i++){
        if ((depTask=commandLET->getDependentTask(i))==0) continue;
        //if (depTask!=0){
#ifdef DEBUG_KERNEL
        std::cout << "kernel:simulate: dependent Task found" << std::endl;
#endif
        depCPU=depTask->getCPU();
        //std::cout << "CPU this task : " << cpuLET->toString();
        //if (depCPU==0) std::cout << "  CPU dep task " << depTask->toString() << ": 0\n"; else std::cout << "  CPU dep task: "<< depTask->toString() << " " << depCPU->toString() << std::endl;
        if (depCPU!=cpuLET){
#ifdef DEBUG_KERNEL
          std::cout << "kernel:simulate: Tasks running on different CPUs" << std::endl;
#endif
          depCommand=depTask->getCurrCommand();
          //if (depCommand!=0 && (dynamic_cast<TMLSelectCommand*>(depCommand)!=0 || channelImpactsCommand(commandLET->getChannel(i), depCommand))){
          if (depCommand!=0 && channelImpactsCommand(commandLET->getChannel(i), depCommand)) { //RIGHT one

#ifdef DEBUG_KERNEL
            std::cout << "kernel:simulate: commands are accessing the same channel" << std::endl;
#endif
            depTransaction=depCommand->getCurrTransaction();
            if (depTransaction!=0 && depTransaction->getVirtualLength()!=0){
#ifdef DEBUG_KERNEL
              std::cout << "kernel:simulate: dependent task has a current transaction and is not blocked any more" << std::endl;
#endif
              /* if (depCPU==0){
                 aRescheduleCoresFlag=true;
                 //#ifdef DEBUG_KERNEL
                 std::cout << "Multi Core scheduling procedure\n";
                 //#endif
                 depTask->setRescheduleFlagForCores();
                 continue;
                 }*/
              //std::cout << "Let's crash!!!!!!!!\n";
              depCPUnextTrans=depCPU->getNextTransaction();
              //std::cout << "Not crahed!!!!!!!!\n";
              if (depCPUnextTrans!=0){
#ifdef DEBUG_KERNEL
                std::cout << "kernel:simulate: transaction scheduled on dependent CPU" << std::endl;
#endif
                depCPUnextCommand=depCPUnextTrans->getCommand();
                if (depCPUnextCommand->getTask()!=depTask){
#ifdef DEBUG_KERNEL
                  std::cout << "kernel:simulate: dependent task not yet scheduled on dependent CPU" << std::endl;
#endif

                  depCPU->truncateAndAddNextTransAt(transLET->getEndTime());
#ifdef DEBUG_KERNEL
                  std::cout << "kernel:simulate: dependent transaction truncated" << std::endl;
#endif
                }
              }else{
#ifdef DEBUG_KERNEL
                std::cout << "kernel:simulate: schedule dependent CPU  " << depCPU->toString() << std::endl;
#endif
                depCPU->schedule();
              }
            }
          }
        }
      }
#ifdef DEBUG_KERNEL
      std::cout << "kernel:simulate: invoke schedule on executing CPU" << std::endl;
#endif
      /*if (aRescheduleCoresFlag){
        for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
        if (*i!=cpuLET) (*i)->truncateIfNecessary(transLET->getEndTime());
        }
        for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
        if (*i!=cpuLET) (*i)->rescheduleIfNecessary();
        }
        }*/
      cpuLET->schedule();
#ifdef LISTENERS_ENABLED
      NOTIFY_TIME_ADVANCES(transLET->getEndTime());
#endif
    }
    oLastTrans=transLET;

    //std::cout << "kernel:simulate: getTransLowestEndTime" << std::endl;
    transLET=getTransLowestEndTime(cpuLET);

    //_syncInfo->_server->sendReply("Sleep once again\n");
    //sleep(1);
  }

  bool aSimCompleted = (transLET==0 && !_simComp->getStoppedOnAction());

  if (aSimCompleted){
#ifdef LISTENERS_ENABLED
    NOTIFY_SIM_STOPPED();
    NOTIFY_EVALUATE();
#endif
    _longRunTime = max(_longRunTime, SchedulableDevice::getSimulatedTime());
    _shortRunTime = min(_shortRunTime, SchedulableDevice::getSimulatedTime());
    //_simComp->showTaskStates();
  }
  return (aSimCompleted);
}

const std::string Simulator::getArgs(const std::string& iComp, const std::string& iDefault, int iLen, char** iArgs){
  int aPosition=0;
  while (aPosition < iLen){
    if (iComp.compare(iArgs[aPosition])==0){
      if (aPosition+1 < iLen && iArgs[aPosition+1][0]!='-'){
        return std::string(iArgs[aPosition+1]);
      }else
        return iDefault;
    }
    aPosition++;
  }
  return std::string("");
}

void Simulator::printHelp(){
  std::cout <<  "\n************************** Command line arguments *************************\n"
    "-gpath                 specify path for graph output\n"
    "-server                launch simulator in server mode\n"
    "-file                  read simulation commands from file\n"
    "-help                  display this help text\n"
    "-ohtml ofile           simulate and write traces to ofile in html format\n"
    "-otxt ofile            simulate and write traces to ofile in text format\n"
    "-ovcd ofile            simulate and write traces to ofile in vcd format\n"
    "-ograph ofile          simulate and write traces to ofile in aut format\n"
    "-gname ofile           name of the file WITHOUT extension storing the reachability graph\n"
    "-explo                 generate the reachability graph                 \n"
    "-cmd \'c1 p1 p2;c2\'     execute commands c1 with parameters p1 and p2 and c2\n"
    "-oxml ofile            xml reply is written to ofile, in case the -cmd option is used\n"
    "***************************************************************************\n\n";
}

void Simulator::run(){
  std::string* aNewCmd;
  std::cout << "Running in server mode.\n";
  while (!_syncInfo->_terminate){
    //pthread_mutex_lock (&_syncInfo->_mutexConsume);
    //std::cout << "Simulator Waiting for cmd\n";
    aNewCmd=_syncInfo->popCommand();
    //decodeCommand(_syncInfo->_command);
    //std::cout << "Let's crash.\n";
    decodeCommand(*aNewCmd);
    //std::cout << "Returned from decode.\n";
    //std::cout << "Before delete.\n";
    delete aNewCmd;
    //pthread_mutex_unlock (&_syncInfo->_mutexProduce);
  }
  std::cout << "Simulator loop terminated." << std::endl;
}

ServerIF* Simulator::run(int iLen, char ** iArgs){
  std::string aArgString;
  std::string graphName = "";
  std::cout << "Starting up...\n";
   graphName = getArgs("-gname", "", iLen, iArgs);
   if (graphName.empty()) {
     graphName = "graph";
   }
  _graphOutPath = getArgs("-gpath", "", iLen, iArgs);
  if (_graphOutPath.length()>0 && _graphOutPath[_graphOutPath.length()-1]!='/')
    _graphOutPath+="/";
  aArgString =getArgs("-server", "server", iLen, iArgs);
  if (!aArgString.empty()) return new Server();
  aArgString =getArgs("-file", "file", iLen, iArgs);
  if (!aArgString.empty()) return new ServerLocal(aArgString);
  aArgString =getArgs("-explo", "file", iLen, iArgs);
  std::cout << "Just analyzed explo 1->" + aArgString + "<-\n";
  if (!aArgString.empty()) {
    std::string command = "1 7 100 100 " + graphName;
    std::cout << "Just analyzed explo 1->" + aArgString + "<- with command: " + command + "\n";
    decodeCommand(command);
  }
  std::cout << "Just analyzed explo 2\n";
  //if (!aArgString.empty()) return new ServerExplore();
  std::cout << "Running in command line mode.\n";
  _replyToServer = false;
  aArgString =getArgs("-help", "help", iLen, iArgs);
  if (aArgString.empty()){
    //aArgString =getArgs("-explo", "explo", iLen, iArgs);
    aArgString =getArgs("-cmd", "1 0", iLen, iArgs);
    if (aArgString.empty()){
      TMLTransaction* oLastTrans;
      simulate(oLastTrans);
      aArgString=getArgs("-ohtml", "scheduling.html", iLen, iArgs);
      if (!aArgString.empty()) schedule2HTML(aArgString);
      aArgString=getArgs("-otxt", "scheduling.txt", iLen, iArgs);
      if (!aArgString.empty()) schedule2TXT(aArgString);
      aArgString=getArgs("-ovcd", "scheduling.vcd", iLen, iArgs);
      if (!aArgString.empty()) schedule2VCD(aArgString);
      aArgString=getArgs("-ograph", "scheduling.aut", iLen, iArgs);
      if (!aArgString.empty()) schedule2Graph(aArgString);
      _simComp->streamBenchmarks(std::cout);
      std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
    }else{
      std::ofstream aXmlOutFile;
      std::string aXmlFileName = getArgs("-oxml", "reply.xml", iLen, iArgs);
      if (aXmlFileName.empty()) aXmlOutFile.open("/dev/null"); else aXmlOutFile.open(aXmlFileName.c_str());
      if (aXmlOutFile.is_open()){
        std::string aNextCmd;
        std::istringstream iss(aArgString+";");
        getline(iss, aNextCmd, ';');
        while (!(iss.eof() || aNextCmd.empty())){
          std::cout << "next cmd to execute: \"" << aNextCmd << "\"\n";
          decodeCommand(aNextCmd, aXmlOutFile);
          getline(iss, aNextCmd, ';');
        }
        aXmlOutFile.close();
      }else
        std::cout << "XML output file could not be opened, aborting.\n";
    }
    rusage res;
    getrusage(RUSAGE_SELF, &res);
    //std::cerr << res.ru_utime.tv_sec << "," << res.ru_utime.tv_usec << "," << res.ru_stime.tv_sec << "," << res.ru_stime.tv_usec << "\n";
    double aRunTime = ((double)((res.ru_utime.tv_sec + res.ru_stime.tv_sec) *1000000 + res.ru_utime.tv_usec + res.ru_stime.tv_usec))/1000000;
    std::cerr << "trans/sec: " << ((double)SchedulableDevice::getOverallTransNo())/aRunTime << "\n";
    std::cerr << "cycles/trans: " << ((double)SchedulableDevice::getOverallTransSize())/((double)SchedulableDevice::getOverallTransNo()) << "\n";
    std::cerr << "Trans size: " << SchedulableDevice::getOverallTransSize() << "  trans no: " << SchedulableDevice::getOverallTransNo() << "\n";
    std::cerr << "Statement coverage of application: " << TMLCommand::getCmdCoverage() << "%\n";
    std::cerr << "Branch coverage of application: " << TMLCommand::getBranchCoverage() << "%\n";
  }else{
    printHelp();
  }
  //clock_t tick =sysconf(_SC_CLK_TCK);
  //tms test;
  //times(&test);
  //std::cout << "user time: " << test.tms_utime << "  system time: " << test.tms_stime + test.tms_cstime << "  tick: " << tick << "\n";
  return 0;
}

void Simulator::decodeCommand(std::string iCmd, std::ostream& iXmlOutStream){
  //std::cout << "Not crashed. I: " << iCmd << std::endl;
  //std::cout << iCmd << std::endl;
  unsigned int aCmd, aParam1, aParam2, anErrorCode=0;
  //std::string anIssuedCmd(iCmd);
  std::istringstream aInpStream(iCmd);
  //std::cout << "Not crashed. II\n";
  std::ostringstream aGlobMsg, anEntityMsg, anAckMsg;
  std::string aStrParam;
  int returnedNbOfTransactions = 0;
  //bool aSimTerminated=false;
  //std::cout << "Not crashed. III\n";
  //std::cout << "Not crashed. I: " << iCmd << std::endl;
  _simComp->setStopFlag(false,"");
  //anEntityMsg.str("");
  aGlobMsg << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl /*<< TAG_REPLYo << anIssuedCmd << TAG_REPLYc << std::endl*/;
  aInpStream >> aCmd;
  //std::cout << "Not crashed. I: " << iCmd << std::endl;
  //std::cout << "Decoding command: d" << iCmd << " " << aCmd<<std::endl;
  TMLTransaction* oLastTrans;
  switch (aCmd){
  case 0: //Quit simulation
    std::cout << "QUIT SIMULATION from Decode Command"  << std::endl;
    break;
  case 1:{
    struct timeval aBegin,aEnd;
    gettimeofday(&aBegin,NULL);
    _busy=true;
    //std::cout << "Not crashed. I: " << iCmd << std::endl;
    anAckMsg << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << /*TAG_REPLYo << anIssuedCmd << TAG_REPLYc << std::endl<< */ TAG_MSGo << "Command received" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_BUSY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
    if (_replyToServer) {
      if (_syncInfo != NULL)
        if (_syncInfo->_server != NULL)
          _syncInfo->_server->sendReply(anAckMsg.str());
    }
    aInpStream >> aParam1;
    std::cout << "Not crashed. I: " << iCmd << " param= " << aParam1 << std::endl;

    switch (aParam1){
      _end =oLastTrans->printEnd();
    case 0:     //Run to next breakpoint
      std::cout << "Run to next breakpoint." << std::endl;
      aGlobMsg << TAG_MSGo << "Run to next breakpoint" << TAG_MSGc << std::endl;
      _simTerm=runToNextBreakpoint(oLastTrans);
      std::cout << "End Run to next breakpoint." << std::endl;
      _end =oLastTrans->printEnd();
      break;
    case 1:     //Run up to trans x
      std::cout << "Run to transaction x." << std::endl;
      aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
      anErrorCode=1;
      std::cout << "End Run to transaction x." << std::endl;
      break;
    case 2:     //Run x transactions
      std::cout << "Run x transactions." << std::endl;
      aInpStream >> aParam2;
      //_currCmdListener=new RunXTransactions(_simComp,aParam2);
      aGlobMsg << TAG_MSGo << "Created listener run " << aParam2 << " transactions" << TAG_MSGc << std::endl;
      _simTerm=runXTransactions(aParam2, oLastTrans);
      std::cout << "Run x transactions." << std::endl;
      break;
    case 3:     //Run up to command x
      std::cout << "Run to command x." << std::endl;
      aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
      anErrorCode=1;
      std::cout << "End Run to command x." << std::endl;
      break;
    case 4:     //Run x commands
      std::cout << "Run x commands." << std::endl;
      aInpStream >> aParam2;
      //_currCmdListener=new RunXCommands(_simComp,aParam2);
      aGlobMsg << TAG_MSGo << "Created listener run " << aParam2 << " commands" << TAG_MSGc << std::endl;
      _simTerm=runXCommands(aParam2, oLastTrans);
      std::cout << "End Run x commands." << std::endl;
      break;
    case 5: //Run up to time x
      std::cout << "Run to time x." << std::endl;
      aInpStream >> aParam2;
      //_currCmdListener=new RunXTimeUnits(_simComp,aParam2);
      aGlobMsg << TAG_MSGo << "Created listener run to time " << aParam2 << TAG_MSGc << std::endl;
      _simTerm=runTillTimeX(aParam2, oLastTrans);
      std::cout << "End Run to time x." << std::endl;
      break;
    case 6:     //Run for x time units
      std::cout << "Run for x time units." << std::endl;
      aInpStream >> aParam2;
      //_currCmdListener=new RunXTimeUnits(_simComp,aParam2+SchedulableDevice::getSimulatedTime());
      aGlobMsg << TAG_MSGo  << "Created listener run " << aParam2 << " time units" << TAG_MSGc << std::endl;
      _simTerm=runXTimeUnits(aParam2, oLastTrans);
      std::cout << "End Run for x time units." << std::endl;
      break;
    case 7: {//Explore Tree
      //for (int i=0; i<RECUR_DEPTH; i++) leafsForLevel[i]=0;
      std::cout << "Explore tree." << std::endl;
      _commandCoverage=100; _branchCoverage=100;
      aInpStream >> _commandCoverage;
      aInpStream >> _branchCoverage;
      aInpStream >> aStrParam;
      std::stringstream aPathAutFile;
      aPathAutFile << _graphOutPath << aStrParam << ".aut.tmp";
      std::ofstream myAUTfile(aPathAutFile.str().c_str());
      //aPath.str("");
      //std::ofstream myfile2 ("tree.txt");
      //if (myDOTfile.is_open() && myAUTfile.is_open()){
      if (myAUTfile.is_open()){
        //#ifdef DOT_GRAPH_ENABLED
        //myDOTfile << "digraph BCG {\nsize = \"7, 10.5\";\ncenter = TRUE;\nnode [shape = circle];\n0 [peripheries = 2];\n";
        //#endif
        unsigned int aTransCounter=0;
        _terminateExplore=false;
        _nbOfBranchesToExplore = 1;
        _nbOfBranchesExplored = 0;
        exploreTree(0, 0, myAUTfile, aTransCounter);
        //#ifdef DOT_GRAPH_ENABLED
        //myDOTfile << "}\n";
        //system ("mv tree tree.dot");
        //myDOTfile.close();
        //#else
        myAUTfile.close();
//        aPath.str("");
        std::stringstream aPathTreeFile;
        aPathTreeFile <<  _graphOutPath << "header";
        std::ofstream myTMPfile( aPathTreeFile.str().c_str() );

        if (myTMPfile.is_open()) {
          //des (0, 29, 27)
          myTMPfile << "des(0," << aTransCounter << "," << TMLTransaction::getID() << ")\n";
          myTMPfile.close();
          //system ("cat header tree.aut.tmp > tree.aut");
          //system ("rm header tree.aut.tmp");
          std::stringstream treeFileContent;
          treeFileContent << "cat " << _graphOutPath << "header " << _graphOutPath << aStrParam << ".aut.tmp > " << _graphOutPath << aStrParam << ".aut";
          system( treeFileContent.str().c_str() );
          treeFileContent.str("");
          treeFileContent << "rm " <<  _graphOutPath << "header " << _graphOutPath << aStrParam << ".aut.tmp";
          system( treeFileContent.str().c_str() );

          aGlobMsg << TAG_MSGo  << "Tree was explored" << TAG_MSGc << std::endl;
        }

        // Issue #56: Output error message when could not open the file
        else {
            aGlobMsg << TAG_MSGo << MSG_FILEERR << aPathTreeFile.str() << TAG_MSGc << std::endl;
            anErrorCode = 4;
        }
      }

      // Issue #56: Output error message when could not open the file
      else {
          aGlobMsg << TAG_MSGo << MSG_FILEERR << aPathAutFile.str() << TAG_MSGc << std::endl;
          anErrorCode = 4;
      }

      _simTerm=true;
      //aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
      //anErrorCode=1;
      std::cout << "** Longest runtime: " << _longRunTime << ", shortest runtime: " << _shortRunTime << " **\n";
      std::cout << "End Explore tree." << std::endl;
      break;
    }
    case 8:{//Run to next transfer on bus x
      std::cout << "Run to next transfer on bus x." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getBusByName(aStrParam));
      SchedulableCommDevice* aBus=_simComp->getBusByName(aStrParam);
      if (aBus!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on Bus " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToBusTrans(aBus, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run to next transfer on bus x." << std::endl;
      break;
    }
    case 9:{//Run until CPU x executes
      std::cout << "Run until CPU x executes." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getCPUByName(aStrParam));
      SchedulableDevice* aCPU=_simComp->getCPUByName(aStrParam);
      if (aCPU!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on CPU " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToCPUTrans(aCPU, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until CPU x executes." << std::endl;
      break;
    }
    case 10:{//Run until Task x executes
      std::cout << "Run until Task x executes." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getTaskByName(aStrParam));
      TMLTask* aTask=_simComp->getTaskByName(aStrParam);
      if (aTask!=0){
        aGlobMsg << TAG_MSGo << "Created listener on Task " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToTaskTrans(aTask, oLastTrans);
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);

      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until Task x executes." << std::endl;
      break;
    }
    case 11:{//Run until Mem x is accessed
      std::cout << "Run until Mem x is accessed." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getSlaveByName(aStrParam));
      Slave* aSlave=_simComp->getSlaveByName(aStrParam);
      if (aSlave!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on Slave " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToSlaveTrans(aSlave, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until Mem x is accessed." << std::endl;
      break;
    }
    case 12:{//Run until operation on channel x is performed
      std::cout << "Run until operation on channel x is performed." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getChannelByName(aStrParam));
      TMLChannel* aChannel=_simComp->getChannelByName(aStrParam);
      if (aChannel!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on Channel " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToChannelTrans(aChannel, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until operation on channel x is performed." << std::endl;
      break;
    }
    case 13:{//Run to next random choice command
      std::cout << "Run to next random command." << std::endl;
      _simTerm=runToNextRandomCommand(oLastTrans);
      std::cout << "End Run to next random choice command." << std::endl;
      break;
    }
    case 14:{//Run until condition is satisfied
      std::cout << "Run until condition is satisfied." << std::endl;
      aInpStream >> aStrParam;
      TMLTask* aTask=_simComp->getTaskByName(aStrParam);
      if (aTask!=0){
        bool aSuccess, aTerminated;
        aInpStream >> aStrParam;
        aTerminated = runUntilCondition(aStrParam, aTask, oLastTrans, aSuccess);
        if (aSuccess){
          _simTerm=aTerminated;
          aGlobMsg << TAG_MSGo << "Created listeners for condition " << aStrParam << TAG_MSGc << std::endl;
        }else{
          aGlobMsg << TAG_MSGo << MSG_CONDERR << TAG_MSGc << std::endl;
          anErrorCode=5;
        }
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until condition is satisfied." << std::endl;
      break;
    }
    default:
      aGlobMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
      anErrorCode=3;
    }
    gettimeofday(&aEnd,NULL);
    _simDuration += getTimeDiff(aBegin,aEnd);
    aGlobMsg << TAG_SIMDURo <<  _simDuration << TAG_SIMDURc << std::endl;
    //std::cout << "Before sim\n";
    if (anErrorCode==0){
      //aGlobMsg << TAG_CURRTASKo << oLastTrans->getCommand()->getTask()->getID() << TAG_CURRTASKc;
      //simulate();
      //aGlobMsg <<
      std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
    }
    _busy=false;
    break;
  }
  case 2:       //reset
    std::cout << "Simulator reset." << std::endl;
    _wasReset=true;
    _simComp->reset();
    _simComp->resetStateHash();
    _simTerm=false;
    _simDuration=0;
    _longRunTime = 0;
    _shortRunTime = -1;
    aGlobMsg << TAG_MSGo << "Simulator reset" << TAG_MSGc << std::endl;
    std::cout << "End Simulator reset." << std::endl;
    break;
  case 3:{//Print variable x
    std::cout << "Print variable x." << std::endl;
    aInpStream >> aStrParam;
    if (aStrParam=="all"){
      //for(TaskList::const_iterator i=_simComp->getTaskIterator(false); i !=_simComp->getTaskIterator(true); ++i){
      for(TaskList::const_iterator i=_simComp->getTaskList().begin(); i !=_simComp->getTaskList().end(); ++i){
        printVariablesOfTask(*i, anEntityMsg);
      }
    }else{
      TMLTask* aTask = _simComp->getTaskByName(aStrParam);
      if (aTask==0){
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }else{
        //std::cout << "Task " << aStrParam << " exists" << std::endl;
        aInpStream >> aStrParam;
        if (aStrParam=="all"){
          printVariablesOfTask(aTask, anEntityMsg);
        }else{
          //std::cout << "Check if Var *" << aStrParam << "* exists" << std::endl;
          //std::cout << "Len: " << aStrParam.length() << std::endl;
          bool aIsId;
          ParamType* aParam=aTask->getVariableByName(aStrParam, aIsId);
          if (aParam!=0){
            aGlobMsg << TAG_MSGo << "Variable values" << TAG_MSGc << std::endl;
            anEntityMsg << TAG_TASKo << " id=\"" << aTask-> getID() << "\" name=\"" << aTask->toString() << "\">" << TAG_VARo;
            if (aIsId) anEntityMsg << " id=\""; else anEntityMsg << " name=\"";
            anEntityMsg << aStrParam << "\">" << *aParam << TAG_VARc << TAG_TASKc << std::endl;
          }else{
            aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
            anErrorCode=2;
          }
        }
      }
    }
    std::cout << "End Print variable x." << std::endl;
    break;
  }
  case 4:{//Print information about simulation element x
    //bool aFailure=false;
    //std::cout << "Print information about simulation element x." << std::endl;
    aInpStream >> aParam1;
    aInpStream >> aStrParam;
    //std::cout << "Print information about simulation element: " << aStrParam << std::endl;
    anErrorCode=0;
    switch (aParam1){
    case 0: {//CPU
      TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getCPUByName(aStrParam));
      if (aDevice!=0) {
        std::cout << "Print information about CPU: " << _simComp->getCPUByName(aStrParam) << std::endl;
        aDevice->streamStateXML(anEntityMsg);
      } else anErrorCode=2;
      break;
    }
    case 1: {//Bus
      TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getBusByName(aStrParam));
      if (aDevice!=0) {
        std::cout << "Print information about bus: " << _simComp->getBusByName(aStrParam) << std::endl;
        aDevice->streamStateXML(anEntityMsg);
      } else anErrorCode=2;
      break;
    }
    case 2: //Mem
    case 3: //Bridge
      anErrorCode=1;
      break;
    case 4:{ //Channel
      TMLChannel* aDevice = _simComp->getChannelByName(aStrParam);
      if (aDevice!=0){
        std::cout << "Print information about channel: " <<  _simComp->getChannelByName(aStrParam) << std::endl;
        //std::cout << "get Channel info" << std::endl;
        aDevice->streamStateXML(anEntityMsg);
      } else anErrorCode=2;
      break;
    }
    case 5: {//Task
      TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getTaskByName(aStrParam));
      if (aDevice!=0) {
        std::cout << "Print information about Task: " <<  _simComp->getTaskByName(aStrParam) << std::endl;
        aDevice->streamStateXML(anEntityMsg);
      } else anErrorCode=2;
      break;
    }
    default:anErrorCode=3;
    }
    switch(anErrorCode){
    case 0:
      aGlobMsg << TAG_MSGo << "Component information" << TAG_MSGc << std::endl;
      break;
    case 1:
      aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
      break;
    case 2:
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      break;
    default:
      aGlobMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
      break;
    }
    //std::cout << "End Print information about simulation element" << aStrParam << std::endl;
    break;
  }
  case 5:{//Set variable x to value y
    std::cout << "Set variable x to value y." << std::endl;
    aInpStream >> aStrParam;
    TMLTask* aTask = _simComp->getTaskByName(aStrParam);
    if (aTask==0){
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      anErrorCode=2;
    }else{
      aInpStream >> aStrParam;
      bool aIsId;
      ParamType* aParam=aTask->getVariableByName(aStrParam, aIsId);
      if (aParam!=0){
        aInpStream >> *aParam;
        aGlobMsg << TAG_MSGo << "Set variable " << aStrParam << " to " << *aParam << TAG_MSGc << std::endl;
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
    }
    std::cout << "End Set variable x to value y." << std::endl;
    break;
  }
  case 6:{ //Write x samples/events to channel y
    std::cout << "Write x samples/events to channel y." << std::endl;
    //aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
    //anErrorCode=1;
    aInpStream >> aStrParam;
    TMLChannel* aChannel = _simComp->getChannelByName(aStrParam);
    if (aChannel==0){
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      anErrorCode=2;
    }else{
      aInpStream >> aParam1;
      TMLEventChannel* anEventChannel = dynamic_cast<TMLEventChannel*>(aChannel);
      if (anEventChannel==0){
        //aChannel->insertSamples(aParam1, anInsertParam);
        aChannel->insertSamples(aParam1, 0);
      }else{
        //Parameter<ParamType> anInsertParam((dynamic_cast<TMLEventChannel*>(aChannel))->getParamNo());
        Parameter* anInsertParam = anEventChannel->buildParameter();
        aInpStream >> anInsertParam;
        //aChannel->insertSamples(aParam1, anInsertParam);
        aChannel->insertSamples(aParam1, anInsertParam);
      }
      aGlobMsg << TAG_MSGo << "Write data/event to channel." << TAG_MSGc << std::endl;
    }
    std::cout << "End Write x samples/events to channel y." << std::endl;
    break;
  }
  case 7: //Save trace in file x
    std::cout << "Save trace in file x." << std::endl;
    aInpStream >> aParam1;
    aInpStream >>aStrParam;
    switch (aParam1){
    case 0: //VCD
      aGlobMsg << TAG_MSGo << "Schedule output in VCD format" << TAG_MSGc << std::endl;
      schedule2VCD(aStrParam);
      break;
    case 1: //HTML
      aGlobMsg << TAG_MSGo << "Schedule output in HTML format" << TAG_MSGc << std::endl;
      schedule2HTML(aStrParam);
      break;
    case 2: //TXT
      aGlobMsg << TAG_MSGo << "Schedule output in TXT format" << TAG_MSGc << std::endl;
      schedule2TXT(aStrParam);
      break;
    default:
      aGlobMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
      anErrorCode=3;
    }
    std::cout << "End Save trace in file x." << std::endl;
    break;
  case 8:{ //Save simulation state in file x
    std::cout << "Save simulation state in file x." << std::endl;
    aInpStream >> aStrParam;
    std::ofstream aFile (aStrParam.c_str());
    if (aFile.is_open()){
      _simComp->writeObject(aFile);
      aGlobMsg << TAG_MSGo << "Simulation state saved in file " << aStrParam << TAG_MSGc << std::endl;
    }else{
      aGlobMsg << TAG_MSGo << MSG_FILEERR << aStrParam << TAG_MSGc << std::endl;
      anErrorCode=4;
    }
    std::cout << "End Save simulation state in file x." << std::endl;
    break;
  }
  case 9:{//Restore simulation state from file x
    std::cout << "Restore simulation state from file x." << std::endl;
    aInpStream >> aStrParam;
    std::ifstream aFile(aStrParam.c_str());
    if (aFile.is_open()){
      _simTerm=false;
      _simComp->reset();
      _simComp->readObject(aFile);
      aGlobMsg << TAG_MSGo << "Simulation state restored from file " << aStrParam << TAG_MSGc << std::endl;
    }else{
      aGlobMsg << TAG_MSGo << MSG_FILEERR << aStrParam << TAG_MSGc << std::endl;
      anErrorCode=4;
    }
    std::cout << "End Restore simulation state from file x." << std::endl;
    break;
  }
  case 10:{ //Save benchmarks in file x
    std::cout << "Save benchmarks in file x." << std::endl;
    aInpStream >> aParam1;

    std::cout<< "printhtis"<<std::endl;
    std::cout<< TAG_MSGo << oLastTrans->toString() << aStrParam << TAG_MSGc << std::endl;
    switch (aParam1){
    case 0: _simComp->streamBenchmarks(std::cout);
      aGlobMsg << TAG_MSGo << "Benchmarks written to screen " << TAG_MSGc << std::endl;
      break;
    case 1:{
      aInpStream >> aStrParam;
      std::ofstream aFile (aStrParam.c_str());
      if (aFile.is_open()){
        _simComp->streamBenchmarks(aFile);
        aGlobMsg << TAG_MSGo << "Benchmarks written to file " << aStrParam << TAG_MSGc << std::endl;
      }else{
        aGlobMsg << TAG_MSGo << MSG_FILEERR << aStrParam << TAG_MSGc << std::endl;
        anErrorCode=4;
      }
      break;
    }
    default:
      aGlobMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
      anErrorCode=3;
    }
    std::cout << "End Save benchmarks in file x." << std::endl;
    break;
  }
  case 11:{//Set breakpoint in task x, command y
    std::cout << "Set breakpoint in task x, command y." << std::endl;
    aInpStream >> aStrParam;
    TMLTask* aTask = _simComp->getTaskByName(aStrParam);
    if (aTask==0){
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      anErrorCode=2;
    }else{
      aInpStream >> aParam2;
      TMLCommand* aCommand=aTask->getCommandByID(aParam2);
      if (aCommand!=0){
        aCommand->setBreakpoint(new Breakpoint(_simComp));
        _breakpoints.insert(aCommand);
        aGlobMsg << TAG_MSGo << "Breakpoint was created" << TAG_MSGc << std::endl;
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
    }
    std::cout << "End Set breakpoint in task x, command y." << std::endl;
    break;
  }
  case 12:{//Choose branch
    std::cout << "Choose branch." << std::endl;
    aInpStream >> aStrParam;
    TMLTask* aTask = _simComp->getTaskByName(aStrParam);
    if (aTask==0){
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      anErrorCode=2;
    }else{
      aInpStream >> aParam1;
      //TMLChoiceCommand* aChoiceCmd=dynamic_cast<TMLChoiceCommand*>(aTask->getCommandByID(aParam1));
      IndeterminismSource* aRandomCmd=dynamic_cast<IndeterminismSource*>(aTask->getCommandByID(aParam1));
      if (aRandomCmd!=0){
        aInpStream >> aParam2;
        //aChoiceCmd->setPreferredBranch(aParam2);
        aRandomCmd->setRandomValue(aParam2);
        aGlobMsg << TAG_MSGo << "Preferred branch was set" << TAG_MSGc << std::endl;
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
    }
    std::cout << "End Choose branch." << std::endl;
    break;
  }
  case 16:{//Delete breakpoint in task x, command y
    std::cout << "Delete breakpoint in task x, command y." << std::endl;
    aInpStream >> aStrParam;
    TMLTask* aTask = _simComp->getTaskByName(aStrParam);
    if (aTask==0){
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      anErrorCode=2;
    }else{

      aInpStream >> aParam2;
      TMLCommand* aCommand=aTask->getCommandByID(aParam2);
      if (aCommand!=0){
        aCommand->removeBreakpoint();
        _breakpoints.erase(aCommand);
        aGlobMsg << TAG_MSGo << "Breakpoint was removed" << TAG_MSGc << std::endl;
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
    }
    std::cout << "End Delete breakpoint in task x, command y." << std::endl;
    break;
  }
  case 17:{//Get number of branches of current cmd
    std::cout << "Get number of branches of current cmd." << std::endl;
    IndeterminismSource* aRandomCmd =_simComp->getCurrentRandomCmd();
    if (aRandomCmd==0){
      aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
      anErrorCode=2;
    }else{
      unsigned int aNbNextCmds;
      aNbNextCmds = aRandomCmd->getRandomRange();
      TMLCommand* aCmd = dynamic_cast<TMLCommand*>(aRandomCmd);
      anEntityMsg << TAG_TASKo << " id=\"" << aCmd->getTask()-> getID() << "\" name=\"" << aCmd->getTask()->toString() << "\">" << TAG_CURRCMDo << " id=\"" << aCmd->getID() << "\">" << TAG_BRANCHo << aNbNextCmds << TAG_BRANCHc << "\">" << TAG_CURRCMDc << TAG_TASKc << std::endl;
      aGlobMsg << TAG_MSGo << "Current choice command" << TAG_MSGc << std::endl;
    }
    std::cout << "End Get number of branches of current cmd." << std::endl;
    break;
  }
  case 18:{//Get breakpoint list
    std::cout << "Get breakpoint list." << std::endl;
    for(BreakpointSet::iterator i=_breakpoints.begin(); i != _breakpoints.end(); ++i){
      anEntityMsg << TAG_TASKo << " id=\"" << (*i)->getTask()->getID() << "\" name=\"" << (*i)->getTask()->toString() << "\">" << TAG_BREAKCMDo << " id=\"" << (*i)->getID() << "\">" << TAG_BREAKCMDc << TAG_TASKc << std::endl;
    }
    aGlobMsg << TAG_MSGo << "Breakpoint List" << TAG_MSGc << std::endl;
    std::cout << "End Get breakpoint list." << std::endl;
    break;
  }
  case 19://Get Hash Value
    std::cout << "Get Hash Value." << std::endl;
    aGlobMsg << TAG_HASHo << _simComp->getHashValue() << TAG_HASHc << TAG_MSGo << "Hash Value Notification" << TAG_MSGc << std::endl;
    std::cout << "End Get Hash Value." << std::endl;
    break;
  case 20://Enable Breakpoints
    std::cout << "Enable Breakpoints." << std::endl;
    aInpStream >> aParam1;
    if (aParam1==0){
      aGlobMsg << TAG_MSGo << "Breakpoints are disabled." << TAG_MSGc << std::endl;
      Breakpoint::setEnabled(false);
    }else{
      aGlobMsg << TAG_MSGo << "Breakpoints are enabled." << TAG_MSGc << std::endl;
      Breakpoint::setEnabled(true);
    }
    std::cout << "End Enable Breakpoints." << std::endl;
    break;
  case 21://Get execution statistics of commands
    std::cout << "Get execution statistics of commands." << std::endl;
    TMLCommand::streamStateXML(aGlobMsg);
    std::cout << "End Get execution statistics of commands." << std::endl;
    break;
  case 22://Get list of transactions
    aInpStream >> aParam2;
    std::cout << "Get list of at most " << aParam2 << " transactions per CPU and Bus." << std::endl;
    //aGlobMsg << TAG_MSGo << "Breakpoints are disabled." << TAG_MSGc << std::endl;
    returnedNbOfTransactions = allTrans2XML(anEntityMsg, aParam2);
    anEntityMsg << TAG_TRANSACTION_NBo << "nb=\"" << returnedNbOfTransactions << "\"" << TAG_TRANSACTION_NBc <<  std::endl;
    std::cout << "End list of transactions." << std::endl;
    break;
  case 23:
    aInpStream >> aParam1;
    aInpStream >> aParam2;
    std::cout <<"Calculate latencies between " << aParam1 << " and " << aParam2 << std::endl;
    latencies2XML(anEntityMsg, aParam1, aParam2);
    std::cout << "latencies " << &anEntityMsg << std::endl;
    break;
  case 24:
	aInpStream >> aParam1;
    aInpStream >> aParam2;
 	std::cout <<"Calculate latencies between " << aParam1 << " and " << aParam2 << std::endl;
    addLatencyIds(aParam1, aParam2);
    std::cout << "latencies " << &anEntityMsg << std::endl;
  default:
    anEntityMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
    anErrorCode=3;

  }

  aGlobMsg << TAG_ERRNOo << anErrorCode << TAG_ERRNOc << std::endl;
  //if (aSimTerminated) aGlobMsg << SIM_TERM; else aGlobMsg << SIM_READY;
  writeSimState(aGlobMsg);
  aGlobMsg << std::endl << TAG_GLOBALc << std::endl << anEntityMsg.str() << TAG_STARTc << std::endl;

  if (_replyToServer) {
    if (_syncInfo->_server != NULL) {
      _syncInfo->_server->sendReply(aGlobMsg.str());
    } else {
      iXmlOutStream << aGlobMsg.str() << "\n";
    }
  }
  //std::cout << "End of command decode procedure." << std::endl;
  //std::cout << "Command: " << aCmd << "  Param1: " << aParam1 << "  Param2: " << aParam2 << std::endl;
}

void Simulator::printVariablesOfTask(TMLTask* iTask, std::ostream& ioMessage){
  //if (iTask->getVariableIteratorID(false)==iTask->getVariableIteratorID(true)) return;
  if (iTask->getVariableLookUpTableID().size()>0){
    ioMessage << TAG_TASKo << " id=\"" << iTask-> getID() << "\" name=\"" << iTask->toString() << "\">" << std::endl;
    //for(VariableLookUpTableID::const_iterator i=iTask->getVariableIteratorID(false); i !=iTask->getVariableIteratorID(true); ++i){
    for(VariableLookUpTableID::const_iterator i=iTask->getVariableLookUpTableID().begin(); i !=iTask->getVariableLookUpTableID().end(); ++i){
      ioMessage << TAG_VARo << " id=\"" << i->first << "\">" << *(i->second) << TAG_VARc << std::endl;
    }
    ioMessage << TAG_TASKc << std::endl;
  }
}

bool Simulator::runToNextBreakpoint(TMLTransaction*& oLastTrans){
  //TestListener myListener(_simComp);
  //_simComp->getTaskByName("DIPLODOCUSDesign__TMLTask_0")->registerListener(&myListener);
  //_simComp->getChannelByName("DIPLODOCUSDesign__evt")->registerListener(&myListener);
  //_simComp->getTaskByName("DIPLODOCUSDesign__TMLTask_0")->getCommandByID(17)->registerListener(&myListener);
  bool erg=simulate(oLastTrans);
  //return simulate(oLastTrans);
  //_simComp->getTaskByName("DIPLODOCUSDesign__TMLTask_0")->removeListener(&myListener);
  //_simComp->getChannelByName("DIPLODOCUSDesign__evt")->removeListener(&myListener);
  //_simComp->getTaskByName("DIPLODOCUSDesign__TMLTask_0")->getCommandByID(17)->removeListener(&myListener);
  return erg;
}

bool Simulator::runXTransactions(unsigned int iTrans, TMLTransaction*& oLastTrans){
  RunXTransactions aListener(_simComp, iTrans);
  return simulate(oLastTrans);
}

bool Simulator::runXCommands(unsigned int iCmds, TMLTransaction*& oLastTrans){
  RunXCommands aListener(_simComp,iCmds);
  bool test=simulate(oLastTrans);
  if (test) std::cout << "Simulate returned end" << std::endl;
  return test;
}

bool Simulator::runTillTimeX(TMLTime iTime, TMLTransaction*& oLastTrans){
  RunXTimeUnits aListener(_simComp,iTime);
  return simulate(oLastTrans);
}

bool Simulator::runXTimeUnits(TMLTime iTime, TMLTransaction*& oLastTrans){
  RunXTimeUnits aListener(_simComp,iTime+SchedulableDevice::getSimulatedTime());
  return simulate(oLastTrans);
}

bool Simulator::runToBusTrans(SchedulableCommDevice* iBus, TMLTransaction*& oLastTrans){
  //ListenerSubject <TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iBus);
  ListenerSubject <GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iBus);
  RunTillTransOnDevice aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToCPUTrans(SchedulableDevice* iCPU, TMLTransaction*& oLastTrans){
  //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iCPU);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iCPU);
  RunTillTransOnDevice aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToTaskTrans(TMLTask* iTask, TMLTransaction*& oLastTrans){
  //ListenerSubject<TaskListener>* aSubject= static_cast<ListenerSubject<TaskListener>* > (iTask);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iTask);
  RunTillTransOnTask aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToSlaveTrans(Slave* iSlave, TMLTransaction*& oLastTrans){
  //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iSlave);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iSlave);
  RunTillTransOnDevice aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToChannelTrans(TMLChannel* iChannel, TMLTransaction*& oLastTrans){
  //ListenerSubject<ChannelListener>* aSubject= static_cast<ListenerSubject<ChannelListener>* > (iChannel);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iChannel);
  RunTillTransOnChannel aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToNextRandomCommand(TMLTransaction*& oLastTrans){
  _randChoiceBreak.setEnabled(true);
  //_randChoiceBreak->setEnabled(true);
  bool aSimTerminated=simulate(oLastTrans);
  _randChoiceBreak.setEnabled(false);
  //_randChoiceBreak->setEnabled(false);
  return aSimTerminated;
}

bool Simulator::runUntilCondition(std::string& iCond, TMLTask* iTask, TMLTransaction*& oLastTrans, bool& oSuccess){
  CondBreakpoint aListener(_simComp, iCond, iTask);
  oSuccess=aListener.conditionValid();
  //return simulate(oLastTrans);
  //aListener.commandEntered(0);
  if (oSuccess) return simulate(oLastTrans); else return false;
}

void Simulator::exploreTree(unsigned int iDepth, ID iPrevID, std::ofstream& iAUTFile, unsigned int& oTransCounter){
  
  TMLTransaction* aLastTrans;
  //if (iDepth<RECUR_DEPTH){
  ID aLastID;
  bool aSimTerminated=false;
  IndeterminismSource* aRandomCmd;

  //std::cout << "Command coverage current:"<<  TMLCommand::getCmdCoverage() << " to reach:" << _commandCoverage << " nbOfBranchesExplored:"<< _nbOfBranchesExplored << " nbOfBranchesToExplore:" << _nbOfBranchesToExplore << " branch coverage:" <<_branchCoverage <<std::endl;
  
  do{
    aSimTerminated=runToNextRandomCommand(aLastTrans);
    aRandomCmd = _simComp->getCurrentRandomCmd();
    //std::cout << "Random command:" << aRandomCmd <<std::endl;
  }while (!aSimTerminated && aRandomCmd==0 && _simComp->wasKnownStateReached()==0);
#ifdef EXPLOGRAPH_ENABLED
  aLastID = schedule2GraphAUT(iAUTFile, iPrevID,oTransCounter);
#endif
  if(aSimTerminated){
    oTransCounter++;
    //#ifdef DOT_GRAPH_ENABLED
    //#else
    //(21,"i(allCPUsTerminated)", 25)
    iAUTFile << "(" << aLastID << "," << "\"i(allCPUsTerminated<" << SchedulableDevice::getSimulatedTime() << ">)\"," << TMLTransaction::getID() << ")\n";
    _nbOfBranchesExplored ++;
    //#endif
    TMLTransaction::incID();

    //if(_commandCoverage <= TMLCommand::getCmdCoverage() && _branchCoverage <= TMLCommand::getBranchCoverage()){
    //std::cout << "Command coverage current:"<<  TMLCommand::getCmdCoverage() << " to reach:" << _commandCoverage << " nbOfBranchesExplored:"<< _nbOfBranchesExplored << " nbOfBranchesToExplore:" << _nbOfBranchesToExplore<< std::endl;
    if (_commandCoverage <= TMLCommand::getCmdCoverage()) {
      if (_nbOfBranchesExplored > 0) {
	if (100 * _nbOfBranchesExplored / _nbOfBranchesToExplore >= _branchCoverage) {
	  //std::cout << "*********************************** 100% REACH" << std::endl;
	  _simComp->setStopFlag(true, MSG_COVREACHED);
	  _terminateExplore=true;
	  //_syncInfo->_terminate=true;
	}
      }
    }
  } else if (_simComp->wasKnownStateReached()==0){
    //std::cout << "No known state reached" << std::endl;
    if(aRandomCmd==0){
      //std::cout << "We should never get here\n";
    } else {
      //std::cout << "Command coverage current:"<<  TMLCommand::getCmdCoverage() << " to reach:" << _commandCoverage << " nbOfBranchesExplored:"<< _nbOfBranchesExplored << " nbOfBranchesToExplore:" << _nbOfBranchesToExplore<< std::endl;
      
      unsigned int aNbNextCmds;
      std::stringstream aStreamBuffer;
      std::string aStringBuffer;
      aNbNextCmds = aRandomCmd->getRandomRange();
      //std::cout << "Simulation " << iPrevID << "_" << "continued nb of nexts commands:" << aNbNextCmds << std::endl;
      _simComp->writeObject(aStreamBuffer);
      aStringBuffer=aStreamBuffer.str();
      if ((aNbNextCmds & INT_MSB)==0){
	_nbOfBranchesToExplore += aNbNextCmds-1;
        //for (unsigned int aBranch=0; aBranch<aNbNextCmds && !_syncInfo->_terminate; aBranch++){
        for (unsigned int aBranch=0; aBranch<aNbNextCmds && !_terminateExplore; aBranch++){
	  //for (unsigned int aBranch=0; aBranch<aNbNextCmds; aBranch++){
	  //std::cout << "1. Exploring branch #" << aBranch << " from " << iPrevID << std::endl;
          _simComp->reset();
          aStreamBuffer.str(aStringBuffer);
          //std::cout << "Read 1 in exploreTree\n";
          _simComp->readObject(aStreamBuffer);
          aRandomCmd->setRandomValue(aBranch);
          exploreTree(iDepth+1, aLastID, iAUTFile, oTransCounter);
	  if (_terminateExplore && aBranch<aNbNextCmds-1) {
	    //std::cout << "Terminate explore but still branches to execute ...\n";
	  }
        }
      } else{
        unsigned int aBranch=0;
        aNbNextCmds ^= INT_MSB;
        //while (aNbNextCmds!=0 && !_syncInfo->_terminate){
	while (aNbNextCmds!=0 && !_terminateExplore){
	  //while (aNbNextCmds!=0){
	  //std::cout << "2. Exploring branch #" << aNbNextCmds << " from " << iPrevID << std::endl;
          if ((aNbNextCmds & 1)!=0){
	    //_nbOfBranchesToExplore += 1;
            _simComp->reset();
            aStreamBuffer.str(aStringBuffer);
            //std::cout << "Read 2 in exploreTree\n";
            _simComp->readObject(aStreamBuffer);
            aRandomCmd->setRandomValue(aBranch);
            exploreTree(iDepth+1, aLastID, iAUTFile, oTransCounter);
          }
          aBranch++; aNbNextCmds >>=1;
        }
      }
    }
    //}else{
    //iFile << "Simulation " << iPrevID << "_" << aMyID << " encountered known state " << aCurrState << std::endl;
    //13 -> 17 [label = "i(CPU0__test1__TMLTask_1__wro__test1__ch<4 ,4>)"];
    //iFile << aLastID << " -> " << aLastID << " [label = \"i\"]\n";

    /*ID aNewID = TMLTransaction::getID();
      TMLTransaction::incID();
      iFile << aLastID << " -> " << aNewID << " [label = \"option\"]\n";
      std::stringstream aStreamBuffer;
      std::string aStringBuffer;
      _simComp->writeObject(aStreamBuffer);
      aStringBuffer=aStreamBuffer.str();
      _simComp->reset();
      aStreamBuffer.str(aStringBuffer);
      _simComp->readObject(aStreamBuffer);*/
    //exploreTree(iDepth, aNewID, iFile/*, iFile2*/);
  }
  //}
}



void Simulator::exploreTreeDOT(unsigned int iDepth, ID iPrevID, std::ofstream& iDOTFile, std::ofstream& iAUTFile, unsigned int& oTransCounter){
  TMLTransaction* aLastTrans;
  //if (iDepth<RECUR_DEPTH){
  ID aLastID;
  bool aSimTerminated=false;
  IndeterminismSource* aRandomCmd;
  do{
    aSimTerminated=runToNextRandomCommand(aLastTrans);
    aRandomCmd = _simComp->getCurrentRandomCmd();
    std::cout << "Random command:" << aRandomCmd <<std::endl;
  }while (!aSimTerminated && aRandomCmd==0 && _simComp->wasKnownStateReached()==0);
#ifdef EXPLOGRAPH_ENABLED
  aLastID = schedule2GraphDOT(iDOTFile, iAUTFile, iPrevID,oTransCounter);
#endif
  if(aSimTerminated){
    oTransCounter++;
    //#ifdef DOT_GRAPH_ENABLED
    iDOTFile << aLastID << " -> " << TMLTransaction::getID() << " [label = \"i(allCPUsTerminated<" << SchedulableDevice::getSimulatedTime() << ">)\"]\n";
    //#else
    //(21,"i(allCPUsTerminated)", 25)
    iAUTFile << "(" << aLastID << "," << "\"i(allCPUsTerminated<" << SchedulableDevice::getSimulatedTime() << ">)\"," << TMLTransaction::getID() << ")\n";
    //#endif
    TMLTransaction::incID();

    if(_commandCoverage <= TMLCommand::getCmdCoverage() && _branchCoverage <= TMLCommand::getBranchCoverage()){
      _simComp->setStopFlag(true, MSG_COVREACHED);
      _terminateExplore=true;
      //_syncInfo->_terminate=true;
    }
  }else if (_simComp->wasKnownStateReached()==0){
    if(aRandomCmd==0){
      std::cout << "We should never get here\n";
    }else{
      unsigned int aNbNextCmds;
      std::stringstream aStreamBuffer;
      std::string aStringBuffer;
      aNbNextCmds = aRandomCmd->getRandomRange();
      std::cout << "Simulation " << iPrevID << "_" << "continued " << aNbNextCmds << std::endl;
      _simComp->writeObject(aStreamBuffer);
      aStringBuffer=aStreamBuffer.str();
      if ((aNbNextCmds & INT_MSB)==0){
        //for (unsigned int aBranch=0; aBranch<aNbNextCmds && !_syncInfo->_terminate; aBranch++){
        for (unsigned int aBranch=0; aBranch<aNbNextCmds && !_terminateExplore; aBranch++){
          _simComp->reset();
          aStreamBuffer.str(aStringBuffer);
          //std::cout << "Read 1 in exploreTree\n";
          _simComp->readObject(aStreamBuffer);
          aRandomCmd->setRandomValue(aBranch);
          exploreTreeDOT(iDepth+1, aLastID, iDOTFile, iAUTFile, oTransCounter);
        }
      }else{
        unsigned int aBranch=0;
        aNbNextCmds ^= INT_MSB;
        //while (aNbNextCmds!=0 && !_syncInfo->_terminate){
        while (aNbNextCmds!=0 && !_terminateExplore){
          if ((aNbNextCmds & 1)!=0){
            _simComp->reset();
            aStreamBuffer.str(aStringBuffer);
            //std::cout << "Read 2 in exploreTree\n";
            _simComp->readObject(aStreamBuffer);
            aRandomCmd->setRandomValue(aBranch);
            exploreTreeDOT(iDepth+1, aLastID, iDOTFile, iAUTFile, oTransCounter);
          }
          aBranch++; aNbNextCmds >>=1;
        }
      }
    }
    //}else{
    //iFile << "Simulation " << iPrevID << "_" << aMyID << " encountered known state " << aCurrState << std::endl;
    //13 -> 17 [label = "i(CPU0__test1__TMLTask_1__wro__test1__ch<4 ,4>)"];
    //iFile << aLastID << " -> " << aLastID << " [label = \"i\"]\n";

    /*ID aNewID = TMLTransaction::getID();
      TMLTransaction::incID();
      iFile << aLastID << " -> " << aNewID << " [label = \"option\"]\n";
      std::stringstream aStreamBuffer;
      std::string aStringBuffer;
      _simComp->writeObject(aStreamBuffer);
      aStringBuffer=aStreamBuffer.str();
      _simComp->reset();
      aStreamBuffer.str(aStringBuffer);
      _simComp->readObject(aStreamBuffer);*/
    //exploreTree(iDepth, aNewID, iFile/*, iFile2*/);
  }
  //}
}

bool Simulator::execAsyncCmd(const std::string& iCmd){
  unsigned int aCmd;
  std::istringstream aInpStream(iCmd);
  std::string aStrParam;
  aInpStream >> aCmd;
  std::ostringstream aMessage;
  switch (aCmd){
  case 0: //Quit simulation
    aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulator terminated" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_BUSY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
    _syncInfo->_server->sendReply(aMessage.str());
    _simComp->setStopFlag(true, MSG_SIMSTOPPED);
    _syncInfo->_terminate=true;
    return false;
  case 13://get current time
    if (_longRunTime > 0) {
    aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_TIMEo << SchedulableDevice::getSimulatedTime() << TAG_TIMEc << std::endl << TAG_TIME_MINo << _shortRunTime << TAG_TIME_MINc << std::endl << TAG_TIME_MAXo << _longRunTime << TAG_TIME_MAXc <<  std::endl << TAG_MSGo << "Simulation time" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
    } else {
      aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_TIMEo << SchedulableDevice::getSimulatedTime() << TAG_TIMEc <<  std::endl << TAG_MSGo << "Simulation time" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
    }
    //if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
    writeSimState(aMessage);
    aMessage << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
    _syncInfo->_server->sendReply(aMessage.str());
    break;
  case 14:{//get actual command, thread safeness, be careful!
    aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl;
    aInpStream >> aStrParam;
    if (aStrParam=="all"){
      //for(TaskList::const_iterator i=_simComp->getTaskIterator(false); i !=_simComp->getTaskIterator(true); ++i){
      for(TaskList::const_iterator i=_simComp->getTaskList().begin(); i !=_simComp->getTaskList().end(); ++i){
        printCommandsOfTask(*i, aMessage);
      }
      aMessage << TAG_GLOBALo << std::endl << TAG_MSGo << "Current command" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
    }else{
      TMLTask* aTask = _simComp->getTaskByName(aStrParam);
      aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl;
      if (aTask!=0){
        printCommandsOfTask(aTask, aMessage);
        aMessage << TAG_GLOBALo << std::endl << TAG_MSGo << "Current command" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
      }else{
        aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << TAG_ERRNOo << 2;
      }
    }
    //if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
    writeSimState(aMessage);
    aMessage << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
    std::cout << aMessage.str();
    _syncInfo->_server->sendReply(aMessage.str());
    break;
  }
  case 15://pause simulation
    _simComp->setStopFlag(true, MSG_SIMPAUSED);
    aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulation stopped" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
    writeSimState(aMessage);
    aMessage << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
    _syncInfo->_server->sendReply(aMessage.str());
    break;
  default:
    return false;
  }
  return true;
}

void Simulator::printCommandsOfTask(TMLTask* iTask, std::ostream& ioMessage){
  ioMessage << TAG_TASKo << " id=\"" << iTask-> getID() << "\" name=\"" << iTask->toString() << "\">" << TAG_CURRCMDo << " id=\"";
  TMLCommand* currCommand=iTask->getCurrCommand();
  //if (iTask->getCurrCommand()==0)
  if (currCommand==0){
    ioMessage << 0 << "\">";
  }else{
    ioMessage << currCommand->getID() << "\">" << TAG_PROGRESSo << currCommand->getProgressInPercent() << TAG_PROGRESSc;
    ioMessage << TAG_STARTTIMEo << currCommand->getCommandStartTime() << TAG_STARTTIMEc;
    TMLTransaction* currTrans = currCommand->getCurrTransaction();
    if (currTrans==0 || currTrans->getOverallLength()==0 || currTrans->getVirtualLength()==0){
      ioMessage << TAG_FINISHTIMEo << "0" << TAG_FINISHTIMEc;
      ioMessage << TAG_FINISHTIMETRANSo << "0" << TAG_FINISHTIMETRANSc;
      ioMessage << TAG_STARTTIMETRANSo << "0" << TAG_STARTTIMETRANSc;
    }else{
      ioMessage << TAG_FINISHTIMEo << (currTrans->getEndTime() + currTrans->getOverallLength()*(currCommand->getLength()-currCommand->getProgress()-currTrans->getVirtualLength())/currTrans->getVirtualLength()) << TAG_FINISHTIMEc;
      //if (currCommand->getLength()==currCommand->getProgress())
      //ioMessage << TAG_STARTTIMETRANSo << "99" << TAG_STARTTIMETRANSc;
      //else
      ioMessage << TAG_STARTTIMETRANSo << currTrans->getStartTime() << TAG_STARTTIMETRANSc;
      ioMessage << TAG_FINISHTIMETRANSo << currTrans->getEndTime() << TAG_FINISHTIMETRANSc;
    }
    unsigned int aNbNextCmds;
    TMLCommand** aNextCmds = currCommand->getNextCommands(aNbNextCmds);
    for(unsigned int i=0; i<aNbNextCmds; i++){
      ioMessage << TAG_NEXTCMDo << aNextCmds[i]->getID() << TAG_NEXTCMDc;
    }
  }
  ioMessage << TAG_CURRCMDc << TAG_TASKc << std::endl;
}

void Simulator::sendStatus(){
  std::ostringstream aMessage;
  aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulator status notification" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
  //if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
  writeSimState(aMessage);
  aMessage << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
  _syncInfo->_server->sendReply(aMessage.str());
}

bool Simulator::isBusy(){
  return _busy;
}

void Simulator::writeSimState(std::ostream& ioMessage){
  ioMessage << TAG_STATUSo;
  if (_busy){
    ioMessage << SIM_BUSY << TAG_STATUSc;
  }else{
    if (_simTerm){
      ioMessage << SIM_TERM << TAG_STATUSc << TAG_REASONo << MSG_SIMENDED << TAG_REASONc;
    }else{
      ioMessage << SIM_READY << TAG_STATUSc;
      if (_simComp->getStopReason()!="") ioMessage << TAG_REASONo << _simComp->getStopReason() << TAG_REASONc;
    }
  }
}
