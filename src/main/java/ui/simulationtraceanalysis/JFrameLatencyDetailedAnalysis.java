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
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import tmltranslator.TMLMapping;
import ui.ColorManager;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.interactivesimulation.SimulationTransactionParser;

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
    protected JTextArea jta;
    protected JScrollPane jsp;
    private JTabbedPane commandTab, resultTab;/* , resultTabDetailed; */
    private JPanel loadxml, commands, jp01, jp02, /* activities, */ graphAnalysisResult, jp03, jp04, jp05; // ,graphAnalysisResultDetailed;
    protected JButton buttonClose, buttonShowDGraph, buttonSaveDGraph, buttonBrowse, buttonDetailedAnalysis;

    public Vector<String> checkedTransactions = new Vector<String>();
    protected JTextField saveDirName;
    protected JTextField saveFileName;

    private JComboBox<String> tasksDropDownCombo1 = new JComboBox<String>();
    private JComboBox<String> tasksDropDownCombo2 = new JComboBox<String>();

//    private JComboBox<Object> tracesCombo1, tracesCombo2;

    protected SaveGraphToolBar sgtb;

    public LatencyDetailedAnalysisActions[] actions;
    private JFileChooser fc, fc2;

    private String[] columnNames = new String[5];
    private String[] columnMinMaxNames = new String[5];
//    private String[] columnByTaskNames = new String[4];
    private Object[][] dataDetailedByTask;
    private Object[][] dataDetailedMinMax;

    public static JTable table11, table12;
    private JTextField file1;
    private File file;
    private static Vector<SimulationTransaction> transFile1;

    private DirectedGraphTranslator dgraph;

    private JScrollPane scrollPane11, scrollPane12;// , scrollPane13;

//    private GridBagLayout gridbag01;
//    private GridBagConstraints c01;
    private Thread t;

    // @SuppressWarnings("deprecation")
    public JFrameLatencyDetailedAnalysis(TMLMapping<TGComponent> tmap, List<TMLComponentDesignPanel> cpanels, SimulationTrace selectedST) {
        super("Latency Detailed Analysis");
        initActions();
        fillCheckedTrans(tmap);

        file = new File(selectedST.getFullPath());

        GridBagLayout gridbagmain = new GridBagLayout();

        GridBagConstraints mainConstraint = new GridBagConstraints();

        Container framePanel = getContentPane();
        framePanel.setLayout(gridbagmain);
        // mainConstraint.weighty = 1.0;
        // mainConstraint.weightx = 1.0;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 0;
        // mainConstraint.gridwidth = GridBagConstraints.REMAINDER; // end row
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;

        // buttonSaveDGraph = new
        // JButton(actions[LatencyDetailedAnalysisActions.ACT_SAVE_TRACE]);
        buttonShowDGraph = new JButton(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH]);

        buttonDetailedAnalysis = new JButton(actions[LatencyDetailedAnalysisActions.ACT_DETAILED_ANALYSIS]);
        buttonClose = new JButton(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL]);

        // JPanel mainpanel = new JPanel(new BorderLayout());
        // mainpanel.setBackground(ColorManager.InteractiveSimulationBackground);

        JPanel jp = new JPanel();
        // jp.setBackground(ColorManager.InteractiveSimulationBackground);
        // jp.setPreferredSize(new Dimension(800, 75));
        // jp.add(buttonSaveDGraph);
        jp.add(buttonShowDGraph);

        jp.add(buttonDetailedAnalysis);
        jp.add(buttonClose);

        buttonDetailedAnalysis.setEnabled(true);
        buttonShowDGraph.setEnabled(false);

        framePanel.add(jp, mainConstraint);

        // mainpanel.add(jp, BorderLayout.NORTH);

//        GridBagLayout gridbag02 = new GridBagLayout();

//        GridBagLayout gridbag03 = new GridBagLayout();

        commands = new JPanel(new BorderLayout());
        commands.setBorder(new javax.swing.border.TitledBorder("load/Save"));
        // commands.setMaximumSize(new Dimension(800, 500));

        mainConstraint.gridheight = 1;
        // mainConstraint.weighty = 0.5;
        // mainConstraint.weightx = 0.5;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 1;
        mainConstraint.gridwidth = 1; // end row
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;

        framePanel.add(commands, mainConstraint);

