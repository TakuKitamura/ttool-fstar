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
* Class EBRDDTranslator
* Creation: 18/09/2009
* @author Ludovic APVRILLE
* @see
*/

package ui;

import java.util.*;



import myutil.*;
import ui.ebrdd.*;
import req.ebrdd.*;


public class EBRDDTranslator {
	protected EBRDD ebrdd;
	protected Vector checkingErrors, warnings;
	protected CorrespondanceTGElement listE; // usual list
	//protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
	
	public EBRDDTranslator() {
		reinit();
	}
	
	public void reinit() {
		checkingErrors = new Vector();
		warnings = new Vector();
		listE = new CorrespondanceTGElement();
		//listB = new CorrespondanceTGElement();
	}
	
	public Vector getErrors() {
		return checkingErrors;
	}
	
	public Vector getWarnings() {
		return warnings;
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
	
	public CorrespondanceTGElement getCorrespondanceTGElement() {
		return listE;
	}
	
	public EBRDD generateEBRDD(EBRDDPanel ebrddp) {
		ebrdd = new EBRDD();
		
		// Search for start state
		LinkedList list = ebrddp.getComponentList();
		Iterator iterator = list.listIterator();
		TGComponent tgc, tgc1, tgc2, tgc3, tgc1tmp, tgc2tmp;
		TGComponent tss = null;
		int cptStart = 0;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof EBRDDStartState){
				tss = tgc;
				cptStart ++;
			}
		}
		
		if (tss == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in EBRDD");
			ce.setTDiagramPanel(ebrddp);
			addCheckingError(ce);
			return ebrdd;
		}
		
