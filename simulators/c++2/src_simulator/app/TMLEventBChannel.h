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

#ifndef TMLEventBChannelH
#define TMLEventBChannelH

#include <definitions.h>
#include <TMLEventSizedChannel.h>
#include <TMLCommand.h>

class TMLCommand;
class Bus;

///This class models a blocking read non blocking write channel (infinite FIFO).
template <typename T, int paramNo> 
class TMLEventBChannel : public TMLEventSizedChannel<T,paramNo>{
public:
	//typedef TMLEventSizedChannel<T,paramNo> SC;
	
	///Constructor
    	/**
      	\param iID ID of channel
	\param iName Name of the channel
	\param iNumberOfHops Number of buses on which the channel is mapped
	\param iMasters Pointers to the masters which the channel is connected to
	\param iSlaves Pointers to the slaves on which the channel is mapped
	\param iContent Initial content of the channel
	\param iRequestChannel Flag indicating if channel is used by a request
	\param iSourceIsFile Flag indicating if events are read from a file
	\param iLossRate Loss rate of the channel
	\param iMaxNbOfLosses Maximum number of losses
    	*/
	TMLEventBChannel(ID iID, std::string iName, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, TMLLength iContent, bool iRequestChannel, bool iSourceIsFile, unsigned int iLossRate=0, unsigned int iMaxNbOfLosses=0): TMLEventSizedChannel<T,paramNo>(iID, iName, iNumberOfHops, iMasters, iSlaves, iContent, iLossRate, iMaxNbOfLosses), _requestChannel(iRequestChannel), _sourceIsFile(iSourceIsFile),_eventFile(0) {
		this->_overflow = false; 
		if (_sourceIsFile){
			std::cout << "try to open Event file " << this->_name.c_str() << std::endl;
			//_name="./"+_name;
			_eventFile = new std::ifstream(this->_name.c_str());
			readNextEvents();
		}
	}

	~TMLEventBChannel(){
		if (_eventFile!=0){
			if (_eventFile->is_open()) _eventFile->close();
			delete _eventFile;
		}
	}

	void testRead(TMLTransaction* iTrans){
		this->_readTrans=iTrans;
		this->_readTrans->setVirtualLength((this->_content>0)?WAIT_SEND_VLEN:0);
		this->_readTrans->setChannel(this);	//NEW!!!!
		this->_underflow = (this->_content==0);
	}

	void write(TMLTransaction* iTrans){
#ifdef LOSS_ENABLED
		if (this->_maxNbOfLosses > this->_nbOfLosses && this->_lossRate!=0 && myrand(0,99) < this->_lossRate){
			this->_nbOfLosses++;
		}else{
#endif
			this->_content++;
			if (paramNo!=0){		
				//this->_paramQueue.push_back(_tmpParam);
				//std::cerr << "write!\n";
				this->_tmpParam = iTrans->getCommand()->setParams(0);
				this->_paramQueue.push_back(this->_tmpParam);
#ifdef STATE_HASH_ENABLED
				this->_tmpParam->getStateHash(& this->_stateHash);  //new in if
#endif
			}
			if (this->_readTrans!=0 && this->_readTrans->getVirtualLength()==0){
				this->_readTrans->setRunnableTime(iTrans->getEndTime());
				this->_readTrans->setChannel(this);
				this->_readTrans->setVirtualLength(WAIT_SEND_VLEN);
			}
#ifdef LOSS_ENABLED
		}
#endif
	#ifdef LISTENERS_ENABLED
		NOTIFY_WRITE_TRANS_EXECUTED(iTrans);
	#endif
		this->_writeTrans=0; //TEST 
	}

	bool read(){
		if (this->_content<1){
			return false;
		}else{
			this->_content--;
			if (this->_content==0 && _sourceIsFile) readNextEvents();
			//std::cout << "read next" << std::endl;
			//if (this->_readTrans->getCommand()->getParamFuncPointer()!=0) (this->_readTask->*(this->_readTrans->getCommand()->getParamFuncPointer()))(this->_paramQueue.front()); //NEW
			if (paramNo!=0){
				//std::cout << "read! ...";
				//this->_paramQueue.front()->print();
				//std::cerr << "\n";
				this->_readTrans->getCommand()->setParams(this->_paramQueue.front());
				delete dynamic_cast<SizedParameter<T,paramNo>*>(this->_paramQueue.front());
				this->_paramQueue.pop_front();  //NEW
			}
	#ifdef STATE_HASH_ENABLED
			//_stateHash-=this->_paramQueue.front().getStateHash();
			//this->_paramQueue.front().removeStateHash(&_stateHash);
			this->_hashValid = false;
	#endif
			
	#ifdef LISTENERS_ENABLED
			NOTIFY_READ_TRANS_EXECUTED(this->_readTrans);
	#endif
			this->_readTrans=0;
			return true;
		}
	}

