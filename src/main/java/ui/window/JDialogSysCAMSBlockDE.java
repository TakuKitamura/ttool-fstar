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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class JDialogSystemCAMSBlockDE
 * Dialog for managing of SystemC-AMS DE Block
 * Creation: 26/04/2018
 * @version 1.0 26/04/2018
 * @author Irina Kit Yan LEE
*/

@SuppressWarnings("serial")

public class JDialogSysCAMSBlockDE extends JDialog implements ActionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField periodTextField;

	/** Parameters **/
	private SysCAMSBlockDE block;

	/** Constructor **/
	public JDialogSysCAMSBlockDE(SysCAMSBlockDE block) {
		/** Set JDialog **/
		this.setTitle("Setting TDF Block Attributes");
		this.setSize(500, 168);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

		/** Parameters **/
		this.block = block;
		
		dialog();
	}

	public void dialog() {
		/** JPanel **/
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		/** JTabbedPane **/
		JPanel attributesMainPanel = new JPanel();
		mainPanel.add(attributesMainPanel, BorderLayout.NORTH); // add tab to main panel

		// --- Attributes GridLayout ---//
		attributesMainPanel.setLayout(new BorderLayout());

		// Box for Attributes
		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting DE block attributes"));

		// BorderLayout for Adding Attributes
		JPanel attributesBoxPanel = new JPanel(new GridLayout(2, 2, 0, 10));
		
		// GridLayout for name
		JLabel nameLabel = new JLabel("Name : ");
		attributesBoxPanel.add(nameLabel);
		if (block.getValue().toString().equals("")) { // name empty
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(block.getValue().toString(), 10); // name not empty
		}
		attributesBoxPanel.add(nameTextField);
		
		// GridLayout for period
		JLabel periodLabel = new JLabel("Period Tm : ");
		attributesBoxPanel.add(periodLabel);
		if (block.getPeriod() == -1) {
			periodTextField = new JTextField(10);
		} else {
			periodTextField = new JTextField(Integer.toString(block.getPeriod()), 10);
		}
		attributesBoxPanel.add(periodTextField);
		attributesBox.add(attributesBoxPanel); // add border to box
		
		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); // add box to grid

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
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}