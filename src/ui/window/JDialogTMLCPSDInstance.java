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
* Class JDialogTMLCPSDInstance
* Dialog for managing attributes, methods and signals of instances of Sequence Diagrams
* Creation: 25/07/2014
* @version 1.0 25/07/2014
* @author Ludovic APVRILLE, Andrea ENRIC
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


public class JDialogTMLCPSDInstance extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
	
	private Vector attributes, attributesPar, forbidden, initValues;
	private Vector memoriesPar;
	private boolean checkKeyword, checkJavaKeyword;
    
  private boolean cancelled = false;
    
    
  private JPanel panel1, panel2;
    
  private Frame frame;
	private int tab;
    
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
		
		//Panel 3
	  private JButton removeMappingButton;
		private JComboBox referenceMemoriesName;


	// Mapping of storage units
//	private boolean hasMemories = false;
	private JPanel panel3, panel4;
	private JTextField methodText;
	private JButton addMappingButton;
	private JList listMethod, listStorageUnits;
  private JButton upMethodButton;
  private JButton downMethodButton;
  private JButton removeMethodButton;
  private JCheckBox implementationProvided;
	private Vector<String> memories;
	
  // Main Panel
  private JButton closeButton;
  private JButton cancelButton;
    
  /** Creates new form  */
  public JDialogTMLCPSDInstance( Vector _attributes, Vector<TMLArchiMemoryNode> _memories, Vector _forbidden, Frame f, String title,
																		String attrib )	{
		super(f, title, true);
		frame = f;
		attributesPar = _attributes;
		memoriesPar = _memories;

		
		
		/*if( memoriesPar == null ) {
			hasMemories = false;
		}*/
		
    forbidden = _forbidden;
    initValues = new Vector();
    this.attrib = attrib;
        
	 	attributes = new Vector();
		//methods = new Vector();
        
    for(int i=0; i<attributesPar.size(); i++) {
			attributes.addElement(((TAttribute)(attributesPar.elementAt(i))).makeClone());
		}
		
		/*for(int i=0; i<methodsPar.size(); i++) {
			methods.addElement(((AvatarMethod)(methodsPar.elementAt(i))).makeClone());
		}*/
		
    initComponents();
    myInitComponents();
    pack();
	}
    
  private void myInitComponents() {
		removeButton.setEnabled(false);
    upButton.setEnabled(false);
    downButton.setEnabled(false);
		removeMappingButton.setEnabled(false);
 }
    
 private void initComponents() {
	JTabbedPane tabbedPane = new JTabbedPane();
	Container c = getContentPane();
		
		JPanel panelAttr = new JPanel(new BorderLayout());
		JPanel panelMethod = new JPanel(new BorderLayout());
    GridBagLayout gridbag0 = new GridBagLayout();
    GridBagLayout gridbag1 = new GridBagLayout();
    GridBagLayout gridbag2 = new GridBagLayout();
		GridBagLayout gridbag3 = new GridBagLayout();
		GridBagLayout gridbag4 = new GridBagLayout();
		GridBagLayout gridbag5 = new GridBagLayout();
		GridBagLayout gridbag6 = new GridBagLayout();
		GridBagLayout gridbag7 = new GridBagLayout();
    GridBagConstraints c0 = new GridBagConstraints();
    GridBagConstraints c1 = new GridBagConstraints();
    GridBagConstraints c2 = new GridBagConstraints();
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c4 = new GridBagConstraints();
		GridBagConstraints c5 = new GridBagConstraints();
		GridBagConstraints c6 = new GridBagConstraints();
		GridBagConstraints c7 = new GridBagConstraints();
        
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
        if (attrib.equals("Attribute") || attrib.equals("Variable")) {
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
        accessBox = new JComboBox();
        panel1.add(accessBox, c1);
        identifierText = new JTextField();
        identifierText.setColumns(15);
        identifierText.setEditable(true);
        panel1.add(identifierText, c1);
        
        initialValue = new JTextField();
        initialValue.setColumns(5);
        initialValue.setEditable(true);
        
        if (attrib.equals("Attribute") || attrib.equals("Variable")) {
            panel1.add(new JLabel(" = "), c1);
            panel1.add(initialValue, c1);
        }
        
        panel1.add(new JLabel(" : "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeBox = new JComboBox();
        typeBox.addActionListener(this);
        panel1.add(typeBox, c1);
        
        // third line panel1
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
        listAttribute = new JList(attributes);
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
		
		// Methods
		panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Available storage units"));
        panel3.setPreferredSize(new Dimension(300, 250));
        
        panel4 = new JPanel();
        panel4.setLayout(gridbag2);
        panel4.setBorder(new javax.swing.border.TitledBorder("Mapped storage unit"));
        panel4.setPreferredSize(new Dimension(300, 250));
        
        // first line panel3
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel(" "), c3);
        
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.anchor = GridBagConstraints.CENTER;
				c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel3.add(new JLabel("Storage unit:"), c3);
        
        // second line panel3
        c3.fill = GridBagConstraints.HORIZONTAL;
				
				memories = new Vector<String>();
				for( int j = 0; j < memoriesPar.size(); j++ )	{
					TMLArchiMemoryNode mem = (TMLArchiMemoryNode) memoriesPar.get(j);
					memories.add( mem.getName() );
				}
				referenceMemoriesName = new JComboBox( memories );
        panel3.add( referenceMemoriesName, c3);


        /*methodText = new JTextField();
        methodText.setColumns(50);
        methodText.setEditable(true);
        panel3.add(methodText, c3);*/
        
        // third line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add( new JLabel(" "), c3 );
        
        // fourth line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        /*implementationProvided = new JCheckBox("Implementation provided by user");
        implementationProvided.setSelected(false);
        panel3.add(implementationProvided, c3);*/
        
        
        // fifth line panel3
        c3.gridheight = 1;
        c3.fill = GridBagConstraints.HORIZONTAL;
        addMappingButton = new JButton("Map storage unit");
        addMappingButton.addActionListener(this);
        panel3.add( addMappingButton, c3 );
        
        // 1st line panel4
        listStorageUnits = new JList( memories );
        listStorageUnits.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        listStorageUnits.addListSelectionListener( this );
        scrollPane = new JScrollPane( listStorageUnits );
        scrollPane.setSize( 300, 250 );
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 5;
        c4.weighty = 10.0;
        c4.weightx = 10.0;
        panel4.add(scrollPane, c4);
        
        // 2nd line panel4
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 1;
        panel4.add(new JLabel(""), c4);
        
        // third line panel4
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.HORIZONTAL;
        removeMappingButton = new JButton( "Remove storage unit" );
        removeMappingButton.addActionListener( this );
        panel4.add( removeMappingButton, c4 );
        /*upMethodButton = new JButton("Up");
        upMethodButton.addActionListener(this);
        panel4.add(upMethodButton, c4);
        
        downMethodButton = new JButton("Down");
        downMethodButton.addActionListener(this);
        panel4.add(downMethodButton, c4);
        
        removeMethodButton = new JButton("Remove method");
        removeMethodButton.addActionListener(this);
        panel4.add(removeMethodButton, c4);*/
		
        
        // main panel;
		panelAttr.add(panel1, BorderLayout.WEST);
		panelAttr.add(panel2, BorderLayout.EAST);
		tabbedPane.addTab("Attributes", panelAttr);
		
		//if (hasMethods) {
			panelMethod.add(panel3, BorderLayout.WEST);
			panelMethod.add(panel4, BorderLayout.EAST);
			tabbedPane.addTab("Mapping", panelMethod);
		//}
		
		tabbedPane.setSelectedIndex(tab);
		
        //c.add(panel1, c0);
        //c.add(panel2, c0);
		
		c0.gridwidth = 1;
    c0.gridheight = 10;
    c0.weighty = 1.0;
    c0.weightx = 1.0;
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.add(tabbedPane, c0);
        
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

		TraceManager.addDev( "**************************" );
		TraceManager.addDev( referenceMemoriesName.getSelectedItem().toString() );
		TraceManager.addDev( "**************************" );
		removeMappingButton.setEnabled( true );
    String s = referenceMemoriesName.getSelectedItem().toString();//methodText.getText();
		memories.add(s);
		listStorageUnits.setListData( memories );
	}
	
    public void removeMappedUnit() {
			memories.removeElementAt( 0 );
			listStorageUnits.setListData( memories );
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
	
    public void closeDialog() {
    	cancelled = false;
      attributesPar.removeAllElements();
      for(int i=0; i<attributes.size(); i++) {
				attributesPar.addElement(attributes.elementAt(i));
			}
			memoriesPar.removeAllElements();
      for( int i=0; i < memories.size(); i++ ) {
     		memoriesPar.addElement( memories.elementAt(i) );
      }
      dispose();
    }
    
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
		
		/*i = listMethod.getSelectedIndex() ;
        if (i == -1) {
            removeMethodButton.setEnabled(false);
            upMethodButton.setEnabled(false);
            downMethodButton.setEnabled(false);
            methodText.setText("");
            //initialValue.setText("");
        } else {
            String am = memories.elementAt(i);
            methodText.setText( am );
            TraceManager.addDev("Implementation of " + am + " is: " +  am.isImplementationProvided());
            implementationProvided.setSelected(am.isImplementationProvided());
            removeMethodButton.setEnabled(true);
            if (i > 0) {
                upMethodButton.setEnabled(true);
            } else {
                upMethodButton.setEnabled(false);
            }
            if (i != memories.size() - 1) {
                downMethodButton.setEnabled(true);
            } else {
                downMethodButton.setEnabled(false);
            }
        }*/
		
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
    
}	//End of class
