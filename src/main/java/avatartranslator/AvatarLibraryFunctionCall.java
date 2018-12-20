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

import java.util.HashMap;
import java.util.LinkedList;


/**
 * AvatarLibraryFunctionCall represent a call to a library function. It is part of an {@link AvatarStateMachine}.
 *
 * <p> While the attributes for parameters and return values, as well as the signals are only placeholders for {@link AvatarLibraryFunction}.
 * This class contains <i>real</i> attributes and signals (associated to an {@link AvatarBlock}) that will be mapped to the placeholders
 * of the function called.</p>
 *
 * @version 1.0 04.07.2016
 * @author Florian LUGOU
 */
public class AvatarLibraryFunctionCall extends AvatarStateMachineElement {

    /**
     * The list of parameters passed to the function.
     */
    private LinkedList<AvatarAttribute> parameters;

    /**
     * The list of signals that should be mapped to signals used by the function.
     */
    private LinkedList<AvatarSignal> signals;

    /**
     * The list of attributes that will hold the return values of the function.
     */
    private LinkedList<AvatarAttribute> returnAttributes;

    /**
     * The library function that corresponds to this call.
     */
    private AvatarLibraryFunction libraryFunction;

    /**
     * Counter for library function call.
     */
    private int counter;

    /**
     * Basic constructor of the function function.
     *
     * @param name
     *      The name that identifies this function call (different from each call, even of the same function).
     * @param referenceObject
     *      The graphical element that this function is related to.
     * @param libraryFunction
     *      The library function that corresponds to this call.
     */
    public AvatarLibraryFunctionCall (String name, AvatarLibraryFunction libraryFunction, Object referenceObject) {
        super(name, referenceObject);

        this.libraryFunction = libraryFunction;
        this.counter = this.libraryFunction.getCounter();
        this.parameters = new LinkedList<AvatarAttribute> ();
        this.signals = new LinkedList<AvatarSignal> ();
        this.returnAttributes = new LinkedList <AvatarAttribute> ();
    }

    /**
     * @return The counter for this library function call
     */
    public int getCounter()
    {
        return this.counter;
    }

    /**
     * Get the list of parameters passed to the function.
     *
     * @return The list of parameters passed to the function.
     */
    public LinkedList<AvatarAttribute> getParameters () {
        return this.parameters;
    }

    /**
     * Add a parameter passed to the function.
     *
     * @param parameter
     *      The parameter passed to the function.
     */
    public void addParameter (AvatarAttribute parameter) {
        this.parameters.add (parameter);
    }

    /**
     * Get the list of signals passed to the function.
     *
     * @return The list of signals passed to the function.
     */
    public LinkedList<AvatarSignal> getSignals () {
        return this.signals;
    }

    /**
     * Add a signal passed to the function.
     *
     * @param signal
     *      The signal passed to the function.
     */
    public void addSignal (AvatarSignal signal) {
        this.signals.add (signal);
    }

    /**
     * Get the list of return values.
     *
     * @return The list of return values.
     */
    public LinkedList<AvatarAttribute> getReturnAttributes () {
        return this.returnAttributes;
    }

    /**
     * Add a return value.
     *
     * @param returnAttribute
     *      The return value.
     */
    public void addReturnAttribute (AvatarAttribute returnAttribute) {
        this.returnAttributes.add (returnAttribute);
    }

    /**
     * Returns the library function associated to this call.
     *
     * @return The library function associated to this call.
     */
    public AvatarLibraryFunction getLibraryFunction () {
        return this.libraryFunction;
    }

    @Override
    public String getNiceName() {
        return "Library Function Call " + getName();
    }

    @Override
    public AvatarStateMachineElement basicCloneMe(AvatarStateMachineOwner _block) {
        AvatarLibraryFunctionCall asme = new AvatarLibraryFunctionCall(this.name, this.libraryFunction, this.referenceObject);
        for (AvatarAttribute attr: this.parameters)
            asme.addParameter (attr);
        for (AvatarSignal signal: this.signals)
            asme.addSignal (signal);
        for (AvatarAttribute attr: this.returnAttributes)
            asme.addReturnAttribute (attr);

        return asme;
    }

    /**
     * Inline the function call into another state machine.
     *
     * @param firstElement
     *      The first element that will be used as a base for the translation
     * @param block
     *      Avatar block
     *
     * @return The last state that marks the end of the function call.
     */
    public AvatarState inlineFunctionCall (AvatarBlock block, AvatarStateMachineElement firstElement) {
        HashMap<AvatarAttribute, AvatarAttribute> placeholdersMapping = new HashMap<AvatarAttribute, AvatarAttribute> ();

        /* Create new attributes for local variables */
        this.libraryFunction.addAttributesToBlock (block, placeholdersMapping);

        /* Add parameters to mapping */
        this.libraryFunction.addAttributesToMapping (placeholdersMapping, this.parameters, this.returnAttributes);

        /* Add signals to mapping */
        HashMap<AvatarSignal, AvatarSignal> signalsMapping = new HashMap<AvatarSignal, AvatarSignal> ();
        this.libraryFunction.addSignalsToMapping (signalsMapping, this.signals);

        /* Translate the state machine */
        return this.libraryFunction.translateASMWithMapping (placeholdersMapping, signalsMapping, firstElement, block, this.referenceObject, this.counter);
    }

    @Override
    public void translate (AvatarTranslator translator, Object arg) {
        translator.translateLibraryFunctionCall (this, arg);
    }
}
