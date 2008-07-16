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
 * Class TURTLE2TPN
 * Creation: 04/07/2006
 * @version 1.0 04/07/2006
 * @author Ludovic APVRILLE
 * @see
 */

package translator.totpn;

import java.util.*;

import tpndescription.*;
import myutil.*;
import translator.*;

public class TURTLE2TPN {
    
    private TPN tpn;
    private TURTLEModeling tm;
    private Vector warnings;
    private LinkedList tmpComponents;
    private LinkedList entryPlaces;
    private LinkedList exitPlaces;
    
    public TURTLE2TPN(TURTLEModeling _tm) {
        tm = _tm;
    }
    
    public TPN getTPN() {
        return tpn;
    }
    
    public void saveFiles(String path) throws FileException {
        tpn.saveInFile(path);
    }
    
    public void printTPN() {
       System.out.println(tpn.toString());
    }
    
    public String toString() {
        return tpn.toString();
    }
    
    public Vector getWarnings() {
        return warnings;
    }
    
    public TPN generateTPN(boolean _debug) {
        warnings = new Vector();
        tpn = new TPN();
        tmpComponents = new LinkedList();
        entryPlaces = new LinkedList();
        exitPlaces = new LinkedList();
        
        // Name initialization -> we reuse the names used by LOTOS specification
        MasterGateManager.reinitNameRestriction();
        tm.makeRTLOTOSName();
        tm.makeLOTOSName();
        
        // Work with tm modeling
        // For example, compact latencies together, etc.
        
        // Deal with tclasses
        translateTClasses();
        
        
        // Deal with relations
        
        
        // Deal with initial instances
        setMarking();
        
        
        // optimize PN
        tpn.optimize();
        
        
        
        return tpn;
    }  
    
    public void translateTClasses() {
        for(int i=0; i<tm.classNb(); i++) {
            translateTClass(tm.getTClassAtIndex(i));
        }
    }
    
    public void translateTClass(TClass t) {
        addPlaces(t);
        ADStart adstart = t.getActivityDiagram().getStartState();
        translateADComponents(t, adstart,  getEntryPlace(t));    
    }
    
    public void translateADComponents(TClass t, ADComponent adc, Place p) {
        Place p1;
        Transition t1;
        
        if (adc instanceof ADStart) {
            addComponentRef(adc, p, p);
            translateADComponents(t, adc.getNext(0), p);
        } else if (adc instanceof ADStop) {
            // Managed regularly -> join with an epsilon transition to the end place of the tclass
            t1 = newEpsilonTransition();
            t1.addOriginPlace(p);
            t1.addDestinationPlace(getExitPlace(t));
            addComponentRef(adc, p, p);
        } else if (adc instanceof ADDelay) {
            // Is delay value valid?
            ADDelay delay = (ADDelay)adc;
            int delayValue = getDelayValue(t, delay.getValue());
            if (delayValue == -1) {
                p1 = p;
            } else {
                p1 = newPlace();
                t1 = newEpsilonTransition();
                t1.addOriginPlace(p);
                t1.addDestinationPlace(p1);
                t1.setDelay(delayValue);
            }
            addComponentRef(adc, p, p1);
            translateADComponents(t, adc.getNext(0), p1);
        } else if (adc instanceof ADActionStateWithGate) {
            // Get action value -> should also be managed
            ADActionStateWithGate adswg = (ADActionStateWithGate)adc;
            p1 = newPlace();
            t1 = newTransition(t.getName() + "__" + adswg.getGate().getName());
            t1.addOriginPlace(p);
            t1.addDestinationPlace(p1);
            addComponentRef(adc, p, p1);
            translateADComponents(t, adc.getNext(0), p1);
        } else {
            // Operator is ignored
            warnings.add("Operator " + adc + " is not a recognized operator -> ignored");
            addComponentRef(adc, p, p);
            translateADComponents(t, adc.getNext(0), p);
        }
    }
    
    
    public Place newPlace() {
        Place p = new Place();
        tpn.addPlace(p);
        return p;
    }
    
    public Transition newEpsilonTransition() {
        Transition t = new Transition("epsilon");
        tpn.addTransition(t);
        return t;
    }
    
    public Transition newTransition(String label) {
        Transition t = new Transition(label);
        tpn.addTransition(t);
        return t;
    }
    
    public int getDelayValue(TClass t, String delay) {
        delay = delay.trim();
        int value = 0;
        
        // Is the delay a number value?
        try {
            value = Integer.parseInt(delay);
            return value;
        } catch (NumberFormatException nfe) {
        }
        
        // is it a variable? -> if so, return the initial value of this variable
        Param p = t.getParamByName(delay);
        
        if (p!= null) {
             try {
                value = Integer.parseInt(p.getValue());
                return value;
            } catch (NumberFormatException nfe) {
                p = null;
            }
        }
        
        if (p == null) {
            warnings.add("Delay (" + delay + ") is not a valid delay -> ignoring delay");
            return -1;
        }
        
        warnings.add("Delay (" + delay + ") is set as delay("+ value + ")"); 
        return value;
    }
    
    public void setMarking() {
        // For all tclasses -> search for its initial place -> if tclass is start, then, add a token
        TClass t;
        Place p;
        
        for(int i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            if (t.isActive()) {
                p = getEntryPlace(t);
                if (p != null) {
                    p.nbOfToken = 1;
                }
            }
        }
    }
    
    
    
    
    // Management of references between components and places
    
    public void addPlaces(Object o) {
        Place p1 = new Place();
        Place p2 = new Place();
        tpn.addPlace(p1);
        tpn.addPlace(p2);
        addComponentRef(o, p1, p2);
    }
    
    public void addComponentRef(Object o, Place p1, Place p2) {
        tmpComponents.add(o);
        entryPlaces.add(p1);
        exitPlaces.add(p2);
    }
    
    public Place getEntryPlace(Object o) {
        int index = tmpComponents.indexOf(o);
        if (index == -1) {
            return null;
        }
        return (Place)(entryPlaces.get(index));
    }
    
    public Place getExitPlace(Object o) {
        int index = tmpComponents.indexOf(o);
        if (index == -1) {
            return null;
        }
        return (Place)(exitPlaces.get(index));
    }
    
}