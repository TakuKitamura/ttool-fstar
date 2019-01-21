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

import myutil.TraceManager;

import java.util.*;

/**
 * Class AvatarStateMachine
 * State machine, with composite states
 * Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/05/2010
 */
public class AvatarStateMachine extends AvatarElement {
    // to be used by code generator for fast access to states
    public AvatarStateElement[] allStates;


    protected List<AvatarStateMachineElement> elements;
    protected AvatarStartState startState;

    private static int ID_ELT = 0;

    protected List<AvatarStateMachineElement> states;
    protected AvatarStateMachineOwner block;

    public AvatarStateMachine(AvatarStateMachineOwner _block, String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        block = _block;
        elements = new LinkedList<AvatarStateMachineElement>();
    }

    public void setStartState(AvatarStartState _state) {
        startState = _state;
    }

    public AvatarStartState getStartState() {
        return startState;
    }

    public int getNbOfStatesElement() {
        int cpt = 0;
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarStateElement) {
                cpt++;
            }
        }
        return cpt;
    }

    public void addElement(AvatarStateMachineElement _element) {
        if (_element != null) {
            elements.add(_element);
            //TraceManager.addDev("Adding element " + _element);
            states = null;
        } else {
            TraceManager.addDev("NULL element found " + _element);
        }
    }

    public void removeElement(AvatarStateMachineElement _element) {
        elements.remove(_element);
        states = null;
    }

    public List<AvatarStateMachineElement> getListOfElements() {
        return elements;
    }

    private void makeStates() {
        states = new LinkedList<AvatarStateMachineElement>();
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarState) {
                states.add(asme);
            }
        }
    }

    public void makeAllStates() {
        int cpt = 0;
        allStates = new AvatarStateElement[getNbOfStatesElement()];
        for (AvatarStateMachineElement asme : elements) {
            if (asme instanceof AvatarStateElement) {
                allStates[cpt] = (AvatarStateElement) asme;
                cpt++;
            }
        }
    }

    public int stateNb() {
        if (states == null) {
            makeStates();
        }

        return states.size();
    }

    public int getNbOfASMGraphicalElements() {
        int cpt = 0;
        for (AvatarElement elt : elements) {
            if (elt.getReferenceObject() != null) {
                cpt++;
            }
        }
        return cpt;
    }

    public AvatarState getState(int index) {
        if (states == null) {
            makeStates();
        }

        try {
            return (AvatarState) (states.get(index));
        } catch (Exception e) {
        }
        return null;
    }

    private int getSimplifiedElementsAux( Map<AvatarStateMachineElement, Integer> simplifiedElements, Set<AvatarStateMachineElement> visited, AvatarStateMachineElement root, int counter) {
        if (visited.contains(root)) {
            Integer name = simplifiedElements.get(root);
            if (name == null) {
                if (root == this.startState)
                    simplifiedElements.put(root, new Integer(0));
                else {
                    counter++;
                    simplifiedElements.put(root, new Integer(counter));
                }
            }
        } else {
            visited.add(root);
            for (AvatarStateMachineElement asme : root.nexts)
                counter = this.getSimplifiedElementsAux(simplifiedElements, visited, asme, counter);
        }

        return counter;
    }

    public Map<AvatarStateMachineElement, Integer> getSimplifiedElements() {
        Map<AvatarStateMachineElement, Integer> simplifiedElements = new HashMap<AvatarStateMachineElement, Integer>();
        this.getSimplifiedElementsAux(simplifiedElements, new HashSet<AvatarStateMachineElement>(), startState, 0);
        return simplifiedElements;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("State machine Id=" + getID() + "\n");

        for (AvatarStateMachineElement element : elements) {
            sb.append(element.toString() + "\n");
        }

        return sb.toString();
    }


    // Add missing implicit states.
    public void makeFullStates(AvatarBlock _block) {
        addStatesToEmptyNonTerminalEmptyNext(_block);
        addStatesToTransitionsBetweenTwoNonStates(_block);
        addStatesToActionTransitions(_block);
        addStatesToNonEmptyTransitionsBetweenNonStateToState(_block);
    }

    private void addStatesToEmptyNonTerminalEmptyNext(AvatarBlock _b) {
        List<AvatarStateMachineElement> toConsider = new ArrayList<AvatarStateMachineElement>();
        for (AvatarStateMachineElement elt : elements) {
            if (!(elt instanceof AvatarStopState)) {
                if (elt.getNext(0) == null) {
                    // Missing state
                    toConsider.add(elt);
                }
            }
        }

        for (AvatarStateMachineElement elt : toConsider) {
            AvatarStopState stopMe = new AvatarStopState("stopCreated", elt.getReferenceObject());
            addElement(stopMe);
            AvatarTransition tr = new AvatarTransition(_b, "trForStopCreated", elt.getReferenceObject());
            addElement(tr);
            elt.addNext(tr);
            tr.addNext(stopMe);
        }

    }


    private void addStatesToNonEmptyTransitionsBetweenNonStateToState(AvatarBlock _block) {
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;

                if (tr.hasDelay() || tr.isGuarded() || tr.hasAction()) {
                    previous = getPreviousElementOf(elt);
                    next = elt.getNext(0);

                    // If the next is a state, but not the previous one
                    if ((previous != null) && (next != null)) {
                        if ((!(previous instanceof AvatarStateElement)) && (next instanceof AvatarStateElement)) {
                            // We create an intermediate state
                            AvatarState state = new AvatarState("IntermediateState1__" + id, elt.getReferenceObject());
                            toAdd.add(state);
                            AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState1__" + id, elt.getReferenceObject());
                            toAdd.add(at1);

                            previous.removeAllNexts();
                            previous.addNext(at1);
                            at1.addNext(state);
                            state.addNext(tr);

                            id++;
                        }
                    }
                }

            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }

    }

    private void addStatesToTransitionsBetweenTwoNonStates(AvatarBlock _block) {
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;
                previous = getPreviousElementOf(elt);
                next = elt.getNext(0);

                // If the next and previous are non states
                if ((previous != null) && (next != null)) {
                    if ((!(previous instanceof AvatarStateElement)) && (!(next instanceof AvatarStateElement))) {
                        // We create an intermediate state
                        AvatarState state = new AvatarState("IntermediateState2__" + id, elt.getReferenceObject());
                        toAdd.add(state);
                        AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState2__" + id, elt.getReferenceObject());
                        toAdd.add(at1);


                        previous.removeAllNexts();
                        previous.addNext(at1);
                        at1.addNext(state);
                        state.addNext(tr);

                        id++;
                    }
                }

            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }

    }


    // Hanlding transitions with actions which have a non state
    // after

    // Then, handling transitions with actions which have a non state
    // before
    private void addStatesToActionTransitions(AvatarBlock _block) {
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        int id = 0;
        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;

                // tr with actions?
                if (tr.getNbOfAction() > 0) {
                    previous = getPreviousElementOf(elt);
                    next = elt.getNext(0);

                    if (!(next instanceof AvatarState)) {
                        // We create an intermediate state
                        AvatarState state = new AvatarState("IntermediateState3__" + id, elt.getReferenceObject());
                        toAdd.add(state);
                        AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState3__" + id, elt.getReferenceObject());
                        toAdd.add(at1);

                        tr.removeAllNexts();
                        tr.addNext(state);
                        state.addNext(at1);
                        at1.addNext(next);

                        id++;
                    }
                }
            }

        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }
        toAdd.clear();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarTransition tr = (AvatarTransition) elt;

                // tr with actions?
                if (tr.getNbOfAction() > 0) {
                    previous = getPreviousElementOf(elt);
                    if (!(previous instanceof AvatarStateElement)) {
                        // We create an intermediate state
                        AvatarState state = new AvatarState("IntermediateState__" + id, elt.getReferenceObject());
                        toAdd.add(state);
                        AvatarTransition at1 = new AvatarTransition(_block, "TransitionForIntermediateState__" + id, elt.getReferenceObject());
                        toAdd.add(at1);

                        previous.removeAllNexts();
                        previous.addNext(at1);
                        at1.addNext(state);
                        state.addNext(tr);
                        id++;
                    }
                }
            }
        }

        for (AvatarStateMachineElement add : toAdd) {
            elements.add(add);
        }

    }

    public void removeRandoms(AvatarBlock _block) {
        int id = 0;
        List<AvatarStateMachineElement> toRemove = new ArrayList<AvatarStateMachineElement>();
        List<AvatarStateMachineElement> toAdd = new ArrayList<AvatarStateMachineElement>();
        AvatarStateMachineElement next;
        AvatarStateMachineElement previous;

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarRandom) {
                AvatarRandom random = (AvatarRandom) elt;
                previous = getPreviousElementOf(elt);
                next = elt.getNext(0);
                toRemove.add(elt);

                // Creating elements
                AvatarTransition at1 = new AvatarTransition(_block, "Transition1ForRandom__ " + elt.getName() + "__" + id, elt.getReferenceObject());
                at1.addAction(random.getVariable() + "=" + random.getMinValue());
                AvatarState randomState = new AvatarState("StateForRandom__" + elt.getName() + "__" + id, elt.getReferenceObject());
                AvatarState beforeRandom = new AvatarState("StateBeforeRandom__" + elt.getName() + "__" + id, elt.getReferenceObject());
                AvatarTransition at2 = new AvatarTransition(_block, "Transition2ForRandom__" + elt.getName() + "__" + id, elt.getReferenceObject());
                at2.setGuard("[" + random.getVariable() + " < " + random.getMaxValue() + "]");
                at2.addAction(random.getVariable() + "=" + random.getVariable() + " + 1");

                // Adding elements
                toAdd.add(at1);
                toAdd.add(randomState);
                toAdd.add(beforeRandom);
                toAdd.add(at2);

                // Linking elements
                if (previous != null) {
                    previous.removeAllNexts();
                    previous.addNext(beforeRandom);
                }
                beforeRandom.addNext(at1);
                at1.addNext(randomState);
                randomState.addNext(at2);
                randomState.addNext(next);
                at2.addNext(randomState);

                id++;

            }
        }

        for (AvatarStateMachineElement trash : toRemove) {
            elements.remove(trash);
        }

        for (AvatarStateMachineElement newOnes : toAdd) {
            elements.add(newOnes);
        }
    }


    // Assumes no after clause on composite relation
    public void removeCompositeStates(AvatarBlock _block) {
        //TraceManager.addDev("\n-------------- Remove composite states ---------------\n");

        /*LinkedList<AvatarState> lists =*/ removeAllInternalStartStates();

        AvatarTransition at = getAvatarCompositeTransition();

        if (at == null) {
            return;
        }

        // We modify all composite states with intermediate states
        for (int i = 0; i < elements.size(); i++) {
            AvatarStateMachineElement element = elements.get(i);
            if (element instanceof AvatarState) {
                modifyStateForCompositeSupport((AvatarState) element);
            }
        }

        // For each composite transition: Welink it to all the substates of the current state
        AvatarState src;
        while (((at = getAvatarCompositeTransition()) != null)) {
            src = (AvatarState) (getPreviousElementOf(at));
            elements.remove(at);

            // Add a new state after the transition
            /*String  tmp = findUniqueStateName("forCompositeTransition_state");
              AvatarState as = new AvatarState(tmp, at.getReferenceObject());
              elements.add(as);
              AvatarTransition ats = new AvatarTransition("forCompositeTransition_trans", at.getReferenceObject());
              elements.add(ats);
              ats.addNext(at.getNext(0));
              at.removeAllNexts();
              at.addNext(as);
              as.addNext(ats);*/

            // Link a clone of the transition  to all internal states

            for (int j = 0; j < elements.size(); j++) {
                AvatarStateMachineElement elt = elements.get(j);
                if ((elt instanceof AvatarState) && (elt.hasInUpperState(src))) {
                    AvatarTransition att = cloneCompositeTransition(at);
                    elt.addNext(att);
                }
            }

        }

    }

    public void removeCompositeStatesOld(AvatarBlock _block) {
        //TraceManager.addDev("\n-------------- Remove composite states ---------------\n");

        // Contains in odd index: composite state
        // even index: new state replacing the start state

        /*List<AvatarState> lists =*/ removeAllInternalStartStates();
        //List<AvatarTransition> ats;
        List<AvatarStateMachineElement> toRemove = new ArrayList<AvatarStateMachineElement>();
        List<AvatarState> states = new ArrayList<AvatarState>();

        AvatarTransition at;

        //ats = getAllAvatarCompositeTransitions();

        //at = getCompositeTransition();
        while (((at = getAvatarCompositeTransition()) != null) && (!(toRemove.contains(at)))) {
            TraceManager.addDev("*********************************** Found composite transition: " + at.toString());
            //TraceManager.addDev(_block.toString());
            if (!(toRemove.contains(getPreviousElementOf(at)))) {
                toRemove.add(getPreviousElementOf(at));
            }
            toRemove.add(at);
            AvatarTransition at2 = removeAfter(at, _block);
            AvatarState state = (AvatarState) (getPreviousElementOf(at2));

            if (!states.contains(state)) {
                TraceManager.addDev("Working on internal elements of " + state);
                states.add(state);
                addFullInternalStates(state, at2);
            }


            for (int i = 0; i < elements.size(); i++) {
                AvatarStateMachineElement element = elements.get(i);
                if (element instanceof AvatarState) {
                    if (element.hasInUpperState(state) == true) {
                        AvatarTransition at3 = cloneCompositeTransition(at2);
                        //addElement(at);
                        element.addNext(at3);
                    }
                }
            }

            //removeCompositeTransition2(at, _block);
        }

        for (AvatarStateMachineElement asme : toRemove) {
            removeElement(asme);
        }

        removeAllSuperStates();

        /*if (at != null) {
          TraceManager.addDev("********************************** Found composite transition: " + at.toString());
          // Adding intermediate states in transitions : splitting transitions
          }

          LinkedList<AvatarStateMachineElement> toRemove = new LinkedList<AvatarStateMachineElement>();

          while((at = getCompositeTransition()) != null) {
          TraceManager.addDev("*********************************** Found composite transition: " + at.toString());
          //TraceManager.addDev(_block.toString());
          if (!(toRemove.contains(getPreviousElementOf(at)))) {
          toRemove.add(getPreviousElementOf(at));
          }
          removeCompositeTransition(at, _block);

          }*/



        /*if (ats.size() > 0) {
          LinkedList<AvatarStateMachineElement> toRemove = new LinkedList<AvatarStateMachineElement>();
          ArrayList <AvatarTransition> ats2 = new ArrayList <AvatarTransition>();
          ArrayList <AvatarState> states = new ArrayList <AvatarState>();
          for(AvatarTransition at: ats) {
          if (!(toRemove.contains(getPreviousElementOf(at)))) {
          toRemove.add(getPreviousElementOf(at));
          }

          AvatarState state = (AvatarState)(getPreviousElementOf(_at));
          AvatarTransition at = removeAfter(_at, _block);
          // Put state after transition
          modifyAvatarTransition(at);
          ats2.add(at);


          }




          //removeStopStatesOf(state);

          // Remove "after" and replace them with timers


          remove

          removeCompositeTransitions(ats, _block);


          for(AvatarStateMachineElement asme: toRemove) {
          removeElement(asme);
          }
          }*/

        //TraceManager.addDev(_block.toString());


    }

    private void modifyStateForCompositeSupport(AvatarState _state) {
        // Each time there is a transition with an after or more than one action, we must rework the transition
        // We first gather all transitions internal to that state

        Vector<AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) element;
                //TraceManager.addDev("at? element=" + element);
                // Transition fully in the internal state?
                if (element.getNext(0).hasInUpperState(_state) == true) {
                    AvatarStateMachineElement previous = getPreviousElementOf(element);
                    if (previous.hasInUpperState(_state) == true) {
                        if (!(at.isEmpty())) {
                            v.add(at);
                        }
                    }
                }
            }
        }

        for (AvatarStateMachineElement element : v) {
            //TraceManager.addDev(">" + element + "<");
            splitAvatarTransition((AvatarTransition) element, _state);
        }

    }


    private void splitAvatarTransition(AvatarTransition _at, AvatarState _currentState) {
        if (_at.hasDelay()) {
            AvatarStateMachineElement element = getPreviousElementOf(_at);
            if (element.hasInUpperState(_currentState) == true) {
                if (!(element instanceof AvatarState)) {
                    // Must add an intermediate state
                    String tmp = findUniqueStateName("splitstate_after__");
                    AvatarState as = new AvatarState(tmp, _currentState.getReferenceObject());
                    addElement(as);
                    as.setHidden(true);
                    as.setState(_currentState);
                    AvatarTransition atn = new AvatarTransition(_at.getBlock(), "splittransition_after", null);
                    addElement(atn);
                    element.removeNext(_at);
                    element.addNext(atn);
                    atn.addNext(as);
                    as.addNext(_at);
                    splitAvatarTransition(_at, _currentState);
                }
            }
        } else {

            if (_at.getNbOfAction() > 1) {
                //TraceManager.addDev("New split state");
                String tmp = findUniqueStateName("splitstate_action__");
                AvatarState as = new AvatarState(tmp, null);
                as.setHidden(true);
                as.setState(_currentState);
                AvatarTransition at = (AvatarTransition) (_at.basicCloneMe(block));
                _at.removeAllActionsButTheFirstOne();
                at.removeFirstAction();
                at.addNext(_at.getNext(0));
                _at.removeAllNexts();
                _at.addNext(as);
                as.addNext(at);
                addElement(as);
                addElement(at);

                splitAvatarTransition(_at, _currentState);
            }
        }
    }

