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

package tmltranslator;

import org.junit.Before;
import org.junit.Test;
import ui.AbstractUITest;

import java.util.*;

import static org.junit.Assert.*;

/**
 * class TMLComparingMethodTest This class consists of the methods for testing
 * equalSpec() and comparing lists of Hardware nodes, activity elements, etc
 * 
 * @version 1.0 27/09/2019
 * @author Minh Hiep PHAM
 */

public class TMLComparingMethodTest extends AbstractUITest {

    private TMLComparingMethod comparing;

    private HwLink hwLink1, hwLink2, hwLink3;

    private HwCPU hwCPU, cpu1, cpu2, cpu3, cpu4, cpu5, cpu6, cpu7, cpu8, cpu9, cpu10, cpu11, cpu12, cpu13, cpu14;
    private HwA hwa, hwa1, hwa2, hwa3, hwa4, hwa5, hwa6;
    private HwDMA hwDMA, dma1, dma2, dma3, dma4, dma5;
    private HwMemory hwMemory, hwMemory2, hwMemory3, memory1, memory2, memory3, memory4, memory5, memory6;
    private HwFPGA hwFPGA, fpga1, fpga2, fpga3, fpga4, fpga5, fpga6, fpga7, fpga8, fpga9, fpga10, fpga11, fpga12,
            fpga13;
    private HwBus hwBus, bus1, bus2, bus3, bus4, bus5, bus6, bus7, bus8;
    private HwBridge hwBridge, bridge1, bridge2, bridge3, bridge4;
    private HwNoC hwNoC, noc1, noc2, noc3, noc4, noc5, noc6;

    private List<HwNode> hwNodeList1, hwNodeList2, hwNodeList3, hwNodeList4, hwNodeList5, hwNodeList6;
    private List<HwLink> hwLinkList1, hwLinkList2, hwLinkList3, hwLinkList4, hwLinkList5, hwLinkList6;
    private List<HwExecutionNode> hwExecutionNodeList1, hwExecutionNodeList2, hwExecutionNodeList3,
            hwExecutionNodeList4, hwExecutionNodeList5, hwExecutionNodeList6;
    private List<HwCommunicationNode> hwCommunicationNodeList1, hwCommunicationNodeList2, hwCommunicationNodeList3,
            hwCommunicationNodeList4, hwCommunicationNodeList5, hwCommunicationNodeList6;

    private List<String[]> listOfStrArray1, listOfStrArray2, listOfStrArray3, listOfStrArray4, listOfStrArray5,
            listOfStrArray6;
    private List<HwMemory> memoryList1, memoryList2, memoryList3, memoryList4, memoryList5, memoryList6;

    private Map<SecurityPattern, List<HwMemory>> securityMap1, securityMap2, securityMap3, securityMap4, securityMap5,
            securityMap6;

    private SecurityPattern securityPattern1, securityPattern2, securityPattern3, securityPattern4, securityPattern5,
            securityPattern6, secuPt1, secuPt2, secuPt3, secuPt4, secuPt5, secuPt6, secuPt7, secuPt8, secuPt9, secuPt10,
            secuPt11, secuPt12;

    private TMLElement tmlElement1, tmlElement2, tmlElement3;
    private TMLActivity tmlActivity;
    private List<TMLElement> tmlElementList1, tmlElementList2, tmlElementList3, tmlElementList4, tmlElementList5,
            tmlElementList6;

    private TMLChannel channel1, channel2, channel3, channel4, tmlChannel, chl1, chl2, chl3, chl4, chl5, chl6, chl7,
            chl8, chl9, chl10, chl11, chl12, chl13;
    private TMLEvent event1, event2, event3, event4, tmlEvent, evt1, evt2, evt3, evt4, evt5, evt6, evt7, evt8, evt9;
    private TMLRequest tmlRequest, request1, request2, request3, request4, request5, request6, request7, request8;
    private TMLTask task1, task2, task3, tmlTask, tmlTask1, tmlTask2, tmlTask3, tmlTask4, tmlTask5, tmlTask6, tmlTask7,
            tmlTask8, tmlTask9, tmlTask10;

    private List<TMLTask> taskList1, taskList2, taskList3, taskList4, taskList5, taskList6;
    private Set<TMLChannel> channelSet1, channelSet2, channelSet3, channelSet4, channelSet5, channelSet6;
    private Set<TMLEvent> eventSet1, eventSet2, eventSet3, eventSet4, eventSet5, eventSet6;
    private TMLPort port1, port2, port3, tmlPort, tmlP1, tmlP2, tmlP3, tmlP4, tmlP5, tmlP6;
    private List<TMLPort> portList1, portList2, portList3, portList4, portList5, portList6;

    // For testing list of activity elements
    private TMLActionState tmlActionState;
    private TMLExecC tmlExecC;
    private TMLExecI tmlExecI;

    private TMLReadChannel tmlReadChannel;
    private TMLWriteChannel tmlWriteChannel;

    private TMLNotifiedEvent tmlNotifiedEvent;
    private TMLSendEvent tmlSendEvent;
    private TMLWaitEvent tmlWaitEvent;

    private TMLDelay tmlDelay;
    private TMLExecCInterval tmlExecCInterval;
    private TMLExecIInterval tmlExecIInterval;

    private TMLForLoop tmlForLoop;
    private TMLJunction tmlJunction;
    private TMLRandom tmlRandom;
    private TMLRandomSequence tmlRandomSequence;
    private TMLSelectEvt tmlSelectEvt;
    private TMLSendRequest tmlSendRequest;
    private TMLSequence tmlSequence;
    private TMLStartState tmlStartState;
    private TMLStopState tmlStopState;
    private TMLChoice tmlChoice;

    private List<TMLActivityElement> tmlActEltList1, tmlActEltList2, tmlActEltList3, tmlActEltList4, tmlActEltList5,
            tmlActEltList6;

    public TMLComparingMethodTest() {
        super();
        comparing = new TMLComparingMethod();
    }

    @Before
    public void setUp() {
        hwCPU = new HwCPU("CPU0");
        hwa = new HwA("HWA0");
        hwDMA = new HwDMA("DMA0");
        hwMemory = new HwMemory("Memory0");
        hwMemory2 = new HwMemory("memory2");
        hwMemory3 = new HwMemory("memory3");
        hwFPGA = new HwFPGA("FPGA0");
        hwBus = new HwBus("Bus0");
        hwBridge = new HwBridge("Bridge0");
        hwNoC = new HwNoC("NoC0");
        hwNoC.placementMap = hwNoC.makePlacementMap("CPU0", 2);

        hwLink1 = new HwLink("link_Bridge0_to_Bus0");
        hwLink1.setNodes(hwBus, hwBridge);
        hwLink2 = new HwLink("link_CPU0_to_Bus0");
        hwLink2.setNodes(hwBus, hwCPU);
        hwLink3 = new HwLink("link_DMA0_to_Bus0");
        hwLink3.setNodes(hwBus, hwDMA);

        hwLinkList1 = new ArrayList<>();
        hwLinkList2 = new ArrayList<>();
        hwLinkList3 = new ArrayList<>();
        hwLinkList4 = new ArrayList<>();
        hwLinkList5 = new ArrayList<>();
        hwLinkList6 = new ArrayList<>();

        hwNodeList1 = new ArrayList<>();
        hwNodeList2 = new ArrayList<>();
        hwNodeList3 = new ArrayList<>();
        hwNodeList4 = new ArrayList<>();
        hwNodeList5 = new ArrayList<>();
        hwNodeList6 = new ArrayList<>();

        hwExecutionNodeList1 = new ArrayList<>();
        hwExecutionNodeList2 = new ArrayList<>();
        hwExecutionNodeList3 = new ArrayList<>();
        hwExecutionNodeList4 = new ArrayList<>();
        hwExecutionNodeList5 = new ArrayList<>();
        hwExecutionNodeList6 = new ArrayList<>();

        hwCommunicationNodeList1 = new ArrayList<>();
        hwCommunicationNodeList2 = new ArrayList<>();
        hwCommunicationNodeList3 = new ArrayList<>();
        hwCommunicationNodeList4 = new ArrayList<>();
        hwCommunicationNodeList5 = new ArrayList<>();
        hwCommunicationNodeList6 = new ArrayList<>();

        listOfStrArray1 = new ArrayList<>();
        listOfStrArray2 = new ArrayList<>();
        listOfStrArray3 = new ArrayList<>();
        listOfStrArray4 = new ArrayList<>();
        listOfStrArray5 = new ArrayList<>();
        listOfStrArray6 = new ArrayList<>();

        memoryList1 = new ArrayList<>();
        memoryList2 = new ArrayList<>();
        memoryList3 = new ArrayList<>();
        memoryList4 = new ArrayList<>();
        memoryList5 = new ArrayList<>();
        memoryList6 = new ArrayList<>();

        securityMap1 = new HashMap<>();
        securityMap2 = new HashMap<>();
        securityMap3 = new HashMap<>();
        securityMap4 = new HashMap<>();
        securityMap5 = new HashMap<>();
        securityMap6 = new HashMap<>();

        securityPattern1 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100",
                "None", "formula1", "Key1");

