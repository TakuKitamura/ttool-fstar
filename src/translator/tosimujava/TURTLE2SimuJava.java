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
 * Class TURTLE2SimuJava
 * Creation: 19/06/2006
 * @version 1.0 19/06/2006
 * @author Ludovic APVRILLE
 * @see
 */

package translator.tosimujava;

import java.util.*;

//import ddtranslator.*;
import myutil.*;
import translator.*;
import translator.tojava.*;

public class TURTLE2SimuJava {
    
    //private static int gateId;
    //private int idSeq = 0;
    //private int idPar = 0;
    
    private TURTLEModeling tm;
    private LinkedList processes;
    private MasterGateManager mgm;
    private MainClassSimu mainClass;
    //private MainClass mainclass;
    private Vector components;
    
    private boolean debug;
    private boolean longforint;
    
    public static String DECL_CODE_01 = "public class ";
    public static String DECL_CODE_02 = " extends TProcess {";
    public static String DECL_CODE_03 = "public void startSequence() {";
    public static String DECL_CODE_04 = "public void startPreemptionTasks() {";
    public static String OP_SEQ = "startingSequence()";
    public static String OP_PRE = "startPreemptionTasks()";
    public static String OP_DEC = "execute__";
    public static String TH_EXCEPTION = "throws PreemptionException";
    public static String EXIT = "jse.exitRequest();\n";
    public static String ATTR_DEC = "__";
    public static final String JGATE = "Gate";
    
    public static final String MAIN_CLASS = "MainClass";
    
    public static final int NO_PROTOCOL = 0;
    public static final int UDP = 1;
    public static final int TCP = 2;
    public static final int RMI = 3;
    
    public TURTLE2SimuJava(TURTLEModeling _tm) {
        tm = _tm;
        longforint = false;
        components = new Vector();
    }
    
    public void saveJavaClasses(String path) throws FileException {
        ListIterator iterator = processes.listIterator();
        TJavaProcess tjc;
        
        while(iterator.hasNext()) {
            tjc = (TJavaProcess)(iterator.next());
            tjc.saveAsFileIn(path);
        }
        
        saveAsFileInMainClass(path);
    }
    
    public String getMainListFiles(String path) {
        return path + mainClass.getName() + ".java ";
    }
    
    public void setLongSelected(boolean b) {
        longforint = b;
    }
    
    public void printJavaClasses() {
        ListIterator iterator = processes.listIterator();
        TJavaProcess tjp;
        
        while(iterator.hasNext()) {
            tjp = (TJavaProcess)(iterator.next());
            System.out.println(tjp.getJavaName() + ":\n" + tjp.toString() + "\n\n");
        }
        
        printMainClass();    
    }
    
    public void generateSimulationCode(boolean _debug) {
        //int i;
        debug = _debug;
        
        // Preprocessing
        tm.makeRTLOTOSName();
        tm.translateWatchdogs();
        tm.translateInvocationIntoSynchronization();
        tm.translateActionStatesWithMultipleParams();
        mgm = new MasterGateManager(tm, false);
        mgm.sort();
        
        processes = new LinkedList();
        
        // Creating classes & attributes & operations
        generateMainClass();
        generateBasicCodeMainClass();
        //mainclass = new MainClass("MainClass");
        //mainclass.generateBasicCode();
        
        generateJavaClasses1();
        generateJavaClasses2();
        
        // Generate mainclass starting code
        generateJGateCreation();
        generateJGateSynchronisation();
        generateProcessStarting();
        generateOperationCodeMainClass();
        
        
        
        // Post computing
        MasterGateManager.reinitNameRestriction();
        
    }
    
    private void generateJavaClasses1() {
        TClass t;
        int i;
        TJavaProcess tjp;
        
        for(i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            tjp = new TJavaProcess(t.getName(), t.isActive());
            tjp.setDeclarationCode(DECL_CODE_01 + tjp.getJavaName() + DECL_CODE_02);
            processes.add(tjp);
            generateBasicAttributes(t, tjp);
            // Prepare parallels at activity diagram level -> new gates
            //prepareParallelOperators(t, t.getActivityDiagram());
            generateGateAttributes(t, tjp);
            tjp.generateAttributeDeclaration();
            tjp.generateGateDeclaration();
            tjp.generateJTToolAttributes();
            tjp.generateConstructor();
            generateCloneOperation(tjp);
        }
    }
    
    private void generateJavaClasses2() {
        TClass t;
        int i;
        TJavaProcess tjp;
     
        for(i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            tjp = foundTJProcess(t.getName());
            if (tjp != null) {
                //generateExternalPreemption(t, jc);
                //System.out.println("Generate operations for " + jc.getJavaName());
                generateOperations(t, tjp);
                //System.out.println("Done");
            }
        }
    }
    
    /* It does not verify that attributes are not keywords of the Java language */
    public void generateBasicAttributes(TClass t, TJavaProcess tjp) {
        int i;
        Param p;
        JAttribute ja;
        
        Vector params = t.getParamList();
        
        for(i=0; i<params.size(); i++) {
            p = (Param)(params.elementAt(i));
            ja = new JAttribute(p, longforint);
            tjp.addAttribute(ja);
        }
    }
    
    /* It does not verify that gates are not keywords of the Java language */
    public void generateGateAttributes(TClass t, TJavaProcess tjp) {
        int i;
        Gate g;
        JSimuGate jsg;
        
        Vector gates = t.getGateList();
        
        for(i=0; i<gates.size(); i++) {
            g = (Gate)(gates.elementAt(i));
            jsg = new JSimuGate(g.getName(), g.isInternal());
            tjp.addGate(jsg);
        }
    }
    
    /*public void generateExternalPreemption(TClass t, JavaClass jc) {
        int i;
        JavaClass jc1, jc2;
        Relation r;
        TClass t1;
     
        LinkedList tclasses = new LinkedList();
     
        for(i=0; i<tm.relationNb(); i++) {
            r = tm.getRelationAtIndex(i);
            System.out.println("t = " + t.getName() + " Relation=" + r);
            if ((r.type == Relation.PRE) && (r.t1 == t)) {
                //System.out.println("Preemption from t to " + r.t2.getName());
                jc1 = foundJClass(r.t2.getName());
                //System.out.println("jc1 = " + jc1.getJavaName());
                tclasses.add(jc1);
            }
        }
     
        if (tclasses.size() == 0) {
            return;
        }
     
        //System.out.println("Size=" + tclasses.size());
     
        // Create the necessary operation
        jc.addStartingPreemptionCode(JKeyword.INDENT + DECL_CODE_04 + "\n");
     
        // Create all new threads
        jc.addStartingPreemptionCode(generateJGateCreation(tclasses));
        jc.addStartingPreemptionCode(generateTClassCreation(tclasses, false));
     
     
        ListIterator iterator = tclasses.listIterator();
        ListIterator iterator1;
        while(iterator.hasNext())  {
            jc1 = (JavaClass)(iterator.next());
            jc.addStartingPreemptionCode(JKeyword.INDENT + JKeyword.INDENT + jc1.getJavaName().toLowerCase() + ".setToPreempt(this);\n");
            iterator1 = tclasses.listIterator();
            while(iterator1.hasNext())  {
                jc2 = (JavaClass)(iterator1.next());
                if (jc2 != jc1) {
                    jc.addStartingPreemptionCode(JKeyword.INDENT + JKeyword.INDENT + jc1.getJavaName().toLowerCase() + ".setToPreempt(" + jc2.getJavaName().toLowerCase() + ");\n");
                }
            }
     
        }
        jc.addStartingPreemptionCode(generateTClassStarting(tclasses, false, false));
        jc.addStartingPreemptionCode(JKeyword.INDENT + JKeyword.STOP_CODE_N);
     
    }*/
    
