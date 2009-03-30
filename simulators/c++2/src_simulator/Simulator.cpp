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

Simulator::Simulator(SimServSyncInfo* iSyncInfo):_syncInfo(iSyncInfo),  _simComp(iSyncInfo->_simComponents), _traceFileName("schedule"), _currCmdListener(0){}

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

void Simulator::schedule2TXT() const{
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::ofstream myfile (_traceFileName.c_str());
	if (myfile.is_open()){
		for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i){
			(*i)->schedule2TXT(myfile);
		}
		myfile.close();
	}
	else
		std::cout << "Unable to open text output file." << std::endl;
	gettimeofday(&aEnd,NULL);
	std::cout << "The text output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << _traceFileName << std::endl;
}

void Simulator::schedule2HTML() const{
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::ofstream myfile (_traceFileName.c_str());
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
	std::cout << "The HTML output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << _traceFileName << std::endl;
}

void Simulator::schedule2VCD() const{
	time_t aRawtime;
  	struct tm * aTimeinfo;
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
  	time(&aRawtime);
  	aTimeinfo=localtime(&aRawtime);
	std::ofstream myfile (_traceFileName.c_str());
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
	std::cout << "The VCD output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << _traceFileName << std::endl;

}

void Simulator::simulate(){
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
	for(TaskList::const_iterator i=_simComp->getTaskList().begin(); i!=_simComp->getTaskList().end();i++)
		if ((*i)->getCurrCommand()!=0) (*i)->getCurrCommand()->prepare();
	for_each(_simComp->getCPUList().begin(), _simComp->getCPUList().end(),std::mem_fun(&SchedulableDevice::schedule));
	transLET=getTransLowestEndTime(cpuLET);
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
		//sleep(1);
	}
	gettimeofday(&aEnd,NULL);
	std::cout << "The simulation took " << getTimeDiff(aBegin,aEnd) << "usec.\n";
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

/*void Simulator::setNumOfArgs(int iNumArgs){
	_numArgs=iNumArgs;
}

void Simulator::reset(){
	_simComp->reset();
	std::cout << "simulator reset" << std::endl;
}*/

void Simulator::run(){
	std::cout << "Running in server mode.\n";
	while (!_syncInfo->_terminate){
		pthread_mutex_lock (&_syncInfo->_mutexConsume);
		//_syncInfo->_terminate = decodeCommand(_syncInfo->_command);
		decodeCommand(_syncInfo->_command);
		pthread_mutex_unlock (&_syncInfo->_mutexProduce);
	}
	std::cout << "Simulator loop terminated." << std::endl;
}

bool Simulator::run(int iLen, char ** iArgs){
	_traceFileName =getArgs("-server", "server", iLen, iArgs);
	if (!_traceFileName.empty()) return false;
	std::cout << "Running in command line mode.\n";
	_traceFileName =getArgs("-help", "help", iLen, iArgs);
	if (_traceFileName.empty()){
		simulate();
		_traceFileName=getArgs("-ohtml", "scheduling.html", iLen, iArgs);
		if (!_traceFileName.empty()) schedule2HTML();
		_traceFileName=getArgs("-otxt", "scheduling.txt", iLen, iArgs);
		if (!_traceFileName.empty()) schedule2TXT();
		_traceFileName=getArgs("-ovcd", "scheduling.vcd", iLen, iArgs);
		if (!_traceFileName.empty()) schedule2VCD();
		//_traceFileName=getArgs("-ograph", "scheduling.vcd", iLen, iArgs);
		//if (!aFilename.empty()) schedule2Graph();
		_simComp->streamBenchmarks(std::cout);
		std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
	}
	else
		printHelp();
	return true;
}

