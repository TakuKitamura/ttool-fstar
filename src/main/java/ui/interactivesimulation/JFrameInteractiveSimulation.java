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

package ui.interactivesimulation;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import remotesimulation.CommandParser;
import remotesimulation.RemoteConnection;
import remotesimulation.RemoteConnectionException;
import tmltranslator.*;
import tmltranslator.simulation.SimulationTransaction;
import ui.*;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.window.JDialogSelectTasks;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;



/**
   * Class JFrameInteractiveSimulation
   * Creation: 21/04/2009
   * version 1.1 01/02/2017
   * @author Ludovic APVRILLE
 */
public class JFrameInteractiveSimulation extends JFrame implements ActionListener, Runnable, MouseListener, ItemListener, ChangeListener {

    protected static final int NB_OF_TRANSACTIONS = 1000;

    public static final String SIMULATION_HEADER = "siminfo";
    public static final String SIMULATION_GLOBAL = "global";
    protected static final String SIMULATION_TASK = "task";
    protected static final String SIMULATION_CPU = "cpu";
    protected static final String SIMULATION_BUS = "bus";
    protected static final String SIMULATION_TRANS = "transinfo";
    protected static final String SIMULATION_TRANS_NB = "transnb";
    protected static final String SIMULATION_COMMAND = "cmd";

    //private static String buttonStartS = "Start simulator";
    //private static String buttonCloseS = "Close";
    //private static String buttonStopAndCloseS = "Stop simulator and close";

    private static int NOT_STARTED = 0;
    private static int STARTING = 1;
    private static int STARTED_NOT_CONNECTED = 2;
    private static int STARTED_AND_CONNECTED = 3;

    private Frame f;
    private MainGUI mgui;
    // private String title;
    private String hostSystemC;
    private String pathExecute;

    protected JButton buttonClose, buttonStart, buttonStopAndClose, buttonShowTrace, buttonShowTraceTimeline;
    protected JTextArea jta;
    protected JScrollPane jsp;

    protected Thread t;
    protected int threadMode = 0;
    protected boolean go;
    protected RshClient rshc;
    protected RemoteConnection rc;
    protected CommandParser cp;
    protected String ssxml;

    // Text commands
    protected JTextField textCommand;
    protected JButton sendTextCommand, printHelpTextCommands, listTextCommands;
    //private static String sendTextCommandS = "Send Command";

    // Control command
    protected JButton resetCommand, runCommand, StopCommand;
    protected MainCommandsToolBar mctb;
    protected SaveCommandsToolBar sctb;
    protected StateCommandsToolBar stctb;
    protected BenchmarkCommandsToolBar bctb;
    protected FormalVerificationToolBar fvtb;


    // Commands
    private JPanel /*main,*/ mainTop, commands/*, save, state*/, infos/*, outputs*/, cpuPanel, variablePanel;
    protected JPanelTransactions transactionPanel;
    protected JPanelTaskTransactions taskTransactionPanel;
    private JCheckBox latex, debug, animate, diploids, update, openDiagram, animateWithInfo;
    private JTabbedPane commandTab, infoTab;
    protected JTextField paramMainCommand;
    protected JTextField saveDirName;
    protected JTextField saveFileName;
    protected JTextField stateFileName;
    protected JTextField benchmarkFileName;
    protected JComboBox<String> cpus, busses, mems, tasks, chans;
    private Map<String, List<Integer>> origChannelIDMap= new HashMap<String, List<Integer>>();
    private Map<String, List<Integer>> destChannelIDMap= new HashMap<String, List<Integer>>();

    private String[] cpuIDs, busIDs, memIDs, taskIDs, chanIDs;
	private List<String> simtraces= new ArrayList<String>();
	
	//Find matching channels
	public Map<String, List<Integer>> channelMsgIdMap = new HashMap<String, List<Integer>>();
	public Map<Integer, String> msgIdStartTimeMap = new HashMap<Integer, String>();
	public Map<Integer, String> msgIdEndTimeMap = new HashMap<Integer, String>();
		
    // Status elements
	private JLabel status, time, info;
	private int frequency;

    // Task elements
	private TaskVariableTableModel tvtm;
	private JButton updateTaskVariableInformationButton;
    private JScrollPane jspTaskVariableInfo;

    // Last transactions elements
    //private TransactionTableModel ttm;
    //JButton updateTransactionInformationButton;
    //private JScrollPane jspTransactionInfo;
    private Vector<SimulationTransaction> trans;

    // Breakpoints
    private JPanelBreakPoints jpbp;

    // Set variables
    private JPanelSetVariables jpsv;

    // Formal verification
    private JSlider minimalCommandCoverage, minimalBranchCoverage;
    private JLabel labelMinimalCommandCoverage, labelMinimalBranchCoverage;
    private String lastGraphName;
   // private RG lastRG;
    
    // Tasks
    private JPanel taskPanel;
    private TaskTableModel tasktm;
    private JButton updateTaskInformationButton;
    private JScrollPane jspTaskInfo;

    // CPU
    private CPUTableModel cputm;
    private JButton updateCPUInformationButton, printCPUInfo;
    private JScrollPane jspCPUInfo;
    private JPanel panelCPU;

    // Memories
    private JPanel memPanel;
   // private MemTableModel memtm;
    private JButton updateMemoryInformationButton, printBusInfo;
    private JScrollPane jspMemInfo;

    // Bus
    private JPanel busPanel;
    private BusTableModel bustm;
    private JButton updateBusInformationButton;
    private JScrollPane jspBusInfo;
    private JPanel panelBus;

    //Latency
    private JPanel latencyPanel;
    private JComboBox<String> transaction1;
    private JComboBox<String> transaction2;
    private JButton addLatencyCheckButton;
    private JButton updateLatencyButton;
    private LatencyTableModel latm;
    public Vector<String> checkedTransactions = new Vector<String>(); //List of all strings: Name: (id)
    private JScrollPane jspLatency;
    private int chanId=0;

    private int mode = 0;
    //private boolean busyStatus = false;
    private int busyMode = 0; // 0: unknown; 1: ready; 2:busy; 3:term
    private boolean threadStarted = false;
    private boolean gotTimeAnswerFromServer = false;

    // For managing actions
    public InteractiveSimulationActions [] actions;
    public MouseHandler mouseHandler;
    public KeyListener keyHandler;

    private TMLMapping<TGComponent> tmap;
    private int hashCode;
    private boolean hashOK = true;

    private Map<Integer, String> valueTable;
    private Map<Integer, Integer> rowTable;

    private Map<Integer, Integer> runningTable;
    private Map<String, String> diagramTable;

    private List<Point> points;
    private Map<String, String> checkTable = new HashMap<String, String>(); // commands: transaction time map
    private Map<String, List<String>> transTimes = new HashMap<String, List<String>>(); //OperatorId : {transaction time} map
    private Vector<SimulationLatency> latencies = new Vector<SimulationLatency>(); //List of all latencies
	private HashMap<String, List<String>> msgTimes = new HashMap<String, List<String>>(); //msgId : {transaction time} map
	private HashMap<String, List<String>> nameIdMap = new HashMap<String, List<String>>() ; // Names : List{operatorid}

	private PipedOutputStream pos;
	private PipedInputStream pis;
	private JFrameTMLSimulationPanel tmlSimPanel;
	private JFrameTMLSimulationPanelTimeline tmlSimPanelTimeline;
	private BufferedWriter bw;
	private int simIndex=0;
    private String listOfTaskToShowInTimeLine = "";
    private String timelineParam = "";
    private boolean isServerReply = false;
    
	public JFrameInteractiveSimulation(Frame _f, MainGUI _mgui, String _title, String _hostSystemC, String _pathExecute, TMLMapping<TGComponent> _tmap, List<Point> _points) {
        super(_title);

        f = _f;
        mgui = _mgui;
        //title = _title;
        hostSystemC = _hostSystemC;
        pathExecute = _pathExecute;

        mode = NOT_STARTED;




        tmap = _tmap;
        if (tmap != null) {
            frequency = tmap.getTMLArchitecture().getMasterClockFrequency();
        } else {
            frequency = TMLArchitecture.MASTER_CLOCK_FREQUENCY;
        }

        if (tmap != null) {
            tmap.makeMinimumMapping();
            hashCode = tmap.getHashCode();
            tmap.getTMLModeling().computeCorrespondance();
        } else {
            hashOK = false;
        }

        points = _points;

        valueTable = new Hashtable<Integer, String>();
        rowTable = new Hashtable<Integer, Integer>();
        runningTable = new Hashtable<Integer, Integer>();
        diagramTable = new Hashtable<String, String>();

        mgui.resetRunningID();
        mgui.resetLoadID();

        setBackground(new Color(50, 40, 40));

        initActions();
        makeComponents();
        setComponents();
    }

    private JLabel createStatusBar()  {
        status = new JLabel("Ready...");
        status.setForeground(ColorManager.InteractiveSimulationText);
        status.setBorder(BorderFactory.createEtchedBorder());
        return status;
    }

    private void makeComponents() {
        JPanel jp01, jp02;
        //jp01.setPreferredSize(new Dimension(375, 400));
        GridBagLayout gridbag01;
        GridBagConstraints c01 ;

        cp = new CommandParser();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        //framePanel.setBackground(ColorManager.InteractiveSimulationBackground);
        //framePanel.setForeground(new Color(255, 166, 38));

        //
        buttonStart = new JButton(actions[InteractiveSimulationActions.ACT_START_ALL]);
        buttonClose = new JButton(actions[InteractiveSimulationActions.ACT_STOP_ALL]);
        buttonStopAndClose = new JButton(actions[InteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL]);
        buttonShowTrace = new JButton(actions[InteractiveSimulationActions.ACT_SHOW_TRACE]);
        buttonShowTraceTimeline = new JButton(actions[InteractiveSimulationActions.ACT_SHOW_TRACE_TIMELINE]);
        //buttonStopAndClose = new JButton(buttonStopAndCloseS, IconManager.imgic27);





        // statusBar
        status = createStatusBar();
        framePanel.add(status, BorderLayout.SOUTH);

        // Mouse handler
        mouseHandler = new MouseHandler(status);

        JPanel mainpanel = new JPanel(new BorderLayout());
        //mainpanel.setBackground(ColorManager.InteractiveSimulationBackground);
        framePanel.add(mainpanel, BorderLayout.NORTH);

        JPanel jp = new JPanel();
        //jp.setBackground(ColorManager.InteractiveSimulationBackground);
        jp.setPreferredSize(new Dimension(800, 80));
        jp.add(buttonStart);
        jp.add(buttonStopAndClose);
        jp.add(buttonClose);
        jp.add(buttonShowTrace);
        jp.add(buttonShowTraceTimeline);
        mainpanel.add(jp, BorderLayout.NORTH);


        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        mainTop = new JPanel(gridbag02);
        //mainTop.setPreferredSize(new Dimension(800, 375));
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = 1;
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;

        // Ouput textArea
        jta = new ScrolledJTextArea();
        jta.setBackground(ColorManager.InteractiveSimulationJTABackground);
        jta.setForeground(ColorManager.InteractiveSimulationJTAForeground);
        jta.setMinimumSize(new Dimension(800, 400));
        jta.setRows(15);
        //jta.setMaximumSize(new Dimension(800, 500));
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Click on \"Connect to simulator\" to start the remote simulator and connect to it\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));

        //jsp.setColumnHeaderView(100);
        //jsp.setRowHeaderView(30);


        jsp.setMaximumSize(new Dimension(800, 500));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTop, jsp);
        //split.setBackground(ColorManager.InteractiveSimulationBackground);
        mainpanel.add(split, BorderLayout.CENTER);

        // Commands
        commands = new JPanel(new BorderLayout());
        //commands.setFloatable(true);
        //commands.setMinimumSize(new Dimension(300, 250));
        commands.setBorder(new javax.swing.border.TitledBorder("Commands"));


        mainTop.add(commands, c02);

        // Issue #41 Ordering of tabbed panes 
        commandTab = GraphicLib.createTabbedPaneRegular();//new JTabbedPane();

        // Control commands
        jp01 = new JPanel(new BorderLayout());

        commandTab.addTab("Control", null, jp01, "Main control commands");

        mctb = new MainCommandsToolBar(this);
        jp01.add(mctb, BorderLayout.NORTH);

