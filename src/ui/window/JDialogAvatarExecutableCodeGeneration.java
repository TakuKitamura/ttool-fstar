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
* Class JDialogAvatarExecutableCodeGeneration
* Dialog for managing the generation and compilation of AVATAR executable code
* Creation: 29/03/2011
* @version 1.1 29/03/2011
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import myutil.*;
import ui.*;


import avatartranslator.*;
import avatartranslator.toexecutable.*;
import launcher.*;

import ui.interactivesimulation.*;


public class JDialogAvatarExecutableCodeGeneration extends javax.swing.JFrame implements ActionListener, Runnable, MasterProcessInterface  {
    
	private static String[] unitTab = {"usec", "msec", "sec"}; 
	
    protected Frame f;
    protected MainGUI mgui;
    
    private String textSysC1 = "Base directory of code generation code:";
    private String textSysC2 = "Compile executable code in";
	private String textSysC3 = "Compile soclib executable with";
    //private String textSysC3 = "with";
    private String textSysC4 = "Run code:";
	private String textSysC5 = "Run code and trace events (if enabled at code generation):";
	private String textSysC6 = "Run code in soclib / mutekh:";
	private String textSysC8 = "Show trace from file:";
	private String textSysC9 = "Show trace from soclib file:";
    
    private static String unitCycle = "1";
	
	private static String[] codes = {"AVATAR CPOSIX"};
	private static int selectedItem = 0;
	private static int selectedRun = 1;
	private static int selectedCompile = 0;
	private static int selectedViewTrace = 0;
    
    protected static String pathCode;
    protected static String pathCompiler;
    protected static String pathExecute;
	protected static String pathExecuteWithTracing;
	protected static String pathCompileSoclib;
	protected static String pathExecuteSoclib;
	protected static String pathSoclibTraceFile;
	
	
	protected static boolean optimizeModeSelected = true;
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
    
    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
	protected JRadioButton exe, exeint, exetrace, exesoclib, compile, compilesoclib, viewtrace, viewtracesoclib;
	protected ButtonGroup compilegroup, exegroup, viewgroup;
    protected JLabel gen;
    protected JTextField code1, code2, compiler1, compiler2, exe1, exe2, exe3, exe4, exe2int, simulationTraceFile, simulationsoclibTraceFile;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox removeCFiles, removeXFiles, debugmode, tracemode, optimizemode;
	protected JComboBox versionCodeGenerator, units;
    protected JButton showSimulationTrace;
	
	private static int selectedUnit = 2;
	
    
    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
	protected boolean startProcess = false;

    
    private String hostExecute;
    
    protected RshClient rshc;
    
    
    /** Creates new form  */
    public JDialogAvatarExecutableCodeGeneration(Frame _f, MainGUI _mgui, String title, String _hostExecute, String _pathCode, String _pathCompiler, String _pathExecute, String _pathCompilerSoclib, String _pathExecuteSoclib, String _pathSoclibTraceFile) {
        super(title);
        
        f = _f;
        mgui = _mgui;
        
        if (pathCode == null) {
            pathCode = _pathCode;
        }
        
        if (pathCompiler == null)
            pathCompiler = _pathCompiler;
        
        if (pathExecute == null)
            pathExecute = _pathExecute;
        
		if (pathCompileSoclib == null) {
			pathCompileSoclib = _pathCompilerSoclib;
		}
		
		if (pathExecuteSoclib == null) {
			pathExecuteSoclib = _pathExecuteSoclib;
		}
		
		if (pathSoclibTraceFile == null){
			pathSoclibTraceFile = _pathSoclibTraceFile;
		}
		
        hostExecute = _hostExecute;
        
		
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    
    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
		makeSelectionCompile();
		makeSelectionExecute();
		makeSelectionViewTrace();
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
        
        JPanel jp02 = new JPanel();
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        jp02.setLayout(gridbag02);
        jp02.setBorder(new javax.swing.border.TitledBorder("Compilation"));
        
        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Execution"));
        
        JPanel jp04 = new JPanel();
        GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        jp04.setLayout(gridbag04);
        jp04.setBorder(new javax.swing.border.TitledBorder("Simulation trace"));
        
        
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;
        
        gen = new JLabel(textSysC1);
        //genJava.addActionListener(this);
        jp01.add(gen, c01);
        
        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);
        
        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        removeCFiles = new JCheckBox("Remove .c / .h files");
        removeCFiles.setSelected(true);
        jp01.add(removeCFiles, c01);
        
