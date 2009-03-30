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

#include <RefValUnion.h>
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
	Parameter(std::istream& s, unsigned int iAdr):_p1(s,iAdr), _p2(s,iAdr), _p3(s,iAdr){}
	///Assignement operator, copies all parameters
	const Parameter<T>& operator=(const Parameter<T>& rhs){
		_p1()=rhs._p1();
		_p2()=rhs._p2();
		_p3()=rhs._p3();
		return *this;
	}
	///Print function for testing purposes
	void print() const{
		std::cout << "p1:" << _p1.print() << " p2:" << _p2.print() << " p3:" << _p3.print() << std::endl;
	}
	inline std::ostream& writeObject(std::ostream& s, unsigned int iAdr){
		_p1.writeObject(s,iAdr);
		_p2.writeObject(s,iAdr);
		_p3.writeObject(s,iAdr);
		return s;
	}
	friend std::istream& operator >>(std::istream &is,Parameter<T> &obj){
		is >>obj._p1 >> obj._p2 >> obj._p3;
 		return is;
	}
	//inline static void * operator new(size_t size){
	//	return memPool.pmalloc(size);
	//}
	//inline static void operator delete(void *p, size_t size){
	//	memPool.pfree(p, size);
	//}
private:
	///Three parameters
	RefValUnion<T> _p1,_p2,_p3;
	//static Pool<Parameter<T> > memPool;
};
#endif
