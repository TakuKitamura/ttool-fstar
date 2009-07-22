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
#include <TransactionListener.h>

Bus::Bus(unsigned int iID, std::string iName, TMLLength iBurstSize, unsigned int ibusWidth, TMLTime iTimePerSample): SchedulableCommDevice(iID, iName), _burstSize(iBurstSize), _nextTransaction(_transactionHash.end()), _schedulingNeeded(true), _timePerSample(iTimePerSample), _busWidth(ibusWidth), _busyCycles(0){
	//_myid=++_id;
	_transactList.reserve(BLOCK_SIZE);
}

Bus::~Bus(){
	/*BusTransPrioTab::iterator i;
	std::cout << "Remaining transactions in list of bus " << _name << ": " << _transactionQueue.size() << "\n";
	for (i=_transactionQueue.begin(); i!= _transactionQueue.end(); ++i){
		std::cout << i->second->toString() << "\n" ;
	}
	std::cout << "added: " << add << "  removed: " << remove << "  max size: " << _transactionHash.max_size() << std::endl;*/
}

void Bus::schedule(){
	TMLTransaction *aTempTrans;
	TMLTime aTransTimeFuture=-1;
	BusTransHashTab::iterator i, aTransToExecute=_transactionHash.end(), aFutureTrans=_transactionHash.end();
#ifdef DEBUG_BUS	
	std::cout << "Bus:schedule: start" << std::endl;
#endif
	unsigned int aTransPrio=-1,aTempPrio;
	for (i=_transactionHash.begin(); i != _transactionHash.end(); ++i){
		//std::cout << "0" << std::endl;
		aTempTrans=i->second;
		//std::cout << "1" << std::endl;
		if (aTempTrans->getStartTimeOperation()<=_endSchedule){
			//demand in the past
			//std::cout << "2" << std::endl;
			aTempPrio=i->first->getBusPriority(this);
			//std::cout << "2a" << std::endl;
			if (aTempPrio<aTransPrio){
				aTransToExecute=i;
				aTransPrio=aTempPrio;
			}
		}else{
			//demand in the future
			//std::cout << "3" << std::endl;
			if (aTempTrans->getStartTimeOperation()<aTransTimeFuture){
				aTransTimeFuture=aTempTrans->getStartTimeOperation();
				aFutureTrans=i;
			}
		}
	}
	if (aTransToExecute==_transactionHash.end()) aTransToExecute=aFutureTrans;
	if (aTransToExecute!=_transactionHash.end()){
		_nextTransaction=aTransToExecute;
		calcStartTimeLength();
		//FOR_EACH_TRANSLISTENER (*i)->transScheduled(_nextTransaction->second);
	}
	_schedulingNeeded=false;
#ifdef DEBUG_BUS
	if (_nextTransaction==_transactionHash.end())
		 std::cout << "Bus:schedule: decision of BUS " << _name << ": no transaction" << std::endl;
	else
		std::cout << "Bus:schedule: decision of BUS " << _name << ": " << _nextTransaction->second->toString() << std::endl;
#endif
}

void Bus::registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice){
	//std::cout << "within Bus::registerTransaction " << std::endl;
	if (iTrans==0){
		_transactionHash.erase(iSourceDevice);
	}else{
		_transactionHash[iSourceDevice]=iTrans;
	}
	_schedulingNeeded=true;
#ifdef DEBUG_BUS
	std::cout << "Bus:registerTrans: registered at bus " << _name << ": " << iTrans->toString() << std::endl;
#endif
	/*std::cout << "Remaining transactions in list of bus " << _name << ": " << _transactionQueue.size() << "\n";
	for (BusMasterPrioTab::iterator i=_transactionQueue.begin(); i!= _transactionQueue.end(); ++i){
		std::cout << i->second->toString() << "\n";
	}*/
}

bool Bus::addTransaction(){
	TMLTransaction* aNextTrans=_nextTransaction->second;
	_endSchedule = aNextTrans->getEndTime();
	_transactList.push_back(aNextTrans);
	_busyCycles += aNextTrans->getOperationLength();
#ifdef DEBUG_BUS
	std::cout << "Bus::addTrans: add trans at bus " << _name << ": " << aNextTrans->toString() << std::endl;
#endif
	_transactionHash.erase(_nextTransaction);
	_nextTransaction = _transactionHash.end();
	/*std::cout << "   size: " << _transactionQueue.size() << std::endl;
	std::cout << "Remaining transactions in list of bus " << _name << ": " << _transactionQueue.size() << "\n";
	for (BusTransPrioTab::iterator i=_transactionQueue.begin(); i!= _transactionQueue.end(); ++i){
		std::cout << i->second->toString() << "\n";
	}*/
	_schedulingNeeded=true;
	//FOR_EACH_TRANSLISTENER (*i)->transExecuted(aNextTrans);
	NOTIFY_TRANS_EXECUTED(aNextTrans);
	return true;
}

