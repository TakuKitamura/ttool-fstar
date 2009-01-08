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

#include <TMLEventFBChannel.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>

TMLEventFBChannel::TMLEventFBChannel(std::string iName, unsigned int iNumberOfHops, SchedulableCommDevice** iBuses, Slave** iSlaves, TMLLength iLength, TMLLength iContent): TMLEventChannel(iName, iNumberOfHops, iBuses, iSlaves, iContent),_length(iLength){
}

void TMLEventFBChannel::testWrite(TMLTransaction* iTrans){
	_writeTrans=iTrans;
	_writeTrans->setVirtualLength((_length-_content>0)?WAIT_SEND_VLEN:0);
}

void TMLEventFBChannel::testRead(TMLTransaction* iTrans){
	_readTrans=iTrans;
	_readTrans->setVirtualLength((_content>0)?WAIT_SEND_VLEN:0);
}

void TMLEventFBChannel::write(){
	_content++;
	_paramQueue.push_back(_writeTrans->getCommand()->getParam());
	if (_readTrans!=0 && _readTrans->getVirtualLength()==0){
		_readTrans->setRunnableTime(_writeTrans->getEndTime());
		_readTrans->setVirtualLength(WAIT_SEND_VLEN);
	}	
	_writeTrans=0;
}

bool TMLEventFBChannel::read(){
	Parameter<ParamType> *pRead,*pWrite;
	if (_content<1){
		return false;
	}else{
		_content--;
		pRead=_readTrans->getCommand()->getParam();
		pWrite=_paramQueue.front();
		if (pRead!=0 && pWrite!=0) *pRead=*pWrite;
		_paramQueue.pop_front();
		if (_writeTrans!=0 && _writeTrans->getVirtualLength()==0){
			_writeTrans->setRunnableTime(_readTrans->getEndTime());
			_writeTrans->setVirtualLength(WAIT_SEND_VLEN);
		}	
		_readTrans=0;
		return true;
	}
}

void TMLEventFBChannel::cancelReadTransaction(){
	_readTrans=0;
}

TMLTask* TMLEventFBChannel::getBlockedReadTask() const{
	return _readTask;
}

TMLTask* TMLEventFBChannel::getBlockedWriteTask() const{
	return _writeTask;
}

std::string TMLEventFBChannel::toString(){
	std::ostringstream outp;
	outp << _name << "(evtFB) len:" << _length << " content:" << _content;
	return outp.str();
}


