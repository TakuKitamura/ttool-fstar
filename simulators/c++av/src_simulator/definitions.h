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


#ifndef DefinitionsH
#define DefinitionsH

#include <list>
#include <vector>
#include <string>
#include <string.h>
#include <iostream>
#include <sstream>
#include <fstream>
#include <map>
#include <set>
//#include <deque>
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
#include <dlfcn.h> 

#define xx__VARS(a1, a2, a3, a4, a5, a6, a7, a8, ...) (void*)&(a1), sizeof(a1), (void*)&(a2), sizeof(a2), (void*)&(a3), sizeof(a3), (void*)&(a4), sizeof(a4), (void*)&(a5), sizeof(a5), (void*)&(a6), sizeof(a6), (void*)&(a7), sizeof(a7), (void*)&(a8), sizeof(a8)
#define xx__SIZE(a1, a2, a3, a4, a5, a6, a7, a8, ...) sizeof(a1)+sizeof(a2)+sizeof(a3)+sizeof(a4)+sizeof(a5)+sizeof(a6)+sizeof(a7)+sizeof(a8)
#define PARAM_CPY(nb, a1, ...) nb, xx__VARS(a1, ##__VA_ARGS__, a1, a1, a1, a1, a1, a1, a1, a1)
#define PARAM_INIT(nb, ...) xx__SIZE(__VA_ARGS__, int[0], int[0], int[0], int[0], int[0], int[0], int[0], int[0]), PARAM_CPY(nb, __VA_ARGS__)
#define COMMENTS

class AvBlock;
class TMLTask;
class SystemTransition;
class AvCmd;
class Parameter;
class AvTransition;
class EventQueueCallback;
class EvtQueueNode;

typedef int ParamType;
typedef unsigned int EventID;
typedef unsigned int ID;
typedef unsigned int AVTTime;
typedef unsigned int (AvBlock::*CondFuncPointer) ();
typedef void (AvBlock::*ActionFuncPointer) ();
typedef void (AvBlock::*ParamSetFuncPointer) (Parameter*);
typedef Parameter* (AvBlock::*ParamGetFuncPointer) ();
typedef std::vector<SystemTransition> EnabledTransList;
///keep track of reading/writing transitions in channels
typedef std::set<AvTransition*> AvTransSet;
typedef std::queue<Parameter*> ParamQueue;
typedef std::priority_queue< EvtQueueNode, std::vector<EvtQueueNode>, std::greater<EvtQueueNode> > PrioEventQueue;
typedef std::list<AvBlock*> BlockList;
struct ltstr{
	bool operator()(const char* s1, const char* s2) const{
		return strcmp(s1, s2) < 0;
	}
};

///Datatype which associates a variable name with the coresponding pointer to that variable, used for look-up table of tasks
typedef std::map<const char*, ParamType*, ltstr> VariableLookUpTableName;
///Datatype which associates a variable ID with the coresponding pointer to that variable, used for look-up table of tasks
typedef std::map<ID, ParamType*> VariableLookUpTableID;


class SystemTransition{
public:
	SystemTransition(){}
	SystemTransition(AvBlock* iBlock, AvTransition* iFiringTrans, AvTransition* iSyncTrans, std::string iText): block(iBlock), firingTrans(iFiringTrans), syncTrans(iSyncTrans), text(iText){}
	AvBlock* block;
	AvTransition* firingTrans;
	AvTransition* syncTrans;
	std::string text;
};

class EvtQueueNode {
public:
	EvtQueueNode(EventQueueCallback* iCallBack, AVTTime iTime, EventID iEvtID): callBack(iCallBack), time(iTime), evtID(iEvtID) {}
	/*bool operator<( const EvtQueueNode &rhs ) const {
		return (_time < rhs._time);
	}*/
	bool operator>( const EvtQueueNode &rhs ) const {
		return (time > rhs.time);
	}
//protected:
	EventQueueCallback* callBack;
	AVTTime time;
	EventID evtID;
};

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

class Parameter{
public:
	Parameter(unsigned int entireSize, unsigned int noArgs ...){
		_mem = malloc(entireSize);
		void*  memPos=_mem;
		void* address;
		size_t size;
		va_list args; // argument list
		va_start(args, noArgs); // initialize args
		for (unsigned int i=0;i<noArgs;i++){
			address=va_arg(args, void*);
			size= va_arg(args, size_t);
			memcpy(memPos, address, size);
			memPos = ((char*) memPos) + size;
		}
		va_end(args); // clean up args
	}
	
	~Parameter(){
		free(_mem);
	}
	
	void copyTo(unsigned int noArgs ...) const{
		void*  memPos=_mem;
		void* address;
		size_t size;
		va_list args; // argument list
		va_start(args, noArgs); // initialize args
		for (unsigned int i=0;i<noArgs;i++){
			address=va_arg(args, void*);
			size= va_arg(args, size_t);
			memcpy(address, memPos, size);
			memPos = ((char*) memPos) + size;
		}
		va_end(args); // clean up args
	}
protected:
	void* _mem;
private:
	Parameter(const Parameter& p) {}
};

int myrand(int n1, int n2);

#endif
