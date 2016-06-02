/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class AvatarModelChecker
   * Avatar Model Checker
   * Creation: 31/05/2016
   * @version 1.0 31/05/2016
   * @author Ludovic APVRILLE
   * @see
   */


package avatartranslator.modelchecker;

import java.util.*;

import avatartranslator.*;
import myutil.*;

public class AvatarModelChecker implements Runnable {
    private final static int DEFAULT_NB_OF_THREADS = 4;
    private final static int SLEEP_DURATION = 500;

    private AvatarSpecification spec;
    private int nbOfThreads = DEFAULT_NB_OF_THREADS;
    private int nbOfCurrentComputations;
    private boolean stoppedBeforeEnd;



    // ReachabilityGraph
    private Map<Integer, SpecificationState> states;
    private List<SpecificationState> pendingStates;
    private List<SpecificationLink> links;

    public AvatarModelChecker(AvatarSpecification _spec) {
        spec = _spec;
    }

    public void startModelChecking() {
        stoppedBeforeEnd = false;

        // Remove timers, composite states, randoms
        TraceManager.addDev("Reworking Avatar specification");
        spec.removeTimers();
        spec.removeCompositeStates();
        spec.removeRandoms();
        spec.makeFullStates();


        TraceManager.addDev("Preparing Avatar specification");
        prepareStates();
        prepareTransitions();

        TraceManager.addDev("Starting the model checking");
        startModelChecking(DEFAULT_NB_OF_THREADS);
        TraceManager.addDev("Model checking done");
    }

    public boolean hasBeenStoppedBeforeCompletion() {
        return stoppedBeforeEnd;
    }




    public void startModelChecking(int _nbOfThreads) {
        nbOfThreads = _nbOfThreads;

        // Init data stuctures
        states = Collections.synchronizedMap(new HashMap<Integer, SpecificationState>());
        pendingStates = Collections.synchronizedList(new LinkedList<SpecificationState>());
        links = Collections.synchronizedList(new ArrayList<SpecificationLink>());

        // Compute initial state
        SpecificationState initialState = new SpecificationState();
        initialState.setInit(spec);
        TraceManager.addDev("initialState=" + initialState.toString());

        states.put(initialState.hashValue, initialState);
        pendingStates.add(initialState);

        computeAllStates();

        // All done
    }

    public void stopModelChecking() {
        emptyPendingStates();
        stoppedBeforeEnd = true;
    }

    private void computeAllStates() {
        int i;
        Thread []ts = new Thread[nbOfThreads];

        for(i=0; i<nbOfThreads; i++) {
            ts[i] = new Thread(this);
            ts[i].start();
        }

        for(i=0; i<nbOfThreads; i++) {
            try {
                ts[i].join();} catch (Exception e){}
        }
    }

    public void run() {
        SpecificationState s;

        boolean go = true;
        while(go) {
            // Pickup a state
            s = pickupState();

            if (s == null) {
                // Terminate
                go = false;
            } else {
                // Handle one given state
                computeAllStatesFrom(s);
                // Release the computation
                releasePickupState();
            }
        }
    }

    private synchronized SpecificationState pickupState() {
        int size = pendingStates.size();
        while (size == 0) {
            if (nbOfCurrentComputations == 0) {
                return null;
            } else {
                try {
                    wait(SLEEP_DURATION);
                } catch (Exception e) {}
            }
        }

        SpecificationState s = pendingStates.get(0);
        pendingStates.remove(0);
        nbOfCurrentComputations ++;
        return s;
    }

    private synchronized void releasePickupState() {
        nbOfCurrentComputations --;
        notifyAll();
    }

    private synchronized int getNbOfComputations() {
        return nbOfCurrentComputations;
    }

    private synchronized void emptyPendingStates() {
        pendingStates.clear();
        nbOfCurrentComputations = 0;
    }


