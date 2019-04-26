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

#ifndef TMLTaskH
#define TMLTaskH

#include <definitions.h>
#include <TMLTransaction.h>
#include <TraceableDevice.h>
#include <Serializable.h>
#include <Comment.h>
#include <MemPool.h>
#include <ListenerSubject.h>
#include <GeneralListener.h>
#include <WorkloadSource.h>
#include <HashAlgo.h>

class TMLCommand;
class CPU;
class Comment;

enum vcdTaskVisState
    {
	END_TRANS,
	BETWEEN_TRANS,
	START_TRANS
};

class TMLTask: public TraceableDevice, public ListenerSubject <GeneralListener>, public WorkloadSource{
public:	
	///Constructor
    	/**
      	\param iID ID of the Task
	\param iPriority Priority of the task
	\param iName Name of the task
	\param iCPU Pointer to the CPUs the task is mapped onto
	\param iNoOfCPUs Number of CPUs
    	*/
	TMLTask(ID iID, Priority iPriority, std::string iName, CPU** iCPU, unsigned int iNoOfCPUs);
	///Destructor
	virtual ~TMLTask();
	///Returns the priority of the task
	/**
      	\return Priority
    	*/
	inline Priority getPriority() const {return _priority;}
	///Returns the end of the last scheduled transaction of the task
	/**
      	\return End of transaction
    	*/
	inline TMLTime getEndLastTransaction() const {return _endLastTransaction;}
	///Returns a pointer to the current command of the task
	/**
      	\return Pointer to the current command
    	*/
	inline TMLCommand* getCurrCommand() const {return _currCommand;}
	///Sets the pointer to the current command of the task
	/**
      	\param iCurrCommand Pointer to the current command
    	*/
	inline void setCurrCommand(TMLCommand* iCurrCommand){_currCommand=iCurrCommand;}
	///Return a pointer to the CPU on which the task in running
	/**
      	\return Pointer to the CPU
    	*/
	inline CPU* getCPU() const {return _currentCPU;}
	///Returns a string representation of the task
	/**
	\return Detailed string representation
	*/
	virtual std::string toString() const {return _name;}
	///Returns a short string representation of the Task
	/**
	\return Short string representation
	*/
	std::string toShortString() const;
	///Returns the unique ID of the task
	/**
      	\return Unique ID
    	*/ 
	inline ID getID() const {return _ID;}
#ifdef ADD_COMMENTS

	///Adds a new execution comment to the internal list
	/**
      	\param iComment Pointer to the comment
    	*/ 
	inline void addComment(Comment* iComment) {_commentList.push_back(iComment);}

