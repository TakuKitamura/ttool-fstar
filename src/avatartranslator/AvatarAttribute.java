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
 * Class AvatarAttribute
 * Avatar attributes, either of blocks, or manipulated by signals / methods
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator;

import java.util.*;

import myutil.*;
import translator.*;
import translator.tojava.*;

public class AvatarAttribute extends AvatarElement{
    
    // Types of parameters
    private int type;
	private String initialValue;
	
    
	public AvatarAttribute(String _name, int _type, Object _referenceObject) {
		super(_name, _referenceObject);
		type = _type;
	}
	
	public void setInitialValue(String _initialValue) {
		initialValue = _initialValue;
	}
	
	public boolean hasInitialValue() {
		if (getInitialValue() == null) {
			return false;
		}
		return (!(getInitialValue().trim().length() == 0));
	}
	
	public String getInitialValue() {
		return initialValue;
	}
	
	public String getDefaultInitialValue() {
		return AvatarType.getDefaultInitialValue(type);
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isInt() {
		return (type == AvatarType.INTEGER);
	}
	
	public boolean isNat() {
		return (type == AvatarType.NATURAL);
	}
	
	public boolean isBool() {
		return (type == AvatarType.BOOLEAN);
	}
	
	public static boolean isAValidAttributeName(String id) {
		if ((id == null) || (id.length() < 1)) {
            return false;
        }
        
        String lowerid = id.toLowerCase();
        boolean b1, b2, b3, b4, b5;
        b1 = (id.substring(0,1)).matches("[a-zA-Z]");
        b2 = id.matches("\\w*");
        b3 = !RTLOTOSKeyword.isAKeyword(lowerid);
        b5 = !JKeyword.isAKeyword(lowerid);
        
	
		if ((lowerid.equals(AvatarType.getStringType(0).toLowerCase())) || (lowerid.equals(AvatarType.getStringType(1).toLowerCase())) || (lowerid.equals(AvatarType.getStringType(2).toLowerCase())) || (lowerid.equals(AvatarType.getStringType(3).toLowerCase())) || (lowerid.equals(AvatarType.getStringType(4).toLowerCase()))) {
			b4 = false;
		} else {
			b4 = true;
		}
		
        
        return (b1 && b2 && b3 && b4 && b5);
	}
	
	public String toString() {
		String ret = AvatarType.getStringType(type) + " " + getName();
		if (initialValue  == null) {
			return ret;
		}
		
		return ret + " = " + initialValue;
	}
}