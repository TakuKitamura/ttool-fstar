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
 * Class DeadlockItem
 * Data of an action on a simulation trace
 * Creation: 15/08/2004
 * @version 1.0 15/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;


import java.util.*;

public class DeadlockItem implements Comparable {
    private String name;
    private Vector origin;
    private Vector action;
    private String path;
    
    public DeadlockItem(String _name) {
        name = _name;
        origin = new Vector();
        action = new Vector();
    }
    
    //observers
    public String getName() {
        return name;
    }
 
    public String getOriginAction() {
        String s1, s2;
        StringBuffer ret = new StringBuffer();
        
        for(int i=0; i<origin.size(); i++) {
            s1 = (String)(origin.elementAt(i));
            s2 = (String)(action.elementAt(i));
            if (i != 0) {
                ret.append(", ");
            }
            ret.append("(");
            ret.append(s1);
            ret.append(", ");
            ret.append(s2);
            ret.append(")");
        }
        
        return new String(ret);
    }
    
    public String getPath() {
        return path;
    }
    
    
    // modifiers

    public void addOriginAction(String _origin, String _action) {
        origin.add(_origin);
        action.add(_action);
    }
    
    public void setPath(String _path) {
        path = _path;
    }
    
    // comparable interface
    public int compareTo(Object o) {
        if (!(o instanceof DeadlockItem)) {
            return 0;
        } else {
            return getName().compareTo(((DeadlockItem)o).getName());
        }
        
    }
    
}
