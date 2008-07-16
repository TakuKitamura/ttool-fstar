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
 * Class State
 * Creation: 23/03/2006
 * @version 1.0 23/03/2006
 * @author Ludovic APVRILLE
 * @see
 */

package automata;

import java.util.*;


public class State {
    private String name;
    private LinkedList transitions;
    
    public State(String _name) {
         transitions = new LinkedList();
         name = _name;
    }
    
    public int nbOfTransitions() {
        return transitions.size();
    }
    
    public LinkedList getTransitions() {
        return transitions;
    }
    
    public Transition getTransition(int index) {
        return (Transition)(transitions.get(index));
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String _name) {
        name = _name;
    }
    
    public void addTransition(Transition t) {
        //System.out.println("Adding transition to state " + getName() + " transition=" + t.getValue() + " to " + t.getNextState().getName());
        transitions.add(t);
    }
    
    public String toAUT() {
        StringBuffer sb = new StringBuffer("");
        Transition tr;
        
        ListIterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            tr = (Transition)(iterator.next());
            sb.append("(" + name + ", \"" + tr.getValue() + "\", " + tr.getNextState().getName() + ")\n");
        }
        return new String(sb);
    }
    
}