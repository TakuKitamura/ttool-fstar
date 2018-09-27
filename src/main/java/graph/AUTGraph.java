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


package graph;

import myutil.Conversion;
import myutil.DijkstraState;
import myutil.GraphAlgorithms;
import myutil.TraceManager;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

/**
 * Class AUTGraph
 * Creation : 16/09/2004
 * * @version 1.0 16/09/2004
 *
 * @author Ludovic APVRILLE
 */
public class AUTGraph implements myutil.Graph {

    protected ArrayList<AUTTransition> transitions;
    protected ArrayList<AUTState> states;
    protected int nbState;
    protected BufferedReader br;
    protected long nbTransition;
    protected int percentage;
    protected boolean[] hasExitTransition;
    protected boolean[] hasEntryTransition;
    protected boolean statesComputed;

    protected static String STYLE_SHEET =
            "node {" +
                    "       fill-color: blue;" +
                    "} " +
                    //          "edge.defaultedge {" +
                    //  "   shape: cubic-curve;" +
                    //   "}" +
                    //    "edge {shape: cubic-curve}" +
                    "edge.external {" +
                    "       text-style: bold;" +
                    "} " +
                    "node.deadlock {" +
                    "       fill-color: green;" +
                    "} " +
                    "node.init {" +
                    "       fill-color: red;" +
                    "} ";

    public AUTGraph() {
        transitions = new ArrayList<AUTTransition>();
        //buildGraph(dataAUT);
    }

    public AUTGraph(ArrayList<AUTState> _st, ArrayList<AUTTransition> _tr) {
        states = _st;
        transitions = _tr;
        nbState = states.size();
        statesComputed = true;
    }

    public void stopBuildGraph() {
        br = null;
    }

    public int getPercentage() {
        return percentage;
    }

    public void buildGraph(String data) {
        if (data == null) {
            return;
        }

        StringReader sr = new StringReader(data);
        br = new BufferedReader(sr);
        String s, s1, s2;
        int index1;
        //int origin, destination;
        AUTTransition at;

        percentage = 0;
        int cpt, mod;

        /* read header */
        //TraceManager.addDev("Building graph");
        try {
            while ((s = br.readLine()) != null) {
                index1 = s.indexOf("des");
                //TraceManager.addDev("Searching for des");
                if (index1 == 0) {
                    //TraceManager.addDev("des found");
                    s1 = s.substring(s.indexOf(',') + 1, s.length());
                    s1 = s1.substring(0, s1.indexOf(','));
                    s1 = Conversion.removeFirstSpaces(s1);
                    nbTransition = new Integer(s1).intValue();
                    s2 = s.substring(s.indexOf(",") + 1, s.indexOf(')'));
                    s2 = s2.substring(s2.indexOf(",") + 1, s2.length());
                    s2 = Conversion.removeFirstSpaces(s2);
                    nbState = new Integer(s2).intValue();
                    break;
                }
            }
        } catch (Exception e) {
            TraceManager.addDev("Exception when reading graph information: " + e.getMessage() + "\n");
            return;
        }

        String[] array;
        hasExitTransition = new boolean[nbState];
        hasEntryTransition = new boolean[nbState];

        TraceManager.addDev("NbState=" + nbState + "  NbTransition=" + nbTransition + "\n");

        /*for(cpt=0; cpt<nbState; cpt ++) {
          hasExitTransition[cpt] = false;
          hasEntryTransition[cpt] = false;
          }*/

        /* read transitions */
        try {
            cpt = 0;
            mod = Math.max(1, (int) (nbTransition / 100));


            while ((s = br.readLine()) != null) {
                //TraceManager.addDev("realine:" + s);
                array = AUTGraph.decodeLine(s);
                at = new AUTTransition(array[0], array[1], array[2]);
                transitions.add(at);
                hasExitTransition[at.origin] = true;
                hasEntryTransition[at.destination] = true;
                cpt++;
                if ((cpt % mod) == 0) {
                    percentage = (int) ((cpt * 100) / nbTransition);
                    //TraceManager.addDev("percentage=" + percentage + "cpt=" + cpt + "nbTransition=" + nbTransition);
                }
            }
        } catch (Exception e) {
            TraceManager.addDev("Cancelled: " + e.getMessage() + "\n");
            return;
        }
    }

    public static String[] decodeLine(String s) {
        int index1, index2;
        String s1, s2, s3;

        index1 = s.indexOf("(");
        index2 = s.indexOf(",");
        s1 = s.substring(index1 + 1, index2);
        s = s.substring(index2 + 1, s.length());
        s = Conversion.removeFirstSpaces(s);

        // for of the action
        // , action,
        // "i(action<1,2,4>)",
        // "action<1,2,4>",

        // guillemets ?
        index1 = s.indexOf("\"");
        if (index1 > -1) {
            //TraceManager.addDev("Guillemets on " + s);
            s2 = s.substring(index1 + 1, s.length());
            s2 = s2.substring(0, s2.indexOf("\""));
            //TraceManager.addDev("Guillemets on " + s2);
            /*index2 = s2.indexOf("(");
              if (index2 > -1) {
              s2 = s2.substring(index2+1, s2.indexOf(")"));
              }*/
            //TraceManager.addDev("Guillemets on " + s2);

        } else {
            //TraceManager.addDev("No Guillemets on " + s);
            index1 = s.indexOf(",");
            if ((index2 = s.indexOf("(")) >= 0) {
                s2 = s.substring(index2 + 1, index1 - 2);
            } else {
                if ((index2 = s.indexOf("\"t\"")) >= 0) {
                    s2 = "t";
                } else {
                    s2 = s.substring(0, index1);
                }
            }
        }

        s = s.substring(s.indexOf(s2) + s2.length(), s.length());
        //TraceManager.addDev("s=" + s);
        index1 = s.indexOf(",");
        //index2 = s.indexOf(")");
        //s2 = s.substring(0, index1-1);
        s3 = s.substring(index1 + 1, s.length() - 1);
        s3 = Conversion.removeFirstSpaces(s3);
        //TraceManager.addDev("s1=" + s1 + " s2=" + s2 + " s3=" + s3);

        String[] array = new String[3];
        array[0] = s1;
        array[1] = s2;
        array[2] = s3;
        return array;
    }

