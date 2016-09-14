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

public class AvatarModelChecker implements Runnable, myutil.Graph {
    private final static int DEFAULT_NB_OF_THREADS = 12;
    private final static int SLEEP_DURATION = 500;

    private AvatarSpecification initialSpec;
    private AvatarSpecification spec;
    private int nbOfThreads = DEFAULT_NB_OF_THREADS;
    private int nbOfCurrentComputations;
    private boolean stoppedBeforeEnd;
    private boolean stoppedConditionReached;



    // ReachabilityGraph
    private Map<Integer, SpecificationState> states;
    private Map<Long, SpecificationState> statesByID;
    private List<SpecificationState> pendingStates;
    //private List<SpecificationLink> links;
    private int nbOfLinks;
    private long stateID = 0;
    private int blockValues;
    private boolean freeIntermediateStateCoding;


    // Options
    private boolean ignoreEmptyTransitions;
    private boolean ignoreConcurrenceBetweenInternalActions;

    // RG
    private boolean computeRG;

    // Reachability
    private boolean studyReachability;
    private ArrayList<SpecificationReachability> reachabilities;
    private int nbOfRemainingReachabilities;

    // Dealocks
    private int nbOfDeadlocks;

    // Liveness
    private boolean livenessDone;
    private boolean studyLiveness;
    private SpecificationLiveness livenessInfo;

    public AvatarModelChecker(AvatarSpecification _spec) {
	if (_spec != null) {
	    initialSpec = _spec;
	    //TraceManager.addDev("Before clone:\n" + spec);
	    spec = initialSpec.advancedClone();
	    //TraceManager.addDev("After clone:\n" + spec);
	}
        ignoreEmptyTransitions = true;
        ignoreConcurrenceBetweenInternalActions = true;
        studyReachability = false;
        computeRG = false;
	freeIntermediateStateCoding = true;
    }

    public int getWeightOfTransition(int originState, int destinationState) {
	if (statesByID == null) {
	    return 0;
	}
	SpecificationState st = statesByID.get(originState);
	if (st == null) {
	    return 0;
	}
	return st.getWeightOfTransitionTo(destinationState);

    }

    public int getNbOfStates() {
	if (states == null) {
	    return 0;
	}
        return states.size();
    }

    public int getNbOfLinks() {
        //return links.size();
        return nbOfLinks;
    }

    public int getNbOfPendingStates() {
        return pendingStates.size();
    }

    public synchronized long getStateID() {
        long tmp = stateID;
        stateID ++;
        return tmp;
    }

    public void setFreeIntermediateStateCoding(boolean _b) {
	freeIntermediateStateCoding = _b;
    }

    public void setIgnoreEmptyTransitions(boolean _b) {
        ignoreEmptyTransitions = _b;
    }

    public void setIgnoreConcurrenceBetweenInternalActions(boolean _b) {
        ignoreConcurrenceBetweenInternalActions = _b;
    }

    public void setLivenessofState(AvatarStateElement _ase, AvatarBlock _ab) {
	livenessInfo = new SpecificationLiveness(_ase, _ab);
	studyLiveness = true;
    }

    public int setReachabilityOfSelected() {
        reachabilities = new ArrayList<SpecificationReachability>();
        for(AvatarBlock block: spec.getListOfBlocks()) {
            for(AvatarStateMachineElement elt: block.getStateMachine().getListOfElements()) {
                if (elt.isCheckable()) {
                    SpecificationReachability reach = new SpecificationReachability(elt, block);
                    reachabilities.add(reach);
                }
            }
        }
        nbOfRemainingReachabilities = reachabilities.size();
        studyReachability = true;
        return nbOfRemainingReachabilities;
    }

    public int setReachabilityOfAllStates() {
        reachabilities = new ArrayList<SpecificationReachability>();
        for(AvatarBlock block: spec.getListOfBlocks()) {
            for(AvatarStateMachineElement elt: block.getStateMachine().getListOfElements()) {
                if (elt instanceof AvatarStateElement) {
                    SpecificationReachability reach = new SpecificationReachability(elt, block);
                    reachabilities.add(reach);
                }
            }
        }
        nbOfRemainingReachabilities = reachabilities.size();
        studyReachability = true;
        return nbOfRemainingReachabilities;
    }

    public ArrayList<SpecificationReachability> getReachabilities() {
        return reachabilities;
    }

