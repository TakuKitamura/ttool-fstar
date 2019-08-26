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
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class JFrameShowLatencyDetails : open the latency popup with all the related
 * tables for the chosen transactions, these tables include the start time and
 * end time for every row in the table.
 * 
 * a simple function will subtract the end from start time when you click on the
 * table cells
 * 
 * Creation: 19/07/2019
 * 
 * @author Maysam ZOOR
 */

public class JFrameShowLatencyDetails extends JFrame {
	private String[] columnNames = new String[3];
	private Object[][] dataTrans1Run1;
	private Object[][] dataTrans1Run2;
	private Object[][] dataTrans2Run1;
	private Object[][] dataTrans2Run2;
	private Vector<SimulationTransaction> trans1Run1 = new Vector<SimulationTransaction>();
	private Vector<SimulationTransaction> trans1Run2 = new Vector<SimulationTransaction>();
	private Vector<SimulationTransaction> trans2Run1 = new Vector<SimulationTransaction>();
	private Vector<SimulationTransaction> trans2Run2 = new Vector<SimulationTransaction>();
	private JLabel labStart, labEnd, labTotal;

	public static JTable table11, table12, table21, table22;

	public static JTable getTable11() {
		return table11;
	}

	public static JTable getTable12() {
		return table12;
	}

	public static JTable getTable21() {
		return table21;
	}

	public static JTable getTable22() {
		return table22;
	}