//
//    private List<AvatarTransition> getAllAvatarCompositeTransitions() {
//        List<AvatarTransition> ats = new ArrayList<AvatarTransition>();
//        for (AvatarStateMachineElement element : elements) {
//            if (element instanceof AvatarTransition) {
//                if ((isACompositeTransition((AvatarTransition) element))) {
//                    ats.add((AvatarTransition) element);
//                }
//            }
//        }
//
//        return ats;
//    }


    private void addFullInternalStates(AvatarState state, AvatarTransition _at) {
        // First:split internal transitions
        Vector<AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

        for (AvatarStateMachineElement element : elements) {
            //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
            if (element instanceof AvatarTransition) {
                //TraceManager.addDev("at? element=" + element);
                if (element.getNext(0).hasInUpperState(state) == true) {
                    if (getPreviousElementOf(element).hasInUpperState(state) == true) {
                        v.add(element);
                    }
                }
            } else if (element.hasInUpperState(state) == true) {
                // We found a candidate!
                if (element != _at) {
                    v.add(element);
                }
            }
        }

        //TraceManager.addDev("*** Analyzing components in state " + state);
        // Split avatar transitions
        for (AvatarStateMachineElement element : v) {
            //TraceManager.addDev(">" + element + "<");
            if (element instanceof AvatarTransition) {
                splitAvatarTransition((AvatarTransition) element, state);
            }
        }
    }

    private AvatarTransition getAvatarCompositeTransition() {

        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarTransition) {
                if ((isACompositeTransition((AvatarTransition) element))) {
                    return (AvatarTransition) element;

                }
            }
        }

        return null;
    }

    // Checks whether the previous element is a state with an internal state machine
    public boolean isACompositeTransition(AvatarTransition _at) {
        AvatarStateMachineElement element = getPreviousElementOf(_at);
        if (element == null) {
            return false;
        }

        if (!(element instanceof AvatarState)) {
            return false;
        }

        AvatarState state = (AvatarState) element;
        return hasInternalComponents(state);
    }

    private boolean hasInternalComponents(AvatarState _state) {
        for (AvatarStateMachineElement element : elements) {
            if (element.getState() == _state) {
                return true;
            }
        }

        return false;
    }

    /*private void removeCompositeTransition(AvatarTransition _at, AvatarBlock _block) {
      AvatarState state = (AvatarState)(getPreviousElementOf(_at));

      removeStopStatesOf(state);

      // Remove "after" and replace them with timers
      AvatarTransition at = removeAfter(_at, _block);

      // Put state after transition
      modifyAvatarTransition(at);

      Vector <AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

      for(AvatarStateMachineElement element: elements) {
      //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
      if (element instanceof AvatarTransition) {
      //TraceManager.addDev("at? element=" + element);
      if (element.getNext(0).hasInUpperState(state) == true) {
      if (getPreviousElementOf(element).hasInUpperState(state) == true) {
      v.add(element);
      }
      }
      } else if (element.hasInUpperState(state) == true) {
      // We found a candidate!
      if (element != _at) {
      v.add(element);
      }
      }
      }

      //TraceManager.addDev("*** Analyzing components in state " + state);
      // Split avatar transitions
      for(AvatarStateMachineElement element: v) {
      TraceManager.addDev(">" + element + "<");
      if (element instanceof AvatarTransition) {
      splitAvatarTransition((AvatarTransition)element, state);
      }
      }

      //TraceManager.addDev("\nAdding new elements in state");
      v.clear();
      for(AvatarStateMachineElement element: elements) {
      //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
      if (element.hasInUpperState(state) == true) {
      // We found a candidate!
      if ((element != _at) && (element != at)) {
      v.add(element);
      }
      }
      }


      for(AvatarStateMachineElement element: v) {
      adaptCompositeTransition(at, element, 0);
      }

      removeElement(at);

      }*/


    /*private void removeCompositeTransitions(ArrayList<AvatarTransition> _ats, AvatarBlock _block) {


    // Put state after transition
    modifyAvatarTransition(at);

    Vector <AvatarStateMachineElement> v = new Vector<AvatarStateMachineElement>();

    for(AvatarStateMachineElement element: elements) {
    //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
    if (element instanceof AvatarTransition) {
    //TraceManager.addDev("at? element=" + element);
    if (element.getNext(0).hasInUpperState(state) == true) {
    if (getPreviousElementOf(element).hasInUpperState(state) == true) {
    v.add(element);
    }
    }
    } else if (element.hasInUpperState(state) == true) {
    // We found a candidate!
    if (element != _at) {
    v.add(element);
    }
    }
    }

    //TraceManager.addDev("*** Analyzing components in state " + state);
    // Split avatar transitions
    for(AvatarStateMachineElement element: v) {
    TraceManager.addDev(">" + element + "<");
    if (element instanceof AvatarTransition) {
    splitAvatarTransition((AvatarTransition)element, state);
    }
    }

    //TraceManager.addDev("\nAdding new elements in state");
    v.clear();
    for(AvatarStateMachineElement element: elements) {
    //TraceManager.addDev("\nIs in composite state " + state + ": >" + element + "< ???");
    if (element.hasInUpperState(state) == true) {
    // We found a candidate!
    if ((element != _at) && (element != at)) {
    v.add(element);
    }
    }
    }

    for(AvatarStateMachineElement element: v) {
    adaptCompositeTransition(at, element, 0);
    }

    removeElement(at);

    }*/

