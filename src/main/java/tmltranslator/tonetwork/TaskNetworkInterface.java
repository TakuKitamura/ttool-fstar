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


package tmltranslator.tonetwork;

import tmltranslator.*;

import java.util.Vector;


/**
 * Class TaskNetworkInterface
 * Creation: 06/03/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 06/03/2019
 */
public class TaskNetworkInterface extends TMLTask {
    protected int nbOfVCs;

    public TaskNetworkInterface(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
    }

    // feedbackEvents: one per vc
    // inputEvt, channels: one per task
    // outputChannel, output event: only one, common: this is a network interface, only one exit!
    public void generate(int nbOfVCs, Vector<TMLEvent> inputFeedbackEvents, Vector<TMLEvent> inputEventsFromMUX,
                         TMLEvent outputEvent, TMLChannel outputChannel) {
        int i;

        this.nbOfVCs = nbOfVCs;


        // Attributes
        //TMLAttribute dst = new TMLAttribute("dst", "dst", new TMLType(TMLType.NATURAL), "0");
        //this.addAttribute(dst);
        TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(vc);
        TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(eop);
        TMLAttribute nEvt = new TMLAttribute("nEvt", "nEvt", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(nEvt);
        TMLAttribute loopExit = new TMLAttribute("loopExit", "loopExit", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(loopExit);
        TMLAttribute higherPrio = new TMLAttribute("higherPrio", "higherPrio", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(higherPrio);
        TMLAttribute feedback = new TMLAttribute("feedback", "feedback", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(feedback);


        //Attributes per tasks
        for (i=0; i<nbOfVCs; i++) {
            TMLAttribute pktlen = new TMLAttribute("pktlen"+i, "pktlen"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(pktlen);
            TMLAttribute iA = new TMLAttribute("i"+i, "i"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(iA);
            TMLAttribute ispkt = new TMLAttribute("ispkt"+i, "ispkt"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(ispkt);
            TMLAttribute eopi = new TMLAttribute("eop"+i, "eop"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(eopi);
            TMLAttribute dst = new TMLAttribute("dst"+i, "dst"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(dst);
            TMLAttribute vci = new TMLAttribute("vc"+i, "vc"+i, new TMLType(TMLType.NATURAL), ""+i);
            this.addAttribute(vci);
        }



        // Events and channels
        addTMLEvent(outputEvent);
        for(TMLEvent evt: inputFeedbackEvents) {
            addTMLEvent(evt);
        }
        for(TMLEvent evt: inputEventsFromMUX) {
            addTMLEvent(evt);
        }
        addWriteTMLChannel(outputChannel);


        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        TMLForLoop loop = new TMLForLoop("mainLoop", referenceObject);
        loop.setInfinite(true);
        addElement(start, loop);

        TMLActionState state = new TMLActionState("LoopExitSetting", referenceObject);
        state.setAction("loopexit = 2");
        addElement(loop, state);

        TMLSequence mainSequence = new TMLSequence("mainSequence", referenceObject);
        addElement(state, mainSequence);

        for(i=0; i<nbOfVCs; i++) {
            TMLChoice testOnLoopExit = null;
            if (i>0) {
                //Test on loopexit
                testOnLoopExit = new TMLChoice("testOnLoopExit", referenceObject);
                addElement(mainSequence, testOnLoopExit);

                // Right branch
                TMLStopState endOfLoopExit = new TMLStopState("endOfLoopExit", referenceObject);
                addElement(testOnLoopExit, endOfLoopExit);
                testOnLoopExit.addGuard("loopexit == 1");

            } else {
                testOnLoopExit = null;
            }

            TMLNotifiedEvent notifiedEvent = new TMLNotifiedEvent("NotifiedVC", referenceObject);
            notifiedEvent.setEvent(inputEventsFromMUX.get(i));
            notifiedEvent.setVariable("nEvt");
            if (testOnLoopExit == null) {
                addElement(mainSequence, notifiedEvent);
            } else {
                addElement(testOnLoopExit, notifiedEvent);
                testOnLoopExit.addGuard("loopexit == 2");
            }

            TMLChoice testingEvt = new TMLChoice("testingEvtVC", referenceObject);
            addElement(notifiedEvent, testingEvt);

            TMLStopState endOfLoopNotified = new TMLStopState("NoEventNorispkt", referenceObject);
            addElement(testingEvt, endOfLoopNotified);
            testingEvt.addGuard("(nEvt == 0) and (ispkt" + i + " == 0)");

            TMLSequence intermediateSeq = new TMLSequence("intermediateSeq", referenceObject);
            addElement(testingEvt, intermediateSeq);
            testingEvt.addGuard("(nEvt > 0) or (ispkt" + i + " > 0)");

            // Choice on the left of intermediate sequence
            TMLChoice ispktChoice = new TMLChoice("ChoiceOnNewPacket", referenceObject);
            addElement(intermediateSeq, ispktChoice);

            TMLStopState endOfIspkt = new TMLStopState("endOfIspkt", referenceObject);
            addElement(testingEvt, endOfIspkt);
            testingEvt.addGuard("(ispkt" + i + " > 0)");

            TMLWaitEvent waitingForStartPacket = new TMLWaitEvent("WaitingStartPacket", referenceObject);
            waitingForStartPacket.setEvent(inputEventsFromMUX.get(i));
            waitingForStartPacket.addParam("pktlen" + i);
            waitingForStartPacket.addParam("dst" + i);
            waitingForStartPacket.addParam("vc");
            waitingForStartPacket.addParam("eop");
            addElement(ispktChoice, waitingForStartPacket);
            ispktChoice.addGuard("ispkt == 0");

            TMLActionState ispktSetting = new TMLActionState("ispktSetting", referenceObject);
            ispktSetting.setAction("ispkt = 1");
            addElement(waitingForStartPacket, ispktSetting);

            TMLActionState iSetting = new TMLActionState("iSetting", referenceObject);
            iSetting.setAction("i" + i + " = 1");
            addElement(ispktSetting, iSetting);

            TMLStopState endOfInitPkt = new TMLStopState("endOfInitPkt", referenceObject);
            addElement(iSetting, endOfInitPkt);

            // Right branch of intermediate seq
            TMLForLoop loopOfRightBranch = new TMLForLoop("LoopOfRightBranch", referenceObject);
            loopOfRightBranch.setInit("loopexit = 0");
            loopOfRightBranch.setCondition("loopexit < 1");
            loopOfRightBranch.setIncrement("loopexit = loopexit");
            addElement(intermediateSeq, loopOfRightBranch);

            TMLNotifiedEvent feedbackNotified = new TMLNotifiedEvent("WaitingForFeedback", referenceObject);
            feedbackNotified.setEvent(inputFeedbackEvents.get(i));
            feedbackNotified.setVariable("feedback");
            addElement(loopOfRightBranch, feedbackNotified);

            // Also adding end of intermediate loop
             TMLStopState endOfIntermediateLoop = new TMLStopState("endOfIntermediateLoop", referenceObject);
            addElement(loopOfRightBranch, endOfIntermediateLoop);

            // Test on feedback
            TMLChoice testOnFeedback = new TMLChoice("TestOnFeedback", referenceObject);
            addElement(feedbackNotified, testOnFeedback);

            // No feedback
            TMLActionState noFeedbackAction = new TMLActionState("noFeedbackAction", referenceObject);
            noFeedbackAction.setAction("loopexit = 2");
            addElement(testOnFeedback, noFeedbackAction);
            testOnFeedback.addGuard("else");

            TMLStopState endOfNoFeedback = new TMLStopState("endOfNoFeedback", referenceObject);
            addElement(noFeedbackAction, endOfNoFeedback);


            // Feedback present
            TMLWriteChannel sendingSample = new TMLWriteChannel("SendingSample", referenceObject);
            sendingSample.addChannel(outputChannel);
            addElement(testOnFeedback, sendingSample);
            testOnFeedback.addGuard("feedback > 0");

            // Waiting for feedback
            TMLWaitEvent waitingForFeedback = new TMLWaitEvent("WaitingForFeedback", referenceObject);
            waitingForFeedback.setEvent(inputFeedbackEvents.get(i));
            addElement(sendingSample, waitingForFeedback);

            TMLChoice packetLengthChoice = new TMLChoice("PacketLengthChoice",referenceObject);
            addElement(waitingForFeedback, packetLengthChoice);

            // Left branch
            TMLSendEvent sendEvtpktin = new TMLSendEvent("SendEvtPktin", referenceObject);
            sendEvtpktin.setEvent(outputEvent);
            sendEvtpktin.addParam("pktlen" + i);
            sendEvtpktin.addParam("dst" + i);
            sendEvtpktin.addParam("vc" + i);
            sendEvtpktin.addParam("0");
            addElement(packetLengthChoice, sendEvtpktin);
            packetLengthChoice.addGuard("i" + i + " < pktlen" + i + " - 1");

            TMLActionState asOnI = new TMLActionState("ActionstateOnI", referenceObject);
            asOnI.setAction("i" + i + " = i" + i + " 1");
            addElement(sendEvtpktin, asOnI);

            TMLStopState endOfLB = new TMLStopState("EndOfLB", referenceObject);
            addElement(asOnI, endOfLB);

            // Middle branch
            TMLActionState loopExitMB = new TMLActionState("loopExitMB", referenceObject);
            loopExitMB.setAction("loopexit = 1");
            addElement(packetLengthChoice, loopExitMB);
            packetLengthChoice.addGuard("i" + i + " == pktlen" + i);

            TMLActionState isPktMB = new TMLActionState("isPktMB", referenceObject);
            isPktMB.setAction("ispkt" + i + " = 0");
            addElement(loopExitMB, isPktMB);

            TMLStopState endOfMB = new TMLStopState("endOfMB", referenceObject);
            addElement(isPktMB, endOfMB);


            // Right branch
            TMLSendEvent sendEvtpktinRB = new TMLSendEvent("sendEvtpktinRB", referenceObject);
            sendEvtpktinRB.setEvent(outputEvent);
            sendEvtpktinRB.addParam("pktlen" + i);
            sendEvtpktinRB.addParam("dst" + i);
            sendEvtpktinRB.addParam("vc" + i);
            sendEvtpktinRB.addParam("1");
            addElement(packetLengthChoice, sendEvtpktinRB);
            packetLengthChoice.addGuard("i" + i + " == pktlen" + i + " - 1");

            TMLActionState asOnIRB = new TMLActionState("asOnIRB", referenceObject);
            asOnI.setAction("i" + i + " = i" + i + " 1");
            addElement(sendEvtpktinRB, asOnIRB);

            TMLStopState endOfRB = new TMLStopState("endOfRB", referenceObject);
            addElement(asOnIRB, endOfRB);



        }



    }

}
