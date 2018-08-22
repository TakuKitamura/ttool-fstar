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

import syscamstranslator.toSysCAMS.MakefileCode;
import syscamstranslator.toSysCAMS.TopCellGenerator;
import syscamstranslator.toSysCAMSCluster.TopCellGeneratorCluster;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.*;
import syscamstranslator.*;
import ui.util.IconManager;
import ui.MainGUI;
import ui.SysCAMSPanelTranslator;
import ui.syscams.SysCAMSComponentTaskDiagramPanel;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.*;
import org.apache.commons.math3.fraction.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Arrays;

/**
 * Class JDialogSysCAMSExecutableCodeGeneration
 * Dialog for managing the generation and compilation of SystemC-AMS executable code
 * Creation: 23/05/2018
 * @version 1.0 23/05/2018
 * @author Irina Kit Yan LEE, 
 * @version 1.1 06/08/2018
 * @author Rodrigo CORTES PORTO
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSExecutableCodeGeneration extends javax.swing.JFrame implements ActionListener, Runnable, MasterProcessInterface {

    protected Frame f;
    protected MainGUI mgui;

    private String textSysC1 = "Base directory of code generation:";
    private String textSysC7 = "Base directory of topcell generation:";
    private String textSysC8 = "Base directory of Makefile:";
//    private String textSysC2 = "Compile SystemC-AMS executable with"; // compile
//    private String textSysC4 = "Run code in soclib / mutekh:";
//    private String textSysC5 = "Show AVATAR trace from file w/o hardware:";
//    private String textSysC6 = "Show cycle accurate trace from MPSoC file:";

//    private static String[] codes = {"AVATAR SOCLIB"};

//    private static int selectedRun = 1;
//    private static int selectedCompile = 0;
//    private static int selectedViewTrace = 0;
//    private static boolean static_putUserCode = true;

    protected static String pathCode;
    protected static String pathSoclibTraceFile;
    protected static String pathCompileMPSoC;
    protected static String pathExecuteMPSoC;
    protected static boolean optimizeModeSelected = true;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    private static

    int mode;

    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;

    protected JRadioButton exe, exeint, exetrace, exesoclib, compile, compilesoclib, viewtrace, viewtracesoclib;
    protected ButtonGroup compilegroup, exegroup, viewgroup;
    protected JLabel gen;
    protected JTextField code1, code2, code3, compiler, exe1, exe2, exe3, exe4, exe2int, simulationTraceFile, simulationsoclibTraceFile;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox removeCFiles, removeXFiles, debugmode, tracemode, optimizemode, putUserCode;
    protected JComboBox<String> versionCodeGenerator, units;
    protected JButton showSimulationTrace, showOverflowStatus;

//    private static int selectedUnit = 2;
//    private static boolean removeCFilesValue = true;
//    private static boolean removeXFilesValue = true;
//    private static boolean debugValue = false;
//    private static boolean tracingValue = false;
//    private static boolean optimizeValue = true;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    protected boolean startProcess = false;

//    private String hostExecute;

    protected RshClient rshc;

    public JDialogSysCAMSExecutableCodeGeneration(Frame _f, MainGUI _mgui, String title, String _pathCode) {
        super(title);
        f = _f;
        mgui = _mgui;

        if (pathCode == null) {
            pathCode = _pathCode;
        }

//        if (pathCompileMPSoC == null) {
//            pathCompileMPSoC = _pathCompileMPSoC;
//        }
//
//        if (pathExecuteMPSoC == null) {
//            pathExecuteMPSoC = _pathExecuteMPSoC;
//        }
//
//        hostExecute = _hostExecute;

        initComponents();
        myInitComponents();
        pack();

        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
//        makeSelectionCompile();
//        makeSelectionExecute();
//        makeSelectionViewTrace();
    }

    protected void initComponents() {
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        // Issue #41 Ordering of tabbed panes 
        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();
        
        JPanel jp00 = new JPanel();
        GridBagLayout gridbag00 = new GridBagLayout();
        GridBagConstraints c00 = new GridBagConstraints();
        jp00.setLayout(gridbag00);
        jp00.setBorder(new javax.swing.border.TitledBorder("Validation"));
       
        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Code generation"));
        
         // Panel 00 -> Validation
        c00.gridheight = 1;
        c00.weighty = 1.0;
        c00.weightx = 1.0;
        c00.gridwidth = GridBagConstraints.REMAINDER; //end row
        c00.fill = GridBagConstraints.BOTH;
        c00.gridheight = 1;

        jp00.add(new JLabel(" "), c00);
        c00.gridwidth = GridBagConstraints.REMAINDER; //end row

        jp1.add("Validation", jp00);

        // Panel 01 -> Code Generation
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        gen = new JLabel(textSysC1);
        jp01.add(gen, c01);

        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);

        gen = new JLabel(textSysC7);
        jp01.add(gen, c01);

        code2 = new JTextField(pathCode, 100);
        jp01.add(code2, c01);
        
        gen = new JLabel(textSysC8);
        jp01.add(gen, c01);
        
        code3 = new JTextField(pathCode, 100);
        jp01.add(code3, c01);

        jp01.add(new JLabel(" "), c01);
        //c01.gridwidth = GridBagConstraints.REMAINDER; //end row

//        removeCFiles = new JCheckBox("Remove .c / .h files");
//        removeCFiles.setSelected(removeCFilesValue);
//        jp01.add(removeCFiles, c01);

//        removeXFiles = new JCheckBox("Remove .x files");
//        removeXFiles.setSelected(removeXFilesValue);
//        jp01.add(removeXFiles, c01);

//        debugmode = new JCheckBox("Put debug information in generated code");
//        debugmode.setSelected(debugValue);
//        jp01.add(debugmode, c01);
//
//        tracemode = new JCheckBox("Put tracing capabilities in generated code");
//        tracemode.setSelected(tracingValue);
//        jp01.add(tracemode, c01);
//
//        optimizemode = new JCheckBox("Optimize code");
//        optimizemode.setSelected(optimizeModeSelected);
//        jp01.add(optimizemode, c01);
//
//        putUserCode = new JCheckBox("Include user code");
//        putUserCode.setSelected(static_putUserCode);
//        jp01.add(putUserCode, c01);

//        jp01.add(new JLabel("1 time unit ="), c01);
//
//        units = new JComboBox<String>(unitTab);
//        units.setSelectedIndex(selectedUnit);
//        units.addActionListener(this);
//        jp01.add(units, c01);

//        jp01.add(new JLabel("Code generator used:"), c01);

//        versionCodeGenerator = new JComboBox<String>(codes);
        // versionCodeGenerator.setSelectedIndex(selectedItem);
//        versionCodeGenerator.addActionListener(this);
//        jp01.add(versionCodeGenerator, c01);

        jp01.add(new JLabel(" "), c01);
        jp1.add("Generate code", jp01);

        // Panel 02 -> compile
//        c02.gridheight = 1;
//        c02.weighty = 1.0;
//        c02.weightx = 1.0;
//        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
//        c02.fill = GridBagConstraints.BOTH;
//        c02.gridheight = 1;

//        compilegroup = new ButtonGroup();
//
//        compilesoclib = new JRadioButton(textSysC2, false);
//        compilesoclib.addActionListener(this);
//        jp02.add(compilesoclib, c02);
//        compilegroup.add(compilesoclib);
//        compiler = new JTextField(pathCompileMPSoC, 100);
//        jp02.add(compiler, c02);

        //compile.setSelected(selectedCompile == 0);
//        compilesoclib.setSelected(selectedCompile == 1);
//
//        jp1.add("Compile", jp02);

        // Panel 03 -> Execute
//        c03.gridheight = 1;
//        c03.weighty = 1.0;
//        c03.weightx = 1.0;
//        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
//        c03.fill = GridBagConstraints.BOTH;
//        c03.gridheight = 1;

//        exegroup = new ButtonGroup();

        /*exetrace = new JRadioButton(textSysC3, false);
        exetrace.addActionListener(this);
        exegroup.add(exetrace);
        jp03.add(exetrace, c03);
        exe3 = new JTextField(pathExecuteMPSoC+"-trace", 100);
        jp03.add(exe3, c03);*/
