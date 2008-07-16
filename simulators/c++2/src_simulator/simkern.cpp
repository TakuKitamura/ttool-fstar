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

TMLTransaction* getTransLowestEndTime(CPUList& iCPUlist, CPU*& oCPU){
	CPUList::iterator i;
	TMLTransaction *aMarker=0, *aTempTrans;
	TMLTime aLowestTime=-1;
	CPU* tempCPU;
	//static unsigned int aTransitionNo=0;
	for(i=iCPUlist.begin(); i != iCPUlist.end(); ++i){
		tempCPU=*i;
		aTempTrans=tempCPU->getNextTransaction();	
		if (aTempTrans!=0 && aTempTrans->getVirtualLength()>0){	
			//std::cout << tempCPU->toString() << " has trans\n";
			if (aTempTrans->getEndTime() < aLowestTime){		
				aMarker=aTempTrans;
				aLowestTime=aTempTrans->getEndTime();
				oCPU=tempCPU;
			}
		}
	}
	/*std::ostringstream outp;
	if (aMarker!=0){
		outp << "(" << aTransitionNo << ",\"i(" << oCPU->toString() << "__" << aMarker->getCommand()->getTask()->toString() << "__" << aMarker->getCommand()->getCommandStr();
		if (aMarker->getChannel()!=0)
			outp << "__" << aMarker->getChannel()->toShortString();
		outp << ")\"," << ++aTransitionNo << ")";
		//std::cout << "des (0, " << aTransitionNo << ", " << aTransitionNo+1 << ")" << std::endl << outp.str() << std::endl;
		for (unsigned int i=0; i<aMarker->getVirtualLength(); i++) std::cout << outp.str() << std::endl;
	}*/
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

void schedule2HTML(CPUList& iCPUlist,BusList& iBusList, int iLen, char** iArgs){
	CPUList::iterator i;
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
		myfile << "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"schedstyle.css\" />\n<title>Scheduling</title>\n</head>\n<body>\n";		
		for(i=iCPUlist.begin(); i != iCPUlist.end(); ++i){
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
		for (i=iVcdList.begin(); i!= iVcdList.end(); ++i){
			myfile << "r4 " << (*i)->toShortString() << "\n";
		}
		std::cout << "Simulated cycles: " << aCurrTime << std::endl;
		myfile.close();
	}
	else
		std::cout << "Unable to open VCD output file" << std::endl;
	gettimeofday(&aEnd,NULL);
	std::cout << "The VCD output took " << getTimeDiff(aBegin,aEnd) << "usec. File: " << aFilename << std::endl;

}

inline void scheduleCPUBus(CPUList& iCPUlist,BusList& iBusList){
	for_each(iCPUlist.begin(), iCPUlist.end(),std::mem_fun(&CPU::schedule));
	for_each(iBusList.begin(), iBusList.end(),std::mem_fun(&Bus::schedule));
}

inline void scheduleCPUBus(CPU* iCPU,BusList& iBusList){
	iCPU->schedule();
	for_each(iBusList.begin(), iBusList.end(),std::mem_fun(&Bus::schedule));
}

void simulate(CPUList& cpulist, BusList& buslist){
	TMLTransaction* depTransaction,*depCPUnextTrans,*transLET;
	TMLCommand* commandLET,*depCommand,*depCPUnextCommand;
	TMLTask* depTask;
	CPU* cpuLET,*depCPU;
	struct timeval aBegin,aEnd;
	gettimeofday(&aBegin,NULL);
	scheduleCPUBus(cpulist,buslist);
	transLET=getTransLowestEndTime(cpulist,cpuLET);
	while (transLET!=0){	
		commandLET=transLET->getCommand();
		//std::cout << "Execute " << commandLET->toString() << " next Trans:" << commandLET->getCurrTransaction() << " commandLET:" << commandLET << std::endl;
		commandLET->execute();
		//std::cout << "Add" << std::endl;
		cpuLET->addTransaction();
		commandLET->getTask()->addTransaction(transLET);
		//std::cout << "Schedule" << std::endl;
		scheduleCPUBus(cpuLET,buslist);	
		//std::cout << "huge IF" << std::endl;
		depTask=commandLET->getDependentTask();
		if (depTask!=0){
		 depCPU=depTask->getCPU();			
		 if (depCPU!=cpuLET){								//tasks running on different CPUs
		  depCommand=depTask->getCurrCommand();
		  if (depCommand!=0 && (depCommand->getChannel()==commandLET->getChannel() || depCommand->channelUnknown())){ //commands accessing the same channel
		   depTransaction=depCommand->getCurrTransaction();
		   if (depTransaction!=0 && depTransaction->getVirtualLength()!=0){		//dependent task has a current transaction and is not blocked any more
		    depCPUnextTrans=depCPU->getNextTransaction();
		    if (depCPUnextTrans!=0){							//there is a transaction scheduled on depCPU
		     depCPUnextCommand=depCPUnextTrans->getCommand();
		     if (depCPUnextCommand->getTask()!=depTask){				//dependent task is not yet scheduled
			//if (depCPUnextCommand->truncateTransactionAt(transLET->getEndTime())!=0){
			if (depCPU->truncateNextTransAt(transLET->getEndTime())!=0){
				depCPUnextCommand->execute();
				depCPU->addTransaction();
				depCPUnextCommand->getTask()->addTransaction(depCPUnextTrans);
			}
			//std::cout << "Schedule within big IF" << std::endl;
			scheduleCPUBus(depCPU,buslist);
		     }
		    }else{
			//std::cout << "Schedule within small if" << std::endl;
		     scheduleCPUBus(depCPU,buslist);
		    }
		   }
		  }
		 }
		}
		transLET=getTransLowestEndTime(cpulist,cpuLET);
	}
	gettimeofday(&aEnd,NULL);
	std::cout << "The simulation took " << getTimeDiff(aBegin,aEnd) << "usec.\n";
}

const std::string getArgs(int iLen, char** iArgs, const std::string& iComp, const std::string& iDefault){
	int aPosition=0;
	while (aPosition < iLen){
		if (iComp.compare(iArgs[aPosition])==0){
			if (aPosition+1 < iLen && iArgs[aPosition+1][0]!='-'){
				//std::cout << "next argument: " << std::string(iArgs[aPosition+1]) << std::endl;
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
