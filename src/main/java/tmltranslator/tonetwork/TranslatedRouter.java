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

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;



/**
 * Class TranslatedRouter
 * Creation: 17/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 17/01/2019
 */
public class TranslatedRouter<E> {

    private final int NB_OF_PORTS = 5;
    private final int CHANNEL_SIZE = 4;
    private final int CHANNEL_MAX = 8;

    private int nbOfVCs, xPos, yPos;
    private HwExecutionNode myHwExecutionNode;

    private TMAP2Network<?> main;

    private HwNoC noc;
    private List<TMLChannel> channelsViaNoc;


    private TMLMapping<?> tmlmap;


    // Events and channels with other routers

    // Between  IN and INVC
    private TMLEvent[][] pktInEvtsVCs; // position, vc
    private TMLChannel[][] pktInChsVCs; // position, vc

    // Between INVC and OUTVC
    private TMLEvent[][][] routeEvtVCs; // Task, vc, destination id
    private TMLEvent[][][] routeEvtVCsFeedback; // Task, vc, destination id

    // Between OUTVC and OUT
    private TMLEvent[][] evtOutVCs; // position, vc
    private TMLEvent[][] evtSelectVC; // position, vc


    // Links to other routers
    public Link[] playingTheRoleOfPrevious;
    public Link[] playingTheRoleOfNext;

    // All my tasks
    private Vector<TMLTask> allTasks;

    private Vector<TaskMUXAppDispatch> muxTasks;
    private TaskNetworkInterface tniIn;
    private HashMap<Integer, TaskINForDispatch> dispatchIns;
    private TaskINForVC[][] dispatchInVCs;
    private TaskOUTForVC[][] dispatchOutVCs;
    private HashMap<Integer, TaskOUTForDispatch> dispatchOuts;
    private TaskNetworkInterfaceOUT tniOut;
    private FakeTaskOut fto;


    // Connection channels and events
    private HashMap<TMLChannel, TMLEvent> mapOfAllOutputChannels; // Channels going to the NoC
    private HashMap<TMLChannel, TMLEvent> mapOfAllInputChannels; // Channels getting out of the NoC
    private Vector<TMLChannel> handledChannels;


    public TranslatedRouter(TMAP2Network<?> main, TMLMapping<?> tmlmap, HwNoC noc, List<TMLChannel> channelsViaNoc,
                            int nbOfVCs, int xPos, int yPos, HwExecutionNode myHwExecutionNode) {
        this.main = main;
        this.nbOfVCs = nbOfVCs;
        this.noc = noc;
        this.channelsViaNoc = channelsViaNoc;
        this.xPos = xPos;
        this.yPos = yPos;
        this.tmlmap = tmlmap;
        this.myHwExecutionNode = myHwExecutionNode;

        playingTheRoleOfPrevious = new Link[NB_OF_PORTS];
        playingTheRoleOfNext = new Link[NB_OF_PORTS];

        allTasks = new Vector<>();
        handledChannels = new Vector<>();
    }

    public void setLinkFromPreviousRouter(int index, Link l) {
        playingTheRoleOfNext[index] = l;
    }

