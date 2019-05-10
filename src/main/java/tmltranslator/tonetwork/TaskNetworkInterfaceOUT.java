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
 * Class TaskNetworkInterfaceOUT
 * Creation: 07/05/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 07/05/2019
 */
public class TaskNetworkInterfaceOUT extends TMLTask {
    protected int nbOfVCs;

    public TaskNetworkInterfaceOUT(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
    }

    // feedbackEvents: one per vc
    // inputEvt, channels: one per task
    // outputChannel, output event: only one, common: this is a network interface, only one exit!
    public void generate(int nbOfVCs, Vector<TMLEvent> outputFeedbackEvents, TMLEvent packetOutFromOUT,
                         TMLEvent packetAvailable, TMLChannel outputChannelFromOUT) {
        int i;

        this.nbOfVCs = nbOfVCs;

        // Attributes
        TMLAttribute dst = new TMLAttribute("dst", "dst", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dst);
        TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(pktlen);
        TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(vc);
        TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(eop);
        TMLAttribute memSpace = new TMLAttribute("memSpace", "memSpace", new TMLType(TMLType.NATURAL), "4096");
        this.addAttribute(memSpace);


        // Events and channels
        for(TMLEvent evt: outputFeedbackEvents) {
            addTMLEvent(evt);
        }
        addTMLEvent(packetOutFromOUT);
        addTMLEvent(packetAvailable);

        addReadTMLChannel(outputChannelFromOUT);


        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        //Main Sequence
        TMLSequence mainSequence = new TMLSequence("mainSequence", referenceObject);
        addElement(start, mainSequence);

        // Left branch of left sequence
        // For each VC, send a feedback
        TMLActivityElement previous = mainSequence;
        for(i=0; i<nbOfVCs; i++) {
            TMLSendEvent sendEvtFeedback = new TMLSendEvent("sendEvtFeedback_VC" + i, referenceObject);
            sendEvtFeedback.setEvent(outputFeedbackEvents.get(i));
            addElement(previous, sendEvtFeedback);
            previous = sendEvtFeedback;
        }

        TMLStopState stopOfLeftBranch = new TMLStopState("stopStateOfLeftBranchOfMainSequence", referenceObject);
        addElement(previous, stopOfLeftBranch);


        // Right branch of main sequence
        //
        TMLForLoop loop = new TMLForLoop("mainLoop", referenceObject);
        loop.setInfinite(true);
        addElement(mainSequence, loop);

        // Waiting for a packet from OUT
        TMLWaitEvent waitingForPacketFromOUT = new TMLWaitEvent("waitingForPacketFromOUT", referenceObject);
        waitingForPacketFromOUT.setEvent(packetOutFromOUT);
        waitingForPacketFromOUT.addParam("pktlen");
        waitingForPacketFromOUT.addParam("dst");
        waitingForPacketFromOUT.addParam("vc");
        waitingForPacketFromOUT.addParam("eop");
        addElement(loop, waitingForPacketFromOUT);

        // Reading on channel
        TMLReadChannel readFromOUT = new TMLReadChannel("readFromOUT", referenceObject);
        readFromOUT.setNbOfSamples("1");
        readFromOUT.addChannel(outputChannelFromOUT);
        addElement(waitingForPacketFromOUT, readFromOUT);

        // subsequence
        TMLSequence internalSeq = new TMLSequence("internalSeq", referenceObject);
        addElement(readFromOUT, internalSeq);

        // Left branch: test on eop
        TMLChoice testingEOP = new TMLChoice("testingEOP", referenceObject);
        addElement(internalSeq, testingEOP);

        // Left branch of choice: eop==1
        TMLSendEvent sendEvtPktAvailable = new TMLSendEvent("sendEvtPktAvailable", referenceObject);
        sendEvtPktAvailable.setEvent(packetAvailable);
        sendEvtPktAvailable.addParam("pktlen");
        sendEvtPktAvailable.addParam("dst");
        sendEvtPktAvailable.addParam("vc");
        sendEvtPktAvailable.addParam("eop");
        addElement(testingEOP, sendEvtPktAvailable);
        testingEOP.addGuard("eop == 1");

        TMLStopState stopOfLeftBranchOfChoice = new TMLStopState("stopOfLeftBranchOfChoice", referenceObject);
        addElement(sendEvtPktAvailable, stopOfLeftBranchOfChoice);

        // Right branch of choice
        TMLStopState stopOfRightBranchOfChoice = new TMLStopState("stopOfRightBranchOfChoice", referenceObject);
        addElement(testingEOP, stopOfRightBranchOfChoice);
        testingEOP.addGuard("not(eop == 1)");

        // Right branch of internal seg
        // Test on vc
        TMLChoice testingVC = new TMLChoice("testingVC", referenceObject);
        addElement(internalSeq, testingVC);

        for(i=0; i<nbOfVCs; i++) {
            TMLSendEvent sendEvtFeedback = new TMLSendEvent("sendEvtFeedback_VC" + i, referenceObject);
            sendEvtFeedback.setEvent(outputFeedbackEvents.get(i));
            addElement(testingVC, sendEvtFeedback);
            testingEOP.addGuard("vc == " + i);

            TMLStopState stopVC = new TMLStopState("stopVC" + i, referenceObject);
            addElement(sendEvtFeedback, stopVC);
        }




    }

}
