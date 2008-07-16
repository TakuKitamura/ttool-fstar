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
 * Class RequirementModeling
 * Management of formal requirements
 * Creation: 11/08/2006
 * @version 1.0 11/08/2006
 * @author Ludovic APVRILLE
 * @see
 */


package tmatrix;

import java.util.*;


import translator.*;
import ui.*;
import ui.req.*;
import sddescription.*;
import sdtranslator.*;

public class RequirementModeling {
    protected static final String NOT_FORMAL = "Requirement is not a fomal requirement";
    protected static final String TOO_MANY_VERIFY = "Observer is linked with too many verify connectors";
    protected static final String UNKNOWN_DIAGRAM_NAME = "is an unknown diagram";
    protected static final String BAD_FORMATTING = "Bad formal description";
    
    private LinkedList matrix;
    private Vector errors, warnings;
    
    private MainGUI mgui;
    
    // reqs should contain only formal requirement
    public RequirementModeling(Vector reqs, RequirementDiagramPanel rdp, MainGUI _mgui) {
        mgui = _mgui;
        
        matrix = new LinkedList();
        errors = new Vector();
        warnings = new Vector();
        
        generateFirstMatrix(reqs, rdp);
        printMatrix();
        generateTURTLEModelings();
        generateFormalSpecification();
    }
    
    
    public void generateFirstMatrix(Vector reqs, RequirementDiagramPanel rdp) {
        Requirement r;
        int i, j;
        CheckingError ce;
        Requirements rs;
        ListIterator iterator;
        TGComponent tgc;
        int cpt;
        String tab[];
        RequirementObserver ro;
        
        for(i=0; i<reqs.size(); i++) {
            r = (Requirement)(reqs.elementAt(i));
            if (!(r.isFormal())) {
                ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, NOT_FORMAL);
                ce.setTDiagramPanel(rdp);
                ce.setTGComponent(r);
                errors.add(ce);
            } else {
                // r is a formal requirement!
                // Search for all observers linked only to this formal requirement with a "verify" semantics
                iterator = rdp.getComponentList().listIterator();
                while(iterator.hasNext()) {
                    tgc = (TGComponent)(iterator.next());
                    if (tgc instanceof RequirementObserver) {
                        ro = (RequirementObserver)tgc;
                        cpt = rdp.nbOfVerifyStartingAt(tgc);
                        if (cpt > 1) {
                            ce = new CheckingError(CheckingError.STRUCTURE_ERROR, TOO_MANY_VERIFY);
                            ce.setTDiagramPanel(rdp);
                            ce.setTGComponent(tgc);
                            errors.add(ce);
                        } else if ((cpt == 1) && (rdp.isLinkedByVerifyTo(tgc, r))) {
                            // Good observer!
                            // So ... parse all observed diagrams -> for the one found -> add a line in the matrix
                            //System.out.println("Getting diagram names for " + ro.getDiagramNames());
                            tab = ro.getDiagramNames();
                            
                            for(j=0; j<tab.length; j++) {
                                //System.out.println("Diagram name = " + tab[j]);
                                rs = new Requirements();
                                rs.req = r;
                                rs.ro = ro;
                                rs.diagramName = tab[j];
                                matrix.add(rs);
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    // For each (req,obs,diagram), a TURTLE modeling is generated
    public void generateTURTLEModelings() {
        Requirements reqs;
        ListIterator iterator = matrix.listIterator();
        TURTLEPanel tp;
        CheckingError ce;
        //int sizee;
        
        while(iterator.hasNext()) {
            reqs = (Requirements)(iterator.next());
            
            // Locate diagram
            tp = mgui.getTURTLEPanel(reqs.diagramName);
            
            if (tp == null) {
                ce = new CheckingError(CheckingError.STRUCTURE_ERROR, reqs.diagramName + " " + UNKNOWN_DIAGRAM_NAME);
                ce.setTDiagramPanel(reqs.ro.getTDiagramPanel());
                ce.setTGComponent(reqs.ro);
                errors.add(ce);
            } else {
                //sizee = errors.size();
                buildTURTLEModelingFromTURTLEPanel(reqs, tp);
                /*if (errors.size() != sizee) {
                    System.out.println("New errors!!");
                }*/
                reqs.tm = updateTURTLEModeling(reqs.tm, reqs);
                reqs.tm.simplify();
            }
        }
        
    }
    
    // RT-LOTOS is used by default
    public void generateFormalSpecification() {
        Requirements reqs;
        ListIterator iterator = matrix.listIterator();
        //TURTLEPanel tp;
        //CheckingError ce;
        
        while(iterator.hasNext()) {
            reqs = (Requirements)(iterator.next());
            if (reqs.tm != null) {
                System.out.println("Generating a formal specification for " + reqs.ro.getValue());
                TURTLETranslator tt = new TURTLETranslator(reqs.tm);
                reqs.formalSpec = tt.generateRTLOTOS();
                warnings.addAll(tt.getWarnings());
            }
        }
    }
    
    public Vector getCheckingErrors() {
        return errors;
    }
    
    public Vector getWarnings() {
        return warnings;
    }
    
    public LinkedList getMatrix() {
        return matrix;
    }
    
    public void printMatrix() {
        System.out.println("Matrix:\n-------\n" + toString() + "\n-------");
    }
    
    public int nbOfElements() {
        return matrix.size();
    }
    
    public Requirements getRequirements(int index) {
        return (Requirements)(matrix.get(index));
    }
    
    public String toString() {
        Requirements reqs;
        ListIterator iterator = matrix.listIterator();
        String ret = "";
        
        while(iterator.hasNext()) {
            reqs = (Requirements)(iterator.next());
            ret += reqs.toString() + "\n";
        }
        
        return ret;
    }
    
    private void buildTURTLEModelingFromTURTLEPanel(Requirements reqs, TURTLEPanel tp) {
        TURTLEModeling tm;
        CorrespondanceTGElement listE;
        Vector errorstmp;
        CheckingError ce;
        
        mgui.reinitCountOfPanels();
        
        if (tp instanceof AnalysisPanel) {
            HMSC h = null;
            AnalysisPanel ap = (AnalysisPanel)tp;
            AnalysisPanelTranslator apt = new AnalysisPanelTranslator(ap, mgui);
            
            try {
                h = apt.translateHMSC();
                apt.translateMSCs(h);  // -> sans doute une analysisSyntaxException ?
            } catch (AnalysisSyntaxException ase) {
                ce = new CheckingError(CheckingError.STRUCTURE_ERROR, ase.getMessage());
                errors.add(ce);
                return;
            }
            
            
            SDTranslator sd = new SDTranslator(h);
            try {
                tm = sd.toTURTLEModeling();
            } catch (SDTranslationException e) {
                ce = new CheckingError(CheckingError.STRUCTURE_ERROR, e.getMessage());
                errors.add(ce);
                return;
            }
            
            TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
            errorstmp = tmc.syntaxAnalysisChecking();
            
            if ((errorstmp != null) && (errorstmp.size() > 0)){
                errors.addAll(errorstmp);
            }
            
            reqs.tm = tm;
        } else if (tp instanceof DesignPanel) {
            System.out.println("Dealing with a design panel ...");
            //MasterGateManager.reinitNameRestriction();
            DesignPanel dp = (DesignPanel)tp;
            // Builds a TURTLE modeling from diagrams
            
            DesignPanelTranslator dpt = new DesignPanelTranslator(dp);
            tm = dpt.generateTURTLEModeling();
            
            //tm.print();
            
            listE = dpt.getCorrespondanceTGElement();
            errors = dpt.getErrors();
            if ((errors != null) && (errors.size()>0)) {
                //System.out.println("Errors 1");
                return;
            }
            
            // modeling is built
            // Now check it !
            TURTLEModelChecker tmc = new TURTLEModelChecker(tm);
            errorstmp = tmc.syntaxAnalysisChecking();
            
            if ((errorstmp != null) && (errorstmp.size() > 0)){
                //System.out.println("Errors 2");
                errors.addAll(errorstmp);
            }
            
            reqs.tm = tm;
        }
    }
    
    public TURTLEModeling updateTURTLEModeling(TURTLEModeling tm, Requirements reqs) {
        // Get the requirements String
        String text = reqs.req.getText();
        //String s1, s2, s3;
        //int index1;
        String[] actions;
        
        //System.out.println("Generating for " + text);
        
        // BEFORE_T
        if (text.indexOf("BEFORE_T") != -1) {
            actions = extractInfo(reqs, text, "BEFORE_T", 2, 1);
            if (actions == null) {
                return tm;
            }
            return generateBeforeT(tm, reqs, actions, true);
            
            //LESS_T
        } else if (text.indexOf("LESS_T") != -1) {
            actions = extractInfo(reqs, text, "LESS_T", 2, 1);
            if (actions == null) {
                return tm;
            }
            return generateLessThan(tm, reqs, actions);
            
            //GREAT_T
        } else if (text.indexOf("GREAT_T") != -1) {
            actions = extractInfo(reqs, text, "GREAT_T", 2, 1);
            if (actions == null) {
                return tm;
            }
            return generateGreatThan(tm, reqs, actions);
            
            //BETWEEN
        } else if (text.indexOf("BETWEEN") != -1) {
            actions = extractInfo(reqs, text, "BETWEEN", 2, 2);
            if (actions == null) {
                return tm;
            }
            return generateBetween(tm, reqs, actions);
            
            // OUTSIDE
        } else if (text.indexOf("OUTSIDE") != -1) {
            actions = extractInfo(reqs, text, "OUTSIDE", 2, 2);
            if (actions == null) {
                return tm;
            }
            return generateOutside(tm, reqs, actions);
            
            // MEETS
        } else if (text.indexOf("MEETS") != -1) {
            actions = extractInfo(reqs, text, "MEETS", 2, 0);
            if (actions == null) {
                return tm;
            }
            return generateMeet(tm, reqs, actions);
            
            // BEFORE
        } else if (text.indexOf("BEFORE") != -1) {
            actions = extractInfo(reqs, text, "BEFORE", 2, 0);
            if (actions == null) {
                return tm;
            }
            return generateBefore(tm, reqs, actions);
            
            // STARTS
        } else if (text.indexOf("STARTS") != -1) {
            actions = extractInfo(reqs, text, "STARTS", 2, 0);
            if (actions == null) {
                return tm;
            }
            return generateMeet(tm, reqs, actions);
            
            // FINISHES
        } else if (text.indexOf("FINISHES") != -1) {
            actions = extractInfo(reqs, text, "FINISHES", 2, 0);
            if (actions == null) {
                return tm;
            }
            return generateMeet(tm, reqs, actions);
            
            // UNKNOWN FORMAL REQUIREMENT -> ERROR
        } else {
            addReqFormattingError(reqs);
        }
        return tm;
    }
    
    private String[] extractInfo(Requirements reqs, String text, String word, int action, int time) {
        int index1 = text.indexOf(word);
        String s1 = text.substring(0, index1) + text.substring(index1+word.length()+1, text.length());
        s1 = s1.trim();
        System.out.println("s1=" + s1);
        String [] actions = extractData(s1);
        //print(actions);
        if (actions.length != (action + time)) {
            addReqFormattingError(reqs);
            return null;
        }
        if ((action == 2) && (time == 2)) {
            if (analysisActionActionTimeTime(reqs, actions) == false) {
                return null;
            }
        } else if ((action == 2) && (time == 1)) {
            if (analysisActionActionTime(reqs, actions) == false) {
                return null;
            }
        }
        return actions;
    }
    
    private String[] extractData(String s) {
        return s.trim().split("((\\s)+(\\r)*(\\n)*)+");
    }
    
    public void addReqFormattingError(Requirements reqs) {
        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, reqs.req.getValue() + ": " + BAD_FORMATTING);
        ce.setTDiagramPanel(reqs.req.getTDiagramPanel());
        ce.setTGComponent(reqs.req);
        errors.add(ce);
    }
    
    public boolean analysisActionActionTime(Requirements reqs, String[] actions) {
        boolean ret = true;
        if (analysisAction(reqs, actions[0]) == false) {
            ret = false;
        }
        if (analysisAction(reqs, actions[1]) == false) {
            ret = false;
        }
        if (analysisTime(reqs, actions[2]) == false) {
            ret = false;
        }
        return ret;
    }
    
    public boolean analysisActionActionTimeTime(Requirements reqs, String[] actions) {
        boolean ret = true;
        if(analysisActionActionTime(reqs, actions) == false) {
            ret = false;
        }
        if (analysisTime(reqs, actions[3]) == false) {
            ret = false;
        }
        return ret;
    }
    
    public boolean analysisAction(Requirements reqs, String action) {
        CheckingError ce;
        if (reqs.tm.knownAction(action) == false) {
            ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, reqs.req.getValue() + ", action : " + action + " " + "is an unknown action");
            ce.setTDiagramPanel(reqs.req.getTDiagramPanel());
            ce.setTGComponent(reqs.req);
            errors.add(ce);
            return false;
        }
        
        if (reqs.tm.hasSeveralTClasWithAction(action)) {
            ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, reqs.req.getValue() + ", action : " + action + " " + "belongs to more than one class");
            ce.setTDiagramPanel(reqs.req.getTDiagramPanel());
            ce.setTGComponent(reqs.req);
            errors.add(ce);
            return false;
        }
        
        return true;
    }
    
    public boolean analysisTime(Requirements reqs, String time) {
        try {
            int i = Integer.decode(time).intValue();
        } catch (NumberFormatException nfe) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, reqs.req.getValue() + ", value: " + time + " " + "is not a correct time value");
            ce.setTDiagramPanel(reqs.req.getTDiagramPanel());
            ce.setTGComponent(reqs.req);
            errors.add(ce);
            return false;
        }
        return true;
    }
    
    public TURTLEModeling generateLessThan(TURTLEModeling tm, Requirements reqs, String[] actions) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADChoice adc = new ADChoice();
        ADJunction junc0 = new ADJunction();
        //ADJunction junc1 = new ADJunction();
        ADActionStateWithGate action0 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate actionfirst = new ADActionStateWithGate(gaction1);
        ADTLO tlo0 = new ADTLO(gaction1);
        tlo0.setDelay(actions[2]);
        tlo0.setLatency("0");
        ADActionStateWithGate actionv = new ADActionStateWithGate(gv);
        
        ad.add(adc);
        ad.add(junc0);
        //ad.add(junc1);
        ad.add(actionfirst);
        ad.add(action0);
        ad.add(tlo0);
        ad.add(actionv);
        
        
        ads.addNext(junc0);
        //junc0.addNext(junc1);
        //junc1.addNext(adc);
        junc0.addNext(adc);
        adc.addNext(actionfirst);
        adc.addGuard("[]");
        adc.addNext(action0);
        adc.addGuard("[]");
        actionfirst.addNext(junc0);
        action0.addNext(tlo0);
        tlo0.addNext(junc0);
        tlo0.addNext(actionv);
        
        
        // Modifying observed class: after each observed action, a call to an action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        
        // Managing criticality
        manageCriticality(tm, reqs, t, ad, actions, 2, actionv, junc0);
        
        return tm;
    }
    
