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


#ifndef AvBlockH
#define AvBlockH
#include <definitions.h>

class AvCmd;
class AvCheckpoint;
class AvNode;

class AvBlock{
public:
	AvBlock(ID iID, std::string iName);
	~AvBlock();
	//bool prepare();
	AvNode* execute(const SystemTransition& iTrans);
	bool isEnabled(EnabledTransList& iEnaTransList);
	AvCheckpoint* getCurrCommand() const;
	void setCurrCommand(AvCheckpoint* iCmd);
	std::string toString() const;
	ID getID() const;
	ParamType* getVariableByName(std::string& iVarName);
	AvCmd* getCommandByID(ID iID);
protected:
	void addCommand(ID iID, AvCmd* iCmd);
	///ID of the Block
	ID _ID;
	///Name of the Block
	std::string _name;
	///Pointer to the current command of the task
	AvCheckpoint* _currCommand;
	///Pointer to the first command of the task
	AvCmd* _firstCommand;
	///Look up table for task variables (by name)
	VariableLookUpTableName _varLookUpName;
	///Look up table for task variables (by ID)
	VariableLookUpTableID _varLookUpID;
};
#endif
