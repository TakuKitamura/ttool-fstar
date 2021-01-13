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

package ui.simulationtraceanalysis;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;

import avatartranslator.AvatarStateMachineElement;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmltranslator.*;
import tmltranslator.tomappingsystemc2.DiploSimulatorCodeGenerator;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TGConnector;
import ui.TMLComponentDesignPanel;
import ui.ad.TADComponentWithSubcomponents;
import ui.ad.TADComponentWithoutSubcomponents;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmlad.*;
import ui.tmlcompd.TMLCPrimitivePort;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class DirectedGraphTranslator: this class generate the directed graph
 * equivalent for the sysml model
 * <p>
 * 23/09/2019
 *
 * @author Maysam Zoor
 */
public class DirectedGraphTranslator extends JApplet {

    private TMLTask taskAc, task1, task2;

    private TMLActivity activity;
    private int nodeNbProgressBar = 0;
    private int nodeNb = 0;

    // List<HwNode> path;

    private TMLActivityElement currentElement;
    private TMLActivityElement backwardElement;
    private ArrayList<String> SummaryCommMapping;

    private Graph<vertex, DefaultEdge> g;

    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    private List<TMLComponentDesignPanel> cpanels;

    private final List<HwLink> links;
    private final TMLMapping<TGComponent> tmap;
    private final HashMap<String, String> addedEdges = new HashMap<String, String>();
    private final HashMap<String, HashSet<String>> sendEventWaitEventEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> readWriteChannelEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> writeReadChannelEdges = new HashMap<String, HashSet<String>>();

    private final HashMap<String, HashSet<String>> forkreadEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> forkwriteEdges = new HashMap<String, HashSet<String>>();

    private final HashMap<String, HashSet<String>> joinreadEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> joinwriteEdges = new HashMap<String, HashSet<String>>();

    private final HashMap<String, HashSet<String>> sequenceEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, ArrayList<String>> orderedSequenceList = new HashMap<String, ArrayList<String>>();
    private final HashMap<String, HashSet<String>> unOrderedSequenceEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, ArrayList<String>> unOrderedSequenceList = new HashMap<String, ArrayList<String>>();
    private final List<String> forEverLoopList = new ArrayList<String>();
    private final HashMap<String, List<TMLTask>> requests = new HashMap<String, List<TMLTask>>();

    private final HashMap<String, HashSet<String>> requestEdges = new HashMap<String, HashSet<String>>();

    private final HashMap<String, List<String>> requestsOriginDestination = new HashMap<String, List<String>>();
    private final HashMap<String, List<String>> requestsPorts = new HashMap<String, List<String>>();

    private final HashMap<String, List<String>> requestsDestination = new HashMap<String, List<String>>();
    private final Vector<String> allLatencyTasks = new Vector<String>();

    private static JScrollPane scrollPane = new JScrollPane();

    // List<String,String> = new ArrayList<String,String>();

    private final HashMap<String, String> nameIDTaskList = new HashMap<String, String>();

    private final HashMap<String, ArrayList<String>> channelPaths = new HashMap<String, ArrayList<String>>();

    private Object[][] dataByTask = null;
    private Object[][] dataByTaskMinMax = null;
    private Object[][] dataByTaskBYRow;
    private Object[][] dataByTaskHWBYRow;

    private HashMap<Integer, Vector<SimulationTransaction>> dataByTaskR = new HashMap<Integer, Vector<SimulationTransaction>>();
    private HashMap<Integer, List<SimulationTransaction>> dataBydelayedTasks = new HashMap<Integer, List<SimulationTransaction>>();
    private HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>> timeDelayedPerRow = new HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>>();

    private HashMap<Integer, List<String>> detailsOfMinMaxRow = new HashMap<Integer, List<String>>();
    private HashMap<Integer, List<SimulationTransaction>> dataBydelayedTasksOfMinMAx = new HashMap<Integer, List<SimulationTransaction>>();
    private final JFrame frame = new JFrame("The SysML Model As Directed Graph");

    private List<Integer> times1 = new ArrayList<Integer>();
    private List<Integer> times2 = new ArrayList<Integer>();

    private Vector<SimulationTransaction> transFile;
    private String idTask1;
    private String idTask2;
    private String task2DeviceName = "";
    private String task1DeviceName = "";
    private String task2CoreNbr = "";
    private String task1CoreNbr = "";
    private ArrayList<String> devicesToBeConsidered = new ArrayList<String>();

    private Vector<SimulationTransaction> relatedsimTraces = new Vector<SimulationTransaction>();
    private Vector<SimulationTransaction> delayDueTosimTraces = new Vector<SimulationTransaction>();

    private HashMap<String, ArrayList<SimulationTransaction>> relatedsimTraceswithTaint = new HashMap<String, ArrayList<SimulationTransaction>>();

    private JFrameLatencyDetailedAnalysis frameLatencyDetailedAnalysis;
    private JFrameCompareLatencyDetail frameCompareLatencyDetail;
    private int callingFrame;
    private int nbOfNodes = 0;

    private List<String> usedLabels = new ArrayList<String>();
    private List<String> warnings = new ArrayList<String>();

    private static Random random = new Random();

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String data = CHAR_LOWER + CHAR_UPPER;
    // List<vertex> gVertecies = new ArrayList<vertex>();

    private HashMap<String, ArrayList<ArrayList<Integer>>> runnableTimePerDevice = new HashMap<String, ArrayList<ArrayList<Integer>>>();

    private HashMap<String, List<String>> allForLoopNextValues = new HashMap<String, List<String>>();
    private HashMap<vertex, List<vertex>> allChoiceValues = new HashMap<vertex, List<vertex>>();
    private HashMap<vertex, List<vertex>> allSelectEvtValues = new HashMap<vertex, List<vertex>>();

    private HashMap<vertex, List<vertex>> allSeqValues = new HashMap<vertex, List<vertex>>();
    private HashMap<vertex, List<vertex>> allRandomSeqValues = new HashMap<vertex, List<vertex>>();

    private String taintLabel = "";

    private Vector<String> readChannelTransactions = new Vector<String>();
    private Vector<String> writeChannelTransactions = new Vector<String>();
    private HashMap<vertex, List<vertex>> ruleAddedEdges = new HashMap<vertex, List<vertex>>();

    private HashMap<vertex, List<vertex>> ruleAddedEdgesChannels = new HashMap<vertex, List<vertex>>();
    private HashMap<String, Integer> cpuIDs = new HashMap<String, Integer>();
    private HashMap<String, Integer> fpgaIDs = new HashMap<String, Integer>();
    private HashMap<String, List<String>> forLoopNextValues = new HashMap<String, List<String>>();

    private int opCount;

    private HashMap<String, List<String>> sendEvt = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> waitEvt = new HashMap<String, List<String>>();

    private HashMap<String, String> sendData = new HashMap<String, String>();
    private HashMap<String, String> receiveData = new HashMap<String, String>();
    private String taskStartName = "";

    private List<SimulationTransaction> onPath = new ArrayList<SimulationTransaction>();
    private List<SimulationTransaction> offPath = new ArrayList<SimulationTransaction>();
    private List<SimulationTransaction> offPathDelay = new ArrayList<SimulationTransaction>();

    private static final String selectEventparam = "SelectEvent params:";
    private static final String selectEvent = "SelectEvent";
    private static final String transactions = "Transactions";
    private static final String mandatory = "Mandatory";
    private static final String operatorLabel = "operator";
    private static final String startTimeLabel = "starttime";
    private static final String endTimeLabel = "endtime";
    private static final String lengthLabel = "length";
    private static final String noContentionLabel = "NoContention";
    private static final String contentionLabel = "Contention";
    private static final String idLabel = "id";
    private static final String latencyTableLabel = "LatencyTable";
    private static final String latencyLabel = "Latency";
    private static final String rowLabel = "Row";
    private static final String minLabel = "Min";
    private static final String maxLabel = "Max";
    private static final String op1 = "op1";
    private static final String op2 = "op2";
    private static final String stLAbel = "st";
    private static final String waitLabel = "Wait";
    private static final String waitReqLabel = "Wait reqChannel_";
    private static final String getReqArgLabel = "getReqArg";
    private static final String waitSt = "Wait: ";
    private static final String waitEvent = "wait event: ";

    @SuppressWarnings("deprecation")
    public DirectedGraphTranslator(JFrameLatencyDetailedAnalysis jFrameLatencyDetailedAnalysis, JFrameCompareLatencyDetail jframeCompareLatencyDetail,
            TMLMapping<TGComponent> tmap1, List<TMLComponentDesignPanel> cpanels1, int i) {

        tmap = tmap1;
        setCpanels(cpanels1);

        links = tmap.getTMLArchitecture().getHwLinks();

        // tmlcdp = getCpanels().get(0);

        callingFrame = i;

        if (callingFrame == 0)

        {
            frameLatencyDetailedAnalysis = jFrameLatencyDetailedAnalysis;
        } else if (callingFrame == 1) {
            frameCompareLatencyDetail = jframeCompareLatencyDetail;

        }
        DrawDirectedGraph();

    }

    // The main function to add the vertices and edges according to the model

    public vertex vertex(String name, int id) {
        // TODO Auto-generated method stub

        vertex v = new vertex(name, id);
        return v;
    }

    private void DrawDirectedGraph() {
        nodeNbProgressBar = 0;

        nodeNbProgressBar = tmap.getArch().getBUSs().size() + tmap.getArch().getHwBridge().size() + tmap.getArch().getHwA().size()
                + tmap.getArch().getMemories().size() + tmap.getArch().getCPUs().size();

        expectedNumberofVertex();

        if (callingFrame == 0)

        {
            frameLatencyDetailedAnalysis.getPbar().setMaximum(nodeNbProgressBar);
            frameLatencyDetailedAnalysis.getPbar().setMinimum(0);

        }
        if (callingFrame == 1)

        {
            frameCompareLatencyDetail.getPbar().setMaximum(nodeNbProgressBar);
            frameCompareLatencyDetail.getPbar().setMinimum(0);
        }

        nbOfNodes = 0;

        // HashMap<String, HashSet<String>> cpuTasks;
        HashMap<String, HashSet<TMLElement>> buschannel = new HashMap<String, HashSet<TMLElement>>();
        HashMap<String, HashSet<TMLElement>> memorychannel = new HashMap<String, HashSet<TMLElement>>();

        HashMap<String, HashSet<TMLElement>> bridgechannel = new HashMap<String, HashSet<TMLElement>>();

        // HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String,
        // HashSet<TMLTask>>();

        g = new DefaultDirectedGraph<>(DefaultEdge.class);

        buschannel = addBUSs();
        bridgechannel = addBridge();
        addHwAs();
        memorychannel = addMemories();
        addBuschannel(buschannel);
        addBridgechannel(bridgechannel);
        addMemorychannel(memorychannel);
        addUnmappedchannel();
        addCPUs();
        addLinkEdges();
        addFlowEdges();
        addSendEventWaitEventEdges();
        addReadWriteChannelEdges();
        addForkreadEdges();
        addJoinreadEdges();
        addWriteReadChannelEdges();
        addSeqEdges();
        addunOrderedSeqEdges();
        addRequestEdges();

    }

    private void addUnmappedchannel() {
        // TODO Auto-generated method stub
        DiploSimulatorCodeGenerator gen = new DiploSimulatorCodeGenerator(tmap);
        for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
            List<HwCommunicationNode> pathNodes = gen.determineRoutingPath(tmap.getHwNodeOf(ch.getOriginTask()),
                    tmap.getHwNodeOf(ch.getDestinationTask()), ch);

            if (!g.vertexSet().contains(getvertex(ch.getName()))) {
                vertex v2 = vertex(ch.getName(), ch.getID());
                g.addVertex(v2);
                updateMainBar();
                // gVertecies.add(vertex(ch.getName()));
                getvertex(ch.getName()).setType(vertex.TYPE_CHANNEL);
                getvertex(ch.getName()).setTaintFixedNumber(0);
                updateMainBar();

            }

            if (!pathNodes.isEmpty()) {
                for (HwCommunicationNode node : pathNodes) {

                    if (channelPaths.containsKey(ch.getName())) {
                        if (!channelPaths.get(ch.getName()).contains(node.getName())) {
                            channelPaths.get(ch.getName()).add(node.getName());
                        }
                    } else {
                        ArrayList<String> pathNodeNames = new ArrayList<String>();
                        pathNodeNames.add(node.getName());
                        channelPaths.put(ch.getName(), pathNodeNames);
                    }
                    vertex v1 = vertex(node.getName(), node.getID());

                    vertex v2 = vertex(ch.getName(), ch.getID());

                    if (!g.containsEdge(v1, v2)) {
                        g.addEdge(v1, v2);
                    }

                }

            }

        }