//        GridBagLayout gridbag05 = new GridBagLayout();

        graphAnalysisResult = new JPanel(new BorderLayout());
        graphAnalysisResult.setBorder(new javax.swing.border.TitledBorder("Latency Detailed Analysis "));
        mainConstraint.gridheight = 1;
        // .weighty =0.5;
        // mainConstraint.weightx = 0.5;
        mainConstraint.weighty = 1.0;
        mainConstraint.weightx = 1.0;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 2;
        mainConstraint.ipady = 200;
        mainConstraint.gridwidth = 1; // end row
        // mainConstraint.gridwidth = GridBagConstraints.REMAINDER; // end row
        mainConstraint.fill = GridBagConstraints.BOTH;

        framePanel.add(graphAnalysisResult, mainConstraint);
        // mainTop.add(commands, c02);

        jp05 = new JPanel(new BorderLayout());
        // mainpanel.add(split, BorderLayout.SOUTH);
        mainConstraint.weighty = 00;
        mainConstraint.ipady = 100;

        mainConstraint.gridx = 0;
        mainConstraint.gridy = 3;

        mainConstraint.fill = GridBagConstraints.HORIZONTAL;

        framePanel.add(jp05, mainConstraint);
        commandTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();

        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        loadxml = new JPanel(gridbag01);
        commandTab.addTab("load XML", null, loadxml, "load XML");

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        // c01.fill = GridBagConstraints.BOTH;

        JLabel xmlLabel = new JLabel("Simulation trace as XML File ", JLabel.LEFT);
        loadxml.add(xmlLabel, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 1;
        // c01.fill = GridBagConstraints.BOTH;

        JLabel tasksLabel = new JLabel("Study the Detailed Latency Between:  ", JLabel.LEFT);
        loadxml.add(tasksLabel, c01);

        file1 = new JTextField(40);
        file1.setMinimumSize(new Dimension(300, 30));
        file1.setText(selectedST.getFullPath());
        file1.setEditable(false);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 0;

        loadxml.add(file1, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 1;
        c01.gridy = 1;
        tasksDropDownCombo1 = new JComboBox<String>(checkedTransactions);
        loadxml.add(tasksDropDownCombo1, c01);

        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 2;
        c01.gridy = 1;

        tasksDropDownCombo2 = new JComboBox<String>(checkedTransactions);

        loadxml.add(tasksDropDownCombo2, c01);

        // buttonBrowse = new
        // JButton(actions[LatencyDetailedAnalysisActions.ACT_LOAD_SIMULATION_TRACES]);

        // c01.gridheight = 1;
        // c01.weighty = 1.0;
        // c01.weightx = 1.0;
        // c01.gridwidth = 1;
        // c01.gridx = 2;
        // c01.gridy = 0;
        // loadxml.add(buttonBrowse, c01);

        GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        // Save
        jp01 = new JPanel(gridbag04);

        commandTab.addTab("Save Graph", null, jp01, "Save graph");

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
        c01.gridheight = 1;

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
        c04.gridy = 1;
        c04.gridwidth = GridBagConstraints.REMAINDER; // end row
        c04.fill = GridBagConstraints.HORIZONTAL;
        c04.gridheight = 1;

        jp01.add(jp02, c04);

        // mainTop.add(loadxml, c02);

        commands.add(commandTab, BorderLayout.NORTH);

        columnNames[0] = "OPERATOR A";
        columnNames[1] = "Start Time ";
        columnNames[2] = "OPERATOR B";
        columnNames[3] = "End Time ";
        columnNames[4] = "Delay ";

        // columnNames[5] = "Related Tasks Details ";
        // columnNames[6] = "Total time- Related Tasks ";

        dataDetailedByTask = new Object[0][0];

        jp03 = new JPanel(new BorderLayout());

        table11 = new JTable(dataDetailedByTask, columnNames);
        scrollPane11 = new JScrollPane(table11, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane11.setVisible(true);
        jp03.add(scrollPane11, BorderLayout.CENTER);

        columnMinMaxNames[0] = "OPERATOR A";
        columnMinMaxNames[1] = "Start Time ";
        columnMinMaxNames[2] = "OPERATOR B";
        columnMinMaxNames[3] = "End Time ";
        columnMinMaxNames[4] = "Delay ";

        // columnNames[5] = "Related Tasks Details ";
        // columnNames[6] = "Total time- Related Tasks ";

        dataDetailedMinMax = new Object[0][0];

        jp04 = new JPanel(new BorderLayout());
        table12 = new JTable(dataDetailedMinMax, columnMinMaxNames);
        scrollPane12 = new JScrollPane(table12, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane12.setVisible(true);
        jp04.add(scrollPane12, BorderLayout.CENTER);

        // graphAnalysisResult.setMinimumSize(new Dimension(100, 800));

        resultTab = GraphicLib.createTabbedPaneRegular();// new JTabbedPane();
        resultTab.addTab("Latency detailed By Tasks", null, jp03, "Latency detailed By Tasks ");
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
        jta.append("Generating the corresponding Directed Graph \nPlease wait...\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));
        jsp.setVisible(true);
        // jsp.setColumnHeaderView(100);
        // jsp.setRowHeaderView(30);

        jp05.setMaximumSize(new Dimension(800, 500));
        jp05.add(jsp, BorderLayout.CENTER);
        // JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
        // graphAnalysisResult,
        // jp05);
        // split.setBackground(ColorManager.InteractiveSimulationBackground);

        this.pack();
        this.setVisible(true);

        t = new Thread() {
            public void run() {
                generateDirectedGraph(tmap, cpanels);
            }
        };

        t.start();

    }

    public DirectedGraphTranslator getDgraph() {
        return dgraph;
    }

    public State graphStatus() {
        return t.getState();
    }

    private void generateDirectedGraph(TMLMapping<TGComponent> tmap, List<TMLComponentDesignPanel> cpanels) {

        try {
            dgraph = new DirectedGraphTranslator(tmap, cpanels);
            jta.append("A Directed Graph with " + dgraph.getGraphsize() + " vertices" + dgraph.getGraphEdgeSet() + "Edges was generated.\n");
            // buttonSaveDGraph.setEnabled(true);
            buttonShowDGraph.setEnabled(true);

        } catch (Exception e) {
            jta.append("An Error has Accord \n");
            jta.append(e.getMessage() + "\n");
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
                    jta.append("Directed Graph Save in" + filename + ".png \n");
                } else {
                    error("Wrong parameter: must be a file name");
                }

            } catch (ExportException e) {
                jta.append("An Error has Accord \n");
                jta.append(e.getMessage() + "\n");
            } catch (IOException e) {
                jta.append("An Error has Accord \n");
                jta.append(e.getMessage() + "\n");
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
                    jta.append("Directed Graph Save in" + filename + ".graphml \n");

                } else {
                    error("Wrong parameter: must be a file name");
                }

            } catch (ExportException e) {
                jta.append("An Error has Accord \n");
                jta.append(e.getMessage() + "\n");
            } catch (IOException e) {
                jta.append("An Error has Accord \n");
                jta.append(e.getMessage() + "\n");
            }

        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_SHOW_GRAPH].getActionCommand())) {
            Thread t = new Thread() {
                public void run() {
                    showgraphFrame();
                }
            };

            t.start();

        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand())) {
            jta.setText("");
            dispose();
            setVisible(false);
        } /*
           * else if (command.equals(actions[LatencyDetailedAnalysisActions.
           * ACT_LOAD_SIMULATION_TRACES].getActionCommand())) {
           * 
           * if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) { fc = new
           * JFileChooser(ConfigurationTTool.SystemCCodeDirectory); } else { fc = new
           * JFileChooser(); }
           * 
           * FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files",
           * "xml"); fc.setFileFilter(filter); int returnVal = fc.showOpenDialog(this);
           * 
           * if (returnVal == JFileChooser.APPROVE_OPTION) {
           * 
           * ComboBoxModel[] models = new ComboBoxModel[2]; file = fc.getSelectedFile();
           * file1.setText(file.getPath());
           * 
           * transFile1 = parseFile(file);
           * 
           * // models[0] = new DefaultComboBoxModel(loadDropDowns()); // models[1] = new
           * DefaultComboBoxModel(loadDropDowns());
           * 
           * tasksDropDownCombo1.setModel(models[0]);
           * tasksDropDownCombo2.setModel(models[1]);
           * buttonDetailedAnalysis.setEnabled(true);
           * 
           * jta.append("The imported file contains: " + transFile1.size() +
           * " Traces \n"); this.pack(); this.setVisible(true); } }
           */ else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_DETAILED_ANALYSIS].getActionCommand())) {
            jta.append("the Latency Between: \n " + tasksDropDownCombo1.getSelectedItem() + " and \n" + tasksDropDownCombo2.getSelectedItem()
                    + " is studied \n");
            Thread t = new Thread() {
                public void run() {
                    transFile1 = parseFile(file);
                    latencyDetailedAnalysis();
                }
            };

            t.start();

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
                // file2.setText(file.getPath());

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

            // SimpleGraphMLImporter <String, DefaultEdge> importer = new
            // SimpleGraphMLImporter<String, DefaultEdge>();

            try {
                Graph<String, DefaultEdge> importedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
                ;
                importer.importGraph(importedGraph, ps);

            } catch (ImportException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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

    private void latencyDetailedAnalysis() {
        String task1 = tasksDropDownCombo1.getSelectedItem().toString();
        String task2 = tasksDropDownCombo2.getSelectedItem().toString();

        Object[][] tableData = null;

        tableData = dgraph.latencyDetailedAnalysis(task1, task2, transFile1);

//        DefaultTableModel model = new DefaultTableModel();

        table11.removeAll();

        table11 = new JTable(tableData, columnNames);

        table11.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table11.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = table11.getSelectedRow();
                    Thread t = new Thread() {
                        public void run() {
                            /* JFrameLatencyDetailedPopup rowPopup = */ new JFrameLatencyDetailedPopup(dgraph, row, true);
                        }
                    };

                    t.start();

                }
            }

        });

        Object[][] tableData2 = null;

        tableData2 = dgraph.latencyMinMaxAnalysis(task1, task2, transFile1);

