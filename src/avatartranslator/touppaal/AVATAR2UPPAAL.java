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
 * Class AVATAR2UPPAAL
 * Creation: 25/05/2010
 * @version 1.1 25/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package avatartranslator.touppaal;

import java.awt.*;
import java.util.*;

import uppaaldesc.*;
import myutil.*;
import avatartranslator.*;
import ui.CheckingError;

public class AVATAR2UPPAAL {

    public final static String ACTION_INT = "actionint__";
    public final static String ACTION_BOOL = "actionbool__";
    public final static String CHOICE_ACTION = "makeChoice";
    public final static String CHOICE_VAR = "choice__";

    private UPPAALSpec spec;
    private AvatarSpecification avspec;

    private Vector warnings;

    private int currentX, currentY;

    private LinkedList gatesNotSynchronized; // String
    private LinkedList gatesSynchronized;
    private LinkedList gatesSynchronizedRelations;
    private LinkedList gatesAsynchronized;
    private LinkedList<String> unoptStates;
    private int nbOfIntParameters, nbOfBooleanParameters;

    private Hashtable <AvatarStateMachineElement, UPPAALLocation> hash;
    private Hashtable <AvatarStateMachineElement, UPPAALLocation> hashChecking;
    private Hashtable <String, String> translateString;
    public final static int STEP_X = 5;
    public final static int STEP_Y = 70;
    public final static int STEP_LOOP_X = 150;
    public final static int NAME_X = 10;
    public final static int NAME_Y = 5;
    public final static int SYNCHRO_X = 5;
    public final static int SYNCHRO_Y = -10;
    public final static int ASSIGN_X = 10;
    public final static int ASSIGN_Y = 0;
    public final static int GUARD_X = 0;
    public final static int GUARD_Y = -20;

    /*private boolean isRegular;
      private boolean isRegularTClass;
      private boolean choicesDeterministic = false;
      private boolean variableAsActions = false;
      private RelationTIFUPPAAL table;


      private LinkedList tmpComponents;
      private LinkedList tmpLocations;
      private ArrayList<UPPAALTemplate> templatesWithMultipleProcesses;
      private LinkedList locations;
      private LinkedList gates;
      private LinkedList relations; // null: not synchronize, Relation : synchronized
      private LinkedList parallels;

      private LinkedList gatesNotSynchronized; // String
      private ArrayList<Gate> gatesWithInternalSynchro;
      private int maxSentInt; // Max nb of int put on non synchronized gates
      private int maxSentBool;
      private LinkedList gatesSynchronized;
      private int idChoice;
      private int idTemplate;
      private int idPar;
      private int idParProcess;
      private ArrayList<ADParallel> paras;
      private ArrayList<Integer> parasint;
      //private int idTemplate;
      private boolean multiprocess;





      public final static String SYNCID = "__sync__";
      public final static String GSYNCID = "__gsync__";*/

    private UPPAALTemplate templateNotSynchronized;
    private UPPAALTemplate templateAsynchronous;

    public AVATAR2UPPAAL(AvatarSpecification _avspec) {
        avspec = _avspec;
    }


    public void saveInFile(String path) throws FileException {
        FileUtils.saveFile(path + "spec.xml", spec.makeSpec());
        //System.out.println("spec.xml generated:\n" + spec.getFullSpec());
    }


    public Vector getWarnings() {
        return warnings;
    }

    /*public RelationTIFUPPAAL getRelationTIFUPPAAL () {
      return table;
      }*/
    public Hashtable <String, String> getHash(){
	return translateString;
    }
    public UPPAALSpec generateUPPAAL(boolean _debug, boolean _optimize) {
        warnings = new Vector();
        hash = new Hashtable<AvatarStateMachineElement, UPPAALLocation>();
        hashChecking = new Hashtable<AvatarStateMachineElement, UPPAALLocation>();
	translateString = new Hashtable<String, String>();
        spec = new UPPAALSpec();

        avspec.removeCompositeStates();
        avspec.removeLibraryFunctionCalls ();
        avspec.removeTimers();
        avspec.makeRobustness();
	LinkedList<String> uppaalPragmas = avspec.getSafetyPragmas();
	unoptStates= new LinkedList<String>();
	for (String s: uppaalPragmas){
	    String[] split = s.split("[^a-zA-Z0-9\\.]");
	    for (String t:split){
		if (t.contains(".")){
		    unoptStates.add(t);
		}
	    }
	}

        //TraceManager.addDev("->   Spec:" + avspec.toString());

        UPPAALLocation.reinitID();
        gatesNotSynchronized = new LinkedList();
        gatesNotSynchronized.add("makeChoice");
        gatesSynchronized = new LinkedList();
        gatesSynchronizedRelations = new LinkedList();
        gatesAsynchronized = new LinkedList();

        // Deal with blocks
        translateBlocks();

        translationRelations();

        makeNotSynchronized();
        makeAsynchronous();
        makeSynchronized();

        makeGlobal();


        // Generate system
        //makeGlobal(effectiveNb);
        //makeParallel(nb);


        makeSystem();

        if (_optimize) {
            spec.optimize();
        }

        TraceManager.addDev("Enhancing graphical representation ...");
        spec.enhanceGraphics();
        TraceManager.addDev("Enhancing graphical representation done");

        //System.out.println("relations:" + table.toString());

        return spec;
    }

    public void initXY() {
        currentX = 0; currentY = -220;
    }

    public void makeGlobal() {

        int i;
        String s = "";

        s += "\n// Global parameters for method calls and signal exchange\n";
        for(i=0; i<nbOfIntParameters; i++) {
            s+= "int " + ACTION_INT  + i + ";\n";
        }
        for(i=0; i<nbOfBooleanParameters; i++) {
            s+= "int " + ACTION_BOOL  + i + ";\n";
        }
        s+= "\n";


        s += "\nint min(int x, int y) {\nif(x<y) {\nreturn x;\n}\nreturn y;\n}\n\n";
        s += "int max(int x, int y) {\nif(x<y) {\nreturn y;\n}\nreturn x;\n}\n";
        spec.addGlobalDeclaration(Conversion.indentString(s, 2));
    }