        securityPattern2 = new SecurityPattern("securityPattern2", "Advanced", "6", "256", "200", "200", "None",
                "formula2", "Key2");

        securityPattern3 = new SecurityPattern("securityPattern3", "MAC", "7", "512", "300", "300", "None", "formula3",
                "Key3");

        securityPattern4 = new SecurityPattern("securityPattern3", "Hash", "8", "200", "400", "400", "None", "formula4",
                "Key3");

        securityPattern5 = new SecurityPattern("securityPattern3", "Advanced", "9", "500", "500", "500", "None",
                "formula5", "Key3");

        securityPattern6 = new SecurityPattern("securityPattern3", "Nonce", "10", "600", "600", "600", "None",
                "formula6", "Key3");

        tmlElement1 = new TMLElement("tmlElement1", null);
        tmlElement2 = new TMLElement("tmlElement2", null);
        tmlElement3 = new TMLElement("tmlElement3", null);

        tmlElementList1 = new ArrayList<>();
        tmlElementList2 = new ArrayList<>();
        tmlElementList3 = new ArrayList<>();
        tmlElementList4 = new ArrayList<>();
        tmlElementList5 = new ArrayList<>();
        tmlElementList6 = new ArrayList<>();

        channel1 = new TMLChannel("channel1", null);
        channel2 = new TMLChannel("channel2", null);
        channel3 = new TMLChannel("channel3", null);
        channel4 = new TMLChannel("channel4", null);

        event1 = new TMLEvent("event1", null, 3, false);
        event2 = new TMLEvent("event2", null, 4, true);
        event3 = new TMLEvent("event3", null, 5, true);
        event4 = new TMLEvent("event4", null, 6, true);

        task1 = new TMLTask("task1", null, null);
        task2 = new TMLTask("task2", null, null);
        task3 = new TMLTask("task3", null, null);

        port1 = new TMLPort("port1", null);
        port2 = new TMLPort("port2", null);
        port3 = new TMLPort("port3", null);

        tmlActivity = new TMLActivity("tmlActivity", null);

        tmlActionState = new TMLActionState("tmlACtionState", null);
        tmlExecC = new TMLExecC("tmlExecC", null);
        tmlExecI = new TMLExecI("tmlExecI", null);
        tmlReadChannel = new TMLReadChannel("tmlReadChannel", null);
        tmlWriteChannel = new TMLWriteChannel("tmlWriteChannel", null);
        tmlNotifiedEvent = new TMLNotifiedEvent("tmlNotifiedEvent", null);
        tmlSendEvent = new TMLSendEvent("tmlSendEvent", null);
        tmlWaitEvent = new TMLWaitEvent("tmlWaitEvent", null);
        tmlDelay = new TMLDelay("tmlDelay", null);
        tmlExecCInterval = new TMLExecCInterval("tmlExecCInterval", null);
        tmlExecIInterval = new TMLExecIInterval("tmlExecIInterval", null);
        tmlForLoop = new TMLForLoop("tmlForLoop", null);
        tmlJunction = new TMLJunction("tmlJunction", null);
        tmlRandom = new TMLRandom("tmlRandom", null);
        tmlRandomSequence = new TMLRandomSequence("tmlRandomSequence", null);
        tmlSelectEvt = new TMLSelectEvt("tmlSelectEvt", null);
        tmlSendRequest = new TMLSendRequest("tmlSendRequest", null);
        tmlSequence = new TMLSequence("tmlSequence", null);
        tmlStartState = new TMLStartState("tmlStartState", null);
        tmlStopState = new TMLStopState("tmlStopState", null);
        tmlChoice = new TMLChoice("tmlChoice", null);

        taskList1 = new ArrayList<>();
        taskList2 = new ArrayList<>();
        taskList3 = new ArrayList<>();
        taskList4 = new ArrayList<>();
        taskList5 = new ArrayList<>();
        taskList6 = new ArrayList<>();

        channelSet1 = new HashSet<>();
        channelSet2 = new HashSet<>();
        channelSet3 = new HashSet<>();
        channelSet4 = new HashSet<>();
        channelSet5 = new HashSet<>();
        channelSet6 = new HashSet<>();

        eventSet1 = new HashSet<>();
        eventSet2 = new HashSet<>();
        eventSet3 = new HashSet<>();
        eventSet4 = new HashSet<>();
        eventSet5 = new HashSet<>();
        eventSet6 = new HashSet<>();

        portList1 = new ArrayList<>();
        portList2 = new ArrayList<>();
        portList3 = new ArrayList<>();
        portList4 = new ArrayList<>();
        portList5 = new ArrayList<>();
        portList6 = new ArrayList<>();

        tmlActEltList1 = new ArrayList<>();
        tmlActEltList2 = new ArrayList<>();
        tmlActEltList3 = new ArrayList<>();
        tmlActEltList4 = new ArrayList<>();
        tmlActEltList5 = new ArrayList<>();
        tmlActEltList6 = new ArrayList<>();

