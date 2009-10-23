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
#include <TransactionListener.h>
#include <CommandListener.h>
#include <ListenerSubject.h>
#include <TransactionAbstr.h>
#include <CommandAbstr.h>
#include <TaskAbstr.h>
#include <CPUAbstr.h>
#include <ChannelAbstr.h>

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
class RunXTransactions: public TransactionListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param  iTransToExecute Number of transactions to execute
	*/
	RunXTransactions(SimComponents* iSimComp, unsigned int iTransToExecute);
	///Destructor
	virtual ~RunXTransactions();
	//void transExecuted(TMLTransaction* iTrans);
	void transExecuted(TMLTransaction* iTrans, unsigned int iID);
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
class Breakpoint: public CommandListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	*/
	Breakpoint(SimComponents* iSimComp);
	void commandEntered(TMLCommand* iComm, unsigned int iID);
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
class CondBreakpoint: public CommandListener{
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
	void commandFinished(TMLCommand* iComm, unsigned int iID);
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
	unsigned int _ID;
	///Task for which the condition is evaluated
	TMLTask* _task;
	///Keeps track of the IDs already in use
	static unsigned int _freeID;
	///Flag indicating that the C source file has been created
	bool _cSourceFileCreated;
	///Flag indicating that the object file has been created
	bool _objectFileCreated;
	///Flag indicating that the library file has been created
	bool _libFileCreated;
};


//************************************************************************
///Listener which stops the simulation as soon as a random choice command is encountered
class RunTillNextRandomChoice: public CommandListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	*/
	RunTillNextRandomChoice(SimComponents* iSimComp);
	void commandEntered(TMLCommand* iComm, unsigned int iID);
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
class RunXCommands: public CommandListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iCommandsToExecute Number of commands to execute
	*/
	RunXCommands(SimComponents* iSimComp, unsigned int iCommandsToExecute);
	///Destructor
	virtual ~RunXCommands();
	void commandFinished(TMLCommand* iComm, unsigned int iID);
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
class RunXTimeUnits: public TransactionListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iEndTime End time
	*/
	RunXTimeUnits(SimComponents* iSimComp, TMLTime iEndTime);
	///Destructor
	virtual ~RunXTimeUnits();
	//void transExecuted(TMLTransaction* iTrans);
	void transExecuted(TMLTransaction* iTrans, unsigned int iID);
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
class RunTillTransOnDevice: public TransactionListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iSubject Device to listen on
	*/
	RunTillTransOnDevice(SimComponents* iSimComp, ListenerSubject<TransactionListener>* iSubject);
	///Destructor
	virtual ~RunTillTransOnDevice();
	//void transExecuted(TMLTransaction* iTrans);
	void transExecuted(TMLTransaction* iTrans, unsigned int iID);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Device to listen on
	ListenerSubject <TransactionListener> * _subject;
};


//************************************************************************
///Listener which stops the simulation as soon as a given task executes a transaction
class RunTillTransOnTask: public TaskListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iSubject Task to listen on
	*/
	RunTillTransOnTask(SimComponents* iSimComp, ListenerSubject<TaskListener>* iSubject);
	///Destructor
	virtual ~RunTillTransOnTask();
	//void transExecuted(TMLTransaction* iTrans);
	void transExecuted(TMLTransaction* iTrans, unsigned int iID);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Task to listen on
	ListenerSubject <TaskListener> * _subject;
};


//************************************************************************
///Listener which stops the simulation as soon data is conveyed on a given channel
class RunTillTransOnChannel: public ChannelListener{
public:
	///Constructor
	/**
	\param iSimComp Pointer to a SimComponents object
	\param iSubject Channel to listen on
	*/
	RunTillTransOnChannel(SimComponents* iSimComp, ListenerSubject<ChannelListener>* iSubject);
	///Destructor
	virtual ~RunTillTransOnChannel();
	//void transExecuted(TMLTransaction* iTrans);
	void transExecuted(TMLTransaction* iTrans, unsigned int iID);
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
	///Channel to listen on
	ListenerSubject <ChannelListener> * _subject;
};


//************************************************************************
/*class TestListener: public TaskListener, public ChannelListener, public CommandListener{
public:
	/////Constructor
	///**
	//\param iSimComp Pointer to a SimComponents object
	//
	TestListener(SimComponents* iSimComp);
	void taskStarted(TMLTransaction* iTrans);
	void taskFinished(TMLTransaction* iTrans);
	void readTrans(TMLTransaction* iTrans);
	void writeTrans(TMLTransaction* iTrans);
	void commandFinished(TMLCommand* iComm);
	void commandStarted(TMLCommand* iComm);
	///Destructor
	virtual ~TestListener();
	
protected:
	///Pointer to a SimComponents object
	SimComponents* _simComp;
};


//************************************************************************
class ConstraintBlock: public TransactionListener{
	ConstraintBlock(SimComponents* iSimComp);
	~ConstraintBlock();
	void transExecuted(TMLTransaction* iTrans);
	virtual bool constraintFunc(TransactionAbstr iTrans, CommandAbstr iCmd, TaskAbstr iTask, CPUAbstr iCPU, ChannelAbstr iChan) =0;
private:
	SimComponents* _simComp;
};*/
#endif
