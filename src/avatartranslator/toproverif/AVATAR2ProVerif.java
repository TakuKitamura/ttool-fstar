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
* Class AVATAR2ProVerif
* Creation: 03/09/2010
* @version 1.1 03/09/2010
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.toproverif;

import java.awt.*;
import java.util.*;

import proverifspec.*;
import myutil.*;
import avatartranslator.*;

public class AVATAR2ProVerif {
	
	private final static String PK_HEADER = "(* Public key cryptography *)\nfun pk/1.\nfun encrypt/2.\nreduc decrypt(encrypt(x,pk(y)),y) = x.\n";
	private final static String SK_HEADER = "(* Symmetric key cryptography *)\nfun sencrypt/2.\nreduc decrypt(encrypt(x,k),k) = x.\n";
	
	private ProVerifSpec spec;
	private AvatarSpecification avspec;
	
	private Vector warnings;
	

	
	public AVATAR2ProVerif(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	
	public void saveInFile(String path) throws FileException {
		FileUtils.saveFile(path + "pvspec", spec.makeSpec());
	}
	
	
	public Vector getWarnings() {
		return warnings;
	}
	

	
	public ProVerifSpec generateProVerif(boolean _debug, boolean _optimize) {
		warnings = new Vector();
		spec = new ProVerifSpec();
		
		avspec.removeCompositeStates();
		avspec.removeTimers();
		
		makeHeader();
		
		makeBlocks();
		
		makeStartingProcess();
		
		//TraceManager.addDev("->   Spec:" + avspec.toString());
		
		
		// Deal with blocks
		//translateBlocks();
		
		//translationRelations();
		
		//makeGlobal();
		
		
		// Generate system
		//makeGlobal(effectiveNb);
		//makeParallel(nb);
		
		
		//makeSystem();
		
		/*if (_optimize) {
			spec.optimize();
		}*/
		
		
		return spec;
	}
	
	public void makeHeader() {
		spec.addToGlobalSpecification(PK_HEADER + "\n");
		spec.addToGlobalSpecification(SK_HEADER + "\n");
		
		spec.addToGlobalSpecification("\n(* Channel *)\nfree ch.\n");
		
		/* Parse all attributes declared by blocks and declare them as "private free" */
		/*LinkedList<AvatarBlock> blocks = avatarspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			spec.addToGlobalSpecification("\n(* Block " + block.getName() + " : variables *)\n");
			for(AvatarAttribute attribute: block.getAttributes()) {
				spec.addToGlobalSpecification("private free " + attribute.getName() + ".\n");
			}
		}*/
		
		/* Queries */
		/* Parse all attributes starting with "secret" and declare them as non accesible to attacker" */
		spec.addToGlobalSpecification("\n(* Queries *)\n");
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			for(AvatarAttribute attribute: block.getAttributes()) {
				if (attribute.getName().startsWith("secret")) {
					spec.addToGlobalSpecification("private free " + attribute.getName() + ".\n");
					spec.addToGlobalSpecification("query attacker:" + attribute.getName() + ".\n\n");
				}
			}
		}
	}
	
