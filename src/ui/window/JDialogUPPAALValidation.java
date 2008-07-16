/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
*
* /**
* Class JDialogUPPAALValidation
* Dialog for managing the syntax analysis of LOTOS specifications
* Creation: 16/05/2007
* @version 1.0 16/05/2007
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

import launcher.*;
import myutil.*;
import ui.*;

public class JDialogUPPAALValidation extends javax.swing.JDialog implements ActionListener, Runnable  {
	private static boolean deadlockAChecked, deadlockEChecked, generateTraceChecked, customChecked, stateAChecked, stateEChecked, showDetailsChecked;
	
	protected MainGUI mgui;
	
	protected String cmdVerifyta;
	protected String fileName;
	protected String pathTrace;
	protected String spec;
	protected String host;
	protected int mode;
	protected RshClient rshc;
	protected Thread t;
	
	protected final static int NOT_STARTED = 1;
	protected final static int STARTED = 2;
	protected final static int STOPPED = 3;
	
	//components
	protected JTextArea jta;
	protected JButton start;
	protected JButton stop;
	protected JButton close;
	protected JButton eraseAll;
	
	protected JCheckBox deadlockE, deadlockA, generateTrace, custom, stateE, stateA, showDetails;
	protected JTextField customText;
	
	
	/** Creates new form  */
	public JDialogUPPAALValidation(Frame f, MainGUI _mgui, String title, String _cmdVerifyta, String _pathTrace, String _fileName, String _spec, String _host) {
		super(f, title, true);
		
		mgui = _mgui;
		
		cmdVerifyta = _cmdVerifyta;
		fileName = _fileName;
		pathTrace = _pathTrace;
		spec = _spec;
		host = _host;
		
		initComponents();
		myInitComponents();
		pack();
		
		//getGlassPane().addMouseListener( new MouseAdapter() {});
		getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	
	protected void myInitComponents() {
		mode = NOT_STARTED;
		setButtons();
	}
	
	protected void initComponents() {
		
		Container c = getContentPane();
		setFont(new Font("Helvetica", Font.PLAIN, 14));
		c.setLayout(new BorderLayout());
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel jp1 = new JPanel();
		GridBagLayout gridbag1 = new GridBagLayout();
		GridBagConstraints c1 = new GridBagConstraints();
		
		jp1.setLayout(gridbag1);
		jp1.setBorder(new javax.swing.border.TitledBorder("Verify with UPPAAL: options"));
		//jp1.setPreferredSize(new Dimension(300, 150));
		
		// first line panel1
		//c1.gridwidth = 3;
		c1.gridheight = 1;
		c1.weighty = 1.0;
		c1.weightx = 1.0;
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		c1.fill = GridBagConstraints.BOTH;
		c1.gridheight = 1;
		
		/*deadlockE = new JCheckBox("Search for absence of deadock situations");
		deadlockE.addActionListener(this);
		jp1.add(deadlockE, c1);
		deadlockE.setSelected(deadlockEChecked);*/
		
		deadlockA = new JCheckBox("Search for absence of deadock situations");
		deadlockA.addActionListener(this);
		jp1.add(deadlockA, c1);
		deadlockA.setSelected(deadlockAChecked);
		
		stateE = new JCheckBox("Reachability of selected states");
		stateE.addActionListener(this);
		stateE.setToolTipText("Study the fact that a given state may be reachable i.e. in at least one path");
		jp1.add(stateE, c1);
		stateE.setSelected(stateEChecked);
		
		stateA = new JCheckBox("Liveness of selected states");
		stateA.addActionListener(this);
		stateA.setToolTipText("Study the fact that a given state is always reachable i.e. in all paths");
		jp1.add(stateA, c1);
		stateA.setSelected(stateAChecked);
		
		custom = new JCheckBox("Custom verification");
		custom.addActionListener(this);
		jp1.add(custom, c1);
		custom.setSelected(customChecked);
		
		c1.gridwidth = 1;
		jp1.add(new JLabel("Custom formulae = "), c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		customText = new JTextField("Type your CTL formulae here!", 80);
		customText.addActionListener(this);
		jp1.add(customText, c1);
		
		generateTrace = new JCheckBox("Generate simulation trace");
		generateTrace.addActionListener(this);
		jp1.add(generateTrace, c1);
		generateTrace.setSelected(generateTraceChecked);  
		
		showDetails = new JCheckBox("Show verification details");
		showDetails.addActionListener(this);
		jp1.add(showDetails, c1);
		showDetails.setSelected(showDetailsChecked);
		
		
		c.add(jp1, BorderLayout.NORTH);
		
		jta = new ScrolledJTextArea();
		jta.setEditable(false);
		jta.setMargin(new Insets(10, 10, 10, 10));
		jta.setTabSize(3);
		jta.append("Select options and then, click on 'start' to start generation of RG\n");
		Font f = new Font("Courrier", Font.BOLD, 12);
		jta.setFont(f);
		JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		c.add(jsp, BorderLayout.CENTER);
		
		start = new JButton("Start", IconManager.imgic53);
		stop = new JButton("Stop", IconManager.imgic55);
		close = new JButton("Close", IconManager.imgic27);
		eraseAll = new JButton("Del", IconManager.imgic337);
		
		start.setPreferredSize(new Dimension(100, 30));
		stop.setPreferredSize(new Dimension(100, 30));
		close.setPreferredSize(new Dimension(100, 30));
		eraseAll.setPreferredSize(new Dimension(100, 30));
		
		start.addActionListener(this);
		stop.addActionListener(this);
		close.addActionListener(this);
		eraseAll.addActionListener(this);
		
		JPanel jp2 = new JPanel();
		jp2.add(start);
		jp2.add(stop);
		jp2.add(close);
		jp2.add(eraseAll);
		
		c.add(jp2, BorderLayout.SOUTH);
	}
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		
		// Compare the action command to the known actions.
		if (evt.getSource() == eraseAll) {
			eraseTextArea();
		} else if (command.equals("Start"))  {
			startProcess();
		} else if (command.equals("Stop")) {
			stopProcess();
		} else if (command.equals("Close")) {
			closeDialog();
		} else {
			setButtons();
		}
		
	}
	
	public void eraseTextArea() {
		jta.setText("");
	}
	
	public void closeDialog() {
		if (mode == STARTED) {
			stopProcess();
		}
		//deadlockEChecked = deadlockE.isSelected();
		deadlockAChecked = deadlockA.isSelected();
		stateEChecked = stateE.isSelected();
		stateAChecked = stateA.isSelected();
		customChecked = custom.isSelected();
		generateTraceChecked = generateTrace.isSelected();
		showDetailsChecked = showDetails.isSelected();
		dispose();
	}
	
	public void stopProcess() {
		try {
			rshc.stopFillJTA();
		} catch (LauncherException le) {
		}
		rshc = null;
		mode = 	NOT_STARTED;
		setButtons();
	}
	
	public void startProcess() {
		t = new Thread(this);
		mode = STARTED;
		setButtons();
		t.start();
	}
	
	
	public void run() {
		
		String cmd1 = "";
		String data1;
		int id = 0;
		String query;
		String name;
		int trace_id = 0;
		int index;
		String fn;
		
		rshc = new RshClient(host);
		RshClient rshctmp = rshc;
		
		try {
			id = rshc.getId();
			jta.append("Session id on launcher="+id + "\n");
			
			fn = fileName.substring(0, fileName.length()-4) + "_" + id;
			
			jta.append("Sending UPPAAL specification data\n");
			rshc.sendFileData(fn+".xml", spec);
			
			/*if (deadlockE.isSelected()) {
				jta.append("Searching for absence of deadlock situations\n");
				workQuery("A[] not deadlock", fileName, trace_id, rshc);
				trace_id++;
			}*/
			
			if (deadlockA.isSelected() && (mode != NOT_STARTED)) {
				jta.append("\n\n--------------------------------------------\n");
				jta.append("Searching for absence of deadlock situations\n");
				workQuery("A[] not deadlock", fn, trace_id, rshc);
				trace_id++;
			}
			
			if (stateE.isSelected()&& (mode != NOT_STARTED)) {
				ArrayList<String> list = mgui.gtm.getUPPAALQueries();
				if ((list != null) && (list.size() > 0)){
					for(String s: list) {
						index = s.indexOf('$');
						if ((index != -1) && (mode != NOT_STARTED)) {
							name = s.substring(index+1, s.length());
							query = s.substring(0, index);
							jta.append("\n\n--------------------------------------------\n");
							jta.append("Studying component reachability\n");
							jta.append("Component:" + name + "\n");
							workQuery("E<> " + query, fn, trace_id, rshc);
							trace_id++;
						} else {
							jta.append("A component could not be studied (internal error)\n");
						}
					}
				} else {
					jta.append("No component to analyze found on diagrams\n");
				}
			}
			
			if (stateA.isSelected() && (mode != NOT_STARTED)) {
				ArrayList<String> list = mgui.gtm.getUPPAALQueries();
				if ((list != null) && (list.size() > 0)){
					for(String s: list) {
						index = s.indexOf('$');
						if ((index != -1) && (mode != NOT_STARTED)) {
							name = s.substring(index+1, s.length());
							query = s.substring(0, index);
							jta.append("\n\n--------------------------------------------\n");
							jta.append("Studying component liveness\n");
							jta.append("Component:" + name + "\n");
							workQuery("A<> " + query, fn, trace_id, rshc);
							trace_id++;
						} else {
							jta.append("A component could not be studied (internal error)\n");
						}
					}
				} else {
					jta.append("No component found\n");
				}
			}
			
			if(custom.isSelected() && (mode != NOT_STARTED)) {
				jta.append("\n\n--------------------------------------------\n");
				jta.append("Studying custom CTL formulae\n");
				workQuery(customText.getText(), fn, trace_id, rshc);
				trace_id++;
			}
			
			//Removing files
			rshc.deleteFile(fn+".xml");
			rshc.deleteFile(fn + ".q");
			rshc.deleteFile(fn + ".res");
			rshc.deleteFile(fn + ".xtr");
			
			rshc.freeId(id);
			jta.append("\nAll Done\n");
			
		} catch (LauncherException le) {
			jta.append(le.getMessage() + "\n");
			mode = 	NOT_STARTED;
			setButtons();
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException le1) {}
			return;
		} catch (Exception e) {
			mode = 	NOT_STARTED;
			setButtons();
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException le1) {}
			return;
		}
		
		mode = NOT_STARTED;
		setButtons();
	}
	
	private void workQuery(String query, String fn, int trace_id, RshClient rshc) throws LauncherException {
		String cmd1, data;
		jta.append("-> " + query + "\n");
		rshc.sendFileData(fn+".q", query);
		
		cmd1 = cmdVerifyta + " -u ";
		if (generateTrace.isSelected()) {
			cmd1 += "-t1 -f " + fn +  " ";
		}
		cmd1 += fn + ".xml " + fn + ".q";
		jta.append("--------------------------------------------\n");
		data = processCmd(cmd1);
		if(showDetails.isSelected()) {
			jta.append(data);
		} else {
			if (mode != NOT_STARTED) {
				if (data.indexOf("NOT") > -1) {
					jta.append("** property is NOT satisfied **\n");
				} else {
					jta.append("** property is satisfied **\n");			
				}
			} else {
				jta.append("** verification stopped **\n");
			}
		}
		
		if (generateTrace.isSelected()) {
			generateTraceFile(fn, trace_id, rshc);
		}
	}
	
	private void generateTraceFile(String fn, int trace_id, RshClient rshc) throws LauncherException{
		//jta.append("Going to generate trace file\n");
		String data, name;
		try {
			data = rshc.getFileData(fn + "-1.xtr");
			rshc.deleteFile(fn + "-1.xtr");
		} catch (Exception e) {
			jta.append("(no simulation trace was generated)\n");
			return;
		}
		name = pathTrace + fn + "_" + trace_id + ".xtr";
		//jta.append("Trying to generate trace file in" + name + "\n");
		try {
			FileUtils.saveFile(name, data);
			jta.append("Trace has been generated in " + name + "\n");
		} catch (FileException fe) {
			jta.append("Trace could not be generated in " + name + "\n");
			jta.append("Exception: " + fe.getMessage() + "\n");
		}
	}
	
	protected String processCmd(String cmd) throws LauncherException {
		rshc.setCmd(cmd);
		String s = null;
		rshc.sendProcessRequest();
		s = rshc.getDataFromProcess();
		return s;
	}
	
	protected void setButtons() {
		switch(mode) {
		case NOT_STARTED:
			custom.setEnabled(true);
			//deadlockE.setEnabled(true);
			deadlockA.setEnabled(true);
			stateE.setEnabled(true);
			stateA.setEnabled(true);
			//customText.setEnabled(true);
			customText.setEnabled(custom.isSelected());
			generateTrace.setEnabled(true);
			showDetails.setEnabled(true);
			
			if (custom.isSelected() || /*deadlockE.isSelected() ||*/deadlockA.isSelected() || stateE.isSelected() || stateA.isSelected()) {
				start.setEnabled(true);	
			} else {
				start.setEnabled(false);
			}
			
			stop.setEnabled(false);
			close.setEnabled(true);
			eraseAll.setEnabled(true);
			getGlassPane().setVisible(false);
			
			
			break;
		case STARTED:
			custom.setEnabled(false);
			//deadlockE.setEnabled(false);
			deadlockA.setEnabled(false);
			stateE.setEnabled(false);
			stateA.setEnabled(false);
			customText.setEnabled(false);
			generateTrace.setEnabled(false);
			showDetails.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(true);
			close.setEnabled(false);
			eraseAll.setEnabled(false);
			getGlassPane().setVisible(true);
			break;
		case STOPPED:
		default:
			custom.setEnabled(false);
			//deadlockE.setEnabled(false);
			deadlockA.setEnabled(false);
			stateE.setEnabled(false);
			stateA.setEnabled(false);
			customText.setEnabled(false);
			generateTrace.setEnabled(false);
			showDetails.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(true);
			eraseAll.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
		}
	}
}
