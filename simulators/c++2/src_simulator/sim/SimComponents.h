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

#ifndef SimComponentsH
#define SimComponentsH

#include <definitions.h>
#include <Serializable.h>

class TMLEventChannel;
class TMLEventBChannel;
class Bridge;
class Memory;
class Simulator;
class TMLChoiceCommand;
class EBRDD;

///Class encapsulating architecture and application objects
class SimComponents: public Serializable{
public:
	///Constructor
	/**
	\param iHashValue Hash Value for application and architecture
	*/
	SimComponents(int iHashValue);
	///Destructor
	virtual	~SimComponents();
	///Add a task
	/**
	\param iTask Pointer to task
	*/
	void addTask(TMLTask* iTask);
	///Add a channel
	/**
	\param iChan Pointer to channel
	*/
	void addChannel(TMLChannel* iChan);
	///Add an event
	/**
	\param iEvt Pointer to event
	*/
	void addEvent(TMLEventChannel* iEvt);
	///Add a request
	/**
	\param iReq Pointer to request
	*/
	void addRequest(TMLEventBChannel* iReq);
	///Add a CPU
	/**
	\param iCPU Pointer to CPU
	*/
	void addCPU(CPU* iCPU);
	///Add a bus
	/**
	\param iBus Pointer to bus
	*/
	void addBus(SchedulableCommDevice* iBus);
	///Add a bridge
	/**
	\param iBridge Pointer to bridge
	*/
	void addBridge(Bridge* iBridge);
	///Add a memory
	/**
	\param iMem Pointer to memory
	*/
	void addMem(Memory* iMem);
	///Add an EBRDD
	/**
	\param iEBRDD Pointer to EBRDD
	*/
	void addEBRDD(EBRDD* iEBRDD);
	///Calls streamBenchmarks of all traceable devices contained in vcdList
	/**
	param s Reference to output stream object
	*/
	void streamBenchmarks(std::ostream& s) const;
	std::ostream& writeObject(std::ostream& s);
	std::istream& readObject(std::istream& s);
	///Resets all serializable entities to their initial state
	void reset();
	///Searches for a CPU based on its name
	/**
	\param iCPU Name of the CPU
	\return Pointer to that CPU
	*/
	SchedulableDevice* getCPUByName(const std::string& iCPU) const;
	///Searches for a Task based on its name
	/**
	\param iTask Name of the Task
	\return Pointer to that Task
	*/
	TMLTask* getTaskByName(const std::string& iTask) const;
	///Searches for a Bus based on its name
	/**
	\param iBus Name of the Bus
	\return Pointer to that Bus
	*/
	SchedulableCommDevice* getBusByName(const std::string& iBus) const;
	///Searches for a Slave based on its name
	/**
	\param iSlave Name of the Slave
	\return Pointer to that Slave
	*/
	Slave* getSlaveByName(const std::string& iSlave) const;
	///Searches for a Channel based on its name
	/**
	\param iChannel Name of the Channel
	\return Pointer to that Channel
	*/
	TMLChannel* getChannelByName(const std::string& iChannel) const;
	///Searches for a CPU based on its name
	/**
	\param iID ID of the CPU
	\return Pointer to that CPU
	*/
	SchedulableDevice* getCPUByID(unsigned int iID) const;
	///Searches for a Task based on its name
	/**
	\param iID ID of the Task
	\return Pointer to that Task
	*/
	TMLTask* getTaskByID(unsigned int iID) const;
	///Searches for a Bus based on its name
	/**
	\param iID ID of the Bus
	\return Pointer to that Bus
	*/
	SchedulableCommDevice* getBusByID(unsigned int iID) const;
	///Searches for a Slave based on its name
	/**
	\param iID ID of the Slave
	\return Pointer to that Slave
	*/
	Slave* getSlaveByID(unsigned int iID) const;
	///Searches for a Channel based on its name
	/**
	\param iID ID of the Channel
	\return Pointer to that Channel
	*/
	TMLChannel* getChannelByID(unsigned int iID) const;
	///Returns an iterator for the internal CPU list
	/**
	\param iEnd true for iterator pointing to the end of the list, false for iterator pointing to the first element
	\return Const iterator for CPU list
	*/	
	SchedulingList::const_iterator getCPUIterator(bool iEnd) const;
	///Returns an iterator for the internal bus list
	/**
	\param iEnd true for iterator pointing to the end of the list, false for iterator pointing to the first element
	\return Const iterator for bus list
	*/	
	BusList::const_iterator getBusIterator(bool iEnd) const;
	///Returns an iterator for the internal VCD list
	/**
	\param iEnd true for iterator pointing to the end of the list, false for iterator pointing to the first element
	\return Const iterator for VCD list
	*/	
	TraceableDeviceList::const_iterator getVCDIterator(bool iEnd) const;
	///Returns the state of the stop flag
	/**
	\return Stop flag
	*/	
	bool getStopFlag() const {return _stopFlag;}
	///Sets the value of the stop flag
	/**
	\param iStopFlag Stop flag
	\param iStopReason Reason why the simulation stopped
	*/	
	void setStopFlag(bool iStopFlag, const std::string& iStopReason);
	bool getStoppedOnAction(){return _stoppedOnAction;}
	void setStoppedOnAction(){_stoppedOnAction=true; std::cout << "_stoppedOnAction=true\n";}
	///If a task has a choice command as current command, a pointer to it is returned
	/**
	\return Pointer if choice command was found, null otherwise
	*/
	TMLChoiceCommand* getCurrentChoiceCmd();
	///Returns a hash value for the current application and architecture
	/**
	\return Hash value
	*/
	int getHashValue();
	///Returns an iterator for the internal task list
	/**
	\param iEnd true for iterator pointing to the end of the list, false for iterator pointing to the first element
	\return Const iterator for task list
	*/
	TaskList::const_iterator getTaskIterator(bool iEnd) const;
	///Returns an iterator for the internal EBRDD list
	/**
	\param iEnd true for iterator pointing to the end of the list, false for iterator pointing to the first element
	\return Const iterator for EBRDD list
	*/
	EBRDDList::const_iterator getEBRDDIterator(bool iEnd) const;
	///Returns the reason why the simulation stopped
	/**
	\return Reason why the simulation stopped
	*/
	std::string getStopReason(){return _stopReason;}
	///Returns a pointer to the simulator
	/**
	\return Pointer to simulator
	*/
	Simulator* getSimulator(){return _simulator;}
	void setSimulator(Simulator* iSim){_simulator=iSim;}
	std::string getCmpNameByID(unsigned int iID);
protected:
	///Pointer to simulator
	Simulator* _simulator;
	///List holding schedulable devices
	SchedulingList _cpuList;
	///List holding schedulable communication devices
	BusList _busList;
	///List holding traceable devices
	TraceableDeviceList _vcdList;
	///List holding serializable devices
	SerializableList _serList;
	///List holding bridges and memories
	SlaveList _slList;
	///List holding tasks
	TaskList _taskList;
	///List holding channels
	ChannelList _channelList;
	///List holding EBRDDs
	EBRDDList _ebrddList;
	///Flag indicating whether the simulation must be stopped
	bool _stopFlag;
	///Hash Value for application and architecture
	int _hashValue;
	///Reason why the simulation stopped
	std::string _stopReason;
	///Flag indicating whether a task has an action command pending
	bool _stoppedOnAction;
};
#endif

