/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 * Class Conversion
 * Creation: 01/12/2003
 * @version 1.1 01/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;

import java.util.*;


public class Conversion {
    
    
    public static String removeFirstSpaces(String s) {
        if (s == null) {
            return s;
        }
        
        while (s.indexOf(' ') == 0) {
            s = s.substring(1, s.length());
        }
        return s;
    }
    
    public static int nbChar(String s, char c) {
        int cpt = 0;
        int index;
        
        while( (index = s.indexOf(c)) != -1) {
            s = s.substring(index +1);
            cpt ++;
        }
        
        return cpt;
    }
    
    public static String replaceChar(String s, char c, String snew) {
        int index = s.indexOf(c);
        if (index > -1) {
            return s.substring(0, index) + snew + s.substring(index + 1);
        }
        return s;
    }
    
    public static String replaceAllChar(String s, char c, String snew) {
        int index;
        String output = "";
        while((index = s.indexOf(c)) > -1 ) {
            output += s.substring(0, index) + snew;
            s = s.substring(index + 1);
        }
        return output + s;
    }
    
    public static String replaceAllString(String s, String input, String snew) {
		if (s == null) {
			return s;
		}
        int index;
        String output = "";
        while((index = s.indexOf(input)) > -1 ) {
            output += s.substring(0, index) + snew;
            s = s.substring(index + input.length());
        }
        return output + s;
    }
	
	public static String replaceRecursiveAllString(String s, String input, String snew) {
        int index;
        while((index = s.indexOf(input)) > -1 ) {
            s = s.substring(0, index) + snew + s.substring(index + input.length(), s.length());
        }
        return s;
    }
	
	public static String replaceBetweenIndex(String s, int index0, int index1, String oldone, String newone) {
		return s.substring(0, index0) + replaceAllString(s.substring(index0, index1), oldone, newone) + s.substring(index1, s.length());
	}
    
    public static String replaceAllStringNonAlphanumerical(String s, String input, String snew) {
        int index;
        String output = "";
        boolean b1, b2;
		
		if (s == null) {
			return s;
		}
		
        //System.out.println("\nMust analyze:" + s);
        while((index = s.indexOf(input)) > -1 ) {
            //System.out.println("Analyzing:" + s);
            if (index == 0) {
                b1 = false;
            } else {
                //System.out.println("substring = " + s.substring(index-1, index));
                b1 = s.substring(index-1, index).matches("\\w*");
            }
            if ((index+input.length()) == s.length()) {
                b2 = false;
            } else {
                //System.out.println("substring = " + s.substring(index+1, index+2));
                b2 = s.substring(index+input.length(), index+input.length()+1).matches("\\w*");
            }
            
            if (!b1 && !b2) {
                output += s.substring(0, index) + snew;
                s = s.substring(index + input.length());
                //System.out.println("modifying");
            } else {
                output += s.substring(0, index+input.length());
                s = s.substring(index + input.length());
            }
        }
        //System.out.println("result=" + output + s);
        return output + s;
    }
    
    // example of call: "div(8,5)", "div", "/"
    public static String changeBinaryOperatorWithUnary(String _input, String _binary, String _unary) {
        int index1, index2;
        String s1, s2;
        
        while ((index1 = locateBinaryOperator(_input, _binary)) > -1) {
            //System.out.println("binary located at index:" + index1);
            s1 = _input.substring(_binary.length() + index1 + 1, extractComma(index1, _input, _binary));
            //System.out.println("s1=" + s1);
            index2 = extractLastParenthesis(index1, _input, _binary);
            s2 = _input.substring(extractComma(index1, _input, _binary) + 1, index2);
            //System.out.println("s2=" + s2);
            _input = _input.substring(0, index1) + "((" + s1 + ")" + _unary + "(" + s2 + "))" + _input.substring(index2+1,_input.length()); 
        }
        
        return _input;
    }
    
    
    public static int extractComma(int index, String _input, String _binary) {
        int dep = index + _binary.length() + 1;
        int cpt = dep;
        int paren = 0;
        char c;
        
        while(cpt < _input.length()) {
            c = _input.charAt(cpt);
            //System.out.println("c=" + c + " cpt=" + cpt);
            if (c == '(') {
                paren ++;
            }
            if (c == ')') {
                paren --;
            }
            if ((c == ',') && (paren == 0)) {
                return cpt;
            }
           
            cpt ++;
        }
        
        return -1;      
    }
    
