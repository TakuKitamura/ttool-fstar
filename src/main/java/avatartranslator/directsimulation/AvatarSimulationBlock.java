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


package avatartranslator.directsimulation;

import avatartranslator.*;
import avatartranslator.modelchecker.SpecificationBlock;
import myutil.*;

import java.util.Vector;

/**
 * Class AvatarSimulationBlock
 * Avatar: notion of block in simulation
 * Creation: 14/12/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 14/12/2010
 */
public class AvatarSimulationBlock {
    public final static int NOT_STARTED = 0;
    public final static int STARTED = 1;
    public final static int COMPLETED = 2;

    private AvatarBlock block;
    private AvatarSpecificationSimulation sim;
    private AvatarSimulationTransaction lastTransaction;
    private Vector<AvatarSimulationTransaction> transactions;
    private boolean completed;
    public boolean selected; // Free use for graphic purpose
    private int forcedRandom = -1;


    //private int elapsedTime;

    public AvatarSimulationBlock(AvatarBlock _block, AvatarSpecificationSimulation _sim) {
        block = _block;
        sim = _sim;
        transactions = new Vector<AvatarSimulationTransaction>();
        completed = false;
        //elapsedTime = 0;
    }

    public void forceRandom(int _value) {
        forcedRandom = _value;
        //TraceManager.addDev("Random has been forced to:" + _value);
    }


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

