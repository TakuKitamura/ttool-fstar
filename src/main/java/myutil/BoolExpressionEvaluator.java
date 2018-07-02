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


package myutil;

import java.util.StringTokenizer;

/**
 * Class BoolExpressionEvaluator
 * Creation: 13/12/2010
 * Version 2.0 13/12/2010
 *
 * @author Ludovic APVRILLE
 */
public class BoolExpressionEvaluator {

    private class IntBoolRes {
        public static final int AVAILABLE = -1;
        public static final int INT_TERM = 0;
        public static final int BOOL_TERM = 1;
        public static final int FAIL = 2;
        public static final int INT_BINARY_OP = 3;
        public static final int BOOL_BINARY_OP = 4;
        public static final int BOOL_UNARY_OP = 5;
        public static final int OPEN_PARENTHESIS = 6;
        public static final int UNKNOWN_TERM = 7;

        public int id = (int) (Math.ceil(Math.random() * 10000000));
        public int i = -18;
        public boolean b;
        public int res; //-1: available, 0:int, 2:bool, 3:ko, others : OPs
        public int op = 0;
        public String symb;

        public IntBoolRes left;
        public IntBoolRes right;
        public IntBoolRes father;


        public IntBoolRes() {
            res = AVAILABLE;
        }


        public IntBoolRes(int _type, int _op, IntBoolRes _father) {
            res = _type;
            op = _op;
            father = _father;
        }

        public IntBoolRes(int _val, IntBoolRes _father) {
            i = _val;
            res = INT_TERM;
            father = _father;
        }

        public IntBoolRes(boolean _val, IntBoolRes _father) {
            b = _val;
            res = BOOL_TERM;
            father = _father;
        }

        public IntBoolRes(String val, IntBoolRes _father) {
            symb = val;
            res = UNKNOWN_TERM;
            father = _father;
        }

        public IntBoolRes getTop() {
            if (father == null) {
                return this;
            } else return father.getTop();
        }

        public IntBoolRes addTerminalInt(int _value) {
            if (isFull()) {
                return null;
            }

            IntBoolRes news = new IntBoolRes(_value, this);
            if (left == null) {
                TraceManager.addDev("Adding on the left:" + _value);
                left = news;
            } else {
                TraceManager.addDev("Adding on the right:" + _value);
                right = news;
            }
            return news;

        }

        public IntBoolRes addTerminalBool(boolean _value) {
            if (isFull()) {
                return null;
            }

            IntBoolRes news = new IntBoolRes(_value, this);
            if (left == null) {
                left = news;
            } else {
                right = news;
            }
            return news;
        }

        public IntBoolRes addTerminalUnknown(String val) {
            if (isFull()) {
                return null;
            }

            IntBoolRes news = new IntBoolRes(val, this);
            if (left == null) {
                left = news;
            } else {
                right = news;
            }
            return news;
        }


        public IntBoolRes addOpenParenthesis() {
            if ((left != null) && (right != null)) {
                return null;
            }

            if ((left != null) && (op == 0)) {
                return null;
            }

            IntBoolRes newE = new IntBoolRes(OPEN_PARENTHESIS, OPEN_PAR_TOKEN, this);

            if (left == null) {
                left = newE;
            } else {
                right = newE;
            }


            IntBoolRes topPar = new IntBoolRes();
            topPar.father = newE;
            newE.left = topPar;

            return topPar;

        }


        public IntBoolRes addIntOperator(int _op) {
            // Must have at least one right operator
            TraceManager.addDev("Add int op");
            if (left == null) {
                TraceManager.addDev("No left terminal!");
                return null;
            }

            if (right != null) {
                // Must change the tree structure according to the operator priority
                IntBoolRes newE = new IntBoolRes(INT_BINARY_OP, _op, this);
                return addBinaryChangeTreeStruct(newE);
                /*newE.left = right;
                newE.father = right.father;
                right.father = newE;
                this.right = newE;
                return newE;*/
            }


            // Element added at the root of the current
            // If the current has not type ..
            if (!isAvailable()) {
                return null;
            }

            res = INT_BINARY_OP;
            op = _op;
            return this;
        }

        public IntBoolRes addUnaryOperator(int _op) {
            if ((left != null) && (right != null)) {
                return null;
            }

            if ((left != null) && (op == 0)) {
                return null;
            }

            IntBoolRes newE = new IntBoolRes(BOOL_UNARY_OP, _op, this);

            if (left == null) {
                left = newE;
            } else {
                right = newE;
            }


            IntBoolRes topPar = new IntBoolRes();
            topPar.father = newE;
            newE.left = topPar;

            return topPar;
        }

        public IntBoolRes addBinaryOperator(int _op) {
            // Must have at least one right operator
            TraceManager.addDev("Add binary op");
            if (left == null) {
                TraceManager.addDev("No left terminal!");
                return null;
            }

            if (right != null) {
                // Must change the tree structure accoding to the operator priority
                IntBoolRes newE = new IntBoolRes(BOOL_BINARY_OP, _op, this);
                return addBinaryChangeTreeStruct(newE);

            }

            // Element added at the root of the current
            // If the current has no type ..
            if (!isAvailable()) {
                return null;
            }

            res = BOOL_BINARY_OP;
            op = _op;
            return this;
        }

