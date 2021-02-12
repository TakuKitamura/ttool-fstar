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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import tmltranslator.simulationtraceanalysis.DependencyGraphTranslator;
import tmltranslator.simulationtraceanalysis.PlanArrays;

/**
 * Class JFrameLatencyDetailedPopup: this class opens the frame showing the
 * details of the latency per selected row
 * 
 * 23/09/2019
 *
 * @author Maysam Zoor
 */
public class JFrameLatencyDetailedPopup extends JFrame implements TableModelListener {
    private String[] columnByTaskNames = new String[5];
    private String[] columnByHWNames = new String[5];
    private JScrollPane scrollPane12, scrollPane13, scrollPane14;
    private static JTable taskNames, hardwareNames;
    private Object[][] dataDetailedByTask;
    private List<String> onPathBehavior = new ArrayList<String>();
    private List<String> offPathBehavior = new ArrayList<String>();
    private List<String> offPathBehaviorCausingDelay = new ArrayList<String>();
    // private Thread t, t1;
    private Object[][] dataHWDelayByTask;
    private static final String TRANSACTION_LIST = "Transaction List ";
    private static final String TRANSACTION_DIAGRAM_NAME = "Transaction Diagram Name ";
    private static final String HARDWARE = "Hardware ";
    private static final String START_TIME = "Start Time ";
    private static final String END_TIME = "End Time ";
    private static final String MANDATORY_TRANSACTIONS = "Mandatory Transactions ";
    private static final String TASKS_ON_SAME_DEVICE = "Tasks on the Same Device ";
    private static final String NON_MANDATORY_TRANSACTIONS = "Non-Mandatory Transactions";
    private static final String NON_MAND_NO_CONT_TRAN = "Non-Mandatory Transactions No-Contention";
    private static final String DEVICE_NAME = "Device Name";
    private static final String ZERO = "0";
    private static final String NON_MAND_CONT_TRAN = "Non-Mandatory Transactions Causing Contention";
    private static final String TABLE_LENGED = "Table Lenged: ";

