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
* Class AvatarDesignPanelTranslator
* Creation: 18/05/2010
* @author Ludovic APVRILLE
* @see
*/

package ui;

import java.util.*;



import myutil.*;
import ui.avatarbd.*;
import ui.avatarsmd.*;

import avatartranslator.*;
//import translator.*;
import ui.window.*;


public class AvatarDesignPanelTranslator {
	protected AvatarDesignPanel adp;
	protected Vector checkingErrors, warnings;
	protected CorrespondanceTGElement listE; // usual list
	//protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
	protected LinkedList <TDiagramPanel> panels;
	
	public AvatarDesignPanelTranslator(AvatarDesignPanel _adp) {
		adp = _adp;
		reinit();
	}
	
	public void reinit() {
		checkingErrors = new Vector();
		warnings = new Vector();
		listE = new CorrespondanceTGElement();
		panels = new LinkedList <TDiagramPanel>();
	}
	
	public Vector getErrors() {
		return checkingErrors;
	}
	
	public Vector getWarnings() {
		return warnings;
	}
	
	public CorrespondanceTGElement getCorrespondanceTGElement() {
		return listE;
	}
	
	public AvatarSpecification generateAvatarSpecification(Vector _blocks) {
		LinkedList<AvatarBDBlock> blocks = new LinkedList<AvatarBDBlock>();
		blocks.addAll(_blocks);
		AvatarSpecification as = new AvatarSpecification("avatarspecification", adp);
		createBlocks(as, blocks);
		createRelationsBetweenBlocks(as, blocks);
		return as;
	}
	
