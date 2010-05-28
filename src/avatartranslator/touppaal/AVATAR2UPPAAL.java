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
* Class AVATAR2UPPAAL
* Creation: 25/05/2010
* @version 1.1 25/05/2010
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.touppaal;

import java.awt.*;
import java.util.*;

import uppaaldesc.*;
import myutil.*;
import avatartranslator.*;

public class AVATAR2UPPAAL {
	
	public final static String ACTION_INT = "actionint__";
	public final static String ACTION_BOOL = "actionbool__";
	public final static String CHOICE_ACTION = "makeChoice";
	public final static String CHOICE_VAR = "choice__";
	
	private UPPAALSpec spec;
	private AvatarSpecification avspec;
	
	private Vector warnings;
	
	private int currentX, currentY;
	
	private LinkedList gatesNotSynchronized; // String
	private LinkedList gatesSynchronized;
	private int nbOfIntParameters, nbOfBooleanParameters;
	
	private Hashtable <AvatarStateMachineElement, UPPAALLocation> hash; 
	
	public final static int STEP_X = 0;
	public final static int STEP_Y = 80;
	public final static int STEP_LOOP_X = 150;
	public final static int NAME_X = 10;
	public final static int NAME_Y = 5;
	public final static int SYNCHRO_X = 5;
	public final static int SYNCHRO_Y = -10;
	public final static int ASSIGN_X = 10;
	public final static int ASSIGN_Y = 0;
	public final static int GUARD_X = 0;
	public final static int GUARD_Y = -20;
	
	/*private boolean isRegular;
	private boolean isRegularTClass;
	private boolean choicesDeterministic = false;
	private boolean variableAsActions = false;
	private RelationTIFUPPAAL table;
	
	
	private LinkedList tmpComponents;
	private LinkedList tmpLocations;
	private ArrayList<UPPAALTemplate> templatesWithMultipleProcesses;
	private LinkedList locations;
	private LinkedList gates;
	private LinkedList relations; // null: not synchronize, Relation : synchronized
	private LinkedList parallels;
	
	private LinkedList gatesNotSynchronized; // String
	private ArrayList<Gate> gatesWithInternalSynchro;
	private int maxSentInt; // Max nb of int put on non synchronized gates
	private int maxSentBool;
	private LinkedList gatesSynchronized;
	private int idChoice;
	private int idTemplate;
	private int idPar;
	private int idParProcess;
	private ArrayList<ADParallel> paras;
	private ArrayList<Integer> parasint;
	//private int idTemplate;
	private boolean multiprocess;
	
	
	
	
	
	public final static String SYNCID = "__sync__";
	public final static String GSYNCID = "__gsync__";*/
	
	private UPPAALTemplate templateNotSynchronized;
	
	public AVATAR2UPPAAL(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	
	public void saveInFile(String path) throws FileException {
		FileUtils.saveFile(path + "spec.xml", spec.makeSpec());
		//System.out.println("spec.xml generated:\n" + spec.getFullSpec());
	}
	
	
	public Vector getWarnings() {
		return warnings;
	}
	
	/*public RelationTIFUPPAAL getRelationTIFUPPAAL () {
		return table;
	}*/
	
	public UPPAALSpec generateUPPAAL(boolean _debug) {
		warnings = new Vector();
		hash = new Hashtable<AvatarStateMachineElement, UPPAALLocation>();
		spec = new UPPAALSpec();
		
		avspec.removeCompositeStates();
		
		TraceManager.addDev("Spec:" + avspec.toString());
		
		UPPAALLocation.reinitID();
		gatesNotSynchronized = new LinkedList();
		gatesNotSynchronized.add("makeChoice");
		gatesSynchronized = new LinkedList();
		
		// Deal with blocks
		translateBlocks();
		
		translationRelations();
		
		makeNotSynchronized();
		makeSynchronized();
		
		makeGlobal();
		
		
		// Generate system
		//makeGlobal(effectiveNb);
		//makeParallel(nb);
		
		
		makeSystem();
		
		TraceManager.addDev("Enhancing graphical representation ...");
		spec.enhanceGraphics();
		TraceManager.addDev("Enhancing graphical representation done");
		
		//System.out.println("relations:" + table.toString());
		
		return spec;
	}
	
	public void initXY() {
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
			for(int i=0; i<ar.nbOfSignals(); i++) {
				gatesSynchronized.add(relationToString(ar, i));
			}
		}
	}
	
	public String relationToString(AvatarRelation _ar, int _index) {
		return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
	}
	