    private void generateOperations(TClass t, TJavaProcess tjp) {
        generateStandardOperations(t, tjp);
        tjp.generateAllOperations();
    }
    
    private void generateStandardOperations(TClass t, TJavaProcess tjp) {
        ActivityDiagram ad = t.getActivityDiagram();
        ADComponent adc = ad.getStartState();
        
        JOperation jo = makeNewJOperation(tjp);
        jo.addStandardCode();
        
        generateStandardOperationsRec(t, tjp, ad, adc.getNext(0), adc, jo, 2, true);
    }
    
    private JOperation newAndfinishJO(TJavaProcess tjp, JOperation jo, int dec) {
        finishJO(jo, dec, tjp.getOperationNb());
        return makeNewJOperation(tjp);
    }
    
    private void finishJO(JOperation jo, int dec, int nb) {
        indent(jo, dec);
        jo.addCode("functionId=" + nb + ";\n");
        dec--;
        indent(jo, dec);
        jo.addCode("}\n");
    }
    
     private void basicFinishJO(JOperation jo, int dec) {
        dec--;
        indent(jo, dec);
        jo.addCode("}\n");
    }
    
    private JOperation makeNewJOperation(TJavaProcess tjp) {
        JOperation jo = new JOperation(TURTLE2SimuJava.OP_DEC + (tjp.getOperationNb()));
        jo.nb = tjp.getOperationNb();
        tjp.addOperation(jo);
        jo.addCode(tjp.getDeclarationOfOp(tjp.getOperationNb()));
        if (debug) {
            jo.addCode("System.out.println(getName() + \" / id=\" + id + \"executes :\" + " + jo.nb + ");\n"); 
        }
        return jo;
    }
    
    private void generateStandardOperationsRec(TClass t, TJavaProcess tjp, ActivityDiagram ad, ADComponent adc, ADComponent last, JOperation jo, int dec, boolean endNeeded) {
        System.out.println("Generating std op rec " + jo.getName());
        if (adc instanceof ADActionStateWithGate) {
            ADActionStateWithGate adaswg = (ADActionStateWithGate)adc;
            indent(jo, dec);
            if (debug) {
                jo.addCode("System.out.println(\"Call on " + adaswg.getGate().getName() + " value=" + adaswg.getActionValue() + " from " + tjp.getJavaName() + "\");\n");
            }
            // Call on gate!
            manageCallOnGate(t, tjp, ad, adc, jo,dec, tjp.foundJGate(adaswg.getGate().getName()), adaswg.getActionValue(), null, null, endNeeded);
            // Next component
            //generateStandardOperationsRec(t, jc, adc.getNext(0), jo, dec, endNeeded);
        } else if (adc instanceof ADActionStateWithParam) {
            ADActionStateWithParam adaswp = (ADActionStateWithParam) adc;
            indent(jo, dec);
            //System.out.println("java expr: " + adaswp.brutToString());
            //System.out.println(" Modified java expr: " + makeJavaExpression(adaswp.brutToString()));
            jo.addCode(makeJavaExpression(adaswp.brutToString()) + JKeyword.END_OP_N);
            generateStandardOperationsRec(t, tjp, ad, adc.getNext(0), adc, jo, dec, endNeeded);
        } /*else if (adc instanceof ADChoice) {
            ADChoice adch = (ADChoice) adc;
            makeChoiceCode(t, tjp, ad, adch, jo, dec, endNeeded);
        } */else if (adc instanceof ADDelay) {
            ADDelay add = (ADDelay) adc;
            makeIntervalCode(add.getValue(), add.getValue(), jo, dec);
            jo = newAndfinishJO(tjp, jo, dec);
            generateStandardOperationsRec(t, tjp, ad, add.getNext(0), adc, jo, dec, endNeeded);
        } else if (adc instanceof ADJunction) {
            ADJunction adj = (ADJunction) adc;
            makeJunctionCode(t, tjp, ad, adj, jo, dec, endNeeded);
        } else if (adc instanceof ADLatency) {
            ADLatency adl = (ADLatency) adc;
            makeIntervalCode("0", adl.getValue(), jo, dec);
            jo = newAndfinishJO(tjp, jo, dec);
            generateStandardOperationsRec(t, tjp, ad, adl.getNext(0), adc, jo, dec, endNeeded);
        } else if (adc instanceof ADTimeInterval) {
            ADTimeInterval adti = (ADTimeInterval) adc;
            makeIntervalCode(adti.getMinValue(), adti.getMaxValue(), jo, dec);
            jo = newAndfinishJO(tjp, jo, dec);
            generateStandardOperationsRec(t, tjp, ad, adti.getNext(0), adc, jo, dec, endNeeded);
        } else if (adc instanceof ADTLO) {
            ADTLO adtlo = (ADTLO)(adc);
            indent(jo, dec);
            if (debug)
                jo.addCode("System.out.println(\"Limited call  on " + adtlo.getGate().getName() + " value=" + adtlo.getAction() + " from " + tjp.getJavaName() + "\");\n");
            manageCallOnGate(t, tjp, ad, adc, jo,dec, tjp.foundJGate(adtlo.getGate().getName()), adtlo.getAction(), adtlo.getLatency(), adtlo.getDelay(), endNeeded);
            //generateStandardOperationsRec(t, jc, adtlo.getNext(0), jo, dec, endNeeded);
        } else if (adc instanceof ADStop) {
            makeEndCode(jo, dec, endNeeded);
        }/*else if (adc instanceof ADSequence) {
            makeSequenceCode(t, tjp, ad, (ADSequence)adc, jo, dec, endNeeded);
        } */else if (adc instanceof ADParallel) {
            makeParallelCode(t, tjp, ad, (ADParallel)adc, last, jo, dec, endNeeded);
        } /*else if (adc instanceof ADPreempt) {
            makePreemptionCode(t, tjp, ad, (ADPreempt)adc, last, jo, dec, endNeeded);
        } else {
            System.out.println("Operator not supported: " + adc.toString());
            if (endNeeded) {
                makeEndCode(jo, dec, true);
            }
        }*/
    }
    
