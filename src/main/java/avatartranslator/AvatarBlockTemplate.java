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

    public static AvatarTransition makeAvatarEmptyTransitionBetween(AvatarBlock _block, AvatarStateMachine _asm, AvatarStateMachineElement _elt1, AvatarStateMachineElement _elt2, Object _reference) {
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


    public static AvatarBlock getSWGraphBlock(String _name, AvatarSpecification _avspec, Object _referenceBlock,
                                              int duration, Vector<String> unblocks, Vector<String> blocks) {

        AvatarBlock ab = new AvatarBlock(_name, _avspec, _referenceBlock);

        // Create  signals
        AvatarSignal selectP = new AvatarSignal("selectP", AvatarSignal.IN, _referenceBlock);
        AvatarSignal stepP = new AvatarSignal("stepP", AvatarSignal.IN, _referenceBlock);
        AvatarAttribute att1 = new AvatarAttribute("step", AvatarType.INTEGER, ab, _referenceBlock);
        stepP.addParameter(att1);
        AvatarSignal preemptP = new AvatarSignal("preemptP", AvatarSignal.IN, _referenceBlock);
        AvatarSignal finishP = new AvatarSignal("finishP", AvatarSignal.OUT, _referenceBlock);

        ab.addSignal(selectP);
        ab.addSignal(stepP);
        ab.addSignal(preemptP);
        ab.addSignal(finishP);

        // block / unblock signals

        //for(int)


        // Create attributes
        AvatarAttribute durationAtt = new AvatarAttribute("duration", AvatarType.INTEGER, ab, _referenceBlock);
        durationAtt.setInitialValue(""+duration);
        ab.addAttribute(durationAtt);

        AvatarAttribute stepAtt = new AvatarAttribute("step", AvatarType.INTEGER, ab, _referenceBlock);
        ab.addAttribute(stepAtt);

        // State machines



       return ab;
    }


}