    public int getNbOfRemainingReachabilities() {
        return nbOfRemainingReachabilities;
    }

    public int getNbOfReachabilities() {
	if (reachabilities == null) {
	    return -1;
	}
        return reachabilities.size() - nbOfRemainingReachabilities;
    }

    public int getNbOfDeadlocks() {
	return nbOfDeadlocks;
    }

    public void setComputeRG(boolean _rg) {
        computeRG = _rg;
    }

    /*private synchronized boolean startMC() {
	
      }*/

    public boolean startModelCheckingLiveness() {
	// No other study are authorized at the same time
	// 

	if (spec == null) {
	    return false;
	}
	
	if (livenessInfo == null) {
	    return false;
	}

	studyLiveness = true;
	livenessDone = false;
	studyReachability = false;
	computeRG = false;


	
	startModelChecking();
	return true;
    }

    public void startModelChecking() {
	TraceManager.addDev("String model checking");
	
	if (spec == null) {
	    return;
	}
	
        stoppedBeforeEnd = false;
        stateID = 0;
	nbOfDeadlocks = 0;


        // Remove timers, composite states, randoms
        TraceManager.addDev("Reworking Avatar specification");
        spec.removeTimers();
        spec.removeCompositeStates();
        spec.removeRandoms();
	spec.removeFIFOs(4);
        spec.makeFullStates();
        if (ignoreEmptyTransitions) {
            spec.removeEmptyTransitions((nbOfRemainingReachabilities == 0)||studyLiveness);
        }


        TraceManager.addDev("Preparing Avatar specification :" + spec.toString());
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
	statesByID = Collections.synchronizedMap(new HashMap<Long, SpecificationState>());
        pendingStates = Collections.synchronizedList(new LinkedList<SpecificationState>());
        //links = Collections.synchronizedList(new ArrayList<SpecificationLink>());
        nbOfLinks = 0;

        // Check stop conditions
        if (mustStop()) {
            return;
        }

        // Compute initial state
        SpecificationState initialState = new SpecificationState();
        initialState.setInit(spec, ignoreEmptyTransitions);
        for(AvatarBlock block: spec.getListOfBlocks()) {
            checkElement(block.getStateMachine().getStartState(), initialState);
        }
        initialState.id = getStateID();
        if (ignoreEmptyTransitions) {
            handleNonEmptyUniqueTransition(initialState);
        }
        prepareTransitionsOfState(initialState);
        blockValues = initialState.getBlockValues();

        //TraceManager.addDev("initialState=" + initialState.toString() + " nbOfTransitions" + initialState.transitions.size());
        initialState.computeHash(blockValues);
        states.put(initialState.hashValue, initialState);
	statesByID.put(initialState.id, initialState);
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

        // Set to non reachable not computed elements
        if ((studyReachability) && (!stoppedBeforeEnd)) {
            for(SpecificationReachability re: reachabilities) {
                if (re.result == SpecificationReachabilityType.NOTCOMPUTED) {
                    re.result = SpecificationReachabilityType.NONREACHABLE;
                }
            }
        }

    }

