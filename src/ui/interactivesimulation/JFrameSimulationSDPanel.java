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
* Class JFrameSimulationSDPanel
* Creation: 26/05/2011
* version 1.0 26/05/2011
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


public	class JFrameSimulationSDPanel extends JFrame implements ActionListener {
	
	/*protected static final String SIMULATION_HEADER = "siminfo";
	protected static final String SIMULATION_GLOBAL = "global";
	protected static final String SIMULATION_TASK = "task";
	protected static final String SIMULATION_CPU = "cpu";
	protected static final String SIMULATION_BUS = "bus";
	protected static final String SIMULATION_COMMAND = "cmd";
	
	private static String buttonStartS = "Start simulator";
	private static String buttonCloseS = "Close";
	private static String buttonStopAndCloseS = "Stop simulator and close";
	
	private static int NOT_STARTED = 0;
	private static int STARTING = 1;
	private static int STARTED_NOT_CONNECTED = 2;
	private static int STARTED_AND_CONNECTED = 3;*/
    
    public InteractiveSimulationActions [] actions;
	
	private Frame f;
	private MainGUI mgui;
	private String title;
	//private String hostSystemC;
	//private String pathExecute;
	
    private static String[] unitTab = {"sec", "msec", "usec", "nsec"};
    private static int[] clockDivisers = {1000000000, 1000000, 1000, 1};
    protected JComboBox units;
    
	protected JButton buttonClose, buttonRefresh;
    protected JSimulationSDPanel sdpanel;
    protected JLabel status;
    //, buttonStart, buttonStopAndClose;
	//protected JTextArea jta;
	//protected JScrollPane jsp;
	
	public JFrameSimulationSDPanel(Frame _f, MainGUI _mgui, String _title) {
		super(_title);
		
		f = _f;
		mgui = _mgui;
		title = _title;
        
		initActions();
		makeComponents();
		//setComponents();
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
        
        // Top panel
        JPanel topPanel = new JPanel();
        buttonClose = new JButton(actions[InteractiveSimulationActions.ACT_STOP_ALL]);
        topPanel.add(buttonClose);
        topPanel.add(new JLabel(" time unit:"));
        units = new JComboBox(unitTab);
        units.setSelectedIndex(1);
        units.addActionListener(this);
        topPanel.add(units);
		buttonRefresh = new JButton(actions[InteractiveSimulationActions.ACT_REFRESH]);
        topPanel.add(buttonRefresh);
        framePanel.add(topPanel, BorderLayout.NORTH);
        
        // Simulation panel
        sdpanel = new JSimulationSDPanel(this);
        JScrollPane jsp	= new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sdpanel.setMyScrollPanel(jsp);
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        framePanel.add(jsp, BorderLayout.CENTER);
        
        // statusBar
        status = createStatusBar();
        framePanel.add(status, BorderLayout.SOUTH);
        
        // Mouse handler
        //mouseHandler = new MouseHandler(status);
        
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
    }
	
	
	
	public void close() {
		dispose();
		setVisible(false);
		
	}
	
	public void refresh() {
		if (sdpanel != null ){
			sdpanel.refresh();
		}
	}
    
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//TraceManager.addDev("Command:" + command);
		
		if (command.equals(actions[InteractiveSimulationActions.ACT_STOP_ALL].getActionCommand()))  {
			close();
			return;
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[InteractiveSimulationActions.ACT_REFRESH].getActionCommand()))  {
			refresh();
			return;
			//TraceManager.addDev("Start simulation!");
		} else if (evt.getSource() == units) {
            if (sdpanel != null) {
                switch(units.getSelectedIndex()) {
                case 0:
                    
                }
                sdpanel.setClockDiviser(clockDivisers[units.getSelectedIndex()]);
            }
        }
	}
    
    public void setFileReference(String _fileReference) {
        if (sdpanel != null) {
            TraceManager.addDev("Resetting file");
            sdpanel.setFileReference(_fileReference);
        }
    }
	
	public void setCurrentTime(long timeValue) {
		status.setText("time = " + timeValue);
	}
	
	public void setStatus(String _status) {
		status.setText(_status);
	}
	
	public void setNbOfTransactions(int x, long minTime, long maxTime) {
		status.setText("" + x + " transactions, min time=" + minTime + ", max time=" + maxTime);
	}
	
    
	
	
} // Class