    private void indent(JOperation jo, int d) {
        while(d>0) {
            jo.addCode(JKeyword.INDENT);
            d --;
        }
    }
    
    /*private String addThread(String name, int dec) {
        String s = "";
        while(dec>0) {
            s += JKeyword.INDENT;
            dec --;
        }
        s+="internalThreads.add(" + name + ");\n";
        return s;
    }*/
    
    
    /*private void generateRunOperation(JavaClass jc) {
        int i;
        JOperation run0 = jc.getOperationAt(0);
        //run0.addCode(JKeyword.INDENT + JKeyword.INDENT + "try " + JKeyword.START_CODE_N);
        run0.addCode(JKeyword.INDENT + JKeyword.INDENT + "while(" + TURTLE2Java.T__GO + " == true)" + JKeyword.START_CODE_N);
        run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + "switch(" + TURTLE2Java.T__STATE + ")" + JKeyword.START_CODE_N);
     
        for(i=1; i<jc.getOperationNb(); i++) {
            run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT);
            run0.addCode("case " + (i-1) + ":\n");
            run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + "  ");
            run0.addCode(jc.getCallToOp(i));
            run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + "  ");
            run0.addCode("break" + JKeyword.END_OP_N);
        }
     
        run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT);
        run0.addCode("default:\n");
        run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + "  ");
        run0.addCode(TURTLE2Java.T__GO + " = false" + JKeyword.END_OP_N);
     
        run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.INDENT + JKeyword.STOP_CODE_N);
        run0.addCode(JKeyword.INDENT + JKeyword.INDENT + JKeyword.STOP_CODE_N);
        //run0.addCode(JKeyword.INDENT + JKeyword.INDENT + OP_SEQ + JKeyword.END_OP_N);
        //run0.addCode(JKeyword.INDENT + JKeyword.INDENT + "} catch (PreemptionException pe) {}\n");
        run0.addCode(JKeyword.INDENT + JKeyword.STOP_CODE);
    }*/
    
    private void generateJGateCreation() {
        generateJGateCreationMainClass();
        //addGateCodeMainClass(generateJGateCreation(javaClasses));
    }
    
    private String generateJGateCreation(LinkedList toTakeIntoAccountJC) {
        TJavaProcess tjp;
        int j;
        JSimuGate jg;
        String s = "";
        
        ListIterator iterator = processes.listIterator();
        
        while(iterator.hasNext()) {
            tjp = (TJavaProcess)(iterator.next());
            if (toTakeIntoAccountJC.contains(tjp)) {
                for(j=0; j<tjp.getGateNb(); j++) {
                    jg = tjp.getGateAt(j);
                    jg.setJName(tjp.getJavaName() + "__" + jg.getName());
                    s += JKeyword.INDENT + JKeyword.INDENT + "Gate " + jg.getJName() + " = new Gate(\"" + jg.getJName() + "\")" + JKeyword.END_OP + "\n";
                    /*if (jg.hasAProtocol()) {
                        s += JKeyword.INDENT + JKeyword.INDENT + jg.getJName() + ".setProtocol(" + jg.getProtocol() + ")" + JKeyword.END_OP + "\n";
                        s += JKeyword.INDENT + JKeyword.INDENT + jg.getJName() + ".setLocalPort(" + jg.getLocalPort() + ")" + JKeyword.END_OP + "\n";
                        s += JKeyword.INDENT + JKeyword.INDENT + jg.getJName() + ".setDestPort(" + jg.getDestPort() + ")" + JKeyword.END_OP + "\n";
                        s += JKeyword.INDENT + JKeyword.INDENT + jg.getJName() + ".setDestHost(\"" + jg.getDestHost() + "\")" + JKeyword.END_OP + "\n";
                        s += JKeyword.INDENT + JKeyword.INDENT + jg.getJName() + ".setLocalHost(\"" + jg.getLocalHost() + "\")" + JKeyword.END_OP + "\n";
                    }*/
                }
            }
        }
        
        return s;
    }
    
    private void generateJGateSynchronisation() {
        generateJGateSynchronisationMainClass();
        //mainclass.addSynchroCode(generateJGateSynchronisation(javaClasses));
    }
    
    private String generateJGateSynchronisation(LinkedList toTakeIntoAccountJC) {
        // Assume that all invocation operations have been removed
        //TClass t;
        Relation r;
        int i, j;
        TJavaProcess tjp1, tjp2;
        JSimuGate jg1, jg2;
        //Gate g;
        String s = "";
        //MainClassSimu tmpc;
        
        if (toTakeIntoAccountJC.size() == 0) {
            return "";
        }
        
        //tmpc=mainClass;
        
        for(i=0; i<tm.relationNb(); i++) {
            r = tm.getRelationAtIndex(i);
            if (r.type == Relation.SYN) {
                tjp1 = foundTJProcess(r.t1.getName());
                tjp2 = foundTJProcess(r.t2.getName());
                if ((tjp1 != null) && (tjp2 != null)) {
                    if (toTakeIntoAccountJC.contains(tjp1) && toTakeIntoAccountJC.contains(tjp2)) {
                        for(j=0; j<r.gatesOfT1.size(); j++) {
                            System.out.println("Gates 1)" + ((Gate)(r.gatesOfT1.elementAt(j))).getName() + " 2:" + ((Gate)(r.gatesOfT2.elementAt(j))).getName());
                            jg1 = tjp1.foundJGate(((Gate)(r.gatesOfT1.elementAt(j))).getName());
                            jg2 = tjp2.foundJGate(((Gate)(r.gatesOfT2.elementAt(j))).getName());
                            //System.out.println("foundJGate");
                            if ((jg1 != null) && (jg2 != null)) {
                                s += JKeyword.INDENT + JKeyword.INDENT + jg1.getJName() + ".synchroGate = " + jg2.getJName() + JKeyword.END_OP + "\n";
                                s += JKeyword.INDENT + JKeyword.INDENT + jg2.getJName() + ".synchroGate = " + jg1.getJName() + JKeyword.END_OP + "\n";
                            }
                        }
                    }
                }
            }
        }
        
        // Gates of parallel operators in activity diagrams
        //Vector v;
        //String name, nameSearched;*/
        
        //tmpc.addSynchroCode(JKeyword.INDENT + JKeyword.INDENT + "/* Parallel operators of activity diagrams */\n");
        /*ListIterator iterator = javaClasses.listIterator();
        JavaClass jc;
         
        while(iterator.hasNext()) {
            jc1 = (JavaClass)(iterator.next());
            if (toTakeIntoAccountJC.contains(jc1)) {
                for(i=0; i<jc1.getGateNb(); i++) {
                    jg1 = jc1.getGateAt(i);
                    name = jg1.getName();
                    if (name.startsWith("par__master")) {
                        nameSearched = "par__" + name.substring(11, name.length());
                        jg2 = jc1.foundJGate(nameSearched);
                        if (jg2 != null) {
                            id = getMainClassOf(jc1).getUniqueGateId();
                            s += JKeyword.INDENT + JKeyword.INDENT + "JMasterGate " + "mgate__" + id + " = new JMasterGate()" + JKeyword.END_OP + "\n";
                            s += JKeyword.INDENT + JKeyword.INDENT + jg1.getJName() + ".setMasterGate(" + "mgate__" + id + ")" + JKeyword.END_OP + "\n";
                            s += JKeyword.INDENT + JKeyword.INDENT + jg1.getJName() + ".setLeft()" + JKeyword.END_OP + "\n";
                            s += JKeyword.INDENT + JKeyword.INDENT + jg2.getJName() + ".setMasterGate(" + "mgate__" + id + ")" + JKeyword.END_OP + "\n";
                            s += JKeyword.INDENT + JKeyword.INDENT + jg2.getJName() + ".setRight()" + JKeyword.END_OP + "\n";
                        }
                    }
                }
            }
        }*/
        
        return s;
    }
    