	void cancelReadTransaction(){
		this->_readTrans=0;
	}

	TMLTask* getBlockedReadTask() const{
		return this->_readTask;
	}

	TMLTask* getBlockedWriteTask() const{
		return 0;
	}

	std::string toString() const{
		std::ostringstream outp;
		outp << this->_name << "(evtB) content:" << this->_content;
		return outp.str();
	}

	bool getRequestChannel() const{
		return _requestChannel;
	}

	std::ostream& writeObject(std::ostream& s){
		TMLEventSizedChannel<T,paramNo>::writeObject(s);
		if (_eventFile!=0){
			//std::istream::streampos aPos=_eventFile->tellg();
			int aPos=_eventFile->tellg();
			WRITE_STREAM(s,aPos);
	#ifdef DEBUG_SERIALIZE
			std::cout << "Write: TMLEventBChannel " << this->_name << " posInFile: " <<  _eventFile->tellg() << std::endl;
	#endif
		}
		return s;
	}

	std::istream& readObject(std::istream& s){
		//std::istream::streampos aPos;
		int aPos;
		TMLEventSizedChannel<T,paramNo>::readObject(s);
		//std::cout << "Read Object TMLEventBChannel " << _name << std::endl;
		if (_eventFile!=0){
			READ_STREAM(s,aPos);
			_eventFile->seekg(aPos);
	#ifdef DEBUG_SERIALIZE
			std::cout << "Read: TMLEventBChannel " << this->_name << " posInFile: " <<  aPos << std::endl;
	#endif
			
		}
		return s;
	}

	void reset(){
		TMLEventSizedChannel<T,paramNo>::reset();
		if (_eventFile!=0){
			_eventFile->clear();
			_eventFile->seekg(0,std::ios::beg);
			//std::cout << "EventB reset " << _eventFile->eof() << std::endl;
			//*_eventFile >> param;
			//param.print();
			readNextEvents();
			std::cout << "no of events: " << this->_content << std::endl;
		}
	}

	TMLLength insertSamples(TMLLength iNbOfSamples, Parameter* iParam){
		TMLLength aNbToInsert;
		if (iNbOfSamples==0){
			this->_content=0;
			this->_paramQueue.clear();
			aNbToInsert=0;
		}else{
			aNbToInsert=iNbOfSamples;
			this->_content+=iNbOfSamples;
			for (TMLLength i=0; i<iNbOfSamples; i++) this->_paramQueue.push_back(iParam);
		} 
		if (this->_readTrans!=0) this->_readTrans->setVirtualLength((this->_content>0)?WAIT_SEND_VLEN:0);
		return aNbToInsert;
	}
	protected:
		void readNextEvents(){
		//std::cout << "vv" << std::endl;
		if (_eventFile->is_open()){
			unsigned int i=0;
			//Parameter<ParamType> aNewParam;
			Parameter* aNewParam; //NEW
			while (++i<NO_EVENTS_TO_LOAD && !_eventFile->eof()){
				this->_content++;
				aNewParam = new SizedParameter<T,paramNo>();
				(*_eventFile) >> aNewParam;  //NEW
				//aNewParam.readTxtStream(*_eventFile);
	#ifdef STATE_HASH_ENABLED
				//_stateHash+=aNewParam.getStateHash();
				//aNewParam.getStateHash(&_stateHash);
	#endif
				this->_paramQueue.push_back(aNewParam);
			}
		}else
			std::cout << "Event file failure" << std::endl;
	}

	void testWrite(TMLTransaction* iTrans){
		this->_writeTrans=iTrans;
		//if (paramNo!=0){
			//_tmpParam = iTrans->getCommand()->setParams(0);
			//this->_paramQueue.push_back(_tmpParam);   //NEW
	//#ifdef STATE_HASH_ENABLED
			//_tmpParam->getStateHash(&_stateHash);
	//#endif
		//}
		this->_writeTrans->setVirtualLength(WAIT_SEND_VLEN);
	}
	///Flag indicating if channel is used by a request
	bool _requestChannel;
	///Flag indicating if events are read from a file
	bool _sourceIsFile;
	///File where events are stored
	std::ifstream* _eventFile;
};

#endif
