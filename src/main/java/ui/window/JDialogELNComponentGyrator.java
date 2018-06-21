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

import ui.eln.sca_eln.ELNComponentGyrator;
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
 * Class JDialogELNComponentGyrator
 * Dialog for managing of ELN gyrator
 * Creation: 14/06/2018
 * @version 1.0 14/06/2018
 * @author Irina Kit Yan LEE
*/

@SuppressWarnings("serial")

public class JDialogELNComponentGyrator extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField gyra1TextField;
	private String gyra1ListString[];
	private JComboBox<String> gyra1ComboBoxString;
	private JTextField gyra2TextField;
	private String gyra2ListString[];
	private JComboBox<String> gyra2ComboBoxString;

	/** Parameters **/
	private ELNComponentGyrator gyrator;

	/** Constructor **/
	public JDialogELNComponentGyrator(ELNComponentGyrator _gyrator) {
		/** Set JDialog **/
		setTitle("Setting the gyrator");
		setLocationRelativeTo(null);
		setVisible(true);
		setAlwaysOnTop(true);
		setResizable(false);

		/** Parameters **/
		gyrator = _gyrator;
		
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
		box.setBorder(BorderFactory.createTitledBorder("Setting gyrator attributes"));

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

		nameTextField = new JTextField(gyrator.getValue().toString(), 10); 
	    constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(nameTextField, constraints);
	    boxPanel.add(nameTextField);
	   
		JLabel gyra1Label = new JLabel("g1 : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(gyra1Label, constraints);
	    boxPanel.add(gyra1Label);

		gyra1TextField = new JTextField("" + gyrator.getG1(), 10); 
	    constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(gyra1TextField, constraints);
	    boxPanel.add(gyra1TextField);
	    
	    gyra1ListString = new String[9];
	    gyra1ListString[0] = "GS";
	    gyra1ListString[1] = "MS";
	    gyra1ListString[2] = "kS";
	    gyra1ListString[3] = "S";
	    gyra1ListString[4] = "mS";
	    gyra1ListString[5] = "\u03BCS";
	    gyra1ListString[6] = "nS";
	    gyra1ListString[7] = "pS";
	    gyra1ListString[8] = "fS";
		gyra1ComboBoxString = new JComboBox<String>(gyra1ListString);
		if (gyrator.getUnit1().equals("GS")) {
			gyra1ComboBoxString.setSelectedIndex(0);
		} else if (gyrator.getUnit1().equals("MS")) {
			gyra1ComboBoxString.setSelectedIndex(1);
		} else if (gyrator.getUnit1().equals("kS")) {
			gyra1ComboBoxString.setSelectedIndex(2);
		} else if (gyrator.getUnit1().equals("S")) {
			gyra1ComboBoxString.setSelectedIndex(3);
		} else if (gyrator.getUnit1().equals("mS")) {
			gyra1ComboBoxString.setSelectedIndex(4);
		} else if (gyrator.getUnit1().equals("\u03BCS")) {
			gyra1ComboBoxString.setSelectedIndex(5);
		} else if (gyrator.getUnit1().equals("nS")) {
			gyra1ComboBoxString.setSelectedIndex(6);
		} else if (gyrator.getUnit1().equals("pS")) {
			gyra1ComboBoxString.setSelectedIndex(7);
		} else if (gyrator.getUnit1().equals("fS")) {
			gyra1ComboBoxString.setSelectedIndex(8);
		}
		gyra1ComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(gyra1ComboBoxString, constraints);
	    boxPanel.add(gyra1ComboBoxString);
	    
	    JLabel gyra2Label = new JLabel("g2 : ");
	    constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(gyra2Label, constraints);
	    boxPanel.add(gyra2Label);
	    
	    gyra2TextField = new JTextField("" + gyrator.getG2(), 10); // name not empty
	    constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(gyra2TextField, constraints);
	    boxPanel.add(gyra2TextField);
	    
	    gyra2ListString = new String[9];
	    gyra2ListString[0] = "GS";
	    gyra2ListString[1] = "MS";
	    gyra2ListString[2] = "kS";
	    gyra2ListString[3] = "S";
	    gyra2ListString[4] = "mS";
	    gyra2ListString[5] = "\u03BCS";
	    gyra2ListString[6] = "nS";
	    gyra2ListString[7] = "pS";
	    gyra2ListString[8] = "fS";
	    gyra2ComboBoxString = new JComboBox<String>(gyra2ListString);
	    if (gyrator.getUnit2().equals("GS")) {
	    	gyra2ComboBoxString.setSelectedIndex(0);
	    } else if (gyrator.getUnit2().equals("MS")) {
	    	gyra2ComboBoxString.setSelectedIndex(1);
	    } else if (gyrator.getUnit2().equals("kS")) {
	    	gyra2ComboBoxString.setSelectedIndex(2);
	    } else if (gyrator.getUnit2().equals("S")) {
	    	gyra2ComboBoxString.setSelectedIndex(3);
	    } else if (gyrator.getUnit2().equals("mS")) {
	    	gyra2ComboBoxString.setSelectedIndex(4);
	    } else if (gyrator.getUnit2().equals("\u03BCS")) {
	    	gyra2ComboBoxString.setSelectedIndex(5);
	    } else if (gyrator.getUnit2().equals("nS")) {
	    	gyra2ComboBoxString.setSelectedIndex(6);
	    } else if (gyrator.getUnit2().equals("pS")) {
	    	gyra2ComboBoxString.setSelectedIndex(7);
	    } else if (gyrator.getUnit2().equals("fS")) {
	    	gyra2ComboBoxString.setSelectedIndex(8);
	    }
	    gyra2ComboBoxString.addActionListener(this);
	    constraints = new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(gyra2ComboBoxString, constraints);
	    boxPanel.add(gyra2ComboBoxString);
	    
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
			gyrator.setValue(new String(nameTextField.getText()));

			if (!(gyra1TextField.getText().isEmpty())) {
				Boolean valValueDouble = false;
				try {
					Double.parseDouble(gyra1TextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The gyration conductance is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					valValueDouble = true;
				}
				if (valValueDouble == false) {
					gyrator.setG1(Double.parseDouble(gyra1TextField.getText()));
				}
			} else {
				gyrator.setG1(1.0);
			}
			gyrator.setUnit1((String) gyra1ComboBoxString.getSelectedItem());
			
			if (!(gyra2TextField.getText().isEmpty())) {
				Boolean valValueDouble = false;
				try {
					Double.parseDouble(gyra2TextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The gyration conductance is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					valValueDouble = true;
				}
				if (valValueDouble == false) {
					gyrator.setG2(Double.parseDouble(gyra2TextField.getText()));
				}
			} else {
				gyrator.setG2(1.0);
			}
			gyrator.setUnit2((String) gyra2ComboBoxString.getSelectedItem());
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}
