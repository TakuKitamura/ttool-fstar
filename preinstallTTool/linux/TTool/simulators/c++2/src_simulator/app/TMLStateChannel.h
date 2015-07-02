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

#ifndef TMLStateChannelH
#define TMLStateChannelH

#include <definitions.h>
#include <TMLChannel.h>

class HashAlgo;
class Bus;

///This class defines the basic interfaces and functionalites of a TML stateful channel.
class TMLStateChannel:public TMLChannel{
public:
	///Constructor
    	/**
      	\param iID ID of channel
	\param iName Name of the channel
	\param iWidth Channel width
	\param iNumberOfHops Number of buses on which the channel is mapped
	\param iMasters Pointers to the masters which the channel is connected to
	\param iSlaves Pointers to the slaves on which the channel is mapped
	\param iContent Initial content of the channel
	\param iPriority Priority of the channel
	\param iLossRate Loss rate of the channel
	\param iMaxNbOfLosses Maximum number of losses
    	*/
	TMLStateChannel(ID iID, std::string iName, unsigned int iWidth, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves ,TMLLength iContent, unsigned int iPriority, unsigned int iLossRate, unsigned int iMaxNbOfLosses);
	///Destructor
	virtual ~TMLStateChannel();
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void reset();
	virtual void streamStateXML(std::ostream& s) const;
	inline virtual TMLLength getContent() const {return _content;}
	inline bool getOverflow() const {return _overflow;}
	inline bool getUnderflow() const {return _underflow;}
	virtual void getStateHash(HashAlgo* iHash) const;
protected:
	///Content of the channel
	TMLLength _content;
	///Number of samples the write transaction attempts to write
	TMLLength _nbToWrite;
	///Number of samples the read transaction attempts to read
	TMLLength _nbToRead;
	///Buffer overflow flag
	mutable bool _overflow;
	///Buffer underflow flag
	mutable bool _underflow;
#ifdef LOSS_ENABLED	
	///Loss rate of the channel
	unsigned int _lossRate;
	///Maximum number of losses
	TMLLength _maxNbOfLosses;
	///Current nb of losses
	TMLLength _nbOfLosses;
	///Loss Reamainder
	unsigned int _lossRemainder;
#endif
};

#endif
