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

#ifndef SchedulableDeviceH
#define SchedulableDeviceH

#include <definitions.h>
#include <Serializable.h>
#include <ListenerSubject.h>
#include <WorkloadSource.h>

class Master;
class TMLTransaction;
class TransactionListener;
class TransactionListener;

///Base class for devices which perform a scheduling
class SchedulableDevice: public Serializable, public ListenerSubject <TransactionListener> {
public:
	///Constructor
	/**
	\param iID ID of the device
	\param iName Name of the device
	\param iScheduler Pointer to the scheduler object
	*/
	SchedulableDevice(ID iID, std::string iName, WorkloadSource* iScheduler):_ID(iID), _name(iName), _endSchedule(0), _scheduler(iScheduler), _nextTransaction(0), _deleteScheduler(true) {
		_transactList.reserve(BLOCK_SIZE);
	}
	///Determines the next transaction to be executed
	virtual void schedule()=0;
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	virtual bool addTransaction()=0;
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	//virtual TMLTransaction* getNextTransaction()=0;
	virtual TMLTransaction* getNextTransaction(){
		//std::cout << "Raw version of getNextTransaction\n";
		return _nextTransaction;
	}
	///Writes a HTML representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	virtual void schedule2HTML(std::ofstream& myfile) const =0;
	///Writes a plain text representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	virtual void schedule2TXT(std::ofstream& myfile) const =0;
	virtual std::string toString() const =0;
	virtual std::istream& readObject(std::istream &is){
		READ_STREAM(is,_endSchedule);
		//_simulatedTime=max(_simulatedTime,_endSchedule);   ????????????
#ifdef DEBUG_SERIALIZE
		std::cout << "Read: Schedulable Device " << _name << ": " << _endSchedule << std::endl;
#endif
		return is;
	}
	virtual std::ostream& writeObject(std::ostream &os){
		WRITE_STREAM(os,_endSchedule);
#ifdef DEBUG_SERIALIZE
		std::cout << "Write: Schedulable Device " << _name << ": " << _endSchedule << std::endl;
#endif
		return os;
	}
	virtual void reset(){
		_endSchedule=0;
		_simulatedTime=0;
	}
	///Returns the number of simulated clock cycles
	/**
	\return Number of simulated clock cycles
	*/
	static TMLTime getSimulatedTime() {return _simulatedTime;}
	///Sets the number of simulated clock cycles
	/**
	\param iSimulatedTime Number of simulated clock cycles
	*/
	static void setSimulatedTime(TMLTime iSimulatedTime) {
		//if (iSimulatedTime<_simulatedTime) std::cout << "FAILURE SIMULATION TIME!!!!!!!!!!\n";
		_simulatedTime=iSimulatedTime;
	}
	///Returns the unique ID of the device
	/**
      	\return Unique ID
    	*/ 
	ID getID() const {return _ID;}
	///Destructor
	virtual ~SchedulableDevice(){
		if (_scheduler!=0 && _deleteScheduler) delete _scheduler; 
	}
	///Returns the end time of the last scheduled transaction of the device 
	/**
      	\return End time of the last scheduled transaction
    	*/ 
	TMLTime getEndSchedule(){return _endSchedule;}
	///Sets the scheduler object
	/**
	\param iScheduler Pointer to the scheduler object 
	\param iDelScheduler Determines whether the scheduler is destroyed upon destruction of the device
	*/
	void setScheduler(WorkloadSource* iScheduler, bool iDelScheduler=true){ _scheduler=iScheduler; _deleteScheduler=iDelScheduler;}
	///Returns a pointer to the scheduler object
	/**
	\return Pointer to the scheduler object 
	*/
	WorkloadSource* getScheduler(){ return _scheduler;}
	///Returns the scheduled transaction one after another
	/**
      	\param iInit If init is true, the methods starts from the first transaction 
	\return Pointer to the next transaction
    	*/
	TMLTransaction* getTransactions1By1(bool iInit){
		if (iInit) _posTrasactListGraph=_transactList.begin();
		if (_posTrasactListGraph == _transactList.end()) return 0; 
		TMLTransaction* aTrans = *_posTrasactListGraph;
		_posTrasactListGraph++;
		return aTrans;
	}
protected:
	///Unique ID of the device
	ID _ID;
	///Name of the device
	std::string _name;
	///Class variable holding the simulation time
	static TMLTime _simulatedTime;
	///End time of the last scheduled transaction
	TMLTime _endSchedule;
	///Scheduler
	WorkloadSource* _scheduler;
	///List containing all already scheduled transactions
	TransactionList _transactList;
	///Pointer to the next transaction to be executed
	TMLTransaction* _nextTransaction;
	///State variable for consecutive Transaction output
	TransactionList::iterator _posTrasactListGraph;
	///Flag indicating whether the scheduler has to be deleted when the device is deleted
	bool _deleteScheduler;
};

#endif
