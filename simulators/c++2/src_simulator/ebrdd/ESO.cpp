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

#define NOTIFY_ABORT {_ancestorNode->notifyAbort(_ID); _aborted=true;}
#define NOTIFY_EVENT {_nbOfNotific++; _ancestorNode->notifyEvent(_ID);}
#define RETURN_IF_TERMINATED if (_nbOfNotific>0 || _aborted) return;


//************************************************************************
ESOConjunction::ESOConjunction(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _oncePerEvent(iOncePerEvent){
}

void ESOConjunction::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNegated() || (_oncePerEvent && _eventArray[iID]->getNbOfNotific()>1)){
		NOTIFY_ABORT;
	}else{	
		//all non negated events received?
		for (unsigned int i=0; i<_nbOfEvents; i++){
			//std::cout << i << "\n";
			if (!_eventArray[i]->notified() && !_eventArray[i]->getNegated()) return;
		}
		NOTIFY_EVENT;
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
	}else{
		NOTIFY_ABORT;
	}
}

void ESOConjunction::timeTick(TMLTime iNewTime){
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	if (iNewTime>=_timeOut){
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
	reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESOConjunction::deactivate(){
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
	if (iNewTime>=_timeOut){
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
	reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESODisjunction::deactivate(){
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
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	if (iNewTime>=_timeOut){
		if(_nextEvtToWaitFor==_nbOfEvents){
			NOTIFY_EVENT;
		}else{
			NOTIFY_ABORT;
		}
	}
}

void ESOSequence::activate(){
	reset();
	searchForNextEvt();
}

void ESOSequence::reset(){
	ESOIF::reset();
	_nextEvtToWaitFor=-1;
	_lastEvtToWaitFor=0;
}

void ESOSequence::deactivate(){
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
	reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
	searchForNextEvt();
}

void ESOSSequence::deactivate(){
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
ESOAtMost::ESOAtMost(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent, unsigned int iN): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _oncePerEvent(iOncePerEvent), _N(iN){}

void ESOAtMost::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNbOfNotific()>1){
		NOTIFY_ABORT;
		return;
	}
	checkEvents();
}

void ESOAtMost::notifyAbort(unsigned int iID){
	RETURN_IF_TERMINATED;
	checkEvents();
}

void ESOAtMost::timeTick(TMLTime iNewTime){
	RETURN_IF_TERMINATED;
	ESOIF::timeTick(iNewTime);
	if (iNewTime>=_timeOut){
		unsigned int aReceivedEvents=0;
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if ((_eventArray[i]->getNegated() && !_eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())) aReceivedEvents++;
			if (aReceivedEvents>_N){
				NOTIFY_ABORT;
				return;
			}
			
		}
		NOTIFY_EVENT;
	}
}

void ESOAtMost::activate(){
	reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESOAtMost::deactivate(){
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}

void ESOAtMost::checkEvents(){
	unsigned int aReceivedEvents=0;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if ((_eventArray[i]->getNegated() && _eventArray[i]->getAborted()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())) aReceivedEvents++;
		if (aReceivedEvents>_N){
			NOTIFY_ABORT;
			return;
		}
	}
	unsigned int aPossibleEvents=_nbOfEvents;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if ((_eventArray[i]->getNegated() && _eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->getAborted())) aPossibleEvents--;
		if (aPossibleEvents <=_N){
			NOTIFY_EVENT;
			return;
		}
	}
}


//************************************************************************
ESOAtLeast::ESOAtLeast(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent, unsigned int iN): ESOIF(iAncestorNode, iNegated, iNbOfEvents, iTimeOut), _oncePerEvent(iOncePerEvent), _N(iN){}

void ESOAtLeast::notifyEvent(unsigned int iID){
	RETURN_IF_TERMINATED;
	if (_eventArray[iID]->getNbOfNotific()>1){
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
	if (iNewTime>=_timeOut){
		unsigned int aReceivedEvents=0;
		for (unsigned int i=0; i<_nbOfEvents; i++){
			if ((_eventArray[i]->getNegated() && !_eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())) aReceivedEvents++;
			if (aReceivedEvents==_N){
				NOTIFY_EVENT;
				return;
			}
			
		}
		NOTIFY_ABORT;
	}
}

void ESOAtLeast::activate(){
	reset();
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->activate();
	}
}

void ESOAtLeast::deactivate(){
	for (unsigned int i=0; i<_nbOfEvents; i++){
		_eventArray[i]->deactivate();
	}
}


void ESOAtLeast::checkEvents(){
	unsigned int aReceivedEvents=0;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if ((_eventArray[i]->getNegated() && _eventArray[i]->getAborted()) || (!_eventArray[i]->getNegated() && _eventArray[i]->notified())) aReceivedEvents++;
		if (aReceivedEvents==_N){
			NOTIFY_EVENT;
			return;
		}
	}
	unsigned int aPossibleEvents=_nbOfEvents;
	for (unsigned int i=0; i<_nbOfEvents; i++){
		if ((_eventArray[i]->getNegated() && _eventArray[i]->notified()) || (!_eventArray[i]->getNegated() && _eventArray[i]->getAborted())) aPossibleEvents--;
		if (aPossibleEvents <_N){
			NOTIFY_ABORT;
			return;
		}
	}
}
