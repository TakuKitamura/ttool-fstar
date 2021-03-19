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

package avatartranslator;


import graph.AUTGraph;
import graph.AUTState;
import graph.AUTTransition;
import myutil.TraceManager;
import ui.TGComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AvatarDependencyGraph  {
    private AUTGraph graph;
    private HashMap<AvatarElement, AUTState> toStates;
    private HashMap<AUTState, AvatarElement> fromStates;
    private int id = 0;

    public AvatarDependencyGraph() {
        toStates = new HashMap<>();
        fromStates = new HashMap<>();
    }

    public AUTGraph getGraph() {
        return graph;
    }

    public void setGraph(AUTGraph _g) {
        graph = _g;
    }

    public void setRefs(HashMap<AvatarElement, AUTState> _toStates, HashMap<AUTState, AvatarElement> _fromStates) {
       toStates = _toStates;
       fromStates = _fromStates;
    }

    public void buildGraph(AvatarSpecification _avspec) {
        graph = new AUTGraph();
        id = 0;

        ArrayList<AUTState> states = new ArrayList<>();
        ArrayList<AUTTransition> transitions = new ArrayList<>();
        // First build state machines, and then link them on RD / WR operators
        for(AvatarBlock block: _avspec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            AvatarStartState ass = asm.getStartState();

            // Make general structure
            makeDependencyGraphForAvatarElement(ass, null, null, states, transitions);

        }
        // Connect everything ie writers to all potential readers
        // For each writing state, we draw a transition to all possible corresponding readers
        // Double direction if synchronous
        for(AUTState state: states) {
            if (state.referenceObject instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) state.referenceObject;
                AvatarSignal signal = aaos.getSignal();
                if (signal.isOut()) {
                    // Write operation
                    AvatarSignal correspondingSig = _avspec.getCorrespondingSignal(signal);
                    //TraceManager.addDev("Corresponding signal=" + correspondingSig);
                    if (correspondingSig != null) {
                        for(AUTState stateDestination: states) {
                            if (stateDestination.referenceObject instanceof AvatarActionOnSignal) {
                                AvatarActionOnSignal aaosD = (AvatarActionOnSignal) stateDestination.referenceObject;
                                if (aaosD.getSignal() == correspondingSig) {
                                    // Found relation
                                    //TraceManager.addDev("Found relation!");
                                    AUTTransition tr = new AUTTransition(state.id, "", stateDestination.id);
                                    transitions.add(tr);
                                    state.addOutTransition(tr);
                                    stateDestination.addInTransition(tr);
                                    AvatarRelation ar = _avspec.getAvatarRelationWithSignal(correspondingSig);
                                    if (!(ar.isAsynchronous())) {
                                        tr = new AUTTransition(stateDestination.id, "", state.id);
                                        transitions.add(tr);
                                        stateDestination.addOutTransition(tr);
                                        state.addInTransition(tr);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Make the graph
        graph = new AUTGraph(states, transitions);

    }

    private AUTState makeDependencyGraphForAvatarElement(AvatarStateMachineElement _elt,
                                                     AUTState _previousS, AvatarStateMachineElement _previousE,
                                                         ArrayList<AUTState> _states,
                                                         ArrayList<AUTTransition> _transitions) {
        if (_elt == null) {
            return null;
        }

        AUTState state = new AUTState(id);
        _states.add(state);
        state.referenceObject = _elt;
        state.info = _elt.toStringExtendedID();
        toStates.put(_elt, state);
        fromStates.put(state, _elt);
        id ++;

        if (_previousE != null) {
            AUTTransition tr = new AUTTransition(_previousS.id, "", state.id);
            _transitions.add(tr);
            _previousS.addOutTransition(tr);
            state.addInTransition(tr);
        } else {
            state.isOrigin = true;
        }

        // Handling all nexts
        for(AvatarStateMachineElement eltN: _elt.getNexts()) {
            // Already a state for a next?
            AUTState stateN = toStates.get(eltN);
            if (stateN != null) {
                AUTTransition tr = new AUTTransition(state.id, "", stateN.id);
                _transitions.add(tr);
                state.addOutTransition(tr);
                stateN.addInTransition(tr);
            } else {
                makeDependencyGraphForAvatarElement(eltN, state, _elt, _states, _transitions);
            }
        }

        return state;
    }

    @SuppressWarnings("unchecked")
    public AvatarDependencyGraph clone() {
        AvatarDependencyGraph adg = new AvatarDependencyGraph();
        AUTGraph g = graph.cloneMe();
        adg.setGraph(g);

        HashMap<AvatarElement, AUTState> newToStates = new HashMap<>();
        HashMap<AUTState, AvatarElement> newFromStates = new HashMap<>();

        adg.setRefs(newToStates, newFromStates);

        // Filling states references
        for(AvatarElement ae: toStates.keySet()) {
            AUTState st = toStates.get(ae);

            // We must find the corresponding state in the new graph
            AUTState newState = g.getState(st.id);
            newToStates.put(ae, newState);
            newFromStates.put(newState, ae);
        }

        return adg;
    }

    public AvatarDependencyGraph reduceGraphBefore(ArrayList<AvatarElement> eltsOfInterest) {
        AvatarDependencyGraph result = clone();

        /*TraceManager.addDev("Size of original graph: s" + graph.getNbOfStates() + " t" + graph.getNbOfTransitions());
        TraceManager.addDev("Size of graph after clone: s" + result.graph.getNbOfStates() + " t" + result.graph.getNbOfTransitions());

        TraceManager.addDev("old graph:\n" + graph.toStringAll() + "\n");

        TraceManager.addDev("Cloned graph:\n" + result.graph.toStringAll() + "\n");*/

        /*TraceManager.addDev("Size of original graph toStates:" + toStates.size());
        TraceManager.addDev("Size of original graph fromStates:" + fromStates.size());
        TraceManager.addDev("Size of cloned graph toStates:" + result.toStates.size());
        TraceManager.addDev("Size of cloned graph fromStates:" + result.fromStates.size());*/

        // For each state, we figure out whether if it is linked to go to the elt states
        // or if they are after the elts.

        HashSet<AUTState> beforeStates = new HashSet<>();


        // We take each elt one after the other and we complete the after or before states
        for(AvatarElement ae: eltsOfInterest) {
            //TraceManager.addDev("Condering elt:" + ae.getName());
            Object ref = ae.getReferenceObject();
            if (ref != null) {
                // Finding the state referencing o
                AUTState stateOfInterest = null;
                for(AUTState s: graph.getStates()) {
                    AvatarElement elt = fromStates.get(s);
                    if (elt.getReferenceObject() == ref) {
                        stateOfInterest = s;
                        break;
                    }
                }

                if (stateOfInterest != null) {
                    //TraceManager.addDev("Has a state of interest: " + stateOfInterest.id);
                    for (AUTState state : graph.getStates()) {
                        if (state == stateOfInterest) {
                            beforeStates.add(result.graph.getState(state.id));
                        } else {
                            /*if (graph.hasPathFromTo(state.id, stateOfInterest.id)) {
                                beforeStates.add(result.graph.getState(state.id));
                            }*/
                            if (graph.canGoFromTo(state.id, stateOfInterest.id)) {
                                beforeStates.add(result.graph.getState(state.id));
                            }
                        }
                    }
                }
            }
        }

        //TraceManager.addDev("Size of before: " + beforeStates.size());

        // We now have to figure out which states have to be removed
        ArrayList<AUTState> toRemoveStates = new ArrayList<>();
        for(AUTState st: result.graph.getStates()) {
            if (!beforeStates.contains(st)) {
                toRemoveStates.add(st);
            }
        }

        //TraceManager.addDev("Size of remove: " + toRemoveStates.size());

        result.graph.removeStates(toRemoveStates);

        /*TraceManager.addDev("Size of graph after remove: s" + result.graph.getNbOfStates() + " t" + result.graph.getNbOfTransitions());
        TraceManager.addDev("New graph:\n" +result.graph.toStringAll() + "\n");*/



        return result;

    }



}
