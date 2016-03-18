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
#ifndef ListenersSimCmdH
#define ListenersSimCmdH

#include <definitions.h>
//#include <TransactionListener.h>
//#include <CommandListener.h>
#include <GeneralListener.h>
#include <ListenerSubject.h>
//#include <TransactionAbstr.h>
//#include <CommandAbstr.h>
//#include <TaskAbstr.h>
//#include <CPUAbstr.h>
#include <dlfcn.h>
//#include <ChannelAbstr.h>
#include <SignalConstraint.h>
#include <PropertyConstraint.h>

#define MSG_RUNXTRANSACTIONS "Transactions executed"
#define MSG_BREAKPOINT "Breakpoint reached"
#define MSG_CONDBREAKPOINT "Conditional breakpoint reached"
#define MSG_RANDOMCHOICE "Random choice operator reached"
#define MSG_RUNXCOMMANDS "Commands executed"
#define MSG_RUNXTIMEUNITS "Time units elapsed"
#define MSG_TRANSONDEVICE "Transaction on device encountered"
#define MSG_TRANSONTASK "Transaction of task encountered"
#define MSG_TRANSONCHANNEL "Transaction on channel encountered"
#define MSG_CONSTRAINTBLOCK "Constraint not fulfilled"


class SimComponents;

//************************************************************************
///Listener which stops the simulation after a given number of transactions
class RunXTransactions: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param  iTransToExecute Number of transactions to execute
	*/
	RunXTransactions(SimComponents* iSimComp, unsigned int iTransToExecute);
	///Destructor
	virtual ~RunXTransactions();
	void transExecuted(TMLTransaction* iTrans, ID iID);
	///Sets the number of transactions to execute
	/**
	\param  iTransToExecute Number of transactions to execute
	*/
	void setTransToExecute(unsigned int iTransToExecute);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Number of transactions to execute
	unsigned int _count, _transToExecute;

};


//************************************************************************
///Listener establishing a breakpoint
class Breakpoint: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	*/
	Breakpoint(SimComponents* iSimComp);
	void commandEntered(TMLCommand* iComm, ID iID);
	///Enable/disable all breakpoints
	/**
	\param iEnabled true=enable, false=disable
	*/
	static void setEnabled(bool iEnabled);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Breakpoints enabled flag
	static bool _enabled;
};


//************************************************************************
///Breakpoint based on a condition
class CondBreakpoint: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iCond String containing the condition
	\param iTask Task for which the condition is evaluated
	*/
	CondBreakpoint(SimComponents* iSimComp, std::string iCond, TMLTask* iTask);
	///Destructor
	~CondBreakpoint();
	void commandFinished(TMLCommand* iComm, ID iID);
	///Enable/disable all conditional breakpoints
	/**
	\param iEnabled true=enable, false=disable
	*/
	static void setEnabled(bool iEnabled);
	///Checks whether the condition could be compiled
	/**
	\return True if compilation was successful
	*/
	bool conditionValid() const;
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Breakpoints enabled flag
	static bool _enabled;
	///Condition string
	std::string _condText;
	///Pointer to funtion in shared library which contains the breakpoint condition
	BreakCondFunc _condFunc;
	///Handle of shared library
	void * _dlHandle;
	///ID of the breakpoint
	ID _ID;
	///Task for which the condition is evaluated
	TMLTask* _task;
	///Keeps track of the IDs already in use
	static ID _freeID;
	///Flag indicating that the C source file has been created
	bool _cSourceFileCreated;
	///Flag indicating that the object file has been created
	bool _objectFileCreated;
	///Flag indicating that the library file has been created
	bool _libFileCreated;
};


//************************************************************************
///Listener which stops the simulation as soon as a random choice command is encountered
class RunTillNextRandomChoice: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	*/
	RunTillNextRandomChoice(SimComponents* iSimComp);
	void commandEntered(TMLCommand* iComm, ID iID);
	///Enable/disable the Listener
	/**
	\param iEnabled true=enable, false=disable
	*/
	void setEnabled(bool iEnabled);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Listener enabled flag
	bool _enabled;
};


//************************************************************************
///Listener which stops the simulation after a given number of commands
class RunXCommands: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iCommandsToExecute Number of commands to execute
	*/
	RunXCommands(SimComponents* iSimComp, unsigned int iCommandsToExecute);
	///Destructor
	virtual ~RunXCommands();
	void commandFinished(TMLCommand* iComm, ID iID);
	///Sets the number of commands to execute
	/**
	\param  iCommandsToExecute Number of commands to execute
	*/
	void setCmdsToExecute(unsigned int iCommandsToExecute);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Command counter
	unsigned int _count;
	///Number of commands to execute
	unsigned int _commandsToExecute;

};


