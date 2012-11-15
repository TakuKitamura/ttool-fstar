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
 * Creation: 13/12/2010
 * @version 1.0 13/12/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator.directsimulation;

import java.awt.*;
import java.util.*;

import avatartranslator.*;
import myutil.*;


public class AvatarSpecificationSimulation  {
	public static int MAX_TRANSACTION_IN_A_ROW = 1000; 
	
	public static int MAX_TRANSACTIONS = 100000; 
	
	public final static int INITIALIZE = 0;
	public final static int RESET = 1;
	public final static int GATHER = 2;
	public final static int EXECUTE = 3;
	public final static int DONT_EXECUTE = 4;
	public final static int TERMINATED = 5;
	public final static int KILLED = 6;
	
	private int state;
	
    private AvatarSpecification avspec;
	private AvatarSimulationInteraction asi;
	private long clockValue;
	private Vector<AvatarSimulationBlock> blocks;
	private Vector<AvatarSimulationBlock> selectedBlocks;
	private AvatarSimulationBlock previousBlock;
	private Vector<AvatarSimulationAsynchronousTransaction> asynchronousMessages;
	private Vector<AvatarSimulationPendingTransaction> pendingTransactions;
	//private Vector<AvatarSimulationPendingTransaction> pendingTimedTransactions;
	private Vector<AvatarSimulationTransaction> allTransactions;
	
	private boolean go = false;
	private boolean stopped = false;
	private boolean killed = false;
	private boolean reset = false;
	private boolean newState = false;
	
	private long bunchid;
	
	private boolean nbOfCommandsActivated = false;
	private int nbOfCommands = -1; // means: until it blocks
	private int indexSelectedTransaction = -1;
	
	private boolean executeEmptyTransition;
	private boolean executeStateEntering;
	private boolean silentTransactionExecuted;
	
	private IntExpressionEvaluator iee;
	
    public AvatarSpecificationSimulation(AvatarSpecification _avspec, AvatarSimulationInteraction _asi) {
        avspec = _avspec;
		asi = _asi;
		iee = new IntExpressionEvaluator();
		executeEmptyTransition = true;
		executeStateEntering = true;
		
		initialize();
		reset();
		setState(GATHER);
    }
	
	public AvatarSpecification getAvatarSpecification() {
		return avspec;
	}
	
	public Vector<AvatarSimulationBlock> getSimulationBlocks() {
		return blocks;
	}
	
	public void computeSelectedSimulationBlocks() {
		selectedBlocks = new Vector<AvatarSimulationBlock>();
		for (AvatarSimulationBlock block: blocks) {
			if (block.selected) {
				selectedBlocks.add(block);
			}
		}
		TraceManager.addDev("computeSelectedSimulationBlocks: Nb of selected blocks: " + selectedBlocks.size() + "\n");
		//for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
		//	System.out.println(ste);
		//}
	}
	
	public Vector<AvatarSimulationBlock> getSelectedSimulationBlocks() {
		
		return selectedBlocks;
	}
	
	public Vector<AvatarSimulationTransaction> getAllTransactions() {
		return allTransactions;
	}
	
	public long getClockValue() {
		return clockValue;
	}
	
	public void initialize() {
		
		// Remove composite states
		avspec.removeCompositeStates();
		
		// Remove timers
		avspec.removeTimers();
		
		// Robustness
		avspec.makeRobustness();
		
		TraceManager.addDev("Spec:" + avspec.toString());
	}
	
	public void reset() {
		
		// Reinit clock
		clockValue = 0;
		
		// Reinit bunch id
		bunchid = 0;
		
		// Stop the first transaction
		unsetNbOfCommands();
		stopped = true;
		
		// Reinit simulation 
		AvatarSimulationTransaction.reinit();
		
		// Create all simulation blocks
		int cpt = 0;
		Vector<AvatarSimulationBlock> tmpblocks = blocks;
		blocks = new Vector<AvatarSimulationBlock>();
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			if (block.hasARealBehaviour()) {
				AvatarSimulationBlock asb = new AvatarSimulationBlock(block);
				blocks.add(asb);
				if (tmpblocks != null) {
					asb.selected = tmpblocks.get(cpt).selected; 
				}
				cpt ++;
			}
		}
		TraceManager.addDev("Nb of simulated blocks :" + blocks.size() + "\n");
		
		
		// Computing selected blocks
		TraceManager.addDev("Computing simulation blocks\n");
		computeSelectedSimulationBlocks();
		
		TraceManager.addDev("Nb of selected blocks: " + selectedBlocks.size() + "\n");
		
		// Create all simulation asynchronous channels
		asynchronousMessages = new Vector<AvatarSimulationAsynchronousTransaction>();
		
