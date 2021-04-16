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




package tmltranslator.touppaal;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import tmltranslator.*;
import uppaaldesc.*;

import java.util.Iterator;
import java.util.Vector;

import common.SpecConfigTTool;


/**
 * Class TML2UPPAAL
 * Creation: 03/11/2006
 * @version 1.0 03/11/2006
 * @author Ludovic APVRILLE
 */
public class TML2UPPAAL {

    //private static int gateId;

    private TMLModeling<?> tmlmodeling;
    private UPPAALSpec spec;
    private RelationTMLUPPAAL rtu;
    private UPPAALTemplate lossTemplate;
    private Vector<String> lossNames;

    //  private boolean debug;
    private int sizeInfiniteFIFO = DEFAULT_INFINITE_FIFO_SIZE;

    private int currentX, currentY;

    public final static int STEP_X = 0;
    public final static int STEP_Y = 75;
    public final static int STEP_LOOP_X = 75;
    public final static int NAME_X = 10;
    public final static int NAME_Y = 5;
    public final static int SYNCHRO_X = 5;
    public final static int SYNCHRO_Y = -10;
    public final static int ASSIGN_X = 10;
    public final static int ASSIGN_Y = 0;
    public final static int GUARD_X = 0;
    public final static int GUARD_Y = -20;

    public static final int DEFAULT_INFINITE_FIFO_SIZE = 8;


    public TML2UPPAAL(TMLModeling<?> _tmlmodeling) {
        tmlmodeling = _tmlmodeling;
        TraceManager.addDev("TML2UPPAAL");
    }

    // Returns a list of all file names ..
    public void saveInFile(String path) throws FileException {
    	SpecConfigTTool.checkAndCreateUPPAALDir(path);
        FileUtils.saveFile(path + "spec.xml", spec.makeSpec());

    }

    public void saveInPathFile(String PathFile) throws FileException {
        FileUtils.saveFile(PathFile, spec.makeSpec());

    }

    public String getUPPAALSpec() {
        return spec.makeSpec();
    }


    public void setSizeInfiniteFIFO(int _size) {
        sizeInfiniteFIFO = _size;
    }

    public RelationTMLUPPAAL getRelationTMLUPPAAL() {
        return rtu;
    }

    public UPPAALSpec generateUPPAAL(boolean _debug) {
        TraceManager.addDev("Generating UPPAAL Specification from TML");
        tmlmodeling.removeAllRandomSequences();

        //  debug = _debug;
        spec = new UPPAALSpec();
        rtu = new RelationTMLUPPAAL();
        lossTemplate = null;

        // Must fill spec!

        makeGlobal();
        makeChannels();
        makeRequests();
        makeEvents();

        // Make tasks
        makeTasks();

        // Instanciation of the system
        makeSystem();

        return spec;
    }

    public void makeGlobal() {
        spec.addGlobalDeclaration("const int DEFAULT_INFINITE_SIZE = " + sizeInfiniteFIFO + ";\n\n");
        spec.addGlobalDeclaration("int min(int a, int b) {\nif (a<b) {\nreturn a;\n} else {\nreturn b;\n}\n}\n\n");
        spec.addGlobalDeclaration("int max(int a, int b) {\nif (a>b) {\nreturn a;\n} else {\nreturn b;\n}\n}\n\n");

    }

    public void makeChannels() {
        Iterator<TMLChannel> iterator = tmlmodeling.getListIteratorChannels();

        while(iterator.hasNext()) {
            makeChannel(iterator.next());
        }
    }

