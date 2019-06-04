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
#include<ReconfigScheduler.h>
#include <TMLTransaction.h>
#include <FPGA.h>

ReconfigScheduler::ReconfigScheduler(const std::string& iName, Priority iPrio, const std::string iTaskOrder): WorkloadSource(iPrio), _name(iName), _taskOrder(iTaskOrder), _nextTransaction(0), _tempWorkloadList(0){}

ReconfigScheduler::ReconfigScheduler(const std::string& iName, Priority iPrio, WorkloadSource** aSourceArray, unsigned int iNbOfSources, const std::string iTaskOrder): WorkloadSource(iPrio, aSourceArray, iNbOfSources), _name(iName), _taskOrder(iTaskOrder), _nextTransaction(0), _lastSource(0), _tempWorkloadList(0) {
}

TMLTime ReconfigScheduler::schedule(TMLTime iEndSchedule){
  std::cout<<"reconfig scheduler"<<std::endl;
	TaskList::iterator i;
	TMLTransaction *aMarkerPast=0, *aMarkerFuture=0,*aTempTrans;
	TMLTask* aTempTask;
	TMLTime aTransTimeFuture=-1,aRunnableTime;
	WorkloadSource *aSourcePast=0, *aSourceFuture=0;  //NEW
	static unsigned int taskStart=0;
	static unsigned int reconfigNumber=0;

	if( _tempWorkloadList.empty()){
	  for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i){
	    std::cout<<"temp for"<<std::endl;
	    if(taskStart>=_taskOrder.length()){
	      break;
	    }
	    aTempTrans=(*i)->getNextTransaction(iEndSchedule);
	    if(aTempTrans==0) continue;
	    aTempTask=aTempTrans->getCommand()->getTask();
	    std::string taskName=aTempTask->toString();
	    unsigned int indexTask=taskName.find_last_of("_");
	    unsigned int taskNameLength=taskName.length()-indexTask;
	    std::cout<<"taskName "<<taskName.substr(indexTask+1,taskNameLength)<<" task order "<<_taskOrder.substr(taskStart,taskNameLength-1)<<std::endl;
	    if(taskName.substr(indexTask+1,taskNameLength) == _taskOrder.substr(taskStart,taskNameLength-1)){
	      _tempWorkloadList.push_back(aTempTask);
	      std::cout<<"add task"<<std::endl;
	      taskStart+=taskNameLength;
	     
	    }
	  }
	}
	if(_tempWorkloadList.empty()){
	  _nextTransaction=0;
	  return reconfigNumber ;
	}

	for(WorkloadList::iterator i=_tempWorkloadList.begin(); i != _tempWorkloadList.end(); ++i){
	  std::cout<<"schedule for"<<std::endl;
	  aTempTrans=(*i)->getNextTransaction(iEndSchedule);
	  if(aTempTrans==0) std::cout<<"temp trans is 0"<<std::endl;
	  else std::cout<<"temp trans is "<<aTempTrans->toShortString()<<std::endl;
	  if (aTempTrans!=0 && aTempTrans->getVirtualLength()!=0){
	    std::cout<<"erase"<<std::endl;
	    _nextTransaction=aTempTrans;
	    // _maxTransEndTime=max(_maxTransEndTime,_nextTransaction->getEndTime());
	    _tempWorkloadList.erase(i);
	    if(_taskOrder[taskStart-1]==';' && _tempWorkloadList.empty()){
	      // std::cout<<"plus 1 for ; "<<std::endl;
	      ++reconfigNumber;
	      //_endSchedule=maxEndTime;
	      //_maxEndTime=_maxTransEndTime;
	    }
	    break;
	  }
		     
	}

	
	std::cout<<"end order scheduler"<<std::endl;
	return reconfigNumber;
	  
	
     
}

ReconfigScheduler::~ReconfigScheduler(){
	std::cout << _name << ": Scheduler deleted\n";
}

void ReconfigScheduler::reset(){
	WorkloadSource::reset();
	_nextTransaction=0;
}
