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

import ui.eln.sca_eln.ELNComponentCapacitor;
import ui.util.IconManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Class JDialogELNComponentCapacitor
 * Dialog for managing of ELN capacitor
 * Creation: 12/06/2018
 * @version 1.0 12/06/2018
 * @author Irina Kit Yan LEE
*/

@SuppressWarnings("serial")

public class JDialogELNComponentCapacitor extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField valueTextField;
	private JTextField chargeTextField;
	private String valueListString[];
	private JComboBox<String> valueComboBoxString;
	private String chargeListString[];
	private JComboBox<String> chargeComboBoxString;

	/** Parameters **/
	private ELNComponentCapacitor c;

	/* Constructor **/
	public JDialogELNComponentCapacitor(ELNComponentCapacitor _c) {
		/** Set JDialog **/
		setTitle("Setting the capacitor");
		setLocationRelativeTo(null);
		setVisible(true);
		setAlwaysOnTop(true);
		setResizable(false);

		/** Parameters **/
		c = _c;
		
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
        	public void actionPerformed(ActionEvent e) {
                dispose();
        	}
        });
		
		dialog();
	}

	public void dialog() {
		/** JPanel **/
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);
		
		JPanel attributesMainPanel = new JPanel(new GridLayout());
		mainPanel.add(attributesMainPanel, BorderLayout.NORTH);
		
		// Left Side
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createTitledBorder("Setting capacitor attributes"));

		GridBagLayout gridBag = new GridBagLayout();
	    GridBagConstraints constraints = new GridBagConstraints();
	    JPanel boxPanel = new JPanel();
	    boxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
	    boxPanel.setLayout(gridBag); 
	    
	    JLabel labelName = new JLabel("nm : ");
	    constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(labelName, constraints);
	    boxPanel.add(labelName);

		nameTextField = new JTextField(c.getValue().toString(), 10); // name not empty
	    constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(nameTextField, constraints);
	    boxPanel.add(nameTextField);
	   
		JLabel valueLabel = new JLabel("value : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(valueLabel, constraints);
	    boxPanel.add(valueLabel);

		valueTextField = new JTextField("" + c.getVal(), 10); // name not empty
	    constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(valueTextField, constraints);
	    boxPanel.add(valueTextField);
	    
	    valueListString = new String[9];
	    valueListString[0] = "GF";
	    valueListString[1] = "MF";
	    valueListString[2] = "kF";
	    valueListString[3] = "F";
	    valueListString[4] = "mF";
	    valueListString[5] = "\u03BCF";
	    valueListString[6] = "nF";
	    valueListString[7] = "pF";
	    valueListString[8] = "fF";
		valueComboBoxString = new JComboBox<String>(valueListString);
		if (c.getUnit0().equals("GF")) {
			valueComboBoxString.setSelectedIndex(0);
		} else if (c.getUnit0().equals("MF")) {
			valueComboBoxString.setSelectedIndex(1);
		} else if (c.getUnit0().equals("kF")) {
			valueComboBoxString.setSelectedIndex(2);
		} else if (c.getUnit0().equals("F")) {
			valueComboBoxString.setSelectedIndex(3);
		} else if (c.getUnit0().equals("mF")) {
			valueComboBoxString.setSelectedIndex(4);
		} else if (c.getUnit0().equals("\u03BCF")) {
			valueComboBoxString.setSelectedIndex(5);
		} else if (c.getUnit0().equals("nF")) {
			valueComboBoxString.setSelectedIndex(6);
		} else if (c.getUnit0().equals("pF")) {
			valueComboBoxString.setSelectedIndex(7);
		} else if (c.getUnit0().equals("fF")) {
			valueComboBoxString.setSelectedIndex(8);
		}
		valueComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(valueComboBoxString, constraints);
	    boxPanel.add(valueComboBoxString);
	    
	    JLabel chargeLabel = new JLabel("q0 : ");
	    constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(chargeLabel, constraints);
	    boxPanel.add(chargeLabel);
	    
	    chargeTextField = new JTextField("" + c.getQ0(), 10); // name not empty
	    constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(chargeTextField, constraints);
	    boxPanel.add(chargeTextField);
	    
	    chargeListString = new String[9];
	    chargeListString[0] = "GC";
	    chargeListString[1] = "MC";
	    chargeListString[2] = "kC";
	    chargeListString[3] = "C";
	    chargeListString[4] = "mC";
	    chargeListString[5] = "\u03BCC";
	    chargeListString[6] = "nC";
	    chargeListString[7] = "pC";
	    chargeListString[8] = "fC";
	    chargeComboBoxString = new JComboBox<String>(chargeListString);
	    if (c.getUnit1().equals("GC")) {
	    	chargeComboBoxString.setSelectedIndex(0);
	    } else if (c.getUnit1().equals("MC")) {
	    	chargeComboBoxString.setSelectedIndex(1);
	    } else if (c.getUnit1().equals("kC")) {
	    	chargeComboBoxString.setSelectedIndex(2);
	    } else if (c.getUnit1().equals("C")) {
	    	chargeComboBoxString.setSelectedIndex(3);
	    } else if (c.getUnit1().equals("mC")) {
	    	chargeComboBoxString.setSelectedIndex(4);
	    } else if (c.getUnit1().equals("\u03BCC")) {
	    	chargeComboBoxString.setSelectedIndex(5);
	    } else if (c.getUnit1().equals("nC")) {
	    	chargeComboBoxString.setSelectedIndex(6);
	    } else if (c.getUnit1().equals("pC")) {
	    	chargeComboBoxString.setSelectedIndex(7);
	    } else if (c.getUnit1().equals("fC")) {
	    	chargeComboBoxString.setSelectedIndex(8);
	    }
	    chargeComboBoxString.addActionListener(this);
	    constraints = new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(chargeComboBoxString, constraints);
	    boxPanel.add(chargeComboBoxString);
	    
		box.add(boxPanel); // add border to box
		attributesMainPanel.add(box); // add grid to grid

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
		pack();
		this.getRootPane().setDefaultButton(saveCloseButton);
	}

	public void actionPerformed(ActionEvent e) {
		if ("Save_Close".equals(e.getActionCommand())) {
			c.setValue(new String(nameTextField.getText()));

			if (!(valueTextField.getText().isEmpty())) {
				Boolean valValueDouble = false;
				try {
					Double.parseDouble(valueTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The capacitance is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					valValueDouble = true;
				}
				if (valValueDouble == false && Double.parseDouble(valueTextField.getText()) != 0.0) {
					c.setVal(Double.parseDouble(valueTextField.getText()));
				} else if (valValueDouble == false && Double.parseDouble(valueTextField.getText()) == 0.0) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The capacitance shall not be numerically zero", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					valValueDouble = true;
					c.setVal(1.0);
				}
			} else {
				c.setVal(1.0);
			}
			c.setUnit0((String) valueComboBoxString.getSelectedItem());
			
			if (!(chargeTextField.getText().isEmpty())) {
				Boolean chargeValueDouble = false;
				try {
					Double.parseDouble(chargeTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The initial charge is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					chargeValueDouble = true;
				}
				if (chargeValueDouble == false) {
					c.setQ0(Double.parseDouble(chargeTextField.getText()));
				}
			} else {
				c.setQ0(0.0);
			}
			c.setUnit1((String) chargeComboBoxString.getSelectedItem());
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}