        removeXFiles = new JCheckBox("Remove .x files");
        removeXFiles.setSelected(true);
        jp01.add(removeXFiles, c01);
        
        debugmode = new JCheckBox("Put debug information in generated code");
        debugmode.setSelected(true);
        jp01.add(debugmode, c01);
		
		tracemode = new JCheckBox("Put tracing capabilities in generated code");
        tracemode.setSelected(true);
        jp01.add(tracemode, c01);
		
		optimizemode = new JCheckBox("Optimize code");
		optimizemode.setSelected(optimizeModeSelected);
        jp01.add(optimizemode, c01);
		
		jp01.add(new JLabel("1 time unit ="), c01);
		
		units = new JComboBox(unitTab);
		units.setSelectedIndex(selectedUnit);
		units.addActionListener(this);
		jp01.add(units, c01);
		
		jp01.add(new JLabel("Code generator used:"), c01);
		
		versionCodeGenerator = new JComboBox(codes);
		versionCodeGenerator.setSelectedIndex(selectedItem);
		versionCodeGenerator.addActionListener(this);
		jp01.add(versionCodeGenerator, c01);
		//System.out.println("selectedItem=" + selectedItem);
		
		//devmode = new JCheckBox("Development version of the simulator");
        //devmode.setSelected(true);
        //jp01.add(devmode, c01);
        
        jp01.add(new JLabel(" "), c01);
        jp1.add("Generate code", jp01);
        
        // Panel 02 -> compile
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;
		
		compilegroup =  new ButtonGroup();
        
        compile = new JRadioButton(textSysC2, false);
        jp02.add(compile, c02);
		compile.addActionListener(this);
        compilegroup.add(compile);
        //code2 = new JTextField(pathCode, 100);
        //jp02.add(code2, c02);
        
        //jp02.add(new JLabel("with"), c02);
        
        compiler1 = new JTextField(pathCompiler, 100);
        jp02.add(compiler1, c02);
        
        jp02.add(new JLabel(" "), c02);
		
		
		compilesoclib = new JRadioButton(textSysC3, false);
		compilesoclib.addActionListener(this);
        jp02.add(compilesoclib, c02);
        compilegroup.add(compilesoclib);
        compiler2 = new JTextField(pathCompileSoclib, 100);
        jp02.add(compiler2, c02);
		
		compile.setSelected(selectedCompile == 0);
		compilesoclib.setSelected(selectedCompile == 1);
		
        jp1.add("Compile", jp02);
        
        // Panel 03 -> Execute
        c03.gridheight = 1;
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;
		
		exegroup = new ButtonGroup();
        exe = new JRadioButton(textSysC4, false);
		exe.addActionListener(this);
		exegroup.add(exe);
        jp03.add(exe, c03);
        exe2 = new JTextField(pathExecute, 100);
        jp03.add(exe2, c03);
		exegroup.add(exe);
		
		exetrace = new JRadioButton(textSysC5, false);
		exetrace.addActionListener(this);
		exegroup.add(exetrace);
		jp03.add(exetrace, c03);
		exe3 = new JTextField(pathExecute +  " " + pathCode + File.separator + "trace.txt", 100);
        jp03.add(exe3, c03);
		
		exesoclib = new JRadioButton(textSysC6, false);
		exesoclib.addActionListener(this);
		exegroup.add(exesoclib);
		jp03.add(exesoclib, c03);
		exe4 = new JTextField(pathExecuteSoclib, 100);
        jp03.add(exe4, c03);
		
		exe.setSelected(selectedRun == 0);
		exetrace.setSelected(selectedRun == 1);
		exesoclib.setSelected(selectedRun == 2);
		
