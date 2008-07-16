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
 * Class Relation
 * Creation: 2001
 * @version 1.1 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;

public class Relation {
    // composition operator
    public static final int WAT = 6;
    public static final int PRE = 5;
    public static final int SEQ = 4;
    public static final int INV = 3;
    public static final int SYN = 2;
    public static final int PAR = 1;
    
    public static int gateNumber;
    
    // attributes
    public int type;
    private String name;
    public TClass t1;
    public TClass t2;
    public Vector gatesOfT1;
    public Vector gatesOfT2;
    public boolean navigation;
    
    public Relation(int type, TClass t1, TClass t2, boolean navigation) {
        this.type = type;
        this.t1 = t1;
        this.t2 = t2;
        switch(type) {
            case 1:
                name = "Parallel";
                break;
            case 2:
                name = "Synchro";
                break;
            case 3:
                name = "Invocation";
                break;
            case 4:
                name = "Sequence";
                break;
            case 5:
                name = "Preemption";
                break;
            case 6:
                name = "Watchdog";
                break;
            default:
                name = "Unknown";
        }
        gatesOfT1 = new Vector();
        gatesOfT2 = new Vector();
    }
    
    public static boolean hasNavigation(int type) {
        switch(type) {
            case 1:
                return false;
            case 2:
                return false;
            case 3:
                return true;
            case 4:
                return true;
            case 5:
                return true;
            case 6:
                return true;
            default:
                return false;
        }
    }
	
    public void addGates(Gate g1, Gate g2) {
        gatesOfT1.add(g1);
        gatesOfT2.add(g2);
    }
	
	public void removeGates(Gate g1, Gate g2) {
		gatesOfT1.removeElement(g1);
		gatesOfT2.removeElement(g1);
		gatesOfT1.removeElement(g2);
		gatesOfT2.removeElement(g2);
	}
    
    public void addGatesIfApplicable(Gate g1, Gate g2) {
        if (!gatesOfT1.contains(g1) && !gatesOfT2.contains(g2)) {
            gatesOfT1.add(g1);
            gatesOfT2.add(g2);
        }
    }
    
    public String generateGateName() {
        int tmp = Relation.gateNumber;
        Relation.gateNumber ++;
        return "g" + tmp;
    }
    
    public static String translation(int type) {
        String name;
        switch(type) {
            case 1:
                name = "|||";
                break;
            case 2:
                name = "|[ ]|";
                break;
            case 3:
                name = "|[ ]|";
                break;
            case 4:
                name = ">>";
                break;
            case 5:
                name = "[>";
                break;
            case 6:
                name = "Watchdog";
                break;
            default:
                name = " ";
        }
        return name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean hasGate() {
        return ((gatesOfT1.size() > 0) && (gatesOfT2.size() > 0));
    }
    
    public Gate correspondingGate(Gate g, TClass tc) {
        if (!gatesOfRelation(g, tc)) {
            return null;
        } else {
            int index;
            if (tc == t1) {
                index = gatesOfT1.indexOf(g);
                if (index == -1)
                    return null;
                return (Gate)(gatesOfT2.elementAt(index));
            } else {
                index = gatesOfT2.indexOf(g);
                if (index == -1)
                    return null;
                return (Gate)(gatesOfT1.elementAt(index));
            }
        }
        
    }
    
    public boolean gatesOfRelation(Gate g, TClass tc) {
        if (tc == t1) {
            return 	gatesOfT1.contains(g);
        }
        
        if (tc == t2) {
            return 	gatesOfT2.contains(g);
        }
        
        return false;
    }
    
    public boolean gatesConnected(Gate g1, Gate g2) {
        if ((!(gatesOfT1.contains(g1))) ||(!(gatesOfT2.contains(g2)))) {
            return false;
        }
        int index1 = gatesOfT1.indexOf(g1);
        int index2 = gatesOfT2.indexOf(g2);
        
        return (index1 == index2);
    }
    
    public TClass otherTClass(TClass tc) {
        if (tc == t1) {
            return t2;
        } else {
            return t1;
        }
    }
    
    public void print() {
        System.out.println("Relation: " + name +" between   " + t1.getName() + "   and   " + t2.getName());
    }
    
    public void printToStringBuffer(StringBuffer sb) {
        sb.append("Relation: " + name +" between   " + t1.getName() + "   and   " + t2.getName() + "\n");
    }
}
