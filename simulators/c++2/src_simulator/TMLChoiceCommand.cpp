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

#include <TMLChoiceCommand.h>
#include <TMLTask.h>
#include <TMLTransaction.h>

TMLChoiceCommand::TMLChoiceCommand(unsigned int iID, TMLTask* iTask, CondFuncPointer iCondFunc):TMLCommand(iID, iTask,1,0),_condFunc(iCondFunc),_indexNextCommand(0), _preferredBranch(-1){
}

void TMLChoiceCommand::execute(){
}

TMLCommand* TMLChoiceCommand::getNextCommand() const{
	if (_preferredBranch==-1){
		return _nextCommand[_indexNextCommand];
	}else{
		std::cout << "Command was enforced: " << _preferredBranch << std::endl;
		unsigned int aPreferredBranch=_preferredBranch;
		_preferredBranch=-1;
		return _nextCommand[aPreferredBranch];
	}
}

TMLCommand* TMLChoiceCommand::prepareNextTransaction(){
	TMLCommand* aNextCommand;
	//std::cout << "Choice func CALLED length: " << *_pLength << " progress:" << _progress << std::endl;
	_indexNextCommand=(_task->*_condFunc)();
	aNextCommand=getNextCommand();
	_task->setCurrCommand(aNextCommand);
	if (aNextCommand!=0) return aNextCommand->prepare(false);
	return 0;
}

TMLTask* TMLChoiceCommand::getDependentTask() const{
	return 0;
}

std::string TMLChoiceCommand::toString() const{
	std::ostringstream outp;	
	outp << "Choice in " << TMLCommand::toString() << " nextCommand:" << _indexNextCommand;
	return outp.str();
}

std::string TMLChoiceCommand::toShortString() const{
	return "Choice";
}

std::string TMLChoiceCommand::getCommandStr() const{
	return "choice";
}

void TMLChoiceCommand::setPreferredBranch(unsigned int iBranch){
	_preferredBranch=iBranch;
}
