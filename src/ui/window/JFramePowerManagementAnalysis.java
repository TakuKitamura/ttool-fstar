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
* Class JFramePowerManagementAnalysis
* Creation: 13/07/2009
* version 1.0 13/07/2009
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
//import java.util.*;
import ui.graph.*;


import myutil.*;
import ui.*;


public	class JFramePowerManagementAnalysis extends JFrame implements ActionListener, StoppableGUIElement, Runnable {
	private static String START_STRING = "Select options and then, click on 'start' to analyze power consumption and generate a VCD trace\n";
	private static int NOT_STARTED = 0;
	private static int STARTED = 1;
	
    private String data;
	
    //private JScrollPane jsp;
    private JTextField state;
    private JTextField transition;
	private JTextField simulationTime;
	private JTextField tickInfoText;
	private JTextField coreInfoText;
	private JTextField taskInfoText;
	private JTextField infoText;
	
	private JTextField pcInLowMode;
	private JTextField pcInLowToHighMode;
	private JTextField pcInHighToLowMode;   
	private JTextField pcInHighMode;
	
	private JComboBox simulationAlgorithm;
	
	protected JButton start;
    protected JButton stop;
    protected JButton close;
	
	protected JTextArea jta;
	protected JScrollPane jsp;
	
	protected AUTGraph graph;
	protected VCDGenerator generator;
	protected JDialogCancel jdc;
	protected boolean finished;
	
	
	protected int mode;
	protected Thread t;
    
    public JFramePowerManagementAnalysis(String title, String dataAUT) {
        super(title);
        data = dataAUT;
		mode = NOT_STARTED;
        makeComponents();
    }
    
    public void makeComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        
        graph = new AUTGraph();
        graph.buildGraph(data);
		
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append(START_STRING);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        framePanel.add(jsp, BorderLayout.CENTER);
        
        // Buttons
		
        JPanel jp = new JPanel();
		start = new JButton("Start", IconManager.imgic53);
        start.addActionListener(this);
        jp.add(start);
		stop = new JButton("Stop", IconManager.imgic55);
        stop.addActionListener(this);
        jp.add(stop);
        close = new JButton("Close", IconManager.imgic27);
        close.addActionListener(this);
        jp.add(close);
        framePanel.add(jp, BorderLayout.SOUTH);
        
		// Other inputs
		JPanel jp1 = new JPanel(new BorderLayout());
		
        // upper information
        Point p = FormatManager.nbStateTransitionRGAldebaran(data);
        jp = new JPanel();
        
        jp.add(new JLabel("States:"));
        state = new JTextField(5);
        state.setEditable(false);
        state.setText(String.valueOf(p.x));
        jp.add(state);
        
        jp.add(new JLabel("Transitions:"));
        transition = new JTextField(15);
        transition.setEditable(false);
        transition.setText(String.valueOf(p.y));
        jp.add(transition);
		jp1.add(jp, BorderLayout.NORTH);
		
		// Inputs from user
		JPanel panel2 = new JPanel(new BorderLayout());
		
