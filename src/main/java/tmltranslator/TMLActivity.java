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

package tmltranslator;

import myutil.Conversion;
import myutil.TraceManager;

import java.util.Vector;

/**
 * Class TMLActivity
 * Creation: 23/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 23/11/2005
 */
public class TMLActivity extends TMLElement {
    private TMLActivityElement first;
    private Vector<TMLActivityElement> elements;


    public TMLActivity(String name, Object reference) {
        super(name, reference);
        elements = new Vector<TMLActivityElement>();
    }

    public TMLActivity copy() {
        TMLActivity newAct = new TMLActivity(this.name, this.referenceObject);
        newAct.setFirst(this.first);

        for (TMLActivityElement act : elements) {
            newAct.addElement(act);
        }

        return newAct;
    }

    public boolean contains(TMLActivityElement _elt) {
        return elements.contains(_elt);
    }

    public void setFirst(TMLActivityElement _tmlae) {
        first = _tmlae;
        addElement(_tmlae);
    }

    public TMLActivityElement getFirst() {
        return first;
    }

    public TMLActivityElement get(int index) {
        return elements.elementAt(index);
    }

    public void removeElementAt(int index) {
        elements.removeElementAt(index);
    }

    public void removeElement(TMLActivityElement _element) {
        elements.remove(_element);
    }

    public int nElements() {
        return elements.size();
    }

    public void addElement(TMLActivityElement _tmlae) {
        elements.add(_tmlae);
    }


    public void addLinkElement(TMLActivityElement _previous, TMLActivityElement _tmlae) {
        addElement(_tmlae);
        _previous.addNext(_tmlae);
    }

