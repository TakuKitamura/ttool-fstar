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
 * AvatarLibraryFunction is used to represent a library function that can be further used in state machine diagrams.
 * <p>
 * A library function is defined by:
 * <ul>
 * <li>a name</li>
 * <li>a set of {@link AvatarAttribute} representing the parameters of the function,</li>
 * <li>a set of {@link AvatarAttribute} for local usage,</li>
 * <li>a set of {@link AvatarSignal} that are used to communicate,</li>
 * <li>a set of {@link AvatarAttribute} that will contain the return values of the function and</li>
 * <li>an {@link AvatarStateMachine} that describes the content of the function.</li>
 * </ul>
 * <p>
 *
 * @version 1.0 04.07.2016
 * @author Florian LUGOU
 */
public class AvatarLibraryFunction extends AvatarElement implements AvatarTranslator, AvatarStateMachineOwner {

    /**
     * The list of parameters of the function. Their values should never be reaffected.
     *
     * <p>Note that these are only placeholders and should not be used outside this class.</p>
     */
    private List<AvatarAttribute> parameters;

    /**
     * The list of variables local to the function.
     *
     * <p>Note that these are only placeholders and should not be used outside this class.</p>
     */
    private List<AvatarAttribute> attributes;

    /**
     * The list of signals used by the function.
     *
     * <p>Note that these are only placeholders and should not be used outside this class.</p>
     */
    private List<AvatarSignal> signals;

    /**
     * The list of attribute that will hold the return values of the function. These shouldn't be elements of {@link AvatarLibraryFunction#attributes} or {@link AvatarLibraryFunction#parameters}.
     *
     * <p>Note that these are only placeholders and should not be used outside this class.</p>
     */
    private List<AvatarAttribute> returnAttributes;

    /**
     * The list of methods that can be used by the function.
     *
     */
    private List<AvatarMethod> methods;

    /**
     * The state machine describing the behaviour of the function.
     */
    private AvatarStateMachine asm;

    /**
     * The specification that this library function is part of.
     */
    private AvatarSpecification avspec;

    /**
     * Counter of invocations of this library function.
     */
    private int counter;


    /**
     * Basic constructor of the function function.
     *
     * @param name
     *      The name that identifies this function.
     * @param avspec
     *      The specification this function is part of.
     * @param referenceObject
     *      The graphical element that this function is related to.
     */
    public AvatarLibraryFunction (String name, AvatarSpecification avspec, Object referenceObject) {
        super(name, referenceObject);

        this.avspec = avspec;

        this.parameters = new LinkedList<AvatarAttribute> ();
        this.signals = new LinkedList<AvatarSignal> ();
        this.returnAttributes = new LinkedList<AvatarAttribute> ();
        this.attributes = new LinkedList<AvatarAttribute> ();
        this.methods = new LinkedList<AvatarMethod> ();

        this.asm = new AvatarStateMachine (this, "statemachineoffunction__" + name, referenceObject);
        this.counter = 0;
    }

    /**
     * @return : a unique counter for library function call.
     */
    public int getCounter()
    {
        return this.counter++;
    }

    /**
     * Set counter for this library function.
     * @param counter : counter to set
     */
    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    @Override
    public AvatarSpecification getAvatarSpecification () {
        return this.avspec;
    }

    /**
     * Return the list of parameters of the function.
     *
     * @return The list of parameters.
     */
    public List<AvatarAttribute> getParameters () {
        return this.parameters;
    }

    /**
     * Add a parameter for this function.
     *
     * @param attr The parameter to add.
     */
    public void addParameter (AvatarAttribute attr) {
        this.parameters.add (attr);
    }

    /**
     * Return the list of signals.
     *
     * @return The list of signals.
     */
    public List<AvatarSignal> getSignals () {
        return this.signals;
    }

    /**
     * Add a signal.
     *
     * @param signal The signal to add.
     */
    public void addSignal (AvatarSignal signal) {
        this.signals.add (signal);
    }

    @Override
    public AvatarSignal getAvatarSignalWithName (String signalName) {
        for (AvatarSignal signal: this.signals)
            if (signal.getName ().equals (signalName))
                return signal;

        return null;
    }

    /**
     * Return the list of return values.
     *
     * @return The list of return values.
     */
    public List<AvatarAttribute> getReturnAttributes () {
        return this.returnAttributes;
    }

