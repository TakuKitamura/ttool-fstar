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

#include <TMLbrnbwChannel.h>
#include <TMLTransaction.h>

TMLbrnbwChannel::TMLbrnbwChannel(unsigned int iID, std::string iName, unsigned int iNumberOfHops, SchedulableCommDevice** iBuses, Slave** iSlaves, TMLLength iContent):TMLStateChannel(iID, iName, iNumberOfHops, iBuses, iSlaves, iContent){
}

void TMLbrnbwChannel::testWrite(TMLTransaction* iTrans){
	_nbToWrite=iTrans->getVirtualLength();
	_writeTrans=iTrans;
	setTransactionLength();
}

void TMLbrnbwChannel::testRead(TMLTransaction* iTrans){
	//std::cout << "brnbw test read."<< std::endl;
	_nbToRead=iTrans->getVirtualLength();
	_readTrans=iTrans;
	setTransactionLength();
}

void TMLbrnbwChannel::write(){
	_content+=_writeTrans->getVirtualLength();
	_nbToWrite=0;
	if (_readTrans!=0 && _readTrans->getVirtualLength()==0) _readTrans->setRunnableTime(_writeTrans->getEndTime());
	FOR_EACH_TRANSLISTENER (*i)->transExecuted(_writeTrans);
	_writeTrans=0;
	setTransactionLength();
}

bool TMLbrnbwChannel::read(){
	if (_content<_readTrans->getVirtualLength()){
		return false;
	}else{
		_content-=_readTrans->getVirtualLength();
		_nbToRead=0;
		FOR_EACH_TRANSLISTENER (*i)->transExecuted(_readTrans);
		_readTrans=0;
		setTransactionLength();
		return true;
	}
}

void TMLbrnbwChannel::setTransactionLength() const{
	if (_writeTrans!=0){	
		if (_nbToRead==0){
			//_writeTrans->setVirtualLength(min(_nbToWrite,_burstSize));
			_writeTrans->setVirtualLength(_nbToWrite);
		}else{
			if (_nbToRead<=_content){
				//read could be executed right away			
				//_writeTrans->setVirtualLength(min(_nbToWrite,_burstSize));
				_writeTrans->setVirtualLength(_nbToWrite);
			}else{
				//read could wake up because of write
				//_writeTrans->setVirtualLength(min(_nbToRead-_content,_nbToWrite,_burstSize));
				_writeTrans->setVirtualLength(min(_nbToRead-_content,_nbToWrite));
			}
		}
	}
	if (_readTrans!=0){
		//_readTrans->setVirtualLength(min(_content,_nbToRead,_burstSize));
		_readTrans->setVirtualLength(min(_content,_nbToRead));
	}
}

TMLTask* TMLbrnbwChannel::getBlockedReadTask() const{
	return _readTask;
}

TMLTask* TMLbrnbwChannel::getBlockedWriteTask() const{
	return 0;
}

std::string TMLbrnbwChannel::toString() const{
	std::ostringstream outp;
	outp << _name << "(brnbw) content:" << _content << " nbToRead:" << _nbToRead << " nbToWrite:" << _nbToWrite;
	return outp.str();
}
