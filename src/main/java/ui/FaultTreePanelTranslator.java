/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */


package ui;

import avatartranslator.*;
import faulttrees.*;
import translator.CheckingError;
import ui.ftd.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Class FaultTreePanelTranslator
 * Creation: 24/01/2018
 *
 * @author Ludovic APVRILLE
 */
public class FaultTreePanelTranslator {

    protected FaultTree at;
    protected FaultTreePanel atp;
    protected LinkedList<CheckingError> checkingErrors, warnings;
    protected CorrespondanceTGElement listE; // usual list
    protected LinkedList<TDiagramPanel> panels;


    public FaultTreePanelTranslator(FaultTreePanel _atp) {
        atp = _atp;
        reinit();
    }

    public void reinit() {
        checkingErrors = new LinkedList<CheckingError>();
        warnings = new LinkedList<CheckingError>();
        listE = new CorrespondanceTGElement();
        panels = new LinkedList<TDiagramPanel>();
    }

    public LinkedList<CheckingError> getCheckingErrors() {
        return checkingErrors;
    }

    public LinkedList<CheckingError> getWarnings() {
        return warnings;
    }

    public CorrespondanceTGElement getCorrespondanceTGElement() {
        return listE;
    }

    public FaultTree translateToFaultTreeDataStructure() {

        at = new FaultTree("FaultTree", atp);


        for (TDiagramPanel panel : atp.panels) {
            if (panel instanceof FaultTreeDiagramPanel) {
                translate((FaultTreeDiagramPanel) panel);
                boolean b = at.checkSyntax();
                if (!b) {
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, at.errorOfFaultyElement);
                    ce.setTGComponent((TGComponent) (at.faultyElement.getReferenceObject()));
                    ce.setTDiagramPanel(panel);
                    addCheckingError(ce);
                }
            }
        }


