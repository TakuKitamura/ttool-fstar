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

package ui.directedgraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.*;
import tmltranslator.*;
import tmltranslator.tomappingsystemc2.DiploSimulatorCodeGenerator;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TGConnector;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmlad.*;
import ui.tmlcompd.TMLCPrimitivePort;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class DirectedGraphTranslator: this class generate the directed graph
 * equivalent for the sysml model
 * 
 * 23/09/2019
 *
 * @author Maysam Zoor
 */
public class DirectedGraphTranslator extends JApplet {

    // private TMLArchiPanel tmlap; // USed to retrieve the currently opened
    // architecture panel
    // private TMLMapping<TGComponent> tmap;
    private TMLComponentDesignPanel tmlcdp;

    private TMLTask task, task1, task2;

    protected TMLActivity activity;

    // List<HwNode> path;

    TMLActivityElement currentElement;
    TMLActivityElement backwardElement;
    ArrayList<String> SummaryCommMapping;

    private Graph<String, DefaultEdge> g;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    List<TMLComponentDesignPanel> cpanels;
    List<HwLink> links;
    TMLMapping<TGComponent> tmap;
    HashMap<String, String> addedEdges = new HashMap<String, String>();
    HashMap<String, HashSet<String>> sendEventWaitEventEdges = new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> readWriteChannelEdges = new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> sequenceEdges = new HashMap<String, HashSet<String>>();
    HashMap<String, ArrayList<String>> orderedSequenceList = new HashMap<String, ArrayList<String>>();
    HashMap<String, HashSet<String>> unOrderedSequenceEdges = new HashMap<String, HashSet<String>>();
    HashMap<String, ArrayList<String>> unOrderedSequenceList = new HashMap<String, ArrayList<String>>();
    List<String> forEverLoopList = new ArrayList<String>();
    HashMap<String, List<TMLTask>> requests = new HashMap<String, List<TMLTask>>();

    HashMap<String, HashSet<String>> requestEdges = new HashMap<String, HashSet<String>>();

    HashMap<String, List<String>> requestsOriginDestination = new HashMap<String, List<String>>();
    HashMap<String, List<String>> requestsPorts = new HashMap<String, List<String>>();

    HashMap<String, List<String>> requestsDestination = new HashMap<String, List<String>>();
    Vector<String> allLatencyTasks = new Vector<String>();

    static JScrollPane scrollPane = new JScrollPane();

    // List<String,String> = new ArrayList<String,String>();

    HashMap<String, String> nameIDTaskList = new HashMap<String, String>();

    public HashMap<String, String> getNameIDTaskList() {
        return nameIDTaskList;
    }

    private Object[][] dataByTask = null;
    private Object[][] dataByTaskMinMax = null;
    private Object[][] dataByTaskBYRow;
    private Object[][] dataByTaskHWBYRow;

    HashMap<Integer, List<SimulationTransaction>> dataByTaskR = new HashMap<Integer, List<SimulationTransaction>>();
    HashMap<Integer, List<SimulationTransaction>> dataBydelayedTasks = new HashMap<Integer, List<SimulationTransaction>>();
    HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>> timeDelayedPerRow = new HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>>();

    HashMap<Integer, List<String>> detailsOfMinMaxRow = new HashMap<Integer, List<String>>();
    HashMap<Integer, List<SimulationTransaction>> dataBydelayedTasksOfMinMAx = new HashMap<Integer, List<SimulationTransaction>>();
    JFrame frame;

    List<Integer> times1 = new ArrayList<Integer>();
    List<Integer> times2 = new ArrayList<Integer>();

    Vector<SimulationTransaction> transFile;
    String idTask1;
    String idTask2;
    String task2DeviceName = "";
    String task1DeviceName = "";

    Vector<SimulationTransaction> relatedsimTraces = new Vector<SimulationTransaction>();
    Vector<SimulationTransaction> delayDueTosimTraces = new Vector<SimulationTransaction>();

    HashMap<String, ArrayList<ArrayList<Integer>>> runnableTimePerDevice = new HashMap<String, ArrayList<ArrayList<Integer>>>();

    @SuppressWarnings("deprecation")
    public DirectedGraphTranslator(TMLMapping<TGComponent> tmap1, List<TMLComponentDesignPanel> cpanels1) {

        tmap = tmap1;
        cpanels = cpanels1;

        links = tmap.getTMLArchitecture().getHwLinks();

        tmlcdp = cpanels.get(0);

        DrawDirectedGraph();

        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(g);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);

        layout.setInterHierarchySpacing(100);
        layout.setInterRankCellSpacing(100);
        layout.setIntraCellSpacing(100);

        layout.execute(graphAdapter.getDefaultParent());

        scrollPane.setViewportView(new mxGraphComponent(graphAdapter));

        scrollPane.setVisible(true);

