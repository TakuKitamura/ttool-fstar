/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class Param
 * Creation: 11/12/2003
 * @version 1.1 11/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import translator.tojava.*;
import myutil.*;

public class Param {
    private String name;
    private String lotosName;
    private String type;
    private String value;
    private String access;
    
    public static final String NAT = "nat" ;
    public static final String BOOL = "bool" ;
	public static final String QUEUE_NAT = "Queue_nat";
    
    public Param(String _name, String _type, String _value) {
        name = _name;
        type = _type;
        value = _value;
        //System.out.println("New param : name=" + name + " value=" + value);
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setAccess(String s) {
        access = s;
        //System.out.println("Setting access of " + name + " to " + s);
    }
    
    public String getAccess() {
        return access;
    }
    
    public boolean isNat() {
      return (type.compareTo(NAT) == 0);
    }
	
	public boolean isBool() {
      return (type.compareTo(BOOL) == 0);
    }
	
	public boolean isQueueNat() {
      return (type.compareTo(QUEUE_NAT) == 0);
    }
    
    public String getValue() {
        if ((value == null) || (value.equals("")))   {
            return getDefaultValue();
        } else {
            return value;
        }
    }
    
    public String getDefaultValue() {
        if (type.equals(NAT)) {
            return "0";
        } else if (type.equals(BOOL)) {
            return "false";
        } else if (type.equals(QUEUE_NAT)) {
            return "nil";
		}
        return ""  ;
    }
    
    
    public String getTranslation() {
        return name + ":" + type;
    }
    
    public String getLotosTranslation() {
        return lotosName + ":" + type;
    }
    
    public String getLotosName() {
        return lotosName;
    }
    
    public void setLotosName(String _lotosName) {
        lotosName = _lotosName;
    }
	
	public static boolean isAValidParamName(String id) {
		 // test whether _id is a word
        
        if ((id == null) || (id.length() < 1)) {
            return false;
        }
        
        String lowerid = id.toLowerCase();
        boolean b1, b2, b3, b4, b5;
        b1 = (id.substring(0,1)).matches("[a-zA-Z]");
        b2 = id.matches("\\w*");
        b3 = !RTLOTOSKeyword.isAKeyword(lowerid);
        b5 = !JKeyword.isAKeyword(lowerid);
     
        return (b1 && b2 && b3 && b5);
	}
    
}
