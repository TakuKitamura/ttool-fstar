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
 * Class AvatarMethod
 * Method in Avatar ...
 * Creation: 08/04/2010
 * @version 1.0 08/04/2010
 * @author Ludovic APVRILLE
 * @see
 */


package ui;

import translator.*;
import translator.tojava.*;
import java.util.*;

import myutil.*;

public class AvatarMethod {
    
    // Types of parameters
    public final static int NATURAL = 0;
    public final static int BOOLEAN = 1;
	public final static int INTEGER = 2;
	
    protected String id;
	protected String typeIds[];
    protected String types[];
    
	
    public AvatarMethod(String _id, String _types[], String _typeIds[]) {
        id = _id;
		types = _types;
		typeIds = _typeIds;
    }
	
	// An operation must be of the form: "id(type id0, type id1, ...)
	// Returns null in case the method is not valid
	public static AvatarMethod isAValidMethod(String _method) {
		String method, tmp, id;
		
		if (_method == null) {
			return null;
		}
		
		method = _method.trim();
		
		if (method.length() == 0) {
			return null;
		}
		
		int index0 = _method.indexOf('(');
		int index1 = _method.indexOf(')');
		
		// Only one of the two parenthesis
		if ((index0 == -1) && (index1 > -1)) {
			return null;
		}
		
		if ((index1 == -1) && (index0 > -1)) {
			return null;
		}
		
		// No parenthesis at all
		if ((index0 == -1) && (index1 == -1)) {
			if (isAValidId(method, true, true, true)) {
				return new AvatarMethod(method, new String [0], new String[0]); 
			} else {
				return null;
			}
		}
		
		// Check parenthesis order 
		if (index0 > index1) {
			return null;
		}
		
		// Check that only one parenthesis of each type
		tmp = method.substring(Math.min(index0+1, method.length()), method.length());
		if (tmp.indexOf('(') > -1) {
			return null;
		}
		tmp = method.substring(Math.min(index1+1, method.length()), method.length());
		if (tmp.indexOf(')') > -1) {
			return null;
		}
		
		// And so: parenthesis are in the right order, and are used only one for each
		
		// Before parenthesis -> id
		tmp = method.substring(0, index0).trim();
		if (!isAValidId(tmp, true, true, true)) {
			return null;
		}
		id = tmp;
		
		// Between parenthesis: parameters of the form: String space String comma
		// We replace double space by spaces and then spaces by commas
		tmp = method.substring(index0+1, index1).trim();
		
		// no parameter?
		if (tmp.length() == 0) {
			return new AvatarMethod(id, new String [0], new String[0]); 
		}
		
		// Has parameters...
		tmp = Conversion.replaceAllString(tmp, "  ", " ");
		tmp = Conversion.replaceAllString(tmp, " ,", ",");
		tmp = Conversion.replaceAllString(tmp, ", ", ",");
		tmp = Conversion.replaceAllChar(tmp, ' ', ","); 
		
		//TraceManager.addDev("tmp=" + tmp);
		
		String splitted[] = tmp.split(",");
		int size = splitted.length/2;
		//TraceManager.addDev("Nb of parameters=" + size);
		String types[] = new String[size];
		String typeIds[] = new String[size];
		boolean b0, b1;
		int i;
		
		/*for(i=0; i<splitted.length; i++) {
			TraceManager.addDev("splitted[" + i + "]: " + splitted[i]);
		}*/
		
		try {
			for(i=0; i<splitted.length; i = i + 2){
				if (splitted[i].length() == 0) {
					return null;
				}
				if (splitted[i+1].length() == 0) {
					return null;
				}
				if (!isAValidId(splitted[i], false, false, false)) {
					//TraceManager.addDev("Unvalid type: " + splitted[i]);
					return null;
				} 
				if (!isAValidId(splitted[i+1], true, true, true)) {
					//TraceManager.addDev("Unvalid id of parameter " + splitted[i+1]);
					return null;
				} 
				//TraceManager.addDev("Adding parameter: " + splitted[i] + " " + splitted[i+1]);
				types[i/2] = splitted[i];
				typeIds[i/2] = splitted[i+1];
			}
		} catch (Exception e) {
			TraceManager.addDev("AvatarMethod Exception:" + e.getMessage());
			return null;
		}
		
		return new AvatarMethod(id, types, typeIds);
	}
    
