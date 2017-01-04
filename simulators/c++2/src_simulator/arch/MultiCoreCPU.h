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

#ifndef MultiCoreCPUH
#define MultiCoreCPUH

#include <definitions.h>
#include <SchedulableDevice.h>
#include <SchedulableCommDevice.h>
#include <TraceableDevice.h>
#include <BusMaster.h>
#include <CPU.h>

class TMLTask;
class TMLTransaction;
class Bus;

/*enum vcdCPUVisState
    {
	END_IDLE_CPU,
	END_PENALTY_CPU,
	END_TASK_CPU
};*/

///Simulates the bahavior of a CPU and an operating system
class MultiCoreCPU: public CPU{
public:
	///Constructor
    	/**
      	\param iID ID of the CPU
	\param iName Name of the CPU
	\param iScheduler Pointer to the scheduler object
	\param iTimePerCycle 1/Processor frequency
	\param iCyclesPerExeci Cycles needed to execute one EXECI unit
	\param iCyclesPerExecc Cycles needed to execute one EXECC unit
	\param iPipelineSize Pipeline size
	\param iTaskSwitchingCycles Task switching penalty in cycles
	\param iBranchingMissrate Branching prediction miss rate in %
	\param iChangeIdleModeCycles Cycles needed to switch into indle mode
	\param iCyclesBeforeIdle Idle cycles which elapse before entering idle mode
	\param ibyteDataSize Machine word length
    	*/
	MultiCoreCPU(ID iID, std::string iName, WorkloadSource* iScheduler, WorkloadSource* iScheduler2,TMLTime iTimePerCycle, unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc, unsigned int iPipelineSize, unsigned int iTaskSwitchingCycles, unsigned int iBranchingMissrate, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle, unsigned int ibyteDataSize);
	///Destructor
	virtual ~MultiCoreCPU();
	///Determines the next CPU transaction to be executed
	virtual void schedule();
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	virtual bool addTransaction(TMLTransaction* iTransToBeAdded);
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	TMLTransaction* getNextTransaction();
	void truncateAndAddNextTransAt(TMLTime iTime);
	///Returns a string representation of the CPU
	/**
	\return Detailed string representation
	*/
	inline std::string toString() const {return _name;}
	///Returns a short string representation of the transaction
	/**
	\return Short string representation
	*/
	std::string toShortString() const;
	///Writes a HTML representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	void schedule2HTML(std::ofstream& myfile) const;
	void getNextSignalChange(bool iInit, SignalChangeData* oSigData);
	///Writes a plain text representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	void schedule2TXT(std::ofstream& myfile) const;
	
	/**
      	\param glob refers to the output stream
    	*/
	int allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const;

	void latencies2XML(std::ostringstream& glob, int id1, int id2);
	virtual void streamBenchmarks(std::ostream& s) const;
	virtual void reset();
	inline void streamStateXML(std::ostream& s) const {streamBenchmarks(s);}
	std::istream& readObject(std::istream &is);
	std::ostream& writeObject(std::ostream &os);
protected:
	///Truncates the next transaction at time iTime

	
	/**
	\param iTime Indicates at what time the transaction should be truncated
	*/
	TMLTime truncateNextTransAt(TMLTime iTime);
	///Calculates the start time and the length of the next transaction
	/**
	\param iTimeSlice CPU Time slice granted by the scheduler
	*/
	void calcStartTimeLength(TMLTime iTimeSlice);
	///Determines the correct bus master of this CPU connected to the same bus as bus master iDummy
	/**
	\param iDummy Dummy Bus Master
	*/
	BusMaster* getMasterForBus(BusMaster* iDummy);
	///Pointer to the bus which will be accessed by the next transaction
	BusMaster* _masterNextTransaction;
	///1/Processor frequency
	TMLTime _timePerCycle;
#ifdef PENALTIES_ENABLED
	///Pipeline size
	unsigned int _pipelineSize;
	///Task switching penalty in cycles
	unsigned int _taskSwitchingCycles;
	///Branching prediction miss rate
	unsigned int _brachingMissrate;
	///Cycles needed to switch to idle mode
	unsigned int _changeIdleModeCycles;
	///Idle cycles which elapse before entering idle mode
	unsigned int _cyclesBeforeIdle;
#endif
	///Cycles needed to execute one execi unit
	unsigned int _cyclesPerExeci;
	///Time needed to execute one execi unit
	float _timePerExeci;
#ifdef PENALTIES_ENABLED
	///Task switching penalty in time units
	TMLTime _taskSwitchingTime;
	///Idle time which elapses before entering idle mode
	TMLTime _timeBeforeIdle;
	///Time needed to switch into idle mode
	TMLTime _changeIdleModeTime;
#endif
	///State variable for the VCD output
	vcdCPUVisState _vcdOutputState;
};

#endif
