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
* Class AvatarBlock
* Creation: 20/05/2010
* @version 1.0 20/05/2010
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator;

import java.util.*;

import myutil.*;


public class AvatarBlock extends AvatarElement {
	
	private AvatarBlock father;
	private LinkedList<AvatarAttribute> attributes;
	private LinkedList<AvatarMethod> methods;
	private LinkedList<AvatarSignal> signals;
	private AvatarStateMachine asm;
	
	
	
    public AvatarBlock(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
		attributes = new LinkedList<AvatarAttribute>();
		methods = new LinkedList<AvatarMethod>();
		signals = new LinkedList<AvatarSignal>();
		asm = new AvatarStateMachine("statemachineofblock__" + _name, _referenceObject);
    }
	
	public void setFather(AvatarBlock _father) {
		father = _father;
	}
	
	public AvatarBlock getFather() {
		return father;
	}
	
	public AvatarStateMachine getStateMachine() {
		return asm;
	}
	
	public void addAttribute(AvatarAttribute _attribute) {
		attributes.add(_attribute);
	}
	
	public void addMethod(AvatarMethod _method) {
		methods.add(_method);
	}
	
	public void addSignal(AvatarSignal _signal) {
		signals.add(_signal);
	}
	
	public LinkedList<AvatarAttribute> getAttributes() {
		return attributes;
	}
	
	public LinkedList<AvatarMethod> getMethods() {
		return methods;
	}
	
	public void addIntAttributeIfApplicable(String _name) {
		if (getAvatarAttributeWithName(_name) == null) {
			AvatarAttribute aa = new AvatarAttribute(_name, AvatarType.INTEGER, null);
			addAttribute(aa);
		}
	}
	
	public String toString() {
		//Thread.currentThread().dumpStack();
		StringBuffer sb = new StringBuffer("block:" + getName() + " ID=" + getID() + " \n");
		if (getFather() != null) {
			sb.append("  subblock of: " + getFather().getName() + " ID=" + getFather().getID()+ "\n"); 
		} else {
			sb.append("  top level block\n");
		}
		for(AvatarAttribute attribute: attributes) {
			sb.append("  attribute: " + attribute.toString() + " ID=" + attribute.getID() + "\n"); 
		}
		for(AvatarMethod method: methods) {
			sb.append("  method: " + method.toString() + " ID=" + method.getID() + "\n"); 
		}
		for(AvatarSignal signal: signals) {
			sb.append("  signal: " + signal.toString() + " ID=" + signal.getID() + "\n"); 
		}
		if (asm != null) {
			sb.append(asm.toString());
		} else {
			sb.append("No state machine");
		}
		
		return sb.toString();
	}
	
	public int attributeNb() {
		return attributes.size();
	}
	
	public AvatarAttribute getAttribute(int _index) {
		return attributes.get(_index);
	}
	
	public int getIndexOfAvatarAttributeWithName(String _name) {
		int cpt = 0;
		for(AvatarAttribute attribute: attributes) {
			if (attribute.getName().compareTo(_name)== 0) {
				return cpt;
			}
			cpt ++;
		}
		return -1;
	}
	
	public AvatarAttribute getAvatarAttributeWithName(String _name) {
		for(AvatarAttribute attribute: attributes) {
			if (attribute.getName().compareTo(_name)== 0) {
				return attribute;
			}
		}
		return null;
	}
	
	public AvatarMethod getAvatarMethodWithName(String _name) {
		for(AvatarMethod method: methods) {
			if (method.getName().compareTo(_name)== 0) {
				return method;
			}
		}
		
		if (getFather() != null) {
			return getFather().getAvatarMethodWithName(_name);
		}
		
		return null;
	}
	
	public AvatarAttribute getNTypeOfMethod(String methodName, int indexOfAttribute) {
		AvatarMethod am = getAvatarMethodWithName(methodName);
		if (am == null) {
			return null;
		}
		
		return am.getListOfAttributes().get(indexOfAttribute);
	}
	
	public AvatarBlock getBlockOfMethodWithName(String _name) {
		for(AvatarMethod method: methods) {
			if (method.getName().compareTo(_name)== 0) {
				return this;
			}
		}
		
		if (getFather() != null) {
			return getFather().getBlockOfMethodWithName(_name);
		}
		
		return null;
	}
	
