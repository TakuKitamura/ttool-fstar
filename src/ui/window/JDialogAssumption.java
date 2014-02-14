/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogAssumption
 * Dialog for managing attributes of assummptions
 * Creation: 04/09/2013
 * @version 1.0 04/09/2013
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
//import java.util.*;

import ui.*;
import ui.avatarmad.*;


public class JDialogAssumption extends javax.swing.JDialog implements ActionListener  {
    
	
    private boolean regularClose;
    
    private JPanel panel1, panel2;
    private Frame frame;
    private String name;
    private int type, durability, source, status, limitation;
    private String text;
    
    // Panel1
    private JComboBox typeBox;
    private JTextField nameField;
    protected JTextArea jta;
    
    //Panel2
    private JComboBox durabilityBox, sourceBox, statusBox, limitationBox;
    
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogAssumption(Frame _frame, String _title, String _name, String _text, int _type, int _durability, int _source, int _status, int _limitation) {
        super(_frame, _title, true);
        frame = _frame;
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
		int i;
		
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
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
       
        panel1.setBorder(new javax.swing.border.TitledBorder("Main attributes"));
       
        panel1.setPreferredSize(new Dimension(300, 450));
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Other attributes:"));
        panel2.setPreferredSize(new Dimension(300, 450));
        
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        
        c1.gridwidth = 1;
        JLabel label = new JLabel("Type:");
        panel1.add(label, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeBox = new JComboBox(AvatarMADAssumption.ASSUMPTION_TYPE_STR);
        typeBox.setSelectedIndex(type);
        panel1.add(typeBox, c1);
        
        c1.gridwidth = 1;
        label = new JLabel("Name:");
        panel1.add(label, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nameField = new JTextField(name, 40);
        panel1.add(nameField, c1);
        
        c1.gridheight = 7;
        
        c1.fill = GridBagConstraints.BOTH;
        
        jta = new JTextArea();
        jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append(text);
        jta.setFont(new Font("times", Font.PLAIN, 12));
       
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //jsp.setPreferredSize(new Dimension(300, 250));
        panel1.add(jsp, c1);
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
        durabilityBox = new JComboBox(AvatarMADAssumption.DURABILITY_TYPE);
        durabilityBox.setSelectedIndex(durability);
        panel2.add(durabilityBox, c2);
        
        c2.gridwidth = 1;
        label = new JLabel("Source:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        sourceBox = new JComboBox(AvatarMADAssumption.SOURCE_TYPE);
        sourceBox.setSelectedIndex(source);
        panel2.add(sourceBox, c2);
        
        c2.gridwidth = 1;
        label = new JLabel("Status:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        statusBox = new JComboBox(AvatarMADAssumption.STATUS_TYPE);
        statusBox.setSelectedIndex(status);
        panel2.add(statusBox, c2);
        
        c2.gridwidth = 1;
        label = new JLabel("Scope:");
        panel2.add(label, c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        limitationBox = new JComboBox(AvatarMADAssumption.LIMITATION_TYPE);
        limitationBox.setSelectedIndex(limitation);
        panel2.add(limitationBox, c2);
		
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        
        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        //c0.fill = GridBagConstraints.BOTH;
        c.add(panel2, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
       /* if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
            initialValue.setEnabled(b);
            return;
        }*/
        
        
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
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
    
    public int getType() {
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
    
   
    
}
