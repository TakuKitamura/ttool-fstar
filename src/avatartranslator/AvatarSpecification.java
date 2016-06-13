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
   * Class AvatarSpecification
   * Avatar specification
   * Creation: 20/05/2010
   * @version 1.0 20/05/2010
   * @author Ludovic APVRILLE
   * @see
   */


package avatartranslator;

import java.util.*;


import avatartranslator.modelchecker.*;
import myutil.*;

public class AvatarSpecification extends AvatarElement {
    public static String[] ops = {">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", ",", "!", "?", "{", "}", "|", "&"};
    
    private LinkedList<AvatarBlock> blocks;
    private LinkedList<AvatarRelation> relations;

    /**
     * The list of all library functions that can be called.
     */
    private LinkedList<AvatarLibraryFunction> libraryFunctions;

    private String applicationCode;

    //private AvatarBroadcast broadcast;

    private LinkedList<AvatarPragma> pragmas;
    private LinkedList<String> safety_pragmas;
    private LinkedList<AvatarConstant> constants;

    private boolean robustnessMade = false;

    private Object informationSource; // element from which the spec has been built


    public AvatarSpecification(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        blocks = new LinkedList<AvatarBlock>();
        relations = new LinkedList<AvatarRelation>();
        //broadcast = new AvatarBroadcast("Broadcast", _referenceObject);
        pragmas = new LinkedList<AvatarPragma>();
	constants = new LinkedList<AvatarConstant>();
	safety_pragmas = new LinkedList<String>();
        this.constants.add (AvatarConstant.FALSE);
        this.constants.add (AvatarConstant.TRUE);

        this.libraryFunctions = new LinkedList<AvatarLibraryFunction> ();
    }

    public LinkedList<AvatarLibraryFunction> getListOfLibraryFunctions () {
        return this.libraryFunctions;
    }

    public void addLibraryFunction (AvatarLibraryFunction libraryFunction) {
        this.libraryFunctions.add (libraryFunction);
    }

    // For code generation
    public void addApplicationCode(String _code) {
        if (_code == null) {
            return;
        }
        if (applicationCode == null) {
            applicationCode = _code;
            return;
        }
        applicationCode += _code + "\n";
    }

    public String getApplicationCode() {
        if (applicationCode == null) {
            return "";
        }
        return applicationCode;
    }

    public boolean hasApplicationCode() {
        if (applicationCode == null) {
            return false;
        }
        return (applicationCode.indexOf("__user_init()") != -1);
    }


    public void setInformationSource(Object o) {
        informationSource = o;
    }

    public Object getInformationSource() {
        return informationSource;
    }

    public LinkedList<AvatarBlock> getListOfBlocks() {
        return blocks;
    }

    public LinkedList<AvatarRelation> getRelations() {
        return relations;
    }

    public LinkedList<AvatarPragma> getPragmas() {
        return pragmas;
    }
    public LinkedList<String> getSafetyPragmas() {
        return safety_pragmas;
    }
    public LinkedList<AvatarConstant> getAvatarConstants() {
        return constants;
    }

    public int getNbOfASMGraphicalElements() {
	int cpt = 0;
	for(AvatarBlock block: blocks) {
	    cpt += block.getNbOfASMGraphicalElements();
	}
	return cpt;
    }

    public boolean isASynchronousSignal(AvatarSignal _as) {
        for(AvatarRelation ar: relations) {
            if (ar.containsSignal(_as)) {
                return !(ar.isAsynchronous());
            }
        }

        return false;

    }

    //DG
 public boolean ASynchronousExist(){
      LinkedList<AvatarRelation> asynchro = getRelations();
      for ( AvatarRelation ar : asynchro )
        if (ar.isAsynchronous())
          return true;
      return false;
    }
    // end DG
    public void addBlock(AvatarBlock _block) {
        blocks.add(_block);
    }

    public void addRelation(AvatarRelation _relation) {
        relations.add(_relation);
    }

