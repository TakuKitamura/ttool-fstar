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

import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
public class JPanelCompareXmlGraph extends JPanel implements TableModelListener {

	private int[] graphSource;
	public static JTable table, tableUpdated;

	private boolean DEBUG = false;
	private String[] columnNames;
	private Object[][] data;
	static JScrollPane scrollPane = new JScrollPane();

	public JPanelCompareXmlGraph(Vector<SimulationTransaction> transFile1, Vector<SimulationTransaction> transFile2) {

		super(new GridLayout(1, 0));

		if (transFile1.isEmpty() || transFile2.isEmpty()) {
			return;
		}

		// System.out.println("transFile1*****************" + transFile1.size());
		// System.out.println("transFile2*******************" + transFile2.size());
		int maxTime = -1;

		int rowIndex = 0;
		int totalrows = 0;
		Vector<String> deviceNames1 = new Vector();
		Vector<String> deviceNames2 = new Vector();

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

		totalrows = deviceNames1.size() + deviceNames2.size();

		Vector<String> allDevices = new Vector();

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
		// System.out.println("-------------------------- " + "all devices Done" + "
		// ---------------------------------");

		columnNames = new String[maxTime + 2];
		data = new Object[deviceNames1.size() + deviceNames2.size()][maxTime + 2];

		columnNames[0] = "Device Name";
		columnNames[1] = "Trace Name";

		// columnNames[2]="Trace";

		for (SimulationTransaction st : transFile1) {

			for (String dName : deviceNames1) {

				if (st.deviceName.equals(dName)) {
					// maxTime = Integer.parseInt(st.endTime);

					for (int i = 0; i < Integer.parseInt(st.length); i++) {

						// String[] sentences2 = st.command.split("__");
						// for (int j = 0; j < sentences2.length; j++) {
						// String s1 = sentences2[j];
						// System.out.println("st.command : " + st.command);
						// System.out.println("sentences2 : " + sentences2);
						// System.out.println("substring : " + s1);

						// }
						String[] sentences2 = null;

						sentences2 = st.command.split("__");

						if (sentences2[0].contains(",") && sentences2.length > 1)

						{

							String[] writeCommand = sentences2[0].split(",");

							st.command = writeCommand[0] + " " + sentences2[1];

						} else if (sentences2[0].contains(" ") && sentences2.length > 1)

						{

							String[] writeCommand = sentences2[0].split(" ");

							st.command = writeCommand[0] + " " + sentences2[1];

						}

						data[allDevices.indexOf(dName.concat("1"))][Integer.parseInt(st.startTime) + i
								+ 2] = st.command;
						;
						data[allDevices.indexOf(dName.concat("1"))][1] = "transFile 1";
					}
				}

			}

		}
		// System.out.println(
		// "-------------------------- " + "all transactions 1 Done" + "
		// ---------------------------------");

		for (SimulationTransaction st : transFile2) {

			for (String dName : deviceNames2) {

				if (st.deviceName.equals(dName)) {
					// maxTime = Integer.parseInt(st.endTime);

					for (int i = 0; i < Integer.parseInt(st.length); i++) {

						String[] sentences2 = null;

						sentences2 = st.command.split("__");

						if (sentences2[0].contains(",") && sentences2.length > 1)

						{

							String[] writeCommand = sentences2[0].split(",");

							st.command = writeCommand[0] + " " + sentences2[1];

						} else if (sentences2[0].contains(" ") && sentences2.length > 1)

						{

							String[] writeCommand = sentences2[0].split(" ");

							st.command = writeCommand[0] + " " + sentences2[1];

						}

						data[allDevices.indexOf(dName.concat("2"))][Integer.parseInt(st.startTime) + i
								+ 2] = st.command;
						data[allDevices.indexOf(dName.concat("2"))][1] = "transFile 2";
					}
				}

			}

		}

		for (String dName : allDevices) {
			data[allDevices.indexOf(dName)][0] = dName.substring(0, dName.length() - 1);
			;
		}

		// System.out.println("-------------------------- " + maxTime + "
		// ---------------------------------");

		// System.out.println("-------------------------- " + deviceNames1 + "
		// ---------------------------------");

		// System.out.println("----------*****---------------- " + deviceNames2 + "
		// ---------------------------------");

		for (int i = 2; i < maxTime + 2; i++) {

			columnNames[i] = Integer.toString(i - 2);

		}

		// TODO Auto-generated method stub
		table = new JTable(data, columnNames);
		// table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		// table.setBackground(Color.YELLOW);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableRenderer tr = new TableRenderer();
		int nrows = table.getRowCount();
		int ncols = table.getColumnCount();

		table.getModel().addTableModelListener(this);

		TableColumnModel tcm = table.getColumnModel();

		// For each table column, sets its renderer to the previously
		// created table renderer.

		// System.out.println("ncols : " + ncols);

		for (int c = 0; c < ncols; c++) {
			TableColumn tc = tcm.getColumn(c);
			tc.setCellRenderer(tr);
		}

		// table.getCellRenderer(1, 3).setCellRenderer(ColorRenderer());

	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		// System.out.println("Value of data: ");
		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				// System.out.print(" " + model.getValueAt(i, j));
			}
			// System.out.println();
		}
		// System.out.println("--------------------------");
	}

	public void showDifference() {
		// TODO Auto-generated method stub

		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();

		// System.out.println("Value of data: ");

		for (int j = 2; j < numCols; j++) {

			for (int i = 0; i < numRows; i++) {
				for (int k = 0; k < numRows; k++) {

					if (table.getValueAt(i, 0).equals(table.getValueAt(k, 0)))

					{

						if (i != k && table.getValueAt(i, j) != null && table.getValueAt(k, j) != null
								&& table.getValueAt(i, j).equals(table.getValueAt(k, j))) {

							// System.out.print(table.getValueAt(i, j) + " ==" + table.getValueAt(k, j));
							table.setValueAt(null, k, j);
							table.setValueAt(null, i, j);

						}
					}

				}
				// System.out.print(" row " + i + ":");

				// System.out.println();
			}

		}

		numRows = table.getRowCount();
		numCols = table.getColumnCount();

		table.repaint();
		table.revalidate();
		scrollPane.setViewportView(table);
		// SwingUtilities.updateComponentTreeUI(scrollPane);
		scrollPane.setVisible(true);
		// add(scrollPane);
		// table.revalidate();
		// table.repaint();

		scrollPane.revalidate();
		scrollPane.repaint();

		// System.out.println("similar eraised" + numRows + "---" + numCols);

	}

	public void drawTable() {
		// Create the scroll pane and add the table to it.
		// scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		/// JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Add the scroll pane to this panel.

		// scrollPane.setBackground(Color.blue);
		scrollPane.setViewportView(table);

		scrollPane.setVisible(true);
		add(scrollPane);
		// System.out.println("table added :");

	}

	public void updateTable() {

		// table.setModel(new DefaultTableModel());

		// table.tableChanged(new TableModelEvent(table.getModel()));

		scrollPane.setViewportView(table);
		// SwingUtilities.updateComponentTreeUI(scrollPane);
		scrollPane.setVisible(true);
		// add(scrollPane);
		// table.revalidate();
		// table.repaint();

		scrollPane.revalidate();
		scrollPane.repaint();
		// scrollPane.setVisible(true);
		// System.out.println("revalidated");

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}

	public Vector<Object> loadTransacationsDropDown(Object object) {

		// System.out.println(object);
		Vector<Object> allTransacions = new Vector();
		;

		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		for (int i = 0; i < numRows; i++) {
			for (int j = 2; j < numCols; j++) {

				if (table.getValueAt(i, j) != null && !allTransacions.contains(table.getValueAt(i, j))
						&& table.getValueAt(i, 0).equals(object)) {
					allTransacions.add(table.getValueAt(i, j));
				}
			}
			// System.out.println();
		}

		return allTransacions;
	}

	public Vector<Object> loadDevicesDropDown() {
		Vector<Object> allDevices = new Vector();
		;

		int numRows = table.getRowCount();
		// int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		for (int i = 0; i < numRows; i++) {

			if (table.getValueAt(i, 0) != null && !allDevices.contains(table.getValueAt(i, 0))) {
				allDevices.add(table.getValueAt(i, 0));

			}
			// System.out.println();
		}

		return allDevices;
	}

	public JTable getTable() {
		return table;

	}

}

