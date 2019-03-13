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
 * Class TaskINForDispatch
 * Creation: 07/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 07/01/2019
 */
public class TaskINForDispatch extends TMLTask {
    protected int nbOfVCs;

    public TaskINForDispatch(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
    }

    // Output Channels are given in the order of VCs
    public void generate(int nbOfVCs, TMLEvent inputEvent, TMLChannel inputChannel,
                         Vector<TMLEvent> outputEvents, Vector<TMLChannel> outputChannels) {

        this.nbOfVCs = nbOfVCs;


        // Attributes
        TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(pktlen);
        TMLAttribute dst = new TMLAttribute("dst", "dst", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dst);
        TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(vc);
        TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(eop);

        // Events and channels
        addTMLEvent(inputEvent);
        for(TMLEvent evt: outputEvents) {
            addTMLEvent(evt);
        }
        addReadTMLChannel(inputChannel);
        for(TMLChannel ch: outputChannels) {
            addWriteTMLChannel(ch);
        }

        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        TMLForLoop loop = new TMLForLoop("mainLoop", referenceObject);
        loop.setInfinite(true);
        activity.addElement(loop);
        start.addNext(loop);

        TMLWaitEvent waitEvt = new TMLWaitEvent("PacketEvent", referenceObject);
        waitEvt.setEvent(inputEvent);
        waitEvt.addParam("pktlen");
        waitEvt.addParam("dst");
        waitEvt.addParam("vc");
        waitEvt.addParam("eop");
        activity.addElement(waitEvt);
        loop.addNext(waitEvt);

        TMLChoice choice = new TMLChoice("MainChoice", referenceObject);
        activity.addElement(choice);

        for(int i=0; i<nbOfVCs; i++) {
            TMLSendEvent sendEvt = new TMLSendEvent("SendEvtToVC" + i, referenceObject);
            sendEvt.setEvent(outputEvents.get(i));
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dst");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            activity.addElement(sendEvt);
            choice.addNext(sendEvt);
            choice.addGuard("vc == " + i);

            TMLReadChannel read = new TMLReadChannel("ReadFlit" + i, referenceObject);
            read.addChannel(inputChannel);
            read.setNbOfSamples("1");
            activity.addElement(read);
            sendEvt.addNext(read);

            TMLWriteChannel write = new TMLWriteChannel("WriteFlit" + i, referenceObject);
            write.addChannel(outputChannels.get(i));
            write.setNbOfSamples("1");
            activity.addElement(write);
            read.addNext(write);

            TMLStopState stopL = new TMLStopState("WriteFlit" + i, referenceObject);
            activity.addElement(stopL);
            write.addNext(stopL);
        }

        // Ending loop
        TMLStopState stop = new TMLStopState("StopState", referenceObject);
        activity.addElement(stop);
        loop.addNext(stop);

    }

}
