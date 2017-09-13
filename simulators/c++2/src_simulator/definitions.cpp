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

#include <definitions.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLTask.h>
#include <Bus.h>
#include <CPU.h>
#include <SchedulableDevice.h>
#include <ListenersSimCmd.h>
#include <BusMaster.h>
#include <SeqConstraint.h>
#include <LogConstraint.h>
#include <sys/types.h>
#include <unistd.h>

TMLTime SchedulableDevice::_simulatedTime=0;
TMLTime SchedulableDevice::_overallTransNo=0;
TMLTime SchedulableDevice::_overallTransSize=0;
ID TMLTransaction::_ID=1;

const unsigned int SeqConstraint::_transTableSeq[12]={0,20,0,0,16,16,10,32,32,32,11,32};
const unsigned int LogConstraint::_transTableLog[20]={0, 20, 52, 0, 16, 16, 10, 32, 32, 32, 11, 32, 48, 10, 48, 64, 64, 11, 64, 64};
//template<class T,int size> MemPool<SizedParameter<T,size> > SizedParameter<T, size>::memPool(BLOCK_SIZE_PARAM);

int myrand(int n1, int n2){
	static bool firstTime = true;
	if(firstTime){
		srand(time(NULL));
		firstTime = false;
	}
	n2++;
	int r = (n1 + (int)(((float)(n2 - n1))*rand()/(RAND_MAX + 1.0)));
	//std::cout << "random number: " << r << std::endl;
	//return (n1 + (int)(((float)(n2 - n1))*rand()/(RAND_MAX + 1.0)));
	return r;
	//return n1 + rand()/(RAND_MAX/(n2-n1+1));
}

long getTimeDiff(struct timeval& begin, struct timeval& end){
	return end.tv_usec-begin.tv_usec+(end.tv_sec-begin.tv_sec)*1000000;
}

/*bool greaterRunnableTime::operator()(TMLTransaction const* p1, TMLTransaction const* p2){
	return p1->getRunnableTime() > p2->getRunnableTime();
}

bool greaterPrio::operator()(TMLTransaction const* p1, TMLTransaction const* p2){
	return p1->getCommand()->getTask()->getPriority() > p2->getCommand()->getTask()->getPriority();
}*/

bool greaterStartTime::operator()(	TMLTransaction const* p1,
									TMLTransaction const* p2 ) {
	return p1->getStartTime() > p2->getStartTime();
}

void replaceAll( 	std::string& ioHTML,
					std::string iSearch,
					std::string iReplace ) {
	// Issue #4: aPos must be declared as size_type otherwise the comparison with npos does not work leading to a crash
	std::string::size_type aPos = 0;
//	unsigned int aPos=0;
	while ( aPos < ioHTML.length() && ( aPos = ioHTML.find( iSearch, aPos ) ) != std::string::npos ) {
		ioHTML.replace( aPos++, iSearch.length(), iReplace );
	}
}

bool ends_with(std::string const& str, std::string const& suffix) {
    return suffix.size() <= str.size() && str.find( suffix, str.size() - suffix.size()) != str.npos;
}

//std::string vcdValConvert(unsigned int iVal){
//	if(iVal==1 || iVal==2) return "1"; else return "0";
//}

std::string vcdTimeConvert( TMLTime iVal ) {
	std::string iResult;

	do{
		if (iVal & 1) iResult="1" + iResult; else iResult="0" + iResult;
		iVal >>= 1;
	}while(iVal);

	return iResult;
}

int getexename(	char* buf,
				size_t size ) {
	char linkname[64]; /* /proc/<pid>/exe */
	pid_t pid;
	int ret;
	pid = getpid();
	if (snprintf(linkname, sizeof(linkname), "/proc/%i/exe", pid) < 0) return -1;
	ret = readlink(linkname, buf, size);
	if (ret == -1 || ret>=size) return -1;
	buf[ret] = 0;
	return ret;
}

unsigned int getEnabledBranchNo(int iNo, int iMask){
	unsigned int retIndex=0;
	while (iMask!=0 && iNo!=0){
		if ((iMask & 1)!=0) iNo--;
		iMask >>= 1; retIndex++;
	}
	return retIndex-1;
}
