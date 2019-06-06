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
 * Class TaskINForVC
 * Creation: 08/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 08/01/2019
 */
public class TaskINForVC extends TMLTask {

    public TaskINForVC(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
    }

    // Output Channels are given in the order of VCs

    public void generate(TMLEvent inPacketEvent, Vector<TMLEvent> inFeedbackEvents, TMLChannel inChannel,
                         TMLEvent outFeedbackEvent, Vector<TMLEvent> outVCEvents, int nocSize, int xPos, int yPos) {

        TMLSendEvent sendEvt;
        TMLStopState stop;

        // Attributes
        TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(pktlen);
        TMLAttribute dstX = new TMLAttribute("dstX", "dstX", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dstX);
        TMLAttribute dstY = new TMLAttribute("dstY", "dstY", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dstY);
        TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(vc);
        TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(eop);
        TMLAttribute chid = new TMLAttribute("chid", "chid", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(chid);
        TMLAttribute feedbackDownstr = new TMLAttribute("feedbackDownstr", "feedbackDownstr", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(feedbackDownstr);
        TMLAttribute bufferSize = new TMLAttribute("bufferSize", "bufferSize", new TMLType(TMLType.NATURAL), "2");
        this.addAttribute(bufferSize);
        TMLAttribute requestedOutput = new TMLAttribute("requestedOutput", "requestedOutput", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(requestedOutput);
        TMLAttribute j = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(j);

        TMLAttribute noc_xsize = new TMLAttribute("noc_xsize", "noc_xsize", new TMLType(TMLType.NATURAL), "" + nocSize);
        this.addAttribute(noc_xsize);

        TMLAttribute noc_ysize = new TMLAttribute("noc_ysize", "noc_ysize", new TMLType(TMLType.NATURAL), "" + nocSize);
        this.addAttribute(noc_ysize);

        TMLAttribute x = new TMLAttribute("x", "x", new TMLType(TMLType.NATURAL), "" + xPos);
        this.addAttribute(x);

        TMLAttribute y = new TMLAttribute("y", "y", new TMLType(TMLType.NATURAL), "" + yPos);
        this.addAttribute(y);

        TMLAttribute xd = new TMLAttribute("xd", "xd", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(xd);

        TMLAttribute yd = new TMLAttribute("yd", "yd", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(yd);

        // Events and channels
        addTMLEvent(inPacketEvent);
        for (TMLEvent evt : inFeedbackEvents) {
            addTMLEvent(evt);
        }
        addReadTMLChannel(inChannel);
        addTMLEvent(outFeedbackEvent);
        for (TMLEvent evt : outVCEvents) {
            addTMLEvent(evt);
        }

        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        // Main Sequence
        TMLSequence seq = new TMLSequence("mainSequence", referenceObject);
        activity.addLinkElement(start, seq);

        // Loop at the left of the sequence
        TMLForLoop loop = new TMLForLoop("mainLoop", referenceObject);
        loop.setInit("j=0");
        loop.setCondition("j<bufferSize");
        loop.setIncrement("j=j+1");
        activity.addElement(loop);
        seq.addNext(loop);

        sendEvt = new TMLSendEvent("outFeedbackEvent", referenceObject);
        sendEvt.setEvent(outFeedbackEvent);
        activity.addElement(sendEvt);
        loop.addNext(sendEvt);

        stop = new TMLStopState("StopStateInLoop", referenceObject);
        activity.addElement(stop);
        sendEvt.addNext(stop);

        stop = new TMLStopState("StopStateAfterLoop", referenceObject);
        activity.addElement(stop);
        loop.addNext(stop);

        // Second activity after sequence
        TMLForLoop loop2 = new TMLForLoop("mainLoop", referenceObject);
        loop2.setInfinite(true);
        activity.addLinkElement(seq, loop2);


        TMLWaitEvent waitEvt = new TMLWaitEvent("PacketEventBeforeSecondSeq", referenceObject);
        waitEvt.setEvent(inPacketEvent);
        waitEvt.addParam("pktlen");
        waitEvt.addParam("dstX");
        waitEvt.addParam("dstY");
        waitEvt.addParam("vc");
        waitEvt.addParam("eop");
        waitEvt.addParam("chid");
        activity.addLinkElement(loop2, waitEvt);

        TMLSequence secondSeq = new TMLSequence("SecondSeq", referenceObject);
        activity.addLinkElement(waitEvt, secondSeq);


        // Routing : first branch of secondSeq

        TMLChoice firstRoutingChoice = new TMLChoice("firstRoutingChoice", referenceObject);
        activity.addLinkElement(secondSeq, firstRoutingChoice);

        TMLActionState requested1 = new TMLActionState("requested1", referenceObject);
        requested1.setAction("requestedOutput = 1");
        activity.addLinkElement(firstRoutingChoice, requested1);
        firstRoutingChoice.addGuard("dstX>x");
        activity.addLinkElement(requested1, new TMLStopState("stopOfRequest1", referenceObject));

        TMLActionState requested0 = new TMLActionState("requested0", referenceObject);
        requested0.setAction("requestedOutput = 0");
        activity.addLinkElement(firstRoutingChoice, requested0);
        firstRoutingChoice.addGuard("dstX<x");
        activity.addLinkElement(requested0, new TMLStopState("stopOfRequest0", referenceObject));

        TMLChoice secondRoutingChoice = new TMLChoice("secondRoutingChoice", referenceObject);
        activity.addLinkElement(firstRoutingChoice, secondRoutingChoice);
        firstRoutingChoice.addGuard("dstX==x");

        TMLActionState requested3 = new TMLActionState("requested3", referenceObject);
        requested3.setAction("requestedOutput = 3");
        activity.addLinkElement(secondRoutingChoice, requested3);
        secondRoutingChoice.addGuard("dstY<y");
        activity.addLinkElement(requested3, new TMLStopState("stopOfRequest3", referenceObject));

        TMLActionState requested4 = new TMLActionState("requested4", referenceObject);
        requested4.setAction("requestedOutput = 4");
        activity.addLinkElement(secondRoutingChoice, requested4);
        secondRoutingChoice.addGuard("dstY==y");
        activity.addLinkElement(requested4, new TMLStopState("stopOfRequest4", referenceObject));

        TMLActionState requested2 = new TMLActionState("requested2", referenceObject);
        requested2.setAction("requestedOutput = 2");
        activity.addLinkElement(secondRoutingChoice, requested2);
        secondRoutingChoice.addGuard("dstY<y");
        activity.addLinkElement(requested2, new TMLStopState("stopOfRequest2", referenceObject));






        // Main choice : second branch of secondSeq
        TMLChoice mainChoice = new TMLChoice("mainChoice", referenceObject);
        activity.addLinkElement(secondSeq, mainChoice);

        // Each link to an output for a given packet
        for (int i = 0; i < outVCEvents.size(); i++) {
            TMLForLoop packetLoop = new TMLForLoop("packetLoop", referenceObject);
            packetLoop.setInit("j=0");
            packetLoop.setCondition("j<pktlen-1");
            packetLoop.setIncrement("j=j+1");
            activity.addLinkElement(mainChoice, packetLoop);
            mainChoice.addGuard("requestedOutput == " + i);

            // Inside packetloop
            sendEvt = new TMLSendEvent("info on packet", referenceObject);
            sendEvt.setEvent(outVCEvents.get(i));
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dstX");
            sendEvt.addParam("dstY");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            sendEvt.addParam("chid");
            activity.addLinkElement(packetLoop, sendEvt);

            waitEvt = new TMLWaitEvent("FeedbackDownEvent", referenceObject);
            waitEvt.setEvent(inFeedbackEvents.get(i));
            activity.addLinkElement(sendEvt, waitEvt);

            sendEvt = new TMLSendEvent("feedbackUpEvent", referenceObject);
            sendEvt.setEvent(outFeedbackEvent);
            activity.addLinkElement(waitEvt, sendEvt);

            TMLReadChannel read = new TMLReadChannel("ReadFlit" + i, referenceObject);
            read.addChannel(inChannel);
            read.setNbOfSamples("1");
            activity.addLinkElement(sendEvt, read);

            waitEvt = new TMLWaitEvent("PacketEventInLoop", referenceObject);
            waitEvt.setEvent(inPacketEvent);
            waitEvt.addParam("pktlen");
            waitEvt.addParam("dstX");
            waitEvt.addParam("dstY");
            waitEvt.addParam("vc");
            waitEvt.addParam("eop");
            waitEvt.addParam("chid");
            activity.addLinkElement(read, waitEvt);

            stop = new TMLStopState("StopStateInLoop", referenceObject);
            activity.addLinkElement(waitEvt, stop);

            // After packetloop
            sendEvt = new TMLSendEvent("infoOnPacketO", referenceObject);
            sendEvt.setEvent(outVCEvents.get(i));
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dstX");
            sendEvt.addParam("dstY");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            sendEvt.addParam("chid");
            activity.addLinkElement(loop, sendEvt);

            waitEvt = new TMLWaitEvent("FeedbackDownEventO", referenceObject);
            waitEvt.setEvent(inFeedbackEvents.get(i));
            activity.addLinkElement(sendEvt, waitEvt);

            sendEvt = new TMLSendEvent("feedbackUpEventO", referenceObject);
            sendEvt.setEvent(outFeedbackEvent);
            activity.addLinkElement(waitEvt, sendEvt);

            read = new TMLReadChannel("ReadFlitO" + i, referenceObject);
            read.addChannel(inChannel);
            read.setNbOfSamples("1");
            activity.addLinkElement(sendEvt, read);

            stop = new TMLStopState("StopStateOutLoop", referenceObject);
            activity.addLinkElement(read, stop);

        }

    }

}
