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
	
	private final String[] PRAGMAS = {"Confidentiality", "Secret", "InitialCommonKnowledge", "Authenticity"};
	private final String[] PRAGMAS_TRANSLATION = {"Secret", "Secret", "InitialCommonKnowledge", "Authenticity"};
	
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
		makeBlockStateMachines(as);
		createPragmas(as, blocks);
		TraceManager.addDev("Removing else guards");
		as.removeElseGuards();
		TraceManager.addDev("Removing else guards ... done");
		return as;
	}
	
	
	public void createPragmas(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
		Iterator iterator = adp.getAvatarBDPanel().getComponentList().listIterator();
		TGComponent tgc;
		TGCNote tgcn;
		String values [];
		String tmp;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TGCNote) {
				tgcn = (TGCNote)tgc;
				values = tgcn.getValues();
				for(int i=0; i<values.length; i++) {
					tmp = values[i].trim();
					if ((tmp.startsWith("#") && (tmp.length() > 1))) {
							tmp = tmp.substring(1, tmp.length()).trim();
							
							TraceManager.addDev("Reworking pragma =" + tmp);
							
							tmp = reworkPragma(tmp, _blocks);
							
							TraceManager.addDev("Reworked pragma =" + tmp);
							
							if (tmp == null) {
								CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Invalid pragma: " + values[i].trim() + " (ignored)");
								ce.setTGComponent(tgc);
								ce.setTDiagramPanel(adp.getAvatarBDPanel());
								addWarning(ce);
							} else {
								_as.addPragma(tmp);
								TraceManager.addDev("Adding pragma:" + tmp);
							}
					}
				}
			}
		}
	}
	
	public String reworkPragma(String _pragma, LinkedList<AvatarBDBlock> _blocks) {
		String ret = "";
		int i;
		
		// Identify first keyword
		_pragma = _pragma.trim();
		
		int index = _pragma.indexOf(" ");
		
		if (index == -1) {
			return null;
		}
		
		String header = _pragma.substring(0, index).trim();
		
		for(i=0; i<PRAGMAS.length; i++) {
			if (header.compareTo(PRAGMAS[i]) == 0) {
					break;
			}
		}
		
		// Invalid header?
		if (i == PRAGMAS.length) {
			return null;
		}
		
		
		
		ret = PRAGMAS_TRANSLATION[i] + " ";
		
		// Checking for arguments
		
		boolean b = ret.startsWith("Authenticity ");
		String arguments [] = _pragma.substring(index+1, _pragma.length()).trim().split(" ");
		String tmp;
		String blockName, stateName, paramName;
		boolean found = false;
		Vector types;
		AvatarBDBlock block;
		TAttribute ta;
		
		for(i=0; i<arguments.length; i++) {
			tmp = arguments[i];
			index = tmp.indexOf(".");
			if (index == -1) {
				return null;
			}
			blockName = tmp.substring(0, index);
			
			//TraceManager.addDev("blockName=" + blockName);
			// Search for the block
			for(Object o: _blocks) {
				block = (AvatarBDBlock)o;
				if (block.getBlockName().compareTo(blockName) == 0) {
					if (b) {
						// authenticity
						stateName = tmp.substring(index+1, tmp.length());
						//TraceManager.addDev("stateName=" + stateName);
						index = stateName.indexOf(".");
						if (index == -1) {
							return null;
						}
						paramName = stateName.substring(index+1, stateName.length());
						stateName = stateName.substring(0, index);
						
						for(Object oo: block.getAttributeList()) {
							ta = (TAttribute)oo;
							if (ta.getId().compareTo(paramName) == 0) {
								found = true;
								
								if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
									ret = ret + blockName + "." + paramName + " ";
								} else if (ta.getType() == TAttribute.OTHER) {
									// Must find all subsequent types
									types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
									if (types == null) {
										return null;
									} else {
										for(int j=0; j<types.size(); j++) {
											ret = ret + blockName + "." + stateName + "." + paramName + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
										}
									}
									
								} else {
									return null;
								}
								
								break;
							}
						}
						
					} else {
						// Other: confidentiality, initial common knowledge
						paramName = tmp.substring(index+1, tmp.length());
						for(Object oo: block.getAttributeList()) {
							ta = (TAttribute)oo;
							if (ta.getId().compareTo(paramName) == 0) {
								found = true;
								
								if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
									ret = ret + blockName + "." + paramName + " ";
								} else if (ta.getType() == TAttribute.OTHER) {
									// Must find all subsequent types
									types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
									if (types == null) {
										return null;
									} else {
										for(int j=0; j<types.size(); j++) {
											ret = ret + blockName + "." + paramName + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
										}
									}
									
								} else {
									return null;
								}
								
								break;
							}
						}
					}
				}
			}
			
			if (!found) {
				return null;
			}
			
		}
		
		return ret.trim();
		
	}
	
	public void addRegularAttribute(AvatarBlock _ab, TAttribute _a, String _preName) {
		int type = 0;
		if (_a.getType() == TAttribute.INTEGER){
			type = AvatarType.INTEGER;
		} else if (_a.getType() == TAttribute.NATURAL){
			type = AvatarType.INTEGER;
		} else if (_a.getType() == TAttribute.BOOLEAN) {
			type = AvatarType.BOOLEAN;
		} else if (_a.getType() == TAttribute.TIMER) {
			type = AvatarType.TIMER;
		}
		AvatarAttribute aa = new AvatarAttribute(_preName + _a.getId(), type, _a);
		aa.setInitialValue(_a.getInitialValue());
		_ab.addAttribute(aa);
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
		Vector types;
		
		for(AvatarBDBlock block: _blocks) {
			ab = new AvatarBlock(block.getBlockName(), block);
			_as.addBlock(ab);
			listE.addCor(ab, block);
			block.setAVATARID(ab.getID());
			
			// Create attributes
			v = block.getAttributeList();
			for(i=0; i<v.size(); i++) {
				a = (TAttribute)(v.elementAt(i));
				if (a.getType() == TAttribute.INTEGER){
					addRegularAttribute(ab, a, "");
				} else if (a.getType() == TAttribute.NATURAL){
					addRegularAttribute(ab, a, "");
				} else if (a.getType() == TAttribute.BOOLEAN) {
					addRegularAttribute(ab, a, "");
				} else if (a.getType() == TAttribute.TIMER) {
					addRegularAttribute(ab, a, "");
				} else {
					// other
					//TraceManager.addDev(" -> Other type found: " + a.getTypeOther());
					types = adp.getAvatarBDPanel().getAttributesOfDataType(a.getTypeOther());
					if (types == null) {
						CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + a.getTypeOther() + " used in " + ab.getName());
						ce.setAvatarBlock(ab);
						ce.setTDiagramPanel(adp.getAvatarBDPanel());
						addCheckingError(ce);
						return;
					} else {
						for(int j=0; j<types.size(); j++) {
							addRegularAttribute(ab, (TAttribute)(types.elementAt(j)), a.getId() + "__");
						}
					}
					
				}
			}
			
			// Create methods
			v = block.getMethodList();
			for(i=0; i<v.size(); i++) {
				uiam = (AvatarMethod)(v.get(i));
				atam = new avatartranslator.AvatarMethod(uiam.getId(), uiam);
				ab.addMethod(atam);
				makeParameters(ab, atam, uiam);
				makeReturnParameters(ab, block, atam, uiam);
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
				makeParameters(ab, atas, uias);
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
	}
	
	public void makeBlockStateMachines(AvatarSpecification _as) {
		// Make state machine of blocks
		for(AvatarBlock block: _as.getListOfBlocks()) {
			makeStateMachine(_as, block);
		}
		
	}
	
	public void makeReturnParameters(AvatarBlock _ab, AvatarBDBlock _block, avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
		String rt = _uiam.getReturnType().trim();
		AvatarAttribute aa;
		Vector types;
		TAttribute ta;
		int type;
		
		if (rt.length() == 0) {
			return;
		}
		
		if ((rt.compareTo("int") == 0) || (rt.compareTo("bool") == 0)) {
				aa = new AvatarAttribute("return__0", AvatarType.getType(rt), _block); 
				_atam.addReturnParameter(aa);
		} else {
			types = adp.getAvatarBDPanel().getAttributesOfDataType(rt);
			if (types == null) {
				CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + rt + " declared as a return parameter of a method of " + _block.getName());
				ce.setAvatarBlock(_ab);
				ce.setTDiagramPanel(adp.getAvatarBDPanel());
				addCheckingError(ce);
				return;
			} else {
				for(int j=0; j<types.size(); j++) {
					ta = (TAttribute)(types.elementAt(j));
					if (ta.getType() == TAttribute.INTEGER){
						type = AvatarType.INTEGER;
					} else if (ta.getType() == TAttribute.NATURAL){
						type = AvatarType.INTEGER;
					} else if (ta.getType() == TAttribute.BOOLEAN) {
						type = AvatarType.BOOLEAN;
					} else {
						type = AvatarType.INTEGER;
					}
					aa = new AvatarAttribute("return__" + j, type, _block); 
					_atam.addReturnParameter(aa);
				}
			}
		}
		
	}
	
	public void makeParameters(AvatarBlock _block, avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
		String typeIds[] = _uiam.getTypeIds();
		String types[] = _uiam.getTypes();
		AvatarAttribute aa;
		TAttribute ta;
		Vector v;
		int type = 0;
		
		for(int i=0; i<types.length; i++) {
			v = adp.getAvatarBDPanel().getAttributesOfDataType(types[i]);
			if (v == null) {
				if (AvatarType.getType(types[i]) == -1) {
					CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + types[i] + " declared in method" + _atam + " of block " + _block.getName());
					ce.setAvatarBlock(_block);
					ce.setTDiagramPanel(adp.getAvatarBDPanel());
					addCheckingError(ce);
				}
				aa = new AvatarAttribute(typeIds[i], AvatarType.getType(types[i]), _uiam);
				_atam.addParameter(aa);
			} else {
				for(int j=0; j<v.size(); j++) {
					ta = (TAttribute)(v.get(j));
					if (ta.getType() == TAttribute.INTEGER){
						type = AvatarType.INTEGER;
					} else if (ta.getType() == TAttribute.NATURAL){
						type = AvatarType.INTEGER;
					} else if (ta.getType() == TAttribute.BOOLEAN) {
						type = AvatarType.BOOLEAN;
					} else if (ta.getType() == TAttribute.TIMER) {
						type = AvatarType.TIMER;
					}
					aa = new AvatarAttribute(typeIds[i] + "__" + ta.getId(), type, _uiam);
					_atam.addParameter(aa);
				}
			}
		}
	}
	
	public void manageAttribute(String _name, AvatarBlock _ab, AvatarActionOnSignal _aaos, TDiagramPanel _tdp, TGComponent _tgc, String _idOperator) {
		TAttribute ta =  adp.getAvatarBDPanel().getAttribute(_name, _ab.getName());
		if (ta == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
			ce.setAvatarBlock(_ab);
			ce.setTDiagramPanel(_tdp);
			ce.setTGComponent(_tgc);
			addCheckingError(ce);
			TraceManager.addDev("not found");
			return ;
		}
		
		//TraceManager.addDev("Found: " + ta.getId());
		
		AvatarAttribute aa;
		Vector v = new Vector();
		int i;
		TAttribute tatmp;
		
		if (ta.getType() == TAttribute.OTHER) {
			Vector v0 = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
			for(i=0; i<v0.size(); i++) {
				tatmp = (TAttribute)(v0.get(i));
				v.add(_name + "__" + tatmp.getId());
				}
		} else {
			v.add(_name);
		}
		
		//TraceManager.addDev("Size of vector:" + v.size());
		for(i=0; i<v.size(); i++) {
			aa = _ab.getAvatarAttributeWithName((String)(v.get(i)));
			if (aa == null) {
				CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
				ce.setAvatarBlock(_ab);
				ce.setTDiagramPanel(_tdp);
				ce.setTGComponent(_tgc);
				addCheckingError(ce);
				return ;
			} else {
				//TraceManager.addDev("-> Adding attr in action on signal in block " + _ab.getName() + ":" + _name + "__" + tatmp.getId());
				_aaos.addValue((String)(v.get(i)));
			}
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
		
		// First pass: creating AVATAR components, but no interconnection between them
		iterator = asmdp.getAllComponentList().listIterator();
		AvatarSMDReceiveSignal asmdrs;
		AvatarSMDSendSignal asmdss;
		AvatarSMDRandom asmdrand;
		
		AvatarStateMachine asm = _ab.getStateMachine();
		avatartranslator.AvatarSignal atas;
		AvatarActionOnSignal aaos;
		AvatarAttribute aa;
		AvatarStopState astop;
		AvatarStartState astart;
		AvatarState astate;
		AvatarRandom arandom;
		AvatarSetTimer asettimer;
		AvatarResetTimer aresettimer;
		AvatarExpireTimer aexpiretimer;
		AvatarRelation ar;
		int i;
		String tmp;
		TAttribute ta;
		
		int choiceID = 0;
		int error;
		String tmp1, tmp2;
		
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
					// Get relation of that signal
					ar = _as.getAvatarRelationWithSignal(atas);
					if (ar == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Signal used for receiving in " + asmdrs.getValue() + " is not connected to a channel");
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
						//_as.addBroadcastSignal(atas);
					} 
					aaos = new AvatarActionOnSignal("action_on_signal", atas, tgc);
					if (asmdrs.hasCheckableAccessibility()) {
						aaos.setCheckable();
					}
					if (aaos.isSending()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "a sending signal is used for receiving: " + asmdrs.getValue());
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					}
					if (asmdrs.getNbOfValues() == -1){
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
								manageAttribute(tmp, _ab, aaos, tdp, tgc, asmdrs.getValue());
							}
						}
						
						if (aaos.getNbOfValues() != atas.getListOfAttributes().size()) {
							CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> nb of parameters does not match definition");
							TraceManager.addDev(" ERROR NB: in signal : " + aaos.getNbOfValues() + " in signal def:" + atas.getListOfAttributes().size() + " NAME=" + atas.getName());
							ce.setAvatarBlock(_ab);
							ce.setTDiagramPanel(tdp);
							ce.setTGComponent(tgc);
							addCheckingError(ce);
						}
						
						// Checking expressions passed as parameter
						for(i=0; i<aaos.getNbOfValues(); i++) {
							String theVal = aaos.getValue(i);
							if (atas.getListOfAttributes().get(i).isInt()) {
								if (AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, theVal) < 0) {
									CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> value at index #" + i + " does not match definition");
									//TraceManager.addDev(" ERROR NB: in signal : " + aaos.getNbOfValues() + " in signal def:" + atas.getListOfAttributes().size() + " NAME=" + atas.getName());
									ce.setAvatarBlock(_ab);
									ce.setTDiagramPanel(tdp);
									ce.setTGComponent(tgc);
									addCheckingError(ce);
								}
							} else {
								// We assume it is a bool attribute
								if (AvatarSyntaxChecker.isAValidBoolExpr(_as, _ab, theVal) < 0) {
									CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> value at index #" + i + " does not match definition");
									//TraceManager.addDev(" ERROR NB: in signal : " + aaos.getNbOfValues() + " in signal def:" + atas.getListOfAttributes().size() + " NAME=" + atas.getName());
									ce.setAvatarBlock(_ab);
									ce.setTDiagramPanel(tdp);
									ce.setTGComponent(tgc);
									addCheckingError(ce);
								}
							}
						}
						//adag.setActionValue(makeTIFAction(asmdrs.getValue(), "?"));
						listE.addCor(aaos, tgc);
						tgc.setAVATARID(aaos.getID());
						asm.addElement(aaos);
					}
				}
				
			// Send signals
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
					// Get relation of that signal
					ar = _as.getAvatarRelationWithSignal(atas);
					if (ar == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Signal used for sending in " + asmdss.getValue() + " is not connected to a channel");
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
						//_as.addBroadcastSignal(atas);
					} 
					
					aaos = new AvatarActionOnSignal("action_on_signal", atas, tgc);
					if (asmdss.hasCheckableAccessibility()) {
						aaos.setCheckable();
					}
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
								manageAttribute(tmp, _ab, aaos, tdp, tgc, asmdss.getValue());
							}
						}
						if (aaos.getNbOfValues() != atas.getListOfAttributes().size()) {
							CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal sending: " + asmdss.getValue() + " -> nb of parameters does not match definition");
							TraceManager.addDev(" ERROR NB: in signal : " + aaos.getNbOfValues() + " in signal def:" + atas.getListOfAttributes().size() + " NAME=" + atas.getName());
							ce.setAvatarBlock(_ab);
							ce.setTDiagramPanel(tdp);
							ce.setTGComponent(tgc);
							addCheckingError(ce);
						}
						
						// Checking expressions passed as parameter
						for(i=0; i<aaos.getNbOfValues(); i++) {
							String theVal = aaos.getValue(i);
							if (atas.getListOfAttributes().get(i).isInt()) {
								if (AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, theVal) < 0) {
									CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal sending: " + asmdss.getValue() + " -> value at index #" + i + " does not match definition");
									//TraceManager.addDev(" ERROR NB: in signal : " + aaos.getNbOfValues() + " in signal def:" + atas.getListOfAttributes().size() + " NAME=" + atas.getName());
									ce.setAvatarBlock(_ab);
									ce.setTDiagramPanel(tdp);
									ce.setTGComponent(tgc);
									addCheckingError(ce);
								}
							} else {
								// We assume it is a bool attribute
								if (AvatarSyntaxChecker.isAValidBoolExpr(_as, _ab, theVal) < 0) {
									CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal sending: " + asmdss.getValue() + " -> value at index #" + i + " does not match definition");
									//TraceManager.addDev(" ERROR NB: in signal : " + aaos.getNbOfValues() + " in signal def:" + atas.getListOfAttributes().size() + " NAME=" + atas.getName());
									ce.setAvatarBlock(_ab);
									ce.setTDiagramPanel(tdp);
									ce.setTGComponent(tgc);
									addCheckingError(ce);
								}
							}
						}
						
						// Check that tmp is the identifier of an attribute
						/*aa = _ab.getAvatarAttributeWithName(tmp);
						if (aa == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in signal expression: " + asmdss.getValue());
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
						} else {
						aaos.addValue(tmp);
						}*/
						//adag.setActionValue(makeTIFAction(asmdrs.getValue(), "?"));
						listE.addCor(aaos, tgc);
						tgc.setAVATARID(aaos.getID());
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
				if (tgc.hasCheckableAccessibility()) {
					astate.setCheckable();
				}
				listE.addCor(astate, tgc);
				tgc.setAVATARID(astate.getID());
			
				
			// Choice
			} else if (tgc instanceof AvatarSMDChoice) {
				astate = new AvatarState("choice__" + choiceID, tgc);
				choiceID ++;
				asm.addElement(astate);
				listE.addCor(astate, tgc);
				tgc.setAVATARID(astate.getID());
				
			// Random
			} else if (tgc instanceof AvatarSMDRandom) {
				asmdrand = (AvatarSMDRandom)tgc;
				arandom = new AvatarRandom("random", tgc);
				tmp1 = modifyString(asmdrand.getMinValue());
				error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
				if (error < 0) {
					makeError(error, tdp, _ab, tgc, "min value of random", tmp1);
				} 
				tmp2 = modifyString(asmdrand.getMaxValue());
				error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
				if (error < 0) {
					makeError(error, tdp, _ab, tgc, "max value of random", tmp2);
				} 
				arandom.setValues(tmp1, tmp2);
				arandom.setFunctionId(asmdrand.getFunctionId());
				
				tmp1 = modifyString(asmdrand.getVariable());
				aa = _ab.getAvatarAttributeWithName(tmp1);
				
				if (aa == null) {
					makeError(error, tdp, _ab, tgc, "variable of random", tmp2);
				}
				arandom.setVariable(tmp1);
				
				asm.addElement(arandom);
				listE.addCor(arandom, tgc);	
				tgc.setAVATARID(arandom.getID());
				
			// Set timer
			} else if (tgc instanceof AvatarSMDSetTimer) {
				tmp = ((AvatarSMDSetTimer)tgc).getTimerName();
				aa = _ab.getAvatarAttributeWithName(tmp);
				if (aa == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer setting");
					ce.setAvatarBlock(_ab);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					if (aa.getType() != AvatarType.TIMER) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer setting: shall be a parameter of type \"Timer\"");
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					} else {
						tmp = modifyString(((AvatarSMDSetTimer)tgc).getTimerValue());
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "value of the timer setting", tmp);
						}
						asettimer = new AvatarSetTimer("settimer__" + aa.getName(), tgc);
						asettimer.setTimer(aa);
						asettimer.setTimerValue(tmp);
						asm.addElement(asettimer);
						listE.addCor(asettimer, tgc);	
						tgc.setAVATARID(asettimer.getID());
					}
				}
				
			// Reset timer
			} else if (tgc instanceof AvatarSMDResetTimer) {
				tmp = ((AvatarSMDResetTimer)tgc).getTimerName();
				aa = _ab.getAvatarAttributeWithName(tmp);
				if (aa == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer reset");
					ce.setAvatarBlock(_ab);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					if (aa.getType() != AvatarType.TIMER) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer reset: shall be a parameter of type \"Timer\"");
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					} else {
						aresettimer = new AvatarResetTimer("resettimer__" + aa.getName(), tgc);
						aresettimer.setTimer(aa);
						asm.addElement(aresettimer);
						listE.addCor(aresettimer, tgc);	
						tgc.setAVATARID(aresettimer.getID());
					}
				}
				
			// Expire timer
			} else if (tgc instanceof AvatarSMDExpireTimer) {
				tmp = ((AvatarSMDExpireTimer)tgc).getTimerName();
				aa = _ab.getAvatarAttributeWithName(tmp);
				if (aa == null) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer expiration");
					ce.setAvatarBlock(_ab);
					ce.setTDiagramPanel(tdp);
					ce.setTGComponent(tgc);
					addCheckingError(ce);
				} else {
					if (aa.getType() != AvatarType.TIMER) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer expiration: shall be a parameter of type \"Timer\"");
						ce.setAvatarBlock(_ab);
						ce.setTDiagramPanel(tdp);
						ce.setTGComponent(tgc);
						addCheckingError(ce);
					} else {
						aexpiretimer = new AvatarExpireTimer("expiretimer__" + aa.getName(), tgc);
						aexpiretimer.setTimer(aa);
						asm.addElement(aexpiretimer);
						listE.addCor(aexpiretimer, tgc);	
						tgc.setAVATARID(aexpiretimer.getID());
					}
				}
			
			// Start state
			} else if (tgc instanceof AvatarSMDStartState) {
				astart = new AvatarStartState("start", tgc);
				listE.addCor(astart, tgc);
				tgc.setAVATARID(astart.getID());
				asm.addElement(astart);
				if (tgc.getFather() == null) {
					asm.setStartState(astart);
				}
				
			// Stop state
			} else if (tgc instanceof AvatarSMDStopState) {
				astop = new AvatarStopState("stop", tgc);
				listE.addCor(astop, tgc);
				tgc.setAVATARID(astop.getID());
				asm.addElement(astop);
			}
		}
		
		if (checkingErrors.size() != size) {
			return;
		}
		
		// Remove all internal start states
		asm.removeAllInternalStartStates();
		
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
						tmp = modifyString(asmdco.getGuard());
						
						if (AvatarSpecification.isElseGuard(tmp)) {
							at.setGuard(tmp);
						} else {
							error = AvatarSyntaxChecker.isAValidGuard(_as, _ab, tmp);
							if (error < 0) {
								makeError(error, tdp, _ab, tgc, "transition guard", tmp); 
							} else {
								at.setGuard(tmp);
							}
						}
						
						// Delays
						tmp1 = modifyString(asmdco.getAfterMinDelay());
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "after min delay", tmp1);
							tmp1 = null;
						} 
						tmp2 = modifyString(asmdco.getAfterMaxDelay());
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "after max delay", tmp2);
							tmp2 = null;
						} 
						
						if ((tmp1 != null) && (tmp2 != null)) {
							at.setDelays(tmp1, tmp2);
						}
						
						// Compute min and max
						tmp1 = modifyString(asmdco.getComputeMinDelay());
						error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
						if (error < 0) {
							makeError(error, tdp, _ab, tgc, "compute min ", tmp1);
							tmp1 = null;
						} 
						tmp2 = modifyString(asmdco.getComputeMaxDelay());
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
								s = modifyString(s.trim());
								// Variable assignation or method call?
								
								//TraceManager.addDev("IsAVariable Assignation: " + s + " -> " + isAVariableAssignation(s));
								
								if (!isAVariableAssignation(s)) {
									// Method call
									s = modifyStringMethodCall(s, _ab.getName());
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
						tgc.setAVATARID(at.getID());
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
				
				//TraceManager.addDev("Searching block with name " + block1.getBlockName());
				b1 = _as.getBlockWithName(block1.getBlockName());
				b2 = _as.getBlockWithName(block2.getBlockName());
				
				if ((b1 != null) && (b2 != null)) {
				
					r = new AvatarRelation("relation", b1, b2, tgc);
					// Signals of l1
					l1 = port.getListOfSignalsOrigin();
					l2 = port.getListOfSignalsDestination();
					
					for(i=0; i<l1.size(); i++) {
						name1 = AvatarSignal.getSignalNameFromFullSignalString(l1.get(i));
						name2 = AvatarSignal.getSignalNameFromFullSignalString(l2.get(i));
						//TraceManager.addDev("Searching signal with name " + name1 +  " in block " + b1.getName());
						atas1 = b1.getAvatarSignalWithName(name1);
						atas2 = b2.getAvatarSignalWithName(name2);
						if ((atas1 != null) && (atas2 != null)) {
							r.addSignals(atas1, atas2);
						} else {
							TraceManager.addDev("null gates in AVATAR relation: " + name1 + " " + name2);
						}
					}
					
					// Attribute of the relation
					r.setBlocking(port.isBlocking());
					r.setAsynchronous(port.isAsynchronous());
					r.setSizeOfFIFO(port.getSizeOfFIFO());
					r.setPrivate(port.isPrivate());
					
					_as.addRelation(r);
				} else {
					TraceManager.addDev("Null block b1=" + b1 + " b2=" + b2);
				}
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
	
	private String modifyString(String _input) {
		return Conversion.replaceAllChar(_input, '.', "__");
	}
	
	private String modifyStringMethodCall(String _input, String _blockName) {
		
		int index0 = _input.indexOf('(');
		int index1 = _input.indexOf(')');
		
		if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
			return _input;
		}
		
		String s = _input.substring(index0+1, index1).trim();
		String output = "";
		
		if (s.length() == 0) {
			return _input;
		}
		
		//TraceManager.addDev("-> -> Analyzing method call " + s);
		TAttribute ta, tatmp; 
		
		String [] actions = s.split(",");
		s = "";
		for(int i=0; i<actions.length; i++) {
			ta = adp.getAvatarBDPanel().getAttribute(actions[i].trim(), _blockName);
			if (ta == null) {
				s = s + actions[i].trim();
			} else {
				if (ta.getType() == TAttribute.OTHER) {
					Vector v0 = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
					for(int j=0; j<v0.size(); j++) {
						tatmp = (TAttribute)(v0.get(j));
						s += actions[i].trim() + "__" + tatmp.getId();
						if (j != v0.size()-1) {
							s = s + ", ";
						}
					}
				} else {
					s = s + actions[i].trim();
				}
			}
			if (i != actions.length-1) {
					s = s + ", ";
			}
		}
		
		s  = _input.substring(0, index0) + "(" + s + ")";
		
		// Managing output parameters
		index0 = s.indexOf("=");
		if (index0 != -1) {
			String param = s.substring(0, index0).trim();
			ta = adp.getAvatarBDPanel().getAttribute(param, _blockName);
			if (ta == null) {
				TraceManager.addDev("-> -> NULL Param " + param + " in block " + _blockName);
				s = param + s.substring(index0, s.length());
			} else {
				if (ta.getType() == TAttribute.OTHER) {
					String newparams = "";
					Vector v0 = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
					for(int j=0; j<v0.size(); j++) {
						tatmp = (TAttribute)(v0.get(j));
						newparams += param + "__" + tatmp.getId();
						if (j != v0.size()-1) {
							newparams = newparams + ", ";
						}
					}
					if (v0.size() > 1) {
						newparams = "(" + newparams + ")";
					}
					s = newparams + s.substring(index0, s.length());
				} else {
					s = param + s.substring(index0, s.length());
				}
			}
		}
		
		//TraceManager.addDev("-> -> Returning method call " + s);
		
		return s;
	}
	
	public boolean isAVariableAssignation (String _input) {
		int index = _input.indexOf('=');
		if (index == -1) {
			return false;
		}
		
		// Must check whether what follows the '=' is a function or not.
		String tmp = _input.substring(index+1, _input.length()).trim();
		
		index = tmp.indexOf('(');
		if (index == -1) {
			return true;
		}
		
		tmp = tmp.substring(0, index);
		
		//TraceManager.addDev("rest= >" + tmp + "<");
		int length = tmp.length();
		tmp = tmp.trim();
		if (tmp.length() != length) {
			TraceManager.addDev("pb of length");
			return true;
		}
		
		return !(TAttribute.isAValidId(tmp, false, false)); 
	}
	

	
}
