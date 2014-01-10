/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
* Class BoolExpressionEvaluator
* Creation: 13/12/2010
* Version 2.0 13/12/2010
* @author Ludovic APVRILLE
* @see
*/

package myutil;

import java.util.*;

public class BoolExpressionEvaluator {
	
	public static final String TRUE = "t";
	public static final String FALSE = "f";
	
	public static final int TRUE_VALUE = 1;
	public static final int FALSE_VALUE = 0;
	
	public static final int NUMBER_TOKEN = -1;
	public static final int BOOL_TOKEN = -2;
	public static final int EQUAL_TOKEN = -3;
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
	
	// PARSING_STAGE
	public static final int BEGIN_EXPR = 1;
	
	private StringTokenizer tokens;
	private String errorMessage = null;
	
	private int currentType;
	private int currentValue; 
	
	private int nbOpen;
	
	
	public BoolExpressionEvaluator() {
	}
	
	public String getError() {
		int index = errorMessage.indexOf("/");
		if (index == -1) {
			return errorMessage;
		}
		
		return errorMessage.substring(index+1, errorMessage.length());
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
		TraceManager.addDev("Evaluating bool expr: " + _expr);
		//_expr = Conversion.replaceAllString(_expr, "not", "!").trim();
		
		nbOpen = 0;
		
		String tmp = Conversion.replaceAllString(_expr, "==", "$").trim();
		tmp = Conversion.replaceAllString(tmp, ">=", ":").trim();
		tmp = Conversion.replaceAllString(tmp, "<=", ";").trim();
		if (tmp.indexOf("=") > -1) {
			errorMessage = "Not a boolean expression because it contains \"=\" operators";
			return false;
		}
		
		_expr = Conversion.replaceAllString(_expr, "||", "|").trim();
		_expr = Conversion.replaceAllString(_expr, "&&", "&").trim();
		_expr = Conversion.replaceAllString(_expr, "or", "|").trim();
		_expr = Conversion.replaceAllString(_expr, "and", "&").trim();
		_expr = Conversion.replaceAllString(_expr, "==", "=").trim();
		_expr = Conversion.replaceAllString(_expr, ">=", ":").trim();
		_expr = Conversion.replaceAllString(_expr, "<=", ";").trim();
		
		// For not() -> must find the closing bracket
		
		int index;
		int indexPar;
		
		while((index = _expr.indexOf("not(")) != -1) {
			indexPar = Conversion.findMatchingParenthesis(_expr, index+3, '(', ')');
			if( indexPar == -1)	{
				errorMessage = "Parenthesis not maching at index " + (index + 3) + " in expression: " + _expr;
				return false;
			}
			
			_expr = _expr.substring(0, index) + "(!" + _expr.substring(index+3, indexPar) + ")" + _expr.substring(indexPar, _expr.length());
		}
		
		
		
		//TraceManager.addDev("Computing:" + _expr);
		
		tokens = new java.util.StringTokenizer(_expr," \t\n\r!=&|<>():;tf",true);
		
		int result = parseRootExpr1();
		
		if (result == TRUE_VALUE) {
			TraceManager.addDev("equal true");
			return true;
		}
		
		if (result == FALSE_VALUE) {
			TraceManager.addDev("equal false");
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
		TraceManager.addDev("1/Parsing non empty expr");
		int[] result = new int[2];
		
		computeNextToken1();
		
		TraceManager.addDev("currentType=" + currentType);
	
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
		
		TraceManager.addDev("Testing parenthesis type=" + currentType);
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
			TraceManager.addDev("opening par token");
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
		TraceManager.addDev("parseEmptyOrOpExpr result0= " + result[0] + " result1=" + result[1] + " currentType=" + currentType);
		
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
		TraceManager.addDev("parseBeginExprOp result0= " + result[0] + " result1=" + result[1] + " currentType=" + currentType);
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
		TraceManager.addDev("Parsing right expression");
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
			TraceManager.addDev("** Bool operator = " + typeOfOp);
			if (typeOfOp == EQUAL_TOKEN) {
				if (result[0] == resultRight[0]) {
					result[0] = 1; 
				} else {
					result[0] = 0; 
				}
				
			} else if (typeOfOp == OR_TOKEN) {
				result[0] = result[0] + resultRight[0];
				if (result[0] > 1) {
					result[0] = 1;
				}
				TraceManager.addDev("Or result=" + result[0]);
				
			} else if (typeOfOp == AND_TOKEN) {
				result[0] = (result[0] * resultRight[0]);
				
			} else {
				errorMessage = "13/Invalid boolean operator";
			}
					
		// Int ops
		} else {
			TraceManager.addDev("** Int operator = " + typeOfOp);
			if (typeOfOp == EQUAL_TOKEN) {
				if (result[0] == resultRight[0]) {
					result[0] = 1; 
				} else {
					result[0] = 0; 
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
		TraceManager.addDev("Token? = >" + s + "<");
		
		char c1 = s.charAt(0);
		if (Character.isDigit(c1)) {
			TraceManager.addDev("digit found");
			try {
				currentValue = Integer.valueOf(s).intValue();
				currentType = NUMBER_TOKEN;
				//System.out.println("value:" + s);
			}
			catch (NumberFormatException x) {
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
			nbOpen --;
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
			nbOpen ++;
			return;
		}
		
		// Any other single character that is not 
		// white space is a token.
		
		if (Character.isWhitespace(c1)) {
			currentType = WHITE_SPACE_TOKEN;
			TraceManager.addDev("White space found: looping");
			computeNextToken1();
		}
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
			}
			else if (currentType == '-') {
				match('-');
				if (hasError()) return result;
				result -= parseMulexp();
				if (hasError()) return result;
			}
			else return result;
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
				
				
				if ((result != 0)  && (resulttmp != 0)) {
					return TRUE_VALUE;
				} else {
					return FALSE_VALUE;
				}
				
			} else if (currentType == NOT_TOKEN) {
				match(NOT_TOKEN);
				if (errorMessage != null) return result;
				
				TraceManager.addDev("NOT TOKEN!");
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
				
				if (((int)(resulttmp)) == 0) {
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
			}
			else return result;
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
		
		else if (currentType==NUMBER_TOKEN){
			result = currentValue;
			if (errorMessage != null) return result;
			match(NUMBER_TOKEN);
			if (errorMessage != null) return result;
		}
		
		// <rootexp> ::= bool
		
		else if (currentType==BOOL_TOKEN){
			result = currentValue;
			if (errorMessage != null) return result;
			match(BOOL_TOKEN);
			if (errorMessage != null) return result;
		}
		
		else if (currentType==NOT_TOKEN){
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
		}
		
		else {
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
			TraceManager.addDev("Token? = >" + s + "<");
			
			char c1 = s.charAt(0);
			if (Character.isDigit(c1)) {
				try {
					currentValue = Integer.valueOf(s).intValue();
					currentType = NUMBER_TOKEN;
					//System.out.println("value:" + s);
				}
				catch (NumberFormatException x) {
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
				nbOpen --;
				if (nbOpen < 0) {
					TraceManager.addDev("Boolean expr: Found pb with a parenthesis");
				}
				return;
			}
			
			if (s.compareTo("(") == 0) {
				currentType = c1;
				nbOpen ++;
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
	
	
	
	
	
}
