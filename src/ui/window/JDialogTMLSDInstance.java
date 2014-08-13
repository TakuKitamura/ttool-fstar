/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
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
*
* /**
* Class JDialogTMLSDInstance
* Dialog for managing attributes, mapping and name of a SD instance
* Creation: 25/07/2014
* @version 1.0 25/07/2014
* @author Ludovic APVRILLE, Andrea ENRICI
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import ui.*;
import ui.tmldd.*;
import myutil.*;


public abstract class JDialogTMLSDInstance extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
	
	protected Vector attributes, attributesPar, forbidden, initValues;
	protected Vector unitsPar;
	protected Vector<String> mappedUnits = new Vector<String>();
	protected Vector<String> availableUnits = new Vector<String>();
	protected boolean checkKeyword, checkJavaKeyword;
    
  protected boolean cancelled = false;
    
  protected JPanel panel1, panel2;
    
  protected Frame frame;
	protected int tab;
    
  protected String attrib; // "Attributes", "Gates", etc.
  
  //Name panel
  protected JTextField nameOfInstance;
    
  // Panel1
  protected JComboBox accessBox, typeBox;
  protected JTextField identifierText;
  protected JTextField initialValue;
  protected JButton addButton;
    
  //Panel2
  protected JList listAttribute;
  protected JButton upButton;
  protected JButton downButton;
  protected JButton removeButton;
		
	//Panel 3
	protected JButton removeMappingButton;
	protected JComboBox referenceMemoriesName;


	// Mapping of storage units
	protected JPanel panel3, panel4;
	protected JButton addMappingButton;
	protected JList listMappedUnits;
	
  // Main Panel
  protected JButton closeButton;
  protected JButton cancelButton;

	protected String name = "";
    
  /** Creates new form  */
  public JDialogTMLSDInstance( Vector _attributes, Vector<TMLArchiNode> _availableUnits, Vector _forbidden, Frame f, String title,
																		String attrib, String _name )	{
		super(f, title, true);
		frame = f;
		attributesPar = _attributes;
		unitsPar = _availableUnits;
		this.name = _name;	
    forbidden = _forbidden;
    initValues = new Vector();
    this.attrib = attrib;
        
	 	attributes = new Vector();
        
    for( int i = 0; i < attributesPar.size(); i++ ) {
			attributes.addElement( ( (TAttribute)( attributesPar.elementAt(i) ) ).makeClone() );
		}
		
    initComponents();
    myInitComponents();
    pack();
	}
    
  protected void myInitComponents() {
		removeButton.setEnabled(false);
    upButton.setEnabled(false);
    downButton.setEnabled(false);
		removeMappingButton.setEnabled(false);
 }
    
 protected abstract void initComponents();
    
    public void	actionPerformed(ActionEvent evt)  {
        if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
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
        } else if (evt.getSource() == addMappingButton) {
						addMappedUnit();
				} else if (evt.getSource() == removeMappingButton) {
						removeMappedUnit();
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
						int j = TAttribute.getAvatarType(o2.toString());
						if( ( j == TAttribute.ARRAY_NAT ) && ( value.length() < 1 ) )	{
							value = "2";
						}
						if ((i != -1) && (j!= -1)) {
							if ((value.length() < 1) || (initialValue.isEnabled() == false))	{
								value = "";
							}
							else	{
								if( !TAttribute.isAValidInitialValue(j, value) ) {
									JOptionPane.showMessageDialog( frame, "The initial value is not valid", "Error", JOptionPane.INFORMATION_MESSAGE );
									return;
								}
              }
							if( j == TAttribute.OTHER )	{
								a = new TAttribute(i, s, value, o2.toString());
								a.isAvatar = true;
								//System.out.println("New attribute: " + o2.toString());
							}
							else	{
								a = new TAttribute(i, s, value, j);
								a.isAvatar = true;
							}
							//checks whether the same attribute already belongs to the list
							int index = attributes.size();
							if( attributes.contains(a) )	{
								index = attributes.indexOf(a);
								a = (TAttribute)(attributes.elementAt(index));
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
							listAttribute.setListData(attributes);
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
	
	public void addMappedUnit() {

		//TraceManager.addDev( "**************************" );
		//TraceManager.addDev( referenceMemoriesName.getSelectedItem().toString() );
		//TraceManager.addDev( "**************************" );
		removeMappingButton.setEnabled( true );
    String s = referenceMemoriesName.getSelectedItem().toString();
		mappedUnits.add(s);
		listMappedUnits.setListData( mappedUnits );
	}
	
    public void removeMappedUnit() {
			mappedUnits.removeElementAt( 0 );
			listMappedUnits.setListData( mappedUnits );
			removeMappingButton.setEnabled( false );
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
        //TraceManager.addDev("Selected index = " + i);
        if (i > 0) {
            //TraceManager.addDev("Modifying ...");
            Object o = attributes.elementAt(i);
            attributes.removeElementAt(i);
            attributes.insertElementAt(o, i-1);
            listAttribute.setListData(attributes);
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
            TAttribute a = (TAttribute)(attributes.elementAt(i));
            identifierText.setText(a.getId());
            initialValue.setText(a.getInitialValue());
            select(accessBox, a.getStringAccess(a.getAccess()));
            if (a.getType() == TAttribute.OTHER) {
                select(typeBox, a.getTypeOther());
            } else {
                select(typeBox, a.getStringAvatarType(a.getType()));
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
    
	public String getName()	{
		return this.name;
	}

	public String getMappedUnit()	{
		if( mappedUnits.size() == 1 )	{
			return mappedUnits.get(0);
		}
		return "";
	}
}	//End of class
