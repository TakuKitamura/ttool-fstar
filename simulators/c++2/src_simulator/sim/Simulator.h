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
#ifndef SimulatorH
#define SimulatorH

//#include <Parameter.h>
#include <definitions.h>
#include <GeneralListener.h>
#include <ListenerSubject.h>
#include <ListenersSimCmd.h>
//#include <CPU.h>
//#include <SingleCoreCPU.h>
//#include <RRScheduler.h>
//#include <PrioScheduler.h>
//#include <Bus.h>
//#include <Bridge.h>
//#include <Memory.h>
//#include <TMLbrbwChannel.h>
//#include <TMLnbrnbwChannel.h>
//#include <TMLbrnbwChannel.h>
//#include <TMLEventBChannel.h>
//#include <TMLEventFChannel.h>
//#include <TMLEventFBChannel.h>
//#include <TMLTransaction.h>
//#include <TMLCommand.h>
//#include <TMLTask.h>
//#include <SimComponents.h>
//#include <Server.h>
//#include <SimServSyncInfo.h>
//#include <ListenersSimCmd.h>
#ifdef EBRDD_ENABLED 
#include <ERC.h>
#include <ERB.h>
#endif

class CPU;
class TMLTransaction;
class TMLCommand;
class TMLTask;
class SchedulableCommDevice;

#define RECUR_DEPTH 20

#define MSG_CMPNFOUND "Component not found"
#define MSG_CMDNFOUND "Command not found"
#define MSG_CMDNIMPL "Command currently not implemented"
#define MSG_FILEERR "Cannot open file "
#define MSG_CONDERR "Condition cannot be compiled"
#define MSG_SIMSTOPPED "Simulation stopped"
#define MSG_SIMPAUSED "Simulation paused"
#define MSG_SIMENDED "Simulation completed"
#define MSG_COVREACHED "Coverage reached"
#define SIM_READY "ready"
#define SIM_BUSY "busy"
#define SIM_TERM "term"

class SimComponents;
class SimServSyncInfo;
class ServerIF;

///Simulation engine and output capabilities
class Simulator: public ListenerSubject<GeneralListener> {
public:
	///Constructor
	/**
	\param iSyncInfo Pointer to synchronization info structure
	*/
	Simulator(SimServSyncInfo* iSyncInfo);
	///Destructor
	~Simulator();
	///Runs the simulator in server mode
	void run();
	///Runs the simulator in command line mode
	/**
	\param iLen Number of arguments
	\param iArgs Array withe arguments
	\return Returns true if simulation could be executed
	*/
	ServerIF* run(int iLen, char** iArgs);
	///Execute asynchronous command
	/**
	\param iCmd Command string
	*/
	bool execAsyncCmd(const std::string& iCmd);
	///Sends simulator status information to client
	void sendStatus();
	///Run simulation until a breakpoint is encountered
	/**
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToNextBreakpoint(TMLTransaction*& oLastTrans);
	///Runs the simulation until iTrans transaction have been executed
	/**
	\param iTrans Number of transactions
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runXTransactions(unsigned int iTrans, TMLTransaction*& oLastTrans);
	///Runs the simulation until iTrans commands have been executed
	/**
	\param iCmds Number of commands
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runXCommands(unsigned int iCmds, TMLTransaction*& oLastTrans);
	///Runs the simulation until the simulation time is greater or equal than iTime
	/**
	\param iTime Simulation End Time
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runTillTimeX(TMLTime iTime, TMLTransaction*& oLastTrans);
	///Runs the simulation for iTime time units
	/**
	\param iTime Number of time units
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runXTimeUnits(TMLTime iTime, TMLTransaction*& oLastTrans);
	///Runs the simulation until a transaction on iBus is executed
	/**
	\param iBus Pointer to the bus
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToBusTrans(SchedulableCommDevice* iBus, TMLTransaction*& oLastTrans);
	///Runs the simulation until a transaction on iCPU is executed
	/**
	\param iCPU Pointer to the CPU
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToCPUTrans(SchedulableDevice* iCPU, TMLTransaction*& oLastTrans);
	///Runs the simulation until a transaction of iTask is executed
	/**
	\param iTask Pointer to the task
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToTaskTrans(TMLTask* iTask, TMLTransaction*& oLastTrans);
	///Runs the simulation until a transaction on Slave iSlave is executed
	/**
	\param iSlave Pointer to the Slave
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToSlaveTrans(Slave* iSlave, TMLTransaction*& oLastTrans);
	///Runs the simulation until a transaction on iChannel is executed
	/**
	\param iChannel Pointer to the Channel
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToChannelTrans(TMLChannel* iChannel, TMLTransaction*& oLastTrans);
	///Runs the simulation until a random choice command is encountered
	/**
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runToNextRandomCommand(TMLTransaction*& oLastTrans);
	///Runs the simulation until a given condition is satisfied
	/**
	\param iCond Condition expressed in terms of task variables of a given task
	\param iTask Task
	\param oLastTrans Returns the last transaction executed during a simulation
	\param oSuccess Indicates whether the condition could be compiled and is valid
	\return Return value of simulate() function
	*/
	bool runUntilCondition(std::string& iCond, TMLTask* iTask, TMLTransaction*& oLastTrans, bool& oSuccess);
	///Runs the automatic exploration of several branches of control flow. Choice commands to be explored must be marked with a breakpoint.
	/**
	\param iDepth Maximal recursion depth
	\param iPrevID ID of the parent leaf
	\param iDOTFile Handle of DOT file
	\param iAUTFile Handle of AUT file
	\param oTransCounter Reference to transaction counter
	*/
	void exploreTreeDOT(unsigned int iDepth, ID iPrevID, std::ofstream& iDOTFile, std::ofstream& iAUTFile, unsigned int& oTransCounter);

