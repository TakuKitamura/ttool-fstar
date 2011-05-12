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

#ifndef MemPoolH
#define MemPoolH

#include <definitions.h>

///Generic Memory pool
template <typename T>
class MemPool {
public:
	///Constructor
	/**
	\param iBlockSize Size of chunks to be allocated
	*/
	MemPool(unsigned int iBlockSize) :_headFreeList(0), _blockSize(iBlockSize){}
	//MemPool();
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
		//std::cerr << "ALLOC in MemPool " << this << "--------------\n";
		T* aHead = _headFreeList;
		if (aHead){
			_headFreeList = *(reinterpret_cast<T**>(aHead));
		}else{
			T** aAdr;
			T* newBlock = static_cast<T*>(::operator new(_blockSize * sizeof(T)));
			_chunkList.push_back(newBlock);
			for (unsigned int i = 1; i < _blockSize-1; ++i){
				aAdr = reinterpret_cast<T**>(&newBlock[i]);
				*aAdr = &newBlock[i+1];
			}
			aAdr = reinterpret_cast<T**>(&newBlock[_blockSize-1]);
			*aAdr = 0;
			aHead = newBlock;
			_headFreeList = &newBlock[1];
		}
		return aHead;
	}

	///Deallocation method
	/**
	\param p Pointer to the memory chunk to be deallocated 
	\param n Size of memory chunk to be deallocated
	*/
	void pfree(void *p, unsigned int n){
		//std::cerr << "DELETE in MemPool--------------\n";
		if (p == 0) return;
		if (n != sizeof(T)){
			std::cerr << "MemPool ERROR delete********\n";
			::operator delete(p);
			return;
		}
		T* aDelObj = static_cast<T*>(p);
		T** aAdr = reinterpret_cast<T**>(aDelObj);
		//std::cerr << "Lets crash\n";
		*aAdr = _headFreeList;
		//std::cerr << "Not crashed\n";
		_headFreeList = aDelObj;
	}
	///Reset memory pool, deallocate all memory chunks
	void reset(){
		_headFreeList=0;
		for(typename std::list<T*>::iterator i=_chunkList.begin(); i != _chunkList.end(); ++i) ::operator delete(*i);
		_chunkList.clear();
	}
	///Destructor
	~MemPool(){
		reset();
	}
private:
	///Head pointer of the free list
	T* _headFreeList;
	///Size of chunks to be allocated
	unsigned int _blockSize;
	///List containing allocated memory chunks
	std::list<T*> _chunkList;
};
#endif
