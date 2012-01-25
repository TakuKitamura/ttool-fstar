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
 * Class AvatarTransition
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package avatartranslator;

import java.util.*;

import myutil.*;


public class AvatarTransition extends AvatarStateMachineElement {
	private String guard = "[ ]";
	private String minDelay = "", maxDelay = "";
	private String minCompute = "", maxCompute = "";
	
	private LinkedList<String> actions; // actions on variable, or method call
	
    public AvatarTransition(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
		actions = new LinkedList<String>();
    }
	
	public String getGuard() {
		return guard;
	}
	
	public void setGuard(String _guard) {
		guard = _guard;
	}
	
	public void addGuard(String _g) {
		guard = "(" + guard + ") and (" + _g + ")";
	}
	
	public int getNbOfAction() {
		return actions.size();
	}
	
	public String getAction(int _index) {
		return actions.get(_index);
	}
	
	public void addAction(String _action) {
		actions.add(_action);
	}
	
	public void setDelays(String _minDelay, String _maxDelay) {
		minDelay = _minDelay;
		maxDelay = _maxDelay;
	}
	
	public void setComputes(String _minCompute, String _maxCompute) {
		minCompute = _minCompute;
		maxCompute = _maxCompute;
	}
	
	public String getMinDelay() {
		return minDelay;
	}
	
	public String getMaxDelay() {
		if (maxDelay.trim().length() ==0) {
				return getMinDelay();
		}
		return maxDelay;
	}
	
	public String getMinCompute() {
		return minCompute;
	}
	
	public String getMaxCompute() {
		if (maxCompute.trim().length() ==0) {
				return getMinCompute();
		}
		return maxCompute;
	}
	
	public boolean hasElseGuard() {
		if (guard == null) {
			return false;
		}
		
		return AvatarSpecification.isElseGuard(guard);
	}
	
	public boolean hasNonDeterministicGuard() {
		if (guard == null) {
			return false;
		}
		
		String tmp = Conversion.replaceAllChar(guard, ' ', "").trim();
		
		return tmp.compareTo("[]") == 0;
		
	}
	
	public boolean isEmpty() {
		if (hasDelay() || hasCompute()) {
			return false;
		}
		
		return (actions.size()  == 0);
	}

	
	public AvatarTransition cloneMe() {
		AvatarTransition at = new AvatarTransition(getName(), getReferenceObject());
		at.setGuard(getGuard());
		at.setDelays(getMinDelay(), getMaxDelay());
		at.setComputes(getMinCompute(), getMaxCompute());
		
		for(int i=0; i<getNbOfAction(); i++) {
			at.addAction(getAction(i));
		}
		
		for(int i=0; i<nbOfNexts(); i++) {
			at.addNext(getNext(i));
		}
		
		return at;
	}
	
	public AvatarStateMachineElement basicCloneMe() {
		AvatarTransition at = new AvatarTransition(getName() + "_clone", getReferenceObject());
		
		for(int i=0; i<getNbOfAction(); i++) {
			at.addAction(getAction(i));
		}
		
		at.setComputes(getMinCompute(), getMaxCompute());
		
		return at;
	}
	
	public void removeAllActionsButTheFirstOne() {
		if (actions.size() < 2) {
			return;
		}
		String action = actions.get(0);
		actions.clear();
		actions.add(action);
	}
	
	public void removeFirstAction() {
		actions.remove(0);
	}
	
	public void removeAllActions() {
		actions.clear();
	}
	
	// No actions
	//public boolean isAGuardTransition() {
	//}
	
	public boolean isGuarded() {
		if (guard == null) {
			return false;
		}
		
		if (guard.trim().length() == 0) {
			return false;
		}
		 
		String s = Conversion.replaceAllString(guard, " ", "").trim();
		if (s.compareTo("[]") == 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean hasDelay() {
		if (minDelay.trim().length() == 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean hasCompute() {
		if (minCompute.trim().length() ==0) {
			return false;
		}
		return true;
	}
	
	public boolean hasActions() {
		if (actions.size() == 0) {
			return false;
		}
		
		for(String s: actions) {
			if (s.trim().length() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public String specificToString() {
		String ret = "";
		if (hasDelay()) {
			ret += "minDelay=" + getMinDelay() + " maxDelay=" + getMaxDelay() + "\n"; 
		}
		
		if (hasCompute()) {
			ret += "minCompute=" + getMinCompute() + " maxcompute=" + getMaxCompute() + "\n";  
		}
		
		for(String s: actions) {
			if (s.trim().length() > 0) {
				ret += s.trim() + " / ";
			}
		}
		
		if (ret.length() > 0) {
			ret = "\n" + ret;
		}
		
		return ret;
	}
	
	
	// Assumes actions are correctly formatted
	public boolean hasMethodCall() {
		
		
		for(String action: actions) {
			if (isAMethodCall(action)) {
				return true;
			}
		}
		return false;
			
	}
	
	public static boolean isAMethodCall(String _action) {
		int index;
		index = _action.indexOf("=");
		
		// Method of the form f(...)
		if (index == -1) {
			return true;
		}
		
		// Method of the form x = f(...)
		_action = _action.substring(index+1, _action.length()).trim();
		index = _action.indexOf("(");
		if (index != -1) {
			_action = _action.substring(0, index).trim();
			if (_action.length() == 0) {
				return false;
				
			}
			boolean b1 = (_action.substring(0,1)).matches("[a-zA-Z]");
			boolean b2 = _action.matches("\\w*");
			if (b1 && b2) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getNiceName() {
		if (isGuarded())
			return "Transition (guard=" + guard + ", ...)";
		
		if (hasDelay()) 
			return "Transition (delay=(" + minDelay + ", " + maxDelay + "), ...)";
				
		if (actions.size() > 0) {
			return "Transition (" + actions.get(0) + ", ...)";
		}
		
		return "Empty transition";
	}
    
    public static String getVariableInAction(String action) {
        action = action.trim();
        int index = action.indexOf("=");
        if (index == -1) {
            return action;
        }
        
        return action.substring(0, index).trim();
    }
	
	
	
	
	
    
}