        createHwNodeList();
        createHwCommunicationNodeList();
        createHwExecutionNodeList();
        createListOfStringArray();
        createHwMemoryList();
        createHwLinkList();
        createSecurityMap();
        createTMLElementList();
        createChannelSets();
        createEventSets();
        createdTaskLists();
        createdPortLists();
        createTMLActivityElementList();
        createHwCPUsForTestingDifferenceConfig();
        createHwAsForTestingDifferenceConfig();
        createHwFPGAForTestingDifferenceConfig();
        createHwBusForTestingDifferenceConfig();
        createHwBridgeForTestingDifferenceConfig();
        createHwDMAForTestingDifferenceConfig();
        createHwMemoryForTestingDifferenceConfig();
        createTMLChannelForTestingConfigs();
        createTMLEventForTestingConfigs();
        createTMLRequestForTestingConfigs();
        createTMLPortForTestingConfigs();
        createTMLTaskForTestingConfigs();
        createSecurityPatternForTestingConfigs();
        createHwNoCForTestingDifferenceConfig();

    }

    private void createSecurityPatternForTestingConfigs() {
        // For testing method equalSpec() in class SecurityPattern
        secuPt1 = new SecurityPattern("securityPattern1", "Advanced", "5", "128", "100", "100", "None", "formula1",
                "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt2 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "10", "128", "100", "100", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt3 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "256", "100", "100", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt4 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "200", "100", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt5 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "300", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt6 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100", "Yes",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt7 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100", "None",
                "formula2", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt8 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100", "None",
                "formula1", "Key2");

        // For testing method equalSpec() in class SecurityPattern
        secuPt9 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt10 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt11 = new SecurityPattern("DifferenceName", "Symmetric Encryption", "5", "128", "100", "100", "None",
                "formula1", "Key1");

        // For testing method equalSpec() in class SecurityPattern
        secuPt12 = new SecurityPattern("securityPattern1", "Symmetric Encryption", "5", "128", "100", "100", "None",
                "formula1", "Key1");

        secuPt9.originTask = "OriginTask";
        secuPt10.algorithm = "EDF";
    }

    private void createTMLTaskForTestingConfigs() {
        tmlTask = new TMLTask("tmlTask", null, null);
        tmlTask1 = new TMLTask("tmlTask", null, null);
        tmlTask2 = new TMLTask("tmlTask", null, null);
        tmlTask3 = new TMLTask("tmlTask", null, null);
        tmlTask4 = new TMLTask("tmlTask", null, null);
        tmlTask5 = new TMLTask("tmlTask", null, null);
        tmlTask6 = new TMLTask("tmlTask", null, null);
        tmlTask7 = new TMLTask("tmlTask", null, null);
        tmlTask8 = new TMLTask("tmlTask", null, null);
        tmlTask9 = new TMLTask("tmlTaskDiffName", null, null);
        tmlTask10 = new TMLTask("tmlTask", null, null);

        tmlTask1.setRequested(true);
        tmlTask2.setExit(true);
        tmlTask3.setPriority(10);
        tmlTask4.addOperationType(2);
        tmlTask5.addOperation("operation");
        tmlTask6.setDaemon(true);
        tmlTask7.addOperationMEC("operationMEC");
        tmlTask8.setAttacker(true);
    }

    private void createTMLPortForTestingConfigs() {
        tmlPort = new TMLPort("tmlPort", null);
        tmlP1 = new TMLPort("tmlPort", null);
        tmlP2 = new TMLPort("tmlPort", null);
        tmlP3 = new TMLPort("tmlPort", null);
        tmlP4 = new TMLPort("tmlPort", null);
        tmlP5 = new TMLPort("tmlPortDiffName", null);
        tmlP6 = new TMLPort("tmlPort", null);

        tmlP1.setPostex(true);
        tmlP2.setPrex(true);

        tmlP3.setAssociatedEvent("associatedEvent");
        tmlP4.setDataFlowType("String");
    }

    private void createTMLRequestForTestingConfigs() {
        tmlRequest = new TMLRequest("tmlRequest", null);
        request1 = new TMLRequest("tmlRequest", null);
        request2 = new TMLRequest("tmlRequest", null);
        request3 = new TMLRequest("tmlRequest", null);
        request4 = new TMLRequest("tmlRequest", null);
        request5 = new TMLRequest("tmlRequest", null);
        request6 = new TMLRequest("tmlRequest", null);
        request7 = new TMLRequest("tmlRequestDiffName", null);
        request8 = new TMLRequest("tmlRequest", null);

        request1.confStatus = 10;
        request2.checkConf = true;
        request3.checkAuth = true;

        request4.configLossy(true, 0, 0); // For testing isLossy
        request5.configLossy(false, 0, 20); // For testing max number of loss
        request6.configLossy(false, 10, 0); // For testing difference lossy percentage*/

    }

    private void createTMLEventForTestingConfigs() {
        tmlEvent = new TMLEvent("tmlEvent", null, 10, false);
        evt1 = new TMLEvent("tmlEvent", null, 5, false); // Test maxEvt
        evt2 = new TMLEvent("tmlEvent", null, 10, true); // Test isBlocking
        evt3 = new TMLEvent("tmlEvent", null, 10, false);
        evt4 = new TMLEvent("tmlEvent", null, 10, false);
        evt5 = new TMLEvent("tmlEvent", null, 10, false);
        evt6 = new TMLEvent("tmlEvent", null, 10, false);
        evt7 = new TMLEvent("tmlEvent", null, 10, false);
        evt8 = new TMLEvent("tmlEvent", null, 10, false);
        evt9 = new TMLEvent("tmlEvent", null, 10, false);

        evt3.setNotified(true); // For testing canBeNotified
        evt4.checkAuth = true;
        evt5.checkConf = true;

        evt6.configLossy(true, 0, 0); // For testing isLossy
        evt7.configLossy(false, 0, 20); // For testing max number of loss
        evt8.configLossy(false, 10, 0); // For testing difference lossy percentage*/
    }

    private void createTMLChannelForTestingConfigs() {
        tmlChannel = new TMLChannel("Channel", null);
        chl1 = new TMLChannel("Channel", null);
        chl2 = new TMLChannel("Channel", null);
        chl3 = new TMLChannel("Channel", null);
        chl4 = new TMLChannel("Channel", null);
        chl5 = new TMLChannel("Channel", null);
        chl6 = new TMLChannel("Channel", null);
        chl7 = new TMLChannel("Channel", null);
        chl8 = new TMLChannel("Channel", null);
        chl9 = new TMLChannel("Channel", null);
        chl10 = new TMLChannel("Channel", null);
        chl11 = new TMLChannel("Channel", null);
        chl12 = new TMLChannel("Channel", null);
        chl13 = new TMLChannel("ChannelDiffName", null);

        chl1.checkConf = true; // For testing difference checkConf
        chl2.checkAuth = true; // For testing difference checkAuth
        chl3.setSize(100); // For testing difference size
        chl4.setType(2); // For testing difference type
        chl5.setMax(100); // For testing difference max number if samples
        chl6.setNumberOfSamples(10); // For testing difference number of samples
        chl7.setPriority(3); // For testing difference priority
        chl8.setVC(3); // For testing difference vc
        chl9.configLossy(true, 0, 0); // For testing isLossy
        chl10.configLossy(false, 0, 20); // For testing max number of loss
        chl11.configLossy(false, 10, 0); // For testing difference lossy percentage*/

    }

    @Test
    public void Test_ComparingTwoSecurityPattern() {
        assertFalse(securityPattern1.equalSpec(secuPt1));
        assertFalse(securityPattern1.equalSpec(secuPt2));
        assertFalse(securityPattern1.equalSpec(secuPt3));
        assertFalse(securityPattern1.equalSpec(secuPt4));
        assertFalse(securityPattern1.equalSpec(secuPt5));
        assertFalse(securityPattern1.equalSpec(secuPt6));
        assertFalse(securityPattern1.equalSpec(secuPt7));
        assertFalse(securityPattern1.equalSpec(secuPt8));
        assertFalse(securityPattern1.equalSpec(secuPt9));
        assertFalse(securityPattern1.equalSpec(secuPt10));
        assertFalse(securityPattern1.equalSpec(secuPt11));
        assertTrue(securityPattern1.equalSpec(secuPt12));
    }

    @Test
    public void Test_ComparingTwosTMLTasks() {
        assertFalse(tmlTask.equalSpec(tmlTask1));
        assertFalse(tmlTask.equalSpec(tmlTask2));
        assertFalse(tmlTask.equalSpec(tmlTask3));
        assertFalse(tmlTask.equalSpec(tmlTask4));
        assertFalse(tmlTask.equalSpec(tmlTask5));
        assertFalse(tmlTask.equalSpec(tmlTask6));
        assertFalse(tmlTask.equalSpec(tmlTask7));
        assertFalse(tmlTask.equalSpec(tmlTask8));
        assertFalse(tmlTask.equalSpec(tmlTask9));
        assertTrue(tmlTask.equalSpec(tmlTask10));

    }

    @Test
    public void Test_ComparingTwoTMLPorts() {
        assertFalse(tmlPort.equalSpec(tmlP1));
        assertFalse(tmlPort.equalSpec(tmlP2));
        assertFalse(tmlPort.equalSpec(tmlP3));
        assertFalse(tmlPort.equalSpec(tmlP4));
        assertFalse(tmlPort.equalSpec(tmlP5));
        assertTrue(tmlPort.equalSpec(tmlP6));
    }

    @Test
    public void Test_ComparingTwoTMLRequests() {
        assertFalse(tmlRequest.equalSpec(request1));
        assertFalse(tmlRequest.equalSpec(request2));
        assertFalse(tmlRequest.equalSpec(request3));
        assertFalse(tmlRequest.equalSpec(request4));
        assertFalse(tmlRequest.equalSpec(request5));
        assertFalse(tmlRequest.equalSpec(request6));
        assertFalse(tmlRequest.equalSpec(request7));
        assertTrue(tmlRequest.equalSpec(request8));
    }

    @Test
    public void Test_ComparingTwoTMLEvents() {
        assertFalse(tmlEvent.equalSpec(evt1));
        assertFalse(tmlEvent.equalSpec(evt2));
        assertFalse(tmlEvent.equalSpec(evt3));
        assertFalse(tmlEvent.equalSpec(evt4));
        assertFalse(tmlEvent.equalSpec(evt5));
        assertFalse(tmlEvent.equalSpec(evt6));
        assertFalse(tmlEvent.equalSpec(evt7));
        assertFalse(tmlEvent.equalSpec(evt8));
        assertTrue(tmlEvent.equalSpec(evt9));
    }

    @Test
    public void Test_ComparingTwoTMLChannels() {
        assertFalse(tmlChannel.equalSpec(chl1));
        assertFalse(tmlChannel.equalSpec(chl2));
        assertFalse(tmlChannel.equalSpec(chl3));
        assertFalse(tmlChannel.equalSpec(chl4));
        assertFalse(tmlChannel.equalSpec(chl5));
        assertFalse(tmlChannel.equalSpec(chl6));
        assertFalse(tmlChannel.equalSpec(chl7));
        assertFalse(tmlChannel.equalSpec(chl8));
        assertFalse(tmlChannel.equalSpec(chl9));
        assertFalse(tmlChannel.equalSpec(chl10));
        assertFalse(tmlChannel.equalSpec(chl11));
        assertFalse(tmlChannel.equalSpec(chl13));
        assertTrue(tmlChannel.equalSpec(chl12));

    }

    private void createHwCPUsForTestingDifferenceConfig() {
        cpu1 = new HwCPU("CPU0");
        cpu2 = new HwCPU("CPU0");
        cpu3 = new HwCPU("CPU0");
        cpu4 = new HwCPU("CPU0");
        cpu5 = new HwCPU("CPU0");
        cpu6 = new HwCPU("CPU0");
        cpu7 = new HwCPU("CPU0");
        cpu8 = new HwCPU("CPU0");
        cpu9 = new HwCPU("CPU0");
        cpu10 = new HwCPU("CPU0");
        cpu11 = new HwCPU("CPU0");
        cpu12 = new HwCPU("CPU0");
        cpu13 = new HwCPU("CPU1");
        cpu14 = new HwCPU("CPU0");

        cpu1.encryption = 1; // For testing difference encryption;
        cpu2.nbOfCores = HwCPU.DEFAULT_NB_OF_CORES + 1; // For testing difference number of cores
        cpu3.byteDataSize = HwCPU.DEFAULT_BYTE_DATA_SIZE + 1; // For testing difference byte data size;
        cpu4.pipelineSize = HwCPU.DEFAULT_PIPELINE_SIZE + 1; // For testing difference pipe line size
        cpu5.goIdleTime = HwCPU.DEFAULT_GO_IDLE_TIME + 1; // For testing difference idle time
        cpu6.maxConsecutiveIdleCycles = HwCPU.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES + 1; // For testing difference
                                                                                       // maxConsecutiveIdleCycles
        cpu7.taskSwitchingTime = HwCPU.DEFAULT_TASK_SWITCHING_TIME + 1; // For testing difference taskSwitchingTime
        cpu8.branchingPredictionPenalty = HwCPU.DEFAULT_BRANCHING_PREDICTION_PENALTY + 1; // For testing difference
                                                                                          // branchingPredictionPenalty
        cpu9.cacheMiss = HwCPU.DEFAULT_CACHE_MISS + 1; // For testing difference cacheMiss
        cpu10.schedulingPolicy = HwCPU.DEFAULT_SCHEDULING + 1; // For testing difference schedulingPolicy
        cpu11.sliceTime = HwCPU.DEFAULT_SLICE_TIME + 1; // For testing difference sliceTime
        cpu14.setOperation("operation"); // For testing difference operation

    }

    private void createHwAsForTestingDifferenceConfig() {
        hwa1 = new HwA("HWA0");
        hwa2 = new HwA("HWA0");
        hwa3 = new HwA("HWA0");
        hwa4 = new HwA("HWA0");
        hwa5 = new HwA("HWA0");
        hwa6 = new HwA("HWA1");

        hwa1.byteDataSize = HwA.DEFAULT_BYTE_DATA_SIZE + 1; // For testing difference data size
        hwa2.execiTime = HwExecutionNode.DEFAULT_EXECI_TIME + 1; // For testing difference execiTime
        hwa3.clockRatio = HwA.DEFAULT_CLOCK_RATIO + 1; // For testing difference clockRatio
        hwa4.setOperation("Operation"); // For testing difference operation
    }

    private void createHwFPGAForTestingDifferenceConfig() {
        fpga1 = new HwFPGA("FPGA0");
        fpga2 = new HwFPGA("FPGA0");
        fpga3 = new HwFPGA("FPGA0");
        fpga4 = new HwFPGA("FPGA0");
        fpga5 = new HwFPGA("FPGA0");
        fpga6 = new HwFPGA("FPGA0");
        fpga7 = new HwFPGA("FPGA0");
        fpga8 = new HwFPGA("FPGA0");
        fpga9 = new HwFPGA("FPGA0");
        fpga10 = new HwFPGA("FPGA0");
        fpga11 = new HwFPGA("FPGA0");
        fpga12 = new HwFPGA("FPGA1");
        fpga13 = new HwFPGA("FPGA0");

        fpga1.byteDataSize = HwFPGA.DEFAULT_BYTE_DATA_SIZE + 1; // For testing difference byteDataSize
        fpga2.capacity = HwFPGA.DEFAULT_CAPACITY + 1; // For testing difference overall mapping capacity
        fpga3.mappingPenalty = HwFPGA.DEFAULT_MAPPING_PENALTY + 1; // For testing difference mapping penalty
        fpga4.reconfigurationTime = HwFPGA.DEFAULT_RECONFIGURATION_TIME + 1; // For testing difference
                                                                             // reconfigurationTime
        fpga5.goIdleTime = HwFPGA.DEFAULT_GO_IDLE_TIME + 1; // For testing difference do idle time
        fpga6.maxConsecutiveIdleCycles = HwFPGA.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES + 1; // For testing difference
                                                                                         // maxConsecutiveIdleCycles
        fpga7.execiTime = HwFPGA.DEFAULT_EXECI_TIME + 1; // For testing difference execiTime
        fpga8.execcTime = HwFPGA.DEFAULT_EXECC_TIME + 1; // For testing difference execcTime
        fpga9.setOperation("operation"); // For testing difference opereation;
        fpga10.setScheduling("EDF"); // For testing difference scheduling;
        fpga11.clockRatio = HwFPGA.DEFAULT_CLOCK_RATIO + 1; // For testing difference clockRatio
    }

    private void createHwNoCForTestingDifferenceConfig() {
        noc1 = new HwNoC("NoC0");
        noc2 = new HwNoC("NoC0");
        noc3 = new HwNoC("NoC0");
        noc4 = new HwNoC("NoC0");
        noc5 = new HwNoC("NoCDiffName");
        noc6 = new HwNoC("NoC0");

        noc1.bufferByteSize = HwNoC.DEFAULT_BUFFER_BYTE_DATA_SIZE + 1;
        noc2.latency = 1;
        noc3.size = 3;
        noc4.clockRatio = HwNoC.DEFAULT_CLOCK_RATIO + 1;
    }

    private void createHwBusForTestingDifferenceConfig() {
        bus1 = new HwBus("Bus0");
        bus2 = new HwBus("Bus0");
        bus3 = new HwBus("Bus0");
        bus4 = new HwBus("Bus0");
        bus5 = new HwBus("Bus0");
        bus6 = new HwBus("Bus0");
        bus7 = new HwBus("Bus1");
        bus8 = new HwBus("Bus0");

        bus1.arbitration = HwBus.DEFAULT_ARBITRATION + 1; // For testing difference arbitration
        bus2.byteDataSize = HwBus.DEFAULT_BYTE_DATA_SIZE + 1; // For testing difference byteDataSize
        bus3.pipelineSize = HwBus.DEFAULT_PIPELINE_SIZE + 1; // for testing difference pipelineSize
        bus4.sliceTime = HwBus.DEFAULT_SLICE_TIME + 1; // For testing difference slide time
        bus5.clockRatio = HwBus.DEFAULT_CLOCK_RATIO + 1; // For testing difference clockRatio
        bus6.privacy = HwBus.BUS_PRIVATE; // For testing difference bus privacy

    }

    private void createHwBridgeForTestingDifferenceConfig() {
        bridge1 = new HwBridge("Bridge0");
        bridge2 = new HwBridge("Bridge0");
        bridge3 = new HwBridge("Bridge1");
        bridge4 = new HwBridge("Bridge0");

        bridge1.bufferByteSize = HwBridge.DEFAULT_BUFFER_BYTE_DATA_SIZE + 1; // For testing difference bufferByteSize
        bridge2.clockRatio = HwBridge.DEFAULT_CLOCK_RATIO + 1; // For testing difference clockRatio
    }

    private void createHwDMAForTestingDifferenceConfig() {
        dma1 = new HwDMA("DMA0");
        dma2 = new HwDMA("DMA0");
        dma3 = new HwDMA("DMA0");
        dma4 = new HwDMA("DMA1");
        dma5 = new HwDMA("DMA0");

        dma1.byteDataSize = HwDMA.DEFAULT_BYTE_DATA_SIZE + 1; // For testing difference byteDataSize
        dma2.nbOfChannels = HwDMA.DEFAULT_NB_OF_CHANNELS + 1; // For testing number of channels
        dma3.clockRatio = HwDMA.DEFAULT_CLOCK_RATIO + 1; // For testing difference clockRatio
    }

    private void createHwMemoryForTestingDifferenceConfig() {
        memory1 = new HwMemory("Memory0");
        memory2 = new HwMemory("Memory0");
        memory3 = new HwMemory("Memory0");
        memory4 = new HwMemory("Memory0");
        memory5 = new HwMemory("Memory5");
        memory6 = new HwMemory("Memory0");

        memory1.byteDataSize = HwMemory.DEFAULT_BYTE_DATA_SIZE + 1; // For testing difference byteDataSize
        memory2.clockRatio = HwMemory.DEFAULT_CLOCK_RATIO + 1; // For testing difference clockRatio
        memory3.memorySize = HwMemory.DEFAULT_MEMORY_SIZE + 1024; // For testing difference memorySize
        memory4.bufferType = HwMemory.DEFAULT_BUFFER_TYPE + 100; // For testing difference bufferByteSize

    }

    @Test
    public void Test_ComparingTwoHwNoCs() {
        assertFalse(hwNoC.equalSpec(noc1));
        assertFalse(hwNoC.equalSpec(noc2));
        assertFalse(hwNoC.equalSpec(noc3));
        assertFalse(hwNoC.equalSpec(noc4));
        assertFalse(hwNoC.equalSpec(noc5));
        assertTrue(hwNoC.equalSpec(noc6));
    }

    @Test
    public void Test_ComparingTwoHwMemorys() {
        assertFalse(hwMemory.equalSpec(memory1));
        assertFalse(hwMemory.equalSpec(memory2));
        assertFalse(hwMemory.equalSpec(memory3));
        assertFalse(hwMemory.equalSpec(memory4));
        assertFalse(hwMemory.equalSpec(memory5));
        assertTrue(hwMemory.equalSpec(memory6));
    }

    @Test
    public void Test_ComparingTwoHwDMAs() {
        assertFalse(hwDMA.equalSpec(dma1));
        assertFalse(hwDMA.equalSpec(dma2));
        assertFalse(hwDMA.equalSpec(dma3));
        assertFalse(hwDMA.equalSpec(dma4));
        assertTrue(hwDMA.equalSpec(dma5));
    }

    @Test
    public void Test_ComparingTwoHwBridge() {
        assertFalse(hwBridge.equalSpec(bridge1));
        assertFalse(hwBridge.equalSpec(bridge2));
        assertFalse(hwBridge.equalSpec(bridge3));
        assertTrue(hwBridge.equalSpec(bridge4));
    }

    @Test
    public void Test_ComparingTwoHwBus() {
        assertFalse(hwBus.equalSpec(bus1));
        assertFalse(hwBus.equalSpec(bus2));
        assertFalse(hwBus.equalSpec(bus3));
        assertFalse(hwBus.equalSpec(bus4));
        assertFalse(hwBus.equalSpec(bus5));
        assertFalse(hwBus.equalSpec(bus6));
        assertFalse(hwBus.equalSpec(bus7));
        assertTrue(hwBus.equalSpec(bus8));
    }

    @Test
    public void Test_ComparingTwoHwFPGA() {
        assertFalse(hwFPGA.equalSpec(fpga1));
        assertFalse(hwFPGA.equalSpec(fpga2));
        assertFalse(hwFPGA.equalSpec(fpga3));
        assertFalse(hwFPGA.equalSpec(fpga4));
        assertFalse(hwFPGA.equalSpec(fpga5));
        assertFalse(hwFPGA.equalSpec(fpga6));
        assertFalse(hwFPGA.equalSpec(fpga7));
        assertFalse(hwFPGA.equalSpec(fpga8));
        assertFalse(hwFPGA.equalSpec(fpga9));
        assertFalse(hwFPGA.equalSpec(fpga10));
        assertFalse(hwFPGA.equalSpec(fpga11));
        assertFalse(hwFPGA.equalSpec(fpga12));
        assertTrue(hwFPGA.equalSpec(fpga13));
    }

    @Test
    public void Test_ComparingTwoHwAs() {
        assertFalse(hwa.equalSpec(hwa1));
        assertFalse(hwa.equalSpec(hwa2));
        assertFalse(hwa.equalSpec(hwa3));
        assertFalse(hwa.equalSpec(hwa4));
        assertTrue(hwa.equalSpec(hwa5));
        assertFalse(hwa.equalSpec(hwa6));
    }

    @Test
    public void Test_ComparingTwosHwCPUs() {
        assertFalse(hwCPU.equalSpec(cpu1));
        assertFalse(hwCPU.equalSpec(cpu2));
        assertFalse(hwCPU.equalSpec(cpu3));
        assertFalse(hwCPU.equalSpec(cpu4));
        assertFalse(hwCPU.equalSpec(cpu5));
        assertFalse(hwCPU.equalSpec(cpu6));
        assertFalse(hwCPU.equalSpec(cpu7));
        assertFalse(hwCPU.equalSpec(cpu8));
        assertFalse(hwCPU.equalSpec(cpu9));
        assertFalse(hwCPU.equalSpec(cpu10));
        assertFalse(hwCPU.equalSpec(cpu11));
        assertFalse(hwCPU.equalSpec(cpu13));
        assertFalse(hwCPU.equalSpec(cpu14));
        assertTrue(hwCPU.equalSpec(cpu12));
    }

    private void createHwCommunicationNodeList() {
        hwCommunicationNodeList1.add(hwBus);
        hwCommunicationNodeList1.add(hwBridge);
        hwCommunicationNodeList1.add(hwDMA);
        hwCommunicationNodeList1.add(hwNoC);
        hwCommunicationNodeList1.add(hwMemory);

        hwCommunicationNodeList2.add(hwMemory);
        hwCommunicationNodeList2.add(hwDMA);
        hwCommunicationNodeList2.add(hwBridge);
        hwCommunicationNodeList2.add(hwBus);
        hwCommunicationNodeList2.add(hwNoC);

        hwCommunicationNodeList3.add(hwMemory);
        hwCommunicationNodeList3.add(hwDMA);
        hwCommunicationNodeList3.add(hwBridge);
        hwCommunicationNodeList3.add(hwBus);
        hwCommunicationNodeList3.add(hwBus);

        hwCommunicationNodeList6.add(hwBus);
        hwCommunicationNodeList6.add(hwBridge);
        hwCommunicationNodeList6.add(hwDMA);
        hwCommunicationNodeList6.add(hwNoC);
        hwCommunicationNodeList6.add(hwMemory);
        hwCommunicationNodeList6.add(hwMemory);
    }

    @Test
    public void isOncommondesListEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isOncommondesListEquals(hwCommunicationNodeList1, hwCommunicationNodeList2));

        assertFalse("two lists have same size but difference context" + "",
                comparing.isOncommondesListEquals(hwCommunicationNodeList1, hwCommunicationNodeList3));

        assertTrue("two empty lists",
                comparing.isOncommondesListEquals(hwCommunicationNodeList4, hwCommunicationNodeList5));

        assertFalse("two lists have difference size",
                comparing.isOncommondesListEquals(hwCommunicationNodeList1, hwCommunicationNodeList6));

    }

    private void createTMLElementList() {
        tmlElementList1.add(tmlElement1);
        tmlElementList1.add(tmlElement2);
        tmlElementList1.add(tmlElement3);
        tmlElementList1.add(tmlActivity);

        tmlElementList2.add(tmlActivity);
        tmlElementList2.add(tmlElement3);
        tmlElementList2.add(tmlElement1);
        tmlElementList2.add(tmlElement2);

        tmlElementList3.add(tmlElement1);
        tmlElementList3.add(tmlActivity);
        tmlElementList3.add(tmlElement3);

        tmlElementList6.add(tmlElement1);
        tmlElementList6.add(tmlElement2);
        tmlElementList6.add(tmlActivity);
        tmlElementList6.add(tmlElement3);
        tmlElementList6.add(tmlElement3);

    }

    @Test
    public void isMappedcommeltsListEquals() {
        assertTrue("two lists have same context but difference element order" + "",
                comparing.isMappedcommeltsListEquals(tmlElementList1, tmlElementList2));

        assertFalse("two lists have same context but difference context" + "",
                comparing.isMappedcommeltsListEquals(tmlElementList1, tmlElementList3));

        assertTrue("two empty lists", comparing.isMappedcommeltsListEquals(tmlElementList4, tmlElementList5));

        assertFalse("two lists have difference size",
                comparing.isMappedcommeltsListEquals(tmlElementList1, tmlElementList6));
    }

    private void createdTaskLists() {
        taskList1.add(task1);
        taskList1.add(task2);
        taskList1.add(task3);

        taskList2.add(task3);
        taskList2.add(task1);
        taskList2.add(task2);

        taskList3.add(task1);
        taskList3.add(task2);
        taskList3.add(task2);

        taskList6.add(task1);
        taskList6.add(task2);
        taskList6.add(task3);
        taskList6.add(task3);

    }

    @Test
    public void isTasksListEquals() {
        assertTrue("two lists have same context but difference element order" + "",
                comparing.isTasksListEquals(taskList1, taskList2));

        assertFalse("two lists have same context but difference context" + "",
                comparing.isTasksListEquals(taskList1, taskList3));

        assertTrue("two empty lists", comparing.isTasksListEquals(taskList4, taskList5));

        assertFalse("two lists have difference size", comparing.isTasksListEquals(taskList1, taskList6));

    }

    private void createHwExecutionNodeList() {
        hwExecutionNodeList1.add(hwCPU);
        hwExecutionNodeList1.add(hwFPGA);
        hwExecutionNodeList1.add(hwa);

        hwExecutionNodeList2.add(hwa);
        hwExecutionNodeList2.add(hwCPU);
        hwExecutionNodeList2.add(hwFPGA);

        hwExecutionNodeList3.add(hwCPU);
        hwExecutionNodeList3.add(hwFPGA);
        hwExecutionNodeList3.add(hwFPGA);

        hwExecutionNodeList6.add(hwCPU);
        hwExecutionNodeList6.add(hwFPGA);
        hwExecutionNodeList6.add(hwa);
        hwExecutionNodeList6.add(hwa);

    }

    @Test
    public void isOnExecutionNodeListEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isOnExecutionNodeListEquals(hwExecutionNodeList1, hwExecutionNodeList2));

        assertFalse("two lists have difference context" + "",
                comparing.isOnExecutionNodeListEquals(hwExecutionNodeList1, hwExecutionNodeList3));

        assertTrue("two empty lists",
                comparing.isOnExecutionNodeListEquals(hwExecutionNodeList4, hwExecutionNodeList5));

        assertFalse("two lists have difference size",
                comparing.isOnExecutionNodeListEquals(hwExecutionNodeList1, hwExecutionNodeList6));
    }

    private void createListOfStringArray() {
        String[] str1 = { "#Authenticity", "#Confidentiality", "#PublicConstant", "#PrivateConstant",
                "#InitialSessionKnowledge", "#InitialSystemKnowledge", "#PrivatePublicKeys", "#Public",
                "#SecrecyAssumption", "#Secret" };

        String[] str2 = { "#Confidentiality", "#PublicConstant", "#PrivateConstant", "#InitialSessionKnowledge",
                "#InitialSystemKnowledge", "#PrivatePublicKeys", "#Public", "#SecrecyAssumption", "#Secret" };

        String[] str3 = { "#PublicConstant", "#PrivateConstant", "#InitialSessionKnowledge", "#Authenticity",
                "#Confidentiality", "#InitialSystemKnowledge", "#PrivatePublicKeys", "#Public", "#SecrecyAssumption",
                "#Secret" };
        String[] str4 = {};

        listOfStrArray1.add(str1);
        listOfStrArray1.add(str2);
        listOfStrArray1.add(str3);
        listOfStrArray1.add(str4);
        listOfStrArray1.add(str2);
        listOfStrArray1.add(str3);

        listOfStrArray2.add(str2);
        listOfStrArray2.add(str2);
        listOfStrArray2.add(str1);
        listOfStrArray2.add(str3);
        listOfStrArray2.add(str4);
        listOfStrArray2.add(str3);

        listOfStrArray6.add(str1);
        listOfStrArray6.add(str2);
        listOfStrArray6.add(str3);
        listOfStrArray6.add(str4);
        listOfStrArray6.add(str2);
        listOfStrArray6.add(str3);
        listOfStrArray6.add(str3);
    }

    @Test
    public void isListOfStringArrayEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isListOfStringArrayEquals(listOfStrArray1, listOfStrArray2));

        assertFalse("two lists have difference context" + "",
                comparing.isListOfStringArrayEquals(listOfStrArray1, listOfStrArray3));

        assertTrue("two empty lists", comparing.isListOfStringArrayEquals(listOfStrArray4, listOfStrArray5));

        assertFalse("two lists have difference size",
                comparing.isListOfStringArrayEquals(listOfStrArray1, listOfStrArray6));
    }

    private void createSecurityMap() {
        securityMap1.put(securityPattern1, memoryList1);
        securityMap1.put(securityPattern2, memoryList2);
        securityMap1.put(securityPattern3, memoryList3);
        securityMap1.put(securityPattern4, memoryList4);
        securityMap1.put(securityPattern5, memoryList5);

        securityMap2.put(securityPattern3, memoryList3);
        securityMap2.put(securityPattern2, memoryList2);
        securityMap2.put(securityPattern1, memoryList1);
        securityMap2.put(securityPattern5, memoryList5);
        securityMap2.put(securityPattern4, memoryList4);

        securityMap3.put(securityPattern1, memoryList1);
        securityMap3.put(securityPattern2, memoryList2);
        securityMap3.put(securityPattern3, memoryList3);
        securityMap3.put(securityPattern4, memoryList3);
        securityMap3.put(securityPattern5, memoryList4);

        securityMap6.put(securityPattern1, memoryList1);
        securityMap6.put(securityPattern2, memoryList2);
        securityMap6.put(securityPattern3, memoryList3);
        securityMap6.put(securityPattern4, memoryList4);
        securityMap6.put(securityPattern5, memoryList5);
        securityMap6.put(securityPattern6, memoryList5);

    }

    @Test
    public void isSecurityPatternMapEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isSecurityPatternMapEquals(securityMap1, securityMap2));

        assertFalse("two lists have same size but difference context" + "",
                comparing.isSecurityPatternMapEquals(securityMap1, securityMap3));

        assertTrue("two empty lists", comparing.isSecurityPatternMapEquals(securityMap4, securityMap5));

        assertFalse("two lists have difference size", comparing.isSecurityPatternMapEquals(securityMap1, securityMap6));

    }

    private void createHwMemoryList() {
        memoryList1.add(hwMemory);
        memoryList1.add(hwMemory2);
        memoryList1.add(hwMemory3);

        memoryList2.add(hwMemory2);
        memoryList2.add(hwMemory3);
        memoryList2.add(hwMemory);

        memoryList3.add(hwMemory);
        memoryList3.add(hwMemory2);
        memoryList3.add(hwMemory);

        memoryList6.add(hwMemory);
        memoryList6.add(hwMemory2);
        memoryList6.add(hwMemory3);
        memoryList6.add(hwMemory3);
    }

    @Test
    public void isHwMemoryListEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isHwMemoryListEquals(memoryList1, memoryList2));

        assertFalse("two lists have difference context" + "", comparing.isHwMemoryListEquals(memoryList1, memoryList3));

        assertTrue("two empty lists", comparing.isHwMemoryListEquals(memoryList4, memoryList5));

        assertFalse("two lists have difference size", comparing.isHwMemoryListEquals(memoryList1, memoryList6));
    }

    private void createTMLActivityElementList() {
        tmlActEltList1.add(tmlActionState);
        tmlActEltList1.add(tmlExecC);
        tmlActEltList1.add(tmlExecI);
        tmlActEltList1.add(tmlReadChannel);
        tmlActEltList1.add(tmlWriteChannel);
        tmlActEltList1.add(tmlNotifiedEvent);
        tmlActEltList1.add(tmlSendEvent);
        tmlActEltList1.add(tmlWaitEvent);
        tmlActEltList1.add(tmlDelay);
        tmlActEltList1.add(tmlExecCInterval);
        tmlActEltList1.add(tmlExecIInterval);
        tmlActEltList1.add(tmlForLoop);
        tmlActEltList1.add(tmlJunction);
        tmlActEltList1.add(tmlRandom);
        tmlActEltList1.add(tmlRandomSequence);
        tmlActEltList1.add(tmlSelectEvt);
        tmlActEltList1.add(tmlSendRequest);
        tmlActEltList1.add(tmlSequence);
        tmlActEltList1.add(tmlStartState);
        tmlActEltList1.add(tmlStopState);
        tmlActEltList1.add(tmlChoice);

        tmlActEltList2.add(tmlSequence);
        tmlActEltList2.add(tmlSendEvent);
        tmlActEltList2.add(tmlNotifiedEvent);
        tmlActEltList2.add(tmlActionState);
        tmlActEltList2.add(tmlExecI);
        tmlActEltList2.add(tmlSelectEvt);
        tmlActEltList2.add(tmlRandom);
        tmlActEltList2.add(tmlReadChannel);
        tmlActEltList2.add(tmlStopState);
        tmlActEltList2.add(tmlWriteChannel);
        tmlActEltList2.add(tmlExecCInterval);
        tmlActEltList2.add(tmlWaitEvent);
        tmlActEltList2.add(tmlDelay);
        tmlActEltList2.add(tmlExecIInterval);
        tmlActEltList2.add(tmlForLoop);
        tmlActEltList2.add(tmlJunction);
        tmlActEltList2.add(tmlRandomSequence);
        tmlActEltList2.add(tmlSendRequest);
        tmlActEltList2.add(tmlStartState);
        tmlActEltList2.add(tmlExecC);
        tmlActEltList2.add(tmlChoice);

        tmlActEltList3.add(tmlActionState);
        tmlActEltList3.add(tmlExecC);
        tmlActEltList3.add(tmlExecI);
        tmlActEltList3.add(tmlReadChannel);
        tmlActEltList3.add(tmlWriteChannel);
        tmlActEltList3.add(tmlNotifiedEvent);
        tmlActEltList3.add(tmlSendEvent);
        tmlActEltList3.add(tmlWaitEvent);
        tmlActEltList3.add(tmlDelay);
        tmlActEltList3.add(tmlExecCInterval);
        tmlActEltList3.add(tmlExecIInterval);
        tmlActEltList3.add(tmlForLoop);
        tmlActEltList3.add(tmlJunction);
        tmlActEltList3.add(tmlRandom);
        tmlActEltList3.add(tmlRandomSequence);
        tmlActEltList3.add(tmlSelectEvt);
        tmlActEltList3.add(tmlSendRequest);
        tmlActEltList3.add(tmlStartState); // difference here
        tmlActEltList3.add(tmlStartState);
        tmlActEltList3.add(tmlStopState);
        tmlActEltList3.add(tmlChoice);

        tmlActEltList6.add(tmlActionState);
        tmlActEltList6.add(tmlExecC);
        tmlActEltList6.add(tmlExecI);
        tmlActEltList6.add(tmlReadChannel);
        tmlActEltList6.add(tmlWriteChannel);
        tmlActEltList6.add(tmlNotifiedEvent);
        tmlActEltList6.add(tmlSendEvent);
        tmlActEltList6.add(tmlWaitEvent);
        tmlActEltList6.add(tmlDelay);
        tmlActEltList6.add(tmlExecCInterval);
        tmlActEltList6.add(tmlExecIInterval);
        tmlActEltList6.add(tmlForLoop);
        tmlActEltList6.add(tmlJunction);
        tmlActEltList6.add(tmlRandom);
        tmlActEltList6.add(tmlRandomSequence);
        tmlActEltList6.add(tmlSelectEvt);
        tmlActEltList6.add(tmlSendRequest);
        tmlActEltList6.add(tmlSequence);
        tmlActEltList6.add(tmlStartState);
        tmlActEltList6.add(tmlStopState);
        tmlActEltList6.add(tmlChoice);
        tmlActEltList6.add(tmlStopState);

    }

    @Test
    public void isTMLActivityEltListEquals() {
        assertTrue("two lists have same context but difference element order" + "",
                comparing.isTMLActivityEltListEquals(tmlActEltList1, tmlActEltList2));

        assertFalse("two lists have difference context" + "",
                comparing.isTMLActivityEltListEquals(tmlActEltList1, tmlActEltList3));

        assertTrue("two empty lists", comparing.isTMLActivityEltListEquals(tmlActEltList4, tmlActEltList5));

        assertFalse("two lists have difference size",
                comparing.isTMLActivityEltListEquals(tmlActEltList1, tmlActEltList6));
    }

    private void createHwNodeList() {
        hwNodeList1.add(hwCPU);
        hwNodeList1.add(hwa);
        hwNodeList1.add(hwDMA);
        hwNodeList1.add(hwMemory);
        hwNodeList1.add(hwFPGA);
        hwNodeList1.add(hwBridge);
        hwNodeList1.add(hwBus);
        hwNodeList1.add(hwNoC);

        hwNodeList2.add(hwBridge);
        hwNodeList2.add(hwMemory);
        hwNodeList2.add(hwa);
        hwNodeList2.add(hwCPU);
        hwNodeList2.add(hwDMA);
        hwNodeList2.add(hwNoC);
        hwNodeList2.add(hwFPGA);
        hwNodeList2.add(hwBus);

        hwNodeList3.add(hwBridge);
        hwNodeList3.add(hwMemory);
        hwNodeList3.add(hwa);
        hwNodeList3.add(hwCPU);
        hwNodeList3.add(hwDMA);
        hwNodeList3.add(hwNoC);
        hwNodeList3.add(hwFPGA);
        hwNodeList3.add(hwNoC);

        hwNodeList6.add(hwCPU);
        hwNodeList6.add(hwa);
        hwNodeList6.add(hwDMA);
        hwNodeList6.add(hwMemory);
        hwNodeList6.add(hwFPGA);
        hwNodeList6.add(hwBridge);
        hwNodeList6.add(hwBus);
        hwNodeList6.add(hwNoC);
        hwNodeList6.add(hwNoC);
    }

    @Test
    public void isHwNodeListEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isHwNodeListEquals(hwNodeList1, hwNodeList2));

        assertFalse("two lists have difference context" + "", comparing.isHwNodeListEquals(hwNodeList1, hwNodeList3));

        assertTrue("two empty lists", comparing.isHwNodeListEquals(hwNodeList4, hwNodeList5));

        assertFalse("two lists have difference size", comparing.isHwNodeListEquals(hwNodeList1, hwNodeList6));

    }

    private void createHwLinkList() {
        hwLinkList1.add(hwLink1);
        hwLinkList1.add(hwLink2);
        hwLinkList1.add(hwLink3);
        hwLinkList1.add(hwLink1);
        hwLinkList1.add(hwLink2);
        hwLinkList1.add(hwLink3);

        hwLinkList2.add(hwLink2);
        hwLinkList2.add(hwLink3);
        hwLinkList2.add(hwLink1);
        hwLinkList2.add(hwLink2);
        hwLinkList2.add(hwLink3);
        hwLinkList2.add(hwLink1);

        hwLinkList3.add(hwLink1);
        hwLinkList3.add(hwLink2);
        hwLinkList3.add(hwLink3);
        hwLinkList3.add(hwLink1);
        hwLinkList3.add(hwLink3);
        hwLinkList3.add(hwLink3);

        hwLinkList6.add(hwLink2);
        hwLinkList6.add(hwLink3);
        hwLinkList6.add(hwLink1);
        hwLinkList6.add(hwLink2);
        hwLinkList6.add(hwLink3);
    }

    @Test
    public void isHwlinkListEquals() {

        assertTrue("two lists have same context but difference element order" + "",
                comparing.isHwlinkListEquals(hwLinkList1, hwLinkList2));

        assertFalse("two lists have difference context" + "", comparing.isHwlinkListEquals(hwLinkList1, hwLinkList3));

        assertTrue("two empty lists", comparing.isHwlinkListEquals(hwLinkList4, hwLinkList5));

        assertFalse("two lists have difference size", comparing.isHwlinkListEquals(hwLinkList1, hwLinkList6));
    }

    private void createdPortLists() {
        portList1.add(port1);
        portList1.add(port2);
        portList1.add(port3);

        portList2.add(port3);
        portList2.add(port1);
        portList2.add(port2);

        portList3.add(port1);
        portList3.add(port2);
        portList3.add(port2);

        portList6.add(port1);
        portList6.add(port2);
        portList6.add(port3);
        portList6.add(port3);
    }

    @Test
    public void isPortListEquals() {
        assertTrue("two lists have same context but difference element order" + "",
                comparing.isPortListEquals(portList1, portList2));

        assertFalse("two lists have difference context" + "", comparing.isPortListEquals(portList1, portList3));

        assertTrue("two empty lists", comparing.isPortListEquals(portList4, portList5));

        assertFalse("two lists have difference size", comparing.isPortListEquals(portList1, portList6));

    }

    private void createChannelSets() {
        channelSet1.add(channel1);
        channelSet1.add(channel2);
        channelSet1.add(channel3);

        channelSet2.add(channel3);
        channelSet2.add(channel1);
        channelSet2.add(channel2);

        channelSet3.add(channel1);
        channelSet3.add(channel2);
        channelSet3.add(channel4);

        channelSet6.add(channel1);
        channelSet6.add(channel2);
        channelSet6.add(channel3);
        channelSet6.add(channel4);
    }

    @Test
    public void isTMLChannelSetEquals() {
        assertTrue("two sets have same context but difference element order" + "",
                comparing.isTMLChannelSetEquals(channelSet1, channelSet2));

        assertFalse("two sets have difference context" + "", comparing.isTMLChannelSetEquals(channelSet1, channelSet3));

        assertTrue("two empty sets", comparing.isTMLChannelSetEquals(channelSet4, channelSet5));

        assertFalse("two sets have difference size", comparing.isTMLChannelSetEquals(channelSet1, channelSet6));
    }

    private void createEventSets() {
        eventSet1.add(event1);
        eventSet1.add(event2);
        eventSet1.add(event3);

        eventSet2.add(event3);
        eventSet2.add(event1);
        eventSet2.add(event2);

        eventSet3.add(event1);
        eventSet3.add(event2);
        eventSet3.add(event4);

        eventSet6.add(event1);
        eventSet6.add(event2);
        eventSet6.add(event3);
        eventSet6.add(event4);
    }

    @Test
    public void isTMLEventSetEquals() {
        assertTrue("two sets have same context but difference element order" + "",
                comparing.isTMLEventSetEquals(eventSet1, eventSet2));

        assertFalse("two sets have difference context" + "", comparing.isTMLEventSetEquals(eventSet1, eventSet3));

        assertTrue("two empty sets", comparing.isTMLEventSetEquals(eventSet4, eventSet5));

        assertFalse("two sets have difference size", comparing.isTMLEventSetEquals(eventSet1, eventSet6));
    }
}