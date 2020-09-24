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

ReconfigScheduler::ReconfigScheduler(const std::string& iName, Priority iPrio, const std::string iTaskOrder): WorkloadSource(iPrio), _name(iName), _taskOrder(iTaskOrder), _nextTransaction(0), _tempWorkloadList(0), _indexMark(0), _reconfigNumber(0){}

ReconfigScheduler::ReconfigScheduler(const std::string& iName, Priority iPrio, WorkloadSource** aSourceArray, unsigned int iNbOfSources, const std::string iTaskOrder): WorkloadSource(iPrio, aSourceArray, iNbOfSources), _name(iName), _taskOrder(iTaskOrder), _nextTransaction(0), _lastSource(0), _tempWorkloadList(0), _indexMark(0), _reconfigNumber(0) {
}
Priority currentRank = 0;
TMLTime ReconfigScheduler::schedule(TMLTime iEndSchedule){
  std::cout<<"reconfig scheduler"<<std::endl;
	TaskList::iterator i;
	TMLTransaction *aMarkerPast=0, *aMarkerFuture=0,*aTempTrans;
	TMLTask* aTempTask;
	TMLTime aTransTimeFuture=-1,aRunnableTime,aLowestRunnableTimePast=-1;;
	WorkloadSource *aSourcePast=0, *aSourceFuture=0;  //NEW
	std::vector<std::string> getTaskOrder;
    std::string _tempTaskOrder, taskOrderCopy;
    taskOrderCopy = _taskOrder.substr(0,_taskOrder.length());
    while (taskOrderCopy.find_first_of(";") != std::string::npos){
        _indexMark=taskOrderCopy.find_first_of(";");
        _tempTaskOrder = taskOrderCopy.substr(0,_indexMark);
        taskOrderCopy=taskOrderCopy.substr(_indexMark+1, taskOrderCopy.length());
        getTaskOrder.push_back(_tempTaskOrder);
    }
     getTaskOrder.push_back(taskOrderCopy);
	  for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i){
#ifdef DEBUG_FPGA
	    std::cout<<"temp for"<<std::endl;
#endif
	    /* if(taskStart>=_taskOrder.length()){
	      break;
	      }*/
	    aTempTrans=(*i)->getNextTransaction(iEndSchedule);
	    if(aTempTrans==0) continue;
	    aTempTask=aTempTrans->getCommand()->getTask();
	    std::string taskName=aTempTask->toString();
	    unsigned int indexTask=taskName.find_last_of("_");
	    unsigned int taskNameLength=taskName.length()-indexTask;
#ifdef DEBUG_FPGA
	    std::cout<<"taskName "<<taskName.substr(indexTask+1,taskNameLength)<<std::endl;
#endif
        // set priority to task base on order of user
        for(int z = 0; z < getTaskOrder.size(); z++){
             std::string::size_type position=getTaskOrder[z].find(taskName.substr(indexTask+1,taskNameLength));
                    if(position!=_tempTaskOrder.npos){
                        (*i)->setPriority(z);

             #ifdef DEBUG_FPGA
                        std::cout<<"add task"<<std::endl;
             #endif
                        break;
                    }

        }
        std::string::size_type position1=_taskOrder.find(taskName.substr(indexTask+1,taskNameLength));
        //if task is not in order list, make it has lowest priority
        if(position1 == std::string::npos){
            (*i)->setPriority(getTaskOrder.size());
        }

	  }
//    _workloadList.sort(sortByPriority);
    Priority aHighestPrioPast=-1;
    bool isFirstRankSelected = false;
	for(WorkloadList::iterator i = _workloadList.begin(); i != _workloadList.end(); ++i){
#ifdef DEBUG_FPGA
	  std::cout<<"schedule for"<<std::endl;
#endif
	  (*i)->schedule(iEndSchedule);
	  aTempTrans=(*i)->getNextTransaction(iEndSchedule);
	  if(aTempTrans==0) std::cout<< (*i)->toString() + "temp trans is 0"<<std::endl;
      else {
        std::cout<<(*i)->toString() + " hellboy temp trans is "<<iEndSchedule<<", rank = "<<(*i)->getPriority()<< ",Runable "<< aTempTrans->getRunnableTime()<< ",virtual "<< aTempTrans->getVirtualLength()<<std::endl;
        currentRank = (*i)->getPriority();
      }
#ifdef DEBUG_FPGA
	  if(aTempTrans==0) std::cout<<"temp trans is 0"<<std::endl;
	  else std::cout<<"temp trans is "<<aTempTrans->toShortString()<<std::endl;
#endif
	  if (aTempTrans!=0 && aTempTrans->getVirtualLength()!=0){

	    aRunnableTime=aTempTrans->getRunnableTime();
	    std::cout << "-----------hellboy_FUTURE: Runable time: " << aRunnableTime << ", " << aTempTrans->getCommand()->getTask()->toString() << std::endl;
        if ((*i)->getPriority()<aHighestPrioPast || ((*i)->getPriority()==aHighestPrioPast && aRunnableTime<aLowestRunnableTimePast)){
            aHighestPrioPast=(*i)->getPriority();
            aLowestRunnableTimePast=aRunnableTime;
            aMarkerPast=aTempTrans;
            aSourcePast=*i;
        }
#ifdef DEBUG_FPGA
	    std::cout<<"erase"<<std::endl;
#endif
	  }
	}
	if (aMarkerPast!=0){
	  _nextTransaction=aSourcePast->getNextTransaction(iEndSchedule);;
	  _lastSource=aSourcePast; //NEW
	  _reconfigNumber = aSourcePast->getPriority();
	}

#ifdef DEBUG_FPGA
	std::cout<<"end order scheduler"<<std::endl;
#endif
	return _reconfigNumber;
}

ReconfigScheduler::~ReconfigScheduler(){
  #ifdef DEBUG_FPGA
	std::cout << _name << ": Scheduler deleted\n";
#endif	
}

void ReconfigScheduler::reset(){
	WorkloadSource::reset();
	_nextTransaction=0;
	_reconfigNumber = 0;
}
