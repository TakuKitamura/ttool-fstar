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
 * Class Transition
 * Creation: 04/07/2006
 * @version 1.1 04/07/2006
 * @author Ludovic APVRILLE
 * @see
 */

package tpndescription;

import java.util.*;

public class Transition {
    public static int INDEX  = 0;
    
    public String name;
    public String label; /* name of the synchro gate, name of the delay, etc.) */
    public String predicat; /* action to be executed before the transition is fired */
    public int intervalMin = 0;
    // Default interval: "[0, w[
    public boolean minIncluded = true; // "["
    public int intervalMax = -1; // -1 assumes that it is infinite
    public boolean maxIncluded = false; // "["
    public String action; /* action on a variable for example */
    public String infoSynchro; /* action coming along with synchro; for example !x?y */
    
    private LinkedList originPlaces;
    private LinkedList destinationPlaces;
    
    public Transition(String _label) {
        label = _label;
        name = generateName();
        originPlaces = new LinkedList();
        destinationPlaces = new LinkedList();
    }
    
    public void addOriginPlace(Place p) {
        originPlaces.add(p);
    }
    
    public void addDestinationPlace(Place p) {
        destinationPlaces.add(p);
    }
    
    public void setDelay(int delay) {
        intervalMin = delay;
        intervalMax = delay;
        minIncluded = true;
        maxIncluded = true;
    }
    
    public String generateName() {
        int index = INDEX;
        INDEX ++;
        return "t" + index;
    }
    
    public String toString() {
        return  name + " " + getStringInterval();
    }
    
    public String toTINAString() {
        String s = "tr " + name + " ";
        if (label != null) {
            s += ": " + label + " ";
        }
        s += getStringInterval() + " " + getStringOriginPlaces() + "-> " + getStringDestinationPlaces();
        return s;
    }
    
    public String getStringInterval() {
        String s = "";
        if (minIncluded) {
            s+="[";
        } else {
            s+="]";
        }
        s+=intervalMin;
        s+= ", ";
        if (intervalMax == -1) {
            s+="w";
        } else {
            s+=intervalMax;
        }
        if (maxIncluded) {
            s+="]";
        } else {
            s+="[";
        }
        return s;
    }
    
    public String getStringOriginPlaces() {
        return getStringPlaces(originPlaces);
    }
    
    public String getStringDestinationPlaces() {
        return getStringPlaces(destinationPlaces);
    }
    
    // Put a white space at the end
    public String getStringPlaces(LinkedList list) {
        String s = "";
        ListIterator iterator = list.listIterator();
        
        while(iterator.hasNext()) {
            s += (iterator.next()).toString() + " ";
        }
        return s;
    }
    
    
}