    public String getId() { return id;}
	public String[] getTypes(){ return types;}
	public String[] getTypeIds(){ return typeIds;}
	
    public String getType(int _index) { 
		if ((_index <0) || (_index>=types.length)) {
			return null;
		}
		return types[_index];
	}
	
	public String getTypeId(int _index) {
		if ((_index <0) || (_index>=typeIds.length)) {
			return null;
		}
		return typeIds[_index];
	}
		
    
    public static boolean isAValidId(String id, boolean checkKeyword, boolean checkJavaKeyword, boolean checkTypes) {
        // test whether _id is a word
        
        if ((id == null) || (id.length() < 1)) {
            return false;
        }
        
        String lowerid = id.toLowerCase();
        boolean b1, b2, b3, b4, b5;
        b1 = (id.substring(0,1)).matches("[a-zA-Z]");
        b2 = id.matches("\\w*");
        if (checkKeyword) {
            b3 = !RTLOTOSKeyword.isAKeyword(lowerid);
        } else {
            b3 = true;
        }
        if (checkJavaKeyword) {
            b5 = !JKeyword.isAKeyword(lowerid);
        } else {
            b5 = true;
        }
        
		if (checkTypes) {
			if ((lowerid.equals(getStringType(0).toLowerCase())) || (lowerid.equals(getStringType(1).toLowerCase())) || (lowerid.equals(getStringType(2).toLowerCase())) || (lowerid.equals(getStringType(3).toLowerCase())) || (lowerid.equals(getStringType(4).toLowerCase()))) {
				b4 = false;
			} else {
				b4 = true;
			}
		} else {
			b4 = true;
		}
        
        return (b1 && b2 && b3 && b4 && b5);
    }
    
    public static boolean notIn(String s, Vector forbidden) {
        if (forbidden == null) {
            return true;
        }
        
        AvatarMethod am;
        
        for(int i= 0; i<forbidden.size(); i++) {
            am = (AvatarMethod)(forbidden.elementAt(i));
            if (s.compareTo(am.getId()) ==0) {
                return false;
            }
        }
        
        return true;
    }
    
    public static int getType(String s) {
        if (s.equals("nat")) {
            return 	NATURAL;
        } else if (s.equals("bool")) {
            return 	BOOLEAN;
        } else if (s.equals("int")) {
            return 	INTEGER;
        }
        return -1;
    }
    
    public static String getStringType(int type) {
        switch(type) {
            case NATURAL:
                return "nat";
            case BOOLEAN:
                return "bool";
			case INTEGER:
                return "int";
        }
		return "";
    }
    
    public String toString() {
		int cpt = 0;
		
        String method = id + "(";
		for (int i=0; i<types.length; i++) {
			method += types[i] + " " + typeIds[i];
			if (i<(types.length - 1)) {
				method += ", ";
			}
		}
		method += ")";
		return method;
    }
    
    // Comparison on id only
    public boolean equals(Object o) {
        if (!(o instanceof AvatarMethod)) {
            return false;
        }
        
        AvatarMethod am = (AvatarMethod)o;
        if (getId().equals(am.getId())) {
            return true;
        }
        return false;
        
    }
    
    // Comparison on all fields
    /*public int compareTo(Object o){
         if (!(o instanceof AvatarMethod)) {
            return 1;
        }
         
        AvatarMethod am = (AvatarMethod)o;
        if (!(getId().equals(am.getId()))) {
            return 1;
        }
       
         
        return 0;
        
    }*/
    
    public AvatarMethod makeClone() {
        return isAValidMethod(toString());
    }
}