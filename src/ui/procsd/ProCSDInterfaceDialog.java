/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 * @author Emil Salageanu
 * @see
 */

// we don't use this class for the moment

package ui.procsd;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import ui.*;
import ui.window.*;

public class ProCSDInterfaceDialog extends  JDialogAttributeProCSD implements ActionListener, ListSelectionListener  {
    private Vector attributes, attributesPar, forbidden, initValues;
    private boolean checkKeyword, checkJavaKeyword;
    
    private JPanel panel1, panel2;
    
    private Frame frame;
    
    private String attrib; // "Attributes", "Gates", etc.
    
    // Panel1
    private JComboBox accessBox, typeBox;
    private JTextField identifierText;
    private JTextField initialValue;
    private JButton addButton;
    
    //Panel2
    private JList listAttribute;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public ProCSDInterfaceDialog(Vector _attributes, Vector _forbidden, Frame f, String title, String attrib) {
        super(_attributes,  _forbidden,  f,  title,  attrib);
      }
    
    
    public void addAttribute() {
        Object o1 = accessBox.getSelectedItem();
        Object o2 = typeBox.getSelectedItem();
        
        String s = identifierText.getText();
        String value = initialValue.getText();
        
        TAttribute a;
        
        if (s.length()>0) {
            if ((TAttribute.isAValidId(s, checkKeyword, checkJavaKeyword)) && (TAttribute.notIn(s, forbidden))){
             int i;
             int j;
                       
            	if (o1==null) i = 1;
            	else  i = TAttribute.getAccess(o1.toString());
            
            	
            	if (o2==null) j=1;
            	else j = TAttribute.getType(o2.toString());
            	
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
                          if (o2!=null)
                    	         a = new TAttribute(i, s, value, o2.toString());
                          else    a = new TAttribute(i, s, value, "null");
                       
                    } else {
                        a = new TAttribute(i, s, value, j);
                    }
                    //checks whether the same attribute already belongs to the list
                    int index = attributes.size();
                    if (attributes.contains(a)) {
                        index = attributes.indexOf(a);
                        a = (TAttribute)(attributes.elementAt(index));
                        a.setAccess(i);
                        if (j == TAttribute.OTHER) {
                          if (o2!=null)
                        	a.setTypeOther(o2.toString());
                          else a.setTypeOther("null");
                        }
                        a.setType(j);                        
                        a.setInitialValue(value);
                        
                        //attributes.removeElementAt(index);
                    } else {
                        attributes.add(index, a);
                    }
                    listAttribute.setListData(attributes);
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
            TAttribute a = (TAttribute)(attributes.elementAt(i));
            a.setAccess(-1);
            attributes.removeElementAt(i);
            listAttribute.setListData(attributes);
        }
    }
    
    public void downAttribute() {
        int i = listAttribute.getSelectedIndex();
        if ((i!= -1) && (i != attributes.size() - 1)) {
            Object o = attributes.elementAt(i);
            attributes.removeElementAt(i);
            attributes.insertElementAt(o, i+1);
            listAttribute.setListData(attributes);
            listAttribute.setSelectedIndex(i+1);
        }
    }
    
    public void upAttribute() {
        int i = listAttribute.getSelectedIndex();
        if (i > 0) {
            Object o = attributes.elementAt(i);
            attributes.removeElementAt(i);
            attributes.insertElementAt(o, i-1);
            listAttribute.setListData(attributes);
            listAttribute.setSelectedIndex(i-1);
        }
    }
    
    
    public void closeDialog() {
        attributesPar.removeAllElements();
        for(int i=0; i<attributes.size(); i++) {
            attributesPar.addElement(attributes.elementAt(i));
        }
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
            //initialValue.setText("");
        } else {
            TAttribute a = (TAttribute)(attributes.elementAt(i));
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
