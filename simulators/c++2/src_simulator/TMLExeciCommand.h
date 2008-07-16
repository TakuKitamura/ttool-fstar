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

#ifndef TMLExeciCommandH
#define TMLExeciCommandH

#include <definitions.h>
#include <TMLCommand.h>


///This class models a delay due to calculating time within a TML tasks.
class TMLExeciCommand:public TMLCommand{
public:
	///Constructor
    	/**
      	\param iTask Pointer to the task the command belongs to
	\param iLength Constant virtual length of the command
	\param iMaxDelay Constant maximal delay for Execi Interval commands 
    	*/
	//TMLExeciCommand(TMLTask* iTask, const TMLLength& iLength);
	//TMLExeciCommand(TMLTask* iTask, const TMLLength& iMinLen, const TMLLength& iMaxLen, unsigned int iType);
	TMLExeciCommand(TMLTask* iTask, LengthFuncPointer iLengthFunc, unsigned int iType);
	/*///Constructor
    	/**
      	\param iTask Pointer to the task the command belongs to
	\param iLength Constant virtual length of the command
	\param iMaxDelay Maximal delay for Execi Interval commands 
    	*/
	//TMLExeciCommand(TMLTask* iTask, const TMLLength& iMinLen, TMLLength& iMaxLen, unsigned int iType);
	/*///Constructor
    	/**
      	\param iTask Pointer to the task the command belongs to
	\param iLength Virtual length of the command
	\param iMaxDelay Constant maximal delay for Execi Interval commands
    	*/
	//TMLExeciCommand(TMLTask* iTask, TMLLength& iLength);
	//TMLExeciCommand(TMLTask* iTask, TMLLength& iMinLen, const TMLLength& iMaxLen, unsigned int iType);
	/*///Constructor
    	/**
      	\param iTask Pointer to the task the command belongs to
	\param iLength Virtual length of the command
	\param iMaxDelay Maximal delay for Execi Interval commands
    	*/
	//TMLExeciCommand(TMLTask* iTask, TMLLength& iMinLen, TMLLength& iMaxLen, unsigned int iType);
	void execute();
	TMLTask* getDependentTask() const;
	std::string toString();
	std::string toShortString();
	std::string getCommandStr();
protected:
	bool prepareNextTransaction();
	///Pointer to the variable maximal length
	//TMLLength* _pMaxLen;
	///Constant value of the maximal length
	//TMLLength _cMaxLen;
	///Pointer to the variable maximal length
	//TMLLength* _pMinLen;
	///Constant value of the maximal length
	//TMLLength _cMinLen;
	///Type of exec command (execi, execc)
	LengthFuncPointer _lengthFunc;
	unsigned int _type;
};

#endif
