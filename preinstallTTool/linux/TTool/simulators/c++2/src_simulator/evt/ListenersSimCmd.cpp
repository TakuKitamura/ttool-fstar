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

#include <ListenersSimCmd.h>
#include <SimComponents.h>
#include <SchedulableDevice.h>
#include <TMLCommand.h>
#include <TMLRandomChoiceCommand.h>
#include <TMLActionCommand.h>
#include <TMLNotifiedCommand.h>
#include <TMLWaitCommand.h>
#include <TMLTask.h>
#include <dlfcn.h>
#include <CPU.h>
#define COND_SOURCE_FILE_NAME "newlib.c"
#define COND_OBJ_FILE_NAME "newlib.o"

ID CondBreakpoint::_freeID=0;
bool Breakpoint::_enabled=true;
bool CondBreakpoint::_enabled=true;


//************************************************************************
RunXTransactions::RunXTransactions(SimComponents* iSimComp, unsigned int iTransToExecute):_simComp(iSimComp), _count(0), _transToExecute(iTransToExecute){
	for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->registerListener(this);	
}

RunXTransactions::~RunXTransactions(){
	for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->removeListener(this);
}

void RunXTransactions::transExecuted(TMLTransaction* iTrans, ID iID){
	_count++;
	if (_count>=_transToExecute){
		std::ostringstream aOut;
		aOut << MSG_RUNXTRANSACTIONS << ": " << _transToExecute;
		_simComp->setStopFlag(true, aOut.str());
		//return true;
	}
	//return false;
}
void RunXTransactions::setTransToExecute(unsigned int iTransToExecute){
	_transToExecute=iTransToExecute;
}


//************************************************************************
Breakpoint::Breakpoint(SimComponents* iSimComp):_simComp(iSimComp){
}

void Breakpoint::commandEntered(TMLCommand* iComm, ID iID){
	if (_enabled){
		_simComp->setStopFlag(true, MSG_BREAKPOINT);
		//return true;
	}
	//return false;
}

void Breakpoint::setEnabled(bool iEnabled){
	_enabled=iEnabled;
}


//************************************************************************
CondBreakpoint::CondBreakpoint(SimComponents* iSimComp, std::string iCond, TMLTask* iTask):_simComp(iSimComp), _condText(iCond), _condFunc(0), _dlHandle(0),  _task(iTask), _cSourceFileCreated(false), _objectFileCreated(false), _libFileCreated(false){
	_ID=_freeID++;
	FILE* in;
	std::ofstream myfile (COND_SOURCE_FILE_NAME);
	char aExeName[256];
	int len = getexename(aExeName, sizeof(aExeName));
	if (len==-1) return;
	aExeName[len-6]=0;
	//strcat(aExeName, "/src_simulator");
	std::cout << "ExeName: " << aExeName << std::endl;
	if (myfile.is_open()){
		_cSourceFileCreated=true;
		std::ostringstream aCmd;
		myfile << "#include <" << iTask->toString() << ".h>\n";
		//for(VariableLookUpTableName::const_iterator i=iTask->getVariableIteratorName(false); i !=iTask->getVariableIteratorName(true); ++i){
		for(VariableLookUpTableName::const_iterator i=iTask->getVariableLookUpTableName().begin(); i !=iTask->getVariableLookUpTableName().end(); ++i){ 
			myfile << "#define " << *(i->first) << " _castTask_->" << *(i->first) << "\n";
		}
		myfile << "class TMLTask;\n\n";
		myfile << "extern \"C\" bool condFunc(TMLTask* _ioTask_){\n";
		myfile << "    " << iTask->toString() << "* _castTask_ = dynamic_cast<" << iTask->toString() << "*>(" << "_ioTask_" << ");\n";
		myfile << "    return (" << iCond << ");\n";
		myfile << "}\n";
		myfile.close();
		aCmd << "g++ -c -fPIC -Wall " << COND_SOURCE_FILE_NAME << " -I" << aExeName << " -I" << aExeName << "/src_simulator"; 
		//in = popen("g++ -c -fPIC -Wall newlib.c -I. -I./src_simulator", "r");
		in = popen(aCmd.str().c_str(), "r");
		if (pclose(in)!=0){
			std::cout << "Compiler error!\n";
       			return;
		}
		_objectFileCreated=true;
		aCmd.str("");	
		aCmd << "g++ -shared -Wl,-soname," << "lib" << _ID  << ".so.1" << " -o " << "lib" << _ID << ".so.1.0.1 " << COND_OBJ_FILE_NAME;
		//in = popen("g++ -shared -Wl,-soname,l.so.1 -o l.so.1.0.1 newlib.o", "r");
		in = popen(aCmd.str().c_str(), "r");
		if (pclose(in)!=0){
			std::cout << "Compiler error!\n";
			return;
		}
		_libFileCreated=true;
	}else{
		std::cout << "Error when creating C condition source file.\n";
    		return;
  	}
	std::ostringstream aCmd;
	aCmd << "lib" << _ID << ".so.1.0.1";
	_dlHandle = dlopen(aCmd.str().c_str(), RTLD_LAZY);
	if (!_dlHandle){
		std::cout << "Error " << dlerror() << " occurred when opening shared library.\n";
		return;
	}
	_condFunc = (BreakCondFunc) dlsym( _dlHandle, "condFunc");
	if (dlerror() != NULL) {
		std::cout << "Error when getting function handle\n";
    		return;
	}
	//Take task into account, to register at every command
	TMLCommand::registerGlobalListenerForType<TMLChoiceCommand>(this, iTask);
	TMLCommand::registerGlobalListenerForType<TMLActionCommand>(this, iTask);
	TMLCommand::registerGlobalListenerForType<TMLNotifiedCommand>(this, iTask);
	TMLCommand::registerGlobalListenerForType<TMLWaitCommand>(this, iTask);
}

