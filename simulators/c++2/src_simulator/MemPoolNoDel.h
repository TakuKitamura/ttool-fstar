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

#ifndef MemPoolNoDelH
#define MemPoolNoDelH

#include <definitions.h>

///Generic Memory pool without delete functionality
template <typename T>
class MemPoolNoDel {
public:
	///Constructor
	/**
	\param iChunkSize Size of chunks to be allocated
	*/
	MemPoolNoDel(unsigned int iChunkSize): _currChunk(0), _currPos(0), _chunkSize(iChunkSize), _currPosInList(_chunkList.end()){}
	///Allocation method
	/**
	\param n Size of memory chunk to be allocated
	\return Pointer to the allocated chunk of memory
	*/
	void* pmalloc(unsigned int n){
		if (n != sizeof(T)){
			std::cerr << "MemPool ERROR malloc********\n";
			return ::operator new(n);
		}
		if (_currChunk==0 || _currPos==_chunkSize){
			if (_currPosInList != _chunkList.end()) _currPosInList++;
			if (_currPosInList == _chunkList.end()){
				//std::cout << "Reallocate size=" << _chunkList.size() << "\n" ;
				_currChunk = static_cast<T*>(::operator new(_chunkSize * sizeof(T)));
				_chunkList.push_back(_currChunk);
				_currPosInList = _chunkList.end();
			}else{
				//std::cout << "Next List pos size=" << _chunkList.size() << "\n" ;
				_currChunk = *_currPosInList;
			}
			_currPos=0;
		}
		
		//std::cout << "Allocated adr: " << &_currChunk[_currPos] << "\n";
		return &_currChunk[_currPos++];
	}

	///Deallocation method
	/**
	\param p Pointer to the memory chunk to be deallocated 
	\param n Size of memory chunk to be deallocated
	*/
	void pfree(void *p, unsigned int n){
		//if (p == 0) return;
		if (p!=0 && n != sizeof(T)){
			std::cerr << "MemPool ERROR delete********\n";
			::operator delete(p);
			return;
		}
	}
	///Reset memory pool, deallocate all memory chunks
	void reset(){
		_currPosInList = _chunkList.begin();
		if (_currPosInList==_chunkList.end())
			_currChunk=0;
		else
			_currChunk = *_currPosInList;
		_currPos=0;
	}
	
	///Destructor
	~MemPoolNoDel(){
		for(typename std::list<T*>::const_iterator i=_chunkList.begin(); i != _chunkList.end(); ++i) ::operator delete(*i);
	}
private:
	///Current memory chunk to be used
	T* _currChunk;
	///Next free position in current chunk
	unsigned int  _currPos;
	///Size of chunks to be allocated
	unsigned int _chunkSize;
	///List containing allocated memory chunks
	std::list<T*> _chunkList;
	///Position in List
	typename std::list<T*>::iterator _currPosInList;
};
#endif
