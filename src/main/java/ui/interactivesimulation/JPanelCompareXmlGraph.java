package ui.interactivesimulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class JPanelCompareXmlGraph extends JPanel {

	private int[] graphSource;

	private boolean DEBUG = false;

	public JPanelCompareXmlGraph(Vector<SimulationTransaction> transFile1, Vector<SimulationTransaction> transFile2) {
		super(new GridLayout(1, 0));

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
		System.out.println("-------------------------- " + "all devices Done" + " ---------------------------------");

		String[] columnNames = new String[maxTime + 2];
		Object[][] data = new Object[deviceNames1.size() + deviceNames2.size()][maxTime + 2];

		columnNames[0] = "Device Name";
		columnNames[1] = "Trace Name";

		// columnNames[2]="Trace";

		for (SimulationTransaction st : transFile1) {

			for (String dName : deviceNames1) {

				if (st.deviceName.equals(dName)) {
					// maxTime = Integer.parseInt(st.endTime);

					for (int i = 0; i < Integer.parseInt(st.length); i++) {

						data[allDevices.indexOf(dName.concat("1"))][Integer.parseInt(st.startTime) + i
								+ 2] = st.command;
						data[allDevices.indexOf(dName.concat("1"))][1] = "transFile 1";
					}
				}

			}

		}
		System.out.println(
				"-------------------------- " + "all transactions 1 Done" + " ---------------------------------");

		for (SimulationTransaction st : transFile2) {

			for (String dName : deviceNames2) {

				if (st.deviceName.equals(dName)) {
					// maxTime = Integer.parseInt(st.endTime);

					for (int i = 0; i < Integer.parseInt(st.length); i++) {

						data[allDevices.indexOf(dName.concat("2"))][Integer.parseInt(st.startTime) + i
								+ 2] = st.command;
						data[allDevices.indexOf(dName.concat("2"))][1] = "transFile 2";
					}
				}

			}

		}

		System.out.println(
				"-------------------------- " + "all transactions 2 Done" + " ---------------------------------");

		for (String dName : allDevices) {
			data[allDevices.indexOf(dName)][0] = dName.substring(0, dName.length() - 1);
			;
		}

		System.out.println("-------------------------- " + maxTime + " ---------------------------------");

		System.out.println("-------------------------- " + deviceNames1 + " ---------------------------------");

		System.out.println("----------*****---------------- " + deviceNames2 + " ---------------------------------");

		for (int i = 2; i < maxTime + 2; i++) {

			columnNames[i] = Integer.toString(i - 2);

		}

		final JTable table = new JTable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		// table.setBackground(Color.YELLOW);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableRenderer tr = new TableRenderer();
		int nrows = table.getRowCount();
		int ncols = table.getColumnCount();
		
		 TableColumnModel tcm = table.getColumnModel ();

		   // For each table column, sets its renderer to the previously 
		   // created table renderer.

			System.out.println("ncols :  " + ncols );

		   for (int c = 0; c < ncols; c++)
		   {
		      TableColumn tc = tcm.getColumn (c);
		      tc.setCellRenderer (tr);
		   }

		// table.getCellRenderer(1, 3).setCellRenderer(ColorRenderer());

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Add the scroll pane to this panel.

		// scrollPane.setBackground(Color.blue);

		scrollPane.setVisible(true);
		add(scrollPane);
		System.out.println("table added :");
	}

	private TableCellRenderer ColorRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
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
