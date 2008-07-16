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
 * Class AnalysisPanelTranslator
 * Creation: 16/08/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

//import java.io.*;
//import java.awt.*;
//import javax.swing.*;
import java.util.*;



//import myutil.*;
import ui.iod.*;
import ui.sd.*;
//import ui.ucd.*;
//import ui.tree.*;
//import ui.window.*;

//import ddtranslator.*;
import sddescription.*;
//import sdtranslator.*;

public class AnalysisPanelTranslator {
    AnalysisPanel ap;
    MainGUI mgui;
    Vector checkingErrors;
    CorrespondanceTGElement listE;
    
    public AnalysisPanelTranslator(AnalysisPanel _ap, MainGUI _mgui) {
        ap = _ap;
        mgui = _mgui;
        reinit();
    }
    
    public void reinit() {
        checkingErrors = new Vector();
        listE = new CorrespondanceTGElement();
    }
    
    public Vector getErrors() {
        return checkingErrors;
    }
    
    public Vector getWarnings() {
        return null;
    }
    
    public CorrespondanceTGElement getCorrespondanceTGElement() {
        return listE;
    }
    
    public HMSC translateHMSC() throws AnalysisSyntaxException {
        int i, j;
        TDiagramPanel thmsc, thmsctmp;
        int nodeCounter = 0;
        HMSCNode node, node1, node2;
        LinkedList list;
        TGComponent tgc;
        MSC msc;
        int startCounter = 0;
        HMSCNode start = null;
        ListIterator iterator;
        String action;
        boolean test = false;
        
        // For each IOD, we create a node
        Vector iodsPanels = new Vector();
        Vector iodsNodes = new Vector() ;
        
        for(i=0; i<ap.panels.size(); i++) {
            thmsc = (TDiagramPanel)(ap.panels.elementAt(i));
            if (thmsc instanceof InteractionOverviewDiagramPanel) {
                System.out.println("Dealing with " + thmsc.getName());
                iodsPanels.add(thmsc);
                node = new HMSCNode("n"+nodeCounter + "choice_refiod", HMSCNode.CHOICE);
                iodsNodes.add(node);
            }
        }
        
        // For each element -> we create a node
        // Get the first
        
        
        for(i=0; i<ap.panels.size(); i++) {
            thmsc = (TDiagramPanel)(ap.panels.elementAt(i));
            listE = new CorrespondanceTGElement();
            if (thmsc instanceof InteractionOverviewDiagramPanel) {
                System.out.println("Managing " + thmsc.getName());
                list = thmsc.getComponentList();
                iterator = list.listIterator();
                while(iterator.hasNext()) {
                    tgc = (TGComponent)(iterator.next());
                    node = null;
                    if (tgc instanceof IODChoice) {
                        node = new HMSCNode("n"+nodeCounter + "choice", HMSCNode.CHOICE);
                    } else if (tgc instanceof IODJunction) {
                        node = new HMSCNode("n"+nodeCounter + "choice_junction", HMSCNode.CHOICE);
                    } else if (tgc instanceof IODParallel) {
                        node = new HMSCNode("n"+nodeCounter + "parallel", HMSCNode.PARALLEL);
                    } else if (tgc instanceof IODPreemption) {
                        node = new HMSCNode("n"+nodeCounter + "preempt", HMSCNode.PREEMPT);
                        //System.out.println(" --------------> Preempt");
                    } else if (tgc instanceof IODSequence) {
                        node = new HMSCNode("n"+nodeCounter + "sequence", HMSCNode.SEQUENCE);
                        //System.out.println(" --------------> Sequence");
                    } else if (tgc instanceof IODRefIOD) {
                        action = (((IODRefIOD)tgc).getAction());
                        thmsctmp = mgui.getIODiagramPanel(ap, action);
                        test = false;
                        if (thmsctmp != null) {
                            j = iodsPanels.indexOf(thmsctmp);
                            if (j != -1) {
                                node = new HMSCNode("n"+nodeCounter + "sequence", HMSCNode.SEQUENCE);
                                node1 = (HMSCNode)(iodsNodes.elementAt(j));
                                node.addNextNode(node1);
								//node2 = new HMSCNode("n"+nodeCounter + "choice", HMSCNode.CHOICE);
                            } else {
                                test = true;
                            }
                        } else {
                            test = true;
                        }
                        
                        if (test) {
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Reference to an unknown IOD named " + action);
                            addCheckingError(ce);
                            ce.setTDiagramPanel(thmsc);
                            ce.setTGComponent(tgc);
                            throw new AnalysisSyntaxException("Reference to an unknown IOD named " + action);
                        }
                    } else if (tgc instanceof IODRefSD) {
                        msc = new MSC(((IODRefSD)tgc).getAction());
                        listE.addCor(msc, tgc);
                    } else if (tgc instanceof IODStartState) {
                        if (i == 0) {
                            node = new HMSCNode("n"+nodeCounter + "start", HMSCNode.START);
                            startCounter ++;
                            if (startCounter > 1) {
                                CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Too many start nodes on Interaction Overview Diagram");
                                addCheckingError(ce);
                                ce.setTDiagramPanel(thmsc);
                                ce.setTGComponent(tgc);
                                throw new AnalysisSyntaxException("Too many start nodes on Interaction Overview Diagram");
                            }
                            start = node;
                        }
                        test = false;
                        j = iodsPanels.indexOf(thmsc);
                        if (j != -1) {
                            //System.out.println("Node = sequence instead of start");
                            node = (HMSCNode)(iodsNodes.elementAt(j));
                        } else {
                            test = true;
                        }
                        if (test) {
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Badly formatted IOD");
                            addCheckingError(ce);
                            ce.setTDiagramPanel(thmsc);
                            ce.setTGComponent(tgc);
                            throw new AnalysisSyntaxException("Badly formatted IOD");
                        }
                        
                        
                    } else if (tgc instanceof IODStopState) {
                        node = new HMSCNode("n"+nodeCounter+"stop", HMSCNode.STOP);
                    }
                    
                    if (node != null) {
                        listE.addCor(node, tgc);
                        nodeCounter ++;
                    }
                }
                
                // we look through each element of the listE list
                //
                TGConnectingPoint p;
                TGConnector tgco;
                TGComponent tgc2, tgc3;
                int ii, k;
                HMSCElement elt1, elt2;
                HMSCNode nodeTmp;
                
                for(ii=0; ii<listE.getSize(); ii++) {
                    tgc = listE.getTGAt(ii);
                    for(j=0; j<tgc.getNbConnectingPoint(); j++) {
                        p = tgc.tgconnectingPointAtIndex(j);
                        tgco = thmsc.findTGConnectorStartingAt(p);
                        if (tgco != null) {
                            // Identification of connected component
                            //System.out.println("tgc=" + tgc.getName() + ": " + j);
                            tgc2 = null;
                            for(k=0; k<list.size(); k++) {
                                tgc3 = (TGComponent)(list.get(k));
                                if (tgc3.belongsToMe(tgco.getTGConnectingPointP2())) {
                                    tgc2 = tgc3;
                                }
                            }
                            
                            if (tgc2 != null) {
                                //Object o1, o2;
                                elt1 = listE.getHMSCElement(tgc);
                                elt2 = listE.getHMSCElement(tgc2);
                                
                                if ((elt1 != null) && (elt2 != null)) {
                                    //System.out.println("Adding a link");
                                    if ((elt1 instanceof MSC) && (elt2 instanceof MSC)) {
                                        // must include a choice node between
                                        node = new HMSCNode("n"+nodeCounter, HMSCNode.CHOICE);
                                        nodeCounter ++;
                                        ((MSC)elt1).setNextNode(node);
                                        node.addNextMSC((MSC)elt2);
                                        node.addMSCGuard("[]");
                                    }
                                    
                                    if ((elt1 instanceof MSC) && (elt2 instanceof HMSCNode)) {
                                        ((MSC)elt1).setNextNode((HMSCNode)elt2);
                                    }
                                    
                                    if ((elt1 instanceof HMSCNode) && (elt2 instanceof MSC)) {
                                        //((HMSCNode)elt1).addNextMSC((MSC)elt2);
                                        nodeTmp = new HMSCNode("n"+nodeCounter, HMSCNode.CHOICE);
                                        nodeCounter++;
                                        ((HMSCNode)elt1).addNextNode(nodeTmp);
                                        nodeTmp.addNextMSC((MSC)elt2);
                                        //System.out.println("Adding link to MSC " + ((MSC)elt2).getName());
                                    }
                                    
                                    if ((elt1 instanceof HMSCNode) && (elt2 instanceof HMSCNode)) {
                                        ((HMSCNode)elt1).addNextNode((HMSCNode)elt2);
                                        //System.out.println("Adding link to Node " + ((HMSCNode)elt2).getName());
                                    }
                                    
                                    if (tgc instanceof IODChoice) {
                                        //System.out.println("*** IODCHOICE ***");
                                        
                                        if (((IODChoice)tgc).hasUnvalidGuards()) {
                                            String s = ((IODChoice)tgc).getUnvalidGuards();
                                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Choice has a badly formed guard: " + s + " -> should be of the form \"[<instance name> / <guard>]\"");
                                            addCheckingError(ce);
                                            ce.setTDiagramPanel(thmsc);
                                            ce.setTGComponent(tgc);
                                            throw new AnalysisSyntaxException("Choice has a badly formed guard: " + s + " -> should be of the form \"[<instance name> / <guard>]\"");
                                        }
                                        
                                        node = listE.getNodeAt(ii);
                                /*if (elt2 instanceof MSC){
                                    System.out.println("Adding a MSC guard #" + (j-1) + " g=" + ((IODChoice)tgc).getGuard(j-1) + " on node " + node.getName());
                                    node.addMSCGuard(((IODChoice)tgc).getGuard(j-1));
                                } else {*/
                                        //System.out.println("Adding a Node guard #" + (j-1) + " g=" + ((IODChoice)tgc).getGuard(j-1) + " on node " + node.getName());
                                        node.addNodeGuard(((IODChoice)tgc).getGuard(j-1));
                                /*}*/
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
        }
        
        if (start != null) {
            // Make a link between the start and the first choice
            start.addNextNode((HMSCNode)(iodsNodes.elementAt(0)));
            
            // Create HMSC
            HMSC hmsc = new HMSC("myHMSC", start);
            return hmsc;
        }
        
        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "No start node on Interaction Overview Diagram");
        addCheckingError(ce);
        ce.setTDiagramPanel((TDiagramPanel)(ap.panels.elementAt(0)));
        throw new AnalysisSyntaxException("No start node on Interaction Overview Diagram");
    }
    
    
    
    public void translateMSCs(HMSC hmsc) throws AnalysisSyntaxException {
        // For each msc -> we look for the corresponding panel
        // then, the msc is built accordingly if not yet built
        MSC msc;
        SequenceDiagramPanel sdp;
        
        LinkedList ll = hmsc.getMSCs();
        Iterator iterator = ll.listIterator();
        
        while(iterator.hasNext()) {
            msc = (MSC)(iterator.next());
            sdp = mgui.getSequenceDiagramPanel(ap, msc.getName());
            if (sdp == null) {
                throw new AnalysisSyntaxException("No Sequence Diagram named " + msc.getName());
            }
            translateMSCFromPanel(hmsc, msc, sdp);
        }
    }
    
    private void translateMSCFromPanel(HMSC hmsc, MSC msc, SequenceDiagramPanel sdp) throws AnalysisSyntaxException {
        CorrespondanceTGElement correspondance = new CorrespondanceTGElement(); // evt <-> tgcomponent
        
        //System.out.println("Necessary instances for msc " + msc.getName());
        createAllNecessaryInstances(hmsc, sdp);
        
        //System.out.println("Evts of " + msc.getName());
        createEvt(hmsc, msc, sdp, correspondance);
        
        //System.out.println("Time constraints of " + msc.getName());
        createTimeConstraints(hmsc, msc, sdp, correspondance);
        
        //System.out.println("Order events for " + msc.getName());
        createEvtsOrder(hmsc, msc, sdp, correspondance);
        
    }
    
    private void createAllNecessaryInstances(HMSC hmsc, SequenceDiagramPanel sdp) throws AnalysisSyntaxException {
        LinkedList list = sdp.getComponentList();
        Iterator iterator = list.listIterator();
        TGComponent tgc;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            
            if (tgc instanceof SDInstance) {
                hmsc.getCreateInstanceIfNecessary(((SDInstance)tgc).getInstanceName());
            }
        }
    }
    
    private void createEvt(HMSC hmsc, MSC msc, SequenceDiagramPanel sdp, CorrespondanceTGElement correspondance) throws AnalysisSyntaxException {
        // regular evt i.e. not message sending / receiving
        LinkedList list = sdp.getComponentList();
        Iterator iterator = list.listIterator();
        TGComponent tgc, tgc1, tgc2, tgc3;
        TGComponent internal;
        TGConnectingPoint p1, p2;
        TGConnector tgco;
        
        SDInstance sdi, sdi1, sdi2;
        int j;
        Instance ins, ins1, ins2;
        Evt evt, evt1, evt2;
        LinkEvts le;
        
        String s;
        
        
        
        // internal component of instances
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            
            if (tgc instanceof SDInstance) {
                sdi = (SDInstance)tgc;
                ins = hmsc.getInstance(sdi.getInstanceName());
                if (ins == null) {
                    throw new AnalysisSyntaxException("No instance named " + sdi.getInstanceName() + "in sequence diagram " + sdp.getName());
                }
                
                for(j=0; j<tgc.getNbInternalTGComponent(); j++) {
                    internal = tgc.getInternalTGComponent(j);
                    
                    if (internal instanceof SDTimerExpiration) {
                        evt = new Evt(Evt.TIMER_EXP, ((SDTimerExpiration)(internal)).getTimer(), ins);
                        correspondance.addCor(evt, internal);
                        msc.addEvt(evt);
                        evt.y = ((SDTimerExpiration)internal).getYOrder();
                    } else if (internal instanceof SDTimerCancellation) {
                        evt = new Evt(Evt.TIMER_RESET, ((SDTimerCancellation)(internal)).getTimer(), ins);
                        correspondance.addCor(evt, internal);
                        msc.addEvt(evt);
                        evt.y = ((SDTimerCancellation)internal).getYOrder();
                    } else if (internal instanceof SDTimerSetting) {
                        evt = new Evt(Evt.TIMER_SET, ((SDTimerSetting)(internal)).getTimer() + ";" + ((SDTimerSetting)(internal)).getDuration(), ins);
                        correspondance.addCor(evt, internal);
                        msc.addEvt(evt);
                        evt.y = ((SDTimerSetting)internal).getYOrder();
                    } else if (internal instanceof SDActionState) {
                        s = ((SDActionState)internal).getAction();
                        if (s.indexOf("=") > -1) {
                            // variable
                            evt = new Evt(Evt.VARIABLE_SET, s, ins);
                            correspondance.addCor(evt, internal);
                            msc.addEvt(evt);
                            evt.y = internal.getY();
                        } else {
                            //internal action
                            evt = new Evt(Evt.INTERNAL_ACTION, s, ins);
                            correspondance.addCor(evt, internal);
                            msc.addEvt(evt);
                            evt.y = internal.getY();
                        }
                    } else if (internal instanceof SDGuard) {
                        s = ((SDGuard)internal).getGuard().trim();
                        System.out.println("New guard evt: " + s);
						if (s.compareTo("else") == 0) {
							evt = new Evt(Evt.ELSE_GUARD, s, ins);
						} else if (s.compareTo("end") == 0) {
							evt = new Evt(Evt.END_GUARD, s, ins);
						} else {
							evt = new Evt(Evt.GUARD, s, ins);
						}
                        correspondance.addCor(evt, internal);
                        msc.addEvt(evt);
                        evt.y = internal.getY();
                    }else if (internal instanceof SDTimeInterval) {
                        s = ((SDTimeInterval)internal).getMinDelay() + "," + ((SDTimeInterval)internal).getMaxDelay();
                        // Time interval
                        evt = new Evt(Evt.TIME_INTERVAL, s, ins);
                        correspondance.addCor(evt, internal);
                        msc.addEvt(evt);
                        evt.y = internal.getY();
                    }
                    
                }
                // Messages
            } else if (tgc instanceof TGConnectorMessageSD) {
                // Identification of connected components
                tgco = (TGConnectorMessageSD)tgc;
                p1 = tgco.getTGConnectingPointP1();
                p2 = tgco.getTGConnectingPointP2();
                
                tgc1 = null; tgc2 = null;
                for(j=0; j<list.size(); j++) {
                    tgc3 = (TGComponent)(list.get(j));
                    if (tgc3.belongsToMe(p1)) {
                        tgc1 = tgc3;
                    }
                    if (tgc3.belongsToMe(p2)) {
                        tgc2 = tgc3;
                    }
                }
                
                if ((tgc1 instanceof SDInstance) && (tgc2 instanceof SDInstance)) {
                    sdi1 = (SDInstance)tgc1;
                    ins1 = hmsc.getInstance(sdi1.getInstanceName());
                    if (ins1 == null) {
                        throw new AnalysisSyntaxException("No instance named " + sdi1.getInstanceName() + " in sequence diagram " + sdp.getName());
                    }
                    
                    sdi2 = (SDInstance)tgc2;
                    ins2 = hmsc.getInstance(sdi2.getInstanceName());
                    if (ins2 == null) {
                        throw new AnalysisSyntaxException("No instance named " + sdi2.getInstanceName() + " in sequence diagram " + sdp.getName());
                    }
                    
                    if (tgc instanceof TGConnectorMessageAsyncSD) {
						
                        evt1 = new Evt(Evt.SEND_MSG, ((TGConnectorMessageAsyncSD)(tgc)).getFirstPartMessage() + "__" + ins1.getName() + "_to_" + ins2.getName() + ((TGConnectorMessageAsyncSD)(tgc)).getSecondPartMessage(), ins1);
                        correspondance.addCor(evt1, tgc);
                        msc.addEvt(evt1);
                        evt1.y = p1.getY();
                        
                        evt2 = new Evt(Evt.RECV_MSG, ((TGConnectorMessageAsyncSD)(tgc)).getFirstPartMessage() + "__" + ins1.getName() + "_to_" + ins2.getName() + ((TGConnectorMessageAsyncSD)(tgc)).getSecondPartMessage(), ins2);
                        correspondance.addCor(evt2, tgc);
                        msc.addEvt(evt2);
                        evt2.y = p2.getY();
                        
                        le = new LinkEvts(evt1, evt2);
                        msc.addLinkEvts(le);
                        
                    } else if (tgc instanceof TGConnectorMessageSyncSD) {
                        //System.out.println("**************** Synchronous message ************************");
						if (!((TGConnectorMessageSyncSD)(tgc)).isMessageWellFormed()) {
							throw new AnalysisSyntaxException("Badly formatted synchronous exchange:" + ((TGConnectorMessageSyncSD)(tgc)).getMessage() +" in instance "+ sdi1.getInstanceName() + " in sequence diagram " + sdp.getName());
						}
                        evt1 = new Evt(Evt.SEND_SYNC, ((TGConnectorMessageSyncSD)(tgc)).getFirstPartMessage() + "__" + ins1.getName() + "_to_" + ins2.getName() + ((TGConnectorMessageSyncSD)(tgc)).getSecondPartMessage(), ins1);
                        correspondance.addCor(evt1, tgc);
                        msc.addEvt(evt1);
                        evt1.y = p1.getY();
                        
                        evt2 = new Evt(Evt.RECV_SYNC, ((TGConnectorMessageSyncSD)(tgc)).getFirstPartMessage() + "__" + ins1.getName() + "_to_" + ins2.getName()+((TGConnectorMessageSyncSD)(tgc)).getSecondPartMessage(), ins2);
                        correspondance.addCor(evt2, tgc);
                        msc.addEvt(evt2);
                        evt2.y = p2.getY();
                        
                        le = new LinkEvts(evt1, evt2);
                        msc.addLinkEvts(le);
                        
                    }
                }
            }
        }
    }
    
    private void createTimeConstraints(HMSC hmsc, MSC msc, SequenceDiagramPanel sdp, CorrespondanceTGElement correspondance) throws AnalysisSyntaxException {
        // regular evt i.e. not message sending / receiving
        Iterator iterator = sdp.getComponentList().listIterator();
        TGComponent tgc, tgc1, tgc4;
        TGComponent internal;
        //TGConnectingPoint p1, p2;
        //TGConnector tgco;
        //GConnectorRelativeTimeSD tgcortc;
        
        SDInstance sdi1, sdi2;
        int j;
        Instance ins;
        Evt evt1;
        TimeConstraint tc;
        int min_tc, max_tc;
        Evt [] evts;
        
        
        
        // internal component of instances
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            //System.out.println("i=" + i);
            
            if (tgc instanceof SDInstance) {
                sdi1 = (SDInstance)tgc;
                ins = hmsc.getInstance(sdi1.getInstanceName());
                if (ins == null) {
                    throw new AnalysisSyntaxException("No instance named " + sdi1.getInstanceName() + " in sequence diagram " + sdp.getName());
                }
                
                for(j=0; j<tgc.getNbInternalTGComponent(); j++) {
                    internal = tgc.getInternalTGComponent(j);
                    //System.out.println("j=" + j);
                    
                    if (internal instanceof SDAbsoluteTimeConstraint) {
                        tgc1 = sdi1.getTGComponentActionCloserTo(internal);
                        if (tgc1 == null) {
                            throw new AnalysisSyntaxException("Absolute timing constraint " + internal.getValue() + " has no associated action in sequence diagram" + sdp.getName());
                        }
                        
                        evt1 = getEvtAbsolute((SDAbsoluteTimeConstraint)internal, tgc1, sdp, sdi1, correspondance);
                        
                        if (evt1 == null) {
                            throw new AnalysisSyntaxException("Translation algorithms error #1");
                        }
                        
                        try {
                            min_tc = Integer.decode(((SDAbsoluteTimeConstraint)internal).getMinConstraint()).intValue();
                            
                        } catch (Exception e) {
                            throw new AnalysisSyntaxException("Absolute timing constraint " + internal.getValue() + " has an invalid minimal value" + sdp.getName());
                        }
                        
                        try {
                            max_tc = Integer.decode(((SDAbsoluteTimeConstraint)internal).getMaxConstraint()).intValue();
                            
                        } catch (Exception e) {
                            throw new AnalysisSyntaxException("Absolute timing constraint " + internal.getValue() + " has an invalid maximal value" + sdp.getName());
                        }
                        
                        tc = new TimeConstraint(evt1, min_tc, max_tc);
                        msc.addTimeConstraint(tc);
                        
                    } else if (internal instanceof SDRelativeTimeConstraint) {
                        //System.out.println("Relative time constraint");
                        TGConnectorRelativeTimeSD tgcort = sdp.firstAndConnectedSDRelativeTimeConstraint(internal);
                        //System.out.println("tgcort=" + tgcort);
                        
                        if (tgcort != null){
                            TGComponent internal2 = sdp.getSecondTGComponent(tgcort);
                            //System.out.println("internal2=" + internal2);
                            
                            if (internal2 instanceof SDRelativeTimeConstraint) {
                                tgc4 = internal2.getTopFather();
                                if (!(tgc4 instanceof SDInstance)) {
                                    throw new AnalysisSyntaxException("Relative timing constraint " + tgcort.getValue() + ": invalid graphical component" );
                                }
                                sdi2 = (SDInstance)tgc4;
                                evts = getEvts((SDRelativeTimeConstraint)internal, (SDRelativeTimeConstraint)internal2, sdp, sdi1, sdi2, correspondance);
                                
                                //System.out.println("evt1=" + evts[0] + "evt2=" + evts[1]);
                                
                                if (evts != null) {
                                    try {
                                        min_tc = Integer.decode(((TGConnectorRelativeTimeSD)tgcort).getMinConstraint()).intValue();
                                        
                                    } catch (Exception e) {
                                        throw new AnalysisSyntaxException("Relative timing constraint " + tgcort.getValue() + " has an invalid minimal value on" + sdp.getName());
                                    }
                                    
                                    try {
                                        max_tc = Integer.decode(((TGConnectorRelativeTimeSD)tgcort).getMaxConstraint()).intValue();
                                        
                                    } catch (Exception e) {
                                        throw new AnalysisSyntaxException("Relative timing constraint " + tgcort.getValue() + " has an invalid maximal value on" + sdp.getName());
                                    }
                                    
                                    tc = new TimeConstraint(evts[0], evts[1], min_tc, max_tc);
                                    msc.addTimeConstraint(tc);
                                    //System.out.println("new relative tc = " + tc);
                                    
                                } else {
                                    throw new AnalysisSyntaxException("Relative timing constraint " + tgcort.getValue() + ": null events" );
                                }
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Assumes that internal is effectively connected
    public Evt[] getEvts(SDRelativeTimeConstraint tc1, SDRelativeTimeConstraint tc2, SequenceDiagramPanel sdp, SDInstance sdi1, SDInstance sdi2, CorrespondanceTGElement correspondance) {
        //System.out.println("get1");
        Evt evt1 = getEvt(tc1, sdp, sdi1, correspondance);
        //System.out.println("get2");
        Evt evt2 = getEvt(tc2, sdp, sdi2, correspondance);
        //System.out.println("get3");
        if ((evt1 != null) && (evt2 != null)) {
            Evt [] evts = new Evt[2];
            evts[0] = evt1;
            evts[1] = evt2;
            return evts;
        }
        return null;
    }
    
    public Evt getEvt(SDRelativeTimeConstraint tc1, SequenceDiagramPanel sdp, SDInstance sdi, CorrespondanceTGElement correspondance) {
        TGConnector tgco;
        TGConnectingPoint p;
        //Evt evt;
        TGComponent tgc;
        
        //System.out.println("GetEvt 1");
        // is-it connected to a message action ?
        tgco = sdp.messageActionCloserTo(tc1, sdi);
        if (tgco instanceof TGConnectorMessageSD) {
            p = sdp.TGConnectingPointActionCloserTo(tc1, tgco, sdi);
            //System.out.println("GetEvt 2");
            if (p == null) {
                return null;
            }
            
            //System.out.println("GetEvt 3");
            if (p==tgco.getTGConnectingPointP1()) {
                // sending message event
                //System.out.println("GetEvt 4");
                return correspondance.getSendingMsgEvt(tgco);
            } else {
                //System.out.println("GetEvt 5");
                return correspondance.getReceivingMsgEvt(tgco);
            }
            
        }
        
        // other action? -> not yet implemented
        //System.out.println("Others relative");
        tgc = sdp.getActionCloserTo(tc1.getY(), sdi);
        
        if (tgc instanceof SDActionState) {
            return correspondance.getEvt(tgc);
        }
        
        return null;
    }
    
    public Evt getEvtAbsolute(SDAbsoluteTimeConstraint sdatc, TGComponent tgc, SequenceDiagramPanel sdp, SDInstance sdi, CorrespondanceTGElement correspondance) {
        TGConnector tgco;
        TGConnectingPoint p;
        //Evt evt;
        
        //System.out.println("GetEvt 1");
        // is-it connected to a message action ?
        
        if (tgc instanceof TGConnectorMessageSD) {
            tgco = (TGConnector)tgc;
            p = sdp.TGConnectingPointActionCloserTo(sdatc, tgco, sdi);
            //System.out.println("GetEvt 2");
            if (p == null) {
                return null;
            }
            
            //System.out.println("GetEvt 3");
            if (p==tgco.getTGConnectingPointP1()) {
                // sending message event
                //System.out.println("GetEvt 4");
                return correspondance.getSendingMsgEvt(tgco);
            } else {
                //System.out.println("GetEvt 5");
                return correspondance.getReceivingMsgEvt(tgco);
            }
            
        }
        
        // other action? -> not yet implemented
        // get closer actions -> internal actions or variable increment
        //System.out.println("Others absolute");
        tgc = sdp.getActionCloserTo(sdatc.getY(), sdi);
        
        if (tgc instanceof SDActionState) {
            return correspondance.getEvt(tgc);
        }
        
        return null;
    }
    
    private void createEvtsOrder(HMSC hmsc, MSC msc, SequenceDiagramPanel sdp, CorrespondanceTGElement correspondance) throws AnalysisSyntaxException {
        LinkedList ll = hmsc.getInstances();
        Iterator iterator = ll.listIterator();
        Instance ins;
        
        while(iterator.hasNext()) {
            ins = (Instance)(iterator.next());
            createEvtsOrderForInstance(msc, ins, sdp, correspondance);
        }
    }
    
    private void  createEvtsOrderForInstance(MSC msc, Instance ins, SequenceDiagramPanel sdp, CorrespondanceTGElement correspondance) throws AnalysisSyntaxException {
        
        Evt evt, evt1, nextEvt, lastEvt;
        LinkedList ll = msc.getEvts();
        Iterator iterator = ll.listIterator();
        LinkedList orderedy = new LinkedList();
        LinkedList grouped = new LinkedList();
        
        SDInstance sdi = sdp.getSDInstance(ins.getName());
        
        if (sdi == null) {
            return;
        }
        
        
        //Iterator it1;
        int y;
        
        if (ll.size() == 0) {
            return;
        }
        
        boolean go = true;
        // we build a list of events ordered according to their y
        while(go == true) {
            nextEvt = null;
            iterator = ll.listIterator();
            y = Integer.MAX_VALUE;
            while(iterator.hasNext()) {
                evt = (Evt)(iterator.next());
                if ((evt.y <= y) && (!orderedy.contains(evt)) && (evt.getInstance() == ins)) {
                    nextEvt = evt;
                    y = evt.y;
                }
            }
            
            if (nextEvt != null) {
                orderedy.add(nextEvt);
            } else {
                go = false;
            }
        }
        
        if (orderedy.size() ==0) {
            return;
        }
        iterator = orderedy.listIterator();
        
        //System.out.println("Order on " + msc.getName() + " and on instance " + sdi.getValue());
        while(iterator.hasNext()) {
            evt = (Evt)(iterator.next());
            //System.out.println("Evt=" + evt.getActionId());
        }
        //events in the same coregion are regrouped:
        
        iterator = orderedy.listIterator();
        // first evt -> special algorithm
        if (iterator.hasNext()) {
            evt = (Evt)(iterator.next());
            ll = new LinkedList();
            ll.add(evt);
            grouped.add(ll);
            lastEvt = evt;
        } else {
            return;
        }
        
        // other evts
        while(iterator.hasNext()) {
            evt = (Evt)(iterator.next());
            // in same coregion
            if ((lastEvt.y == evt.y) || (sdi.isInSameCoregion(lastEvt.y, evt.y))) {
                //System.out.println("evt evt=" + evt.getActionId() + " and lastEvt=" + lastEvt.getActionId() + " are in the same coregion / y");
                //System.out.println("evt evty=" + evt.y + " and lastEvt=" + lastEvt.y);
                ll.add(evt);
                lastEvt = evt;
            } else {
                ll = new LinkedList();
                ll.add(evt);
                grouped.add(ll);
                lastEvt = evt;
            }
        }
        
        if (grouped.size() < 1) {
            return;
        }
        
        // Orders are created!
        Iterator iterator1, iterator2, iterator3;
        iterator1 = grouped.listIterator();
        LinkedList previous, current;
        Order order;
        
        // first groups has no previous evts
        previous = (LinkedList)(iterator1.next());
        
        while(iterator1.hasNext()) {
            current = (LinkedList)(iterator1.next());
            iterator2 = current.listIterator();
            while(iterator2.hasNext()) {
                evt = (Evt)(iterator2.next());
                iterator3 = previous.listIterator();
                while(iterator3.hasNext()) {
                    evt1 = (Evt)(iterator3.next());
                    order = new Order(evt1, evt);
                    //System.out.println("New order between " + evt1.getActionId() + " and " + evt.getActionId());
                    msc.addOrder(order);
                }
            }
            previous = current;
        }
    }
    
    private void addCheckingError(CheckingError ce) {
        if (checkingErrors == null) {
            checkingErrors = new Vector();
        }
        checkingErrors.addElement(ce);
    }
    
}
