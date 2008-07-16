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
 * Class JAttribute
 * Creation: 03/03/2005
 * @version 1.1 03/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package translator.tojava;

import translator.*;

public class JAttribute {
    private String access;
    private String name;
    private String javaName;
    private String type;
    private String value;
    
    public JAttribute(String _name, String _type, String _value) {
        name = _name;
        javaName = name;
        if (!(_type.equals("nat"))) {
            type = JKeyword.BOOLEAN;
        } else {
            type = JKeyword.INTEGER;
        }
        value = _value;
        access = "private";
    }
    
    public JAttribute(Param p) {
       access = p.getAccess();
        
        name = p.getName();
        javaName = name;
        
        if (p.getType().equals(Param.BOOL)) {
            type = JKeyword.BOOLEAN;
        } else {
            type = JKeyword.INTEGER;
        }
        
        value = p.getValue();
    }
    
    public JAttribute(Param p, boolean longforint) {
        access = p.getAccess();
        
        name = p.getName();
        javaName = name;
        
        if (p.getType().equals(Param.BOOL)) {
            type = JKeyword.BOOLEAN;
        } else {
            if (longforint) {
                type = JKeyword.LONG;
            } else {
                type = JKeyword.INTEGER;
            }
        }
        
        value = p.getValue();
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getValue() {
        if ((value == null) || (value.equals("")))   {
            return getDefaultValue();
        } else {
            return value;
        }
    }
    
    public String getDefaultValue() {
        if (type.equals(JKeyword.INTEGER)) {
            return "0";
        } else if (type.equals(JKeyword.BOOLEAN)) {
            return "false";
        }
        return ""  ;
    }
    
    public String getJavaDeclaration() {
        if (access == null) {
            access = "private";
        }
        return access + " " + type + " " + javaName + " = " + getValue() + JKeyword.END_OP;
    }
    
    public String getJavaName() {
        return javaName;
    }
    
    public void setJavaName(String _javaName) {
        javaName = _javaName;
    }
    
    public void setAccess(String _access) {
        access = _access;
    }
}