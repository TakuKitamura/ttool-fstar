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


package ui.avatarinteractivesimulation;

import avatartranslator.*;
import avatartranslator.directsimulation.*;
import common.ConfigurationTTool;
import myutil.*;
import myutilsvg.SVGGeneration;
import ui.*;
import ui.avatarbd.AvatarBDPortConnector;
import ui.interactivesimulation.LatencyTableModel;
import ui.interactivesimulation.SimulationLatency;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Class JFrameAvatarInteractiveSimulation
 * Creation: 21/01/2011
 * version 1.0 21/01/2011
 *
 * @author Ludovic APVRILLE
 */
public class JFrameAvatarInteractiveSimulation extends JFrame implements AvatarSimulationInteraction, ActionListener, Runnable, MouseListener, ItemListener, ListSelectionListener, WindowListener/*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
    private static int TRACED_TRANSACTIONS = 1000;
    private static int LAST_TRANSACTIONS = 0;

    public static SimulationTrace SELECTED_SIMULATION_TRACE;


    //    private static String buttonStartS = "Start simulator";
    //    private static String buttonStopAndCloseS = "Stop simulator and close";
    //
    //    private static int NOT_STARTED = 0;
    //    private static int STARTED = 1;

    private static long SPACE_UPDATE_TIME = 100;

    // private Frame f;
    private MainGUI mgui;
    //   private String title;

    protected JButton buttonClose, buttonStart, buttonStopAndClose;
    protected JTextArea jta;
    protected JScrollPane jsp;

    protected Thread t;
    protected int threadMode = 0;
    protected boolean go;

    // Control command
    protected JButton resetCommand, runCommand, StopCommand;
    protected AvatarMainCommandsToolBar mctb;
    protected AvatarSaveCommandsToolBar sctb;
    //protected StateCommandsToolBar stctb;
    //protected BenchmarkCommandsToolBar bctb;


    // Commands
    JPanel main, mainTop, commands, save, state, infos;
    //outputs, cpuPanel; // from MGUI
    JLabel nameOfTrace;
    JCheckBox latex, debug, animate, diploids, hidden, update, openDiagram, animateWithInfo, executeEmptyTransition, executeStateEntering, traceInSD;
    JTabbedPane commandTab, infoTab;
    protected JTextField displayedTransactionsText, lastTransactionsText;
    protected JTextField paramMainCommand;
    protected JTextField saveFileName;
    protected JTextField stateFileName;
    protected JTextField benchmarkFileName;
    //protected JComboBox cpus, busses, mems, tasks, chans;

    //List of transactions

    private JList<AvatarSimulationPendingTransaction> listPendingTransactions;
    private TGComponent selectedComponentForTransaction1, selectedComponentForTransaction2;
    private AvatarSimulationBlock previousBlock;

    private int invokedLater = 0;

    private int totalNbOfElements = -1;
    private HashSet<TGComponent> mettableElements;

    // Edition of variable value
    protected JMenuItem edit;
    protected int selectedRow;


    //private String[] cpuIDs, busIDs, memIDs, taskIDs, chanIDs;

    // Status elements
    JLabel statuss, status, time, info, coverage;

    // Task elements
    //TaskVariableTableModel tvtm;
    //JButton updateTaskVariableInformationButton;
    //private JScrollPane jspTaskVariableInfo;

    // Breakpoints
    //JPanelBreakPoints jpbp;

    // Set variables
    //JPanelSetVariables jpsv;

    // Blocks
    private JPanel blockPanel;
    private BlockTableModel blocktm;
    private JScrollPane jspBlockInfo;

    // Variables
    private JPanel variablePanel;
    private VariableTableModel variabletm;
    private JScrollPane jspVariableInfo;

    // Transactions
    private JPanel transactionPanel;
    private TransactionTableModel transactiontm;
    private JScrollPane jspTransactionInfo;

    // Met elements
    private JPanel metElementsPanel;
    private MetElementsTableModel metelementstm;
    private JScrollPane jspMetElementsInfo;


    // DisplayedBlocks
    private JPanel displayedBlockPanel;
    private JScrollPane jspDisplayedBlocks;
    private Vector<JCheckBox> displayedBlocks;

    // RandomBlocks
    private JPanel randomPanel;
    private JTextField randomValue;
    private JCheckBox imposeRandom;
    private JButton updateRandom;

    // Asynchronous transactions
    private JPanel asyncPanel;
    private JComboBox<AvatarInteractiveSimulationFIFOData> comboFIFOs;
    private Vector<AvatarInteractiveSimulationFIFOData> fifos;
    private JButton delete, up, down;
    private JList<AvatarSimulationAsynchronousTransaction> asyncmsgs;
    private int nbOfAsyncMsgs;

    // Sequence Diagram
    private AvatarSpecificationSimulationSDPanel sdpanel;

    //JButton updateBlockInformationButton;


    private int busyMode = 0; // Mode of AvatarSpecificationSimulation

    // For managing actions
    public AvatarInteractiveSimulationActions[] actions;
    public MouseHandler mouseHandler;
    public KeyListener keyHandler;


    /*private Hashtable <Integer, String> valueTable;
      private Hashtable <Integer, Integer> rowTable;

      private Hashtable <Integer, Integer> runningTable;
      private Hashtable <String, String> diagramTable;*/


    // new
    private AvatarSpecification avspec;
    private AvatarSpecificationSimulation ass;
    private Thread simulationThread;
    //    private boolean resetThread;
    //    private boolean killThread;

    private LinkedList<TGComponent> runningTGComponents;
    private int nbOfAllExecutedElements = 0;
    private double coverageVal = 0;

    private long previousTime;

    private boolean simulationRunning;

    // Async messages
    private Vector<AvatarSimulationAsynchronousTransaction> lastAsyncmsgs;

    //Latency
    private JPanel latencyPanel;
    private JComboBox<String> transaction1;
    private JComboBox<String> transaction2;
    private JButton addLatencyCheckButton;
    private JButton updateLatencyButton;
    private LatencyTableModel latm;
    //    public Vector<String> checkedTransactions = new Vector<String>();
    private Vector<SimulationLatency> latencies = new Vector<SimulationLatency>();
    //List<String> toCheck = new ArrayList<String>();
    private Map<String, SimulationLatency> nameLatencyMap = new HashMap<String, SimulationLatency>();
    private Map<String, List<String>> transTimes = new HashMap<String, List<String>>(); //Map of each checked element: all transaction times
    private JScrollPane jspLatency;

    // Trace playing
    private SimulationTrace selectedTrace;
    private CSVObject traceObject;


    public JFrameAvatarInteractiveSimulation(/*Frame _f,*/ MainGUI _mgui, String _title, AvatarSpecification _avspec) {
        super(_title);

        //   f = _f;
        mgui = _mgui;
        //  title = _title;
        avspec = _avspec;

        addWindowListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(IconManager.img5100);
        setBackground(Color.WHITE);

        try {
            setBackground(new Color(50, 40, 40, 200));
        } catch (Exception e) {
            setBackground(new Color(50, 40, 40));
        }
        initActions();

        initSimulation();
        for (String id : _avspec.checkedIDs) {
            // checkedTransactions.add(id);
            transTimes.put(id, new ArrayList<String>());
        }
        for (AvatarPragmaLatency latencyPragma : _avspec.getLatencyPragmas()) {
            for (String id1 : latencyPragma.getId1()) {
                for (String id2 : latencyPragma.getId2()) {
                    if (!nameLatencyMap.containsKey(id1 + "--" + id2)) {
                        SimulationLatency sl = new SimulationLatency();
                        sl.setTransaction1(id1);
                        sl.setTransaction2(id2);
                        sl.addPragma(latencyPragma);
                        nameLatencyMap.put(id1 + "--" + id2, sl);
                        latencies.add(sl);
                        //toCheck.add(latencyPragma.getId1().get(0) + "--"+latencyPragma.getId2().get(0));
                        updateTransactionsTable();
                    } else {
                        nameLatencyMap.get(id1 + "--" + id2).addPragma(latencyPragma);
                    }
                }
            }
        }
        makeComponents();
        setComponents();
        TraceManager.addDev("Components done");

    }

    private void initSimulation() {
        TraceManager.addDev("Init simulation");
        runningTGComponents = new LinkedList<TGComponent>();
        nbOfAllExecutedElements = 0;
        resetMetElements();
        ass = new AvatarSpecificationSimulation(avspec, this);


        //ass.initialize();
        simulationRunning = false;
        simulationThread = new Thread(this);
        simulationThread.start();

        TraceManager.addDev("Init simulation done");
    }

    public synchronized void setSimulationRunning() {
        simulationRunning = true;
    }

    public synchronized void stopSimulationRunning() {
        simulationRunning = false;
    }


    public synchronized void updatePending() {
        invokedLater = 0;
        //TraceManager.addDev("Simulation is already running -> beg of code:" + Thread.currentThread());
        if (ass == null) {
            return;
        }

        try {

            Vector<AvatarSimulationPendingTransaction> ll = new Vector<>(ass.getPendingTransitions());

            listPendingTransactions.clearSelection();
            selectedComponentForTransaction1 = null;
            selectedComponentForTransaction2 = null;
            if (ll != null) {
                listPendingTransactions.setListData(ll);

                //int random = (int)(Math.floor((Math.random()*ll.size())));
                //listPendingTransactions.setSelectedIndex(random);

                // Use probabilities
                double sumProb = 0.0;
                for (AvatarSimulationPendingTransaction pt : ll) {
                    //TraceManager.addDev("prob=" + pt.probability);
                    sumProb += pt.probability;
                }


                double rand2 = Math.random() * sumProb;
                //TraceManager.addDev("Nb of pending:" + ll.size() + " total prob=" + sumProb +  " rand=" + rand2);


                double prob = 0.0;
                int index = 0;
                for (AvatarSimulationPendingTransaction pt : ll) {
                    prob += pt.probability;
                    //TraceManager.addDev("rand=" + rand2 + " prob=" + prob + " pt.probability=" + pt.probability);
                    if (rand2 < prob) {
                        listPendingTransactions.setSelectedIndex(index);
                        break;
                    }
                    index++;
                }


            } else {
                listPendingTransactions.setListData(new Vector<AvatarSimulationPendingTransaction>());
            }
        } catch (Exception e) {
        }

        //TraceManager.addDev("Simulation is already running -> end of code:" + Thread.currentThread());
    }

    public void run() {
        if (simulationRunning == true) {
            updatePending();

        } else {
            setSimulationRunning();
            previousTime = System.currentTimeMillis();
            ass.runSimulation();
            TraceManager.addDev("Simulation thread ended");
            stopSimulationRunning();
        }
    }


    private JLabel createStatusBar() {
        statuss = new JLabel("Ready...");
        statuss.setForeground(ColorManager.InteractiveSimulationText);
        statuss.setBorder(BorderFactory.createEtchedBorder());
        return statuss;
    }

    public void makeComponents() {
        JPanel jp01, jp02;
        //jp01.setPreferredSize(new Dimension(375, 400));
        GridBagLayout gridbag01;
        GridBagConstraints c01;

        //cp = new CommandParser();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        //framePanel.setBackground(ColorManager.InteractiveSimulationBackground);
        //framePanel.setForeground(new Color(255, 166, 38));

        //
        //buttonStart = new JButton(actions[InteractiveSimulationActions.ACT_RUN_SIMU]);
        buttonStopAndClose = new JButton(actions[AvatarInteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL]);
        //buttonStopAndClose = new JButton(buttonStopAndCloseS, IconManager.imgic27);


        // statusBar
        statuss = createStatusBar();
        framePanel.add(statuss, BorderLayout.SOUTH);

        // Mouse handler
        mouseHandler = new MouseHandler(statuss);

        JPanel mainpanel = new JPanel(new BorderLayout());
        //mainpanel.setBackground(ColorManager.InteractiveSimulationBackground);
        framePanel.add(mainpanel, BorderLayout.CENTER);

        JPanel jp = new JPanel();
        //jp.setBackground(ColorManager.InteractiveSimulationBackground);
        //jp.setPreferredSize(new Dimension(800, 75));
        //jp.add(buttonStart);
        jp.add(buttonStopAndClose);
        mainpanel.add(jp, BorderLayout.NORTH);


        //GridBagLayout gridbag02 = new GridBagLayout();
        //GridBagConstraints c02 = new GridBagConstraints();
        //mainTop = new JPanel(gridbag02);
        //mainTop.setPreferredSize(new Dimension(800, 375));
        //c02.gridheight = 1;
        //c02.weighty = 1.0;
        //c02.weightx = 1.0;
        //c02.gridwidth = 1;
        //c02.fill = GridBagConstraints.BOTH;
        //c02.gridheight = 1;

        // Ouput SD
        /*jta = new ScrolledJTextArea();
          jta.setBackground(ColorManager.InteractiveSimulationJTABackground);
          jta.setForeground(ColorManager.InteractiveSimulationJTAForeground);
          jta.setMinimumSize(new Dimension(800, 200));
          jta.setRows(15);
          //jta.setMaximumSize(new Dimension(800, 500));
          jta.setEditable(false);
          jta.setMargin(new Insets(10, 10, 10, 10));
          jta.setTabSize(3);
          jta.append("Interactive simulation ready to run\n");
          Font f = new Font("Courrier", Font.BOLD, 12);
          jta.setFont(f);
          jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
          jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));

          //jsp.setColumnHeaderView(100);
          //jsp.setRowHeaderView(30);
          //jsp.setMaximumSize(new Dimension(800, 400));*/
        JPanel lowerPartPanel = new JPanel();
        lowerPartPanel.setLayout(new BorderLayout());
        sdpanel = new AvatarSpecificationSimulationSDPanel(ass);
        sdpanel.setShowHiddenStates(false);
        //ass.setName("Interaction Overview Diagram");
        JScrollPane jsp = new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sdpanel.setMyScrollPanel(jsp);
        jsp.setWheelScrollingEnabled(true);
        //jsp.setPreferredSize(new Dimension(800, 400));
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
        lowerPartPanel.add(jsp, BorderLayout.CENTER);

        // Commands
        commands = new JPanel(new BorderLayout());
        commands.setBorder(new javax.swing.border.TitledBorder("Commands"));

        // Issue #41 Ordering of tabbed panes
        commandTab = GraphicLib.createTabbedPaneRegular();//new JTabbedPane();
        commands.add(commandTab, BorderLayout.CENTER);


        // Control commands
        jp01 = new JPanel(new BorderLayout());
        commandTab.addTab("Control", null, jp01, "Main control commands");

        mctb = new AvatarMainCommandsToolBar(this);
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

        jp02.add(new JLabel("Nb of steps: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        paramMainCommand = new JTextField("1", 30);
        jp02.add(paramMainCommand, c01);

        c01.gridwidth = 1;
        jp02.add(new JLabel("Selected trace: "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        nameOfTrace = new JLabel();
        setTraceName();
        jp02.add(nameOfTrace, c01);

        // list of pending transactions
        JPanel panellpt = new JPanel();
        panellpt.setLayout(new BorderLayout());
        panellpt.setBorder(new javax.swing.border.TitledBorder("Pending transactions"));

        listPendingTransactions = new JList<AvatarSimulationPendingTransaction>();
        //listPendingTransactions.setPreferredSize(new Dimension(400, 300));
        listPendingTransactions.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listPendingTransactions.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listPendingTransactions);
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panellpt.add(scrollPane1);
        c01.gridheight = 10;
        jp02.add(panellpt, c01);
        jp01.add(jp02, BorderLayout.CENTER);


        // Save commands
        jp01 = new JPanel(new BorderLayout());

        commandTab.addTab("Save trace", null, jp01, "Save commands");

        sctb = new AvatarSaveCommandsToolBar(this);
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

        jp02.add(new JLabel("File name:"), c01);
        saveFileName = new JTextField(30);
        jp02.add(saveFileName, c01);

        jp01.add(jp02, BorderLayout.CENTER);

        AvatarSimulationStatisticsPanel assp = new AvatarSimulationStatisticsPanel(mgui, avspec, ass);
        commandTab.addTab("Statistics", null, assp, "Run x simulations and perform statistics");


        // Benchmark commands
        /*jp01 = new JPanel(new BorderLayout());

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

          jp01.add(jp02, BorderLayout.CENTER);*/


        //Info
        infos = new JPanel(new BorderLayout());
        infos.setPreferredSize(new Dimension(300, 200));
        //infos.setPreferredSize(new Dimension(400, 450));
        infos.setBorder(new javax.swing.border.TitledBorder("Simulation information"));


        // Main panels
        //mainTop.add(infos, c02);
        //mainTop.add(commands, c02);
        JSplitPane mainTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, commands, infos);
        mainTop.setResizeWeight(0.5);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTop, lowerPartPanel);
        split.setResizeWeight(0.5);
        mainpanel.add(split, BorderLayout.CENTER);

        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row

        // Issue #41 Ordering of tabbed panes
        infoTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        infoTab.setPreferredSize(new Dimension(300, 200));
        infos.add(infoTab, BorderLayout.CENTER);

        // Simulation time
        jp02 = new JPanel();
        infos.add(jp02, BorderLayout.NORTH);
        jp02.add(new JLabel("Status:"));
        status = new JLabel("Unknown");
        status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(status);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Time:"));
        time = new JLabel("0");
        time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(time);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Transactions:"));
        info = new JLabel("Unknown");
        jp02.add(info);
        jp02.add(new JLabel(" "));
        jp02.add(new JLabel("Coverage:"));
        coverage = new JLabel(coverageVal + " %");
        coverage.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
        jp02.add(coverage);

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

        //jp01.add(new JLabel(" "), c01);
        /*latex = new JCheckBox("Generate info in Latex format");
          jp01.add(latex, c01);*/
        /*debug = new JCheckBox("Print messages received from server");
          jp01.add(debug, c01);*/
        c01.gridwidth = 1;
        animate = new JCheckBox("Animate UML diagrams");
        jp01.add(animate, c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        diploids = new JCheckBox("Show AVATAR IDs on diagrams");
        jp01.add(diploids, c01);
        diploids.addItemListener(this);
        diploids.setSelected(false);
        c01.gridwidth = 1;
        hidden = new JCheckBox("Show hidden state in sequence diagram");
        jp01.add(hidden, c01);
        hidden.addItemListener(this);
        hidden.setSelected(false);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        animateWithInfo = new JCheckBox("Show transaction progression on UML diagrams");
        //jp01.add(animateWithInfo, c01);
        animateWithInfo.addItemListener(this);
        animateWithInfo.setSelected(true);
        openDiagram = new JCheckBox("Auto open active state machines");
        jp01.add(openDiagram, c01);
        openDiagram.setSelected(true);
        //update = new JCheckBox("Automatically update information (variables)");
        //jp01.add(update, c01);
        //update.addItemListener(this);
        //update.setSelected(true);

        animate.addItemListener(this);
        animate.setSelected(true);


        c01.gridwidth = 1;
        traceInSD = new JCheckBox("Trace in sequence diagram");
        jp01.add(traceInSD, c01);
        traceInSD.addItemListener(this);
        traceInSD.setSelected(true);
        jp01.add(new JLabel("# of transactions:"), c01);
        displayedTransactionsText = new JTextField("" + TRACED_TRANSACTIONS, 10);
        jp01.add(displayedTransactionsText, c01);
        //displayedTransactionsText.addActionListener(this);
        displayedTransactionsText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    int nb = Integer.parseInt(displayedTransactionsText.getText());
                    if ((nb > 0) && (nb <= 100000)) {
                        statuss.setText("Nb of traced transactions modified to: " + nb);
                        if (sdpanel != null) {
                            sdpanel.setNbOfDrawnTransactions(nb);
                            if (sdpanel.isVisible()) {
                                sdpanel.repaint();
                            }
                        }
                        return;
                    }
                } catch (Exception e) {
                }
                statuss.setText("Unknown / bad number: " + displayedTransactionsText.getText());
            }
        });
        jp01.add(new JLabel("Index of last trans.:"), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        lastTransactionsText = new JTextField("" + LAST_TRANSACTIONS, 10);
        jp01.add(lastTransactionsText, c01);
        //displayedTransactionsText.addActionListener(this);
        lastTransactionsText.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    int nb = Integer.parseInt(lastTransactionsText.getText());
                    if (nb > -1) {
                        statuss.setText("Index of last transation modified to: " + nb);
                        if (sdpanel != null) {
                            sdpanel.setLastDrawnTransactions(nb);
                            if (sdpanel.isVisible()) {
                                sdpanel.repaint();
                            }
                        }
                        return;
                    }
                } catch (Exception e) {
                }
                statuss.setText("Unknown / bad number: " + lastTransactionsText.getText());
            }
        });


        c01.gridwidth = 1;
        executeEmptyTransition = new JCheckBox("Auto execute empty transitions");
        jp01.add(executeEmptyTransition, c01);
        executeEmptyTransition.setSelected(true);
        executeEmptyTransition.addItemListener(this);
        ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());

        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        executeStateEntering = new JCheckBox("Auto enter states");
        jp01.add(executeStateEntering, c01);
        executeStateEntering.setSelected(true);
        executeStateEntering.addItemListener(this);
        ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());


        TableSorter sorterPI;
        JTable jtablePI;

        // Breakpoints
        /*jpbp = new JPanelBreakPoints(this, points);
          infoTab.addTab("Breakpoints", null, jpbp, "List of active breakpoints");*/

        // blocks
        blockPanel = new JPanel();
        blockPanel.setLayout(new BorderLayout());
        infoTab.addTab("Blocks", IconManager.imgic1202, blockPanel, "Current state of blocks");
        blocktm = new BlockTableModel(ass);

        sorterPI = new TableSorter(blocktm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(80);
        ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(150);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspBlockInfo = new JScrollPane(jtablePI);
        jspBlockInfo.setWheelScrollingEnabled(true);
        jspBlockInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspBlockInfo.setPreferredSize(new Dimension(250, 300));
        blockPanel.add(jspBlockInfo, BorderLayout.CENTER);

        // Variables
        variablePanel = new JPanel();
        variablePanel.setLayout(new BorderLayout());
        infoTab.addTab("Variables", IconManager.imgic1202, variablePanel, "Variables");
        variabletm = new VariableTableModel(ass);

        sorterPI = new TableSorter(variabletm);
        final JTable JTablejtablePIV = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        JTablejtablePIV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = JTablejtablePIV.rowAtPoint(e.getPoint());
                if (r >= 0 && r < JTablejtablePIV.getRowCount()) {
                    JTablejtablePIV.setRowSelectionInterval(r, r);
                } else {
                    JTablejtablePIV.clearSelection();
                }

                selectedRow = JTablejtablePIV.getSelectedRow();
                if (selectedRow < 0)
                    return;

                if (e.getComponent() instanceof JTable) {
                    TraceManager.addDev("Popup at x=" + e.getX() + " y=" + e.getY() + " row=" + selectedRow);
                    JPopupMenu popup = createVariablePopup();
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });


        ((JTablejtablePIV.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((JTablejtablePIV.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((JTablejtablePIV.getColumnModel()).getColumn(2)).setPreferredWidth(100);
        ((JTablejtablePIV.getColumnModel()).getColumn(3)).setPreferredWidth(100);
        JTablejtablePIV.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspVariableInfo = new JScrollPane(JTablejtablePIV);
        jspVariableInfo.setWheelScrollingEnabled(true);
        jspVariableInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspVariableInfo.setPreferredSize(new Dimension(250, 300));
        variablePanel.add(jspVariableInfo, BorderLayout.CENTER);
        //updateTaskInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_TASKS]);
        //taskPanel.add(updateTaskInformationButton, BorderLayout.SOUTH);

        // Transactions
        transactionPanel = new JPanel();
        transactionPanel.setLayout(new BorderLayout());
        infoTab.addTab("Transactions", IconManager.imgic1202, transactionPanel, "Transactions");
        transactiontm = new TransactionTableModel(ass);

        /*sorterPI = new TableSorter(transactiontm);
          jtablePI = new JTable(sorterPI);
          sorterPI.setTableHeader(jtablePI.getTableHeader());
          ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(50);
          ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
          ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
          ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
          ((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
          ((jtablePI.getColumnModel()).getColumn(5)).setPreferredWidth(75);
          ((jtablePI.getColumnModel()).getColumn(6)).setPreferredWidth(100);
          jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
          jspTransactionInfo = new JScrollPane(jtablePI);
          jspTransactionInfo.setWheelScrollingEnabled(true);
          jspTransactionInfo.getVerticalScrollBar().setUnitIncrement(10);
          jspTransactionInfo.setPreferredSize(new Dimension(250, 300));
          transactionPanel.add(jspTransactionInfo, BorderLayout.CENTER);*/

        jtablePI = new JTable(transactiontm);
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(50);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(5)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(6)).setPreferredWidth(75);
        ((jtablePI.getColumnModel()).getColumn(7)).setPreferredWidth(100);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspTransactionInfo = new JScrollPane(jtablePI);
        jspTransactionInfo.setWheelScrollingEnabled(true);
        jspTransactionInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspTransactionInfo.setPreferredSize(new Dimension(250, 300));
        transactionPanel.add(jspTransactionInfo, BorderLayout.CENTER);


        // Met elements
        metElementsPanel = new JPanel();
        metElementsPanel.setLayout(new BorderLayout());
        infoTab.addTab("Met states", IconManager.imgic1202, metElementsPanel, "Met states");
        metelementstm = new MetElementsTableModel(ass);

        sorterPI = new TableSorter(metelementstm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspMetElementsInfo = new JScrollPane(jtablePI);
        jspMetElementsInfo.setWheelScrollingEnabled(true);
        jspMetElementsInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspMetElementsInfo.setPreferredSize(new Dimension(250, 300));
        metElementsPanel.add(jspMetElementsInfo, BorderLayout.CENTER);


        // Displayed blocks
        displayedBlockPanel = new JPanel();
        displayedBlockPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridheight = 1;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;

        displayedBlocks = new Vector<JCheckBox>();
        for (AvatarSimulationBlock block : ass.getSimulationBlocks()) {
            JCheckBox jcb = new JCheckBox(block.getName(), true);
            block.selected = true;
            jcb.addActionListener(this);
            //TraceManager.addDev("Adding block: " + block);
            displayedBlocks.add(jcb);
            displayedBlockPanel.add(jcb, c);
        }
        ass.computeSelectedSimulationBlocks();

        jspDisplayedBlocks = new JScrollPane(displayedBlockPanel);
        jspDisplayedBlocks.setWheelScrollingEnabled(true);
        jspDisplayedBlocks.getVerticalScrollBar().setUnitIncrement(10);
        jspDisplayedBlocks.setPreferredSize(new Dimension(200, 300));
        infoTab.addTab("Displayed blocks", IconManager.imgic1202, jspDisplayedBlocks, "Displayed blocks");


        // Latencies
        latencyPanel = new JPanel();
        infoTab.addTab("Latencies", IconManager.imgic1202, new JScrollPane(latencyPanel), "Latencies");
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        latencyPanel.setLayout(gridbag0);
        c0.gridwidth = GridBagConstraints.REMAINDER;
        latencyPanel.add(new JLabel("Latencies shown in number of cycles w.r.t. the main clock"), c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        latencyPanel.add(new JLabel("Checkpoint 1:"), c0);
        c0.gridwidth = GridBagConstraints.REMAINDER;
        Vector<String> transactions = new Vector<String>(transTimes.keySet());
        transaction1 = new JComboBox<String>(transactions);
        latencyPanel.add(transaction1, c0);

        c0.gridwidth = 1;
        latencyPanel.add(new JLabel("Checkpoint 2:"), c0);
        c0.gridwidth = GridBagConstraints.REMAINDER;
        transaction2 = new JComboBox<String>(transactions);
        latencyPanel.add(transaction2, c0);


        addLatencyCheckButton = new JButton(actions[AvatarInteractiveSimulationActions.ACT_ADD_LATENCY]);
        latencyPanel.add(addLatencyCheckButton, c0);
        latm = new LatencyTableModel();
        latm.setData(latencies);
        sorterPI = new TableSorter(latm);
        final JTable latTable = new JTable(sorterPI);
        /*      latTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = latTable.rowAtPoint(evt.getPoint());
                int col = latTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0 && col <2) {
                for (TGComponent tgc: tmap.getTMLModeling().getCheckedComps().keySet()){
                if (tmap.getTMLModeling().getCheckedComps().get(tgc).equals(latm.getValueAt(row,col).toString().split(" ")[0])){
                mgui.selectTab(tgc.getTDiagramPanel());
                tgc.getTDiagramPanel().highlightTGComponent(tgc);
                }
                }
                }
                }
                });*/
        sorterPI.setTableHeader(latTable.getTableHeader());
        ((latTable.getColumnModel()).getColumn(0)).setPreferredWidth(400);
        ((latTable.getColumnModel()).getColumn(1)).setPreferredWidth(400);
        ((latTable.getColumnModel()).getColumn(2)).setPreferredWidth(100);
        ((latTable.getColumnModel()).getColumn(3)).setPreferredWidth(100);
        ((latTable.getColumnModel()).getColumn(4)).setPreferredWidth(100);
        ((latTable.getColumnModel()).getColumn(5)).setPreferredWidth(100);
        //  latTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspLatency = new JScrollPane(latTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jspLatency.setWheelScrollingEnabled(true);
        jspLatency.getVerticalScrollBar().setUnitIncrement(10);
        jspLatency.setMinimumSize(new Dimension(1200, 100));
        jspLatency.setPreferredSize(new Dimension(1200, 100));
        latencyPanel.add(jspLatency, c0);


        //        updateLatencyButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_LATENCY]);
        //      latencyPanel.add(updateLatencyButton,c0);

        //Randomness
        randomPanel = new JPanel();
        randomPanel.setLayout(new GridBagLayout());
        GridBagConstraints cr = new GridBagConstraints();

        cr.gridheight = 1;
        cr.weighty = 1.0;
        cr.weightx = 1.0;
        cr.gridwidth = GridBagConstraints.REMAINDER; //end row
        cr.fill = GridBagConstraints.BOTH;
        cr.gridheight = 1;
        imposeRandom = new JCheckBox("Force random value");
        imposeRandom.addActionListener(this);
        randomPanel.add(imposeRandom, cr);
        cr.gridwidth = 1;
        cr.fill = GridBagConstraints.HORIZONTAL;
        randomPanel.add(new JLabel("value:"), cr);
        randomValue = new JTextField();
        randomPanel.add(randomValue, cr);
        cr.gridwidth = GridBagConstraints.REMAINDER; //end row
        updateRandom = new JButton("Update", IconManager.imgic16);
        updateRandom.setToolTipText("Update random value");
        randomPanel.add(updateRandom, cr);
        updateRandom.addActionListener(this);

        infoTab.addTab("Randomness", IconManager.imgic1202, randomPanel, "Randomness");

        imposeRandom.setSelected(false);
        randomValue.setEnabled(false);


        //Asynchronous

        // Making vector of fifos
        AvatarInteractiveSimulationFIFOData fifo;
        fifos = new Vector<AvatarInteractiveSimulationFIFOData>();
        for (AvatarRelation ar : avspec.getRelations()) {
            if (ar.isAsynchronous()) {
                fifo = new AvatarInteractiveSimulationFIFOData(ar);
                fifos.add(fifo);
            }
        }


        asyncPanel = new JPanel();
        asyncPanel.setLayout(new GridBagLayout());
        GridBagConstraints ca = new GridBagConstraints();

        ca.gridheight = 1;
        ca.weighty = 1.0;
        ca.weightx = 1.0;
        ca.gridwidth = GridBagConstraints.REMAINDER; //end row
        ca.fill = GridBagConstraints.HORIZONTAL;
        ca.gridheight = 1;

        comboFIFOs = new JComboBox<AvatarInteractiveSimulationFIFOData>(fifos);
        comboFIFOs.addActionListener(this);
        asyncPanel.add(comboFIFOs, ca);
        ca.fill = GridBagConstraints.BOTH;

        JPanel borderjlist = new JPanel(new GridBagLayout());
        GridBagConstraints cb = new GridBagConstraints();

        cb.gridheight = 1;
        cb.weighty = 1.0;
        cb.weightx = 1.0;
        cb.gridwidth = GridBagConstraints.REMAINDER; //end row
        cb.fill = GridBagConstraints.BOTH;
        cb.gridheight = 1;
        borderjlist.setBorder(new javax.swing.border.TitledBorder("Top of selected FIFO:"));
        asyncmsgs = new JList<AvatarSimulationAsynchronousTransaction>();
        JScrollPane pane = new JScrollPane(asyncmsgs);
        borderjlist.add(pane, cb);
        asyncPanel.add(borderjlist, ca);
        infoTab.addTab("Asynch. msg", IconManager.imgic1202, asyncPanel, "Asynch. msg.");

        ca.fill = GridBagConstraints.NONE;
        ca.gridwidth = 1;
        delete = new JButton(actions[AvatarInteractiveSimulationActions.ACT_DELETE_ASYNC_MSG]);
        //delete.addActionListener(this);
        asyncPanel.add(delete, ca);
        up = new JButton(actions[AvatarInteractiveSimulationActions.ACT_UP_ASYNC_MSG]);
        //up.addActionListener(this);
        asyncPanel.add(up, ca);
        ca.gridwidth = GridBagConstraints.REMAINDER; //end row
        down = new JButton(actions[AvatarInteractiveSimulationActions.ACT_DOWN_ASYNC_MSG]);
        //down.addActionListener(this);
        asyncPanel.add(down, ca);
        setDeleteUpDown();
        asyncmsgs.addListSelectionListener(this);


        pack();

    }

    private void initActions() {
        actions = new AvatarInteractiveSimulationActions[AvatarInteractiveSimulationActions.NB_ACTION];
        for (int i = 0; i < AvatarInteractiveSimulationActions.NB_ACTION; i++) {
            actions[i] = new AvatarInteractiveSimulationActions(i);
            actions[i].addActionListener(this);
            //actions[i].addKeyListener(this);
        }

        //cpuIDs = makeCPUIDs();
        //busIDs = makeBusIDs();
        //memIDs = makeMemIDs();
        //taskIDs = makeTasksIDs();
        //chanIDs = makeChanIDs();
    }


    public void setComponents() {
        setAll();
        animateDiagrams();
        animateFutureTransactions();
    }

    public void close() {
        //killThread();
        if (ass != null) {
            ass.killSimulation();
        }
        dispose();
        setVisible(false);
        runningTGComponents.clear();
        resetMetElements();
        mgui.setAvatarAnimate(false);
    }

    public void runSimulation() {
        ass.resetTrace();
        previousTime = System.currentTimeMillis();
        if (ass != null) {
            ass.setNbOfCommands(AvatarSpecificationSimulation.MAX_TRANSACTION_IN_A_ROW);
            ass.goSimulation();
            //ass.backOneTransactionBunch();
        }
    }

    public void runXCommands() {
        ass.resetTrace();
        String txt = paramMainCommand.getText();
        int nb;
        try {
            nb = Math.max(1, Integer.decode(txt).intValue());
        } catch (Exception e) {
            nb = 1;
        }
        if (ass != null) {
            ass.setNbOfCommands(nb);
            previousTime = System.currentTimeMillis();
            ass.goSimulation();
        }
    }


    public void runTrace() {
        TraceManager.addDev("Running trace");

        if (SELECTED_SIMULATION_TRACE == null) {
            return;
        }

        selectedTrace = SELECTED_SIMULATION_TRACE;

        TraceManager.addDev("Testing content");

        if (!selectedTrace.hasContent()) {
            return;
        }

        TraceManager.addDev("Content ok");

        // Transform String into a CSV object
        traceObject = new CSVObject(selectedTrace.getContent());

        // Start the trace execution
        TraceManager.addDev("Reset simulation");
        resetSimulation();
        ass.setNbOfCommands(AvatarSpecificationSimulation.MAX_TRANSACTION_IN_A_ROW);
        ass.setTraceToPlay(traceObject);
        TraceManager.addDev("Going to play the trace");
        previousTime = System.currentTimeMillis();
        if (ass != null) {
            ass.setNbOfCommands(AvatarSpecificationSimulation.MAX_TRANSACTION_IN_A_ROW);
            ass.goSimulation();
            //ass.backOneTransactionBunch();
        }
    }

    public void stopSimulation() {
        //previousTime = System.currentTimeMillis();
        if (ass != null) {
            ass.stopSimulation();
        }
    }

    public void resetSimulation() {
        //resetThread();
        previousTime = System.currentTimeMillis();
        if (ass != null) {
            resetMetElements();
            ass.resetSimulation();
            ass.resetTrace();
            //ass.backOneTransactionBunch();
        }
        //      latencies.clear();
        transTimes.clear();
        for (String id : avspec.checkedIDs) {
            transTimes.put(id, new ArrayList<String>());
        }
        //ass.killSimulation();
    }

    public void backwardOneTransaction() {
        previousTime = System.currentTimeMillis();
        ass.backOneTransactionBunch();
    }


    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        /*if (e.getSource() == sendTextCommand) {
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


    public void setMode(int _mode) {
        busyMode = _mode;
        //TraceManager.addDev("****************** mode set to " + busyMode);
        if (ass != null) {
            if (ass.getPendingTransitions() != null) {
                //TraceManager.addDev("# of Pending transactions: " + ass.getPendingTransitions().size());
            }
        }
        setAll();


        // Diagram animation?
        if (!(busyMode == AvatarSpecificationSimulation.GATHER) && !(busyMode == AvatarSpecificationSimulation.EXECUTE)) {
            //TraceManager.addDev("Animating");
            updateMetElements();
            updateTransactionsTable();
            updateAsynchronousChannels();
            animateDiagrams();
        }

        animateFutureTransactions();

    }

    public void updateTransactionAndTime(int _nbOfTransactions, long clockValue) {
        //TraceManager.addDev("Update transactions and time");
        long timeNow = System.currentTimeMillis();
        if (timeNow - previousTime > SPACE_UPDATE_TIME) {
            previousTime = timeNow;
            setLabelColors();
        }
    }

    private void setTraceName() {
        if (SELECTED_SIMULATION_TRACE != null) {
            if (SELECTED_SIMULATION_TRACE.hasContent()) {
                nameOfTrace.setText(SELECTED_SIMULATION_TRACE.getName());
            } else {
                nameOfTrace.setText("No selected trace");
            }
        }
    }

    public void updateSimulationTrace() {
        setTraceName();
        setAll();
    }

    public void setAll() {

        boolean b = true;

        switch (busyMode) {
            case AvatarSpecificationSimulation.DONT_EXECUTE:
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE].setEnabled(
                        JFrameAvatarInteractiveSimulation.SELECTED_SIMULATION_TRACE != null);
                actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);

                b = true;
                break;
            case AvatarSpecificationSimulation.GATHER:
            case AvatarSpecificationSimulation.EXECUTE:
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(true);
                b = false;
                break;
            case AvatarSpecificationSimulation.TERMINATED:
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE].setEnabled(
                        JFrameAvatarInteractiveSimulation.SELECTED_SIMULATION_TRACE != null);
                actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
                b = true;
                break;
            case AvatarSpecificationSimulation.INITIALIZE:
            case AvatarSpecificationSimulation.RESET:
            case AvatarSpecificationSimulation.KILLED:
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
                actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE].setEnabled(
                        JFrameAvatarInteractiveSimulation.SELECTED_SIMULATION_TRACE != null);
                actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].setEnabled(true);
                actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
                b = true;
                break;
        }

        actions[AvatarInteractiveSimulationActions.ACT_SAVE_SD_PNG].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_SAVE_SVG].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_SAVE_TXT].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_SAVE_CSV].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_PRINT_BENCHMARK].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_SAVE_BENCHMARK].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_ZOOM_IN].setEnabled(b);
        actions[AvatarInteractiveSimulationActions.ACT_ZOOM_OUT].setEnabled(b);

        setLabelColors();

        if ((blockPanel != null) && (blockPanel.isVisible())) {
            blockPanel.repaint();
        }

        if ((variablePanel != null) && (variablePanel.isVisible())) {
            variablePanel.repaint();
        }

        if ((sdpanel != null) && (sdpanel.isVisible())) {
            sdpanel.repaint();
            sdpanel.scrollToLowerPosition();
        }

        // Delete, up, down
        setDeleteUpDown(b);
    }

    public boolean isBusy() {
        if (busyMode == AvatarSpecificationSimulation.EXECUTE) {
            return true;
        }

        return busyMode == AvatarSpecificationSimulation.GATHER;

    }


    public void setDeleteUpDown() {
        setDeleteUpDown(!isBusy());
    }

    public void setDeleteUpDown(boolean b) {
        if (down != null) {
            delete.setEnabled(b && (asyncmsgs.getSelectedIndex() > -1));
            up.setEnabled(b && (asyncmsgs.getSelectedIndex() > 0));
            down.setEnabled(b && (asyncmsgs.getSelectedIndex() > -1) && (asyncmsgs.getSelectedIndex() < (nbOfAsyncMsgs - 1)));
        }
    }

    public void animateFutureTransactions() {
        setContentOfListOfPendingTransactions();
    }

    public void setLabelColors() {
        if ((time != null) && (status != null) && (info != null) && (coverage != null)) {
            String oldTime = time.getText();
            int index = oldTime.indexOf("(");
            if (index != -1) {
                oldTime = oldTime.substring(0, index).trim();
            }
            String newTime = "" + ass.getClockValue();
            if (oldTime.compareTo(newTime) != 0) {
                newTime += " (before:" + oldTime + ")";
            }
            time.setText(newTime);
            if (ass.getAllTransactions() != null) {
                info.setText("" + ass.getAllTransactions().size());
            } else {
                info.setText("0");
            }
            switch (busyMode) {
                case AvatarSpecificationSimulation.DONT_EXECUTE:
                    status.setText("Stopped");
                    status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
                    time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
                    info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
                    coverage.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
                    break;
                case AvatarSpecificationSimulation.GATHER:
                case AvatarSpecificationSimulation.EXECUTE:
                case AvatarSpecificationSimulation.RESET:
                case AvatarSpecificationSimulation.INITIALIZE:
                    status.setText("Running...");
                    status.setForeground(ColorManager.InteractiveSimulationText_BUSY);
                    time.setForeground(ColorManager.InteractiveSimulationText_BUSY);
                    info.setForeground(ColorManager.InteractiveSimulationText_BUSY);
                    coverage.setForeground(ColorManager.InteractiveSimulationText_BUSY);
                    break;
                case AvatarSpecificationSimulation.TERMINATED:
                    status.setText("Terminated");
                    status.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    time.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    info.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    coverage.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    break;
                case AvatarSpecificationSimulation.KILLED:
                    status.setText("killed");
                    status.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    time.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    info.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    coverage.setForeground(ColorManager.InteractiveSimulationText_TERM);
                    break;
            }
        }
    }

    public void zoomIn() {
        if (sdpanel != null) {
            sdpanel.zoomIn();
        }
    }

    public void zoomOut() {
        if (sdpanel != null) {
            sdpanel.zoomOut();
        }
    }

    public synchronized void setContentOfListOfPendingTransactions() {
        if (invokedLater == 0) {
            invokedLater = 1;
            //TraceManager.addDev("Invoke later:" + invokedLater);
            EventQueue.invokeLater(this);
        }
    }

    public void resetMetElements() {
        if (avspec.getReferenceObject() instanceof AvatarDesignPanel) {
            ((AvatarDesignPanel) (avspec.getReferenceObject())).resetMetElements();

        } else if (avspec.getReferenceObject() instanceof AttackTreePanel) {
            ((AttackTreePanel) (avspec.getReferenceObject())).resetMetElements();
        } else if (avspec.getReferenceObject() instanceof FaultTreePanel) {
            ((FaultTreePanel) (avspec.getReferenceObject())).resetMetElements();
        }

        if (coverage != null) {
            coverage.setText("0 %");
        }

    }

    public void updateMetElements() {
        Hashtable<AvatarStateMachineElement, Integer> hashOfAllElements = AvatarSimulationTransaction.hashOfAllElements;
        TGComponent tgc;
        Object o;


        if ( ((mettableElements == null) || (totalNbOfElements == -1)) && (ass != null) ) {
            totalNbOfElements = 0;
            mettableElements = new HashSet<>();
            for (AvatarSimulationBlock asb : ass.getSimulationBlocks()) {
                AvatarBlock ab = asb.getBlock();
                if (ab != null) {
                    //if (!(ab.getName().startsWith("Timer__"))) {
                    AvatarStateMachine asm = ab.getStateMachine();
                    if (asm != null) {
                        for (AvatarStateMachineElement elt : asm.getListOfElements()) {
                            Object obj = elt.getReferenceObject();
                            if (obj != null) {

                                // Verifier que obj est une element de machine à états
                                if (obj.getClass().getPackage().getName().compareTo("ui.avatarsmd") == 0) {
                                    mettableElements.add((TGComponent)obj);
                                }

                            }
                        }
                    }
                    //for(Avatar
                    //totalNbOfElements = mettable
                    //}
                }
            }
            totalNbOfElements = mettableElements.size();
            //totalNbOfElements = ass.getNbOfASMGraphicalElements();
        }

        if (hashOfAllElements == null) {
            nbOfAllExecutedElements = 0;
            return;
        }

        int total = 0;
        HashSet<Object> metTmp = new HashSet<>();
        if (hashOfAllElements.hashCode() != nbOfAllExecutedElements) {
            Object objs [] = hashOfAllElements.keySet().toArray();
            //int total = 0;
            //int totalMet = 0;
            //TraceManager.addDev("Parsing array of elements: " + objs.length);
            for (int i = 0; i<objs.length; i++) {
                o = objs[i];
                //TraceManager.addDev("objs: " + o);
                Object oo = ((AvatarElement) o).getReferenceObject();
                if ( (oo != null) && (mettableElements.contains(oo) && (!metTmp.contains(oo))) ) {
                    metTmp.add(oo);
                    tgc = (TGComponent) oo;
                    /*if (tgc.getClass().getPackage().getName().compareTo("ui.avatarsmd") == 0) {
                      total ++;
                      }*/
                    //TraceManager.addDev("TGComponent: " + tgc);
                    int met = hashOfAllElements.get(o);
                    if ((met > 0) && (tgc.getClass().getPackage().getName().compareTo("ui.avatarsmd") == 0)) {
                        total++;
                    }
                    tgc.setAVATARMet(met);
                    //total ++;
                    //if (met >0) {
                    //totalMet ++;
                    //}

                }

            }
            nbOfAllExecutedElements = hashOfAllElements.hashCode();

            if ((totalNbOfElements != -1)) {
                //TraceManager.addDev("totalMet=" + hashOfAllElements.size() + " total=" + totalNbOfElements);
                double cov = (total * 1000.0) / mettableElements.size();
                cov = Math.floor(cov);
                coverageVal = cov / 10;
                if (coverage != null) {
                    coverage.setText("" + coverageVal + "%");
                }
            }
        }
        //nbOfAllExecutedElements = hashOfAllElements.hashCode();
    }

    public void addLatency() {
        SimulationLatency sl = new SimulationLatency();
        if (transaction1.getSelectedItem() != null && transaction2.getSelectedItem() != null) {
            sl.setTransaction1(transaction1.getSelectedItem().toString());
            sl.setTransaction2(transaction2.getSelectedItem().toString());
            nameLatencyMap.put(transaction1.getSelectedItem().toString() + "--" + transaction2.getSelectedItem().toString(), sl);
            latencies.add(sl);
            //        toCheck.add(transaction1.getSelectedItem().toString()+"--"+transaction2.getSelectedItem().toString());
            updateTransactionsTable();
        }
    }

    public void resetLatencies() {
        for (SimulationLatency latency : latencies) {
            latency.setMinTime("N/A");
            latency.setMaxTime("N/A");
            latency.setAverageTime("N/A");
            latency.setStDev("N/A");
        }
    }

    public void updateTransactionsTable() {
        if (transactiontm != null) {
            transactiontm.fireTableStructureChanged();
        }
        if (ass != null && latm != null) {
            resetLatencies();
            if (ass.getAllTransactions() != null) {
                for (AvatarSimulationTransaction trans : ass.getAllTransactions()) {
                    if ((trans.executedElement != null) && (trans.executedElement.getReferenceObject() != null)) {
                        //                        String id = ((TGComponent)trans.executedElement.getReferenceObject()).getName() + ":"+Integer.toString(trans.executedElement.getID());
                        String id = Integer.toString(trans.executedElement.getID());
                        //  
                        //  
                        String key = "";
                        for (String s : transTimes.keySet()) {
                            String tmpid = s.split(":")[1];
                            if (id.equals(tmpid)) {
                                key = s;
                            }
                        }
                        if (transTimes.containsKey(key)) {
                            if (!transTimes.get(key).contains(Long.toString(trans.clockValueWhenFinished))) {
                                transTimes.get(key).add(Long.toString(trans.clockValueWhenFinished));
                            }
                        }
                    }
                }
            }
            //  

            for (String st1 : transTimes.keySet()) {
                for (String st2 : transTimes.keySet()) {
                    if (st1 != st2 && nameLatencyMap.containsKey(st1 + "--" + st2)) {
                        SimulationLatency sl = nameLatencyMap.get(st1 + "--" + st2);
                        if (transTimes.get(st1) != null && transTimes.get(st2) != null) {

                            List<String> tmptimes2 = new ArrayList<>(transTimes.get(st2));

                            ArrayList<Integer> minTimes = new ArrayList<Integer>();
                            /*SimulationLatency sl = new SimulationLatency();
                              sl.setTransaction1(st1);
                              sl.setTransaction2(st2);*/


                            for (String time1 : transTimes.get(st1)) {
                                String match = "";
                                //Find the first subsequent transaction
                                int time = Integer.MAX_VALUE;
                                for (String time2 : tmptimes2) {

                                    int diff = Integer.valueOf(time2) - Integer.valueOf(time1);
                                    if (diff < time && diff >= 0) {
                                        time = diff;
                                        match = time2;
                                    }
                                }
                                tmptimes2.remove(match);
                                if (time != Integer.MAX_VALUE) {
                                    minTimes.add(time);
                                }
                            }
                            //  
                            if (minTimes.size() > 0) {

                                int sum = 0;
                                sl.setMinTime(Integer.toString(Collections.min(minTimes)));
                                sl.setMaxTime(Integer.toString(Collections.max(minTimes)));
                                for (int time : minTimes) {
                                    sum += time;
                                }
                                double average = (double) sum / (double) minTimes.size();
                                double stdev = 0.0;
                                for (int time : minTimes) {
                                    stdev += (time - average) * (time - average);
                                }
                                stdev = stdev / minTimes.size();
                                stdev = Math.sqrt(stdev);
                                sl.setAverageTime(String.format("%.1f", average));
                                sl.setStDev(String.format("%.1f", stdev));
                                mgui.addLatencyVals(Integer.valueOf(st2.split(":")[1]), new String[]{st1, Integer.toString(Collections.max(minTimes))});
                            }
                            //                          latencies.add(sl);


                        }

                    }

                }
            }

            if (latm != null && latencies.size() > 0) {
                latm.setData(latencies);
                ((AvatarDesignPanel) (avspec.getReferenceObject())).modelBacktracingLatency(latencies);
            }
        }
    }


    public String[] getFirstMessagesOnEachConnectorSide(AvatarBDPortConnector conn) {
        String[] messages = new String[2];
        messages[0] = null;
        messages[1] = null;
        boolean b0, b1;
        AvatarRelation ar;
        AvatarBlock ab;
        int index;
        String info;

        if (asyncmsgs == null) {
            return messages;
        }

        b0 = false;
        b1 = false;
        for (AvatarSimulationAsynchronousTransaction msg : lastAsyncmsgs) {
            ar = msg.getRelation();
            if (ar.hasReferenceObject(conn)) {
                ab = ar.getInBlock(msg.getIndex());

                if (ab == ar.block1) {
                    info = ar.getSignal1(msg.getIndex()).getName();
                    index = 0;
                } else {
                    info = ar.getSignal2(msg.getIndex()).getName();
                    index = 1;
                }

                info += msg.parametersToString();

                if ((index == 0) && (!b0)) {
                    b0 = true;
                    messages[0] = info;
                } else if ((index == 1) && (!b1)) {
                    b1 = true;
                    messages[1] = info;
                }

                if (b0 && b1) {
                    break;
                }
            }

        }

        return messages;
    }


    public void updateAsynchronousChannels() {

        if (ass != null) {
            lastAsyncmsgs = new Vector<>(ass.getAsynchronousMessages());

            if (fifos != null) {
                for (AvatarInteractiveSimulationFIFOData fifo : fifos) {
                    fifo.nb = 0;
                }
            }

            if (lastAsyncmsgs != null) {

                if (lastAsyncmsgs.size() > 0) {
                    for (AvatarSimulationAsynchronousTransaction msg : lastAsyncmsgs) {
                        for (AvatarInteractiveSimulationFIFOData fifo0 : fifos) {
                            if (fifo0.fifo == msg.getRelation()) {
                                fifo0.nb++;
                                break;
                            }
                        }
                    }
                }

                if (asyncPanel != null) {
                    asyncPanel.revalidate();
                    if (comboFIFOs != null) {
                        comboFIFOs.revalidate();
                        comboFIFOs.repaint();
                    }
                }


                if (asyncmsgs != null) {
                    AvatarInteractiveSimulationFIFOData currentFifo = (AvatarInteractiveSimulationFIFOData) (comboFIFOs.getSelectedItem());
                    if (currentFifo != null) {
                        nbOfAsyncMsgs = 0;
                        Vector<AvatarSimulationAsynchronousTransaction> vectorForList = new Vector<AvatarSimulationAsynchronousTransaction>();
                        for (AvatarSimulationAsynchronousTransaction as : lastAsyncmsgs) {
                            if (as.getRelation() == currentFifo.fifo) {
                                vectorForList.add(as);
                                nbOfAsyncMsgs++;
                            }
                        }
                        //Collections.reverse(vectorForList);
                        asyncmsgs.setListData(vectorForList);
                    }
                }

            }
        }
    }

    public void animateDiagrams() {
        if ((animate != null) && (ass != null) && (ass.getSimulationBlocks() != null)) {
            if (animate.isSelected()) {
                // We go through all blocks
                runningTGComponents.clear();
                AvatarStateMachineElement asme;
                TGComponent tgc;
                for (AvatarSimulationBlock block : ass.getSimulationBlocks()) {
                    asme = block.getCurrentAvatarElement();
                    if (asme != null) {
                        // Search for corresponding element in avatar spec
                        tgc = (TGComponent) (asme.getReferenceObject());
                        if (tgc != null) {
                            //TraceManager.addDev("Found an object:" + tgc);
                            runningTGComponents.add(tgc);
                        }
                    }
                }
                if (openDiagram.isSelected()) {
                    if (ass.getPreviousBlock() != null) {
                        //TraceManager.addDev("Open SMD diag" + ass.getPreviousBlock().getName());
                        mgui.openAVATARSMD(ass.getPreviousBlock().getName());
                    }
                } else {
                    //Refresh current diagram
                    mgui.refreshCurrentPanel();
                }
            }
            mgui.setAvatarAnimate(animate.isSelected());
        }
        //TraceManager.addDev("End animate diag");
    }

    public boolean isRunningComponent(TGComponent _tgc) {
        if (isVisible()) {
            return runningTGComponents.contains(_tgc);
        }
        return false;
    }

    public boolean isSelectedComponentFromTransaction(TGComponent _tgc) {
        if (isVisible()) {
            return (_tgc == selectedComponentForTransaction1) || (_tgc == selectedComponentForTransaction2);
        }
        return false;
    }

    public void actSaveTxt() {
        TraceManager.addDev("Saving in txt format");
        String fileName = saveFileName.getText().trim();

        if (fileName.length() == 0) {
            fileName += "simulationtrace_fromttool.txt";
        }

        if (ConfigurationTTool.isConfigured(ConfigurationTTool.TGraphPath)) {
            fileName = ConfigurationTTool.TGraphPath + System.getProperty("file.separator") + fileName;
        } else {
            // Using model directory
            String path = mgui.getModelFileFullPath();
            fileName = path.substring(0, path.lastIndexOf(File.separator) + 1) + fileName;
            TraceManager.addDev("New Filename = " + fileName);
        }


        boolean ok = true;

        try {
            ok = FileUtils.checkFileForSave(new File(fileName));
        } catch (Exception e) {
            TraceManager.addDev("Exception=" + e.getMessage());
            ok = false;
        }

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "The capture could not be performed: the file name or path is not valid",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        try {
            FileUtils.saveFile(fileName, ass.getStringExecutedTransactions());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "The simulation trace in text format could not be saved: " + e.getMessage(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        /*JOptionPane.showMessageDialog(this,
                "Simulation trace was saved in " + fileName,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);*/

        String shortFileName;
        File f = new File(fileName);
        shortFileName = f.getName();
        SimulationTrace st = new SimulationTrace(shortFileName, SimulationTrace.TXT_AVATAR, fileName);
        mgui.addSimulationTrace(st);

        //ass.printExecutedTransactions();
    }

    public void actSaveCSV() {
        TraceManager.addDev("Saving in CSV format");
        String fileName = saveFileName.getText().trim();

        if (fileName.length() == 0) {
            fileName += "simulationtrace_fromttool.csv";
        }

        if (ConfigurationTTool.isConfigured(ConfigurationTTool.TGraphPath)) {
            fileName = ConfigurationTTool.TGraphPath + System.getProperty("file.separator") + fileName;
        } else {
            // Using model directory
            String path = mgui.getModelFileFullPath();
            fileName = path.substring(0, path.lastIndexOf(File.separator) + 1) + fileName;
            TraceManager.addDev("New Filename = " + fileName);
        }


        boolean ok = true;

        try {
            ok = FileUtils.checkFileForSave(new File(fileName));
        } catch (Exception e) {
            TraceManager.addDev("Exception=" + e.getMessage());
            ok = false;
        }

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "The capture could not be performed: the file name or path is not valid",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        String data = ass.toCSV();
        try {
            FileUtils.saveFile(fileName, data);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "The simulation trace in text format could not be saved: " + e.getMessage(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }



        String shortFileName;
        File f = new File(fileName);
        shortFileName = f.getName();
        SimulationTrace st = new SimulationTrace(shortFileName, SimulationTrace.CSV_AVATAR, fileName);
        st.setContent(data);
        mgui.addSimulationTrace(st);

        //ass.printExecutedTransactions();
    }


    public void actSaveSvg() {
        TraceManager.addDev("Saving in svg format");


        // Testing file for save

        String fileName = saveFileName.getText().trim();

        if (fileName.length() == 0) {
            fileName += "simulationtrace_fromttool.svg";
        }

        if (ConfigurationTTool.isConfigured(ConfigurationTTool.IMGPath)) {
            fileName = ConfigurationTTool.IMGPath + System.getProperty("file.separator") + fileName;
        } else {
            // Using model directory
            String path = mgui.getModelFileFullPath();
            fileName = path.substring(0, path.lastIndexOf(File.separator) + 1) + fileName;
            TraceManager.addDev("New Filename = " + fileName);
        }

        boolean ok = true;

        try {
            ok = FileUtils.checkFileForSave(new File(fileName));
        } catch (Exception e) {
            ok = false;
        }

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "The capture could not be performed: the specified file is not valid",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SVGGeneration gen = new SVGGeneration();
        gen.saveInSVG(sdpanel, fileName);
        //newSVGSave(fileName);

        String shortFileName;
        File f = new File(fileName);
        shortFileName = f.getName();
        SimulationTrace st = new SimulationTrace(shortFileName, SimulationTrace.SVG_AVATAR, fileName);
        mgui.addSimulationTrace(st);


        /*StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");


        SVGGraphics svgg = new SVGGraphics(sdpanel.getLastGraphics());

        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        //this.paint(svgg);
        TraceManager.addDev("Painting for svg");
        sdpanel.paintComponent(svgg);
        TraceManager.addDev("Painting for svg done");
        sb.append(svgg.getSVGString());
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        sb.append("</svg>");

        try {
            FileUtils.saveFile(fileName, sb.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "The capture could not be performed: " + e.getMessage(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "The capture was performed in " + fileName,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);

        //TraceManager.addDev("Svg=" + sb.toString());

        //return sb.toString();*/

    }

    // FileName must be valid
    /*private void newSVGSave(String fileName) {
        TraceManager.addDev("New SVG save in " + fileName);
        // Get a DOMImplementation.
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        sdpanel.paint(svgGenerator);


        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        try {
            File fileSave = new File(fileName);
            FileOutputStream fos = new FileOutputStream(fileSave);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            svgGenerator.stream(out, useCSS);
        } catch (Exception e) {
            TraceManager.addDev("SVG generation failed: " + e.getMessage());
        }
    }*/

    public void actSaveSDPNG() {
        //Saving PNG file;
        BufferedImage bi;
        File file;

        bi = sdpanel.performCapture();

        String filePath = "";
        if (ConfigurationTTool.isConfigured(ConfigurationTTool.IMGPath)) {
            filePath += ConfigurationTTool.IMGPath;
            if (!filePath.endsWith(File.separator)) {
                filePath += File.separator;
            }
        } else {
            String path = mgui.getModelFileFullPath();
            filePath = path.substring(0, path.lastIndexOf(File.separator) + 1);
        }

        if ((saveFileName.getText() != null) && (saveFileName.getText().length() > 0)) {
            filePath += saveFileName.getText();
        } else {
            filePath += "simulationtrace_fromttool.png";
        }

        file = new File(filePath);

        mgui.writeImageCapture(bi, file, false);

        SimulationTrace st = new SimulationTrace(file.getName(), SimulationTrace.PNG_AVATAR, file.getAbsolutePath());
        mgui.addSimulationTrace(st);
    }

    public void deleteAsyncMsg() {
        //TraceManager.addDev("Deleting async msg");
        if ((ass != null) && (!isBusy())) {
            int index = asyncmsgs.getSelectedIndex();
            if (index > -1) {
                boolean pendingModified = ass.removeAsyncMessage(((AvatarInteractiveSimulationFIFOData) (comboFIFOs.getSelectedItem())).fifo, index);
                updateAsynchronousChannels();
                if (pendingModified) {
                    updatePending();
                }
            }
        }

    }

    public void upAsyncMsg() {
        //TraceManager.addDev("Up async msg");
        if ((ass != null) && (!isBusy())) {
            int index = asyncmsgs.getSelectedIndex();
            if (index > 0) {
                ass.moveAsyncMessage(((AvatarInteractiveSimulationFIFOData) (comboFIFOs.getSelectedItem())).fifo, index, index - 1);
                updateAsynchronousChannels();
                asyncmsgs.setSelectedIndex(index - 1);
            }
        }

        //printFullList();

    }

    public void downAsyncMsg() {
        //TraceManager.addDev("Down async msg");
        if ((ass != null) && (!isBusy())) {
            int index = asyncmsgs.getSelectedIndex();
            if (index > -1) {
                //TraceManager.addDev("Moving from  index: " + index + " to: " + (index+1));
                ass.moveAsyncMessage(((AvatarInteractiveSimulationFIFOData) (comboFIFOs.getSelectedItem())).fifo, index, index + 1);
                updateAsynchronousChannels();
                //TraceManager.addDev("Selecting list at index:" + index);
                asyncmsgs.repaint();
                asyncmsgs.setSelectedIndex(index + 1);
            }
        }
        //printFullList();
    }

    public void printFullList() {
        int i = 0;
        if (ass != null) {
            for (AvatarSimulationAsynchronousTransaction tr : ass.getAsynchronousMessages()) {
                TraceManager.addDev("#" + i + "\t: " + tr);
                i++;
            }
        }
    }


    // Mouse management
    public void mouseReleased(MouseEvent e) {
    }


    /**
     * This adapter is constructed to handle mouse over component events.
     */
    private class MouseHandler extends MouseAdapter {

        private JLabel label;

        /**
         * ctor for the adapter.
         *
         * @param label the JLabel which will recieve value of the
         *              Action.LONG_DESCRIPTION key.
         */
        public MouseHandler(JLabel label) {
            setLabel(label);
        }

        public void setLabel(JLabel label) {
            this.label = label;
        }

        public void mouseEntered(MouseEvent evt) {
            if (evt.getSource() instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) evt.getSource();
                Action action = button.getAction();
                if (action != null) {
                    String message = (String) action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        actionPerformed(command, evt);
    }

    public void actionPerformed(String command, ActionEvent evt) {
        //TraceManager.addDev("Command:" + command);

        if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].getActionCommand())) {
            runSimulation();
            //TraceManager.addDev("Start simulation!");
        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU_MAX_TRANS].getActionCommand())) {
            runSimulation();

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].getActionCommand())) {
            runXCommands();

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_TRACE].getActionCommand())) {
            runTrace();

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].getActionCommand())) {
            stopSimulation();

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].getActionCommand())) {
            backwardOneTransaction();

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].getActionCommand())) {
            resetSimulation();

        } else if  (command.equals(actions[AvatarInteractiveSimulationActions.ACT_REMOVE_ALL_TRANS].getActionCommand())) {
            if (ass != null) {
                ass.removeAllTransactions();
            }
        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand())) {
            close();
            return;

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_TXT].getActionCommand())) {
            actSaveTxt();
            return;

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_CSV].getActionCommand())) {
            actSaveCSV();
            return;

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_SD_PNG].getActionCommand())) {
            actSaveSDPNG();
            return;

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_SVG].getActionCommand())) {
            actSaveSvg();
            return;

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_ZOOM_IN].getActionCommand())) {
            zoomIn();
            return;

        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_ZOOM_OUT].getActionCommand())) {
            zoomOut();
            return;
        } else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_ADD_LATENCY].getActionCommand())) {
            addLatency();
            return;
        }

        if (evt == null) {
            return;
        }

        if (evt.getSource() == displayedTransactionsText) {
            TraceManager.addDev("Entered text:" + displayedTransactionsText.getText());

        } else if ((evt.getSource() == imposeRandom) || (evt.getSource() == updateRandom)) {
            randomValue.setEnabled(imposeRandom.isSelected());
            if (ass != null) {
                if (imposeRandom.isSelected()) {
                    int val;
                    try {
                        val = Integer.decode(randomValue.getText().trim()).intValue();
                    } catch (Exception e) {
                        val = -1;
                    }
                    ass.forceRandom(val);
                } else {
                    ass.forceRandom(-1);
                }
            }
        } else if (evt.getSource() == comboFIFOs) {
            updateAsynchronousChannels();
            setDeleteUpDown();
        } else if (evt.getSource() == delete) {
            deleteAsyncMsg();
            return;
        } else if (evt.getSource() == up) {
            TraceManager.addDev("Source = up");
            upAsyncMsg();
        } else if (evt.getSource() == down) {
            TraceManager.addDev("Source = up");
            downAsyncMsg();
        } else if (evt.getSource() == edit) {
            TraceManager.addDev("Edit variable at index: " + selectedRow);
            editVariableValue(selectedRow);
        }

        // Check for source of jcheckbox
        int index = 0;
        for (JCheckBox jcb : displayedBlocks) {
            if (evt.getSource() == jcb) {
                ass.getSimulationBlocks().get(index).selected = jcb.isSelected();
                TraceManager.addDev("Block " + ass.getSimulationBlocks().get(index) + " is now " + ass.getSimulationBlocks().get(index).selected);
                ass.computeSelectedSimulationBlocks();
                sdpanel.repaint();
                return;
            }
            index++;
        }
    }

    public void error(String error) {
        jta.append("error: " + error + "\n");
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == asyncmsgs) {
            setDeleteUpDown();
            return;
        }

        int index = listPendingTransactions.getSelectedIndex();
        //TraceManager.addDev("Selected index = " +  index);
        if (index > -1) {
            try {
                AvatarSimulationPendingTransaction aspt = listPendingTransactions.getSelectedValue();
                selectedComponentForTransaction1 = (TGComponent) (aspt.elementToExecute.getReferenceObject());
                selectedComponentForTransaction2 = null;
                if ((selectedComponentForTransaction1 == null) && (aspt.linkedTransaction != null)) {
                    //TraceManager.addDev("Adding reference object: " + aspt.linkedTransaction.elementToExecute.getReferenceObject());
                    selectedComponentForTransaction1 = (TGComponent) (aspt.linkedTransaction.elementToExecute.getReferenceObject());
                    selectedComponentForTransaction2 = null;
                } else if (aspt.linkedTransaction != null) {
                    selectedComponentForTransaction2 = (TGComponent) (aspt.linkedTransaction.elementToExecute.getReferenceObject());
                }
                if (!(busyMode == AvatarSpecificationSimulation.GATHER) && !(busyMode == AvatarSpecificationSimulation.EXECUTE)) {
                    ass.setIndexSelectedTransaction(listPendingTransactions.getSelectedIndex());
                }
                if (animate.isSelected()) {
                    if (openDiagram.isSelected()) {
                        if (aspt.asb != null) {
                            previousBlock = aspt.asb;
                            mgui.openAVATARSMD(previousBlock.getName());
                        }
                    } else {
                        mgui.refreshCurrentPanel();
                    }
                }
            } catch (Exception ex) {
                TraceManager.addDev("Exception selected component");
                selectedComponentForTransaction1 = null;
                selectedComponentForTransaction2 = null;
                if (openDiagram.isSelected()) {
                    if ((previousBlock != null) && (animate.isSelected())) {
                        mgui.openAVATARSMD(previousBlock.getName());
                    } else {
                        mgui.refreshCurrentPanel();
                    }
                } else {
                    mgui.refreshCurrentPanel();
                }
            }
        } else {
            selectedComponentForTransaction1 = null;
            selectedComponentForTransaction2 = null;
            if ((previousBlock != null) && (animate.isSelected())) {
                if (openDiagram.isSelected()) {
                    mgui.openAVATARSMD(previousBlock.getName());
                } else {
                    mgui.refreshCurrentPanel();
                }
            } else {
                mgui.refreshCurrentPanel();
            }
        }

    }


    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == animate) {
            //TraceManager.addDev("Animate is ... " + animate.isSelected());
            animateDiagrams();
            diploids.setEnabled(animate.isSelected());
            animateWithInfo.setEnabled(animate.isSelected());
            openDiagram.setEnabled(animate.isSelected());
        } else if (e.getSource() == diploids) {
            mgui.setAVATARIDs(diploids.isSelected());
            if (sdpanel != null) {
                sdpanel.setShowIDs(diploids.isSelected());
            }
        } else if (e.getSource() == executeEmptyTransition) {
            ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());
        } else if (e.getSource() == executeStateEntering) {
            ass.setExecuteStateEntering(executeStateEntering.isSelected());
        } else if (e.getSource() == traceInSD) {
            if (sdpanel != null) {
                sdpanel.setTrace(traceInSD.isSelected());
                sdpanel.repaint();
            }

        } else if (e.getSource() == hidden) {
            if (sdpanel != null) {
                sdpanel.setShowHiddenStates(hidden.isSelected());
            }
        }

    }


    private JPopupMenu createVariablePopup() {
        JPopupMenu menu = new JPopupMenu("Change variable value");
        edit = new JMenuItem("Edit variable value");
        edit.setActionCommand("edit");
        edit.addActionListener(this);
        menu.add(edit);
        return menu;
    }


    private void editVariableValue(int rowIndex) {
        // Show dialog
        String variableValue = (String)(variabletm.getValueAt(rowIndex, 3));
        String blockName = (String)(variabletm.getValueAt(rowIndex, 0));
        String variableName = (String)(variabletm.getValueAt(rowIndex, 2));
        String type = ((String)(variabletm.getValueAt(rowIndex, 1))).toLowerCase();
        String s = (String)JOptionPane.showInputDialog(this, "Block: " + blockName + ". Value of " + variableName,
                "Variable modification", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                null,
                variableValue);
        s = s.trim();

        boolean ret;

        try {
            // Verifie the value is correct
            if (type.startsWith("int")) {
                int value = Integer.parseInt(s);
            } else {
                // We assume this is a boolean
                boolean b = Boolean.parseBoolean(s);
            }

            ret = variabletm.setAttributeValueByRow(rowIndex, s);
            variablePanel.repaint();

        } catch (Exception e) {
            // Not a correct value: show an error
            TraceManager.addDev("Error in new variable value:" + e.getMessage());
            ret = false;
        }

        if (!ret) {
            // Show error on value change
            JOptionPane.showMessageDialog(this,
                    "Could not change the value of " + variableName + ": the new value is incorrect",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);

        }


    }


    public void windowClosing(WindowEvent e) {
        TraceManager.addDev("Windows closed!");
        close();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

} // Class
