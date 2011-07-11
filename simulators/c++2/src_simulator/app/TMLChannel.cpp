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
#include <BusMaster.h>
#include <TMLCommand.h>
#include <TMLTransaction.h>

TMLChannel::TMLChannel(ID iID, std::string iName, unsigned int iWidth, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, Priority iPriority): _ID(iID), _name(iName), _width(iWidth), _readTask(0), _writeTask(0), _writeTrans(0), _readTrans(0),_numberOfHops(iNumberOfHops), _masters(iMasters), _slaves(iSlaves), _writeTransCurrHop(0), _readTransCurrHop(iNumberOfHops-1), _priority(iPriority), _significance(0){
}

TMLChannel::~TMLChannel(){
	if (_masters!=0) delete[] _masters;
	if (_slaves!=0) delete[] _slaves;
}

BusMaster* TMLChannel::getNextMaster(TMLTransaction* iTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
	if (iTrans==_writeTrans){
		_writeTransCurrHop++;
		//if (_writeTransCurrHop>0 && _masters[_writeTransCurrHop]->getBus()==_masters[_writeTransCurrHop-1]->getBus()) return 0;
		if (_writeTransCurrHop>0 && (_masters[_writeTransCurrHop]->getBus()==_masters[_writeTransCurrHop-1]->getBus() || _slaves[_writeTransCurrHop-1]==0)) return 0; //NEW!!!
		return _masters[_writeTransCurrHop];
	}else{
		_readTransCurrHop--;
		//if (_readTransCurrHop<_numberOfHops-1 && _masters[_readTransCurrHop]->getBus()==_masters[_readTransCurrHop+1]->getBus()) return 0;
		if (_readTransCurrHop<_numberOfHops-1 && _masters[_readTransCurrHop]->getBus()==_masters[_readTransCurrHop+1]->getBus()) return 0;
		return _masters[_readTransCurrHop];
	}
}

BusMaster* TMLChannel::getFirstMaster(TMLTransaction* iTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
	//std::cout << "fima 1\n";
	if (_masters==0 || _slaves==0 || _numberOfHops==0) return 0;
	//std::cout << "fima 2\n";
	if (iTrans==_writeTrans){
	//if (iTrans->getCommand()->getTask()==_writeTask){
		//std::cout << "fima 3\n";
		_writeTransCurrHop=0;
		return _masters[_writeTransCurrHop];
	}else{
		//std::cout << "fima 4\n";
		if (_slaves[(_numberOfHops/2)]==0) return 0;	//NEW!!!
		//std::cout << "fima 5\n";
		_readTransCurrHop=_numberOfHops-1;
		return _masters[_readTransCurrHop];
	}
}
	
Slave* TMLChannel::getNextSlave(TMLTransaction* iTrans) const{
	//if (iTrans->getCommand()->getTask()==_writeTask){
	if (iTrans==_writeTrans){
		return _slaves[_writeTransCurrHop];
	}else{
		return _slaves[_readTransCurrHop];
	}
}

std::ostream& TMLChannel::writeObject(std::ostream& s){
	//WRITE_STREAM(s,_writeTransCurrHop);
	//WRITE_STREAM(s,_readTransCurrHop);
	//if (_ID==53 && _significance==0) std::cout << "failure before write\n";
	WRITE_STREAM(s, _significance);
	return s;
}
std::istream& TMLChannel::readObject(std::istream& s){
	//READ_STREAM(s,_writeTransCurrHop);
	//READ_STREAM(s,_readTransCurrHop);
	READ_STREAM(s, _significance);
	//if (_ID==53 && _significance==0) std::cout << "failure after read\n";
	//std::cout << "read\n";
	return s;
}

void TMLChannel::reset(){
	//std::cout << "Channel reset" << std::endl;
	_writeTrans=0;
	_readTrans=0;
	_writeTransCurrHop=0;
	_readTransCurrHop=_numberOfHops-1;
	_significance=0;
	//std::cout << "reset\n";
	//std::cout << "Channel reset end" << std::endl;
}

void TMLChannel::setSignificance(TMLTask* iTask, bool iSignificance){
	//unsigned int aInput = (iTask==_writeTask)?1:2;
	unsigned char aInput = (iTask==_writeTask)?1:2;
	if (iSignificance)
		_significance |= aInput;
	else
		_significance &= (~aInput);
}