//
//        exesoclib = new JRadioButton(textSysC4, false);
//        exesoclib.addActionListener(this);
//        exegroup.add(exesoclib);
//        jp03.add(exesoclib, c03);
//        exe4 = new JTextField(pathExecuteMPSoC, 100);
//        jp03.add(exe4, c03);

        //exe.setSelected(selectedRun == 0);
        //exetrace.setSelected(selectedRun == 1);
      //  exesoclib.setSelected(selectedRun == 2);

//        jp03.add(new JLabel(" "), c03);

//        jp1.add("Execute", jp03);

        // Panel 04 -> View trace
//        c04.gridheight = 1;
//        c04.weighty = 1.0;
//        c04.weightx = 1.0;
//        c04.gridwidth = GridBagConstraints.REMAINDER; //end row
//        c04.fill = GridBagConstraints.HORIZONTAL;
//        c04.gridheight = 1;

//        viewgroup = new ButtonGroup();
//        viewtrace = new JRadioButton(textSysC5, false);
//        viewgroup.add(viewtrace);
//        viewtrace.addActionListener(this);
//        jp04.add(viewtrace, c04);
//        simulationTraceFile = new JTextField(pathCode + "trace.txt", 100);
//        jp04.add(simulationTraceFile, c04);
//        viewtracesoclib = new JRadioButton(textSysC6, false);
//        viewgroup.add(viewtracesoclib);
//        viewtracesoclib.addActionListener(this);
//        jp04.add(viewtracesoclib, c04);
//        simulationsoclibTraceFile = new JTextField(pathSoclibTraceFile, 100);
//        jp04.add(simulationsoclibTraceFile, c04);
//
//        showSimulationTrace = new JButton("Show simulation trace");
//        showSimulationTrace.addActionListener(this);
//        jp04.add(showSimulationTrace, c04);

        //-------------Ajout C.Demarigny---------------

//        showOverflowStatus = new JButton("Show overflow status");
//        showOverflowStatus.addActionListener(this);
//        jp04.add(showOverflowStatus, c04);

        //----------------Fin ajout--------------------

//        viewtrace.setSelected(selectedViewTrace == 0);
//        viewtracesoclib.setSelected(selectedViewTrace == 1);

//        jp1.add("Results", jp04);

        c.add(jp1, BorderLayout.NORTH);

        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch validation / code generation\n"); // / compilation / execution
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

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Start")) {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (evt.getSource() == versionCodeGenerator) {
            // selectedItem = versionCodeGenerator.getSelectedIndex();
//        } else if (evt.getSource() == units) {
//            selectedUnit = units.getSelectedIndex();
//        } else if (evt.getSource() == showSimulationTrace) {
//            showSimulationTrace();
        //} else if ((evt.getSource() == exe) || (evt.getSource() == exetrace)|| (evt.getSource() == exesoclib)) {
