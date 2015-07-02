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

#ifndef GeneralListenerH
#define GeneralListenerH

#define NOTIFY_SIM_STARTED() {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->simulationStarted(); this->listenersUnLock();}}
#define NOTIFY_SIM_STOPPED() {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->simulationStopped(); this->listenersUnLock();}}
#define NOTIFY_TIME_ADVANCES(iTime) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->timeAdvances(iTime); this->listenersUnLock();}}
#define NOTIFY_TASK_TRANS_EXECUTED(iTrans) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->transExecuted(iTrans,this->_ID); this->listenersUnLock();}}
#define NOTIFY_TASK_FINISHED(iTrans) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->taskFinished(iTrans,this->_ID); this->listenersUnLock();}}
#define NOTIFY_TASK_STARTED(iTrans) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->taskStarted(iTrans,this->_ID); this->listenersUnLock();}}
#define NOTIFY_TRANS_EXECUTED(iTrans) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->transExecuted(iTrans,this->_ID); this->listenersUnLock();}}
#define NOTIFY_CMD_ENTERED(iComm) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->commandEntered(iComm,this->_ID); this->listenersUnLock();}}


#define NOTIFY_CMD_EXECUTED(iTrans) {if (!this->_listeners.empty()){this->listenersLock();for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->transExecuted(iTrans,this->_ID); this->listenersUnLock();}}

#define NOTIFY_CMD_FINISHED(iComm) {if (!this->_listeners.empty()){this->listenersLock();for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->commandFinished(iComm,this->_ID); this->listenersUnLock();}}

#define NOTIFY_CMD_STARTED(iComm) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) (*i)->commandStarted(iComm,this->_ID); this->listenersUnLock();}}

#define NOTIFY_WRITE_TRANS_EXECUTED(iTrans) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) {(*i)->transExecuted(iTrans,this->_ID);} this->listenersUnLock();}}
#define NOTIFY_READ_TRANS_EXECUTED(iTrans) {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) {(*i)->transExecuted(iTrans,this->_ID);} this->listenersUnLock();}}
#define NOTIFY_EVALUATE() {if (!this->_listeners.empty()){this->listenersLock(); for(std::list<GeneralListener*>::const_iterator i=this->_listeners.begin(); i != this->_listeners.end(); ++i) {(*i)->evaluate();} this->listenersUnLock();}}

///Encapsulates events associated with transactions
class GeneralListener{
public:
	///Gets called when a the simulation is started
	virtual void simulationStarted(){}
	///Gets called when a the simulation is stopped
	virtual void simulationStopped(){}
	///Gets called when simulation time advances
	/**
	\param  iCurrTime Current simulation time
	*/
	virtual void timeAdvances(TMLTime iCurrTime){}
	
	///Gets called when a task executes its first transaction
	/**
	\param iTrans Pointer to the transaction
	\param iID ID of the event source
	*/
	virtual void taskStarted(TMLTransaction* iTrans, ID iID){}
	///Gets called when a task executes its last transaction
	/**
	\param iTrans Pointer to the transaction
	\param iID ID of the event source
	*/
	virtual	void taskFinished(TMLTransaction* iTrans, ID iID){}
	///Destructor
	///Gets called when a transaction is executed
	/**
	\param iTrans Pointer to the transaction
	\param iID ID of the event source
	*/
	virtual void transExecuted(TMLTransaction* iTrans, ID iID){}
	
	
	///Gets called when a command is entered the first time
	/**
	\param iComm Pointer to the command
	\param iID ID of the event source
	*/
	virtual void commandEntered(TMLCommand* iComm, ID iID){}
	//Gets called when a transaction of the command is executed
	/*
	\param iComm Pointer to the command
	\param iID ID of the event source
	*/
	//virtual	void commandExecuted(TMLCommand* iComm, ID iID){}
	///Gets called when a the last transaction of the command is executed
	/**
	\param iComm Pointer to the command
	\param iID ID of the event source
	*/
	virtual void commandFinished(TMLCommand* iComm, ID iID){}
	///Gets called when a the first transaction of the command is executed
	/**
	\param iComm Pointer to the command
	\param iID ID of the event source
	*/
	virtual void commandStarted(TMLCommand* iComm, ID iID){}
	
	
	//Gets called when a read transaction is executed
	/*
	\param iTrans Pointer to the transaction
	\param iID ID of the event source
	*/
	//virtual void readTrans(TMLTransaction* iTrans, ID iID){}
	//Gets called when a write transaction is executed
	/*
	\param iTrans Pointer to the transaction
	\param iID ID of the event source
	*/
	//virtual void writeTrans(TMLTransaction* iTrans, ID iID){}
	///Event triggering the evaluation of TEPE constraints
	virtual void evaluate(){}
	///Destructor
	virtual ~GeneralListener(){}
protected:
};
#endif