	public AvatarSignal getAvatarSignalWithName(String _name) {
		for(AvatarSignal signal: signals) {
			if (signal.getName().compareTo(_name)== 0) {
				return signal;
			}
		}
		
		if (getFather() != null) {
			return getFather().getAvatarSignalWithName(_name);
		}
		
		return null;
	}
	
	public boolean isAValidMethodCall(String _s) {
		int i;
		
		//TraceManager.addDev("****** method=" + _s);
		String all = _s;
		
		int indexeq = _s.indexOf('=');
		
		if (indexeq != -1) {
			_s = _s.substring(indexeq + 1, _s.length()).trim();
			//TraceManager.addDev("****** cut method: " + _s);
		}
		
		int index0 = _s.indexOf("(");
		int index1 = _s.indexOf(")");
		if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
			//TraceManager.addDev("No parenthesis");
			return false;
		}
		
		String method = _s.substring(0, index0);
		
		AvatarMethod am = getAvatarMethodWithName(method);
		if (am == null) {
			//TraceManager.addDev("Method not found");
			return false;
		}
		
		String params = _s.substring(index0+1, index1).trim();
		//TraceManager.addDev("params=" + params);
		if (params.length() == 0) {
			if (am.getListOfAttributes().size() == 0) {
				return true;
			} else {
				return false;
			}
		}
		//TraceManager.addDev("params=" + params);
		String [] actions = params.split(",");
		if (am.getListOfAttributes().size() != actions.length) {
			return false;
		}
		
		AvatarAttribute aa;
		for(i=0; i<actions.length; i++) {
			//TraceManager.addDev("params=" + params +  " actions=" + actions[i]);
			// Must check tha validity of this action
			
			if (am.getListOfAttributes().get(i).isInt()) {
				if (AvatarSyntaxChecker.isAValidIntExpr(null, this, actions[i].trim()) < 0) {
					return false;
				}
			} else {
				// Assume it is a bool attribute
				if (AvatarSyntaxChecker.isAValidBoolExpr(null, this, actions[i].trim()) < 0) {
					return false;
				}
			}
			
			/*aa = getAvatarAttributeWithName(actions[i].trim());
			if (aa == null) {
				//TraceManager.addDev("Failed for attribute " + actions[i]);
				return false;
			}*/
		}
		
		// Checking for return attributes
		if (indexeq != -1) {
			//TraceManager.addDev("Checking for return params");
			String retparams = all.substring(0, indexeq).trim();
			
			// multiple params
			if (retparams.charAt(0) == '(') {
				if (retparams.charAt(retparams.length()-1) != ')') {
					//TraceManager.addDev("Bad format for return params: " + retparams);
					return false;
				}
				
				retparams = retparams.substring(1, retparams.length()-1).trim();
				actions = retparams.split(",");
				if (am.getListOfReturnAttributes().size() != actions.length) {
					return false;
				}
				
				for(i=0; i<actions.length; i++) {
					//TraceManager.addDev("params=" + retparams +  " actions=" + actions[i]);
					aa = getAvatarAttributeWithName(actions[i].trim());
					if (aa == null) {
						//TraceManager.addDev("Failed for attribute " + actions[i]);
						return false;
					}
				}
				
			} else {
				// Only one param.
				aa = getAvatarAttributeWithName(retparams);
				if (aa == null) {
					//TraceManager.addDev("Failed for return attribute " + retparams);
					return false;
				}
				
				if (am.getListOfReturnAttributes().size() != 1) {
					//TraceManager.addDev("Wrong number of return parameters in :" + retparams);
					return false;
				}
			}
			
		}
		//TraceManager.addDev("Ok for method " + _s);
		
