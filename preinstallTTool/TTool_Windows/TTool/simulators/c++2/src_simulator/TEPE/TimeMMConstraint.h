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

#ifndef TimeMMConstraintH
#define TimeMMConstraintH
#include <TwoSigConstraint.h>
#include <PropertyStateConstraint.h>

///Class representing the TEPE Time Constraint with one input signal and one time value
class TimeMMConstraint: public TwoSigConstraint, public PropertyStateConstraint{
public:
	///Constructor
	/**
	\param iID ID of the constraint
	\param iType Temporal quantifier: GENERAL, NGENERAL, FINALLY, NFINALLY
	\param iTmin Minimum time to elapse between the two input signals
	\param iTmax Maximum time to elapse between the two input signals
	\param iRetrigger Indicates whether a sencond occurrence of the first input signals retriggers the timer
	\param iIncludeBounds Indicates whether the verification interval is open or closed (on both sides in each case)
	*/
	TimeMMConstraint(ID iID, PropType iType, TMLTime iTmin, TMLTime iTmax, bool iRetrigger, bool iIncludeBounds);
	void notifiedReset();
	void reset();
	std::ostream& writeObject(std::ostream& s);
	std::istream& readObject(std::istream& s);
protected:
	void evalInput();
	///Minimum time to elapse between the two input signals
	TMLTime _tmin;
	///Maximum time to elapse between the two input signals
	TMLTime _tmax;
	///Indicates whether a sencond occurrence of the first input signals retriggers the timer
	bool _retrigger;
	///Time of occurrence of the first input signal
	TMLTime _s1Time;
};
#endif