    public int getNbOfStates() {
        return nbState;
    }

    public void setNbOfStates(int _nb) {
        nbState = _nb;
    }

    public int getNbOfTransitions() {
        //return nbTransition;
        return transitions.size();
    }

    public AUTTransition getAUTTransition(int index) {
        return transitions.get(index);
    }

    public ArrayList<AUTState> getStates() {
        return states;
    }

    public ArrayList<AUTTransition> getTransitions() {
        return transitions;
    }

    public void addTransition(AUTTransition _tr) {
        transitions.add(_tr);
        statesComputed = false;
    }

    public void addState(AUTState _st) {
        states.add(_st);
    }


    public int getNbPotentialDeadlocks() {
        int nb = 0;

        for (int i = 0; i < nbState; i++) {
            if (hasEntryTransition(i)) {
                if (!hasExitTransition(i)) {
                    nb++;
                }
            }
        }

        return nb;
    }

    public String getActionTransition(int origin, int destination) {

        for (AUTTransition aut1 : transitions) {
            if ((aut1.origin == origin) && (aut1.destination == destination)) {
                return aut1.transition;
            }
        }
        return "";
    }

    public boolean hasEntryTransition(int state) {
        if (hasEntryTransition == null) {
            computeEntryExitTransitions();
        }
        return hasEntryTransition[state];
    }

    public boolean hasExitTransition(int state) {
        if (hasExitTransition == null) {
            computeEntryExitTransitions();
        }
        return hasExitTransition[state];
    }

    public boolean hasExitTransitionTo(int state, int destination) {
        if (!hasExitTransition(state)) {
            return false;
        }


        for (AUTTransition aut1 : transitions) {
            if ((aut1.origin == state) && (aut1.destination == destination)) {
                return true;
            }
        }
        return false;

    }

    /* State numbers are return under the form of int */
    /* Should be rewritten: not of high performance at all */

    public int[] getVectorPotentialDeadlocks() {
        int nbPotentialDeadlock = getNbPotentialDeadlocks();
        //TraceManager.addDev("nb of deadlocks: " + nbPotentialDeadlock);
        int[] states = new int[nbPotentialDeadlock];
        int index = 0;

        for (int i = 0; i < nbState; i++) {
            if (hasEntryTransition(i)) {
                if (!hasExitTransition(i)) {
                    states[index] = i;
                    index++;
                }
            }
        }

        return states;
    }

    public int[] shortestPathTo(int fromState, int targetState) {
        return GraphAlgorithms.ShortestPathFrom(this, fromState)[targetState].path;
    }


    public boolean hasTransitionWithAction(String action) {

        for (AUTTransition aut1 : transitions) {
            if (aut1.transition.compareTo(action) == 0) {
                return true;
            }
        }
        return false;
    }

    // For Graph interface

    public int getWeightOfTransition(int originState, int destinationState) {
        if (statesComputed) {
            if (states.get(originState).hasTransitionTo(destinationState)) {
                return 1;
            }
        } else {
            if (hasExitTransitionTo(originState, destinationState)) {
                return 1;
            }
        }
        return 0;
    }

    public String toAUTStringFormat() {
        StringBuffer graph = new StringBuffer("");
        graph.append("des(0," + nbTransition + "," + nbState + ")\n");
        for (AUTTransition aut1 : transitions) {
            graph.append("(" + aut1.origin + ",\"" + aut1.transition + "\"," + aut1.destination + ")\n");
        }
        return graph.toString();
    }


    public String toFullString() {
        StringBuffer graph = new StringBuffer("Transitions:");
        for (AUTTransition aut1 : transitions) {
            graph.append(aut1.toString());
        }
        graph.append("\nstates:\n");
        for (AUTState str : states) {
            graph.append(str.toString());
        }
        return graph.toString();

    }

    public void computeStates() {
        if (!statesComputed) {
            states = new ArrayList<AUTState>(nbState);
            AUTState state;
            for (int i = 0; i < nbState; i++) {
                state = new AUTState(i);
                states.add(state);
            }

            for (AUTTransition aut1 : transitions) {
                states.get(aut1.origin).addOutTransition(aut1);
                states.get(aut1.destination).addInTransition(aut1);
            }
            statesComputed = true;
        }
    }

    public AUTState getState(int _id) {
        return states.get(_id);
    }

    public boolean areStateComputed() {
        return statesComputed;
    }

    public void setStates(ArrayList<AUTState> _states) {
        states = _states;
        nbState = states.size();
        statesComputed = true;
    }

    public HashSet<String> getAllActions() {
        HashSet<String> hs = new HashSet<String>();
        for (AUTTransition tr : transitions) {
            hs.add(tr.transition);
        }
        return hs;
    }


    public void reinitMet() {
        for (AUTState state : states) {
            state.met = false;
        }
    }

