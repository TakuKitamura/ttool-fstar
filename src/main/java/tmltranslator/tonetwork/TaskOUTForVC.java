
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
 * Class TaskOUTForVC
 * Creation: 09/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 09/01/2019
 */
public class TaskOUTForVC extends TMLTask {

    public TaskOUTForVC(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
        setDaemon(true);
    }

    // Output Channels are given in the order of VCs

    public void generate(Vector<TMLEvent> inPacketEvents, TMLEvent vcSelect,
                         Vector<TMLEvent> outFeedbackEvents, TMLEvent outVCEvent) {

        TMLSendEvent sendEvt;
        TMLWaitEvent waitEvt;
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
        TMLAttribute j = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(j);

        // Events
        addTMLEvent(vcSelect);
        for(TMLEvent evt: inPacketEvents) {
            addTMLEvent(evt);
        }

        addTMLEvent(outVCEvent);
        for(TMLEvent evt: outFeedbackEvents) {
            addTMLEvent(evt);
        }

        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        // Loop forever
        TMLForLoop mainLoop = new TMLForLoop("MainLoop", referenceObject);
        mainLoop.setInfinite(true);
        activity.addLinkElement(start,mainLoop);

        // Select Event
        TMLSelectEvt select = new TMLSelectEvt("selectEvent", referenceObject);
        activity.addLinkElement(mainLoop, select);

        // Branch for each input event
        for(int i=0; i<inPacketEvents.size(); i++) {
            TMLWaitEvent waitPacket = new TMLWaitEvent("WaitForFirstFlit", referenceObject);
            waitPacket.setEvent(inPacketEvents.get(i));
            waitPacket.addParam("pktlen");
            waitPacket.addParam("dstX");
            waitPacket.addParam("dstY");
            waitPacket.addParam("vc");
            waitPacket.addParam("eop");
            waitPacket.addParam("chid");
            activity.addLinkElement(select, waitPacket);

            // loop on packet size
            TMLForLoop packetLoop = new TMLForLoop("packetLoop", referenceObject);
            packetLoop.setInit("j=0");
            packetLoop.setCondition("j<pktlen");
            packetLoop.setIncrement("j=j+1");
            activity.addLinkElement(waitPacket, packetLoop);

            // Inside packetloop
            TMLSendEvent sendFlitEvt = new TMLSendEvent("PacketInfo", referenceObject);
            sendFlitEvt.setEvent(outVCEvent);
            sendFlitEvt.addParam("pktlen");
            sendFlitEvt.addParam("dstX");
            sendFlitEvt.addParam("dstY");
            sendFlitEvt.addParam("vc");
            sendFlitEvt.addParam("eop");
            sendFlitEvt.addParam("chid");
            activity.addLinkElement(packetLoop, sendFlitEvt);

            waitEvt = new TMLWaitEvent("ReturnFromVC", referenceObject);
            waitEvt.setEvent(vcSelect);
            activity.addLinkElement(sendFlitEvt, waitEvt);

            sendEvt = new TMLSendEvent("Feedback", referenceObject);
            sendEvt.setEvent(outFeedbackEvents.get(i));
            activity.addLinkElement(waitEvt, sendEvt);

            TMLChoice choice = new TMLChoice("ChoiceOnEOP", referenceObject);
            activity.addLinkElement(sendEvt, choice);

            // Left branch of choice
            TMLWaitEvent waitNextFlit = new TMLWaitEvent("WaitForNextFlit", referenceObject);
            waitNextFlit.setEvent(inPacketEvents.get(i));
            waitNextFlit.addParam("pktlen");
            waitNextFlit.addParam("dstX");
            waitNextFlit.addParam("dstY");
            waitNextFlit.addParam("vc");
            waitNextFlit.addParam("eop");
            waitNextFlit.addParam("chid");
            activity.addLinkElement(choice, waitNextFlit);
            choice.addGuard("eop == 0");

            TMLStopState stopAfterFlit = new TMLStopState("StopStateLeftChoice", referenceObject);
            activity.addLinkElement(waitNextFlit, stopAfterFlit);

            //Right branch of choice
            TMLStopState stopEOP = new TMLStopState("StopStateRightChoice", referenceObject);
            activity.addLinkElement(choice, stopEOP);
            choice.addGuard("eop > 0");

            // Exit of the loop
            stop = new TMLStopState("EndOfPacketLoop", referenceObject);
            activity.addLinkElement(packetLoop, stop);
        }



    }

}