    public void translateBlocks() {
        for(AvatarBlock block: avspec.getListOfBlocks()) {
            translateBlock(block);
        }
    }

    public void translateBlock(AvatarBlock _block) {
        UPPAALTemplate template = newBlockTemplate(_block, 0);

        // Behaviour
        makeBehaviour(_block, template);

        // Attributes
        makeAttributes(_block, template);

        // Methods
        makeMethods(_block, template);

    }

    public void translationRelations() {
        AvatarSignal si1, sig2;
        for(AvatarRelation ar: avspec.getRelations()) {
            if (ar.isAsynchronous()) {
                for(int i=0; i<ar.nbOfSignals(); i++) {
                    gatesAsynchronized.add(relationToString(ar, i, false));
                    gatesAsynchronized.add(relationToString(ar, i, true));
                }
            } else {
                for(int i=0; i<ar.nbOfSignals(); i++) {
                    gatesSynchronized.add(relationToString(ar, i));
                    gatesSynchronizedRelations.add(ar);
                }
            }
        }
    }


    // For synchronous relations
    public String relationToString(AvatarRelation _ar, int _index) {
        return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
    }

    // For asynchronous relations
    public String relationToString(AvatarRelation _ar, int _index, boolean inSignal) {
        String signalName;
        AvatarSignal sig;
        AvatarBlock block;

        if (inSignal) {
            sig = _ar.getInSignal(_index);
            block = _ar.getInBlock(_index);
        } else {
            sig = _ar.getOutSignal(_index);
            block = _ar.getOutBlock(_index);
        }

        signalName = block.getName() + "_" + sig.getName() + "__";

        if (sig.isIn()) {
            signalName += "rd";
        } else {
            signalName += "wr";
        }

        return signalName;
    }

    public String signalToUPPAALString(AvatarSignal _as) {
        AvatarRelation ar = avspec.getAvatarRelationWithSignal(_as);
        if (ar == null) {
            return null;
        }

        if (ar.isAsynchronous()) {
            if (_as.isIn()) {
                return relationToString(ar, ar.hasSignal(_as), true);
            } else {
                return relationToString(ar, ar.hasSignal(_as), false);
            }
        } else {
            return relationToString(ar, ar.hasSignal(_as));
        }
    }

    public UPPAALTemplate newBlockTemplate(AvatarBlock _block, int id) {
        UPPAALTemplate template = new UPPAALTemplate();
        if (id != 0) {
            template.setName(_block.getName() + "___" + id);
        } else {
            template.setName(_block.getName());
        }
        spec.addTemplate(template);
        //table.addTClassTemplate(t, template, id);
        return template;
    }

    public void makeAttributes(AvatarBlock _block, UPPAALTemplate _template) {
        AvatarAttribute aa;
        int i;

        for(i=0; i<_block.attributeNb(); i++) {
            aa = _block.getAttribute(i);
            if (aa.isInt()) {
                _template.addDeclaration("int ");
            } else {
                _template.addDeclaration("bool ");
            }
            if (aa.hasInitialValue()) {
                _template.addDeclaration(aa.getName() + " = " + aa.getInitialValue() + ";\n");
            } else {
                _template.addDeclaration(aa.getName() + " = " + aa.getDefaultInitialValue() + ";\n");
            }
        }

        _template.addDeclaration("clock h__;\n");
    }

    public void makeMethods(AvatarBlock _block, UPPAALTemplate _template) {
        String s;
        for(AvatarMethod method: _block.getMethods()) {
            gatesNotSynchronized.add(_block.getName() + "__" + method.getName());
        }
    }


    public void makeNotSynchronized() {
        if (gatesNotSynchronized.size() == 0) {
            return;
        }

        initXY();

        templateNotSynchronized = new UPPAALTemplate();
        templateNotSynchronized.setName("Nonsync__actions");
        spec.addTemplate(templateNotSynchronized);
        UPPAALLocation loc = addLocation(templateNotSynchronized);
        templateNotSynchronized.setInitLocation(loc);
        UPPAALTransition tr;

        spec.addGlobalDeclaration("\n//Declarations used for non synchronized gates\n");

        String action;
        ListIterator iterator = gatesNotSynchronized.listIterator();
        while(iterator.hasNext()) {
            action = (String)(iterator.next());
            tr = addTransition(templateNotSynchronized, loc, loc);
            setSynchronization(tr, action+"?");
            //addGuard(tr, action + TURTLE2UPPAAL.SYNCID + " == 0");
            spec.addGlobalDeclaration("urgent chan " + action + ";\n");
            //spec.addGlobalDeclaration("int " + action + TURTLE2UPPAAL.SYNCID + " = 0;\n");
        }

        if (avspec.hasLossyChannel()) {
            tr = addTransition(templateNotSynchronized, loc, loc);
            setSynchronization(tr, "messageLost__?");
            //addGuard(tr, action + TURTLE2UPPAAL.SYNCID + " == 0");
            spec.addGlobalDeclaration("urgent chan messageLost__;\n");
        }

    }

