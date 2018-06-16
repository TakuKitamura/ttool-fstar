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

import ui.eln.*;
import ui.eln.sca_eln.ELNComponentTransmissionLine;
import ui.util.IconManager;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

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
 * Class JDialogELNComponentTransmissionLine
 * Dialog for managing of ELN transmission line
 * Creation: 15/06/2018
 * @version 1.0 15/06/2018
 * @author Irina Kit Yan LEE
*/

@SuppressWarnings("serial")

public class JDialogELNComponentTransmissionLine extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField z0TextField;
	private String z0ListString[];
	private JComboBox<String> z0ComboBoxString;
	private JTextField delayTextField;
	private JTextField delta0TextField;
	private String delta0ListString[];
	private JComboBox<String> delta0ComboBoxString;

	/** Parameters **/
	private ELNComponentTransmissionLine transmission_line;

	/** Constructor **/
	public JDialogELNComponentTransmissionLine(ELNComponentTransmissionLine _transmission_line) {
		/** Set JDialog **/
		setTitle("Setting the transmission line");
		setSize(500, 250);
		setLocationRelativeTo(null);
		setVisible(true);
		setAlwaysOnTop(true);
		setResizable(false);

		/** Parameters **/
		transmission_line = _transmission_line;
		
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
		box.setBorder(BorderFactory.createTitledBorder("Setting transmission line attributes"));

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

		nameTextField = new JTextField(transmission_line.getValue().toString(), 10); 
	    constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(nameTextField, constraints);
	    boxPanel.add(nameTextField);
	   
		JLabel z0Label = new JLabel("z0 : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(z0Label, constraints);
	    boxPanel.add(z0Label);

		z0TextField = new JTextField("" + transmission_line.getZ0(), 10); 
	    constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(z0TextField, constraints);
	    boxPanel.add(z0TextField);
	    
	    z0ListString = new String[9];
	    z0ListString[0] = "G\u03A9";
	    z0ListString[1] = "M\u03A9";
	    z0ListString[2] = "k\u03A9";
	    z0ListString[3] = "\u03A9";
	    z0ListString[4] = "m\u03A9";
	    z0ListString[5] = "\u03BC\u03A9";
	    z0ListString[6] = "n\u03A9";
	    z0ListString[7] = "p\u03A9";
	    z0ListString[8] = "f\u03A9";
		z0ComboBoxString = new JComboBox<String>(z0ListString);
		if (transmission_line.getUnit0().equals("G\u03A9")) {
			z0ComboBoxString.setSelectedIndex(0);
		} else if (transmission_line.getUnit0().equals("M\u03A9")) {
			z0ComboBoxString.setSelectedIndex(1);
		} else if (transmission_line.getUnit0().equals("k\u03A9")) {
			z0ComboBoxString.setSelectedIndex(2);
		} else if (transmission_line.getUnit0().equals("\u03A9")) {
			z0ComboBoxString.setSelectedIndex(3);
		} else if (transmission_line.getUnit0().equals("m\u03A9")) {
			z0ComboBoxString.setSelectedIndex(4);
		} else if (transmission_line.getUnit0().equals("\u03BC\u03A9")) {
			z0ComboBoxString.setSelectedIndex(5);
		} else if (transmission_line.getUnit0().equals("n\u03A9")) {
			z0ComboBoxString.setSelectedIndex(6);
		} else if (transmission_line.getUnit0().equals("p\u03A9")) {
			z0ComboBoxString.setSelectedIndex(7);
		} else if (transmission_line.getUnit0().equals("f\u03A9")) {
			z0ComboBoxString.setSelectedIndex(8);
		}
		z0ComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(z0ComboBoxString, constraints);
	    boxPanel.add(z0ComboBoxString);
	    
	    JLabel delayLabel = new JLabel("delay : ");
	    constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(delayLabel, constraints);
	    boxPanel.add(delayLabel);
	    
	    delayTextField = new JTextField("" + transmission_line.getDelay(), 10); 
	    constraints = new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(delayTextField, constraints);
	    boxPanel.add(delayTextField);
	    
	    JLabel delta0Label = new JLabel("delta0 : ");
	    constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(delta0Label, constraints);
	    boxPanel.add(delta0Label);
	    
	    delta0TextField = new JTextField("" + transmission_line.getDelta0(), 10); // name not empty
	    constraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(delta0TextField, constraints);
	    boxPanel.add(delta0TextField);
	    
	    delta0ListString = new String[9];
	    delta0ListString[0] = "GHz";
	    delta0ListString[1] = "MHz";
	    delta0ListString[2] = "kHz";
	    delta0ListString[3] = "Hz";
	    delta0ListString[4] = "mHz";
	    delta0ListString[5] = "\u03BCHz";
	    delta0ListString[6] = "nHz";
	    delta0ListString[7] = "pHz";
	    delta0ListString[8] = "fHz";
	    delta0ComboBoxString = new JComboBox<String>(delta0ListString);
	    if (transmission_line.getUnit2().equals("GHz")) {
	    	delta0ComboBoxString.setSelectedIndex(0);
	    } else if (transmission_line.getUnit2().equals("MHz")) {
	    	delta0ComboBoxString.setSelectedIndex(1);
	    } else if (transmission_line.getUnit2().equals("kHz")) {
	    	delta0ComboBoxString.setSelectedIndex(2);
	    } else if (transmission_line.getUnit2().equals("Hz")) {
	    	delta0ComboBoxString.setSelectedIndex(3);
	    } else if (transmission_line.getUnit2().equals("mHz")) {
	    	delta0ComboBoxString.setSelectedIndex(4);
	    } else if (transmission_line.getUnit2().equals("\u03BCHz")) {
	    	delta0ComboBoxString.setSelectedIndex(5);
	    } else if (transmission_line.getUnit2().equals("nHz")) {
	    	delta0ComboBoxString.setSelectedIndex(6);
	    } else if (transmission_line.getUnit2().equals("pHz")) {
	    	delta0ComboBoxString.setSelectedIndex(7);
	    } else if (transmission_line.getUnit2().equals("fHz")) {
	    	delta0ComboBoxString.setSelectedIndex(8);
	    }
	    delta0ComboBoxString.addActionListener(this);
	    constraints = new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
	    		GridBagConstraints.CENTER,
	    		GridBagConstraints.BOTH,
	    		new Insets(5, 10, 5, 10), 0, 0);
	    gridBag.setConstraints(delta0ComboBoxString, constraints);
	    boxPanel.add(delta0ComboBoxString);
	    
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
			transmission_line.setValue(new String(nameTextField.getText()));

			if (!(z0TextField.getText().isEmpty())) {
				Boolean z0ValueDouble = false;
				try {
					Double.parseDouble(z0TextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The characteristic impedance of the transmission line is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					z0ValueDouble = true;
				}
				if (z0ValueDouble == false) {
					transmission_line.setZ0(Double.parseDouble(z0TextField.getText()));
				}
			} else {
				transmission_line.setZ0(1.0);
			}
			transmission_line.setUnit0((String) z0ComboBoxString.getSelectedItem());
			
			String a = delayTextField.getText().split(Pattern.quote("("))[1].split(",")[0];
			String b = delayTextField.getText().split(Pattern.quote("("))[1].split(",")[1].split(Pattern.quote(")"))[0].split(" ")[1];
			
			if (delayTextField.getText() == "sc_core::SC_ZERO_TIME" || (Double.parseDouble(a) >= 1.0) && b.equals("sc_core::SC_SEC")) {
				transmission_line.setDelay(delayTextField.getText());
			}
			
			if (!(delta0TextField.getText().isEmpty())) {
				Boolean delta0ValueDouble = false;
				try {
					Double.parseDouble(delta0TextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The dissipation factor is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					delta0ValueDouble = true;
				}
				if (delta0ValueDouble == false) {
					transmission_line.setDelta0(Double.parseDouble(delta0TextField.getText()));
				}
			} else {
				transmission_line.setDelta0(1.0);
			}
			transmission_line.setUnit2((String) delta0ComboBoxString.getSelectedItem());
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}