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
  
    private AvatarBlock block;
	private AvatarSimulationTransaction lastTransaction;
	private LinkedList<AvatarSimulationTransaction> transactions;
	private boolean completed;
	
    public AvatarSimulationBlock(AvatarBlock _block) {
		block = _block;
		transactions = new LinkedList<AvatarSimulationTransaction>();
		completed = false;
    }
	
	public String getName() {
		if (block != null) {
			return block.getName();
		}
		return "noname";
	}
	
	public LinkedList<AvatarSimulationPendingTransaction> getPendingTransactions(LinkedList<AvatarSimulationTransaction> _allTransactions, long _clockValue, int _maxTransationsInARow) {
		LinkedList<AvatarSimulationPendingTransaction> ll = new LinkedList<AvatarSimulationPendingTransaction>();
		
		if (completed) {
			return ll;
		}
		
		if (lastTransaction == null) {
			runToNextBlockingElement(_allTransactions, _clockValue, _maxTransationsInARow);
		}
		
		if ((lastTransaction == null) || completed) {
			return ll;
		}
		
		// Create pseudo-transactions (all pending transactions)
		// ...
		// ...
		// To be done!
		AvatarSimulationPendingTransaction aspt;
		for(int i=0; i<lastTransaction.executedElement.nbOfNexts(); i++) {
			aspt = new AvatarSimulationPendingTransaction();
			aspt.asb = this;
			aspt.elementToExecute = lastTransaction.executedElement.getNext(i);
			if ((aspt.elementToExecute instanceof AvatarTransition) && (lastTransaction.executedElement instanceof AvatarState)) {
				AvatarTransition trans = (AvatarTransition)(aspt.elementToExecute);
				if (!trans.hasDelay() && (trans.getNbOfAction() == 0)){
					// empty transition, "empty" is the meaning of actions -> look for an action after
					if(trans.getNext(0) != null) {
						if (trans.getNext(0) instanceof AvatarActionOnSignal) {
							aspt.involvedElement = trans;
							aspt.elementToExecute = trans.getNext(0);
						}
					}
				}
			}
			aspt.clockValue = _clockValue;
			ll.add(aspt);
		}
		return ll;
	}
	
	public void runToNextBlockingElement(LinkedList<AvatarSimulationTransaction> _allTransactions, long _clockValue, int _maxTransationsInARow) {
		
		// No previous transaction
		if (lastTransaction == null) {
			AvatarStartState ass = block.getStateMachine().getStartState();
			if (ass == null) {
				completed = true;
				return;
			}
			makeExecutedTransaction(_allTransactions, ass, _clockValue);
		}
		
		boolean go = true;
		int nbOfTransactions = 0;
		AvatarStateMachineElement elt;
		while(go) {
			elt = lastTransaction.executedElement;
			TraceManager.addDev("" + nbOfTransactions + "-> " + elt);
			
			// Last element?
			if (elt.nbOfNexts() == 0) {
				completed = true;
				go = false;
			}
			
			// Only one next?
			if (elt.nbOfNexts() == 1) {
				if (isBlocking(elt.getNext(0))) {
					TraceManager.addDev("is blocking!");
					go = false;
				} else {
					TraceManager.addDev("not blocking -> going on!");
					executeElement(_allTransactions, elt.getNext(0), _clockValue);
				}
			}
			
			if (elt.nbOfNexts() > 1) {
				go = false;
			}
			
			nbOfTransactions ++;
			if (nbOfTransactions == _maxTransationsInARow) {
				go = false;
			}
		}
		
		if (nbOfTransactions == _maxTransationsInARow) {
			go = false;
			TraceManager.addDev("Too many transactions in a row: aborting block");
			completed = true;
		}
	}
	
	
	public boolean isBlocking(AvatarStateMachineElement _elt) {
		TraceManager.addDev("Testing whether " + _elt + "is blocking or not");
		
		if (_elt instanceof AvatarStopState) {
			return false;
		}
		
		if (_elt instanceof AvatarState) {
			return false;
		}
		
		if (_elt instanceof AvatarRandom) {
			return false;
		}
		
		if (_elt instanceof AvatarTransition) {
			AvatarTransition at = (AvatarTransition)_elt;
			
			if ((at.hasDelay()) || (at.hasCompute())) {
				return true;
			}
			
			return false;
		}
		
		return true;
	}
	
	public void executeElement(LinkedList<AvatarSimulationTransaction>_allTransactions, AvatarStateMachineElement _elt, long _clockValue) {
		// Stop state
		if (_elt instanceof AvatarStopState) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue);
			
		// Random
		} else if (_elt instanceof AvatarState) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue);
			
		// Random
		} else if (_elt instanceof AvatarRandom) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue);
			
		// Transition
		} else if (_elt instanceof AvatarTransition) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue);
		
		// Signal
		} else if (_elt instanceof AvatarActionOnSignal) {
			makeExecutedTransaction(_allTransactions, _elt, _clockValue);
		}
	}
	
	public void makeExecutedTransaction(LinkedList<AvatarSimulationTransaction> _allTransactions, AvatarStateMachineElement _elt, long _clockValue) {
			AvatarSimulationTransaction ast = new AvatarSimulationTransaction();
			ast.block = block;
			ast.asb = this;
			ast.executedElement = _elt;
			ast.concernedElement = null;
			ast.initialClockValue = _clockValue;
			ast.clockValueWhenPerformed = _clockValue;
			ast.id = ast.setID();
			
			// Attributes
			LinkedList<String> attributeValues = new LinkedList<String>();
			String s;
			if (lastTransaction == null) {
				for(AvatarAttribute aa: block.getAttributes()) {
					s = new String(aa.getInitialValue());
					attributeValues.add(s);
				}
			} else {
				for(String ss: lastTransaction.attributeValues) {
					attributeValues.add(""+ss);
				}
			}
			ast.attributeValues = attributeValues;
			
			addExecutedTransaction(_allTransactions, ast);
	}
	
	public void addExecutedTransaction(LinkedList<AvatarSimulationTransaction> _allTransactions, AvatarSimulationTransaction _ast) {
		transactions.add(_ast);
		lastTransaction = _ast;
		_allTransactions.add(_ast);
	}
}