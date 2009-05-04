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

package ui.window;

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

import launcher.*;
import remotesimulation.*;


public	class JFrameInteractiveSimulation extends JFrame implements ActionListener, Runnable, MouseListener/*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	
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
	
	// Text commands
	protected JTextField textCommand;
	protected JButton sendTextCommand, printHelpTextCommands, listTextCommands;
	//private static String sendTextCommandS = "Send Command";
	
	// Control command
	protected JButton resetCommand, runCommand, StopCommand;
	
	JPanel main, mainTop, commands, infos, outputs; // from MGUI
	JTabbedPane commandTab, infoTab;
	
	private int mode = 0;
	
	//shortest paths
	//JComboBox combo1, combo2, combo3, combo4;
	//JTextField combo1, combo2, combo3, combo4;
	//JTextField text1, text2;
	//JButton goPath, goPathL, savePath, savePathL;
	
	
	public JFrameInteractiveSimulation(Frame _f, MainGUI _mgui, String _title, String _hostSystemC, String _pathExecute) {
		super(_title);
		
		f = _f;
		mgui = _mgui;
		title = _title;
		hostSystemC = _hostSystemC;
		pathExecute = _pathExecute;
		
		mode = NOT_STARTED;
		
		makeComponents();
		setComponents();
	}
	
	public void makeComponents() {
		JPanel jp01;
		//jp01.setPreferredSize(new Dimension(375, 400));
        GridBagLayout gridbag01;
        GridBagConstraints c01 ;
		
		cp = new CommandParser();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container framePanel = getContentPane();
		framePanel.setLayout(new BorderLayout());
		
		//System.out.println("Button start created");
		buttonStart = new JButton(buttonStartS, IconManager.imgic53);
		buttonStart.addActionListener(this);
		buttonClose = new JButton(buttonCloseS, IconManager.imgic27);
		buttonClose.addActionListener(this);
		buttonStopAndClose = new JButton(buttonStopAndCloseS, IconManager.imgic27);
		buttonStopAndClose.addActionListener(this);
		
		JPanel jp = new JPanel();
		jp.add(buttonStart);
		jp.add(buttonStopAndClose);
		jp.add(buttonClose);
		framePanel.add(jp, BorderLayout.SOUTH);
		
		
		GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
		mainTop = new JPanel(gridbag02);
		mainTop.setPreferredSize(new Dimension(800, 475));
		c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = 1; 
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;
		
		// Ouput textArea
		jta = new ScrolledJTextArea();
		jta.setMinimumSize(new Dimension(800, 200));
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Click on start to start the simulator and connect to it\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTop, jsp);
        framePanel.add(split, BorderLayout.CENTER);
		
		// Commands
		commands = new JPanel();
		//commands.setPreferredSize(new Dimension(400, 450));
		commands.setBorder(new javax.swing.border.TitledBorder("Commands"));
		
		
		mainTop.add(commands, c02);
		
		commandTab = new JTabbedPane();
		
		// Control commands
		jp01 = new JPanel();
		//jp01.setPreferredSize(new Dimension(375, 400));
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
		
		commandTab.addTab("Control", null, jp01, "Main control commands");
		
		
		c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        //c01.gridwidth = 1;
        c01.fill = GridBagConstraints.BOTH;
        //c01.gridheight = 1;
		
		c01.gridheight = 1;
		jp01.add(new JLabel("  "), c01);
		c01.gridheight = 1;
		resetCommand = new JButton(IconManager.imgic45);
		//resetCommand.setPreferredSize(new Dimension(35, 35));
		resetCommand.addMouseListener(this);
		jp01.add(resetCommand, c01);
		StopCommand = new JButton(IconManager.imgic55);
		StopCommand.addMouseListener(this);
		jp01.add(StopCommand, c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		runCommand = new JButton(IconManager.imgic53);
		runCommand.addMouseListener(this);
		jp01.add(runCommand, c01);
		c01.gridheight = 1;
		jp01.add(new JLabel(" "), c01);
		
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
		
		
		//Info
		infos = new JPanel();
		//infos.setPreferredSize(new Dimension(400, 450));
		infos.setBorder(new javax.swing.border.TitledBorder("Simulation results"));
		c02.gridwidth = GridBagConstraints.REMAINDER; //end row
		mainTop.add(infos, c02);
		
		infoTab = new JTabbedPane();
		infos.add(infoTab);
		
		// Simulation time
		jp01 = new JPanel();
		//jp01.setPreferredSize(new Dimension(375, 400));
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
		
		infoTab.addTab("Time", null, jp01, "Current time of the simulation");
		
		
		// Information
		/*TURTLEPanel tp;
		int i, j;
		TDiagramPanel tdp;
		RequirementDiagramPanel rdp;
		LinkedList<Requirement> all, list;
		all = new LinkedList<Requirement>();
		String title;
		String maintitle;
		
		for(i=0; i<tabs.size(); i++) {
			tp = (TURTLEPanel)(tabs.elementAt(i));
			maintitle = main.getTitleAt(i);
			if (tp instanceof RequirementPanel) {
				for(j=0; j<tp.panels.size(); j++) {
					if (tp.panels.elementAt(j) instanceof RequirementDiagramPanel) {
						rdp = (RequirementDiagramPanel)(tp.panels.elementAt(j));
						list = rdp.getAllRequirements();
						all.addAll(list);
						
						title = maintitle + " / " + tp.tabbedPane.getTitleAt(j);
						
						makeJScrollPane(list, mainTabbedPane, title);
					}
				}
			}
		}
		
		makeJScrollPane(all, mainTabbedPane, "All requirements");*/
		
		
		
		pack();
		
		//System.out.println("Requirements computed");
	}
	
	/*private void makeJScrollPane(LinkedList<Requirement> list, JTabbedPane tab, String title) {
		RequirementsTableModel rtm = new RequirementsTableModel(list, pts);
		TableSorter sorterRTM = new TableSorter(rtm);
		JTable jtableRTM = new JTable(sorterRTM);
		sorterRTM.setTableHeader(jtableRTM.getTableHeader());
		
		for(int i=0; i<pts.length; i++) {
			((jtableRTM.getColumnModel()).getColumn(i)).setPreferredWidth((pts[i].y)*50);
		}
		jtableRTM.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane jspRTM = new JScrollPane(jtableRTM);
		jspRTM.setWheelScrollingEnabled(true);
		jspRTM.getVerticalScrollBar().setUnitIncrement(10);
		
		tab.addTab(title, IconManager.imgic13, jspRTM, title);
		
		atms.add(rtm);
		titles.add(title);
	}*/
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//System.out.println("Command:" + command);
		
		if (command.equals(buttonCloseS)) {
			close();
			return;
		} else if (command.equals(buttonStartS)) {
			setComponents();
			startSimulation();
			//System.out.println("Start simulation!");
		} else if (command.equals(buttonStopAndCloseS)) {
			killSimulator();
			close();
			return;
			//System.out.println("Start simulation!");
		}
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
		resetCommand.setEnabled(b);
		runCommand.setEnabled(b);
		StopCommand.setEnabled(b);
	}
	
	public void close() {
		if(mode == STARTING) {
			go = false;
		}
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
		
		t = new Thread(this);
		go = true;
		threadMode = 0;
		t.start();
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
		
		try {
			if (threadMode == 0) {
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
				setComponents();
				
				try {
					while(true) {
						testGo();
						s = rc.readOneLine();
						jta.append("\nFrom server: " + s + "\n");
					}
				} catch (RemoteConnectionException rce) {
					jta.append("Exception: " + rce.getMessage());
					jta.append("Could not read data from host: " + hostSystemC + ".... Aborting\n");
				}
			} else if (threadMode == 1) {
				try {
					while(true) {
						testGo();
						s = rshc.getDataFromProcess();
						jta.append("\nFrom launcher: " + s + "\n");
					}
				} catch (LauncherException le) {
					jta.append("Exception: " + le.getMessage() + "\n");
				}
			}
		} catch (InterruptedException ie) {
			jta.append("Interrupted\n");
		}
		
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
		t = new Thread(this);
		//go = true;
		threadMode = 1;
		t.start();
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
		} else if (e.getSource() == resetCommand) {
			sendCommand("reset");
		} else if (e.getSource() == runCommand) {
			sendCommand("run-to-next-breakpoint");
		} else if (e.getSource() == StopCommand) {
			sendCommand("stop");
		}
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
		}
	}
	
	protected void append(String info, String list) {
		jta.append("\n");
		jta.append(info + "\n");
		printSeparator();
		jta.append(list);
		jta.append("\n");
		printSeparator();
	}
	
	
	// Mouse management
        
	public void mouseReleased(MouseEvent e) {}
	
	
} // Class