    public AUTState findFirstOriginState() {
        AUTState state;

        for (int i = 0; i < states.size(); i++) {
            state = states.get(i);
            //TraceManager.addDev("id=" + state.id + " transitions to me = " +state.inTransitions.size());
            if (state.inTransitions.size() == 0) {
                return state;
            }
        }

        return null;
    }

    public void putTransitionsFromInitFirst() {
        ArrayList<AUTTransition> tmp = new ArrayList<AUTTransition>();

        for (AUTTransition aut1 : transitions) {
            if (aut1.origin == 0) {
                tmp.add(aut1);
            }
        }

        for (AUTTransition aut2 : tmp) {
            transitions.remove(aut2);
            transitions.add(0, aut2);
        }
    }

    public void display() {
        display(false);
    }

    public void display(boolean exitOnClose) {
        AUTGraphDisplay display = new AUTGraphDisplay(this, exitOnClose);
        display.display();
    }


    public AUTGraph cloneMe() {
        AUTGraph newGraph = new AUTGraph();
        newGraph.setNbOfStates(getNbOfStates());
        for (AUTTransition tr : transitions) {
            AUTTransition newTr = new AUTTransition(tr.origin, tr.transition, tr.destination);
            newGraph.addTransition(newTr);
        }
        newGraph.computeStates();
        return newGraph;
    }


    public void minimizeRemoveInternal(boolean tauOnly) {
        String s = "tau";

        // mark all transitions as non tau
        for (AUTTransition tr : transitions) {
            tr.isTau = false;
        }

        // Mark all tau transitions as tau
        for (AUTTransition tr : transitions) {

            if (tr.transition.startsWith("i(")) {
                tr.isTau = true;
                tr.transition = s;
            }

        }

        minimizeTau(tauOnly);
    }


    public void minimize(String[] tauTransitions, boolean tauOnly) {
        String s = "tau";

        // mark all transitions as non tau
        for (AUTTransition tr : transitions) {
            tr.isTau = false;
        }

        // Mark all tau transitions as tau
        for (AUTTransition tr : transitions) {
            for (int i = 0; i < tauTransitions.length; i++) {
                if (tr.transition.compareTo(tauTransitions[i]) == 0) {
                    tr.isTau = true;
                    tr.transition = s;
                }
            }
        }

        minimizeTau(tauOnly);

    }

    public void minimizeTau(boolean tauOnly) {
        boolean modif = true;

        //TraceManager.addDev(toFullString());

        factorizeNonTauTransitions();

        if (tauOnly) {
            return;
        }

        TraceManager.addDev("RG before partition: " + toFullString());

        partitionGraph();

    }

    // Remove transition going from one state with only one tau transition as output
    private boolean removeOnlyOneTauTr() {
        AUTTransition tr;
        ArrayList<AUTState> toRemoveStates = new ArrayList<AUTState>();
        // Remove in case state with one outgoing and outgoing is tau -> remove tr
        for (AUTState st : states) {
            if (st.outTransitions.size() == 1) {
                tr = st.outTransitions.get(0);
                if (tr.isTau) {
                    transitions.remove(tr);
                    st.outTransitions.clear();


                    AUTState st1 = states.get(tr.destination);
                    if (st1 != st) {
                        toRemoveStates.add(st1);
                        //TraceManager.addDev("Removing state " + st1.id);

                        // Must put all incoming transition to the first state
                        st1.inTransitions.remove(tr);
                        for (AUTTransition trM : st1.inTransitions) {
                            trM.destination = st.id;
                            st.inTransitions.add(trM);
                            //TraceManager.addDev("New in transitions " + trM);
                        }
                        st1.inTransitions.clear();

                        // Out transitions
                        st.outTransitions.clear();
                        for (AUTTransition trM : st1.outTransitions) {
                            st.outTransitions.add(trM);
                            trM.origin = st.id;
                           // TraceManager.addDev("New out transitions " + trM);
                        }
                        st1.outTransitions.clear();
                        break;
                    }
                }
            }
        }

        // Remove all states and adapt the id in the graph
        if (toRemoveStates.size() > 0) {
            removeStates(toRemoveStates);
            return true;
        }

        return false;
    }


    // Rework states with at least 2 tau transitions
    private boolean removeMultipleTauOutputTr() {
        AUTTransition tr1, tr2, trtmp;
        AUTState st1, st2, sttmp;
        ArrayList<AUTState> toRemoveStates = new ArrayList<AUTState>();
        AUTTransition[] ret;
        boolean modif = false;

        // Remove in case state with one outgoing and outgoing is tau -> remove transition
        for (AUTState st : states) {
            ret = st.getAtLeastTwoOutTauTransitions();
            if (ret != null) {
                tr1 = ret[0];
                tr2 = ret[1];
                tr2 = st.outTransitions.get(1);
                st1 = states.get(tr1.destination);
                st2 = states.get(tr2.destination);

                // Same states
                if (st1 == st2) {
                    //We can simply remove the transition
                    transitions.remove(tr2);
                    st.outTransitions.remove(tr2);
                    modif = true;
                }

                // We can merge st1 or st2 because one has no other incoming transition than
                // the tau transition
                else if ((st1.inTransitions.size() == 1) && (st2.inTransitions.size() == 1)) {
                    //We can remove st2 and the tau transition
                    toRemoveStates.add(st2);
                    transitions.remove(tr2);
                    st.outTransitions.remove(tr2);

                    // All transitions leaving st2 must now leave from st1 as well
                    for (AUTTransition trf : st2.outTransitions) {
                        trf.origin = st1.id;
                        st1.outTransitions.add(trf);
                    }
                }

            }
        }

        // Remove all states and adapt the id in the graph
        if (toRemoveStates.size() > 0) {
            removeStates(toRemoveStates);
            modif = true;
        }


        return modif;
    }