//************************************************************************
///Listener which stops the simulation at a given time
class RunXTimeUnits: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iEndTime End time
	*/
	RunXTimeUnits(SimComponents* iSimComp, TMLTime iEndTime);
	///Destructor
	virtual ~RunXTimeUnits();
	void transExecuted(TMLTransaction* iTrans, ID iID);
	///Sets the end time of the simulation
	/**
	\param  iEndTime End time of the simulation
	*/	
	void setEndTime(TMLTime iEndTime);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///End time of the simulation
	TMLTime _endTime;

};


//************************************************************************
///Listener which stops the simulation as soon as a transaction is executed on a given device
class RunTillTransOnDevice: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iSubject Device to listen on
	*/
	RunTillTransOnDevice(SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSubject);
	///Destructor
	virtual ~RunTillTransOnDevice();
	void transExecuted(TMLTransaction* iTrans, ID iID);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Device to listen on
	ListenerSubject <GeneralListener> * _subject;
};


//************************************************************************
///Listener which stops the simulation as soon as a given task executes a transaction
class RunTillTransOnTask: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iSubject Task to listen on
	*/
	RunTillTransOnTask(SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSubject);
	///Destructor
	virtual ~RunTillTransOnTask();
	void transExecuted(TMLTransaction* iTrans, ID iID);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Task to listen on
	ListenerSubject <GeneralListener> * _subject;
};


//************************************************************************
///Listener which stops the simulation as soon data is conveyed on a given channel
class RunTillTransOnChannel: public GeneralListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iSubject Channel to listen on
	*/
	RunTillTransOnChannel(SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSubject);
	///Destructor
	virtual ~RunTillTransOnChannel();
	void transExecuted(TMLTransaction* iTrans, ID iID);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Channel to listen on
	ListenerSubject <GeneralListener> * _subject;
};

//************************************************************************
///Listener for generating signals to be evaluated by TEPE constraints
class TEPESigListener: public GeneralListener{
public:
	///Constructor
	/**
	\param iSubjectIDs IDs of event sources to be taken into account
	\param iNbOfSubjectIDs Number of event sources
	\param iEvtBitmap Bitmap of event types to be taken into account
	\param iTransTypeBitmap Bitmap of tranaction types to be taken into account
	\param inbOfSignals Number of signals to be driven
	\param iNotifConstr Pointer to constraints the signals belong to
	\param iNotifFunc Notification function of constraints
	\param iSimComp Pointer to SimComponents object
	\param iSimulator Pointer to simulator as event source
	*/
	TEPESigListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, unsigned int iEvtBitmap, unsigned int iTransTypeBitmap, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator);
	~TEPESigListener();
	void simulationStarted();
	void simulationStopped();
	void timeAdvances(TMLTime iCurrTime);
	void taskStarted(TMLTransaction* iTrans, ID iID);
	void taskFinished(TMLTransaction* iTrans, ID iID);
	void transExecuted(TMLTransaction* iTrans, ID iID);
	void commandEntered(TMLCommand* iComm, ID iID);
	void commandFinished(TMLCommand* iComm, ID iID);
	void commandStarted(TMLCommand* iComm, ID iID);
	void readTrans(TMLTransaction* iTrans, ID iID);
	void writeTrans(TMLTransaction* iTrans, ID iID);
protected:
	///IDs of event sources to be taken into account
	ID* _subjectIDs;
	///Number of event sources
	unsigned int _nbOfSubjectIDs;
	///Bitmap of event types to be taken into account
	unsigned int _evtBitmap;
	///Bitmap of tranaction types to be taken into account
	unsigned int _transTypeBitmap;
	///Number of signals to be driven
	unsigned int _nbOfSignals;
	///Pointer to constraints the signals belong to
	SignalConstraint** _notifConstr;
	///Dedicated notification function of constraints
	NtfSigFuncPointer* _notifFunc;
	///Flag indicating whether the signal was notified
	bool _sigNotified;
	///Pointer to SimComponents object
	SimComponents* _simComp;
	///Pointer to simulator as event source
	ListenerSubject<GeneralListener>* _simulator;
	
};

