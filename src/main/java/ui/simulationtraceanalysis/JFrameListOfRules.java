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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.jgrapht.Graphs;
import myutil.ScrolledJTextArea;
import ui.ColorManager;

/**
 * Class JFrameLatencyDetailedPopup: this class opens the frame showing the
 * details of the latency per selected row
 * 
 * 23/09/2019
 *
 * @author Maysam Zoor
 * 
 */
public class JFrameListOfRules extends JFrame implements TableModelListener, ActionListener {
    private String[] columnByTaskNames = new String[3];
    private HashMap<vertex, List<vertex>> ruleAddedEdges = new HashMap<vertex, List<vertex>>();
    private HashMap<vertex, List<vertex>> ruleAddedEdgesChannels = new HashMap<vertex, List<vertex>>();
    private Object[][] tableData;
    private JScrollPane scrollPane12;
    private JButton buttonClose, buttonDeleteRule, buttonDeleteALLRules;
    private LatencyDetailedAnalysisActions[] actions;
    private JTextArea jta;
    private JPanel jp, jp05, commands, rulesList, rulesList1;
    private JScrollPane jsp;
    private DirectedGraphTranslator directedGraph;
    private JTable taskNames;
    private DefaultTableModel model;

    public JFrameListOfRules(DirectedGraphTranslator dgraph) {
        super("All Added Rules");
        initActions();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        directedGraph = dgraph;
        GridBagLayout gridbagmain = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        Container framePanel = getContentPane();
        framePanel.setLayout(gridbagmain);
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        // Save
        jp = new JPanel();
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 0;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        // framePanel.setBackground(Color.red);
        buttonDeleteRule = new JButton(actions[LatencyDetailedAnalysisActions.ACT_DELETE_SELECTED_RULE]);
        buttonDeleteALLRules = new JButton(actions[LatencyDetailedAnalysisActions.ACT_DELETE_ALL_RULE]);
        buttonClose = new JButton(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_RULE]);
        jp.add(buttonDeleteRule);
        jp.add(buttonDeleteALLRules);
        jp.add(buttonClose);
        // jp.setBackground(Color.red);
        framePanel.add(jp, mainConstraint);
        buttonClose.setEnabled(true);
        buttonDeleteRule.setEnabled(false);
        columnByTaskNames[0] = "Operator 2";
        columnByTaskNames[1] = "After ";
        columnByTaskNames[2] = "Operator 1 ";
        ruleAddedEdgesChannels = dgraph.getRuleAddedEdgesChannels();
        fillRuleTables();
        scrollPane12 = new JScrollPane(taskNames, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane12.setVisible(true);
        rulesList = new JPanel(new BorderLayout());
        rulesList.setBorder(new javax.swing.border.TitledBorder("All Added Rules "));
        // mainConstraint.gridheight = 1;
        // mainConstraint.weighty = 5;
        // mainConstraint.weightx = 5;
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 1;
        mainConstraint.gridwidth = 1; // end row
        mainConstraint.ipady = 200;
        // mainConstraint.ipady = 500;
        mainConstraint.fill = GridBagConstraints.BOTH;
        rulesList.add(scrollPane12);
        framePanel.add(rulesList, mainConstraint);
        jp05 = new JPanel(new BorderLayout());
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
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        framePanel.add(jp05, mainConstraint);
        jta = new ScrolledJTextArea();
        jta.setBackground(ColorManager.InteractiveSimulationJTABackground);
        jta.setForeground(ColorManager.InteractiveSimulationJTAForeground);
        jta.setMinimumSize(new Dimension(800, 400));
        jta.setRows(15);
        jta.setMaximumSize(new Dimension(800, 500));
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Here are all the added rules...\n");
        jta.append("Select a row and press \"Delete Selected Rules\" to remove it. \n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));
        jsp.setVisible(true);
        // jsp.setColumnHeaderView(100);
        // jsp.setRowHeaderView(30);
        jp05.setMaximumSize(new Dimension(800, 500));
        jp05.add(jsp, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }

// fill table with prevously created rules in the same session
    private void fillRuleTables() {
        int size = 0;
        for (Entry<vertex, List<vertex>> v : ruleAddedEdgesChannels.entrySet()) {
            vertex v1 = v.getKey();
            List<vertex> lv = v.getValue();
            size = size + lv.size();
        }
        tableData = new Object[size][3];
        int j = 0;
        for (Entry<vertex, List<vertex>> v : ruleAddedEdgesChannels.entrySet()) {
            vertex v1 = v.getKey();
            List<vertex> lv = v.getValue();
            for (int i = 0; i < lv.size(); i++) {
                tableData[j][0] = lv.get(i).getName();
                tableData[j][1] = "After";
                tableData[j][2] = v1.getName();
                j++;
            }
        }
        model = new DefaultTableModel(tableData, columnByTaskNames) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return Integer.class;
                case 4:
                    return Integer.class;
                default:
                    return Integer.class;
                }
            }
        };
        // taskNames = new JTable(dataDetailedByTask, columnByTaskNames);
        taskNames = new JTable(model);
        taskNames.setAutoCreateRowSorter(true);
        taskNames.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                buttonDeleteRule.setEnabled(true);
            }
        });
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    private void initActions() {
        actions = new LatencyDetailedAnalysisActions[LatencyDetailedAnalysisActions.NB_ACTION];
        for (int i = 0; i < LatencyDetailedAnalysisActions.NB_ACTION; i++) {
            actions[i] = new LatencyDetailedAnalysisActions(i);
            actions[i].addActionListener(this);
            // actions[i].addKeyListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        // TraceManager.addDev("Command:" + command);
        if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_STOP_AND_CLOSE_RULE].getActionCommand())) {
            jta.setText("");
            dispose();
            setVisible(false);
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_DELETE_ALL_RULE].getActionCommand())) {
            directedGraph.setRuleAddedEdges(new HashMap<vertex, List<vertex>>());
            directedGraph.setRuleAddedEdgesChannels(new HashMap<vertex, List<vertex>>());
            model.setRowCount(0);
            taskNames.setModel(model);
            jta.append("All Rules are deleted \n");
            model.fireTableDataChanged();
            taskNames.revalidate();
            taskNames.repaint();
            scrollPane12.revalidate();
            scrollPane12.repaint();
            this.revalidate();
            this.pack();
            this.repaint();
            this.setVisible(true);
        } else if (command.equals(actions[LatencyDetailedAnalysisActions.ACT_DELETE_SELECTED_RULE].getActionCommand())) {
            int i = taskNames.getSelectedRow();
            vertex v1 = directedGraph.getvertex(model.getValueAt(i, 0).toString());
            vertex v2 = directedGraph.getvertex(model.getValueAt(i, 2).toString());
            ruleAddedEdges = directedGraph.getRuleAddedEdges();
            // remove rule and its channel
            removeRule(v1, v2, i);
            model.removeRow(i);
            taskNames.setModel(model);
            model.fireTableDataChanged();
            buttonDeleteRule.setEnabled(false);
            taskNames.revalidate();
            taskNames.repaint();
            scrollPane12.revalidate();
            scrollPane12.repaint();
            this.revalidate();
            this.pack();
            this.repaint();
            this.setVisible(true);
            jta.append("All Rule between: " + v1.getName() + " and " + v2.getName() + " is deleted.\n");
        }
    }

    private void removeRule(vertex v1, vertex v2, int i) {
        vertex v1Channel = null, v2Channel = null;
        if (v2Channel == null && Graphs.vertexHasSuccessors(directedGraph.getG(), v2)) {
            for (vertex n : Graphs.successorListOf(directedGraph.getG(), v2)) {
                if (n.getType() == vertex.TYPE_CHANNEL) {
                    v2Channel = n;
                    break;
                }
            }
        }
        if (Graphs.vertexHasPredecessors(directedGraph.getG(), v1)) {
            for (vertex n : Graphs.predecessorListOf(directedGraph.getG(), v1)) {
                if (n.getType() == vertex.TYPE_CHANNEL) {
                    v1Channel = n;
                    break;
                }
            }
        }
        if (v1Channel != null && v2Channel != null) {
            if (model.getValueAt(i, 1).equals("After")) {
                if (ruleAddedEdges.containsKey(v2Channel)) {
                    if (ruleAddedEdges.get(v2Channel).contains(v1Channel)) {
                        ruleAddedEdges.get(v2Channel).remove(v1Channel);
                    }
                }
                if (ruleAddedEdgesChannels.containsKey(v2)) {
                    if (ruleAddedEdgesChannels.get(v2).contains(v1)) {
                        ruleAddedEdgesChannels.get(v2).remove(v1);
                    }
                }
            }
        }
    }
}