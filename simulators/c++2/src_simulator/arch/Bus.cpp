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
//#include <TransactionListener.h>
#include <WorkloadSource.h>

Bus::Bus(ID iID, std::string iName, WorkloadSource* iScheduler, TMLLength iBurstSize, unsigned int ibusWidth, TMLTime iTimePerSample, bool iChannelBasedPrio): SchedulableCommDevice(iID, iName, iScheduler, iChannelBasedPrio), _burstSize(iBurstSize), _schedulingNeeded(true), _timePerSample(iTimePerSample), _busWidth(ibusWidth){}

Bus::~Bus(){
	//delete _scheduler;
	//std::cout << _transactList.size() << " elements in List of " << _name << ", busy cycles: " << _busyCycles << std::endl;
	//for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
	//	std::cout << (*i)->toString() << "\n";
	//}
}

//Determines the next bus transaction to be executed (_nextTransaction) and
//computes the start time and length
void Bus::schedule(){
	//Pointer to the next transaction to be executed
	_nextTransaction=0;
	//Get the end time of the last scheduled transaction. _scheduler is a member
	//variable and schedule a member function of different classes that mask the
	//type of scheduler
	TMLTime aTimeSlice = _scheduler->schedule(_endSchedule);

	_nextTransaction=_scheduler->getNextTransaction(_endSchedule);
	if (_nextTransaction!=0){
		//_scheduler->transWasScheduled();
		//Sets the virtual length of the transaction (number of execution units already carried out by previous transactions)
		_nextTransaction->setVirtualLength(min(_nextTransaction->getVirtualLength(), _burstSize));
		calcStartTimeLength(aTimeSlice);
	}
	_schedulingNeeded=false;
#ifdef DEBUG_BUS
	if (_nextTransaction==0)
		 std::cout << "Bus:schedule: decision of BUS " << _name << ": no transaction" << std::endl;
	else
		std::cout << "Bus:schedule: decision of BUS " << _name << ": " << _nextTransaction->toString() << std::endl;
#endif
}

//void Bus::registerTransaction(){
//	_schedulingNeeded=true;
//	_nextTransaction=0;
//}


//Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
bool Bus::addTransaction(TMLTransaction* iTransToBeAdded){
	//std::cout << "Bus add trans " << _nextTransaction << "\n";
	_endSchedule = _nextTransaction->getEndTime();
	//std::cout << "set end time to " << _endSchedule << "\n";
	//_transactList.push_back(_nextTransaction);
#ifdef TRANSLIST_ENABLED
	_transactList.push_back(iTransToBeAdded);  //NEW!!!!!
#endif
	_busyCycles += _nextTransaction->getOperationLength();
#ifdef DEBUG_BUS
	std::cout << "Bus::addTrans: add trans at bus " << _name << ": " << _nextTransaction->toString() << std::endl;
#endif
#ifdef LISTENERS_ENABLED
	NOTIFY_TRANS_EXECUTED(_nextTransaction);
#endif
	_nextTransaction = 0;
	_schedulingNeeded=true;
	//std::cout << "End Bus add trans\n";
	return true;
}

//Calculates the start time and the length of the next transaction
void Bus::calcStartTimeLength(TMLTime iTimeSlice) const{
  TMLTime tmp1 = static_cast<TMLTime>(_endSchedule);
  TMLTime tmp2  = static_cast<TMLTime>(_nextTransaction->getPenalties());
  TMLTime tmp3 = static_cast<TMLTime>(_nextTransaction->getStartTime());
  //std::cout << "BUS   ------------- tmp1:" << tmp1 << " tmp2:" << tmp2 << " tmp3:" << tmp3 << "\n";
  if (tmp1 < tmp2) { tmp1 = tmp2;}
  
  
  //_nextTransaction->setStartTime(max(tmp1+tmp2, tmp3));
  _nextTransaction->setStartTime(max(tmp1+tmp2, tmp3));
	
	//if (_nextTransaction->getOperationLength()!=-1){
	if (iTimeSlice!=0){
		_nextTransaction->setVirtualLength(max(min(_nextTransaction->getVirtualLength(), iTimeSlice *_busWidth/_timePerSample),(TMLTime)1));
	}
	TMLTime aLength = _nextTransaction->getVirtualLength();
	
	aLength = (aLength%_busWidth == 0)? (aLength/_busWidth)*_timePerSample : (aLength/_busWidth + 1)*_timePerSample;
	_nextTransaction->setLength(max(aLength, _nextTransaction->getOperationLength()));
	//_nextTransaction->setLength(aLength);  //TODO: this is not correct if speed of buses differ, max should be taken

	Slave* aSlave = _nextTransaction->getChannel()->getNextSlave(_nextTransaction);
	if (aSlave!=0) aSlave->CalcTransactionLength(_nextTransaction);
}

std::string Bus::toShortString() const{
	std::ostringstream outp;
	//outp << "bus" << _ID;
	outp << "bus" << _name;
	return outp.str();
}