    // Rework states with only one tau before, and only one action after
    private boolean removeTauWithOneFollower() {
        AUTTransition tr1, tr2;
        AUTState st1, st2;
        ArrayList<AUTState> toRemoveStates = new ArrayList<AUTState>();
        boolean modif = false;

        // Remove stgate in case state with one outgoing and outgoing is tau
        for (AUTState st : states) {
            if (st.hasOneIncomingTauAndOneFollower()) {
                //We can remove the previous tau transaction, and the current state
                tr1 = st.inTransitions.get(0);
                st1 = states.get(tr1.origin);
                if (st1 != st) {
                    tr2 = st.outTransitions.get(0);
                    tr2.origin = st1.id;
                    st1.outTransitions.remove(tr1);
                    st1.outTransitions.add(tr2);
                    transitions.remove(tr1);
                    toRemoveStates.add(st);
                    break;
                }
            }

        }

        // Remove all states and adapt the id in the graph
        if (toRemoveStates.size() > 0) {
            removeStates(toRemoveStates);
            modif = true;
        }


        return modif;
    }


    private boolean removeSimilarTransitions() {
        boolean modif = false;

        // Remove tr if it is duplicated
        for (AUTState st : states) {

            // May modify the outTransitions list, and result in exception.
            // The try .. catch clause protects from this
            try {
                if (st.outTransitions.size() > 1) {
                    for (int i = 0; i < st.outTransitions.size(); i++) {
                        for (int j = i + 1; j < st.outTransitions.size(); j++) {
                            AUTTransition tri = st.outTransitions.get(i);
                            AUTTransition trj = st.outTransitions.get(j);
                            if (tri.destination == trj.destination) {
                                if (tri.transition.compareTo(trj.transition) == 0) {
                                    modif = true;
                                    //We remove trj
                                    st.outTransitions.remove(trj);
                                    transitions.remove(trj);
                                    i--;
                                    j--;
                                }
                            }
                        }
                    }


                }
            } catch (Exception e) {
            }
        }

        return modif;
    }


    private void removeStates(ArrayList<AUTState> toRemoveStates) {

        if (toRemoveStates.size() > 0) {
            hasExitTransition = null;
            hasEntryTransition = null;
        }

        // Remove all states and adapt the id in the graph
        //TraceManager.addDev("nbState=" + nbState + " states size = " + states.size());

        for (AUTState str : toRemoveStates) {
            // We need to remove all transitions of the removed state
            //TraceManager.addDev("Removing transitions of state:" + str.id + "\n" + toFullString());
            for (AUTTransition trin : str.inTransitions) {
                transitions.remove(trin);
            }
            for (AUTTransition trout : str.outTransitions) {
                transitions.remove(trout);
            }

            for (AUTState state : states) {
                state.removeAllTransitionsWithId(str.id);
            }

            //TraceManager.addDev("Done removing transitions of state:" + str.id + "\n" + toFullString());

            // Last state of the array?
            if (str.id == (nbState - 1)) {
                //TraceManager.addDev("Last state " + str.id);
                nbState--;
                states.remove(str.id);

                // str not at the end: we replace it with the last state
                // We need to accordingly update
            } else {

                AUTState moved = states.get(nbState - 1);
                //TraceManager.addDev("Moving state " + moved.id +  " to index " + str.id);
                states.set(str.id, moved);
                states.remove(nbState - 1);
                nbState--;
                //TraceManager.addDev("nbState=" + nbState + " states size = " + states.size());
                /*AUTTransition tt = findTransitionWithId(nbState);
                  if (tt != null) {
                  TraceManager.addDev("1) Transition with id not normal" + tt);
                  }*/
                //TraceManager.addDev("Update id\n" + toAUTStringFormat());
                moved.updateID(str.id);
                /*tt = findTransitionWithId(nbState);
                  if (tt != null) {
                  TraceManager.addDev("2) Transition with id not normal" + tt);
                  }*/
            }

        }
    }