        scrollPane.revalidate();
        scrollPane.repaint();
        frame = new JFrame("The Sys-ML Model As Directed Graph");
        frame.add(scrollPane);
        frame.pack();
        // frame.setVisible(false);

    }

    // The main function to add the vertices and edges according to the model

    private void DrawDirectedGraph() {

        HashMap<String, HashSet<String>> cpuTasks;
        HashMap<String, HashSet<TMLElement>> buschannel = new HashMap<String, HashSet<TMLElement>>();
        HashMap<String, HashSet<TMLElement>> memorychannel = new HashMap<String, HashSet<TMLElement>>();

        HashMap<String, HashSet<TMLElement>> bridgechannel = new HashMap<String, HashSet<TMLElement>>();

        g = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (HwNode node : tmap.getArch().getBUSs()) {

            g.addVertex(node.getName());

            if (tmap.getLisMappedChannels(node).size() > 0) {
                buschannel.put(node.getName(), tmap.getLisMappedChannels(node));

            }

        }

        for (HwNode node : tmap.getArch().getHwBridge()) {

            g.addVertex(node.getName());

            if (tmap.getLisMappedChannels(node).size() > 0) {
                bridgechannel.put(node.getName(), tmap.getLisMappedChannels(node));

            }

        }

        for (HwNode node : tmap.getArch().getMemories()) {

            g.addVertex(node.getName());

            if (tmap.getLisMappedChannels(node).size() > 0) {
                memorychannel.put(node.getName(), tmap.getLisMappedChannels(node));

            }

        }

        for (Entry<String, HashSet<TMLElement>> entry : buschannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();

            for (TMLElement busCh : busChList) {

                String ChannelName = busCh.getName();
                g.addVertex(ChannelName);
                g.addEdge(busName, ChannelName);

                TMLChannel tmlch = (TMLChannel) busCh;

                String writeChannel = tmlch.getDestinationTask().getName() + "__" + "writechannel:" + tmlch.getDestinationPort();
                String readChannel;

            }

        }

        for (Entry<String, HashSet<TMLElement>> entry : bridgechannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();

            for (TMLElement busCh : busChList) {

                String ChannelName = busCh.getName();
                g.addVertex(ChannelName);
                g.addEdge(busName, ChannelName);
            }

        }

        for (Entry<String, HashSet<TMLElement>> entry : memorychannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();

            for (TMLElement busCh : busChList) {

                String ChannelName = busCh.getName();
                g.addVertex(ChannelName);
                g.addEdge(busName, ChannelName);
            }

        }

        DiploSimulatorCodeGenerator gen = new DiploSimulatorCodeGenerator(tmap);
        for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
            List<HwCommunicationNode> pathNodes = gen.determineRoutingPath(tmap.getHwNodeOf(ch.getOriginTask()),
                    tmap.getHwNodeOf(ch.getDestinationTask()), ch);

            if (!g.vertexSet().contains(ch.getName())) {
                g.addVertex(ch.getName());
            }

            if (!pathNodes.isEmpty()) {
                for (HwCommunicationNode node : pathNodes) {
                    if (!g.containsEdge(node.getName(), ch.getName())) {
                        g.addEdge(node.getName(), ch.getName());
                    }

                }

            }

        }

        SummaryCommMapping = tmap.getSummaryCommMapping();

        cpuTasks = getCPUTaskMap();

        for (HwLink link : links) {

            if (g.containsVertex(link.hwnode.getName()) && g.containsVertex(link.bus.getName())) {

                g.addEdge(link.hwnode.getName(), link.bus.getName());
                g.addEdge(link.bus.getName(), link.hwnode.getName());
            }

        }

        if (addedEdges.size() > 0) {
            for (Entry<String, String> edge : addedEdges.entrySet()) {
                g.addEdge(edge.getKey(), edge.getValue());

            }
        }

        if (sendEventWaitEventEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : sendEventWaitEventEdges.entrySet()) {

                for (String waitEventEdge : edge.getValue())

                    g.addEdge(edge.getKey(), waitEventEdge);

            }
        }
        if (readWriteChannelEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : readWriteChannelEdges.entrySet()) {

                for (String readChannelEdge : edge.getValue())

                    g.addEdge(edge.getKey(), readChannelEdge);

            }
        }

        if (sequenceEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : sequenceEdges.entrySet()) {

                for (String sequenceEdge : edge.getValue())

                    g.addEdge(edge.getKey(), sequenceEdge);

            }
        }

        if (unOrderedSequenceEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : unOrderedSequenceEdges.entrySet()) {

                for (String sequenceEdge : edge.getValue())

                    g.addEdge(edge.getKey(), sequenceEdge);

            }
        }

        if (requestEdges.size() > 0) {

            for (Entry<String, HashSet<String>> edge : requestEdges.entrySet()) {

                for (String requestsingleEdges : edge.getValue()) {

                    g.addEdge(edge.getKey(), requestsingleEdges);

                }

            }

        }

    }

    // draw the vertices and edges for the tasks mapped to the CPUs

    public HashMap<String, HashSet<String>> getCPUTaskMap() {
        HashMap<String, HashSet<String>> cpuTaskMap = new HashMap<String, HashSet<String>>();

        HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String, HashSet<TMLTask>>();

        if (tmap == null) {
            return cpuTaskMap;
        }

        for (HwNode node : tmap.getArch().getCPUs()) {
            if (tmap.getLisMappedTasks(node).size() > 0) {
                cpuTask.put(node.getName(), tmap.getLisMappedTasks(node));

            }

        }

        for (Entry<String, HashSet<TMLTask>> entry : cpuTask.entrySet()) {

            String key = entry.getKey();
            HashSet<TMLTask> value = entry.getValue();
            Vector<TMLActivityElement> multiNexts = new Vector<TMLActivityElement>();
            TMLActivityElement elt;
            // Map <String, String> sendEvt;
            HashMap<String, List<String>> sendEvt = new HashMap<String, List<String>>();
            HashMap<String, List<String>> waitEvt = new HashMap<String, List<String>>();

            HashMap<String, String> sendData = new HashMap<String, String>();
            HashMap<String, String> receiveData = new HashMap<String, String>();

            // GEt List of all requests

            for (TMLTask task : value) {

                if (task.isRequested()) {
                    TMLRequest requestToTask = task.getRequest();

                    requestToTask.getReferenceObject();

                    requestToTask.getDestinationTask();

                    requestToTask.getOriginTasks().get(0);

                    requestToTask.ports.get(0).getName();
                    requestToTask.getExtendedName();

                    String destinationRequest = requestToTask.getDestinationTask().getName() + "__"
                            + requestToTask.getDestinationTask().getActivityDiagram().get(0).getName() + "__"
                            + requestToTask.getDestinationTask().getActivityDiagram().get(0).getID();

                    String destinationRequestName = requestToTask.getDestinationTask().getName();

                    for (TMLTask originTask : requestToTask.getOriginTasks()) {

                        String requestOriginTaskName = originTask.getName();

                        if (requestsOriginDestination.containsKey(requestOriginTaskName)) {
                            if (!requestsOriginDestination.get(requestOriginTaskName).contains(destinationRequestName)) {
                                requestsOriginDestination.get(requestOriginTaskName).add(destinationRequestName);
                            }
                        } else {
                            ArrayList<String> destinationRequestNames = new ArrayList<String>();
                            destinationRequestNames.add(destinationRequestName);
                            requestsOriginDestination.put(requestOriginTaskName, destinationRequestNames);
                        }

                    }

                    for (TMLCPrimitivePort requestsPort : requestToTask.ports) {

                        String requestsPortName = requestsPort.getPortName();

                        if (requestsPorts.containsKey(task.getName())) {
                            if (!requestsPorts.get(task.getName()).contains(requestsPortName)) {
                                requestsPorts.get(task.getName()).add(requestsPortName);
                            }
                        } else {
                            ArrayList<String> requestsPortNames = new ArrayList<String>();
                            requestsPortNames.add(requestsPortName);
                            requestsPorts.put(task.getName(), requestsPortNames);
                        }

                    }

                    if (requestsDestination.containsKey(destinationRequestName)) {
                        if (!requestsDestination.get(destinationRequestName).contains(destinationRequest)) {
                            requestsDestination.get(destinationRequestName).add(destinationRequest);
                        }
                    } else {
                        ArrayList<String> destinationRequestNames = new ArrayList<String>();
                        destinationRequestNames.add(destinationRequest);
                        requestsDestination.put(destinationRequestName, destinationRequestNames);
                    }

                }

            }

            for (TMLTask task : value) {

                for (TMLComponentDesignPanel dpPanel : cpanels) {
                    String[] taskpanel = task.getName().split("__");

                    if (dpPanel.getNameOfTab().equals(taskpanel[0])) {
                        tmlcdp = dpPanel;
                    }

                }
                // get the names and params of send events per task and their corresponding wait
                // events
                for (TMLSendEvent sendEvent : task.getSendEvents()) {
                    TMLCPrimitivePort sendingPortdetails = sendEvent.getEvent().port;
                    TMLCPrimitivePort receivePortdetails = sendEvent.getEvent().port2;

                    String sendingPortparams = sendEvent.getAllParams();

                    TMLTask destinationTasks = sendEvent.getEvent().getDestinationTask();

                    sendEvt.put("sendevent:" + sendingPortdetails.getPortName() + "(" + sendingPortparams + ")", new ArrayList<String>());

                    for (TMLWaitEvent wait_sendEvent : destinationTasks.getWaitEvents()) {
                        String receivePortparams = wait_sendEvent.getAllParams();

                        sendEvt.get("sendevent:" + sendingPortdetails.getPortName() + "(" + sendingPortparams + ")")
                                .add("waitevent:" + receivePortdetails.getPortName() + "(" + receivePortparams + ")");

                    }

                }
                // get the names of read channels per task and their corresponding write
                // channels

                for (TMLReadChannel readChannel : task.getReadChannels()) {

                    int i = readChannel.getNbOfChannels();

                    for (int j = 0; j < i; j++) {

                        String sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName();
                        String receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName();

                        if (!sendingDataPortdetails.equals(receiveDataPortdetails)) {
                            receiveData.put(receiveDataPortdetails, sendingDataPortdetails);

                        }

                    }

                }
                // get the names of write channels per task and their corresponding read
                // channels
                for (TMLWriteChannel writeChannel : task.getWriteChannels()) {

                    int i = writeChannel.getNbOfChannels();

                    for (int j = 0; j < i; j++) {

                        // writeChannel.getChannel(j);
                        String sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName();
                        String receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName();

                        if (!sendingDataPortdetails.equals(receiveDataPortdetails)) {

                            sendData.put(sendingDataPortdetails, receiveDataPortdetails);
                        }

                    }

                }
                // get the names and params of wait events per task and their corresponding send
                // events

                for (TMLWaitEvent waitEvent : task.getWaitEvents()) {
                    // TMLCPrimitivePort portdetails = waitEvent.getEvent().port;
                    TMLCPrimitivePort sendingPortdetails = waitEvent.getEvent().port;
                    TMLCPrimitivePort receivePortdetails = waitEvent.getEvent().port2;

                    String receivePortparams = waitEvent.getAllParams();

                    // tmlcdp.tmlctdp.getAllPortsConnectedTo(portdetails);

                    waitEvt.put("waitevent:" + receivePortdetails.getPortName() + "(" + receivePortparams + ")", new ArrayList<String>());

                    TMLTask originTasks = waitEvent.getEvent().getOriginTask();

                    for (TMLSendEvent wait_sendEvent : originTasks.getSendEvents()) {

                        String sendingPortparams = wait_sendEvent.getAllParams();

                        waitEvt.get("waitevent:" + receivePortdetails.getPortName() + "(" + receivePortparams + ")")
                                .add("sendevent:" + sendingPortdetails.getPortName() + "(" + sendingPortparams + ")");

                    }

                }

                // add the name of the task as a vertex
                g.addVertex(key);
                g.addVertex(task.getName());

                g.addEdge(key, task.getName());

                activity = task.getActivityDiagram();
                int count = 1;
                currentElement = activity.getFirst();
                String taskStartName = "";
                String taskEndName = "";

                int countTillStart = 0;
                boolean hasSequence = false;
                boolean hasForLoop = false;

                HashMap<String, List<String>> forLoopNextValues = new HashMap<String, List<String>>();

                // loop over all the activites corresponding to a task
                while (count <= activity.nElements()) {

                    String eventName;
                    String preEventName;

                    if (currentElement.getName().equals("Stop after infinite loop")) {
                        count++;

                        if (count <= activity.nElements()) {
                            if (currentElement.getNexts().size() == 1) {

                                currentElement = currentElement.getNexts().firstElement();

                            } else if (!multiNexts.isEmpty()) {
                                currentElement = multiNexts.get(0);

                                multiNexts.remove(0);

                            }

                            continue;
                        } else {
                            break;
                        }
                    } else if (currentElement.getReferenceObject() instanceof TMLADRandom) {
                        eventName = task.getName() + "__" + currentElement.getName() + "__" + currentElement.getID();

                    } else {
                        eventName = task.getName() + "__" + currentElement.getReferenceObject().toString() + "__" + currentElement.getID();

                    }

                    if (currentElement.getNexts().size() > 1) {
                        for (TMLActivityElement ae : currentElement.getNexts()) {
                            multiNexts.add(ae);

                        }

                    }

                    // in case an end was encountered , the previous activities should be checked:
                    // in
                    // case it is an end for a loop or sequence speavial edges should be added

                    if (currentElement.getReferenceObject() instanceof TMLADStopState) {

                        taskEndName = task.getName() + "__" + currentElement.getName() + "__" + currentElement.getID();

                        preEventName = task.getName() + "__" + activity.getPrevious(currentElement).getReferenceObject().toString() + "__"
                                + activity.getPrevious(currentElement).getID();

                        g.addVertex(taskEndName);
                        // allTasks.add(taskEndName);

                        if (!(activity.getPrevious(currentElement).getReferenceObject() instanceof TMLADSequence)) {
                            g.addEdge(preEventName, taskEndName);
                        }

                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String, DefaultEdge>(g);
                        if (orderedSequenceList.size() > 0) {

                            int noForLoop = 0;
                            // get path from sequence to end
                            for (Entry<String, ArrayList<String>> sequenceListEntry : orderedSequenceList.entrySet()) {

                                int directlyConnectedSeq = 0;

                                if (g.containsVertex(sequenceListEntry.getKey())) {

                                    List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(sequenceListEntry.getKey(), taskEndName, false,
                                            g.vertexSet().size());

                                    for (Entry<String, ArrayList<String>> othersequenceListEntryValue : orderedSequenceList.entrySet()) {

                                        for (int i = 0; i < path.size(); i++) {

                                            if (!othersequenceListEntryValue.getKey().equals(sequenceListEntry.getKey())) {

                                                if (path.get(i).getVertexList().contains(othersequenceListEntryValue.getKey())) {

                                                    directlyConnectedSeq++;

                                                }
                                            }
                                        }
                                    }

                                    if (path.size() > 0 && sequenceListEntry.getValue().size() > 0 && directlyConnectedSeq == 0) {

                                        for (int i = 0; i < path.size(); i++) {

                                            for (String sequenceListEntryValue : sequenceListEntry.getValue()) {

                                                if (g.containsVertex(sequenceListEntryValue)) {

                                                    if (path.get(i).getVertexList().contains(sequenceListEntryValue)) {

                                                        if (forLoopNextValues.size() > 0) {

                                                            for (Entry<String, List<String>> forloopListEntry : forLoopNextValues.entrySet()) {

                                                                if (path.get(i).getVertexList().contains(forloopListEntry.getValue().get(0))) {

                                                                    noForLoop++;
                                                                }
                                                            }
                                                        }

                                                        if (forEverLoopList.size() > 0) {

                                                            for (String forloopListEntry : forEverLoopList) {

                                                                if (path.get(i).getVertexList().contains(forloopListEntry)) {

                                                                    noForLoop++;
                                                                }
                                                            }
                                                        }

                                                        if (noForLoop == 0) {

                                                            int nextIndex = sequenceListEntry.getValue().indexOf(sequenceListEntryValue) + 1;

                                                            if (nextIndex < sequenceListEntry.getValue().size()) {

                                                                HashSet<String> endSequenceVertex = new HashSet<String>();
                                                                endSequenceVertex.add(sequenceListEntry.getValue().get(nextIndex));

                                                                if (sequenceEdges.containsKey(taskEndName)) {

                                                                    if (!sequenceEdges.get(taskEndName)
                                                                            .contains(sequenceListEntry.getValue().get(nextIndex))) {
                                                                        sequenceEdges.get(taskEndName)
                                                                                .add(sequenceListEntry.getValue().get(nextIndex));
                                                                    }

                                                                } else {

                                                                    sequenceEdges.put(eventName, endSequenceVertex);

                                                                }

                                                            } else if (nextIndex == sequenceListEntry.getValue().size()
                                                                    && orderedSequenceList.size() > 1) {

                                                                for (Entry<String, ArrayList<String>> othersequenceListEntryValue : orderedSequenceList
                                                                        .entrySet()) {

                                                                    if (!othersequenceListEntryValue.getKey().equals(sequenceListEntry.getKey())) {

                                                                        int connectedSeq = 0;

                                                                        List<GraphPath<String, DefaultEdge>> pathBetweenSeq = allPaths.getAllPaths(
                                                                                othersequenceListEntryValue.getKey(), taskEndName, false,
                                                                                g.vertexSet().size());

                                                                        for (int j = 0; j < pathBetweenSeq.size(); j++) {

                                                                            for (Entry<String, ArrayList<String>> adjacentsequenceListEntryValue : orderedSequenceList
                                                                                    .entrySet()) {
                                                                                if (!adjacentsequenceListEntryValue.getKey()
                                                                                        .equals(sequenceListEntry.getKey())
                                                                                        && !adjacentsequenceListEntryValue.getKey()
                                                                                                .equals(othersequenceListEntryValue.getKey())) {

                                                                                    if (path.get(i).getVertexList()
                                                                                            .contains(adjacentsequenceListEntryValue)) {

                                                                                        connectedSeq++;

                                                                                    }

                                                                                }
                                                                            }

                                                                        }

                                                                        if (connectedSeq == 0 && pathBetweenSeq.size() > 0) {

                                                                            for (String othersequenceListValue : othersequenceListEntryValue
                                                                                    .getValue()) {

                                                                                List<GraphPath<String, DefaultEdge>> pathToNextValue = allPaths
                                                                                        .getAllPaths(othersequenceListValue, taskEndName, false,
                                                                                                g.vertexSet().size());

                                                                                if (pathToNextValue.size() > 0)

                                                                                {

                                                                                    int nextAdjIndex = othersequenceListEntryValue.getValue()
                                                                                            .indexOf(othersequenceListValue) + 1;

                                                                                    if (nextAdjIndex < othersequenceListEntryValue.getValue()
                                                                                            .size()) {

                                                                                        HashSet<String> nextSequenceVertex = new HashSet<String>();
                                                                                        nextSequenceVertex.add(othersequenceListEntryValue.getValue()
                                                                                                .get(nextAdjIndex));

                                                                                        if (sequenceEdges.containsKey(taskEndName)) {

                                                                                            if (!sequenceEdges.get(taskEndName)
                                                                                                    .contains(othersequenceListEntryValue.getValue()
                                                                                                            .get(nextAdjIndex))) {
                                                                                                sequenceEdges.get(taskEndName)
                                                                                                        .add(othersequenceListEntryValue.getValue()
                                                                                                                .get(nextAdjIndex));
                                                                                            }

                                                                                        } else {

                                                                                            sequenceEdges.put(eventName, nextSequenceVertex);

                                                                                        }
                                                                                    }

                                                                                }

                                                                            }

                                                                        }

                                                                    }

                                                                }
                                                            }

                                                        }

                                                    }

                                                }

                                            }

                                        }

                                    }

                                }
                            }

                        }

                        if (unOrderedSequenceList.size() > 0) {

                            // get path from sequence to end
                            for (Entry<String, ArrayList<String>> sequenceListEntry : unOrderedSequenceList.entrySet()) {

                                if (g.containsVertex(sequenceListEntry.getKey())) {

                                    int noForLoop = 0;

                                    List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(sequenceListEntry.getKey(), taskEndName, false,
                                            g.vertexSet().size());

                                    for (int i = 0; i < path.size(); i++) {

                                        if (path.size() > 0 && sequenceListEntry.getValue().size() > 0) {

                                            if (forLoopNextValues.size() > 0) {

                                                for (Entry<String, List<String>> forloopListEntry : forLoopNextValues.entrySet()) {

                                                    if (path.get(i).getVertexList().contains(forloopListEntry.getKey())) {

                                                        noForLoop++;
                                                    }
                                                }
                                            }

                                            if (forEverLoopList.size() > 0) {

                                                for (String forloopListEntry : forEverLoopList) {

                                                    if (path.get(i).getVertexList().contains(forloopListEntry)) {

                                                        noForLoop++;
                                                    }
                                                }
                                            }

                                            if (noForLoop == 0) {

                                                HashSet<String> endSequenceVertex = new HashSet<String>();
                                                endSequenceVertex.add(sequenceListEntry.getKey());

                                                if (unOrderedSequenceEdges.containsKey(taskEndName)) {

                                                    if (!unOrderedSequenceEdges.get(taskEndName).contains(sequenceListEntry.getKey())) {
                                                        unOrderedSequenceEdges.get(taskEndName).add(sequenceListEntry.getKey());
                                                    }

                                                } else {

                                                    unOrderedSequenceEdges.put(eventName, endSequenceVertex);

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // add if sequence on path of multiple for

                        if (forLoopNextValues.size() > 0) {

                            for (Entry<String, List<String>> forloopListEntry : forLoopNextValues.entrySet()) {

                                if (g.containsVertex(forloopListEntry.getValue().get(0))) {

                                    List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(forloopListEntry.getValue().get(0), taskEndName,
                                            false, g.vertexSet().size());

                                    for (int i = 0; i < path.size(); i++) {
                                        int forloopCount = 0;

                                        for (Entry<String, List<String>> forEntry : forLoopNextValues.entrySet()) {

                                            if (!forloopListEntry.getKey().equals(forEntry.getKey())) {
                                                if (path.get(i).getVertexList().contains(forEntry.getKey())) {

                                                    forloopCount++;

                                                }

                                            }

                                        }

                                        for (Entry<String, ArrayList<String>> seqEntry : orderedSequenceList.entrySet()) {

                                            if (path.get(i).getVertexList().contains(seqEntry.getKey())) {

                                                if (path.get(i).getVertexList().contains(seqEntry.getValue().get(seqEntry.getValue().size() - 1)))

                                                {

                                                } else {
                                                    forloopCount++;
                                                }

                                            }

                                        }

                                        for (Entry<String, ArrayList<String>> unOrderedseqEntry : unOrderedSequenceList.entrySet()) {
                                            forloopCount++;

                                            if (path.get(i).getVertexList().contains(unOrderedseqEntry.getKey())) {

                                                HashSet<String> forLoopName = new HashSet<String>();
                                                forLoopName.add(forloopListEntry.getKey());

                                                if (unOrderedSequenceEdges.containsKey(unOrderedseqEntry.getKey())) {

                                                    if (unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).contains(forloopListEntry.getKey())) {
                                                        unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).add(forloopListEntry.getKey());
                                                    }

                                                } else {

                                                    unOrderedSequenceEdges.put(unOrderedseqEntry.getKey(), forLoopName);

                                                }

                                            }

                                        }
                                        String forvertexName = forloopListEntry.getKey();
                                        if (forloopCount == 0 && !g.containsEdge(taskEndName, forvertexName)) {

                                            addedEdges.put(taskEndName, forvertexName);

                                        }
                                    }

                                }

                                if (g.containsVertex(forloopListEntry.getValue().get(1)) && forLoopNextValues.size() > 1) {

                                    List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(forloopListEntry.getValue().get(1), taskEndName,
                                            false, g.vertexSet().size());

                                    if (path.size() > 0) {

                                        for (Entry<String, List<String>> previousForLoop : forLoopNextValues.entrySet()) {
                                            if (g.containsVertex(previousForLoop.getValue().get(0))
                                                    && !previousForLoop.getKey().equals(forloopListEntry.getKey())) {

                                                List<GraphPath<String, DefaultEdge>> previousForpath = allPaths
                                                        .getAllPaths(previousForLoop.getValue().get(0), taskEndName, false, g.vertexSet().size());

                                                for (int i = 0; i < previousForpath.size(); i++) {
                                                    int forloopCount = 0;

                                                    for (Entry<String, List<String>> forEntry : forLoopNextValues.entrySet()) {

                                                        if (previousForpath.get(i).getVertexList().contains(forEntry.getKey())
                                                                && !forloopListEntry.getKey().equals(forEntry.getKey())) {

                                                            forloopCount++;

                                                        }

                                                    }

                                                    String forvertexName = previousForLoop.getKey();
                                                    if (forloopCount == 0

                                                            && !g.containsEdge(taskEndName, forvertexName)) {

                                                        addedEdges.put(taskEndName, forvertexName);

                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                            }

                        }

                        if (!forEverLoopList.isEmpty())

                        {

                            for (String loopforEver : forEverLoopList) {

                                List<GraphPath<String, DefaultEdge>> pathforloopforever = allPaths.getAllPaths(loopforEver, taskEndName, false,
                                        g.vertexSet().size());

                                if (pathforloopforever.size() > 0) {

                                    for (int i = 0; i < pathforloopforever.size(); i++) {
                                        int forloopCount = 0;

                                        for (Entry<String, List<String>> previousForLoop : forLoopNextValues.entrySet()) {

                                            if (pathforloopforever.get(i).getVertexList().contains(previousForLoop.getValue().get(0))) {

                                                forloopCount++;

                                            }
                                        }

                                        for (Entry<String, ArrayList<String>> seqEntry : orderedSequenceList.entrySet()) {

                                            if (pathforloopforever.get(i).getVertexList().contains(seqEntry.getKey())) {

                                                if (pathforloopforever.get(i).getVertexList()
                                                        .contains(seqEntry.getValue().get(seqEntry.getValue().size() - 1)))

                                                {

                                                } else {
                                                    forloopCount++;
                                                }

                                            }

                                        }

                                        for (Entry<String, ArrayList<String>> unOrderedseqEntry : unOrderedSequenceList.entrySet()) {

                                            if (pathforloopforever.get(i).getVertexList().contains(unOrderedseqEntry.getKey())) {
                                                forloopCount++;

                                                HashSet<String> forLoopName = new HashSet<String>();
                                                forLoopName.add(loopforEver);

                                                if (unOrderedSequenceEdges.containsKey(unOrderedseqEntry.getKey())) {

                                                    if (unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).contains(loopforEver)) {
                                                        unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).add(loopforEver);
                                                    }

                                                } else {

                                                    unOrderedSequenceEdges.put(unOrderedseqEntry.getKey(), forLoopName);

                                                }

                                            }
                                        }

                                        if (forloopCount == 0) {

                                            addedEdges.put(taskEndName, loopforEver);

                                        }
                                    }
                                }
                            }

                        }

                        count++;

                    }

                    // start activity is added as a vertex
                    if (currentElement.getReferenceObject() instanceof TMLADStartState) {

                        taskStartName = task.getName() + "__" + currentElement.getName() + "__" + currentElement.getID();

                        g.addVertex(taskStartName);

                        g.addEdge(task.getName(), taskStartName);

                        count++;

                        if (!nameIDTaskList.containsKey(currentElement.getID())) {
                            nameIDTaskList.put(String.valueOf(currentElement.getID()), eventName);
                        }

                    }

                    // the below activities are added as vertex with the required edges
                    // these activities can be used to check later for latency

                    else if (currentElement.getReferenceObject() instanceof TMLADSendEvent
                            || currentElement.getReferenceObject() instanceof TMLADWaitEvent
                            || currentElement.getReferenceObject() instanceof TMLADForLoop
                            || currentElement.getReferenceObject() instanceof TMLADForStaticLoop
                            || currentElement.getReferenceObject() instanceof TMLADChoice
                            || currentElement.getReferenceObject() instanceof TMLADForEverLoop
                            || currentElement.getReferenceObject() instanceof TMLADExecI || currentElement.getReferenceObject() instanceof TMLADExecC
                            || currentElement.getReferenceObject() instanceof TMLADDelay
                            || currentElement.getReferenceObject() instanceof TMLADSendRequest
                            || currentElement.getReferenceObject() instanceof TMLADReadRequestArg
                            || currentElement.getReferenceObject() instanceof TMLADActionState
                            || currentElement.getReferenceObject() instanceof TMLADDelayInterval
                            || currentElement.getReferenceObject() instanceof TMLADExecCInterval
                            || currentElement.getReferenceObject() instanceof TMLADExecIInterval
                            || currentElement.getReferenceObject() instanceof TMLADNotifiedEvent
                            || currentElement.getReferenceObject() instanceof TMLADRandom
                            || currentElement.getReferenceObject() instanceof TMLADReadChannel
                            || currentElement.getReferenceObject() instanceof TMLADWriteChannel
                            || currentElement.getReferenceObject() instanceof TMLADSequence
                            || currentElement.getReferenceObject() instanceof TMLADUnorderedSequence
                            || currentElement.getReferenceObject() instanceof TMLADSelectEvt
                            || currentElement.getReferenceObject() instanceof TMLADDecrypt
                            || currentElement.getReferenceObject() instanceof TMLADEncrypt) {

                        if (activity.getPrevious(currentElement).getReferenceObject() instanceof TMLADRandom) {
                            preEventName = task.getName() + "__" + activity.getPrevious(currentElement).getName() + "__"
                                    + activity.getPrevious(currentElement).getID();

                        } else {
                            preEventName = task.getName() + "__" + activity.getPrevious(currentElement).getReferenceObject().toString() + "__"
                                    + activity.getPrevious(currentElement).getID();

                        }

                        if (!nameIDTaskList.containsKey(currentElement.getID())) {
                            nameIDTaskList.put(String.valueOf(currentElement.getID()), eventName);
                        }

                        if (g.containsVertex(preEventName)) {

                            g.addVertex(eventName);

                            g.addEdge(preEventName, eventName);
                            count++;

                        } else if ((activity.getPrevious(currentElement).getName().equals("start")) && g.containsVertex(taskStartName)) {

                            g.addVertex(eventName);

                            g.addEdge(taskStartName, eventName);
                            count++;

                        }

                        if (currentElement.getReferenceObject() instanceof TMLADSendEvent
                                || currentElement.getReferenceObject() instanceof TMLADWaitEvent
                                || currentElement.getReferenceObject() instanceof TMLADSendRequest
                                || currentElement.getReferenceObject() instanceof TMLADActionState
                                || currentElement.getReferenceObject() instanceof TMLADNotifiedEvent
                                || currentElement.getReferenceObject() instanceof TMLADReadChannel
                                || currentElement.getReferenceObject() instanceof TMLADWriteChannel
                                || currentElement.getReferenceObject() instanceof TMLADExecI
                                || currentElement.getReferenceObject() instanceof TMLADExecC
                                || currentElement.getReferenceObject() instanceof TMLADDelay
                                || currentElement.getReferenceObject() instanceof TMLADActionState
                                || currentElement.getReferenceObject() instanceof TMLADDelayInterval
                                || currentElement.getReferenceObject() instanceof TMLADExecCInterval
                                || currentElement.getReferenceObject() instanceof TMLADExecIInterval
                                || currentElement.getReferenceObject() instanceof TMLADDecrypt
                                || currentElement.getReferenceObject() instanceof TMLADEncrypt
                                || currentElement.getReferenceObject() instanceof TMLADReadRequestArg) {

                            allLatencyTasks.add(eventName);

                        }

                        if (currentElement.getReferenceObject() instanceof TMLADForEverLoop) {
                            forEverLoopList.add(eventName);
                        }

                        if (currentElement.getReferenceObject() instanceof TMLADSendRequest) {

                            if (requestsOriginDestination.containsKey(task.getName())) {

                                for (String destinationTask : requestsOriginDestination.get(task.getName())) {

                                    if (requestsPorts.containsKey(destinationTask)) {

                                        for (String portNames : requestsPorts.get(destinationTask)) {

                                            String[] requestName = currentElement.getReferenceObject().toString().split(":");

                                            String[] portname = requestName[1].split("[(]");

                                            if (portname[0].replaceAll(" ", "").equals(portNames.replaceAll(" ", ""))) {

                                                for (String destinationTaskstartname : requestsDestination.get(destinationTask)) {

                                                    if (requestEdges.containsKey(eventName)) {

                                                        if (!requestEdges.get(eventName).contains(destinationTaskstartname)) {
                                                            requestEdges.get(eventName).add(destinationTaskstartname);
                                                        }

                                                    } else {

                                                        HashSet<String> destinationTaskoriginstart = new HashSet<String>();
                                                        destinationTaskoriginstart.add(destinationTaskstartname);

                                                        requestEdges.put(eventName, destinationTaskoriginstart);

                                                    }

                                                }

                                            }

                                        }

                                    }

                                }

                            }
                        }

                        if (currentElement.getReferenceObject() instanceof TMLADSendEvent) {

                            if (sendEvt.containsKey(currentElement.getReferenceObject().toString().replaceAll(" ", ""))) {

                                List<String> recieveEvt = sendEvt.get(currentElement.getReferenceObject().toString().replaceAll(" ", ""));

                                for (String vertex : g.vertexSet()) {

                                    String[] vertexName = vertex.split("__");

                                    for (String n : recieveEvt) {

                                        if (vertexName.length >= 3) {

                                            if ((n.replaceAll(" ", "").equals((vertexName[2].toString().replaceAll(" ", ""))))) {

                                                HashSet<String> waitEventVertex = new HashSet<String>();
                                                waitEventVertex.add(vertex);

                                                if (sendEventWaitEventEdges.containsKey(eventName)) {

                                                    if (!sendEventWaitEventEdges.get(eventName).contains(vertex)) {
                                                        sendEventWaitEventEdges.get(eventName).add(vertex);
                                                    }

                                                } else {

                                                    sendEventWaitEventEdges.put(eventName, waitEventVertex);

                                                }
                                            }
                                        }
                                    }

                                }

                            }

                        }

                        if (currentElement.getReferenceObject() instanceof TMLADWaitEvent) {

                            if (waitEvt.containsKey(currentElement.getReferenceObject().toString().replaceAll(" ", ""))) {

                                List<String> sendevent = waitEvt.get(currentElement.getReferenceObject().toString().replaceAll(" ", ""));

                                for (String vertex : g.vertexSet()) {

                                    String[] vertexName = vertex.split("__");

                                    for (String n : sendevent) {
                                        if (vertexName.length >= 3) {

                                            if ((n.replaceAll(" ", "").equals((vertexName[2].toString().replaceAll(" ", ""))))) {

                                                HashSet<String> waitEventVertex = new HashSet<String>();
                                                waitEventVertex.add(eventName);

                                                if (sendEventWaitEventEdges.containsKey(vertex)) {
                                                    if (!sendEventWaitEventEdges.get(vertex).contains(eventName)) {

                                                        sendEventWaitEventEdges.get(vertex).add(eventName);
                                                    }

                                                } else {

                                                    sendEventWaitEventEdges.put(vertex, waitEventVertex);

                                                }
                                            }
                                        }
                                    }

                                }

                            }

                        }

                        if (currentElement.getReferenceObject() instanceof TMLADWriteChannel) {

                            String[] name = eventName.split("__");

                            String[] removewrite = name[2].split(":");

                            String[] portname = removewrite[1].split("[(]");

                            String chwriteName = (name[0] + "__" + portname[0]).replaceAll(" ", "");

                            String portNameNoSpaces = portname[0].replaceAll(" ", "");

                            if (sendData.containsKey(portNameNoSpaces)) {

                                String sendDatachannels = name[0] + "__" + portNameNoSpaces + "__" + name[0] + "__" + sendData.get(portNameNoSpaces);

                                HashSet<String> writeChVertex = new HashSet<String>();
                                writeChVertex.add(sendDatachannels);

                                if (readWriteChannelEdges.containsKey(eventName)) {

                                    if (!readWriteChannelEdges.get(eventName).contains(sendDatachannels)) {
                                        readWriteChannelEdges.get(eventName).add(sendDatachannels);
                                    }

                                } else {

                                    readWriteChannelEdges.put(eventName, writeChVertex);

                                }

                            }

                            else {
                                HashSet<String> writeChVertex = new HashSet<String>();
                                writeChVertex.add(chwriteName);

                                if (readWriteChannelEdges.containsKey(eventName)) {

                                    if (!readWriteChannelEdges.get(eventName).contains(chwriteName)) {
                                        readWriteChannelEdges.get(eventName).add(chwriteName);
                                    }

                                } else {

                                    readWriteChannelEdges.put(eventName, writeChVertex);

                                }
                            }

                        }

                        if (currentElement.getReferenceObject() instanceof TMLADReadChannel) {

                            String[] name = eventName.split("__");

                            String[] removewrite = name[2].split(":");

                            String[] portname = removewrite[1].split("[(]");

                            String chwriteName = (name[0] + "__" + portname[0]).replaceAll(" ", "");

                            String portNameNoSpaces = portname[0].replaceAll(" ", "");

                            if (receiveData.containsKey(portNameNoSpaces)) {

                                String sendDatachannels = name[0] + "__" + receiveData.get(portNameNoSpaces) + "__" + name[0] + "__"
                                        + portNameNoSpaces;

                                HashSet<String> readChVertex = new HashSet<String>();
                                readChVertex.add(sendDatachannels);

                                if (readWriteChannelEdges.containsKey(eventName)) {

                                    if (!readWriteChannelEdges.get(eventName).contains(sendDatachannels)) {
                                        readWriteChannelEdges.get(eventName).add(sendDatachannels);
                                    }

                                } else {

                                    readWriteChannelEdges.put(eventName, readChVertex);

                                }

                                /*
                                 * if (g.containsVertex(chwriteName))
                                 * 
                                 * { g.addEdge(chwriteName, eventName); }
                                 */

                            } else {
                                HashSet<String> readChVertex = new HashSet<String>();
                                readChVertex.add(eventName);

                                if (readWriteChannelEdges.containsKey(eventName)) {

                                    if (!readWriteChannelEdges.get(eventName).contains(chwriteName)) {
                                        readWriteChannelEdges.get(eventName).add(chwriteName);
                                    }

                                } else {

                                    readWriteChannelEdges.put(chwriteName, readChVertex);

                                }
                            }

                        }
                    }

                    // check if the next activity :add to an array:
                    // in case of for loop : the first element of inside/outside branches of loop
                    // in case of sequence: add first element of all branches

                    if (currentElement.getNexts().size() == 1) {

                        currentElement = currentElement.getNexts().firstElement();

                    } else if (!multiNexts.isEmpty()) {

                        if (currentElement.getReferenceObject() instanceof TMLADForStaticLoop
                                || currentElement.getReferenceObject() instanceof TMLADForLoop) {

                            if (currentElement.getNexts().size() > 1) {

                                List<TGConnectingPoint> points = new ArrayList<TGConnectingPoint>();
                                List<TGConnector> getOutputConnectors = new ArrayList<TGConnector>();
                                if (currentElement.getReferenceObject() instanceof TMLADForStaticLoop) {
                                    points = Arrays.asList(((TMLADForStaticLoop) (currentElement.getReferenceObject())).getConnectingPoints());

                                    getOutputConnectors = ((TMLADForStaticLoop) (currentElement

                                            .getReferenceObject())).getOutputConnectors();
                                } else if (currentElement.getReferenceObject() instanceof TMLADForLoop) {
                                    points = Arrays.asList(((TMLADForLoop) (currentElement.getReferenceObject())).getConnectingPoints());

                                    getOutputConnectors = ((TMLADForLoop) (currentElement

                                            .getReferenceObject())).getOutputConnectors();
                                }

                                TGConnector inputConnector = null, outputConnector = null;

                                for (TGConnector connector : getOutputConnectors) {

                                    if (connector.getTGConnectingPointP1() == points.get(1)) {
                                        inputConnector = connector;

                                    } else if (connector.getTGConnectingPointP1() == points.get(2)) {
                                        outputConnector = connector;
                                    }

                                }

                                List<String> afterloopActivity = new ArrayList<String>(2);

                                String insideLoop = "", outsideLoop = "";

                                for (TMLActivityElement ae : currentElement.getNexts()) {

                                    List<TGConnector> cg = (((TGComponent) ae.getReferenceObject()).getInputConnectors());

                                    for (TGConnector afterloopcg : cg) {

                                        if (afterloopcg == inputConnector) {

                                            insideLoop = task.getName() + "__" + ae.getReferenceObject().toString() + "__" + ae.getID();

                                        } else if (afterloopcg == outputConnector) {
                                            outsideLoop = task.getName() + "__" + ae.getReferenceObject().toString() + "__" + ae.getID();

                                        }
                                    }

                                }

                                afterloopActivity.add(0, insideLoop);
                                afterloopActivity.add(1, outsideLoop);
                                forLoopNextValues.put(eventName, afterloopActivity);

                            }

                        } else if (currentElement.getReferenceObject() instanceof TMLADSequence) {

                            String nextEventName = "";

                            for (TMLActivityElement seqListnextElement : currentElement.getNexts()) {
                                if (seqListnextElement.getReferenceObject() instanceof TMLADRandom) {
                                    nextEventName = task.getName() + "__" + seqListnextElement.getName() + "__" + seqListnextElement.getID();

                                } else {
                                    nextEventName = task.getName() + "__" + seqListnextElement.getReferenceObject().toString() + "__"
                                            + seqListnextElement.getID();

                                }

                                if (orderedSequenceList.containsKey(eventName)) {
                                    if (!orderedSequenceList.get(eventName).contains(nextEventName)) {
                                        orderedSequenceList.get(eventName).add(nextEventName);
                                    }
                                } else {
                                    ArrayList<String> seqListNextValues = new ArrayList<String>();
                                    seqListNextValues.add(nextEventName);
                                    orderedSequenceList.put(eventName, seqListNextValues);
                                }

                            }

                        } else if (currentElement.getReferenceObject() instanceof TMLADUnorderedSequence) {

                            String nextEventName = "";

                            for (TMLActivityElement seqListnextElement : currentElement.getNexts()) {
                                if (seqListnextElement.getReferenceObject() instanceof TMLADRandom) {
                                    nextEventName = task.getName() + "__" + seqListnextElement.getName() + "__" + seqListnextElement.getID();

                                } else {
                                    nextEventName = task.getName() + "__" + seqListnextElement.getReferenceObject().toString() + "__"
                                            + seqListnextElement.getID();

                                }

                                if (unOrderedSequenceList.containsKey(eventName)) {
                                    if (!unOrderedSequenceList.get(eventName).contains(nextEventName)) {
                                        unOrderedSequenceList.get(eventName).add(nextEventName);
                                    }
                                } else {
                                    ArrayList<String> seqListNextValues = new ArrayList<String>();
                                    seqListNextValues.add(nextEventName);
                                    unOrderedSequenceList.put(eventName, seqListNextValues);
                                }

                            }

                        }

                        List<TGConnector> cg = (((TGComponent) currentElement.getReferenceObject()).getInputConnectors());

                        currentElement = multiNexts.get(0);

                        multiNexts.remove(0);

                    }

                }

            }

        }

        return cpuTaskMap;
    }

    // get graph size
    public int getGraphsize() {
        return g.vertexSet().size();
    }

    // get graph size
    public int getGraphEdgeSet() {
        return g.edgeSet().size();
    }

    // open graph in a frame
    public void showGraph() {

        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    // save graph in .graphml format
    public void exportGraph(String filename) throws ExportException, IOException {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        GraphMLExporter<String, DefaultEdge> gmlExporter = new GraphMLExporter();

        ComponentNameProvider<String> vertexIDProvider = new ComponentNameProvider<String>() {

            @Override
            public String getName(String vertex) {
                // TODO Auto-generated method stub
                vertex = vertex.replaceAll("\\s+", "");
                vertex = vertex.replaceAll("\\(", "\\u0028");
                vertex = vertex.replaceAll("\\)", "\\u0029");
                return vertex;
            }

        };

        ComponentNameProvider<String> vertexNameProvider = new ComponentNameProvider<String>() {

            @Override
            public String getName(String vertex) {
                // TODO Auto-generated method stub
                return vertex;
            }
        };

        ComponentNameProvider<DefaultEdge> edgeIDProvider = new ComponentNameProvider<DefaultEdge>() {

            @Override
            public String getName(DefaultEdge edge) {
                String source = g.getEdgeSource(edge).replaceAll("\\s+", "");
                source = source.replaceAll("\\(", "\\u0028");
                source = source.replaceAll("\\)", "\\u0029");

                // .replaceAll("\\(", "");
                // source.replaceAll("\\)", "");

                String target = g.getEdgeTarget(edge).replaceAll("\\s+", "");
                target = target.replaceAll("\\(", "\\u0028");
                target = target.replaceAll("\\)", "\\u0029");
                // target.replaceAll("\\(", "");
                // target.replaceAll("\\)", "");
                // TODO Auto-generated method stub
                return source + target;
            }
        };

        ComponentNameProvider<DefaultEdge> edgeLabelProvider = new ComponentNameProvider<DefaultEdge>() {

            @Override
            public String getName(DefaultEdge edge) {
                // TODO Auto-generated method stub
                return Double.toString(g.getEdgeWeight(edge));
            }
        };

        GraphMLExporter<String, DefaultEdge> exporter = new GraphMLExporter<String, DefaultEdge>(vertexIDProvider, vertexNameProvider, edgeIDProvider,
                edgeLabelProvider);

        Writer fileWriter;

        FileWriter PS = new FileWriter(filename + ".graphml");
        // gmlExporter.exportGraph(g, PS);

        // FileWriter PS2 = new FileWriter(filename + "test.graphml");

        exporter.exportGraph(g, PS);

    }

    // save graph frame in .png format
    public void exportGraphAsImage(String filename) throws ExportException, IOException {

        Container c = frame.getContentPane();
        BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        c.paint(im.getGraphics());
        ImageIO.write(im, "PNG", new File(filename + ".png"));

    }

    // return all vertices that can be checked for latency
    // used to fill drop down
    public Vector<String> getLatencyVertices() {

        return allLatencyTasks;
    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    // fill the main table of the latency frame by checking all the delay times
    // between the selected tasks
    public Object[][] latencyDetailedAnalysis(String task12, String task22, Vector<SimulationTransaction> transFile1) {

        transFile = transFile1;

        // AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String,
        // DefaultEdge>(g);

        String message = "";

        String[] task1 = task12.split("__");

        int task1index = task1.length;

        idTask1 = task1[task1index - 1];

        String[] task2 = task22.split("__");

        int task2index = task2.length;

        idTask2 = task2[task2index - 1];

        Vector<SimulationTransaction> Task1Traces = new Vector<SimulationTransaction>();
        Vector<SimulationTransaction> Task2Traces = new Vector<SimulationTransaction>();

        GraphPath<String, DefaultEdge> path2 = DijkstraShortestPath.findPathBetween(g, task12, task22);

        // List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(task12,
        // task22, false, 100);

        // int size = path.size();
        times1.clear();
        times2.clear();

        // message = "there exists " +path.size()+" between: " + task12 + " and " +
        // task22;

        for (SimulationTransaction st : transFile1) {

            if (st.id.equals(idTask1)) {
                Task1Traces.add(st);
                task1DeviceName = st.deviceName;
                times1.add(Integer.valueOf(st.startTime));
                Collections.sort(times1);

            }

            if (st.id.equals(idTask2)) {
                Task2Traces.add(st);
                task2DeviceName = st.deviceName;
                times2.add(Integer.valueOf(st.endTime));
                Collections.sort(times1);
            }

        }
        // one to one
        int minIndex = 0;
        if (times1.size() != times2.size()) {
            minIndex = Math.min(times1.size(), times2.size());
        } else {
            minIndex = times1.size();
        }

        dataByTask = new Object[minIndex][7];
        dataByTaskBYRow = new Object[minIndex][2];
        dataByTaskHWBYRow = new Object[minIndex][2];
        for (int i = 0; i < minIndex; i++) {

            HashMap<String, ArrayList<SimulationTransaction>> relatedHWs = new HashMap<String, ArrayList<SimulationTransaction>>();
            HashMap<String, ArrayList<SimulationTransaction>> relatedTasks = new HashMap<String, ArrayList<SimulationTransaction>>();
            relatedsimTraces = new Vector<SimulationTransaction>();
            delayDueTosimTraces = new Vector<SimulationTransaction>();

            for (SimulationTransaction st : transFile1) {
                Boolean onPath = false;

                if (Integer.valueOf(st.startTime) >= times1.get(i) && Integer.valueOf(st.endTime) <= times2.get(i)) {

                    String taskname = "";

                    for (String tasknameCheck : g.vertexSet()) {

                        String[] taskToAdd = tasknameCheck.replaceAll(" ", "").split("__");

                        int taskToAddindex = taskToAdd.length;

                        String taskToAddid = taskToAdd[taskToAddindex - 1];
                        if (isNumeric(taskToAddid)) {

                            if (Integer.valueOf(taskToAddid).equals(Integer.valueOf(st.id))) {

                                taskname = tasknameCheck;
                                break;

                            }

                        }

                    }
                    // there is a path between task 1 and task 2
                    if (path2 != null && path2.getLength() > 0) {
                        if (!taskname.equals(null) && !taskname.equals("")) {

                            GraphPath<String, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, task12, taskname);

                            GraphPath<String, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g, taskname, task22);

                            if (taskname.equals(task12) || taskname.equals(task22) || (pathToOrigin != null && pathToOrigin.getLength() > 0
                                    && pathToDestination != null && pathToDestination.getLength() > 0)) {

                                relatedsimTraces.add(st);
                                ArrayList<Integer> timeValues = new ArrayList<Integer>();
                                timeValues.add(0, Integer.valueOf(st.runnableTime));
                                timeValues.add(1, Integer.valueOf(st.startTime));

                                if (!(st.runnableTime).equals(st.startTime)) {

                                    if (runnableTimePerDevice.containsKey(st.deviceName)) {

                                        if (!runnableTimePerDevice.get(st.deviceName).contains(timeValues)) {
                                            runnableTimePerDevice.get(st.deviceName).add(timeValues);
                                        }
                                    } else {

                                        ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                        timeValuesList.add(timeValues);

                                        runnableTimePerDevice.put(st.deviceName, timeValuesList);

                                    }

                                }
                            }

                            else if (((st.deviceName.equals(task2DeviceName)) || st.deviceName.equals(task1DeviceName)) && !st.id.equals(idTask1)
                                    && !st.id.equals(idTask2)) {
                                delayDueTosimTraces.add(st);

                            }

                        }

                    } else {
                        if (!taskname.equals(null) && !taskname.equals("")) {

                            GraphPath<String, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath.findPathBetween(g, task12, taskname);

                            GraphPath<String, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath.findPathBetween(g, taskname, task22);

                            if (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                    || pathExistsTestwithTask2 != null && pathExistsTestwithTask2.getLength() > 0) {
                                relatedsimTraces.add(st);

                            } else if (((st.deviceName.equals(task2DeviceName)) || st.deviceName.equals(task1DeviceName)) && !st.id.equals(idTask1)
                                    && !st.id.equals(idTask2)) {
                                delayDueTosimTraces.add(st);

                            }

                        }

                    }

                }
            }

            dataByTask[i][0] = task12;
            dataByTask[i][1] = times1.get(i);
            dataByTask[i][2] = task22;
            dataByTask[i][3] = times2.get(i);

            dataByTask[i][4] = times2.get(i) - times1.get(i);
            dataByTask[i][5] = "";

            dataByTaskR.put(i, relatedsimTraces);
            dataBydelayedTasks.put(i, delayDueTosimTraces);
            timeDelayedPerRow.put(i, runnableTimePerDevice);
            // dataByTask[i][5] = list.getModel();
            // dataByTask[i][6] = totalTime;

        }

        return dataByTask;

    }

    // fill the detailed latency table once a row is selected
    public Object[][] getTaskByRowDetails(int row) {

        Object[][] dataByTaskRowDetails = new Object[dataByTaskR.get(row).size()][5];

        int i = 0;

        for (SimulationTransaction st : dataByTaskR.get(row)) {
            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);

            dataByTaskRowDetails[i][2] = st.deviceName;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);
            i++;
        }

        return dataByTaskRowDetails;
    }

    // fill the detailed latency table once a row is selected
    public List<SimulationTransaction> getRowDetailsTaks(int row) {

        return dataByTaskR.get(row);
    }

    // fill the detailed latency table once a row is selected
    public List<SimulationTransaction> getRowDetailsByHW(int row) {

        return dataBydelayedTasks.get(row);
    }

    // fill the detailed latency table once a row is selected
    public HashMap<String, ArrayList<ArrayList<Integer>>> getRowDelayDetailsByHW(int row) {

        return timeDelayedPerRow.get(row);
    }

    // fill the detailed latency table once a row is selected from min/max table
    public Vector<SimulationTransaction> getMinMaxTasksByRow(int row) {

        return relatedsimTraces;
    }

    // fill the tasks that run on the same hardware but don't belong to the path
    // between selected activities
    public Vector<SimulationTransaction> getTaskMinMaxHWByRowDetails(int row) {

        return delayDueTosimTraces;
    }

    // get the details of the delay for a selected min or max delay row
    public void getRowDetailsMinMax(int row) {

        relatedsimTraces = new Vector<SimulationTransaction>();
        delayDueTosimTraces = new Vector<SimulationTransaction>();

        String task12 = (String) dataByTaskMinMax[row][0];
        int minTime = (int) dataByTaskMinMax[row][1];
        String task22 = (String) dataByTaskMinMax[row][2];
        int maxTime = (int) dataByTaskMinMax[row][3];

        HashMap<String, ArrayList<SimulationTransaction>> relatedHWs = new HashMap<String, ArrayList<SimulationTransaction>>();
        HashMap<String, ArrayList<SimulationTransaction>> relatedTasks = new HashMap<String, ArrayList<SimulationTransaction>>();
        relatedsimTraces = new Vector<SimulationTransaction>();
        delayDueTosimTraces = new Vector<SimulationTransaction>();

        // AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String,
        // DefaultEdge>(g);

        // List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(task12,
        // task22, false, g.vertexSet().size());

        // int size = path.size();

        GraphPath<String, DefaultEdge> path2 = DijkstraShortestPath.findPathBetween(g, task12, task22);

        for (SimulationTransaction st : transFile) {
            Boolean onPath = false;

            if (Integer.valueOf(st.startTime) >= minTime && Integer.valueOf(st.endTime) <= maxTime) {

                String taskname = "";

                for (String tasknameCheck : g.vertexSet()) {
                    String[] taskToAdd = tasknameCheck.split("__");

                    int taskToAddindex = taskToAdd.length;

                    String taskToAddid = taskToAdd[taskToAddindex - 1];
                    if (isNumeric(taskToAddid)) {
                        if (Integer.valueOf(taskToAddid).equals(Integer.valueOf(st.id))) {

                            taskname = tasknameCheck;

                            break;

                        }

                    }
                }
                // there is a path between task 1 and task 2
                if (path2 != null && path2.getLength() > 0) {
                    if (!taskname.equals(null) && !taskname.equals("")) {

                        GraphPath<String, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, task12, taskname);

                        GraphPath<String, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g, taskname, task22);

                        if (taskname.equals(task12) || taskname.equals(task22) || (pathToOrigin != null && pathToOrigin.getLength() > 0
                                && pathToDestination != null && pathToDestination.getLength() > 0)) {

                            relatedsimTraces.add(st);

                        }

                        else if (((st.deviceName.equals(task2DeviceName)) || st.deviceName.equals(task1DeviceName)) && !st.id.equals(idTask1)
                                && !st.id.equals(idTask2)) {
                            delayDueTosimTraces.add(st);

                        }

                    }

                } else {
                    if (!taskname.equals(null) && !taskname.equals("")) {

                        GraphPath<String, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath.findPathBetween(g, task12, taskname);

                        GraphPath<String, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath.findPathBetween(g, taskname, task22);

                        if (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                || pathExistsTestwithTask2 != null && pathExistsTestwithTask2.getLength() > 0) {
                            relatedsimTraces.add(st);

                        } else if (((st.deviceName.equals(task2DeviceName)) || st.deviceName.equals(task1DeviceName)) && !st.id.equals(idTask1)
                                && !st.id.equals(idTask2)) {
                            delayDueTosimTraces.add(st);

                        }

                    }

                }

            }
        }

    }

    // fill the tasks that run on the same hardware but don't belong to the path
    // between selected activities
    public Object[][] getTaskHWByRowDetails(int row) {

        Object[][] dataByTaskRowDetails = new Object[dataBydelayedTasks.get(row).size()][6];

        int i = 0;

        for (SimulationTransaction st : dataBydelayedTasks.get(row)) {

            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);
            dataByTaskRowDetails[i][2] = st.deviceName;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);

            HashMap<String, ArrayList<ArrayList<Integer>>> delayTime = timeDelayedPerRow.get(row);

            boolean causeDelay = false;

            if (delayTime.containsKey(st.deviceName)) {

                for (Entry<String, ArrayList<ArrayList<Integer>>> entry : delayTime.entrySet()) {
                    if (entry.getKey().equals(st.deviceName)) {
                        ArrayList<ArrayList<Integer>> timeList = entry.getValue();

                        for (int j = 0; j < timeList.size(); j++) {

                            if (Integer.valueOf(st.startTime) > timeList.get(j).get(0) && Integer.valueOf(st.startTime) < timeList.get(j).get(1)) {

                                causeDelay = true;

                            }
                        }

                    }

                }

            }

            dataByTaskRowDetails[i][5] = causeDelay;

            i++;
        }

        return dataByTaskRowDetails;
    }

    // fill the Min max delay table on main latency analysis frame
    public Object[][] latencyMinMaxAnalysis(String task12, String task22, Vector<SimulationTransaction> transFile1) {
        List<Integer> times1MinMAx = new ArrayList<Integer>();
        List<Integer> times2MinMAx = new ArrayList<Integer>();

        times1MinMAx = times1;
        times2MinMAx = times2;

        HashMap<Integer, ArrayList<Integer>> minTimes = new HashMap<Integer, ArrayList<Integer>>();

        for (int time1 : times1MinMAx) {
            int match = Integer.MAX_VALUE;
            // Find the first subsequent transaction
            int time = Integer.MAX_VALUE;
            for (int time2 : times2MinMAx) {

                int diff = time2 - time1;
                if (diff < time && diff >= 0) {
                    time = diff;
                    match = time2;
                }
            }
            try {
                if (times2MinMAx.contains(match)) {
                    times2MinMAx.remove(Integer.valueOf(match));
                }
            } catch (Exception e) {
            }
            if (time != Integer.MAX_VALUE) {
                ArrayList<Integer> startEndT = new ArrayList<Integer>();
                startEndT.add(time1);
                startEndT.add(match);
                minTimes.put(time, startEndT);
            }
        }
        dataByTaskMinMax = new Object[2][5];

        if (minTimes.size() > 0) {
            Integer min = Collections.min(minTimes.keySet());
            Integer max = Collections.max(minTimes.keySet());

            dataByTaskMinMax = new Object[2][5];

            ArrayList<Integer> numMax = minTimes.get(max);
            ArrayList<Integer> numMin = minTimes.get(min);

            dataByTaskMinMax[0][0] = task12;
            dataByTaskMinMax[0][1] = numMin.get(0);
            dataByTaskMinMax[0][2] = task22;
            dataByTaskMinMax[0][3] = numMin.get(1);
            dataByTaskMinMax[0][4] = min;

            dataByTaskMinMax[1][0] = task12;
            dataByTaskMinMax[1][1] = numMax.get(0);
            dataByTaskMinMax[1][2] = task22;
            dataByTaskMinMax[1][3] = numMax.get(1);
            dataByTaskMinMax[1][4] = max;

        }

        return dataByTaskMinMax;
    }

    // fill the detailed latency table once a row is selected from min/max table
    public Object[][] getTasksByRowMinMax(int row) {
        Object[][] dataByTaskRowDetails = new Object[relatedsimTraces.size()][5];

        int i = 0;

        for (SimulationTransaction st : relatedsimTraces) {

            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);
            dataByTaskRowDetails[i][2] = st.deviceName;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);
            i++;
        }

        return dataByTaskRowDetails;
    }

    // fill the tasks that run on the same hardware but don't belong to the path
    // between selected activities
    public Object[][] getTaskHWByRowDetailsMinMax(int row) {
        Object[][] dataByTaskRowDetails = new Object[delayDueTosimTraces.size()][5];

        int i = 0;

        for (SimulationTransaction st : delayDueTosimTraces) {

            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);
            dataByTaskRowDetails[i][2] = st.deviceName;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);
            i++;
        }

        return dataByTaskRowDetails;
    }

    // import graph in .graphml format
    public void importGraph(String filename) throws ExportException, IOException, ImportException {

        FileReader ps = new FileReader(filename + ".graphml");
        // gmlExporter.exportGraph(g, PS);

        // FileWriter PS2 = new FileWriter(filename + "test.graphml");

        VertexProvider<String> vertexProvider = (id, attributes) -> {
            String cv = new String(id);
            return cv;
        };

        EdgeProvider<String, DefaultEdge> edgeProvider = (from, to, label, attributes) -> new DefaultEdge();

        GraphMLImporter<String, DefaultEdge> importer = new GraphMLImporter<String, DefaultEdge>(vertexProvider, edgeProvider);

        Graph<String, DefaultEdge> importedGraph = null;

        importer.importGraph(importedGraph, ps);

    }

}