        public IntBoolRes addBinaryChangeTreeStruct(IntBoolRes newE) {

            // Look for the father
            if (isABinaryOperator() && (newE.getPriority() >= getPriority())) {
                newE.left = right;
                right.father = newE;
                this.right = newE;
                return newE;
            } else {
                // We must find the father where to add the operator
                // We thus look for the first father with no binary operator
                // or with a binary operator that has a higher priority
                TraceManager.addDev("----- Must find a target");
                IntBoolRes targetF = this.father;

                boolean go = true;
                while (go == true) {
                    TraceManager.addDev("in loop targetF=" + targetF);
                    if (targetF == null) {
                        go = false;
                    } else {
                        if (!(targetF.isABinaryOperator())) {
                            go = false;
                        } else if (targetF.hasAHigherPriorityThan(newE)) {
                            IntBoolRes nexT = targetF.father;
                            if (nexT == targetF) {
                                go = false;
                            }
                            targetF = nexT;
                        } else {
                            go = false;
                        }
                    }
                }

                TraceManager.addDev("**************** Considering targetF=" + targetF);

                if (targetF == null) {
                    newE.left = top;
                    top.father = newE;
                    newE.father = null;
                    top = newE;
                    return top;
                } else {
                    if (targetF.isABinaryOperator()) {
                        newE.right = targetF.left;
                        targetF.left = newE;
                        newE.father = targetF.father;
                        targetF.father = newE;
                        return newE;
                    } else {
                        TraceManager.addDev("Unaryoperator");
                        newE.left = targetF.left;
                        targetF.left = newE;
                        newE.father = targetF;
                        return newE;
                    }
                }
            }
        }


        public boolean isAvailable() {
            return res == AVAILABLE;
        }

        public boolean isFull() {
            return ((left != null) && (right != null));
        }

        public boolean isTop() {
            return father == null;
        }

        public boolean isTerminal() {
            return (res == INT_TERM) || (res == BOOL_TERM);
        }

        public boolean isRight() {
            if (father != null) {
                return father.right == this;
            }
            return false;
        }

        public boolean isLeft() {
            if (father != null) {
                return father.left == this;
            }
            return false;
        }

        public boolean isABinaryOperator() {
            return (res == INT_BINARY_OP) || (res == BOOL_BINARY_OP);
        }

        public boolean hasAHigherPriorityThan(IntBoolRes _other) {
            return (getPriority() > _other.getPriority());
        }

        public int getPriority() {
            if (res == BOOL_BINARY_OP) {
                return 1;
            }

            if (res == INT_BINARY_OP) {
                if ((op == PLUS_TOKEN) || (op == MINUS_TOKEN))
                    return 2;
                else {
                    TraceManager.addDev("HAVE PRIORITY 3");
                    return 3;
                }
            }

            return 0;
        }


        public String getValueString() {
            if (res == 0) {
                return "" + i;
            }
            return "" + b;
        }

        public Object getObjectValue() {
            if (res == INT_TERM) {
                return new Integer(i);
            }
            if (res == BOOL_TERM) {
                return new Boolean(b);
            }
            return null;

        }

