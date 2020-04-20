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
    private static final int IMMEDIATE_NO = 0;
    private static final int IMMEDIATE_INT = 1;
    
    private AvatarExpressionSolver left, right;
    private char operator;
    private String expression;
    private boolean isLeaf; //variable
    private boolean isNot;
    private boolean isNegated;
    private int isImmediateValue; //0: No; 1: Boolean; 2: Int
    private int intValue;
    private AvatarExpressionAttribute leaf;
    
    
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
        
        removeUselessBrackets();
        
        if (!expression.matches("^.+[\\+\\-<>=&\\*/].*$")) {
            // leaf
            isLeaf = true;
            if (expression.equals("true")) {
                intValue = 1;
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else if (expression.equals("false")) {
                intValue = 0;
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else if (expression.matches("-?\\d+")) {
                intValue = Integer.parseInt(expression);
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else {
                leaf = new AvatarExpressionAttribute(spec, expression);
                returnVal = !leaf.hasError();
            }
            //System.out.println("Variable " + expression + "\n");
            return returnVal;
        }
        
        isLeaf = false;
        
        if (expression.startsWith("not(")) {
            //not bracket must be closed in the last char
            int closingIndex = getClosingBracket(4);
            
            if (closingIndex == -1) {
                return false;
            }
            if (closingIndex == expression.length() - 1) {
              //not(expression)
                isNot = true;
                expression = expression.substring(4, expression.length() - 1).trim();
            }
        }
        
        if (expression.startsWith("-(")) {
            //not bracket must be closed in the last char
            int closingIndex = getClosingBracket(4);
            
            if (closingIndex == -1) {
                return false;
            }
            if (closingIndex == expression.length() - 1) {
              //not(expression)
                isNot = true;
                expression = expression.substring(2, expression.length() - 1).trim();
            }
        }
        
        int index = getOperatorIndex();
        
        if (index == -1) {
            return false;
        }
        
        operator = expression.charAt(index);
        
        //split and recur
        String leftExpression = expression.substring(0, index).strip();
        String rightExpression = expression.substring(index + 1, expression.length()).strip();
        
        left = new AvatarExpressionSolver(leftExpression);
        right = new AvatarExpressionSolver(rightExpression);
        //System.out.println("Expression " + expression + " ; " + leftExpression + " ; " + rightExpression + "\n");  
        returnVal = left.buildExpression(spec);
        returnVal &= right.buildExpression(spec);   
        
        return returnVal;
    }
    
    public boolean buildExpression(AvatarBlock block) {
        boolean returnVal;
        
        removeUselessBrackets();

        if (!expression.matches("^.+[\\+\\-<>=&\\*/].*$")) {
            // leaf
            isLeaf = true;
            if (expression.equals("true")) {
                intValue = 1;
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else if (expression.equals("false")) {
                intValue = 0;
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else if (expression.matches("-?\\d+")) {
                intValue = Integer.parseInt(expression);
                isImmediateValue = IMMEDIATE_INT;
                returnVal = true;
            } else {
                leaf = new AvatarExpressionAttribute(block, expression);
                returnVal = !leaf.hasError();
            }
            //System.out.println("Variable " + expression + "\n");
            return returnVal;
        }
        
        isLeaf = false;
        
        if (expression.startsWith("not(")) {
            //not bracket must be closed in the last char
            int closingIndex = getClosingBracket(4);
            
            if (closingIndex == -1) {
                return false;
            }
            if (closingIndex == expression.length() - 1) {
              //not(expression)
                isNot = true;
                expression = expression.substring(4, expression.length() - 1).trim();
            }
        }
        
        if (expression.startsWith("-(")) {
            //not bracket must be closed in the last char
            int closingIndex = getClosingBracket(4);
            
            if (closingIndex == -1) {
                return false;
            }
            if (closingIndex == expression.length() - 1) {
              //not(expression)
                isNot = true;
                expression = expression.substring(2, expression.length() - 1).trim();
            }
        }
        
        int index = getOperatorIndex();
        
        if (index == -1) {
            return false;
        }
        
        operator = expression.charAt(index);
        
        //split and recur
        String leftExpression = expression.substring(0, index).strip();
        String rightExpression = expression.substring(index + 1, expression.length()).strip();
        
        left = new AvatarExpressionSolver(leftExpression);
        right = new AvatarExpressionSolver(rightExpression);
        //System.out.println("Expression " + expression + " ; " + leftExpression + " ; " + rightExpression + "\n");  
        returnVal = left.buildExpression(block);
        returnVal &= right.buildExpression(block);
        
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
    
    private int getOperatorIndex() {
        int index;
        // find the last executed operator
//        if (expression.matches("^.+[=\\$:;<>].*$")) {
//            // boolean operator found
//            if (expression.indexOf('=') != -1) {
//                return expression.indexOf('=');
//            } else if (expression.indexOf('$') != -1) {
//                return expression.indexOf('$');
//            } else if (expression.indexOf('<') != -1) {
//                return expression.indexOf('<');
//            } else if (expression.indexOf('>') != -1) {
//                return expression.indexOf('>');
//            } else if (expression.indexOf(':') != -1) {
//                return expression.indexOf(':');
//            } else {
//                return expression.indexOf(';');
//            }
//        } else {
            // search for middle operator
            int i, level, priority;
            boolean subVar = true; //when a subtraction is only one one variable
            char a;
            level = 0;
            priority = 0;
            for (i = 0, index = -1; i < expression.length(); i++) {
                a = expression.charAt(i);
                switch (a) {
                case '=':
                    if (level == 0) {
                        index = i;
                        priority = 2;
                    }
                    subVar = true;
                    break;
                case '$':
                    if (level == 0) {
                        index = i;
                        priority = 2;
                    }
                    subVar = true;
                    break;
                case '<':
                    if (level == 0) {
                        index = i;
                        priority = 2;
                    }
                    subVar = true;
                    break;
                case '>':
                    if (level == 0) {
                        index = i;
                        priority = 2;
                    }
                    subVar = true;
                    break;
                case ':':
                    if (level == 0) {
                        index = i;
                        priority = 2;
                    }
                    subVar = true;
                    break;
                case ';':
                    if (level == 0) {
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
                case '|':
                    if (level == 0 && priority < 2) {
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
                case '&':
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
//        }
        return index;
    }
    
    public int getResult() {
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                return 0;
            } else {
                return intValue;
            }
        }
        
        return getChildrenResult(left.getResult(), right.getResult());
    }
    
    public int getResult(SpecificationState ss) {
        int res;
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_INT) {
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
    
    public int getResult(SpecificationBlock sb) {
        int res;
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_INT) {
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
            result = ((leftV + rightV) > 0) ? 1 : 0;
        case '/':
            result = leftV / rightV;
            break;
        case '*':
            result = leftV * rightV;
            break;
        case '&':
            result = ((leftV + rightV - 2) == 0) ? 1 : 0;
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
        if (isLeaf) {
            if (isImmediateValue == IMMEDIATE_NO) {
                return leaf.toString();
            } else {
                return String.valueOf(intValue);
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
            return "(" + leftString + " " + opString + " " + rightString + ")";
        }
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

}