void Simulator::decodeCommand(char* iCmd){
	unsigned int aCmd, aParam1, aParam2;
	std::istringstream aInpStream(iCmd);
	std::string aStrParam;
	aInpStream >> aCmd;
	//sscanf(iCmd,"%u %u %u", &aCmd, &aParam1, &aParam2);
	if (_currCmdListener!=0){
		//std::cout << "Before del listener.\n";
		 delete _currCmdListener;
		_currCmdListener=0;
		//std::cout << "After del listener.\n";
	}
	_simComp->setStopFlag(false);
	_syncInfo->_server->sendReply("Begin processing\n");
	switch (aCmd){
		case 0: //Quit simulation
			//_syncInfo->_terminate=true;
			//std::cout << "I was here " << std::endl;
			break;
		case 1:
			aInpStream >> aParam1;
			switch (aParam1){
				case 0:	//Run up to next breakpoint
					break;
				case 1:	//Run up to trans x
					break;
				case 2:	//Run x transactions
					aInpStream >> aParam2;
					_currCmdListener=new RunXTransactions(_simComp,aParam2);
					std::cout << "created listener run " << aParam2 << " transactions" << std::endl; 
					break;
				case 3:	//Run up to command x
					break;
				case 4:	//Run x commands
					aInpStream >> aParam2;
					_currCmdListener=new RunXCommands(_simComp,aParam2);
					std::cout << "created listener run " << aParam2 << " commands" << std::endl; 
					break;
				case 5: //Run up to time x
					aInpStream >> aParam2;
					_currCmdListener=new RunXTimeUnits(_simComp,aParam2);
					std::cout << "created listener run to time " << aParam2 << std::endl; 
					break;
				case 6:	//Run for x time units
					 aInpStream >> aParam2;
					_currCmdListener=new RunXTimeUnits(_simComp,aParam2+SchedulableDevice::getSimulatedTime());
					std::cout << "created listener run " << aParam2 << " time units" << std::endl; 
					break;
				case 7: //Run up to next choice/select event
					break;
				case 8:{//Run up to next transfer on bus x
					aInpStream >> aStrParam;
					ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getBusByName(aStrParam));
					if (aSubject!=0){
						_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						std::cout << "created listener on Bus " << aStrParam << std::endl;
					}
				} 
					break;
				case 9:{//Run until CPU x executes
					aInpStream >> aStrParam;
					ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getCPUByName(aStrParam));
					if (aSubject!=0){
						_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						std::cout << "created listener on CPU " << aStrParam << std::endl;
					}
					break;
				} 
				case 10:{//Run until Task x executes
					aInpStream >> aStrParam;
					ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getTaskByName(aStrParam));
					if (aSubject!=0){
						_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						std::cout << "created listener on Task " << aStrParam << std::endl;
					}
					break;
				} 
				case 11:{//Run until Mem x is accessed
					aInpStream >> aStrParam;
					ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getSlaveByName(aStrParam));
					if (aSubject!=0){
						_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						std::cout << "created listener on Slave " << aStrParam << std::endl;
					}
					break;
				} 

				case 12:{//Run until operation on channel x is performed
					aInpStream >> aStrParam;
					ListenerSubject<TransactionListener>* aSubject= static_cast<ListenerSubject<TransactionListener>* > (_simComp->getChannelByName(aStrParam));
					if (aSubject!=0){
						_currCmdListener=new RunTillTransOnDevice(_simComp, aSubject);
						std::cout << "created listener on Channel " << aStrParam << std::endl;
					}
					break;
				} 
				default:
					std::cout << "Command not found"<< std::endl;
			}
			//std::cout << "Before sim\n";
			simulate();
			std::cout << "Simulated time: " << SchedulableDevice::getSimulatedTime() << " time units.\n";
			break;
		case 2:	//reset
			_simComp->reset();
			std::cout << "simulator reset" << std::endl;
			break;
		case 3:{//Print variable x
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				std::cout << "Task " << aStrParam << " exists" << std::endl;
				aInpStream >> aStrParam;
				std::cout << "Check if Var *" << aStrParam << "* exists" << std::endl;
				std::cout << "Len: " << aStrParam.length() << std::endl;
				ParamType* aParam=aTask->getVariableByName(aStrParam);
				if (aParam!=0){
					std::cout << "Variable " << aStrParam << " exists" << std::endl;
					std::cout << "value of variable " << aStrParam << ": " << *aParam << std::endl;
				}else{
					std::cout << "Var doesn't exist" << std::endl;
				}
			}
			break;
		}
		case 4:	//Print information about simulation element x
			break;
		case 5:{//Set variable x to value y
			aInpStream >> aStrParam;
			TMLTask* aTask = _simComp->getTaskByName(aStrParam);
			if (aTask!=0){
				aInpStream >> aStrParam;
				ParamType* aParam=aTask->getVariableByName(aStrParam);
				if (aParam!=0){
					aInpStream >> *aParam;
					std::cout << "set variable " << aStrParam << " to " << *aParam << std::endl;
				}
			}
			break;
		}
		case 6: //Write x samples/events to channel y
			break;
		case 7: //Save trace in file x
			aInpStream >> aParam1;
			aInpStream >>_traceFileName;
			switch (aParam1){
				case 0: //VCD
					schedule2VCD();
					break;
				case 1: //HTML
					schedule2HTML();
					break;
				case 2: //TXT
					schedule2TXT();
					break;
				default:
					std::cout << "Command not found"<< std::endl;
			}
			break;
		case 8:{ //Save simulation state in file x
			aInpStream >> aStrParam;
			std::ofstream aFile (aStrParam.c_str());
			if (aFile.is_open()){
				_simComp->writeObject(aFile);
			}else{
				std::cout << "Error when opening file "<< aStrParam << std::endl;
			}
			break;
		}
		case 9:{//Restore simulation state from file x
			aInpStream >> aStrParam;
			std::ifstream aFile(aStrParam.c_str());
			if (aFile.is_open()){
				_simComp->readObject(aFile);
			}else{
				std::cout << "Error when opening file "<< aStrParam << std::endl;
			}
			break;
		}
		case 10:{ //Save benchmarks in file x
			 
			aInpStream >> aParam1;
			switch (aParam1){
			case 0: _simComp->streamBenchmarks(std::cout);
				break;
			case 1:{
				aInpStream >> aStrParam;
				std::ofstream aFile (aStrParam.c_str());
				if (aFile.is_open()){
					_simComp->streamBenchmarks(aFile);
					std::cout << "Benchmarks written to file "<< aStrParam << std::endl;

				}else{
					std::cout << "Error when opening file "<< aStrParam << std::endl;
				}
				break;
			}
			default:
				std::cout << "Command not found"<< std::endl;
			}
			break;
		}
		case 11://Set breakpoint in task x, command y
			
			break;
		case 12://Choose branch
			break;
		default:
			std::cout << "Command not found"<< std::endl;
	}
	_syncInfo->_server->sendReply("End processing\n");
	//std::cout << "Command: " << aCmd << "  Param1: " << aParam1 << "  Param2: " << aParam2 << std::endl;
}

bool Simulator::execAsyncCmd(char* iCmd){
	unsigned int aCmd;
	std::istringstream aInpStream(iCmd);
	std::string aStrParam;
	aInpStream >> aCmd;
	switch (aCmd){
		case 0: //Quit simulation
			_simComp->setStopFlag(true);
			_syncInfo->_terminate=true;
			return false;
		case 13:{//get current time
			std::ostringstream atmpstr;
			atmpstr << SchedulableDevice::getSimulatedTime() << std::endl;
			_syncInfo->_server->sendReply(atmpstr.str());
			break;
		}
		case 14://get actual command
			_syncInfo->_server->sendReply("act command goes here\n");
			break;
		case 15://pause simulation
			_simComp->setStopFlag(true);
			break;
		default:
			return false; 
	}
	return true;
}