    private void computeAllStatesFrom(SpecificationState _ss) {
        int cpt;

        // For each block, get the list of possible transactions
        ArrayList<SpecificationTransition> transitions = new ArrayList<SpecificationTransition>();

        // At first, do not merge synchronous transitions
        // Simply get basics transitions
        cpt = 0;
        for(AvatarBlock block: spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            SpecificationBlock sb = _ss.blocks[cpt];
            AvatarStateElement ase = asm.allStates[sb.values[SpecificationBlock.STATE_INDEX]];

            for(AvatarStateMachineElement elt: ase.getNexts()) {
                if (elt instanceof AvatarTransition) {
                    handleAvatarTransition((AvatarTransition)elt, block, sb, cpt, transitions);
                }
            }

            cpt ++;
        }

	// All locally executable transitions are now gathered.
	// We simply need to select the one that are executable
	// Two constraints: synchronous transactions must have a counter part
	// then, we select only the transitions which clock intervals are within the lowest clock interval

	// Reworking sync/non sync. We create one new transition for all possible synchros, and we remove the ones
	// with only one synchro
	ArrayList<SpecificationTransition> newTransitions = new ArrayList<SpecificationTransition>();
	for(SpecificationTransition tr: transitions) {
	    if (tr.getType() == AvatarTransition.TYPE_SEND_SYNC) {
		for(SpecificationTransition tro: transitions) {
		    if (tro.getType() == AvatarTransition.TYPE_RECV_SYNC) {
			SpecificationTransition newT = computeSynchronousTransition(tr, tro);
			if (newT != null) newTransitions.add(newT);
		    }
		}
	    } else if (tr.getType() == AvatarTransition.TYPE_ACTION) {
		newTransitions.add(tr);
	    }
	}
	transitions = newTransitions;

	// Selecting only the transactions within the smallest clock interval
	int clockMin=Integer.MAX_VALUE, clockMax=0;
	for(SpecificationTransition tr: transitions) {
	    clockMin = Math.min(clockMin, tr.clockMin);
	    clockMax = Math.min(clockMin, tr.clockMin);
	}
	TraceManager.addDev("Selected clock interval:" + clockMin + "," + clockMax);

	newTransitions = new ArrayList<SpecificationTransition>();
	for(SpecificationTransition tr: transitions) {
	    if (tr.clockMin  < clockMax) {
		tr.clockMax = clockMax;
		newTransitions.add(tr);
	    }
	}
	transitions = newTransitions;
	
        // For each realizable transition
        //   Make it, reset clock of the involved blocks to 0, increase clockmin/clockhmax of each block
        //   compute new state, and compare with existing ones
        //   If not a new state, create the link rom the previous state to the new one
        //   Otherwise create the new state and its link, and add it to the pending list of states
	for(SpecificationTransition tr: transitions) {
	    // Make tr
	    // to do so, must create a new state
	    SpecificationState newState = _ss.advancedClone();

	    // For non impacted blocks, increase their clock value, or set their clock to 0
	    newState.increaseClockOfBlocksExcept(tr);

	    // Impact the variable of the state, either by executing actions, or by
	    // doing the synchronization
	    String action = executeTransition(_ss, newState, tr);

	    // Compute the hash of the new state, and create the link to the right next state
	    SpecificationLink link = new SpecificationLink();
	    link.originState = _ss;
	    link.action = action;
	    newState.computeHash();
	    SpecificationState similar = states.get(newState.getHash());
	    if (similar == null) {
		//  Unknown state
		states.put(newState.getHash(), newState);
		pendingStates.add(newState);
		link.destinationState = newState;
		
	    } else {
		// Create a link from former state to the existing one
		link.destinationState = similar;
	    }
	    links.add(link);	    
	}
    }

    private void handleAvatarTransition(AvatarTransition _at, AvatarBlock _block, SpecificationBlock _sb,  int _indexOfBlock, ArrayList<SpecificationTransition> _transitionsToAdd) {
        if (_at.type == AvatarTransition.UNDEFINED) {
            return;
        }

        // Must see whether the guard is ok or not
        if (_at.isGuarded()) {
            // Must evaluate the guard
            String guard = _at.getGuard().toString ();
            String s = Conversion.replaceAllString(guard, "[", "").trim();
            s = Conversion.replaceAllString(s, "]", "").trim();
            boolean guardOk = evaluateBoolExpression(s, _block, _sb);
            TraceManager.addDev("guard ok=" + guardOk);
            if (!guardOk) {
                return;
            }
        }

        SpecificationTransition st = new SpecificationTransition();
	st.init(1, _at, _block, _sb, _indexOfBlock);
	
	// Must compute the clockmin and clockmax values
	st.clockMin = evaluateIntExpression(_at.getMinDelay(), _block, _sb) - _sb.values[SpecificationBlock.CLOCKMIN_INDEX];
	st.clockMax = evaluateIntExpression(_at.getMaxDelay(), _block, _sb) - _sb.values[SpecificationBlock.CLOCKMAX_INDEX];
    }