    public TURTLEModeling generateGreatThan(TURTLEModeling tm, Requirements reqs, String[] actions) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADChoice adc = new ADChoice();
        ADJunction junc0 = new ADJunction();
        ADActionStateWithGate action0 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate actionfirst = new ADActionStateWithGate(gaction1);
        ADTLO tlo0 = new ADTLO(gaction1);
        tlo0.setDelay(actions[2]);
        tlo0.setLatency("0");
        ADActionStateWithGate actionv = new ADActionStateWithGate(gv);
        ADStop stop = new ADStop();
        ad.add(adc);
        ad.add(junc0);
        ad.add(actionfirst);
        ad.add(action0);
        ad.add(tlo0);
        ad.add(actionv);
        ad.add(stop);
        
        ads.addNext(junc0);
        junc0.addNext(adc);
        adc.addNext(actionfirst);
        adc.addGuard("[]");
        adc.addNext(action0);
        adc.addGuard("[]");
        actionfirst.addNext(junc0);
        action0.addNext(tlo0);
        tlo0.addNext(actionv);
        tlo0.addNext(junc0);
        actionv.addNext(stop);
        
        // Modifying observed class: after each observed action, a call to an action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        
        // Managing criticality
        manageCriticality(tm, reqs, t, ad, actions, 2, actionv, junc0); 
        
