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
package tmltranslator.simulation;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphMLExporter;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import tmltranslator.DIPLOElement;
import tmltranslator.HwA;
import tmltranslator.HwCommunicationNode;
import tmltranslator.HwLink;
import tmltranslator.HwNode;
import tmltranslator.TMLActionState;
import tmltranslator.TMLActivity;
import tmltranslator.TMLActivityElement;
import tmltranslator.TMLAttribute;
import tmltranslator.TMLChannel;
import tmltranslator.TMLChoice;
import tmltranslator.TMLDelay;
import tmltranslator.TMLElement;
import tmltranslator.TMLEvent;
import tmltranslator.TMLExecC;
import tmltranslator.TMLExecCInterval;
import tmltranslator.TMLExecI;
import tmltranslator.TMLExecIInterval;
import tmltranslator.TMLForLoop;
import tmltranslator.TMLMapping;
import tmltranslator.TMLNotifiedEvent;
import tmltranslator.TMLRandom;
import tmltranslator.TMLRandomSequence;
import tmltranslator.TMLReadChannel;
import tmltranslator.TMLRequest;
import tmltranslator.TMLSelectEvt;
import tmltranslator.TMLSendEvent;
import tmltranslator.TMLSendRequest;
import tmltranslator.TMLSequence;
import tmltranslator.TMLStartState;
import tmltranslator.TMLStopState;
import tmltranslator.TMLTask;
import tmltranslator.TMLWaitEvent;
import tmltranslator.TMLWriteChannel;
import tmltranslator.tomappingsystemc2.DiploSimulatorCodeGenerator;

/**
 * Class DirectedGraphTranslator: this class generate the directed graph
 * equivalent for the sysml model
 * <p>
 * 23/09/2019
 *
 * @author Maysam Zoor
 */
public class DependencyGraphTranslator extends SwingWorker {
    private TMLTask taskAc;
    private TMLActivity activity;
    private int nodeNbProgressBar = 0;
    private TMLActivityElement currentElement;
    private Graph<Vertex, DefaultEdge> g;
    private final List<HwLink> links;
    private final TMLMapping tmap;
    private final HashMap<String, String> addedEdges = new HashMap<String, String>();
    private final Vector<String> allLatencyTasks = new Vector<String>();
    private static JScrollPane scrollPane = new JScrollPane();
    private final HashMap<String, String> nameIDTaskList = new HashMap<String, String>();
    private final HashMap<String, ArrayList<String>> channelPaths = new HashMap<String, ArrayList<String>>();
    private Object[][] dataByTask = null;
    private Object[][] dataByTaskMinMax = null;
    private HashMap<Integer, Vector<SimulationTransaction>> dataByTaskR = new HashMap<Integer, Vector<SimulationTransaction>>();
    private HashMap<Integer, Vector<SimulationTransaction>> mandatoryOptionalSimT = new HashMap<Integer, Vector<SimulationTransaction>>();
    private HashMap<Integer, List<SimulationTransaction>> dataBydelayedTasks = new HashMap<Integer, List<SimulationTransaction>>();
    private HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>> timeDelayedPerRow = new HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>>();
    private HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>> timeDelayedPerRowMinMax = new HashMap<Integer, HashMap<String, ArrayList<ArrayList<Integer>>>>();
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
    private Vector<SimulationTransaction> mandatoryOptional = new Vector<SimulationTransaction>();
    private HashMap<String, ArrayList<SimulationTransaction>> relatedsimTraceswithTaint = new HashMap<String, ArrayList<SimulationTransaction>>();
    private int nbOfNodes = 0;
    private List<String> usedLabels = new ArrayList<String>();
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private static Random random = new Random();
    private String taintLabel = "";
    private int opCount;
    private String taskStartName = "";
    private List<SimulationTransaction> onPath = new ArrayList<SimulationTransaction>();
    private List<SimulationTransaction> offPath = new ArrayList<SimulationTransaction>();
    private List<SimulationTransaction> offPathDelay = new ArrayList<SimulationTransaction>();
    private final HashMap<String, Integer> allChannels = new HashMap<String, Integer>();
    private static final String MODEL_AS_GRAPH = "The SysML Model As Dependency Graph";
    private static final String SELECT_EVENT_PARAM = "SelectEvent params:";
    private static final String SELECT_EVENT = "SelectEvent";
    private static final String TRANSACTIONS = "Transactions";
    private static final String MANDATORY = "Mandatory";
    private static final String OPERATOR_LABEL = "operator";
    private static final String START_TIME_LABEL = "starttime";
    private static final String END_TIME_LABEL = "endtime";
    private static final String LENGTH_LABEL = "length";
    private static final String NO_CONTENTION_LABEL = "NoContention";
    private static final String CONTENTION_LABEL = "Contention";
    private static final String ID_LABEL = "id";
    private static final String LATENCY_TABLE_LABEL = "LatencyTable";
    private static final String LATENCY_LABEL = "Latency";
    private static final String ROW_LABEL = "Row";
    private static final String MIN_LABEL = "Min";
    private static final String MAX_LABEL = "Max";
    private static final String OPERATOR_1 = "op1";
    private static final String OPERATOR_2 = "op2";
    private static final String ST_LABEL = "st";
    private static final String WAIT_LABEL = "Wait";
    private static final String WAIT_REQ_LABEL = "Wait reqChannel_";
    private static final String GET_REQ_ARG_LABEL = "getReqArg";
    private static final String WAIT_ST = "Wait: ";
    private static final String WAIT_EVENT = "Waitevent:";
    private static final String SEND_EVENT = "Sendevent:";
    private static final String STOP_AFTER_INFINITE_LOOP = "Stop after infinite loop";
    private static final String START_OF_FORK = "startOfFork";
    private static final String START_OF_JOIN = "startOfJoin";
    private static final String JUNCTION_OF_FORK = "junctionOfFork";
    private static final String JUNCTION_OF_JOIN = "junctionOfJoin";
    private static final String FORK_TASK_S = "FORKTASK_S";
    private static final String READ_OF_FORK = "ReadOfFork";
    private static final String WRITE_OF_FORK = "WriteOfFork_S";
    private static final String STOP_OF_FORK = "stopOfFork";
    private static final String STOP2_OF_FORK = "stop2OfFork";
    private static final String STOP_OF_JOIN = "stopOfJoin";
    private static final String JOIN_TASK_S = "JOINTASK_S_";
    private static final String READ_OF_JOIN = "ReadOfJoin";
    private static final String WRITE_OF_JOIN = "WriteOfJoin";
    private static final String WRITE_EVT_OF_FORK = "WriteEvtOfFork_S";
    private static final String WAIT_OF_FORK = "WaitOfFork";
    private static final String ZERO = "0";
    private static final String FORK_PORT_ORIGIN = "FORKPORTORIGIN";
    private static final String JOIN_PORT_ORIGIN = "JOINPORTORIGIN";
    private static final String JOIN_EVENT = "JOINEVENT";
    private static final String FORK_PORT_DESTINATION = "FORKPORTDESTINATION";
    private static final String FORK_EVENT = "FORKEVENT";
    private static final String S_LABEL = "_S_";
    private static final String JOIN_PORT_DESTINATION = "JOINPORTDESTINATION";
    private static final String FORK_CHANNEL = "FORKCHANNEL";
    private static final String JOIN_CHANNEL = "JOINCHANNEL";
    private static final String FORK_TASK = "FORKTASK";
    private static final String JOIN_TASK = "JOINTASK";
    private static final String WRITE = "Write";
    private static final String READ = "Read";
    private static final String UNORDERED_SEQUENCE = "unOrderedSequence";
    private static final String EXPRESSION_NOT_SUPPORTED = " Expression in For Loop is not supported by tainting";
    private static final String NO_TRANSACTION_FOUND = "no transaction was found for taint";
    private static final String PATH_EXISTS = "A path exists between operators";
    private static final String NO_PATH = "No path between operators";
    private static final String START = "start";
    private static final String WAITEVENT = "waitevent:";
    private static final String SENDEVENT = "sendevent:";
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String DATA = CHAR_LOWER + CHAR_UPPER;
    private final JFrame frame = new JFrame(MODEL_AS_GRAPH);
    private DependencyGraphRelations dependencyGraphRelations = new DependencyGraphRelations();

    @SuppressWarnings("deprecation")
    public DependencyGraphTranslator(TMLMapping tmap1) {
        tmap = tmap1;
        links = tmap.getTMLArchitecture().getHwLinks();
        nodeNbProgressBar = 0;
        nodeNbProgressBar = tmap.getArch().getBUSs().size() + tmap.getArch().getHwBridge().size()
                + tmap.getArch().getHwA().size() + tmap.getArch().getMemories().size()
                + tmap.getArch().getCPUs().size();
        expectedNumberofVertex();
        // DrawDirectedGraph();
    }

    // The main function to add the vertices and edges according to the model
    public Vertex Vertex(String name, int id) {
        Vertex v = new Vertex(name, id);
        return v;
    }

    public void DrawDirectedGraph() {
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
        addFPGAs();
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
        return;
    }

    @SuppressWarnings("unchecked")
    private void addUnmappedchannel() {
        DiploSimulatorCodeGenerator gen = new DiploSimulatorCodeGenerator(tmap);
        for (TMLChannel ch : (List<TMLChannel>) tmap.getTMLModeling().getChannels()) {
            List<HwCommunicationNode> pathNodes = gen.determineRoutingPath(tmap.getHwNodeOf(ch.getOriginTask()),
                    tmap.getHwNodeOf(ch.getDestinationTask()), ch);
            String channelName = ch.getName() + "__" + ch.getID();
            if (!g.vertexSet().contains(getvertex(channelName))) {
                g.addVertex(Vertex(channelName, ch.getID()));
                allChannels.put(ch.getName(), ch.getID());
                Vertex v = getvertex(channelName);
                updateMainBar();
                // gVertecies.add(vertex(ch.getName()));
                v.setType(Vertex.getTypeChannel());
                v.setTaintFixedNumber(0);
                updateMainBar();
            }
            if (!pathNodes.isEmpty()) {
                for (HwCommunicationNode node : pathNodes) {
                    if (channelPaths.containsKey(channelName)) {
                        if (!channelPaths.get(channelName).contains(node.getName())) {
                            channelPaths.get(channelName).add(node.getName());
                        }
                    } else {
                        ArrayList<String> pathNodeNames = new ArrayList<String>();
                        pathNodeNames.add(node.getName());
                        channelPaths.put(channelName, pathNodeNames);
                    }
                    if (!g.vertexSet().contains(getvertex(node.getName()))) {
                        g.addVertex(Vertex(node.getName(), node.getID()));
                    }
                    if (!g.vertexSet().contains(getvertex(channelName))) {
                        g.addVertex(Vertex(channelName, ch.getID()));
                        allChannels.put(ch.getName(), ch.getID());
                    }
                    if (!g.containsEdge(getvertex(node.getName()), getvertex(channelName))) {
                        g.addEdge(getvertex(node.getName()), getvertex(channelName));
                    }
                }
            }
        }
        // SummaryCommMapping = tmap.getSummaryCommMapping();
    }