    /*private String generateExternalSequence(LinkedList toTakeIntoAccount, boolean onlyActiveClasses) {
        JavaClass jc;
        String s = "";
        LinkedList ll;
        LinkedList one;
     
        s += "\n";
        // Sequence code
        s+= "\n";
     
     
        ListIterator iterator = javaClasses.listIterator();
     
        while(iterator.hasNext()) {
            jc = (JavaClass)(iterator.next());
            if (toTakeIntoAccount.contains(jc)) {
                ll = listClassesStartingAt(jc.getJavaName());
                System.out.println("Getting list for" + jc.getJavaName());
                if ((ll != null) && (ll.size() > 0)) {
                    jc.addStartingSequenceCode(JKeyword.INDENT + DECL_CODE_03 + "\n");
                    jc.addStartingSequenceCode(generateJGateCreation(ll));
                    jc.addStartingSequenceCode(generateJGateSynchronisation(ll));
                    jc.addStartingSequenceCode(generateTClassCreation(ll, false));
                    jc.addStartingSequenceCode(generateCodeStartingSeq(ll));
                    jc.addStartingSequenceCode(generateTClassStarting(ll, false, false));
                    jc.addStartingSequenceCode(JKeyword.INDENT + JKeyword.STOP_CODE_N);
                    if ((!onlyActiveClasses) || (onlyActiveClasses && jc.isActive())) {
                        one = new LinkedList();
                        one.add(jc);
                        s+= generateCodeStartingSeq(one);//JKeyword.INDENT + JKeyword.INDENT + jc.getJavaName().toLowerCase() + ".setStartingSequence(true)" + JKeyword.END_OP + "\n";
                    }
                }
            }
        }
        return s;
    }*/
    
    /*private String generateCodeStartingSeq(LinkedList ll) {
        String s = "";
        JavaClass jc;
        ListIterator iterator = ll.listIterator();
     
        while(iterator.hasNext()) {
            jc = (JavaClass)(iterator.next());
            s+= JKeyword.INDENT + JKeyword.INDENT + jc.getJavaName().toLowerCase() + ".setStartingSequence(true)" + JKeyword.END_OP + "\n";
        }
        return s;
    }*/
    
    /*private LinkedList listClassesStartingAt(String name) {
        TClass t1, t2;
        Relation r;
        JavaClass jc;
        LinkedList ll = new LinkedList();
        for(int i=0; i<tm.relationNb(); i++) {
            r = tm.getRelationAtIndex(i);
            if (r.type == Relation.SEQ) {
                System.out.println("Found one seq relation");
                if(r.t1.getName().equals(name)) {
                    jc = foundJClass(r.t2.getName());
                    if (jc != null) {
                        ll.add(jc);
                    }
                }
            }
        }
        return ll;
    }*/
    
    private void generateProcessStarting() {
        generateProcessStartingMainClass();
    }
    
    private String generateProcessCreation(LinkedList toTakeIntoAccount, boolean onlyActiveClasses) {
        TJavaProcess tjc;
        String s = "";
        
        ListIterator iterator = processes.listIterator();
        
        while(iterator.hasNext()) {
            tjc = (TJavaProcess)(iterator.next());
            if (toTakeIntoAccount.contains(tjc)) {
                if ((!onlyActiveClasses) || (onlyActiveClasses && tjc.isActive())) {
                    s += tjc.getCreationCode(tjc.getJavaName().toLowerCase()) + "\n";
                }
            }
        }
        
        return s;
    }
    
    
    /*private String generateProcessStarting(LinkedList toTakeIntoAccount, boolean onlyActiveClasses, boolean internal) {
        JavaClass jc;
        String s = "";
        ListIterator iterator = javaClasses.listIterator();
        while(iterator.hasNext()) {
            jc = (JavaClass)(iterator.next());
            if (toTakeIntoAccount.contains(jc)) {
                if ((!onlyActiveClasses) || (onlyActiveClasses && jc.isActive())) {
                    if (internal) {
                        s += addThread(jc.getJavaName().toLowerCase(), 2);
                    }
                    s += JKeyword.INDENT + JKeyword.INDENT + jc.getJavaName().toLowerCase() + ".start()" + JKeyword.END_OP + "\n";
                }
            }
        }
     
        return s;
    }*/
    
    
    private TJavaProcess foundTJProcess(String name) {
        TJavaProcess tjp;
        ListIterator iterator = processes.listIterator();
        
        while(iterator.hasNext()) {
            tjp = (TJavaProcess)(iterator.next());
            if (tjp.getTURTLEName().equals(name)) {
                return tjp;
            }
        }
        
        return null;
    }
    
