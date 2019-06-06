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

import ui.syscams.SysCAMSClock;
import ui.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Class JDialogSysCAMSClock
 * Dialog for managing of SystemC-AMS Clock
 * Creation: 04/06/2019
 * @version 1.0 04/06/2016
 * @author Daniela GENIUS
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSClock extends JDialog implements ActionListener {

	private JTextField nameTextField;
        private JTextField frequencyTextField;
        private JTextField unitTextField;
        private JTextField dutyCycleTextField;
    
	private JTextField startTimeTextField;
	private String listUnitString[];
        private String posFirstString[];
        private JComboBox<String> unitComboBoxString;
        private JComboBox<String> posFirstComboBoxString;
	private SysCAMSClock clock;

	public JDialogSysCAMSClock(SysCAMSClock clock) {
		this.setTitle("Setting Clock Attributes");
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

		this.clock = clock;

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		dialog();
	}

	public void dialog() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		JPanel attributesMainPanel = new JPanel();
		mainPanel.add(attributesMainPanel, BorderLayout.NORTH); 

		attributesMainPanel.setLayout(new BorderLayout());

		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting clock attributes"));

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel attributesBoxPanel = new JPanel();
		attributesBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		attributesBoxPanel.setLayout(gridBag);

		JLabel labelName = new JLabel("Name : ");
		constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelName, constraints);
		attributesBoxPanel.add(labelName);

		if (clock.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(clock.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(nameTextField, constraints);
		attributesBoxPanel.add(nameTextField);


		
		JLabel labelFrequency = new JLabel("Frequency : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelFrequency, constraints);
		attributesBoxPanel.add(labelFrequency);

		if (clock.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			frequencyTextField = new JTextField(clock.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(frequencyTextField, constraints);
		attributesBoxPanel.add(frequencyTextField);


		JLabel labelDutyCycle = new JLabel("DutyCycle : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelDutyCycle, constraints);
		attributesBoxPanel.add(labelDutyCycle);

		if (clock.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			frequencyTextField = new JTextField(clock.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(frequencyTextField, constraints);
		attributesBoxPanel.add(frequencyTextField);
		
		listUnitString = new String[4];
		listUnitString[0] = "s";
		listUnitString[1] = "ms";
		listUnitString[2] = "\u03BCs";
		listUnitString[3] = "ns";
		unitComboBoxString = new JComboBox<String>(listUnitString);
		if (clock.getUnitTemplate().equals("") || clock.getUnitTemplate().equals("s")) {
			unitComboBoxString.setSelectedIndex(0);
		} else if (clock.getUnitTemplate().equals("ms")){
			unitComboBoxString.setSelectedIndex(1);
		} else if (clock.getUnitTemplate().equals("\u03BCs")){
			unitComboBoxString.setSelectedIndex(2);
		} else if (clock.getUnitTemplate().equals("ns")){
			unitComboBoxString.setSelectedIndex(3);
		}
		unitComboBoxString.setActionCommand("unit");
		unitComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(unitComboBoxString, constraints);
		attributesBoxPanel.add(unitComboBoxString);

		posFirstString = new String[2];
		posFirstString[0] = "true";
		posFirstString[1] = "false";
	
		posFirstComboBoxString = new JComboBox<String>(posFirstString);
		if (clock.getPosFirstTemplate().equals("") || clock.getPosFirstTemplate().equals("true")) {
			posFirstComboBoxString.setSelectedIndex(0);
		} else if (clock.getPosFirstTemplate().equals("false")){
			posFirstComboBoxString.setSelectedIndex(1);
		}
		posFirstComboBoxString.setActionCommand("positive edge first");
		posFirstComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(posFirstComboBoxString, constraints);
		attributesBoxPanel.add(posFirstComboBoxString);
		
		attributesBox.add(attributesBoxPanel);

		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); 

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
		pack();
		this.getRootPane().setDefaultButton(saveCloseButton);
	}

	public void actionPerformed(ActionEvent e) {
	    clock.setNameTemplate(nameTextField.getText());		
	    clock.setFrequencyTemplate(frequencyTextField.getText());
	    clock.setDutyCycleTemplate(dutyCycleTextField.getText());
	    clock.setStartTimeTemplate(startTimeTextField.getText());
	    clock.setPosFirstTemplate((String) posFirstComboBoxString.getSelectedItem());
	    clock.setUnitTemplate((String) unitComboBoxString.getSelectedItem());
	    
		if ("Save_Close".equals(e.getActionCommand())) {
			clock.setValue(new String(nameTextField.getText()));
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}
