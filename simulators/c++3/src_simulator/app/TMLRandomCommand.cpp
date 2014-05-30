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
#include <TMLRandomCommand.h>
#include <SimComponents.h>
//#include <CommandListener.h>
#include <TMLTask.h>

TMLRandomCommand::TMLRandomCommand(ID iID, TMLTask* iTask, RangeFuncPointer iRangeFunc, ParamType* iResultVar, const char* iLiveVarList, bool iCheckpoint): TMLCommand(iID, iTask, 1, 1, iLiveVarList, iCheckpoint), _rangeFunc(iRangeFunc), _resultVar(iResultVar), _aMin(0){
	_type=RND;
}

unsigned int TMLRandomCommand::getRandomRange(){
	ParamType aMax;
	(_task->*_rangeFunc)(_aMin, aMax);
	//std::cout << "Got amin: " << _aMin << " got amax: " << aMax << "\n";
	return aMax-_aMin+1;
}

TMLCommand* TMLRandomCommand::prepareNextTransaction(){
	//std::cout << "In TMLChoice::prepare next transaction\n";
	if (_simComp->getStopFlag()){
		//std::cout << "aSimStopped=true " << std::endl;
		_simComp->setStoppedOnAction();
		_task->setCurrCommand(this);
		return this;  //for command which generates transactions this is returned anyway by prepareTransaction
	}
	TMLCommand* aNextCommand=getNextCommand();
	if (_randomValue==(unsigned int)-1){
		ParamType aMin, aMax;
		(_task->*_rangeFunc)(aMin, aMax);
		*_resultVar = myrand(aMin, aMax);
	}else{
		*_resultVar = _aMin + _randomValue;
		_randomValue=(unsigned int)-1;
	}
	_task->setCurrCommand(aNextCommand);
	_execTimes++;
#ifdef STATE_HASH_ENABLED
	if (_liveVarList!=0) _task->refreshStateHash(_liveVarList);
#endif
#ifdef LISTENERS_ENABLED
	NOTIFY_CMD_FINISHED(this);
	//NOTIFY_CMD_FINISHED(0);
#endif
	//std::cout << "after notify listeners" << std::endl;
	if (aNextCommand!=0) return aNextCommand->prepare(false);
	return 0;
}

std::string TMLRandomCommand::TMLRandomCommand::toString() const{
	std::ostringstream outp;	
	outp << "Random in " << TMLCommand::toString();
	return outp.str();
}