    // Removes all tau transition of a state, replacing them with reachable non tau transitions
    // A tau transition reaching an end state cannot be removed but can be replaced with a unique transition
    private void factorizeNonTauTransitions() {
        boolean modif = false;
        boolean endState = false;
        // met is used to specify states that have a tau-path to a termination state
        for (AUTState st1 : states) {
            st1.met = false;
        }

        for (AUTState st : states) {
            if ((st.id == 0) || (st.getNbInTransitions() > 0)) {
                //TraceManager.addDev("  1. state " + st.id);
                if (st.hasOutputTauTransition()) {
                    //TraceManager.addDev("  2. state " + st.id);
                    LinkedList<AUTTransition> nonTauTransitions = new LinkedList<AUTTransition>();
                    boolean canReachAnEndStateWithTau = getAllNonTauTransitionsFrom(st, nonTauTransitions);

                    //TraceManager.addDev("State " + st.id + " has the following real transitions:");
                    /*for(AUTTransition tr: nonTauTransitions) {
                      TraceManager.addDev("\t" + tr);
                      }*/

                    //TraceManager.addDev("State " + st.id + " can reach an end state with tau tr only?" + canReachAnEndStateWithTau);
                    st.met = canReachAnEndStateWithTau;
                    endState = endState || canReachAnEndStateWithTau;
                }
            }
        }

        // Remove tr if it is duplicated
        for (AUTState st : states) {
            // We ignore states with no input tr apart from the start state (id 0)
            //TraceManager.addDev("0. state " + st.id);
            if ((st.id == 0) || (st.getNbInTransitions() > 0)) {
                //TraceManager.addDev("  1. state " + st.id);
                if (st.hasOutputTauTransition()) {
                    //TraceManager.addDev("  2. state " + st.id);
                    LinkedList<AUTTransition> nonTauTransitions = new LinkedList<AUTTransition>();
                    getAllNonTauTransitionsFrom(st, nonTauTransitions);

                    // Create these transitions in st if not yet existing
                    //TraceManager.addDev("Remove tau\n" + toFullString());
                    st.removeAllOutTauTransitions(transitions, states);
                    //TraceManager.addDev("Done remove tau. create trans\n" + toFullString());
                    st.createTransitionsButNotDuplicate(nonTauTransitions, states, transitions);
                    //TraceManager.addDev("Done create trans\n" + toFullString());
                }
            }
        }

        // Remove all non reachable state
        //removeAllNonReachableStates();

        // If end state: we must create a new end state, and all "met" states should have a tau transition
        // to this state
        if (endState) {
            // We must see if at least one met state has output transitions
            boolean hasEndState = false;
            for (AUTState st : states) {
                if (st.met) {
                    if (st.outTransitions.size() > 0) {
                        hasEndState = true;
                        break;
                    }

                }
            }
            if (hasEndState) {
                int newId = states.size();
                AUTState endSt = new AUTState(newId);
                states.add(endSt);
                nbState = states.size();
                for (AUTState st1 : states) {
                    if (st1.met) {
                        if (st1.outTransitions.size() > 0) {
                            //TraceManager.addDev("Adding an end tau to state " + st1.id);
                            AUTTransition tr = new AUTTransition(st1.id, "tau", endSt.id);
                            tr.isTau = true;
                            transitions.add(tr);
                            st1.addOutTransition(tr);
                            endSt.addInTransition(tr);
                        }
                    }
                    st1.met = false;
                }
            }
        }
        //TraceManager.addDev(toFullString());

        // Remove all non reachable state
        removeAllNonReachableStates();


        // Print graph in AUT
        //TraceManager.addDev(toAUTStringFormat());


    }

    private boolean getAllNonTauTransitionsFrom(AUTState st, LinkedList<AUTTransition> nonTauTransitions) {
        LinkedList<AUTState> metStates = new LinkedList<AUTState>();
        //metStates.add(st);

        return getAllNonTauTransitionsIterative(st, metStates, nonTauTransitions);
        //return getAllNonTauTransitionsRecursive(st, metStates, nonTauTransitions);
    }


    private boolean getAllNonTauTransitionsRecursive(AUTState st, LinkedList<AUTState> metStates, LinkedList<AUTTransition> nonTauTransitions) {
        if (metStates.contains(st)) {
            return false;
        }

        if (st.getNbOutTransitions() == 0) {
            return true;
        }

        boolean ret = false;
        for (AUTTransition at : st.outTransitions) {
            if (!(at.isTau)) {
                nonTauTransitions.add(at);
            } else {
                ret = ret || getAllNonTauTransitionsRecursive(states.get(at.destination), metStates, nonTauTransitions);
            }
        }

        return ret;
    }


    private boolean getAllNonTauTransitionsIterative(AUTState _st, LinkedList<AUTState> metStates, LinkedList<AUTTransition> nonTauTransitions) {

        boolean ret = false;

        LinkedList<AUTState> toExplore = new LinkedList<AUTState>();
        LinkedList<AUTState> toExploreTmp = new LinkedList<AUTState>();
        toExplore.add(_st);

        while (toExplore.size() > 0) {
            toExploreTmp.clear();
            for (AUTState st : toExplore) {
                if (!(metStates.contains(st))) {
                    metStates.add(st);
                    if (st.getNbOutTransitions() == 0) {
                        ret = true;
                    } else {
                        for (AUTTransition at : st.outTransitions) {
                            if (!(at.isTau)) {
                                nonTauTransitions.add(at);
                            } else {
                                toExploreTmp.add(states.get(at.destination));
                            }
                        }
                    }

                }
            } // for
            toExplore.clear();
            toExplore.addAll(toExploreTmp);

        }// While

        return ret;
    }

    private AUTTransition findTransitionWithId(int id) {
        for (AUTTransition tr : transitions) {
            if ((tr.origin == id) || (tr.destination == id)) {
                return tr;
            }
        }
        return null;
    }


    private void removeAllNonReachableStates() {
        // reset met of states
        for (AUTState st1 : states) {
            st1.met = false;
        }

        int cpt = 0;

        LinkedList<AUTState> statesToConsider = new LinkedList<AUTState>();
        LinkedList<AUTState> nextStatesToConsider = new LinkedList<AUTState>();
        statesToConsider.add(states.get(0));

        while (statesToConsider.size() > 0) {
            nextStatesToConsider.clear();
            for (AUTState st : statesToConsider) {
                st.met = true;
                cpt++;
                for (AUTTransition tr : st.outTransitions) {
                    AUTState s = states.get(tr.destination);
                    if (!(s.met)) {
                        nextStatesToConsider.add(s);
                    }
                }
            }
            statesToConsider.clear();
            statesToConsider.addAll(nextStatesToConsider);
        }

        //TraceManager.addDev("Found " + cpt + " reachable states");
        ArrayList<AUTState> toRemoveStates = new ArrayList<AUTState>();
        for (AUTState st2 : states) {
            if (!(st2.met)) {
                toRemoveStates.add(st2);
                //TraceManager.addDev("Removing state: " + st2.id);
            }
        }

        //TraceManager.addDev(toFullString());
        removeStates(toRemoveStates);
        //TraceManager.addDev(toFullString());

        //statesComputed = false;
        //states = null;
        //computeStates();

    }


