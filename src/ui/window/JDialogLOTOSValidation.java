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
* Class JDialogLOTOSValidation
* Dialog for managing the syntax analysis of LOTOS specifications
* Creation: 10/03/2006
* @version 1.0 10/03/2006
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import launcher.*;
import myutil.*;
import ui.*;

public class JDialogLOTOSValidation extends javax.swing.JDialog implements ActionListener, Runnable  {
	private static boolean verboseChecked, monitorChecked, safetyChecked, gradualChecked, v3v4Checked, fc2Checked, distributorChecked, autChecked = true, dotChecked=true, bcgChecked = false;
	private static int max = 5000000;
	
	protected MainGUI mgui;
	
	protected String cmdCaesar, cmdCaesarOpen, cmdBcgio, cmdBcgmerge;
	protected String fileName;
	protected String spec;
	protected String host;
	protected int mode;
	protected RshClient rshc;
	protected Thread t;
	
	protected int simuTime = 0;
	
	protected final static int NOT_STARTED = 1;
	protected final static int STARTED = 2;
	protected final static int STOPPED = 3;
	
	//components
	protected JTextArea jta;
	protected JButton start;
	protected JButton stop;
	protected JButton close;
	
	protected JCheckBox verbose, monitor, safety, gradual, v3v4, fc2, distributor, dot, bcg, aut;
	protected JTextField maxText;
	
	protected boolean distributorEnabled;
	
