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

#ifndef TMLChannelH
#define TMLChannelH

#include <definitions.h>
#include <SchedulableCommDevice.h>
#include <Slave.h>
#include <Serializable.h>
#include <ListenerSubject.h>
//#include <ChannelListener.h>
#include <GeneralListener.h>
//#include <TransactionListener.h>

class TMLTransaction;
class TMLCommand;
class TMLTask;
class BusMaster;
class HashAlgo;

///This class defines the basic interfaces and functionalites of a TML channel. All specific channels are derived from this base class. A channel is able to convey data and events. 
class TMLChannel: public Serializable, public ListenerSubject <GeneralListener>{
//class TMLChannel: public Serializable, public ListenerSubject <ChannelListener>, public ListenerSubject <TransactionListener> {
public:
	///Constructor
    	/**
      	\param iID ID of channel
	\param iName Name of the channel
	\param iWidth Channel width
	\param iNumberOfHops Number of buses on which the channel is mapped
	\param iMasters Pointers to the masters which the channel is connected to
	\param iSlaves Pointers to the slaves on which the channel is mapped
	\param iPriority Priority of the channel
    	*/
	TMLChannel(ID iID, std::string iName, unsigned int iWidth, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, Priority iPriority);
	///Destructor
	virtual ~TMLChannel();
	///Prepares a write operation
	/**
      	\param iTrans Poiter to the write transaction 
    	*/
	virtual void testWrite(TMLTransaction* iTrans)=0;
	///Prepares a read operation
	/**
      	\param iTrans Pointer to the read transaction
	*/
	virtual void testRead(TMLTransaction* iTrans)=0;
	///Performs the write operation
	virtual void write()=0;
	///Performs the read operation
	virtual bool read()=0;
	///Stores a pointer to the tasks which performs read operation on the channel
	/**
	\param iReadTask Pointer to the task
	*/
	inline void setBlockedReadTask(TMLTask* iReadTask) {_readTask=iReadTask;}
	///Returns a pointer to the tasks which performs read operation on the channel
	/**
	\return Pointer to the task
	*/
	virtual TMLTask* getBlockedReadTask() const=0;
	///Stores a pointer to the tasks which performs write operation on the channel
	/**
	\param iWriteTask Pointer to the task
	*/
	inline void setBlockedWriteTask(TMLTask* iWriteTask) {_writeTask=iWriteTask;}
	///Returns a pointer to the tasks which performs write operation on the channel
	/**
	\return Pointer to the task
	*/
	virtual TMLTask* getBlockedWriteTask()const=0;
	///Returns the next communication master on which the given transaction is conveyed
	/**
	\param iTrans Transaction
	\return Pointer to the communication master
	*/
	BusMaster* getNextMaster(TMLTransaction* iTrans);
	///Returns the first communication master on which the given transaction is conveyed
	/**
	\param iTrans Transaction
	\return Pointer to the communication master
	*/
	BusMaster* getFirstMaster(TMLTransaction* iTrans);
	///Returns the next slave component to which the given transaction is sent
	/**
	\param iTrans Transaction
	\return Pointer to the slave
	*/
	Slave* getNextSlave(TMLTransaction* iTrans) const;
	//Returns the number of buses on which the channel is mapped
	//\return Number of buses
	//unsigned int getNumberOfHops() const;
	///Returns a string representation of the channel
	/**
	\return Detailed string representation
	*/
	virtual std::string toString() const =0;
	///Returns a short string representation of the channel
	/**
	\return Short string representation
	*/
	inline std::string toShortString() const {return _name;}
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void reset();
	///Returns the unique ID of the device
	/**
      	\return Unique ID
    	*/ 
	inline ID getID() const {return _ID;}
	///Inserts samples into the channel
	/**
	\param iNbOfSamples Number of samples to insert
	\param iParam Parameter to insert
      	\return Returns true if successful
    	*/ 
	inline virtual TMLLength insertSamples(TMLLength iNbOfSamples, Parameter* iParam) {return iNbOfSamples;}
	///Writes XML information about the component to a stream
	/**
      	\param s Reference to an output stream
	*/
	inline virtual void streamStateXML(std::ostream& s) const {}
	///Returns the number of samples stored in the channel
   	/**
      	\return Content of the channel
	*/
	inline virtual TMLLength getContent()  const {return 0;}
	///Returns a flag indicating if a channel overflow has been encoutered
	/**
	\return Channel overflow flag
	*/
	inline virtual bool getOverflow() const {return false;}
	///Returns a flag indicating if a channel underflow has been encoutered
	/**
	\return Channel underflow flag
	*/
	inline virtual bool getUnderflow() const {return false;}
	///Returns the hash value for the current task state
	/**
	\param iHash Hash algorithm object
	*/
	virtual void getStateHash(HashAlgo* iHash) const =0;
	///Returns the priority of the channel
	/**
	\return Hash Value
	*/
	inline Priority getPriority() {return _priority;}
	///Returns the width of the channel
	/**
	\return Channel width
	*/
	inline unsigned int getWidth() {return _width;}
	///Returns the width of the channel
	/**
	\param iTask Reference to reading or writing task
	\param iSignificance Flag indicating if operations performed by this task are still reachable
	*/
	void setSignificance(TMLTask* iTask, bool iSignificance);
	inline bool getSignificance() {return (_significance != 0);}
	inline bool mappedOnBus() {return _numberOfHops!=0;}
protected:
	///ID of channel
	ID _ID;
	///Name of the channel
	std::string _name;
	///Channel size
	unsigned int _width;	
	///Pointer to the tasks which performs read operation on the channel
	TMLTask* _readTask;
	///Pointer to the tasks which performs write operation on the channel
	TMLTask* _writeTask;
	///Pointer to the transaction which attempts to write in the channel
	TMLTransaction* _writeTrans;
	///Pointer to the transaction which attempts to read the channel
	TMLTransaction* _readTrans;
	///Number of Buses/Slave devices on which the channel is mapped
	unsigned int _numberOfHops;
	///List of buses on which the channel is mapped
	BusMaster** _masters;
	///List of slaves on which the channel is mapped
	Slave** _slaves;
	///Keeps track of the current Hop of a write Transaction
	unsigned int _writeTransCurrHop;
	///Keeps track of the current Hop of a read Transaction
	unsigned int _readTransCurrHop;
	///channel priority
	Priority _priority;
	///Flag indicating if read or write commands for that channel are still reachable
	unsigned char _significance;
};

#endif