		/*exeint = new JRadioButton(textSysC5, true);
		exeint.addActionListener(this);
		exegroup.add(exeint);
        //exeJava.addActionListener(this);
        jp03.add(exeint, c03);
        
        exe2int = new JTextField(pathInteractiveExecute, 100);
        jp03.add(exe2int, c02);*/
        
        jp03.add(new JLabel(" "), c03);
        
        jp1.add("Execute", jp03);
        
        // Panel 04 -> View trace
        c04.gridheight = 1;
        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridwidth = GridBagConstraints.REMAINDER; //end row
        c04.fill = GridBagConstraints.HORIZONTAL;
        c04.gridheight = 1;
		
		viewgroup = new ButtonGroup();
		viewtrace = new JRadioButton(textSysC8, false);
		viewgroup.add(viewtrace);
		viewtrace.addActionListener(this);
		 jp04.add(viewtrace, c04);
        simulationTraceFile = new JTextField(pathCode + File.separator + "trace.txt", 100);
        jp04.add(simulationTraceFile, c04);
		viewtracesoclib = new JRadioButton(textSysC9, false);
		viewgroup.add(viewtracesoclib);
		viewtracesoclib.addActionListener(this);
		 jp04.add(viewtracesoclib, c04);
		simulationsoclibTraceFile = new JTextField(pathSoclibTraceFile, 100);
        jp04.add(simulationsoclibTraceFile, c04);
        
        showSimulationTrace = new JButton("Show simulation trace");
        showSimulationTrace.addActionListener(this);
        jp04.add(showSimulationTrace, c04);
        
		viewtrace.setSelected(selectedViewTrace == 0);
		viewtracesoclib.setSelected(selectedViewTrace == 1);
		
        jp1.add("Results", jp04);
        
        
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch code generation / compilation / execution\n");
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
        
