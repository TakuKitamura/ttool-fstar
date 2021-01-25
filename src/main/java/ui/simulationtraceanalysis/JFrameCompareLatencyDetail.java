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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
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
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import common.ConfigurationTTool;
import myutil.GraphicLib;
import myutil.ScrolledJTextArea;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.ColorManager;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.interactivesimulation.SimulationTransactionParser;

public class JFrameCompareLatencyDetail extends JFrame implements ActionListener {
    private JButton buttonClose, buttonShowDGraph1, buttonShowDGraph2, buttonDetailedAnalysis, buttonCompareInDetails;
    private LatencyDetailedAnalysisActions[] actions;
    private DirectedGraphTranslator dgraph1, dgraph2;
    private JPanel loadxml, commandTab, jp05, graphAnalysisResult, jp03, jp04, loadmodel, progressBarpanel;
    private JTextArea jta;
    private JScrollPane jsp;
    private JTabbedPane resultTab;
    private JComboBox<String> tasksDropDownCombo1 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo2 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo3 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo4 = new JComboBox<String>();
    private Vector<String> checkedTransactionsFile1 = new Vector<String>();
    private Vector<String> checkedTransactionsFile2 = new Vector<String>();
    private Vector<String> checkedTransactionsFile = new Vector<String>();
    private static JTable table11, table12, table21, table22;
    private String[] columnNames = new String[5];
    private String[] columnMinMaxNames = new String[5];
    private Object[][] dataDetailedByTask;
    private Object[][] dataDetailedMinMax;
    private JTextField secondFile = new JTextField();
    private JButton browse;
    private File file1, file2;
    private JFileChooser fc, fc2;
    private boolean visible;
    // private Thread t, t1;
    private Object[][] tableData2MinMax, tableData1MinMax, tableData2, tableData = null;
    private JScrollPane scrollPane11, scrollPane12, scrollPane21, scrollPane22;
    private MainGUI mainGUI;
    private latencyDetailedAnalysisMain latencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis jFrameLatencyDetailedAnalysis;
    private JFrameCompareLatencyDetail jFrameCompareLatencyDetail;
    private JProgressBar pbar;
    private JLabel pBarLabel;
    private TMLMapping<TGComponent> map;
    private List<TMLComponentDesignPanel> cpanels;
    private DirectedGraphTranslator dgraph;
    private LatencyAnalysisParallelAlgorithms tc;
    private JCheckBox taintFirstOp, considerRules;
    private static final String START_TIME_COL_NAME = "Start Time ";
    private static final String END_TIME_COL_NAME = "End Time ";
    private static final String LATENCY_COLNAME = "Latency ";
    private static final String OP_A_COLNAME = "OPERATOR A";
    private static final String OP_B_COLNAME = "OPERATOR B ";

