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

import java.util.Vector;
import java.util.jar.Attributes;

/**
 * Class AvatarBlockTemplate
 * Templates of AVATAR blocks (Timers, etc.)
 * Creation: 09/07/2010
 *
 * @author Ludovic APVRILLE
 * @version 2.0 14/11/2019
 */
public class AvatarBlockTemplate {

    public AvatarBlockTemplate() {
    }

    public static AvatarBlock getTimerBlock(String _name, AvatarSpecification _avspec, Object _referenceBlock, Object _referenceSet, Object _referenceExpire, Object _referenceReset) {
        AvatarBlock ab = new AvatarBlock(_name, _avspec, _referenceBlock);

        /*AvatarAttribute aa2 = new AvatarAttribute("toto", AvatarType.INTEGER, _referenceBlock);
          ab.addAttribute(aa2);*/
        AvatarAttribute aa = new AvatarAttribute("value", AvatarType.INTEGER, ab, _referenceBlock);
        ab.addAttribute(aa);
        /*AvatarAttribute aa1 = new AvatarAttribute("__value", AvatarType.INTEGER, _referenceBlock);
          ab.addAttribute(aa1);*/

        AvatarSignal set = new AvatarSignal("set", AvatarSignal.IN, _referenceBlock);
        AvatarSignal reset = new AvatarSignal("reset", AvatarSignal.IN, _referenceBlock);
        AvatarSignal expire = new AvatarSignal("expire", AvatarSignal.OUT, _referenceBlock);

        AvatarAttribute val = new AvatarAttribute("value", AvatarType.INTEGER, ab, aa.getReferenceObject());
        set.addParameter(val);
        ab.addSignal(set);
        ab.addSignal(reset);
        ab.addSignal(expire);

        AvatarStateMachine asm = ab.getStateMachine();
        AvatarStartState ass = new AvatarStartState("start", _referenceBlock);

        asm.setStartState(ass);
        asm.addElement(ass);

        AvatarState as1 = new AvatarState("wait4set", _referenceBlock);
        asm.addElement(as1);

        AvatarState as2 = new AvatarState("wait4expire", _referenceBlock);
        asm.addElement(as2);

        AvatarActionOnSignal aaos1 = new AvatarActionOnSignal("set1", set, _referenceSet);
        aaos1.addValue("value");
        asm.addElement(aaos1);

        AvatarActionOnSignal aaos2 = new AvatarActionOnSignal("set2", set, _referenceSet);
        aaos2.addValue("value");
        asm.addElement(aaos2);

        AvatarActionOnSignal aaos3 = new AvatarActionOnSignal("reset1", reset, _referenceReset);
        asm.addElement(aaos3);

        AvatarActionOnSignal aaos4 = new AvatarActionOnSignal("reset2", reset, _referenceReset);
        asm.addElement(aaos4);

        AvatarActionOnSignal aaos5 = new AvatarActionOnSignal("expire", expire, _referenceExpire);
        asm.addElement(aaos5);

        AvatarTransition at;

        // set
        at = makeAvatarEmptyTransitionBetween(ab, asm, ass, as1, _referenceBlock);
        at = makeAvatarEmptyTransitionBetween(ab, asm, as1, aaos1, _referenceBlock);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaos1, as2, _referenceBlock);

