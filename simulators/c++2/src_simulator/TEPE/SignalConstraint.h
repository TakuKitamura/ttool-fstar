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

#ifndef SignalConstraintH
#define SignalConstraintH

class SignalConstraint;
typedef void (SignalConstraint::*NtfSigFuncPointer) (bool iSigState);

class SignalConstraint{
public:
	SignalConstraint(bool iIncludeBounds):_s1Notified(UNDEF), _ntfFuncSigOut(0), _rightConstr(0), _includeBounds(iIncludeBounds){
	}
	
	void notifyS1(bool iSigState){
		//_s1Notified = iSigState;
		//_notificationMask |=1;
		std::cout << "Notify S1\n;";
		_s1Notified = (iSigState)?TRUE:FALSE;
		evalInput();
	}
	
	virtual void notifyS2(bool iSigState){};
	
	virtual void notifySf(bool iSigState){};
	
	void connectSigOut(SignalConstraint* iRightConstr, NtfSigFuncPointer iNotFunc){
		_ntfFuncSigOut = iNotFunc;
		_rightConstr = iRightConstr;
	}
	
	virtual void notifiedReset(){
		//_notificationMask=0;
		_s1Notified = UNDEF;
	}
	
	static void setSimTime(TMLTime iSimTime){
		_simTime = iSimTime;
	}
	
protected:
	virtual void evalInput()=0;
	//bool _s1Notified;
	Tristate _s1Notified;
	//unsigned int _notificationMask;
	NtfSigFuncPointer _ntfFuncSigOut;
	SignalConstraint *_rightConstr;
	bool _includeBounds;
	static TMLTime _simTime;
};
#endif
