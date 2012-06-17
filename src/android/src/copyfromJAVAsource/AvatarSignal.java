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
 * Class AvatarSignal
 * Signals in Avatar ...
 * Creation: 08/04/2010
 * @version 1.0 08/04/2010
 * @author Ludovic APVRILLE
 * @see
 */


package copyfromJAVAsource;

//import translator.*;
//import translator.tojava.*;
import java.util.*;

//import myutil.*;

public class AvatarSignal extends AvatarMethod {
    
    // Signa type 
    public final static int IN = 0;
    public final static int OUT = 1;
    
    private int inout;
  
	
    public AvatarSignal(int _inout, String _id, String _types[], String _typeIds[]) {
        super(_id, _types, _typeIds);
		inout = _inout;
    }
	
	public static AvatarSignal isAValidSignal(String _content) {
		String tmp = _content.trim();
		if (!((_content.startsWith("in ")) || (_content.startsWith("out ")))) {
			return null;
		}
		
		int tmpinout;
		if (_content.startsWith("in ")) {
			tmpinout = IN;
		} else {
			tmpinout = OUT;
		}
		
		return isAValidSignal(tmpinout, tmp.substring(3, tmp.length()).trim());
	}
	
	public static AvatarSignal isAValidSignal(int _inout, String _content) {
		if (!((_inout == IN) || (_inout == OUT))) {
			return null;
		}
		
		AvatarMethod am = isAValidMethod(_content);
		if (am == null) {
			TraceManager.addDev("invalid signal: " + _content); 
			return null;
		}
		AvatarSignal as = new AvatarSignal(_inout, am.getId(), am.getTypes(), am.getTypeIds());
		
		return as;
	}
	
	public static boolean isAValidUseSignal(String _content) {
		if (_content.indexOf("()") == -1) {
			_content = Conversion.replaceAllString(_content, "(", "#int ");
			_content = Conversion.replaceAllString(_content, "#", "(");
			_content = Conversion.replaceAllString(_content, ",", "#int ");
			_content = Conversion.replaceAllString(_content, "#", ",");
		}
		TraceManager.addDev("content:" + _content);
		return (isAValidMethod(_content) != null);
	}
	
	public int getInOut() {
		return inout;
	}
	
	public static String getStringInOut(int _inout) {
		switch(_inout) {
		case IN:
			return "in";
		case OUT:
		default:
			return "out";
		}
	}
	
	public String toBasicString() {
		return super.toString();
	}
    
    public String toString() {
		int cpt = 0;
		
        String signal = getStringInOut(inout) + " ";
		signal += super.toString();
		return signal;
    }
	

    
    // Comparison on id only
    public boolean equals(Object o) {
        if (!(o instanceof AvatarSignal)) {
            return false;
        }
        
        AvatarSignal as = (AvatarSignal)o;
        if (getId().equals(as.getId())) {
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
    
    public AvatarSignal makeClone() {
        return isAValidSignal(inout, super.toString());
    }
	
	public boolean isCompatibleWith(AvatarSignal _as) {
		if (_as.getInOut() == getInOut()) {
			return false;
		}
		
		String[] astypes = _as.getTypes();
		
		if (astypes.length != types.length) {
			return false;
		}
		
		for(int i=0; i<types.length; i++) {
			if (!(types[i].compareTo(astypes[i]) == 0)) {
					return false;
			}
		}
		
		return true;
	}
	
	public static String getSignalNameFromFullSignalString(String _signal) {
		String signal = _signal.trim();
		int index0 = signal.indexOf(" ");
		if (index0 != -1) {
			signal = signal.substring(index0+1, signal.length()).trim();
		}
		
		index0 = signal.indexOf("(");
		if (index0 != -1) {
			signal = signal.substring(0, index0).trim();
		}
		
		return signal;
	}
	
	public static int getNbOfValues(String _value) {
		int index = _value.indexOf('(');
		if (index == -1) {
			return -1;
		}
		
		int index0 = _value.indexOf(')');
		if (index0 == -1) {
			return -1;
		}
	
		String val = _value.substring(index+1, index0).trim();
		
		if (val.length() == 0) {
			return 0;
		}
		
		int nbChar = Conversion.nbChar(_value, ',');
		return nbChar+1;
	}
	
	public static String getValue(String _value, int _index) {
		int nbOfValues = getNbOfValues(_value);
		if (nbOfValues < 1) {
			return null;
		}
		
		if (_index >= nbOfValues) {
			return null;
		}
		
		int index0 = _value.indexOf('(');
		int index1 = _value.indexOf(')');
		
		String val = _value.substring(index0+1, index1).trim();
		
		String [] vals = val.split(",");
		return vals[_index].trim();
		
	}
}