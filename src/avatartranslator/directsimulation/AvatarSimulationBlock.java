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
 * Class AvatarSimulationBlock
 * Avatar: notion of block in simulation
 * Creation: 14/12/2010
 * @version 1.0 14/12/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator.directsimulation;

import java.util.*;

import avatartranslator.*;
import myutil.*;

public class AvatarSimulationBlock  {
	public final static int NOT_STARTED = 0;
	public final static int STARTED = 1;
	public final static int COMPLETED = 2;
	
    private AvatarBlock block;
	private AvatarSimulationTransaction lastTransaction;
	private Vector <AvatarSimulationTransaction> transactions;
	private boolean completed;
	
	//private int elapsedTime; 
	
    public AvatarSimulationBlock(AvatarBlock _block) {
		block = _block;
		transactions = new Vector<AvatarSimulationTransaction>();
		completed = false;
		//elapsedTime = 0;
    }
	
	/*public void addElapsedTime(int _elapsedTimed) {
		elapsedTime += _elapsedTimed;
	}
	
	public void resetElapsedTime() {
		elapsedTime = 0;
	}*/
	
	public AvatarBlock getBlock() {
		return block;
	}
	
	public AvatarSimulationTransaction getLastTransaction() {
		return lastTransaction;
	}
	
	public String getName() {
		if (block != null) {
			return block.getName();
		}
		return "noname";
	}
	
	public int getID() {
		if (block != null) {
			return block.getID();
		}
		return -1;
	}
	
	public int getStatus() {
		if (completed) {
			return COMPLETED;
		}
		
		if (lastTransaction == null) {
			return NOT_STARTED;
		}
		
		return STARTED;
	}
	
	public String getAttributeValue(int _index) {
		if (lastTransaction == null) {
			return block.getAttribute(_index).getInitialValue();
		}
		
		return lastTransaction.attributeValues.get(_index);
	}
	
	public Vector<AvatarSimulationTransaction> getTransactions() {
		return transactions;
	}
	