//    private void splitAvatarTransitionOld(AvatarTransition _at, AvatarState _currentState) {
//        /*if (_at.hasCompute()) {
//          AvatarState as0 = new AvatarState("splitstate0", null);
//          AvatarState as1 = new AvatarState("splitstate1", null);
//
//
//
//          AvatarTransition at = _at.basicCloneMe();
//          _at.removeAllActions();
//          _at.removeAllNexts();
//          _at.addNext(as);
//          as.addNext(at);
//          addElement(as);
//          addElement(at);
//          splitAvatarTransition(at);
//          }*/
//
//        TraceManager.addDev(" - - - - - - - - - - Split transition nbofactions=" + _at.getNbOfAction());
//        if (_at.getNbOfAction() > 1) {
//            TraceManager.addDev("New split state");
//            AvatarState as = new AvatarState("splitstate", null);
//            as.setHidden(true);
//            as.setState(_currentState);
//            AvatarTransition at = (AvatarTransition) (_at.basicCloneMe(block));
//            _at.removeAllActionsButTheFirstOne();
//            at.removeFirstAction();
//            at.addNext(_at.getNext(0));
//            _at.removeAllNexts();
//            _at.addNext(as);
//            as.addNext(at);
//            addElement(as);
//            addElement(at);
//
//            splitAvatarTransition(_at, _currentState);
//        }
//
//        if (_at.hasDelay()) {
//            AvatarStateMachineElement element = getPreviousElementOf(_at);
//            if (element.hasInUpperState(_currentState) == true) {
//                if (!(element instanceof AvatarState)) {
//                    // Must add an intermediate state
//                    String tmp = findUniqueStateName("internalstate__");
//                    AvatarState as = new AvatarState(tmp, _currentState.getReferenceObject());
//                    addElement(as);
//                    as.setHidden(true);
//                    as.setState(_currentState);
//                    AvatarTransition atn = new AvatarTransition(_at.getBlock(), "internaltransition", null);
//                    addElement(atn);
//                    element.removeNext(_at);
//                    element.addNext(atn);
//                    atn.addNext(as);
//                    as.addNext(_at);
//                    splitAvatarTransition(_at, _currentState);
//                }
//            }
//        }
//    }