        return tm;
    }
    
    public TURTLEModeling generateBeforeT(TURTLEModeling tm, Requirements reqs, String[] actions, boolean useTimeInfo) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADJunction junc1 = new ADJunction();
        ADActionStateWithGate action0 = new ADActionStateWithGate(gaction0);
        ADTLO tlo0 = new ADTLO(gaction1);
        if (useTimeInfo) {
            tlo0.setDelay(actions[2]);
        } else {
            tlo0.setDelay("1");
        }
        tlo0.setLatency("0");
        ADActionStateWithGate action2 = new ADActionStateWithGate(gv);
        ADStop stop = new ADStop();
        ad.add(junc1);
        ad.add(action0);
        ad.add(tlo0);
        ad.add(action2);
        ad.add(stop);
        
        ads.addNext(junc1);
        junc1.addNext(action0);
        action0.addNext(tlo0);
        tlo0.addNext(junc1);
        tlo0.addNext(action2);
        action2.addNext(stop);
        
        // Modifying observed class: after each observed action, a call to a action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        return tm;
    }
    
    public TURTLEModeling generateBetween(TURTLEModeling tm, Requirements reqs, String[] actions) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADChoice adc = new ADChoice();
        ADJunction junc0 = new ADJunction();
        ADJunction junc1 = new ADJunction();
        ADJunction junc2 = new ADJunction();
        ADActionStateWithGate action0 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate actionfirst = new ADActionStateWithGate(gaction1);
        ADTLO tlo0 = new ADTLO(gaction1);
        tlo0.setDelay(actions[2]);
        tlo0.setLatency("0");
        ADTLO tlo1 = new ADTLO(gaction1);
        tlo1.setDelay(deduct(actions[3],actions[2]));
        tlo1.setLatency("0");
        ADActionStateWithGate action2 = new ADActionStateWithGate(gv);
        ADStop stop = new ADStop();
        ad.add(adc);
        ad.add(junc0);
        ad.add(actionfirst);
        ad.add(junc1);
        ad.add(junc2);
        ad.add(action0);
        ad.add(tlo0);
        ad.add(tlo1);
        ad.add(action2);
        ad.add(stop);
        
        ads.addNext(junc0);
        junc0.addNext(adc);
        adc.addNext(actionfirst);
        adc.addGuard("[]");
        adc.addNext(junc1);
        adc.addGuard("[]");
        actionfirst.addNext(junc0);
        junc1.addNext(action0);
        action0.addNext(tlo0);
        tlo0.addNext(junc2);
        tlo0.addNext(tlo1);
        tlo1.addNext(junc1);
        tlo1.addNext(junc2);
        junc2.addNext(action2);
        action2.addNext(stop);
        
        // Modifying observed class: after each observed action, a call to a action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        return tm;
    }
    
    public TURTLEModeling generateOutside(TURTLEModeling tm, Requirements reqs, String[] actions) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADChoice adc = new ADChoice();
        ADJunction junc0 = new ADJunction();
        ADJunction junc1 = new ADJunction();
        
        ADActionStateWithGate actiona0 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate actiona1 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate actionb0 = new ADActionStateWithGate(gaction1);
        ADActionStateWithGate actionb1 = new ADActionStateWithGate(gaction1);
        ADTLO tlo0 = new ADTLO(gaction1);
        tlo0.setDelay(actions[2]);
        tlo0.setLatency("0");
        ADTLO tlo1 = new ADTLO(gaction1);
        tlo1.setDelay(deduct(actions[3],actions[2]));
        tlo1.setLatency("0");
        ADActionStateWithGate actionv = new ADActionStateWithGate(gv);
        ADStop stop = new ADStop();
        
        ads.addNext(junc0);
        junc0.addNext(adc);
        adc.addNext(actionb0);
        adc.addGuard("[]");
        adc.addNext(actiona1);
        adc.addGuard("[]");
        actionb0.addNext(actiona0);
        actiona0.addNext(junc0);
        actiona1.addNext(tlo0);
        tlo0.addNext(junc1);
        tlo0.addNext(tlo1);
        tlo1.addNext(actionv);
        tlo1.addNext(actionb1);
        actionv.addNext(stop);
        actionb1.addNext(junc1);
        junc1.addNext(junc0);
        
        ad.add(junc0);
        ad.add(junc1);
        ad.add(adc);
        ad.add(actiona0);
        ad.add(actiona1);
        ad.add(actionb0);
        ad.add(actionb1);
        ad.add(tlo0);
        ad.add(tlo1);
        ad.add(actionv);
        ad.add(stop);
        
        // Modifying observed class: after each observed action, a call to a action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        return tm;
    }
    
    public TURTLEModeling generateMeet(TURTLEModeling tm, Requirements reqs, String[] actions) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADChoice adc = new ADChoice();
        ADJunction junc1 = new ADJunction();
        ADJunction junc2 = new ADJunction();
        ADActionStateWithGate action0 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate action1 = new ADActionStateWithGate(gaction1);
        ADTLO tlo0 = new ADTLO(gaction1);
        tlo0.setDelay("1");
        tlo0.setLatency("0");
        ADActionStateWithGate action2 = new ADActionStateWithGate(gv);
        ADStop stop = new ADStop();
        ad.add(adc);
        ad.add(junc1);
        ad.add(junc2);
        ad.add(action0);
        ad.add(tlo0);
        ad.add(action2);
        ad.add(stop);
        
        ads.addNext(junc1);
        junc1.addNext(adc);
        adc.addNext(junc2);
        adc.addGuard("[]");
        adc.addNext(action0);
        adc.addGuard("[]");
        action1.addNext(junc1);
        action0.addNext(tlo0);
        tlo0.addNext(junc2);
        tlo0.addNext(action2);
        junc2.addNext(action1);
        action2.addNext(stop);
        
        // Modifying observed class: after each observed action, a call to a action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        return tm;
    }
    
    public TURTLEModeling generateBefore(TURTLEModeling tm, Requirements reqs, String[] actions) {
        // Creating TClass
        TClass t = new TClass("T__" + reqs.ro.getValue(), true);
        Gate gv = t.addNewGateIfApplicable(reqs.req.getViolatedAction());
        Gate gaction0 = t.addNewGateIfApplicable("obs__" + actions[0]);
        
        //System.out.println("Action0=" + actions[0] + "Action1=" + actions[1]);
        Gate gaction1 = t.addNewGateIfApplicable("obs__" + actions[1]);
        
        // Making Activity diagram
        ActivityDiagram ad = new ActivityDiagram();
        ADStart ads = ad.getStartState();
        t.setActivityDiagram(ad);
        
        ADChoice adc = new ADChoice();
        ADJunction junc1 = new ADJunction();
        ADJunction junc2 = new ADJunction();
        ADActionStateWithGate action0 = new ADActionStateWithGate(gaction0);
        ADActionStateWithGate action1 = new ADActionStateWithGate(gaction1);
        ADTLO tlo0 = new ADTLO(gaction1);
        tlo0.setDelay("1");
        tlo0.setLatency("0");
        ADActionStateWithGate action2 = new ADActionStateWithGate(gv);
        ADStop stop = new ADStop();
        ad.add(adc);
        ad.add(junc1);
        ad.add(junc2);
        ad.add(action0);
        ad.add(tlo0);
        ad.add(action2);
        ad.add(stop);
        
        ads.addNext(junc1);
        junc1.addNext(adc);
        adc.addNext(junc2);
        adc.addGuard("[]");
        adc.addNext(action0);
        adc.addGuard("[]");
        action1.addNext(junc1);
        action0.addNext(tlo0);
        tlo0.addNext(action2);
        tlo0.addNext(junc2);
        junc2.addNext(action1);
        action2.addNext(stop);
        
        // Modifying observed class: after each observed action, a call to a action synchronized with the observer is performed
        ModifyTMTwoActions(tm, t, gaction0, gaction1, reqs, actions);
        return tm;
    }
    
    public void ModifyTMTwoActions(TURTLEModeling tm, TClass t, Gate gaction0, Gate gaction1, Requirements reqs, String[] actions) {
        int nb;
        
        TClass t0 = tm.getTClassWithAction(actions[0]);
        Gate g0 = t0.getGateByName(actions[0]);
        Gate g00 = t0.addNewGateIfApplicable("obs__" + actions[0]);
        nb = t0.duplicateCall(g0, g00);
        if ( nb > 1) {
            addWarningDuplicate(reqs, actions[0], t0);
        } else if (nb == 0) {
            addWarningNoCall(reqs, actions[0], t0);
        }
        
        TClass t1 = tm.getTClassWithAction(actions[1]);
        Gate g1 = t1.getGateByName(actions[1]);
        Gate g11 = t1.addNewGateIfApplicable("obs__" + actions[1]);
        nb = t1.duplicateCall(g1, g11);
        if ( nb > 1) {
            addWarningDuplicate(reqs, actions[1], t1);
        } else if (nb == 0) {
            addWarningNoCall(reqs, actions[1], t1);
        }
        
        // Modifying TURTLE Modeling
        tm.addTClass(t);
        tm.addSynchroRelation(t, gaction0, t0, g00);
        tm.addSynchroRelation(t, gaction1, t1, g11);
    }
    
    public void manageCriticality(TURTLEModeling tm, Requirements reqs, TClass t, ActivityDiagram ad, String[] actions, int nbActions,  ADComponent actionv, ADComponent tgc2) {
        System.out.println("Managing criticality for observer " + reqs.ro.getName());
        if (reqs.req.getCriticality() == Requirement.LOW) {
            // LOW
            // If action is violated -> generate an error action an reloop on the observer
            System.out.println("Criticality: LOW");
            actionv.addNext(tgc2);
            
        } else if (reqs.req.getCriticality() == Requirement.MEDIUM) {
            // MEDIUM
            // if action is violated -> generate an error action, preempt all objects concerned with observation, and stop
            System.out.println("Criticality: MEDIUM");
            ADStop adstop; 
            ADParallel adpar = new ADParallel();
            Vector tclasses = new Vector();
            Gate g, go;
            TClass to;
            ADActionStateWithGate action;
             
            ad.add(adpar);
            actionv.addNext(adpar);
             
            for(int i=0; i<nbActions; i++) {
                to = tm.getTClassWithAction(actions[i]);
                if (!tclasses.contains(to)) {
                    // add a action to preempt to
                    tclasses.add(to);
                    g = t.addNewGateIfApplicable("preempt__" + i);
                    go = to.addNewGateIfApplicable("preempted__by__" + reqs.ro.getValue());
                    action = new ADActionStateWithGate(g);
                    adstop = new ADStop();
                    
                    ad.add(action);
                    ad.add(adstop);
                    
                    adpar.addNext(action);
                    action.addNext(adstop);
                    
                    to.addTopPreemptOn(go);
                    tm.addSynchroRelation(t, g, to, go);
                }
            }
            
        } else {
            // HIGH
            // if action is violated, preempt all objects
            System.out.println("Criticality: HIGH");
            ADStop adstop; 
            ADParallel adpar = new ADParallel();
            Gate g, go;
            TClass to;
            ADActionStateWithGate action;
             
            ad.add(adpar);
            actionv.addNext(adpar);
            
            for(int i=0; i<tm.classNb(); i++) {
                to = tm.getTClassAtIndex(i);
                if (to != t) {
                    // Add an action to preempt to
                    g = t.addNewGateIfApplicable("preempt__" + i);
                    go = to.addNewGateIfApplicable("preempted__by__" + reqs.ro.getValue());
                    action = new ADActionStateWithGate(g);
                    adstop = new ADStop();
                    
                    ad.add(action);
                    ad.add(adstop);
                    
                    adpar.addNext(action);
                    action.addNext(adstop);
                    
                    to.addTopPreemptOn(go);
                    tm.addSynchroRelation(t, g, to, go);
                }
            }
        }
    }
    
    public void addWarningDuplicate(Requirements reqs, String action, TClass t0) {
        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, reqs.req.getValue() + ", value: " + action + " is called more than one time in" + t0.getName());
        ce.setTDiagramPanel(reqs.req.getTDiagramPanel());
        ce.setTGComponent(reqs.req);
        warnings.add(ce);
    }
    
    public void addWarningNoCall(Requirements reqs, String action, TClass t0) {
        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, reqs.req.getValue() + ", value: " + action + " is never called in" + t0.getName());
        ce.setTDiagramPanel(reqs.req.getTDiagramPanel());
        ce.setTGComponent(reqs.req);
        warnings.add(ce);
    }
    
    public void print(String []actions) {
        for(int i=0; i<actions.length; i++) {
            System.out.println(i + ":" + actions[i]);
        }
    }
    
    public String deduct(String s1, String s2) {
        int i1 = Integer.decode(s1).intValue();
        int i2 = Integer.decode(s2).intValue();
        int i3 = i1 - i2;
        //System.out.println("i3=" + i3 + " i2=" + i2 + " i1=" + i1);
        return Integer.toString(i3);
    }
}