	public String signalToUPPAALString(AvatarSignal _as) {
		AvatarRelation ar = avspec.getAvatarRelationWithSignal(_as);
		if (ar == null) {
			return null;
		}
		return relationToString(ar, ar.hasSignal(_as));
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
			if (aa.isInt() || aa.isNat()) {
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
		templateNotSynchronized.setName("Actions__not__synchronized");
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
	
	public void makeSynchronized() {
		if (gatesSynchronized.size() == 0) {
			return;
		}
		
		spec.addGlobalDeclaration("\n//Declarations used for synchronized gates\n");
		
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
		
		if (_elt == null) {
			return;
		}
		
		loc = hash.get(_elt);
		if (loc != null) {
			tr = addTransition(_template, _previous, loc);
			return;
		}
		
		// Start state
		if (_elt instanceof AvatarStartState) {
			hash.put(_elt, _previous);
			makeElementBehavior(_block, _template, _elt.getNext(0), _previous, _end, null, false);
			return;
		
		// Stop state
		} else if (_elt instanceof AvatarStopState) {
			//tr = addRTransition(template, previous, end);
			hash.put(_elt, _previous);
			return;
			
			
		// Avatar Action on Signal
		} else if (_elt instanceof AvatarActionOnSignal) {
			aaos = (AvatarActionOnSignal)_elt;
			String [] ss = manageSynchro(_block, aaos);
			loc = addLocation(_template);
			hash.put(_elt, loc);
			tr = addTransition(_template, _previous, loc);
			if (_guard != null) {
				addGuard(tr, _guard);
			}
			setSynchronization(tr, ss[0]);
			addAssignment(tr, ss[1]);
			makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false);
		
		// Avatar State
		} else if (_elt instanceof AvatarState) {
			if (_elt.nbOfNexts() == 0) {
				return;
			}
			
			state = (AvatarState)_elt;
			
			// We translate at the same time the state and its next transitions (guard and time + first method call)
			// We assume all nexts are transitions
			
			// At first, we set variables choice__i to the min delay 
			tmps = "h__ = 0";
			j = 0;
			for(i=0; i<state.nbOfNexts(); i++) {
				at = (AvatarTransition)(state.getNext(i));
				if (at.hasDelay()) {
					tmps += ", " + CHOICE_VAR + j + " = max(0 , " + at.getTotalMinDelay() + ")";
					_block.addIntAttributeIfApplicable(CHOICE_VAR + j);
					j ++;
				}
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
					tmps = CHOICE_VAR + j + " < (" + at.getTotalMaxDelay() + ")";
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
					tmps = "(" + CHOICE_VAR + j + " < 0) && (h__ >" + CHOICE_VAR + j  + ")";
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
				if (at.hasActions()) {
					tmps0 = at.getAction(0);
					if (AvatarSpecification.isAVariableSettingString(tmps0)) {
						// We must introduce a fake action
						loc = addLocation(_template); 
						tr = addTransition(_template, loc1, loc);
						setGuard(tr, tmps);
						setSynchronization(tr, CHOICE_ACTION + "!");
						makeElementBehavior(_block, _template, _elt.getNext(i), loc, _end, null, true);
					} else {
						// We make the translation in the next transition
						makeElementBehavior(_block, _template, _elt.getNext(i), loc, _end, tmps, true);
					}
				} else {
					// Must consider whether the transition leads to an action on a signal
					if (at.followedWithAnActionOnASignal()) {
						makeElementBehavior(_block, _template, at.getNext(0), loc1, _end, tmps, true);
					} else {
						// We must introduce a fake action
						loc = addLocation(_template); 
						tr = addTransition(_template, loc1, loc);
						setGuard(tr, tmps);
						setSynchronization(tr, CHOICE_ACTION + "!");
						// Useless to translate the next transition, we directly jump to after the transition
						makeElementBehavior(_block, _template, at.getNext(0), loc, _end, null, true);
					}
				}
			}
			
			/*loc = addLocation(_template);
			hash.put(_elt, loc);
			tr = addTransition(_template, _previous, loc);
			_previous.setCommitted();
			for(i=0; i<_elt.nbOfNexts(); i++) {
				makeElementBehavior(_block, _template, _elt.getNext(i), loc, _end, null);
			}*/
			
			// Avatar Transition not following a state -> only the next one one transitions: 
			// So, translated at it is
		} else if (_elt instanceof AvatarTransition) {
			at = (AvatarTransition) _elt;
			loc = _previous;
			if (!_previousState) {
				if (at.isGuarded()) {
					loc1 = addLocation(_template);
					tr = addTransition(_template, _previous, loc1);
					tmps = convertGuard(at.getGuard());
					setGuard(tr, tmps);
					loc = loc1;
				}
				
				if (at.hasDelay()) {
					loc = makeTimeInterval(_template, loc, at.getTotalMinDelay(), at.getTotalMaxDelay());
				}
			}
			
			if (at.hasActions()) {
				for(i=0; i<at.getNbOfAction(); i++) {
					tmps = at.getAction(i);
					
					// Setting a variable
					if (AvatarSpecification.isAVariableSettingString(tmps)) {
						loc1 = addLocation(_template);
						loc.setCommitted();
						tr = addTransition(_template, loc, loc1);
						setAssignment(tr, tmps);
						loc = loc1;
					// Method call
					} else {
						
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
			hash.put(_elt, loc);
			makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false);
		
		} else {
			TraceManager.addDev("Reached end of elseif in block behaviour...");
			return;
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
				if (aa.isInt() || aa.isNat()) {
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
				if (aa.isInt() || aa.isNat()) {
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
			dec += template.getName() + "__" + id + " = " + template.getName() + "();\n";
			system += template.getName() + "__" + id;
			if (iterator.hasNext()) {
				system += ",";
			} else {
				system += ";";
			}
			id++;
		}
		
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
	
	
}