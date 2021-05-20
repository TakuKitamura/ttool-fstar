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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import common.ConfigurationTTool;
import tmltranslator.simulation.CompareSimulationTrace;
import tmltranslator.simulation.SimulationTraceTableRenderer;
import tmltranslator.simulation.SimulationTransaction;
import tmltranslator.simulation.SimulationTransactionParser;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.interactivesimulation.InteractiveSimulationActions;

public class JFrameCompareSimulationTraces extends JFrame implements ActionListener, TableModelListener {
  private JButton browse, parse, difference, latencyDetails, latencyAnalysis, close;
  private JFileChooser fc, fc2;
  private File file;
  private SimulationTrace selectedST;
  private static Vector<SimulationTransaction> transFile1;
  private Vector<String> dropDown1, dropDown2 = new Vector<String>();
  private JFrame JFrameTest;
  private GridBagConstraints mainConstraint = new GridBagConstraints();
  private JTable table11;
  private static final String FIRST_SIMULATION_TRACE_FILE = "First Simulation Trace File ";
  private static final String Second_SIMULATION_TRACE_FILE = "Second Simulation Trace File ";
  private static final String XML_FILES = "XML files";
  private static final String XML = "XML";
  private static final String NAME_REQUIRED = "The name of the XML file is required!";
  private MainGUI _mgui;

  private Vector<SimulationTransaction> transFile2;
  private SimulationTransaction st = new SimulationTransaction();
  private JTextField file2 = new JTextField();
  private boolean latencyPanelAdded = false;
  private static CompareSimulationTrace newContentPane;
  JPanel latencyPanel = new JPanel(new GridBagLayout());;
  private JComboBox<String> tracesCombo1, tracesCombo2;
  private Thread t, t1;
  private LatencyDetailedAnalysisActions[] actions;
  private JScrollPane scrollPane11;

