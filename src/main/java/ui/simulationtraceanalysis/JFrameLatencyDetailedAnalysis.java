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
package ui.simulationtraceanalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
//import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;
import org.xml.sax.SAXException;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.GraphicLib;
import myutil.ScrolledJTextArea;
import myutil.TraceManager;
import tmltranslator.TMLActivity;
import tmltranslator.TMLMapping;
import tmltranslator.TMLTask;
import ui.CheckableLatency;
import ui.ColorManager;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TGTextFieldWithHelp;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.interactivesimulation.SimulationTransactionParser;
import ui.tmlad.TMLADExecI;
import ui.tmlad.TMLActivityDiagramPanel;

/**
 * Class JFrameLatencyDetailedAnalysis: this class opens the frame showing the
 * latency details
 * 
 * 23/09/2019
 *
 * @author Maysam Zoor
 */
public class JFrameLatencyDetailedAnalysis extends JFrame implements ActionListener, Runnable, MouseListener, ItemListener, ChangeListener {
//    private JButton saveGraph, viewGraph;
    private JTextArea jta;
    private JScrollPane jsp;
    private JTabbedPane commandTab, resultTab;/* , resultTabDetailed; */
    private JPanel loadxml, commands, jp01, jp02, /* activities, */ graphAnalysisResult, jp03, jp04, jp05, jp06, progressBarpanel, addRules; // ,graphAnalysisResultDetailed;
    private JButton buttonClose, buttonShowDGraph, buttonSaveDGraph, buttonBrowse, latencybutton, buttonCheckPath, addRulebutton, viewRulesbutton,
            preciseAnalysisbutton, showAllOp;
    private Vector<String> checkedTransactions = new Vector<String>();
    private Vector<String> allTasks = new Vector<String>();
    private HashMap<String, Integer> checkedT = new HashMap<String, Integer>();
    private JTextField saveDirName;
    private JTextField saveFileName;
    private JComboBox<String> tasksDropDownCombo1 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo2 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo3 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo4 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo5 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo6 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo7 = new JComboBox<String>();
    private Vector<String> readChannelTransactions = new Vector<String>();
    private Vector<String> writeChannelTransactions = new Vector<String>();
    private Vector<String> ruleDirection = new Vector<String>();
    private SaveGraphToolBar sgtb;
    private LatencyDetailedAnalysisActions[] actions;
    private JFileChooser fc, fc2;
    private String[] columnNames = new String[5];
    private String[] columnMinMaxNames = new String[5];
    private Object[][] dataDetailedByTask;
    private Object[][] dataDetailedMinMax;
    private static JTable table11, table12;
    private JTextField file1;
    private File file;
    private static Vector<SimulationTransaction> transFile1;
    private JLabel task2, taskL;
    private DirectedGraphTranslator dgraph;
    private JScrollPane scrollPane11, scrollPane12;// , scrollPane13;
    private LatencyAnalysisParallelAlgorithms tc;
    private Boolean allop = false;
    private TMLMapping<TGComponent> tmap1;
    public static final String[] helpStrings = { "considerRule.html", "taint.html", "addRule.html" };
    private static final String START_TIME_COL_NAME = "Start Time ";
    private static final String END_TIME_COL_NAME = "End Time ";
    private static final String LATENCY_COL_NAME = "Latency ";
    private static final String OP_A_COL_NAME = "OPERATOR A";
    private static final String OP_B_COL_NAME = "OPERATOR B ";
    private static final String PROGRESS_BAR = "Progress of Graph Generation";
    // @SuppressWarnings("deprecation")
    private JProgressBar pbar;
    private JFrameCompareLatencyDetail jframeCompareLatencyDetail;
    private JCheckBox taintFirstOp, considerRules;
    protected TGTextFieldWithHelp checkRuleHelp, taintHelp, ruleHelp;

