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
        setDaemon(true);
    }

    // feedbackEvents: one per vc
    // inputEvt, channels: one per task
    // outputChannel, output event: only one, common: this is a network interface, only one exit!
    public void generate(int nbOfVCs, Vector<TMLEvent> inputFeedbackEvents, Vector<TMLEvent> inputEventsFromMUX,
                         TMLEvent outputEvent, TMLChannel outputChannel) {
        int i;

        this.nbOfVCs = nbOfVCs;

        for(TMLEvent evt: inputFeedbackEvents) {
            evt.setDestinationTask(this);
        }

        for(TMLEvent evt: inputEventsFromMUX) {
            evt.setDestinationTask(this);
        }

        outputEvent.setOriginTask(this);
        outputChannel.setOriginTask(this);



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
            TMLAttribute dstX = new TMLAttribute("dstX"+i, "dstX"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(dstX);
            TMLAttribute dstY = new TMLAttribute("dstY"+i, "dstY"+i, new TMLType(TMLType.NATURAL), "0");
            this.addAttribute(dstY);
            TMLAttribute vci = new TMLAttribute("vc"+i, "vc"+i, new TMLType(TMLType.NATURAL), ""+i);
            this.addAttribute(vci);
            TMLAttribute chid = new TMLAttribute("chid"+i, "chid"+i, new TMLType(TMLType.NATURAL), ""+i);
            this.addAttribute(chid);
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
        state.setAction("loopExit = 2");
        addElement(loop, state);

        TMLExecI execI = new TMLExecI("ExecI", referenceObject);
        execI.setAction("1");
        addElement(state, execI);


        TMLSequence mainSequence = new TMLSequence("mainSequence", referenceObject);
        addElement(execI, mainSequence);

        for(i=0; i<nbOfVCs; i++) {
            TMLChoice testOnLoopExit = null;
            if (i>0) {
                //Test on loop exit
                testOnLoopExit = new TMLChoice("testOnLoopExit", referenceObject);
                addElement(mainSequence, testOnLoopExit);

                // Right branch
                TMLStopState endOfLoopExit = new TMLStopState("endOfLoopExit", referenceObject);
                addElement(testOnLoopExit, endOfLoopExit);
                testOnLoopExit.addGuard("loopExit == 1");

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
                testOnLoopExit.addGuard("loopExit == 2");
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
            addElement(ispktChoice, endOfIspkt);
            ispktChoice.addGuard("(ispkt" + i + " > 0)");

            TMLWaitEvent waitingForStartPacket = new TMLWaitEvent("WaitingStartPacket", referenceObject);
            waitingForStartPacket.setEvent(inputEventsFromMUX.get(i));
            waitingForStartPacket.addParam("pktlen" + i);
            waitingForStartPacket.addParam("dstX" + i);
            waitingForStartPacket.addParam("dstY" + i);
            waitingForStartPacket.addParam("vc");
            waitingForStartPacket.addParam("eop");
            waitingForStartPacket.addParam("chid" + i);
            addElement(ispktChoice, waitingForStartPacket);
            ispktChoice.addGuard("ispkt" + i + " == 0");

            // Check if packet length is 1 
            TMLChoice pktLength = new TMLChoice("ChoiceOnPacketLength", referenceObject);
            addElement(waitingForStartPacket, pktLength);

            // First branch of pktlen choice: pktlen == 1
            // Send event for the first flit with eop=1
            TMLSendEvent sendEvtSingleFlit = new TMLSendEvent("SendSingleFlit", referenceObject);
            sendEvtSingleFlit.setEvent(outputEvent);
            sendEvtSingleFlit.addParam("pktlen" + i);
            sendEvtSingleFlit.addParam("dstX" + i);
            sendEvtSingleFlit.addParam("dstY" + i);
            sendEvtSingleFlit.addParam("vc" + i);
            sendEvtSingleFlit.addParam("1");
            sendEvtSingleFlit.addParam("chid"+i);
            addElement(pktLength, sendEvtSingleFlit);
            pktLength.addGuard("pktlen" + i + " == 1");

            // Set i to 1
            TMLActionState iSettingSingleFlit = new TMLActionState("iSetting", referenceObject);
            iSettingSingleFlit.setAction("i" + i + " = 1");
            addElement(sendEvtSingleFlit, iSettingSingleFlit);
            // Stop
            TMLStopState endOfSingleFlit = new TMLStopState("endOfSingleFlit", referenceObject);
            addElement(iSettingSingleFlit, endOfSingleFlit);

            // Second branch of pktlen choice: pktlen > 1
            // Send event for the first flit with eop=0
            TMLSendEvent sendEvtFirstFlit = new TMLSendEvent("SendFirstFlit", referenceObject);
            sendEvtFirstFlit.setEvent(outputEvent);
            sendEvtFirstFlit.addParam("pktlen" + i);
            sendEvtFirstFlit.addParam("dstX" + i);
            sendEvtFirstFlit.addParam("dstY" + i);
            sendEvtFirstFlit.addParam("vc" + i);
            sendEvtFirstFlit.addParam("0");
            sendEvtFirstFlit.addParam("chid"+i);
            addElement(pktLength, sendEvtFirstFlit);
            pktLength.addGuard("pktlen" + i + " > 1");

            TMLActionState ispktSetting = new TMLActionState("ispktSetting", referenceObject);
            ispktSetting.setAction("ispkt" + i + " = 1");
            addElement(sendEvtFirstFlit, ispktSetting);

            TMLActionState iSetting = new TMLActionState("iSetting", referenceObject);
            iSetting.setAction("i" + i + " = 1");
            addElement(ispktSetting, iSetting);

            TMLStopState endOfInitPkt = new TMLStopState("endOfInitPkt", referenceObject);
            addElement(iSetting, endOfInitPkt);

            // Right branch of intermediate seq
            TMLForLoop loopOfRightBranch = new TMLForLoop("LoopOfRightBranch", referenceObject);
            loopOfRightBranch.setInit("loopExit = 0");
            loopOfRightBranch.setCondition("loopExit < 1");
            loopOfRightBranch.setIncrement("loopExit = loopExit");
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
            noFeedbackAction.setAction("loopExit = 2");
            addElement(testOnFeedback, noFeedbackAction);
            testOnFeedback.addGuard("else");

            TMLStopState endOfNoFeedback = new TMLStopState("endOfNoFeedback", referenceObject);
            addElement(noFeedbackAction, endOfNoFeedback);


            // Feedback present
            TMLWriteChannel sendingSample = new TMLWriteChannel("SendingSample", referenceObject);
            sendingSample.addChannel(outputChannel);
            sendingSample.setNbOfSamples("1");
            addElement(testOnFeedback, sendingSample);
            testOnFeedback.addGuard("feedback > 0");

            // Waiting for feedback
            TMLWaitEvent waitingForFeedback = new TMLWaitEvent("WaitingForFeedback", referenceObject);
            waitingForFeedback.setEvent(inputFeedbackEvents.get(i));
            addElement(sendingSample, waitingForFeedback);

            if (i == 0) {


                TMLChoice packetLengthChoice = new TMLChoice("PacketLengthChoice", referenceObject);
                addElement(waitingForFeedback, packetLengthChoice);

                // Left branch
                TMLSendEvent sendEvtpktin = new TMLSendEvent("SendEvtPktin", referenceObject);
                sendEvtpktin.setEvent(outputEvent);
                sendEvtpktin.addParam("pktlen" + i);
                sendEvtpktin.addParam("dstX" + i);
                sendEvtpktin.addParam("dstY" + i);
                sendEvtpktin.addParam("vc" + i);
                sendEvtpktin.addParam("0");
                sendEvtpktin.addParam("chid"+i);
                addElement(packetLengthChoice, sendEvtpktin);
                packetLengthChoice.addGuard("i" + i + " < pktlen" + i + " - 1");

                TMLActionState asOnI = new TMLActionState("ActionstateOnI", referenceObject);
                asOnI.setAction("i" + i + " = i" + i + " + 1");
                addElement(sendEvtpktin, asOnI);

                TMLStopState endOfLB = new TMLStopState("EndOfLB", referenceObject);
                addElement(asOnI, endOfLB);

                // Middle branch
                TMLActionState loopExitMB = new TMLActionState("loopExitMB", referenceObject);
                loopExitMB.setAction("loopExit = 1");
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
                sendEvtpktinRB.addParam("dstX" + i);
                sendEvtpktinRB.addParam("dstY" + i);
                sendEvtpktinRB.addParam("vc" + i);
                sendEvtpktinRB.addParam("1");
                sendEvtpktinRB.addParam("chid"+i);
                addElement(packetLengthChoice, sendEvtpktinRB);
                packetLengthChoice.addGuard("i" + i + " == pktlen" + i + " - 1");

                TMLActionState asOnIRB = new TMLActionState("asOnIRB", referenceObject);
                asOnIRB.setAction("i" + i + " = i" + i + "+ 1");
                addElement(sendEvtpktinRB, asOnIRB);

                TMLStopState endOfRB = new TMLStopState("endOfRB", referenceObject);
                addElement(asOnIRB, endOfRB);

            } else {
                
                TMLChoice packetLengthChoice1 = new TMLChoice("PacketLengthChoice1", referenceObject);
                addElement(waitingForFeedback, packetLengthChoice1);

                // Right branch
                TMLActionState loopexitTo1= new TMLActionState("loopexitTo1", referenceObject);
                loopexitTo1.setAction("loopExit = 1");
                addElement(packetLengthChoice1, loopexitTo1);
                packetLengthChoice1.addGuard("i" + i + " == pktlen" + i);


                TMLActionState ispktResetAction = new TMLActionState("ispktResetAction", referenceObject);
                ispktResetAction.setAction("ispkt" + i + " = 0");
                addElement(loopexitTo1, ispktResetAction);

                TMLStopState endOfRB1 = new TMLStopState("endOfRB1", referenceObject);
                addElement(ispktResetAction, endOfRB1);

                // TMLSequence of left branch
                TMLSequence seqLB = new TMLSequence("seqLB", referenceObject);
                addElement(packetLengthChoice1, seqLB);
                packetLengthChoice1.addGuard("i" + i + " < pktlen" + i);

                //LB of seqLB
                TMLChoice choiceLBSeq = new TMLChoice("choiceLBSeq", referenceObject);
                addElement(seqLB, choiceLBSeq);

                TMLSendEvent sendEvtpktin1 = new TMLSendEvent("sendEvtpktin1", referenceObject);
                sendEvtpktin1.setEvent(outputEvent);
                sendEvtpktin1.addParam("pktlen" + i);
                sendEvtpktin1.addParam("dstX" + i);
                sendEvtpktin1.addParam("dstY" + i);
                sendEvtpktin1.addParam("vc" + i);
                sendEvtpktin1.addParam("0");
                sendEvtpktin1.addParam("chid"+i);
                addElement(choiceLBSeq, sendEvtpktin1);
                choiceLBSeq.addGuard("i" + i + " < pktlen" + i + " - 1");

                TMLActionState asOnI1 = new TMLActionState("ActionstateOnI1", referenceObject);
                asOnI1.setAction("i" + i + " = i" + i + " + 1");
                addElement(sendEvtpktin1, asOnI1);

                TMLStopState endOfLB1 = new TMLStopState("endOfLB1", referenceObject);
                addElement(asOnI1, endOfLB1);

                TMLSendEvent sendEvtpktinRB1 = new TMLSendEvent("sendEvtpktinRB1", referenceObject);
                sendEvtpktinRB1.setEvent(outputEvent);
                sendEvtpktinRB1.addParam("pktlen" + i);
                sendEvtpktinRB1.addParam("dstX" + i);
                sendEvtpktinRB1.addParam("dstY" + i);
                sendEvtpktinRB1.addParam("vc" + i);
                sendEvtpktinRB1.addParam("1");
                sendEvtpktinRB1.addParam("chid"+i);
                addElement(choiceLBSeq, sendEvtpktinRB1);
                choiceLBSeq.addGuard("i" + i + " == pktlen" + i + " - 1");

                TMLActionState asOnIRB1 = new TMLActionState("asOnIRB1", referenceObject);
                asOnIRB1.setAction("i" + i + " = i" + i + "+ 1");
                addElement(sendEvtpktinRB1, asOnIRB1);

                TMLStopState endOfRB1SEQ = new TMLStopState("endOfRB1SEQ", referenceObject);
                addElement(asOnIRB1, endOfRB1SEQ);

                // Right branch of seq
                TMLNotifiedEvent notifiedEventPrio = new TMLNotifiedEvent("notifiedEventPrio", referenceObject);
                notifiedEventPrio.setEvent(inputEventsFromMUX.get(i-1));
                notifiedEventPrio.setVariable("higherPrio");
                addElement(seqLB, notifiedEventPrio);

                TMLChoice testPrio = new TMLChoice("testPrio", referenceObject);
                addElement(notifiedEventPrio, testPrio);

                TMLActionState loopexitPrio= new TMLActionState("loopexitPrio", referenceObject);
                loopexitPrio.setAction("loopExit = 1");
                addElement(testPrio, loopexitPrio);
                testPrio.addGuard("higherPrio > 0");

                addElement(loopexitPrio, new TMLStopState("EndOfHigherPrio", referenceObject));

                addElement(testPrio, new TMLStopState("NoHigherPrio", referenceObject));
                testPrio.addGuard("higherPrio == 0");

            }
        }
    }

}