	///Runs the automatic exploration of several branches of control flow. Choice commands to be explored must be marked with a breakpoint.
	/**
	\param iDepth Maximal recursion depth
	\param iPrevID ID of the parent leaf
	\param iAUTFile Handle of AUT file
	\param oTransCounter Reference to transaction counter
	*/
	void exploreTree(unsigned int iDepth, ID iPrevID, std::ofstream& iAUTFile, unsigned int& oTransCounter);

	///Writes a HTML representation of the schedule of CPUs and buses to an output file
	void schedule2HTML(std::string& iTraceFileName) const;

	///Writes simulation traces in VCD format to an output file
	/**
	\param iTraceFileName Name of the output trace file
	*/
	void schedule2VCD(std::string& iTraceFileName) const;
	///Writes the simulation graph to an output file
	/**
	\param iTraceFileName Name of the output trace file
	*/
	void schedule2Graph(std::string& iTraceFileName) const;
	///Writes the simulation graph to a DOT and AUT file, for exploration mode
	/**
	\param iDOTFile Handle of DOT file
	\param iAUTFile Handle of AUT file
	\param iStartState ID of the state to begin with
	\param oTransCounter Reference to a transation counter
	\return ID of the last state writte to the files
	*/
	ID schedule2GraphDOT(std::ostream& iDOTFile, std::ostream& iAUTFile, ID iStartState, unsigned int& oTransCounter) const;
	///Writes the simulation graph to a AUT file, for exploration mode
	/**
	\param iAUTFile Handle of AUT file
	\param iStartState ID of the state to begin with
	\param oTransCounter Reference to a transation counter
	\return ID of the last state writte to the files
	*/
	ID schedule2GraphAUT(std::ostream& iAUTFile, ID iStartState, unsigned int& oTransCounter) const;
	///Writes a plain text representation of the schedule of CPUs to an output file
	/**
	\param iTraceFileName Name of the output trace file
	*/
	void schedule2TXT(std::string& iTraceFileName) const;

	/**
	\param glob Stream on which the XML answer shall be send to
	*/
	int allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const;
	