    public TMLActivityElement getPrevious(TMLActivityElement tmlae) {
        TMLActivityElement ae;
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae.hasNext(tmlae)) {
                return ae;
            }
        }
        return null;
    }


    public TMLActivityElement findReferenceElement(Object reference) {
        TMLActivityElement ae;
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae.getReferenceObject() == reference) {
                return ae;
            }
        }
        return null;
    }

    public int getMaximumSelectEvtSize() {
        int found = -1;
        int next;
        TMLActivityElement ae;
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLSelectEvt) {
                next = ae.getNbNext();
                if (next > found) {
                    found = next;
                }
            }
        }
        return found;
    }

    private void replaceAllNext(TMLActivityElement _oldE, TMLActivityElement _newE) {
        TMLActivityElement tmlae;
        for (int i = 0; i < elements.size(); i++) {
            tmlae = elements.elementAt(i);
            tmlae.setNewNext(_oldE, _newE);
        }
    }

    private TMLRandomSequence findTMLRandomSequence() {
        TMLActivityElement tmlae;
        for (int i = 0; i < elements.size(); i++) {
            tmlae = elements.elementAt(i);
            if (tmlae instanceof TMLRandomSequence) {
                return (TMLRandomSequence) tmlae;
            }
        }

        return null;
    }

    public void replaceElement(TMLActivityElement _oldE, TMLActivityElement _newE) {
        _newE.setNexts(_oldE.getNexts());
        replaceAllNext(_oldE, _newE);
        elements.add(_newE);
        elements.remove(_oldE);
    }

    public void removeAllRandomSequences(TMLTask _task) {
        int idRandomSequence = 0;
        TMLRandomSequence tmlrs = findTMLRandomSequence();

        while (tmlrs != null) {
            replaceRandomSequence(_task, tmlrs, idRandomSequence);
            idRandomSequence++;
            tmlrs = findTMLRandomSequence();
        }
    }

    private void replaceRandomSequence(TMLTask _task, TMLRandomSequence _tmlrs, int _idRandomSequence) {
        int nnext = _tmlrs.getNbNext();
        int i;

        if (nnext == 0) {
            TMLStopState adstop = new TMLStopState("stop", _tmlrs.getReferenceObject());
            addElement(adstop);
            removeElement(_tmlrs);
            replaceAllNext(_tmlrs, adstop);
            return;
        }

        // At least one next!
        if (nnext == 1) {
            TMLActivityElement tmlae = _tmlrs.getNextElement(0);
            removeElement(_tmlrs);
            replaceAllNext(_tmlrs, tmlae);
            return;
        }

        // At least two nexts -> use of a loop combined with a choice
        String name;
        TMLChoice choice = new TMLChoice("choice for random sequence", _tmlrs.getReferenceObject());
        elements.addElement(choice);

        TMLForLoop loop = new TMLForLoop("loop for random sequence", _tmlrs.getReferenceObject());
        elements.addElement(loop);
        name = "looprd__" + _idRandomSequence;
        TMLAttribute loopAttribute = new TMLAttribute(name, name, new TMLType(TMLType.NATURAL), "0");
        _task.addAttribute(loopAttribute);
        loop.setInit(name + "=0");
        loop.setCondition(name + " < " + nnext);
        loop.setIncrement(name + " = " + name + " + 1");

        TMLStopState tmlstop = new TMLStopState("stop", _tmlrs.getReferenceObject());
        addElement(tmlstop);

        TMLActionState[] tmlactions = new TMLActionState[nnext];
        TMLActionState tmlaction;
        TMLAttribute[] attributes = new TMLAttribute[nnext];


        for (i = 0; i < nnext; i++) {
            name = "rd__" + _idRandomSequence + "__" + i;
            attributes[i] = new TMLAttribute(name, name, new TMLType(TMLType.BOOLEAN), "false");
            _task.addAttribute(attributes[i]);

            tmlactions[i] = new TMLActionState("Setting random sequence", _tmlrs.getReferenceObject());
            elements.add(tmlactions[i]);
            tmlactions[i].setAction(name + " = false");

            tmlaction = new TMLActionState("Setting random sequence", _tmlrs.getReferenceObject());
            elements.add(tmlaction);
            tmlaction.setAction(name + " = true");
            tmlaction.addNext(_tmlrs.getNextElement(i));

            choice.addNext(tmlaction);
            choice.addGuard("[not(" + name + ")]");

            if (i != 0) {
                tmlactions[i - 1].addNext(tmlactions[i]);
            }
        }

        replaceAllNext(_tmlrs, tmlactions[0]);
        tmlactions[nnext - 1].addNext(loop);
        loop.addNext(choice);
        loop.addNext(tmlstop);
        removeElement(_tmlrs);
    }

    public void splitActionStatesWithUnderscoreVariables(TMLTask _task) {
        //TraceManager.addDev("Splitting actions in task " + _task.getName());

        TMLActivityElement ae;
        Vector<TMLActionState> states = new Vector<TMLActionState>();
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLActionState) {
                states.add((TMLActionState) ae);
            }
        }

        for (TMLActionState as : states) {
            splitActionStatesWithUnderscoreVariables(as, _task);
        }

    }

    private void splitActionStatesWithUnderscoreVariables(TMLActionState _ae, TMLTask _task) {
        // Is ae if the form name0 = name1 with variables in the task of type name0__ and name1__ ?
        String s = _ae.getAction();

        if (s == null) {
            return;
        }

        //TraceManager.addDev("Analyzing action to split : " + s);

        s = s.trim();

        if (s.length() == 0) {
            return;
        }

        int index0 = s.indexOf('=');
        if (index0 == -1) {
            return;
        }

        String name0 = s.substring(0, index0).trim();
        String name1 = s.substring(index0 + 1, s.length()).trim();

        //TraceManager.addDev("name0=" + name0 + " name1=" + name1);

        if (!TMLTextSpecification.isAValidId(name0)) {
            return;
        }

        if (!TMLTextSpecification.isAValidId(name1)) {
            return;
        }

        Vector<TMLAttribute> v0 = _task.getAllTMLAttributesStartingWith(name0 + "__");
        Vector<TMLAttribute> v1 = _task.getAllTMLAttributesStartingWith(name1 + "__");

        //TraceManager.addDev("size");

        if ((v0.size() == 0) || (v0.size() != v1.size())) {
            return;
        }

        //TraceManager.addDev("Analyzing types");
        for (int i = 0; i < v0.size(); i++) {
            if (v0.get(i).getType() == v1.get(i).getType()) {
                return;
            }
        }

        //TraceManager.addDev("Found action to split : " + s);

        TMLActionState previous, tmlas;
        TMLActivityElement tmlae = _ae.getNextElement(0);

        _ae.setAction(v0.get(0).getName() + " = " + v1.get(0).getName());

        if (v0.size() == 1) {
            return;
        }

        _ae.clearNexts();
        previous = _ae;

        for (int i = 1; i < v0.size(); i++) {
            tmlas = new TMLActionState(previous.getName(), previous.getReferenceObject());
            tmlas.setAction(v0.get(i).getName() + " = " + v1.get(i).getName());
            elements.add(tmlas);
            previous.addNext(tmlas);
            previous = tmlas;
        }

        previous.addNext(tmlae);
    }

    public void splitActionStatesWithDollars(TMLTask _task) {
        //TraceManager.addDev("Splitting actions in task " + _task.getName());

        TMLActivityElement ae;
        Vector<TMLActionState> states = new Vector<TMLActionState>();
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLActionState) {
                states.add((TMLActionState) ae);
            }
        }

        for (TMLActionState as : states) {
            splitActionStatesWithDollars(as, _task);
        }
    }

    private void splitActionStatesWithDollars(TMLActionState _ae, TMLTask _task) {
        // Is ae if the form name0 = name1 with variables in the task of type name0__ and name1__ ?
        String s = _ae.getAction();

        if (s == null) {
            return;
        }

        //TraceManager.addDev("Analyzing action to split : " + s);

        s = s.trim();

        if (s.length() == 0) {
            return;
        }

        int index0 = s.indexOf('$');
        if (index0 == -1) {
            return;
        }

        String name0 = s.substring(0, index0).trim();
        String name1 = s.substring(index0 + 1, s.length()).trim();

        if ((name0.length() == 0) || (name1.length() == 0)) {
            _ae.setAction(Conversion.replaceAllString(_ae.getAction(), "$", " ").trim());
            return;
        }

        //TraceManager.addDev("Found action to split : " + s);

        TMLActionState previous, tmlas;
        TMLActivityElement tmlae = _ae.getNextElement(0);

        TraceManager.addDev("Setting action0 to " + name0);
        _ae.setAction(name0);
        _ae.clearNexts();
        previous = _ae;

        tmlas = new TMLActionState(previous.getName(), previous.getReferenceObject());
        tmlas.setAction(name1);
        TraceManager.addDev("Setting action1 to " + name1);
        elements.add(tmlas);
        previous.addNext(tmlas);
        previous = tmlas;
        previous.addNext(tmlae);

        splitActionStatesWithDollars(tmlas, _task);

    }

    public int computeMaxID() {
        int max = -1;
        TMLActivityElement ae;
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            max = Math.max(max, ae.getID());
        }
        return max;
    }

    public void computeCorrespondance(TMLElement[] _correspondance) {
        _correspondance[getID()] = this;
        TMLActivityElement ae;
        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            _correspondance[ae.getID()] = ae;
        }

    }

    public Vector<TMLActivityElement> getElements() {
        return elements;
    }

    public void replaceWaitEventWith(TMLEvent oldEvt, TMLEvent newEvt) {
        TMLActivityElement ae;

        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLWaitEvent) {
                ((TMLWaitEvent) ae).replaceEventWith(oldEvt, newEvt);
            }
        }
    }

    public void replaceSendEventWith(TMLEvent oldEvt, TMLEvent newEvt) {
        TMLActivityElement ae;

        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLSendEvent) {
                ((TMLSendEvent) ae).replaceEventWith(oldEvt, newEvt);
            }
        }
    }

    public void replaceReadChannelWith(TMLChannel oldChan, TMLChannel newChan) {
        TMLActivityElement ae;

        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLReadChannel) {
                ((TMLReadChannel) ae).replaceChannelWith(oldChan, newChan);
            }
        }
    }

    public void replaceWriteChannelWith(TMLChannel oldChan, TMLChannel newChan) {
        TMLActivityElement ae;

        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLWriteChannel) {
                ((TMLWriteChannel) ae).replaceChannelWith(oldChan, newChan);
            }
        }
    }

    public void addSendEventAfterWriteIn(TMLChannel chan, TMLEvent evt, String action) {
        TMLActivityElement ae;
        TMLWriteChannel twc;
        int cpt = 0;

        Vector<TMLSendEvent> newElements = new Vector<TMLSendEvent>();

        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLWriteChannel) {
                twc = (TMLWriteChannel) ae;
                for (int j = 0; j < twc.getNbOfChannels(); j++) {
                    if (twc.getChannel(j) == chan) {
                        TMLSendEvent send = new TMLSendEvent("SendEvt" + cpt, ae.getReferenceObject());
                        send.setEvent(evt);
                        //Vector nexts = ae.getNexts();
                        for (TMLActivityElement o : ae.getNexts()) {
                            send.addNext(o);
                        }

                        newElements.add(send);
                        send.addParam(action);
                        ae.clearNexts();
                        ae.addNext(send);
                    }
                }
            }
        }

        for (TMLSendEvent s : newElements) {
            elements.add(s);
        }
    }

    public void addSendAndReceiveEventAfterWriteIn(TMLChannel chan, TMLEvent evt1, TMLEvent evt2, String action1, String action2) {
        TMLActivityElement ae;
        TMLWriteChannel twc;
        int cpt = 0;

        Vector<TMLActivityElementEvent> newElements = new Vector<TMLActivityElementEvent>();

        for (int i = 0; i < elements.size(); i++) {
            ae = elements.elementAt(i);
            if (ae instanceof TMLWriteChannel) {
                twc = (TMLWriteChannel) ae;
                for (int j = 0; j < twc.getNbOfChannels(); j++) {
                    if (twc.getChannel(j) == chan) {
                        TMLSendEvent send = new TMLSendEvent("SendEvt" + cpt, ae.getReferenceObject());
                        send.setEvent(evt1);
                        TMLWaitEvent receive = new TMLWaitEvent("RecvEvt" + cpt, ae.getReferenceObject());
                        receive.setEvent(evt2);

                        // Vector nexts = ae.getNexts();
                        for (TMLActivityElement o : ae.getNexts()) {
                            receive.addNext(o);
                        }

                        send.addNext(receive);
                        newElements.add(send);
                        newElements.add(receive);
                        send.addParam(action1);
                        receive.addParam(action2);
                        ae.clearNexts();
                        ae.addNext(send);
                    }
                }
            }
        }

        for (TMLActivityElementEvent s : newElements) {
            elements.add(s);
        }
    }

    public void removeEmptyInfiniteLoop() {
        TMLForLoop loop = null;
        for (TMLActivityElement elt : elements) {
            if (elt instanceof TMLForLoop) {
                if (((TMLForLoop) elt).isInfinite()) {
                    loop = (TMLForLoop) elt;
                    break;
                }
            }
        }

        if (loop != null) {
            TMLActivityElement next = (loop.getNexts()).get(0);
            if ((next == null) || (next instanceof TMLStopState)) {
                //Replace the element pointing to the infinite loop to the element at getNext(0)
                for (TMLActivityElement elt : elements) {
                    elt.setNewNext(loop, next);
                }
            }

            removeEmptyInfiniteLoop();
        }
    }

    public String toXML() {
        String s = new String("<ACTIVITY first=\"" + elements.indexOf(first) + "\">\n");

        for (TMLActivityElement elt : elements) {
            s += elt.toXML(elements);
        }
        s += "</ACTIVITY>\n";

        return s;
    }


    // returns -1 if the WC cannot be computed
    // The function follows execution paths and concatenate ExecI values
    // Loops are assumed to be executed only once
    public int getWorstCaseIComplexity() {
        return getWorstCaseIComplexity(getFirst());
    }

    public int getWorstCaseIComplexity(TMLActivityElement tae) {
        //TraceManager.addDev("Handling op:" + tae);
        TMLActivityElement currentElement = tae;
        int result = 0;
        int ret;

        if (tae == null) {
            return 0;
        }

        try {


                if ((currentElement instanceof TMLForLoop) || (currentElement instanceof TMLSequence) || (currentElement instanceof TMLRandomSequence)) {
                    // We consider each next independently
                    for (TMLActivityElement elt : currentElement.getNexts()) {
                        ret = getWorstCaseIComplexity(elt);
                        if (ret == -1) {
                            //TraceManager.addDev("1. -1 in Handling op:" + tae);
                            return -1;
                        } else {
                            result += ret;
                        }
                    }

                } else if ((currentElement instanceof TMLChoice) || (currentElement instanceof TMLSelectEvt)) {
                    for (TMLActivityElement elt : currentElement.getNexts()) {
                        ret = getWorstCaseIComplexity(elt);
                        if (ret == -1) {
                            //TraceManager.addDev("2. -1 in Handling op:" + tae);
                            return -1;
                        } else {
                            result = Math.max(ret, result);
                        }
                    }

                } else if (currentElement instanceof TMLExecI) {
                    ret = getWorstCaseIComplexity(currentElement.getNextElement(0));
                    if (ret == -1) {
                        //TraceManager.addDev("3. -1 in Handling op:" + tae);
                        return -1;
                    }
                    // Get the value exec value
                    String value = ((TMLExecI) (currentElement)).getAction();
                    int val = Integer.decode(value).intValue();
                    if (val > 0) {
                        result = ret + val;
                    } else {
                        result = ret;
                    }


                } else if (currentElement instanceof TMLExecIInterval) {
                    ret = getWorstCaseIComplexity(currentElement.getNextElement(0));
                    if (ret == -1) {
                        //TraceManager.addDev("4. -1 in Handling op:" + tae);
                        return -1;
                    }
                    // Get the value exec value
                    String value = ((TMLExecI) (currentElement)).getAction().trim();
                    int index = value.indexOf(" ");
                    if (index != -1) {
                        value = value.substring(index+1, value.length());
                    }
                    int val = Integer.decode(value).intValue();
                    if (val > 0) {
                        result = ret + val;
                    } else {
                        result = ret;
                    }


                } else {
                    return getWorstCaseIComplexity(currentElement.getNextElement(0));
                }
        } catch (Exception e) {
            TraceManager.addDev("Exception in Complexity computation:" + e.getMessage());
            return -1;
        }

        return result;
    }
}
