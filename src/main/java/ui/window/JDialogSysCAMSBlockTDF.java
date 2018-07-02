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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.derby.tools.sysinfo;

/**
 * Class JDialogSystemCAMSBlockTDF Dialog for managing of SystemC-AMS TDF Block
 * Creation: 26/04/2018
 * 
 * @version 1.0 26/04/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSBlockTDF extends JDialog implements ActionListener, ListSelectionListener {

	/** Access to ActionPerformed **/
	private JTextField nameTextField;
	private JTextField periodTextField;
	private String listPeriodString[];
	private JComboBox<String> periodComboBoxString;

	//
	private JTextField nameParameterTextField;
	private JTextField valueParameterTextField;
	private JRadioButton constantParameterRadioButton;
	private String listTypeParameterString[];
	private JComboBox<String> typeParameterComboBoxString;
	//
	private JList<String> listParameters;
	private DefaultListModel<String> listModel;
	private JButton upButton, downButton, removeButton;

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
		JPanel parametersMainPanel = new JPanel();
		JPanel processMainPanel = new JPanel();
		tabbedPane.add("Attributes", attributesMainPanel);
		tabbedPane.add("Parameters", parametersMainPanel);
		tabbedPane.add("Process Code", processMainPanel);

		mainPanel.add(tabbedPane, BorderLayout.NORTH); 

		// --- Attributes ---//
		attributesMainPanel.setLayout(new BorderLayout());

		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting TDF block attributes"));

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

		if (block.getValue().toString().equals("")) { 
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(block.getValue().toString(), 10);
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

		if (block.getPeriod() == -1) { 
			periodTextField = new JTextField(10);
		} else {
			periodTextField = new JTextField("" + block.getPeriod(), 10); 
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

		attributesBox.add(attributesBoxPanel); 
		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); 

		// --- Parameters ---//
		parametersMainPanel.setLayout(new BorderLayout());

		Box parametersBox = Box.createVerticalBox();
		parametersBox.setBorder(BorderFactory.createTitledBorder("Setting TDF block parameters"));

		GridBagLayout gridBagParameter = new GridBagLayout();
		GridBagConstraints constraintParameter = new GridBagConstraints();
		JPanel parameterBoxPanel = new JPanel();
		parameterBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		parameterBoxPanel.setLayout(gridBagParameter);

		JLabel nameParameterLabel = new JLabel("identifier");
		constraintParameter = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(nameParameterLabel, constraintParameter);
		parameterBoxPanel.add(nameParameterLabel);

		nameParameterTextField = new JTextField();
		constraintParameter = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(nameParameterTextField, constraintParameter);
		parameterBoxPanel.add(nameParameterTextField);

		JLabel egalLabel = new JLabel("=");
		constraintParameter = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(egalLabel, constraintParameter);
		parameterBoxPanel.add(egalLabel);

		JLabel valueParameterLabel = new JLabel("value");
		constraintParameter = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(valueParameterLabel, constraintParameter);
		parameterBoxPanel.add(valueParameterLabel);

		valueParameterTextField = new JTextField();
		constraintParameter = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(valueParameterTextField, constraintParameter);
		parameterBoxPanel.add(valueParameterTextField);

		JLabel pointsLabel = new JLabel(":");
		constraintParameter = new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(pointsLabel, constraintParameter);
		parameterBoxPanel.add(pointsLabel);

		JLabel constantLabel = new JLabel("const");
		constraintParameter = new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(constantLabel, constraintParameter);
		parameterBoxPanel.add(constantLabel);

		constantParameterRadioButton = new JRadioButton();
		constantParameterRadioButton.setActionCommand("Const");
		constantParameterRadioButton.setSelected(false);
		constantParameterRadioButton.addActionListener(this);
		constraintParameter = new GridBagConstraints(4, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(constantParameterRadioButton, constraintParameter);
		parameterBoxPanel.add(constantParameterRadioButton);

		JLabel typeParameterLabel = new JLabel("type");
		constraintParameter = new GridBagConstraints(5, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(typeParameterLabel, constraintParameter);
		parameterBoxPanel.add(typeParameterLabel);

		listTypeParameterString = new String[4];
		listTypeParameterString[0] = "bool";
		listTypeParameterString[1] = "int";
		listTypeParameterString[2] = "double";
		listTypeParameterString[3] = "long";
		typeParameterComboBoxString = new JComboBox<String>(listTypeParameterString);
		typeParameterComboBoxString.setSelectedIndex(0);
		typeParameterComboBoxString.setActionCommand("type");
		typeParameterComboBoxString.addActionListener(this);
		constraintParameter = new GridBagConstraints(5, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(typeParameterComboBoxString, constraintParameter);
		parameterBoxPanel.add(typeParameterComboBoxString);

		JButton addModifyButton = new JButton("Add / Modify parameter");
		addModifyButton.setActionCommand("Add_Modify");
		addModifyButton.addActionListener(this);
		constraintParameter = new GridBagConstraints(0, 2, 6, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(addModifyButton, constraintParameter);
		parameterBoxPanel.add(addModifyButton);

		parametersBox.add(parameterBoxPanel); 
		parametersMainPanel.add(parametersBox, BorderLayout.WEST); 

		Box managingParametersBox = Box.createVerticalBox();
		managingParametersBox.setBorder(BorderFactory.createTitledBorder("Managing parameters"));

		BorderLayout borderLayout = new BorderLayout(5, 10);
		JPanel managingParameterBoxPanel = new JPanel(borderLayout);
		managingParameterBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));

		listModel = new DefaultListModel<String>();
		listParameters = new JList<String>(listModel);
		listParameters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listParameters.setLayoutOrientation(JList.VERTICAL);
		listParameters.setVisibleRowCount(10);
		listParameters.addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(listParameters);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(scrollPane.getBorder(), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		managingParameterBoxPanel.add(scrollPane, BorderLayout.NORTH);

		GridLayout gridLayout = new GridLayout(3, 1, 5, 10);
		JPanel buttonPanel = new JPanel(gridLayout);
		
		upButton = new JButton("Up");
		upButton.setActionCommand("Up");
		upButton.setEnabled(false);
		upButton.addActionListener(this);
		upButton.setBorder(BorderFactory.createCompoundBorder(upButton.getBorder(), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		buttonPanel.add(upButton);

		downButton = new JButton("Down");
		downButton.setActionCommand("Down");
		downButton.setEnabled(false);
		downButton.addActionListener(this);
		downButton.setBorder(BorderFactory.createCompoundBorder(downButton.getBorder(), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		buttonPanel.add(downButton);

		removeButton = new JButton("Remove parameter");
		removeButton.setActionCommand("Remove");
		removeButton.setEnabled(false);
		removeButton.addActionListener(this);
		removeButton.setBorder(BorderFactory.createCompoundBorder(removeButton.getBorder(), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		buttonPanel.add(removeButton);
		
		managingParameterBoxPanel.add(buttonPanel, BorderLayout.SOUTH);

		managingParametersBox.add(managingParameterBoxPanel); 
		parametersMainPanel.add(managingParametersBox, BorderLayout.CENTER); 

		// --- ProcessCode ---//
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

		// --- Button --- //
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
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if ("Add_Modify".equals(e.getActionCommand())) {
			Boolean alreadyExist = false;
			int alreadyExistId = -1;
			String type = (String) typeParameterComboBoxString.getSelectedItem();
			String s = null;

			Boolean valueBoolean = false, valueInteger = false, valueDouble = false, valueLong = false, nameEmpty = false;

			if (nameParameterTextField.getText().isEmpty()) {
				JDialog msg = new JDialog(this);
				msg.setLocationRelativeTo(null);
				JOptionPane.showMessageDialog(msg, "The name is empty", "Warning !",
						JOptionPane.WARNING_MESSAGE);	
				nameEmpty = true;
			}

			for (int i = 0; i < listModel.getSize(); i++) {
				if (nameParameterTextField.getText().equals(listModel.elementAt(i).split("\\s")[0])) {
					alreadyExist = true;
					alreadyExistId = i;
				}
			}

			if (alreadyExist == false) {
				try {
					if (type.equals("bool")) {
						Boolean.parseBoolean(valueParameterTextField.getText());
					} else if (type.equals("int")) {
						Integer.parseInt(valueParameterTextField.getText());
					} else if (type.equals("double")) {
						Double.parseDouble(valueParameterTextField.getText());
					} else if (type.equals("long")) {
						Long.parseLong(valueParameterTextField.getText());
					}
				} catch (NumberFormatException e1) {
					if (type.equals("bool")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Boolean", "Warning !",
								JOptionPane.WARNING_MESSAGE);	
						valueBoolean = true;
					} else if (type.equals("int")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Integer", "Warning !",
								JOptionPane.WARNING_MESSAGE);
						valueInteger = true;
					} else if (type.equals("double")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Double", "Warning !",
								JOptionPane.WARNING_MESSAGE);		
						valueDouble = true;
					} else if (type.equals("long")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Long", "Warning !",
								JOptionPane.WARNING_MESSAGE);
						valueLong = true;
					}
				}

				if ((valueBoolean == false) && (valueInteger == false) && (valueDouble == false) && (valueLong == false) && (nameEmpty == false)) {
					s = nameParameterTextField.getText() + " = ";

					if (type.equals("bool")) {
						s = s + Boolean.parseBoolean(valueParameterTextField.getText()) + " : ";
					} else if (type.equals("int")) {
						s = s + Integer.parseInt(valueParameterTextField.getText()) + " : ";
					} else if (type.equals("double")) {
						s = s + Double.parseDouble(valueParameterTextField.getText()) + " : ";
					} else if (type.equals("long")) {
						s = s + Long.parseLong(valueParameterTextField.getText()) + " : ";
					}

					if (constantParameterRadioButton.isSelected()) {
						s = s + "const " + type;
					} else {
						s = s + type;
					}
					listModel.addElement(s);
				}
			} else {
				listModel.remove(alreadyExistId);
				
				try {
					if (type.equals("bool")) {
						Boolean.parseBoolean(valueParameterTextField.getText());
					} else if (type.equals("int")) {
						Integer.parseInt(valueParameterTextField.getText());
					} else if (type.equals("double")) {
						Double.parseDouble(valueParameterTextField.getText());
					} else if (type.equals("long")) {
						Long.parseLong(valueParameterTextField.getText());
					}
				} catch (NumberFormatException e1) {
					if (type.equals("bool")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Boolean", "Warning !",
								JOptionPane.WARNING_MESSAGE);	
						valueBoolean = true;
					} else if (type.equals("int")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Integer", "Warning !",
								JOptionPane.WARNING_MESSAGE);
						valueInteger = true;
					} else if (type.equals("double")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Double", "Warning !",
								JOptionPane.WARNING_MESSAGE);		
						valueDouble = true;
					} else if (type.equals("long")) {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Long", "Warning !",
								JOptionPane.WARNING_MESSAGE);
						valueLong = true;
					}
				}

				if ((valueBoolean == false) && (valueInteger == false) && (valueDouble == false) && (valueLong == false) && (nameEmpty == false)) {
					s = nameParameterTextField.getText() + " = ";

					if (type.equals("bool")) {
						s = s + Boolean.parseBoolean(valueParameterTextField.getText()) + " : ";
					} else if (type.equals("int")) {
						s = s + Integer.parseInt(valueParameterTextField.getText()) + " : ";
					} else if (type.equals("double")) {
						s = s + Double.parseDouble(valueParameterTextField.getText()) + " : ";
					} else if (type.equals("long")) {
						s = s + Long.parseLong(valueParameterTextField.getText()) + " : ";
					}

					if (constantParameterRadioButton.isSelected()) {
						s = s + "const " + type;
					} else {
						s = s + type;
					}
					listModel.add(alreadyExistId, s);
				}
			}
		}
		
		if ("Remove".equals(e.getActionCommand())) {
			if (listModel.getSize() >= 1) {
				listModel.remove(listParameters.getSelectedIndex());
			}
		}

		if ("Up".equals(e.getActionCommand())) {
			if (listParameters.getSelectedIndex() >= 1) {
				String sprev = listModel.get(listParameters.getSelectedIndex()-1);
				listModel.remove(listParameters.getSelectedIndex()-1);
				listModel.add(listParameters.getSelectedIndex()+1, sprev);
			} else {
				JDialog msg = new JDialog(this);
				msg.setLocationRelativeTo(null);
				JOptionPane.showMessageDialog(msg, "Cannot move the parameter up", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		
		if ("Down".equals(e.getActionCommand())) {
			if (listParameters.getSelectedIndex() < listModel.getSize()-1) {
				String snext = listModel.get(listParameters.getSelectedIndex()+1);
				listModel.remove(listParameters.getSelectedIndex()+1);
				listModel.add(listParameters.getSelectedIndex(), snext);
			} else {
				JDialog msg = new JDialog(this);
				msg.setLocationRelativeTo(null);
				JOptionPane.showMessageDialog(msg, "Cannot move the parameter down", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		
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
					block.setTime((String) periodComboBoxString.getSelectedItem());
				}
			} else {
				block.setPeriod(-1);
				block.setTime((String) periodComboBoxString.getSelectedItem());
			}

			/** Save the process code into listProcessCodeTDF **/
			block.setProcessCode(processCodeTextArea.getText());

			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if (listParameters.getSelectedIndex() == -1) {
				upButton.setEnabled(false);
				downButton.setEnabled(false);
				removeButton.setEnabled(false);
			} else {
				if (listModel.getSize() >= 2) {
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
				removeButton.setEnabled(true);
			}
		}
	}
}