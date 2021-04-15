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

import java.util.HashMap;
import java.util.Map;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

/**
 * Class AvatarExpressionSolver
 * Avatar Expression Solver
 * Creation: 17/04/2020
 *
 * @author Alessandro TEMPIA CALVINO
 * @version 1.0 17/04/2020
 */
public class AvatarExpressionSolver {
    protected static final int IMMEDIATE_NO = 0;
    protected static final int IMMEDIATE_INT = 1;
    protected static final int IMMEDIATE_BOOL = 2;

    
    private AvatarExpressionSolver left, right;
    private char operator;
    private String expression;
    private boolean isLeaf; //variable
    private boolean isNot;
    private boolean isNegated;
    private int isImmediateValue; //0: No; 1: Int; 2: Boolean;
    private int intValue;
    private AvatarExpressionAttribute leaf;
    
    private static Map<AvatarElement, AvatarExpressionAttribute> attributesMap;
    
    
    public AvatarExpressionSolver() {
        left = null;
        right = null;
        isLeaf = true;
        isImmediateValue = IMMEDIATE_NO;
        intValue = 0;
    }
    
    public AvatarExpressionSolver(String expression) {
        this.expression = expression.trim();
        replaceOperators();
        left = null;
        right = null;
        isLeaf = true;
        isImmediateValue = IMMEDIATE_NO;
        intValue = 0;
        isNot = false;
        isNegated = false;
    }
    
    
    public void setExpression(String expression) {
        this.expression = expression;
        replaceOperators();
    }
    
    public boolean buildExpression(AvatarSpecification spec) {
        boolean returnVal;
        
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, AvatarExpressionAttribute>();
        }
        returnVal = buildExpressionRec(spec);

        if (!returnVal) {
            return false;
        }