	void latencies2XML(std::ostringstream& glob, int id1, int id2);
	
	///Is true if the simulator is busy
	/**
	\return Busy flag
	*/
	bool isBusy();
	///Returns the time elapsed during simulation since the last reset
	/**
	\return Simulation duration
	*/
	inline long getSimDuration(){ return _simDuration;}

	inline std::string getEnd(){return _end;}
	std::list<std::pair<int,int> > latencyIds;
	void addLatencyIds(int id1, int id2){latencyIds.push_back(std::make_pair(id1, id2));};

protected:
	///Runs the simulation
	/**
	\return returns true if the simulation is completed, false otherwise
	*/
	bool simulate(TMLTransaction*& oLastTrans);
	///Returns a pointer to the transaction with the lowest end time proposed by CPU schedulers
	/**
	\param oResultDevice Pointer to the CPU which is running the returned transaction
	\return Pointer to transaction with lowest end time
	*/
	TMLTransaction* getTransLowestEndTime(SchedulableDevice*& oResultDevice) const;
	///Decodes a simulation command
	/**
	\param iCmd Pointer to the command
	\return Returns false if simulator should be terminated
	*/
	//inline void decodeCommand(std::string iCmd){std::ofstream aNullStream("/dev/null"); decodeCommand(iCmd, aNullStream);}
	void decodeCommand(std::string iCmd, std::ostream& iXmlOutStream=std::cout);
	///Searches for switches in the command line string
	/**
	\param iComp Command line switch to search for 
	\param iDefault Default output file name
	\param iLen Number of arguments
	\param iArgs Array withe arguments
	\return File name of output file, string is empty if output not requested
	*/
	const std::string getArgs(const std::string& iComp, const std::string& iDefault, int iLen, char** iArgs);
	///Print information about simulator command line usage
	void printHelp();
	///Writes information about the current command of a task to the given stream in XML format
	/**
	\param aTask Pointer to the task
	\param ioMessage Output stream
	*/
	void printCommandsOfTask(TMLTask* aTask, std::ostream& ioMessage);
	///Writes information about task variables to the given stream in XML format
	/**
	\param aTask Pointer to the task
	\param ioMessage Output stream
	*/
	void printVariablesOfTask(TMLTask* aTask, std::ostream& ioMessage);
	///Writes the current simulator state to a stream
	/**
	\param ioMessage output stream
	*/
	void writeSimState(std::ostream& ioMessage);
	///Checks if command may be impacted by a anction on a channel
	/**
	\param iCh Channel
	\param iCmd Command
	\return  Flag indicating if the command may be impacted
	*/
	bool channelImpactsCommand(TMLChannel* iCh, TMLCommand* iCmd);
	///Pointer to synchronization structure
	SimServSyncInfo* _syncInfo;
	///Pointer to structure encapsulating architecture and application objects
	SimComponents* _simComp;
	///Simulator Busy flag
	bool _busy;
	///Simulation terminated flag
	bool _simTerm;
	///Keeps track of all breakpoints set during the simulation 
	BreakpointSet _breakpoints;
	///Random choice breakpoint
	RunTillNextRandomChoice _randChoiceBreak;
	///Flag indicating if the simulator has previously been reset
	bool _wasReset;
	///Graph output path
	std::string _graphOutPath;
	///Longest runtime
	TMLTime _longRunTime;
	///Shortest runtime
	TMLTime _shortRunTime;
	///Flag indicating whether replies should be sent back to the server, not set in command line mode
	bool _replyToServer;
	///Branch coverage to target
	unsigned int _branchCoverage;
	///Statement coverage to target
	unsigned int _commandCoverage;
	///Termination flag for exploration mode;
	bool _terminateExplore;
	///Duration of Simulation
	long _simDuration;
	//branch coverage in exploration
	long _nbOfBranchesToExplore;
	long _nbOfBranchesExplored;
	std::string _end;
};
#endif
