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
 * Class TURTLEModelChecker
 * Creation: 09/12/2003
 * @version 1.0 09/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import ui.*;
import compiler.tmlparser.*;

import java.util.*;
import java.io.*;


public class TURTLEModelChecker {
    private TURTLEModeling tm;
    private Vector warnings;
    
    //private String SEPARATOR = ": ";
    //private String END = "\n";
    
    //private String RULE_CD_001 = "Only one association between two TClasses";
    //private String RULE_CD_002 = "Every association between two TClasses must be attributed with one and only one composition operator";
    //private String RULE_CD_003 = "Synchronization composition operator must be accompanied with an OCL formula";
    //private String RULE_CD_004 = "TClasses must have a unique name";
    
    //private String RULE_AD_000 = "All Tclasses must have an activity diagram";
    private String ERROR_AD_000 = "has no activity diagram";
    //private String RULE_AD_001 = "An activity diagram must have one start state, and only one";
    private String ERROR_AD_001a = "activity diagram has no start state";
    private String ERROR_AD_001b = " should be followed by another component";
    private String ERROR_AD_001c = " activity diagram is badly built";
    //private String RULE_AD_002 = "An activity diagram should terminate";
    //private String RULE_AD_003 = "All activities must be linked either to a loop or to a stop state";
    //private String RULE_AD_004 = "Parallel operator should have a valid list of synchronization gates";
    private String ERROR_AD_004 = "the list of gates is badly formatted";
    //private String RULE_AD_006 = "Parallel operator should no more than two nexts if they define synchronization gates";
    private String ERROR_AD_006 = "No more than two next activities are allowed after parallel with synchronization gates";
    //private String RULE_AD_005 = "Preferably, no recursive process should start after a parallel operator";
    private String WARNING_AD_001 = "A recursive process starts after a parallel operator";
    //private String RULE_AD_007 = "Synchronization gates of parallel operators should not be involved in external synchronization";
    private String ERROR_AD_007 = " gate is involved in both internal and external synchronization";
    //private String RULE_AD_008 = "From an internal synchronization, reaching another synchronization on the same gate is not allowed";
    private String ERROR_AD_008 = " gate is involved in several internal synchronizations";
    
	
    private String ERROR_AD_009_0 = " syntax error";
	private String ERROR_AD_009_1 = " variable is not correctly used";
	private String ERROR_AD_009_2 = " unknown variable";
	private String ERROR_AD_009_3 = " unknown gate";
	private String ERROR_AD_009_4 = " unknown param";
	private String ERROR_AD_009_5 = " null gate";
	private String ERROR_AD_009_6 = " null param";
	
	private CorrespondanceTGElement listE;
    
    
    public TURTLEModelChecker(TURTLEModeling _tm, CorrespondanceTGElement _listE) {
        tm = _tm;
		listE = _listE;
    }
	
	 public TURTLEModelChecker(TURTLEModeling _tm) {
        tm = _tm;
		listE = null;
    }
    
    public void setTURTLEModeling(TURTLEModeling _tm) {
        tm = _tm;
    }
    
    public Vector getWarnings() {
        return warnings;
    }
    
    public Vector syntaxAnalysisChecking() {
        //System.out.println("modelChecking");
        Vector errors = new Vector();
        warnings = new Vector();
        syntaxAnalysisCheckingCD(errors, warnings);
        TClass t;
        for(int i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            syntaxAnalysisCheckingAD(t, errors, warnings);
        }
		
		tm.removeUselessVariables(warnings);
		tm.removeUselessGates(warnings);
        return errors;
    }
    
    public void syntaxAnalysisCheckingCD(Vector errors, Vector warnings) {
    	checkRuleCD001(errors, warnings);
    }
    
    public void syntaxAnalysisCheckingAD(TClass t, Vector errors, Vector warnings) {
        //System.out.println("Checking activity diagram of " + t.getName());
        checkRuleAD000(t, errors, warnings);
        checkRuleAD001(t, errors, warnings);
        checkRuleAD004(t, errors, warnings);
        checkRuleAD005(t, errors, warnings);
        checkRuleAD006(t, errors, warnings);
        checkRuleAD007(t, errors, warnings);
        checkRuleAD008(t, errors, warnings);
		checkRuleAD009(t, errors, warnings);
    }
    
    
    private void checkRuleCD001(Vector errors, Vector warnings) {
        return;
    }
    
