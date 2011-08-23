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

import myutil.*;

public class AvatarSpecification extends AvatarElement {
   public static String[] ops = {">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", ",", "!", "?", "{", "}"};
    
   private LinkedList<AvatarBlock> blocks;
   private LinkedList<AvatarRelation> relations;
   
   //private AvatarBroadcast broadcast;
   
   private LinkedList<String> pragmas;
   
   private boolean robustnessMade = false;
  
	
    public AvatarSpecification(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
		blocks = new LinkedList<AvatarBlock>();
		relations = new LinkedList<AvatarRelation>();
		//broadcast = new AvatarBroadcast("Broadcast", _referenceObject);
		pragmas = new LinkedList<String>();
    }
	
	public LinkedList<AvatarBlock> getListOfBlocks() {
		return blocks;
	}
	
	public LinkedList<AvatarRelation> getRelations() {
		return relations;
	}
	
	public LinkedList<String> getPragmas() {
		return pragmas;
	}
	
	public boolean isASynchronousSignal(AvatarSignal _as) {
		for(AvatarRelation ar: relations) {
			if (ar.containsSignal(_as)) {
				return !(ar.isAsynchronous());
			}
		}
		
		return false;
		
	}
	
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
	
	public void addPragma(String _pragma) {
		pragmas.add(_pragma);
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
		for(String pragma: pragmas) {
			sb.append("Pagma:" + pragma.toString() + "\n");
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
	
	public static String putAttributeValueInString(String _source, AvatarAttribute _at) {
		return Conversion.putVariableValueInString(ops, _source, _at.getName(), _at.getDefaultInitialValue());
	}
	
	public static String putRealAttributeValueInString(String _source, AvatarAttribute _at) {
		return Conversion.putVariableValueInString(ops, _source, _at.getName(), _at.getInitialValue());
	}
	
	
	public void removeCompositeStates() {
		for(AvatarBlock block: blocks) {
			block.getStateMachine().removeCompositeStates(block);
		}
	}
	
	public void removeTimers() {
		LinkedList<AvatarBlock> addedBlocks = new LinkedList<AvatarBlock>();
		for(AvatarBlock block: blocks) {
			block.removeTimers(this, addedBlocks);
		}
		
		for(int i=0; i<addedBlocks.size(); i++) {
			addBlock(addedBlocks.get(i));
		}
	}
	
	public AvatarRelation getAvatarRelationWithSignal(AvatarSignal _as) {
		for(AvatarRelation ar: relations) {
			if (ar.hasSignal(_as) > -1) {
				return ar;
			}
		}
		return null;
	}
	
	public static boolean isAVariableSettingString(String _action) {
		int index = _action.indexOf('=');
		
		if (index == -1) {
			return false;
		}
		
		String tmp = _action.substring(index+1, _action.length()).trim();
		
		index = tmp.indexOf('(');
		
		if (index == -1) {
			return true;
		}
		
		tmp = tmp.substring(0, index);
		
		if (AvatarAttribute.isAValidAttributeName(tmp)) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isABasicVariableSettingString(String _action) {
		int index = _action.indexOf('=');
		
		if (index == -1) {
			return false;
		}
		
		String name0 = _action.substring(index+1, _action.length()).trim();
		String name1 = _action.substring(0, index).trim();
		
		if (!AvatarAttribute.isAValidAttributeName(name0)) {
			return false;
		}
		
		if (!AvatarAttribute.isAValidAttributeName(name1)) {
			return false;
		}
		
		return true;
	}
	
	public static String getMethodCallFromAction(String _action) {
		int index = _action.indexOf('=');
		if (index > -1) {
			_action = _action.substring(index+1, _action.length()).trim();
		}
		
		index = _action.indexOf('(');
		if (index == -1) {
			return _action;
		}
		return _action.substring(0, index);
	}
	
	public static int getNbOfParametersInAction(String _action) {
		int index = _action.indexOf('=');
		if (index > -1) {
			_action = _action.substring(index+1, _action.length()).trim();
		}
		
		index = _action.indexOf('(');
		if (index == -1) {
			return 0;
		}
		
		String actions  = _action.substring(index+1, _action.length()).trim();
		
		index = actions.indexOf(')');
		if (index == -1) {
			return 0;
		}
		
		actions = actions.substring(0, index).trim();
		
		if (actions.length() == 0) {
			return 0;
		}
		
		int cpt = 1;
		while ((index = actions.indexOf(',')) != -1) {
			cpt ++;
			actions = actions.substring(index+1, actions.length()).trim();
		}
		
		return cpt;
	}
	
	public static int getNbOfReturnParametersInAction(String _action) {
		int index = _action.indexOf('=');
		if (index == -1) {
			return 0;
			
		}
		
		_action = _action.substring(0, index).trim();
		
		index = _action.indexOf('(');
		if (index == -1) {
			return 0;
		}
		
		String actions  = _action.substring(index+1, _action.length()).trim();
		
		index = actions.indexOf(')');
		if (index == -1) {
			return 0;
		}
		
		actions = actions.substring(0, index).trim();
		
		if (actions.length() == 0) {
			return 0;
		}
		
		int cpt = 1;
		while ((index = actions.indexOf(',')) != -1) {
			cpt ++;
			actions = actions.substring(index+1, actions.length()).trim();
		}
		
		return cpt;
	}
	
	public static String getParameterInAction(String _action, int _index) {
		int nb = getNbOfParametersInAction(_action);
		if (!(_index < nb) || (_index < 0)) {
			return null;
		}
		
		int index = _action.indexOf('=');
		if (index > -1) {
			_action = _action.substring(index+1, _action.length()).trim();
		}
		
		int index1 = _action.indexOf('(');
		int index2 = _action.indexOf(')');
		String actions = _action.substring(index1+1, index2).trim();
		String actionss[] = actions.split(",");
		return actionss[_index].trim();
		
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
	
	public AvatarState removeElseGuards() {
		AvatarState state;
		
		for(AvatarBlock block: blocks) {
			state = block.removeElseGuards();
			if (state != null) {
				return state;
			}
		}
		
		return null;
	}
	
	public static boolean isElseGuard(String _guard) {
		if (_guard == null) {
			return false;
		}
		
		String guard = Conversion.replaceAllChar(_guard, ' ', "").trim();
		
		return guard.compareTo("[else]") == 0;
	}
	
	public boolean hasLossyChannel() {
		for(AvatarRelation relation: relations) {
			if (relation.isLossy()) {
				return true;
			}
		}
		
		return false;
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
	
}