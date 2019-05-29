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
        TMLAttribute dst = new TMLAttribute("dst", "dst", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dst);
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

        // Select Event
        TMLSelectEvt select = new TMLSelectEvt("selectEvent", referenceObject);
        activity.addLinkElement(start, select);

        // Branch for each input event
        for(int i=0; i<inPacketEvents.size(); i++) {
            waitEvt = new TMLWaitEvent("PacketEvent", referenceObject);
            waitEvt.setEvent(inPacketEvents.get(i));
            waitEvt.addParam("pktlen");
            waitEvt.addParam("dst");
            waitEvt.addParam("vc");
            waitEvt.addParam("eop");
            waitEvt.addParam("chid");
            activity.addLinkElement(select, waitEvt);

            // loop on packet size
            TMLForLoop packetLoop = new TMLForLoop("packetLoop", referenceObject);
            packetLoop.setInit("j=0");
            packetLoop.setCondition("j<pktlen");
            packetLoop.setIncrement("j=j+1");
            activity.addLinkElement(waitEvt, packetLoop);

            // Inside packetloop
            sendEvt = new TMLSendEvent("PacketInfo", referenceObject);
            sendEvt.setEvent(outVCEvent);
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dst");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            sendEvt.addParam("chid");
            activity.addLinkElement(packetLoop, sendEvt);

            waitEvt = new TMLWaitEvent("ReturnFromVC", referenceObject);
            waitEvt.setEvent(vcSelect);
            activity.addLinkElement(sendEvt, waitEvt);

            sendEvt = new TMLSendEvent("Feedback", referenceObject);
            sendEvt.setEvent(outFeedbackEvents.get(i));
            activity.addLinkElement(waitEvt, sendEvt);

            TMLChoice choice = new TMLChoice("Choice on EOP", referenceObject);
            activity.addLinkElement(sendEvt, choice);

            // Left branch of choice
            waitEvt = new TMLWaitEvent("PacketEvent", referenceObject);
            waitEvt.setEvent(inPacketEvents.get(i));
            waitEvt.addParam("pktlen");
            waitEvt.addParam("dst");
            waitEvt.addParam("vc");
            waitEvt.addParam("eop");
            waitEvt.addParam("chid");
            activity.addLinkElement(choice, waitEvt);
            choice.addGuard("eop == 0");

            stop = new TMLStopState("StopStateLeftChoice", referenceObject);
            activity.addLinkElement(waitEvt, stop);

            //Right branch of choice
            stop = new TMLStopState("StopStateRightChoice", referenceObject);
            activity.addLinkElement(choice, stop);
            choice.addGuard("eop > 0");

            // Exit of the loop
            stop = new TMLStopState("EndOfPacketLoop", referenceObject);
            activity.addLinkElement(packetLoop, stop);
        }



    }

}
