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
 * Class AvatarStateMachineElement
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package avatartranslator;

import java.util.*;

import myutil.*;


public abstract class AvatarStateMachineElement extends AvatarElement {
	
	private LinkedList<AvatarStateMachineElement> nexts;
	private AvatarState myState;
	
    public AvatarStateMachineElement(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
		nexts = new LinkedList<AvatarStateMachineElement>();
    }
	
	public void addNext(AvatarStateMachineElement _element) {
		nexts.add(_element);
	}
	
	public AvatarStateMachineElement getNext(int _index) {
		if (_index < nexts.size()) {
			return nexts.get(_index);
		}
		return null;
	}
	
	public void setState(AvatarState _as) {
		myState = _as; 
	}
	
	public AvatarState getState() {
		return myState;
	}
	
	public boolean hasInStrictUpperState(AvatarState _as) {
		if (getState() != null) {
			return getState().hasInUpperState(_as);
		}
		
		return false;
	}
	
	public boolean hasInUpperState(AvatarState _as) {
		if (getState() == _as) {
			return true;
		}
		
		if (getState() != null) {
			return getState().hasInUpperState(_as);
		}
		
		return false;
	}
	
	public boolean inAnUpperStateOf(AvatarState _state) {
		if (_state == null) {
			return false;
		}
		
		AvatarState as = getState();
		if (as == null) {
			return true;
		}
		
		while((_state = _state.getState()) != null) {
			if (_state == as) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public String toString() {
		String ret = getName() + " ID=" + getID();
		if (myState == null) {
			ret += " / top level operator\n";
		} else {
			ret += " / in state " + myState.getName() + " ID=" + myState.getID() + "\n"; 
		}
		
		ret += "nexts= ";
		int cpt=0;
		for(AvatarStateMachineElement element: nexts) {
			ret += cpt + ":" + element.getName() + "/ ID=" + element.getID() + " ";
			cpt ++;
		}
		
		ret += specificToString();
		
		return ret;
	}
	
	public String specificToString() {
		return "";
	}
	
	public int nbOfNexts() {
		return nexts.size();
	}
	
	public boolean hasNext(AvatarStateMachineElement _elt) {
		return nexts.contains(_elt);
	}
	
	public void removeNext(AvatarStateMachineElement _elt) {
		nexts.remove(_elt);
	}
	
	public void replaceAllNext(AvatarStateMachineElement oldone, AvatarStateMachineElement newone) {
		if (nexts.contains(oldone)) {
			LinkedList<AvatarStateMachineElement> oldnexts = nexts;
			nexts = new LinkedList<AvatarStateMachineElement>();
			for(AvatarStateMachineElement elt: oldnexts) {
				if (elt == oldone) {
					nexts.add(newone);
				} else {
					nexts.add(oldone);
				}
			}
		}
	}
	
	public void removeAllNexts() {
		nexts.clear();
	}
	
	public boolean followedWithAnActionOnASignal() {
		AvatarStateMachineElement element = getNext(0);
		if (element == null) {
			return false;
		}
		
		return (element instanceof AvatarActionOnSignal);
	}
	
	public abstract AvatarStateMachineElement basicCloneMe();
	
	
	// Guard with an id and not(id)
	public boolean hasElseChoiceType1() {
		if (nexts.size() != 2) {
			return false;
		}
		
		AvatarStateMachineElement elt1, elt2;
		
		elt1 = getNext(0);
		elt2 = getNext(1);
		
		if ( (!(elt1 instanceof AvatarTransition)) || (!(elt2 instanceof AvatarTransition))) {
			return false;
		}
		
		AvatarTransition at1, at2;
		
		at1 = (AvatarTransition)elt1;
		at2 = (AvatarTransition)elt2;
		
		if ((!(at1.isGuarded())) || (!(at2.isGuarded()))) {
			return false;
		}
		
		String g1, g2;
		g1 = at1.getGuard();
		g2 = at2.getGuard();
		
		g1 = Conversion.replaceAllString(g1, "[", "");
		g1 = Conversion.replaceAllString(g1, "]", "").trim();
		g1 = Conversion.replaceAllString(g1, " ", "");
		
		g2 = Conversion.replaceAllString(g2, "[", "");
		g2 = Conversion.replaceAllString(g2, "]", "").trim();
		g2 = Conversion.replaceAllString(g2, " ", "");
		
		String g, n;
		
		if (g1.startsWith("not(")) {
			n = g1;
			g = g2;
		} else {
			g = g1;
			n = g2;
		}
		
		/*if (!AvatarAttribute.isAValidAttributeName(g.trim())) {
			return false;
		}*/
		
		if (n.substring(4, n.length()-1).compareTo(g) != 0) {
			return false;
		}
		
		
		return true;
	}
	
    
}