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

#include <TMLbrbwChannel.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>

TMLbrbwChannel::TMLbrbwChannel(ID iID, std::string iName, unsigned int iWidth, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, TMLLength iLength,TMLLength iContent, Priority iPriority, unsigned int iLossRate, unsigned int iMaxNbOfLosses):TMLStateChannel(iID, iName, iWidth, iNumberOfHops, iMasters, iSlaves, iWidth*iContent, iPriority, iLossRate, iMaxNbOfLosses),_length(iLength){
}

void TMLbrbwChannel::testWrite(TMLTransaction* iTrans){
	_nbToWrite=iTrans->getVirtualLength();
	_writeTrans=iTrans;
	//std::cout << "testWrite called by " << _writeTrans->getCommand()->toShortString() << std::endl;
	setTransactionLength();
	//std::cout << "TestWrite " << iLength<< " writeTrans: " << _writeTrans<< " transLen: "<< _writeTrans->getVirtualLength()<< std::endl; 
}

void TMLbrbwChannel::testRead(TMLTransaction* iTrans){
	_nbToRead=iTrans->getVirtualLength();
	_readTrans=iTrans;
	setTransactionLength();
	//std::cout << "TestRead " << iLength<< " readTrans: " << _readTrans << " transLen: "<< _readTrans->getVirtualLength()<< std::endl;
}

void TMLbrbwChannel::write(){
#ifdef LOSS_ENABLED
	if (_maxNbOfLosses > _nbOfLosses){
		TMLLength aLostBytes = _writeTrans->getVirtualLength() * _lossRate + _lossRemainder;
		//std::cout << "lost bytes: " << aLostBytes << "\n";
		_lossRemainder = aLostBytes % 100;
		//aLostBytes /= 100;
		aLostBytes = min(aLostBytes/100, _maxNbOfLosses - _nbOfLosses);
		_content += _writeTrans->getVirtualLength() - aLostBytes;
		std::cout << "Bytes to write: " << _writeTrans->getVirtualLength()-aLostBytes << "\n";
	        std::cout << "Bytes lost: " << aLostBytes << "\n";
		_nbOfLosses +=  aLostBytes;
	}else{
#endif
		//std::cout << "write all  " << _writeTrans->getVirtualLength() << "\n";
		_content+=_writeTrans->getVirtualLength();
#ifdef LOSS_ENABLED
	}
#endif	
	if (_readTrans!=0 && _readTrans->getVirtualLength()==0) _readTrans->setRunnableTime(_writeTrans->getEndTime());
	_nbToWrite=0;
	//FOR_EACH_TRANSLISTENER (*i)->transExecuted(_writeTrans);
#ifdef LISTENERS_ENABLED
	NOTIFY_WRITE_TRANS_EXECUTED(_writeTrans);
#endif
	_writeTrans=0;
	setTransactionLength();
}

bool TMLbrbwChannel::read(){
	if (_content<_readTrans->getVirtualLength()){
		return false;
	}else{
		_content-=_readTrans->getVirtualLength();
		_nbToRead=0;
		if (_writeTrans!=0 && _writeTrans->getVirtualLength()==0) _writeTrans->setRunnableTime(_readTrans->getEndTime());	
		//FOR_EACH_TRANSLISTENER (*i)->transExecuted(_readTrans);
#ifdef LISTENERS_ENABLED
		NOTIFY_READ_TRANS_EXECUTED(_readTrans);
#endif
		_readTrans=0;
		setTransactionLength();
		return true;
	}
}

void TMLbrbwChannel::setTransactionLength() const{
	if (_writeTrans!=0){	
		if (_nbToRead==0){
			_writeTrans->setVirtualLength(min(_length-_content,_nbToWrite));
			_underflow=false;
			//std::cout << _name << ": set write trans len, no read trans, len = " << _writeTrans->getVirtualLength() << std::endl;
		}else{
			if (_nbToRead<=_content){
				//read could be executed right away			
				_writeTrans->setVirtualLength(min(_length-_content,_nbToWrite));
				_underflow=false;
			}else{
				//read could wake up because of write
				_writeTrans->setVirtualLength(min(_length-_content,_nbToRead-_content,_nbToWrite));
				_underflow=true;
			}
			//std::cout << _name << ": set write trans len, with read trans, len = " << _writeTrans->getVirtualLength() << std::endl;
		}
	}
	if (_readTrans!=0){
		if (_nbToWrite==0){
			_readTrans->setVirtualLength(min(_content,_nbToRead));
			_overflow=false;
			//std::cout << _name << ": set read trans len, no write trans, len = " << _readTrans->getVirtualLength() << std::endl;
		}else{
			if (_nbToWrite<=_length-_content){
				//write could be executed right away
				_readTrans->setVirtualLength(min(_content,_nbToRead));
				_overflow=false;
			}else{
				//write could wakeup because of read
				_readTrans->setVirtualLength(min(_content,_nbToWrite-(_length-_content),_nbToRead));
				_overflow=true;
			}
			//std::cout << _name << ": set read trans len, with write trans, len = " << _readTrans->getVirtualLength() << std::endl;

		}
	}
}

std::string TMLbrbwChannel::toString() const{
	std::ostringstream outp;
	outp << _name << "(brbw) len:" << _length << " content:" << _content << " nbToRead:" << _nbToRead << " nbToWrite:" << _nbToWrite;
	return outp.str();
}

TMLLength TMLbrbwChannel::insertSamples(TMLLength iNbOfSamples, Parameter* iParam){
	TMLLength aNbToInsert;
	if (iNbOfSamples==0){
		_content=0;
		aNbToInsert=0;
	}else{
		aNbToInsert=min(iNbOfSamples, _length-_content);
		iNbOfSamples+=aNbToInsert;
	}
	setTransactionLength();
	return aNbToInsert;
}
