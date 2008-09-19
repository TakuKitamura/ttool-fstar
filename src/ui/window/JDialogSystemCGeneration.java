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
 * Class JDialogSystemCGeneration
 * Dialog for managing the generation and compilation of SystemC code
 * Creation: 01/12/2005
 * @version 1.1 03/09/2007
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import myutil.*;
//import tmltranslator.tosystemc.*;
import tmltranslator.tomappingsystemc.*;
import tmltranslator.tomappingsystemc2.*;
import ui.*;

import launcher.*;


public class JDialogSystemCGeneration extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface  {
    
    protected MainGUI mgui;
    
    private String textSysC1 = "Generate SystemC code in";
    private String textSysC2 = "Compile SystemC code in";
    //private String textSysC3 = "with";
    private String textSysC4 = "Execute SystemC application:";
    
    private static String unitCycle = "1";
	
	private static String[] simus = {"SystemC Simulator - LabSoC version", "C++ Simulator - LabSoc version"};
	private static int selectedItem = 0;
    
    protected static String pathCode;
    protected static String pathCompiler;
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
    
    protected JLabel gen, comp, exe;
    protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox removeCppFiles, removeXFiles, debugmode;
	protected JComboBox versionSimulator;
    
    private Thread t;
    private boolean go = false;
    //private ProcessThread pt;
    private boolean hasError = false;
    
    //private TURTLE2Java t2j;
    
    private String hostSystemC;
    
