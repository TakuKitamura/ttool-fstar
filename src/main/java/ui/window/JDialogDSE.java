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

import dseengine.DSEConfiguration;
import launcher.LauncherException;
import launcher.RshClient;
import myutil.GraphicLib;
import myutil.ScrolledJTextArea;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.util.IconManager;
import ui.MainGUI;

import javax.swing.*;
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
 * @version 1.1 10/09/2010
 * @author Ludovic APVRILLE
 */
public class JDialogDSE extends JDialog implements ActionListener, ListSelectionListener, Runnable  {

    protected MainGUI mgui;

    protected static String pathCode;
    protected static String pathExecute;

    protected final static int NOT_SELECTED = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    int mode;
    JRadioButton dseButton;
    JRadioButton simButton;
    JButton addConstraint;
    ButtonGroup group;
    //components



    JCheckBox outputTXT, outputHTML;
    protected JCheckBox secAnalysis;
    protected JTextField encTime2, decTime2, secOverhead2;

    protected JButton start;
    protected JButton stop;
    protected JButton close;
    String simulator;

    protected JTextField tmlDirectory, mappingFile, modelFile, simulationThreads, resultsDirectory, simulationCycles, minCPU, maxCPU, simulationsPerMapping;
    protected JTextArea outputText;
    protected String output = "";

    protected JSlider JSMinSimulationDuration, JSAverageSimulationDuration, JSMaxSimulationDuration, JSArchitectureComplexity, JSMinCPUUsage, JSAverageCPUUsage, JSMaxCPUUsage, JSMinBusUsage, JSAverageBusUsage, JSMaxBusUsage, JSMinBusContention, JSAverageBusContention, JSMaxBusContention;
    DSEConfiguration config;

    protected static String tmlDir;
    protected static String mapFile = "spec.tmap";
    protected static String modFile = "spec.tml";
    protected static String resDirect;
    protected static String simThreads="10";
    protected static String simCycles="10000";
    protected static String NbMinCPU ="1";
    protected static String NbMaxCPU ="1";
    protected static String Nbsim ="100";
    protected static String encCC="100";
    protected static String decCC="100";
    protected static String secOv = "100";
    protected static boolean secAnalysisState = false;
    protected static boolean outputTXTState = false;
    protected static boolean outputHTMLState = false;

    JList<String> constraints;
    JTextField constraintTextField;
    protected JTabbedPane jp1;

    private Thread t;
    private boolean go = false;
    //   private boolean hasError = false;
    //protected boolean startProcess = false;
    JList<String> contraints;
    // private String hostProVerif;

    protected RshClient rshc;


