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
* Class JDialogTMLCPControllerInstance
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


public class JDialogTMLCPControllerInstance extends JDialogTMLSDInstance implements ActionListener, ListSelectionListener  {
    
  /** Creates new form  */
  public JDialogTMLCPControllerInstance( Vector _attributes, Vector<TMLArchiNode> _availableUnits, Vector _forbidden, Frame f, String title,
																		String attrib, String _name, String _mappedUnit )	{
  	super(_attributes, _availableUnits, _forbidden, f, title, attrib, _name, _mappedUnit );
	}
    
 @Override protected void initComponents() {
	JTabbedPane tabbedPane = new JTabbedPane();
	Container c = getContentPane();
		
		JPanel namePanel = new JPanel();
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
        
        
        //Name panel
        namePanel.add( new JLabel( "Name of controller instance:" ) );
        nameOfInstance = new JTextField( this.name, 30 );
        namePanel.add( nameOfInstance );
        
        //Panel1
        
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
		
		// Mapping
		panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Available controller units"));
        panel3.setPreferredSize(new Dimension(300, 250));
        
        panel4 = new JPanel();
        panel4.setLayout(gridbag2);
        panel4.setBorder(new javax.swing.border.TitledBorder("Mapped controller unit"));
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
        panel3.add(new JLabel("Controller unit:"), c3);
        
        // second line panel3
        c3.fill = GridBagConstraints.HORIZONTAL;
				
				for( int j = 0; j < unitsPar.size(); j++ )	{
					//TMLArchiMemoryNode mem = (TMLArchiMemoryNode) memoriesPar.get(j);
					availableUnits.add( ( (TMLArchiNode) unitsPar.get(j)).getName() );
				}
				referenceUnitsName = new JComboBox( availableUnits );
        panel3.add( referenceUnitsName, c3);

        // third line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add( new JLabel(" "), c3 );
        
        // fourth line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        
        // fifth line panel3
        c3.gridheight = 1;
        c3.fill = GridBagConstraints.HORIZONTAL;
        addMappingButton = new JButton("Map controller unit");
        addMappingButton.addActionListener(this);
        panel3.add( addMappingButton, c3 );
        
        // 1st line panel4

        listMappedUnits = new JList( mappedUnits );
        listMappedUnits.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        listMappedUnits.addListSelectionListener( this );
        scrollPane = new JScrollPane( listMappedUnits );
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
        removeMappingButton = new JButton( "Remove controller unit" );
        removeMappingButton.addActionListener( this );
        panel4.add( removeMappingButton, c4 );
        
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
    
    	c.add(namePanel, c0);
    	
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
    
   @Override public void closeDialog() {
    	cancelled = false;
      attributesPar.removeAllElements();
      for(int i=0; i<attributes.size(); i++) {
				attributesPar.addElement(attributes.elementAt(i));
			}
			this.name = nameOfInstance.getText();
			if( mappedUnits.size() > 1 )	{
				JOptionPane.showMessageDialog( frame, "Only one CPU unit can be mapped per each Controller instance",
																				"Error", JOptionPane.INFORMATION_MESSAGE );
				return;
			}
      dispose();
    }
    
}	//End of class