    private void makeSynchroSchemes(TClass t, JOperation jo, int dec, JSimuGate jg, String value) {
        int i;
        boolean sending;
        boolean nat;
     
        indent(jo, dec);
        jo.addCode("//Synchro on " + jg.getName() + " with action = " + value + " \n");
     
        
        indent(jo, dec);
        jo.addCode("sizeSynchros = " + tm.getNbOfSynchroItems(value) + ";\n");
     
        // Pre syncro
        for(i=0; i<tm.getNbOfSynchroItems(value); i++) {
            // Sending
            indent(jo, dec);
            sending = tm.isSendingSynchro(value, i);
            if (sending) {
                jo.addCode("synchros[" + i + "].sending = true;\n");
            } else {
                jo.addCode("synchros[" + i + "].sending = false;\n");
            }
     
            // Type and value
            indent(jo, dec);
     
            nat = tm.isNaturalSynchro(t, value, i);
            if (nat) {
                jo.addCode("synchros[" + i + "].type = 0;\n");
            } else {
                jo.addCode("synchros[" + i + "].type = 1;\n");
            }
     
            if (sending) {
                indent(jo, dec);
                if (nat) {
                    jo.addCode("synchros[" + i + "].valueNat = (int)(" + tm.getSynchroValueAt(value, i) + ");\n");
                } else {
                    jo.addCode("synchros[" + i + "].valueBool = " + tm.getSynchroValueAt(value, i) + ";\n");
                }
            }
        }
    }
    
    private void manageCallOnGate(TClass t, TJavaProcess tjp, ActivityDiagram ad, ADComponent adc, JOperation jo, int dec, JSimuGate jg, String value, String latency, String delay, boolean endNeeded) {
     
        if (adc.getPreJavaCode() != null) {
            addUserCode(jo, dec, adc.getPreJavaCode());
        }
     
     
        if ((delay != null) && (delay.length() == 0)) {
            delay = null;
        }
     
        if ((latency != null) && (latency.length() == 0)) {
            latency = null;
        }
        
        if ((delay != null) && (latency == null)) {
            latency = "0";
        }
     
        
        if ((delay != null) && (latency != null)) {
           JOperation jo1 = makeNewJOperation(tjp);
           makePostSynchro(t, tjp, ad, adc, jo1, dec, jg, value, endNeeded);
           generateStandardOperationsRec(t, tjp, ad, adc.getNext(0), adc, jo1, dec, endNeeded);
           JOperation jo2 = makeNewJOperation(tjp);
           generateStandardOperationsRec(t, tjp, ad, adc.getNext(1), adc, jo2, dec, endNeeded);
           makeSynchroSchemes(t, jo, dec, jg, value);
           indent(jo, dec);
           jo.addCode("jse.synchroTimeLimitedRequest(" + jg.getName() + ", " + latency + ", " + delay + " ," + jo1.nb + "," + jo2.nb + ");\n");       
           finishJO(jo, dec, jo1.nb);
        } else {
           makeSynchroSchemes(t, jo, dec, jg, value);
           indent(jo, dec);
           jo.addCode("jse.synchroRequest(" + jg.getName() + ");\n");
           jo = newAndfinishJO(tjp, jo, dec);
           makePostSynchro(t, tjp, ad, adc, jo, dec, jg, value, endNeeded);
           generateStandardOperationsRec(t, tjp, ad, adc.getNext(0), adc, jo, dec, endNeeded);
        }
     
     
        /*// Removing all elements from the Vector
        makeSynchroSchemes(t, jo, dec, jg, value);
     
     
        // Synchro
     
        jo.addCode("\n");
        //indent(jo, dec);
     
        if (delay != null) {
            indent(jo, dec);
            jo.addCode("__sss.maxDelay = " + delay + ";\n");
        }
     
        indent(jo, dec);
        jo.addCode("__sss.jgate = " + jg.getName() + ";\n");
        indent(jo,dec);
        jo.addCode("__ssss = new SynchroSchemes[1];\n");
        indent(jo,dec);
        jo.addCode("__ssss[0] = __sss;\n");
        indent(jo,dec);
        jo.addCode("__sss = MasterSynchro.master.synchro(__ssss, this);\n");
     
        indent(jo, dec);
        if (debug)
            jo.addCode("System.out.println(\"Call terminated for" + jc.getJavaName() + "\");\n");
     
        if (delay != null) {
            indent(jo, dec);
            jo.addCode("if (__sss == null) {\n");
            dec ++;
     
            if (adc.getNext(1) == null) {
                makeEndCode(jo, dec, endNeeded);
            } else {
                generateStandardOperationsRec(t, tjp, ad, adc.getNext(1), adc, jo, dec, false);
            }
     
            dec --;
            indent(jo, dec);
            jo.addCode("} else {\n");
            dec ++;
            makePostSynchro(t, tjp, ad, adc, jo, dec, jg, value, false);
            dec --;
            indent(jo, dec);
            jo.addCode("}\n");
     
            if (endNeeded) {
                dec --;
                indent(jo, dec);
                jo.addCode("}\n");
            }
        } else {
            makePostSynchro(t, tjp, ad, adc, jo, dec, jg, value, endNeeded);
        }*/
    }
    
    private void makePostSynchro(TClass t, TJavaProcess tjp, ActivityDiagram ad, ADComponent adc, JOperation jo, int dec, JSimuGate jg, String value, boolean endNeeded) {
        int i;
        boolean sending, nat;
     
        // Post synchro
        for(i=0; i<tm.getNbOfSynchroItems(value); i++) {
            sending = tm.isSendingSynchro(value, i);
            if (!sending) {
                nat = tm.isNaturalSynchro(t, value, i);
                indent(jo, dec);
                if (nat) {
                    jo.addCode(tm.getShortSynchroValueAt(value, i) + " = synchros[" + i + "].valueNat;\n");
                } else {
                    jo.addCode(tm.getShortSynchroValueAt(value, i) + " = synchros[" + i + "].valueBool;\n");
                }
            }
        }
     
        jo.addCode("\n");
     
        if (adc.getPostJavaCode() != null) {
            addUserCode(jo, dec, adc.getPostJavaCode());
        }
     
        //generateStandardOperationsRec(t, tjp, ad, adc.getNext(0), adc, jo, dec, endNeeded);
    }
    
    public void addUserCode(JOperation jo, int dec, String code) {
        jo.addNonFormattedCode(dec, "/* User Code */\n" + code + "\n/* End of User Code */\n");
    }
    
    public void makeEndCode(JOperation jo, int dec, boolean endNeeded) {
        indent(jo, dec);
        //jo.addCode(TURTLE2Java.T__GO + " = false" + JKeyword.END_OP_N);
        if (endNeeded) {
            indent(jo, dec);
            jo.addCode(TURTLE2SimuJava.EXIT);
            dec --;
            indent(jo, dec);
            jo.addCode("}");
        }
    }
    