    /** Creates new form  */
    public JDialogDSE(Frame f, MainGUI _mgui, String title, String _simulator, String dir) {
        super(f, title, true);

        mgui = _mgui;
        simulator=_simulator;
        tmlDir = dir+"/";
        resDirect = _simulator + "results/";

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

        // Issue #41 Ordering of tabbed panes
        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();


        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Mapping Exploration"));



        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;

        jp03.add(new JLabel("Directory of TML specification files"),c03);
        tmlDirectory = new JTextField(tmlDir);
        jp03.add(tmlDirectory, c03);

        jp03.add(new JLabel("Mapping File name (.tmap)"),c03);
        mappingFile = new JTextField(mapFile);
        jp03.add(mappingFile,c03);

        jp03.add(new JLabel("Modeling File name (.tml)"),c03);
        modelFile = new JTextField(modFile);
        jp03.add(modelFile,c03);


        jp03.add(new JLabel("Number of Simulation Threads"),c03);
        simulationThreads = new JTextField(simThreads);
        jp03.add(simulationThreads, c03);

        jp03.add(new JLabel("Results Directory"),c03);
        resultsDirectory = new JTextField(resDirect);
        jp03.add(resultsDirectory, c03);

        jp03.add(new JLabel("Number of Simulation Cycles"),c03);
        simulationCycles = new JTextField(simCycles);
        jp03.add(simulationCycles, c03);


        jp03.add(new JLabel("Minimum Number of CPUs"),c03);
        minCPU = new JTextField(NbMinCPU);
        jp03.add(minCPU, c03);

        jp03.add(new JLabel("Maximum Number of CPUs"),c03);
        maxCPU = new JTextField(NbMaxCPU);
        jp03.add(maxCPU, c03);

        jp03.add(new JLabel("Number of Simulations Per Mapping"),c03);
        simulationsPerMapping = new JTextField(Nbsim);
        jp03.add(simulationsPerMapping, c03);


        secAnalysis = new JCheckBox("Security Analysis");
        secAnalysis.setSelected(secAnalysisState);
        jp03.add(secAnalysis,c03);

        jp03.add(new JLabel("Encryption Computational Complexity"),c03);
        encTime2 = new JTextField(encCC);
        jp03.add(encTime2,c03);

        jp03.add(new JLabel("Decryption Computational Complexity"),c03);
        decTime2 = new JTextField(decCC);
        jp03.add(decTime2,c03);

        jp03.add(new JLabel("Data Overhead (bits)"),c03);
        secOverhead2 = new JTextField(secOv);
        jp03.add(secOverhead2,c03);

        outputTXT = new JCheckBox("Save text files?");
        outputTXT.addActionListener(this);
        outputTXT.setSelected(outputTXTState);
        jp03.add(outputTXT, c03);

        outputHTML = new JCheckBox("Save html files?");
        outputHTML.addActionListener(this);
        outputHTML.setSelected(outputHTMLState);
        jp03.add(outputHTML, c03);

        constraints = new JList<String>();
        jp03.add(constraints, c03);


        constraintTextField=new JTextField();

        addConstraint = new JButton("Add Constraint");
        addConstraint.addActionListener(this);
        addConstraint.setPreferredSize(new Dimension(50, 25));
        addConstraint.setActionCommand("addConstraint");
        jp03.add(addConstraint, c03);




        group = new ButtonGroup();
        dseButton = new JRadioButton("Run Design Space Exploration");
        dseButton.addActionListener(this);
        jp03.add(dseButton,c03);
        simButton = new JRadioButton("Run Lots of Simulations");
        simButton.addActionListener(this);
        jp03.add(simButton,c03);
        group.add(dseButton);
        group.add(simButton);



        jp1.add("Mapping Exploration", jp03);

        JPanel jp04 = new JPanel();

        GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        jp04.setLayout(gridbag04);

        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridwidth = GridBagConstraints.REMAINDER; //end row
        c04.fill = GridBagConstraints.BOTH;
        c04.gridheight = 1;

        jp04.setBorder(new javax.swing.border.TitledBorder("DSE Output"));
        jp04.add(new JLabel("Design Space Exploration Output"), c04);


        outputText = new ScrolledJTextArea();
        outputText.setEditable(false);
        outputText.setMargin(new Insets(10, 10, 10, 10));
        outputText.setTabSize(3);
        outputText.append("Output results");
        JScrollPane jsp = new JScrollPane(outputText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300,300));
        Font f = new Font("Courrier", Font.BOLD, 12);
        outputText.setFont(f);
        jp04.add(jsp, c04);
        jp1.add("DSE Output", jp04);

        JPanel jp05 = new JPanel();
        GridBagLayout gridbag05 = new GridBagLayout();
        GridBagConstraints c05 = new GridBagConstraints();
        jp05.setLayout(gridbag05);

        c05.weighty = 1.0;
        c05.weightx = 1.0;
        c05.gridwidth = GridBagConstraints.RELATIVE;
        c05.fill = GridBagConstraints.BOTH;
        c05.gridheight = 1;

