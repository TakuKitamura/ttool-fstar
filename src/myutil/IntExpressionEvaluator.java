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
 * Class IntExpressionEvaluator
 * Creation: 13/12/2010
 * Version 2.0 13/12/2010
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;

import java.util.*;

public class IntExpressionEvaluator {
  public static final int NUMBER_TOKEN = -1;
  public static final int EOLN_TOKEN = -2;

   private StringTokenizer tokens;
   private String errorMessage = null;
   
   private int currentType;
   private int currentValue;
   
   
   public IntExpressionEvaluator() {
   }
   
   public String getError() {
	   return errorMessage;
   }
   
   public boolean hasError() {
	   return errorMessage != null;
   }
   
   public boolean hasFinished() {
	   return currentType == EOLN_TOKEN;
   }
   
   public double getResultOf(String _expr) {
	   //TraceManager.addDev("Computing:" + _expr);
	   tokens = new java.util.StringTokenizer(_expr," \t\n\r+-*/()",true);
	   
	   computeNextToken();
	   return parseExpression();
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
        errorMessage = "Expected a number.";
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

    double result = parseMulexp();
    if (errorMessage != null) return result;

    while (true) {
      if (currentType == '+') {
        match('+');
        if (errorMessage != null) return result;
        result += parseMulexp();
        if (errorMessage != null) return result;
      }
      else if (currentType == '-') {
        match('-');
        if (errorMessage != null) return result;
        result -= parseMulexp();
        if (errorMessage != null) return result;
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
    //   <rootexp> { ('*' <rootexp>) | ('/' <rootexp>) }

    double result = parseRootexp();
    if (errorMessage != null) return result;

    while (true) {
      if (currentType == '*') {
        match('*');
        if (errorMessage != null) return result;
        result *= parseRootexp();
        if (errorMessage != null) return result;
      }
      else if (currentType == '/') {
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

    else {
      errorMessage = 
        "Expected a number or a parenthesis.";
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
      char c1 = s.charAt(0);
      if (s.length()>1 || Character.isDigit(c1)) {
        try {
          currentValue = Integer.valueOf(s).intValue();
          currentType = NUMBER_TOKEN;
        }
        catch (NumberFormatException x) {
          errorMessage = "Illegal format for a number.";
        }
        return;
      }

      // Any other single character that is not 
      // white space is a token.

      else if (!Character.isWhitespace(c1)) {
        currentType = c1;
        return;
      }
    }
  }


   
   
  
}