		JPanel jp2 = new JPanel();
		GridBagLayout gridbag0 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
		jp2.setLayout(gridbag0);
		c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
		jp2.setBorder(new javax.swing.border.TitledBorder("Simulation options"));
		JLabel label = new JLabel("Simulation time:");
		jp2.add(label, c0);
		simulationTime = new JTextField("1000000", 20);
		jp2.add(simulationTime, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel(" (in ticks)");
		jp2.add(label, c0);
		c0.gridwidth = 1;
		label = new JLabel("Simulation algorithm:");
		jp2.add(label, c0);
		simulationAlgorithm = new JComboBox();
		simulationAlgorithm.addItem(new String("Random"));
		jp2.add(simulationAlgorithm, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel(" ");
		jp2.add(label, c0);
		panel2.add(jp2, BorderLayout.NORTH);
		
		
		
		jp = new JPanel();
		gridbag0 = new GridBagLayout();
        c0 = new GridBagConstraints();
		jp.setLayout(gridbag0);
		c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
		
		jp.setBorder(new javax.swing.border.TitledBorder("Actions on graph"));
		
		c0.gridwidth = 1;
		label = new JLabel("System information action:");
		jp.add(label, c0);
		infoText = new JTextField("systemInfo", 20);
		jp.add(infoText, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel(" (not case sensitive)");
		jp.add(label, c0);
		
		c0.gridwidth = 1;
		label = new JLabel("Time elapsed action:");
		jp.add(label, c0);
		tickInfoText = new JTextField("goTS", 20);
		jp.add(tickInfoText, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel(" (not case sensitive)");
		jp.add(label, c0);
		
		c0.gridwidth = 1;
		label = new JLabel("Core information action:");
		jp.add(label, c0);
		coreInfoText = new JTextField("printCoreStates", 20);
		jp.add(coreInfoText, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel(" (not case sensitive)");
		jp.add(label, c0);
		
		c0.gridwidth = 1;
		label = new JLabel("Task information action:");
		jp.add(label, c0);
		taskInfoText = new JTextField("taskInfo", 20);
		jp.add(taskInfoText, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel(" (not case sensitive)");
		jp.add(label, c0);
		
		panel2.add(jp, BorderLayout.CENTER);
		
		jp = new JPanel();
		gridbag0 = new GridBagLayout();
        c0 = new GridBagConstraints();
		jp.setLayout(gridbag0);
		c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
		
		jp.setBorder(new javax.swing.border.TitledBorder("Power consumption"));
		
		c0.gridwidth = 1;
		label = new JLabel("LOW mode:");
		jp.add(label, c0);
		pcInLowMode = new JTextField("100", 20);
		jp.add(pcInLowMode, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel("pw / clock cycle");
		jp.add(label, c0);
		
		c0.gridwidth = 1;
		label = new JLabel("LOW to HIGH mode:");
		jp.add(label, c0);
		pcInLowToHighMode = new JTextField("140", 20);
		jp.add(pcInLowToHighMode, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel("pw / clock cycle");
		jp.add(label, c0);
		
		c0.gridwidth = 1;
		label = new JLabel("HIGH to LOW mode:");
		jp.add(label, c0);
		pcInHighToLowMode = new JTextField("160", 20);
		jp.add(pcInHighToLowMode, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel("pw / clock cycle");
		jp.add(label, c0);
		
		c0.gridwidth = 1;
		label = new JLabel("HIGH mode:");
		jp.add(label, c0);
		pcInHighMode = new JTextField("200", 20);
		jp.add(pcInHighMode, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		label = new JLabel("pw / clock cycle");
		jp.add(label, c0);
		
		panel2.add(jp, BorderLayout.SOUTH);
		
		jp1.add(panel2, BorderLayout.SOUTH);
		
        framePanel.add(jp1, BorderLayout.NORTH);
        
		setMode();
        pack();
    }
    
    public void actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();
        //System.out.println("Command:" + command);
        
        if (evt.getSource() == close) {
			if (mode == STARTED) {
				stopElement();
			}
            dispose();
            return;
        } else if (evt.getSource() == start) {
			generateVCD();
		}
    }
	
	private void setMode() {
		//System.out.println("Setting mode");
		stop.setEnabled(mode == STARTED);
		start.setEnabled(mode == NOT_STARTED);
		close.setEnabled(true);
		
	}
	
	private synchronized void next() {
		while(jdc == null) {
			try {
				wait();
			} catch (Exception e){}
		}
	}
	
	private synchronized void notifyJDCDone() {
		notifyAll();
	}
	
	private void generateVCD() {
		mode = STARTED;
		finished = false;
		setMode();
		jdc = null;
		t = new Thread(this);
		t.start();
		next();
		t = new Thread(this);
		t.start();
	}
	
	public void run() {
		if (jdc == null) {
			jdc = new JDialogCancel(this, "Generating VCD trace", "Generating VCD", this);
			GraphicLib.centerOnParent(jdc);
			notifyJDCDone();
			jdc.setSize(500, 200);
			jdc.setVisible(true);
		} else {
			simulate();
		}
	}
	
	public void simulate() {
		generator = new VCDGenerator(graph);
		String st = simulationTime.getText();
		long time;
		try {
			time = Long.parseLong(st.trim());
		} catch (NumberFormatException nfe) {
			time = 1000000;
			jta.append("Wrong simulation time (" + st + "): Setting simulation time to 1000000\n");
		}
		generator.setSimulationTicks(time);
		
		generator.setInfo(infoText.getText());
		generator.setTickInfo(tickInfoText.getText());
		generator.setCoreInfo(coreInfoText.getText());
		generator.setTaskInfo(taskInfoText.getText());
		
		// Setting power consumptions
		long val;
		try {
			val = Long.parseLong(pcInLowMode.getText().trim());
		} catch (NumberFormatException nfe) {
			val = 101;
		}
		generator.setPowerConsumptionInMode(VCDGenerator.LOW, val);
		
		try {
			val = Long.parseLong(pcInLowToHighMode.getText().trim());
		} catch (NumberFormatException nfe) {
			val = 101;
		}
		generator.setPowerConsumptionInMode(VCDGenerator.LOW_TO_HIGH, val);
		
		try {
			val = Long.parseLong(pcInHighToLowMode.getText().trim());
		} catch (NumberFormatException nfe) {
			val = 101;
		}
		generator.setPowerConsumptionInMode(VCDGenerator.HIGH_TO_LOW, val);
		
		try {
			val = Long.parseLong(pcInHighMode.getText().trim());
		} catch (NumberFormatException nfe) {
			val = 101;
		}
		generator.setPowerConsumptionInMode(VCDGenerator.HIGH, val);
		
		
		
		jta.append("\nGenerating VCD ... \n");
		int ret = generator.generateVCD();
		
		if (!hasBeenStopped()) {
			finished = true;
			jdc.stopAll();
			jdc = null;
		}
		
		if (ret == 0) {
			jta.append("VCD generated... \n");
			//System.out.println(generator.getVCDString());
			jta.append("Saving VCD in " + ConfigurationTTool.VCDPath + "spec.vcd \n");
			try {
				generator.saveInFile(ConfigurationTTool.VCDPath, "spec.vcd");
			} catch (FileException fe) {
				jta.append("Saving in file failed: " + fe.getMessage() + "\n");
			}
			
			jta.append("\n\nComputing power consumption:\n");
			long pc = 0, tmppc;
			for(int i=0; i<generator.getNbOfCores(); i++) {
				tmppc = generator.getPowerConsumptionOfCore(i);
				pc += tmppc;
				jta.append("Power consumption of core #" + i + " = " + tmppc + "pw \n");
			}
			jta.append("Total power consumption = " + pc + "pw\n");
		} else if (ret == 3) {
			jta.append("Cancelled\n");
		}
		
		jta.append("\nAll done\n");
		generator = null;
		jta.append("\n\n" + START_STRING);
		mode = NOT_STARTED;
		setMode();
	}
	
	public void goElement() {
		
	}
	
	public void stopElement() {
		if (generator != null) {
			generator.stop();
		}
	}
	
	public boolean hasFinished() {
		return finished;
	}
	
	public void setFinished() {
		if (generator != null) {
			generator.stop();
		}
	}
	
	public boolean hasBeenStopped() {
		if (generator != null) {
			return generator.hasBeenStopped();
		}
		return false;
	}
	
	public  int getPercentage() {
		if (generator != null) {
			return generator.getPercentage();
		} else {
			return 0;
		}
	}
	
	public String getCurrentActivity() {
		if (generator != null) {
			return generator.getCurrentActivity();
		}
		return "No activity";
	}
    
	
    
} // Class