void Bus::calcStartTimeLength() const{
	TMLTransaction* aNextTrans=_nextTransaction->second;
	//aNextTrans->setStartTime(max(((int)_endSchedule)-((int)aNextTrans->getPenalties()),(int)aNextTrans->getStartTime()));
	aNextTrans->setStartTime(max(static_cast<int>(_endSchedule)-static_cast<int>(aNextTrans->getPenalties()),static_cast<int>(aNextTrans->getStartTime())));
	//TMLTime aLength = aNextTrans->getVirtualLength()*_timePerSample;
	//aLength = (aLength%_busWidth == 0)? aLength/_busWidth : aLength/_busWidth+1;
	TMLTime aLength = aNextTrans->getVirtualLength();
	aLength = (aLength%_busWidth == 0)? (aLength/_busWidth)*_timePerSample : (aLength/_busWidth + 1)*_timePerSample;
	aNextTrans->setLength(max(aLength, aNextTrans->getOperationLength()));
	Slave* aSlave = aNextTrans->getChannel()->getNextSlave(aNextTrans);
	if (aSlave!=0) aSlave->CalcTransactionLength(aNextTrans);
	//_nextTransaction->setLength(max(_endSchedule,_nextTransaction->getStartTime())-_nextTransaction->getStartTime()+_nextTransaction->getVirtualLength());
}

TMLTransaction* Bus::getNextTransaction(){
	if (_schedulingNeeded) schedule();
	return (_nextTransaction==_transactionHash.end())?0:_nextTransaction->second;
}

TMLLength Bus::getBurstSize() const{
	return _burstSize;
}

void Bus::truncateToBurst(TMLTransaction* iTrans) const{
	iTrans->setVirtualLength(min(iTrans->getVirtualLength(), _burstSize));
}

std::string Bus::toString() const{
	return _name;
}

std::string Bus::toShortString() const{
	std::ostringstream outp;
	outp << "bus" << _ID;
	return outp.str();
}

void Bus::schedule2HTML(std::ofstream& myfile) const{
	TMLTime aCurrTime=0;
	TMLTransaction* aCurrTrans;
	unsigned int aBlanks,aLength,aColor;
	if (_transactList.empty()) return;
	myfile << "<h2><span>Scheduling for device: "<< _name <<"</span></h2>\n<table>\n<tr>";
	for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
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

void Bus::schedule2TXT(std::ofstream& myfile) const{
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
		outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << " bus" << _ID;
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
					outp << VCD_PREFIX << vcdValConvert(END_WRITE_BUS) << " bus" << _ID;
					_vcdOutputState=END_WRITE_BUS;
				}else{
					outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << " bus" << _ID;
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
					outp << VCD_PREFIX << vcdValConvert(END_READ_BUS) << " bus" << _ID;
					_vcdOutputState=END_READ_BUS;
				}else{
					outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << " bus" << _ID;
					_vcdOutputState=END_IDLE_BUS;
					if (_posTrasactList == _transactList.end()) oNoMoreTrans=true;						
				}
				oSigChange=outp.str();
				return _previousTransEndTime;
			break;
			case INIT_BUS:
				if (aCurrTrans->getStartTimeOperation()!=0){
					_vcdOutputState=END_IDLE_BUS;
					outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << " bus" << _ID;
					oSigChange=outp.str();
					return 0;
				}
			case END_IDLE_BUS:
				if (aCurrTrans->getCommand()->getTask()==aCurrTrans->getCommand()->getChannel()->getBlockedReadTask()){
					_vcdOutputState=END_READ_BUS;
					outp << VCD_PREFIX << vcdValConvert(END_READ_BUS) << " bus" << _ID;
				}else{
					_vcdOutputState=END_WRITE_BUS;
					outp << VCD_PREFIX << vcdValConvert(END_WRITE_BUS) << " bus" << _ID;
				}
				oSigChange=outp.str();
				return aCurrTrans->getStartTimeOperation();
			break;
		}
	}
}

void Bus::reset(){
	//std::cout << "Bus reset" << std::endl;
	SchedulableDevice::reset();
	_nextTransaction=_transactionHash.end();
	_schedulingNeeded=true;
	_transactionHash.clear();
	_transactList.clear();
	_busyCycles=0;
}

void Bus::streamBenchmarks(std::ostream& s) const{
	s << TAG_BUSo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl; 
	if (_simulatedTime!=0) s << TAG_UTILo << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << TAG_UTILc;
	s << TAG_BUSc;
}

void Bus::streamStateXML(std::ostream& s) const{
	streamBenchmarks(s);
}