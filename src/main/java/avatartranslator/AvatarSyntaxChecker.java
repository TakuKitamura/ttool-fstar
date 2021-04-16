/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package avatartranslator;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import compiler.tmlparser.ParseException;
import compiler.tmlparser.SimpleNode;
import compiler.tmlparser.TMLExprParser;
import compiler.tmlparser.TokenMgrError;
import myutil.BoolExpressionEvaluator;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.TraceManager;



/**
 * Class AvatarSyntaxChecker
 * Creation: 21/05/2010
 * @version 1.0 21/05/2010
 * @author Ludovic APVRILLE
 */
public class AvatarSyntaxChecker  {

    public AvatarSyntaxChecker() {
    }


    public static ArrayList<AvatarError> checkSyntax(AvatarSpecification avspec) {
        ArrayList<AvatarError> errors = new ArrayList<>();

        errors.addAll(checkSignalRelations(avspec));
        errors.addAll(checkASMLibraryFunctions(avspec));

        return errors;
    }



    public static ArrayList<AvatarError> checkASMLibraryFunctions(AvatarSpecification avspec) {
        ArrayList<AvatarError> errors = new ArrayList<>();

        for(AvatarLibraryFunction alf: avspec.getListOfLibraryFunctions()) {
            errors.addAll(checkASMLibraryFunction(avspec, alf));
        }

        return errors;
    }

    public static ArrayList<AvatarError> checkASMLibraryFunction(AvatarSpecification avspec, AvatarLibraryFunction alf) {
        ArrayList<AvatarError> errors = new ArrayList<>();

        AvatarStateMachine asm = alf.getStateMachine();
        for(AvatarStateMachineElement elt: asm.getListOfElements()) {
            if ( ! (elt instanceof AvatarStopState) ) {
                if (elt.getNexts().size() == 0) {
                    AvatarError error = new AvatarError(avspec);
                    error.firstAvatarElement = alf;
                    error.secondAvatarElement = elt;
                    error.error = 7;
                    errors.add(error);
                }
            }
        }
        return errors;
    }


    public static ArrayList<AvatarError> checkSignalRelations(AvatarSpecification avspec) {
        ArrayList<AvatarError> errors = new ArrayList<>();

        List<AvatarSignal> signals1, signals2;
        // Check relations are corrects
        for (AvatarRelation relation : avspec.getRelations()) {
            signals1 = relation.getSignals1();
            signals2 = relation.getSignals2();

            AvatarBlock block1, block2;
            block1 = relation.block1;
            block2 = relation.block2;

            if (block1 == null) {
                AvatarError error = new AvatarError(avspec);
                error.relation = relation;
                error.error = 4;
                errors.add(error);
            }

            if (block2 == null) {
                AvatarError error = new AvatarError(avspec);
                error.relation = relation;
                error.error = 5;
                errors.add(error);
            }

            if (signals1.size() != signals2.size()) {
                AvatarError error = new AvatarError(avspec);
                error.relation = relation;
                error.error = 0;
                errors.add(error);
            } else {
                // Compare signals characteristics
                AvatarSignal sig1, sig2;
                for(int i=0; i<signals1.size(); i++) {
                    sig1 = signals1.get(i);
                    sig2 = signals2.get(i);

                    // In vs out
                    if (sig1.isIn() && sig2.isIn()) {
                        AvatarError error = new AvatarError(avspec);
                        error.relation = relation;
                        error.firstAvatarElement = sig1;
                        error.secondAvatarElement = sig2;
                        error.error = 1;
                        errors.add(error);
                    } else if (sig1.isOut() && sig2.isOut()) {
                        AvatarError error = new AvatarError(avspec);
                        error.relation = relation;
                        error.firstAvatarElement = sig1;
                        error.secondAvatarElement = sig2;
                        error.error = 2;
                        errors.add(error);
                    }

                    // Attributes
                    //TraceManager.addDev("Checking attributes compatibility");
                    if (!(sig1.isCompatibleWith(sig2))) {
                        AvatarError error = new AvatarError(avspec);
                        error.relation = relation;
                        error.firstAvatarElement = sig1;
                        error.secondAvatarElement = sig2;
                        error.error = 3;
                        errors.add(error);
                    }

                    // Both signals exist in their respective block
                    if (block1 != null) {
                        AvatarSignal as = block1.getSignalByName(sig1.getSignalName());
                        if (as == null) {
                            AvatarError error = new AvatarError(avspec);
                            error.relation = relation;
                            error.block = block1;
                            error.firstAvatarElement = sig1;
                            error.error = 6;
                            errors.add(error);
                        }
                    }

                    if (block2 != null) {
                        AvatarSignal as = block2.getSignalByName(sig2.getSignalName());
                        if (as == null) {
                            AvatarError error = new AvatarError(avspec);
                            error.relation = relation;
                            error.block = block2;
                            error.firstAvatarElement = sig2;
                            error.error = 6;
                            errors.add(error);
                        }
                    }

                }
            }
        }


        return errors;
    }



