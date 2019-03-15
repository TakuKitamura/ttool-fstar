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
import ui.TGComponent;

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
public class TranslatedRouter<E>  {
    private final int NB_OF_PORTS = 5;
    private final int CHANNEL_SIZE = 4;
    private final int CHANNEL_MAX = 8;

    private int nbOfVCs, xPos, yPos, nbOfApps;

    private HwNoC noc;
    private List<TMLChannel> channelsViaNoc;

    private Vector<TMLEvent> pktins;
    private Vector<TMLTask> dispatchers;

    private TMLMapping<?> tmlmap;



    public TranslatedRouter(TMLMapping<E> tmlmap, HwNoC noc, List<TMLChannel> channelsViaNoc, int nbOfVCs, int xPos, int yPos) {
        this.nbOfVCs = nbOfVCs;
        this.noc = noc;
        this.channelsViaNoc = channelsViaNoc;
        this.xPos = xPos;
        this.yPos = yPos;
        this.tmlmap = tmlmap;
    }


    private String getInfo() {
        return "__R" + xPos + "_" + yPos;
    }

    public void makeRouter() {
        int i, j;
        TMLTask t;
        TMLModeling tmlm = tmlmap.getTMLModeling();


        // MUX for the different writing tasks
        // For each writing channel of the corresponding CPU, we need MUX to be created.
        // We first get the corresponding CPU
        String nameOfExecNode = noc.getHwExecutionNode(xPos, yPos);
        HwExecutionNode execNode = tmlmap.getTMLArchitecture().getHwExecutionNodeByName(nameOfExecNode);

        if (nameOfExecNode == null) {
            nameOfExecNode = "fakeCPU_" + xPos + "_" + yPos;
        }


        if (execNode == null) {
            TraceManager.addDev("Could NOT find an exec node for (" + xPos + "," + yPos + ")");
        } else {
            TraceManager.addDev("Found an exec node for (" + xPos + "," + yPos + "): " + execNode.getName());
        }

        // Then, we need to find the channels starting from/arriving to a task mapped on this execNode
        Vector<TMLChannel> inputChannels = new Vector<>();
        Vector<TMLChannel> outputChannels = new Vector<>();
        if (execNode != null) {
            for(TMLChannel ch: channelsViaNoc) {
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
        for(i=0; i<nbOfVCs; i++) {
            // Now that we know all channels, we can generate the MUX tasks
            // We need one event par outputChannel
            HashMap<TMLChannel, TMLEvent> mapOfOutputChannels = new HashMap<>();
            Vector<TMLEvent> inputEventsOfMUX = new Vector<>();
            for(TMLChannel chan: outputChannels) {
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

            TaskMUXAppDispatch muxTask = new TaskMUXAppDispatch("MUXof" + nameOfExecNode +"_VC" + i, null, null);
            tmlm.addTask(muxTask);
            muxTask.generate(inputEventsOfMUX, eventForMUX_and_NI_IN);
            muxTasks.add(muxTask);
        }


        // Finally, we need to modify the src apps with the new event, and modifying the channel in order to write into
        // the corresponding local memory


        // NETWORK INTERFACE IN
        // We must first gathers events from must task
        Vector<TMLEvent> inputEventsFromMUX = new Vector<>();
        for(TaskMUXAppDispatch tmux: muxTasks) {
            inputEventsFromMUX.add(tmux.getOutputEvent());
        }


        TaskNetworkInterface tniIn = new TaskNetworkInterface("NI_IN_" + nameOfExecNode, null,
                null);
        tmlm.addTask(tniIn);

        // One TMLEvent for feedback for each VC
        Vector<TMLEvent> feedbackEventsNIINs = new Vector<>();
        for(i=0; i<nbOfVCs; i++) {
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
        tmlm.addChannel(outputChannelFromNIINtoIN);

        tniIn.generate(nbOfVCs, feedbackEventsNIINs, inputEventsFromMUX, outputFromNIINtoIN, outputChannelFromNIINtoIN);


        // IN NOC
        // We need one ouput channel per VC and one output event per VC
        Vector<TMLEvent> evtFromINtoINVCs = new Vector<>();
        Vector<TMLChannel> chFromINtoINVCs = new Vector<>();
        for(i=0; i<nbOfVCs; i++) {
            TMLEvent evtFromINtoINVC = new TMLEvent("EventBetweenIN_IN_forVC_" + i + "_" + nameOfExecNode,
                    null, 8, true);
            tmlm.addEvent(evtFromINtoINVC);
            evtFromINtoINVCs.add(evtFromINtoINVC);

            TMLChannel chFromINtoINVC = new TMLChannel("channelBetweenIN_IN_for_VC" + i + "_" + nameOfExecNode,
                    null);
            chFromINtoINVC.setSize(4);
            chFromINtoINVC.setMax(8);
            tmlm.addChannel(chFromINtoINVC);
            chFromINtoINVCs.add(chFromINtoINVC);
        }

        TaskINForDispatch inDispatch = new TaskINForDispatch("IN_" + execNode, null, null);
        tmlm.addTask(inDispatch);
        inDispatch.generate(nbOfVCs, outputFromNIINtoIN, outputChannelFromNIINtoIN, evtFromINtoINVCs, chFromINtoINVCs);


        // IN specific to an input of the NoC apart from internal CPU
        // inputs 0 to 3
        


    }



}
