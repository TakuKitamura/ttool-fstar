/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
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
 * Used verifying the syntax of a TML specification
 * Creation: 12/09/2007
 * @version 1.0 12/09/2007
 * @author Ludovic APVRILLE
 * @see
 */


package tmltranslator;

import java.io.*;
import java.util.*;
import compiler.tmlparser.*;
import myutil.*;


public class TMLSyntaxChecking {
    
	private final String WRONG_ORIGIN_CHANNEL = "is not declared as an origin channel of the task"; 
	private final String WRONG_DESTINATION_CHANNEL = "is not declared as a destination channel of the task"; 
	private final String WRONG_ORIGIN_EVENT = "is not declared as an origin event of the task"; 
	private final String WRONG_DESTINATION_EVENT = "is not declared as a destination event of the task";
	private final String WRONG_ORIGIN_REQUEST = "is not declared as an origin request of the task";
	private final String SYNTAX_ERROR = "syntax error";
	private final String VARIABLE_ERROR = "variable is not used according to its type";
	private final String UNDECLARED_VARIABLE = "unknown variable";
	private final String SYNTAX_ERROR_VARIABLE_EXPECTED = "syntax error (variable expected)";
	
	
	private ArrayList<TMLError> errors;
	private ArrayList<TMLError> warnings;
	private TMLModeling tmlm;
	private TMLMapping mapping;
  
    
    public TMLSyntaxChecking(TMLModeling _tmlm) {
        tmlm = _tmlm;
    }
	
	public TMLSyntaxChecking(TMLMapping _mapping) {
		mapping = _mapping;
		tmlm = mapping.getTMLModeling();
    }
	
	public void checkSyntax() {
		
		errors = new ArrayList<TMLError>();
		warnings = new ArrayList<TMLError>();
		
		//System.out.println("Checking syntax");
		
		checkReadAndWriteInChannelsEventsAndRequests();
		
		checkActionSyntax();
	}
	
	public int hasErrors() {
		if (errors  == null) {
			return 0;
		}
		return errors.size();
	}
	
