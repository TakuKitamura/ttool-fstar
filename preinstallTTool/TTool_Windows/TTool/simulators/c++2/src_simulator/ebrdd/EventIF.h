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

#ifndef EventIFH
#define EventIFH

#include <definitions.h>
#include <Serializable.h>

class NotifyIF; 

///Interface class for top down node configuration within event trees of ERCs
class EventIF: public Serializable{
public:
	///Constructor
	/**
	\param iAncestorNode 
	\param iNegated
	*/
	EventIF(NotifyIF* iAncestorNode, bool iNegated);
	///Set event ID of this event source
	/**
	\param iID ID of the node
	*/
	void setEventID(ID iID);
	///Returns whether the event source has already generated an event
	/**
	\return Returns true if event has already been notified
	*/
	bool notified();
	///Returns the number of notifications of this event source
	/**
	\return Number of notifications
	*/
	unsigned int getNbOfNotific();
	///Returns whether this event source is negated
	/**
	\return Returns true if this source is negated
	*/
	bool getNegated();
	virtual void reset();
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	///Called to notify an advancement of simulation time
	/**
	\param iNewTime New simulation time
	*/
	virtual void timeTick(TMLTime iNewTime)=0;
	///Activates the node
	virtual void activate()=0;
	///Deactivates the node
	virtual void deactivate()=0;
	///Prepares the node
	virtual void prepare()=0;
	///Returns whether this event cannot be raised any more
	/**
	\return true if even was aborted
	*/
	bool getAborted();
	///Destructor
	virtual ~EventIF();
protected:
	///Ancestor node to notify upon event reception
	NotifyIF* _ancestorNode;
	///Negated flag
	bool _negated;
	///ID of the node
	ID _ID;
	///Number of event notifications
	unsigned int _nbOfNotific;
	///Aborted flag
	bool _aborted;
	///Active flag
	bool _active;
};
#endif