	public Vector<AvatarSimulationPendingTransaction> getPendingTransactions(Vector<AvatarSimulationTransaction> _allTransactions, long _clockValue, int _maxTransationsInARow) {
		Vector<AvatarSimulationPendingTransaction> ll = new Vector<AvatarSimulationPendingTransaction>();
		
		if (completed) {
			return ll;
		}
		
		if (lastTransaction == null) {
			//runToNextBlockingElement(_allTransactions, _clockValue, _maxTransationsInARow);
			// First transaction
			AvatarStartState ass = block.getStateMachine().getStartState();
			if (ass == null) {
				completed = true;
				return ll;
			}
			makeExecutedTransaction(_allTransactions, ass, _clockValue, null);
			
		}
		
		if ((lastTransaction == null) || completed) {
			return ll;
		}
		
		// Create pseudo-transactions (all pending transactions)
		// ...
		// ...
		// To be done!
		AvatarSimulationPendingTransaction aspt;
		AvatarStateMachineElement asme;
		boolean guardOk;
		for(int i=0; i<lastTransaction.executedElement.nbOfNexts(); i++) {
			asme = lastTransaction.executedElement.getNext(i);
			guardOk = true;
			// Guard on transition ? -> must evaluate the guard!
			if (asme instanceof AvatarTransition) {
				AvatarTransition at = (AvatarTransition)(asme);
				if (at.isGuarded()) {
					// Must evaluate the guard
					String guard = at.getGuard();
					String s = Conversion.replaceAllString(guard, "[", "").trim();
					s = Conversion.replaceAllString(s, "]", "").trim();
					guardOk = evaluateBoolExpression(s, lastTransaction.attributeValues);
					TraceManager.addDev("guard ok=" + guardOk);
				}
			}
			
			if(guardOk) {
				aspt = new AvatarSimulationPendingTransaction();
				aspt.asb = this;
				aspt.elementToExecute = lastTransaction.executedElement.getNext(i);
				if ((aspt.elementToExecute instanceof AvatarTransition) && (lastTransaction.executedElement instanceof AvatarState)) {
					AvatarTransition trans = (AvatarTransition)(aspt.elementToExecute);
					if (trans.getNbOfAction() == 0){
						// empty transition, "empty" is the meaning of actions -> look for an action after
						if(trans.getNext(0) != null) {
							if (trans.getNext(0) instanceof AvatarActionOnSignal) {
								aspt.involvedElement = trans;
								aspt.elementToExecute = trans.getNext(0);
							}
						}
					}
				}
				
				if (aspt.elementToExecute instanceof AvatarTransition) { 
					AvatarTransition trans = (AvatarTransition)(aspt.elementToExecute);
					if (trans.hasDelay()) {
						aspt.myMinDelay = evaluateIntExpression(trans.getMinDelay(), lastTransaction.attributeValues);
						aspt.myMaxDelay = evaluateIntExpression(trans.getMaxDelay(), lastTransaction.attributeValues);
						aspt.hasDelay = true;
						if (lastTransaction != null) {
							if (lastTransaction.clockValueWhenPerformed < _clockValue) {
								aspt.hasElapsedTime = true;
								aspt.elapsedTime = (int)(_clockValue - lastTransaction.clockValueWhenPerformed);
							}
						}
					}
				} else if (aspt.involvedElement instanceof AvatarTransition) {
					AvatarTransition trans = (AvatarTransition)(aspt.involvedElement);
					if (trans.hasDelay()) {
						aspt.myMinDelay = evaluateIntExpression(trans.getMinDelay(), lastTransaction.attributeValues);
						aspt.myMaxDelay = evaluateIntExpression(trans.getMaxDelay(), lastTransaction.attributeValues);
						aspt.hasDelay = true;
						
						TraceManager.addDev(">>>>>   Signal with delay before");
						
						if (lastTransaction != null) {
							if (lastTransaction.clockValueWhenPerformed < _clockValue) {
								aspt.hasElapsedTime = true;
								aspt.elapsedTime = (int)(_clockValue - lastTransaction.clockValueWhenPerformed);
							}
						}
					}
				}
				aspt.clockValue = _clockValue;
				
				if (aspt.hasDelay) {
					aspt.myMinDelay = Math.max(0, aspt.myMinDelay);
					aspt.myMaxDelay = Math.max(0, aspt.myMaxDelay);
				}
				
				if (aspt.hasElapsedTime) {
					 aspt.myMinDelay = aspt.myMinDelay -aspt.elapsedTime;
					 aspt.myMaxDelay = aspt.myMaxDelay -aspt.elapsedTime;
				}
				
				ll.add(aspt);
			}
		}
		return ll;
	}
	
	public void runSoloPendingTransaction(AvatarSimulationPendingTransaction _aspt, Vector<AvatarSimulationTransaction> _allTransactions, long _clockValue, int _maxTransationsInARow) {
		if (_aspt.involvedElement != null) {
			executeElement(_allTransactions, _aspt.involvedElement, _clockValue, _aspt);
		}
		executeElement(_allTransactions, _aspt.elementToExecute, _clockValue, _aspt);
		
	
		//runToNextBlockingElement(_allTransactions, _clockValue, _maxTransationsInARow);
	}
	
	
	public void executeElement(Vector<AvatarSimulationTransaction>_allTransactions, AvatarStateMachineElement _elt, long _clockValue, AvatarSimulationPendingTransaction _aspt) {
		// Stop state
		if (_elt instanceof AvatarStopState) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt);
			