    private void computeEntryExitTransitions() {
        hasExitTransition = new boolean[nbState];
        hasEntryTransition = new boolean[nbState];

        for (AUTTransition t : transitions) {
            hasExitTransition[t.origin] = true;
            hasEntryTransition[t.destination] = true;
        }
    }


    public void partitionGraph() {

        // Create the alphabet
        HashMap<String, AUTElement> alphabet = new HashMap<String, AUTElement>();
        for (AUTTransition tr : transitions) {
            AUTElement tmp = alphabet.get(tr.transition);
            if (tmp == null) {
                AUTElement elt = new AUTElement(tr.transition);
                alphabet.put(tr.transition, elt);
                tr.elt = elt;
            } else {
                tr.elt = tmp;
            }
            //TraceManager.addDev("Transition "+ tr + " has element " + tr.elt);
        }

        List<AUTElement> sortedAlphabet = new ArrayList<AUTElement>(alphabet.values());
        Collections.sort(sortedAlphabet);

        TraceManager.addDev("Alphabet size:" + sortedAlphabet.size());
        for (int i=0; i<alphabet.size(); i++) {
            TraceManager.addDev("Letter #" + i +  ": " + sortedAlphabet.get(i).toString());
        }


        Map<Integer, AUTBlock> allBlocks = Collections.synchronizedMap(new HashMap<Integer, AUTBlock>());

        // Create the first block that contains all states
        AUTBlock b0 = new AUTBlock();
        for (AUTState st : states) {
            b0.addState(st);
        }
        b0.computeHash();
        allBlocks.put(new Integer(b0.hashValue), b0);


        AUTBlock b0Test = new AUTBlock();
        for (AUTState st : states) {
            b0Test.addState(st);
        }
        b0Test.computeHash();

        AUTBlock B0Ret = allBlocks.get(new Integer(b0Test.hashValue));
        if (B0Ret == null) {
            //TraceManager.addDev("ERROR: hash not working for blocks");
        } else {
            //TraceManager.addDev("Hash working for blocks");
        }


        // Create the first partition containing only block B0
        AUTPartition partition = new AUTPartition();
        partition.addBlock(b0);

        // Create the splitter that contains partitions

        AUTPartition partitionForSplitter = new AUTPartition();
        partitionForSplitter.addBlock(b0);
        AUTSplitter w = new AUTSplitter();
        w.addPartition(partitionForSplitter);

        //printConfiguration(partition, w);

        int maxIte = 10; // With same nb of partitions
        int nbOfPartitions = w.size();
        int evolution = 20;
        int hashCode = -1;

        AUTPartition currentP;
        while ((w.size() > 0) && (maxIte > 0) && (evolution > 0)) {

            int newHash = w.getHashCode();
            if (newHash == hashCode) {
                break;
            }

            if (w.size() == nbOfPartitions) {
                evolution--;
            } else {
                evolution = 20;
            }
            nbOfPartitions = w.size();
            maxIte--;
            currentP = w.partitions.get(0);
            w.partitions.remove(0);


            // Simple splitter?
            if (currentP.blocks.size() == 1) {
                TraceManager.addDev("Simple splitter = " + currentP);
                AUTBlock currentBlock = currentP.blocks.get(0);
                //List<AUTElement> sortedAlphabet = new ArrayList<AUTElement>(alphabet.values());
                //Collections.sort(sortedAlphabet);
                for (AUTElement elt : sortedAlphabet) {
                    //TraceManager.addDev("\n*** Considering alphabet element = " + elt.value);
                    //printConfiguration(partition, w);
                    // Look for states of the leading to another state by a = T
                    // Construct I = all blocks of P that have at least an element in T
                    AUTBlock T_minus1_elt_B = currentBlock.getMinus1(elt, states);

                    //TraceManager.addDev("T_minus1_elt_B=" + T_minus1_elt_B);

                    LinkedList<AUTBlock> I = partition.getI(elt, T_minus1_elt_B);
                    //printI(I);
                    for (AUTBlock blockX : I) {
                        AUTBlock blockX1 = blockX.getStateIntersectWith(T_minus1_elt_B);
                        blockX1.computeHash();
                        AUTBlock blockX2 = blockX.getStateDifferenceWith(T_minus1_elt_B);
                        blockX2.computeHash();
                        //TraceManager.addDev("X1=" + blockX1);
                        //TraceManager.addDev("X2=" + blockX2);

                        if (blockX1.isEmpty() || blockX2.isEmpty()) {
                            //TraceManager.addDev("Nothing to do");
                            // Nothing to do!
                        } else {
                            boolean b = partition.removeBlock(blockX);
                            if (!b) {
                                //TraceManager.addDev("Block " + blockX + " could not be removed from partition");
                            }
                            partition.addBlock(blockX1);
                            partition.addBlock(blockX2);
                            AUTPartition X_X1_X2 = new AUTPartition();
                            X_X1_X2.addBlock(blockX);
                            X_X1_X2.addBlock(blockX1);
                            X_X1_X2.addBlock(blockX2);
                            //TraceManager.addDev("Test concat X1+X2=" + AUTBlock.concat(blockX1, blockX2));
                            w.addPartition(X_X1_X2);
                            TraceManager.addDev("Modifying P and W:");
                            printConfiguration(partition, w);
                            TraceManager.addDev("-----------------\n");
                        }

                    }
                    TraceManager.addDev("-----------------\n");

                }

            }

            // Compound splitter
            else if (currentP.blocks.size() == 3) {
                TraceManager.addDev("Complex splitter (b, bi, bii) =" + currentP);
                AUTBlock b = currentP.blocks.get(0);
                AUTBlock bi = currentP.blocks.get(1);
                AUTBlock bii = currentP.blocks.get(2);

                if (bi.size() > bii.size()) {
                    bi = currentP.blocks.get(2);
                    bii = currentP.blocks.get(1);
                }

                TraceManager.addDev("B= " + b +  " bi=" + bi + " bii=" + bii);

                for (AUTElement elt : sortedAlphabet) {
                    //TraceManager.addDev("\n*** Considering alphabet element = " + elt.value);
                    //printConfiguration(partition, w);
                    AUTBlock T_minus1_elt_B = b.getMinus1(elt, states);
                    //TraceManager.addDev("T_minus1_elt_B=" + T_minus1_elt_B);
                    LinkedList<AUTBlock> I = partition.getI(elt, T_minus1_elt_B);
                    //printI(I);
                    for (AUTBlock blockX : I) {
                        // Compute block X1 = set of states in blockX that goes to Bi, but not to Bii
                        // with current action
                        AUTBlock blockX1 = new AUTBlock();

                        // Compute block X2 = set of states in blockX that goes to Bii, but not to Bi
                        // with current action
                        AUTBlock blockX2 = new AUTBlock();

                        // Compute block X3 = set of states in blockX that goes to both Bi and Bii
                        // with current action
                        boolean b1, b2;
                        AUTBlock blockX3 = new AUTBlock();
                        for (AUTState st : blockX.states) {
                            b1 = blockX.leadsTo(bi, elt);
                            b2 = blockX.leadsTo(bii, elt);
                            if (b1 && !b2) {
                                blockX1.addState(st);
                            } else if (!b1 && b2) {
                                blockX2.addState(st);
                            } else {
                                blockX3.addState(st);
                            }
                        }
                        TraceManager.addDev("Block X = " + blockX + " Block1,2,3 computed\n\tX1 = " + blockX1 + "\n\tX2 = " + blockX2 + "\n\tX3 = " + blockX3);

                        if ((blockX.compareTo(blockX1) == 0) || (blockX.compareTo(blockX2) == 0) || (blockX.compareTo(blockX2) == 0)) {
                            // do nothing
                            TraceManager.addDev("Identical blocks! X");
                        } else {
                            TraceManager.addDev("Non Identical blocks! X");
                            // Modifying partition
                            partition.removeBlock(blockX);
                            partition.addIfNonEmpty(blockX1);
                            partition.addIfNonEmpty(blockX2);
                            partition.addIfNonEmpty(blockX3);


                            // Add two splitter to W if all are non null
                            if (!(blockX1.isEmpty()) && !(blockX2.isEmpty()) && !(blockX3.isEmpty())) {
                                // Add splitter (X, X1, X23) && (X23, X2, X3)
                                AUTPartition tmpP = new AUTPartition();
                                tmpP.addBlock(blockX);
                                tmpP.addBlock(blockX1);
                                tmpP.addBlock(AUTBlock.concat(blockX2, blockX3));
                                w.addPartition(tmpP);
                                tmpP = new AUTPartition();
                                tmpP.addBlock(AUTBlock.concat(blockX2, blockX3));
                                tmpP.addBlock(blockX2);
                                tmpP.addBlock(blockX3);
                                w.addPartition(tmpP);


                            } else {
                                // Add non empty individual block to W
                                AUTPartition tmpP;
                                if (!(blockX1.isEmpty())) {
                                    tmpP = new AUTPartition();
                                    tmpP.addBlock(blockX1);
                                    w.addPartition(tmpP);
                                }
                                if (!(blockX2.isEmpty())) {
                                    tmpP = new AUTPartition();
                                    tmpP.addBlock(blockX2);
                                    w.addPartition(tmpP);
                                }
                                if (!(blockX3.isEmpty())) {
                                    tmpP = new AUTPartition();
                                    tmpP.addBlock(blockX3);
                                    w.addPartition(tmpP);
                                }
                            }

                        }
                    }

                }

            }
        }

        TraceManager.addDev("\nAll done: it: " + maxIte + "\n---------");
        printConfiguration(partition, w);
        //TraceManager.addDev("------------------");

        // Generating new graph
        generateGraph(partition, alphabet);

    }

