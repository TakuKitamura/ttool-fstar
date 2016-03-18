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

#include <EventIF.h>
#include <NotifyIF.h>
#include <ERB.h>

EventIF::EventIF(NotifyIF* iAncestorNode, bool iNegated):_ancestorNode(iAncestorNode), _negated(iNegated), _nbOfNotific(0), _aborted(false), _active(false){
	iAncestorNode->registerEvent(this);
}

void EventIF::setEventID(ID iID){
	_ID=iID;
	//std::cout << "setEventID: " << _ID << "\n";
}

bool EventIF::notified(){
	//std::cout << _ID << " notified?: " << (_nbOfNotific!=0) << "\n";
	return _nbOfNotific!=0;
}

unsigned int EventIF::getNbOfNotific(){
	//std::cout << "Number of notifications: " << _nbOfNotific << "\n";
	return _nbOfNotific;
}

bool EventIF::getNegated(){
	return _negated;
}

void EventIF::reset(){
	//std::cout << "Reset leaf\n";
	_nbOfNotific=0;
	_aborted=false;
 	_active=false;
	//std::cout << "End Reset leaf\n";
}

std::ostream& EventIF::writeObject(std::ostream& s){
	WRITE_STREAM(s, _nbOfNotific);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: EventIF nbOfNotific: " << _nbOfNotific << std::endl;
#endif
	WRITE_STREAM(s, _aborted);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: EventIF aborted: " << _aborted << std::endl;
#endif
	WRITE_STREAM(s, _active);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: EventIF active: " << _active << std::endl;
#endif
	return s;
}

std::istream& EventIF::readObject(std::istream& s){
	READ_STREAM(s, _nbOfNotific);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: EventIF nbOfNotific: " << _nbOfNotific << std::endl;
#endif
	READ_STREAM(s, _aborted);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: EventIF aborted: " << _aborted << std::endl;
#endif
	READ_STREAM(s, _active);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: EventIF active: " << _active << std::endl;
#endif
	return s;
}

bool EventIF::getAborted(){
	return _aborted;
}

EventIF::~EventIF(){
}