    /*public void addBroadcastSignal(AvatarSignal _as) {
      if (!broadcast.containsSignal(_as)) {
      broadcast.addSignal(_as);
      }
      }

      public AvatarBroadcast getBroadcast() {
      return broadcast;
      }*/

    public void addPragma(AvatarPragma _pragma) {
        pragmas.add(_pragma);
    }
    public void addSafetyPragma(String _pragma) {
        safety_pragmas.add(_pragma);
    }
    public void addConstant(AvatarConstant _constant) {
	//Only add unique constants
	if (this.getAvatarConstantWithName(_constant.getName())==null){
	    constants.add(_constant);
	}
    }

    public String toString() {
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer("Blocks:\n");
        for(AvatarBlock block: blocks) {
            sb.append("*** " + block.toString()+"\n");
        }
        sb.append("\nRelations:\n");
        for(AvatarRelation relation: relations) {
            sb.append("Relation:" + relation.toString() + "\n");
        }
        sb.append("\nPragmas:\n");
        for(AvatarPragma pragma: pragmas) {
            sb.append("Pragma:" + pragma.toString() + "\n");
        }
	for (AvatarConstant constant: constants){
	    sb.append("Constant:" + constant.toString() + "\n");
	}
        return sb.toString();

    }

    public AvatarBlock getBlockWithName(String _name) {
        for(AvatarBlock block: blocks) {
            if (block.getName().compareTo(_name)== 0) {
                return block;
            }
        }

        return null;
    }

    public AvatarConstant getAvatarConstantWithName(String _name) {
        for(AvatarConstant constant: constants) {
            if (constant.getName().compareTo(_name)== 0) {
                return constant;
            }
        }

        return null;
    }
    public static String putAttributeValueInString(String _source, AvatarAttribute _at) {
        return Conversion.putVariableValueInString(ops, _source, _at.getName(), _at.getDefaultInitialValue());
    }

    public static String putRealAttributeValueInString(String _source, AvatarAttribute _at) {
        return Conversion.putVariableValueInString(ops, _source, _at.getName(), _at.getInitialValue());
    }


    public void removeCompositeStates() {
        for(AvatarBlock block: blocks) {
	    //TraceManager.addDev("- - - - - - - - Removing composite states of " + block);
            block.getStateMachine().removeCompositeStates(block);
        }
    }


    public void makeFullStates() {
	for(AvatarBlock block: blocks) {
	    //TraceManager.addDev("- - - - - - - - Making full states of " + block);
            block.getStateMachine().makeFullStates(block);
        }
    }

    public void removeRandoms() {
	for(AvatarBlock block: blocks) {
	    //TraceManager.addDev("- - - - - - - - Removing randoms of " + block);
            block.getStateMachine().removeRandoms(block);
        }
    }

    public void removeTimers() {
        renameTimers();

        LinkedList<AvatarBlock> addedBlocks = new LinkedList<AvatarBlock>();
        for(AvatarBlock block: blocks) {
	    if (block.hasTimerAttribute()) {
		block.removeTimers(this, addedBlocks);
	    }
        }

        for(int i=0; i<addedBlocks.size(); i++) {
            addBlock(addedBlocks.get(i));
        }
    }

    private void renameTimers() {
        // Check whether timers have the same name in different blocks
        ArrayList<AvatarAttribute> allTimers = new ArrayList<AvatarAttribute>();
        for(AvatarBlock block: blocks) {
            allTimers.clear();
            block.putAllTimers(allTimers);
            for(AvatarAttribute att: allTimers) {
                for(AvatarBlock bl: blocks) {
                    if (block != bl) {
                        if (bl.hasTimer(att.getName())) {
                            // Must change name of timer
                            TraceManager.addDev("Changing name of Timer:" + att);
                            att.setName(att.getName() + "__" + block.getName());
                        }
                    }
                }
            }
        }

    }