    private void checkRuleAD000(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        CheckingError error;
        
        if (ad == null) {
            error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName()+ ERROR_AD_000);
            error.setTClass(t);
            errors.add(error);
        }
        
        return;
    }
    
    // assumes a non-null activity diagram
    private void  checkRuleAD001(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        
        if (ad == null) {
            return;
        }
        
        CheckingError error;
        
        if (ad.getStartState() == null) {
            error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName()+ ERROR_AD_001a);
            error.setTClass(t);
            errors.add(error);
            return;
        }
        
        // put to false all components
        ad.setSelectedAll(false);
        
        analyzeAccessibility(t, ad.getStartState(), errors, warnings);
        
        ad.setSelectedAll(false);
    }
    
    private void analyzeAccessibility(TClass t, ADComponent ad, Vector errors, Vector warnings) {
        if (ad == null) {
			
            CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName()+ ERROR_AD_001c);
            error.setTClass(t);
            errors.add(error);
            return;
        }
        
        if (ad.isSelected()) {
            return;
        }
        
        ad.setSelected(true);
        
        if (ad.realNbOfNext() < ad.getMinNext()) {
            CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ad + ERROR_AD_001b);
            error.setTClass(t);
            errors.add(error);
            return;
        } else {
            //search for each next
            ADComponent ad1;
            for(int i=0; i<ad.realNbOfNext(); i++) {
                ad1 = (ADComponent)(ad.getNext(i));
                analyzeAccessibility(t, ad1, errors, warnings);
            }
        }
    }
    
    // Valid list of synchronization gates for parallel operators
    private void  checkRuleAD004(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        ADComponent ad1;
        ADParallel adp;
        
        if (ad == null) {
            return;
        }
        
        if (ad.getStartState() == null) {
            return;
        }
        
        for(int i=0; i<ad.size(); i++) {
            ad1 = (ADComponent)(ad.elementAt(i));
            if (ad1 instanceof ADParallel) {
                adp = (ADParallel) ad1;
                boolean b = adp.isAValidMotif(t);
                if (!b) {
                    CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName() + "/Parallel operator: " + ERROR_AD_004);
                    error.setTClass(t);
                    errors.add(error);
                }
            }
        }
    }
    
    // No recursive process after parallel operator
    private void  checkRuleAD005(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        if (ad == null) {
            CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName() + " has no activity diagram");
            error.setTClass(t);
            errors.add(error);
            return ;
        }
        ADComponent ad1;
        ADParallel adp;
        
        for(int i=0; i<ad.size(); i++) {
            ad1 = (ADComponent)(ad.elementAt(i));
            if (ad1 instanceof ADParallel) {
                adp = (ADParallel) ad1;
                if (ad.hasRecursivePath(adp)) {
                    CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName() + "/Parallel operator: " + WARNING_AD_001);
                    error.setTClass(t);
                    warnings.add(error);
                }
            }
        }
    }
    
    // no more than two nexts after parallel operators with synchronized gates
    private void  checkRuleAD006(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        ADComponent ad1;
        ADParallel adp;
        
        if (ad == null) {
            return;
        }
        
        if (ad.getStartState() == null) {
            return;
        }
        
        for(int i=0; i<ad.size(); i++) {
            ad1 = (ADComponent)(ad.elementAt(i));
            if (ad1 instanceof ADParallel) {
                adp = (ADParallel) ad1;
                if (adp.nbGate() > 0) {
                    if (adp.getNbNext() > 2) {
                        CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName() + "/Parallel operator: " + ERROR_AD_006);
                        error.setTClass(t);
                        errors.add(error);
                    }
                }
            }
        }
    }
    
    // Valid list of synchronization gates for parallel operators: gates should not be involved in external synchronization
    private void  checkRuleAD007(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        ADComponent ad1;
        ADParallel adp;
        int i, j;
        Gate g;

        if (ad == null) {
            return;
        }

        if (ad.getStartState() == null) {
            return;
        }

        for(i=0; i<ad.size(); i++) {
            ad1 = (ADComponent)(ad.elementAt(i));
            if (ad1 instanceof ADParallel) {
                adp = (ADParallel) ad1;
                boolean b = adp.isAValidMotif(t);
                if (b) {
                   for(j=0; j<adp.nbGate(); j++) {
                     //System.out.println("Getting gate #" + j + " of t " + t.getName());
                     g = adp.getGate(j);
                     if (tm.syncRelationWith(t, g) != null) {
                        CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName() + "/Parallel operator: " + g.getName() + ERROR_AD_007);
                        error.setTClass(t);
                        errors.add(error);
                     }
                   }
                }

            }
        }
    }
    
    private void  checkRuleAD008(TClass t, Vector errors, Vector warnings) {
        ActivityDiagram ad = t.getActivityDiagram();
        ADComponent ad1;
        ADParallel adp;
        int i, j;
        Gate g;

        if (ad == null) {
            return;
        }

        if (ad.getStartState() == null) {
            return;
        }

        for(i=0; i<ad.size(); i++) {
            ad1 = (ADComponent)(ad.elementAt(i));
            if (ad1 instanceof ADParallel) {
                adp = (ADParallel) ad1;
                boolean b = adp.isAValidMotif(t);
                if (b) {
                   for(j=0; j<adp.nbGate(); j++) {
                     //System.out.println("Getting gate #" + j + " of t " + t.getName());
                     g = adp.getGate(j);
                     if (tm.canReachSynchroOn(adp, g)) {
                        CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, t.getName() + "/Parallel operator: " + g.getName() + ERROR_AD_008);
                        error.setTClass(t);
                        errors.add(error);
                     }
                   }
                }

            }
        }
    }
	
	
	// Syntax error
	private void  checkRuleAD009(TClass t, Vector errors, Vector warnings) {
		ActivityDiagram ad = t.getActivityDiagram();
        ADComponent ad1;
		Param p;
		Gate g;
		String action;
		String s;
		int j;
		ADChoice choice;
		
		if (ad == null) {
			return;
		}
		
		for(int i=0; i<ad.size(); i++) {
            ad1 = (ADComponent)(ad.elementAt(i));
			
			if (ad1 instanceof ADActionStateWithGate) {
				g = ((ADActionStateWithGate)ad1).getGate();
				
				if (g == null) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_5 + " in an action state of tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
					return;
				}
				
				if (!t.getGateList().contains(g)) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, g.getName() + ": " + ERROR_AD_009_3 + " in tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
				}
				
				action = ((ADActionStateWithGate)ad1).getActionValue();
				if ((action!= null) && (action.length() > 0)) {
					parsing(t, ad1, "actiongate", action, errors);
				} else {
					//System.out.println("null action on gate=" + ((ADActionStateWithGate)ad1).getGate().getName() + action);
				}
				
			} else if (ad1 instanceof ADActionStateWithParam) {
				p = ((ADActionStateWithParam)ad1).getParam();
				
				if (p == null) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_6 + " in an action state of tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
					return;
				}
				
				if (!t.getParamList().contains(p)) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, p.getName() + ": " + ERROR_AD_009_3 + " in tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
				}
				
				action = ((ADActionStateWithParam)ad1).getActionValue();
				if (p.isNat()) {
					parsing(t, ad1, "assnat", p.getName() + " = " + action, errors);
				} else if (p.isBool()) {
					parsing(t, ad1, "assbool", p.getName() + " = " + action, errors);
				} else if (p.isQueueNat()) {
					parsing(t, ad1, "assqueuenat", p.getName() + " = " + action, errors);
				}
				
			} else if (ad1 instanceof ADChoice) {
				choice = (ADChoice)ad1;
				for(j=0; j<choice.getNbGuard(); j++) {
					if (choice.isGuarded(j)) {
						parsing(t, ad1, "guard", choice.getGuard(j), errors);
					}
				}
				
			} else if (ad1 instanceof ADDelay) {
				parsing(t, ad1, "actionnat", ((ADDelay)ad1).getValue(), errors);
				
			} else if (ad1 instanceof ADLatency) {
				parsing(t, ad1, "actionnat", ((ADLatency)ad1).getValue(), errors);
				
			} else if (ad1 instanceof ADTimeInterval) {
				parsing(t, ad1, "actionnat", ((ADTimeInterval)ad1).getMinValue(), errors);
				parsing(t, ad1, "actionnat", ((ADTimeInterval)ad1).getMaxValue(), errors);
				
			} else if (ad1 instanceof ADTLO) {
				g = ((ADTLO)ad1).getGate();
				
				if (g == null) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_5 + " in an action state of tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
					return;
				}
				
				if (!t.getGateList().contains(g)) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, g.getName() + ": " + ERROR_AD_009_3 + " in tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
				}
				
				action = ((ADTLO)ad1).getAction();
				s = TURTLEModeling.manageGateDataStructures(t, action);
				
				if (s == null) {
					CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_0 + " in expression " + action + " of tclass " + t.getName());
					error.setTClass(t);
					errors.add(error);
				}
				
				parsing(t, ad1, "actionnat", ((ADTLO)ad1).getDelay(), errors);
				parsing(t, ad1, "actionnat", ((ADTLO)ad1).getLatency(), errors);
				
			}
			
		}
		
	}
	
	/**
	* Parsing in two steps:
	* 1. Parsing the expression with no variable checking
	* 2. Parsing the expression with variables values to see whether variables are well-placed or not
	* The second parsing is performed iff the first one succeeds
	*/
	public void parsing(TClass t, ADComponent elt, String parseCmd, String action, Vector errors) {
		TMLExprParser parser;
		SimpleNode root;
		int i;
		
		// First parsing
		parser = new TMLExprParser(new StringReader(parseCmd + " " + action));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + action);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
		} catch (ParseException e) {
			System.out.println("\nParsing :" + parseCmd + " " + action);
			System.out.println("ParseException --------> Parse error in :" + parseCmd + " " + action);
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_0 + " in expression " + action + " of tclass " + t.getName());
            error.setTClass(t);
			putCorrespondance(error, elt);
            errors.add(error);
			return;
		} catch (TokenMgrError tke) {
			System.out.println("TokenMgrError --------> Parse error in :" + parseCmd + " " + action);
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_0 + " in expression " + action + " of tclass " + t.getName());
            error.setTClass(t);
			putCorrespondance(error, elt);
            errors.add(error);
			return;
		}  
		
		// Second parsing
		// We only replace variables values after the "=" sign
		int index = action.indexOf('=');
		String modif = action;
		
		if (parseCmd.startsWith("ass")) { 
			if (index != -1) {
				modif = action.substring(index+1, action.length());
			}
			
			parseCmd = "action" + parseCmd.substring(3, parseCmd.length()); 
		}
		
		for(i=0; i<t.paramNb(); i++) {
			modif = tm.putParamValueInString(modif, t.getParam(i));
		}
		
		if (parseCmd.compareTo("actiongate") ==0) {
			parseCmd = "actiongatevalue";
		}
		
		parser = new TMLExprParser(new StringReader(parseCmd + " " + modif));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + modif);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
		} catch (ParseException e) {
			System.out.println("\nParsing :" + parseCmd + " " + modif);
			System.out.println("\n(Original parsing :" + parseCmd + " " + action);
			System.out.println("ParseException --------> Parse error in :" + parseCmd + " " + action);
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_1 + " in expression " + action + " of tclass " + t.getName());
            error.setTClass(t);
            errors.add(error);
			putCorrespondance(error, elt);
			return;
		} catch (TokenMgrError tke ) {
			System.out.println("\nParsing :" + parseCmd + " " + modif);
			System.out.println("TokenMgrError --------> Parse error in :" + parseCmd + " " + action);
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, ERROR_AD_009_1 + " in expression " + action + " of tclass " + t.getName());
            error.setTClass(t);
			putCorrespondance(error, elt);
            errors.add(error);
			return;
		}  
		
		// Tree analysis: if the tree contains a variable, then, this variable has not been declared
		ArrayList<String> vars = root.getVariables();
		for(String s: vars) {
			// is that string a variable?
			if ((s.compareTo("true") != 0) && (s.compareTo("false") != 0) && (s.compareTo("nil") != 0)) {
				System.out.println("Variable not declared: " +s);
				CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, s + ": " + ERROR_AD_009_2 + " in expression " + action + " of tclass " + t.getName());
				error.setTClass(t);
				putCorrespondance(error, elt);
				errors.add(error);
			}
		}
		
	}
	
	public void putCorrespondance(CheckingError _error, ADComponent _elt) {
		if (listE == null) {
			return;
		}
		
		TGComponent tgc = listE.getTG(_elt);
		_error.setTGComponent(tgc);
		
	}
}
