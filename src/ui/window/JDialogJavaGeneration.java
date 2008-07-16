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
 * Class JDialogJavaGeneration
 * Dialog for managing the generation and compilation of Java code
 * Creation: 16/05/2005
 * @version 1.0 16/05/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
//import java.util.*;

import myutil.*;
import translator.tojava.*;
import ui.*;


public class JDialogJavaGeneration extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface  {
    
    protected MainGUI mgui;
    
    private String textJava1 = "Generate Java code in";
    private String textJava2 = "Compile Java code in";
    //private String textJava3 = "with";
    private String textJava4 = "Execute Java application:";
    
    private static String unitNs = "0";
    private static String unitMs = "10";
    
    protected static String pathCode;
    protected static String pathCompiler;
    protected static String pathMainClass;
    protected static String pathExecute;
    protected static String pathClassExecute;
	protected static String javaHeader;
    
    protected String ttoolclasspath;
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
    
    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
    protected JLabel genJava, compJava, exeJava;
    protected JTextField code1, code2, unitms, unitns, compiler1, exe1, exe2, exe3;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox removeJavaFiles, removeClassFiles, debugmode, longforint;
    
    private Thread t;
    private boolean go = false;
    private ProcessThread pt;
    private boolean hasError = false;
    
    private TURTLE2Java t2j;
    
    
    /** Creates new form  */
    public JDialogJavaGeneration(Frame f, MainGUI _mgui, String title, String _pathCode, String _pathCompiler, String _ttoolclasspath, String _pathExecute, String _javaHeader) {
        super(f, title, true);
        
        mgui = _mgui;
        ttoolclasspath = _ttoolclasspath;
        
        if (pathCode == null) {
            pathCode = _pathCode;
        }
        
        if (pathCompiler == null)
            pathCompiler = _pathCompiler;
        
        if (pathMainClass == null) {
            pathMainClass = "MainClass_";
        }
        
        if (pathExecute == null)
            pathExecute = _pathExecute;
        
        
        if (pathClassExecute == null) {
            pathClassExecute = pathCode;
        }
		
		if (javaHeader == null) {
            javaHeader = _javaHeader;
			System.out.println("java Header = " + javaHeader);
        }
		
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
        jp03.setBorder(new javax.swing.border.TitledBorder("Code generation"));
        
        // first line panel01
        //c1.gridwidth = 3;
        /*c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;*/
        
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;
        
        genJava = new JLabel(textJava1);
        //genJava.addActionListener(this);
        jp01.add(genJava, c01);
        
        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);
        
        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c01.gridwidth = 1;
        jp01.add(new JLabel("1 time unit = "), c01);
        
        unitms = new JTextField(unitMs, 10);
        jp01.add(unitms, c01);
        
        jp01.add(new JLabel("milliseconds"), c01);
        
        unitns = new JTextField(unitNs, 10);
        jp01.add(unitns, c01);
        
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp01.add(new JLabel("nanoseconds"), c01);
        
        jp01.add(new JLabel("(Note : Nanoseconds are ignored for some TURTLE operators)"), c01);
        jp01.add(new JLabel(" "), c01);
        
        removeJavaFiles = new JCheckBox("Remove old .java files");
        removeJavaFiles.setSelected(true);
        jp01.add(removeJavaFiles, c01);
        
        removeClassFiles = new JCheckBox("Remove old .class files");
        removeClassFiles.setSelected(true);
        jp01.add(removeClassFiles, c01);
        
        debugmode = new JCheckBox("Put debug information in code");
        debugmode.setSelected(false);
        jp01.add(debugmode, c01);
        
        longforint = new JCheckBox("Use \"long\" instead of \"int\" for natural numbers");
        longforint.setSelected(false);
        jp01.add(longforint, c01);
        
        jp01.add(new JLabel(" "), c01);
        
        jp1.add("Generate code", jp01);
        
        
        // Panel 02
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;
        
        compJava = new JLabel(textJava2);
        //compJava.addActionListener(this);
        jp02.add(compJava, c02);
        
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
        exeJava = new JLabel(textJava4);
        //exeJava.addActionListener(this);
        jp03.add(exeJava, c03);
        