        jp02 = new JPanel();
        //jp01.setPreferredSize(new Dimension(375, 400));
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp02.add(new JLabel("Command parameter: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        paramMainCommand = new JTextField("1", 30);
        jp02.add(paramMainCommand, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("CPUs and HwA: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (cpuIDs == null) {
            cpus = new JComboBox<String>();
        } else {
            cpus = new JComboBox<String>(cpuIDs);
        }
        jp02.add(cpus, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Buses: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (busIDs == null) {
            busses = new JComboBox<String>();
        } else {
            busses = new JComboBox<String>(busIDs);
        }
        jp02.add(busses, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Memories: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (memIDs == null) {
            mems = new JComboBox<String>();
        } else {
            mems = new JComboBox<String>(memIDs);
        }
        jp02.add(mems, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Tasks: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (taskIDs == null) {
            tasks = new JComboBox<String>();
        } else {
            tasks = new JComboBox<String>(taskIDs);
        }
        jp02.add(tasks, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Channels: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        if (chanIDs == null) {
            chans = new JComboBox<String>();
        } else {
            chans = new JComboBox<String>(chanIDs);
        }
        jp02.add(chans, c01);

        jp01.add(jp02, BorderLayout.CENTER);


        // Text commands
        jp01 = new JPanel();
        //jp01.setPreferredSize(new Dimension(375, 400));
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);

        commandTab.addTab("Text commands", null, jp01, "Sending text commands to simulator");

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        c01.gridheight = 2;
        jp01.add(new JLabel("Enter a text command:"), c01);
        textCommand = new JTextField(30);
        jp01.add(textCommand, c01);
        c01.gridheight = 1;
        jp01.add(new JLabel(" "), c01);
        c01.gridheight = 2;
        sendTextCommand = new JButton("Send Command", IconManager.imgic71);
        sendTextCommand.addMouseListener(this);
        jp01.add(sendTextCommand, c01);
        c01.gridheight = 1;
        jp01.add(new JLabel(" "), c01);
        c01.gridheight = 2;
        printHelpTextCommands = new JButton("Help on a text command", IconManager.imgic33);
        printHelpTextCommands.addMouseListener(this);
        jp01.add(printHelpTextCommands, c01);
        c01.gridheight = 1;
        jp01.add(new JLabel(" "), c01);
        c01.gridheight = 2;
        listTextCommands = new JButton("List all text commands", IconManager.imgic29);
        listTextCommands.addMouseListener(this);
        jp01.add(listTextCommands, c01);

        commands.add(commandTab, BorderLayout.NORTH);

        // Set variables
        jpsv = new JPanelSetVariables(this, valueTable);
        commandTab.addTab("Set variables", null, jpsv, "Set variables");

        // Save commands
        jp01 = new JPanel(new BorderLayout());

        commandTab.addTab("Save trace", null, jp01, "Save commands");

        sctb = new SaveCommandsToolBar(this);
        jp01.add(sctb, BorderLayout.NORTH);

        jp02 = new JPanel();
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp02.add(new JLabel("Directory:"), c01);
        saveDirName = new JTextField(30);
        if (ConfigurationTTool.SystemCCodeDirectory != null) {
            saveDirName.setText(SpecConfigTTool.SystemCCodeDirectory);
        }
        jp02.add(saveDirName, c01);
        jp02.add(new JLabel("File name:"), c01);
        saveFileName = new JTextField(30);
        jp02.add(saveFileName, c01);

        jp01.add(jp02, BorderLayout.CENTER);

        // State commands
        jp01 = new JPanel(new BorderLayout());

        commandTab.addTab("Save / restore state", null, jp01, "Save commands");

        stctb = new StateCommandsToolBar(this);
        jp01.add(stctb, BorderLayout.NORTH);

        jp02 = new JPanel();
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp02.add(new JLabel("File name:"), c01);
        stateFileName = new JTextField(30);
        jp02.add(stateFileName, c01);

        jp01.add(jp02, BorderLayout.CENTER);

        // Benchmark commands
        jp01 = new JPanel(new BorderLayout());

        commandTab.addTab("Benchmarks", null, jp01, "Benchmarks");

        bctb = new BenchmarkCommandsToolBar(this);
        jp01.add(bctb, BorderLayout.NORTH);

        jp02 = new JPanel();
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp02.add(new JLabel("File name:"), c01);
        benchmarkFileName = new JTextField(30);
        jp02.add(benchmarkFileName, c01);

        jp01.add(jp02, BorderLayout.CENTER);

        // Formal verification
        jp01 = new JPanel(new BorderLayout());

        commandTab.addTab("Formal verification", null, jp01, "Formal verification");

        fvtb = new FormalVerificationToolBar(this);
        jp01.add(fvtb, BorderLayout.NORTH);

        jp02 = new JPanel();
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        // First empty line
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp02.add(new JLabel(" "), c01);
        JLabel warning = new JLabel("Beware: Formal Verification ignores Penalties");
        Font newLabelFont=new Font(warning.getFont().getName(),Font.ITALIC,warning.getFont().getSize());
        //Set JLabel font using new created font
        warning.setFont(newLabelFont);
        jp02.add(warning, c01);
        jp02.add(new JLabel(" "), c01);

        // Line minimum command: labels
        c01.gridwidth = 1;
        jp02.add(new JLabel("minimum COMMAND coverage"), c01);
        labelMinimalCommandCoverage = new JLabel("100%");
        c01.fill = GridBagConstraints.CENTER;
        jp02.add(labelMinimalCommandCoverage, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        jp02.add(new JLabel(" "), c01);

        // Line minimum command: slider
        c01.gridwidth = 1;
        jp02.add(new JLabel(" "), c01);
        minimalCommandCoverage = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        minimalCommandCoverage.setValue(100);
        minimalCommandCoverage.setMajorTickSpacing(10);
        minimalCommandCoverage.setMinorTickSpacing(1);
        minimalCommandCoverage.setPaintTicks(true);
        minimalCommandCoverage.setPaintLabels(true);
        minimalCommandCoverage.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        minimalCommandCoverage.addChangeListener(this);
        Font font = new Font("Serif", Font.ITALIC, 10);
        minimalCommandCoverage.setFont(font);
        jp02.add(minimalCommandCoverage, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp02.add(new JLabel(" "), c01);

        // One empty line
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp02.add(new JLabel(""), c01);

        // Line minimum command: labels
        c01.gridwidth = 1;
        jp02.add(new JLabel("minimum BRANCH coverage"), c01);

        labelMinimalBranchCoverage = new JLabel("100%");
        c01.fill = GridBagConstraints.CENTER;
        jp02.add(labelMinimalBranchCoverage, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        jp02.add(new JLabel(" "), c01);

        // Line minimum branch: slider
        c01.gridwidth = 1;
        jp02.add(new JLabel(" "), c01);
        minimalBranchCoverage = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        minimalBranchCoverage.setValue(100);
        minimalBranchCoverage.setMajorTickSpacing(10);
        minimalBranchCoverage.setMinorTickSpacing(1);
        minimalBranchCoverage.setPaintTicks(true);
        minimalBranchCoverage.setPaintLabels(true);
        minimalBranchCoverage.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        minimalBranchCoverage.addChangeListener(this);
        minimalBranchCoverage.setFont(font);
        jp02.add(minimalBranchCoverage, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp02.add(new JLabel(" "), c01);

        // Last empty line
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp02.add(new JLabel(" "), c01);

        /*c01.gridwidth = 1;
          jp02.add(new JLabel("minimum BRANCH coverage"), c01);
          c01.gridwidth = GridBagConstraints.REMAINDER; //end row
          labelMinimalBranchCoverage = new JLabel("100%");
          c01.fill = GridBagConstraints.EAST;
          jp02.add(labelMinimalBranchCoverage, c01);
          c01.fill = GridBagConstraints.BOTH;
          minimalBranchCoverage = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
          minimalBranchCoverage.setValue(100);
          minimalBranchCoverage.setMajorTickSpacing(10);
          minimalBranchCoverage.setMinorTickSpacing(1);
          minimalBranchCoverage.setPaintTicks(true);
          minimalBranchCoverage.setPaintLabels(true);
          minimalBranchCoverage.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
          minimalBranchCoverage.addChangeListener(this);
          minimalBranchCoverage.setFont(font);
          c01.gridwidth = 1; //end row
          jp02.add(new JLabel(" "), c01);
          jp02.add(minimalBranchCoverage, c01);
          c01.gridwidth = GridBagConstraints.REMAINDER; //end row
          jp02.add(new JLabel(" "), c01);*/
        jp01.add(jp02, BorderLayout.CENTER);


        //Info
        infos = new JPanel(new BorderLayout());
        infos.setMinimumSize(new Dimension(300, 250));
        //infos.setPreferredSize(new Dimension(400, 450));
        infos.setBorder(new javax.swing.border.TitledBorder("Simulation information"));
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        mainTop.add(infos, c02);

        // Issue #41 Ordering of tabbed panes 
        infoTab = GraphicLib.createTabbedPaneRegular();//new JTabbedPane();
        infoTab.setMinimumSize(new Dimension(300, 250));
        infos.add(infoTab, BorderLayout.NORTH);

        // Simulation time
        jp02 = new JPanel();
        //infos.add(jp02, BorderLayout.SOUTH);
        commands.add(jp02, BorderLayout.SOUTH);
        //mainTop.add(jp02, c02);
        jp02.add(new JLabel("Status:"));
        status = new JLabel("Unknown");
        status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(status);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Time:"));
        time = new JLabel("Unknown");
        time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(time);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Sim. interrupt reason:"));
        info = new JLabel("Unknown");
        info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(info);

        // Options
        jp01 = new JPanel();
        //jp01.setMinimumSize(new Dimension(375, 400));
        //jp01.setPreferredSize(new Dimension(375, 400));
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);


        // INFORMATION

        infoTab.addTab("Options", null, jp01, "Options on simulation");

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp01.add(new JLabel(" "), c01);
        latex = new JCheckBox("Generate info in Latex format");
        jp01.add(latex, c01);

        debug = new JCheckBox("Print messages received from server");
        jp01.add(debug, c01);

        animate = new JCheckBox("Animate UML diagrams");
        jp01.add(animate, c01);

        diploids = new JCheckBox("Show DIPLO IDs on UML diagrams");
        jp01.add(diploids, c01);
        diploids.addItemListener(this);
        diploids.setSelected(false);

        animateWithInfo = new JCheckBox("Show transaction progression on UML diagrams");
        jp01.add(animateWithInfo, c01);
        animateWithInfo.addItemListener(this);
        animateWithInfo.setSelected(ModelParameters.getBooleanValueFromID("ANIMATE_WITH_INFO_DIPLO_SIM"));

        openDiagram = new JCheckBox("Automatically open active task diagram");
        jp01.add(openDiagram, c01);
        openDiagram.setSelected(ModelParameters.getBooleanValueFromID("OPEN_DIAG_DIPLO_SIM"));
        openDiagram.addItemListener(this);

        update = new JCheckBox("Automatically update information (task, CPU, etc.)");
        jp01.add(update, c01);
        update.addItemListener(this);
        update.setSelected(ModelParameters.getBooleanValueFromID("UPDATE_INFORMATION_DIPLO_SIM"));

        animate.addItemListener(this);
        animate.setSelected(ModelParameters.getBooleanValueFromID("ANIMATE_INTERACTIVE_SIMULATION"));


        TableSorter sorterPI;
        JTable jtablePI;

        // Breakpoints
        jpbp = new JPanelBreakPoints(this, points);
        infoTab.addTab("Breakpoints", null, jpbp, "List of active breakpoints");

        // Tasks
        taskPanel = new JPanel();
        taskPanel.setLayout(new BorderLayout());
        infoTab.addTab("Tasks", IconManager.imgic1202, taskPanel, "Current state of tasks");
        if (tmap == null) {
            tasktm = new TaskTableModel(null, valueTable, rowTable);
        } else {
            tasktm = new TaskTableModel(tmap.getTMLModeling(), valueTable, rowTable);
        }

        sorterPI = new TableSorter(tasktm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(80);
        ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(300);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspTaskInfo = new JScrollPane(jtablePI);
        jspTaskInfo.setWheelScrollingEnabled(true);
        jspTaskInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspTaskInfo.setPreferredSize(new Dimension(500, 300));
        taskPanel.add(jspTaskInfo, BorderLayout.NORTH);
        updateTaskInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_TASKS]);
        taskPanel.add(updateTaskInformationButton, BorderLayout.SOUTH);

        // Variables
        variablePanel = new JPanel();
        variablePanel.setLayout(new BorderLayout());
        infoTab.addTab("Tasks variables", null, variablePanel, "Current value of variables");
        if (tmap == null) {
            tvtm = new TaskVariableTableModel(null, valueTable, rowTable);
        } else {
            tvtm = new TaskVariableTableModel(tmap.getTMLModeling(), valueTable, rowTable);
        }
        sorterPI = new TableSorter(tvtm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(60);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(60);
        ((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspTaskVariableInfo = new JScrollPane(jtablePI);
        jspTaskVariableInfo.setWheelScrollingEnabled(true);
        jspTaskVariableInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspTaskVariableInfo.setPreferredSize(new Dimension(500, 300));
        variablePanel.add(jspTaskVariableInfo, BorderLayout.NORTH);
        updateTaskVariableInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_VARIABLES]);
        variablePanel.add(updateTaskVariableInformationButton, BorderLayout.SOUTH);

        // Transactions
        transactionPanel = new JPanelTransactions(this, NB_OF_TRANSACTIONS);
        //transactionPanel.setLayout(new BorderLayout());
        infoTab.addTab("Transactions", null, transactionPanel, "Recent transactions");
        /*ttm = new TransactionTableModel(this);
          sorterPI = new TableSorter(ttm);
          jtablePI = new JTable(sorterPI);
          sorterPI.setTableHeader(jtablePI.getTableHeader());
          ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
          ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(100);
          ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(150);
          ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
          ((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
          jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
          jspTransactionInfo = new JScrollPane(jtablePI);
          jspTransactionInfo.setWheelScrollingEnabled(true);
          jspTransactionInfo.getVerticalScrollBar().setUnitIncrement(10);
          jspTransactionInfo.setPreferredSize(new Dimension(500, 300));
          transactionPanel.add(jspTransactionInfo, BorderLayout.NORTH);
          updateTransactionInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_TRANSACTIONS]);
          transactionPanel.add(updateTransactionInformationButton, BorderLayout.SOUTH);*/
        if (tmap == null) {
            taskTransactionPanel = new JPanelTaskTransactions(null, this,NB_OF_TRANSACTIONS);
        } else {
            taskTransactionPanel = new JPanelTaskTransactions(tmap.getTMLModeling(),this,NB_OF_TRANSACTIONS);
        }

        infoTab.addTab("Task Transactions", null, taskTransactionPanel, "Transactions of given Task");
        // CPUs
        cpuPanel = new JPanel();
        cpuPanel.setLayout(new BorderLayout());
        infoTab.addTab("CPUs/HwA", IconManager.imgic1100, cpuPanel, "Current state of CPUs and hardware accelerators");
        cputm = new CPUTableModel(tmap, valueTable, rowTable);
        sorterPI = new TableSorter(cputm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(700);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspCPUInfo = new JScrollPane(jtablePI);
        jspCPUInfo.setWheelScrollingEnabled(true);
        jspCPUInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspCPUInfo.setPreferredSize(new Dimension(500, 300));
        cpuPanel.add(jspCPUInfo, BorderLayout.NORTH);
        panelCPU = new JPanel(new FlowLayout());
        updateCPUInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_CPUS]);
        panelCPU.add(updateCPUInformationButton);
        printCPUInfo = new JButton(actions[InteractiveSimulationActions.ACT_PRINT_CPUS]);
        panelCPU.add(printCPUInfo);
        cpuPanel.add(panelCPU, BorderLayout.SOUTH);

        // Memories
        memPanel = new JPanel();
        memPanel.setLayout(new BorderLayout());
        infoTab.addTab("Memories", IconManager.imgic1108, memPanel, "Current state of Memories");
        MemTableModel memtm = new MemTableModel(tmap, valueTable, rowTable);
        sorterPI = new TableSorter(memtm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(300);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspMemInfo = new JScrollPane(jtablePI);
        jspMemInfo.setWheelScrollingEnabled(true);
        jspMemInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspMemInfo.setPreferredSize(new Dimension(500, 300));
        memPanel.add(jspMemInfo, BorderLayout.NORTH);
        updateMemoryInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_MEMS]);
        memPanel.add(updateMemoryInformationButton, BorderLayout.SOUTH);

        // Busses
        busPanel = new JPanel();
        busPanel.setLayout(new BorderLayout());
        infoTab.addTab("Bus", IconManager.imgic1102, busPanel, "Current state of busses");
        bustm = new BusTableModel(tmap, valueTable, rowTable);
        sorterPI = new TableSorter(bustm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(300);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspBusInfo = new JScrollPane(jtablePI);
        jspBusInfo.setWheelScrollingEnabled(true);
        jspBusInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspBusInfo.setPreferredSize(new Dimension(500, 300));
        busPanel.add(jspBusInfo, BorderLayout.NORTH);
        panelBus = new JPanel(new FlowLayout());
        updateBusInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_BUS]);
        panelBus.add(updateBusInformationButton);
        printBusInfo = new JButton(actions[InteractiveSimulationActions.ACT_PRINT_BUS]);
        panelBus.add(printBusInfo);
        busPanel.add(panelBus, BorderLayout.SOUTH);

        //Latency
        latencyPanel = new JPanel();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        latencyPanel.setLayout(gridbag0);
        infoTab.addTab("Latency", null, new JScrollPane(latencyPanel), "Latency Measurements");
        c0.gridwidth = GridBagConstraints.REMAINDER;
		latencyPanel.add(new JLabel("Latencies shown in number of cycles relative to the main clock"), c0);

        c0.gridwidth=1;
        c0.gridheight=1;
        latencyPanel.add(new JLabel("Checkpoint 1:"),c0);
        c0.gridwidth = GridBagConstraints.REMAINDER;
        transaction1 = new JComboBox<String>(checkedTransactions);
        latencyPanel.add(transaction1, c0);

        c0.gridwidth=1;
        latencyPanel.add(new JLabel("Checkpoint 2:"),c0);
        c0.gridwidth= GridBagConstraints.REMAINDER;
        transaction2 = new JComboBox<String>(checkedTransactions);
        latencyPanel.add(transaction2, c0);


        addLatencyCheckButton = new JButton(actions[InteractiveSimulationActions.ACT_ADD_LATENCY]);
        latencyPanel.add(addLatencyCheckButton,c0);

        latm = new LatencyTableModel();
        latm.setData(latencies);
        sorterPI = new TableSorter(latm);
        final JTable latTable = new JTable(sorterPI);
		latTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
 			public void mouseClicked(java.awt.event.MouseEvent evt) {
    			int row = latTable.rowAtPoint(evt.getPoint());
    			int col = latTable.columnAtPoint(evt.getPoint());
    			if (row >= 0 && col >= 0 && col <2 && latencies.size()>row) {
					for (TGComponent tgc: tmap.getTMLModeling().getCheckedComps().keySet()){
						if (tmap.getTMLModeling().getCheckedComps().get(tgc).equals(latm.getValueAt(row,col).toString().split(" ")[0])){
        				    mgui.selectTab(tgc.getTDiagramPanel());
            				tgc.getTDiagramPanel().highlightTGComponent(tgc);
						}
					}
    			}
 			}
		});
        sorterPI.setTableHeader(latTable.getTableHeader());
        ((latTable.getColumnModel()).getColumn(0)).setPreferredWidth(700);
        ((latTable.getColumnModel()).getColumn(1)).setPreferredWidth(700);
        ((latTable.getColumnModel()).getColumn(2)).setPreferredWidth(100);
        ((latTable.getColumnModel()).getColumn(3)).setPreferredWidth(100);
        ((latTable.getColumnModel()).getColumn(4)).setPreferredWidth(100);
        ((latTable.getColumnModel()).getColumn(5)).setPreferredWidth(100);
        latTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jspLatency = new JScrollPane(latTable);
        jspLatency.setWheelScrollingEnabled(true);
        jspLatency.getVerticalScrollBar().setUnitIncrement(10);
        jspLatency.setMinimumSize(new Dimension(1000, 250));
        jspLatency.setPreferredSize(new Dimension(2100, 250));
        latencyPanel.add(jspLatency, c0);

        updateLatencyButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_LATENCY]);
        latencyPanel.add(updateLatencyButton,c0);

        if (!hashOK) {
            wrongHashCode();
        }
        
        pack();

        //
        //
	}
    private void initActions() {
        actions = new InteractiveSimulationActions[InteractiveSimulationActions.NB_ACTION];
        for(int i=0; i<InteractiveSimulationActions.NB_ACTION; i++) {
            actions[i] = new InteractiveSimulationActions(i);
            actions[i].addActionListener(this);
            //actions[i].addKeyListener(this);
        }

        cpuIDs = makeCPUIDs();
        busIDs = makeBusIDs();
        memIDs = makeMemIDs();
        taskIDs = makeTasksIDs();
        chanIDs = makeChanIDs();
        fillCheckedTrans();
    }

    private void setComponents() {
        if (mode == NOT_STARTED) {
            buttonStart.setEnabled(true);
        } else {
            buttonStart.setEnabled(false);
        }
        if ((mode == NOT_STARTED) || (mode == STARTED_NOT_CONNECTED)) {
            buttonShowTrace.setEnabled(false);
            buttonShowTraceTimeline.setEnabled(false);
        } else {
            buttonShowTrace.setEnabled(true);
            buttonShowTraceTimeline.setEnabled(true);
        }

        if ((mode == STARTED_NOT_CONNECTED) || (mode == STARTED_AND_CONNECTED)) {
            buttonStopAndClose.setEnabled(true);
        } else {
            buttonStopAndClose.setEnabled(false);
        }

        boolean b = (mode == STARTED_AND_CONNECTED);
        sendTextCommand.setEnabled(b);
        setAll();
        //resetCommand.setEnabled(b);
        //runCommand.setEnabled(b);
        //StopCommand.setEnabled(b);
    }

    public void close() {
        if (mode != NOT_STARTED)  {
            go = false;
            if (rc != null) {
                try {
                    rc.disconnect();
                } catch (RemoteConnectionException rce) {
                }
                //
                rc = null;
            }
        }
        mgui.resetRunningID();
        mgui.resetStatus();
        mgui.resetTransactions();
        mgui.resetLoadID();
        mgui.setDiploAnimate(false);
        dispose();
        setVisible(false);

    }

    public void killSimulator() {
        if (mode == STARTED_AND_CONNECTED) {
            if (rc != null) {
                try {
                    rc.send("0");
                } catch (RemoteConnectionException rce) {
                    jta.append("Exception: " + rce.getMessage());
                    jta.append("Could not kill simulator\n");
                }
            }
            rshc = null;
        } else {
            if (rshc != null) {
                try {
                    rshc.sendKillProcessRequest();
                } catch (LauncherException le) {
                    jta.append("Exception: " + le.getMessage() + "\n");
                }
            }
        }
    }

    public boolean getHash() {
	    return hashOK;
    }

    public int getBusyMode() {
	    return busyMode;
    }

    public void startSimulation() {
        mode = STARTING;
        setComponents();
        go = true;

        startThread(0);
        //t = new Thread(this);
        //go = true;
        //threadMode = 0;
        //t.start();
    }

    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }

