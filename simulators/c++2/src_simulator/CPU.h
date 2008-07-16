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

#ifndef CPUH
#define CPUH

#include <definitions.h>
#include <SchedulableDevice.h>
#include <TraceableDevice.h>

class TMLTask;
class TMLTransaction;
class Bus;

enum vcdCPUVisState
    {
	END_IDLE_CPU,
	END_PENALTY_CPU,
	END_TASK_CPU
};

///Simulates the bahavior of a CPU and an operating system
class CPU: public SchedulableDevice, public TraceableDevice{
public:
	///Constructor
    	/**
      	\param iName Name of the CPU
	\param iTimePerCycle 1/Processor frequency
	\param iCyclesPerExeci Cycles needed to execute one execi unit
	\param iPipelineSize Pipeline size
	\param iTaskSwitchingCycles Task switching penalty in cycles
	\param iBranchingMissrate Branching prediction miss rate in %
	\param iChangeIdleModeCycles Cycles needed to switch into indle mode
	\param iCyclesBeforeIdle Idle cycles which elapse before entering idle mode
    	*/
	CPU(std::string iName, TMLTime iTimePerCycle, unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc, unsigned int iPipelineSize, unsigned int iTaskSwitchingCycles, unsigned int iBranchingMissrate, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle, unsigned int ibyteDataSize);
	///Destructor
	virtual ~CPU();
	///Determines the next CPU transaction to be executed
	virtual void schedule()=0;
	///Stores a new task in the internal task list
	/**
      	\param iTask Pointer to the task to add
    	*/
	void registerTask(TMLTask* iTask);
	///Add a transaction waiting for execution to the internal list
	/**
      	\param iTrans Pointer to the transaction to add
    	*/
	virtual void registerTransaction(TMLTransaction* iTrans)=0;
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	virtual void addTransaction()=0;
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	TMLTransaction* getNextTransaction() const;
	///Truncates the next transaction at time iTime
	/**
	\param iTime Indicates at what time the transaction should be truncated
	*/
	TMLTime truncateNextTransAt(TMLTime iTime);
	///Returns the unique ID of the CPU
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID();
	///Returns a string representation of the CPU
	/**
	\return Detailed string representation
	*/
	std::string toString();
	///Returns a short string representation of the transaction
	/**
	\return Short string representation
	*/
	std::string toShortString();
	///Writes a HTML representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	void schedule2HTML(std::ofstream& myfile);
	///Creates a string representation of the next signal change of the device (VCD format)
	/**
      	\param iInit If init is true, the methods starts from the first transaction
	\param oSigChange String representation of the signal change
	\param oNoMoreTrans Is true if the last transaction is processed 
	\return Time when the signal change occurred
    	*/
	TMLTime getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans);
	///Returns the scheduled transaction one after another
	/**
      	\param iInit If init is true, the methods starts from the first transaction 
	\return Pointer to the next transaction
    	*/
	TMLTransaction* getTransactions1By1(bool iInit);
protected:
	///Calculates the start time and the length of the next transaction
	void calcStartTimeLength();
	///Name of the CPU
	std::string _name;
	///List of all tasks running on the CPU
	TaskList _taskList;
	///List containing all already scheduled transactions
	TransactionList _transactList;
	///End time of the last scheduled transaction
	TMLTime _endSchedule;
	///Pointer to the next transaction to be executed
	TMLTransaction* _nextTransaction;
	///Pointer to the last transaction which has been executed
	TMLTransaction* _lastTransaction;
	///Pointer to the bus which will be accessed by the next transaction
	Bus* _busNextTransaction;
	
	///Unique ID of the CPU
	unsigned int _myid;
	///Class variable counting the number of CPU instances
	static unsigned int _id;
	
	///1/Processor frequency
	TMLTime _timePerCycle;
	///Pipeline size
	unsigned int _pipelineSize;
	///Task switching penalty in cycles
	unsigned int _taskSwitchingCycles;
	///Branching prediction miss rate
	unsigned int _brachingMissrate;
	///Cycles needed to switch into indle mode
	unsigned int _changeIdleModeCycles;
	///Idle cycles which elapse before entering idle mode
	unsigned int _cyclesBeforeIdle;
	///Cycles needed to execute one execi unit
	unsigned int _cyclesPerExeci;

	//values deduced from CPU parameters 
	///Time needed to execute one execi unit
	TMLTime _timePerExeci;
	///Task switching penalty in time units
	TMLTime _taskSwitchingTime;
	///Idle time which elapses before entering idle mode
	TMLTime _timeBeforeIdle;
	///Time needed to switch into idle mode
	TMLTime _changeIdleModeTime;
	///_pipelineSize * _timePerExeci
	TMLTime _pipelineSizeTimesExeci;
	///_brachingMissrate * _pipelineSize
	unsigned int _missrateTimesPipelinesize;
	
	//varibales for branch miss calculation
	///Indicates the number of commands executed since the last branch miss
	unsigned int _branchMissReminder;
	///Potentially new value of _branchMissReminder
	unsigned int _branchMissTempReminder;

	///Actual position within transaction list (used for vcd output)
	TransactionList::iterator _posTrasactListVCD;
	///EndTime of the transaction before _posTransactList (used for vcd output)
	TMLTime _previousTransEndTime;
	///State variable for the VCD output
	vcdCPUVisState _vcdOutputState;
	///State variable for consecutive Transaction output
	TransactionList::iterator _posTrasactListGraph;
};

#endif