    /*public void makeChoiceCode(TClass t, JavaClass jc, ActivityDiagram ad, ADChoice adch, JOperation jo, int dec, boolean endNeeded) {
        int nbG = adch.getNbGuard();
        int i;
        String guard;
        JGate jg;
        ADActionStateWithGate adag;
        long delay;
        String delays;
     
        if (nbG < 1) {
            makeEndCode(jo, dec, endNeeded);
            return;
        }
     
        // Commentary
        indent(jo, dec);
        jo.addCode("//Managing choice\n");
     
        // building array
        indent(jo, dec);
        jo.addCode("__bchoice = new boolean[" + nbG + "];\n");
     
        for(i=0; i<nbG; i++) {
            indent(jo, dec);
            if (!(adch.isGuarded(i))) {
                jo.addCode("__bchoice[" + i + "] = true;\n");
            } else {
                guard = adch.getGuard(i);
                if (guard == null) {
                    jo.addCode("__bchoice[" + i + "] = false;\n");
                } else {
                    guard = makeJavaGuard(guard);
                    jo.addCode("__bchoice[" + i + "] = " + guard + ";\n");
                }
            }
        }
     
     
        if (adch.isSpecialChoice()) {
            indent(jo, dec);
            jo.addCode("__bchoice__synchro = new SynchroSchemes[" + nbG + "];\n");
     
     
            for(i=0; i<nbG; i++) {
                adag = adch.getADActionStateWithGate(i);
                jg = jc.foundJGate(adag.getGate().getName());
                if (adag.getPreJavaCode() != null) {
                    addUserCode(jo, dec, adag.getPreJavaCode());
                }
     
                makeSynchroSchemes(t, jo, dec, jg, adag.getActionValue());
     
                indent(jo, dec);
                jo.addCode("__sss.jgate = " +  jg.getName() + ";\n");
                indent(jo, dec);
                delays = adch.getMinDelay(i);
     
                jo.addCode("__sss.minDelay =" + delays + ";\n");
                indent(jo, dec);
                delays = adch.getMaxDelay(i);
     
                jo.addCode("__sss.maxDelay =" + delays + ";\n");
     
                indent(jo, dec);
                jo.addCode("__bchoice__synchro[" + i + "] = __sss;\n");
     
            }
     
            // new scheme
            indent(jo, dec);
            jo.addCode("//Special choice \n\n");
     
            indent(jo, dec);
            jo.addCode("__nchoice = LibLogicalOperator.nbOfTrue(__bchoice);\n");
     
            if (debug) {
                indent(jo, dec);
                jo.addCode("System.out.println(\" __nchoice=\" + __nchoice + \" \");\n");
            }
            indent(jo,dec);
            jo.addCode("__ssss = new SynchroSchemes[__nchoice];\n");
     
            indent(jo, dec);
            jo.addCode("__cpt1 = 0; __cpt2 = 0;\n");
            indent(jo, dec);
            jo.addCode("while(__cpt2 < __nchoice) {\n");
            dec ++;
            indent(jo, dec);
            jo.addCode("if (__bchoice[__cpt1] == true) {\n");
            dec ++;
     
            indent(jo, dec);
            jo.addCode("__ssss[__cpt2] =  __bchoice__synchro[__cpt1];\n");
            indent(jo, dec);
            jo.addCode("__cpt2++;\n");
     
            dec --;
            indent(jo, dec);
            jo.addCode("}\n");
            indent(jo, dec);
            jo.addCode("__cpt1 ++;\n");
     
            dec --;
            indent(jo, dec);
            jo.addCode("}\n");
     
            indent(jo,dec);
            jo.addCode("__sss = MasterSynchro.master.synchro(__ssss, this);\n\n");
     
            // After choice has been made
            // Determine the nchoice number ..
     
            indent(jo,dec);
            jo.addCode("__cpt1 = 0;\n");
            indent(jo,dec);
            jo.addCode("while(__cpt1 < __nchoice) {\n");
            dec ++;
            indent(jo,dec);
            jo.addCode("if (__bchoice__synchro[__cpt1] == __sss) break;\n");
            indent(jo,dec);
            jo.addCode("__cpt1 ++;\n");
            dec--;
            indent(jo,dec);
            jo.addCode("}\n\n");
     
            // switching a path
            indent(jo, dec);
            jo.addCode("switch(__cpt1) {\n");
     
            dec ++;
            for(i=0; i<nbG; i++) {
                indent(jo, dec);
                jo.addCode("case " + i + ":\n");
                dec ++;
     
                if (debug) {
                    indent(jo, dec);
                    jo.addCode("System.out.println(\"Going to branch " + i + "\");\n");
                }
                // Get values from synchro
                adag = adch.getADActionStateWithGate(i);
                jg = jc.foundJGate(adag.getGate().getName());
     
                makePostSynchro(t, jc, ad, adag, jo, dec, jg, adag.getActionValue(), false);
     
                //generateStandardOperationsRec(t, jc, ad, adch.getADActionStateWithGate(i).getNext(0), adch.getADActionStateWithGate(i), jo, dec, false);
                indent(jo, dec);
                jo.addCode("break;\n");
                dec --;
            }
            dec --;
            indent(jo, dec);
            jo.addCode("}\n");
     
     
            if (endNeeded) {
                dec --;
                indent(jo, dec);
                jo.addCode("}\n");
            }
     
        } else {
            // old scheme
     
     
            // Deciding of a path
            indent(jo, dec);
            jo.addCode("__nchoice = LibLogicalOperator.makeChoice(__bchoice);\n");
            indent(jo, dec);
            if (debug)
                jo.addCode("System.out.println(\" __nchoice=\" + __nchoice + \" \");\n");
     
            // switching a path
            indent(jo, dec);
            jo.addCode("switch(__nchoice) {\n");
     
            dec ++;
            for(i=0; i<nbG; i++) {
                indent(jo, dec);
                jo.addCode("case " + i + ":\n");
                dec ++;
                indent(jo, dec);
                if (debug)
                    jo.addCode("System.out.println(\"Guard #" + i + "\");\n");
                generateStandardOperationsRec(t, jc, ad, adch.getNext(i), adch, jo, dec, false);
                indent(jo, dec);
                jo.addCode("break;\n");
                dec --;
            }
     
            dec --;
            indent(jo, dec);
            jo.addCode("}\n\n");
     
            if (endNeeded) {
                dec --;
                indent(jo, dec);
                jo.addCode("}\n");
            }
        }
    }*/
    
    public void makeIntervalCode(String minExpr, String maxExpr, JOperation jo, int dec) {
        minExpr = makeJavaExpression(minExpr);
        maxExpr = makeJavaExpression(maxExpr);
     
        indent(jo, dec);
        jo.addCode("\n");
        indent(jo, dec);
        jo.addCode("jse.addDelay(" + minExpr + ", " + maxExpr + ");\n");
    }
    
    /*public void makeLatencyCode(String expr, JOperation jo, int dec) {
        expr = makeJavaExpression(expr);
     
        indent(jo, dec);
        jo.addCode("\n");
        indent(jo, dec);
        jo.addCode("//Non deterministic delay of " + expr + "\n");
        indent(jo, dec);
        //jo.addCode("LibTemporalOperator.waitForAtMost( (" + expr + ") * " + millis + ", (" + expr + ") *" + nanos + ");\n");
    }*/
    
