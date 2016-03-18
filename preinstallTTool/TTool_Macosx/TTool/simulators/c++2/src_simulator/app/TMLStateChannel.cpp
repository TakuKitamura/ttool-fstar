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

#include <TMLStateChannel.h>
#include <HashAlgo.h>

TMLStateChannel::TMLStateChannel(ID iID, std::string iName, unsigned int iWidth, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, TMLLength iContent, unsigned int iPriority, unsigned int iLossRate, unsigned int iMaxNbOfLosses): TMLChannel(iID, iName, iWidth, iNumberOfHops, iMasters, iSlaves, iPriority), _content(iContent), _nbToWrite(0), _nbToRead(0), _overflow(false), _underflow(false)
#ifdef LOSS_ENABLED
, _lossRate(iLossRate), _maxNbOfLosses(iMaxNbOfLosses*iWidth), _nbOfLosses(0), _lossRemainder(0)
#endif
{
}

TMLStateChannel::~TMLStateChannel(){}

std::ostream& TMLStateChannel::writeObject(std::ostream& s){
	TMLChannel::writeObject(s);
	WRITE_STREAM(s,_content);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: TMLStateChannel " << _name << " content: " << _content << std::endl;
#endif
	return s;
}

std::istream& TMLStateChannel::readObject(std::istream& s){
	TMLChannel::readObject(s);
	READ_STREAM(s,_content);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: TMLStateChannel " << _name << " content: " << _content << std::endl;
#endif
	return s;
}

void TMLStateChannel::reset(){
	//std::cout << "StateChannel reset" << std::endl;
	TMLChannel::reset();
	_content=0;
	_nbToWrite=0;
	_nbToRead=0;
#ifdef LOSS_ENABLED
	_nbOfLosses = 0;
	_lossRemainder = 0;
#endif
	//std::cout << "StateChannel reset end" << std::endl;
}

void TMLStateChannel::streamStateXML(std::ostream& s) const{
	s << TAG_CHANNELo << " name=\"" << _name << "\" id=\"" << _ID << "\">" << std::endl;
	s << TAG_CONTENTo << _content << TAG_CONTENTc << TAG_TOWRITEo << _nbToWrite << TAG_TOWRITEc << TAG_TOREADo << _nbToRead << TAG_TOREADc;
	s << TAG_CHANNELc << std::endl;
}

void TMLStateChannel::getStateHash(HashAlgo* iHash) const{
	if (_significance!=0){
		//std::cout << "add channel content:\n";
		iHash->addValue(_content);
	}
}

