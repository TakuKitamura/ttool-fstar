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

#include <TMLChannel.h>
#include <Bus.h>
#include <TMLCommand.h>
#include <TMLTransaction.h>

TMLChannel::TMLChannel(std::string iName, unsigned int iNumberOfHops, SchedulableCommDevice** iBuses, Slave** iSlaves):_name(iName), _readTask(0), _writeTask(0), _writeTrans(0), _readTrans(0),_numberOfHops(iNumberOfHops), _buses(iBuses), _slaves(iSlaves), _writeTransCurrHop(0), _readTransCurrHop(iNumberOfHops-1){
}

TMLChannel::~TMLChannel(){
	if (_buses!=0) delete[] _buses;
	if (_slaves!=0) delete[] _slaves;
}

void TMLChannel::setBlockedReadTask(TMLTask* iReadTask){
	_readTask=iReadTask;
}

void TMLChannel::setBlockedWriteTask(TMLTask* iWriteTask){
	_writeTask=iWriteTask;
}

SchedulableCommDevice* TMLChannel::getNextBus(TMLTransaction* iTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
	if (iTrans==_writeTrans){
		_writeTransCurrHop++;
		if (_writeTransCurrHop>0 && _buses[_writeTransCurrHop]==_buses[_writeTransCurrHop-1]) return 0;
		return _buses[_writeTransCurrHop];
	}else{
		_readTransCurrHop--;
		if (_readTransCurrHop<_numberOfHops-1 && _buses[_readTransCurrHop]==_buses[_readTransCurrHop+1]) return 0;
		return _buses[_readTransCurrHop];
	}
}

SchedulableCommDevice* TMLChannel::getFirstBus(TMLTransaction* iTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
	if (_buses==0 || _slaves==0 || _numberOfHops==0) return 0;
	if (iTrans==_writeTrans){
		_writeTransCurrHop=0;
		return _buses[_writeTransCurrHop];
	}else{
		_readTransCurrHop=_numberOfHops-1;
		return _buses[_readTransCurrHop];
	}
}
	
Slave* TMLChannel::getNextSlave(TMLTransaction* iTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
	if (iTrans==_writeTrans){
		return _slaves[_writeTransCurrHop];
	}else{
		return _slaves[_readTransCurrHop];
	}
}

//void TMLChannel::switchToNextBus(TMLTransaction* iTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
//	if (iTrans==_writeTrans){
		//_writeTransCurrHop++;
//	}else{
		//_readTransCurrHop--;
//	}
//}


//SchedulableCommDevice* TMLChannel::getBus(unsigned int iIndex) const{
//	if (_buses==0 || iIndex>=_numberOfHops) return 0;
//	return _buses[iIndex];
//}


//unsigned int TMLChannel::getNumberOfHops() const{
//	return _numberOfHops;
//}

std::string TMLChannel::toShortString(){
	return _name;
}

std::ostream& TMLChannel::writeObject(std::ostream& s){
	WRITE_STREAM(s,_writeTransCurrHop);
	WRITE_STREAM(s,_readTransCurrHop);
	return s;
}
std::istream& TMLChannel::readObject(std::istream& s){
	READ_STREAM(s,_writeTransCurrHop);
	READ_STREAM(s,_readTransCurrHop);
	return s;
}