    // Must first start the remote server
    // Then, must start

    @Override
    public void run() {
        String s;
        TraceManager.addDev("mode=" + threadMode);

        try {
            if (threadMode == 0) {
                threadStarted();
                testGo();
                rc = new RemoteConnection(hostSystemC);

                if (!connect()) {
                    rshc = new RshClient(hostSystemC);
                    try {
                        jta.append("\nStarting simulation server\n");
                        processCmd(pathExecute);
                        //jta.append(data + "\n");
                    } catch (LauncherException le) {
                        jta.append("Error: " + le.getMessage() + "\n");
                        mode =  NOT_STARTED;
                        setComponents();
                        return;
                    } catch (Exception e) {
                        mode =  NOT_STARTED;
                        setComponents();
                        return;
                    }
                    testGo();

                    // Wait for the server to start
                    Thread.sleep(1000);

                    //jta.append("Simulator started\n\n");
                    jta.append("Connecting to simulation server ...\n");
                    mode = STARTED_NOT_CONNECTED;
                    if (!connect()) {
                        jta.append("Could not connect to server... Aborting\n");
                        mode =  NOT_STARTED;
                        setComponents();
                        return;
                    }
                }

                testGo();

                jta.append("Connected to simulation server ...\n");
                mode = STARTED_AND_CONNECTED;

                startThread(2);

                setComponents();

                if (tmap != null) {
                    sendCommand("get-hashcode");
                } else {
                    sendCommand("time");
                }


                try {
                    while(true) {
                        testGo();
                        s = rc.readOneLine();
                        if (debug.isSelected()) {
                            jta.append("\nFrom server: " + s + "\n");
                        }
                        analyzeServerAnswer(s);
                    }
                } catch (RemoteConnectionException rce) {
                    jta.append("Exception: " + rce.getMessage());
                    jta.append("Could not read data from host: " + hostSystemC + ".... Aborting\n");
                    busyMode = 0;
                    setLabelColors();
                    //
                }
            } else if (threadMode == 1) {
                threadStarted();
                try {
                    while(true) {
                        testGo();
                        s = rshc.getDataFromProcess();
                        jta.append("\nFrom launcher: " + s + "\n");
                    }
                } catch (LauncherException le) {
                    jta.append("Exception: " + le.getMessage() + "\n");
                }
            } else if (threadMode == 2) {
                threadStarted();
                while(true) {
                    testGo();
                    Thread.sleep(500);
                    if (busyMode == 2 && gotTimeAnswerFromServer) {
                        gotTimeAnswerFromServer = false;
                        askForUpdate();

                    }
                }
            }
        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }

        //

    }

    protected boolean connect() {
        try {
            rc.connect();
            return true;
        } catch (RemoteConnectionException rce) {
            //rce.printStackTrace();

            return false;
        }
    }