    public String toString() {
        return "AvatarSimulationBlock:" + getName();
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

    public boolean setAttributeValue(int _index, String _value) {
        if (lastTransaction == null) {
            return block.setAttributeValue(_index, _value);
        }


        return lastTransaction.setAttributeValue(_index, _value);
    }

    public Vector<AvatarSimulationTransaction> getTransactions() {
        return transactions;
    }

    public Vector<AvatarSimulationPendingTransaction> getPendingTransactions(Vector<AvatarSimulationTransaction> _allTransactions, long _clockValue,
                                                                             int _maxTransationsInARow, long _bunchid) {
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
            makeExecutedTransaction(_allTransactions, ass, _clockValue, null, _bunchid);

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
        //double prob = 0.5;
        for (int i = 0; i < lastTransaction.executedElement.nbOfNexts(); i++) {
            asme = lastTransaction.executedElement.getNext(i);

            if (asme == null) {
                TraceManager.addDev("Null element after: " + lastTransaction.executedElement.toString());
            }
            guardOk = true;
            // Guard on transition ? -> must evaluate the guard!
            if (asme instanceof AvatarTransition) {
                AvatarTransition at = (AvatarTransition) (asme);
                //prob = at.getProbability();
                if (at.isGuarded()) {
                    // Must evaluate the guard
                    String guard = at.getGuard().toString();
                    String s = Conversion.replaceAllString(guard, "[", "").trim();
                    s = Conversion.replaceAllString(s, "]", "").trim();
                    guardOk = evaluateBoolExpression(s, lastTransaction.attributeValues);
                    //TraceManager.addDev("guard ok=" + guardOk);
                }
            }

            if (guardOk) {
                aspt = new AvatarSimulationPendingTransaction();
                aspt.asb = this;
                //aspt.probability = prob;
                aspt.elementToExecute = lastTransaction.executedElement.getNext(i);
                aspt.previouslyExecutedElement = lastTransaction.executedElement;
                if ((aspt.elementToExecute instanceof AvatarTransition) && (lastTransaction.executedElement instanceof AvatarState)) {
                    AvatarTransition trans = (AvatarTransition) (aspt.elementToExecute);
                    if (trans.getNbOfAction() == 0) {
                        // empty transition, "empty" is the meaning of actions -> look for an action after
                        if (trans.getNext(0) != null) {
                            if (trans.getNext(0) instanceof AvatarActionOnSignal) {
                                aspt.involvedElement = trans;
                                aspt.elementToExecute = trans.getNext(0);
                            } else if (trans.getNext(0) instanceof AvatarRandom) {
                                aspt.involvedElement = trans;
                                aspt.elementToExecute = trans.getNext(0);
                            }
                        }
                    }
                }

                if (aspt.elementToExecute instanceof AvatarTransition) {
                    AvatarTransition trans = (AvatarTransition) (aspt.elementToExecute);
                    aspt.probability = trans.getProbability();
                    if (trans.hasDelay()) {
                        aspt.myMinDelay = newEvaluateIntExpression(trans.getMinDelay(), lastTransaction.attributeValues);
                        aspt.myMaxDelay = newEvaluateIntExpression(trans.getMaxDelay(), lastTransaction.attributeValues);
                        aspt.hasDelay = true;
                        aspt.extraParam1 = newEvaluateIntExpression(trans.getDelayExtra1(), lastTransaction.attributeValues);
                        aspt.extraParam2 = newEvaluateIntExpression(trans.getDelayExtra2(), lastTransaction.attributeValues);
                        aspt.delayDistributionLaw = trans.getDelayDistributionLaw();
                        if (lastTransaction != null) {
                            if (lastTransaction.clockValueWhenFinished < _clockValue) {
                                aspt.hasElapsedTime = true;
                                aspt.elapsedTime = (int) (_clockValue - lastTransaction.clockValueWhenFinished);
                            }
                        }

                    }
                } else if (aspt.involvedElement instanceof AvatarTransition) {
                    AvatarTransition trans = (AvatarTransition) (aspt.involvedElement);
                    if (trans.hasDelay()) {
                        aspt.myMinDelay = newEvaluateIntExpression(trans.getMinDelay(), lastTransaction.attributeValues);
                        aspt.myMaxDelay = newEvaluateIntExpression(trans.getMaxDelay(), lastTransaction.attributeValues);
                        aspt.hasDelay = true;
                        aspt.extraParam1 = evaluateDoubleExpression(trans.getDelayExtra1(), lastTransaction.attributeValues);
                        aspt.extraParam2 = evaluateDoubleExpression(trans.getDelayExtra2(), lastTransaction.attributeValues);
                        aspt.delayDistributionLaw = trans.getDelayDistributionLaw();

                        //TraceManager.addDev(">>>>>   Signal with delay before");

                        if (lastTransaction != null) {
                            if (lastTransaction.clockValueWhenFinished < _clockValue) {
                                aspt.hasElapsedTime = true;
                                aspt.elapsedTime = (int) (_clockValue - lastTransaction.clockValueWhenFinished);
                            }
                        }
                    }
                }
                aspt.clockValue = _clockValue;

                if (aspt.hasElapsedTime) {
                    aspt.myMinDelay = aspt.myMinDelay - aspt.elapsedTime;
                    aspt.myMaxDelay = aspt.myMaxDelay - aspt.elapsedTime;
                    aspt.extraParam1 = aspt.extraParam1 - aspt.elapsedTime;
                }

                if (aspt.hasDelay) {
                    aspt.myMinDelay = Math.max(0, aspt.myMinDelay);
                    aspt.myMaxDelay = Math.max(0, aspt.myMaxDelay);
                    aspt.extraParam1 = Math.max(0, aspt.extraParam1);
                }


                ll.add(aspt);
            }
        }
        return ll;
    }

    public void runSoloPendingTransaction(AvatarSimulationPendingTransaction _aspt, Vector<AvatarSimulationTransaction> _allTransactions, long _clockValue, int _maxTransationsInARow, long _bunchid) {
        if (_aspt.involvedElement != null) {
            executeElement(_allTransactions, _aspt.involvedElement, _clockValue, _aspt, _bunchid);
            /*if (lastTransaction != null) {
              _clockValue = lastTransaction.clockValueWhenFinished;
              }*/
        }

        executeElement(_allTransactions, _aspt.elementToExecute, _clockValue, _aspt, _bunchid);


        //runToNextBlockingElement(_allTransactions, _clockValue, _maxTransationsInARow);
    }


    public void executeElement(Vector<AvatarSimulationTransaction> _allTransactions, AvatarStateMachineElement _elt, long _clockValue, AvatarSimulationPendingTransaction _aspt, long _bunchid) {
        //TraceManager.addDev("Execute Element");

        // Stop state
        if (_elt instanceof AvatarStopState) {
            makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt, _bunchid);

            // State
        } else if (_elt instanceof AvatarState) {
            makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt, _bunchid);

            // Random
        } else if (_elt instanceof AvatarRandom) {
            makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt, _bunchid);