		return true;
		
	}
	
	public AvatarStateMachineElement getStateMachineElementFromReferenceObject(Object _o) {
		return asm.getStateMachineElementFromReferenceObject(_o);
	}
	
	public boolean containsStateMachineElementWithReferenceObject(Object _o) {
		AvatarStateMachineElement asme = asm.getStateMachineElementFromReferenceObject(_o);
		return (asme != null);
	}
	
	public void removeTimers(AvatarSpecification _spec, LinkedList<AvatarBlock> _addedBlocks) {
		AvatarSignal as;
		AvatarAttribute value;
		
		// Add new timer signals
		for (AvatarAttribute aa: attributes) {
			if (aa.getType() == AvatarType.TIMER) {
				as = new AvatarSignal("set__" + aa.getName(), AvatarSignal.OUT, aa.getReferenceObject());
				value = new AvatarAttribute("__myvalue", AvatarType.INTEGER,  aa.getReferenceObject());
				as.addParameter(value);
				addSignal(as);
				addSignal(new AvatarSignal("reset__" + aa.getName(), AvatarSignal.OUT, aa.getReferenceObject()));
				addSignal(new AvatarSignal("expire__" + aa.getName(), AvatarSignal.IN, aa.getReferenceObject()));
				
				// Create a timer block, and connect signals
				AvatarBlock ab = AvatarBlockTemplate.getTimerBlock("Timer__" + aa.getName(), getReferenceObject(), null, null, null);
				_addedBlocks.add(ab);
				
				AvatarRelation ar;
				ar = new AvatarRelation("timerrelation", this, ab, getReferenceObject());
				ar.addSignals(getAvatarSignalWithName("set__" + aa.getName()), ab.getAvatarSignalWithName("set"));
				ar.addSignals(getAvatarSignalWithName("reset__" + aa.getName()), ab.getAvatarSignalWithName("reset"));
				ar.addSignals(getAvatarSignalWithName("expire__" + aa.getName()), ab.getAvatarSignalWithName("expire"));
				_spec.addRelation(ar);
			}
		}
		
		// Modify the state machine
		asm.removeTimers(this);
		
		// Remove Timer attribute
		LinkedList<AvatarAttribute> tmps = attributes;
		attributes = new LinkedList<AvatarAttribute>();
		for (AvatarAttribute aa: tmps) {
			if (aa.getType() != AvatarType.TIMER) {
				attributes.add(aa);
			}
		}
	}
	
	public AvatarAttribute addTimerAttribute(String _name) {
		// Find a suitable name;
		AvatarAttribute aa;
		int cpt;
		
		for(cpt=0; cpt<50000; cpt++) {
			aa = getAvatarAttributeWithName(_name + cpt);
			if (aa == null) {
				break;
			}
		}
		
		aa = new AvatarAttribute(_name+cpt, AvatarType.TIMER, getReferenceObject());
		addAttribute(aa);
		
		return aa;
	}
	
	public AvatarAttribute addIntegerAttribute(String _name) {
		AvatarAttribute aa;
		int cpt;
		
		for(cpt=0; cpt<50000; cpt++) {
			aa = getAvatarAttributeWithName(_name + cpt);
			if (aa == null) {
				break;
			}
		}
		
		aa = new AvatarAttribute(_name+cpt, AvatarType.INTEGER, getReferenceObject());
		addAttribute(aa);
		
		return aa;
	}
	
	public boolean hasARealBehaviour() {
		if (asm == null) {
			return false;
		}
		
		AvatarStartState ass = asm.getStartState();
		if (ass == null) {
			return false;
		}
		
		AvatarStateMachineElement asme = ass.getNext(0);
		
		if (asme == null) {
			return false;
		}
		
		if (asme instanceof AvatarTransition) {
			AvatarTransition at = (AvatarTransition)asme;
			if (at.hasDelay() || at.hasCompute() || at.hasActions()) {
				return true;
			}
			
			if (at.getNext(0) instanceof AvatarStopState) {
				return false;
			}
		}
		
		return true;
	}
	
	public int getMaxNbOfParams() {
		if (asm == null) {
			return 0;
		}
		
		int cpt = 0;
		
		for(AvatarStateMachineElement asme :asm.getListOfElements()) {
			if (asme instanceof AvatarActionOnSignal) {
				cpt = Math.max(cpt, ((AvatarActionOnSignal)asme).getNbOfValues());
			}
		}
		return cpt;
	}
	
	public int getMaxNbOfMultipleBranches() {
		if (asm == null) {
			return 0;
		}
		
		int cpt = 1;
		
		for(AvatarStateMachineElement asme :asm.getListOfElements()) {
			if (asme instanceof AvatarState) {
				cpt = Math.max(cpt, asme.nbOfNexts());
			}
		}
		return cpt;
	}
	
	
    
}