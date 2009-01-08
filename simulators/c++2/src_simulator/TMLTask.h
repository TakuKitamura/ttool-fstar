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

class TMLCommand;
class CPU;

enum vcdTaskVisState
    {
	END_TRANS,
	BETWEEN_TRANS,
	START_TRANS
};

class TMLTask: public TraceableDevice, public Serializable{
public:	
	///Constructor
    	/**
      	\param iPriority Priority of the task
	\param iName Name of the task
	\param iCPU pointer to the CPU which executes the task
    	*/
	TMLTask(unsigned int iPriority, std::string iName, CPU* iCPU);
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
	CPU* getCPU();
	///Returns a string representation of the task
	/**
	\return Detailed string representation
	*/
	virtual std::string toString();
	///Returns a short string representation of the Task
	/**
	\return Short string representation
	*/
	std::string toShortString();
	///Returns the unique ID of the task
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID();
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
	///Returns the next signal change (for vcd output)
	/**
      	\param iInit Indicates if the list iterator has to be reset to the beginning of the list
	\param oSigChange This string representation of the signal change is returned to the callee
	\param oNoMoreTrans This flag signals to the callee that no more signal changes are available
	\return String representation of comment
    	*/ 
	TMLTime getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans);
	///Adds a given transaction to the internal transaction list
	/**
      	\param iTrans Pointer to the transaction
    	*/ 
	void addTransaction(TMLTransaction* iTrans);
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void streamBenchmarks(std::ostream& s);
protected:
	///Name of the task
	std::string _name;
	///Priority of the task
	unsigned int _priority;
	///End of the last scheduled transaction of the task
	TMLTime _endLastTransaction;
	///Pointer to the current command of the task
	TMLCommand* _currCommand;
	///Pointer to the CPU which executes the task
	CPU* _cpu;
	///Unique ID of the task
	unsigned int _myid;
	///Class variable counting the number of task instances
	static unsigned int _id;
	///Comment list
	CommentList _commentList;
	///Actual position within comment list (used for HTML output)
	CommentList::iterator _posCommentList;
	///List of scheduled transactions
	TransactionList _transactList;
	///Actual position within transaction list (used for vcd output)
	TransactionList::iterator _posTrasactList;
	///EndTime of the transaction before _posTransactList (used for vcd output)
	TMLTime _previousTransEndTime;
	///State variable for the VCD output
	vcdTaskVisState _vcdOutputState;
	///Array of static comments concerning the control flow of the task
	std::string* _comment;
	///Busy cycles since simulation start
	unsigned long _busyCycles;
};

#endif