	public int hasWarnings() {
		if (warnings  == null) {
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
	
	public void addError(TMLTask t, TMLActivityElement elt, String message, int type) {
		TMLError error = new TMLError(type);
		error.message = message;
		error.task = t;
		error.element = elt;
		errors.add(error);
	}
	
	public void checkReadAndWriteInChannelsEventsAndRequests() {
		TMLChannel ch;
		TMLEvent evt;
		TMLRequest request;
		
		for(TMLTask t: tmlm.getTasks()) {
			TMLActivity tactivity = t.getActivityDiagram();
			TMLActivityElement elt;
			int n = tactivity.nElements();
			for(int i=0; i<n; i++) {
				elt = tactivity.get(i);
				//System.out.println("Task= " + t.getName() + " element=" + elt);
				
				if (elt instanceof TMLWriteChannel) {
					ch = ((TMLWriteChannel)elt).getChannel();
					//System.out.println("Write channel");
					if (ch.getOriginTask() != t) {
						addError(t, elt, ch.getName() + ": " + WRONG_ORIGIN_CHANNEL, TMLError.ERROR_BEHAVIOR);
					}
				}
				
				if (elt instanceof TMLReadChannel) {
					ch = ((TMLReadChannel)elt).getChannel();
					//System.out.println("Write channel");
					if (ch.getDestinationTask() != t) {
						addError(t, elt, ch.getName() + ": " + WRONG_DESTINATION_CHANNEL, TMLError.ERROR_BEHAVIOR);
					}
				}
				
				if (elt instanceof TMLSendEvent) {
					evt = ((TMLSendEvent)elt).getEvent();
					//System.out.println("Write channel");
					if (evt.getOriginTask() != t) {
						addError(t, elt, evt.getName() + ": " + WRONG_ORIGIN_EVENT, TMLError.ERROR_BEHAVIOR);
					}
				}
				
				if (elt instanceof TMLWaitEvent) {
					evt = ((TMLWaitEvent)elt).getEvent();
					//System.out.println("Write channel");
					if (evt.getDestinationTask() != t) {
						addError(t, elt, evt.getName() + ": " + WRONG_DESTINATION_EVENT, TMLError.ERROR_BEHAVIOR);
					}
				}     
				
				if (elt instanceof TMLNotifiedEvent) {
					evt = ((TMLNotifiedEvent)elt).getEvent();
					//System.out.println("Write channel");
					if (evt.getDestinationTask() != t) {
						addError(t, elt, evt.getName() + ": " + WRONG_DESTINATION_EVENT, TMLError.ERROR_BEHAVIOR);
					}
				}
				
				if (elt instanceof TMLSendRequest) {
					request = ((TMLSendRequest)elt).getRequest();
					//System.out.println("Write channel");
					if (!request.isAnOriginTask(t)) {
						addError(t, elt, request.getName() + ": " + WRONG_ORIGIN_REQUEST, TMLError.ERROR_BEHAVIOR);
					}
				}
			}
		}
	}
	
	public void checkActionSyntax() {
		TMLWaitEvent tmlwe;
		TMLSendEvent tmlase;
		TMLSendRequest tmlsr;
		TMLChoice choice;
		TMLForLoop loop;
		TMLEvent evt;
		TMLRequest req;
		TMLType type;
		TMLRandom random;
		int j;
		int elseg, afterg;
		TMLAttribute attr;
		
		StringReader toParse;
		String action;
		
		
		for(TMLTask t: tmlm.getTasks()) {
			TMLActivity tactivity = t.getActivityDiagram();
			TMLActivityElement elt;
			int n = tactivity.nElements();
			//System.out.println("Task" + t.getName());
			for(int i=0; i<n; i++) {
				elt = tactivity.get(i);
				//System.out.println("elt=" + elt);
				if (elt instanceof TMLActionState) {
					action = ((TMLActivityElementWithAction)elt).getAction();
					parsingAssignment(t, elt, action);
					
				} else if (elt instanceof TMLActivityElementWithAction) {
					action = ((TMLActivityElementWithAction)elt).getAction();
					parsing(t, elt, "actionnat", action);
				
				} else if (elt instanceof TMLActivityElementWithIntervalAction) {
					action = ((TMLActivityElementWithIntervalAction)elt).getMinDelay();
					parsing(t, elt, "actionnat", action);
					action = ((TMLActivityElementWithIntervalAction)elt).getMaxDelay();
					parsing(t, elt, "actionnat", action);
					
				} else if (elt instanceof TMLActivityElementChannel) {
					action = ((TMLActivityElementChannel)elt).getNbOfSamples();
					parsing(t, elt, "actionnat", action);
					
				} else if (elt instanceof TMLSendEvent) {
					tmlase = (TMLSendEvent)elt;
					evt = tmlase.getEvent();
					for(j=0; j<tmlase.getNbOfParams(); j++) {
						action = tmlase.getParam(j);
						if ((action != null) && (action.length() > 0)){
							type = evt.getType(j);
							if ((type == null) || (type.getType() == TMLType.NATURAL)) {
								parsing(t, elt, "actionnat", action);
							} else {
								parsing(t, elt, "actionbool", action);
							}
						}
					}
					
				} else if (elt instanceof TMLWaitEvent) {
					tmlwe = (TMLWaitEvent)elt;
					evt = tmlwe.getEvent();
					for(j=0; j<tmlwe.getNbOfParams(); j++) {
						action = tmlwe.getParam(j).trim();
						if ((action != null) && (action.length() > 0)) {
							if (!(Conversion.isId(action))) {
								addError(t, elt, SYNTAX_ERROR_VARIABLE_EXPECTED + " in expression " + action, TMLError.ERROR_BEHAVIOR);
							} else {
								// Declared variable?
								attr = t.getAttributeByName(action);
								if (attr == null ) {
									addError(t, elt, UNDECLARED_VARIABLE + " :" + action + " in expression " + action, TMLError.ERROR_BEHAVIOR);
								} else {
									if (attr.getType().getType() != tmlwe.getEvent().getType(j).getType()) {
										addError(t, elt, VARIABLE_ERROR + " :" + action + " in expression " + action, TMLError.ERROR_BEHAVIOR);
									}
								}
							}
						}
					}
					
				} else if (elt instanceof TMLSendRequest) {
					tmlsr = (TMLSendRequest)elt;
					req = tmlsr.getRequest();
					for(j=0; j<tmlsr.getNbOfParams(); j++) {
						action = tmlsr.getParam(j);
						if ((action != null) && (action.length() > 0)){
							type = req.getType(j);
							if ((type == null) || (type.getType() == TMLType.NATURAL)) {
								parsing(t, elt, "actionnat", action);
							} else {
								parsing(t, elt, "actionbool", action);
							}
						}
					}
					
				} else if (elt instanceof TMLChoice) {
					choice = (TMLChoice)elt;
					elseg = choice.getElseGuard();
					afterg = choice.getAfterGuard();
					for(j=0; j<choice.getNbGuard(); j++) {
						if (!choice.isNonDeterministicGuard(j) && !choice.isStochasticGuard(j)) {
							if ((j!= elseg) && (j!=afterg)) {
								action = choice.getGuard(j);
								parsing(t, elt, "guard", action);
							}
						}
					}
					
				} else if (elt instanceof TMLForLoop) {
					loop = (TMLForLoop)elt;
					parsing(t, elt, "assnat", loop.getInit());
					parsing(t, elt, "actionbool", loop.getCondition());
					parsing(t, elt, "assnat", loop.getIncrement());
					
				} else if (elt instanceof TMLRandom) {
					random = (TMLRandom)elt;
					parsing(t, elt, "actionnat", random.getMinValue());
					parsing(t, elt, "actionnat", random.getMaxValue());
					parsing(t, elt, "natid", random.getVariable());
					parsing(t, elt, "natnumeral", ""+random.getFunctionId());
				}
			}
		}
	}
	
	public void parsingAssignment(TMLTask t, TMLActivityElement elt, String action) {
		int index = action.indexOf("=");
		
		if (index == -1) {
			addError(t, elt, SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		}
		
		String var = action.substring(0, index).trim();
		TMLAttribute attrFound = null;
		for(TMLAttribute attr: t.getAttributes()) {
			if (attr.getName().compareTo(var) == 0) {
				attrFound = attr;
				break;
			}
		}
		
		if (attrFound == null) {
			addError(t, elt, UNDECLARED_VARIABLE + " :" + var + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		}
		
		if (attrFound.isNat()) {
			parsing(t, elt, "assnat", action);
		} else {
			parsing(t, elt, "assbool", action);
		}
		
	}
	
	
	/**
	* Parsing in two steps:
	* 1. Parsing the expression with no varaible checking
	* 2. Parsing the expression with variables values to see whether variables are well-placed or not
	* The second parsing is performed iff the first one succeeds
	*/
	public void parsing(TMLTask t, TMLActivityElement elt, String parseCmd, String action) {
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
			addError(t, elt, SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		} catch (TokenMgrError tke ) {
			//System.out.println("TokenMgrError --------> Parse error in :" + parseCmd + " " + action);
			addError(t, elt, SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
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
		
		for(TMLAttribute attr: t.getAttributes()) {
			modif = tmlm.putAttributeValueInString(modif, attr);
		}
		parser = new TMLExprParser(new StringReader(parseCmd + " " + modif));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + modif);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
		} catch (ParseException e) {
			//System.out.println("ParseException --------> Parse error in :" + parseCmd + " " + action);
			addError(t, elt, VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		} catch (TokenMgrError tke ) {
			//System.out.println("TokenMgrError --------> Parse error in :" + parseCmd + " " + action);
			addError(t, elt, VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
			return;
		}  
		
		// Tree analysis: if the tree contains a variable, then, this variable has not been declared
		ArrayList<String> vars = root.getVariables();
		for(String s: vars) {
			addError(t, elt, UNDECLARED_VARIABLE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
		}
		
	}
	
	public String printSummary() {
		String ret = "";
		if (errors.size() == 0) {
			ret += printWarnings();
			ret += "Syntax checking: successful\n";
			ret += "No error, " + warnings.size() + " warning(s)\n";
		} else {
			ret += printErrors() + printWarnings();
			ret += "Syntax checking: failed\n";
			ret += errors.size() + " error(s), "+ warnings.size() + " warning(s)\n";	
		}
		
		return ret;
	}
	
	public String printErrors() {
		String ret = "";
		for(TMLError error: errors) {
			ret += "ERROR / task " + error.task.getName() + " / element " + error.element.getName() + ": " + error.message + "\n";
		}
		return ret;
	}
	
	public String printWarnings() {
		String ret = "";
		for(TMLError error: warnings) {
			ret += "ERROR / task " + error.task.getName() + " / element: " + error.element.getName() + ": " + error.message + "\n";
		}
		return ret;
	}
	
}