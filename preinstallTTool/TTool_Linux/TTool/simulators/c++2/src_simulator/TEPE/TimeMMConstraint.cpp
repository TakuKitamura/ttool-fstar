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

#include <TimeMMConstraint.h>
#include <SchedulableDevice.h>

TimeMMConstraint::TimeMMConstraint(ID iID, PropType iType, TMLTime iTmin, TMLTime iTmax, bool iRetrigger, bool iIncludeBounds): TwoSigConstraint(iID, iIncludeBounds), PropertyStateConstraint(iType), _tmin(iTmin), _tmax(iTmax), _retrigger(iRetrigger), _s1Time(-1){
}

void TimeMMConstraint::notifiedReset(){
	TwoSigConstraint::notifiedReset();
	PropertyStateConstraint::notifiedReset();
}

void TimeMMConstraint::reset(){
	PropertyStateConstraint::reset();
	_s1Time=-1;
}

std::ostream& TimeMMConstraint::writeObject(std::ostream& s){
	PropertyStateConstraint::writeObject(s);
	WRITE_STREAM(s, _s1Time);
	return s;
}

std::istream& TimeMMConstraint::readObject(std::istream& s){
	PropertyStateConstraint::readObject(s);
	READ_STREAM(s, _s1Time);
	return s;
}

void TimeMMConstraint::evalInput(){
	if (!(_enabledNotified==UNDEF || _s1Notified==UNDEF || _s2Notified==UNDEF)){
		if(_enabledNotified==TRUE && _includeBounds){
			std::cout << "_enabledNotified && _includeBounds\n";
			_constrEnabled=true;
		}
		unsigned int aEnaFlag=0;
		bool aSigOut=false;
		if (_disabledNotified==TRUE && !_includeBounds) _constrEnabled=false;
		if(_constrEnabled){
			if (_s1Notified==TRUE){
				if (_s1Time==-1){
					_s1Time = SchedulableDevice::getSimulatedTime();
					aEnaFlag |=2;
				}else{
					if (_retrigger){
						std::cout << "retrigger!!!\n";
						_s1Time = SchedulableDevice::getSimulatedTime();
					}
				}
			}
			if (_s1Time!=-1){
				//if (_simTime-_s1Time>_tmax) reportPropViolation();
				if (SchedulableDevice::getSimulatedTime() - _s1Time>_tmax) reportPropOccurrence(false);

				if (_s2Notified==TRUE){
					aEnaFlag |=1;
					std::cout << "s2 notified  _simTime " << SchedulableDevice::getSimulatedTime() << "  _s1Time " << _s1Time << "\n";
					//if (_simTime-_s1Time<_tmin) reportPropViolation();
					if (SchedulableDevice::getSimulatedTime() - _s1Time<=_tmax) reportPropOccurrence(SchedulableDevice::getSimulatedTime() - _s1Time >= _tmin);
					aSigOut=true;
					_s1Time=-1;
				}
			}
			
		}
		_constrEnabled |= (_enabledNotified==TRUE);
		if (_disabledNotified==TRUE){
			if (_s1Time!=-1) aEnaFlag |=1; //NEW to investigate
			reset();
		}
		notifiedReset();
		if (_aboveConstr!=0) _aboveConstr[0]->notifyEnable(aEnaFlag);
		//if (_rightConstr!=0)  (_rightConstr->*_ntfFuncSigOut)(aSigOut);
		notifyRightConstraints(aSigOut);
	}
}