        jp05.add(new JLabel("Minimum Simulation Duration"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinSimulationDuration = new JSlider(-10,10);
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
        jp05.add(new JLabel("Average Simulation Duration"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageSimulationDuration = new JSlider(-10,10);
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
        jp05.add(new JLabel("Maximum Simulation Duration"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxSimulationDuration = new JSlider(-10,10);
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
        jp05.add(new JLabel("Architecture Complexity"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSArchitectureComplexity = new JSlider(-10,10);
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
        jp05.add(new JLabel("Min CPU Usage"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinCPUUsage = new JSlider(-10,10);
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
        jp05.add(new JLabel("Average CP UUsage"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageCPUUsage = new JSlider(-10,10);
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
        jp05.add(new JLabel("Max CPU Usage"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxCPUUsage = new JSlider(-10,10);
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
        jp05.add(new JLabel("Min Bus Usage"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinBusUsage = new JSlider(-10,10);
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
        jp05.add(new JLabel("Average Bus Usage"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageBusUsage = new JSlider(-10,10);
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
        jp05.add(new JLabel("Max Bus Usage"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxBusUsage = new JSlider(-10,10);
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
        jp05.add(new JLabel("Minimum Bus Contention"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMinBusContention = new JSlider(-10,10);
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
        jp05.add(new JLabel("Average Bus Contention"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSAverageBusContention = new JSlider(-10,10);
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
        jp05.add(new JLabel("Maximum Bus Contention"),c05);
        c05.gridwidth = GridBagConstraints.REMAINDER;
        JSMaxBusContention = new JSlider(-10,10);
        JSMaxBusContention.setMinorTickSpacing(5);
        JSMaxBusContention.setMajorTickSpacing(1);
        JSMaxBusContention.setLabelTable(labelTable);
        JSMaxBusContention.setPaintTicks(true);
        JSMaxBusContention.setPaintLabels(true);
        jp05.add(JSMaxBusContention, c05);

        jp1.add("DSE Custom", jp05);


        c.add(jp1, BorderLayout.NORTH);


        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void storeValues() {
        tmlDir = tmlDirectory.getText();
        mapFile = mappingFile.getText();
        modFile = modelFile.getText();
        simThreads = simulationThreads.getText();
        simCycles = simulationCycles.getText();
        resDirect = resultsDirectory.getText();
        NbMinCPU = minCPU.getText();
        NbMaxCPU = maxCPU.getText();
        Nbsim = simulationsPerMapping.getText();
        encCC = encTime2.getText();
        decCC = decTime2.getText();
        secAnalysisState = secAnalysis.isSelected();
        secOv = secOverhead2.getText();
        outputTXTState = outputTXT.isSelected();
        outputHTMLState = outputHTML.isSelected();

    }



    private void handleStartButton() {
        if (mode != NOT_STARTED  && mode != NOT_SELECTED) {
            return;
        }
        if (jp1.getSelectedIndex() !=1){
            mode = NOT_STARTED;
            setButtons();
            return;
        }
        boolean oneResult, oneAction;
        oneResult = outputHTML.isSelected() || outputTXT.isSelected();
        oneAction = dseButton.isSelected() || simButton.isSelected();

        if (oneAction == false || oneResult == false) {
            mode = NOT_SELECTED;
        } else {
            mode = NOT_STARTED;
        }
        setButtons();

    }

    public void valueChanged(ListSelectionEvent e) {
    }


    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
        else if ((evt.getSource() == dseButton) || (evt.getSource() == simButton) || (evt.getSource() == outputHTML) || (evt.getSource() == outputTXT) ){
            handleStartButton();
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
        storeValues();
    }

    public void stopProcess() {
        if (rshc != null ){
            try {
                rshc.stopCommand();
            } catch (LauncherException le) {
            }
        }
        rshc = null;
        mode =  STOPPED;
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
        output="";

        //  hasError = false;
        //try {
        mapFile = mappingFile.getText();
        modFile = modelFile.getText();
        tmlDir = tmlDirectory.getText();
        resDirect = resultsDirectory.getText();
        simThreads = simulationThreads.getText();
        simCycles = simulationCycles.getText();
        NbMinCPU = minCPU.getText();
        NbMaxCPU = maxCPU.getText();
        Nbsim = simulationsPerMapping.getText();
        TraceManager.addDev("Thread started");
        //   File testFile;

        if (jp1.getSelectedIndex()==0){
            encCC=encTime2.getText();
            decCC=decTime2.getText();
            secOv = secOverhead2.getText();

            config = new DSEConfiguration();
            config.addSecurity = secAnalysis.isSelected();
            config.encComp = encCC;
            config.overhead = secOv;
            config.decComp = decCC;

            config.mainGUI = mgui;
            // TMLMapping map = mgui.gtm.getTMLMapping();

            if (config.setModelPath(tmlDir) != 0) {
                TraceManager.addDev("TML Directory file at " + tmlDir + " error");
                output+="TML Directory file at " + tmlDir + " error \n";
                checkMode();
                return;
            }
            else {
                TraceManager.addDev("Set directory to " + tmlDir);
            }
            if (!mapFile.isEmpty()){
                if (config.setMappingFile(mapFile) <0) {
                    TraceManager.addDev("Mapping at " + mapFile + " error");
                    output+="Mapping at " + mapFile + " error";
                    mode = STOPPED;
                    return;
                }
                else {
                    TraceManager.addDev("Set mapping file to " + mapFile);
                }
            }
            if (config.setTaskModelFile(modFile)!=0){
                TraceManager.addDev("Model File " + modFile +" error");
                output+="Model File " + modFile +" error \n";
                checkMode();
                return;
            }
            else {
                TraceManager.addDev("Set model file to " + modFile);
            }
            if (config.setPathToSimulator(simulator) != 0) {
                TraceManager.addDev("Simulator at " + mapFile + " error");
                output+="Simulator at " + mapFile + " error \n";
                checkMode();
                return;
            }
            else {
                TraceManager.addDev("Simulator set");
            }

            if (config.setPathToResults(resDirect) != 0) {
                TraceManager.addDev("Results Directory at " + resDirect + " error");
                output+="Results Directory at " + resDirect + " error \n";
                return;
            }
            else {
                TraceManager.addDev("Results Directory set");
            }

            if (config.setNbOfSimulationThreads(simThreads) != 0) {
                TraceManager.addDev("Simulation threads error: "+simThreads);
                output+="Simulation threads error: "+simThreads+"\n";
                return;
            }

            if (config.setNbOfSimulationsPerMapping(Nbsim) != 0) {
                TraceManager.addDev("Simulations per mapping error: "+Nbsim);
                output+="Simulation per mapping error: "+Nbsim+"\n";
                return;
            }

            if (config.setSimulationCompilationCommand("make -j9 -C") !=0){
                TraceManager.addDev("Simulation compilation error");
                output+="Simulation compilation error"+"\n";
                return;
            }
            if (config.setSimulationExecutionCommand("run.x") !=0){
                TraceManager.addDev("Simulation execution error");
                output+="Simulation execution error \n";
                return;
            }

            TraceManager.addDev("Setting min nb of CPUs to:" + NbMinCPU);
            if (config.setMinNbOfCPUs(NbMinCPU) != 0) {
                TraceManager.addDev("Can't set Min # CPUS to " + NbMinCPU);
                output+="Can't set Min # CPUS to " + NbMinCPU+"\n";
            }

            TraceManager.addDev("Setting max nb of CPUs to:" + NbMaxCPU);
            if (config.setMaxNbOfCPUs(NbMaxCPU) != 0) {
                TraceManager.addDev("Can't set Max # CPUS to " + NbMaxCPU);
                output+="Can't set Max # CPUS to " + NbMaxCPU +"\n";
            }

            config.setOutputTXT(outputTXT.isSelected()? "true": "false");
            config.setOutputHTML(outputHTML.isSelected()?"true": "false");
            config.setRecordResults("true");

	    // Simulations
            if (simButton.isSelected()){
                if (config.runParallelSimulation(Nbsim, true, true) != 0) {
                    output+="Simulation Failed:\n" + config.getErrorMessage() + " \n";
                    outputText.setText(output);
                    checkMode();
                    return;
                }
                else {
                    output+="Simulation Succeeded";
                    outputText.setText(output);
                }

		// DSE
            } else if (dseButton.isSelected()){
                if (config.runDSE("", false, false)!=0){
                    TraceManager.addDev("Can't run DSE");

                }
                TraceManager.addDev("DSE run");
            }

	    // Results
            if (config.printAllResults("", true, true)!=0){
                TraceManager.addDev("Can't print all results");
                output+="Can't print all results \n";
            }
            //System.out.println("Results printed");
            if (config.printResultsSummary("", true, true)!=0){
                TraceManager.addDev("Can't print result summary");
                output+="Can't print result summary \n";
            }
            //System.out.println("Results summary printed");
            jp1.setSelectedIndex(1);
            outputText.setText(output + "\n" + config.overallResults);
        }
        //} catch (Exception e){
        //    System.out.println(e);
        //}
        if (jp1.getSelectedIndex()==2){
            double[] tap = new double[]{JSMinSimulationDuration.getValue(), JSAverageSimulationDuration.getValue(), JSMaxSimulationDuration.getValue(), JSArchitectureComplexity.getValue(), JSMinCPUUsage.getValue(), JSAverageCPUUsage.getValue(), JSMaxCPUUsage.getValue(), JSMinBusUsage.getValue(), JSAverageBusUsage.getValue(), JSMaxBusUsage.getValue(), JSMinBusContention.getValue(), JSAverageBusContention.getValue(), JSMaxBusContention.getValue()};
            for (int i=0; i<tap.length; i++){
                tap[i] = tap[i]/10.0;
            }
            if (config.replaceTapValues(tap)<0){
                output+="Error changing values";
            }
            //System.out.println(tap[0]);
            if (config.printResultsSummary("", true, true)!=0){
                TraceManager.addDev("Can't print result summary");
                output+="Can't print result summary \n";
            }
            jp1.setSelectedIndex(3);
            outputText.setText(output + "\n" + config.overallResults);
        }
        checkMode();
        setButtons();

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
        switch(mode) {
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

    public boolean hasToContinue() {
        return (go == true);
    }
    //
    //    public void setError() {
    //        hasError = true;
    //    }
    //
}
