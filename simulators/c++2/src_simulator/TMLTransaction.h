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
#include <MemPoolNoDel.h>
#include <TMLCommand.h>

//class TMLCommand;
class TMLChannel;

class TMLTransaction {
public:
	///Constructor
    	/**
      	\param iCommand Pointer to the command the transaction belongs to
	\param iVirtualLength Virtual length of the transaction
	\param iRunnableTime Time when the transaction became runnable
	\param iChannel Channel on which data was conveyed
    	*/
	TMLTransaction(TMLCommand* iCommand, TMLLength iVirtualLength, TMLTime iRunnableTime, TMLChannel* iChannel=0);
	///Constructor
	TMLTransaction();
	///Returns the time when the transaction became runnable
	/**
      	\return Runnable time
    	*/
	inline TMLTime getRunnableTime() const {return _runnableTime;}
	///Sets the time when the transaction became runnable
	/**
      	\param iRunnableTime Runnable time
    	*/
	inline void setRunnableTime(TMLTime iRunnableTime) {_runnableTime = max(_runnableTime,iRunnableTime);}
	///Returns the start time of the transaction
	/**
      	\return Start time
    	*/
	inline TMLTime getStartTime() const {return _startTime;}
	///Returns the start time of the operational part of the transaction
	/**
      	\return Start time of the operational part
    	*/
	inline TMLTime getStartTimeOperation() const {
#ifdef PENALTIES_ENABLED
	  //std::cout << "astartime: " << _startTime << " idlePenatly:" << _idlePenalty  << " switching penalty:" << _taskSwitchingPenalty<< std::endl;
		return _startTime + _idlePenalty + _taskSwitchingPenalty;
#else
		return _startTime;
#endif
	}
	///Sets the start time of the transaction
	/**
      	\param iStartTime Start time
    	*/
	inline void setStartTime(TMLTime iStartTime) {_startTime=iStartTime;
	  //std::cout << "setting startime: " << _startTime << std::endl;

	}
	///Returns the length of the operational part of the transaction
	/**
      	\return Length of the operational part
    	*/
	inline TMLTime getOperationLength() const {return _length;}
	///Returns the length of the operation and penalties
	/**
      	\return Overall transaction length
    	*/
	inline TMLTime getOverallLength() const{
#ifdef PENALTIES_ENABLED
	return _length + _idlePenalty + _taskSwitchingPenalty;
#else
	return _length;
#endif
	}
	///Sets the length of the transaction
	/**
      	\param iLength Length of the transaction
    	*/
	inline void setLength(TMLTime iLength) {_length=iLength;}
	///Returns the length of all penalties
	/**
      	\return Length of penalties
    	*/
	inline TMLTime getPenalties() const{
#ifdef PENALTIES_ENABLED
		return _idlePenalty + _taskSwitchingPenalty;
#else
		return 0;
#endif
	}
	///Returns the virtual length of the transaction (number of execution units already carried out by previous transactions)
	/**
      	\return Virtual length
    	*/
	inline TMLLength getVirtualLength() const {return _virtualLength;}
	///Sets the virtual length of the transaction (number of execution units already carried out by previous transactions)
	/**
      	\param iLength Virtual length of the transaction
    	*/
	inline void setVirtualLength(TMLLength iLength) {_virtualLength=iLength;}
	///Returns a pointer to the command the transaction belongs to
	/**
      	\return Pointer to command
    	*/
	inline TMLCommand* getCommand() const {return _command;}
	///Returns the end time of the transaction
	/**
      	\return End time
    	*/
	inline TMLTime getEndTime() const{
#ifdef PENALTIES_ENABLED
		return _startTime  + _length + _idlePenalty + _taskSwitchingPenalty;
#else
		return _startTime  + _length;
#endif
	}
	///Returns the idle panalty of the transaction
	/**
      	\return Idle panalty
    	*/
	inline TMLTime getIdlePenalty() const{
#ifdef PENALTIES_ENABLED
		return _idlePenalty;
#else
		return 0;
#endif
	}
	///Sets the idle panalty of the transaction
	/**
      	\param iIdlePenalty Idle penalty
    	*/
	inline void setIdlePenalty(TMLTime iIdlePenalty){
#ifdef PENALTIES_ENABLED
		_idlePenalty=iIdlePenalty;
#endif
	}
	///Returns the task switching penalty of the transaction
	/**
      	\return Task switching penalty
    	*/	
	inline TMLTime getTaskSwitchingPenalty() const{
#ifdef PENALTIES_ENABLED
		return _taskSwitchingPenalty;
#else
		return 0;
#endif
	}
	///Sets the task switching penalty of the transaction
	/**
      	\param iTaskSwitchingPenalty Task switching penalty
    	*/	
	inline void setTaskSwitchingPenalty(TMLTime iTaskSwitchingPenalty){
#ifdef PENALTIES_ENABLED
	_taskSwitchingPenalty=iTaskSwitchingPenalty;
#endif	
	}
	/////Returns the branching penalty of the transaction
	////**
      	//\return Branching penalty
    	//*/	
	//TMLTime getBranchingPenalty() const;
	////Sets the branching panalty of the transaction
	////**
      	////\param iBranchingPenalty Branching penalty
    	//*/
	//void setBranchingPenalty(TMLTime iBranchingPenalty);
	///Returns a string representation of the transaction
	/**
	\return Detailed string representation
	*/
	std::string printEnd() const;
	std::string toString() const;
	///Returns a short string representation of the transaction
	/**
	\return Short string representation
	*/
	std::string toShortString() const;
	///Set channel on which data was conveyed
	/**
	\param iChannel Pointer to a channel
	*/
	inline void setChannel(TMLChannel* iChannel) {_channel=iChannel;}
	///Get channel on which data was conveyed

	
	/**
	\return Pointer to channel
	*/
	inline TMLChannel* getChannel() const {return _channel;}
	inline static void * operator new(size_t size) {return memPool.pmalloc(size);}
	inline static void operator delete(void *p, size_t size) {memPool.pfree(p, size);}
	inline static void reset() {memPool.reset();}
	inline static void incID() {_ID++;}
	inline static ID getID() {return _ID;}
	inline static void resetID() {_ID=1;}
	inline void setStateID(ID iID) {_stateID=iID;}
	inline ID getStateID() {return _stateID;}
	inline void setTaskID(ID iID) {_taskID=iID;}
	void toXML(std::ostringstream& glob, int deviceID, std::string deviceName) const;
	

protected:
	///Time when the transaction became runnable
	TMLTime _runnableTime;
	///Start time of the transaction
	TMLTime _startTime;
	///Length of the transaction
	TMLTime _length;
	///Virtual length of the transaction (number of execution units of the transaction)
	TMLLength _virtualLength;
	///Pointer to the command the transaction belongs to
	TMLCommand* _command;
#ifdef PENALTIES_ENABLED
	///Idle penalty
	TMLTime _idlePenalty;
	///Task switching penalty
	TMLTime _taskSwitchingPenalty;
	/////Branching penalty
	//TMLTime _branchingPenalty;
#endif
	///Channel on which data was conveyed
	TMLChannel* _channel;
	ID _stateID;
	ID _taskID;
	///Memory pool for transactions
	static MemPoolNoDel<TMLTransaction> memPool;
	///Current Transaction ID
	static ID _ID;
};

#endif