    public JFrameLatencyDetailedAnalysis(TMLMapping<TGComponent> tmap, List<TMLComponentDesignPanel> cpanels, SimulationTrace selectedST,
            LatencyAnalysisParallelAlgorithms tc1) {
        super("Precise Latency Analysis");
        initActions();
        tmap1 = tmap;
        fillCheckedTrans(tmap);
        tc = tc1;
        file = new File(selectedST.getFullPath());
        // ruleDirection.add("Before");
        ruleDirection.add("After");
        GridBagLayout gridbagmain = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(gridbagmain);
        // mainConstraint.weighty = 1.0;
        // mainConstraint.weightx = 1.0;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 0;
        // mainConstraint.gridwidth = GridBagConstraints.REMAINDER; // end row
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        buttonShowDGraph = new JButton(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH]);
        latencybutton = new JButton(actions[LatencyDetailedAnalysisActions.ACT_LATENCY]);
        preciseAnalysisbutton = new JButton(actions[LatencyDetailedAnalysisActions.ACT_LATENCY_PRECISE_ANALYSIS]);
        buttonClose = new JButton(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL]);
        buttonCheckPath = new JButton(actions[LatencyDetailedAnalysisActions.ACT_CHECK_PATH]);
        JPanel jp = new JPanel();
        jp.add(buttonShowDGraph);
        jp.add(latencybutton);
        jp.add(preciseAnalysisbutton);
        jp.add(buttonCheckPath);
        jp.add(buttonClose);
        latencybutton.setEnabled(false);
        buttonShowDGraph.setEnabled(false);
        preciseAnalysisbutton.setEnabled(false);
        framePanel.add(jp, mainConstraint);
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        progressBarpanel = new JPanel(gridbag01);
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        // c01.fill = GridBagConstraints.BOTH;
        JLabel pBarLabel = new JLabel(PROGRESS_BAR, JLabel.LEFT);
        progressBarpanel.add(pBarLabel, c01);
        c01.gridx = 1;
        c01.gridy = 0;
        pbar = new JProgressBar();
        pbar.setForeground(Color.GREEN);
        progressBarpanel.add(pbar, c01);
        mainConstraint.gridheight = 1;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 3;
        mainConstraint.ipady = 40;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        framePanel.add(progressBarpanel, mainConstraint);
        commands = new JPanel(new BorderLayout());
        commands.setBorder(new javax.swing.border.TitledBorder("load/Save"));
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 1;
        mainConstraint.gridwidth = 1; // end row
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        framePanel.add(commands, mainConstraint);
        graphAnalysisResult = new JPanel(new BorderLayout());
        graphAnalysisResult.setBorder(new javax.swing.border.TitledBorder("Latency Analysis "));
        mainConstraint.weighty = 1.0;
        mainConstraint.weightx = 1.0;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 2;
        mainConstraint.ipady = 200;
        mainConstraint.fill = GridBagConstraints.BOTH;
        framePanel.add(graphAnalysisResult, mainConstraint);
        jp05 = new JPanel(new BorderLayout());
        // mainpanel.add(split, BorderLayout.SOUTH);
        mainConstraint.weighty = 00;
        mainConstraint.ipady = 100;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 4;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        framePanel.add(jp05, mainConstraint);
        commandTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        loadxml = new JPanel(gridbag01);
        commandTab.addTab("Load XML", null, loadxml, "Load XML");
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        JLabel xmlLabel = new JLabel("Simulation Trace as XML File ", JLabel.LEFT);
        loadxml.add(xmlLabel, c01);
        c01.gridx = 0;
        c01.gridy = 2;
        JLabel tasksLabel = new JLabel("Study Latency Between ", JLabel.LEFT);
        loadxml.add(tasksLabel, c01);
        c01.gridx = 0;
        c01.gridy = 1;
        taskL = new JLabel("Select Task 1 ", JLabel.LEFT);
        loadxml.add(taskL, c01);
        file1 = new JTextField(40);
        file1.setMinimumSize(new Dimension(300, 30));
        file1.setText(selectedST.getFullPath());
        file1.setEditable(false);
        c01.gridx = 1;
        c01.gridy = 0;
        loadxml.add(file1, c01);
        c01.gridx = 1;
        c01.gridy = 1;
        tasksDropDownCombo6 = new JComboBox<String>(checkedTransactions);
        loadxml.add(tasksDropDownCombo6, c01);
        c01.gridx = 1;
        c01.gridy = 2;
        tasksDropDownCombo1 = new JComboBox<String>(checkedTransactions);
        loadxml.add(tasksDropDownCombo1, c01);
        c01.weightx = 0.2;
        c01.gridx = 2;
        c01.gridy = 1;
        task2 = new JLabel("and Task 2 ", JLabel.LEFT);
        loadxml.add(task2, c01);
        c01.gridx = 2;
        c01.gridy = 2;
        JLabel op2 = new JLabel("and ", JLabel.LEFT);
        loadxml.add(op2, c01);
        c01.weightx = 1.0;
        c01.gridx = 4;
        c01.gridy = 2;
        tasksDropDownCombo2 = new JComboBox<String>(checkedTransactions);
        loadxml.add(tasksDropDownCombo2, c01);
        c01.gridx = 4;
        c01.gridy = 1;
        tasksDropDownCombo7 = new JComboBox<String>(checkedTransactions);
        loadxml.add(tasksDropDownCombo7, c01);
        task2.setVisible(false);
        taskL.setVisible(false);
        tasksDropDownCombo6.setVisible(false);
        tasksDropDownCombo7.setVisible(false);
        c01.gridx = 5;
        c01.gridy = 2;
        showAllOp = new JButton(actions[LatencyDetailedAnalysisActions.ACT_SHOW_ALL_OP]);
        loadxml.add(showAllOp, c01);
        c01.gridx = 0;
        c01.gridy = 4;
        considerRules = new JCheckBox("Consider Rules");
        loadxml.add(considerRules, c01);
        checkRuleHelp = new TGTextFieldWithHelp("", 0);
        checkRuleHelp.setEditable(false);
        // loadxml.add(sliceTime, c01);
        c01.gridx = 1;
        c01.gridy = 4;
        c01.anchor = GridBagConstraints.WEST;
        checkRuleHelp.makeEndHelpButton(helpStrings[0], tc.getMainGUI(), tc.getMainGUI().getHelpManager(), loadxml, c01);
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 4;
        c01.anchor = GridBagConstraints.EAST;
        taintFirstOp = new JCheckBox("Taint First Operator ");
        loadxml.add(taintFirstOp, c01);
        taintHelp = new TGTextFieldWithHelp("", 0);
        taintHelp.setEditable(false);
        c01.gridx = 2;
        c01.gridy = 4;
        c01.anchor = GridBagConstraints.WEST;
        taintHelp.makeEndHelpButton(helpStrings[1], tc.getMainGUI(), tc.getMainGUI().getHelpManager(), loadxml, c01);
        c01.gridx = 3;
        c01.gridy = 4;
        GridBagLayout gridbag05 = new GridBagLayout();
        GridBagConstraints c05 = new GridBagConstraints();
        // Save
        jp06 = new JPanel(gridbag05);
        JLabel tasksRules = new JLabel("Add Rule:  ", JLabel.LEFT);
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        jp06.add(tasksRules, c01);
        c01.gridx = 1;
        c01.gridy = 0;
        tasksDropDownCombo3 = new JComboBox<String>(readChannelTransactions);
        jp06.add(tasksDropDownCombo3, c01);
        c01.gridx = 2;
        c01.gridy = 0;
        tasksDropDownCombo5 = new JComboBox<String>(ruleDirection);
        jp06.add(tasksDropDownCombo5, c01);
        c01.gridx = 3;
        c01.gridy = 0;
        tasksDropDownCombo4 = new JComboBox<String>(writeChannelTransactions);
        jp06.add(tasksDropDownCombo4, c01);
        addRulebutton = new JButton(actions[LatencyDetailedAnalysisActions.ACT_ADD_RULE]);
        c01.gridx = 4;
        c01.gridy = 0;
        jp06.add(addRulebutton, c01);
        viewRulesbutton = new JButton(actions[LatencyDetailedAnalysisActions.ACT_VIEW_RULE]);
        c01.gridx = 4;
        c01.gridy = 2;
        jp06.add(viewRulesbutton, c01);
        c01.gridx = 0;
        c01.gridy = 0;
        ruleHelp = new TGTextFieldWithHelp("", 0);
        ruleHelp.setEditable(false);
        c01.anchor = GridBagConstraints.EAST;
        ruleHelp.makeEndHelpButton(helpStrings[2], tc.getMainGUI(), tc.getMainGUI().getHelpManager(), jp06, c01);
        JScrollPane scrollPane = new JScrollPane(jp06);
        commandTab.addTab("Add Rules", null, scrollPane, "Add Rules");
        GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        // Save
        jp01 = new JPanel(gridbag04);
        commandTab.addTab("Save Graph", null, jp01, "Save Graph");
        c04.anchor = GridBagConstraints.FIRST_LINE_START;
        c04.gridheight = 1;
        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridx = 0;
        c04.gridy = 0;
        c04.gridwidth = GridBagConstraints.REMAINDER; // end row
        c04.fill = GridBagConstraints.HORIZONTAL;
        c04.gridheight = 1;
        sgtb = new SaveGraphToolBar(this);
        jp01.add(sgtb, c04);
        jp02 = new JPanel(gridbag04);
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        jp02.setLayout(gridbag01);
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; // end row
        c01.fill = GridBagConstraints.HORIZONTAL;
        jp02.add(new JLabel("Directory:"), c01);
        saveDirName = new JTextField(30);
        // saveDirName.setMinimumSize(new Dimension(100, 100));
        if (ConfigurationTTool.SystemCCodeDirectory != null) {
            saveDirName.setText(SpecConfigTTool.SystemCCodeDirectory);
        }
        jp02.add(saveDirName, c01);
        jp02.add(new JLabel("File name:"), c01);
        saveFileName = new JTextField(30);
        jp02.add(saveFileName, c01);
        c04.gridheight = 1;
        c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridx = 0;
        c04.gridy = 2;
        c04.gridwidth = GridBagConstraints.REMAINDER; // end row
        c04.fill = GridBagConstraints.HORIZONTAL;
        c04.gridheight = 1;
        jp01.add(jp02, c04);
        // mainTop.add(loadxml, c02);
        commands.add(commandTab, BorderLayout.NORTH);
        columnNames[0] = OP_A_COL_NAME;
        columnNames[1] = START_TIME_COL_NAME;
        columnNames[2] = OP_B_COL_NAME;
        columnNames[3] = END_TIME_COL_NAME;
        columnNames[4] = LATENCY_COL_NAME;
        // columnNames[5] = "Related Tasks Details ";
        // columnNames[6] = "Total time- Related Tasks ";
        dataDetailedByTask = new Object[0][0];
        jp03 = new JPanel(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel(dataDetailedByTask, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table11 = new JTable(tableModel);
        scrollPane11 = new JScrollPane(table11, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane11.setVisible(true);
        jp03.add(scrollPane11, BorderLayout.CENTER);
        columnMinMaxNames[0] = OP_A_COL_NAME;
        columnMinMaxNames[1] = START_TIME_COL_NAME;
        columnMinMaxNames[2] = OP_B_COL_NAME;
        columnMinMaxNames[3] = END_TIME_COL_NAME;
        columnMinMaxNames[4] = LATENCY_COL_NAME;
        dataDetailedMinMax = new Object[0][0];
        jp04 = new JPanel(new BorderLayout());
        DefaultTableModel tableModel2 = new DefaultTableModel(dataDetailedMinMax, columnMinMaxNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table12 = new JTable(tableModel2);
        scrollPane12 = new JScrollPane(table12, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane12.setVisible(true);
        jp04.add(scrollPane12, BorderLayout.CENTER);
        resultTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        resultTab.addTab("Latency Values", null, jp03, "Latency Detailed by Tasks ");
        resultTab.addTab("Min/Max Latency", null, jp04, "Min and Max Latency");
        graphAnalysisResult.add(resultTab, BorderLayout.CENTER);
        jta = new ScrolledJTextArea();
        jta.setBackground(ColorManager.InteractiveSimulationJTABackground);
        jta.setForeground(ColorManager.InteractiveSimulationJTAForeground);
        jta.setMinimumSize(new Dimension(800, 400));
        jta.setRows(15);
        jta.setMaximumSize(new Dimension(800, 500));
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Generating the corresponding Directed Graph.\n Please wait...\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));
        jsp.setVisible(true);
        jp05.setMaximumSize(new Dimension(800, 500));
        jp05.add(jsp, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        tc.setjFrameLDA(this);
        tc.setCpanels(cpanels);
        tc.setTmap(tmap);
        tc.start(1);
        tc.run();
    }

    public JProgressBar getPbar() {
        return pbar;
    }

    public void updateBar(int newValue) {
        pbar.setValue(newValue);
    }

    public DirectedGraphTranslator getDgraph() {
        return dgraph;
    }

    protected void generateDirectedGraph(TMLMapping<TGComponent> tmap, List<TMLComponentDesignPanel> cpanels) {
        try {
            dgraph = new DirectedGraphTranslator(this, jframeCompareLatencyDetail, tmap, cpanels, 0);
            jta.append("A directed graph with " + dgraph.getGraphsize() + " vertices and " + dgraph.getGraphEdgeSet()
                    + " edges has been successfully generated.\n");
            // buttonSaveDGraph.setEnabled(true);
            if (dgraph.getWarnings().size() > 0) {
                jta.append("Warnings: \n ");
                for (int i = 0; i < dgraph.getWarnings().size(); i++) {
                    jta.append("    - " + dgraph.getWarnings().get(i) + ".\n ");
                }
            }
            buttonShowDGraph.setEnabled(true);
            readChannelTransactions.addAll(dgraph.getreadChannelNodes());
            writeChannelTransactions.addAll(dgraph.getwriteChannelNodes());
            ComboBoxModel<String> aModel = new DefaultComboBoxModel<String>(readChannelTransactions);
            ComboBoxModel<String> aModel1 = new DefaultComboBoxModel<String>(writeChannelTransactions);
            tasksDropDownCombo3.setModel(aModel);
            tasksDropDownCombo4.setModel(aModel1);
            if (pbar.getValue() == pbar.getMaximum()) {
                updateBar(0);
            }
            latencybutton.setEnabled(true);
            this.pack();
            this.revalidate();
            this.repaint();
        } catch (Exception e) {
            jta.append("An error has occurred.\n");
            jta.append(e.getMessage() + ".\n");
            // buttonSaveDGraph.setEnabled(false);
            buttonShowDGraph.setEnabled(false);
        }
    }

    private void initActions() {
        actions = new LatencyDetailedAnalysisActions[LatencyDetailedAnalysisActions.NB_ACTION];
        for (int i = 0; i < LatencyDetailedAnalysisActions.NB_ACTION; i++) {
            actions[i] = new LatencyDetailedAnalysisActions(i);
            actions[i].addActionListener(this);
            // actions[i].addKeyListener(this);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        // TraceManager.addDev("Command:" + command);
        if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SAVE_TRACE_PNG].getActionCommand())) {
            try {
                String filename = "";
                final String directory = saveDirName.getText().trim();
                String param = saveFileName.getText().trim();
                if (param.length() > 0) {
                    if (!directory.isEmpty()) {
                        if (!directory.endsWith(File.separator))
                            filename = directory + File.separator + param;
                        else
                            filename = directory + param;
                    }
                    dgraph.exportGraphAsImage(filename);
                    jta.append("Directed graph save in" + filename + ".png.\n");
                } else {
                    error("Wrong parameter: must be a file name");
                }
            } catch (ExportException e) {
                jta.append("An error has occurred.\n");
                jta.append(e.getMessage() + ".\n");
            } catch (IOException e) {
                jta.append("An error has occurred.\n");
                jta.append(e.getMessage() + ".\n");
            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SAVE_TRACE_GRAPHML].getActionCommand())) {
            try {
                String filename = "";
                final String directory = saveDirName.getText().trim();
                String param = saveFileName.getText().trim();
                if (param.length() > 0) {
                    if (!directory.isEmpty()) {
                        if (!directory.endsWith(File.separator))
                            filename = directory + File.separator + param;
                        else
                            filename = directory + param;
                    }
                    dgraph.exportGraph(filename);
                    jta.append("Directed graph saved in" + filename + ".graphml.\n");
                } else {
                    error("Wrong parameter: must be a file name");
                }
            } catch (ExportException e) {
                jta.append("An error has occurred.\n");
                jta.append(e.getMessage() + ".\n");
            } catch (IOException e) {
                jta.append("An error has occurred.\n");
                jta.append(e.getMessage() + ".\n");
            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH].getActionCommand())) {
            tc.setjFrameLDA(this);
            tc.start(12);
            tc.run();
            // tc.showgraphFrame();
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand())) {
            jta.setText("");
            dispose();
            setVisible(false);
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_LATENCY].getActionCommand())) {
            jta.append("The Latency Between: \n " + tasksDropDownCombo1.getSelectedItem() + " and \n" + tasksDropDownCombo2.getSelectedItem()
                    + " is under analysis.\n");
            latencybutton.setEnabled(false);
            this.pack();
            this.revalidate();
            this.repaint();
            if (taintFirstOp.isSelected()) {
                jta.append("Operator 1 is tainted.\n ");
            }
            if (considerRules.isSelected()) {
                jta.append("Rules are considered in the graph.\n ");
            }
            try {
                tc.getT().join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            transFile1 = parseFile(file);
            tc.setjFrameLDA(this);
            tc.start(13);
            tc.run();
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_Import_ANALYSIS].getActionCommand())) {
            if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) {
                fc2 = new JFileChooser(ConfigurationTTool.SystemCCodeDirectory);
            } else {
                fc2 = new JFileChooser();
            }
            FileNameExtensionFilter filter = new FileNameExtensionFilter("graphml files", "graphml");
            fc2.setFileFilter(filter);
            int returnVal = fc2.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc2.getSelectedFile();
            }
            FileReader ps = null;
            try {
                ps = new FileReader(file);
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            VertexProvider<String> vertexProvider = (label, attributes) -> {
                String cv = new String(label);
                cv.replaceAll("\\s+", "");
                // cv.replaceAll("(", "");
                // cv.replaceAll(")", "");
                return cv;
            };
            EdgeProvider<String, DefaultEdge> edgeProvider = (from, to, label, attributes) -> new DefaultEdge();
            GraphMLImporter<String, DefaultEdge> importer = new GraphMLImporter<String, DefaultEdge>(vertexProvider, edgeProvider);
            try {
                Graph<String, DefaultEdge> importedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
                ;
                importer.importGraph(importedGraph, ps);
            } catch (ImportException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_CHECK_PATH].getActionCommand())) {
            String task1 = tasksDropDownCombo1.getSelectedItem().toString();
            String task2 = tasksDropDownCombo2.getSelectedItem().toString();
            String message = dgraph.checkPath(task1, task2);
            jta.append(message + " :" + task1 + " and " + task2 + ".\n");
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_ADD_RULE].getActionCommand())) {
            String node1 = tasksDropDownCombo3.getSelectedItem().toString();
            String node2 = tasksDropDownCombo4.getSelectedItem().toString();
            String ruleDirection = tasksDropDownCombo5.getSelectedItem().toString();
            String message = dgraph.addRule(node1, node2, writeChannelTransactions, ruleDirection);
            jta.append(message + ".\n");
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_VIEW_RULE].getActionCommand())) {
            new JFrameListOfRules(dgraph);
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_LATENCY_PRECISE_ANALYSIS].getActionCommand())) {
            int row1 = 0;
            String noLatValue = null;
            int selectedIndex = resultTab.getSelectedIndex();
            if (selectedIndex == 0) {
                row1 = table11.getSelectedRow();
                if (row1 > -1) {
                    noLatValue = table11.getValueAt(row1, 4).toString();
                } else {
                    jta.append("Please select a row before precise analysis.\n");
                }
            } else if (selectedIndex == 1) {
                row1 = table12.getSelectedRow();
                if (row1 > -1) {
                    noLatValue = table12.getValueAt(row1, 4).toString();
                } else {
                    jta.append("Please select a row before precise analysis.\n");
                }
            }
            if (noLatValue.startsWith("Assumption Does Not Hold;")) {
                jta.append("Can not run latency when the one-to-one assumption does not hold.\n");
            } else {
                tc.setjFrameLDA(this);
                tc.setRow(row1);
                tc.start(11);
                tc.run();
                // tc.getT().join();
            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_ALL_OP].getActionCommand())) {
            if (!allop) {
                for (TMLTask tmltask : tmap1.getTMLModeling().getTasks()) {
                    allTasks.add(tmltask.getName());
                }
                ComboBoxModel<String> taskModel = new DefaultComboBoxModel<String>(allTasks);
                tasksDropDownCombo6.setModel(taskModel);
                tasksDropDownCombo6.addActionListener(actions[LatencyDetailedAnalysisActions.ACT_LOAD_ALL_OP]);
                ComboBoxModel<String> taskModel2 = new DefaultComboBoxModel<String>(allTasks);
                tasksDropDownCombo7.setModel(taskModel2);
                tasksDropDownCombo7.addActionListener(actions[LatencyDetailedAnalysisActions.ACT_LOAD_ALL_OP2]);
                ComboBoxModel<String> aModel1 = new DefaultComboBoxModel<String>(checkedTransactions);
                tasksDropDownCombo2.setModel(aModel1);
                tasksDropDownCombo1.setModel(getAlloperators(tasksDropDownCombo6.getItemAt(0)));
                tasksDropDownCombo2.setModel(getAlloperators(tasksDropDownCombo7.getItemAt(0)));
                // ComboBoxModel<String> aModel2 = new
                // DefaultComboBoxModel<String>(checkedTransactions);
                // tasksDropDownCombo1.setModel(aModel2);
                allop = true;
                showAllOp.setLabel("Show checkpoints operators");
                jta.append("Show latency checkpoints operators.\n");
                task2.setVisible(true);
                taskL.setVisible(true);
                tasksDropDownCombo6.setVisible(true);
                tasksDropDownCombo7.setVisible(true);
                this.pack();
                this.revalidate();
                this.repaint();
            } else {
                checkedTransactions = new Vector<String>();
                checkedT = new HashMap<String, Integer>();
                fillCheckedTrans(tmap1);
                ComboBoxModel<String> aModel = new DefaultComboBoxModel<String>(checkedTransactions);
                ComboBoxModel<String> aModel1 = new DefaultComboBoxModel<String>(checkedTransactions);
                tasksDropDownCombo1.setModel(aModel);
                tasksDropDownCombo2.setModel(aModel1);
                task2.setVisible(false);
                taskL.setVisible(false);
                tasksDropDownCombo6.setVisible(false);
                tasksDropDownCombo7.setVisible(false);
                this.pack();
                this.revalidate();
                this.repaint();
                allop = false;
                showAllOp.setLabel("Show all operators");
                jta.append("Show all operators.\n");
            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_LOAD_ALL_OP].getActionCommand())) {
            tasksDropDownCombo1.setModel(getAlloperators(tasksDropDownCombo6.getSelectedItem()));
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_LOAD_ALL_OP2].getActionCommand())) {
            tasksDropDownCombo2.setModel(getAlloperators(tasksDropDownCombo7.getSelectedItem()));
        }
    }

    private ComboBoxModel<String> getAlloperators(Object object) {
        TMLActivity activity;
        checkedTransactions = new Vector<String>();
        allTasks = new Vector<String>();
        checkedT = new HashMap<String, Integer>();
        for (TMLTask tmltask : tmap1.getTMLModeling().getTasks()) {
            int opCount = 0;
            if (tmltask.getName().equals(object)) {
                activity = tmltask.getActivityDiagram();
                TMLActivityDiagramPanel tadp = (TMLActivityDiagramPanel) (activity.getReferenceObject());
                List<TGComponent> list = tadp.getComponentList();
                // Iterator<TGComponent> iterator = list.listIterator();
                // TGComponent tgc;
                opCount = 0;
                for (TGComponent tgc : list) {
                    String compName = "";
                    if (tgc.isEnabled()) {
                        if (tgc instanceof CheckableLatency) {
                            compName = tmltask.getName() + ":" + tgc.getName();
                            compName = compName.replaceAll(" ", "");
                            if (tgc.getValue().contains("(")) {
                                compName = compName + ":" + tgc.getValue().split("\\(")[0];
                            } else {
                                if (tgc instanceof TMLADExecI) {
                                    compName = ((TMLADExecI) tgc).getDelayValue();
                                }
                            }
                            checkedT.put(compName + "__" + tgc.getDIPLOID(), tgc.getDIPLOID());
                        }
                    }
                }
            }
        }
        for (Entry<String, Integer> cT : checkedT.entrySet()) {
            String name = cT.getKey();
            int id = cT.getValue();
            if (!checkedTransactions.contains(name)) {
                if (checkedTransactions.size() > 0) {
                    Boolean inserted = false;
                    for (int j = 0; j < checkedTransactions.size(); j++) {
                        if (id < checkedT.get(checkedTransactions.get(j)) && !checkedTransactions.contains(name)) {
                            checkedTransactions.insertElementAt(name, j);
                            inserted = true;
                        }
                    }
                    if (!inserted) {
                        checkedTransactions.insertElementAt(name, checkedTransactions.size());
                    }
                } else {
                    checkedTransactions.add(name);
                }
            }
        }
        ComboBoxModel<String> aModel = new DefaultComboBoxModel<String>(checkedTransactions);
        return aModel;
    }

    protected void preciselatencyAnalysis(int row1) throws InterruptedException {
        tc.getT().join();
        Boolean taint = taintFirstOp.isSelected();
        int selectedIndex = resultTab.getSelectedIndex();
        int row = -1;
        if (selectedIndex == 0) {
            row = table11.getSelectedRow();
            if (row >= 0) {
                try {
                    new JFrameLatencyDetailedPopup(dgraph, row, true, taint, tc, true);
                } catch (Exception e) {
                    jta.append("An error has occurred.\n");
                    jta.append(e.getMessage() + ".\n");
                }
            } else {
                jta.append("Please select a row to analyze.\n");
            }
        } else if (selectedIndex == 1) {
            row = table12.getSelectedRow();
            if (row >= 0) {
                try {
                    new JFrameLatencyDetailedPopup(dgraph, row, false, taint, tc, true);
                } catch (Exception e) {
                    jta.append("An error has occurred.\n");
                    jta.append(e.getMessage() + ".\n");
                }
            } else {
                jta.append("Please select a row to analyze.\n");
            }
        }
    }

    public Vector<SimulationTransaction> parseFile(File file2) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            error(e.getMessage());
        } catch (SAXException e) {
            error(e.getMessage());
        }
        SimulationTransactionParser handler = new SimulationTransactionParser();
        try {
            saxParser.parse(file2, handler);
        } catch (SAXException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error(e.getMessage());
        }
        return transFile1 = handler.getStList();
    }

    protected void latencyDetailedAnalysis() {
        try {
            preciseAnalysisbutton.setEnabled(false);
            String task1 = tasksDropDownCombo1.getSelectedItem().toString();
            String task2 = tasksDropDownCombo2.getSelectedItem().toString();
            Object[][] tableData = null;
            Boolean taint = taintFirstOp.isSelected();
            Boolean considerAddedRules = considerRules.isSelected();
            tableData = dgraph.latencyDetailedAnalysis(task1, task2, transFile1, taint, considerAddedRules);
            table11.removeAll();
            DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table11 = new JTable(tableModel);
            table11.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table11.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    preciseAnalysisbutton.setEnabled(true);
                }
            });
            Object[][] tableData2 = null;
            if (taint) {
                tableData2 = dgraph.latencyMinMaxAnalysisTaintedData(task1, task2, transFile1);
                table12.removeAll();
                DefaultTableModel tableModel2 = new DefaultTableModel(tableData2, columnMinMaxNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                table12 = new JTable(tableModel2);
                table12.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table12.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        preciseAnalysisbutton.setEnabled(true);
                    }
                });
            } else {
                tableData2 = dgraph.latencyMinMaxAnalysis(task1, task2, transFile1);
                table12.removeAll();
                DefaultTableModel tableModel2 = new DefaultTableModel(tableData2, columnMinMaxNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                table12 = new JTable(tableModel2);
                // table12 = new JTable(tableData2, columnMinMaxNames);
                table12.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table12.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        preciseAnalysisbutton.setEnabled(true);
                    }
                });
            }
            table12.repaint();
            table12.revalidate();
            scrollPane12.setViewportView(table12);
            scrollPane12.revalidate();
            scrollPane12.repaint();
            // scrollPane12.setVisible(true);
            table11.repaint();
            table11.revalidate();
            scrollPane11.setViewportView(table11);
            scrollPane11.revalidate();
            scrollPane11.repaint();
            // scrollPane11.setVisible(true);
            jta.append("Latency has been computed...Please refer to the tables in the Latency Analysis section for the results.\n");
            latencybutton.setEnabled(true);
            this.pack();
            this.setVisible(true);
        } catch (Exception e) {
            jta.append("An error has occurred.\n");
            jta.append(e.getMessage() + ".\n");
            latencybutton.setEnabled(true);
        }
    }

    public void error(String error) {
        jta.append("Error: " + error + ".\n");
    }

    protected void showgraphFrame() {
        try {
            dgraph.showGraph(dgraph);
            jta.append("Refer to the generatd dialog to view the graph.\n");
        } catch (Exception e) {
            jta.append("An error has occurred.\n");
            jta.append(e.getMessage() + ".\n");
        }
    }

    public void fillCheckedTrans(TMLMapping<TGComponent> tmap) {
        if (tmap == null) {
            return;
        }
        for (TGComponent tgc : tmap.getTMLModeling().getCheckedComps().keySet()) {
            String compName = tmap.getTMLModeling().getCheckedComps().get(tgc);
            // TraceManager.addDev(compName + "__" + tgc.getDIPLOID());
            checkedT.put(compName + "__" + tgc.getDIPLOID(), tgc.getDIPLOID());
        }
        for (Entry<String, Integer> cT : checkedT.entrySet()) {
            String name = cT.getKey();
            int id = cT.getValue();
            if (!checkedTransactions.contains(name)) {
                if (checkedTransactions.size() > 0) {
                    Boolean inserted = false;
                    for (int j = 0; j < checkedTransactions.size(); j++) {
                        if (id < checkedT.get(checkedTransactions.get(j)) && !checkedTransactions.contains(name)) {
                            checkedTransactions.insertElementAt(name, j);
                            inserted = true;
                        }
                    }
                    if (!inserted) {
                        checkedTransactions.insertElementAt(name, checkedTransactions.size());
                    }
                } else {
                    checkedTransactions.add(name);
                }
            }
        }
    }

    public Vector<String> getCheckedTransactions() {
        return checkedTransactions;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    public Thread getT() {
        return tc.getT();
    }

    public LatencyDetailedAnalysisActions[] getActions() {
        return actions;
    }

    public void setActions(LatencyDetailedAnalysisActions[] actions) {
        this.actions = actions;
    }

    public LatencyAnalysisParallelAlgorithms getTc() {
        return tc;
    }

    public HashMap<String, Integer> getCheckedT() {
        return checkedT;
    }

    public TMLMapping<TGComponent> getTmap1() {
        return tmap1;
    }
}