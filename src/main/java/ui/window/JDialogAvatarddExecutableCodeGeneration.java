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

import avatartranslator.AvatarSpecification;
import ddtranslatorSoclib.AvatarddSpecification;
import ddtranslatorSoclib.toSoclib.TasksAndMainGenerator;
import ddtranslatorSoclib.toTopCell.TopCellGenerator;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.*;
import ui.AvatarDeploymentPanelTranslator;
import common.ConfigurationTTool;
import ui.util.IconManager;
import ui.MainGUI;
import ui.avatardd.ADDDiagramPanel;
import ui.interactivesimulation.JFrameSimulationSDPanel;
import ui.syscams.SysCAMSComponentTaskDiagramPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Vector;

/**
 * Class JDialogAvatarddExecutableCodeGeneration Dialog for managing the
 * generation and compilation of AVATAR executable code Creation: june 2014
 *
 * @author Ludovic APVRILLE
 * @author (adapted to code generation from deployment diagrams) Julien Henon,
 *         Daniela GENIUS 2015-2016
 * @version 2.0 (march 2016)
 */
public class JDialogAvatarddExecutableCodeGeneration extends javax.swing.JFrame
        implements ActionListener, Runnable, MasterProcessInterface {

    private static String[] unitTab = { "usec", "msec", "sec" };

    protected Frame f;
    protected MainGUI mgui;

    private String textSysC1 = "Base directory of code generation:";
    private String textSysC2 = "Compile soclib executable with";
    // private String textSysC3 = "Run code and trace traffic on interconnect :";
    private String textSysC4 = "Run code in soclib / mutekh:";
    private String textSysC5 = "Show AVATAR trace from file w/o hardware:";
    private String textSysC6 = "Show cycle accurate trace from MPSoC file:";
    private String textSysC7 = "Base directory of topcell generation:";

    // private static String unitCycle = "1";

    private static String[] codes = { "AVATAR SOCLIB" };

    private static int selectedRun = 1;
    private static int selectedCompile = 0;
    private static int selectedViewTrace = 0;
    private static boolean static_putUserCode = true;

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

    // components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;

    protected JRadioButton exe, exeint, exetrace, exesoclib, compile, compilesoclib, viewtrace, viewtracesoclib;
    protected ButtonGroup compilegroup, exegroup, viewgroup;
    protected JLabel gen;
    protected JTextField code1, code2, compiler, exe1, exe2, exe3, exe4, exe2int, simulationTraceFile,
            simulationsoclibTraceFile;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox removeCFiles, removeXFiles, debugmode, tracemode, optimizemode, putUserCode;
    protected JComboBox<String> versionCodeGenerator, units;
    protected JButton showSimulationTrace, showOverflowStatus;

    private static int selectedUnit = 2;
    private static boolean removeCFilesValue = true;
    private static boolean removeXFilesValue = true;
    private static boolean debugValue = false;
    private static boolean tracingValue = false;
    // private static boolean optimizeValue = true;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    protected boolean startProcess = false;

    // private AvatarRelation FIFO;

    private String hostExecute;

    protected RshClient rshc;

    /*
     * Creates new form
     */

    public JDialogAvatarddExecutableCodeGeneration(Frame _f, MainGUI _mgui, String title, String _hostExecute,
            String _pathCode, String _pathCompileMPSoC, String _pathExecuteMPSoC) {

        super(title);
        ;
        f = _f;
        mgui = _mgui;

        if (pathCode == null) {
            pathCode = _pathCode;
        }

        if (pathCompileMPSoC == null) {
            pathCompileMPSoC = _pathCompileMPSoC;
        }

        if (pathExecuteMPSoC == null) {
            pathExecuteMPSoC = _pathExecuteMPSoC;
        }

        hostExecute = _hostExecute;

        initComponents();
        myInitComponents();
        pack();

        // getGlassPane().addMouseListener( new MouseAdapter() {});
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

        // Issue #41 Ordering of tabbed panes
        jp1 = GraphicLib.createTabbedPane();// new JTabbedPane();

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
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        gen = new JLabel(textSysC1);
        // genJava.addActionListener(this);
        jp01.add(gen, c01);

        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);

        gen = new JLabel(textSysC7);
        // genJava.addActionListener(this);
        jp01.add(gen, c01);

        code2 = new JTextField(pathCode, 100);
        jp01.add(code2, c01);

        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row

        removeCFiles = new JCheckBox("Remove .c / .h files");
        removeCFiles.setSelected(removeCFilesValue);
        jp01.add(removeCFiles, c01);

        removeXFiles = new JCheckBox("Remove .x files");
        removeXFiles.setSelected(removeXFilesValue);
        jp01.add(removeXFiles, c01);

        debugmode = new JCheckBox("Put debug information in generated code");
        debugmode.setSelected(debugValue);
        jp01.add(debugmode, c01);

        tracemode = new JCheckBox("Put tracing capabilities in generated code");
        tracemode.setSelected(tracingValue);
        jp01.add(tracemode, c01);

        optimizemode = new JCheckBox("Optimize code");
        optimizemode.setSelected(optimizeModeSelected);
        jp01.add(optimizemode, c01);

        putUserCode = new JCheckBox("Include user code");
        putUserCode.setSelected(static_putUserCode);
        jp01.add(putUserCode, c01);

        jp01.add(new JLabel("1 time unit ="), c01);

        units = new JComboBox<String>(unitTab);
        units.setSelectedIndex(selectedUnit);
        units.addActionListener(this);
        jp01.add(units, c01);

        jp01.add(new JLabel("Code generator used:"), c01);

        versionCodeGenerator = new JComboBox<String>(codes);
        // versionCodeGenerator.setSelectedIndex(selectedItem);
        versionCodeGenerator.addActionListener(this);
        jp01.add(versionCodeGenerator, c01);

        jp01.add(new JLabel(" "), c01);
        jp1.add("Generate code", jp01);

        // Panel 02 -> compile
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = GridBagConstraints.REMAINDER; // end row
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;

        compilegroup = new ButtonGroup();

        compilesoclib = new JRadioButton(textSysC2, false);
        compilesoclib.addActionListener(this);
        jp02.add(compilesoclib, c02);
        compilegroup.add(compilesoclib);
        compiler = new JTextField(pathCompileMPSoC, 100);
        jp02.add(compiler, c02);

        // compile.setSelected(selectedCompile == 0);
        compilesoclib.setSelected(selectedCompile == 1);

        jp1.add("Compile", jp02);

        // Panel 03 -> Execute
        c03.gridheight = 1;
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; // end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;

        exegroup = new ButtonGroup();

        /*
         * exetrace = new JRadioButton(textSysC3, false);
         * exetrace.addActionListener(this); exegroup.add(exetrace); jp03.add(exetrace,
         * c03); exe3 = new JTextField(pathExecuteMPSoC+"-trace", 100); jp03.add(exe3,
         * c03);
         */

        exesoclib = new JRadioButton(textSysC4, false);
        exesoclib.addActionListener(this);
        exegroup.add(exesoclib);
        jp03.add(exesoclib, c03);
        exe4 = new JTextField(pathExecuteMPSoC, 100);
        jp03.add(exe4, c03);

        // exe.setSelected(selectedRun == 0);
        // exetrace.setSelected(selectedRun == 1);
        exesoclib.setSelected(selectedRun == 2);

        jp03.add(new JLabel(" "), c03);

        jp1.add("Execute", jp03);

        // Panel 04 -> View trace
        c04.gridheight = 1;
        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridwidth = GridBagConstraints.REMAINDER; // end row
        c04.fill = GridBagConstraints.HORIZONTAL;
        c04.gridheight = 1;

        viewgroup = new ButtonGroup();
        viewtrace = new JRadioButton(textSysC5, false);
        viewgroup.add(viewtrace);
        viewtrace.addActionListener(this);
        jp04.add(viewtrace, c04);
        simulationTraceFile = new JTextField(pathCode + "trace.txt", 100);
        jp04.add(simulationTraceFile, c04);
        viewtracesoclib = new JRadioButton(textSysC6, false);
        viewgroup.add(viewtracesoclib);
        viewtracesoclib.addActionListener(this);
        jp04.add(viewtracesoclib, c04);
        simulationsoclibTraceFile = new JTextField(pathSoclibTraceFile, 100);
        jp04.add(simulationsoclibTraceFile, c04);

        showSimulationTrace = new JButton("Show simulation trace");
        showSimulationTrace.addActionListener(this);
        jp04.add(showSimulationTrace, c04);

        // -------------Ajout C.Demarigny---------------

        showOverflowStatus = new JButton("Show overflow status");
        showOverflowStatus.addActionListener(this);
        jp04.add(showOverflowStatus, c04);

        // ----------------Fin ajout--------------------

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
        } else if (evt.getSource() == units) {
            selectedUnit = units.getSelectedIndex();
        } else if (evt.getSource() == showSimulationTrace) {
            showSimulationTrace();
        } // else if ((evt.getSource() == exe) || (evt.getSource() == exetrace)||
          // (evt.getSource() == exesoclib)) {
        else if ((evt.getSource() == exetrace) || (evt.getSource() == exesoclib)) {
            makeSelectionExecute();
            // } else if ((evt.getSource() == compile) || (evt.getSource() ==
            // compilesoclib)) {
        } else if ((evt.getSource() == compilesoclib)) {
            makeSelectionCompile();
        } else if ((evt.getSource() == viewtrace) || (evt.getSource() == viewtracesoclib)) {
            makeSelectionViewTrace();
        } else if ((evt.getSource() == showOverflowStatus)) { // ajout CD
            showOverflowStatus();
        } // fin ajout CD
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        optimizeModeSelected = optimizemode.isSelected();
        removeCFilesValue = removeCFiles.isSelected();
        removeXFilesValue = removeXFiles.isSelected();
        debugValue = debugmode.isSelected();
        tracingValue = tracemode.isSelected();
        static_putUserCode = putUserCode.isSelected();
        dispose();
    }

    public void makeSelectionExecute() {

        // if (exetrace.isSelected()) {
        // selectedRun = 1;
        // } else {
        selectedRun = 2;
        // }

        // exe2.setEnabled(selectedRun == 0);
        // exe3.setEnabled(selectedRun == 1);
        exe4.setEnabled(selectedRun == 2);
    }

    public void makeSelectionCompile() {

        selectedCompile = 1;
        compiler.setEnabled(selectedCompile == 1);

    }

    public void makeSelectionViewTrace() {
        if (viewtrace.isSelected()) {
            selectedViewTrace = 0;
        } else {
            selectedViewTrace = 1;
        }
        simulationTraceFile.setEnabled(selectedViewTrace == 0);
        // simulationsoclibTraceFile.setEnabled(selectedViewTrace == 1);
    }

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
        String cmd;
        String list;// , data;
        // int cycle = 0;

        hasError = false;

        try {

            if (jp1.getSelectedIndex() == 0) {
                jta.append("Generating executable code (SOCLIB version)\n");
                // selectedUnit = 1;

                ADDDiagramPanel deploymentDiagramPanel = mgui.getFirstAvatarDeploymentPanelFound();
                AvatarDeploymentPanelTranslator avdeploymenttranslator = new AvatarDeploymentPanelTranslator(
                        deploymentDiagramPanel);

                AvatarddSpecification avddspec = avdeploymenttranslator.getAvatarddSpecification();
                AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();

                Vector<SysCAMSComponentTaskDiagramPanel> listsyscamspanel = mgui.getListSysCAMSPanel();

                // SysCAMSPanelTranslator syscamspaneltranslator = new
                // SysCAMSPanelTranslator(syscamspanel);
                // SysCAMSSpecification syscamsspec =
                // syscamspaneltranslator.getSysCAMSSpecification();

                // Generating code
                if (avddspec == null) {
                    jta.append("Error: No AVATAR Deployemnt specification\n");
                } else {
                    TraceManager.addDev("**AVATAR TOPCELL found");

                    TopCellGenerator topCellGenerator = new TopCellGenerator(avddspec, tracemode.isSelected(), avspec);
                    testGo();
                    jta.append("Generation of TopCell executable code: done\n");

                    try {
                        jta.append("Saving  MPSoC code in files\n");
                        TraceManager.addDev("Saving MPSoC code in files\n");
                        pathCode = code2.getText();

                        TraceManager.addDev("AVATAR TOPCELL saved in " + code2.getText());
                        // if (!listsyscamspanel.isEmpty())
                        if (listsyscamspanel == null)
                            topCellGenerator.saveFile(pathCode);
                        // topCellGenerator.saveFile(pathCode,listsyscamspanel); // DG 13.12.2019
                        else // topCellGenerator.saveFile(pathCode);
                            topCellGenerator.saveFile(pathCode, listsyscamspanel); // DG 13.12.2019
                        jta.append("Code saved\n");
                    } catch (Exception e) {
                        jta.append("Could not generate files\n");
                        TraceManager.addDev("Could not generate MPSoC files\n");
                        e.printStackTrace();
                    }
                }

                testGo();

                if (removeCFiles.isSelected()) {

                    jta.append("Removing all .h files\n");

                    list = FileUtils.deleteFiles(code1.getText() + TasksAndMainGenerator.getGeneratedPath(), ".h");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                    jta.append("Removing all  .c files\n");
                    list = FileUtils.deleteFiles(code1.getText() + TasksAndMainGenerator.getGeneratedPath(), ".c");
                    // list = FileUtils.deleteFiles(code1.getText() +
                    // AVATAR2SOCLIB.getGeneratedPath(), ".c");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                }

                if (removeXFiles.isSelected()) {
                    jta.append("Removing all .x files\n");
                    list = FileUtils.deleteFiles(code1.getText(), ".x");
                    if (list.length() == 0) {
                        jta.append("No files were deleted\n");
                    } else {
                        jta.append("Files deleted:\n" + list + "\n");
                    }
                }

                testGo();

                selectedUnit = units.getSelectedIndex();
                //
                // AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();

                // Generating code
                if (avspec == null) {
                    jta.append("Error: No AVATAR specification\n");
                } else {

                    TasksAndMainGenerator gene = new TasksAndMainGenerator(avddspec, avspec);
                    gene.includeUserCode(putUserCode.isSelected());
                    gene.setTimeUnit(selectedUnit);
                    gene.generateSoclib(debugmode.isSelected(), tracemode.isSelected());

                    if (avddspec == null) {
                        jta.append("Error: No AVATAR Deployment specification\n");
                    } else {
                        TraceManager.addDev("AVATAR TOPCELL found");
                    }

                    TopCellGenerator topCellGenerator = new TopCellGenerator(avddspec, tracemode.isSelected(), avspec);
                    testGo();
                    jta.append("Generation of TopCell executable code: done\n");

                    try {
                        jta.append("Saving code in files\n");
                        pathCode = code2.getText();
                        topCellGenerator.saveFile(pathCode, listsyscamspanel);

                        jta.append("Code saved\n");
                    } catch (Exception e) {
                        jta.append("Could not generate files\n");
                    }

                    testGo();
                    jta.append("Generation of C-SOCLIB executable code: done\n");
                    // t2j.printJavaClasses();
                    try {
                        jta.append("Saving code in files\n");
                        pathCode = code1.getText();
                        gene.saveInFiles(pathCode);

                        jta.append("Code saved\n");
                    } catch (Exception e) {
                        jta.append("Could not generate files\n");
                    }
                }
            }

            testGo();

            // Compilation
            if (jp1.getSelectedIndex() == 1) {
                cmd = compiler.getText();
                jta.append("Compiling executable code with command: \n" + cmd + "\n");

                rshc = new RshClient(hostExecute);
                // AssumE data are on the remote host
                // Command
                try {
                    processCmd(cmd, jta);
                    // data = processCmd(cmd);
                    // jta.append(data);
                    jta.append("Compilation done\n");
                } catch (LauncherException le) {
                    jta.append("Error: " + le.getMessage() + "\n");
                    mode = STOPPED;
                    setButtons();
                    return;
                } catch (Exception e) {
                    mode = STOPPED;
                    setButtons();
                    return;
                }
            }

            if (jp1.getSelectedIndex() == 2) {
                try {
                    // if (selectedRun == 1) {
                    // cmd = exe3.getText();
                    // } else {
                    cmd = exe4.getText();
                    // }

                    jta.append("Executing code with command: \n" + cmd + "\n");

                    rshc = new RshClient(hostExecute);
                    // Assume data are on the remote host
                    // Command

                    processCmd(cmd, jta);
                    // jta.append(data);
                    jta.append("Execution done\n");
                } catch (LauncherException le) {
                    jta.append("Error: " + le.getMessage() + "\n");
                    mode = STOPPED;
                    setButtons();
                    return;
                } catch (Exception e) {
                    mode = STOPPED;
                    setButtons();
                    return;
                }
            }

            if ((hasError == false) && (jp1.getSelectedIndex() < 2)) {
                jp1.setSelectedIndex(jp1.getSelectedIndex() + 1);
            }
            // }

            // fin ajout DG

        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }

        jta.append("\n\nReady to process next command\n");

        checkMode();
        setButtons();

        //
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
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                // setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
        JFrameSimulationSDPanel jfssdp = new JFrameSimulationSDPanel(f, mgui,
                "Simulation trace of " + simulationTraceFile.getText());
        jfssdp.setIconImage(IconManager.img8);
        // jfssdp.setSize(600, 600);
        GraphicLib.centerOnParent(jfssdp, 600, 600);
        if (selectedViewTrace == 0) {
            jfssdp.setFileReference(simulationTraceFile.getText());
        } else {
            jfssdp.setFileReference(simulationsoclibTraceFile.getText());
        }
        jfssdp.setVisible(true);
        TraceManager.addDev("Ok JFrame");
    }

    // ----------Ajout CD------------
    public void showOverflowStatus() {
        try {
            // String chemin =
            // "~/TTool/MPSoC/soclib/soclib/platform/topcells/caba-vgmn-mutekh_kernel_tutorial/";
            // //ajouter le chemin relatif
            String path = ConfigurationTTool.AVATARMPSoCPerformanceEvaluationDirectory;
            // tentative d'instanciation de AvatarRelation afin d'utiliser getSizeOfFIFO()
            // AvatarRelation ar = new AvatarRelation();

            // int fifo = ar.getSizeOfFIFO(); //ajouter dynamiquement la taille du FIFO
            // String taille = ""+fifo;
            String taille = "0";

            String log = "mwmr0.log"; // ajouter dynamiquement le nom du log généré

            String[] commande = { "sh", path + "callingOverflow.sh", taille, path, log };

            /*
             * idealement il faudrait inclure un moyen de detecter l'OS sur lequel
             * l'application est lancé car le script utilise la commande "acroread" qui ne
             * fonctionne que sur linux. Ainsi ajouter un paramètre avec l'OS permetterais
             * de générer la commande appropriée sur windows ou mac
             */
            // Use System.getProperty( "os.name" );
            ProcessBuilder pb = new ProcessBuilder(commande);// Letitia Runtime.runtimexec()
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();

            // FIXME: Should the return code be tested?
            int exitStatus = p.waitFor();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // -------------fin ajout CD-----------

}