    public void setLinkToNextRouter(int index, Link l) {
        playingTheRoleOfPrevious[index] = l;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public HwExecutionNode getHwExecutionNode() {
        return myHwExecutionNode;
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
        /*String nameOfExecNode = noc.getHwExecutionNode(xPos, yPos);
        if (nameOfExecNode == null) {
            nameOfExecNode = "fakeCPU_" + xPos + "_" + yPos;
        } else {
            execNode = tmlmap.getTMLArchitecture().getHwExecutionNodeByName(nameOfExecNode);

            if (execNode == null) {
                TraceManager.addDev("Could NOT find an exec node for (" + xPos + "," + yPos + ")");
            } else {
                TraceManager.addDev("Found an exec node for (" + xPos + "," + yPos + "): " + execNode.getName());
            }
        }*/
        execNode = myHwExecutionNode;
        String nameOfExecNode = execNode.getName();

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
        muxTasks = new Vector<>();
        mapOfAllOutputChannels = new HashMap<>();
        for (i = 0; i < nbOfVCs; i++) {
            // Now that we know all channels, we can generate the MUX tasks
            // We need one event par outputChannel
            Vector<TMLEvent> inputEventsOfMUX = new Vector<>();
            for (TMLChannel chan : outputChannels) {
                //TraceManager.addDev("Output Channel:" + chan.getName() + " VC=" + chan.getVC());
                if (chan.getVC() == i) {
                    TMLEvent outputEventOfMux = new TMLEvent("EventMUXof__" + chan.getName(), null, 8,
                            true);
                    // PARAMS!!!!!
                    outputEventOfMux.setOriginTask(chan.getOriginTask());
                    outputEventOfMux.addParam(new TMLType(TMLType.NATURAL));
                    outputEventOfMux.addParam(new TMLType(TMLType.NATURAL));
                    outputEventOfMux.addParam(new TMLType(TMLType.NATURAL));
                    outputEventOfMux.addParam(new TMLType(TMLType.NATURAL));
                    outputEventOfMux.addParam(new TMLType(TMLType.NATURAL));
                    outputEventOfMux.addParam(new TMLType(TMLType.NATURAL));

                    //mapOfOutputChannels.put(chan, outputEventOfMux);
                    mapOfAllOutputChannels.put(chan, outputEventOfMux);
                    inputEventsOfMUX.add(outputEventOfMux);
                    tmlm.addEvent(outputEventOfMux);
                }
            }

            // We also need an output event for MUX / NI_IN
            TMLEvent eventForMUX_and_NI_IN = new TMLEvent("EventBetweenMUXandNI_IN_for_" + nameOfExecNode + "_" + i,
                    null, 8, true);
            tmlm.addEvent(eventForMUX_and_NI_IN);
            eventForMUX_and_NI_IN.addParam(new TMLType(TMLType.NATURAL));
            eventForMUX_and_NI_IN.addParam(new TMLType(TMLType.NATURAL));
            eventForMUX_and_NI_IN.addParam(new TMLType(TMLType.NATURAL));
            eventForMUX_and_NI_IN.addParam(new TMLType(TMLType.NATURAL));
            eventForMUX_and_NI_IN.addParam(new TMLType(TMLType.NATURAL));
            eventForMUX_and_NI_IN.addParam(new TMLType(TMLType.NATURAL));

            TaskMUXAppDispatch muxTask = new TaskMUXAppDispatch("MUXof" + nameOfExecNode + "_VC" + i, null, null);
            tmlm.addTask(muxTask);
            muxTask.generate(inputEventsOfMUX, eventForMUX_and_NI_IN);
            muxTasks.add(muxTask);
            allTasks.add(muxTask);
        }


        // Finally, we need to modify the src apps with the new event, and modifying the channel in order to write into
        // the corresponding local memory


        // NETWORK INTERFACE IN - Internal domain
        // We must first gathers events from must task
        Vector<TMLEvent> inputEventsFromMUX = new Vector<>();
        for (TaskMUXAppDispatch tmux : muxTasks) {
            inputEventsFromMUX.add(tmux.getOutputEvent());
        }

        tniIn = new TaskNetworkInterface("NI_IN_" + nameOfExecNode, null,
                null);
        tmlm.addTask(tniIn);
        allTasks.add(tniIn);

        Vector<TMLEvent> feedbackEventsNIINs = new Vector<>();
        for (i = 0; i < nbOfVCs; i++) {
            feedbackEventsNIINs.add(playingTheRoleOfNext[NB_OF_PORTS-1].feedbackPerVC[i]);
            (playingTheRoleOfNext[NB_OF_PORTS-1].feedbackPerVC[i]).setDestinationTask(tniIn);
        }

        TMLEvent outputFromNIINtoIN = playingTheRoleOfNext[NB_OF_PORTS-1].packetOut;
        outputFromNIINtoIN.setOriginTask(tniIn);

        TMLChannel outputChannelFromNIINtoIN = playingTheRoleOfNext[NB_OF_PORTS-1].chOutToIN;
        outputChannelFromNIINtoIN.setOriginTask(tniIn);

        tniIn.generate(nbOfVCs, feedbackEventsNIINs, inputEventsFromMUX, outputFromNIINtoIN, outputChannelFromNIINtoIN);


        // IN NOC - One for each input
        // We need one output channel per VC and one output event per VC

        dispatchIns = new HashMap<>();
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (playingTheRoleOfNext[portNb] != null) {
                TaskINForDispatch inDispatch = new TaskINForDispatch("IN_" + nameOfExecNode + "_" + portNb +
                        "_x" + xPos + "_y" + yPos, null,
                        null);
                tmlm.addTask(inDispatch);
                Vector<TMLEvent> listOfOutEvents = new Vector<TMLEvent>();
                Vector<TMLChannel> listOfOutChannels = new Vector<TMLChannel>();
                for (int vcN = 0; vcN < nbOfVCs; vcN++) {
                    TMLEvent evt = pktInEvtsVCs[portNb][vcN];
                    //tmlm.addEvent(evt);
                    listOfOutEvents.add(evt);
                    evt.setOriginTask(inDispatch);

                    TMLChannel ch = pktInChsVCs[portNb][vcN];
                    ch.setOriginTask(inDispatch);
                    //tmlm.addChannel(ch);
                    listOfOutChannels.add(ch);
                }
                if (portNb == NB_OF_PORTS-1) { // From Network Interface?
                    inDispatch.generate(nbOfVCs, outputFromNIINtoIN, outputChannelFromNIINtoIN, listOfOutEvents,
                            listOfOutChannels);
                    outputFromNIINtoIN.setDestinationTask(inDispatch);
                    outputChannelFromNIINtoIN.setDestinationTask(inDispatch);
                } else {
                    // We have to use events / channels coming from another router
                    TMLEvent toInEvt = playingTheRoleOfNext[portNb].packetOut;
                    toInEvt.setDestinationTask(inDispatch);
                    //tmlm.addEvent(toInEvt);
                    TMLChannel toInCh = playingTheRoleOfNext[portNb].chOutToIN;
                    toInCh.setDestinationTask(inDispatch);
                    tmlm.addChannel(toInCh);

                    inDispatch.generate(nbOfVCs, toInEvt, toInCh, listOfOutEvents,
                            listOfOutChannels);

                }
                dispatchIns.put(new Integer(portNb), inDispatch);
                allTasks.add(inDispatch);
            }
        }

        // IN VC
        dispatchInVCs = new TaskINForVC[NB_OF_PORTS][nbOfVCs];
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (playingTheRoleOfNext[portNb] != null) {
                for (int vcNb = 0; vcNb < nbOfVCs; vcNb++) {

                    TaskINForVC taskINForVC = new TaskINForVC("INVC_" + nameOfExecNode + "___" + portNb + "_" + vcNb, null,
                            null);
                    tmlm.addTask(taskINForVC);
                    allTasks.add(taskINForVC);
                    dispatchInVCs[portNb][vcNb] = taskINForVC;

                    pktInEvtsVCs[portNb][vcNb].setDestinationTask(taskINForVC);

                    Vector<TMLEvent> inFeedbacks = new Vector<>();
                    for (int k = 0; k < NB_OF_PORTS; k++) {
                        if (playingTheRoleOfPrevious[k] != null) {
                            inFeedbacks.add(routeEvtVCsFeedback[portNb][vcNb][k]);
                            routeEvtVCsFeedback[portNb][vcNb][k].setDestinationTask(taskINForVC);
                        }
                    }

                    TMLChannel inChannel = pktInChsVCs[portNb][vcNb];
                    inChannel.setDestinationTask(taskINForVC);

                    Vector<TMLEvent> listOfOutVCEvents = new Vector<TMLEvent>();
                    Vector<Integer> listOfIndexes = new Vector<>();
                    for (int dom = 0; dom < NB_OF_PORTS; dom++) {
                        if (playingTheRoleOfPrevious[dom] != null) {
                            TMLEvent evt = routeEvtVCs[portNb][vcNb][dom];
                            listOfOutVCEvents.add(evt);
                            listOfIndexes.add(new Integer(dom));
                            evt.setOriginTask(taskINForVC);
                        }
                    }

                    TMLEvent feedback = playingTheRoleOfNext[portNb].feedbackPerVC[vcNb];
                    feedback.setOriginTask(taskINForVC);

                    taskINForVC.generate(pktInEvtsVCs[portNb][vcNb], inFeedbacks, inChannel,
                            feedback, listOfOutVCEvents, listOfIndexes, noc.size, xPos, yPos);


                }
            }
        }

