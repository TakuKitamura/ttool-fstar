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

#include <TMLEventChannel.h>

TMLEventChannel::TMLEventChannel(unsigned int iID, std::string iName, unsigned int iNumberOfHops, SchedulableCommDevice** iBuses, Slave** iSlaves, TMLLength iContent): TMLStateChannel(iID, iName, iNumberOfHops, iBuses, iSlaves, iContent),_tmpParam(0,0,0){
}

TMLEventChannel::~TMLEventChannel(){
	/*ParamQueue::iterator i;
	for(i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
		delete (*i);
	}*/
}

TMLLength TMLEventChannel::getContent() const{
	return _content;
}

bool TMLEventChannel::getRequestChannel() const{
	return false;
}

std::ostream& TMLEventChannel::writeObject(std::ostream& s){
	ParamQueue::iterator i;
	std::cout << "write size of channel " << _name << " :" << _content << std::endl;
	TMLStateChannel::writeObject(s);
	for(i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
		i->writeObject(s);
	}
	//for_each( _paramQueue.begin(), _paramQueue.end(), std::bind2nd(std::bind1st(std::mem_fun(&(Parameter<ParamType>::writeObject)),s),(unsigned int)_writeTask));
	return s;
}

std::istream& TMLEventChannel::readObject(std::istream& s){
	TMLLength aParamNo;
	ParamQueue::iterator i;
	//Parameter<ParamType>* aNewParam;
	TMLStateChannel::readObject(s);
	std::cout << "Read Object TMLEventChannel " << _name << std::endl;
	//std::cout << "read new size of channel " << _name << " :" << _content << std::endl;
	//for(i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
	//	delete (*i);
	//}
	//_paramQueue.clear();
	for(aParamNo=0; aParamNo < _content; aParamNo++){
		//aNewParam = new Parameter<ParamType>(s, (unsigned int) _writeTask);
		_paramQueue.push_back(Parameter<ParamType>(s));
	}
	return s;
}

void TMLEventChannel::print() const{
	for(ParamQueue::const_iterator i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
		i->print();
	}
}

void TMLEventChannel::reset(){
	//std::cout << "EventChannel reset" << std::endl;
	TMLStateChannel::reset();
	//for(ParamQueue::iterator i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
	//	delete (*i);
	//}
	_paramQueue.clear();
	//std::cout << "EventChannel reset end" << std::endl; 
	//_tmpParam=Parameter<ParamType>(0,0,0);
}
