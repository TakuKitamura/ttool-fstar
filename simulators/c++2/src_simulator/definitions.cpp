#include <definitions.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLTask.h>

Pool<Comment> Comment::memPool;

int myrand(int n1, int n2){
	static bool firstTime = true;
	if(firstTime){
		srand(time(NULL));
		firstTime = false;
	}
	n2++;
	int r = (n1 + (int)(((float)(n2 - n1))*rand()/(RAND_MAX + 1.0)));
	std::cout << "random number: " << r << std::endl;
	//return (n1 + (int)(((float)(n2 - n1))*rand()/(RAND_MAX + 1.0)));
	return r;
	//return n1 + rand()/(RAND_MAX/(n2-n1+1));
}

long getTimeDiff(struct timeval& begin, struct timeval& end){
	return end.tv_usec-begin.tv_usec+(end.tv_sec-begin.tv_sec)*1000000;
}


bool greaterRunnableTime::operator()(TMLTransaction const* p1, TMLTransaction const* p2){
	//std::cout << "greaterRunnableTime\n";
	return p1->getRunnableTime() > p2->getRunnableTime();
}

bool greaterPrio::operator()(TMLTransaction const* p1, TMLTransaction const* p2){
	//std::cout << "greaterPrio\n";
	return p1->getCommand()->getTask()->getPriority() > p2->getCommand()->getTask()->getPriority();
}

bool greaterStartTime::operator()(TMLTransaction const* p1, TMLTransaction const* p2){
	return p1->getStartTime() > p2->getStartTime();
}

/*template<class T> Pool<T>::Pool():_headFreeList(0){}

template<class T> void* Pool<T>::pmalloc(unsigned int n){
	if (n != sizeof(T)) return ::operator new(n);
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

template<class T> void Pool<T>::pfree(void *p, unsigned int n){
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

template<class T> Pool<T>::~Pool(){
	//std::list<T*>::iterator i;
	//for(i=_chunkList.begin(); i != _chunkList.end(); ++i) ::operator delete [] *i;
}*/
