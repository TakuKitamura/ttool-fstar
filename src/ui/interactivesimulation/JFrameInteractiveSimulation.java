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
* Class JFrameInteractiveSimulation
* Creation: 21/04/2009
* version 1.0 21/04/2009
* @author Ludovic APVRILLE
* @see
*/

package ui.interactivesimulation;

//import java.io.*;
import javax.swing.*;
//import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


import myutil.*;
import ui.*;
import ui.file.*;

import tmltranslator.*; 

import launcher.*;
import remotesimulation.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;


public	class JFrameInteractiveSimulation extends JFrame implements ActionListener, Runnable, MouseListener, ItemListener/*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	
	protected static final String SIMULATION_HEADER = "siminfo";
	protected static final String SIMULATION_GLOBAL = "global";
	protected static final String SIMULATION_TASK = "task";
	protected static final String SIMULATION_CPU = "cpu";
	protected static final String SIMULATION_BUS = "bus";
	
	private static String buttonStartS = "Start simulator";
	private static String buttonCloseS = "Close";
	private static String buttonStopAndCloseS = "Stop simulator and close";
	
	private static int NOT_STARTED = 0;
	private static int STARTING = 1;
	private static int STARTED_NOT_CONNECTED = 2;
	private static int STARTED_AND_CONNECTED = 3;
	
	private Frame f;
	private MainGUI mgui;
	private String title;
	private String hostSystemC;
	private String pathExecute;
	
	protected JButton buttonClose, buttonStart, buttonStopAndClose;
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
	
	
	// Commands
	JPanel main, mainTop, commands, save, state, infos, outputs, cpuPanel, variablePanel; // from MGUI
	JCheckBox debug, animate, update, openDiagram;
	JTabbedPane commandTab, infoTab;
	protected JTextField paramMainCommand;
	protected JTextField saveFileName;
	protected JTextField stateFileName;
	protected JTextField benchmarkFileName;
	protected JComboBox cpus, busses, mems, tasks, chans;
	
	
	private String[] cpuIDs, busIDs, memIDs, taskIDs, chanIDs;
	
	// Status elements
	JLabel status, time;
	
	// Task elements
	TaskVariableTableModel tvtm;
	JButton updateTaskVariableInformationButton;
	private JScrollPane jspTaskVariableInfo;
	
	// Breakpoints
	JPanelBreakPoints jpbp;
	
	// Set variables
	JPanelSetVariables jpsv;
	
	// Tasks
	JPanel taskPanel;
	TaskTableModel tasktm;
	JButton updateTaskInformationButton;
	private JScrollPane jspTaskInfo;
	
	// CPU
	CPUTableModel cputm;
	JButton updateCPUInformationButton;
	private JScrollPane jspCPUInfo;
	
	// Memories
	JPanel memPanel;
	MemTableModel memtm;
	JButton updateMemoryInformationButton;
	private JScrollPane jspMemInfo;
	
	// Bus
	JPanel busPanel;
	BusTableModel bustm;
	JButton updateBusInformationButton;
	private JScrollPane jspBusInfo;
	
	
	private int mode = 0;
	//private boolean busyStatus = false;
	private int busyMode = 0; // 0: unknown; 1: ready; 2:busy; 3:term
	private boolean threadStarted = false;
	private boolean gotTimeAnswerFromServer = false; 
	
	// For managing actions
	public	InteractiveSimulationActions [] actions;
    public	MouseHandler mouseHandler;
    public  KeyListener keyHandler;
	
	private TMLMapping tmap;
	private int hashCode;
	private boolean hashOK = true;
	
	private Hashtable <Integer, String> valueTable;
	private Hashtable <Integer, Integer> rowTable;
	
	private Hashtable <Integer, Integer> runningTable;
	private Hashtable <String, String> diagramTable;
	
	private ArrayList<Point> points;
	
	public JFrameInteractiveSimulation(Frame _f, MainGUI _mgui, String _title, String _hostSystemC, String _pathExecute, TMLMapping _tmap, ArrayList<Point> _points) {
		super(_title);
		
		f = _f;
		mgui = _mgui;
		title = _title;
		hostSystemC = _hostSystemC;
		pathExecute = _pathExecute;
		
		mode = NOT_STARTED;
		
		tmap = _tmap;
		if (tmap != null) {
			tmap.makeMinimumMapping();
			hashCode = tmap.getHashCode();
		} else {
			hashOK = false;
		}
		
		points = _points;
		
		//System.out.println("Tmap=" + tmap);
		
		valueTable = new Hashtable<Integer, String>();
		rowTable = new Hashtable<Integer, Integer>();
		runningTable = new Hashtable<Integer, Integer>();
		diagramTable = new Hashtable<String, String>();
		
		
		mgui.resetRunningID();
		
		setBackground(new Color(50, 40, 40, 200));
		
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
	
	public void makeComponents() {
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
		
		//System.out.println("Button start created");
		buttonStart = new JButton(actions[InteractiveSimulationActions.ACT_START_ALL]);
		buttonClose = new JButton(actions[InteractiveSimulationActions.ACT_STOP_ALL]);
		buttonStopAndClose = new JButton(actions[InteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL]);
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
		//jp.setPreferredSize(new Dimension(800, 75));
		jp.add(buttonStart);
		jp.add(buttonStopAndClose);
		jp.add(buttonClose);
		mainpanel.add(jp, BorderLayout.SOUTH);
		
		
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
		jta.append("Click on \"Connect\" to start the remote simulator and connect to it\n");
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
		commands = new JPanel();
		//commands.setFloatable(true);
		//commands.setMinimumSize(new Dimension(300, 250));
		commands.setBorder(new javax.swing.border.TitledBorder("Commands"));
		
		
		mainTop.add(commands, c02);
		
		commandTab = new JTabbedPane();
		//commandTab.setBackground(ColorManager.InteractiveSimulationBackground);
		
		// Control commands
		jp01 = new JPanel(new BorderLayout());
		//jp01.setMinimumSize(new Dimension(375, 400));
		//gridbag01 = new GridBagLayout();
		//c01 = new GridBagConstraints();
		//jp01.setLayout(gridbag01);
		
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
		jp02.add(new JLabel("CPUs: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		if (cpuIDs == null) {
			cpus = new JComboBox();
		} else {
			cpus = new JComboBox(cpuIDs);
		}
		jp02.add(cpus, c01);
		
		c01.gridwidth = 1;
		jp02.add(new JLabel("Busses: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		if (busIDs == null) {
			busses = new JComboBox();
		} else {
			busses = new JComboBox(busIDs);
		}
		jp02.add(busses, c01);
		
		c01.gridwidth = 1;
		jp02.add(new JLabel("Memories: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		if (memIDs == null) {
			mems = new JComboBox();
		} else {
			mems = new JComboBox(memIDs);
		}
		jp02.add(mems, c01);
		
		c01.gridwidth = 1;
		jp02.add(new JLabel("Tasks: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		if (taskIDs == null) {
			tasks = new JComboBox();
		} else {
			tasks = new JComboBox(taskIDs);
		}
		jp02.add(tasks, c01);
		
		c01.gridwidth = 1;
		jp02.add(new JLabel("Channels: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		if (chanIDs == null) {
			chans = new JComboBox();
		} else {
			chans = new JComboBox(chanIDs);
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
		
		commands.add(commandTab);
		
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
		jp02.add(stateFileName, c01);
		
		jp01.add(jp02, BorderLayout.CENTER);
		
		
		//Info
		infos = new JPanel(new BorderLayout());
		infos.setMinimumSize(new Dimension(300, 250));
		//infos.setPreferredSize(new Dimension(400, 450));
		infos.setBorder(new javax.swing.border.TitledBorder("Simulation information"));
		c02.gridwidth = GridBagConstraints.REMAINDER; //end row
		mainTop.add(infos, c02);
		
		infoTab = new JTabbedPane();
		infoTab.setMinimumSize(new Dimension(300, 250));
		infos.add(infoTab, BorderLayout.NORTH);
		
		// Simulation time
		jp02 = new JPanel();
		infos.add(jp02, BorderLayout.SOUTH);
		jp02.add(new JLabel("Status:"));
		status = new JLabel("Unknown");
		status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
		jp02.add(status);
		jp02.add(new JLabel(" "));
		jp02.add(new JLabel("Time:"));
		time = new JLabel("Unknown");
		time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
		jp02.add(time);
		
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
		debug = new JCheckBox("Print messages received from server");
		jp01.add(debug, c01);
		animate = new JCheckBox("Animate UML diagrams");
		jp01.add(animate, c01);
		animate.addItemListener(this);
		animate.setSelected(true);
		update = new JCheckBox("Automatically update information (task, CPU, etc.)");
		jp01.add(update, c01);
		update.addItemListener(this);
		update.setSelected(true);
		openDiagram = new JCheckBox("Automatically open active task diagram");
		jp01.add(openDiagram, c01);
		openDiagram.setSelected(true);
		
		
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
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
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
		
		// CPUs
		cpuPanel = new JPanel();
		cpuPanel.setLayout(new BorderLayout());
		infoTab.addTab("CPUs", IconManager.imgic1100, cpuPanel, "Current state of CPUs");
		cputm = new CPUTableModel(tmap, valueTable, rowTable);
		sorterPI = new TableSorter(cputm);
		jtablePI = new JTable(sorterPI);
		sorterPI.setTableHeader(jtablePI.getTableHeader());
		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspCPUInfo = new JScrollPane(jtablePI);
		jspCPUInfo.setWheelScrollingEnabled(true);
		jspCPUInfo.getVerticalScrollBar().setUnitIncrement(10);
		jspCPUInfo.setPreferredSize(new Dimension(500, 300));
		cpuPanel.add(jspCPUInfo, BorderLayout.NORTH);
		updateCPUInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_CPUS]);
		cpuPanel.add(updateCPUInformationButton, BorderLayout.SOUTH);
		
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
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
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
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspBusInfo = new JScrollPane(jtablePI);
		jspBusInfo.setWheelScrollingEnabled(true);
		jspBusInfo.getVerticalScrollBar().setUnitIncrement(10);
		jspBusInfo.setPreferredSize(new Dimension(500, 300));
		busPanel.add(jspBusInfo, BorderLayout.NORTH);
		updateBusInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_BUS]);
		busPanel.add(updateBusInformationButton, BorderLayout.SOUTH);
		
		
		if (!hashOK) {
			wrongHashCode();
		}
		pack();
		
		//System.out.println("Row table:" + rowTable.toString());
		//System.out.println("Value table:" + valueTable.toString());
	}
	
	private	void initActions() {
        actions = new InteractiveSimulationActions[InteractiveSimulationActions.NB_ACTION];
        for(int	i=0; i<InteractiveSimulationActions.NB_ACTION; i++) {
            actions[i] = new InteractiveSimulationActions(i);
            actions[i].addActionListener(this);
            //actions[i].addKeyListener(this);
        }
		
		cpuIDs = makeCPUIDs();
		busIDs = makeBusIDs();
		memIDs = makeMemIDs();
		taskIDs = makeTasksIDs();
		chanIDs = makeChanIDs();
    }
	
	
	
	public void setComponents() {
		if (mode == NOT_STARTED) {
			buttonStart.setEnabled(true);
		} else {
			buttonStart.setEnabled(false);
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
		if(mode != NOT_STARTED)  {
			go = false;
			if (rc != null) {
				try {
					rc.disconnect();
				} catch (RemoteConnectionException rce) {
				}
				//System.out.println("Disconnected");
				rc = null;
			}
		}
		mgui.resetRunningID();
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
	
	public void run() {
		String s;
		System.out.println("mode=" + threadMode);
		
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
						mode = 	NOT_STARTED;
						setComponents();
						return;
					} catch (Exception e) {
						mode = 	NOT_STARTED;
						setComponents();
						return;
					}
					testGo();
					
					// Wait for the server to start
					Thread.currentThread().sleep(1000);
					
					//jta.append("Simulator started\n\n");
					jta.append("Connecting to simulation server ...\n");
					mode = STARTED_NOT_CONNECTED;
					if (!connect()) {
						jta.append("Could not connect to server... Aborting\n");
						mode = 	NOT_STARTED;
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
					//System.out.println("rc left");
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
					Thread.currentThread().sleep(500);
					if (busyMode == 2 && gotTimeAnswerFromServer) {
						gotTimeAnswerFromServer = false;
						askForUpdate();
						
					}
				}
			}
		} catch (InterruptedException ie) {
			jta.append("Interrupted\n");
		}
		
		//System.out.println("rc left threadMode=" + threadMode);
		
	}
	
	protected boolean connect() {
		try {
			rc.connect();
			return true;
		} catch (RemoteConnectionException rce) {
			return false;
		}
	}
	
	protected void processCmd(String cmd) throws LauncherException {
		rshc.setCmd(cmd);
		rshc.sendProcessRequest();
		startThread(1);
		//t = new Thread(this);
		////go = true;
		//threadMode = 1;
		//t.start();
	}
	
	public void mouseClicked(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
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
		//System.out.println("From server:" + s);
		int index0 = s.indexOf("<?xml");
		
		if (index0 != -1) {
			//System.out.println("toto1");
			ssxml = s.substring(index0, s.length()) + "\n";
		} else {
			//System.out.println("toto2");
			ssxml = ssxml + s + "\n";
		}
		
		index0 = ssxml.indexOf("</siminfo>");
		
		if (index0 != -1) {
			//System.out.println("toto3");
			ssxml = ssxml.substring(0, index0+10);
			loadXMLInfoFromServer(ssxml);
			ssxml = "";
		}
		//System.out.println("toto4");
		
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
				//System.out.println("Node = " + dnd);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// create design, and get an index for it
					return loadConfiguration(node);
				}
			}
			
		} catch (IOException e) {
			System.err.println("Error when parsing server info:" + e.getMessage());
			return false;
		} catch (SAXException saxe) {
			System.err.println("Error when parsing server info:" + saxe.getMessage());
			System.err.println("xml:" + xmldata);
			return false;
		}
		return true;
		
	}
	
	protected boolean loadConfiguration(Node node1) {
		NodeList diagramNl = node1.getChildNodes();
		Element elt, elt0;
		Node node, node0;
		NodeList nl;
		
		
		String tmp;
		int val;
		
		int[] colors;
		String msg = null;
		String error = null;
		String hash = null;
		
		String id, idvar;
		String name;
		String command;
		String util = null;
		String value;
		String extime;
		String contdel;
		String busname;
		String busid;
		
		int k;
		
		try {
			for(int j=0; j<diagramNl.getLength(); j++) {
				//System.out.println("Ndes: " + j);
				node = diagramNl.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					
					// Status
					if (elt.getTagName().compareTo(SIMULATION_GLOBAL) == 0) {
						
						nl = elt.getElementsByTagName("status");
						if (nl.getLength() > 0) {
							node0 = nl.item(0);
							//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
							
							makeStatus(node0.getTextContent());
						}
						
						nl = elt.getElementsByTagName("simtime");
						if (nl.getLength() > 0) {
							gotTimeAnswerFromServer = true;
							node0 = nl.item(0);
							//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
							time.setText(node0.getTextContent());
						}
						
						nl = elt.getElementsByTagName("msg");
						if (nl.getLength() > 0) {
							node0 = nl.item(0);
							msg = node0.getTextContent();
						}
						
						nl = elt.getElementsByTagName("error");
						if (nl.getLength() > 0) {
							node0 = nl.item(0);
							error = node0.getTextContent();
						}
						
						nl = elt.getElementsByTagName("hashval");
						if (nl.getLength() > 0) {
							node0 = nl.item(0);
							hash = node0.getTextContent();
						}
						
					}
					
					if (hashOK) {
						if (elt.getTagName().compareTo(SIMULATION_TASK) == 0) {
							id = null;
							name = null;
							command = null;
							id = elt.getAttribute("id");
							name = elt.getAttribute("name");
							nl = elt.getElementsByTagName("currcmd");
							if (nl.getLength() > 0) {
								node0 = nl.item(0);
								if (node0.getNodeType() == Node.ELEMENT_NODE) {
									elt0 = (Element)node0;
									command = elt0.getAttribute("id");
								}
							}
							
							//System.out.println("Got info on task " + id + " command=" + command);
							
							if ((id != null) && (command != null)) {
								updateRunningCommand(id, command);
							}
							
							if (openDiagram.isEnabled() && openDiagram.isSelected() && (name != null) && (command != null)) {
								updateOpenDiagram(name, command);
							}
							
							extime = null;
							nl = elt.getElementsByTagName("extime");
							if (nl.getLength() > 0) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								extime =  node0.getTextContent();
							}
							
							if ((id != null) && (extime != null)) {
								updateTaskState(id, extime);
							}
							
							
							
							nl = elt.getElementsByTagName("var");
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
						
						if (elt.getTagName().compareTo(SIMULATION_CPU) == 0) {
							id = null;
							name = null;
							command = null;
							contdel = null;
							busname = null;
							busid = null;
							
							id = elt.getAttribute("id");
							name = elt.getAttribute("name");
							nl = elt.getElementsByTagName("util");
							if (nl.getLength() > 0) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							//System.out.println("Got info on cpu " + id + " util=" + util);
							
							nl = elt.getElementsByTagName("util");
							if (nl.getLength() > 0) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							
							if (nl.getLength() > 0) {
								nl = elt.getElementsByTagName("contdel");
								node0 = nl.item(0);
								elt0 = (Element)node0;
								busid = elt0.getAttribute("busID");
								busname = elt0.getAttribute("busName");
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								contdel = node0.getTextContent();
							}
							
							//System.out.println("contdel: " + contdel + " busID:" + busid + " busName:" + busname);
							
							
							if ((id != null) && (util != null)) {
								updateCPUState(id, util, contdel, busname, busid);
							}
						}
						
						if (elt.getTagName().compareTo(SIMULATION_BUS) == 0) {
							id = null;
							name = null;
							command = null;
							id = elt.getAttribute("id");
							name = elt.getAttribute("name");
							nl = elt.getElementsByTagName("util");
							if (nl.getLength() > 0) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							//System.out.println("Got info on bus " + id + " util=" + util);
							
							if ((id != null) && (util != null)) {
								updateBusState(id, util);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Exception " + e.getMessage());
			return false;
		}
		
		if ((msg != null) && (error != null)) {
			if (error.trim().equals("0")) {
				printFromServer(msg + ": command successful");
				if (msg.indexOf("reset") != -1) {
					time.setText("0");
				}
			} else {
				printFromServer(msg + ": command failed (error=" + error + ")");
			}
		} else if (msg != null) {
			printFromServer("Server: " + msg);
		} else {
			printFromServer("Server: error " + error);
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
		System.out.println("Wrong hash code");
		
		cpuPanel.setVisible(false);
		variablePanel.setVisible(false);
		openDiagram.setSelected(false);
		openDiagram.setEnabled(false);
		animate.setEnabled(false);
		animate.setSelected(false);
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
		//System.out.println("thread of mode:" + threadMode);
		while(threadStarted == false) {
			try {
				wait();
			} catch (InterruptedException ie) {}
		}
	}
	
	public synchronized void threadStarted() {
		System.out.println("thread started");
		threadStarted = true;
		notify();
	}
	
	public void makeStatus(String s) {
		//System.out.println("busystatus="  + busyStatus);
		
		if (s.equals("busy")) {
			status.setText("Busy");
			setBusyStatus();
			busyMode = 2;
			//busyStatus = true;
		}
		if (s.equals("ready")) {
			status.setText("Ready");
			if (busyMode == 2) {
				//System.out.println("Sending time command");
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
			
			//System.out.println("**** TERM ****");
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
			break;
		case 1:
			status.setForeground(ColorManager.InteractiveSimulationText_READY);
			time.setForeground(ColorManager.InteractiveSimulationText_READY);
			break;        
		case 2:
			status.setForeground(ColorManager.InteractiveSimulationText_BUSY);
			time.setForeground(ColorManager.InteractiveSimulationText_BUSY);
			break;
		case 3:
			status.setForeground(ColorManager.InteractiveSimulationText_TERM);
			time.setForeground(ColorManager.InteractiveSimulationText_TERM);
			break;
		}
		
		
	}
	
	public void setAll() {
		boolean b = false;;
		if (busyMode == 1) {
			b = true;
		}
		actions[InteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_RUN_X_TIME_UNITS].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_RUN_TO_TIME].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_RUN_X_TRANSACTIONS].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_RUN_EXPLORATION].setEnabled(b);
		
		if (jpsv != null) {
			jpsv.setVariableButton(b);
		}
		
		if(busyMode == 3) {
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
		
		if((busyMode == 0) || (busyMode == 2)) {
			b = false;
		} else {
			b = true;
		}
		
		actions[InteractiveSimulationActions.ACT_SAVE_VCD].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_SAVE_HTML].setEnabled(b);
		actions[InteractiveSimulationActions.ACT_SAVE_TXT].setEnabled(b);
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
	
	public void printFromServer(String s) {
		jta.append("Server> " + s + "\n");
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
	
	public void sendCommandWithPositiveInt(String command) {
		String param = paramMainCommand.getText().trim();
		if (isAPositiveInt(param)) {
			sendCommand(command + " " + param);
		} else {
			error("Wrong parameter: must be a positive int"); 
		}
	}
	
	public void sendSaveTraceCommand(String format) {
		String param = saveFileName.getText().trim();
		if (param.length() >0) {
			sendCommand("save-trace-in-file" + " " + format + " " + param);
		} else {
			error("Wrong parameter: must be a file name"); 
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
			sendCommand("getbenchmark 0 " + param);
		} else {
			error("Wrong parameter: must be a file name"); 
		}
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
			if (node instanceof HwCPU) {
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
	
	private void updateRunningCommand(String id, String command) {
		Integer i = getInteger(id);
		Integer c = getInteger(command);
		
		if ((i != null) && (c != null)) {
			try {
				//System.out.println("Searching for old value");
				Integer old = runningTable.get(i);
				if(old != null) {
					mgui.removeRunningId(old);
					runningTable.remove(old);
				}
				
				runningTable.put(i, c);
				//System.out.println("Adding running command");
				mgui.addRunningID(c);
			} catch (Exception e) {
				System.out.println("Exception updateRunningCommand: " + e.getMessage());
			}
		}
		
	}
	
	private void updateOpenDiagram(String name, String command) {
		if (tmap == null) {
			return;
		}
		
		String cmd = diagramTable.get(name);
		if (cmd == null) {
			diagramTable.put(name, command);
			return;
		}
		
		if (!(cmd.equals(command))) {
			diagramTable.remove(name);
			diagramTable.put(name, command);
			
			String diag = "";
			String tab = name;
			int index = tab.indexOf("__");
			if (index != -1) {
				diag = tab.substring(0, index);
				tab = tab.substring(index+2, tab.length());
			}
			//System.out.println("Opening diagram " + tab);
			
			mgui.openTMLTaskActivityDiagram(diag, tab);
		}
	}
	
	private void updateVariableState(String _idvar, String _value) {
		Integer i = getInteger(_idvar);
		int row;
		
		if (i != null) {
			try {
				valueTable.remove(i);
				valueTable.put(i, _value);
				//System.out.println("Searching for old row");
				row = (Integer)(rowTable.get(i)).intValue();
				tvtm.fireTableCellUpdated(row, 4);
			} catch (Exception e) {
				System.out.println("Exception updateVariableState: " + e.getMessage() + " idvar=" + _idvar + " val=" + _value);
			}
		}
		
	}
	
	private void updateTaskState(String _id, String _extime) {
		Integer i = getInteger(_id);
		Integer ex = getInteger(_extime);
		int row;
		
		if ((i != null) && (ex != null)) {
			try {
				valueTable.remove(i);
				valueTable.put(i, "nbOfCycles: " + _extime);
				//System.out.println("Searching for old row");
				row = rowTable.get(i).intValue();
				tasktm.fireTableCellUpdated(row, 2);
			} catch (Exception e) {
				System.out.println("Exception updateTaskState: " + e.getMessage());
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
				if ((contdel != null) && (busName != null) && (busID != null)) {
					info += "; Cont. delay on " + busName + " (" + busID + ") = " + contdel;
				}
				valueTable.put(i, info);
				//System.out.println("Searching for old row");
				row = (Integer)(rowTable.get(i)).intValue();
				cputm.fireTableCellUpdated(row, 2);
			} catch (Exception e) {
				System.out.println("Exception updateCPUState: " + e.getMessage() + " id=" + _id + " util=" + _utilization);
			}
		}
	}
	
	private void updateBusState(String _id, String _utilization) {
		Integer i = getInteger(_id);
		int row;
		
		if (i != null) {
			try {
				valueTable.remove(i);
				valueTable.put(i, "Utilization: " + _utilization);
				//System.out.println("Searching for old row");
				row = rowTable.get(i).intValue();
				bustm.fireTableCellUpdated(row, 2);
			} catch (Exception e) {
				System.err.println("Exception updateBusState: " + e.getMessage());
			}
		}
	}
	
	public void askForUpdate() {
		sendCommand("time");
		if (hashOK) {
			if (animate.isSelected()) {
				updateTaskCommands();
			}
			if (update.isSelected()) {
				updateTasks();
				updateVariables();
				updateCPUs();
				updateBus();
			}
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == animate) {
			mgui.setDiploIDs(animate.isSelected());
		}
	}
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//System.out.println("Command:" + command);
		
		if (command.equals(actions[InteractiveSimulationActions.ACT_STOP_ALL].getActionCommand()))  {
            close();
        }  else if (command.equals(actions[InteractiveSimulationActions.ACT_START_ALL].getActionCommand()))  {
			setComponents();
			startSimulation();
			//System.out.println("Start simulation!");
		} else if (command.equals(actions[InteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand()))  {
			killSimulator();
			close();
			return;
			//System.out.println("Start simulation!");
		} else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_SIMU].getActionCommand()))  {
            sendCommand("run-to-next-breakpoint");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_X_TIME_UNITS].getActionCommand()))  {
            sendCommandWithPositiveInt("run-x-time-units");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_TO_TIME].getActionCommand()))  {
            sendCommandWithPositiveInt("run-to-time");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_X_TRANSACTIONS].getActionCommand()))  {
            sendCommandWithPositiveInt("run-x-transactions");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_X_COMMANDS].getActionCommand()))  {
            sendCommandWithPositiveInt("run-x-commands");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_EXPLORATION].getActionCommand()))  {
            sendCommand("run-exploration");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_TO_NEXT_BUS_TRANSFER].getActionCommand()))  {
            toNextBusTransfer();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CPU_EXECUTES].getActionCommand()))  {
            runUntilCPUExecutes();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_MEMORY_ACCESS].getActionCommand()))  {
            toNextMemoryTransfer();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_TASK_EXECUTES].getActionCommand()))  {
            runUntilTaskExecutes();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_UNTIL_CHANNEL_ACCESS].getActionCommand()))  {
            runUntilChannelAccess();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_VCD].getActionCommand()))  {
            sendSaveTraceCommand("0");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_HTML].getActionCommand()))  {
            sendSaveTraceCommand("1");
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_SAVE_TXT].getActionCommand()))  {
            sendSaveTraceCommand("2");
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
            sendCommand("reset");
			askForUpdate();
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
        }
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
		if (val > -1) {
			return true;
		}
		return false;
	}
	
	public Integer getInteger(String s) {
		try {
			return Integer.decode(s);
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
	
	
	
	public void addBreakPoint(int _commandID) {
		//System.out.println("Add breakpoint: " + _commandID);
		// Check whether that breakpoint is already listed or not
		for(Point p: points) {
			if (p.y == _commandID) {
				return;
			}
		}
		
		if (tmap != null) {
			TMLTask task = tmap.getTMLTaskByCommandID(_commandID);
			//System.out.println("Got task: " + task);
			if (task != null) {
				//System.out.println("Adding bkp");
				sendCommand("add-breakpoint " + task.getID() + " " + _commandID + "\n");
				jpbp.addExternalBreakpoint(task.getID(), _commandID);
			}
		}
	}
	
	public void removeBreakPoint(int _commandID) {
		System.out.println("remove breakpoint");
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
			sendCommand("add-breakpoint " + p.x + " " + p.y + "\n");
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
		
		return tmap.getCPUIDs();
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
	
	
} // Class