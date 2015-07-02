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

#include <ESO.h>
#include <SchedulableDevice.h>

//std::cout << "Abort: " << _ID << "\n";
#define NOTIFY_ABORT {_aborted=true; _ancestorNode->notifyAbort(_ID);}
//std::cout << "Notify: " << _ID << "\n";
#define NOTIFY_EVENT {_nbOfNotific++; _ancestorNode->notifyEvent(_ID);}
//omit _nbOfNotific>0
#define RETURN_IF_TERMINATED if (_nbOfNotific>0 || _aborted ) return;
#define RETURN_IF_NO_TIMEOUT if (_timeOut==0 || !_active) return;
#define RETURN_IF_ACTIVE if (_active) return;

//************************************************************************
ESOConjunction::ESOConjunction(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _oncePerEvent(iOncePerEvent){
}

void ESOConjunction::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNegated() || (_oncePerEvent && _eventArray[iID]->getNbOfNotific()>1)){
		NOTIFY_ABORT;
		//std::cout << "Abort Conjuction (negated or too much): " << _ID << "\n";
	}else{	
		//all non negated events received?
		for (unsigned int i=0; i<_nbOfEvents; i++){
			//std::cout << i << "\n";
			if (!_eventArray[i]->notified() && !_eventArray[i]->getNegated()) return;
		}
		NOTIFY_EVENT;
		//std::cout << "Notify Conjuction: " << _ID << "\n";
	}
}

void ESOConjunction::notifyAbort(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNegated()){
		//all events negated and aborted?
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if (!_eventArray[i]->getNegated() || !_eventArray[i]->getAborted()) return;
		}
		NOTIFY_EVENT;
		//std::cout << "Notify Conjuction: " << _ID << "\n";
	}else{
		NOTIFY_ABORT;
		//std::cout << "Abort Conjuction (abort received): " << _ID << "\n";
	}
}

void ESOConjunction::timeTick(TMLTime iNewTime){
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	RETURN_IF_NO_TIMEOUT;
	if (iNewTime>=_timeOut + _startTime){
		//all events negated?
		for (unsigned int i=0; i<_nbOfEvents; i++){
				if (!_eventArray[i]->getNegated()){
				NOTIFY_ABORT;
				return;
			}
		}
		//if all events are negated
		NOTIFY_EVENT;
	}
}

void ESOConjunction::activate(){
	RETURN_IF_ACTIVE;
	_active=true;
	_startTime = SchedulableDevice::getSimulatedTime();
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESOConjunction::deactivate(){
	_active=false;
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}


//************************************************************************
ESODisjunction::ESODisjunction(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut){
}

void ESODisjunction::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNegated()){
		//test if all norm events are aborted, all neg events notified 
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if ((!_eventArray[i]->getNegated() && !_eventArray[i]->getAborted()) || (_eventArray[i]->getNegated() && !_eventArray[i]->notified()))  return;
		}
		NOTIFY_ABORT;
	}else{
		NOTIFY_EVENT;
	}
}

void ESODisjunction::notifyAbort(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNegated()){
		NOTIFY_EVENT;
	}else{
		//test if all norm events are aborted, all neg events notified 
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if ((!_eventArray[i]->getNegated() && !_eventArray[i]->getAborted()) || (_eventArray[i]->getNegated() && !_eventArray[i]->notified()))  return;
		}
		NOTIFY_ABORT;
	}
}

void ESODisjunction::timeTick(TMLTime iNewTime){
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	RETURN_IF_NO_TIMEOUT;
	if (iNewTime>=_timeOut + _startTime){
		for (unsigned int i=0; i<_nbOfEvents; i++){
			//was negated event not received?
			if (_eventArray[i]->getNegated() && !_eventArray[i]->notified()){
				NOTIFY_EVENT;
				return;
			}
		}
		NOTIFY_ABORT;
	}
}

