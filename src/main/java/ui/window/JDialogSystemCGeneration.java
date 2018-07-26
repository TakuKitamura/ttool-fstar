/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.f
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

import common.SpecConfigTTool;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.*;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.AvatarRequirementPanelTranslator;
import ui.JTextAreaWriter;
import ui.MainGUI;
import ui.avatarpd.AvatarPDPanel;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class JDialogSystemCGeneration
 * Dialog for managing the generation and compilation of SystemC code
 * Creation: 01/12/2005
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.2 02/06/2014
 */
public class JDialogSystemCGeneration extends JDialog implements ActionListener, Runnable, MasterProcessInterface, ListSelectionListener {

    protected MainGUI mgui;

    private static String textSysC1 = "Generate C++ simulator code in";
    private static String textSysC2 = "Compile C++ simulator  code in";
    private static String textSysC4 = "Run simulation to completion:";
    private static String textSysC5 = "Run interactive simulation:";
    private static String textSysC6 = "Run formal verification:";

    private static float unitCycleValue = 1;

    private static String[] simus = {"Diplodocus C++ Simulator - LabSoc version"};

    private static int selectedItem = 0;

    protected static String pathCode;
    protected static String pathCompiler;
    protected static String pathExecute;
    protected static String pathInteractiveExecute;
    protected static String pathFormalExecute;

    protected static boolean interactiveSimulationSelected = true;
    protected static boolean optimizeModeSelected = true;
    protected static boolean activatePenaltiesSelected = true;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    protected final static int ERROR = 4;
    int mode;

    //components
    protected JTextArea jta;
    private JTextAreaWriter textAreaWriter;
    protected JButton start;
    protected JButton stop;
    protected JButton close;

    protected JRadioButton exe, exeint, exeformal;
    protected ButtonGroup exegroup;
    protected JLabel gen, comp;
    protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int, exe2formal;
    protected JTabbedPane tabbedPane;
    protected JScrollPane jsp;
    protected JCheckBox removeCppFiles, removeXFiles, debugmode, optimizemode, activatePenalties;
    protected JComboBox<String> versionSimulator;

    //TEPE Diagram
    private static Vector<AvatarPDPanel> validatedTepe, ignoredTepe;
    private Vector<AvatarPDPanel> valTepe, ignTepe;
    private JList<AvatarPDPanel> listIgnoredTepe;
    private JList<AvatarPDPanel> listValidatedTepe;
    private JButton allValidatedTepe;
    private JButton addOneValidatedTepe;
    private JButton addOneIgnoredTepe;
    private JButton allIgnoredTepe;
    private JPanel panele1Tepe, panele2Tepe, panele3Tepe, panele4Tepe;//, panel5Tepe, panel6Tepe;

    private Thread t;
    private boolean go = false;
    //    private boolean hasError = false;
    private int errorTabIndex = -1;
    protected boolean startProcess = false;
    private boolean mustRecompileAll;

    private String simulatorHost;

    protected RshClient rshc;

    // Automatic modes
    public final static int MANUAL = 0;
    public final static int ONE_TRACE = 1;
    public final static int ANIMATION = 2;
    public final static int FORMAL_VERIFICATION = 3;

    private int automatic;
    //  private boolean wasClosed = false;


    /**
     * Creates new form
     */
    public JDialogSystemCGeneration(Frame f, MainGUI _mgui, String title, String _simulatorHost, String _pathCode,
                                    String _pathCompiler, String _pathExecute, String _pathInteractiveExecute,
                                    String _graphPath, int _automatic) {
        super(f, title, true);

        mgui = _mgui;

        // Must first create all directories
        new File(_pathCode).mkdirs();
        new File(_graphPath).mkdirs();


        pathCode = _pathCode;

        pathCompiler = _pathCompiler;
        if (!(pathCompiler.contains("-C "))) {
            pathCompiler = pathCompiler + " -C " + pathCode;
        }

        pathExecute = _pathExecute;

        if (_graphPath != null) {
            _pathInteractiveExecute += " -gpath " + _graphPath;
        }
        pathInteractiveExecute = _pathInteractiveExecute;


        pathFormalExecute = pathInteractiveExecute;


        int index = pathFormalExecute.indexOf("-server");
        if (index != -1) {
            pathFormalExecute = pathFormalExecute.substring(0, index) +
                    pathFormalExecute.substring(index + 7, pathFormalExecute.length());
            pathFormalExecute += " -gname graph -explo";
        }


        simulatorHost = _simulatorHost;

        automatic = _automatic;

        makeLists();

        initComponents();
        myInitComponents();
        pack();

        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (automatic > 0) {
            startProcess();
        }
    }