void CondBreakpoint::commandFinished(TMLCommand* iComm, ID iID){
	if (_enabled && _condFunc!=0){
		if ((*_condFunc)(_task)){
			std::ostringstream aOut;
			aOut << MSG_CONDBREAKPOINT << ": " << _condText;
			_simComp->setStopFlag(true, aOut.str());
			std::cout << "Stop simulation due to condition\n";
			//return true;
		}
	}
	//return false;
}

void CondBreakpoint::setEnabled(bool iEnabled){
	_enabled=iEnabled;
}

bool CondBreakpoint::conditionValid() const{
	return (_condFunc!=0);
}

CondBreakpoint::~CondBreakpoint(){
	TMLCommand::removeGlobalListener(this);
	if (_dlHandle!=0) dlclose(_dlHandle);
	if (_cSourceFileCreated) remove(COND_SOURCE_FILE_NAME);
	if (_objectFileCreated) remove(COND_OBJ_FILE_NAME);
	if (_libFileCreated){
		std::ostringstream aFileName;
		aFileName << "lib" << _ID << ".so.1.0.1";
		remove(aFileName.str().c_str());
	}
}


//************************************************************************
RunTillNextRandomChoice::RunTillNextRandomChoice(SimComponents* iSimComp):_simComp(iSimComp), _enabled(false){
	TMLCommand::registerGlobalListenerForType<IndeterminismSource>(this,0);
}

void RunTillNextRandomChoice::commandEntered(TMLCommand* iComm, ID iID){
	IndeterminismSource* aChoice=dynamic_cast<IndeterminismSource*>(iComm);
	if (_enabled && aChoice!=0 ){
		_simComp->setStopFlag(true, MSG_RANDOMCHOICE);
		//return true;
	}
	//return false;
}

void RunTillNextRandomChoice::setEnabled(bool iEnabled){
	_enabled=iEnabled;
}


//************************************************************************
RunXCommands::RunXCommands(SimComponents* iSimComp, unsigned int iCommandsToExecute):_simComp(iSimComp), _count(0), _commandsToExecute(iCommandsToExecute){
	TMLCommand::registerGlobalListener(this);
}