    public void makeChannel(TMLChannel ch) {
        if (ch.getType() == TMLChannel.BRBW) {

            spec.addGlobalDeclaration("urgent chan rd__" + ch.getName() + ", wr__" + ch.getName() + ";\n");
            if (ch.isLossy()) {
                spec.addTemplate(new UPPAALFiniteFIFOTemplateLoss("channel__" + ch.getName(), ch.getName(), ch.getMax(), ch.getMaxNbOfLoss()));
                makeLoss("ch__" + ch.getName());
            } else {
                spec.addTemplate(new UPPAALFiniteFIFOTemplate("channel__" + ch.getName(), ch.getName(), ch.getMax()));

            }
        } else if (ch.getType() == TMLChannel.BRNBW) {
            spec.addGlobalDeclaration("urgent chan rd__" + ch.getName() + ", wr__" + ch.getName() + ";\n");
            if (ch.isLossy()) {
                spec.addTemplate(new UPPAALInfiniteFIFOTemplateLoss("channel__" + ch.getName(), ch.getName(), ch.getMaxNbOfLoss()));
                makeLoss("ch__" + ch.getName());
            } else {
                spec.addTemplate(new UPPAALInfiniteFIFOTemplate("channel__" + ch.getName(), ch.getName()));
            }
        } else if (ch.getType() == TMLChannel.NBRNBW) {
            spec.addGlobalDeclaration("urgent chan rd__" + ch.getName() + ", wr__" + ch.getName() + ";\n");
            if (ch.isLossy()) {
                spec.addTemplate(new UPPAALMemoryTemplateLoss("channel__" + ch.getName(), ch.getName(), ch.getMaxNbOfLoss()));
                makeLoss("ch__" + ch.getName());
            } else {
                spec.addTemplate(new UPPAALMemoryTemplate("channel__" + ch.getName(), ch.getName()));
            }
        }
    }

    public void makeRequests() {
        Iterator<TMLRequest> iterator = tmlmodeling.getListIteratorRequests();

        while(iterator.hasNext()) {
            makeRequest( iterator.next() );
        }
    }

    public void makeRequest(TMLRequest request) {
        for(int i=0; i<request.getNbOfParams(); i++) {
            spec.addGlobalDeclaration(request.getType(i).toString() + " head" + i + "__" + request.getName()+ ";\n");
            spec.addGlobalDeclaration(request.getType(i).toString() + " tail" + i + "__" + request.getName()+ ";\n");
        }
        spec.addGlobalDeclaration("urgent chan request__" + request.getName() + ", wait__" + request.getName() + ";\n");
        if (request.isLossy()) {
            TraceManager.addDev("Lossy req");
            spec.addTemplate(new uppaaldesc.tmltouppaal.UPPAALRequestTemplateWithLoss("ReqManager__" + request.getName(), request, "DEFAULT_INFINITE_SIZE", request.getMaxNbOfLoss()));
            makeLoss("req__" + request.getName());
        } else {
            TraceManager.addDev("Non lossy req");
            spec.addTemplate(new uppaaldesc.tmltouppaal.UPPAALRequestTemplate("ReqManager__" + request.getName(), request, "DEFAULT_INFINITE_SIZE"));
        }
    }

    public void makeEvents() {
        Iterator<TMLEvent> iterator = tmlmodeling.getListIteratorEvents();

        while(iterator.hasNext()) {
            makeEvent( iterator.next());
        }
    }

    public void makeEvent(TMLEvent event) {
        for(int i=0; i<event.getNbOfParams(); i++) {
            spec.addGlobalDeclaration(event.getType(i).toString() + " eventHead" + i + "__" + event.getName()+ ";\n");
            spec.addGlobalDeclaration(event.getType(i).toString() + " eventTail" + i + "__" + event.getName()+ ";\n");
        }
        spec.addGlobalDeclaration("urgent chan eventSend__" + event.getName() + ", eventNotify__" + event.getName() + ", eventNotified__" + event.getName()+ ";\n");
        spec.addGlobalDeclaration("int notified__" + event.getName() + ";\n");
        if (event.isLossy()) {
            spec.addTemplate(new uppaaldesc.tmltouppaal.UPPAALEventTemplateWithLoss("EvtManager__" + event.getName(), event, "DEFAULT_INFINITE_SIZE", event.getMaxNbOfLoss()));
            makeLoss("evt__" + event.getName());
        } else {
            spec.addTemplate(new uppaaldesc.tmltouppaal.UPPAALEventTemplate("EvtManager__" + event.getName(), event, "DEFAULT_INFINITE_SIZE"));
        }
    }

