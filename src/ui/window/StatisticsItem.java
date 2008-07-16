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
 * Class StatisticsItem
 * Data of an action on a simulation trace
 * Creation: 13/08/2004
 * @version 1.0 13/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.util.*;

public class StatisticsItem implements Comparable {
    private String name;
    private Integer occurence;
    private Vector originDestination;
    
    public StatisticsItem(String _name) {
        name = _name;
        originDestination = new Vector();
        occurence = new Integer(0);
    }
    
    //observers
    public String getName() {
        return name;
    }
    
    public Integer getOccurence() {
        return occurence;
    }
    
 
    public String getOriginDestination() {
        Point p;
        StringBuffer ret = new StringBuffer();
        
        for(int i=0; i<originDestination.size(); i++) {
            p = (Point)(originDestination.elementAt(i));
            if (i != 0) {
                ret.append(", ");
            }
            ret.append("(");
            ret.append(p.x);
            ret.append(", ");
            ret.append(p.y);
            ret.append(")");
        }
        
        return new String(ret);
    }
    
    
    // modifiers
    public void increaseOccurence() {
        occurence = new Integer(occurence.intValue() + 1);
    }
    
    public void addOriginDestination(int origin, int destination) {
        originDestination.add(new Point(origin, destination));
    }
    
    
    // comparable interface
    public int compareTo(Object o) {
        if (!(o instanceof StatisticsItem)) {
            return 0;
        } else {
            return getName().compareTo(((StatisticsItem)o).getName());
        }
        
    }
    
}
