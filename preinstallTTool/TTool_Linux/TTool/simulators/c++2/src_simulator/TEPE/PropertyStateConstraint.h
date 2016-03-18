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
#include <PropertyConstraint.h>

typedef enum{GENERAL, NGENERAL, FINALLY, NFINALLY} PropType;

///Class representing a stateful TEPE constraint
class PropertyStateConstraint: public PropertyConstraint{
public:
	///Constructor
	/**
	\param iType Temporal quantifier: GENERAL, NGENERAL, FINALLY, NFINALLY
	*/
	PropertyStateConstraint(PropType iType);
	bool evalProp();
	void notifyEnable(unsigned int iSigState);
	///Reset function to be called after a simulation round, thus upon advance of simulation time
	virtual void notifiedReset();
	virtual void reset();
	void forceDisable();
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
protected:
	///Logics that evaluates the input signals, updates the internal state of the constraint and generates output signals
	virtual void evalInput()=0;
	///Is called to report a(n) (un)successfull property occurrence
	/**
	\param iProp Property value
	*/
	void reportPropOccurrence(bool iProp);
	///Temporal quantifier: GENERAL, NGENERAL, FINALLY, NFINALLY
	PropType _type;
	///Constraint enabled flag
	bool _constrEnabled;
	///State of enable input signal
	Tristate _enabledNotified;
	///State of disable input signal
	Tristate _disabledNotified;
private:
	///Current value of the property for the current path to be explored
	bool _property;
};
#endif
