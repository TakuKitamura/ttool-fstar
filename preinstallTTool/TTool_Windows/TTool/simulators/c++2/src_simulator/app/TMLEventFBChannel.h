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

#ifndef TMLEventFBChannelH
#define TMLEventFBChannelH

#include <definitions.h>
#include <TMLEventSizedChannel.h>

class TMLCommand;
class Bus;

///This class models a blocking read blocking write channel (finite blocking FIFO).
template <typename T, int paramNo> 
class TMLEventFBChannel:public TMLEventSizedChannel<T,paramNo>{
public:
	//typedef TMLEventSizedChannel<T,paramNo> SC;
	
	///Constructor
    	/**
	\param iID of channel
      	\param iName Name of the channel
	\param iNumberOfHops Number of buses on which the channel is mapped
	\param iMasters Pointers to the masters which the channel is connected to
	\param iSlaves Pointers to the slaves on which the channel is mapped
	\param iLength Length of the channel
	\param iContent Initial content of the channel
	\param iLossRate Loss rate of the channel
	\param iMaxNbOfLosses Maximum number of losses
    	*/
	TMLEventFBChannel(ID iID, std::string iName, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, TMLLength iLength, TMLLength iContent, unsigned int iLossRate=0, unsigned int iMaxNbOfLosses=0): TMLEventSizedChannel<T,paramNo>(iID, iName, iNumberOfHops, iMasters, iSlaves, iContent, iLossRate, iMaxNbOfLosses),_length(iLength){
	}

	void testWrite(TMLTransaction* iTrans){
		this->_writeTrans=iTrans;
		//if (paramNo!=0) this->_tmpParam = iTrans->getCommand()->setParams(0);  //NEW in if
		if (paramNo!=0) this->_tmpParam = iTrans->getCommand()->setParams(0);  //NEW in if
		this->_writeTrans->setVirtualLength((_length-this->_content>0)?WAIT_SEND_VLEN:0);
		this->_overflow = (this->_content==_length);
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
				this->_paramQueue.push_back(this->_tmpParam);   //NEW
#ifdef STATE_HASH_ENABLED
				this->_tmpParam->getStateHash(& this->_stateHash);   //NEW in if
#endif
			}
			if (this->_readTrans!=0 && this->_readTrans->getVirtualLength()==0){
				//std::cout << "FB: Wake up trans in channel: " << this->_name << "\n";
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
			//if (this->_readTrans->getCommand()->getParamFuncPointer()!=0) (this->_readTask->*(this->_readTrans->getCommand()->getParamFuncPointer()))(this->_paramQueue.front()); //NEW
			if (paramNo!=0){
				this->_readTrans->getCommand()->setParams(this->_paramQueue.front());
				delete dynamic_cast<SizedParameter<T,paramNo>*>(this->_paramQueue.front());
				this->_paramQueue.pop_front();  //NEW
			}
	#ifdef STATE_HASH_ENABLED
			//_stateHash-=this->_paramQueue.front().getStateHash();
			//this->_paramQueue.front().removeStateHash(&_stateHash);
			this->_hashValid = false;
	#endif
			
			if (this->_writeTrans!=0 && this->_writeTrans->getVirtualLength()==0){
				this->_writeTrans->setRunnableTime(this->_readTrans->getEndTime());
				this->_writeTrans->setChannel(this);
				this->_writeTrans->setVirtualLength(WAIT_SEND_VLEN);
			}
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
		return this->_writeTask;
	}

	std::string toString() const{
		std::ostringstream outp;
		outp << this->_name << "(evtFB) len:" << _length << " content:" << this->_content;
		return outp.str();
	}

	TMLLength insertSamples(TMLLength iNbOfSamples, Parameter* iParam){
		TMLLength aNbToInsert;
		if (iNbOfSamples==0){
			this->_content=0;
			this->_paramQueue.clear();
			aNbToInsert=0;
		}else{
			aNbToInsert=min(iNbOfSamples, _length-this->_content);
			this->_content+=aNbToInsert;
			for (TMLLength i=0; i<aNbToInsert; i++) this->_paramQueue.push_back(iParam);
		} 
		if (this->_writeTrans!=0) this->_writeTrans->setVirtualLength((_length-this->_content>0)?WAIT_SEND_VLEN:0);
		if (this->_readTrans!=0) this->_readTrans->setVirtualLength((this->_content>0)?WAIT_SEND_VLEN:0);
		return aNbToInsert;
	}
protected:
	///Length of the channel
	TMLLength _length;
};

#endif
