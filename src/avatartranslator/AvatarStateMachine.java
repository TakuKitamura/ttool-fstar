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
	
	public AvatarStartState getStartState() {
		return startState; 
	}
	
	public void addElement(AvatarStateMachineElement _element) {
		elements.add(_element);
	}
	
	public void removeElement(AvatarStateMachineElement _element) {
		elements.remove(_element);
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
		removeAllInternalStartStates();
		
		AvatarTransition at;
		
		at = getCompositeTransition();
		if (at != null) {
			TraceManager.addDev("*** Found composite transition: " + at.toString());
		}
		
		while((at = getCompositeTransition()) != null) {
			removeCompositeTransition(at);
		}
		
		removeAllSuperStates();
	}
	
	private void removeallSuperStates() {
		for(AvatarStateMachineElement element: elements) {
			element.setState(null);
		}
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
		
		Vector <AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();
		
		for(AvatarStateMachineElement element: elements) {
			if (element.hasInUpperState(state) == true) {
				// We found a candidate!
				if (element != _at) {
					v.add(element);
				}
			}
		}
		
		// Split avatar transitions
		for(AvatarStateMachineElement element: v) {
			if (element instanceof AvatarTransition) {
				splitAvatarTransition((AvatarTransition)element);
			}
		}
		
		for(AvatarStateMachineElement element: v) {
			adaptCompositeTransition(_at, element);
		}
		
		removeElement(_at);
		
	}
	
	private void splitAvatarTransition(AvatarTransition _at) {
		if (_at.getNbOfAction() > 1) {
			AvatarState as = new AvatarState("splitstate", null);
			AvatarTransition at = _at.basicCloneMe();
			_at.removeAllActionsButTheFirstOne();
			at.removeFirstAction();
			_at.removeAllNexts();
			_at.addNext(as);
			as.addNext(at);
			addElement(as);
			addElement(at);
			
			splitAvatarTransition(at);
		}
	}
	
	private void adaptCompositeTransition(AvatarTransition _at, AvatarStateMachineElement _element) {
		AvatarState as;
		AvatarTransition at;
		LinkedList<AvatarStateMachineElement> ll;
		
		// It cannot be a start state since they have been previously removed ..
		if ((_element instanceof AvatarActionOnSignal) || (_element instanceof AvatarStopState)){
			ll = getPreviousElementsOf(_element);
			for(AvatarStateMachineElement element: ll) {
				if (element instanceof AvatarTransition) {
					as = new AvatarState("internalstate", null);
					element.removeNext(_element);
					element.addNext(as);
					at = new AvatarTransition("internaltransition", null);
					addElement(at);
					at.addNext(_element);
					as.addNext(at);
					addElement(as);
					at = _at.cloneMe();
					addElement(at);
					as.addNext(at);
				} else {
					// Badly formed machine!
					TraceManager.addError("Badly formed sm (removing composite transition)");
				}
			}
			
		} else if (_element instanceof AvatarState) {
			at = _at.cloneMe();
			addElement(at);
			_element.addNext(at);
		} else if (_element instanceof AvatarTransition) {
			// Nothing to do since they shall have been split before
		} else {
			// Nothing to do either
		}
	}
	
	
	// Return the first previous element met. Shall be used preferably only for transitions
	private AvatarStateMachineElement getPreviousElementOf(AvatarStateMachineElement _elt) {
		for(AvatarStateMachineElement element: elements) {
			if (element.hasNext(_elt)) {
				return element;
			}
		}
		
		return null;
	}
	
	private LinkedList<AvatarStateMachineElement> getPreviousElementsOf(AvatarStateMachineElement _elt) {
		LinkedList<AvatarStateMachineElement> ll = new LinkedList<AvatarStateMachineElement>();
		for(AvatarStateMachineElement element: elements) {
			if (element.hasNext(_elt)) {
				ll.add(element);
			}
		}
		
		return ll;
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
	
	
	// All transitions reaching a state that has an internal start state
	// shall in fact go directly to the nexts of the start state
	public void removeAllInternalStartStates() {
		// identify allstart state
		LinkedList<AvatarStartState> ll = new LinkedList<AvatarStartState>();
		for(AvatarStateMachineElement element: elements) {
			if ((element instanceof AvatarStartState) && (element.getState() != null)) {
				TraceManager.addDev("-> -> found an internal state state");
				ll.add((AvatarStartState)element);
			}
		}
		
		AvatarState as0;
		LinkedList<AvatarStateMachineElement> le;
		for(AvatarStartState as: ll) {
			AvatarState astate = as.getState();
			if (as != null) {
				le = getPreviousElementsOf(astate);
				if (le.size() > 0) {
					as0 = new AvatarState(astate.getName() + "__external", astate.getReferenceObject());
					as0.addNext(as.getNext(0));
					for(AvatarStateMachineElement element: le) {
						if (element instanceof AvatarTransition) {
							element.removeAllNexts();
							element.addNext(as0);
							as0.setState(element.getState());
						} else {
							TraceManager.addDev("Badly formed state machine");
						}
					}
					// Remove the start state and its next transition
					removeElement(as);
					addElement(as0);
					TraceManager.addDev("-> -> removed an internal state state!");
				} else {
					TraceManager.addDev("Badly formed state machine");
				}
			}
		}
		
	}
	
	public void removeAllSuperStates() {
		for(AvatarStateMachineElement element: elements) {
			element.setState(null);
		}
	}
	
	public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
		for(AvatarStateMachineElement element: elements) {
			if(element.getReferenceObject() == _o) {
				return element;
			}
		}
		return null;
	}
    

}