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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import common.ConfigurationTTool;
import myutil.GraphicLib;
import myutil.ScrolledJTextArea;
import tmltranslator.TMLMapping;
import ui.ColorManager;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.interactivesimulation.SimulationTransactionParser;

public class JFrameCompareLatencyDetail extends JFrame implements ActionListener {

    protected JButton buttonClose, buttonShowDGraph1, buttonShowDGraph2, buttonDetailedAnalysis, buttonCompareInDetails;
    public LatencyDetailedAnalysisActions[] actions;
    private DirectedGraphTranslator dgraph1, dgraph2;

    private JPanel loadxml, commandTab, jp05, graphAnalysisResult, jp03, jp04, loadmodel, progressBarpanel;
    protected JTextArea jta;
    protected JScrollPane jsp;
    private JTabbedPane resultTab;

    private JComboBox<String> tasksDropDownCombo1 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo2 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo3 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo4 = new JComboBox<String>();
    public Vector<String> checkedTransactionsFile1 = new Vector<String>();
    public Vector<String> checkedTransactionsFile2 = new Vector<String>();
    private Vector<String> checkedTransactionsFile = new Vector<String>();

    public static JTable table11, table12, table21, table22;
    private String[] columnNames = new String[5];
    private String[] columnMinMaxNames = new String[5];
    private Object[][] dataDetailedByTask;
    private Object[][] dataDetailedMinMax;
    private JTextField secondFile = new JTextField();
    private JButton browse;

    private File file1, file2;
    private JFileChooser fc, fc2;
    private boolean visible;
    public Thread t, t1;

    private Object[][] tableData2MinMax, tableData1MinMax, tableData2, tableData = null;

    private JScrollPane scrollPane11, scrollPane12, scrollPane21, scrollPane22;

    private MainGUI mainGUI;
    private latencyDetailedAnalysisMain latencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis jFrameLatencyDetailedAnalysis;
    private JFrameCompareLatencyDetail jFrameCompareLatencyDetail;

    JProgressBar pbar;
    JLabel pBarLabel;
    TMLMapping<TGComponent> map;
    List<TMLComponentDesignPanel> cpanels;
    public DirectedGraphTranslator dgraph;

    JCheckBox taintFirstOp, considerRules;

