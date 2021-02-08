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

package ui.interactivesimulation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import common.ConfigurationTTool;
import ui.MainGUI;
import ui.SimulationTrace;

public class JFrameCompareSimulationTraces extends JFrame implements ActionListener {

    private JButton browse, parse, difference, latencyDetails, latencyAnalysis;
    private JFileChooser fc, fc2;
    private File file;
    private SimulationTrace selectedST, secondSelectedST;
    private static Vector<SimulationTransaction> transFile1;
    Vector<String> dropDown1, dropDown2 = new Vector<String>();
    private JFrame  JFrameTest;

    MainGUI _mgui;

    public Vector<SimulationTransaction> getTransFile1() {
        return transFile1;
    }

    public void setTransFile1(Vector<SimulationTransaction> transFile1) {
        this.transFile1 = transFile1;
    }

    public Vector<SimulationTransaction> getTransFile2() {
        return transFile2;
    }

    public void setTransFile2(Vector<SimulationTransaction> transFile2) {
        this.transFile2 = transFile2;
    }

    private Vector<SimulationTransaction> transFile2;
    private SimulationTransaction st = new SimulationTransaction();
    private JTextField file2 = new JTextField();
    private boolean panelAdded = false;
    private boolean latencyPanelAdded = false;
    private static JPanelCompareXmlGraph newContentPane;

    JPanel latencyPanel = new JPanel(new GridBagLayout());;
    private JComboBox<String> devicesDropDownCombo1 = new JComboBox<String>();
    private JComboBox<String> devicesDropDownCombo2 = new JComboBox<String>();
    private JComboBox<String> tracesCombo1, tracesCombo2;

    private Thread t, t1;

    public JFrameCompareSimulationTraces(MainGUI mgui, String _title, SimulationTrace sST, boolean visible) {

        super(_title);
        
       

        this.selectedST = sST;
        GridLayout myLayout = new GridLayout(3, 1);
        _mgui = mgui;
        // this.setBackground(Color.RED);
        this.setLayout(myLayout);

        //addWindowListener(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) {
            fc = new JFileChooser(ConfigurationTTool.SystemCCodeDirectory);
        } else {
            fc = new JFileChooser();
        }

        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
        fc.setFileFilter(filter);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NORTHWEST;

        JTextField file1 = new JTextField();

        JLabel lab1 = new JLabel("First Simulation Traces File ", JLabel.LEFT);
        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        buttonPanel.add(lab1, c);

        JLabel lab2 = new JLabel("Secound Simulation Traces File ", JLabel.LEFT);

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        buttonPanel.add(lab2, c);

        file1.setEditable(false);
        file1.setBorder(new LineBorder(Color.BLACK));
        file1.setText(selectedST.getFullPath());

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 2;
        c.weighty = 1;
        buttonPanel.add(file1, c);

        file2.setEditable(false);
        file2.setText("file 2 name");
        file2.setBorder(new LineBorder(Color.BLACK));

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        buttonPanel.add(file2, c);

        browse = new JButton("Browse");
        browse.addActionListener(this);
        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 1;
        buttonPanel.add(browse, c);

        parse = new JButton("parse");
        parse.addActionListener(this);
        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 3;
        c.gridy = 3;
        c.weightx = 5;
        c.weighty = 1;
        buttonPanel.add(parse, c);

        /*latencyAnalysis = new JButton("latencyAnalysis");
        latencyAnalysis.addActionListener(this);
        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 5;
        c.weighty = 1;
        buttonPanel.add(latencyAnalysis, c);*/

        difference = new JButton("difference");
        difference.addActionListener(this);
        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 3;
        c.gridy = 4;
        c.weightx = 5;
        c.weighty = 1;
        buttonPanel.add(difference, c);

        this.add(buttonPanel);

