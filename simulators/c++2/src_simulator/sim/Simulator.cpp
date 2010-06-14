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
#include <TMLChoiceCommand.h>
#include <Server.h>
#include <ServerLocal.h>
#include <TMLSelectCommand.h>
#include <EBRDD.h>
#include <EBRDDCommand.h>
#include <ERC.h>

Simulator::Simulator(SimServSyncInfo* iSyncInfo):_syncInfo(iSyncInfo), _simComp(_syncInfo->_simComponents), _busy(false), _simTerm(false), _leafsID(0), _randChoiceBreak(_syncInfo->_simComponents) {
}

Simulator::~Simulator(){
	//if (_currCmdListener!=0) delete _currCmdListener;
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
	for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
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
#ifdef DEBUG_KERNEL
		else std::cout << "kernel:getTLET: no transaction found on " << aTempDevice->toString() << std::endl;
#endif
	}
	//if (tmp==1) std::cout << "trans only on one CPU " << oResultDevice->toString() << "\n";
	return aMarker; 
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
		for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
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
	std::ofstream myfile (iTraceFileName.c_str());
	if (myfile.is_open()){
		for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
			(*i)->schedule2TXT(myfile);
		}
		myfile.close();
	}
	else
		std::cout << "Unable to open text output file." << std::endl;
	gettimeofday(&aEnd,NULL);
	std::cout << "The text output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << iTraceFileName << std::endl;
}