    // MAIN LOOP
    /////////////////////////////////////////////
    public void run() {
        SpecificationState s;

        boolean go = true;
        while(go) {
            // Pickup a state
            if ((stoppedBeforeEnd) || (stoppedConditionReached)) {
                return;
            }

            // Pickup a state
            s = pickupState();

            if ((stoppedBeforeEnd) || (stoppedConditionReached)) {
                return;
            }

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

    private void prepareTransitionsOfState(SpecificationState _ss) {
	
        int cpt;
        _ss.transitions = new ArrayList<SpecificationTransition>();
	//TraceManager.addDev("Preparing transitions of state " + _ss);

	
        // At first, do not merge synchronous transitions
        // Simply get basics transitions
        cpt = 0;

        for(AvatarBlock block: spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            SpecificationBlock sb = _ss.blocks[cpt];
            AvatarStateElement ase = asm.allStates[sb.values[SpecificationBlock.STATE_INDEX]];

            for(AvatarStateMachineElement elt: ase.getNexts()) {
                if (elt instanceof AvatarTransition) {
                    handleAvatarTransition((AvatarTransition)elt, block, sb, cpt, _ss.transitions);
                }
            }

            cpt ++;
        }
    }


    private void computeAllStatesFrom(SpecificationState _ss) {
	//TraceManager.addDev("Compute all state of: " + _ss);
	if (_ss == null) {
	    TraceManager.addDev("null state");
	    mustStop();
	    return;
	}
	
        ArrayList<SpecificationTransition> transitions = _ss.transitions;
	if (transitions == null) {
	    TraceManager.addDev("null transitions");
	    nbOfDeadlocks ++;
	    mustStop();
	    return;
	}

        //TraceManager.addDev("Possible transitions 1:" + transitions.size());

        // All locally executable transitions are now gathered.
        // We simply need to select the ones that are executable
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
            } else if (AvatarTransition.isActionType(tr.getType())) {
                newTransitions.add(tr);
            } else if (tr.getType() == AvatarTransition.TYPE_EMPTY) {
                newTransitions.add(tr);
            }
        }
        transitions = newTransitions;
        //TraceManager.addDev("Possible transitions 2:" + transitions.size());


        // Selecting only the transactions within the smallest clock interval
        int clockMin=Integer.MAX_VALUE, clockMax=Integer.MAX_VALUE;
        for(SpecificationTransition tr: transitions) {
            clockMin = Math.min(clockMin, tr.clockMin);
            clockMax = Math.min(clockMax, tr.clockMax);
        }

        //TraceManager.addDev("Selected clock interval:" + clockMin + "," + clockMax);

        newTransitions = new ArrayList<SpecificationTransition>();
        for(SpecificationTransition tr: transitions) {
            if (tr.clockMin  <= clockMax) {
                tr.clockMax = clockMax;
                newTransitions.add(tr);
            }
        }
        transitions = newTransitions;
        //TraceManager.addDev("Possible transitions 3:" + transitions.size());

        if (ignoreConcurrenceBetweenInternalActions) {
            SpecificationTransition st = null;
            // See whether there is at least one transition with an immediate internal action with no alternative in the same block
            for(SpecificationTransition tr: transitions) {
                if ((AvatarTransition.isActionType(tr.getType()) && (tr.clockMin == tr.clockMax) && (tr.clockMin == 0)) || tr.getType() == AvatarTransition.TYPE_EMPTY) {
                    // Must look for similar transitions in the the same block
                    //TraceManager.addDev("Lookin for same block for " + tr);
                    boolean foundSameBlock = false;
                    for(SpecificationTransition tro: transitions) {
                        if (tro != tr) {
                            if (tro.hasBlockOf(tr)) {
                                foundSameBlock = true;
                                //TraceManager.addDev("Found same block tr=" + tr + " tro=" + tro);
                                break;
                            }
                        }
                    }
                    if (!foundSameBlock) {
                        st = tr;
                        break;
                    }
                }
            }
            if (st != null) {
                transitions.clear();
                transitions.add(st);
            }
        }

	//TraceManager.addDev("Possible transitions 4:" + transitions.size());
	if (transitions.size() == 0) {
	    if (studyLiveness) {
		livenessInfo.result = false;
		livenessInfo.state = _ss;
		livenessDone = true;
	    } else {
		nbOfDeadlocks ++;
	    }
	    //TraceManager.addDev("Deadlock found");
	}

        //TraceManager.addDev("Possible transitions 4:" + transitions.size());
        // For each realizable transition
        //   Make it, reset clock of the involved blocks to 0, increase clockmin/clockhmax of each block
        //   compute new state, and compare with existing ones
        //   If not a new state, create the link rom the previous state to the new one
        //   Otherwise create the new state and its link, and add it to the pending list of states
        int cptt = 0;
        for(SpecificationTransition tr: transitions) {
            //TraceManager.addDev("Handling transitions #" + cptt + " type =" + tr.getType());
            cptt ++;

            // Make tr
            // to do so, must create a new state
            SpecificationState newState = _ss.advancedClone();

            // For non impacted blocks, increase their clock value, or set their clock to 0
            newState.increaseClockOfBlocksExcept(tr);

            // Impact the variable of the state, either by executing actions, or by
            // doing the synchronization
	    executeTransition(_ss, newState, tr);
            String action = tr.infoForGraph;

            // Remove empty transitions if applicable
            if (ignoreEmptyTransitions) {
                handleNonEmptyUniqueTransition(newState);
            }
            prepareTransitionsOfState(newState);

            // Compute the hash of the new state, and create the link to the right next state
            SpecificationLink link = new SpecificationLink();
            link.originState = _ss;
            action += " [" + tr.clockMin + " ..." + clockMax + "]";
            link.action = action;
            newState.computeHash(blockValues);
            SpecificationState similar = states.get(newState.getHash(blockValues));
            if (similar == null) {
                //  Unknown state
                states.put(newState.getHash(blockValues), newState);
		statesByID.put(newState.id, newState);
		if ((studyLiveness == false) || (studyLiveness && !(tr.livenessFound))) {
		    pendingStates.add(newState);
		}
		
                link.destinationState = newState;
                newState.id = getStateID();
                //TraceManager.addDev("Creating new state for newState=" + newState);

            } else {
                // Create a link from former state to the existing one
                //TraceManager.addDev("Similar state found State=" + newState.getHash(blockValues) + "\n" + newState + "\nsimilar=" + similar.getHash(blockValues) + "\n" + similar);
                
		link.destinationState = similar;

		// If liveness, must verify that from similar it is possible to go to the considered
		// state or not.

		if (studyLiveness) {
		}
		
            }
	    if (studyLiveness && (!tr.livenessFound)) {
		TraceManager.addDev("Liveness: path without the element found");
		livenessInfo.result = false;
		livenessInfo.state = newState;
		livenessDone = true;
	    }
            //links.add(link);
            nbOfLinks ++;
	    _ss.addNext(link);
        }
	if (freeIntermediateStateCoding) {
	    _ss.freeUselessAllocations();
	} else {
	    _ss.finished();
	}
	
        mustStop();
    }

