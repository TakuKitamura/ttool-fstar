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
import ui.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Class JDialogSystemCAMSPortDE 
 * Dialog for managing of SystemC-AMS DE Port
 * Creation: 07/05/2018
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
 * @version 1.0 27/10/2019
 * @author Daniela GENIUS
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSPortDE extends JDialog implements ActionListener {
	private JTextField nameTextField;
        private JTextField nbitsTextField;
	private ArrayList<String> listArrayTypeString;
	private JComboBox<String> typeComboBoxString;
	private String listOriginString[];
	private JComboBox<String> originComboBoxString;
	private JRadioButton sensitiveRadioButton;
	private String listSensitiveString[];
	private JComboBox<String> sensitiveComboBoxString;

	private SysCAMSPortDE port;

	public JDialogSysCAMSPortDE(SysCAMSPortDE port) {
		this.setTitle("Setting DE Ports");
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

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
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		JPanel attributesMainPanel = new JPanel(new GridLayout());
		mainPanel.add(attributesMainPanel, BorderLayout.NORTH);

		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createTitledBorder("Setting DE port attributes"));

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel boxPanel = new JPanel();
		boxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		boxPanel.setLayout(gridBag);

		JLabel labelName = new JLabel("Name : ");
		constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelName, constraints);
		boxPanel.add(labelName);

		if (port.getPortName().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(port.getPortName().toString(), 10);
		}
		constraints = new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(nameTextField, constraints);
		boxPanel.add(nameTextField);

		JLabel nbitsLabel = new JLabel("Nbits : ");
	        constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
		GridBagConstraints.CENTER,
	        GridBagConstraints.BOTH,
		new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(nbitsLabel, constraints);
	        boxPanel.add(nbitsLabel);
		
		 if (port.getNbits() == -1) {
		 nbitsTextField = new JTextField(10);
		 } else {
		 nbitsTextField = new JTextField("" + port.getNbits(), 10);
		 }
		 constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
		 GridBagConstraints.CENTER,
		 GridBagConstraints.BOTH,
		 new Insets(5, 10, 5, 10), 0, 0);
		 gridBag.setConstraints(nbitsTextField, constraints);
		 boxPanel.add(nbitsTextField);
		         
		JLabel typeLabel = new JLabel("Type : ");
		constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(typeLabel, constraints);
		boxPanel.add(typeLabel);

		listArrayTypeString = new ArrayList<String>();
		listArrayTypeString.add("int");
		listArrayTypeString.add("bool");
		listArrayTypeString.add("double");
		listArrayTypeString.add("sc_int<N>");
		listArrayTypeString.add("sc_uint<32>");
		
		if (port.getFather() != null) {
			if (port.getFather() instanceof SysCAMSBlockDE) {
				if (!((SysCAMSBlockDE) port.getFather()).getListTypedef().isEmpty()) {
					for (int i = 0; i < ((SysCAMSBlockDE) port.getFather()).getListTypedef().getSize(); i++) {
						String select = ((SysCAMSBlockDE) port.getFather()).getListTypedef().get(i);
						String[] split = select.split(" : ");
						listArrayTypeString.add(split[0]);
					}
				}
				if ((!((SysCAMSBlockDE) port.getFather()).getNameTemplate().equals("")) && (!((SysCAMSBlockDE) port.getFather()).getTypeTemplate().equals("")) 
						&& ((SysCAMSBlockDE) port.getFather()).getListTypedef().isEmpty()) {
					listArrayTypeString.add("sc_dt::sc_int<"+((SysCAMSBlockDE) port.getFather()).getNameTemplate()+">");
				}
			}
		}
		typeComboBoxString = new JComboBox<String>();
		for (int i = 0; i < listArrayTypeString.size(); i++) {
			typeComboBoxString.addItem(listArrayTypeString.get(i));
		}
		for (int i = 0; i < listArrayTypeString.size(); i++) {
			if (port.getDEType().equals("")) {
			 
				typeComboBoxString.setSelectedIndex(0);
			}
			if (port.getDEType().equals(listArrayTypeString.get(i))) {
				typeComboBoxString.setSelectedIndex(i);
			
			}
			if (port.getDEType().equals("sc_dt::sc_int")) {
				typeComboBoxString.setSelectedIndex(3);
			}
			if (port.getDEType().equals("sc_dt::sc_uint<32>")) {
				typeComboBoxString.setSelectedIndex(3);		      
			}
			
		}
		typeComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(typeComboBoxString, constraints);
	
		boxPanel.add(typeComboBoxString); 

		JLabel orginLabel = new JLabel("Origin : ");
		constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(orginLabel, constraints);
		boxPanel.add(orginLabel);

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
		constraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(originComboBoxString, constraints);
		boxPanel.add(originComboBoxString);

		box.add(boxPanel);
		attributesMainPanel.add(box);

		JPanel sensitivePanel = new JPanel(new FlowLayout());

		sensitiveRadioButton = new JRadioButton();
		sensitiveRadioButton.setActionCommand("Sensitive");
		sensitiveRadioButton.setSelected(port.getSensitive());
		sensitiveRadioButton.addActionListener(this);
		sensitivePanel.add(sensitiveRadioButton);

		JLabel sensitiveLabel = new JLabel("Sensitive");
		sensitivePanel.add(sensitiveLabel);

		constraints = new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(sensitivePanel, constraints);
		boxPanel.add(sensitivePanel);
		
		listSensitiveString = new String[3];
		listSensitiveString[0] = "";
        listSensitiveString[1] = "positive";
		listSensitiveString[2] = "negative";
		sensitiveComboBoxString = new JComboBox<String>(listSensitiveString);
		if (port.getSensitiveMethod().equals("")) {
			sensitiveComboBoxString.setSelectedIndex(0);
        } else if (port.getSensitiveMethod().equals("positive")) {
            sensitiveComboBoxString.setSelectedIndex(1);
		} else if (port.getSensitiveMethod().equals("negative")) {
			sensitiveComboBoxString.setSelectedIndex(2);
		}
		sensitiveComboBoxString.setActionCommand("Sensitive_method");
		sensitiveComboBoxString.setEnabled(port.getSensitive());
		sensitiveComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(sensitiveComboBoxString, constraints);
		boxPanel.add(sensitiveComboBoxString);
		
		// -- Button -- //
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
		if ("Sensitive".equals(e.getActionCommand())) {
			if (sensitiveRadioButton.isSelected() == true) {
				sensitiveComboBoxString.setEnabled(true);
			} else {
				sensitiveComboBoxString.setEnabled(false);
			}
		}
		if ("Save_Close".equals(e.getActionCommand())) {
			port.setPortName(new String(nameTextField.getText()));

			if (!(nbitsTextField.getText().isEmpty())) {
				Boolean nbitsValueInteger = false;
				try {
					Integer.parseInt(nbitsTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "Nbits is not a Integer", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					nbitsValueInteger = true;
				}
				//	if (nbitsValueInteger == false) {
					port.setNbits(Integer.parseInt(nbitsTextField.getText()));				
			}
	        		       
		
				if(typeComboBoxString.getSelectedItem()=="sc_int<N>"){
			    //	port.setDEType((String)"sc_int<"+nbitsTextField.getText()+"> ");
			    port.setDEType("sc_dt::sc_int");
				 
			 
			    	}
				else{
				   	if(typeComboBoxString.getSelectedItem()=="sc_uint<32>"){
			    //	port.setDEType((String)"sc_int<"+nbitsTextField.getText()+"> ");
			    port.setDEType("sc_dt::sc_uint<32>");				 			 
			    	} 
				
			   	else{
			    	port.setDEType((String) typeComboBoxString.getSelectedItem());

				}
				}
			
			       
			
			if ((String) originComboBoxString.getSelectedItem() == "Output") {
				port.setOrigin(1);
			} else {
				port.setOrigin(0);
			}

			port.setSensitive(sensitiveRadioButton.isSelected());
			port.setSensitiveMethod((String) sensitiveComboBoxString.getSelectedItem());
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}
}