	public void createBlocks(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
		AvatarBlock ab;
		Vector v;
		TAttribute a;
		int i;
		AvatarAttribute aa;
		ui.AvatarMethod uiam;
		ui.AvatarSignal uias;
		avatartranslator.AvatarMethod atam;
		avatartranslator.AvatarSignal atas;
		TGComponent tgc1, tgc2;
		
		for(AvatarBDBlock block: _blocks) {
			ab = new AvatarBlock(block.getBlockName(), block);
			_as.addBlock(ab);
			listE.addCor(ab, block);
			
			// Create attributes
			v = block.getAttributeList();
			for(i=0; i<v.size(); i++) {
				a = (TAttribute)(v.elementAt(i));
				if (a.getType() == TAttribute.INTEGER){
					aa = new AvatarAttribute(a.getId(), AvatarType.INTEGER, a);
					aa.setInitialValue(a.getInitialValue());
					ab.addAttribute(aa);
				}
				if (a.getType() == TAttribute.NATURAL){
					aa = new AvatarAttribute(a.getId(), AvatarType.NATURAL, a);
					aa.setInitialValue(a.getInitialValue());
					ab.addAttribute(aa);
				}
				if (a.getType() == TAttribute.BOOLEAN) {
					aa = new AvatarAttribute(a.getId(), AvatarType.BOOLEAN, a);
					aa.setInitialValue(a.getInitialValue());
					ab.addAttribute(aa);
				}
			}
			
			// Create methods
			v = block.getMethodList();
			for(i=0; i<v.size(); i++) {
				uiam = (AvatarMethod)(v.get(i));
				atam = new avatartranslator.AvatarMethod(uiam.getId(), uiam);
				ab.addMethod(atam);
				makeParameters(atam, uiam);
			}
			// Create signals
			v = block.getSignalList();
			for(i=0; i<v.size(); i++) {
				uias = (AvatarSignal)(v.get(i));
				
				if (uias.getInOut() == uias.IN) {
					atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.IN, uias);
				} else {
					atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.OUT, uias);
				}
				ab.addSignal(atas);
				makeParameters(atas, uias);
			}
			
		}
		
		// Make block hierarchy
		for(AvatarBlock block: _as.getListOfBlocks()) {
			tgc1 = listE.getTG(block);
			if ((tgc1 != null) && (tgc1.getFather() != null)) {
				tgc2 = tgc1.getFather();
				ab = listE.getAvatarBlock(tgc2);
				if (ab != null) {
					block.setFather(ab);
				}
			}
		}
		
		// Make state machine of blocks
		for(AvatarBlock block: _as.getListOfBlocks()) {
			makeStateMachine(_as, block);
		}
		
	}
	
	public void makeParameters(avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
		String typeIds[] = _uiam.getTypeIds();
		String types[] = _uiam.getTypes();
		AvatarAttribute aa;
		
		for(int i=0; i<types.length; i++) {
			aa = new AvatarAttribute(typeIds[i], AvatarType.getType(types[i]), _uiam);
			_atam.addParameter(aa);
		}
	}
	
	public void makeStateMachine(AvatarSpecification _as, AvatarBlock _ab) {
		AvatarBDBlock block = (AvatarBDBlock)(listE.getTG(_ab));
		if (block == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No corresponding graphical block for  " + _ab.getName());
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(adp.getAvatarBDPanel());
			addCheckingError(ce);
			return;
		}
		
		AvatarSMDPanel asmdp = block.getAvatarSMDPanel();
		String name = block.getBlockName();
		TDiagramPanel tdp;
		
		int size = checkingErrors.size();
		
		if (asmdp == null) {
			return;
		}
		
		tdp = (TDiagramPanel)asmdp;

		// search for start state
		LinkedList list = asmdp.getComponentList();
		Iterator iterator = list.listIterator();
		TGComponent tgc;
		AvatarSMDStartState tss = null;
		int cptStart = 0;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDStartState){
				tss = (AvatarSMDStartState)tgc;
				cptStart ++;
			}
		}
		
		if (tss == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the state machine diagram of " + name);
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}
		
		if (cptStart > 1) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the state machine diagram of " + name);
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}
		
		// This shall also be true for all composite state: at most one start state!
		tgc = checkForStartStateOfCompositeStates(asmdp);
		if (tgc != null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in composite state");
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(tdp);
			ce.setTGComponent(tgc);
			addCheckingError(ce);
			return;
		}
		
		// First pass: creating TIF components, but no interconnection between them
		iterator = asmdp.getAllComponentList().listIterator();
		AvatarSMDReceiveSignal asmdrs;
		AvatarSMDSendSignal asmdss;
		
		AvatarStateMachine asm = _ab.getStateMachine();
		avatartranslator.AvatarSignal atas;
		AvatarActionOnSignal aaos;
		AvatarAttribute aa;
		AvatarStopState astop;
		AvatarStartState astart;
		AvatarState astate;
		int i;
		String tmp;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
				
			// Receive signal
			if (tgc instanceof AvatarSMDReceiveSignal) {
				asmdrs = (AvatarSMDReceiveSignal)tgc;
				atas = _ab.getAvatarSignalWithName(asmdrs.getSignalName());
				if (atas == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdrs.getSignalName());
					ce.setAvatarBlock(_ab);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					aaos = new AvatarActionOnSignal("action_on_signal", atas, tgc);
					if (aaos.isSending()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "a sending signal is used for receiving: " + asmdrs.getValue());
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					}
					if (asmdrs.getNbOfValues() == -1) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal: " + asmdrs.getValue());
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					} else {
						for(i=0; i<asmdrs.getNbOfValues(); i++) {
							tmp = asmdrs.getValue(i);
							if (tmp.length() == 0) {
								CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in signal expression: " + asmdrs.getValue());
								ce.setAvatarBlock(_ab);
								ce.setTDiagramPanel(tdp);
								ce.setTGComponent(tgc);
								addCheckingError(ce);
							} else {
								// Check that tmp is the identifier of an attribute
								aa = _ab.getAvatarAttributeWithName(tmp);
								if (aa == null) {
									CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in signal expression: " + asmdrs.getValue());
									ce.setAvatarBlock(_ab);
									ce.setTDiagramPanel(tdp);
									ce.setTGComponent(tgc);
									addCheckingError(ce);
								} else {
									aaos.addValue(tmp);
								}
							}
						}
						//adag.setActionValue(makeTIFAction(asmdrs.getValue(), "?"));
						listE.addCor(aaos, tgc);
						asm.addElement(aaos);
					}
				}
			} else if (tgc instanceof AvatarSMDSendSignal) {
				asmdss = (AvatarSMDSendSignal)tgc;
				atas = _ab.getAvatarSignalWithName(asmdss.getSignalName());
				if (atas == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdss.getSignalName());
					ce.setAvatarBlock(_ab);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					aaos = new AvatarActionOnSignal("action_on_signal", atas, tgc);
					if (aaos.isReceiving()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A sending signal is used for receiving: " + asmdss.getValue());
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					}
					if (asmdss.getNbOfValues() == -1) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal: " + asmdss.getValue());
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					} else {
						for(i=0; i<asmdss.getNbOfValues(); i++) {
							tmp = asmdss.getValue(i);
							if (tmp.length() == 0) {
								CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in signal expression: " + asmdss.getValue());
								ce.setAvatarBlock(_ab);
								ce.setTDiagramPanel(tdp);
								ce.setTGComponent(tgc);
								addCheckingError(ce);
							} else {
								// Check that tmp is the identifier of an attribute
								aa = _ab.getAvatarAttributeWithName(tmp);
								if (aa == null) {
									CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in signal expression: " + asmdss.getValue());
									ce.setAvatarBlock(_ab);
									ce.setTDiagramPanel(tdp);
									ce.setTGComponent(tgc);
									addCheckingError(ce);
								} else {
									aaos.addValue(tmp);
								}
							}
						}
						//adag.setActionValue(makeTIFAction(asmdrs.getValue(), "?"));
						listE.addCor(aaos, tgc);
						asm.addElement(aaos);
					}
				}
				
			// State
			} else if (tgc instanceof AvatarSMDState) {
				astate = asm.getStateWithName(tgc.getValue());
				if (astate == null) {
					astate = new AvatarState(tgc.getValue(), tgc);
					asm.addElement(astate);
				}
				listE.addCor(astate, tgc);
			
			// Start state
			} else if (tgc instanceof AvatarSMDStartState) {
				astart = new AvatarStartState("start", tgc);
				listE.addCor(astart, tgc);
				asm.addElement(astart);
				if (tgc.getFather() == null) {
					asm.setStartState(astart);
				}
				
			// Stop state
			} else if (tgc instanceof AvatarSMDStopState) {
				astop = new AvatarStopState("stop", tgc);
				listE.addCor(astop, tgc);
				asm.addElement(astop);
			}
		}
		
		if (checkingErrors.size() != size) {
			return;
		}
		
		// Make hierachy between states and elements
		iterator = asmdp.getAllComponentList().listIterator();
		AvatarStateMachineElement element1, element2;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if ((tgc != null) && (tgc.getFather() != null)) {
				element1 = (AvatarStateMachineElement)(listE.getObject(tgc));
				element2 = (AvatarStateMachineElement)(listE.getObject(tgc.getFather()));
				if ((element1 != null) && (element2 != null) && (element2 instanceof AvatarState)) {
					element1.setState((AvatarState)element2);
				}
			}
		}
		
		// Make next: handle transitions
		iterator = asmdp.getAllComponentList().listIterator();
		AvatarSMDConnector asmdco;
		AvatarTransition at;
		TGComponent tgc1, tgc2;
		int error;
		String tmp1, tmp2;
		Vector <String> vs;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDConnector) {
				asmdco = (AvatarSMDConnector)tgc;
				tgc1 = tdp.getComponentToWhichBelongs(asmdco.getTGConnectingPointP1());
				tgc2 = tdp.getComponentToWhichBelongs(asmdco.getTGConnectingPointP2());
				if ((tgc1 == null) || (tgc2 == null)) {
					TraceManager.addDev("Tgcs null in Avatar translation");
				} else {
					element1 = (AvatarStateMachineElement)(listE.getObject(tgc1));
					element2 = (AvatarStateMachineElement)(listE.getObject(tgc2));
					if ((element1 != null) && (element2 != null)) {
						at = new AvatarTransition("avatar transition", tgc);
						
						// Guard
						tmp = asmdco.getGuard();
						error = AvatarSyntaxChecker.isAValidGuard(_as, _ab, tmp);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "transition guard", tmp); 
						} else {
							at.setGuard(tmp);
						}
						
						// Delays
						tmp1 = asmdco.getAfterMinDelay();
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "after min delay", tmp1);
							tmp1 = null;
						} 
						tmp2 = asmdco.getAfterMaxDelay();
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "after max delay", tmp2);
							tmp2 = null;
						} 
						
						if ((tmp1 != null) && (tmp2 != null)) {
							at.setDelays(tmp1, tmp2);
						}
						
						// Compute min and max
						tmp1 = asmdco.getComputeMinDelay();
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "compute min ", tmp1);
							tmp1 = null;
						} 
						tmp2 = asmdco.getComputeMaxDelay();
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "compute max ", tmp2);
							tmp2 = null;
						} 
						
						if ((tmp1 != null) && (tmp2 != null)) {
							at.setComputes(tmp1, tmp2);
						}
						
						// Actions
						vs = asmdco.getActions();
						for(String s: vs) {
							if (s.trim().length() > 0) {
								s = s.trim();
								// Variable assignation or method call?
								error = s.indexOf("=");
								if (error == -1) {
									// Method call
									if(!_ab.isAValidMethodCall(s)) {
										CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed transition method call: " + s);
										ce.setAvatarBlock(_ab);
										ce.setTDiagramPanel(tdp);
										ce.setTGComponent(tgc);
										addCheckingError(ce);
									} else {
										at.addAction(s);
									}
								} else {
									// Variable assignation
									error = AvatarSyntaxChecker.isAValidVariableExpr(_as, _ab, s);
									if (error < 0) {
										makeError(error, tdp, _ab, tgc, "transition action", s);
									} else {
										at.addAction(s);
									}
								}
							}
						}
						
						element1.addNext(at);
						at.addNext(element2);
						listE.addCor(at, tgc);
						asm.addElement(at);
					}
				}
			}
		}
	
	}
	
	private void makeError(int _error, TDiagramPanel _tdp, AvatarBlock _ab, TGComponent _tgc, String _info, String _element) {
		if (_error == -3) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Undeclared variable in " + _info + ": " + _element);
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(_tdp);
			ce.setTGComponent(_tgc);
			addCheckingError(ce);
		} else {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted " + _info + ": " + _element);
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(_tdp);
			ce.setTGComponent(_tgc);
			addCheckingError(ce);
		}
	}
	
	// Checks whether all states with internal state machines have at most one start state
	private TGComponent checkForStartStateOfCompositeStates(AvatarSMDPanel _panel) {
		TGComponent tgc;
		ListIterator iterator = _panel.getComponentList().listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDState) {
				tgc = (((AvatarSMDState)(tgc)).checkForStartStateOfCompositeStates());
				if (tgc != null) {
					return tgc;
				}
			}
		}
		return null;
	}
	
	
	public void createRelationsBetweenBlocks(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
		adp.getAvatarBDPanel().updateAllSignalsOnConnectors();
		Iterator iterator = adp.getAvatarBDPanel().getComponentList().listIterator();
		
		TGComponent tgc;
		AvatarBDPortConnector port;
		AvatarBDBlock block1, block2;
		LinkedList<String> l1, l2;
		int i;
		String name1, name2;
		AvatarRelation r;
		AvatarBlock b1, b2;
		avatartranslator.AvatarSignal atas1, atas2;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarBDPortConnector) {
				port = (AvatarBDPortConnector)tgc;
				block1 = port.getAvatarBDBlock1();
				block2 = port.getAvatarBDBlock2();
				
				b1 = _as.getBlockWithName(block1.getBlockName());
				b2 = _as.getBlockWithName(block2.getBlockName());
				
				r = new AvatarRelation("relation", b1, b2, tgc);
				// Signals of l1
				l1 = port.getListOfSignalsOrigin();
				l2 = port.getListOfSignalsDestination();
				
				for(i=0; i<l1.size(); i++) {
					name1 = AvatarSignal.getSignalNameFromFullSignalString(l1.get(i));
					name2 = AvatarSignal.getSignalNameFromFullSignalString(l2.get(i));
					atas1 = b1.getAvatarSignalWithName(name1);
					atas2 = b2.getAvatarSignalWithName(name2);
					if ((atas1 != null) && (atas2 != null)) {
						r.addSignals(atas1, atas2);
					} else {
						TraceManager.addDev("null gates in AVATAR relation: " + name1 + " " + name2);
					}
				}
				_as.addRelation(r);
			}
		}
	}
	
	private void addCheckingError(CheckingError ce) {
		if (checkingErrors == null) {
			checkingErrors = new Vector();
		}
		checkingErrors.addElement(ce);
	}
	
	private void addWarning(CheckingError ce) {
		if (warnings == null) {
			warnings = new Vector();
		}
		warnings.addElement(ce);
	}
	
	/*public TURTLEModeling generateTURTLEModeling() {
		LinkedList<AvatarBDBlock> blocks = adp.getAvatarBDPanel().getFullBlockList();
		return generateTURTLEModeling(blocks, "");
	}
	
	public TURTLEModeling generateTURTLEModeling(Vector blocks, String preName) {
		LinkedList<AvatarBDBlock> ll = new LinkedList<AvatarBDBlock>();
		for(int i=0; i<blocks.size(); i++) {
			ll.add((AvatarBDBlock)blocks.get(i));
		}
		return generateTURTLEModeling(ll, preName);
	}
	
	public TURTLEModeling generateTURTLEModeling(LinkedList<AvatarBDBlock> blocks, String preName) {
		TURTLEModeling tmodel = new TURTLEModeling();
		createTClassesFromBlocks(tmodel, blocks, preName);
		createRelationsBetweenTClasses(tmodel, blocks, preName);
		//addTClasses(adp, blocks, preName, tmodel);
		//addRelations(adp, tmodel);
		return tmodel;
	}
	
	private void addCheckingError(CheckingError ce) {
		if (checkingErrors == null) {
			checkingErrors = new Vector();
		}
		checkingErrors.addElement(ce);
	}
	
	private void addWarning(CheckingError ce) {
		if (warnings == null) {
			warnings = new Vector();
		}
		warnings.addElement(ce);
	}
	
	public void createTClassesFromBlocks(TURTLEModeling tm, LinkedList<AvatarBDBlock> blocks, String preName) {
		TClass t;
		Vector v;
		int i;
		TAttribute a;
		Param p;
		AvatarMethod am;
		Gate g;
		AvatarSignal as;
		
		for(AvatarBDBlock block: blocks) {
			t = new TClass(preName + block.getBlockName(), true);
			
			tm.addTClass(t);
			listE.addCor(t, block, preName);
			
			// Create attributes
			v = block.getAttributeList();
			for(i=0; i<v.size(); i++) {
				a = (TAttribute)(v.elementAt(i));
				if ((a.getType() == TAttribute.NATURAL) || (a.getType() == TAttribute.INTEGER)){
					p = new Param(a.getId(), Param.NAT, a.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
				if (a.getType() == TAttribute.BOOLEAN) {
					p = new Param(a.getId(), Param.BOOL, a.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
			}
			
			// Create internal gates
			v = block.getMethodList();
			for(i=0; i<v.size(); i++) {
				am = (AvatarMethod)(v.get(i));
				g = new Gate(am.getId(), Gate.GATE, false);
				t.addGate(g);
			}
			
			// Create external gates from signals
			v = block.getSignalList();
			for(i=0; i<v.size(); i++) {
				as = (AvatarSignal)(v.get(i));
				if (as.getInOut() == AvatarSignal.IN) {
					g = new Gate(as.getId(), Gate.INGATE, false);
				} else {
					g = new Gate(as.getId(), Gate.OUTGATE, false);
				}
				t.addGate(g);
			}
			
			// Activity Diagram
			buildStateMachineDiagram(tm, block, t);
		}
	}
	
	private void buildStateMachineDiagram(TURTLEModeling tm, AvatarBDBlock block, TClass t) {
		int j;
		//TActivityDiagramPanel tadp;
		AvatarSMDPanel asmdp = block.getAvatarSMDPanel();
		String name = block.getBlockName();
		TDiagramPanel tdp;
		
		int size = checkingErrors.size();
		
		if (asmdp == null) {
			return;
		}
		
		tdp = (TDiagramPanel)asmdp;
		
		int indexTdp = panels.indexOf(tdp);
		if (indexTdp > -1) {
			TraceManager.addDev("Found similar  diagram for " + block.getBlockName());
			t.setActivityDiagram((activities.get(indexTdp)).duplicate(t));
			
			//System.out.println("AD of " + t.getName() + "=");
			//t.getActivityDiagram().print();
			
			// Must fill correspondances!
			ADComponent ad0, ad1;
			TGComponent tgcad;
			for(int adi=0; adi<t.getActivityDiagram().size(); adi++) {
				ad0 = (ADComponent)(t.getActivityDiagram().get(adi));
				ad1 = (ADComponent)(activities.get(indexTdp).get(adi));
				tgcad = listE.getTG(ad1);
				if (tgcad != null ){
					//System.out.println("Adding correspondance for " + ad0);
					listE.addCor(ad0, tgcad);
				}
			}
			
			return;
		}
		
		// search for start state
		LinkedList list = asmdp.getComponentList();
		Iterator iterator = list.listIterator();
		TGComponent tgc;
		AvatarSMDStartState tss = null;
		int cptStart = 0;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDStartState){
				tss = (AvatarSMDStartState)tgc;
				cptStart ++;
			}
		}
		
		if (tss == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the state machine diagram of " + name);
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}
		
		if (cptStart > 1) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the state machine diagram of " + name);
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}
		
		// This shall also be true for all composite state: at most one start state!
		tgc = checkForStartStateOfCompositeStates(asmdp);
		if (tgc != null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in composite state");
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			ce.setTGComponent(tgc);
			addCheckingError(ce);
			return;
		}
		
		// Creation of the activity diagram
		ADStart ads;
		ADStop adstop;
		ads = new ADStart();
		
		listE.addCor(ads, tss);
		ActivityDiagram ad = new ActivityDiagram(ads);
		
		adstop = new ADStop();
		ads.addNext(adstop);
		ad.add(adstop);
		
		t.setActivityDiagram(ad);
		
		// First pass: creating TIF components, but no interconnection between them
		ADParallel adpar;
		iterator = asmdp.getAllComponentList().listIterator();
		ADActionStateWithGate adag;
		Gate g;
		ADJunction adj;
		ADChoice adch;
		
		AvatarSMDReceiveSignal asmdrs;
		AvatarSMDSendSignal asmdss;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			// Parallel
			if (tgc instanceof AvatarSMDParallel) {
				adpar = new ADParallel();
				listE.addCor(adpar, tgc);
				ad.add(adpar);
				
			// Receive signal
			} else if (tgc instanceof AvatarSMDReceiveSignal) {
				asmdrs = (AvatarSMDReceiveSignal)tgc;
				g = t.getGateByName(asmdrs.getSignalName());
				if (g == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdrs.getSignalName());
					ce.setTClass(t);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					adag = new ADActionStateWithGate(g);
					adag.setActionValue(makeTIFAction(asmdrs.getValue(), "?"));
					listE.addCor(adag, tgc);
					ad.add(adag);
				}
			
			// Send signal
			} else if (tgc instanceof AvatarSMDSendSignal) {
				asmdss = (AvatarSMDSendSignal)tgc;
				g = t.getGateByName(asmdss.getSignalName());
				if (g == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdss.getSignalName());
					ce.setTClass(t);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					adag = new ADActionStateWithGate(g);
					adag.setActionValue(makeTIFAction(asmdss.getValue(), "!"));
					listE.addCor(adag, tgc);
					ad.add(adag);
				}
				
			// State
			} else if (tgc instanceof AvatarSMDState) {
				// First case: no internal
				// One junction followed by one choice
				// The junction remains the reference
				adj = new ADJunction();
				listE.addCor(adj, tgc);
				ad.add(adj);
				adch = new ADChoice();
				ad.add(adch);
				adj.addNext(adch);
			
			// Start state
			} else if (tgc instanceof AvatarSMDStartState) {
				
				
			// Stop state
			} else if (tgc instanceof AvatarSMDStopState) {
				adstop = new ADStop();
				listE.addCor(adstop, tgc);
				ad.add(adstop);
			}
		}
		
		if (checkingErrors.size() != size) {
			return;
		}
		
		// Second pass: connectors between components
		iterator = asmdp.getAllComponentList().listIterator();
		AvatarSMDConnector asmdco;
		TGComponent tgc1, tgc2;
		Object o;
		boolean first;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDConnector) {
				asmdco = (AvatarSMDConnector)tgc;
				tgc1 = tdp.getComponentToWhichBelongs(asmdco.getTGConnectingPointP1());
				tgc2 = tdp.getComponentToWhichBelongs(asmdco.getTGConnectingPointP2());
				if ((tgc1 == null) || (tgc2 == null)) {
					TraceManager.addDev("tgcs null in Avatar translation");
				} else {
					// First case: not quiting a composite state
					first = true;
					if (tgc1 instanceof AvatarSMDState) {
						if (((AvatarSMDState)tgc1).isACompositeState()) {
							first = false;
						}
					}
					if (first) {
						connect(asmdco, tgc1, tgc2, tss, ad, t, tdp);
					} else {
						// Second case: not yet implemented...
					}
				}
			}
		}
		
		panels.add(tdp);
		activities.add(ad);
	}
	
	private void connect(AvatarSMDConnector _asmdco, TGComponent _tgc1, TGComponent _tgc2, AvatarSMDStartState _tss, ActivityDiagram _ad, TClass _t, TDiagramPanel _tdp) {
		ADComponent adc1, adc2;
		ADComponent adc;
		String s1, s2;
		ADChoice adch;
		ADTimeInterval adti;
		ADDelay addelay;
		boolean hasChoice = false;
		Vector<String> v;
		Gate g;
		Param p;
		ADActionStateWithGate adag;
		ADActionStateWithParam adap;
		
		// Search for the two elements to connect
		if (_tgc1 instanceof AvatarSMDStartState) {
			if (_tgc1 != _tss) {
				_tgc1 = _tgc1.getFather(); // Shall be a state!
			}
		}
		
		// Search for the two related TIF Components
		adc1 = listE.getADComponent(_tgc1);
		adc2 = listE.getADComponent(_tgc2);
		
		
		
		if ((adc1 == null) || (adc2 == null)) {
			TraceManager.addDev("adcs null in Avatar translation");
		} else {
			adc = adc1;
			
			if (_tgc1 instanceof AvatarSMDState) {
				adc1 = adc1.getNext(0); // shall be a choice!
			}
			
			// Guard
			if (adc1 instanceof ADChoice) {
				adch = (ADChoice)adc1;
				s1 = _asmdco.getGuard();
				if (s1 == null) {
					s1 = "[ ]";
				}
				adch.addGuard(s1);
				hasChoice = true;
			}
			
			// Delay
			s1 = _asmdco.getTotalMinDelay();
			s2 = _asmdco.getTotalMaxDelay();
			if (s1.length() > 0) {
				if (s2.length() > 0) {
					adti = new ADTimeInterval();
					adti.setValue(s1, s2);
					_ad.add(adti);
					listE.addCor(adti, _asmdco);
					adc.addNext(adti);
					adc = adti;
				} else {
					addelay = new ADDelay();
					addelay.setValue(s1);
					_ad.add(addelay);
					listE.addCor(addelay, _asmdco);
					adc.addNext(addelay);
					adc = addelay;
				}
			}
			
			// Actions
			v = _asmdco.getActions();
			if (v.size() == 0) {
				if (hasChoice) {
					// Must make an action to make the choice deterministic, except if the next component is an action!
					if (!((_tgc2 instanceof AvatarSMDReceiveSignal) || (_tgc2 instanceof AvatarSMDSendSignal))) {
						adc = makeChoiceAction(_ad, _t, adc, _asmdco);
						TraceManager.addDev("Adding artifical action for choice to be deterministic");
					}
				}
			} else {
				// has actions!
				if (!isActionOnGate(_t, v.get(0))) {
					if (hasChoice) {
					// Must make an action to make the choice deterministic, except if the next component is an action!
						if (!((_tgc2 instanceof AvatarSMDReceiveSignal) || (_tgc2 instanceof AvatarSMDSendSignal))) {
							adc = makeChoiceAction(_ad, _t, adc, _asmdco);
							TraceManager.addDev("Adding artifical action for choice to be deterministic");
						}
					}	
				}
				
				for (String action: v) {
					g = getGateFromActionState(_t, action);
					p = getParamFromActionState(_t, action);
					if (g != null) {
						adag = new ADActionStateWithGate(g);
						_ad.addElement(adag);
						adag.setActionValue(makeTIFAction(action, "!"));
						listE.addCor(adag, _asmdco);
						adc.addNext(adag);
						adc = adag;
					} else if (p != null) {
						adap = new ADActionStateWithParam(p);
						_ad.addElement(adap);
						adap.setActionValue(makeTIFActionOnParam(action));
						listE.addCor(adap, _asmdco);
						adc.addNext(adap);
						adc = adap;
					} else {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed action: " + action);
						ce.setTClass(_t);
						ce.setTDiagramPanel(_tdp);
						ce.setTGComponent(_asmdco);
						addCheckingError(ce);
						return;
					}
				}
			}
			
			adc.addNext(adc2);
		}
		
	}
	
	private boolean isActionOnGate(TClass _t, String _s) {
		Gate g = getGateFromActionState(_t, _s);
		return (g != null);
	}
	
	private Gate getGateFromActionState(TClass _t, String _action) {
		String action = _action;
		int index0 = action.indexOf("(");
		if (index0 != -1) {
			action = _action.substring(0, index0);
		}
		
		return _t.getGateByName(action);
	}
	
	private Param getParamFromActionState(TClass _t, String _action) {
		String action = _action;
		int index0 = action.indexOf("(");
		if (index0 != -1) {
			action = _action.substring(0, index0);
		}
		
		return _t.getParamByName(action);
	}
	
	private ADComponent makeChoiceAction(ActivityDiagram _ad, TClass _t, ADComponent _adc, TGComponent _asmdco) {
		Gate g = _t.addNewGateIfApplicable("choice__", true);
		ADActionStateWithGate adag = new ADActionStateWithGate(g);
		adag.setActionValue("");
		_ad.add(adag);
		_adc.addNext(adag);
		listE.addCor(adag, _asmdco);
		return adag;
	}
	
	private String makeTIFActionOnParam(String _s) {
		String ret = _s.trim();
		int index0 = ret.indexOf("=");
		if (index0 == -1) {
			return ret;
		}
		
		return ret.substring(index0+1, ret.length()).trim();
		
	}
	
	private String makeTIFAction(String _s, String _replace) {
		String ret = _s.trim();
		int index0 = ret.indexOf("(");
		if (index0 == -1) {
			return "";
		}
		
		int index1 = ret.indexOf(")");
		if (index1 == -1) {
			return "";
		}
		
		ret = ret.substring(index0, index1); 
		
		ret = Conversion.replaceAllString(ret, "(", _replace);
		ret = Conversion.replaceAllString(ret, ",", _replace);
		ret = Conversion.replaceAllString(ret, " ", "");
		
		return ret;
	}
	
	// Checks whether all states with internal state machines have at most one start state
	private TGComponent checkForStartStateOfCompositeStates(AvatarSMDPanel _panel) {
		TGComponent tgc;
		ListIterator iterator = _panel.getComponentList().listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDState) {
				tgc = (((AvatarSMDState)(tgc)).checkForStartStateOfCompositeStates());
				if (tgc != null) {
					return tgc;
				}
			}
		}
		return null;
	}
	
	
	public void createRelationsBetweenTClasses(TURTLEModeling tm, LinkedList<AvatarBDBlock> blocks, String preName) {
		adp.getAvatarBDPanel().updateAllSignalsOnConnectors();
		Iterator iterator = adp.getAvatarBDPanel().getComponentList().listIterator();
		
		TGComponent tgc;
		AvatarBDPortConnector port;
		AvatarBDBlock block1, block2;
		LinkedList<String> l1, l2;
		int i;
		String name1, name2;
		Relation r;
		TClass t1, t2;
		Gate g1, g2;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarBDPortConnector) {
				port = (AvatarBDPortConnector)tgc;
				block1 = port.getAvatarBDBlock1();
				block2 = port.getAvatarBDBlock2();
				
				t1 = tm.getTClassWithName(preName + block1.getBlockName());
				t2 = tm.getTClassWithName(preName + block2.getBlockName());
				r = new Relation(Relation.SYN, t1, t2, true);
				// Signals of l1
				l1 = port.getListOfSignalsOrigin();
				l2 = port.getListOfSignalsDestination();
				
				for(i=0; i<l1.size(); i++) {
					name1 = AvatarSignal.getSignalNameFromFullSignalString(l1.get(i));
					name2 = AvatarSignal.getSignalNameFromFullSignalString(l2.get(i));
					g1 = t1.getGateByName(name1);
					g2 = t2.getGateByName(name2);
					if ((g1 != null) && (g2 != null)) {
						r.addGates(g1, g2);
					} else {
						TraceManager.addDev("null gates in AVATAR relation: " + name1 + " " + name2);
					}
				}
				tm.addRelation(r);
			}
		}
	}*/
	
	
}