	/** Creates new form  */
	public JDialogLOTOSValidation(Frame f, MainGUI _mgui, String title, String _cmdCaesar, String _cmdCaesarOpen, String _cmdBcgio, String _cmdBcgmerge, String _fileName, String _spec, String _host) {
		super(f, title, true);
		
		mgui = _mgui;
		
		cmdCaesar = _cmdCaesar;
		cmdCaesarOpen = _cmdCaesarOpen;
		cmdBcgio = _cmdBcgio;
		cmdBcgmerge = _cmdBcgmerge;
		fileName = _fileName;
		spec = _spec;
		host = _host;
		
		initComponents();
		myInitComponents();
		pack();
		
		
		//getGlassPane().addMouseListener( new MouseAdapter() {});
		getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	
	protected void myInitComponents() {
		distributorEnabled = !mgui.gtm.useDynamicStructure(spec);
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
		jp1.setBorder(new javax.swing.border.TitledBorder("RG generation options"));
		//jp1.setPreferredSize(new Dimension(300, 150));
		
		// first line panel1
		//c1.gridwidth = 3;
		c1.gridheight = 1;
		c1.weighty = 1.0;
		c1.weightx = 1.0;
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		c1.fill = GridBagConstraints.BOTH;
		c1.gridheight = 1;
		
		gradual = new JCheckBox("Gradual (recommended for large specifications)");
		gradual.addActionListener(this);
		jp1.add(gradual, c1);
		gradual.setSelected(gradualChecked);
		
		v3v4 = new JCheckBox("No optimization on variables and expressions");
		v3v4.addActionListener(this);
		jp1.add(v3v4, c1);
		v3v4.setSelected(v3v4Checked);
		
		safety = new JCheckBox("Safety");
		safety.addActionListener(this);
		jp1.add(safety, c1);
		safety.setSelected(safetyChecked);
		
		monitor = new JCheckBox("Monitor");
		monitor.addActionListener(this);
		jp1.add(monitor, c1);
		monitor.setSelected(monitorChecked);
		
		verbose = new JCheckBox("Verbose");
		verbose.addActionListener(this);
		jp1.add(verbose, c1);
		verbose.setSelected(verboseChecked);
		
		distributor = new JCheckBox("Use DISTRIBUTOR (Distributed computation of RG)");
		distributor.addActionListener(this);
		jp1.add(distributor, c1);
		distributor.setSelected(distributorChecked);
		jp1.add(new JLabel("[DISTRIBUTOR May be used only if the formal specification uses no FIFO structure]"), c1);
		
		bcg = new JCheckBox("Save graph in bcg format");
		bcg.addActionListener(this);
		jp1.add(bcg, c1);
		bcg.setSelected(bcgChecked);
		
		aut = new JCheckBox("Save graph in aut format");
		aut.addActionListener(this);
		jp1.add(aut, c1);
		aut.setSelected(autChecked);
		
		dot = new JCheckBox("Save graph in dot format");
		dot.addActionListener(this);
		jp1.add(dot, c1);
		dot.setSelected(dotChecked);
		
		fc2 = new JCheckBox("Save graph in fc2 format");
		fc2.addActionListener(this);
		jp1.add(fc2, c1);
		fc2.setSelected(fc2Checked);
		
		
		c1.gridwidth = 1;
		jp1.add(new JLabel("Max # of transitions = "), c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		maxText = new JTextField("" + max, 10);
		maxText.addActionListener(this);
		jp1.add(maxText, c1);
		
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
		
		start.setPreferredSize(new Dimension(100, 30));
		stop.setPreferredSize(new Dimension(100, 30));
		close.setPreferredSize(new Dimension(100, 30));
		
		start.addActionListener(this);
		stop.addActionListener(this);
		close.addActionListener(this);
		
		JPanel jp2 = new JPanel();
		jp2.add(start);
		jp2.add(stop);
		jp2.add(close);
		
		c.add(jp2, BorderLayout.SOUTH);
	}
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		
		// Compare the action command to the known actions.
		if (command.equals("Start"))  {
			startProcess();
		} else if (command.equals("Stop")) {
			stopProcess();
		} else if (command.equals("Close")) {
			closeDialog();
		}
	}
	
	public void setAutomatic(boolean automatic) {
		if (automatic) {
			startProcess();
		}
	}
	
	
	public void closeDialog() {
		if (mode == STARTED) {
			stopProcess();
		}
		gradualChecked = gradual.isSelected();
		v3v4Checked = v3v4.isSelected();
		safetyChecked = safety.isSelected();
		monitorChecked = monitor.isSelected();
		verboseChecked = verbose.isSelected();
		fc2Checked = fc2.isSelected();
		dotChecked = dot.isSelected();   
		bcgChecked = bcg.isSelected();
		autChecked = aut.isSelected();
		distributorChecked = distributor.isSelected();
		dispose();
	}
	
	public void stopProcess() {
		try {
			rshc.stopFillJTA();
		} catch (LauncherException le) {
		}
		rshc = null;
		mode = 	STOPPED;
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
		String data, data1;
		Point p;
		int id = 0;
		
		
		rshc = new RshClient(host);
		RshClient rshctmp = rshc;
		
		try {
			max = Integer.decode(maxText.getText()).intValue();
			
			jta.append("Sending LOTOS specification data\n");
			
			id = rshc.getId();
			jta.append("Session id on launcher="+id + "\n");
			
			fileName = fileName.substring(0, fileName.length()-4) + "_" + id;
			
			
			// file data
			rshc.sendFileData(fileName+".lot", spec);
			
			
			
			// Command for RG
			cmd1 = cmdCaesar;
			
			if (distributor.isSelected()) {
				cmd1 = cmdCaesarOpen;
			}
			
			cmd1 += " -english -warning -error ";
			
			if (gradual.isSelected()) {
				cmd1 += "-gradual ";
			}
			
			if (v3v4.isSelected()) {
				cmd1 += "-v3 -v4 ";
			}
			
			if (safety.isSelected()) {
				cmd1 += "-safety ";
			}
			
			if (monitor.isSelected()) {
				cmd1 += "-monitor ";
			}
			
			if (verbose.isSelected()) {
				cmd1 += "-verbose ";
			}
			
			cmd1 += fileName;
			
			if (distributor.isSelected() && (distributorEnabled)) {
				cmd1 += " distributor configuration " + fileName;
			}
			
			
			jta.append("\nGenerating RG\n");
			data = processCmd(cmd1);
			jta.append(data);
			
			if (distributor.isSelected()) {
				jta.append("\nMerging RGs\n");
				cmd1 = cmdBcgmerge + " " + fileName + ".pbg "  + fileName + ".bcg";
				data = processCmd(cmd1);
			}
			
			// Getting graph
			jta.append("\nGetting RG from " + fileName + ".bcg" + "\n");
			
			if (bcg.isSelected()) {
				data = rshc.getFileData(fileName + ".bcg");
				String loc;
				if ((ConfigurationTTool.TGraphPath != null) && (ConfigurationTTool.TGraphPath.length() > 0)) {
					loc = ConfigurationTTool.TGraphPath + fileName + ".bcg";
				} else {
					loc = fileName + ".bcg";
				}
				mgui.gtm.saveInFile(new File(loc), data);
				jta.append("Graph saved in bcg format at " + loc + "\n");
			}
			
			// AUT  dot
			if (aut.isSelected()) {
				jta.append("\nConverting to aut format\n");
				//rshc.sendFileData(fileName + ".bcg", data);
				cmd1 = cmdBcgio + " -bcg " + fileName + ".bcg" + " -aldebaran " + fileName + ".aut";
				data = processCmd(cmd1);
				data = rshc.getFileData(fileName + ".aut");
				p = FormatManager.nbStateTransitionRGAldebaran(data);
				//jta.append("\ngot data ... Converting 5\n");
				jta.append("\n" + p.x + " state(s), " + p.y + " transition(s)\n\n");
				//jta.append("\nConverting 1\n");
				jta.append("\nActions on graph are being modified to be human-readable");
				data1 = mgui.gtm.convertCADP_AUT_to_RTL_AUT(data, max);
				//System.out.println("data1 = " + data1);
				if (data1 == null) {
					jta.append("\nGraph is larger than the maximum specified size.\nThe graph is being saved to be used with text commands only.");
					mgui.gtm.setRGAut(data);
					//jta.append("\nConverting 3\n");
					data = mgui.saveRGAut();
					jta.append("\nGraph was saved in: " + data);
				} else {
					data = data1;
					//jta.append("\nConverting 2\n");
					//System.out.println("graph AUT=" + data);
					mgui.gtm.setRGAut(data);
					//jta.append("\nConverting 3\n");
					mgui.saveRGAut();
					//jta.append("\nConverting 4\n");
					
					
					// Bcgio command
					// Must send modified graph
					if (dot.isSelected()) {
						jta.append("\nConverting to dot format\n");
						rshc.sendFileData(fileName + ".aut", data);
						cmd1 = cmdBcgio + " -aldebaran " + fileName + ".aut" + " -graphviz " + fileName + ".aut.dot";
						data = processCmd(cmd1);
						data = rshc.getFileData(fileName + ".aut.dot");
						mgui.gtm.setRGAutDOT(data);
						mgui.saveRGAutDOT();
					}
					
					if (fc2.isSelected()) {
						String path = ConfigurationTTool.GGraphPath;
						if ((path == null) || (path.length() == 0)) {
							path = new File("").getAbsolutePath();
						} 
						jta.append("\nConverting to fc2 format and saving it in " + path + fileName + ".fc2\n");
						//rshc.sendFileData(fileName + ".aut", data);
						cmd1 = cmdBcgio + " -aldebaran " + fileName + ".aut" + " -fc2 " + fileName + ".fc2";
						data = processCmd(cmd1);
						data = rshc.getFileData(fileName + ".fc2");
						//System.out.println("Got data!");
						mgui.gtm.saveInFile(new File(path, fileName + ".fc2"), data);
					}
				}
			} else {
				if (dot.isSelected()) {
					jta.append("\nConverting to dot format from bcg format and saving it in " + fileName + ".aut.dot\n");
					//rshc.sendFileData(fileName + ".aut", data);
					cmd1 = cmdBcgio + " -bcg " + fileName + ".bcg" + " -graphviz " + fileName + ".aut.dot";
					data = processCmd(cmd1);
					data = rshc.getFileData(fileName + ".aut.dot");
					mgui.gtm.setRGAutDOT(data);
					mgui.saveRGAutDOT();
				}
				
				if (fc2.isSelected()) {
					String path = ConfigurationTTool.GGraphPath;
					if ((path == null) || (path.length() == 0)) {
						path = new File("").getAbsolutePath();
					} 
					jta.append("\nConverting to fc2 format from bcg format and saving it in " + path + fileName + ".fc2\n");
					//rshc.sendFileData(fileName + ".aut", data);
					cmd1 = cmdBcgio + " -bcg " + fileName + ".bcg" + " -fc2 " + fileName + ".fc2";
					data = processCmd(cmd1);
					data = rshc.getFileData(fileName + ".fc2");
					//System.out.println("Got data!");
					mgui.gtm.saveInFile(new File(path, fileName + ".fc2"), data);
				}
				
			}
			
			//Removing files
			rshc.deleteFile(fileName+".lot");
			rshc.deleteFile(fileName + ".bcg");
			rshc.deleteFile(fileName + ".aut");
			rshc.deleteFile(fileName + ".aut.dot");
			rshc.deleteFile(fileName + ".pbg");
			rshc.deleteFile(fileName + ".fc2");
			rshc.deleteFile(fileName + ".o");
			rshc.deleteFile(fileName + ".c");
			rshc.deleteFile(fileName + "@1.o");
			
			rshc.freeId(id);
			jta.append("\nAll Done\n");
			//rshc.deleteFile(fileName);
			
		} catch (LauncherException le) {
			jta.append(le.getMessage() + "\n");
			mode = 	STOPPED;
			setButtons();
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException le1) {}
			return;
		} catch (Exception e) {
			mode = 	STOPPED;
			setButtons();
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException le1) {}
			return;
		}
		