    public JFrameLatencyDetailedPopup(DependencyGraphTranslator dgraph, int row, boolean firstTable, Boolean taint,
            LatencyAnalysisParallelAlgorithms th, boolean visible) throws InterruptedException {
        super("Precise Latency By Row");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GridLayout myLayout = new GridLayout(4, 1);
        this.setLayout(myLayout);
        columnByTaskNames[0] = TRANSACTION_LIST;
        columnByTaskNames[1] = TRANSACTION_DIAGRAM_NAME;
        columnByTaskNames[2] = HARDWARE;
        columnByTaskNames[3] = START_TIME;
        columnByTaskNames[4] = END_TIME;
        JPanel jp04 = new JPanel(new BorderLayout());
        jp04.setBorder(new javax.swing.border.TitledBorder(MANDATORY_TRANSACTIONS));
        if (firstTable) {
            th.setDgraph(dgraph);
            th.setRow(row);
            th.start(2);
            th.run();
            dataDetailedByTask = th.getDataDetailedByTask();
        } else {
            if (taint) {
                th.setDgraph(dgraph);
                th.setRow(row);
                th.start(3);
                th.run();
                // th.getT().join();
                dataDetailedByTask = th.getDataDetailedByTask();
            } else {
                th.setDgraph(dgraph);
                th.setRow(row);
                th.start(4);
                th.run();
                dataDetailedByTask = th.getDataDetailedByTask();
            }
        }
        DefaultTableModel model = new DefaultTableModel(dataDetailedByTask, columnByTaskNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

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
        JTable taskNames = new JTable(model);
        taskNames.setAutoCreateRowSorter(true);
        scrollPane12 = new JScrollPane(taskNames, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane12.setVisible(visible);
        jp04.add(scrollPane12);
        this.add(jp04);
        columnByHWNames[0] = TASKS_ON_SAME_DEVICE;
        columnByHWNames[1] = TRANSACTION_DIAGRAM_NAME;
        columnByHWNames[2] = HARDWARE;
        columnByHWNames[3] = START_TIME;
        columnByHWNames[4] = END_TIME;
        if (firstTable) {
            th.setDgraph(dgraph);
            th.setRow(row);
            th.start(5);
            th.run();
            // th.getT().join();
            dataHWDelayByTask = th.getDataDetailedByTask();
        } else {
            if (taint) {
                th.setDgraph(dgraph);
                th.setRow(row);
                th.start(6);
                th.run();
                dataHWDelayByTask = th.getDataDetailedByTask();
            } else {
                th.setDgraph(dgraph);
                th.setRow(row);
                th.start(7);
                th.run();
                dataHWDelayByTask = th.getDataDetailedByTask();
            }
        }
        DefaultTableModel model2 = new DefaultTableModel(dataHWDelayByTask, columnByHWNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

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
        JPanel jp05 = new JPanel(new BorderLayout());
        jp05.setBorder(new javax.swing.border.TitledBorder(NON_MANDATORY_TRANSACTIONS));
        JTable hardwareNames = new JTable(model2);
        hardwareNames.setAutoCreateRowSorter(true);
        scrollPane13 = new JScrollPane(hardwareNames, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane13.setVisible(visible);
        jp05.add(scrollPane13);
        this.add(jp05);
        PlanArrays arrays = new PlanArrays();
        arrays.fillArrays(dgraph, row, firstTable, taint);
        DefaultTableModel model3 = new DefaultTableModel(arrays.getDataDetailedByTask(), arrays.getColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model3);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoCreateRowSorter(true);
        TableRenderer tr = new TableRenderer(arrays.getOnPathBehavior(), arrays.getOffPathBehaviorCausingDelay(), arrays.getOffPathBehavior());
        int ncols = table.getColumnCount();
        table.getModel().addTableModelListener(this);
        TableColumnModel tcm = table.getColumnModel();
        for (int c = 0; c < ncols; c++) {
            TableColumn tc = tcm.getColumn(c);
            tc.setCellRenderer(tr);
        }
        // set the column width for small tables/ performance issue with big tables
        if (ncols < 1000) {
            for (int c = 0; c < ncols; c++) {
                TableColumn tc = tcm.getColumn(c);
                tc.setCellRenderer(tr);
                tc.setPreferredWidth(100);
                TableColumn tableColumn = table.getColumnModel().getColumn(c);
                int preferredWidth = 100 + tableColumn.getMinWidth();
                int maxWidth = tableColumn.getMaxWidth();
                for (int row1 = 0; row1 < table.getRowCount(); row1++) {
                    TableCellRenderer cellRenderer = table.getCellRenderer(row1, c);
                    Component c1 = table.prepareRenderer(cellRenderer, row1, c);
                    int width = c1.getPreferredSize().width + table.getIntercellSpacing().width;
                    preferredWidth = Math.max(preferredWidth, width);
                    // We've exceeded the maximum width, no need to check other rows
                    if (preferredWidth >= maxWidth) {
                        preferredWidth = maxWidth;
                        break;
                    }
                }
                tableColumn.setPreferredWidth(preferredWidth);
            }
        }
        scrollPane14 = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane14.setVisible(visible);
        this.add(scrollPane14);
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = 1;
        c01.gridx = 0;
        c01.gridy = 0;
        JLabel pBarLabel0 = new JLabel(TABLE_LENGED);
        JPanel lengedpanel = new JPanel(gridbag01);
        lengedpanel.add(pBarLabel0, c01);
        c01.gridx = 2;
        c01.gridy = 0;
        JLabel pBarLabel = new JLabel(MANDATORY_TRANSACTIONS, JLabel.RIGHT);
        lengedpanel.add(pBarLabel, c01);
        c01.gridx = 1;
        c01.gridy = 0;
        JLabel pBarLabel2 = new JLabel("    ", JLabel.LEFT);
        pBarLabel2.setOpaque(true);
        pBarLabel2.setBackground(Color.GREEN);
        lengedpanel.add(pBarLabel2, c01);
        c01.gridx = 4;
        c01.gridy = 0;
        JLabel pBarLabel3 = new JLabel(NON_MAND_CONT_TRAN, JLabel.RIGHT);
        lengedpanel.add(pBarLabel3, c01);
        c01.gridx = 3;
        c01.gridy = 0;
        JLabel pBarLabel4 = new JLabel("    ", JLabel.LEFT);
        pBarLabel4.setOpaque(true);
        pBarLabel4.setBackground(Color.RED);
        lengedpanel.add(pBarLabel4, c01);
        c01.gridx = 6;
        c01.gridy = 0;
        JLabel pBarLabel5 = new JLabel(NON_MAND_NO_CONT_TRAN, JLabel.RIGHT);
        lengedpanel.add(pBarLabel5, c01);
        c01.gridx = 5;
        c01.gridy = 0;
        JLabel pBarLabel6 = new JLabel("    ", JLabel.LEFT);
        pBarLabel6.setOpaque(true);
        pBarLabel6.setBackground(Color.ORANGE);
        lengedpanel.add(pBarLabel6, c01);
        this.add(lengedpanel);
        this.pack();
        this.setVisible(visible);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    public Object[][] getDataDetailedByTask() {
        return dataDetailedByTask;
    }

    public Object[][] getDataHWDelayByTask() {
        return dataHWDelayByTask;
    }
}