RunXCommands::~RunXCommands(){
	TMLCommand::removeGlobalListener(this);
}

void RunXCommands::commandFinished(TMLCommand* iComm, ID iID){
	_count++;
	if (_count>=_commandsToExecute){
		std::ostringstream aOut;
		aOut << MSG_RUNXCOMMANDS << ": " << _commandsToExecute;
		_simComp->setStopFlag(true, aOut.str());
		std::cout << "x commands stopped sim\n";
		//return true;
	}
	//return false;
}

void RunXCommands::setCmdsToExecute(unsigned int iCommandsToExecute){
	_commandsToExecute=iCommandsToExecute;
}


//************************************************************************
RunXTimeUnits::RunXTimeUnits(SimComponents* iSimComp, TMLTime iEndTime):_simComp(iSimComp), _endTime(iEndTime){
	for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->registerListener(this);	
}

RunXTimeUnits::~RunXTimeUnits(){
	for(CPUList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->removeListener(this);
}

void RunXTimeUnits::transExecuted(TMLTransaction* iTrans, ID iID){
	if (SchedulableDevice::getSimulatedTime()>=_endTime){
		_simComp->setStopFlag(true, MSG_RUNXTIMEUNITS);
		//return true;
	}
	//return false;
}

void RunXTimeUnits::setEndTime(TMLTime iEndTime){
	_endTime=iEndTime;
}


//************************************************************************
RunTillTransOnDevice::RunTillTransOnDevice(SimComponents* iSimComp, ListenerSubject <GeneralListener> * iSubject):_simComp(iSimComp), _subject(iSubject) {
	_subject->registerListener(this);
}
RunTillTransOnDevice::~RunTillTransOnDevice(){
	_subject->removeListener(this);
}

void RunTillTransOnDevice::transExecuted(TMLTransaction* iTrans, ID iID){
	_simComp->setStopFlag(true, MSG_TRANSONDEVICE);
	//return true;
}


//************************************************************************
RunTillTransOnTask::RunTillTransOnTask(SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSubject):_simComp(iSimComp), _subject(iSubject) {
	_subject->registerListener(this);
}

RunTillTransOnTask::~RunTillTransOnTask(){
	_subject->removeListener(this);
}

void RunTillTransOnTask::transExecuted(TMLTransaction* iTrans, ID iID){
	_simComp->setStopFlag(true, MSG_TRANSONTASK);
	//return true;
}


//************************************************************************
RunTillTransOnChannel::RunTillTransOnChannel(SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSubject):_simComp(iSimComp), _subject(iSubject) {
	_subject->registerListener(this);
}

RunTillTransOnChannel::~RunTillTransOnChannel(){
	_subject->removeListener(this);
}

void RunTillTransOnChannel::transExecuted(TMLTransaction* iTrans, ID iID){
	_simComp->setStopFlag(true, MSG_TRANSONCHANNEL);
	//return true;
}

//************************************************************************
//{SIM_START, SIM_END, TIME_ADV, TASK_START, TASK_END, CMD_RUNNABLE, CMD_START, CMD_END, TRANS_EXEC} EventType;
TEPESigListener::TEPESigListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, unsigned int iEvtBitmap, unsigned int iTransTypeBitmap, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator): _subjectIDs(iSubjectIDs), _nbOfSubjectIDs(iNbOfSubjectIDs), _evtBitmap(iEvtBitmap), _transTypeBitmap(iTransTypeBitmap), _nbOfSignals(inbOfSignals), _notifConstr(iNotifConstr), _notifFunc(iNotifFunc), _sigNotified(false), _simComp(iSimComp), _simulator(iSimulator){
	for (unsigned int i=0; i< _nbOfSubjectIDs; i++){
		ListenerSubject <GeneralListener>*  aSubject = _simComp->getListenerByID(_subjectIDs[i]);
		if (aSubject!=0) aSubject->registerListener(this);
	}
	_simulator->registerListener(this);
}