//        else if ((evt.getSource() == exetrace) || (evt.getSource() == exesoclib)) {
//            makeSelectionExecute();
//            // } else if ((evt.getSource() == compile) || (evt.getSource() == compilesoclib)) {
//        } else if ((evt.getSource() == compilesoclib)) {
//            makeSelectionCompile();
//        } else if ((evt.getSource() == viewtrace) || (evt.getSource() == viewtracesoclib)) {
//            makeSelectionViewTrace();
//        } else if ((evt.getSource() == showOverflowStatus)) { //ajout CD
//            showOverflowStatus();
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
//        optimizeModeSelected = optimizemode.isSelected();
//        removeCFilesValue = removeCFiles.isSelected();
//        removeXFilesValue = removeXFiles.isSelected();
//        debugValue = debugmode.isSelected();
//        tracingValue = tracemode.isSelected();
//        static_putUserCode = putUserCode.isSelected();
        dispose();
    }
//
//    public void makeSelectionExecute() {
//
//        //if (exetrace.isSelected()) {
//        //    selectedRun = 1;
//        //} else {
//        selectedRun = 2;
//        //}
//
//        // exe2.setEnabled(selectedRun == 0);
//        //exe3.setEnabled(selectedRun == 1);
//        exe4.setEnabled(selectedRun == 2);
//    }

//    public void makeSelectionCompile() {
//
//        selectedCompile = 1;
//        compiler.setEnabled(selectedCompile == 1);
//
//    }

