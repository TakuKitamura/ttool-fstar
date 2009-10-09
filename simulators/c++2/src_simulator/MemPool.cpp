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

#include<MemPool.h>
#include<TMLTransaction.h>
#include<Comment.h>

///Generic Memory pool class

template <typename T>
MemPool<T>::MemPool():_headFreeList(0){}

template <typename T>
void* MemPool<T>::pmalloc(unsigned int n){
	if (n != sizeof(T)){
		return ::operator new(n);
	}
	T* aHead = _headFreeList;
	if (aHead){
		_headFreeList = *(reinterpret_cast<T**>(aHead));
	}else{
		T** aAdr;
		T* newBlock = static_cast<T*>(::operator new(BLOCK_SIZE * sizeof(T)));
		_chunkList.push_back(newBlock);
		for (int i = 1; i < BLOCK_SIZE-1; ++i){
			aAdr = reinterpret_cast<T**>(&newBlock[i]);
			*aAdr = &newBlock[i+1];
		}
		aAdr = reinterpret_cast<T**>(&newBlock[BLOCK_SIZE-1]);
		*aAdr = 0;
		aHead = newBlock;
		_headFreeList = &newBlock[1];
	}
	return aHead;
}

template <typename T>
void MemPool<T>::pfree(void *p, unsigned int n){
	if (p == 0) return;
	if (n != sizeof(T)){
		::operator delete(p);
		return;
	}
	T* aDelObj = static_cast<T*>(p);
	T** aAdr = reinterpret_cast<T**>(aDelObj);
	*aAdr = _headFreeList;
	_headFreeList = aDelObj;
}

template <typename T>
void MemPool<T>::reset(){
	_headFreeList=0;
	for(typename std::list<T*>::iterator i=_chunkList.begin(); i != _chunkList.end(); ++i) ::operator delete(*i);
	_chunkList.clear();
}

template <typename T>
MemPool<T>::~MemPool(){
	reset();
}

template class MemPool<TMLTransaction>;
template class MemPool<Comment>;  
