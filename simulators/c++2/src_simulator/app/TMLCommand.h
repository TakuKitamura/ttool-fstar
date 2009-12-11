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

#ifndef TMLCommandH
#define TMLCommandH

#include <definitions.h>
#include <Serializable.h>
#include <ListenerSubject.h>

class TMLTransaction;
class TMLTask;
class TMLChannel;
class CommandListener;
class Comment;
class SimComponents;

///This class defines the basic interfaces and functionalites of a TML command. All specific commands are derived from this base class. 
class TMLCommand: public Serializable, public ListenerSubject <CommandListener> {
public:
	///Constructor
    	/**
      	\param iID ID of the command
	\param iTask Pointer to the task the command belongs to
	\param iLength Virtual length of the command
	\param iParamFunc Pointer to a parameter function
	\param iNbOfNextCmds Number of next commands
    	*/
	//TMLCommand(unsigned int iID, TMLTask* iTask, TMLLength iLength, ParamFuncPointer iParamFunc, unsigned int iNbOfNextCmds);
	TMLCommand(unsigned int iID, TMLTask* iTask, TMLLength iLength, unsigned int iNbOfNextCmds);
	///Destructor
	virtual ~TMLCommand();
	///Initializes the command and passes the control flow to the prepare() method of the next command if necessary
	/**This function calls prepareNextCommand() which is implemented by subclasses of TMLCommand
      	\return True if there was a transaction to prepare
	\sa prepareNextTransaction()
	*/
	TMLCommand* prepare(bool iInit);
	///Updates the inner state of the command as well as the state of all dependent objects (channel, bus,...)
	virtual void execute()=0;
	///Assigns a value to the pointer referencing the array of next commands
	/**
	\param iNextCommand Pointer to an array of pointers to the next commands
	*/
	void setNextCommand(TMLCommand** iNextCommand);
	///Returns a pointer to the task the command belongs to
	/**
	\return Pointer to the task
	*/
	TMLTask* getTask() const;
	///Returns a pointer to the current transaction
	/**
	\return Pointer to the current transaction
	*/
	TMLTransaction* getCurrTransaction() const;
	///Returns a pointer to the task which could be unblocked by the command
	/**
	\return Pointer to the dependent task
	*/
	virtual TMLTask* getDependentTask()const =0;
	///Returns a pointer to the channel on which the command performs operations
	/**
	\return Pointer to the channel
	\sa channelUnknown()
	*/
	virtual TMLChannel* getChannel() const;
	/////Indicates if the channel can be determined
	////**
	//\return False if a nullpointer returned by getChannel() means the that command does not depend on any channel, true if a nullpointer returned by getChannel() indicates that the channel cannot be determined
	//\sa getChannel()
	//*/
	//virtual bool channelUnknown() const;
	/////Returns a pointer to the parameter data structure
	////**
	//\return Pointer to parameter data structure
	//*/
	//virtual ParamFuncPointer getParamFuncPointer() const;
	virtual void setParams(Parameter<ParamType>& ioParam);
	///Returns a string representation of the command
	/**
	\return Detailed string representation
	*/
	virtual std::string toString() const;
	///Returns a short string representation of the command
	/**
	\return Short string representation
	*/
	virtual std::string toShortString() const =0;
	///Returns a short string representation of the command type
	/**
	\return Short string representation of command type
	*/
	virtual std::string getCommandStr() const=0;
#ifdef ADD_COMMENTS	
	///Translates a comment into a readable string
	/**
	\param iCom Pointer to comment
	\return Sring representation of the comment
	*/
	virtual std::string getCommentString(Comment* iCom) const;
#endif
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	void reset();
	///Registers a listener at all TMLCommand instances
	/**
	\param  iListener Pointer to the listener
	*/
	static void registerGlobalListener(CommandListener* iListener);
	///Registers a listener at all TMLCommand instances of a specific type
	/**
	\param iListener Pointer to the listener
	\param aTask Only commands of this task are taken into account, if set to 0 all tasks are considered
	*/
	template<typename T> static void registerGlobalListenerForType(CommandListener* iListener, TMLTask* aTask);
	///Removes a listener at all TMLCommand instances
	/**
	\param  iListener Pointer to the listener
	*/
	static void removeGlobalListener(CommandListener* iListener);
	///Returns the unique ID of the command
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID() const;
	///Sets a new breakpoint
	/**
      	\param iBreakp Pointer to breakpoint
    	*/ 
	void setBreakpoint(CommandListener* iBreakp);
	///Removes the breakpoint
	void removeBreakpoint();
	///Returns the progress of the command
	/**
	\return Progress of the command
	*/
	TMLLength getProgress() const;
	///Sets the internal pointer to the simulation components
	/**
      	\param iSimComp Pointer to simulation components
    	*/ 
	static void setSimComponents(SimComponents* iSimComp);
	///Returns a pointer to the next command array and the number of successors of this command
	/**
	\param oNbOfCmd Number of successors of this command
	\return Pointer to next command array
	*/
	TMLCommand** getNextCommands(unsigned int& oNbOfCmd) const;
protected:
	///ID of the command
	unsigned int _ID;
	///Length of the command
	TMLLength _length;
	///Progress of the command (in execution units)
	TMLLength _progress;
	///Pointer to the current transaction
	TMLTransaction* _currTransaction;
	///Pointer to the task the command belongs to
	TMLTask* _task;
	///Pointer to an array of pointers to the next commands
	TMLCommand** _nextCommand;
	/////Pointer to the parameter function of the command
	//ParamFuncPointer _paramFunc;
	///Number of successors of this command
	unsigned int _nbOfNextCmds;
	///Breakpoint
	CommandListener* _breakpoint;
	/////Indicates if the breakpoint of the command has stopped the simulation  
	//bool _simStopped;
	///Is true until the first transaction of a task is executed
	bool _justStarted;
	///Determines the next command based on the _nextCommand array
	/**
	\return Pointer to the next command
	*/
	virtual TMLCommand* getNextCommand() const;
	///Special actions taken by subclasses of TMLCommand to prepare the next transaction
	/**
	\return True if there was a transaction to prepare
	\sa prepare()
	*/
	virtual TMLCommand* prepareNextTransaction()=0;
	///List of pointers to all TMLCommand instances
	static std::list<TMLCommand*> _instanceList;
	///Pointer to simulation components
	static SimComponents* _simComp;
};

#endif

