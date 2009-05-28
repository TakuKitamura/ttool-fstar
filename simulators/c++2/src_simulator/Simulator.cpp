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

Simulator::Simulator(SimServSyncInfo* iSyncInfo):_syncInfo(iSyncInfo), _simComp(iSyncInfo->_simComponents), _busy(false), _leafsID(0) {}

Simulator::~Simulator(){
	//if (_currCmdListener!=0) delete _currCmdListener;
}

TMLTransaction* Simulator::getTransLowestEndTime(SchedulableDevice*& oResultDevice) const{
	TMLTransaction *aMarker=0, *aTempTrans;
	TMLTime aLowestTime=-1;
	SchedulableDevice* aTempDevice;
	//static unsigned int aTransitionNo=0;
#ifdef DEBUG_KERNEL
	std::cout << "kernel:getTLET: before loop" << std::endl;
#endif
	//for(SchedulingList::const_iterator i=_simComp->_cpuList.begin(); i != _simComp->_cpuList.end(); ++i){
	for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
		aTempDevice=*i;
		aTempTrans=aTempDevice->getNextTransaction();	
		if (aTempTrans!=0 && aTempTrans->getVirtualLength()>0){	
#ifdef DEBUG_KERNEL
			std::cout << "kernel:getTLET: transaction found on " << aTempDevice->toString() << ": " << aTempTrans->toString() << std::endl;
#endif
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
	return aMarker; 
}

void Simulator::schedule2Graph() const{/*
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::string aFilename(getArgs("-ogra", "scheduling.aut"));
	if (aFilename.empty()) return;
	std::ofstream myfile (aFilename.c_str());
	if (myfile.is_open()){
 		CPUList::iterator i;
		GraphTransactionQueue aQueue;
		TMLTransaction* aTrans, *aTopElement;
		unsigned int aTransitionNo=0;
		for (i=_simComp->_cpuList.begin(); i!= _simComp->_cpuList.end(); ++i){
			aTrans = (*i)->getTransactions1By1(true);
			if (aTrans!=0) aQueue.push(aTrans);
		}
		std::ostringstream aOutp;
		while (!aQueue.empty()){
			//std::ostringstream aTempStr;
			CPU* aCPU;
			aTopElement = aQueue.top();
			aCPU = aTopElement->getCommand()->getTask()->getCPU();
			for (unsigned int a=0; a < aTopElement->getVirtualLength(); a++){
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
	std::cout << "The Graph output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << aFilename << std::endl;
*/
}

void Simulator::schedule2TXT(std::string& iTraceFileName) const{
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::ofstream myfile (iTraceFileName.c_str());
	if (myfile.is_open()){
		for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
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
		for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
			(*i)->schedule2HTML(myfile);
		}
		for(BusList::const_iterator j=_simComp->getBusList().begin(); j != _simComp->getBusList().end(); ++j){
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
		TMLTime aTime, aCurrTime=-1;
		SignalChangeData* aTopElement;
		unsigned int aNextClockEvent=0;
		myfile << "$date\n" << asctime(aTimeinfo) << "$end\n\n$version\nDaniels TML simulator\n$end\n\n";
		myfile << "$timescale\n1 ns\n$end\n\n$scope module Simulation $end\n";
		std::cout << "Before 1st loop" << std::endl;
		for (TraceableDeviceList::const_iterator i=_simComp->getVCDList().begin(); i!= _simComp->getVCDList().end(); ++i){
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
		for (TraceableDeviceList::const_iterator i=_simComp->getVCDList().begin(); i!= _simComp->getVCDList().end(); ++i){
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

bool Simulator::simulate(){
	TMLTransaction* depTransaction,*depCPUnextTrans,*transLET;
	TMLCommand* commandLET,*depCommand,*depCPUnextCommand;
	TMLTask* depTask;
	SchedulableDevice* cpuLET;
	CPU* depCPU;
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
#ifdef DEBUG_KERNEL
	std::cout << "kernel:simulate: first schedule" << std::endl;
#endif
	for(TaskList::const_iterator i=_simComp->getTaskList().begin(); i!=_simComp->getTaskList().end();i++){
		//std::cout << (*i)->toString() << " in loop" << std::endl;
		if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare(true);
	}
	//std::cout << "after loop" << std::endl;
	for_each(_simComp->getCPUList().begin(), _simComp->getCPUList().end(),std::mem_fun(&SchedulableDevice::schedule));
	//std::cout << "after schedule" << std::endl;
	transLET=getTransLowestEndTime(cpuLET);
	//std::cout << "after getTLET" << std::endl;
	_simComp->setStopFlag(false);
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
#ifdef DEBUG_KERNEL
		 std::cout << "kernel:simulate: invoke schedule on executing CPU" << std::endl;
#endif
		 cpuLET->schedule();
		 depTask=commandLET->getDependentTask();
		 if (depTask!=0){
#ifdef DEBUG_KERNEL
		  std::cout << "kernel:simulate: dependent Task found" << std::endl;
#endif
		  depCPU=depTask->getCPU();			
		  if (depCPU!=cpuLET){
#ifdef DEBUG_KERNEL
		   std::cout << "kernel:simulate: Tasks running on different CPUs" << std::endl;
#endif
		   depCommand=depTask->getCurrCommand();
		   if (depCommand!=0 && (depCommand->getChannel()==commandLET->getChannel() || depCommand->channelUnknown())){
#ifdef DEBUG_KERNEL
		    std::cout << "kernel:simulate: commands are accessing the same channel" << std::endl;
#endif
		    depTransaction=depCommand->getCurrTransaction();
		    if (depTransaction!=0 && depTransaction->getVirtualLength()!=0){
#ifdef DEBUG_KERNEL
		     std::cout << "kernel:simulate: dependent task has a current transaction and is not blocked any more" << std::endl;
#endif
		     depCPUnextTrans=depCPU->getNextTransaction();
		      if (depCPUnextTrans!=0){
#ifdef DEBUG_KERNEL
		      std::cout << "kernel:simulate: transaction scheduled on dependent CPU" << std::endl;
#endif
		       depCPUnextCommand=depCPUnextTrans->getCommand();
		       if (depCPUnextCommand->getTask()!=depTask){
#ifdef DEBUG_KERNEL
 			std::cout << "kernel:simulate: dependent task not yet scheduled on dependent CPU" << std::endl;
#endif
			if (depCPU->truncateNextTransAt(transLET->getEndTime())!=0){
#ifdef DEBUG_KERNEL
				std::cout << "kernel:simulate: dependent transaction truncated" << std::endl;
#endif
				depCPU->addTransaction();
			}
#ifdef DEBUG_KERNEL
			std::cout << "kernel:simulate: schedule dependent CPU" << std::endl;
#endif
			depCPU->schedule();
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
		}
//#ifdef DEBUG_KERNEL
//		else std::cout << "kernel:simulate: *** this should never happen ***" << std::endl;
//#endif
		transLET=getTransLowestEndTime(cpuLET);
		//_syncInfo->_server->sendReply("Sleep once again\n");
		//sleep(1);
	}
	gettimeofday(&aEnd,NULL);
	std::cout << "The simulation took " << getTimeDiff(aBegin,aEnd) << "usec.\n";
	//_lastSimTrans=transLET;
	return (transLET==0);
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
	std::cout << "Running in server mode.\n";
	while (!_syncInfo->_terminate){
		pthread_mutex_lock (&_syncInfo->_mutexConsume);
		//_syncInfo->_terminate = decodeCommand(_syncInfo->_command);
		_busy=true;
		decodeCommand(_syncInfo->_command);
		_busy=false;
		pthread_mutex_unlock (&_syncInfo->_mutexProduce);
	}
	std::cout << "Simulator loop terminated." << std::endl;
}

ServerIF* Simulator::run(int iLen, char ** iArgs){
	std::string aTraceFileName;
	aTraceFileName =getArgs("-server", "server", iLen, iArgs);
	if (!aTraceFileName.empty()) return new Server();
	aTraceFileName =getArgs("-file", "file", iLen, iArgs);
	if (!aTraceFileName.empty()) return new ServerLocal(aTraceFileName);
	//aTraceFileName =getArgs("-explore", "file", iLen, iArgs);
	//if (!aTraceFileName.empty()) return new ServerExplore();
	std::cout << "Running in command line mode.\n";
	aTraceFileName =getArgs("-help", "help", iLen, iArgs);
	if (aTraceFileName.empty()){
		simulate();
		aTraceFileName=getArgs("-ohtml", "scheduling.html", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2HTML(aTraceFileName);
		aTraceFileName=getArgs("-otxt", "scheduling.txt", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2TXT(aTraceFileName);
		aTraceFileName=getArgs("-ovcd", "scheduling.vcd", iLen, iArgs);
		if (!aTraceFileName.empty()) schedule2VCD(aTraceFileName);
		//_traceFileName=getArgs("-ograph", "scheduling.vcd", iLen, iArgs);
		//if (!aFilename.empty()) schedule2Graph();
		_simComp->streamBenchmarks(std::cout);
		std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
	}
	else
		printHelp();
	return 0;
}

void Simulator::decodeCommand(char* iCmd){
	unsigned int aCmd, aParam1, aParam2, anErrorCode=0;
	std::istringstream aInpStream(iCmd);
	std::ostringstream aMessage;
	std::string aStrParam;
	aInpStream >> aCmd;
	//if (_currCmdListener!=0){
		//std::cout << "Before del listener.\n";
		 //delete _currCmdListener;
		//_currCmdListener=0;
		//std::cout << "After del listener.\n";
	//}
	_simComp->setStopFlag(false);
	aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Command received" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_BUSY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
	_syncInfo->_server->sendReply(aMessage.str());
	aMessage.str("");
	aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl;
	switch (aCmd){
		case 0: //Quit simulation
			//std::cout << "QUIT SIMULATION EXECUTED "  << std::endl;
			break;
		case 1:
			aInpStream >> aParam1;
			switch (aParam1){
				case 0:	//Run to next breakpoint
					aMessage << TAG_MSGo << "Run to next breakpoint" << TAG_MSGc << std::endl;
					runToNextBreakpoint();
					break;
				case 1:	//Run up to trans x
					aMessage << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
					anErrorCode=1;
					break;
				case 2:	//Run x transactions
					aInpStream >> aParam2;
					//_currCmdListener=new RunXTransactions(_simComp,aParam2);
					aMessage << TAG_MSGo << "Created listener run " << aParam2 << " transactions" << TAG_MSGc << std::endl;
					runXTransactions(aParam2);
					break;
				case 3:	//Run up to command x
					aMessage << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
					anErrorCode=1;
					break;
				case 4:	//Run x commands
					aInpStream >> aParam2;
					//_currCmdListener=new RunXCommands(_simComp,aParam2);
					runXCommands(aParam2);
					aMessage << TAG_MSGo << "Created listener run " << aParam2 << " commands" << TAG_MSGc << std::endl; 
					break;
				case 5: //Run up to time x
					aInpStream >> aParam2;
					//_currCmdListener=new RunXTimeUnits(_simComp,aParam2);
					aMessage << TAG_MSGo << "Created listener run to time " << aParam2 << TAG_MSGc << std::endl;
					runTillTimeX(aParam2);
					break;
				case 6:	//Run for x time units
					 aInpStream >> aParam2;
					runXTimeUnits(aParam2);
					//_currCmdListener=new RunXTimeUnits(_simComp,aParam2+SchedulableDevice::getSimulatedTime());
					aMessage << TAG_MSGo  << "Created listener run " << aParam2 << " time units" << TAG_MSGc << std::endl; 
					break;
				case 7: //Run up to next choice/select event
					//for (int i=0; i<RECUR_DEPTH; i++) leafsForLevel[i]=0;
					_leafsID=0;
					exploreTree(0,0);
					aMessage << TAG_MSGo  << "Tree was explored" << TAG_MSGc << std::endl;
					//aMessage << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
					//anErrorCode=1;
					break;
				case 8:{//Run up to next transfer on bus x
					aInpStream >> aStrParam;
					//ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getBusByName(aStrParam));
					SchedulableCommDevice* aBus=_simComp->getBusByName(aStrParam);
					if (aBus!=0){
						//_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						aMessage << TAG_MSGo << "Created listener on Bus " << aStrParam << TAG_MSGc << std::endl;
						runToBusTrans(aBus);
					}else{
						aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
						anErrorCode=2;
					}
				} 
					break;
				case 9:{//Run until CPU x executes
					aInpStream >> aStrParam;
					//ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getCPUByName(aStrParam));
					SchedulableDevice* aCPU=_simComp->getCPUByName(aStrParam);
					if (aCPU!=0){
						//_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						aMessage << TAG_MSGo << "Created listener on CPU " << aStrParam << TAG_MSGc << std::endl;
						runToCPUTrans(aCPU);
					}else{
						aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
						anErrorCode=2;
					}
					break;
				} 
				case 10:{//Run until Task x executes
					aInpStream >> aStrParam;
					//ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getTaskByName(aStrParam));
					TMLTask* aTask=_simComp->getTaskByName(aStrParam);
					if (aTask!=0){
						aMessage << TAG_MSGo << "Created listener on Task " << aStrParam << TAG_MSGc << std::endl;
						runToTaskTrans(aTask);
						//_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						
					}else{
						aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
						anErrorCode=2;
					}
					break;
				} 
				case 11:{//Run until Mem x is accessed
					aInpStream >> aStrParam;
					//ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getSlaveByName(aStrParam));
					Slave* aSlave=_simComp->getSlaveByName(aStrParam);
					if (aSlave!=0){
						//_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						aMessage << TAG_MSGo << "Created listener on Slave " << aStrParam << TAG_MSGc << std::endl;
						runToSlaveTrans(aSlave);
					}else{
						aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
						anErrorCode=2;
					}
					break;
				} 
				case 12:{//Run until operation on channel x is performed
					aInpStream >> aStrParam;
					//ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getChannelByName(aStrParam));
					TMLChannel* aChannel=_simComp->getChannelByName(aStrParam);
					if (aChannel!=0){
						//_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						aMessage << TAG_MSGo << "Created listener on Channel " << aStrParam << TAG_MSGc << std::endl;
						runToChannelTrans(aChannel);
					}else{
						aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
						anErrorCode=2;
					}
					break;
				} 
				default:
					aMessage << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
					anErrorCode=3;
			}
			//std::cout << "Before sim\n";
			if (anErrorCode==0){
				//simulate();
				std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
			}
			break;
		case 2:	//reset
			_simComp->reset();
			aMessage << TAG_MSGo << "Simulator reset" << TAG_MSGc << std::endl;
			break;
		case 3:{//Print variable x
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				//std::cout << "Task " << aStrParam << " exists" << std::endl;
				aInpStream >> aStrParam;
				//std::cout << "Check if Var *" << aStrParam << "* exists" << std::endl;
				//std::cout << "Len: " << aStrParam.length() << std::endl;
				ParamType* aParam=aTask->getVariableByName(aStrParam);
				if (aParam!=0){
					aMessage << TAG_MSGo << "Variable " << aStrParam << " exists" << TAG_MSGo << std::endl;
					aMessage << TAG_TASKo << " id=\"" << aTask-> getID() << "\">" << TAG_VARo << " name=\"" << aStrParam << "\">" << *aParam << TAG_VARc << TAG_TASKc << std::endl;
				}else{
					aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
				}
			}
			break;
		}
		case 4:	//Print information about simulation element x
			aMessage << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
			anErrorCode=1;
			break;
		case 5:{//Set variable x to value y
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				aInpStream >> aStrParam;
				ParamType* aParam=aTask->getVariableByName(aStrParam);
				if (aParam!=0){
					aInpStream >> *aParam;
					aMessage << TAG_MSGo << "Set variable " << aStrParam << " to " << *aParam << TAG_MSGc << std::endl;
				}else{
					aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
				}
			}
			break;
		}
		case 6: //Write x samples/events to channel y
			aMessage << TAG_MSGo << MSG_CMDNIMPL << TAG_MSGc << std::endl;
			anErrorCode=1;
			break;
		case 7: //Save trace in file x
			aInpStream >> aParam1;
			aInpStream >>aStrParam;
			switch (aParam1){
				case 0: //VCD
					aMessage << TAG_MSGo << "Schedule output in VCD format" << TAG_MSGc << std::endl;
					schedule2VCD(aStrParam);
					break;
				case 1: //HTML
					aMessage << TAG_MSGo << "Schedule output in HTML format" << TAG_MSGc << std::endl;
					schedule2HTML(aStrParam);
					break;
				case 2: //TXT
					aMessage << TAG_MSGo << "Schedule output in TXT format" << TAG_MSGc << std::endl;
					schedule2TXT(aStrParam);
					break;
				default:
					aMessage << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
					anErrorCode=3;
			}
			break;
		case 8:{ //Save simulation state in file x
			aInpStream >> aStrParam;
			std::ofstream aFile (aStrParam.c_str());
			if (aFile.is_open()){
				_simComp->writeObject(aFile);
				aMessage << TAG_MSGo << "Simulation state saved in file " << aStrParam << TAG_MSGc << std::endl;
			}else{
				aMessage << TAG_MSGo << MSG_FILEERR << aStrParam << TAG_MSGc << std::endl;
				anErrorCode=4;
			}
			break;
		}
		case 9:{//Restore simulation state from file x
			aInpStream >> aStrParam;
			std::ifstream aFile(aStrParam.c_str());
			if (aFile.is_open()){
				_simComp->readObject(aFile);
				aMessage << TAG_MSGo << "Simulation state restored from file " << aStrParam << TAG_MSGc << std::endl;
			}else{
				aMessage << TAG_MSGo << MSG_FILEERR << aStrParam << TAG_MSGc << std::endl;
				anErrorCode=4;
			}
			break;
		}
		case 10:{ //Save benchmarks in file x
			aInpStream >> aParam1;
			switch (aParam1){
			case 0: _simComp->streamBenchmarks(std::cout);
				aMessage << TAG_MSGo << "Benchmarks written to screen " << TAG_MSGc << std::endl;
				break;
			case 1:{
				aInpStream >> aStrParam;
				std::ofstream aFile (aStrParam.c_str());
				if (aFile.is_open()){
					_simComp->streamBenchmarks(aFile);
					aMessage << TAG_MSGo << "Benchmarks written to file " << aStrParam << TAG_MSGc << std::endl;
				}else{
					aMessage << TAG_MSGo << MSG_FILEERR << aStrParam << TAG_MSGc << std::endl;
					anErrorCode=4;
				}
				break;
			}
			default:
				aMessage << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
				anErrorCode=3;
			}
			break;
		}
		case 11:{//Set breakpoint in task x, command y
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				aInpStream >> aParam2;
				TMLCommand* aCommand=aTask->getCommandByID(aParam2);
				if (aCommand!=0){
					aCommand->setBreakpoint(new Breakpoint(_simComp));
					aMessage << TAG_MSGo << "Breakpoint was created" << TAG_MSGc << std::endl;
				}else{
					aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
				}
			}else{
				aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
				anErrorCode=2;
			}
		}	break;
		case 12:{//Choose branch
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				aInpStream >> aParam1;
				TMLChoiceCommand* aChoiceCmd=dynamic_cast<TMLChoiceCommand*>(aTask->getCommandByID(aParam1));
				if (aChoiceCmd!=0){
					aInpStream >> aParam2; 
					aChoiceCmd->setPreferredBranch(aParam2);
					aMessage << TAG_MSGo << "Preferred branch was set" << TAG_MSGc << std::endl;
				}else{
					aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
				}
			}else{
				aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
				anErrorCode=2;
			}
			break;
		}
		case 16:{//Delete breakpoint in task x, command y
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				aInpStream >> aParam2;
				TMLCommand* aCommand=aTask->getCommandByID(aParam2);
				if (aCommand!=0){
					aCommand->removeBreakpoint();
					aMessage << TAG_MSGo << "Breakpoint was removed" << TAG_MSGc << std::endl;
				}else{
					aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
				}
			}else{
				aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
				anErrorCode=2;
			}
			break;
		}
		case 17:{
			TMLChoiceCommand* aCurrChCmd =_simComp->getCurrentChoiceCmd();
			if (aCurrChCmd==0){
					aMessage << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << std::endl;
					anErrorCode=2;
			}else{
					TMLTask* aTask=aCurrChCmd->getTask();
					aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_TASKo << " id=\"" << aTask->getID() << "\">" << std::endl << TAG_CURRCMDo;
					aMessage << aCurrChCmd->getID() << TAG_BRANCHESo << aCurrChCmd->getNumberOfBranches() << TAG_BRANCHESc << std::endl;
					aMessage << TAG_CURRCMDc << TAG_TASKc << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Current choice command" << TAG_MSGc << TAG_ERRNOo << 0;
					aMessage << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_READY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			}
			break;
		}
		default:
			aMessage << TAG_MSGo << MSG_CMDNFOUND<< TAG_MSGc << std::endl;
			anErrorCode=3;

	}
	aMessage << TAG_ERRNOo << anErrorCode << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_READY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
	_syncInfo->_server->sendReply(aMessage.str());
	//std::cout << "Command: " << aCmd << "  Param1: " << aParam1 << "  Param2: " << aParam2 << std::endl;
}

bool Simulator::runToNextBreakpoint(){
	return simulate();
}

bool Simulator::runXTransactions(unsigned int iTrans){
	RunXTransactions aListener(_simComp, iTrans);
	return simulate();
}

bool Simulator::runXCommands(unsigned int iCmds){
	RunXCommands aListener(_simComp,iCmds);
	return simulate();
}

bool Simulator::runTillTimeX(unsigned int iTime){
	RunXTimeUnits aListener(_simComp,iTime);
	return simulate();
}

bool Simulator::runXTimeUnits(unsigned int iTime){
	RunXTimeUnits aListener(_simComp,iTime+SchedulableDevice::getSimulatedTime());
	return simulate();
}

bool Simulator::runToBusTrans(SchedulableCommDevice* iBus){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iBus);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate();	
}

bool Simulator::runToCPUTrans(SchedulableDevice* iCPU){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iCPU);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate();
}

bool Simulator::runToTaskTrans(TMLTask* iTask){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iTask);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate();
}

bool Simulator::runToSlaveTrans(Slave* iSlave){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iSlave);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate();
}

bool Simulator::runToChannelTrans(TMLChannel* iChannel){
	ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (iChannel);
	RunTillTransOnDevice aListener(_simComp, aSubject);
	return simulate();
}

void Simulator::exploreTree(unsigned int iDepth, unsigned int iPrevID){
	//std::ostringstream aFileName;
	//aFileName << "canc" << iDepth << "." << leafsForLevel[iDepth]++;
	//std::string aFileStr(aFileName.str());
	//schedule2TXT(aFileStr);
	if (iDepth<RECUR_DEPTH){
		unsigned int aMyID= ++_leafsID;
		bool aSimTerminated=false;
		TMLChoiceCommand* aChoiceCmd;
		do{
			aSimTerminated=runToNextBreakpoint();
			aChoiceCmd=_simComp->getCurrentChoiceCmd();
		}while (!aSimTerminated && aChoiceCmd==0);
		//std::ostringstream aFileName;
		std::stringstream aStreamBuffer;
		//aStreamBuffer << "sched" << iDepth << "." << leafsForLevel[iDepth]++;
		aStreamBuffer << "edge_" << iPrevID << "_" << aMyID;
		std::string aStringBuffer(aStreamBuffer.str());
		schedule2TXT(aStringBuffer);
		aStreamBuffer.str(""); 
		//if (!aSimTerminated){
		if(aChoiceCmd!=0){
			std::cout << "Simulation " << iPrevID << "_" << aMyID << "continued " << aChoiceCmd->getNumberOfBranches() << std::endl;
			_simComp->writeObject(aStreamBuffer);
			aStringBuffer=aStreamBuffer.str();
			for (unsigned int aBranch=0;aBranch<aChoiceCmd->getNumberOfBranches();aBranch++){
				_simComp->reset();
				aStreamBuffer.str(aStringBuffer);
				_simComp->readObject(aStreamBuffer);
				aChoiceCmd->setPreferredBranch(aBranch);
				exploreTree(iDepth+1,aMyID);
				//_simComp->reset();
				//_simComp->readObject(aBuffer);
			}
		}else
			std::cout << "Simulation " << iPrevID << "_" << aMyID << "terminated" << std::endl;
	}
}

bool Simulator::execAsyncCmd(const char* iCmd){
	unsigned int aCmd;
	std::istringstream aInpStream(iCmd);
	std::string aStrParam;
	aInpStream >> aCmd;
	std::ostringstream aMessage;
	switch (aCmd){
		case 0: //Quit simulation
			aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulator terminated" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_BUSY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			_syncInfo->_server->sendReply(aMessage.str());
			_simComp->setStopFlag(true);
			_syncInfo->_terminate=true;
			return false;
		case 13://get current time
			aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_TIMEo << SchedulableDevice::getSimulatedTime() << TAG_TIMEc << std::endl << TAG_MSGo << "Simulation time" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo;
			if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
			aMessage << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			_syncInfo->_server->sendReply(aMessage.str());
			break;
		case 14:{//get actual command, thread safeness, be careful!
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){			
				//atmpstr << "current command: " << aTask->getCurrCommand()->getID() << std::endl;
				aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_TASKo << " id=\"" << aTask->getID() << "\">" << TAG_CURRCMDo;
				if (aTask->getCurrCommand()==0) aMessage << 0; else aMessage << aTask->getCurrCommand()->getID();
				aMessage << TAG_CURRCMDc << TAG_TASKc << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Current command" << TAG_MSGc << TAG_ERRNOo << 0;
			}else{
				aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << MSG_CMPNFOUND << TAG_MSGc << TAG_ERRNOo << 2;
			}
			aMessage << TAG_ERRNOc << std::endl << TAG_STATUSo;
			if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
			aMessage << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			_syncInfo->_server->sendReply(aMessage.str());
			break;
		}
		case 15://pause simulation
			_simComp->setStopFlag(true);
			aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulation stopped" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo << SIM_READY << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
			_syncInfo->_server->sendReply(aMessage.str());
			break;
		default:
			return false; 
	}
	return true;
}

void Simulator::sendStatus(){
	std::ostringstream aMessage;
	aMessage << TAG_HEADER << std::endl << TAG_STARTo << std::endl << TAG_GLOBALo << std::endl << TAG_MSGo << "Simulator status notification" << TAG_MSGc << TAG_ERRNOo << 0 << TAG_ERRNOc << std::endl << TAG_STATUSo;
	if (_busy) aMessage << SIM_BUSY; else aMessage << SIM_READY;
	aMessage << TAG_STATUSc << std::endl << TAG_GLOBALc << std::endl << TAG_STARTc << std::endl;
	_syncInfo->_server->sendReply(aMessage.str());
}
