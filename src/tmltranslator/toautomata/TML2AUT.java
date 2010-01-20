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
 * Class TML2AUT
 * Creation: 23/03/2006
 * @version 1.0 23/03/2006
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator.toautomata;

import java.util.*;

import tmltranslator.*;
import automata.*;
import myutil.*;


public class TML2AUT {
    
    //private static int gateId;
    
    private TMLModeling tmlmodeling;
    private LinkedList automatas;
    
    private boolean debug;
    
    
    public static String AUT_EXTENSION = "aut";
    public static String IMM = "imm__";
    
    
    public TML2AUT(TMLModeling _tmlmodeling) {
        tmlmodeling = _tmlmodeling;
    }
    
    // Returns a list of all file names ..
    public LinkedList saveInFiles(String path) throws FileException {
        //print();
        
        ListIterator iterator = automatas.listIterator();
        Automata aut;
        String name;
        LinkedList ll = new LinkedList();
        
        while(iterator.hasNext()) {
            aut = (Automata)(iterator.next());
            name = aut.getName() + "." + AUT_EXTENSION;
            ll.add(name);
            System.out.println("File: " + path + aut.getName() + "." + AUT_EXTENSION);
            FileUtils.saveFile(path + aut.getName() + "." + AUT_EXTENSION, aut.toAUT());
        }
        return ll;
        
    }
    
    public void print() {
        // Print each automatas
        ListIterator iterator = automatas.listIterator();
        Automata aut;
        
        while(iterator.hasNext()) {
            aut = (Automata)(iterator.next());
            System.out.println("Automata: " + aut.getName());
            System.out.println(aut.toAUT());
        }
    }
    
    public void generateAutomatas(boolean _debug) {
        debug = _debug;
        automatas = new LinkedList();
        
        // Generate one automata per TMLTask
        generateAUTTMLTasks();
        
        // Generate one automata per Channel
        
        // Generate one automata per Request
        
        // Generate one automata per Event
        
        
        // All done!
        
    }
    
    public void generateAUTTMLTasks() {
        ListIterator iterator = tmlmodeling.getTasks().listIterator();
        while(iterator.hasNext()) {
            automatas.add(generateAUTTMLTask((TMLTask)(iterator.next())));
        }
    }
    
    public Automata generateAUTTMLTask(TMLTask task) {
        Automata aut = new Automata();
        aut.setName(task.getName());
        TMLRequest request;
        State s, init;
        Transition t;
        String action;
        
        init = aut.getInitState();
        
        if (task.isRequested()) {
            request = task.getRequest();
            s = aut.newState();
            action = "waitInit " + task.getName() + "(";
            for (int i=0; i<task.getRequest().getNbOfParams(); i++) {
                if (i != 0) {
                    action += ",";
                }
                action += "arg" + (i+1) + "_req";
            }
            action += ")";
            t = new Transition(action, s);
            init.addTransition(t);
            generateAUTTMLTask(aut, task, task.getActivityDiagram().getFirst(), s, init);
        } else {
            generateAUTTMLTask(aut, task, task.getActivityDiagram().getFirst(), init, null);
        }
        removeImmTransitions(aut);
        return aut;
        
    }
    