    /**
     * Add a return value.
     *
     * @param returnAttribute
     *      The return value to add.
     */
    public void addReturnAttribute (AvatarAttribute returnAttribute) {
        this.returnAttributes.add (returnAttribute);
    }

    /**
     * Return the list of attributes local to the function.
     *
     * @return The list of local attributes.
     */
    public List<AvatarAttribute> getLocalAttributes () {
        return this.attributes;
    }

    @Override
    public List<AvatarAttribute> getAttributes () {
        List<AvatarAttribute> result = new LinkedList<AvatarAttribute> ();

        for (AvatarAttribute attr: this.attributes)
            result.add (attr);
        for (AvatarAttribute attr: this.returnAttributes)
            result.add (attr);
        for (AvatarAttribute attr: this.parameters)
            result.add (attr);

        return result;
    }

    @Override
    public void addAttribute (AvatarAttribute attribute) {
        this.attributes.add (attribute);
    }

    /**
     * Return the list of methods.
     *
     * @return The list of methods used by this function.
     */
    public List<AvatarMethod> getMethods () {
        return this.methods;
    }

    @Override
    public AvatarMethod getAvatarMethodWithName (String methodName) {
        for (AvatarMethod method: this.methods)
            if (method.getName ().equals (methodName))
                return method;

        return null;
    }

    /**
     * Add a method.
     *
     * @param method
     *      The method to add to this function.
     */
    public void addMethod (AvatarMethod method) {
        this.methods.add (method);
    }

    @Override
    public AvatarStateMachine getStateMachine () {
        return this.asm;
    }

    @Override
    public AvatarAttribute getAvatarAttributeWithName (String name) {
        for (AvatarAttribute attr: this.parameters)
            if (attr.getName ().equals (name))
                return attr;
        for (AvatarAttribute attr: this.attributes)
            if (attr.getName ().equals (name))
                return attr;
        for (AvatarAttribute attr: this.returnAttributes)
            if (attr.getName ().equals (name))
                return attr;
        return null;
    }

    /**
     * Add all of the temporary attributes used by the function to the block.
     *
     * @param block
     *      The block to which the attributes should be added.
     * @param mapping
     *      A mapping from placeholders to attributes of the block.
     */
    public void addAttributesToBlock (AvatarBlock block, Map<AvatarAttribute, AvatarAttribute> mapping) {
        for (AvatarAttribute attribute: this.attributes) {
            // TODO: We should use different attributes for different library function call
            String name = this.name + "__" + attribute.getName ();
            AvatarAttribute attr = block.getAvatarAttributeWithName (name);
            if (attr == null) {
                attr = new AvatarAttribute (name, attribute.getType (), block, block.getReferenceObject ());
                if (attribute.getInitialValue() != null) {
                    attr.setInitialValue(attribute.getInitialValue());
                }
                block.addAttribute (attr);
            }

            mapping.put (attribute, attr);
        }
    }

    /**
     * Add mappings from parameters and return values placeholders to "real" attributes.
     *
     * @param mapping
     *      A mapping from placeholders to attributes of the block.
     * @param parameters
     *      A list of the attributes that were passed as parameters.
     * @param returnAttributes
     *      A list of the attributes that should receive return values.
     */
    public void addAttributesToMapping( Map<AvatarAttribute, AvatarAttribute> mapping, List<AvatarAttribute> parameters, List<AvatarAttribute> returnAttributes) {
        Iterator<AvatarAttribute> placeholders = this.parameters.iterator ();
        for (AvatarAttribute attr: parameters)
            mapping.put (placeholders.next (), attr);

        placeholders = this.returnAttributes.iterator ();
        for (AvatarAttribute attr: returnAttributes)
            mapping.put (placeholders.next (), attr);
    }

    /**
     * Add mappings from signals placeholders to "real" signals.
     *
     * @param mapping
     *      A mapping from placeholders to signals of the block.
     * @param signals
     *      A list of the attributes that were passed as parameters.
     */
    public void addSignalsToMapping( Map<AvatarSignal, AvatarSignal> mapping, List<AvatarSignal> signals) {
        Iterator<AvatarSignal> placeholders = this.signals.iterator ();
        for (AvatarSignal signal: signals)
            mapping.put (placeholders.next (), signal);
    }

