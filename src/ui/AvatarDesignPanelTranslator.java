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

import translator.*;
import ui.window.*;


public class AvatarDesignPanelTranslator {
	protected AvatarDesignPanel adp;
	protected Vector checkingErrors, warnings;
	protected CorrespondanceTGElement listE; // usual list
	protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
	protected LinkedList <TDiagramPanel> panels;
	protected LinkedList <ActivityDiagram> activities;
	
	public AvatarDesignPanelTranslator(AvatarDesignPanel _adp) {
		adp = _adp;
		reinit();
	}
	
	public void reinit() {
		checkingErrors = new Vector();
		warnings = new Vector();
		listE = new CorrespondanceTGElement();
		listB = new CorrespondanceTGElement();
		panels = new LinkedList <TDiagramPanel>();
		activities = new LinkedList <ActivityDiagram>();
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
	
	public TURTLEModeling generateTURTLEModeling() {
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
		TGComponent tss = null;
		int cptStart = 0;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarSMDStartState){
				tss = tgc;
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
		
		panels.add(tdp);
		activities.add(ad);
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
	
	
	
	/*private void addTDataAttributes(TAttribute a, TClass t, ClassDiagramPanelInterface tdp, TURTLEModeling tm) {
		//System.out.println("Find data: " + a.getId() + " getTypeOther=" + a.getTypeOther());
		if (tdp instanceof TClassDiagramPanel) {
			TCDTData tdata  = ((TClassDiagramPanel)tdp).findTData(a.getTypeOther());
			if (tdata == null) {
				CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown type: " + a.getTypeOther());
				ce.setTClass(t);
				ce.setTDiagramPanel((TDiagramPanel)tdp);
				addCheckingError(ce);
				return ;
			}
			
			Vector v = tdata.getAttributes();
			TAttribute b; Param p;
			for(int i=0; i<v.size(); i++) {
				b = (TAttribute)(v.elementAt(i));
				if (b.getType() == TAttribute.NATURAL) {
					p = new Param(a.getId() + "__" + b.getId(), Param.NAT, b.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
				if (b.getType() == TAttribute.BOOLEAN) {
					p = new Param(a.getId() + "__" + b.getId(), Param.BOOL, b.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
				
				if (b.getType() == TAttribute.QUEUE_NAT) {
					p = new Param(a.getId() + "__" + b.getId(), Param.QUEUE_NAT, b.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
			}
		}
		
	}
	
	private void buildActivityDiagram(TClass t) {
		int j;
		//TActivityDiagramPanel tadp;
		ActivityDiagramPanelInterface adpi;
		TDiagramPanel tdp;
		//t.printParams();
		
		// find the panel of this TClass
		TClassInterface tci = (TClassInterface)(listE.getTG(t));
		
		String name = tci.getClassName();
		int index_name = name.indexOf(':');
		// instance
		if (index_name != -1) {
			name = name.substring(index_name+2, name.length());
		}
		
		adpi = tci.getBehaviourDiagramPanel();
		if (adpi == null) {
			return;
		}
		
		tdp = (TDiagramPanel)adpi;
		
		int indexTdp = panels.indexOf(tdp);
		if (indexTdp > -1) {
			System.out.println("Found similar activity diagram for " + t.getName());
			t.setActivityDiagram(activities.get(indexTdp).duplicate(t));
			
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
		LinkedList list = adpi.getComponentList();
		Iterator iterator = list.listIterator();
		TGComponent tgc;
		TGComponent tss = null;
		int cptStart = 0;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TADStartState){
				tss = tgc;
				cptStart ++;
			} else if (tgc instanceof TOSADStartState) {
				tss = tgc;
				cptStart ++;
			}
		}
		
		if (tss == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the activity diagram of " + name);
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}
		
		if (cptStart > 1) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the activity diagram of " + name);
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}
		
		TADActionState tadas;
		
		ADStart ads;
		//ADActionState ada;
		ADActionStateWithGate adag;
		ADActionStateWithParam adap;
		ADActionStateWithMultipleParam adamp;
		ADChoice adch;
		ADDelay add;
		ADJunction adj;
		ADLatency adl;
		ADParallel adp;
		ADSequence adseq;
		ADPreempt adpre;
		ADStop adst;
		ADTimeInterval adti;
		ADTLO adtlo;
		ADTimeCapture adtc;
		String s, s1;
		Gate g;
		Param p;
		
		int nbActions;
		String sTmp;
		
		int startIndex = listE.getSize();
		
		// Creation of the activity diagram
		ads = new ADStart();
		listE.addCor(ads, tss);
		ActivityDiagram ad = new ActivityDiagram(ads);
		t.setActivityDiagram(ad);
		
		panels.add(tdp);
		activities.add(ad);
		
		
		//System.out.println("Making activity diagram of " + t.getName());
		
		// Creation of other elements
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			if (tgc instanceof TADActionState) {
				tadas = (TADActionState)tgc;
				s = ((TADActionState)tgc).getAction();
				s = s.trim();
				//remove ';' if last character
				if (s.substring(s.length()-1, s.length()).compareTo(";") == 0) {
					s = s.substring(0, s.length()-1);
				}
				nbActions = Conversion.nbChar(s, ';') + 1;
				//System.out.println("Nb Actions in state: " + nbActions);
				
				s = TURTLEModeling.manageDataStructures(t, s);
				
				g = t.getGateFromActionState(s);
				p = t.getParamFromActionState(s);
				if ((g != null) && (nbActions == 1)){
					//System.out.println("Action state with gate found " + g.getName() + " value:" + t.getActionValueFromActionState(s));
					adag = new ADActionStateWithGate(g);
					ad.addElement(adag);
					s1 = t.getActionValueFromActionState(s);
					//System.out.println("s1=" + s1);
					//System.out.println("Adding type");
					s1 = TURTLEModeling.manageGateDataStructures(t, s1);
					
					//System.out.println("hi");
					if (s1 == null) {
						//System.out.println("ho");
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Invalid expression: " + t.getActionValueFromActionState(s));
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						tadas.setStateAction(ErrorHighlight.UNKNOWN_AS);
						//return;
					} else {
						tadas.setStateAction(ErrorHighlight.GATE);
						s1 = TURTLEModeling.addTypeToDataReceiving(t, s1);
						
						adag.setActionValue(s1);
						//System.out.println("Adding correspondance tgc=" + tgc +  "adag=" + adag);
						listE.addCor(adag, tgc);
						listB.addCor(adag, tgc);
					}
				} else if ((p != null) && (nbActions == 1)){
					//System.out.println("Action state with param found " + p.getName() + " value:" + t.getExprValueFromActionState(s));
					if (t.getExprValueFromActionState(s).trim().startsWith("=")) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, s + " should not start with a '=='");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);  
						tadas.setStateAction(ErrorHighlight.UNKNOWN_AS);
					}
					adap = new ADActionStateWithParam(p);
					ad.addElement(adap);
					adap.setActionValue(TURTLEModeling.manageDataStructures(t, t.getExprValueFromActionState(s)));
					listE.addCor(adap, tgc);
					listB.addCor(adap, tgc);
					tadas.setStateAction(ErrorHighlight.ATTRIBUTE);
					
				} else if ((p != null) && (nbActions > 1)){
					//System.out.println("Action state with multi param found " + p.getName() + " value:" + t.getExprValueFromActionState(s));
					// Checking params
					CheckingError ce;
					Vector v;
					for(j=0; j<nbActions; j++) {
						sTmp = TURTLEModeling.manageDataStructures(t,((TADActionState)(tgc)).getAction(j));
						if (sTmp == null) {
							ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (0) (" + s + "): \"" + s + "\" is not a correct expression");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
							tadas.setStateAction(ErrorHighlight.UNKNOWN_AS);
						}
						
						p = t.getParamFromActionState(sTmp);
						if (p == null) {
							ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (1) (" + s + "): \"" + sTmp + "\" is not a correct expression");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
							tadas.setStateAction(ErrorHighlight.UNKNOWN_AS);
						}
					}
				
					
					tadas.setStateAction(ErrorHighlight.ATTRIBUTE);
					ADComponent adtmp = null;
					for(j=0; j<nbActions; j++) {
						sTmp = TURTLEModeling.manageDataStructures(t,((TADActionState)(tgc)).getAction(j));
						p = t.getParamFromActionState(sTmp);
						adap = new ADActionStateWithParam(p);
						ad.addElement(adap);
						if (adtmp != null) {
							adtmp.addNext(adap);
						} else {
							listB.addCor(adap, tgc);
						}
						adtmp = adap;
						adap.setActionValue(t.getExprValueFromActionState(sTmp));
					}
					
					listE.addCor(adtmp, tgc);
					
				} else {
					// Is it of kind: tdata = tdata'?
					int index = s.indexOf("=");
					if (index > -1) {
						String name0 = s.substring(0,index).trim();
						String name1 = s.substring(index+1,s.length()).trim();
						Vector attributes = tci.getAttributes();
						int index0 = -1;
						int index1 = -1;
						TAttribute ta, ta0 = null, ta1 = null;
						
						for(j=0; j<attributes.size(); j++) {
							ta = (TAttribute)(attributes.get(j));
							if (ta.getId().compareTo(name0) == 0) {
								index0 = j;
								ta0 = ta;
							}
							if (ta.getId().compareTo(name1) == 0) {
								index1 = j;
								ta1 = ta;
							}
						}
						
						if (((index0 != -1) && (index1 != -1)) && (ta0.getTypeOther().compareTo(ta1.getTypeOther()) == 0)) {
							// Expand the equality!
							tadas.setStateAction(ErrorHighlight.ATTRIBUTE);
							
							String nameTmp;
							Vector v0 = t.getParamStartingWith(ta0.getId()+ "__");
							ADComponent adtmp = null;
							
							for(j=0; j<v0.size(); j++) {
								p = (Param)(v0.get(j));
								adap = new ADActionStateWithParam(p);
								ad.addElement(adap);
								if (adtmp != null) {
									adtmp.addNext(adap);
								} else {
									listB.addCor(adap, tgc);
								}
								adtmp = adap;
								nameTmp = p.getName();
								nameTmp = nameTmp.substring(nameTmp.indexOf("__"), nameTmp.length());
								adap.setActionValue(name1 + nameTmp);
							}
							
							listE.addCor(adtmp, tgc);
						} else {
							//System.out.println("Unknown param 0 or 1");
							CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (2) (" + s + "): \"" + s + "\" is not a correct expression");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
							tadas.setStateAction(ErrorHighlight.UNKNOWN_AS);
						}
						
					} else {
						//System.out.println("Unknown param");
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (2) (" + s + "): \"" + s + "\" is not a correct expression");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						tadas.setStateAction(ErrorHighlight.UNKNOWN_AS);
					}
					//System.out.println("Bad action state found " + s);
				}
				
			} else if (tgc instanceof TADTimeCapture) {
				p = t.getParamByName(tgc.getValue().trim());
				if (p != null){
					System.out.println("Time capture with param " + p.getName());
					adtc = new ADTimeCapture(p);
					ad.addElement(adtc);
					((TADTimeCapture)tgc).setStateAction(ErrorHighlight.ATTRIBUTE);
				} else {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown variable: " + tgc.getValue());
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
					((TADTimeCapture)tgc).setStateAction(ErrorHighlight.UNKNOWN_AS);
				}
				
			// Get element from Array
			} else if (tgc instanceof TADArrayGetState) {
				TADArrayGetState ags = (TADArrayGetState)tgc;
				sTmp = ags.getIndex();
				try {
					nbActions = Integer.decode(sTmp).intValue();
					
					p = t.getParamByName(ags.getVariable());
					if (p == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ags.getVariable() + ": unknown variable");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						ags.setStateAction(ErrorHighlight.UNKNOWN);
					} else {
						adap = new ADActionStateWithParam(p);
						p = t.getParamByName(ags.getArray() + "__" + nbActions);
						if (p == null) {
							CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ags.getArray() + "[" + ags.getIndex() + "]: unknown array or wrong index");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
							ags.setStateAction(ErrorHighlight.UNKNOWN);
						} else {
							ad.addElement(adap);
							adap.setActionValue(TURTLEModeling.manageDataStructures(t, ags.getArray() + "__" + nbActions));
							listE.addCor(adap, tgc);
							listB.addCor(adap, tgc);
							ags.setStateAction(ErrorHighlight.OK);
						}
					}
				} catch (Exception e) {
					// Index is not an absolute value
					System.out.println("Index is not an absolute value");
					Gate error = t.addNewGateIfApplicable("arrayOverflow");
					
					ADChoice choice1 = new ADChoice();
					ADJunction junc = new ADJunction();
					ADStop stop1 = new ADStop();
					ADActionStateWithGate adag1 = new ADActionStateWithGate(error);
					
					ad.addElement(choice1);
					ad.addElement(junc);
					ad.addElement(stop1);
					ad.addElement(adag1);
					
					String basicGuard = "(" + ags.getIndex() + ")";
					
					p = t.getParamByName(ags.getArray() + "__size");
					
					if (p == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ags.getArray() + "[" + ags.getIndex() + "]: unknown array or wrong index");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						ags.setStateAction(ErrorHighlight.UNKNOWN);
					} else {
						int size = 2;
						try {
							size = Integer.decode(p.getValue()).intValue();
						} catch (Exception e0) {
						}
						
						p = t.getParamByName(ags.getVariable());
						
						if (p == null) {
							CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ags.getVariable() + ": unknown variable");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
							ags.setStateAction(ErrorHighlight.UNKNOWN);
						} else {
							for(int i=0; i<size; i++) {
								//System.out.println("Adding guard: [" + basicGuard + "== " + i + "]");
								choice1.addGuard("[" + basicGuard + " == " + i + "]");
								adap = new ADActionStateWithParam(p);
								ad.addElement(adap);
								adap.setActionValue(TURTLEModeling.manageDataStructures(t, ags.getArray() + "__" + i));
								choice1.addNext(adap);
								adap.addNext(junc);
								ags.setStateAction(ErrorHighlight.OK);
							}
							
							choice1.addGuard("[" + basicGuard + "> (" + ags.getArray() + "__size - 1)]");
							choice1.addNext(adag1);
							adag1.addNext(stop1);
							
							listE.addCor(junc, tgc);
							listB.addCor(choice1, tgc);
							
						}
					}
				}
				
			} else if (tgc instanceof TADArraySetState) {
				TADArraySetState ass = (TADArraySetState)tgc;
				sTmp = ass.getIndex();
				try {
					nbActions = Integer.decode(sTmp).intValue();
					p = t.getParamByName(ass.getArray() + "__" + nbActions);
					if (p == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ass.getArray() + "[" + ass.getIndex() + "]: unknown array or wrong index");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						ass.setStateAction(ErrorHighlight.UNKNOWN);
					} else {
						adap = new ADActionStateWithParam(p);
						ad.addElement(adap);
						adap.setActionValue(TURTLEModeling.manageDataStructures(t, ass.getExpr()));
						listE.addCor(adap, tgc);
						listB.addCor(adap, tgc);
						ass.setStateAction(ErrorHighlight.OK);
					}
					
				} catch (Exception e) {
					// Index is not an absolute value
					//System.out.println("Set: Index is not an absolute value");
					Gate error = t.addNewGateIfApplicable("arrayOverflow");
					
					ADChoice choice1 = new ADChoice();
					ADJunction junc = new ADJunction();
					ADStop stop1 = new ADStop();
					ADActionStateWithGate adag1 = new ADActionStateWithGate(error);
					
					ad.addElement(choice1);
					ad.addElement(junc);
					ad.addElement(stop1);
					ad.addElement(adag1);
					
					String basicGuard = "(" + ass.getIndex() + ")";
					
					p = t.getParamByName(ass.getArray() + "__size");
					
					if (p == null) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ass.getArray() + "[" + ass.getIndex() + "]: unknown array or wrong index");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						ass.setStateAction(ErrorHighlight.UNKNOWN);
					} else {
						int size = 2;
						try {
							size = Integer.decode(p.getValue()).intValue();
						} catch (Exception e0) {
						}
						
						for(int i=0; i<size; i++) {
							//System.out.println("Adding guard: [" + basicGuard + "== " + i + "]");
							p = t.getParamByName(ass.getArray() + "__" + i);
							adap = null;
							if (p == null) {
								CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Array setting: " + ass.getArray() + "[" + ass.getIndex() + "]: unknown array or wrong index");
								ce.setTClass(t);
								ce.setTGComponent(tgc);
								ce.setTDiagramPanel(tdp);
								addCheckingError(ce);
								ass.setStateAction(ErrorHighlight.UNKNOWN);
							} else {
								choice1.addGuard("[" + basicGuard + " == " + i + "]");
								adap = new ADActionStateWithParam(p);
								ad.addElement(adap);
								adap.setActionValue(TURTLEModeling.manageDataStructures(t, ass.getExpr()));
								choice1.addNext(adap);
								adap.addNext(junc);
								ass.setStateAction(ErrorHighlight.OK);
							}
							
							choice1.addGuard("[" + basicGuard + "> (" + ass.getArray() + "__size - 1)]");
							choice1.addNext(adag1);
							adag1.addNext(stop1);
							
							listE.addCor(junc, tgc);
							listE.addCor(choice1, tgc);
							if (adap != null) {
								listE.addCor(adap, tgc);
							}
							listE.addCor(stop1, tgc);
							listE.addCor(adag1, tgc);
							listB.addCor(choice1, tgc);
							
						}
					}
				}
				
			} else if (tgc instanceof TADChoice) {
				adch = new ADChoice();
				ad.addElement(adch);
				listE.addCor(adch, tgc);
			} else if (tgc instanceof TADDeterministicDelay) {
				add = new ADDelay();
				ad.addElement(add);
				add.setValue(TURTLEModeling.manageGateDataStructures(t, ((TADDeterministicDelay)tgc).getDelayValue()));
				listE.addCor(add, tgc);
			} else if (tgc instanceof TADJunction) {
				adj = new ADJunction();
				ad.addElement(adj);
				listE.addCor(adj, tgc);
			} else if (tgc instanceof TADNonDeterministicDelay) {
				adl = new ADLatency();
				ad.addElement(adl);
				adl.setValue(TURTLEModeling.manageGateDataStructures(t, ((TADNonDeterministicDelay)tgc).getLatencyValue()));
				listE.addCor(adl, tgc);
			} else if (tgc instanceof TADParallel) {
				adp = new ADParallel();
				ad.addElement(adp);
				adp.setValueGate(((TADParallel)tgc).getValueGate());
				listE.addCor(adp, tgc);
			} else if (tgc instanceof TADSequence) {
				adseq = new ADSequence();
				ad.addElement(adseq);
				listE.addCor(adseq, tgc);
			} else if (tgc instanceof TADPreemption) {
				adpre = new ADPreempt();
				ad.addElement(adpre);
				listE.addCor(adpre, tgc);
			} else if (tgc instanceof TADStopState) {
				adst = new ADStop();
				ad.addElement(adst);
				listE.addCor(adst, tgc);
			} else if (tgc instanceof TADTimeInterval) {
				adti = new ADTimeInterval();
				ad.addElement(adti);
				adti.setValue(TURTLEModeling.manageGateDataStructures(t, ((TADTimeInterval)tgc).getMinDelayValue()), TURTLEModeling.manageGateDataStructures(t, ((TADTimeInterval)tgc).getMaxDelayValue()));
				listE.addCor(adti, tgc);
			} else if (tgc instanceof TADTimeLimitedOffer) {
				s = ((TADTimeLimitedOffer)tgc).getAction();
				g = t.getGateFromActionState(s);
				if (g != null) {
					adtlo = new ADTLO(g);
					ad.addElement(adtlo);
					adtlo.setLatency("0");
					s1 = t.getActionValueFromActionState(s);
					//System.out.println("Adding type");
					s1 = TURTLEModeling.manageGateDataStructures(t, s1);
					s1 = TURTLEModeling.addTypeToDataReceiving(t, s1);
					//System.out.println("Adding type done");
					adtlo.setAction(s1);
					adtlo.setDelay(TURTLEModeling.manageGateDataStructures(t, ((TADTimeLimitedOffer)tgc).getDelay()));
					listE.addCor(adtlo, tgc);
					((TADTimeLimitedOffer)tgc).setStateAction(ErrorHighlight.GATE);
				} else {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time-limited offer (" + s + ", " + ((TADTimeLimitedOffer)tgc).getDelay() + "): \"" + s + "\" is not a correct expression");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
					((TADTimeLimitedOffer)tgc).setStateAction(ErrorHighlight.UNKNOWN_AS);
					//System.out.println("Bad time limited offer found " + s);
				}
			} else if (tgc instanceof TADTimeLimitedOfferWithLatency) {
				s = ((TADTimeLimitedOfferWithLatency)tgc).getAction();
				g = t.getGateFromActionState(s);
				if (g != null) {
					adtlo = new ADTLO(g);
					ad.addElement(adtlo);
					adtlo.setLatency(TURTLEModeling.manageGateDataStructures(t, ((TADTimeLimitedOfferWithLatency)tgc).getLatency()));
					s1 = t.getActionValueFromActionState(s);
					//System.out.println("Adding type");
					s1 = TURTLEModeling.manageGateDataStructures(t, s1);
					s1 = TURTLEModeling.addTypeToDataReceiving(t, s1);
					//System.out.println("Adding type done");
					adtlo.setAction(s1);
					adtlo.setDelay(TURTLEModeling.manageGateDataStructures(t, ((TADTimeLimitedOfferWithLatency)tgc).getDelay()));
					listE.addCor(adtlo, tgc);
					((TADTimeLimitedOfferWithLatency)tgc).setStateAction(ErrorHighlight.GATE);
				} else {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time-limited offer (" + s + ", " + ((TADTimeLimitedOfferWithLatency)tgc).getLatency() + ", " + ((TADTimeLimitedOfferWithLatency)tgc).getDelay() + "): \"" + s + "\" is not a correct expression");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
					((TADTimeLimitedOfferWithLatency)tgc).setStateAction(ErrorHighlight.UNKNOWN_AS);
					//System.out.println("Bad time limited offer found " + s);
				}
				
				// TURTLE-OS AD
			} else if (tgc instanceof TOSADTimeInterval) {
				adti = new ADTimeInterval();
				ad.addElement(adti);
				adti.setValue(TURTLEModeling.manageGateDataStructures(t, ((TOSADTimeInterval)tgc).getMinDelayValue()), TURTLEModeling.manageGateDataStructures(t, ((TOSADTimeInterval)tgc).getMaxDelayValue()));
				listE.addCor(adti, tgc);
			} else if (tgc instanceof TOSADIntTimeInterval) {
				adti = new ADTimeInterval();
				ad.addElement(adti);
				adti.setValue(TURTLEModeling.manageGateDataStructures(t, ((TOSADIntTimeInterval)tgc).getMinDelayValue()), TURTLEModeling.manageGateDataStructures(t, ((TOSADIntTimeInterval)tgc).getMaxDelayValue()));
				listE.addCor(adti, tgc);
			} else if (tgc instanceof TOSADStopState) {
				adst = new ADStop();
				ad.addElement(adst);
				listE.addCor(adst, tgc);
			} else if (tgc instanceof TOSADJunction) {
				adj = new ADJunction();
				ad.addElement(adj);
				listE.addCor(adj, tgc);
			} else if (tgc instanceof TOSADChoice) {
				adch = new ADChoice();
				ad.addElement(adch);
				listE.addCor(adch, tgc);
			} if (tgc instanceof TOSADActionState) {
				s = ((TOSADActionState)tgc).getAction();
				s = s.trim();
				//remove ';' if last character
				if (s.substring(s.length()-1, s.length()).compareTo(";") == 0) {
					s = s.substring(0, s.length()-1);
				}
				nbActions = Conversion.nbChar(s, ';') + 1;
				
				if (nbActions>1) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, s + " should not start with a '=='");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
				} else {
					//s = TURTLEModeling.manageDataStructures(t, s);
					g = t.getGateFromActionState(s);
					p = t.getParamFromActionState(s);
					
					if (p != null) {
						adap = new ADActionStateWithParam(p);
						ad.addElement(adap);
						adap.setActionValue(TURTLEModeling.manageDataStructures(t, t.getExprValueFromActionState(s)));
						listE.addCor(adap, tgc);
					} else {
						adag = new ADActionStateWithGate(g);
						ad.addElement(adag);
						listE.addCor(adag, tgc);
						adag.setActionValue(s);
					}
				}
				//System.out.println("Nb Actions in state: " + nbActions);
			}
		}
		
		TGConnectingPoint p1, p2;
		//TGConnectorFullArrow tgco;
		TGComponent tgc1, tgc2, tgc3;
		ADComponent ad1, ad2;
		
		// Managing Java code
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof PreJavaCode) {
				ad1 = listE.getADComponentByIndex(tgc, tdp.count);
				if (ad1 != null) {
					ad1.setPreJavaCode(tgc.getPreJavaCode());
				}
			}
			if (tgc instanceof PostJavaCode) {
				ad1 = listE.getADComponentByIndex(tgc, tdp.count);
				if (ad1 != null) {
					ad1.setPostJavaCode(tgc.getPostJavaCode());
				}
			}
		}
		
		// Connecting elements
		TGConnectorBetweenElementsInterface tgcbei;
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TGConnectorBetweenElementsInterface) {
				tgcbei = (TGConnectorBetweenElementsInterface)tgc;
				p1 = tgcbei.getTGConnectingPointP1();
				p2 = tgcbei.getTGConnectingPointP2();
				
				// identification of connected components
				tgc1 = null; tgc2 = null;
				for(j=0; j<list.size(); j++) {
					tgc3 = 	(TGComponent)(list.get(j));
					if (tgc3.belongsToMe(p1)) {
						tgc1 = tgc3;
					}
					if (tgc3.belongsToMe(p2)) {
						tgc2 = tgc3;
					}
				}
				
				// connecting turtle modeling components
				if ((tgc1 != null) && (tgc2 != null)) {
					//ADComponent ad1, ad2;
					
					//System.out.println("tgc1 = " + tgc1.getValue() + " tgc2= "+ tgc2.getValue());
					
					ad1 = listE.getADComponentByIndex(tgc1, tdp.count);
					if ((tgc2 instanceof TADArrayGetState) || (tgc2 instanceof TADArraySetState) || (tgc2 instanceof TADActionState)) {
						ad2 = listB.getADComponent(tgc2);
					}  else {
						ad2 = listE.getADComponentByIndex(tgc2, tdp.count);
					}
					
					//System.out.println("ad1 = " + ad1 + " ad2= "+ ad2);
					
					if ((ad1 == null) || (ad2 == null)) {
						//System.out.println("Correspondance issue");
					}
					int index = 0;
					if ((ad1 != null ) && (ad2 != null)) {
						if ((tgc1 instanceof TADTimeLimitedOffer) || (tgc1 instanceof TADTimeLimitedOfferWithLatency)) {
							index = tgc1.indexOf(p1) - 1;
							ad1.addNextAtIndex(ad2, index);
						} else if (tgc1 instanceof TADChoice) {
							TADChoice tadch = (TADChoice)tgc1;
							index = tgc1.indexOf(p1) - 1;
							String myguard = TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(index));
							String tmp = Conversion.replaceAllChar(myguard, '[', "");
							tmp = Conversion.replaceAllChar(tmp, ']', "").trim();
							if (tmp.compareTo("else") == 0) {
								// Must calculate guard
								String realGuard = "";
								int cpt = 0;
								for(int k=0; k<tadch.getNbInternalTGComponent(); k++) {
									if (k != index) {
										if (cpt == 0) {
											tmp = TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(k));
											tmp = Conversion.replaceAllChar(tmp, '[', "");
											tmp = Conversion.replaceAllChar(tmp, ']', "").trim();
											if (tmp.length() > 0) {
												realGuard = tmp;
												cpt ++;
											}
										} else {
											tmp =  TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(k));
											tmp = Conversion.replaceAllChar(tmp, '[', "");
											tmp = Conversion.replaceAllChar(tmp, ']', "").trim();
											if (tmp.length() > 0) {
												realGuard = "(" + realGuard + ") and (" + tmp + ")";
												cpt ++;
											}
										}
									}
									//System.out.println("Real guard=" + realGuard + "k=" + k + " index=" + index);
								}
								
								if (realGuard.length() == 0) {
									myguard = "[ ]";
								} else {
									myguard = "[not(" + realGuard + ")]";
								}
								System.out.println("My guard=" + myguard);
							}
							((ADChoice)ad1).addGuard(myguard);
							ad1.addNext(ad2);
						} else if ((tgc1 instanceof TADSequence) ||(tgc1 instanceof TADPreemption)){
							index = tgc1.indexOf(p1) - 1;
							ad1.addNextAtIndex(ad2, index);
						} else if (tgc1 instanceof TOSADChoice) {
							TOSADChoice tadch = (TOSADChoice)tgc1;
							index = tgc1.indexOf(p1) - 1;
							((ADChoice)ad1).addGuard(TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(index)));
							ad1.addNext(ad2);
						} else {
							ad1.addNextAtIndex(ad2, index);
							//System.out.println("Adding connector from " + ad1 + " to " + ad2);
						}
					}
				}
			}
		}
		// Increasing count of this panel
		tdp.count ++;
		
		// Remove all elements not reachable from start state
		int sizeb = ad.size();
		
		System.out.println("Removing non reachable elements in t:" + t.getName());
		ad.removeAllNonReferencedElts();
		
		int sizea = ad.size();
		if (sizeb > sizea) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Non reachable elements have been removed in " + t.getName());
			ce.setTClass(t);
			ce.setTGComponent(null);
			ce.setTDiagramPanel(tdp);
			addWarning(ce);
			//System.out.println("Non reachable elements have been removed in " + t.getName());
		}
		
		//ad.replaceAllADActionStatewithMultipleParam(listE);
	}*/
	
	
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
	}
	
	
}
