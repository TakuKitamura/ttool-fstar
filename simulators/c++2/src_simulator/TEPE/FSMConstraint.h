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
#ifndef FSMConstraintH
#define FSMConstraintH
#include "ThreeSigConstraint.h"
#include "PropertyStateConstraint.h"

class FSMConstraint: public ThreeSigConstraint, public PropertyStateConstraint{
public:
	FSMConstraint(bool iIncludeBounds): ThreeSigConstraint(iIncludeBounds), _state(0){
	}
	
	void notifiedReset(){
		ThreeSigConstraint::notifiedReset();
		PropertyStateConstraint::notifiedReset();
	}
	
	void reset(){
		PropertyStateConstraint::reset();
		_state=0;
	}
	
	virtual std::ostream& writeObject(std::ostream& s){
		PropertyStateConstraint::writeObject(s);
		std::cout << "_state written " << _state << "\n";
		WRITE_STREAM(s, _state);
		return s;
	}
	
	virtual std::istream& readObject(std::istream& s){
		PropertyStateConstraint::readObject(s);
		READ_STREAM(s, _state);
		std::cout << "_state read " << _state << "\n";
		return s;
	}
protected:
	
	void evalInput(){
		if (!(_enabledNotified==UNDEF || _s1Notified==UNDEF || _sfNotified==UNDEF || _s2Notified==UNDEF)){
			//std::cout << "_notificationMask=15\n";
			if(_enabledNotified==TRUE && _includeBounds){
				//std::cout << "_enabledNotified && _includeBounds\n";
				_constrEnabled=true;
			}
			if (_disabledNotified==TRUE && !_includeBounds) _constrEnabled=false;
			unsigned int aEnableFlag=0;
			bool aSigOutFlag=false;
			if(_constrEnabled){
				//std::cout << "_constrEnabled\n";
				if( _s1Notified==TRUE) moveToNextState(1, &aEnableFlag, &aSigOutFlag);
				if( _sfNotified==TRUE) moveToNextState(3, &aEnableFlag, &aSigOutFlag);
				if( _s2Notified==TRUE) moveToNextState(2, &aEnableFlag, &aSigOutFlag);
			}
			_constrEnabled |= (_enabledNotified==TRUE);
			if (_disabledNotified==TRUE){
				//std::cout << "DISABLE=============================\n";
				aEnableFlag |=1;
				//_propViolation |= (_state!=0);
				if (_state!=0) reportPropViolation();
				//if (_state!=0) std::cout << "Violation detected!!!\n";
				reset();
			}
			notifiedReset();
			if (_aboveConstr!=0) _aboveConstr[0]->notifyEnable(aEnableFlag);
			if (_rightConstr!=0)  (_rightConstr->*_ntfFuncSigOut)(aSigOutFlag);
			//std::cout << "... violation: " << _propViolation << "\n";
		}//else
			//std::cout << "_notificationMask=" << _notificationMask << "\n";
	}
	
	void moveToNextState(unsigned int iSignal, unsigned int * iEnableFlag, bool * iSigOutFlag){
		unsigned int aTabEntry = _transTable[iSignal + (_state << 2)];
		//_propViolation |= (aTabEntry & 1 !=0);
		if ((aTabEntry & 1) !=0) reportPropViolation();
		*iEnableFlag |= ((aTabEntry>>1) & 3);
		*iSigOutFlag |= ((aTabEntry & 8)!=0);
		_state = (aTabEntry >> 4);
		//std::cout << "State: " << _state << "  enable: " << ((aTabEntry>>1) & 3) << "  sigout: " << ((aTabEntry & 8)!=0) << "  violation: " << _propViolation << "\n";
	}
	
	const unsigned int* _transTable;
	unsigned int _state;
};
#endif