		mode = STOPPED;
		setButtons();
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
			maxText.setEnabled(true);
			fc2.setEnabled(true);
			dot.setEnabled(true);
			aut.setEnabled(true);
			bcg.setEnabled(true);
			gradual.setEnabled(true);
			v3v4.setEnabled(true);
			safety.setEnabled(true);
			monitor.setEnabled(true);
			verbose.setEnabled(true);
			distributor.setEnabled(distributorEnabled);
			start.setEnabled(true);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
		case STARTED:
			maxText.setEnabled(false);
			fc2.setEnabled(false);   
			dot.setEnabled(false);
			aut.setEnabled(false);
			bcg.setEnabled(false);
			gradual.setEnabled(false);
			v3v4.setEnabled(false);
			safety.setEnabled(false);
			monitor.setEnabled(false);
			verbose.setEnabled(false);
			distributor.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(true);
			close.setEnabled(false);
			getGlassPane().setVisible(true);
			break;
		case STOPPED:
		default:
			maxText.setEnabled(false);
			fc2.setEnabled(false);
			dot.setEnabled(false);
			aut.setEnabled(false);
			bcg.setEnabled(false);
			gradual.setEnabled(false);
			v3v4.setEnabled(false);
			safety.setEnabled(false);
			monitor.setEnabled(false);
			verbose.setEnabled(false);
			distributor.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
		}
	}
}