void ESODisjunction::activate(){
	RETURN_IF_ACTIVE;
	_active=true;
	_startTime = SchedulableDevice::getSimulatedTime();
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESODisjunction::deactivate(){
	_active=false;
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}


//************************************************************************
ESOSequence::ESOSequence(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _nextEvtToWaitFor(-1), _lastEvtToWaitFor(0){
}

void ESOSequence::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (iID==_nextEvtToWaitFor){
		if (searchForNextEvt()==-1) NOTIFY_EVENT;
	}else{
		NOTIFY_ABORT;
	}
		
}

void ESOSequence::notifyAbort(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (iID==_nextEvtToWaitFor){
		NOTIFY_ABORT;
	}else{
		if(_nextEvtToWaitFor==_nbOfEvents){
			//Only negated and aborted events at the end of the list?
			for(unsigned int i=_nextEvtToWaitFor-1; i>=0 && _eventArray[i]->getNegated(); i--){
				if (!_eventArray[i]->getAborted()) return;
			}
			NOTIFY_EVENT;
		}
	}
}

void ESOSequence::timeTick(TMLTime iNewTime){
	//std::cout << "check time tick0\n";
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	//if (_active)
	//	std::cout << "check time tick1 true" << "\n";
	//else
	//	std::cout << "check time tick1 false" << "\n";
	RETURN_IF_NO_TIMEOUT;
	//std::cout << "check time tick2\n";
	if (iNewTime>=_timeOut + _startTime){
		if(_nextEvtToWaitFor==_nbOfEvents){
			NOTIFY_EVENT;
		}else{
			NOTIFY_ABORT;
		}
	}
}

void ESOSequence::activate(){
	RETURN_IF_ACTIVE;
	_active=true;
	_startTime = SchedulableDevice::getSimulatedTime();
	//reset();
	searchForNextEvt();
}

void ESOSequence::reset(){
	//std::cout << "Sequence reset\n";
	ESOIF::reset();
	_nextEvtToWaitFor=-1;
	_lastEvtToWaitFor=0;
}

std::ostream& ESOSequence::writeObject(std::ostream& s){
	ESOIF::writeObject(s);
	WRITE_STREAM(s, _nextEvtToWaitFor);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: ESOSequence nextEvtToWaitFor: " << _nextEvtToWaitFor << std::endl;
#endif
	WRITE_STREAM(s, _lastEvtToWaitFor);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: ESOSequence lastEvtToWaitFor: " << _lastEvtToWaitFor << std::endl;
#endif
	return s;
}

std::istream& ESOSequence::readObject(std::istream& s){
	ESOIF::readObject(s);
	READ_STREAM(s, _nextEvtToWaitFor);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: ESOSequence nextEvtToWaitFor: " << _nextEvtToWaitFor << std::endl;
#endif
	READ_STREAM(s, _lastEvtToWaitFor);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: ESOSequence lastEvtToWaitFor: " << _lastEvtToWaitFor << std::endl;
#endif
	return s;
}

void ESOSequence::deactivate(){
	_active=false;
	//reset();
	if (_nextEvtToWaitFor!=-1){
		for (unsigned int i=_lastEvtToWaitFor; i< std::min(_nextEvtToWaitFor+1, _nbOfEvents); i++){
			_eventArray[i]->deactivate();
			//std::cout << "[end] deactivate event: " << i << "\n";
		}
	}
}

int ESOSequence::searchForNextEvt(){
	deactivate();
	_nextEvtToWaitFor++;
	if (_nextEvtToWaitFor>=_nbOfEvents) return -1;
	unsigned int aNextPos;
	for (aNextPos=_nextEvtToWaitFor; aNextPos<_nbOfEvents; aNextPos++){
		_eventArray[aNextPos]->activate();
		//std::cout << "activate event: " << aNextPos << "\n";
		if (!_eventArray[aNextPos]->getNegated()) break;
	}
	_lastEvtToWaitFor=_nextEvtToWaitFor;
	_nextEvtToWaitFor=aNextPos;
	//std::cout << "next event to wait for: " << _nextEvtToWaitFor << "\n";
	return 0;
}


//************************************************************************
ESOSSequence::ESOSSequence(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut): ESOSequence(iAncestorNode, iNegated, iNbOfEvents, iTimeOut){
}

void ESOSSequence::activate(){
	//std::cout << "activate SSeq1\n";
	RETURN_IF_ACTIVE;
	//std::cout << "activate SSeq2\n";
	_active=true;
	_startTime = SchedulableDevice::getSimulatedTime();
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
	searchForNextEvt();
}

void ESOSSequence::deactivate(){
	_active=false;
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}

int ESOSSequence::searchForNextEvt(){
	_nextEvtToWaitFor++;
	if (_nextEvtToWaitFor>=_nbOfEvents) return -1;
	unsigned int aNextPos;
	for (aNextPos=_nextEvtToWaitFor; aNextPos<_nbOfEvents; aNextPos++){
		if (!_eventArray[aNextPos]->getNegated()) break;
	}
	_lastEvtToWaitFor=_nextEvtToWaitFor;
	_nextEvtToWaitFor=aNextPos;
	//std::cout << "next event to wait for: " << _nextEvtToWaitFor << "\n";
	return 0;
}	


//************************************************************************
ESOAtMost::ESOAtMost(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent, unsigned int iN): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _oncePerEvent(iOncePerEvent), _n(iN){}

void ESOAtMost::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_oncePerEvent && _eventArray[iID]->getNbOfNotific()>1){
		NOTIFY_ABORT;
		return;
	}
	checkEvents();
}