//    private void adaptCompositeTransition(AvatarTransition _at, AvatarStateMachineElement _element, int _transitionID) {
//        AvatarState as;
//        AvatarTransition at;
//        LinkedList<AvatarStateMachineElement> ll;
//        String tmp;
//
//        // It cannot be a start / stop state since they have been previously removed ..
//        if (_element instanceof AvatarActionOnSignal) {
//            AvatarStateMachineElement element = _element.getNext(0);
//            if (element instanceof AvatarTransition) {
//                if (!(((AvatarTransition) element).isEmpty())) {
//                    //We need to create a new state
//                    tmp = findUniqueStateName("internalstate__");
//                    TraceManager.addDev("Creating state with name=" + tmp);
//                    as = new AvatarState(tmp, null);
//                    addElement(as);
//                    as.setHidden(true);
//                    at = new AvatarTransition(_at.getBlock(), "internaltransition", null);
//                    addElement(at);
//                    //_element -> at -> as -> element
//
//                    _element.removeNext(element);
//                    _element.addNext(at);
//
//                    at.addNext(as);
//                    as.addNext(element);
//
//                    at = cloneCompositeTransition(_at);
//                    //addElement(at);
//                    as.addNext(at);
//                } else {
//                    // We see if a state follows it. Otherwise, we create one
//                    if (!(element.getNext(0) instanceof AvatarState)) {
//                        //We need to create a new state
//                        tmp = findUniqueStateName("internalstate__");
//                        TraceManager.addDev("Creating state with name=" + tmp);
//                        as = new AvatarState(tmp, null);
//                        addElement(as);
//                        as.setHidden(true);
//                        at = new AvatarTransition(_at.getBlock(), "internaltransition", null);
//                        addElement(at);
//                        //_element -> at -> as -> element
//
//                        _element.removeNext(element);
//                        _element.addNext(at);
//
//                        at.addNext(as);
//                        as.addNext(element);
//
//                        at = cloneCompositeTransition(_at);
//                        //addElement(at);
//                        as.addNext(at);
//
//                    } else {
//                        //We link to this state-> will be done later
//                    }
//                }
//            }
//            /*ll = getPreviousElementsOf(_element);
//              for(AvatarStateMachineElement element: ll) {
//              if (element instanceof AvatarTransition) {
//              // if empty transition: we do just nothing
//              if (!(((AvatarTransition)element).isEmpty())) {
//              tmp = findUniqueStateName("internalstate__");
//              TraceManager.addDev("Creating state with name=" + tmp);
//              as = new AvatarState(tmp, null);
//              as.setHidden(true);
//              element.removeNext(_element);
//              element.addNext(as);
//              at = new AvatarTransition("internaltransition", null);
//              addElement(at);
//              at.addNext(_element);
//              as.addNext(at);
//              addElement(as);
//
//              at = cloneCompositeTransition(_at);
//              addElement(at);
//              as.addNext(at);
//              }
//
//              } else {
//              // Badly formed machine!
//              TraceManager.addError("Badly formed sm (removing composite transition)");
//              }
//              }*/
//
//        } else if (_element instanceof AvatarState) {
//            at = cloneCompositeTransition(_at);
//            //addElement(at);
//            _element.addNext(at);
//        } else if (_element instanceof AvatarTransition) {
//            // Nothing to do since they shall have been split before
//        } else {
//            // Nothing to do either
//        }
//    }


    // Return the first previous element met. Shall be used preferably only for transitions
    private AvatarStateMachineElement getPreviousElementOf(AvatarStateMachineElement _elt) {
        for (AvatarStateMachineElement element : elements) {
            if (element.hasNext(_elt)) {
                return element;
            }
        }

        return null;
    }

    private List<AvatarStateMachineElement> getPreviousElementsOf(AvatarStateMachineElement _elt) {
        List<AvatarStateMachineElement> ll = new LinkedList<AvatarStateMachineElement>();
        for (AvatarStateMachineElement element : elements) {
            if (element.hasNext(_elt)) {
                ll.add(element);
            }
        }

        return ll;
    }

    public AvatarState getStateWithName(String _name) {
        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarState) {
                if (element.getName().compareTo(_name) == 0) {
                    return (AvatarState) element;
                }
            }
        }
        return null;
    }


    public List<AvatarActionOnSignal> getAllAOSWithName(String _name) {
        List<AvatarActionOnSignal> list = new ArrayList<AvatarActionOnSignal>();
        for (AvatarStateMachineElement element : elements) {
            if (element instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) element;
                if (aaos.getSignal().getName().compareTo(_name) == 0) {
                    list.add(aaos);
                }
            }
        }
        return list;
    }

    // All transitions reaching a state that has an internal start state
    // shall in fact go directly to the nexts of the start state
    public List<AvatarState> removeAllInternalStartStates() {
        // identify allstart state
        List<AvatarStartState> ll = new LinkedList<AvatarStartState>();

        List<AvatarState> removedinfos = new LinkedList<AvatarState>();

        for (AvatarStateMachineElement element : elements) {
            if ((element instanceof AvatarStartState) && (element.getState() != null)) {
                //TraceManager.addDev("-> -> found an internal state state");
                ll.add((AvatarStartState) element);
            }
        }

        AvatarState as0;
        List<AvatarStateMachineElement> le;
        for (AvatarStartState as : ll) {
            AvatarState astate = as.getState();
            if (as != null) {
                le = getPreviousElementsOf(astate);
                if (le.size() > 0) {
                    as0 = new AvatarState("entrance__" + astate.getName(), astate.getReferenceObject());
                    as0.addNext(as.getNext(0));
                    as0.setHidden(true);
                    as0.setState(astate);
                    for (AvatarStateMachineElement element : le) {
                        if (element instanceof AvatarTransition) {
                            element.removeAllNexts();
                            element.addNext(as0);
                        } else {
                            TraceManager.addDev("Badly formed state machine");
                        }
                    }

                    removedinfos.add(as.getState());
                    removedinfos.add(as0);

                    // Remove the start state and its next transition
                    removeElement(as);
                    addElement(as0);

                    //TraceManager.addDev("-> -> removed an internal state state!");
                } else {
                    TraceManager.addDev("Badly formed state machine");
                }
            }
        }

        return removedinfos;

    }

    public void removeAllSuperStates() {
        for (AvatarStateMachineElement element : elements) {
            element.setState(null);
        }
    }

    public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
        for (AvatarStateMachineElement element : elements) {
            if (element.hasReferenceObject(_o)) {
                return element;
            }
        }
        return null;
    }

    public Object getReferenceObjectFromID(int _ID) {
        for (AvatarStateMachineElement element : elements) {
            if (element.getID() == _ID) {
                return element.getReferenceObject();
            }
        }
        return null;
    }

    // Return true iff at least one timer was removed
    public boolean removeTimers(AvatarBlock _block, String timerAttributeName) {
        AvatarSetTimer ast;
        AvatarTimerOperator ato;

        List<AvatarStateMachineElement> olds = new LinkedList<AvatarStateMachineElement>();
        List<AvatarStateMachineElement> news = new LinkedList<AvatarStateMachineElement>();


        for (AvatarStateMachineElement elt : elements) {
            // Set timer...
            if (elt instanceof AvatarSetTimer) {
                ast = (AvatarSetTimer) elt;
                AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), _block.getAvatarSignalWithName("set__" + ast.getTimer().getName()), elt.getReferenceObject());
                aaos.addValue(timerAttributeName);
                olds.add(elt);
                news.add(aaos);

                // Modifying the transition just before
                List<AvatarStateMachineElement> previous = getPreviousElementsOf(ast);
                if (previous.size() == 1) {
                    if (previous.get(0) instanceof AvatarTransition) {
                        AvatarTransition at = (AvatarTransition) (previous.get(0));
                        TraceManager.addDev("Timer value setting=" + ast.getTimerValue());
                        at.addAction(timerAttributeName + " = " + ast.getTimerValue());
                    } else {
                        TraceManager.addError("The element before a set time is not a transition!");
                    }
                } else {
                    TraceManager.addError("More than one transition before a set time!");
                }

                // Reset timer
            } else if (elt instanceof AvatarResetTimer) {
                ato = (AvatarTimerOperator) elt;
                AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), _block.getAvatarSignalWithName("reset__" + ato.getTimer().getName()), elt.getReferenceObject());
                olds.add(elt);
                news.add(aaos);

                // Expire timer
            } else if (elt instanceof AvatarExpireTimer) {
                ato = (AvatarTimerOperator) elt;
                AvatarActionOnSignal aaos = new AvatarActionOnSignal(elt.getName(), _block.getAvatarSignalWithName("expire__" + ato.getTimer().getName()), elt.getReferenceObject());
                olds.add(elt);
                news.add(aaos);
            }
        }

        // Replacing old elements with new ones
        AvatarStateMachineElement oldelt, newelt;
        for (int i = 0; i < olds.size(); i++) {
            oldelt = olds.get(i);
            newelt = news.get(i);
            replace(oldelt, newelt);
        }

        return (olds.size() > 0);
    }

    public void replace(AvatarStateMachineElement oldone, AvatarStateMachineElement newone) {

        //TraceManager.addDev("Replacing " + oldone + " with " + newone);

        addElement(newone);
        removeElement(oldone);

        // Previous elements
        List<AvatarStateMachineElement> previous = getPreviousElementsOf(oldone);
        for (AvatarStateMachineElement elt : previous) {
            elt.replaceAllNext(oldone, newone);
        }

        // Next elements
        for (int i = 0; i < oldone.nbOfNexts(); i++) {
            AvatarStateMachineElement elt = oldone.getNext(i);
            newone.addNext(elt);
        }
    }

    public AvatarTransition removeAfter(AvatarTransition _at, AvatarBlock _block) {
        String delay = _at.getMinDelay();
        if ((delay == null) || (delay.trim().length() == 0)) {
            return _at;
        }


        // We have to use a timer for this transition
        AvatarAttribute aa = _block.addTimerAttribute("timer__");
        AvatarAttribute val = _block.addIntegerAttribute(aa.getName() + "_val");

        //TraceManager.addDev("ADDING TIMER: " + aa.getName());

        // Timer is set at the entrance in the composite state
        List<AvatarTransition> ll = findEntranceTransitionElements((AvatarState) (getPreviousElementOf(_at)));

        AvatarTransition newat0, newat1;
        AvatarSetTimer ast;
        AvatarRandom ar;
        AvatarState as;
        for (AvatarTransition att : ll) {
            //TraceManager.addDev(" ------------------ Dealing with an entrance transition");
            ar = new AvatarRandom("randomfortimer", _block.getReferenceObject());
            ar.setVariable(val.getName());
            ar.setValues(_at.getMinDelay(), _at.getMaxDelay());

            ast = new AvatarSetTimer("settimer_" + aa.getName(), _block.getReferenceObject());
            ast.setTimerValue(val.getName());
            ast.setTimer(aa);

            newat0 = new AvatarTransition(_block, "transition_settimer_" + aa.getName(), _block.getReferenceObject());
            newat1 = new AvatarTransition(_block, "transition_settimer_" + aa.getName(), _block.getReferenceObject());

            elements.add(ar);
            elements.add(ast);
            elements.add(newat0);
            elements.add(newat1);

            newat1.addNext(att.getNext(0));
            att.removeAllNexts();
            att.addNext(ar);
            ar.addNext(newat0);
            newat0.addNext(ast);
            ast.addNext(newat1);

        }

        // Wait for timer expiration on the transition
        AvatarExpireTimer aet = new AvatarExpireTimer("expiretimer_" + aa.getName(), _block.getReferenceObject());
        aet.setTimer(aa);
        newat0 = new AvatarTransition(_block, "transition0_expiretimer_" + aa.getName(), _block.getReferenceObject());
        newat1 = new AvatarTransition(_block, "transition1_expiretimer_" + aa.getName(), _block.getReferenceObject());
        as = new AvatarState("state1_expiretimer_" + aa.getName(), _block.getReferenceObject());
        addElement(aet);
        addElement(newat0);
        addElement(newat1);
        addElement(as);

        newat0.addNext(aet);
        aet.addNext(newat1);
        newat1.addNext(as);
        _at.setDelays("", "");

        List<AvatarStateMachineElement> elts = getElementsLeadingTo(_at);

        for (AvatarStateMachineElement elt : elts) {
            elt.removeNext(_at);
            elt.addNext(newat0);
        }

        as.addNext(_at);

        return newat0;
    }

    public List<AvatarTransition> findEntranceTransitionElements(AvatarState _state) {
        //TraceManager.addDev("Searching for transitions entering:" + _state.getName());
        List<AvatarTransition> ll = new LinkedList<AvatarTransition>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarTransition) {
                AvatarStateMachineElement element = getPreviousElementOf(elt);
                if (elt.getNext(0) == _state) {
                    ll.add((AvatarTransition) elt);
                } else if (element.inAnUpperStateOf(_state) && (elt.getNext(0).getState() == _state)) {
                    ll.add((AvatarTransition) elt);
                }
            }
        }
        //TraceManager.addDev("Nb of elements found:" + ll.size());
        return ll;
    }

    public List<AvatarStateMachineElement> getElementsLeadingTo(AvatarStateMachineElement _elt) {
        List<AvatarStateMachineElement> elts = new LinkedList<AvatarStateMachineElement>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt.hasNext(_elt)) {
                elts.add(elt);
            }
        }

        return elts;
    }

    public void modifyAvatarTransition(AvatarTransition _at) {
        /*if ((_at.getNbOfAction() > 0) || (_at.hasCompute())) {
          return;
          }*/

        AvatarStateMachineElement next = _at.getNext(0);

        if (next instanceof AvatarState) {
            return;
        } else if ((next instanceof AvatarTimerOperator) || (next instanceof AvatarActionOnSignal)) {

            TraceManager.addDev("-> Timer modification");

            AvatarState myState = new AvatarState("statefortransition__" + ID_ELT, _at.getReferenceObject());
            myState.setHidden(true);
            AvatarTransition at2 = new AvatarTransition(_at.getBlock(), "transitionfortransition__" + ID_ELT, _at.getReferenceObject());
            ID_ELT++;
            AvatarTransition at1 = (AvatarTransition) (next.getNext(0));

            next.removeAllNexts();
            next.addNext(at2);
            at2.addNext(myState);
            myState.addNext(at1);

            addElement(myState);
            addElement(at2);

            return;
        } else {
            AvatarState myState = new AvatarState("statefortransition__" + ID_ELT, _at.getReferenceObject());
            AvatarTransition at = new AvatarTransition(_at.getBlock(), "transitionfortransition__", _at.getReferenceObject());
            at.addNext(_at.getNext(0));
            _at.removeAllNexts();
            _at.addNext(myState);
            myState.addNext(at);
            addElement(myState);
            addElement(at);
            return;
        }
    }

    public AvatarTransition cloneCompositeTransition(AvatarTransition _at) {
        //TraceManager.addDev("Must clone: " + _at);
        // We clone elements until we find a state!
        AvatarStateMachineElement tomake, current;
        AvatarStateMachineElement tmp;
        AvatarTransition at = (AvatarTransition) (_at.basicCloneMe(block));
        addElement(at);

        current = _at.getNext(0);
        tomake = at;

        while ((current != null) && !(current instanceof AvatarState)) {
            //TraceManager.addDev("Cloning: " + current);
            tmp = current.basicCloneMe(block);
            addElement(tmp);
            tomake.addNext(tmp);
            tomake = tmp;
            current = current.getNext(0);
            if (current == null) {
                break;
            }
        }

        if (current == null) {
            TraceManager.addDev("NULL CURRENT !!! NULL CURRENT");
        }

        tomake.addNext(current);

        return at;

        /*if ((_at.getNbOfAction() > 0) || (_at.hasCompute())) {
          return _at.basicCloneMe();
          }

          AvatarStateMachineElement next = _at.getNext(0);

          if (next instanceof AvatarActionOnSignal) {
          AvatarTransition at = _at.
          AvatarActionOnSignal aaos = ((AvatarActionOnSignal)next).basicCloneMe();
          addElement(at);
          addElement(aaos);
          at.addNext(aaos);
          aaos.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          if (next instanceof AvatarExpireTimer) {
          AvatarTransition at = _at.basicCloneMe();
          AvatarExpireTimer aet = ((AvatarExpireTimer)next).basicCloneMe();
          AvatarTransition
          addElement(at);
          addElement(aet);
          at.addNext(aet);
          aet.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          if (next instanceof AvatarResetTimer) {
          AvatarTransition at = _at.basicCloneMe();
          AvatarResetTimer art = ((AvatarResetTimer)next).basicCloneMe();
          addElement(at);
          addElement(art);
          at.addNext(art);
          art.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          if (next instanceof AvatarSetTimer) {
          AvatarTransition at = _at.basicCloneMe();
          AvatarSetTimer ast = ((AvatarSetTimer)next).basicCloneMe();
          addElement(at);
          addElement(ast);
          at.addNext(ast);
          ast.addNext(next.getNext(0)); // Shall be a state!
          return at;
          }

          return _at.basicCloneMe();*/

    }

    public void removeStopStatesOf(AvatarState _as) {
        List<AvatarStopState> ll = new LinkedList<AvatarStopState>();

        for (AvatarStateMachineElement elt : elements) {
            if (elt instanceof AvatarStopState) {
                if (elt.getState() == _as) {
                    ll.add((AvatarStopState) elt);
                }
            }
        }

        for (AvatarStopState ass : ll) {
            TraceManager.addDev("Removed a stop state");
            AvatarState astate = new AvatarState("OldStopState", ass.getReferenceObject());
            astate.setState(ass.getState());
            replace(ass, astate);
        }
    }

    public String findUniqueStateName(String name) {
        int id = 0;
        boolean found;

        while (id < 10000) {
            found = false;
            for (AvatarStateMachineElement elt : elements) {
                if (elt instanceof AvatarState) {
                    if (elt.getName().compareTo(name + id) == 0) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return name + id;
            }
            id++;
        }
        return name + id;
    }

    public void handleUnfollowedStartState(AvatarStateMachineOwner _block) {
        if (startState.nbOfNexts() == 0) {
            AvatarStopState stopState = new AvatarStopState("__StopState", startState.getReferenceObject());
            AvatarTransition at = new AvatarTransition(_block, "__toStop", startState.getReferenceObject());
            addElement(stopState);
            addElement(at);
            startState.addNext(at);
            at.addNext(stopState);
        }
    }

    /**
     * Removes all function calls by inlining them.
     *
     * @param block The block from which library function calls should be removed.
     */
    public void removeLibraryFunctionCalls(AvatarBlock block) {
        /* Perform BFS for AvatarLibraryFunctionCall elements. When one is found, replace it by the state machine and fix the links */
        LinkedList<AvatarStateMachineElement> toVisit = new LinkedList<AvatarStateMachineElement>();
        toVisit.add(this.startState);
        Set<AvatarStateMachineElement> visited = new HashSet<AvatarStateMachineElement>();
        Map<AvatarLibraryFunctionCall, AvatarStateMachineElement> callsTranslated = new HashMap<AvatarLibraryFunctionCall, AvatarStateMachineElement>();

        while (!toVisit.isEmpty()) {
            /* Get the first element of the queue */
            AvatarStateMachineElement curAsme = toVisit.remove();
            if (visited.contains(curAsme))
                continue;

            if (curAsme instanceof AvatarLibraryFunctionCall) {
                AvatarLibraryFunctionCall alfc = (AvatarLibraryFunctionCall) curAsme;
                /* Create a state that will be used as an entry point for the sub-state machine */
                AvatarState firstState = new AvatarState("entry_" + alfc.getLibraryFunction().getName() + "_" + alfc.getCounter(), curAsme.getReferenceObject());

                /* Add this state to the mapping so that future state can use it to replace their next element */
                callsTranslated.put(alfc, firstState);

                /* inline the function call */
                AvatarStateMachineElement lastState = alfc.inlineFunctionCall(block, firstState);

                /* Add the next elements to the newly created last state */
                for (AvatarStateMachineElement asme : curAsme.getNexts())
                    lastState.addNext(asme);

                /* Use the translated function call's first element as current element */
                curAsme = firstState;
            }

            /* Add current element to the visited set */
            visited.add(curAsme);

            /* Loop through the next elements */
            int i = 0;
            if (curAsme.getNexts() != null) {
                for (AvatarStateMachineElement asme : curAsme.getNexts()) {
                    /* Check if it is a function call */
                    if (asme instanceof AvatarLibraryFunctionCall) {
                        AvatarStateMachineElement replaceBy = callsTranslated.get(asme);
                        /* Check if function call has already been translated */
                        if (replaceBy != null) {
                            /* replace by the translated function call */
                            curAsme.removeNext(i);
                            curAsme.addNext(replaceBy);

                            /* new next element has been added at the end of the list so we need to fix i */
                            i--;
                        } else {
                            /* mark the function call and the current state to be visited */
                            toVisit.add(asme);
                            toVisit.add(curAsme);
                            visited.remove(curAsme);
                        }
                    } else
                        toVisit.add(asme);

                    i++;
                }
            }
        }
    }

    /**
     * Removes all empty transitions between two states.
     * This concerns also the start state, and end states.
     * DO NOT take into account code of states, and start states
     *
     * @param block The block containing the state machine
     * @param _canOptimize boolean data
     */
    public void removeEmptyTransitions(AvatarBlock block, boolean _canOptimize) {

        //TraceManager.addDev("Remove empty transitions with optimize=" + _canOptimize);

        // Look for such a transition
        // states -> tr -> state with tr is empty
        // a tr is empty when it has no action or guard
        AvatarStateElement foundState1 = null, foundState2 = null;
        AvatarTransition foundAt = null;


        for (AvatarStateMachineElement elt : elements) {
            if ((elt instanceof AvatarStateElement) && (!(elt instanceof AvatarStartState))) {
                if (elt.getNexts().size() == 1) {
                    AvatarTransition at = (AvatarTransition) (elt.getNext(0));
                    if (at.getNext(0) instanceof AvatarStateElement) {
                        if (at.isEmpty() && at.hasNonDeterministicGuard()) {
                            if ((_canOptimize) && (!(elt.isCheckable()))) {
                                TraceManager.addDev("State found:" + elt);
                                foundState1 = (AvatarStateElement) elt;
                                foundAt = at;
                                foundState2 = (AvatarStateElement) (at.getNext(0));
                                break;
                            }

                        }
                    }
                }
            }
        }

        // Found?
        if (foundState1 != null) {
            if (foundState1 == foundState2) {
                // We simply remove the transition
                TraceManager.addDev("Found same state -> removing the transitions");
                removeElement(foundAt);
                // removing from the next of foundState1
                foundState1.removeNext(foundAt);

            } else {
                // Must remove state1 and at, and link all previous of state 1 to state2
                TraceManager.addDev("Found 2 states state1=" + foundState1.getName() + " state2=" + foundState2.getName());
                for (AvatarStateMachineElement elt : getPreviousElementsOf(foundState1)) {
                    elt.replaceAllNext(foundState1, foundState2);
                }
                removeElement(foundAt);
                removeElement(foundState1);
                foundState2.addReferenceObjectFrom(foundState1);

            }
            removeEmptyTransitions(block, _canOptimize);
        }
    }

    public int getIndexOfStartState() {
        if (allStates == null) {
            return -1;
        }

        for (int i = 0; i < allStates.length; i++) {
            if (allStates[i] instanceof AvatarStartState) {
                return i;
            }
        }

        return -1;
    }

    public int getIndexOfState(AvatarStateElement _ase) {
        if (allStates == null) {
            return -1;
        }

        for (int i = 0; i < allStates.length; i++) {
            if (allStates[i] == _ase) {
                return i;
            }
        }

        return -1;
    }


    // Fills the current state machine by cloning the current one

    public void advancedClone(AvatarStateMachine _newAsm, AvatarStateMachineOwner _newBlock) {
        // Elements
        HashMap<AvatarStateMachineElement, AvatarStateMachineElement> correspondenceMap = new HashMap<AvatarStateMachineElement, AvatarStateMachineElement>();
        for (AvatarStateMachineElement elt : elements) {
            AvatarStateMachineElement ae;
            ae = elt.basicCloneMe(_newBlock);

            _newAsm.addElement(ae);

            if (ae instanceof AvatarStartState) {
                _newAsm.setStartState((AvatarStartState) ae);
            }
            correspondenceMap.put(elt, ae);
        }

        // Other attributes
        for (AvatarStateMachineElement elt : elements) {
            AvatarStateMachineElement ae = correspondenceMap.get(elt);
            if (ae != null) {
                elt.fillAdvancedValues(ae, correspondenceMap);
            }
        }
    }
    
    public AvatarTransition findEmptyTransition( 	final AvatarStateMachineElement elementSource,
    												final AvatarStateMachineElement elementTarget ) {
        for ( final AvatarStateMachineElement element : elements ) {
            if ( element instanceof AvatarTransition ) {
            	final AvatarTransition transition = (AvatarTransition) element;
            	
            	if ( transition.isEmpty() && !transition.getNexts().isEmpty() ) {
            		if ( getPreviousElementOf( transition ) == elementSource && transition.getNexts().get( 0 ) == elementTarget ) {
            			return transition;
            		}
            	}
            }
        }
        
        return null;
    }
}
