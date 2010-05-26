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
   private String[] ops = {">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", ",", "!", "?", "{", "}"};
    
   private LinkedList<AvatarBlock> blocks;
   private LinkedList<AvatarRelation> relations;
  
	
    public AvatarSpecification(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
		blocks = new LinkedList<AvatarBlock>();
		relations = new LinkedList<AvatarRelation>();
    }
	
	public LinkedList<AvatarBlock> getListOfBlocks() {
		return blocks;
	}
	
	public LinkedList<AvatarRelation> getRelations() {
		return relations;
	}
	
	public void addBlock(AvatarBlock _block) {
		blocks.add(_block);
	}
	
	public void addRelation(AvatarRelation _relation) {
		relations.add(_relation);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("Blocks:\n");
		for(AvatarBlock block: blocks) {
			sb.append("*** " + block.toString()+"\n");
		}
		sb.append("\nRelations:\n");
		for(AvatarRelation relation: relations) {
			sb.append("Relation:" + relation.toString() + "\n");
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
	
	public String putAttributeValueInString(String _source, AvatarAttribute _at) {
		return Conversion.putVariableValueInString(ops, _source, _at.getName(), _at.getDefaultInitialValue());
	}
	
	
	public void removeCompositeStates() {
		for(AvatarBlock block: blocks) {
			block.getStateMachine().removeCompositeStates();
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
		return (index > -1);
	}
	
	public static String getMethodCallFromAction(String _action) {
		int index = _action.indexOf('(');
		if (index == -1) {
			return _action;
		}
		return _action.substring(0, index);
	}
	
	public static int getNbOfParametersInAction(String _action) {
		int index = _action.indexOf('(');
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
		
		int index1 = _action.indexOf('(');
		int index2 = _action.indexOf(')');
		String actions = _action.substring(index1+1, index2).trim();
		String actionss[] = actions.split(",");
		return actionss[_index].trim();
		
	}
}