        return checkIntegrity();
    }
    
    public boolean buildExpression(AvatarBlock block) {
        boolean returnVal;
        
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, AvatarExpressionAttribute>();
        }
        returnVal = buildExpressionRec(block); 
        if (!returnVal) {
            return false;
        }
        
        return checkIntegrity();
    }
    
    public boolean buildExpression() {
        boolean returnVal;
        
        returnVal = buildExpressionRec(); 
        if (!returnVal) {
            return false;
        }
        
        return checkIntegrity();
    }
    
    public boolean buildExpression(AvatarExpressionAttribute attribute) {
        this.expression = attribute.toString();
        isLeaf = true;
        isImmediateValue = IMMEDIATE_NO;
        leaf = attribute;
        return true;
    }
    
    
    public boolean buildExpressionRec(AvatarSpecification spec) {
        boolean returnVal;
        
        removeUselessBrackets();
        
        if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
            // leaf
            isLeaf = true;
            checkNot();
            checkNegated();
            checkNegatedNoBrackets();
            if (expression.equals("true")) {
                intValue = 1;
                isImmediateValue = IMMEDIATE_BOOL;
                returnVal = true;
            } else if (expression.equals("false")) {
                intValue = 0;
                isImmediateValue = IMMEDIATE_BOOL;
                returnVal = true;
            } else if (expression.matches("-?\\d+")) {
                intValue = Integer.parseInt(expression);
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else {
                AvatarElement ae = AvatarExpressionAttribute.getElement(expression, spec);
                if (ae != null && attributesMap.containsKey(ae)) {
                    leaf = attributesMap.get(ae);
                    leaf.setBlockIndex(spec); //might be uninitialized
                    returnVal = true;
                } else {
                    leaf = new AvatarExpressionAttribute(spec, expression);
                    returnVal = !leaf.hasError();
                    if (leaf.isConstant()) {
                        AvatarAttribute attr = leaf.getConstAttribute();
                        intValue = attr.getInitialValueInInt();
                        isImmediateValue = attr.isBool() ? IMMEDIATE_BOOL : IMMEDIATE_INT;
                        leaf = null;
                    } else if (returnVal == true) {
                        attributesMap.put(ae, leaf);
                    }
                }
            }
            //System.out.println("Variable " + expression + "\n");
            return returnVal;
        }
        
        isLeaf = false;
        
        returnVal = checkNot();
        returnVal &= checkNegated();
        
        if (!returnVal) {
            return false;
        }
        
        int index = getOperatorIndex();
        
        if (index == -1) {
            return false;
        }
        
        operator = expression.charAt(index);
        
        //split and recur
        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();
        
        left = new AvatarExpressionSolver(leftExpression);
        right = new AvatarExpressionSolver(rightExpression);
        //System.out.println("Expression " + expression + " ; " + leftExpression + " ; " + rightExpression + "\n");  
        returnVal = left.buildExpressionRec(spec);
        returnVal &= right.buildExpressionRec(spec);   
        
        return returnVal;
    }
    
    public boolean buildExpressionRec(AvatarBlock block) {
        boolean returnVal;
        
        removeUselessBrackets();

        if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
            // leaf
            isLeaf = true;
            checkNot();
            checkNegated();
            checkNegatedNoBrackets();
            if (expression.equals("true")) {
                intValue = 1;
                isImmediateValue = IMMEDIATE_BOOL;
                returnVal = true;
            } else if (expression.equals("false")) {
                intValue = 0;
                isImmediateValue = IMMEDIATE_BOOL;
                returnVal = true;
            } else if (expression.matches("-?\\d+")) {
                intValue = Integer.parseInt(expression);
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else {
                AvatarElement ae = AvatarExpressionAttribute.getElement(expression, block);
                if (ae != null && attributesMap.containsKey(ae)) {
                    leaf = attributesMap.get(ae);
                    returnVal = true;
                } else {
                    leaf = new AvatarExpressionAttribute(block, expression);
                    returnVal = !leaf.hasError();
                    if (leaf.isConstant()) {
                        AvatarAttribute attr = leaf.getConstAttribute();
                        intValue = attr.getInitialValueInInt();
                        isImmediateValue = attr.isBool() ? IMMEDIATE_BOOL : IMMEDIATE_INT;
                        leaf = null;
                    } else if (returnVal == true) {
                        attributesMap.put(ae, leaf);
                    }
                }
            }
            //System.out.println("Variable " + expression + "\n");
            return returnVal;
        }
        
        isLeaf = false;
        
        returnVal = checkNot();
        returnVal &= checkNegated();
        
        if (!returnVal) {
            return false;
        }
        
        int index = getOperatorIndex();
        
        if (index == -1) {
            return false;
        }
        
        operator = expression.charAt(index);
        
        //split and recur
        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();
        
        left = new AvatarExpressionSolver(leftExpression);
        right = new AvatarExpressionSolver(rightExpression);
        //System.out.println("Expression " + expression + " ; " + leftExpression + " ; " + rightExpression + "\n");  
        returnVal = left.buildExpressionRec(block);
        returnVal &= right.buildExpressionRec(block);
        
        return returnVal;
    }
    
    
    public boolean buildExpressionRec() {
        boolean returnVal;
        
        removeUselessBrackets();

        if (!expression.matches("^.+[\\+\\-<>=:;\\$&\\|\\*/].*$")) {
            // leaf
            isLeaf = true;
            checkNot();
            checkNegated();
            checkNegatedNoBrackets();
            if (expression.equals("true")) {
                intValue = 1;
                isImmediateValue = IMMEDIATE_BOOL;
                returnVal = true;
            } else if (expression.equals("false")) {
                intValue = 0;
                isImmediateValue = IMMEDIATE_BOOL;
                returnVal = true;
            } else if (expression.matches("-?\\d+")) {
                intValue = Integer.parseInt(expression);
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else {
                returnVal = false;
            }
            //System.out.println("Variable " + expression + "\n");
            return returnVal;
        }
        
        isLeaf = false;
        
        returnVal = checkNot();
        returnVal &= checkNegated();
        
        if (!returnVal) {
            return false;
        }
        
        int index = getOperatorIndex();
        
        if (index == -1) {
            return false;
        }
        
        operator = expression.charAt(index);
        
        //split and recur
        String leftExpression = expression.substring(0, index).trim();
        String rightExpression = expression.substring(index + 1, expression.length()).trim();
        
        left = new AvatarExpressionSolver(leftExpression);
        right = new AvatarExpressionSolver(rightExpression);
        //System.out.println("Expression " + expression + " ; " + leftExpression + " ; " + rightExpression + "\n");  
        returnVal = left.buildExpressionRec();
        returnVal &= right.buildExpressionRec();
        
        return returnVal;
    }
    
    private void replaceOperators() {
        expression = expression.replaceAll("\\|\\|", "\\|").trim();
        expression = expression.replaceAll("&&", "&").trim();
        expression = expression.replaceAll("==", "=").trim();
        expression = expression.replaceAll("!=", "\\$").trim();
        expression = expression.replaceAll(">=", ":").trim();
        expression = expression.replaceAll("<=", ";").trim(); 
        expression = expression.replaceAll("\\bor\\b", "\\|").trim();
        expression = expression.replaceAll("\\band\\b", "&").trim();
        //expression.replaceAll("\\btrue\\b", "t").trim();
        //expression.replaceAll("\\bfalse\\b", "f").trim(); 
    }
    
    private boolean checkNot() {
        boolean notStart1, notStart2;
        
        notStart1 = expression.startsWith("not(");
        notStart2 = expression.startsWith("!(");
        
        while (notStart1 || notStart2) {
            if (notStart1) {
                //not bracket must be closed in the last char
                int closingIndex = getClosingBracket(4);
                
                if (closingIndex == -1) {
                    return false;
                }
                if (closingIndex == expression.length() - 1) {
                  //not(expression)
                    isNot = !isNot;
                    expression = expression.substring(4, expression.length() - 1).trim();
                } else {
                    return true;
                }
            } else if (notStart2) {
                int closingIndex = getClosingBracket(2);
                
                if (closingIndex == -1) {
                    return false;
                }
                if (closingIndex == expression.length() - 1) {
                    //not(expression)
                    isNot = !isNot;
                    expression = expression.substring(2, expression.length() - 1).trim();
                } else {
                    return true;
                }
            }
            notStart1 = expression.startsWith("not(");
            notStart2 = expression.startsWith("!(");
        }
        return true;
    }
    
    private boolean checkNegated() {
        while (expression.startsWith("-(")) {
            //not bracket must be closed in the last char
            int closingIndex = getClosingBracket(2);
            
            if (closingIndex == -1) {
                return false;
            }
            if (closingIndex == expression.length() - 1) {
              //-(expression)
                isNegated = !isNegated;
                expression = expression.substring(2, expression.length() - 1).trim();
            } else {
                return true;
            }
        }
        return true;
    }
    
    private boolean checkNegatedNoBrackets() {
        if (expression.startsWith("-")) {     
            isNegated = true;
            expression = expression.substring(1, expression.length()).trim();
        }
        return true;
    }
    
    private int getOperatorIndex() {
        int index;
        // find the last executed operator
        int i, level, priority;
        boolean subVar = true; //when a subtraction is only one one variable
        char a;
        level = 0;
        priority = 0;
        for (i = 0, index = -1; i < expression.length(); i++) {
            a = expression.charAt(i);
            switch (a) {
            case '|':
                if (level == 0) {
                    index = i;
                    priority = 5;
                }
                break;
            case '&':
                if (level == 0 && priority < 5) {
                    index = i;
                    priority = 4;
                }
                break;
            case '=':
                if (level == 0 && priority < 4) {
                    index = i;
                    priority = 3;
                }
                subVar = true;
                break;
            case '$':
                if (level == 0 && priority < 4) {
                    index = i;
                    priority = 3;
                }
                subVar = true;
                break;
            case '<':
                if (level == 0 && priority < 3) {
                    index = i;
                    priority = 2;
                }
                subVar = true;
                break;
            case '>':
                if (level == 0 && priority < 3) {
                    index = i;
                    priority = 2;
                }
                subVar = true;
                break;
            case ':':
                if (level == 0 && priority < 3) {
                    index = i;
                    priority = 2;
                }
                subVar = true;
                break;
            case ';':
                if (level == 0 && priority < 3) {
                    index = i;
                    priority = 2;
                }
                subVar = true;
                break;
            case '-':
                if (level == 0 && !subVar && priority < 2) {
                    index = i;
                    priority = 1;
                }
                break;
            case '+':
                if (level == 0 && !subVar && priority < 2) {
                    index = i;
                    priority = 1;
                }
                break;
            case '/':
                if (level == 0  && priority == 0) {
                    index = i;
                }
                break;
            case '*':
                if (level == 0  && priority == 0) {
                    index = i;
                }
                break;
            case '(':
                level++;
                subVar = true;
                break;
            case ')':
                level--;
                subVar = false;
                break;
            case ' ':
                break;
            default:
                subVar = false;
                break;
            }
        }
        return index;
    }
    
    public int getResult() {
        int res;
        if (isLeaf) {
            if (isImmediateValue != IMMEDIATE_NO) {
                res = intValue;
            } else {
                return 0;
            }
        } else {
            res = getChildrenResult(left.getResult(), right.getResult());
        }
        
        if (isNot) {
            res = (res == 0) ? 1 : 0;
        } else if (isNegated) {
            res = -res;
        }       
        return res;
    }
    
    public int getResult(SpecificationState ss) {
        int res;
        if (isLeaf) {
            if (isImmediateValue != IMMEDIATE_NO) {
                res = intValue;
            } else {
                res = leaf.getValue(ss);
            }
        } else {
            res = getChildrenResult(left.getResult(ss), right.getResult(ss));
        }
        
        if (isNot) {
            res = (res == 0) ? 1 : 0;
        } else if (isNegated) {
            res = -res;
        }
        return res;
    }
    
    public int getResult(SpecificationState ss, AvatarStateMachineElement asme) {
        int res;
        if (isLeaf) {
            if (isImmediateValue != IMMEDIATE_NO) {
                res = intValue;
            } else {
                res = leaf.getValue(ss, asme);
            }
        } else {
            res = getChildrenResult(left.getResult(ss, asme), right.getResult(ss, asme));
        }
        
        if (isNot) {
            res = (res == 0) ? 1 : 0;
        } else if (isNegated) {
            res = -res;
        }
        return res;
    }
    
    public int getResult(SpecificationBlock sb) {
        int res;
        if (isLeaf) {
            if (isImmediateValue != IMMEDIATE_NO) {
                res = intValue;
            } else {
                res = leaf.getValue(sb);
            }
        } else {
            res = getChildrenResult(left.getResult(sb), right.getResult(sb));
        }
        
        if (isNot) {
            res = (res == 0) ? 1 : 0;
        } else if (isNegated) {
            res = -res;
        }
        return res;
    }
    
    public int getResult(int[] attributesValues) {
        int res;
        if (isLeaf) {
            if (isImmediateValue != IMMEDIATE_NO) {
                res = intValue;
            } else {
                res = leaf.getValue(attributesValues);
            }
        } else {
            res = getChildrenResult(left.getResult(attributesValues), right.getResult(attributesValues));
        }
        
        if (isNot) {
            res = (res == 0) ? 1 : 0;
        } else if (isNegated) {
            res = -res;
        }
        return res;
    }
    
    private int getChildrenResult(int leftV, int rightV) {
        int result;
        
        switch (operator) {
        case '=':
            result = (leftV == rightV) ? 1 : 0;
            break;
        case '$':
            result = (leftV != rightV) ? 1 : 0;
            break;
        case '<':
            result = (leftV < rightV) ? 1 : 0;
            break;
        case '>':
            result = (leftV > rightV) ? 1 : 0;
            break;
        case ':':
            result = (leftV >= rightV) ? 1 : 0;
            break;
        case ';':
            result = (leftV <= rightV) ? 1 : 0;
            break;
        case '-':
            result = leftV - rightV;
            break;
        case '+':
            result = leftV + rightV;
            break;
        case '|':
            result = (leftV == 0 && rightV == 0) ? 0 : 1;
            break;
        case '/':
            result = leftV / rightV;
            break;
        case '*':
            result = leftV * rightV;
            break;
        case '&':
            result = (leftV == 0 || rightV == 0) ? 0 : 1;
            break;
        default:
            //System.out.println("Error in EquationSolver::getResult");
            result = 0;
            break;
        }
        //System.out.println(leftV + " " + operator + " " + rightV + " = " + result);
        return result;
    }
    
    public String toString() {
        String retS;
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                retS = leaf.toString();
            } else if (isImmediateValue == IMMEDIATE_INT){
                retS = String.valueOf(intValue);
            } else {
                if (intValue == 0) {
                    retS = "false";
                } else {
                    retS = "true";
                }
            }
            if (isNegated) {
                retS = "-(" + retS + ")";
            }
            if (isNot) {
                retS = "not(" + retS + ")";
            }
        } else {
            String leftString = left.toString();
            String rightString = right.toString();
            String opString;
            switch (operator) {
            case '=':
                opString = "==";
                break;
            case '$':
                opString = "!=";
                break;
            case ':':
                opString = ">=";
                break;
            case ';':
                opString = "<=";
                break;
            case '|':
                opString = "||";
                break;
            case '&':
                opString = "&&";
                break;
            default:
                opString = "" + operator;
                break;
            }
            retS = "(" + leftString + " " + opString + " " + rightString + ")";
            if (isNegated) {
                retS = "-" + retS;
            }
            if (isNot) {
                retS = "not" + retS;
            }
        }
        return retS;
    }
    
    public boolean hasState() {
        boolean hasState;
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                return leaf.isState();
            } else {
                return false;
            }
        } else {
            hasState = left.hasState();
            hasState |= right.hasState();
            return hasState;
        }
    }
    
    public void linkStates() {
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                leaf.linkState();
            }
        } else {
            left.linkStates();
            right.linkStates();
        }
    }
    
    public void setBlockIndex(AvatarSpecification spec) {
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                leaf.setBlockIndex(spec);
            }
        } else {
            left.setBlockIndex(spec);
            right.setBlockIndex(spec);
        }
    }
    
    
    public static boolean containsElementAttribute(AvatarElement ae) {
        if (attributesMap != null) {
            return attributesMap.containsKey(ae);
        } else {
            return false;
        }
    }


    public static AvatarExpressionAttribute getElementAttribute(AvatarElement ae) {
        if (attributesMap != null) {
            return attributesMap.get(ae);
        } else {
            return null;
        }
    }
    

    public static void addElementAttribute(AvatarElement ae, AvatarExpressionAttribute aexa) {
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, AvatarExpressionAttribute>();
        }
        attributesMap.put(ae, aexa);
    }
    

    public static void emptyAttributesMap() {
        attributesMap = new HashMap<AvatarElement, AvatarExpressionAttribute>();
    }


    private boolean checkIntegrity() {
        int optype, optypel, optyper;
        boolean returnVal;
        
        if (isLeaf) {
            if (isNot) {
                return getReturnType() == IMMEDIATE_BOOL;
            } else if (isNegated) {
                return getReturnType() == IMMEDIATE_INT;
            } else {
                return true;
            }
        }
        
        optype = getType();
        optypel = left.getReturnType();
        optyper = right.getReturnType();
                
        switch(optype) {
        case IMMEDIATE_NO:
            returnVal = false; //Error
            break;
        case IMMEDIATE_INT:
            returnVal = (optypel == IMMEDIATE_INT && optyper == IMMEDIATE_INT) ? true : false;
            break;
        case IMMEDIATE_BOOL:
            returnVal = (optypel == IMMEDIATE_BOOL && optyper == IMMEDIATE_BOOL) ? true : false;
            break;
        case 3:
            returnVal = (optypel == optyper) ? true : false;
            break;
        default:
            returnVal = false;
        }
        
        if (returnVal == false) {
            return false;
        }
        
        returnVal = left.checkIntegrity();
        returnVal &= right.checkIntegrity();

        return returnVal;
    }
    
    private void removeUselessBrackets() {
        while (expression.startsWith("(") && expression.endsWith(")")) {
            if (getClosingBracket(1) == expression.length() - 1) {
                expression = expression.substring(1, expression.length() - 1).trim();
            } else {
                break;
            }
        }
    }
    
    private int getClosingBracket(int startChar) {
        int level = 0;
        char a;
        for (int i = startChar; i < expression.length(); i++) {
            a = expression.charAt(i);
            if (a == ')') {
                if (level == 0) {
                    return i;
                } else {
                    level--;
                }
            } else if (a == '(') {
                level++;
            }
        }
        return -1;
    }
    
    private int getType() {
        int optype;
        
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                return leaf.getAttributeType();
            } else {
                return isImmediateValue;
            }      
        }
        
        switch (operator) {
        case '=':
        case '$':
            optype = 3; //BOTH sides must have the same type
            break;
        case '<':
        case '>':
        case ':':
        case ';':
        case '-':
        case '+':
        case '/':
        case '*':
            optype = IMMEDIATE_INT;
            break;
        case '|':
        case '&':
            optype = IMMEDIATE_BOOL;
            break;
        default:
            optype = IMMEDIATE_NO; //ERROR
            break;
        }
        
        return optype;
    }
    
    public int getReturnType() {
        int optype;
        
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                return leaf.getAttributeType();
            } else {
                return isImmediateValue;
            }      
        }
        
        switch (operator) {
        case '-':
        case '+':
        case '/':
        case '*':
            optype = IMMEDIATE_INT;
            break;
        case '|':
        case '&':
        case '=':
        case '$':
        case '<':
        case '>':
        case ':':
        case ';':
            optype = IMMEDIATE_BOOL;
            break;
        default:
            optype = IMMEDIATE_NO; //ERROR
            break;
        }
        
        return optype;
    }

    public static int indexOfVariable(String expr, String variable) {
        int index;
        String tmp = expr;
        int removed = 0;
        //System.out.println("\nHandling expr: " + expr);

        while ((index = tmp.indexOf(variable)) > -1) {
            char c1, c2;
            if (index > 0) {
                c1 = tmp.charAt(index - 1);
            } else {
                c1 = ' ';
            }

            if (index+variable.length() < tmp.length())
                c2 = tmp.charAt(index+variable.length());
            else
                c2 = ' ';

            //System.out.println("tmp=" + tmp + " c1=" + c1 + " c2=" + c2);

            if (!(Character.isLetterOrDigit(c1) || (c1 == '_'))) {
                if (!(Character.isLetterOrDigit(c2) || (c2 == '_'))) {
                    //System.out.println("Found at index=" + index + " returnedIndex=" + (index+removed));
                    return index + removed;
                }
            }
            tmp = tmp.substring(index+variable.length(), tmp.length());
            //System.out.println("tmp=" + tmp);
            removed = index+variable.length();
            if (tmp.length() == 0) {
                return -1;
            }
            // We cut until we find a non alphanumerical character
            while( Character.isLetterOrDigit(tmp.charAt(0)) || (tmp.charAt(0) == '_')) {
                tmp = tmp.substring(1, tmp.length());
                if (tmp.length() == 0) {
                    return -1;
                }
                removed ++;
            }
            //System.out.println("after remove: tmp=" + tmp);

        }
        return -1;
    }

    public static String replaceVariable(String expr, String oldVariable, String newVariable) {
        if (oldVariable.compareTo(newVariable) == 0) {
            return expr;
        }
        int index;
        String tmp = expr;

        while ((index = indexOfVariable(tmp, oldVariable)) > -1) {
            String tmp1 = "";
            if (index > 0) {
                tmp1 = tmp.substring(0, index);
            }
            tmp1 += newVariable;
            tmp1 += tmp.substring(index+oldVariable.length(), tmp.length());
            tmp = tmp1;
        }

        return tmp;
    }

}
