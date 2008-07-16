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

class TMLTransaction;
class TMLCommand;
class TMLTask;
class Bus;

///This class defines the basic interfaces and functionalites of a TML channel. All specific channels are derived from this base class. A channel is able to convey data and events. 
class TMLChannel{
public:
	///Constructor
    	/**
      	\param iName Name of the channel
	\param iBus Pointer to the bus on which the channel is mapped
    	*/
	TMLChannel(std::string iName, Bus* iBus);
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
	//Cancels a pending read opeartion 
	//virtual void cancelReadTransaction()=0;
	///Stores a pointer to the tasks which performs read operation on the channel
	/**
	\param iReadTask Pointer to the task
	*/
	void setBlockedReadTask(TMLTask* iReadTask);
	///Returns a pointer to the tasks which performs read operation on the channel
	/**
	\return Pointer to the task
	*/
	virtual TMLTask* getBlockedReadTask() const=0;
	///Stores a pointer to the tasks which performs write operation on the channel
	/**
	\param iWriteTask Pointer to the task
	*/
	void setBlockedWriteTask(TMLTask* iWriteTask);
	///Returns a pointer to the tasks which performs write operation on the channel
	/**
	\return Pointer to the task
	*/
	virtual TMLTask* getBlockedWriteTask()const=0;
	///Returns a pointer to the Bus on which the channel is mapped
	/**
	\return Pointer to the bus
	*/
	Bus* getBus() const;
	///Returns a string representation of the channel
	/**
	\return Detailed string representation
	*/
	virtual std::string toString()=0;
	///Returns a short string representation of the channel
	/**
	\return Short string representation
	*/
	std::string toShortString();
protected:
	///Name of the channel
	std::string _name;	
	///Pointer to the tasks which performs read operation on the channel
	TMLTask* _readTask;
	///Pointer to the tasks which performs write operation on the channel
	TMLTask* _writeTask;
	///Pointer to the bus on which the channel is mapped
	Bus* _bus;
	///Burst size of the associated bus (for performance reasons)
	TMLLength _burstSize;
};

#endif