// Issue #4: Moved to SchedulableDevice for easier maintenance
//Writes a HTML representation of the schedule to an output file
//void Bus::schedule2HTML(std::ofstream& myfile) const{
//	TMLTime aCurrTime = 0;
//	TMLTransaction* aCurrTrans;
//	unsigned int aBlanks, aLength;//,aColor;
//
//	std::map<TMLTask*, std::string> taskColors;
//	unsigned int nextColor = 0;
//
//	if ( _transactList.empty( )) {
//		return;
//	}
//
//	myfile << "<h2><span>Scheduling for device: "<< _name <<"</span></h2>\n<table>\n<tr>";
//
//	for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
//		aCurrTrans = *i;
//		//if (aCurrTrans->getVirtualLength()==0) continue;
//		aBlanks = aCurrTrans->getStartTimeOperation() - aCurrTime;
//
//		if ( aBlanks > 0 ) {
//
//			// Issue #4
//			writeActivityRow( myfile, aBlanks, "not", "idle time" );
////			if (aBlanks==1)
////				myfile << "<td title=\"idle time\" class=\"not\"></td>\n";
////			else
////				myfile << "<td colspan=\""<< aBlanks <<"\" title=\"idle time\" class=\"not\"></td>\n";
//		}
//
//		aLength = aCurrTrans->getOperationLength();
//
//		// Issue #4
//	    TMLTask* task = aCurrTrans->getCommand()->getTask();
//	    const std::string cellClass = taskColor( taskColors, task, nextColor );
//		//unsigned int instNumber = aCurrTrans->getCommand()->getTask()->getInstanceNo() - 1;
//		//aColor = instNumber % NB_HTML_COLORS;
////	    std::ostringstream cellClass;
////	    cellClass << "t" << aColor;
//
//		writeActivityRow( myfile, aLength, cellClass, aCurrTrans->toShortString() );
////
////		if ( aLength==1 ) {
////			myfile << "<td title=\""<< aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
////		}
////		else {
////			myfile << "<td colspan=\"" << aLength << "\" title=\"" << aCurrTrans->toShortString() << "\" class=\"t"<< aColor <<"\"></td>\n";
////		}
//
//		aCurrTime = aCurrTrans->getEndTime();
//	}
//
//	myfile << "</tr>\n<tr>";
//
//	for ( aLength = 0; aLength < aCurrTime; aLength++ ) {
//		myfile << "<th></th>";
//	}
//
//	myfile << "</tr>\n<tr>";
//
//	for ( aLength = 0; aLength <= aCurrTime; aLength += 5 ) {
//		std::ostringstream spanVal;
//		spanVal << aLength;
//		writeActivityRow( myfile, 5, "sc", "", spanVal.str(), false );
////		myfile << "<td colspan=\"5\" class=\"sc\">" << aLength << "</td>";
//	}
//
//	myfile << "</tr>\n</table>\n";
//}

//Writes a plain text representation of the schedule to an output file
void Bus::schedule2TXT(std::ofstream& myfile) const{
	myfile << "========= Scheduling for device: "<< _name << " =========\n" ;
	for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
		myfile << (*i)->toShortString() << std::endl;
	}
}

int Bus::allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const {
  int size = _transactList.size();
  int begining = size - maxNbOfTrans;
  if (begining <0) {
    begining = 0;
  }
  int cpt =0;
  int total = 0;
  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
    if (cpt >= begining) {
      (*i)->toXML(glob, 1, _name);
      total ++;
    }
    cpt ++;
  }
  return total;
}

void Bus::latencies2XML(std::ostringstream& glob, unsigned int id1, unsigned int id2) {

  for(TransactionList::const_iterator i=_transactList.begin(); i != _transactList.end(); ++i){
	if ((*i)->getCommand() !=NULL){
		if ((*i)->getCommand()->getID() == id1 || (*i)->getCommand()->getID() == id2){
		    (*i)->toXML(glob, 1, _name);
		}
	}
  }
  return;
}