		// Create the structure for pending and executed transactions
		pendingTransactions = new Vector<AvatarSimulationPendingTransaction>();
		allTransactions = new Vector<AvatarSimulationTransaction>();
		
	}
	
	public boolean isInDeadlock() {
		return true;
	}
	
	/*public void runSimulation() {
		setMode(RUNNING);
		int index[];
		Vector<AvatarSimulationPendingTransaction> selectedTransactions;
		
		boolean go = true;
		stopped = true;
		
		TraceManager.addDev("Simulation started at time: " + clockValue);
		
		while(!killed) {
			while((go == true) && !killed) {
				while((go == true) && !killed) {
					gatherPendingTransactions();
					
					if (pendingTransactions.size() == 0) {
						go = false;
						TraceManager.addDev("No more pending transactions");
					} else {
						
						if (stopped && go) {
							setMode(STOPPED);
							TraceManager.addDev("Simulation waiting for run");
							waitForUnstopped();
							if (go) {
								setMode(RUNNING);
							}
						} else if (nbOfCommands == 0) {
							if (getSilentTransactionToExecute(pendingTransactions) == null) {
								stopSimulation();
								stopSimulation(go);
							}
						}
						
						if (!killed) {
							silentTransactionExecuted = false;
							selectedTransactions = selectTransactions(pendingTransactions);
							
							if (selectedTransactions.size() == 0) {
								go = false;
								TraceManager.addDev("Deadlock: no transaction can be selected");
							} else {
								//TraceManager.addDev("* * * * * Nb of selected transactions: " + selectedTransactions.size());
								go = performSelectedTransactions(selectedTransactions);
								//TraceManager.addDev("NbOfcommands=" + nbOfCommands);
								if (!silentTransactionExecuted) {
									nbOfCommands --;
								}
								if (asi != null) {
									asi.updateTransactionAndTime(allTransactions.size(), clockValue);
								}g
								//TraceManager.addDev("------------- new NbOfcommands=" + nbOfCommands);
							}
						}
					}
				}
			}
			setMode(TERMINATED);
			TraceManager.addDev("Simulation finished at time: " + clockValue + "\n--------------------------------------");
			waitForKillResetOrBackward();
			go = true;
		}
		
		setMode(KILLED);
		//printExecutedTransactions();
	}*/
	
	// Control function
	
	public void runSimulation() {
		int index[];
		Vector<AvatarSimulationPendingTransaction> selectedTransactions;
		
		TraceManager.addDev("Simulation started at time: " + clockValue);
		
		//boolean executeNextState;
		
		while(true) {
			//TraceManager.addDev("State=" + state);
			switch(state) {
			case INITIALIZE:
				initialize();
				setState(RESET);
				break;
				
			case RESET:
				reset();
				setState(GATHER);
				break;
				
			case GATHER:
				gatherPendingTransactions();
				if (pendingTransactions.size() == 0) {
					setState(TERMINATED);
					TraceManager.addDev("No more pending transactions");
				} else {
					if ((nbOfCommandsActivated) && (nbOfCommands < 1)) {
						if (getSilentTransactionToExecute(pendingTransactions) == null) {
							setState(DONT_EXECUTE);
						} else {
							setState(EXECUTE);
						}
					} else {
						setState(EXECUTE);
					}
				}
				
				break;
				
			case EXECUTE:
				silentTransactionExecuted = false;
				selectedTransactions = selectTransactions(pendingTransactions);
				
				if (selectedTransactions.size() == 0) {
					setState(TERMINATED);
					TraceManager.addDev("Deadlock: no transaction can be selected");
				} else {
					if (performSelectedTransactions(selectedTransactions)) {
						if (!silentTransactionExecuted) {
							if (nbOfCommandsActivated) {
								nbOfCommands --;
							}
						}
						if (asi != null) {
							asi.updateTransactionAndTime(allTransactions.size(), clockValue);
						}
						setState(GATHER);
					} else {
						setState(TERMINATED);
						TraceManager.addDev("Error when executing transaction");
					}
				}
				break;
				
			case DONT_EXECUTE:
				waitForExecute();
				break;
				
			case TERMINATED:
				waitForResetOrNewState();
				break;
				
			case KILLED:
				TraceManager.addDev("Simulation killed");
				return;
				
			default:
				TraceManager.addDev("Unknown state");
				setState(KILLED);
			}
			
			computeExternalCommands();
		}
		
		
	}
	
	public synchronized void waitForExecute() {
		while(!reset && !newState && !killed && !go) {
			try {
				wait();
			} catch(Exception e){}
		}
	}
	
	public synchronized void waitForResetOrNewState() {
		while(!reset && !newState && !killed) {
			try {
				wait();
			} catch(Exception e){}
		}
	}
	
	public synchronized void  computeExternalCommands() {
		
		
		if (state == KILLED) {
			return;
		}
		
		if (killed) {
			killed = false;
			reset = false;
			newState = false;
			stopped = false;
			setState(KILLED);
			return;
		}
		
		if (state == RESET) {
			return;
		}
		
		if (reset) {
			reset = false;
			newState = false;
			stopped = false;
			setState(RESET);
			return;
		}
		
		if (newState) {
			newState = false;
			setState(GATHER);
			TraceManager.addDev("Going to gather");
			return;
		}
		
		if (stopped && (state == EXECUTE)) {
			stopped = false;
			setState(DONT_EXECUTE);
		}
		
		if (go && (state  == DONT_EXECUTE)) {
			go = false;
			setState(EXECUTE);
		}
		
		return;
	}
	
	public void setState(int _state) {
		state = _state;
		
		if (state == DONT_EXECUTE) {
			unsetNbOfCommands();
		}
		
		if (asi != null) {
			asi.setMode(state);
		}
	}
	
	public int getState() {
		return state;
	}
	
	public void setNbOfCommands(int _nbOfCommands) {
		nbOfCommands = _nbOfCommands;
		if (nbOfCommands > 0) {
			nbOfCommandsActivated = true;
		}
	}
	
	public void unsetNbOfCommands() {
		nbOfCommands = -1;
		nbOfCommandsActivated = false;
	}
	
	// External control functions
	public synchronized void killSimulation() {
		killed = true;
		notifyAll();
	}
	
	public synchronized void resetSimulation() {
		TraceManager.addDev("reset on simulation");
		reset = true;
		notifyAll();
	}
	
	public synchronized void newStateInSimulation() {
		newState = true;
		notifyAll();
	}
	
	public synchronized void stopSimulation() {
		stopped = true;
		notifyAll();
	}
	
	public synchronized void goSimulation() {
		go = true;
		notifyAll();
	}
	
	
	// Simulation functions
	
	public void gatherPendingTransactions() {
		AvatarTransition tr;
		
		pendingTransactions.clear();
		// Gather all pending transactions from blocks
		for(AvatarSimulationBlock asb: blocks) {
			pendingTransactions.addAll(asb.getPendingTransactions(allTransactions, clockValue, MAX_TRANSACTION_IN_A_ROW, bunchid));
		}
		
		/*TraceManager.addDev("# of pending transactions before selection: " + pendingTransactions.size());
		
		int ind = 0;
		for(AvatarSimulationPendingTransaction asptt :pendingTransactions) {
			TraceManager.addDev("#" + ind + ": " + asptt);
			ind ++;
		}*/
		
		Vector<AvatarSimulationPendingTransaction> ll = new Vector<AvatarSimulationPendingTransaction>();
		
		// First compute the delay of transactions
		String res;
		AvatarBlock ab;
		int i;
		for(AvatarSimulationPendingTransaction 	aspt: pendingTransactions) {
			if (aspt.hasDelay) {
				aspt.myMinDuration = aspt.myMinDelay;
				aspt.myMaxDuration = aspt.myMaxDelay;
				
				if (aspt.myMaxDuration < 1) {
					// It has in fact no delay!
					aspt.hasDelay = false;
				} else {
					//TraceManager.addDev("min Duration = " + aspt.myMinDuration + " max duration=" + aspt.myMaxDuration);
				}
			}
		}
		
		// Work on signals
		for(AvatarSimulationPendingTransaction 	aspt: pendingTransactions) {
			if (aspt.elementToExecute instanceof AvatarActionOnSignal) {
				workOnAvatarActionOnSignalTransaction(ll, aspt, (AvatarActionOnSignal)(aspt.elementToExecute));
			} else {
				ll.add(aspt);
			}
		}
		
		// Transactions are only put with one another
		// Synchronous transactions: the sending one has a link to the receiving one
		// Work on broadcast transactions
		ll = workOnBroadcastTransactions(ll);
		
		
		// Select possible logical transactions
		pendingTransactions = ll;
		ll = new Vector<AvatarSimulationPendingTransaction>();
		
		int nbOfPureLogicalTransitions = 0;
		for(AvatarSimulationPendingTransaction 	aspt: pendingTransactions) {
			if (!(aspt.hasDelay)) {
				nbOfPureLogicalTransitions ++;
			}
		}
		
		boolean hasSilentTransaction = false;
		if (nbOfPureLogicalTransitions >0) {
			//TraceManager.addDev("Pure logical transaction");
			for(AvatarSimulationPendingTransaction 	aspt: pendingTransactions) {
				if (!aspt.hasDelay) {
					if (isASilentTransaction(aspt, pendingTransactions)) {
						hasSilentTransaction = true;
					}
					ll.add(aspt);
				}
			}
			pendingTransactions = ll;
			//TraceManager.addDev("At least on logical transition");
			
			if (hasSilentTransaction) {
				// Must keep only silent transactions
				//TraceManager.addDev("has silent logical transaction");
				ll = new Vector<AvatarSimulationPendingTransaction>();
				for(AvatarSimulationPendingTransaction aspt: pendingTransactions) {
					if (isASilentTransaction(aspt, pendingTransactions)) {
						ll.add(aspt);
					}
				}
				pendingTransactions = ll;
			}
			
			return;
		}
		
		//TraceManager.addDev("No logical transition -> temporal transitions?");
		// Resolving time constraints
		int indexMin = -1;
		int minMin = Integer.MAX_VALUE;
		int maxDuration = Integer.MAX_VALUE;
		int min, max;
			
		for(AvatarSimulationPendingTransaction 	aspt: pendingTransactions) {
			if (aspt.hasDelay) {
				if (aspt.myMinDuration < minMin) {
					minMin = aspt.myMinDuration;
					//TraceManager.addDev("Setting min duration = " +  minMin);
				}
				if (aspt.myMaxDuration < maxDuration) {
					maxDuration = aspt.myMaxDuration;
					indexMin = ll.size();
					//TraceManager.addDev("Setting max Duration = " +  maxDuration);
				}
			}
			ll.add(aspt);
		}
		
		pendingTransactions = ll;
		ll = new Vector<AvatarSimulationPendingTransaction>();
		
		
		// Temporal transitions
		if ((pendingTransactions.size() > 0) && (indexMin > -1)) {
			//TraceManager.addDev("At least one temporal trans");
			// Must compute the min index, and the max duration
			// We put in ll all transactions that are between the min and the max of the selected index
			AvatarSimulationPendingTransaction aspt_tmp = pendingTransactions.get(indexMin);
			for(AvatarSimulationPendingTransaction 	aspt1: pendingTransactions) {
				//TraceManager.addDev("aspt1 min=" + aspt1.myMinDuration + " max autre=" + aspt_tmp.myMaxDuration);
				if (aspt1.myMinDuration <= aspt_tmp.myMaxDuration) {
					ll.add(aspt1);
					aspt1.maxDuration = maxDuration;
					aspt1.hasClock = true;
					if (aspt1.linkedTransaction != null) {
						aspt1.linkedTransaction.hasDelay = true;
						aspt1.linkedTransaction.hasClock = true;
						aspt1.linkedTransaction.maxDuration = aspt1.maxDuration;
						aspt1.linkedTransaction.myMinDuration = aspt1.myMinDuration;
						aspt1.linkedTransaction.myMaxDuration = aspt1.myMaxDuration;
					}
				}
			}
		} 
		
		pendingTransactions = ll;
		
	}
	
	public boolean isASilentTransaction(AvatarSimulationPendingTransaction aspt, Vector<AvatarSimulationPendingTransaction> _pendingTransactions) {
		if (aspt.elementToExecute instanceof AvatarTransition) {
			AvatarTransition atr = (AvatarTransition)(aspt.elementToExecute);
			if (!(atr.hasDelay()) && !(atr.hasCompute()) && !(atr.hasActions())){
                if ((aspt.previouslyExecutedElement != null) && (aspt.previouslyExecutedElement.nbOfNexts() < 2)) {
					if (nbOfTransactions(aspt.asb, _pendingTransactions) < 2) {
						return true;
					}
                }
			}
			// State entering?
		} else if (((aspt.elementToExecute instanceof AvatarState) ||  (aspt.elementToExecute instanceof AvatarStopState))) {
			if (nbOfTransactions(aspt.asb, _pendingTransactions) < 2) {
				return true;
			}
		}
		
		return false;
	}
	
	public void workOnAvatarActionOnSignalTransaction(Vector<AvatarSimulationPendingTransaction> transactions, AvatarSimulationPendingTransaction _aspt, AvatarActionOnSignal _aaos) {
		AvatarSignal as = _aaos.getSignal();
		if (as.isIn()) {
			AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
			// If synchronous, not taken into account -> taken into account at sending side
			if (ar.isAsynchronous()) {
				// Must check whether there is at least one element to read in the channel
				AvatarSimulationAsynchronousTransaction asat = getAsynchronousMessage(ar, as);
				if (asat != null) {
					_aspt.linkedAsynchronousMessage = asat;
					transactions.add(_aspt);
					/*if (asat.firstTransaction == null) {
						TraceManager.addDev("NULL FIRST");
					}*/
				}
			} 
		} else {
			AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
			int index0 = ar.getIndexOfSignal(as);
			if (ar.isAsynchronous()) {
				// Mus see whether the channel is full or not
				if (ar.isBlocking()) {
					// Must see whether the channel is full or not
					int nb = getNbOfAsynchronousMessages(ar);
					if (nb < ar.getSizeOfFIFO()) {
						transactions.add(_aspt);
					}
				} else {
					// The transaction can be performed
					transactions.add(_aspt);
				}
			} else {
				if (ar.isBroadcast()) {
					// Broadcast -> The sender can execute at once
					// Each time one is found, a new pending transaction is added, linked with the receiving action
					//TraceManager.addDev("Found a synchronous signal");
					transactions.add(_aspt);
					_aspt.isBroadcast = true;
					for(AvatarSimulationPendingTransaction 	otherTransaction: pendingTransactions) {
						if ((otherTransaction != _aspt) && (otherTransaction.elementToExecute instanceof AvatarActionOnSignal)){
							AvatarSignal sig = ((AvatarActionOnSignal)(otherTransaction.elementToExecute)).getSignal();
							AvatarRelation rel = avspec.getAvatarRelationWithSignal(sig);
							if (rel == ar) {
								int index1 = rel.getIndexOfSignal(sig);
								if (index1 == index0) {
									//TraceManager.addDev("step 3");
									if (sig.isIn()) {
										if (!(otherTransaction.hasDelay)) {
											if (_aspt.linkedTransactions == null) {
												_aspt.linkedTransactions = new Vector<AvatarSimulationPendingTransaction>();
											}
											_aspt.linkedTransactions.add(otherTransaction);
											otherTransaction.isBroadcast = true;
										}
									}
								}
							}
						}
					}
					
				} else {
					// Synchronous -> must find a corresponding synchronous one
					// Each time one is found, a new pending transaction is added, linked with the receiving action
					//TraceManager.addDev("Found a synchronous signal");
					for(AvatarSimulationPendingTransaction 	otherTransaction: pendingTransactions) {
						if (otherTransaction != _aspt) {
							if (otherTransaction.elementToExecute instanceof AvatarActionOnSignal) {
								//TraceManager.addDev("step 2");
								AvatarSignal sig = ((AvatarActionOnSignal)(otherTransaction.elementToExecute)).getSignal();
								AvatarRelation rel = avspec.getAvatarRelationWithSignal(sig);
								if (rel == ar) {
									int index1 = rel.getIndexOfSignal(sig);
									if (index1 == index0) {
										//TraceManager.addDev("step 3");
										if (sig.isIn()) {
											// Found one!
											//TraceManager.addDev("step 4 sig=" + sig + " as = " + as + "rel = " + rel + "ar=" + ar);
											
											AvatarSimulationPendingTransaction newone = _aspt.cloneMe();
											/*if (ar.isBroadcast()) {
												newone.isBroadcast = true;
											}*/
											newone.linkedTransaction = otherTransaction;
											transactions.add(newone);
											if (_aspt.hasDelay) {
												if (otherTransaction.hasDelay) {
													newone.myMinDuration = Math.max(otherTransaction.myMinDuration, _aspt.myMinDuration);
													newone.myMaxDuration = Math.max(otherTransaction.myMaxDuration, _aspt.myMaxDuration);
													newone.hasDelay = true;
													newone.durationOnOther = true;
													newone.durationOnCurrent = true;
												} else {
													newone.durationOnOther = false;
													newone.durationOnCurrent = true;
												}
											} else {
												if (otherTransaction.hasDelay) {
													newone.hasDelay = true;
													newone.myMinDuration = otherTransaction.myMinDuration;
													newone.myMaxDuration = otherTransaction.myMaxDuration;
													TraceManager.addDev("Other transaction hasDelay MyMax = " + otherTransaction.myMaxDuration);
													newone.durationOnOther = true;
													newone.durationOnCurrent = false;
												} else {
													newone.durationOnOther = false;
													newone.durationOnCurrent = false;
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
		}
	}
	
	public AvatarSimulationAsynchronousTransaction getAsynchronousMessage(AvatarRelation _ar, AvatarSignal _as) {
		for(AvatarSimulationAsynchronousTransaction asat: asynchronousMessages) {
			if (asat.getRelation() == _ar) {
				if (_ar.getIndexOfSignal(_as) == asat.getIndex()) {
					return asat;
				}
			}
		}
		return null;
	}
	
	public int getNbOfAsynchronousMessages(AvatarRelation _ar) {
		int cpt = 0;
		for(AvatarSimulationAsynchronousTransaction asat: asynchronousMessages) {
			if (asat.getRelation() == _ar) {
				cpt ++;
			}
		}
		return cpt;
	}
	
	public AvatarSimulationPendingTransaction getSilentTransactionToExecute(Vector<AvatarSimulationPendingTransaction> _pendingTransactions) {
		if(!executeEmptyTransition && !executeStateEntering) {
			return null;
		}
		
		if (_pendingTransactions.size() == 0) {
			return null;
		}
		
		for(AvatarSimulationPendingTransaction tr: _pendingTransactions) {
			
			// Empty transition?
			if ((tr.elementToExecute instanceof AvatarTransition) && (executeEmptyTransition)) {
				AvatarTransition atr = (AvatarTransition)(tr.elementToExecute);
				if (!(atr.hasDelay()) && !(atr.hasCompute()) && !(atr.hasActions())){
					if (nbOfTransactions(tr.asb, _pendingTransactions) < 2) {
                          TraceManager.addDev("Setting to silent");
						tr.isSilent = true;
						return tr;
					}
				}
			// State entering?
			} else if (((tr.elementToExecute instanceof AvatarState) ||  (tr.elementToExecute instanceof AvatarStopState)) && (executeStateEntering)) {
				if (nbOfTransactions(tr.asb, _pendingTransactions) < 2) {
                     TraceManager.addDev("Setting to silent");
					tr.isSilent = true;
					return tr;
				}
			}
		}
		
		return null;
	}
	
	public AvatarSimulationPendingTransaction getRandomSilentTransactionToExecute(Vector<AvatarSimulationPendingTransaction> _pendingTransactions) {
		if(!executeEmptyTransition && !executeStateEntering) {
			return null;
		}
		
		if (_pendingTransactions.size() == 0) {
			return null;
		}
		
		int index = (int)(Math.floor(Math.random()*_pendingTransactions.size()));
		
		AvatarSimulationPendingTransaction tr;
		for(int i=0; i<_pendingTransactions.size(); i++) {
			tr = _pendingTransactions.get((i+index)%pendingTransactions.size());
			// Empty transition?
			if ((tr.elementToExecute instanceof AvatarTransition) && (executeEmptyTransition)) {
				AvatarTransition atr = (AvatarTransition)(tr.elementToExecute);
				if (!(atr.hasDelay()) && !(atr.hasCompute()) && !(atr.hasActions())){
					//TraceManager.addDev("Empty transition?");
					if (nbOfTransactions(tr.asb, _pendingTransactions) < 2) {
						tr.isSilent = true;
						//TraceManager.addDev("Yes");
						return tr;
					}
				}
			// State entering?
			} else if (((tr.elementToExecute instanceof AvatarState) ||  (tr.elementToExecute instanceof AvatarStopState)) && (executeStateEntering)) {
				//TraceManager.addDev("Empty state enter?");
				if (nbOfTransactions(tr.asb, _pendingTransactions) < 2) {
					tr.isSilent = true;
					//TraceManager.addDev("Yes");
					return tr;
				}
			}
		}
		
		return null;
	}
	
	public int nbOfTransactions(AvatarSimulationBlock _asb, Vector<AvatarSimulationPendingTransaction> _pendingTransactions) {
		int cpt = 0;
		for(AvatarSimulationPendingTransaction tr: _pendingTransactions) {
			if (tr.asb == _asb) {
				cpt ++;
			}
		}
		return cpt;
	}
	
	
	
	
	public Vector<AvatarSimulationPendingTransaction> selectTransactions(Vector<AvatarSimulationPendingTransaction> _pendingTransactions) {
		Vector<AvatarSimulationPendingTransaction> ll = new Vector<AvatarSimulationPendingTransaction>();
		
		// Silent transition ?
		AvatarSimulationPendingTransaction tr = getRandomSilentTransactionToExecute(_pendingTransactions);
		if (tr != null) {
			ll.add(tr);
			indexSelectedTransaction = -1;
			silentTransactionExecuted = true;
			return ll;
		}
		
		// Put in ll the first possible logical transaction which is met
		// Random select the first index if none has been selected
		if (indexSelectedTransaction == -1) {
			//TraceManager.addDev("No transition selected");
			indexSelectedTransaction = (int)(Math.floor(Math.random()*_pendingTransactions.size()));
		}
		
		AvatarSimulationPendingTransaction currentTransaction = _pendingTransactions.get(indexSelectedTransaction);
		ll.add(currentTransaction);
		indexSelectedTransaction = -1;
		return ll;
	}
	
	public boolean performSelectedTransactions(Vector<AvatarSimulationPendingTransaction> _pendingTransactions) {
		
		if (_pendingTransactions.size() == 1) {
			long tempo_clock_Value = clockValue; 
			preExecutedTransaction(_pendingTransactions.get(0));
			_pendingTransactions.get(0).asb.runSoloPendingTransaction(_pendingTransactions.get(0), allTransactions, tempo_clock_Value, MAX_TRANSACTION_IN_A_ROW, bunchid);
			postExecutedTransaction(_pendingTransactions.get(0));
			previousBlock = _pendingTransactions.get(0).asb;
			AvatarSimulationTransaction transaction0 = _pendingTransactions.get(0).asb.getLastTransaction();
			if (_pendingTransactions.get(0).isBroadcast) {
				TraceManager.addDev("BROADCAST");
				transaction0.isBroadcast = true;
				transaction0.isSolo = true;
			} 
			
			if (_pendingTransactions.get(0).linkedTransaction != null) {
				tempo_clock_Value = clockValue; 
				preExecutedTransaction(_pendingTransactions.get(0).linkedTransaction);
				_pendingTransactions.get(0).linkedTransaction.asb.runSoloPendingTransaction(_pendingTransactions.get(0).linkedTransaction, allTransactions, tempo_clock_Value, MAX_TRANSACTION_IN_A_ROW, bunchid);
				postExecutedTransaction(_pendingTransactions.get(0).linkedTransaction);
				AvatarSimulationTransaction transaction1 = _pendingTransactions.get(0).linkedTransaction.asb.getLastTransaction();
				transaction1.linkedTransaction = transaction0;
			}
			
		
			if (_pendingTransactions.get(0).linkedTransactions != null) {
				//TraceManager.addDev("BROADCAST");
				tempo_clock_Value = clockValue; 
				for(AvatarSimulationPendingTransaction aspt: _pendingTransactions.get(0).linkedTransactions) {
					TraceManager.addDev("Executing broadcast transactions");
					preExecutedTransaction(aspt);
					aspt.asb.runSoloPendingTransaction(aspt, allTransactions, tempo_clock_Value, MAX_TRANSACTION_IN_A_ROW, bunchid);
					postExecutedTransaction(aspt);
					AvatarSimulationTransaction transaction1 = aspt.asb.getLastTransaction();
					transaction1.linkedTransaction = transaction0;
					transaction0.isSolo = false;
				}
			}
			
			if (!(_pendingTransactions.get(0).isSilent)) {
				bunchid++;
			}
			
			return true;
		}
		
		return false;
			
	}
	
	public void preExecutedTransaction(AvatarSimulationPendingTransaction _aspt) {
		if (_aspt.elementToExecute instanceof AvatarActionOnSignal) {
			AvatarSignal sig = ((AvatarActionOnSignal)(_aspt.elementToExecute)).getSignal();
			AvatarRelation rel = avspec.getAvatarRelationWithSignal(sig);
			if (sig.isOut()) {
				_aspt.isSending = true;
			} else {
				_aspt.isSending = false;
			}
			if (rel.isAsynchronous()) {
				_aspt.isSynchronous = false;
				if (sig.isOut()) {
					// Create the stucture to put elements
					// Get the index of the signal in the relation
					AvatarSimulationAsynchronousTransaction asat = new AvatarSimulationAsynchronousTransaction(rel, rel.getIndexOfSignal(sig));
					_aspt.linkedAsynchronousMessage = asat;
					// Testing whether the message can be lost or not
					if (rel.isLossy()) {
						int ra = ((int)(Math.random()*100))%2;
						if (ra == 0) {
							_aspt.isLost = true;
						} else {
							_aspt.isLost = false;
							asynchronousMessages.add(asat);
						}
					} else {
						asynchronousMessages.add(asat);
					}
				} else {
					// Must remove the asynchronous operation, and give the parameters
					AvatarSimulationAsynchronousTransaction asat = getAsynchronousMessage(rel, sig);
					asynchronousMessages.remove(asat);
					_aspt.linkedAsynchronousMessage = asat;
				}
			} else {
				_aspt.isSynchronous = true;
			}
		}
		
		if (_aspt.hasClock && !_aspt.durationSelected) {
			// Must select a value for the duration!
			if (_aspt.linkedTransaction != null) {
				if (_aspt.durationOnCurrent) {
					_aspt.selectedDuration = _aspt.myMinDuration + (int)(Math.floor(Math.random()*(_aspt.maxDuration-_aspt.myMinDuration)));
					//TraceManager.addDev("Selected duration:" + _aspt.selectedDuration + " myMinDuration=" + _aspt.myMinDuration + " maxDuration=" + _aspt.maxDuration);
					if (_aspt.durationOnOther) {
						_aspt.linkedTransaction.durationSelected = true;
						_aspt.linkedTransaction.selectedDuration = _aspt.selectedDuration;
					} else {
						_aspt.linkedTransaction.hasClock = false;
					}
				}
			} else {
				_aspt.selectedDuration = _aspt.myMinDuration + (int)(Math.floor(Math.random()*(_aspt.maxDuration-_aspt.myMinDuration)));
				//TraceManager.addDev("Selected duration:" + _aspt.selectedDuration + " myMinDuration=" + _aspt.myMinDuration + " maxDuration=" + _aspt.maxDuration);
			}
		}
	}
	
	public void postExecutedTransaction(AvatarSimulationPendingTransaction _aspt) {
		clockValue = _aspt.clockValueAtEnd;
	}
	
	public synchronized void backOneTransactionBunch() {
		backOneTransactionBunch(false);
	}
	
	public synchronized void backOneTransactionBunch(boolean _rec) {
		if ( (state != DONT_EXECUTE) && (state != TERMINATED)) {
			return;
		}
		
		if (allTransactions.size() == 0) {
			return;
		}
		
		//TraceManager.addDev("Backward size="+ allTransactions.size());
		
		// Remove one transaction
		// Getting last transaction
		
		AvatarSimulationTransaction ast = allTransactions.get(allTransactions.size()-1);
		long bunchid_tmp = ast.bunchid;
		
		boolean isAllSilent = true;
		
		while((ast != null) && (ast.bunchid == bunchid_tmp)) {
			allTransactions.removeElementAt(allTransactions.size()-1);
			if (ast.asb != null) {
				ast.asb.removeLastTransaction(ast);
			}
			
			if (!ast.silent) {
				isAllSilent = false;
			}
			
			// Must handle asynchronous messages
			if (ast.receivedMessage != null) {
				asynchronousMessages.add(0, ast.receivedMessage);
			}
			
			if (ast.sentMessage != null) {
				asynchronousMessages.remove(ast.sentMessage);
			}
			
			if (allTransactions.size() > 0) {
				ast = allTransactions.get(allTransactions.size()-1);
			} else {
				ast = null;
			}
		}
		
		if (isAllSilent) {
			backOneTransactionBunch(true);
		}
		
		if (_rec) {
			return;
		}
		
		//TraceManager.addDev("Backward size="+ allTransactions.size());
		
		if (allTransactions.size() > 0) {
			bunchid = (allTransactions.get(allTransactions.size()-1).bunchid) + 1;
			clockValue = (allTransactions.get(allTransactions.size()-1)).clockValueWhenFinished;
		} else {
			bunchid = 0;
			clockValue =  0;
		}
		
		AvatarSimulationTransaction.setID(allTransactions.size());
		
		setNbOfCommands(0);
		nbOfCommandsActivated = true;
		newState = true;
		notifyAll();
	}
	
	public void printExecutedTransactions() {
		for(AvatarSimulationTransaction ast: allTransactions) {
			TraceManager.addDev(ast.toString() + "\n");
		}
	}
	
	public String getStringExecutedTransactions() {
		StringBuffer sb = new StringBuffer("");
		for(AvatarSimulationTransaction ast: allTransactions) {
			sb.append(ast.toString() + "\n");
		}
		return sb.toString();
	}
	
	
    // Must split transactions when a broadcast transactions contains more than one transaction per block
	private Vector<AvatarSimulationPendingTransaction> workOnBroadcastTransactions(Vector<AvatarSimulationPendingTransaction> _transactions) {
		Vector<AvatarSimulationPendingTransaction> ll = new Vector<AvatarSimulationPendingTransaction>();
		
        AvatarSimulationPendingTransaction asptfound, newaspt;
        Point p;
        
        while(true) {
            asptfound = null;
            p = null;
            for(AvatarSimulationPendingTransaction aspt: _transactions) {
                if ((aspt.isBroadcast) && (aspt.linkedTransactions != null)) {
                    if ((p = aspt.hasDuplicatedBlockTransaction()) != null) {
                        TraceManager.addDev("FOUND DUPLICATED BLOCK");
                        asptfound = aspt;
                        break;
                    }
                }
            }
            if (asptfound == null) {
                return _transactions;
            }
            newaspt = asptfound.fullCloneMe();
            newaspt.linkedTransactions.removeElementAt(p.x);
            _transactions.add(newaspt);
            asptfound.linkedTransactions.removeElementAt(p.y);
        }
	}
	
	private void workOnABroadcastTransaction(Vector<AvatarSimulationPendingTransaction> _oldTransactions, Vector<AvatarSimulationPendingTransaction> _newTransactions, AvatarSimulationPendingTransaction _aspt) {
		boolean isMet = false;
		// Other transactions?
		for (AvatarSimulationPendingTransaction aspt: _oldTransactions) {
			if ((aspt.elementToExecute == _aspt.elementToExecute) && (aspt != _aspt)) {
				isMet = true;
				break;
			}
		}
		
		if (!isMet) {
			_newTransactions.add(_aspt);
			return;
		}
		
		// Working on that broadcast
		// Searching for a timed broadcast transaction
		isMet = false;
		AvatarSimulationPendingTransaction untimedTransaction = null;
		for (AvatarSimulationPendingTransaction aspt: _oldTransactions) {
			if (aspt.elementToExecute == _aspt.elementToExecute) {
				if (aspt.hasConfiguredDurationMoreThan0()) {
					isMet = true;
					break;
				} else {
					untimedTransaction = aspt;
				}
			}
		}
		
		if (!isMet) {
			// At least one untimed transaction: We only aggregate all untimed transactions to untimedTransaction
			untimedTransaction.linkedTransactions = new Vector<AvatarSimulationPendingTransaction>();
			for (AvatarSimulationPendingTransaction aspt: _oldTransactions) {
				if ((aspt != untimedTransaction) && (aspt.elementToExecute == untimedTransaction.elementToExecute)) {
					if (!(aspt.hasConfiguredDurationMoreThan0())) {
						untimedTransaction.linkedTransactions.add(aspt);
					}
				}
			}
			_newTransactions.add(untimedTransaction);
			return;
		}
		
		
		// Must see whether other transactions can be agregated
		// Timing issues are resolved afterwards
		/*for (AvatarSimulationPendingTransaction aspt: _oldTransactions) {
			if ((aspt != _aspt) && (aspt.isBroadcast) && (aspt.) {
			}
		}*/
		
		// We locate the transaction with the minimum max, and all transactions with a minimum which is strctly 
		// higher than this max are removed
		int max = Integer.MAX_VALUE;
		Vector<AvatarSimulationPendingTransaction> ll = new Vector<AvatarSimulationPendingTransaction>();
		for (AvatarSimulationPendingTransaction aspt: _oldTransactions) {
			if (aspt.myMaxDuration < max) {
				max = aspt.myMaxDuration;
			}
		}
		
		if (max < 0) {
			return;
		}
		
		for (AvatarSimulationPendingTransaction aspt: _oldTransactions) {
			if (aspt.myMinDuration <= max) {
				ll.add(aspt);
			}
		}
		
		if (ll.size() == 0) {
			return;
		}
		
		if (ll.size() == 1) {
			_newTransactions.add(ll.get(0));
			return;
		}
		
		// -> At least two transactions
		// Then, for each interval between a min and another min / or a max, 
		// We compute sets of possible transactions
		
		ll = basicSortOnMinDuration(ll);
		
		AvatarSimulationPendingTransaction a0;
		AvatarSimulationPendingTransaction tmp;
		for(int i=0; i<ll.size(); i++) {
			a0 = ll.get(i);
			// We compute all possible sets of transactions that are before a0
			// a0 is then cloned with all possibilities
			//_newTransactions.add(a0);
			makeRandom(a0, 0, i, ll, _newTransactions);
		}

		TraceManager.addDev("Size of new transactions: " + _newTransactions.size());
		
		return;
	}
	
	private void makeRandom(AvatarSimulationPendingTransaction mainTransaction, int index, int indexMax, Vector<AvatarSimulationPendingTransaction> _transactions, Vector<AvatarSimulationPendingTransaction> _newTransactions) {
		if (index >= indexMax) {
			return;
		}
		
		// Dont add the current one to the list
		makeRandom(mainTransaction, index+1, indexMax, _transactions, _newTransactions);
		
		// Add the current one to the list
		AvatarSimulationPendingTransaction aspt = _transactions.get(index);
		AvatarSimulationPendingTransaction asptnew = mainTransaction.fullCloneMe();
		
		if (asptnew.linkedTransactions == null) {
			asptnew.linkedTransactions = new Vector<AvatarSimulationPendingTransaction>();
		}
		
		asptnew.linkedTransactions.add(aspt);
		_newTransactions.add(asptnew);
		makeRandom(asptnew, index+1, indexMax, _transactions, _newTransactions);
		
	}
	
	
	public Vector<AvatarSimulationPendingTransaction> basicSortOnMinDuration(Vector<AvatarSimulationPendingTransaction> _vector) {
		
		if (_vector.size() == 1) {
			return _vector;
		}
		
		Vector<AvatarSimulationPendingTransaction> ll = new Vector<AvatarSimulationPendingTransaction>();
		
		
		int min;
		int index = 0;
		int cpt = 0;
		
		
		while(_vector.size() >0) {
			min = Integer.MAX_VALUE;
			index = 0;
			cpt = 0;
			for(AvatarSimulationPendingTransaction aspt: _vector) {
				if(aspt.myMinDuration < min) {
					index = cpt;
					min = aspt.myMinDuration;
				}
				cpt ++;
			}
		
			ll.add(_vector.get(index));
			_vector.remove(index);
		}
		
		return ll;
	}
	
	
	
	/*public synchronized void waitForKillResetOrBackward() {
		while(stopped && !killed) {
			try {
				wait();
			} catch (Exception e) {
			}
		}
	}
	
	public synchronized void waitForUnstopped() {
		while(stopped && !killed) {
			try {
				wait();
			} catch (Exception e) {
			}
		}
	}
	
	public synchronized void unstop() {
		stopped = false;
		notifyAll();
	}
	
	public synchronized void stopSimulation() {
		//TraceManager.addDev("Ask for simulation stop");
		notifyAll();
		stopped = true;
	}
	
	public synchronized void killSimulation() {
		TraceManager.addDev("Simulation killed");
		killed = true;
		stopped = true;
		notifyAll();
	}
	
	public void setMode(int _mode) {
		mode = _mode;
		
		if (mode == STOPPED) {
			unsetNbOfCommands();
		}
		
		if (asi != null) {
			asi.setMode(mode);
		}
	}
	

	
	public void setNbOfCommands(int _nbOfCommands) {
		nbOfCommands = _nbOfCommands;
	}
	
	public void unsetNbOfCommands() {
		nbOfCommands = -1;
	}
	
	public void stopSimulation(boolean _go) {
		setMode(STOPPED);
		unsetNbOfCommands();
		//TraceManager.addDev("Simulation stopped at time: " + clockValue + "\n--------------------------------------");
		waitForUnstopped();
		if (_go && !killed) {
			setMode(RUNNING);
		}
	}*/
	
	public AvatarSimulationBlock getPreviousBlock() {
		return previousBlock;
	}
	
	public Vector<AvatarSimulationPendingTransaction> getPendingTransitions() {
		return pendingTransactions;
	}
	
	public void setIndexSelectedTransaction(int _index) {
		//TraceManager.addDev("Selected transition: " + _index);
		indexSelectedTransaction = _index;
	}
	
	public void setExecuteEmptyTransition(boolean _b) {
		executeEmptyTransition = _b;
	}
	
	public void setExecuteStateEntering(boolean _b) {
		executeStateEntering = _b;
	}

}