    public void makeJunctionCode(TClass t, TJavaProcess tjp, ActivityDiagram ad, ADJunction adj, JOperation jo, int dec, boolean endNeeded) {
        int numOp;
        JOperation jo1;
        boolean makeOp = false;
        ComponentId cid;
     
        cid = getComponentId(adj);
     
        if (cid != null) {
            numOp = cid.id;
            jo1 = tjp.getOperationAt(numOp-1);
        } else {
            jo1 = makeNewJOperation(tjp);
            jo1.addStandardCode();
            numOp = tjp.getOperationNb() - 1;
            cid = new ComponentId(adj, numOp);
            components.add(cid);
            makeOp = true;
        }
        
        if (endNeeded) {
            indent(jo, dec);
            jo.addCode(TURTLE2SimuJava.OP_DEC + numOp + "();\n");
            dec --;
            indent(jo, dec);            
            jo.addCode("}\n");
        }
     
        if (makeOp) {
            generateStandardOperationsRec(t, tjp, ad, adj.getNext(0), adj, jo1, 2, true);
        }
    }
    
    /*public void makeSequenceCode(TClass t, JavaClass jc, ActivityDiagram ad, ADSequence adseq, JOperation jo, int dec, boolean endNeeded) {
        // One thread is created to execute each sequenced operation
        JOperation jo1;
        String name;
        int i;
     
        if (adseq.getNbNext() > 1) {
            indent(jo, dec);
            jo.addCode("\n");
            indent(jo, dec);
            jo.addCode("// Sequence operator \n");
            indent(jo, dec);
            jo.addCode("\n");
            for(i=0; i<adseq.getNbNext()-1; i++) {
                jo1 = makeNewJOperation(jc);
                jo1.addStandardCode();
                generateStandardOperationsRec(t, jc, ad, adseq.getNext(i), adseq, jo1, dec, true);
                //indent(jo, dec);
                name = jc.getJavaName().toLowerCase() + "__" + idSeq;
                jo.addCode(jc.getCreationCodeWithSpecialStateCurrentValues(name, jc.getOperationNb() - 2, dec) + "\n");
                jo.addCode(addThread(name, dec));
                indent(jo, dec);
                jo.addCode(name + ".start()" + JKeyword.END_OP + "\n");
                indent(jo, dec);
                jo.addCode("try {\n");
                dec ++;
                indent(jo, dec);
                jo.addCode(name+ ".join();\n");
                dec --;
                indent(jo, dec);
                jo.addCode("} catch (InterruptedException ie) {System.out.println(\"Interrupted\");}\n");
                idSeq ++;
                indent(jo, dec);
                jo.addCode("\n");
            }
            generateStandardOperationsRec(t, jc, ad, adseq.getNext(adseq.getNbNext()-1), adseq, jo, dec, endNeeded);
        } else {
            generateStandardOperationsRec(t, jc, ad, adseq.getNext(0), adseq, jo, dec, endNeeded);
        }
    }*/
    
    public void makeParallelCode(TClass t, TJavaProcess tjp, ActivityDiagram ad, ADParallel adpar, ADComponent last, JOperation jo, int dec, boolean endNeeded) {
        indent(jo, dec);
        jo.addCode("\n");
        indent(jo, dec);
        jo.addCode("// Parallel operator \n");
     
        int i;
        //ComponentId cid = getComponentId(adpar);
     
        /*if (ad.getNbComponentLeadingTo(adpar) > 1) {
            // The first one must synchronize with all the others
            if (ad.getFirstComponentLeadingTo(adpar) == last) {
                for(i=1; i<ad.getNbComponentLeadingTo(adpar); i++) {
                    indent(jo, dec);
                    jo.addCode("\n");
                    indent(jo, dec);
                    jo.addCode("__sss = new SynchroSchemes();\n");
                    indent(jo, dec);
                    jo.addCode("__sss = par__master" + cid.id + "__" + i + ".synchro(__sss, this);\n");
                }
            } else {
                // The others synchronize with the first one and exit
                int id = ad.getIndexOfComponentLeadingTo(adpar, last);
                indent(jo, dec);
                jo.addCode("__sss = new SynchroSchemes();\n");
                indent(jo, dec);
                jo.addCode("__sss = par__" + cid.id + "__" + id + ".synchro(__sss, this);\n");
                makeEndCode(jo, dec, endNeeded);
                return;
            }
     
        }*/
     
        JOperation jo1 = null;
        //String name;
        int idop;
     
        if (adpar.getNbNext() > 1) {
            // We assume that there is no synchronization
            boolean b = true;//adpar.isAValidMotif(t);
            System.out.println("Nb of gates = " + adpar.nbGate() + " valueGate=" + adpar.getValueGate());
            if ((adpar.nbGate() == 0) ||(adpar.getNbNext() > 2) || (!b)) {
                indent(jo, dec);
                jo.addCode("int []tab = new int[" + adpar.getNbNext() + "];\n");
                for(i=0; i<adpar.getNbNext(); i++) {
                    jo1 = makeNewJOperation(tjp);
                    jo1.addStandardCode();
                    idop = tjp.getOperationNb() - 1;
                    indent(jo, dec);
                    jo.addCode("tab[" + i + "] = " + idop + ";\n");
                    //System.out.println("Parallel with next = " + adpar.getNext(i).toString());
                    generateStandardOperationsRec(t, tjp, ad, adpar.getNext(i), adpar, jo1, 2, true);
                }
                indent(jo, dec);
                jo.addCode("jse.addParallelProcess(tab);\n");       
                basicFinishJO(jo, dec);
            } else {
                System.out.println("Parallel - synchro");
                // Synchronization
                // adpar : nb next == 2
                // Creation of new synchronization gates
                /*Vector v = adpar.getGateList();
                Gate g;
                JGate jg1;
                JGate jg2;
                int id = 0;
     
                for(i=0; i<v.size(); i++) {
                    g = (Gate)(v.elementAt(i));
                    jg1 = jc.foundJGate(g.getName());
                    if (jg1 != null) {
                        id = getMainClassOf(jc).getUniqueGateId();
                        indent(jo, dec);
                        jo.addCode(jg1.getJName() +  "= new JGate(\"" + jg1.getName()+ "\");\n");
                        indent(jo, dec);
                        jo.addCode("JMasterGate " + "mgate__" + id + " = new JMasterGate()" + JKeyword.END_OP + "\n");
                        indent(jo, dec);
                        jo.addCode(jg1.getJName() + ".setMasterGate(" + "mgate__" + id + ")" + JKeyword.END_OP + "\n");
                        indent(jo, dec);
                        jo.addCode(jg1.getJName() + ".setLeft()" + JKeyword.END_OP + "\n");
                    }
                }
     
                // Preparing second process
                jo1 = makeNewJOperation(jc);
                jo1.addStandardCode();
                idop = jc.getOperationNb() - 2;
                generateStandardOperationsRec(t, jc, ad, adpar.getNext(1), adpar, jo1, dec, true);
                //indent(jo, dec);
                name = jc.getJavaName().toLowerCase() + "__" + idPar;
                jo.addCode(jc.getCreationCodeWithSpecialStateCurrentValues(name, jc.getOperationNb() - 2, dec) + "\n");
     
                // First process
                int idp = id - v.size() + 1;
                for(i=0; i<v.size(); i++) {
                    g = (Gate)(v.elementAt(i));
                    jg1 = jc.foundJGate(g.getName());
                    if (jg1 != null) {
                        indent(jo, dec);
                        jo.addCode(jg1.getJName() +  "= new JGate(\"" + jg1.getName()+ "\");\n");
                        indent(jo, dec);
                        jo.addCode(jg1.getJName() + ".setMasterGate(" + "mgate__" + (idp  + i) + ")" + JKeyword.END_OP + "\n");
                        indent(jo, dec);
                        jo.addCode(jg1.getJName() + ".setRight()" + JKeyword.END_OP + "\n");
                    }
                }
     
                // Starting second process
     
     
                jo.addCode(addThread(name, dec));
                indent(jo, dec);
                jo.addCode(name + ".setState(" + idop +")" + JKeyword.END_OP + "\n");
                indent(jo, dec);
                jo.addCode(name + ".start()" + JKeyword.END_OP + "\n");
                idPar ++;
                indent(jo, dec);
                jo.addCode("\n");
     
     
                // Starting of one new process
                generateStandardOperationsRec(t, jc, ad, adpar.getNext(0), adpar, jo, dec, endNeeded);
                 **/
            }
        } else {
            generateStandardOperationsRec(t, tjp, ad, adpar.getNext(0), adpar, jo, dec, endNeeded);
        }
     
    }
    