void Simulator::schedule2HTML(std::string& iTraceFileName) const{
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::ofstream myfile (iTraceFileName.c_str());
	if (myfile.is_open()){
		myfile << "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
		myfile << "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"schedstyle.css\" />\n<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\" />\n<title>Scheduling</title>\n</head>\n<body>\n";		
		for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
			(*i)->schedule2HTML(myfile);
		}
		for(BusList::const_iterator j=_simComp->getBusIterator(false); j != _simComp->getBusIterator(true); ++j){
			(*j)->schedule2HTML(myfile);
		}
		//for_each(iCPUlist.begin(), iCPUlist.end(),std::bind2nd(std::mem_fun(&CPU::schedule2HTML),myfile));
		myfile << "</body>\n</html>\n";
		myfile.close();
	}
	else
		std::cout << "Unable to open HTML output file." << std::endl;
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
	std::ofstream myfile (iTraceFileName.c_str());
	if (myfile.is_open()){
		std::cout << "File is open" << std::endl;
		SignalChangeQueue aQueue;
		std::string aSigString;
		bool aNoMoreTrans;
		TraceableDevice* actDevice;
		TMLTime aTime=0, aCurrTime=-1;
		SignalChangeData* aTopElement;
		TMLTime aNextClockEvent=0;
		myfile << "$date\n" << asctime(aTimeinfo) << "$end\n\n$version\nDaniels TML simulator\n$end\n\n";
		myfile << "$timescale\n1 ns\n$end\n\n$scope module Simulation $end\n";
		std::cout << "Before 1st loop" << std::endl;
		for (TraceableDeviceList::const_iterator i=_simComp->getVCDIterator(false); i!= _simComp->getVCDIterator(true); ++i){
			TraceableDevice* a=*i;
//			a->streamBenchmarks(std::cout);
//			a->toString();
			std::cout << "in 1st loop " << a << std::endl;
			std::cout << "device: " << (*i)->toString() << std::endl;
			myfile << "$var integer 3 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
			std::cout << "get next signal change" << std::endl;
			aTime = (*i)->getNextSignalChange(true, aSigString, aNoMoreTrans);
			std::cout << "push" << std::endl;
			aQueue.push(new SignalChangeData(aSigString, aTime, (aNoMoreTrans)?0:(*i)));
		}
		myfile << "$var integer 32 clk Clock $end\n";
		myfile << "$upscope $end\n$enddefinitions  $end\n\n";
		std::cout << "Before 2nd loop" << std::endl;
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
				myfile << VCD_PREFIX << vcdValConvert(aNextClockEvent) << " clk\n";
				aNextClockEvent+=CLOCK_INC; 
			}
			myfile << aTopElement->_sigChange << "\n";
			actDevice=aTopElement->_device;
			if (actDevice!=0) aTime = actDevice->getNextSignalChange(false, aSigString, aNoMoreTrans);
			delete aTopElement;
			aQueue.pop();
			if (actDevice!=0) aQueue.push(new SignalChangeData(aSigString, aTime, (aNoMoreTrans)?0:actDevice));
    		}
		myfile << "#" << aCurrTime+1 << "\n";
		std::cout << "Simulated cycles: " << aCurrTime << std::endl;
		for (TraceableDeviceList::const_iterator i=_simComp->getVCDIterator(false); i!= _simComp->getVCDIterator(true); ++i){
			myfile << VCD_PREFIX << "100 " << (*i)->toShortString() << "\n";
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
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
//#ifdef DEBUG_KERNEL
	std::cout << "kernel:simulate: first schedule" << std::endl;
//#endif
	_simComp->setStopFlag(false,"");
	//std::cout << "before loop " << std::endl;
	for(TaskList::const_iterator i=_simComp->getTaskIterator(false); i!=_simComp->getTaskIterator(true);i++){
		//std::cout << "loop it " << (*i)->toString() << std::endl;
		if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare(true);
		//std::cout << "loop it end" << (*i)->toString() << std::endl;
	}
	//std::cout << "after loop1" << std::endl;
	for(EBRDDList::const_iterator i=_simComp->getEBRDDIterator(false); i!=_simComp->getEBRDDIterator(true);i++){
		if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare();
	}
	//std::cout << "after loop2" << std::endl;
	for_each(_simComp->getCPUIterator(false), _simComp->getCPUIterator(true),std::mem_fun(&CPU::schedule));
	//std::cout << "after schedule" << std::endl;
	transLET=getTransLowestEndTime(cpuLET);
	//std::cout << "after getTLET" << std::endl;
#ifdef LISTENERS_ENABLED
	NOTIFY_SIM_STARTED();
#endif
	while (transLET!=0 && !_simComp->getStopFlag()){
#ifdef DEBUG_KERNEL
		std::cout << "kernel:simulate: scheduling decision: " <<  transLET->toString() << std::endl;
#endif
		commandLET=transLET->getCommand();
		//if (commandLET->getBreakpoint()) break;
#ifdef DEBUG_KERNEL
		std::cout << "kernel:simulate: add trans " << commandLET->toString() << std::endl;
#endif
		if (cpuLET->addTransaction()){
		 //cpuLET->schedule();
		 //if (commandLET->getTask()->getNoOfCPUs()==1) cpuLET->schedule();
		 unsigned int nbOfChannels = commandLET->getNbOfChannels();
		 bool aRescheduleCoresFlag=false;
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

		   //if (depCommand!=0 && channelImpactsCommand(commandLET->getChannel(i), depCommand)) {
		   //if (depCommand!=0 && (dynamic_cast<TMLSelectCommand*>(depCommand)!=0 || channelImpactsCommand(commandLET->getChannel(i), depCommand))){
#ifdef DEBUG_KERNEL
		    std::cout << "kernel:simulate: commands are accessing the same channel" << std::endl;
#endif
		    depTransaction=depCommand->getCurrTransaction();
		    if (depTransaction!=0 && depTransaction->getVirtualLength()!=0){
#ifdef DEBUG_KERNEL
		     std::cout << "kernel:simulate: dependent task has a current transaction and is not blocked any more" << std::endl;
#endif
		     if (depCPU==0){
			aRescheduleCoresFlag=true;
#ifdef DEBUG_KERNEL
			std::cout << "Multi Core scheduling procedure\n";
#endif 
			depTask->setRescheduleFlagForCores();
			continue;
		     }
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
			//if (depCPU->truncateAndAddNextTransAt(transLET->getEndTime())!=0){
#ifdef DEBUG_KERNEL
				depCPU->truncateAndAddNextTransAt(transLET->getEndTime());
				std::cout << "kernel:simulate: dependent transaction truncated" << std::endl;
#endif
				//depCPU->addTransaction();
			//}
#ifdef DEBUG_KERNEL
			std::cout << "kernel:simulate: schedule dependent CPU" << std::endl;
#endif
			//depCPU->schedule();
		      }
		     }else{
#ifdef DEBUG_KERNEL
			std::cout << "kernel:simulate: schedule dependent CPU" << std::endl;
#endif
			depCPU->schedule();
		     }
		    }
		   }
		  }
		 }
		 //reschedule CPUs if necessary
		//std::cout << "new reschedule position\n";
