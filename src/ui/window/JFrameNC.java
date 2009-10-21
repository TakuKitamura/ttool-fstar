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
* Class JFrameNC
* Creation: 25/11/2008
* version 1.0 25/11/2008
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


import myutil.*;
import ui.*;
import ui.file.*;

import nc.*;


public	class JFrameNC extends JFrame implements ActionListener /*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	
	private NCStructure ncs;
	
	
	//private StatisticsTableModel tm;
	
	//private JStatisticsPanel jstat;
	
	private JScrollPane jspGeneralInfo;
	private JScrollPane jspEquipmentInfo;
	private JScrollPane jspSwitchInfo;
	private JScrollPane jspTrafficInfo;
	private JScrollPane jspLinkInfo;
	private JScrollPane jspPathInfo;
	
	//private JTextField eq, sw, tr, li, pa;
	
	// tab pane
	JTabbedPane mainTabbedPane;
	
	//shortest paths
	//JComboBox combo1, combo2, combo3, combo4;
	//JTextField combo1, combo2, combo3, combo4;
	//JTextField text1, text2;
	//JButton goPath, goPathL, savePath, savePathL;
	
	/*public synchronized void stopElement() {
		if (computingPath) {
			GraphAlgorithms.go = false;
			return;
		}
		if (!hasFinished()) {
			if (!graphDone) {
				if (graph != null) {
					graph.stopBuildGraph();
				}
			} else  {
				if (tm != null) {
					tm.stopBuildElement();
				} 
			}
			stopped = true;
		}
		stopAsSoonAsPossible = true;
	}
	
	public synchronized void setFinished() {
		finished = true;
	}
	
	public synchronized boolean hasFinished() {
		return finished;
	}
	
	public synchronized boolean hasBeenStopped() {
		return stopped;
	}
	
	public int getPercentage() {
		if (computingPath) {
			return -1;
		}
		if (graph == null) {
			return 0;
		} else {
			if (!graphDone) {
				return graph.getPercentage();
			} else {
				if (!statisticsDone) {
					if (tm != null) {
						return tm.getPercentage();
					} else {
						return 0;
					}
				}
				return -1;
			}
		}
	}
	
	public String getCurrentActivity() {
		if (computingPath) {
			return "Path is being calculated";
		}
		if (graph == null) {
			return "All done";
		} else {
			if (!graphDone) {
				return "Analyzing graph";
			} else {
				if (statisticsDone) {
					if (cycleDone) {
						return "Computing cycles";
					} else {
						return "Building deadlock information";
					}
				} else {
					return "Building statistical information";
				}
			}
		}	
	}
	
	public void goElement() {
		graph = new AUTGraph();
		//System.out.println("Building graph : " + data);
		graph.buildGraph(data);
		graph.computeStates();
		//System.out.println("Build is done");
		if (stopped) {
			return;
		}
		graphDone = true;
		//System.out.println("making components");
		makeComponents();
		//System.out.println("setting finished");
		setFinished();
		//System.out.println("Done");
	}*/
	
	public JFrameNC(String title, NCStructure _ncs) {
		super(title);
		ncs = _ncs;
		makeComponents();
	}
	
	public void makeComponents() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container framePanel = getContentPane();
		framePanel.setLayout(new BorderLayout());
		
		
		//jstat = new JStatisticsPanel(this, data);
		//System.out.println("Building statistical elements");
		//tm = new StatisticsTableModel(data);
		/*tm = new StatisticsTableModel();
		tm.analyzeData(graph);
		
		
		TableSorter sorter = new TableSorter(tm);
		
	
		
		
		JTable jtable = new JTable(sorter);
	
		sorter.setTableHeader(jtable.getTableHeader());
		
	
		
		/*try {
			sorter.setColumnComparator(Class.forName("Integer"), sorter.INTEGER_COMPARATOR);
        } catch (ClassNotFoundException e) {
			
        }
		((jtable.getColumnModel()).getColumn(0)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tm, 0) + 20, 100));
		((jtable.getColumnModel()).getColumn(1)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tm, 1) + 15, 60));
		((jtable.getColumnModel()).getColumn(2)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tm, 2) + 15, 400));
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);*/
		
		
		//jsp = new JScrollPane(jtable);
		
		
		//jsp.setWheelScrollingEnabled(true);
		//jsp.getVerticalScrollBar().setUnitIncrement(10);
		//statisticsDone = true;
		//jsp.setMaximumSize(new Dimension(250, 50));
		
		//framePanel.add(jsp, BorderLayout.CENTER);
		
		// Buttons
		
		JButton button1 = new JButton("Close", IconManager.imgic27);
		button1.addActionListener(this);
		JPanel jp = new JPanel();
		jp.add(button1);
		
		framePanel.add(jp, BorderLayout.SOUTH);
		
		// upper information
		//Point p = FormatManager.nbStateTransitionRGAldebaran(data);
		//Container c = getContentPane();
		//GridBagLayout gridbag0 = new GridBagLayout();
		//GridBagConstraints c0 = new GridBagConstraints();
		
		//jp = new JPanel();
		//jp.setLayout(gridbag0);
		
		/*c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
		jp.add(new JLabel("Equipments:"), c0);
		c0.gridwidth = GridBagConstraints.REMAINDER;
		eq = new JTextField(5);
		eq.setEditable(false);
		eq.setText(""+nc.equipments.size());
		jp.add(eq, c0);
		
		c0.gridwidth = 1;
		jp.add(new JLabel("Switches:"), c0);
		c0.gridwidth = GridBagConstraints.REMAINDER;
		sw = new JTextField(5);
		sw.setEditable(false);
		sw.setText(""+nc.switches.size());
		jp.add(sw, c0);*/
		
		// Information
		GeneralInfoNCTableModel tmgi = new GeneralInfoNCTableModel(ncs);
		TableSorter sorterGI = new TableSorter(tmgi);
		JTable jtableGI = new JTable(sorterGI);
		sorterGI.setTableHeader(jtableGI.getTableHeader());
			

		((jtableGI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtableGI.getColumnModel()).getColumn(1)).setPreferredWidth(25);
		jtableGI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspGeneralInfo = new JScrollPane(jtableGI);
		jspGeneralInfo.setWheelScrollingEnabled(true);
		jspGeneralInfo.getVerticalScrollBar().setUnitIncrement(10);
		
		// Equipments
		EquipmentsNCTableModel enctm = new EquipmentsNCTableModel(ncs);
		TableSorter sorterEI = new TableSorter(enctm);
		JTable jtableEI = new JTable(sorterEI);
		sorterEI.setTableHeader(jtableEI.getTableHeader());
			

		((jtableEI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtableEI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtableEI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		((jtableEI.getColumnModel()).getColumn(3)).setPreferredWidth(150);
		((jtableEI.getColumnModel()).getColumn(4)).setPreferredWidth(150);
		jtableEI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspEquipmentInfo = new JScrollPane(jtableEI);
		jspEquipmentInfo.setWheelScrollingEnabled(true);
		jspEquipmentInfo.getVerticalScrollBar().setUnitIncrement(10);
		
		// Switchs
		SwitchesNCTableModel snctm = new SwitchesNCTableModel(ncs);
		TableSorter sorterSI = new TableSorter(snctm);
		JTable jtableSI = new JTable(sorterSI);
		sorterSI.setTableHeader(jtableSI.getTableHeader());
			

		((jtableSI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtableSI.getColumnModel()).getColumn(1)).setPreferredWidth(100);
		((jtableSI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		((jtableSI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
		((jtableSI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
		jtableSI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspSwitchInfo = new JScrollPane(jtableSI);
		jspSwitchInfo.setWheelScrollingEnabled(true);
		jspSwitchInfo.getVerticalScrollBar().setUnitIncrement(10);
		
		// Traffics
		TrafficsNCTableModel tnctm = new TrafficsNCTableModel(ncs);
		TableSorter sorterTI = new TableSorter(tnctm);
		JTable jtableTI = new JTable(sorterTI);
		sorterTI.setTableHeader(jtableTI.getTableHeader());
			

		((jtableTI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtableTI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtableTI.getColumnModel()).getColumn(2)).setPreferredWidth(75);
		((jtableTI.getColumnModel()).getColumn(3)).setPreferredWidth(75);
		((jtableTI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
		((jtableTI.getColumnModel()).getColumn(5)).setPreferredWidth(100);
		((jtableTI.getColumnModel()).getColumn(6)).setPreferredWidth(75);
		jtableTI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspTrafficInfo = new JScrollPane(jtableTI);
		jspTrafficInfo.setWheelScrollingEnabled(true);
		jspTrafficInfo.getVerticalScrollBar().setUnitIncrement(10);
		
		// Links
		LinksNCTableModel lnctm = new LinksNCTableModel(ncs);
		TableSorter sorterLI = new TableSorter(lnctm);
		JTable jtableLI = new JTable(sorterLI);
		sorterLI.setTableHeader(jtableLI.getTableHeader());
			

		((jtableLI.getColumnModel()).getColumn(0)).setPreferredWidth(150);
		((jtableLI.getColumnModel()).getColumn(1)).setPreferredWidth(100);
		((jtableLI.getColumnModel()).getColumn(2)).setPreferredWidth(150);
		jtableLI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspLinkInfo = new JScrollPane(jtableLI);
		jspLinkInfo.setWheelScrollingEnabled(true);
		jspLinkInfo.getVerticalScrollBar().setUnitIncrement(10);
		
		// Paths
		PathsNCTableModel pnctm = new PathsNCTableModel(ncs);
		TableSorter sorterPI = new TableSorter(pnctm);
		JTable jtablePI = new JTable(sorterPI);
		sorterPI.setTableHeader(jtablePI.getTableHeader());
			

		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(300);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspPathInfo = new JScrollPane(jtablePI);
		jspPathInfo.setWheelScrollingEnabled(true);
		jspPathInfo.getVerticalScrollBar().setUnitIncrement(10);
		
		/*
		// Table
		//System.out.println("Building deadlock elements");
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();
		//if (graph.getNbTransition() < MAX_TRANSITIONS) { 
			DeadlockTableModel tmDeadlock = new DeadlockTableModel(graph, MAX_TRANSITIONS);
			
			if (shouldIStop()) {
				return;
			}
			
			TableSorter sorterDeadlock = new TableSorter(tmDeadlock);
			
			if (shouldIStop()) {
				return;
			}
			
			JTable jtableDeadlock = new JTable(sorterDeadlock);
			
			if (shouldIStop()) {
				return;
			}
			
			sorterDeadlock.setTableHeader(jtableDeadlock.getTableHeader());
			
			if (shouldIStop()) {
				return;
			}
			
			//System.out.println("Deadlock table");
			
			((jtableDeadlock.getColumnModel()).getColumn(0)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tmDeadlock, 0) + 20, 50));
			((jtableDeadlock.getColumnModel()).getColumn(1)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tmDeadlock, 1) + 15, 100));
			((jtableDeadlock.getColumnModel()).getColumn(2)).setPreferredWidth(Math.max(maxLengthColumn(framePanel, tmDeadlock, 2) + 15, 400));
			jtableDeadlock.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			jspDeadlock = new JScrollPane(jtableDeadlock);
			jspDeadlock.setWheelScrollingEnabled(true);
			jspDeadlock.getVerticalScrollBar().setUnitIncrement(10);
			
			if (shouldIStop()) {
				return;
			}
			
			//System.out.println("End Deadlock table");
			
			// shortest paths
			GridBagLayout gridbag1 = new GridBagLayout();
			GridBagConstraints c1 = new GridBagConstraints();
			jp1.setLayout(gridbag1);
			jp2.setLayout(new BorderLayout());
			
			//jp1.setBorder(new javax.swing.border.TitledBorder("Simulation options"));
			
			// first line panel1
			//c1.gridwidth = 3;
			c1.gridheight = 1;
			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.gridwidth = 1; //GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.HORIZONTAL;
			c1.gridheight = 1;
			
			JLabel label1 = new JLabel("Shortest path from ");
			jp1.add(label1, c1);
			
			if (shouldIStop()) {
				return;
			}
			
			//System.out.println("Graphical");
			
			Integer[] tab1 = new Integer[graph.getNbState()];
			
			if (shouldIStop()) {
				return;
			}
			
			Integer[] tab2 = new Integer[graph.getNbState()];
			
			if (shouldIStop()) {
				return;
			}
			
			for(int i=0; i<graph.getNbState(); i++) {
				tab1[i] = new Integer(i);
				tab2[i] = new Integer(i);
			}
			
			//combo1 = new JComboBox(tab1);
			combo1 = new JTextField("0", 10);
			jp1.add(combo1, c1);
			
			JLabel label2 = new JLabel("   to ");
			jp1.add(label2, c1);
			
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			//combo2 = new JComboBox(tab2);
			combo2 = new JTextField("" + (graph.getNbState() - 1), 10);
			jp1.add(combo2, c1);
			//jp2.add(jp1, BorderLayout.NORTH);
			
			goPath = new JButton("Compute", IconManager.imgic16);
			goPath.addActionListener(this);
			jp1.add(goPath, c1);
			
			savePath = new JButton("Save last path as a graph", IconManager.imgic24);
			savePath.addActionListener(this);
			jp1.add(savePath, c1);
			savePath.setEnabled(false);
			
			jp2.add(jp1, BorderLayout.NORTH);
			
			text1 = new JTextField(300);
			JScrollPane jspText = new JScrollPane(text1);
			jspText.setWheelScrollingEnabled(true);
			jspText.getVerticalScrollBar().setUnitIncrement(10);
			jp2.add(jspText, BorderLayout.CENTER);
			
			// Longest path
			GridBagLayout gridbag2 = new GridBagLayout();
			GridBagConstraints c2 = new GridBagConstraints();
			jp3.setLayout(gridbag2);
			jp4.setLayout(new BorderLayout());
			
			//jp1.setBorder(new javax.swing.border.TitledBorder("Simulation options"));
			
			// first line panel1
			//c1.gridwidth = 3;
			c2.gridheight = 1;
			c2.weighty = 1.0;
			c2.weightx = 1.0;
			c2.gridwidth = 1; //GridBagConstraints.REMAINDER; //end row
			c2.fill = GridBagConstraints.HORIZONTAL;
			c2.gridheight = 1;
			
			cycleDone = true;
			//System.out.println("Searching for cycles");
			if (graph.getNbTransition() < MAX_TRANSITIONS) {
				hasCycle = GraphAlgorithms.hasCycle(graph);
				cycleComputed = true;
			} else {
				cycleComputed = false;
			}
			//System.out.println("End searching for cycles");
			
			if (shouldIStop()) {
				return;
			}
			
			//System.out.println("G comp");
			
			label2 = new JLabel("Longest path from ");
			jp3.add(label2, c2);
			
			if (shouldIStop()) {
				return;
			}
			
			tab1 = new Integer[graph.getNbState()];
			
			if (shouldIStop()) {
				return;
			}
			
			tab2 = new Integer[graph.getNbState()];
			
			if (shouldIStop()) {
				return;
			}
			
			for(int i=0; i<graph.getNbState(); i++) {
				tab1[i] = new Integer(i);
				tab2[i] = new Integer(i);
			}
			
			//combo3 = new JComboBox(tab1);
			combo3 = new JTextField("0", 10);
			//graph.getNbState()
			jp3.add(combo3, c2);
			
			label2 = new JLabel("   to ");
			jp3.add(label2, c2);
			
			c2.gridwidth = GridBagConstraints.REMAINDER; //end row
			//combo4 = new JComboBox(tab2);
			combo4 = new JTextField("" + (graph.getNbState() - 1), 10);
			jp3.add(combo4, c2);
			//jp2.add(jp1, BorderLayout.NORTH);
			
			goPathL = new JButton("Compute", IconManager.imgic16);
			goPathL.addActionListener(this);
			jp3.add(goPathL, c2);
			
			savePathL = new JButton("Save last path as a graph", IconManager.imgic24);
			savePathL.addActionListener(this);
			jp3.add(savePathL, c2);
			savePathL.setEnabled(false);
			
			jp4.add(jp3, BorderLayout.NORTH);
			
			text2 = new JTextField(300);
			jspText = new JScrollPane(text2);
			jspText.setWheelScrollingEnabled(true);
			jspText.getVerticalScrollBar().setUnitIncrement(10);
			jp4.add(jspText, BorderLayout.CENTER);
			
			if (cycleComputed) {
				if (hasCycle) {
					text2.setText("The graph contains cycle -> longest path is infinite");
					goPathL.setEnabled(false);
				}
			} else {
				text2.setText("Graph may contain cycles -> in that case, the longest path cannot be computed");
			}
		//}
		
		
		//System.out.println("Making last elements");
		
		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addTab("General info.", IconManager.imgic13, jp, "# states, #transitions");
		mainTabbedPane.addTab("Statistics", IconManager.imgic13, jsp, "Statistics on states & transitions");
		//if (graph.getNbTransition() < MAX_TRANSITIONS) { 
			mainTabbedPane.addTab("Deadlocks", IconManager.imgic13, jspDeadlock, "Potential deadlocks");
			mainTabbedPane.addTab("Shortest Paths", IconManager.imgic13, jp2, "Shortest paths");
			mainTabbedPane.addTab("Longest Paths", IconManager.imgic13, jp4, "Longest paths");
		//}
		//}
		
		
		
		*/
		
		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addTab("General info.", IconManager.imgic13, jspGeneralInfo, "Information on diagram");
		mainTabbedPane.addTab("Equipments", IconManager.imgic13, jspEquipmentInfo, "Information on equipments");
		mainTabbedPane.addTab("Switches", IconManager.imgic13, jspSwitchInfo, "Information on switches");
		mainTabbedPane.addTab("Traffics", IconManager.imgic13, jspTrafficInfo, "Information on traffics");
		mainTabbedPane.addTab("Links", IconManager.imgic13, jspLinkInfo, "Information on links");
		mainTabbedPane.addTab("Paths", IconManager.imgic13, jspPathInfo, "Information on paths");
		
		framePanel.add(mainTabbedPane, BorderLayout.CENTER);
		
		
		pack();
		
		System.out.println("GG comp done");
	}
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//System.out.println("Command:" + command);
		
		if (command.equals("Close")) {
			dispose();
			return;
		} /*else if (evt.getSource() == goPath) {
			compute(1);
		} else if (evt.getSource() == goPathL) {
			compute(2);
		}*/
	}
	

	
	private void compute(int idFunc) {
		/*computingPath = true;
		GraphAlgorithms.go = true;
		String info;
		if (idFunc == 1) {
			info = "Calculating shortest path...";
		} else {
			info = "Calculating longest path...";
		}
		
		int from;
		int to;
		JTextField text;
		
		if (idFunc == 1) {
			text = text1;
			try {
				from = Integer.decode(combo1.getText()).intValue(); 
			} catch (Exception e) {
				text1.setText("Invalid value:" + combo1.getText());
				return;
			}
			try {
				to = Integer.decode(combo2.getText()).intValue(); 
			} catch (Exception e) {
				text1.setText("Invalid value:" + combo2.getText());
				return;
			}
			
			if(from<0) {
				text.setText("Invalid value:" + combo1.getText() + ". Minimun value is 0");
				return;
			}
			
			if(to<0) {
				text.setText("Invalid value:" + combo2.getText() + ". Minimun value is 0");
				return;
			}
			
			if(from>=graph.getNbState()) {
				text1.setText("Invalid value:" + combo1.getText() + ". Maximum value is: " + (graph.getNbState()-1));
				return;
			}
			
			if(to>=graph.getNbState()) {
				text1.setText("Invalid value:" + combo2.getText() + ". Maximum value is: " + (graph.getNbState()-1));
				return;
			}
			
		} else {
			text = text2;
			
			try {
				from = Integer.decode(combo3.getText()).intValue(); 
			} catch (Exception e) {
				text2.setText("Invalid value:" + combo3.getText());
				return;
			}
			try {
				to = Integer.decode(combo4.getText()).intValue(); 
			} catch (Exception e) {
				text2.setText("Invalid value:" + combo4.getText());
				return;
			}
			
			if(from<0) {
				text2.setText("Invalid value:" + combo3.getText() + ". Minimun value is 0");
				return;
			}
			
			if(to<0) {
				text2.setText("Invalid value:" + combo4.getText() + ". Minimun value is 0");
				return;
			}
			
			if(from>=graph.getNbState()) {
				text1.setText("Invalid value:" + combo3.getText() + ". Maximum value is: " + (graph.getNbState()-1));
				return;
			}
			
			if(to>=graph.getNbState()) {
				text2.setText("Invalid value:" + combo4.getText() + ". Maximum value is: " + (graph.getNbState()-1));
				return;
			}
			
			if (!cycleComputed) {
				hasCycle = GraphAlgorithms.hasCycle(graph);
				cycleComputed = true;
			}
		}
		
		ThreadGUIElement t = new ThreadGUIElement(this, idFunc, info, "Please wait", "");
		t.setExternalCall((ExternalCall)this);
		t.setStoppableGUIElement((StoppableGUIElement)this);
		t.go();*/
	}
	
	

	
	private int maxLengthColumn(Component c, AbstractTableModel tm, int index) {
		int w = 0, wtmp;
		FontMetrics fm = c.getFontMetrics(c.getFont());
		if (fm == null) {
			return 0;
		}
		
		String s;
		
		for(int i=0; i<tm.getRowCount(); i++) {
			s = tm.getValueAt(i, index).toString();
			wtmp = fm.stringWidth(s);
			w = Math.max(w, wtmp);
		}
		return w;
	}
	
	
} // Class