    // Assumes AUTElement have been added to transitions
    public void generateGraph(AUTPartition partition, HashMap<String, AUTElement> _alphabet) {
        ArrayList<AUTState> sts = new ArrayList<AUTState>();
        ArrayList<AUTTransition> trs = new ArrayList<AUTTransition>();
        HashMap<AUTBlock, AUTState> blockToNewStates = new HashMap<AUTBlock, AUTState>();

        int stID = 1;
        // We create one state per block
        // We look to the block that contains state 0 and we create the state id = 0
        for (AUTBlock bl : partition.blocks) {
            if (bl.hasState(0)) {
                AUTState st0 = new AUTState(0);
                blockToNewStates.put(bl, st0);
                sts.add(0, st0);
            } else {
                AUTState st = new AUTState(stID);
                stID++;
                blockToNewStates.put(bl, st);
                sts.add(st);
            }
        }

        // We now need to create the transitions
        // We parse all states in blocks, and consider their transition
        // We look for the destination and create a transition accordingly
        for (AUTBlock bl : partition.blocks) {
            AUTState newOrigin = blockToNewStates.get(bl);
            for (AUTState src : bl.states) {
                for (AUTTransition tr : src.outTransitions) {
                    AUTState newDestination = blockToNewStates.get(partition.getBlockWithState(tr.destination));

                    boolean foundSimilar = false;
                    AUTTransition newT = new AUTTransition(newOrigin.id, tr.transition, newDestination.id);
                    newT.elt = tr.elt;
                    for (AUTTransition testT : trs) {
                        if (testT.compareTo(newT) == 0) {
                            foundSimilar = true;
                            break;
                        }
                    }
                    if (!foundSimilar) {
                        trs.add(newT);
                        newOrigin.outTransitions.add(newT);
                        newDestination.inTransitions.add(newT);
                    }
                }
            }
        }

        states = sts;
        transitions = trs;
        nbState = sts.size();

        //TraceManager.addDev("New graph: " + toFullString());

    }