    /**
     * Inner class used to pass arguments for the translation process.
     */
    private class TranslatorArgument {

        /**
         * A mapping from placeholders to attributes of the block.
         */
        public Map<AvatarAttribute, AvatarAttribute> placeholdersMapping;

        /**
         * A mapping from placeholders to signals of the block.
         */
        public Map<AvatarSignal, AvatarSignal> signalsMapping;

        /**
         * The previous element of the state machine being created.
         */
        public AvatarStateMachineElement previousElement;

        /**
         * The last element of the state machine being created.
         */
        public AvatarStateMachineElement lastElement;

        /**
         * A mapping from placeholder state machine elements to "real" elements.
         */
        public Map<AvatarStateMachineElement, AvatarStateMachineElement> elementsMapping;

        /**
         * The block the function call belongs to.
         */
        public AvatarBlock block;

        /**
         * The reference object associated to the function call being translated.
         */
        public Object referenceObject;

        /**
         * The counter for the library function call.
         */
        public int counter;

        /**
         * Basic constructor.
         *
         * @param placeholdersMapping
         *      A mapping from placeholders to attributes of the block.
         * @param signalsMapping
         *      A mapping from placeholders to signals of the block.
         * @param previousElement
         *      The previous element of the state machine being created.
         * @param lastElement
         *      The last element of the state machine being created.
         * @param elementsMapping
         *      A mapping from placeholder state machine elements to <i>real</i> elements.
         * @param block
         *      The block the function call belongs to.
         * @param referenceObject
         *      The reference object associated to the function call being translated.
         * @param counter
         *      The counter for the library function call.
         */
        public TranslatorArgument( Map<AvatarAttribute, AvatarAttribute> placeholdersMapping, Map<AvatarSignal, AvatarSignal> signalsMapping, AvatarStateMachineElement previousElement, AvatarStateMachineElement lastElement, Map<AvatarStateMachineElement, AvatarStateMachineElement> elementsMapping, AvatarBlock block, Object referenceObject, int counter) {
            this.placeholdersMapping = placeholdersMapping;
            this.signalsMapping = signalsMapping;
            this.previousElement = previousElement;
            this.lastElement = lastElement;
            this.elementsMapping = elementsMapping;
            this.block = block;
            this.referenceObject = referenceObject;
            this.counter = counter;
        }
    }

    /**
     * Translate the state machine described by this function in the context of a particular block.
     *
     * @param placeholdersMapping
     *      A mapping from placeholders to attributes of the block.
     * @param signalsMapping
     *      A mapping from placeholders to signals of the block.
     * @param firstElement
     *      The first element of the state machine to be created.
     * @param block
     *      The block the function call belongs to.
     * @param referenceObject
     *      The reference object associated to the function call being translated.
     * @param counter
     *      The counter of this library function
     *
     * @return The last element of the state machine created.
     */
    public AvatarState translateASMWithMapping( Map<AvatarAttribute, AvatarAttribute> placeholdersMapping, Map<AvatarSignal, AvatarSignal> signalsMapping,
                                                AvatarStateMachineElement firstElement, AvatarBlock block, Object referenceObject, int counter) {
        /* Create the last state */
        AvatarState lastState = new AvatarState ("exit_" + this.name + "_" + counter, referenceObject);
        block.getStateMachine().addElement(lastState);

        /* Create the argument object that will be passed to translation functions */
        Object arg = new TranslatorArgument (
                placeholdersMapping,
                signalsMapping,
                firstElement,
                lastState,
                new HashMap<AvatarStateMachineElement, AvatarStateMachineElement> (),
                block,
                referenceObject,
                counter);

        /* Translate the state machine, starting from the first state */
        this.asm.getStartState ().translate (this, arg);

        return lastState;
    }

