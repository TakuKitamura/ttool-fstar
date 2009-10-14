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

class SimComponents;

class ERB: public EventIF, public ChannelListener, public CommandListener, public KernelListener, public TaskListener, public TransactionListener{
public:
	ERB(NotifyIF* iAncestorNode, bool iNegated, const std::string& iName, unsigned int iSourceClass, unsigned int iSourceID, unsigned int iEvtID);
	void timeTick(TMLTime iNewTime);
	void activate();
	void deactivate();
	void transExecuted(TMLTransaction* iTrans);
	void commandEntered(TMLCommand* iComm);
	void commandStarted(TMLCommand* iComm);
	void commandExecuted(TMLCommand* iComm);
	void commandFinished(TMLCommand* iComm);
	void taskStarted(TMLTransaction* iTrans);
	void taskFinished(TMLTransaction* iTrans);
	void readTrans(TMLTransaction* iTrans);
	void writeTrans(TMLTransaction* iTrans);
	void simulationStarted();
	void simulationStopped();
	///Sets the internal pointer to the simulation components
	/**
      	\param iSimComp Pointer to simulation components
    	*/ 
	static void setSimComponents(SimComponents* iSimComp);
protected:
	/*void notify(){
		if (conditionFunction) 
			action;
		else
			abort;
		NOTIFY_ANCESTOR;
	}*/
	
	/*void abort(){
		if (_active){
			_aborted=true;
			_ancestorNode->notifyAbort(_ID);
		}
	}*/

	bool _active;
	std::string _name;
	unsigned int _sourceClass;
	unsigned int _sourceID;
	unsigned int _evtID;
	///Pointer to simulation components
	static SimComponents* _simComp;
	//Function condition
	//Function action
};
#endif