        this.pack();
        this.setVisible(visible);
        JFrameTest= this;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == browse) {

            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
            fc.setFileFilter(filter);

            int returnVal = fc.showOpenDialog(JFrameCompareSimulationTraces.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                file2.setText(file.getPath());

            }
        } else if (e.getSource() == parse) {

            try {
                parseXML(selectedST.getFullPath(), file.getPath());
                DrawSimulationResults(transFile1, transFile2);

            } catch (SAXException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();
            } catch (ParserConfigurationException e1) {

                e1.printStackTrace();
            }

        } else if (e.getSource() == latencyDetails) {
            newContentPane.getTable();

            new JFrameShowLatencyDetails(transFile1, transFile2, devicesDropDownCombo1.getSelectedItem(), tracesCombo1.getSelectedItem(),
                    devicesDropDownCombo2.getSelectedItem(), tracesCombo2.getSelectedItem(), true);

        } else if (e.getSource() == difference) {

            newContentPane.showDifference();

            this.pack();
            this.setVisible(true);
        } else if (e.getSource() == devicesDropDownCombo1) {
            Vector<Object> transacationsDropDown1 = newContentPane.loadTransacationsDropDown(devicesDropDownCombo1.getSelectedItem());

            final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(transacationsDropDown1);

            // tracesCombo1.setModel(model);

        } else if (e.getSource() == devicesDropDownCombo2) {
            Vector<Object> transacationsDropDown2 = newContentPane.loadTransacationsDropDown(devicesDropDownCombo2.getSelectedItem());

            final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(transacationsDropDown2);

            // tracesCombo2.setModel(model);

        } /*else if (e.getSource() == latencyAnalysis) {

            t = new Thread() {
                public void run() {
                    try {
                        _mgui.latencyDetailedAnalysisForXML(selectedST, true, false,1);
                    } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };

            t.start();

            try {
                t.join();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (t.getState() == Thread.State.TERMINATED) {
                dropDown1 = _mgui.getLatencyDetailedAnalysis().getCheckedTransactions();
            }

            secondSelectedST = new SimulationTrace("file2", SimulationTrace.XML_DIPLO, file2.getText());

            System.out.println("load drop down1: " + _mgui.getLatencyDetailedAnalysis().getCheckedTransactions().size());

            t1 = new Thread() {
                public void run() {
                    try {
                        _mgui.latencyDetailedAnalysisForXML(secondSelectedST, true,false,2);
                    } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };

            t1.start();

            try {
                t1.join();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (t1.getState() == Thread.State.TERMINATED) {

                dropDown2 = _mgui.getLatencyDetailedAnalysis().getCheckedTransactions();
            }
            System.out.println("load drop down2: " + _mgui.getLatencyDetailedAnalysis().getCheckedTransactions().size());

            if (!latencyPanelAdded) {
                DrawLatencyPanel();

                latencyPanelAdded = true;
                latencyPanel.revalidate();
                latencyPanel.repaint();
                latencyPanel.setVisible(true);
                this.add(latencyPanel);
                this.pack();

            } else {
                this.revalidate();
                latencyPanel.revalidate();
                latencyPanel.repaint();

            }

            this.revalidate();
            this.repaint();
            this.pack();
            this.revalidate();
            this.repaint();

            this.setVisible(true);
        }*/

    }

    /*
     * else if (e.getSource() == graph) {
     * 
     * if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) { fc2 = new
     * JFileChooser(ConfigurationTTool.SystemCCodeDirectory); } else { fc2 = new
     * JFileChooser(); }
     * 
     * FileNameExtensionFilter filter = new FileNameExtensionFilter("graphml files",
     * "graphml"); fc2.setFileFilter(filter);
     * 
     * int returnVal = fc2.showOpenDialog(JFrameCompareSimulationTraces.this);
     * 
     * if (returnVal == JFileChooser.APPROVE_OPTION) { file = fc2.getSelectedFile();
     * // file2.setText(file.getPath());
     * 
     * }
     * 
     * FileReader ps = null; try { ps = new FileReader(file); } catch
     * (FileNotFoundException e1) { // TODO Auto-generated catch block
     * e1.printStackTrace(); }
     * 
     * VertexProvider<String> vertexProvider = (id, attributes) -> { String cv = new
     * String(id); cv.replaceAll("\\s+", ""); cv.replaceAll("(", "");
     * cv.replaceAll(")", ""); return cv;
     * 
     * };
     * 
     * EdgeProvider<String, DefaultEdge> edgeProvider = (from, to, label,
     * attributes) -> new DefaultEdge();
     * 
     * GraphMLImporter<String, DefaultEdge> importer = new GraphMLImporter<String,
     * DefaultEdge>(vertexProvider, edgeProvider);
     * 
     * try { Graph<String, DefaultEdge> importedGraph = new
     * DefaultDirectedGraph<>(DefaultEdge.class); ;
     * importer.importGraph(importedGraph, ps);
     * System.out.print(importedGraph.vertexSet().size()); } catch (ImportException
     * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
     * 
     * }
     */

    // }

    public int parseXML(String file1Path, String file2Path) throws SAXException, IOException, ParserConfigurationException {

        if (file1Path.length() == 0 || file2Path.length() == 0)
            throw new RuntimeException("The name of the XML file is required!");

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        SimulationTransactionParser handler = new SimulationTransactionParser();

        saxParser.parse(new File(file1Path), handler);
        transFile1 = handler.getStList();

        handler = new SimulationTransactionParser();

        saxParser.parse(new File(file2Path), handler);
        transFile2 = handler.getStList();

        return 1;

    }

    private void DrawSimulationResults(Vector<SimulationTransaction> transFile1, Vector<SimulationTransaction> transFile2) {

        if (panelAdded == true) {

            newContentPane = new JPanelCompareXmlGraph(transFile1, transFile2);
            newContentPane.setOpaque(true);

            newContentPane.updateTable();

        } else {

            newContentPane = new JPanelCompareXmlGraph(transFile1, transFile2);
            newContentPane.setOpaque(true);

            newContentPane.drawTable();

            this.add(newContentPane);

            panelAdded = true;
        }

        this.pack();
        this.setVisible(true);
    }

    private void DrawLatencyPanel() {

        latencyPanel = new JPanel(new GridBagLayout()); // use FlowLayout
        GridBagConstraints c = new GridBagConstraints();
        latencyPanel.setBorder(new javax.swing.border.TitledBorder("Latency for Simulation Traces File"));
        c.fill = GridBagConstraints.NORTHWEST;

        new JTextField();

        devicesDropDownCombo1 = new JComboBox<String>(dropDown1);

        tracesCombo1 = new JComboBox<String>(dropDown1);

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        latencyPanel.add(devicesDropDownCombo1, c);

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        latencyPanel.add(tracesCombo1, c);

        devicesDropDownCombo1.addActionListener(this);

        devicesDropDownCombo2 = new JComboBox<String>(dropDown2);

        tracesCombo2 = new JComboBox<String>(dropDown2);

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 0;

        latencyPanel.add(devicesDropDownCombo2, c);

        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 1;

        latencyPanel.add(tracesCombo2, c);

        devicesDropDownCombo2.addActionListener(this);

        latencyDetails = new JButton("latency Details");
        latencyDetails.addActionListener(this);
        c.fill = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 1;
        latencyPanel.add(latencyDetails, c);
        latencyPanel.setVisible(true);

    }



    public void close() {

        dispose();
        setVisible(false);

    }

}