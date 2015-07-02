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

#include <EqConstraint.h>

EqConstraint::EqConstraint(ID iID, PropType iType, bool iIncludeBounds): SignalConstraint(iID, iIncludeBounds), PropertyStateConstraint(iType), _eqResult(true), _propReported(false){
}

void EqConstraint::notifiedReset(){
	SignalConstraint::notifiedReset();
	PropertyStateConstraint::notifiedReset();
}

void EqConstraint::reset(){
	PropertyStateConstraint::reset();
	_propReported=false;
}

std::ostream& EqConstraint::writeObject(std::ostream& s){
	unsigned char aTmp = (_eqResult)?1:0;
	if (_propReported) aTmp |=2;
	PropertyStateConstraint::writeObject(s);
	WRITE_STREAM(s, aTmp);
	return s;
}

std::istream& EqConstraint::readObject(std::istream& s){
	unsigned char aTmp;
	PropertyStateConstraint::readObject(s);
	READ_STREAM(s, aTmp);
	_eqResult = ((aTmp & 1)!=0);
	_propReported = ((aTmp & 2)!=0);
	return s;
}


void EqConstraint::evalInput(){
	if (!(_enabledNotified==UNDEF || _s1Notified==UNDEF)){
		if(_enabledNotified==TRUE){		//early enable
			_constrEnabled = (_disabledNotified==FALSE);
			_propReported = false;  //why do we need that? --> failure may otherwise not be reported if _eqResult==true
		}
		
		bool aRelevantEqResult=_eqResult;
		if (_s1Notified==TRUE){		//sigout and enable/disable notifications for connected operators
			//_eqResult = ! _eqResult;
			if (_eqResult){
				_eqResult=false;
				notifyRightConstraints(false);
			}else{
				_eqResult=true;
				notifyRightConstraints(true);
			}
		}else{
			notifyRightConstraints(false);
		}
		if (_includeBounds ) aRelevantEqResult=_eqResult;
		
		std::cout << "Enabled: " << _constrEnabled << "  eqresult: " << _eqResult << "  reported: " << _propReported <<"\n";
		if (_constrEnabled && (!aRelevantEqResult) && (!_propReported)){		//report failure
			reportPropOccurrence(false);
			std::cout << "Report occurrence of Eq: 0\n";
			_propReported = true;
		}
		if (_disabledNotified==TRUE){	//disable, report success
			//std::cout << " DIsable*********************************************\n";
			if (!_propReported){
				reportPropOccurrence(true);
				//std::cout << "Report occurrence of Eq: 1\n";
			}//else
				//std::cout << "Prop occurrence suppressed\n";
			reset();
		}
		notifiedReset();
	}
}