        //TraceManager.addDev("AT=" + at.toString());
        return at;

    }

    public void translate(FaultTreeDiagramPanel atdp) {
        List<TGComponent> allComponents = atdp.getAllComponentList();

        int nodeID = 0;
        TGComponent father;

        //Create Faults, nodes
        for (TGComponent comp : allComponents) {
            if (comp instanceof FTDFault) {
                FTDFault atdatt = (FTDFault) comp;
                Fault att;
                String value = atdatt.getValue();
                father = atdatt.getFather();
                if ((father != null) && (father instanceof FTDBlock)) {
                    value = ((FTDBlock) father).getNodeName() + "__" + value;

                }
                att = new Fault(value, atdatt);
                att.setRoot(atdatt.isRootFault());
                att.setProbability(atdatt.getProbability());
                att.setEnabled(atdatt.isEnabled());
                at.addFault(att);
                listE.addCor(att, comp);
            }
            if (comp instanceof FTDConstraint) {
                FTDConstraint cons = (FTDConstraint) comp;
                nodeID++;

                //OR
                if (cons.isOR()) {
                    ORNode ornode = new ORNode("OR__" + nodeID, cons);
                    at.addNode(ornode);
                    listE.addCor(ornode, comp);

                    //AND
                } else if (cons.isAND()) {
                    ANDNode andnode = new ANDNode("AND__" + nodeID, cons);
                    at.addNode(andnode);
                    listE.addCor(andnode, comp);

                    //XOR
                } else if (cons.isXOR()) {
                    XORNode xornode = new XORNode("XOR__" + nodeID, cons);
                    at.addNode(xornode);
                    listE.addCor(xornode, comp);

                } else if (cons.isNOT()) {
                    NOTNode notnode = new NOTNode("NOT__" + nodeID, cons);
                    at.addNode(notnode);
                    listE.addCor(notnode, comp);

                    //SEQUENCE
                } else if (cons.isSequence()) {
                    SequenceNode seqnode = new SequenceNode("SEQUENCE__" + nodeID, cons);
                    at.addNode(seqnode);
                    listE.addCor(seqnode, comp);

                    //BEFORE
                } else if (cons.isBefore()) {
                    String eq = cons.getEquation();
                    int time;
                    try {
                        time = Integer.decode(eq).intValue();
                        BeforeNode befnode = new BeforeNode("BEFORE__" + nodeID, cons, time);
                        at.addNode(befnode);
                        listE.addCor(befnode, comp);
                    } catch (Exception e) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid time in before node");
                        ce.setTGComponent(comp);
                        ce.setTDiagramPanel(atdp);
                        addCheckingError(ce);
                    }

                    //AFTER
                } else if (cons.isAfter()) {
                    String eq = cons.getEquation();
                    int time;
                    try {
                        time = Integer.decode(eq).intValue();
                        AfterNode aftnode = new AfterNode("AFTER__" + nodeID, cons, time);
                        at.addNode(aftnode);
                        listE.addCor(aftnode, comp);
                    } catch (Exception e) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid time in after node");
                        ce.setTGComponent(comp);
                        ce.setTDiagramPanel(atdp);
                        addCheckingError(ce);
                    }

                } else if (cons.isVote()) {
                    VoteNode voteNode = new VoteNode("VOTE__" + nodeID, cons);
                    at.addNode(voteNode);
                    listE.addCor(voteNode, comp);

                } else {
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid Fault node");
                    ce.setTGComponent(comp);
                    ce.setTDiagramPanel(atdp);
                    addCheckingError(ce);
                }
            }
        }

        // Making connections between nodes&Faults
        TGComponent tgc1, tgc2;
        for (TGComponent comp : allComponents) {
            if (comp instanceof FTDFaultConnector) {
                FTDFaultConnector con = (FTDFaultConnector) (comp);
                tgc1 = atdp.getComponentToWhichBelongs(con.getTGConnectingPointP1());
                tgc2 = atdp.getComponentToWhichBelongs(con.getTGConnectingPointP2());
                if (((tgc1 instanceof FTDFault) || (tgc1 instanceof FTDConstraint)) &&
                        ((tgc2 instanceof FTDFault) || (tgc2 instanceof FTDConstraint))) {
                    try {
                        // We must transpose this into Fault -> node or node -> Fault

                        // Fault -> Fault
                        if ((tgc1 instanceof FTDFault) && (tgc2 instanceof FTDFault)) {
                            // We link the two Faults with an "and" node
                            Fault at1 = (Fault) (listE.getObject(tgc1));
                            Fault at2 = (Fault) (listE.getObject(tgc2));
                            nodeID++;
                            ANDNode andnode = new ANDNode("ANDBetweenFaults__" + nodeID + "__" + at1.getName() + "__" + at2.getName(), tgc1);
                            at.addNode(andnode);
                            listE.addCor(andnode, comp);
                            at1.addDestinationNode(andnode);
                            at2.setOriginNode(andnode);
                            andnode.addInputFault(at1, new Integer("0"));
                            andnode.setResultingFault(at2);


                            // Fault -> node
                        } else if ((tgc1 instanceof FTDFault) && (tgc2 instanceof FTDConstraint)) {
                            Fault at1 = (Fault) (listE.getObject(tgc1));
                            FaultNode node1 = (FaultNode) (listE.getObject(tgc2));
                            at1.addDestinationNode(node1);
                            String val = comp.getValue().trim();
                            if (val.length() == 0) {
                                val = "0";
                            }
                            node1.addInputFault(at1, new Integer(val));

                            // Node -> Fault
                        } else if ((tgc1 instanceof FTDConstraint) && (tgc2 instanceof FTDFault)) {
                            Fault at1 = (Fault) (listE.getObject(tgc2));
                            FaultNode node1 = (FaultNode) (listE.getObject(tgc1));
                            at1.setOriginNode(node1);
                            if (node1.getResultingFault() != null) {
                                // Already a resulting Fault -> error
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Too many resulting Faults");
                                ce.setTGComponent(tgc1);
                                ce.setTDiagramPanel(atdp);
                                addCheckingError(ce);
                            } else {
                                node1.setResultingFault(at1);
                            }

                            // Node -> Node
                        } else if ((tgc1 instanceof FTDConstraint) && (tgc2 instanceof FTDConstraint)) {
                            FaultNode node1 = (FaultNode) (listE.getObject(tgc1));
                            FaultNode node2 = (FaultNode) (listE.getObject(tgc2));
                            // Make fake Fault
                            Fault att = new Fault("Fault__from_" + node1.getName() + "_to_" + node2.getName(), tgc1);
                            att.setRoot(false);
                            at.addFault(att);
                            listE.addCor(att, comp);

                            att.setOriginNode(node1);
                            att.addDestinationNode(node2);

                            if (node1.getResultingFault() != null) {
                                // Already a resulting Fault -> error
                                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Too many resulting Faults");
                                ce.setTGComponent(tgc1);
                                ce.setTDiagramPanel(atdp);
                                addCheckingError(ce);
                            } else {
                                node1.setResultingFault(att);
                            }

                            String val = comp.getValue().trim();
                            if (val.length() == 0) {
                                val = "0";
                            }
                            node2.addInputFault(att, new Integer(val));
                        }

                    } catch (Exception e) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Badly formed connector");
                        ce.setTGComponent(comp);
                        ce.setTDiagramPanel(atdp);
                        addCheckingError(ce);
                    }
                }
            }
        }

    }


    public AvatarSpecification generateAvatarSpec() {
        AvatarSpecification as = new AvatarSpecification("spec from Fault trees", atp);
        // One block per Faultnode to receive the Fault
        // One block per Fault -> syncho
        // One mast block with all channels declared at that level
        AvatarBlock mainBlock = new AvatarBlock("MainBlock", as, null);
        AvatarStartState ass = new AvatarStartState("StartStateOfMainBlock", null);
        mainBlock.getStateMachine().setStartState(ass);
        mainBlock.getStateMachine().addElement(ass);
        as.addBlock(mainBlock);

        // Declare all Faults
        declareAllFaults(as, mainBlock);

        // Make block for Faults
        makeFaultBlocks(as, mainBlock);

        // Make blocks for nodes
        makeFaultNodeBlocks(as, mainBlock);


        return as;
    }

    private void declareAllFaults(AvatarSpecification _as, AvatarBlock _main) {
        AvatarRelation ar = new AvatarRelation("MainRelation", _main, _main, null);
        ar.setAsynchronous(false);
        ar.setPrivate(true);
        ar.setBroadcast(false);

        _as.addRelation(ar);
        for (Fault Fault : at.getFaults()) {
            avatartranslator.AvatarSignal makeFault = new avatartranslator.AvatarSignal("make__" + Fault.getName(), AvatarSignal.OUT, listE.getTG(Fault));
            _main.addSignal(makeFault);
            avatartranslator.AvatarSignal stopMakeFault = new avatartranslator.AvatarSignal("makeStop__" + Fault.getName(), AvatarSignal.IN, listE.getTG(Fault));
            _main.addSignal(stopMakeFault);
            avatartranslator.AvatarSignal acceptFault = new avatartranslator.AvatarSignal("accept__" + Fault.getName(), AvatarSignal.IN, listE.getTG(Fault));
            _main.addSignal(acceptFault);
            avatartranslator.AvatarSignal stopAcceptFault = new avatartranslator.AvatarSignal("acceptStop__" + Fault.getName(), AvatarSignal.OUT, listE.getTG(Fault));
            _main.addSignal(stopAcceptFault);
            ar.addSignals(makeFault, acceptFault);
            ar.addSignals(stopMakeFault, stopAcceptFault);

            // If Fault is not leaf: add the intermediate action to activate the intermediate leaf
            if (!Fault.isLeaf()) {
                avatartranslator.AvatarSignal nodeDone = new avatartranslator.AvatarSignal("nodeDone__" + Fault.getName(), AvatarSignal.OUT, listE.getTG(Fault));
                _main.addSignal(nodeDone);
                avatartranslator.AvatarSignal activateFault = new avatartranslator.AvatarSignal("activate__" + Fault.getName(), AvatarSignal.IN, listE.getTG(Fault));
                _main.addSignal(activateFault);
                ar.addSignals(nodeDone, activateFault);

            }

        }
    }

    private void makeFaultBlocks(AvatarSpecification _as, AvatarBlock _main) {
        for (Fault Fault : at.getFaults()) {
            if (Fault.isLeaf()) {
                // Make the block
                AvatarBlock ab = new AvatarBlock(Fault.getName(), _as, listE.getTG(Fault));
                _as.addBlock(ab);
                ab.setFather(_main);

                avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("make__" + Fault.getName());
                avatartranslator.AvatarSignal stopFault = _main.getAvatarSignalWithName("makeStop__" + Fault.getName());

                if ((sigFault != null) && (stopFault != null)) {
                    makeFaultBlockSMD(ab, sigFault, stopFault, Fault.isEnabled(), listE.getTG(Fault));
                }

            } else {
                // Make the block
                AvatarBlock ab = new AvatarBlock(Fault.getName(), _as, listE.getTG(Fault));
                _as.addBlock(ab);
                ab.setFather(_main);

                avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("make__" + Fault.getName());
                avatartranslator.AvatarSignal stopFault = _main.getAvatarSignalWithName("makeStop__" + Fault.getName());
                avatartranslator.AvatarSignal activateFault = _main.getAvatarSignalWithName("activate__" + Fault.getName());

                makeIntermediateFaultBlockSMD(ab, sigFault, stopFault, activateFault, Fault.isEnabled(), listE.getTG(Fault));

                // Intermediate Fault
            }
        }
    }


    private void makeFaultBlockSMD(AvatarBlock _ab, avatartranslator.AvatarSignal _sigFault, avatartranslator.AvatarSignal _sigStop, boolean isEnabled, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;

        boolean isCheckable = false;
        boolean isChecked = false;

        AvatarStateMachine asm = _ab.getStateMachine();

        if (isEnabled) {

            if (_ref1 instanceof TGComponent) {
                isCheckable = ((TGComponent) (_ref1)).hasCheckableAccessibility();
            }
            if (_ref1 instanceof TGComponent) {
                isChecked = ((TGComponent) (_ref1)).hasCheckedAccessibility();
            }

            AvatarStartState start = new AvatarStartState("start", _ref);
            AvatarState mainState = new AvatarState("main", _ref, false, false);
            AvatarState performedState = new AvatarState("main", _ref1, isCheckable, isChecked);
            performedState.setAsVerifiable(true);
            AvatarState mainStop = new AvatarState("stop", _ref, false, false);
            AvatarActionOnSignal getMake = new AvatarActionOnSignal("GettingFault", _sigFault, _ref1);
            AvatarActionOnSignal getStop = new AvatarActionOnSignal("GettingStop", _sigStop, _ref);

            asm.addElement(start);
            asm.setStartState(start);
            asm.addElement(mainState);
            asm.addElement(performedState);
            asm.addElement(getMake);
            asm.addElement(getStop);


            AvatarTransition at = new AvatarTransition(_ab, "at1", _ref);
            asm.addElement(at);
            start.addNext(at);
            at.addNext(mainState);

            at = new AvatarTransition(_ab, "at2", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(getMake);

            at = new AvatarTransition(_ab, "at3", _ref);
            asm.addElement(at);
            getMake.addNext(at);
            at.addNext(performedState);

            at = new AvatarTransition(_ab, "backToMain", _ref);
            asm.addElement(at);
            performedState.addNext(at);
            at.addNext(mainState);

            at = new AvatarTransition(_ab, "at4", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(getStop);

            at = new AvatarTransition(_ab, "at5", _ref);
            asm.addElement(at);
            getStop.addNext(at);
            at.addNext(mainStop);

        } else {

            AvatarStartState start = new AvatarStartState("start", _ref);
            AvatarState mainState = new AvatarState("main", _ref, false, false);
            AvatarState mainStop = new AvatarState("stop", _ref, false, false);
            AvatarActionOnSignal getStop = new AvatarActionOnSignal("GettingStop", _sigStop, _ref);

            asm.addElement(start);
            asm.setStartState(start);
            asm.addElement(mainState);
            asm.addElement(getStop);


            AvatarTransition at = new AvatarTransition(_ab, "at1", _ref);
            asm.addElement(at);
            start.addNext(at);
            at.addNext(mainState);

            at = new AvatarTransition(_ab, "at4", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(getStop);

            at = new AvatarTransition(_ab, "at5", _ref);
            asm.addElement(at);
            getStop.addNext(at);
            at.addNext(mainStop);
        }

    }

    private void makeIntermediateFaultBlockSMD(AvatarBlock _ab, avatartranslator.AvatarSignal _sigFault, avatartranslator.AvatarSignal _sigStop, avatartranslator.AvatarSignal _sigActivate, boolean isEnabled, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        boolean isCheckable = false;
        boolean isChecked = false;

        if (isEnabled) {
            if (_ref1 instanceof TGComponent) {
                isCheckable = ((TGComponent) (_ref1)).hasCheckableAccessibility();
            }
            if (_ref1 instanceof TGComponent) {
                isChecked = ((TGComponent) (_ref1)).hasCheckedAccessibility();
            }
            AvatarStartState start = new AvatarStartState("start", _ref);
            AvatarState activateState = new AvatarState("activate", _ref, false, false);
            AvatarState mainState = new AvatarState("main", _ref, false, false);
            AvatarState activatedState = new AvatarState("activated", _ref1, isCheckable, isChecked);
            if (_ref1 instanceof FTDFault) {
                activatedState.setAsVerifiable(true);
            }

            AvatarState performedState = new AvatarState("performed", _ref, false, false);
            AvatarState mainStop = new AvatarState("stop", _ref, false, false);
            AvatarState stopBeforeActivate = new AvatarState("stopBeforeActivate", _ref, false, false);
            AvatarActionOnSignal getMake = new AvatarActionOnSignal("GettingFault", _sigFault, _ref1);
            AvatarActionOnSignal getStop = new AvatarActionOnSignal("GettingStop", _sigStop, _ref);
            AvatarActionOnSignal getStopInitial = new AvatarActionOnSignal("GettingInitialStop", _sigStop, _ref);
            AvatarActionOnSignal getActivate = new AvatarActionOnSignal("GettingActivate", _sigActivate, _ref1);
            AvatarActionOnSignal getActivateAfterStop = new AvatarActionOnSignal("GettingActivateAfterStop", _sigActivate, _ref1);

            asm.addElement(start);
            asm.setStartState(start);
            asm.addElement(activateState);
            asm.addElement(activatedState);
            asm.addElement(mainState);
            asm.addElement(stopBeforeActivate);
            asm.addElement(performedState);
            asm.addElement(getMake);
            asm.addElement(getStop);
            asm.addElement(getStopInitial);
            asm.addElement(getActivate);
            asm.addElement(getActivateAfterStop);


            AvatarTransition at = new AvatarTransition(_ab, "at1", _ref);
            asm.addElement(at);
            start.addNext(at);
            at.addNext(activateState);

            at = new AvatarTransition(_ab, "at1_act", _ref);
            asm.addElement(at);
            activateState.addNext(at);
            at.addNext(getActivate);

            at = new AvatarTransition(_ab, "at1_performed", _ref);
            asm.addElement(at);
            getActivate.addNext(at);
            at.addNext(activatedState);

            at = new AvatarTransition(_ab, "at2_main", _ref);
            asm.addElement(at);
            activatedState.addNext(at);
            at.addNext(mainState);
            at.setHidden(true);


            at = new AvatarTransition(_ab, "at2", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(getMake);

            at = new AvatarTransition(_ab, "at3", _ref);
            asm.addElement(at);
            getMake.addNext(at);
            at.addNext(performedState);

            at = new AvatarTransition(_ab, "backToMain", _ref);
            asm.addElement(at);
            performedState.addNext(at);
            at.addNext(mainState);

            at = new AvatarTransition(_ab, "at4", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(getStop);

            at = new AvatarTransition(_ab, "at5", _ref);
            asm.addElement(at);
            getStop.addNext(at);
            at.addNext(mainStop);

            // Stop before activate
            at = new AvatarTransition(_ab, "at6", _ref);
            asm.addElement(at);
            activateState.addNext(at);
            at.addNext(getStopInitial);

            at = new AvatarTransition(_ab, "at7", _ref);
            asm.addElement(at);
            getStopInitial.addNext(at);
            at.addNext(stopBeforeActivate);

            at = new AvatarTransition(_ab, "at8", _ref);
            asm.addElement(at);
            stopBeforeActivate.addNext(at);
            at.addNext(getActivateAfterStop);

            at = new AvatarTransition(_ab, "at9", _ref);
            asm.addElement(at);
            getActivateAfterStop.addNext(at);
            at.addNext(mainStop);
        } else {
            AvatarStartState start = new AvatarStartState("start", _ref);
            AvatarState activateState = new AvatarState("activate", _ref, false, false);
            AvatarState mainState = new AvatarState("main", _ref, false, false);
            AvatarState activatedState = new AvatarState("main", _ref1, isCheckable, isChecked);
            AvatarState mainStop = new AvatarState("stop", _ref, false, false);
            AvatarState stopBeforeActivate = new AvatarState("stopBeforeActivate", _ref, false, false);
            AvatarActionOnSignal getStop = new AvatarActionOnSignal("GettingStop", _sigStop, _ref);
            AvatarActionOnSignal getStopInitial = new AvatarActionOnSignal("GettingInitialStop", _sigStop, _ref);
            AvatarActionOnSignal getActivate = new AvatarActionOnSignal("GettingActivate", _sigActivate, _ref1);
            AvatarActionOnSignal getActivateAfterStop = new AvatarActionOnSignal("GettingActivateAfterStop", _sigActivate, _ref1);

            asm.addElement(start);
            asm.setStartState(start);
            asm.addElement(activateState);
            asm.addElement(activatedState);
            asm.addElement(mainState);
            asm.addElement(stopBeforeActivate);
            asm.addElement(getStop);
            asm.addElement(getStopInitial);
            asm.addElement(getActivate);
            asm.addElement(getActivateAfterStop);


            AvatarTransition at = new AvatarTransition(_ab, "at1", _ref);
            asm.addElement(at);
            start.addNext(at);
            at.addNext(activateState);

            at = new AvatarTransition(_ab, "at1_act", _ref);
            asm.addElement(at);
            activateState.addNext(at);
            at.addNext(getActivate);

            at = new AvatarTransition(_ab, "at1_performed", _ref);
            asm.addElement(at);
            getActivate.addNext(at);
            at.addNext(activatedState);

            at = new AvatarTransition(_ab, "at2_main", _ref);
            asm.addElement(at);
            activatedState.addNext(at);
            at.addNext(mainState);
            at.setHidden(true);

            at = new AvatarTransition(_ab, "at4", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(getStop);

            at = new AvatarTransition(_ab, "at5", _ref);
            asm.addElement(at);
            getStop.addNext(at);
            at.addNext(mainStop);

            // Stop before activate
            at = new AvatarTransition(_ab, "at6", _ref);
            asm.addElement(at);
            activateState.addNext(at);
            at.addNext(getStopInitial);

            at = new AvatarTransition(_ab, "at7", _ref);
            asm.addElement(at);
            getStopInitial.addNext(at);
            at.addNext(stopBeforeActivate);

            at = new AvatarTransition(_ab, "at8", _ref);
            asm.addElement(at);
            stopBeforeActivate.addNext(at);
            at.addNext(getActivateAfterStop);

            at = new AvatarTransition(_ab, "at9", _ref);
            asm.addElement(at);
            getActivateAfterStop.addNext(at);
            at.addNext(mainStop);
        }
    }


    private void makeFaultNodeBlocks(AvatarSpecification _as, AvatarBlock _main) {
        //Fault att;

        for (FaultNode node : at.getFaultNodes()) {
            if (node.isWellFormed()) {
                // Make the block
                AvatarBlock ab = new AvatarBlock(node.getName(), _as, listE.getTG(node));
                _as.addBlock(ab);
                ab.setFather(_main);

                if (node instanceof ANDNode) {
                    makeANDNode(_as, _main, ab, (ANDNode) node, listE.getTG(node));
                } else if (node instanceof ORNode) {
                    makeORNode(_as, _main, ab, (ORNode) node, listE.getTG(node));
                } else if (node instanceof XORNode) {
                    makeXORNode(_as, _main, ab, (XORNode) node, listE.getTG(node));
                } else if (node instanceof SequenceNode) {
                    makeSequenceNode(_as, _main, ab, (SequenceNode) node, listE.getTG(node));
                } else if (node instanceof AfterNode) {
                    makeAfterNode(_as, _main, ab, (AfterNode) node, listE.getTG(node));
                } else if (node instanceof BeforeNode) {
                    makeBeforeNode(_as, _main, ab, (BeforeNode) node, listE.getTG(node));
                } else if (node instanceof VoteNode) {
                    makeVoteNode(_as, _main, ab, (VoteNode) node, listE.getTG(node));
                } else if (node instanceof NOTNode) {
                    makeNOTNode(_as, _main, ab, (NOTNode) node, listE.getTG(node));
                }
            }
        }
    }


    private void makeANDNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, ANDNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        AvatarTransition atF = new AvatarTransition(_ab, "at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        atF.setHidden(true);
        String finalGuard = "";
        for (Fault att : _node.getInputFaults()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ab, _ref);
            if (finalGuard.length() == 0) {
                finalGuard += "(" + att.getName() + "__performed == true)";
            } else {
                finalGuard += " && (" + att.getName() + "__performed == true)";
            }
            _ab.addAttribute(aa);
            atF.addAction(att.getName() + "__performed = false");

            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            AvatarTransition at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            at.setProbability(att.getProbability());
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(mainState);
            at.setHidden(true);
        }

        // Adding resulting Fault
        AvatarTransition at = new AvatarTransition(_ab, "at_toEnd", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(endState);
        at.setGuard("[" + finalGuard + "]");

        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);
    }

    private void makeNOTNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, NOTNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        AvatarTransition atF = new AvatarTransition(_ab, "at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        atF.setHidden(true);
        String finalGuard = "";
        for (Fault att : _node.getInputFaults()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ab, _ref);
            if (finalGuard.length() == 0) {
                finalGuard += "(" + att.getName() + "__performed == false)";
            } else {
                finalGuard += " && (" + att.getName() + "__performed == false)";
            }
            _ab.addAttribute(aa);
            atF.addAction(att.getName() + "__performed = false");


            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            AvatarTransition at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(mainState);
            at.setHidden(true);
        }

        // Adding resulting Fault
        AvatarTransition at = new AvatarTransition(_ab, "at_toEnd", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(endState);
        at.setGuard("[" + finalGuard + "]");

        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);
    }

    private void makeVoteNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, VoteNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        AvatarTransition atF = new AvatarTransition(_ab, "at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        atF.setHidden(true);
        String finalGuard = "";

        AvatarAttribute aCount = new AvatarAttribute("__nbOfPerformed", AvatarType.INTEGER, _ab, _ref);
        _ab.addAttribute(aCount);

        double nbOfVoters = _node.getInputFaults().size() / 2.0;
        int thres = (int) (Math.ceil(nbOfVoters));
        finalGuard = "( __nbOfPerformed >= " + thres + ")";
        for (Fault att : _node.getInputFaults()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ab, _ref);

            /*if (finalGuard.length() == 0) {
                finalGuard += "(" + att.getName() + "__performed == true)";
            } else {
                finalGuard += " && (" + att.getName() + "__performed == true)";
            }*/
            _ab.addAttribute(aa);
            atF.addAction(att.getName() + "__performed = false");


            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            AvatarTransition at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(att.getName() + "__performed = true");
            at.addAction(aCount.getName() + " = " + aCount.getName() + " + 1" );
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(mainState);
            at.setHidden(true);
        }

        // Adding resulting Fault
        AvatarTransition at = new AvatarTransition(_ab, "at_toEnd", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(endState);
        at.setGuard("[" + finalGuard + "]");

        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);
    }


    private void makeORNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, ORNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);

        AvatarTransition atF = new AvatarTransition(_ab, "at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        AvatarGuard finalGuard = null;
        for (Fault att : _node.getInputFaults()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ab, _ref);
            if (finalGuard == null)
                finalGuard = new AvatarSimpleGuardDuo(aa, AvatarConstant.TRUE, "==");
            else
                finalGuard = AvatarGuard.addGuard(finalGuard, new AvatarSimpleGuardDuo(aa, AvatarConstant.TRUE, "=="), "||");

            _ab.addAttribute(aa);
            atF.addAction(new AvatarActionAssignment(aa, AvatarConstant.FALSE));

            // From Main
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            AvatarTransition at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(new AvatarActionAssignment(aa, AvatarConstant.TRUE));
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(mainState);

            // Link from End
            acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            asm.addElement(at);
            endState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(new AvatarActionAssignment(aa, AvatarConstant.TRUE));
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(endState);

            // Link from Overall
            acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            asm.addElement(at);
            overallState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(new AvatarActionAssignment(aa, AvatarConstant.TRUE));
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(overallState);


        }


        // Adding resulting Fault
        AvatarTransition at = new AvatarTransition(_ab, "at_toEnd", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(endState);
        at.setGuard(finalGuard);

        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);

    }


    private void makeXORNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, XORNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState stoppingAll = new AvatarState("stoppingAll", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        asm.addElement(stoppingAll);


        AvatarTransition atF = new AvatarTransition(_ab, "at1", _ref);
        atF.setHidden(true);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        AvatarAttribute oneDone = new AvatarAttribute("oneDone", AvatarType.BOOLEAN, _ab, _ref);
        AvatarGuard finalGuard = new AvatarSimpleGuardDuo(oneDone, AvatarConstant.TRUE, "==");
        AvatarGuard toEndGuard = null;
        _ab.addAttribute(oneDone);
        atF.addAction("oneDone = false");
        for (Fault att : _node.getInputFaults()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ab, _ref);
            _ab.addAttribute(aa);
            atF.addAction(new AvatarActionAssignment(aa, AvatarConstant.FALSE));
            if (toEndGuard == null)
                toEndGuard = new AvatarSimpleGuardDuo(aa, AvatarConstant.TRUE, "==");
            else
                toEndGuard = AvatarGuard.addGuard(toEndGuard, new AvatarSimpleGuardDuo(aa, AvatarConstant.TRUE, "=="), "&&");

            // From Main
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            AvatarTransition at = new AvatarTransition(_ab, "at_toInputFault", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarBinaryGuard(
                    new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="),
                    new AvatarSimpleGuardDuo(oneDone, AvatarConstant.FALSE, "=="),
                    "&&"));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(new AvatarActionAssignment(aa, AvatarConstant.TRUE));
            at.setHidden(true);
            at.addAction(new AvatarActionAssignment(oneDone, AvatarConstant.TRUE));
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(mainState);

            // Link from stoppingAll
            //           if (att.isLeaf()) {
            // Leaf Fault -> must make a stop
            sigAtt = _main.getAvatarSignalWithName("acceptStop__" + att.getName());
            acceptFault = new AvatarActionOnSignal("StopFault", sigAtt, _ref1);
            asm.addElement(acceptFault);
            at = new AvatarTransition(_ab, "at_toInputFault_leaf", _ref);
            asm.addElement(at);
            stoppingAll.addNext(at);
            at.addNext(acceptFault);
            at.setGuard(new AvatarSimpleGuardDuo(aa, AvatarConstant.FALSE, "=="));
            at = new AvatarTransition(_ab, "at_fromInputFault", _ref);
            at.addAction(new AvatarActionAssignment(aa, AvatarConstant.TRUE));
            at.setHidden(true);
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(stoppingAll);
            //           } else {
            // Generated Fault-> must set performed to true.
            /*          at = new AvatarTransition("at_toInputFault", _ref);
                        stoppingAll.addNext(at);
                        asm.addElement(at);
                        at.addNext(stoppingAll);
                        at.setGuard("["+att.getName() + "__performed == false]");
                        at.addAction(att.getName() + "__performed = true");
                        at.setHidden(true);*/
            //          }

        }

        // Adding link to stopping all
        AvatarTransition at = new AvatarTransition(_ab, "at_toStoppingAll", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(stoppingAll);
        at.setGuard(finalGuard);


        // Adding resulting Fault
        at = new AvatarTransition(_ab, "at_toEnd", _ref);
        asm.addElement(at);
        stoppingAll.addNext(at);
        at.addNext(endState);
        at.setGuard(toEndGuard);

        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);

    }


    private void makeSequenceNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, SequenceNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        _node.orderFaults();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);


        AvatarTransition at = new AvatarTransition(_ab, "at", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        AvatarState previousState = mainState;

        // Chaining accept Faults
        for (Fault att : _node.getInputFaults()) {
            AvatarState state = new AvatarState("state__" + att.getName(), _ref);
            asm.addElement(state);
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);

            at = new AvatarTransition(_ab, "at", _ref);
            asm.addElement(at);
            previousState.addNext(at);
            at.addNext(acceptFault);
            at = new AvatarTransition(_ab, "at", _ref);
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(state);
            previousState = state;
        }

        at = new AvatarTransition(_ab, "at", _ref);
        asm.addElement(at);
        previousState.addNext(at);
        at.addNext(endState);


        // Performing resulting Fault
        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);
    }

    private void makeAfterNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, AfterNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        _node.orderFaults();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);


        AvatarTransition at = new AvatarTransition(_ab, "at", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        AvatarState previousState = mainState;

        // Chaining accept Faults
        int cpt = 0;
        for (Fault att : _node.getInputFaults()) {
            AvatarState state = new AvatarState("state__" + att.getName(), _ref);
            asm.addElement(state);
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);

            at = new AvatarTransition(_ab, "at", _ref);
            asm.addElement(at);
            previousState.addNext(at);
            at.addNext(acceptFault);
            if (cpt > 0) {
                at.setDelays("" + _node.getTime(), "" + _node.getTime());
            }
            at = new AvatarTransition(_ab, "at", _ref);
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(state);
            previousState = state;
            cpt++;
        }

        at = new AvatarTransition(_ab, "at", _ref);
        asm.addElement(at);
        previousState.addNext(at);
        at.addNext(endState);


        // Performing resulting Fault
        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);
    }

    private void makeBeforeNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, BeforeNode _node, Object _ref) {
        Object _ref1 = _ref;
        _ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        _node.orderFaults();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false, false);
        AvatarState endState = new AvatarState("end", _ref, false, false);
        AvatarState overallState = new AvatarState("overall", _ref, false, false);
        AvatarState timeout = new AvatarState("timeout", _ref, false, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        asm.addElement(timeout);


        AvatarTransition at = new AvatarTransition(_ab, "at", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        AvatarState previousState = mainState;

        // Chaining accept Faults
        int cpt = 0;
        for (Fault att : _node.getInputFaults()) {
            AvatarState state = new AvatarState("state__" + att.getName(), _ref);
            asm.addElement(state);
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptFault = new AvatarActionOnSignal("AcceptFault", sigAtt, _ref1);
            asm.addElement(acceptFault);

            at = new AvatarTransition(_ab, "at", _ref);
            asm.addElement(at);
            previousState.addNext(at);
            at.addNext(acceptFault);
            if (cpt > 0) {
                at = new AvatarTransition(_ab, "at_totimeout", _ref);
                asm.addElement(at);
                previousState.addNext(at);
                at.addNext(timeout);
                at.setDelays("" + _node.getTime(), "" + _node.getTime());
            }
            at = new AvatarTransition(_ab, "at", _ref);
            asm.addElement(at);
            acceptFault.addNext(at);
            at.addNext(state);
            previousState = state;
            cpt++;
        }

        at = new AvatarTransition(_ab, "at", _ref);
        asm.addElement(at);
        previousState.addNext(at);
        at.addNext(endState);


        // Performing resulting Fault
        Fault resulting = _node.getResultingFault();
        avatartranslator.AvatarSignal sigFault = _main.getAvatarSignalWithName("nodeDone__" + resulting.getName());
        AvatarActionOnSignal resultingFault = new AvatarActionOnSignal("ResultingFault", sigFault, _ref1);
        asm.addElement(resultingFault);
        at = new AvatarTransition(_ab, "at_toResultingFault", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingFault);
        at = new AvatarTransition(_ab, "at_Overall", _ref);
        asm.addElement(at);
        resultingFault.addNext(at);
        at.addNext(overallState);
    }

    private void addCheckingError(CheckingError ce) {
        if (checkingErrors == null) {
            checkingErrors = new LinkedList<CheckingError>();
        }
        checkingErrors.add(ce);
    }
//
//    private void addWarning(CheckingError ce) {
//        if (warnings == null) {
//            warnings = new LinkedList<CheckingError> ();
//        }
//        warnings.add (ce);
//    }
}