    public boolean areSynchronized(AvatarSignal as1, AvatarSignal as2) {
	AvatarRelation ar = getAvatarRelationWithSignal(as1);
	if (ar == null) {
	    return false;
	}

	int index1 = ar.getIndexOfSignal(as1);
	int index2 = ar.getIndexOfSignal(as2);

	return (index1 == index2);
    }

    public AvatarRelation getAvatarRelationWithSignal(AvatarSignal _as) {
        for(AvatarRelation ar: relations) {
            if (ar.hasSignal(_as) > -1) {
                return ar;
            }
        }
        return null;
    }

    public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
        AvatarStateMachineElement asme;
        for(AvatarBlock block: blocks) {
            asme = block.getStateMachineElementFromReferenceObject(_o);
            if (asme != null) {
                return asme;
            }
        }
        return null;
    }

    public AvatarBlock getBlockFromReferenceObject(Object _o) {
        for(AvatarBlock block: blocks) {
            if (block.containsStateMachineElementWithReferenceObject(_o)) {
                return block;
            }
        }
        return null;
    }

    public AvatarBlock getBlockWithAttribute(String _attributeName) {
        int index;

        for(AvatarBlock block: blocks) {
            index = block.getIndexOfAvatarAttributeWithName(_attributeName);
            if (index > -1) {
                return block;
            }
        }
        return null;
    }

    public void removeElseGuards() {
        for (AvatarBlock block: blocks) {
            AvatarStateMachine asm = block.getStateMachine ();
            if (asm == null)
                continue;
            for (AvatarStateMachineElement asme: asm.getListOfElements ()) {
                if (! (asme instanceof AvatarState))
                    continue;

                for (AvatarStateMachineElement next: asme.getNexts ()) {
                    if (! (next instanceof AvatarTransition))
                        continue;
                    AvatarTransition at = (AvatarTransition) next;
                    AvatarGuard ancientGuard = at.getGuard ();

                    if (ancientGuard == null)
                        continue;

                    at.setGuard (ancientGuard.getRealGuard (asme));
                }
            }
        }
    }

    /**
     * Removes all function calls by inlining them.
     */
    public void removeLibraryFunctionCalls () {
        for (AvatarBlock block: this.blocks) {
            AvatarStateMachine asm = block.getStateMachine ();
            if (asm == null)
                continue;

            asm.removeLibraryFunctionCalls (block);
        }
    }

    public boolean hasLossyChannel() {
        for(AvatarRelation relation: relations)
            if (relation.isLossy())
                return true;

        return false;
    }


    public void removeEmptyTransitions(boolean _canOptimize) {
	for (AvatarBlock block: this.blocks) {
            AvatarStateMachine asm = block.getStateMachine ();
            if (asm != null)
		asm.removeEmptyTransitions (block, _canOptimize);
        }
    }

    public void makeRobustness() {
        TraceManager.addDev("Make robustness");
        if (robustnessMade) {
            return;
        }

        /*robustnessMade = true;

          TraceManager.addDev("Testing lossy channels");

          if (hasLossyChannel()) {
          TraceManager.addDev("Making robustness");
          int idstate = 0;
          for(AvatarBlock block: blocks) {
          idstate = block.getStateMachine().makeMessageLostRobustness(idstate);
          }

          /*AvatarBlock ab = new AvatarBlock("Robustness__", this.getReferenceObject());
          addBlock(ab);
          AvatarMethod am = new AvatarMethod("messageLost", null);
          ab.addMethod(am);
          AvatarStateMachine asm = ab.getStateMachine();
          AvatarStartState ass = new AvatarStartState("StartState", null);
          asm.addElement(ass);
          asm.setStartState(ass);
          AvatarTransition at = new AvatarTransition("Transition", null);
          asm.addElement(at);
          ass.addNext(at);
          AvatarState state = new AvatarState("MainState", null);
          asm.addElement(state);
          at.addNext(state);

          // Parsing all state machines to add robustness
          AvatarStateMachine sm;
          AvatarActionOnSignal aaos;
          AvatarSignal as;
          AvatarState state0;
          int i;

          for(AvatarRelation ar: relations) {
          if (ar.isAsynchronous() && ar.isLossy()) {
          // Modify the relation
          ar.makeRobustness();
          for(i=0; i<ar.nbOfSignals(); i = i+2) {
          as = ar.getInSignal(i);
          at = new AvatarTransition("TransitionToReceiving", null);
          asm.addElement(at);
          state.addNext(at);
          aaos = new AvatarActionOnSignal("Receiving__" + as.getName(), as, null);
          asm.addElement(aaos);
          at.addNext(aaos);
          at = new AvatarTransition("TransitionToIntermediateState", null);
          asm.addElement(at);
          state0 = new AvatarState("Choice__" + as.getName(), null);
          asm.addElement(state0);
          aaos.addNext(at);
          at.addNext(state0);
          at = new AvatarTransition("TransitionToMainState", null);
          at.addAction("messageLost()");
          asm.addElement(at);
          state0.addNext(at);
          at.addNext(state);

          as = ar.getOutSignal(i+1);
          at = new AvatarTransition("TransitionToSending", null);
          asm.addElement(at);
          aaos = new AvatarActionOnSignal("Sending__" + as.getName(), as, null);
          asm.addElement(aaos);
          state0.addNext(at);
          at.addNext(aaos);
          at = new AvatarTransition("TransitionAfterSending", null);
          asm.addElement(at);
          aaos.addNext(at);
          at.addNext(state);
          }

          }
          }
          }*/
    }

    public AvatarSpecification advancedClone() {
	AvatarSpecification spec = new AvatarSpecification(this.getName(), this.getReferenceObject());
	HashMap<AvatarBlock, AvatarBlock> correspondenceBlocks = new HashMap<AvatarBlock, AvatarBlock>();

	// Cloning block definition
	for(AvatarBlock block: blocks) {
	    AvatarBlock nB = block.advancedClone(spec);
	    correspondenceBlocks.put(block, nB);
	    spec.addBlock(nB);
	}

	// Handling the clone of fathers
	for(AvatarBlock block: blocks) {
	    AvatarBlock father = block.getFather();
	    if (father != null) {
		AvatarBlock nb = spec.getBlockWithName(block.getName());
		if (nb != null) {
		    AvatarBlock nf = spec.getBlockWithName(father.getName());
		    if (nf != null) {
			TraceManager.addDev("Setting "+ nf.getName() + " as the father of " + nb.getName());
			nb.setFather(nf);
		    }
		}
	    }
	}

	// Cloning asm
	for(AvatarBlock block: blocks) {
	    AvatarBlock nb = spec.getBlockWithName(block.getName());
	    block.getStateMachine().advancedClone(nb.getStateMachine(), nb);
	}

	// Relations
	for(AvatarRelation relation: relations) {
	    AvatarRelation nR = relation.advancedClone(correspondenceBlocks);
	    if (nR != null) {
		spec.addRelation(nR);
	    }
	}

	/*for(AvatarPragma pragma: pragmas) {
	    AvatarPragma nP = pragma.advancedClone();
	    spec.addPragma(nP);
	    }*/

	for(String safetyPragma: safety_pragmas) {
	    spec.addSafetyPragma(safetyPragma);
	}

	for(AvatarConstant constant: constants) {
	    AvatarConstant cN = constant.advancedClone();
	    spec.addConstant(cN);
	}

	spec.setInformationSource(getInformationSource());
	spec.addApplicationCode(getApplicationCode());

	return spec;
    }

    public AvatarAttribute getMatchingAttribute (AvatarAttribute aa) {
        for (AvatarBlock block: this.blocks) {
            if (block.getName().compareTo(aa.getBlock().getName()) == 0) {
                return block.getAvatarAttributeWithName (aa.getName ());
            }
        }

        return null;
    }
}
