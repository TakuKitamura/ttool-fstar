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

#include <Simulator.h>
#include <Parameter.h>
#include <definitions.h>
#include <CPU.h>
#include <CPUPB.h>
#include <CPUPBL.h>
#include <CPURR.h>
#include <Bus.h>
#include <Bridge.h>
#include <Memory.h>
#include <TMLbrbwChannel.h>
#include <TMLnbrnbwChannel.h>
#include <TMLbrnbwChannel.h>
#include <TMLEventBChannel.h>
#include <TMLEventFChannel.h>
#include <TMLEventFBChannel.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLTask.h>
#include <SimComponents.h>
#include <Server.h>
#include <SimServSyncInfo.h>
#include <ListenersSimCmd.h>

#define RECUR_DEPTH 20

#define MSG_CMPNFOUND "Component not found"
#define MSG_CMDNFOUND "Command not found"
#define MSG_CMDNIMPL "Command currently not implemented"
#define MSG_FILEERR "Cannot open file "
#define SIM_READY "ready"
#define SIM_BUSY "busy"
#define SIM_TERM "term"

class SimComponents;
class SimServSyncInfo;
class ServerIF;

///Simulation engine and output capabilities
class Simulator{
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
	//bool execAsyncCmd(const char* iCmd);
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
	bool runTillTimeX(unsigned int iTime, TMLTransaction*& oLastTrans);
	///Runs the simulation for iTime time units
	/**
	\param iTime Number of time units
	\param oLastTrans Returns the last transaction executed during a simulation
	\return Return value of simulate() function
	*/
	bool runXTimeUnits(unsigned int iTime, TMLTransaction*& oLastTrans);
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
	bool runToNextChoiceCommand(TMLTransaction*& oLastTrans);
	///Runs the automatic exploration of several branches of control flow. Choice commands to be explored must be marked with a breakpoint.
	/**
	\param iDepth Maximal recursion depth
	\param iPrevID ID of the parent leaf
	*/
	void exploreTree(unsigned int iDepth, unsigned int iPrevID);
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
	void schedule2Graph() const;
	///Writes a plain text representation of the schedule of CPUs to an output file
	/**
	\param iTraceFileName Name of the output trace file
	*/
	void schedule2TXT(std::string& iTraceFileName) const;
	///Is true if the simulator is busy
	/**
	\return Busy flag
	*/
	bool isBusy();
protected:
	///Runs the simulation
	/**
	\return returns true if the simulation is completed, false otherwise
	*/
	//bool simulate();
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
	//void decodeCommand(char* iCmd);
	void decodeCommand(std::string iCmd);
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
	///Pointer to synchronization structure
	SimServSyncInfo* _syncInfo;
	///Pointer to structure encapsulating architecture and application objects
	SimComponents* _simComp;
	///Simulator Busy flag
	bool _busy;
	///Simulation terminated flag
	bool _simTerm;
	///Counts the leafs of the tree made up by explored control flow branches
	unsigned int _leafsID;
	///Keeps track of all breakpoints set during the simulation 
	BreakpointSet _breakpoints;
	RunTillNextRandomChoice _randChoiceBreak;
};
#endif
