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

package ui.directedgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import tmltranslator.HwLink;
import tmltranslator.TMLMapping;
import ui.TGComponent;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;

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
    JScrollPane scrollPane12, scrollPane13, scrollPane14;
    public static JTable taskNames, hardwareNames;
    private Object[][] dataDetailedByTask;
    private String[] columnNames;
    List<String> onPathBehavior = new ArrayList<String>();
    List<String> offPathBehavior = new ArrayList<String>();
    List<String> offPathBehaviorCausingDelay = new ArrayList<String>();

    public Object[][] getDataDetailedByTask() {
        return dataDetailedByTask;
    }

    public Object[][] getDataHWDelayByTask() {
        return dataHWDelayByTask;
    }

    private Object[][] dataHWDelayByTask;

    public JFrameLatencyDetailedPopup(DirectedGraphTranslator dgraph, int row, boolean firstTable) {

        super("Detailed Latency By Row");

        GridLayout myLayout = new GridLayout(3, 1);

        this.setLayout(myLayout);

        columnByTaskNames[0] = "Transaction List";
        columnByTaskNames[1] = "Transaction Diagram Name ";
        columnByTaskNames[2] = "Hardware ";
        columnByTaskNames[3] = "Start Time";
        columnByTaskNames[4] = "End Time ";

        JPanel jp04 = new JPanel(new BorderLayout());
        if (firstTable) {

            dataDetailedByTask = dgraph.getTaskByRowDetails(row);

        } else {
            dgraph.getRowDetailsMinMax(row);

            dataDetailedByTask = dgraph.getTasksByRowMinMax(row);

        }
        DefaultTableModel model = new DefaultTableModel(dataDetailedByTask, columnByTaskNames) {
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

        JTable taskNames = new JTable(model);
        taskNames.setAutoCreateRowSorter(true);
        scrollPane12 = new JScrollPane(taskNames, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane12.setVisible(true);

        this.add(scrollPane12);

        columnByHWNames[0] = "Task on Same device";
        columnByHWNames[1] = "Transaction Diagram Name ";
        columnByHWNames[2] = "Hardware ";
        columnByHWNames[3] = "Start Time";
        columnByHWNames[4] = "End Time ";

        if (firstTable) {

            dataHWDelayByTask = dgraph.getTaskHWByRowDetails(row);

        } else {
            dataHWDelayByTask = dgraph.getTaskHWByRowDetailsMinMax(row);
        }

        DefaultTableModel model2 = new DefaultTableModel(dataHWDelayByTask, columnByHWNames) {
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

        JTable hardwareNames = new JTable(model2);
        hardwareNames.setAutoCreateRowSorter(true);

        scrollPane13 = new JScrollPane(hardwareNames, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane13.setVisible(true);
        this.add(scrollPane13);

        int maxTime = -1;

        int minTime = Integer.MAX_VALUE;

        Vector<String> deviceNames1 = new Vector<String>();

        if (firstTable) {

            for (SimulationTransaction st : dgraph.getRowDetailsTaks(row)) {
                if (Integer.parseInt(st.endTime) > maxTime) {
                    maxTime = Integer.parseInt(st.endTime);
                }
                if (Integer.parseInt(st.startTime) < minTime) {
                    minTime = Integer.parseInt(st.startTime);
                }
                if (!deviceNames1.contains(st.deviceName)) {
                    deviceNames1.add(st.deviceName);

                }

            }

            for (SimulationTransaction st : dgraph.getRowDetailsByHW(row)) {
                if (Integer.parseInt(st.endTime) > maxTime) {
                    maxTime = Integer.parseInt(st.endTime);
                }
                if (Integer.parseInt(st.startTime) < minTime) {
                    minTime = Integer.parseInt(st.startTime);
                }
                if (!deviceNames1.contains(st.deviceName)) {
                    deviceNames1.add(st.deviceName);

                }

            }

            int timeInterval = (maxTime - minTime);
            columnNames = new String[timeInterval + 1];

            columnNames[0] = "Device Name";
            for (int i = 0; i < timeInterval; i++) {

                columnNames[i + 1] = Integer.toString(minTime + i);

            }

            dataDetailedByTask = new Object[deviceNames1.size()][timeInterval + 1];

            for (SimulationTransaction st : dgraph.getRowDetailsTaks(row)) {

                for (String dName : deviceNames1) {

                    if (st.deviceName.equals(dName)) {

                        for (int i = 0; i < Integer.parseInt(st.length); i++) {

                            int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
                            dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
                            ;

                            onPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);

                        }
                    }

                }

            }

            HashMap<String, ArrayList<ArrayList<Integer>>> delayTime = dgraph.getRowDelayDetailsByHW(row);

            for (SimulationTransaction st : dgraph.getRowDetailsByHW(row)) {

                for (String dName : deviceNames1) {

                    if (st.deviceName.equals(dName)) {

                        for (int i = 0; i < Integer.parseInt(st.length); i++) {

                            int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
                            dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
                            ;

                            boolean causeDelay = false;

                            if (delayTime.containsKey(st.deviceName)) {

                                for (Entry<String, ArrayList<ArrayList<Integer>>> entry : delayTime.entrySet()) {
                                    if (entry.getKey().equals(st.deviceName)) {
                                        ArrayList<ArrayList<Integer>> timeList = entry.getValue();

                                        for (int j = 0; j < timeList.size(); j++) {
                                            
                                            if (Integer.valueOf(st.startTime) >= timeList.get(j).get(0)
                                                    && Integer.valueOf(st.startTime) <= timeList.get(j).get(1)) {
                                               
                                                causeDelay = true;

                                            }
                                        }

                                    }

                                }

                            }

                            if (causeDelay) {
                                offPathBehaviorCausingDelay.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
                            } else {
                                offPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
                            }

                        }
                    }

                }

            }

            for (String dName : deviceNames1) {
                dataDetailedByTask[deviceNames1.indexOf(dName)][0] = dName;
                ;
            }
        } else {
            // min/max table row selected

            for (SimulationTransaction st : dgraph.getMinMaxTasksByRow(row)) {
                if (Integer.parseInt(st.endTime) > maxTime) {
                    maxTime = Integer.parseInt(st.endTime);
                }
                if (Integer.parseInt(st.startTime) < minTime) {
                    minTime = Integer.parseInt(st.startTime);
                }
                if (!deviceNames1.contains(st.deviceName)) {
                    deviceNames1.add(st.deviceName);

                }

            }

            for (SimulationTransaction st : dgraph.getTaskMinMaxHWByRowDetails(row)) {
                if (Integer.parseInt(st.endTime) > maxTime) {
                    maxTime = Integer.parseInt(st.endTime);
                }
                if (Integer.parseInt(st.startTime) < minTime) {
                    minTime = Integer.parseInt(st.startTime);
                }
                if (!deviceNames1.contains(st.deviceName)) {
                    deviceNames1.add(st.deviceName);

                }

            }

            int timeInterval = (maxTime - minTime);
            columnNames = new String[timeInterval + 1];

            columnNames[0] = "Device Name";
            for (int i = 0; i < timeInterval; i++) {

                columnNames[i + 1] = Integer.toString(minTime + i);

            }

            dataDetailedByTask = new Object[deviceNames1.size()][timeInterval + 1];

            for (SimulationTransaction st : dgraph.getMinMaxTasksByRow(row)) {

                for (String dName : deviceNames1) {

                    if (st.deviceName.equals(dName)) {

                        for (int i = 0; i < Integer.parseInt(st.length); i++) {

                            int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
                            dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
                            ;

                            onPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);

                        }
                    }

                }

            }

            for (SimulationTransaction st : dgraph.getTaskMinMaxHWByRowDetails(row)) {

                for (String dName : deviceNames1) {

                    if (st.deviceName.equals(dName)) {

                        for (int i = 0; i < Integer.parseInt(st.length); i++) {

                            int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
                            dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
                            ;

                            offPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);

                        }
                    }

                }

            }

            for (String dName : deviceNames1) {
                dataDetailedByTask[deviceNames1.indexOf(dName)][0] = dName;
                ;
            }

        }

        JTable table = new JTable(dataDetailedByTask, columnNames);
        table.setFillsViewportHeight(true);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.setAutoCreateRowSorter(true);

        TableRenderer tr = new TableRenderer(onPathBehavior, offPathBehaviorCausingDelay, offPathBehavior);
        int ncols = table.getColumnCount();

        table.getModel().addTableModelListener(this);

        TableColumnModel tcm = table.getColumnModel();

        for (int c = 0; c < ncols; c++) {
            TableColumn tc = tcm.getColumn(c);
            tc.setCellRenderer(tr);
        }

        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = 100 + tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row1 = 0; row1 < table.getRowCount(); row1++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row1, column);
                Component c = table.prepareRenderer(cellRenderer, row1, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                // We've exceeded the maximum width, no need to check other rows

                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }

        scrollPane14 = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane14.setVisible(true);
        this.add(scrollPane14);

        this.pack();
        this.setVisible(true);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // TODO Auto-generated method stub

    }

}
