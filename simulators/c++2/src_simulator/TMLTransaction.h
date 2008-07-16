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

#ifndef TMLTransactionH
#define TMLTransactionH

#include <definitions.h>

class TMLCommand;
class TMLChannel;

class TMLTransaction{
public:
	///Constructor
    	/**
      	\param iCommand Pointer to the command the transaction belongs to
	\param iVirtualStartTime Virtual start time of the transaction
	\param iVirtualLength Virtual length of the transaction
	\param iRunnableTime Time when the transaction became runnable
    	*/
	TMLTransaction(TMLCommand* iCommand,TMLLength iVirtualStartTime, TMLLength iVirtualLength, TMLTime iRunnableTime, TMLChannel* iChannel=0);
	///Returns the time when the transaction became runnable
	/**
      	\return Runnable time
    	*/
	TMLTime getRunnableTime() const;
	///Sets the time when the transaction became runnable
	/**
      	\param iRunnableTime Runnable time
    	*/
	void setRunnableTime(TMLTime iRunnableTime);
	///Returns the start time of the transaction
	/**
      	\return Start time
    	*/
	TMLTime getStartTime() const;
	///Returns the start time of the operational part of the transaction
	/**
      	\return Start time of the operational part
    	*/
	TMLTime getStartTimeOperation() const;
	///Sets the start time of the transaction
	/**
      	\param iStartTime Start time
    	*/
	void setStartTime(TMLTime iStartTime);
	///Returns the virtual start time of the transaction (number of execution units already carried out by previous transactions)
	/**
      	\return Virtual start time
    	*/
	TMLLength getVirtualStartTime() const;
	///Returns the length of the operational part of the transaction
	/**
      	\return Length of the operational part
    	*/
	TMLTime getOperationLength() const;
	///Returns the length of the operation and penalties
	/**
      	\return Overall transaction length
    	*/
	TMLTime getOverallLength() const;
	///Sets the length of the transaction
	/**
      	\param iLength Length of the transaction
    	*/
	void setLength(TMLTime iLength);
	///Returns the length of all penalties
	/**
      	\return Length of penalties
    	*/
	TMLTime getPenalties() const;
	///Returns the virtual length of the transaction (number of execution units already carried out by previous transactions)
	/**
      	\return Virtual length
    	*/
	TMLLength getVirtualLength() const;
	///Sets the virtual length of the transaction (number of execution units already carried out by previous transactions)
	/**
      	\param iLength Virtual length of the transaction
    	*/
	void setVirtualLength(TMLLength iLength);
	///Returns a pointer to the command the transaction belongs to
	/**
      	\return Pointer to command
    	*/
	TMLCommand* getCommand() const;
	///Returns the end time of the transaction
	/**
      	\return End time
    	*/
	TMLTime getEndTime() const;
	///Returns the idle panalty of the transaction
	/**
      	\return Idle panalty
    	*/
	unsigned int getIdlePenalty() const;
	///Sets the idle panalty of the transaction
	/**
      	\param iIdlePenalty Idle penalty
    	*/
	void setIdlePenalty(unsigned int iIdlePenalty);
	///Returns the task switching panalty of the transaction
	/**
      	\return Task switching penalty
    	*/	
	unsigned int getTaskSwitchingPenalty() const;
	///Sets the task switching panalty of the transaction
	/**
      	\param iTaskSwitchingPenalty Task switching penalty
    	*/	
	void setTaskSwitchingPenalty(unsigned int iTaskSwitchingPenalty);
	///Returns the branching panalty of the transaction
	/**
      	\return Branching penalty
    	*/	
	unsigned int getBranchingPenalty() const;
	///Sets the branching panalty of the transaction
	/**
      	\param iBranchingPenalty Branching penalty
    	*/
	void setBranchingPenalty(unsigned int iBranchingPenalty);
	///Returns the terminated flag of the transaction
	bool getTerminatedFlag() const;
	///Sets the terminated flag of the transaction to true
	void setTerminatedFlag();
	///Returns a string representation of the transaction
	/**
	\return Detailed string representation
	*/
	std::string toString();
	///Returns a short string representation of the transaction
	/**
	\return Short string representation
	*/
	std::string toShortString();
	void setChannel(TMLChannel* iChannel);
	TMLChannel* getChannel();
	
	static void * operator new(unsigned int size);//{
		//return memPool.pmalloc(size);
	//}

	static void operator delete(void *p, unsigned int size);//{
		//memPool.pfree(p, size);
	//}
	//union{
	//	TMLChannel* _channel;		//TMLSelectCommand
	//	TMLLength _channelContent	//TMLNotifiedCommand
	//	unsigned int randomValue	//random command
	//}
protected:
	///Time when the transaction became runnable
	TMLTime _runnableTime;
	///Start time of the transaction
	TMLTime _startTime;
	///Virtual start time of the transaction (number of execution units already carried out by previous transactions)
	TMLLength _virtualStartTime;
	///Length of the transaction
	TMLTime _length;
	///Virtual length of the transaction (number of execution units of the transaction)
	TMLLength _virtualLength;
	///Pointer to the command the transaction belongs to
	TMLCommand* _command;
	///Idle penalty
	unsigned int _idlePenalty;
	///Task switching penalty
	unsigned int _taskSwitchingPenalty;
	///Branching penalty
	unsigned int _branchingPenalty;
	///Task terminated flag
	bool _terminated;
	//bool _runnableTimeSet;
	TMLChannel* _channel;
	static Pool<TMLTransaction> memPool;
};

#endif
