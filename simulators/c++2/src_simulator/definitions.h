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
#include <set>
#include <deque>
#include <algorithm>
#include <stdarg.h>
#include <functional>
#include <queue>
#include <vector>
#include <sys/time.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>

#define WRITE_STREAM(s,v) s.write((char*) &v,sizeof(v)); std::cout << sizeof(v) << " bytes written" << std::endl;
#define READ_STREAM(s,v) s.read((char*) &v,sizeof(v)); std::cout << sizeof(v) << " bytes read" << std::endl;

using std::min;
using std::max;

#undef DEBUG_KERNEL
#undef DEBUG_CPU
#undef DEBUG_BUS

#define BUS_ENABLED
#define WAIT_SEND_VLEN 1
#undef PENALTIES_ENABLED
#define CPURRPB CPUPB
#define CLOCK_INC 20
#define BLOCK_SIZE 500000
#define PARAMETER_BLOCK_SIZE 1000
#define ADD_COMMENTS
#define NO_EVENTS_TO_LOAD 10
#define PORT "3490"
#define BACKLOG 10
#define VCD_PREFIX "b"
//#define SERVER_MODE

//Task VCD output
#define TERMINATED 3
#define RUNNING 2
#define RUNNABLE 1
#define SUSPENDED 0

//XML Tags
#define TAG_HEADER "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>"
#define TAG_STARTo "<siminfo>"
#define TAG_STARTc "</siminfo>"
#define TAG_ERRNOo "<error>"
#define TAG_ERRNOc "</error>"
#define TAG_MSGo "<msg>"
#define TAG_MSGc "</msg>"
#define TAG_TIMEo "<simtime>"
#define TAG_TIMEc "</simtime>"
#define TAG_CYCLESo "<simcycles>"
#define TAG_CYCLESc "</simcycles>"
#define TAG_TASKo "<task"
#define TAG_TASKc "</task>"
#define TAG_VARo "<var"
#define TAG_VARc "</var>"
#define TAG_STATUSo "<status>"
#define TAG_STATUSc "</status>"
#define TAG_GLOBALo "<global>"
#define TAG_GLOBALc "</global>"
#define TAG_CURRCMDo "<currcmd"
#define TAG_CURRCMDc "</currcmd>"
#define TAG_BREAKCMDo "<breakcmd"
#define TAG_BREAKCMDc "</breakcmd>"
#define TAG_HASHo "<hashval>"
#define TAG_HASHc "</hashval>"
#define TAG_BRANCHo "<branch>"
#define TAG_BRANCHc "</branch>"

#define TAG_EXTIMEo "<extime>"
#define TAG_EXTIMEc "</extime>"
#define TAG_CONTDELo "<contdel"
#define TAG_CONTDELc "</contdel>"
#define TAG_BUSo "<bus"
#define TAG_BUSc "</bus>"
#define TAG_UTILo "<util>"
#define TAG_UTILc "</util>"
#define TAG_CPUo "<cpu"
#define TAG_CPUc "</cpu>"
#define TAG_PROGRESSo "<progr>"
#define TAG_PROGRESSc "</progr>"


class TMLTask;
class TMLTransaction;
class TMLCommand;
class CPU;
class SchedulableCommDevice;
class SchedulableDevice;
template <typename T> class Parameter;
class TraceableDevice;
class Master;
class BusMasterInfo;
class Serializable;
class TMLChannel;
class Slave;
class Comment;

///Datatype used for time measurements
typedef unsigned int TMLTime;
///Datatype used to indicate the virtual length of commands (execution units, data units)
typedef unsigned int TMLLength;
///Datatype used by the CPU to store pointers to associated tasks
typedef std::list<TMLTask*> TaskList;
///Datatype used by CPU and bus to store already scheduled transactions
typedef std::vector<TMLTransaction*> TransactionList;
///Datatype holding pointer to CPUs, used by SimComponents and simulation kernel
typedef std::list<CPU*> CPUList;
///Datatype holding pointer to CPUs and Bridges, used by simulation kernel for scheduling
typedef std::list<SchedulableDevice*> SchedulingList;
///Datatype holding references to buses, used by SimComponents and simulation kernel
typedef std::list<SchedulableCommDevice*> BusList;
///Datatype holding references to CPUs, buses, Tasks and channels, used by serialization functions
typedef std::list<Serializable*> SerializableList;
///Datatype used in SimComponents to store slave objects
typedef std::list<Slave*> SlaveList;
///Datatype used in SimComponents to store channel objects
typedef std::list<TMLChannel*> ChannelList;
///Datatype used in Tasks to store comments concerning the task execution
typedef std::vector<Comment*> CommentList;
///Datatype used in Tasks in order to associate a command with an ID 
typedef std::map<unsigned int, TMLCommand*> CommandHashTab;
///Datatype establishing an association between a CPU and a transaction, used by the bus
typedef std::map<Master*, TMLTransaction*> BusTransHashTab;
///Datatype establishing an association between a bus and a priority, used by Masters
typedef std::map<SchedulableCommDevice*, BusMasterInfo*> MasterPriorityHashTab;
///Datatype for event parameters
typedef int ParamType;
///Datatype used in EventChannels to store parameters of events
typedef std::deque<Parameter<ParamType> > ParamQueue;
///Type of member function pointer used to indicate a function encapsulating a condition (for TMLChoiceCommand)
typedef unsigned int (TMLTask::*CondFuncPointer) ();
///Type of member function pointer used to indicate a function encapsulating an action (for TMLActionCommand)
typedef unsigned int (TMLTask::*ActionFuncPointer) ();
///Type of member function pointer used to indicate a function encapsulating a condition (for TMLChoiceCommand)
typedef unsigned int (TMLTask::*LengthFuncPointer) ();
///Type of member function pointer used to indicate a function encapsulating parameter manipulation (for TMLWaitCommand, TMLSendCommand)
typedef unsigned int (TMLTask::*ParamFuncPointer) (Parameter<ParamType>& ioParam);
///Datatype holding references to TraceableDevices (for VCD output)
typedef std::list<TraceableDevice*> TraceableDeviceList;
///Datatype used by the Simulator to keep track of all breakpoints
typedef std::set<TMLCommand*> BreakpointSet;
///Command queue used by server
typedef std::deque<std::string*> CommandQueue;

struct ltstr{
	bool operator()(const char* s1, const char* s2) const{
		return strcmp(s1, s2) < 0;
	}
};
///Datatype which associates a variable name with the coresponding pointer to that variable, used for look-up table of tasks
typedef std::map<const char*, ParamType*, ltstr> VariableLookUpTableName;
///Datatype which associates a variable ID with the coresponding pointer to that variable, used for look-up table of tasks
typedef std::map<unsigned int, ParamType*> VariableLookUpTableID;


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

template<typename T>
inline T StringToNum(const std::string& iStr){
	T aResult(0);
	std::istringstream myStream(iStr);
	myStream>>aResult;
	return aResult;
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

//template<class T> Pool<Parameter<T> > Parameter<T>::memPool;

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
//typedef std::map<SchedulableCommDevice*, FutureTransactionQueue*> BridgeTransactionListHash;

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

///Replaces all occurrences of iSearch in ioHTML by iReplace
/**
	\param ioHTML String in which the replacements shall be made
	\param iSearch String to search for
	\param iReplace String which is filled in
*/
void replaceAll(std::string& ioHTML, std::string iSearch, std::string iReplace);
std::string vcdValConvert(unsigned int iVal);
#endif
