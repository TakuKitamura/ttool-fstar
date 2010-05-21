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
 * Class AvatarStateMachine
 * State machine, with composite states
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator;

import java.util.*;

import myutil.*;

public class AvatarStateMachine extends AvatarElement {
    
	protected LinkedList<AvatarStateMachineElement> elements;
	protected AvatarStartState startState;
    
	
    public AvatarStateMachine(String _name, Object _referenceObject) {
		super(_name, _referenceObject);
        elements = new LinkedList<AvatarStateMachineElement>();
    }
	
	public void setStartState(AvatarStartState _state) {
		startState = _state; 
	}
	
	public void addElement(AvatarStateMachineElement _element) {
		elements.add(_element);
	}
	
	public LinkedList<AvatarStateMachineElement> getListOfElements() {
		return elements;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("State machine Id=" + getID() + "\n");
		
		for(AvatarStateMachineElement element: elements) {
			sb.append(element.toString() + "\n");
		}
		
		return sb.toString();
	}
	
	public void removeCompositeStates() {
		AvatarTransition at;
		
		at = getCompositeTransition();
		if (at != null) {
			TraceManager.addDev("*** Found composite transition: " + at.toString());
		}
		
		/*while((at = getCompositeTransition()) != null) {
			removeCompositeTransition(at);
		}*/
	}
	
	private AvatarTransition getCompositeTransition() {
		for(AvatarStateMachineElement element: elements) {
			if (element instanceof AvatarTransition) {
				if ((isACompositeTransition((AvatarTransition)element))) {
					return (AvatarTransition)element;
				}
			}
		}
		
		return null;
	}
	
	// Checks whether the previous element is a state with an internal state machine
	private boolean isACompositeTransition(AvatarTransition _at) {
		AvatarStateMachineElement element = getPreviousElementOf(_at);
		if (element == null) {
			return false;
		}
		
		if (!(element instanceof AvatarState)) {
			return false;
		}
		
		AvatarState state = (AvatarState)element;
		return hasInternalComponents(state);
	}
	
	private boolean hasInternalComponents(AvatarState _state) {
		for(AvatarStateMachineElement element: elements) {
			if (element.getState() == _state) {
				return true;
			}
		}
		
		return false;
	}
	
	private void removeCompositeTransition(AvatarTransition _at) {
		AvatarState state = (AvatarState)(getPreviousElementOf(_at));
		for(AvatarStateMachineElement element: elements) {
			if (element.hasInUpperState(state) == true) {
				// We found a candidate!
			}
		}
		// For each element elt in the composite state at the origin of at:
		// make a new transition from elt to the destinaton of _at
		// Si elt un peu special, need to add also intermediate states
		// add this transition
		
		// Remove the old transition
		
		// Shall work!
		
	}
	
	private AvatarStateMachineElement getPreviousElementOf(AvatarStateMachineElement _elt) {
		for(AvatarStateMachineElement element: elements) {
			if (element.hasNext(_elt)) {
				return element;
			}
		}
		
		return null;
	}
	
	public AvatarState getStateWithName(String _name) {
		for(AvatarStateMachineElement element: elements) {
			if (element instanceof AvatarState) {
				if (element.getName().compareTo(_name) == 0) {
					return (AvatarState)element;
				}
			}
		}
		return null;
	}
    

}