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

import common.SpecConfigTTool;
import dseengine.DSEConfiguration;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.GraphicLib;
import myutil.ScrolledJTextArea;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import ui.util.IconManager;
import ui.MainGUI;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * Class JDialogDSE
 * Dialog for managing the generation of ProVerif code and execution of
 * ProVerif
 * Creation: 10/09/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.1 10/09/2010
 */
public class JDialogDSE extends JDialog implements ActionListener, ListSelectionListener, Runnable, DocumentListener {

    protected MainGUI mgui;

    protected static String pathCode;
    protected static String pathExecute;

    protected final static int NOT_SELECTED = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    int mode;


    // Action
    protected JRadioButton dseButton;
    protected JRadioButton simButton;
    protected JRadioButton newResultsButton;
    protected JLabel nbOfMappings;
    protected JLabel infoNbOfMappings;
    protected JCheckBox randomMappingBox;
    protected JTextField randomMappingNb;
    protected ButtonGroup group;
    //components

    //protected JButton addConstraint;


    JCheckBox outputTXT, outputHTML;
    protected JCheckBox secAnalysis;
    protected JTextField encTime2, decTime2, secOverhead2;

    protected JButton next, previous;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected String simulator;

    // files
    protected JTextField tmlDirectory, mappingFile, modelFile, resultsDirectory;
    protected JRadioButton defaultFiles;
    protected JRadioButton specificFiles;
    protected ButtonGroup groupOfFiles;

    protected JTextField simulationThreads, simulationCycles, minCPU, maxCPU, simulationsPerMapping;
    protected JTextArea outputText;
    protected String output = "";

    // Weights
    protected JSlider JSMinSimulationDuration, JSAverageSimulationDuration, JSMaxSimulationDuration,
            JSArchitectureComplexity, JSMinCPUUsage, JSAverageCPUUsage, JSMaxCPUUsage, JSMinBusUsage,
            JSAverageBusUsage, JSMaxBusUsage, JSMinBusContention, JSAverageBusContention, JSMaxBusContention;

    private DSEConfiguration config;

    protected static String tmlDir;
    protected static String mapFile = "spec.tmap";
    protected static String modFile = "spec.tml";
    protected static String resDirect;
    protected static String simThreads = "10";
    protected static String simCycles = "10000";
    protected static String nbMinCPU = "1";
    protected static String nbMaxCPU = "1";
    protected static String nbSim = "100";
    protected static String encCC = "100";
    protected static String decCC = "100";
    protected static String secOv = "100";
    protected static String randomMappingsSelected = "100";
    protected static boolean useRandomMappings = false;
    protected static boolean secAnalysisState = false;

    // Outputs
    protected static boolean outputTXTState = false;
    protected static boolean outputHTMLState = false;

    //JList<String> constraints;
    //JTextField constraintTextField;
    //JList<String> contraints;

    protected JTabbedPane jp1;

    private Thread t;
    private boolean go = false;
    //   private boolean hasError = false;
    //protected boolean startProcess = false;

    // private String hostProVerif;

    protected RshClient rshc;


