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
	JCheckBox latex, debug, animate, diploids, update, openDiagram, animateWithInfo;
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

		setBackground(new Color(50, 40, 40, 200));
		
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
		ass.initialize();
		simulationThread = new Thread(this);
		simulationThread.start();
	}
	
	public void run() {
		resetThread = false;
		killThread = false;
		
		ass.runSimulation();
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
			ass.reset();
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
	
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTop, lowerPartPanel);
		split.setResizeWeight(0.5);
		//split.setBackground(ColorManager.InteractiveSimulationBackground);
		mainpanel.add(split, BorderLayout.CENTER);
		
		// Commands
		commands = new JPanel(new BorderLayout());
		//commands.setFloatable(true);
		//commands.setMinimumSize(new Dimension(300, 250));
		commands.setBorder(new javax.swing.border.TitledBorder("Commands"));
		
		
		mainTop.add(commands, c02);
		
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
		c02.gridwidth = GridBagConstraints.REMAINDER; //end row
		mainTop.add(infos, c02);
		
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
		killThread();
		ass.killSimulation();
		dispose();
		setVisible(false);
		runningTGComponents.clear();
		resetMetElements();
		mgui.setAvatarAnimate(false);
	}
	
	public void runSimulation() {
		ass.unstop();
	}
	
	public void runXCommands() {
		String txt = paramMainCommand.getText();
		int nb;
		try {
			nb = Math.max(1, Integer.decode(txt).intValue());
		} catch (Exception e) {
			nb = 1;
		}
		ass.setNbOfCommands(nb);
		ass.unstop();
	}
	
	public void stopSimulation() {
		ass.stopSimulation();
	}
	
	public void resetSimulation() {
		resetThread();
		ass.killSimulation();
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
		TraceManager.addDev("****************** mode set to " + busyMode);
		TraceManager.addDev("# of Pending transactions: " + ass.getPendingTransitions().size());
		setAll();
		
		
		// Diagram animation?
		if (!(busyMode == AvatarSpecificationSimulation.RUNNING)) {
			updateMetElements();
			animateDiagrams();
		}
		
		animateFutureTransactions();
		
	}
	
	public void setAll() {
		boolean b= true;
		
		switch(busyMode) {
		case AvatarSpecificationSimulation.STOPPED:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		case AvatarSpecificationSimulation.RUNNING:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(true);
			b = false;
			break;
		case AvatarSpecificationSimulation.TERMINATED:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		}
		
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_VCD].setEnabled(b);
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
			info.setText(""+ass.getAllTransactions().size());
			switch(busyMode) {
			case AvatarSpecificationSimulation.STOPPED:
				status.setText("Stopped");
				status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				break;
			case AvatarSpecificationSimulation.RUNNING:
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
			}
		}
	}
	
	public void setContentOfListOfPendingTransactions() {
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
	
	
	
	public void animateDiagrams() {
		if (animate != null) {
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
		}
	}
	
	public void error(String error) {
		jta.append("error: " + error + "\n");
	}
	
	public void valueChanged(ListSelectionEvent e) {
		int index = listPendingTransactions.getSelectedIndex();
		if (index > -1) {
			try {
				AvatarSimulationPendingTransaction aspt = (AvatarSimulationPendingTransaction)(listPendingTransactions.getSelectedValue());
				selectedComponentForTransaction = (TGComponent)(aspt.elementToExecute.getReferenceObject());
				if (!(busyMode == AvatarSpecificationSimulation.RUNNING)) {
					ass.setIndexSelectedTransaction(listPendingTransactions.getSelectedIndex());
				}
				if (animate.isSelected() && (openDiagram.isSelected())) {
					if (aspt.asb != null) {
						previousBlock = aspt.asb;
						mgui.openAVATARSMD(previousBlock.getName());
					}
				}
			} catch (Exception ex){
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
		} /*else if (e.getSource() == animateWithInfo) {
			mgui.setTransationProgression(animateWithInfo.isSelected());
		}*/
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
	
	
	/*private void printCPUs() {
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
			String tmp, tmp1;
			int index, index1;
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
			int index, index1;
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
	}*/
	
	/*private void updateVariableState(String _idvar, String _value) {
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
				//System.out.println("Searching for old row");
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
				if ((contdel != null) && (busName != null) && (busID != null)) {
					info += "; Cont. delay on " + busName + " (" + busID + "): " + contdel;
				}
				valueTable.put(i, info);
				//System.out.println("Searching for old row");
				row = (Integer)(rowTable.get(i)).intValue();
				cputm.fireTableCellUpdated(row, 2);
				mgui.addLoadInfo(i, getDouble(_utilization).doubleValue());
			} catch (Exception e) {
				TraceManager.addDev("Exception updateCPUState: " + e.getMessage() + " id=" + _id + " util=" + _utilization);
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
				//TraceManager.addDev("Searching for old row");
				row = rowTable.get(i).intValue();
				bustm.fireTableCellUpdated(row, 2);
				mgui.addLoadInfo(i, getDouble(_utilization).doubleValue());
			} catch (Exception e) {
				System.err.println("Exception updateBusState: " + e.getMessage());
			}
		}
	}*/
	
	/*public void askForUpdate() {
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
	}*/
	

	

		
		
		/*else if (command.equals(actions[InteractiveSimulationActions.ACT_RUN_SIMU].getActionCommand()))  {
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
			mgui.resetLoadID();
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
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_PRINT_CPUS].getActionCommand())) {
            printCPUs();
        } else if (command.equals(actions[InteractiveSimulationActions.ACT_PRINT_BUS].getActionCommand())) {
            printBuses();
        } */
	

	
	/*public boolean isAPositiveInt(String s) {
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
	
	public Double getDouble(String s) {
		try {
			return new Double(s);
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
		//TraceManager.addDev("Add breakpoint: " + _commandID);
		// Check whether that breakpoint is already listed or not
		for(Point p: points) {
			if (p.y == _commandID) {
				return;
			}
		}
		
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
	}*/
	
		/*protected void listTextCommands() {
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
	}*/
	
	/*protected void append(String info, String list) {
		jta.append("\n");
		jta.append(info + "\n");
		printSeparator();
		jta.append(list);
		jta.append("\n");
		printSeparator();
	}*/
	
	/*protected void analyzeServerAnswer(String s) {
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
		
	}*/
	
	/*protected boolean loadXMLInfoFromServer(String xmldata) {
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
			TraceManager.addError("Error when parsing server info:" + e.getMessage());
			return false;
		} catch (SAXException saxe) {
			TraceManager.addError("Error when parsing server info:" + saxe.getMessage());
			TraceManager.addError("xml:" + xmldata);
			return false;
		}
		return true;
		
	}
	
	protected boolean loadConfiguration(Node node1) {
		NodeList diagramNl = node1.getChildNodes();
		if (diagramNl == null) {
			return false;
		}
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
		
		int k;
		
		//System.out.println("toto0");
		
		try {
			for(int j=0; j<diagramNl.getLength(); j++) {
				//System.out.println("Ndes: " + j);
				node = diagramNl.item(j);
				
				if (node == null) {
					TraceManager.addDev("null node");
					return false;
				}
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					
					// Status
					if (elt.getTagName().compareTo(SIMULATION_GLOBAL) == 0) {
						
						nl = elt.getElementsByTagName("status");
						if ((nl != null) && (nl.getLength() > 0)) {
							node0 = nl.item(0);
							//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
							
							makeStatus(node0.getTextContent());
						}
						
						nl = elt.getElementsByTagName("brkreason");
						if ((nl != null) && (nl.getLength() > 0)) {
							node0 = nl.item(0);
							//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
							
							makeBrkReason(node0.getTextContent());
						}
						
						nl = elt.getElementsByTagName("simtime");
						if ((nl != null) && (nl.getLength() > 0)) {
							gotTimeAnswerFromServer = true;
							node0 = nl.item(0);
							//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
							time.setText(node0.getTextContent());
						}
						
						nl = elt.getElementsByTagName("msg");
						if ((nl != null) && (nl.getLength() > 0)) {
							node0 = nl.item(0);
							msg = node0.getTextContent();
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
						
					}
					
					if (hashOK) {
						if (elt.getTagName().compareTo(SIMULATION_TASK) == 0) {
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
									//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								}
								nl = elt.getElementsByTagName("starttime");
								if ((nl != null) && (nl.getLength() > 0)) {
									node0 = nl.item(0);
									startTime = node0.getTextContent();
									//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								}
								nl = elt.getElementsByTagName("finishtime");
								if ((nl != null) && (nl.getLength() > 0)) {
									node0 = nl.item(0);
									finishTime = node0.getTextContent();
									//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								}
								nl = elt.getElementsByTagName("transstarttime");
								if ((nl != null) && (nl.getLength() > 0)) {
									node0 = nl.item(0);
									transStartTime = node0.getTextContent();
									//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								}
								nl = elt.getElementsByTagName("transfinishtime");
								if ((nl != null) && (nl.getLength() > 0)) {
									node0 = nl.item(0);
									transFinishTime = node0.getTextContent();
									//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								}
								nl = elt.getElementsByTagName("nextcmd");
								if ((nl != null) && (nl.getLength() > 0)) {
									node0 = nl.item(0);
									nextCommand = node0.getTextContent();
									//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								}
								
							}
							
							//System.out.println("Got info on task " + id + " command=" + command);
							extime = null;
							nl = elt.getElementsByTagName("extime");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								extime =  node0.getTextContent();
							}
							
							state = null;
							nl = elt.getElementsByTagName("tskstate");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								state =  node0.getTextContent();
								//TraceManager.addDev("TASK STATE: " + state);
							}
							
							if ((id != null) && ((extime != null) || (state != null))) {
								updateTaskCyclesAndState(id, extime, state);
							}
							
							
							if ((id != null) && (command != null)) {
								if (nextCommand ==null) {
									nextCommand = "-1";
								}
								updateRunningCommand(id, command, progression, startTime, finishTime, nextCommand, transStartTime, transFinishTime, state);
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
						
						//System.out.println("toto1");
						
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
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							//System.out.println("toto12");
							nl = elt.getElementsByTagName("contdel");
							if ((nl != null) && (nl.getLength() > 0)) {
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
						
						//System.out.println("toto2");
						
						if (elt.getTagName().compareTo(SIMULATION_BUS) == 0) {
							id = null;
							name = null;
							command = null;
							id = elt.getAttribute("id");
							name = elt.getAttribute("name");
							nl = elt.getElementsByTagName("util");
							if ((nl != null) && (nl.getLength() > 0)) {
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
			TraceManager.addError("Exception in xml parsing " + e.getMessage() + " node= " + node1);
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
	}*/
	
	/*private void wrongHashCode() {
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
		
	}*/
	
	/*public synchronized void startThread(int mode) {
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
		TraceManager.addDev("thread started");
		threadStarted = true;
		notify();
	}
	
	public void makeBrkReason(String s) {
		info.setText(s);
	}*/
	
	/*public void makeStatus(String s) {
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
	}*/
	
	/*public void setLabelColors() {
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
		
		
	}*/
	
	
	
} // Class