    /**
     * Translate elements that follow the current state.
     *
     * @param asme
     *      The newly created element.
     * @param placeholder
     *      The state machine element that has just be translated.
     * @param arg
     *      The object containing the arguments to pass to the translation functions.
     */
    private void translateNext (AvatarStateMachineElement asme, AvatarStateMachineElement placeholder, TranslatorArgument arg) {

        //TraceManager.addDev("TRANSLATION of:" + asme.getExtendedName());

        arg.previousElement.addNext (asme);
        arg.elementsMapping.put (placeholder, asme);

        // Must be added to the state machine as well?
        arg.block.getStateMachine().addElement(asme);

        /* If there is no next element, consider this as an end state */
        if (placeholder.nbOfNexts () == 0) {
            asme.addNext (arg.lastElement);
            return;
        }

        /* Loop through the next elements */
        for (AvatarStateMachineElement next: placeholder.getNexts ()) {
            AvatarStateMachineElement existingNext = arg.elementsMapping.get (next);
            /* Check if next element has already been translated */
            if (existingNext != null)
                asme.addNext (existingNext);
            else {
                arg.previousElement = asme;
                next.translate (this, arg);
            }
        }
    }

    @Override
    public void translateTimerOperator (AvatarTimerOperator _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        AvatarTimerOperator asme;

        if (_asme instanceof AvatarSetTimer) {
            // TODO: isn't the name used for the timer ?
            asme = new AvatarSetTimer (this.name + "_" + arg.counter + "__" + _asme.getName (), arg.referenceObject);

            // TODO: should probably replace attributes too, right ?
            ((AvatarSetTimer) asme).setTimerValue (((AvatarSetTimer) _asme).getTimerValue ());
        } else if (_asme instanceof AvatarResetTimer)
            asme = new AvatarResetTimer (this.name + "_" + arg.counter + "__" + _asme.getName (), arg.referenceObject);
        else if (_asme instanceof AvatarExpireTimer) 
            asme = new AvatarExpireTimer (this.name + "_" + arg.counter + "__" + _asme.getName (), arg.referenceObject);
        else
            /* !!! should not happen */
            return;
        
        asme.setTimer (arg.placeholdersMapping.get (_asme.getTimer ()));

        this.translateNext (asme, _asme, arg);
    }

    @Override
    public void translateActionOnSignal (AvatarActionOnSignal _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        AvatarActionOnSignal asme = new AvatarActionOnSignal (this.name + "_" + arg.counter + "__" + _asme.getName (), arg.signalsMapping.get (_asme.getSignal ()), arg.referenceObject);
        for (String s: _asme.getValues ()) {
            AvatarAttribute attr = this.getAvatarAttributeWithName (s);
            if (attr == null)
                asme.addValue (s);
            else
                asme.addValue (arg.placeholdersMapping.get (attr).getName ());
        }

        this.translateNext (asme, _asme, arg);
    }

    @Override
    public void translateTransition (AvatarTransition _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;


        //printCorrespondance(_arg);

        AvatarTransition asme = new AvatarTransition (arg.block, this.name + "_" + arg.counter + "__" + _asme.getName (), arg.referenceObject);

        AvatarGuard guard = _asme.getGuard ().clone ();
        guard.replaceAttributes (arg.placeholdersMapping);
        asme.setGuard (guard);

        //TraceManager.addDev("minD:" + _asme.getMinDelay() + " in block " + arg.block.getName());
        String minD = replaceAttributesInExpr(_asme.getMinDelay(), _arg);
        //TraceManager.addDev("minD:" + minD);

        //TraceManager.addDev("maxD:" + _asme.getMaxDelay() + " in block " + arg.block.getName());
        String maxD = replaceAttributesInExpr(_asme.getMaxDelay(), _arg);
        //TraceManager.addDev("maxD:" + maxD);

        asme.setDelays(minD, maxD);

        asme.setComputes (replaceAttributesInExpr(_asme.getMinCompute (), _arg),
                replaceAttributesInExpr(_asme.getMaxCompute (), _arg));




        for (AvatarAction _action: _asme.getActions ()) {
            AvatarAction action = _action.clone ();
            //TraceManager.addDev("\n*** Action BEFORE replace:" + action.toString() + " " + action.getClass().getCanonicalName());
            action.replaceAttributes (arg.placeholdersMapping);
            //TraceManager.addDev("Action AFTER replace:" + action.getName() + "\n");
            asme.addAction (action);
        }

        this.translateNext (asme, _asme, arg);
    }

    @Override
    public void translateStartState (AvatarStartState _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        _asme.getNext (0).translate (this, arg);
    }

    @Override
    public void translateState (AvatarState _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        /* Mark state as non checkable as it is up to now impossible to display the
         * reachability of state in a function for a particular invocation of this
         * function.
         */
        AvatarState asme = new AvatarState (this.name + "_" + arg.counter + "__" + _asme.getName (), arg.referenceObject, false, false);
        asme.setHidden (true);
        asme.addEntryCode (_asme.getEntryCode ());

        this.translateNext (asme, _asme, arg);
    }