    /**
     * Creates new form
     */
    public JDialogDSE(Frame f, MainGUI _mgui, String title, String _simulator, String dir) {
        super(f, title, true);

        mgui = _mgui;
        simulator = _simulator;
        tmlDir = dir + "/";
        resDirect = _simulator + "results/";

        initComponents();
        myInitComponents();

        pack();

        /*TraceManager.addDev("Nb of tabs:" + jp1.getTabCount());
        for(int i=0; i<jp1.getTabCount(); i++) {
            TraceManager.addDev("Title at: " + i + ": " + jp1.getTitleAt(i));
        }*/
        if (jp1.getTabCount() > 5) {
            jp1.setSelectedIndex(5);
        }

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
        handleStartButton();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        // Issue #41 Ordering of tabbed panes
        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();


        // Files
        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        //jp03.setBorder(new javax.swing.border.TitledBorder("Directories and files"));
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;

        defaultFiles = new JRadioButton("Use current model");
        specificFiles = new JRadioButton("Use textual specification");
        jp03.add(defaultFiles, c03);
        jp03.add(specificFiles, c03);


        jp03.add(new JLabel("Directory of TML specification files"), c03);
        tmlDirectory = new JTextField(tmlDir);
        jp03.add(tmlDirectory, c03);

        jp03.add(new JLabel("Mapping File name (.tmap)"), c03);
        mappingFile = new JTextField(mapFile);
        jp03.add(mappingFile, c03);

        jp03.add(new JLabel("Modeling File name (.tml)"), c03);
        modelFile = new JTextField(modFile);
        jp03.add(modelFile, c03);

        jp03.add(new JLabel("Results Directory"), c03);
        resultsDirectory = new JTextField(resDirect);
        jp03.add(resultsDirectory, c03);

        groupOfFiles = new ButtonGroup();
        groupOfFiles.add(defaultFiles);
        groupOfFiles.add(specificFiles);
        defaultFiles.addActionListener(this);
        specificFiles.addActionListener(this);
        defaultFiles.setSelected(true);
        defaultFileIsSelected(true);


        // Simulation
        JPanel jp03_sim = new JPanel();
        GridBagLayout gridbag03_sim = new GridBagLayout();
        GridBagConstraints c03_sim = new GridBagConstraints();
        jp03_sim.setLayout(gridbag03);
        jp03_sim.setBorder(new javax.swing.border.TitledBorder("Simulation options"));

        c03_sim.weighty = 1.0;
        c03_sim.weightx = 1.0;
        c03_sim.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03_sim.fill = GridBagConstraints.BOTH;
        c03_sim.gridheight = 1;

        jp03_sim.add(new JLabel("Number of Simulation Threads"), c03_sim);
        simulationThreads = new JTextField(simThreads);
        jp03_sim.add(simulationThreads, c03_sim);

        jp03_sim.add(new JLabel("Number of Simulations Per Mapping"), c03_sim);
        simulationsPerMapping = new JTextField(nbSim);
        jp03_sim.add(simulationsPerMapping, c03_sim);

        jp03_sim.add(new JLabel("Max. Number of Simulation Cycles"), c03_sim);
        simulationCycles = new JTextField(simCycles);
        jp03_sim.add(simulationCycles, c03_sim);

        jp1.add("Directories", jp03);
        jp1.add("Simulation options", jp03_sim);

        jp03 = new JPanel();
        gridbag03 = new GridBagLayout();
        c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;

        jp03.add(new JLabel("Minimum Number of CPUs"), c03);
        minCPU = new JTextField(nbMinCPU);
        minCPU.getDocument().addDocumentListener(this);
        jp03.add(minCPU, c03);

        jp03.add(new JLabel("Maximum Number of CPUs"), c03);
        maxCPU = new JTextField(nbMaxCPU);
        maxCPU.getDocument().addDocumentListener(this);
        jp03.add(maxCPU, c03);

        jp1.add("HW Platform", jp03);


        JPanel jp05 = new JPanel();
        GridBagLayout gridbag05 = new GridBagLayout();
        GridBagConstraints c05 = new GridBagConstraints();
        jp05.setLayout(gridbag05);

        c05.weighty = 1.0;
        c05.weightx = 1.0;
        c05.gridwidth = GridBagConstraints.RELATIVE;
        c05.fill = GridBagConstraints.BOTH;
        c05.gridheight = 1;

        jp05.add(new JLabel("Minimum Simulation Duration"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinSimulationDuration = new JSlider(-10, 10);
        JSMinSimulationDuration.setMinorTickSpacing(5);
        JSMinSimulationDuration.setMajorTickSpacing(1);
        Dictionary<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMinSimulationDuration.setLabelTable(labelTable);
        JSMinSimulationDuration.setPaintTicks(true);
        JSMinSimulationDuration.setPaintLabels(true);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        jp05.add(JSMinSimulationDuration, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Average Simulation Duration"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageSimulationDuration = new JSlider(-10, 10);
        JSAverageSimulationDuration.setMinorTickSpacing(5);
        JSAverageSimulationDuration.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSAverageSimulationDuration.setLabelTable(labelTable);
        JSAverageSimulationDuration.setPaintTicks(true);
        JSAverageSimulationDuration.setPaintLabels(true);
        jp05.add(JSAverageSimulationDuration, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Maximum Simulation Duration"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxSimulationDuration = new JSlider(-10, 10);
        JSMaxSimulationDuration.setMinorTickSpacing(5);
        JSMaxSimulationDuration.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMaxSimulationDuration.setLabelTable(labelTable);
        JSMaxSimulationDuration.setPaintTicks(true);
        JSMaxSimulationDuration.setPaintLabels(true);
        jp05.add(JSMaxSimulationDuration, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Architecture Complexity"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSArchitectureComplexity = new JSlider(-10, 10);
        JSArchitectureComplexity.setMinorTickSpacing(5);
        JSArchitectureComplexity.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSArchitectureComplexity.setLabelTable(labelTable);
        JSArchitectureComplexity.setPaintTicks(true);
        JSArchitectureComplexity.setPaintLabels(true);
        jp05.add(JSArchitectureComplexity, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Min CPU Usage"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinCPUUsage = new JSlider(-10, 10);
        JSMinCPUUsage.setMinorTickSpacing(5);
        JSMinCPUUsage.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMinCPUUsage.setLabelTable(labelTable);
        JSMinCPUUsage.setPaintTicks(true);
        JSMinCPUUsage.setPaintLabels(true);
        jp05.add(JSMinCPUUsage, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Average CPU Usage"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageCPUUsage = new JSlider(-10, 10);
        JSAverageCPUUsage.setMinorTickSpacing(5);
        JSAverageCPUUsage.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSAverageCPUUsage.setLabelTable(labelTable);
        JSAverageCPUUsage.setPaintTicks(true);
        JSAverageCPUUsage.setPaintLabels(true);
        jp05.add(JSAverageCPUUsage, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Max CPU Usage"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxCPUUsage = new JSlider(-10, 10);
        JSMaxCPUUsage.setMinorTickSpacing(5);
        JSMaxCPUUsage.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMaxCPUUsage.setLabelTable(labelTable);
        JSMaxCPUUsage.setPaintTicks(true);
        JSMaxCPUUsage.setPaintLabels(true);
        jp05.add(JSMaxCPUUsage, c05);

        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Min Bus Usage"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinBusUsage = new JSlider(-10, 10);
        JSMinBusUsage.setMinorTickSpacing(5);
        JSMinBusUsage.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMinBusUsage.setLabelTable(labelTable);
        JSMinBusUsage.setPaintTicks(true);
        JSMinBusUsage.setPaintLabels(true);
        jp05.add(JSMinBusUsage, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Average Bus Usage"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageBusUsage = new JSlider(-10, 10);
        JSAverageBusUsage.setMinorTickSpacing(5);
        JSAverageBusUsage.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSAverageBusUsage.setLabelTable(labelTable);
        JSAverageBusUsage.setPaintTicks(true);
        JSAverageBusUsage.setPaintLabels(true);
        jp05.add(JSAverageBusUsage, c05);


        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Max Bus Usage"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxBusUsage = new JSlider(-10, 10);
        JSMaxBusUsage.setMinorTickSpacing(5);
        JSMaxBusUsage.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMaxBusUsage.setLabelTable(labelTable);
        JSMaxBusUsage.setPaintTicks(true);
        JSMaxBusUsage.setPaintLabels(true);
        jp05.add(JSMaxBusUsage, c05);

        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Minimum Bus Contention"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinBusContention = new JSlider(-10, 10);
        JSMinBusContention.setMinorTickSpacing(5);
        JSMinBusContention.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSMinBusContention.setLabelTable(labelTable);
        JSMinBusContention.setPaintTicks(true);
        JSMinBusContention.setPaintLabels(true);
        jp05.add(JSMinBusContention, c05);

        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Average Bus Contention"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageBusContention = new JSlider(-10, 10);
        JSAverageBusContention.setMinorTickSpacing(5);
        JSAverageBusContention.setMajorTickSpacing(1);
        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(-10), new JLabel("-1.0"));
        labelTable.put(new Integer(-5), new JLabel("-0.5"));
        labelTable.put(new Integer(0), new JLabel("0.0"));
        labelTable.put(new Integer(5), new JLabel("0.5"));
        labelTable.put(new Integer(10), new JLabel("1.0"));
        JSAverageBusContention.setLabelTable(labelTable);
        JSAverageBusContention.setPaintTicks(true);
        JSAverageBusContention.setPaintLabels(true);
        jp05.add(JSAverageBusContention, c05);

        c05.gridwidth = GridBagConstraints.RELATIVE;
        jp05.add(new JLabel("Maximum Bus Contention"), c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxBusContention = new JSlider(-10, 10);
        JSMaxBusContention.setMinorTickSpacing(5);
        JSMaxBusContention.setMajorTickSpacing(1);
        JSMaxBusContention.setLabelTable(labelTable);
        JSMaxBusContention.setPaintTicks(true);
        JSMaxBusContention.setPaintLabels(true);
        jp05.add(JSMaxBusContention, c05);

        jp1.add("Weights", jp05);


        jp03 = new JPanel();
        gridbag03 = new GridBagLayout();
        c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;


        secAnalysis = new JCheckBox("Security Analysis");
        secAnalysis.setSelected(secAnalysisState);
        jp03.add(secAnalysis, c03);

        jp03.add(new JLabel("Encryption Computational Complexity"), c03);
        encTime2 = new JTextField(encCC);
        jp03.add(encTime2, c03);

        jp03.add(new JLabel("Decryption Computational Complexity"), c03);
        decTime2 = new JTextField(decCC);
        jp03.add(decTime2, c03);

        jp03.add(new JLabel("Data Overhead (bits)"), c03);
        secOverhead2 = new JTextField(secOv);
        jp03.add(secOverhead2, c03);

        jp1.add("Security", jp03);


        JPanel select = new JPanel(new BorderLayout());

        jp03 = new JPanel();
        gridbag03 = new GridBagLayout();
        c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Which formats for outputs?"));
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;

        outputTXT = new JCheckBox("Save text files?");
        outputTXT.addActionListener(this);
        outputTXT.setSelected(outputTXTState);
        jp03.add(outputTXT, c03);

        outputHTML = new JCheckBox("Save html files?");
        outputHTML.addActionListener(this);
        outputHTML.setSelected(outputHTMLState);
        jp03.add(outputHTML, c03);

        select.add(jp03, BorderLayout.SOUTH);

        //constraints = new JList<String>();
        //jp03.add(constraints, c03);

        //constraintTextField=new JTextField();
        /*addConstraint = new JButton("Add Constraint");
        addConstraint.addActionListener(this);
        addConstraint.setPreferredSize(new Dimension(50, 25));
        addConstraint.setActionCommand("addConstraint");
        if (mgui.isExperimentalOn()) {
            jp03.add(addConstraint, c03);
        }*/

        jp03 = new JPanel();
        gridbag03 = new GridBagLayout();
        c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("What do you want to do?"));
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;
        //c03.fill = GridBagConstraints.NONE;
        //c03.anchor = GridBagConstraints.WEST;

        group = new ButtonGroup();
        dseButton = new JRadioButton("Run Design Space Exploration");
        dseButton.addActionListener(this);
        jp03.add(dseButton, c03);

        int anchor = c03.anchor;
        int fill = c03.fill;
        c03.fill = GridBagConstraints.NONE;
        c03.anchor = GridBagConstraints.EAST;
        c03.gridwidth = 1;
        infoNbOfMappings = new JLabel("Nb of mappings to be explored: ");
        jp03.add(infoNbOfMappings, c03);

        c03.fill = fill;
        c03.anchor = GridBagConstraints.WEST;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfMappings = new JLabel("" + getNbOfPossibleMappings());
        jp03.add(nbOfMappings, c03);

        c03.gridwidth = 1;
        c03.fill = GridBagConstraints.NONE;
        c03.anchor = GridBagConstraints.EAST;
        randomMappingBox = new JCheckBox("Use Random mappings, at most: ");
        randomMappingBox.setSelected(useRandomMappings);
        jp03.add(randomMappingBox, c03);
        c03.fill = fill;


        c03.anchor = GridBagConstraints.WEST;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        randomMappingNb = new JTextField(randomMappingsSelected);
        jp03.add(randomMappingNb, c03);

        c03.anchor = anchor;

        simButton = new JRadioButton("Run Lots of Simulations");
        simButton.addActionListener(this);
        jp03.add(simButton, c03);
        newResultsButton = new JRadioButton("Update results with new weights");
        newResultsButton.addActionListener(this);
        jp03.add(newResultsButton, c03);
        group.add(dseButton);
        group.add(simButton);
        group.add(newResultsButton);
        newResultsButton.setEnabled(false);

        select.add(jp03, BorderLayout.CENTER);

        jp1.add("Outputs", select);

        //mainP.add(jp03);
        //mainP.add(jp03_sim);


        JPanel jp04 = new JPanel();

        GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        jp04.setLayout(gridbag04);

        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridwidth = GridBagConstraints.REMAINDER; //end row
        c04.fill = GridBagConstraints.BOTH;
        c04.gridheight = 1;

        //jp04.setBorder(new javax.swing.border.TitledBorder("DSE Output"));
        //jp04.add(new JLabel("Design Space Exploration Output"), c04);


        outputText = new ScrolledJTextArea();
        outputText.setEditable(false);
        outputText.setMargin(new Insets(10, 10, 10, 10));
        outputText.setTabSize(3);
        outputText.append("How to start?" +
                "\n - Select at least one output format (txt, html)\n - Select an exploration way (DSE, intensive simulation)");
        JScrollPane jsp = new JScrollPane(outputText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300, 300));
        Font f = new Font("Courrier", Font.BOLD, 12);
        outputText.setFont(f);
        jp04.add(jsp, c04);
        //jp1.add("Results", jp04);

        c.add(jp1, BorderLayout.NORTH);
        c.add(jp04, BorderLayout.CENTER);

        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);
        next = new JButton("Next", IconManager.imgic49);
        previous = new JButton("Prev", IconManager.imgic47);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));
        next.setPreferredSize(new Dimension(100, 30));
        previous.setPreferredSize(new Dimension(100, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        next.addActionListener(this);
        previous.addActionListener(this);

        JPanel jp2 = new JPanel();

        jp2.add(previous);
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        jp2.add(next);

        c.add(jp2, BorderLayout.SOUTH);


    }

    private String getNbOfPossibleMappings() {
        TMLModeling tmlm;
        TMLMapping map = mgui.gtm.getTMLMapping();
        if (map != null) {
            tmlm = map.getTMLModeling();
        } else {
            tmlm = mgui.gtm.getTMLModeling();
        }
        if (tmlm == null) {
            return "No current graphical model";
        }
        try {
            return "" + DSEConfiguration.getNbOfPossibleMappings(Integer.parseInt(nbMinCPU), Integer.parseInt(nbMaxCPU),
                    tmlm);
        } catch (Exception e) {
            return "Invalid model or nb of CPUs";
        }
    }

    public void storeValues() {
        tmlDir = tmlDirectory.getText();
        mapFile = mappingFile.getText();
        modFile = modelFile.getText();
        simThreads = simulationThreads.getText();
        simCycles = simulationCycles.getText();
        resDirect = resultsDirectory.getText();
        nbMinCPU = minCPU.getText();
        nbMaxCPU = maxCPU.getText();
        nbSim = simulationsPerMapping.getText();
        encCC = encTime2.getText();
        decCC = decTime2.getText();
        secAnalysisState = secAnalysis.isSelected();
        secOv = secOverhead2.getText();
        outputTXTState = outputTXT.isSelected();
        outputHTMLState = outputHTML.isSelected();
        randomMappingsSelected = randomMappingNb.getText();
        useRandomMappings = randomMappingBox.isSelected();
    }


    private void handleStartButton() {

        nbOfMappings.setEnabled(dseButton.isSelected());
        infoNbOfMappings.setEnabled(dseButton.isSelected());
        randomMappingBox.setEnabled(dseButton.isSelected());
        randomMappingNb.setEnabled(dseButton.isSelected());

        if (mode != NOT_STARTED && mode != NOT_SELECTED) {
            return;
        }

        boolean oneResult, oneAction;
        oneResult = outputHTML.isSelected() || outputTXT.isSelected();
        oneAction = dseButton.isSelected() || simButton.isSelected() || newResultsButton.isSelected();

        if (oneAction == false || oneResult == false) {
            mode = NOT_SELECTED;
        } else {
            mode = NOT_STARTED;
        }
        setButtons();

    }

    public void valueChanged(ListSelectionEvent e) {
    }


    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (evt.getSource() == start) {
            startProcess();
        } else if (evt.getSource() == stop) {
            stopProcess();
        } else if (evt.getSource() == close) {
            closeDialog();
        } else if (evt.getSource() == previous) {
            previousTab();
        } else if (evt.getSource() == next) {
            nextTab();
        } else if ((evt.getSource() == dseButton) || (evt.getSource() == simButton) || (evt.getSource() == newResultsButton)|| (evt.getSource() ==
                outputHTML) || (evt.getSource() == outputTXT)) {
            handleStartButton();
        } else if (evt.getSource() == defaultFiles) {
            defaultFileIsSelected(true);
        } else if (evt.getSource() == specificFiles) {
            defaultFileIsSelected(false);
        }
    }

    private void defaultFileIsSelected(boolean b) {
        tmlDirectory.setEnabled(!b);
        mappingFile.setEnabled(!b);
        modelFile.setEnabled(!b);
    }

    public void nextTab() {
        try {
            jp1.setSelectedIndex(jp1.getSelectedIndex() + 1);
        } catch (Exception e) {
        }
    }

    public void previousTab() {
        if (jp1.getSelectedIndex() > 0)
            jp1.setSelectedIndex(jp1.getSelectedIndex() - 1);

    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
        storeValues();
    }

    public void stopProcess() {
        if (rshc != null) {
            try {
                rshc.stopCommand();
            } catch (LauncherException le) {
            }
        }
        rshc = null;
        mode = STOPPED;
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
    //
    //    private void testGo() throws InterruptedException {
    //        if (go == false) {
    //            throw new InterruptedException("Stopped by user");
    //        }
    //    }

    public void run() {
        //      String cmd;
        //    String list, data;
        //  int cycle = 0;
        output = "";

        //  hasError = false;
        //try {
        mapFile = mappingFile.getText();
        modFile = modelFile.getText();
        tmlDir = tmlDirectory.getText();
        resDirect = resultsDirectory.getText();
        simThreads = simulationThreads.getText();
        simCycles = simulationCycles.getText();
        nbMinCPU = minCPU.getText();
        nbMaxCPU = maxCPU.getText();
        nbSim = simulationsPerMapping.getText();
        randomMappingsSelected = randomMappingNb.getText();
        useRandomMappings = randomMappingBox.isSelected();

        TraceManager.addDev("Thread started");
        //   File testFile;

        if (simButton.isSelected() || dseButton.isSelected()) {
            encCC = encTime2.getText();
            decCC = decTime2.getText();
            secOv = secOverhead2.getText();

            config = new DSEConfiguration();
            config.addSecurity = secAnalysis.isSelected();
            config.encComp = encCC;
            config.overhead = secOv;
            config.decComp = decCC;

            config.mainGUI = mgui;
            // TMLMapping map = mgui.gtm.getTMLMapping();

            // Randomness definition
            if (config.setRandomness(useRandomMappings, randomMappingsSelected) != 0) {
                TraceManager.addDev("Randomness error " + randomMappingsSelected + " error");
                output += "Randomness error " + randomMappingsSelected + "\n";
                checkMode();
                return;
            } else {
                TraceManager.addDev("Randomness set to " + randomMappingsSelected);
            }

            if (defaultFiles.isSelected()) {
                mgui.generateTMLTxt();
                tmlDir = SpecConfigTTool.TMLCodeDirectory;
                mapFile = "spec.tmap";
                modFile = "spec.tml";
            }



            if (config.setModelPath(tmlDir) != 0) {
                TraceManager.addDev("TML Directory file at " + tmlDir + " error");
                output += "TML Directory file at " + tmlDir + " error \n";
                checkMode();
                return;
            } else {
                TraceManager.addDev("Set directory to " + tmlDir);
            }
            if (!mapFile.isEmpty()) {
                if (config.setMappingFile(mapFile) < 0) {
                    TraceManager.addDev("Mapping at " + mapFile + " error");
                    output += "Mapping at " + mapFile + " error";
                    mode = STOPPED;
                    return;
                } else {
                    TraceManager.addDev("Set mapping file to " + mapFile);
                }
            }
            if (config.setTaskModelFile(modFile) != 0) {
                TraceManager.addDev("Model File " + modFile + " error");
                output += "Model File " + modFile + " error \n";
                checkMode();
                return;
            } else {
                TraceManager.addDev("Set model file to " + modFile);
            }
            if (config.setPathToSimulator(simulator) != 0) {
                TraceManager.addDev("Simulator at " + mapFile + " error");
                output += "Simulator at " + mapFile + " error \n";
                checkMode();
                return;
            } else {
                TraceManager.addDev("Simulator set");
            }

            if (config.setPathToResults(resDirect) != 0) {
                TraceManager.addDev("Results Directory at " + resDirect + " error");
                output += "Results Directory at " + resDirect + " error \n";
                return;
            } else {
                TraceManager.addDev("Results Directory set");
            }

            if (config.setNbOfSimulationThreads(simThreads) != 0) {
                TraceManager.addDev("Simulation threads error: " + simThreads);
                output += "Simulation threads error: " + simThreads + "\n";
                return;
            }

            if (config.setNbOfSimulationsPerMapping(nbSim) != 0) {
                TraceManager.addDev("Simulations per mapping error: " + nbSim);
                output += "Simulation per mapping error: " + nbSim + "\n";
                return;
            }

            if (config.setSimulationCompilationCommand("make -j9 -C") != 0) {
                TraceManager.addDev("Simulation compilation error");
                output += "Simulation compilation error" + "\n";
                return;
            }
            if (config.setSimulationExecutionCommand("run.x") != 0) {
                TraceManager.addDev("Simulation execution error");
                output += "Simulation execution error \n";
                return;
            }

            TraceManager.addDev("Setting min nb of CPUs to:" + nbMinCPU);
            if (config.setMinNbOfCPUs(nbMinCPU) != 0) {
                TraceManager.addDev("Can't set Min # CPUS to " + nbMinCPU);
                output += "Can't set Min # CPUS to " + nbMinCPU + "\n";
            }

            TraceManager.addDev("Setting max nb of CPUs to:" + nbMaxCPU);
            if (config.setMaxNbOfCPUs(nbMaxCPU) != 0) {
                TraceManager.addDev("Can't set Max # CPUS to " + nbMaxCPU);
                output += "Can't set Max # CPUS to " + nbMaxCPU + "\n";
            }

            config.setOutputTXT(outputTXT.isSelected() ? "true" : "false");
            config.setOutputHTML(outputHTML.isSelected() ? "true" : "false");
            config.setRecordResults("true");



            // Simulations
            if (simButton.isSelected()) {
                if (config.runParallelSimulation(nbSim, true, true) != 0) {
                    output += "Simulation Failed:\n" + config.getErrorMessage() + " \n";
                    outputText.setText(output);
                    checkMode();
                    return;
                } else {
                    output += "Simulation Succeeded";
                    outputText.setText(output);
                }

                // DSE
            } else if (dseButton.isSelected()) {
                if (config.runDSE("", false, false) != 0) {
                    TraceManager.addDev("Can't run DSE");

                }
                TraceManager.addDev("DSE run");
            }

            // Results
            if (config.printAllResults("", true, true) != 0) {
                TraceManager.addDev("Can't print all results");
                output += "Can't print all results \n";
            }
            //System.out.println("Results printed");
            if (config.printResultsSummary("", true, true) != 0) {
                TraceManager.addDev("Can't print result summary");
                output += "Can't print result summary \n";
            }
            //System.out.println("Results summary printed");
            //jp1.setSelectedIndex(1);
            outputText.setText(output + "\n" + config.overallResults);
            newResultsButton.setEnabled(true);

            //}
            //} catch (Exception e){
            //    System.out.println(e);
            //}
        } else if (newResultsButton.isSelected()) {
            double[] tap = new double[]{JSMinSimulationDuration.getValue(), JSAverageSimulationDuration.getValue(), JSMaxSimulationDuration.getValue(), JSArchitectureComplexity.getValue(), JSMinCPUUsage.getValue(), JSAverageCPUUsage.getValue(), JSMaxCPUUsage.getValue(), JSMinBusUsage.getValue(), JSAverageBusUsage.getValue(), JSMaxBusUsage.getValue(), JSMinBusContention.getValue(), JSAverageBusContention.getValue(), JSMaxBusContention.getValue()};
            for (int i = 0; i < tap.length; i++) {
                tap[i] = tap[i] / 10.0;
            }
            if (config.replaceTapValues(tap) < 0) {
                output += "Error changing values";
            }
            //System.out.println(tap[0]);
            if (config.printResultsSummary("", true, true) != 0) {
                TraceManager.addDev("Can't print result summary");
                output += "Can't print result summary \n";
            }
            //jp1.setSelectedIndex(3);
            outputText.setText(output + "\n" + config.overallResults);
        }

        checkMode();
        setButtons();
        //TraceManager.addDev("Unselecting radio buttons");
        group.clearSelection();
        handleStartButton();


        //System.out.println("Selected item=" + selectedItem);
    }

    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        checkMode();
        return s;
    }

    protected void checkMode() {
        mode = NOT_SELECTED;
    }

    protected void setButtons() {
        switch (mode) {
            case NOT_SELECTED:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
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

    public void updateNbOfMappings() {
        nbMinCPU = minCPU.getText();
        nbMaxCPU = maxCPU.getText();
        nbOfMappings.setText("" + getNbOfPossibleMappings());
    }

    public void changedUpdate(DocumentEvent e) {
        updateNbOfMappings();
    }
    public void removeUpdate(DocumentEvent e) {
        updateNbOfMappings();
    }
    public void insertUpdate(DocumentEvent e) {
        updateNbOfMappings();
    }

    public boolean hasToContinue() {
        return (go == true);
    }
    //
    //    public void setError() {
    //        hasError = true;
    //    }
    //
}