TEPESigListener::~TEPESigListener(){
	std::cerr << "Delete Sig\n";
	for (unsigned int i=0; i< _nbOfSubjectIDs; i++){
		ListenerSubject <GeneralListener>*  aSubject = _simComp->getListenerByID(_subjectIDs[i]);
		if (aSubject!=0) aSubject->removeListener(this);
	}
	/*for (unsigned int i=0; i<_nbOfSignals; i++){
		std::cout << "loop\n";
		if (dynamic_cast<PropertyConstraint*>(_notifConstr[i])==0){
			delete _notifConstr[i];
			std::cout << "Delete done II\n";
		}
	}*/
	_simulator->removeListener(this);
	delete [] _subjectIDs;
	delete [] _notifConstr;
	delete [] _notifFunc;
	std::cerr << "End Delete Sig\n";
}

void TEPESigListener::simulationStarted(){
	//(_notifConstr->*_notifFunc)((_evtBitmap & SIM_START)!=0);
	bool aNotified = (_evtBitmap & SIM_START)!=0;
	for (unsigned int i=0; i<_nbOfSignals; i++)
		(_notifConstr[i]->*_notifFunc[i])(aNotified);
}

void TEPESigListener::simulationStopped(){
	//(_notifConstr->*_notifFunc)((_evtBitmap & SIM_END)!=0);
	bool aNotified = (_evtBitmap & SIM_END)!=0;
	for (unsigned int i=0; i<_nbOfSignals; i++)
		(_notifConstr[i]->*_notifFunc[i])(aNotified);
}

void TEPESigListener::timeAdvances(TMLTime iCurrTime){
	/*if(_sigNotified){
		(_notifConstr->*_notifFunc)(true);
		_sigNotified=false;
	}else{
		(_notifConstr->*_notifFunc)(false);
	}*/
	//if (_sigNotified) std::cout << "Signal notified!!!\n";
	for (unsigned int i=0; i<_nbOfSignals; i++)
		(_notifConstr[i]->*_notifFunc[i])(_sigNotified);
	_sigNotified=false;
}

