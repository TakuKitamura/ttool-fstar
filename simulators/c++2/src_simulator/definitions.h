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

#ifndef definitionsH
#define definitionsH

#include <list>
#include <vector>
#include <string>
#include <iostream>
#include <sstream>
#include <fstream>
#include <map>
#include <deque>
#include <algorithm>
#include <stdarg.h>
#include <functional>
#include <queue>
#include <vector>
#include <sys/time.h>

#include <SchedulableDevice.h>

using std::min;
using std::max;

#define BUS_ENABLED
#define EVENTS_MAPPED_ON_BUS
#define WAIT_SEND_VLEN 1
#define CPURRPB CPUPB
#define CLOCK_INC 20
#define BLOCK_SIZE 512
#define ADD_COMMENTS

//Task VCD output
#define TERMINATED 3
#define RUNNING 2
#define RUNNABLE 1
#define SUSPENDED 0

class TMLTask;
class TMLTransaction;
class TMLCommand;
class CPU;
class Bus;
template <typename T> class Parameter;
class TraceableDevice;

///Datatype used for time measurements
typedef unsigned int TMLTime;
///Datatype used to indicate the virtual length of commands (execution units, data units)
typedef unsigned int TMLLength;
///Datatype used by the CPU to store pointers to associated tasks
typedef std::list<TMLTask*> TaskList;
///Datatype used by CPU and bus to store already scheduled transactions
typedef std::vector<TMLTransaction*> TransactionList;
///Datatype holding pointer to CPUs, used by TMLMain and simulation kernel
typedef std::list<CPU*> CPUList;
///Datatype holding references to buses, used by TMLMain and simulation kernel
typedef std::list<Bus*> BusList;
///Datatype used by the Bus to store pointers to all connected master devices
typedef std::list<CPU*> MasterDeviceList;
///Datatype establishing an association between a CPU and a transaction, used by the bus
typedef std::map<CPU*, TMLTransaction*> BusTransHashTab;
///Datatype for event parameters
typedef int ParamType;
///Datatype used in EventChannels to store parameters of events
typedef std::deque<Parameter<ParamType>*> ParamQueue;
///Type of member function pointer used to indicate a function encapsulating a condition (for TMLChoiceCommand)
typedef unsigned int (TMLTask::*CondFuncPointer) ();
///Type of member function pointer used to indicate a function encapsulating an action (for TMLActionCommand)
//typedef void (TMLTask::*ActionFuncPointer) ();
typedef unsigned int (TMLTask::*ActionFuncPointer) ();
///Type of member function pointer used to indicate a function encapsulating a condition (for TMLChoiceCommand)
typedef unsigned int (TMLTask::*LengthFuncPointer) ();
///Datatype holding references to TraceableDevices (for VCD output)
typedef std::list<TraceableDevice*> TraceableDeviceList;


template <typename T>
class Pool {
public:
	Pool():_headFreeList(0){}

void* pmalloc(unsigned int n){
	if (n != sizeof(T)){
		std::cout << "FAEAEAELA!";
		return ::operator new(n);
	}
	T* aHead = _headFreeList;
        if (aHead){
		_headFreeList = *(reinterpret_cast<T**>(aHead));
		//_headFreeList = (T*)((void*)(*aHead));
        }else{
		T** aAdr;
		T* newBlock = static_cast<T*>(::operator new(BLOCK_SIZE * sizeof(T)));
		for (int i = 1; i < BLOCK_SIZE-1; ++i){
			aAdr = reinterpret_cast<T**>(&newBlock[i]);
			*aAdr = &newBlock[i+1];
			//newBlock[i] = &newBlock[i+1];
		}
		aAdr = reinterpret_cast<T**>(&newBlock[BLOCK_SIZE-1]);
		*aAdr = 0;
		//newBlock[BLOCK_SIZE-1].next = 0;
		aHead = newBlock;
		_headFreeList = &newBlock[1];
		//_chunkList.push_back(p);
        }
	return aHead;
}

void pfree(void *p, unsigned int n){
	if (p == 0) return;
	if (n != sizeof(T)){
		::operator delete(p);
		return;
	}
	T* aDelObj = static_cast<T*>(p);
	//delObj->next = _headFreeList;
	T** aAdr = reinterpret_cast<T**>(aDelObj);
	*aAdr = _headFreeList;
	_headFreeList = aDelObj;
}

~Pool(){
	//std::list<T*>::iterator i;
	//for(i=_chunkList.begin(); i != _chunkList.end(); ++i) ::operator delete [] *i;
}
private:
	T* _headFreeList;
	//std::list<T*> _chunkList;
};


class Comment{
public:	
	///Constructor
	/**
	\param iTime Time when the message occurred
	*/
	Comment(TMLTime iTime, TMLCommand* iCommand, unsigned int iActionCode):_time(iTime), _command(iCommand), _actionCode(iActionCode){}
	
	void * operator new(unsigned int size){
		return memPool.pmalloc(size);
	}
	void operator delete(void *p, unsigned int size){
		memPool.pfree(p, size);
	}

	///Time when the massage occurred
	TMLTime _time;
	TMLCommand* _command;
	unsigned int _actionCode;
private:
	static Pool<Comment> memPool;
};

///Datatype used in Tasks to store comments concerning the task execution
typedef std::vector<Comment*> CommentList;

///Minimum of three values
/**
\param a Value 1
\param b Value 2
\param c Value 3
\return Minimum
*/
template<typename T>
inline const T& min(const T& a, const T& b, const T& c){
	const T& tmin=min(a,b);
	return (tmin<c)?tmin:c;
}