    public void makeAsynchronous() {
        if (gatesAsynchronized.size() == 0) {
            return;
        }

        initXY();

        templateAsynchronous = new UPPAALTemplate();
        templateAsynchronous.setName("Async__channels");
        spec.addTemplate(templateAsynchronous);
        UPPAALLocation loc = addLocation(templateAsynchronous);
        templateAsynchronous.setInitLocation(loc);
        UPPAALTransition tr, tr1;

        spec.addGlobalDeclaration("\n//Declarations for asynchronous channels\n");
        String action;
        ListIterator iterator = gatesAsynchronized.listIterator();
        while(iterator.hasNext()) {
            action = (String)(iterator.next());
            spec.addGlobalDeclaration("urgent chan " + action + ";\n");
        }


        for(AvatarRelation ar: avspec.getRelations()) {
            if (ar.isAsynchronous() && (ar.nbOfSignals() > 0)) {
                for(int i=0; i<ar.nbOfSignals(); i++) {
                    AvatarSignal sig1 = ar.getOutSignal(i);
                    AvatarSignal sig2 = ar.getInSignal(i);
                    AvatarBlock block = ar.getOutBlock(i);
                    String name0 = block.getName() + "__" + sig1.getName();
                    String enqueue, dequeue;

                    enqueue = "\nvoid enqueue__" + name0 + "(){\n";
                    dequeue = "\nvoid dequeue__" + name0 + "(){\n";

                    // Lists
                    templateAsynchronous.addDeclaration("\n// Asynchronous relations:" + ar.block1.getName() + "/" + sig1.getName() + " -> " + ar.block2.getName() + "/" + sig2.getName() + "\n");
                    templateAsynchronous.addDeclaration("\nint size__" + name0 + " = 0;\n");
                    templateAsynchronous.addDeclaration("int head__" + name0 + " = 0;\n");
                    templateAsynchronous.addDeclaration("int tail__" + name0 + " = 0;\n");

                    int cpt_int = 0;
                    int cpt_bool = 0;
                    String listName;

                    for(AvatarAttribute aa: sig1.getListOfAttributes()) {
                        listName = "list__" + name0 + "_" + (cpt_int+cpt_bool);

                        TraceManager.addDev("* * * -> ATTRIBUTE: "+ aa.toStringType());

                        if (aa.isInt()) {
                            TraceManager.addDev("isInt");
                            templateAsynchronous.addDeclaration("int " + listName + "[" + ar.getSizeOfFIFO() + "];\n");
                            enqueue += "  " + listName +  "[tail__" + name0 + "] = " +  ACTION_INT + cpt_int + ";\n";
                            dequeue += "  " + ACTION_INT + cpt_int + " = " + listName +  "[head__" + name0 + "] " + ";\n";
                            cpt_int ++;
                        } else {
                            TraceManager.addDev("isBool");
                            templateAsynchronous.addDeclaration("bool " + listName + "[" + ar.getSizeOfFIFO() + "];\n");
                            enqueue += "  " + listName +  "[tail__" + name0 + "] = " +  ACTION_BOOL + cpt_bool + ";\n";
                            dequeue += "  " + ACTION_BOOL + cpt_bool + " = " + listName +  "[head__" + name0 + "] " + ";\n";
                            cpt_bool ++;
                        }
                    }
                    enqueue += "  tail__" + name0 + " = (tail__" + name0 + "+1) %" + ar.getSizeOfFIFO() + ";\n";
                    enqueue += "  size__" + name0 + "++;\n";
                    enqueue += "}\n";
                    dequeue += "  head__" + name0 + " = (head__" + name0 + "+1) %" + ar.getSizeOfFIFO() + ";\n";
                    dequeue += "  size__" + name0 + "--;\n";
                    dequeue += "}\n";
                    templateAsynchronous.addDeclaration(enqueue);
                    templateAsynchronous.addDeclaration(dequeue);

                    if (ar.isLossy()) {
                        UPPAALLocation loc1 = addLocation(templateAsynchronous);
                        loc1.setCommitted();
                        tr = addTransition(templateAsynchronous, loc, loc1);
                        setSynchronization(tr, signalToUPPAALString(sig1)+"?");
                        setGuard(tr, "size__" + name0 + " <" +  ar.getSizeOfFIFO());
                        tr = addTransition(templateAsynchronous, loc1, loc);
                        setSynchronization(tr, "messageLost__!");
                        tr = addTransition(templateAsynchronous, loc1, loc);
                        setAssignment(tr, "enqueue__" + name0 + "()");

                    } else {
                        tr = addTransition(templateAsynchronous, loc, loc);
                        setSynchronization(tr, signalToUPPAALString(sig1)+"?");
                        setGuard(tr, "size__" + name0 + " <" +  ar.getSizeOfFIFO());
                        setAssignment(tr, "enqueue__" + name0 + "()");
                    }

                    tr = addTransition(templateAsynchronous, loc, loc);
                    setSynchronization(tr, signalToUPPAALString(sig2)+"!");
                    setAssignment(tr, "dequeue__" + name0 + "()");
                    setGuard(tr, "size__" + name0 + "> 0");

                    if (!ar.isBlocking()) {
                        tr = addTransition(templateAsynchronous, loc, loc);
                        setSynchronization(tr, signalToUPPAALString(sig1)+"?");
                        setGuard(tr, "size__" + name0 + " ==" +  ar.getSizeOfFIFO());
                        setAssignment(tr, "dequeue__" + name0 + "(),\n enqueue__" + name0 + "()");
                    }
                }
            }
        }
    }

    public void makeSynchronized() {
        if (gatesSynchronized.size() == 0) {
            return;
        }

        spec.addGlobalDeclaration("\n//Declarations for synchronous channels\n");

        String action;
        AvatarRelation ar;
        ListIterator iterator = gatesSynchronized.listIterator();
        ListIterator iterator0 = gatesSynchronizedRelations.listIterator();
        while(iterator.hasNext()) {
            action = (String)(iterator.next());
            ar = (AvatarRelation)(iterator0.next());
            if (!(ar.isBroadcast())) {
                spec.addGlobalDeclaration("urgent chan " + action + ";\n");
            } else {
                spec.addGlobalDeclaration("urgent broadcast chan " + action + ";\n");
            }
        }


    }

    public void makeBehaviour(AvatarBlock _block, UPPAALTemplate _template) {
        initXY();
        UPPAALLocation loc = makeBlockInit(_block, _template);

        TraceManager.addDev("Nb of locations=" + _template.getNbOfLocations());

//	translateString.put(_block.getName(), _block.getName()+"__"+_template.getNbOfLocations());

        AvatarStartState ass = _block.getStateMachine().getStartState();

        TraceManager.addDev("Making behaviour of " + _block.getName());

        makeElementBehavior(_block, _template, ass, loc, null, null, false, false);

        TraceManager.addDev("Nb of locations=" + _template.getNbOfLocations());

    }