    public AUTGraph generateRefusalGraph() {
        computeStates();

        AUTState currentState = new AUTState(0);
        ArrayList<AUTState> newStates = new ArrayList<AUTState>();
        ArrayList<AUTTransition> newTransitions = new ArrayList<AUTTransition>();
        newStates.add(currentState);

        // We go thru the graph, starting at state1. Each time we meet an already handled state, we stop.
        HashSet<AUTState> metStates = new HashSet<AUTState>(); // in origin Graph
        HashMap<AUTState, AUTState> toRefusalState = new HashMap<AUTState, AUTState>();
        LinkedList<AUTState> toHandle = new LinkedList<AUTState>();
        toHandle.add(states.get(0));
        toRefusalState.put(states.get(0), currentState);

        while (toHandle.size() > 0) {
            AUTState current = toHandle.get(0);
            toHandle.remove(0);
            metStates.add(current);
            AUTState currentRefusal = toRefusalState.get(current);
            if (currentRefusal == null) {
                //TraceManager.addDev("NULL current Refusal");
            } else {
                // For each possible transition, we create a new transition to a new destination state
                for (AUTTransition tr : current.outTransitions) {
                    // Create new transition. Is a new state necessary?
                    AUTState destState = states.get(tr.destination);
                    AUTState stRefusal;
                    stRefusal = new AUTState(newStates.size());
                    newStates.add(stRefusal);
                    toRefusalState.put(destState, stRefusal);
                    AUTTransition trRefusal = new AUTTransition(current.id, tr.transition, stRefusal.id);
                    newTransitions.add(trRefusal);
                    stRefusal.addInTransition(trRefusal);
                    currentRefusal.addOutTransition(trRefusal);

                    if (!metStates.contains(destState)) {
                        if (!(toHandle.contains(destState))) {
                            toHandle.add(destState);
                        }
                    }

                }
            }
        }
        AUTGraph refusalGraph = new AUTGraph(newStates, newTransitions);

        return refusalGraph;
    }


    public AUTGraph makeTestSequencesFromRefusalGraph() {
        ArrayList<AUTState> newStates = new ArrayList<AUTState>();
        ArrayList<AUTTransition> newTransitions = new ArrayList<AUTTransition>();
        AUTState firstState = new AUTState(0);
        newStates.add(firstState);

        // Take all termination states of the refusal graph and get the shortest path
        // from the first state to this termination state
        // Add this path as a new path of the new graph

        computeStates();

        DijkstraState[] allPaths = GraphAlgorithms.LongestPathFrom(this, 0);

        for (AUTState state : states) {
            if (state.isTerminationState()) {
                int[] path = allPaths[state.id].path;
                if (path != null) {
                    AUTState currentStateN = firstState;
                    // We create a corresponding path in the new graph.
                    String s = "";
                    for (int j = 0; j < path.length; j++) {
                        s += path[j] + " ";
                    }
                    //TraceManager.addDev("path=" + s);
                    for (int i = 1; i < path.length; i++) {
                        AUTState currentState = states.get(path[i - 1]);
                        AUTState nextState = states.get(path[i]);

                        AUTTransition tr = currentState.getTransitionTo(nextState.id);
                        //TraceManager.addDev("Looking for transition");
                        if (tr != null) {
                            // We need to create the destination state
                            AUTState newDest = new AUTState(newStates.size());
                            newStates.add(newDest);
                            AUTTransition newTr = new AUTTransition(currentStateN.id, tr.transition, newDest.id);
                            newTransitions.add(newTr);
                            //TraceManager.addDev("Adding transition:" + newTr);
                            currentStateN = newDest;
                        } else {
                            //TraceManager.addDev(" -> null transitions");
                        }
                    }
                }
            }
        }


        // Making the graph
        AUTGraph testGraph = new AUTGraph(newStates, newTransitions);

        return testGraph;

    }


    private void printConfiguration(AUTPartition _part, AUTSplitter _w) {
        TraceManager.addDev("P={" + _part.toString() + "}");
        TraceManager.addDev("W={" + _w.toString() + "}");
    }

    private void printI(LinkedList<AUTBlock> I) {
        StringBuffer sb = new StringBuffer("I:");
        for (AUTBlock b : I) {
            sb.append(" " + b);
        }
        TraceManager.addDev(sb.toString());

    }


}