///Minimum of four values
/**
\param a Value 1
\param b Value 2
\param c Value 3
\param d Value 4
\return Minimum
*/
template<typename T>
inline const T& min(const T& a, const T& b, const T& c, const T& d){
	const T& tmin=min(a,b,c);
	return (tmin<d)?tmin:d;
}

///The function writes its arguments to an array which has been allocated dynamically.
/**
\param noArgs Number of arguments
\param arg1 An arbitrary number of arguments can follow here
\return Pointer to the array
*/
template<typename T>
T* array(unsigned int noArgs, T arg1 ...){
	T arg=arg1;
	T* newArray;
	unsigned int i;
	va_list args; // argument list
	va_start(args, arg1); // initialize args
	newArray=new T[noArgs];
	for (i=0;i<noArgs;i++){
		newArray[i]=arg;
		arg=va_arg(args, T);
	}
	va_end(args); // clean up args
	return newArray;
}

///This class encapsulates a pointer to a value or the value itself
template <typename T>
class RefValUnion{
public:
	///Constructor called for constants
	/**
	\param in Constant reference to value
	*/
	RefValUnion(const T& in):isValue(true){
		//std::cout << "const constructor executed" << std::endl;
		value=in;
	}
	///Constructor called for variables
	/**
	\param in Reference to variable
	*/
	RefValUnion(T& in):isValue(false){
		//std::cout << "varible constructor executed" << std::endl;
		pointer=&in;
	}
	///The parenthesis operator returns a reference to the stored value
	/**
	\return Reference to value 
	*/
	T& operator() (){if (isValue) return value; else return *pointer;}
	///The parenthesis operator returns a reference to the stored value
	/**
	\return Constant reference to value 
	*/
	const T& operator() () const {if (isValue) return value; else return *pointer;}
	
	T print() const {return value;}
private:
	///Indicates whether the class holds a value or a pointer to a value
	bool isValue;
	union{
		///Pointer
		T* pointer;
		///Value
		T value;
	};
};

///This class encapsulates three parameters
template <typename T>
class Parameter{
public:
	///Constructor
	/**
	\param ip1 Value 1
	\param ip2 Value 2
	\param ip3 Value 3
	*/
	Parameter(const RefValUnion<T>& ip1,const RefValUnion<T>& ip2,const RefValUnion<T>& ip3):_p1(ip1),_p2(ip2),_p3(ip3){}
	///Assignement operator, copies all parameters
	const Parameter<T>& operator=(const Parameter<T>& rhs){
		_p1()=rhs._p1();
		_p2()=rhs._p2();
		_p3()=rhs._p3();
		return *this;
	}
	///Print function for testing purposes
	void print(){
		std::cout << "p1:" << _p1.print() << " p2:" << _p2.print() << " p3:" << _p3.print() << std::endl;
	}
private:
	///Three parameters
	RefValUnion<T> _p1,_p2,_p3;
};

///Datatype which encapsulates singnal changes, used for VCD output
class SignalChangeData{
public:
	///Constructor
	/**
	\param iSigChange String representation of the signal change in VCD format
	\param iTime Time when the change occurred 
	\param iDevice Pointer to the device the signal belongs to
	*/
	SignalChangeData( std::string& iSigChange, unsigned int iTime, TraceableDevice* iDevice):_sigChange(iSigChange),_time(iTime),_device(iDevice){
	}
	///String representation of the signal change in VCD format
	std::string _sigChange;
	///Time when the change occurred 
	TMLTime _time;
	///Pointer to the device the signal belongs to
	TraceableDevice* _device;
};

///Function object for the comparison of the runnable time of two transaction
struct greaterRunnableTime{
	bool operator()(TMLTransaction const* p1, TMLTransaction const* p2);
};

///Function object for the comparison of the priority of two transaction
struct greaterPrio{
	bool operator()(TMLTransaction const* p1, TMLTransaction const* p2);
};

///Function object for the comparison of the start time of two transaction
struct greaterStartTime{
	bool operator()(TMLTransaction const* p1, TMLTransaction const* p2);
};

namespace std{
	///Specialization of std::greater for SignalChangeData pointers
	template<> struct greater<SignalChangeData*>{
	bool operator()(SignalChangeData const* p1, SignalChangeData const* p2){
		return p1->_time > p2->_time;
	}
	};
};

///Priority queue for SignalChangeData objects, keeps track of the temporal ordering of signal changes (for VCD output)
typedef std::priority_queue<SignalChangeData*, std::vector<SignalChangeData*>, std::greater<SignalChangeData*> > SignalChangeQueue;
///Priority queue for not yet processed transactions, runnableTime being less than the end of the last scheduled transaction of the device
typedef std::priority_queue<TMLTransaction*, std::vector<TMLTransaction*>, greaterPrio > PastTransactionQueue;
///Priority queue for not yet processed transactions, runnableTime being greater than the end of the last scheduled transaction of the device
typedef std::priority_queue<TMLTransaction*, std::vector<TMLTransaction*>, greaterRunnableTime > FutureTransactionQueue;
///Priority queue holding Transactions for the graph output
typedef std::priority_queue<TMLTransaction*, std::vector<TMLTransaction*>, greaterStartTime > GraphTransactionQueue;

///Calculates random numbers between n1 and n2 (both inclusive)
/**
	\param n1 Lower bound
	\param n2 Upper bound
	\return Random number
*/
int myrand(int n1, int n2);

///Calculates the difference between to timeval structures
/**
	\param begin Time 1
	\param end Time 2
	\return Time difference in usec (Time2 - Time1)
*/
long getTimeDiff(struct timeval& begin, struct timeval& end);

#endif
