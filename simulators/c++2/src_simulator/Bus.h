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
#include <SchedulableDevice.h>
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
class Bus: public SchedulableDevice, public TraceableDevice {
public:
	///Constructor
    	/**
      	\param iName Name of the bus
      	\param iBurstSize Size of an atomic bus transaction
    	*/
	Bus(std::string iName, TMLLength iBurstSize, TMLTime iTimePerSample=1);
	///Destructor
	virtual ~Bus();
	///Adds a new transaction to the internal queue and invalidates the scheduling decision
	/**
      	\param iCPU Pointer to the calling CPU
      	\param iTrans Pointer to the new transaction
    	*/
	void registerTransaction(CPU* iCPU, TMLTransaction* iTrans);
	///Adds a new master device to the internal device list
	/**
      	\param iMasterDev Pointer to the new device
    	*/
	void registerMasterDevice(CPU* iMasterDev);
	///Determines the next bus transaction to be executed
	void schedule();
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	void addTransaction();
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	TMLTransaction* getNextTransaction() const;
	///Returns the size of an atomic bus transaction
	TMLLength getBurstSize() const;
	///Returns the unique ID of the Bus
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID();
	///Returns a string representation of the Bus
	/**
	\return Detailed string representation
	*/
	std::string toString();
	///Returns a short string representation of the bus
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
	
protected:
	///Calculates the start time and the length of the next transaction
	void calcStartTimeLength();
	///Name of the bus
	std::string _name;
	///Unique ID of the Bus
	unsigned int _myid;
	///Class variable counting the number of Bus instances
	static unsigned int _id;
	///Size of an atomic bus transaction
	TMLLength _burstSize;
	///End time of the last scheduled transaction
	TMLTime _endSchedule;
	///Pointer to the next transaction to be executed
	TMLTransaction* _nextTransaction;
	///Pointer to the CPU on which the next transaction will be executed
	CPU* _nextTransOnCPU;
	///Dirty flag of the current scheduling decision
	bool schedulingNeeded;
	///List containing all master devices
	MasterDeviceList _masterDevices;
	///Transaction queue
	BusTransHashTab _transactionHash;
	///List containing all already scheduled transactions
	TransactionList _transactList;
	///Inverse bus speed
	TMLTime _timePerSample;

	///Actual position within transaction list (used for vcd output)
	TransactionList::iterator _posTrasactList;
	///EndTime of the transaction before _posTransactList (used for vcd output)
	TMLTime _previousTransEndTime;
	///State variable for the VCD output
	vcdBusVisState _vcdOutputState;
};

#endif