    private void prepareStates() {
        // Put states in a list
        for(AvatarBlock block: spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null) {
                asm.makeAllStates();
            }
        }
    }

    private void prepareTransitions() {
        // Compute the id of each transition
        // Assumes the allStates list has been computed in AvatarStateMachine
        // Assumes that it is only after states that transitions have non empty


        for(AvatarBlock block: spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null) {
                for(int i=0; i<asm.allStates.length; i++) {
                    for(int j=0; j<asm.allStates[i].nbOfNexts(); j++) {
                        AvatarStateMachineElement elt = asm.allStates[i].getNext(j);
                        if (elt instanceof AvatarTransition) {
                            AvatarTransition at = (AvatarTransition)elt;
                            AvatarStateMachineElement next = at.getNext(0);
                            if (next != null) {
                                if (next instanceof AvatarActionOnSignal) {
                                    AvatarSignal sig = ((AvatarActionOnSignal)next).getSignal();
                                    if (sig != null) {
                                        if (sig.isIn()) {
                                            at.type = AvatarTransition.TYPE_RECV_SYNC;
                                        } else {
                                            at.type = AvatarTransition.TYPE_SEND_SYNC;
                                        }
                                    }
                                } else {
                                    at.type = AvatarTransition.TYPE_ACTION;
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    public boolean evaluateBoolExpression(String _expr, AvatarBlock _block, SpecificationBlock _sb) {
        String act = _expr;
        int cpt = 0;
        for(AvatarAttribute at: _block.getAttributes()) {
            String val = "";
            if (at.isInt()) {
                val = ""+_sb.values[cpt+SpecificationBlock.ATTR_INDEX];
                if (val.startsWith("-")) {
                    val = "(0" + val + ")";
                }
            } else if (at.isBool()) {
		if (_sb.values[cpt+SpecificationBlock.ATTR_INDEX] == 0) {
		    val = "false";
		} else {
		    val = "true";
		}
	    }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, _block.getAttribute(cpt).getName(), val);
            cpt ++;
        }

        BoolExpressionEvaluator bee = new BoolExpressionEvaluator();

        if (act.trim().startsWith("100")) {
            TraceManager.addDev("Current block " + _block.getName());
        }

        boolean result = bee.getResultOf(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error: " + bee.getError());
        }

        //TraceManager.addDev("Result of " + _expr + " = " + result);
        return result;
    }

    public int evaluateIntExpression(String _expr, AvatarBlock _block, SpecificationBlock _sb) {
	String act = _expr;
        int cpt = 0;
	for(AvatarAttribute at: _block.getAttributes()) {
            String val = "";
            if (at.isInt()) {
                val = ""+_sb.values[cpt+SpecificationBlock.ATTR_INDEX];
                if (val.startsWith("-")) {
                    val = "(0" + val + ")";
                }
            } else if (at.isBool()) {
		if (_sb.values[cpt+SpecificationBlock.ATTR_INDEX] == 0) {
		    val = "false";
		} else {
		    val = "true";
		}
	    }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, _block.getAttribute(cpt).getName(), val);
            cpt ++;
        }
        
        TraceManager.addDev("Evaluating Int expression: " + act);

        return (int)(new IntExpressionEvaluator().getResultOf(act));
    }

    private SpecificationTransition computeSynchronousTransition(SpecificationTransition sender, SpecificationTransition receiver) {
	AvatarTransition trs = sender.transitions[0];
	AvatarTransition trr = receiver.transitions[0];

	AvatarStateMachineElement asmes, asmer;
	asmes = trs.getNext(0);
	asmer = trs.getNext(0);
	if ((asmes == null) || (asmer == null)) return null;
	if (!(asmes instanceof AvatarActionOnSignal)) return null;
	if (!(asmer instanceof AvatarActionOnSignal)) return null;

	AvatarSignal ass = ((AvatarActionOnSignal)asmes).getSignal();
	AvatarSignal asr = ((AvatarActionOnSignal)asmer).getSignal();

	if (spec.areSynchronized(ass, asr)) {
	    SpecificationTransition st = new SpecificationTransition();
	    st.makeFromTwoSynchronous(sender, receiver);
	    return st;
	}
				     	
	return null;
	
    }

    private String executeTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
	return "not implemented";
    }

    

}