#ifdef DEBUG_KERNEL
		 std::cout << "kernel:simulate: invoke schedule on executing CPU" << std::endl;
#endif
		//if (aRescheduleCoresFlag) for_each(_simComp->getCPUIterator(false), _simComp->getCPUIterator(true), bind2nd(std::mem_fun(&CPU::truncateAndRescheduleIfNecessary), transLET->getEndTime()));
		if (aRescheduleCoresFlag){
			//std::cout << "new reschedule loop\n";
			for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
				if (*i!=cpuLET) (*i)->truncateIfNecessary(transLET->getEndTime());
			}
			for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
				if (*i!=cpuLET) (*i)->rescheduleIfNecessary();
			}
			//std::cout << "end new reschedule loop\n";
		}
		cpuLET->schedule();
#ifdef LISTENERS_ENABLED
                 NOTIFY_TIME_ADVANCES(transLET->getEndTime());
#endif
		}
//#ifdef DEBUG_KERNEL
		//else std::cout << "kernel:simulate: *** this should never happen ***" << std::endl;
//#endif
		oLastTrans=transLET;
		//std::cout << "kernel:simulate: getTransLowestEndTime" << std::endl;
		transLET=getTransLowestEndTime(cpuLET);
		//_syncInfo->_server->sendReply("Sleep once again\n");
		//sleep(1);
	}
#ifdef LISTENERS_ENABLED
	NOTIFY_SIM_STOPPED();
