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

import ui.ColorManager;
import ui.util.IconManager;
import ui.avatarmad.AvatarMADAssumption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Class JDialogAssumption
 * Dialog for managing attributes of assummptions
 * Creation: 04/09/2013
 * @version 1.0 04/09/2013
 * @author Ludovic APVRILLE
 */
public class JDialogAssumption extends JDialogBase implements ActionListener  {



    private boolean regularClose;

    private String name;
    private int type, durability, source, status, limitation;
    private String text;

    private JButton selectStereotype;
    private JTextField stereotype;
    private JButton colorButton;
    private JButton useDefaultColor;
    
    // Panel1
    private JComboBox<String> typeBox;
    private JTextField nameField;
    protected JTextArea jta;
    
    //Panel2
    private JComboBox<String> durabilityBox, sourceBox, statusBox, limitationBox;


    /* Creates new form  */
    public JDialogAssumption(Frame _frame, String _title, String _name, String _text, int _type, int _durability, int _source, int _status, int _limitation) {
        super(_frame, _title, true);
		name = _name;
        text = _text;
        type = _type;
        durability = _durability;
        source = _source;
        status = _status;
        limitation = _limitation;
      
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel panel1 = new JPanel();
        panel1.setLayout(gridbag1);
       
        panel1.setBorder(new javax.swing.border.TitledBorder("Stereotype"));
        //panel1.setPreferredSize(new Dimension(250, 200));

        JPanel panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Other attributes"));
        //panel2.setPreferredSize(new Dimension(250, 200));
        
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        
        c1.gridwidth = 1;
        JLabel label = new JLabel("Stereotype:");
        panel1.add(label, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeBox = new JComboBox<>(AvatarMADAssumption.ASSUMPTION_TYPE_STR.toArray(new String[0]));
        typeBox.setSelectedIndex(type);
        panel1.add(typeBox, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        selectStereotype = new JButton("Select stereotype");
        panel1.add(selectStereotype, c1);
        selectStereotype.setEnabled(AvatarMADAssumption.ASSUMPTION_TYPE_STR.size() > 0);
        selectStereotype.addActionListener(this);

        // Text of stereotype
        stereotype = new JTextField(AvatarMADAssumption.ASSUMPTION_TYPE_STR.get(type), 30);
        panel1.add(stereotype, c1);
        colorButton = new JButton("Select stereotype color");
        colorButton.setBackground(AvatarMADAssumption.ASSUMPTION_TYPE_COLOR.get(type));
        colorButton.addActionListener(this);
        c1.gridwidth = GridBagConstraints.REMAINDER;
        c1.fill = GridBagConstraints.BOTH;
        panel1.add(colorButton, c1);

        useDefaultColor = new JButton("Use default color");
        useDefaultColor.setBackground(AvatarMADAssumption.ASSUMPTION_TYPE_COLOR.get(0));
        useDefaultColor.addActionListener(this);
        panel1.add(useDefaultColor, c1);

        JPanel panel1S = new JPanel();
        gridbag1 = new GridBagLayout();
        c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.BOTH;
        panel1S.setLayout(gridbag1);
        panel1S.setBorder(new javax.swing.border.TitledBorder("Name and description"));
        //panel1S.setPreferredSize(new Dimension(550, 450));
        
        c1.gridwidth = 1;
        label = new JLabel("Name:");
        panel1S.add(label, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nameField = new JTextField(name, 50);
        panel1S.add(nameField, c1);
        
        c1.gridheight = 7;
        
        //c1.fill = GridBagConstraints.BOTH;
        jta = new JTextArea();
        jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append(text);
        jta.setFont(new Font("times", Font.PLAIN, 12));
       
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //jsp.setPreferredSize(new Dimension(300, 250));
        //c1.fill = GridBagConstraints.HORIZONTAL;
        panel1S.add(jsp, c1);


        //}
        
        // Panel2
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        
        c2.gridwidth = 1;
        label = new JLabel("Durability:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        durabilityBox = new JComboBox<>(AvatarMADAssumption.DURABILITY_TYPE);
        durabilityBox.setSelectedIndex(durability);
        panel2.add(durabilityBox, c2);
        
        c2.gridwidth = 1;
        label = new JLabel("Source:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        sourceBox = new JComboBox<>(AvatarMADAssumption.SOURCE_TYPE);
        sourceBox.setSelectedIndex(source);
        panel2.add(sourceBox, c2);
        
        c2.gridwidth = 1;
        label = new JLabel("Status:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        statusBox = new JComboBox<>(AvatarMADAssumption.STATUS_TYPE);
        statusBox.setSelectedIndex(status);
        panel2.add(statusBox, c2);
        
        c2.gridwidth = 1;
        label = new JLabel("Scope:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        limitationBox = new JComboBox<>(AvatarMADAssumption.LIMITATION_TYPE);
        limitationBox.setSelectedIndex(limitation);
        panel2.add(limitationBox, c2);
		
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 20;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;

        //c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER;
        c.add(panel2, c0);
        //c0.fill = GridBagConstraints.BOTH;
        c0.gridheight = 20;
        //c0.gridwidth = GridBagConstraints.REMAINDER;
        c.add(panel1S, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);   
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (evt.getSource() == selectStereotype)  {
            selectStereotype();
        } else if (evt.getSource() == colorButton)  {
            selectColor();
        } else if (evt.getSource() == useDefaultColor)  {
            selectDefaultColor();
        }
    }

    public void selectColor() {
        Color newColor = JColorChooser.showDialog
                (null, "Background color of top box", colorButton.getBackground());
        colorButton.setBackground(newColor);
    }

    public void selectDefaultColor() {
        colorButton.setBackground(AvatarMADAssumption.ASSUMPTION_TYPE_COLOR.get(0));
    }

    public void selectStereotype() {
        int index = typeBox.getSelectedIndex();
        stereotype.setText(AvatarMADAssumption.ASSUMPTION_TYPE_STR.get(index));
        colorButton.setBackground( AvatarMADAssumption.ASSUMPTION_TYPE_COLOR.get(index));
    }
    
    
    
    public void closeDialog() {
        regularClose = true;
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public boolean isRegularClose() {
        return regularClose;
    }
	
	public String getName() {
        return nameField.getText();
    }
    
    public String getText() {
        return jta.getText();
    }
    
    public int getAssumptionType() {
        return typeBox.getSelectedIndex();
    }
    
    public int getDurability() {
        return durabilityBox.getSelectedIndex();
    }
    
    public int getSource() {
        return sourceBox.getSelectedIndex();
    }
    
    public int getStatus() {
        return statusBox.getSelectedIndex();
    }
    
    public int getLimitation() {
        return limitationBox.getSelectedIndex();
    }

    public String getStereotype() {
        return stereotype.getText();
    }

    public int getColor() {
        return colorButton.getBackground().getRGB();
    }
    
   
    
}