    public static int extractLastParenthesis(int index, String _input, String _binary) {
        int dep =  extractComma(index, _input, _binary);
        int cpt = dep;
        int paren = 0;
        char c;
        
        while(cpt < _input.length()) {
            c = _input.charAt(cpt);
            if ((c == ')') && (paren == 0)) {
                return cpt;
            }
            if (c == '(') {
                paren ++;
            }
            if (c == ')') {
                paren --;
            }
            cpt ++;
        }
        
        return -1;      
    }
    
    
    public static int locateBinaryOperator(String _input, String _binary) {
        int index;
        boolean b1;
        int cpt = 0;
        
        //System.out.println("locate");
        
        if ((index = _input.indexOf(_binary+"(")) > -1) {
            //System.out.println("May have found one " + _binary + " in " + _input);
            if (index == 0) {
                b1 = false;
            } else {
                b1 = _input.substring(index-1, index).matches("\\w*");
            }
            
            if (!b1) {
                return index + cpt;
            } else {
                //System.out.println("Wrong one");
                cpt = index + _binary.length();
                _input = _input.substring(index+_binary.length(), _input.length());
            }
        }
        return -1;
    }
    
    public static String insertAfterAll(String s, char c, String snew) {
        int index;
        String result="";
        while((index = s.indexOf(c)) > -1 ) {
            result += s.substring(0, index+1) + snew;
            s = s.substring(index+1, s.length());
        }
        return result + s;
    }
    
    public static String[] wrapText(String text) {
        text = text.replace('\r', '\n') ;
        return text.split("\n");
    }
    
    public static String cutSection(String s, char c1, char c2) {
        String s1;
        int index1, index2;
        while((index1 =  s.indexOf(c1)) != -1) {
            s1 = s.substring(index1);
            index2 = s1.indexOf(c2);
            if (index2 == -1) {
                s = s.substring(0, index1);
            } else {
                s = s.substring(0, index1) + s.substring(index2);
            }
        }
        return s;
    }
    
    public static String[] cutIntoSectionsBeginningWith(String s, char c) {
      int nb = nbOf(s,c);
      String[] result = new String[nb];
      int index, index1;

      for(int i=0; i<nb; i++) {
        //System.out.println("1 c.s=" + s);
        index = s.indexOf(c);
        s = s.substring(index+1, s.length());
        //System.out.println("2 c.s=" + s);
        index1 = s.indexOf(c);
        if (index1 == -1) {
           index1 = s.length();
        }
        if (index != -1) {
          result[i] = s.substring(0, index1).trim();
        }
      }
      return result;
    }

    public static double ro(double x, double y) {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }
    
    public static double theta(double x, double y) {
        if (x == 0) {
            if (y < 0)
                return -Math.PI/2;
            else
                return Math.PI/2;
        }
        else {
            if (x <0)
                return Math.atan(y/x) + Math.PI;
            else
                return Math.atan(y/x);
        }
        
    }
    
    public static String replaceOp(String s, String from, String to) {
        //System.out.println("Replace op s=" + s + " from=" + from + " to=" + to);
        int fromIndex = 0;
        int index;
        int len;
        boolean b1, b2;
        String s1, s2;
        
        while ( (index = s.indexOf(from, fromIndex)) != -1) {
            // Wrong data or not ? Nb or character just before or just after ?
            if (index == 0) {
                s1 = s.substring(index, index + from.length());
            } else {
                s1 = s.substring(index-1, index + from.length());
            }
            
            b1 = s1.matches("\\w*");
            
            if (index == (s.length() - from.length() - 1)) {
                s2 = s.substring(index, index + from.length());
            } else {
                s2 = s.substring(index, Math.min(index + from.length() + 1, s.length()));
            }
            //System.out.println("s1 = " + s1 + " s2 = " + s2);
            
            b2 = s2.matches("\\w*");
            
            if (!(b1 || b2)) {
                // from must be replaced
                s = s.substring(0, index) + to + s.substring(index + from.length(), s.length());
                //System.out.println("Replaced ! new s=" + s);
                fromIndex = index + to.length();
            } else {
                fromIndex = index + 1;
            }
            
        }
        
        
        return s;
    }
    
