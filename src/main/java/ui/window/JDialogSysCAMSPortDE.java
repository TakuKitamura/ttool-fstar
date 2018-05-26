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
 * Class JDialogSystemCAMSPortDE
 * Dialog for managing of SystemC-AMS DE Port
 * Creation: 07/05/2018
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
*/

@SuppressWarnings("serial")

public class JDialogSysCAMSPortDE extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField periodTextField;
	private String listPeriodString[];
	private JComboBox<String> periodComboBoxString;
	private JTextField rateTextField;
	private JTextField delayTextField;
	private String listTypeString[];
	private JComboBox<String> typeComboBoxString;
	private String listOriginString[];
	private JComboBox<String> originComboBoxString;

	/** Parameters **/
	private SysCAMSPortDE port;

	/** Constructor **/
	public JDialogSysCAMSPortDE(SysCAMSPortDE port) {
		/** Set JDialog **/
		this.setTitle("Setting DE Ports");
		this.setSize(500, 318);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

		/** Parameters **/
		this.port = port;
		
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
		box.setBorder(BorderFactory.createTitledBorder("Setting DE port attributes"));

		GridBagLayout gridBag = new GridBagLayout();
	    GridBagConstraints constraints = new GridBagConstraints();
	    JPanel boxPanel = new JPanel();
	    boxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
	    boxPanel.setLayout(gridBag); 
	    
	    JLabel labelName = new JLabel("Name : ");
	    constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(labelName, constraints);
	    boxPanel.add(labelName);

	    if (port.getPortName().toString().equals("")) { // name empty
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(port.getPortName().toString(), 10); // name not empty
		}
	    constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(nameTextField, constraints);
	    boxPanel.add(nameTextField);
	   
		JLabel periodLabel = new JLabel("Period Tp : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(periodLabel, constraints);
	    boxPanel.add(periodLabel);

	    if (port.getPeriod() == -1) { // name empty 		// port.getName().toString().equals("") ||
			periodTextField = new JTextField(10);
		} else {
			periodTextField = new JTextField("" + port.getPeriod(), 10); // name not empty
		}
	    constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(periodTextField, constraints);
	    boxPanel.add(periodTextField);
	    
	    listPeriodString = new String[3];
	    listPeriodString[0] = "us";
		listPeriodString[1] = "ms";
		listPeriodString[2] = "s";
		periodComboBoxString = new JComboBox<String>(listPeriodString);
		if (port.getTime().equals("") || port.getTime().equals("us")) {
			periodComboBoxString.setSelectedIndex(0);
		} else if (port.getTime().equals("ms")){
			periodComboBoxString.setSelectedIndex(1);
		} else {
			periodComboBoxString.setSelectedIndex(2);
		}
		periodComboBoxString.setActionCommand("time");
		periodComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(periodComboBoxString, constraints);
	    boxPanel.add(periodComboBoxString);
	    
		JLabel rateLabel = new JLabel("Rate : ");
		constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(rateLabel, constraints);
		boxPanel.add(rateLabel); // add label to box

		if (port.getRate() == -1) { // name empty	
			rateTextField = new JTextField(10);
		} else {
			rateTextField = new JTextField("" + port.getRate(), 10); // name not empty
		}
		constraints = new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(rateTextField, constraints);
		boxPanel.add(rateTextField); // add text to box

		JLabel delayLabel = new JLabel("Delay : ");
		constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(delayLabel, constraints);
		boxPanel.add(delayLabel); // add label to box
		
		if (port.getDelay() == -1) { // name empty			// port.getName().toString().equals("") || 
			delayTextField = new JTextField(10);
		} else {
			delayTextField = new JTextField("" + port.getDelay(), 10); // name not empty
		}
		constraints = new GridBagConstraints(1, 3, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(delayTextField, constraints);
		boxPanel.add(delayTextField); // add text to box

		JLabel typeLabel = new JLabel("Type : ");
		constraints = new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(typeLabel, constraints);
		boxPanel.add(typeLabel); // add label to box
		
		listTypeString = new String[3];
		listTypeString[0] = "int";
		listTypeString[1] = "bool";
		listTypeString[2] = "double";
		typeComboBoxString = new JComboBox<String>(listTypeString);
		if (port.getDEType().equals("") || port.getDEType().equals("int")) {
			typeComboBoxString.setSelectedIndex(0);
		}
		if (port.getDEType().equals("bool")) {
			typeComboBoxString.setSelectedIndex(1);
		}
		if (port.getDEType().equals("double")) {
			typeComboBoxString.setSelectedIndex(2);
		}
		typeComboBoxString.setActionCommand("type");
		typeComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(1, 4, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(typeComboBoxString, constraints);
		boxPanel.add(typeComboBoxString); // add combo to box
		
		JLabel orginLabel = new JLabel("Origin : ");
		constraints = new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(orginLabel, constraints);
		boxPanel.add(orginLabel); // add label to box
		
		listOriginString = new String[2];
		listOriginString[0] = "Input";
		listOriginString[1] = "Output";
		originComboBoxString = new JComboBox<String>(listOriginString);
		if (port.getOrigin() == 0 || port.getOrigin() == -1) {
			originComboBoxString.setSelectedIndex(0);
		} else {
			originComboBoxString.setSelectedIndex(1);
		}
		originComboBoxString.setActionCommand("origin");
		originComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(1, 5, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(originComboBoxString, constraints);
		boxPanel.add(originComboBoxString); // add combo to box

		box.add(boxPanel); // add border to box
		attributesMainPanel.add(box); // add grid to grid

		// Down Side
		JPanel downPanel = new JPanel(new GridLayout(1, 2));

		JButton saveCloseButton = new JButton("Save and close");
		saveCloseButton.setIcon(IconManager.imgic25);
		saveCloseButton.setActionCommand("Save_Close");
		saveCloseButton.addActionListener(this);
		downPanel.add(saveCloseButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setIcon(IconManager.imgic27);
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		downPanel.add(cancelButton);

		mainPanel.add(downPanel, BorderLayout.CENTER);
		
		this.getRootPane().setDefaultButton(saveCloseButton);
	}

	public void actionPerformed(ActionEvent e) {
		if ("Save_Close".equals(e.getActionCommand())) {
			port.setPortName(new String(nameTextField.getText()));

			if (!(periodTextField.getText().isEmpty())) {
				Boolean periodValueInteger = false;
				try {
					Integer.parseInt(periodTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "Period is not a Integer", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					periodValueInteger = true;
				}
				if (periodValueInteger == false) {
					port.setPeriod(Integer.parseInt(periodTextField.getText()));
				}
			} else {
				port.setPeriod(-1);
			}
			if (!(rateTextField.getText().isEmpty())) {
				Boolean rateValueInteger = false;
				try {
					Integer.parseInt(rateTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "Rate is not a Integer", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					rateValueInteger = true;
				}
				if (rateValueInteger == false) {
					port.setRate(Integer.parseInt(rateTextField.getText()));
				}
			} else {
				port.setRate(-1);
			}

			if (!(delayTextField.getText().isEmpty())) {
				Boolean delayValueInteger = false;
				try {
					Integer.parseInt(delayTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "Delay is not a Integer", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					delayValueInteger = true;
				}
				if (delayValueInteger == false) {
					port.setDelay(Integer.parseInt(delayTextField.getText()));
				}
			} else {
				port.setDelay(-1);
			}
			port.setDEType((String) typeComboBoxString.getSelectedItem());
			port.setTime((String) periodComboBoxString.getSelectedItem());

			if ((String) originComboBoxString.getSelectedItem() == "Output") {
				port.setOrigin(1);
			} else {
				port.setOrigin(0);
			}
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}