    public void makeTasks() {
        Iterator<TMLTask> iterator = tmlmodeling.getListIteratorTasks();

        while(iterator.hasNext()) {
            makeTask( iterator.next() );
        }
    }

    public void makeTask(TMLTask task) {
        // We do not take into account if the task is requested or not

        // Generate template
        UPPAALTemplate template = new UPPAALTemplate();
        template.setName(task.getName());
        spec.addTemplate(template);
        rtu.addTMLTaskTemplate(task, template);

        // Attributes
        makeAttributes(task, template);

        // Behavior
        makeBehavior(task, template);

    }

    public void makeAttributes(TMLTask task, UPPAALTemplate template) {
        Iterator<TMLAttribute> iterator = task.getAttributes().listIterator();
        TMLAttribute tmlatt;

        while(iterator.hasNext()) {
            tmlatt = iterator.next();
            if (tmlatt.hasInitialValue()) {
                template.addDeclaration(tmlatt.getType().toString() + " " + tmlatt.getName() + " = " + tmlatt.getInitialValue() +";\n");
            } else {
                template.addDeclaration(tmlatt.getType().toString() + " " + tmlatt.getName() + ";\n");
            }
        }

        template.addDeclaration("int nb__rd;\nint nb__wr;\n");
        if (task.isRequested()) {
            TMLRequest req = task.getRequest();



            for(int i=0; i<req.getNbOfParams(); i++) {
                if (task.getAttributeByName("arg" + (i+1) + "__req") == null) {

                    template.addDeclaration(req.getType(i).toString() + "arg" + (i+1) + "__req;\n");
                }
            }
        }

        if (task.hasTMLRandom()) {
            template.addDeclaration("int min__random;\n");
            template.addDeclaration("int max__random;\n");
        }
    }

    public void makeBehavior(TMLTask task, UPPAALTemplate template) {
        // Request is not yet taken into account
        TMLActivityElement first = task.getActivityDiagram().getFirst();

        currentX = 0; currentY = -220;

        if (task.isRequested()) {
            UPPAALLocation loc, loc1;
            UPPAALTransition tr;
            loc = addLocation(template);
            template.setInitLocation(loc);
            loc1 = addLocation(template);
            tr = addTransition(template, loc, loc1);
            setSynchronization(tr, "wait__" + task.getRequest().getName() + "?");
            String s = "";
            for(int i=0; i<task.getRequest().getNbOfParams(); i++) {
                if (i!= 0) {
                    s+= ",\n";
                }
                s += " arg" + (i+1) + "__req = head" + i + "__" + task.getRequest().getName();
            }
            setAssignment(tr, s);
            currentX += STEP_LOOP_X;

            // skip start state
            makeElementBehavior(task, template, first.getNextElement(0), loc1, loc);
        } else {
            makeElementBehavior(task, template, first, null, null);
        }
    }

