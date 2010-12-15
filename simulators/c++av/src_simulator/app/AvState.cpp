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

#include<AvState.h>
#include<AvTransition.h>
#include<AvBlock.h>

AvState::AvState(ID iID, std::string iName, AvBlock* iBlock, unsigned int iNbOfOutgoingTrans): AvCheckpoint(), AvCmd(iID, iName, iBlock, iNbOfOutgoingTrans), _lastControlTransfer(true){
}

AvState::~AvState(){
}

AvNode* AvState::prepare(bool iControlTransfer){
	//std::cout<< "prepare " << _name << "\n";
	_lastControlTransfer=iControlTransfer;
	if(_lastControlTransfer){
		AvTransition* directAccessTrans = determineDirectAccessTrans();
		if (directAccessTrans!=0){
			//std::cout<< _name << " go on to next\n";
			return directAccessTrans->prepare(true);
		}
		//std::cout<< "prepare transitions " << _name << "\n";
		_block->setCurrCommand(this);
		for (unsigned int i=0; i<_nbOfOutgoingTrans; i++)
			_outgoingTrans[i]->prepare(false);
	}
	//std::cout<< "end prepare " << _name << "\n";
	return this;
}

AvNode* AvState::execute(const SystemTransition& iTrans){
	if(_lastControlTransfer){
		for (unsigned int i=0; i<_nbOfOutgoingTrans; i++){
			if (_outgoingTrans[i]!= iTrans.firingTrans) _outgoingTrans[i]->cancel();
		}
		iTrans.firingTrans->execute(iTrans);
	}else{
		prepare(true);
	}
	return this;
}

bool AvState::isEnabled(EnabledTransList& iEnaTransList, AvTransition* iIncomingTrans){
	//if _lastControlTransfer==true iEnaTransList "go to state xy", but which transition? e
	bool aResult;
	if(_lastControlTransfer){
		aResult=false;
		for (unsigned int i=0; i<_nbOfOutgoingTrans; i++){
			aResult |= _outgoingTrans[i]->isEnabled(iEnaTransList,0);
		}
	}else{
		aResult=true;
		std::ostringstream aTransText;
		aTransText << "move on to state " << toString();
		iEnaTransList.push_back(SystemTransition(_block, iIncomingTrans, 0, aTransText.str()));
	}
	return aResult;
}
	
AvNode* AvState::cancel(){
	return this;
}

void AvState::setIncomingTrans(AvTransition* iTrans){
}

std::string AvState::toString() const{
	return AvNode::toString();
}

bool AvState::directExecution(){
	return true;
}

AvTransition* AvState::determineDirectAccessTrans(){
	AvTransition* aResTrans=0;
	for (unsigned int i=0; i<_nbOfOutgoingTrans; i++){
		if (_outgoingTrans[i]->directExecution()){
			if (aResTrans!=0) return 0;
			aResTrans=_outgoingTrans[i];
		}else{
			return 0;
		}
	}
	return aResTrans;
}

//void AvState::setOutgoingTrans(AvTransition** iTrans){
//	_outgoingTrans=iTrans;
//}