//        DefaultTableModel model2 = new DefaultTableModel();

        table12.removeAll();

        table12 = new JTable(tableData2, columnMinMaxNames);

        table12.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table12.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = table12.getSelectedRow();
                    Thread t = new Thread() {
                        public void run() {
                            /* JFrameLatencyDetailedPopup rowPopup = */new JFrameLatencyDetailedPopup(dgraph, row, false);
                        }
                    };

                    t.start();

                }
            }

        });

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
        this.setVisible(true);

        // jta.append("Message: " + tableData.length + "\n");

    }

    /*
     * public Vector<String> loadDropDowns() { Vector<String> allLatencyTasks =
     * dgraph.getLatencyVertices();
     * 
     * return allLatencyTasks;
     * 
     * }
     */

    public void error(String error) {
        jta.append("error: " + error + "\n");
    }

    private void showgraphFrame() {
        try {
            dgraph.showGraph(dgraph);

            jta.append("Refer to the generatd dialog to view the graph.\n");

        } catch (Exception e) {
            jta.append("An Error has Accord \n");
            jta.append(e.getMessage() + "\n");
        }

    }

    public void fillCheckedTrans(TMLMapping<TGComponent> tmap) {
        if (tmap == null) {
            return;
        }
        for (TGComponent tgc : tmap.getTMLModeling().getCheckedComps().keySet()) {
            String compName = tmap.getTMLModeling().getCheckedComps().get(tgc);
            TraceManager.addDev(compName + "__" + tgc.getDIPLOID());
            checkedTransactions.add(compName + "__" + tgc.getDIPLOID());
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
        return t;
    }

}