        SummaryCommMapping = tmap.getSummaryCommMapping();

    }

    private void addCPUs() {
        // TODO Auto-generated method stub
        HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String, HashSet<TMLTask>>();
        HashMap<String, HashSet<String>> cpuTasks;
        for (HwNode node : tmap.getArch().getCPUs()) {
            cpuTask = new HashMap<String, HashSet<TMLTask>>();
            cpuIDs.put(node.getName(), node.getID());
            if (tmap.getLisMappedTasks(node).size() > 0) {

                cpuTask.put(node.getName(), tmap.getLisMappedTasks(node));

            }
            if (cpuTask.size() > 0) {
                cpuTasks = getCPUTaskMap(cpuTask);
            }

        }

    }

    private void addMemorychannel(HashMap<String, HashSet<TMLElement>> memorychannel) {
        // TODO Auto-generated method stub
        for (Entry<String, HashSet<TMLElement>> entry : memorychannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();

            for (TMLElement busCh : busChList) {

                String ChannelName = busCh.getName();
                vertex v = vertex(ChannelName, busCh.getID());

                if (!g.containsVertex(getvertex(ChannelName))) {
                    g.addVertex(v);
                    updateMainBar();
                    getvertex(ChannelName).setType(vertex.TYPE_CHANNEL);
                    // gVertecies.add(vertex(ChannelName));
                    getvertex(ChannelName).setTaintFixedNumber(0);
                    updateMainBar();

                }

                g.addEdge(getvertex(busName), getvertex(ChannelName));
            }

        }

    }

    private void addBridgechannel(HashMap<String, HashSet<TMLElement>> bridgechannel) {
        // TODO Auto-generated method stub

        for (Entry<String, HashSet<TMLElement>> entry : bridgechannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();

            for (TMLElement busCh : busChList) {

                String ChannelName = busCh.getName();
                vertex v = vertex(ChannelName, busCh.getID());

                if (!g.containsVertex(getvertex(ChannelName))) {
                    g.addVertex(v);
                    updateMainBar();
                    getvertex(ChannelName).setType(vertex.TYPE_CHANNEL);
                    // gVertecies.add(vertex(ChannelName));

                    getvertex(ChannelName).setTaintFixedNumber(0);

                    updateMainBar();

                }

                g.addEdge(getvertex(busName), getvertex(ChannelName));
            }

        }

    }

    private void addBuschannel(HashMap<String, HashSet<TMLElement>> buschannel) {
        // TODO Auto-generated method stub
        for (Entry<String, HashSet<TMLElement>> entry : buschannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();

            for (TMLElement busCh : busChList) {

                String ChannelName = busCh.getName();

                vertex v = vertex(ChannelName, busCh.getID());

                if (!g.containsVertex(v)) {
                    g.addVertex(v);
                    updateMainBar();
                    getvertex(ChannelName).setType(vertex.TYPE_CHANNEL);
                    // gVertecies.add(vertex(ChannelName));
                    getvertex(ChannelName).setTaintFixedNumber(0);
                    updateMainBar();

                }

                g.addEdge(getvertex(busName), getvertex(ChannelName));

                // TMLChannel tmlch = (TMLChannel) busCh;

                // String writeChannel = tmlch.getDestinationTask().getName() + "__" +
                // "writechannel:" + tmlch.getDestinationPort();
                // String readChannel;

            }

        }

    }

    private HashMap<String, HashSet<TMLElement>> addMemories() {

        HashMap<String, HashSet<TMLElement>> memorychannel = new HashMap<String, HashSet<TMLElement>>();
        for (HwNode node : tmap.getArch().getMemories()) {

            vertex v = vertex(node.getName(), node.getID());

            if (!g.containsVertex(v)) {
                g.addVertex(v);
                updateMainBar();

            }

            if (tmap.getLisMappedChannels(node).size() > 0) {
                memorychannel.put(node.getName(), tmap.getLisMappedChannels(node));

            }

        }

        return memorychannel;
    }

    private void addHwAs() {
        // TODO Auto-generated method stub
        HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String, HashSet<TMLTask>>();
        HashMap<String, HashSet<String>> cpuTasks;
        for (HwA node : tmap.getArch().getHwA()) {

            cpuTask = new HashMap<String, HashSet<TMLTask>>();
            cpuIDs.put(node.getName(), node.getID());

            if (tmap.getLisMappedTasks(node).size() > 0) {

                cpuTask.put(node.getName(), tmap.getLisMappedTasks(node));

            }
            if (cpuTask.size() > 0) {
                cpuTasks = getCPUTaskMap(cpuTask);
            }

        }

    }

    private HashMap<String, HashSet<TMLElement>> addBridge() {
        // TODO Auto-generated method stub

        HashMap<String, HashSet<TMLElement>> bridgechannel = new HashMap<String, HashSet<TMLElement>>();

        for (HwNode node : tmap.getArch().getHwBridge()) {

            vertex v = vertex(node.getName(), node.getID());

            if (!g.containsVertex(v)) {
                g.addVertex(v);

                updateMainBar();

            }

            if (tmap.getLisMappedChannels(node).size() > 0) {
                bridgechannel.put(node.getName(), tmap.getLisMappedChannels(node));

            }

        }
        return bridgechannel;

    }

    private HashMap<String, HashSet<TMLElement>> addBUSs() {
        // TODO Auto-generated method stub
        HashMap<String, HashSet<TMLElement>> buschannel = new HashMap<String, HashSet<TMLElement>>();
        for (HwNode node : tmap.getArch().getBUSs()) {

            vertex v = vertex(node.getName(), node.getID());

            if (!g.containsVertex(v)) {
                g.addVertex(v);

                updateMainBar();

            }

            if (tmap.getLisMappedChannels(node).size() > 0) {
                buschannel.put(node.getName(), tmap.getLisMappedChannels(node));

            }

        }

        return buschannel;

    }

    private void addLinkEdges() {
        // TODO Auto-generated method stub
        for (HwLink link : links) {

            vertex vlink1 = vertex(link.hwnode.getName(), link.hwnode.getID());
            vertex vlink2 = vertex(link.bus.getName(), link.bus.getID());

            if (g.containsVertex(getvertex(link.hwnode.getName())) && g.containsVertex(getvertex(link.bus.getName()))) {

                g.addEdge(vlink1, vlink2);
                g.addEdge(vlink2, vlink1);
            }

        }

    }

    private void addFlowEdges() {
        // TODO Auto-generated method stub
        if (addedEdges.size() > 0) {
            for (Entry<String, String> edge : addedEdges.entrySet()) {
                g.addEdge(getvertex(edge.getKey()), getvertex(edge.getValue()));

            }
        }

    }

    private void addSendEventWaitEventEdges() {
        // TODO Auto-generated method stub
        if (sendEventWaitEventEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : sendEventWaitEventEdges.entrySet()) {

                for (String waitEventEdge : edge.getValue())

                    g.addEdge(getvertex(edge.getKey()), getvertex(waitEventEdge));

            }
        }

    }

    private void addReadWriteChannelEdges() {
        // TODO Auto-generated method stub
        if (readWriteChannelEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : readWriteChannelEdges.entrySet()) {

                for (String readChannelEdge : edge.getValue()) {

                    g.addEdge(getvertex(edge.getKey()), getvertex(readChannelEdge));

                    getvertex(edge.getKey()).setTaintFixedNumber(getvertex(edge.getKey()).getTaintFixedNumber() + 1);

                }
            }
        }

    }

    private void addForkreadEdges() {
        // TODO Auto-generated method stub
        if (forkreadEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : forkreadEdges.entrySet()) {

                HashSet<String> writech = forkwriteEdges.get(edge.getKey());

                for (String readChannelEdge : edge.getValue()) {

                    for (String wch : writech) {

                        g.addEdge(getvertex(readChannelEdge), getvertex(wch));

                    }
                }
            }
        }
    }

    // draw the vertices and edges for the tasks mapped to the CPUs

    private void addJoinreadEdges() {
        // TODO Auto-generated method stub
        if (joinreadEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : joinreadEdges.entrySet()) {

                HashSet<String> writech = joinwriteEdges.get(edge.getKey());

                for (String readChannelEdge : edge.getValue()) {

                    for (String wch : writech) {

                        g.addEdge(getvertex(readChannelEdge), getvertex(wch));

                    }
                }
            }
        }

    }

    private void addWriteReadChannelEdges() {
        // TODO Auto-generated method stub
        if (writeReadChannelEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : writeReadChannelEdges.entrySet()) {

                for (String readChannelEdge : edge.getValue()) {

                    g.addEdge(getvertex(edge.getKey()), getvertex(readChannelEdge));
                    getvertex(readChannelEdge).setTaintFixedNumber(getvertex(readChannelEdge).getTaintFixedNumber() + 1);

                }
            }

        }

    }

    private void addRequestEdges() {
        // TODO Auto-generated method stub
        if (requestEdges.size() > 0) {

            for (Entry<String, HashSet<String>> edge : requestEdges.entrySet()) {

                for (String requestsingleEdges : edge.getValue()) {

                    g.addEdge(getvertex(edge.getKey()), getvertex(requestsingleEdges));

                }

            }

        }

    }

    private void addunOrderedSeqEdges() {
        // TODO Auto-generated method stub
        if (unOrderedSequenceEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : unOrderedSequenceEdges.entrySet()) {

                for (String sequenceEdge : edge.getValue())

                    g.addEdge(getvertex(edge.getKey()), getvertex(sequenceEdge));

            }
        }

    }

    private void addSeqEdges() {
        if (sequenceEdges.size() > 0) {
            for (Entry<String, HashSet<String>> edge : sequenceEdges.entrySet()) {

                for (String sequenceEdge : edge.getValue())

                    g.addEdge(getvertex(edge.getKey()), getvertex(sequenceEdge));

            }
        }

    }

    private void expectedNumberofVertex() {
        for (HwA node : tmap.getArch().getHwA()) {

            if (tmap.getLisMappedTasks(node).size() > 0) {

                nodeNbProgressBar = tmap.getLisMappedTasks(node).size() + nodeNbProgressBar;

                for (TMLTask task : tmap.getLisMappedTasks(node)) {

                    for (TMLActivityElement ae : task.getActivityDiagram().getElements()) {

                        if (ae.getName().equals("Stop after infinite loop")) {

                        } else {
                            nodeNbProgressBar++;

                        }

                    }

                }

            }

        }

        for (HwNode node : tmap.getArch().getCPUs()) {

            if (tmap.getLisMappedTasks(node).size() > 0) {

                nodeNbProgressBar = tmap.getLisMappedTasks(node).size() + nodeNbProgressBar;

                for (TMLTask task : tmap.getLisMappedTasks(node)) {

                    for (TMLActivityElement ae : task.getActivityDiagram().getElements()) {

                        if (ae.getName().equals("Stop after infinite loop")) {

                        } else {
                            nodeNbProgressBar++;

                        }

                    }

                }

            }
        }
        HashSet<String> mappedcomm = new HashSet<String>();

        for (HwNode node : tmap.getArch().getBUSs()) {

            if (tmap.getLisMappedChannels(node).size() > 0) {
                for (TMLElement entry : tmap.getLisMappedChannels(node)) {

                    if (!mappedcomm.contains(entry.getName())) {
                        mappedcomm.add(entry.getName());
                        nodeNbProgressBar++;

                    }

                }
            }

        }

        for (HwNode node : tmap.getArch().getHwBridge()) {

            if (tmap.getLisMappedChannels(node).size() > 0) {

                for (TMLElement entry : tmap.getLisMappedChannels(node)) {
                    if (!mappedcomm.contains(entry.getName())) {
                        mappedcomm.add(entry.getName());
                        nodeNbProgressBar++;

                    }

                }
            }

        }

        for (HwNode node : tmap.getArch().getMemories()) {

            if (tmap.getLisMappedChannels(node).size() > 0) {
                for (TMLElement entry : tmap.getLisMappedChannels(node)) {
                    if (!mappedcomm.contains(entry.getName())) {
                        mappedcomm.add(entry.getName());
                        nodeNbProgressBar++;

                    }

                }

            }

        }

        for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {

            if (!mappedcomm.contains(ch.getName())) {
                mappedcomm.add(ch.getName());

                nodeNbProgressBar++;

            }

        }

    }

    private void updateMainBar() {

        nbOfNodes++;

        if (callingFrame == 0)

        {
            frameLatencyDetailedAnalysis.updateBar(nbOfNodes);
        } else if (callingFrame == 1) {

            frameCompareLatencyDetail.updateBar(nbOfNodes);

        }

    }

    public HashMap<String, HashSet<String>> getCPUTaskMap(HashMap<String, HashSet<TMLTask>> cpuTask) {

        HashMap<String, HashSet<String>> cpuTaskMap = new HashMap<String, HashSet<String>>();
        if (tmap == null) {
            return cpuTaskMap;
        }

        for (Entry<String, HashSet<TMLTask>> entry : cpuTask.entrySet()) {

            String key = entry.getKey();
            int keyID = cpuIDs.get(key);

            HashSet<TMLTask> value = entry.getValue();
            Vector<TMLActivityElement> multiNexts = new Vector<TMLActivityElement>();

            // Map <String, String> sendEvt;
            sendEvt = new HashMap<String, List<String>>();
            waitEvt = new HashMap<String, List<String>>();

            sendData = new HashMap<String, String>();
            receiveData = new HashMap<String, String>();

            // HashMap<String, List<String>> sendEvt = new HashMap<String, List<String>>();

            // GEt List of all requests

            requestedTask(value);

            for (TMLTask task : value) {
                int taskID = task.getID();
                String taskName = task.getName();
                // get the names and params of send events per task and their corresponding wait
                // events
                taskAc = task;
                sendEventsNames();

                // get the names of read channels per task and their corresponding write
                // channels

                readChannelNames();

                // get the names of write channels per task and their corresponding read
                // channels

                writeChannelNames();

                // get the names and params of wait events per task and their corresponding send
                // events
                waitEventNames();

                // add the name of the task as a vertex

                if (!g.vertexSet().contains(getvertex(key))) {
                    g.addVertex(vertex(key, keyID));
                    updateMainBar();
                }
                if (!g.vertexSet().contains(getvertex(taskName))) {
                    g.addVertex(vertex(taskName, taskID));
                    updateMainBar();
                }

                g.addEdge(getvertex(key), getvertex(taskName));

                activity = task.getActivityDiagram();
                opCount = 1;
                currentElement = activity.getFirst();
                taskStartName = "";
                // int taskStartid;

                forLoopNextValues = new HashMap<String, List<String>>();

                // loop over all the activites corresponding to a task
                while (opCount <= activity.nElements()) {

                    String eventName = null;

                    // int eventid = currentElement.getID();

                    if (currentElement.getName().equals("Stop after infinite loop")) {
                        opCount++;
                        updateMainBar();
                        if (opCount <= activity.nElements()) {
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
                    } else if (currentElement.getName().equals("startOfFork") || currentElement.getName().equals("junctionOfFork")
                            || currentElement.getName().equals("startOfJoin") || currentElement.getName().equals("junctionOfJoin")) {
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();

                        continue;
                    } else if (taskName.startsWith("FORKTASK_S_") && currentElement.getName().equals("ReadOfFork")) {

                        String name = ((TMLReadChannel) (currentElement)).getChannel(0).getName();
                        int id = ((TMLReadChannel) (currentElement)).getChannel(0).getID();
                        if (!g.containsVertex(getvertex(name))) {
                            g.addVertex(vertex(name, id));
                            updateMainBar();

                        }

                        g.addEdge(getvertex(taskName), getvertex(((TMLReadChannel) (currentElement)).getChannel(0).getName()));

                        HashSet<String> readForkVertex = new HashSet<String>();
                        readForkVertex.add(((TMLReadChannel) (currentElement)).getChannel(0).getName());

                        if (forkreadEdges.containsKey(taskName)) {

                            if (!forkreadEdges.get(taskName).contains(((TMLReadChannel) (currentElement)).getChannel(0).getName())) {
                                forkreadEdges.get(taskName).add(((TMLReadChannel) (currentElement)).getChannel(0).getName());
                            }

                        } else {

                            forkreadEdges.put(taskName, readForkVertex);

                        }

                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();

                        continue;

                    } else if (taskName.startsWith("FORKTASK_S_") && currentElement.getName().startsWith("WriteOfFork_S")) {

                        String vName = ((TMLWriteChannel) (currentElement)).getChannel(0).getName();
                        int vid = ((TMLWriteChannel) (currentElement)).getChannel(0).getID();

                        vertex v = getvertex(vName);
                        if (!g.containsVertex(v)) {
                            g.addVertex(vertex(vName, vid));
                            updateMainBar();

                        }

                        HashSet<String> writeForkVertex = new HashSet<String>();
                        writeForkVertex.add(((TMLWriteChannel) (currentElement)).getChannel(0).getName());

                        if (forkwriteEdges.containsKey(taskName)) {

                            if (!forkwriteEdges.get(taskName).contains(((TMLWriteChannel) (currentElement)).getChannel(0).getName())) {
                                forkwriteEdges.get(taskName).add(((TMLWriteChannel) (currentElement)).getChannel(0).getName());
                            }

                        } else {

                            forkwriteEdges.put(taskName, writeForkVertex);

                        }

                        // g.addEdge(getvertex(taskName),getvertex(((TMLWriteChannel)(currentElement)).getChannel(0).getName()));

                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();

                        continue;

                    } else if (currentElement.getName().equals("stopOfFork") || currentElement.getName().equals("stop2OfFork")
                            || currentElement.getName().equals("stopOfJoin")) {
                        opCount++;
                        updateMainBar();
                        // currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith("JOINTASK_S_") && currentElement.getName().startsWith("ReadOfJoin")) {

                        String vName = ((TMLReadChannel) (currentElement)).getChannel(0).getName();
                        int vid = ((TMLReadChannel) (currentElement)).getChannel(0).getID();

                        if (!g.containsVertex(getvertex(vName))) {
                            g.addVertex(vertex(vName, vid));
                            updateMainBar();

                        }

                        HashSet<String> writeForkVertex = new HashSet<String>();
                        writeForkVertex.add(((TMLReadChannel) (currentElement)).getChannel(0).getName());

                        if (joinreadEdges.containsKey(taskName)) {

                            if (!joinreadEdges.get(taskName).contains(((TMLReadChannel) (currentElement)).getChannel(0).getName())) {
                                joinreadEdges.get(task.getName()).add(((TMLReadChannel) (currentElement)).getChannel(0).getName());
                            }

                        } else {

                            joinreadEdges.put(taskName, writeForkVertex);

                        }

                        // g.addEdge(getvertex(task.getName()),getvertex(((TMLWriteChannel)(currentElement)).getChannel(0).getName()));

                        opCount++;
                        updateMainBar();

                        currentElement = currentElement.getNexts().firstElement();

                        continue;
                    } else if (taskName.startsWith("JOINTASK_S_") && currentElement.getName().equals("WriteOfJoin")) {

                        String vName = ((TMLWriteChannel) (currentElement)).getChannel(0).getName();
                        int vid = ((TMLWriteChannel) (currentElement)).getChannel(0).getID();

                        if (!g.containsVertex(getvertex(vName))) {
                            g.addVertex(vertex(vName, vid));
                            updateMainBar();

                        }

                        g.addEdge(getvertex(taskName), getvertex(((TMLWriteChannel) (currentElement)).getChannel(0).getName()));

                        HashSet<String> readForkVertex = new HashSet<String>();
                        readForkVertex.add(((TMLWriteChannel) (currentElement)).getChannel(0).getName());

                        if (joinwriteEdges.containsKey(taskName)) {

                            if (!joinwriteEdges.get(taskName).contains(((TMLWriteChannel) (currentElement)).getChannel(0).getName())) {
                                joinwriteEdges.get(taskName).add(((TMLWriteChannel) (currentElement)).getChannel(0).getName());
                            }

                        } else {

                            joinwriteEdges.put(taskName, readForkVertex);

                        }

                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();

                        continue;

                    } else if (taskName.startsWith("FORKTASK_S") && currentElement.getName().startsWith("WriteEvtOfFork_S")) {

                        String vName = ((TMLSendEvent) (currentElement)).getEvent().getName();
                        int vid = ((TMLSendEvent) (currentElement)).getEvent().getID();

                        vertex v = getvertex(vName);
                        if (!g.containsVertex(v)) {
                            g.addVertex(vertex(vName, vid));
                            updateMainBar();

                        }

                        HashSet<String> writeForkVertex = new HashSet<String>();
                        writeForkVertex.add(((TMLSendEvent) (currentElement)).getEvent().getName());

                        if (forkwriteEdges.containsKey(taskName)) {

                            if (!forkwriteEdges.get(taskName).contains(((TMLSendEvent) (currentElement)).getEvent().getName())) {
                                forkwriteEdges.get(taskName).add(((TMLSendEvent) (currentElement)).getEvent().getName());
                            }

                        } else {

                            forkwriteEdges.put(taskName, writeForkVertex);

                        }

                        // g.addEdge(getvertex(taskName),getvertex(((TMLWriteChannel)(currentElement)).getChannel(0).getName()));

                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();

                        continue;

                    } else if (taskName.startsWith("FORKTASK_S") && currentElement.getName().equals("WaitOfFork")) {

                        String name = ((TMLWaitEvent) (currentElement)).getEvent().getName();
                        int id = ((TMLWaitEvent) (currentElement)).getEvent().getID();
                        if (!g.containsVertex(getvertex(name))) {
                            g.addVertex(vertex(name, id));
                            updateMainBar();

                        }

                        g.addEdge(getvertex(taskName), getvertex(((TMLWaitEvent) (currentElement)).getEvent().getName()));

                        HashSet<String> readForkVertex = new HashSet<String>();
                        readForkVertex.add(((TMLWaitEvent) (currentElement)).getEvent().getName());

                        if (forkreadEdges.containsKey(taskName)) {

                            if (!forkreadEdges.get(taskName).contains(((TMLWaitEvent) (currentElement)).getEvent().getName())) {
                                forkreadEdges.get(taskName).add(((TMLWaitEvent) (currentElement)).getEvent().getName());
                            }

                        } else {

                            forkreadEdges.put(taskName, readForkVertex);

                        }

                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();

                        continue;
                    }
                    if (currentElement.getNexts().size() > 1) {
                        for (TMLActivityElement ae : currentElement.getNexts()) {
                            multiNexts.add(ae);

                        }

                    }

                    eventName = getEventName(taskName, currentElement);

                    // in case an end was encountered , the previous activities should be checked:
                    // in
                    // case it is an end for a loop or sequence speavial edges should be added

                    if (currentElement.getReferenceObject() instanceof TMLADStopState) {

                        addStopVertex(taskName);

                    }

                    // start activity is added as a vertex
                    else if (currentElement.getReferenceObject() instanceof TMLADStartState) {

                        addStartVertex(taskName);

                    }

                    // the below activities are added as vertex with the required edges
                    // these activities can be used to check later for latency

                    else if (currentElement.getReferenceObject() instanceof TADComponentWithoutSubcomponents
                            || currentElement.getReferenceObject() instanceof TADComponentWithSubcomponents
                            || currentElement.getReferenceObject() instanceof TMLADActionState)

                    {

                        addcurrentElementVertex(taskName, taskStartName);

                    }

                    // check if the next activity :add to an array:
                    // in case of for loop : the first element of inside/outside branches of loop
                    // in case of sequence: add first element of all branches

                    if (currentElement.getNexts().size() == 1) {

                        currentElement = currentElement.getNexts().firstElement();

                    } else if (!multiNexts.isEmpty()) {

                        trackMultiNexts(taskName, eventName);
                        currentElement = multiNexts.get(0);

                        multiNexts.remove(0);

                    }

                    allForLoopNextValues.putAll(forLoopNextValues);

                }

            }

        }

        return cpuTaskMap;
    }

    private void trackMultiNexts(String taskName, String eventName) {
        // TODO Auto-generated method stub

        if (currentElement.getReferenceObject() instanceof TMLADForStaticLoop || currentElement.getReferenceObject() instanceof TMLADForLoop) {

            if (currentElement.getNexts().size() > 1) {

                List<TGConnectingPoint> points = new ArrayList<TGConnectingPoint>();
                List<TGConnector> getOutputConnectors = new ArrayList<TGConnector>();
                if (currentElement.getReferenceObject() instanceof TMLADForStaticLoop) {
                    points = Arrays.asList(((TMLADForStaticLoop) (currentElement.getReferenceObject())).getConnectingPoints());

                    getOutputConnectors = ((TMLADForStaticLoop) (currentElement.getReferenceObject())).getOutputConnectors();

                    String loopValue = ((TMLADForStaticLoop) (currentElement.getReferenceObject())).getValue();

                    getvertex(eventName).setType(vertex.TYPE_STATIC_FOR_LOOP);

                    if ((loopValue != null) && (loopValue.length() > 0)) {

                        if ((loopValue.matches("\\d*"))) {
                            getvertex(eventName).setTaintFixedNumber(Integer.valueOf(loopValue));
                        } else {
                            for (TMLAttribute att : taskAc.getAttributes()) {

                                if (loopValue.contains(att.getName())) {
                                    loopValue = loopValue.replace(att.getName(), (att.getInitialValue()));
                                }

                            }
                            getvertex(eventName).setTaintFixedNumber(Integer.valueOf(loopValue));

                        }
                    }

                } else if (currentElement.getReferenceObject() instanceof TMLADForLoop) {
                    points = Arrays.asList(((TMLADForLoop) (currentElement.getReferenceObject())).getConnectingPoints());

                    getOutputConnectors = ((TMLADForLoop) (currentElement.getReferenceObject())).getOutputConnectors();
                    // String loopValue = ((TMLADForLoop)
                    // (currentElement.getReferenceObject())).getValue();

                    getvertex(eventName).setType(vertex.TYPE_FOR_LOOP);
                    String cond = ((TMLADForLoop) (currentElement.getReferenceObject())).getCondition();

                    if (cond.contains("<=")) {

                        String[] val = cond.split("<=");

                        String loopValue = val[2].toString();

                        int loopVal = Integer.valueOf(loopValue);
                        if ((loopValue != null) && (loopValue.length() > 0)) {

                            if ((loopValue.matches("\\d*"))) {
                                getvertex(eventName).setTaintFixedNumber(loopVal);
                            } else {
                                for (TMLAttribute att : taskAc.getAttributes()) {

                                    if (loopValue.contains(att.getName())) {
                                        loopValue = loopValue.replace(att.getName(), (att.getInitialValue()));
                                    }

                                }

                                getvertex(eventName).setTaintFixedNumber(loopVal);

                            }
                        }

                    } else if (cond.contains("<")) {

                        String[] val = cond.split("<");

                        String loopValue = val[1].toString();
                        int loopVal = Integer.valueOf(loopValue);

                        if ((loopValue != null) && (loopValue.length() > 0)) {

                            if ((loopValue.matches("\\d*"))) {
                                getvertex(eventName).setTaintFixedNumber(loopVal);
                            } else {
                                for (TMLAttribute att : taskAc.getAttributes()) {

                                    if (loopValue.contains(att.getName())) {
                                        loopValue = loopValue.replace(att.getName(), (att.getInitialValue()));
                                    }

                                }
                                if ((loopValue.matches("\\d*"))) {
                                    getvertex(eventName).setTaintFixedNumber(loopVal);
                                }
                                {
                                    frameLatencyDetailedAnalysis.error(loopValue + " Expression in For Loop is not supported by Tainting");
                                }

                            }

                        }

                    }

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

                            if (ae.getReferenceObject() instanceof TMLADRandom) {

                                insideLoop = taskName + "__" + ae.getName() + "__" + ae.getID();

                            } else if (ae.getReferenceObject() instanceof TMLADUnorderedSequence) {

                                insideLoop = taskName + "__" + "unOrderedSequence" + "__" + ae.getID();

                            } else {

                                insideLoop = taskName + "__" + ae.getReferenceObject().toString() + "__" + ae.getID();

                            }

                        } else if (afterloopcg == outputConnector) {

                            if (ae.getReferenceObject() instanceof TMLADRandom) {

                                outsideLoop = taskName + "__" + ae.getName() + "__" + ae.getID();

                            } else if (ae.getReferenceObject() instanceof TMLADUnorderedSequence) {

                                outsideLoop = taskName + "__" + "unOrderedSequence" + "__" + ae.getID();

                            } else {

                                outsideLoop = taskName + "__" + ae.getReferenceObject().toString() + "__" + ae.getID();

                            }

                        }
                    }

                }

                afterloopActivity.add(0, insideLoop);
                afterloopActivity.add(1, outsideLoop);
                forLoopNextValues.put(eventName, afterloopActivity);

            }

        } else if (currentElement.getReferenceObject() instanceof TMLADSequence) {

            getvertex(eventName).setType(vertex.TYPE_SEQ);
            getvertex(eventName).setTaintFixedNumber(1);
            String nextEventName = "";

            for (TMLActivityElement seqListnextElement : currentElement.getNexts()) {
                if (seqListnextElement.getReferenceObject() instanceof TMLADRandom) {
                    nextEventName = taskName + "__" + seqListnextElement.getName() + "__" + seqListnextElement.getID();

                } else if (seqListnextElement.getReferenceObject() instanceof TMLADUnorderedSequence) {

                    nextEventName = taskName + "__" + "unOrderedSequence" + "__" + seqListnextElement.getID();

                } else {
                    nextEventName = taskName + "__" + seqListnextElement.getReferenceObject().toString() + "__" + seqListnextElement.getID();

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

            getvertex(eventName).setType(vertex.TYPE_UNORDER_SEQ);
            getvertex(eventName).setTaintFixedNumber(1);

            String nextEventName = "";

            for (TMLActivityElement seqListnextElement : currentElement.getNexts()) {
                if (seqListnextElement.getReferenceObject() instanceof TMLADRandom) {
                    nextEventName = taskName + "__" + seqListnextElement.getName() + "__" + seqListnextElement.getID();

                } else if (seqListnextElement.getReferenceObject() instanceof TMLADUnorderedSequence) {

                    nextEventName = taskName + "__" + "unOrderedSequence" + "__" + seqListnextElement.getID();

                } else {
                    nextEventName = taskName + "__" + seqListnextElement.getReferenceObject().toString() + "__" + seqListnextElement.getID();

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

    }

    private void addStartVertex(String taskName) {
        // TODO Auto-generated method stub
        taskStartName = taskName + "__" + currentElement.getName() + "__" + currentElement.getID();
        vertex startv = vertex(taskStartName, currentElement.getID());

        g.addVertex(startv);
        updateMainBar();
        // gVertecies.add(vertex(taskStartName));
        getvertex(taskStartName).setType(vertex.TYPE_START);
        getvertex(taskStartName).setTaintFixedNumber(1);
        g.addEdge(getvertex(taskName), getvertex(taskStartName));

        opCount++;

        if (!nameIDTaskList.containsKey(currentElement.getID())) {
            nameIDTaskList.put(String.valueOf(currentElement.getID()), taskStartName);

        }
    }

    private void waitEventNames() {
        // TODO Auto-generated method stub
        for (TMLWaitEvent waitEvent : taskAc.getWaitEvents()) {
            // TMLCPrimitivePort portdetails = waitEvent.getEvent().port;
            TMLCPrimitivePort sendingPortdetails = waitEvent.getEvent().port;
            TMLCPrimitivePort receivePortdetails = waitEvent.getEvent().port2;

            if (sendingPortdetails != null && !sendingPortdetails.isBlocking()) {
                warnings.add(
                        "Send event port:" + sendingPortdetails.getPortName() + " is non-blocking. Use tainting for an accurate latency analysis");
            }
            if (sendingPortdetails != null && sendingPortdetails.isFinite()) {
                warnings.add("Send event port:" + sendingPortdetails.getPortName() + " is Finite. Event lost is not supported in latency analysis ");
            }
            String receivePortparams = waitEvent.getAllParams();

            String[] checkchannel;

            String sendingDataPortdetails = "";
            String receiveDataPortdetails = "";

            if (sendingPortdetails != null && receivePortdetails != null) {
                waitEvt.put("waitevent:" + receivePortdetails.getPortName() + "(" + receivePortparams + ")", new ArrayList<String>());

                TMLTask originTasks = waitEvent.getEvent().getOriginTask();

                for (TMLSendEvent wait_sendEvent : originTasks.getSendEvents()) {

                    String sendingPortparams = wait_sendEvent.getAllParams();

                    waitEvt.get("waitevent:" + receivePortdetails.getPortName() + "(" + receivePortparams + ")")
                            .add("sendevent:" + sendingPortdetails.getPortName() + "(" + sendingPortparams + ")");

                }

            } else {
                String sendingPortparams = null;
                if (waitEvent.getEvent().getOriginPort().getName().contains("FORKPORTORIGIN")) {

                    checkchannel = waitEvent.getEvent().getOriginPort().getName().split("_S_");

                    if (checkchannel.length > 2) {
                        sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName().replace("FORKPORTORIGIN", "FORKEVENT");
                        sendingPortparams = waitEvent.getEvent().getParams().toString();

                    } else if (checkchannel.length <= 2) {

                        sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName().replace("FORKPORTORIGIN", "");

                        sendingDataPortdetails = sendingDataPortdetails.replace("_S_", "");
                        sendingPortparams = waitEvent.getEvent().getParams().toString();

                    }

                } else if (waitEvent.getEvent().getOriginPort().getName().contains("JOINPORTORIGIN")) {

                    checkchannel = waitEvent.getEvent().getOriginPort().getName().split("_S_");

                    if (checkchannel.length > 2) {
                        sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName().replace("JOINPORTORIGIN", "JOINEVENT");
                        sendingPortparams = waitEvent.getEvent().getParams().toString();

                    } else if ((checkchannel.length) <= 2) {
                        sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName().replace("JOINPORTORIGIN", "");

                        sendingDataPortdetails = sendingDataPortdetails.replace("_S_", "");
                        sendingPortparams = waitEvent.getEvent().getParams().toString();

                    }
                } else {
                    sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName();
                    sendingPortparams = waitEvent.getEvent().getParams().toString();

                }
                if (waitEvent.getEvent().getDestinationPort().getName().contains("FORKPORTDESTINATION")) {

                    checkchannel = waitEvent.getEvent().getDestinationPort().getName().split("_S_");

                    if (checkchannel.length > 2) {

                        receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName().replace("FORKPORTDESTINATION", "FORKEVENT");
                        receivePortparams = waitEvent.getEvent().getParams().toString();
                    } else if (checkchannel.length <= 2) {

                        receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName().replace("FORKPORTDESTINATION", "");

                        receiveDataPortdetails = receiveDataPortdetails.replace("_S_", "");
                        receivePortparams = waitEvent.getEvent().getParams().toString();
                    }

                } else if (waitEvent.getEvent().getDestinationPort().getName().contains("JOINPORTDESTINATION")) {

                    checkchannel = waitEvent.getEvent().getDestinationPort().getName().split("_S_");

                    if (checkchannel.length > 2) {

                        receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName().replace("JOINPORTDESTINATION", "JOINEVENT");
                    } else if (checkchannel.length <= 2) {

                        receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName().replace("JOINPORTDESTINATION", "");

                        receiveDataPortdetails = receiveDataPortdetails.replace("_S_", "");
                        receivePortparams = waitEvent.getEvent().getParams().toString();
                    }
                } else {
                    receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName();
                    receivePortparams = waitEvent.getEvent().getParams().toString();
                }

                if (sendingDataPortdetails != null && receiveDataPortdetails != null) {
                    waitEvt.put("waitevent:" + receiveDataPortdetails + "(" + receivePortparams + ")", new ArrayList<String>());

                    waitEvt.get("waitevent:" + receiveDataPortdetails + "(" + receivePortparams + ")")
                            .add("sendevent:" + sendingDataPortdetails + "(" + sendingPortparams + ")");

                }

            }

        }
    }

    private void writeChannelNames() {
        // TODO Auto-generated method stub

        for (TMLWriteChannel writeChannel : taskAc.getWriteChannels()) {

            int i = writeChannel.getNbOfChannels();

            for (int j = 0; j < i; j++) {

                String sendingDataPortdetails = "";
                String receiveDataPortdetails = "";

                if ((writeChannel.getChannel(j)).originalDestinationTasks.size() > 0) {
                    String[] checkchannel;

                    if (writeChannel.getChannel(j).getOriginPort().getName().contains("FORKPORTORIGIN")) {

                        checkchannel = writeChannel.getChannel(j).getOriginPort().getName().split("_S_");

                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName().replace("FORKPORTORIGIN", "FORKCHANNEL");
                            ;

                        } else if (checkchannel.length < 2) {

                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName().replace("FORKPORTORIGIN", "");
                            ;

                            sendingDataPortdetails = sendingDataPortdetails.replace("_S_", "");
                            ;

                        }

                    } else if (writeChannel.getChannel(j).getOriginPort().getName().contains("JOINPORTORIGIN")) {

                        checkchannel = writeChannel.getChannel(j).getOriginPort().getName().split("_S_");

                        if (checkchannel.length > 2) {

                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName().replace("JOINPORTORIGIN", "JOINCHANNEL");

                        } else if (checkchannel.length <= 2) {
                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName().replace("JOINPORTORIGIN", "");

                            sendingDataPortdetails = sendingDataPortdetails.replace("_S_", "");
                            ;

                        }

                    } else {
                        sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName();
                    }

                    if (writeChannel.getChannel(j).getDestinationPort().getName().contains("FORKPORTDESTINATION")) {

                        checkchannel = writeChannel.getChannel(j).getDestinationPort().getName().split("_S_");

                        if (checkchannel.length > 2) {

                            receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName().replace("FORKPORTDESTINATION",
                                    "FORKCHANNEL");
                        } else if (checkchannel.length <= 2) {

                            receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName().replace("FORKPORTDESTINATION", "");

                            receiveDataPortdetails = receiveDataPortdetails.replace("_S_", "");
                        }

                    } else if (writeChannel.getChannel(j).getDestinationPort().getName().contains("JOINPORTDESTINATION")) {

                        checkchannel = writeChannel.getChannel(j).getDestinationPort().getName().split("_S_");

                        if (checkchannel.length > 2) {

                            receiveDataPortdetails = "JOINCHANNEL_S_" + checkchannel[1] + "__" + checkchannel[2];

                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName().replace("JOINPORTDESTINATION", "");

                            receiveDataPortdetails = receiveDataPortdetails.replace("_S_", "");
                        }

                    } else {
                        receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName();

                    }

                } else {

                    // writeChannel.getChannel(j);
                    sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName();
                    receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName();
                }

                if (!sendingDataPortdetails.equals(receiveDataPortdetails)) {

                    sendData.put(sendingDataPortdetails, receiveDataPortdetails);
                }

            }
        }

    }

    private void readChannelNames() {
        // TODO Auto-generated method stub

        for (TMLReadChannel readChannel : taskAc.getReadChannels()) {

            int i = readChannel.getNbOfChannels();

            // name = _ch.getOriginPorts().get(0).getName(); //return the name of the source
            // port of the channel

            for (int j = 0; j < i; j++) {

                String sendingDataPortdetails = "";
                String receiveDataPortdetails = "";

                if ((readChannel.getChannel(j)).originalDestinationTasks.size() > 0) {

                    String[] checkchannel;
                    if (readChannel.getChannel(j).getOriginPort().getName().contains("FORKPORTORIGIN")) {

                        checkchannel = readChannel.getChannel(j).getOriginPort().getName().split("_S_");

                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName().replace("FORKPORTORIGIN", "FORKCHANNEL");

                        } else if (checkchannel.length <= 2) {

                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName().replace("FORKPORTORIGIN", "");

                            sendingDataPortdetails = sendingDataPortdetails.replace("_S_", "");
                            ;

                        }

                    } else if (readChannel.getChannel(j).getOriginPort().getName().contains("JOINPORTORIGIN")) {

                        checkchannel = readChannel.getChannel(j).getOriginPort().getName().split("_S_");

                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName().replace("JOINPORTORIGIN", "JOINCHANNEL");

                        } else if ((checkchannel.length) <= 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName().replace("JOINPORTORIGIN", "");

                            sendingDataPortdetails = sendingDataPortdetails.replace("_S_", "");

                        }
                    } else {
                        sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName();
                    }
                    if (readChannel.getChannel(j).getDestinationPort().getName().contains("FORKPORTDESTINATION")) {

                        checkchannel = readChannel.getChannel(j).getDestinationPort().getName().split("_S_");

                        if (checkchannel.length > 2) {

                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName().replace("FORKPORTDESTINATION",
                                    "FORKCHANNEL");
                        } else if (checkchannel.length <= 2) {

                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName().replace("FORKPORTDESTINATION", "");

                            receiveDataPortdetails = receiveDataPortdetails.replace("_S_", "");
                        }

                    } else if (readChannel.getChannel(j).getDestinationPort().getName().contains("JOINPORTDESTINATION")) {

                        checkchannel = readChannel.getChannel(j).getDestinationPort().getName().split("_S_");

                        if (checkchannel.length > 2) {

                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName().replace("JOINPORTDESTINATION",
                                    "JOINCHANNEL");
                        } else if (checkchannel.length <= 2) {

                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName().replace("JOINPORTDESTINATION", "");

                            receiveDataPortdetails = receiveDataPortdetails.replace("_S_", "");
                        }
                    } else {
                        receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName();
                    }
                } else {

                    sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName();
                    receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName();
                }

                if (!sendingDataPortdetails.equals(receiveDataPortdetails)) {
                    receiveData.put(receiveDataPortdetails, sendingDataPortdetails);

                }

                TMLCPrimitivePort sp = null, rp = null;

                if (readChannel.getChannel(j).getOriginPort().getReferenceObject() instanceof TMLCPrimitivePort) {

                    rp = (TMLCPrimitivePort) readChannel.getChannel(j).getOriginPort().getReferenceObject();

                }

                if (readChannel.getChannel(j).getOriginPort().getReferenceObject() instanceof TMLCPrimitivePort) {

                    sp = (TMLCPrimitivePort) readChannel.getChannel(j).getDestinationPort().getReferenceObject();

                }

                if (sp != null && rp != null) {

                    if (!sp.isBlocking() && !rp.isBlocking()) {
                        warnings.add("Send data port:" + sp.getPortName() + " and read data port:" + rp.getPortName()
                                + " are non-blocking. Use tainting for an accurate latency analysis.");

                    }
                }

            }

        }

    }

    private void sendEventsNames() {
        // TODO Auto-generated method stub
        for (TMLSendEvent sendEvent : taskAc.getSendEvents()) {

            // int i = sendEvent.getEvents().size();
            TMLCPrimitivePort sendingPortdetails = sendEvent.getEvent().port;
            TMLCPrimitivePort receivePortdetails = sendEvent.getEvent().port2;

            String sendingPortparams = sendEvent.getAllParams();

            TMLTask destinationTasks = sendEvent.getEvent().getDestinationTask();
            if (sendingPortdetails != null && receivePortdetails != null) {
                sendEvt.put("sendevent:" + sendingPortdetails.getPortName() + "(" + sendingPortparams + ")", new ArrayList<String>());

                for (TMLWaitEvent wait_sendEvent : destinationTasks.getWaitEvents()) {
                    String receivePortparams = wait_sendEvent.getAllParams();

                    sendEvt.get("sendevent:" + sendingPortdetails.getPortName() + "(" + sendingPortparams + ")")
                            .add("waitevent:" + receivePortdetails.getPortName() + "(" + receivePortparams + ")");

                }
            }

        }

    }

    private void requestedTask(HashSet<TMLTask> value) {
        // TODO Auto-generated method stub
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

    }

    private void addcurrentElementVertex(String taskName, String taskStartName) {
        // TODO Auto-generated method stub

        String preEventName;

        int preEventid;
        String eventName = getEventName(taskName, currentElement);

        int eventid = currentElement.getID();

        if (activity.getPrevious(currentElement).getReferenceObject() instanceof TMLADRandom) {
            preEventName = taskName + "__" + activity.getPrevious(currentElement).getName() + "__" + activity.getPrevious(currentElement).getID();
            preEventid = activity.getPrevious(currentElement).getID();

        } else if (activity.getPrevious(currentElement).getReferenceObject() instanceof TMLADUnorderedSequence) {

            preEventName = taskName + "__" + "unOrderedSequence" + "__" + activity.getPrevious(currentElement).getID();
            preEventid = activity.getPrevious(currentElement).getID();

        } else {
            preEventName = taskName + "__" + activity.getPrevious(currentElement).getReferenceObject().toString() + "__"
                    + activity.getPrevious(currentElement).getID();
            preEventid = activity.getPrevious(currentElement).getID();

        }

        /*
         * if (((activity.getPrevious(currentElement).getReferenceObject() instanceof
         * TMLADExecI || activity.getPrevious(currentElement).getReferenceObject()
         * instanceof TMLADExecC) &&
         * activity.getPrevious(currentElement).getValue().equals("0")) ||
         * ((activity.getPrevious(currentElement).getReferenceObject() instanceof
         * TMLADDelay) && ((TMLADDelay)
         * activity.getPrevious(currentElement).getReferenceObject()).getDelayValue().
         * equals("0"))
         * 
         * || ((activity.getPrevious(currentElement).getReferenceObject() instanceof
         * TMLADDelayInterval) && (((TMLADDelayInterval)
         * activity.getPrevious(currentElement).getReferenceObject()).getMinDelayValue()
         * .equals("0") && ((TMLADDelayInterval)
         * activity.getPrevious(currentElement).getReferenceObject()).getMaxDelayValue()
         * .equals("0")))
         * 
         * || ((activity.getPrevious(currentElement).getReferenceObject() instanceof
         * TMLADExecCInterval) && (((TMLADExecCInterval)
         * activity.getPrevious(currentElement).getReferenceObject()).getMinDelayValue()
         * .equals("0") && ((TMLADExecCInterval)
         * activity.getPrevious(currentElement).getReferenceObject()).getMaxDelayValue()
         * .equals("0"))
         * 
         * || ((activity.getPrevious(currentElement).getReferenceObject() instanceof
         * TMLADExecIInterval) && (((TMLADExecIInterval)
         * activity.getPrevious(currentElement).getReferenceObject()).getMinDelayValue()
         * .equals("0") && ((TMLADExecIInterval)
         * activity.getPrevious(currentElement).getReferenceObject()).getMaxDelayValue()
         * .equals("0")))))
         * 
         * {
         * 
         * if (activity.getPrevious(activity.getPrevious(currentElement)).
         * getReferenceObject() instanceof TMLADRandom) { preEventName = taskName + "__"
         * + activity.getPrevious(activity.getPrevious(currentElement)).getName() + "__"
         * + activity.getPrevious(activity.getPrevious(currentElement)).getID();
         * preEventid =
         * activity.getPrevious(activity.getPrevious(currentElement)).getID();
         * 
         * } else if (activity.getPrevious(activity.getPrevious(currentElement)).
         * getReferenceObject() instanceof TMLADUnorderedSequence) { preEventName =
         * taskName + "__" + "unOrderedSequence" + "__" +
         * activity.getPrevious(activity.getPrevious(currentElement)).getID();
         * preEventid =
         * activity.getPrevious(activity.getPrevious(currentElement)).getID();
         * 
         * } else { preEventName = taskName + "__" +
         * activity.getPrevious(activity.getPrevious(currentElement)).getReferenceObject
         * ().toString() + "__" +
         * activity.getPrevious(activity.getPrevious(currentElement)).getID();
         * preEventid =
         * activity.getPrevious(activity.getPrevious(currentElement)).getID();
         * 
         * }
         * 
         * }
         */

        if (!nameIDTaskList.containsKey(currentElement.getID())) {
            nameIDTaskList.put(String.valueOf(currentElement.getID()), eventName);
        }

        if (g.containsVertex(getvertex(preEventName))) {

            vertex v = vertex(eventName, eventid);

            vertex preV = vertex(preEventName, preEventid);

            g.addVertex(v);
            updateMainBar();
            g.addEdge(preV, v);
            opCount++;

        } else if ((activity.getPrevious(currentElement).getName().equals("start")) && g.containsVertex(getvertex(taskStartName))) {
            vertex v = vertex(eventName, eventid);

            g.addVertex(v);
            updateMainBar();
            // gVertecies.add(vertex(eventName));
            g.addEdge(getvertex(taskStartName), getvertex(eventName));
            opCount++;

        }

        if (currentElement.getReferenceObject() instanceof TMLADSendEvent || currentElement.getReferenceObject() instanceof TMLADWaitEvent
                || currentElement.getReferenceObject() instanceof TMLADSendRequest
                || currentElement.getReferenceObject() instanceof TMLADNotifiedEvent
                || currentElement.getReferenceObject() instanceof TMLADReadChannel || currentElement.getReferenceObject() instanceof TMLADWriteChannel
                || (currentElement.getReferenceObject() instanceof TMLADExecI) || (currentElement.getReferenceObject() instanceof TMLADExecC)
                || (currentElement.getReferenceObject() instanceof TMLADDelay) || (currentElement.getReferenceObject() instanceof TMLADDelayInterval)
                || (currentElement.getReferenceObject() instanceof TMLADExecCInterval)
                || (currentElement.getReferenceObject() instanceof TMLADExecIInterval) || currentElement.getReferenceObject() instanceof TMLADEncrypt
                || currentElement.getReferenceObject() instanceof TMLADDecrypt
                || currentElement.getReferenceObject() instanceof TMLADReadRequestArg) {

            allLatencyTasks.add(eventName);
            getvertex(eventName).setType(vertex.TYPE_TRANSACTION);
            getvertex(eventName).setTaintFixedNumber(1);

        } else if (currentElement.getReferenceObject() instanceof TMLADRandom) {
            getvertex(eventName).setType(vertex.TYPE_CTRL);
            getvertex(eventName).setTaintFixedNumber(1);
        } else if (currentElement.getReferenceObject() instanceof TMLADSelectEvt) {
            getvertex(eventName).setType(vertex.TYPE_SELECT_EVT);
            getvertex(eventName).setTaintFixedNumber(1);

        } else if (currentElement.getReferenceObject() instanceof TMLADActionState) {
            getvertex(eventName).setType(vertex.TYPE_CTRL);
            getvertex(eventName).setTaintFixedNumber(1);

        }

        if (currentElement.getReferenceObject() instanceof TMLADForEverLoop) {
            forEverLoopList.add(eventName);

            getvertex(eventName).setType(vertex.TYPE_FOR_EVER_LOOP);

            getvertex(eventName).setTaintFixedNumber(Integer.MAX_VALUE);

        }

        if (currentElement.getReferenceObject() instanceof TMLADChoice) {

            getvertex(eventName).setType(vertex.TYPE_CHOICE);
            getvertex(eventName).setTaintFixedNumber(1);

        }

        if (currentElement.getReferenceObject() instanceof TMLADSendRequest) {

            if (requestsOriginDestination.containsKey(taskName)) {

                for (String destinationTask : requestsOriginDestination.get(taskName)) {

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

                for (vertex vertex : g.vertexSet()) {

                    String[] vertexName = vertex.toString().split("__");

                    for (String n : recieveEvt) {

                        if (vertexName.length >= 3) {

                            if ((n.replaceAll(" ", "").equals((vertexName[2].toString().replaceAll(" ", ""))))) {

                                HashSet<String> waitEventVertex = new HashSet<String>();
                                waitEventVertex.add(vertex.toString());

                                if (sendEventWaitEventEdges.containsKey(eventName)) {

                                    if (!sendEventWaitEventEdges.get(eventName).contains(vertex.toString())) {
                                        sendEventWaitEventEdges.get(eventName).add(vertex.toString());
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

                for (vertex vertex : g.vertexSet()) {

                    String[] vertexName = vertex.toString().split("__");

                    for (String n : sendevent) {
                        if (vertexName.length >= 3) {

                            if ((n.replaceAll(" ", "").equals((vertexName[2].toString().replaceAll(" ", ""))))) {

                                HashSet<String> waitEventVertex = new HashSet<String>();
                                waitEventVertex.add(eventName);

                                if (sendEventWaitEventEdges.containsKey(vertex.toString())) {
                                    if (!sendEventWaitEventEdges.get(vertex.toString()).contains(eventName)) {

                                        sendEventWaitEventEdges.get(vertex.toString()).add(eventName);
                                    }

                                } else {

                                    sendEventWaitEventEdges.put(vertex.toString(), waitEventVertex);

                                }
                            }
                        }
                    }

                }

            }

        }

        if (currentElement.getReferenceObject() instanceof TMLADWriteChannel) {

            writeChannelTransactions.add(eventName);

            String[] name = eventName.split("__");

            String[] removewrite = name[2].split(":");

            String[] portname = removewrite[1].split("[(]");

            String chwriteName = (name[0] + "__" + portname[0]).replaceAll(" ", "");

            String portNameNoSpaces = portname[0].replaceAll(" ", "");

            if (sendData.containsKey(portNameNoSpaces)) {
                String sendDatachannels;

                if (((TMLWriteChannel) currentElement).getChannel(0).getName().contains("FORKCHANNEL")
                        || ((TMLWriteChannel) currentElement).getChannel(0).getDestinationTask().getName().startsWith("FORKTASK")
                        || ((TMLWriteChannel) currentElement).getChannel(0).getOriginTask().getName().startsWith("FORKTASK")
                        || ((TMLWriteChannel) currentElement).getChannel(0).getName().contains("JOINCHANNEL")
                        || ((TMLWriteChannel) currentElement).getChannel(0).getDestinationTask().getName().startsWith("JOINTASK")
                        || ((TMLWriteChannel) currentElement).getChannel(0).getOriginTask().getName().startsWith("JOINTASK")

                ) {
                    sendDatachannels = sendData.get(portNameNoSpaces);
                } else {
                    // sendDatachannels = name[0] + "__" + sendData.get(portNameNoSpaces) + "__" +
                    // name[0] + "__" + portNameNoSpaces;
                    sendDatachannels = name[0] + "__" + portNameNoSpaces + "__" + name[0] + "__" + sendData.get(portNameNoSpaces);
                }

                // String sendDatachannels = name[0] + "__" + portNameNoSpaces + "__" + name[0]
                // + "__" + sendData.get(portNameNoSpaces);

                // if (sendDatachannels.contains("FORKPORTORIGIN")) {
                // sendDatachannels= sendDatachannels.replace("FORKPORTORIGIN", "FORKCHANNEL");
//
                // }

                HashSet<String> writeChVertex = new HashSet<String>();
                writeChVertex.add(sendDatachannels);

                if (writeReadChannelEdges.containsKey(eventName)) {

                    if (!writeReadChannelEdges.get(eventName).contains(sendDatachannels)) {
                        writeReadChannelEdges.get(eventName).add(sendDatachannels);
                    }

                } else {

                    writeReadChannelEdges.put(eventName, writeChVertex);

                }
                // getvertex(sendDatachannels).setTaintFixedNumber(getvertex(sendDatachannels).getTaintFixedNumber()
                // + 1);

            }

            else {
                HashSet<String> writeChVertex = new HashSet<String>();
                writeChVertex.add(chwriteName);

                if (writeReadChannelEdges.containsKey(eventName)) {

                    if (!writeReadChannelEdges.get(eventName).contains(chwriteName)) {
                        writeReadChannelEdges.get(eventName).add(chwriteName);
                    }

                } else {

                    writeReadChannelEdges.put(eventName, writeChVertex);

                }

                // getvertex(chwriteName).setTaintFixedNumber(getvertex(chwriteName).getTaintFixedNumber()
                // + 1);
            }

        }

        if (currentElement.getReferenceObject() instanceof TMLADReadChannel) {

            readChannelTransactions.add(eventName);

            String[] name = eventName.split("__");

            String[] removewrite = name[2].split(":");

            String[] portname = removewrite[1].split("[(]");

            String chwriteName = (name[0] + "__" + portname[0]).replaceAll(" ", "");

            String portNameNoSpaces = portname[0].replaceAll(" ", "");

            if (receiveData.containsKey(portNameNoSpaces)) {
                String sendDatachannels;

                if (((TMLReadChannel) currentElement).getChannel(0).getName().contains("FORKCHANNEL")
                        || ((TMLReadChannel) currentElement).getChannel(0).getDestinationTask().getName().startsWith("FORKTASK")
                        || ((TMLReadChannel) currentElement).getChannel(0).getOriginTask().getName().startsWith("FORKTASK")
                        || ((TMLReadChannel) currentElement).getChannel(0).getName().contains("JOINCHANNEL")
                        || ((TMLReadChannel) currentElement).getChannel(0).getDestinationTask().getName().startsWith("JOINTASK")
                        || ((TMLReadChannel) currentElement).getChannel(0).getOriginTask().getName().startsWith("JOINTASK")) {
                    sendDatachannels = receiveData.get(portNameNoSpaces);
                } else {
                    sendDatachannels = name[0] + "__" + receiveData.get(portNameNoSpaces) + "__" + name[0] + "__" + portNameNoSpaces;

                }

                HashSet<String> readChVertex = new HashSet<String>();
                readChVertex.add(eventName);

                if (readWriteChannelEdges.containsKey(sendDatachannels)) {

                    if (!readWriteChannelEdges.get(sendDatachannels).contains(eventName)) {
                        readWriteChannelEdges.get(sendDatachannels).add(eventName);
                    }

                } else {

                    readWriteChannelEdges.put(sendDatachannels, readChVertex);

                }

                // getvertex(sendDatachannels).setTaintFixedNumber(getvertex(sendDatachannels).getTaintFixedNumber()
                // + 1);

                /*
                 * if (g.containsVertex(chwriteName))
                 * 
                 * { g.addEdge(chwriteName, eventName); }
                 */

            } else {
                HashSet<String> readChVertex = new HashSet<String>();
                readChVertex.add(eventName);

                if (readWriteChannelEdges.containsKey(chwriteName)) {

                    if (!readWriteChannelEdges.get(chwriteName).contains(eventName)) {
                        readWriteChannelEdges.get(chwriteName).add(eventName);
                    }

                } else {

                    readWriteChannelEdges.put(chwriteName, readChVertex);

                }

                //
            }

        }

    }

    private void addStopVertex(String taskName) {
        // TODO Auto-generated method stub

        String taskEndName = "";
        int taskEndid;
        int preEventid;
        String preEventName;
        String eventName = null;

        eventName = getEventName(taskName, currentElement);

        taskEndid = currentElement.getID();
        taskEndName = taskName + "__" + currentElement.getName() + "__" + taskEndid;

        preEventid = activity.getPrevious(currentElement).getID();
        if (activity.getPrevious(currentElement).getReferenceObject() instanceof TMLADRandom) {
            preEventName = taskName + "__" + activity.getPrevious(currentElement).getName() + "__" + preEventid;

        } else if (currentElement.getReferenceObject() instanceof TMLADUnorderedSequence) {

            preEventName = taskName + "__" + "unOrderedSequence" + "__" + preEventid;

        } else {
            preEventName = taskName + "__" + activity.getPrevious(currentElement).getReferenceObject().toString() + "__" + preEventid;

        }

        vertex taskEndVertex = vertex(taskEndName, taskEndid);

        g.addVertex(taskEndVertex);
        updateMainBar();
        // gVertecies.add(vertex(taskEndName));
        getvertex(eventName).setType(vertex.TYPE_END);
        getvertex(eventName).setTaintFixedNumber(1);
        // allTasks.add(taskEndName);

        if (!(activity.getPrevious(currentElement).getReferenceObject() instanceof TMLADSequence)) {
            g.addEdge(getvertex(preEventName), getvertex(taskEndName));

        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        AllDirectedPaths<vertex, DefaultEdge> allPaths = new AllDirectedPaths<vertex, DefaultEdge>(g);
        if (orderedSequenceList.size() > 0) {

            int noForLoop = 0;
            // get path from sequence to end
            for (Entry<String, ArrayList<String>> sequenceListEntry : orderedSequenceList.entrySet()) {

                int directlyConnectedSeq = 0;

                if (g.containsVertex(getvertex(sequenceListEntry.getKey()))) {

                    List<GraphPath<vertex, DefaultEdge>> path = allPaths.getAllPaths(getvertex(sequenceListEntry.getKey()), getvertex(taskEndName),
                            false, g.vertexSet().size());

                    for (Entry<String, ArrayList<String>> othersequenceListEntryValue : orderedSequenceList.entrySet()) {

                        for (int i = 0; i < path.size(); i++) {

                            if (!othersequenceListEntryValue.getKey().equals(sequenceListEntry.getKey())) {

                                if (path.get(i).getVertexList().contains(getvertex(othersequenceListEntryValue.getKey()))) {

                                    directlyConnectedSeq++;

                                }
                            }
                        }
                    }

                    if (path.size() > 0 && sequenceListEntry.getValue().size() > 0 && directlyConnectedSeq == 0) {

                        for (int i = 0; i < path.size(); i++) {

                            for (String sequenceListEntryValue : sequenceListEntry.getValue()) {

                                if (g.containsVertex(getvertex(sequenceListEntryValue))) {

                                    if (path.get(i).getVertexList().contains(getvertex(sequenceListEntryValue))) {

                                        if (forLoopNextValues.size() > 0) {

                                            for (Entry<String, List<String>> forloopListEntry : forLoopNextValues.entrySet()) {

                                                if ((path.get(i).getVertexList().contains(getvertex(forloopListEntry.getValue().get(0)))
                                                        && getvertex(forloopListEntry.getValue().get(0)).getName() != sequenceListEntry.getKey())

                                                        || path.get(i).getVertexList().contains(getvertex(
                                                                sequenceListEntry.getValue().get(sequenceListEntry.getValue().size() - 1)))) {

                                                    noForLoop++;
                                                }
                                            }
                                        }

                                        if (forEverLoopList.size() > 0) {

                                            for (String forloopListEntry : forEverLoopList) {

                                                if (path.get(i).getVertexList().contains(getvertex(forloopListEntry))) {

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

                                                    if (!sequenceEdges.get(taskEndName).contains(sequenceListEntry.getValue().get(nextIndex))) {
                                                        sequenceEdges.get(taskEndName).add(sequenceListEntry.getValue().get(nextIndex));
                                                    }

                                                } else {

                                                    sequenceEdges.put(eventName, endSequenceVertex);

                                                }

                                            } else if (nextIndex == sequenceListEntry.getValue().size() && orderedSequenceList.size() > 1) {

                                                for (Entry<String, ArrayList<String>> othersequenceListEntryValue : orderedSequenceList.entrySet()) {

                                                    if (!othersequenceListEntryValue.getKey().equals(sequenceListEntry.getKey())) {

                                                        int connectedSeq = 0;

                                                        List<GraphPath<vertex, DefaultEdge>> pathBetweenSeq = allPaths.getAllPaths(
                                                                getvertex(othersequenceListEntryValue.getKey()), getvertex(taskEndName), false,
                                                                g.vertexSet().size());

                                                        for (int j = 0; j < pathBetweenSeq.size(); j++) {

                                                            for (Entry<String, ArrayList<String>> adjacentsequenceListEntryValue : orderedSequenceList
                                                                    .entrySet()) {
                                                                if (!adjacentsequenceListEntryValue.getKey().equals(sequenceListEntry.getKey())
                                                                        && !adjacentsequenceListEntryValue.getKey()
                                                                                .equals(othersequenceListEntryValue.getKey())) {

                                                                    if (path.get(i).getVertexList()
                                                                            .contains(getvertex(adjacentsequenceListEntryValue.getKey()))) {

                                                                        connectedSeq++;

                                                                    }

                                                                }
                                                            }

                                                        }

                                                        if (connectedSeq == 0 && pathBetweenSeq.size() > 0) {

                                                            for (String othersequenceListValue : othersequenceListEntryValue.getValue()) {

                                                                List<GraphPath<vertex, DefaultEdge>> pathToNextValue = allPaths.getAllPaths(
                                                                        getvertex(othersequenceListValue), getvertex(taskEndName), false,
                                                                        g.vertexSet().size());

                                                                if (pathToNextValue.size() > 0)

                                                                {

                                                                    int nextAdjIndex = othersequenceListEntryValue.getValue()
                                                                            .indexOf(othersequenceListValue) + 1;

                                                                    if (nextAdjIndex < othersequenceListEntryValue.getValue().size()) {

                                                                        HashSet<String> nextSequenceVertex = new HashSet<String>();
                                                                        nextSequenceVertex
                                                                                .add(othersequenceListEntryValue.getValue().get(nextAdjIndex));

                                                                        if (sequenceEdges.containsKey(taskEndName)) {

                                                                            if (!sequenceEdges.get(taskEndName).contains(
                                                                                    othersequenceListEntryValue.getValue().get(nextAdjIndex))) {
                                                                                sequenceEdges.get(taskEndName).add(
                                                                                        othersequenceListEntryValue.getValue().get(nextAdjIndex));
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

                if (g.containsVertex(getvertex(sequenceListEntry.getKey()))) {

                    int noForLoop = 0;

                    List<GraphPath<vertex, DefaultEdge>> path = allPaths.getAllPaths(getvertex(sequenceListEntry.getKey()), getvertex(taskEndName),
                            false, g.vertexSet().size());

                    for (int i = 0; i < path.size(); i++) {

                        if (path.size() > 0 && sequenceListEntry.getValue().size() > 0) {

                            if (forLoopNextValues.size() > 0) {

                                for (Entry<String, List<String>> forloopListEntry : forLoopNextValues.entrySet()) {

                                    if (path.get(i).getVertexList().contains(getvertex(forloopListEntry.getKey()))) {

                                        if (path.get(i).getVertexList().contains(getvertex(forloopListEntry.getValue().get(0))))

                                        {
                                            noForLoop++;

                                        }

                                    }
                                }
                            }

                            if (forEverLoopList.size() > 0) {

                                for (String forloopListEntry : forEverLoopList) {

                                    if (path.get(i).getVertexList().contains(getvertex(forloopListEntry))) {

                                        noForLoop++;
                                    }
                                }
                            }

                            for (Entry<String, ArrayList<String>> seqEntry : orderedSequenceList.entrySet()) {

                                if (path.get(i).getVertexList().contains(getvertex(seqEntry.getKey()))) {

                                    if (path.get(i).getVertexList().contains(getvertex(seqEntry.getValue().get(seqEntry.getValue().size() - 1))))

                                    {

                                    } else {
                                        noForLoop++;
                                    }

                                }

                            }

                            if (noForLoop == 0) {

                                // if (unOrderedSequenceEdges.containsKey(taskEndName)) {

                                // if
                                // (!unOrderedSequenceEdges.get(taskEndName).contains(sequenceListEntry.getKey()))
                                // {
                                // unOrderedSequenceEdges.get(taskEndName).add(sequenceListEntry.getKey());
                                // }

                                // } else {

                                // unOrderedSequenceEdges.put(eventName, endSequenceVertex);

                                for (String seqEntry : sequenceListEntry.getValue()) {
                                    GraphPath<vertex, DefaultEdge> pathToEnd = null;
                                    if (g.containsVertex(getvertex(seqEntry))) {
                                        pathToEnd = DijkstraShortestPath.findPathBetween(g, getvertex(seqEntry), getvertex(eventName));

                                    }

                                    if (pathToEnd == null) {
                                        if (unOrderedSequenceEdges.containsKey(taskEndName)) {

                                            if (!unOrderedSequenceEdges.get(taskEndName).contains(sequenceListEntry.getKey())) {
                                                unOrderedSequenceEdges.get(taskEndName).add(seqEntry);
                                            }

                                        } else {

                                            HashSet<String> endSequenceVertex = new HashSet<String>();
                                            endSequenceVertex.add(seqEntry);

                                            unOrderedSequenceEdges.put(eventName, endSequenceVertex);

                                        }
                                    }
                                }

                                // }

                            }
                        }
                    }
                }
            }
        }

        // add if sequence on path of multiple for

        if (forLoopNextValues.size() > 0) {

            for (Entry<String, List<String>> forloopListEntry : forLoopNextValues.entrySet()) {

                if (g.containsVertex(getvertex(forloopListEntry.getValue().get(0)))) {

                    List<GraphPath<vertex, DefaultEdge>> path = allPaths.getAllPaths(getvertex(forloopListEntry.getValue().get(0)),
                            getvertex(taskEndName), false, g.vertexSet().size());

                    for (int i = 0; i < path.size(); i++) {
                        int forloopCount = 0;

                        for (Entry<String, List<String>> forEntry : forLoopNextValues.entrySet()) {

                            if (!forloopListEntry.getKey().equals(forEntry.getKey())) {
                                if (path.get(i).getVertexList().contains(getvertex(forEntry.getKey()))) {

                                    forloopCount++;

                                }

                            }

                        }

                        for (Entry<String, ArrayList<String>> seqEntry : orderedSequenceList.entrySet()) {

                            if (path.get(i).getVertexList().contains(getvertex(seqEntry.getKey()))) {

                                if (path.get(i).getVertexList().contains(getvertex(seqEntry.getValue().get(seqEntry.getValue().size() - 1))))

                                {

                                } else {
                                    forloopCount++;
                                }

                            }

                        }

                        /*
                         * for (Entry<String, ArrayList<String>> unOrderedseqEntry :
                         * unOrderedSequenceList.entrySet()) {
                         * 
                         * if
                         * (path.get(i).getVertexList().contains(getvertex(unOrderedseqEntry.getKey())))
                         * { forloopCount++;
                         * 
                         * HashSet<String> forLoopName = new HashSet<String>();
                         * forLoopName.add(forloopListEntry.getKey());
                         * 
                         * 
                         * //GraphPath<vertex, DefaultEdge> pathToEnd = null;
                         * 
                         * if (unOrderedSequenceEdges.containsKey(taskEndName)) {
                         * 
                         * if
                         * (!unOrderedSequenceEdges.get(taskEndName).contains(forloopListEntry.getKey())
                         * ) { unOrderedSequenceEdges.get(taskEndName).add(forloopListEntry.getKey()); }
                         * 
                         * } else {
                         * 
                         * // HashSet<String> endSequenceVertex = new HashSet<String>(); //
                         * endSequenceVertex.add(unOrderedseqEntry);
                         * 
                         * unOrderedSequenceEdges.put(eventName, forLoopName);
                         * 
                         * }
                         * 
                         * 
                         * 
                         * /* if (unOrderedSequenceEdges.containsKey(unOrderedseqEntry.getKey())) {
                         * 
                         * if (unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).contains(
                         * forloopListEntry.getKey())) {
                         * unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).add(forloopListEntry.
                         * getKey()); }
                         * 
                         * } else {
                         * 
                         * unOrderedSequenceEdges.put(unOrderedseqEntry.getKey(), forLoopName);
                         * 
                         * }
                         * 
                         * 
                         * }
                         * 
                         * }
                         */
                        String forvertexName = forloopListEntry.getKey();
                        if (forloopCount == 0 && !g.containsEdge(getvertex(taskEndName), getvertex(forvertexName))) {

                            addedEdges.put(taskEndName, forvertexName);

                        }
                    }

                }

                if (g.containsVertex(getvertex(forloopListEntry.getValue().get(1))) && forLoopNextValues.size() > 1) {

                    List<GraphPath<vertex, DefaultEdge>> path = allPaths.getAllPaths(getvertex(forloopListEntry.getValue().get(1)),
                            getvertex(taskEndName), false, g.vertexSet().size());

                    if (path.size() > 0) {

                        for (Entry<String, List<String>> previousForLoop : forLoopNextValues.entrySet()) {
                            if (g.containsVertex(getvertex(previousForLoop.getValue().get(0)))
                                    && !previousForLoop.getKey().equals(forloopListEntry.getKey())) {

                                List<GraphPath<vertex, DefaultEdge>> previousForpath = allPaths.getAllPaths(
                                        getvertex(previousForLoop.getValue().get(0)), getvertex(taskEndName), false, g.vertexSet().size());

                                for (int i = 0; i < previousForpath.size(); i++) {
                                    int forloopCount = 0;

                                    for (Entry<String, List<String>> forEntry : forLoopNextValues.entrySet()) {

                                        if (previousForpath.get(i).getVertexList().contains(getvertex(forEntry.getKey()))
                                                && !forloopListEntry.getKey().equals(forEntry.getKey())) {

                                            forloopCount++;

                                        }

                                    }

                                    String forvertexName = previousForLoop.getKey();
                                    if (forloopCount == 0

                                            && !g.containsEdge(getvertex(taskEndName), getvertex(forvertexName))) {

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

                List<GraphPath<vertex, DefaultEdge>> pathforloopforever = allPaths.getAllPaths(getvertex(loopforEver), getvertex(taskEndName), false,
                        g.vertexSet().size());

                if (pathforloopforever.size() > 0) {

                    for (int i = 0; i < pathforloopforever.size(); i++) {
                        int forloopCount = 0;

                        for (Entry<String, List<String>> previousForLoop : forLoopNextValues.entrySet()) {

                            if (pathforloopforever.get(i).getVertexList().contains(getvertex(previousForLoop.getValue().get(0)))) {

                                forloopCount++;

                            }
                        }

                        for (Entry<String, ArrayList<String>> seqEntry : orderedSequenceList.entrySet()) {

                            if (pathforloopforever.get(i).getVertexList().contains(getvertex(seqEntry.getKey()))) {

                                if (pathforloopforever.get(i).getVertexList()
                                        .contains(getvertex(seqEntry.getValue().get(seqEntry.getValue().size() - 1))))

                                {

                                } else {
                                    forloopCount++;
                                }

                            }

                        }

                        /*
                         * for (Entry<String, ArrayList<String>> unOrderedseqEntry :
                         * unOrderedSequenceList.entrySet()) {
                         * 
                         * if (pathforloopforever.get(i).getVertexList().contains(getvertex(
                         * unOrderedseqEntry.getKey()))) { forloopCount++;
                         * 
                         * HashSet<String> forLoopName = new HashSet<String>();
                         * forLoopName.add(loopforEver);
                         * 
                         * if (unOrderedSequenceEdges.containsKey(unOrderedseqEntry.getKey())) {
                         * 
                         * if
                         * (unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).contains(loopforEver)
                         * ) { unOrderedSequenceEdges.get(unOrderedseqEntry.getKey()).add(loopforEver);
                         * }
                         * 
                         * } else {
                         * 
                         * unOrderedSequenceEdges.put(unOrderedseqEntry.getKey(), forLoopName);
                         * 
                         * }
                         * 
                         * } }
                         */

                        if (forloopCount == 0) {

                            addedEdges.put(taskEndName, loopforEver);

                        }
                    }
                }
            }

        }

        opCount++;

    }

    private String getEventName(String taskName, TMLActivityElement currentElement2) {

        String eventName = null;

        if (currentElement.getReferenceObject() instanceof TMLADRandom) {

            eventName = taskName + "__" + currentElement2.getName() + "__" + currentElement2.getID();

        } else if (currentElement.getReferenceObject() instanceof TMLADUnorderedSequence) {

            eventName = taskName + "__" + "unOrderedSequence" + "__" + currentElement2.getID();

        } else {
            eventName = taskName + "__" + currentElement2.getReferenceObject().toString() + "__" + currentElement2.getID();

        }

        return eventName;
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
    public void showGraph(DirectedGraphTranslator dgraph) {

        JGraphXAdapter<vertex, DefaultEdge> graphAdapter = new JGraphXAdapter<vertex, DefaultEdge>(dgraph.getG());

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);

        layout.setInterHierarchySpacing(100);
        layout.setInterRankCellSpacing(100);
        layout.setIntraCellSpacing(100);

        layout.execute(graphAdapter.getDefaultParent());

        dgraph.getScrollPane().setViewportView(new mxGraphComponent(graphAdapter));

        dgraph.getScrollPane().setVisible(true);

        dgraph.getScrollPane().revalidate();
        dgraph.getScrollPane().repaint();

        dgraph.getFrame().add(dgraph.getScrollPane());

        dgraph.getFrame().pack();
        dgraph.getFrame().setLocationByPlatform(true);
        dgraph.getFrame().setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    // save graph in .graphml format
    public void exportGraph(String filename) throws ExportException, IOException {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        GraphMLExporter<String, DefaultEdge> gmlExporter = new GraphMLExporter();

        ComponentNameProvider<vertex> vertexIDProvider = new ComponentNameProvider<vertex>() {

            @Override
            public String getName(vertex vertex) {

                String name;
                for (vertex v : g.vertexSet()) {
                    if (v.getName().equals(vertex.getName())) {

                        name = vertex.getName().toString().replaceAll("\\s+", "");
                        name = vertex.getName().replaceAll("\\(", "\\u0028");
                        name = vertex.getName().replaceAll("\\)", "\\u0029");
                        return name;
                    }
                }
                return null;
            }

        };

        ComponentNameProvider<vertex> vertexNameProvider = new ComponentNameProvider<vertex>() {

            @Override
            public String getName(vertex arg0) {
                for (vertex v : g.vertexSet()) {
                    if (v.getName().equals(arg0.getName())) {
                        return arg0.getName();
                    }
                }
                return null;
            }

        };

        ComponentNameProvider<DefaultEdge> edgeIDProvider = new ComponentNameProvider<DefaultEdge>() {

            @Override
            public String getName(DefaultEdge edge) {
                String source = g.getEdgeSource(edge).toString().replaceAll("\\s+", "");
                source = source.replaceAll("\\(", "\\u0028");
                source = source.replaceAll("\\)", "\\u0029");

                // .replaceAll("\\(", "");
                // source.replaceAll("\\)", "");

                String target = g.getEdgeTarget(edge).toString().replaceAll("\\s+", "");
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

        GraphMLExporter<vertex, DefaultEdge> exporter = new GraphMLExporter<vertex, DefaultEdge>(vertexIDProvider, vertexNameProvider, edgeIDProvider,
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
    public Object[][] latencyDetailedAnalysis(String task12ID, String task22ID, Vector<SimulationTransaction> transFile1, Boolean taint,
            Boolean considerAddedRules) {

        for (vertex v : g.vertexSet()) {

            v.setLabel(new ArrayList<String>());
            v.setMaxTaintFixedNumber(new HashMap<String, Integer>());
            v.setTaintConsideredNumber(new HashMap<String, Integer>());
            v.setVirtualLengthAdded(0);
            v.setSampleNumber(0);
        }

        transFile = transFile1;

        // AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String,
        // DefaultEdge>(g);

        String message = "";

        String[] task1 = task12ID.split("__");

        int task1index = task1.length;

        idTask1 = task1[task1index - 1];

        String[] task2 = task22ID.split("__");

        int task2index = task2.length;

        idTask2 = task2[task2index - 1];

        String task12 = nameIDTaskList.get(idTask1);
        String task22 = nameIDTaskList.get(idTask2);

        vertex v1 = getvertex(task12);

        Vector<SimulationTransaction> Task1Traces = new Vector<SimulationTransaction>();
        Vector<SimulationTransaction> Task2Traces = new Vector<SimulationTransaction>();

        HashMap<String, Vector<String>> Task1TaintedTraces = new LinkedHashMap<String, Vector<String>>();
        HashMap<String, Vector<String>> Task2TaintedTraces = new LinkedHashMap<String, Vector<String>>();

        GraphPath<vertex, DefaultEdge> path2 = DijkstraShortestPath.findPathBetween(g, v1, getvertex(task22));

        // List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(task12,
        // task22, false, 100);

        // int size = path.size();
        times1.clear();
        times2.clear();

        // message = "there exists " +path.size()+" between: " + task12 + " and " +
        // task22;

        if (path2 != null && path2.getLength() > 0) {

            for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {

                String ChannelName = entry.getKey();
                ArrayList<String> busChList = entry.getValue();

                GraphPath<vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g, v1, getvertex(ChannelName));
                GraphPath<vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g, getvertex(ChannelName), getvertex(task22));

                if (pathTochannel != null && pathTochannel.getLength() > 0 && pathFromChannel != null && pathFromChannel.getLength() > 0) {

                    devicesToBeConsidered.addAll(busChList);

                }

            }

        } else {

            for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {

                String ChannelName = entry.getKey();
                ArrayList<String> busChList = entry.getValue();

                GraphPath<vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g, v1, getvertex(ChannelName));
                GraphPath<vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g, getvertex(ChannelName), getvertex(task22));

                if ((pathTochannel != null && pathTochannel.getLength() > 0) || (pathFromChannel != null && pathFromChannel.getLength() > 0)) {

                    devicesToBeConsidered.addAll(busChList);

                }

            }

        }

        Collections.sort(transFile1, new Comparator<SimulationTransaction>() {
            public int compare(SimulationTransaction o1, SimulationTransaction o2) {
                BigDecimal t1 = new BigDecimal(o1.startTime);
                BigDecimal t2 = new BigDecimal(o2.startTime);

                int startTimeEq = t1.compareTo(t2);

                if (startTimeEq == 0) {
                    BigDecimal t1End = new BigDecimal(o1.endTime);
                    BigDecimal t2End = new BigDecimal(o2.endTime);
                    return t1End.compareTo(t2End);
                }

                return startTimeEq;

            }
        });

        if (taint) {

            for (SimulationTransaction st : transFile1) {

                if (st.coreNumber == null) {
                    st.coreNumber = "0";

                }

                if (task1DeviceName.isEmpty()) {
                    if (st.id.equals(idTask1)) {

                        task1DeviceName = st.deviceName;
                        task1CoreNbr = st.coreNumber;

                    }
                }

                if (task2DeviceName.isEmpty()) {
                    if (st.id.equals(idTask2)) {

                        task2DeviceName = st.deviceName;

                        task2CoreNbr = st.coreNumber;

                    }
                }

                if (!task1DeviceName.isEmpty() && !task2DeviceName.isEmpty()) {
                    break;

                }

            }
            int j = 0;
            for (SimulationTransaction st : transFile1) {

                int startTime = Integer.valueOf(st.startTime);
                int endTime = Integer.valueOf(st.endTime);
                int id = Integer.valueOf(st.id);
                if (st.coreNumber == null) {
                    st.coreNumber = "0";

                }

                // ADD rules as edges
                if (considerAddedRules) {
                    if (ruleAddedEdges.size() > 0) {

                        for (Entry<vertex, List<vertex>> rulevertex : ruleAddedEdges.entrySet()) {

                            vertex fromVertex = rulevertex.getKey();

                            List<vertex> listOfToV = rulevertex.getValue();

                            for (vertex toVertex : listOfToV) {
                                if (g.containsVertex(toVertex) && g.containsVertex(fromVertex)) {
                                    g.addEdge(fromVertex, toVertex);
                                }
                            }

                        }

                    }
                }

                // if st started and ended before the first call of operator- don't consider it
                // if (!(Integer.valueOf(st.startTime) < times1.get(0) &&
                // Integer.valueOf(st.endTime) < times1.get(0))) {

                String taskname = "";

                String tasknameCheckID = "";

                if (st.command.contains(selectEventparam)) {
                    st.command = st.command.replace(selectEvent, waitSt + st.channelName);

                    String[] chN = st.channelName.split("__");

                    String eventN = chN[chN.length - 1];

                    vertex v = getvertexFromID(id);
                    String vName = v.getName();

                    if (Graphs.vertexHasSuccessors(g, v)) {
                        for (vertex vsec : Graphs.successorListOf(g, v)) {

                            if (vsec.getName().contains(waitEvent + eventN + "(")) {
                                st.id = String.valueOf(vsec.getId());
                            }

                        }

                    }

                } else if (st.command.contains(waitReqLabel)) {
                    vertex v = getvertexFromID(id);
                    if (v.getType() == vertex.TYPE_START) {
                        if (Graphs.vertexHasSuccessors(g, v)) {

                            for (vertex vbefore : Graphs.successorListOf(g, v)) {

                                if (vbefore.getName().startsWith(getReqArgLabel)) {
                                    st.id = String.valueOf(vbefore.getId());
                                }

                            }

                        }
                    }
                }

                for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {
                    String ChannelName = entry.getKey();
                    ArrayList<String> busChList = entry.getValue();
                    String bus1 = "";
                    for (String busName : busChList) {

                        String[] bus = st.deviceName.split("_");

                        if (bus.length > 2) {
                            for (int i = 0; i < bus.length - 1; i++) {
                                if (i == 0) {
                                    bus1 = bus[i];
                                } else {
                                    bus1 = bus1 + "_" + bus[i];
                                }

                            }
                        } else {
                            bus1 = bus[0];
                        }

                        if (bus1.equals(busName) && st.channelName.equals(ChannelName)) {
                            tasknameCheckID = ChannelName;
                            taskname = getvertex(ChannelName).getName();

                        }
                    }

                }

                if (tasknameCheckID.isEmpty()) {

                    for (vertex tasknameCheck : g.vertexSet()) {

                        String[] taskToAdd = tasknameCheck.toString().replaceAll(" ", "").split("__");

                        int taskToAddindex = taskToAdd.length;

                        String taskToAddid = taskToAdd[taskToAddindex - 1];

                        if (isNumeric(taskToAddid)) {

                            if (Integer.valueOf(taskToAddid).equals(id)) {

                                taskname = tasknameCheck.toString();

                                tasknameCheckID = tasknameCheck.getName();

                                if (taskname.equals(task12) && task1DeviceName.equals(st.deviceName) && task1CoreNbr.equals(st.coreNumber)) {

                                    addTaint(tasknameCheck);

                                    if (Task1TaintedTraces.containsKey(tasknameCheck.getLastLabel())) {
                                        Task1TaintedTraces.get(tasknameCheck.getLastLabel()).add(st.startTime);
                                    } else {
                                        Vector<String> Task1TracesTD = new Vector<String>();

                                        Task1TracesTD.add(st.startTime);

                                        Task1TaintedTraces.put(tasknameCheck.getLastLabel(), Task1TracesTD);

                                    }

                                    times1.add(startTime);
                                    Collections.sort(times1);

                                }

                                break;

                            }

                        }

                    }

                }

                if (Graphs.vertexHasSuccessors(g, getvertex(tasknameCheckID)) && !getvertex(tasknameCheckID).getLabel().isEmpty()) {

                    for (vertex v : Graphs.successorListOf(g, getvertex(tasknameCheckID))) {
                        String labelToaAddtoV = getfirstCommonLabel(v, getvertex(tasknameCheckID));

                        // removed after testing in for loop/ action/choice

                        /*
                         * if (Graphs.vertexHasPredecessors(g, getvertex(tasknameCheckID))) { for
                         * (vertex previousV : Graphs.predecessorListOf(g, getvertex(tasknameCheckID)))
                         * { if (previousV.getType() == vertex.TYPE_CHOICE) {
                         * 
                         * for (Entry<vertex, List<vertex>> vChoice : allChoiceValues.entrySet()) {
                         * 
                         * if (previousV.equals(vChoice.getKey())) {
                         * 
                         * if (previousV.getLabel().contains(labelToaAddtoV)) {
                         * 
                         * for (vertex cVertex : allChoiceValues.get(vChoice.getKey())) {
                         * 
                         * if (!cVertex.equals(getvertex(tasknameCheckID))) {
                         * 
                         * if (cVertex.getLabel().contains(labelToaAddtoV)) {
                         * cVertex.getLabel().remove(labelToaAddtoV);
                         * 
                         * }
                         * 
                         * if (cVertex.getMaxTaintFixedNumber().containsKey(labelToaAddtoV)) {
                         * cVertex.getMaxTaintFixedNumber().remove(labelToaAddtoV);
                         * 
                         * }
                         * 
                         * if (cVertex.getTaintConsideredNumber().containsKey(labelToaAddtoV)) {
                         * cVertex.getTaintConsideredNumber().remove(labelToaAddtoV);
                         * 
                         * } } }
                         * 
                         * }
                         * 
                         * }
                         * 
                         * }
                         * 
                         * } } }
                         */

                        if (v.getType() == vertex.TYPE_CHANNEL || v.getType() == vertex.TYPE_TRANSACTION) {

                            if (v.getLabel().contains(labelToaAddtoV)) {
                                if (v.getMaxTaintFixedNumber().containsKey(labelToaAddtoV)) {
                                    if (v.getMaxTaintFixedNumber().get(labelToaAddtoV) != v.getTaintFixedNumber()) {
                                        v.getMaxTaintFixedNumber().put(labelToaAddtoV,
                                                v.getMaxTaintFixedNumber().get(labelToaAddtoV) * v.getTaintFixedNumber());
                                    }
                                }

                            } else {
                                v.addLabel(labelToaAddtoV);
                                v.getMaxTaintFixedNumber().put(labelToaAddtoV, v.getTaintFixedNumber());
                            }

                            for (vertex subV : Graphs.successorListOf(g, v)) {

                                if (!subV.equals(v1)) {

                                    if (!(subV.getType() == vertex.TYPE_TRANSACTION || subV.getType() == vertex.TYPE_CHANNEL)) {

                                        HashMap<vertex, List<vertex>> NonTransVertexes = new LinkedHashMap<vertex, List<vertex>>();
                                        HashMap<vertex, List<vertex>> NonTransVertexes2 = new LinkedHashMap<vertex, List<vertex>>();
                                        HashMap<vertex, List<vertex>> NonTransVertexesAdded = new LinkedHashMap<vertex, List<vertex>>();

                                        NonTransVertexes.putAll(taintingNonTransVertexes(v, getvertex(tasknameCheckID), v1));

                                        int addeditems = 0;

                                        for (Entry<vertex, List<vertex>> e : NonTransVertexes.entrySet()) {
                                            vertex vet = e.getKey();
                                            List<vertex> vl = e.getValue();

                                            for (vertex ver : vl) {

                                                NonTransVertexes2 = taintingNonTransVertexes(ver, vet, v1);

                                                NonTransVertexesAdded.putAll(NonTransVertexes2);
                                                // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet, v1));
                                                addeditems = addeditems + NonTransVertexes2.size();

                                            }
                                        }

                                        while (addeditems > 0) {

                                            NonTransVertexes = new LinkedHashMap<vertex, List<vertex>>();
                                            NonTransVertexes.putAll(NonTransVertexesAdded);
                                            NonTransVertexesAdded = new LinkedHashMap<vertex, List<vertex>>();

                                            for (Entry<vertex, List<vertex>> e : NonTransVertexes.entrySet()) {
                                                vertex vet = e.getKey();
                                                List<vertex> vl = e.getValue();

                                                for (vertex ver : vl) {

                                                    NonTransVertexesAdded.putAll(taintingNonTransVertexes(ver, vet, v1));
                                                    // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet, v1));
                                                    addeditems--;
                                                    addeditems = addeditems + NonTransVertexesAdded.size();

                                                }

                                            }
                                        }

                                    }
                                }

                            }

                        } else {

                            HashMap<vertex, List<vertex>> NonTransVertexes = new LinkedHashMap<vertex, List<vertex>>();
                            HashMap<vertex, List<vertex>> NonTransVertexes2 = new LinkedHashMap<vertex, List<vertex>>();
                            HashMap<vertex, List<vertex>> NonTransVertexesAdded = new LinkedHashMap<vertex, List<vertex>>();

                            NonTransVertexes.putAll(taintingNonTransVertexes(v, getvertex(tasknameCheckID), v1));

                            int addeditems = 0;

                            for (Entry<vertex, List<vertex>> e : NonTransVertexes.entrySet()) {
                                vertex vet = e.getKey();
                                List<vertex> vl = e.getValue();

                                for (vertex ver : vl) {

                                    NonTransVertexes2 = taintingNonTransVertexes(ver, vet, v1);

                                    NonTransVertexesAdded.putAll(NonTransVertexes2);
                                    // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet, v1));
                                    addeditems = addeditems + NonTransVertexes2.size();

                                }

                            }

                            while (addeditems > 0) {

                                NonTransVertexes = new LinkedHashMap<vertex, List<vertex>>();
                                NonTransVertexes.putAll(NonTransVertexesAdded);
                                NonTransVertexesAdded = new LinkedHashMap<vertex, List<vertex>>();

                                for (Entry<vertex, List<vertex>> e : NonTransVertexes.entrySet()) {
                                    vertex vet = e.getKey();
                                    List<vertex> vl = e.getValue();

                                    for (vertex ver : vl) {

                                        NonTransVertexesAdded.putAll(taintingNonTransVertexes(ver, vet, v1));
                                        // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet, v1));
                                        addeditems--;
                                        addeditems = addeditems + NonTransVertexesAdded.size();

                                    }

                                }
                            }

                        }

                    }

                }

                Boolean hasLabelAstask12 = false;
                hasLabelAstask12 = considerVertex(task12, taskname, st.virtualLength, st.command);

                // remove rules edges

                if (considerAddedRules) {
                    if (ruleAddedEdges.size() > 0) {

                        for (Entry<vertex, List<vertex>> rulevertex : ruleAddedEdges.entrySet()) {

                            vertex fromVertex = rulevertex.getKey();

                            List<vertex> listOfToV = rulevertex.getValue();

                            for (vertex toVertex : listOfToV) {
                                if (g.containsVertex(fromVertex) && g.containsVertex(toVertex) && g.containsEdge(fromVertex, toVertex)) {

                                    g.removeEdge(fromVertex, toVertex);
                                }
                            }

                        }

                    }
                }

                String[] name = st.deviceName.split("_");
                String deviceName = name[0];
                // there is a path between task 1 and task 2
                // if (path2 != null && path2.getLength() > 0) {

                j++;

                if (path2 != null && path2.getLength() > 0) {

                    GraphPath<vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, v1, getvertex(taskname));

                    GraphPath<vertex, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g, getvertex(taskname),
                            getvertex(task22));

                    if (taskname.equals(task12) || (hasLabelAstask12 && taskname.equals(task22)) || (hasLabelAstask12 && (pathToOrigin != null
                            && pathToOrigin.getLength() > 0 && pathToDestination != null && pathToDestination.getLength() > 0))) {

                        if (taskname.equals(task22)) {

                            if (Task2TaintedTraces.containsKey(taintLabel)) {
                                Task2TaintedTraces.get(taintLabel).add(st.endTime);
                            } else {
                                Vector<String> Task2TracesTD = new Vector<String>();

                                Task2TracesTD.add(st.endTime);

                                Task2TaintedTraces.put(taintLabel, Task2TracesTD);

                            }

                            task2DeviceName = st.deviceName;

                            task2CoreNbr = st.coreNumber;
                            times2.add(endTime);
                            Collections.sort(times2);

                        }

                        if (relatedsimTraceswithTaint.containsKey(taintLabel)) {
                            relatedsimTraceswithTaint.get(taintLabel).add(st);
                        } else {

                            ArrayList<SimulationTransaction> TaskST = new ArrayList<SimulationTransaction>();

                            TaskST.add(st);
                            relatedsimTraceswithTaint.put(taintLabel, TaskST);

                        }

                        ArrayList<Integer> timeValues = new ArrayList<Integer>();
                        timeValues.add(0, Integer.valueOf(st.runnableTime));
                        timeValues.add(1, startTime);

                        if (!(st.runnableTime).equals(st.startTime)) {

                            String dName = st.deviceName + "_" + st.coreNumber;

                            if (runnableTimePerDevice.containsKey(dName)) {

                                if (!runnableTimePerDevice.get(dName).contains(timeValues)) {
                                    runnableTimePerDevice.get(dName).add(timeValues);
                                }
                            } else {

                                ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                timeValuesList.add(timeValues);

                                runnableTimePerDevice.put(dName, timeValuesList);

                            }

                        }
                    }

                    else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                            || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                            || devicesToBeConsidered.contains(deviceName))) {
                        delayDueTosimTraces.add(st);

                    }

                } else {

                    if (!taskname.equals(null) && !taskname.equals("")) {

                        GraphPath<vertex, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath.findPathBetween(g, v1, getvertex(taskname));

                        GraphPath<vertex, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath.findPathBetween(g, getvertex(taskname),
                                getvertex(task22));

                        if (taskname.equals(task12) || (hasLabelAstask12 && taskname.equals(task22))
                                || (hasLabelAstask12 && (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                        || pathExistsTestwithTask2 != null && pathExistsTestwithTask2.getLength() > 0))) {

                            if (taskname.equals(task22)) {

                                if (Task2TaintedTraces.containsKey(taintLabel)) {
                                    Task2TaintedTraces.get(taintLabel).add(st.endTime);
                                } else {
                                    Vector<String> Task2TracesTD = new Vector<String>();

                                    Task2TracesTD.add(st.endTime);

                                    Task2TaintedTraces.put(taintLabel, Task2TracesTD);

                                }

                                task2DeviceName = st.deviceName;

                                task2CoreNbr = st.coreNumber;
                                times2.add(endTime);
                                Collections.sort(times2);

                            }

                            if (relatedsimTraceswithTaint.containsKey(taintLabel)) {
                                relatedsimTraceswithTaint.get(taintLabel).add(st);
                            } else {

                                ArrayList<SimulationTransaction> TaskST = new ArrayList<SimulationTransaction>();

                                TaskST.add(st);
                                relatedsimTraceswithTaint.put(taintLabel, TaskST);

                            }

                            ArrayList<Integer> timeValues = new ArrayList<Integer>();
                            timeValues.add(0, Integer.valueOf(st.runnableTime));
                            timeValues.add(1, startTime);

                            if (!(st.runnableTime).equals(st.startTime)) {

                                String dName = st.deviceName + "_" + st.coreNumber;

                                if (runnableTimePerDevice.containsKey(dName)) {

                                    if (!runnableTimePerDevice.get(dName).contains(timeValues)) {
                                        runnableTimePerDevice.get(dName).add(timeValues);
                                    }
                                } else {

                                    ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                    timeValuesList.add(timeValues);

                                    runnableTimePerDevice.put(dName, timeValuesList);

                                }

                            }

                        } else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                || devicesToBeConsidered.contains(deviceName))) {
                            delayDueTosimTraces.add(st);

                        }

                    }
                }

                // }

                // }

            }

            int i = 0;

            dataByTask = new Object[Task1TaintedTraces.size()][7];
            for (Entry<String, Vector<String>> entry : Task1TaintedTraces.entrySet()) {
                String labeli = entry.getKey();

                dataByTask[i][0] = task12;
                dataByTask[i][1] = Task1TaintedTraces.get(labeli).get(0);

                Boolean haslabelinTask2 = false;

                for (Entry<String, Vector<String>> entry2 : Task2TaintedTraces.entrySet()) {

                    if (labeli.equals(entry2.getKey())) {
                        dataByTask[i][2] = task22;
                        dataByTask[i][3] = Task2TaintedTraces.get(labeli).get(entry2.getValue().size() - 1);
                        haslabelinTask2 = true;

                    }
                }

                if (!haslabelinTask2) {
                    dataByTask[i][2] = "no transaction was found for taint";
                    dataByTask[i][3] = "0";
                }

                int s1 = Integer.valueOf((String) dataByTask[i][1]);
                int s2 = Integer.valueOf((String) dataByTask[i][3]);

                int val = s2 - s1;

                if (val >= 0) {
                    dataByTask[i][4] = val;

                    if (times2.size() <= i) {
                        times2.add(i, s2);
                    }

                } else {
                    dataByTask[i][4] = 0;
                    times2.add(i, 0);
                }

                dataByTask[i][5] = "";

                Vector<SimulationTransaction> relatedSTTaint = new Vector<SimulationTransaction>();

                if (relatedsimTraceswithTaint.containsKey(labeli)) {

                    for (SimulationTransaction st : relatedsimTraceswithTaint.get(labeli)) {

                        int startTime = Integer.valueOf(st.startTime);
                        int endTime = Integer.valueOf(st.endTime);

                        if (!(startTime < s1 && endTime <= s1) && !(startTime >= s2 && endTime > s2)) {

                            // if (Integer.valueOf(st.startTime) >= minTime && Integer.valueOf(st.startTime)
                            // < maxTime) {

                            if (endTime > s2) {
                                st.endTime = String.valueOf(s2);
                                st.length = String.valueOf(s2 - startTime);
                            }

                            if (startTime < s1 && endTime != s1) {
                                st.startTime = String.valueOf(s1);
                                st.length = String.valueOf(endTime - s1);
                            }

                            if (startTime < s1 && endTime > s2) {
                                st.startTime = String.valueOf(s1);
                                st.endTime = String.valueOf(s2);
                                st.length = String.valueOf(s2 - s1);
                            }
                            relatedSTTaint.add(st);

                        }

                    }
                }

                dataByTaskR.put(i, relatedSTTaint);

                timeDelayedPerRow.put(i, runnableTimePerDevice);

                i++;

            }

            for (int row = 0; row < dataByTask.length; row++) {
                Vector<SimulationTransaction> delayDueTosimTracesTaint = new Vector<SimulationTransaction>();

                for (SimulationTransaction st : delayDueTosimTraces) {

                    int startTime = Integer.valueOf(st.startTime);
                    int endTime = Integer.valueOf(st.endTime);

                    if (!(startTime < times1.get(row) && endTime <= times1.get(row))
                            && !(startTime >= times2.get(row) && endTime > times2.get(row))) {

                        if (endTime > times2.get(row)) {
                            st.endTime = times2.get(row).toString();
                            st.length = String.valueOf(times2.get(row) - startTime);
                        }

                        if (startTime < times1.get(row)) {
                            st.startTime = times1.get(row).toString();
                            st.length = String.valueOf(endTime - times1.get(row));
                        }

                        if (startTime < times1.get(row) && endTime > times2.get(row)) {
                            st.startTime = times1.get(row).toString();
                            st.endTime = times2.get(row).toString();
                            st.length = String.valueOf(times2.get(row) - times1.get(row));
                        }
                        delayDueTosimTracesTaint.add(st);

                    }

                }

                dataBydelayedTasks.put(row, delayDueTosimTracesTaint);

            }
        } else

        {

            for (SimulationTransaction st : transFile1) {

                int startTime = Integer.valueOf(st.startTime);
                int endTime = Integer.valueOf(st.endTime);
                int selectID = Integer.valueOf(st.id);

                if (st.coreNumber == null) {
                    st.coreNumber = "0";

                }

                if (st.command.contains(selectEventparam) && getvertexFromID(selectID).getType() == 11) {
                    st.command = st.command.replace(selectEventparam, waitLabel + st.channelName);

                    vertex selectV = getvertexFromID(selectID);

                    String[] chName = st.channelName.toString().split("__");

                    int waitEvntName = chName.length;

                    String waitEvnt = chName[waitEvntName - 1];

                    for (vertex nextV : Graphs.successorListOf(g, selectV)) {

                        if (nextV.getName().contains(waitEvnt)) {
                            st.id = String.valueOf(nextV.getId());
                        }
                    }

                } else if (st.command.contains(waitReqLabel)) {
                    vertex v = getvertexFromID(selectID);
                    if (v.getType() == vertex.TYPE_START) {
                        if (Graphs.vertexHasSuccessors(g, v)) {

                            for (vertex vbefore : Graphs.successorListOf(g, v)) {
                                if (vbefore.getName().startsWith(getReqArgLabel)) {
                                    st.id = String.valueOf(vbefore.getId());
                                }

                            }

                        }
                    }
                }

                if (st.id.equals(idTask1) && !times1.contains(startTime)) {
                    Task1Traces.add(st);
                    task1DeviceName = st.deviceName;
                    task1CoreNbr = st.coreNumber;
                    times1.add(startTime);
                    Collections.sort(times1);

                }

                if (st.id.equals(idTask2) && !times2.contains(endTime)) {
                    Task2Traces.add(st);
                    task2DeviceName = st.deviceName;
                    task2CoreNbr = st.coreNumber;
                    times2.add(endTime);
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

                runnableTimePerDevice = new HashMap<String, ArrayList<ArrayList<Integer>>>();

                for (SimulationTransaction st : transFile1) {

                    int startTime = Integer.valueOf(st.startTime);
                    int endTime = Integer.valueOf(st.endTime);
                    int selectID = Integer.valueOf(st.id);
                    Boolean onPath = false;

                    if (!(startTime < times1.get(i) && endTime <= times1.get(i)) && !(startTime >= times2.get(i) && endTime > times2.get(i))) {

                        if (st.command.contains(selectEventparam) && getvertexFromID(selectID).getType() == 11) {
                            st.command = st.command.replace(selectEventparam, waitLabel + st.channelName);

                            vertex selectV = getvertexFromID(selectID);

                            String[] chName = st.channelName.toString().split("__");

                            int waitEvntName = chName.length;

                            String waitEvnt = chName[waitEvntName - 1];

                            for (vertex nextV : Graphs.successorListOf(g, selectV)) {

                                if (nextV.getName().contains(waitEvnt)) {
                                    st.id = String.valueOf(nextV.getId());
                                }
                            }
                        } else if (st.command.contains(waitReqLabel)) {
                            vertex v = getvertexFromID(selectID);
                            if (v.getType() == vertex.TYPE_START) {
                                if (Graphs.vertexHasSuccessors(g, v)) {

                                    for (vertex vbefore : Graphs.successorListOf(g, v)) {
                                        if (vbefore.getName().startsWith(getReqArgLabel)) {
                                            st.id = String.valueOf(vbefore.getId());
                                        }

                                    }

                                }
                            }
                        }

                        if (endTime > times2.get(i)) {
                            st.endTime = times2.get(i).toString();
                            st.length = String.valueOf(times2.get(i) - startTime);
                        }

                        if (startTime < times1.get(i) && endTime != times1.get(i)) {
                            st.startTime = String.valueOf(times1.get(i));
                            st.length = String.valueOf(endTime - times1.get(i));
                        }

                        if (startTime < times1.get(i) && endTime > times2.get(i)) {
                            st.startTime = String.valueOf(times1.get(i));
                            st.endTime = times2.get(i).toString();
                            st.length = String.valueOf(times2.get(i) - times1.get(i));
                        }

                        String taskname = "";

                        for (vertex tasknameCheck : g.vertexSet()) {

                            String[] taskToAdd = tasknameCheck.toString().replaceAll(" ", "").split("__");

                            int taskToAddindex = taskToAdd.length;

                            String taskToAddid = taskToAdd[taskToAddindex - 1];
                            if (isNumeric(taskToAddid)) {

                                if (Integer.valueOf(taskToAddid).equals(selectID)) {

                                    taskname = tasknameCheck.toString();
                                    break;

                                }

                            }

                        }
                        String[] name = st.deviceName.split("_");
                        String deviceName = name[0];
                        // there is a path between task 1 and task 2
                        if (path2 != null && path2.getLength() > 0) {

                            if (!taskname.equals(null) && !taskname.equals("")) {

                                GraphPath<vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, v1, getvertex(taskname));

                                GraphPath<vertex, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g, getvertex(taskname),
                                        getvertex(task22));

                                if (taskname.equals(task12) || taskname.equals(task22) || (pathToOrigin != null && pathToOrigin.getLength() > 0
                                        && pathToDestination != null && pathToDestination.getLength() > 0)) {

                                    relatedsimTraces.add(st);
                                    ArrayList<Integer> timeValues = new ArrayList<Integer>();
                                    timeValues.add(0, Integer.valueOf(st.runnableTime));
                                    timeValues.add(1, startTime);

                                    String dName = st.deviceName + "_" + st.coreNumber;

                                    if (!(st.runnableTime).equals(st.startTime)) {

                                        if (runnableTimePerDevice.containsKey(dName)) {

                                            if (!runnableTimePerDevice.get(dName).contains(timeValues)) {
                                                runnableTimePerDevice.get(dName).add(timeValues);
                                            }
                                        } else {

                                            ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                            timeValuesList.add(timeValues);

                                            runnableTimePerDevice.put(dName, timeValuesList);

                                        }

                                    }
                                }

                                else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                        || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                        || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1) && !st.id.equals(idTask2)) {
                                    delayDueTosimTraces.add(st);

                                }

                            }

                        } else {
                            if (!taskname.equals(null) && !taskname.equals("")) {

                                GraphPath<vertex, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath.findPathBetween(g, v1,
                                        getvertex(taskname));

                                GraphPath<vertex, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath.findPathBetween(g, getvertex(taskname),
                                        getvertex(task22));

                                if (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                        || pathExistsTestwithTask2 != null && pathExistsTestwithTask2.getLength() > 0) {
                                    relatedsimTraces.add(st);

                                } else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                        || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                        || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1) && !st.id.equals(idTask2)) {
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

                if (times2.get(i) - times1.get(i) >= 0) {

                    dataByTask[i][4] = times2.get(i) - times1.get(i);
                } else {
                    dataByTask[i][4] = "Assumption Does Not Hold; Please try Tainting";
                }
                dataByTask[i][5] = "";

                dataByTaskR.put(i, relatedsimTraces);
                dataBydelayedTasks.put(i, delayDueTosimTraces);
                timeDelayedPerRow.put(i, runnableTimePerDevice);
                // dataByTask[i][5] = list.getModel();
                // dataByTask[i][6] = totalTime;

            }

        }

        return dataByTask;

    }

    public void saveDetailsToXML(String outputFile) {

        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document dom = builder.newDocument();

            Element root2 = dom.createElement(transactions);
            dom.appendChild(root2); // write DOM to XML file

            Element root1 = dom.createElement(mandatory);
            root2.appendChild(root1); // write DOM to XML file

            for (int i = 0; i < onPath.size(); i++) {

                SimulationTransaction st = onPath.get(i);

                // first create root element
                Element root = dom.createElement(stLAbel);
                root1.appendChild(root);

                // set `id` attribute to root element
                Attr attr = dom.createAttribute(idLabel);
                attr.setValue(st.id);
                root.setAttributeNode(attr);

                // now create child elements
                Element operator = dom.createElement(operatorLabel);
                operator.setTextContent(st.command);
                Element start = dom.createElement(startTimeLabel);
                start.setTextContent(st.startTime);
                Element et = dom.createElement(endTimeLabel);
                et.setTextContent(st.endTime);
                Element l = dom.createElement(lengthLabel);
                l.setTextContent(st.length);

                // add child nodes to root node
                root.appendChild(operator);
                root.appendChild(start);
                root.appendChild(et);
                root.appendChild(l);

            }

            Element root3 = dom.createElement(noContentionLabel);
            root2.appendChild(root3); // write DOM to XML file

            for (int i = 0; i < offPath.size(); i++) {

                SimulationTransaction st = offPath.get(i);

                // first create root element
                Element root5 = dom.createElement(stLAbel);
                root3.appendChild(root5);

                // set `id` attribute to root element
                Attr attr = dom.createAttribute(idLabel);
                attr.setValue(st.id);
                root5.setAttributeNode(attr);

                // now create child elements
                Element operator = dom.createElement(operatorLabel);
                operator.setTextContent(st.command);
                Element start = dom.createElement(startTimeLabel);
                start.setTextContent(st.startTime);
                Element et = dom.createElement(endTimeLabel);
                et.setTextContent(st.endTime);
                Element l = dom.createElement(lengthLabel);
                l.setTextContent(st.length);

                // add child nodes to root node
                root5.appendChild(operator);
                root5.appendChild(start);
                root5.appendChild(et);
                root5.appendChild(l);

            }

            Element root4 = dom.createElement(contentionLabel);
            root2.appendChild(root4); // write DOM to XML file

            for (int i = 0; i < offPathDelay.size(); i++) {

                SimulationTransaction st = offPathDelay.get(i);

                // first create root element
                Element root6 = dom.createElement(stLAbel);
                root4.appendChild(root6);

                // set `id` attribute to root element
                Attr attr = dom.createAttribute(idLabel);
                attr.setValue(st.id);
                root6.setAttributeNode(attr);

                // now create child elements
                Element operator = dom.createElement(operatorLabel);
                operator.setTextContent(st.command);
                Element start = dom.createElement(startTimeLabel);
                start.setTextContent(st.startTime);
                Element et = dom.createElement(endTimeLabel);
                et.setTextContent(st.endTime);
                Element l = dom.createElement(lengthLabel);
                l.setTextContent(st.length);

                // add child nodes to root node
                root6.appendChild(operator);
                root6.appendChild(start);
                root6.appendChild(et);
                root6.appendChild(l);

            }
            Transformer tr;
            try {
                tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                try {
                    tr.transform(new DOMSource(dom), new StreamResult(new File(outputFile)));
                } catch (TransformerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void saveLAtencyValuesToXML(String outputFile) {

        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document dom = builder.newDocument();

            Element root1 = dom.createElement(latencyTableLabel);
            dom.appendChild(root1);

            for (int id = 0; id < dataByTask.length; id++) {

                String starttime = String.valueOf(dataByTask[id][1]);
                String endtime = String.valueOf(dataByTask[id][3]);
                String latency = String.valueOf(dataByTask[id][4]);

                String op1 = String.valueOf(dataByTask[id][0]);
                String op2 = String.valueOf(dataByTask[id][2]);

                // first create root element
                Element root = dom.createElement(rowLabel);
                root1.appendChild(root);

                // set `id` attribute to root element
                Attr attr = dom.createAttribute(idLabel);
                attr.setValue(String.valueOf(id));
                root.setAttributeNode(attr);

                // now create child elements
                Element operator1 = dom.createElement(op1);
                operator1.setTextContent(op1);
                Element operator2 = dom.createElement(op2);
                operator2.setTextContent(op2);
                Element st = dom.createElement(startTimeLabel);
                st.setTextContent(starttime);
                Element et = dom.createElement(endTimeLabel);
                et.setTextContent(endtime);
                Element lat = dom.createElement(latencyLabel);
                lat.setTextContent(latency);

                // add child nodes to root node
                root.appendChild(operator1);
                root.appendChild(st);
                root.appendChild(operator2);
                root.appendChild(et);
                root.appendChild(lat);

            }

            // for (int id = 0; id < dataByTaskMinMax.length; id++) {

            String starttime = String.valueOf(dataByTaskMinMax[0][1]);
            String endtime = String.valueOf(dataByTaskMinMax[0][3]);
            String latency = String.valueOf(dataByTaskMinMax[0][4]);

            String op1 = String.valueOf(dataByTaskMinMax[0][0]);
            String op2 = String.valueOf(dataByTaskMinMax[0][2]);

            // first create root element
            Element root = dom.createElement(minLabel);
            root1.appendChild(root);

            // now create child elements
            Element operator1 = dom.createElement(op1);
            operator1.setTextContent(op1);
            Element operator2 = dom.createElement(op2);
            operator2.setTextContent(op2);
            Element st = dom.createElement(startTimeLabel);
            st.setTextContent(starttime);
            Element et = dom.createElement(endTimeLabel);
            et.setTextContent(endtime);
            Element lat = dom.createElement(latencyLabel);
            lat.setTextContent(latency);

            // add child nodes to root node
            root.appendChild(operator1);
            root.appendChild(st);
            root.appendChild(operator2);
            root.appendChild(et);
            root.appendChild(lat);

            String starttimeMax = String.valueOf(dataByTaskMinMax[1][1]);
            String endtimeMax = String.valueOf(dataByTaskMinMax[1][3]);
            String latencyMax = String.valueOf(dataByTaskMinMax[1][4]);

            String op1Max = String.valueOf(dataByTaskMinMax[1][0]);
            String op2Max = String.valueOf(dataByTaskMinMax[1][2]);

            // first create root element
            Element rootMax = dom.createElement(maxLabel);
            root1.appendChild(rootMax);

            // now create child elements
            Element operator1Max = dom.createElement(op1);
            operator1Max.setTextContent(op1Max);
            Element operator2Max = dom.createElement(op2);
            operator2Max.setTextContent(op2Max);
            Element stMax = dom.createElement(startTimeLabel);
            stMax.setTextContent(starttimeMax);
            Element etMax = dom.createElement(endTimeLabel);
            etMax.setTextContent(endtimeMax);
            Element latMax = dom.createElement(latencyLabel);
            latMax.setTextContent(latencyMax);

            // add child nodes to root node
            rootMax.appendChild(operator1Max);
            rootMax.appendChild(stMax);
            rootMax.appendChild(operator2Max);
            rootMax.appendChild(etMax);
            rootMax.appendChild(latMax);

            // }

            // write DOM to XML file
            Transformer tr;
            try {
                tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                try {
                    tr.transform(new DOMSource(dom), new StreamResult(new File(outputFile)));
                } catch (TransformerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String getfirstCommonLabel(vertex vertex, vertex v) {

        for (int i = 0; i < v.getLabel().size(); i++) {
            if (!vertex.getLabel().contains(v.getLabel().get(i))) {
                return v.getLabel().get(i);

            }
        }

        for (int j = 0; j < v.getLabel().size(); j++) {
            if (vertex.getMaxTaintFixedNumber().containsKey(v.getLabel().get(j)) && vertex.getMaxTaintFixedNumber().get(v.getLabel().get(j)) == 0) {

                return v.getLabel().get(j);

            }

        }

        for (int j = 0; j < v.getLabel().size(); j++) {
            if (vertex.getTaintConsideredNumber().containsKey(v.getLabel().get(j))
                    && vertex.getTaintConsideredNumber().get(v.getLabel().get(j)) < vertex.getMaxTaintFixedNumber().get(v.getLabel().get(j))) {

                return v.getLabel().get(j);

            }

        }
        // TODO Auto-generated method stub

        if (v.getLabel().size() - 1 >= 0) {
            return v.getLabel().get(v.getLabel().size() - 1);
        }
        return v.getLabel().get(v.getLabel().size());
    }

    private Boolean considerVertex(String task12, String taskname, String virtualLength, String command) {

        boolean hasLabelAstask12 = false;

        vertex v1 = getvertex(task12);
        vertex v = getvertex(taskname);
        String Label = null;

        if (command.contains("Write") || command.contains("Read")) {

            String[] str = command.split(",");
            String[] str2 = null;
            if (command.contains("Write")) {
                str2 = str[0].split("Write");
            } else if (command.contains("Read")) {
                str2 = str[0].split("Read");
            }

            if (str2[1].trim().matches("\\d*")) {

                int snbr = Integer.parseInt(str2[1].trim());

                if (v.getSampleNumber() != snbr) {
                    v.setSampleNumber(snbr);
                }
            }

            if (v.getVirtualLengthAdded() < v.getSampleNumber()) {

                v.setVirtualLengthAdded(v.getVirtualLengthAdded() + Integer.parseInt(virtualLength));

            }

        }

        for (int i = 0; i < v.getLabel().size(); i++) {

            String labelConsidered = v.getLabel().get(i);

            int consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);

            if (Graphs.vertexHasPredecessors(g, v)) {
                for (vertex previousV : Graphs.predecessorListOf(g, v)) {
                    if (previousV.getType() == vertex.TYPE_CHOICE) {

                        for (Entry<vertex, List<vertex>> vChoice : allChoiceValues.entrySet()) {

                            if (previousV.equals(vChoice.getKey())) {

                                if (v1.getLabel().contains(v.getLabel().get(i)) && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {

                                    if (v.getVirtualLengthAdded() == v.getSampleNumber()) {

                                        consideredNbr = vChoice.getKey().getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        vChoice.getKey().getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        Label = labelConsidered;
                                        hasLabelAstask12 = true;

                                        v.setVirtualLengthAdded(0);
                                    } else {
                                        Label = labelConsidered;
                                        hasLabelAstask12 = true;

                                    }

                                }

                            }
                        }

                    }
                    if (previousV.getType() == vertex.TYPE_SELECT_EVT) {

                        for (Entry<vertex, List<vertex>> vChoice : allSelectEvtValues.entrySet()) {

                            if (previousV.equals(vChoice.getKey())) {

                                if (v1.getLabel().contains(v.getLabel().get(i)) && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {

                                    if (v.getVirtualLengthAdded() == v.getSampleNumber()) {

                                        consideredNbr = vChoice.getKey().getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        vChoice.getKey().getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        Label = labelConsidered;
                                        hasLabelAstask12 = true;

                                        v.setVirtualLengthAdded(0);
                                    } else {
                                        Label = labelConsidered;
                                        hasLabelAstask12 = true;

                                    }

                                }

                            }
                        }

                    } else if (previousV.getType() == vertex.TYPE_SEQ) {

                        for (Entry<vertex, List<vertex>> vSeq : allSeqValues.entrySet()) {

                            if (previousV.equals(vSeq.getKey())) {

                                if (v1.getLabel().contains(v.getLabel().get(i)) && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {

                                    if (v.getVirtualLengthAdded() == v.getSampleNumber()) {
                                        consideredNbr = vSeq.getKey().getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        vSeq.getKey().getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                                        Label = labelConsidered;

                                        hasLabelAstask12 = true;

                                        v.setVirtualLengthAdded(0);

                                    } else {
                                        Label = labelConsidered;
                                        hasLabelAstask12 = true;

                                    }

                                }

                            }

                        }
                    } else if (previousV.getType() == vertex.TYPE_UNORDER_SEQ) {

                        if (v1.getLabel().contains(v.getLabel().get(i)) && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                            if (v.getVirtualLengthAdded() == v.getSampleNumber()) {
                                consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                                consideredNbr++;

                                v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                hasLabelAstask12 = true;

                                v.setVirtualLengthAdded(0);

                                for (Entry<vertex, List<vertex>> vSeq : allRandomSeqValues.entrySet()) {

                                    if (previousV.equals(vSeq.getKey())) {

                                        int count = 0;

                                        for (vertex seqNext : vSeq.getValue()) {

                                            if (seqNext.getTaintConsideredNumber().get(labelConsidered) != seqNext.getMaxTaintFixedNumber()
                                                    .get(labelConsidered)) {
                                                count++;

                                            }

                                        }

                                        if (count == 0) {

                                            if (previousV.getLabel().contains(labelConsidered)) {

                                                consideredNbr = previousV.getTaintConsideredNumber().get(labelConsidered);
                                                consideredNbr++;

                                                previousV.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                            }

                                        }

                                    }

                                }

                            } else {
                                Label = labelConsidered;
                                hasLabelAstask12 = true;

                            }

                        }

                    } else if ((previousV.getType() == vertex.TYPE_FOR_LOOP || previousV.getType() == vertex.TYPE_STATIC_FOR_LOOP)
                            && (previousV.getLabel().contains(labelConsidered))) {

                        if (v1.getLabel().contains(v.getLabel().get(i)) && (previousV.getTaintConsideredNumber()
                                .get(labelConsidered) == previousV.getMaxTaintFixedNumber().get(labelConsidered) - 1)) {

                            if (v.getVirtualLengthAdded() == v.getSampleNumber()) {

                                for (Entry<String, List<String>> nextvertexOfLoop : allForLoopNextValues.entrySet()) {

                                    vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));

                                    if (v.getName().equals(previousV.getName())) {
                                        consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        consideredNbr = previousV.getTaintConsideredNumber().get(labelConsidered);
                                        consideredNbr++;

                                        previousV.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                                        hasLabelAstask12 = true;
                                    }

                                }

                                v.setVirtualLengthAdded(0);

                            } else {
                                Label = labelConsidered;
                                hasLabelAstask12 = true;

                            }

                        }

                    }

                }

            }
            if (Graphs.vertexHasSuccessors(g, v)) {

                if (v.getVirtualLengthAdded() == v.getSampleNumber()) {

                    for (vertex nextV : Graphs.successorListOf(g, v)) {

                        if (nextV.getType() == vertex.TYPE_END) {

                            if (nextV.getLabel().contains(labelConsidered)) {

                                consideredNbr = nextV.getTaintConsideredNumber().get(labelConsidered);

                                if (consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                                    consideredNbr++;

                                    nextV.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                                }

                                for (vertex subE : Graphs.successorListOf(g, nextV)) {

                                    if (subE.getType() == vertex.TYPE_FOR_LOOP || subE.getType() == vertex.TYPE_STATIC_FOR_LOOP) {
                                        consideredNbr = subE.getTaintConsideredNumber().get(labelConsidered);

                                        if (consideredNbr < subE.getMaxTaintFixedNumber().get(labelConsidered)) {
                                            consideredNbr++;

                                            subE.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                                        }

                                    }

                                }
                            }

                        } else if (nextV.getType() == vertex.TYPE_START) {

                            consideredNbr = nextV.getTaintConsideredNumber().get(labelConsidered);

                            if (consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                                consideredNbr++;

                                nextV.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                            }

                        }

                    }

                }
            }

            if (!hasLabelAstask12 && (v.getType() == vertex.TYPE_TRANSACTION || v.getType() == vertex.TYPE_CHANNEL))

            {

                consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);

                if (v1.getLabel().contains(v.getLabel().get(i)) && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {

                    if (v.getVirtualLengthAdded() == v.getSampleNumber()) {

                        consideredNbr++;

                        v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);

                        Label = labelConsidered;

                        hasLabelAstask12 = true;
                        v.setVirtualLengthAdded(0);

                    } else {
                        Label = labelConsidered;
                        hasLabelAstask12 = true;

                    }

                }

            }

        }

        taintLabel = Label;
        return hasLabelAstask12;

    }

    private HashMap<vertex, List<vertex>> taintingNonTransVertexes(vertex subV, vertex v, vertex v1) {

        HashMap<vertex, List<vertex>> NonTransV = new LinkedHashMap<vertex, List<vertex>>();
        String label = getfirstCommonLabel(subV, v);
        int i = v.getMaxTaintFixedNumber().get(label);
        ;

        if (v.getType() == vertex.TYPE_FOR_EVER_LOOP || v.getType() == vertex.TYPE_STATIC_FOR_LOOP || v.getType() == vertex.TYPE_FOR_LOOP) {
            for (Entry<String, List<String>> nextvertexOfLoop : allForLoopNextValues.entrySet()) {

                vertex vFor0 = getvertex(nextvertexOfLoop.getValue().get(0));
                vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));

                if ((getvertex(nextvertexOfLoop.getKey())).equals(v)) {

                    if (subV.equals(vFor1)) {
                        int max = subV.getMaxTaintFixedNumber().get(label) / subV.getTaintFixedNumber();
                        i = max;
                    }
                }

            }

        }

        if (subV.getType() == vertex.TYPE_FOR_EVER_LOOP) {

            if (subV.getLabel().contains(label)) {
                if (subV.getMaxTaintFixedNumber().containsKey(label)) {
                    if (subV.getMaxTaintFixedNumber().get(label) != i) {
                        // subV.getMaxTaintFixedNumber().put(label,
                        // subV.getMaxTaintFixedNumber().get(label) * i * subV.getTaintFixedNumber());
                        // after testing on for static loop
                        subV.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * i);
                    }
                }

            } else {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }

            for (vertex subFor : Graphs.successorListOf(g, subV)) {

                if (!subFor.equals(v1)) {

                    /*
                     * if (subFor.getLabel().contains(label)) { if
                     * (subFor.getMaxTaintFixedNumber().containsKey(label)) { if
                     * (subFor.getMaxTaintFixedNumber().get(label) != subV.getTaintFixedNumber()) {
                     * subFor.getMaxTaintFixedNumber().put(label,
                     * subFor.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber()); }
                     * 
                     * }
                     * 
                     * }
                     */

                    if (!subFor.getLabel().contains(label)) {
                        subFor.addLabel(label);
                        subFor.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * subFor.getTaintFixedNumber());
                    }

                    if (subFor.getType() != vertex.TYPE_TRANSACTION) {

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subFor);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subFor);
                            NonTransV.put(subV, lv);

                        }

                    }
                }

            }

        } else if ((subV.getType() == vertex.TYPE_STATIC_FOR_LOOP || subV.getType() == vertex.TYPE_FOR_LOOP)) {

            /*
             * if (subV.getLabel().contains(label)) { if
             * (subV.getMaxTaintFixedNumber().containsKey(label)) { if
             * (subV.getMaxTaintFixedNumber().get(label) != i) { //
             * subV.getMaxTaintFixedNumber().put(label, //
             * subV.getMaxTaintFixedNumber().get(label) * i * subV.getTaintFixedNumber());
             * 
             * subV.getMaxTaintFixedNumber().put(label,
             * subV.getMaxTaintFixedNumber().get(label) * i); } }
             * 
             * }
             */

            if (!subV.getLabel().contains(label)) {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }

            for (Entry<String, List<String>> nextvertexOfLoop : allForLoopNextValues.entrySet()) {

                vertex vFor0 = getvertex(nextvertexOfLoop.getValue().get(0));
                vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));

                if ((getvertex(nextvertexOfLoop.getKey())).equals(subV)) {

                    if (!vFor0.equals(v1)) {

                        /*
                         * if (vFor0.getLabel().contains(label)) { if
                         * (vFor0.getMaxTaintFixedNumber().containsKey(label)) { if
                         * (vFor0.getMaxTaintFixedNumber().get(label) != subV.getTaintFixedNumber()) {
                         * vFor0.getMaxTaintFixedNumber().put(label,
                         * vFor0.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber()); } }
                         * 
                         * }
                         */

                        if (!vFor0.getLabel().contains(label)) {
                            vFor0.addLabel(label);
                            vFor0.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * vFor0.getTaintFixedNumber());
                        }

                        if (vFor0.getType() != vertex.TYPE_TRANSACTION) {

                            if (NonTransV.containsKey(subV)) {
                                NonTransV.get(subV).add(vFor0);
                            } else {

                                List<vertex> lv = new ArrayList<vertex>();
                                lv.add(vFor0);
                                NonTransV.put(subV, lv);

                            }

                        }

                    }

                    if (!vFor1.equals(v1)) {

                        int max = subV.getMaxTaintFixedNumber().get(label) / subV.getTaintFixedNumber();

                        /*
                         * if (vFor1.getLabel().contains(label)) { if
                         * (vFor1.getMaxTaintFixedNumber().containsKey(label)) {
                         * 
                         * if (vFor1.getMaxTaintFixedNumber().get(label) != max) {
                         * vFor1.getMaxTaintFixedNumber().put(label,
                         * vFor1.getMaxTaintFixedNumber().get(label) * max); } }
                         * 
                         * }
                         */
                        if (!vFor1.getLabel().contains(label)) {
                            vFor1.addLabel(label);
                            vFor1.getMaxTaintFixedNumber().put(label, vFor1.getTaintFixedNumber() * max);
                        }

                        if (vFor1.getType() != vertex.TYPE_TRANSACTION) {

                            if (NonTransV.containsKey(subV)) {
                                NonTransV.get(subV).add(vFor1);
                            } else {

                                List<vertex> lv = new ArrayList<vertex>();
                                lv.add(vFor1);
                                NonTransV.put(subV, lv);

                            }

                        }

                    }

                }
            }

        } else if (subV.getType() == vertex.TYPE_CHOICE) {

            /*
             * if (subV.getLabel().contains(label)) { if
             * (subV.getMaxTaintFixedNumber().containsKey(label)) { if
             * (subV.getMaxTaintFixedNumber().get(label) != i) {
             * subV.getMaxTaintFixedNumber().put(label,
             * subV.getMaxTaintFixedNumber().get(label) * i * subV.getTaintFixedNumber()); }
             * }
             * 
             * }
             */
            if (!subV.getLabel().contains(label)) {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }
            List<vertex> subChoice = new ArrayList<vertex>();

            for (vertex subCh : Graphs.successorListOf(g, subV)) {

                subChoice.add(subCh);

                if (!subCh.equals(v1)) {

                    if (subCh.getLabel().contains(label)) {
                        if (subCh.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subCh.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subCh.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().put(label,
                                        subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber()));

                                subCh.getMaxTaintFixedNumber().put(label, subCh.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }

                        }

                    } else {
                        subCh.addLabel(label);
                        subCh.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber());
                    }

                    if (subCh.getType() != vertex.TYPE_TRANSACTION) {

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subCh);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subCh);
                            NonTransV.put(subV, lv);

                        }

                    }
                }

            }

            allChoiceValues.put(subV, subChoice);

        } else if (subV.getType() == vertex.TYPE_SELECT_EVT) {

            if (!subV.getLabel().contains(label)) {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }
            List<vertex> subChoice = new ArrayList<vertex>();

            for (vertex subCh : Graphs.successorListOf(g, subV)) {

                subChoice.add(subCh);

                if (!subCh.equals(v1)) {

                    if (subCh.getLabel().contains(label)) {
                        if (subCh.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subCh.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subCh.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().put(label,
                                        subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber()));

                                subCh.getMaxTaintFixedNumber().put(label, subCh.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }

                        }

                    } else {
                        subCh.addLabel(label);
                        subCh.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber());
                    }

                    if (subCh.getType() != vertex.TYPE_TRANSACTION) {

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subCh);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subCh);
                            NonTransV.put(subV, lv);

                        }

                    }
                }

            }

            allSelectEvtValues.put(subV, subChoice);

        } else if (subV.getType() == vertex.TYPE_END) {

            if (subV.getLabel().contains(label)) {
                if (subV.getMaxTaintFixedNumber().containsKey(label)) {
                    if (subV.getMaxTaintFixedNumber().get(label) != i) {
                        // subV.getMaxTaintFixedNumber().put(label,
                        // subV.getMaxTaintFixedNumber().get(label) * i * subV.getTaintFixedNumber());

                        subV.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * i);
                    }
                }

            } else {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }

            for (vertex subSE : Graphs.successorListOf(g, subV)) {

                if (!subSE.equals(v1)) {

                    if (subSE.getType() == vertex.TYPE_STATIC_FOR_LOOP || subSE.getType() == vertex.TYPE_FOR_LOOP) {

                        if (subSE.getLabel().contains(label)) {
                            if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                                if (subSE.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                    subSE.getMaxTaintFixedNumber().put(label, subSE.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                                }
                            }

                        } else {
                            subSE.addLabel(label);
                            subSE.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label));
                        }

                        for (Entry<String, List<String>> nextvertexOfLoop : allForLoopNextValues.entrySet()) {
                            if ((getvertex(nextvertexOfLoop.getKey())).equals(subSE)) {

                                vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));

                                int max;

                                if (subSE.getMaxTaintFixedNumber().get(label) > 0 && subSE.getTaintFixedNumber() > 0) {
                                    max = subSE.getMaxTaintFixedNumber().get(label) / subSE.getTaintFixedNumber();
                                } else {
                                    max = subSE.getTaintFixedNumber();
                                }

                                if (!vFor1.equals(v1)) {

                                    if (vFor1.getLabel().contains(label)) {
                                        if (vFor1.getMaxTaintFixedNumber().containsKey(label)) {

                                            if (vFor1.getMaxTaintFixedNumber().get(label) != max) {
                                                vFor1.getMaxTaintFixedNumber().put(label, vFor1.getMaxTaintFixedNumber().get(label) * max);
                                            }
                                        }

                                    } else {
                                        vFor1.addLabel(label);
                                        vFor1.getMaxTaintFixedNumber().put(label, vFor1.getTaintFixedNumber() * max);
                                    }

                                    if (vFor1.getType() != vertex.TYPE_TRANSACTION) {

                                        if (NonTransV.containsKey(subSE)) {
                                            NonTransV.get(subSE).add(vFor1);
                                        } else {

                                            List<vertex> lv = new ArrayList<vertex>();
                                            lv.add(vFor1);
                                            NonTransV.put(subSE, lv);

                                        }

                                    }
                                }

                            }

                        }
                    } else if (subSE.getType() == vertex.TYPE_TRANSACTION) {

                        if (subSE.getLabel().contains(label)) {
                            if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                                if (subSE.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                    subSE.getMaxTaintFixedNumber().put(label, subSE.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                                }
                            }

                        } else {
                            subSE.addLabel(label);
                            subSE.getMaxTaintFixedNumber().put(label, subSE.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                        }

                    } else if (subSE.getType() == vertex.TYPE_FOR_EVER_LOOP) {

                        if (subSE.getLabel().contains(label)) {
                            if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                                if (subSE.getMaxTaintFixedNumber().get(label) != subSE.getTaintFixedNumber()) {
                                    subSE.getMaxTaintFixedNumber().put(label, subSE.getTaintFixedNumber());
                                }
                            }

                        } else {
                            subSE.addLabel(label);
                            subSE.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label));
                        }

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subSE);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subSE);
                            NonTransV.put(subV, lv);

                        }

                    } else {

                        if (subSE.getLabel().contains(label)) {
                            if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                                if (subSE.getMaxTaintFixedNumber().get(label) != i * subSE.getTaintFixedNumber()) {
                                    subSE.getMaxTaintFixedNumber().put(label, i * subSE.getTaintFixedNumber());
                                }
                            }

                        } else {
                            subSE.addLabel(label);
                            subSE.getMaxTaintFixedNumber().put(label, subSE.getTaintFixedNumber());
                        }

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subSE);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subSE);
                            NonTransV.put(subV, lv);

                        }

                    }

                }
            }
        } else if (subV.getType() == vertex.TYPE_START || subV.getType() == vertex.TYPE_CTRL) {

            if (subV.getLabel().contains(label)) {
                if (subV.getMaxTaintFixedNumber().containsKey(label)) {
                    if (subV.getMaxTaintFixedNumber().get(label) != i) {
                        // subV.getMaxTaintFixedNumber().put(label,
                        // subV.getMaxTaintFixedNumber().get(label) * i * subV.getTaintFixedNumber());

                        subV.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label) * i);
                    }
                }

            } else {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }

            for (vertex subSE : Graphs.successorListOf(g, subV)) {

                if (!subSE.equals(v1)) {

                    if (subSE.getLabel().contains(label)) {
                        if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subSE.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subSE.getMaxTaintFixedNumber().put(label, subSE.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }

                    } else {
                        subSE.addLabel(label);
                        subSE.getMaxTaintFixedNumber().put(label, subSE.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                    }

                    if (subSE.getType() != vertex.TYPE_TRANSACTION) {

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subSE);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subSE);
                            NonTransV.put(subV, lv);

                        }
                    }

                }
            }

        } else if (subV.getType() == vertex.TYPE_SEQ) {

            /*
             * if (subV.getLabel().contains(label)) { if
             * (subV.getMaxTaintFixedNumber().containsKey(label)) { if
             * (subV.getMaxTaintFixedNumber().get(label) != subV.getTaintFixedNumber()) {
             * subV.getMaxTaintFixedNumber().put(label,
             * subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber())); }
             * }
             * 
             * }
             */
            if (!subV.getLabel().contains(label)) {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }

            List<vertex> subSeq = new ArrayList<vertex>();

            for (vertex subSEQ : Graphs.successorListOf(g, subV)) {

                if (!subSEQ.equals(v1)) {

                    if (subSEQ.getLabel().contains(label)) {
                        if (subSEQ.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subSEQ.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subSEQ.getMaxTaintFixedNumber().put(label, subSEQ.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }

                    } else {
                        subSEQ.addLabel(label);
                        subSEQ.getMaxTaintFixedNumber().put(label, subSEQ.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                    }

                    if (subSEQ.getType() != vertex.TYPE_TRANSACTION) {

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subSEQ);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(subSEQ);
                            NonTransV.put(subV, lv);

                        }
                    }

                }

                subSeq.add(subSEQ);

            }

            allSeqValues.put(subV, subSeq);
        } else if (subV.getType() == vertex.TYPE_UNORDER_SEQ) {

            /*
             * if (subV.getLabel().contains(label)) { if
             * (subV.getMaxTaintFixedNumber().containsKey(label)) { if
             * (subV.getMaxTaintFixedNumber().get(label) != subV.getTaintFixedNumber()) {
             * subV.getMaxTaintFixedNumber().put(label,
             * subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber())); }
             * }
             * 
             * }
             */
            if (!subV.getLabel().contains(label)) {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }

            List<vertex> subSeq = new ArrayList<vertex>();

            List<vertex> preSeq = Graphs.predecessorListOf(g, subV);

            for (vertex sub_UN_SEQ : Graphs.successorListOf(g, subV)) {

                if (preSeq.contains(sub_UN_SEQ)) {
                    continue;

                }

                if (!sub_UN_SEQ.equals(v1)) {

                    if (sub_UN_SEQ.getLabel().contains(label)) {
                        if (sub_UN_SEQ.getMaxTaintFixedNumber().containsKey(label)) {
                            if (sub_UN_SEQ.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                sub_UN_SEQ.getMaxTaintFixedNumber().put(label,
                                        sub_UN_SEQ.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }

                    } else {
                        sub_UN_SEQ.addLabel(label);
                        sub_UN_SEQ.getMaxTaintFixedNumber().put(label, sub_UN_SEQ.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                    }

                    if (sub_UN_SEQ.getType() != vertex.TYPE_TRANSACTION) {

                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(sub_UN_SEQ);
                        } else {

                            List<vertex> lv = new ArrayList<vertex>();
                            lv.add(sub_UN_SEQ);
                            NonTransV.put(subV, lv);

                        }
                    }

                }

                subSeq.add(sub_UN_SEQ);

            }

            allRandomSeqValues.put(subV, subSeq);
        }

        return NonTransV;

    }

    protected vertex getvertex(String task12) {

        for (vertex v : g.vertexSet()) {
            if (v.getName().equals(task12)) {
                return v;
            }
        }
        return null;

    }

    protected vertex getvertexFromID(int id) {

        for (vertex v : g.vertexSet()) {
            if (v.getId() == (id)) {
                return v;
            }
        }
        return null;

    }

    private void addTaint(vertex currentVertex) {

        String label = generateLabel();
        boolean generatenewLabel = false;

        while (!generatenewLabel) {

            int count = 0;
            for (int i = 0; i < usedLabels.size(); i++) {
                if (usedLabels.contains(label.toString())) {
                    count++;
                    break;
                }
            }
            if (count > 0) {
                label = generateLabel();

            } else {
                generatenewLabel = true;

            }
        }

        usedLabels.add(label.toString());
        currentVertex.addLabel(label.toString());

        currentVertex.getMaxTaintFixedNumber().put(label.toString(), currentVertex.getTaintFixedNumber());

        return;
    }

    private String generateLabel() {

        StringBuilder label = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {

            char rndChar = data.charAt(random.nextInt(data.length()));
            label.append(rndChar);

        }

        return label.toString();

    }

    // fill the detailed latency table once a row is selected
    public String[][] getTaskByRowDetails(int row) {

        String[][] dataByTaskRowDetails = new String[dataByTaskR.get(row).size()][5];

        int i = 0;

        for (SimulationTransaction st : dataByTaskR.get(row)) {
            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);

            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
            dataByTaskRowDetails[i][3] = st.startTime;
            dataByTaskRowDetails[i][4] = st.endTime;
            i++;
        }

        return dataByTaskRowDetails;
    }

    // fill the detailed latency table once a row is selected
    public Object[][] getTaskByRowDetailsMinMaxTaint(int row) {

        String task12 = (String) dataByTaskMinMax[row][0];
        int maxStartTime = (int) dataByTaskMinMax[row][1];
        String task22 = (String) dataByTaskMinMax[row][2];
        int maxEndTime = (int) dataByTaskMinMax[row][3];

        int rowIndex = 0;

        for (int i = 0; i < dataByTask.length; i++) {

            int s1 = Integer.valueOf((String) dataByTask[i][1]);
            int s2 = Integer.valueOf((String) dataByTask[i][3]);

            if (s1 == maxStartTime && s2 == maxEndTime) {
                rowIndex = i;
                break;

            }

        }

        Object[][] dataByTaskRowDetails = new Object[dataByTaskR.get(rowIndex).size()][5];

        int i = 0;

        for (SimulationTransaction st : dataByTaskR.get(rowIndex)) {
            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);

            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);
            i++;
        }

        return dataByTaskRowDetails;
    }

    // fill the tasks that run on the same hardware but don't belong to the path
    // between selected activities
    public Object[][] getTaskHWByRowDetailsMinMaxTaint(int row) {

        String task12 = (String) dataByTaskMinMax[row][0];
        int maxStartTime = (int) dataByTaskMinMax[row][1];
        String task22 = (String) dataByTaskMinMax[row][2];
        int maxEndTime = (int) dataByTaskMinMax[row][3];

        int rowIndex = 0;

        for (int i = 0; i < dataByTask.length; i++) {

            int s1 = Integer.valueOf((String) dataByTask[i][1]);
            int s2 = Integer.valueOf((String) dataByTask[i][3]);

            if (s1 == maxStartTime && s2 == maxEndTime) {
                rowIndex = i;
                break;

            }

        }

        Object[][] dataByTaskRowDetails = new Object[dataBydelayedTasks.get(rowIndex).size()][5];

        int i = 0;

        for (SimulationTransaction st : dataBydelayedTasks.get(rowIndex)) {

            dataByTaskRowDetails[i][0] = st.command;
            dataByTaskRowDetails[i][1] = nameIDTaskList.get(st.id);
            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
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

    public Vector<SimulationTransaction> getMinMaxTasksByRowTainted(int row) {

        int maxStartTime = (int) dataByTaskMinMax[row][1];

        int maxEndTime = (int) dataByTaskMinMax[row][3];

        int rowIndex = 0;

        for (int i = 0; i < dataByTask.length; i++) {

            int s1 = Integer.valueOf((String) dataByTask[i][1]);
            int s2 = Integer.valueOf((String) dataByTask[i][3]);

            if (s1 == maxStartTime && s2 == maxEndTime) {
                rowIndex = i;
                break;

            }

        }

        return dataByTaskR.get(rowIndex);
    }

    // between selected activities
    public List<SimulationTransaction> getTaskMinMaxHWByRowDetailsTainted(int row) {

        int maxStartTime = (int) dataByTaskMinMax[row][1];

        int maxEndTime = (int) dataByTaskMinMax[row][3];

        int rowIndex = 0;

        for (int i = 0; i < dataByTask.length; i++) {

            int s1 = Integer.valueOf((String) dataByTask[i][1]);
            int s2 = Integer.valueOf((String) dataByTask[i][3]);

            if (s1 == maxStartTime && s2 == maxEndTime) {
                rowIndex = i;
                break;

            }

        }

        return dataBydelayedTasks.get(rowIndex);
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

        String task12 = (String) dataByTaskMinMax[row][0];
        int minTime = (int) dataByTaskMinMax[row][1];
        String task22 = (String) dataByTaskMinMax[row][2];
        int maxTime = (int) dataByTaskMinMax[row][3];

        HashMap<String, ArrayList<SimulationTransaction>> relatedHWs = new HashMap<String, ArrayList<SimulationTransaction>>();
        HashMap<String, ArrayList<SimulationTransaction>> relatedTasks = new HashMap<String, ArrayList<SimulationTransaction>>();
        relatedsimTraces = new Vector<SimulationTransaction>();
        delayDueTosimTraces = new Vector<SimulationTransaction>();
        runnableTimePerDevice = new HashMap<String, ArrayList<ArrayList<Integer>>>();

        // AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String,
        // DefaultEdge>(g);

        // List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(task12,
        // task22, false, g.vertexSet().size());

        // int size = path.size();

        GraphPath<vertex, DefaultEdge> path2 = DijkstraShortestPath.findPathBetween(g, getvertex(task12), getvertex(task22));

        if (path2 != null && path2.getLength() > 0) {

            for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {

                String ChannelName = entry.getKey();
                ArrayList<String> busChList = entry.getValue();

                GraphPath<vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g, getvertex(task12), getvertex(ChannelName));
                GraphPath<vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g, getvertex(ChannelName), getvertex(task22));

                if (pathTochannel != null && pathTochannel.getLength() > 0 && pathFromChannel != null && pathFromChannel.getLength() > 0) {

                    devicesToBeConsidered.addAll(busChList);

                }

            }

        } else {

            for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {

                String ChannelName = entry.getKey();
                ArrayList<String> busChList = entry.getValue();

                GraphPath<vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g, getvertex(task12), getvertex(ChannelName));
                GraphPath<vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g, getvertex(ChannelName), getvertex(task22));

                if ((pathTochannel != null && pathTochannel.getLength() > 0) || (pathFromChannel != null && pathFromChannel.getLength() > 0)) {

                    devicesToBeConsidered.addAll(busChList);

                }

            }

        }

        for (SimulationTransaction st : transFile) {
            Boolean onPath = false;

            int startTime = Integer.valueOf(st.startTime);
            int endTime = Integer.valueOf(st.endTime);
            int id = Integer.valueOf(st.id);
            if (!(startTime < minTime && endTime < minTime) && !(startTime > maxTime && endTime > maxTime)) {

                if (endTime > maxTime) {
                    st.endTime = String.valueOf(maxTime);
                    st.length = String.valueOf(maxTime - startTime);
                }

                if (startTime < minTime) {
                    st.startTime = String.valueOf(minTime);
                    st.length = String.valueOf(endTime - minTime);
                }

                if (startTime < minTime && endTime > maxTime) {
                    st.startTime = String.valueOf(minTime);
                    st.endTime = String.valueOf(maxTime);
                    st.length = String.valueOf(maxTime - minTime);
                }

                String taskname = "";

                for (vertex tasknameCheck : g.vertexSet()) {
                    String[] taskToAdd = tasknameCheck.toString().split("__");

                    int taskToAddindex = taskToAdd.length;

                    String taskToAddid = taskToAdd[taskToAddindex - 1];
                    if (isNumeric(taskToAddid)) {
                        if (Integer.valueOf(taskToAddid).equals(id)) {

                            taskname = tasknameCheck.toString();

                            break;

                        }

                    }
                }

                String[] name = st.deviceName.split("_");
                String deviceName = name[0];

                // there is a path between task 1 and task 2
                if (path2 != null && path2.getLength() > 0) {
                    if (!taskname.equals(null) && !taskname.equals("")) {

                        GraphPath<vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, getvertex(task12), getvertex(taskname));

                        GraphPath<vertex, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g, getvertex(taskname),
                                getvertex(task22));

                        if (taskname.equals(task12) || taskname.equals(task22) || (pathToOrigin != null && pathToOrigin.getLength() > 0
                                && pathToDestination != null && pathToDestination.getLength() > 0)) {

                            relatedsimTraces.add(st);

                            ArrayList<Integer> timeValues = new ArrayList<Integer>();
                            timeValues.add(0, Integer.valueOf(st.runnableTime));
                            timeValues.add(1, startTime);

                            if (!(st.runnableTime).equals(st.startTime)) {

                                String dName = st.deviceName + "_" + st.coreNumber;

                                if (runnableTimePerDevice.containsKey(dName)) {

                                    if (!runnableTimePerDevice.get(dName).contains(timeValues)) {
                                        runnableTimePerDevice.get(dName).add(timeValues);
                                    }
                                } else {

                                    ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                    timeValuesList.add(timeValues);

                                    runnableTimePerDevice.put(dName, timeValuesList);

                                }

                            }

                        }

                        else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1) && !st.id.equals(idTask2)) {
                            delayDueTosimTraces.add(st);

                        }

                    }

                    timeDelayedPerRow.put(row, runnableTimePerDevice);

                } else {
                    if (!taskname.equals(null) && !taskname.equals("")) {

                        GraphPath<vertex, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath.findPathBetween(g, getvertex(task12),
                                getvertex(taskname));

                        GraphPath<vertex, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath.findPathBetween(g, getvertex(taskname),
                                getvertex(task22));

                        if (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                || pathExistsTestwithTask2 != null && pathExistsTestwithTask2.getLength() > 0) {
                            relatedsimTraces.add(st);

                        } else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1) && !st.id.equals(idTask2)) {
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
            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);
            i++;
            /*
             * HashMap<String, ArrayList<ArrayList<Integer>>> delayTime =
             * timeDelayedPerRow.get(row);
             * 
             * boolean causeDelay = false;
             * 
             * if (delayTime.containsKey(st.deviceName)) {
             * 
             * for (Entry<String, ArrayList<ArrayList<Integer>>> entry :
             * delayTime.entrySet()) { if (entry.getKey().equals(st.deviceName)) {
             * ArrayList<ArrayList<Integer>> timeList = entry.getValue();
             * 
             * for (int j = 0; j < timeList.size(); j++) {
             * 
             * if (Integer.valueOf(st.startTime) > timeList.get(j).get(0) &&
             * Integer.valueOf(st.startTime) < timeList.get(j).get(1)) {
             * 
             * causeDelay = true;
             * 
             * } }
             * 
             * }
             * 
             * }
             * 
             * }
             * 
             * dataByTaskRowDetails[i][5] = causeDelay;
             * 
             * 
             */
        }

        return dataByTaskRowDetails;
    }

    // fill the Min max delay table on main latency analysis frame
    public Object[][] latencyMinMaxAnalysis(String task12ID, String task22ID, Vector<SimulationTransaction> transFile1) {
        List<Integer> times1MinMAx = new ArrayList<Integer>();
        List<Integer> times2MinMAx = new ArrayList<Integer>();

        String[] task1 = task12ID.split("__");

        int task1index = task1.length;

        idTask1 = task1[task1index - 1];

        String[] task2 = task22ID.split("__");

        int task2index = task2.length;

        idTask2 = task2[task2index - 1];

        String task12 = nameIDTaskList.get(idTask1);
        String task22 = nameIDTaskList.get(idTask2);

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
                    times2MinMAx.remove(match);
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

    // fill the Min max delay table on main latency analysis frame for tainted data
    public Object[][] latencyMinMaxAnalysisTaintedData(String task12ID, String task22ID, Vector<SimulationTransaction> transFile1) {
        List<Integer> times1MinMAx = new ArrayList<Integer>();
        List<Integer> times2MinMAx = new ArrayList<Integer>();

        String[] task1 = task12ID.split("__");

        int task1index = task1.length;

        idTask1 = task1[task1index - 1];

        String[] task2 = task22ID.split("__");

        int task2index = task2.length;

        idTask2 = task2[task2index - 1];

        String task12 = nameIDTaskList.get(idTask1);
        String task22 = nameIDTaskList.get(idTask2);

        times1MinMAx = times1;
        times2MinMAx = times2;

        HashMap<Integer, ArrayList<Integer>> minTimes = new HashMap<Integer, ArrayList<Integer>>();

        int index = 0;
        for (int time1 : times1MinMAx) {
            int match = Integer.MAX_VALUE;
            // Find the first subsequent transaction
            int time = Integer.MAX_VALUE;

            if (times2MinMAx.get(index) > 0) {

                int time2 = times2MinMAx.get(index);
                int diff = time2 - time1;
                if (diff < time && diff >= 0) {
                    time = diff;
                    match = time2;
                }

            }

            if (time != Integer.MAX_VALUE) {
                ArrayList<Integer> startEndT = new ArrayList<Integer>();
                startEndT.add(time1);
                startEndT.add(match);
                minTimes.put(time, startEndT);
            }

            index++;
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
            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
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
            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
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

    public List<TMLComponentDesignPanel> getCpanels() {
        return cpanels;
    }

    public void setCpanels(List<TMLComponentDesignPanel> cpanels) {
        this.cpanels = cpanels;
    }

    public HashMap<String, String> getNameIDTaskList() {
        return nameIDTaskList;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public static void setScrollPane(JScrollPane scrollPane) {
        DirectedGraphTranslator.scrollPane = scrollPane;
    }

    public String checkPath(String task12ID, String task22ID) {

        Boolean isPath = false;

        String result = "";

        String[] task1 = task12ID.split("__");

        int task1index = task1.length;

        idTask1 = task1[task1index - 1];

        String[] task2 = task22ID.split("__");

        int task2index = task2.length;

        idTask2 = task2[task2index - 1];

        String task12 = nameIDTaskList.get(idTask1);
        String task22 = nameIDTaskList.get(idTask2);

        vertex v1 = getvertex(task12);
        vertex v2 = getvertex(task22);

        GraphPath<vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, v1, v2);

        if (pathToOrigin != null && pathToOrigin.getLength() > 0) {

            isPath = true;

        }

        if (isPath) {

            result = "A path exists between operators";
        } else {
            result = "No path between operators";
        }

        return result;
    }

    public Vector<String> getreadChannelNodes() {

        return readChannelTransactions;
    }

    public Vector<String> getwriteChannelNodes() {

        return writeChannelTransactions;
    }

    public String addRule(String node1, String node2, Vector<String> writeChannelTransactions, String ruleDirection) {

        vertex v1 = getvertex(node1);
        vertex v2 = getvertex(node2);

        vertex v1Channel = null, v2Channel = null;

        String message = "";

        if (v2Channel == null && Graphs.vertexHasSuccessors(g, v2)) {
            for (vertex n : Graphs.successorListOf(g, v2)) {
                if (n.getType() == vertex.TYPE_CHANNEL) {
                    v2Channel = n;
                    break;

                }
            }
        }

        Boolean hasWriteVertex = false;
        if (Graphs.vertexHasPredecessors(g, v1)) {
            for (vertex n : Graphs.predecessorListOf(g, v1)) {

                if (n.getType() == vertex.TYPE_CHANNEL) {

                    if (Graphs.vertexHasPredecessors(g, n)) {
                        for (vertex writenode : Graphs.predecessorListOf(g, n)) {

                            if (writeChannelTransactions.contains(writenode.getName())) {
                                hasWriteVertex = true;
                                break;
                            }

                        }

                    }

                    if (hasWriteVertex) {
                        v1Channel = n;
                        break;
                    } else {
                        v1Channel = v1;

                    }

                }
            }
        }

        if (v1Channel != null && v2Channel != null) {

            if (ruleDirection.equals("After")) {

                if (ruleAddedEdges.containsKey(v2Channel)) {
                    ruleAddedEdges.get(v2Channel).add(v1Channel);

                    message = "Rule between " + v1Channel + " and " + v2Channel + " was added";

                } else {
                    List<vertex> sendVertex = new ArrayList<vertex>();
                    sendVertex.add(v1Channel);

                    ruleAddedEdges.put(v2Channel, sendVertex);

                    message = "Rule between " + v1Channel + " and " + v2Channel + " was added";

                }

                if (ruleAddedEdgesChannels.containsKey(v2)) {
                    ruleAddedEdgesChannels.get(v2).add(v1);

                } else {
                    List<vertex> sendVertex = new ArrayList<vertex>();
                    sendVertex.add(v1);

                    ruleAddedEdgesChannels.put(v2, sendVertex);

                }

            }
        }

        if (message.isEmpty())

        {
            message = "Couln't add rule between " + v1 + " and " + v2 + "";

        }

        // TODO Auto-generated method stub
        return message;
    }

    public Boolean edgeExists(int vID1, int vID2) {

        vertex v1 = getvertexFromID(vID1);
        vertex v2 = getvertexFromID(vID2);

        if (g.containsEdge(v1, v2)) {
            return true;
        }

        return false;
    }

    public HashMap<vertex, List<vertex>> getRuleAddedEdges() {
        return ruleAddedEdges;
    }

    public void setRuleAddedEdges(HashMap<vertex, List<vertex>> ruleAddedEdges) {
        this.ruleAddedEdges = ruleAddedEdges;
    }

    public void setRuleAddedEdgesChannels(HashMap<vertex, List<vertex>> ruleAddedEdgesChannels) {
        this.ruleAddedEdgesChannels = ruleAddedEdgesChannels;
    }

    public HashMap<vertex, List<vertex>> getRuleAddedEdgesChannels() {
        return ruleAddedEdgesChannels;
    }

    public Graph<vertex, DefaultEdge> getG() {
        return g;
    }

    public void setG(Graph<vertex, DefaultEdge> g) {
        this.g = g;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<SimulationTransaction> getOnPath() {
        return onPath;
    }

    public void setOnPath(List<SimulationTransaction> onPath) {
        this.onPath = onPath;
    }

    public List<SimulationTransaction> getOffPath() {
        return offPath;
    }

    public void setOffPath(List<SimulationTransaction> offPath) {
        this.offPath = offPath;
    }

    public List<SimulationTransaction> getOffPathDelay() {
        return offPathDelay;
    }

    public void setOffPathDelay(List<SimulationTransaction> offPathDelay) {
        this.offPathDelay = offPathDelay;
    }

}