    public void makeElementBehavior(TMLTask task, UPPAALTemplate template, TMLActivityElement elt, UPPAALLocation previous, UPPAALLocation end) {
        UPPAALLocation loc, loc1, loc2,/* loc3,*/ loc4;
        UPPAALTransition tr, tr1, tr2, tr3;
        TMLReadChannel rc;
        TMLWriteChannel wc;
        TMLForLoop tmlloop;
        TMLSendRequest sr;
        TMLChoice choice;
        TMLSendEvent sendevt;
        TMLWaitEvent waitevt;
        TMLNotifiedEvent notifiedevt;
        TMLSequence seq;
        TMLRandom random;



        if (elt == null) {
            return;
        }

        // Start state
        if (elt instanceof TMLStartState) {
            loc = addLocation(template);
            template.setInitLocation(loc);
            rtu.addTMLActivityElementLocation(elt, loc, loc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc, end);
            return;

            // Stop state
        } else if (elt instanceof TMLStopState) {
            if (end == null) {
                rtu.addTMLActivityElementLocation(elt, previous, previous);
                return;
            }
            previous.setCommitted();
            tr = addTransition(template, previous, end);
            rtu.addTMLActivityElementLocation(elt, previous, end);
            return;

            // Read samples
        } else if (elt instanceof TMLReadChannel) {
            rc = (TMLReadChannel)elt;
            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            previous.setCommitted();
            loc.setUrgent();
            setAssignment(tr, "nb__rd = " + rc.getNbOfSamples());

            tr1 = addTransition(template, loc, loc);
            setGuard(tr1, "nb__rd>0");
            setSynchronization(tr1, "rd__" +rc.getChannel(0).getName() + "!");
            setAssignment(tr1, "nb__rd = nb__rd - 1");

            loc1 = addLocation(template);
            tr2 = addTransition(template, loc, loc1);
            setGuard(tr2, "nb__rd==0");
            rtu.addTMLActivityElementLocation(elt, previous, loc1);
            makeElementBehavior(task, template, elt.getNextElement(0), loc1, end);

            //tr.synchronization = "rd__" + rc.getChannel().getName() + "!";

            // Write channel
        } else if (elt instanceof TMLWriteChannel) {
            wc = (TMLWriteChannel)elt;

            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            setAssignment(tr, "nb__wr = " + wc.getNbOfSamples());
            previous.setCommitted();
            loc.setUrgent();

            // no support for multiwrite
            if (wc.getNbOfChannels() == 1) {
                tr1 = addTransition(template, loc, loc);
                setGuard(tr1, "nb__wr>0");
                setSynchronization(tr1, "wr__" +wc.getChannel(0).getName() + "!");
                setAssignment(tr1, "nb__wr = nb__wr - 1");

                loc1 = addLocation(template);
                tr2 = addTransition(template, loc, loc1);
                setGuard(tr2, "nb__wr==0");
                rtu.addTMLActivityElementLocation(elt, previous, loc1);

            } else {
                loc2 = loc;
                loc1 = null;
                for(int k=0; k<wc.getNbOfChannels(); k++) {
                    tr1 = addTransition(template, loc, loc);
                    setGuard(tr1, "nb__wr>0");
                    setSynchronization(tr1, "wr__" +wc.getChannel(k).getName() + "!");
                    setAssignment(tr1, "nb__wr = nb__wr - 1");

                    if (k == (wc.getNbOfChannels()-1)) {
                        loc1 = addLocation(template);
                        tr2 = addTransition(template, loc, loc1);
                        setGuard(tr2, "nb__wr==0");
                        rtu.addTMLActivityElementLocation(elt, previous, loc1);
                    } else {
                        loc1 = addLocation(template);
                        tr2 = addTransition(template, loc, loc1);
                        setGuard(tr2, "nb__wr == 0");
                        setAssignment(tr2, "nb__wr = " + wc.getNbOfSamples());
                        loc = loc1;
                    }
                }
            }

            makeElementBehavior(task, template, elt.getNextElement(0), loc1, end);

            // Send Request
        } else if (elt instanceof TMLSendRequest) {
            sr = (TMLSendRequest)elt;
            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            setSynchronization(tr, "request__" + sr.getRequest().getName() + "!");
            String s = "";
            for(int i=0; i<sr.getRequest().getNbOfParams(); i++) {
                if (i != 0) {
                    s += ",\n";
                }
                s += "tail" + i + "__" + sr.getRequest().getName()+ " = " + sr.getParam(i);
            }
            setAssignment(tr, s);
            rtu.addTMLActivityElementLocation(elt, previous, loc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc, end);

            // Send Event
        } else if (elt instanceof TMLSendEvent) {
            sendevt = (TMLSendEvent)elt;
            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            setSynchronization(tr, "eventSend__" + sendevt.getEvent().getName() + "!");
            String s = "";
            for(int i=0; i<sendevt.getEvent().getNbOfParams(); i++) {
                if (i != 0) {
                    s += ",\n";
                }
                s += "eventTail" + i + "__" + sendevt.getEvent().getName()+ " = " + sendevt.getParam(i);
            }
            setAssignment(tr, s);
            rtu.addTMLActivityElementLocation(elt, previous, loc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc, end);

            // Wait event
        } else if (elt instanceof TMLWaitEvent) {
            waitevt = (TMLWaitEvent)elt;
            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            setSynchronization(tr, "eventNotify__" + waitevt.getEvent().getName() + "?");
            String s = "";
            for(int i=0; i<waitevt.getEvent().getNbOfParams(); i++) {
                if (i != 0) {
                    s += ",\n";
                }
                s += waitevt.getParam(i) + "= eventHead" + i + "__" + waitevt.getEvent().getName();
            }
            setAssignment(tr, s);
            rtu.addTMLActivityElementLocation(elt, previous, loc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc, end);

            // Notified event
        } else if (elt instanceof TMLNotifiedEvent) {
            notifiedevt = (TMLNotifiedEvent)elt;
            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            setSynchronization(tr, "eventNotified__" + notifiedevt.getEvent().getName() + "?");
            setAssignment(tr, notifiedevt.getVariable() + " = notified__" + notifiedevt.getEvent().getName());
            rtu.addTMLActivityElementLocation(elt, previous, loc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc, end);

            // Action State
        } else if (elt instanceof TMLActionState) {
            if (((TMLActionState)elt).getAction().indexOf("<<") > -1) {
                rtu.addTMLActivityElementLocation(elt, previous, previous);
                makeElementBehavior(task, template, elt.getNextElement(0), previous, end);
                return;
            }

            if (((TMLActionState)elt).getAction().indexOf("exit(") > -1) {
                rtu.addTMLActivityElementLocation(elt, previous, previous);
                makeElementBehavior(task, template, elt.getNextElement(0), previous, end);
                return;
            }

            String action =((TMLActionState)elt).getAction();
            action = action.trim();
            if (action.endsWith(";")) {
                action = action.substring(0, action.length() - 1);
            }

            loc = addLocation(template);
            tr = addTransition(template, previous, loc);
            setAssignment(tr, action);
            rtu.addTMLActivityElementLocation(elt, previous, loc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc, end);

            // Random
        } else if (elt instanceof TMLRandom) {
            random = (TMLRandom)elt;
            loc = addLocation(template);
            previous.setCommitted();
            loc.setCommitted();
            tr = addTransition(template, previous, loc);
            setAssignment(tr, "min__random =" + random.getMinValue() + ", max__random = " + random.getMaxValue());


            loc1 = addLocation(template);
            tr1 = addTransition(template, loc, loc1);
            setAssignment(tr1, random.getVariable() + " = min__random");
            setGuard(tr1, "min__random < max__random + 1");

            tr2 = addTransition(template, loc, loc);
            setAssignment(tr2, "min__random = min__random + 1");
            setGuard(tr2, "min__random < max__random ");

            makeElementBehavior(task, template, elt.getNextElement(0), loc1, end);

            // EXEC operations -> ignored
        } else if ((elt instanceof TMLExecI) || (elt instanceof TMLExecC) ||(elt instanceof TMLExecIInterval)|| (elt instanceof TMLExecCInterval)|| (elt instanceof TMLDelay)) {
            rtu.addTMLActivityElementLocation(elt, previous, previous);
            makeElementBehavior(task, template, elt.getNextElement(0), previous, end);

            // Sequence
        } else if (elt instanceof TMLSequence) {
            seq = ((TMLSequence)elt);

            // Make sure the sequence is sorted;
            seq.sortNexts();

            // Check at least for sequences with two nexts
            if (seq.getNbNext() < 2) {
                // nothing to do!
                rtu.addTMLActivityElementLocation(elt, previous, previous);
                makeElementBehavior(task, template, elt.getNextElement(0), previous, end);
                return;
            }

            // Take the last one ...
            currentX = currentX + (seq.getNbNext()-1)*STEP_LOOP_X;
            loc = addLocation(template);
            rtu.addTMLActivityElementLocation(elt, previous, loc);
            makeElementBehavior(task, template, elt.getNextElement(seq.getNbNext()-1), loc, end);

            for (int i=seq.getNbNext()-2; i>=0; i--) {
                currentX -= STEP_LOOP_X;
                loc1 = addLocation(template);
                makeElementBehavior(task, template, elt.getNextElement(i), loc1, loc);
                loc = loc1;
            }

            tr = addTransition(template, previous, loc);

            // Loop
        } else if (elt instanceof TMLForLoop) {
            String tmpc;
            tmlloop = ((TMLForLoop)elt);
            loc = addLocation(template);
            previous.setCommitted();
            loc.setCommitted();
            tr = addTransition(template, previous, loc);
            setAssignment(tr, tmlloop.getInit());

            currentX += STEP_LOOP_X;
            loc1 = addLocation(template);
            tr1 = addTransition(template, loc, loc1);
            loc4 = addLocation(template);
            loc4.setCommitted();
            tr2 = addTransition(template, loc4, loc);
            setAssignment(tr2, tmlloop.getIncrement());
            currentX += STEP_LOOP_X;

            tmpc =  tmlloop.getCondition();
            if (tmpc.length() ==0) {
                tmpc = "true";
            }
            setGuard(tr1, tmpc);
            makeElementBehavior(task, template, elt.getNextElement(0), loc1, loc4);
            currentX -= STEP_LOOP_X;
            currentX -= STEP_LOOP_X;

            loc2 = addLocation(template);
            tr3 = addTransition(template, loc, loc2);
            setGuard(tr3, "!(" + tmpc + ")");
            rtu.addTMLActivityElementLocation(elt, previous, loc2);
            makeElementBehavior(task, template, elt.getNextElement(1), loc2, end);

            // TMLSelectEvt
        } else if (elt instanceof TMLSelectEvt) {
            for(int i=0; i<elt.getNbNext(); i++) {
                makeElementBehavior(task, template, elt.getNextElement(i), previous, end);
                currentX += STEP_LOOP_X;
            }
            rtu.addTMLActivityElementLocation(elt, previous, previous);

            // Choice
        } else if (elt instanceof TMLChoice) {
            choice = (TMLChoice)elt;
            String g;

            if (choice.getNbGuard() ==0 ) {
                rtu.addTMLActivityElementLocation(elt, previous, previous);
                makeElementBehavior(task, template, elt.getNextElement(0), previous, end);
                return;
            }

            // Nb of guards > 0
            int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();

            if (index2 != -1) {
                // [after]
                loc = addLocation(template);
                makeElementBehavior(task, template, elt.getNextElement(index2), loc, end);
                end = loc;
                currentX += STEP_LOOP_X;
            }

            for(int i=0; i<choice.getNbGuard(); i++) {
                if (i != index2) {
                    if (i != index1) {
                        g = choice.getGuard(i);
                        if (choice.isStochasticGuard(i)) {
                            g = "[ ]";
                        }
                        g = Conversion.replaceAllChar(g, '[', "(");
                        g = Conversion.replaceAllChar(g, ']', ")");
                        g = g.trim();
                        if ((g.compareTo("()") == 0) ||(g.compareTo("( )") == 0)) {
                            g = " ";
                        }
                        loc1 = addLocation(template);
                        tr1 = addTransition(template, previous, loc1);
                        setGuard(tr1, g);
                        makeElementBehavior(task, template, elt.getNextElement(i), loc1, end);
                    } else {
                        // else transition
                        int cpt = 0;
                        String gs = "";
                        for(int j=0; j<choice.getNbGuard(); j++) {
                            if ((j != index2) && (j != index1)) {
                                g = choice.getGuard(j);

                                g = Conversion.replaceAllChar(g, '[', "(");
                                g = Conversion.replaceAllChar(g, ']', ")");
                                if (cpt == 0) {
                                    gs = g;
                                } else {
                                    gs = "((" + gs + ")||(" + g + "))";
                                }
                                cpt ++;
                            }
                        }
                        gs = "!" + gs;
                        loc1 = addLocation(template);
                        tr1 = addTransition(template, previous, loc1);
                        setGuard(tr1, gs);
                        rtu.addTMLActivityElementLocation(elt, previous, loc1);
                        makeElementBehavior(task, template, elt.getNextElement(i), loc1, end);
                    }
                    currentX += STEP_LOOP_X;
                }
            }

        } else {
            TraceManager.addDev("Warning: elt = " + elt + " is not yet taken into account .. skipping");
            rtu.addTMLActivityElementLocation(elt, previous, previous);
            makeElementBehavior(task, template, elt.getNextElement(0), previous, end);
        }


    }

