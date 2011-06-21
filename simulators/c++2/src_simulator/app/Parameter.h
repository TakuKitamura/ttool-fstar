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

#ifndef ParameterH
#define ParameterH

#include <definitions.h>
#include <MemPool.h>
#include <HashAlgo.h>

class Parameter {
public:
	///Constructor
	virtual void print() const=0;
	virtual std::ostream& writeObject(std::ostream& s) = 0;
	virtual void streamStateXML(std::ostream& s) const = 0;
	virtual void getP(void* op1 ...) const = 0;
	virtual void getStateHash(HashAlgo* iHash) const = 0;
	virtual void readFromStream(std::istream &is)=0;
	
	///Stream operator >>
	friend std::istream& operator >>(std::istream &is,Parameter* obj){
		obj->readFromStream(is);
		//for (unsigned int i=0;i<obj.size;i++){
		//	is >> obj._p[i];
		//}
 		return is;
	}
};
		
template <typename T, int size>
class SizedParameter: public Parameter {
public:
	SizedParameter(){
	}
	
	SizedParameter(const T& ip1 ...){
//#if size>0
		if (size!=0){
			T arg=ip1;
			va_list args; // argument list
			va_start(args, ip1); // initialize args
			for (unsigned int i=0;i<size;i++){
				_p[i]=arg;
				arg=va_arg(args, T);
			}
		}
//#endif
	}
			
	SizedParameter(std::istream& s){
//#if size>0
		for (unsigned int i=0;i<size;i++){
			READ_STREAM(s, _p[i]);
		}
#ifdef DEBUG_SERIALIZE
		print();
#endif
//#endif
	}

		
	virtual ~SizedParameter(){
	}
		
	///Print function for testing purposes
	void print() const{
		//std::cerr << "print " << size << " elements in mempool " << &memPool << " :\n";
//#if size>0	
		for (unsigned int i=0;i<size;i++){
				std::cout << " p[" << (i+1) << "]:" << _p[i];
		}
//#endif
		//std::cerr << std::endl;
		//std::cerr << "end print:\n";
	}
		
	std::ostream& writeObject(std::ostream& s){
//#if size>0
		//std::cout << "writeObject:\n";
		for (unsigned int i=0;i<size;i++){
			WRITE_STREAM(s, _p[i]);
		}
#ifdef DEBUG_SERIALIZE
		print();
#endif
//#endif
		//std::cout << "end writeObject:\n";
		return s;
	}
		
	///Streams the parameter in XML format
	/**
	\param s Stream
	*/
	void streamStateXML(std::ostream& s) const{
		//std::cout << "streamStateXML:\n";
		s << TAG_PARAMo;
//#if size>0
		for (unsigned int i=0;i<size;i++){
			s << TAG_Pxo << i << ">" << _p[i] << TAG_Pxc << i << ">";
		}
//#endif
		s << TAG_PARAMc;
		//std::cout << "end streamStateXML:\n";
	}

	/* template <typename T, int size> void SizedParameter<T,size>::setP(T ip1 ...){
		T arg=ip1;
		va_list args; // argument list
		va_start(args, ip1); // initialize args
		for (unsigned int i=0;i<size;i++){
			_p[i]=arg;
			arg=va_arg(args, T);
		}
	}*/

	void getP(void* op1 ...) const {
		//std::cout << "getP:\n";
		if(size!=0){
//#if size>0
			T* arg= (T*) op1;
			va_list args; // argument list
			va_start(args, op1); // initialize args
			for (unsigned int i=0;i<size;i++){
				//std::cerr << "set Param " << i << "\n";
				*arg=_p[i];
				arg=va_arg(args, T*);
			}
		}
//#endif
		//std::cout << "end getP:\n";
	}
		
	//inline T getPByIndex(unsigned int iIndex){
	//	return _p[iIndex];
	//}
		
	void getStateHash(HashAlgo* iHash) const{
//#if size>0
		//std::cout << "add param vals:\n";
		for (unsigned int i=0;i<size;i++){
			iHash->addValue((HashValueType)_p[i]);
			//std::cout << _p[i] << ", ";
		}
			//std::cout << "\nend add param vals:\n";
//#endif
	}
		
	static void * operator new(size_t iSize){
			return memPool.pmalloc(iSize);
	}

	static void operator delete(void *p, size_t iSize){
			memPool.pfree(p, iSize);
	}
		
	void readFromStream(std::istream &is){
//#if size>0
		for (unsigned int i=0;i<size;i++){
			is >> _p[i];
		}
//#endif
	}
	protected:
		static MemPool<SizedParameter<T,size> > memPool;
//#if size>0
		T _p[(size==0)?1:size];
//#endif
		
};

template<class T,int size> MemPool<SizedParameter<T,size> > SizedParameter<T, size>::memPool(BLOCK_SIZE_PARAM);
#endif