    // private DirectedGraphTranslator dgraph;
    public JFrameCompareLatencyDetail(latencyDetailedAnalysisMain latencyDetailedAnaly, MainGUI mgui, final Vector<String> checkedTransactionsFile1,
            TMLMapping<TGComponent> map1, List<TMLComponentDesignPanel> cpanels1, final SimulationTrace selectedST1, boolean b) {

        super("Latency Detailed Comparision");

        this.setVisible(b);

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
        // mainConstraint.weighty = 0.5;
        // mainConstraint.weightx = 0.5;
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

        JLabel xmlLabel = new JLabel("Secound Simulation Traces ", JLabel.LEFT);
        loadmodel.add(xmlLabel, c02);

        secondFile.setMinimumSize(new Dimension(300, 30));
        secondFile.setEditable(false);
        secondFile.setText("file 2 name");
        secondFile.setBorder(new LineBorder(Color.BLACK));

        c02.gridx = 1;
        c02.gridy = 0;
        c02.weightx = 1;
        c02.weighty = 1;
        loadmodel.add(secondFile, c02);

        browse = new JButton("Browse");
        browse.addActionListener(this);
        browse.setEnabled(false);
        c02.gridx = 2;
        c02.gridy = 0;
        c02.weightx = 1;
        c02.weighty = 1;
        loadmodel.add(browse, c02);
        loadmodel.setBorder(new javax.swing.border.TitledBorder("Simulation Traces File"));

        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        loadxml = new JPanel(new BorderLayout());

        loadxml.setLayout(gridbag01);

        mainConstraint.gridheight = 1;
        // mainConstraint.weighty = 0.5;
        // mainConstraint.weightx = 0.5;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 2;
        mainConstraint.gridwidth = 1; // end row
        mainConstraint.fill = GridBagConstraints.BOTH;
        loadxml.setBorder(new javax.swing.border.TitledBorder("chose Latency Operators"));
        framePanel.add(loadxml, mainConstraint);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 1;

        JLabel tasksLabel1 = new JLabel("Operators of Simulation Traces File 1  ", JLabel.LEFT);
        loadxml.add(tasksLabel1, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 2;

        JLabel tasksLabel2 = new JLabel("Operators of Simulation Traces File 2  ", JLabel.LEFT);
        loadxml.add(tasksLabel2, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 1;
        tasksDropDownCombo1 = new JComboBox<String>(checkedTransactionsFile1);
        loadxml.add(tasksDropDownCombo1, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 2;
        c01.gridy = 1;

        tasksDropDownCombo2 = new JComboBox<String>(checkedTransactionsFile1);
        loadxml.add(tasksDropDownCombo2, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 3;
        tasksDropDownCombo3 = new JComboBox<String>(checkedTransactionsFile2);
        loadxml.add(tasksDropDownCombo3, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
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

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 0;

        pbar = new JProgressBar();
        pbar.setForeground(Color.GREEN);

        progressBarpanel.add(pbar, c01);

        mainConstraint.gridheight = 1;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 5;
        mainConstraint.ipady = 40;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;

        framePanel.add(progressBarpanel, mainConstraint);

        graphAnalysisResult = new JPanel(new BorderLayout());
        graphAnalysisResult.setBorder(new javax.swing.border.TitledBorder("Latency Detailed Analysis "));
        mainConstraint.gridheight = 1;
        // .weighty =0.5;
        // mainConstraint.weightx = 0.5;
        mainConstraint.weighty = 1.0;
        mainConstraint.weightx = 1.0;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 4;
        mainConstraint.ipady = 200;
        mainConstraint.gridwidth = 1; // end row
        // mainConstraint.gridwidth = GridBagConstraints.REMAINDER; // end row
        mainConstraint.fill = GridBagConstraints.BOTH;

        framePanel.add(graphAnalysisResult, mainConstraint);

        columnNames[0] = "OPERATOR A";
        columnNames[1] = "Start Time ";
        columnNames[2] = "OPERATOR B";
        columnNames[3] = "End Time ";
        columnNames[4] = "Delay ";

        // columnNames[5] = "Related Tasks Details ";
        // columnNames[6] = "Total time- Related Tasks ";

        dataDetailedByTask = new Object[0][0];

        jp03 = new JPanel(new BorderLayout());
        jp03.setLayout(gridbag01);

        table11 = new JTable(dataDetailedByTask, columnNames);
        // table11.setBackground(Color.red);
        scrollPane11 = new JScrollPane(table11, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // scrollPane11.setBackground(Color.red);
        scrollPane11.setVisible(b);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        c01.fill = GridBagConstraints.BOTH;

        jp03.add(scrollPane11, c01);

        table12 = new JTable(dataDetailedByTask, columnNames);
        // table12.setBackground(Color.blue);
        scrollPane12 = new JScrollPane(table12, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // scrollPane11.setBackground(Color.blue);
        scrollPane12.setVisible(b);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 1;
        c01.fill = GridBagConstraints.BOTH;

        jp03.add(scrollPane12, c01);

        jp04 = new JPanel(new BorderLayout());

        jp04.setLayout(gridbag01);

        columnMinMaxNames[0] = "OPERATOR A";
        columnMinMaxNames[1] = "Start Time ";
        columnMinMaxNames[2] = "OPERATOR B";
        columnMinMaxNames[3] = "End Time ";
        columnMinMaxNames[4] = "Delay ";

        dataDetailedMinMax = new Object[0][0];

        table21 = new JTable(dataDetailedMinMax, columnMinMaxNames);
        scrollPane21 = new JScrollPane(table21, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane21.setVisible(b);
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        c01.fill = GridBagConstraints.BOTH;

        jp04.add(scrollPane21, c01);

        table22 = new JTable(dataDetailedMinMax, columnMinMaxNames);
        // table12.setBackground(Color.blue);
        scrollPane22 = new JScrollPane(table22, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane22.setVisible(b);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 1;
        c01.fill = GridBagConstraints.BOTH;

        jp04.add(scrollPane22, c01);

        resultTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        resultTab.addTab("Latency detailed By Tasks", null, jp03, "Latency detailed By Tasks ");
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
        jta.append("Generating the corresponding Directed Graph \nPlease wait...\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));

        // jsp.setColumnHeaderView(100);
        // jsp.setRowHeaderView(30);

        jsp.setMaximumSize(new Dimension(800, 500));
        // JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTop,
        // jsp);
        // split.setBackground(ColorManager.InteractiveSimulationBackground);
        jp05.setMaximumSize(new Dimension(800, 500));
        jp05.add(jsp, BorderLayout.CENTER);

        this.pack();
        this.setVisible(b);

        t = new Thread() {
            public void run() {
                generateDirectedGraph(map, cpanels);

            }

        };

        t.start();
    }

    private void generateDirectedGraph(TMLMapping<TGComponent> map, List<TMLComponentDesignPanel> cpanels) {
        try {
            dgraph = new DirectedGraphTranslator(jFrameLatencyDetailedAnalysis, this, map, cpanels, 1);
            dgraph1 = dgraph;
            jta.append("A Directed Graph with " + dgraph.getGraphsize() + " vertices and " + dgraph.getGraphEdgeSet() + " edges was generated.\n");
            // buttonSaveDGraph.setEnabled(true);
            buttonShowDGraph1.setEnabled(true);
            jta.append("Browse the second simulation trace to generate the second graph \n");
            browse.setEnabled(true);

        } catch (Exception e) {
            jta.append("An Error has Accord \n");
            jta.append(e.getMessage() + "\n");
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

    private void generateDirectedGraph2(TMLMapping<TGComponent> map, List<TMLComponentDesignPanel> cpanels) {
        // TODO Auto-generated method stub
        try {
            dgraph = null;

            jFrameCompareLatencyDetail = this;

            t1 = new Thread() {
                public void run() {
                    dgraph = new DirectedGraphTranslator(jFrameLatencyDetailedAnalysis, jFrameCompareLatencyDetail, map, cpanels, 1);

                }

            };

            t1.start();

            while (t1.getState() != Thread.State.TERMINATED) {

            }

            if (t1.getState() == Thread.State.TERMINATED) {

                dgraph2 = dgraph;

            }
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

            buttonShowDGraph2.setEnabled(true);
            buttonDetailedAnalysis.setEnabled(true);
            buttonCompareInDetails.setEnabled(true);

            this.pack();
            this.revalidate();
            this.repaint();

            jta.append("A Directed Graph with " + dgraph.getGraphsize() + " vertices and " + dgraph.getGraphEdgeSet() + " edges was generated.\n");

        } catch (Exception e) {
            jta.append("An Error has Accord \n");
            jta.append(e.getMessage() + "\n");
            // buttonSaveDGraph.setEnabled(false);
            buttonShowDGraph2.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        // TODO Auto-generated method stub

        String command = evt.getActionCommand();
        // TraceManager.addDev("Command:" + command);

        if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH_FILE_1].getActionCommand())) {
            Thread t1 = new Thread() {
                public void run() {
                    dgraph1.showGraph(dgraph1);

                }
            };

            t1.start();

        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH_FILE_2].getActionCommand())) {
            Thread t2 = new Thread() {
                public void run() {
                    dgraph2.showGraph(dgraph2);

                }
            };

            t2.start();

        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand())) {
            // jta.setText("");
            dispose();
            setVisible(false);
        } else if (evt.getSource() == browse) {

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
                // file2.setText(file.getPath());

                // Object obj = filefc;

                latencyDetailedAnalysisMain.checkedTransactionsFile = new Vector<String>();
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

                    checkedTransactionsFile2 = latencyDetailedAnalysisMain.checkedTransactionsFile;
                    map = latencyDetailedAnalysisMain.map1;
                    cpanels = latencyDetailedAnalysisMain.getCpanels1();

                    this.toFront();
                    this.requestFocus();

                    this.pack();
                    this.revalidate();
                    this.repaint();
                    Thread t3 = new Thread() {
                        public void run() {
                            generateDirectedGraph2(map, cpanels);
                        }

                    };

                    t3.start();
                }

            }
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_LATENCY].getActionCommand())) {
            jta.append("Simulation Trace 1 : the Latency Between: \n " + tasksDropDownCombo1.getSelectedItem() + " and \n"
                    + tasksDropDownCombo2.getSelectedItem() + " is studied \n");

            jta.append("Simulation Trace 2 : the Latency Between: \n " + tasksDropDownCombo3.getSelectedItem() + " and \n"
                    + tasksDropDownCombo4.getSelectedItem() + " is studied \n");

            // pBarLabel.setText("Generating Detailed Latency");
            // pbar.setIndeterminate(true);
            // pbar.setMinimum(0);
            // pbar.setMaximum(1);
            updateBar(0);
            Thread t = new Thread() {
                public void run() {

                    String task1 = tasksDropDownCombo1.getSelectedItem().toString();
                    String task2 = tasksDropDownCombo2.getSelectedItem().toString();

                    String task3 = tasksDropDownCombo3.getSelectedItem().toString();
                    String task4 = tasksDropDownCombo4.getSelectedItem().toString();

                    // Boolean taint = taintFirstOp.isSelected();

                    // Boolean considerAddedRules = considerRules.isSelected();

                    Vector<SimulationTransaction> transFile1, transFile2;
                    transFile1 = parseFile(file1);
                    transFile2 = parseFile(file2);

                    latencyDetailedAnalysis(task1, task2, task3, task4, transFile1, transFile2, true, false, false);

                }
            };

            t.start();

            while (t.getState() != Thread.State.TERMINATED) {

            }

            if (t.getState() == Thread.State.TERMINATED) {

                // pbar.setIndeterminate(false);

                updateBar(1);

            }

        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_IN_DETAILS].getActionCommand())) {
            jta.append("the Latency Between: \n " + tasksDropDownCombo1.getSelectedItem() + " and \n" + tasksDropDownCombo2.getSelectedItem()
                    + " is studied \n");
            Thread t = new Thread() {
                public void run() {

                    int row1 = table11.getSelectedRow();
                    int row2 = table12.getSelectedRow();
                    int row3 = table21.getSelectedRow();
                    int row4 = table22.getSelectedRow();

                    int selectedIndex = resultTab.getSelectedIndex();

                    compareLatencyInDetails(row1, row2, row3, row4, selectedIndex);
                }

            };

            t.start();

        }
        

    }

    private void showgraphFrame(DirectedGraphTranslator dgraph) {
        try {
            dgraph.showGraph(dgraph);

            // jta.append("Refer to the generatd dialog to view the graph.\n");

        } catch (Exception e) {
            // jta.append("An Error has Accord \n");
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

        table11 = new JTable(tableData, columnNames);

        table11.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /*
         * table11.getSelectionModel().addListSelectionListener(new
         * ListSelectionListener() { public void valueChanged(ListSelectionEvent e) { if
         * (!e.getValueIsAdjusting()) { int row = table11.getSelectedRow(); Thread t =
         * new Thread() { public void run() { new JFrameLatencyDetailedPopup(dgraph1,
         * row, true); } };
         * 
         * t.start();
         * 
         * } }
         * 
         * });
         */

        tableData2 = dgraph2.latencyDetailedAnalysis(task3, task4, transFile2, taint, considerAddedRules);

//        DefaultTableModel model2 = new DefaultTableModel();

        table12.removeAll();

        table12 = new JTable(tableData2, columnNames);

        table12.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*
         * table12.getSelectionModel().addListSelectionListener(new
         * ListSelectionListener() { public void valueChanged(ListSelectionEvent e) { if
         * (!e.getValueIsAdjusting()) { int row = table12.getSelectedRow(); Thread t =
         * new Thread() { public void run() { new JFrameLatencyDetailedPopup(dgraph2,
         * row, true); } };
         * 
         * t.start();
         * 
         * } }
         * 
         * });
         */

        tableData1MinMax = dgraph1.latencyMinMaxAnalysis(task1, task2, transFile1);

//      DefaultTableModel model2 = new DefaultTableModel();

        table21.removeAll();

        table21 = new JTable(tableData1MinMax, columnMinMaxNames);

        table21.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*
         * table21.getSelectionModel().addListSelectionListener(new
         * ListSelectionListener() { public void valueChanged(ListSelectionEvent e) { if
         * (!e.getValueIsAdjusting()) { int row = table21.getSelectedRow(); Thread t =
         * new Thread() { public void run() { new JFrameLatencyDetailedPopup(dgraph1,
         * row, false); } };
         * 
         * t.start();
         * 
         * } }
         * 
         * });
         */

        tableData2MinMax = dgraph2.latencyMinMaxAnalysis(task3, task4, transFile2);

//    DefaultTableModel model2 = new DefaultTableModel();

        table22.removeAll();

        table22 = new JTable(tableData2MinMax, columnMinMaxNames);

        table22.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*
         * table22.getSelectionModel().addListSelectionListener(new
         * ListSelectionListener() { public void valueChanged(ListSelectionEvent e) { if
         * (!e.getValueIsAdjusting()) { int row = table22.getSelectedRow(); Thread t =
         * new Thread() { public void run() { new JFrameLatencyDetailedPopup(dgraph2,
         * row, false); } };
         * 
         * t.start();
         * 
         * } }
         * 
         * });
         */

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

        this.pack();
        this.setVisible(b);

        // jta.append("Message: " + tableData.length + "\n");

    }

    private void compareLatencyInDetails(int row1, int row2, int row3, int row4, int selectedIndex) {
        // TODO Auto-generated method stub

        if (selectedIndex == 0) {

            jta.append("the latency for row : " + row1 + ": and row: " + row2 + " will be studied\n");

            new JFrameLatencyComparedDetailedPopup(dgraph1, dgraph2, row1, row2, true);

        } else if (selectedIndex == 1) {
            jta.append("the latency for row : " + row3 + ": and row: " + row4 + " will be studied\n");

            new JFrameLatencyComparedDetailedPopup(dgraph1, dgraph2, row3, row4, false);

        } else {

            error("Select a panel");

        }

    }

    public void error(String error) {
        jta.append("error: " + error + "\n");
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

}