	///Returns the next execution comment (pointed to by _posCommentList)
	/**
      	\param iInit Indicates if the list iterator has to be reset to the beginning of the list
	\param oComment This pointer to the comment object is returned to the callee
	\return String representation of the comment
    	*/ 
	std::string getNextComment(bool iInit, Comment*& oComment);
#endif
	void getNextSignalChange(bool iInit, SignalChangeData* oSigData);
	///Adds a given transaction to the internal transaction list
	/**
      	\param iTrans Pointer to the transaction
    	*/ 
	void addTransaction(TMLTransaction* iTrans);
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void streamBenchmarks(std::ostream& s) const;
	virtual void reset();
	///Returns a pointer to the task variable specified by its name
	/**
	\param iVarName Name of the task variable
	\param oIsId Is set to true if an ID was passed to this function 
	\return Pointer to the variable
	*/
	ParamType* getVariableByName(const std::string& iVarName ,bool& oIsId);
	///Searches for a Command based on its ID
	/**
	\param iID ID of the Command
	\return Pointer to the Commmand
	*/
	inline TMLCommand* getCommandByID(ID iID) {return _commandHash[iID];}
	///Adds a new command to the internal list
	/**
	\param iID ID of the command
	\param iCmd Pointer to the command
	*/
	inline void addCommand(ID iID, TMLCommand* iCmd) {_commandHash[iID]=iCmd;}
	///Returns a pointer to the task variable specified by its ID
	/**
	\param iVarID ID of the task variable
	\return Pointer to the variable
	*/
	inline ParamType* getVariableByID(ID iVarID) {return _varLookUpID[iVarID];}
	inline void streamStateXML(std::ostream& s) const {streamBenchmarks(s);}
	///Returns an iterator for the internal variable ID hash table
	/**
	\return Const iterator for variable table
	*/
	inline const VariableLookUpTableID& getVariableLookUpTableID() const{return _varLookUpID;}
	///Returns an iterator for the internal variable Name hash table
	/**
	\return Const iterator for variable table
	*/
	const VariableLookUpTableName& getVariableLookUpTableName() const{return _varLookUpName;}
	///Is called when a stop command is encountered
	void finished();
	///Returns the current task state 
	/**
	\return Task state: UNKNOWN, SUSPENDED, READY, RUNNING, TERMINATED
	*/
	unsigned int getState() const;
	TMLTransaction* getNextTransaction(TMLTime iEndSchedule) const;
	/////Returns the hash value for the current task state
	////**
	//\param iLiveVarList Bitmap of live variables
	//\param iHash Hash Algorithm Object
	//*/
	//virtual void getStateHash(const char* iLiveVarList, HashAlgo* iHash) const =0;
	///Returns the instance number of this task
	/**
	\return Instance number 
	*/
	inline unsigned int getInstanceNo() {return _myInstance;}
	////Notifies the Task of being scheduled by a CPU
	////**
	//\param iCPU CPU that has scheduled the Task
	//*/
	//void transWasScheduled(SchedulableDevice* iCPU);
	////Resets the flag indicating that the Task has been scheduled
	//void resetScheduledFlag();
	////Invalidates the schedule of all cores the task is mapped onto 
	//void setRescheduleFlagForCores();
	///Returns the current state hash
	/**
	\return Current state hash
	*/
	virtual HashValueType getStateHash()=0;
	/////Returns whether the current task state has been encountered before
	////**
	//\return Common execution flag
	//*/
	//bool getCommonExecution() const;
	/////Sets the common execution flag
	////**
	//\param iCommonExecution Common execution flag
	//*/
	//void setCommonExecution(bool iCommonExecution);
	void refreshStateHash(const char* iLiveVarList);
	void schedule2TXT(std::ostream& myfile) const;
	int hasRunnableTrans(CPU* iCPU);
protected:
	///ID of the task
	ID _ID;
	///Name of the task
	std::string _name;
	///End of the last scheduled transaction of the task
	TMLTime _endLastTransaction;
	///Pointer to the current command of the task
	TMLCommand* _currCommand;
	///Pointer to the first command of the task
	TMLCommand* _firstCommand;
	///Pointer to the CPU which currently executes the task, can be zero in case the Task is mapped onto a multicore CPU
	CPU* _currentCPU;
	///Array containing all the cores the task is mapped onto
	CPU** _cpus;
	///Number of cores assigned to the task
	unsigned int _noOfCPUs;
#ifdef ADD_COMMENTS
	///Comment list
	CommentList _commentList;
	///Actual position within comment list (used for HTML output)
	CommentList::iterator _posCommentList;
#endif
	///List of scheduled transactions
	TransactionList _transactList;
	///State variable for the VCD output
	vcdTaskVisState _vcdOutputState;
	///Array of static comments concerning the control flow of the task
	std::string* _comment;
	///Busy cycles since simulation start
	TMLTime _busyCycles;
	///Sum of contention delay of CPU transactions
	unsigned long _CPUContentionDelay;
	///Number of transactions which have been executed on a CPU
	unsigned long _noCPUTransactions;
	///Look up table for task variables (by name)
	VariableLookUpTableName _varLookUpName;
	///Look up table for task variables (by ID)
	VariableLookUpTableID _varLookUpID;
	///Hash table containing commands
	CommandHashTab _commandHash;
	///Is true until the first transaction of a task is executed
	bool _justStarted;
	///Instace counter
	static unsigned int _instanceCount;
	///Consecutive number of this task instance
	unsigned int _myInstance;
	///Indicates whether this task has already been scheduled
	/*bool _isScheduled;
	///Last established state Hash
	HashValueType _lastStateHash;
	///Flag indicating whether the task state has been encoutered before
	bool _commonExecution;*/
	HashAlgo _stateHash;
	const char* _liveVarList;
	bool _hashInvalidated;
};

#endif
