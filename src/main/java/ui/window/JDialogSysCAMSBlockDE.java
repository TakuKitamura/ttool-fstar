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
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * Class JDialogSystemCAMSBlockDE 
 * Dialog for managing of SystemC-AMS DE Block
 * Creation: 26/04/2018
 * @version 1.0 26/04/2018
 * @author Irina Kit Yan LEE
 * @version 1.1 27/10/2019
 * @author Daniela Genius
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSBlockDE extends JDialog implements ActionListener, ListSelectionListener {

	private JTextField nameTextField;
        private JTextField clockTextField;
    
//	private JTextField periodTextField;
//	private String listPeriodString[];
//	private JComboBox<String> periodComboBoxString;

	private JPanel parametersMainPanel;
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
	
	private JPanel codeMainPanel;
	private JTextField nameFnTextField;
	private JButton nameFnButton;
	private JTextArea codeTextArea;
	private String finalString;
    
        private JRadioButton sensitiveRadioButton;  
	private String listSensitiveString[];
	private JComboBox<String> sensitiveComboBoxString;
    
	private SysCAMSBlockDE block;

	public JDialogSysCAMSBlockDE(SysCAMSBlockDE block) {
		this.setTitle("Setting DE Block Attributes");
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

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
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		JPanel attributesMainPanel = new JPanel();
		if (block.getFather() != null) {
			JTabbedPane tabbedPane = new JTabbedPane();
			parametersMainPanel = new JPanel();
			codeMainPanel = new JPanel();
			tabbedPane.add("Attributes", attributesMainPanel);
			tabbedPane.add("Parameters", parametersMainPanel);
			tabbedPane.add("Method Code", codeMainPanel);

			mainPanel.add(tabbedPane, BorderLayout.NORTH); 
		} else {
			mainPanel.add(attributesMainPanel, BorderLayout.NORTH); 
		}

		// --- Attributes ---//
		attributesMainPanel.setLayout(new BorderLayout());

		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting DE block attributes"));

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

		if (block.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(block.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(nameTextField, constraints);
		attributesBoxPanel.add(nameTextField);

		JLabel labelClock = new JLabel("Clock : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelClock, constraints);
		attributesBoxPanel.add(labelClock);

		if (block.getValue().toString().equals("")) {
			clockTextField = new JTextField(10);
		} else {
			clockTextField = new JTextField(block.getClockName().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(clockTextField, constraints);
		attributesBoxPanel.add(clockTextField);


JPanel sensitivePanel = new JPanel(new FlowLayout());

		sensitiveRadioButton = new JRadioButton();
		sensitiveRadioButton.setActionCommand("Sensitive");
		sensitiveRadioButton.setSelected(block.getSensitive());
		sensitiveRadioButton.addActionListener(this);
		sensitivePanel.add(sensitiveRadioButton);

		JLabel sensitiveLabel = new JLabel("Sensitive");
		sensitivePanel.add(sensitiveLabel);

		constraints = new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(sensitivePanel, constraints);
		attributesBoxPanel.add(sensitivePanel);

		
		//JLabel labelClock = new JLabel("Clock sensitivity : ");
	
	listSensitiveString = new String[3];
		listSensitiveString[0] = "";
        listSensitiveString[1] = "positive";
		listSensitiveString[2] = "negative";
		sensitiveComboBoxString = new JComboBox<String>(listSensitiveString);
		if (block.getClockSensitivityMethod().equals("")) {
			sensitiveComboBoxString.setSelectedIndex(0);
        } else if (block.getClockSensitivityMethod().equals("positive")) {
            sensitiveComboBoxString.setSelectedIndex(1);
		} else if (block.getClockSensitivityMethod().equals("negative")) {
			sensitiveComboBoxString.setSelectedIndex(2);
		}
		sensitiveComboBoxString.setActionCommand("Sensitive_method");
		sensitiveComboBoxString.setEnabled(block.getSensitive());
		sensitiveComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(sensitiveComboBoxString, constraints);
		//boxPanel.add(sensitiveComboBoxString);
		attributesBoxPanel.add(sensitiveComboBoxString);
		
		attributesBox.add(attributesBoxPanel);
		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); 

		if (block.getFather() != null) {
			// --- Parameters ---//
			parametersMainPanel.setLayout(new BorderLayout());

			Box parametersBox = Box.createVerticalBox();
			parametersBox.setBorder(BorderFactory.createTitledBorder("Setting TDF block parameters"));

			JPanel blockPanel = new JPanel(new GridLayout(3, 1));

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

			blockPanel.add(structPanel);

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

			nameTemplateTextField = new JTextField(block.getNameTemplate());
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

            valueTemplateTextField = new JTextField(block.getValueTemplate());
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
			if (block.getTypeTemplate().equals("int") || block.getTypeTemplate().equals("")) {
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

			blockPanel.add(templatePanel);

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
			if (block.getListTypedef().isEmpty()) {
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
			if (block.getListTypedef().isEmpty()) {
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
			if (block.getListTypedef().isEmpty()) {
				addModifyTypedefButton.setEnabled(false);
			} else {
				addModifyTypedefButton.setEnabled(true);
			}
			typedefConstraint = new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(5, 10, 5, 10), 0, 0);
			typedefGridBag.setConstraints(addModifyTypedefButton, typedefConstraint);
			typedefPanel.add(addModifyTypedefButton);

			blockPanel.add(typedefPanel);

			parametersBox.add(blockPanel); 
			parametersMainPanel.add(parametersBox, BorderLayout.WEST); 

			Box managingParametersBox = Box.createVerticalBox();

			JPanel managingParameterBoxPanel = new JPanel(new GridLayout(3, 1));
			managingParameterBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));

			JPanel listStructPanel = new JPanel();
			TitledBorder listStructBorder = new TitledBorder("Managing struct :");
			listStructBorder.setTitleJustification(TitledBorder.CENTER);
			listStructBorder.setTitlePosition(TitledBorder.TOP);
			listStructPanel.setBorder(listStructBorder);

			structListModel = block.getListStruct();
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

			typedefListModel = block.getListTypedef();
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
			codeMainPanel.setLayout(new BorderLayout());

			Box codeBox = Box.createVerticalBox();
			codeBox.setBorder(BorderFactory.createTitledBorder("Behavior function of DE block"));

			JPanel codeBoxPanel = new JPanel(new BorderLayout());

			GridBagLayout nameGridBag = new GridBagLayout();
			GridBagConstraints nameConstraints = new GridBagConstraints();
			JPanel namePanel = new JPanel();
			namePanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
			namePanel.setLayout(nameGridBag);

			JLabel nameFnLabel = new JLabel("Name of the method :");
			nameConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 10, 5, 10), 0, 0);
			nameGridBag.setConstraints(nameFnLabel, nameConstraints);
			namePanel.add(nameFnLabel);
			
			nameFnTextField = new JTextField(block.getNameFn(), 20);
			nameConstraints = new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 10, 5, 10), 0, 0);
			nameGridBag.setConstraints(nameFnTextField, nameConstraints);
			namePanel.add(nameFnTextField);
			
			nameFnButton = new JButton("OK");
			nameFnButton.setActionCommand("Name_OK");
			nameFnButton.addActionListener(this);
			nameConstraints = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 10, 5, 10), 0, 0);
			nameGridBag.setConstraints(nameFnButton, nameConstraints);
			namePanel.add(nameFnButton);
			
			codeBoxPanel.add(namePanel, BorderLayout.NORTH);
			
			StringBuffer stringbuf = encode(block.getCode());
			String beginString = stringbuf.toString();
			finalString = beginString.replaceAll("\t}", "}");

			codeTextArea = new JTextArea(finalString);
			codeTextArea.setSize(100, 100);
			codeTextArea.setTabSize(2);

			codeTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
			codeTextArea.setLineWrap(true);
			codeTextArea.setWrapStyleWord(true);

			JScrollPane codeScrollPane = new JScrollPane(codeTextArea);
			codeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			codeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			codeScrollPane.setPreferredSize(new Dimension(200, 300));
			codeScrollPane.setBorder(new EmptyBorder(15, 10, 15, 10));

			codeBoxPanel.add(codeScrollPane, BorderLayout.SOUTH);

			codeBox.add(codeBoxPanel);
			codeMainPanel.add(codeBox, BorderLayout.PAGE_START);
		}

		// -- Button -- /
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
		if ("Name_OK".equals(e.getActionCommand())) {
			if (!nameFnTextField.getText().equals("")) {
				codeTextArea.setText("void " + nameFnTextField.getText() + "() {\n\n}");
			} else {
				JDialog msg = new JDialog(this);
				msg.setLocationRelativeTo(null);
				JOptionPane.showMessageDialog(msg, "This method has no name. Please add a name for this method.", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		
		if (!(clockTextField.getText().isEmpty())) {
		    block.setClockName(clockTextField.getText());
		}else{
		    block.setClockName("");
		}
		
		if (block.getFather() != null) {
			if ("OK".equals(e.getActionCommand())) {
				nameTypedefTextField.setEditable(true);
				typeTypedefComboBoxString.setEnabled(true);
				addModifyTypedefButton.setEnabled(true);
			}

			if ("Add_Modify_Struct".equals(e.getActionCommand())) {
				listTmpStruct = new ArrayList<String>();
				Boolean alreadyExist = false;
				int alreadyExistId = -1;
				String type = (String) typeStructComboBoxString.getSelectedItem();
				String s = null;

				Boolean valueBoolean = false, valueInteger = false, valueDouble = false, valueLong = false, nameEmpty = false;

				if (nameStructTextField.getText().isEmpty()) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The name of struct is empty", "Warning !",
							JOptionPane.WARNING_MESSAGE);	
					nameEmpty = true;
				}

				for (int i = 0; i < structListModel.getSize(); i++) {
					if (nameStructTextField.getText().equals(structListModel.elementAt(i).split("\\s")[0])) {
						alreadyExist = true;
						alreadyExistId = i;
					}
				}

				if (alreadyExist == false) {
					try {
						if (type.equals("bool")) {
							Boolean.parseBoolean(valueStructTextField.getText());
						} else if (type.equals("double")) {
							Double.parseDouble(valueStructTextField.getText());
						} else if (type.equals("float")) {
							Float.parseFloat(valueStructTextField.getText());
						} else if (type.equals("int")) {
							Integer.parseInt(valueStructTextField.getText());
						} else if (type.equals("long")) {
							Long.parseLong(valueStructTextField.getText());
						} else if (type.equals("short")) {
							Short.parseShort(valueStructTextField.getText());
						}
					} catch (NumberFormatException e1) {
						if (type.equals("bool")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Boolean", "Warning !",
									JOptionPane.WARNING_MESSAGE);	
							valueBoolean = true;
						} else if (type.equals("double")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Double", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("float")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Float", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("int")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Integer", "Warning !",
									JOptionPane.WARNING_MESSAGE);		
							valueDouble = true;
						} else if (type.equals("long")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Long", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						} else if (type.equals("short")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Short", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						}
					}

					if ((valueBoolean == false) && (valueInteger == false) && (valueDouble == false) && (valueLong == false) && (nameEmpty == false)) {
						s = nameStructTextField.getText() + " = ";

						if (type.equals("bool")) {
							s = s + Boolean.parseBoolean(valueStructTextField.getText()) + " : ";
						} else if (type.equals("double")) {	
							s = s + Double.parseDouble(valueStructTextField.getText()) + " : ";
						} else if (type.equals("float")) {	
							s = s + Float.parseFloat(valueStructTextField.getText()) + " : ";
						} else if (type.equals("int")) {
							s = s + Integer.parseInt(valueStructTextField.getText()) + " : ";
						} else if (type.equals("long")) {
							s = s + Long.parseLong(valueStructTextField.getText()) + " : ";
						} else if (type.equals("short")) {
							s = s + Short.parseShort(valueStructTextField.getText()) + " : ";
						}

						if (constantStructRadioButton.isSelected()) {
							s = s + "const " + type;
						} else {
							s = s + type;
						}
						structListModel.addElement(s);
						listTmpStruct.add(s);
					}
				} else {
					try {
						if (type.equals("bool")) {
							Boolean.parseBoolean(valueStructTextField.getText());
						} else if (type.equals("double")) {
							Double.parseDouble(valueStructTextField.getText());
						} else if (type.equals("float")) {
							Float.parseFloat(valueStructTextField.getText());
						} else if (type.equals("int")) {
							Integer.parseInt(valueStructTextField.getText());
						} else if (type.equals("long")) {
							Long.parseLong(valueStructTextField.getText());
						} else if (type.equals("short")) {
							Short.parseShort(valueStructTextField.getText());
						}
					} catch (NumberFormatException e1) {
						if (type.equals("bool")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Boolean", "Warning !",
									JOptionPane.WARNING_MESSAGE);	
							valueBoolean = true;
						} else if (type.equals("double")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Double", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("float")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Float", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("int")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Integer", "Warning !",
									JOptionPane.WARNING_MESSAGE);		
							valueDouble = true;
						} else if (type.equals("long")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Long", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						} else if (type.equals("short")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Short", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						}
					}

					if ((valueBoolean == false) && (valueInteger == false) && (valueDouble == false) && (valueLong == false) && (nameEmpty == false)) {
						s = nameStructTextField.getText() + " = ";

						if (type.equals("bool")) {
							s = s + Boolean.parseBoolean(valueStructTextField.getText()) + " : ";
						} else if (type.equals("double")) {	
							s = s + Double.parseDouble(valueStructTextField.getText()) + " : ";
						} else if (type.equals("float")) {	
							s = s + Float.parseFloat(valueStructTextField.getText()) + " : ";
						} else if (type.equals("int")) {
							s = s + Integer.parseInt(valueStructTextField.getText()) + " : ";
						} else if (type.equals("long")) {
							s = s + Long.parseLong(valueStructTextField.getText()) + " : ";
						} else if (type.equals("short")) {
							s = s + Short.parseShort(valueStructTextField.getText()) + " : ";
						}

						if (constantStructRadioButton.isSelected()) {
							s = s + "const " + type;
						} else {
							s = s + type;
						}
						structListModel.setElementAt(s, alreadyExistId);
						listTmpStruct.add(s);
					}
				}
			}

			if ("Add_Modify_Typedef".equals(e.getActionCommand())) {
				listTmpTypedef = new ArrayList<String>();
				Boolean alreadyExist = false;
				int alreadyExistId = -1;
				String type = (String) typeTypedefComboBoxString.getSelectedItem();
				String s = null;

				Boolean nameEmpty = false;

				if (nameTypedefTextField.getText().isEmpty()) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The name of typedef is empty", "Warning !",
							JOptionPane.WARNING_MESSAGE);	
					nameEmpty = true;
				}

				if (nameEmpty == false) {
					for (int i = 0; i < typedefListModel.getSize(); i++) {
						if (nameTypedefTextField.getText().equals(typedefListModel.elementAt(i).split("\\s")[0])) {
							alreadyExist = true;
							alreadyExistId = i;
						}
					}

					if (alreadyExist == false) {
						s = nameTypedefTextField.getText() + " : " + type;
						typedefListModel.addElement(s);
						listTmpTypedef.add(s);
					} else {
						s = nameTypedefTextField.getText() + " : " + type;
						typedefListModel.setElementAt(s, alreadyExistId);
						listTmpTypedef.add(s);
					}
				}
			}


			if ("Remove".equals(e.getActionCommand())) {
				if (structBool == true) {
					if (structListModel.getSize() >= 1) {
						structListModel.remove(structList.getSelectedIndex());
					}
				}
				if (typedefBool == true) {
					if (typedefListModel.getSize() >= 1) {
						typedefListModel.remove(typedefList.getSelectedIndex());
					}
				}
			}

			if ("Up".equals(e.getActionCommand())) {
				if (structBool == true) {
					if (structList.getSelectedIndex() >= 1) {
						String sprev = structListModel.get(structList.getSelectedIndex()-1);
						structListModel.remove(structList.getSelectedIndex()-1);
						structListModel.add(structList.getSelectedIndex()+1, sprev);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter up", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
				if (typedefBool == true) {
					if (typedefList.getSelectedIndex() >= 1) {
						String sprev = typedefListModel.get(typedefList.getSelectedIndex()-1);
						typedefListModel.remove(typedefList.getSelectedIndex()-1);
						typedefListModel.add(typedefList.getSelectedIndex()+1, sprev);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter up", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			if ("Down".equals(e.getActionCommand())) {
				if (structBool == true) {
					if (structList.getSelectedIndex() < structListModel.getSize()-1) {
						String snext = structListModel.get(structList.getSelectedIndex()+1);
						structListModel.remove(structList.getSelectedIndex()+1);
						structListModel.add(structList.getSelectedIndex(), snext);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter down", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
				if (typedefBool == true) {
					if (typedefList.getSelectedIndex() < typedefListModel.getSize()-1) {
						String snext = typedefListModel.get(typedefList.getSelectedIndex()+1);
						typedefListModel.remove(typedefList.getSelectedIndex()+1);
						typedefListModel.add(typedefList.getSelectedIndex(), snext);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter down", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
		
		if ("Save_Close".equals(e.getActionCommand())) {
			block.setValue(new String(nameTextField.getText()));

			if (block.getFather() != null) {
				block.setListStruct(structListModel);
				block.setNameTemplate(nameTemplateTextField.getText());
				block.setTypeTemplate((String) typeTemplateComboBoxString.getSelectedItem());
                block.setValueTemplate(valueTemplateTextField.getText());
				block.setListTypedef(typedefListModel);
				block.setNameFn(nameFnTextField.getText());
				block.setCode(codeTextArea.getText());
			}

			block.setSensitive(sensitiveRadioButton.isSelected());
			block.setClockSensitivityMethod((String) sensitiveComboBoxString.getSelectedItem());
			
			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			if (block.getFather() != null) {
				if (listTmpStruct != null) {
					for (String s : listTmpStruct) {
						structListModel.removeElement(s);
					}
				}
				if (listTmpTypedef != null) {
					for (String s : listTmpTypedef) {
						typedefListModel.removeElement(s);
					}
				}
			}
			this.dispose();
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		JList listTmp = (JList) e.getSource();
		if (listTmp.equals(structList)) {
			structBool = true;
			typedefBool = false;
		}
		if (listTmp.equals(typedefList)) {
			typedefBool = true;
			structBool = false;
		}

		if (e.getValueIsAdjusting() == false) {
			if (structBool == true) {
				if (structList.getSelectedIndex() != -1) {
					String select = structListModel.get(structList.getSelectedIndex());
					String[] splita = select.split(" = ");
					nameStructTextField.setText(splita[0]);
					String[] splitb = splita[1].split(" : ");
					valueStructTextField.setText(splitb[0]);
					String[] splitc = splitb[1].split(" ");

					if (splitc[0].equals("const")) {
						constantStructRadioButton.setSelected(true);
						if (splitc[1].equals("bool")) {
							typeStructComboBoxString.setSelectedIndex(0);
						} else if (splitc[1].equals("double")) {
							typeStructComboBoxString.setSelectedIndex(1);
						} else if (splitc[1].equals("float")) {
							typeStructComboBoxString.setSelectedIndex(2);
						} else if (splitc[1].equals("int")) {
							typeStructComboBoxString.setSelectedIndex(3);
						} else if (splitc[1].equals("long")) {
							typeStructComboBoxString.setSelectedIndex(4);
						} else if (splitc[1].equals("short")) {
							typeStructComboBoxString.setSelectedIndex(5);
						}
					} else {
						constantStructRadioButton.setSelected(false);
						if (splitc[0].equals("bool")) {
							typeStructComboBoxString.setSelectedIndex(0);
						} else if (splitc[0].equals("double")) {
							typeStructComboBoxString.setSelectedIndex(1);
						} else if (splitc[0].equals("float")) {
							typeStructComboBoxString.setSelectedIndex(2);
						} else if (splitc[0].equals("int")) {
							typeStructComboBoxString.setSelectedIndex(3);
						} else if (splitc[0].equals("long")) {
							typeStructComboBoxString.setSelectedIndex(4);
						} else if (splitc[0].equals("short")) {
							typeStructComboBoxString.setSelectedIndex(5);
						}
					}

					if (structListModel.getSize() >= 2) {
						upButton.setEnabled(true);
						downButton.setEnabled(true);
					}
					removeButton.setEnabled(true);
				} 
			}

			if (typedefBool == true) {
				if (typedefList.getSelectedIndex() != -1) {
					String select = typedefListModel.get(typedefList.getSelectedIndex());
					String[] split = select.split(" : ");
					nameTypedefTextField.setText(split[0]);

					if (split[1].equals("sc_dt::sc_int")) {
						typeTypedefComboBoxString.setSelectedIndex(0);
					}

					if (typedefListModel.getSize() >= 2) {
						upButton.setEnabled(true);
						downButton.setEnabled(true);
					}
					removeButton.setEnabled(true);
				}
			}
		}
	}
}
