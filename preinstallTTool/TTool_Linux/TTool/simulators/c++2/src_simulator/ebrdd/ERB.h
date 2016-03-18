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

#ifndef ERBH
#define ERBH

#include <EventIF.h>
#include <ChannelListener.h>
#include <CommandListener.h>
#include <KernelListener.h>
#include <TaskListener.h>
#include <TransactionListener.h>

#define MSG_EBRDDCONVIOLATED "EBRDD condition has been violated"

class SimComponents;
class ERC;

///Event Reaction Blocks are leaf nodes of an Event Tree within an ERC
class ERB: public EventIF, public ChannelListener, public CommandListener, public KernelListener, public TaskListener, public TransactionListener{
public:
	///Constructor
	/**
	\param iContainer Pointer to subordinate ERC
	\param iAncestorNode Pointer to ancestor node within the event tree
	\param iNegated Event negated flag
	\param iName Name of the ERB
	\param iEvtID ID of event to be received
	\param iSourceClass Category of then event source (CPU, Bus, Slave, ...)
	\param iArrayOfSources Array of event sources
	\param iNbOfSources Number of event sources
	\param iEbrddFunc Member function pointer to EBRDD function
	\param iCondString ERB Condition in string format
	*/
	ERB(ERC* iContainer, NotifyIF* iAncestorNode, bool iNegated, const std::string& iName, ID iEvtID, unsigned int iSourceClass, unsigned int* iArrayOfSources, unsigned int iNbOfSources, EBRDDFuncPointer iEbrddFunc, const std::string& iCondString);
	///Destructor
	virtual ~ERB();
	void timeTick(TMLTime iNewTime);
	void activate();
	void deactivate();
	void prepare();
	//void reset();
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	void transExecuted(TMLTransaction* iTrans, ID iID);
	void commandEntered(TMLCommand* iComm, ID iID);
	void commandStarted(TMLCommand* iComm, ID iID);
	void commandExecuted(TMLCommand* iComm, ID iID);
	void commandFinished(TMLCommand* iComm, ID iID);
	void taskStarted(TMLTransaction* iTrans, ID iID);
	void taskFinished(TMLTransaction* iTrans, ID iID);
	void readTrans(TMLTransaction* iTrans, ID iID);
	void writeTrans(TMLTransaction* iTrans, ID iID);
	void simulationStarted();
	void simulationStopped();
	///Sets the class variable pointing to the simulation objects
	/**
	\param iSimComp Pointer to the simulation components
	*/
	static void setSimComponents(SimComponents* iSimComp);
protected:
	///Pointer to subordinate ERC
	ERC* _container;
	///Name of ERB
	std::string _name;
	///ID of event to be received
	ID _evtID;
	///Category of then event source (CPU, Bus, Slave, ...)
	unsigned int _sourceClass;
	///Array of event sources
	unsigned int* _arrayOfSources;
	///Number of event sources
	unsigned int _nbOfSources;
	///Pointer to simulation components
	static SimComponents* _simComp;
	///Class variable holding strinf representations of events
	static char* _evtString[];
	///Member function pointer to EBRDD function
	EBRDDFuncPointer _ebrddFunc;
	///ERB Condition in string format
	std::string _condString;
	///Signals event to ancestor node
	/**
	\param iEvtSourceID  ID of event source
	*/
	void notifyAncestor(ID iEvtSourceID);
};
#endif
