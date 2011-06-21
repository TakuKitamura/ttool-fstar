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

#include <TMLActionCommand.h>
#include <TMLTask.h>
#include <SimComponents.h>
//#include <CommandListener.h>

TMLActionCommand::TMLActionCommand(ID iID, TMLTask* iTask, ActionFuncPointer iActionFunc, const char* iLiveVarList, bool iCheckpoint):TMLCommand(iID, iTask, 1, 1, iLiveVarList, iCheckpoint),_actionFunc(iActionFunc){
	_type=ACT;
}

//void TMLActionCommand::execute(){
//}

TMLCommand* TMLActionCommand::prepareNextTransaction(){
	if (_simComp->getStopFlag()){
		_simComp->setStoppedOnAction();
		//std::cout << "sim stopped in action command " << std::endl;
		_task->setCurrCommand(this);
		return this;  //for command which generates transactions this is returned anyway by prepareTransaction
	}
	TMLCommand* aNextCommand=getNextCommand();
	//std::cout << "Action func CALLED length: " << *_pLength << " progress:" << _progress << std::endl;
	(_task->*_actionFunc)();
	_execTimes++;
#ifdef STATE_HASH_ENABLED
	if (_liveVarList!=0) _task->refreshStateHash(_liveVarList);
#endif
	_task->setCurrCommand(aNextCommand);
	//FOR_EACH_CMDLISTENER (*i)->commandFinished(this);
#ifdef LISTENERS_ENABLED
	NOTIFY_CMD_FINISHED(this);
	//NOTIFY_CMD_FINISHED(0);
#endif
	if (aNextCommand!=0) return aNextCommand->prepare(false);
	return 0;
}

/*TMLTask* TMLActionCommand::getDependentTask() const{
	return 0;
}*/

std::string TMLActionCommand::toString() const{
	std::ostringstream outp;	
	outp << "Action in " << TMLCommand::toString();
	return outp.str();
}

//std::string TMLActionCommand::toShortString() const{
//	return "Action";
//}

//std::string TMLActionCommand::getCommandStr() const{
//	return "act";
//}
