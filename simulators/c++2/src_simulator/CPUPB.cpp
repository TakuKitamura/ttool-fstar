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

#include <CPUPB.h>
#include <TMLTransaction.h>
#include <TMLTask.h>
#include <TMLCommand.h>
#include <TMLChannel.h>
#include <Bus.h>
#include <TransactionListener.h>

CPUPB::CPUPB(unsigned int iID, std::string iName, TMLTime iTimePerCycle, unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc, unsigned int iPipelineSize, unsigned int iTaskSwitchingCycles, unsigned int iBranchingMissrate, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle, unsigned int ibyteDataSize): CPU(iID, iName, iTimePerCycle, iCyclesPerExeci, iCyclesPerExecc, iPipelineSize, iTaskSwitchingCycles, iBranchingMissrate, iChangeIdleModeCycles, iCyclesBeforeIdle, ibyteDataSize){
}

CPUPB::~CPUPB(){  
}

void CPUPB::schedule(){
	TaskList::iterator i;
	TMLTransaction *aMarkerPast=0, *aMarkerFuture=0,*aTempTrans, *aOldTransaction;
	unsigned int aHighestPrioPast=-1;
	TMLTime aTransTimeFuture=-1,aRunnableTime;
	TMLTask* aTempTask;
	for(i=_taskList.begin(); i != _taskList.end(); ++i){
		aTempTask=*i;	
		//std::cout << _name << " schedules, before getCurrTransaction " << std::endl;
		if (aTempTask->getCurrCommand()!=0){
			aTempTrans=aTempTask->getCurrCommand()->getCurrTransaction();
			//std::cout << "after getCurrTransaction " << std::endl;
			if (aTempTrans!=0 && aTempTrans->getVirtualLength()!=0){
				aRunnableTime=aTempTrans->getRunnableTime();
				//if (aRunnableTime<=_endSchedule && aTempTrans->getVirtualLength()!=0){
				if (aRunnableTime<=_endSchedule){
				//Past
					if (aTempTask->getPriority()<aHighestPrioPast){
						aHighestPrioPast=aTempTask->getPriority();
						aMarkerPast=aTempTrans;
					}
				}else{
				//Future
					if(aRunnableTime<aTransTimeFuture){
						aTransTimeFuture=aRunnableTime;
						aMarkerFuture=aTempTrans;
					}
					
				}
			}
		}
	}
	//_nextTransaction=(aMarkerPast==0)?aMarkerFuture:aMarkerPast;
	aOldTransaction=_nextTransaction;
	if (aMarkerPast==0){
		_nextTransaction=aMarkerFuture;
		/*if (aMarkerFuture==0){
			std::cout << _name << " no transaction found" << std::endl;
		}else{
			std::cout << _name << " transaction in the FUTURE found" << std::endl << _nextTransaction->toString() << std::endl;
		}*/
	}else{
		_nextTransaction=aMarkerPast;
		//std::cout << std::endl << _name << " transaction in the PAST found" << std::endl << _nextTransaction->toString() << std::endl;
	}
	if (aOldTransaction!=0 && aOldTransaction!=_nextTransaction && _busNextTransaction!=0) _busNextTransaction->registerTransaction(0,this);
	if (_nextTransaction!=0){
		calcStartTimeLength();
		FOR_EACH_TRANSLISTENER (*i)->transScheduled(_nextTransaction);
	}
}

void CPUPB::registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice){
}
