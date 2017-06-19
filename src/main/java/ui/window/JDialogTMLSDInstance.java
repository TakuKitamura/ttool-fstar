/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

import ui.TAttribute;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;


/**
 * Class JDialogTMLSDInstance
 * Dialog for managing attributes and name of a SD instance
 * Creation: 25/07/2014
 * @version 1.0 25/07/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public abstract class JDialogTMLSDInstance extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {

    protected LinkedList<TAttribute> attributes, attributesPar, forbidden;
    protected LinkedList<Boolean> initValues;
    protected boolean checkKeyword, checkJavaKeyword;

    protected boolean cancelled = false;

    protected JPanel panel1, panel2;

    protected Frame frame;
    protected int tab;

    protected String attrib; // "Attributes", "Gates", etc.

    //Name panel
    protected JTextField nameOfInstance;

    // Panel1
    protected JComboBox<String> accessBox, typeBox;
    protected JTextField identifierText;
    protected JTextField initialValue;
    protected JButton addButton;

    //Panel2
    protected JList<TAttribute> listAttribute;
    protected JButton upButton;
    protected JButton downButton;
    protected JButton removeButton;

    //Panel 3
    protected JComboBox referenceUnitsName;

    // Main Panel
    protected JButton closeButton;
    protected JButton cancelButton;

    protected String name = "";

    /** Creates new form  */
    public JDialogTMLSDInstance( LinkedList<TAttribute> _attributes, LinkedList<TAttribute> _forbidden, Frame f, String title, String attrib, String _name )	{
        super(f, title, true);
        frame = f;
        attributesPar = _attributes;
        this.name = _name;	
        forbidden = _forbidden;
        initValues = new LinkedList<Boolean> ();
        this.attrib = attrib;

        attributes = new LinkedList<TAttribute> ();

        for (TAttribute attr: attributesPar)
            attributes.add (attr.makeClone());

        initComponents();
        myInitComponents();
        pack();
    }

    protected void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }

    protected abstract void initComponents();

    public void	actionPerformed(ActionEvent evt)  {
        if (evt.getSource() == typeBox) {
            boolean b = initValues.get (typeBox.getSelectedIndex()).booleanValue();
            initialValue.setEnabled(b);
            return;
        }


        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == addButton) {
            addAttribute();
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
        } else if (evt.getSource() == removeButton) {
            removeAttribute();
        } else if (evt.getSource() == downButton) {
            downAttribute();
        } else if (evt.getSource() == upButton) {
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

        if( s.length() > 0 ) {
            if( ( TAttribute.isAValidId( s, checkKeyword, checkJavaKeyword ) ) && ( TAttribute.notIn(s, forbidden ) ) )	{
                int i = TAttribute.getAccess(o1.toString());
                int j = TAttribute.getType(o2.toString());
                if( ( j == TAttribute.ARRAY_NAT ) && ( value.length() < 1 ) )	{
                    value = "2";
                }
                if ((i != -1) && (j!= -1)) {
                    if ((value.length() < 1) || (initialValue.isEnabled() == false))	{
                        value = "";
                    }
                    else	{
                        if( !TAttribute.isAValidInitialValue(j, value) ) {
                            //TraceManager.addDev( "Initial value issue i = " + i + " j = " + j );
                            JOptionPane.showMessageDialog( frame, "The initial value is not valid", "Error", JOptionPane.INFORMATION_MESSAGE );
                            return;
                        }
                    }
                    if( j == TAttribute.OTHER )	{
                        a = new TAttribute(i, s, value, o2.toString());
                        //System.out.println("New attribute: " + o2.toString());
                    }
                    else	{
                        a = new TAttribute(i, s, value, j);
                    }
                    //checks whether the same attribute already belongs to the list
                    int index = attributes.size();
                    if( attributes.contains(a) )	{
                        index = attributes.indexOf(a);
                        a = attributes.get (index);
                        a.setAccess(i);
                        if( j == TAttribute.OTHER ) {
                            a.setTypeOther(o2.toString());
                        }
                        a.setType(j);                        
                        a.setInitialValue(value);
                        //attributes.removeElementAt(index);
                    }
                    else	{
                        attributes.add(index, a);
                    }
                    listAttribute.setListData(attributes.toArray (new TAttribute[0]));
                    identifierText.setText("");
                }
                else	{
                    JOptionPane.showMessageDialog( frame, "Bad access / type", "Error", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            else	{
                JOptionPane.showMessageDialog( frame, "Bad identifier: identifier already in use, or invalid identifier",
                        "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        else	{
            JOptionPane.showMessageDialog( frame, "Bad identifier", "Error", JOptionPane.INFORMATION_MESSAGE );
            return;
        }
    }	//End of method

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
        //TraceManager.addDev("Selected index = " + i);
        if (i > 0) {
            //TraceManager.addDev("Modifying ...");
            TAttribute o = attributes.get (i);
            attributes.remove (i);
            attributes.add (i-1, o);
            listAttribute.setListData(attributes.toArray (new TAttribute[0]));
            listAttribute.setSelectedIndex(i-1);
        }
    }

    public abstract void closeDialog();

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        cancelled = true;
        dispose();
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listAttribute.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            identifierText.setText("");
            //initialValue.setText("");
        } else {
            TAttribute a = attributes.get(i);
            identifierText.setText(a.getId());
            initialValue.setText(a.getInitialValue());
            select(accessBox, TAttribute.getStringAccess(a.getAccess()));
            if (a.getType() == TAttribute.OTHER) {
                select(typeBox, a.getTypeOther());
            } else {
                select(typeBox, TAttribute.getStringAvatarType(a.getType()));
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

    }	//End of method

    public void select(JComboBox<String> jcb, String text) {
        for(int i=0; i<jcb.getItemCount(); i++) {
            String s = jcb.getItemAt(i);
            if (s.equals(text)) {
                jcb.setSelectedIndex(i);
                return;
            }
        }
    }

    public String getName()	{
        return this.name;
    }
}	//End of class