    private boolean guardResult(AvatarTransition _at, AvatarBlock _block, SpecificationBlock _sb) {
        if (!_at.isGuarded()) {
            return true;
        }

        // Must evaluate the guard
        String guard = _at.getGuard().toString ();
        String s = Conversion.replaceAllString(guard, "[", "").trim();
        s = Conversion.replaceAllString(s, "]", "").trim();
        return evaluateBoolExpression(s, _block, _sb);
    }

    private void handleAvatarTransition(AvatarTransition _at, AvatarBlock _block, SpecificationBlock _sb,  int _indexOfBlock, ArrayList<SpecificationTransition> _transitionsToAdd) {
        if (_at.type == AvatarTransition.UNDEFINED) {
            return;
        }

        // Must see whether the guard is ok or not
        boolean guard = guardResult(_at, _block, _sb);
        if (!guard) {
            return;
        }

        SpecificationTransition st = new SpecificationTransition();
        _transitionsToAdd.add(st);
        st.init(1, _at, _block, _sb, _indexOfBlock);

        // Must compute the clockmin and clockmax values
        String minDelay = _at.getMinDelay().trim();
        if ((minDelay == null) || (minDelay.length() == 0)) {
            st.clockMin = 0 - _sb.values[SpecificationBlock.CLOCKMIN_INDEX];
        } else {
            st.clockMin = evaluateIntExpression(_at.getMinDelay(), _block, _sb) - _sb.values[SpecificationBlock.CLOCKMIN_INDEX];
        }
        String maxDelay = _at.getMaxDelay().trim();
        if ((maxDelay == null) || (maxDelay.length() == 0)) {
            st.clockMax = 0 - _sb.values[SpecificationBlock.CLOCKMAX_INDEX];
        } else {
            int resMax = evaluateIntExpression(_at.getMaxDelay(), _block, _sb);
            _sb.maxClock = Math.max(_sb.maxClock, resMax);
            st.clockMax = resMax - _sb.values[SpecificationBlock.CLOCKMAX_INDEX];
        }

	if (st.clockMin > st.clockMax) {
	    int tmp = st.clockMin;
	    st.clockMin = st.clockMax;
	    st.clockMax = tmp;
	}

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
                                    if (at.hasAction()) {
                                        if (at.hasMethod()) {
                                            at.type = AvatarTransition.TYPE_ACTION_AND_METHOD;
                                        } else {
                                            at.type = AvatarTransition.TYPE_ACTIONONLY;
                                        }
                                    } else {
                                        if (at.hasMethod()) {
                                            at.type = AvatarTransition.TYPE_METHODONLY;
                                        } else {
                                            at.type = AvatarTransition.TYPE_EMPTY;
                                        }
                                    }
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
            //TraceManager.addDev("Current block " + _block.getName());
        }

        boolean result = bee.getResultOf(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error: " + bee.getError());
        }