        at = makeAvatarEmptyTransitionBetween(ab, asm, as2, aaos2, _referenceBlock);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaos2, as2, _referenceBlock);

        // expire
        at = makeAvatarEmptyTransitionBetween(ab, asm, as2, aaos5, _referenceBlock);
        at.setDelays("value", "value");
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaos5, as1, _referenceBlock);

        // reset
        at = makeAvatarEmptyTransitionBetween(ab, asm, as1, aaos3, _referenceBlock);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaos3, as1, _referenceBlock);

        at = makeAvatarEmptyTransitionBetween(ab, asm, as2, aaos4, _referenceBlock);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaos4, as1, _referenceBlock);


        return ab;

    }

    public static AvatarTransition makeAvatarEmptyTransitionBetween(AvatarBlock _block, AvatarStateMachine _asm,
                                                                    AvatarStateMachineElement _elt1, AvatarStateMachineElement _elt2, Object _reference) {
        AvatarTransition at = new AvatarTransition(_block, "tr", _reference);

        _asm.addElement(at);

        _elt1.addNext(at);
        at.addNext(_elt2);

        return at;
    }


    // WARNING: Does not handle the non blocking case
    public static AvatarBlock getFifoBlock(String _name, AvatarSpecification _avspec, AvatarRelation _ar, Object _referenceRelation, AvatarSignal _sig1, AvatarSignal _sig2, int _sizeOfFifo, int FIFO_ID) {
        AvatarBlock ab = new AvatarBlock(_name, _avspec, _referenceRelation);

        // Create the read and write signals
        AvatarSignal write = new AvatarSignal("write", AvatarSignal.IN, _referenceRelation);
        AvatarSignal read = new AvatarSignal("read", AvatarSignal.OUT, _referenceRelation);

        ab.addSignal(write); // corresponds to sig1
        ab.addSignal(read);  // corresponds to sig2


        // Creating the attributes of the signals
        // Same attributes for all signals
        for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
            write.addParameter(aa.advancedClone(null));
        }
        for (AvatarAttribute aa : _sig2.getListOfAttributes()) {
            read.addParameter(aa.advancedClone(null));
        }


        // Creating the attributes to support the FIFO
        // For each parameter, we create an attribute that is similar to the one of e.g. sig1
        // We duplicate this for the size of the fifo
        for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
            for (int i = 0; i < _sizeOfFifo; i++) {
                AvatarAttribute newA = aa.advancedClone(null);
                newA.setName("arg__" + aa.getName() + "__" + i);
                ab.addAttribute(newA);
            }
        }

        // If lossy, add corresponding lossy attributes
        if (_ar.isLossy()) {
            for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
                AvatarAttribute newL = aa.advancedClone(null);
                newL.setName("loss__" + aa.getName());
                ab.addAttribute(newL);
            }
        }

        // If non blocking, then, we need extra attributes
        if (!(_ar.isBlocking())) {
            for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
                AvatarAttribute newL = aa.advancedClone(null);
                newL.setName("bucket__" + aa.getName());
                ab.addAttribute(newL);
            }
        }

        // We create the attribute to manage the FIFO
        AvatarAttribute size = new AvatarAttribute("size", AvatarType.INTEGER, ab, _referenceRelation);
        size.setInitialValue("0");
        ab.addAttribute(size);

        AvatarAttribute maxSize = new AvatarAttribute("maxSize", AvatarType.INTEGER, ab, _referenceRelation);
        TraceManager.addDev("*********************************** Size of FIFO=" + _sizeOfFifo);
        maxSize.setInitialValue("" + _sizeOfFifo);
        ab.addAttribute(maxSize);

        // Where we write: the head
        AvatarAttribute head = new AvatarAttribute("head", AvatarType.INTEGER, ab, _referenceRelation);
        head.setInitialValue("0");
        ab.addAttribute(head);

        // Where we read: the tail
        AvatarAttribute tail = new AvatarAttribute("tail", AvatarType.INTEGER, ab, _referenceRelation);
        tail.setInitialValue("0");
        ab.addAttribute(tail);


        // Creating the state machine
        // Don't forget the isLossy

        AvatarTransition at;
        AvatarStateMachine asm = ab.getStateMachine();

        // Start state
        AvatarStartState ass = new AvatarStartState("start", _referenceRelation);
        asm.setStartState(ass);
        asm.addElement(ass);

        // Main state: Wait4Request
        AvatarState main = new AvatarState("Wait4Request", _referenceRelation);
        asm.addElement(main);
        at = makeAvatarEmptyTransitionBetween(ab, asm, ass, main, _referenceRelation);


        // Can write only if fifo is not full only if transition
        AvatarState testHead = new AvatarState("testHead", _referenceRelation);
        asm.addElement(testHead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, testHead, main, _referenceRelation);
        at.setGuard("[head<maxSize]");
        at = makeAvatarEmptyTransitionBetween(ab, asm, testHead, main, _referenceRelation);
        at.setGuard("[head==maxSize]");
        at.addAction("head=0");

        for (int i = 0; i < _sizeOfFifo; i++) {
            AvatarActionOnSignal aaos_write = new AvatarActionOnSignal("write__" + i, write, _referenceRelation);
            for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
                aaos_write.addValue("arg__" + aa.getName() + "__" + i);
            }
            asm.addElement(aaos_write);
            at = makeAvatarEmptyTransitionBetween(ab, asm, main, aaos_write, _referenceRelation);
            at.setGuard("[(size < maxSize) && (head==" + i + ")]");
            at = makeAvatarEmptyTransitionBetween(ab, asm, aaos_write, testHead, _referenceRelation);
            at.addAction("head = head + 1");
            at.addAction("size = size + 1");

        }
        // if is lossy, can write, and does not store this nor increase the fifo size
        if (_ar.isLossy()) {
            AvatarActionOnSignal aaos_write_loss = new AvatarActionOnSignal("writeloss__", write, _referenceRelation);
            for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
                aaos_write_loss.addValue("loss__" + aa.getName());
            }
            asm.addElement(aaos_write_loss);
            at = makeAvatarEmptyTransitionBetween(ab, asm, main, aaos_write_loss, _referenceRelation);
            at.setGuard("[(size < maxSize)]");
            at = makeAvatarEmptyTransitionBetween(ab, asm, aaos_write_loss, main, _referenceRelation);
        }

        // If it is blocking, then, the new message is written but not added
        if (!(_ar.isBlocking())) {
            AvatarActionOnSignal aaos_write_bucket = new AvatarActionOnSignal("writebucket__", write, _referenceRelation);
            for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
                aaos_write_bucket.addValue("bucket__" + aa.getName());
            }
            asm.addElement(aaos_write_bucket);
            at = makeAvatarEmptyTransitionBetween(ab, asm, main, aaos_write_bucket, _referenceRelation);
            at.setGuard("[(size == maxSize)]");
            at = makeAvatarEmptyTransitionBetween(ab, asm, aaos_write_bucket, main, _referenceRelation);
        }

        // Read
        AvatarState testTail = new AvatarState("testTail", _referenceRelation);
        asm.addElement(testTail);
        at = makeAvatarEmptyTransitionBetween(ab, asm, testTail, main, _referenceRelation);
        at.setGuard("[tail<maxSize]");
        at = makeAvatarEmptyTransitionBetween(ab, asm, testTail, main, _referenceRelation);
        at.setGuard("[tail==maxSize]");
        at.addAction("tail=0");
        for (int i = 0; i < _sizeOfFifo; i++) {
            AvatarActionOnSignal aaos_read = new AvatarActionOnSignal("read__" + i, read, _referenceRelation);
            for (AvatarAttribute aa : _sig1.getListOfAttributes()) {
                aaos_read.addValue("arg__" + aa.getName() + "__" + i);
            }
            asm.addElement(aaos_read);
            at = makeAvatarEmptyTransitionBetween(ab, asm, main, aaos_read, _referenceRelation);
            at.setGuard("[(size > 0) && (tail==" + i + ")]");
            at = makeAvatarEmptyTransitionBetween(ab, asm, aaos_read, testTail, _referenceRelation);
            at.addAction("tail = tail + 1");
            at.addAction("size = size - 1");
        }


        // Block is finished!

        return ab;
    }


    public static AvatarBlock getSWGraphBlock(String _name, AvatarSpecification _avspec, Object _refB,
                                              int duration, Vector<String> unblockedBy, Vector<String> unblockNext) {

        AvatarBlock ab = new AvatarBlock(_name, _avspec, _refB);

        // Create  signals
        AvatarSignal selectP = new AvatarSignal("selectP", AvatarSignal.IN, _refB);
        AvatarSignal stepP = new AvatarSignal("stepP", AvatarSignal.IN, _refB);
        AvatarAttribute att1 = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        stepP.addParameter(att1);
        AvatarSignal preemptP = new AvatarSignal("preemptP", AvatarSignal.IN, _refB);
        AvatarSignal finishP = new AvatarSignal("finishP", AvatarSignal.OUT, _refB);

        ab.addSignal(selectP);
        ab.addSignal(stepP);
        ab.addSignal(preemptP);
        ab.addSignal(finishP);

        // block / unblock signals

        Vector<AvatarSignal> unblokedBySigs = new Vector<>();
        for(String unblockName: unblockedBy) {
            AvatarSignal sig = new AvatarSignal(unblockName, AvatarSignal.IN, _refB);
            unblokedBySigs.add(sig);
            ab.addSignal(sig);
        }

        Vector<AvatarSignal> unblokingBySigs = new Vector<>();
        for(String unblockName: unblockNext) {
            AvatarSignal sig = new AvatarSignal(unblockName, AvatarSignal.OUT, _refB);
            unblokingBySigs.add(sig);
            ab.addSignal(sig);
        }


        // Create attributes
        AvatarAttribute durationAtt = new AvatarAttribute("duration", AvatarType.INTEGER, ab, _refB);
        durationAtt.setInitialValue(""+duration);
        ab.addAttribute(durationAtt);

        AvatarAttribute stepAtt = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        ab.addAttribute(stepAtt);

        // State machines
        AvatarTransition at;
        AvatarStateMachine asm = ab.getStateMachine();

        // Start state
        AvatarStartState ass = new AvatarStartState("start", _refB);
        asm.setStartState(ass);
        asm.addElement(ass);

        // Wait for being unblocked
        AvatarStateMachineElement previous = ass;
        for(AvatarSignal as: unblokedBySigs) {
            AvatarActionOnSignal aaosRead = new AvatarActionOnSignal("read__" + as.getSignalName(), as, _refB);
            asm.addElement(aaosRead);
            at = makeAvatarEmptyTransitionBetween(ab, asm, previous, aaosRead, _refB);
            previous = aaosRead;
        }

        // Main step: waiting to be activated
        AvatarState mainState = new AvatarState("Main", _refB);
        asm.addElement(mainState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, previous, mainState, _refB);

        AvatarActionOnSignal selectRead = new AvatarActionOnSignal("read_" + selectP.getSignalName(), selectP, _refB);
        asm.addElement(selectRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, selectRead, _refB);

        // Activated
        AvatarState activatedState = new AvatarState("activatedState", _refB);
        asm.addElement(activatedState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, selectRead, activatedState, _refB);

        // Making a step
        AvatarActionOnSignal stepRead = new AvatarActionOnSignal("read_" + stepP.getSignalName(), stepP, _refB);
        stepRead.addValue("step");
        asm.addElement(stepRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, activatedState, stepRead, _refB);
        at.setGuard("duration>0");
        at = makeAvatarEmptyTransitionBetween(ab, asm, stepRead, activatedState, _refB);
        at.setDelays("2", "2");
        at.addAction("duration = duration - step");

        // Preempted
        AvatarActionOnSignal preemptRead = new AvatarActionOnSignal("read_" + preemptP.getSignalName(), preemptP, _refB);
        asm.addElement(preemptRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, activatedState, preemptRead, _refB);
        at.setGuard("duration>0");
        at = makeAvatarEmptyTransitionBetween(ab, asm, preemptRead, mainState, _refB);

        // Finished!
        AvatarActionOnSignal finishRead = new AvatarActionOnSignal("write_" + finishP.getSignalName(), finishP, _refB);
        asm.addElement(finishRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, activatedState, finishRead, _refB);
        at.setGuard("duration==0");

        // Unblocking next
        AvatarState unblockingState = new AvatarState("unblockingState", _refB);
        asm.addElement(unblockingState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, finishRead, unblockingState, _refB);

        // Adding all unblocking signals
        for(AvatarSignal as: unblokingBySigs) {
            AvatarActionOnSignal aaosWrite = new AvatarActionOnSignal("write__" + as.getSignalName(), as, _refB);
            asm.addElement(aaosWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, unblockingState, aaosWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, aaosWrite, unblockingState, _refB);
        }


        return ab;
    }

    public static AvatarBlock getHWGraphBlock(String _name, AvatarSpecification _avspec, Object _refB,
                                              int duration, Vector<String> unblockedBy, Vector<String> unblockNext) {

        AvatarBlock ab = new AvatarBlock(_name, _avspec, _refB);

        // Create  signals
        AvatarSignal selectP = new AvatarSignal("selectP", AvatarSignal.IN, _refB);
        AvatarSignal stepP = new AvatarSignal("stepP", AvatarSignal.IN, _refB);
        AvatarAttribute att1 = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        stepP.addParameter(att1);
        AvatarSignal deactivatedP = new AvatarSignal("deactivatedP", AvatarSignal.IN, _refB);
        AvatarSignal reactivatedP = new AvatarSignal("reactivatedP", AvatarSignal.IN, _refB);
        AvatarSignal finishP = new AvatarSignal("finishP", AvatarSignal.OUT, _refB);

        ab.addSignal(selectP);
        ab.addSignal(stepP);
        ab.addSignal(deactivatedP);
        ab.addSignal(reactivatedP);
        ab.addSignal(finishP);

        // block / unblock signals

        Vector<AvatarSignal> unblokedBySigs = new Vector<>();
        for(String unblockName: unblockedBy) {
            AvatarSignal sig = new AvatarSignal(unblockName, AvatarSignal.IN, _refB);
            unblokedBySigs.add(sig);
            ab.addSignal(sig);
        }

        Vector<AvatarSignal> unblokingBySigs = new Vector<>();
        for(String unblockName: unblockNext) {
            AvatarSignal sig = new AvatarSignal(unblockName, AvatarSignal.OUT, _refB);
            unblokingBySigs.add(sig);
            ab.addSignal(sig);
        }


        // Create attributes
        AvatarAttribute durationAtt = new AvatarAttribute("duration", AvatarType.INTEGER, ab, _refB);
        durationAtt.setInitialValue(""+duration);
        ab.addAttribute(durationAtt);

        AvatarAttribute stepAtt = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        ab.addAttribute(stepAtt);

        // State machines
        AvatarTransition at;
        AvatarStateMachine asm = ab.getStateMachine();

        // Start state
        AvatarStartState ass = new AvatarStartState("start", _refB);
        asm.setStartState(ass);
        asm.addElement(ass);

        // Wait for being unblocked
        AvatarStateMachineElement previous = ass;
        for(AvatarSignal as: unblokedBySigs) {
            AvatarActionOnSignal aaosRead = new AvatarActionOnSignal("read__" + as.getSignalName(), as, _refB);
            asm.addElement(aaosRead);
            at = makeAvatarEmptyTransitionBetween(ab, asm, previous, aaosRead, _refB);
            previous = aaosRead;
        }

        // Main step: waiting to be activated
        AvatarState mainState = new AvatarState("Main", _refB);
        asm.addElement(mainState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, previous, mainState, _refB);

        AvatarActionOnSignal selectRead = new AvatarActionOnSignal("read_" + selectP.getSignalName(), selectP, _refB);
        asm.addElement(selectRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, selectRead, _refB);


        // Activation / deactivation
        AvatarActionOnSignal deactivateRead = new AvatarActionOnSignal("read_" + deactivatedP.getSignalName(), deactivatedP, _refB);
        asm.addElement(deactivateRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, deactivateRead, _refB);
        AvatarActionOnSignal activateRead = new AvatarActionOnSignal("read_" + reactivatedP.getSignalName(), reactivatedP, _refB);
        asm.addElement(activateRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, deactivateRead, activateRead, _refB);
        at = makeAvatarEmptyTransitionBetween(ab, asm, activateRead, mainState, _refB);


        // Activated
        AvatarState activatedState = new AvatarState("activatedState", _refB);
        asm.addElement(activatedState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, selectRead, activatedState, _refB);

        // Making a step
        AvatarActionOnSignal stepRead = new AvatarActionOnSignal("read_" + stepP.getSignalName(), stepP, _refB);
        stepRead.addValue("step");
        asm.addElement(stepRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, activatedState, stepRead, _refB);
        at.setGuard("duration>0");
        at = makeAvatarEmptyTransitionBetween(ab, asm, stepRead, activatedState, _refB);
        at.setDelays("2", "2");
        at.addAction("duration=duration-step");

        // Finished!
        AvatarActionOnSignal finishRead = new AvatarActionOnSignal("write_" + finishP.getSignalName(), finishP, _refB);
        asm.addElement(finishRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, activatedState, finishRead, _refB);
        at.setGuard("duration==0");

        // Unblocking next
        AvatarState unblockingState = new AvatarState("unblockingState", _refB);
        asm.addElement(unblockingState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, finishRead, unblockingState, _refB);

        // Adding all unblocking signals
        for(AvatarSignal as: unblokingBySigs) {
            AvatarActionOnSignal aaosWrite = new AvatarActionOnSignal("write__" + as.getSignalName(), as, _refB);
            asm.addElement(aaosWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, unblockingState, aaosWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, aaosWrite, unblockingState, _refB);
        }

        return ab;
    }



    public static AvatarBlock getClockGraphBlock(String _name, AvatarSpecification _avspec, Object _refB,
                                              int stepV, String tickS, String allFinishedS) {

        AvatarBlock ab = new AvatarBlock(_name, _avspec, _refB);

        // Create  signals
        AvatarSignal tick = new AvatarSignal(tickS, AvatarSignal.OUT, _refB);
        AvatarAttribute att = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        tick.addParameter(att);
        AvatarSignal allFinished = new AvatarSignal(allFinishedS, AvatarSignal.OUT, _refB);
        AvatarAttribute attH = new AvatarAttribute("h", AvatarType.INTEGER, ab, _refB);
        allFinished.addParameter(attH);

        ab.addSignal(tick);
        ab.addSignal(allFinished);



        // Create attributes
        AvatarAttribute h = new AvatarAttribute("h", AvatarType.INTEGER, ab, _refB);
        h.setInitialValue("" + 0);
        ab.addAttribute(h);

        AvatarAttribute step = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        step.setInitialValue("" + stepV);
        ab.addAttribute(step);

        // State machines
        AvatarTransition at;
        AvatarStateMachine asm = ab.getStateMachine();

        // Start state
        AvatarStartState ass = new AvatarStartState("start", _refB);
        asm.setStartState(ass);
        asm.addElement(ass);

        // Main state
        AvatarState mainState = new AvatarState("mainState", _refB);
        asm.addElement(mainState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, ass, mainState, _refB);


        // Tick
        AvatarActionOnSignal aaosWrite = new AvatarActionOnSignal("sendTickIn_" + tick.getSignalName(), tick, _refB);
        asm.addElement(aaosWrite);
        aaosWrite.addValue("step");
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, aaosWrite, _refB);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaosWrite, mainState, _refB);
        at.addAction("h = h + step");

        // finished
        aaosWrite = new AvatarActionOnSignal("sendAllFinished_" + allFinished.getSignalName(), allFinished, _refB);
        aaosWrite.addValue("h");
        asm.addElement(aaosWrite);
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, aaosWrite, _refB);

        AvatarStopState stop = new AvatarStopState("stop", _refB);
        asm.addElement(stop);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaosWrite, stop, _refB);


        return ab;
    }


    public static AvatarBlock getDRManagerBlock(String _name, AvatarSpecification _avspec, Object _refB) {
        AvatarBlock ab = new AvatarBlock(_name, _avspec, _refB);

        AvatarSignal as;

        AvatarSignal startDR = new AvatarSignal("startDR", AvatarSignal.IN, _refB);
        AvatarSignal stopDR = new AvatarSignal("stopDR", AvatarSignal.IN, _refB);

        ab.addSignal(startDR);
        ab.addSignal(stopDR);


        AvatarTransition at;
        AvatarStateMachine asm = ab.getStateMachine();

        // Start state
        AvatarStartState ass = new AvatarStartState("start", _refB);
        asm.setStartState(ass);
        asm.addElement(ass);

        // MainState state
        AvatarState mainState = new AvatarState("mainState", _refB);
        asm.addElement(mainState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, ass, mainState, _refB);

        // Receive startDR
        AvatarActionOnSignal aaosRead = new AvatarActionOnSignal("startDR", startDR, _refB);
        asm.addElement(aaosRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, aaosRead, _refB);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaosRead, mainState, _refB);

        // Receive stopDR
        aaosRead = new AvatarActionOnSignal("stopDR", stopDR, _refB);
        asm.addElement(aaosRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, mainState, aaosRead, _refB);
        at = makeAvatarEmptyTransitionBetween(ab, asm, aaosRead, mainState, _refB);

        return ab;
    }


    // main block
    public static AvatarBlock getMainGraphBlock(String _name, AvatarSpecification _avspec, Object _refB,
                                                 Vector<String> swTasks, Vector<String> hwTasks, Vector<String> hwSizes,
                                                 String tickS, String allFinishedS, int nbCoresV, int durationDRV,
                                                 int HWSize, int minSize) {

        AvatarBlock ab = new AvatarBlock(_name, _avspec, _refB);

        AvatarSignal as;


        AvatarSignal tick = new AvatarSignal(tickS, AvatarSignal.IN, _refB);
        AvatarAttribute attT = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        tick.addParameter(attT);
        AvatarSignal allFinished = new AvatarSignal(allFinishedS, AvatarSignal.IN, _refB);
        AvatarAttribute att = new AvatarAttribute("h", AvatarType.INTEGER, ab, _refB);
        allFinished.addParameter(att);

        ab.addSignal(tick);
        ab.addSignal(allFinished);

        // Signals for DR
        AvatarSignal startDRSig = new AvatarSignal("startDR", AvatarSignal.OUT, _refB);
        AvatarSignal stopDRSig = new AvatarSignal("stopDR", AvatarSignal.OUT, _refB);

        ab.addSignal(startDRSig);
        ab.addSignal(stopDRSig);

        // Create  signals for SW Tasks
        for(String taskName: swTasks) {
            as = new AvatarSignal("finished_" + taskName, AvatarSignal.IN, _refB);
            ab.addSignal(as);
            as = new AvatarSignal("select_" + taskName, AvatarSignal.OUT, _refB);
            ab.addSignal(as);
            as = new AvatarSignal("preempt_" + taskName, AvatarSignal.OUT, _refB);
            ab.addSignal(as);
            as = new AvatarSignal("step_" + taskName, AvatarSignal.OUT, _refB);
            AvatarAttribute att1 = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
            as.addParameter(att1);
            ab.addSignal(as);
        }

        // Create  signals for hW Tasks
        for(String taskName: hwTasks) {
            as = new AvatarSignal("finished_" + taskName, AvatarSignal.IN, _refB);
            ab.addSignal(as);
            as = new AvatarSignal("select_" + taskName, AvatarSignal.OUT, _refB);
            ab.addSignal(as);
            as = new AvatarSignal("step_" + taskName, AvatarSignal.OUT, _refB);
            AvatarAttribute att1 = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
            as.addParameter(att1);
            ab.addSignal(as);
            as = new AvatarSignal("deactivate_" + taskName, AvatarSignal.OUT, _refB);
            ab.addSignal(as);
            as = new AvatarSignal("reactivate_" + taskName, AvatarSignal.OUT, _refB);
            ab.addSignal(as);

        }


        // Attributes
        AvatarAttribute nbHWTasks = new AvatarAttribute("nbHWTasks", AvatarType.INTEGER, ab, _refB);
        nbHWTasks.setInitialValue("" + hwTasks.size());
        ab.addAttribute(nbHWTasks);

        AvatarAttribute durationDR = new AvatarAttribute("durationDR", AvatarType.INTEGER, ab, _refB);
        durationDR.setInitialValue("" + durationDRV);
        ab.addAttribute(durationDR);

        AvatarAttribute step = new AvatarAttribute("step", AvatarType.INTEGER, ab, _refB);
        ab.addAttribute(step);

        AvatarAttribute nbHW = new AvatarAttribute("nbHW", AvatarType.INTEGER, ab, _refB);
        nbHW.setInitialValue("" + HWSize);
        ab.addAttribute(nbHW);

        AvatarAttribute allocHW = new AvatarAttribute("allocHW", AvatarType.INTEGER, ab, _refB);
        allocHW.setInitialValue("0");
        ab.addAttribute(allocHW);


        AvatarAttribute remainHW = new AvatarAttribute("remainHW", AvatarType.INTEGER, ab, _refB);
        remainHW.setInitialValue("" + HWSize);
        ab.addAttribute(remainHW);

        AvatarAttribute nbCores = new AvatarAttribute("nbCores", AvatarType.INTEGER, ab, _refB);
        nbCores.setInitialValue("" + nbCoresV);
        ab.addAttribute(nbCores);

        AvatarAttribute allocCore = new AvatarAttribute("allocCore", AvatarType.INTEGER, ab, _refB);
        allocCore.setInitialValue("0");
        ab.addAttribute(allocCore);

        AvatarAttribute minSizeAttr = new AvatarAttribute("minHW", AvatarType.INTEGER, ab, _refB);
        minSizeAttr.setInitialValue(""+minSize);
        ab.addAttribute(minSizeAttr);

        AvatarAttribute delayDR = new AvatarAttribute("delayDR", AvatarType.BOOLEAN, ab, _refB);
        delayDR.setInitialValue("false");
        ab.addAttribute(delayDR);


        int cpt = 0;
        for(String size: hwSizes) {
            AvatarAttribute sizeTask = new AvatarAttribute("size_" + hwTasks.get(cpt),
                    AvatarType.INTEGER, ab, _refB);
            sizeTask.setInitialValue(""+size);
            ab.addAttribute(sizeTask);
            cpt ++;
        }

        AvatarAttribute rescheduleSW = new AvatarAttribute("rescheduleSW", AvatarType.BOOLEAN, ab, _refB);
        rescheduleSW.setInitialValue("false");
        ab.addAttribute(rescheduleSW);

        AvatarAttribute runningHW = new AvatarAttribute("runningHW", AvatarType.INTEGER, ab, _refB);
        ab.addAttribute(runningHW);

        AvatarAttribute currentDR = new AvatarAttribute("currentDR", AvatarType.INTEGER, ab, _refB);
        currentDR.setInitialValue("0");
        ab.addAttribute(currentDR);

        AvatarAttribute finalClockValue = new AvatarAttribute("finalClockValue", AvatarType.INTEGER, ab, _refB);
        ab.addAttribute(finalClockValue);


        // State machines
        AvatarTransition at;
        AvatarStateMachine asm = ab.getStateMachine();

        // Start state
        AvatarStartState ass = new AvatarStartState("start", _refB);
        asm.setStartState(ass);
        asm.addElement(ass);

        // finishSW state
        AvatarState finishSW = new AvatarState("finishSW", _refB);
        asm.addElement(finishSW);
        at = makeAvatarEmptyTransitionBetween(ab, asm, ass, finishSW, _refB);

        for(String task: swTasks) {
            as = ab.getAvatarSignalWithName("finished_" + task);
            AvatarActionOnSignal finishedSWRead = new AvatarActionOnSignal("read_" + as.getSignalName() + "_" + task, as, _refB);
            asm.addElement(finishedSWRead);
            at = makeAvatarEmptyTransitionBetween(ab, asm, finishSW, finishedSWRead, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, finishedSWRead, finishSW, _refB);
            at.addAction("allocCore = allocCore-1");
        }

        // finishHW state
        AvatarState finishHW = new AvatarState("finishHW", _refB);
        asm.addElement(finishHW);
        at = makeAvatarEmptyTransitionBetween(ab, asm, finishSW, finishHW, _refB);
        at.setDelays("1", "1");
        at.addAction("rescheduleSW=false");

        cpt = 0;
        for(String task: hwTasks) {
            as = ab.getAvatarSignalWithName("finished_" + task);
            AvatarActionOnSignal finishedHWRead = new AvatarActionOnSignal("read_" + as.getSignalName(), as, _refB);
            asm.addElement(finishedHWRead);
            at = makeAvatarEmptyTransitionBetween(ab, asm, finishHW, finishedHWRead, _refB);
            if (cpt < hwTasks.size() - 1) {
                at = makeAvatarEmptyTransitionBetween(ab, asm, finishedHWRead, finishHW, _refB);
                at.addAction("rescheduleSW = true");
                at.addAction("runningHW = runningHW - 1");
                at.addAction("nbHWTasks = nbHWTasks - 1");

            } else {
                as = allFinished;
                AvatarActionOnSignal allFinishedRead = new AvatarActionOnSignal("read_" + as.getSignalName(), as, _refB);
                allFinishedRead.addValue("finalClockValue");
                asm.addElement(allFinishedRead);

                at = makeAvatarEmptyTransitionBetween(ab, asm, finishedHWRead, allFinishedRead, _refB);
                at.addAction("rescheduleSW = true");
                at.addAction("runningHW = runningHW - 1");
                at.addAction("nbHWTasks = nbHWTasks - 1");

                AvatarStopState stop = new AvatarStopState("stopAllFinished", _refB);
                asm.addElement(stop);
                at = makeAvatarEmptyTransitionBetween(ab, asm, allFinishedRead, stop, _refB);
            }
            cpt ++;

        }

        // evaluateSW state
        AvatarState evaluateSW = new AvatarState("evaluateSW", _refB);
        asm.addElement(evaluateSW);
        at = makeAvatarEmptyTransitionBetween(ab, asm, finishHW, evaluateSW, _refB);
        at.setDelays("1", "1");


        // preemptSW state
        AvatarState preemptSW = new AvatarState("preemptSW", _refB);
        asm.addElement(preemptSW);
        at = makeAvatarEmptyTransitionBetween(ab, asm, evaluateSW, preemptSW, _refB);
        at.setGuard("(allocCore > 0) && (rescheduleSW)");
        at.addAction("allocCore = 0");

        for(String task: swTasks) {
            as = ab.getAvatarSignalWithName("preempt_" + task);
            AvatarActionOnSignal preemptSWWrite = new AvatarActionOnSignal("write_" + as.getSignalName(), as, _refB);
            asm.addElement(preemptSWWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, preemptSW, preemptSWWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, preemptSWWrite, preemptSW, _refB);
        }



        // selectSW state
        AvatarState selectSW = new AvatarState("selectSW", _refB);
        asm.addElement(selectSW);
        at = makeAvatarEmptyTransitionBetween(ab, asm, preemptSW, selectSW, _refB);
        at.setDelays("1", "1");
        at = makeAvatarEmptyTransitionBetween(ab, asm, evaluateSW, selectSW, _refB);
        at.setGuard("else");

        for(String task: swTasks) {
            as = ab.getAvatarSignalWithName("select_" + task);
            AvatarActionOnSignal selectSWWrite = new AvatarActionOnSignal("write_" + as.getSignalName(), as, _refB);
            asm.addElement(selectSWWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, selectSW, selectSWWrite, _refB);
            at.setGuard("allocCore < nbCores");
            at = makeAvatarEmptyTransitionBetween(ab, asm, selectSWWrite, selectSW, _refB);
            at.addAction("allocCore = allocCore + 1");
        }

        // selectHW state
        AvatarState selectHW = new AvatarState("selectHW", _refB);
        asm.addElement(selectHW);
        at = makeAvatarEmptyTransitionBetween(ab, asm, selectSW, selectHW, _refB);
        at.setDelays("2", "2");
        at.setGuard("currentDR == 0");

        cpt = 0;
        for(String task: hwTasks) {
            as = ab.getAvatarSignalWithName("select_" + task);
            AvatarActionOnSignal selectHWWrite = new AvatarActionOnSignal("write_" + as.getSignalName(), as, _refB);
            asm.addElement(selectHWWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, selectHW, selectHWWrite, _refB);
            at.setGuard("remainHW >= " + hwSizes.get(cpt));

            at = makeAvatarEmptyTransitionBetween(ab, asm, selectHWWrite, selectHW, _refB);
            at.addAction("runningHW = runningHW + 1");
            at.addAction("allocHW = allocHW + " + hwSizes.get(cpt));
            at.addAction("remainHW = remainHW - " + hwSizes.get(cpt));
            at.addAction("delayDR = false");
            cpt ++;
        }

        // Delaying DR
        at = makeAvatarEmptyTransitionBetween(ab, asm, selectHW, selectHW, _refB);
        at.setGuard("(runningHW == 0) && ((allocHW > 0) && ((remainHW >= minHW)&&(delayDR == false)))");
        at.addAction("delayDR = true");


        // blockNonSelected state
        AvatarState blockNonSelected = new AvatarState("blockNonSelected", _refB);
        asm.addElement(blockNonSelected);
        at = makeAvatarEmptyTransitionBetween(ab, asm, selectHW, blockNonSelected, _refB);
        at.setGuard("runningHW> 0 ");

        for(String task: hwTasks) {
            as = ab.getAvatarSignalWithName("deactivate_" + task);
            AvatarActionOnSignal deactivateHWWrite = new AvatarActionOnSignal("deactivate_" + as.getSignalName(), as, _refB);
            asm.addElement(deactivateHWWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, blockNonSelected, deactivateHWWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, deactivateHWWrite, blockNonSelected, _refB);
        }

        // startDR state
        AvatarState startDR = new AvatarState("startDR", _refB);
        asm.addElement(startDR);
        at = makeAvatarEmptyTransitionBetween(ab, asm, selectHW, startDR, _refB);
        //at.setGuard("(runningHW == 0) && (allocHW > 0)");
        at.setGuard("(runningHW == 0) && ((allocHW > 0) && (delayDR == false))");
        AvatarActionOnSignal startDRWrite = new AvatarActionOnSignal("startDRWrite", startDRSig, _refB);
        asm.addElement(startDRWrite);
        at = makeAvatarEmptyTransitionBetween(ab, asm, startDR, startDRWrite, _refB);
        at.addAction("currentDR = durationDR");
        at.addAction("allocHW = 0");
        at.addAction("remainHW = nbHW");
        at.addAction("delayDR = true");

        // unblockAllHWP state
        AvatarState unblockAllHWP = new AvatarState("unblockAllHWP", _refB);
        asm.addElement(unblockAllHWP);
        at = makeAvatarEmptyTransitionBetween(ab, asm, startDRWrite, unblockAllHWP, _refB);


        for(String task: hwTasks) {
            as = ab.getAvatarSignalWithName("reactivate_" + task);
            AvatarActionOnSignal reactivateHWWrite = new AvatarActionOnSignal("reactivate_" + as.getSignalName(), as, _refB);
            asm.addElement(reactivateHWWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, unblockAllHWP, reactivateHWWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, reactivateHWWrite, unblockAllHWP, _refB);
        }

        // makeStep state
        AvatarState makeStep = new AvatarState("makeStep", _refB);
        asm.addElement(makeStep);

        at = makeAvatarEmptyTransitionBetween(ab, asm, selectSW, makeStep, _refB);
        at.setGuard("currentDR > 0");
        at.setDelays("2", "2");

        at = makeAvatarEmptyTransitionBetween(ab, asm, selectSW, makeStep, _refB);
        at.setGuard("nbHWTasks == 0");
        at.setDelays("1", "1");


        at = makeAvatarEmptyTransitionBetween(ab, asm, blockNonSelected, makeStep, _refB);
        at.setDelays("1", "1");

        at = makeAvatarEmptyTransitionBetween(ab, asm, selectHW, makeStep, _refB);
        at.setDelays("1", "1");

        at = makeAvatarEmptyTransitionBetween(ab, asm, unblockAllHWP, makeStep, _refB);
        at.setDelays("1", "1");



        // makeSteps state
        AvatarState makeSteps = new AvatarState("makeSteps", _refB);
        asm.addElement(makeSteps);
        AvatarActionOnSignal tickRead = new AvatarActionOnSignal(tick.getSignalName(), tick, _refB);
        tickRead.addValue("step");
        asm.addElement(tickRead);
        at = makeAvatarEmptyTransitionBetween(ab, asm, makeStep, tickRead, _refB);
        at = makeAvatarEmptyTransitionBetween(ab, asm, tickRead, makeSteps, _refB);

        for(String task: swTasks) {
            as = ab.getAvatarSignalWithName("step_" + task);
            AvatarActionOnSignal stepSWWrite = new AvatarActionOnSignal("step_" + as.getSignalName(), as, _refB);
            stepSWWrite.addValue("step");
            asm.addElement(stepSWWrite);
            at = makeAvatarEmptyTransitionBetween(ab, asm, makeSteps, stepSWWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, stepSWWrite, makeSteps, _refB);
        }

        for(String task: hwTasks) {
            as = ab.getAvatarSignalWithName("step_" + task);
            AvatarActionOnSignal stepHWWrite = new AvatarActionOnSignal("step_" + as.getSignalName(), as, _refB);
            asm.addElement(stepHWWrite);
            stepHWWrite.addValue("step");
            at = makeAvatarEmptyTransitionBetween(ab, asm, makeSteps, stepHWWrite, _refB);
            at = makeAvatarEmptyTransitionBetween(ab, asm, stepHWWrite, makeSteps, _refB);
        }


        // nothing is running? Stop now!
        AvatarStopState assDeadlock = new AvatarStopState("deadlockstate", _refB);
        asm.addElement(assDeadlock);
        at = makeAvatarEmptyTransitionBetween(ab, asm, makeSteps, assDeadlock, _refB);
        at.setGuard("(runningHW == 0) && ((allocCore == 0) && (currentDR == 0))");

        // waitForSteps state
        AvatarState waitForSteps = new AvatarState("waitForSteps", _refB);
        asm.addElement(waitForSteps);

        at = makeAvatarEmptyTransitionBetween(ab, asm, makeSteps, waitForSteps, _refB);
        at.setDelays("1", "1");
        at.setGuard("currentDR == 0");

        AvatarState DRAnalysisSState = new AvatarState("DRAnalysisSState", _refB);
        asm.addElement(DRAnalysisSState);
        at = makeAvatarEmptyTransitionBetween(ab, asm, makeSteps, DRAnalysisSState, _refB);
        at.setDelays("1", "1");
        at.setGuard("currentDR > 0");
        at.addAction("currentDR = currentDR - step");

        at = makeAvatarEmptyTransitionBetween(ab, asm, DRAnalysisSState, waitForSteps, _refB);
        at.setGuard("currentDR > 0");

        AvatarActionOnSignal drStopWrite = new AvatarActionOnSignal("drStopWrite", stopDRSig, _refB);
        asm.addElement(drStopWrite);
        at = makeAvatarEmptyTransitionBetween(ab, asm, DRAnalysisSState, drStopWrite, _refB);
        at.setGuard("currentDR == 0");
        at = makeAvatarEmptyTransitionBetween(ab, asm, drStopWrite, waitForSteps, _refB);




        at = makeAvatarEmptyTransitionBetween(ab, asm, waitForSteps, finishSW, _refB);
        at.setDelays("2", "2");

        return ab;
    }

}
