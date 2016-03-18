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
#include<ServerExplore.h>
#include<SimServSyncInfo.h>
#include<Simulator.h>
#include<SimComponents.h>
#include<TMLChoiceCommand.h>

ServerExplore::ServerExplore(){
	for (int i=0; i<RECUR_DEPTH; i++) leafsForLevel[i]=0;
}

int ServerExplore::run(){
	_syncInfo->_simComponents->setBreakpointOnChoiceCmds();
	exploreTree(0);
}

void ServerExplore::exploreTree(unsigned int iDepth){
	bool aSimTerminated=false;
	TMLChoiceCommand* aChoiceCmd;
	std::stringstream aBuffer;
	if (iDepth==RECUR_DEPTH){
		std::ostringstream aFileName;
		aFileName << "canc" << iDepth << "." << leafsForLevel[iDepth]++;
		std::string aFileStr(aFileName.str());
		_syncInfo->_simulator->schedule2TXT(aFileStr);
	}else{
		do{
			aSimTerminated=_syncInfo->_simulator->runToNextBreakpoint();
			aChoiceCmd=_syncInfo->_simComponents->getCurrentChoiceCmd();
		}while (!aSimTerminated && aChoiceCmd==0);
		if (aSimTerminated){
			std::ostringstream aFileName;
			aFileName << "term" << iDepth << "." << leafsForLevel[iDepth]++;
			std::string aFileStr(aFileName.str());
			_syncInfo->_simulator->schedule2TXT(aFileStr);
		}else{
			_syncInfo->_simComponents->writeObject(aBuffer);
			for (unsigned int aBranch=0;aBranch<aChoiceCmd->getNumberOfBranches();aBranch++){
				aChoiceCmd->setPreferredBranch(aBranch);
				exploreTree(iDepth+1);
				_syncInfo->_simComponents->readObject(aBuffer);
			}
		}
	}
}

void ServerExplore::sendReply(std::string iReplyStr){
	//std::cout << iReplyStr;
}
