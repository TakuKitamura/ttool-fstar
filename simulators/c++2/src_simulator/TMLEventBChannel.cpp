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

#include <TMLEventBChannel.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>

TMLEventBChannel::TMLEventBChannel(unsigned int iID, std::string iName, unsigned int iNumberOfHops, SchedulableCommDevice** iBuses, Slave** iSlaves, TMLLength iContent, bool iRequestChannel, bool iSourceIsFile):TMLEventChannel(iID, iName, iNumberOfHops, iBuses, iSlaves, iContent), _requestChannel(iRequestChannel), _sourceIsFile(iSourceIsFile),_eventFile(0) {
	if (_sourceIsFile){
		std::cout << "try to open Event file " << _name.c_str() << std::endl;
		//_name="./"+_name;
		_eventFile = new std::ifstream(_name.c_str());
		readNextEvents();
	}
}

TMLEventBChannel::~TMLEventBChannel(){
	if (_eventFile!=0){
		if (_eventFile->is_open()) _eventFile->close();
		delete _eventFile;
	}
}

void TMLEventBChannel::readNextEvents(){
	//std::cout << "vv" << std::endl;
	if (_eventFile->is_open()){
		int i=0;
		Parameter<ParamType> aNewParam; //NEW
		while (++i<NO_EVENTS_TO_LOAD && !_eventFile->eof()){
			_content++;
			(*_eventFile) >> aNewParam;  //NEW
			//aNewParam.readTxtStream(*_eventFile);
			_paramQueue.push_back(aNewParam);
		}
	}else
		std::cout << "Event file failure" << std::endl;
}

void TMLEventBChannel::testWrite(TMLTransaction* iTrans){
	_writeTrans=iTrans;
	if (iTrans->getCommand()->getParamFuncPointer()!=0){
		(_writeTask->*(iTrans->getCommand()->getParamFuncPointer()))(_tmpParam);  //NEW
		//std::cout << "written: ";
		//_tmpParam.print();
	}
	_writeTrans->setVirtualLength(WAIT_SEND_VLEN);
}

void TMLEventBChannel::testRead(TMLTransaction* iTrans){
	_readTrans=iTrans;
	_readTrans->setVirtualLength((_content>0)?WAIT_SEND_VLEN:0);
}

void TMLEventBChannel::write(){
	write(_writeTrans);	
	_writeTrans=0;
}

void TMLEventBChannel::write(TMLTransaction* iTrans){
	_content++;
	_paramQueue.push_back(_tmpParam);   //NEW
	if (_readTrans!=0 && _readTrans->getVirtualLength()==0){
		_readTrans->setRunnableTime(iTrans->getEndTime());
		_readTrans->setVirtualLength(WAIT_SEND_VLEN);
	}
	FOR_EACH_TRANSLISTENER (*i)->transExecuted(iTrans);
}

bool TMLEventBChannel::read(){
	if (_content<1){
		return false;
	}else{
		_content--;
		if (_content==0 && _sourceIsFile) readNextEvents();
		//std::cout << "read next" << std::endl;
		//if (_writeTrans->getCommand()->getParamFuncPointer()!=0){  //NEW
			//std::cout << "in if" << std::endl;
		if (_readTrans->getCommand()->getParamFuncPointer()!=0) (_readTask->*(_readTrans->getCommand()->getParamFuncPointer()))(_paramQueue.front()); //NEW
		//std::cout << "read: ";
		//_paramQueue.front().print();
		//std::cout << "after 2nd if" << std::endl;
		_paramQueue.pop_front();  //NEW
		//}
		//std::cout << "after if" << std::endl;
		FOR_EACH_TRANSLISTENER (*i)->transExecuted(_readTrans);
		_readTrans=0;
		return true;
	}
}

void TMLEventBChannel::cancelReadTransaction(){
	_readTrans=0;
}

TMLTask* TMLEventBChannel::getBlockedReadTask() const{
	return _readTask;
}

TMLTask* TMLEventBChannel::getBlockedWriteTask() const{
	return 0;
}

std::string TMLEventBChannel::toString() const{
	std::ostringstream outp;
	outp << _name << "(evtB) content:" << _content;
	return outp.str();
}

bool TMLEventBChannel::getRequestChannel() const{
	return _requestChannel;
}

std::ostream& TMLEventBChannel::writeObject(std::ostream& s){
	TMLEventChannel::writeObject(s);
	if (_eventFile!=0){
		std::istream::streampos aPos=_eventFile->tellg();
		WRITE_STREAM(s,aPos);
		std::cout << "Write: TMLEventBChannel " << _name << " posInFile: " <<  _eventFile->tellg() << std::endl;
	}
	return s;
}

std::istream& TMLEventBChannel::readObject(std::istream& s){
	std::istream::streampos aPos;
	TMLEventChannel::readObject(s);
	//std::cout << "Read Object TMLEventBChannel " << _name << std::endl;
	if (_eventFile!=0){
		READ_STREAM(s,aPos);
		_eventFile->seekg(aPos);
		std::cout << "Read: TMLEventBChannel " << _name << " posInFile: " <<  aPos << std::endl;
		
	}
	return s;
}

void TMLEventBChannel::reset(){
	//Parameter<ParamType> param(0,0,0);
	TMLEventChannel::reset();
	if (_eventFile!=0){
		_eventFile->clear();
		_eventFile->seekg(0,std::ios::beg);
		//std::cout << "EventB reset " << _eventFile->eof() << std::endl;
		//*_eventFile >> param;
		//param.print();
		readNextEvents();
		std::cout << "no of events: " << _content << std::endl;
	}
}

unsigned int TMLEventBChannel::insertSamples(unsigned int iNbOfSamples, Parameter<ParamType>& iParam){
	unsigned int aNbToInsert;
	if (iNbOfSamples==0){
		_content=0;
		_paramQueue.clear();
		aNbToInsert=0;
	}else{
		aNbToInsert=iNbOfSamples;
		_content+=iNbOfSamples;
		for (unsigned int i=0; i<iNbOfSamples; i++) _paramQueue.push_back(iParam);
	} 
	if (_readTrans!=0) _readTrans->setVirtualLength((_content>0)?WAIT_SEND_VLEN:0);
	return aNbToInsert;
}