    /*public void makePreemptionCode(TClass t, JavaClass jc, ActivityDiagram ad, ADPreempt adpre, ADComponent last, JOperation jo, int dec, boolean endNeeded) {
        indent(jo, dec);
        jo.addCode("\n");
        indent(jo, dec);
        jo.addCode("// Preemption operator \n");
     
        int i;
        if (adpre.getNbNext() < 2) {
            // no real preemption
            generateStandardOperationsRec(t, jc, ad, adpre.getNext(0), adpre, jo, dec, endNeeded);
            return;
        }
     
        JOperation jo1;
        String name = "";
        int idop;
        // One thread is started per outside next
        for(i=0; i<adpre.getNbNext(); i++) {
            jo1 = makeNewJOperation(jc);
            jo1.addStandardCode();
            idop = jc.getOperationNb() - 2;
            generateStandardOperationsRec(t, jc, ad, adpre.getNext(i), adpre, jo1, 2, true);
            name = jc.getJavaName().toLowerCase() + "__" + idPar;
            jo.addCode(jc.getCreationCodeWithSpecialStateCurrentValues(name, idop, dec) + "\n");
     
            if (i>0) {
                indent(jo, dec);
                jo.addCode(name + ".setToPreempt(" + jc.getJavaName().toLowerCase() + "__" + (idPar - 1) + ");\n");
            }
            jo.addCode(addThread(name, dec));
            indent(jo, dec);
            jo.addCode(name + ".start()" + JKeyword.END_OP + "\n");
            idPar ++;
            indent(jo, dec);
            jo.addCode("\n");
        }
        makeEndCode(jo, dec, true);
     
    }*/
    
    public String makeJavaGuard(String guard) {
        guard = guard.replace('[', '(');
        guard = guard.replace(']', ')');
        
        return makeJavaExpression(guard);
    }
    
    public String makeJavaExpression(String guard) {
        // min, max, div, mod, divs, not, or, and
        // div mod divs are not currently supported
        
        // min -> Math.min
        guard = Conversion.replaceOp(guard, "min", "Math.min");
        
        // max -> Math.max
        guard = Conversion.replaceOp(guard, "max", "Math.max");
        
        // not -> !
        guard = Conversion.replaceOp(guard, "not", "!");
        
        // or -> ||
        guard = Conversion.replaceOp(guard, "or", "||");
        
        // and -> &&
        guard = Conversion.replaceOp(guard, "and", "&&");
        
        return guard;
    }
    
    private ComponentId getComponentId(ADComponent adc) {
        ComponentId cid;
        
        for(int i=0; i<components.size(); i++) {
            cid = (ComponentId)(components.elementAt(i));
            if (cid.adc == adc) {
                return cid;
            }
        }
        
        return null;
    }
    
    /*private void prepareParallelOperators(TClass t, ActivityDiagram ad) {
        ADComponent adc;
        int idPar = 0;
        ComponentId cid;
        int i, j;
        Gate g;
     
        for(i=0; i<ad.size(); i++) {
            adc = (ADComponent)(ad.elementAt(i));
            if (adc instanceof ADParallel) {
                cid = new ComponentId(adc, idPar);
     
                for(j=1; j<ad.getNbComponentLeadingTo(adc); j++) {
                    t.addNewGateIfApplicable("par__" + idPar + "__" + j);
                    t.addNewGateIfApplicable("par__master" + idPar + "__" + j);
                }
     
                components.add(cid);
     
                idPar ++;
            }
        }
    }*/
    
    private void generateMainClass() {
        mainClass = new MainClassSimu(MAIN_CLASS);
        System.out.println("Adding mainclass :" + MAIN_CLASS);
    }
    
    private void generateBasicCodeMainClass() {
        mainClass.generateBasicCode();
    }
    
    private void generateOperationCodeMainClass() {
        mainClass.generateOperationCode();
    }
    
    private void saveAsFileInMainClass(String path) throws FileException {
        mainClass.saveAsFileIn(path);
    }
    
    
    private void generateJGateCreationMainClass() {
        mainClass.addGateCode(generateJGateCreation(processes));
    }
    
    private void generateJGateSynchronisationMainClass() {
        mainClass.addSynchroCode(generateJGateSynchronisation(processes));
    }
    
    private void generateProcessStartingMainClass() {
        mainClass.addStartingCode(generateProcessCreation(processes, true));
    }
    
    private void generateCloneOperation(TJavaProcess tjp) {
        String code = "\n\n    public TProcess makeClone() {\n";
        code += tjp.getCloneCodeWithSpecialState("__tp", tjp.getJavaName(), 2);
        code +="        setClone(__tp);\n";     
        code +="        return __tp;\n";       
        code +="    }\n\n";
        tjp.addOperationCode(code);
    }
    
    
    
    private void printMainClass() {
        System.out.println(mainClass.getName() + ":\n" + mainClass.toString() + "\n\n");
    }
    
}