		if (cptStart > 1) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in EBRDD");
			ce.setTDiagramPanel(ebrddp);
			addCheckingError(ce);
			return ebrdd;
		}
		
		
		req.ebrdd.EBRDDActionState acst;
		req.ebrdd.EBRDDChoice ch;
		req.ebrdd.EBRDDERC erc;
		req.ebrdd.EBRDDLoop loop;
		req.ebrdd.EBRDDSequence seq;
		req.ebrdd.EBRDDStart start;
		req.ebrdd.EBRDDStop stop;
		ESO eso;
		ERB erb;
		int i;
		
		start = ebrdd.getStartState();
		listE.addCor(start, tss);
		
		// Creation of other elements
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			// Action
			if (tgc instanceof ui.ebrdd.EBRDDActionState) {
				acst = new req.ebrdd.EBRDDActionState();
				acst.setAction(((ui.ebrdd.EBRDDActionState)tgc).getAction());
				listE.addCor(acst, tgc);
				
				// Stop
			} else if (tgc instanceof ui.ebrdd.EBRDDStopState) {
				stop = new req.ebrdd.EBRDDStop();
				listE.addCor(stop, tgc);
				
				// Choice	
			} else if (tgc instanceof ui.ebrdd.EBRDDChoice) {
				// guards are added later on
				ch = new req.ebrdd.EBRDDChoice();
				listE.addCor(ch, tgc);
				
				// Sequence
			} else if (tgc instanceof ui.ebrdd.EBRDDSequence) {
				// guards are added later on
				seq = new req.ebrdd.EBRDDSequence();
				listE.addCor(seq, tgc);
				
				// Loop
			} else if (tgc instanceof ui.ebrdd.EBRDDForLoop) {
				// guards are added later on
				loop = new req.ebrdd.EBRDDLoop();
				listE.addCor(loop, tgc);
				loop.setInit(((ui.ebrdd.EBRDDForLoop)tgc).getInit());
				loop.setCondition(((ui.ebrdd.EBRDDForLoop)tgc).getCondition());
				loop.setIncrement(((ui.ebrdd.EBRDDForLoop)tgc).getIncrement());
				
				// ERC
			} else if (tgc instanceof ui.ebrdd.EBRDDERC) {
				// ERC's internal elements are built later on
				erc = new req.ebrdd.EBRDDERC();
				listE.addCor(erc, tgc);
			}
		}
		
		Vector v = listE.getData();
		Object o;
		for(i=0; i<v.size(); i++) {
			o = v.get(i);
			if (o instanceof EBRDDComponent) {
				ebrdd.add((EBRDDComponent)o);
			}
		}
		
		// Build ERCs
		ui.ebrdd.EBRDDESO esotgc;
		ui.ebrdd.EBRDDERB erbtgc;
		
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			if (tgc instanceof ui.ebrdd.EBRDDERC) {
				erc = (req.ebrdd.EBRDDERC)(listE.getEBRDDGeneralComponent(tgc));
				if (erc != null) {
					for(i=0; i<tgc.getChildCount(); i++) {
						tgc1 = tgc.getInternalTGComponent(i);
						
						if (tgc1 instanceof ui.ebrdd.EBRDDESO) {
							System.out.println("ESO found");
							esotgc = (ui.ebrdd.EBRDDESO)tgc1;
							eso = new req.ebrdd.ESO();
							listE.addCor(eso, esotgc);
							eso.setID(esotgc.getID());
							eso.setTimeout(esotgc.getTimeout());
							eso.setOncePerEvent(esotgc.getOncePerEvent());
							eso.setN(esotgc.getN());
							eso.setM(esotgc.getM());
							erc.addTreeElement(eso);
							
						} else if (tgc1 instanceof ui.ebrdd.EBRDDERB) {
							System.out.println("ERB found");
							erbtgc = (ui.ebrdd.EBRDDERB)tgc1;
							erb = new req.ebrdd.ERB();
							listE.addCor(erb, erbtgc);
							erb.setEvent(erbtgc.getEvent());
							erb.setAction(erbtgc.getAction());
							erb.setCondition(erbtgc.getCondition());
							erc.addTreeElement(erb);
						}
					}
				}
			}
		}
		
		
		// Interconnection between elements
        //TGConnectorEBRDD tgco;
		//TGConnectorEBRDDERC tgcoerc;
		TGConnector tgco;
        TGConnectingPoint p1, p2;
        EBRDDComponent eb1, eb2;
		EBRDDGeneralComponent ebg1, ebg2;
        int j, index;
        
        iterator = list.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnector) {
                //tgco = (TGConnectorEBRDD)tgc;
				tgco = (TGConnector)tgc;
                p1 = tgco.getTGConnectingPointP1();
                p2 = tgco.getTGConnectingPointP2();
                
                // Identification of connected components
                tgc1 = null; tgc2 = null;
                for(j=0; j<list.size(); j++) {
                    tgc3 = 	(TGComponent)(list.get(j));
                    tgc1tmp = tgc3.belongsToMeOrSon(p1);
					tgc2tmp = tgc3.belongsToMeOrSon(p2);
					if (tgc1tmp != null) {
						tgc1 = tgc1tmp;
					}
					if (tgc2tmp != null) {
						tgc2 = tgc2tmp;
					}
                     
                }
                
                // Connecting ebrdd modeling components
                if ((tgc1 != null) && (tgc2 != null)) {
                    //ADComponent ad1, ad2;
                    ebg1 = listE.getEBRDDGeneralComponent(tgc1);
                    ebg2 = listE.getEBRDDGeneralComponent(tgc2);
                    if ((ebg1 != null ) && (ebg2 != null)) {
						if ((ebg1 instanceof EBRDDComponent) && (ebg2 instanceof EBRDDComponent)) {
							System.out.println("Adding link");
							eb1 = (EBRDDComponent)ebg1;
							eb2 = (EBRDDComponent)ebg2;
							//Special case if "for loop" or if "choice"
							
							if (eb1 instanceof req.ebrdd.EBRDDLoop) {
								index = tgc1.indexOf(p1) - 1;
								if (index == 0) {
									eb1.addNext(0, eb2);
								} else {
									eb1.addNext(eb2);
								}
							} else if (eb1 instanceof req.ebrdd.EBRDDChoice) {
								index = tgc1.indexOf(p1) - 1;
								//System.out.println("Adding next:" + ae2);
								eb1.addNext(eb2);
								//System.out.println("Adding guard:" + ((TMLADChoice)tgc1).getGuard(index));
								((req.ebrdd.EBRDDChoice)eb1).addGuard(((ui.ebrdd.EBRDDChoice)tgc1).getGuard(index));
							} else if (eb1 instanceof req.ebrdd.EBRDDSequence) {
								index = tgc1.indexOf(p1) - 1;
								((req.ebrdd.EBRDDSequence)eb1).addIndex(index);
								eb1.addNext(eb2);
								//System.out.println("Adding " + ae2 + " at index " + index);
							} else {
								eb1.addNext(eb2);
							}
						} else if ((ebg1 instanceof ERCElement) && (ebg2 instanceof ERCElement)) {
							//System.out.println("ERCElements!");
							if ((ebg1 instanceof req.ebrdd.ESO) && (tgco instanceof TGConnectorEBRDDERC)) {
								index = tgc1.indexOf(p1) - 1;
								((req.ebrdd.ESO)ebg1).addIndex(index);
								((req.ebrdd.ESO)ebg1).addSon((ERCElement)ebg2);
								((ERCElement)ebg2).setNegated(((TGConnectorEBRDDERC)tgco).getNegation());
								System.out.println("ESO: Adding son: " + ebg2);
							}
						}
                    }
                }
            } 
        }
		
		// Sorting nexts elements of Sequence and ESOs
		for(j=0; j<ebrdd.size(); j++) {
			eb1 = ebrdd.get(j);
			if (eb1 instanceof req.ebrdd.EBRDDSequence) {
				((req.ebrdd.EBRDDSequence)eb1).sortNexts();
			} else  if (eb1 instanceof req.ebrdd.EBRDDERC) {
				((req.ebrdd.EBRDDERC)eb1).sortNexts();
			}
		}
        
        // Check that each "for" has two nexts
        // Check that Choice have compatible guards
		// Check validity of ERC (no cycles, etc.)
        iterator = list.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof ui.ebrdd.EBRDDChoice) {
                ch = (req.ebrdd.EBRDDChoice)(listE.getEBRDDGeneralComponent(tgc));
                ch.orderGuards();
				
                if (ch.hasMoreThanOneElse()) {
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have only one [else] guard");
                    ce.setTDiagramPanel(ebrddp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                } 
            } else if (tgc instanceof ui.ebrdd.EBRDDForLoop) {
				loop = (req.ebrdd.EBRDDLoop)(listE.getEBRDDGeneralComponent(tgc));
				if (loop.realNbOfNext() != 2) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Loop should have exactly two next elements");
                    ce.setTDiagramPanel(ebrddp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
				}
			} else if (tgc instanceof ui.ebrdd.EBRDDERC) {
				erc = (req.ebrdd.EBRDDERC)(listE.getEBRDDGeneralComponent(tgc));
				if (!erc.makeRoot()) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed ERC");
                    ce.setTDiagramPanel(ebrddp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
				}
			}
        }
		
		
		
		
		// Remove all elements not reachable from start state
		int sizeb = ebrdd.size();
		ebrdd.removeAllNonReferencedElts();
		int sizea = ebrdd.size();
		if (sizeb > sizea) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Non reachable elements have been removed in EBRDD");
			ce.setTGComponent(null);
			ce.setTDiagramPanel(ebrddp);
			addWarning(ce);
			//System.out.println("Non reachable elements have been removed in " + t.getName());
		}
		
		
		
		
		
		
		System.out.println("EBRDD generated");
		
		return ebrdd;
	}
	
}
