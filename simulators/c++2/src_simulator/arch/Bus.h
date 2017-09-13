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

#ifndef BusH
#define BusH

#include <definitions.h>
#include <SchedulableCommDevice.h>
#include <TraceableDevice.h>

class CPU;
class TMLTransaction;

enum vcdBusVisState
    {
	END_IDLE_BUS,
	END_READ_BUS,
	END_WRITE_BUS,
	INIT_BUS
};

///Simulates the bahavior of a bus shared by several master devices
class Bus: public SchedulableCommDevice, public TraceableDevice {
public:
	///Constructor
    	/**
	\param iID ID of the bus
      	\param iName Name of the bus
	\param iScheduler Pointer to the scheduler object
      	\param iBurstSize Size of an atomic bus transaction
	\param ibusWidth Bus width
	\param iTimePerSample Transfer time per sample
	\param iChannelBasedPrio Flag indicating whether bus master based or channel based priorities are used to arbitrate the bus
    	*/
	Bus(ID iID, std::string iName, WorkloadSource* iScheduler, TMLLength iBurstSize, unsigned int ibusWidth, TMLTime iTimePerSample, bool iChannelBasedPrio);
	///Destructor
	virtual ~Bus();
	///Add a transaction waiting for execution to the internal list
	inline void registerTransaction() {_schedulingNeeded=true; _nextTransaction=0;}
	///Determines the next bus transaction to be executed
	void schedule();
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	bool addTransaction(TMLTransaction* iTransToBeAdded);
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	inline TMLTransaction* getNextTransaction() {
		if (_schedulingNeeded) schedule();
		return _nextTransaction;
	}
	///Returns the size of an atomic bus transaction
	/**
	\return Burst size
	*/
	inline TMLLength getBurstSize() const {return _burstSize;}
	///Returns a string representation of the Bus
	/**
	\return Detailed string representation
	*/
	inline std::string toString() const {return _name;}
	///Returns a short string representation of the bus
	/**
	\return Short string representation
	*/
	std::string toShortString() const;

	/**
      	\param glob refers to the output stream
    	*/
	int allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const;
	void latencies2XML(std::ostringstream& glob, unsigned int id1, unsigned int id2);
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
//	void schedule2HTML(std::ofstream& myfile) const;
	///Writes a plain text representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	void schedule2TXT(std::ofstream& myfile) const;
	
	void getNextSignalChange(bool iInit, SignalChangeData* oSigData);
	virtual void streamBenchmarks(std::ostream& s) const;
	virtual void reset();
	inline void streamStateXML(std::ostream& s) const {streamBenchmarks(s);}
	std::istream& readObject(std::istream &is);
	std::ostream& writeObject(std::ostream &os);
protected:
	///Calculates the start time and the length of the next transaction
	/**
	\param iTimeSlice Bus time slice granted by the scheduler
	*/
	void calcStartTimeLength(TMLTime iTimeSlice) const;
	///Size of an atomic bus transaction
	TMLLength _burstSize;
	///Dirty flag of the current scheduling decision
	bool _schedulingNeeded;
	///Inverse bus speed
	TMLTime _timePerSample;
	///Bus width in bytes
	unsigned int _busWidth;
	///State variable for the VCD output
	vcdBusVisState _vcdOutputState;
};

#endif
