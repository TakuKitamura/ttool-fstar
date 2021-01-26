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
#include <FPGA.h>
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
#include <stdio.h>
#include <unistd.h>
#define GetCurrentDir getcwd
class CurrentComponents;

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

    //std::cout << "Preparing next transaction" << aTempDevice->toString() << "\n";
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
  for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
    aTempDevice=*i;
    aTempTrans=aTempDevice->getNextTransaction();
    if (aTempTrans!=0 && aTempTrans->getVirtualLength()>0){
#ifdef DEBUG_KERNEL
      std::cout << "kernel:getTLET: transaction found on " << aTempDevice->toString() << ": " << aTempTrans->toString() << std::endl;
#endif
      std::cout<<aTempTrans->toShortString()<<"getEndtime is "<<aTempTrans->getEndTime()<<std::endl;
      std::cout<<"alowest time is "<<aLowestTime<<std::endl;
      if (aTempTrans->getEndTime() < aLowestTime){
	std::cout<<"in!!!"<<std::endl;
        aMarker=aTempTrans;
        aLowestTime=aTempTrans->getEndTime();
        oResultDevice=aTempDevice;     
      }
    }
    //#ifdef DEBUG_KERNEL
    else {

    }
  }
  return aMarker;
}
/*
TMLTransaction* Simulator::getTransLowestEndTimeFPGA(SchedulableDevice*& oResultDevice) const{
  //int tmp=0;
  TMLTransaction *aMarker=0, *aTempTrans;
  TMLTime aLowestTime=-1;
  SchedulableDevice* aTempDevice;

#ifdef DEBUG_KERNEL
  std::cout << "kernel:getTLET: before loop" << std::endl;
#endif
  for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
    aTempDevice=*i;
    aTempTrans=aTempDevice->getNextTransaction();
    if (aTempTrans!=0 && aTempTrans->getVirtualLength()>0){
#ifdef DEBUG_KERNEL
      std::cout << "kernel:getTLET: transaction found on " << aTempDevice->toString() << ": " << aTempTrans->toString() << std::endl;
#endif
      std::cout<<aTempTrans->toShortString()<<"getEndtime is "<<aTempTrans->getEndTime()<<std::endl;
      std::cout<<"alowest time is "<<aLowestTime<<std::endl;
      if (aTempTrans->getEndTime() < aLowestTime){
	std::cout<<"in!!!"<<std::endl;
        aMarker=aTempTrans;
        aLowestTime=aTempTrans->getEndTime();
        oResultDevice=aTempDevice;     
      }
    }
    //#ifdef DEBUG_KERNEL
    else {

    }
  }
  return aMarker;
}
*/

ID Simulator::schedule2GraphAUT(std::ostream& iAUTFile, ID iStartState, unsigned int& oTransCounter) const{
  std::cout<<"schedule graph aut!"<<std::endl;
  // CPUList::iterator i;
  //std::cout << "entry graph output\n";
  GraphTransactionQueue aQueue;
  TMLTransaction* aTrans, *aTopElement;
  ID aStartState=iStartState, aEndState=0;
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      aTrans = (*i)->getTransactions1By1(true);
      if (aTrans!=0) {
	aQueue.push(aTrans);
      }
  }
  for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
       aTrans = (*i)->getTransactions1By1(true);
       if (aTrans!=0) aQueue.push(aTrans);
  }
  //std::ostringstream aOutp;
  while (!aQueue.empty()){
    CPU* aCPU;
    FPGA* aFPGA;
    aTopElement = aQueue.top();
    aCPU = aTopElement->getCommand()->getTask()->getCPU();
    aFPGA = aTopElement->getCommand()->getTask()->getFPGA();
    aEndState = aTopElement->getStateID();
    if (aEndState==0){
      aEndState=TMLTransaction::getID();
      TMLTransaction::incID();
    }
    //13 -> 17 [label = "i(CPU0__test1__TMLTask_1__wro__test1__ch<4 ,4>)"];
    oTransCounter++;
    //(20,"i(CPU0__test1__TMLTask_1__wr__test1__ch<4 ,4>)", 24)
    //std::cout << "(" << aStartState<< "," << "\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
    if(aCPU){
      if(aCPU->getAmoutOfCore()>1){
	iAUTFile << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "_core_" << aTopElement->getTransactCoreNumber() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr() << "_Endtime<" << aTopElement->getEndTime() << ">";
	std::cout << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "_core_" << aTopElement->getTransactCoreNumber() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
      }
      else {
	iAUTFile << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr() << "_Endtime<" << aTopElement->getEndTime() << ">";
	std::cout << "(" << aStartState << "," << "\"i(" << aCPU->toString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr();
      }
    }
    else if(aFPGA){
      iAUTFile << "(" << aStartState << "," << "\"i(" << aFPGA->toString() << "_core_" << aTopElement->toShortString() << "__" << aTopElement->getCommand()->getTask()->toString() << "__" << aTopElement->getCommand()->getCommandStr() << "_Endtime<" << aTopElement->getEndTime() << ">";
    }
    if (aTopElement->getChannel()!=0){
      iAUTFile << "__" << aTopElement->getChannel()->toShortString();
      std::cout << "__" << aTopElement->getChannel()->toShortString();
    }
    iAUTFile << "<" << aTopElement->getVirtualLength() << ">)\"," << aEndState <<")\n";
    std::cout << "<" << aTopElement->getVirtualLength() << ">)\"," << aEndState <<")\n";
    aStartState = aEndState;
    aQueue.pop();
    if(aCPU)
      aTrans = aCPU->getTransactions1By1(false);
    else if(aFPGA)
      aTrans = aFPGA->getTransactions1By1(false);
    if (aTrans!=0) aQueue.push(aTrans);
  }
  std::cout << "exit graph output\n";
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
  std::cout<<"schedule graph"<<std::endl;
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
    for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
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


void Simulator::schedule2XML(std::ostringstream& glob,std::string& iTraceFileName) const{
  struct timeval aBegin,aEnd;
  gettimeofday(&aBegin,NULL);

  if ( !ends_with( iTraceFileName, EXT_XML ) ) {
    iTraceFileName.append( EXT_XML );
  }

  std::ofstream myfile(iTraceFileName.c_str());
  if (myfile.is_open()){

      glob << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulator status notification" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
          //if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;

    //for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      (*i)->schedule2XML(glob,myfile);
    }
    for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
      (*i)->schedule2XML(glob,myfile);
    }
    //for(BusList::const_iterator j=_simComp->getBusIterator(false); j != _simComp->getBusIterator(true); ++j){
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->schedule2XML(glob,myfile);
    }
    glob << TAG_MODELo<< _simComp->getModelName() << TAG_MODELc; //name of model
    glob << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;

    myfile << glob.str() << std::endl;
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

 for(FPGAList::const_iterator k=_simComp->getFPGAList().begin(); k != _simComp->getFPGAList().end(); ++k){
    total += (*k)->allTrans2XML(glob, maxNbOfTrans);
  }
  return total;
}

int Simulator::allTrans2XMLByTask(std::ostringstream& glob, std::string taskName) const{
  int total = 0;
  //glob << TAG_TRANSo << "Transaction" << TAG_TRANSc << std::endl;
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    total += (*i)->allTrans2XMLByTask(glob, taskName);
  }

  for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
    total += (*j)->allTrans2XMLByTask(glob, taskName);
  }

  for(FPGAList::const_iterator k=_simComp->getFPGAList().begin(); k != _simComp->getFPGAList().end(); ++k){
    total += (*k)->allTrans2XMLByTask(glob, taskName);
  }

  return total;
}

