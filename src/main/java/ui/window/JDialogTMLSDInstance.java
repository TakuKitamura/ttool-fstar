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
import ui.util.IconManager;

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
public abstract class JDialogTMLSDInstance extends JDialog implements ActionListener, ListSelectionListener  {

	protected static final int DEFAULT_MARGIN_SIZE = 5;

    protected java.util.List<TAttribute> attributes, attributesPar, forbidden;
    protected java.util.List<Boolean> initValues;
    protected boolean checkKeyword, checkJavaKeyword;

    protected boolean cancelled = false;

    protected JPanel pnlAddingAtt, pnlManagingAtt;

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
    //protected JComboBox<String> referenceUnitsName;

    // Main Panel
    protected JButton closeButton;
    protected JButton cancelButton;

    protected String name = "";

    /** Creates new form  */
    public JDialogTMLSDInstance( java.util.List<TAttribute> _attributes, java.util.List<TAttribute> _forbidden, Frame f, String title, String attrib, String _name )	{
        super(f, title, true);
        frame = f;
        attributesPar = _attributes;
        this.name = _name;	
        forbidden = _forbidden;
        initValues = new LinkedList<Boolean> ();
        this.attrib = attrib;

        attributes = new LinkedList<TAttribute> ();

        for (TAttribute attr: attributesPar) {
            attributes.add (attr.makeClone());
        }

        initComponents();
        myInitComponents();
        pack();
    }

