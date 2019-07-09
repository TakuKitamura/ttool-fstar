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
    
        private JComboBox<String> unitComboBoxString;

        private JTextField posFirstTextField;
	private String listPosFirstString[];
        private JComboBox<String> posFirstComboBoxString;
    
        private JTextField frequencyTextField;
	private String listFrequencyString[];
	private JComboBox<String> frequencyComboBoxString;

        private JTextField dutyCycleTextField;
	private String listDutyCycleString[];
	private JComboBox<String> dutyCycleComboBoxString;

        private JTextField startTimeTextField;
	private String listStartTimeString[];
	private JComboBox<String> startTimeComboBoxString;
    
	private JTextField nameStructTextField;
	private JTextField valueStructTextField;
	private JRadioButton constantStructRadioButton;
	private String listTypeStructString[];
	private JComboBox<String> typeStructComboBoxString;
	private ArrayList<String> listTmpStruct;
	private JList<String> structList;
	private DefaultListModel<String> structListModel;
	private boolean structBool = false;
	private JTextField nameTemplateTextField;
        private JTextField valueTemplateTextField;
	private String listTypeTemplateString[];
	private JComboBox<String> typeTemplateComboBoxString;
	private JTextField nameTypedefTextField;
	private String listTypeTypedefString[];
	private JComboBox<String> typeTypedefComboBoxString;
	private JButton addModifyTypedefButton;
	private ArrayList<String> listTmpTypedef;
	private JList<String> typedefList;
	private DefaultListModel<String> typedefListModel;
	private boolean typedefBool = false;

	private JButton upButton, downButton, removeButton;
    
    
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
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel attributesMainPanel = new JPanel();
		JPanel parametersMainPanel = new JPanel();
		JPanel processMainPanel = new JPanel();
        JPanel contructorMainPanel = new JPanel();
		tabbedPane.add("Attributes", attributesMainPanel);
		//	tabbedPane.add("Parameters", parametersMainPanel);
		//tabbedPane.add("Process Code", processMainPanel);
		//tabbedPane.add("Constructor Code", contructorMainPanel);

		mainPanel.add(tabbedPane, BorderLayout.NORTH); 

		// --- Attributes ---//
		attributesMainPanel.setLayout(new BorderLayout());

		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting Clock attributes"));

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

		JLabel frequencyLabel = new JLabel("Frequency Tm : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(frequencyLabel, constraints);
		attributesBoxPanel.add(frequencyLabel);

		if (clock.getFrequency() == -1) { 
			frequencyTextField = new JTextField(10);
		} else {
			frequencyTextField = new JTextField("" + clock.getFrequency(), 10); 
		}
		constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(frequencyTextField, constraints);
		attributesBoxPanel.add(frequencyTextField);

		listFrequencyString = new String[4];
		listFrequencyString[0] = "s";
		listFrequencyString[1] = "ms";
		listFrequencyString[2] = "\u03BCs";
		listFrequencyString[3] = "ns";
		frequencyComboBoxString = new JComboBox<String>(listFrequencyString);
		if (clock.getUnit().equals("") || clock.getUnit().equals("s")) {
			frequencyComboBoxString.setSelectedIndex(0);
		} else if (clock.getUnit().equals("ms")) {
			frequencyComboBoxString.setSelectedIndex(1);
		} else if (clock.getUnit().equals("\u03BCs")) {
			frequencyComboBoxString.setSelectedIndex(2);
		} else if (clock.getUnit().equals("ns")) {
			frequencyComboBoxString.setSelectedIndex(3);
		}
		frequencyComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(frequencyComboBoxString, constraints);
		attributesBoxPanel.add(frequencyComboBoxString);
		//

	JLabel dutyCycleLabel = new JLabel("DutyCycle Tm : ");
		constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(dutyCycleLabel, constraints);
		attributesBoxPanel.add(dutyCycleLabel);

		if (clock.getDutyCycle() == -1) { 
			dutyCycleTextField = new JTextField(10);
		} else {
			dutyCycleTextField = new JTextField("" + clock.getDutyCycle(), 10); 
		}
		constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(dutyCycleTextField, constraints);
		attributesBoxPanel.add(dutyCycleTextField);

		listDutyCycleString = new String[4];
		listDutyCycleString[0] = "s";
		listDutyCycleString[1] = "ms";
		listDutyCycleString[2] = "\u03BCs";
		listDutyCycleString[3] = "ns";
		dutyCycleComboBoxString = new JComboBox<String>(listDutyCycleString);
		if (clock.getUnit().equals("") || clock.getUnit().equals("s")) {
			dutyCycleComboBoxString.setSelectedIndex(0);
		} else if (clock.getUnit().equals("ms")) {
			dutyCycleComboBoxString.setSelectedIndex(1);
		} else if (clock.getUnit().equals("\u03BCs")) {
			dutyCycleComboBoxString.setSelectedIndex(2);
		} else if (clock.getUnit().equals("ns")) {
			dutyCycleComboBoxString.setSelectedIndex(3);
		}
		dutyCycleComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(dutyCycleComboBoxString, constraints);
		attributesBoxPanel.add(dutyCycleComboBoxString);
		//

			JLabel startTimeLabel = new JLabel("StartTime Tm : ");
		constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(startTimeLabel, constraints);
		attributesBoxPanel.add(startTimeLabel);

		if (clock.getStartTime() == -1) { 
			startTimeTextField = new JTextField(10);
		} else {
			startTimeTextField = new JTextField("" + clock.getStartTime(), 10); 
		}
		constraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(startTimeTextField, constraints);
		attributesBoxPanel.add(startTimeTextField);

		listStartTimeString = new String[4];
		listStartTimeString[0] = "s";
		listStartTimeString[1] = "ms";
		listStartTimeString[2] = "\u03BCs";
		listStartTimeString[3] = "ns";
		startTimeComboBoxString = new JComboBox<String>(listStartTimeString);
		if (clock.getUnit().equals("") || clock.getUnit().equals("s")) {
			startTimeComboBoxString.setSelectedIndex(0);
		} else if (clock.getUnit().equals("ms")) {
			startTimeComboBoxString.setSelectedIndex(1);
		} else if (clock.getUnit().equals("\u03BCs")) {
			startTimeComboBoxString.setSelectedIndex(2);
		} else if (clock.getUnit().equals("ns")) {
			startTimeComboBoxString.setSelectedIndex(3);
		}
		startTimeComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(startTimeComboBoxString, constraints);
		attributesBoxPanel.add(startTimeComboBoxString);


		//
		JLabel posFirstLabel = new JLabel("PosFirst Tm : ");
		constraints = new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(posFirstLabel, constraints);
		attributesBoxPanel.add(posFirstLabel);

		if (clock.getPosFirst() == false) { 
			posFirstTextField = new JTextField(10);
		} else {
			posFirstTextField = new JTextField("" + clock.getPosFirst(), 10); 
		}
		constraints = new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(posFirstTextField, constraints);
		attributesBoxPanel.add(posFirstTextField);

		listPosFirstString = new String[2];
		listPosFirstString[0] = "no";
		listPosFirstString[1] = "yes";
	
		posFirstComboBoxString = new JComboBox<String>(listPosFirstString);
		if (clock.getUnit().equals("") || clock.getUnit().equals("no")) {
			posFirstComboBoxString.setSelectedIndex(0);
		} else if (clock.getUnit().equals("yes")) {
			posFirstComboBoxString.setSelectedIndex(1);
		}
		posFirstComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 10, 15, 10), 0, 0);
		gridBag.setConstraints(posFirstComboBoxString, constraints);
		attributesBoxPanel.add(posFirstComboBoxString);
		//
		//
		
		
		attributesBox.add(attributesBoxPanel); 
		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); 
		// --- Parameters ---//
		/*	parametersMainPanel.setLayout(new BorderLayout());

		Box parametersBox = Box.createVerticalBox();
		parametersBox.setBorder(BorderFactory.createTitledBorder("Setting TDF clock parameters"));

		JPanel clockPanel = new JPanel(new GridLayout(3, 1));

		// Struct
		JPanel structPanel = new JPanel();
		structPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		GridBagLayout gridBagParameter = new GridBagLayout();
		GridBagConstraints constraintParameter = new GridBagConstraints();
		structPanel.setLayout(gridBagParameter);
		TitledBorder border = new TitledBorder("Struct :");
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitlePosition(TitledBorder.TOP);
		structPanel.setBorder(border);

		JLabel nameParameterLabel = new JLabel("identifier");
		constraintParameter = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(nameParameterLabel, constraintParameter);
		structPanel.add(nameParameterLabel);

		nameStructTextField = new JTextField();
		constraintParameter = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(nameStructTextField, constraintParameter);
		structPanel.add(nameStructTextField);

		JLabel egalLabel = new JLabel("=");
		constraintParameter = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(egalLabel, constraintParameter);
		structPanel.add(egalLabel);

		JLabel valueParameterLabel = new JLabel("value");
		constraintParameter = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(valueParameterLabel, constraintParameter);
		structPanel.add(valueParameterLabel);

		valueStructTextField = new JTextField();
		constraintParameter = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(valueStructTextField, constraintParameter);
		structPanel.add(valueStructTextField);

		JLabel pointsLabel = new JLabel(":");
		constraintParameter = new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(pointsLabel, constraintParameter);
		structPanel.add(pointsLabel);

		JLabel constantLabel = new JLabel("const");
		constraintParameter = new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(constantLabel, constraintParameter);
		structPanel.add(constantLabel);

		constantStructRadioButton = new JRadioButton();
		constantStructRadioButton.setActionCommand("Const");
		constantStructRadioButton.setSelected(false);
		constantStructRadioButton.addActionListener(this);
		constraintParameter = new GridBagConstraints(4, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(constantStructRadioButton, constraintParameter);
		structPanel.add(constantStructRadioButton);

		JLabel typeParameterLabel = new JLabel("type");
		constraintParameter = new GridBagConstraints(5, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(typeParameterLabel, constraintParameter);
		structPanel.add(typeParameterLabel);

		listTypeStructString = new String[6];
		listTypeStructString[0] = "bool";
		listTypeStructString[1] = "double";
		listTypeStructString[2] = "float";
		listTypeStructString[3] = "int";
		listTypeStructString[4] = "long";
		listTypeStructString[5] = "short";
		typeStructComboBoxString = new JComboBox<String>(listTypeStructString);
		typeStructComboBoxString.setSelectedIndex(0);
		typeStructComboBoxString.addActionListener(this);
		constraintParameter = new GridBagConstraints(5, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(typeStructComboBoxString, constraintParameter);
		structPanel.add(typeStructComboBoxString);

		JButton addModifyButton = new JButton("Add / Modify parameter");
		addModifyButton.setActionCommand("Add_Modify_Struct");
		addModifyButton.addActionListener(this);
		constraintParameter = new GridBagConstraints(0, 2, 6, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBagParameter.setConstraints(addModifyButton, constraintParameter);
		structPanel.add(addModifyButton);

		clockPanel.add(structPanel);

		// Template
		JPanel templatePanel = new JPanel();
		templatePanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		GridBagLayout templateGridBag = new GridBagLayout();
		GridBagConstraints templateConstraint = new GridBagConstraints();
		templatePanel.setLayout(templateGridBag);
		TitledBorder templateBorder = new TitledBorder("Template :");
		templateBorder.setTitleJustification(TitledBorder.CENTER);
		templateBorder.setTitlePosition(TitledBorder.TOP);
		templatePanel.setBorder(templateBorder);

		JLabel nameTemplateLabel = new JLabel("identifier");
		templateConstraint = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(nameTemplateLabel, templateConstraint);
		templatePanel.add(nameTemplateLabel);

		nameTemplateTextField = new JTextField(clock.getNameTemplate());
		templateConstraint = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(nameTemplateTextField, templateConstraint);
		templatePanel.add(nameTemplateTextField);
        
        //CHANGES
        JLabel egalTemplateLabel = new JLabel("=");
		templateConstraint = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(egalTemplateLabel, templateConstraint);
		templatePanel.add(egalTemplateLabel);

		JLabel valueTemplateLabel = new JLabel("value");
		templateConstraint = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(valueTemplateLabel, templateConstraint);
		templatePanel.add(valueTemplateLabel);

		valueTemplateTextField = new JTextField(clock.getValueTemplate());
		templateConstraint = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(valueTemplateTextField, templateConstraint);
		templatePanel.add(valueTemplateTextField);
        //CHANGES

		JLabel pointsTemplateLabel = new JLabel(":");
		templateConstraint = new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(pointsTemplateLabel, templateConstraint);
		templatePanel.add(pointsTemplateLabel);

		JLabel typeTemplateLabel = new JLabel("type");
		templateConstraint = new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(typeTemplateLabel, templateConstraint);
		templatePanel.add(typeTemplateLabel);

		listTypeTemplateString = new String[1];
		listTypeTemplateString[0] = "int";
		typeTemplateComboBoxString = new JComboBox<String>(listTypeTemplateString);
		if (clock.getTypeTemplate().equals("int") || clock.getTypeTemplate().equals("")) {
			typeTemplateComboBoxString.setSelectedIndex(0);
		}
		typeTemplateComboBoxString.addActionListener(this);
		templateConstraint = new GridBagConstraints(4, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(typeTemplateComboBoxString, templateConstraint);
		templatePanel.add(typeTemplateComboBoxString);

		JButton OKButton = new JButton("OK");
		OKButton.setActionCommand("OK");
		OKButton.addActionListener(this);
		templateConstraint = new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		templateGridBag.setConstraints(OKButton, templateConstraint);
		templatePanel.add(OKButton);

		clockPanel.add(templatePanel);

		// Typedef
		JPanel typedefPanel = new JPanel();
		typedefPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		GridBagLayout typedefGridBag = new GridBagLayout();
		GridBagConstraints typedefConstraint = new GridBagConstraints();
		typedefPanel.setLayout(typedefGridBag);
		TitledBorder typedefBorder = new TitledBorder("Typedef :");
		typedefBorder.setTitleJustification(TitledBorder.CENTER);
		typedefBorder.setTitlePosition(TitledBorder.TOP);
		typedefPanel.setBorder(typedefBorder);

		JLabel nameTypedefLabel = new JLabel("identifier");
		typedefConstraint = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		typedefGridBag.setConstraints(nameTypedefLabel, typedefConstraint);
		typedefPanel.add(nameTypedefLabel);

		nameTypedefTextField = new JTextField();
		if (clock.getListTypedef().isEmpty()) {
			nameTypedefTextField.setEditable(false);
		} else {
			nameTypedefTextField.setEditable(true);
		}
		typedefConstraint = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		typedefGridBag.setConstraints(nameTypedefTextField, typedefConstraint);
		typedefPanel.add(nameTypedefTextField);

		JLabel pointsTypedefLabel = new JLabel(":");
		typedefConstraint = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		typedefGridBag.setConstraints(pointsTypedefLabel, typedefConstraint);
		typedefPanel.add(pointsTypedefLabel);

		JLabel typeTypedefLabel = new JLabel("type");
		typedefConstraint = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		typedefGridBag.setConstraints(typeTypedefLabel, typedefConstraint);
		typedefPanel.add(typeTypedefLabel);

		listTypeTypedefString = new String[1];
		listTypeTypedefString[0] = "sc_dt::sc_int";
		typeTypedefComboBoxString = new JComboBox<String>(listTypeTypedefString);
		typeTypedefComboBoxString.setSelectedIndex(0);
		if (clock.getListTypedef().isEmpty()) {
			typeTypedefComboBoxString.setEnabled(false);
		} else {
			typeTypedefComboBoxString.setEnabled(true);
		}
		typeTypedefComboBoxString.addActionListener(this);
		typedefConstraint = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		typedefGridBag.setConstraints(typeTypedefComboBoxString, typedefConstraint);
		typedefPanel.add(typeTypedefComboBoxString);

		addModifyTypedefButton = new JButton("Add / Modify typedef");
		addModifyTypedefButton.setActionCommand("Add_Modify_Typedef");
		addModifyTypedefButton.addActionListener(this);
		if (clock.getListTypedef().isEmpty()) {
			addModifyTypedefButton.setEnabled(false);
		} else {
			addModifyTypedefButton.setEnabled(true);
		}
		typedefConstraint = new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		typedefGridBag.setConstraints(addModifyTypedefButton, typedefConstraint);
		typedefPanel.add(addModifyTypedefButton);

		clockPanel.add(typedefPanel);

		parametersBox.add(clockPanel); 
		parametersMainPanel.add(parametersBox, BorderLayout.WEST); 

		Box managingParametersBox = Box.createVerticalBox();

		JPanel managingParameterBoxPanel = new JPanel(new GridLayout(3, 1));
		managingParameterBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));

		JPanel listStructPanel = new JPanel();
		TitledBorder listStructBorder = new TitledBorder("Managing struct :");
		listStructBorder.setTitleJustification(TitledBorder.CENTER);
		listStructBorder.setTitlePosition(TitledBorder.TOP);
		listStructPanel.setBorder(listStructBorder);

		structListModel = clock.getListStruct();
		structList = new JList<String>(structListModel);
		structList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		structList.setLayoutOrientation(JList.VERTICAL);
		structList.setSelectedIndex(-1);
		structList.setVisibleRowCount(5);
		structList.addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(structList);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(300, 100));
		listStructPanel.add(scrollPane);
		managingParameterBoxPanel.add(listStructPanel);

		JPanel listTypedefPanel = new JPanel();
		TitledBorder listTypedefBorder = new TitledBorder("Managing typedef :");
		listTypedefBorder.setTitleJustification(TitledBorder.CENTER);
		listTypedefBorder.setTitlePosition(TitledBorder.TOP);
		listTypedefPanel.setBorder(listTypedefBorder);

		typedefListModel = clock.getListTypedef();
		typedefList = new JList<String>(typedefListModel);
		typedefList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typedefList.setLayoutOrientation(JList.VERTICAL);
		typedefList.setSelectedIndex(-1);
		typedefList.setVisibleRowCount(5);
		typedefList.addListSelectionListener(this);
		JScrollPane typedefScrollPane = new JScrollPane(typedefList);
		typedefScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		typedefScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		typedefScrollPane.setPreferredSize(new Dimension(300, 100));
		listTypedefPanel.add(typedefScrollPane);
		managingParameterBoxPanel.add(listTypedefPanel);

		GridBagLayout buttonGridBag = new GridBagLayout();
		GridBagConstraints buttonconstraints = new GridBagConstraints();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		buttonPanel.setLayout(buttonGridBag);

		upButton = new JButton("Up");
		upButton.setActionCommand("Up");
		upButton.setEnabled(false);
		upButton.addActionListener(this);
		buttonconstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		buttonGridBag.setConstraints(upButton, buttonconstraints);
		buttonPanel.add(upButton);

		downButton = new JButton("Down");
		downButton.setActionCommand("Down");
		downButton.setEnabled(false);
		downButton.addActionListener(this);
		buttonconstraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0);
		buttonGridBag.setConstraints(downButton, buttonconstraints);
		buttonPanel.add(downButton);

		removeButton = new JButton("Remove parameter");
		removeButton.setActionCommand("Remove");
		removeButton.setEnabled(false);
		removeButton.addActionListener(this);
		buttonconstraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 15, 10), 0, 0);
		buttonGridBag.setConstraints(removeButton, buttonconstraints);
		buttonPanel.add(removeButton);

		managingParameterBoxPanel.add(buttonPanel);

		managingParametersBox.add(managingParameterBoxPanel); 
		parametersMainPanel.add(managingParametersBox, BorderLayout.EAST); 

		// --- ProcessCode ---//
		processMainPanel.setLayout(new BorderLayout());

		Box codeBox = Box.createVerticalBox();
		codeBox.setBorder(BorderFactory.createTitledBorder("Behavior function of TDF clock"));

		JPanel codeBoxPanel = new JPanel(new BorderLayout());

		StringBuffer stringbuf = encode(clock.getProcessCode());
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
		processScrollPane.setPreferredSize(new Dimension(200, 300));
		processScrollPane.setBorder(new EmptyBorder(15, 10, 15, 10));

		codeBoxPanel.add(processScrollPane, BorderLayout.SOUTH);

		codeBox.add(codeBoxPanel);
		processMainPanel.add(codeBox, BorderLayout.PAGE_START);
        
        // --- ContructorCode --- //
        contructorMainPanel.setLayout(new BorderLayout());

		Box codeBox2 = Box.createVerticalBox();
		codeBox2.setBorder(BorderFactory.createTitledBorder("Contructor code of TDF clock"));

		JPanel codeBoxPanel2 = new JPanel(new BorderLayout());
        
        //StringBuffer stringbuf2 = encode(clock.getConstructorCode());
		//String beginString2 = stringbuf2.toString();
		//finalString = beginString2.replaceAll("\t}", "}");
        finalString = clock.getConstructorCode();

		constructorCodeTextArea = new JTextArea(finalString);
		constructorCodeTextArea.setSize(100, 100);
		constructorCodeTextArea.setTabSize(2);

		constructorCodeTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
		constructorCodeTextArea.setLineWrap(true);
		constructorCodeTextArea.setWrapStyleWord(true);

		JScrollPane constructorScrollPane = new JScrollPane(constructorCodeTextArea);
		constructorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		constructorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		constructorScrollPane.setPreferredSize(new Dimension(200, 300));
		constructorScrollPane.setBorder(new EmptyBorder(15, 10, 15, 10));

		codeBoxPanel2.add(constructorScrollPane, BorderLayout.SOUTH);

		codeBox2.add(codeBoxPanel2);
		contructorMainPanel.add(codeBox2, BorderLayout.PAGE_START);
		*/
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
		pack();
		this.getRootPane().setDefaultButton(saveCloseButton);
	}


	public void actionPerformed(ActionEvent e) {
	    clock.setName(nameTextField.getText());		
	    // clock.setFrequency(frequencyTextField.getText());
	    //clock.setDutyCycle(dutyCycleTextField.getText());
	    //clock.setStartTime(startTimeTextField.getText());
	    clock.setPosFirst((boolean)posFirstComboBoxString.getSelectedItem()); //ToDo boolean
	    clock.setUnit((String) unitComboBoxString.getSelectedItem());


            if ("Save_Close".equals(e.getActionCommand())) {
			clock.setValue(new String(nameTextField.getText()));

			if (!(frequencyTextField.getText().isEmpty())) {
				Boolean frequencyValueInteger = false;
				try {
					Double.parseDouble(frequencyTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "Frequency is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					frequencyValueInteger = true;
				}
				if (frequencyValueInteger == false) {
					clock.setFrequency(Double.parseDouble(frequencyTextField.getText()));
					clock.setUnit((String) frequencyComboBoxString.getSelectedItem());
				}
			} else {
				clock.setFrequency(-1);
				clock.setUnit("");
			}

				if (!(dutyCycleTextField.getText().isEmpty())) {
				Boolean dutyCycleValueInteger = false;
				try {
					Double.parseDouble(dutyCycleTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "DutyCycle is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					dutyCycleValueInteger = true;
				}
				if (dutyCycleValueInteger == false) {
					clock.setDutyCycle(Double.parseDouble(dutyCycleTextField.getText()));
					clock.setUnit((String) dutyCycleComboBoxString.getSelectedItem());
				}
			} else {
				clock.setDutyCycle(-1);
				clock.setUnit("");
			}

				if (!(startTimeTextField.getText().isEmpty())) {
				Boolean startTimeValueInteger = false;
				try {
					Double.parseDouble(startTimeTextField.getText());
				} catch (NumberFormatException e1) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "StartTime is not a Double", "Warning !",
							JOptionPane.WARNING_MESSAGE);
					startTimeValueInteger = true;
				}
				if (startTimeValueInteger == false) {
					clock.setStartTime(Double.parseDouble(startTimeTextField.getText()));
					clock.setUnit((String) startTimeComboBoxString.getSelectedItem());
				}
			} else {
				clock.setStartTime(-1);
				clock.setUnit("");
			}	

				//clock.setProcessCode(processCodeTextArea.getText());
			// clock.setConstructorCode(constructorCodeTextArea.getText());
			clock.setListStruct(structListModel);
			clock.setNameTemplate(nameTemplateTextField.getText());
			clock.setTypeTemplate((String) typeTemplateComboBoxString.getSelectedItem());
            clock.setValueTemplate(valueTemplateTextField.getText());
			clock.setListTypedef(typedefListModel);

			this.dispose();
		}

	    
	    /*	if ("Save_Close".equals(e.getActionCommand())) {
			clock.setValue(new String(nameTextField.getText()));
			this.dispose();
		}*

		if ("Cancel".equals(e.getActionCommand())) {
			this.dispose();
			}*/
	}
}