    // private DirectedGraphTranslator dgraph;
    public JFrameCompareLatencyDetail(latencyDetailedAnalysisMain latencyDetailedAnaly, MainGUI mgui, final Vector<String> checkedTransactionsFile1,
            TMLMapping<TGComponent> map1, List<TMLComponentDesignPanel> cpanels1, final SimulationTrace selectedST1, boolean b,
            LatencyAnalysisParallelAlgorithms tc1) throws InterruptedException {
        super("Latency Comparision");
        this.setVisible(b);
        tc = tc1;
        mainGUI = mgui;
        map = map1;
        cpanels = cpanels1;
        latencyDetailedAnalysisMain = latencyDetailedAnaly;
        // dgraph2 = graph2;
        file1 = new File(selectedST1.getFullPath());
        // file2 = new File(selectedST2.getFullPath());
        visible = b;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initActions();
        GridBagLayout gridbagmain = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        Container framePanel = getContentPane();
        framePanel.setLayout(gridbagmain);
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 0;
        // mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        buttonShowDGraph1 = new JButton(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH_FILE_1]);
        buttonDetailedAnalysis = new JButton(actions[LatencyDetailedAnalysisActions.ACT_LATENCY]);
        buttonShowDGraph2 = new JButton(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH_FILE_2]);
        buttonClose = new JButton(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL]);
        buttonCompareInDetails = new JButton(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_IN_DETAILS]);
        JPanel jp = new JPanel();
        jp.add(buttonClose);
        jp.add(buttonShowDGraph1);
        jp.add(buttonShowDGraph2);
        jp.add(buttonDetailedAnalysis);
        jp.add(buttonCompareInDetails);
        buttonShowDGraph1.setEnabled(false);
        buttonShowDGraph2.setEnabled(false);
        buttonDetailedAnalysis.setEnabled(false);
        buttonCompareInDetails.setEnabled(false);
        framePanel.add(jp, mainConstraint);
        mainConstraint.gridheight = 1;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 1;
        mainConstraint.gridwidth = 1; // end row
        mainConstraint.fill = GridBagConstraints.BOTH;
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        loadmodel = new JPanel(new BorderLayout());
        loadmodel.setLayout(gridbag02);
        framePanel.add(loadmodel, mainConstraint);
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = 1;
        c02.gridx = 0;
        c02.gridy = 0;
        c02.anchor = GridBagConstraints.WEST;
        JLabel xmlLabel = new JLabel("Second Simulation Traces ", JLabel.LEFT);
        loadmodel.add(xmlLabel, c02);
        secondFile.setMinimumSize(new Dimension(300, 30));
        secondFile.setEditable(false);
        secondFile.setText("file 2 name");
        secondFile.setBorder(new LineBorder(Color.BLACK));
        c02.gridx = 1;
        c02.gridy = 0;
        loadmodel.add(secondFile, c02);
        browse = new JButton("Browse");
        browse.addActionListener(this);
        browse.setEnabled(false);
        c02.gridx = 2;
        c02.gridy = 0;
        loadmodel.add(browse, c02);
        loadmodel.setBorder(new javax.swing.border.TitledBorder("Simulation Traces File"));
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        loadxml = new JPanel(new BorderLayout());
        loadxml.setLayout(gridbag01);
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 2;
        mainConstraint.gridwidth = 1; // end row
        mainConstraint.fill = GridBagConstraints.BOTH;
        loadxml.setBorder(new javax.swing.border.TitledBorder("Chose Latency Operators"));
        framePanel.add(loadxml, mainConstraint);
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 1;
        JLabel tasksLabel1 = new JLabel("Operators of Simulation Traces File 1  ", JLabel.LEFT);
        loadxml.add(tasksLabel1, c01);
        c01.gridx = 0;
        c01.gridy = 2;
        JLabel tasksLabel2 = new JLabel("Operators of Simulation Traces File 2  ", JLabel.LEFT);
        loadxml.add(tasksLabel2, c01);
        c01.gridx = 1;
        c01.gridy = 1;
        tasksDropDownCombo1 = new JComboBox<String>(checkedTransactionsFile1);
        loadxml.add(tasksDropDownCombo1, c01);
        c01.gridx = 2;
        c01.gridy = 1;
        tasksDropDownCombo2 = new JComboBox<String>(checkedTransactionsFile1);
        loadxml.add(tasksDropDownCombo2, c01);
        c01.gridx = 1;
        c01.gridy = 3;
        tasksDropDownCombo3 = new JComboBox<String>(checkedTransactionsFile2);
        loadxml.add(tasksDropDownCombo3, c01);
        c01.gridx = 2;
        c01.gridy = 3;
        tasksDropDownCombo4 = new JComboBox<String>(checkedTransactionsFile2);
        loadxml.add(tasksDropDownCombo4, c01);
        gridbag01 = new GridBagLayout();
        c01 = new GridBagConstraints();
        progressBarpanel = new JPanel(gridbag01);
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        // c01.fill = GridBagConstraints.BOTH;
        pBarLabel = new JLabel("Progress of Graph Generation", JLabel.LEFT);
        progressBarpanel.add(pBarLabel, c01);
        c01.gridx = 1;
        c01.gridy = 0;
        pbar = new JProgressBar();
        pbar.setForeground(Color.GREEN);
        progressBarpanel.add(pbar, c01);
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 5;
        mainConstraint.ipady = 40;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        framePanel.add(progressBarpanel, mainConstraint);
        graphAnalysisResult = new JPanel(new BorderLayout());
        graphAnalysisResult.setBorder(new javax.swing.border.TitledBorder("Latency Analysis "));
        mainConstraint.weighty = 1.0;
        mainConstraint.weightx = 1.0;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 4;
        mainConstraint.ipady = 200;
        mainConstraint.gridwidth = 1; // end row
        // mainConstraint.gridwidth = GridBagConstraints.REMAINDER; // end row
        mainConstraint.fill = GridBagConstraints.BOTH;
        framePanel.add(graphAnalysisResult, mainConstraint);
        columnNames[0] = OP_A_COLNAME;
        columnNames[1] = START_TIME_COL_NAME;
        columnNames[2] = OP_B_COLNAME;
        columnNames[3] = END_TIME_COL_NAME;
        columnNames[4] = LATENCY_COLNAME;
        // columnNames[5] = "Related Tasks Details ";
        // columnNames[6] = "Total time- Related Tasks ";
        dataDetailedByTask = new Object[0][0];
        jp03 = new JPanel(new BorderLayout());
        jp03.setLayout(gridbag01);
        DefaultTableModel tableModel1 = new DefaultTableModel(dataDetailedByTask, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table11 = new JTable(tableModel1);
        // table11.setBackground(Color.red);
        scrollPane11 = new JScrollPane(table11, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // scrollPane11.setBackground(Color.red);
        scrollPane11.setVisible(b);
        c01.gridx = 0;
        c01.gridy = 0;
        c01.fill = GridBagConstraints.BOTH;
        jp03.add(scrollPane11, c01);
        DefaultTableModel tableModel2 = new DefaultTableModel(dataDetailedByTask, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table12 = new JTable(tableModel2);
        // table12.setBackground(Color.blue);
        scrollPane12 = new JScrollPane(table12, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // scrollPane11.setBackground(Color.blue);
        scrollPane12.setVisible(b);
        c01.gridx = 0;
        c01.gridy = 1;
        c01.fill = GridBagConstraints.BOTH;
        jp03.add(scrollPane12, c01);
        jp04 = new JPanel(new BorderLayout());
        jp04.setLayout(gridbag01);
        columnMinMaxNames[0] = OP_A_COLNAME;
        columnMinMaxNames[1] = START_TIME_COL_NAME;
        columnMinMaxNames[2] = OP_B_COLNAME;
        columnMinMaxNames[3] = END_TIME_COL_NAME;
        columnMinMaxNames[4] = LATENCY_COLNAME;
        dataDetailedMinMax = new Object[0][0];
        DefaultTableModel tableModel3 = new DefaultTableModel(dataDetailedMinMax, columnMinMaxNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table21 = new JTable(tableModel3);
        scrollPane21 = new JScrollPane(table21, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane21.setVisible(b);
        c01.gridx = 0;
        c01.gridy = 0;
        c01.fill = GridBagConstraints.BOTH;
        jp04.add(scrollPane21, c01);
        DefaultTableModel tableModel4 = new DefaultTableModel(dataDetailedMinMax, columnMinMaxNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table22 = new JTable(tableModel4);
        // table12.setBackground(Color.blue);
        scrollPane22 = new JScrollPane(table22, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane22.setVisible(b);
        c01.gridx = 0;
        c01.gridy = 1;
        c01.fill = GridBagConstraints.BOTH;
        jp04.add(scrollPane22, c01);
        resultTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        resultTab.addTab("Latency Values", null, jp03, "Latency Detailed by Tasks ");
        resultTab.addTab("Min/Max Latency", null, jp04, "Min and Max Latency");
        graphAnalysisResult.add(resultTab, BorderLayout.CENTER);
        jp05 = new JPanel(new BorderLayout());
        // mainpanel.add(split, BorderLayout.SOUTH);
        mainConstraint.weighty = 00;
        mainConstraint.ipady = 200;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 6;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        framePanel.add(jp05, mainConstraint);
        // Ouput textArea
        jta = new ScrolledJTextArea();
        jta.setBackground(ColorManager.InteractiveSimulationJTABackground);
        jta.setForeground(ColorManager.InteractiveSimulationJTAForeground);
        jta.setMinimumSize(new Dimension(800, 400));
        jta.setRows(15);
        // jta.setMaximumSize(new Dimension(800, 500));
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Generating the corresponding directed graph.\n Please wait...\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));
        jsp.setMaximumSize(new Dimension(800, 500));
        jp05.setMaximumSize(new Dimension(800, 500));
        jp05.add(jsp, BorderLayout.CENTER);
        this.pack();
        this.setVisible(b);
        tc.setCld(this);
        tc.setMap(map);
        tc.start(10);
        tc.run();
    }

    protected void generateDirectedGraph1(TMLMapping<TGComponent> map, List<TMLComponentDesignPanel> cpanels) {
        try {
            dgraph = new DirectedGraphTranslator(jFrameLatencyDetailedAnalysis, this, map, cpanels, 1);
            dgraph1 = dgraph;
            jta.append("A directed graph with " + dgraph.getGraphsize() + " vertices and " + dgraph.getGraphEdgeSet()
                    + " edges has been successfully generated.\n");
            // buttonSaveDGraph.setEnabled(true);
            if (dgraph.getWarnings().size() > 0) {
                jta.append("Warnings: \n ");
                for (int i = 0; i < dgraph.getWarnings().size(); i++) {
                    jta.append("    - " + dgraph.getWarnings().get(i) + ".\n ");
                }
            }
            buttonShowDGraph1.setEnabled(true);
            if (pbar.getValue() == pbar.getMaximum()) {
                updateBar(0);
            }
            jta.append("Browse the second simulation trace to generate the second graph.\n");
            browse.setEnabled(true);
        } catch (Exception e) {
            jta.append("An error has occurred.\n");
            jta.append(e.getMessage() + ".\n");
            // buttonSaveDGraph.setEnabled(false);
            buttonShowDGraph1.setEnabled(false);
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

    protected void generateDirectedGraph2(TMLMapping<TGComponent> map, List<TMLComponentDesignPanel> cpanels) {
        try {
            dgraph = null;
            dgraph = tc.getDgraph();
            dgraph2 = dgraph;
            String[] checkTransactionStrTable = new String[checkedTransactionsFile2.size()];
            int idx = 0;
            for (String str : checkedTransactionsFile2) {
                checkTransactionStrTable[idx] = str;
                idx++;
            }
            ComboBoxModel<String> aModel = new DefaultComboBoxModel<String>(checkTransactionStrTable);
            ComboBoxModel<String> aModel1 = new DefaultComboBoxModel<String>(checkTransactionStrTable);
            tasksDropDownCombo3.setModel(aModel);
            tasksDropDownCombo4.setModel(aModel1);
            if (dgraph.getWarnings().size() > 0) {
                jta.append("Warnings:\n ");
                for (int i = 0; i < dgraph.getWarnings().size(); i++) {
                    jta.append("    - " + dgraph.getWarnings().get(i) + ".\n ");
                }
            }
            buttonShowDGraph2.setEnabled(true);
            buttonDetailedAnalysis.setEnabled(true);
            buttonCompareInDetails.setEnabled(true);
            if (pbar.getValue() == pbar.getMaximum()) {
                updateBar(0);
            }
            this.pack();
            this.revalidate();
            this.repaint();
            jta.append("A directed graph with " + dgraph.getGraphsize() + " vertices and " + dgraph.getGraphEdgeSet()
                    + " edges has been successfully generated.\n");
        } catch (Exception e) {
            jta.append("An error has occurred.\n");
            jta.append(e.getMessage() + ".\n");
            // buttonSaveDGraph.setEnabled(false);
            buttonShowDGraph2.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        // TraceManager.addDev("Command:" + command);
        if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH_FILE_1].getActionCommand())) {
            tc.setDgraph(dgraph1);
            tc.start(14);
            tc.run();
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH_FILE_2].getActionCommand())) {
            tc.setDgraph2(dgraph2);
            tc.start(17);
            tc.run();
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand())) {
            // jta.setText("");
            dispose();
            setVisible(false);
        } else if (evt.getSource() == browse) {
            try {
                pBarLabel.setText("Progress of Graph 2 Generation");
                updateBar(0);
                if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) {
                    fc = new JFileChooser(ConfigurationTTool.SystemCCodeDirectory);
                } else {
                    fc = new JFileChooser();
                }
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
                fc.setFileFilter(filter);
                int returnVal = fc.showOpenDialog(mainGUI.frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File filefc = fc.getSelectedFile();
                    latencyDetailedAnalysisMain.setCheckedTransactionsFile(new Vector<String>());
                    SimulationTrace STfile2 = new SimulationTrace(filefc.getName(), 6, filefc.getAbsolutePath());
                    secondFile.setText(filefc.getAbsolutePath());
                    if (STfile2 instanceof SimulationTrace) {
                        file2 = new File(STfile2.getFullPath());
                        try {
                            latencyDetailedAnalysisMain.latencyDetailedAnalysisForXML(mainGUI, STfile2, false, true, 2);
                        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        checkedTransactionsFile2 = latencyDetailedAnalysisMain.getCheckedTransactionsFile();
                        map = latencyDetailedAnalysisMain.getMap1();
                        cpanels = latencyDetailedAnalysisMain.getCpanels1();
                        this.toFront();
                        this.requestFocus();
                        this.pack();
                        this.revalidate();
                        this.repaint();
                        tc.setCld(this);
                        tc.setMap(map);
                        // tc.setCld(jFrameCompareLatencyDetail);
                        tc.setjFrameLDA(jFrameLatencyDetailedAnalysis);
                        tc.start(15);
                        tc.run();
                    }
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
                TraceManager.addDev("Error: " + e1.getMessage());
            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_LATENCY].getActionCommand())) {
            jta.append("Simulation Trace 1: the latency between: \n " + tasksDropDownCombo1.getSelectedItem() + " and \n"
                    + tasksDropDownCombo2.getSelectedItem() + " is under analysis.\n");
            jta.append("Simulation Trace 2: the latency between: \n " + tasksDropDownCombo3.getSelectedItem() + " and \n"
                    + tasksDropDownCombo4.getSelectedItem() + "is under analysis.\n");
            String task1 = tasksDropDownCombo1.getSelectedItem().toString();
            String task2 = tasksDropDownCombo2.getSelectedItem().toString();
            String task3 = tasksDropDownCombo3.getSelectedItem().toString();
            String task4 = tasksDropDownCombo4.getSelectedItem().toString();
            // Boolean taint = taintFirstOp.isSelected();
            // Boolean considerAddedRules = considerRules.isSelected();
            Vector<SimulationTransaction> transFile1, transFile2;
            transFile1 = parseFile(file1);
            transFile2 = parseFile(file2);
            tc.latencyDetailedAnalysis(this, task1, task2, task3, task4, transFile1, transFile2);
            tc.start(18);
            tc.run();
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_IN_DETAILS].getActionCommand())) {
            jta.append("The latency between: \n " + tasksDropDownCombo1.getSelectedItem() + " and \n" + tasksDropDownCombo2.getSelectedItem()
                    + " is under analysis.\n");
            int selectedIndex = resultTab.getSelectedIndex();
            if ((table11.getSelectedRowCount() > 0 && table12.getSelectedRowCount() > 0 && selectedIndex == 0)
                    || (table21.getSelectedRowCount() > 0 && table22.getSelectedRowCount() > 0 && selectedIndex == 1)) {
                int row1 = table11.getSelectedRow();
                int row2 = table12.getSelectedRow();
                int row3 = table21.getSelectedRow();
                int row4 = table22.getSelectedRow();
                // int selectedIndex = resultTab.getSelectedIndex();
                tc.compareLatencyInDetails(this, row1, row2, row3, row4, selectedIndex);
                tc.start(19);
                tc.run();
            } else {
                jta.append("Please select a row from each table to proceed.\n");
            }
        }
    }

    private void showgraphFrame(DirectedGraphTranslator dgraph) {
        try {
            dgraph.showGraph(dgraph);
            // jta.append("Refer to the generatd dialog to view the graph.\n");
        } catch (Exception e) {
            // jta.append("An Error has occurred \n");
            // jta.append(e.getMessage() + "\n");
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
        return handler.getStList();
    }

    public void latencyDetailedAnalysis(String task1, String task2, String task3, String task4, Vector<SimulationTransaction> transFile1,
            Vector<SimulationTransaction> transFile2, boolean b, boolean taint, boolean considerAddedRules) {
        tableData = dgraph1.latencyDetailedAnalysis(task1, task2, transFile1, taint, considerAddedRules);
//        DefaultTableModel model = new DefaultTableModel();
        table11.removeAll();
        DefaultTableModel tableModel1 = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table11 = new JTable(tableModel1);
        table11.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableData2 = dgraph2.latencyDetailedAnalysis(task3, task4, transFile2, taint, considerAddedRules);
//        DefaultTableModel model2 = new DefaultTableModel();
        table12.removeAll();
        DefaultTableModel tableModel2 = new DefaultTableModel(tableData2, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table12 = new JTable(tableModel2);
        table12.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableData1MinMax = dgraph1.latencyMinMaxAnalysis(task1, task2, transFile1);
//      DefaultTableModel model2 = new DefaultTableModel();
        table21.removeAll();
        DefaultTableModel tableModel3 = new DefaultTableModel(tableData1MinMax, columnMinMaxNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table21 = new JTable(tableModel3);
        table21.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableData2MinMax = dgraph2.latencyMinMaxAnalysis(task3, task4, transFile2);
//    DefaultTableModel model2 = new DefaultTableModel();
        table22.removeAll();
        DefaultTableModel tableModel4 = new DefaultTableModel(tableData2MinMax, columnMinMaxNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table22 = new JTable(tableModel4);
        table22.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table22.repaint();
        table22.revalidate();
        scrollPane22.setViewportView(table22);
        scrollPane22.revalidate();
        scrollPane22.repaint();
        table21.repaint();
        table21.revalidate();
        scrollPane21.setViewportView(table21);
        scrollPane21.revalidate();
        scrollPane21.repaint();
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
        this.pack();
        this.setVisible(b);
        // jta.append("Message: " + tableData.length + "\n");
    }

    protected void compareLatencyInDetails(int row1, int row2, int row3, int row4, int selectedIndex) throws InterruptedException {
        if (selectedIndex == 0) {
            jta.append("The latency for row : " + row1 + ": and row: " + row2 + "is under analysis.\n");
            new JFrameLatencyComparedDetailedPopup(dgraph1, dgraph2, row1, row2, true, tc);
        } else if (selectedIndex == 1) {
            jta.append("The latency for row : " + row3 + ": and row: " + row4 + "is under analysis.\n");
            new JFrameLatencyComparedDetailedPopup(dgraph1, dgraph2, row3, row4, false, tc);
        } else {
            error("Select a panel");
        }
    }

    public void error(String error) {
        jta.append("error: " + error + ".\n");
    }

    public Object[][] getTableData2MinMax() {
        return tableData2MinMax;
    }

    public Object[][] getTableData1MinMax() {
        return tableData1MinMax;
    }

    public Object[][] getTableData2() {
        return tableData2;
    }

    public Object[][] getTableData() {
        return tableData;
    }

    public DirectedGraphTranslator getDgraph2() {
        return dgraph2;
    }

    public void setDgraph2(DirectedGraphTranslator dgraph2) {
        this.dgraph2 = dgraph2;
    }

    public void updateBar(int newValue) {
        pbar.setValue(newValue);
    }

    public JProgressBar getPbar() {
        return pbar;
    }

    public void setPbar(JProgressBar pbar) {
        this.pbar = pbar;
    }

    public DirectedGraphTranslator getDgraph() {
        return dgraph;
    }

    public void setDgraph(DirectedGraphTranslator dgraph) {
        this.dgraph = dgraph;
    }
    // public Thread getT() {
    // return t;
    // }
    // public void setT(Thread t) {
    // this.t = t;
    // }
}