    protected void processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        rshc.sendExecuteCommandRequest();
        startThread(1);
        //t = new Thread(this);
        ////go = true;
        //threadMode = 1;
        //t.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e){
        if (e.getSource() == sendTextCommand) {
            if (sendTextCommand.isEnabled()) {
                sendCommand();
            }
        } else if (e.getSource() == printHelpTextCommands) {
            helpOnCommand();
        } else if (e.getSource() == listTextCommands) {
            listTextCommands();
        } /*else if (e.getSource() == resetCommand) {
            sendCommand("reset");
            } else if (e.getSource() == runCommand) {
            sendCommand("run-to-next-breakpoint");
            } else if (e.getSource() == StopCommand) {
            sendCommand("stop");
            }*/
    }

    // Command management

    public void printSeparator() {
        jta.append("-------------------------------------------------------------\n");
    }

    protected void listTextCommands() {
        String text = cp.getCommandList();
        append("Available commands", text);
    }

    protected void helpOnCommand() {
        String text = textCommand.getText().trim();
        String texts[] = text.split(" ");
        text = texts[0];
        String result = cp.getHelp(text);
        append("Help on command: " + text, result);
    }

    protected void sendCommand() {
        String text = textCommand.getText().trim();
        sendCommand(text);
    }

    public void sendTestCmd(String text) {
	    if (!text.equals("")) {
	        sendCommand(text);
        }
    }

    protected void sendCommand(String text) {
        jta.append(">" + text + "\n");
        String command = cp.transformCommandFromUserToSimulator(text);
        if (command.length() == 0) {
            jta.append("** Wrong command / parameters **\n");
            return;
        }

        try {
            rc.send(command);
        } catch (RemoteConnectionException rce) {
            jta.append("** Sending command failed **\n");
            return ;
        } catch (Exception e) {}
    }

    protected void append(String info, String list) {
        jta.append("\n");
        jta.append(info + "\n");
        printSeparator();
        jta.append(list);
        jta.append("\n");
        printSeparator();
    }

    protected void analyzeServerAnswer(String s) {
        //
        int index0 = s.indexOf("<?xml");

        if (index0 != -1) {
            //
            ssxml = s.substring(index0, s.length()) + "\n";
        } else {
            //
            ssxml = ssxml + s + "\n";
        }
        index0 = ssxml.indexOf("<![CDATA[");
        int index1 = ssxml.indexOf("]]>");
        if ((index0 > -1) && (index1 > -1)) {
            timelineParam = ssxml.substring(index0+9, index1).trim();
            if(tmlSimPanelTimeline != null) {
                tmlSimPanelTimeline.setServerReply(timelineParam);
            }
            ssxml = ssxml.replace( timelineParam,"");
            ssxml = ssxml.replace( "<![CDATA[","");
            ssxml = ssxml.replace( "]]>","");
            isServerReply = true;
        }

        index0 = ssxml.indexOf("</siminfo>");

        if (index0 != -1) {
            //
            ssxml = ssxml.substring(0, index0+10);
            loadXMLInfoFromServer(ssxml);
            ssxml = "";
        }
    }

    protected boolean loadXMLInfoFromServer(String xmldata) {
        //jta.append("XML from server:" + xmldata + "\n\n");

        DocumentBuilderFactory dbf;
        DocumentBuilder db;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            dbf = null;
            db = null;
        }

        if ((dbf == null) || (db == null)) {
            return false;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(decodeString(xmldata).getBytes());
        int i;

        try {
            // building nodes from xml String
            Document doc = db.parse(bais);
            NodeList nl;
            Node node;

            nl = doc.getElementsByTagName(SIMULATION_HEADER);

            if (nl == null) {
                return false;
            }

            for(i=0; i<nl.getLength(); i++) {
                node = nl.item(i);
                //
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    // create design, and get an index for it
                    return loadConfiguration(node);
                }
            }
        } catch (IOException e) {
            TraceManager.addError("Error when parsing server info:" + e.getMessage());
            return false;
        } catch (SAXException saxe) {
            TraceManager.addError("Error when parsing server info:" + saxe.getMessage());
            TraceManager.addError("xml:" + xmldata);
            return false;
        }
        
        return true;
    }

	public void resetSimTrace(){
		msgTimes.clear();
		chanId=0;
		channelMsgIdMap.clear();
		msgIdStartTimeMap.clear();
		msgIdEndTimeMap.clear();
		origChannelIDMap.clear();
		destChannelIDMap.clear();	
		simtraces.clear();
		simIndex=0;
	}

	public void writeSimTrace(){
		try {
			tmlSimPanel = new JFrameTMLSimulationPanel(new Frame(), mgui, "Simulation Transactions");

			//Make a popup to select which tasks
			Vector<String> tmlComponentsToValidate = new Vector<String>();
			List<String> tasks = new ArrayList<String>();
			for (TMLTask task: tmap.getTMLModeling().getTasks()){
				tasks.add(task.getName());
			}
			JDialogSelectTasks jdstmlc = new JDialogSelectTasks(f, tmlComponentsToValidate, tasks, "Select tasks to show in trace");

			GraphicLib.centerOnParent(jdstmlc);
            jdstmlc.setVisible(true); 
 
			LinkedHashMap<String, ArrayList<String>> deviceTaskMap = new LinkedHashMap<String, ArrayList<String>>();
			for (String name: tmlComponentsToValidate){
				TMLTask task = tmap.getTMLModeling().getTMLTaskByName(name);
				if (task==null){
					continue;
				}
				HwNode node = tmap.getHwNodeOf(task);
				if (node!=null) {
					if (!deviceTaskMap.containsKey(node.getName())){
						deviceTaskMap.put(node.getName(), new ArrayList<String>());
					}
					deviceTaskMap.get(node.getName()).add(task.getName());
				}
			}
			pos = new PipedOutputStream();
			pis = new PipedInputStream(pos, 4096);
			tmlSimPanel.setFileReference(new BufferedReader(new InputStreamReader(pis)));
			tmlSimPanel.getSDPanel().setDevices(deviceTaskMap);

			bw = new BufferedWriter(new OutputStreamWriter(pos));	
		/*	for (HwNode node: tmap.getTMLArchitecture().getHwNodes()) {
				simtraces.add("time=0 block="+ node.getName()+" type=state_entering state=start");
				simIndex++;
			}*/
			for (TMLTask task : tmap.getTMLModeling().getTasks()){
				if (!simtraces.contains("time=0 block="+ task.getName()+" type=state_entering state=startState") && tmlComponentsToValidate.contains(task.getName())){
					simtraces.add("time=0 block="+ task.getName()+" type=state_entering state=startState");
					simIndex++;
				}
			}
			

			//Sort simtraces by end time
			Collections.sort(simtraces, new Comparator<String>() {
    			@Override
    			public int compare(String o1, String o2) {
       				BigInteger i = new BigInteger((o1.split(" ")[0]).split("=")[1]);
					BigInteger j = new BigInteger((o2.split(" ")[0]).split("=")[1]);
					return i.compareTo(j);
    			}
			});
			//
			//
			if (simtraces.size()>2000){
				//Only write last 2000 simulations
				int numTraces = simtraces.size();
				for (int i=0; i<2000; i++){
					bw.write("#" + simIndex+ " " +simtraces.get(numTraces-2000+i));
					bw.newLine();
					bw.flush();
					simIndex++;
				}
			}
			else {
				for (String s: simtraces){
					bw.write("#"+simIndex+ " "+s);
					bw.newLine();
					bw.flush();
					simIndex++;
				}
			}
			bw.close();
			pos.close();
			tmlSimPanel.setVisible(true);
	
		}
		catch (Exception e){
			
		}
	}

	private void updateTimelineTrace() {
        if (tmlSimPanelTimeline != null && tmlSimPanelTimeline.isShowing() && !listOfTaskToShowInTimeLine.equals("")) {
            Thread t =  new Thread(new Runnable() {
                @Override
                public void run() {
                    commandTab.setEnabled(false);
                    tmlSimPanelTimeline.setParam(paramMainCommand.getText().trim());
                    tmlSimPanelTimeline.setContentPaneEnable(false);
                    mctb.setActive(false);
                    isServerReply = false;
                    timelineParam = "";
                    sendCommand( "show-timeline-trace " + listOfTaskToShowInTimeLine );
                    Frame f = new JFrame("Updating Data");
                    JPanel p = new JPanel();
                    JProgressBar b = new JProgressBar();
                    // set initial value
                    b.setValue(0);
                    int temp = 0;
                    b.setStringPainted(true);
                    p.add(b);
                    f.add(p);
                    f.setSize(200, 200);
                    f.setLocationRelativeTo(tmlSimPanelTimeline);
                    f.setVisible(true);
                    while (!isServerReply) {
                        temp++;
                        b.setValue(temp);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    f.dispose();
                    f.setVisible(false);
                    tmlSimPanelTimeline.setStatusBar(status.getText().trim(), time.getText().trim(), info.getText().trim());
                    tmlSimPanelTimeline.setContentPaneEnable(true);
                    mctb.setActive(true);
                    commandTab.setEnabled(true);
                    setAll();
                }
            });
            t.start();

        }
    }

	private void writeSimTraceTimeline() {
        buttonShowTraceTimeline.setEnabled(false);
        //Make a popup to select which tasks
        Vector<String> tmlComponentsToValidate = new Vector<String>();
        List<String> tasks = new ArrayList<String>();
        for (TMLTask task : tmap.getTMLModeling().getTasks()) {
            tasks.add(task.getName());
        }
        JDialogSelectTasks jdstmlc = new JDialogSelectTasks(f, tmlComponentsToValidate, tasks, "Select tasks to show in trace");
        GraphicLib.centerOnParent(jdstmlc);
        jdstmlc.setVisible(true);
        listOfTaskToShowInTimeLine = "";
        for (String taskname : tmlComponentsToValidate) {
            listOfTaskToShowInTimeLine += taskname + ",";
        }

        if (!listOfTaskToShowInTimeLine.equals("")) {
            tmlSimPanelTimeline = new JFrameTMLSimulationPanelTimeline(new Frame(), mgui, this, "Show Trace - Timeline", timelineParam);
            tmlSimPanelTimeline.setParam(paramMainCommand.getText().trim());
            tmlSimPanelTimeline.setVisible(true);
            updateTimelineTrace();
        }
        buttonShowTraceTimeline.setEnabled(true);
    }
	
	public void writeArchitectureSimTrace(){
	}
/*
	public void processDeviceTraces(SimulationTransaction tran){
		
		String command = tran.command;
		if (command.contains(" ")){
			command = command.split(" ")[0];
		}
		try {
			if ((command.equals("Write") || command.equals("Read"))){
				TMLChannel chan = tmap.getTMLModeling().getChannelByShortName(tran.channelName);
				if (chan!=null){
					TMLTask originTask = chan.getOriginTask();
					TMLTask destTask = chan.getDestinationTask();
					if (originTask!=null && destTask!=null){
							String asynchType = (command.equals("Write") ? "send_async" : "receive_async");
							int msgId=chanId;
							if (!msgTimes.containsKey(tran.channelName)){
								msgTimes.put(tran.channelName, new ArrayList<String>());
							} 
							if (!msgTimes.get(tran.channelName).contains(tran.endTime)){
	//						int tmp=msgId-1;
						
							if (command.equals("Write")){	
								if (!channelIDMap.containsKey(tran.channelName)){
									channelIDMap.put(tran.channelName, new ArrayList<Integer>());
								}
								channelIDMap.get(tran.channelName).add(msgId);
								chanId++;
							}
							else {
								if (channelIDMap.containsKey(tran.channelName) && channelIDMap.get(tran.channelName).size()>0){
									msgId=channelIDMap.get(tran.channelName).remove(0);
								}
							
							}
						String trace="";
						if (command.equals("Write")){
							if (tran.nodeType.equals("0")){
								trace = "time=" + tran.endTime+ " block="+ tran.deviceName.replaceAll("_0","") + " type="+asynchType+ " blockdestination="+ tmap.getHwNodeOf(destTask).getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"" +chan.getSize()+"\"";	
							}
						}
						else {
							trace = "time=" + tran.endTime+ " block="+ tran.deviceName.replaceAll("_0","") + " type="+asynchType+ " blockdestination="+ tmap.getHwNodeOf(destTask).getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"" +chan.getSize()+"\"";	
						}
						//	
							if (!simtraces.contains(trace)){
								simtraces.add(trace);
								if (!msgTimes.containsKey(tran.channelName)){
									msgTimes.put(tran.channelName, new ArrayList<String>());
								}
								msgTimes.get(tran.channelName).add(tran.endTime);
							}
						//}
					}
				}
				}
			}
			else if (command.equals("SelectEvent")){
			}
			else if (command.equals("Send")){
			}
			else if (command.equals("Wait")){
			}
			else if (command.equals("Request")){
			}
			else if (command.equals("Notified")){
			}
			else if (command.contains("Execi")){
				String trace="time="+tran.endTime+ " block=" + tran.deviceName.replaceAll("_0","") + " type=state_entering state=exec" + tran.length;
				if (!simtraces.contains(trace)){
					simtraces.add(trace);
				}
			}
			else {
				
			}
		} catch (Exception e){
			
		}
	}*/
    
	protected void addTransactionToNode(SimulationTransaction tran){

		String command = tran.command;

		if (command.contains(" ")){
			command = command.split(" ")[0];
		}
		
		try {
			if ((command.equals("Write") || command.equals("Read")) && tran.nodeType.equals("0")){
				TMLChannel chan = tmap.getTMLModeling().getChannelByShortName(tran.channelName);
				if (chan!=null){
					TMLTask originTask = chan.getOriginTask();
					TMLTask destTask = chan.getDestinationTask();
					if (originTask!=null && destTask!=null){
						String asynchType = (command.equals("Write") ? "send_async" : "receive_async");
						int msgId=chanId;
						if (!msgTimes.containsKey(tran.channelName)){
							msgTimes.put(tran.channelName, new ArrayList<String>());
						} 
						if (!msgTimes.get(tran.channelName).contains(tran.endTime)){
							String trace="";
							if (command.equals("Write")){
								if (destChannelIDMap.containsKey(tran.channelName) && destChannelIDMap.get(tran.channelName).size()>0){
									msgId=destChannelIDMap.get(tran.channelName).remove(0);
								}
								else {
									if (!origChannelIDMap.containsKey(tran.channelName)){
										origChannelIDMap.put(tran.channelName, new ArrayList<Integer>());
									}
									origChannelIDMap.get(tran.channelName).add(msgId);
									chanId++;
								}
								trace = "time=" + tran.endTime+ " block="+ originTask.getName() + " type="+asynchType+ " blockdestination="+ destTask.getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"" +chan.getSize()+"\"";	
							}
							else {
								if (origChannelIDMap.containsKey(tran.channelName) && origChannelIDMap.get(tran.channelName).size()>0){
									msgId=origChannelIDMap.get(tran.channelName).remove(0);
								}
								else {
									if (!destChannelIDMap.containsKey(tran.channelName)){
										destChannelIDMap.put(tran.channelName, new ArrayList<Integer>());
									}
									destChannelIDMap.get(tran.channelName).add(msgId);
									chanId++;
								}
								trace = "time=" + tran.endTime+ " block="+ destTask.getName() + " type="+asynchType+ " blockdestination="+ destTask.getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"" +chan.getSize()+"\"";	
							}
							//	
							if (!simtraces.contains(trace)){
								simtraces.add(trace);
								if (!msgTimes.containsKey(tran.channelName)){
									msgTimes.put(tran.channelName, new ArrayList<String>());
								}
								msgTimes.get(tran.channelName).add(tran.endTime);
							}
							//}
						}
					}
				}
			}
			else if ((command.equals("Send") || command.equals("Wait") || command.equals("SelectEvent"))  && tran.nodeType.equals("0") && !tran.channelName.startsWith("reqChannel")){
				if (command.equals("SelectEvent")){
					String trace="time="+tran.endTime+ " block=" + tran.taskName + " type=state_entering state=SelectEvent";
					if (!simtraces.contains(trace)){
						simtraces.add(trace);
					}
				}
				TMLEvent evt = tmap.getTMLModeling().getEventByShortName(tran.channelName);
				if (evt!=null){
					TMLTask originTask = evt.getOriginTask();
					TMLTask destTask = evt.getDestinationTask();
					if (originTask!=null && destTask!=null){
						String asynchType = (command.equals("Send") ? "send_async" : "receive_async");
						int msgId=chanId;
						if (!msgTimes.containsKey(tran.channelName)){
							msgTimes.put(tran.channelName, new ArrayList<String>());
						} 
						if (!msgTimes.get(tran.channelName).contains(tran.endTime)){
							String trace="";
							if (command.equals("Send")){
								if (destChannelIDMap.containsKey(tran.channelName) && destChannelIDMap.get(tran.channelName).size()>0){
									msgId=destChannelIDMap.get(tran.channelName).remove(0);
								}
								else {
									if (!origChannelIDMap.containsKey(tran.channelName)){
										origChannelIDMap.put(tran.channelName, new ArrayList<Integer>());
									}
									origChannelIDMap.get(tran.channelName).add(msgId);
									chanId++;
								}
								trace = "time=" + tran.endTime+ " block="+ originTask.getName() + " type="+asynchType+ " blockdestination="+ destTask.getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"";	
							}
							else {
								if (origChannelIDMap.containsKey(tran.channelName) && origChannelIDMap.get(tran.channelName).size()>0){
									msgId=origChannelIDMap.get(tran.channelName).remove(0);
								}
								else {
									if (!destChannelIDMap.containsKey(tran.channelName)){
										destChannelIDMap.put(tran.channelName, new ArrayList<Integer>());
									}
									destChannelIDMap.get(tran.channelName).add(msgId);
									chanId++;
								}
								trace = "time=" + tran.endTime+ " block="+ destTask.getName() + " type="+asynchType+ " blockdestination="+ destTask.getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"";
							}
							//	
							if (!simtraces.contains(trace)){
								simtraces.add(trace);
								if (!msgTimes.containsKey(tran.channelName)){
									msgTimes.put(tran.channelName, new ArrayList<String>());
								}
								msgTimes.get(tran.channelName).add(tran.endTime);
							}
							//}
						}
					}
				}
			}
			else if ((command.equals("Request") || command.equals("Wait")) && tran.nodeType.equals("0") && tran.channelName.startsWith("reqChannel")){
				TMLRequest req=null;
				for (TMLRequest request: tmap.getTMLModeling().getRequests()){
					if (tran.channelName.replaceAll("reqChannel_","").equals(request.getDestinationTask().getName())){						
						if (command.equals("Request")){
							for (TMLTask t: request.getOriginTasks()){
								if (tran.taskName.equals(t.getName())){
									req=request;
								}
							}
						}
						else {
							req=request;
						}
					}
				//	
				}

				if (req!=null) {
					TMLTask destTask = req.getDestinationTask();
					if (destTask!=null){
						String asynchType = (command.equals("Request") ? "send_async" : "receive_async");
						int msgId=chanId;
						if (!msgTimes.containsKey(tran.channelName)){
							msgTimes.put(tran.channelName, new ArrayList<String>());
						}
						if (!channelMsgIdMap.containsKey(tran.channelName)){
							channelMsgIdMap.put(tran.channelName, new ArrayList<Integer>());
						} 
						


						if (!msgTimes.get(tran.channelName).contains(tran.endTime)){
							
							
							String trace="";
							if (command.equals("Request")){
								if (destChannelIDMap.containsKey(tran.channelName) && destChannelIDMap.get(tran.channelName).size()>0){
									msgId=destChannelIDMap.get(tran.channelName).remove(0);
								}
								else {
									if (!origChannelIDMap.containsKey(tran.channelName)){
										origChannelIDMap.put(tran.channelName, new ArrayList<Integer>());
									}
									origChannelIDMap.get(tran.channelName).add(msgId);
									chanId++;
								}				
								trace = "time=" + tran.endTime+ " block="+ tran.taskName + " type="+asynchType+ " blockdestination="+ destTask.getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"";	
								msgIdStartTimeMap.put(msgId, tran.endTime);
							}
							else {
								if (origChannelIDMap.containsKey(tran.channelName) && origChannelIDMap.get(tran.channelName).size()>0){
									msgId=origChannelIDMap.get(tran.channelName).remove(0);
								}
								else {
									if (!destChannelIDMap.containsKey(tran.channelName)){
										destChannelIDMap.put(tran.channelName, new ArrayList<Integer>());
									}
									destChannelIDMap.get(tran.channelName).add(msgId);
									chanId++;
								}
								trace = "time=" + tran.endTime+ " block="+ destTask.getName() + " type="+asynchType+ " blockdestination="+ destTask.getName() + " channel="+tran.channelName+" msgid="+ msgId + " params=\"";	
								msgIdEndTimeMap.put(msgId, tran.endTime);
							}
							
							channelMsgIdMap.get(tran.channelName).add(msgId);
							//	
							if (!simtraces.contains(trace)){
								simtraces.add(trace);
								if (!msgTimes.containsKey(tran.channelName)){
									msgTimes.put(tran.channelName, new ArrayList<String>());
								}
								msgTimes.get(tran.channelName).add(tran.endTime);
							}
							//}
						}
					}
				}
			}
			else if (command.contains("Execi")){
				String trace="time="+tran.endTime+ " block=" + tran.taskName + " type=state_entering state=exec" + tran.length;
				if (!simtraces.contains(trace)){
					simtraces.add(trace);
				}
			}
            else if (command.contains("Delay")){
                String trace="time="+tran.endTime+ " block=" + tran.taskName + " type=state_entering state=delay" + tran.length;
                if (!simtraces.contains(trace)){
                    simtraces.add(trace);
                }
            }
			else {
				//TraceManager.addDev("UNHANDLED COMMAND " + tran.command + " " + tran.deviceName + " " + tran.nodeType);
			}
			//


			//	bw.write("#1 time=0.000000000 block=Attacker blockdestination=Bob type=synchro channel= params=\"Attacker.m2");
			//			bw.newLine();
			//		bw.flush();
			//bw.close();
			//pos.close();
		}
		catch (Exception e){
			
		}

		/*String nodename = tran.deviceName;
		TraceManager.addDev("Transaction=" + tran);
		for(HwNode node: tmap.getTMLArchitecture().getHwNodes()) {
		    TraceManager.addDev("Adding transaction for " + nodename + " vs " + node.getName());
			if ((node.getName()+"_0").equals(nodename)){
				mgui.addTransaction(node.getID(), tran);
			}
		}*/
        //TraceManager.addDev("Transaction=" + tran);
        for(HwNode node: tmap.getTMLArchitecture().getHwNodes()) {
            //TraceManager.addDev("Adding transaction for " + tran.uniqueID + " vs " + node.getID());
            if (node.getID() == tran.uniqueID){
                mgui.addTransaction(node.getID(), tran);
                break;
            }
        }
	}
    
    public void calculateCorrespondingTimes(){
	    for (String channel: channelMsgIdMap.keySet()){
	    	List<Integer> minTimes = new ArrayList<Integer>();
	    	for (int msgId: channelMsgIdMap.get(channel)){
	    		String startTime = msgIdStartTimeMap.get(msgId);
	    		String endTime = msgIdEndTimeMap.get(msgId);
	    		if (startTime!=null && endTime !=null){
	    			int diff = Integer.valueOf(endTime) - Integer.valueOf(startTime); 
	    			minTimes.add(diff);
	    		}
	    	}  
	    	SimulationLatency sl = new SimulationLatency();
        	sl.setTransaction1("Send Req: " + channel);
        	sl.setTransaction2("Corresponding Wait Req " + channel);
        	
        	
        	
        	
       		sl.setMinTime("??");
       		sl.setMaxTime("??");
        	sl.setAverageTime("??");
        	sl.setStDev("??");
        	boolean found=false;
        	for (Object o:latencies){
        	    SimulationLatency s = (SimulationLatency) o;
        	    if (s.getTransaction1().equals(sl.getTransaction1()) && s.getTransaction2().equals(sl.getTransaction2())){
        	    	sl = s;
        	        found=true;
        	    }
        	}
        	if (!found){
        	    latencies.add(sl);
        	}
        	if (minTimes.size()>0){
            	int sum=0;
                sl.setMinTime(Integer.toString(Collections.min(minTimes)));
                sl.setMaxTime(Integer.toString(Collections.max(minTimes)));
                for (int time: minTimes){
                	sum+=time;
                }
                double average = (double) sum/ (double) minTimes.size();
                double stdev =0.0;
                for (int time:minTimes){
                	stdev +=(time - average)*(time-average);
                }
                stdev= stdev/minTimes.size();
                stdev = Math.sqrt(stdev);
                sl.setAverageTime(String.format("%.1f",average));
                sl.setStDev(String.format("%.1f",stdev)); 
            }
        	
        	
        	//updateLatency();
        //	if (latm !=null && latencies.size()>0){
        	 //   latm.setData(latencies);
        //	}
        	
	    	
	    }
	    
	    
	    
    	//System.out.println(channelMsgIdMap);
    	//System.out.println(msgIdStartTimeMap);
    	//System.out.println(msgIdEndTimeMap);
    }
    
    protected void addStatusToNode(String status, String task){
        mgui.addStatus(task,status);
    }
    
    protected boolean loadConfiguration(Node node1) {
        NodeList diagramNl = node1.getChildNodes();
        if (diagramNl == null) {
            return false;
        }
        Element elt, elt0;
        Node node, node0, node00;
        NodeList nl, nl0;

        String msg = null;
        String error = null;
        String hash = null;

        String id, idvar;
        String name;
        String command;
        String startTime="", finishTime="";
        String progression="", nextCommand="";
        String transStartTime="", transFinishTime="";
        String util = null;
        String value;
        String extime;
        String contdel;
        String busname;
        String busid;
        String state;
//        String usedEnergy;
        boolean transInfo = false;

        int k;

        try {
            for(int j=0; j<diagramNl.getLength(); j++) {
                node = diagramNl.item(j);

                if (node == null) {
                    TraceManager.addDev("null node");
                    return false;
                }

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    elt = (Element)node;

                    // Status
                    if (elt.getTagName().compareTo(SIMULATION_GLOBAL) == 0) {
						String minT = "";
						String maxT = "";

                        nl = elt.getElementsByTagName("status");
                        
                        if ((nl != null) && (nl.getLength() > 0)) {
                            node0 = nl.item(0);
                            //

                            makeStatus(node0.getTextContent());
                        }

                        nl = elt.getElementsByTagName("brkreason");
                        if ((nl != null) && (nl.getLength() > 0)) {
                            node0 = nl.item(0);
                            //

                            makeBrkReason(node0.getTextContent());
                        }

                        nl = elt.getElementsByTagName("simtime");
                        if ((nl != null) && (nl.getLength() > 0)) {
                            gotTimeAnswerFromServer = true;
                            node0 = nl.item(0);
                            //
                            if (node0.getTextContent() != null) {
                                String val = node0.getTextContent();
                                TraceManager.addDev("Sim time=" + val);
                                long valueCycle = Long.decode(val);
                                long timeP = ((valueCycle) * 1000 / frequency);
                                val = formatString(val);
                                String timePS = formatString(""+timeP);
                                val = val + " cycles / " + timePS  + " ns";
                                time.setText(val);
                            }
                        }

                        nl = elt.getElementsByTagName("simtimemin");
                        
                        if ((nl != null) && (nl.getLength() > 0)) {
                            gotTimeAnswerFromServer = true;
                            node0 = nl.item(0);
                            //
                            minT = node0.getTextContent();
                            //time.setText(minT + " ... " + maxT);
                        }

                        nl = elt.getElementsByTagName("simtimemax");
                        
                        if ((nl != null) && (nl.getLength() > 0)) {
                            gotTimeAnswerFromServer = true;
                            node0 = nl.item(0);
                            //
						    maxT = node0.getTextContent();
						    
						    if (minT.compareTo(maxT) != 0) {
						    	time.setText(minT + " ... " + maxT);
						    }
                        }

                        nl = elt.getElementsByTagName("msg");
                        if ((nl != null) && (nl.getLength() > 0)) {
                            node0 = nl.item(0);
                            msg = node0.getTextContent();
//                            if (msg != null && msg.contains("run-to-next-breakpoint-max-trans")) {
//                                TraceManager.addDev("Server: " + msg);
//                            }
                        }

                        nl = elt.getElementsByTagName("error");
                        if ((nl != null) && (nl.getLength() > 0)) {
                            node0 = nl.item(0);
                            error = node0.getTextContent();
                        }

                        nl = elt.getElementsByTagName("hashval");
                        if ((nl != null) && (nl.getLength() > 0)) {
                            node0 = nl.item(0);
                            hash = node0.getTextContent();
                        }

                        nl = elt.getElementsByTagName(SIMULATION_COMMAND);
                        for(int kk=0; kk<nl.getLength(); kk++) {
                            node0 = nl.item(kk);
                            elt0 = (Element)node0;
                            id = null;
                            name = null;
                            command = null;
                            id = elt0.getAttribute("id");
                            //TraceManager.addDev("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
                            nl0 = elt0.getElementsByTagName("exectimes");
                            if ((nl0 != null) && (nl0.getLength() > 0)) {
                                node00 = nl0.item(0);
                                //TraceManager.addDev("nl0:" + nl0 + " value=" + node00.getNodeValue() + " content=" + node00.getTextContent());
                                util = node00.getTextContent();
                            }

                            //TraceManager.addDev("Got info on command " + id + " util=" + util);

                            if ((id != null) && (util != null)) {
                                //TraceManager.addDev("Updating command");
                                updateCommandExecutionState(id, util);
                            }
                        }
                    }

                    if (hashOK) {
                        if (elt.getTagName().compareTo(SIMULATION_TASK) == 0) {
                            //                                  for (int i=0; i<elt.getAttributes().getLength(); i++){
                            //                                                  
                            //                                                  }
                        //    System.out.println("element " + elt);
                            id = null;
                            name = null;
                            command = null;
                            nextCommand = null;
                            progression = null;
                            startTime = null; finishTime = null;
                            transStartTime = null; transFinishTime = null;
                            id = elt.getAttribute("id");
                            name = elt.getAttribute("name");
                            nl = elt.getElementsByTagName("currcmd");
                            if ((nl != null) && (nl.getLength() > 0)) {
                                node0 = nl.item(0);
                                if (node0.getNodeType() == Node.ELEMENT_NODE) {
                                    elt0 = (Element)node0;
                                    command = elt0.getAttribute("id");
                                }
                                nl = elt.getElementsByTagName("progr");
                                if ((nl != null) && (nl.getLength() > 0)) {
                                    node0 = nl.item(0);
                                    progression = node0.getTextContent();
                                    //
                                }
                                nl = elt.getElementsByTagName("starttime");
                                if ((nl != null) && (nl.getLength() > 0)) {
                                    node0 = nl.item(0);
                                    startTime = node0.getTextContent();
                                    //
                                }
                                nl = elt.getElementsByTagName("finishtime");
                                if ((nl != null) && (nl.getLength() > 0)) {
                                    node0 = nl.item(0);
                                    finishTime = node0.getTextContent();
                                    //
                                }
                                nl = elt.getElementsByTagName("transstarttime");
                                if ((nl != null) && (nl.getLength() > 0)) {
                                    node0 = nl.item(0);
                                    transStartTime = node0.getTextContent();
                                    //
                                }
                                nl = elt.getElementsByTagName("transfinishtime");
                                if ((nl != null) && (nl.getLength() > 0)) {
                                    node0 = nl.item(0);
                                    transFinishTime = node0.getTextContent();
                                    //
                                }
                                nl = elt.getElementsByTagName("nextcmd");
                                if ((nl != null) && (nl.getLength() > 0)) {
                                    node0 = nl.item(0);
                                    nextCommand = node0.getTextContent();
                                    //
                                }

                            }

                            //
                            extime = null;
                            nl = elt.getElementsByTagName("extime");
                            if ((nl != null) && (nl.getLength() > 0)) {
                                node0 = nl.item(0);
                                extime =  node0.getTextContent();
                            }

                            state = null;
                            nl = elt.getElementsByTagName("tskstate");
                            if ((nl != null) && (nl.getLength() > 0)) {
                                node0 = nl.item(0);
                                state =  node0.getTextContent();
                                //TraceManager.addDev("TASK STATE: " + state);
                            }

                            if ((id != null) && ((extime != null) || (state != null))) {
                                addStatusToNode(state, name);
                                updateTaskCyclesAndState(id, extime, state);
                            }


                            if ((id != null) && (command != null)) {
                                
                            	if (nextCommand ==null) {
                                    nextCommand = "-1";
                                }
                                updateRunningCommand(id, command, progression, startTime, finishTime, nextCommand, transStartTime, transFinishTime, state);
                                if (checkTable.containsKey(command)){
                                    if (!transTimes.containsKey(command)){
                                        ArrayList<String> timeList = new ArrayList<String>();
                                        transTimes.put(command, timeList);
                                    }
                                    if (!transTimes.get(command).contains(finishTime)){
                                        transTimes.get(command).add(finishTime);
                                    }
                                }
                            }

                            if (openDiagram.isEnabled() && openDiagram.isSelected() && (name != null) && (command != null)) {
                                updateOpenDiagram(name, command, progression, startTime, finishTime, transStartTime, transFinishTime);
                            }

                            nl = elt.getElementsByTagName("var");

                            if ((nl != null) && (nl.getLength() > 0)) {
                                idvar = null;
                                value = null;
                                for(k=0; k<nl.getLength(); k++) {
                                    node0 = nl.item(k);
                                    value = node0.getTextContent();
                                    if (node0.getNodeType() == Node.ELEMENT_NODE) {
                                        elt0 = (Element)node0;
                                        idvar = elt0.getAttribute("id");
                                    }
                                    if ((value != null) && (idvar != null)) {
                                        updateVariableState(idvar, value);
                                        jpsv.updateOnVariableValue(idvar);
                                    }
                                }
                            }
                        }

                        if (elt.getTagName().compareTo(SIMULATION_TRANS) == 0) {
							//TraceManager.addDev("New simulation transaction:" + elt);
                            SimulationTransaction st = new SimulationTransaction();
                            st.nodeType = elt.getAttribute("deviceid");

                            try {
                                st.uniqueID = Integer.parseInt(elt.getAttribute("uniqueid"));
                            } catch (Exception e) {

                            }

                            st.deviceName = elt.getAttribute("devicename");
                            st.coreNumber = elt.getAttribute("corenumber");

                            String commandT = elt.getAttribute("command");
                            if (commandT != null) {
                                int index = commandT.indexOf(": ");
                                if (index == -1){
                                    st.taskName = "Unknown";
                                    st.command = commandT;
                                } else {
                                    st.taskName = commandT.substring(0, index).trim();
                                    st.command = commandT.substring(index+1, commandT.length()).trim();
                                }
                            }

                            //TraceManager.addDev("Command handled");
                            st.startTime = elt.getAttribute("starttime");
                            st.endTime = elt.getAttribute("endtime");
                            String taskId= elt.getAttribute("id");
                            //
                            if (checkTable.containsKey(taskId)){
                                //
                                if (!transTimes.containsKey(taskId)){
                                    ArrayList<String> timeList = new ArrayList<String>();
                                    transTimes.put(taskId, timeList);
                                }
                                if (!transTimes.get(taskId).contains(st.endTime)){
                                    transTimes.get(taskId).add(st.endTime);
                                }
                                //

                            }
                            st.length = elt.getAttribute("length");
                            st.virtualLength = elt.getAttribute("virtuallength");
                            st.channelName = elt.getAttribute("ch");

                            //st.id = id;
                            if (trans == null) {
                                trans = new Vector<SimulationTransaction>();
                            }

                            trans.add(st);
                            addTransactionToNode(st);
                            transInfo = true;
                        }


                        //
                        if (elt.getTagName().compareTo(SIMULATION_TRANS_NB) == 0) {
                            transInfo = true;
                            //
                            name = elt.getAttribute("nb");
                            try {
                                int nb = Integer.decode(name).intValue();
                                if (nb < 1) {
                                    trans = new Vector<SimulationTransaction>();
                                }
                            } catch (Exception e) {
                            }
                        }


                        if (elt.getTagName().compareTo(SIMULATION_CPU) == 0) {
                            id = null;
                            name = null;
                            command = null;
                            contdel = null;
                            busname = null;
                            busid = null;
//                            usedEnergy = null;

                            id = elt.getAttribute("id");
                            name = elt.getAttribute("name");
                            nl = elt.getElementsByTagName("util");
                            if ((nl != null) && (nl.getLength() > 0)) {
                                node0 = nl.item(0);
                                //
                                util = node0.getTextContent();
                            }
//                            nl = elt.getElementsByTagName("energy");
//                            if ((nl != null) && (nl.getLength() > 0)) {
//                                node0 = nl.item(0);
//                                //
//                                usedEnergy = node0.getTextContent();
//                            }

                            //
                            nl = elt.getElementsByTagName("contdel");
                            if ((nl != null) && (nl.getLength() > 0)) {
                                nl = elt.getElementsByTagName("contdel");
                                node0 = nl.item(0);
                                elt0 = (Element)node0;
                                busid = elt0.getAttribute("busID");
                                busname = elt0.getAttribute("busName");
                                //
                                contdel = node0.getTextContent();
                            }

                            //


                            if ((id != null) && (util != null)) {
                                updateCPUState(id, util, contdel, busname, busid);
                            }
                        }

                        //TraceManager.addDev("toto2");

                        if (elt.getTagName().compareTo(SIMULATION_BUS) == 0) {
                            id = null;
                            name = null;
                            command = null;
                            id = elt.getAttribute("id");
                            name = elt.getAttribute("name");
                            nl = elt.getElementsByTagName("util");
                            if ((nl != null) && (nl.getLength() > 0)) {
                                node0 = nl.item(0);
                                //
                                util = node0.getTextContent();
                            }

                            //

                            if ((id != null) && (util != null)) {
                                updateBusState(id, util);
                            }
                        }

                        //TraceManager.addDev("toto3 " + elt.getTagName());
                    }
                }
            }
            if (transInfo) {
                if (trans == null) {
                    trans = new Vector<SimulationTransaction>();
                }
                //TraceManager.addDev("Transinfo -> " + trans.size());
                if (transactionPanel != null) {
                    transactionPanel.setData(trans);
                }
                if (taskTransactionPanel != null) {
                    taskTransactionPanel.setData(trans);
                }
                if (latencyPanel !=null){
                    processLatency();
                }
                //ttm.setData(trans);
            }
        } catch (Exception e) {
            TraceManager.addError("Exception in xml parsing " + e.getMessage() + " node= " + node1);
            return false;
        }

        if ((msg != null) && (error != null)) {
            if (error.trim().equals("0")) {
                //printFromServer(msg + ": command successful");
                
                if (msg.indexOf("reset") != -1) {
                    time.setText("0");
                }
		
                if (msg.compareTo("Tree was explored") == 0) {
                	addGraph();
                }
	        } 
            else {
	            printFromServer(msg + ": command failed (error=" + error + ")");
	        }
        } 
        else if (msg != null) {
            printFromServer("Server: " + msg);
        } 
        else {
            //TraceManager.addDev("Node: " +node1 + " diagramNL=" + diagramNl);
            //printFromServer("Server: error " + error);
        }

        if ((hash != null) && (tmap != null)) {
            try {
                int thehash = Integer.decode(hash).intValue();

                if (thehash != hashCode) {
                    jta.append("\n*** Simulated model is not the model currently loaded under TTool ***\n");
                    jta.append("*** Some features are therefore deactivated ***\n\n");
                    hashOK = false;
                    wrongHashCode();
                } else {
                    askForUpdate();
                    sendBreakPointList();
                    jta.append("\n*** Simulated model is the one currently loaded under TTool ***\n");
                    hashOK = true;
                    animate.setSelected(true);
                    animate.setEnabled(true);
                    diploids.setEnabled(true);
                    animateWithInfo.setSelected(true);
                    animateWithInfo.setEnabled(true);
                    openDiagram.setEnabled(true);
                    cpus.setEnabled(true);
                    busses.setEnabled(true);
                    mems.setEnabled(true);
                    tasks.setEnabled(true);
                    chans.setEnabled(true);
                }
            } catch (Exception e) {
            }
        }

        return true;
    }

    private void wrongHashCode() {
        TraceManager.addDev("Wrong hash code");

        cpuPanel.setVisible(false);
        variablePanel.setVisible(false);
        openDiagram.setSelected(false);
        openDiagram.setEnabled(false);
        animate.setEnabled(false);
        diploids.setEnabled(false);
        animate.setSelected(false);
        diploids.setSelected(false);
        animateWithInfo.setSelected(false);
        animateWithInfo.setEnabled(false);
        update.setEnabled(false);
        update.setSelected(false);

        cpus.setEnabled(false);
        busses.setEnabled(false);
        mems.setEnabled(false);
        tasks.setEnabled(false);
        chans.setEnabled(false);
        cpus.removeAllItems();
        busses.removeAllItems();
        mems.removeAllItems();
        tasks.removeAllItems();
        chans.removeAllItems();

        jpsv.setEnabled(false);
        jpsv.unsetElements();

        actions[InteractiveSimulationActions.ACT_RUN_TO_NEXT_BUS_TRANSFER].setEnabled(false);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CPU_EXECUTES].setEnabled(false);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_TASK_EXECUTES].setEnabled(false);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_MEMORY_ACCESS].setEnabled(false);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CHANNEL_ACCESS].setEnabled(false);

        // Set variable tab is removed
        //
        commandTab.removeTabAt(2);
        jpsv = null;

        while(infoTab.getTabCount() > 2) {
            infoTab.removeTabAt(2);
        }
        jpbp.unsetElements();

    }

    public synchronized void startThread(int mode) {
        threadMode = mode;
        t = new Thread(this);
        t.start();
        threadStarted = false;
        //
        while(threadStarted == false) {
            try {
                wait();
            } catch (InterruptedException ie) {}
        }
    }

    public synchronized void threadStarted() {
        TraceManager.addDev("thread started");
        threadStarted = true;
        notify();
    }

    public void makeBrkReason(String s) {
        info.setText(s);
    }

    public void makeStatus(String s) {
        //

        if (s.equals("busy")) {
            status.setText("Busy");
            setBusyStatus();
            busyMode = 2;
            //busyStatus = true;
        }
        if (s.equals("ready")) {
            status.setText("Ready");
            if (busyMode == 2) {
                //
                askForUpdate();
                //sendCommand("time");
            }
            busyMode = 1;
            setBusyStatus();
        }

        if (s.equals("term")) {
            status.setText("Terminated");
            if (busyMode == 2) {
                askForUpdate();
            }
            busyMode = 3;
            setBusyStatus();

            //
        }
        setLabelColors();
    }

    public void setBusyStatus() {
        setAll();
        actions[InteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(busyMode == 2);
    }

    public void setLabelColors() {
        switch(busyMode) {
        case 0:
            status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
            time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
            info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
            break;
        case 1:
            status.setForeground(ColorManager.InteractiveSimulationText_READY);
            time.setForeground(ColorManager.InteractiveSimulationText_READY);
            info.setForeground(ColorManager.InteractiveSimulationText_READY);
            break;
        case 2:
            status.setForeground(ColorManager.InteractiveSimulationText_BUSY);
            time.setForeground(ColorManager.InteractiveSimulationText_BUSY);
            info.setForeground(ColorManager.InteractiveSimulationText_BUSY);
            break;
        case 3:
            status.setForeground(ColorManager.InteractiveSimulationText_TERM);
            time.setForeground(ColorManager.InteractiveSimulationText_TERM);
            info.setForeground(ColorManager.InteractiveSimulationText_TERM);
            break;
        }


    }

    public void setAll() {
        boolean b = false;
        if (busyMode == 1) {
            b = true;
        }
        actions[InteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_X_TIME_UNITS].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_TO_TIME].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_X_TRANSACTIONS].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_EXPLORATION].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].setEnabled(b);

        if (jpsv != null) {
            jpsv.setVariableButton(b);
        }

        if (busyMode == 3) {
            actions[InteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
        }

        if (!hashOK) {
            b = false;
        }

        actions[InteractiveSimulationActions.ACT_RUN_TO_NEXT_BUS_TRANSFER].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CPU_EXECUTES].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_TASK_EXECUTES].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_MEMORY_ACCESS].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CHANNEL_ACCESS].setEnabled(b);

        b = !((busyMode == 0) || (busyMode == 2));

        actions[InteractiveSimulationActions.ACT_SAVE_VCD].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_SAVE_HTML].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_SAVE_TXT].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_SAVE_XML].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_PRINT_BENCHMARK].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_SAVE_BENCHMARK].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_SAVE_STATE].setEnabled(b);
        actions[InteractiveSimulationActions.ACT_RESTORE_STATE].setEnabled(b);
    }

    public static String decodeString(String s)  {
        if (s == null)
            return s;
        byte b[] = null;
        try {
            b = s.getBytes("ISO-8859-1");
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

    private void printFromServer(String s) {
        jta.append("Server> " + s + "\n");
    }


    // Mouse management
    @Override
    public void mouseReleased(MouseEvent e) {}


    /**
     * This adapter is constructed to handle mouse over component events.
     */
    private class MouseHandler extends MouseAdapter  {

        private final JLabel label;

        /**
         * ctor for the adapter.
         * @param label the JLabel which will recieve value of the
         *              Action.LONG_DESCRIPTION key.
         */
        public MouseHandler(JLabel label)  {
            this.label = label;
        }

//        public void setLabel(JLabel label)  {
//            this.label = label;
//        }

        @Override
        public void mouseEntered(MouseEvent evt)  {
            if (evt.getSource() instanceof AbstractButton)  {
                AbstractButton button = (AbstractButton)evt.getSource();
                Action action = button.getAction();
                if (action != null)  {
                    String message = (String)action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }

    public void sendCommandWithPositiveInt(String command) {
        String param = paramMainCommand.getText().trim();
        if (tmlSimPanelTimeline != null && tmlSimPanelTimeline.isFocused()) {
            param = tmlSimPanelTimeline.getParam();
            paramMainCommand.setText(param);
        }
        if (isAPositiveInt(param)) {
            sendCommand(command + " " + param);
        } else {
            error("Wrong parameter: must be a positive int");
        }
    }

    public void sendCommandWithMaxTrans(String command) {
        String param = paramMainCommand.getText().trim();
        if (tmlSimPanelTimeline != null && tmlSimPanelTimeline.isFocused()) {
                param = tmlSimPanelTimeline.getParam();
                paramMainCommand.setText(param);
        }
        //if param > 0 then send command with param, else send command with default max trans value = 1000
        if (isAPositiveInt(param)) {
            sendCommand(command + " " + param);
        } else {
            sendCommand(command + " 0");
        }
    }

    private void saveTraceVCD() {
    	sendSaveTraceCommand( manageFileExtension( saveFileName.getText(), ".vcd" ), "0" );
    }

    private void saveTraceHTML() {
    	sendSaveTraceCommand( manageFileExtension( saveFileName.getText(), ".html" ), "1" );
    }

    private void saveTraceText() {
    	sendSaveTraceCommand( manageFileExtension( saveFileName.getText(), ".txt" ), "2" );
    }
    private void saveTraceXml() {
    	sendSaveTraceCommand( manageFileExtension( saveFileName.getText(), ".xml" ), "3" );
    }
    private String manageFileExtension( String filename,
    									final String extension ) {
    	filename = filename.trim();
    	
    	if ( !filename.isEmpty() && !filename.endsWith( extension ) ) {
    		return filename.concat( extension );
    	}
    
    	return filename;
    }

    // Format. VCD=0, HTML=1, TXT=2
    private void sendSaveTraceCommand( 	String filename,
    									final String format) {
//        String param = saveFileName.getText().trim();

        //TraceManager.addDev("format >" + format + "<");

        if ( filename.isEmpty() ) {
        	final String message = "Please enter a file name for the trace.";
        	JOptionPane.showMessageDialog( this, message, "Output File Name not Specified", JOptionPane.ERROR_MESSAGE );
        	error( message );
        }
        else {
            String original = filename;
        	final String directory = saveDirName.getText().trim();
        	
	        if ( !directory.isEmpty() ) {
	        	if (!directory.endsWith(File.separator))
	        		filename = directory + File.separator + filename;
	        	else
	        		filename = directory + filename;
	        }

	        // VCD
	        if (format.compareTo("0") == 0) {
	        	//SpecConfigTTool.ExternalCommand1 = SpecConfigTTool.ExternalCommand1.replace(SpecConfigTTool.lastVCD, filename);
	        	SpecConfigTTool.lastVCD = filename;

	        }

	        int type;
            if (format.compareTo("0") == 0) {
                type = SimulationTrace.VCD_DIPLO;
            } else if (format.compareTo("1") == 0 || format.compareTo("4") == 0) {
	            type = SimulationTrace.HTML_DIPLO;
            } else  if (format.compareTo("2") == 0){
	            type = SimulationTrace.TXT_DIPLO;
            } else
            {
            	 type = SimulationTrace.XML_DIPLO;
            }
	        SimulationTrace st = new SimulationTrace(original, type, filename);
            mgui.addSimulationTrace(st);

	        // DB: now useless check
//	        if (param.length() >0) {
	        sendCommand( "save-trace-in-file" + " " + format + " " + filename );
//	        } else {
//	            error("Wrong parameter: must be a file name");
//	        }
        }
    }

    public void sendSaveStateCommand() {
        String param = stateFileName.getText().trim();
        if (param.length() >0) {
            sendCommand("save-simulation-state-in-file " + param);
        } else {
            error("Wrong parameter: must be a file name");
        }
    }

    public void sendRestoreStateCommand() {
        String param = stateFileName.getText().trim();
        if (param.length() >0) {
            sendCommand("restore-simulation-state-from-file " + param);
        } else {
            error("Wrong parameter: must be a file name");
        }
    }

    public void sendSaveBenchmarkCommand() {
        String param = benchmarkFileName.getText().trim();
        if (param.length() >0) {
            sendCommand("get-benchmark 1 " + param);
        } else {
            error("Wrong benchmark parameter: must be a file name");
        }
    }

    private void runExploration() {
        animate.setSelected(false);
        mgui.setDiploAnimate(animate.isSelected());
        diploids.setEnabled(animate.isSelected());
        animateWithInfo.setEnabled(animate.isSelected());
        openDiagram.setEnabled(animate.isSelected());
        update.setSelected(false);
        String graph = getCurrentRGName();
        sendCommand("run-exploration " + minimalCommandCoverage.getValue() + " " + minimalBranchCoverage.getValue()
                + " " + graph);

    }

    private void addGraph() {
    	TraceManager.addDev("Adding graph");
    	mgui.setLastRGDiplodocus(lastGraphName);
    }

    private String getCurrentRGName() {
		DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
		Date date = new Date();
		String dateAndTime=dateFormat.format(date);
		lastGraphName = "RG_Diplo_" + dateAndTime;

		return lastGraphName;
    }

    private void updateVariables() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        sendCommand("get-variable-of-task all all\n");

        /*for(TMLTask task: tmap.getTMLModeling().getTasks()) {
          for(TMLAttribute tmla: task.getAttributes()) {
          sendCommand("get-variable-of-task " + task.getID() + " " + tmla.getID());
          }
          }*/
    }

    private void updateCPUs() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        for(HwNode node: tmap.getTMLArchitecture().getHwNodes()) {
            if ((node instanceof HwCPU) || (node instanceof HwA)){
                sendCommand("get-info-on-hw 0 " + node.getID());
            }
        }
    }

    private void updateMemories() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        for(HwNode node: tmap.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwMemory) {
                sendCommand("get-info-on-hw 2 " + node.getID());
            }
        }
    }

    private void updateBus() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        for(HwNode node: tmap.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBus) {
                sendCommand("get-info-on-hw 1 " + node.getID());
            }
        }
    }

    private void updateTasks() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        for(TMLTask task: tmap.getTMLModeling().getTasks()) {
            sendCommand("get-info-on-hw 5 " + task.getID());
        }
    }

    private void updateTransactions() {
        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        trans = null;

        int nb = NB_OF_TRANSACTIONS;
        if (transactionPanel != null) {
            nb = transactionPanel.getNbOfTransactions();
        }
        if (taskTransactionPanel != null) {
            nb = taskTransactionPanel.getNbOfTransactions();
        }
        sendCommand("lt " + nb);
    }

    private void addLatency(){
        SimulationLatency sl = new SimulationLatency();
        if (transaction1.getSelectedItem() !=null && transaction2.getSelectedItem() != null){        
        	sl.setTransaction1(transaction1.getSelectedItem().toString());
        	sl.setTransaction2(transaction2.getSelectedItem().toString());
       		sl.setMinTime("??");
       		sl.setMaxTime("??");
        	sl.setAverageTime("??");
        	sl.setStDev("??");
        	boolean found=false;
        	for (Object o:latencies){
        	    SimulationLatency s = (SimulationLatency) o;
        	    if (s.getTransaction1() == sl.getTransaction1() && s.getTransaction2() == sl.getTransaction2()){
        	        found=true;
        	    }
        	}
        	if (!found){
        	    latencies.add(sl);
        	}
        	updateLatency();
        	if (latm !=null && latencies.size()>0){
        	    latm.setData(latencies);
        	}
        }
    }
    private void updateLatency(){
        for (Object o:latencies){
            SimulationLatency sl = (SimulationLatency) o;
            //calcuate response + checkpoint 1 id + checkpoint 2 id
            List<String> id1List = nameIdMap.get(sl.getTransaction1());
            List<String> id2List = nameIdMap.get(sl.getTransaction2());            
            if (id1List!=null && id2List!=null){
	            for (String id1: id1List){
    	        	for (String id2: id2List){
    	    	    	sendCommand("cl " + id1 + " " + id2);
    	    	    }
    	    	}
        	}
        }
    }

    private void processLatency(){

//        TraceManager.addDev(transTimes.toString());
		//
		//
        for (Object o: latencies){
            SimulationLatency sl = (SimulationLatency) o;
            sl.setMinTime("??");
            sl.setMaxTime("??");
            sl.setAverageTime("??");
            sl.setStDev("??");
            List<String> ids1 = nameIdMap.get(sl.getTransaction1());
            List<String> ids2 = nameIdMap.get(sl.getTransaction2());
            List<Integer> times1 = new ArrayList<Integer>();
            List<Integer> times2 = new ArrayList<Integer>();
            if (ids1!=null && ids2!=null){
				for (String id1: ids1){
					if (transTimes.containsKey(id1)){
						for(String time1: transTimes.get(id1)){
		                	times1.add(Integer.valueOf(time1));
		                }
					}
				}
				for (String id2: ids2){
					if (transTimes.containsKey(id2)){			
    	            	ArrayList<Integer> minTimes = new ArrayList<Integer>();
    	                for (String time2: transTimes.get(id2)){
		                	times2.add(Integer.valueOf(time2));
		                }
		            }
		        }
		    }
	       // 
	       //
	        List<Integer> minTimes = new ArrayList<Integer>();
	        for (int time1 : times1){
				int match = Integer.MAX_VALUE;
				//Find the first subsequent transaction
	            int time = Integer.MAX_VALUE;
	            for (int time2: times2){

   		        	int diff = time2 - time1;
                    if (diff < time && diff >=0){
                    	time=diff;
						match = time2;
                    }
                }
				try {
				if (times2.contains(match)){
					times2.remove(Integer.valueOf(match));
					}
				} catch (Exception e){
				}
                if (time!=Integer.MAX_VALUE){

            		minTimes.add(time);
            	}
            }
            if (minTimes.size()>0){
            	int sum=0;
                sl.setMinTime(Integer.toString(Collections.min(minTimes)));
                sl.setMaxTime(Integer.toString(Collections.max(minTimes)));
                for (int time: minTimes){
                	sum+=time;
                }
                double average = (double) sum/ (double) minTimes.size();
                double stdev =0.0;
                for (int time:minTimes){
                	stdev +=(time - average)*(time-average);
                }
                stdev= stdev/minTimes.size();
                stdev = Math.sqrt(stdev);
                sl.setAverageTime(String.format("%.1f",average));
                sl.setStDev(String.format("%.1f",stdev));
                if (ids2.size()==1){
					mgui.addLatencyVals(Integer.valueOf(ids2.get(0)), new String[]{sl.getTransaction1(), Integer.toString(Collections.max(minTimes))});
				} 
            }
        }
        calculateCorrespondingTimes();
        if (latm!=null && latencies.size()>0){
            latm.setData(latencies);
        }
    }

    private void updateTaskCommands() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        sendCommand("get-command-of-task all");

        /*for(TMLTask task: tmap.getTMLModeling().getTasks()) {
          sendCommand("get-command-of-task " + task.getID());
          }*/
    }

    private void updateExecutedCommands() {
        if (tmap == null) {
            return;
        }

        if (mode != STARTED_AND_CONNECTED) {
            return;
        }

        sendCommand("get-executed-operators");
    }

    private void updateRunningCommand(String id, String command, String progression, String startTime, String finishTime, String nextCommand, String transStartTime, String transFinishTime, String _state) {
        Integer i = getInteger(id);
        Integer c = getInteger(command);
        Integer nc = getInteger(nextCommand);

        if (_state == null) {
            _state = tasktm.getState(valueTable.get(id));
        }

        //TraceManager.addDev("state:" + _state);

        if ((i != null) && (c != null)) {
            try {
                //
                Integer old = runningTable.get(i);
                if (old != null) {
                    mgui.removeRunningId(old);
                    runningTable.remove(old);
                }

                runningTable.put(i, c);
                //
                mgui.addRunningID(c, nc, progression, startTime, finishTime, transStartTime, transFinishTime, _state);
            } catch (Exception e) {
                TraceManager.addDev("Exception updateRunningCommand: " + e.getMessage());
            }
        }

    }

    private void updateOpenDiagram(String name, String _command, String _progression, String _startTime, String _finishTime, String _transStartTime, String _transFinishTime) {
        //
        if (tmap == null) {
            return;
        }

        String command = _command;
        if (_progression != null) {
            command += _progression;
        }
        if (_startTime != null) {
            command += _startTime;
        }
        if (_finishTime != null) {
            command += _finishTime;
        }
        if (_transStartTime != null) {
            command += _transStartTime;
        }
        if (_transFinishTime != null) {
            command += _transFinishTime;
        }

        String cmd = diagramTable.get(name);
        if (cmd == null) {
            diagramTable.put(name, command);
            //return;
        }

        if ((cmd == null) || (!(cmd.equals(command)))) {
            diagramTable.remove(name);
            diagramTable.put(name, command);

            String diag = "";
            String tab = name;
            int index = tab.indexOf("__");
            if (index != -1) {
                diag = tab.substring(0, index);
                tab = tab.substring(index+2, tab.length());
            }
            //

            mgui.openTMLTaskActivityDiagram(diag, tab);
        }
    }

    private void printCPUs() {
        if (latex.isSelected()) {
            String name;
            String tmp, tmp1;
            int index, index1;
            jta.append("\\begin{tabular}{|l|c|c|}\n");
            jta.append("\\hline\n");
            jta.append("\\texbf{CPU} & \\textbf{Load} & \\textbf{Contention delay}\n");
            jta.append("\\hline\n");
            for(int i=0; i<cputm.getRowCount(); i++) {
                name = (String)(cputm.getValueAt(i, 0));
                tmp = (String)(cputm.getValueAt(i, 2));
                jta.append(Conversion.toLatex(name) + " &");
                index = tmp.indexOf(';');
                if (index == -1) {
                    jta.append(" - & - \\\\\n");
                } else {


                    tmp1 = tmp.substring(0, index);
                    index1 = tmp1.indexOf(':');
                    if (index1 != -1) {
                        tmp1 = tmp1.substring(index1 + 2, tmp1.length());
                    }
                    jta.append("" + tmp1 + " &");
                    tmp1 = tmp.substring(index+1, tmp.length());
                    index1 = tmp1.indexOf(':');
                    if (index1 != -1) {
                        tmp1 = tmp1.substring(index1 + 2, tmp1.length());
                    }
                    jta.append("" + tmp1 + "\\\\\n");
                }
            }
            jta.append("\\hline\n");
        } else {
            String name;
            String tmp;//, tmp1;
            int index;//, index1;
            jta.append("\nCPUs:\n");
            for(int i=0; i<cputm.getRowCount(); i++) {
                name = (String)(cputm.getValueAt(i, 0));
                tmp = (String)(cputm.getValueAt(i, 2));
                jta.append("* " + name + "\n");
                index = tmp.indexOf(';');
                if (index == -1) {
                    jta.append("\t - \n");
                } else {
                    jta.append("\t" + tmp.substring(0, index) + "\n");
                    jta.append("\t" + tmp.substring(index+1, tmp.length()) + "\n");
                }
            }
        }
    }

    private void printBuses() {
        if (latex.isSelected()) {
            String name;
            String tmp, tmp1;
            int index;//, index1;
            jta.append("\\begin{tabular}{|l|c|c|}\n");
            jta.append("\\hline\n");
            jta.append("\\texbf{CPU} & \\textbf{Load} & \\textbf{Contention delay}\n");
            jta.append("\\hline\n");
            for(int i=0; i<bustm.getRowCount(); i++) {
                name = (String)(bustm.getValueAt(i, 0));
                tmp = (String)(bustm.getValueAt(i, 2));
                jta.append(Conversion.toLatex(name) + " &");
                index = tmp.indexOf(':');
                if (index == -1) {
                    jta.append(" - \\\\\n");
                } else {
                    tmp1 = tmp.substring(index+2, tmp.length());
                    jta.append("" + tmp1 + "\\\\\n");
                }
            }
            jta.append("\\hline\n");
        } else {
            String name;
            String tmp;
            jta.append("\nBuses:\n");
            for(int i=0; i<bustm.getRowCount(); i++) {
                name = (String)(bustm.getValueAt(i, 0));
                tmp = (String)(bustm.getValueAt(i, 2));
                jta.append("* " + name + "\n");
                jta.append("\t" + tmp + "\n");
            }
        }
    }

    private void updateVariableState(String _idvar, String _value) {
        Integer i = getInteger(_idvar);
        int row;

        if (i != null) {
            try {
                valueTable.remove(i);
                valueTable.put(i, _value);
                //
                row = (rowTable.get(i)).intValue();
                tvtm.fireTableCellUpdated(row, 4);
            } catch (Exception e) {
                TraceManager.addDev("Exception updateVariableState: " + e.getMessage() + " idvar=" + _idvar + " val=" + _value);
            }
        }

    }

    private void updateTaskCyclesAndState(String _id, String _extime, String _state) {
        Integer i = getInteger(_id);
        Integer ex = getInteger(_extime);
        int row;
        String s = "";
        if (_state != null) {
            s += _state;
        }
        s += ";";
        if (_extime != null) {
            s+= _extime;
        }

        if ((i != null) && (ex != null)) {
            try {
                valueTable.remove(i);
                valueTable.put(i, s);
                //
                row = rowTable.get(i).intValue();
                if (_state != null) {
                    tasktm.fireTableCellUpdated(row, 2);
                }
                if (_extime != null) {
                    tasktm.fireTableCellUpdated(row, 3);
                }

                Integer c = runningTable.get(i);
                if (c != null) {
                    mgui.addRunningIDTaskState(c, _state);
                }
            } catch (Exception e) {
                TraceManager.addDev("Exception updateTaskCyclesAndStates: " + e.getMessage());
            }
        }
    }

    private void updateCPUState(String _id, String _utilization, String contdel, String busName, String busID) {
        Integer i = getInteger(_id);
        int row;
        String info;

        if (i != null) {
            try {
                valueTable.remove(i);
                info = "Utilization: " + _utilization;
//                if (_usedEnergy != null) {
//                    info += "; used energy: " +  _usedEnergy;
//                }
                if ((contdel != null) && (busName != null) && (busID != null)) {
                    info += "; Cont. delay on " + busName + " (" + busID + "): " + contdel;
                }
                valueTable.put(i, info);
                //
                row = (rowTable.get(i)).intValue();
                cputm.fireTableCellUpdated(row, 2);
//                if (_usedEnergy == null) {
//                    mgui.addLoadInfo(i, getDouble(_utilization).doubleValue(), -1);
//                } else {
//                    mgui.addLoadInfo(i, getDouble(_utilization).doubleValue(), getLong(_usedEnergy).longValue());
//                }
            } catch (Exception e) {
                TraceManager.addDev("Exception updateCPUState: " + e.getMessage() + " id=" + _id + " util=" + _utilization);
            }
        }
    }

    /*private void updateTableOfTransactions(int index) {
      ttm.fireTableRowUpdated(index);
      }*/

    private void updateBusState(String _id, String _utilization) {
        Integer i = getInteger(_id);
        int row;

        if (i != null) {
            try {
                valueTable.remove(i);
                valueTable.put(i, "Utilization: " + _utilization);
                //TraceManager.addDev("Searching for old row");
                row = rowTable.get(i).intValue();
                bustm.fireTableCellUpdated(row, 2);
                mgui.addLoadInfo(i, getDouble(_utilization).doubleValue(), -1);
            } catch (Exception e) {
                System.err.println("Exception updateBusState: " + e.getMessage());
            }
        }
    }

    private void updateCommandExecutionState(String _id, String _nbOfExec) {
        Integer id = getInteger(_id);
        Integer nbOfExec = getInteger(_nbOfExec);

        //TraceManager.addDev("Updating execution of command " + _id + " to " + _nbOfExec);

        if (tmap != null) {
            TMLElement tmle = tmap.getTMLModeling().getCorrespondance(id);
            if (tmle != null) {
                Object o = tmle.getReferenceObject();
                if ((o != null) && (o instanceof TGComponent)) {
                    //TraceManager.addDev("Setting met DIPLO = " + o);
                    ((TGComponent)o).setDIPLOMet(nbOfExec);
                }
            }
        }

        //tmap.getElementByID();
    }

    public void askForUpdate() {
        sendCommand("time");
        if (hashOK) {
            if (animate.isSelected()) {
                updateTaskCommands();
                updateExecutedCommands();
            }
            if (update.isSelected()) {
                updateTasks();
                updateVariables();
                updateCPUs();
                updateBus();
                trans = null;
                updateTransactions();
            }
        }
    }

    private void analyzeRG() {
    	mgui.statAUTDiplodocus();
        //mgui.statAUTDiplodocus();
    }

    private void viewRG() {
        mgui.showRGDiplodocus();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == animate) {
            ModelParameters.setValueForID("ANIMATE_INTERACTIVE_SIMULATION", "" + animate.isSelected());
            mgui.setDiploAnimate(animate.isSelected());
            diploids.setEnabled(animate.isSelected());
            animateWithInfo.setEnabled(animate.isSelected());
            openDiagram.setEnabled(animate.isSelected());
        } else if (e.getSource() == diploids) {
            mgui.setDiploIDs(diploids.isSelected());
        } else if (e.getSource() == animateWithInfo) {
            mgui.setTransationProgression(animateWithInfo.isSelected());
            ModelParameters.setValueForID("ANIMATE_WITH_INFO_DIPLO_SIM", "" + animateWithInfo.isSelected());
        } else if (e.getSource() == update) {
            ModelParameters.setValueForID("UPDATE_INFORMATION_DIPLO_SIM", "" + update.isSelected());
        } else if (e.getSource() == debug) {
            ModelParameters.setValueForID("OPEN_DIAG_DIPLO_SIM", "" + debug.isSelected());
        }
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        //if (!source.getValueIsAdjusting()) {
        int val = source.getValue();
        if (source == minimalCommandCoverage) {
            labelMinimalCommandCoverage.setText("" + val+ "%");
        } else {
            labelMinimalBranchCoverage.setText("" + val+ "%");
        }
        //}
    }

    @Override
    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //TraceManager.addDev("Command:" + command);

        if (command.equals(actions[InteractiveSimulationActions.ACT_STOP_ALL].getActionCommand()))  {
            close();
        }  else if (command.equals(actions[InteractiveSimulationActions.ACT_START_ALL].getActionCommand()))  {
            setComponents();
            startSimulation();
            //TraceManager.addDev("Start simulation!");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand()))  {
            mgui.resetTransactions();
            if (tmlSimPanelTimeline != null) {
                tmlSimPanelTimeline.setContentPaneEnable(false);
            }
            killSimulator();
            close();
            return;
            //TraceManager.addDev("Start simulation!");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_SIMU].getActionCommand()))  {
            sendCommand("run-to-next-breakpoint");
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].getActionCommand()))  {
            sendCommandWithMaxTrans("run-to-next-breakpoint-max-trans");
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_X_TIME_UNITS].getActionCommand()))  {
            sendCommandWithPositiveInt("run-x-time-units");
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_TO_TIME].getActionCommand()))  {
            sendCommandWithPositiveInt("run-to-time");
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_X_TRANSACTIONS].getActionCommand()))  {
            sendCommandWithPositiveInt("run-x-transactions");
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_X_COMMANDS].getActionCommand()))  {
            sendCommandWithPositiveInt("run-x-commands");
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_EXPLORATION].getActionCommand()))  {
            runExploration();
            //sendCommand("run-exploration");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_TO_NEXT_BUS_TRANSFER].getActionCommand()))  {
            toNextBusTransfer();
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CPU_EXECUTES].getActionCommand()))  {
            runUntilCPUExecutes();
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_MEMORY_ACCESS].getActionCommand()))  {
            toNextMemoryTransfer();
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_TASK_EXECUTES].getActionCommand()))  {
            runUntilTaskExecutes();
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CHANNEL_ACCESS].getActionCommand()))  {
            runUntilChannelAccess();
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_VCD].getActionCommand()))  {
            saveTraceVCD();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_HTML].getActionCommand()))  {
            saveTraceHTML();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_TXT].getActionCommand()))  {
            saveTraceText();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_XML].getActionCommand())) {
        	saveTraceXml();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_STATE].getActionCommand()))  {
            sendSaveStateCommand();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RESTORE_STATE].getActionCommand()))  {
            sendRestoreStateCommand();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_PRINT_BENCHMARK].getActionCommand()))  {
            sendCommand("get-benchmark 0");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_BENCHMARK].getActionCommand()))  {
            sendSaveBenchmarkCommand();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RESET_SIMU].getActionCommand())) {
            mgui.resetRunningID();
            mgui.resetLoadID();
            mgui.resetTransactions();
            mgui.resetStatus();
            resetSimTrace();
            sendCommand("reset");
            transTimes=new HashMap<String, List<String>>();
            processLatency();
            askForUpdate();
            updateTimelineTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].getActionCommand())) {
            sendCommand("rmat 1");

            if (taskTransactionPanel != null) {
                taskTransactionPanel.resetTable();
            }

            if (transactionPanel != null) {
                transactionPanel.resetTable();
            }

            mgui.resetTransactions();
            updateTransactions();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_STOP_SIMU].getActionCommand())) {
            sendCommand("stop");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_VARIABLES].getActionCommand())) {
            updateVariables();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_CPUS].getActionCommand())) {
            updateCPUs();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_MEMS].getActionCommand())) {
            updateMemories();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_BUS].getActionCommand())) {
            updateBus();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_TASKS].getActionCommand())) {
            updateTasks();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_TRANSACTIONS].getActionCommand())) {
            updateTransactions();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_ADD_LATENCY].getActionCommand())) {
            addLatency();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_UPDATE_LATENCY].getActionCommand())) {
            updateLatency();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_PRINT_CPUS].getActionCommand())) {
            printCPUs();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_PRINT_BUS].getActionCommand())) {
            printBuses();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_ANALYSIS_RG].getActionCommand())) {
            analyzeRG();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_VIEW_RG].getActionCommand())) {
            viewRG();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_REFRESH].getActionCommand())) {
            resetSimTrace();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SHOW_TRACE].getActionCommand())) {
			writeSimTrace();
		} else if (command.equals(actions[InteractiveSimulationActions.ACT_SHOW_TRACE_TIMELINE].getActionCommand())) {
            writeSimTraceTimeline();
        }
    }

    private String formatString(String input) {
        StringBuffer sb = new StringBuffer(input);
        int ptr = 3;
        int inc = 4;

        while(sb.length() > ptr) {
            sb.insert(sb.length()-ptr, " ");
            ptr = ptr + inc;
        }

        return sb.toString();
    }

    public void error(String error) {
        jta.append("error: " + error + "\n");
    }

    public boolean isAPositiveInt(String s) {
        int val;
        try {
            val = Integer.decode(s).intValue();
        } catch (Exception e) {
            return false;
        }
        return val > -1;
    }

    public Integer getInteger(String s) {
        try {
            return Integer.decode(s);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLong(String s) {
        try {
            return Long.decode(s);
        } catch (Exception e) {
            return null;
        }
    }

    public Double getDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    public void toNextBusTransfer() {
        int id = getIDFromString(busIDs[busses.getSelectedIndex()]);
        if (id != -1) {
            sendCommand("run-to-next-transfer-on-bus " + id);
        }
    }

    public void runUntilCPUExecutes() {
        int id = getIDFromString(cpuIDs[cpus.getSelectedIndex()]);
        if (id != -1) {
            sendCommand("run-until-cpu-executes " + id);
        }
    }

    public void toNextMemoryTransfer() {
        int id = getIDFromString(memIDs[mems.getSelectedIndex()]);
        if (id != -1) {
            sendCommand("run-until-memory-access " + id);
        }
    }

    public void runUntilTaskExecutes() {
        int id = getIDFromString(taskIDs[tasks.getSelectedIndex()]);
        if (id != -1) {
            sendCommand("run-until-task-executes " + id);
        }
    }

    public void runUntilChannelAccess() {
        int id = getIDFromString(chanIDs[chans.getSelectedIndex()]);
        if (id != -1) {
            sendCommand("run-until-channel-access " + id);
        }
    }



    public int getIDFromString(String s) {
        int index0 = s.indexOf("(");
        int index1 = s.indexOf(")");
        if ((index0 < 0) || (index1 <0) || (index1 < index0)) {
            return -1;
        }

        String in = s.substring(index0+1, index1);

        try {
            return Integer.decode(in).intValue();
        } catch (Exception e) {
            System.err.println("Wrong string: "+ in);
        }

        return -1;
    }

    public boolean breakpointAfterSelectEvent(int _commandID) {
        if (tmap != null) {
            TMLTask task = tmap.getTMLTaskByCommandID(_commandID);
            if (task != null) {
                TMLActivityElement tmlae = task.getElementByID(_commandID);
                if (tmlae instanceof TMLWaitEvent) {
                    if (task.getActivityDiagram().getPrevious(tmlae) instanceof TMLSelectEvt) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addBreakPoint(int _commandID) {
        //TraceManager.addDev("Add breakpoint: " + _commandID);
        // Check whether that breakpoint is already listed or not
        for(Point p: points) {
            if (p.y == _commandID) {
                return;
            }
        }

        if (breakpointAfterSelectEvent(_commandID)) {
            jta.append("Breakpoint on sendEvent " + _commandID + " cannot be taken into account because it is placed after a selectEvent. \n " +
                    "Instead, you can put this breakpoing on the selectEvent operator");
            return;
        }
        // Check if valid breakpoint: cannot be put on receive events after selectevt



        if (tmap != null) {
            TMLTask task = tmap.getTMLTaskByCommandID(_commandID);
            //TraceManager.addDev("Got task: " + task);
            if (task != null) {
                //TraceManager.addDev("Adding bkp");
                sendCommand("add-breakpoint " + task.getID() + " " + _commandID + "\n");
                jpbp.addExternalBreakpoint(task.getID(), _commandID);
            }
        }
    }

    public void removeBreakPoint(int _commandID) {
        TraceManager.addDev("remove breakpoint");
        int cpt = 0;
        for(Point p: points) {
            if (p.y == _commandID) {
                sendCommand("rm-breakpoint " + p.x +  " " + p.y + "\n");
                jpbp.removeExternalBreakpoint(cpt);
                return;
            }
            cpt ++;
        }
    }

    public void sendBreakPointList() {
        for(Point p: points) {
            if (breakpointAfterSelectEvent(p.y)) {
                jta.append("Breakpoint on sendEvent " + p.y + " cannot be taken into account because it is placed after a selectEvent. \n " +
                        "Instead, you can put this breakpoing on the selectEvent operator");
            } else {
                sendCommand("add-breakpoint " + p.x + " " + p.y + "\n");
            }
        }
        sendCommand("active-breakpoints 1");
    }

    public void removeBreakpoint(Point p) {
        if (mode == STARTED_AND_CONNECTED) {
            sendCommand("rm-breakpoint " + p.x +  " " + p.y + "\n");
        }
        if (animate.isSelected()) {
            mgui.removeBreakpoint(p);
        }
    }

    public void addBreakpoint(Point p) {
        if (mode == STARTED_AND_CONNECTED) {
            sendCommand("add-breakpoint " + p.x +  " " + p.y + "\n");
        }
        if (animate.isSelected()) {
            mgui.addBreakpoint(p);
        }
    }

    public void printMessage(String msg) {
        jta.append("*** " + msg + " ***\n");
    }

    public String[] makeCPUIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getCPUandHwAIDs();
    }

    public String[] makeBusIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getBusIDs();
    }

    public String[] makeMemIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getMemIDs();
    }

    public String[] makeTasksIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getTasksIDs();
    }

    public String[] makeChanIDs() {
        if (tmap == null) {
            return null;
        }

        return tmap.getChanIDs();
    }

    public String[] makeCommandIDs(int index) {
        if (tmap == null) {
            return null;
        }

        return tmap.makeCommandIDs(index);
    }

    public String[] makeVariableIDs(int index) {
        if (tmap == null) {
            return null;
        }

        return tmap.makeVariableIDs(index);
    }

    public void fillCheckedTrans(){
        if (tmap==null){
            return;
        }
        for (TGComponent tgc: tmap.getTMLModeling().getCheckedComps().keySet()){
     		String compName = tmap.getTMLModeling().getCheckedComps().get(tgc);
            TraceManager.addDev(compName+" (ID: " + tgc.getDIPLOID() + ")");
			checkedTransactions.add(compName+" (ID: " + tgc.getDIPLOID() + ")");
			if (!nameIdMap.containsKey(compName)){
				nameIdMap.put(compName,new ArrayList<String>());
				checkedTransactions.add(compName + " (all instances)");
			}
			nameIdMap.get(compName).add(Integer.toString(tgc.getDIPLOID()));
			nameIdMap.put(compName+" (ID: " + tgc.getDIPLOID() + ")",new ArrayList<String>());
			nameIdMap.get(compName + " (ID: " + tgc.getDIPLOID() + ")").add(Integer.toString(tgc.getDIPLOID()));
            checkTable.put(Integer.toString(tgc.getDIPLOID()),compName+" (ID: " + tgc.getDIPLOID() + ")");
        }
	
    }
    
    public void activeBreakPoint(boolean active) {
        if (mode == STARTED_AND_CONNECTED) {
            if (active) {
                sendCommand("active-breakpoints 1");
            } else {
                sendCommand("active-breakpoints 0");
            }
        }
    }

    public void setVariables(int _idTask, int _idVariable, String _value) {
        sendCommand("set-variable " + _idTask + " " + _idVariable + " " + _value);
        sendCommand("get-variable-of-task " + _idTask + " " + _idVariable);
    }

    public Vector<SimulationTransaction> getListOfRecentTransactions() {
        return trans;
    }
} // Class
