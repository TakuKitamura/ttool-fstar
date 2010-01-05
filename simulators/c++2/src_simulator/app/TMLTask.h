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
#include <TaskListener.h>
#include <WorkloadSource.h>

class TMLCommand;
class CPU;
class Comment;

enum vcdTaskVisState
    {
	END_TRANS,
	BETWEEN_TRANS,
	START_TRANS
};

class TMLTask: public TraceableDevice, public ListenerSubject <TaskListener>, public WorkloadSource{
public:	
	///Constructor
    	/**
      	\param iID ID of the Task
	\param iPriority Priority of the task
	\param iName Name of the task
	\param iCPU pointer to the CPU which executes the task
    	*/
	TMLTask(unsigned int iID, unsigned int iPriority, std::string iName, CPU* iCPU);
	///Destructor
	virtual ~TMLTask();
	///Returns the priority of the task
	/**
      	\return Priority
    	*/
	unsigned int getPriority() const;
	///Returns the end of the last scheduled transaction of the task
	/**
      	\return End of transaction
    	*/
	TMLTime getEndLastTransaction() const;
	///Returns a pointer to the current command of the task
	/**
      	\return Pointer to the current command
    	*/
	TMLCommand* getCurrCommand() const;
	///Sets the pointer to the current command of the task
	/**
      	\param iCurrCommand Pointer to the current command
    	*/
	void setCurrCommand(TMLCommand* iCurrCommand);
	///Return a pointer to the CPU on which the task in running
	/**
      	\return Pointer to the CPU
    	*/
	CPU* getCPU() const;
	///Returns a string representation of the task
	/**
	\return Detailed string representation
	*/
	virtual std::string toString() const;
	///Returns a short string representation of the Task
	/**
	\return Short string representation
	*/
	std::string toShortString() const;
	///Returns the unique ID of the task
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID() const;
#ifdef ADD_COMMENTS
	///Adds a new execution comment to the internal list
	/**
      	\param iComment Pointer to the comment
    	*/ 
	void addComment(Comment* iComment);
	///Returns the next execution comment (pointed to by _posCommentList)
	/**
      	\param iInit Indicates if the list iterator has to be reset to the beginning of the list
	\param oComment This pointer to the comment object is returned to the callee
	\return String representation of the comment
    	*/ 
	std::string getNextComment(bool iInit, Comment*& oComment);
#endif
	TMLTime getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans);
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
	ParamType* getVariableByName(std::string& iVarName ,bool& oIsId);
	///Searches for a Command based on its ID
	/**
	\param iID ID of the Command
	\return Pointer to the Commmand
	*/
	TMLCommand* getCommandByID(unsigned int iID);
	///Adds a new command to the internal list
	/**
	\param iID ID of the command
	\param iCmd Pointer to the command
	*/
	void addCommand(unsigned int iID, TMLCommand* iCmd);
	///Returns a pointer to the task variable specified by its ID
	/**
	\param iVarID ID of the task variable
	\return Pointer to the variable
	*/
	ParamType* getVariableByID(unsigned int iVarID);
	void streamStateXML(std::ostream& s) const;
	///Returns an iterator for the internal variable ID hash table
	/**
	\param iEnd true for iterator pointing to the end of the table, false for iterator pointing to the first element
	\return Const iterator for variable table
	*/
	VariableLookUpTableID::const_iterator getVariableIteratorID(bool iEnd) const;
	///Returns an iterator for the internal variable Name hash table
	/**
	\param iEnd true for iterator pointing to the end of the table, false for iterator pointing to the first element
	\return Const iterator for variable table
	*/
	VariableLookUpTableName::const_iterator getVariableIteratorName(bool iEnd) const;
	///Is called when a stop command is encountered
	void finished();
	///Returns the current task state 
	/**
	\return Task state: UNKNOWN, SUSPENDED, READY, RUNNING, TERMINATED
	*/
	unsigned int getState() const;
	TMLTransaction* getNextTransaction() const;
	///Returns the hash value for the current task state
	/**
	\return Hash Value
	*/
	virtual unsigned long getStateHash() const=0;
protected:
	///ID of the task
	unsigned int _ID;
	///Name of the task
	std::string _name;
	///End of the last scheduled transaction of the task
	TMLTime _endLastTransaction;
	///Pointer to the current command of the task
	TMLCommand* _currCommand;
	///Pointer to the first command of the task
	TMLCommand* _firstCommand;
	///Pointer to the CPU which executes the task
	CPU* _cpu;
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
	unsigned long _busyCycles;
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
};

#endif