    public static String indentString(String _input, int _nbDec) {
        int dec = 0;
        int indexEnd;
        String output = "";
        String tmp;
        int nbOpen = 0;
        int nbClose = 0;
        
        while ( (indexEnd = _input.indexOf('\n')) > -1) {
            tmp = _input.substring(0, indexEnd+1);
            try {
                _input = _input.substring(indexEnd+1, _input.length());
            } catch (Exception e) {
                _input = "";
            }
            
            //System.out.println("tmp = " + tmp);
            nbOpen = nbOf(tmp, '{');
            nbClose = nbOf(tmp, '}');
            dec -= nbClose * _nbDec;
            tmp = addHead(tmp, ' ', dec);
            dec += nbOpen * _nbDec;
            output += tmp;
        }
        
        return output + _input;
    }
    
    public static int nbOf(String _input, char _c) {
        int total = 0;
        for(int i=0; i<_input.length(); i++) {
            if (_input.charAt(i) == _c) {
                total ++;
            }
        }
        return total;
    }
    
    public static String addHead(String _input, char _c, int _nb) {
        String output = "";
        while(_nb >0) {
            output = output + _c;
            _nb --;
        }
        return output + _input;
    }
    
    public static boolean containsStringInList(LinkedList ll, String s) {
        ListIterator iterator = ll.listIterator();
        Object o;
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof String) {
                if (s.compareTo((String)o) == 0) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static String transformToXMLString(String s) {
        if (s != null) {
            s = Conversion.replaceAllChar(s, '&', "&amp;");
            s = Conversion.replaceAllChar(s, '<', "&lt;");
            s = Conversion.replaceAllChar(s, '>', "&gt;");
            s = Conversion.replaceAllChar(s, '"', "&quot;");
            s = Conversion.replaceAllChar(s, '\'', "&apos;");
        }
        return s;
    }
	
	public static String removeComments(String _s) {
		// Two types of comments: the one used in C++
		String input = _s;
		int index = 0;
		int index1;
		boolean inString = false;
		boolean inQuote = false;
		boolean isSlashed = false;
		boolean isEscaped = false;
		boolean inStarComment = false;
		boolean inLineComment = false;
		String ret = "";
		char c;
		
		while(index<input.length()) {
			c = input.charAt(index);
			index ++;
			
			if(c == '\"') {
				
				if (isEscaped) {
					isEscaped = false;
				} else {
					inString = !inString;
				}
				//System.out.println("Found guillemet: instring=" + inString);
			}
			
			if((c == '\'') && (inString == false)){
				if (isEscaped) {
					isEscaped = false;
				} else {
					inQuote = !inQuote;
				}
			}
			
			if (c == '\\') {
				isEscaped = !isEscaped;
				isSlashed = false;
			} else {
				isEscaped = false;
			}
			
			if ((c == '*') && (!inString) && (!inQuote)){
				if (isSlashed) {
					// Beginning of star comment
					ret += input.substring(0, index-2);
					input = input.substring(index, input.length());
					
					index1 = input.indexOf("*/");
					if (index1 == -1) {
						return ret;
					}
					input = input.substring(index1+2, input.length());
					index = 0;
				}
				isSlashed = false;
			}
			
			if ((c == '/') && (!inString) && (!inQuote)) {
				if (!isSlashed) {
					isSlashed = true;
				} else {
					// Beginning of line comment
					ret += input.substring(0, index-2);
					input = input.substring(index, input.length());
					//System.out.println("ret=" + ret);
					//System.out.println("beg of input=" + input.substring(0, Math.min(5, input.length())));
						
					index1 = input.indexOf("\n");
					if (index1 == -1) {
						return ret;
					}
					input = input.substring(index1, input.length());
					//System.out.println("beg of input=" + input.substring(0, Math.min(5, input.length())));
					index = 0;
					isSlashed = false;
				}
			}
			
		}
		
		return ret + input;
	}
	
	public static String putVariableValueInString(String[] ops, String expr, String variableName, String value) {
		String ret = " " + expr + " ";
		String name = " " + variableName + " ";
		String s0;
		
		
		boolean go = true;
		while(go) {
			s0 = removeAllActionOps(ops, ret, " ");
			int index = s0.indexOf(name);
			if (index == -1) {
				go = false;
			} else {
				ret = ret.substring(0, index+1) + value + ret.substring(index + name.length() - 1, ret.length());
			}
		}
		
		return ret.trim();
	}
	
	public static String removeAllActionOps(String[] ops, String input, String replacementValue) {
		String output = input;
		String tmp;
		int cpt;
		
		for(int i=0; i<ops.length; i++) {
			cpt = ops[i].length();
			tmp = "";
			while(cpt > 0) {
				tmp = tmp + replacementValue;
				cpt --;
			}
			output = replaceAllString(output, ops[i], tmp);
		}
		return output;
	}
	
	/**
	 * Returns the index of the corresponding closing parenthesis.
	 * indexFirst corresponds to the indexd of the open parenthesis
	 */
	public static int findMatchingParenthesis(String expr, int indexFirst, char openPar, char closePar) {
		int index = indexFirst + 1;
		boolean found = true;
		int total = 0;
		while(index < expr.length()) {
			if (expr.charAt(index) == openPar) {
				total ++;
			} else if (expr.charAt(index) == closePar) {
				if (total == 0) {
					// found!!
					return index;
				} 
				total --;
			}
			index ++;
		}
		
		return -1;
	}
	
	public static boolean isNumeralOrId(String s) {
		return (isNumeral(s) || isId(s));
	}
	
	public static boolean isNumeral(String s) {
		s = s.trim();
		return s.matches("\\d+");
	}
	
	public static boolean isId(String s) {
		s = s.trim();
		boolean b1 = (s.substring(0,1)).matches("[a-zA-Z]");
        boolean b2 = s.matches("\\w*");
		return (b1 && b2);
	}
	
	
	/*public static String removeComments(String _s) {
		// Two types of comments: the one used in C++
		String ret = removeStarComments(_s);
		ret = removeLineComments(ret);
		return ret;
	}
	
	public static String removeStarComments(String _s) {
		int size = _s.length();
		int index = 0;
		boolean inString = false;
		boolean slash = false;
		boolean antislash = false;
		boolean inStarComment = false;
		boolean inLineComment = false;
		String ret = "";
		char c;
		
		while(index<size) {
			c = _s.charAt(index);
			if (c == '\\') {
				antislash = true;
				slash = false;
			} else if (c == '\"') {
				if (!antislah) {
					inString = !inString;
				}
			} else {
				antislash = false;
			}	
		}
	}*/
	
	// Assume that there is no star-based comments
	/*public static String removeLineComments(String _s) {
		String input = _s;
		String ret = "";
		int index0; // Comment
		
		
		
		while((index0 = input.indexOf("//")) > -1) {
			index1 = firstStringIndex(input);
			
			if ((index1 == -1) || (index0 < index1)) {
				//Comment to remove
				index3 = input.indexOf('\n');
				if (index3 == -1) {
					index3 = input.length();
				}
				ret = ret + input.substring(0, index1);
				input = input.substring(index3, input.length());
			} else {
				if (index1 != -1) {
					index2 = endOfString(input, index1);
					ret = ret + input.substring(0, index2);
					input = substring(Math.min(index2+1, input.length()), input.length());
				}
			}
		}
		return ret;
	}

	public static int firstStringIndex(String _s) {
		if (_s.indexOf('\"') == -1) {
			return -1;
		}
		
		int cpt;
		int index;
		
		while(index = _s.indexOf('\"') > -1) {
			
		}
	}
	
	//Locate the first string and returns the final index of its end
	public static int endOfString(String _s) {
		int index;
		String ret = "";
		while(index = _s.indexOf(
	}*/


    
} // Class Conversion
