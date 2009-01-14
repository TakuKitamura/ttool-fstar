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

#include <simkern.h>
#include <TraceableDevice.h>

unsigned int TMLTask::_id=0;
unsigned int Bus::_id=0;
unsigned int CPU::_id=0;
TMLTime SchedulableDevice::_simulatedTime=0;

TMLTransaction* getTransLowestEndTime(SchedulingList& iSchedList, SchedulableDevice*& oResultDevice){
	SchedulingList::iterator i;
	TMLTransaction *aMarker=0, *aTempTrans;
	TMLTime aLowestTime=-1;
	SchedulableDevice* aTempDevice;
	//static unsigned int aTransitionNo=0;
#ifdef DEBUG_KERNEL
	std::cout << "kernel:getTLET: before loop" << std::endl;
#endif
	for(i=iSchedList.begin(); i != iSchedList.end(); ++i){
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

void schedule2Graph(CPUList& iCPUlist, int iLen, char** iArgs){
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::string aFilename(getArgs(iLen, iArgs, "-ogra", "scheduling.aut"));
	if (aFilename.empty()) return;
	std::ofstream myfile (aFilename.c_str());
	if (myfile.is_open()){
 		CPUList::iterator i;
		GraphTransactionQueue aQueue;
		TMLTransaction* aTrans, *aTopElement;
		unsigned int aTransitionNo=0;
		for (i=iCPUlist.begin(); i!= iCPUlist.end(); ++i){
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

}

void schedule2TXT(SchedulingList& iSchedList, int iLen, char** iArgs){
	SchedulingList::iterator i;
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	std::string aFilename(getArgs(iLen, iArgs, "-otxt", "scheduling.txt"));
	if (aFilename.empty()) return;
	std::ofstream myfile (aFilename.c_str());
	if (myfile.is_open()){
		for(i=iSchedList.begin(); i != iSchedList.end(); ++i){
			(*i)->schedule2TXT(myfile);
		}
		myfile.close();
	}
	else
		std::cout << "Unable to open text output file" << std::endl;
	gettimeofday(&aEnd,NULL);
	std::cout << "The text output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << aFilename << std::endl;
}

void schedule2HTML(SchedulingList& iSchedList,BusList& iBusList, int iLen, char** iArgs){
	SchedulingList::iterator i;
	BusList::iterator j;
	struct timeval aBegin,aEnd;
	std::string helpNeeded(getArgs(iLen, iArgs, "-help", "help"));
	if (!helpNeeded.empty()) printHelp();
	gettimeofday(&aBegin,NULL);
	std::string aFilename(getArgs(iLen, iArgs, "-ohtml", "scheduling.htm"));
	if (aFilename.empty()) return;
	std::ofstream myfile (aFilename.c_str());
	if (myfile.is_open()){
		myfile << "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
		myfile << "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"schedstyle.css\" />\n<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\" />\n<title>Scheduling</title>\n</head>\n<body>\n";		
		for(i=iSchedList.begin(); i != iSchedList.end(); ++i){
			(*i)->schedule2HTML(myfile);
		}
		for(j=iBusList.begin(); j != iBusList.end(); ++j){
			(*j)->schedule2HTML(myfile);
		}
		//for_each(iCPUlist.begin(), iCPUlist.end(),std::bind2nd(std::mem_fun(&CPU::schedule2HTML),myfile));
		myfile << "</body>\n</html>\n";
		myfile.close();
	}
	else
		std::cout << "Unable to open HTML output file" << std::endl;
	gettimeofday(&aEnd,NULL);
	std::cout << "The HTML output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << aFilename << std::endl;
}

void schedule2VCD(TraceableDeviceList& iVcdList, int iLen, char** iArgs){
	time_t aRawtime;
  	struct tm * aTimeinfo;
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
  	time(&aRawtime);
  	aTimeinfo=localtime(&aRawtime);
	std::string aFilename(getArgs(iLen, iArgs, "-ovcd", "scheduling.vcd"));
	if (aFilename.empty()) return;
	std::ofstream myfile (aFilename.c_str());
	if (myfile.is_open()){
 		TraceableDeviceList::iterator i;
		SignalChangeQueue aQueue;
		std::string aSigString;
		bool aNoMoreTrans;
		TraceableDevice* actDevice;
		TMLTime aTime, aCurrTime=-1;
		SignalChangeData* aTopElement;
		unsigned int aNextClockEvent=0;
		myfile << "$date\n" << asctime(aTimeinfo) << "$end\n\n$version\nDaniels TML simulator\n$end\n\n";
		myfile << "$timescale\n1 ns\n$end\n\n$scope module Simulation $end\n";
		for (i=iVcdList.begin(); i!= iVcdList.end(); ++i){
			//std::cout << "device: " << (*i)->toString() << std::endl;
			myfile << "$var integer 3 " << (*i)->toShortString() << " " << (*i)->toString() << " $end\n";
			aTime = (*i)->getNextSignalChange(true, aSigString, aNoMoreTrans);
			aQueue.push(new SignalChangeData(aSigString, aTime, (aNoMoreTrans)?0:(*i)));
		}
		myfile << "$var integer 32 clk Clock $end\n";
		myfile << "$upscope $end\n$enddefinitions  $end\n\n";
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
				myfile << "r" << aNextClockEvent << " clk\n";
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
		for (i=iVcdList.begin(); i!= iVcdList.end(); ++i){
			myfile << "r4 " << (*i)->toShortString() << "\n";
			//std::cout << "Utilization of component " << (*i)->toString() << ": " << ((float)(*i)->getBusyCycles()) / ((float)aCurrTime) << std::endl;
		}

		myfile.close();
	}
	else
		std::cout << "Unable to open VCD output file" << std::endl;
	gettimeofday(&aEnd,NULL);
	std::cout << "The VCD output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << aFilename << std::endl;

}

//inline void scheduleCPUBus(SchedulingList& iSchedList,BusList& iBusList){
//	for_each(iSchedList.begin(), iSchedList.end(),std::mem_fun(&SchedulableDevice::schedule));
//	for_each(iBusList.begin(), iBusList.end(),std::mem_fun(&SchedulableCommDevice::schedule));
//}

//inline void scheduleCPUBus(SchedulableDevice* iDevice,BusList& iBusList){
//	iDevice->schedule();
//	for_each(iBusList.begin(), iBusList.end(),std::mem_fun(&SchedulableCommDevice::schedule));
//}

void simulate(SchedulingList& iSchedList, BusList& buslist){
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
	for_each(iSchedList.begin(), iSchedList.end(),std::mem_fun(&SchedulableDevice::schedule));
	transLET=getTransLowestEndTime(iSchedList,cpuLET);
	while (transLET!=0){
#ifdef DEBUG_KERNEL
		std::cout << "kernel:simulate: scheduling decision: " <<  transLET->toString() << std::endl;
#endif
		commandLET=transLET->getCommand();
#ifdef DEBUG_KERNEL
		std::cout << "kernel:simulate: add trans " << commandLET->toString() << std::endl;
#endif
		if (cpuLET->addTransaction()){
#ifdef DEBUG_KERNEL
		 std::cout << "kernel:simulate: invoke on executing CPU" << std::endl;
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
		transLET=getTransLowestEndTime(iSchedList,cpuLET);
	}
	gettimeofday(&aEnd,NULL);
	std::cout << "The simulation took " << getTimeDiff(aBegin,aEnd) << "usec.\n";
}

const std::string getArgs(int iLen, char** iArgs, const std::string& iComp, const std::string& iDefault){
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

void printHelp(){
	std::cout << "*****\nCommand line usage: run.x -ohtml myfile.htm -ovcd myfile.vcd\nParameters can be omitted if output is not needed, if file name is omitted the following default values will be taken instead: scheduling.htm, scheduling.vcd.\n*****\n";
}

void streamBenchmarks(std::ostream& s, TraceableDeviceList& iVcdList){
	TraceableDeviceList::iterator i;
	for (i=iVcdList.begin(); i!= iVcdList.end(); ++i){
		(*i)->streamBenchmarks(s);
	}
}
