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
 * Class AvatarSyntaxChecker
 * Creation: 21/05/2010
 * @version 1.0 21/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package avatartranslator;

import java.io.*;
import java.util.*;

import compiler.tmlparser.*;
import myutil.*;


public class AvatarSyntaxChecker  {
	
	
    public AvatarSyntaxChecker() {
    }
	
	public static int isAValidGuard(AvatarSpecification _as, AvatarBlock _ab, String _guard) {
		String tmp = _guard.replaceAll(" ", "").trim();
		if (tmp.compareTo("[]") == 0) {
			return 0;
		}
		return parse(_as, _ab, "guard", _guard);
	}
	
	public static int isAValidIntExpr(AvatarSpecification _as, AvatarBlock _ab, String _expr) {
		if (_expr.trim().length() == 0) {
			return 0;
		}
		return parse(_as, _ab, "actionnat", _expr);
	}
	
	public static int isAValidVariableExpr(AvatarSpecification _as, AvatarBlock _ab, String _expr) {
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
		
		if (aa.isInt() || aa.isNat()) {
			return parse(_as, _ab, "actionnat", action);
		} else if (aa.isBool()) {
			return parse(_as, _ab, "actionbool", action);
		} 
		return -1;
	}
	
	/**
	* Parsing in two steps:
	* 1. Parsing the expression with no variable checking
	* 2. Parsing the expression with variables values to see whether variables are well-placed or not
	* The second parsing is performed iff the first one succeeds
	* return -1 in case of error in first pass
	* return -2 in case of error in second pass
	* return -3 in case a variable has not been declared
	*/
	public static int parse(AvatarSpecification _as, AvatarBlock _ab, String _parseCmd, String _action) {
		TMLExprParser parser;
		SimpleNode root;
		int i;
		
		// First parsing
		parser = new TMLExprParser(new StringReader(_parseCmd + " " + _action));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + action);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
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
		
		for(i=0; i<_ab.attributeNb(); i++) {
			modif = _as.putAttributeValueInString(modif, _ab.getAttribute(i));
		}
		
		parser = new TMLExprParser(new StringReader(_parseCmd + " " + modif));
		try {
			//System.out.println("\nParsing :" + parseCmd + " " + modif);
			root = parser.CompilationUnit();
			//root.dump("pref=");
			//System.out.println("Parse ok");
		} catch (ParseException e) {
			TraceManager.addDev("\nAvatar Parsing :" + _parseCmd + " " + modif);
			TraceManager.addDev("\nn(Original parsing :" + _parseCmd + " " + _action);
			TraceManager.addDev("ParseException --------> Parse error in :" + _parseCmd + " " + _action);
			return -2;
		} catch (TokenMgrError tke ) {
			TraceManager.addDev("\nnAvatar Parsing :" + _parseCmd + " " + modif);
			TraceManager.addDev("TokenMgrError --------> Parse error in :" + _parseCmd + " " + _action);
			return -2;
		}  
		
		// Tree analysis: if the tree contains a variable, then, this variable has not been declared
		ArrayList<String> vars = root.getVariables();
		for(String s: vars) {
			// is that string a variable?
			if ((s.compareTo("true") != 0) && (s.compareTo("false") != 0) && (s.compareTo("nil") != 0)) {
				TraceManager.addDev("Variable not declared: " +s);
				return -3;
			}
		}
		
		return 0;
		
	}
	
	
	
    
}