/*
 * 
 * public JPanelCompareXmlGraph() { { graphSource = new int[] { 2, 4, 20, 40
 * ,100, 130 }; setupPanel(); System.out.println(
 * "JPanelCompareXmlGraph called") ; }
 * 
 * private void setupPanel() { // TODO Auto-generated method stub
 * this.setBackground(Color.LIGHT_GRAY); System.out.println(
 * "setupPanel called") ;
 * 
 * 
 * }
 * 
 * @Override protected void paintComponent(Graphics currentGraphics) {
 * super.paintComponent(currentGraphics); System.out.println(
 * "the graph function is called" + this.getHeight()) ; Graphics2D mainGraphics
 * = (Graphics2D) currentGraphics;
 * 
 * for (int index = 0; index < graphSource.length; index=index+2) { //int height
 * = this.getHeight() / graphSource.length; //int width = (int)
 * ((graphSource[index] / 200.00) * this.getWidth()); int height =20; int width=
 * (int)graphSource[index+1] - graphSource[index]; int xPosition =
 * graphSource[index]; int yPosistion = 0;
 * 
 * int red = (int) (Math.random() * 256); int green = (int) (Math.random() *
 * 256); int blue = (int) (Math.random() * 256); int alpha = (int)
 * (Math.random() * 256);
 * 
 * mainGraphics.setColor(new Color(red, green, blue, alpha));
 * 
 * mainGraphics.fill(new Rectangle(xPosition, yPosistion, width, height));
 * System.out.println(index +"--"+xPosition + "--"+ yPosistion+ "--"+ width
 * +"--"+ height) ;
 * 
 * 
 * }
 * 
 * 
 * }
 * 
 */
