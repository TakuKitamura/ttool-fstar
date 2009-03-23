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
* Class JFrameRequirementTable
* Creation: 17/02/2009
* version 1.0 17/02/2009
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
import ui.req.*;

import nc.*;


public	class JFrameRequirementTable extends JFrame implements ActionListener /*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	
	private Vector tabs;
	
	private ArrayList<AbstractTableModel> atms;
	private ArrayList<String> titles;
	
	//private StatisticsTableModel tm;
	
	//private JStatisticsPanel jstat;
	
	private LinkedList<JScrollPane> panes;
	private JButton buttonGenerate;
	
	//private JTextField eq, sw, tr, li, pa;
	
	// tab pane
	JTabbedPane mainTabbedPane;
	
	JTabbedPane main; // from MGUI
	
	Point [] pts; // storing column data, see JDialogRequirementTable
	
	
	
	//shortest paths
	//JComboBox combo1, combo2, combo3, combo4;
	//JTextField combo1, combo2, combo3, combo4;
	//JTextField text1, text2;
	//JButton goPath, goPathL, savePath, savePathL;
	
	
	public JFrameRequirementTable(String title, Vector _tabs, JTabbedPane _main, Point [] _pts) {
		super(title);
		tabs = _tabs;
		pts = _pts;
		main = _main;
		//makeRequirements();
		
		atms = new ArrayList<AbstractTableModel>();
		titles = new ArrayList<String>();
		
		makeComponents();
	}
	
	public void makeComponents() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container framePanel = getContentPane();
		framePanel.setLayout(new BorderLayout());
		
		
		JButton button1 = new JButton("Close", IconManager.imgic27);
		button1.addActionListener(this);
		buttonGenerate = new JButton("Generate doc.", IconManager.imgic29);
		buttonGenerate.addActionListener(this);
		JPanel jp = new JPanel();
		jp.add(button1);
		jp.add(buttonGenerate);
		
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
		
		mainTabbedPane = new JTabbedPane();
		
		// Information
		TURTLEPanel tp;
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
		
		makeJScrollPane(all, mainTabbedPane, "All requirements");
		
		framePanel.add(mainTabbedPane, BorderLayout.CENTER);
		
		pack();
		
		System.out.println("Requirements computed");
	}
	
	private void makeJScrollPane(LinkedList<Requirement> list, JTabbedPane tab, String title) {
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
	}
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//System.out.println("Command:" + command);
		
		if (command.equals("Close")) {
			dispose();
			return;
		} else if (evt.getSource() == buttonGenerate) {
			generateDoc();
		} 
	}
	
	
	
	private void generateDoc() {
		System.out.println("Generate doc");
		HTMLCodeGeneratorForTables doc = new HTMLCodeGeneratorForTables();
		String s = doc.getHTMLCode(atms, titles, "List of Requirements").toString();
		//System.out.println("HTML code:" + s); 
		
		String path;
		if (ConfigurationTTool.IMGPath.length() > 0) {
			path = ConfigurationTTool.IMGPath + "/";
		} else {
			path = "";
		}
		path += "tablereq.html";
		
		try {
			FileUtils.saveFile(path, s);
		} catch (FileException fe) {
			System.out.println("HTML file could not be saved");
			return ;
		}
		
		System.out.println("File generated in " + path);
		
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