	public void makeStartingProcess() {
		String action = "";
		
		ProVerifProcess p = new ProVerifProcess();
		p.processName = "starting__";
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			if (action.length() != 0) {
				action += " | ";
			} 
			action += "(!" + block.getName() + "__0)";
		}
		action = "(" + action + ")";
		p.processLines.add(action);
		spec.addProcess(p);
		spec.setStartingProcess(p);
	}
	
	public void makeBlocks() {
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			makeBlock(block);
		}
	}
	
	public void makeBlock(AvatarBlock ab) {
		LinkedList<ProVerifProcess> tmprocesses = new LinkedList<ProVerifProcess>();
		LinkedList<AvatarState> state = new LinkedList<AvatarState>();
		
		// First process : variable declaration
		ProVerifProcess p = new ProVerifProcess(ab.getName() + "__0");
		
		for(AvatarAttribute aa: ab.getAttributes()) {
			p.processLines.add("new " + aa.getName() + ";");
		}
		
		
	}
	

	
	/*public void initXY() {
		currentX = 0; currentY = -220;
	}
	
	public void makeGlobal() {
		
		int i;
		String s = "";
		
		s += "\n// Global parameters for method calls and signal exchange\n";
		for(i=0; i<nbOfIntParameters; i++) {
			s+= "int " + ACTION_INT  + i + ";\n";
		}
		for(i=0; i<nbOfBooleanParameters; i++) {
			s+= "int " + ACTION_BOOL  + i + ";\n";
		}
		s+= "\n";
		
		
		s += "\nint min(int x, int y) {\nif(x<y) {\nreturn x;\n}\nreturn y;\n}\n\n";
		s += "int max(int x, int y) {\nif(x<y) {\nreturn y;\n}\nreturn x;\n}\n";
		spec.addGlobalDeclaration(Conversion.indentString(s, 2));
	}
	
	public void translateBlocks() {
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			translateBlock(block);
		}
	}
	
	public void translateBlock(AvatarBlock _block) {
		
		UPPAALTemplate template = newBlockTemplate(_block, 0);
		
		// Behaviour
		makeBehaviour(_block, template);
		
		// Attributes
		makeAttributes(_block, template);
		
		// Methods
		makeMethods(_block, template);
		
	}
	
	public void translationRelations() {
		AvatarSignal si1, sig2;
		for(AvatarRelation ar: avspec.getRelations()) {
			if (ar.isAsynchronous()) {
				for(int i=0; i<ar.nbOfSignals(); i++) {
					gatesAsynchronized.add(relationToString(ar, i, false));
					gatesAsynchronized.add(relationToString(ar, i, true));
				}
			} else {
				for(int i=0; i<ar.nbOfSignals(); i++) {
					gatesSynchronized.add(relationToString(ar, i));
				}
			}
		}
	}
	
	
	// For synchronous relations
	public String relationToString(AvatarRelation _ar, int _index) {
		return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
	}
	
	// For asynchronous relations
	public String relationToString(AvatarRelation _ar, int _index, boolean inSignal) {
		String signalName;
		AvatarSignal sig;
		AvatarBlock block;
		
		if (inSignal) {
			sig = _ar.getInSignal(_index);
			block = _ar.getInBlock(_index);
		} else {
			sig = _ar.getOutSignal(_index);
			block = _ar.getOutBlock(_index);
		}
		
		signalName = block.getName() + "_" + sig.getName() + "__";
		
		if (sig.isIn()) {
			signalName += "rd";
		} else {
			signalName += "wr";
		}
		
		return signalName;
	}
	
	public String signalToUPPAALString(AvatarSignal _as) {
		AvatarRelation ar = avspec.getAvatarRelationWithSignal(_as);
		if (ar == null) {
			return null;
		}
		
		if (ar.isAsynchronous()) {
			if (_as.isIn()) {
				return relationToString(ar, ar.hasSignal(_as), true);
			} else {
				return relationToString(ar, ar.hasSignal(_as), false);
			}
		} else {
			return relationToString(ar, ar.hasSignal(_as));
		}
	}
	
	public UPPAALTemplate newBlockTemplate(AvatarBlock _block, int id) {
		UPPAALTemplate template = new UPPAALTemplate();
		if (id != 0) {
			template.setName(_block.getName() + "___" + id);
		} else {
			template.setName(_block.getName());
		}
		spec.addTemplate(template);
		//table.addTClassTemplate(t, template, id);
		return template;
	}
	
	public void makeAttributes(AvatarBlock _block, UPPAALTemplate _template) {
		AvatarAttribute aa;
		int i;
		
		for(i=0; i<_block.attributeNb(); i++) {
			aa = _block.getAttribute(i);
			if (aa.isInt()) {
				_template.addDeclaration("int ");
			} else {
				_template.addDeclaration("bool ");
			}
			if (aa.hasInitialValue()) {
				_template.addDeclaration(aa.getName() + " = " + aa.getInitialValue() + ";\n");
			} else {
				_template.addDeclaration(aa.getName() + " = " + aa.getDefaultInitialValue() + ";\n");
			}
		}
		
		_template.addDeclaration("clock h__;\n");
	}
	
	public void makeMethods(AvatarBlock _block, UPPAALTemplate _template) {
		String s;
		for(AvatarMethod method: _block.getMethods()) {
			gatesNotSynchronized.add(_block.getName() + "__" + method.getName());
		}
	}
	
	
	public void makeNotSynchronized() {
		if (gatesNotSynchronized.size() == 0) {
			return;
		}
		
		initXY();
		
		templateNotSynchronized = new UPPAALTemplate();
		templateNotSynchronized.setName("Nonsync__actions");
		spec.addTemplate(templateNotSynchronized);
		UPPAALLocation loc = addLocation(templateNotSynchronized);
		templateNotSynchronized.setInitLocation(loc);
		UPPAALTransition tr;
		
		spec.addGlobalDeclaration("\n//Declarations used for non synchronized gates\n");
		
		String action;
		ListIterator iterator = gatesNotSynchronized.listIterator();
		while(iterator.hasNext()) {
			action = (String)(iterator.next());
			tr = addTransition(templateNotSynchronized, loc, loc);
			setSynchronization(tr, action+"?");
			//addGuard(tr, action + TURTLE2UPPAAL.SYNCID + " == 0");
			spec.addGlobalDeclaration("urgent chan " + action + ";\n");
			//spec.addGlobalDeclaration("int " + action + TURTLE2UPPAAL.SYNCID + " = 0;\n");
		}
		
		
	}
	
	public void makeAsynchronous() {
		if (gatesAsynchronized.size() == 0) {
			return;
		}
		
		initXY();
		
		templateAsynchronous = new UPPAALTemplate();
		templateAsynchronous.setName("Async__channels");
		spec.addTemplate(templateAsynchronous);
		UPPAALLocation loc = addLocation(templateAsynchronous);
		templateAsynchronous.setInitLocation(loc);
		UPPAALTransition tr;
		
		spec.addGlobalDeclaration("\n//Declarations for asynchronous channels\n");
		String action;
		ListIterator iterator = gatesAsynchronized.listIterator();
		while(iterator.hasNext()) {
			action = (String)(iterator.next());
			spec.addGlobalDeclaration("urgent chan " + action + ";\n");
		}
		
		
		for(AvatarRelation ar: avspec.getRelations()) {
			if (ar.isAsynchronous() && (ar.nbOfSignals() > 0)) {
				for(int i=0; i<ar.nbOfSignals(); i++) { 
					AvatarSignal sig1 = ar.getOutSignal(i);
					AvatarSignal sig2 = ar.getInSignal(i);
					AvatarBlock block = ar.getOutBlock(i);
					String name0 = block.getName() + "__" + sig1.getName();
					String enqueue, dequeue;
					
					enqueue = "\nvoid enqueue__" + name0 + "(){\n";
					dequeue = "\nvoid dequeue__" + name0 + "(){\n";
					
					// Lists
					templateAsynchronous.addDeclaration("\n// Asynchronous relations:" + ar.block1.getName() + "/" + sig1.getName() + " -> " + ar.block2.getName() + "/" + sig2.getName() + "\n");
					templateAsynchronous.addDeclaration("\nint size__" + name0 + " = 0;\n");
					templateAsynchronous.addDeclaration("int head__" + name0 + " = 0;\n");
					templateAsynchronous.addDeclaration("int tail__" + name0 + " = 0;\n");
					
					int cpt = 0;
					String listName;
					
					for(AvatarAttribute aa: sig1.getListOfAttributes()) {
						listName = "list__" + name0 + "_" + cpt;
						
						if (aa.isInt()) {
							templateAsynchronous.addDeclaration("int " + listName + "[" + ar.getSizeOfFIFO() + "];\n");
							enqueue += "  " + listName +  "[tail__" + name0 + "] = " +  ACTION_INT + cpt + ";\n";
							dequeue += "  " + ACTION_INT + cpt + " = " + listName +  "[tail__" + name0 + "] " + ";\n";
						} else {
							templateAsynchronous.addDeclaration("bool " + listName + "[" + ar.getSizeOfFIFO() + "];\n");
							enqueue += "  " + listName +  "[tail__" + name0 + "] = " +  ACTION_BOOL + cpt + ";\n";
							dequeue += "  " + ACTION_BOOL + cpt + " = " + listName +  "[tail__" + name0 + "] " + ";\n";
						}
						cpt ++;
					}
					enqueue += "  tail__" + name0 + " = (tail__" + name0 + "+1) %" + ar.getSizeOfFIFO() + ";\n";
					enqueue += "  size__" + name0 + "++;\n";
					enqueue += "}\n";
					dequeue += "  head__" + name0 + " = (head__" + name0 + "+1) %" + ar.getSizeOfFIFO() + ";\n";
					dequeue += "  size__" + name0 + "--;\n";
					dequeue += "}\n";
					templateAsynchronous.addDeclaration(enqueue);
					templateAsynchronous.addDeclaration(dequeue);
					
					tr = addTransition(templateAsynchronous, loc, loc);
					setSynchronization(tr, signalToUPPAALString(sig1)+"?");
					setGuard(tr, "size__" + name0 + " <" +  ar.getSizeOfFIFO());
					setAssignment(tr, "enqueue__" + name0 + "()"); 
					
					tr = addTransition(templateAsynchronous, loc, loc);
					setSynchronization(tr, signalToUPPAALString(sig2)+"!");
					setAssignment(tr, "dequeue__" + name0 + "()"); 
					setGuard(tr, "size__" + name0 + "> 0");
					
					if (!ar.isBlocking()) {
						tr = addTransition(templateAsynchronous, loc, loc);
						setSynchronization(tr, signalToUPPAALString(sig1)+"?");
						setGuard(tr, "size__" + name0 + " ==" +  ar.getSizeOfFIFO());
						setAssignment(tr, "dequeue__" + name0 + "()\n enqueue__" + name0 + "()"); 
					}
				}
			}
		}
	}
	
	public void makeSynchronized() {
		if (gatesSynchronized.size() == 0) {
			return;
		}
		
		spec.addGlobalDeclaration("\n//Declarations for synchronous channels\n");
		
		String action;
		ListIterator iterator = gatesSynchronized.listIterator();
		while(iterator.hasNext()) {
			action = (String)(iterator.next());
			spec.addGlobalDeclaration("urgent chan " + action + ";\n");
		}
		
		
	}
	
	public void makeBehaviour(AvatarBlock _block, UPPAALTemplate _template) {
		initXY();
		UPPAALLocation loc = makeBlockInit(_block, _template);
		AvatarStartState ass = _block.getStateMachine().getStartState();
		
		TraceManager.addDev("Making behaviour of " + _block.getName());
		
		makeElementBehavior(_block, _template, ass, loc, null, null, false);
	}
	
	public void makeElementBehavior(AvatarBlock _block, UPPAALTemplate _template, AvatarStateMachineElement _elt, UPPAALLocation _previous, UPPAALLocation _end, String _guard, boolean _previousState) {
		AvatarActionOnSignal aaos;
		UPPAALLocation loc, loc1;
		UPPAALTransition tr;
		AvatarTransition at;
		int i, j;
		String tmps, tmps0;
		AvatarAttribute aa;
		AvatarState state;
		AvatarRandom arand;
		
		if (_elt == null) {
			return;
		}
		
		loc = hash.get(_elt);
		if (loc != null) {
			if (_previous == null) {
				TraceManager.addDev("************************* NULL PREVIOUS !!!!!!!*****************");
			}
			tr = addTransition(_template, _previous, loc);
			return;
		}
		
		// Start state
		if (_elt instanceof AvatarStartState) {
			hash.put(_elt, _previous);
			//if (_elt.getNext(0) != null) {
			makeElementBehavior(_block, _template, _elt.getNext(0), _previous, _end, null, false);
			//}
			return;
			
			// Stop state
		} else if (_elt instanceof AvatarStopState) {
			//tr = addRTransition(template, previous, end);
			hash.put(_elt, _previous);
			return;
			
			// Random
		} else if (_elt instanceof AvatarRandom) {
			arand = (AvatarRandom)_elt;
			//tr = addRTransition(template, previous, end);
			loc = addLocation(_template);  
			tr = addTransition(_template, _previous, loc);
			setAssignment(tr, arand.getVariable() + "=" + arand.getMinValue());
			tr = addTransition(_template, loc, loc);
			setAssignment(tr, arand.getVariable() + "=" + arand.getVariable() + "+1");
			setGuard(tr, arand.getVariable() + "<" + arand.getMaxValue());
			_previous.setCommitted();
			loc.setCommitted();
			hash.put(_elt, _previous);
			makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false);
			return;
			
			
			// Avatar Action on Signal
		} else if (_elt instanceof AvatarActionOnSignal) {
			loc = translateAvatarActionOnSignal((AvatarActionOnSignal)_elt, _block, _template, _previous, _guard);
			makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false);
			
			// Avatar State
		} else if (_elt instanceof AvatarState) {
			if (_elt.nbOfNexts() == 0) {
				return;
			}
			
			state = (AvatarState)_elt;
			hash.put(_elt, _previous);
			
			// We translate at the same time the state and its next transitions (guard and time + first method call)
			// We assume all nexts are transitions
			
			
			LinkedList<AvatarTransition> transitions = new LinkedList<AvatarTransition>();
			for(i=0; i<state.nbOfNexts(); i++) {
				at = (AvatarTransition)(state.getNext(i));
				if (at.hasDelay()) {
					transitions.add(at);
				}
			}
			
			if (transitions.size() == 0) {
				// No transition with a delay
				for(i=0; i<state.nbOfNexts(); i++) {
					at = (AvatarTransition)(state.getNext(i));
					makeElementBehavior(_block, _template, at, _previous, _end, null, false);
				}
			} else {
				// At least one transition with a delay
				// Reset the clock
				tmps = "h__ = 0";
				loc = addLocation(_template);  
				tr = addTransition(_template, _previous, loc);
				setAssignment(tr, tmps);
				_previous.setCommitted();
				
				LinkedList<UPPAALLocation> locs = new LinkedList<UPPAALLocation>();
				for(i=0; i<state.nbOfNexts(); i++) {
					at = (AvatarTransition)(state.getNext(i));
					locs.add(addLocation(_template)); 
				}
				
				LinkedList<UPPAALLocation> builtlocs = new LinkedList<UPPAALLocation>();
				LinkedList<AvatarStateMachineElement> elements = new LinkedList<AvatarStateMachineElement>();
				
				makeStateTransitions(state, locs, transitions, loc, _end, _block, _template, builtlocs, elements);
				
				for(int k=0; k<builtlocs.size(); k++) {
					makeElementBehavior(_block, _template, elements.get(k), builtlocs.get(k), _end, null, false);
				}
			}
			
			
		} else if (_elt instanceof AvatarTransition) {
			at = (AvatarTransition) _elt;
			loc = translateAvatarTransition(at, _block, _template, _previous, _guard, _previousState);
			makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false);
			
		} else {
			TraceManager.addDev("Reached end of elseif in block behaviour...");
			return;
		}
	}
	
	
	public UPPAALLocation translateAvatarActionOnSignal(AvatarActionOnSignal _aaos, AvatarBlock _block, UPPAALTemplate _template, UPPAALLocation _previous, String _guard) {
		String [] ss = manageSynchro(_block, _aaos);
		UPPAALLocation loc = addLocation(_template);
		hash.put(_aaos, loc);
		UPPAALTransition tr = addTransition(_template, _previous, loc);
		if (_guard != null) {
			addGuard(tr, _guard);
		}
		setSynchronization(tr, ss[0]);
		addAssignment(tr, ss[1]);
		return loc;
	}
	
	public UPPAALLocation translateAvatarTransition(AvatarTransition _at, AvatarBlock _block, UPPAALTemplate _template, UPPAALLocation _previous, String _guard, boolean _previousState) {
		UPPAALLocation loc = _previous;
		UPPAALLocation loc1;
		UPPAALTransition tr;
		String tmps;
		int i;
		
		if (!_previousState) {
			if (_at.isGuarded()) {
				loc1 = addLocation(_template);
				tr = addTransition(_template, _previous, loc1);
				tmps = convertGuard(_at.getGuard());
				setGuard(tr, tmps);
				loc = loc1;
			}
			
			if (_at.hasDelay()) {
				loc = makeTimeInterval(_template, loc, _at.getMinDelay(), _at.getMaxDelay());
			}
		}
		
		if (_at.hasCompute()) {
			loc = makeTimeInterval(_template, loc, _at.getMinCompute(), _at.getMaxCompute());
			_previousState = false;
		}
		
		if (_at.hasActions()) {
			for(i=0; i<_at.getNbOfAction(); i++) {
				tmps = _at.getAction(i);
				
				// Setting a variable
				if (AvatarSpecification.isAVariableSettingString(tmps)) {
					loc1 = addLocation(_template);
					loc.setCommitted();
					tr = addTransition(_template, loc, loc1);
					setAssignment(tr, tmps);
					loc = loc1;
					// Method call
				} else {
					TraceManager.addDev("Found method call:" + tmps);
					loc1 = addLocation(_template);
					tr = addTransition(_template, loc, loc1);
					
					if ((i ==0) && (_previousState)) {
						setGuard(tr, _guard);
					} else {
						loc.setUrgent();
					}
					setSynchronization(tr, AvatarSpecification.getMethodCallFromAction(tmps) + "!");
					makeMethodCall(_block, tr, tmps);
					loc = loc1;
				}
			}
		}
		hash.put(_at, loc);
		return loc;
	}
	
	// Start from a given state / loc, and derive progressively all locations
	// _transitions contains timing transitions
	public void makeStateTransitions(AvatarState _state, LinkedList<UPPAALLocation> _locs, LinkedList<AvatarTransition> _transitions, UPPAALLocation _loc, UPPAALLocation _end, AvatarBlock _block, UPPAALTemplate _template, LinkedList<UPPAALLocation> _builtlocs, LinkedList<AvatarStateMachineElement> _elements) {
		// Make the current state
		// Invariant
		String inv = "";
		int cpt = 0;
		int i;
		UPPAALLocation loc1;
		String tmps, tmps0;
		AvatarTransition at;
		UPPAALLocation loc;
		UPPAALTransition tr;
		
		
		for(AvatarTransition att: _transitions) {
			if (cpt == 0) {
				inv += "h__ <= " + att.getMaxDelay();
			} else {
				inv = "(" + inv + ") && (h__ <= " +att.getMaxDelay() + ")";
			}
			cpt ++;
		}
		
		_loc.setInvariant(inv);
		
		// Put all logical transitions
		// Choice between transitions
		// If the first action is a method call, or not action but the next one is an action on a signal:
		// Usual translation way i.e. use the action as the UPPAAL transition trigger
		// Otherwise introduce a fake choice action
		//j = 0;
		UPPAALLocation locend;
		for(i=0; i<_state.nbOfNexts(); i++) {
			at = (AvatarTransition)(_state.getNext(i));
			locend = _locs.get(i);
			
			if (!(_transitions.contains(at))) {
				
				// Computing guard
				if (at.isGuarded()) {
					tmps = convertGuard(at.getGuard());
				} else {
					tmps = "";
				}
				
				if (at.hasCompute()) {
					tr = addTransition(_template, _loc, locend);
					setGuard(tr, tmps);
					setSynchronization(tr, CHOICE_ACTION + "!");
					if (_template.nbOfTransitionsExitingFrom(locend) == 0) {
						loc1 = translateAvatarTransition(at, _block, _template, locend, "", true);
						_builtlocs.add(loc1);
						_elements.add(at.getNext(0));
					}
					
				} else if (at.hasActions()) {
					tmps0 = at.getAction(0);
					if (AvatarSpecification.isAVariableSettingString(tmps0)) {
						// We must introduce a fake action
						tr = addTransition(_template, _loc, locend);
						if (tmps != null) {
							setGuard(tr, tmps);
						}
						setSynchronization(tr, CHOICE_ACTION + "!");
						if (_template.nbOfTransitionsExitingFrom(locend) == 0) {
							loc1 = translateAvatarTransition(at, _block, _template, locend, "", true);
							_builtlocs.add(loc1);
							_elements.add(at.getNext(0));
						}
						
					} else {
						// We make the translation in the next transition
						loc1 = translateAvatarTransition(at, _block, _template, _loc, "", true);
						tr = addTransition(_template, loc1, locend);
						loc1.setCommitted();
						if (!(_elements.contains(at.getNext(0)))) {
						  _builtlocs.add(locend);
						  _elements.add(at.getNext(0));
						}
					}
				} else {
					// Must consider whether the transition leads to an action on a signal
					if (at.followedWithAnActionOnASignal()) {
						loc1 = translateAvatarActionOnSignal((AvatarActionOnSignal)(at.getNext(0)), _block, _template, _loc, "");
						tr = addTransition(_template, loc1, locend);
						loc1.setCommitted();
						if (!(_elements.contains(at.getNext(0).getNext(0)))) {
						  _builtlocs.add(locend);
						  _elements.add(at.getNext(0).getNext(0));
						}
					} else {
						// If this is not the only transition
						// We must introduce a fake action
						tr = addTransition(_template, _loc, locend);
						setGuard(tr, tmps);
						setSynchronization(tr, CHOICE_ACTION + "!");
						// Useless to translate the next transition, we directly jump to after the transition
						if (!(_elements.contains(at.getNext(0)))) {
							_builtlocs.add(locend);
							_elements.add(at.getNext(0));
						}
					}
				}
			}
		}
		
		
		// Make the nexts transitions / put all timing transitions
		// Consider all possibilities
		
		if (_transitions.size() == 0) {
			return;
		}
		
		LinkedList<AvatarTransition> cloneList;
		
		for(i=0; i<_transitions.size(); i++) {
			cloneList = new LinkedList<AvatarTransition>();
			cloneList.addAll(_transitions);
			cloneList.remove(i);
			currentX = currentX + STEP_LOOP_X;
			loc1 = addLocation(_template);
			tr = addTransition(_template, _loc, loc1);
			addGuard(tr, "h__ >= " + _transitions.get(i).getMinDelay());
			makeStateTransitions(_state, _locs, cloneList, loc1, _end, _block, _template,  _builtlocs, _elements);
			currentX = currentX - STEP_LOOP_X;
		}
		
		
	}
	
	public void makeMethodCall(AvatarBlock _block, UPPAALTransition _tr, String _call) {
		int j;
		AvatarAttribute aa;
		String result = "";
		int nbOfInt = 0;
		int nbOfBool = 0;
		String tmps;
		
		TraceManager.addDev("Making method call:" + _call);
		
		String mc = "";
		AvatarBlock block = _block;
		String method = AvatarSpecification.getMethodCallFromAction(_call);
		
		block = _block.getBlockOfMethodWithName(method);
		
		if (block != null) {
			mc = block.getName() + "__" + method + "!";
		}
		
		TraceManager.addDev("Method name:" + mc);
		
		setSynchronization(_tr, mc);
		for(j=0; j<AvatarSpecification.getNbOfParametersInAction(_call); j++) {
			tmps = AvatarSpecification.getParameterInAction(_call, j);
			TraceManager.addDev("Attribute #j: " + tmps);
			aa = _block.getAvatarAttributeWithName(tmps);
			if (aa != null) {
				if ((nbOfInt > 0) || (nbOfBool > 0)) {
					result = result + ",\n";
				}
				if (aa.isInt()) {
					result = result + ACTION_INT + nbOfInt + " =" + aa.getName();
					nbOfInt ++;
				} else {
					result = result + ACTION_BOOL + nbOfBool + " =" + aa.getName();
					nbOfBool ++;
				}
			}
		}
		
		if (result.length() > 0) {
			setAssignment(_tr, result);
		}
		
		nbOfIntParameters = Math.max(nbOfIntParameters, nbOfInt);
		nbOfBooleanParameters = Math.max(nbOfBooleanParameters, nbOfBool);
		
	}
	
	public UPPAALLocation makeTimeInterval(UPPAALTemplate _template, UPPAALLocation _previous, String _minint, String _maxint) {
		UPPAALLocation loc, loc1;
		UPPAALTransition tr, tr1;
		loc1 = addLocation(_template);
		_previous.setCommitted();
		tr1 = addTransition(_template, _previous, loc1);
		setAssignment(tr1, "h__ = 0");
		loc = addLocation(_template);
		tr = addTransition(_template, loc1, loc);
		loc1.setInvariant("(h__ <= (" + _maxint + "))");
		addGuard(tr, "(h__ >= (" + _minint + "))");
		return loc;
	}
	
	
	public UPPAALLocation makeBlockInit(AvatarBlock _block, UPPAALTemplate _template) {
		currentX = currentX - 100;
		UPPAALLocation loc1 = addLocation(_template);
		currentX = currentX + 100;
		_template.setInitLocation(loc1);
		return loc1;
	}
	
	public String [] manageSynchro(AvatarBlock _block, AvatarActionOnSignal _aaos) {
		AvatarSignal as = _aaos.getSignal();
		return manageSynchroSynchronous(_block, _aaos);
		
		

	}
	
	
	public String [] manageSynchroSynchronous(AvatarBlock _block, AvatarActionOnSignal _aaos) {
		String []result = new String[2];
		String val;
		
		int nbOfInt = 0;
		int nbOfBool = 0;
		
		AvatarAttribute aa;
		
		result[0] = "";
		result[1] = "";
		
		String signal = signalToUPPAALString(_aaos.getSignal());
		
		if (signal == null) {
			return result;
		}
		
		if (_aaos.isSending()) {
			signal += "!";
		} else {
			signal += "?";
		}
		
		result[0] = signal;
		
		//TraceManager.addDev("Nb of params on signal " + signal + ":" + _aaos.getNbOfValues());
		
		for(int i=0; i<_aaos.getNbOfValues(); i++) {
			val = _aaos.getValue(i);
			aa = _block.getAvatarAttributeWithName(val);
			if (aa != null) {
				if (aa.isInt()) {
					if (_aaos.isSending()) {
						result[1] = result[1] + ACTION_INT + nbOfInt + " = " + aa.getName();
					} else {
						result[1] = result[1] + aa.getName() + " = " + ACTION_INT + nbOfInt;
					}
					nbOfInt ++;
				} else {
					if (_aaos.isSending()) {
						result[1] = result[1] + ACTION_INT + nbOfBool + " = " + aa.getName();
					} else {
						result[1] = result[1] + aa.getName() + " = " + ACTION_INT + nbOfBool;
					}
					nbOfBool ++;
				}
				if (i != (_aaos.getNbOfValues() -1)) {
					result[1] += ", ";
				}
			} else {
				TraceManager.addDev("Null param:" + _aaos.getValue(i));
			}
		}
		
		nbOfIntParameters = Math.max(nbOfIntParameters, nbOfInt);
		nbOfBooleanParameters = Math.max(nbOfBooleanParameters, nbOfBool);
		
		return result;
	}
	
	public String [] manageSynchroAsynchronous(AvatarBlock _block, AvatarActionOnSignal _aaos) {
		String []result = new String[2];
		String val;
		
		int nbOfInt = 0;
		int nbOfBool = 0;
		
		AvatarAttribute aa;
		
		result[0] = "";
		result[1] = "";
		
		String signal = signalToUPPAALString(_aaos.getSignal());
		
		if (signal == null) {
			return result;
		}
		
		if (_aaos.isSending()) {
			signal += "!";
		} else {
			signal += "?";
		}
		
		result[0] = signal;
		
		for(int i=0; i<_aaos.getNbOfValues(); i++) {
			val = _aaos.getValue(i);
			aa = _block.getAvatarAttributeWithName(val);
			if (aa != null) {
				if (aa.isInt()) {
					if (_aaos.isSending()) {
						result[1] = result[1] + ACTION_INT + nbOfInt + " = " + aa.getName();
					} else {
						result[1] = result[1] + aa.getName() + " = " + ACTION_INT + nbOfInt;
					}
					nbOfInt ++;
				} else {
					if (_aaos.isSending()) {
						result[1] = result[1] + ACTION_INT + nbOfBool + " = " + aa.getName();
					} else {
						result[1] = result[1] + aa.getName() + " = " + ACTION_INT + nbOfBool;
					}
					nbOfBool ++;
				}
				if (i != (_aaos.getNbOfValues() -1)) {
					result[1] += ", ";
				}
			}
		}
		
		nbOfIntParameters = Math.max(nbOfIntParameters, nbOfInt);
		nbOfBooleanParameters = Math.max(nbOfBooleanParameters, nbOfBool);
		
		return result;
	}
	
	public UPPAALLocation addLocation(UPPAALTemplate _template) {
		UPPAALLocation loc = new UPPAALLocation();
		loc.idPoint.x = currentX;
		loc.idPoint.y = currentY;
		loc.namePoint.x = currentX + NAME_X;
		loc.namePoint.y = currentY + NAME_Y;
		_template.addLocation(loc);
		currentX += STEP_X;
		currentY += STEP_Y;
		return loc;
	}
	
	public void addRandomNailPoint(UPPAALTransition tr) {
		int x = 0, y = 0;
		if (tr.sourceLoc != tr.destinationLoc) {
			x = ((tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2) - 25 + (int)(50.0 * Math.random());
			y = ((tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2) - 25 + (int)(50.0 * Math.random());
			tr.points.add(new Point(x, y));
		}
	}
	
	public UPPAALTransition addTransition(UPPAALTemplate template, UPPAALLocation loc1, UPPAALLocation loc2) {
		UPPAALTransition tr = new UPPAALTransition();
		tr.sourceLoc = loc1;
		tr.destinationLoc = loc2;
		template.addTransition(tr);
		// Nails?
		// Adding random intermediate nail
		addRandomNailPoint(tr);
		return tr;
	}
	
	
	public void setSynchronization(UPPAALTransition tr, String s) {
		tr.synchronization = modifyString(s);
		tr.synchronizationPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + SYNCHRO_X;
		tr.synchronizationPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + SYNCHRO_Y;
	}
	
	public void addGuard(UPPAALTransition tr, String s) {
		if ((tr.guard == null) || (tr.guard.length() < 2)){
			tr.guard = modifyString(s);
		} else {
			tr.guard = "(" + tr.guard + ")&&(" + modifyString(s) + ")";
		}
		tr.guardPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + GUARD_X;
		tr.guardPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + GUARD_Y;
	}
	
	public void setInvariant(UPPAALLocation loc, String s) {
		loc.setInvariant(modifyString(s));
	}
	
	public void setGuard(UPPAALTransition tr, String s) {
		tr.guard = modifyString(s);
		tr.guardPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + GUARD_X;
		tr.guardPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + GUARD_Y;
	}
	
	public void setAssignment(UPPAALTransition tr, String s) {
		tr.assignment = modifyString(s);
		tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
		tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
	}
	
	public void addAssignment(UPPAALTransition tr, String s) {
		if (s.length() <1) {
			return;
		}
		if ((tr.assignment == null) || (tr.assignment.length() < 2)){
			tr.assignment = modifyString(s);
		} else {
			tr.assignment = tr.assignment + ",\n " + modifyString(s);
		}
		
		tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
		tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
	}
	
	public void setEndAssignment(UPPAALTransition tr) {
		tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
		tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
	}
	
	public void makeSystem() {
		ListIterator iterator = spec.getTemplates().listIterator();
		UPPAALTemplate template;
		String system = "system ";
		String dec = "";
		int id = 0;
		int i;
		
		while(iterator.hasNext()) {
			template = (UPPAALTemplate)(iterator.next());
			if (template.getNbOfTransitions() > 0) {
				dec += template.getName() + "__" + id + " = " + template.getName() + "();\n";
				if (id > 0) {
					system += ",";
				}
				system += template.getName() + "__" + id;
			}
			id++;
		}
		
		system += ";";
		
		spec.addInstanciation(dec+system);
	}
	
	public String modifyString(String _input) {
		try {
			//_input = Conversion.replaceAllString(_input, "&&", "&amp;&amp;");
			//_input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
			//_input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
			//_input = Conversion.replaceAllChar(_input, '<', "&lt;");
			//_input = Conversion.replaceAllChar(_input, '>', "&gt;");
			_input = Conversion.replaceAllStringNonAlphanumerical(_input, "mod", "%");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception when changing binary operator in " + _input);
		}
		//System.out.println("Modified string=" + _input);
		return _input;
	}
	
	public String convertGuard(String g) {
		if (g == null) {
			return "";
		}
		
		if (g.compareTo("null") == 0) {
			return "";
		}
		String action = Conversion.replaceAllChar(g, '[', "");
		action = Conversion.replaceAllChar(action, ']', "");
		return modifyString(action.trim());
	}
	
	public String getUPPAALIdentification(Object _o) {
		if (avspec == null) {
			return null;
		}
		
		String ret = "";
		
		AvatarBlock block = avspec.getBlockFromReferenceObject(_o);
		
		if (block != null) {
			UPPAALTemplate temp = spec.getTemplateByName(block.getName());
			int index = spec.getIndexOfTemplate(temp);
			if (temp != null) { 
				ret += block.getName() + "__" + index;
				
				AvatarStateMachineElement asme = avspec.getStateMachineElementFromReferenceObject(_o);
				if (asme != null) {
					UPPAALLocation loc = hash.get(asme);
					if (loc != null) {
						ret += "." + loc.name;
					}
				}
			}
		}
		
		return ret;
		
		
	}
	
	
				// At first, we set variables choice__i to the min delay 
			/*tmps = "h__ = 0";
			j = 0;
			for(i=0; i<state.nbOfNexts(); i++) {
			at = (AvatarTransition)(state.getNext(i));
			if (at.hasDelay()) {
			tmps += ", " + CHOICE_VAR + j + " = max(0 , " + at.getMinDelay() + ")";
			_block.addIntAttributeIfApplicable(CHOICE_VAR + j);
			j ++;
			}
			}
			
			if (j == 0) {
			tmps = "";
			}
			
			
			loc = addLocation(_template);  
			loc.setCommitted();
			hash.put(_elt, loc);
			tr = addTransition(_template, _previous, loc);
			setAssignment(tr, tmps);
			_previous.setCommitted();
			
			// Then, random value between min and max delays 
			j = 0;
			for(i=0; i<state.nbOfNexts(); i++) {
			at = (AvatarTransition)(state.getNext(i));
			if (at.hasDelay()) {
			tr = addTransition(_template, loc, loc);
			tmps = CHOICE_VAR + j + " = " + CHOICE_VAR + j + " + 1";
			setAssignment(tr, tmps);
			tmps = CHOICE_VAR + j + " < (" + at.getMaxDelay() + ")";
			setGuard(tr, tmps);
			j++;
			}
			}
			
			// Then, wait for delays to elapse ... 
			loc1 = addLocation(_template); 
			tr = addTransition(_template, loc, loc1);
			j = 0;
			for(i=0; i<state.nbOfNexts(); i++) {
			at = (AvatarTransition)(state.getNext(i));
			if (at.hasDelay()) {
			tr = addTransition(_template, loc1, loc1);
			tmps = CHOICE_VAR + j + " = 0";
			setAssignment(tr, tmps);
			tmps = "(" + CHOICE_VAR + j + " > 0) && (h__ >" + CHOICE_VAR + j  + ")";
			setGuard(tr, tmps);
			j ++;
			}
			}
			
			// Choice between transitions
			// If the first action is a method call, or not action but the next one is an action on a signal:
			// Usual translation way i.e. use the action as the UPPAAL transition trigger
			// Otherwise introduce a fake choice action
			j = 0;
			for(i=0; i<state.nbOfNexts(); i++) {
			at = (AvatarTransition)(state.getNext(i));
			
			// Computing guard
			if (at.isGuarded()) {
			tmps = convertGuard(at.getGuard());
			if (at.hasDelay()) {
			tmps = "("  + tmps + ") && (" + CHOICE_VAR + j + " == 0)";
			j ++;
			} 
			} else {
			if (at.hasDelay()) {
			tmps = CHOICE_VAR + j + " == 0";
			j ++;
			} else {
			tmps = "";
			}
			}
			
			if (at.hasCompute()) {
			loc = addLocation(_template); 
			tr = addTransition(_template, loc1, loc);
			setSynchronization(tr, CHOICE_ACTION + "!");
			makeElementBehavior(_block, _template, _elt.getNext(i), loc, _end, null, true);
			} else if (at.hasActions()) {
			tmps0 = at.getAction(0);
			if (AvatarSpecification.isAVariableSettingString(tmps0)) {
			// We must introduce a fake action
			loc = addLocation(_template); 
			tr = addTransition(_template, loc1, loc);
			if (tmps != null) {
			setGuard(tr, tmps);
			}
			setSynchronization(tr, CHOICE_ACTION + "!");
			makeElementBehavior(_block, _template, _elt.getNext(i), loc, _end, null, true);
			} else {
			// We make the translation in the next transition
			makeElementBehavior(_block, _template, _elt.getNext(i), loc1, _end, tmps, true);
			}
			} else {
			// Must consider whether the transition leads to an action on a signal
			if (at.followedWithAnActionOnASignal()) {
			makeElementBehavior(_block, _template, at.getNext(0), loc1, _end, tmps, true);
			} else {
			// If this is not the only transition
			// We must introduce a fake action
			if (state.nbOfNexts() > 1) {
			loc = addLocation(_template); 
			tr = addTransition(_template, loc1, loc);
			setGuard(tr, tmps);
			setSynchronization(tr, CHOICE_ACTION + "!");
			// Useless to translate the next transition, we directly jump to after the transition
			makeElementBehavior(_block, _template, at.getNext(0), loc, _end, null, true);
			} else {
			// Only one transition
			if (tmps.length() > 0) {
			loc = addLocation(_template); 
			tr = addTransition(_template, loc1, loc);
			setGuard(tr, tmps);
			makeElementBehavior(_block, _template, at.getNext(0), loc, _end, null, true);
			} else {
			makeElementBehavior(_block, _template, at.getNext(0), loc1, _end, null, true);
			}
			}
			}
			}
			}*/
	
}