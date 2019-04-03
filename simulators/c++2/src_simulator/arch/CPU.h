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
#include <SchedulableCommDevice.h>
#include <TraceableDevice.h>
#include <BusMaster.h>
#include <WorkloadSource.h>
#include <TMLTask.h>
#include <TMLCommand.h>

class TMLTask;
class TMLTransaction;
class Bus;

enum vcdCPUVisState
    {
	END_IDLE_CPU,
	END_PENALTY_CPU,
	END_TASK_CPU
};

///Represents the base class for CPUs
class CPU: public SchedulableDevice, public TraceableDevice {
public:
	///Constructor
	/**
	\param iID ID of the device
	\param iName Name of the device
	\param iScheduler Pointer to the scheduler object
	*/
	CPU(ID iID, std::string iName, WorkloadSource* iScheduler): SchedulableDevice(iID, iName, iScheduler), _lastTransaction(0)/*,_schedulingNeeded(false)*/{
	}
	///Destructor
	virtual ~CPU(){
	}
	///Stores a new task in the internal task list
	/**
      	\param iTask Pointer to the task to add
    	*/
	virtual void registerTask(TMLTask* iTask){
		_taskList.push_back(iTask);
		if (_scheduler!=0) _scheduler->addWorkloadSource(iTask);
	}
	///Truncates the next transaction at time iTime
	/**
	\param iTime Indicates at what time the transaction should be truncated
	\return Returns true if scheduling of device has been performed
	*/
	virtual void truncateAndAddNextTransAt(TMLTime iTime)=0;
	//virtual TMLTime truncateNextTransAt(TMLTime iTime)=0;
	///Adds a new bus master to the internal list
	/**
	\param iMaster Pointer to bus master 
	*/
	virtual void addBusMaster(BusMaster* iMaster){
		_busMasterList.push_back(iMaster);
	}
	virtual void reset(){
		SchedulableDevice::reset();
		_lastTransaction=0;
		//_schedulingNeeded=false;
	}
	virtual std::string toString() const =0;
	virtual std::istream& readObject(std::istream &is){
		SchedulableDevice::readObject(is);
		return is;
	}
	virtual std::ostream& writeObject(std::ostream &os){
		SchedulableDevice::writeObject(os);
		return os;
	}
	///Invalidate schedule of CPU
	/*void setRescheduleFlag(){
		_schedulingNeeded=true;
		//std::cout <<" CPU " << _name << " forwards to scheduler\n";
		_scheduler->resetScheduledFlag();
	}*/

	///Truncates current transaction if schedule is invalid
	/**
	\param iTime Truncation time
	*/
	/*void truncateIfNecessary(TMLTime iTime){
		if(_schedulingNeeded && getNextTransaction()!=0){
			//std::cout << "truncateIfNecessary for CPU " << _name << "\n";
			_schedulingNeeded=false;	
			truncateAndAddNextTransAt(iTime);
			//std::cout << "truncateIfNecessary end\n";
		}
	}*/

	///Reschedules CPU if schedule is invalid
	/*void rescheduleIfNecessary(){
		if(_schedulingNeeded){
			//std::cout << "rescheduleIfNecessary for CPU " << _name << "\n";
			_schedulingNeeded=false;
			schedule();
			//std::cout << "rescheduleIfNecessary end\n";
		}
	}*/
	
	/*void truncateAndRescheduleIfNecessary(TMLTime iTime){  commented out
		std::cout << "truncateAndRescheduleIfNecessary for CPU " << _name << " started\n";
		if(_schedulingNeeded){
			_schedulingNeeded=false;
			//if(_nextTransaction==0)
			//std::cout << "shouldn't be raw >\n";
			if(getNextTransaction()==0){
				//std::cout << "shouldn't be raw <\n";		
				schedule();
			}else
				
			std::cout << "truncateAndRescheduleIfNecessary " << _name <<  " scheduled\n";
		}else
			std::cout << "truncateAndRescheduleIfNecessary " << _name <<  " no scheduling needed\n";
		std::cout << "Current Trans " << _name << ": ";
		if (_nextTransaction==0) std::cout << "0\n"; else std::cout << _nextTransaction->toString() << "\n";  
	}*/
protected:
	///List of all tasks running on the CPU
	TaskList _taskList;
	///Pointer to the last transaction which has been executed
	TMLTransaction* _lastTransaction;
	///List of bus masters
	BusMasterList _busMasterList;
	///Dirty flag of the current scheduling decision
	//bool _schedulingNeeded;
};

#endif