void ESOAtMost::notifyAbort(unsigned int iID){
	RETURN_IF_TERMINATED;
	//std::cout << "At most received abort " << _ID << "\n";
	checkEvents();
}

void ESOAtMost::timeTick(TMLTime iNewTime){
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	RETURN_IF_NO_TIMEOUT;
	if (iNewTime>=_timeOut + _startTime){
		unsigned int aReceivedEvents=0;
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if ((_eventArray[i]->getNegated() && !_eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())) aReceivedEvents++;
			if (aReceivedEvents>_n){
				NOTIFY_ABORT;
				return;
			}
			
		}
		NOTIFY_EVENT;
	}
}

void ESOAtMost::activate(){
	RETURN_IF_ACTIVE;
	_active=true;
	_startTime = SchedulableDevice::getSimulatedTime();
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESOAtMost::deactivate(){
	_active=false;
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}

void ESOAtMost::checkEvents(){
	unsigned int aReceivedEvents=0;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if ((_eventArray[i]->getNegated() && _eventArray[i]->getAborted()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())){
			if (_oncePerEvent || _eventArray[i]->getNegated())
			 	aReceivedEvents++;
			else
				aReceivedEvents+=_eventArray[i]->getNbOfNotific();
		}
		// aReceivedEvents++;
		if (aReceivedEvents>_n){
			NOTIFY_ABORT;
			//std::cout << "Abort at most " << _ID << "\n";
			return;
		}
	}
	unsigned int aPossibleEvents=_nbOfEvents;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if (!_oncePerEvent && !_eventArray[i]->getNegated() && !_eventArray[i]->getAborted()){
			//std::cout << "Return from notify at most " << _ID << "\n";
		 	return;  //NEW
		}
		if ((_eventArray[i]->getNegated() && _eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->getAborted())) aPossibleEvents--;
		if (aPossibleEvents <=_n){
			//std::cout << "Notify at most " << _ID << "\n";
			NOTIFY_EVENT;
			return;
		}
	}
}


//************************************************************************
ESOAtLeast::ESOAtLeast(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent, unsigned int iN): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _oncePerEvent(iOncePerEvent), _n(iN){}

void ESOAtLeast::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_oncePerEvent && _eventArray[iID]->getNbOfNotific()>1){
		NOTIFY_ABORT;
		return;
	}
	checkEvents();
}

void ESOAtLeast::notifyAbort(unsigned int iID){
	RETURN_IF_TERMINATED;
	checkEvents();
}

void ESOAtLeast::timeTick(TMLTime iNewTime){
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	RETURN_IF_NO_TIMEOUT;
	if (iNewTime>=_timeOut + _startTime){
		unsigned int aReceivedEvents=0;
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if ((_eventArray[i]->getNegated() && !_eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())) aReceivedEvents++;
			if (aReceivedEvents==_n){
				NOTIFY_EVENT;
				return;
			}
			
		}
		NOTIFY_ABORT;
	}
}

void ESOAtLeast::activate(){
	RETURN_IF_ACTIVE;
	_active=true;
	_startTime = SchedulableDevice::getSimulatedTime();
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESOAtLeast::deactivate(){
	_active=false;
	//reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}


void ESOAtLeast::checkEvents(){
	unsigned int aReceivedEvents=0;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if ((_eventArray[i]->getNegated() && _eventArray[i]->getAborted()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())){
			if (_oncePerEvent || _eventArray[i]->getNegated())
			 	aReceivedEvents++;
			else
				aReceivedEvents+=_eventArray[i]->getNbOfNotific();
		}
		if (aReceivedEvents==_n){
			NOTIFY_EVENT;
			return;
		}
	}
	unsigned int aPossibleEvents=_nbOfEvents;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if (!_oncePerEvent && !_eventArray[i]->getNegated() && !_eventArray[i]->getAborted()) return;
		if ((_eventArray[i]->getNegated() && _eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->getAborted())) aPossibleEvents--;
		if (aPossibleEvents <_n){
			NOTIFY_ABORT;
			//std::cout << "Abort at least " << _ID << "\n";
			return;
		}
	}
}