    public void generateAUTTMLTask(Automata aut, TMLTask task, TMLActivityElement elt, State currentState, State endState) {
        State s, s0, s1, s2, s3;
        Transition t;
        TMLForLoop loop;
        TMLActivityElementChannel ch;
        TMLActivityElementEvent evt;
        TMLSendRequest req;
        String action;
        TMLChoice choice;
        TMLSequence tmlseq;
        
        // Action State
        if (elt instanceof TMLActionState) {
            action = ((TMLActionState)(elt)).getAction();
            if (printAnalyzer(action)) {
                generateAUTTMLTask(aut, task, elt.getNextElement(0), currentState, endState);
            } else {
                action = modifyString(action);
                action = removeLastSemicolon(action);
                s = aut.newState();
                t = new Transition(action, s);
                currentState.addTransition(t);
                generateAUTTMLTask(aut, task, elt.getNextElement(0), s, endState);
            }
            // EXECI
        } else if (elt instanceof TMLExecI) {
            generateAUTTMLTask(aut, task, elt.getNextElement(0), currentState, endState);
            
            // EXECI Interval
        } else if (elt instanceof TMLExecIInterval) {
            generateAUTTMLTask(aut, task, elt.getNextElement(0), currentState, endState);
            
            // LOOP
        } else if (elt instanceof TMLForLoop) {
            loop = (TMLForLoop)elt;
            
            s0 = aut.newState();
            t = new Transition(loop.getInit(), s0);
            currentState.addTransition(t);
            
            s1 = aut.newState();
            t = new Transition(loop.getIncrement(), s0);
            s1.addTransition(t);
            
            s2 = aut.newState();
            t = new Transition("~(" + loop.getCondition() + ")", s2);
            s0.addTransition(t);
            
            s3 = aut.newState();
            t = new Transition(loop.getCondition(), s3);
            s0.addTransition(t);
            
            // In loop
            generateAUTTMLTask(aut, task, elt.getNextElement(0), s3, s1);
            
            // Aftr loop ...
            generateAUTTMLTask(aut, task, elt.getNextElement(1), s2, endState);
            
            // Junction
        } else if (elt instanceof TMLJunction) {
            generateAUTTMLTask(aut, task, elt.getNextElement(0), currentState, endState);
            
            // Read Channel
        } else if (elt instanceof TMLReadChannel) {
            ch = (TMLActivityElementChannel)elt;
            s = aut.newState();
            t = new Transition(ch.getChannel(0).getName() + ".get(" + ch.getNbOfSamples() + ")", s);
            currentState.addTransition(t);
            generateAUTTMLTask(aut, task, elt.getNextElement(0), s, endState);
            
            // Write Channel
        } else if (elt instanceof TMLWriteChannel) {
            ch = (TMLActivityElementChannel)elt;
            s = aut.newState();
            t = new Transition(ch.getChannel(0).getName() + ".put(" + ch.getNbOfSamples() + ")", s);
            currentState.addTransition(t);
            generateAUTTMLTask(aut, task, elt.getNextElement(0), s, endState);
            
            // Wait for an event
        } else if (elt instanceof TMLWaitEvent) {
            evt = (TMLActivityElementEvent)elt;
            s = aut.newState();
            action = "?" + evt.getEvent().getName() + "(";
            for (int i=0; i<evt.getEvent().getNbOfParams(); i++) {
                if (i != 0) {
                    action += ",";
                }
                action += evt.getParam(i);
            }
            action += ")";
            t = new Transition(action, s);
            currentState.addTransition(t);
            generateAUTTMLTask(aut, task, elt.getNextElement(0), s, endState);
            
            // Notify an event
        } else if (elt instanceof TMLSendEvent) {
            evt = (TMLActivityElementEvent)elt;
            s = aut.newState();
            action = "!" + evt.getEvent().getName() + "(";
            for (int i=0; i<evt.getEvent().getNbOfParams(); i++) {
                if (i != 0) {
                    action += ",";
                }
                action += evt.getParam(i);
            }
            action += ")";
            t = new Transition(action, s);
            currentState.addTransition(t);
            generateAUTTMLTask(aut, task, elt.getNextElement(0), s, endState);
            
            // Send a request
        } else if (elt instanceof TMLSendRequest) {
            req = (TMLSendRequest)elt;
            s = aut.newState();
            action = "init " + req.getRequest().getDestinationTask().getName() + "(";
            for (int i=0; i<req.getRequest().getNbOfParams(); i++) {
                if (i != 0) {
                    action += ",";
                }
                action += req.getParam(i);
            }
            action += ")";
            t = new Transition(action, s);
            currentState.addTransition(t);
            generateAUTTMLTask(aut, task, elt.getNextElement(0), s, endState);
            
            // Choice
        } else if (elt instanceof TMLChoice) {
            choice = (TMLChoice)elt;
            //String guard = "";
            State newEndState = endState;
            int i;
            
            if (choice.getNbGuard() !=0 ) {
                int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
                if (index2 != -1) {
                    //System.out.println("Managing after");
                    s = aut.newState();
                    newEndState = s;
                    generateAUTTMLTask(aut, task, elt.getNextElement(index2), s, endState);
                }
                
                for(i=0; i<choice.getNbGuard(); i++) {
                    //System.out.println("Get guards i=" + i);
                    s = aut.newState();
                    if (i==index1) {
                        /* else guard */
                        action = modifyString(choice.getValueOfElse());
                    } else {
                        action = modifyString(choice.getGuard(i));
                    }
                    t = new Transition(action, s);
                    currentState.addTransition(t);
                    generateAUTTMLTask(aut, task, elt.getNextElement(i), s, newEndState);
                }
            } else {
                endOfActivity(aut, currentState, endState);
            }
            
            // Start State
        } else if (elt instanceof TMLStartState) {
            generateAUTTMLTask(aut, task, elt.getNextElement(0), currentState, endState);
            
            // Stop State
        } else if (elt instanceof TMLStopState) {
            endOfActivity(aut, currentState, endState);
            
            // Sequence
        } else if (elt instanceof TMLSequence) {
            //System.out.println("TML sequence !");
            tmlseq = (TMLSequence)elt;
            
            if (tmlseq.getNbNext() == 0) {
                endOfActivity(aut, currentState, endState);
            } else {
                
                
                if (tmlseq.getNbNext() == 1) {
                    generateAUTTMLTask(aut, task, elt.getNextElement(0), currentState, endState);
                } else {
                    
                    tmlseq.sortNexts();
                    // At least 2 next elements
                    for(int i=1; i<tmlseq.getNbNext()-1; i++) {
                        s1 = aut.newState();
                        generateAUTTMLTask(aut, task, elt.getNextElement(i), currentState, s1);
                        currentState = s1;
                    }
                    generateAUTTMLTask(aut, task, elt.getNextElement(tmlseq.getNbNext()-1), currentState, endState);
                }
            }
        } else {
            System.out.println("Element " + elt + " is not managed by AUT format");
        }
    }
    