//    public void makeSelectionViewTrace() {
//        if (viewtrace.isSelected()) {
//            selectedViewTrace = 0;
//        } else {
//            selectedViewTrace = 1;
//        }
//        simulationTraceFile.setEnabled(selectedViewTrace == 0);
//        // simulationsoclibTraceFile.setEnabled(selectedViewTrace == 1);
//    }

    public void stopProcess() {
        try {
            rshc.stopCommand();
        } catch (LauncherException le) {

        }
        rshc = null;
        mode = STOPPED;
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
//        String cmd;
//        String list;//, data;
        hasError = false;

        try {
            if (jp1.getSelectedIndex() == 0) {
                Vector<SysCAMSComponentTaskDiagramPanel> syscamsDiagramPanels = mgui.getListSysCAMSPanel();
                for (SysCAMSComponentTaskDiagramPanel syscamsDiagramPanel : syscamsDiagramPanels) {
                    SysCAMSPanelTranslator syscamspaneltranslator = new SysCAMSPanelTranslator(syscamsDiagramPanel);
                    SysCAMSSpecification syscalsspec = syscamspaneltranslator.getSysCAMSSpecification();
                    if (syscalsspec == null) {
                        jta.append("Error: No SYSCAMS specification\n");
                    } else {
                        jta.append("\nPerforming Validation for \""+(syscalsspec.getCluster()).getClusterName()+"\".\n");
                        LinkedList<SysCAMSTConnector> connectors = syscalsspec.getAllConnectorsCluster4Matrix();
                        System.out.printf("Connectors for 1 cluster = %d.\n", connectors.size());
                        LinkedList<SysCAMSTBlockTDF> tdfBlocks = syscalsspec.getAllBlockTDF();
                        System.out.printf("Blocks for 1 cluster = %d.\n", tdfBlocks.size());
                        LinkedList<SysCAMSTConnector> connectorsTdfDe = syscalsspec.getAllConnectorsTdfDe();
                        System.out.printf("ConnectorsTdfDe for 1 cluster = %d.\n", connectorsTdfDe.size());
                        for(SysCAMSTBlockTDF tdfBlock : tdfBlocks) {
                            if(tdfBlock.getPeriod() < 0) {
                                tdfBlock.setPeriod(0);
                            }
                            for(SysCAMSTPortTDF portTdf : tdfBlock.getPortTDF()) {
                                if(portTdf.getPeriod() < 0) {
                                    portTdf.setPeriod(0);
                                }
                                if(portTdf.getRate() < 0) {
                                    portTdf.setRate(1);
                                }
                                if(portTdf.getDelay() < 0) {
                                    portTdf.setDelay(0);
                                }
                            }
                            for(SysCAMSTPortConverter portConverter : tdfBlock.getPortConverter()) {
                                if(portConverter.getPeriod() < 0) {
                                    portConverter.setPeriod(0);
                                }
                                if(portConverter.getRate() < 0) {
                                    portConverter.setRate(1);
                                }
                                if(portConverter.getDelay() < 0) {
                                    portConverter.setDelay(0);
                                }
                            }
                        }
                        
                        //Validate Block parameters (rate, Tp, Tm, Delay) and propagate timesteps to all blocks
                        try {
                        //if(!propagateTimestep(connectorsTdfDe, tdfBlocks)){
                            propagateTimestep(connectorsTdfDe, tdfBlocks);
                            //System.out.println("Error in propagateTimestep");
                        //}
                        
                            //TODO-DELETE THIS, after debugging:
                            for(SysCAMSTBlockTDF tdfBlock : tdfBlocks) {
                                System.out.println("params of Block: " +tdfBlock.getName());
                                System.out.println("Tm: " + tdfBlock.getPeriod());
                                for(SysCAMSTPortTDF portTdf : tdfBlock.getPortTDF()) {
                                    System.out.println("Port: " + portTdf.getName());
                                    System.out.println("Tp: " + portTdf.getPeriod());
                                    System.out.println("Rate: " + portTdf.getRate());
                                    System.out.println("Delay: " + portTdf.getDelay());
                                }
                                
                                for(SysCAMSTPortConverter portConverter : tdfBlock.getPortConverter()) {
                                    System.out.println("Port: " + portConverter.getName());
                                    System.out.println("Tp: " + portConverter.getPeriod());
                                    System.out.println("Rate: " + portConverter.getRate());
                                    System.out.println("Delay: " + portConverter.getDelay());
                                }
                            }
                            
                            //TODO: verify trivial case when only 1 block and no connectors. 
                            if(connectors.size() > 0) {
                                boolean recompute = false;
                                boolean suggest_delays = false;
                                RealVector buffer = new ArrayRealVector(connectors.size());
                                RealVectorFormat printFormat = new RealVectorFormat();
                                RealMatrix topologyMatrix = buildTopologyMatrix(connectors, tdfBlocks, buffer);
                                System.out.println("Buffer after topMatrix is: " + printFormat.format(buffer) );
                                //try {
                                RealVector execRate = solveTopologyMatrix(topologyMatrix, tdfBlocks);
                                //TODO: to recompute missing delays in loops: modify buffer with the suggested delay, and loop.
                                do {
                                    recompute = computeSchedule(execRate, topologyMatrix, buffer, tdfBlocks, connectors);
                                    if(recompute)
                                        suggest_delays = true;
                                } while (recompute);
                                if(suggest_delays){
                                    jta.append("The following delays are suggested to solve synchronization issues between DE and TDF modules:\n");
                                    for(SysCAMSTBlockTDF tdfBlock : tdfBlocks){
                                        for(SysCAMSTPortConverter portConverter : tdfBlock.getPortConverter()) {
                                            if(portConverter.getRecompute()){
                                                jta.append("In Block: \"" + tdfBlock.getName() + "\". Port: \"" + portConverter.getName() + "\". Insert delay of " + portConverter.getDelay() + "\n");
                                            }
                                        }
                                    }
                                }
                                jta.append("Validation for \""+(syscalsspec.getCluster()).getClusterName()+"\" completed.\n");
                            }
                        } catch (InterruptedException ie) {
                            System.err.println("Interrupted");
                            jta.append("Interrupted\n");
                            mode = STOPPED;
                            setButtons();
                            hasError = true;
                            //return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            mode = STOPPED;
                            setButtons();
                            hasError = true;
                            return;
                        }
                    }
                }
            }
            
            testGo();
            
            if (jp1.getSelectedIndex() == 1) {
                jta.append("Generating executable code (SystemC-AMS version)\n");

                Vector<SysCAMSComponentTaskDiagramPanel> syscamsDiagramPanels = mgui.getListSysCAMSPanel();
                LinkedList<SysCAMSTCluster> clusters = new LinkedList<SysCAMSTCluster>();
                for (SysCAMSComponentTaskDiagramPanel syscamsDiagramPanel : syscamsDiagramPanels) {
                	SysCAMSPanelTranslator syscamspaneltranslator = new SysCAMSPanelTranslator(syscamsDiagramPanel);
                	SysCAMSSpecification syscalsspec = syscamspaneltranslator.getSysCAMSSpecification();
                	clusters.add(syscalsspec.getCluster());
                }
                for (SysCAMSComponentTaskDiagramPanel syscamsDiagramPanel : syscamsDiagramPanels) {
                	SysCAMSPanelTranslator syscamspaneltranslator = new SysCAMSPanelTranslator(syscamsDiagramPanel);
                	SysCAMSSpecification syscalsspec = syscamspaneltranslator.getSysCAMSSpecification();

                	// Generating code
                	if (syscalsspec == null) {
                		jta.append("Error: No SYSCAMS specification\n");
                	} else {
                		System.err.println("**SYSCAMS TOPCELL found");

                		TopCellGenerator topCellGenerator = new TopCellGenerator(syscalsspec);
                        TopCellGeneratorCluster topCellGeneratorCluster = new TopCellGeneratorCluster(syscalsspec);
                		testGo();
                		jta.append("Generation of TopCell \"" + syscalsspec.getCluster().getClusterName() + "\" executable code: done\n");

                		try {
                			jta.append("Saving SysCAMS code in files\n");
                			System.err.println("Saving SysCAMS code in files\n");
                			pathCode = code2.getText();

                			System.err.println("SYSCAMS TOPCELL : " + syscalsspec.getCluster().getClusterName() + "saved in " + code2.getText());
                			topCellGenerator.saveFile(pathCode);
                            topCellGeneratorCluster.saveFile(pathCode);

                			jta.append("Code saved\n");
                		} catch (Exception e) {
                			jta.append("Could not generate files\n");
                			System.err.println("Could not generate SysCAMS files\n");
                			e.printStackTrace();
                		}
                	}
                	testGo();
                }
                try {
        			String makefile;
        			System.err.println(pathCode + "Makefile");
        			FileWriter fw = new FileWriter(pathCode + "/" + "Makefile");
        			makefile = MakefileCode.getMakefileCode(clusters);
        			fw.write(makefile);
        			fw.close();
        		} catch (Exception ex) {
        			ex.printStackTrace();
        		}
                testGo();
            }
//                if (removeCFiles.isSelected()) {
//
//                    jta.append("Removing all .h files\n");
//
//                    list = FileUtils.deleteFiles(code1.getText() + TasksAndMainGenerator.getGeneratedPath(), ".h");
//                    if (list.length() == 0) {
//                        jta.append("No files were deleted\n");
//                    } else {
//                        jta.append("Files deleted:\n" + list + "\n");
//                    }
//                    jta.append("Removing all  .c files\n");
//                    list = FileUtils.deleteFiles(code1.getText() + TasksAndMainGenerator.getGeneratedPath(), ".c");
//                    if (list.length() == 0) {
//                        jta.append("No files were deleted\n");
//                    } else {
//                        jta.append("Files deleted:\n" + list + "\n");
//                    }
//                }

//                if (removeXFiles.isSelected()) {
//                    jta.append("Removing all .x files\n");
//                    list = FileUtils.deleteFiles(code1.getText(), ".x");
//                    if (list.length() == 0) {
//                        jta.append("No files were deleted\n");
//                    } else {
//                        jta.append("Files deleted:\n" + list + "\n");
//                    }
//                }

//                testGo();

//                selectedUnit = units.getSelectedIndex();

                // Generating code
//                if (avspec == null) {
//                    jta.append("Error: No AVATAR specification\n");
//                } else {
//
//                    TasksAndMainGenerator gene = new TasksAndMainGenerator(syscalsspec, avspec);
//                    gene.includeUserCode(putUserCode.isSelected());
//                    gene.setTimeUnit(selectedUnit);
//                    gene.generateSoclib(debugmode.isSelected(), tracemode.isSelected());
//
//                    if (syscalsspec == null) {
//                        jta.append("Error: No AVATAR Deployment specification\n");
//                    } else {
//                        System.err.println("AVATAR TOPCELL found");
//                    }
//
//                    TopCellGenerator topCellGenerator = new TopCellGenerator(syscalsspec, tracemode.isSelected(), avspec);
//                    testGo();
//                    jta.append("Generation of TopCell executable code: done\n");
//
//                    try {
//                        jta.append("Saving code in files\n");
//                        pathCode = code2.getText();
//                        topCellGenerator.saveFile(pathCode);
//
//                        jta.append("Code saved\n");
//                    } catch (Exception e) {
//                        jta.append("Could not generate files\n");
//                    }
//
//                    testGo();
//                    jta.append("Generation of C-SOCLIB executable code: done\n");
//                    try {
//                        jta.append("Saving code in files\n");
//                        pathCode = code1.getText();
//                        gene.saveInFiles(pathCode);
//
//                        jta.append("Code saved\n");
//                    } catch (Exception e) {
//                        jta.append("Could not generate files\n");
//                    }
//                }
//            }

//            testGo();

//            if (jp1.getSelectedIndex() == 1) {
//                cmd = compiler.getText();
//                jta.append("Compiling executable code with command: \n" + cmd + "\n");
//
//                rshc = new RshClient(hostExecute);
//                try {
//                    processCmd(cmd, jta);
//                    jta.append("Compilation done\n");
//                } catch (LauncherException le) {
//                    jta.append("Error: " + le.getMessage() + "\n");
//                    mode = STOPPED;
//                    setButtons();
//                    return;
//                } catch (Exception e) {
//                    mode = STOPPED;
//                    setButtons();
//                    return;
//                }
//            }
//
//            if (jp1.getSelectedIndex() == 2) {
//                try {
//                    cmd = exe4.getText();
//                    jta.append("Executing code with command: \n" + cmd + "\n");
//                    rshc = new RshClient(hostExecute);
//                    processCmd(cmd, jta);
//                    jta.append("Execution done\n");
//                } catch (LauncherException le) {
//                    jta.append("Error: " + le.getMessage() + "\n");
//                    mode = STOPPED;
//                    setButtons();
//                    return;
//                } catch (Exception e) {
//                    mode = STOPPED;
//                    setButtons();
//                    return;
//                }
//            }
            if ((hasError == false) && (jp1.getSelectedIndex() < 1)) {
                jp1.setSelectedIndex(jp1.getSelectedIndex() + 1);
            }
        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }
        if (!hasError) {
            jta.append("\n\nReady to process next command\n");

            checkMode();
            setButtons();
        }
    }

    protected void processCmd(String cmd, JTextArea _jta) throws LauncherException {
        rshc.setCmd(cmd);
        rshc.sendExecuteCommandRequest();
        final Writer output = new StringWriter();
        rshc.writeCommandMessages(output);
        _jta.append(output.toString());

        return;
    }

    protected void checkMode() {
        mode = NOT_STARTED;
    }

    protected void setButtons() {
        switch (mode) {
            case NOT_STARTED:
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
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
//        hasError = true;
    }

    public void propagateTimestep(LinkedList<SysCAMSTConnector> connectors, LinkedList<SysCAMSTBlockTDF> tdfBlocks) throws InterruptedException {
        for(SysCAMSTBlockTDF tdfBlock : tdfBlocks) {
            if(tdfBlock.getPeriod() > 0){
                if(!propagateTm(tdfBlock, connectors)){
                    throw new InterruptedException(); 
                }
                return;
            }
            else {
                for(SysCAMSTPortTDF tdfPort : tdfBlock.getPortTDF()) {
                    if(tdfPort.getPeriod() > 0){
                        if (!propagateTpTdf(tdfBlock, tdfPort, connectors)) {
                            throw new InterruptedException(); 
                        }
                        return;
                    }
                }
                for(SysCAMSTPortConverter converterPort : tdfBlock.getPortConverter()) {
                    if(converterPort.getPeriod() > 0){
                        if(!propagateTpConverter(tdfBlock, converterPort, connectors)) {
                            throw new InterruptedException(); 
                        }
                        return;
                    }
                }
            }
        }
        jta.append("Error: At least one Module or Port Timestep should be entered in at least one TDF block of this cluster.\n");
        throw new InterruptedException(); 
    }
    
    public boolean propagateTm(SysCAMSTBlockTDF tdfBlock, LinkedList<SysCAMSTConnector> connectors) {
        /* * 1. Get period Tm of tdfBlock
         * for each port (TDF and converter)
         *      if Tp > 0
         *          validate timestep consistency against Tm.
         *      else: set Tp //(setPeriod of port - calcuating it).
         *      if it has TDF connected blocks
         *          if Tp of connected block <= 0 (unvisited)
         *              set Tp of connected port block
         *              propagateTp(nextTdfBlock);
         *          else //(Tp of connected block > 0)
         *              validate timestep consitency between 2 ports.
         *              if connected block is unvisted
         *                  propagateTp(nextTdfBlock);
         * return
         * */
        double tm = tdfBlock.getPeriod();
        double tp;
        int rate;
        //Mark this tdf block as visited.
        tdfBlock.setIsTimestepPropagated();
        SysCAMSTPortTDF p1_tdfPort, p2_tdfPort;
        for(SysCAMSTPortTDF tdfPort : tdfBlock.getPortTDF()) {
            tp = tdfPort.getPeriod();
            rate = tdfPort.getRate();
            if(tp > 0) {
                //validate timestep consistency (rate*tp == tm)
                if(rate*tp != tm){
                    jta.append("Error: In block \""+tdfBlock.getName()+ "\" Timestep Tm is inconsistent with timestep Tp of port \"" + tdfPort.getName()+"\".\n");
                    return false;
                }
            } else {
                tp = tm/rate;
                tdfPort.setPeriod((int)tp);
            }
            for(SysCAMSTConnector connector : connectors){
                //if p1 belongs to portTDF, it implies p2 belongs as well.
                if (connector.get_p1().getComponent() instanceof SysCAMSTPortTDF) {
                    p1_tdfPort = ((SysCAMSTPortTDF) connector.get_p1().getComponent());
                    p2_tdfPort = ((SysCAMSTPortTDF) connector.get_p2().getComponent());
                    if(p1_tdfPort.getName().equals(tdfPort.getName()) && p1_tdfPort.getBlockTDF().getName().equals(tdfBlock.getName())) {
                        if(p2_tdfPort.getPeriod() <= 0) {
                            p2_tdfPort.setPeriod(tp);
                            if(!propagateTpTdf(p2_tdfPort.getBlockTDF(), p2_tdfPort, connectors)) {
                                return false;
                            }
                        } else {
                            //validate timestep consitency between 2 ports.
                            if(p2_tdfPort.getPeriod() != tp) {
                                jta.append("Error: In block \""+tdfBlock.getName()+"\" Timestep Tp of port \"" +tdfPort.getName()
                               + "\" is inconsistent with timestep Tp of port \"" + p2_tdfPort.getName()+"\" from block \""+p2_tdfPort.getBlockTDF().getName()+"\".\n");
                                return false;
                            }
                            //if connected block was not visited yet, then propagate timestep
                            if(!p2_tdfPort.getBlockTDF().getIsTimestepPropagated()) {
                                if(!propagateTpTdf(p2_tdfPort.getBlockTDF(), p2_tdfPort, connectors)) {
                                    return false;
                                }
                            }
                        }
                    } else if(p2_tdfPort.getName().equals(tdfPort.getName()) && p2_tdfPort.getBlockTDF().getName().equals(tdfBlock.getName())) {
                        if(p1_tdfPort.getPeriod() <= 0) {
                            p1_tdfPort.setPeriod(tp);
                            if(!propagateTpTdf(p1_tdfPort.getBlockTDF(), p1_tdfPort, connectors)) {
                                return false;
                            }
                        } else {
                            //validate timestep consitency between 2 ports.
                            if(p1_tdfPort.getPeriod() != tp) {
                               jta.append("Error: In block \""+tdfBlock.getName()+"\" Timestep Tp of port \"" +tdfPort.getName()
                               + "\" is inconsistent with timestep Tp of port \"" + p1_tdfPort.getName()+"\" from block \""+p1_tdfPort.getBlockTDF().getName()+"\".\n");
                               return false;
                            }
                            //if connected block was not visited yet, then propagate timestep
                            if(!p1_tdfPort.getBlockTDF().getIsTimestepPropagated()) {
                                if(!propagateTpTdf(p1_tdfPort.getBlockTDF(), p1_tdfPort, connectors)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
         
        for(SysCAMSTPortConverter converterPort : tdfBlock.getPortConverter()) {
           tp = converterPort.getPeriod();
           rate = converterPort.getRate();
           if(tp > 0) {
               //validate timestep consistency (rate*tp == tm)
               if(rate*tp != tm){
                   jta.append("Error: In block \""+tdfBlock.getName()+ "\" Timestep Tm is inconsistent with timestep Tp of port \"" + converterPort.getName()+"\".\n");
                   return false;
               }
           } else {
               tp = tm/rate;
               converterPort.setPeriod(tp);
           }
        }
        return true;
    }
    
    public boolean propagateTpTdf(SysCAMSTBlockTDF tdfBlock, SysCAMSTPortTDF tdfPort, LinkedList<SysCAMSTConnector> connectors) {
        /* 1. Get period Tp of tdfPort
         * if current Tm > 0, 
         *      validate against Tp of tdfPort.
         * else: set Tm //(setPeriod of tdfBlock)
         * 2. propagateTm(tdfBlock);
         * return
         * 
         * */
        double tm = tdfBlock.getPeriod();
        double tp = tdfPort.getPeriod();
        int rate = tdfPort.getRate();
        if(tm > 0) {
            if(rate*tp != tm) {
                jta.append("Error: In block \""+tdfBlock.getName()+ "\" Timestep Tm is inconsistent with timestep Tp of port \"" + tdfPort.getName()+"\".\n");
                return false;
            } 
        } else {
            tm = rate*tp;
            tdfBlock.setPeriod(tm);
        }
        propagateTm(tdfBlock, connectors);
        return true;
    }
    
    public boolean propagateTpConverter(SysCAMSTBlockTDF tdfBlock, SysCAMSTPortConverter converterPort, LinkedList<SysCAMSTConnector> connectors) {
        double tm = tdfBlock.getPeriod();
        double tp = converterPort.getPeriod();
        int rate = converterPort.getRate();
        if(tm > 0) {
            if(rate*tp != tm) {
                jta.append("Error: In block \""+tdfBlock.getName()+ "\" Timestep Tm is inconsistent with timestep Tp of port \"" + converterPort.getName()+"\".\n");
                return false;
            } 
        } else {
            tm = rate*tp;
            tdfBlock.setPeriod(tm);
        }
        propagateTm(tdfBlock, connectors);
        return true;
    }

    public RealMatrix buildTopologyMatrix(LinkedList<SysCAMSTConnector> connectors, LinkedList<SysCAMSTBlockTDF> blocks, RealVector buffer) {
        double [][] tArray = new double[connectors.size()][blocks.size()];
        for(int i = 0; i < connectors.size(); i++) {
            for(int j = 0; j < blocks.size(); j++) {
                if( ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getBlockTDF().getName().equals(blocks.get(j).getName()) ) {
                    System.out.println("Inserting in : "+ i + " " + j + " From port: "+ ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getName() +" Rate: " + ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getRate() );
                    tArray[i][j] = ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getRate();
                    if(((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getDelay() > 0) {
                        buffer.addToEntry(i, ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getDelay() ); 
                    }
                } else if( ((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getBlockTDF().getName().equals(blocks.get(j).getName()) ) {
                    System.out.println("Inserting in : "+ i + " " + j + " From port: "+ ((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getName() +" Rate: " + -((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getRate() );
                    tArray[i][j] = -((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getRate();
                    if(((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getDelay() > 0) {
                        buffer.addToEntry(i, ((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getDelay() );
                    }
                }
            }
        }
        
        //double[][] tArray = { { 2, -1, 0 }, { 0, 2, -4 }, { -1, 0, 1 } };
        //double[][] tArray = { { 2, 3, 5 }, { -4, 2, 3} };
        //double[][] tArray = { { 3, -2, 0, 0 }, { 0, 4, 0, -3 }, { 0, 1, -3, 0 }, { -1, 0, 2, 0 }, { -2, 0, 0, 1 } };
        //double[][] tArray = { { 3, -2 } };
        RealMatrix tMatrix = new Array2DRowRealMatrix(tArray);
        return tMatrix;
    }

    public RealVector solveTopologyMatrix(RealMatrix matrixA, LinkedList<SysCAMSTBlockTDF> blocks) throws InterruptedException {
        double dropThreshold = 1e-7;
        //Using QR decomposition, A^TP=QR. Since rank MUST be rank = s-1 
        //(where s is number of blocks), then the last column of Q provides a basis for the kernel of A.
        RRQRDecomposition qr = new RRQRDecomposition(matrixA.transpose());
        RealMatrix qMatrix = qr.getQ();
        int rank = qr.getRank(dropThreshold);
        if(rank != blocks.size()-1){
            jta.append("Error: Port sample rates are inconsistent. Topology matrix can not be solved.\n");
            System.err.println("Port sample rates are inconsistent. Topology matrix can not be solved. Rank: " +rank+" != #blocks-1");
            throw new InterruptedException(); 
        }
        System.out.println("Checking kernel columns ...");
        RealMatrix zMatrix = matrixA.multiply(qMatrix);
        for (int c = rank; c < matrixA.getColumnDimension(); c++) {
            System.out.printf("The product of A with column %d of Q has sup "
                            + "norm %f.\n",
                            c, zMatrix.getColumnMatrix(c).getNorm());
            //TODO: verify if norm is not zero, throw error that kernel could not be found.              
        }
        
        RealMatrix kernelMatrix = qMatrix.getSubMatrix( 0, qMatrix.getRowDimension()-1, rank, qMatrix.getColumnDimension()-1 );
        double[] resultArray = new double[kernelMatrix.getRowDimension()];
        double result_tmp = 0.0;
        int v_lcm = 1;
        BigFraction[] resultFractionArray = new BigFraction[kernelMatrix.getRowDimension()];
        for (int i = 0; i < kernelMatrix.getRowDimension(); i++) {
            System.out.printf("The kernelMatrix is %f .\n", kernelMatrix.getEntry(i, 0) );
            resultArray[i] = kernelMatrix.getEntry(i, 0) / kernelMatrix.getEntry(kernelMatrix.getRowDimension()-1, 0);
            result_tmp = kernelMatrix.getEntry(i, 0) / kernelMatrix.getEntry(kernelMatrix.getRowDimension()-1, 0);
            resultFractionArray[i] = new BigFraction(result_tmp, 2147483647);
            System.out.println("The resultArray is: "+ resultArray[i] + ", result_tmp: " + result_tmp);
            System.out.println("The resultFractionArray is: "+ resultFractionArray[i].toString() + " with num: " + resultFractionArray[i].getNumeratorAsInt() + " and denom: "+ resultFractionArray[i].getDenominatorAsInt()
            + " and given as a double: " + resultFractionArray[i].doubleValue());
            v_lcm = ArithmeticUtils.lcm(resultFractionArray[i].getDenominatorAsInt() , v_lcm);
            System.out.println("The lcm is: "+ v_lcm );
        }
        int[] tmpResult = new int[kernelMatrix.getRowDimension()];
        double[] finalResult = new double[kernelMatrix.getRowDimension()];
        for (int i = 0; i < kernelMatrix.getRowDimension(); i++) {
            tmpResult[i] = (resultFractionArray[i].multiply(v_lcm)).intValue();
            finalResult[i] = (double)tmpResult[i];
            System.out.println("The finalResult is: "+ finalResult[i] + " - " + blocks.get(i).getName() );
        }
            RealVector xVector = new ArrayRealVector(finalResult);
            return xVector;
    }
    
    public boolean computeSchedule(RealVector q, RealMatrix gamma, RealVector buffer, LinkedList<SysCAMSTBlockTDF> tdfBlocks, LinkedList<SysCAMSTConnector> connectors) throws InterruptedException {
        RealVector q1 = new ArrayRealVector(q.getDimension());
        RealVector nu = new ArrayRealVector(q.getDimension());
        RealVector tmpBuffer = new ArrayRealVector(gamma.getRowDimension());;
        RealVectorFormat printFormat = new RealVectorFormat();
        boolean deadlock = false;
        boolean recompute = false;
        SysCAMSTBlockTDF tdfBlock;
        LinkedList<SysCAMSTPortConverter> portConvertersTmp;
        double[] time_prev = {0.0, 0.0}; //array to store "in" ([0]) and "out" ([1]) previous times
        //Reset the number of times all blocks have been executed
        for(int i = 0; i < tdfBlocks.size(); i++) {
            tdfBlocks.get(i).setN(0);
        }
        
        try {
            while (!(q1.equals(q)) && !deadlock){
                deadlock = true;
                for(int i = 0; i < tdfBlocks.size(); i++) {
                    tdfBlock = tdfBlocks.get(i);
                    //check if block is runnable: If it has not run q times 
                    //and it won't cause a buffer size to go negative.
                    System.out.println("q1 is: " + printFormat.format(q1) );
                    System.out.println("q is: " + printFormat.format(q) );
                    if(q1.getEntry(i) != q.getEntry(i)) {
                        nu.setEntry(i, 1);
                        tmpBuffer = buffer.add(gamma.operate(nu));
                        System.out.println("tmpBuffer is: " + printFormat.format(tmpBuffer) );
                        if(tmpBuffer.getMinValue() >= 0) {
                            deadlock = false;
                            q1 = q1.add(nu);
                            buffer = tmpBuffer.copy();
                            System.out.println("Schedule " + tdfBlock.getName() );
                            System.out.println("Buffer is: " + printFormat.format(buffer) );
                            //Validate sync bewtween TDF/DE 
                            tdfBlock.syncTDFBlockDEBlock(time_prev);
                        }
                        nu.setEntry(i, 0);
                    }
                }
            }
        } catch (SysCAMSValidateException se) {
            recompute = true;
            System.out.println("Causality exception: " + se.getMessage());
        }
        if (deadlock){
            System.out.println("Static schedule can not be computed due to missing delays in loops" );
            jta.append("Error: Static schedule can not be computed due to missing delays in loops\n" );
            int minIndex = tmpBuffer.getMinIndex();
            //TODO: for the suggested delay, I need to first detect loops within the graph(DFS?), then recompute recursively with the suggested delay until it can be solved.
            /*jta.append("Following delay is suggested:\n" );
            int currentDelay = ((SysCAMSTPortTDF) connectors.get(minIndex).get_p2().getComponent()).getDelay();
            jta.append(currentDelay-(int)tmpBuffer.getMinValue() +" in port \""
            +((SysCAMSTPortTDF) connectors.get(minIndex).get_p2().getComponent()).getName()
            +"\" from block \""+ ((SysCAMSTPortTDF) connectors.get(minIndex).get_p2().getComponent()).getBlockTDF().getName()+"\"\n");
            */
            throw new InterruptedException(); 
        } else {
            System.out.println("Schedule complete-STOP" );
        }
        return recompute;
    }
    
//    public void showSimulationTrace() {
//        JFrameSimulationSDPanel jfssdp = new JFrameSimulationSDPanel(f, mgui, "Simulation trace of " + simulationTraceFile.getText());
//        jfssdp.setIconImage(IconManager.img8);
//        GraphicLib.centerOnParent(jfssdp, 600, 600);
//        if (selectedViewTrace == 0) {
//            jfssdp.setFileReference(simulationTraceFile.getText());
//        } else {
//            jfssdp.setFileReference(simulationsoclibTraceFile.getText());
//        }
//        jfssdp.setVisible(true);
//        TraceManager.addDev("Ok JFrame");
//    }

//    public void showOverflowStatus() {
//        try {
//            String path = ConfigurationTTool.AVATARMPSoCPerformanceEvaluationDirectory;
//            String taille = "0";
//
//            String log = "mwmr0.log";
//
//            String[] commande = {"sh", path + "callingOverflow.sh", taille, path, log};
//
//            ProcessBuilder pb = new ProcessBuilder(commande);
//            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//            Process p = pb.start();
//
//            int exitStatus = p.waitFor();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
