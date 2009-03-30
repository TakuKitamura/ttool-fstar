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

#ifndef RefValUnionH
#define RefValUnionH
#include <definitions.h>

///This class encapsulates a pointer to a value or the value itself
template <typename T>
class RefValUnion{
public:
	///Constructor called for constants
	/**
	\param in Constant reference to value
	*/
	RefValUnion(const T& in):isValue(true), value(in){
		//std::cout << "const constructor executed" << std::endl;
		//value=in;
	}
	///Constructor called for variables
	/**
	\param in Reference to variable
	*/
	RefValUnion(T& in):isValue(false), pointer(&in){
		//std::cout << "varible constructor executed" << std::endl;
		//pointer=&in;
	}
	RefValUnion(std::istream& s, unsigned int iAdr){
		READ_STREAM(s, isValue);
		if (isValue){
			READ_STREAM(s, value);
		}else{
			unsigned int aAddrOffs;
			READ_STREAM(s, aAddrOffs);
			pointer = (T*)(iAdr + aAddrOffs);
		}			
	}
	///The parenthesis operator returns a reference to the stored value
	/**
	\return Reference to value 
	*/
	inline T& operator() (){if (isValue) return value; else return *pointer;}
	///The parenthesis operator returns a reference to the stored value
	/**
	\return Constant reference to value 
	*/
	inline const T& operator() () const {if (isValue) return value; else return *pointer;}
	
	T print() const {return value;}
	friend std::istream& operator >> (std::istream &is,RefValUnion<T> &obj){
		is >> obj.value;
		obj.isValue=true;
		return is;
	}
	std::ostream& writeObject(std::ostream& s, unsigned int iAdr){
		WRITE_STREAM(s,isValue);
		if (isValue){
			WRITE_STREAM(s,value);
		}else{
			unsigned int aAdr=((unsigned int)pointer)-iAdr;
			WRITE_STREAM(s,aAdr);
		}
		return s;
	}
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
#endif

