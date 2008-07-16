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

#include <Bus.h>
#include <CPU.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLTask.h>
#include <TMLChannel.h>

Bus::Bus(std::string iName, TMLLength iBurstSize, TMLTime iTimePerSample): _name(iName), _burstSize(iBurstSize), _endSchedule(0), _nextTransaction(0), schedulingNeeded(true), _timePerSample(iTimePerSample){
	_myid=++_id;
	_transactList.reserve(BLOCK_SIZE);
}

Bus::~Bus(){
}

void Bus::registerMasterDevice(CPU* iMasterDev){
	_masterDevices.push_back(iMasterDev);
}

void Bus::schedule(){
	TMLTransaction* aTransToExecute=0,*aTempTrans=0,*aFutureTrans=0;
	TMLTime aTransTimeFuture=-1;
	CPU* aCPUforTrans=0;
	MasterDeviceList::iterator i;
	if (!schedulingNeeded) return;
	for (i=_masterDevices.begin(); aTransToExecute==0 && i != _masterDevices.end(); ++i){
		aTempTrans=_transactionHash[*i];
		if (aTempTrans!=0){
			if (aTempTrans->getStartTimeOperation()<=_endSchedule){
				//demand in the past
				aTransToExecute=aTempTrans;
				aCPUforTrans=*i;
			}else{
				//demand in the future
				if (aTempTrans->getStartTimeOperation()<aTransTimeFuture){
					aTransTimeFuture=aTempTrans->getStartTimeOperation();
					aCPUforTrans=*i;
					aFutureTrans=aTempTrans;
				}
			}
		}
	}
	if (aTransToExecute==0) aTransToExecute=aFutureTrans;
	if (aTransToExecute!=0){
		_nextTransaction=aTransToExecute;
		_nextTransOnCPU=aCPUforTrans;
		calcStartTimeLength();
	}
	schedulingNeeded=false;
}

void Bus::registerTransaction(CPU* iCPU, TMLTransaction* iTrans){
	_transactionHash.erase(iCPU);
	_transactionHash[iCPU]=iTrans;
	schedulingNeeded=true;
}

void Bus::addTransaction(){
	_endSchedule=_nextTransaction->getEndTime();
	_transactList.push_back(_nextTransaction);
	_nextTransaction=0;
	_transactionHash.erase(_nextTransOnCPU);
	schedulingNeeded=true;
}

void Bus::calcStartTimeLength(){
	_nextTransaction->setStartTime(max(((int)_endSchedule)-((int)_nextTransaction->getPenalties()),(int)_nextTransaction->getStartTime()));
	_nextTransaction->setLength(_nextTransaction->getVirtualLength()*_timePerSample);
	//_nextTransaction->setLength(max(_endSchedule,_nextTransaction->getStartTime())-_nextTransaction->getStartTime()+_nextTransaction->getVirtualLength());
}

TMLTransaction* Bus::getNextTransaction() const{
	return _nextTransaction;
}

TMLLength Bus::getBurstSize() const{
	return _burstSize;
}

unsigned int Bus::getID(){
	return _myid;
}

std::string Bus::toString(){
	return _name;
}

std::string Bus::toShortString(){
	std::ostringstream outp;
	outp << "bus" << _myid;
	return outp.str();
}

void Bus::schedule2HTML(std::ofstream& myfile){
	TransactionList::iterator i;
	TMLTime aCurrTime=0;
	TMLTransaction* aCurrTrans;
	unsigned int aBlanks,aLength,aColor;
	if (_transactList.empty()) return;
	myfile << "<h2><span>Scheduling for device: "<< _name <<"</span></h2>\n<table>\n<tr>";
	for(i=_transactList.begin(); i != _transactList.end(); ++i){
		aCurrTrans=*i;
		//if (aCurrTrans->getVirtualLength()==0) continue;
		aBlanks=aCurrTrans->getStartTimeOperation()-aCurrTime;
		if (aBlanks>0){
			if (aBlanks==1)
				myfile << "<td title=\"idle time\" class=\"not\"></td>\n";
			else
				myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time\" class=\"not\"></td>\n";
		}
		aLength=aCurrTrans->getOperationLength();
		//aColor=(((unsigned int)(aCurrTrans->getCommand()->getTask())) & 15)+1;
		aColor=aCurrTrans->getCommand()->getTask()->getID() & 15;
		if (aLength==1)
			myfile << "<td title=\""<< aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
		else
			myfile << "<td colspan=\"" << aLength << "\" title=\"" << aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
		aCurrTime=aCurrTrans->getEndTime();
	}
	myfile << "</tr>\n<tr>";
	for(aLength=0;aLength<aCurrTime;aLength++) myfile << "<th></th>";
	myfile << "</tr>\n<tr>";
	for(aLength=0;aLength<aCurrTime;aLength+=5) myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
	myfile << "</tr>\n</table>\n";
}

