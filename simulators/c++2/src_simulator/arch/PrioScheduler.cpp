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
#include<PrioScheduler.h>
#include <TMLTransaction.h>

PrioScheduler::PrioScheduler(const std::string& iName, Priority iPrio): WorkloadSource(iPrio), _name(iName), _nextTransaction(0) /*,_lastSourceIndex(0)*/{
}

PrioScheduler::PrioScheduler(const std::string& iName, Priority iPrio, WorkloadSource** aSourceArray, unsigned int iNbOfSources): WorkloadSource(iPrio, aSourceArray, iNbOfSources), _name(iName), _nextTransaction(0), _lastSource(0) {
}

TMLTime PrioScheduler::schedule(TMLTime iEndSchedule){
	TaskList::iterator i;
	TMLTransaction *aMarkerPast=0, *aMarkerFuture=0,*aTempTrans;
	Priority aHighestPrioPast=-1;
	TMLTime aTransTimeFuture=-1,aRunnableTime;
	WorkloadSource *aSourcePast=0, *aSourceFuture=0;  //NEW
	//std::cout << "Prio Scheduler " << _name << ":\n";
	for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i){
		(*i)->schedule(iEndSchedule);
		//std::cout << _name << " schedules, before getCurrTransaction " << std::endl;
		aTempTrans=(*i)->getNextTransaction(iEndSchedule);
		//std::cout << "after getCurrTransaction " << std::endl;
		if (aTempTrans!=0 && aTempTrans->getVirtualLength()!=0){
			//std::cout << "Found on " << (*i)->toString() << ": " << aTempTrans->toString() << "\n";
			aRunnableTime=aTempTrans->getRunnableTime();	
			if (aRunnableTime<=iEndSchedule){
			//Past
				if ((*i)->getPriority()<aHighestPrioPast){
					aHighestPrioPast=(*i)->getPriority();
					aMarkerPast=aTempTrans;
					aSourcePast=*i; //NEW
				}
			}else{
			//Future
				if(aRunnableTime<aTransTimeFuture){
					aTransTimeFuture=aRunnableTime;
					aMarkerFuture=aTempTrans;
					aSourceFuture=*i; //NEW
				}
				
			}
		}
			//else std::cout << "Found on " << (*i)->toString() << " nothing\n";
	}
	if (aMarkerPast==0){
		_nextTransaction=aMarkerFuture;
		_lastSource=aSourceFuture; //NEW
	}else{
		_nextTransaction=aMarkerPast;
		_lastSource=aSourcePast; //NEW
	}
	/*if (_nextTransaction==0)
		std::cout << "Scheduler " << _name << " hasn't found anything.\n";
	else
		std::cout << "Scheduler " << _name << " schedules " << _nextTransaction->toString() << "\n";*/
	return 0;
}

//TMLTransaction* PrioScheduler::getNextTransaction(TMLTime iEndSchedule) const{
//	return _nextTransaction;
//}

//std::string PrioScheduler::toString() const{
//	return _name;
//}

PrioScheduler::~PrioScheduler(){
	std::cout << _name << ": Scheduler deleted\n";
}

void PrioScheduler::reset(){
	WorkloadSource::reset();
	_nextTransaction=0;
}

//void PrioScheduler::transWasScheduled(SchedulableDevice* iDevice){
//	if (_lastSource!=0) _lastSource->transWasScheduled(iDevice);
//}