    protected void makeLists() {
        if (validatedTepe == null) {
            validatedTepe = new Vector<AvatarPDPanel>();
        }

        if (ignoredTepe == null) {
            ignoredTepe = new Vector<AvatarPDPanel>();
        }

        valTepe = new Vector<AvatarPDPanel>();
        ignTepe = new Vector<AvatarPDPanel>();

        final List<AvatarPDPanel> al = mgui.getAllAvatarPDPanels();

        for (AvatarPDPanel panel : al) {
            if (validatedTepe.contains(panel)) {
                valTepe.add(panel);
            } else {
                ignTepe.add(panel);
            }
        }
    }


    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
        setList();
        updateInteractiveSimulation();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        // Issue #41 Ordering of tabbed panes
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

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
        jp01.add(gen, c01);

        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);

        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row

        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp01.add(new JLabel("1 time unit = "), c01);

        unitcycle = new JTextField(Float.toString(unitCycleValue), 10);
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

        optimizemode = new JCheckBox("Optimize code");
        optimizemode.setSelected(optimizeModeSelected);
        jp01.add(optimizemode, c01);

        activatePenalties = new JCheckBox("Activate penalties");
        activatePenalties.setSelected(activatePenaltiesSelected);
        jp01.add(activatePenalties, c01);

        jp01.add(new JLabel("Simulator used:"), c01);

        versionSimulator = new JComboBox<String>(simus);
        versionSimulator.setSelectedIndex(selectedItem);
        versionSimulator.addActionListener(this);
        jp01.add(versionSimulator, c01);

        //devmode = new JCheckBox("Development version of the simulator");
        //devmode.setSelected(true);
        //jp01.add(devmode, c01);

        jp01.add(new JLabel(" "), c01);

        //EBRDDs
        panele1Tepe = new JPanel();
        panele1Tepe.setLayout(new BorderLayout());
        panele1Tepe.setBorder(new javax.swing.border.TitledBorder("Ignored TEPE Diagrams"));
        listIgnoredTepe = new JList<AvatarPDPanel>(ignTepe);
        //listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnoredTepe.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listIgnoredTepe.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnoredTepe);
        panele1Tepe.add(scrollPane1, BorderLayout.CENTER);
        panele1Tepe.setPreferredSize(new Dimension(200, 250));

        // validated list
        panele2Tepe = new JPanel();
        panele2Tepe.setLayout(new BorderLayout());
        panele2Tepe.setBorder(new javax.swing.border.TitledBorder("TEPE Diagrams taken into account"));
        listValidatedTepe = new JList<AvatarPDPanel>(valTepe);
        //listValidated.setPreferredSize(new Dimension(200, 250));
        listValidatedTepe.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listValidatedTepe.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listValidatedTepe);
        panele2Tepe.add(scrollPane2, BorderLayout.CENTER);
        panele2Tepe.setPreferredSize(new Dimension(200, 250));


        // central buttons
        panele3Tepe = new JPanel();
        GridBagLayout gridbage1 = new GridBagLayout();
        GridBagConstraints ce1 = new GridBagConstraints();
        panele3Tepe.setLayout(gridbage1);

        ce1.weighty = 1.0;
        ce1.weightx = 1.0;
        ce1.gridwidth = GridBagConstraints.REMAINDER; //end row
        ce1.fill = GridBagConstraints.HORIZONTAL;
        ce1.gridheight = 1;

        allValidatedTepe = new JButton(IconManager.imgic50);
        allValidatedTepe.setPreferredSize(new Dimension(50, 25));
        allValidatedTepe.addActionListener(this);
        allValidatedTepe.setActionCommand("allValidatedTepe");
        panele3Tepe.add(allValidatedTepe, ce1);

        addOneValidatedTepe = new JButton(IconManager.imgic48);
        addOneValidatedTepe.setPreferredSize(new Dimension(50, 25));
        addOneValidatedTepe.addActionListener(this);
        addOneValidatedTepe.setActionCommand("addOneValidatedTepe");
        panele3Tepe.add(addOneValidatedTepe, ce1);

        panele3Tepe.add(new JLabel(" "), ce1);

        addOneIgnoredTepe = new JButton(IconManager.imgic46);
        addOneIgnoredTepe.addActionListener(this);
        addOneIgnoredTepe.setPreferredSize(new Dimension(50, 25));
        addOneIgnoredTepe.setActionCommand("addOneIgnoredTepe");
        panele3Tepe.add(addOneIgnoredTepe, ce1);

        allIgnoredTepe = new JButton(IconManager.imgic44);
        allIgnoredTepe.addActionListener(this);
        allIgnoredTepe.setPreferredSize(new Dimension(50, 25));
        allIgnoredTepe.setActionCommand("allIgnoredTepe");
        panele3Tepe.add(allIgnoredTepe, ce1);


        panele4Tepe = new JPanel();
        panele4Tepe.setLayout(new BorderLayout());
        panele4Tepe.add(panele1Tepe, BorderLayout.WEST);
        panele4Tepe.add(panele2Tepe, BorderLayout.EAST);
        panele4Tepe.add(panele3Tepe, BorderLayout.CENTER);

        jp01.add(panele4Tepe, c01);
        if (automatic > 0) {
            //GraphicLib.enableComponents(jp01, false);
        }

        tabbedPane.add("Generate code", jp01);

        // Panel 02
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;

        comp = new JLabel(textSysC2);
        jp02.add(comp, c02);

        code2 = new JTextField(pathCode, 100);
        jp02.add(code2, c02);

        jp02.add(new JLabel("with"), c02);

        compiler1 = new JTextField(pathCompiler, 100);
        jp02.add(compiler1, c02);

        jp02.add(new JLabel(" "), c02);

        tabbedPane.add("Compile", jp02);

        // Panel 03
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
        jp03.add(exe2, c02);


        exeint = new JRadioButton(textSysC5, true);
        exeint.addActionListener(this);
        exegroup.add(exeint);
        jp03.add(exeint, c03);
        exe2int = new JTextField(pathInteractiveExecute, 100);
        jp03.add(exe2int, c02);


        exeformal = new JRadioButton(textSysC6, true);
        exeformal.addActionListener(this);
        exegroup.add(exeformal);
        jp03.add(exeformal, c03);
        exe2formal = new JTextField(pathFormalExecute, 100);
        jp03.add(exe2formal, c02);

        jp03.add(new JLabel(" "), c03);

        tabbedPane.add("Execute", jp03);

        c.add(tabbedPane, BorderLayout.NORTH);
        if (automatic > 0) {
            GraphicLib.enableComponents(tabbedPane, false);
        }

        // Issue #18
        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setButtons();
            }
        });

        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        if (automatic == 0) {
            jta.append("Select options and then, click on 'start' to launch simulator C++ code generation / compilation\n");
        }
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        textAreaWriter = new JTextAreaWriter(jta);

        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);


        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);


        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));


        start.addActionListener(this);
        stop.addActionListener(this);


        close = new JButton("Close", IconManager.imgic27);
        close.setPreferredSize(new Dimension(100, 30));
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        if (automatic == 0) {
            jp2.add(start);
            jp2.add(stop);
        }
        jp2.add(close);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void updateInteractiveSimulation() {
        if (automatic == 0) {
            exe2.setEnabled(exe.isSelected());
            exe2int.setEnabled(exeint.isSelected());
            exe2formal.setEnabled(exeformal.isSelected());
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        //

        // Compare the action command to the known actions.
        updateInteractiveSimulation();


        if (command.equals("Start")) {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (command.equals("addOneIgnoredTepe")) {
            addOneIgnoredTepe();
        } else if (command.equals("addOneValidatedTepe")) {
            addOneValidatedTepe();
        } else if (command.equals("allValidatedTepe")) {
            allValidatedTepe();
        } else if (command.equals("allIgnoredTepe")) {
            allIgnoredTepe();
        } else if (evt.getSource() == versionSimulator) {
            selectedItem = versionSimulator.getSelectedIndex();
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }

        updateStaticList();
        optimizeModeSelected = optimizemode.isSelected();
        activatePenaltiesSelected = activatePenalties.isSelected();
        // wasClosed = true;
        dispose();
    }
//
//    public boolean wasClosed() {
//        return wasClosed;
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
        if (automatic > 0) {
            startProcess = false;
            t = new Thread(this);
            mode = STARTED;
            go = true;
            t.start();
        } else {
            /*if ((interactiveSimulationSelected) && (jp1.getSelectedIndex() == 2)) {
              startProcess = true;
              dispose();
              } else {*/
            startProcess = false;
            t = new Thread(this);
            mode = STARTED;
            setButtons();
            go = true;
            t.start();
            //}
        }
    }

    private void testGo() throws InterruptedException {
        if (!go) {
            throw new InterruptedException("Stopped by user");
        }
    }

    private boolean hasError() {
        return errorTabIndex > -1;
    }

    private void resetError() {
        mode = NOT_STARTED;
        errorTabIndex = -1;
    }

    private boolean canSwitchNextTab() {
        return !hasError() && tabbedPane.getSelectedIndex() < 2;
    }

    private boolean canExecute() {
        return errorTabIndex < 0 || tabbedPane.getSelectedIndex() <= errorTabIndex;
    }

    @Override
    public void run() {
        try {
            if (automatic > 0) {
                generateCode();
                testGo();

                if (canExecute()) {
                    compileCode();

                    if (canExecute()) {
                        testGo();
                        executeSimulation();
                    }
                }
            } else {
                if (canExecute()) {
                    resetError();

                    if (tabbedPane.getSelectedIndex() == 0) {
                        generateCode();
                    } else if (tabbedPane.getSelectedIndex() == 1) {
                        compileCode();
                    } else {
                        executeSimulation();
                    }

                    if (canSwitchNextTab()) {
                        tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
                    }
                }
            }
        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }

        if (hasError()) {
            jta.append("\nAn error occured when processing command!\n");
        } else {
            jta.append("\nReady to process next command.\n");
        }

        //updateMode();
        setButtons();
    }

    private void generateCode() throws InterruptedException {
        String list;

        jta.append("Generating simulator C++ code\n");

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

        if (!validateData()) {
            return;
        }

        //        try {
        //            defaultUnitCycleValue = unitcycle.getText();
        //        }
        //        catch ( final Throwable th ) {
        //              final String message = "Wrong number of cycles: " + unitcycle.getText() + "!";
        //            jta.append( message );
        //            jta.append("Aborting");
        //            jta.append("\n\nReady to process next command\n");
        //            //checkMode();
        //            TraceManager.addError( message, th );
        //            setError();
        //
        //            return;
        //        }

        selectedItem = versionSimulator.getSelectedIndex();

        switch (selectedItem) {
            case 0: {       //Simulator without CPs (Daniel's version)
                // Making EBRDDs
                List<EBRDD> al = new ArrayList<EBRDD>();
                List<TEPE> alTepe = new ArrayList<TEPE>();
                TEPE tepe;
                AvatarRequirementPanelTranslator arpt = new AvatarRequirementPanelTranslator();

                for (int k = 0; k < valTepe.size(); k++) {
                    testGo();
                    tepe = arpt.generateTEPESpecification(valTepe.get(k));
                    jta.append("TEPE: " + tepe.getName() + "\n");
                    jta.append("Checking syntax\n");
                    // tepe.checkSyntax();
                    alTepe.add(tepe);
                    jta.append("Done.\n");
                }

                final IDiploSimulatorCodeGenerator tml2systc;

                // Generating code
                if (mgui.gtm.getTMLMapping() == null) {
                    if (mgui.gtm.getArtificialTMLMapping() == null) {
                        tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(mgui.gtm.getTMLModeling(), al, alTepe);
                        //tml2systc = new tmltranslator.tomappingsystemc2.TML2MappingSystemC(mgui.gtm.getTMLModeling(), al, alTepe);
                    } else {
                        TraceManager.addDev("Using artifical mapping");
                        tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(mgui.gtm.getArtificialTMLMapping(), al, alTepe);
                        //tml2systc = new tmltranslator.tomappingsystemc2.TML2MappingSystemC(mgui.gtm.getArtificialTMLMapping(), al, alTepe);
                    }
                } else {
                    tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(mgui.gtm.getTMLMapping(), al, alTepe);
                    //                tml2systc = new tmltranslator.tomappingsystemc2.TML2MappingSystemC(mgui.gtm.getTMLMapping(), al, alTepe);
                }

                try {
                    tml2systc.generateSystemC(debugmode.isSelected(), optimizemode.isSelected());
                    testGo();
                    jta.append("Simulator code generation done\n");

                    for (final TEPE tep : alTepe) {
                        //TraceManager.addDev(tep.toString());
                    }

                    jta.append("Saving C++ files...\n");

                    pathCode = code1.getText();
                    TraceManager.addDev("SystemC code generated in" + pathCode);
                    if (!SpecConfigTTool.checkAndCreateSystemCDir(pathCode))
                        throw new Throwable();
                    tml2systc.saveFile(pathCode, "appmodel");

                    jta.append("C++ files saved." + System.lineSeparator());
                } catch (final Throwable th) {
                    final String message = "Could not generate simulator code!";
                    jta.append(System.lineSeparator() + message + System.lineSeparator());

                    TraceManager.addError(message, th);
                    setError();
                }
                // Update the penalty file if necessary
                // Must read the penalty file first

                Penalties penalty = new Penalties(pathCode + File.separator + "src_simulator");
                int changed = penalty.handlePenalties(activatePenalties.isSelected());

                if (changed == -1) {
                    final String message = "Could not generate penalty code";
                    jta.append(System.lineSeparator() + message + System.lineSeparator());
                    setError();
                }

                if (changed == 1) {
                    mustRecompileAll = true;
                } else {
                    mustRecompileAll = false;
                }

                break;
            }
        }



    }   //End of method generateCode()

    private boolean validateData() {
        switch (tabbedPane.getSelectedIndex()) {
            case 0: {
                try {
                    unitCycleValue = Float.parseFloat(unitcycle.getText());
                } catch (final NumberFormatException ex) {
                    final String message = "Wrong number of cycles: " + unitcycle.getText() + "!";
                    jta.append(message);
                    jta.append("Aborting");
                    jta.append("\n\nReady to process next command\n");
                    TraceManager.addError(message, ex);
                    setError();

                    return false;
                }

                break;
            }
            default:
                break;
        }

        return true;
    }

    private void compileCode()
            throws InterruptedException {

        if (mustRecompileAll) {
            jta.append("\"make clean\" must be performed\n");
            rshc = new RshClient(simulatorHost);
            String cmdClean = compiler1.getText() + " clean";
            try {
                if (!processCmd(cmdClean, jta, 0)) {
                    setError();
                    return;
                }

                jta.append("Make clean done.\n");
            } catch (final Throwable th) {
                jta.append("Error: " + th.getMessage() + ".\n");
                TraceManager.addError(th);
                setError();
                return;
            }
        }


        String cmd = compiler1.getText();

        jta.append("Compiling simulator code with command: \n" + cmd + "\n");

        rshc = new RshClient(simulatorHost);

        // Assume data are on the remote host Command
        try {
            if (!processCmd(cmd, jta, 0)) {

                // Issue #18: This check does not work when for example the locale is French so we
                // explicitly return and test the value of the return code
                //if ( jta.getText().contains("Error ") ) {
                //                              mode = ERROR;
                //                              setButtons();
                setError();

                return;
            }

            jta.append("Compilation done.\n");
        } catch (final Throwable th) {
            jta.append("Error: " + th.getMessage() + ".\n");
            // mode = STOPPED;
            //setButtons();
            TraceManager.addError(th);
            setError();

            return;
        }
        //        catch (Exception e) {
        //            mode =      STOPPED;
        //            setButtons();
        //            return;
        //        }
    }


    private void executeSimulation() throws InterruptedException {
        //        if (hasError) {
        //            jta.append("Simulation not executed: error");
        //            return;
        //        }
        int toDo = automatic;

        if (toDo == 0) {
            if (exe.isSelected()) {
                toDo = ONE_TRACE;
            } else if (exeint.isSelected()) {
                toDo = ANIMATION;
            } else {
                toDo = FORMAL_VERIFICATION;
            }
        }

        switch (toDo) {
            case ONE_TRACE:
                executeSimulationCmd(exe2.getText(), "Generating one simulation trace");
                String[] tab = exe2.getText().split(" ");
                SpecConfigTTool.lastVCD = tab[2];
                SpecConfigTTool.ExternalCommand1 = "gtkwave " + SpecConfigTTool.lastVCD;
                break;
            case ANIMATION:
                dispose();
                mgui.interactiveSimulationSystemC(getPathInteractiveExecute());
                break;
            case FORMAL_VERIFICATION:
                executeSimulationCmd(exe2formal.getText(), "Running formal verification");
                break;
        }
    }

    private void executeSimulationCmd(String cmd, String text) throws InterruptedException {
        try {
            jta.append(text + " with command: \n" + cmd + "\n");

            rshc = new RshClient(simulatorHost);
            // It assumes that data are on the remote host
            // Command

            processCmd(cmd, jta, 0);
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

    protected boolean processCmd(final String cmd,
                                 final JTextArea _jta,
                                 final Integer okReturnCode)
            throws LauncherException {
        rshc.setCmd(cmd);

        rshc.sendExecuteCommandRequest(okReturnCode != null);

        rshc.writeCommandMessages(textAreaWriter);

        try {
            return ((okReturnCode == null) || okReturnCode.equals(rshc.getProcessReturnCode()));
        } catch (Exception e) {
            return false;
        }
    }
    //
    //    protected void checkMode() {
    //          if (mode!=ERROR){
    //          mode = NOT_STARTED;
    //          }
    //    }

    protected void setButtons() {
        if (automatic == 0) {
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
                    break;
                case ERROR:
                    start.setEnabled(canExecute());
                    stop.setEnabled(false);
                    close.setEnabled(true);

                    // Issue #18: Resets the busy cursor to normal
                    getGlassPane().setVisible(false);

                    break;
                default:
                    start.setEnabled(false);
                    stop.setEnabled(false);
                    close.setEnabled(true);
                    getGlassPane().setVisible(false);

                    break;
            }
        } else {
            close.setEnabled(true);
        }
    }

    @Override
    public boolean hasToContinue() {
        return go;
    }

    public void appendOut(String s) {
        jta.append(s);
    }

    public void setError() {
        errorTabIndex = tabbedPane.getSelectedIndex();
        mode = ERROR;
        //hasError = true;
    }

    public boolean isInteractiveSimulationSelected() {
        return (startProcess && interactiveSimulationSelected);
    }

    public String getPathInteractiveExecute() {
        return pathInteractiveExecute;
    }

    // List selection listener
    public void valueChanged(ListSelectionEvent e) {
        setList();
    }

    private void setList() {
        if (automatic == 0) {
            int i1 = listIgnoredTepe.getSelectedIndex();
            int i2 = listValidatedTepe.getSelectedIndex();

            if (i1 == -1) {
                addOneValidatedTepe.setEnabled(false);
            } else {
                addOneValidatedTepe.setEnabled(true);
                //listValidated.clearSelection();
            }

            if (i2 == -1) {
                addOneIgnoredTepe.setEnabled(false);
            } else {
                addOneIgnoredTepe.setEnabled(true);
                //listIgnored.clearSelection();
            }

            if (ignTepe.size() == 0) {
                allValidatedTepe.setEnabled(false);
            } else {
                allValidatedTepe.setEnabled(true);
            }

            if (valTepe.size() == 0) {
                allIgnoredTepe.setEnabled(false);
            } else {
                allIgnoredTepe.setEnabled(true);
            }
        }
    }

    private void addOneIgnoredTepe() {
        int[] list = listValidatedTepe.getSelectedIndices();
        Vector<AvatarPDPanel> v = new Vector<AvatarPDPanel>();
        //Object o;
        for (int i = 0; i < list.length; i++) {
            final AvatarPDPanel panel = valTepe.elementAt(list[i]);
            ignTepe.addElement(panel);
            v.addElement(panel);
        }

        valTepe.removeAll(v);
        listIgnoredTepe.setListData(ignTepe);
        listValidatedTepe.setListData(valTepe);
        setList();
    }

    private void addOneValidatedTepe() {
        int[] list = listIgnoredTepe.getSelectedIndices();
        Vector<AvatarPDPanel> v = new Vector<AvatarPDPanel>();
        //Object o;
        for (int i = 0; i < list.length; i++) {
            final AvatarPDPanel panel = ignTepe.elementAt(list[i]);
            valTepe.addElement(panel);
            v.addElement(panel);
        }

        ignTepe.removeAll(v);
        listIgnoredTepe.setListData(ignTepe);
        listValidatedTepe.setListData(valTepe);
        setList();
    }

    private void allValidatedTepe() {
        valTepe.addAll(ignTepe);
        ignTepe.removeAllElements();
        listIgnoredTepe.setListData(ignTepe);
        listValidatedTepe.setListData(valTepe);
        setList();
    }

    private void allIgnoredTepe() {
        ignTepe.addAll(valTepe);
        valTepe.removeAllElements();
        listIgnoredTepe.setListData(ignTepe);
        listValidatedTepe.setListData(valTepe);
        setList();
    }

    private void updateStaticList() {
        validatedTepe = new Vector<AvatarPDPanel>();
        ignoredTepe = new Vector<AvatarPDPanel>();
        int i;

        for (i = 0; i < ignTepe.size(); i++) {
            ignoredTepe.add(ignTepe.get(i));
        }

        for (i = 0; i < valTepe.size(); i++) {
            validatedTepe.add(valTepe.get(i));
        }
    }
}