//Returns the next signal change (for vcd output)
void Bus::getNextSignalChange(bool iInit, SignalChangeData* oSigData){
	//std::ostringstream outp;
	//std::cout << _transactList.size() << " elements in List of " << _name << std::endl;
	if (iInit){
		 _posTrasactListVCD=_transactList.begin();
		_previousTransEndTime=0;
		 _vcdOutputState=INIT_BUS;
	}
	if (_posTrasactListVCD == _transactList.end()){
		//outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << "bus" << _ID;
		//oSigChange=outp.str();
		//oNoMoreTrans=true;
		//return _previousTransEndTime;
		new (oSigData) SignalChangeData(END_IDLE_BUS, _previousTransEndTime, this);
	}else{
		TMLTransaction* aCurrTrans=*_posTrasactListVCD;
		//oNoMoreTrans=false;
		switch (_vcdOutputState){
			case END_READ_BUS:
				do{
					_previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
					_posTrasactListVCD++;
				}while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime && (*_posTrasactListVCD)->getCommand()->getTask()==(*_posTrasactListVCD)->getChannel()->getBlockedReadTask());
				if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime){
					//outp << VCD_PREFIX << vcdValConvert(END_WRITE_BUS) << "bus" << _ID;
					_vcdOutputState=END_WRITE_BUS;
					new (oSigData) SignalChangeData(END_WRITE_BUS, _previousTransEndTime, this);
				}else{
					//outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << "bus" << _ID;
					_vcdOutputState=END_IDLE_BUS;
					//if (_posTrasactListVCD == _transactList.end()) oNoMoreTrans=true;
					new (oSigData) SignalChangeData(END_IDLE_BUS, _previousTransEndTime, this);
				}
				//oSigChange=outp.str();
				//return _previousTransEndTime;
			break;
			case END_WRITE_BUS:
				do{
					_previousTransEndTime=(*_posTrasactListVCD)->getEndTime();
					_posTrasactListVCD++;
				}while (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime && (*_posTrasactListVCD)->getCommand()->getTask()==(*_posTrasactListVCD)->getChannel()->getBlockedWriteTask());
				if (_posTrasactListVCD != _transactList.end() && (*_posTrasactListVCD)->getStartTimeOperation()==_previousTransEndTime){
					//outp << VCD_PREFIX << vcdValConvert(END_READ_BUS) << "bus" << _ID;
					_vcdOutputState=END_READ_BUS;
					new (oSigData) SignalChangeData(END_READ_BUS, _previousTransEndTime, this);
				}else{
					//outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << "bus" << _ID;
					_vcdOutputState=END_IDLE_BUS;
					//if (_posTrasactListVCD == _transactList.end()) oNoMoreTrans=true;
					new (oSigData) SignalChangeData(END_IDLE_BUS, _previousTransEndTime, this);
				}
				//oSigChange=outp.str();
				//return _previousTransEndTime;
			break;
			case INIT_BUS:
				if (aCurrTrans->getStartTimeOperation()!=0){
					_vcdOutputState=END_IDLE_BUS;
					//outp << VCD_PREFIX << vcdValConvert(END_IDLE_BUS) << "bus" << _ID;
					//oSigChange=outp.str();
					new (oSigData) SignalChangeData(END_IDLE_BUS, 0, this);
					//return 0;
					return;
				}
			case END_IDLE_BUS:
				if (aCurrTrans->getCommand()->getTask()==aCurrTrans->getChannel()->getBlockedReadTask()){
					_vcdOutputState=END_READ_BUS;
					new (oSigData) SignalChangeData(END_READ_BUS, aCurrTrans->getStartTimeOperation(), this);
					//outp << VCD_PREFIX << vcdValConvert(END_READ_BUS) << "bus" << _ID;
				}else{
					_vcdOutputState=END_WRITE_BUS;
					new (oSigData) SignalChangeData(END_WRITE_BUS, aCurrTrans->getStartTimeOperation(), this);
					//outp << VCD_PREFIX << vcdValConvert(END_WRITE_BUS) << "bus" << _ID;
				}
				//oSigChange=outp.str();
				//return aCurrTrans->getStartTimeOperation();
			break;
		}
	}
	//return 0;
}

//Resets a simulation component to its initial state
void Bus::reset(){
	//std::cout << "Bus reset" << std::endl;
	_scheduler->reset();
	SchedulableDevice::reset();
	_nextTransaction=0;
	_schedulingNeeded=true;
	_transactList.clear();	//List containing all already scheduled transactions
	_busyCycles=0;
}

//Writes benchmarking data to a given stream
void Bus::streamBenchmarks(std::ostream& s) const{
	s << TAG_BUSo << " id=\"" << _ID << "\" name=\"" << _name << "\">" << std::endl; 
	if (_simulatedTime!=0) s << TAG_UTILo << (static_cast<float>(_busyCycles)/static_cast<float>(_simulatedTime)) << TAG_UTILc;
	s << TAG_BUSc;
}

//Deserializes the object
std::istream& Bus::readObject(std::istream &is){
	SchedulableDevice::readObject(is);
	_scheduler->readObject(is);
#ifdef SAVE_BENCHMARK_VARS
	READ_STREAM(is,_busyCycles);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: Bus " << _name << " busyCycles: " << _busyCycles << std::endl;
#endif
#endif
	return is;
}

//Serializes the object
std::ostream& Bus::writeObject(std::ostream &os){
	SchedulableDevice::writeObject(os);
	_scheduler->writeObject(os);
#ifdef SAVE_BENCHMARK_VARS
	WRITE_STREAM(os,_busyCycles);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: Bus " << _name << " busyCycles: " << _busyCycles << std::endl;
#endif
#endif
	return os;
}
