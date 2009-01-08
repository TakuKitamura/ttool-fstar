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

#ifndef simkernH
#define simkernH

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

///Returns a pointer to the transaction with the lowest end time proposed by CPU schedulers
/**
\param iCPUlist CPU list
\param oCPU CPU which is running the returned transaction
\return Pointer to transaction with lowest end time
*/
TMLTransaction* getTransLowestEndTime(CPUList& iCPUlist, CPU*& oCPU);
///Writes a HTML representation of the schedule of CPUs and buses to an output file
/**
\param iSchedList List containing components to schedule
\param iBusList Bus list
\param iLen Number of command line arguments
\param iArgs Pointer to command line arguments
*/
void schedule2HTML(SchedulingList& iSchedList,BusList& iBusList, int iLen, char** iArgs);
/////Invokes the schedule() method of all CPUs and buses
/////**
//\param iCPUlist CPU list
//\param iBusList Bus list
//*/
//void scheduleCPUBus(CPUList& iCPUlist,BusList& iBusList);
/////Invokes the schedule() method of one CPU and all buses
/////**
//\param iCPU CPU
//\param iBusList Bus list
//*/
//void scheduleCPUBus(CPU* iCPU,BusList& iBusList);
///Runs the simulation
/**
\param iSchedList List containing components to schedule
\param iBusList Bus list
*/
void simulate(SchedulingList& iSchedList, BusList& buslist);
///Writes simulation traces in VCD format to an output file
/**
\param iVcdList List of components whose traces are output
\param iLen Number of command line arguments
\param iArgs Pointer to command line arguments
*/
void schedule2VCD(TraceableDeviceList& iVcdList, int iLen, char** iArgs);
///Writes the simulation graph to an output file
/**
\param iCPUlist CPU list
\param iLen Number of command line arguments
\param iArgs Pointer to command line arguments
*/
void schedule2Graph(CPUList& iCPUlist, int iLen, char** iArgs);
///Writes a HTML representation of the schedule of CPUs and buses to an output file
/**
\param iLen Number of command line arguments
\param iArgs Pointer to command line arguments
\param iComp Flag to search for in within the argument list
\param iDefault Default name of an output file
\return Output file name
*/
const std::string getArgs(int iLen, char** iArgs, const std::string& iComp, const std::string& iDefault);
///Shows a help text explaining the usage of the simulator
void printHelp();
///Writes a plain text representation of the schedule of CPUs to an output file
/**
\param iSchedList List containing components to schedule
\param iLen Number of command line arguments
\param iArgs Pointer to command line arguments
*/
void schedule2TXT(SchedulingList& iSchedList, int iLen, char** iArgs);
///Calls streamBenchmarks of all traceable devices contained in iVcdList
/**
\param s Reference to output stream object
\param iVcdList List of devices which are able to stream their benchmark data
*/
void streamBenchmarks(std::ostream& s, TraceableDeviceList& iVcdList);
#endif

