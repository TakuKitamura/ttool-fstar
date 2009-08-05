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
	*/
	SchedulableDevice(unsigned int iID, std::string iName):_ID(iID), _name(iName), _endSchedule(0){}
	///Determines the next transaction to be executed
	virtual void schedule()=0;
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	virtual bool addTransaction()=0;
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	virtual TMLTransaction* getNextTransaction()=0;
	///Writes a HTML representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	///Add a transaction waiting for execution to the internal list
	/**
      	\param iTrans Pointer to the transaction to add
	\param iSourceDevice Source device
    	*/
	virtual void registerTransaction()=0;
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
		std::cout << "Read: Schedulable Device " << _name << ": " << _endSchedule << std::endl;
		return is;
	}
	virtual std::ostream& writeObject(std::ostream &os){
		WRITE_STREAM(os,_endSchedule);
		std::cout << "Write: Schedulable Device " << _name << ": " << _endSchedule << std::endl;
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
	static void setSimulatedTime(TMLTime iSimulatedTime) {_simulatedTime=iSimulatedTime;}
	///Returns the unique ID of the device
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID() const {return _ID;}
	///Destructor
	virtual ~SchedulableDevice(){}
protected:
	///Unique ID of the device
	unsigned int _ID;
	///Name of the device
	std::string _name;
	///Class variable holding the simulation time
	static TMLTime _simulatedTime;
	///End time of the last scheduled transaction
	TMLTime _endSchedule;
};

#endif
