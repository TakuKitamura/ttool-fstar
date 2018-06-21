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

package ui.window;

import ui.syscams.*;
import ui.util.IconManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Class JDialogSystemCAMSBlockTDF Dialog for managing of SystemC-AMS TDF Block
 * Creation: 26/04/2018
 * 
 * @version 1.0 26/04/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSBlockTDF extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField periodTextField;
	private String listPeriodString[];
	private JComboBox<String> periodComboBoxString;
	private JTextArea processCodeTextArea;
	private String finalString;

	/** Parameters **/
	private SysCAMSBlockTDF block;

	/** Constructor **/
	public JDialogSysCAMSBlockTDF(SysCAMSBlockTDF block) {
		/** Set JDialog **/
		this.setTitle("Setting TDF Block Attributes");
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

		/** Parameters **/
		this.block = block;

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		dialog();
	}

	public StringBuffer encode(String data) {
		StringBuffer databuf = new StringBuffer(data);
		StringBuffer buffer = new StringBuffer("");
		int endline = 0;
		int nb_arobase = 0;
		int condition = 0;

		for (int pos = 0; pos != data.length(); pos++) {
			char c = databuf.charAt(pos);
			switch (c) {
			case '\n':
				break;
			case '\t':
				break;
			case '{':
				buffer.append("{\n");
				endline = 1;
				nb_arobase++;
				break;
			case '}':
				if (nb_arobase == 1) {
					buffer.append("}\n");
					endline = 0;
				} else {
					int i = nb_arobase;
					while (i >= 1) {
						buffer.append("\t");
						i--;
					}
					buffer.append("}\n");
					endline = 1;
				}
				nb_arobase--;
				break;
			case ';':
				if (condition == 1) {
					buffer.append(";");
				} else {
					buffer.append(";\n");
					endline = 1;
				}
				break;
			case ' ':
				if (endline == 0) {
					buffer.append(databuf.charAt(pos));
				}
				break;
			case '(':
				buffer.append("(");
				condition = 1;
				break;
			case ')':
				buffer.append(")");
				condition = 0;
				break;
			default:
				if (endline == 1) {
					endline = 0;
					int i = nb_arobase;
					while (i >= 1) {
						buffer.append("\t");
						i--;
					}
				}
				buffer.append(databuf.charAt(pos));
				break;
			}
		}
		return buffer;
	}

	public void dialog() {
		/** JPanel **/
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		/** JTabbedPane **/
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel attributesMainPanel = new JPanel();
		JPanel processMainPanel = new JPanel();
		tabbedPane.add("Attributes", attributesMainPanel);
		tabbedPane.add("Process Code", processMainPanel);

		mainPanel.add(tabbedPane, BorderLayout.NORTH); // add tab to main panel

		// --- Attributes GridLayout ---//
		attributesMainPanel.setLayout(new BorderLayout());

		// Box for Attributes
		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting TDF block attributes"));

		// BorderLayout for Adding Attributes
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel attributesBoxPanel = new JPanel();
		attributesBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		attributesBoxPanel.setLayout(gridBag);

		JLabel labelName = new JLabel("Name : ");
		constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelName, constraints);
		attributesBoxPanel.add(labelName);

		if (block.getValue().toString().equals("")) { // name empty
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(block.getValue().toString(), 10); // name not empty
		}
		constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(nameTextField, constraints);
		attributesBoxPanel.add(nameTextField);

		JLabel periodLabel = new JLabel("Period Tp : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(periodLabel, constraints);
		attributesBoxPanel.add(periodLabel);

		if (block.getPeriod() == -1) { // name empty // port.getName().toString().equals("") ||
			periodTextField = new JTextField(10);
		} else {
			periodTextField = new JTextField("" + block.getPeriod(), 10); // name not empty
		}
		constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(periodTextField, constraints);
		attributesBoxPanel.add(periodTextField);

		listPeriodString = new String[3];
		listPeriodString[0] = "us";
		listPeriodString[1] = "ms";
		listPeriodString[2] = "s";
		periodComboBoxString = new JComboBox<String>(listPeriodString);
		if (block.getTime().equals("") || block.getTime().equals("us")) {
			periodComboBoxString.setSelectedIndex(0);
		} else if (block.getTime().equals("ms")) {
			periodComboBoxString.setSelectedIndex(1);
		} else {
			periodComboBoxString.setSelectedIndex(2);
		}
		periodComboBoxString.setActionCommand("time");
		periodComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(periodComboBoxString, constraints);
		attributesBoxPanel.add(periodComboBoxString);

		attributesBox.add(attributesBoxPanel); // add border to box

		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); // add box to grid

		// Down Side
		JPanel downPanel = new JPanel(new FlowLayout());

		JButton saveCloseButton = new JButton("Save and close");
		saveCloseButton.setIcon(IconManager.imgic25);
		saveCloseButton.setActionCommand("Save_Close");
		saveCloseButton.addActionListener(this);
		saveCloseButton.setPreferredSize(new Dimension(200, 30));
		downPanel.add(saveCloseButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setIcon(IconManager.imgic27);
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setPreferredSize(new Dimension(200, 30));
		downPanel.add(cancelButton);

		mainPanel.add(downPanel, BorderLayout.CENTER);
		this.getRootPane().setDefaultButton(saveCloseButton);

		// --- ProcessCode BorderLayout ---//
		processMainPanel.setLayout(new BorderLayout());

		Box codeBox = Box.createVerticalBox();
		codeBox.setBorder(BorderFactory.createTitledBorder("Generating code"));

		JPanel codeBoxPanel = new JPanel(new BorderLayout());

		codeBoxPanel.add(new JLabel("Behavior function of TDF block : "), BorderLayout.NORTH);

		StringBuffer stringbuf = encode(block.getProcessCode());
		String beginString = stringbuf.toString();
		finalString = beginString.replaceAll("\t}", "}");

		processCodeTextArea = new JTextArea(finalString);
		processCodeTextArea.setSize(100, 100);
		processCodeTextArea.setTabSize(2);

		processCodeTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
		processCodeTextArea.setLineWrap(true);
		processCodeTextArea.setWrapStyleWord(true);

		JScrollPane processScrollPane = new JScrollPane(processCodeTextArea);
		processScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		processScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		processScrollPane.setPreferredSize(new Dimension(200, 150));

		codeBoxPanel.add(processScrollPane, BorderLayout.SOUTH);

		codeBox.add(codeBoxPanel);
		processMainPanel.add(codeBox, BorderLayout.PAGE_START);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if ("Save_Close".equals(e.getActionCommand())) {
			/** Save the name of the block into listNameTDF **/
			block.setValue(new String(nameTextField.getText()));

			/** Save the period of the block into listPeriodTmTDF **/
			if (!(periodTextField.getText().isEmpty())) {
				Boolean periodValueInteger = false;
				try {
					Integer.parseInt(periodTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "Period Tm is not a Integer", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					periodValueInteger = true;
				}
				if (periodValueInteger == false) {
					block.setPeriod(Integer.parseInt(periodTextField.getText()));
				}
			} else {
				block.setPeriod(-1);
			}

			/** Save the process code into listProcessCodeTDF **/
			block.setProcessCode(processCodeTextArea.getText());

			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}

