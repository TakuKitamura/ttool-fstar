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
 * Class JDialogAttribute
 * Dialog for managing attributes
 * Creation: 18/12/2003
 * @version 1.0 18/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.procsd;

import ui.util.IconManager;
import ui.TAttribute;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;


public class JDialogAttributeProCSD extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    private LinkedList<TAttribute> attributes, attributesPar, forbidden;
    private LinkedList<Boolean> initValues;
    private boolean checkKeyword, checkJavaKeyword;
    
    private JPanel panel1, panel2;
    
    private Frame frame;
    
    private String attrib; // "Attributes", "Gates", etc.
    
    // Panel1
    private JComboBox<String> accessBox, typeBox;
    private JTextField identifierText;
    private JTextField initialValue;
    private JButton addButton;
        
    //Panel2
    private JList<TAttribute> listAttribute;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    
    public JDialogAttributeProCSD(LinkedList<TAttribute> _attributes, LinkedList<TAttribute> _forbidden, Frame f, String title, String attrib) {
        super(f, title, true);
        frame = f;
        attributesPar = _attributes;
        forbidden = _forbidden;
        initValues = new LinkedList<Boolean> ();
        this.attrib = attrib;
        
        attributes = new LinkedList<TAttribute> ();
                
        for (TAttribute ta: attributesPar)
            attributes.add (ta.makeClone());
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
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
        panel1.setBorder(new javax.swing.border.TitledBorder("Adding " + attrib + "s"));
        panel1.setPreferredSize(new Dimension(300, 250));
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib + "s"));
        panel2.setPreferredSize(new Dimension(300, 250));
        
        // first line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("access"), c1);
        panel1.add(new JLabel("identifier"), c1);
        if (attrib.equals("Attribute")) {
            panel1.add(new JLabel(" "), c1);
            panel1.add(new JLabel("initial value"), c1);
        }
        panel1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel("type"), c1);
        
        // second line panel1
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        accessBox = new JComboBox<>();
        panel1.add(accessBox, c1);
        identifierText = new JTextField();
        identifierText.setColumns(15);
        identifierText.setEditable(true);
        panel1.add(identifierText, c1);
        
        initialValue = new JTextField();
        initialValue.setColumns(5);
        initialValue.setEditable(true);
        
        if (attrib.equals("Attribute")) {
            panel1.add(new JLabel(" = "), c1);
            panel1.add(initialValue, c1);
        }
        
        panel1.add(new JLabel(" : "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeBox = new JComboBox<>();
        typeBox.addActionListener(this);
        panel1.add(typeBox, c1);
        
        // third line panel1 not needed, by Solange
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
                          
        // fourth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add / Modify " + attrib);
        addButton.addActionListener(this);
        panel1.add(addButton, c1);
        
        // 1st line panel2
        listAttribute = new JList<TAttribute> (attributes.toArray (new TAttribute[0]));
        //listAttribute.setFixedCellWidth(150);
        //listAttribute.setFixedCellHeight(20);
        listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAttribute.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listAttribute);
        scrollPane.setSize(300, 250);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        panel2.add(scrollPane, c2);
        
        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        panel2.add(new JLabel(""), c2);
        
        // third line panel2
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        panel2.add(upButton, c2);
        
        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel2.add(downButton, c2);
        
        removeButton = new JButton("Remove " + attrib);
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);
        
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
        if (evt.getSource() == typeBox) {
            boolean b = initValues.get(typeBox.getSelectedIndex());
            initialValue.setEnabled(b);
            return;
        }
        
        
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Add / Modify " + attrib)) {
            addAttribute();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Remove " + attrib)) {
            removeAttribute();
        } else if (command.equals("Down")) {
            downAttribute();
        } else if (command.equals("Up")) {
            upAttribute();
        }
    }
    
    public void addAccess(String s) {
        accessBox.addItem(s);
    }
    
    public void addType(String s) {
        initValues.add(new Boolean(true));
        typeBox.addItem(s);
    }
    
    public void addType(String s, boolean b) {
        initValues.add(new Boolean(b));
        typeBox.addItem(s);
    }
   
    public void enableInitialValue(boolean b) {
        initialValue.setEnabled(b);
    }
    
    public void enableRTLOTOSKeyword(boolean b) {
        checkKeyword = !b;
    }
    
    public void enableJavaKeyword(boolean b) {
        checkJavaKeyword = !b;
    }
    
    
    
    public void addAttribute() {
        Object o1 = accessBox.getSelectedItem();
        Object o2 = typeBox.getSelectedItem();
        String s = identifierText.getText();
        String value = initialValue.getText();
        TAttribute a;
        
        
        if (s.length()>0) {
            if ((TAttribute.isAValidId(s, checkKeyword, checkJavaKeyword)) && (TAttribute.notIn(s, forbidden))){
                int i = TAttribute.getAccess(o1.toString());
                int j = TAttribute.getType(o2.toString());
                if ((i != -1) && (j!= -1)) {
                    
                    if ((value.length() < 1) || (initialValue.isEnabled() == false)){
                        value = "";
                    } else {
                        if (!TAttribute.isAValidInitialValue(j, value)) {
                            JOptionPane.showMessageDialog(frame,
                            "The initial value is not valid",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                    if (j == TAttribute.OTHER) {
                        a = new TAttribute(i, s, value, o2.toString());
                        //System.out.println("New attribute: " + o2.toString());
                    } else {
                        a = new TAttribute(i, s, value, j);
                    }
                    //checks whether the same attribute already belongs to the list
                    int index = attributes.size();
                    if (attributes.contains(a)) {
                        index = attributes.indexOf(a);
                        a = attributes.get (index);
                        a.setAccess(i);
                        if (j == TAttribute.OTHER) {
                            a.setTypeOther(o2.toString());
                        }
                        a.setType(j);                      
                        a.setInitialValue(value);
                        //attributes.removeElementAt(index);
                    } else {
                        attributes.add(index, a);
                    }
                    listAttribute.setListData(attributes.toArray (new TAttribute[0]));
                    identifierText.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame,
                    "Bad access / type",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                "Bad identifier: identifier already in use, or invalid identifier",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(frame,
            "Bad identifier",
            "Error",
            JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }
    
    public void removeAttribute() {
        int i = listAttribute.getSelectedIndex() ;
        if (i!= -1) {
            TAttribute a = attributes.get (i);
            a.setAccess(-1);
            attributes.remove (i);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
        }
    }
    
    public void downAttribute() {
        int i = listAttribute.getSelectedIndex();
        if ((i!= -1) && (i != attributes.size() - 1)) {
            TAttribute o = attributes.get (i);
            attributes.remove (i);
            attributes.add (i+1, o);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
            listAttribute.setSelectedIndex(i+1);
        }
    }
    
    public void upAttribute() {
        int i = listAttribute.getSelectedIndex();
        if (i > 0) {
            TAttribute o = attributes.get (i);
            attributes.remove (i);
            attributes.add (i-1, o);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
            listAttribute.setSelectedIndex(i-1);
        }
    }
    
    
    public void closeDialog() {
        attributesPar.clear ();
        for (TAttribute ta: attributes)
            attributesPar.add (ta);

        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int i = listAttribute.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            identifierText.setText("");
        } else {
            TAttribute a = attributes.get (i);
            identifierText.setText(a.getId());
            initialValue.setText(a.getInitialValue());
            select(accessBox, a.getStringAccess(a.getAccess()));
            
            if (a.getType() == TAttribute.OTHER) {
                select(typeBox, a.getTypeOther());
            } else {
                select(typeBox, a.getStringType(a.getType()));
            }
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != attributes.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }
    
    public void select(JComboBox jcb, String text) {
        String s;
        for(int i=0; i<jcb.getItemCount(); i++) {
            s = (String)(jcb.getItemAt(i));
            //System.out.println("String found: *" + s + "* *" + text + "*");
            if (s.equals(text)) {
                jcb.setSelectedIndex(i);
                return;
            }
        }
    }
    
}