        // OUT VC
        dispatchOutVCs = new TaskOUTForVC[NB_OF_PORTS][nbOfVCs];
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (playingTheRoleOfPrevious[portNb] != null) {
                //TraceManager.addDev("I have a router after me at port =" + portNb);
                for (int vcNb = 0; vcNb < nbOfVCs; vcNb++) {

                        TaskOUTForVC taskOUTForVC = new TaskOUTForVC("OUTVC_" + nameOfExecNode + "__" + portNb + "_" + vcNb, null,
                                null);
                        tmlm.addTask(taskOUTForVC);
                        allTasks.add(taskOUTForVC);
                        dispatchOutVCs[portNb][vcNb] = taskOUTForVC;

                        Vector<TMLEvent> inPackets = new Vector<>();
                        Vector<TMLEvent> outFeedbacks = new Vector<>();
                        for (int k = 0; k < NB_OF_PORTS; k++) {
                            if ((playingTheRoleOfNext[k] != null) || (k == NB_OF_PORTS - 1)) {
                                inPackets.add(routeEvtVCs[k][vcNb][portNb]);
                                routeEvtVCs[k][vcNb][portNb].setDestinationTask(taskOUTForVC);

                                outFeedbacks.add(routeEvtVCsFeedback[k][vcNb][portNb]);
                                routeEvtVCsFeedback[k][vcNb][portNb].setOriginTask(taskOUTForVC);
                            }

                        }

                        //TraceManager.addDev("xPos=" + xPos + " yPos=" + yPos + " portNb=" + portNb + " vnNb=" + vcNb);
                        TMLEvent vcSelect = evtSelectVC[portNb][vcNb];
                        vcSelect.setDestinationTask(taskOUTForVC);

                        TMLEvent outVCEvt = evtOutVCs[portNb][vcNb];
                        outVCEvt.setOriginTask(taskOUTForVC);

                        taskOUTForVC.generate(inPackets, vcSelect, outFeedbacks, outVCEvt);

                }
            }
        }

        // OUT NOC - One for each output of the considered router
        // We need one output channel for each exit and one output event per VC
        dispatchOuts = new HashMap<>();
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (playingTheRoleOfPrevious[portNb] != null) {

                TaskOUTForDispatch outDispatch = new TaskOUTForDispatch("OUT_" + xPos + "_" + yPos + "_" + portNb, null,
                        null);
                tmlm.addTask(outDispatch);
                allTasks.add(outDispatch);

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
                    inFeedbackEvents.add(playingTheRoleOfPrevious[portNb].feedbackPerVC[nvc]);
                    playingTheRoleOfPrevious[portNb].feedbackPerVC[nvc].setDestinationTask(outDispatch);
                }

                outPktEvent = playingTheRoleOfPrevious[portNb].packetOut;
                outPktEvent.setOriginTask(outDispatch);
                outPkt = playingTheRoleOfPrevious[portNb].chOutToIN;
                outPkt.setOriginTask(outDispatch);

                outDispatch.generate(inPacketEvents, inFeedbackEvents, outSelectEvents, outPktEvent, outPkt);

                dispatchOuts.put(new Integer(portNb), outDispatch);

            } else {
                // We need to use a fake out
                /*EmptyTMLTask emptyTask = new EmptyTMLTask("EmptyTaskOfOutput_" + portNb + "_OfRouter_" + xPos + "_" + yPos, null, null);
                Vector<TMLEvent> inPacketEvents = new Vector<TMLEvent>();
                Vector<TMLEvent> outSelectEvents = new Vector<TMLEvent>();
                TMLEvent outPktEvent;
                TMLChannel outPkt;
                allTasks.add(emptyTask);

                for (int nvc = 0; nvc < nbOfVCs; nvc++) {
                    inPacketEvents.add(evtOutVCs[portNb][nvc]);
                    evtOutVCs[portNb][nvc].setDestinationTask(emptyTask);
                    outSelectEvents.add(evtSelectVC[portNb][nvc]);
                    evtSelectVC[portNb][nvc].setOriginTask(emptyTask);
                }

                emptyTask.generate();*/
            }
        }


        //NetworkInterfaceOUT (1 per router)
        tniOut = new TaskNetworkInterfaceOUT("NI_OUT_" + nameOfExecNode, null,
                null);
        tmlm.addTask(tniOut);
        allTasks.add(tniOut);

        TMLChannel chOfOut = playingTheRoleOfPrevious[NB_OF_PORTS-1].chOutToIN;
        chOfOut.setDestinationTask(tniOut);

        Vector<TMLEvent> feedbackPerVC = new Vector<TMLEvent>();
        for(i=0; i<nbOfVCs; i++) {
            feedbackPerVC.add(playingTheRoleOfPrevious[NB_OF_PORTS-1].feedbackPerVC[i]);
            playingTheRoleOfPrevious[NB_OF_PORTS-1].feedbackPerVC[i].setOriginTask(tniOut);
        }

        TMLEvent pktoutFromOut = playingTheRoleOfPrevious[NB_OF_PORTS-1].packetOut;
        pktoutFromOut.setDestinationTask(tniOut);

        tniOut.generate(nbOfVCs, feedbackPerVC, pktoutFromOut, chOfOut);

    }


    // TO DO: Adding PARAMS to events
    public void makeOutputEventsChannels() {
        TMLModeling tmlm = tmlmap.getTMLModeling();

        // Internal events and channels

        // Between IN and INVC



        pktInEvtsVCs = new TMLEvent[NB_OF_PORTS][nbOfVCs];
        pktInChsVCs = new TMLChannel[NB_OF_PORTS][nbOfVCs];

        for (int i = 0; i < NB_OF_PORTS; i++) {
            if ((playingTheRoleOfNext[i] != null) || (i == NB_OF_PORTS - 1)) {
                for (int j = 0; j < nbOfVCs; j++) {
                    pktInEvtsVCs[i][j] = new TMLEvent("evt_pktin" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                            null, 8, true);
                    tmlm.addEvent(pktInEvtsVCs[i][j]);
                    pktInEvtsVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    pktInEvtsVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    pktInEvtsVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    pktInEvtsVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    pktInEvtsVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    pktInEvtsVCs[i][j].addParam(new TMLType(TMLType.NATURAL));

                    pktInChsVCs[i][j] = new TMLChannel("ch_pktin" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                            null);
                    pktInChsVCs[i][j].setSize(4);
                    pktInChsVCs[i][j].setMax(8);
                    tmlm.addChannel(pktInChsVCs[i][j]);
                }
            }
        }

        // Between INVC and OUTVC
        routeEvtVCs = new TMLEvent[NB_OF_PORTS][nbOfVCs][NB_OF_PORTS];
        routeEvtVCsFeedback = new TMLEvent[NB_OF_PORTS][nbOfVCs][NB_OF_PORTS];
        for (int i = 0; i < NB_OF_PORTS; i++) {
            if ((playingTheRoleOfNext[i] != null) || (i == NB_OF_PORTS - 1)) {
                for (int j = 0; j < nbOfVCs; j++) {
                    for (int k = 0; k < NB_OF_PORTS; k++) {
                        if ((playingTheRoleOfPrevious[k] != null) || (k == NB_OF_PORTS - 1)) {
                            routeEvtVCs[i][j][k] = new TMLEvent("evtroute_" + i + "_vc" + j + "_" + k + "_" +
                                    xPos + "_" + yPos, null, 8, true);
                            routeEvtVCs[i][j][k].addParam(new TMLType(TMLType.NATURAL));
                            routeEvtVCs[i][j][k].addParam(new TMLType(TMLType.NATURAL));
                            routeEvtVCs[i][j][k].addParam(new TMLType(TMLType.NATURAL));
                            routeEvtVCs[i][j][k].addParam(new TMLType(TMLType.NATURAL));
                            routeEvtVCs[i][j][k].addParam(new TMLType(TMLType.NATURAL));
                            routeEvtVCs[i][j][k].addParam(new TMLType(TMLType.NATURAL));
                            tmlm.addEvent(routeEvtVCs[i][j][k]);
                            routeEvtVCsFeedback[i][j][k] = new TMLEvent("evtfeedback_" + i + "_vc" + j + "_" + k + "_" +
                                    xPos + "_" + yPos, null, 8, true);
                            tmlm.addEvent(routeEvtVCsFeedback[i][j][k]);
                        }
                    }
                }
            }
        }


        // Between OUTVC and OUT
        evtOutVCs = new TMLEvent[NB_OF_PORTS][nbOfVCs];
        evtSelectVC = new TMLEvent[NB_OF_PORTS][nbOfVCs];
        for (int i = 0; i < NB_OF_PORTS; i++) {
            if ((playingTheRoleOfPrevious[i] != null) || (i == NB_OF_PORTS - 1)) {
                for (int j = 0; j < nbOfVCs; j++) {
                    evtOutVCs[i][j] = new TMLEvent("evt_out" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                            null, 8, true);
                    evtOutVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    evtOutVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    evtOutVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    evtOutVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    evtOutVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    evtOutVCs[i][j].addParam(new TMLType(TMLType.NATURAL));
                    tmlm.addEvent(evtOutVCs[i][j]);
                    evtSelectVC[i][j] = new TMLEvent("evt_vcselect" + i + "_vc" + j + "_" + xPos + "_" + yPos,
                            null, 8, true);
                    tmlm.addEvent(evtSelectVC[i][j]);
                }
            } else {
                //TraceManager.addDev("xPos=" + xPos + " yPos=" + yPos + " is not playing the role of previous for port=" + i);
            }
        }

        // Must create the internal links
        // Network in: from apps to router
        Link networkInterfaceIn = new Link(tmlm, this, this, nbOfVCs, "INRouter");
        playingTheRoleOfNext[NB_OF_PORTS-1] = networkInterfaceIn;

        // Network out: from router to apps
        Link networkInterfaceOut = new Link(tmlm, this, this, nbOfVCs, "OUTRouter");
        playingTheRoleOfPrevious[NB_OF_PORTS-1] = networkInterfaceOut;
    }

    public String getPositionNaming() {
        return "_x" + xPos + "_y" + yPos;
    }

    public String toString() {
        String ret = "Router at " + xPos + " " + yPos + "\n";
        //Printing internal tasks;
        for(TMLTask t: allTasks) {
            ret += "\t Task " + t.getName() + "\n";
        }

        // Connections to other routers
        for(int i = 0; i< playingTheRoleOfPrevious.length; i++) {
            ret += "\tOutput port " + TMAP2Network.PORT_NAME[i] + " is ";
            if (playingTheRoleOfPrevious[i] == null) {
                ret += " NO ROUTER";
            } else {
                ret += playingTheRoleOfPrevious[i].getNaming();
            }
            ret += "\n";
        }
        for(int i = 0; i< playingTheRoleOfNext.length; i++) {
            ret += "\tInput port " + TMAP2Network.PORT_NAME[i] + " is ";
            if (playingTheRoleOfNext[i] == null) {
                ret += " NO ROUTER";
            } else {
                ret += playingTheRoleOfNext[i].getNaming();
            }
            ret += "\n";
        }
        return ret;
    }



    public void makeHwArchitectureAndMapping(HwExecutionNode execNode, HwBus busToInternalDomain) {
        TMLArchitecture tmla = tmlmap.getTMLArchitecture();

        int i, j, k;

        // We first need a bridge for the internal domain
        if (busToInternalDomain == null) {
            TraceManager.addDev("NULL bus");
        }

        HwBridge mainBridge = new HwBridge("BridgeInternal_" + getPositionNaming());
        tmla.addHwNode(mainBridge);
        tmla.makeHwLink(busToInternalDomain, mainBridge);


        // NIIN bus
        HwBus busNIIN = new HwBus("BusNetworkInterfaceIN" + getPositionNaming());
        tmla.addHwNode(busNIIN);

        // For each VC, we create a bus and a cpu. The bus connects to the main bridge
        for (i = 0; i < nbOfVCs; i++) {
            HwCPU cpu = new HwCPU("CPUForMUX_VC" + i + getPositionNaming());
            tmla.addHwNode(cpu);
            tmlmap.addTaskToHwExecutionNode(muxTasks.get(i), cpu);

            HwBus bus = new HwBus("BusForMUX_VC" + i + getPositionNaming());
            tmla.addHwNode(bus);

            tmla.makeHwLink(bus, mainBridge);
            tmla.makeHwLink(busNIIN, cpu);
        }

        // Network interface IN common to all MUX VCs
        // Processor, mem, bus and bridge
        HwCPU cpuNIIN = new HwCPU("CPUNetworkInterfaceIN" + getPositionNaming());
        tmla.addHwNode(cpuNIIN);
        tmlmap.addTaskToHwExecutionNode(tniIn, cpuNIIN);

        HwMemory memNIIN = new HwMemory("MemNetworkInterfaceIN" + getPositionNaming());
        tmla.addHwNode(memNIIN);
        tmlmap.addCommToHwCommNode(playingTheRoleOfNext[NB_OF_PORTS - 1].chOutToIN, memNIIN);

        HwBridge bridgeNIIN = new HwBridge("BridgeNetworkInterfaceIN" + getPositionNaming());
        tmla.addHwNode(bridgeNIIN);

        tmla.makeHwLink(busNIIN, cpuNIIN);
        tmla.makeHwLink(busNIIN, memNIIN);
        tmla.makeHwLink(busNIIN, bridgeNIIN);


        // IN and INVC
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (playingTheRoleOfNext[portNb] != null) {
                // We have an IN on that port. Connects on the bus of the corresponding link
                HwCPU cpuIN = new HwCPU("cpuIN_" + portNb + getPositionNaming());
                tmla.addHwNode(cpuIN);
                tmlmap.addTaskToHwExecutionNode(dispatchIns.get(portNb), cpuIN);

                // connection to the right bus
                if (portNb < NB_OF_PORTS - 1) {
                    // external
                    tmla.makeHwLink(playingTheRoleOfNext[portNb].busBetweenRouters, cpuIN);

                    HwMemory memIN = new HwMemory("mem_IN" + portNb + getPositionNaming());
                    tmla.addHwNode(memIN);
                    tmla.makeHwLink(playingTheRoleOfNext[portNb].busBetweenRouters, memIN);
                    tmlmap.addCommToHwCommNode(playingTheRoleOfNext[portNb].chOutToIN, memIN);

                } else {
                    // internal
                    HwBus busInternalIN = new HwBus("BusInternalIN" + getPositionNaming());
                    tmla.addHwNode((busInternalIN));
                    tmla.makeHwLink(busInternalIN, bridgeNIIN);
                    tmla.makeHwLink(busInternalIN, cpuIN);
                }

                // For each IN VC, we do the Hw Arch: bus, cpu, mem
                for (i = 0; i < nbOfVCs; i++) {
                    HwCPU cpuINVC = new HwCPU("cpuINVC_" + portNb + "_" + i + getPositionNaming());
                    tmla.addHwNode(cpuINVC);
                    tmlmap.addTaskToHwExecutionNode(dispatchInVCs[portNb][i], cpuINVC);
                    HwMemory memINVC = new HwMemory("memINVC" + portNb + "_" + i + getPositionNaming());
                    tmla.addHwNode(memINVC);
                    tmlmap.addCommToHwCommNode(pktInChsVCs[portNb][i], memINVC);
                    HwBus busINVC = new HwBus("busINVC_p" + portNb + "_vc" + i + getPositionNaming());
                    tmla.addHwNode(busINVC);
                    tmla.makeHwLink(busINVC, cpuINVC);
                    tmla.makeHwLink(busINVC, memINVC);
                    tmla.makeHwLink(busINVC, cpuIN);
                }
            }
        }

        //HwBridge bridgeNIOUT = new HwBridge("BridgeNetworkiInterfaceOUT" + getPositionNaming());
        HwCPU outForExit = null;

        // OUTVC and OUT
        for (int portNb = 0; portNb < NB_OF_PORTS; portNb++) {
            if (playingTheRoleOfPrevious[portNb] != null) {
                // We have an IN on that port. Connects on the bus of the corresponding link
                HwCPU cpuOUT = new HwCPU("cpuOUT_" + portNb + getPositionNaming());
                tmla.addHwNode(cpuOUT);
                tmlmap.addTaskToHwExecutionNode(dispatchOuts.get(portNb), cpuOUT);

                // connection to the right bus
                if (portNb < NB_OF_PORTS - 1) {
                    // external
                    tmla.makeHwLink(playingTheRoleOfPrevious[portNb].busBetweenRouters, cpuOUT);
                } else {
                    // internal
                    outForExit = cpuOUT;
                   // HwBus busInternalOUT = new HwBus("BusInternalOUT" + getPositionNaming());
                    //tmla.addHwNode((busInternalOUT));

                    //tmla.makeHwLink(busInternalOUT, bridgeNIOUT);
                    //tmla.makeHwLink(busInternalOUT, cpuOUT);
                }

                // For each IN VC, we do the Hw Arch: bus, cpu, mem
                for (i = 0; i < nbOfVCs; i++) {
                    HwCPU cpuOUTVC = new HwCPU("cpuOUTVC_" + portNb + "_" + i + getPositionNaming());
                    tmla.addHwNode(cpuOUTVC);
                    tmlmap.addTaskToHwExecutionNode(dispatchOutVCs[portNb][i], cpuOUTVC);
                    HwMemory memOUTVC = new HwMemory("memOUTVC" + portNb + "_" + i + getPositionNaming());
                    tmla.addHwNode(memOUTVC);
                    HwBus busOUTVC = new HwBus("busINVC" + portNb + "_" + i + getPositionNaming());
                    tmla.addHwNode(busOUTVC);
                    tmla.makeHwLink(busOUTVC, cpuOUTVC);
                    tmla.makeHwLink(busOUTVC, memOUTVC);
                    tmla.makeHwLink(busOUTVC, cpuOUT);
                }
            }
        }

        // Network interface out
        // Basically connects to the main bridge

        // NIOUT bus
        HwBus busNIOUT = new HwBus("BusNetworkInterfaceOUT" + getPositionNaming());
        tmla.addHwNode(busNIOUT);

        HwCPU cpuNIOUT = new HwCPU("CPUNetworkInterfaceOUT" + getPositionNaming());
        tmla.addHwNode(cpuNIOUT);
        tmlmap.addTaskToHwExecutionNode(tniOut, cpuNIOUT);

        HwMemory memNIOUT = new HwMemory("MemNetworkInterfaceOUT" + getPositionNaming());
        tmla.addHwNode(memNIOUT);
        tmlmap.addCommToHwCommNode(playingTheRoleOfPrevious[NB_OF_PORTS-1].chOutToIN, memNIOUT);

        tmla.makeHwLink(busNIOUT, outForExit);
        tmla.makeHwLink(busNIOUT, cpuNIOUT);
        tmla.makeHwLink(busNIOUT, memNIOUT);

        // We must connect this mem out to the main bridge
        // To so so, we create a bus
        HwBus busOUTToMainBridge = new HwBus("busOUTToMainBridge" + getPositionNaming());
        tmla.addHwNode(busOUTToMainBridge);
        tmla.makeHwLink(busOUTToMainBridge, memNIOUT);
        tmla.makeHwLink(busOUTToMainBridge, mainBridge);

        // fake task on CPU
        // tmlmap.addTaskToHwExecutionNode(fto, myHwExecutionNode);
    }

    public void makeOriginChannels() {
        Vector<TMLChannel> newChannels = new Vector<>();

        // We now need to modify the corresponding input tasks
        // The channel is modified to NBRNBW
        // Once the sample has been sent, the outputEventOfMux is sent
        // It is mapped to the HW node mem

        // For all channels whose origin task is mapped on the CPU of the router

        for(TMLChannel ch: tmlmap.getTMLModeling().getChannels()) {
            if (main.getChannelID(ch) != null) {
                TMLTask t = ch.getOriginTask();
                HwExecutionNode mappedOn = tmlmap.getHwNodeOf(t);
                if (mappedOn == myHwExecutionNode) {
                    TraceManager.addDev("Found HwNode of origin task " + t.getTaskName() + " for channel " + ch.getName());
                    // We must rework the channel of the task.
                    // The channel is modified to a NBRNBW with the same task has sender / receiver
                    // The channel is mapped to the local mem
                    // Once the sample has been sent, an event is sent to the input task of the router
                    // For a receiver, the event is first waited for, and then the read in the new channel is performed

                    TMLChannel newChannel = new TMLChannel(ch.getName() + "__origin", ch.getReferenceObject());
                    newChannel.setType(TMLChannel.NBRNBW);
                    newChannel.setOriginTask(t);
                    newChannel.setDestinationTask(t);
                    newChannel.setSize(ch.getSize());
                    newChannel.setVC(ch.getVC());
                    newChannels.add(newChannel);


                    // Map modify channel to the right memory
                    HwMemory mem = tmlmap.getTMLArchitecture().getHwMemoryByName(myHwExecutionNode.getName() + "__mem");
                    if (mem != null) {
                        TraceManager.addDev("Mapping channel " + ch.getName() + " on mem " + mem.getName());
                        tmlmap.addCommToHwCommNode(newChannel, mem);
                    }

                    // Must now modify the source app
                    TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(pktlen);
                    TMLAttribute dstX = new TMLAttribute("dstX", "dstX", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(dstX);
                    TMLAttribute dstY = new TMLAttribute("dstY", "dstY", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(dstY);
                    TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(vc);
                    TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "1");
                    t.addAttributeIfApplicable(eop);
                    TMLAttribute chid = new TMLAttribute("chid", "chid", new TMLType(TMLType.NATURAL),
                            "" + main.getChannelID(ch));
                    t.addAttributeIfApplicable(chid);

                    TMLActivity activity = t.getActivityDiagram();
                    Vector<TMLActivityElement> newElements = new Vector<>();
                    TMLWriteChannel twc;
                    for (TMLElement elt : activity.getElements()) {
                        if (elt instanceof TMLWriteChannel) {
                            twc = (TMLWriteChannel) elt;
                            if (twc.getChannel(0) == ch) {
                                Point p = main.getChannelDstXY(ch);
                                if (p != null) {
                                    TraceManager.addDev("Modifying write ch of task " + t.getTaskName());
                                    TMLSendEvent tse = new TMLSendEvent("EvtForSending__" + ch.getName(), ch.getReferenceObject());
                                    twc.replaceChannelWith(ch, newChannel);
                                    newElements.add(tse);
                                    tse.setEvent(mapOfAllOutputChannels.get(ch));
                                    tse.addParam("" + newChannel.getSize());
                                    tse.addParam("" + (int)(p.getX()));
                                    tse.addParam("" + (int)(p.getY()));
                                    tse.addParam("" + newChannel.getVC());
                                    tse.addParam("eop");
                                    tse.addParam("" + main.getChannelID(ch));
                                    tse.addNext(twc.getNextElement(0));
                                    twc.setNewNext(twc.getNextElement(0), tse);
                                }
                            }
                        }
                    }
                    for (TMLActivityElement newElt : newElements) {
                        activity.addElement(newElt);
                    }
                }
            }
        }

        for(TMLChannel ch: newChannels) {
            tmlmap.getTMLModeling().addChannel(ch);
        }

    }

    public void makeDestinationChannels() {

        Vector<TMLChannel> newChannels = new Vector<>();
        mapOfAllInputChannels = new HashMap<>();

        TMLModeling tmlm = tmlmap.getTMLModeling();

        Vector<TMLEvent> events = new Vector<>();
        Vector<String> ids = new Vector<>();

        for(TMLChannel ch: tmlmap.getTMLModeling().getChannels()) {
            if (main.getChannelID(ch) != null) {
                TMLTask t = ch.getDestinationTask();
                HwExecutionNode mappedOn = tmlmap.getHwNodeOf(t);
                if (mappedOn == myHwExecutionNode) {
                    TMLEvent packetOut = new TMLEvent("evtPktOutToAppFromOut__" + xPos + "_" + yPos,
                            null, 8, true);
                    packetOut.addParam(new TMLType(TMLType.NATURAL));
                    packetOut.addParam(new TMLType(TMLType.NATURAL));
                    packetOut.addParam(new TMLType(TMLType.NATURAL));
                    packetOut.addParam(new TMLType(TMLType.NATURAL));
                    packetOut.addParam(new TMLType(TMLType.NATURAL));
                    packetOut.addParam(new TMLType(TMLType.NATURAL));
                    tmlm.addEvent(packetOut);
                    packetOut.setOriginTask(tniOut);
                    packetOut.setDestinationTask(ch.getDestinationTask());
                    events.add(packetOut);
                    ids.add(main.getChannelID(ch));
                    mapOfAllInputChannels.put(ch, packetOut);
                }
            }
        }

        tniOut.postProcessing(events, ids);

        for(TMLChannel ch: tmlmap.getTMLModeling().getChannels()) {
            if (main.getChannelID(ch) != null) {
                TMLTask t = ch.getDestinationTask();
                HwExecutionNode mappedOn = tmlmap.getHwNodeOf(t);
                if (mappedOn == myHwExecutionNode) {
                    TraceManager.addDev("Found HwNode of origin task " + t.getTaskName() + " for channel " + ch.getName());
                    // We must rework the channel of the task.
                    // The channel is modified to a NBRNBW with the same task has sender / receiver
                    // The channel is mapped to the local mem
                    // Once the sample has been sent, an event is sent to the input task of the router
                    // For a receiver, the event is first waited for, and then the read in the new channel is performed

                    TMLChannel newChannel = new TMLChannel(ch.getName() + "__destination", ch.getReferenceObject());
                    newChannel.setType(TMLChannel.NBRNBW);
                    newChannel.setOriginTask(t);
                    newChannel.setDestinationTask(t);
                    newChannel.setSize(ch.getSize());
                    newChannel.setVC(ch.getVC());
                    newChannels.add(newChannel);

                    // Map modify channel to the right memory
                    HwMemory mem = tmlmap.getTMLArchitecture().getHwMemoryByName(myHwExecutionNode.getName() + "__mem");
                    if (mem != null) {
                        TraceManager.addDev("Mapping channel " + ch.getName() + " on mem " + mem.getName());
                        tmlmap.addCommToHwCommNode(newChannel, mem);
                    }

                    // Must now modify the source app
                    TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(pktlen);
                    TMLAttribute dstX = new TMLAttribute("dstX", "dstX", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(dstX);
                    TMLAttribute dstY = new TMLAttribute("dstY", "dstY", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(dstY);
                    TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
                    t.addAttributeIfApplicable(vc);
                    TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "1");
                    t.addAttributeIfApplicable(eop);
                    TMLAttribute chid = new TMLAttribute("chid", "chid", new TMLType(TMLType.NATURAL),
                            "" + main.getChannelID(ch));
                    t.addAttributeIfApplicable(chid);

                    TMLActivity activity = t.getActivityDiagram();
                    Vector<TMLActivityElement> newElements = new Vector<>();
                    TMLReadChannel trc;
                    for (TMLElement elt : activity.getElements()) {
                        if (elt instanceof TMLReadChannel) {
                            trc = (TMLReadChannel) elt;
                            if (trc.getChannel(0) == ch) {
                                TraceManager.addDev("Modifying read ch of task " + t.getTaskName() + " for channel " + ch.getName());
                                // TODO TODO
                                trc.replaceChannelWith(ch, newChannel);
                                TMLWaitEvent twe = new TMLWaitEvent("EvtForReceiving__" + ch.getName(), ch.getReferenceObject());
                                newElements.add(twe);
                                twe.setEvent(mapOfAllInputChannels.get(ch));
                                twe.addParam("pktlen");
                                twe.addParam("dstX");
                                twe.addParam("dstY");
                                twe.addParam("vc");
                                twe.addParam("eop");
                                twe.addParam("chid");
                                activity.replaceAllNext(trc, twe);
                                twe.addNext(trc);
                            }
                        }
                    }
                    for (TMLActivityElement newElt : newElements) {
                        activity.addElement(newElt);
                    }
                }
            }
        }

        for(TMLChannel ch: newChannels) {
            tmlmap.getTMLModeling().addChannel(ch);
        }



    }


    public void postProcessing() {



    }

}
