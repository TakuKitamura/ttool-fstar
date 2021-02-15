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
package tmltranslator.simulation;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Class JPanelCompareXmlGraph : arranges the two simulation traces in a table
 * and load the table to the compare pop up . It includes functionality to show
 * the difference in the simulation traces and to load the drop down for latency
 * compare later
 * 
 * Creation: 19/07/2019
 * 
 * @author Maysam ZOOR
 */
public class CompareSimulationTrace implements TableModelListener {
    private static JTable table;
    private String[] columnNames;
    private Object[][] data;
    private static final String DEVICE_NAME = "Device Name";
    private static final String TRACE_NAME = "Trace Name";
    private static final String TRANS_FILE_2 = "transFile 2";
    private static final String TRANS_FILE_1 = "transFile 1";

    public TableModel JPanelCompareXmlGraph(Vector<SimulationTransaction> transFile1, Vector<SimulationTransaction> transFile2) {
        // super(new GridLayout(1, 0));
        if (!transFile1.isEmpty() || !transFile2.isEmpty()) {
            int maxTime = -1;
            Vector<String> deviceNames1 = new Vector<String>();
            Vector<String> deviceNames2 = new Vector<String>();
            for (SimulationTransaction st : transFile1) {
                if (Integer.parseInt(st.endTime) > maxTime) {
                    maxTime = Integer.parseInt(st.endTime);
                }
                if (!deviceNames1.contains(st.deviceName)) {
                    deviceNames1.add(st.deviceName);
                }
            }
            for (SimulationTransaction st : transFile2) {
                if (Integer.parseInt(st.endTime) > maxTime) {
                    maxTime = Integer.parseInt(st.endTime);
                }
                if (!deviceNames2.contains(st.deviceName)) {
                    deviceNames2.add(st.deviceName);
                }
            }
            Vector<String> allDevices = new Vector<String>();
            for (String device : deviceNames1) {
                if (!deviceNames2.contains(device)) {
                    allDevices.add(device.concat("1"));
                } else {
                    allDevices.add(device.concat("1"));
                    allDevices.add(device.concat("2"));
                }
            }
            for (String device : deviceNames2) {
                if (!deviceNames1.contains(device)) {
                    allDevices.add(device.concat("2"));
                }
            }
            columnNames = new String[maxTime + 2];
            data = new Object[deviceNames1.size() + deviceNames2.size()][maxTime + 2];
            columnNames[0] = DEVICE_NAME;
            columnNames[1] = TRACE_NAME;
            for (SimulationTransaction st : transFile1) {
                for (String dName : deviceNames1) {
                    if (st.deviceName.equals(dName)) {
                        for (int i = 0; i < Integer.parseInt(st.length); i++) {
                            String[] sentences2 = null;
                            sentences2 = st.command.split("__");
                            if (sentences2[0].contains(",") && sentences2.length > 1) {
                                String[] writeCommand = sentences2[0].split(",");
                                st.command = writeCommand[0] + " " + sentences2[1];
                            } else if (sentences2[0].contains(" ") && sentences2.length > 1) {
                                String[] writeCommand = sentences2[0].split(" ");
                                st.command = writeCommand[0] + " " + sentences2[1];
                            }
                            data[allDevices.indexOf(dName.concat("1"))][Integer.parseInt(st.startTime) + i + 2] = st.command;
                            ;
                            data[allDevices.indexOf(dName.concat("1"))][1] = TRANS_FILE_1;
                        }
                    }
                }
            }
            for (SimulationTransaction st : transFile2) {
                for (String dName : deviceNames2) {
                    if (st.deviceName.equals(dName)) {
                        for (int i = 0; i < Integer.parseInt(st.length); i++) {
                            String[] sentences2 = null;
                            sentences2 = st.command.split("__");
                            if (sentences2[0].contains(",") && sentences2.length > 1) {
                                String[] writeCommand = sentences2[0].split(",");
                                st.command = writeCommand[0] + " " + sentences2[1];
                            } else if (sentences2[0].contains(" ") && sentences2.length > 1) {
                                String[] writeCommand = sentences2[0].split(" ");
                                st.command = writeCommand[0] + " " + sentences2[1];
                            }
                            data[allDevices.indexOf(dName.concat("2"))][Integer.parseInt(st.startTime) + i + 2] = st.command;
                            data[allDevices.indexOf(dName.concat("2"))][1] = TRANS_FILE_2;
                        }
                    }
                }
            }
            for (String dName : allDevices) {
                data[allDevices.indexOf(dName)][0] = dName.substring(0, dName.length() - 1);
                ;
            }
            for (int i = 2; i < maxTime + 2; i++) {
                columnNames[i] = Integer.toString(i - 2);
            }
            table = new JTable(data, columnNames);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
        return table.getModel();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    public JTable getTable() {
        return table;
    }

    public static void setTable(TableModel tableModel) {
        table.setModel(tableModel);
    }
}
