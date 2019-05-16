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

import myutil.TraceManager;
import tmltranslator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * Class TaskINForVC
 * Creation: 17/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 17/01/2019
 */
public class TranslatedRouter<E> {


    private final int NB_OF_PORTS = 5;
    private final int CHANNEL_SIZE = 4;
    private final int CHANNEL_MAX = 8;

    private int nbOfVCs, xPos, yPos, nbOfApps;

    private TMAP2Network<?> main;

    private HwNoC noc;
    private List<TMLChannel> channelsViaNoc;

    private Vector<TMLEvent> pktins;
    private Vector<TMLTask> dispatchers;

    private TMLMapping<?> tmlmap;


    // Events and channels with other routers

    // Between  IN and INVC
    TMLEvent[][] pktInEvtsVCs; // position, vc
    TMLChannel[][] pktInChsVCs; // position, vc

    // Between INVC and OUTVC
    TMLEvent[][][] routeEvtVCs; // Task, vc, destination id
    TMLEvent[][][] routeEvtVCsFeedback; // Task, vc, destination id

    // Between OUTVC and OUT
    TMLEvent[][] evtOutVCs; // position, vc
    TMLEvent[][] evtSelectVC; // position, vc


    // Links to other routers
    public Link[] toNextRouters;
    public Link[] fromPreviousRouters;

    // OUt of the NoC


    public TranslatedRouter(TMAP2Network<?> main, TMLMapping<?> tmlmap, HwNoC noc, List<TMLChannel> channelsViaNoc, int nbOfVCs, int xPos, int yPos) {
        this.main = main;
        this.nbOfVCs = nbOfVCs;
        this.noc = noc;
        this.channelsViaNoc = channelsViaNoc;
        this.xPos = xPos;
        this.yPos = yPos;
        this.tmlmap = tmlmap;

        toNextRouters = new Link[NB_OF_PORTS];
        fromPreviousRouters = new Link[NB_OF_PORTS];
    }

    public void setLinkFromPreviousRouter(int index, Link l) {
        fromPreviousRouters[index] = l;
    }

    public void setLinkToNextRouter(int index, Link l) {
        toNextRouters[index] = l;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }


    private String getInfo() {
        return "__R" + xPos + "_" + yPos;
    }