    protected void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }

    // Issue #55
    protected JPanel createNamePanel() {
        final JPanel pnlName = new JPanel(new GridBagLayout());
        
        GridBagConstraints cstLblName = new GridBagConstraints();
        cstLblName.anchor = GridBagConstraints.NORTHEAST;
        cstLblName.gridx = 0;
        cstLblName.gridy = 0;
        cstLblName.weightx = 0.0;
        cstLblName.weighty = 0.0;
        cstLblName.fill = GridBagConstraints.HORIZONTAL;
        cstLblName.gridwidth = 1;
        cstLblName.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0 );
        pnlName.add( new JLabel( "Name:" ), cstLblName );
        
        nameOfInstance = new JTextField( this.name );//, 30 );
        GridBagConstraints cstTfdName = new GridBagConstraints();
        cstTfdName.anchor = GridBagConstraints.NORTHWEST;
        cstTfdName.gridx = 1;
        cstTfdName.gridy = 0;
        cstTfdName.weightx = 1.0;
        cstTfdName.weighty = 0.0;
        cstTfdName.fill = GridBagConstraints.BOTH;
        cstTfdName.gridwidth = GridBagConstraints.REMAINDER; //end row
        cstTfdName.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        pnlName.add( nameOfInstance, cstTfdName );
        
        return pnlName;
    }
    
    // Issue #55
    protected JPanel createAttributesPanel() {
        JPanel pnlAttributes = new JPanel(new GridBagLayout());

        pnlManagingAtt = new JPanel();
        pnlManagingAtt.setLayout( new GridBagLayout() );
        pnlManagingAtt.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib + "s"));

        final GridBagConstraints cstMngAtt = new GridBagConstraints();
        cstMngAtt.anchor = GridBagConstraints.NORTHWEST;
        cstMngAtt.gridx = 0;
        cstMngAtt.gridy = 0;
        cstMngAtt.weightx = 1.0;
        cstMngAtt.weighty = 1.0;
        cstMngAtt.fill = GridBagConstraints.BOTH;
        cstMngAtt.gridwidth = GridBagConstraints.REMAINDER; //end row;
        cstMngAtt.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        pnlAttributes.add(pnlManagingAtt, cstMngAtt );//BorderLayout.EAST);

        // 1st line panel2
        listAttribute = new JList<TAttribute>(attributes.toArray(new TAttribute[ attributes.size() ]));
        listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAttribute.addListSelectionListener(this);
        final GridBagConstraints cstLstAtt = new GridBagConstraints();
        cstLstAtt.anchor = GridBagConstraints.NORTHWEST;
        cstLstAtt.gridx = 0;
        cstLstAtt.gridy = 0;
        cstLstAtt.weightx = 1.0;
        cstLstAtt.weighty = 1.0;
        cstLstAtt.fill = GridBagConstraints.BOTH;
        cstLstAtt.gridwidth = GridBagConstraints.REMAINDER;
        cstLstAtt.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        JScrollPane scrollPane = new JScrollPane(listAttribute);
        pnlManagingAtt.add(scrollPane, cstLstAtt);

        upButton = new JButton("Up");
        upButton.addActionListener(this);
        final GridBagConstraints cstBtnUp = new GridBagConstraints();
        cstBtnUp.anchor = GridBagConstraints.NORTHWEST;
        cstBtnUp.gridx = 0;
        cstBtnUp.gridy = 1;
        cstBtnUp.weightx = 1.0;
        cstBtnUp.weighty = 0.0;
        cstBtnUp.fill = GridBagConstraints.HORIZONTAL;
        cstBtnUp.gridwidth = GridBagConstraints.REMAINDER;
        cstBtnUp.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        pnlManagingAtt.add(upButton, cstBtnUp );

        downButton = new JButton("Down");
        downButton.addActionListener(this);
        final GridBagConstraints cstBtnDown = new GridBagConstraints();
        cstBtnDown.anchor = GridBagConstraints.NORTHWEST;
        cstBtnDown.gridx = 0;
        cstBtnDown.gridy = 2;
        cstBtnDown.weightx = 1.0;
        cstBtnDown.weighty = 0.0;
        cstBtnDown.fill = GridBagConstraints.HORIZONTAL;
        cstBtnDown.gridwidth = GridBagConstraints.REMAINDER;
        cstBtnDown.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        pnlManagingAtt.add( downButton, cstBtnDown );

        removeButton = new JButton("Remove " + attrib);
        removeButton.addActionListener(this);
        final GridBagConstraints cstBtnRem = new GridBagConstraints();
        cstBtnRem.anchor = GridBagConstraints.NORTHWEST;
        cstBtnRem.gridx = 0;
        cstBtnRem.gridy = 3;
        cstBtnRem.weightx = 1.0;
        cstBtnRem.weighty = 0.0;
        cstBtnRem.fill = GridBagConstraints.HORIZONTAL;
        cstBtnRem.gridwidth = GridBagConstraints.REMAINDER;
        cstBtnRem.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        pnlManagingAtt.add(removeButton, cstBtnRem);


        pnlAddingAtt = new JPanel();
        pnlAddingAtt.setLayout( new GridBagLayout() );
        pnlAddingAtt.setBorder(new javax.swing.border.TitledBorder("Adding / Modifying " + attrib + "s"));

        GridBagConstraints cstPnlAddAtt = new GridBagConstraints();
        cstPnlAddAtt.anchor = GridBagConstraints.NORTHWEST;
        cstPnlAddAtt.gridx = 0;
        cstPnlAddAtt.gridy = 1;
        cstPnlAddAtt.weightx = 1.0;
        cstPnlAddAtt.weighty = 0.0;
        cstPnlAddAtt.fill = GridBagConstraints.BOTH;
        cstPnlAddAtt.gridwidth = 1;
        cstPnlAddAtt.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        pnlAttributes.add( pnlAddingAtt, cstPnlAddAtt );//BorderLayout.WEST);

        int xPos = 0;
        final GridBagConstraints cstLblAccess = new GridBagConstraints();
        cstLblAccess.anchor = GridBagConstraints.NORTHWEST;
        cstLblAccess.gridx = xPos++;
        cstLblAccess.gridy = 0;
        cstLblAccess.weightx = 0.0;
        cstLblAccess.weighty = 0.0;
        cstLblAccess.fill = GridBagConstraints.HORIZONTAL;
        cstLblAccess.gridwidth = 1;
        cstLblAccess.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, 0 );
        pnlAddingAtt.add( new JLabel("Access"), cstLblAccess );
       
        final GridBagConstraints cstLblId = new GridBagConstraints();
        cstLblId.anchor = GridBagConstraints.NORTHWEST;
        cstLblId.gridx = xPos++;
        cstLblId.gridy = 0;
        cstLblId.weightx = 0.0;
        cstLblId.weighty = 0.0;
        cstLblId.fill = GridBagConstraints.HORIZONTAL;
        cstLblId.gridwidth = 1;
        cstLblId.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, 0 );
        pnlAddingAtt.add(new JLabel("Identifier"), cstLblId );
        
        if (attrib.equals("Attribute") || attrib.equals("Variable")) {
            
            // For = label of row below
            xPos++;

            final GridBagConstraints cstLblInVal = new GridBagConstraints();
            cstLblInVal.anchor = GridBagConstraints.NORTHWEST;
            cstLblInVal.gridx = xPos++;
            cstLblInVal.gridy = 0;
            cstLblInVal.weightx = 0.0;
            cstLblInVal.weighty = 0.0;
            cstLblInVal.fill = GridBagConstraints.HORIZONTAL;
            cstLblInVal.gridwidth = 1;
            cstLblInVal.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, 0 );
            pnlAddingAtt.add(new JLabel("Initial Value"), cstLblInVal );
        }
        
        // For = label of row below
        xPos++;

        final GridBagConstraints cstLblType = new GridBagConstraints();
        cstLblType.anchor = GridBagConstraints.NORTHWEST;
        cstLblType.gridx = xPos++;
        cstLblType.gridy = 0;
        cstLblType.weightx = 0.0;
        cstLblType.weighty = 0.0;
        cstLblType.fill = GridBagConstraints.HORIZONTAL;
        cstLblType.gridwidth = GridBagConstraints.REMAINDER;
        cstLblType.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        pnlAddingAtt.add(new JLabel("Type"), cstLblType );

        xPos = 0;
        final GridBagConstraints cstCmbAccess = new GridBagConstraints();
        cstCmbAccess.anchor = GridBagConstraints.NORTHWEST;
        cstCmbAccess.gridx = xPos++;
        cstCmbAccess.gridy = 1;
        cstCmbAccess.weightx = 0.0;
        cstCmbAccess.weighty = 0.0;
        cstCmbAccess.fill = GridBagConstraints.HORIZONTAL;
        cstCmbAccess.gridwidth = 1;
        cstCmbAccess.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0 );
        accessBox = new JComboBox<String>();
        pnlAddingAtt.add(accessBox, cstCmbAccess );
        
        identifierText = new JTextField();
        identifierText.setEditable(true);
        final GridBagConstraints cstTfdId = new GridBagConstraints();
        cstTfdId.anchor = GridBagConstraints.NORTHWEST;
        cstTfdId.gridx = xPos++;
        cstTfdId.gridy = 1;
        cstTfdId.weightx = 1.0;
        cstTfdId.weighty = 0.0;
        cstTfdId.fill = GridBagConstraints.BOTH;
        cstTfdId.gridwidth = 1;
        cstTfdId.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0 );
        pnlAddingAtt.add(identifierText, cstTfdId );

        if (attrib.equals("Attribute") || attrib.equals("Variable")) {
            final GridBagConstraints cstLblEq = new GridBagConstraints();
            cstLblEq.anchor = GridBagConstraints.CENTER;
            cstLblEq.gridx = xPos++;
            cstLblEq.gridy = 1;
            cstLblEq.weightx = 0.0;
            cstLblEq.weighty = 0.0;
            cstLblEq.fill = GridBagConstraints.BOTH;
            cstLblEq.gridwidth = 1;
            cstLblEq.insets = new Insets( 0, 0, DEFAULT_MARGIN_SIZE, 0 );
        	pnlAddingAtt.add( new JLabel( " = " ), cstLblEq );
            initialValue = new JTextField();
            initialValue.setEditable(true);
            final GridBagConstraints cstTfdInVal = new GridBagConstraints();
            cstTfdInVal.anchor = GridBagConstraints.NORTHWEST;
            cstTfdInVal.gridx = xPos++;
            cstTfdInVal.gridy = 1;
            cstTfdInVal.weightx = 0.4;
            cstTfdInVal.weighty = 0.0;
            cstTfdInVal.fill = GridBagConstraints.BOTH;
            cstTfdInVal.gridwidth = 1;
            cstTfdInVal.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0 );
            pnlAddingAtt.add(initialValue, cstTfdInVal );
        }

        final GridBagConstraints cstLblColumn = new GridBagConstraints();
        cstLblColumn.anchor = GridBagConstraints.CENTER;
        cstLblColumn.gridx = xPos++;
        cstLblColumn.gridy = 1;
        cstLblColumn.weightx = 0.0;
        cstLblColumn.weighty = 0.0;
        cstLblColumn.fill = GridBagConstraints.BOTH;
        cstLblColumn.gridwidth = 1;
        cstLblColumn.insets = new Insets( 0, 0, DEFAULT_MARGIN_SIZE, 0 );
    	pnlAddingAtt.add( new JLabel( " : " ), cstLblColumn );
        typeBox = new JComboBox<String>();
        typeBox.addActionListener(this);
        final GridBagConstraints cstCmbType = new GridBagConstraints();
        cstCmbType.anchor = GridBagConstraints.NORTHWEST;
        cstCmbType.gridx = xPos++;
        cstCmbType.gridy = 1;
        cstCmbType.weightx = 0.0;
        cstCmbType.weighty = 0.0;
        cstCmbType.fill = GridBagConstraints.HORIZONTAL;
        cstCmbType.gridwidth = GridBagConstraints.REMAINDER; //end row
        cstCmbType.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        pnlAddingAtt.add(typeBox, cstCmbType );

        addButton = new JButton("Add / Modify " + attrib);
        addButton.addActionListener(this);
        final GridBagConstraints cstBtnAdd = new GridBagConstraints();
        cstBtnAdd.anchor = GridBagConstraints.NORTHWEST;
        cstBtnAdd.gridx = 0;
        cstBtnAdd.gridy = 2;
        cstBtnAdd.weightx = 1.0;
        cstBtnAdd.weighty = 0.0;
        cstBtnAdd.fill = GridBagConstraints.HORIZONTAL;
        cstBtnAdd.gridwidth = xPos;
        cstBtnAdd.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        pnlAddingAtt.add(addButton, cstBtnAdd );
        
        return pnlAttributes;
    }

    // Issue #55
    protected JPanel createButtonsPanel() {
        JPanel pnlButtons = new JPanel(new GridBagLayout());
        closeButton = new JButton("Save and Close", IconManager.imgic25 );
        closeButton.addActionListener(this);
        GridBagConstraints cstBtnClose = new GridBagConstraints();
        cstBtnClose.anchor = GridBagConstraints.NORTHWEST;
        cstBtnClose.gridx = 0;
        cstBtnClose.gridy = 0;
        cstBtnClose.weightx = 1.0;
        cstBtnClose.weighty = 0.0;
        cstBtnClose.fill = GridBagConstraints.HORIZONTAL;
        cstBtnClose.gridwidth = 1;
        cstBtnClose.insets = new Insets( 0, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        pnlButtons.add(closeButton, cstBtnClose);

        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        GridBagConstraints cstBtnCancel = new GridBagConstraints();
        cstBtnCancel.anchor = GridBagConstraints.NORTHWEST;
        cstBtnCancel.gridx = 1;
        cstBtnCancel.gridy = 0;
        cstBtnCancel.weightx = 1.0;
        cstBtnCancel.weighty = 0.0;
        cstBtnCancel.fill = GridBagConstraints.HORIZONTAL;
        cstBtnCancel.gridwidth = GridBagConstraints.REMAINDER;
        cstBtnCancel.insets = new Insets( 0, 0, 0, DEFAULT_MARGIN_SIZE );
        pnlButtons.add(cancelButton, cstBtnCancel);
        
        return pnlButtons;
    }

    protected void initComponents() {
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        Container contentPane = getContentPane();
		contentPane.setLayout( new GridBagLayout() );

	    // Issue #55
        GridBagConstraints cstPnlName = new GridBagConstraints();
        cstPnlName.anchor = GridBagConstraints.NORTHWEST;
        cstPnlName.gridx = 0;
        cstPnlName.gridy = 0;
        cstPnlName.weightx = 1.0;
        cstPnlName.weighty = 0.0;
        cstPnlName.fill = GridBagConstraints.HORIZONTAL;
        cstPnlName.gridwidth = GridBagConstraints.REMAINDER;
		cstPnlName.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        final JPanel pnlName = createNamePanel();
        contentPane.add( pnlName, cstPnlName );

        JPanel pnlAttr = createAttributesPanel();
        GridBagConstraints cstPnlAtt = new GridBagConstraints();
        cstPnlAtt.anchor = GridBagConstraints.NORTHWEST;
        cstPnlAtt.gridx = 0;
        cstPnlAtt.gridy = 1;
        cstPnlAtt.weightx = 1.0;
        cstPnlAtt.weighty = 1.0;
        cstPnlAtt.fill = GridBagConstraints.BOTH;
        cstPnlAtt.gridwidth = GridBagConstraints.REMAINDER; //end row
        cstPnlAtt.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, 0, DEFAULT_MARGIN_SIZE );
        contentPane.add( pnlAttr, cstPnlAtt );

        final JPanel pnlButtons = createButtonsPanel();
        GridBagConstraints cstPnlButtons = new GridBagConstraints();
        cstPnlButtons.anchor = GridBagConstraints.NORTHWEST;
        cstPnlButtons.gridx = 0;
        cstPnlButtons.gridy = 2;
        cstPnlButtons.weightx = 1.0;
        cstPnlButtons.weighty = 0.0;
        cstPnlButtons.fill = GridBagConstraints.BOTH;
        cstPnlButtons.gridwidth = GridBagConstraints.REMAINDER;
        cstPnlButtons.insets = new Insets( DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE );
        contentPane.add( pnlButtons, cstPnlButtons );
    }

    @Override
    public void	actionPerformed(ActionEvent evt)  {
        if (evt.getSource() == typeBox) {
            boolean b = initValues.get (typeBox.getSelectedIndex()).booleanValue();
            initialValue.setEnabled(b);
            return;
        }

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

   // public abstract void closeDialog();

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
            	// Issue #55: The string type is used to populate the combo boxes not the avatar type.
                select(typeBox, TAttribute.getStringType(a.getType()));
//                select(typeBox, TAttribute.getStringAvatarType(a.getType()));
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

    public void closeDialog() {
        cancelled = false;
        attributesPar.clear ();
        
        for(int i=0; i<attributes.size(); i++) {
            attributesPar.add (attributes.get (i));
        }
        
        this.name = nameOfInstance.getText();
        
        dispose();
    }
}	//End of class
