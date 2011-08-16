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

#ifndef TMLEventSizedChannelH
#define TMLEventSizedChannelH

#include <definitions.h>
#include <TMLEventChannel.h>
#include <Parameter.h>
#include <HashAlgo.h>

class Bus;

///This class represents the base class for all event channels.
template <typename T, int paramNo> 
class TMLEventSizedChannel: public TMLEventChannel{
public:
	///Constructor
    	/**
	\param iID ID of channel
      	\param iName Name of the channel
	\param iNumberOfHops Number of buses on which the channel is mapped
	\param iMasters Pointers to the masters which the channel is connected to
	\param iSlaves Pointers to the slaves on which the channel is mapped
	\param iContent Initial content of the channel
	\param iLossRate Loss rate of the channel
	\param iMaxNbOfLosses Maximum number of losses
    	*/
	TMLEventSizedChannel (ID iID, std::string iName, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, TMLLength iContent, unsigned int iLossRate=0, unsigned int iMaxNbOfLosses=0): TMLEventChannel(iID, iName, 1, iNumberOfHops, iMasters, iSlaves, iContent, 0, iLossRate, iMaxNbOfLosses), _tmpParam(0), _stateHash((HashValueType)_ID, 30), _hashValid(true){
	}

	virtual ~TMLEventSizedChannel(){
		//for(ParamQueue::const_iterator i=_paramQueue.begin(); i != _paramQueue.end(); ++i)
		//	delete *i;
	}

	bool getRequestChannel() const{
		return false;
	}

	virtual std::ostream& writeObject(std::ostream& s){
		//std::cout << "write size of channel " << _name << " :" << _content << std::endl;
		TMLStateChannel::writeObject(s);
//#if paramNo>0
		
		if (paramNo!=0){
			ParamQueue::iterator i;
			for(i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
				(*i)->writeObject(s);
			}
		}
//#endif
		//for_each( _paramQueue.begin(), _paramQueue.end(), std::bind2nd(std::bind1st(std::mem_fun(&(Parameter<ParamType>::writeObject)),s),(unsigned int)_writeTask));
		return s;
	}

	virtual std::istream& readObject(std::istream& s){
		//Parameter<ParamType>* aNewParam;
		TMLStateChannel::readObject(s);
		//std::cout << "Read Object TMLEventChannel " << _name << std::endl;
		//_paramQueue.clear();
//#if paramNo>0
		TMLLength aParamNo;
		if (paramNo!=0){
			ParamQueue::iterator i;
			for(aParamNo=0; aParamNo < _content; aParamNo++){
				//aNewParam = new Parameter<ParamType>(s, (unsigned int) _writeTask);
				//_paramQueue.push_back(Parameter<ParamType>(s));
				_paramQueue.push_back(new SizedParameter<T,paramNo>(s));
			}
		}
//#endif
		_hashValid = false;
		return s;
	}

	void print() const{
		for(ParamQueue::const_iterator i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
			(*i)->print();
		}
	}

	virtual void reset(){
		//std::cout << "EventChannel reset" << std::endl;
		ParamQueue::iterator i;
		TMLStateChannel::reset();
	//#if paramNo>0
		if (paramNo!=0){
			for(i=_paramQueue.begin(); i != _paramQueue.end(); ++i)
				delete dynamic_cast<SizedParameter<T,paramNo>*>(*i);
			_paramQueue.clear();
		}
	//#endif
		_stateHash.init((HashValueType)_ID, 30);
		_hashValid=true;
		//std::cout << "EventChannel reset end" << std::endl; 
	}

	virtual void streamStateXML(std::ostream& s) const{
		s << TAG_CHANNELo << " name=\"" << _name << "\" id=\"" << _ID << "\">" << std::endl;
		s << TAG_CONTENTo << _content << TAG_CONTENTc << TAG_TOWRITEo << _nbToWrite << TAG_TOWRITEc << TAG_TOREADo << _nbToRead << TAG_TOREADc << std::endl;
		for(ParamQueue::const_iterator i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
			(*i)->streamStateXML(s);
			s <<std::endl;
		}
		s << TAG_CHANNELc << std::endl;
	}

	void getStateHash(HashAlgo* iHash) const{
		//TMLStateChannel::getStateHash(iHash);
		//iHash->addValue(_stateHash.getHash());
		if (_significance!=0){
	//#if paramNo>0
			if (paramNo!=0){
				if (!_hashValid){
					_stateHash.init((HashValueType)_ID, 30);
					for(ParamQueue::const_iterator i=_paramQueue.begin(); i != _paramQueue.end(); ++i){
						(*i)->getStateHash(&_stateHash);
					}
					_hashValid = true;
				}
				iHash->addValue(_stateHash.getHash());
			}
	//#endif
			iHash->addValue(_content);
		}
	}


	Parameter* buildParameter(){
		return new SizedParameter<T, paramNo>();
	}
	
protected:
	///Queue for parameters
	ParamQueue _paramQueue;
	///Temporary buffer for the parameters of the registered write transaction 
	Parameter* _tmpParam;
	///Channel State Hash
	mutable HashAlgo _stateHash;
	///Flag indicating whether the current hash is up to date
	mutable bool _hashValid;
};

#endif
