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

#ifndef CPURRLH
#define CPURRLH

#include <definitions.h>
#include <CPU.h>


class TMLTransaction;

///Simulates the bahavior of a CPU with a Priority based scheduling policy (priority queue version)
class CPUPBL: public CPU{
public:
	///Constructor
    	/**
	\param iID ID of the CPU
      	\param iName Name of the CPU
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
	CPUPBL(unsigned int iID, std::string iName, TMLTime iTimePerCycle, unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc, unsigned int iPipelineSize, unsigned int iTaskSwitchingCycles, unsigned int iBranchingMissrate, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle, unsigned int ibyteDataSize);
	///Destructor
	~CPUPBL();
	void schedule();
	void registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice);
	bool addTransaction();
	virtual void reset(); 
protected:
	///List of transaction in the future
	FutureTransactionQueue _futureTransQueue;
	///List of transaction in the past
	PastTransactionQueue _pastTransQueue;
};

#endif
