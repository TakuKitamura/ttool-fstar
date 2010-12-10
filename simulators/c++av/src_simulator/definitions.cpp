#include<definitions.h>
#include<EventQueueCallback.h>

int myrand(int n1, int n2){
	static bool firstTime = true;
	if(firstTime){
		srand(time(NULL));
		firstTime = false;
	}
	n2++;
	int r = (n1 + (int)(((float)(n2 - n1))*rand()/(RAND_MAX + 1.0)));
	return r;
}

Simulator* EventQueueCallback::_simulator=0;
