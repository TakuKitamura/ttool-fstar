/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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
 * /**
 * Class TMLSyntaxChecking
 * Used verifying the syntax of the TML specification for a Communication Pattern
 * Creation: 05/09/2014
 * @version 1.0 05/09/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @see
 */


package tmltranslator;

import java.io.*;
import java.util.*;
import compiler.tmlparser.*;
import myutil.*;
import tmltranslator.*;
import tmltranslator.tmlcp.*;


public class TMLCPSyntaxChecking {
    
	private final String WRONG_ORIGIN_CHANNEL = "is not declared as an origin channel of the task"; 
	private final String WRONG_DESTINATION_CHANNEL = "is not declared as a destination channel of the task"; 
	private final String WRONG_ORIGIN_EVENT = "is not declared as an origin event of the task"; 
	private final String WRONG_DESTINATION_EVENT = "is not declared as a destination event of the task";
	private final String WRONG_ORIGIN_REQUEST = "is not declared as an origin request of the task";
	private final String SYNTAX_ERROR = "syntax error";
	private final String WRONG_VARIABLE_IDENTIFIER = "forbidden variable's name";
	private final String VARIABLE_ERROR = "variable is not used according to its type";
	private final String UNDECLARED_VARIABLE = "unknown variable";
	private final String SYNTAX_ERROR_VARIABLE_EXPECTED = "syntax error (variable expected)";
	private final String TIME_UNIT_ERROR = "unknown time unit";
	
	
	private ArrayList<TMLError> errors;
	private ArrayList<TMLError> warnings;
	private TMLCP tmlcp;
	private TMLMapping mapping;
  
    
	public TMLCPSyntaxChecking( TMLCP _tmlcp )	{
		tmlcp = _tmlcp;
	}
	
	/*public TMLCPSyntaxChecking(TMLMapping _mapping) {
		mapping = _mapping;
		tmlm = mapping.getTMLModeling();
    }*/
	
	public void checkSyntax() {
		
		errors = new ArrayList<TMLError>();
		warnings = new ArrayList<TMLError>();
		
		//TraceManager.addDev( "Checking syntax" );
		//Call here the routines to performan syntax checks
		checkMainCP();
		checkActivityDiagrams();
		checkSequenceDiagrams();
	}

	private void checkMainCP()	{
		TMLCPActivityDiagram mainCP = tmlcp.getMainCP();
	}

	private void checkActivityDiagrams()	{
		ArrayList<TMLCPActivityDiagram> listADs = tmlcp.getCPActivityDiagrams();
	}

	private void checkSequenceDiagrams()	{
		ArrayList<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();
		checkVariables( listSDs );
		/*checkMessages( listSDs);
		checkActions( listSDs );
		checkInstances(listSDs );*/
	}

	private void	checkVariables( ArrayList<TMLCPSequenceDiagram> listSDs )	{
	}
		
	public int hasErrors()	{
		if( errors  == null )	{
			return 0;
		}
		return errors.size();
	}
	
	public int hasWarnings() {
		if( warnings  == null ) {
			return 0;
		}
		return warnings.size();
	}
	
	public ArrayList<TMLError> getErrors() {
		return errors;
	}
	
	public ArrayList<TMLError> getWarnings() {
		return warnings;
	}

	public void addError( TMLTask t, TMLActivityElement elt, String message, int type )	{
		TMLError error = new TMLError( type );
		error.message = message;
		error.task = t;
		error.element = elt;
		errors.add( error );
	}

	public String printSummary() {
		String ret = "";
		if( errors.size() == 0 ) {
			ret += printWarnings();
			ret += "Syntax checking: successful\n";
			ret += "No error, " + warnings.size() + " warning(s)\n";
		}
		else {
			ret += printErrors() + printWarnings();
			ret += "Syntax checking: failed\n";
			ret += errors.size() + " error(s), "+ warnings.size() + " warning(s)\n";	
		}
		return ret;
	}
	
	public String printErrors() {
		String ret = "*** ERRORS:";
		for( TMLError error: errors )	{
			ret += "ERROR / task " + error.task.getName() + " / element " + error.element.getName() + ": " + error.message + "\n";
		}
		return ret;
	}
	
	public String printWarnings() {
		String ret = "";
		for( TMLError error: warnings ) {
			ret += "ERROR / task " + error.task.getName() + " / element: " + error.element.getName() + ": " + error.message + "\n";
		}
		return ret;
	}
}	//End of class