    public void makeRouter() {
        int i, j;
        TMLTask t;
        TMLModeling tmlm = tmlmap.getTMLModeling();
        HwExecutionNode execNode = null;


        // MUX for the different writing tasks
        // For each writing channel of the corresponding CPU, we need MUX to be created.
        // We first get the corresponding CPU
        String nameOfExecNode = noc.getHwExecutionNode(xPos, yPos);
        if (nameOfExecNode == null) {
            nameOfExecNode = "fakeCPU_" + xPos + "_" + yPos;
        } else {
            execNode = tmlmap.getTMLArchitecture().getHwExecutionNodeByName(nameOfExecNode);

            if (execNode == null) {
                TraceManager.addDev("Could NOT find an exec node for (" + xPos + "," + yPos + ")");
            } else {
                TraceManager.addDev("Found an exec node for (" + xPos + "," + yPos + "): " + execNode.getName());
            }
        }

        // Then, we need to find the channels starting from/arriving to a task mapped on this execNode
        Vector<TMLChannel> inputChannels = new Vector<>();
        Vector<TMLChannel> outputChannels = new Vector<>();
        if (execNode != null) {
            for (TMLChannel ch : channelsViaNoc) {
                TMLTask origin = ch.getOriginTask();
                TMLTask destination = ch.getDestinationTask();

                if (origin != null) {
                    // find on which CPU is mapped this task
                    HwNode cpuOfOrigin = tmlmap.getHwNodeOf(origin);
                    if (cpuOfOrigin == execNode) {
                        TraceManager.addDev("Found an output channel:" + ch.getName());
                        outputChannels.add(ch);
                    }
                }

                if (destination != null) {
                    // find on which CPU is mapped this task
                    HwNode cpuOfDestination = tmlmap.getHwNodeOf(destination);
                    if (cpuOfDestination == execNode) {
                        TraceManager.addDev("Found an input channel:" + ch.getName());
                        inputChannels.add(ch);
                    }
                }
            }
        }


        // We can create the MUX task: one mux task for each VC
        Vector<TaskMUXAppDispatch> muxTasks = new Vector<>();
        for (i = 0; i < nbOfVCs; i++) {
            // Now that we know all channels, we can generate the MUX tasks
            // We need one event par outputChannel
            HashMap<TMLChannel, TMLEvent> mapOfOutputChannels = new HashMap<>();
            Vector<TMLEvent> inputEventsOfMUX = new Vector<>();
            for (TMLChannel chan : outputChannels) {
                if (chan.getVC() == i) {
                    TMLEvent outputEventOfMux = new TMLEvent("EventMUXof" + chan.getName(), null, 8,
                            true);
                    mapOfOutputChannels.put(chan, outputEventOfMux);
                    inputEventsOfMUX.add(outputEventOfMux);
                    tmlm.addEvent(outputEventOfMux);
                }
            }

            // We also need an output event for MUX / NI_IN
            TMLEvent eventForMUX_and_NI_IN = new TMLEvent("EventBetweenMUXandNI_IN_for_" + nameOfExecNode,
                    null, 8, true);
            tmlm.addEvent(eventForMUX_and_NI_IN);

            TaskMUXAppDispatch muxTask = new TaskMUXAppDispatch("MUXof" + nameOfExecNode + "_VC" + i, null, null);
            tmlm.addTask(muxTask);
            muxTask.generate(inputEventsOfMUX, eventForMUX_and_NI_IN);
            muxTasks.add(muxTask);
        }


        // Finally, we need to modify the src apps with the new event, and modifying the channel in order to write into
        // the corresponding local memory


        // NETWORK INTERFACE IN
        // We must first gathers events from must task
        Vector<TMLEvent> inputEventsFromMUX = new Vector<>();
        for (TaskMUXAppDispatch tmux : muxTasks) {
            inputEventsFromMUX.add(tmux.getOutputEvent());
        }

        TaskNetworkInterface tniIn = new TaskNetworkInterface("NI_IN_" + nameOfExecNode, null,
                null);
        tmlm.addTask(tniIn);

        // One TMLEvent for feedback for each VC
        Vector<TMLEvent> feedbackEventsNIINs = new Vector<>();
        for (i = 0; i < nbOfVCs; i++) {
            TMLEvent eventFeedback = new TMLEvent("EventBetweenNI_IN_ANd_IN_for_" + nameOfExecNode,
                    null, 8, true);
            feedbackEventsNIINs.add(eventFeedback);
            tmlm.addEvent(eventFeedback);
        }

        TMLEvent outputFromNIINtoIN = new TMLEvent("EventBetweenNI_IN_to_IN_for_" + nameOfExecNode,
                null, 8, true);
        tmlm.addEvent(outputFromNIINtoIN);
        TMLChannel outputChannelFromNIINtoIN = new TMLChannel("channelBetweenNI_IN_to_IN_for_" + nameOfExecNode,
                null);
        outputChannelFromNIINtoIN.setSize(4);
        outputChannelFromNIINtoIN.setMax(8);
        outputChannelFromNIINtoIN.setOriginTask(tniIn);
        tmlm.addChannel(outputChannelFromNIINtoIN);

        tniIn.generate(nbOfVCs, feedbackEventsNIINs, inputEventsFromMUX, outputFromNIINtoIN, outputChannelFromNIINtoIN);


        // IN NOC - One for each input
        // We need one output channel per VC and one output event per VC
        // A task only create the output, never the input

        HashMap<Integer, TaskINForDispatch> dispatchIns = new HashMap<>();

        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (fromPreviousRouters[portNb] != null) {

                TaskINForDispatch inDispatch = new TaskINForDispatch("IN_" + nameOfExecNode, null,
                        null);
                tmlm.addTask(inDispatch);
                Vector<TMLEvent> listOfOutEvents = new Vector<TMLEvent>();
                Vector<TMLChannel> listOfOutChannels = new Vector<TMLChannel>();
                for (int vcN = 0; vcN < nbOfVCs; vcN++) {
                    TMLEvent evt = pktInEvtsVCs[portNb][vcN];
                    listOfOutEvents.add(evt);
                    evt.setOriginTask(inDispatch);

                    TMLChannel ch = pktInChsVCs[portNb][vcN];
                    ch.setOriginTask(inDispatch);
                    listOfOutChannels.add(ch);
                }
                if (portNb == NB_OF_PORTS) { // From Network Interface?

                    inDispatch.generate(nbOfVCs, outputFromNIINtoIN, outputChannelFromNIINtoIN, listOfOutEvents,
                            listOfOutChannels);
                    outputFromNIINtoIN.setDestinationTask(inDispatch);
                    outputChannelFromNIINtoIN.setDestinationTask(inDispatch);
                } else {
                    // We have to use events / channels coming from another router
                    TMLEvent toInEvt = fromPreviousRouters[portNb].packetOut;
                    toInEvt.setDestinationTask(inDispatch);
                    TMLChannel toInCh = fromPreviousRouters[portNb].chOutToIN;
                    toInCh.setDestinationTask(inDispatch);

                    inDispatch.generate(nbOfVCs, toInEvt, toInCh, listOfOutEvents,
                            listOfOutChannels);

                }
                dispatchIns.put(new Integer(portNb), inDispatch);

            }
        }