//************************************************************************
///Listener for generating signals for floating inputs of TEPE constraints
class TEPEFloatingSigListener: public GeneralListener, public Serializable{
public:
	///Constructor
	/**
	\param iSimulator Pointer to simulator as event source
	\param inbOfSignals Number of signals to be driven
	\param iNotifConstr Pointer to constraints the signals belong to
	\param iNotifFunc Notification function of constraints
	\param iNbOfStartNodes Number of TEPE constraints whose property output is not connected to any other constraint
	\param iStartNodes TEPE constraints whose property output is not connected to any other constraint
	*/
	TEPEFloatingSigListener(ListenerSubject<GeneralListener>* iSimulator, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, unsigned int iNbOfStartNodes, PropertyConstraint** iStartNodes);
	~TEPEFloatingSigListener();
	void timeAdvances(TMLTime iCurrTime);
	void simulationStarted();
	void simulationStopped();
	void evaluate();
	std::ostream& writeObject(std::ostream& s);
	std::istream& readObject(std::istream& s);
	void reset();
protected:
	///Pointer to simulator as event source
	ListenerSubject<GeneralListener>* _simulator;
	///Number of signals to be driven
	unsigned int _nbOfSignals;
	///Pointer to constraints the signals belong to
	SignalConstraint** _notifConstr;
	///Notification function of constraints
	NtfSigFuncPointer* _notifFunc;
	///Number of TEPE constraints whose property output is not connected to any other constraint
	unsigned int _nbOfStartNodes;
	///TEPE constraints whose property output is not connected to any other constraint
	PropertyConstraint** _startNodes;
};

//************************************************************************
///Listener for generating signals indicating the a TEPE equation has to be reevaluated
class TEPEEquationListener: public GeneralListener{
public:
	///Constructor
	/**
	\param iSubjectIDs IDs of event sources to be taken into account (TML commands that modify significant variables)
	\param iNbOfSubjectIDs Number of event sources
	\param iVar Task Variables referred to in the equation
	\param iEqFunc Evaluation function for the equation
	\param iNotifConstr Pointer to the equation constraint
	\param iNotifFunc Notification function of the equation constraint
	\param iSimComp Pointer to SimComponents object
	\param iSimulator Pointer to simulator as event source
	*/
	TEPEEquationListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, ParamType** iVar, EqFuncPointer iEqFunc, SignalConstraint* iNotifConstr, NtfSigFuncPointer iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator);
	~TEPEEquationListener();
	void commandFinished(TMLCommand* iComm, ID iID);	
	void timeAdvances(TMLTime iCurrTime);
	void simulationStarted();
	void simulationStopped();
protected:
	///IDs of event sources to be taken into account (TML commands that modify significant variables)
	ID* _subjectIDs;
	///Number of event sources
	unsigned int _nbOfSubjectIDs;
	///Task Variables referred to in the equation
	ParamType** _var;
	///Evaluation function for the equation
	EqFuncPointer _eqFunc;
	///Result of the equation
	bool _eqResult;
	///Pointer to the equation constraint
	SignalConstraint* _notifConstr;
	///Notification function of the equation constraint
	NtfSigFuncPointer _notifFunc;
	///Flag indicating whether the signal was notified
	bool _sigNotified;
	///Pointer to SimComponents object
	SimComponents* _simComp;
	///Pointer to simulator as event source
	ListenerSubject<GeneralListener>* _simulator;
};

//************************************************************************
///Listener for generating signals indicating the a TEPE equation has to be reevaluated
class TEPESettingListener: public GeneralListener{
public:
	///Constructor
	/**
	\param iSubjectIDs IDs of event sources to be taken into account (TML commands that modify significant variables)
	\param iNbOfSubjectIDs Number of event sources
	\param iVar Task Variables referred to in the setting
	\param iSetFunc Evaluation function for the setting
	\param inbOfSignals Number of signals to be driven
	\param iNotifConstr Pointer to the setting constraints
	\param iNotifFunc Notification function of setting constraint
	\param iSimComp Pointer to SimComponents object
	\param iSimulator Pointer to simulator as event source
	*/
	TEPESettingListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, ParamType** iVar, SettingFuncPointer iSetFunc, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator);
	~TEPESettingListener();
	void commandFinished(TMLCommand* iComm, ID iID);	
	void timeAdvances(TMLTime iCurrTime);
	void simulationStarted();
	void simulationStopped();
protected:
	///IDs of event sources to be taken into account (TML commands that modify significant variables)
	ID* _subjectIDs;
	///Number of event sources
	unsigned int _nbOfSubjectIDs;
	///Task Variables referred to in the setting
	ParamType** _var;
	///Evaluation function for the setting
	SettingFuncPointer _setFunc;
	///Number of signals to be driven
	unsigned int _nbOfSignals;
	///Result of the setting
	ParamType _setResult;
	///Pointer to the setting constraints
	SignalConstraint** _notifConstr;
	///Notification function of setting constraint
	NtfSigFuncPointer* _notifFunc;
	///Flag indicating whether the signal was notified
	bool _sigNotified;
	///Pointer to SimComponents object
	SimComponents* _simComp;
	///Pointer to simulator as event source
	ListenerSubject<GeneralListener>* _simulator;
};
#endif
