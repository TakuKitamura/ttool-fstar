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
	
	public void removeCompositeStates(AvatarBlock _block) {
		// Contains in odd index: composite state
		// even index: new state replacing the start state
		
		LinkedList <AvatarState> lists = removeAllInternalStartStates();
		
		
		AvatarTransition at;
		
		/*at = getCompositeTransition();
		if (at != null) {
			TraceManager.addDev("*** Found composite transition: " + at.toString());
		}*/
		
		LinkedList<AvatarStateMachineElement> toRemove = new LinkedList<AvatarStateMachineElement>();
		
		while((at = getCompositeTransition()) != null) {
			//TraceManager.addDev("*** Found composite transition: " + at.toString());
			//TraceManager.addDev(_block.toString());
			if (!(toRemove.contains(getPreviousElementOf(at)))) {
				toRemove.add(getPreviousElementOf(at));
			}
			removeCompositeTransition(at, _block);
			
		}
		
		for(AvatarStateMachineElement asme: toRemove) {
			removeElement(asme);
		}
		
		//TraceManager.addDev(_block.toString());
		
		removeAllSuperStates();
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
	
	private void removeCompositeTransition(AvatarTransition _at, AvatarBlock _block) {
		AvatarState state = (AvatarState)(getPreviousElementOf(_at));
		
		removeStopStatesOf(state);
		
		// Remove "after" and replace them with timers
		AvatarTransition at = removeAfter(_at, _block);
		
		// Put state after transition
		modifyAvatarTransition(at);
		
		Vector <AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();
		
		for(AvatarStateMachineElement element: elements) {
			//TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
			if (element instanceof AvatarTransition) {
				//TraceManager.addDev("at? element=" + element);
				if (element.getNext(0).hasInUpperState(state) == true) {
					if (getPreviousElementOf(element).hasInUpperState(state) == true) {
						v.add(element);
					}
				}
			} else if (element.hasInUpperState(state) == true) {
				// We found a candidate!
				if (element != _at) {
					v.add(element);
				}
			}
		}
		
		//TraceManager.addDev("*** Analyzing components in state " + state);
		// Split avatar transitions
		for(AvatarStateMachineElement element: v) {
			TraceManager.addDev(">" + element + "<");
			if (element instanceof AvatarTransition) {
				splitAvatarTransition((AvatarTransition)element, state);
			}
		}
		
		//TraceManager.addDev("\nAdding new elements in state");
		v.clear();
		for(AvatarStateMachineElement element: elements) {
			//TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
			if (element.hasInUpperState(state) == true) {
				// We found a candidate!
				if ((element != _at) && (element != at)) {
					v.add(element);
				}
			}
		}
		
		
		for(AvatarStateMachineElement element: v) {
			adaptCompositeTransition(at, element, 0);
		}
		
		removeElement(at);
		
	}
	
	private void splitAvatarTransition(AvatarTransition _at, AvatarState _currentState) {
		/*if (_at.hasCompute()) {
			AvatarState as0 = new AvatarState("splitstate0", null);
			AvatarState as1 = new AvatarState("splitstate1", null);
			
			
			
			AvatarTransition at = _at.basicCloneMe();
			_at.removeAllActions();
			_at.removeAllNexts();
			_at.addNext(as);
			as.addNext(at);
			addElement(as);
			addElement(at);
			splitAvatarTransition(at);
		}*/
		
		TraceManager.addDev(" - - - - - - - - - - Split transition nbofactions=" + _at.getNbOfAction());
		if (_at.getNbOfAction() > 1) {
			TraceManager.addDev("New split state");
			AvatarState as = new AvatarState("splitstate", null);
			as.setState(_currentState);
			AvatarTransition at = (AvatarTransition)(_at.basicCloneMe());
			_at.removeAllActionsButTheFirstOne();
			at.removeFirstAction();
			at.addNext(_at.getNext(0));
			_at.removeAllNexts();
			_at.addNext(as);
			as.addNext(at);
			addElement(as);
			addElement(at);
			
			splitAvatarTransition(at, _currentState);
		}
	}
	
	private void adaptCompositeTransition(AvatarTransition _at, AvatarStateMachineElement _element, int _transitionID) {
		AvatarState as;
		AvatarTransition at;
		LinkedList<AvatarStateMachineElement> ll;
		String tmp;
		
		// It cannot be a start / stop state since they have been previously removed ..
		if (_element instanceof AvatarActionOnSignal) {
			ll = getPreviousElementsOf(_element);
			for(AvatarStateMachineElement element: ll) {
				if (element instanceof AvatarTransition) {
					tmp = findUniqueStateName("internalstate__");
					TraceManager.addDev("Creating state with name=" + tmp);
					as = new AvatarState(tmp, null);
					element.removeNext(_element);
					element.addNext(as);
					at = new AvatarTransition("internaltransition", null);
					addElement(at);
					at.addNext(_element);
					as.addNext(at);
					addElement(as);
					
					at = cloneCompositeTransition(_at);
					addElement(at);
					as.addNext(at);
					
				} else {
					// Badly formed machine!
					TraceManager.addError("Badly formed sm (removing composite transition)");
				}
			}
			
		} else if (_element instanceof AvatarState) {
			at = cloneCompositeTransition(_at);
			//addElement(at);
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
	public LinkedList<AvatarState> removeAllInternalStartStates() {
		// identify allstart state
		LinkedList<AvatarStartState> ll = new LinkedList<AvatarStartState>();
		
		LinkedList<AvatarState> removedinfos = new LinkedList<AvatarState>();
		
		for(AvatarStateMachineElement element: elements) {
			if ((element instanceof AvatarStartState) && (element.getState() != null)) {
				//TraceManager.addDev("-> -> found an internal state state");
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
					as0 = new AvatarState("entrance__" + astate.getName(), astate.getReferenceObject());
					as0.addNext(as.getNext(0));
					as0.setState(astate);
					for(AvatarStateMachineElement element: le) {
						if (element instanceof AvatarTransition) {
							element.removeAllNexts();
							element.addNext(as0);
						} else {
							TraceManager.addDev("Badly formed state machine");
						}
					}
					
					removedinfos.add(as.getState());
					removedinfos.add(as0);
					
					// Remove the start state and its next transition
					removeElement(as);
					addElement(as0);
					
					//TraceManager.addDev("-> -> removed an internal state state!");
				} else {
					TraceManager.addDev("Badly formed state machine");
				}
			}
		}
		
		return removedinfos;
		
	}
	
	public void removeAllSuperStates() {
		for(AvatarStateMachineElement element: elements) {
			element.setState(null);
		}
	}
	
	public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
		for(AvatarStateMachineElement element: elements) {
			if(element.hasReferenceObject(_o)) {
				return element;
			}
		}
		return null;
	}
	
	// Return true iff at least one timer was removed
	public boolean removeTimers(AvatarBlock _block, String timerAttributeName) {
		AvatarSetTimer ast;
		AvatarTimerOperator ato;
		
		LinkedList<AvatarStateMachineElement> olds = new LinkedList<AvatarStateMachineElement>();
		LinkedList<AvatarStateMachineElement> news = new LinkedList<AvatarStateMachineElement>();
		
		
		for(AvatarStateMachineElement elt: elements) {
			// Set timer...
			if (elt instanceof AvatarSetTimer) {
				ast = (AvatarSetTimer)elt;
				AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), _block.getAvatarSignalWithName("set__" + ast.getTimer().getName()), elt.getReferenceObject());
				aaos.addValue(timerAttributeName);
				olds.add(elt);
				news.add(aaos);
				
				// Modifying the transition just before
				LinkedList<AvatarStateMachineElement> previous = getPreviousElementsOf(ast);
				if (previous.size() == 1) {
					if (previous.get(0) instanceof AvatarTransition) {
						AvatarTransition at = (AvatarTransition)(previous.get(0));
						at.addAction(timerAttributeName + " = " + ast.getTimerValue());
					} else {
						TraceManager.addError("The element before a set time is not a transition!");
					}
				} else {
					TraceManager.addError("More than one transition before a set time!");
				}
				
			// Reset timer
			} else if (elt instanceof AvatarResetTimer) {
				ato = (AvatarTimerOperator)elt;
				AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), _block.getAvatarSignalWithName("reset__" + ato.getTimer().getName()), elt.getReferenceObject());
				olds.add(elt);
				news.add(aaos);
			
			// Expire timer
			} else if (elt instanceof AvatarExpireTimer) {
				ato = (AvatarTimerOperator)elt;
				AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), _block.getAvatarSignalWithName("expire__" + ato.getTimer().getName()), elt.getReferenceObject());
				olds.add(elt);
				news.add(aaos);
			}
		}
		
		// Replacing old elements with new ones
		AvatarStateMachineElement oldelt, newelt;
		for(int i = 0; i<olds.size(); i++) {
			oldelt = olds.get(i);
			newelt = news.get(i);
			replace(oldelt, newelt);
		}
		
		return (olds.size() > 0);
	}
	
	public void replace(AvatarStateMachineElement oldone, AvatarStateMachineElement newone) {
		
		//TraceManager.addDev("Replacing " + oldone + " with " + newone);
		
		addElement(newone);
		removeElement(oldone);
		
		// Previous elements
		LinkedList<AvatarStateMachineElement> previous = getPreviousElementsOf(oldone);
		for(AvatarStateMachineElement elt: previous) {
			elt.replaceAllNext(oldone, newone);
		}
		
		// Next elements
		for(int i=0; i<oldone.nbOfNexts(); i++) {
			AvatarStateMachineElement elt = oldone.getNext(i);
			newone.addNext(elt);
		}
	}

	public AvatarTransition removeAfter(AvatarTransition _at, AvatarBlock _block) {
		String delay = _at.getMinDelay();
		if ((delay == null) || (delay.trim().length() == 0)) {
			return _at;
		}
		
		
		// We have to use a timer for this transition
		AvatarAttribute aa = _block.addTimerAttribute("timer__");
		AvatarAttribute val = _block.addIntegerAttribute(aa.getName() + "_val");
		
		//TraceManager.addDev("ADDING TIMER: " + aa.getName());
		
		// Timer is set at the entrance in the composite state
		LinkedList<AvatarTransition> ll = findEntranceTransitionElements((AvatarState)(getPreviousElementOf(_at)));
		
		AvatarTransition newat0, newat1;
		AvatarSetTimer ast;
		AvatarRandom ar;
		AvatarState as;
		for(AvatarTransition att: ll) {
			//TraceManager.addDev(" ------------------ Dealing with an entrance transition");
			ar = new AvatarRandom("randomfortimer", _block.getReferenceObject());
			ar.setVariable(val.getName());
			ar.setValues(_at.getMinDelay(), _at.getMaxDelay());
			
			ast = new AvatarSetTimer("settimer_" + aa.getName(), _block.getReferenceObject());
			ast.setTimerValue(val.getName());
			ast.setTimer(aa);
			
			newat0 = new AvatarTransition("transition_settimer_" + aa.getName(), _block.getReferenceObject());
			newat1 = new AvatarTransition("transition_settimer_" + aa.getName(), _block.getReferenceObject());
			
			elements.add(ar);
			elements.add(ast);
			elements.add(newat0);
			elements.add(newat1);
			
			newat1.addNext(att.getNext(0));
			att.removeAllNexts();
			att.addNext(ar);
			ar.addNext(newat0);
			newat0.addNext(ast);
			ast.addNext(newat1);
			
		}
		
		// Wait for timer expiration on the transition
		AvatarExpireTimer aet = new AvatarExpireTimer("expiretimer_" + aa.getName(), _block.getReferenceObject());
		aet.setTimer(aa);
		newat0 = new AvatarTransition("transition0_expiretimer_" + aa.getName(), _block.getReferenceObject());
		newat1 = new AvatarTransition("transition1_expiretimer_" + aa.getName(), _block.getReferenceObject());
		as = new AvatarState("state1_expiretimer_" + aa.getName(), _block.getReferenceObject());
		addElement(aet);
		addElement(newat0);
		addElement(newat1);
		addElement(as);
		
		newat0.addNext(aet);
		aet.addNext(newat1);
		newat1.addNext(as);
		_at.setDelays("", "");
		
		LinkedList<AvatarStateMachineElement> elts = getElementsLeadingTo(_at);
		
		for(AvatarStateMachineElement elt: elts) {
			elt.removeNext(_at);
			elt.addNext(newat0);
		}
		
		as.addNext(_at);
		
		return newat0;
	}
	
	public LinkedList<AvatarTransition> findEntranceTransitionElements(AvatarState _state) {
		//TraceManager.addDev("Searching for transitions entering:" + _state.getName());
		LinkedList<AvatarTransition> ll = new LinkedList<AvatarTransition>();
		
		for(AvatarStateMachineElement elt: elements) {
			if (elt instanceof AvatarTransition) {
				AvatarStateMachineElement element = getPreviousElementOf(elt);
				if (elt.getNext(0) == _state) {
					ll.add((AvatarTransition)elt);
				} else if (element.inAnUpperStateOf(_state) && (elt.getNext(0).getState() == _state)) {
					ll.add((AvatarTransition)elt);
				}
			}
		}
		//TraceManager.addDev("Nb of elements found:" + ll.size());
		return ll;
	}
	
	public LinkedList<AvatarStateMachineElement> getElementsLeadingTo(AvatarStateMachineElement _elt) {
		LinkedList<AvatarStateMachineElement> elts = new LinkedList<AvatarStateMachineElement>();
		
		for(AvatarStateMachineElement elt: elements) {
			if (elt.hasNext(_elt)) {
				elts.add(elt);
			}
		}
		
		return elts;
		
	}
	
	
	public void modifyAvatarTransition(AvatarTransition _at) {
		/*if ((_at.getNbOfAction() > 0) || (_at.hasCompute())) {
			return;
		}*/
		
		AvatarStateMachineElement next = _at.getNext(0);
		
		if (next instanceof AvatarState) {
			return;
		} else if ((next instanceof AvatarTimerOperator) || (next instanceof AvatarActionOnSignal)) {
			
			TraceManager.addDev("-> Timer modification");
			
			AvatarState myState = new AvatarState("statefortransition", _at.getReferenceObject());
			AvatarTransition at2 = new AvatarTransition("transitionfortransition", _at.getReferenceObject());
			AvatarTransition at1 = (AvatarTransition)(next.getNext(0));
			
			next.removeAllNexts();
			next.addNext(at2);
			at2.addNext(myState);
			myState.addNext(at1);
			
			addElement(myState);
			addElement(at2);
			
			return;
		} else {
			AvatarState myState = new AvatarState("statefortransition", _at.getReferenceObject());
			AvatarTransition at = new AvatarTransition("transitionfortransition", _at.getReferenceObject());
			at.addNext(_at.getNext(0));
			_at.removeAllNexts();
			_at.addNext(myState);
			myState.addNext(at);
			addElement(myState);
			addElement(at);
			return ;
		}
	}
	
	public AvatarTransition cloneCompositeTransition(AvatarTransition _at) {
		TraceManager.addDev("Must clone: " + _at);
		// We clone elements until we find a state!
		AvatarStateMachineElement tomake, current;
		AvatarStateMachineElement tmp;
		AvatarTransition at = (AvatarTransition)(_at.basicCloneMe());
		addElement(at);
		
		current = _at.getNext(0);
		tomake = at;
		
		while((current != null) && !(current instanceof AvatarState)) {
			TraceManager.addDev("Cloning: " + current);
			tmp = current.basicCloneMe();
			addElement(tmp);
			tomake.addNext(tmp);
			tomake = tmp;
			current = current.getNext(0);
			if (current == null) {
				break;
			}
		}
		
		if (current == null) {
			TraceManager.addDev("NULL CURRENT !!! NULL CURRENT");
		}
		
		tomake.addNext(current);
		
		return at;
		
		/*if ((_at.getNbOfAction() > 0) || (_at.hasCompute())) {
			return _at.basicCloneMe();
		}
		
		AvatarStateMachineElement next = _at.getNext(0);
		
		if (next instanceof AvatarActionOnSignal) {
			AvatarTransition at = _at.
			AvatarActionOnSignal aaos = ((AvatarActionOnSignal)next).basicCloneMe();
			addElement(at);
			addElement(aaos);
			at.addNext(aaos);
			aaos.addNext(next.getNext(0)); // Shall be a state!
			return at;
		}
		
		if (next instanceof AvatarExpireTimer) {
			AvatarTransition at = _at.basicCloneMe();
			AvatarExpireTimer aet = ((AvatarExpireTimer)next).basicCloneMe();
			AvatarTransition
			addElement(at);
			addElement(aet);
			at.addNext(aet);
			aet.addNext(next.getNext(0)); // Shall be a state!
			return at;
		}
		
		if (next instanceof AvatarResetTimer) {
			AvatarTransition at = _at.basicCloneMe();
			AvatarResetTimer art = ((AvatarResetTimer)next).basicCloneMe();
			addElement(at);
			addElement(art);
			at.addNext(art);
			art.addNext(next.getNext(0)); // Shall be a state!
			return at;
		}
		
		if (next instanceof AvatarSetTimer) {
			AvatarTransition at = _at.basicCloneMe();
			AvatarSetTimer ast = ((AvatarSetTimer)next).basicCloneMe();
			addElement(at);
			addElement(ast);
			at.addNext(ast);
			ast.addNext(next.getNext(0)); // Shall be a state!
			return at;
		}
		
		return _at.basicCloneMe();*/
		
	}
	
	public void removeStopStatesOf(AvatarState _as) {
		LinkedList<AvatarStopState> ll = new LinkedList<AvatarStopState>();
		
		for(AvatarStateMachineElement elt: elements) {
			if (elt instanceof AvatarStopState) {
				if (elt.getState() == _as) {
					ll.add((AvatarStopState)elt);
				}
			}
		}
		
		for(AvatarStopState ass: ll) {
			TraceManager.addDev("Removed a stop state");
			AvatarState astate = new AvatarState("OldStopState", ass.getReferenceObject());
			astate.setState(ass.getState());
			replace(ass, astate);
		}
	}
	
	
	public String findUniqueStateName(String name) {
		int id = 0;
		boolean found;
		
		while(id < 10000) {
			found = false;
			for(AvatarStateMachineElement elt: elements) {
				if (elt instanceof AvatarState) {
					if (elt.getName().compareTo(name+id) == 0) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
					return name + id;
				}
			id ++;
		}
		return name + id;
	}
	
	public void handleUnfollowedStartState() {
		if (startState.nbOfNexts() == 0) {
			AvatarStopState stopState = new AvatarStopState("__StopState", startState.getReferenceObject());
			AvatarTransition at = new AvatarTransition("__toStop", startState.getReferenceObject());
			addElement(stopState);
			addElement(at);
			startState.addNext(at);
			at.addNext(stopState);
		}
	}

}
