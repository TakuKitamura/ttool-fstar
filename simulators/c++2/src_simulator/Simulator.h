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

#define TAG_HEADER "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>"
#define TAG_STARTo "<siminfo>"
#define TAG_STARTc "</siminfo>"
#define TAG_ERRNOo "<error>"
#define TAG_ERRNOc "</error>"
#define TAG_MSGo "<msg>"
#define TAG_MSGc "</msg>"
#define TAG_TIMEo "<simtime>"
#define TAG_TIMEc "</simtime>"
#define TAG_CYCLESo "<simcycles>"
#define TAG_CYCLESc "</simcycles>"
#define TAG_TASKo "<task"
#define TAG_TASKc "</task>"
#define TAG_VARo "<var"
#define TAG_VARc "</var>"
#define TAG_STATUSo "<status>"
#define TAG_STATUSc "</status>"
#define TAG_GLOBALo "<global>"
#define TAG_GLOBALc "</global>"
#define TAG_CURRCMDo "<currcmd>"
#define TAG_CURRCMDc "</currcmd>"


class SimComponents;
class SimServSyncInfo;

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
	///Run the simulator in server mode
	void run();
	///Run the simulator in command line mode
	/**
	\param iLen Number of arguments
	\param iArgs Array withe arguments
	\return Returns true if simulation could be executed
	*/
	bool run(int iLen, char** iArgs);
	///Execute asynchronous command
	bool execAsyncCmd(char* iCmd);
	///Sends simulator status information to client
	void sendStatus();
protected:
	///Writes a HTML representation of the schedule of CPUs and buses to an output file
	void schedule2HTML() const;
	///Runs the simulation
	void simulate();
	///Writes simulation traces in VCD format to an output file
	void schedule2VCD() const;
	///Writes the simulation graph to an output file
	void schedule2Graph() const;
	///Writes a plain text representation of the schedule of CPUs to an output file
	void schedule2TXT() const;
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
	void decodeCommand(char* iCmd);
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
	///Pointer to synchronization structure
	SimServSyncInfo* _syncInfo;
	///Pointer to structure encapsulating architecture and application objects
	SimComponents* _simComp;
	///Name of output file for traces
	std::string _traceFileName;
	///Listener for command currently being executed
	TransactionListener* _currCmdListener;
	///Simulator Busy flag
	bool _busy;
};
#endif