void TEPESigListener::taskStarted(TMLTransaction* iTrans, ID iID){
	if ((_evtBitmap & TASK_START)!=0 && (iTrans->getCommand()->getType() & _transTypeBitmap)!=0){
		//(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}
}

void TEPESigListener::taskFinished(TMLTransaction* iTrans, ID iID){
	if ((_evtBitmap & TASK_END)!=0 && (iTrans->getCommand()->getType() & _transTypeBitmap)!=0){
		//(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}
}

void TEPESigListener::transExecuted(TMLTransaction* iTrans, ID iID){
	if ((_evtBitmap & TRANS_EXEC)!=0 && (iTrans->getCommand()->getType() & _transTypeBitmap)!=0){
		//(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}
}

void TEPESigListener::commandEntered(TMLCommand* iComm, ID iID){
	if ((_evtBitmap & CMD_RUNNABLE)!=0 && (iComm->getType()& _transTypeBitmap)!=0){
		//(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}
}

void TEPESigListener::commandFinished(TMLCommand* iComm, ID iID){
//void TEPESigListener::commandFinished(TMLTransaction* iTrans, ID iID){
	//std::cout << "cmd finished!!!\n";
	//std::cout << "cmd_end: " << (1<<CMD_END) << "\n";
	//std::cout << "_evtBitmap: " << _evtBitmap << "\n";
	std::cout << "command finished...\n";
	if ((_evtBitmap & CMD_END)!=0 && ( iComm->getType() & _transTypeBitmap)!=0){
		//(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}else
		std::cout << "but not taken into account\n";
}

void TEPESigListener::commandStarted(TMLCommand* iComm, ID iID){
	if ((_evtBitmap & CMD_START)!=0 && (iComm->getType() & _transTypeBitmap)!=0){
		//(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}
}


//***********************************************************************
TEPEFloatingSigListener::TEPEFloatingSigListener(ListenerSubject<GeneralListener>* iSimulator, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, unsigned int iNbOfStartNodes, PropertyConstraint** iStartNodes): _simulator(iSimulator), _nbOfSignals(inbOfSignals), _notifConstr(iNotifConstr), _notifFunc(iNotifFunc), _nbOfStartNodes(iNbOfStartNodes), _startNodes(iStartNodes){
	_simulator->registerListener(this);
}

TEPEFloatingSigListener::~TEPEFloatingSigListener(){
	std::cerr << "Delete Floating\n";
	_simulator->removeListener(this);
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		delete _startNodes[i];
	}
	delete [] _notifConstr;
	delete [] _notifFunc;
	delete [] _startNodes;
	std::cerr << "End Delete Floating\n";
}
void TEPEFloatingSigListener::timeAdvances(TMLTime iCurrTime){
	std::cout << "New simulation time: " << iCurrTime << "\n";
	for (unsigned int i=0; i<_nbOfSignals; i++){
		(_notifConstr[i]->*_notifFunc[i])(false);
	}
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		_startNodes[i]->notifyEnable(0);
	}
}

void TEPEFloatingSigListener::simulationStarted(){
	for (unsigned int i=0; i<_nbOfSignals; i++){
		(_notifConstr[i]->*_notifFunc[i])(false);
	}
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		_startNodes[i]->notifyEnable(2);
	}
}

void TEPEFloatingSigListener::simulationStopped(){
	for (unsigned int i=0; i<_nbOfSignals; i++){
		(_notifConstr[i]->*_notifFunc[i])(false);
	}
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		_startNodes[i]->notifyEnable(1);
	}
	//for (unsigned int i=0; i<_nbOfStartNodes; i++)
	//	std::cout << "Eval Prop " << i << ": " << _startNodes[i]->evalProp() << "\n";
}

void TEPEFloatingSigListener::evaluate(){
	for (unsigned int i=0; i<_nbOfStartNodes; i++)
		std::cout << "Eval Prop " << i << ": " << _startNodes[i]->evalProp() << "\n";
}

std::ostream& TEPEFloatingSigListener::writeObject(std::ostream& s){
	std::cout << "TEPEFloatingSigListener::writeObject\n";
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		_startNodes[i]->writeObject(s);
	}
	return s;
}

std::istream& TEPEFloatingSigListener::readObject(std::istream& s){
	std::cout << "TEPEFloatingSigListener::readObject\n";
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		_startNodes[i]->readObject(s);
	}
	return s;
}

void TEPEFloatingSigListener::reset(){
	for (unsigned int i=0; i<_nbOfStartNodes; i++){
		_startNodes[i]->reset();
	}
}


//***********************************************************************
TEPEEquationListener::TEPEEquationListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, ParamType** iVar, EqFuncPointer iEqFunc, SignalConstraint* iNotifConstr, NtfSigFuncPointer iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator)
  : _subjectIDs(iSubjectIDs), _nbOfSubjectIDs(iNbOfSubjectIDs), _var(iVar), _eqFunc(iEqFunc), _eqResult(true), _notifConstr(iNotifConstr), _notifFunc(iNotifFunc), _sigNotified(false), _simComp(iSimComp), _simulator(iSimulator){
	//std::cerr << "before func\n";
	//std::cerr << "before loop\n";
	for (unsigned int i=0; i< _nbOfSubjectIDs; i++){
		//std::cerr << "next id: " << _subjectIDs[i] << "\n";
		ListenerSubject <GeneralListener>*  aSubject = _simComp->getListenerByID(_subjectIDs[i]);
		if (aSubject!=0) aSubject->registerListener(this);
	}
	_simulator->registerListener(this);
}

TEPEEquationListener::~TEPEEquationListener(){
	std::cerr << "Delete Eq\n";
	for (unsigned int i=0; i< _nbOfSubjectIDs; i++){
		ListenerSubject <GeneralListener>*  aSubject = _simComp->getListenerByID(_subjectIDs[i]);
		if (aSubject!=0) aSubject->removeListener(this);
	}
	_simulator->removeListener(this);
	delete [] _subjectIDs;
	delete [] _var;
	std::cerr << "End Delete Eq\n";
}