    protected RshClient rshc;
    
    
    /** Creates new form  */
    public JDialogSystemCGeneration(Frame f, MainGUI _mgui, String title, String _hostSystemC, String _pathCode, String _pathCompiler, String _pathExecute) {
        super(f, title, true);
        
        mgui = _mgui;
        
        if (pathCode == null) {
            pathCode = _pathCode;
        }
        
        if (pathCompiler == null)
            pathCompiler = _pathCompiler;
        
        if (pathExecute == null)
            pathExecute = _pathExecute;
        
        hostSystemC = _hostSystemC;
        
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
        
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp01.add(new JLabel("1 time unit = "), c01);
        
        unitcycle = new JTextField(unitCycle, 10);
        jp01.add(unitcycle, c01);
        
        jp01.add(new JLabel("cycle"), c01);
        
        removeCppFiles = new JCheckBox("Remove old .h / .cpp  files");
        removeCppFiles.setSelected(true);
        jp01.add(removeCppFiles, c01);
        
        removeXFiles = new JCheckBox("Remove old .x files");
        removeXFiles.setSelected(true);
        jp01.add(removeXFiles, c01);
        
        debugmode = new JCheckBox("Put debug information in code");
        debugmode.setSelected(false);
        jp01.add(debugmode, c01);
		
		jp01.add(new JLabel("Simulator used:"), c01);
		
		versionSimulator = new JComboBox(simus);
		versionSimulator.setSelectedItem(selectedItem);
		jp01.add(versionSimulator, c01);
		//devmode = new JCheckBox("Development version of the simulator");
        //devmode.setSelected(true);
        //jp01.add(devmode, c01);
        
        jp01.add(new JLabel(" "), c01);
        jp1.add("Generate code", jp01);
        
        
        // Panel 02
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;
        
        comp = new JLabel(textSysC2);
        //compJava.addActionListener(this);
        jp02.add(comp, c02);
        
        code2 = new JTextField(pathCode, 100);
        jp02.add(code2, c02);
        
        jp02.add(new JLabel("with"), c02);
        
        compiler1 = new JTextField(pathCompiler, 100);
        jp02.add(compiler1, c02);
        
        jp02.add(new JLabel(" "), c02);
        
        jp1.add("Compile", jp02);
        
        // Panel 03
        c03.gridheight = 1;
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;
        exe = new JLabel(textSysC4);
        //exeJava.addActionListener(this);
        jp03.add(exe, c03);
        
        exe2 = new JTextField(pathExecute, 100);
        jp03.add(exe2, c02);
        
        jp03.add(new JLabel(" "), c03);
        
        jp1.add("Execute", jp03);
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch SystemC code generation / compilation\n");
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
        //System.out.println("Actions");
        
        // Compare the action command to the known actions.
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
            
            // Code generation
            if (jp1.getSelectedIndex() == 0) {
                jta.append("Generating SystemC code\n");
                
                if (removeCppFiles.isSelected()) {
					jta.append("Removing all old h files\n");
                    list = FileUtils.deleteFiles(code1.getText(), ".h");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                    jta.append("Removing all old cpp files\n");
                    list = FileUtils.deleteFiles(code1.getText(), ".cpp");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                }
                
                if (removeXFiles.isSelected()) {
                    jta.append("Removing all old x files\n");
                    list = FileUtils.deleteFiles(code1.getText(), ".x");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                }
                
                testGo();
                
                try {
                    cycle = Integer.valueOf(unitcycle.getText()).intValue();
                } catch (Exception e) {
                    jta.append("Wrong number of cycles: " + unitcycle.getText());
                    jta.append("Aborting");
                    jta.append("\n\nReady to process next command\n");
                    checkMode();
                    setButtons();
                    return;
                }
                
                /*TML2SystemC tml2systc = new TML2SystemC(mgui.gtm.getTMLModeling());
                tml2systc.generateSystemC(debugmode.isSelected());
                
                testGo();
                jta.append("SystemC code generation done\n");
                //t2j.printJavaClasses();
                try {
                    jta.append("Generating SystemC file\n");
                    pathCode = code1.getText();
                    tml2systc.saveFile(pathCode, "appmodel");
                    jta.append("SystemC file generated\n");
                } catch (Exception e) {
                    jta.append("Could not generate SystemC file\n");
                }*/
				
				selectedItem = versionSimulator.getSelectedIndex();
				if (selectedItem == 0) {
					tmltranslator.tomappingsystemc.TML2MappingSystemC tml2systc;
					if (mgui.gtm.getTMLMapping() == null) {
						tml2systc = new tmltranslator.tomappingsystemc.TML2MappingSystemC(mgui.gtm.getTMLModeling());
					} else {
						tml2systc = new tmltranslator.tomappingsystemc.TML2MappingSystemC(mgui.gtm.getTMLMapping());
					}
					tml2systc.generateSystemC(debugmode.isSelected());
					testGo();
					jta.append("SystemC code generation done\n");
					//t2j.printJavaClasses();
					try {
						jta.append("Generating SystemC file\n");
						pathCode = code1.getText();
						tml2systc.saveFile(pathCode, "appmodel");
						jta.append("SystemC files generated\n");
					} catch (Exception e) {
						jta.append("Could not generate SystemC file\n");
					}
				} else {
					tmltranslator.tomappingsystemc2.TML2MappingSystemC tml2systc;
					if (mgui.gtm.getTMLMapping() == null) {
						tml2systc = new tmltranslator.tomappingsystemc2.TML2MappingSystemC(mgui.gtm.getTMLModeling());
					} else {
						tml2systc = new tmltranslator.tomappingsystemc2.TML2MappingSystemC(mgui.gtm.getTMLMapping());
					}
					tml2systc.generateSystemC(debugmode.isSelected());
					testGo();
					jta.append("SystemC code generation done\n");
					//t2j.printJavaClasses();
					try {
						jta.append("Generating SystemC file\n");
						pathCode = code1.getText();
						tml2systc.saveFile(pathCode, "appmodel");
						jta.append("SystemC files generated\n");
					} catch (Exception e) {
						jta.append("Could not generate SystemC file\n");
					}
				}
                
                
            }
            
            testGo();
            
            
            // Compilation
            if (jp1.getSelectedIndex() == 1) {
                
                cmd = compiler1.getText();
                
                jta.append("Compiling SystemC code with command: \n" + cmd + "\n");
                
                rshc = new RshClient(hostSystemC);
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
                    cmd = exe2.getText();
                    
                    jta.append("Executing SystemC code with command: \n" + cmd + "\n");
                    
                    rshc = new RshClient(hostSystemC);
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
            
        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }
        
        jta.append("\n\nReady to process next command\n");
        
        checkMode();
        setButtons();
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