	public JFrameShowLatencyDetails(Vector<SimulationTransaction> transFile1, Vector<SimulationTransaction> transFile2,
			Object selectedDevice1, Object selectedTrans1, Object selectedDevice2, Object selectedTrans2) {

		super("Simulation Latency ");

		GridLayout myLayout = new GridLayout(4, 1);

		this.setBackground(Color.RED);
		this.setLayout(myLayout);

		columnNames[0] = "transaction ";
		columnNames[1] = "Start Time ";
		columnNames[2] = "End Time ";

		for (SimulationTransaction st : transFile1) {
			if (st.command.equals(selectedTrans1) && st.deviceName.equals(selectedDevice1)) {

				trans1Run1.add(st);

			} else if (st.command.equals(selectedTrans2) && st.deviceName.equals(selectedDevice2)) {
				trans2Run1.add(st);
			}

		}

		for (SimulationTransaction st : transFile2) {
			if (st.command.equals(selectedTrans1) && st.deviceName.equals(selectedDevice1)) {
				trans1Run2.add(st);
			} else if (st.command.equals(selectedTrans2) && st.deviceName.equals(selectedDevice2)) {
				trans2Run2.add(st);

			}

		}

		dataTrans1Run1 = new Object[trans1Run1.size()][3];
		dataTrans1Run2 = new Object[trans1Run2.size()][3];
		dataTrans2Run1 = new Object[trans2Run1.size()][3];
		dataTrans2Run2 = new Object[trans2Run2.size()][3];

		int num11 = 0;
		int num12 = 0;
		int num21 = 0;
		int num22 = 0;

		for (SimulationTransaction st : trans1Run1) {

			dataTrans1Run1[num11][0] = selectedTrans1;
			dataTrans1Run1[num11][1] = st.startTime;
			dataTrans1Run1[num11][2] = st.endTime;
			num11++;

		}
		for (SimulationTransaction st : trans2Run1) {

			{
				dataTrans2Run1[num21][0] = selectedTrans2;
				dataTrans2Run1[num21][1] = st.startTime;
				dataTrans2Run1[num21][2] = st.endTime;
				num21++;

			}

		}

		for (SimulationTransaction st : trans1Run2) {
			dataTrans1Run2[num12][0] = selectedTrans1;
			dataTrans1Run2[num12][1] = st.startTime;
			dataTrans1Run2[num12][2] = st.endTime;
			num12++;

		}
		for (SimulationTransaction st : trans2Run2) {
			dataTrans2Run2[num22][0] = selectedTrans2;
			dataTrans2Run2[num22][1] = st.startTime;
			dataTrans2Run2[num22][2] = st.endTime;
			num22++;

		}

		table11 = new JTable(dataTrans1Run1, columnNames);

		table12 = new JTable(dataTrans1Run2, columnNames);
		table21 = new JTable(dataTrans2Run1, columnNames);

		table22 = new JTable(dataTrans2Run2, columnNames);
		table11.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table12.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table21.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table22.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table11.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int column = table11.getSelectedColumn();
				int row = table11.getSelectedRow();
				String value = table11.getModel().getValueAt(row, column).toString();

				if (column == 1) {

					labStart.setText(value);
				} else if (column == 2) {
					labEnd.setText(value);
				}

				if (!labStart.getText().equals(null) && !labEnd.getText().equals(null) && !labEnd.getText().isEmpty()
						&& !labStart.getText().isEmpty()) {
					int difference = Integer.parseInt(labEnd.getText()) - Integer.parseInt(labStart.getText());
					labTotal.setText(Integer.toString(difference));
				}

			}

		});

		table12.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int column = table12.getSelectedColumn();
				int row = table12.getSelectedRow();
				String value = table12.getModel().getValueAt(row, column).toString();

				if (column == 1) {

					labStart.setText(value);
				} else if (column == 2) {
					labEnd.setText(value);
				}

				if (!labStart.getText().equals(null) && !labEnd.getText().equals(null) && !labEnd.getText().isEmpty()
						&& !labStart.getText().isEmpty()) {
					int difference = Integer.parseInt(labEnd.getText()) - Integer.parseInt(labStart.getText());
					labTotal.setText(Integer.toString(difference));
				}

			}

		});
		table21.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {

				int column = table21.getSelectedColumn();
				int row = table21.getSelectedRow();
				String value = table21.getModel().getValueAt(row, column).toString();

				if (column == 1) {

					labStart.setText(value);
				} else if (column == 2) {
					labEnd.setText(value);
				}

				if (!labStart.getText().equals(null) && !labEnd.getText().equals(null) && !labEnd.getText().isEmpty()
						&& !labStart.getText().isEmpty()) {
					int difference = Integer.parseInt(labEnd.getText()) - Integer.parseInt(labStart.getText());
					labTotal.setText(Integer.toString(difference));
				}

			}

		});
		table22.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {

				int column = table22.getSelectedColumn();
				int row = table22.getSelectedRow();
				String value = table22.getModel().getValueAt(row, column).toString();

				if (column == 1) {

					labStart.setText(value);
				} else if (column == 2) {
					labEnd.setText(value);
				}

				if (!labStart.getText().equals(null) && !labEnd.getText().equals(null) && !labEnd.getText().isEmpty()
						&& !labStart.getText().isEmpty()) {
					int difference = Integer.parseInt(labEnd.getText()) - Integer.parseInt(labStart.getText());
					labTotal.setText(Integer.toString(difference));
				}

			}

		});

		JScrollPane scrollPane11 = new JScrollPane();
		scrollPane11.setViewportView(table11);
		scrollPane11.setVisible(true);
		scrollPane11.setBorder(new javax.swing.border.TitledBorder("First Model: " + selectedTrans1.toString()));
		add(scrollPane11);

		JScrollPane scrollPane12 = new JScrollPane();
		scrollPane12.setViewportView(table12);
		scrollPane12.setVisible(true);
		scrollPane12.setBorder(new javax.swing.border.TitledBorder("Second Model: " + selectedTrans1.toString()));
		add(scrollPane12);

		JScrollPane scrollPane21 = new JScrollPane();
		scrollPane21.setViewportView(table21);
		scrollPane21.setVisible(true);
		scrollPane21.setBorder(new javax.swing.border.TitledBorder("First Model: " + selectedTrans2.toString()));
		add(scrollPane21);

		JScrollPane scrollPane22 = new JScrollPane();
		scrollPane22.setViewportView(table22);
		scrollPane22.setVisible(true);
		scrollPane22.setBorder(new javax.swing.border.TitledBorder("Second Model: " + selectedTrans2.toString()));
		add(scrollPane22);

		JPanel timePanel = new JPanel(new GridBagLayout()); // use FlowLayout
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NORTHWEST;

		JLabel startTime = new JLabel("Start Time", JLabel.LEFT);

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		timePanel.add(startTime, c);

		labStart = new JLabel("", JLabel.LEFT);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		timePanel.add(labStart, c);

		JLabel endTime = new JLabel("End Time", JLabel.LEFT);

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		timePanel.add(endTime, c);

		labEnd = new JLabel("", JLabel.LEFT);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		timePanel.add(labEnd, c);

		JLabel diffTime = new JLabel("Time Difference", JLabel.LEFT);

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		timePanel.add(diffTime, c);

		labTotal = new JLabel("", JLabel.LEFT);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		timePanel.add(labTotal, c);

		this.add(timePanel);
		this.pack();
		this.setVisible(true);

	}

}