		// Random
		} else if (_elt instanceof AvatarState) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt);
			
		// Random
		} else if (_elt instanceof AvatarRandom) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt);
			
		// Transition
		} else if (_elt instanceof AvatarTransition) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt);
		
		// Signal
		} else if (_elt instanceof AvatarActionOnSignal) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt);
		}
	}
	
	public void makeExecutedTransaction(Vector<AvatarSimulationTransaction> _allTransactions, AvatarStateMachineElement _elt, long _clockValue, AvatarSimulationPendingTransaction _aspt) {
		AvatarTransition at;
		String action;
		int i;
		Vector<String> actions;
		String value;
		AvatarAttribute avat;
		String result, name, param;
		int index;
		
		AvatarSimulationTransaction ast = new AvatarSimulationTransaction(_elt);
		ast.block = block;
		ast.asb = this;
		ast.concernedElement = null;
		ast.initialClockValue = 0;
		_aspt.clockValueAtEnd = clockValue;
		if (lastTransaction != null) {
			ast.initialClockValue = lastTransaction.clockValueWhenPerformed;
		}
		ast.clockValueWhenFinished = _clockValue;
		ast.duration = _aspt.selectedDuration;
		if (_aspt != null) {
			if (_aspt.hasClock) {
				if(_aspt.hasElaspedTime) {
					ast.duration = _aspt.elapsedTime + _aspt.selectedDuration;
					ast.duration = Math.min(_aspt.myMaxDuration, ast.duration);
				}
				ast.clockValueWhenFinished = _clockValue + ast.duration;
				_aspt.clockValueAtEnd = ast.clockValueWhenFinished;
			}
		}
		ast.id = ast.setID();
		
		// Attributes
		Vector<String> attributeValues = new Vector<String>();
		String s;
		if (lastTransaction == null) {
			for(AvatarAttribute aa: block.getAttributes()) {
				s = new String(aa.getInitialValue());
				attributeValues.add(s);
			}
		} else {
			// Recopy of previous values
			for(String ss: lastTransaction.attributeValues) {
				attributeValues.add(""+ss);
			}
			// Transition?
			if (_elt instanceof AvatarTransition) {
				at = (AvatarTransition)(_elt);
				// Must compute new values of attributes
				if (at.hasActions()) {
					actions = new Vector<String>();
					for(i=0; i<at.getNbOfAction(); i++) {
						action = at.getAction(i);
						//TraceManager.addDev("action #" + i  + " = " + action);
						makeAction(action, attributeValues, actions);
					}
					ast.actions = actions;
				}
			} 
			
			// Random?
			if (_elt instanceof AvatarRandom) {
				AvatarRandom random = (AvatarRandom)(_elt);
				index = block.getIndexOfAvatarAttributeWithName(random.getVariable());
				if (index >-1) {
					int valMin = evaluateIntExpression(random.getMinValue(), attributeValues);
					int valMax = evaluateIntExpression(random.getMaxValue(), attributeValues);
					valMin = (int)(Math.floor((Math.random()*(valMax - valMin)))) + valMin;
					attributeValues.remove(index);
					attributeValues.add(index, "" + valMin);
					ast.actions = new Vector<String>();
					ast.actions.add(random.getVariable() + " = " + valMin);
				}
			} 
			
			// Action on signal?
			if (_elt instanceof AvatarActionOnSignal) {
				AvatarActionOnSignal aaos = (AvatarActionOnSignal)_elt;
				if (_aspt != null) {
					// Must put the right parameters
					if (_aspt.isSynchronous) {
						// Synchronous call
						if ((_aspt.isSending) && (_aspt.linkedTransaction != null)){
							// Synchronous Sending!
							// Must be in the receiving transaction the right parameters
							Vector<String> parameters = new Vector<String>();
							TraceManager.addDev("Adding value in :" + aaos);
							for(i=0; i<aaos.getNbOfValues(); i++) {
								value = aaos.getValue(i);
								// Must get the type of the value
								//TraceManager.addDev("Sending aaos: " + aaos + " block=" + block.getName());
								try {
									avat = aaos.getSignal().getListOfAttributes().get(i);
									result = "";
									if (avat.getType() == AvatarType.INTEGER) {
										result += evaluateIntExpression(value, lastTransaction.attributeValues);
									} else if (avat.getType() == AvatarType.BOOLEAN) {
										result += evaluateBoolExpression(value, lastTransaction.attributeValues);
									} 
									
									TraceManager.addDev("Adding value:" + result);
									parameters.add(result);
								} catch (Exception e) {
									TraceManager.addDev("EXCEPTION on adding value " + aaos);
								}
							}
							_aspt.linkedTransaction.parameters = parameters;
						} else if ((!(_aspt.isSending))  && (_aspt.parameters != null)){
							TraceManager.addDev("Reading value " + aaos);
							// Synchronous Receiving
							String myAction = "";
							for(i=0; i<aaos.getNbOfValues(); i++) {
								TraceManager.addDev("Reading value #" + i);
								param = _aspt.parameters.get(i);
								name = aaos.getValue(i);
								index = block.getIndexOfAvatarAttributeWithName(name);
								
								if (index != -1) {
									attributeValues.remove(index);
									attributeValues.add(index, param);
									TraceManager.addDev("Reading value:" + param);
									if (myAction.length() == 0) {
										myAction += "" + param;
									} else {
										myAction += ", " + param;
									}
								}
							}
							if (myAction.length() > 0) {
								ast.actions = new Vector<String>();
								ast.actions.add(myAction);
							}
							
						}
						
					} else {
						// Asynchronous call
						if ((_aspt.isSending) && (_aspt.linkedAsynchronousMessage != null)){
							
							// Asynchronous Sending
							String myAction = "";
							for(i=0; i<aaos.getNbOfValues(); i++) {
								value = aaos.getValue(i);
								// Must get the type of the value
								avat = aaos.getSignal().getListOfAttributes().get(i);
								result = "";
								if (avat.getType() == AvatarType.INTEGER) {
									result += evaluateIntExpression(value, lastTransaction.attributeValues);
								} else if (avat.getType() == AvatarType.BOOLEAN) {
									result += evaluateBoolExpression(value, lastTransaction.attributeValues);
								} 
								TraceManager.addDev("Adding value:" + result);
								_aspt.linkedAsynchronousMessage.addParameter(result);
								_aspt.linkedAsynchronousMessage.firstTransaction = ast;
								if (myAction.length() == 0) {
									myAction += "" + result;
								} else {
									myAction += ", " + result;
								}
							}
							if (myAction.length() > 0) {
								ast.actions = new Vector<String>();
								ast.actions.add(myAction);
							}
							
						} else if ((!(_aspt.isSending)) && (_aspt.linkedAsynchronousMessage != null)) {
							// Asynchronous Receiving 
							String myAction = "";
							ast.linkedTransaction = _aspt.linkedAsynchronousMessage.firstTransaction;
							for(i=0; i<aaos.getNbOfValues(); i++) {
								param = _aspt.linkedAsynchronousMessage.getParameters().get(i);
								name = aaos.getValue(i);
								index = block.getIndexOfAvatarAttributeWithName(name);
								if (index != -1) {
									attributeValues.remove(index);
									attributeValues.add(index, param);
									TraceManager.addDev("Reading value:" + param);
									if (myAction.length() == 0) {
										myAction += "" + param;
									} else {
										myAction += ", " + param;
									}
								}
							}
							if (myAction.length() > 0) {
								ast.actions = new Vector<String>();
								ast.actions.add(myAction);
							}
						}
					}
				}
			}
		}
		ast.attributeValues = attributeValues;
		
		addExecutedTransaction(_allTransactions, ast);
	}
	
	public void makeAction(String _action, Vector<String> _attributeValues, Vector<String> _actions) {
		String nameOfVar;
		String act;
		String nameOfMethod;
		int ind;
		
		if (AvatarTransition.isAMethodCall(_action)) {
			// Evaluate all elements of the method call!
			ind = _action.indexOf("(");
			if (ind == -1) {
				return;
			}
			nameOfVar = _action.substring(0, ind).trim();
			
			act = _action.substring(ind+1, _action.length()).trim();
			
			ind = act.lastIndexOf(")");
			if(ind == -1) {
				return;
			}
			act = act.substring(0, ind);
			
			ind = nameOfVar.indexOf("=");
			if (ind != -1) {
				nameOfMethod = nameOfVar.substring(ind+1, nameOfVar.length());
			} else {
				nameOfMethod = nameOfVar;
			}
			
			String[] params = act.split(",");
			String parameters = "";
			String s;
			int indexAtt;
			int cpt = 0;
			for(int i=0; i<params.length; i++) {
				s = params[i].trim();
				if (s.length() > 0) {
					indexAtt = block.getIndexOfAvatarAttributeWithName(s);
					TraceManager.addDev("indexAtt=" + indexAtt + " s=" + s);
					if (indexAtt > -1) {
						if (cpt>0) {
							parameters += ", ";
						}
						parameters += _attributeValues.get(indexAtt);
						cpt = cpt + 1;
					}
				}
			}
			
			_actions.add(nameOfVar + "(" + parameters + ")");
			return;
		}
		
		// Regular attribute
		ind = _action.indexOf("=");
		if (ind == -1) {
			return;
		}
		
		nameOfVar= _action.substring(0, ind).trim();
		act = _action.substring(ind+1, _action.length());
		
		TraceManager.addDev("1- Working on attribute =" + nameOfVar + " action=" + _action);
		
		
		
		// Variable
		TraceManager.addDev("2- Working on attribute =" + nameOfVar);
		int indexVar = block.getIndexOfAvatarAttributeWithName(nameOfVar);
		if (indexVar != -1) {
			// int or bool???
			int type = block.getAttribute(indexVar).getType();
			if (type == AvatarType.INTEGER) {
				int result = evaluateIntExpression(act, _attributeValues);
				_actions.add(nameOfVar + " = " + result);
				_attributeValues.remove(indexVar);
				_attributeValues.add(indexVar, ""+result);
			} else if (type == AvatarType.BOOLEAN) {
				boolean bool = evaluateBoolExpression(act, _attributeValues);
				_actions.add(nameOfVar + " = " + bool);
				_attributeValues.remove(indexVar);
				_attributeValues.add(indexVar, ""+bool);
			}
		}
		
		// find the index of the attribute, and put its new value
		return;
	}
	

	
	public void addExecutedTransaction(Vector<AvatarSimulationTransaction> _allTransactions, AvatarSimulationTransaction _ast) {
		transactions.add(_ast);
		lastTransaction = _ast;
		_allTransactions.add(_ast);
	}
	
	public AvatarStateMachineElement getCurrentAvatarElement() {
		if (lastTransaction == null) {
			return block.getStateMachine().getStartState();
		}
		
		return lastTransaction.executedElement;
	}
	
	public String getAttributeName(int _index) {
		return block.getAttribute(_index).getName();
	}
	
	public int evaluateIntExpression(String _expr, Vector<String> _attributeValues) {
		String act = _expr;
		int cpt = 0;
		for(String attrValue: _attributeValues) {
			act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, getAttributeName(cpt), attrValue);
			cpt ++;
		}
		
		return (int)(new IntExpressionEvaluator().getResultOf(act));
	}
	
	public boolean evaluateBoolExpression(String _expr, Vector<String> _attributeValues) {
		String act = _expr;
		int cpt = 0;
		for(String attrValue: _attributeValues) {
			act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, getAttributeName(cpt), attrValue);
			cpt ++;
		}
		
		BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
		boolean result = bee.getResultOf(act);
		if (bee.getError() != null) {
			TraceManager.addDev("Error: " + bee.getError());
		}
		
		//TraceManager.addDev("Result of " + _expr + " = " + result);
		return result;
	}
}