TMLTime Bus::getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans){
	//end_idle  end_read  end_write
	std::ostringstream outp;
	if (iInit){
		 _posTrasactList=_transactList.begin();
		_previousTransEndTime=0;
		 _vcdOutputState=INIT_BUS;
	}
	if (_posTrasactList == _transactList.end()){
		outp << "r" << END_IDLE_BUS << " bus" << _myid;
		oSigChange=outp.str();
		oNoMoreTrans=true;
		return _previousTransEndTime;
	}else{
		TMLTransaction* aCurrTrans=*_posTrasactList;
		oNoMoreTrans=false;
		switch (_vcdOutputState){
			case END_READ_BUS:
				do{
					_previousTransEndTime=(*_posTrasactList)->getEndTime();
					_posTrasactList++;
				}while (_posTrasactList != _transactList.end() && (*_posTrasactList)->getStartTimeOperation()==_previousTransEndTime && (*_posTrasactList)->getCommand()->getTask()==(*_posTrasactList)->getCommand()->getChannel()->getBlockedReadTask());
				if (_posTrasactList != _transactList.end() && (*_posTrasactList)->getStartTimeOperation()==_previousTransEndTime){
					outp << "r" << END_WRITE_BUS << " bus" << _myid;
					_vcdOutputState=END_WRITE_BUS;
				}else{
					outp << "r" << END_IDLE_BUS << " bus" << _myid;
					_vcdOutputState=END_IDLE_BUS;
					if (_posTrasactList == _transactList.end()) oNoMoreTrans=true;						
				}
				oSigChange=outp.str();
				return _previousTransEndTime;
			break;
			case END_WRITE_BUS:
				do{
					_previousTransEndTime=(*_posTrasactList)->getEndTime();
					_posTrasactList++;
				}while (_posTrasactList != _transactList.end() && (*_posTrasactList)->getStartTimeOperation()==_previousTransEndTime && (*_posTrasactList)->getCommand()->getTask()==(*_posTrasactList)->getCommand()->getChannel()->getBlockedWriteTask());
				if (_posTrasactList != _transactList.end() && (*_posTrasactList)->getStartTimeOperation()==_previousTransEndTime){
					outp << "r" << END_READ_BUS << " bus" << _myid;
					_vcdOutputState=END_READ_BUS;
				}else{
					outp << "r" << END_IDLE_BUS << " bus" << _myid;
					_vcdOutputState=END_IDLE_BUS;
					if (_posTrasactList == _transactList.end()) oNoMoreTrans=true;						
				}
				oSigChange=outp.str();
				return _previousTransEndTime;
			break;
			case INIT_BUS:
				if (aCurrTrans->getStartTimeOperation()!=0){
					_vcdOutputState=END_IDLE_BUS;
					outp << "r" << END_IDLE_BUS << " bus" << _myid;
					oSigChange=outp.str();
					return 0;
				}
			case END_IDLE_BUS:
				if (aCurrTrans->getCommand()->getTask()==aCurrTrans->getCommand()->getChannel()->getBlockedReadTask()){
					_vcdOutputState=END_READ_BUS;
					outp << "r" << END_READ_BUS << " bus" << _myid;
				}else{
					_vcdOutputState=END_WRITE_BUS;
					outp << "r" << END_WRITE_BUS << " bus" << _myid;
				}
				oSigChange=outp.str();
				return aCurrTrans->getStartTimeOperation();
			break;
		}
	}
}