    @SuppressWarnings("unchecked")
    private void addCPUs() {
        HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String, HashSet<TMLTask>>();
        for (HwNode node : tmap.getArch().getCPUs()) {
            cpuTask = new HashMap<String, HashSet<TMLTask>>();
            dependencyGraphRelations.getCpuIDs().put(node.getName(), node.getID());
            if (tmap.getLisMappedTasks(node).size() > 0) {
                cpuTask.put(node.getName(), tmap.getLisMappedTasks(node));
            }
            if (cpuTask.size() > 0) {
                getCPUTaskMap(cpuTask);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addFPGAs() {
        HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String, HashSet<TMLTask>>();
        for (HwNode node : tmap.getArch().getFPGAs()) {
            cpuTask = new HashMap<String, HashSet<TMLTask>>();
            dependencyGraphRelations.getCpuIDs().put(node.getName(), node.getID());
            if (tmap.getLisMappedTasks(node).size() > 0) {
                cpuTask.put(node.getName(), tmap.getLisMappedTasks(node));
            }
            if (cpuTask.size() > 0) {
                getCPUTaskMap(cpuTask);
            }
        }
    }

    private void addMemorychannel(HashMap<String, HashSet<TMLElement>> memorychannel) {
        for (Entry<String, HashSet<TMLElement>> entry : memorychannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();
            for (TMLElement busCh : busChList) {
                String channelName = busCh.getName() + "__" + busCh.getID();
                if (!g.containsVertex(getvertex(channelName))) {
                    g.addVertex(Vertex(channelName, busCh.getID()));
                    allChannels.put(busCh.getName(), busCh.getID());
                    updateMainBar();
                    getvertex(channelName).setType(Vertex.getTypeChannel());
                    // gVertecies.add(vertex(ChannelName));
                    getvertex(channelName).setTaintFixedNumber(0);
                    updateMainBar();
                }
                g.addEdge(getvertex(busName), getvertex(channelName));
            }
        }
    }

    private void addBridgechannel(HashMap<String, HashSet<TMLElement>> bridgechannel) {
        for (Entry<String, HashSet<TMLElement>> entry : bridgechannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();
            for (TMLElement busCh : busChList) {
                String ChannelName = busCh.getName() + "__" + busCh.getID();
                if (!g.containsVertex(getvertex(ChannelName))) {
                    g.addVertex(Vertex(ChannelName, busCh.getID()));
                    allChannels.put(busCh.getName(), busCh.getID());
                    updateMainBar();
                    getvertex(ChannelName).setType(Vertex.getTypeChannel());
                    // gVertecies.add(vertex(ChannelName));
                    getvertex(ChannelName).setTaintFixedNumber(0);
                    updateMainBar();
                }
                g.addEdge(getvertex(busName), getvertex(ChannelName));
            }
        }
    }

    private void addBuschannel(HashMap<String, HashSet<TMLElement>> buschannel) {
        for (Entry<String, HashSet<TMLElement>> entry : buschannel.entrySet()) {
            String busName = entry.getKey();
            HashSet<TMLElement> busChList = entry.getValue();
            for (TMLElement busCh : busChList) {
                String ChannelName = busCh.getName() + "__" + busCh.getID();
                if (!g.containsVertex(getvertex(ChannelName))) {
                    g.addVertex(Vertex(ChannelName, busCh.getID()));
                    allChannels.put(busCh.getName(), busCh.getID());
                    updateMainBar();
                    getvertex(ChannelName).setType(Vertex.getTypeChannel());
                    // gVertecies.add(vertex(ChannelName));
                    getvertex(ChannelName).setTaintFixedNumber(0);
                    updateMainBar();
                }
                g.addEdge(getvertex(busName), getvertex(ChannelName));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, HashSet<TMLElement>> addMemories() {
        HashMap<String, HashSet<TMLElement>> memorychannel = new HashMap<String, HashSet<TMLElement>>();
        for (HwNode node : tmap.getArch().getMemories()) {
            if (!g.containsVertex(getvertex(node.getName()))) {
                g.addVertex(Vertex(node.getName(), node.getID()));
                updateMainBar();
            }
            if (tmap.getLisMappedChannels(node).size() > 0) {
                memorychannel.put(node.getName(), tmap.getLisMappedChannels(node));
            }
        }
        return memorychannel;
    }

    @SuppressWarnings("unchecked")
    private void addHwAs() {
        HashMap<String, HashSet<TMLTask>> cpuTask = new HashMap<String, HashSet<TMLTask>>();
        for (HwA node : tmap.getArch().getHwA()) {
            cpuTask = new HashMap<String, HashSet<TMLTask>>();
            dependencyGraphRelations.getCpuIDs().put(node.getName(), node.getID());
            if (tmap.getLisMappedTasks(node).size() > 0) {
                cpuTask.put(node.getName(), tmap.getLisMappedTasks(node));
            }
            if (cpuTask.size() > 0) {
                getCPUTaskMap(cpuTask);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, HashSet<TMLElement>> addBridge() {
        HashMap<String, HashSet<TMLElement>> bridgechannel = new HashMap<String, HashSet<TMLElement>>();
        for (HwNode node : tmap.getArch().getHwBridge()) {
            if (!g.containsVertex(getvertex(node.getName()))) {
                g.addVertex(Vertex(node.getName(), node.getID()));
                updateMainBar();
            }
            if (tmap.getLisMappedChannels(node).size() > 0) {
                bridgechannel.put(node.getName(), tmap.getLisMappedChannels(node));
            }
        }
        return bridgechannel;
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, HashSet<TMLElement>> addBUSs() {
        HashMap<String, HashSet<TMLElement>> buschannel = new HashMap<String, HashSet<TMLElement>>();
        for (HwNode node : tmap.getArch().getBUSs()) {
            if (!g.containsVertex(getvertex(node.getName()))) {
                g.addVertex(Vertex(node.getName(), node.getID()));
                updateMainBar();
            }
            if (tmap.getLisMappedChannels(node).size() > 0) {
                buschannel.put(node.getName(), tmap.getLisMappedChannels(node));
            }
        }
        return buschannel;
    }

    private void addLinkEdges() {
        for (HwLink link : links) {
            Vertex vlink1 = Vertex(link.hwnode.getName(), link.hwnode.getID());
            Vertex vlink2 = Vertex(link.bus.getName(), link.bus.getID());
            if (g.containsVertex(getvertex(link.hwnode.getName())) && g.containsVertex(getvertex(link.bus.getName()))) {
                g.addEdge(vlink1, vlink2);
                g.addEdge(vlink2, vlink1);
            }
        }
    }

    private void addFlowEdges() {
        if (addedEdges.size() > 0) {
            for (Entry<String, String> edge : addedEdges.entrySet()) {
                Vertex v = getvertex(edge.getKey());
                Vertex v2 = getvertex(edge.getValue());
                if (v != null && v2 != null) {
                    g.addEdge(v, v2);
                }
            }
        }
    }

    private void addSendEventWaitEventEdges() {
        if (dependencyGraphRelations.getSendEventWaitEventEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getSendEventWaitEventEdges()
                    .entrySet()) {
                Vertex v = getvertex(edge.getKey());
                for (String waitEventEdge : edge.getValue()) {
                    Vertex v2 = getvertex(waitEventEdge);
                    if (v != null && v2 != null) {
                        g.addEdge(v, v2);
                    }
                }
            }
        }
    }

    private void addReadWriteChannelEdges() {
        if (dependencyGraphRelations.getReadWriteChannelEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getReadWriteChannelEdges().entrySet()) {
                Integer id = allChannels.get(edge.getKey());
                Vertex v = getvertex(edge.getKey() + "__" + id);
                for (String readChannelEdge : edge.getValue()) {
                    Vertex v2 = getvertex(readChannelEdge);
                    if (v != null && v2 != null) {
                        g.addEdge(v, v2);
                        v.setTaintFixedNumber(v.getTaintFixedNumber() + 1);
                    }
                }
            }
        }
    }

    private void addForkreadEdges() {
        if (dependencyGraphRelations.getForkreadEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getForkreadEdges().entrySet()) {
                HashSet<String> writech = dependencyGraphRelations.getForkwriteEdges().get(edge.getKey());
                for (String readChannelEdge : edge.getValue()) {
                    Vertex v = getvertex(readChannelEdge);
                    if (v != null) {
                        for (String wch : writech) {
                            Vertex v2 = getvertex(wch);
                            if (v2 != null) {
                                g.addEdge(v, v2);
                            }
                        }
                    }
                }
            }
        }
    }

    // draw the vertices and edges for the tasks mapped to the CPUs
    private void addJoinreadEdges() {
        if (dependencyGraphRelations.getJoinreadEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getJoinreadEdges().entrySet()) {
                HashSet<String> writech = dependencyGraphRelations.getJoinwriteEdges().get(edge.getKey());
                for (String readChannelEdge : edge.getValue()) {
                    Vertex v = getvertex(readChannelEdge);
                    if (v != null) {
                        for (String wch : writech) {
                            Vertex v2 = getvertex(wch);
                            if (v2 != null) {
                                g.addEdge(v, v2);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addWriteReadChannelEdges() {
        if (dependencyGraphRelations.getWriteReadChannelEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getWriteReadChannelEdges().entrySet()) {
                for (String readChannelEdge : edge.getValue()) {
                    Integer id = allChannels.get(readChannelEdge);
                    Vertex v = getvertex(edge.getKey());
                    Vertex v2 = getvertex(readChannelEdge + "__" + id);
                    if (v != null && v2 != null) {
                        g.addEdge(v, v2);
                        v2.setTaintFixedNumber(v2.getTaintFixedNumber() + 1);
                    }
                }
            }
        }
    }

    private void addRequestEdges() {
        if (dependencyGraphRelations.getRequestEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getRequestEdges().entrySet()) {
                Vertex v = getvertex(edge.getKey());
                for (String requestsingleEdges : edge.getValue()) {
                    Vertex v2 = getvertex(requestsingleEdges);
                    if (v != null && v2 != null) {
                        g.addEdge(v, v2);
                    }
                }
            }
        }
    }

    private void addunOrderedSeqEdges() {
        if (dependencyGraphRelations.getUnOrderedSequenceEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getUnOrderedSequenceEdges()
                    .entrySet()) {
                Vertex v = getvertex(edge.getKey());
                for (String sequenceEdge : edge.getValue()) {
                    Vertex v2 = getvertex(sequenceEdge);
                    if (v != null && v2 != null) {
                        g.addEdge(v, v2);
                    }
                }
            }
        }
    }

    private void addSeqEdges() {
        if (dependencyGraphRelations.getSequenceEdges().size() > 0) {
            for (Entry<String, HashSet<String>> edge : dependencyGraphRelations.getSequenceEdges().entrySet()) {
                Vertex v = getvertex(edge.getKey());
                for (String sequenceEdge : edge.getValue()) {
                    Vertex v2 = getvertex(sequenceEdge);
                    if (v != null && v2 != null) {
                        g.addEdge(v, v2);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void expectedNumberofVertex() {
        for (HwA node : tmap.getArch().getHwA()) {
            if (tmap.getLisMappedTasks(node).size() > 0) {
                nodeNbProgressBar = tmap.getLisMappedTasks(node).size() + nodeNbProgressBar;
                for (TMLTask task : (HashSet<TMLTask>) tmap.getLisMappedTasks(node)) {
                    for (TMLActivityElement ae : task.getActivityDiagram().getElements()) {
                        if (ae.getName().equals(STOP_AFTER_INFINITE_LOOP)) {
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
                for (TMLTask task : (HashSet<TMLTask>) tmap.getLisMappedTasks(node)) {
                    for (TMLActivityElement ae : task.getActivityDiagram().getElements()) {
                        if (ae.getName().equals(STOP_AFTER_INFINITE_LOOP)) {
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
                for (TMLElement entry : (HashSet<TMLElement>) tmap.getLisMappedChannels(node)) {
                    if (!mappedcomm.contains(entry.getName())) {
                        mappedcomm.add(entry.getName());
                        nodeNbProgressBar++;
                    }
                }
            }
        }
        for (HwNode node : tmap.getArch().getHwBridge()) {
            if (tmap.getLisMappedChannels(node).size() > 0) {
                for (TMLElement entry : (HashSet<TMLElement>) tmap.getLisMappedChannels(node)) {
                    if (!mappedcomm.contains(entry.getName())) {
                        mappedcomm.add(entry.getName());
                        nodeNbProgressBar++;
                    }
                }
            }
        }
        for (HwNode node : tmap.getArch().getMemories()) {
            if (tmap.getLisMappedChannels(node).size() > 0) {
                for (TMLElement entry : (HashSet<TMLElement>) tmap.getLisMappedChannels(node)) {
                    if (!mappedcomm.contains(entry.getName())) {
                        mappedcomm.add(entry.getName());
                        nodeNbProgressBar++;
                    }
                }
            }
        }
        for (TMLChannel ch : (List<TMLChannel>) tmap.getTMLModeling().getChannels()) {
            if (!mappedcomm.contains(ch.getName())) {
                mappedcomm.add(ch.getName());
                nodeNbProgressBar++;
            }
        }
    }

    private void updateMainBar() {
        nbOfNodes++;
        firePropertyChange("progress", nbOfNodes - 1, nbOfNodes);
    }

    public void getCPUTaskMap(HashMap<String, HashSet<TMLTask>> cpuTask) {
        HashMap<String, HashSet<String>> cpuTaskMap = new HashMap<String, HashSet<String>>();
        if (tmap == null) {
            return;
        }
        for (Entry<String, HashSet<TMLTask>> entry : cpuTask.entrySet()) {
            String key = entry.getKey();
            int keyID = dependencyGraphRelations.getCpuIDs().get(key);
            HashSet<TMLTask> value = entry.getValue();
            Vector<TMLActivityElement> multiNexts = new Vector<TMLActivityElement>();
            // Map <String, String> dependencyGraphRelations.getSendEvt();
            dependencyGraphRelations.setSendEvt(new HashMap<String, List<String>>());
            dependencyGraphRelations.setWaitEvt(new HashMap<String, List<String>>());
            dependencyGraphRelations.setSendData(new HashMap<String, String>());
            dependencyGraphRelations.setReceiveData(new HashMap<String, String>());
            // HashMap<String, List<String>> dependencyGraphRelations.getSendEvt() = new
            // HashMap<String, List<String>>();
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
                    g.addVertex(Vertex(key, keyID));
                    updateMainBar();
                }
                if (!g.vertexSet().contains(getvertex(taskName))) {
                    g.addVertex(Vertex(taskName, taskID));
                    updateMainBar();
                }
                g.addEdge(getvertex(key), getvertex(taskName));
                activity = task.getActivityDiagram();
                opCount = 1;
                currentElement = activity.getFirst();
                taskStartName = "";
                // int taskStartid;
                dependencyGraphRelations.setForLoopNextValues(new HashMap<String, List<String>>());
                // loop over all the activites corresponding to a task
                while (opCount <= activity.nElements()) {
                    String eventName = null;
                    // int eventid = currentElement.getID();
                    if (currentElement.getName().equals(STOP_AFTER_INFINITE_LOOP)) {
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
                    } else if (currentElement.getName().equals(START_OF_FORK)
                            || currentElement.getName().equals(JUNCTION_OF_FORK)
                            || currentElement.getName().equals(START_OF_JOIN)
                            || currentElement.getName().equals(JUNCTION_OF_JOIN)) {
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith(FORK_TASK_S) && currentElement.getName().equals(READ_OF_FORK)) {
                        String name = ((TMLReadChannel) (currentElement)).getChannel(0).getName();
                        int id = ((TMLReadChannel) (currentElement)).getChannel(0).getID();
                        name = name + "__" + id;
                        if (!g.containsVertex(getvertex(name))) {
                            g.addVertex(Vertex(name, id));
                            updateMainBar();
                        }
                        g.addEdge(getvertex(taskName), getvertex(name));
                        HashSet<String> readForkVertex = new HashSet<String>();
                        readForkVertex.add(name);
                        if (dependencyGraphRelations.getForkreadEdges().containsKey(taskName)) {
                            if (!dependencyGraphRelations.getForkreadEdges().get(taskName).contains(name)) {
                                dependencyGraphRelations.getForkreadEdges().get(taskName).add(name);
                            }
                        } else {
                            dependencyGraphRelations.getForkreadEdges().put(taskName, readForkVertex);
                        }
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith(FORK_TASK_S) && currentElement.getName().startsWith(WRITE_OF_FORK)) {
                        String vName = ((TMLWriteChannel) (currentElement)).getChannel(0).getName();
                        int vid = ((TMLWriteChannel) (currentElement)).getChannel(0).getID();
                        vName = vName + "__" + vid;
                        Vertex v = getvertex(vName);
                        if (!g.containsVertex(v)) {
                            g.addVertex(Vertex(vName, vid));
                            updateMainBar();
                        }
                        HashSet<String> writeForkVertex = new HashSet<String>();
                        writeForkVertex.add(vName);
                        if (dependencyGraphRelations.getForkwriteEdges().containsKey(taskName)) {
                            if (!dependencyGraphRelations.getForkwriteEdges().get(taskName).contains(vName)) {
                                dependencyGraphRelations.getForkwriteEdges().get(taskName).add(vName);
                            }
                        } else {
                            dependencyGraphRelations.getForkwriteEdges().put(taskName, writeForkVertex);
                        }
                        // g.addEdge(getvertex(taskName),getvertex(((TMLWriteChannel)(currentElement)).getChannel(0).getName()));
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (currentElement.getName().equals(STOP_OF_FORK)
                            || currentElement.getName().equals(STOP2_OF_FORK)
                            || currentElement.getName().equals(STOP_OF_JOIN)) {
                        opCount++;
                        updateMainBar();
                        // currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith(JOIN_TASK_S) && currentElement.getName().startsWith(READ_OF_JOIN)) {
                        String vName = ((TMLReadChannel) (currentElement)).getChannel(0).getName();
                        int vid = ((TMLReadChannel) (currentElement)).getChannel(0).getID();
                        vName = vName + "__" + vid;
                        if (!g.containsVertex(getvertex(vName))) {
                            g.addVertex(Vertex(vName, vid));
                            updateMainBar();
                        }
                        HashSet<String> writeForkVertex = new HashSet<String>();
                        writeForkVertex.add(vName);
                        if (dependencyGraphRelations.getJoinreadEdges().containsKey(taskName)) {
                            if (!dependencyGraphRelations.getJoinreadEdges().get(taskName).contains(vName)) {
                                dependencyGraphRelations.getJoinreadEdges().get(task.getName()).add(vName);
                            }
                        } else {
                            dependencyGraphRelations.getJoinreadEdges().put(taskName, writeForkVertex);
                        }
                        // g.addEdge(getvertex(task.getName()),getvertex(((TMLWriteChannel)(currentElement)).getChannel(0).getName()));
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith(JOIN_TASK_S) && currentElement.getName().equals(WRITE_OF_JOIN)) {
                        String vName = ((TMLWriteChannel) (currentElement)).getChannel(0).getName();
                        int vid = ((TMLWriteChannel) (currentElement)).getChannel(0).getID();
                        vName = vName + "__" + vid;
                        if (!g.containsVertex(getvertex(vName))) {
                            g.addVertex(Vertex(vName, vid));
                            updateMainBar();
                        }
                        g.addEdge(getvertex(taskName), getvertex(vName));
                        HashSet<String> readForkVertex = new HashSet<String>();
                        readForkVertex.add(vName);
                        if (dependencyGraphRelations.getJoinwriteEdges().containsKey(taskName)) {
                            if (!dependencyGraphRelations.getJoinwriteEdges().get(taskName).contains(vName)) {
                                dependencyGraphRelations.getJoinwriteEdges().get(taskName).add(vName);
                            }
                        } else {
                            dependencyGraphRelations.getJoinwriteEdges().put(taskName, readForkVertex);
                        }
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith(FORK_TASK_S)
                            && currentElement.getName().startsWith(WRITE_EVT_OF_FORK)) {
                        String vName = ((TMLSendEvent) (currentElement)).getEvent().getName();
                        int vid = ((TMLSendEvent) (currentElement)).getEvent().getID();
                        vName = vName + "__" + vid;
                        Vertex v = getvertex(vName);
                        if (!g.containsVertex(v)) {
                            g.addVertex(Vertex(vName, vid));
                            updateMainBar();
                        }
                        HashSet<String> writeForkVertex = new HashSet<String>();
                        writeForkVertex.add(vName);
                        if (dependencyGraphRelations.getForkwriteEdges().containsKey(taskName)) {
                            if (!dependencyGraphRelations.getForkwriteEdges().get(taskName).contains(vName)) {
                                dependencyGraphRelations.getForkwriteEdges().get(taskName).add(vName);
                            }
                        } else {
                            dependencyGraphRelations.getForkwriteEdges().put(taskName, writeForkVertex);
                        }
                        // g.addEdge(getvertex(taskName),getvertex(((TMLWriteChannel)(currentElement)).getChannel(0).getName()));
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (taskName.startsWith(FORK_TASK_S) && currentElement.getName().equals(WAIT_OF_FORK)) {
                        String name = ((TMLWaitEvent) (currentElement)).getEvent().getName();
                        int id = ((TMLWaitEvent) (currentElement)).getEvent().getID();
                        name = name + "__" + id;
                        if (!g.containsVertex(getvertex(name))) {
                            g.addVertex(Vertex(name, id));
                            updateMainBar();
                        }
                        g.addEdge(getvertex(taskName), getvertex(name));
                        HashSet<String> readForkVertex = new HashSet<String>();
                        readForkVertex.add(name);
                        if (dependencyGraphRelations.getForkreadEdges().containsKey(taskName)) {
                            if (!dependencyGraphRelations.getForkreadEdges().get(taskName).contains(name)) {
                                dependencyGraphRelations.getForkreadEdges().get(taskName).add(name);
                            }
                        } else {
                            dependencyGraphRelations.getForkreadEdges().put(taskName, readForkVertex);
                        }
                        opCount++;
                        updateMainBar();
                        currentElement = currentElement.getNexts().firstElement();
                        continue;
                    } else if (currentElement == null) {
                        opCount++;
                        updateMainBar();
                        if (currentElement.getNexts().size() > 0) {
                            currentElement = currentElement.getNexts().firstElement();
                        }
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
                    if (currentElement != null && currentElement instanceof TMLStopState) {
                        addStopVertex(taskName);
                    }
                    // start activity is added as a vertex
                    else if (currentElement != null && currentElement instanceof TMLStartState) {
                        addStartVertex(taskName);
                    }
                    // the below activities are added as vertex with the required edges
                    // these activities can be used to check later for latency
                    else if (currentElement != null && currentElement instanceof DIPLOElement) {
                        addcurrentElementVertex(taskName, taskStartName);
                    }
                    // check if the next activity :add to an array:
                    // in case of for loop : the first element of inside/outside branches of loop
                    // in case of sequence: add first element of all branches
                    if (currentElement != null) {
                        if (currentElement.getNexts().size() == 1) {
                            currentElement = currentElement.getNexts().firstElement();
                        } else if (!multiNexts.isEmpty()) {
                            trackMultiNexts(taskName, eventName);
                            currentElement = multiNexts.get(0);
                            multiNexts.remove(0);
                        }
                        dependencyGraphRelations.getAllForLoopNextValues()
                                .putAll(dependencyGraphRelations.getForLoopNextValues());
                    }
                }
            }
        }
        return;
    }

    private void trackMultiNexts(String taskName, String eventName) {
        if (currentElement != null && currentElement instanceof TMLForLoop
                && !((TMLForLoop) currentElement).isInfinite()) {
            if (currentElement.getNexts().size() > 1) {
                getvertex(eventName).setType(Vertex.TYPE_FOR_LOOP);
                String cond = ((TMLForLoop) (currentElement)).getCondition();
                if (cond.contains("<=")) {
                    String[] val = cond.split("<=");
                    String loopValue = val[2].toString();
                    // int loopVal = Integer.valueOf(loopValue);
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
                } else if (cond.contains("<")) {
                    String[] val = cond.split("<");
                    String loopValue = val[1].toString();
                    // int loopVal = Integer.valueOf(loopValue);
                    if ((loopValue != null) && (loopValue.length() > 0)) {
                        if ((loopValue.matches("\\d*"))) {
                            getvertex(eventName).setTaintFixedNumber(Integer.valueOf(loopValue));
                        } else {
                            for (TMLAttribute att : taskAc.getAttributes()) {
                                if (loopValue.contains(att.getName())) {
                                    loopValue = loopValue.replace(att.getName(), (att.getInitialValue()));
                                }
                            }
                            if ((loopValue.matches("\\d*"))) {
                                getvertex(eventName).setTaintFixedNumber(Integer.valueOf(loopValue));
                            } else {
                                this.getErrors().add(loopValue + EXPRESSION_NOT_SUPPORTED);
                            }
                        }
                    }
                }
                List<String> afterloopActivity = new ArrayList<String>(2);
                String insideLoop = "", outsideLoop = "";
                TMLActivityElement insideLoopElement = currentElement.getNexts().get(0);
                TMLActivityElement outsideLoopElement = currentElement.getNexts().get(1);
                insideLoop = getEventName(taskName, insideLoopElement);
                outsideLoop = getEventName(taskName, outsideLoopElement);
                afterloopActivity.add(0, insideLoop);
                afterloopActivity.add(1, outsideLoop);
                dependencyGraphRelations.getForLoopNextValues().put(eventName, afterloopActivity);
            }
        } else if (currentElement != null && currentElement instanceof TMLSequence) {
            getvertex(eventName).setType(Vertex.TYPE_SEQ);
            getvertex(eventName).setTaintFixedNumber(1);
            String nextEventName = "";
            for (TMLActivityElement seqListnextElement : currentElement.getNexts()) {
                nextEventName = getEventName(taskName, seqListnextElement);
                if (dependencyGraphRelations.getOrderedSequenceList().containsKey(eventName)) {
                    if (!dependencyGraphRelations.getOrderedSequenceList().get(eventName).contains(nextEventName)) {
                        dependencyGraphRelations.getOrderedSequenceList().get(eventName).add(nextEventName);
                    }
                } else {
                    ArrayList<String> seqListNextValues = new ArrayList<String>();
                    seqListNextValues.add(nextEventName);
                    dependencyGraphRelations.getOrderedSequenceList().put(eventName, seqListNextValues);
                }
            }
        } else if (currentElement != null && currentElement instanceof TMLRandomSequence) {
            getvertex(eventName).setType(Vertex.TYPE_UNORDER_SEQ);
            getvertex(eventName).setTaintFixedNumber(1);
            String nextEventName = "";
            for (TMLActivityElement seqListnextElement : currentElement.getNexts()) {
                nextEventName = getEventName(taskName, seqListnextElement);
                if (dependencyGraphRelations.getUnOrderedSequenceList().containsKey(eventName)) {
                    if (!dependencyGraphRelations.getUnOrderedSequenceList().get(eventName).contains(nextEventName)) {
                        dependencyGraphRelations.getUnOrderedSequenceList().get(eventName).add(nextEventName);
                    }
                } else {
                    ArrayList<String> seqListNextValues = new ArrayList<String>();
                    seqListNextValues.add(nextEventName);
                    dependencyGraphRelations.getUnOrderedSequenceList().put(eventName, seqListNextValues);
                }
            }
        }
    }

    private void addStartVertex(String taskName) {
        taskStartName = taskName + "__" + currentElement.getName().replace(" ", "") + "__" + currentElement.getID();
        Vertex startv = Vertex(taskStartName, currentElement.getID());
        g.addVertex(startv);
        updateMainBar();
        // gVertecies.add(vertex(taskStartName));
        getvertex(taskStartName).setType(Vertex.TYPE_START);
        getvertex(taskStartName).setTaintFixedNumber(1);
        g.addEdge(getvertex(taskName), getvertex(taskStartName));
        opCount++;
        if (!nameIDTaskList.containsKey(currentElement.getID())) {
            nameIDTaskList.put(String.valueOf(currentElement.getID()), taskStartName);
        }
    }

    private void waitEventNames() {
        for (TMLWaitEvent waitEvent : taskAc.getWaitEvents()) {
            TMLEvent event = waitEvent.getEvent();
            if (event.port != null && !event.port.isBlocking()) {
                warnings.add("Analysis may fail because the model contains non blocking sending port: "
                        + event.port.getPortName() + ". Use tainting analysis instead");
            }
            if (event.port != null && event.port.isFinite()) {
                warnings.add("Send event port:" + event.port.getPortName()
                        + " is Finite. Event lost is not supported in latency analysis");
            }
            String receivePortparams = waitEvent.getAllParams();
            String[] checkchannel;
            String sendingDataPortdetails = "";
            String receiveDataPortdetails = "";
            if (event.port != null && event.port2 != null) {
                String nameW = taskAc.getName() + "__" + WAIT_EVENT + event.getName();
                dependencyGraphRelations.getWaitEvt().put(nameW, new ArrayList<String>());
                TMLTask originTasks = waitEvent.getEvent().getOriginTask();
                for (TMLSendEvent wait_sendEvent : originTasks.getSendEvents()) {
                    event = wait_sendEvent.getEvent();
                    String nameS = originTasks.getName() + "__" + SEND_EVENT + event.getName();
                    dependencyGraphRelations.getWaitEvt().get(nameW).add(nameS);
                }
            } else {
                String sendingPortparams = null;
                if (waitEvent.getEvent().getOriginPort() != null) {
                    if (waitEvent.getEvent().getOriginPort().getName().contains(FORK_PORT_ORIGIN)) {
                        checkchannel = waitEvent.getEvent().getOriginPort().getName().split(S_LABEL);
                        warnings.add("Graph does not support FORK for events. Analysis may fail");
                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName()
                                    .replace(FORK_PORT_ORIGIN, FORK_EVENT);
                            sendingPortparams = waitEvent.getEvent().getParams().toString();
                        } else if (checkchannel.length <= 2) {
                            sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName()
                                    .replace(FORK_PORT_ORIGIN, "");
                            sendingDataPortdetails = sendingDataPortdetails.replace(S_LABEL, "");
                            sendingPortparams = waitEvent.getEvent().getParams().toString();
                        }
                    } else if (waitEvent.getEvent().getOriginPort().getName().contains(JOIN_PORT_ORIGIN)) {
                        checkchannel = waitEvent.getEvent().getOriginPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName()
                                    .replace(JOIN_PORT_ORIGIN, JOIN_EVENT);
                            sendingPortparams = waitEvent.getEvent().getParams().toString();
                        } else if ((checkchannel.length) <= 2) {
                            sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName()
                                    .replace(JOIN_PORT_ORIGIN, "");
                            sendingDataPortdetails = sendingDataPortdetails.replace(S_LABEL, "");
                            sendingPortparams = waitEvent.getEvent().getParams().toString();
                        }
                    } else {
                        sendingDataPortdetails = waitEvent.getEvent().getOriginPort().getName();
                        sendingPortparams = waitEvent.getEvent().getParams().toString();
                    }
                }
                if (waitEvent.getEvent().getDestinationPort() != null) {
                    if (waitEvent.getEvent().getDestinationPort().getName().contains(FORK_PORT_DESTINATION)) {
                        checkchannel = waitEvent.getEvent().getDestinationPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName()
                                    .replace(FORK_PORT_DESTINATION, FORK_EVENT);
                            receivePortparams = waitEvent.getEvent().getParams().toString();
                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName()
                                    .replace(FORK_PORT_DESTINATION, "");
                            receiveDataPortdetails = receiveDataPortdetails.replace(S_LABEL, "");
                            receivePortparams = waitEvent.getEvent().getParams().toString();
                        }
                    } else if (waitEvent.getEvent().getDestinationPort().getName().contains(JOIN_PORT_DESTINATION)) {
                        checkchannel = waitEvent.getEvent().getDestinationPort().getName().split(S_LABEL);
                        warnings.add("Graph doesn not support JOIN for events. Analysis may fail");
                        if (checkchannel.length > 2) {
                            receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName()
                                    .replace(JOIN_PORT_DESTINATION, JOIN_EVENT);
                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName()
                                    .replace(JOIN_PORT_DESTINATION, "");
                            receiveDataPortdetails = receiveDataPortdetails.replace(S_LABEL, "");
                            receivePortparams = waitEvent.getEvent().getParams().toString();
                        }
                    } else {
                        receiveDataPortdetails = waitEvent.getEvent().getDestinationPort().getName();
                        receivePortparams = waitEvent.getEvent().getParams().toString();
                    }
                }
                if (sendingDataPortdetails != null && receiveDataPortdetails != null && sendingDataPortdetails != ""
                        && receiveDataPortdetails != "") {
                    dependencyGraphRelations.getWaitEvt().put(
                            WAITEVENT + receiveDataPortdetails + "(" + receivePortparams + ")",
                            new ArrayList<String>());
                    dependencyGraphRelations.getWaitEvt()
                            .get(WAITEVENT + receiveDataPortdetails + "(" + receivePortparams + ")")
                            .add(SENDEVENT + sendingDataPortdetails + "(" + sendingPortparams + ")");
                } else {
                    String nameW = taskAc.getName() + "__" + WAIT_EVENT + event.getName();
                    dependencyGraphRelations.getWaitEvt().put(nameW, new ArrayList<String>());
                    TMLTask originTasks = waitEvent.getEvent().getOriginTask();
                    for (TMLSendEvent wait_sendEvent : originTasks.getSendEvents()) {
                        event = wait_sendEvent.getEvent();
                        String nameS = originTasks.getName() + "__" + SEND_EVENT + event.getName();
                        dependencyGraphRelations.getWaitEvt().get(nameW).add(nameS);
                    }
                }
            }
        }
    }

    private void writeChannelNames() {
        for (TMLWriteChannel writeChannel : taskAc.getWriteChannels()) {
            int i = writeChannel.getNbOfChannels();
            for (int j = 0; j < i; j++) {
                String sendingDataPortdetails = "";
                String receiveDataPortdetails = "";
                if ((writeChannel.getChannel(j)).originalDestinationTasks.size() > 0) {
                    String[] checkchannel;
                    if (writeChannel.getChannel(j).getOriginPort().getName().contains(FORK_PORT_ORIGIN)) {
                        checkchannel = writeChannel.getChannel(j).getOriginPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName()
                                    .replace(FORK_PORT_ORIGIN, FORK_CHANNEL);
                            ;
                        } else if (checkchannel.length < 2) {
                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName()
                                    .replace(FORK_PORT_ORIGIN, "");
                            ;
                            sendingDataPortdetails = sendingDataPortdetails.replace(S_LABEL, "");
                            ;
                        }
                    } else if (writeChannel.getChannel(j).getOriginPort().getName().contains(JOIN_PORT_ORIGIN)) {
                        checkchannel = writeChannel.getChannel(j).getOriginPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName()
                                    .replace(JOIN_PORT_ORIGIN, JOIN_CHANNEL);
                        } else if (checkchannel.length <= 2) {
                            sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName()
                                    .replace(JOIN_PORT_ORIGIN, "");
                            sendingDataPortdetails = sendingDataPortdetails.replace(S_LABEL, "");
                            ;
                        }
                    } else {
                        sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName();
                    }
                    if (writeChannel.getChannel(j).getDestinationPort().getName().contains(FORK_PORT_DESTINATION)) {
                        checkchannel = writeChannel.getChannel(j).getDestinationPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(FORK_PORT_DESTINATION, FORK_CHANNEL);
                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(FORK_PORT_DESTINATION, "");
                            receiveDataPortdetails = receiveDataPortdetails.replace(S_LABEL, "");
                        }
                    } else if (writeChannel.getChannel(j).getDestinationPort().getName()
                            .contains(JOIN_PORT_DESTINATION)) {
                        checkchannel = writeChannel.getChannel(j).getDestinationPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            receiveDataPortdetails = JOIN_CHANNEL + S_LABEL + checkchannel[1] + "__" + checkchannel[2];
                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(JOIN_PORT_DESTINATION, "");
                            receiveDataPortdetails = receiveDataPortdetails.replace(S_LABEL, "");
                        }
                    } else {
                        receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName();
                    }
                } else {
                    // writeChannel.getChannel(j);
                    if (writeChannel.getChannel(j).getOriginPort() != null
                            && writeChannel.getChannel(j).getDestinationPort() != null) {
                        sendingDataPortdetails = writeChannel.getChannel(j).getOriginPort().getName();
                        receiveDataPortdetails = writeChannel.getChannel(j).getDestinationPort().getName();
                    }
                }
                if (!sendingDataPortdetails.equals(receiveDataPortdetails) && sendingDataPortdetails != null
                        && receiveDataPortdetails != null) {
                    dependencyGraphRelations.getSendData().put(sendingDataPortdetails, receiveDataPortdetails);
                }
            }
        }
    }

    private void readChannelNames() {
        for (TMLReadChannel readChannel : taskAc.getReadChannels()) {
            int i = readChannel.getNbOfChannels();
            // name = _ch.getOriginPorts().get(0).getName(); //return the name of the source
            // port of the channel
            for (int j = 0; j < i; j++) {
                String sendingDataPortdetails = "";
                String receiveDataPortdetails = "";
                if ((readChannel.getChannel(j)).originalDestinationTasks.size() > 0) {
                    String[] checkchannel;
                    if (readChannel.getChannel(j).getOriginPort().getName().contains(FORK_PORT_ORIGIN)) {
                        checkchannel = readChannel.getChannel(j).getOriginPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName()
                                    .replace(FORK_PORT_ORIGIN, FORK_CHANNEL);
                        } else if (checkchannel.length <= 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName()
                                    .replace(FORK_PORT_ORIGIN, "");
                            sendingDataPortdetails = sendingDataPortdetails.replace(S_LABEL, "");
                            ;
                        }
                    } else if (readChannel.getChannel(j).getOriginPort().getName().contains(JOIN_PORT_ORIGIN)) {
                        checkchannel = readChannel.getChannel(j).getOriginPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName()
                                    .replace(JOIN_PORT_ORIGIN, JOIN_CHANNEL);
                        } else if ((checkchannel.length) <= 2) {
                            sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName()
                                    .replace(JOIN_PORT_ORIGIN, "");
                            sendingDataPortdetails = sendingDataPortdetails.replace(S_LABEL, "");
                        }
                    } else {
                        sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName();
                    }
                    if (readChannel.getChannel(j).getDestinationPort().getName().contains(FORK_PORT_DESTINATION)) {
                        checkchannel = readChannel.getChannel(j).getDestinationPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(FORK_PORT_DESTINATION, FORK_CHANNEL);
                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(FORK_PORT_DESTINATION, "");
                            receiveDataPortdetails = receiveDataPortdetails.replace(S_LABEL, "");
                        }
                    } else if (readChannel.getChannel(j).getDestinationPort().getName()
                            .contains(JOIN_PORT_DESTINATION)) {
                        checkchannel = readChannel.getChannel(j).getDestinationPort().getName().split(S_LABEL);
                        if (checkchannel.length > 2) {
                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(JOIN_PORT_DESTINATION, JOIN_CHANNEL);
                        } else if (checkchannel.length <= 2) {
                            receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName()
                                    .replace(JOIN_PORT_DESTINATION, "");
                            receiveDataPortdetails = receiveDataPortdetails.replace(S_LABEL, "");
                        }
                    } else {
                        receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName();
                    }
                } else {
                    if (readChannel.getChannel(j).getOriginPort() != null
                            && readChannel.getChannel(j).getDestinationPort() != null) {
                        sendingDataPortdetails = readChannel.getChannel(j).getOriginPort().getName();
                        receiveDataPortdetails = readChannel.getChannel(j).getDestinationPort().getName();
                    }
                }
                if (!sendingDataPortdetails.equals(receiveDataPortdetails) && sendingDataPortdetails != null
                        && receiveDataPortdetails != null) {
                    dependencyGraphRelations.getReceiveData().put(receiveDataPortdetails, sendingDataPortdetails);
                }
                if (readChannel.getChannel(j).getType() == 2 && readChannel.getChannel(j).getOriginPort() != null
                        && readChannel.getChannel(j).getDestinationPort() != null) {
                    warnings.add("Analysis may fail because the model contains non blocking sending port: "
                            + readChannel.getChannel(j).getDestinationPort().getName().toString()
                            + " and non blocking read data port:"
                            + readChannel.getChannel(j).getOriginPort().getName().toString()
                            + ". Use tainting analysis instead");
                }
            }
        }
    }

    private void sendEventsNames() {
        for (TMLSendEvent sendEvent : taskAc.getSendEvents()) {
            TMLTask destinationTasks = sendEvent.getEvent().getDestinationTask();
            TMLEvent event = sendEvent.getEvent();
            String nameS = taskAc.getName() + "__" + SEND_EVENT + event.getName();
            dependencyGraphRelations.getSendEvt().put(nameS, new ArrayList<String>());
            for (TMLWaitEvent wait_sendEvent : destinationTasks.getWaitEvents()) {
                event = wait_sendEvent.getEvent();
                String nameW = destinationTasks.getName() + "__" + WAIT_EVENT + event.getName();
                dependencyGraphRelations.getSendEvt().get(nameS).add(nameW);
            }
        }
    }

    private void requestedTask(HashSet<TMLTask> value) {
        for (TMLTask task : value) {
            if (task.isRequested()) {
                TMLRequest requestToTask = task.getRequest();
                String destinationRequest = requestToTask.getDestinationTask().getName() + "__"
                        + requestToTask.getDestinationTask().getActivityDiagram().get(0).getName() + "__"
                        + requestToTask.getDestinationTask().getActivityDiagram().get(0).getID();
                String destinationRequestName = requestToTask.getDestinationTask().getName();
                for (TMLTask originTask : requestToTask.getOriginTasks()) {
                    String requestOriginTaskName = originTask.getName();
                    if (dependencyGraphRelations.getRequestsOriginDestination().containsKey(requestOriginTaskName)) {
                        if (!dependencyGraphRelations.getRequestsOriginDestination().get(requestOriginTaskName)
                                .contains(destinationRequestName)) {
                            dependencyGraphRelations.getRequestsOriginDestination().get(requestOriginTaskName)
                                    .add(destinationRequestName);
                        }
                    } else {
                        ArrayList<String> destinationRequestNames = new ArrayList<String>();
                        destinationRequestNames.add(destinationRequestName);
                        dependencyGraphRelations.getRequestsOriginDestination().put(requestOriginTaskName,
                                destinationRequestNames);
                    }
                }
                for (int i = 0; i < requestToTask.ports.size(); i++) {
                    String requestsPortName = requestToTask.ports.get(i).getPortName();
                    if (dependencyGraphRelations.getRequestsPorts().containsKey(task.getName())) {
                        if (!dependencyGraphRelations.getRequestsPorts().get(task.getName())
                                .contains(requestsPortName)) {
                            dependencyGraphRelations.getRequestsPorts().get(task.getName()).add(requestsPortName);
                        }
                    } else {
                        ArrayList<String> requestsPortNames = new ArrayList<String>();
                        requestsPortNames.add(requestsPortName);
                        dependencyGraphRelations.getRequestsPorts().put(task.getName(), requestsPortNames);
                    }
                }
                if (dependencyGraphRelations.getRequestsDestination().containsKey(destinationRequestName)) {
                    if (!dependencyGraphRelations.getRequestsDestination().get(destinationRequestName)
                            .contains(destinationRequest)) {
                        dependencyGraphRelations.getRequestsDestination().get(destinationRequestName)
                                .add(destinationRequest);
                    }
                } else {
                    ArrayList<String> destinationRequestNames = new ArrayList<String>();
                    destinationRequestNames.add(destinationRequest);
                    dependencyGraphRelations.getRequestsDestination().put(destinationRequestName,
                            destinationRequestNames);
                }
            }
        }
    }

    private void addcurrentElementVertex(String taskName, String taskStartName) {
        String preEventName;
        int preEventid;
        String eventName = getEventName(taskName, currentElement);
        int eventid = currentElement.getID();
        preEventName = getEventName(taskName, activity.getPrevious(currentElement));
        preEventid = activity.getPrevious(currentElement).getID();
        if (!nameIDTaskList.containsKey(currentElement.getID())) {
            nameIDTaskList.put(String.valueOf(currentElement.getID()), eventName);
        }
        if (g.containsVertex(getvertex(preEventName))) {
            Vertex v = Vertex(eventName, eventid);
            Vertex preV = Vertex(preEventName, preEventid);
            g.addVertex(v);
            updateMainBar();
            g.addEdge(preV, v);
            opCount++;
        } else if ((activity.getPrevious(currentElement).getName().equals(START))
                && g.containsVertex(getvertex(taskStartName))) {
            Vertex v = Vertex(eventName, eventid);
            g.addVertex(v);
            updateMainBar();
            // gVertecies.add(vertex(eventName));
            g.addEdge(getvertex(taskStartName), getvertex(eventName));
            opCount++;
        }
        if (currentElement instanceof TMLSendEvent || currentElement instanceof TMLWaitEvent
                || currentElement instanceof TMLSendRequest || currentElement instanceof TMLNotifiedEvent
                || currentElement instanceof TMLReadChannel || currentElement instanceof TMLWriteChannel
                || currentElement instanceof TMLExecI || (currentElement instanceof TMLExecC)
                || currentElement instanceof TMLDelay || (currentElement instanceof TMLExecCInterval)
                || (currentElement instanceof TMLExecIInterval)) {
            allLatencyTasks.add(eventName);
            getvertex(eventName).setType(Vertex.TYPE_TRANSACTION);
            getvertex(eventName).setTaintFixedNumber(1);
        } else if (currentElement instanceof TMLRandom) {
            getvertex(eventName).setType(Vertex.TYPE_CTRL);
            getvertex(eventName).setTaintFixedNumber(1);
        } else if (currentElement instanceof TMLSelectEvt) {
            getvertex(eventName).setType(Vertex.TYPE_SELECT_EVT);
            getvertex(eventName).setTaintFixedNumber(1);
        } else if (currentElement instanceof TMLActionState) {
            getvertex(eventName).setType(Vertex.TYPE_CTRL);
            getvertex(eventName).setTaintFixedNumber(1);
        }
        if (currentElement != null && currentElement instanceof TMLForLoop
                && ((TMLForLoop) currentElement).isInfinite()) {
            dependencyGraphRelations.getForEverLoopList().add(eventName);
            getvertex(eventName).setType(Vertex.TYPE_FOR_EVER_LOOP);
            getvertex(eventName).setTaintFixedNumber(Integer.MAX_VALUE);
        }
        if (currentElement instanceof TMLChoice) {
            getvertex(eventName).setType(Vertex.TYPE_CHOICE);
            getvertex(eventName).setTaintFixedNumber(1);
        }
        if (currentElement instanceof TMLSendRequest) {
            if (dependencyGraphRelations.getRequestsOriginDestination().containsKey(taskName)) {
                for (String destinationTask : dependencyGraphRelations.getRequestsOriginDestination().get(taskName)) {
                    if (dependencyGraphRelations.getRequestsPorts().containsKey(destinationTask)) {
                        for (String portNames : dependencyGraphRelations.getRequestsPorts().get(destinationTask)) {
                            String[] requestName = ((TMLSendRequest) currentElement).toString().split(":");
                            String[] portname = requestName[1].split("[(]");
                            String[] pName = portname[0].split("__");
                            if (portNames.replaceAll(" ", "").equals(pName[1].replaceAll(" ", ""))) {
                                for (String destinationTaskstartname : dependencyGraphRelations.getRequestsDestination()
                                        .get(destinationTask)) {
                                    if (dependencyGraphRelations.getRequestEdges().containsKey(eventName)) {
                                        if (!dependencyGraphRelations.getRequestEdges().get(eventName)
                                                .contains(destinationTaskstartname)) {
                                            dependencyGraphRelations.getRequestEdges().get(eventName)
                                                    .add(destinationTaskstartname);
                                        }
                                    } else {
                                        HashSet<String> destinationTaskoriginstart = new HashSet<String>();
                                        destinationTaskoriginstart.add(destinationTaskstartname);
                                        dependencyGraphRelations.getRequestEdges().put(eventName,
                                                destinationTaskoriginstart);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (currentElement instanceof TMLSendEvent) {
            String name = getEventName(taskName, currentElement);
            String[] name1 = name.split("__");
            int i = name1[name1.length - 1].length();
            String pattern = "[0-9]{" + i + "}";
            if (name.substring(name.length() - i, name.length()).matches(pattern)) {
                name = name.substring(0, name.length() - (i + 2));
            }
            if (dependencyGraphRelations.getSendEvt().containsKey(name)) {
                List<String> recieveEvt = dependencyGraphRelations.getSendEvt().get(name);
                for (Vertex Vertex : g.vertexSet()) {
                    String vertexName = Vertex.toString();
                    for (String n : recieveEvt) {
                        String[] vertexName1 = vertexName.split("__");
                        int j = vertexName1[vertexName1.length - 1].length();
                        pattern = "[0-9]{" + j + "}";
                        if (vertexName.substring(vertexName.length() - j, vertexName.length()).matches(pattern)) {
                            vertexName = vertexName.substring(0, vertexName.length() - (j + 2));
                        }
                        if (n.equals(vertexName)) {
                            HashSet<String> waitEventVertex = new HashSet<String>();
                            waitEventVertex.add(Vertex.toString());
                            if (dependencyGraphRelations.getSendEventWaitEventEdges().containsKey(eventName)) {
                                if (!dependencyGraphRelations.getSendEventWaitEventEdges().get(eventName)
                                        .contains(Vertex.toString())) {
                                    dependencyGraphRelations.getSendEventWaitEventEdges().get(eventName)
                                            .add(Vertex.toString());
                                }
                            } else {
                                dependencyGraphRelations.getSendEventWaitEventEdges().put(eventName, waitEventVertex);
                            }
                        }
                    }
                }
            }
        }
        if (currentElement instanceof TMLWaitEvent) {
            String name = getEventName(taskName, currentElement);
            String[] name1 = name.split("__");
            int i = name1[name1.length - 1].length();
            String pattern = "[0-9]{" + i + "}";
            if (name.substring(name.length() - i, name.length()).matches(pattern)) {
                name = name.substring(0, name.length() - (i + 2));
            }
            if (dependencyGraphRelations.getWaitEvt().containsKey(name)) {
                List<String> sendevent = dependencyGraphRelations.getWaitEvt().get(name);
                for (Vertex Vertex : g.vertexSet()) {
                    String vertexName = Vertex.toString();
                    for (String n : sendevent) {
                        String[] vertexName1 = vertexName.split("__");
                        int j = vertexName1[vertexName1.length - 1].length();
                        pattern = "[0-9]{" + j + "}";
                        if (vertexName.substring(vertexName.length() - j, vertexName.length()).matches(pattern)) {
                            vertexName = vertexName.substring(0, vertexName.length() - (j + 2));
                        }
                        if (n.equals(vertexName)) {
                            HashSet<String> waitEventVertex = new HashSet<String>();
                            waitEventVertex.add(eventName);
                            if (dependencyGraphRelations.getSendEventWaitEventEdges().containsKey(Vertex.toString())) {
                                if (!dependencyGraphRelations.getSendEventWaitEventEdges().get(Vertex.toString())
                                        .contains(eventName)) {
                                    dependencyGraphRelations.getSendEventWaitEventEdges().get(Vertex.toString())
                                            .add(eventName);
                                }
                            } else {
                                dependencyGraphRelations.getSendEventWaitEventEdges().put(Vertex.toString(),
                                        waitEventVertex);
                            }
                        }
                    }
                }
            }
        }
        if (currentElement instanceof TMLWriteChannel) {
            dependencyGraphRelations.getWriteChannelTransactions().add(eventName);
            String chanel = ((TMLWriteChannel) currentElement).getChannel(0).getName();
            HashSet<String> writeChVertex = new HashSet<String>();
            writeChVertex.add(chanel);
            if (dependencyGraphRelations.getWriteReadChannelEdges().containsKey(eventName)) {
                if (!dependencyGraphRelations.getWriteReadChannelEdges().get(eventName).contains(chanel)) {
                    dependencyGraphRelations.getWriteReadChannelEdges().get(eventName).add(chanel);
                }
            } else {
                dependencyGraphRelations.getWriteReadChannelEdges().put(eventName, writeChVertex);
            }
            /*
             * String[] name = eventName.split("__"); String[] removewrite =
             * name[2].split(":"); String[] portname = removewrite[1].split("[(]"); String
             * chwriteName = (name[0] + "__" + portname[0]).replaceAll(" ", ""); String
             * portNameNoSpaces = portname[0].replaceAll(" ", ""); if
             * (sendData.containsKey(portNameNoSpaces)) { String sendDatachannels; if
             * (((TMLWriteChannel)
             * currentElement).getChannel(0).getName().contains(FORK_CHANNEL) ||
             * ((TMLWriteChannel)
             * currentElement).getChannel(0).getDestinationTask().getName().startsWith(
             * FORK_TASK) || ((TMLWriteChannel)
             * currentElement).getChannel(0).getOriginTask().getName().startsWith(FORK_TASK)
             * || ((TMLWriteChannel)
             * currentElement).getChannel(0).getName().contains(JOIN_CHANNEL) ||
             * ((TMLWriteChannel)
             * currentElement).getChannel(0).getDestinationTask().getName().startsWith(
             * JOIN_TASK) || ((TMLWriteChannel)
             * currentElement).getChannel(0).getOriginTask().getName().startsWith(JOIN_TASK)
             * ) { sendDatachannels = sendData.get(portNameNoSpaces); } else { //
             * sendDatachannels = name[0] + "__" + sendData.get(portNameNoSpaces) + "__" +
             * // name[0] + "__" + portNameNoSpaces; sendDatachannels = name[0] + "__" +
             * portNameNoSpaces + "__" + name[0] + "__" + sendData.get(portNameNoSpaces); }
             * HashSet<String> writeChVertex = new HashSet<String>();
             * writeChVertex.add(sendDatachannels); if
             * (dependencyGraphRelations.getWriteReadChannelEdges().containsKey(eventName))
             * { if (!writeReadChannelEdges.get(eventName).contains(sendDatachannels)) {
             * writeReadChannelEdges.get(eventName).add(sendDatachannels); } } else {
             * writeReadChannelEdges.put(eventName, writeChVertex); } //
             * getvertex(sendDatachannels).setTaintFixedNumber(getvertex(sendDatachannels).
             * getTaintFixedNumber() // + 1); } else { HashSet<String> writeChVertex = new
             * HashSet<String>(); writeChVertex.add(chwriteName); if
             * (writeReadChannelEdges.containsKey(eventName)) { if
             * (!writeReadChannelEdges.get(eventName).contains(chwriteName)) {
             * writeReadChannelEdges.get(eventName).add(chwriteName); } } else {
             * writeReadChannelEdges.put(eventName, writeChVertex); } //
             * getvertex(chwriteName).setTaintFixedNumber(getvertex(chwriteName).
             * getTaintFixedNumber() // + 1); }
             */
        }
        if (currentElement instanceof TMLReadChannel) {
            dependencyGraphRelations.getReadChannelTransactions().add(eventName);
            String chanel = ((TMLReadChannel) currentElement).getChannel(0).getName();
            HashSet<String> readChVertex = new HashSet<String>();
            readChVertex.add(eventName);
            if (dependencyGraphRelations.getReadWriteChannelEdges().containsKey(chanel)) {
                if (!dependencyGraphRelations.getReadWriteChannelEdges().get(chanel).contains(eventName)) {
                    dependencyGraphRelations.getReadWriteChannelEdges().get(chanel).add(eventName);
                }
            } else {
                dependencyGraphRelations.getReadWriteChannelEdges().put(chanel, readChVertex);
            }
            /*
             * String[] name = eventName.split("__"); String[] removewrite =
             * name[2].split(":"); String[] portname = removewrite[1].split("[(]"); String
             * chwriteName = (name[0] + "__" + portname[0]).replaceAll(" ", ""); String
             * portNameNoSpaces = portname[0].replaceAll(" ", ""); if
             * (receiveData.containsKey(portNameNoSpaces)) { String sendDatachannels; if
             * (((TMLReadChannel)
             * currentElement).getChannel(0).getName().contains(FORK_CHANNEL) ||
             * ((TMLReadChannel)
             * currentElement).getChannel(0).getDestinationTask().getName().startsWith(
             * FORK_TASK) || ((TMLReadChannel)
             * currentElement).getChannel(0).getOriginTask().getName().startsWith(FORK_TASK)
             * || ((TMLReadChannel)
             * currentElement).getChannel(0).getName().contains(JOIN_CHANNEL) ||
             * ((TMLReadChannel)
             * currentElement).getChannel(0).getDestinationTask().getName().startsWith(
             * JOIN_TASK) || ((TMLReadChannel)
             * currentElement).getChannel(0).getOriginTask().getName().startsWith(JOIN_TASK)
             * ) { sendDatachannels = receiveData.get(portNameNoSpaces); } else {
             * sendDatachannels = name[0] + "__" + receiveData.get(portNameNoSpaces) + "__"
             * + name[0] + "__" + portNameNoSpaces; } HashSet<String> readChVertex = new
             * HashSet<String>(); readChVertex.add(eventName); if
             * (readWriteChannelEdges.containsKey(sendDatachannels)) { if
             * (!readWriteChannelEdges.get(sendDatachannels).contains(eventName)) {
             * readWriteChannelEdges.get(sendDatachannels).add(eventName); } } else {
             * readWriteChannelEdges.put(sendDatachannels, readChVertex); } } else {
             * HashSet<String> readChVertex = new HashSet<String>();
             * readChVertex.add(eventName); if
             * (readWriteChannelEdges.containsKey(chwriteName)) { if
             * (!readWriteChannelEdges.get(chwriteName).contains(eventName)) {
             * readWriteChannelEdges.get(chwriteName).add(eventName); } } else {
             * readWriteChannelEdges.put(chwriteName, readChVertex); } // }
             */
        }
    }

    private void addStopVertex(String taskName) {
        String taskEndName = "";
        int taskEndid;
        int preEventid;
        String preEventName;
        String eventName = null;
        eventName = getEventName(taskName, currentElement);
        taskEndid = currentElement.getID();
        taskEndName = taskName + "__" + currentElement.getName().replace(" ", "") + "__" + taskEndid;
        preEventid = activity.getPrevious(currentElement).getID();
        preEventName = getEventName(taskName, activity.getPrevious(currentElement));
        Vertex taskEndVertex = Vertex(taskEndName, taskEndid);
        g.addVertex(taskEndVertex);
        updateMainBar();
        // gVertecies.add(vertex(taskEndName));
        getvertex(eventName).setType(Vertex.TYPE_END);
        getvertex(eventName).setTaintFixedNumber(1);
        // allTasks.add(taskEndName);
        if (!(activity.getPrevious(currentElement) instanceof TMLSequence)) {
            g.addEdge(getvertex(preEventName), getvertex(taskEndName));
        }
        @SuppressWarnings({ "unchecked", "rawtypes" })
        AllDirectedPaths<Vertex, DefaultEdge> allPaths = new AllDirectedPaths<Vertex, DefaultEdge>(g);
        if (dependencyGraphRelations.getOrderedSequenceList().size() > 0) {
            int noForLoop = 0;
            // get path from sequence to end
            for (Entry<String, ArrayList<String>> sequenceListEntry : dependencyGraphRelations.getOrderedSequenceList()
                    .entrySet()) {
                int directlyConnectedSeq = 0;
                if (g.containsVertex(getvertex(sequenceListEntry.getKey()))) {
                    List<GraphPath<Vertex, DefaultEdge>> path = allPaths.getAllPaths(
                            getvertex(sequenceListEntry.getKey()), getvertex(taskEndName), false, g.vertexSet().size());
                    for (Entry<String, ArrayList<String>> othersequenceListEntryValue : dependencyGraphRelations
                            .getOrderedSequenceList().entrySet()) {
                        for (int i = 0; i < path.size(); i++) {
                            if (!othersequenceListEntryValue.getKey().equals(sequenceListEntry.getKey())) {
                                if (path.get(i).getVertexList()
                                        .contains(getvertex(othersequenceListEntryValue.getKey()))) {
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
                                        if (dependencyGraphRelations.getForLoopNextValues().size() > 0) {
                                            for (Entry<String, List<String>> forloopListEntry : dependencyGraphRelations
                                                    .getForLoopNextValues().entrySet()) {
                                                if ((path.get(i).getVertexList()
                                                        .contains(getvertex(forloopListEntry.getValue().get(0)))
                                                        && getvertex(forloopListEntry.getValue().get(0))
                                                                .getName() != sequenceListEntry.getKey())
                                                        || path.get(i).getVertexList()
                                                                .contains(getvertex(sequenceListEntry.getValue().get(
                                                                        sequenceListEntry.getValue().size() - 1)))) {
                                                    noForLoop++;
                                                }
                                            }
                                        }
                                        if (dependencyGraphRelations.getForEverLoopList().size() > 0) {
                                            for (String forloopListEntry : dependencyGraphRelations
                                                    .getForEverLoopList()) {
                                                if (path.get(i).getVertexList().contains(getvertex(forloopListEntry))) {
                                                    noForLoop++;
                                                }
                                            }
                                        }
                                        if (noForLoop == 0) {
                                            int nextIndex = sequenceListEntry.getValue().indexOf(sequenceListEntryValue)
                                                    + 1;
                                            if (nextIndex < sequenceListEntry.getValue().size()) {
                                                HashSet<String> endSequenceVertex = new HashSet<String>();
                                                endSequenceVertex.add(sequenceListEntry.getValue().get(nextIndex));
                                                if (dependencyGraphRelations.getSequenceEdges()
                                                        .containsKey(taskEndName)) {
                                                    if (!dependencyGraphRelations.getSequenceEdges().get(taskEndName)
                                                            .contains(sequenceListEntry.getValue().get(nextIndex))) {
                                                        dependencyGraphRelations.getSequenceEdges().get(taskEndName)
                                                                .add(sequenceListEntry.getValue().get(nextIndex));
                                                    }
                                                } else {
                                                    dependencyGraphRelations.getSequenceEdges().put(eventName,
                                                            endSequenceVertex);
                                                }
                                            } else if (nextIndex == sequenceListEntry.getValue().size()
                                                    && dependencyGraphRelations.getOrderedSequenceList().size() > 1) {
                                                for (Entry<String, ArrayList<String>> othersequenceListEntryValue : dependencyGraphRelations
                                                        .getOrderedSequenceList().entrySet()) {
                                                    if (!othersequenceListEntryValue.getKey()
                                                            .equals(sequenceListEntry.getKey())) {
                                                        int connectedSeq = 0;
                                                        List<GraphPath<Vertex, DefaultEdge>> pathBetweenSeq = allPaths
                                                                .getAllPaths(
                                                                        getvertex(othersequenceListEntryValue.getKey()),
                                                                        getvertex(taskEndName), false,
                                                                        g.vertexSet().size());
                                                        for (int j = 0; j < pathBetweenSeq.size(); j++) {
                                                            for (Entry<String, ArrayList<String>> adjacentsequenceListEntryValue : dependencyGraphRelations
                                                                    .getOrderedSequenceList().entrySet()) {
                                                                if (!adjacentsequenceListEntryValue.getKey()
                                                                        .equals(sequenceListEntry.getKey())
                                                                        && !adjacentsequenceListEntryValue.getKey()
                                                                                .equals(othersequenceListEntryValue
                                                                                        .getKey())) {
                                                                    if (path.get(i).getVertexList().contains(getvertex(
                                                                            adjacentsequenceListEntryValue.getKey()))) {
                                                                        connectedSeq++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (connectedSeq == 0 && pathBetweenSeq.size() > 0) {
                                                            for (String othersequenceListValue : othersequenceListEntryValue
                                                                    .getValue()) {
                                                                List<GraphPath<Vertex, DefaultEdge>> pathToNextValue = allPaths
                                                                        .getAllPaths(getvertex(othersequenceListValue),
                                                                                getvertex(taskEndName), false,
                                                                                g.vertexSet().size());
                                                                if (pathToNextValue.size() > 0) {
                                                                    int nextAdjIndex = othersequenceListEntryValue
                                                                            .getValue().indexOf(othersequenceListValue)
                                                                            + 1;
                                                                    if (nextAdjIndex < othersequenceListEntryValue
                                                                            .getValue().size()) {
                                                                        HashSet<String> nextSequenceVertex = new HashSet<String>();
                                                                        nextSequenceVertex
                                                                                .add(othersequenceListEntryValue
                                                                                        .getValue().get(nextAdjIndex));
                                                                        if (dependencyGraphRelations.getSequenceEdges()
                                                                                .containsKey(taskEndName)) {
                                                                            if (!dependencyGraphRelations
                                                                                    .getSequenceEdges().get(taskEndName)
                                                                                    .contains(
                                                                                            othersequenceListEntryValue
                                                                                                    .getValue()
                                                                                                    .get(nextAdjIndex))) {
                                                                                dependencyGraphRelations
                                                                                        .getSequenceEdges()
                                                                                        .get(taskEndName)
                                                                                        .add(othersequenceListEntryValue
                                                                                                .getValue()
                                                                                                .get(nextAdjIndex));
                                                                            }
                                                                        } else {
                                                                            dependencyGraphRelations.getSequenceEdges()
                                                                                    .put(eventName, nextSequenceVertex);
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
        if (dependencyGraphRelations.getUnOrderedSequenceList().size() > 0) {
            // get path from sequence to end
            for (Entry<String, ArrayList<String>> sequenceListEntry : dependencyGraphRelations
                    .getUnOrderedSequenceList().entrySet()) {
                if (g.containsVertex(getvertex(sequenceListEntry.getKey()))) {
                    int noForLoop = 0;
                    List<GraphPath<Vertex, DefaultEdge>> path = allPaths.getAllPaths(
                            getvertex(sequenceListEntry.getKey()), getvertex(taskEndName), false, g.vertexSet().size());
                    for (int i = 0; i < path.size(); i++) {
                        if (path.size() > 0 && sequenceListEntry.getValue().size() > 0) {
                            if (dependencyGraphRelations.getForLoopNextValues().size() > 0) {
                                for (Entry<String, List<String>> forloopListEntry : dependencyGraphRelations
                                        .getForLoopNextValues().entrySet()) {
                                    if (path.get(i).getVertexList().contains(getvertex(forloopListEntry.getKey()))) {
                                        if (path.get(i).getVertexList()
                                                .contains(getvertex(forloopListEntry.getValue().get(0)))) {
                                            noForLoop++;
                                        }
                                    }
                                }
                            }
                            if (dependencyGraphRelations.getForEverLoopList().size() > 0) {
                                for (String forloopListEntry : dependencyGraphRelations.getForEverLoopList()) {
                                    if (path.get(i).getVertexList().contains(getvertex(forloopListEntry))) {
                                        noForLoop++;
                                    }
                                }
                            }
                            for (Entry<String, ArrayList<String>> seqEntry : dependencyGraphRelations
                                    .getOrderedSequenceList().entrySet()) {
                                if (path.get(i).getVertexList().contains(getvertex(seqEntry.getKey()))) {
                                    if (path.get(i).getVertexList().contains(
                                            getvertex(seqEntry.getValue().get(seqEntry.getValue().size() - 1)))) {
                                    } else {
                                        noForLoop++;
                                    }
                                }
                            }
                            if (noForLoop == 0) {
                                for (String seqEntry : sequenceListEntry.getValue()) {
                                    GraphPath<Vertex, DefaultEdge> pathToEnd = null;
                                    if (g.containsVertex(getvertex(seqEntry))) {
                                        pathToEnd = DijkstraShortestPath.findPathBetween(g, getvertex(seqEntry),
                                                getvertex(eventName));
                                    }
                                    if (pathToEnd == null) {
                                        if (dependencyGraphRelations.getUnOrderedSequenceEdges()
                                                .containsKey(taskEndName)) {
                                            if (!dependencyGraphRelations.getUnOrderedSequenceEdges().get(taskEndName)
                                                    .contains(sequenceListEntry.getKey())) {
                                                dependencyGraphRelations.getUnOrderedSequenceEdges().get(taskEndName)
                                                        .add(seqEntry);
                                            }
                                        } else {
                                            HashSet<String> endSequenceVertex = new HashSet<String>();
                                            endSequenceVertex.add(seqEntry);
                                            dependencyGraphRelations.getUnOrderedSequenceEdges().put(eventName,
                                                    endSequenceVertex);
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
        if (dependencyGraphRelations.getForLoopNextValues().size() > 0) {
            for (Entry<String, List<String>> forloopListEntry : dependencyGraphRelations.getForLoopNextValues()
                    .entrySet()) {
                if (g.containsVertex(getvertex(forloopListEntry.getValue().get(0)))) {
                    List<GraphPath<Vertex, DefaultEdge>> path = allPaths.getAllPaths(
                            getvertex(forloopListEntry.getValue().get(0)), getvertex(taskEndName), false,
                            g.vertexSet().size());
                    for (int i = 0; i < path.size(); i++) {
                        int forloopCount = 0;
                        for (Entry<String, List<String>> forEntry : dependencyGraphRelations.getForLoopNextValues()
                                .entrySet()) {
                            if (!forloopListEntry.getKey().equals(forEntry.getKey())) {
                                if (path.get(i).getVertexList().contains(getvertex(forEntry.getKey()))) {
                                    forloopCount++;
                                }
                            }
                        }
                        for (Entry<String, ArrayList<String>> seqEntry : dependencyGraphRelations
                                .getOrderedSequenceList().entrySet()) {
                            if (path.get(i).getVertexList().contains(getvertex(seqEntry.getKey()))) {
                                if (path.get(i).getVertexList()
                                        .contains(getvertex(seqEntry.getValue().get(seqEntry.getValue().size() - 1)))) {
                                } else {
                                    forloopCount++;
                                }
                            }
                        }
                        String forvertexName = forloopListEntry.getKey();
                        if (forloopCount == 0 && !g.containsEdge(getvertex(taskEndName), getvertex(forvertexName))) {
                            addedEdges.put(taskEndName, forvertexName);
                        }
                    }
                }
                if (g.containsVertex(getvertex(forloopListEntry.getValue().get(1)))
                        && dependencyGraphRelations.getForLoopNextValues().size() > 1) {
                    List<GraphPath<Vertex, DefaultEdge>> path = allPaths.getAllPaths(
                            getvertex(forloopListEntry.getValue().get(1)), getvertex(taskEndName), false,
                            g.vertexSet().size());
                    if (path.size() > 0) {
                        for (Entry<String, List<String>> previousForLoop : dependencyGraphRelations
                                .getForLoopNextValues().entrySet()) {
                            if (g.containsVertex(getvertex(previousForLoop.getValue().get(0)))
                                    && !previousForLoop.getKey().equals(forloopListEntry.getKey())) {
                                List<GraphPath<Vertex, DefaultEdge>> previousForpath = allPaths.getAllPaths(
                                        getvertex(previousForLoop.getValue().get(0)), getvertex(taskEndName), false,
                                        g.vertexSet().size());
                                for (int i = 0; i < previousForpath.size(); i++) {
                                    int forloopCount = 0;
                                    for (Entry<String, List<String>> forEntry : dependencyGraphRelations
                                            .getForLoopNextValues().entrySet()) {
                                        if (previousForpath.get(i).getVertexList()
                                                .contains(getvertex(forEntry.getKey()))
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
        if (!dependencyGraphRelations.getForEverLoopList().isEmpty()) {
            for (String loopforEver : dependencyGraphRelations.getForEverLoopList()) {
                List<GraphPath<Vertex, DefaultEdge>> pathforloopforever = allPaths.getAllPaths(getvertex(loopforEver),
                        getvertex(taskEndName), false, g.vertexSet().size());
                if (pathforloopforever.size() > 0) {
                    for (int i = 0; i < pathforloopforever.size(); i++) {
                        int forloopCount = 0;
                        for (Entry<String, List<String>> previousForLoop : dependencyGraphRelations
                                .getForLoopNextValues().entrySet()) {
                            if (pathforloopforever.get(i).getVertexList()
                                    .contains(getvertex(previousForLoop.getValue().get(0)))) {
                                forloopCount++;
                            }
                        }
                        for (Entry<String, ArrayList<String>> seqEntry : dependencyGraphRelations
                                .getOrderedSequenceList().entrySet()) {
                            if (pathforloopforever.get(i).getVertexList().contains(getvertex(seqEntry.getKey()))) {
                                if (pathforloopforever.get(i).getVertexList()
                                        .contains(getvertex(seqEntry.getValue().get(seqEntry.getValue().size() - 1)))) {
                                } else {
                                    forloopCount++;
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
        opCount++;
    }

    private String getEventName(String taskName, TMLActivityElement currentElement2) {
        String name = "";
        if (currentElement2 instanceof TMLWaitEvent) {
            TMLEvent event = ((TMLWaitEvent) currentElement2).getEvent();
            name = taskName + "__" + WAIT_EVENT + event.getName() + "__" + currentElement2.getID();
        } else if (currentElement2 instanceof TMLSendEvent) {
            TMLEvent event = ((TMLSendEvent) currentElement2).getEvent();
            name = taskName + "__" + SEND_EVENT + event.getName() + "__" + currentElement2.getID();
        } else {
            name = taskName + "__" + currentElement2.getName().replace(" ", "") + "__" + currentElement2.getID();
        }
        return name;
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
    public void showGraph(DependencyGraphTranslator dgraph) {
        JGraphXAdapter<Vertex, DefaultEdge> graphAdapter = new JGraphXAdapter<Vertex, DefaultEdge>(dgraph.getG());
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
        ComponentNameProvider<Vertex> vertexIDProvider = new ComponentNameProvider<Vertex>() {
            @Override
            public String getName(Vertex Vertex) {
                String name;
                for (Vertex v : g.vertexSet()) {
                    if (v.getName().equals(Vertex.getName())) {
                        name = Vertex.getName().toString().replaceAll("\\s+", "");
                        name = Vertex.getName().replaceAll("\\(", "\\u0028");
                        name = Vertex.getName().replaceAll("\\)", "\\u0029");
                        return name;
                    }
                }
                return null;
            }
        };
        ComponentNameProvider<Vertex> vertexNameProvider = new ComponentNameProvider<Vertex>() {
            @Override
            public String getName(Vertex arg0) {
                for (Vertex v : g.vertexSet()) {
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
                String target = g.getEdgeTarget(edge).toString().replaceAll("\\s+", "");
                target = target.replaceAll("\\(", "\\u0028");
                target = target.replaceAll("\\)", "\\u0029");
                return source + target;
            }
        };
        ComponentNameProvider<DefaultEdge> edgeLabelProvider = new ComponentNameProvider<DefaultEdge>() {
            @Override
            public String getName(DefaultEdge edge) {
                return Double.toString(g.getEdgeWeight(edge));
            }
        };
        GraphMLExporter<Vertex, DefaultEdge> exporter = new GraphMLExporter<Vertex, DefaultEdge>(vertexIDProvider,
                vertexNameProvider, edgeIDProvider, edgeLabelProvider);
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
    public Object[][] latencyDetailedAnalysis(String task12ID, String task22ID,
            Vector<SimulationTransaction> transFile1, Boolean taint, Boolean considerAddedRules) {
        try {
            for (Vertex v : g.vertexSet()) {
                v.setLabel(new ArrayList<String>());
                v.setMaxTaintFixedNumber(new HashMap<String, Integer>());
                v.setTaintConsideredNumber(new HashMap<String, Integer>());
                v.setVirtualLengthAdded(0);
                v.setSampleNumber(0);
            }
            transFile = transFile1;
            String message = "";
            String[] task1 = task12ID.split("__");
            int task1index = task1.length;
            idTask1 = task1[task1index - 1];
            String[] task2 = task22ID.split("__");
            int task2index = task2.length;
            idTask2 = task2[task2index - 1];
            String task12 = getvertexFromID(Integer.valueOf(idTask1)).toString();
            String task22 = getvertexFromID(Integer.valueOf(idTask2)).toString();
            Vertex v1 = getvertex(task12);
            Vector<SimulationTransaction> Task1Traces = new Vector<SimulationTransaction>();
            Vector<SimulationTransaction> Task2Traces = new Vector<SimulationTransaction>();
            HashMap<String, Vector<String>> Task1TaintedTraces = new LinkedHashMap<String, Vector<String>>();
            HashMap<String, Vector<String>> Task2TaintedTraces = new LinkedHashMap<String, Vector<String>>();
            GraphPath<Vertex, DefaultEdge> path2 = DijkstraShortestPath.findPathBetween(g, v1, getvertex(task22));
            times1.clear();
            times2.clear();
            Vector<SimulationTransaction> delayDueTosimTracesTaint = new Vector<SimulationTransaction>();
            dataBydelayedTasks = new HashMap<Integer, List<SimulationTransaction>>();
            dataByTask = null;
            relatedsimTraces = new Vector<SimulationTransaction>();
            mandatoryOptional = new Vector<SimulationTransaction>();
            delayDueTosimTraces = new Vector<SimulationTransaction>();
            dependencyGraphRelations.setRunnableTimePerDevice(new HashMap<String, ArrayList<ArrayList<Integer>>>());
            if (path2 != null && path2.getLength() > 0) {
                for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {
                    String ChannelName = entry.getKey();
                    ArrayList<String> busChList = entry.getValue();
                    GraphPath<Vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g, v1,
                            getvertex(ChannelName));
                    GraphPath<Vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g,
                            getvertex(ChannelName), getvertex(task22));
                    if (pathFromChannel != null && pathFromChannel.getLength() > 0) {
                        devicesToBeConsidered.addAll(busChList);
                    }
                }
            } else {
                for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {
                    String ChannelName = entry.getKey();
                    ArrayList<String> busChList = entry.getValue();
                    GraphPath<Vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g, v1,
                            getvertex(ChannelName));
                    GraphPath<Vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g,
                            getvertex(ChannelName), getvertex(task22));
                    if ((pathTochannel != null && pathTochannel.getLength() > 0)
                            || (pathFromChannel != null && pathFromChannel.getLength() > 0)) {
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
                        st.coreNumber = ZERO;
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
                        st.coreNumber = ZERO;
                    }
                    // ADD rules as edges
                    if (considerAddedRules) {
                        if (dependencyGraphRelations.getRuleAddedEdges().size() > 0) {
                            for (Entry<Vertex, List<Vertex>> rulevertex : dependencyGraphRelations.getRuleAddedEdges()
                                    .entrySet()) {
                                Vertex fromVertex = rulevertex.getKey();
                                List<Vertex> listOfToV = rulevertex.getValue();
                                for (Vertex toVertex : listOfToV) {
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
                    if (st.command.contains(SELECT_EVENT_PARAM)) {
                        st.command = st.command.replace(SELECT_EVENT, WAIT_ST + st.channelName);
                        String[] chN = st.channelName.split("__");
                        String eventN = chN[chN.length - 1];
                        Vertex v = getvertexFromID(id);
                        String vName = v.getName();
                        if (Graphs.vertexHasSuccessors(g, v)) {
                            for (Vertex vsec : Graphs.successorListOf(g, v)) {
                                if (vsec.getName().contains("__" + eventN + "__")) {
                                    st.id = String.valueOf(vsec.getId());
                                    id = vsec.getId();
                                }
                            }
                        }
                    } else if (st.command.contains(WAIT_REQ_LABEL)) {
                        Vertex v = getvertexFromID(id);
                        if (v.getType() == Vertex.TYPE_START) {
                            if (Graphs.vertexHasSuccessors(g, v)) {
                                for (Vertex vbefore : Graphs.successorListOf(g, v)) {
                                    if (vbefore.getName().contains(GET_REQ_ARG_LABEL)) {
                                        st.id = String.valueOf(vbefore.getId());
                                        id = vbefore.getId();
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
                            String[] chName = ChannelName.split("__");
                            String name = chName[0];
                            for (int i = 1; i < chName.length - 1; i++) {
                                name = name + "__" + chName[i];
                            }
                            if (bus1.equals(busName) && st.channelName.equals(name)) {
                                tasknameCheckID = ChannelName;
                                taskname = getvertex(ChannelName).getName();
                            }
                        }
                    }
                    if (tasknameCheckID.isEmpty()) {
                        for (Vertex tasknameCheck : g.vertexSet()) {
                            String[] taskToAdd = tasknameCheck.toString().replaceAll(" ", "").split("__");
                            int taskToAddindex = taskToAdd.length;
                            String taskToAddid = taskToAdd[taskToAddindex - 1];
                            if (isNumeric(taskToAddid)) {
                                if (Integer.valueOf(taskToAddid).equals(id)) {
                                    taskname = tasknameCheck.toString();
                                    tasknameCheckID = tasknameCheck.getName();
                                    if (taskname.equals(task12) && task1DeviceName.equals(st.deviceName)
                                            && task1CoreNbr.equals(st.coreNumber)) {
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
                    Vertex taskVertex = getvertex(tasknameCheckID);
                    if (taskVertex != null && Graphs.vertexHasSuccessors(g, taskVertex)
                            && !taskVertex.getLabel().isEmpty()) {
                        for (Vertex v : Graphs.successorListOf(g, taskVertex)) {
                            String labelToaAddtoV = getfirstCommonLabel(v, taskVertex);
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
                            if (v.getType() == Vertex.getTypeChannel() || v.getType() == Vertex.TYPE_TRANSACTION) {
                                if (v.getLabel().contains(labelToaAddtoV)) {
                                    if (v.getMaxTaintFixedNumber().containsKey(labelToaAddtoV)) {
                                        if (v.getMaxTaintFixedNumber().get(labelToaAddtoV) != v.getTaintFixedNumber()) {
                                            v.getMaxTaintFixedNumber().put(labelToaAddtoV,
                                                    v.getMaxTaintFixedNumber().get(labelToaAddtoV)
                                                            * v.getTaintFixedNumber());
                                        }
                                    }
                                } else {
                                    v.addLabel(labelToaAddtoV);
                                    v.getMaxTaintFixedNumber().put(labelToaAddtoV, v.getTaintFixedNumber());
                                }
                                for (Vertex subV : Graphs.successorListOf(g, v)) {
                                    if (!subV.equals(v1)) {
                                        if (!(subV.getType() == Vertex.TYPE_TRANSACTION
                                                || subV.getType() == Vertex.getTypeChannel())) {
                                            HashMap<Vertex, List<Vertex>> NonTransVertexes = new LinkedHashMap<Vertex, List<Vertex>>();
                                            HashMap<Vertex, List<Vertex>> NonTransVertexes2 = new LinkedHashMap<Vertex, List<Vertex>>();
                                            HashMap<Vertex, List<Vertex>> NonTransVertexesAdded = new LinkedHashMap<Vertex, List<Vertex>>();
                                            NonTransVertexes.putAll(taintingNonTransVertexes(v, taskVertex, v1));
                                            int addeditems = 0;
                                            for (Entry<Vertex, List<Vertex>> e : NonTransVertexes.entrySet()) {
                                                Vertex vet = e.getKey();
                                                List<Vertex> vl = e.getValue();
                                                for (Vertex ver : vl) {
                                                    NonTransVertexes2 = taintingNonTransVertexes(ver, vet, v1);
                                                    NonTransVertexesAdded.putAll(NonTransVertexes2);
                                                    // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet, v1));
                                                    addeditems = addeditems + NonTransVertexes2.size();
                                                }
                                            }
                                            while (addeditems > 0) {
                                                NonTransVertexes = new LinkedHashMap<Vertex, List<Vertex>>();
                                                NonTransVertexes.putAll(NonTransVertexesAdded);
                                                NonTransVertexesAdded = new LinkedHashMap<Vertex, List<Vertex>>();
                                                for (Entry<Vertex, List<Vertex>> e : NonTransVertexes.entrySet()) {
                                                    Vertex vet = e.getKey();
                                                    List<Vertex> vl = e.getValue();
                                                    for (Vertex ver : vl) {
                                                        NonTransVertexesAdded
                                                                .putAll(taintingNonTransVertexes(ver, vet, v1));
                                                        // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet,
                                                        // v1));
                                                        addeditems--;
                                                        addeditems = addeditems + NonTransVertexesAdded.size();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                HashMap<Vertex, List<Vertex>> NonTransVertexes = new LinkedHashMap<Vertex, List<Vertex>>();
                                HashMap<Vertex, List<Vertex>> NonTransVertexes2 = new LinkedHashMap<Vertex, List<Vertex>>();
                                HashMap<Vertex, List<Vertex>> NonTransVertexesAdded = new LinkedHashMap<Vertex, List<Vertex>>();
                                NonTransVertexes.putAll(taintingNonTransVertexes(v, taskVertex, v1));
                                int addeditems = 0;
                                for (Entry<Vertex, List<Vertex>> e : NonTransVertexes.entrySet()) {
                                    Vertex vet = e.getKey();
                                    List<Vertex> vl = e.getValue();
                                    for (Vertex ver : vl) {
                                        NonTransVertexes2 = taintingNonTransVertexes(ver, vet, v1);
                                        NonTransVertexesAdded.putAll(NonTransVertexes2);
                                        // NonTransVertexes.putAll(taintingNonTransVertexes(ver, vet, v1));
                                        addeditems = addeditems + NonTransVertexes2.size();
                                    }
                                }
                                while (addeditems > 0) {
                                    NonTransVertexes = new LinkedHashMap<Vertex, List<Vertex>>();
                                    NonTransVertexes.putAll(NonTransVertexesAdded);
                                    NonTransVertexesAdded = new LinkedHashMap<Vertex, List<Vertex>>();
                                    for (Entry<Vertex, List<Vertex>> e : NonTransVertexes.entrySet()) {
                                        Vertex vet = e.getKey();
                                        List<Vertex> vl = e.getValue();
                                        for (Vertex ver : vl) {
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
                    if (taskname == "") {
                        taskname = "";
                    }
                    if (taskname != "") {
                        hasLabelAstask12 = considerVertex(task12, taskname, st.virtualLength, st.command);
                    }
                    // remove rules edges
                    if (considerAddedRules) {
                        if (dependencyGraphRelations.getRuleAddedEdges().size() > 0) {
                            for (Entry<Vertex, List<Vertex>> rulevertex : dependencyGraphRelations.getRuleAddedEdges()
                                    .entrySet()) {
                                Vertex fromVertex = rulevertex.getKey();
                                List<Vertex> listOfToV = rulevertex.getValue();
                                for (Vertex toVertex : listOfToV) {
                                    if (g.containsVertex(fromVertex) && g.containsVertex(toVertex)
                                            && g.containsEdge(fromVertex, toVertex)) {
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
                    if (path2 != null && path2.getLength() > 0 && taskname != "") {
                        GraphPath<Vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, v1,
                                getvertex(taskname));
                        GraphPath<Vertex, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g,
                                getvertex(taskname), getvertex(task22));
                        if (taskname.equals(task12) || (hasLabelAstask12 && taskname.equals(task22))
                                || (hasLabelAstask12 && (pathToOrigin != null && pathToOrigin.getLength() > 0
                                        && pathToDestination != null && pathToDestination.getLength() > 0))) {
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
                                if (dependencyGraphRelations.getRunnableTimePerDevice().containsKey(dName)) {
                                    if (!dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                            .contains(timeValues)) {
                                        dependencyGraphRelations.getRunnableTimePerDevice().get(dName).add(timeValues);
                                    }
                                } else {
                                    ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                    timeValuesList.add(timeValues);
                                    dependencyGraphRelations.getRunnableTimePerDevice().put(dName, timeValuesList);
                                }
                            }
                        } else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                || devicesToBeConsidered.contains(deviceName))) {
                            delayDueTosimTraces.add(st);
                        }
                    } else {
                        if (!taskname.equals(null) && !taskname.equals("")) {
                            GraphPath<Vertex, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath
                                    .findPathBetween(g, v1, getvertex(taskname));
                            GraphPath<Vertex, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath
                                    .findPathBetween(g, getvertex(taskname), getvertex(task22));
                            if (taskname.equals(task12) || (hasLabelAstask12 && taskname.equals(task22))
                                    || (hasLabelAstask12 && (pathExistsTestwithTask1 != null
                                            && pathExistsTestwithTask1.getLength() > 0
                                            || pathExistsTestwithTask2 != null
                                                    && pathExistsTestwithTask2.getLength() > 0))) {
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
                                    if (dependencyGraphRelations.getRunnableTimePerDevice().containsKey(dName)) {
                                        if (!dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                                .contains(timeValues)) {
                                            dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                                    .add(timeValues);
                                        }
                                    } else {
                                        ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                        timeValuesList.add(timeValues);
                                        dependencyGraphRelations.getRunnableTimePerDevice().put(dName, timeValuesList);
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
                        dataByTask[i][2] = NO_TRANSACTION_FOUND;
                        dataByTask[i][3] = ZERO;
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
                                    endTime = s2;
                                    st.endTime = String.valueOf(s2);
                                    st.length = String.valueOf(s2 - startTime);
                                }
                                if (startTime < s1 && endTime != s1) {
                                    startTime = s1;
                                    st.startTime = String.valueOf(s1);
                                    st.length = String.valueOf(endTime - s1);
                                }
                                if (startTime < s1 && endTime > s2) {
                                    endTime = s2;
                                    startTime = s1;
                                    st.startTime = String.valueOf(s1);
                                    st.endTime = String.valueOf(s2);
                                    st.length = String.valueOf(s2 - s1);
                                }
                                relatedSTTaint.add(st);
                            }
                        }
                    }
                    dataByTaskR.put(i, relatedSTTaint);
                    timeDelayedPerRow.put(i, dependencyGraphRelations.getRunnableTimePerDevice());
                    i++;
                }
                for (int row = 0; row < dataByTask.length; row++) {
                    for (SimulationTransaction st : delayDueTosimTraces) {
                        int startTime = Integer.valueOf(st.startTime);
                        int endTime = Integer.valueOf(st.endTime);
                        if (!(startTime < times1.get(row) && endTime <= times1.get(row))
                                && !(startTime >= times2.get(row) && endTime > times2.get(row))) {
                            if (endTime > times2.get(row)) {
                                endTime = times2.get(row);
                                st.endTime = times2.get(row).toString();
                                st.length = String.valueOf(times2.get(row) - startTime);
                            }
                            if (startTime < times1.get(row)) {
                                startTime = times1.get(row);
                                st.startTime = times1.get(row).toString();
                                st.length = String.valueOf(endTime - times1.get(row));
                            }
                            if (startTime < times1.get(row) && endTime > times2.get(row)) {
                                endTime = times2.get(row);
                                startTime = times1.get(row);
                                st.startTime = times1.get(row).toString();
                                st.endTime = times2.get(row).toString();
                                st.length = String.valueOf(times2.get(row) - times1.get(row));
                            }
                            delayDueTosimTracesTaint.add(st);
                        }
                    }
                    dataBydelayedTasks.put(row, delayDueTosimTracesTaint);
                }
            } else {
                for (SimulationTransaction st : transFile1) {
                    int startTime = Integer.valueOf(st.startTime);
                    int endTime = Integer.valueOf(st.endTime);
                    int selectID = Integer.valueOf(st.id);
                    if (st.coreNumber == null) {
                        st.coreNumber = ZERO;
                    }
                    if (st.command.contains(SELECT_EVENT_PARAM) && getvertexFromID(selectID).getType() == 11) {
                        st.command = st.command.replace(SELECT_EVENT_PARAM, WAIT_LABEL + st.channelName);
                        Vertex selectV = getvertexFromID(selectID);
                        String[] chName = st.channelName.toString().split("__");
                        int waitEvntName = chName.length;
                        String waitEvnt = chName[waitEvntName - 1];
                        for (Vertex nextV : Graphs.successorListOf(g, selectV)) {
                            if (nextV.getName().contains(waitEvnt)) {
                                st.id = String.valueOf(nextV.getId());
                                selectID = nextV.getId();
                            }
                        }
                    } else if (st.command.contains(WAIT_REQ_LABEL)) {
                        Vertex v = getvertexFromID(selectID);
                        if (v.getType() == Vertex.TYPE_START) {
                            if (Graphs.vertexHasSuccessors(g, v)) {
                                for (Vertex vbefore : Graphs.successorListOf(g, v)) {
                                    if (vbefore.getName().contains(GET_REQ_ARG_LABEL)) {
                                        st.id = String.valueOf(vbefore.getId());
                                        selectID = vbefore.getId();
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
                for (int i = 0; i < minIndex; i++) {
                    HashMap<String, ArrayList<SimulationTransaction>> relatedHWs = new HashMap<String, ArrayList<SimulationTransaction>>();
                    HashMap<String, ArrayList<SimulationTransaction>> relatedTasks = new HashMap<String, ArrayList<SimulationTransaction>>();
                    relatedsimTraces = new Vector<SimulationTransaction>();
                    delayDueTosimTraces = new Vector<SimulationTransaction>();
                    dependencyGraphRelations
                            .setRunnableTimePerDevice(new HashMap<String, ArrayList<ArrayList<Integer>>>());
                    for (SimulationTransaction st : transFile1) {
                        int startTime = Integer.valueOf(st.startTime);
                        int endTime = Integer.valueOf(st.endTime);
                        int selectID = Integer.valueOf(st.id);
                        Boolean onPath = false;
                        if (!(startTime < times1.get(i) && endTime <= times1.get(i))
                                && !(startTime >= times2.get(i) && endTime > times2.get(i))) {
                            if (st.command.contains(SELECT_EVENT_PARAM) && getvertexFromID(selectID).getType() == 11) {
                                st.command = st.command.replace(SELECT_EVENT_PARAM, WAIT_LABEL + st.channelName);
                                Vertex selectV = getvertexFromID(selectID);
                                String[] chName = st.channelName.toString().split("__");
                                int waitEvntName = chName.length;
                                String waitEvnt = chName[waitEvntName - 1];
                                for (Vertex nextV : Graphs.successorListOf(g, selectV)) {
                                    if (nextV.getName().contains(waitEvnt)) {
                                        st.id = String.valueOf(nextV.getId());
                                        selectID = nextV.getId();
                                    }
                                }
                            } else if (st.command.contains(WAIT_REQ_LABEL)) {
                                Vertex v = getvertexFromID(selectID);
                                if (v.getType() == Vertex.TYPE_START) {
                                    if (Graphs.vertexHasSuccessors(g, v)) {
                                        for (Vertex vbefore : Graphs.successorListOf(g, v)) {
                                            if (vbefore.getName().startsWith(GET_REQ_ARG_LABEL)) {
                                                st.id = String.valueOf(vbefore.getId());
                                                selectID = vbefore.getId();
                                            }
                                        }
                                    }
                                }
                            }
                            if (endTime > times2.get(i)) {
                                endTime = times2.get(i);
                                st.endTime = times2.get(i).toString();
                                st.length = String.valueOf(times2.get(i) - startTime);
                            }
                            if (startTime < times1.get(i) && endTime != times1.get(i)) {
                                startTime = times1.get(i);
                                st.startTime = String.valueOf(times1.get(i));
                                st.length = String.valueOf(endTime - times1.get(i));
                            }
                            if (startTime < times1.get(i) && endTime > times2.get(i)) {
                                st.startTime = String.valueOf(times1.get(i));
                                st.endTime = times2.get(i).toString();
                                st.length = String.valueOf(times2.get(i) - times1.get(i));
                                startTime = times1.get(i);
                                endTime = times2.get(i);
                            }
                            String taskname = "";
                            for (Vertex tasknameCheck : g.vertexSet()) {
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
                                    GraphPath<Vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath
                                            .findPathBetween(g, v1, getvertex(taskname));
                                    GraphPath<Vertex, DefaultEdge> pathToDestination = DijkstraShortestPath
                                            .findPathBetween(g, getvertex(taskname), getvertex(task22));
                                    if (taskname.equals(task12) || taskname.equals(task22)
                                            || (pathToOrigin != null && pathToOrigin.getLength() > 0
                                                    && pathToDestination != null
                                                    && pathToDestination.getLength() > 0)) {
                                        relatedsimTraces.add(st);
                                        ArrayList<Integer> timeValues = new ArrayList<Integer>();
                                        timeValues.add(0, Integer.valueOf(st.runnableTime));
                                        timeValues.add(1, startTime);
                                        String dName = st.deviceName + "_" + st.coreNumber;
                                        if (!(st.runnableTime).equals(st.startTime)) {
                                            if (dependencyGraphRelations.getRunnableTimePerDevice()
                                                    .containsKey(dName)) {
                                                if (!dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                                        .contains(timeValues)) {
                                                    dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                                            .add(timeValues);
                                                }
                                            } else {
                                                ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                                timeValuesList.add(timeValues);
                                                dependencyGraphRelations.getRunnableTimePerDevice().put(dName,
                                                        timeValuesList);
                                            }
                                        }
                                    } else if (pathToDestination != null && pathToDestination.getLength() > 0) {
                                        mandatoryOptional.add(st);
                                        ArrayList<Integer> timeValues = new ArrayList<Integer>();
                                        timeValues.add(0, Integer.valueOf(st.runnableTime));
                                        timeValues.add(1, startTime);
                                        String dName = st.deviceName + "_" + st.coreNumber;
                                        if (!(st.runnableTime).equals(st.startTime)) {
                                            if (dependencyGraphRelations.getRunnableTimePerDevice()
                                                    .containsKey(dName)) {
                                                if (!dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                                        .contains(timeValues)) {
                                                    dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                                            .add(timeValues);
                                                }
                                            } else {
                                                ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                                timeValuesList.add(timeValues);
                                                dependencyGraphRelations.getRunnableTimePerDevice().put(dName,
                                                        timeValuesList);
                                            }
                                        }
                                    } else if (((st.deviceName.equals(task2DeviceName)
                                            && task2CoreNbr.equals(st.coreNumber))
                                            || (st.deviceName.equals(task1DeviceName)
                                                    && task1CoreNbr.equals(st.coreNumber))
                                            || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1)
                                            && !st.id.equals(idTask2)) {
                                        delayDueTosimTraces.add(st);
                                    }
                                }
                            } else {
                                if (!taskname.equals(null) && !taskname.equals("")) {
                                    GraphPath<Vertex, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath
                                            .findPathBetween(g, v1, getvertex(taskname));
                                    GraphPath<Vertex, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath
                                            .findPathBetween(g, getvertex(taskname), getvertex(task22));
                                    if (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                            || pathExistsTestwithTask2 != null
                                                    && pathExistsTestwithTask2.getLength() > 0) {
                                        relatedsimTraces.add(st);
                                    } else if (((st.deviceName.equals(task2DeviceName)
                                            && task2CoreNbr.equals(st.coreNumber))
                                            || (st.deviceName.equals(task1DeviceName)
                                                    && task1CoreNbr.equals(st.coreNumber))
                                            || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1)
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
                    if (times2.get(i) - times1.get(i) >= 0) {
                        dataByTask[i][4] = times2.get(i) - times1.get(i);
                    } else {
                        dataByTask[i][4] = "Assumption Does Not Hold; Please try Tainting";
                    }
                    dataByTask[i][5] = "";
                    dataByTaskR.put(i, relatedsimTraces);
                    dataBydelayedTasks.put(i, delayDueTosimTraces);
                    timeDelayedPerRow.put(i, dependencyGraphRelations.getRunnableTimePerDevice());
                    mandatoryOptionalSimT.put(i, mandatoryOptional);
                    // dataByTask[i][5] = list.getModel();
                    // dataByTask[i][6] = totalTime;
                }
            }
        } catch (Exception e) {
            e.getStackTrace()[0].getLineNumber();
        }
        return dataByTask;
    }

    public void saveDetailsToXML(String outputFile) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = builder.newDocument();
            Element root2 = dom.createElement(TRANSACTIONS);
            dom.appendChild(root2); // write DOM to XML file
            Element root1 = dom.createElement(MANDATORY);
            root2.appendChild(root1); // write DOM to XML file
            for (int i = 0; i < onPath.size(); i++) {
                SimulationTransaction st = onPath.get(i);
                // first create root element
                Element root = dom.createElement(ST_LABEL);
                root1.appendChild(root);
                // set `id` attribute to root element
                Attr attr = dom.createAttribute(ID_LABEL);
                attr.setValue(st.id);
                root.setAttributeNode(attr);
                // now create child elements
                Element operator = dom.createElement(OPERATOR_LABEL);
                operator.setTextContent(st.command);
                Element start = dom.createElement(START_TIME_LABEL);
                start.setTextContent(st.startTime);
                Element et = dom.createElement(END_TIME_LABEL);
                et.setTextContent(st.endTime);
                Element l = dom.createElement(LENGTH_LABEL);
                l.setTextContent(st.length);
                // add child nodes to root node
                root.appendChild(operator);
                root.appendChild(start);
                root.appendChild(et);
                root.appendChild(l);
            }
            Element root3 = dom.createElement(NO_CONTENTION_LABEL);
            root2.appendChild(root3); // write DOM to XML file
            for (int i = 0; i < offPath.size(); i++) {
                SimulationTransaction st = offPath.get(i);
                // first create root element
                Element root5 = dom.createElement(ST_LABEL);
                root3.appendChild(root5);
                // set `id` attribute to root element
                Attr attr = dom.createAttribute(ID_LABEL);
                attr.setValue(st.id);
                root5.setAttributeNode(attr);
                // now create child elements
                Element operator = dom.createElement(OPERATOR_LABEL);
                operator.setTextContent(st.command);
                Element start = dom.createElement(START_TIME_LABEL);
                start.setTextContent(st.startTime);
                Element et = dom.createElement(END_TIME_LABEL);
                et.setTextContent(st.endTime);
                Element l = dom.createElement(LENGTH_LABEL);
                l.setTextContent(st.length);
                // add child nodes to root node
                root5.appendChild(operator);
                root5.appendChild(start);
                root5.appendChild(et);
                root5.appendChild(l);
            }
            Element root4 = dom.createElement(CONTENTION_LABEL);
            root2.appendChild(root4); // write DOM to XML file
            for (int i = 0; i < offPathDelay.size(); i++) {
                SimulationTransaction st = offPathDelay.get(i);
                // first create root element
                Element root6 = dom.createElement(ST_LABEL);
                root4.appendChild(root6);
                // set `id` attribute to root element
                Attr attr = dom.createAttribute(ID_LABEL);
                attr.setValue(st.id);
                root6.setAttributeNode(attr);
                // now create child elements
                Element operator = dom.createElement(OPERATOR_LABEL);
                operator.setTextContent(st.command);
                Element start = dom.createElement(START_TIME_LABEL);
                start.setTextContent(st.startTime);
                Element et = dom.createElement(END_TIME_LABEL);
                et.setTextContent(st.endTime);
                Element l = dom.createElement(LENGTH_LABEL);
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
            Element root1 = dom.createElement(LATENCY_TABLE_LABEL);
            dom.appendChild(root1);
            for (int id = 0; id < dataByTask.length; id++) {
                String starttime = String.valueOf(dataByTask[id][1]);
                String endtime = String.valueOf(dataByTask[id][3]);
                String latency = String.valueOf(dataByTask[id][4]);
                String op1 = String.valueOf(dataByTask[id][0]);
                String op2 = String.valueOf(dataByTask[id][2]);
                // first create root element
                Element root = dom.createElement(ROW_LABEL);
                root1.appendChild(root);
                // set `id` attribute to root element
                Attr attr = dom.createAttribute(ID_LABEL);
                attr.setValue(String.valueOf(id));
                root.setAttributeNode(attr);
                // now create child elements
                Element operator1 = dom.createElement(OPERATOR_1);
                operator1.setTextContent(op1);
                Element operator2 = dom.createElement(OPERATOR_2);
                operator2.setTextContent(op2);
                Element st = dom.createElement(START_TIME_LABEL);
                st.setTextContent(starttime);
                Element et = dom.createElement(END_TIME_LABEL);
                et.setTextContent(endtime);
                Element lat = dom.createElement(LATENCY_LABEL);
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
            Element root = dom.createElement(MIN_LABEL);
            root1.appendChild(root);
            // now create child elements
            Element operator1 = dom.createElement(OPERATOR_1);
            operator1.setTextContent(op1);
            Element operator2 = dom.createElement(OPERATOR_2);
            operator2.setTextContent(op2);
            Element st = dom.createElement(START_TIME_LABEL);
            st.setTextContent(starttime);
            Element et = dom.createElement(END_TIME_LABEL);
            et.setTextContent(endtime);
            Element lat = dom.createElement(LATENCY_LABEL);
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
            Element rootMax = dom.createElement(MAX_LABEL);
            root1.appendChild(rootMax);
            // now create child elements
            Element operator1Max = dom.createElement(OPERATOR_1);
            operator1Max.setTextContent(op1Max);
            Element operator2Max = dom.createElement(OPERATOR_2);
            operator2Max.setTextContent(op2Max);
            Element stMax = dom.createElement(START_TIME_LABEL);
            stMax.setTextContent(starttimeMax);
            Element etMax = dom.createElement(END_TIME_LABEL);
            etMax.setTextContent(endtimeMax);
            Element latMax = dom.createElement(LATENCY_LABEL);
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

    private String getfirstCommonLabel(Vertex Vertex, Vertex v) {
        for (int i = 0; i < v.getLabel().size(); i++) {
            if (!Vertex.getLabel().contains(v.getLabel().get(i))) {
                return v.getLabel().get(i);
            }
        }
        for (int j = 0; j < v.getLabel().size(); j++) {
            if (Vertex.getMaxTaintFixedNumber().containsKey(v.getLabel().get(j))
                    && Vertex.getMaxTaintFixedNumber().get(v.getLabel().get(j)) == 0) {
                return v.getLabel().get(j);
            }
        }
        for (int j = 0; j < v.getLabel().size(); j++) {
            if (Vertex.getTaintConsideredNumber().containsKey(v.getLabel().get(j)) && Vertex.getTaintConsideredNumber()
                    .get(v.getLabel().get(j)) < Vertex.getMaxTaintFixedNumber().get(v.getLabel().get(j))) {
                return v.getLabel().get(j);
            }
        }
        if (v.getLabel().size() - 1 >= 0) {
            return v.getLabel().get(v.getLabel().size() - 1);
        }
        return v.getLabel().get(v.getLabel().size());
    }

    private Boolean considerVertex(String task12, String taskname, String virtualLength, String command) {
        boolean hasLabelAstask12 = false;
        Vertex v1 = getvertex(task12);
        Vertex v = getvertex(taskname);
        String Label = null;
        if (command.contains(WRITE) || command.contains(READ)) {
            String[] str = command.split(",");
            String[] str2 = null;
            if (command.contains(WRITE)) {
                str2 = str[0].split(WRITE);
            } else if (command.contains(READ)) {
                str2 = str[0].split(READ);
            }
            if (str2[1].trim().matches("\\d*")) {
                int snbr = Integer.parseInt(str2[1].trim());
                if (v != null && v.getSampleNumber() != snbr) {
                    v.setSampleNumber(snbr);
                }
            }
            if (v != null && v.getVirtualLengthAdded() < v.getSampleNumber()) {
                v.setVirtualLengthAdded(v.getVirtualLengthAdded() + Integer.parseInt(virtualLength));
            }
        }
        for (int i = 0; i < v.getLabel().size(); i++) {
            String labelConsidered = v.getLabel().get(i);
            int consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
            if (Graphs.vertexHasPredecessors(g, v)) {
                for (Vertex previousV : Graphs.predecessorListOf(g, v)) {
                    if (previousV.getType() == Vertex.TYPE_CHOICE) {
                        for (Entry<Vertex, List<Vertex>> vChoice : dependencyGraphRelations.getAllChoiceValues()
                                .entrySet()) {
                            if (previousV.equals(vChoice.getKey())) {
                                if (v1.getLabel().contains(v.getLabel().get(i))
                                        && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                                    if (v.getVirtualLengthAdded() == v.getSampleNumber()) {
                                        consideredNbr = vChoice.getKey().getTaintConsideredNumber()
                                                .get(labelConsidered);
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
                    if (previousV.getType() == Vertex.TYPE_SELECT_EVT) {
                        for (Entry<Vertex, List<Vertex>> vChoice : dependencyGraphRelations.getAllSelectEvtValues()
                                .entrySet()) {
                            if (previousV.equals(vChoice.getKey())) {
                                if (v1.getLabel().contains(v.getLabel().get(i))
                                        && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                                    if (v.getVirtualLengthAdded() == v.getSampleNumber()) {
                                        consideredNbr = vChoice.getKey().getTaintConsideredNumber()
                                                .get(labelConsidered);
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
                    } else if (previousV.getType() == Vertex.TYPE_SEQ) {
                        for (Entry<Vertex, List<Vertex>> vSeq : dependencyGraphRelations.getAllSeqValues().entrySet()) {
                            if (previousV.equals(vSeq.getKey())) {
                                if (v1.getLabel().contains(v.getLabel().get(i))
                                        && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
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
                    } else if (previousV.getType() == Vertex.TYPE_UNORDER_SEQ) {
                        if (v1.getLabel().contains(v.getLabel().get(i))
                                && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                            if (v.getVirtualLengthAdded() == v.getSampleNumber()) {
                                consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                                consideredNbr++;
                                v.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                                hasLabelAstask12 = true;
                                v.setVirtualLengthAdded(0);
                                for (Entry<Vertex, List<Vertex>> vSeq : dependencyGraphRelations.getAllRandomSeqValues()
                                        .entrySet()) {
                                    if (previousV.equals(vSeq.getKey())) {
                                        int count = 0;
                                        for (Vertex seqNext : vSeq.getValue()) {
                                            if (seqNext.getTaintConsideredNumber().get(labelConsidered) != seqNext
                                                    .getMaxTaintFixedNumber().get(labelConsidered)) {
                                                count++;
                                            }
                                        }
                                        if (count == 0) {
                                            if (previousV.getLabel().contains(labelConsidered)) {
                                                consideredNbr = previousV.getTaintConsideredNumber()
                                                        .get(labelConsidered);
                                                consideredNbr++;
                                                previousV.getTaintConsideredNumber().put(labelConsidered,
                                                        consideredNbr);
                                            }
                                        }
                                    }
                                }
                            } else {
                                Label = labelConsidered;
                                hasLabelAstask12 = true;
                            }
                        }
                    } else if ((previousV.getType() == Vertex.TYPE_FOR_LOOP
                            || previousV.getType() == Vertex.TYPE_STATIC_FOR_LOOP)
                            && (previousV.getLabel().contains(labelConsidered))) {
                        if (v1.getLabel().contains(v.getLabel().get(i)) && (previousV.getTaintConsideredNumber()
                                .get(labelConsidered) == previousV.getMaxTaintFixedNumber().get(labelConsidered) - 1)) {
                            if (v.getVirtualLengthAdded() == v.getSampleNumber()) {
                                for (Entry<String, List<String>> nextvertexOfLoop : dependencyGraphRelations
                                        .getAllForLoopNextValues().entrySet()) {
                                    Vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));
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
                    for (Vertex nextV : Graphs.successorListOf(g, v)) {
                        if (nextV.getType() == Vertex.TYPE_END) {
                            if (nextV.getLabel().contains(labelConsidered)) {
                                consideredNbr = nextV.getTaintConsideredNumber().get(labelConsidered);
                                if (consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                                    consideredNbr++;
                                    nextV.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                                }
                                for (Vertex subE : Graphs.successorListOf(g, nextV)) {
                                    if (subE.getType() == Vertex.TYPE_FOR_LOOP
                                            || subE.getType() == Vertex.TYPE_STATIC_FOR_LOOP) {
                                        consideredNbr = subE.getTaintConsideredNumber().get(labelConsidered);
                                        if (consideredNbr < subE.getMaxTaintFixedNumber().get(labelConsidered)) {
                                            consideredNbr++;
                                            subE.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                                        }
                                    }
                                }
                            }
                        } else if (nextV.getType() == Vertex.TYPE_START) {
                            consideredNbr = nextV.getTaintConsideredNumber().get(labelConsidered);
                            if (consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
                                consideredNbr++;
                                nextV.getTaintConsideredNumber().put(labelConsidered, consideredNbr);
                            }
                        }
                    }
                }
            }
            if (!hasLabelAstask12
                    && (v.getType() == Vertex.TYPE_TRANSACTION || v.getType() == Vertex.getTypeChannel())) {
                consideredNbr = v.getTaintConsideredNumber().get(labelConsidered);
                if (v1.getLabel().contains(v.getLabel().get(i))
                        && consideredNbr < v.getMaxTaintFixedNumber().get(labelConsidered)) {
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

    private HashMap<Vertex, List<Vertex>> taintingNonTransVertexes(Vertex subV, Vertex v, Vertex v1) {
        HashMap<Vertex, List<Vertex>> NonTransV = new LinkedHashMap<Vertex, List<Vertex>>();
        String label = getfirstCommonLabel(subV, v);
        int i = v.getMaxTaintFixedNumber().get(label);
        ;
        if (v.getType() == Vertex.TYPE_FOR_EVER_LOOP || v.getType() == Vertex.TYPE_STATIC_FOR_LOOP
                || v.getType() == Vertex.TYPE_FOR_LOOP) {
            for (Entry<String, List<String>> nextvertexOfLoop : dependencyGraphRelations.getAllForLoopNextValues()
                    .entrySet()) {
                Vertex vFor0 = getvertex(nextvertexOfLoop.getValue().get(0));
                Vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));
                if ((getvertex(nextvertexOfLoop.getKey())).equals(v)) {
                    if (subV.equals(vFor1)) {
                        int max = subV.getMaxTaintFixedNumber().get(label) / subV.getTaintFixedNumber();
                        i = max;
                    }
                }
            }
        }
        if (subV.getType() == Vertex.TYPE_FOR_EVER_LOOP) {
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
            for (Vertex subFor : Graphs.successorListOf(g, subV)) {
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
                        subFor.getMaxTaintFixedNumber().put(label,
                                subV.getMaxTaintFixedNumber().get(label) * subFor.getTaintFixedNumber());
                    }
                    if (subFor.getType() != Vertex.TYPE_TRANSACTION) {
                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subFor);
                        } else {
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(subFor);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
            }
        } else if ((subV.getType() == Vertex.TYPE_STATIC_FOR_LOOP || subV.getType() == Vertex.TYPE_FOR_LOOP)) {
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
            for (Entry<String, List<String>> nextvertexOfLoop : dependencyGraphRelations.getAllForLoopNextValues()
                    .entrySet()) {
                Vertex vFor0 = getvertex(nextvertexOfLoop.getValue().get(0));
                Vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));
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
                            vFor0.getMaxTaintFixedNumber().put(label,
                                    subV.getMaxTaintFixedNumber().get(label) * vFor0.getTaintFixedNumber());
                        }
                        if (vFor0.getType() != Vertex.TYPE_TRANSACTION) {
                            if (NonTransV.containsKey(subV)) {
                                NonTransV.get(subV).add(vFor0);
                            } else {
                                List<Vertex> lv = new ArrayList<Vertex>();
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
                        if (vFor1.getType() != Vertex.TYPE_TRANSACTION) {
                            if (NonTransV.containsKey(subV)) {
                                NonTransV.get(subV).add(vFor1);
                            } else {
                                List<Vertex> lv = new ArrayList<Vertex>();
                                lv.add(vFor1);
                                NonTransV.put(subV, lv);
                            }
                        }
                    }
                }
            }
        } else if (subV.getType() == Vertex.TYPE_CHOICE) {
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
            List<Vertex> subChoice = new ArrayList<Vertex>();
            for (Vertex subCh : Graphs.successorListOf(g, subV)) {
                subChoice.add(subCh);
                if (!subCh.equals(v1)) {
                    if (subCh.getLabel().contains(label)) {
                        if (subCh.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subCh.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subCh.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().put(label,
                                        subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber()));
                                subCh.getMaxTaintFixedNumber().put(label,
                                        subCh.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }
                    } else {
                        subCh.addLabel(label);
                        subCh.getMaxTaintFixedNumber().put(label,
                                subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber());
                    }
                    if (subCh.getType() != Vertex.TYPE_TRANSACTION) {
                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subCh);
                        } else {
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(subCh);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
            }
            dependencyGraphRelations.getAllChoiceValues().put(subV, subChoice);
        } else if (subV.getType() == Vertex.TYPE_SELECT_EVT) {
            if (!subV.getLabel().contains(label)) {
                subV.addLabel(label);
                subV.getMaxTaintFixedNumber().put(label, i * subV.getTaintFixedNumber());
            }
            List<Vertex> subChoice = new ArrayList<Vertex>();
            for (Vertex subCh : Graphs.successorListOf(g, subV)) {
                subChoice.add(subCh);
                if (!subCh.equals(v1)) {
                    if (subCh.getLabel().contains(label)) {
                        if (subCh.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subCh.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subCh.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().put(label,
                                        subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber()));
                                subCh.getMaxTaintFixedNumber().put(label,
                                        subCh.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }
                    } else {
                        subCh.addLabel(label);
                        subCh.getMaxTaintFixedNumber().put(label,
                                subV.getMaxTaintFixedNumber().get(label) * subCh.getTaintFixedNumber());
                    }
                    if (subCh.getType() != Vertex.TYPE_TRANSACTION) {
                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subCh);
                        } else {
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(subCh);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
            }
            dependencyGraphRelations.getAllSelectEvtValues().put(subV, subChoice);
        } else if (subV.getType() == Vertex.TYPE_END) {
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
            for (Vertex subSE : Graphs.successorListOf(g, subV)) {
                if (!subSE.equals(v1)) {
                    if (subSE.getType() == Vertex.TYPE_STATIC_FOR_LOOP || subSE.getType() == Vertex.TYPE_FOR_LOOP) {
                        if (subSE.getLabel().contains(label)) {
                            if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                                if (subSE.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber()
                                        .get(label)) {
                                    subSE.getMaxTaintFixedNumber().put(label,
                                            subSE.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                                }
                            }
                        } else {
                            subSE.addLabel(label);
                            subSE.getMaxTaintFixedNumber().put(label, subV.getMaxTaintFixedNumber().get(label));
                        }
                        for (Entry<String, List<String>> nextvertexOfLoop : dependencyGraphRelations
                                .getAllForLoopNextValues().entrySet()) {
                            if ((getvertex(nextvertexOfLoop.getKey())).equals(subSE)) {
                                Vertex vFor1 = getvertex(nextvertexOfLoop.getValue().get(1));
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
                                                vFor1.getMaxTaintFixedNumber().put(label,
                                                        vFor1.getMaxTaintFixedNumber().get(label) * max);
                                            }
                                        }
                                    } else {
                                        vFor1.addLabel(label);
                                        vFor1.getMaxTaintFixedNumber().put(label, vFor1.getTaintFixedNumber() * max);
                                    }
                                    if (vFor1.getType() != Vertex.TYPE_TRANSACTION) {
                                        if (NonTransV.containsKey(subSE)) {
                                            NonTransV.get(subSE).add(vFor1);
                                        } else {
                                            List<Vertex> lv = new ArrayList<Vertex>();
                                            lv.add(vFor1);
                                            NonTransV.put(subSE, lv);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (subSE.getType() == Vertex.TYPE_TRANSACTION) {
                        if (subSE.getLabel().contains(label)) {
                            if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                                if (subSE.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber()
                                        .get(label)) {
                                    subSE.getMaxTaintFixedNumber().put(label,
                                            subSE.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                                }
                            }
                        } else {
                            subSE.addLabel(label);
                            subSE.getMaxTaintFixedNumber().put(label,
                                    subSE.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                        }
                    } else if (subSE.getType() == Vertex.TYPE_FOR_EVER_LOOP) {
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
                            List<Vertex> lv = new ArrayList<Vertex>();
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
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(subSE);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
            }
        } else if (subV.getType() == Vertex.TYPE_START || subV.getType() == Vertex.TYPE_CTRL) {
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
            for (Vertex subSE : Graphs.successorListOf(g, subV)) {
                if (!subSE.equals(v1)) {
                    if (subSE.getLabel().contains(label)) {
                        if (subSE.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subSE.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber().get(label)) {
                                subSE.getMaxTaintFixedNumber().put(label,
                                        subSE.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }
                    } else {
                        subSE.addLabel(label);
                        subSE.getMaxTaintFixedNumber().put(label,
                                subSE.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                    }
                    if (subSE.getType() != Vertex.TYPE_TRANSACTION) {
                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subSE);
                        } else {
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(subSE);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
            }
        } else if (subV.getType() == Vertex.TYPE_SEQ) {
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
            List<Vertex> subSeq = new ArrayList<Vertex>();
            for (Vertex subSEQ : Graphs.successorListOf(g, subV)) {
                if (!subSEQ.equals(v1)) {
                    if (subSEQ.getLabel().contains(label)) {
                        if (subSEQ.getMaxTaintFixedNumber().containsKey(label)) {
                            if (subSEQ.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber()
                                    .get(label)) {
                                subSEQ.getMaxTaintFixedNumber().put(label,
                                        subSEQ.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }
                    } else {
                        subSEQ.addLabel(label);
                        subSEQ.getMaxTaintFixedNumber().put(label,
                                subSEQ.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                    }
                    if (subSEQ.getType() != Vertex.TYPE_TRANSACTION) {
                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(subSEQ);
                        } else {
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(subSEQ);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
                subSeq.add(subSEQ);
            }
            dependencyGraphRelations.getAllSeqValues().put(subV, subSeq);
        } else if (subV.getType() == Vertex.TYPE_UNORDER_SEQ) {
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
            List<Vertex> subSeq = new ArrayList<Vertex>();
            List<Vertex> preSeq = Graphs.predecessorListOf(g, subV);
            for (Vertex sub_UN_SEQ : Graphs.successorListOf(g, subV)) {
                if (preSeq.contains(sub_UN_SEQ)) {
                    continue;
                }
                if (!sub_UN_SEQ.equals(v1)) {
                    if (sub_UN_SEQ.getLabel().contains(label)) {
                        if (sub_UN_SEQ.getMaxTaintFixedNumber().containsKey(label)) {
                            if (sub_UN_SEQ.getMaxTaintFixedNumber().get(label) != subV.getMaxTaintFixedNumber()
                                    .get(label)) {
                                sub_UN_SEQ.getMaxTaintFixedNumber().put(label,
                                        sub_UN_SEQ.getMaxTaintFixedNumber().get(label) * subV.getTaintFixedNumber());
                            }
                        }
                    } else {
                        sub_UN_SEQ.addLabel(label);
                        sub_UN_SEQ.getMaxTaintFixedNumber().put(label,
                                sub_UN_SEQ.getTaintFixedNumber() * subV.getMaxTaintFixedNumber().get(label));
                    }
                    if (sub_UN_SEQ.getType() != Vertex.TYPE_TRANSACTION) {
                        if (NonTransV.containsKey(subV)) {
                            NonTransV.get(subV).add(sub_UN_SEQ);
                        } else {
                            List<Vertex> lv = new ArrayList<Vertex>();
                            lv.add(sub_UN_SEQ);
                            NonTransV.put(subV, lv);
                        }
                    }
                }
                subSeq.add(sub_UN_SEQ);
            }
            dependencyGraphRelations.getAllRandomSeqValues().put(subV, subSeq);
        }
        return NonTransV;
    }

    public Vertex getvertex(String task12) {
        for (Vertex v : g.vertexSet()) {
            if (v.getName().equals(task12)) {
                return v;
            }
        }
        return null;
    }

    protected Vertex getvertexFromID(int id) {
        for (Vertex v : g.vertexSet()) {
            if (v.getId() == (id)) {
                return v;
            }
        }
        return null;
    }

    private void addTaint(Vertex currentVertex) {
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
            char rndChar = DATA.charAt(random.nextInt(DATA.length()));
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

    public String[][] getMandatoryOptionalByRow(int row) {
        String[][] dataByTaskRowDetails = new String[mandatoryOptionalSimT.get(row).size()][5];
        int i = 0;
        for (SimulationTransaction st : mandatoryOptionalSimT.get(row)) {
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

    public List<SimulationTransaction> getMandatoryOptionalSimTTaks(int row) {
        return mandatoryOptionalSimT.get(row);
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

    public HashMap<String, ArrayList<ArrayList<Integer>>> getTimeDelayedPerRowMinMaxTainted(int row) {
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
        return timeDelayedPerRow.get(rowIndex);
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

    // fill the detailed latency table once a row is selected
    public HashMap<String, ArrayList<ArrayList<Integer>>> getRowDelayDetailsByHWMinMax(int row) {
        return timeDelayedPerRowMinMax.get(row);
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
        dependencyGraphRelations.setRunnableTimePerDevice(new HashMap<String, ArrayList<ArrayList<Integer>>>());
        // AllDirectedPaths<String, DefaultEdge> allPaths = new AllDirectedPaths<String,
        // DefaultEdge>(g);
        // List<GraphPath<String, DefaultEdge>> path = allPaths.getAllPaths(task12,
        // task22, false, g.vertexSet().size());
        // int size = path.size();
        GraphPath<Vertex, DefaultEdge> path2 = DijkstraShortestPath.findPathBetween(g, getvertex(task12),
                getvertex(task22));
        if (path2 != null && path2.getLength() > 0) {
            for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {
                String ChannelName = entry.getKey();
                ArrayList<String> busChList = entry.getValue();
                GraphPath<Vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g,
                        getvertex(task12), getvertex(ChannelName));
                GraphPath<Vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g,
                        getvertex(ChannelName), getvertex(task22));
                if (pathTochannel != null && pathTochannel.getLength() > 0 && pathFromChannel != null
                        && pathFromChannel.getLength() > 0) {
                    devicesToBeConsidered.addAll(busChList);
                }
            }
        } else {
            for (Entry<String, ArrayList<String>> entry : channelPaths.entrySet()) {
                String ChannelName = entry.getKey();
                ArrayList<String> busChList = entry.getValue();
                GraphPath<Vertex, DefaultEdge> pathTochannel = DijkstraShortestPath.findPathBetween(g,
                        getvertex(task12), getvertex(ChannelName));
                GraphPath<Vertex, DefaultEdge> pathFromChannel = DijkstraShortestPath.findPathBetween(g,
                        getvertex(ChannelName), getvertex(task22));
                if ((pathTochannel != null && pathTochannel.getLength() > 0)
                        || (pathFromChannel != null && pathFromChannel.getLength() > 0)) {
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
                    endTime = maxTime;
                    st.endTime = String.valueOf(maxTime);
                    st.length = String.valueOf(maxTime - startTime);
                }
                if (startTime < minTime) {
                    startTime = minTime;
                    st.startTime = String.valueOf(minTime);
                    st.length = String.valueOf(endTime - minTime);
                }
                if (startTime < minTime && endTime > maxTime) {
                    endTime = maxTime;
                    startTime = minTime;
                    st.startTime = String.valueOf(minTime);
                    st.endTime = String.valueOf(maxTime);
                    st.length = String.valueOf(maxTime - minTime);
                }
                String taskname = "";
                for (Vertex tasknameCheck : g.vertexSet()) {
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
                        GraphPath<Vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g,
                                getvertex(task12), getvertex(taskname));
                        GraphPath<Vertex, DefaultEdge> pathToDestination = DijkstraShortestPath.findPathBetween(g,
                                getvertex(taskname), getvertex(task22));
                        if (taskname.equals(task12) || taskname.equals(task22)
                                || (pathToOrigin != null && pathToOrigin.getLength() > 0 && pathToDestination != null
                                        && pathToDestination.getLength() > 0)) {
                            relatedsimTraces.add(st);
                            ArrayList<Integer> timeValues = new ArrayList<Integer>();
                            timeValues.add(0, Integer.valueOf(st.runnableTime));
                            timeValues.add(1, startTime);
                            if (!(st.runnableTime).equals(st.startTime)) {
                                String dName = st.deviceName + "_" + st.coreNumber;
                                if (dependencyGraphRelations.getRunnableTimePerDevice().containsKey(dName)) {
                                    if (!dependencyGraphRelations.getRunnableTimePerDevice().get(dName)
                                            .contains(timeValues)) {
                                        dependencyGraphRelations.getRunnableTimePerDevice().get(dName).add(timeValues);
                                    }
                                } else {
                                    ArrayList<ArrayList<Integer>> timeValuesList = new ArrayList<ArrayList<Integer>>();
                                    timeValuesList.add(timeValues);
                                    dependencyGraphRelations.getRunnableTimePerDevice().put(dName, timeValuesList);
                                }
                            }
                        } else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1)
                                && !st.id.equals(idTask2)) {
                            delayDueTosimTraces.add(st);
                        }
                    }
                    timeDelayedPerRowMinMax.put(row, dependencyGraphRelations.getRunnableTimePerDevice());
                } else {
                    if (!taskname.equals(null) && !taskname.equals("")) {
                        GraphPath<Vertex, DefaultEdge> pathExistsTestwithTask1 = DijkstraShortestPath.findPathBetween(g,
                                getvertex(task12), getvertex(taskname));
                        GraphPath<Vertex, DefaultEdge> pathExistsTestwithTask2 = DijkstraShortestPath.findPathBetween(g,
                                getvertex(taskname), getvertex(task22));
                        if (pathExistsTestwithTask1 != null && pathExistsTestwithTask1.getLength() > 0
                                || pathExistsTestwithTask2 != null && pathExistsTestwithTask2.getLength() > 0) {
                            relatedsimTraces.add(st);
                        } else if (((st.deviceName.equals(task2DeviceName) && task2CoreNbr.equals(st.coreNumber))
                                || (st.deviceName.equals(task1DeviceName) && task1CoreNbr.equals(st.coreNumber))
                                || devicesToBeConsidered.contains(deviceName)) && !st.id.equals(idTask1)
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
            dataByTaskRowDetails[i][2] = st.deviceName + "_" + st.coreNumber;
            dataByTaskRowDetails[i][3] = Integer.valueOf(st.startTime);
            dataByTaskRowDetails[i][4] = Integer.valueOf(st.endTime);
            i++;
        }
        return dataByTaskRowDetails;
    }

    // fill the Min max delay table on main latency analysis frame
    public Object[][] latencyMinMaxAnalysis(String task12ID, String task22ID,
            Vector<SimulationTransaction> transFile1) {
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

    // fill the Min max delay table on main latency analysis frame for tainted data
    public Object[][] latencyMinMaxAnalysisTaintedData(String task12ID, String task22ID,
            Vector<SimulationTransaction> transFile1) {
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
        // times1MinMAx = times1;
        // times2MinMAx = times2;
        for (int row = 0; row < dataByTask.length; row++) {
            times1MinMAx.add(Integer.valueOf((String) dataByTask[row][1]));
            times2MinMAx.add(Integer.valueOf((String) dataByTask[row][3]));
        }
        HashMap<Integer, ArrayList<Integer>> minTimes = new HashMap<Integer, ArrayList<Integer>>();
        int index = 0;
        for (int time1 : times1MinMAx) {
            int match = Integer.MAX_VALUE;
            // Find the first subsequent transaction
            int time = Integer.MAX_VALUE;
            if (times2MinMAx.size() > 0 && times2MinMAx.get(index) > 0) {
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
    public Graph<Vertex, DefaultEdge> importGraph(String filename) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(filename + ".graphml");
        Graph<Vertex, DefaultEdge> importedG = new DefaultDirectedGraph<>(DefaultEdge.class);
        GraphMLImporter<Vertex, DefaultEdge> importer;
        // gmlExporter.exportGraph(g, PS);
        // FileWriter PS2 = new FileWriter(filename + "test.graphml");
        VertexProvider<Vertex> vertexProvider = (id, attributes) -> {
            Vertex v = new Vertex(id, 0);
            return v;
        };
        EdgeProvider<Vertex, DefaultEdge> edgeProvider = (from, to, label, attributes) -> new DefaultEdge();
        importer = new GraphMLImporter<Vertex, DefaultEdge>(vertexProvider, edgeProvider);
        try {
            importer.importGraph(importedG, inputStream);
        } catch (ImportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return importedG;
    }

    public HashMap<String, String> getNameIDTaskList() {
        return nameIDTaskList;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public static void setScrollPane(JScrollPane scrollPane) {
        DependencyGraphTranslator.scrollPane = scrollPane;
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
        Vertex v1 = getvertex(task12);
        Vertex v2 = getvertex(task22);
        GraphPath<Vertex, DefaultEdge> pathToOrigin = DijkstraShortestPath.findPathBetween(g, v1, v2);
        if (pathToOrigin != null && pathToOrigin.getLength() > 0) {
            isPath = true;
        }
        if (isPath) {
            result = PATH_EXISTS;
        } else {
            result = NO_PATH;
        }
        return result;
    }

    public Vector<String> getreadChannelNodes() {
        return dependencyGraphRelations.getReadChannelTransactions();
    }

    public Vector<String> getwriteChannelNodes() {
        return dependencyGraphRelations.getWriteChannelTransactions();
    }

    public String addRule(String node1, String node2, Vector<String> writeChannelTransactions, String ruleDirection) {
        Vertex v1 = getvertex(node1);
        Vertex v2 = getvertex(node2);
        Vertex v1Channel = null, v2Channel = null;
        String message = "";
        if (v2Channel == null && Graphs.vertexHasSuccessors(g, v2)) {
            for (Vertex n : Graphs.successorListOf(g, v2)) {
                if (n.getType() == Vertex.getTypeChannel()) {
                    v2Channel = n;
                    break;
                }
            }
        }
        Boolean hasWriteVertex = false;
        if (Graphs.vertexHasPredecessors(g, v1)) {
            for (Vertex n : Graphs.predecessorListOf(g, v1)) {
                if (n.getType() == Vertex.getTypeChannel()) {
                    if (Graphs.vertexHasPredecessors(g, n)) {
                        for (Vertex writenode : Graphs.predecessorListOf(g, n)) {
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
                if (dependencyGraphRelations.getRuleAddedEdges().containsKey(v2Channel)) {
                    dependencyGraphRelations.getRuleAddedEdges().get(v2Channel).add(v1Channel);
                    message = "Rule between " + v1Channel + " and " + v2Channel + " was added";
                } else {
                    List<Vertex> sendVertex = new ArrayList<Vertex>();
                    sendVertex.add(v1Channel);
                    dependencyGraphRelations.getRuleAddedEdges().put(v2Channel, sendVertex);
                    message = "Rule between " + v1Channel + " and " + v2Channel + " was added";
                }
                if (dependencyGraphRelations.getRuleAddedEdgesChannels().containsKey(v2)) {
                    dependencyGraphRelations.getRuleAddedEdgesChannels().get(v2).add(v1);
                } else {
                    List<Vertex> sendVertex = new ArrayList<Vertex>();
                    sendVertex.add(v1);
                    dependencyGraphRelations.getRuleAddedEdgesChannels().put(v2, sendVertex);
                }
            }
        }
        if (message.isEmpty()) {
            message = "Couln't add rule between " + v1 + " and " + v2 + "";
        }
        return message;
    }

    public Boolean edgeExists(int vID1, int vID2) {
        Vertex v1 = getvertexFromID(vID1);
        Vertex v2 = getvertexFromID(vID2);
        if (g.containsEdge(v1, v2)) {
            return true;
        }
        return false;
    }

    public Vector<SimulationTransaction> parseFile(File file2) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.getMessage();
        } catch (SAXException e) {
            e.getMessage();
        }
        SimulationTransactionParser handler = new SimulationTransactionParser();
        try {
            saxParser.parse(file2, handler);
        } catch (SAXException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        }
        return handler.getStList();
    }

    public Graph<Vertex, DefaultEdge> getG() {
        return g;
    }

    public void setG(Graph<Vertex, DefaultEdge> g) {
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

    @Override
    protected Void doInBackground() throws Exception {
        return null;
    }

    public int getNodeNbProgressBar() {
        return nodeNbProgressBar;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public DependencyGraphRelations getDependencyGraphRelations() {
        return dependencyGraphRelations;
    }

    public boolean compareWithImported(String filename) {
        try {
            Graph<Vertex, DefaultEdge> importedGraph = importGraph(filename);
            for (Vertex vg : g.vertexSet()) {
                if (!importedGraph.vertexSet().contains(vg)) {
                    return false;
                }
            }
            for (DefaultEdge vg : g.edgeSet()) {
                if (!importedGraph.edgeSet().toString().contains(vg.toString())) {
                    return false;
                }
            }
            if (g.vertexSet().size() != importedGraph.vertexSet().size()) {
                return false;
            }
            if (g.edgeSet().size() != importedGraph.edgeSet().size()) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}