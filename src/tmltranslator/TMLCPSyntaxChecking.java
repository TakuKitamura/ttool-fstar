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
	
	
	private ArrayList<TMLCPError> errors;
	private ArrayList<TMLCPError> warnings;
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
		
		errors = new ArrayList<TMLCPError>();
		warnings = new ArrayList<TMLCPError>();
		
		//TraceManager.addDev( "Checking syntax" );
		//Call here the routines to performan syntax checks
		checkMainCP();
		checkActivityDiagrams();
		checkSequenceDiagrams();
	}

	private void checkMainCP()	{
		TMLCPActivityDiagram mainCP = tmlcp.getMainCP();
		ArrayList<String> listConnectorsStartEndNames = new ArrayList<String>();
		ArrayList<String> listDiagramNames = new ArrayList<String>();
		//check that all diagrams are connected

		ArrayList<TMLCPElement> listElements = mainCP.getElements();
		for( TMLCPElement elem : listElements )	{
			if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD )	{
				listDiagramNames.add(((tmltranslator.tmlcp.TMLCPRefAD)elem).getName() );
			}
			if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD )	{
				listDiagramNames.add(((tmltranslator.tmlcp.TMLCPRefSD)elem).getName() );
			}
			if( elem instanceof TMLCPConnector )	{
				listConnectorsStartEndNames.add( ((TMLCPConnector)elem).getEndName() );
				listConnectorsStartEndNames.add( ((TMLCPConnector)elem).getStartName() );
			}
		}

		for( String s: listDiagramNames )	{
			if( !listConnectorsStartEndNames.contains(s) )	{
				//TraceManager.addDev( "Diagram " + s + " in diagram " + mainCP.getName() " is not connected" );
				addError( "Diagram <<" + s + ">> in diagram <<" + mainCP.getName() + ">> is not connected", TMLCPError.ERROR_STRUCTURE );
			}
		}
	}

	private void checkActivityDiagrams()	{
		ArrayList<TMLCPActivityDiagram> listADs = tmlcp.getCPActivityDiagrams();

		ArrayList<String> listConnectorsStartEndNames = new ArrayList<String>();
		ArrayList<String> listDiagramNames = new ArrayList<String>();
		//check that all diagrams are connected

		for( TMLCPActivityDiagram diag: listADs )	{
			ArrayList<TMLCPElement> listElements = diag.getElements();
			for( TMLCPElement elem : listElements )	{
				if( elem instanceof tmltranslator.tmlcp.TMLCPRefAD )	{
					listDiagramNames.add(((tmltranslator.tmlcp.TMLCPRefAD)elem).getName() );
				}
				if( elem instanceof tmltranslator.tmlcp.TMLCPRefSD )	{
					listDiagramNames.add(((tmltranslator.tmlcp.TMLCPRefSD)elem).getName() );
				}
				if( elem instanceof TMLCPConnector )	{
					listConnectorsStartEndNames.add( ((TMLCPConnector)elem).getEndName() );
					listConnectorsStartEndNames.add( ((TMLCPConnector)elem).getStartName() );
				}
			}
			for( String s: listDiagramNames )	{
				if( !listConnectorsStartEndNames.contains(s) )	{
					//TraceManager.addDev( "Diagram " + s + " is not connected in diagram " + diag.getName() );
					addError( "Diagram <<" + s + ">> in diagram <<" + diag.getName() + ">> is not connected", TMLCPError.ERROR_STRUCTURE );
				}
			}
			listConnectorsStartEndNames = new ArrayList<String>();
			listDiagramNames = new ArrayList<String>();
		}
	}

	private void checkSequenceDiagrams()	{
		ArrayList<TMLCPSequenceDiagram> listSDs = tmlcp.getCPSequenceDiagrams();

		for( TMLCPSequenceDiagram diag: listSDs )	{
			ArrayList<TMLAttribute> attributes = diag.getAttributes();
			checkMessages( diag, attributes );	// check that variables have been declared
			checkActions( diag, attributes );			// actions must be done on variables that have
																						//	been declared and coherently boolean = boolean + 6 is not allowed
			checkInstances( diag );	// instances within the same SD must all have different names
		}
	}

	private void checkMessages( TMLCPSequenceDiagram diag, ArrayList<TMLAttribute> attributes )	{
		ArrayList<TMLSDMessage> messages = diag.getMessages();
		for( TMLSDMessage msg: messages )	{
			ArrayList<TMLSDAttribute> attributesMsg = msg.getAttributes();
			for( TMLSDAttribute attr: attributesMsg )	{
				if( !attributes.contains( attr ) )	{	//class TMLSDMessage must have the method equals defined
					//TraceManager.addDev( " Attribute " + attr.getName() + " does not exist in diagram " + diag.getName()  );
					addError( " Attribute <<" + attr.getName() + ">> has not been declared in diagram <<" + diag.getName() + ">>",
										TMLCPError.ERROR_STRUCTURE );
				}
			}
		}
	}

	private void checkActions( TMLCPSequenceDiagram diag, ArrayList<TMLAttribute> attributes )	{
		ArrayList<TMLSDAction> actions = diag.getActions();
		//boolean exists = false;
		for( TMLSDAction action: actions )	{
			String[] array = action.getAction().split("=");
			String temp = array[0].replaceAll("\\s+","");
			//TraceManager.addDev( "CHECKING ACTIONS: " + temp + " of length " + temp.length() );
			for( TMLAttribute attribute: attributes )	{
				//TraceManager.addDev( "PRINTING ATTRIBUTE NAMES: " + attribute.getName() );
				if( attribute.getName().equals( temp ) )	{	
					if( attribute.isBool() )	{
						parsing( "assnat", action.getAction() );
						//TraceManager.addDev( "Found that the action is on a boolean variable: " + temp );
						//exists = true;
						break;
					}
					if( attribute.isNat() )	{
						parsing( "assbool", action.getAction() );
						//TraceManager.addDev( "Found that the action is on a natural variable: " + temp );
						//exists = true;
						break;
					}
				}
			}
			/*if( !exists )	{
				TraceManager.addDev( "Error undeclared attribute in action " + action.getAction() + " in diagram " + diag.getName()  );
				addError( "Error undeclared attribute in action " + action.getAction() + " in diagram " + diag.getName(), TMLCPError.ERROR_STRUCTURE );
			}
			exists = false;*/
		}
	}

	private void checkInstances( TMLCPSequenceDiagram diag )	{
		ArrayList<TMLSDInstance> instances = diag.getInstances();
		HashSet hash = new HashSet();
		for( TMLSDInstance instance: instances )	{
			if( !hash.contains( instance.getName() ) )	{
				hash.add( instance.getName() );
			}
			else	{
				//TraceManager.addDev( "Error double instance name " + instance.getName() + " in diagram " + diag.getName()  );
				addError( "Instance <<" + instance.getName() + ">> is declared multiple times in diagram <<" + diag.getName() + ">>", TMLCPError.ERROR_STRUCTURE );
			}
		}
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
	
	public ArrayList<TMLCPError> getErrors() {
		return errors;
	}
	
	public ArrayList<TMLCPError> getWarnings() {
		return warnings;
	}

/*	public void addError( TMLTask t, TMLActivityElement elt, String message, int type )	{
		TMLError error = new TMLError( type );
		error.message = message;
		error.task = t;
		error.element = elt;
		errors.add( error );
	}*/

	public void addError( String message, int type )	{
		TMLCPError error = new TMLCPError( type );
		error.message = message;
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
		for( TMLCPError error: errors )	{
			ret += "ERROR: " + error.message;
		}
		return ret;
	}
	
	public String printWarnings() {
		String ret = "";
		for( TMLCPError error: warnings ) {
			ret += "ERROR / task " + error.task.getName() + " / element: " + error.element.getName() + ": " + error.message + "\n";
		}
		return ret;
	}

	public void parsing( String parseCmd, String action) {
		TMLExprParser parser;
		SimpleNode root;
		
		// First parsing
		parser = new TMLExprParser(new StringReader(parseCmd + " " + action));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + action);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
		} catch (ParseException e) {
			//System.out.println("ParseException --------> Parse error in :" + parseCmd + " " + action);
			addError( SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		} catch (TokenMgrError tke ) {
			//System.out.println("TokenMgrError --------> Parse error in :" + parseCmd + " " + action);
			addError( SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		}  
		
		// Second parsing
		// We only replace variables values after the "=" sign
		if (parseCmd.compareTo("natnumeral") == 0) {
			return;
		}
		
		int index = action.indexOf('=');
		String modif = action;
		
		if ((parseCmd.compareTo("assnat") ==0) || (parseCmd.compareTo("assbool") ==0)) { 
			if (index != -1) {
				modif = action.substring(index+1, action.length());
			}
			
			if (parseCmd.compareTo("assnat") ==0) {
				parseCmd = "actionnat";
			} else {
				parseCmd = "actionbool";
			}
		}
		
		if (parseCmd.compareTo("natid") == 0) {
			parseCmd = "natnumeral";
		}
		
		/*for(TMLAttribute attr: t.getAttributes()) {
			modif = tmlm.putAttributeValueInString(modif, attr);
		}*/
		parser = new TMLExprParser(new StringReader(parseCmd + " " + modif));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + modif);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
		} catch (ParseException e) {
			//System.out.println("ParseException --------> Parse error in :" + parseCmd + " " + action);
			addError( VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		} catch (TokenMgrError tke ) {
			//System.out.println("TokenMgrError --------> Parse error in :" + parseCmd + " " + action);
			addError( VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		}  
		
		// Tree analysis: if the tree contains a variable, then, this variable has not been declared
		ArrayList<String> vars = root.getVariables();
		TraceManager.addDev( "PRINTING VARS IN PARSING(): " + vars.toString() );
		for(String s: vars) {
			addError( UNDECLARED_VARIABLE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
		}
		
	}
}	//End of class