void TEPEEquationListener::commandFinished(TMLCommand* iComm, ID iID){
	//if several alternations of variables arise at the same time only last value is taken into account
	//_sigNotified = (_eqResult != _eqFunc(_var));
	_sigNotified = true;
	std::cout << "Check equation result: " << _sigNotified << "\n";
}

void TEPEEquationListener::timeAdvances(TMLTime iCurrTime){
	if(_sigNotified){
		_sigNotified=false;
		bool aNewEqResult = _eqFunc(_var);
		(_notifConstr->*_notifFunc)(_eqResult != aNewEqResult);
		_eqResult = aNewEqResult;
	}else{
		(_notifConstr->*_notifFunc)(false);
	}
}

void TEPEEquationListener::simulationStarted(){
	//(_notifConstr->*_notifFunc)(false);
	bool aNewEqResult =  _eqFunc(_var);
	(_notifConstr->*_notifFunc)(_eqResult !=aNewEqResult);
	_eqResult = aNewEqResult;
}

void TEPEEquationListener::simulationStopped(){
	(_notifConstr->*_notifFunc)(false);
}


//***********************************************************************
TEPESettingListener::TEPESettingListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, ParamType** iVar, SettingFuncPointer iSetFunc, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator): _subjectIDs(iSubjectIDs), _nbOfSubjectIDs(iNbOfSubjectIDs), _var(iVar), _setFunc(iSetFunc), _nbOfSignals(inbOfSignals), _setResult( _setFunc(_var)), _notifConstr(iNotifConstr), _notifFunc(iNotifFunc), _sigNotified(false), _simComp(iSimComp), _simulator(iSimulator){
	for (unsigned int i=0; i< _nbOfSubjectIDs; i++){
		//std::cerr << "next id: " << _subjectIDs[i] << "\n";
		ListenerSubject <GeneralListener>*  aSubject = _simComp->getListenerByID(_subjectIDs[i]);
		if (aSubject!=0) aSubject->registerListener(this);
	}
	_simulator->registerListener(this);
}

TEPESettingListener::~TEPESettingListener(){
	std::cerr << "Delete Setting\n";
	for (unsigned int i=0; i< _nbOfSubjectIDs; i++){
		ListenerSubject <GeneralListener>*  aSubject = _simComp->getListenerByID(_subjectIDs[i]);
		if (aSubject!=0) aSubject->removeListener(this);
	}
	_simulator->removeListener(this);
	delete [] _subjectIDs;
	delete [] _var;
	delete [] _notifConstr;
	delete [] _notifFunc;
	std::cerr << "End Delete Setting\n";
}

void TEPESettingListener::commandFinished(TMLCommand* iComm, ID iID){
	/*if (_eqResult != _eqFunc(_var)){
		_eqResult = !_eqResult;
		(_notifConstr->*_notifFunc)(true);
		_sigNotified=true;
	}*/
	//if several alternations of variables at the same time only last one is taken into account
	_sigNotified=true;
	//std::cout << "Check setting result: " << _setNewResult << "\n";
}

void TEPESettingListener::timeAdvances(TMLTime iCurrTime){
	bool aSigNotification;
	if (_sigNotified){
		ParamType aNewSetResult = _setFunc(_var);
		_sigNotified=false;
		aSigNotification = (_setResult != aNewSetResult);
		_setResult = aNewSetResult;
	}else{
		aSigNotification=false;
	}
	for (unsigned int i=0; i<_nbOfSignals; i++)
		(_notifConstr[i]->*_notifFunc[i])(aSigNotification);
}

void TEPESettingListener::simulationStarted(){
	for (unsigned int i=0; i<_nbOfSignals; i++)
		(_notifConstr[i]->*_notifFunc[i])(false);
}

void TEPESettingListener::simulationStopped(){
	for (unsigned int i=0; i<_nbOfSignals; i++)
		(_notifConstr[i]->*_notifFunc[i])(false);
}

