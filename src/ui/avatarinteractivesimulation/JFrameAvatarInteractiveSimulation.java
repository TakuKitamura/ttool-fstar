/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
* Class JFrameAvatarInteractiveSimulation
* Creation: 21/01/2011
* version 1.0 21/01/2011
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarinteractivesimulation;

//import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


import myutil.*;
import ui.*;
import ui.file.*;

import avatartranslator.*;
import avatartranslator.directsimulation.*;


public	class JFrameAvatarInteractiveSimulation extends JFrame implements AvatarSimulationInteraction, ActionListener, Runnable, MouseListener, ItemListener, ListSelectionListener, WindowListener/*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	
	
	private static String buttonStartS = "Start simulator";
	private static String buttonStopAndCloseS = "Stop simulator and close";
	
	private static int NOT_STARTED = 0;
	private static int STARTED = 1;
	
	private static long SPACE_UPDATE_TIME = 100;
	
	private Frame f;
	private MainGUI mgui;
	private String title;
	
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
	JCheckBox latex, debug, animate, diploids, update, openDiagram, animateWithInfo, executeEmptyTransition, executeStateEntering;
	JTabbedPane commandTab, infoTab;
	protected JTextField paramMainCommand;
	protected JTextField saveFileName;
	protected JTextField stateFileName;
	protected JTextField benchmarkFileName;
	//protected JComboBox cpus, busses, mems, tasks, chans;
	
	//List of transactions
	private JList listPendingTransactions;
	private TGComponent selectedComponentForTransaction;
	private AvatarSimulationBlock previousBlock;
	
	
	
	//private String[] cpuIDs, busIDs, memIDs, taskIDs, chanIDs;
	
	// Status elements
	JLabel status, time, info;
	
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
	
	// Sequence Diagram
	private AvatarSpecificationSimulationSDPanel sdpanel;
	
	//JButton updateBlockInformationButton;
	
	
	private int busyMode = 0; // Mode of AvatarSpecificationSimulation
	
	// For managing actions
	public	AvatarInteractiveSimulationActions [] actions;
    public	MouseHandler mouseHandler;
    public  KeyListener keyHandler;
	
	
	/*private Hashtable <Integer, String> valueTable;
	private Hashtable <Integer, Integer> rowTable;
	
	private Hashtable <Integer, Integer> runningTable;
	private Hashtable <String, String> diagramTable;*/
	
	
	// new
	private AvatarSpecification avspec;
	private AvatarSpecificationSimulation ass;
	private Thread simulationThread;
	private boolean resetThread;
	private boolean killThread;
	
	private LinkedList<TGComponent> runningTGComponents;
	private int nbOfAllExecutedElements = 0;
	
	private long previousTime;
	
	private boolean simulationRunning;
	
	public JFrameAvatarInteractiveSimulation(Frame _f, MainGUI _mgui, String _title, AvatarSpecification _avspec) {
		super(_title);
		
		f = _f;
		mgui = _mgui;
		title = _title;
		avspec = _avspec;
		
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
		setIconImage(IconManager.img5100);
		setBackground(Color.WHITE);
		
		/*valueTable = new Hashtable<Integer, String>();
		rowTable = new Hashtable<Integer, Integer>();
		runningTable = new Hashtable<Integer, Integer>();
		diagramTable = new Hashtable<String, String>();*/

		try {
			setBackground(new Color(50, 40, 40, 200));
		} catch (Exception e) {
			setBackground(new Color(50, 40, 40));
		}
		initActions();
		
		initSimulation();
		
		makeComponents();
		setComponents();
	}
	
	private void initSimulation() {
		runningTGComponents = new LinkedList<TGComponent>();
		nbOfAllExecutedElements = 0;
		resetMetElements();
		ass = new AvatarSpecificationSimulation(avspec, this);
		//ass.initialize();
		simulationRunning = false;
		simulationThread = new Thread(this);
		simulationThread.start();
	}
	
	public synchronized void setSimulationRunning() {
		simulationRunning = true;
	}
	
	public synchronized void stopSimulationRunning() {
		simulationRunning = false;
	}
	
	public void run() {
		if (simulationRunning == true) {
			if (ass == null) {
				return;
			}
			
			Vector<AvatarSimulationPendingTransaction> ll = ass.getPendingTransitions();
			
			try {
				listPendingTransactions.clearSelection();
				selectedComponentForTransaction = null;
			if (ll != null) {
				listPendingTransactions.setListData(ll);
				int random = (int)(Math.floor((Math.random()*ll.size())));
				listPendingTransactions.setSelectedIndex(random);
			} else {
				listPendingTransactions.setListData(new Vector<AvatarSimulationPendingTransaction>());
			}
			} catch (Exception e) {}
		} else {
			setSimulationRunning();
			previousTime = System.currentTimeMillis();
			ass.runSimulation();
			TraceManager.addDev("Simulation thread ended");
			stopSimulationRunning();
		}
	}
	
	/*public void run() {
		resetThread = false;
		killThread = false;
		
		previousTime = System.currentTimeMillis();
		
		if (ass.getState() ==  AvatarSpecificationSimulation.INITIALIZE) {
			ass.runSimulation();
		}
		
		if (killThread) {
			return;
		}
		
		waitForResetOrKillThread();
		
		if (killThread) {
			return;
		}
		
		if (resetThread) {
			TraceManager.addDev("Simulation reseted");
			runningTGComponents = new LinkedList<TGComponent>();
			nbOfAllExecutedElements = 0;
			resetMetElements();
			ass.resetSimulation();
			previousTime = System.currentTimeMillis();
			run();
		}
		TraceManager.addDev("Simulation thread ended");
	}
	
	public synchronized void waitForResetOrKillThread() {
		TraceManager.addDev("waitForResetOrKillThread resetThread=" + resetThread + " killThread=" + killThread);
		while ((resetThread == false) && (killThread == false)){
			try {
				wait();
			} catch (Exception e) {}
		}
		TraceManager.addDev("EndWaitForResetOrKillThread");
	}
	
	public synchronized void killThread() {
		killThread = true;
		notifyAll();
	}
	
	public synchronized void resetThread() {
		resetThread = true;
		TraceManager.addDev("Reset thread = " + resetThread);
		notifyAll();
	}*/
	
	
	
	private JLabel createStatusBar()  {
        status = new JLabel("Ready...");
		status.setForeground(ColorManager.InteractiveSimulationText);
        status.setBorder(BorderFactory.createEtchedBorder());
        return status;
    }
	
	public void makeComponents() {
		JPanel jp01, jp02;
		//jp01.setPreferredSize(new Dimension(375, 400));
		GridBagLayout gridbag01;
		GridBagConstraints c01 ;
		
		//cp = new CommandParser();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container framePanel = getContentPane();
		framePanel.setLayout(new BorderLayout());
		//framePanel.setBackground(ColorManager.InteractiveSimulationBackground);
		//framePanel.setForeground(new Color(255, 166, 38));		
		
		//System.out.println("Button start created");
		//buttonStart = new JButton(actions[InteractiveSimulationActions.ACT_RUN_SIMU]);
		buttonStopAndClose = new JButton(actions[AvatarInteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL]);
		//buttonStopAndClose = new JButton(buttonStopAndCloseS, IconManager.imgic27);
		
		
		// statusBar
        status = createStatusBar();
		framePanel.add(status, BorderLayout.SOUTH);
        
        // Mouse handler
        mouseHandler = new MouseHandler(status);
		
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
		JPanel lowerPartPanel = new JPanel(); lowerPartPanel.setLayout(new BorderLayout());
		sdpanel = new AvatarSpecificationSimulationSDPanel(ass);
        //ass.setName("Interaction Overview Diagram");
        JScrollPane jsp	= new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sdpanel.setMyScrollPanel(jsp);
        jsp.setWheelScrollingEnabled(true);
		//jsp.setPreferredSize(new Dimension(800, 400));
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
		lowerPartPanel.add(jsp, BorderLayout.CENTER);
	

		
		// Commands
		commands = new JPanel(new BorderLayout());
		//commands.setFloatable(true);
		//commands.setMinimumSize(new Dimension(300, 250));
		commands.setBorder(new javax.swing.border.TitledBorder("Commands"));
		
		

		
		commandTab = new JTabbedPane();
		commands.add(commandTab, BorderLayout.CENTER);
		//commandTab.setBackground(ColorManager.InteractiveSimulationBackground);
		
		
		// Control commands
		jp01 = new JPanel(new BorderLayout());
		commandTab.addTab("Control", null, jp01, "Main control commands");
		//jp01.setMinimumSize(new Dimension(375, 400));
		//gridbag01 = new GridBagLayout();
		//c01 = new GridBagConstraints();
		//jp01.setLayout(gridbag01);
		
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
		
		jp02.add(new JLabel("Command parameter: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		paramMainCommand = new JTextField("1", 30);
		jp02.add(paramMainCommand, c01);
		// list of pending transactions
        JPanel panellpt = new JPanel();
        panellpt.setLayout(new BorderLayout());
        panellpt.setBorder(new javax.swing.border.TitledBorder("Pending transactions"));
		
        listPendingTransactions = new JList();
        //listPendingTransactions.setPreferredSize(new Dimension(400, 300));
        listPendingTransactions.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        listPendingTransactions.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listPendingTransactions);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panellpt.add(scrollPane1);
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
		
		
		infoTab = new JTabbedPane();
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
		jp02.add(new JLabel("nb Of transactions:"));
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
		
		//jp01.add(new JLabel(" "), c01);
		/*latex = new JCheckBox("Generate info in Latex format");
		jp01.add(latex, c01);*/
		/*debug = new JCheckBox("Print messages received from server");
		jp01.add(debug, c01);*/
		animate = new JCheckBox("Animate UML diagrams");
		jp01.add(animate, c01);
		diploids = new JCheckBox("Show AVATAR IDs on UML diagrams");
		jp01.add(diploids, c01);
		diploids.addItemListener(this);
		diploids.setSelected(false);
		animateWithInfo = new JCheckBox("Show transaction progression on UML diagrams");
		//jp01.add(animateWithInfo, c01);
		animateWithInfo.addItemListener(this);
		animateWithInfo.setSelected(true);
		openDiagram = new JCheckBox("Automatically open active state machine diagram");
		jp01.add(openDiagram, c01);
		openDiagram.setSelected(true);
		//update = new JCheckBox("Automatically update information (variables)");
		//jp01.add(update, c01);
		//update.addItemListener(this);
		//update.setSelected(true);
		
		animate.addItemListener(this);
		animate.setSelected(true);
		
		executeEmptyTransition = new JCheckBox("Automatically execute empty transitions");
		jp01.add(executeEmptyTransition, c01);
		executeEmptyTransition.setSelected(true);
		executeEmptyTransition.addItemListener(this);
		ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());
		
		executeStateEntering = new JCheckBox("Automatically enter states");
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
		jtablePI = new JTable(sorterPI);
		sorterPI.setTableHeader(jtablePI.getTableHeader());
		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspVariableInfo = new JScrollPane(jtablePI);
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
		
		// Variables
		/*variablePanel = new JPanel();
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
		variablePanel.add(updateTaskVariableInformationButton, BorderLayout.SOUTH);*/
		
		// CPUs
		/*cpuPanel = new JPanel();
		cpuPanel.setLayout(new BorderLayout());
		infoTab.addTab("CPUs", IconManager.imgic1100, cpuPanel, "Current state of CPUs");
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
		cpuPanel.add(panelCPU, BorderLayout.SOUTH);*/
		
		pack();
		
	}
	
	private	void initActions() {
        actions = new AvatarInteractiveSimulationActions[AvatarInteractiveSimulationActions.NB_ACTION];
        for(int	i=0; i<AvatarInteractiveSimulationActions.NB_ACTION; i++) {
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
		previousTime = System.currentTimeMillis();
		if (ass != null) {
			ass.setNbOfCommands(AvatarSpecificationSimulation.MAX_TRANSACTION_IN_A_ROW);
			ass.goSimulation();
		}
	}
	
	public void runXCommands() {
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
			ass.resetSimulation();
		}
		//ass.killSimulation();
	}
	
	public void backwardOneTransaction() {
		previousTime = System.currentTimeMillis();
		ass.backOneTransactionBunch(); 
	}
	
	
	public void mouseClicked(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e){
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
		//TraceManager.addDev("# of Pending transactions: " + ass.getPendingTransitions().size());
		setAll();
		
		
		// Diagram animation?
		if (!(busyMode == AvatarSpecificationSimulation.GATHER) && !(busyMode == AvatarSpecificationSimulation.EXECUTE)) {
			updateMetElements();
			updateTransactionsTable();
			animateDiagrams();
		}
		
		animateFutureTransactions();
		
	}
	
	public void updateTransactionAndTime(int _nbOfTransactions, long clockValue) {
		long timeNow = System.currentTimeMillis();
		if (timeNow - previousTime > SPACE_UPDATE_TIME) {
			previousTime = timeNow;
			setLabelColors();
		}
	}
	
	public void setAll() {
		boolean b= true;
		
		switch(busyMode) {
		case AvatarSpecificationSimulation.DONT_EXECUTE:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		case AvatarSpecificationSimulation.GATHER:
		case AvatarSpecificationSimulation.EXECUTE:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(true);
			b = false;
			break;
		case AvatarSpecificationSimulation.TERMINATED:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		case AvatarSpecificationSimulation.INITIALIZE:
		case AvatarSpecificationSimulation.RESET:
		case AvatarSpecificationSimulation.KILLED:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		}
		
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_SD_PNG].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_HTML].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_TXT].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_PRINT_BENCHMARK].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_BENCHMARK].setEnabled(b);
		//actions[AvatarInteractiveSimulationActions.ACT_SAVE_STATE].setEnabled(b);
		//actions[AvatarInteractiveSimulationActions.ACT_RESTORE_STATE].setEnabled(b);
		
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
		
	}
	
	public void animateFutureTransactions() {
		setContentOfListOfPendingTransactions();
	}
	
	public void setLabelColors() {
		if ((time !=null) && (status != null) && (info != null)) {
			String oldTime = time.getText();
			int index = oldTime.indexOf("(");
			if (index != -1) {
				oldTime = oldTime.substring(0, index).trim();
			}
			String newTime = ""+ass.getClockValue();
			if (oldTime.compareTo(newTime) != 0) {
				newTime += " (before:" + oldTime + ")";
			}
			time.setText(newTime);
			if (ass.getAllTransactions() != null) {
				info.setText(""+ass.getAllTransactions().size());
			} else {
				info.setText("0");
			}
			switch(busyMode) {
			case AvatarSpecificationSimulation.DONT_EXECUTE:
				status.setText("Stopped");
				status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				break;
			case AvatarSpecificationSimulation.GATHER:
			case AvatarSpecificationSimulation.EXECUTE:
			case AvatarSpecificationSimulation.RESET:
			case AvatarSpecificationSimulation.INITIALIZE:
				status.setText("Running...");
				status.setForeground(ColorManager.InteractiveSimulationText_BUSY);
				time.setForeground(ColorManager.InteractiveSimulationText_BUSY);
				info.setForeground(ColorManager.InteractiveSimulationText_BUSY);
				break;        
			case AvatarSpecificationSimulation.TERMINATED:
				status.setText("Terminated");
				status.setForeground(ColorManager.InteractiveSimulationText_TERM);
				time.setForeground(ColorManager.InteractiveSimulationText_TERM);
				info.setForeground(ColorManager.InteractiveSimulationText_TERM);
				break;
			case AvatarSpecificationSimulation.KILLED:
				status.setText("killed");
				status.setForeground(ColorManager.InteractiveSimulationText_TERM);
				time.setForeground(ColorManager.InteractiveSimulationText_TERM);
				info.setForeground(ColorManager.InteractiveSimulationText_TERM);
				break;
			}
		}
	}
	
	public void setContentOfListOfPendingTransactions() {
		EventQueue.invokeLater(this);
	}
	
	public void resetMetElements() {
		if (avspec.getReferenceObject() instanceof AvatarDesignPanel) {
			((AvatarDesignPanel)(avspec.getReferenceObject())).resetMetElements();
			
		}
	}
	
	public void updateMetElements() {
		LinkedList<AvatarStateMachineElement> allExecutedElements = AvatarSimulationTransaction.allExecutedElements;
		TGComponent tgc;
		Object o;
		
		if (allExecutedElements == null) {
			nbOfAllExecutedElements = 0;
			return;
		}
		
		if (allExecutedElements.size() > nbOfAllExecutedElements) {
			for(int i=nbOfAllExecutedElements; i<allExecutedElements.size(); i++) {
				o = allExecutedElements.get(i).getReferenceObject();
				if (o instanceof TGComponent) {
					tgc = (TGComponent)o;
					tgc.setAVATARMet(true);
				}
			}
		}
		nbOfAllExecutedElements = allExecutedElements.size();
	}
	
	public void updateTransactionsTable() {
		if (transactiontm != null) {
			transactiontm.fireTableStructureChanged();
		}
	}
	
	public void animateDiagrams() {
		if ((animate != null) && (ass != null) && (ass.getSimulationBlocks() != null)) {
			if (animate.isSelected()) {
				// We go through all blocks
				runningTGComponents.clear();
				AvatarStateMachineElement asme;
				TGComponent tgc;
				for(AvatarSimulationBlock block: ass.getSimulationBlocks()) {
					asme = block.getCurrentAvatarElement();
					if (asme != null) {
						// Search for corresponding element in avatar spec
						tgc = (TGComponent)(asme.getReferenceObject());
						if (tgc != null) {
							//TraceManager.addDev("Found an object:" + tgc);
							runningTGComponents.add(tgc);
						}
					}
				}
				if (openDiagram.isSelected()) {
					if (ass.getPreviousBlock() != null) {
						mgui.openAVATARSMD(ass.getPreviousBlock().getName());
					}
				}
			}
			mgui.setAvatarAnimate(animate.isSelected());
		}
	}
	
	public boolean isRunningComponent(TGComponent _tgc) {
		if (isVisible()) {
			return runningTGComponents.contains(_tgc);
		}
		return false;
	}
	
	public boolean isSelectedComponentFromTransaction(TGComponent _tgc) {
		if (isVisible()) {
			return _tgc == selectedComponentForTransaction;
		}
		return false;
	}
	
	public void actSaveTxt() {
		ass.printExecutedTransactions();
	}
	
	public void actSaveSDPNG() {
		//Saving PNG file;
		BufferedImage bi;
		File file;
		
		bi = sdpanel.performCapture();
		
		String filePath="";
		if (ConfigurationTTool.IMGPath != null) {
			filePath += ConfigurationTTool.IMGPath;
			if (!filePath.endsWith(File.separator)) {
				filePath += File.separator;
			}
		}
		
		if ((saveFileName.getText() != null) && (saveFileName.getText().length() > 0)) {
			filePath += saveFileName.getText();
		} else {
			filePath += "foo.png";
		}
		
		file = new File(filePath);
		
		mgui.writeImageCapture(bi, file, true);
	}
	
	
	// Mouse management
	public void mouseReleased(MouseEvent e) {}
	
	
	
	/**
	* This adapter is constructed to handle mouse over	component events.
	*/
    private class MouseHandler extends MouseAdapter  {
        
        private	JLabel label;
        
        /**
		* ctor	for the	adapter.
		* @param label	the JLabel which will recieve value of the
		*		Action.LONG_DESCRIPTION	key.
		*/
        public MouseHandler(JLabel label)  {
            setLabel(label);
        }
        
        public void setLabel(JLabel label)  {
            this.label = label;
        }
        
        public void mouseEntered(MouseEvent evt)  {
            if (evt.getSource()	instanceof AbstractButton)  {
                AbstractButton button =	(AbstractButton)evt.getSource();
                Action action =	button.getAction();
                if (action != null)  {
                    String message = (String)action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//TraceManager.addDev("Command:" + command);
		
		if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].getActionCommand()))  {
			runSimulation();
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].getActionCommand()))  {
			runXCommands();
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].getActionCommand()))  {
			stopSimulation();
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].getActionCommand()))  {
			backwardOneTransaction();
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].getActionCommand()))  {
			resetSimulation();
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand()))  {
			close();
			return;
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_TXT].getActionCommand()))  {
			actSaveTxt();
			return;
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_SD_PNG].getActionCommand()))  {
			actSaveSDPNG();
			return;
			//TraceManager.addDev("Start simulation!");
		}
	}
	
	public void error(String error) {
		jta.append("error: " + error + "\n");
	}
	
	public void valueChanged(ListSelectionEvent e) {
		int index = listPendingTransactions.getSelectedIndex();
		//TraceManager.addDev("Selected index = " +  index);
		if (index > -1) {
			try {
				AvatarSimulationPendingTransaction aspt = (AvatarSimulationPendingTransaction)(listPendingTransactions.getSelectedValue());
				selectedComponentForTransaction = (TGComponent)(aspt.elementToExecute.getReferenceObject());
				if ((selectedComponentForTransaction == null) && (aspt.linkedTransaction != null)) {
					//TraceManager.addDev("Adding reference object: " + aspt.linkedTransaction.elementToExecute.getReferenceObject());
					selectedComponentForTransaction = (TGComponent)(aspt.linkedTransaction.elementToExecute.getReferenceObject());
				}
				if (!(busyMode == AvatarSpecificationSimulation.GATHER) && !(busyMode == AvatarSpecificationSimulation.EXECUTE)) {
					ass.setIndexSelectedTransaction(listPendingTransactions.getSelectedIndex());
				}
				if (animate.isSelected() && (openDiagram.isSelected())) {
					if (aspt.asb != null) {
						previousBlock = aspt.asb;
						mgui.openAVATARSMD(previousBlock.getName());
					}
				}
			} catch (Exception ex){
				TraceManager.addDev("Exception selected component");
				selectedComponentForTransaction = null;
				if (previousBlock != null) {
					mgui.openAVATARSMD(previousBlock.getName());
				}
			}
		} else {
			selectedComponentForTransaction = null;
			if (previousBlock != null) {
				mgui.openAVATARSMD(previousBlock.getName());
			}
		}
		
	}
	

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == animate) {
			TraceManager.addDev("Animate is ... " + animate.isSelected());
			animateDiagrams();
			diploids.setEnabled(animate.isSelected());
			animateWithInfo.setEnabled(animate.isSelected());
			openDiagram.setEnabled(animate.isSelected());
		} else if (e.getSource() == diploids) {
			mgui.setAVATARIDs(diploids.isSelected());
		} else if (e.getSource() == executeEmptyTransition) {
			ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());
		} else if (e.getSource() == executeStateEntering) {
			ass.setExecuteStateEntering(executeStateEntering.isSelected());
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