    public static int isAValidGuard(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _guard) {
        //TraceManager.addDev("Evaluating (non modified) guard:" + _guard);

        String tmp = _guard.replaceAll(" ", "").trim();
        if (tmp.compareTo("[]") == 0) {
            return 0;
        }

        // NEW
        tmp = Conversion.replaceAllChar(tmp, '[', "").trim();
        tmp = Conversion.replaceAllChar(tmp, ']', "").trim();

        String act = tmp;

        for(AvatarAttribute aa: _ab.getAttributes()) {
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, aa.getName(), aa.getDefaultInitialValue());
        }

        AvatarExpressionSolver e1 = new AvatarExpressionSolver(act);

        if (e1.buildExpression()) {
            return 1;
        }

        return 0;


        /*BoolExpressionEvaluator bee = new BoolExpressionEvaluator();

        //TraceManager.addDev("Evaluating (modified) guard:" + act);
        boolean result = bee.getResultOfWithIntExpr(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error: " + bee.getError() + " result=" + result);
            return -1;
        }


        return 0;*/
        // END of NEW

        //return parse(_as, _ab, "guard", act);
    }

    /*
     * @return 0 if ok, -1 if failure
     */
    public static int isAValidIntExpr(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _expr) {

        /*AvatarExpressionSolver e1 = new AvatarExpressionSolver("x + y");
        e1.buildExpression(_ab);*/

        if (_expr.trim().length() == 0) {
            return 0;
        }

        String tmp = _expr.replaceAll(" ", "").trim();
        String act = tmp;

        for(AvatarAttribute aa: _ab.getAttributes()) {
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, aa.getName(), aa.getDefaultInitialValue());
        }

        AvatarExpressionSolver e1 = new AvatarExpressionSolver(act);
        boolean ret = e1.buildExpression();
        if (!ret) {
            return -1;
        }

        if (e1.getReturnType() == AvatarExpressionSolver.IMMEDIATE_INT) {
            return 0;
        }

        return -1;

        /*IntExpressionEvaluator iee = new IntExpressionEvaluator();

        //TraceManager.addDev("Evaluating int:" + act);
        double result = iee.getResultOf(act);
        if (iee.getError() != null) {
            //TraceManager.addDev("Error: " + iee.getError());
            return -1;
        }

        return 0;*/
        // OLD return parse(_as, _ab, "actionnat", _expr);

    }

    public static int isAValidProbabilityExpr(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _expr) {
        if (_expr.trim().length() == 0) {
            return 0;
        }

        String tmp = _expr.replaceAll(" ", "").trim();
        double prob = 0.5;

        try {
            prob = Double.parseDouble(tmp);
        } catch (Exception e) {
            return -1;
        }

        if ((prob <0) || (prob > 1000)) {
            return -1;
        }

        return 0;

    }

    public static int newIsAValidBoolExpr(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _expr) {
        if (_expr.trim().length() == 0) {
            return 0;
        }

        String tmp = _expr.replaceAll(" ", "").trim();
        String act = tmp;

        for(AvatarAttribute aa: _ab.getAttributes()) {
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, aa.getName(), aa.getDefaultInitialValueTF());
        }

        AvatarExpressionSolver e1 = new AvatarExpressionSolver(act);

        if (e1.buildExpression()) {
            return 1;
        }
        return 0;
    }


    public static int isAValidBoolExpr(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _expr) {
        if (_expr.trim().length() == 0) {
            return 0;
        }

        String tmp = _expr.replaceAll(" ", "").trim();
        String act = tmp;


        //TraceManager.addDev("1. IsValidBoolExpr Evaluating bool:" + act);

        for(AvatarAttribute aa: _ab.getAttributes()) {
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, aa.getName(), aa.getDefaultInitialValue());
        }

        //TraceManager.addDev("2. IsValidBoolExpr Evaluating bool:" + act);


        /*BoolExpressionEvaluator bee = new BoolExpressionEvaluator();


        boolean result = bee.getResultOfWithIntExpr(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error in bool expr: " + bee.getError());
            return -1;
        } else {
            //TraceManager.addDev("IsValidBoolExpr: YES (" + act + ")");
        }*/

        // Testing with parsing AvatarExpressionSolver

        //TraceManager.addDev("3. Now with avatar expression solver:" + _expr);

        AvatarExpressionSolver aee = new AvatarExpressionSolver(act);
        if ( !(aee.buildExpression())) {
            TraceManager.addDev("4. Error with avatar expression solver:" + act);
            return -1;
        }

        //TraceManager.addDev("4. Ok with avatar expression solver:" + act);

        return 0;
        // OLD return parse(_as, _ab, "actionbool", _expr);
    }

    public static int isAValidVariableExpr(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _expr) {
        int index0 = _expr.indexOf("=");
        if (index0 == -1) {
            return -1;
        }

        String attributeName = _expr.substring(0, index0).trim();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName(attributeName);
        if (aa == null) {
            return -1;
        }

        String action = _expr.substring(index0 + 1,  _expr.length()).trim();

        if (aa.isInt()) {
            //TraceManager.addDev("Testing action+" + action);
            return isAValidIntExpr(_as, _ab, action);
            //return parse(_as, _ab, "actionnat", action);
        } else if (aa.isBool()) {
            return isAValidBoolExpr(_as, _ab, action);
            //return parse(_as, _ab, "actionbool", action);
        } 
        return -1;
    }

    /**
     * Parsing in two steps:
     * 1. Parsing the expression with no variable checking
     * 2. Parsing the expression with variables values to see whether variables are well-placed or not
     * The second parsing is performed iff the first one succeeds
     * @param _as       : Unused
     * @param _ab       : Avatar block
     * @param _parseCmd : Parse Command
     * @param _action   : Action
     * @return :    return -1 in case of error in first pass
     *              return -2 in case of error in second pass
     *              return -3 in case a variable has not been declared
     */
    public static int parse(AvatarSpecification _as, AvatarStateMachineOwner _ab, String _parseCmd, String _action) {
        TMLExprParser parser;
        SimpleNode root;
        int i;

        // First parsing
        parser = new TMLExprParser(new StringReader(_parseCmd + " " + _action));
        try {
            //
            root = parser.CompilationUnit();
            //root.dump("pref=");
            //
        } catch (ParseException e) {
            TraceManager.addDev("\nAvatar Parsing :" + _parseCmd + " " + _action);
            TraceManager.addDev("ParseException --------> Parse error in :" + _parseCmd + " " + _action);
            return -1;
        } catch (TokenMgrError tke) {
            TraceManager.addDev("Avatar TokenMgrError --------> Parse error in :" + _parseCmd + " " + _action);
            return -1;
        }  

        // Second parsing
        // We only replace variables values after the "=" sign
        int index = _action.indexOf('=');
        String modif = _action;

        if (_parseCmd.startsWith("ass")) { 
            if (index != -1) {
                modif = _action.substring(index+1, _action.length());
            }

            _parseCmd = "action" + _parseCmd.substring(3, _parseCmd.length()); 
        }

        /*for(i=0; i<_ab.attributeNb(); i++) {
            modif = AvatarSpecification.putAttributeValueInString(modif, _ab.getAttribute(i));
        }*/

        parser = new TMLExprParser(new StringReader(_parseCmd + " " + modif));
        try {
            //
            root = parser.CompilationUnit();
            //root.dump("pref=");
            //
        } catch (ParseException e) {
            TraceManager.addDev("\nAvatar Parsing :" + _parseCmd + " " + modif);
            TraceManager.addDev("\n(Original parsing :" + _parseCmd + " " + _action + ")");
            TraceManager.addDev("ParseException --------> Parse error in :" + _parseCmd + " " + _action);
            return -2;
        } catch (TokenMgrError tke ) {
            TraceManager.addDev("\nnAvatar Parsing :" + _parseCmd + " " + modif);
            TraceManager.addDev("TokenMgrError --------> Parse error in :" + _parseCmd + " " + _action);
            return -2;
        }  

        // Tree analysis: if the tree contains a variable, then, this variable has not been declared
        List<String> vars = root.getVariables();
        for(String s: vars) {
            // is that string a variable?
            if ((s.compareTo("true") != 0) && (s.compareTo("false") != 0) && (s.compareTo("nil") != 0)) {
                TraceManager.addDev("Variable not declared: " +s);
                return -3;
            }
        }

        return 0;

    }


    // Searches for numerical values over max
    // @return list of AvatarElement not respecting this interval
    public static ArrayList<AvatarElement> useOfInvalidUPPAALNumericalValues(AvatarSpecification _as, int maxV) {
        ArrayList<AvatarElement> invalids = new ArrayList<AvatarElement>();

        for(AvatarBlock ab: _as.getListOfBlocks()) {
            // Check for attribute initial value
            invalids.addAll(ab.getAttributesOverMax(maxV));

            // Check operators of State machine
            invalids.addAll(ab.getStateMachine().elementsWithNumericalValueOver(maxV));
        }


        return invalids;
    }

}