void Simulator::removeOldTransaction(int numberOfTrans) {
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i) {
        for(TaskList::const_iterator j = (*i)->getTaskList().begin(); j != (*i)->getTaskList().end(); ++j) {
            (*j)->removeTrans(numberOfTrans);
        }
    }
    for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i) {
        for(TaskList::const_iterator j = (*i)->getTaskList().begin(); j != (*i)->getTaskList().end(); ++j) {
            (*j)->removeTrans(numberOfTrans);
        }
    }
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i) {
      (*i)->removeTrans(numberOfTrans);
    }

    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j) {
      (*j)->removeTrans(numberOfTrans);
    }

    for(FPGAList::const_iterator k=_simComp->getFPGAList().begin(); k != _simComp->getFPGAList().end(); ++k) {
        (*k)->removeTrans(numberOfTrans);
    }
}

void Simulator::latencies2XML(std::ostringstream& glob, int id1, int id2) {
  for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
    (*i)->latencies2XML(glob, id1, id2);
  }

  for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
    (*j)->latencies2XML(glob, id1,id2);
  }
}
void Simulator::timeline2HTML(std::string& iTracetaskList, std::ostringstream& myfile) const {

    std::map<TMLTask*, std::string> taskCellClasses;
    std::ostringstream myfileTemp, myfileTemp1;

    myfile << "<!DOCTYPE html>"; // <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n
    myfile << "<html>"; // <html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n
    myfile << "<head>"; // <head>\n
    myfile << "<style>"; // <style>\n;
    myfile << SCHED_HTML_CSS_CONTENT_TIMELINE;
    myfile << "</style>"; // <style>\n";
    myfile << "<title>"; // <title>
    myfile << "Timeline Diagram";
    myfile << "</title>"; // </title>\n
    myfile << "</head>"; // </head>\n
    myfile << SCHED_HTML_BEG_BODY; // <body>\n
//        myfile << "<h1>Task to show: " << iTracetaskList.c_str() <<"</h1>\n";
    unsigned int maxScale = 0;
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
        for(unsigned int j = 0; j < (*i)->getAmoutOfCore(); j++) {
            taskCellClasses = (*i)->HWTIMELINE2HTML(myfileTemp, taskCellClasses, taskCellClasses.size(), iTracetaskList);
            if((*i)->getMaxScale() > maxScale) {
                maxScale = (*i)->getMaxScale();
            }
            (*i)->setCycleTime((*i)->getCycleTime()+1);
        }
//        if((*i)->getAmoutOfCore() == 1)
//            (*i)->setCycleTime(0);
        (*i)->setCycleTime(0);
        }

    for(FPGAList::const_iterator j=_simComp->getFPGAList().begin(); j != _simComp->getFPGAList().end(); ++j){
        (*j)->setStartFlagHTML(true);
        for(TaskList::const_iterator i = (*j)->getTaskList().begin(); i != (*j)->getTaskList().end(); ++i){
            (*j)->setHtmlCurrTask(*i);
            taskCellClasses = (*j)->HWTIMELINE2HTML(myfileTemp, taskCellClasses, taskCellClasses.size(), iTracetaskList);
            if((*j)->getMaxScale() > maxScale) {
                maxScale = (*j)->getMaxScale();
            }
            (*j)->setStartFlagHTML(false);
        }
    }

    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
        taskCellClasses = (*j)->HWTIMELINE2HTML(myfileTemp, taskCellClasses, taskCellClasses.size(), iTracetaskList);
        if((*j)->getMaxScale() > maxScale) {
            maxScale = (*j)->getMaxScale();
        }
    }

    myfileTemp << "</tr><tr><th></th><th class=\"notfirst\"></th></tr><div class = \"clear\"></div>";
    for ( unsigned int aLength = 0; aLength < maxScale; aLength++ ) {
        if( aLength == 1) {
          myfileTemp << "<th class=\"notfirst\">";
        } else {
          myfileTemp << "<th></th>";
        }
    }
    myfileTemp << "</table>";
    myfileTemp1 << "<table style=\"float: left;position: relative;\"><tr><td width=\"170px\" style=\"max-width: unset;min-width: 170px;border-style: none none none none;\"></td>"
           << "<td class=\"notfirst\"></td>"
           << "<td style=\"border-style: solid none none none; border-width: 2px;border-color: red;text-align:right\"colspan=\"" << maxScale << "\"><b>Time</b></td></tr>"
           << "<tr><th></th><th class=\"notfirst\"></th></tr>"
           << "<div class = \"clear\"></div>";
    myfile << "<table><tr><td width=\"170px\" style=\"max-width: unset;min-width: 170px;border-style: none none none none;\"></td><td class=\"notlast\"></td><td class=\"notlast\"></td>";
    for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
        TMLTask* task = (*taskColIt).first;
        // Unset the default td max-width of 5px. For some reason setting the max-with on a specific t style does not work
        myfile << "<td class=\"" << taskCellClasses[ task ] << "\"></td><td style=\"max-width: unset;min-width: 170px;\">" << task->toString() << "</td><td class=\"space\"></td>";
    }
    myfile << "</tr><tr><td width=\"170px\" style=\"max-width: unset;min-width: 170px;border-style: none none none none;\"></td><td class=\"notlast\"></td><td class=\"notlast\"></td>";
    for( std::map<TMLTask*, std::string>::iterator taskColIt = taskCellClasses.begin(); taskColIt != taskCellClasses.end(); ++taskColIt ) {
        TMLTask* task = (*taskColIt).first;
        std::string str = getTaskCellStatus(task->getState());
        std::size_t pos = str.find(":");
        if(pos != std::string::npos){
            std::string str1 = str.substr (0, pos);
            std::string str2 = str.substr (pos + 1);
            myfile << "<td style=\"border-style: none none none none;\"></td><td style=\"max-width: unset;min-width: 170px;border-style: none none none none;color:" << str2 << "\"><b>" <<  str1 << "</b></td><td class=\"space\"></td>";
        }
    }
    myfile << "</tr></table>";
    myfile << myfileTemp1.str();
    myfile << myfileTemp.str();
    myfile << "</body>"; // </body>\n
    myfile << "</html>"; // </html>\n
}
std::string Simulator::getTaskCellStatus(int i) const {
    switch (i){
        case 2:
            return "Running:green";
        case 1:
            return "Runnable:blue";
        case 0:
            return "Suspended:orange";
        case 3:
            return "Terminated:red";
    }
    return "Unknown:gray";
}
void Simulator::schedule2HTML(std::string& iTraceFileName) const {
#ifdef DEBUG_HTML
std::cout<<"schedule2HTML--------------------------------------******************"<<std::endl;
#endif
  struct timeval aBegin,aEnd;
  time_t aRawtime;
  struct tm * aTimeinfo;
  gettimeofday(&aBegin,NULL);
  time(&aRawtime);
  aTimeinfo=localtime(&aRawtime);

  if ( !ends_with( iTraceFileName, EXT_HTML ) ) {
    iTraceFileName.append( EXT_HTML );
  }


  std::ofstream myfile(iTraceFileName.c_str());
   //myfile<<"model name: "<<iTraceFileName.c_str();


  if (myfile.is_open()) {
    // DB: Issue #4
    myfile << SCHED_HTML_DOC; // <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n
    myfile << SCHED_HTML_BEG_HTML; // <html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n
    myfile << SCHED_HTML_BEG_HEAD; // <head>\n
    const std::string::size_type findSlash = iTraceFileName.find_last_of( "/" );
    unsigned int indexSlash;

    if ( findSlash == std::string::npos ) {
      indexSlash = -1;
      //myfile<<"indexSlash=0\n";
    }
    else {
      indexSlash = findSlash;
    }

    const std::string ext( EXT_HTML );
    const std::string cssFileName = iTraceFileName.substr( indexSlash + 1, iTraceFileName.length() - indexSlash - ext.length() - 1 ) + EXT_CSS;
    //const std::string jsFileName = iTraceFileName.substr( indexSlash + 1, iTraceFileName.length() - indexSlash - ext.length() - 1 ) + EXT_JS;
    //myfile<<"length is "<< iTraceFileName.length() - indexSlash - ext.length() - 1<<std::endl;
    const std::string cssFullFileName = iTraceFileName.substr( 0, indexSlash + 1 ) + cssFileName;
    //const std::string jsFullFileName =  iTraceFileName.substr( 0, indexSlash + 1 ) + jsFileName;
    std::ofstream cssfile( cssFullFileName.c_str() );
    //std::ofstream jsfile( jsFullFileName.c_str() );

    //myfile<<"full name is "<<cssFullFileName<<std::endl;
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

    myfile << "<ul>\n";
    myfile << "<li>Model name: "<< _simComp->getModelName() << "</li><br>\n"; //name of model
    myfile << "<li> Date: " << asctime(aTimeinfo) << "</li>\n"; //date and time
    myfile << "</ul>\n";

    // myfile << SCHED_HTML_JS_DIV_SUB_BEGIN;
    // myfile << SCHED_HTML_JS_TYPE;
    // myfile << SCHED_HTML_JS_CONTENT1;
    char cCurrentPath[FILENAME_MAX];
    GetCurrentDir(cCurrentPath, sizeof(cCurrentPath));
    cCurrentPath[sizeof(cCurrentPath) - 1] = '\0';
    std::string str = cCurrentPath;
    std::size_t pos = str.find("c++_code"); /*pos = position of "c++_code" if we working with open project*/
    std::size_t pos1 = str.find("/bin"); /*pos1 = position of "bin" if we working with open model*/
    if(pos != std::string::npos){
      myfile << "<script src=\"" << str << "/src_simulator/jquery.min.js\">" << SCHED_HTML_END_JS << std::endl;
      myfile << "<script src=\"" << str << "/src_simulator/Chart.min.js\">" << SCHED_HTML_END_JS << std::endl;
    }
    else if (pos1 != std::string::npos){
      myfile << "<script src=\"" << str.substr(0,pos1) << "/simulators/c++2/src_simulator/jquery.min.js\">" << SCHED_HTML_END_JS << std::endl;
      myfile << "<script src=\"" << str.substr(0,pos1) << "/simulators/c++2/src_simulator/Chart.min.js\">" << SCHED_HTML_END_JS << std::endl;
    }
    else {
      myfile << SCHED_HTML_JS_LINK1 << SCHED_HTML_END_JS << std::endl;
      myfile << SCHED_HTML_JS_LINK2 << SCHED_HTML_END_JS << std::endl;
    }
    myfile << SCHED_HTML_BEGIN_JS << std::endl;

    myfile << SCHED_HTML_JS_WINDOW;
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      (*i)->drawPieChart(myfile);
      }
    for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
      for(TaskList::const_iterator j = (*i)->getTaskList().begin(); j != (*i)->getTaskList().end(); ++j){
      	(*i)->setHtmlCurrTask(*j);
	(*i)->drawPieChart(myfile);
      }
      // (*i)->buttonPieChart(myfile);
    }
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
       (*j)->drawPieChart(myfile);
    }

    myfile << "var " << SHOW_PIE_CHART << " = false;" << std::endl;
    myfile << "$(\"#button\").click(function() {\n";
    myfile << "    " << SHOW_PIE_CHART << "=!" << SHOW_PIE_CHART << std::endl;
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      (*i)->buttonPieChart(myfile);
    }
    for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
      (*i)->buttonPieChart(myfile);
    }
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->buttonPieChart(myfile);
    }
    myfile << "     });" << std::endl << "}" << std::endl;

    myfile << SCHED_HTML_END_JS << std::endl; //<script>
    // myfile << SCHED_HTML_END_JS;
    //myfile << SCHED_HTML_JS_LINK;
    //myfile << SCHED_HTML_END_JS;
    //jsfile.close();
    //for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){

    myfile << SCHED_HTML_TITLE_HW << std::endl;
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      for(unsigned int j = 0; j < (*i)->getAmoutOfCore(); j++) {
        //std::cout<<"core number is "<<(*i)->getAmoutOfCore()<<std::endl;
	      (*i)->HW2HTML(myfile);
	      //(*i)->showPieChart(myfile);
	      (*i)->setCycleTime((*i)->getCycleTime()+1);

      }