    private void removeImmTransitions(Automata aut) {
        Transition t1, t2;
        LinkedList states;
        State s1, s2;
        ListIterator st, tr;
        
        states = aut.getStates();
        st = states.listIterator();
        
        while(st.hasNext()) {
            s1 = (State)(st.next());
            tr = s1.getTransitions().listIterator();
            while(tr.hasNext()) {
                t1 = (Transition)(tr.next());
                s2 = t1.getNextState();
                if (onlyImmFromState(s2)) {
                    t2 = s2.getTransition(0);
                    t1.setNextState(t2.getNextState());
                }
            }
        }
        
        // Remove states from which an imm starts ...
        for(int i=0; i<states.size(); i++) {
            s1 = (State)(states.get(i));
            if (onlyImmFromState(s1)) {
                states.remove(i);
                i --;
            }
        }
        
        // Rename states
        aut.renameStates();
        
        
    }
    
    private boolean onlyImmFromState(State s) {
        if (s.nbOfTransitions() != 1) {
            return false;
        }
        
        if (s.getTransition(0).getValue().compareTo(IMM) != 0) {
            return false;
        }
        
        return true;
        
    }
    
    private void endOfActivity(Automata aut, State currentState, State endState) {
        if (endState != null) {
            Transition t = new Transition(IMM, endState);
            currentState.addTransition(t);
        }
    }
    
    private String modifyString(String _input) {
        // Replaces &&, || and !
        _input = Conversion.replaceAllString(_input,"&&", "and");
        _input = Conversion.replaceAllString(_input, "||", "or");
        _input = Conversion.replaceAllString(_input, "!", "not");
        
        return _input;
    }
    
    private String removeLastSemicolon(String action) {
        action = action.trim();
        if (action.charAt(action.length()-1) == ';') {
            return action.substring(0, action.length()-1);
        }
        return action;
    }
    
    private boolean printAnalyzer(String action) {
        action = action.trim();
        if (action.startsWith("cout") || action.startsWith("std::cout")) {
            return true;
        }
        return false;
        
    }
    
    
    
}