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
 * Class JDialogProVerifGeneration
 * Dialog for managing the generation of ProVerif code and execution of 
 * ProVerif
 * Creation: 10/09/2010
 * @version 1.1 10/09/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import myutil.*;
import avatartranslator.toproverif.*;
import avatartranslator.*;
import proverifspec.*;
import ui.*;

import launcher.*;


public class JDialogProVerifGeneration extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface  {
    
    protected MainGUI mgui;
    
    private String textC1 = "Generate ProVerif code in";
    private String textC2 = "Execute ProVerif as";
   
    protected static String pathCode;
    protected static String pathExecute;
	
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
    
    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
	protected JRadioButton exe, exeint;
	protected ButtonGroup exegroup;
    protected JLabel gen, comp;
    protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox stateReachability, translationOfBooleanFunction, outputOfProVerif;
	protected JComboBox versionSimulator;
    
    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
	//protected boolean startProcess = false;
    
    private String hostProVerif;
    
    protected RshClient rshc;
    
    
    /** Creates new form  */
    public JDialogProVerifGeneration(Frame f, MainGUI _mgui, String title, String _hostProVerif, String _pathCode, String _pathExecute) {
        super(f, title, true);
        
        mgui = _mgui;
        
        if (pathCode == null) {
            pathCode = _pathCode;
        }
        
        if (pathExecute == null)
            pathExecute = _pathExecute;
		
        
        hostProVerif = _hostProVerif;
		
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
        
        jp1 = new JTabbedPane();
        
        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Code generation"));
        
        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Execution"));
        
        
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;
        
        gen = new JLabel(textC1);
        //genJava.addActionListener(this);
        jp01.add(gen, c01);
        
        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);
        
        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        stateReachability = new JCheckBox("Compute state reachability");
        stateReachability.setSelected(true);
        jp01.add(stateReachability, c01);
		
		translationOfBooleanFunction = new JCheckBox("Advanced translation of boolean functions");
        translationOfBooleanFunction.setSelected(false);
        jp01.add(translationOfBooleanFunction, c01);

		
		/*optimizemode = new JCheckBox("Optimize code");
		optimizemode.setSelected(optimizeModeSelected);
        jp01.add(optimizemode, c01);
		
		jp01.add(new JLabel("Simulator used:"), c01);
		
		versionSimulator = new JComboBox(simus);
		versionSimulator.setSelectedIndex(selectedItem);
		versionSimulator.addActionListener(this);
		jp01.add(versionSimulator, c01);
		//System.out.println("selectedItem=" + selectedItem);
		
		//devmode = new JCheckBox("Development version of the simulator");
        //devmode.setSelected(true);
        //jp01.add(devmode, c01);*/
        
        jp01.add(new JLabel(" "), c01);
		
        jp1.add("Generate code", jp01);
        
        
        // Panel 03
        c03.gridheight = 1;
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;
		
		exegroup = new ButtonGroup();
        exe = new JRadioButton(textC2, false);
		exe.addActionListener(this);
		exegroup.add(exe);
        //exeJava.addActionListener(this);
        jp03.add(exe, c03);
        
        exe2 = new JTextField(pathExecute +  " -in pi ", 100);
        jp03.add(exe2, c03);
        
        jp03.add(new JLabel(" "), c03);
		
		outputOfProVerif = new JCheckBox("Show output of ProVerif");
        outputOfProVerif.setSelected(false);
        jp03.add(outputOfProVerif, c03);
        
        
        jp1.add("Execute", jp03);
        
        c.add(jp1, BorderLayout.NORTH);
		
	
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch ProVerif code generation / compilation\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
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
		
		if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }
    
    public void stopProcess() {
		if (rshc != null ){
			try {
				rshc.stopFillJTA();
			} catch (LauncherException le) {
			}
		}
        rshc = null;
        mode = 	STOPPED;
        setButtons();
        go = false;
    }
    
    public void startProcess() {
		t = new Thread(this);
		mode = STARTED;
		setButtons();
		go = true;
		t.start();
    }
    
    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }
    
    public void run() {
        String cmd;
        String list, data;
        int cycle = 0;
        
        hasError = false;
		
		TraceManager.addDev("Thread started");
        
        try {
            // Code generation
            if (jp1.getSelectedIndex() == 0) {
                jta.append("Generating ProVerif code\n");
               
                testGo();
				
				if (mgui.gtm.generateProVerifFromAVATAR(pathCode, stateReachability.isSelected(), translationOfBooleanFunction.isSelected())) {
					jta.append("ProVerif code generation done\n");
				} else {
					jta.append("Could not generate SystemC file\n");
				}
			}
				
 
            testGo();
            
			// Execute
            if (jp1.getSelectedIndex() == 1) {
                try {
                    cmd = exe2.getText() + pathCode + "pvspec";
                    
                    jta.append("Executing ProVerif code with command: \n" + cmd + "\n");
                    
                    rshc = new RshClient(hostProVerif);
                    // Assuma data are on the remote host
                    // Command
                    
                    data = processCmd(cmd);
					if (outputOfProVerif.isSelected()) {
						jta.append(data);
					}
					
					ProVerifOutputAnalyzer pvoa = new ProVerifOutputAnalyzer();
					pvoa.analyzeOutput(data);
					
					if (pvoa.getErrors().size() != 0) {
						jta.append("\nErrors found in the generated code:\n----------------\n");
						for(String error: pvoa.getErrors()) {
							jta.append(error+"\n");
						}
						
					} else {
						
						jta.append("\nReachable states:\n----------------\n");
						for(String re: pvoa.getReachableEvents()) {
							jta.append(re+"\n");
						}
						
						jta.append("\nNon reachable states:\n----------------\n");
						for(String re: pvoa.getNonReachableEvents()) {
							jta.append(re+"\n");
						}
						
						jta.append("\nConfidential data:\n----------------\n");
						for(String re: pvoa.getSecretTerms()) {
							jta.append(re+"\n");
						}
						
						jta.append("\nNon confidential data:\n----------------\n");
						for(String re: pvoa.getNonSecretTerms()) {
							jta.append(re+"\n");
						}
						
						jta.append("\nNon proved queries:\n----------------\n");
						for(String re: pvoa.getNotProved()) {
							jta.append(re+"\n");
						}
					}
					
                    jta.append("\nAll done\n");
                } catch (LauncherException le) {
                    jta.append("Error: " + le.getMessage() + "\n");
                    mode = 	STOPPED;
                    setButtons();
                    return;
                } catch (Exception e) {
                    mode = 	STOPPED;
                    setButtons();
                    return;
                }
            }
            
            if ((hasError == false) && (jp1.getSelectedIndex() < 1)) {
                jp1.setSelectedIndex(jp1.getSelectedIndex() + 1);
            }
            
        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }
        
        jta.append("\n\nReady to process next command\n");
        
        checkMode();
        setButtons();
		
		//System.out.println("Selected item=" + selectedItem);
    }
    
    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendProcessRequest();
        s = rshc.getDataFromProcess();
        return s;
    }
    
    protected void checkMode() {
        mode = NOT_STARTED;
    }
    
    protected void setButtons() {
        switch(mode) {
            case NOT_STARTED:
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            case STOPPED:
            default:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }
    
    public boolean hasToContinue() {
        return (go == true);
    }
    
    public void appendOut(String s) {
        jta.append(s);
    }
    
    public void setError() {
        hasError = true;
    }
}