        exe1 = new JTextField(pathMainClass, 100);
        jp03.add(exe1, c03);
        
        jp03.add(new JLabel("with"), c03);
        
        exe2 = new JTextField(pathExecute, 100);
        jp03.add(exe2, c03);
        
        jp03.add(new JLabel("Classpath:"), c03);
        
        exe3 = new JTextField(pathClassExecute, 100);
        jp03.add(exe3, c03);
        
        jp03.add(new JLabel(" "), c03);
        
        jp1.add("Execute", jp03);
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch java code generation / compilation\n");
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
        go = false;
        if (pt != null) {
            pt.stopProcess();
        }
        mode = 	STOPPED;
        setButtons();
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
        String list;
        int milli = 0, nano = 0;
        
        hasError = false;
        
        try {
            
            // Code generation
            if (jp1.getSelectedIndex() == 0) {
                jta.append("Generating java code\n");
                
                if (removeJavaFiles.isSelected()) {
                    jta.append("Removing all old java files\n");
                    list = FileUtils.deleteFiles(code1.getText(), ".java");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                }
                
                if (removeClassFiles.isSelected()) {
                    jta.append("Removing all old class files\n");
                    list = FileUtils.deleteFiles(code1.getText(), ".class");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                }
                
                testGo();
                
                try {
                    milli = Integer.valueOf(unitms.getText()).intValue();
                    unitMs = unitms.getText();
                } catch (Exception e) {
                    jta.append("Wrong number of milliseconds: " + unitms.getText());
                    jta.append("Aborting");
                    jta.append("\n\nReady to process next command\n");
                    checkMode();
                    setButtons();
                    return;
                }
                
                try {
                    nano = Integer.valueOf(unitns.getText()).intValue();
                    unitNs = unitns.getText();
                } catch (Exception e) {
                    jta.append("Wrong number of nanoseconds: " + unitns.getText());
                    jta.append("Aborting");
                    jta.append("\n\nReady to process next command\n");
                    checkMode();
                    setButtons();
                    return;
                }
                
                
                t2j = new TURTLE2Java(mgui.gtm.getTURTLEModeling(), milli, nano, javaHeader);
                if (longforint.isSelected()) {
                    t2j.setLongSelected(true);
                }
                t2j.generateJava(debugmode.isSelected());
                testGo();
                jta.append("Java code generation done\n");
                //t2j.printJavaClasses();
                try {
                    jta.append("Generating java files\n");
                    pathCode = code1.getText();
                    t2j.saveJavaClasses(pathCode);
                    jta.append("Java files generated\n");
                } catch (Exception e) {
                    jta.append("Could not generate files\n");
                }
            }
            
            testGo();
            
            
            // Compilation
            if (jp1.getSelectedIndex() == 1) {
                
                String s;
                if (t2j == null) {
                    s = code2.getText() + "MainClass*.java";
                } else {
                    s = t2j.getMainListFiles(code2.getText());
                    if (s.length() == 0) {
                        s = code2.getText() + "MainClass*.java";
                    }
                }
                cmd = compiler1.getText() + " -classpath " + ttoolclasspath + " " + s;
                
                jta.append("Compiling java code with command: \n" + cmd + "\n");
                
                pt = new ProcessThread(cmd, this);
                pt.start();
                
                Thread.currentThread().sleep(250);
                
                while(pt.isStarted() == true) {
                    Thread.currentThread().sleep(250);
                }
                jta.append("Compilation done\n");
                
            }
            
            // Execution
            if (jp1.getSelectedIndex() == 2) {
                
                cmd = exe2.getText() + " -classpath " + exe3.getText() + " " + exe1.getText();
                
                jta.append("Executing java code with command: \n" + cmd + "\n");
                
                pt = new ProcessThread(cmd, this);
                pt.start();
                
                Thread.currentThread().sleep(250);
                
                while(pt.isStarted() == true) {
                    Thread.currentThread().sleep(250);
                }
                
                jta.append("Execution done\n");
                
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