#endif
	gettimeofday(&aEnd,NULL);
	std::cout << "The simulation took " << getTimeDiff(aBegin,aEnd) << "usec.\n";
	return (transLET==0 && !_simComp->getStoppedOnAction());
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
	std::cout << "*****\nCommand line usage: run.x -ohtml myfile.htm -ovcd myfile.vcd -otxt myfile.txt\nParameters can be omitted if respective output is not needed; if file name is omitted default values will be applied.\nFor server mode: run.x -server\n*****\n";
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
	std::string aTraceFileName;
	std::cout << "Starting up...\n";
	aTraceFileName =getArgs("-server", "server", iLen, iArgs);
	if (!aTraceFileName.empty()) return new Server();
	aTraceFileName =getArgs("-file", "file", iLen, iArgs);
	if (!aTraceFileName.empty()) return new ServerLocal(aTraceFileName);
	//aTraceFileName =getArgs("-explore", "file", iLen, iArgs);
	//if (!aTraceFileName.empty()) return new ServerExplore();
	std::cout << "Running in command line mode.\n";
	aTraceFileName =getArgs("-help", "help", iLen, iArgs);
	if (aTraceFileName.empty()){
		TMLTransaction* oLastTrans;
		simulate(oLastTrans);
		aTraceFileName=getArgs("-ohtml", "scheduling.html", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2HTML(aTraceFileName);
		aTraceFileName=getArgs("-otxt", "scheduling.txt", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2TXT(aTraceFileName);
		aTraceFileName=getArgs("-ovcd", "scheduling.vcd", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2VCD(aTraceFileName);
		aTraceFileName=getArgs("-ograph", "scheduling.aut", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2Graph(aTraceFileName);
		_simComp->streamBenchmarks(std::cout);
		std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
	}
	else
		printHelp();
	return 0;
}

//void Simulator::decodeCommand(char* iCmd){
void Simulator::decodeCommand(std::string iCmd){
	//std::cout << "Not crashed. I: " << iCmd;
	//std::cout << iCmd << std::endl;
	unsigned int aCmd, aParam1, aParam2, anErrorCode=0;
	//std::string anIssuedCmd(iCmd);
	std::istringstream aInpStream(iCmd);
	//std::cout << "Not crashed. II\n";
	std::ostringstream aGlobMsg, anEntityMsg, anAckMsg;
	std::string aStrParam;
	//bool aSimTerminated=false;
	//std::cout << "Not crashed. III\n";
	_simComp->setStopFlag(false,"");
	//anEntityMsg.str("");
	aGlobMsg << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl /*<< TAG_REPLYo << anIssuedCmd << TAG_REPLYc << std::endl*/;
	aInpStream >> aCmd;
	switch (aCmd){
		case 0: //Quit simulation
			//std::cout << "QUIT SIMULATION EXECUTED "  << std::endl;
			break;
		case 1:{
			_busy=true;
			anAckMsg << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << /*TAG_REPLYo << anIssuedCmd << TAG_REPLYc << std::endl<< */ TAG_MSGo << "Command received" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_BUSY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			_syncInfo->_server->sendReply(anAckMsg.str());
			aInpStream >> aParam1;
			TMLTransaction* oLastTrans;
			switch (aParam1){
				case 0:	//Run to next breakpoint
					std::cout << "Run to next breakpoint." << std::endl;
					aGlobMsg << TAG_MSGo << "Run to next breakpoint" << TAG_MSGc << std::endl;
					_simTerm=runToNextBreakpoint(oLastTrans);
					std::cout << "End Run to next breakpoint." << std::endl;
					break;
				case 1:	//Run up to trans x
					std::cout << "Run to transaction x." << std::endl;
					aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
					anErrorCode=1;
					std::cout << "End Run to transaction x." << std::endl;
					break;
				case 2:	//Run x transactions
					std::cout << "Run x transactions." << std::endl;
					aInpStream >> aParam2;
					//_currCmdListener=new RunXTransactions(_simComp,aParam2);
					aGlobMsg << TAG_MSGo << "Created listener run " << aParam2 << " transactions" << TAG_MSGc << std::endl;
					_simTerm=runXTransactions(aParam2, oLastTrans);
					std::cout << "Run x transactions." << std::endl;
					break;
				case 3:	//Run up to command x
					std::cout << "Run to command x." << std::endl;
					aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
					anErrorCode=1;
					std::cout << "End Run to command x." << std::endl;
					break;
				case 4:	//Run x commands
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
				case 6:	//Run for x time units
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
					_leafsID=0;
					std::ofstream myfile ("tree");
					if (myfile.is_open()){
						exploreTree(0, 0, myfile);
						myfile.close();
					}
					aGlobMsg << TAG_MSGo  << "Tree was explored" << TAG_MSGc << std::endl;
					_simTerm=true;
					//aGlobMsg << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
					//anErrorCode=1;
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
					std::cout << "Run to next random choice command." << std::endl;
					_simTerm=runToNextChoiceCommand(oLastTrans);
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
		case 2:	//reset
			std::cout << "Simulator reset." << std::endl;
			_simComp->reset();
			_simTerm=false;
			aGlobMsg << TAG_MSGo << "Simulator reset" << TAG_MSGc << std::endl;
			std::cout << "End Simulator reset." << std::endl;
			break;
		case 3:{//Print variable x
			std::cout << "Print variable x." << std::endl;
			aInpStream >> aStrParam;
			if (aStrParam=="all"){
				for(TaskList::const_iterator i=_simComp->getTaskIterator(false); i !=_simComp->getTaskIterator(true); ++i){
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
			std::cout << "Print information about simulation element x." << std::endl;
			aInpStream >> aParam1;
			aInpStream >> aStrParam;
			anErrorCode=0;
			switch (aParam1){
				case 0: {//CPU
					TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getCPUByName(aStrParam));
					if (aDevice!=0) aDevice->streamStateXML(anEntityMsg); else anErrorCode=2;
					break;
				}
				case 1: {//Bus
					TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getBusByName(aStrParam));
					if (aDevice!=0) aDevice->streamStateXML(anEntityMsg); else anErrorCode=2;
					break;
				}
				case 2: //Mem
				case 3: //Bridge
					anErrorCode=1;
					break;
				case 4:{ //Channel
					TMLChannel* aDevice = _simComp->getChannelByName(aStrParam);
					if (aDevice!=0){
						std::cout << "get Channel info" << std::endl;
						aDevice->streamStateXML(anEntityMsg); 
					}else anErrorCode=2;
					break;
				}
				case 5: {//Task
					TraceableDevice* aDevice = dynamic_cast<TraceableDevice*>(_simComp->getTaskByName(aStrParam));
					if (aDevice!=0) aDevice->streamStateXML(anEntityMsg); else anErrorCode=2;
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
			std::cout << "End Print information about simulation element x." << std::endl;
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
				Parameter<ParamType> anInsertParam;
				if (dynamic_cast<TMLEventChannel*>(aChannel)==0){
					aChannel->insertSamples(aParam1, anInsertParam);
				}else{
					aInpStream >> anInsertParam;
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
				TMLChoiceCommand* aChoiceCmd=dynamic_cast<TMLChoiceCommand*>(aTask->getCommandByID(aParam1));
				if (aChoiceCmd!=0){
					aInpStream >> aParam2; 
					aChoiceCmd->setPreferredBranch(aParam2);
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
			TMLChoiceCommand* aCurrChCmd =_simComp->getCurrentChoiceCmd();
			if (aCurrChCmd==0){
					aGlobMsg << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
			}else{
					unsigned int aNbNextCmds;
					aCurrChCmd->getNextCommands(aNbNextCmds);
					TMLTask* aTask=aCurrChCmd->getTask();
					anEntityMsg << TAG_TASKo << " id=\"" << aTask-> getID() << "\" name=\"" << aTask->toString() << "\">" << TAG_CURRCMDo << " id=\"" << aCurrChCmd->getID() << "\">" << TAG_BRANCHo << aNbNextCmds << TAG_BRANCHc << "\">" << TAG_CURRCMDc << TAG_TASKc << std::endl;
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
		default:
			aGlobMsg << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
			anErrorCode=3;

	}
	aGlobMsg << TAG_ERRNOo << anErrorCode << TAG_ERRNOc << std::endl; 
	//if (aSimTerminated) aGlobMsg << SIM_TERM; else aGlobMsg << SIM_READY;
	writeSimState(aGlobMsg);
	aGlobMsg << std::endl << TAG_GLOBALc << std::endl << anEntityMsg.str() << TAG_STARTc << std::endl;
	//std::cout << "Before reply." << std::endl;
	_syncInfo->_server->sendReply(aGlobMsg.str());
	//std::cout << "End of command decode procedure." << std::endl;
	//std::cout << "Command: " << aCmd << "  Param1: " << aParam1 << "  Param2: " << aParam2 << std::endl;
}

void Simulator::printVariablesOfTask(TMLTask* iTask, std::ostream& ioMessage){
	if (iTask->getVariableIteratorID(false)==iTask->getVariableIteratorID(true)) return;
	ioMessage << TAG_TASKo << " id=\"" << iTask-> getID() << "\" name=\"" << iTask->toString() << "\">" << std::endl; 
	for(VariableLookUpTableID::const_iterator i=iTask->getVariableIteratorID(false); i !=iTask->getVariableIteratorID(true); ++i){
		ioMessage << TAG_VARo << " id=\"" << i->first << "\">" << *(i->second) << TAG_VARc << std::endl; 
	}
	ioMessage << TAG_TASKc << std::endl;
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
	ListenerSubject <TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iBus);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate(oLastTrans);
}

bool Simulator::runToCPUTrans(SchedulableDevice* iCPU, TMLTransaction*& oLastTrans){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iCPU);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate(oLastTrans);
}

bool Simulator::runToTaskTrans(TMLTask* iTask, TMLTransaction*& oLastTrans){
	ListenerSubject<TaskListener>* aSubject= static_cast<ListenerSubject<TaskListener>* > (iTask);
	RunTillTransOnTask aListener(_simComp, aSubject);
	return simulate(oLastTrans);
}

bool Simulator::runToSlaveTrans(Slave* iSlave, TMLTransaction*& oLastTrans){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iSlave);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate(oLastTrans);
}

bool Simulator::runToChannelTrans(TMLChannel* iChannel, TMLTransaction*& oLastTrans){
	ListenerSubject<ChannelListener>* aSubject= static_cast<ListenerSubject<ChannelListener>* > (iChannel);
	RunTillTransOnChannel aListener(_simComp, aSubject);
	return simulate(oLastTrans);
}

bool Simulator::runToNextChoiceCommand(TMLTransaction*& oLastTrans){
	_randChoiceBreak.setEnabled(true);
	bool aSimTerminated=simulate(oLastTrans);
	_randChoiceBreak.setEnabled(false);
	return aSimTerminated;
}

bool Simulator::runUntilCondition(std::string& iCond, TMLTask* iTask, TMLTransaction*& oLastTrans, bool& oSuccess){
	CondBreakpoint aListener(_simComp, iCond, iTask);
	oSuccess=aListener.conditionValid();
	//return simulate(oLastTrans);
	//aListener.commandEntered(0);
	if (oSuccess) return simulate(oLastTrans); else return false;
}

void Simulator::exploreTree(unsigned int iDepth, ID iPrevID, std::ofstream& iFile){
/*std::ofstream myfile (iTraceFileName.c_str());
	if (myfile.is_open()){
		for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
			(*i)->schedule2TXT(myfile);
		}
		myfile.close();*/
	TMLTransaction* aLastTrans;
	if (iDepth<RECUR_DEPTH){
		ID aMyID= ++_leafsID;
		bool aSimTerminated=false;
		TMLChoiceCommand* aChoiceCmd;
		do{
			aSimTerminated=runToNextBreakpoint(aLastTrans);
			aChoiceCmd=_simComp->getCurrentChoiceCmd();
		}while (!aSimTerminated && aChoiceCmd==0);
		//aStreamBuffer << "sched" << iDepth << "." << leafsForLevel[iDepth]++;
		//aStreamBuffer << "edge_" << iPrevID << "_" << aMyID;
		//std::string aStringBuffer(aStreamBuffer.str());
		//schedule2TXT(aStringBuffer);
		iFile << "***** edge_" << iPrevID << "_" << aMyID << " *****\n";
		for(CPUList::const_iterator i=_simComp->getCPUIterator(false); i != _simComp->getCPUIterator(true); ++i){
			(*i)->schedule2TXT(iFile);
		}
		iFile << "\n";
		//aStreamBuffer.str(""); 
		//if (!aSimTerminated){
		if(aChoiceCmd!=0){
			unsigned int aNbNextCmds;
			std::stringstream aStreamBuffer;
			std::string aStringBuffer;
			aChoiceCmd->getNextCommands(aNbNextCmds);
			std::cout << "Simulation " << iPrevID << "_" << aMyID << "continued " << aNbNextCmds << std::endl;
			_simComp->writeObject(aStreamBuffer);
			aStringBuffer=aStreamBuffer.str();
			for (unsigned int aBranch=0; aBranch<aNbNextCmds; aBranch++){
				_simComp->reset();
				aStreamBuffer.str(aStringBuffer);
				_simComp->readObject(aStreamBuffer);
				aChoiceCmd->setPreferredBranch(aBranch);
				exploreTree(iDepth+1, aMyID, iFile);
				//_simComp->reset();
				//_simComp->readObject(aBuffer);
			}
		}else
			std::cout << "Simulation " << iPrevID << "_" << aMyID << "terminated" << std::endl;
	}
}

//bool Simulator::execAsyncCmd(const char* iCmd){
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
			aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_TIMEo << SchedulableDevice::getSimulatedTime() << TAG_TIMEc << std::endl << TAG_MSGo << "Simulation time" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl;
			//if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
			writeSimState(aMessage);
			aMessage << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			_syncInfo->_server->sendReply(aMessage.str());
			break;
		case 14:{//get actual command, thread safeness, be careful!
			aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl;
			aInpStream >> aStrParam;
			if (aStrParam=="all"){
				for(TaskList::const_iterator i=_simComp->getTaskIterator(false); i !=_simComp->getTaskIterator(true); ++i){
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
			ioMessage << TAG_FINISHTIMEo << "-1" << TAG_FINISHTIMEc;
			ioMessage << TAG_FINISHTIMETRANSo << "-1" << TAG_FINISHTIMETRANSc;
			ioMessage << TAG_STARTTIMETRANSo << "-1" << TAG_STARTTIMETRANSc;
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