    public UPPAALLocation addLocation(UPPAALTemplate template) {
        UPPAALLocation loc = new UPPAALLocation();
        loc.idPoint.x = currentX;
        loc.idPoint.y = currentY;
        loc.namePoint.x = currentX + NAME_X;
        loc.namePoint.y = currentY + NAME_Y;
        template.addLocation(loc);
        currentX += STEP_X;
        currentY += STEP_Y;
        return loc;
    }

    public UPPAALTransition addTransition(UPPAALTemplate template, UPPAALLocation loc1, UPPAALLocation loc2) {
        UPPAALTransition tr = new UPPAALTransition();
        tr.sourceLoc = loc1;
        tr.destinationLoc = loc2;
        template.addTransition(tr);
        // Nails?
        return tr;
    }

    public void setSynchronization(UPPAALTransition tr, String s) {
        tr.synchronization = modifyString(s);
        tr.synchronizationPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + SYNCHRO_X;
        tr.synchronizationPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + SYNCHRO_Y;
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

    public void makeSystem() {
        Iterator<UPPAALTemplate> iterator = spec.getTemplates().listIterator();
        UPPAALTemplate template;
        String system = "system ";
        String dec = "";
        int id = 0;

        while(iterator.hasNext()) {
            template = iterator.next();
            template.setIdInstanciation(id);
            dec += template.getName() + "__" + id + " = " + template.getName() + "();\n";
            system += template.getName() + "__" + id;
            if (iterator.hasNext()) {
                system += ",";
            } else {
                system += ";";
            }
            id ++;
        }

        spec.addInstanciation(dec+system);
    }

    public void makeLoss(String name) {
        TraceManager.addDev("Making loss");
        if (lossTemplate == null) {
            lossTemplate = new UPPAALTemplate();
            lossTemplate.setName("LossManager__");
            spec.addTemplate(lossTemplate);
            UPPAALLocation loc = addLocation(lossTemplate);
            loc.name = "main__loss";
            lossTemplate.setInitLocation(loc);
            lossNames = new Vector<String>();
        } else {
            // loss already computed?
            for(String s: lossNames) {
                if (s.compareTo(name) == 0) {
                    return;
                }
            }
        }

        lossNames.add(name);

        UPPAALTransition tr = addTransition(lossTemplate, lossTemplate.getInitLocation(), lossTemplate.getInitLocation());
        setSynchronization(tr, name + "__loss?");
        tr = addTransition(lossTemplate, lossTemplate.getInitLocation(), lossTemplate.getInitLocation());
        setSynchronization(tr, name + "__noloss?");

        spec.addGlobalDeclaration("\n// Lossy communications\n");
        spec.addGlobalDeclaration("urgent chan " + name + "__loss, " + name + "__noloss;\n");

    }

    public String modifyString(String _input) {

        try {
            _input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
            _input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
        } catch (Exception e) {
            TraceManager.addDev("Exception when changing binary operator in " + _input);
        }

        return _input;
    }

}
