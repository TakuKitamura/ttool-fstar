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

#ifndef EBRDDH
#define EBRDDH

#include <definitions.h>
#include <Serializable.h>

class EBRDDCommand;

///Base class for Event Based Requirement Description Diagrams
class EBRDD: public Serializable{
public:	
	///Constructor
    	/**
      	\param iID ID of the EBRDD
	\param iName Name of the EBRDD
    	*/
	EBRDD(unsigned int iID, std::string iName);
	///Destructor
	virtual ~EBRDD();
	///Returns a pointer to the current command of the EBRDD
	/**
      	\return Pointer to the current command
    	*/
	EBRDDCommand* getCurrCommand() const;
	///Sets the pointer to the current command of the EBRDD
	/**
      	\param iCurrCommand Pointer to the current command
    	*/
	void setCurrCommand(EBRDDCommand* iCurrCommand);
	///Returns a string representation of the EBRDD
	/**
	\return Detailed string representation
	*/
	virtual std::string toString() const;
	///Returns a short string representation of the EBRDD
	/**
	\return Short string representation
	*/
	std::string toShortString() const;
	///Returns the unique ID of the EBRDD
	/**
      	\return Unique ID
    	*/ 
	unsigned int getID() const;
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void reset();
	/////Returns a pointer to the EBRDD variable specified by its name
	////**
	//\param iVarName Name of the EBRDD variable
	//\param oIsId Is set to true if an ID was passed to this function 
	//\return Pointer to the variable
	//*/
	//ParamType* getVariableByName(std::string& iVarName ,bool& oIsId);
	///Searches for a Command based on its ID
	/**
	\param iID ID of the Command
	\return Pointer to the Commmand
	*/
	EBRDDCommand* getCommandByID(unsigned int iID);
	///Adds a new command to the internal list
	/**
	\param iID ID of the command
	\param iCmd Pointer to the command
	*/
	void addCommand(unsigned int iID, EBRDDCommand* iCmd);
	/////Returns a pointer to the EBRDD variable specified by its ID
	////**
	//\param iVarID ID of the EBRDD variable
	//\return Pointer to the variable
	//*/
	//ParamType* getVariableByID(unsigned int iVarID);
	/////Returns an iterator for the internal variable ID hash table
	////**
	//\param iEnd true for iterator pointing to the end of the table, false for iterator pointing to the first element
	//\return Const iterator for variable table
	//*/
	//VariableLookUpTableID::const_iterator getVariableIteratorID(bool iEnd) const;
	/////Returns an iterator for the internal variable Name hash table
	////**
	//\param iEnd true for iterator pointing to the end of the table, false for iterator pointing to the first element
	//\return Const iterator for variable table
	//*/
	//VariableLookUpTableName::const_iterator getVariableIteratorName(bool iEnd) const;
	///Is called when a stop command is encountered
	void finished();
	//TMLTransaction* getNextTransaction() const;
protected:
	///ID of the EBRDD
	unsigned int _ID;
	///Name of the EBRDD
	std::string _name;
	///Pointer to the current command of the EBRDD
	EBRDDCommand* _currCommand;
	///Pointer to the first command of the EBRDD
	EBRDDCommand* _firstCommand;
	///Look up table for EBRDD variables (by name)
	VariableLookUpTableName _varLookUpName;
	/////Look up table for EBRDD variables (by ID)
	//VariableLookUpTableID _varLookUpID;
	///Hash table containing commands
	CommandHashTabEBRDD _commandHash;
};

#endif