//        if((*i)->getAmoutOfCore() == 1)
//	   (*i)->setCycleTime(0);
	  (*i)->setCycleTime(0);
    }

    for(FPGAList::const_iterator j=_simComp->getFPGAList().begin(); j != _simComp->getFPGAList().end(); ++j){
      (*j)->setStartFlagHTML(true);
      for(TaskList::const_iterator i = (*j)->getTaskList().begin(); i != (*j)->getTaskList().end(); ++i){
      	(*j)->setHtmlCurrTask(*i);
#ifdef DEBUG_HTML
	std::cout<<"begin fpga html "<<(*j)->toShortString()<<std::endl;
	std::cout<<"task is !!!!!"<<(*i)->toString()<<std::endl;
#endif
	(*j)->HW2HTML(myfile);
	(*j)->setStartFlagHTML(false);
      }
      myfile << "</tr>" << std::endl << "</table>" << std::endl << SCHED_HTML_JS_DIV_END << std::endl;
      myfile << SCHED_HTML_JS_CLEAR << std::endl;
    }



    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->HW2HTML(myfile);
      // (*j)->showPieChart(myfile);
    }
    //for_each(iCPUlist.begin(), iCPUlist.end(),std::bind2nd(std::mem_fun(&CPU::schedule2HTML),myfile));

    myfile << SCHED_HTML_JS_TABLE_BEGIN << std::endl;
    myfile << SCHED_HTML_JS_BUTTON << std::endl;
    myfile << SCHED_HTML_JS_TABLE_END << std::endl;

     for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      (*i)->showPieChart(myfile);
    }
    for(FPGAList::const_iterator j=_simComp->getFPGAList().begin(); j != _simComp->getFPGAList().end(); ++j){
      for(TaskList::const_iterator i = (*j)->getTaskList().begin(); i != (*j)->getTaskList().end(); ++i){
	(*j)->setHtmlCurrTask(*i);
	(*j)->showPieChart(myfile);
      }
    }
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->showPieChart(myfile);
    }
    myfile << SCHED_HTML_JS_CLEAR << std::endl;
    myfile << SCHED_HTML_TITLE_TASK << std::endl;
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      for(TaskList::const_iterator j = (*i)->getTaskList().begin(); j != (*i)->getTaskList().end(); ++j){
	(*j)->schedule2HTML(myfile);
      }
    }
    for(FPGAList::const_iterator i=_simComp->getFPGAList().begin(); i != _simComp->getFPGAList().end(); ++i){
      for(TaskList::const_iterator j = (*i)->getTaskList().begin(); j != (*i)->getTaskList().end(); ++j){
	(*j)->schedule2HTML(myfile);
      }
    }
    myfile << SCHED_HTML_TITLE_DEVICE << std::endl;
    for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
      for(unsigned int j = 0; j < (*i)->getAmoutOfCore(); j++) {
	    (*i)->schedule2HTML(myfile);
	    (*i)->setCycleTime((*i)->getCycleTime()+1);
      }
