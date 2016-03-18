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

#ifndef ERCH
#define ERCH

#include <NotifyIF.h>
#include <EBRDDCommand.h>
#include <KernelListener.h>

#define MSG_EBRDDSEQVIOLATED "EBRDD sequence has been violated"

class SimComponents;

///EBRDD Command Event Reception Container
class ERC: public NotifyIF, public EBRDDCommand, public KernelListener{
public:
	///Constructor
	/**
	\param iID ID of the ERC
	\param iEBRDD Pointer to the subordinate EBRDD 
	*/
	ERC(ID iID, EBRDD* iEBRDD);
	void notifyEvent(ID iID);
	void notifyAbort(ID iID);
	///Returns a pointer to the subordinate EBRDD
	/**
	\return Pointer to EBRDD
	*/
	EBRDD* getEBRDD();
	void timeAdvances(TMLTime iCurrTime);
	EBRDDCommand* prepare();
	std::string toString() const;
	///Sets the class variable pointing to the simulation objects
	/**
	\param iSimComp Pointer to the simulation components
	*/
	static void setSimComponents(SimComponents* iSimComp);
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void reset();
protected:
	///Pointer to subordiate EBRDD
	EBRDD* _ebrdd;
	///Pointer to simulation components
	static SimComponents* _simComp;
	///Indicates whether the ERC has already been prepared
	bool _wasPrepared;
	bool _once;
};
#endif
