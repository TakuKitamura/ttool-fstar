/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class JDialogReducedAttribute
 * Dialog for managing attributes that are partially editable
 * Creation: 11/05/2004
 * @version 1.0 11/05/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import ui.*;

public class JDialogReducedAttribute extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    private Vector attributesPar, setList, unsetList, tclassAttributes;
    
    private JPanel panel1, panel2;
    
    private Frame frame;
    
    private boolean isEditable = true;
    
    private String attrib, nameTClass, nameTObject; // "Attributes", "Gates", etc.
    
    // Panel1
    private JList tclassAttributeList;
    private JTextField identifierText;
    private JTextField initialValue;
    private JButton setButton;
    
    //Panel2
    private JList setAttributes;
    private JButton unsetButton;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogReducedAttribute(Vector _attributes, Vector _tclassAttributes, Frame f, String title, String  _attrib, String _nameTObject, String _nameTClass) {
        super(f, title, true);
        frame = f;
        attributesPar = _attributes;
        attrib = _attrib;
        nameTObject = _nameTObject;
        nameTClass = _nameTClass;
        
        tclassAttributes = _tclassAttributes;
        
        unsetList = new Vector();
        setList = new Vector();
        
        TAttribute ta;
        for(int i=0; i<attributesPar.size(); i++) {
            ta = (TAttribute)(attributesPar.elementAt(i));
            if (ta.isSet()) {
                setList.addElement(ta.makeClone());
            } else {
                unsetList.addElement(ta.makeClone());
            }
        }
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
        setButton.setEnabled(false);
        unsetButton.setEnabled(false);
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
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder(attrib + "s of " + nameTClass));
        panel1.setPreferredSize(new Dimension(300, 250));
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder(attrib + "s modified in " + nameTObject));
        panel2.setPreferredSize(new Dimension(300, 250));
        
        // first line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        tclassAttributeList = new JList(tclassAttributes);
        tclassAttributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tclassAttributeList.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(tclassAttributeList);
        scrollPane1.setSize(300, 250);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 5;
        c1.weighty = 10.0;
        c1.weightx = 10.0;
        panel1.add(scrollPane1, c1);
        
        panel1.add(new JLabel(" "), c1);
        
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        identifierText = new JTextField();
        identifierText.setColumns(15);
        identifierText.setEditable(false);
        panel1.add(identifierText, c1);
        
        initialValue = new JTextField();
        initialValue.setColumns(5);
        initialValue.setEditable(true);
        
        if (attrib.equals("Attribute")) {
            panel1.add(new JLabel(" = "), c1);
            panel1.add(initialValue, c1);
        }
        
        // third line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.CENTER;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        // fourth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        setButton = new JButton("Set value");
        setButton.addActionListener(this);
        panel1.add(setButton, c1);
        
        // 1st line panel2
        setAttributes = new JList(setList);
        setAttributes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAttributes.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(setAttributes);
        scrollPane2.setSize(300, 250);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        panel2.add(scrollPane2, c2);
        
        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel2.add(new JLabel(""), c2);
        
        // third line panel2
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        unsetButton = new JButton("Unset value");
        unsetButton.addActionListener(this);
        panel2.add(unsetButton, c2);
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        
        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
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
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Unset value")) {
            unsetValue();
        } else if (command.equals("Set value")) {
            setValue();
        }
    }
    
    public void closeDialog() {
        attributesPar.removeAllElements();
        
        int i, j;
        TAttribute ta1 = null, ta2 = null;
        boolean found = false;
        
        for(i=0; i<tclassAttributes.size(); i++) {
            ta1 = (TAttribute)(tclassAttributes.elementAt(i));
            
            for(j=0; j<setList.size(); j++) {
                ta2 = (TAttribute)(setList.elementAt(j));
                found = false;
                if (ta2.getId().compareTo(ta1.getId()) ==0) {
                    found = true;
                    break;
                }
            }
            
            if (found) {
                ta2.set(true);
                attributesPar.addElement(ta2);
            } else {
                attributesPar.addElement(ta1);
            }
            
        }
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public void unsetValue() {
        int index = setAttributes.getSelectedIndex();
        if ((index > -1) && (index < setList.size())) {
            setList.removeElementAt(index);
            setAttributes.setListData(setList);
        }
    }
    
    public void setValue() {
        String s = initialValue.getText();
        if ((s == null) ||(s.length() == 0)) {
            return;
        }
        
        int index =  tclassAttributeList.getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        TAttribute ta = (TAttribute)(tclassAttributes.elementAt(index));
        if (!TAttribute.isAValidInitialValue(ta.getType(), s)) {
            JOptionPane.showMessageDialog(frame,
            "The value is not valid",
            "Error",
            JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // ta already in the set of set attributes?
        int i;
        TAttribute ta1;
        //boolean found = false;
        
        for(i=0; i<setList.size(); i++) {
            ta1 = (TAttribute)(setList.elementAt(i));
            if (ta1.getId().compareTo(ta.getId()) == 0) {
                setList.removeElementAt(i);
                break;
            }
        }
        
        ta1 = ta.makeClone();
        ta1.setInitialValue(s);
        setList.addElement(ta1);
        setAttributes.setListData(setList);
        
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int index;
        if (e.getSource() == tclassAttributeList) {
            index = tclassAttributeList.getSelectedIndex();
            if (index == -1) {
                setButton.setEnabled(false);
                identifierText.setText("");
            } else {
                TAttribute a = (TAttribute)(tclassAttributes.elementAt(index));
                identifierText.setText(a.getId());
                initialValue.setText(a.getInitialValue());
                if (isEditable) {
                    setButton.setEnabled(true);
                }
            }
        } else {
            index = setAttributes.getSelectedIndex();
            if (index == -1) {
                unsetButton.setEnabled(false);
            } else {
                if (isEditable) {
                    unsetButton.setEnabled(true);
                }
            }
        }
    }
    
    public void setEditable(boolean b) {
        isEditable = b;
    }
    
}
