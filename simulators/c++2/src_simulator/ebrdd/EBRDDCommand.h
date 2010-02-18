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

#ifndef EBRDDCommandH
#define EBRDDCommandH

#include <definitions.h>
#include <Serializable.h>

class EBRDD;

///This class defines the basic interfaces and functionalites of a EBRDD command. All specific EBRDD commands are derived from this base class. 
class EBRDDCommand: public Serializable {
public:
	///Constructor
    	/**
      	\param iID ID of the command
	\param iEBRDD Pointer to the EBRDD the command belongs to
    	*/
	EBRDDCommand(ID iID, EBRDD* iEBRDD);
	///Destructor
	virtual ~EBRDDCommand();
	///Initializes the command and passes the control flow to the prepare() method of the next command if necessary
	/**
      	\return The EBRDD command which is currently active
	*/
	virtual EBRDDCommand* prepare()=0;
	///Assigns a value to the pointer referencing the array of next commands
	/**
	\param iNextCommand Pointer to an array of pointers to the next commands
	*/
	void setNextCommand(EBRDDCommand** iNextCommand);
	///Returns a pointer to the EBRDD the command belongs to
	/**
	\return Pointer to the EBRDD
	*/
	EBRDD* getEBRDD() const;
	///Returns a string representation of the command
	/**
	\return Detailed string representation
	*/
	virtual std::string toString() const;
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	virtual void reset();
	///Returns the unique ID of the command
	/**
      	\return Unique ID
    	*/ 
	ID getID() const;
	////Sets the internal pointer to the simulation components
	////**
      	//\param iSimComp Pointer to simulation components
    	//*/ 
	//static void setSimComponents(SimComponents* iSimComp);
	/////Returns a pointer to the next command array and the number of successors of this command
	////**
	//\param oNbOfCmd Number of successors of this command
	//\return Pointer to next command array
	//*/
	//EBRDDCommand** getNextCommands(unsigned int& oNbOfCmd) const;
protected:
	///ID of the command
	ID _ID;
	///Pointer to the EBRDD the command belongs to
	EBRDD* _ebrdd;
	///Pointer to an array of pointers to the next commands
	EBRDDCommand** _nextCommand;
	/////Number of successors of this command
	//unsigned int _nbOfNextCmds;
};

#endif