  public JFrameCompareSimulationTraces(MainGUI mgui, String _title, SimulationTrace sST, boolean visible) {
    super(_title);
    initActions();
    this.selectedST = sST;
    // GridLayout myLayout = new GridLayout(3, 1);
    _mgui = mgui;
    // this.setBackground(Color.RED);
    GridBagLayout gridbagmain = new GridBagLayout();
    Container framePanel = getContentPane();
    framePanel.setLayout(gridbagmain);
    mainConstraint.gridx = 0;
    mainConstraint.gridy = 0;
    mainConstraint.fill = GridBagConstraints.BOTH;
    // addWindowListener(this);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    close = new JButton(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_CLOSE]);
    parse = new JButton(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_PARSE]);
    difference = new JButton(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_DIFF]);
    JPanel jp = new JPanel();
    jp.add(close);
    jp.add(parse);
    jp.add(difference);
    framePanel.add(jp, mainConstraint);
    if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) {
      fc = new JFileChooser(ConfigurationTTool.SystemCCodeDirectory);
    } else {
      fc = new JFileChooser();
    }
    FileNameExtensionFilter filter = new FileNameExtensionFilter(XML_FILES, XML);
    fc.setFileFilter(filter);
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.NORTHWEST;
    JTextField file1 = new JTextField();
    JLabel lab1 = new JLabel(FIRST_SIMULATION_TRACE_FILE, JLabel.LEFT);
    c.fill = GridBagConstraints.NORTHWEST;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;
    c.anchor = GridBagConstraints.WEST;
    buttonPanel.add(lab1, c);
    JLabel lab2 = new JLabel(Second_SIMULATION_TRACE_FILE, JLabel.LEFT);
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
    buttonPanel.add(file2, c);
    browse = new JButton("Browse");
    browse.addActionListener(this);
    c.fill = GridBagConstraints.NORTHWEST;
    c.gridx = 4;
    c.gridy = 1;
    c.weightx = 1;
    c.weighty = 1;
    buttonPanel.add(browse, c);
    mainConstraint.gridx = 0;
    mainConstraint.gridy = 1;
    this.add(buttonPanel, mainConstraint);
    JPanel jp03 = new JPanel(new BorderLayout());
    table11 = new JTable();
    scrollPane11 = new JScrollPane(table11, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane11.setVisible(true);
    jp03.add(scrollPane11, BorderLayout.CENTER);
    mainConstraint.weighty = 1.0;
    mainConstraint.weightx = 1.0;
    mainConstraint.gridx = 0;
    mainConstraint.gridy = 2;
    this.add(jp03, mainConstraint);
    this.pack();
    this.setVisible(visible);
    JFrameTest = this;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (e.getSource() == browse) {
      FileNameExtensionFilter filter = new FileNameExtensionFilter(XML_FILES, XML);
      fc.setFileFilter(filter);
      int returnVal = fc.showOpenDialog(JFrameCompareSimulationTraces.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        file = fc.getSelectedFile();
        file2.setText(file.getPath());
      }
    } else if ((command.equals(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_PARSE].getActionCommand()))) {
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
    } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_DIFF].getActionCommand())) {
      int numRows = table11.getRowCount();
      int numCols = table11.getColumnCount();
      for (int j = 2; j < numCols; j++) {
        for (int i = 0; i < numRows; i++) {
          for (int k = 0; k < numRows; k++) {
            if (table11.getValueAt(i, 0).equals(table11.getValueAt(k, 0))) {
              if (i != k && table11.getValueAt(i, j) != null && table11.getValueAt(k, j) != null
                  && table11.getValueAt(i, j).equals(table11.getValueAt(k, j))) {
                table11.setValueAt(null, k, j);
                table11.setValueAt(null, i, j);
              }
            }
          }
        }
      }
      numRows = table11.getRowCount();
      numCols = table11.getColumnCount();
      table11.repaint();
      table11.revalidate();
      scrollPane11.setViewportView(table11);
      scrollPane11.setVisible(true);
      scrollPane11.revalidate();
      scrollPane11.repaint();
      this.pack();
      this.setVisible(true);
    } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_COMPARE_CLOSE].getActionCommand())) {
      dispose();
      setVisible(false);
    }
  }

  public int parseXML(String file1Path, String file2Path)
      throws SAXException, IOException, ParserConfigurationException {
    if (file1Path.length() == 0 || file2Path.length() == 0)
      throw new RuntimeException(NAME_REQUIRED);
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

  private void DrawSimulationResults(Vector<SimulationTransaction> transFile1,
      Vector<SimulationTransaction> transFile2) {
    newContentPane = new CompareSimulationTrace();
    table11.setModel(newContentPane.JPanelCompareXmlGraph(transFile1, transFile2));
    table11.setFillsViewportHeight(true);
    table11.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    SimulationTraceTableRenderer tr = new SimulationTraceTableRenderer();
    int ncols = table11.getColumnCount();
    table11.getModel().addTableModelListener(this);
    TableColumnModel tcm = table11.getColumnModel();
    for (int c = 0; c < ncols; c++) {
      TableColumn tc = tcm.getColumn(c);
      tc.setCellRenderer(tr);
    }
    // newContentPane.setOpaque(true);
    table11.repaint();
    table11.revalidate();
    scrollPane11.setViewportView(table11);
    scrollPane11.setVisible(true);
    scrollPane11.revalidate();
    scrollPane11.repaint();
    this.pack();
    this.setVisible(true);
  }

  private void initActions() {
    actions = new LatencyDetailedAnalysisActions[LatencyDetailedAnalysisActions.NB_ACTION];
    for (int i = 0; i < LatencyDetailedAnalysisActions.NB_ACTION; i++) {
      actions[i] = new LatencyDetailedAnalysisActions(i);
      actions[i].addActionListener(this);
      // actions[i].addKeyListener(this);
    }
  }

  public void close() {
    dispose();
    setVisible(false);
  }

  @Override
  public void tableChanged(TableModelEvent e) {
  }

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
}