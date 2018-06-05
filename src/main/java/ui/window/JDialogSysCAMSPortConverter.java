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
import java.awt.GridLayout;
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
 * Class JDialogSystemCAMSPortConverterIn
 * Dialog for managing of SystemC-AMS Converter Input Port
 * Creation: 07/05/2018
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
*/

@SuppressWarnings("serial")

public class JDialogSysCAMSPortConverter extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField periodTextField;
	private JTextField rateTextField;
	private JTextField delayTextField;
	private String listTypeString[];
	private JComboBox<String> typeComboBoxString;
	private String listOriginString[];
	private JComboBox<String> originComboBoxString;

	/** Parameters **/
	private SysCAMSPortConverter port;

	/** Constructor **/
	public JDialogSysCAMSPortConverter(SysCAMSPortConverter port) {
		/** Set JDialog **/
		this.setTitle("Setting Converter Ports");
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
		box.setBorder(BorderFactory.createTitledBorder("Setting converter input port attributes"));

		JPanel boxPanel = new JPanel(new GridLayout(6, 2, 0, 10));

		JLabel labelName = new JLabel("Name : ");
		boxPanel.add(labelName);

		if (port.getPortName().toString().equals("")) { // name empty
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(port.getPortName().toString(), 10); // name not empty
		}

		boxPanel.add(nameTextField);

		// Period
		JLabel periodLabel = new JLabel("Period Tp (us) : ");
		boxPanel.add(periodLabel); // add label to box
		if (port.getPeriod() == -1) { // name empty 		// port.getName().toString().equals("") ||
			periodTextField = new JTextField(10);
		} else {
			periodTextField = new JTextField("" + port.getPeriod(), 10); // name not empty
		}
		boxPanel.add(periodTextField); // add text to box

		// Rate
		JLabel rateLabel = new JLabel("Rate : ");
		boxPanel.add(rateLabel); // add label to box

		if (port.getRate() == -1) { // name empty		// port.getName().toString().equals("") ||
			rateTextField = new JTextField(10);
		} else {
			rateTextField = new JTextField("" + port.getRate(), 10); // name not empty
		}
		boxPanel.add(rateTextField); // add text to box

		// Delay
		JLabel delayLabel = new JLabel("Delay : ");
		boxPanel.add(delayLabel); // add label to box
		if (port.getDelay() == -1) { // name empty			// port.getName().toString().equals("") || 
			delayTextField = new JTextField(10);
		} else {
			delayTextField = new JTextField("" + port.getDelay(), 10); // name not empty
		}
		boxPanel.add(delayTextField); // add text to box

		// Type
		JLabel typeLabel = new JLabel("Type : ");
		boxPanel.add(typeLabel); // add label to box
		listTypeString = new String[4];
		listTypeString[0] = "int";
		listTypeString[1] = "bool";
		listTypeString[2] = "double";
		listTypeString[3] = "sc_dt::sc_logic";
		typeComboBoxString = new JComboBox<String>(listTypeString);
		if (port.getConvType().equals("") || port.getConvType().equals("int")) {
			typeComboBoxString.setSelectedIndex(0);
		}
		if (port.getConvType().equals("bool")) {
			typeComboBoxString.setSelectedIndex(1);
		}
		if (port.getConvType().equals("double")) {
			typeComboBoxString.setSelectedIndex(2);
		}
		if (port.getConvType().equals("sc_dt::sc_logic")) {
			typeComboBoxString.setSelectedIndex(3);
		}
		typeComboBoxString.setActionCommand("type");
		typeComboBoxString.addActionListener(this);
		boxPanel.add(typeComboBoxString); // add combo to box

		// Origin
		JLabel orginLabel = new JLabel("Origin : ");
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
			port.setConvType((String) typeComboBoxString.getSelectedItem());
			
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