    public void makeElementBehavior(AvatarBlock _block, UPPAALTemplate _template, AvatarStateMachineElement _elt, UPPAALLocation _previous, UPPAALLocation _end, String _guard, boolean _previousState, boolean _severalTransitions) {
        AvatarActionOnSignal aaos;
        UPPAALLocation loc, loc1;
        UPPAALTransition tr;
        AvatarTransition at;
        int i, j;
        String tmps, tmps0;
        AvatarAttribute aa;
        AvatarState state;
        AvatarRandom arand;

        if (_elt == null) {
            return;
        }

        loc = hash.get(_elt);

        if (loc != null) {
            if (_previous == null) {
                TraceManager.addDev("************************* NULL PREVIOUS !!!!!!!*****************");
            }
            TraceManager.addDev("Linking myself = " + _elt + " to " + loc);
            UPPAALLocation locc = hashChecking.get(_elt);
            if (_elt != null) {
                TraceManager.addDev("In hash:" + _elt + " in location:" + locc);
            }
            tr = addTransition(_template, _previous, loc);
            _previous.setCommitted();
            return;
        }

        // Start state
        if (_elt instanceof AvatarStartState) {
            hash.put(_elt, _previous);
            //if (_elt.getNext(0) != null) {
            makeElementBehavior(_block, _template, _elt.getNext(0), _previous, _end, null, false, false);
            //}
            return;

            // Stop state
        } else if (_elt instanceof AvatarStopState) {
            //tr = addRTransition(template, previous, end);
            hash.put(_elt, _previous);
            return;

            // Random
        } else if (_elt instanceof AvatarRandom) {
            arand = (AvatarRandom)_elt;
            //tr = addRTransition(template, previous, end);
            loc = addLocation(_template);
            tr = addTransition(_template, _previous, loc);
            setAssignment(tr, arand.getVariable() + "=" + arand.getMinValue());
            tr = addTransition(_template, loc, loc);
            setAssignment(tr, arand.getVariable() + "=" + arand.getVariable() + "+1");
            setGuard(tr, arand.getVariable() + "<" + arand.getMaxValue());
            _previous.setCommitted();
            loc.setCommitted();
            hash.put(_elt, _previous);
            loc1 = addLocation(_template);
            tr = addTransition(_template, loc, loc1);
            makeElementBehavior(_block, _template, _elt.getNext(0), loc1, _end, null, false, false);
            return;


            // Avatar Action on Signal
        } else if (_elt instanceof AvatarActionOnSignal) {
            loc = translateAvatarActionOnSignal((AvatarActionOnSignal)_elt, _block, _template, _previous, _guard);
            /*if (_elt.isCheckable()) {
              loc1 = addLocation(_template);
              tr = addTransition(_template, loc, loc1);
              TraceManager.addDev("[CHECKING] +-+-+-+- action on signal " + _elt + " is selected for checking");
              hashChecking.put(_elt, loc);
              loc.unsetOptimizable();
              loc.setCommitted();
              loc = loc1;
              }*/
            if (loc != null) {
                makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false, false);
            }

            // Avatar State
        } else if (_elt instanceof AvatarState) {
            TraceManager.addDev("+ + + + + + + + + + + State " + _elt + ": first handling");
            if (_elt.isCheckable() || unoptStates.contains(_block.getName()+"."+_elt.getName())) {
                TraceManager.addDev("[CHECKING] State " + _elt + " is selected for checking previous=" + _previous);
                _previous.unsetOptimizable();
                _previous.setCommitted();
                loc = addLocation(_template);
                tr = addTransition(_template, _previous, loc);
                hashChecking.put(_elt, _previous);
                hash.put(_elt, _previous);
	        translateString.put(_block.getName()+"."+_elt.getName(),_block.getName()+"."+_previous.name);
                _previous = loc;

            } else {
             //   _previous.unsetOptimizable();
                hash.put(_elt, _previous);
	        translateString.put(_block.getName()+"."+_elt.getName(),_block.getName()+"."+_previous.name);
            }
	    //System.out.println(_block.getName()+"."+_elt.getName()+":"+_block.getName()+"."+_previous.name);
            state = (AvatarState)_elt;


            if (_elt.nbOfNexts() == 0) {
                return;
            }

            // We translate at the same time the state and its next transitions (guard and time + first method call)
            // We assume all nexts are transitions


            LinkedList<AvatarTransition> transitions = new LinkedList<AvatarTransition>();
            for(i=0; i<state.nbOfNexts(); i++) {
                at = (AvatarTransition)(state.getNext(i));
                if (at.hasDelay()) {
                    transitions.add(at);
                }
            }

            if (transitions.size() == 0) {
                // No transition with a delay
                for(i=0; i<state.nbOfNexts(); i++) {
                    at = (AvatarTransition)(state.getNext(i));
                    makeElementBehavior(_block, _template, at, _previous, _end, null, true, (state.nbOfNexts() > 1));
                }
            } else {
                // At least one transition with a delay
                // Reset the clock
                tmps = "h__ = 0";
                loc = addLocation(_template);
                tr = addTransition(_template, _previous, loc);
                setAssignment(tr, tmps);
                _previous.setCommitted();

                LinkedList<UPPAALLocation> locs = new LinkedList<UPPAALLocation>();
                for(i=0; i<state.nbOfNexts(); i++) {
                    at = (AvatarTransition)(state.getNext(i));
                    locs.add(addLocation(_template));
                }

                LinkedList<UPPAALLocation> builtlocs = new LinkedList<UPPAALLocation>();
                LinkedList<AvatarStateMachineElement> elements = new LinkedList<AvatarStateMachineElement>();

                makeStateTransitions(state, locs, transitions, loc, _end, _block, _template, builtlocs, elements);

                for(int k=0; k<builtlocs.size(); k++) {
                    makeElementBehavior(_block, _template, elements.get(k), builtlocs.get(k), _end, null, true, false);
                }
            }


        } else if (_elt instanceof AvatarTransition) {
            at = (AvatarTransition) _elt;
            hash.put(_elt, _previous);
            //TraceManager.addDev("Transition with guard = " + at.getGuard() + " previous=" + _previousState);
            if ((at.getNext(0) instanceof AvatarActionOnSignal) && !(at.hasActions()) && _previousState) {
                if (at.isGuarded()) {
                    makeElementBehavior(_block, _template, _elt.getNext(0), _previous, _end, at.getGuard().toString (), false, false);
                }  else {
                    makeElementBehavior(_block, _template, _elt.getNext(0), _previous, _end, null, false, false);
                }
            } else {
                loc = translateAvatarTransition(at, _block, _template, _previous, _guard, _previousState, _severalTransitions);
                makeElementBehavior(_block, _template, _elt.getNext(0), loc, _end, null, false, false);
            }

        } else {
            TraceManager.addDev("Reached end of elseif in block behaviour...");
            return;
        }
    }


    public UPPAALLocation translateAvatarActionOnSignal(AvatarActionOnSignal _aaos, AvatarBlock _block, UPPAALTemplate _template, UPPAALLocation _previous, String _guard) {

        String [] ss = manageSynchro(_block, _aaos);
        UPPAALLocation loc = addLocation(_template);
        UPPAALTransition tr = addTransition(_template, _previous, loc);
        if (_guard != null) {
            String tmpg = convertGuard(_guard);
            addGuard(tr, tmpg);
        }
        setSynchronization(tr, ss[0]);
        addAssignment(tr, ss[1]);


        TraceManager.addDev("* * * * * * * * * * * * * * * * Action on signal " + _aaos.getSignal().getName());

        if (_aaos.isCheckable()) {
            TraceManager.addDev("[CHECKING] Action on signal ??? " + _aaos.getSignal().getName());
            if (hashChecking.get(_aaos) == null) {
                UPPAALLocation loc1 = addLocation(_template);
                UPPAALTransition tr1 = addTransition(_template, loc, loc1);
                TraceManager.addDev("[CHECKING] +-+-+-+- action on signal " + _aaos + " is selected for checking");
                hashChecking.put(_aaos, loc);
                loc.unsetOptimizable();
                loc.setCommitted();
                loc = loc1;
                TraceManager.addDev("[CHECKING] Added");
                //loc.unsetOptimizable();
                /*} else {
                  UPPAALLocation loc1 = (UPPAALLocation)(hashChecking.get(_aaos));
                  UPPAALTransition tr1 = addTransition(_template, loc, loc1);
                  loc = loc1;
                  loc.setCommitted();*/
            }
        }

        return loc;
    }

    public UPPAALLocation translateAvatarTransition(AvatarTransition _at, AvatarBlock _block, UPPAALTemplate _template, UPPAALLocation _previous, String _guard, boolean _previousState, boolean _severalTransitions) {
        UPPAALLocation loc = _previous;
        UPPAALLocation loc1;
        UPPAALTransition tr;
        String tmps;
        int i;

        boolean madeTheChoice = false;

        /*if (_at.isGuarded()) {
          TraceManager.addDev("Guard=" + _at.getGuard());
          }*/

        /*if (_severalTransitions) {
          TraceManager.addDev("SEVERAL TRANSITIONS");
          } else {
          TraceManager.addDev("ONE TRANSITION");
          }*/

        if (_at.isGuarded()) {
            //_previous.setCommitted();
            loc1 = addLocation(_template);
            tr = addTransition(_template, _previous, loc1);
            tmps = convertGuard(_at.getGuard().toString ());
            setGuard(tr, tmps);
            TraceManager.addDev("MAKE CHOICE from guard");
            setSynchronization(tr, "makeChoice!");
            madeTheChoice = true;
            loc = loc1;
        }


        if (_at.hasDelay() && !_previousState) {
            //TraceManager.addDev("Making time interval min=" + _at.getMinDelay());
            loc = makeTimeInterval(_template, loc, _at.getMinDelay(), _at.getMaxDelay());
            madeTheChoice = true;
        }

        if (_at.hasCompute()) {
            loc = makeTimeInterval(_template, loc, _at.getMinCompute(), _at.getMaxCompute());
            _previousState = false;
            madeTheChoice = true;
        }

        if (_at.hasActions()) {
            for(i=0; i<_at.getNbOfAction(); i++) {
                TraceManager.addDev("Adding Action :" + _at.getAction(i));
                tmps = _at.getAction(i).toString ();

                AvatarAction tmpAction = AvatarTerm.createActionFromString (_block, tmps);

                // Setting a variable
                if (tmpAction.isABasicVariableSetting ()) {
                    loc1 = addLocation(_template);
                    //loc.setCommitted();
                    tr = addTransition(_template, loc, loc1);
                    setAssignment(tr, tmps);
                    if ((_severalTransitions) && (!madeTheChoice)) {
                        TraceManager.addDev("MAKE CHOICE from var");
                        setSynchronization(tr, "makeChoice!");
                    } else {
                        loc.setCommitted();
                    }
                    madeTheChoice = true;
                    loc = loc1;
                // Method call
                } else {
                    //TraceManager.addDev("Found method call:" + tmps);
                    
                    AvatarTermFunction funcCall = null;
                    if (tmpAction instanceof AvatarTermFunction)
                        funcCall = (AvatarTermFunction) tmpAction;
                    else
                        funcCall = (AvatarTermFunction) ((AvatarActionAssignment) tmpAction).getRightHand ();

                    loc1 = addLocation(_template);
                    tr = addTransition(_template, loc, loc1);

                    if ((i ==0) && (_previousState)) {
                        setGuard(tr, _guard);
                    } else {
                        loc.setUrgent();
                    }
                    setSynchronization(tr, funcCall.getMethod ().getName () + "!");
                    madeTheChoice = true;
                    makeMethodCall(_block, tr, funcCall);
                    loc = loc1;
                }
            }
        } else {
            // make choice!
            if ((!madeTheChoice) && (_severalTransitions)) {
                loc1 = addLocation(_template);
                tr = addTransition(_template, loc, loc1);
                TraceManager.addDev("MAKE CHOICE from end");
                setSynchronization(tr, "makeChoice!");
                loc = loc1;
            }
        }
        hash.put(_at, loc);
        return loc;
    }

    // Start from a given state / loc, and derive progressively all locations
    // _transitions contains timing transitions
    public void makeStateTransitions(AvatarState _state, LinkedList<UPPAALLocation> _locs, LinkedList<AvatarTransition> _transitions, UPPAALLocation _loc, UPPAALLocation _end, AvatarBlock _block, UPPAALTemplate _template, LinkedList<UPPAALLocation> _builtlocs, LinkedList<AvatarStateMachineElement> _elements) {
        // Make the current state
        // Invariant
        String inv = "";
        int cpt = 0;
        int i;
        UPPAALLocation loc1, loc2;
        String tmps, tmps0;
        AvatarTransition at;
        UPPAALLocation loc;
        UPPAALTransition tr, tr1;
        AvatarActionOnSignal aaos;


        for(AvatarTransition att: _transitions) {
            if (cpt == 0) {
                inv += "h__ <= " + att.getMaxDelay();
            } else {
                inv = "(" + inv + ") && (h__ <= " +att.getMaxDelay() + ")";
            }
            cpt ++;
        }

        _loc.setInvariant(inv);

        // Put all logical transitions
        // Choice between transitions
        // If the first action is a method call, or not action but the next one is an action on a signal:
        // Usual translation way i.e. use the action as the UPPAAL transition trigger
        // Otherwise introduce a fake choice action
        //j = 0;
        UPPAALLocation locend;
        for(i=0; i<_state.nbOfNexts(); i++) {
            at = (AvatarTransition)(_state.getNext(i));
            locend = _locs.get(i);

            if (!(_transitions.contains(at))) {

                // Computing guard
                if (at.isGuarded()) {
                    tmps = convertGuard(at.getGuard().toString ());
                } else {
                    tmps = "";
                }

                if (at.hasCompute()) {
                    tr = addTransition(_template, _loc, locend);
                    setGuard(tr, tmps);
                    setSynchronization(tr, CHOICE_ACTION + "!");
                    if (_template.nbOfTransitionsExitingFrom(locend) == 0) {
                        loc1 = translateAvatarTransition(at, _block, _template, locend, "", true, (_state.nbOfNexts() > 1));
                        _builtlocs.add(loc1);
                        _elements.add(at.getNext(0));
                    }

                } else if (at.hasActions()) {
                    tmps0 = at.getAction(0).toString ();
                    if (AvatarTerm.createActionFromString (_block, tmps0).isAVariableSetting ()) {
                        // We must introduce a fake action
                        tr = addTransition(_template, _loc, locend);
                        if (tmps != null) {
                            setGuard(tr, tmps);
                        }
                        setSynchronization(tr, CHOICE_ACTION + "!");
                        if (_template.nbOfTransitionsExitingFrom(locend) == 0) {
                            loc1 = translateAvatarTransition(at, _block, _template, locend, "", true, (_state.nbOfNexts() > 1));
                            _builtlocs.add(loc1);
                            _elements.add(at.getNext(0));
                        }

                    } else {
                        // We make the translation in the next transition
                        loc1 = translateAvatarTransition(at, _block, _template, _loc, "", true, (_state.nbOfNexts() > 1));
                        tr = addTransition(_template, loc1, locend);
                        loc1.setCommitted();
                        if (!(_elements.contains(at.getNext(0)))) {
                            _builtlocs.add(locend);
                            _elements.add(at.getNext(0));
                        }
                    }
                } else {
                    // Must consider whether the transition leads to an action on a signal
                    if (at.followedWithAnActionOnASignal()) {
                        aaos = (AvatarActionOnSignal)(at.getNext(0));
                        if (tmps == null) {
                            tmps = "";
                        }
                        loc1 = translateAvatarActionOnSignal(aaos, _block, _template, _loc, tmps);

                        loc2 = hash.get(aaos);
                        if (loc2 == null) {
                            hash.put(aaos, loc1);
			    translateString.put(_block.getName()+"."+aaos.getName(),_block.getName()+"."+loc1.name);
                        }

                        tr = addTransition(_template, loc1, locend);
                        loc1.setCommitted();
                        if (!(_elements.contains(at.getNext(0).getNext(0)))) {
                            _builtlocs.add(locend);
                            _elements.add(at.getNext(0).getNext(0));
                        }

                        /*if (aaos.isCheckable()) {
                          TraceManager.addDev("--------- action on signal " + aaos + " is selected for checking");
                          if (hashChecking.get(aaos) == null) {
                          TraceManager.addDev("[CHECKING] --------- action on signal " + aaos + " is selected for checking");
                          hashChecking.put(aaos, locend);
                          locend.unsetOptimizable();
                          } else {
                          // Enforce the new information
                          TraceManager.addDev("[CHECKING] ---------------- Already set as checkable -> enforcing new information");
                          hashChecking.remove(aaos);
                          hashChecking.put(aaos, locend);
                          locend.unsetOptimizable();
                          }
                          }*/


                    } else {
                        // If this is not the only transition
                        // We must introduce a fake action
                        tr = addTransition(_template, _loc, locend);
                        setGuard(tr, tmps);
                        setSynchronization(tr, CHOICE_ACTION + "!");
                        // Useless to translate the next transition, we directly jump to after the transition
                        if (!(_elements.contains(at.getNext(0)))) {
                            _builtlocs.add(locend);
                            _elements.add(at.getNext(0));
                        }
                    }
                }
            }
        }


        // Make the nexts transitions / put all timing transitions
        // Consider all possibilities

        if (_transitions.size() == 0) {
            return;
        }

        LinkedList<AvatarTransition> cloneList;

        for(i=0; i<_transitions.size(); i++) {
            cloneList = new LinkedList<AvatarTransition>();
            cloneList.addAll(_transitions);
            cloneList.remove(i);
            currentX = currentX + STEP_LOOP_X;
            loc1 = addLocation(_template);
            tr = addTransition(_template, _loc, loc1);
            addGuard(tr, "h__ >= " + _transitions.get(i).getMinDelay());
            makeStateTransitions(_state, _locs, cloneList, loc1, _end, _block, _template,  _builtlocs, _elements);
            currentX = currentX - STEP_LOOP_X;
        }


    }

    public void makeMethodCall(AvatarBlock _block, UPPAALTransition _tr, AvatarTermFunction action) {
        int j;
        AvatarAttribute aa;
        String result = "";
        int nbOfInt = 0;
        int nbOfBool = 0;
        String tmps;

        TraceManager.addDev("Making method call:" + action.toString ());

        String mc = "";
        AvatarBlock block = _block;
        AvatarMethod avMethod = action.getMethod ();
        String method = avMethod.getName ();

        block = _block.getBlockOfMethodWithName(method);

        if (block != null) {
            mc = block.getName() + "__" + method + "!";
        }

        TraceManager.addDev("Method name:" + mc);

        setSynchronization(_tr, mc);
        LinkedList<AvatarTerm> arguments = action.getArgs ().getComponents ();
        for(AvatarTerm arg: arguments) {
            if (!(arg instanceof AvatarAttribute))
                continue;

            aa = (AvatarAttribute) arg;
            if ((nbOfInt > 0) || (nbOfBool > 0))
                result = result + ",\n";

            if (aa.isInt()) {
                result = result + ACTION_INT + nbOfInt + " =" + aa.getName();
                nbOfInt ++;
            } else {
                result = result + ACTION_BOOL + nbOfBool + " =" + aa.getName();
                nbOfBool ++;
            }
        }

        if (result.length() > 0) {
            setAssignment(_tr, result);
        }

        nbOfIntParameters = Math.max(nbOfIntParameters, nbOfInt);
        nbOfBooleanParameters = Math.max(nbOfBooleanParameters, nbOfBool);
    }

    public UPPAALLocation makeTimeInterval(UPPAALTemplate _template, UPPAALLocation _previous, String _minint, String _maxint) {
        UPPAALLocation loc, loc1;
        UPPAALTransition tr, tr1;
        loc1 = addLocation(_template);
        _previous.setCommitted();
        tr1 = addTransition(_template, _previous, loc1);
        setAssignment(tr1, "h__ = 0");
        loc = addLocation(_template);
        tr = addTransition(_template, loc1, loc);
        loc1.setInvariant("(h__ <= (" + _maxint + "))");
        addGuard(tr, "(h__ >= (" + _minint + "))");
        return loc;
    }


    public UPPAALLocation makeBlockInit(AvatarBlock _block, UPPAALTemplate _template) {
        currentX = currentX - 100;
        UPPAALLocation loc1 = addLocation(_template);
        currentX = currentX + 100;
        _template.setInitLocation(loc1);
        return loc1;
    }

    public String [] manageSynchro(AvatarBlock _block, AvatarActionOnSignal _aaos) {
        AvatarSignal as = _aaos.getSignal();
        return manageSynchroSynchronous(_block, _aaos);


        /*if (avspec.isASynchronousSignal(as)) {

          } else {
          return  manageSynchroAsynchronous(_block, _aaos);
          }*/
    }


    public String [] manageSynchroSynchronous(AvatarBlock _block, AvatarActionOnSignal _aaos) {
        String []result = new String[2];
        String val;

        int nbOfInt = 0;
        int nbOfBool = 0;

        AvatarAttribute aa;

        result[0] = "";
        result[1] = "";

        String signal = signalToUPPAALString(_aaos.getSignal());

        if (signal == null) {
            if (_aaos.isSending()) {
                CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Signal " + _aaos.getSignal().getName() + " is used in block " + _block.getName() + ", but not connected to any channel. Ignoring the ssending of this signal");
                warnings.add(ce);
            } else {
                CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Signal " + _aaos.getSignal().getName() + " is used in block " + _block.getName() + ", but not connected to any channel. Ignoring the receiving of this signal");
                warnings.add(ce);
            }
            return result;
        }

        if (_aaos.isSending()) {
            signal += "!";
        } else {
            signal += "?";
        }

        result[0] = signal;

        //TraceManager.addDev("Nb of params on signal " + signal + ":" + _aaos.getNbOfValues());

        for(int i=0; i<_aaos.getNbOfValues(); i++) {
            val = _aaos.getValue(i);
            aa = _block.getAvatarAttributeWithName(val);
            if (aa != null) {
                if (aa.isInt()) {
                    if (_aaos.isSending()) {
                        result[1] = result[1] + ACTION_INT + nbOfInt + " = " + aa.getName();
                    } else {
                        result[1] = result[1] + aa.getName() + " = " + ACTION_INT + nbOfInt;
                    }
                    nbOfInt ++;
                } else {
                    if (_aaos.isSending()) {
                        result[1] = result[1] + ACTION_BOOL + nbOfBool + " = " + aa.getName();
                    } else {
                        result[1] = result[1] + aa.getName() + " = " + ACTION_BOOL + nbOfBool;
                    }
                    nbOfBool ++;
                }
                if (i != (_aaos.getNbOfValues() -1)) {
                    result[1] += ", ";
                }
            } else {
                // Try to see whether this is a numerical value and in a sending element
                if (_aaos.isSending()) {
                    if (val.toLowerCase().compareTo("true") == 0) {
                        result[1] = result[1] + ACTION_BOOL + nbOfBool + " = " + val;
                        nbOfBool++;
                    } else if (val.toLowerCase().compareTo("false") == 0) {
                        result[1] = result[1] + ACTION_BOOL + nbOfBool + " = " + val;
                        nbOfBool++;
                    } else {
                        try {
                            int myint = Integer.decode(val);
                            result[1] = result[1] + ACTION_INT + nbOfInt + " = " + val;
                            nbOfInt++;
                        } catch (Exception e) {
                            TraceManager.addDev("Null param:" + _aaos.getValue(i));
                        }
                    }
                } else {
                    TraceManager.addDev("Null param:" + _aaos.getValue(i));
                }
            }
        }

        nbOfIntParameters = Math.max(nbOfIntParameters, nbOfInt);
        nbOfBooleanParameters = Math.max(nbOfBooleanParameters, nbOfBool);

        return result;
    }

    public String [] manageSynchroAsynchronous(AvatarBlock _block, AvatarActionOnSignal _aaos) {
        String []result = new String[2];
        String val;

        int nbOfInt = 0;
        int nbOfBool = 0;

        AvatarAttribute aa;

        result[0] = "";
        result[1] = "";

        String signal = signalToUPPAALString(_aaos.getSignal());

        if (signal == null) {
            return result;
        }

        if (_aaos.isSending()) {
            signal += "!";
        } else {
            signal += "?";
        }

        result[0] = signal;

        for(int i=0; i<_aaos.getNbOfValues(); i++) {
            val = _aaos.getValue(i);
            aa = _block.getAvatarAttributeWithName(val);
            if (aa != null) {
                if (aa.isInt()) {
                    if (_aaos.isSending()) {
                        result[1] = result[1] + ACTION_INT + nbOfInt + " = " + aa.getName();
                    } else {
                        result[1] = result[1] + aa.getName() + " = " + ACTION_INT + nbOfInt;
                    }
                    nbOfInt ++;
                } else {
                    if (_aaos.isSending()) {
                        result[1] = result[1] + ACTION_BOOL + nbOfBool + " = " + aa.getName();
                    } else {
                        result[1] = result[1] + aa.getName() + " = " + ACTION_BOOL + nbOfBool;
                    }
                    nbOfBool ++;
                }
                if (i != (_aaos.getNbOfValues() -1)) {
                    result[1] += ", ";
                }
            }
        }

        nbOfIntParameters = Math.max(nbOfIntParameters, nbOfInt);
        nbOfBooleanParameters = Math.max(nbOfBooleanParameters, nbOfBool);

        return result;
    }

    public UPPAALLocation addLocation(UPPAALTemplate _template) {
        UPPAALLocation loc = new UPPAALLocation();
        loc.idPoint.x = currentX;
        loc.idPoint.y = currentY;
        loc.namePoint.x = currentX + NAME_X;
        loc.namePoint.y = currentY + NAME_Y;
        _template.addLocation(loc);
        currentX += STEP_X;
        currentY += STEP_Y;
        return loc;
    }

    public void addRandomNailPoint(UPPAALTransition tr) {
        int x = 0, y = 0;
        if (tr.sourceLoc != tr.destinationLoc) {
            x = ((tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2) - 25 + (int)(50.0 * Math.random());
            y = ((tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2) - 25 + (int)(50.0 * Math.random());
            tr.points.add(new Point(x, y));
        }
    }

    public UPPAALTransition addTransition(UPPAALTemplate template, UPPAALLocation loc1, UPPAALLocation loc2) {
        UPPAALTransition tr = new UPPAALTransition();
        tr.sourceLoc = loc1;
        tr.destinationLoc = loc2;
        template.addTransition(tr);
        // Nails?
        // Adding random intermediate nail
        addRandomNailPoint(tr);
        return tr;
    }


    public void setSynchronization(UPPAALTransition tr, String s) {
        tr.synchronization = modifyString(s);
        tr.synchronizationPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + SYNCHRO_X;
        tr.synchronizationPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + SYNCHRO_Y;
    }

    public void addGuard(UPPAALTransition tr, String s) {
        if ((tr.guard == null) || (tr.guard.length() < 2)){
            tr.guard = modifyString(s);
        } else {
            tr.guard = "(" + tr.guard + ")&&(" + modifyString(s) + ")";
        }
        tr.guardPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + GUARD_X;
        tr.guardPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + GUARD_Y;
    }

    public void setInvariant(UPPAALLocation loc, String s) {
        loc.setInvariant(modifyString(s));
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

    public void addAssignment(UPPAALTransition tr, String s) {
        if (s.length() <1) {
            return;
        }
        if ((tr.assignment == null) || (tr.assignment.length() < 2)){
            tr.assignment = modifyString(s);
        } else {
            tr.assignment = tr.assignment + ",\n " + modifyString(s);
        }

        tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
        tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
    }

    public void setEndAssignment(UPPAALTransition tr) {
        tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
        tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
    }

    public void makeSystem() {
        ListIterator iterator = spec.getTemplates().listIterator();
        UPPAALTemplate template;
        String system = "system ";
        String dec = "";
        int id = 0;
        int i;

        while(iterator.hasNext()) {
            template = (UPPAALTemplate)(iterator.next());
            if (template.getNbOfTransitions() > 0) {
                dec += template.getName() + "__" + id + " = " + template.getName() + "();\n";
                if (id > 0) {
                    system += ",";
                }
                system += template.getName() + "__" + id;
                id++;
            }
        }

        system += ";";

        spec.addInstanciation(dec+system);
    }

    public String modifyString(String _input) {
        try {
            //_input = Conversion.replaceAllString(_input, "&&", "&amp;&amp;");
            //_input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
            //_input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
            //_input = Conversion.replaceAllChar(_input, '<', "&lt;");
            //_input = Conversion.replaceAllChar(_input, '>', "&gt;");
            _input = Conversion.replaceAllStringNonAlphanumerical(_input, "mod", "%");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception when changing binary operator in " + _input);
        }
        //System.out.println("Modified string=" + _input);
        return _input;
    }

    public String convertGuard(String g) {
        if (g == null) {
            return "";
        }

        if (g.compareTo("null") == 0) {
            return "";
        }
        String action = Conversion.replaceAllChar(g, '[', "");
        action = Conversion.replaceAllChar(action, ']', "");
        return modifyString(action.trim());
    }

    public AvatarBlock getBlockFromReferenceObject(Object _o) {
        return avspec.getBlockFromReferenceObject(_o);
    }

    public String getUPPAALIdentification(Object _o) {
        if (avspec == null) {
            return null;
        }

        String ret = "";

        AvatarBlock block = avspec.getBlockFromReferenceObject(_o);

        if (block != null) {
            UPPAALTemplate temp = spec.getTemplateByName(block.getName());
            int index = getIndexOfTranslatedTemplate(temp);
            if (temp != null) {
                ret += block.getName() + "__" + index;

                AvatarStateMachineElement asme = avspec.getStateMachineElementFromReferenceObject(_o);
                if (asme != null) {
                    UPPAALLocation loc = hashChecking.get(asme);
                    if (loc != null) {
                        ret += "." + loc.name;
                    } else {
                        TraceManager.addDev("Unknown element in hash checking");
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        return ret;


    }

    public int getIndexOfTranslatedTemplate(UPPAALTemplate _temp) {
        ListIterator iterator = spec.getTemplates().listIterator();
        UPPAALTemplate template;
        String system = "system ";
        String dec = "";
        int id = 0;
        int i;

        while(iterator.hasNext()) {
            template = (UPPAALTemplate)(iterator.next());
            if (template == _temp) {
                return id;
            }
            if (template.getNbOfTransitions() > 0) {
                id++;
            }
        }

        return -1;
    }

}