//      if((*i)->getAmoutOfCore() == 1)
//        (*i)->setCycleTime(0);
	  (*i)->setCycleTime(0);
    }
     for(FPGAList::const_iterator j=_simComp->getFPGAList().begin(); j != _simComp->getFPGAList().end(); ++j){
      (*j)->setStartFlagHTML(true);
      for(TaskList::const_iterator i = (*j)->getTaskList().begin(); i != (*j)->getTaskList().end(); ++i){
      	(*j)->setHtmlCurrTask(*i);
	(*j)->schedule2HTML(myfile);
	(*j)->setStartFlagHTML(false);
      }
      (*j)->scheduleBlank(myfile);
    }
    for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
      (*j)->schedule2HTML(myfile);
    }
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
#ifdef DEBUG_VCD
  std::cout<<"schedule2VCD~~~~~~~~~~~~"<<std::endl;
#endif
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
    //std::queue<SignalChangeData*> aQueue;
    //std::string aSigString;
    //bool aNoMoreTrans;
    //TraceableDevice* actDevice;
    TMLTime aCurrTime=-1;
    SignalChangeData* aTopElement;
    TMLTime aNextClockEvent=0;
    myfile << "$date\n" << asctime(aTimeinfo) << "$end\n\n$version\nDaniel's TML simulator\n$end\n\n";
    myfile << "$timescale\n5 ns\n$end\n\n$scope module Simulation $end\n";
    //std::cout << "Before 1st loop" << std::endl;

    for (TraceableDeviceList::const_iterator i=_simComp->getVCDList().begin(); i!= _simComp->getVCDList().end(); ++i){
      //TraceableDevice* a=*i;
      //                        a->streamBenchmarks(std::cout);
      //                        a->toString();
      //std::cout << "in 1st loop " << a << std::endl;
      //std::cout << "device: " << (*i)->toString() << std::endl;
      //myfile << "$var integer 3 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
      if ((*i)->toShortString().substr(0,3) == "cpu"){
	for(unsigned int j = 0; j < (dynamic_cast<CPU*>(*i))->getAmoutOfCore(); j++) {
	  myfile << "$var wire 1 " << (*i)->toShortString() << "_core" << j << " " << (*i)->toString() << "_Core" << j << " $end\n";
	  aTopElement = new SignalChangeData();
	  aTopElement->_coreNumberVcd=j;
	  (*i)->getNextSignalChange(true, aTopElement);
	  aQueue.push(aTopElement);
	  // (dynamic_cast<CPU*>(*i))->setCycleTime( (dynamic_cast<CPU*>(*i))->getCycleTime()+1);
	}
      }
       else if((*i)->toShortString().substr(0,4) == "fpga"){
	 for(TaskList::const_iterator j = _simComp->getTaskList().begin(); j != _simComp->getTaskList().end(); j++){
	   aTopElement = new SignalChangeData();
	   aTopElement->_taskFPGA=(*j);
	   (*i)->getNextSignalChange(true, aTopElement);
	   if(aTopElement->_device){
#ifdef DEBUG_VCD
	     std::cout<<"name of fpga is : "<< (*i)->toShortString() << "_" << (*j)->toString() << std::endl;
#endif
	     myfile << "$var wire 1 " << (*i)->toShortString() << "_" << (*j)->toString() << " " << (*i)->toString() << "_" << (*j)->toString() << " $end\n";
	     aQueue.push(aTopElement);
	   }
	 }
       }
      else{
	if(((*i)->toShortString().substr(0,2) == "ta"))
	  myfile << "$var wire 1 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
	else
	  myfile << "$var wire 1 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
 	aTopElement = new SignalChangeData();
	(*i)->getNextSignalChange(true, aTopElement);
	aQueue.push(aTopElement);
      }
    }


    //  (dynamic_cast<CPU*>(*i))->setCycleTime( (dynamic_cast<CPU*>(*i))->getCycleTime()+1);


    myfile << "$var integer 32 clk Clock $end\n";
    myfile << "$upscope $end\n$enddefinitions  $end\n\n";

    while (!aQueue.empty()){
      // std::cout<<"this is queue"<<std::endl;
      aTopElement=aQueue.top();
      if( aTopElement->_device->toShortString().substr(0,3) == "cpu")
	std::cout<<"the member of queue is "<<aTopElement->_device->toShortString()<< "_core" << aTopElement->_coreNumberVcd<<std::endl;
      else if( aTopElement->_device->toShortString().substr(0,4) == "fpga")
        std::cout<<"the member of queue is "<<aTopElement->_device->toShortString()<< "_" << aTopElement->_taskFPGA->toString()<<std::endl;
      else
	 std::cout<<"the member of queue is "<<aTopElement->_device->toShortString() <<std::endl;


      while (aNextClockEvent < aTopElement->_time){
	myfile << "#" << aNextClockEvent << "\nr" << aNextClockEvent << " clk\n";
	aNextClockEvent+=CLOCK_INC;
	//std::cout<<"aaaa"<<std::endl;
      }
      if (aCurrTime!=aTopElement->_time){
	aCurrTime=aTopElement->_time;
	//std::cout<<"bbbbb"<<std::endl;
	myfile << "#" << aCurrTime << "\n";
      }
      if (aNextClockEvent == aTopElement->_time){
	myfile << "b" << vcdTimeConvert(aNextClockEvent) << " clk\n";
	//std::cout<<"ccccc"<<std::endl;
	aNextClockEvent+=CLOCK_INC;
      }
      //myfile << aTopElement->_sigChange << "\n";
      if( aTopElement->_device->toShortString().substr(0,3) == "cpu" )
	myfile << vcdValConvert(aTopElement->_sigChange) << aTopElement->_device->toShortString() << "_core" << aTopElement->_coreNumberVcd << "\n";

      else if( aTopElement->_device->toShortString().substr(0,4) == "fpga")
	myfile << vcdValConvert(aTopElement->_sigChange) << aTopElement->_device->toShortString() << "_" << aTopElement->_taskFPGA->toString() << "\n";

      else if( aTopElement->_device->toShortString().substr(0,2) == "ta" )
	myfile <<"b"<< vcdTaskValConvert(aTopElement->_sigChange) <<" "<< aTopElement->_device->toShortString() << "\n";

      else myfile << vcdValConvert(aTopElement->_sigChange) << aTopElement->_device->toShortString() << "\n";
      aQueue.pop();
      TMLTime aTime = aTopElement->_time;
      aTopElement->_device->getNextSignalChange(false, aTopElement);
#ifdef DEBUG_VCD
      std::cout<<"aTime is "<<aTime<<std::endl;
      std::cout<<"top element time is "<<aTopElement->_time<<std::endl;
#endif
      if (aTopElement->_time == aTime){
	delete aTopElement;
	//	std::cout<<"delete"<<std::endl;
      }
      else{
	aQueue.push(aTopElement);
	//std::cout<<"no delete"<<std::endl;
      }
    }


      //actDevice=aTopElement->_device;
      //if (actDevice!=0) aTime = actDevice->getNextSignalChange(false, aSigString, aNoMoreTrans);
      //delete aTopElement;
      //aQueue.pop();
      //if (actDevice!=0) aQueue.push(new SignalChangeData(aSigString, aTime, (aNoMoreTrans)?0:actDevice));

    myfile << "#" << aCurrTime+1 << "\n";
    std::cout << "Simulated cycles: " << aCurrTime << std::endl;
    //for (TraceableDeviceList::const_iterator i=_simComp->getVCDIterator(false); i!= _simComp->getVCDIterator(true); ++i){
    ///////test//////////
    for (TraceableDeviceList::const_iterator i=_simComp->getVCDList().begin(); i!= _simComp->getVCDList().end(); ++i){
      if ((*i)->toShortString().substr(0,3)=="cpu"){
	for (unsigned int j = 0; j < (dynamic_cast<CPU*>(*i))->getAmoutOfCore();j++){
	  myfile << "0" << (*i)->toShortString() << "\n";
	}
      }
       else if((*i)->toShortString().substr(0,4) == "FPGA"){
	for(TaskList::const_iterator j=_simComp->getTaskList().begin(); j!=_simComp->getTaskList().end();j++){
	   myfile << "0" << (*i)->toShortString() << "\n";
	}
       }
      //myfile << VCD_PREFIX << "100 " << (*i)->toShortString() << "\n";
      else  myfile << "0" << (*i)->toShortString() << "\n";
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
  TMLTransaction* depTransaction,*depNextTrans,*transLET;
  TMLCommand* commandLET,*depCommand,*depNextCommand;
  TMLTask* depTask;
  SchedulableDevice* deviceLET;
  CPU* depCPU;
  FPGA* depFPGA;

  bool isFinish=true;
//  bool isHanging = false;
//  long countMaxTrans = 0;


#ifdef DEBUG_KERNEL
  std::cout << "kernel:simulate: first schedule" << std::endl;
#endif
  _simComp->setStopFlag(false,"");
  for(TaskList::const_iterator i=_simComp->getTaskList().begin(); i!=_simComp->getTaskList().end();i++){
    if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare(true);
    //std::cout<<"in prepare"<< (*i)->toString() << std::endl;
  }
#ifdef EBRDD_ENABLED
  for(EBRDDList::const_iterator i=_simComp->getEBRDDIterator(false); i!=_simComp->getEBRDDIterator(true);i++){
    if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare();
  }
#endif
#ifdef DEBUG_SIMULATE
  std::cout<<"simulate"<<std::endl;
#endif
  for_each(_simComp->getCPUList().begin(), _simComp->getCPUList().end(),std::mem_fun(&CPU::schedule));

  for_each(_simComp->getFPGAList().begin(), _simComp->getFPGAList().end(),std::mem_fun(&FPGA::schedule));

  transLET=getTransLowestEndTime(deviceLET);
#ifdef LISTENERS_ENABLED
  if (_wasReset) NOTIFY_SIM_STARTED();
  _wasReset=false;
#endif


  if( transLET !=0 && _simComp->getStopFlag())
    isFinish=false;

  while ( transLET!=0 && !_simComp->getStopFlag()){
#ifdef DEBUG_SIMULATE
      std::cout<<"come in cpu"<<std::endl;
#endif
#ifdef DEBUG_KERNEL
      std::cout << "kernel:simulate: scheduling decision: " <<  transLET->toString() << std::endl;
#endif
	commandLET=transLET->getCommand();


	if(transLET!=0 && transLET->getCommand()->getTask()->getIsDaemon()==true){
	  if(transLET->getStartTime() >= deviceLET->getSimulatedTime()){
	    if(_simComp->getNonDaemonTaskList().empty()){
	      isFinish = true;
	      break;
	      }
	    int cnt = 0;
        int cnt1 = 0;
	    for(TaskList::const_iterator i=_simComp->getNonDaemonTaskList().begin(); i != _simComp->getNonDaemonTaskList().end(); ++i){
	      //  std::cout<<"non dameon task"<<(*i)->toString()<<" state is "<<(*i)->getState()<<(*i)->getCurrCommand()->toString()<<std::endl;
	       cnt ++;
	       if((*i)->getState()==3){
		//	std::cout<<"not stop"<<std::endl;
            cnt1 ++;
	      }
	    }
	    if(cnt1>=cnt){
           isFinish = true;
           break;
	    }
	  }
	}
	else
	  isFinish=false;


#ifdef DEBUG_SIMULATE
	std::cout<<"device is "<<deviceLET->getName()<<std::endl;
#endif
        bool x = deviceLET->addTransaction(0);
#ifdef DEBUG_SIMULATE
	std::cout<<"in simulator end addTransactin"<<std::endl;
#endif
#ifdef DEBUG_KERNEL
      std::cout << "kernel:simulate: AFTER add trans: " << x << std::endl;
#endif

      if (x){
#ifdef DEBUG_KERNEL
	std::cout << "kernel:simulate: add transaction 0" << commandLET->toString() << std::endl;
#endif
	unsigned int nbOfChannels = commandLET->getNbOfChannels();
	for (unsigned int i=0;i<nbOfChannels; i++){
	  if ((depTask=commandLET->getDependentTask(i))==0) continue;
#ifdef DEBUG_KERNEL
	  std::cout << "kernel:simulate: dependent Task found" << std::endl;
#endif
	  if(depTask->getIsCPUExist())
	      depCPU=depTask->getCPU();
	  else
	      depCPU = 0;
	  if(depTask->getIsFPGAExist())
	      depFPGA=depTask->getFPGA();
	  else
	      depFPGA = 0;

	  if(depCPU){
#ifdef DEBUG_SIMULATE
	    std::cout<<"lets start cpu"<<std::endl;
#endif
	    if (depCPU!=deviceLET){
#ifdef DEBUG_KERNEL
	      std::cout << "kernel:simulate: Tasks running on different CPUs" << std::endl;
#endif
	      depCommand=depTask->getCurrCommand();
	      if (depCommand!=0 && channelImpactsCommand(commandLET->getChannel(i), depCommand)) { //RIGHT one

#ifdef DEBUG_KERNEL
		std::cout << "kernel:simulate: commands are accessing the same channel" << std::endl;
#endif
		depTransaction=depCommand->getCurrTransaction();
		if (depTransaction!=0 && depTransaction->getVirtualLength()!=0){
#ifdef DEBUG_KERNEL
		  std::cout << "kernel:simulate: dependent task has a current transaction and is not blocked any more" << std::endl;
#endif

		  depNextTrans=depCPU->getNextTransaction();
		  if (depNextTrans!=0){
#ifdef DEBUG_KERNEL
		    std::cout << "kernel:simulate: transaction scheduled on dependent CPU" << std::endl;
#endif
		    depNextCommand=depNextTrans->getCommand();
		    if (depNextCommand->getTask()!=depTask){
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
	  else{
#ifdef DEBUG_SIMULATE
	    std::cout<<"lets start fpga"<<std::endl;
#endif
	    if (depFPGA != 0 && depFPGA != deviceLET){
#ifdef DEBUG_KERNEL
	      std::cout << "kernel:simulate: Tasks running on different FPGAs" << std::endl;
#endif
	      depCommand=depTask->getCurrCommand();
	      if (depCommand!=0 && channelImpactsCommand(commandLET->getChannel(i), depCommand)) { //RIGHT one

#ifdef DEBUG_KERNEL
		std::cout << "kernel:simulate: commands are accessing the same channel" << std::endl;
#endif
		depTransaction=depCommand->getCurrTransaction();
		if (depTransaction!=0 && depTransaction->getVirtualLength()!=0){
#ifdef DEBUG_KERNEL
		  std::cout << "kernel:simulate: dependent task has a current transaction and is not blocked any more" << std::endl;
#endif

		  depNextTrans=depFPGA->getNextTransaction();
		  if (depNextTrans!=0){
#ifdef DEBUG_KERNEL
		    std::cout << "kernel:simulate: transaction scheduled on dependent CPU" << std::endl;
#endif
		    depNextCommand=depNextTrans->getCommand();
		    if (depNextCommand->getTask()!=depTask){
#ifdef DEBUG_KERNEL
		      std::cout << "kernel:simulate: dependent task not yet scheduled on dependent CPU" << std::endl;
#endif

		      depFPGA->truncateAndAddNextTransAt(transLET->getEndTime());
#ifdef DEBUG_KERNEL
		      std::cout << "kernel:simulate: dependent transaction truncated" << std::endl;
#endif
		    }
		  }else{
#ifdef DEBUG_KERNEL
		    std::cout << "kernel:simulate: schedule dependent CPU  " << depFPGA->toString() << std::endl;
#endif
		    depFPGA->schedule();
		  }
		}
	      }
	    }
	  }
	}

#ifdef DEBUG_KERNEL
	std::cout << "kernel:simulate: invoke schedule on executing CPU" << std::endl;
#endif
	deviceLET->schedule();
#ifdef LISTENERS_ENABLED
	NOTIFY_TIME_ADVANCES(transLET->getEndTime());
#endif
      }
      // for run-to-next-breakpoint-max-trans which executes until the next breakpoint or stops after max transactions have been executed
//      if (!isHanging) {
//          if (oLastTrans != NULL && oLastTrans != 0 && transLET != NULL) {
//              countMaxTrans += transLET->getEndTime() - oLastTrans->getEndTime();
//          } else if (transLET != NULL) {
//              countMaxTrans += transLET->getEndTime();
//          }
//
//          if (countMaxTrans >= (MAX_TRANS_TO_EXECUTED*1000)) {
//              std::string msgToSend = "Too many transactions are being executed, try to use run-to-next-breakpoint-max-trans instead!";
//              std::cout << msgToSend << std::endl;
//              std::ostringstream aMessage;
//              //send message to server
//              aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << msgToSend << TAG_MSGc << std::endl;
//              writeSimState(aMessage);
//              aMessage << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
//              _syncInfo->_server->sendReply(aMessage.str());
//              isHanging = true;
//          }
//      }
      oLastTrans=transLET;
#ifdef DEBUG_SIMULATE
      std::cout<<"task is !!!!!"<<oLastTrans->toString()<<std::endl;
#endif

      transLET=getTransLowestEndTime(deviceLET);
  }

  bool aSimCompleted = ( transLET==0  && !_simComp->getStoppedOnAction());
  if(isFinish==true)
    aSimCompleted = true;


  if (aSimCompleted){
#ifdef LISTENERS_ENABLED
    NOTIFY_SIM_STOPPED();
    NOTIFY_EVALUATE();
#endif
    _longRunTime = max(_longRunTime, SchedulableDevice::getSimulatedTime());
    _shortRunTime = min(_shortRunTime, SchedulableDevice::getSimulatedTime());
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
    "-signals ofile         generates signals from declared file \n"
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

std::vector<std::string> readFromFile(std::string& filename){
    std::string x;
    std::vector<std::string> parameters;
    std::ifstream inFile(filename.c_str());

//    inFile.open(filename);
    if (!inFile) {
        std::cout << "Unable to open file";
        exit(1); // terminate with error
    }

    while (inFile >> x) {
        parameters.push_back(x);
    }
    inFile.close();
    return parameters;
}

template < typename T > std::string to_string( const T& n )
    {
        std::ostringstream stm ;
        stm << n ;
        return stm.str() ;
    }
int countLineNumber(std::string& filename){
    int number_of_lines = 0;
    std::string line;
    std::ifstream myfile(filename.c_str());

    while (std::getline(myfile, line)){
        if (line != "") ++number_of_lines;
    }
    std::cout << "Number of lines in text file: " << number_of_lines << std::endl;
    return number_of_lines;
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
  aArgString=getArgs("-signals", "signals.txt", iLen, iArgs);
  if (!aArgString.empty()) {
    int lineNumber =  countLineNumber(aArgString);
    std::vector<std::string> parameters = readFromFile(aArgString);
    std::string aNewCmd;
    int previousTransTime = 0;
    if((parameters.size() != (lineNumber*4))){
        std::cout << "Error: Wrong format, each line should contains 4 parameters.\n";
    } else {
        if(lineNumber != 0){
            for (int i = 0; i < lineNumber; i++){
                std::string channelName =_simComp->getChannelList(parameters[i*4+1]);
                TMLChannel* t = _simComp->getChannelByName(channelName);
                if(t != 0){
                    aNewCmd += "1 5 " + parameters[i*4] + "; 6 " + to_string(t->getID()) + " 1 " + parameters[i*4+3] + "; ";
                }
                else {
                    std::cout << "Error: Wrong channel name\n";
                    previousTransTime++;
                }
            }
            if(previousTransTime != lineNumber){
                aNewCmd += "1 0; 7 1 test.html;  1 7 100 100 test";
            } else {
                aNewCmd = "1 0; 7 1 test.html;  1 7 100 100 test";
            }

            std::cout<<"DecodeCommand "<< aNewCmd << std::endl;
            std::ofstream aXmlOutFile1;
            std::string aXmlFileName1 = getArgs("-oxml", "reply.xml", iLen, iArgs);
            if (aXmlFileName1.empty()) aXmlOutFile1.open("/dev/null"); else aXmlOutFile1.open(aXmlFileName1.c_str());
            if (aXmlOutFile1.is_open()){
            std::string aNextCmd1;
            std::istringstream iss1(aNewCmd+";");
            getline(iss1, aNextCmd1, ';');
            while (!(iss1.eof() || aNextCmd1.empty())){
              std::cout << "next cmd to execute: \"" << aNextCmd1 << "\"\n";
              decodeCommand(aNextCmd1, aXmlOutFile1);
              getline(iss1, aNextCmd1, ';');
            }
            aXmlOutFile1.close();
          } else{
                std::cout << "XML output file could not be opened, aborting.\n";
            }
          } else {
             std::cout << "Signal file contains nothing, aborting.\n";
          }
    }
  }
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
  std::cout<<"decodeCommand"<<std::endl;
  //std::cout << "Not crashed. I: " << iCmd << std::endl;
  //std::cout << iCmd << std::endl;
  unsigned int aCmd = 0, aParam1 = 0, aParam2 = 0, anErrorCode = 0;
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
  TMLTransaction* oLastTrans = 0;
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
    //std::cout << "Not crashed. I: " << iCmd << " param= " << aParam1 << std::endl;

    switch (aParam1){
      //_end =oLastTrans->printEnd();
    case 0: {    //Run to next breakpoint
          std::cout << "Run to next breakpoint." << std::endl;
          aGlobMsg << TAG_MSGo << "Run to next breakpoint" << TAG_MSGc << std::endl;
          _simTerm=runToNextBreakpoint(oLastTrans);
          unsigned int tempDaemon = 0;
          bool checkTerminated = false;
          if (!_simComp->getNonDaemonTaskList().empty()) {
             for (TaskList::const_iterator i=_simComp->getNonDaemonTaskList().begin(); i != _simComp->getNonDaemonTaskList().end(); ++i) {
             //the simulation terminated when all tasks are terminated or suspended
                if((*i)->getState() == 3 || (*i)->getState() == 0){
                    tempDaemon ++;
                    if ((*i)->getState() == 3) {
                        checkTerminated = true;
                    }
                }
             }
          }
          if (tempDaemon < _simComp->getNonDaemonTaskList().size() && !checkTerminated) {
             _simTerm = false;
          }
          std::cout << "End Run to next breakpoint." << std::endl;
          if (oLastTrans != NULL)
              _end = oLastTrans->printEnd();
          else
              std::cout << "There is no more transactions left to execute." << std::endl;
          break;
      }
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
      _simTerm = runXTransactions(aParam2, oLastTrans);
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
      _simTerm = runXCommands(aParam2, oLastTrans);
      std::cout << "End Run x commands." << std::endl;
      break;
    case 5: //Run up to time x
      std::cout << "Run to time x." << std::endl;
      aInpStream >> aParam2;
      //_currCmdListener=new RunXTimeUnits(_simComp,aParam2);
      aGlobMsg << TAG_MSGo << "Created listener run to time " << aParam2 << TAG_MSGc << std::endl;
      _simTerm = runTillTimeX(aParam2, oLastTrans);
      std::cout << "End Run to time x." << std::endl;
      break;
    case 6:     //Run for x time units
      std::cout << "Run for x time units." << std::endl;
      aInpStream >> aParam2;
      //_currCmdListener=new RunXTimeUnits(_simComp,aParam2+SchedulableDevice::getSimulatedTime());
      aGlobMsg << TAG_MSGo  << "Created listener run " << aParam2 << " time units" << TAG_MSGc << std::endl;
      _simTerm = runXTimeUnits(aParam2, oLastTrans);
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
      std::cout << "Explore tree AUT file created" << std::endl;
      //aPath.str("");
      //std::ofstream myfile2 ("tree.txt");
      //if (myDOTfile.is_open() && myAUTfile.is_open()){
      if (myAUTfile.is_open()){
        //#ifdef DOT_GRAPH_ENABLED
        //myDOTfile << "digraph BCG {\nsize = \"7, 10.5\";\ncenter = TRUE;\nnode [shape = circle];\n0 [peripheries = 2];\n";
        //#endif
        std::cout << "Explore tree AUT file opened" << std::endl;
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
        std::cout << "Explore tree AUT file closed" << std::endl;
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
    case 15:{//Run until FPGA x executes
      std::cout << "Run until FPGA x executes." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getFPGAByName(aStrParam));
      SchedulableDevice* aFPGA=_simComp->getFPGAByName(aStrParam);
      if (aFPGA!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on FPGA " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToCPUTrans(aFPGA, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until FPGA x executes." << std::endl;
      break;
    }
    case 16:{ //Write x samples/events to channel y
      std::cout << "\nAdd virtual signals." << std::endl;
      aInpStream >> aStrParam;
      std::string channelName =_simComp->getChannelList(aStrParam);
      TMLChannel* aChannel = _simComp->getChannelByName(channelName);
      //aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
      //anErrorCode=1;
      if (aChannel==0){
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      } else {
        aInpStream >> aParam1;
        TMLEventChannel* anEventChannel = dynamic_cast<TMLEventChannel*>(aChannel);
        if (anEventChannel==0){
          aChannel->insertSamples(aParam1, 0);
        } else {
          //Parameter<ParamType> anInsertParam((dynamic_cast<TMLEventChannel*>(aChannel))->getParamNo());
          std::string str, tempParameter="";
          aInpStream >> str;
          while (1) {
              std::size_t const n = str.find_first_of("0123456789");
              if(n != std::string::npos){
                  std::size_t const m = str.find_first_not_of("0123456789", n);
                  tempParameter += str.substr(n, m != std::string::npos ? m-n : m) + " ";
                  if(m != std::string::npos)
                    str = str.substr(m, str.length());
                  else {
                    std::cout << "End of parameter." << std::endl;
                    break;
                  }

              }
              else {
                  std::cout << "There is no more number in the parameter." << std::endl;
                  break;
              }

          }
          std::istringstream ss(tempParameter);
          Parameter* anInsertParam = anEventChannel->buildParameter();
          ss >> anInsertParam;
          aChannel->insertSamples(aParam1, anInsertParam);
        }
        aGlobMsg << TAG_MSGo << "Write data/event to channel." << TAG_MSGc << std::endl;
      }
      std::cout << "End virtual signals." << std::endl;
      break;
    }
    case 17:{//Run until write operation on channel x is performed
      std::cout << "Run until write operation on channel x is performed." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getChannelByName(aStrParam));
      std::string channelName =_simComp->getChannelList(aStrParam);
      TMLChannel* aChannel = _simComp->getChannelByName(channelName);
      if (aChannel!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on Channel " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToChannelWriteTrans(aChannel, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until write operation on channel x is performed." << std::endl;
      break;
    }
    case 18:{//Run until read operation on channel x is performed
      std::cout << "Run until read operation on channel x is performed." << std::endl;
      aInpStream >> aStrParam;
      //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getChannelByName(aStrParam));
      std::string channelName =_simComp->getChannelList(aStrParam);
      TMLChannel* aChannel = _simComp->getChannelByName(channelName);
      if (aChannel!=0){
        //_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
        aGlobMsg << TAG_MSGo << "Created listener on Channel " << aStrParam << TAG_MSGc << std::endl;
        _simTerm=runToChannelReadTrans(aChannel, oLastTrans);
      }else{
        aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
        anErrorCode=2;
      }
      std::cout << "End Run until read operation on channel x is performed." << std::endl;
      break;
    }
    case 19: {    //Run to next breakpoint max trans
      std::cout << "Run to next breakpoint max trans." << std::endl;
      aGlobMsg << TAG_MSGo << "Run to next breakpoint max trans" << TAG_MSGc << std::endl;
      int tempParam = 0;
      aInpStream >> tempParam;
      std::cout << "tempParam before = " << tempParam << std::endl;
      if (tempParam <= 0) tempParam = MAX_TRANS_TO_EXECUTED;
      std::cout << "tempParam after = " << tempParam << std::endl;
      aGlobMsg << TAG_MSGo << "Created listener run " << tempParam << " transactions" << TAG_MSGc << std::endl;
      _simTerm = runXTransactions(tempParam, oLastTrans);
      std::cout << "End Run to next breakpoint max trans." << std::endl;
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
    case 6: {//FPGA
      TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getFPGAByName(aStrParam));
      if (aDevice!=0) {
        std::cout << "Print information about FPGA: " << _simComp->getFPGAByName(aStrParam) << std::endl;
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
    } else {
      aInpStream >> aParam1;
      TMLEventChannel* anEventChannel = dynamic_cast<TMLEventChannel*>(aChannel);
      if (anEventChannel==0){
        aChannel->insertSamples(aParam1, 0);
      } else {
        //Parameter<ParamType> anInsertParam((dynamic_cast<TMLEventChannel*>(aChannel))->getParamNo());
        Parameter* anInsertParam = anEventChannel->buildParameter();
        aInpStream >> anInsertParam;
        aChannel->insertSamples(aParam1, anInsertParam);
      }
      aGlobMsg << TAG_MSGo << "Write data/event to channel." << TAG_MSGc << std::endl;
    }
    std::cout << "End Write x samples/events to channel y." << std::endl;
    break;
  }
  case 7: { //Save trace in file x
    std::cout << "Save trace in file x." << std::endl;
    aInpStream >> aParam1;
    aInpStream >>aStrParam;
//    std::string aStrParamTask;
//    aInpStream >> aStrParamTask;
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
    case 3: //XML
      aGlobMsg << TAG_MSGo << "Schedule output in XML format" << TAG_MSGc << std::endl;
      schedule2XML(anEntityMsg,aStrParam);
    case 4: {//timeline diagram
      aGlobMsg << TAG_MSGo << "Schedule output in HTML format" << TAG_MSGc << std::endl;
      std::ostringstream timelineContent;
      timelineContent << "<![CDATA[";
      timeline2HTML(aStrParam, timelineContent);
      timelineContent << "]]>";
      aGlobMsg << TAG_MSGo << timelineContent.str() << TAG_MSGc << std::endl;
      break;
      }
    default:
      aGlobMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
      anErrorCode=3;
    }
    std::cout << "End Save trace in file x." << std::endl;
    break;
  }
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
  case 25: //Get list of all transactions belogn to a task
    aInpStream >> aStrParam;
    std::cout << "Get list of all transactions belong to " << aStrParam << std::endl;
    returnedNbOfTransactions = allTrans2XMLByTask(anEntityMsg, aStrParam);
    anEntityMsg << TAG_TRANSACTION_NBo << "nb=\"" << returnedNbOfTransactions << "\"" << TAG_TRANSACTION_NBc <<  std::endl;
    std::cout << "End list of all transactions belong to a task." << std::endl;
    break;
  case 26: //Emptying simulation transactions during simulation
    aInpStream >> aParam2;
    std::cout << "Remove list of " << aParam2 << " transactions per CPU or Bus." << std::endl;
    removeOldTransaction(aParam2);
    std::cout << "End remove list of transactions." << std::endl;
    break;
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

bool Simulator::runToFPGATrans(SchedulableDevice* iFPGA, TMLTransaction*& oLastTrans){
  //ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iFPGA);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iFPGA);
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

bool Simulator::runToChannelWriteTrans(TMLChannel* iChannel, TMLTransaction*& oLastTrans){
  //ListenerSubject<ChannelListener>* aSubject= static_cast<ListenerSubject<ChannelListener>* > (iChannel);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iChannel);
  RunTillWriteTransOnChannel aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToChannelReadTrans(TMLChannel* iChannel, TMLTransaction*& oLastTrans){
  //ListenerSubject<ChannelListener>* aSubject= static_cast<ListenerSubject<ChannelListener>* > (iChannel);
  ListenerSubject<GeneralListener>* aSubject= static_cast<ListenerSubject<GeneralListener>* > (iChannel);
  RunTillReadTransOnChannel aListener(_simComp, aSubject);
  return simulate(oLastTrans);
}

bool Simulator::runToNextRandomCommand(TMLTransaction*& oLastTrans){
  _randChoiceBreak.setEnabled(true);
  //_randChoiceBreak->setEnabled(true);
  //std::cout << "Before simulate" << std::endl;
  bool aSimTerminated=simulate(oLastTrans);
  //std::cout << "After simulate" << std::endl;
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
  std::cout<<"explore tree"<<std::endl;
  TMLTransaction* aLastTrans;
  //if (iDepth<RECUR_DEPTH){
  ID aLastID;
  bool aSimTerminated=false;
  IndeterminismSource* aRandomCmd;

  //std::cout << "Command coverage current:"<<  TMLCommand::getCmdCoverage() << " to reach:" << _commandCoverage << " nbOfBranchesExplored:"<< _nbOfBranchesExplored << " nbOfBranchesToExplore:" << _nbOfBranchesToExplore << " branch coverage:" <<_branchCoverage <<std::endl;

  do{
    std::cout << "simulation step" << std::endl;
    aSimTerminated=runToNextRandomCommand(aLastTrans);
    std::cout << "run to next done" << std::endl;
    aRandomCmd = _simComp->getCurrentRandomCmd();
    //std::cout << "Random command:" << aRandomCmd <<std::endl;
  }while (!aSimTerminated && aRandomCmd==0 && _simComp->wasKnownStateReached()==0);
#ifdef EXPLOGRAPH_ENABLED
  std::cout << "Explo graph AUT" << std::endl;
  aLastID = schedule2GraphAUT(iAUTFile, iPrevID,oTransCounter);
#endif
  if(aSimTerminated){
    std::cout << "simulation terminatd" << std::endl;
    oTransCounter++;
    //#ifdef DOT_GRAPH_ENABLED
    //#else
    //(21,"i(allCPUsTerminated)", 25)
    iAUTFile << "(" << aLastID << "," << "\"i(allCPUsFPGAsTerminated<" << SchedulableDevice::getSimulatedTime() << ">)\"," << TMLTransaction::getID() << ")\n";
    _nbOfBranchesExplored ++;
    //#endif
    TMLTransaction::incID();

    //if(_commandCoverage <= TMLCommand::getCmdCoverage() && _branchCoverage <= TMLCommand::getBranchCoverage()){
    //std::cout << "Command coverage current:"<<  TMLCommand::getCmdCoverage() << " to reach:" << _commandCoverage << " nbOfBranchesExplored:"<< _nbOfBranchesExplored << " nbOfBranchesToExplore:" << _nbOfBranchesToExplore<< std::endl;
    if (_commandCoverage <= TMLCommand::getCmdCoverage()) {
      if (_nbOfBranchesExplored > 0) {
        if (100 * _nbOfBranchesExplored / _nbOfBranchesToExplore >= _branchCoverage) {
          std::cout << "*********************************** 100% REACH" << std::endl;
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
          std::cout << "1. Exploring branch #" << aBranch << " from " << iPrevID << std::endl;
          _simComp->reset();
          aStreamBuffer.str(aStringBuffer);
          //std::cout << "Read 1 in exploreTree\n";
          _simComp->readObject(aStreamBuffer);
          aRandomCmd->setRandomValue(aBranch);
          exploreTree(iDepth+1, aLastID, iAUTFile, oTransCounter);
          if (_terminateExplore && aBranch<aNbNextCmds-1) {
            std::cout << "Terminate explore but still branches to execute ...\n";
          }
        }
      } else{
        unsigned int aBranch=0;
        aNbNextCmds ^= INT_MSB;
        //while (aNbNextCmds!=0 && !_syncInfo->_terminate){
        while (aNbNextCmds!=0 && !_terminateExplore){
          //while (aNbNextCmds!=0){
          std::cout << "2. Exploring branch #" << aNbNextCmds << " from " << iPrevID << std::endl;
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
  std::cout<<"explore dot"<<std::endl;
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
  std::cout<<"exe comd"<<std::endl;
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