            // Query
        } else if (_elt instanceof AvatarQueryOnSignal) {
            makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt, _bunchid);

            // Transition
        } else if (_elt instanceof AvatarTransition) {
            makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt, _bunchid);

            // Signal
        } else if (_elt instanceof AvatarActionOnSignal) {
            makeExecutedTransaction(_allTransactions, _elt, _clockValue, _aspt, _bunchid);
        }
    }

    public void makeExecutedTransaction(Vector<AvatarSimulationTransaction> _allTransactions, AvatarStateMachineElement _elt, long _clockValue,
                                        AvatarSimulationPendingTransaction _aspt, long _bunchid) {

        //TraceManager.addDev("Make executed transaction");

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
        ast.bunchid = _bunchid;
        if (lastTransaction != null) {
            ast.initialClockValue = lastTransaction.clockValueWhenFinished;
        }
        ast.clockValueWhenFinished = _clockValue;
        ast.duration = 0;
        ast.id = AvatarSimulationTransaction.setID();
        if (_aspt != null) {
            ast.silent = _aspt.isSilent;
        } else {
            ast.silent = true;
        }
        //TraceManager.addDev("Transaction " + ast.id + " silent=" + ast.silent);
        if (_aspt != null) {
            _aspt.clockValueAtEnd = _clockValue;
            if (_aspt.hasClock) {
                if (_aspt.hasElapsedTime) {
                    ast.duration = _aspt.elapsedTime + _aspt.selectedDuration;
                } else {
                    ast.duration = _aspt.selectedDuration;
                    ast.duration = Math.min(_aspt.myMaxDuration + _aspt.elapsedTime, ast.duration);
                    ast.duration = Math.max(_aspt.myMinDuration + _aspt.elapsedTime, ast.duration);
                }
                ast.clockValueWhenFinished = _aspt.selectedDuration + _clockValue;
                _aspt.clockValueAtEnd = ast.clockValueWhenFinished;
            }
            //TraceManager.addDev("Id= " + ast.id + " duration=" + ast.duration + " elapsed=" + _aspt.elapsedTime + " selectedDur=" + _aspt.selectedDuration + " at end: " + _aspt.clockValueAtEnd + "clockValue=" + _clockValue);
        }


        // Attributes
        Vector<String> attributeValues = new Vector<String>();
        String s;
        if (lastTransaction == null) {
            for (AvatarAttribute aa : block.getAttributes()) {
                s = new String(aa.getInitialValue());
                attributeValues.add(s);
            }
        } else {
            // Recopy of previous values
            for (String ss : lastTransaction.attributeValues) {
                attributeValues.add("" + ss);
            }
            // Transition?
            if (_elt instanceof AvatarTransition) {
                at = (AvatarTransition) (_elt);
                // Must compute new values of attributes
                if (at.hasActions()) {
                    actions = new Vector<String>();
                    for (i = 0; i < at.getNbOfAction(); i++) {
                        action = at.getAction(i).toString();
                        //TraceManager.addDev("action #" + i  + " = " + action);
                        makeAction(action, attributeValues, actions);
                    }
                    ast.actions = actions;
                }
            }

            // Random?
            if (_elt instanceof AvatarRandom) {
                AvatarRandom random = (AvatarRandom) (_elt);
                index = block.getIndexOfAvatarAttributeWithName(random.getVariable());
                if (index > -1) {
                    int valMin = newEvaluateIntExpression(random.getMinValue(), attributeValues);
                    int valMax = newEvaluateIntExpression(random.getMaxValue(), attributeValues);

                    double extra1;
                    try {
                        extra1 = Double.parseDouble(random.getExtraAttribute1());
                    } catch (Exception e) {
                        extra1 = 0.0;
                    }

                    double extra2;
                    //TraceManager.addDev("Extra2=" + random.getExtraAttribute2());
                    try {
                        extra2 = Double.parseDouble(random.getExtraAttribute2());
                    } catch (Exception e) {
                        //TraceManager.addDev("Extra2 exception");
                        extra2 = 0.0;
                    }
                    //TraceManager.addDev("Extra2=" + extra2);

                    if ((forcedRandom > -1) && (forcedRandom >= valMin) && (forcedRandom <= valMax)) {
                        // Use provided value as random value
                        //TraceManager.addDev("Force random");
                        valMin = forcedRandom;
                    } else {
                        // randomly select a value according to distribution law

                        valMin = makeRandom(valMin, valMax, random.getFunctionId(), extra1, extra2);
                    }
                    attributeValues.remove(index);
                    attributeValues.add(index, "" + valMin);
                    ast.actions = new Vector<String>();
                    ast.actions.add(random.getVariable() + " = " + valMin);
                }
            }

            if (_elt instanceof AvatarQueryOnSignal) {
                TraceManager.addDev("Query on signal");
                AvatarQueryOnSignal aqos = (AvatarQueryOnSignal) (_elt);
                index = block.getIndexOfAvatarAttributeWithName(aqos.getAttribute().getName());
                if (index > -1) {
                    int valFIFO = 0;

                    AvatarSpecification spec = block.getAvatarSpecification();
                    AvatarRelation ar = spec.getAvatarRelationWithSignal(aqos.getSignal());
                    if (ar != null) {
                        valFIFO = sim.getNbOfAsynchronousMessages(ar);

                        attributeValues.remove(index);
                        attributeValues.add(index, "" + valFIFO);

                        ast.actions = new Vector<String>();
                        ast.actions.add(aqos.getAttribute().getName() + " = " + valFIFO);
                    }
                }

            }

            // Action on signal?
            if (_elt instanceof AvatarActionOnSignal) {
                AvatarActionOnSignal aaos = (AvatarActionOnSignal) _elt;
                if (_aspt != null) {
                    // Must put the right parameters
                    if (_aspt.isSynchronous) {
                        // Synchronous call
                        if ((_aspt.isSending) && ((_aspt.linkedTransaction != null) || (_aspt.linkedTransactions != null))) {
                            // Synchronous Sending!
                            // Must be in the receiving transaction the right parameters
                            Vector<String> parameters = new Vector<String>();
                            //TraceManager.addDev("Adding value in :" + aaos);
                            for (i = 0; i < aaos.getNbOfValues(); i++) {
                                value = aaos.getValue(i);
                                // Must get the type of the value
                                //TraceManager.addDev("Sending aaos: " + aaos + " block=" + block.getName());
                                try {
                                    avat = aaos.getSignal().getListOfAttributes().get(i);
                                    result = "";
                                    if (avat.getType() == AvatarType.INTEGER) {
                                        //TraceManager.addDev("Evaluating expression, value=" + value);
                                        result += newEvaluateIntExpression(value, lastTransaction.attributeValues);
                                    } else if (avat.getType() == AvatarType.BOOLEAN) {
                                        result += evaluateBoolExpression(value, lastTransaction.attributeValues);
                                    }

                                    //TraceManager.addDev("Adding value:" + result);
                                    parameters.add(result);
                                } catch (Exception e) {
                                    TraceManager.addDev("EXCEPTION on adding value " + aaos);
                                }
                            }
                            //for(i=0; i<_aspt.linkedTransactions.size(); i++) {
                            if (_aspt.linkedTransaction != null) {
                                _aspt.linkedTransaction.parameters = parameters;
                            }

                            if (_aspt.linkedTransactions != null) {
                                for (AvatarSimulationPendingTransaction aspt0 : _aspt.linkedTransactions) {
                                    aspt0.parameters = parameters;
                                }
                            }
                            //}
                        } else if ((!(_aspt.isSending)) && (_aspt.parameters != null)) {
                            //TraceManager.addDev("Reading value " + aaos);
                            // Synchronous Receiving
                            String myAction = "";
                            for (i = 0; i < aaos.getNbOfValues(); i++) {
                                //TraceManager.addDev("Reading value #" + i);
                                param = _aspt.parameters.get(i);
                                name = aaos.getValue(i);
                                index = block.getIndexOfAvatarAttributeWithName(name);

                                if (index != -1) {
                                    attributeValues.remove(index);
                                    attributeValues.add(index, param);
                                    //TraceManager.addDev("Reading value:" + param);
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
                        if ((_aspt.isSending) && (_aspt.linkedAsynchronousMessage != null)) {

                            // Asynchronous Sending
                            String myAction = "";
                            _aspt.linkedAsynchronousMessage.firstTransaction = ast;
                            ast.sentMessage = _aspt.linkedAsynchronousMessage;
                            for (i = 0; i < aaos.getNbOfValues(); i++) {
                                value = aaos.getValue(i);
                                // Must get the type of the value
                                avat = aaos.getSignal().getListOfAttributes().get(i);
                                result = "";
                                if (avat.getType() == AvatarType.INTEGER) {
                                    result += newEvaluateIntExpression(value, lastTransaction.attributeValues);
                                } else if (avat.getType() == AvatarType.BOOLEAN) {
                                    result += evaluateBoolExpression(value, lastTransaction.attributeValues);
                                }
                                //TraceManager.addDev("Adding value:" + result);
                                _aspt.linkedAsynchronousMessage.addParameter(result);

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

                            ast.isLost = _aspt.isLost;

                        } else if ((!(_aspt.isSending)) && (_aspt.linkedAsynchronousMessage != null)) {
                            // Asynchronous Receiving
                            String myAction = "";
                            ast.linkedTransaction = _aspt.linkedAsynchronousMessage.firstTransaction;
                            ast.receivedMessage = _aspt.linkedAsynchronousMessage;
                            if (_aspt.linkedAsynchronousMessage.firstTransaction == null) {
                                TraceManager.addDev("NULL FIRST TRANSACTION !!!");
                            }
                            for (i = 0; i < aaos.getNbOfValues(); i++) {
                                param = _aspt.linkedAsynchronousMessage.getParameters().get(i);
                                name = aaos.getValue(i);
                                index = block.getIndexOfAvatarAttributeWithName(name);
                                if (index != -1) {
                                    attributeValues.remove(index);
                                    attributeValues.add(index, param);
                                    //TraceManager.addDev("Reading value:" + param);
                                    if (myAction.length() == 0) {
                                        myAction += "" + param;
                                    } else {
                                        myAction += ", " + param;
                                    }
                                }
                            }
                            if (_aspt.linkedAsynchronousMessage == null) {
                                TraceManager.addDev("NULL ASYN MSG");
                            }
                            if (myAction.length() > 0) {
                                ast.actions = new Vector<String>();
                                ast.actions.add(myAction);
                            }
                        } else {
                            TraceManager.addDev("ERROR TRANSACTION");
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
        //String nameOfMethod;
        int ind;

        AvatarAction action = AvatarTerm.createActionFromString(block, _action);
        // TODO: use the new AvatarAction class instead of re-parsing
        if (action.isAMethodCall()) {
            // Evaluate all elements of the method call!
            ind = _action.indexOf("(");
            if (ind == -1) {
                return;
            }
            nameOfVar = _action.substring(0, ind).trim();

            act = _action.substring(ind + 1, _action.length()).trim();

            ind = act.lastIndexOf(")");
            if (ind == -1) {
                return;
            }
            act = act.substring(0, ind);

            //ind = nameOfVar.indexOf("=");
//            if (ind != -1) {
//                nameOfMethod = nameOfVar.substring(ind + 1, nameOfVar.length());
//            } else {
//                nameOfMethod = nameOfVar;
//            }

            String[] params = act.split(",");
            String parameters = "";
            String s;
            int indexAtt;
            int cpt = 0;
            for (int i = 0; i < params.length; i++) {
                s = params[i].trim();
                if (s.length() > 0) {
                    indexAtt = block.getIndexOfAvatarAttributeWithName(s);
                    //TraceManager.addDev("indexAtt=" + indexAtt + " s=" + s);
                    if (indexAtt > -1) {
                        if (cpt > 0) {
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

        nameOfVar = _action.substring(0, ind).trim();
        act = _action.substring(ind + 1, _action.length());

        //TraceManager.addDev("1- Working on attribute =" + nameOfVar + " action=" + _action);


        // Variable
        //TraceManager.addDev("2- Working on attribute =" + nameOfVar);
        int indexVar = block.getIndexOfAvatarAttributeWithName(nameOfVar);
        if (indexVar != -1) {
            // int or bool???
            AvatarType type = block.getAttribute(indexVar).getType();
            if (type == AvatarType.INTEGER) {
                int result = newEvaluateIntExpression(act, _attributeValues);
                _actions.add(nameOfVar + " = " + result);
                _attributeValues.remove(indexVar);
                _attributeValues.add(indexVar, "" + result);
            } else if (type == AvatarType.BOOLEAN) {
                boolean bool = evaluateBoolExpression(act, _attributeValues);
                _actions.add(nameOfVar + " = " + bool);
                _attributeValues.remove(indexVar);
                _attributeValues.add(indexVar, "" + bool);
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

    public void removeLastTransaction(AvatarSimulationTransaction _ast) {
        if (lastTransaction == _ast) {
            transactions.removeElementAt(transactions.size() - 1);
            if (transactions.size() > 0) {
                lastTransaction = transactions.get(transactions.size() - 1);
            } else {
                lastTransaction = null;
            }
        }
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

    public double evaluateDoubleExpression(String _expr,  Vector<String> _attributeValues) {
        try {
            double ret = Double.parseDouble(_expr);
            return ret;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int newEvaluateIntExpression(String _expr, Vector<String> _attributeValues) {
        AvatarExpressionSolver e1 = new AvatarExpressionSolver(_expr);
        SpecificationBlock sb = new SpecificationBlock(_attributeValues);
        e1.buildExpression(block);
        return e1.getResult(sb);

    }

    /*public int evaluateIntExpression(String _expr, Vector<String> _attributeValues) {
        String act = _expr;
        int cpt = 0;
        for (String attrValue : _attributeValues) {
            if (attrValue.trim().startsWith("-")) {
                attrValue = "(0" + attrValue + ")";
            }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, getAttributeName(cpt), attrValue);
            cpt++;
        }

        //TraceManager.addDev("Evaluating expression: " + act);

        return (int) (new IntExpressionEvaluator().getResultOf(act));
    }*/

    public boolean evaluateBoolExpression(String _expr, Vector<String> _attributeValues) {
        String act = _expr;
        int cpt = 0;
        for (String attrValue : _attributeValues) {
            if (attrValue.trim().startsWith("-")) {
                attrValue = "(0" + attrValue + ")";
            }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, getAttributeName(cpt), attrValue);
            cpt++;
        }

        AvatarExpressionSolver aee = new AvatarExpressionSolver(act);
        if ( !(aee.buildExpression())) {
            TraceManager.addDev("4. Error with avatar expression solver:" + act);
            return false;
        }

        int[] attributes = AvatarSimulationTransaction.getAttributeValues(_attributeValues);

        return aee.getResult(attributes) != 0;

        /*BoolExpressionEvaluator bee = new BoolExpressionEvaluator();

        if (act.trim().startsWith("100")) {
            TraceManager.addDev("Current block " + this.getBlock().getName() + " lastTransaction=" + lastTransaction);
        }

        boolean result = bee.getResultOfWithIntExpr(act);
        //boolean result = bee.getResultOf(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error: " + bee.getError());
        }

        //TraceManager.addDev("Result of " + _expr + " = " + result);
        return result;*/
    }

    public int makeRandom(int minV, int maxV, int functionID, double extra1, double extra2) {
        switch (functionID) {
            case AvatarRandom.RANDOM_UNIFORM_LAW:
                //TraceManager.addDev("\n\n\n******* UNIFORM LAW ********");
                return (int) (Math.floor((Math.random()) * (maxV - minV + 1))) + minV;
            case AvatarRandom.RANDOM_TRIANGULAR_LAW:
                //TraceManager.addDev("\n\n\n******* TRIANGULAR LAW ********");
                return (int) (MyMath.triangularDistribution((double) (minV), (double) (maxV), extra1));
            case AvatarRandom.RANDOM_GAUSSIAN_LAW:
                //TraceManager.addDev("\n\n\n******* GAUSSIAN LAW ********");
                return (int)(Math.floor(MyMath.gaussianDistribution((double) (minV), (double) (maxV), extra1)));
            case AvatarRandom.RANDOM_LOG_NORMAL_LAW:
                try {
                    return (int) (Math.floor(MyMath.logNormalDistribution((double) (minV), (double) (maxV), extra1, extra2)));
                } catch (Exception e) {
                    TraceManager.addDev("Exception on log normal: " + e.getMessage());
                    return minV;
                }
            case AvatarRandom.RANDOM_EXPONENTIAL_LAW:
                try {
                    return (int) (Math.floor(MyMath.exponentialDistribution( (double) (minV), (double) (maxV), extra1) ));
                } catch (Exception e) {
                    TraceManager.addDev("Exception on exponential distribution: " + e.getMessage());
                    return minV;
                }
            case AvatarRandom.RANDOM_WEIBULL_LAW:
                try {
                    return (int) (Math.floor(MyMath.weibullDistribution( (double) (minV), (double) (maxV), extra1, extra2) ));
                } catch (Exception e) {
                    TraceManager.addDev("Exception on weibull distribution: " + e.getMessage());
                    return minV;
                }
        }
        return minV;
    }
}