        // Compare the action command to the known actions.
		if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (evt.getSource() == versionCodeGenerator) {
			selectedItem = versionCodeGenerator.getSelectedIndex();
		} else if (evt.getSource() == units) {
			selectedUnit = units.getSelectedIndex();
		} else if (evt.getSource() == showSimulationTrace) {
			showSimulationTrace();
		} else if ((evt.getSource() == exe) || (evt.getSource() == exetrace)|| (evt.getSource() == exesoclib)) {
			makeSelectionExecute();
		} else if ((evt.getSource() == compile) || (evt.getSource() == compilesoclib)) {
			makeSelectionCompile();
		} else if ((evt.getSource() == viewtrace) || (evt.getSource() == viewtracesoclib)) {
			makeSelectionViewTrace();
		}
    }
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
		optimizeModeSelected = optimizemode.isSelected();
        dispose();
    }
	
	public void makeSelectionExecute() {
			if (exe.isSelected()) {
				selectedRun = 0;
			} else {
				if (exetrace.isSelected()) {
					selectedRun = 1;
				} else {
					selectedRun = 2;
				}
			}
			
			exe2.setEnabled(selectedRun == 0);
			exe3.setEnabled(selectedRun == 1);
			exe4.setEnabled(selectedRun == 2);
			
	}
	
	public void makeSelectionCompile() {
			if (compile.isSelected()) {
				selectedCompile = 0;
			} else {
				selectedCompile = 1;
			}
			
			//code2.setEnabled(selectedCompile == 0);
			compiler1.setEnabled(selectedCompile == 0);
			compiler2.setEnabled(selectedCompile == 1);
			
	}
	
	public void makeSelectionViewTrace() {
			if (viewtrace.isSelected()) {
				selectedViewTrace = 0;
			} else {
				selectedViewTrace = 1;
			}
			
			simulationTraceFile.setEnabled(selectedViewTrace == 0);
			simulationsoclibTraceFile.setEnabled(selectedViewTrace == 1);
			
	}
    
    public void stopProcess() {
			try {
				rshc.stopFillJTA();
			} catch (LauncherException le) {
				
			}
			rshc = null;
			mode = 	STOPPED;
			setButtons();
			go = false;
    }
    
    public void startProcess() {
		startProcess = false;
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
        
        try {
            if (versionCodeGenerator.getSelectedIndex() == 0) {
				// Code generation
				if (jp1.getSelectedIndex() == 0) {
					jta.append("Generating executable code (C-POSIX version)\n");
					
					if (removeCFiles.isSelected()) {
						jta.append("Removing all .h files\n");
						list = FileUtils.deleteFiles(code1.getText() +  AVATAR2CPOSIX.getGeneratedPath(), ".h");
						if (list.length() == 0) {
							jta.append("No files were deleted\n");
						} else {
							jta.append("Files deleted:\n" + list + "\n");
						}
						jta.append("Removing all  .c files\n");
						list = FileUtils.deleteFiles(code1.getText() +  AVATAR2CPOSIX.getGeneratedPath(), ".c");
						if (list.length() == 0) {
							jta.append("No files were deleted\n");
						} else {
							jta.append("Files deleted:\n" + list + "\n");
						}
					}
					
					if (removeXFiles.isSelected()) {
						jta.append("Removing all .x files\n");
						list = FileUtils.deleteFiles(code1.getText() , ".x");
						if (list.length() == 0) {
							jta.append("No files were deleted\n");
						} else {
							jta.append("Files deleted:\n" + list + "\n");
						}
					}
					
					testGo();
					
					selectedItem = versionCodeGenerator.getSelectedIndex();
					selectedUnit = units.getSelectedIndex();
					//System.out.println("Selected item=" + selectedItem);
					if (selectedItem == 0) {
						AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();
						
						// Generating code
						if (avspec == null) {
							jta.append("Error: No AVATAR specification\n");
						} else {
							AVATAR2CPOSIX avatartocposix = new AVATAR2CPOSIX(avspec);
							avatartocposix.setTimeUnit(selectedUnit);
							avatartocposix.generateCPOSIX(debugmode.isSelected(), tracemode.isSelected());
							testGo();
							jta.append("Generation of C-POSIX executable code: done\n");
							//t2j.printJavaClasses();
							try {
								jta.append("Saving code in files\n");
								pathCode = code1.getText();
								avatartocposix.saveInFiles(pathCode);
								//tml2systc.saveFile(pathCode, "appmodel");
								jta.append("Code saved\n");
							} catch (Exception e) {
								jta.append("Could not generate files\n");
							}
						}
					}
					
					
				}
				
				testGo();
				
				
				// Compilation
				if (jp1.getSelectedIndex() == 1) {
					
					if (selectedCompile == 0) {
					cmd = compiler1.getText();
					} else {
						cmd = compiler2.getText();
					}
					
					jta.append("Compiling executable code with command: \n" + cmd + "\n");
					
					rshc = new RshClient(hostExecute);
					// Assuma data are on the remote host
					// Command
					try {
						data = processCmd(cmd);
						jta.append(data);
						jta.append("Compilation done\n");
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
				
				if (jp1.getSelectedIndex() == 2) {
					try {
						if (selectedRun == 0) {
							cmd = exe2.getText();
						} else {
							if (selectedRun == 1) {
								cmd = exe3.getText();
							} else {
								cmd = exe4.getText();
							}
						}
						
						jta.append("Executing code with command: \n" + cmd + "\n");
						
						rshc = new RshClient(hostExecute);
						// Assuma data are on the remote host
						// Command
						
						data = processCmd(cmd);
						jta.append(data);
						jta.append("Execution done\n");
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
				
				if ((hasError == false) && (jp1.getSelectedIndex() < 2)) {
					jp1.setSelectedIndex(jp1.getSelectedIndex() + 1);
				}
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
    
    public void showSimulationTrace() {
        JFrameSimulationSDPanel jfssdp = new JFrameSimulationSDPanel(f, mgui, "Simulation trace of " + simulationTraceFile.getText());
        jfssdp.setIconImage(IconManager.img8);
        jfssdp.setSize(600, 600);
        GraphicLib.centerOnParent(jfssdp);
		if (selectedViewTrace == 0) {
        jfssdp.setFileReference(simulationTraceFile.getText());
		} else {
			  jfssdp.setFileReference(simulationsoclibTraceFile.getText());
		}
        jfssdp.setVisible(true);
        TraceManager.addDev("Ok JFrame");
    }
	
	
}