        //TraceManager.addDev("Result of " + _expr + " = " + result);
        return result;
    }

    public int evaluateIntExpression(String _expr, AvatarBlock _block, SpecificationBlock _sb) {
        //TraceManager.addDev("Evaluating Int expression 1: " + _expr);
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

        //TraceManager.addDev("Evaluating Int expression S: " + act);
        //Thread.currentThread().dumpStack();
        return (int)(new IntExpressionEvaluator().getResultOf(act));
    }

    private SpecificationTransition computeSynchronousTransition(SpecificationTransition sender, SpecificationTransition receiver) {
        AvatarTransition trs = sender.transitions[0];
        AvatarTransition trr = receiver.transitions[0];

        AvatarStateMachineElement asmes, asmer;
        asmes = trs.getNext(0);
        asmer = trr.getNext(0);
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

    
    private void executeTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
        int type = _st.transitions[0].type;
        AvatarStateElement ase;

        // Fill the new states of the involved blocks
        for(int i=0; i<_st.transitions.length; i++) {
            ase = getNextState(_st.transitions[i], _newState, 10);
            if (ase != null) {

		if (studyLiveness) {
		    if (livenessInfo.ref1 == ase) {
			TraceManager.addDev("Liveness found on a path");
			_st.livenessFound = true;
		    }
		}
                checkElement(ase, _newState);
                int index = _st.blocks[i].getStateMachine().getIndexOfState(ase);
                if (index > -1) {
                    _newState.blocks[_st.blocksInt[i]].values[SpecificationBlock.STATE_INDEX] = index;
                }
            }
        }



        if ((AvatarTransition.isActionType(type)) || (type == AvatarTransition.TYPE_EMPTY)) {
            _st.infoForGraph = executeActionTransition(_previousState, _newState, _st);
	    return ;
        } else if (type == AvatarTransition.TYPE_SEND_SYNC) {
            _st.infoForGraph =  executeSyncTransition(_previousState, _newState, _st);
	    return ;
        }

	_st.infoForGraph = "not implemented";
    }

    private AvatarStateElement getNextState(AvatarStateMachineElement e, SpecificationState _newState, int maxNbOfIterations) {
        checkElement(e, _newState);
        e = e.getNext(0);
        if (e instanceof AvatarStateElement) {
            return (AvatarStateElement)e;
        }
        maxNbOfIterations --;
        if (maxNbOfIterations == 0) {
            return null;
        }
        return getNextState(e, _newState, maxNbOfIterations);
    }

    // Execute the actions of a transition, and correspondingly impact the variables of the
    // block executing the transition
    private String executeActionTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
        // We use the attributes value as in the _newState
        // Get the attributes value list
        AvatarBlock block = _st.blocks[0];

        String retAction = null;

        for(AvatarAction aAction: _st.transitions[0].getActions()) {
            // Variable affectation
            if (!(aAction.containsAMethodCall())) {
                //if (!(aAction.isAMethodCall ())) {
                /*String action = aAction.toString ();
                  int ind = action.indexOf("=");
                  if (ind == -1) {
                  return "";
                  }*/
                String nameOfVar = ((AvatarActionAssignment)aAction).getLeftHand().getName();
                String act = ((AvatarActionAssignment)aAction).getRightHand().getName();

                //TraceManager.addDev("*** act=" + act);

                if (retAction == null) {
                    retAction = nameOfVar + "=" + act;
                }

                int indexVar = block.getIndexOfAvatarAttributeWithName(nameOfVar);
                AvatarType type = block.getAttribute(indexVar).getType();
                if (indexVar != -1) {
                    if (type == AvatarType.INTEGER) {
                        //TraceManager.addDev("Evaluating int expr=" + act);
                        int result = evaluateIntExpression(act, _st.blocks[0], _newState.blocks[_st.blocksInt[0]]);
                        _newState.blocks[_st.blocksInt[0]].values[SpecificationBlock.ATTR_INDEX+indexVar] = result;
                    } else if (type == AvatarType.BOOLEAN) {
                        boolean bool = evaluateBoolExpression(act, _st.blocks[0], _newState.blocks[_st.blocksInt[0]]);
                        if (bool) {
                            _newState.blocks[_st.blocksInt[0]].values[SpecificationBlock.ATTR_INDEX+indexVar] = 1;
                        } else {
                            _newState.blocks[_st.blocksInt[0]].values[SpecificationBlock.ATTR_INDEX+indexVar] = 0;
                        }
                    }
                }
            }
        }

        if (retAction == null) {
            retAction =  "";
        }

        return "i(" + _st.blocks[0].getName() + "/" + retAction + ")";
    }

    private String executeSyncTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
        AvatarBlock block0 = _st.blocks[0];
        AvatarBlock block1 = _st.blocks[1];
        AvatarActionOnSignal aaoss, aaosr;
        AvatarAttribute avat;
        String value;
        int result;
        boolean resultB;
        int indexVar;
        String nameOfVar;
        String ret = "";


        try {
            aaoss = (AvatarActionOnSignal)(_st.transitions[0].getNext(0));
            aaosr = (AvatarActionOnSignal)(_st.transitions[1].getNext(0));
        } catch (Exception e) {
            return "";
        }

        // copy the value of attributes from one block to the other one
        for(int i=0; i<aaoss.getNbOfValues(); i++) {
            value = aaoss.getValue(i);
            try {
                avat = aaoss.getSignal().getListOfAttributes().get(i);
                if (avat.getType() == AvatarType.INTEGER) {
                    //TraceManager.addDev("Evaluating expression, value=" + value);
                    //TraceManager.addDev("Evaluating int expr=" + value);
                    result = evaluateIntExpression(value, block0, _newState.blocks[_st.blocksInt[0]]);
                } else if (avat.getType() == AvatarType.BOOLEAN) {
                    resultB = evaluateBoolExpression(value, block0, _newState.blocks[_st.blocksInt[0]]);
                    result = resultB? 1 : 0;
                } else {
                    result = 0;
                }

                // Putting the result to the destination var
                nameOfVar = aaosr.getValue(i);
                indexVar = block1.getIndexOfAvatarAttributeWithName(nameOfVar);
                _newState.blocks[_st.blocksInt[1]].values[SpecificationBlock.ATTR_INDEX+indexVar] = result;
                ret += "" + result;
            } catch (Exception e) {
                TraceManager.addDev("EXCEPTION on adding value " + aaoss);
            }
        }


        return "!" + aaoss.getSignal().getName() + "_?" + aaosr.getSignal().getName() + "("+ ret + ")";

    }

    public void handleNonEmptyUniqueTransition(SpecificationState _ss) {
        int cpt = 0;
        for(AvatarBlock block: spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            SpecificationBlock sb = _ss.blocks[cpt];
            AvatarStateElement ase = asm.allStates[sb.values[SpecificationBlock.STATE_INDEX]];


            AvatarStateElement aseAfter = getStateWithNonEmptyUniqueTransition(ase, block, sb, _ss);
            if (aseAfter != ase) {
                checkElement(aseAfter, _ss);
                // Must modify the state of the considered block
                sb.values[SpecificationBlock.STATE_INDEX] = asm.getIndexOfState(aseAfter);
            }
            cpt ++;
        }
    }

    private AvatarStateElement getStateWithNonEmptyUniqueTransition(AvatarStateElement _ase, AvatarBlock _block, SpecificationBlock _sb, SpecificationState _ss) {
        return getStateWithNonEmptyUniqueTransitionArray(_ase, _block, _sb, _ss, null);
    }

    private AvatarStateElement getStateWithNonEmptyUniqueTransitionArray(AvatarStateElement _ase, AvatarBlock _block, SpecificationBlock _sb, SpecificationState _ss, ArrayList<AvatarStateElement> listOfStates ) {

        //      TraceManager.addDev("Handling Empty transition of previous=" + _ase.getName());

        if (_ase.getNexts().size() != 1) {
            return _ase;
        }


        //TraceManager.addDev("Handling Empty transition of previous= 1 " + _ase.getName());

        AvatarTransition at = (AvatarTransition)(_ase.getNext(0));

        //TraceManager.addDev("Handling Empty transition of previous= 2 " + _ase.getName() + " trans=" + at.getName());

        if(!((at.type == AvatarTransition.TYPE_EMPTY) || (at.type == AvatarTransition.TYPE_METHODONLY))) {
            return _ase;
        }

        //TraceManager.addDev("Handling Empty transition of previous= 3 " + _ase.getName() + " trans=" + at.getName());

	// Check time
	boolean hasTime = at.hasDelay();
	if (hasTime)  {
	    return _ase;
	}
			 
	
        // Check guard;
        boolean guard = guardResult(at, _block, _sb);

        if (!guard) {
            return _ase;
        }

        //TraceManager.addDev("Handling Empty transition of previous= 4 " + _ase.getName() + " trans=" + at.getName() + " delay=" + at.getMinDelay());


        AvatarStateElement ase = (AvatarStateElement)(at.getNext(0));
        checkElement(ase, _ss);
        //TraceManager.addDev("Handling Empty transition of " + _block.getName() + " with nextState = " + ase.getName() + " and previous=" + _ase.getName());

        if (listOfStates == null) {
            if (ase == _ase) {
                return _ase;
            }
            //TraceManager.addDev("New list of states " + _block.getName());
            listOfStates = new ArrayList<AvatarStateElement>();
        } else {
            if (listOfStates.contains(ase)) {
                return _ase;
            }
        }
        listOfStates.add(_ase);

        return getStateWithNonEmptyUniqueTransitionArray(ase, _block, _sb, _ss, listOfStates);


    }


    // Checking elements
    public void checkElement(AvatarStateMachineElement elt, SpecificationState _ss) {
        if (studyReachability) {
            checkElementReachability(elt, _ss);
        }
    }

    public void checkElementReachability(AvatarStateMachineElement elt, SpecificationState _ss) {
        for(SpecificationReachability re: reachabilities) {
            if (re.result ==  SpecificationReachabilityType.NOTCOMPUTED) {
                if (re.ref1 == elt) {
                    re.result = SpecificationReachabilityType.REACHABLE;
                    re.state = _ss;
                    nbOfRemainingReachabilities --;
                    TraceManager.addDev("Remaining reachabilities:" + nbOfRemainingReachabilities);
                }
            }
        }
    }



    // Stop condition

    public synchronized boolean mustStop() {
        if (stoppedConditionReached) {
            return true;
        }

	if (studyLiveness && livenessDone) {
	    return true;
	}

        stoppedConditionReached = true;

        if (studyReachability && nbOfRemainingReachabilities==0) {
            //TraceManager.addDev("***** All reachability found");
        }

        if (studyReachability && nbOfRemainingReachabilities>0) {
            stoppedConditionReached = false;
        }

        if (computeRG) {
            stoppedConditionReached = false;
        }

	if (studyLiveness) {
	    stoppedConditionReached = false;
	}

        return stoppedConditionReached;
    }


    // Generators

    public String toString() {
        StringBuffer sb = new StringBuffer("States:\n");
        for(SpecificationState state: states.values()) {
            sb.append(state.toString() + "\n");
        }
        sb.append("\nLinks:\n");
        for(SpecificationState state: states.values()) {
	    if (state.nexts != null) {
		for(SpecificationLink link: state.nexts) {
		    sb.append(link.toString() + "\n");
		}
	    }
        }
        return sb.toString();
    }


    public String toAUT() {
        StringBuffer sb = new StringBuffer();
        sb.append("des(0," + getNbOfLinks() + "," + getNbOfStates() + ")\n");

        for(SpecificationState state: states.values()) {
	     if (state.nexts != null) {
		 for(SpecificationLink link: state.nexts) {
                sb.append("(" + link.originState.id + ",\"" + link.action + "\"," + link.destinationState.id + ")\n");
		 }
	     }
        }
        return new String(sb);
    }

    public String toDOT() {
        StringBuffer sb = new StringBuffer();
        sb.append("digraph TToolAvatarGraph {\n");

        for(SpecificationState state: states.values()) {
	    if (state.nexts != null) {
		for(SpecificationLink link: state.nexts) {

		    sb.append(" " + link.originState.id + " -> " + link.destinationState.id  + "[label=\"" + link.action + "\"];\n");
		}
	    }
        }
        sb.append("}");
        return new String(sb);
    }

    public String reachabilityToString() {
        if (!studyReachability) {
            return "Reachability not activated";
        }


        String ret = "";
        if (stoppedBeforeEnd) {
            ret += "Beware: Full study of reacha&bility might not have been fully completed\n";
        }

        int cpt=0;
        for(SpecificationReachability re: reachabilities) {
            ret += (cpt+1) + ". " + re.toString() + "\n";
            cpt ++;
        }
        return ret;
    }

    // Do not free the RG
    public void freeUselessAllocations() {
	for(SpecificationState state: states.values()) {
	    state.freeUselessAllocations();
	}
    }


}
