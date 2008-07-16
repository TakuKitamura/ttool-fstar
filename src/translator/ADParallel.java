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
 * Class ADParallel
 * Creation: 11/12/2003
 * @version 1.0 11/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;

import myutil.*;

public class ADParallel extends ADComponent implements NonBlockingADComponent, MultiIncomingElt {
    protected String valueGate;
    protected Vector synchroGate;
    protected Vector gateList;
    protected Vector component;
    protected Gate specialGate;
    protected boolean isMulti;
    
    public ADParallel() {
        nbNext = 100;
        gateList = new Vector();
        component = new Vector();
        valueGate = "[]";
    }
    
    public void setValueGate(String s) {
        valueGate = s;
    }
    
     public String getValueGate() {
       if ((synchroGate == null) ||(synchroGate.size() == 0)) {
            return valueGate;
        } else {
            Gate g;
            StringBuffer sb = new StringBuffer("[");
            for(int i=0; i<synchroGate.size(); i++) {
                g = (Gate)(synchroGate.elementAt(i));
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(g.getName());
            }
            sb.append("]");
            return new String(sb);
        }
     }
    
    public String getLotosValueGate() {
        if ((synchroGate == null) ||(synchroGate.size() == 0)) {
            return valueGate;
        } else {
            Gate g;
            StringBuffer sb = new StringBuffer("[");
            for(int i=0; i<synchroGate.size(); i++) {
                g = (Gate)(synchroGate.elementAt(i));
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(g.getLotosName());
            }
            sb.append("]");
            return new String(sb);
        }
    }
    
    public void setMulti(boolean b) {
        isMulti = b;
    }
    
    public boolean isMulti() {
        return isMulti;
    }
    
    public void setSpecialGate(Gate g) {
        specialGate = g;
    }
    
    public Gate getSpecialGate() {
        return specialGate;
    }
    
    public Vector getNewAllGateList() {
        Vector v = new Vector();
        int i;
        
        for(i=0; i<gateList.size(); i++) {
            v.add(gateList.elementAt(i));
        }
        
        return v;
    }
    
    public String getAction(ADComponent ad) {
        //System.out.println("Action on parallel");
        int index = component.indexOf(ad);
        if ((index >= 0) && (index < gateList.size())){
            Gate g = (Gate)(gateList.elementAt(index));
            return g.getName() + ";";
        }
        //System.out.println("No action on parallel " +index);
        return "";
    }
    
    public int nbGate() {
        return synchroGate.size();
    }
    
    public Vector getGateList() {
        return synchroGate;
    }
    
    public void addCoupleGateComponent(Gate g, ADComponent ad) {
        gateList.add(g);
        component.add(ad);
    }
    
    public Gate getGate(int index) {
        if (index < synchroGate.size()) {
            return (Gate)(synchroGate.elementAt(index));
        }
        return null;
    }
	
	public void removeSynchroGateIfApplicable(Gate g) {
		synchroGate.remove(g);
	}
    
    public String toString() {
        return "Parallel / Synchro (" + valueGate + ")";
    }
    
    public boolean isAValidMotif(TClass t) {
        //System.out.println("Is a valid motif!");
        if (valueGate == null) {
            valueGate = "[]";
        }
        String s = new String(valueGate);
        Vector gates = new Vector();
        
        // remove spaces
        s = Conversion.replaceAllChar(s, ' ', "");
        
        // checks for '['and ']'
        if (s.charAt(0) != '[') {
            return false;
        }
        
        if (s.charAt(s.length()-1) != ']') {
            return false;
        }
        
        s = s.substring(1, s.length()-1);
        //System.out.println("new Synchro gates: " + s);
        
        String [] array = s.split(",");
        
        // check gate name
        Gate g;
        for(int i=0; i<array.length; i++) {
            if (!array[i].equals("")) {
                g = t.getGateByName(array[i]);
                //System.out.println("gate = " + g.getName());
                if (g == null) {
                    return false;
                }
                if (gates.contains(g)) {
                    return false;
                } else {
                    //System.out.println("Adding gate = " + g.getName());
                    gates.addElement(g);
                }
            }
        }
        
        synchroGate = gates;
        //System.out.println("OK");

        /*for(int j=0; j<gates.size(); j++) {
                g = (Gate)(gates.elementAt(j));
                System.out.println("j=" + j + " gate=" + g.getName());
        }*/

        return true;
    }
    
   public ADComponent makeSame() {
      ADParallel adp = new ADParallel();
      adp.setValueGate(getValueGate());
      return adp;
    }
    
    
    
}

