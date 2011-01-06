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
#include <HashAlgo.h>

template <typename T>
class Parameter {
public:
	///Constructor
	
	Parameter(unsigned int iParamNo): _p(0), _paramNo(iParamNo){
		if(_paramNo!=0) _p = new T[_paramNo];
	}
	
	Parameter(unsigned int iParamNo, const T& ip1 ...): _p(0), _paramNo(iParamNo){
		_p = new T[_paramNo];
		T arg=ip1;
		va_list args; // argument list
		va_start(args, ip1); // initialize args
		for (unsigned int i=0;i<_paramNo;i++){
			_p[i]=arg;
			arg=va_arg(args, T);
		}
	}
		
	Parameter(const Parameter& iRhs): _p(0), _paramNo(iRhs._paramNo){
		if (_paramNo!=0){
			_p = new T[_paramNo];
			memcpy(_p, iRhs._p, _paramNo*sizeof(T));
		}
	}

	Parameter(unsigned int iParamNo, std::istream& s): _p(0), _paramNo(iParamNo){
		if (_paramNo!=0){
			_paramNo=iParamNo;
			_p = new T[_paramNo];
			for (unsigned int i=0;i<_paramNo;i++){
				READ_STREAM(s, _p[i]);
			}
#ifdef DEBUG_SERIALIZE
			print();
#endif
		}
	}
	
	~Parameter(){
		if (_p!=0) delete [] _p;
	}
	
	///Print function for testing purposes
	void print() const{
		std::cout << "print:\n";
		for (unsigned int i=0;i<_paramNo;i++){
			std::cout << " p[" << (i+1) << "]:" << _p[i];
		}
		std::cout << std::endl;
		std::cout << "end print:\n";
	}
	
	inline std::ostream& writeObject(std::ostream& s){
		std::cout << "writeObject:\n";
		for (unsigned int i=0;i<_paramNo;i++){
			WRITE_STREAM(s, _p[i]);
		}
#ifdef DEBUG_SERIALIZE
		print();
#endif
		std::cout << "end writeObject:\n";
		return s;
	}
	///Stream operator >>
	friend std::istream& operator >>(std::istream &is,Parameter<T> &obj){
		for (unsigned int i=0;i<obj._paramNo;i++){
			is >> obj._p[i];
		}
 		return is;
	}
	///Streams the parameter in XML format
	/**
	\param s Stream
	*/
	void streamStateXML(std::ostream& s) const{
		std::cout << "streamStateXML:\n";
		s << TAG_PARAMo;
		for (unsigned int i=0;i<_paramNo;i++){
			 s << TAG_Pxo << i << ">" << _p[i] << TAG_Pxc << i << ">";
		}
		s << TAG_PARAMc;
		std::cout << "end streamStateXML:\n";
	}

	/*inline void setP(T ip1 ...){
		T arg=ip1;
		va_list args; // argument list
		va_start(args, ip1); // initialize args
		for (unsigned int i=0;i<_paramNo;i++){
			_p[i]=arg;
			arg=va_arg(args, T);
		}
	}*/

	inline void getP(T* op1 ...) const {
		std::cout << "getP:\n";
		T* arg=op1;
		va_list args; // argument list
		va_start(args, op1); // initialize args
		for (unsigned int i=0;i<_paramNo;i++){
			*arg=_p[i];
			arg=va_arg(args, T*);
		}
		std::cout << "end getP:\n";
	}
	
	//inline T getPByIndex(unsigned int iIndex){
	//	return _p[iIndex];
	//}
	
	inline void getStateHash(HashAlgo* iHash) const{
		std::cout << "add param vals:\n";
		for (unsigned int i=0;i<_paramNo;i++){
			iHash->addValue((HashValueType)_p[i]);
		}
		std::cout << "end add param vals:\n";
	}
		
protected:
	//static Pool<Parameter<T> > memPool;
	T* _p;
	unsigned int _paramNo;
};
#endif