    @Override
    public void translateRandom (AvatarRandom _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        AvatarRandom asme = new AvatarRandom (this.name + "_" + arg.counter + "__" + _asme.getName (), arg.referenceObject);
        asme.setValues (replaceAttributesInExpr(_asme.getMinValue (), _arg), replaceAttributesInExpr(_asme.getMaxValue (), _arg));
        asme.setFunctionId (_asme.getFunctionId ());
        asme.setExtraAttribute1(_asme.getExtraAttribute1());
        asme.setExtraAttribute2(_asme.getExtraAttribute2());
        asme.setVariable (arg.placeholdersMapping.get (this.getAvatarAttributeWithName (_asme.getVariable ())).getName ());

        this.translateNext (asme, _asme, arg);
    }

    @Override
    public void translateStopState (AvatarStopState _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        arg.previousElement.addNext (arg.lastElement);
    }

    @Override
    public void translateLibraryFunctionCall (AvatarLibraryFunctionCall _asme, Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;

        AvatarLibraryFunctionCall asme = new AvatarLibraryFunctionCall (this.name + "_" + arg.counter + "__" + _asme.getName (), _asme.getLibraryFunction (), arg.referenceObject);
        for (AvatarAttribute attr: _asme.getParameters ())
            asme.addParameter (arg.placeholdersMapping.get (attr));
        for (AvatarSignal signal: _asme.getSignals ())
            asme.addSignal (arg.signalsMapping.get (signal));
        for (AvatarAttribute attr: _asme.getReturnAttributes ())
            asme.addReturnAttribute (arg.placeholdersMapping.get (attr));

        this.translateNext (asme, _asme, arg);
    }

    @Override
    public AvatarLibraryFunction advancedClone(AvatarSpecification avspec) {
        AvatarLibraryFunction result = new AvatarLibraryFunction(this.name, avspec, this.referenceObject);
        this.cloneLinkToReferenceObjects (result);

        result.setCounter(this.counter);
        for (AvatarAttribute aa: this.parameters)
            result.addParameter(aa.advancedClone(result));
        for (AvatarSignal sig: this.signals)
            result.addSignal(sig.advancedClone(result));
        for (AvatarAttribute aa: this.returnAttributes)
            result.addReturnAttribute(aa.advancedClone(result));
        for (AvatarAttribute aa: this.attributes)
            result.addAttribute(aa.advancedClone(result));
        for (AvatarMethod met: this.methods)
            result.addMethod(met.advancedClone(result));

        this.asm.advancedClone(result.getStateMachine(), result);

        return result;
    }


    private String replaceAttributesInExpr(String expr, Object _arg) {
        if (expr == null) {
            return null;
        }

        expr = expr.trim();

        if (expr.length() == 0) {
            return expr;
        }

        TranslatorArgument arg = (TranslatorArgument) _arg;
        AvatarTerm term = AvatarTerm.createFromString(arg.block, expr);

        if (term == null) {
            TraceManager.addDev("NULL term in /" + expr + "/ of block /" + arg.block.getName() + "/");
        }

        if (term instanceof AvatarAttribute) {
            AvatarAttribute ret = arg.placeholdersMapping.get(term);
            if (ret == null) {
                for(AvatarAttribute atbis: arg.placeholdersMapping.keySet()) {
                    if (atbis.getName().equals(term.getName())) {
                        ret = arg.placeholdersMapping.get(atbis);
                        break;
                    }
                }

                if (ret == null) {
                    TraceManager.addDev("NULL correspondance");
                    return expr;
                }
            }
            TraceManager.addDev("Ok correspondance");
            return ret.getName();
        }


        term.replaceAttributes(arg.placeholdersMapping);

        return term.toString();

    }


    private void printCorrespondance(Object _arg) {
        TranslatorArgument arg = (TranslatorArgument) _arg;
        for(AvatarAttribute elt1: arg.placeholdersMapping.keySet()) {
            AvatarAttribute elt2 = arg.placeholdersMapping.get(elt1);
            TraceManager.addDev("Correspondance " + elt1 + " --> " + elt2);
        }

    }
}
