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

package ui.window;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.*;
import ui.*;
import ui.avatarrd.AvatarRDPanel;
import ui.req.RequirementDiagramPanel;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

/**
* Class JFrameRequirementTable
* Creation: 17/02/2009
* version 1.0 17/02/2009
* @author Ludovic APVRILLE
 */
public	class JFrameRequirementTable extends JFrame implements ActionListener /*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	
	private static final String DOC_GEN_NAME = "tablereq.html";
	
	private Vector<TURTLEPanel> tabs;
	
	private java.util.List<AbstractTableModel> atms;
	private java.util.List<TableSorter> tss;
	private java.util.List<String> titles;
	
	//private StatisticsTableModel tm;
	
	//private JStatisticsPanel jstat;
	
	//private java.util.List<JScrollPane> panes;
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
	
	
	public JFrameRequirementTable(String title, Vector<TURTLEPanel> _tabs, JTabbedPane _main, Point [] _pts) {
		super(title);
		
		tabs = _tabs;
		pts = _pts;
		main = _main;
		//makeRequirements();
		
		atms = new ArrayList<AbstractTableModel>();
		tss = new ArrayList<TableSorter>();
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
		
    	// Issue #41 Ordering of tabbed panes 
		mainTabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
		
		// Information
		TURTLEPanel tp;
		int i, j;
//		TDiagramPanel tdp;
		RequirementDiagramPanel rdp;
		AvatarRDPanel ardp;
		LinkedList<TGComponent> all, list;
		all = new LinkedList<TGComponent>();
		String title;
		String maintitle;
		
		for(i=0; i<tabs.size(); i++) {
			tp = tabs.elementAt(i);
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
			if (tp instanceof AvatarRequirementPanel) {
				for(j=0; j<tp.panels.size(); j++) {
					if (tp.panels.elementAt(j) instanceof AvatarRDPanel) {
						ardp = (AvatarRDPanel)(tp.panels.elementAt(j));
						list = ardp.getAllRequirements();
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
		
		TraceManager.addDev("Requirements computed");
	}
	
	private void makeJScrollPane(LinkedList<TGComponent> list, JTabbedPane tab, String title) {
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
		tss.add(sorterRTM);
		titles.add(title);
	}
	
	@Override
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//
		
		if (command.equals("Close")) {
			dispose();
			return;
		} else if (evt.getSource() == buttonGenerate) {
			
			// Issue #32 Improve document generation
			final File genFile = new File( SpecConfigTTool.DocGenPath );
			String path;
			
			try {
				path = genFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				
				path = genFile.getAbsolutePath();
			}
			
			path += File.separator + DOC_GEN_NAME;

			if ( generateDoc() ) {
                JOptionPane.showMessageDialog(	this,
												"Document '" + path + "' has been successfully generated.",
												"Documentation Generation",
												JOptionPane.INFORMATION_MESSAGE );
			}
			else {
                JOptionPane.showMessageDialog( 	this,
                								"Error generating document '" + path + "'.",
						                        "Error",
						                        JOptionPane.INFORMATION_MESSAGE);
			}
		} 
	}
	
	private boolean generateDoc() {
		TraceManager.addDev("Generate doc");
		HTMLCodeGeneratorForTables doc = new HTMLCodeGeneratorForTables();
		//String s = doc.getHTMLCode(atms, titles, "List of Requirements").toString();
		String s = doc.getHTMLCodeFromSorters(tss, titles, "List of Requirements").toString();
		TraceManager.addDev("HTML code:" + s); 
		
		String path;
		if (SpecConfigTTool.DocGenPath.length() > 0) {
			path = SpecConfigTTool.DocGenPath + "/";
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();
		} else {
			path = "";
		}
		
		path += DOC_GEN_NAME;//"tablereq.html";
		
		try {
			FileUtils.saveFile(path, s);
		} catch (FileException fe) {
			fe.printStackTrace();
			TraceManager.addDev("HTML file could not be saved in " + path);
			
			return false;
		}
		
		return true;
	}
	
//	
//	private int maxLengthColumn(Component c, AbstractTableModel tm, int index) {
//		int w = 0, wtmp;
//		FontMetrics fm = c.getFontMetrics(c.getFont());
//		if (fm == null) {
//			return 0;
//		}
//		
//		String s;
//		
//		for(int i=0; i<tm.getRowCount(); i++) {
//			s = tm.getValueAt(i, index).toString();
//			wtmp = fm.stringWidth(s);
//			w = Math.max(w, wtmp);
//		}
//		return w;
//	}	
} // Class