        // IN VC
        TaskINForVC[][] dispatchInVCs = new TaskINForVC[NB_OF_PORTS][nbOfVCs];
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (fromPreviousRouters[portNb] != null) {
                for (int vcNb = 0; vcNb < nbOfVCs; vcNb++) {

                    TaskINForVC taskINForVC = new TaskINForVC("INVC_" + nameOfExecNode + "___" + portNb + "_" + vcNb, null,
                            null);
                    tmlm.addTask(taskINForVC);
                    dispatchInVCs[portNb][vcNb] = taskINForVC;

                    pktInEvtsVCs[portNb][vcNb].setDestinationTask(taskINForVC);

                    Vector<TMLEvent> inFeedbacks = new Vector<>();
                    for (int k = 0; k < TMAP2Network.DOMAIN + 1; k++) {
                        inFeedbacks.add(routeEvtVCsFeedback[portNb][vcNb][k]);
                        routeEvtVCsFeedback[portNb][vcNb][k].setDestinationTask(taskINForVC);
                    }

                    TMLChannel inChannel = pktInChsVCs[portNb][vcNb];
                    inChannel.setDestinationTask(taskINForVC);

                    Vector<TMLEvent> listOfOutVCEvents = new Vector<TMLEvent>();
                    for (int dom = 0; dom < NB_OF_PORTS; dom++) {
                        TMLEvent evt = routeEvtVCs[portNb][vcNb][dom];
                        listOfOutVCEvents.add(evt);
                        evt.setOriginTask(taskINForVC);
                    }

                    TMLEvent feedback = fromPreviousRouters[portNb].feedbackPerVC[vcNb];
                    feedback.setOriginTask(taskINForVC);

                    taskINForVC.generate(pktInEvtsVCs[portNb][vcNb], inFeedbacks, inChannel,
                            feedback, listOfOutVCEvents, noc.size, xPos, yPos);


                }
            }
        }

        // OUT VC
        TaskOUTForVC[][] dispatchOutVCs = new TaskOUTForVC[NB_OF_PORTS][nbOfVCs];
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            for (int vcNb = 0; vcNb < nbOfVCs; vcNb++) {
                TranslatedRouter routerToConnectWith = main.getRouterFrom(xPos, yPos, portNb);
                if (routerToConnectWith != null) {
                    if (TMAP2Network.hasRouterAt(xPos, yPos, portNb, noc.size)) {
                        TaskOUTForVC taskOUTForVC = new TaskOUTForVC("OUTVC_" + nameOfExecNode + "_" + vcNb, null,
                                null);
                        tmlm.addTask(taskOUTForVC);
                        dispatchOutVCs[portNb][vcNb] = taskOUTForVC;

                        Vector<TMLEvent> inPackets = new Vector<>();
                        Vector<TMLEvent> outFeedbacks = new Vector<>();
                        for (int k = 0; k < TMAP2Network.DOMAIN + 1; k++) {
                            inPackets.add(routeEvtVCs[portNb][vcNb][k]);
                            routeEvtVCs[portNb][vcNb][k].setDestinationTask(taskOUTForVC);

                            outFeedbacks.add(routeEvtVCsFeedback[portNb][vcNb][k]);
                            routeEvtVCsFeedback[portNb][vcNb][k].setOriginTask(taskOUTForVC);

                        }

                        TMLEvent vcSelect = evtSelectVC[portNb][vcNb];
                        vcSelect.setDestinationTask(taskOUTForVC);

                        TMLEvent outVCEvt = evtOutVCs[portNb][vcNb];
                        outVCEvt.setDestinationTask(taskOUTForVC);

                        taskOUTForVC.generate(inPackets, vcSelect, outFeedbacks, outVCEvt);
                    }
                }
            }
        }

        // OUT NOC - One for each output of the considered router
        // We need one output channel for each exit and one output event per VC
        HashMap<Integer, TaskOUTForDispatch> dispatchOuts = new HashMap<>();
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (toNextRouters[portNb] != null) {

                TaskOUTForDispatch outDispatch = new TaskOUTForDispatch("OUT_" + nameOfExecNode, null,
                        null);
                tmlm.addTask(outDispatch);


                Vector<TMLEvent> inPacketEvents = new Vector<TMLEvent>();
                Vector<TMLEvent> inFeedbackEvents = new Vector<TMLEvent>();
                Vector<TMLEvent> outSelectEvents = new Vector<TMLEvent>();
                TMLEvent outPktEvent;
                TMLChannel outPkt;

                for (int nvc = 0; nvc < nbOfVCs; nvc++) {
                    inPacketEvents.add(evtOutVCs[portNb][nvc]);
                    evtOutVCs[portNb][nvc].setDestinationTask(outDispatch);
                    outSelectEvents.add(evtSelectVC[portNb][nvc]);
                    evtSelectVC[portNb][nvc].setOriginTask(outDispatch);
                    inFeedbackEvents.add(toNextRouters[portNb].feedbackPerVC[nvc]);
                    toNextRouters[portNb].feedbackPerVC[nvc].setDestinationTask(outDispatch);
                }

                outPktEvent = toNextRouters[portNb].packetOut;
                outPktEvent.setOriginTask(outDispatch);
                outPkt = toNextRouters[portNb].chOutToIN;
                outPkt.setOriginTask(outDispatch);

                outDispatch.generate(inPacketEvents, inFeedbackEvents, outSelectEvents, outPktEvent, outPkt);

                dispatchOuts.put(new Integer(portNb), outDispatch);

            }
        }


        //NetworkInterfaceOUT (1 per router)
        TaskNetworkInterfaceOUT tniOut = new TaskNetworkInterfaceOUT("NI_OUT_" + nameOfExecNode, null,
                null);
        tmlm.addTask(tniOut);

        outputFromNIINtoIN.setOriginTask(tniOut);

        TMLChannel chOfOut = toNextRouters[NB_OF_PORTS-1].chOutToIN;
        chOfOut.setDestinationTask(tniOut);

        Vector<TMLEvent> feedbackPerVC = new Vector<TMLEvent>();
        for(i=0; i<nbOfVCs; i++) {
            feedbackPerVC.add(toNextRouters[NB_OF_PORTS-1].feedbackPerVC[i]);
            toNextRouters[NB_OF_PORTS-1].feedbackPerVC[i].setOriginTask(tniOut);
        }

        TMLEvent pktout = toNextRouters[NB_OF_PORTS-1].packetOut;
        pktout.setDestinationTask(tniOut);


        tniOut.generate(nbOfVCs, feedbackPerVC, pktout, outputFromNIINtoIN, chOfOut);

    }


    // TO DO: Adding PARAMS to events
    public void makeOutputEventsChannels() {
        TMLModeling tmlm = tmlmap.getTMLModeling();


        // Internal events and channels

        // Between IN and INVC



        pktInEvtsVCs = new TMLEvent[NB_OF_PORTS][nbOfVCs];
        pktInChsVCs = new TMLChannel[NB_OF_PORTS][nbOfVCs];

        for (int i = 0; i < TMAP2Network.DOMAIN + 1; i++) {
            for (int j = 0; j < nbOfVCs; j++) {
                pktInEvtsVCs[i][j] = new TMLEvent("evt_pktin" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                        null, 8, true);
                tmlm.addEvent(pktInEvtsVCs[i][j]);

                pktInChsVCs[i][j] = new TMLChannel("ch_pktin" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                        null);
                pktInChsVCs[i][j].setSize(4);
                pktInChsVCs[i][j].setMax(8);
                tmlm.addChannel(pktInChsVCs[i][j]);
            }
        }

        // Between INVC and OUTVC
        routeEvtVCs = new TMLEvent[TMAP2Network.DOMAIN + 1][nbOfVCs][TMAP2Network.DOMAIN + 1];
        routeEvtVCsFeedback = new TMLEvent[TMAP2Network.DOMAIN + 1][nbOfVCs][TMAP2Network.DOMAIN + 1];
        for (int i = 0; i < TMAP2Network.DOMAIN + 1; i++) {
            for (int j = 0; j < nbOfVCs; j++) {
                for (int k = 0; k < TMAP2Network.DOMAIN + 1; k++) {
                    routeEvtVCs[i][j][k] = new TMLEvent("evtroute_" + i + "_vc" + j + "_" + k + "_" +
                            xPos + "_" + yPos, null, 8, true);
                    tmlm.addEvent(routeEvtVCs[i][j][k]);
                    routeEvtVCsFeedback[i][j][k] = new TMLEvent("evtfeedback_" + i + "_vc" + j + "_" + k + "_" +
                            xPos + "_" + yPos, null, 8, true);
                    tmlm.addEvent(routeEvtVCsFeedback[i][j][k]);
                }
            }
        }


        // Between OUTVC and OUT
        evtOutVCs = new TMLEvent[TMAP2Network.DOMAIN + 1][nbOfVCs];
        evtSelectVC = new TMLEvent[TMAP2Network.DOMAIN + 1][nbOfVCs];
        for (int i = 0; i < TMAP2Network.DOMAIN + 1; i++) {
            for (int j = 0; j < nbOfVCs; j++) {
                evtOutVCs[i][j] = new TMLEvent("evt_out" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                        null, 8, true);
                tmlm.addEvent(evtOutVCs[i][j]);
                evtSelectVC[i][j] = new TMLEvent("evt_vcselect" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                        null, 8, true);
                tmlm.addEvent(evtSelectVC[i][j]);
            }
        }

        // Must create the internal links
        //network in
        Link networkInterfaceIn = new Link(tmlm, this, this, nbOfVCs);
        fromPreviousRouters[NB_OF_PORTS-1] = networkInterfaceIn;
        //network out
        Link networkInterfaceOut = new Link(tmlm, this, this, nbOfVCs);
        toNextRouters[NB_OF_PORTS-1] = networkInterfaceOut;



    }


}
