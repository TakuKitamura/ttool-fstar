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

#ifndef PropertyStateConstraintH
#define PropertyStateConstraintH
#include "PropertyConstraint.h"

typedef enum{GENERAL, NGENERAL, FINALLY, NFINALLY} PropType;

class PropertyStateConstraint: public PropertyConstraint{
public:
	PropertyStateConstraint(PropType iType): _type(iType), _constrEnabled(false), _enabledNotified(UNDEF), _disabledNotified(UNDEF),  _property(_type==GENERAL || _type == NFINALLY){
	}
	
	bool evalProp(){
		if (_aboveConstr==0)
			return _property;
		else
			return _aboveConstr[0]->evalProp() && _property;
	}
	
	void notifyEnable(unsigned int iSigState){
		_disabledNotified = ((iSigState & 1)==0)? FALSE:TRUE;
		_enabledNotified = ((iSigState & 2)==0)? FALSE:TRUE;
		evalInput();
	}
	
	virtual void notifiedReset(){
		_enabledNotified=UNDEF;
		_disabledNotified=UNDEF;
	}
	
	virtual void reset(){
		_constrEnabled=false;
	}
	
	void forceDisable(){
		_constrEnabled=false;
	}
	
	virtual std::ostream& writeObject(std::ostream& s){
		unsigned char aTmp = (_property)?1:0;
		std::cout << "_property written " << _property << "\n";
		if (_constrEnabled) aTmp |= 2;
		WRITE_STREAM(s, aTmp);
		PropertyConstraint::writeObject(s);
		return s;
	}
	
	virtual std::istream& readObject(std::istream& s){
		unsigned char aTmp;
		READ_STREAM(s, aTmp);
		_property = ((aTmp & 1) !=0);
		std::cout << "_property read " << _property << "\n";
		_constrEnabled = ((aTmp & 2) !=0);
		PropertyConstraint::readObject(s);
		return s;
	}
	
protected:
	virtual void evalInput()=0;
	
	void reportPropOccurrence(bool iProp){
		switch (_type){
		case GENERAL:
			_property &= iProp;
			break;
		case NGENERAL:
			_property |= !iProp;
			break;
		case FINALLY:
			_property |= iProp;
			break;
		case NFINALLY:
			_property &= !iProp;
			break;
		}
	}
	
	PropType _type;
	bool _constrEnabled;
	Tristate _enabledNotified;
	Tristate _disabledNotified;
private:
	bool _property;
};
#endif