        private int analysisArg(Object ob1) {
            if (ob1 instanceof Integer) {
                return ((Integer) ob1).intValue();
            } else {
                if (((Boolean) ob1).booleanValue()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        private Boolean makeUnaryOp(int op, int elt1) {
            if (op == NOT_TOKEN) {
                if (elt1 == 0) {
                    return new Boolean(true);
                } else
                    return new Boolean(false);
            }
            return null;
        }


        private Boolean makeBinaryOp(int op, int elt1, int elt2) {
            if (op == EQUAL_TOKEN) {
                return new Boolean(elt1 == elt2);
            }

            if (op == NOT_EQUAL_TOKEN) {
                return new Boolean(elt1 != elt2);
            }

            if (op == OR_TOKEN) {
                return new Boolean((elt1 != 0) || (elt2 != 0));
            }

            if (op == AND_TOKEN) {
                return new Boolean((elt1 != 0) && (elt2 != 0));
            }


            return null;
        }

        private Integer makeIntegerOp(int op, int elt1, int elt2) {
            if (op == PLUS_TOKEN) {
                return new Integer(elt1 + elt2);
            }

            if (op == MINUS_TOKEN) {
                return new Integer(elt1 - elt2);
            }

            if (op == MULT_TOKEN) {
                return new Integer(elt1 * elt2);
            }

            if (op == DIV_TOKEN) {
                //TraceManager.addDev("Div token .. elt1 = " + elt1 + " elt2 = " + elt2 + " res=" +  new Integer(elt1 / elt2).intValue());
                return new Integer(elt1 / elt2);
            }

            return null;
        }

        private Boolean makeIntegerToBooleanOp(int op, int elt1, int elt2) {

            if (op == LT_TOKEN) {
                return new Boolean(elt1 < elt2);
            }

            if (op == GT_TOKEN) {
                return new Boolean(elt1 > elt2);
            }

            if (op == LTEQ_TOKEN) {
                return new Boolean(elt1 <= elt2);
            }

            if (op == GTEQ_TOKEN) {
                return new Boolean(elt1 >= elt2);
            }

            return null;
        }


        public Object computeValue() {
            if (isTerminal()) {
                return getObjectValue();
            }

            if (res == BOOL_UNARY_OP) {
                if (left == null) {
                    errorMessage = "Badly formatted unary boolean operator";
                    return null;
                }
                Object ob1 = left.computeValue();
                if (!(ob1 instanceof Boolean)) {
                    errorMessage = "Bad operand for  unary boolean operator";
                    return null;
                }
                int elt1 = analysisArg(ob1);
                Boolean result = makeUnaryOp(op, elt1);
                TraceManager.addDev("Result unary=" + result);
                return result;
            }

            if (res == BOOL_BINARY_OP) {
                if ((right == null) || (left == null)) {
                    errorMessage = "Badly formatted binary boolean operator";
                    return null;
                }
                Object ob1 = right.computeValue();
                Object ob2 = left.computeValue();
                if ((ob1 == null) || (ob2 == null))
                    return null;
                if (((ob1 instanceof Integer) && (ob2 instanceof Integer)) || ((ob1 instanceof Boolean) && (ob2 instanceof Boolean))) {
                    int elt1 = analysisArg(ob1);
                    int elt2 = analysisArg(ob2);

                    Boolean result = makeBinaryOp(op, elt1, elt2);
                    TraceManager.addDev("Result binary=" + result);
                    return result;
                }
            }

            if (res == INT_BINARY_OP) {
                TraceManager.addDev("Found binary int expr");
                if ((right == null) || (left == null)) {
                    errorMessage = "Badly formatted binary int operator";
                    TraceManager.addDev("Found binary int expr in null");
                    return null;
                }
                Object ob1 = left.computeValue();
                Object ob2 = right.computeValue();
                if ((ob1 == null) || (ob2 == null)) {
                    TraceManager.addDev("Found binary int expr in null elt");
                    return null;
                }
                if ((ob1 instanceof Integer) && (ob2 instanceof Integer)) {
                    int elt1 = analysisArg(ob1);
                    int elt2 = analysisArg(ob2);

                    if (isIntToBooleanOperator(op)) {
                        Boolean resB = makeIntegerToBooleanOp(op, elt1, elt2);
                        return resB;
                    }

                    Integer result = makeIntegerOp(op, elt1, elt2);
                    TraceManager.addDev("Result int=" + result);
                    return result;
                } else {
                    errorMessage = "Invalid operands in integer operations";
                    return null;
                }
            }


            if (res == OPEN_PARENTHESIS) {
                if (left == null) {
                    return null;
                }
                return left.computeValue();
            }

            if (res == AVAILABLE) {
                if (left == null) {
                    return null;
                }
                return left.computeValue();
            }

            errorMessage = "Badly formatted expression from:" + this;

            return null;
        }


        public String toString() {
            return toString(0);
        }

        public String toString(int dec) {
            String s = "\n" + newLine(dec);
            if (isRight()) {
                s = s + "R->";
            }
            if (isLeft()) {
                s = s + "L->";
            }
            s += id;
            if (father == null) {
                s += " father= no";
            } else {
                s += " father=" + id;
            }
            s += " type:" + res + " op:" + toStringAction(op) + " int:" + i + " bool:" + b;

            if (left != null) {
                s += left.toString(dec + 1);
            }
            if (right != null) {
                s += right.toString(dec + 1);
            }
            return s;
        }

        private String newLine(int dec) {
            String s = "";
            for (int i = 0; i < dec; i++) {
                s += "\t";
            }
            return s;
        }

    }

    //  ----------------------------------------

    public static final String TRUE = "t";
    public static final String FALSE = "f";

    public static final int TRUE_VALUE = 1;
    public static final int FALSE_VALUE = 0;


    public static final int NUMBER_TOKEN = -1;
    public static final int BOOL_TOKEN = -2;
    public static final int EQUAL_TOKEN = -3;
    public static final int NOT_EQUAL_TOKEN = -15;
    public static final int NOT_TOKEN = -6;
    public static final int OR_TOKEN = -7;
    public static final int AND_TOKEN = -8;
    public static final int LT_TOKEN = -4;
    public static final int GT_TOKEN = -5;
    public static final int LTEQ_TOKEN = -9;
    public static final int GTEQ_TOKEN = -10;
    public static final int EOLN_TOKEN = -11;
    public static final int OPEN_PAR_TOKEN = -12;
    public static final int CLOSE_PAR_TOKEN = -13;
    public static final int WHITE_SPACE_TOKEN = -14;
    public static final int PLUS_TOKEN = -19;
    public static final int MINUS_TOKEN = -16;
    public static final int DIV_TOKEN = -17;
    public static final int MULT_TOKEN = -18;

    public static final String[] VAL_S = {"true", "false", "nb", "bool", "==", "<", ">", "not", "or", "and", "=<", ">=", "eol", "(", ")", " ", "!=", "-", "/", "*", "+"};

    public static final boolean isIntToBooleanOperator(int op) {
        return ((op == LT_TOKEN) || (op == GT_TOKEN) || (op == LTEQ_TOKEN) || (op == GTEQ_TOKEN));
    }

    public static String toStringAction(int val) {
        if (val >= 0) {
            return VAL_S[val];
        }

        return VAL_S[Math.abs(val) + 1];
    }


    public static int ID = 0;

    // PARSING_STAGE
    public static final int BEGIN_EXPR = 1;

    private StringTokenizer tokens;
    private String errorMessage = null;

    private int currentType;
    private int currentValue;

    private int nbOpen;

    private IntBoolRes top; // top of tree
    private boolean supportUnknownTerminal = false;


    public BoolExpressionEvaluator() {
    }

    public void setSupportUnknownTerminal(boolean support) {
        supportUnknownTerminal = support;
    }


    public String getError() {
        if (errorMessage == null) {
            return null;
        }
        int index = errorMessage.indexOf("/");
        if (index == -1) {
            return errorMessage;
        }

        return errorMessage.substring(index + 1, errorMessage.length());
    }

    public String getFullError() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null;
    }

    public boolean hasFinished() {
        return currentType == EOLN_TOKEN;
    }

    public boolean getResultOf(String _expr) {
        //TraceManager.addDev("Evaluating bool expr: " + _expr);

        String origin = _expr;
        _expr = Conversion.replaceAllString(_expr, "not", "!").trim();

        nbOpen = 0;

        String tmp = Conversion.replaceAllString(_expr, "==", "$").trim();
        tmp = Conversion.replaceAllString(tmp, "!=", "$").trim();
        tmp = Conversion.replaceAllString(tmp, ">=", ":").trim();
        tmp = Conversion.replaceAllString(tmp, "<=", ";").trim();
        if (tmp.indexOf("=") > -1) {
            TraceManager.addDev("Not a bool");
            errorMessage = "Not a boolean expression because it contains \"=\" operators";
            return false;
        }

        _expr = Conversion.replaceAllString(_expr, "true", "t").trim();
        _expr = Conversion.replaceAllString(_expr, "false", "f").trim();
        _expr = Conversion.replaceAllString(_expr, "||", "|").trim();
        _expr = Conversion.replaceAllString(_expr, "&&", "&").trim();
        _expr = Conversion.replaceAllString(_expr, "or", "|").trim();
        _expr = Conversion.replaceAllString(_expr, "and", "&").trim();
        _expr = Conversion.replaceAllString(_expr, "==", "=").trim();
        _expr = Conversion.replaceAllString(_expr, "!=", "$").trim();
        _expr = Conversion.replaceAllString(_expr, ">=", ":").trim();
        _expr = Conversion.replaceAllString(_expr, "<=", ";").trim();

        // For not() -> must find the closing bracket

        int index;
        int indexPar;

        while ((index = _expr.indexOf("not(")) != -1) {
            indexPar = Conversion.findMatchingParenthesis(_expr, index + 3, '(', ')');
            if (indexPar == -1) {
                errorMessage = "Parenthesis not maching at index " + (index + 3) + " in expression: " + _expr;
                return false;
            }

            _expr = _expr.substring(0, index) + "(!" + _expr.substring(index + 3, indexPar) + ")" + _expr.substring(indexPar, _expr.length());
        }


        //TraceManager.addDev("Computing:" + _expr);

        tokens = new java.util.StringTokenizer(_expr, " \t\n\r!$=&|<>():;tf", true);

        //TraceManager.addDev("Evaluating bool bool bool expr: " + _expr);

        int result = parseRootExpr1();

        if (getError() != null) {
            TraceManager.addDev("Error: " + getError() + " in expr=" + origin + " modified=" + _expr);
        }

        if (result == TRUE_VALUE) {
            //TraceManager.addDev("equal true");
            return true;
        }

        if (result == FALSE_VALUE) {
            //  TraceManager.addDev("equal false");
            return false;
        }

        /*computeNextToken();
          int result =  (int)(parseExpression());

          if (errorMessage != null) {
          TraceManager.addDev("Error:" + errorMessage);
          }

          if ((errorMessage == null) && (nbOpen!=0)) {
          errorMessage = "Badly placed parenthesis";
          result = -1;
          }

          if (result == TRUE_VALUE) {
          TraceManager.addDev("equal true");
          return true;
          }

          if (result == FALSE_VALUE) {
          TraceManager.addDev("equal false");
          return false;
          }

          errorMessage = "Not a boolean expression: " + _expr;

          TraceManager.addDev("Error:" + errorMessage);*/
        return false;
    }


    public int parseRootExpr1() {
        int[] result = parseNonEmptyExpr();

        if ((result[1] == NUMBER_TOKEN) && (errorMessage == null)) {
            errorMessage = "0/Unexpected integer value";
            return result[0];
        }

        return result[0];

    }

    // Returns the value and type of the return
    // <boolexpr>
    public int[] parseNonEmptyExpr() {
        //TraceManager.addDev("1/Parsing non empty expr");
        int[] result = new int[2];

        computeNextToken1();

        //TraceManager.addDev("currentType=" + currentType);

        if (endOfParsing()) {
            errorMessage = "2/Unexpected end of expression";
            return result;
        }


        if (currentType == CLOSE_PAR_TOKEN) {
            errorMessage = "3/Unexpected closing parenthesis";
            return result;
        }

        if ((currentType == BOOL_TOKEN) || (currentType == NUMBER_TOKEN)) {
            result[0] = currentValue;
            result[1] = currentType;
            //computeNextToken();
            return parseEmptyOrOpExpr(result);
        }

        //TraceManager.addDev("Testing parenthesis type=" + currentType);
        if (currentType == NOT_TOKEN) {
            result = parseNonEmptyExpr();
            if (result[0] == 0) {
                result[0] = 1;
            } else {
                result[0] = 0;
            }
            return result;
        }

        if (currentType == OPEN_PAR_TOKEN) {
            //TraceManager.addDev("opening par token");
            result = parseNonEmptyExpr();
            if (currentType != CLOSE_PAR_TOKEN) {
                errorMessage = "4/Expecting closing parenthesis";
                return result;
            }

            return parseEmptyOrOpExpr(result);
        }

        return result;
    }

    // <empty> or <op bool/int expr>
    public int[] parseEmptyOrOpExpr(int[] result) {
        //TraceManager.addDev("parseEmptyOrOpExpr result0= " + result[0] + " result1=" + result[1] + " currentType=" + currentType);

        computeNextToken1();

        if (endOfParsing()) {
            return result;
        }

        if ((currentType == BOOL_TOKEN) || (currentType == NUMBER_TOKEN)) {
            errorMessage = "5/Unexpected value";
            return result;
        }

        if (currentType == OPEN_PAR_TOKEN) {
            errorMessage = "6/Unexpected opening parenthesis";
            return result;
        }

        if (currentType == CLOSE_PAR_TOKEN) {
            //errorMessage = "7/Unexpected closing parenthesis";
            return result;
        }

        return parseBeginExprOp(result);
    }


    //<op bool/int expr>
    // Wrning: token already computed
    public int[] parseBeginExprOp(int[] result) {
        //TraceManager.addDev("parseBeginExprOp result0= " + result[0] + " result1=" + result[1] + " currentType=" + currentType);
        if (endOfParsing()) {
            errorMessage = "8/Unexpected end of expression";
            return result;
        }


        if (currentType == CLOSE_PAR_TOKEN) {
            errorMessage = "9/Unexpected closing parenthesis";
            return result;
        }

        if (currentType == OPEN_PAR_TOKEN) {
            errorMessage = "10/Unexpected opening parenthesis";
            return result;
        }

        if ((currentType == BOOL_TOKEN) || (currentType == NUMBER_TOKEN)) {
            errorMessage = "11/Unexpected value";
            return result;
        }

        // So, this is an op!
        int typeOfOp = currentType;
        //TraceManager.addDev("Parsing right expression");
        int resultRight[] = parseNonEmptyExpr();

        if (hasError()) {
            return result;
        }

        // Same type on operand
        if (resultRight[1] != result[1]) {
            errorMessage = "12/Type on left and right operand not compatible";
            return result;
        }

        // Boolean ops
        if (resultRight[1] == BOOL_TOKEN) {
            //TraceManager.addDev("** Bool operator = " + typeOfOp);
            if (typeOfOp == EQUAL_TOKEN) {
                if (result[0] == resultRight[0]) {
                    result[0] = 1;
                } else {
                    result[0] = 0;
                }

            } else if (typeOfOp == NOT_EQUAL_TOKEN) {
                if (result[0] == resultRight[0]) {
                    result[0] = 0;
                } else {
                    result[0] = 1;
                }

            } else if (typeOfOp == OR_TOKEN) {
                result[0] = result[0] + resultRight[0];
                if (result[0] > 1) {
                    result[0] = 1;
                }
                //TraceManager.addDev("Or result=" + result[0]);

            } else if (typeOfOp == AND_TOKEN) {
                result[0] = (result[0] * resultRight[0]);

            } else {
                errorMessage = "13/Invalid boolean operator";
            }

            // Int ops
        } else {
            //TraceManager.addDev("** Int operator = " + typeOfOp);
            if (typeOfOp == EQUAL_TOKEN) {
                if (result[0] == resultRight[0]) {
                    result[0] = 1;
                } else {
                    result[0] = 0;
                }
                result[1] = BOOL_TOKEN;

            } else if (typeOfOp == NOT_EQUAL_TOKEN) {
                if (result[0] == resultRight[0]) {
                    result[0] = 0;
                } else {
                    result[0] = 1;
                }
                result[1] = BOOL_TOKEN;

            } else if (typeOfOp == LT_TOKEN) {
                if (result[0] < resultRight[0]) {
                    result[0] = 1;
                } else {
                    result[0] = 0;
                }
                result[1] = BOOL_TOKEN;

            } else if (typeOfOp == GT_TOKEN) {
                if (result[0] > resultRight[0]) {
                    result[0] = 1;
                } else {
                    result[0] = 0;
                }
                result[1] = BOOL_TOKEN;

            } else if (typeOfOp == GTEQ_TOKEN) {
                if (result[0] >= resultRight[0]) {
                    result[0] = 1;
                } else {
                    result[0] = 0;
                }
                result[1] = BOOL_TOKEN;

            } else if (typeOfOp == LTEQ_TOKEN) {
                if (result[0] <= resultRight[0]) {
                    result[0] = 1;
                } else {
                    result[0] = 0;
                }
                result[1] = BOOL_TOKEN;
            }

        }

        return result;

    }


    public boolean endOfParsing() {
        return (currentType == EOLN_TOKEN);
    }


    public void computeNextToken1() {
        // If we're at the end, make it an EOLN_TOKEN.
        if (!tokens.hasMoreTokens()) {
            currentType = EOLN_TOKEN;
            return;
        }

        // Get a token--if it looks like a number,
        // make it a NUMBER_TOKEN.

        String s = tokens.nextToken();
        //TraceManager.addDev("Token? = >" + s + "<");

        char c1 = s.charAt(0);
        if (Character.isDigit(c1)) {
            //TraceManager.addDev("digit found");
            try {
                currentValue = Integer.valueOf(s).intValue();
                currentType = NUMBER_TOKEN;
            } catch (NumberFormatException x) {
                errorMessage = "Illegal format for a number.";
            }
            return;
        }

        //TraceManager.addDev("next 1");

        if (s.compareTo(TRUE) == 0) {
            currentValue = TRUE_VALUE;
            currentType = BOOL_TOKEN;
            //TraceManager.addDev("true token!");
            return;
        }

        if (s.compareTo(FALSE) == 0) {
            currentValue = FALSE_VALUE;
            currentType = BOOL_TOKEN;
            //TraceManager.addDev("false token!");
            return;
        }

        if (s.compareTo("<") == 0) {
            currentValue = 0;
            currentType = LT_TOKEN;
            return;
        }

        if (s.compareTo(">") == 0) {
            currentValue = 0;
            currentType = GT_TOKEN;
            return;
        }

        if (s.compareTo(":") == 0) {
            currentValue = 0;
            currentType = GTEQ_TOKEN;
            return;
        }

        if (s.compareTo(";") == 0) {
            currentValue = 0;
            currentType = LTEQ_TOKEN;
            return;
        }

        if (s.compareTo("=") == 0) {
            currentValue = 0;
            currentType = EQUAL_TOKEN;
            return;
        }

        if (s.compareTo("$") == 0) {
            currentValue = 0;
            currentType = NOT_EQUAL_TOKEN;
            return;
        }

        if (s.compareTo("!") == 0) {
            currentValue = 0;
            currentType = NOT_TOKEN;
            return;
        }

        if (s.compareTo("|") == 0) {
            currentValue = 0;
            currentType = OR_TOKEN;
            return;
        }
        if (s.compareTo("&") == 0) {
            currentValue = 0;
            currentType = AND_TOKEN;
            return;
        }

        if (s.compareTo(")") == 0) {
            currentType = CLOSE_PAR_TOKEN;
            nbOpen--;
            if (nbOpen < 0) {
                TraceManager.addDev("Boolean expr: Found pb with a parenthesis");
                errorMessage = "Parenthesis mismatch";
            }
            return;
        }

        //TraceManager.addDev("next 10");

        if (s.compareTo("(") == 0) {
            //TraceManager.addDev("opening par token");
            currentType = OPEN_PAR_TOKEN;
            nbOpen++;
            return;
        }

        // Any other single character that is not
        // white space is a token.

        if (Character.isWhitespace(c1)) {
            currentType = WHITE_SPACE_TOKEN;
            //TraceManager.addDev("White space found: looping");
            computeNextToken1();
            return;
        }

        // Invalid token
        errorMessage = "Unknown element: " + s;
    }


    /**
     * Match a given token and advance to the next.
     * This utility is used by our parsing routines.
     * If the given token does not match
     * lexer.nextToken(), we generate an appropriate
     * error message.  Advancing to the next token may
     * also cause an error.
     *
     * @param token the token that must match
     */
    private void match(int token) {

        // First check that the current token matches the
        // one we were passed; if not, make an error.

        if (currentType != token) {
            if (token == EOLN_TOKEN)
                errorMessage =
                        "Unexpected text after the expression.";
            else if (token == NUMBER_TOKEN)
                errorMessage = "Expected an integer number.";
            else if (token == BOOL_TOKEN)
                errorMessage = "Expected a boolean.";
            else if (token == EQUAL_TOKEN)
                errorMessage = "Expected an equal.";
            else if (token == NOT_EQUAL_TOKEN)
                errorMessage = "Expected an not equal.";
            else errorMessage =
                        "Expected a " + ((char) token) + ".";
            return;
        }

        // Now advance to the next token.

        computeNextToken();
    }

    /**
     * Parse an expression.  If any error occurs we
     * return immediately.
     *
     * @return the double value of the expression
     * or garbage in case of errors.
     */
    private double parseExpression() {

        // <expression> ::=
        //    <mulexp> { ('+' <mulexp>) | ('-' <mulexp>) }

        Double result = parseMulexp();
        if (hasError()) return result;

        while (true) {
            if (currentType == '+') {
                match('+');
                if (hasError()) return result;
                result += parseMulexp();
                if (hasError()) return result;
            } else if (currentType == '-') {
                match('-');
                if (hasError()) return result;
                result -= parseMulexp();
                if (hasError()) return result;
            } else return result;
        }
    }


    /**
     * Parse a mulexp, a subexpression at the precedence
     * level of * and /.  If any error occurs we return
     * immediately.
     *
     * @return the double value of the mulexp or
     * garbage in case of errors.
     */
    private double parseMulexp() {

        // <mulexp> ::=
        //   <rootexp> { ('==' <rootexp>) | ('*' <rootexp>) | ('/' <rootexp>) }

        double result = parseRootexp();
        double resulttmp;
        int intresult;
        int intresult2;
        if (errorMessage != null) return result;


        while (true) {
            //TraceManager.addDev("Token:" + currentType + " value=" + currentValue);
            if (currentType == EQUAL_TOKEN) {
                match(EQUAL_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();
                //intresult = (int)(resulttmp);
                //intresult2 = (int)(result);

                if (errorMessage != null) return result;

                /*if ((intresult2 != TRUE_VALUE) && (intresult2 != FALSE_VALUE)) {
                  errorMessage = "Expression on the left is not a boolean (result=" + intresult2 + ")";
                  }
                  if ((intresult != TRUE_VALUE) && (intresult != FALSE_VALUE)) {
                  errorMessage = "Expression on the right is not a boolean (result=" + intresult + ")";
                  }*/

                if (result == resulttmp) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == NOT_EQUAL_TOKEN) {
                match(NOT_EQUAL_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();
                //intresult = (int)(resulttmp);
                //intresult2 = (int)(result);

                if (errorMessage != null) return result;

                /*if ((intresult2 != TRUE_VALUE) && (intresult2 != FALSE_VALUE)) {
                  errorMessage = "Expression on the left is not a boolean (result=" + intresult2 + ")";
                  }
                  if ((intresult != TRUE_VALUE) && (intresult != FALSE_VALUE)) {
                  errorMessage = "Expression on the right is not a boolean (result=" + intresult + ")";
                  }*/

                if (result != resulttmp) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == LT_TOKEN) {
                match(LT_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();

                if (errorMessage != null) return result;

                if (result < resulttmp) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == GT_TOKEN) {
                match(GT_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();

                if (errorMessage != null) return result;

                if (result > resulttmp) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == GTEQ_TOKEN) {
                match(GTEQ_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();

                if (errorMessage != null) return result;

                if (result >= resulttmp) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == LTEQ_TOKEN) {
                match(LTEQ_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();

                if (errorMessage != null) return result;

                if (result <= resulttmp) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == OR_TOKEN) {
                match(OR_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();
                //intresult = (int)(resulttmp);
                //intresult2 = (int)(result);

                if (errorMessage != null) return result;


                if ((result != 0) || (resulttmp != 0)) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == AND_TOKEN) {
                match(AND_TOKEN);
                if (errorMessage != null) return result;

                resulttmp = parseRootexp();
                //intresult = (int)(resulttmp);
                //intresult2 = (int)(result);

                if (errorMessage != null) return result;


                if ((result != 0) && (resulttmp != 0)) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == NOT_TOKEN) {
                match(NOT_TOKEN);
                if (errorMessage != null) return result;

                //TraceManager.addDev("NOT TOKEN!");
                resulttmp = parseRootexp();
                //intresult = (int)(resulttmp);
                //intresult2 = (int)(result);

                if (errorMessage != null) return result;

                /*if ((intresult2 != TRUE_VALUE) && (intresult2 != FALSE_VALUE)) {
                  errorMessage = "Expression on the left is not a boolean (result=" + intresult2 + ")";
                  }
                  if ((intresult != TRUE_VALUE) && (intresult != FALSE_VALUE)) {
                  errorMessage = "Expression on the right is not a boolean (result=" + intresult + ")";
                  }*/

                if (((int) (resulttmp)) == 0) {
                    return TRUE_VALUE;
                } else {
                    return FALSE_VALUE;
                }

            } else if (currentType == '*') {
                match('*');
                if (errorMessage != null) return result;
                result *= parseRootexp();
                if (errorMessage != null) return result;
            } else if (currentType == '/') {
                match('/');
                if (errorMessage != null) return result;
                result /= parseRootexp();
                if (errorMessage != null) return result;
            } else return result;
        }
    }

    /**
     * Parse a rootexp, which is a constant or
     * parenthesized subexpression.  If any error occurs
     * we return immediately.
     *
     * @return the double value of the rootexp or garbage
     * in case of errors
     */
    private double parseRootexp() {
        double result = 0.0;

        // <rootexp> ::= '(' <expression> ')'

        if (currentType == '(') {
            match('(');
            if (errorMessage != null) return result;
            result = parseExpression();
            if (errorMessage != null) return result;
            match(')');
            if (errorMessage != null) return result;
        }

        // <rootexp> ::= number

        else if (currentType == NUMBER_TOKEN) {
            result = currentValue;
            if (errorMessage != null) return result;
            match(NUMBER_TOKEN);
            if (errorMessage != null) return result;
        }

        // <rootexp> ::= bool

        else if (currentType == BOOL_TOKEN) {
            result = currentValue;
            if (errorMessage != null) return result;
            match(BOOL_TOKEN);
            if (errorMessage != null) return result;
        } else if (currentType == NOT_TOKEN) {
            match(NOT_TOKEN);
            result = parseExpression();
            if (result == TRUE_VALUE) {
                result = FALSE_VALUE;
            } else {
                result = TRUE_VALUE;
            }
            if (errorMessage != null) return result;
            //match(NEG_TOKEN);
            if (errorMessage != null) return result;
        } else {
            errorMessage = "Expected a value or a parenthesis.";
        }

        return result;
    }


    public void computeNextToken() {
        while (true) {
            // If we're at the end, make it an EOLN_TOKEN.
            if (!tokens.hasMoreTokens()) {
                currentType = EOLN_TOKEN;
                return;
            }

            // Get a token--if it looks like a number,
            // make it a NUMBER_TOKEN.

            String s = tokens.nextToken();
            //TraceManager.addDev("Token? = >" + s + "<");

            char c1 = s.charAt(0);
            if (Character.isDigit(c1)) {
                try {
                    currentValue = Integer.valueOf(s).intValue();
                    currentType = NUMBER_TOKEN;
                } catch (NumberFormatException x) {
                    errorMessage = "Illegal format for a number.";
                }
                return;
            }


            if (s.compareTo(TRUE) == 0) {
                currentValue = TRUE_VALUE;
                currentType = BOOL_TOKEN;
                //TraceManager.addDev("true token!");
                return;
            }

            if (s.compareTo(FALSE) == 0) {
                currentValue = FALSE_VALUE;
                currentType = BOOL_TOKEN;
                //TraceManager.addDev("false token!");
                return;
            }

            if (s.compareTo("<") == 0) {
                currentValue = 0;
                currentType = LT_TOKEN;
                return;
            }

            if (s.compareTo(">") == 0) {
                currentValue = 0;
                currentType = GT_TOKEN;
                return;
            }

            if (s.compareTo(":") == 0) {
                currentValue = 0;
                currentType = GTEQ_TOKEN;
                return;
            }

            if (s.compareTo(";") == 0) {
                currentValue = 0;
                currentType = LTEQ_TOKEN;
                return;
            }

            if (s.compareTo("=") == 0) {
                currentValue = 0;
                currentType = EQUAL_TOKEN;
                return;
            }

            if (s.compareTo("$") == 0) {
                currentValue = 0;
                currentType = NOT_EQUAL_TOKEN;
                return;
            }

            if (s.compareTo("!") == 0) {
                currentValue = 0;
                currentType = NOT_TOKEN;
                return;
            }

            if (s.compareTo("|") == 0) {
                currentValue = 0;
                currentType = OR_TOKEN;
                return;
            }
            if (s.compareTo("&") == 0) {
                currentValue = 0;
                currentType = AND_TOKEN;
                return;
            }

            if (s.compareTo(")") == 0) {
                currentType = c1;
                nbOpen--;
                if (nbOpen < 0) {
                    TraceManager.addDev("Boolean expr: Found pb with a parenthesis");
                }
                return;
            }

            if (s.compareTo("(") == 0) {
                currentType = c1;
                nbOpen++;
                return;
            }

            // Any other single character that is not
            // white space is a token.

            if (!Character.isWhitespace(c1)) {
                currentType = c1;
                return;
            }
        }
    }


    public boolean getResultOfWithIntExpr(String _expr) {
        int index, indexPar;

        String tmp = Conversion.replaceAllString(_expr, "==", "$").trim();
        tmp = Conversion.replaceAllString(tmp, "!=", "$").trim();
        tmp = Conversion.replaceAllString(tmp, ">=", ":").trim();
        tmp = Conversion.replaceAllString(tmp, "<=", ";").trim();
        if (tmp.indexOf("=") > -1) {
            TraceManager.addDev("Not a bool");
            errorMessage = "Not a boolean expression because it contains \"=\" operators";
            return false;
        }
        _expr = Conversion.replaceAllString(_expr, "not", "!").trim();
        _expr = Conversion.replaceAllString(_expr, "true", "t").trim();
        _expr = Conversion.replaceAllString(_expr, "false", "f").trim();
        _expr = Conversion.replaceAllString(_expr, "||", "|").trim();
        _expr = Conversion.replaceAllString(_expr, "&&", "&").trim();
        _expr = Conversion.replaceAllString(_expr, "or", "|").trim();
        _expr = Conversion.replaceAllString(_expr, "and", "&").trim();
        _expr = Conversion.replaceAllString(_expr, "==", "=").trim();
        _expr = Conversion.replaceAllString(_expr, "!=", "$").trim();
        _expr = Conversion.replaceAllString(_expr, ">=", ":").trim();
        _expr = Conversion.replaceAllString(_expr, "<=", ";").trim();

        while ((index = _expr.indexOf("not(")) != -1) {
            indexPar = Conversion.findMatchingParenthesis(_expr, index + 3, '(', ')');
            if (indexPar == -1) {
                errorMessage = "Parenthesis not maching at index " + (index + 3) + " in expression: " + _expr;
                return false;
            }

            _expr = _expr.substring(0, index) + "(!" + _expr.substring(index + 3, indexPar) + ")" + _expr.substring(indexPar, _expr.length());
        }

        tokens = new java.util.StringTokenizer(_expr, " \t\n\r!$=&|<>():;+-/*tf", true);

        IntBoolRes resIBR = parseRootExprInt();
        if (resIBR == null) {
            return false;
        }


        TraceManager.addDev("Tree of " + _expr + ": " + resIBR.toString() + "\nEnd of tree");


        Object res = resIBR.computeValue();

        if (res == null) {
            return false;
        }

        TraceManager.addDev("Tree of " + _expr + ": " + resIBR.toString() + "\nEnd of tree");

        if (res instanceof Integer) {
            errorMessage = "Integer expression. Was expecting a boolean expression";
        }

        if (getError() != null) {
            TraceManager.addDev("Error: " + getError());
        }

        if (res instanceof Boolean) {
            boolean result = ((Boolean) (res)).booleanValue();
            return result;
        }

        errorMessage = "Invalid boolean expression";

        return false;
    }


    public String getNextToken() {
        if (!tokens.hasMoreTokens()) {
            return null;
        }

        String s = tokens.nextToken();
        return s;
    }


    public IntBoolRes parseRootExprInt() {
        top = new IntBoolRes();
        IntBoolRes res = top;

        boolean go = true;
        while (go) {
            String s = getNextToken();
            if (s == null) {
                go = false;
            } else {
                TraceManager.addDev("Working on token:" + s);
                res = parseAndMakeTree(res, s);
                if (res == null) {
                    go = false;
                }
            }
        }

        return top;


    }

    public IntBoolRes parseAndMakeTree(IntBoolRes current, String token) {
        ID = 0;
        IntBoolRes newElt;

        //TraceManager.addDev(current.getTop().toString());
        //TraceManager.addDev("<><><><><><> Dealing with token:" + token + " current=" + current);

        char c1 = token.charAt(0);

        // Space symbol
        if (c1 == ' ') {
            return current;
        }

        // Terminal symbol
        if (Character.isDigit(c1)) {
            TraceManager.addDev("Adding number:" + token);
            try {
                newElt = current.addTerminalInt(Integer.valueOf(token).intValue());
            } catch (NumberFormatException x) {
                errorMessage = "Illegal format for a number.";
                return null;
            }
            if (newElt == null) {
                errorMessage = "Badly placed int value:" + token;
                return null;
            }
            return current;
        }

        if ((c1 == 't') || (c1 == 'f')) {
            newElt = current.addTerminalBool(c1 == 't');
            if (newElt == null) {
                errorMessage = "Badly placed bool value:" + token;
                return null;
            }
            return current;

        }

        // INT BINARY OP
        if (c1 == '+') {
            newElt = current.addIntOperator(PLUS_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '-') {
            newElt = current.addIntOperator(MINUS_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '*') {
            newElt = current.addIntOperator(MULT_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '/') {
            newElt = current.addIntOperator(DIV_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '<') {
            newElt = current.addIntOperator(LT_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '>') {
            newElt = current.addIntOperator(GT_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '>') {
            newElt = current.addIntOperator(GT_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == ';') {
            newElt = current.addIntOperator(LTEQ_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == ':') {
            newElt = current.addIntOperator(GTEQ_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed int operator:" + token;
                return null;
            }
            return newElt;
        }


        // BOOL BINARY OP
        if (c1 == '=') {
            newElt = current.addBinaryOperator(EQUAL_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed bool operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '&') {
            newElt = current.addBinaryOperator(AND_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed bool operator:" + token;
                return null;
            }
            return newElt;
        }

        if (c1 == '|') {
            newElt = current.addBinaryOperator(OR_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed bool operator:" + token;
                return null;
            }
            return newElt;
        }

        // Bool unary op
        if (c1 == '!') {
            newElt = current.addUnaryOperator(NOT_TOKEN);
            if (newElt == null) {
                errorMessage = "Badly placed bool unary operator:" + token;
                return null;
            }
            return newElt;
        }

        // PARENTHESIS
        if (c1 == '(') {

            newElt = current.addOpenParenthesis();
            if (newElt == null) {
                errorMessage = "Badly placed parenthesis:";
                return null;
            }
            return newElt;
        }

        if (c1 == ')') {
            // Must find corresponding parenthesis
            // Looking for father of the correspoing parenthesis;
            IntBoolRes father = current.father;
            while (father != null) {
                if (father.op == OPEN_PAR_TOKEN) {
                    break;
                }
                father = father.father;
            }
            if (father == null) {
                return null;
            }
            return father.father;
        }

        if (supportUnknownTerminal) {
            TraceManager.addDev("Adding unknown term:" + token);
            newElt = current.addTerminalUnknown(token);

            if (newElt == null) {
                errorMessage = "Badly placed unknown term:" + token;
                return null;
            }
            return current;
        }

        return null;


    }


}
