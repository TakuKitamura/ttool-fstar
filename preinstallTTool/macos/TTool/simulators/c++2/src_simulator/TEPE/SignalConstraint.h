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

#include <definitions.h>

class SignalConstraint;

class SignalConstraint{
public:
	///Constructor
	/**
	\param iID ID of the constraint
	\param iIncludeBounds Indicates whether the verification interval is open or closed (on both sides in each case)
	*/
	SignalConstraint(ID iID, bool iIncludeBounds);
	///Destructor
	virtual ~SignalConstraint();
	///Notify the first input signal
	/**
	\param iSigState Indicates whether the signal was notified
	*/
	void notifyS1(bool iSigState);
	///Notify the second input signal
	/**
	\param iSigState Indicates whether the signal was notified
	*/
	virtual void notifyS2(bool iSigState);
	///Notify the negated (failure) input signal
	/**
	\param iSigState Indicates whether the signal was notified
	*/
	virtual void notifySf(bool iSigState);
	///Connects the signal ouput to the input of another constraint
	/**
	\param iRightConstr Target constraint to be connected to the signal output
	\param iNotFunc Notification function of the target constraint
	*/
	void connectSigOut(SignalConstraint* iRightConstr, NtfSigFuncPointer iNotFunc);
	///
	virtual void notifiedReset();
	///Returns the ID of the constraint
	/**
	\return ID of the constraint
	*/
	ID getID();
	//static void setSimTime(TMLTime iSimTime);
protected:
	virtual void evalInput()=0;
	void notifyRightConstraints(bool iSigState);
	ID _ID;
	///State of first input signal
	Tristate _s1Notified;
	///Constraint connected to signal output
	SignalNotificationList _rightConstraints;
	///Indicates whether the verification interval is open or closed (on both sides in each case)
	bool _includeBounds;
	///Simulation time
	static TMLTime _simTime;
};
#endif
