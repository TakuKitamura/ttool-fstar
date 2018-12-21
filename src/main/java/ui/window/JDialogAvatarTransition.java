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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import myutil.GraphicLib;
import ui.AvatarMethod;
import ui.Expression;
import ui.TAttribute;

/**
   * Class JDialogAvatarTransition
   * Dialog for managing transitions between states
   * Creation: 12/04/2010
   * @version 1.0 12/04/2010
   * @author Ludovic APVRILLE
 */
public class JDialogAvatarTransition extends JDialogBase implements ActionListener  {

    private Vector<Vector<Expression>> actionRows;
    //private Vector<String> actions;
    private String guard, afterMin, afterMax, /*computeMin, computeMax,*/ probability;
    private List<TAttribute> myAttributes;
    private List<AvatarMethod> myMethods;
    private Vector<String> allElements, insertElements;

//    protected String [] filesToInclude;
//    protected String [] codeToInclude;

    private boolean cancelled = true;

    private JPanel pnlTransitionInfo;
//    private JPanel panel1;
//    private JPanel panel2;

    // Panel1
    private JTextField guardT, afterMinT, afterMaxT, /*computeMinT, computeMaxT,*/ probabilityT;
    
    private JTable actionsTable;
//    private JTextArea actionsT;
	private final Vector<String> actionsListHeader;

	private JComboBox<String> codeElements;
    //private JButton insertElement;
    private JButton insertCodeButton;
    private JButton addButton;
	private JButton upButton;
	private JButton downButton;
	private JButton removeButton;

    // Panel of code and files
    protected JTextArea jtaCode, jtaFiles;


    /* Creates new form  */
    // arrayDelay: [0] -> minDelay ; [1] -> maxDelay
    public JDialogAvatarTransition(	Frame _f, 
    								String _title, 
    								String _guard,
    								String _afterMin,
    								String _afterMax,
								  /* String _computeMin, String _computeMax,*/ 
    								Vector<Expression> _actions,
    								List<TAttribute> _myAttributes, 
    								List<AvatarMethod> _myMethods,
//    								String[] _filesToInclude, 
//    								String[] _codeToInclude, 
    								String _probability) {

        super(_f, _title, true);

        guard = _guard;
        afterMin = _afterMin;
        afterMax = _afterMax;
//        computeMin = _computeMin;
//        computeMax = _computeMax;

        actionRows = new Vector<Vector<Expression>>();
        
        for ( final Expression actionExpr : _actions ) {
        	final Vector<Expression> row = new Vector<Expression>();
        	row.add( actionExpr );
            actionRows.add( row );
        }

        actionsListHeader = new Vector<String>();

        //actions = _actions;
        probability = _probability;

        myAttributes = _myAttributes;
        myMethods = _myMethods;

//        filesToInclude = _filesToInclude;
//        codeToInclude = _codeToInclude;

        makeElements();

        initComponents();
    //    myInitComponents();
        pack();
    }

    private void makeElements() {
       // int i;

        allElements = new Vector<String>();
        insertElements = new Vector<String>();

        for (TAttribute ta: myAttributes) {
            allElements.add(ta.toString());
            insertElements.add(ta.getId());
        }

        for (AvatarMethod am: myMethods) {
            allElements.add(am.toString());
            insertElements.add(am.getUseDescription());
        }
    }

//
//    private void myInitComponents() {
//    }
//
    private void initComponents() {
  //  	int i;

    	Container c = getContentPane();
//    	GridBagLayout gridbag0 = new GridBagLayout();
//    	GridBagLayout gridbag1 = new GridBagLayout();
//    	GridBagLayout gridbag2 = new GridBagLayout();
//    	GridBagConstraints c0 = new GridBagConstraints();
//    	GridBagConstraints c1 = new GridBagConstraints();
//    	GridBagConstraints c2 = new GridBagConstraints();

    	setFont(new Font("Helvetica", Font.PLAIN, 14));
    	//c.setLayout(gridbag0);
    	c.setLayout(new BorderLayout());

    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    	pnlTransitionInfo = new JPanel();
    	pnlTransitionInfo.setLayout( new GridBagLayout() );

    	pnlTransitionInfo.setBorder(new TitledBorder("Transition parameters"));
//    	panel1 = new JPanel();
//    	panel1.setLayout(gridbag1);

//    	panel1.setBorder(new javax.swing.border.TitledBorder("Transition parameters"));

    	//panel1.setPreferredSize(new Dimension(350, 350));
		final int defaultMargin = 3;

    	// guard
    	final GridBagConstraints constraintsLabels = new GridBagConstraints();
    	constraintsLabels.weighty = 0.0;
    	constraintsLabels.weightx = 0.0;
    	constraintsLabels.gridwidth = 1;
    	constraintsLabels.gridheight = 1;
    	constraintsLabels.fill = GridBagConstraints.BOTH;
    	constraintsLabels.insets = new Insets( defaultMargin, defaultMargin, 0, 0 );
    	pnlTransitionInfo.add( new JLabel( "guard = ", SwingConstants.RIGHT ), constraintsLabels );

//    	c1.weighty = 1.0;
//    	c1.weightx = 1.0;
//    	c1.gridwidth = 1;
//    	c1.gridheight = 1;
//    	c1.fill = GridBagConstraints.BOTH;
//    	c1.gridheight = 1;
//    	panel1.add(new JLabel("guard = "), c1);
//    	c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	guardT = new JTextField(guard);
    	final GridBagConstraints constraintsFields = new GridBagConstraints();
    	constraintsFields.weighty = 0.0;
    	constraintsFields.weightx = 1.0;
    	constraintsFields.gridwidth = GridBagConstraints.REMAINDER; //end row;
    	constraintsFields.gridheight = 1;
    	constraintsFields.fill = GridBagConstraints.BOTH;
    	constraintsFields.insets = new Insets( defaultMargin, 0, 0, defaultMargin );
    	
    	pnlTransitionInfo.add( guardT, constraintsFields );
    	//panel1.add(guardT, c1);

    	// After
    	pnlTransitionInfo.add(new JLabel("after ( ", SwingConstants.RIGHT ), constraintsLabels );
//    	c1.gridwidth = 1;
//    	c1.gridheight = 1;
//    	c1.weighty = 1.0;
//    	c1.weightx = 1.0;
//    	panel1.add(new JLabel("after ("), c1);
    	afterMinT = new JTextField(afterMin, 10);
    	constraintsFields.gridwidth = 1;
    	constraintsFields.insets.right = 0;
    	pnlTransitionInfo.add(afterMinT, constraintsFields );
    	constraintsLabels.insets.left = 0;
    	pnlTransitionInfo.add(new JLabel( ", " ), constraintsLabels );
//    	panel1.add(afterMinT, c1);
  // 	panel1.add(new JLabel(","), c1);
    	afterMaxT = new JTextField(afterMax, 10);
    	pnlTransitionInfo.add(afterMaxT, constraintsFields );
    	constraintsLabels.gridwidth = GridBagConstraints.REMAINDER;
    	constraintsLabels.insets.right = defaultMargin;
    	pnlTransitionInfo.add(new JLabel( " )" ), constraintsLabels );
    	constraintsLabels.gridwidth = 1;
//    	panel1.add(afterMaxT, c1);
//    	c1.gridwidth = GridBagConstraints.REMAINDER; //end row
//    	panel1.add(new JLabel(")"), c1);

    	// Compute
    	/*c1.gridwidth = 1;
    	c1.gridheight = 1;
    	c1.weighty = 1.0;
    	c1.weightx = 1.0;
    	panel1.add(new JLabel("compute for ("), c1);
    	computeMinT = new JTextField(computeMin, 10);
    	panel1.add(computeMinT, c1);
    	panel1.add(new JLabel(","), c1);
    	computeMaxT = new JTextField(computeMax, 10);
    	panel1.add(computeMaxT, c1);
    	c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	panel1.add(new JLabel(")"), c1);*/

		// probability
    	pnlTransitionInfo.add( new JLabel("weight in [0...1000] (default = 1) = "), constraintsLabels );
//    	c1.weighty = 1.0;
//		c1.weightx = 1.0;
//		c1.gridwidth = 1;
//		c1.gridheight = 1;
//		c1.fill = GridBagConstraints.BOTH;
//		c1.gridheight = 1;
		//panel1.add(new JLabel("weight in [0...1000] (default = 1) = "), c1);
	//	c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	constraintsFields.gridwidth = GridBagConstraints.REMAINDER; //end row;
    	probabilityT = new JTextField(probability);
    	pnlTransitionInfo.add( probabilityT, constraintsFields );
//		panel1.add(probabilityT, c1);

    	// actions
    	constraintsFields.gridwidth = GridBagConstraints.REMAINDER;
    	
    	codeElements = new JComboBox<String>(allElements);
    	pnlTransitionInfo.add(codeElements, constraintsFields );

		insertCodeButton = new JButton( "Insert Code" );
		insertCodeButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				insertCodeAction();
			}
		} );
    	pnlTransitionInfo.add( insertCodeButton, constraintsFields );

    	actionsTable = new JTable( new DefaultTableModel() {
    		
    		/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
    	    public Object getValueAt(int row, int column) {
    	        final Expression expression = (Expression) super.getValueAt( row, column );
    	        
    	        return expression.getText();
    	    }

    		@Override
    	    public void setValueAt(Object aValue, int row, int column) {
    	        final Expression expression = (Expression) super.getValueAt( row, column );
    	        
    	        expression.setText( String.valueOf( aValue ) );
    	        
    	        fireTableCellUpdated(row, column);
    		}
    	} );
    	
    	actionsTable.setShowGrid( false );
    	 
    	final ListSelectionModel selectionModel = actionsTable.getSelectionModel();
    	selectionModel.addListSelectionListener( new ListSelectionListener() {
			
			@Override
			public void valueChanged( ListSelectionEvent e ) {
				manageButtonsEnablement();
			}
		} );
    	
    	actionsTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    	actionsListHeader.addElement("");
    	
    	final DefaultCellEditor defaultCellEditor = (DefaultCellEditor) actionsTable.getDefaultEditor( Object.class );
    	defaultCellEditor.setClickCountToStart( 1 );
    	defaultCellEditor.addCellEditorListener( new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent e) {
				manageButtonsEnablement();
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
				manageButtonsEnablement();
			}
		});
    	
    	( (DefaultTableModel) actionsTable.getModel() ).setDataVector( actionRows, actionsListHeader );

    	//actionsT.getCellRenderer().sEditable(true);
    	//actionsT.setMargin(new Insets(10, 10, 10, 10)));
    	//actionsT.setTabSize(3);
    	//actionsT.setFont(new Font("times", Font.PLAIN, 12));
    	//actionsT.setPreferredSize(new Dimension(350, 250));
    	JScrollPane jsp = new JScrollPane(actionsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    	
//    	for( int i=0; i<actions.size(); i++) {
//    		actionsT.append(actions.get(i) + "\n");
//    	}
    	
    	final GridBagConstraints constraintsList = new GridBagConstraints();
    	constraintsList.weighty = 1.0;
    	constraintsList.weightx = 1.0;
    	constraintsList.gridwidth = GridBagConstraints.REMAINDER; //end row;
    	constraintsList.gridheight = 1;
    	constraintsList.fill = GridBagConstraints.BOTH;
    	
    	pnlTransitionInfo.add(jsp, constraintsList );

//    	c1.weighty = 0.0;

    	final GridBagConstraints constraintsButtons = new GridBagConstraints();
    	constraintsButtons.weighty = 0.0;
    	constraintsButtons.weightx = 1.0;
    	constraintsButtons.gridwidth = GridBagConstraints.REMAINDER; //end row;
    	constraintsButtons.gridheight = 1;
    	constraintsButtons.fill = GridBagConstraints.BOTH;

    	upButton = new JButton( "Up" );
		upButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				upAction();
			}
		});
		
		constraintsFields.gridwidth = GridBagConstraints.REMAINDER;
		constraintsFields.weightx = 1.0;
		pnlTransitionInfo.add(upButton, constraintsButtons );
		
		downButton = new JButton("Down");
		downButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				downAction();
			}
		} );
		
		pnlTransitionInfo.add( downButton, constraintsButtons );
		
		removeButton = new JButton("Remove");
		removeButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAction();
			}
		} );
		
		pnlTransitionInfo.add( removeButton, constraintsButtons );

		addButton = new JButton( "Add" );
		addButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addAction();
			}
		} );
    	pnlTransitionInfo.add( addButton, constraintsButtons );

    	manageButtonsEnablement();

//    	elements = new JComboBox<String>(allElements);
//    	panel1.add(elements, c1);
//
//    	insertElement = new JButton("Insert");
//    	insertElement.setEnabled(allElements.size() > 0);
//    	insertElement.addActionListener(this);
//    	panel1.add(insertElement, c1);
//
//    	c1.gridheight = 10;
//    	c1.weighty = 10.0;
//    	c1.weightx = 10.0;
//    	c1.gridwidth = GridBagConstraints.REMAINDER; //end row
//    	c1.fill = GridBagConstraints.BOTH;
//    	actionsT = new JTextArea();
//    	actionsT.setEditable(true);
//    	actionsT.setMargin(new Insets(10, 10, 10, 10));
//    	actionsT.setTabSize(3);
//    	actionsT.setFont(new Font("times", Font.PLAIN, 12));
//    	//actionsT.setPreferredSize(new Dimension(350, 250));
//    	JScrollPane jsp = new JScrollPane(actionsT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//    	for(i=0; i<actions.size(); i++) {
//    		actionsT.append(actions.get(i) + "\n");
//    	}
//    	panel1.add(jsp, c1);


//    	panel2 = new JPanel();
//    	panel2.setLayout(gridbag2);
//
//    	panel2.setBorder(new javax.swing.border.TitledBorder("Code"));
//    	// guard
//    	c2.weighty = 1.0;
//    	c2.weightx = 1.0;
//    	c2.gridwidth = 1;
//    	c2.gridheight = 1;
//    	c2.fill = GridBagConstraints.BOTH;
//    	c2.gridwidth = GridBagConstraints.REMAINDER;
//    	c2.gridheight = 1;
//    	panel2.add(new JLabel("Files to include:"), c2);
//    	jtaFiles = new JTextArea();
//    	jtaFiles.setEditable(true);
//    	jtaFiles.setMargin(new Insets(10, 10, 10, 10));
//    	jtaFiles.setTabSize(3);
//    	String files = "";
//    	if (filesToInclude != null) {
//    		for(i=0; i<filesToInclude.length; i++) {
//    			files += filesToInclude[i] + "\n";
//    		}
//    	}
//    	jtaFiles.append(files);
//    	jtaFiles.setFont(new Font("times", Font.PLAIN, 12));
//    	jsp = new JScrollPane(jtaFiles, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//    	//jsp.setPreferredSize(new Dimension(300, 300));
//    	panel2.add(jsp, c2);
//    	panel2.add(new JLabel("Code to execute at the end of the transition"), c2);
//    	jtaCode = new JTextArea();
//    	jtaCode.setEditable(true);
//    	jtaCode.setMargin(new Insets(10, 10, 10, 10));
//    	jtaCode.setTabSize(3);
//    	String code = "";
//    	if (codeToInclude != null) {
//    		for(i=0; i<codeToInclude.length; i++) {
//    			code += codeToInclude[i] + "\n";
//    		}
//    	}
//    	jtaCode.append(code);
//    	jtaCode.setFont(new Font("times", Font.PLAIN, 12));
//    	jsp = new JScrollPane(jtaCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//    	//jsp.setPreferredSize(new Dimension(300, 300));
//    	panel2.add(jsp, c2);


    	// button panel;
//    	c0.gridwidth = 1;
//    	c0.gridheight = 10;
//    	c0.weighty = 1.0;
//    	c0.weightx = 1.0;
//    	c0.gridwidth = GridBagConstraints.REMAINDER; //end row

    	// Issue #41 Ordering of tabbed panes 
    	JTabbedPane jtp = GraphicLib.createTabbedPane();//new JTabbedPane();
    	jtp.add( "General", pnlTransitionInfo );
    	//jtp.add("Prototyping", panel2);
    	//c.add(jtp, c0);
    	c.add(jtp, BorderLayout.CENTER);

    	JPanel buttons = new JPanel();
    	buttons.setLayout( new GridBagLayout() );

    	final GridBagConstraints c0 = new GridBagConstraints();
    	c0.gridwidth = 1;
    	c0.gridheight = 1;
    	c0.fill = GridBagConstraints.HORIZONTAL;
    	
    	initButtons(c0, buttons, this);

    	c.add(buttons, BorderLayout.SOUTH);
    }
	
	private void downAction() {
		final int selectedActionIndex = actionsTable.getSelectedRow();

		if ( selectedActionIndex > -1 ) {
	    	saveCurrentActionEditing();

	    	final int newIndex = selectedActionIndex + 1;
			Collections.swap( actionRows, selectedActionIndex, newIndex );
	    	( (DefaultTableModel) actionsTable.getModel() ).setDataVector( actionRows, actionsListHeader );
	    	actionsTable.getSelectionModel().setSelectionInterval( newIndex, newIndex );
			
			manageButtonsEnablement();
		}
	}
	
	private void upAction()	{
		final int selectedActionIndex = actionsTable.getSelectedRow();

		if ( selectedActionIndex > 0 )	{
	    	saveCurrentActionEditing();

	    	final int newIndex = selectedActionIndex - 1;
			Collections.swap( actionRows, selectedActionIndex, newIndex );
	    	( (DefaultTableModel) actionsTable.getModel() ).setDataVector( actionRows, actionsListHeader );
	    	actionsTable.setRowSelectionInterval( newIndex, newIndex );

			manageButtonsEnablement();
		}
	}
	
	private void manageButtonsEnablement() {
		final int selectedActionIndex = actionsTable.getSelectedRow();
		
		if ( selectedActionIndex < 0 ) {
			insertCodeButton.setEnabled( false );
			upButton.setEnabled( false );
			downButton.setEnabled( false );
			removeButton.setEnabled( false );
		}
		else {
			insertCodeButton.setEnabled( codeElements.getSelectedIndex() > -1 );
			removeButton.setEnabled( true );
			upButton.setEnabled( selectedActionIndex > 0 );
			downButton.setEnabled( selectedActionIndex < actionsTable.getRowCount() - 1 );
		}
	}

    @Override
    public void actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
//        } else if (evt.getSource() == insertElement)  {
//            insertElements();
        }
    }
    
    private JTextField getEditingField() {
    	// TODO: Generalize for other fields so that code completion also works for them
    	
        if ( actionsTable.getSelectedRow() > -1 ) {
	    	DefaultCellEditor cellEditor = (DefaultCellEditor) actionsTable.getCellEditor();
	    	
	    	if ( cellEditor == null ) {
	    		actionsTable.editCellAt( actionsTable.getSelectedRow(), 0 );
	    		cellEditor = (DefaultCellEditor) actionsTable.getCellEditor();
	    	}

	    	return (JTextField) cellEditor.getComponent();
        }
        
        return null;
    }

    private void insertCodeAction() {
        int selectedCodeIndex = codeElements.getSelectedIndex();
        
        if ( selectedCodeIndex > -1 ) {
        	final JTextField editingField = getEditingField();

        	if ( editingField != null ) {
        		final int caretPos = editingField.getCaretPosition();
                final String elementName = insertElements.get( codeElements.getSelectedIndex() );

                try {
					editingField.getDocument().insertString( caretPos, elementName, null );
				}
                catch (BadLocationException e) {
					e.printStackTrace();
				}
        	}
    	}
    }
    
    private void saveCurrentActionEditing() {
    	if ( actionsTable.isEditing() ) {
    		actionsTable.getCellEditor().stopCellEditing();
    	}
    }
    
    private void addAction() {
    	saveCurrentActionEditing();

    	final int addingIndex = actionsTable.getSelectedRow() >= 0 ? actionsTable.getSelectedRow() + 1 : actionRows.size();
        final Expression newAction = new Expression( "" );
        final Vector<Expression> newActionRow = new Vector<Expression>();
        newActionRow.add( newAction );
        actionRows.add( addingIndex, newActionRow );
        ( (DefaultTableModel) actionsTable.getModel() ).setDataVector( actionRows, actionsListHeader );
        actionsTable.setRowSelectionInterval( addingIndex, addingIndex );
        
        manageButtonsEnablement();
    }

    private void removeAction() {
    	int selectedIndex = actionsTable.getSelectedRow();
    	
    	if ( selectedIndex > -1 ) {
	    	actionRows.remove( selectedIndex );
	    	( (DefaultTableModel) actionsTable.getModel() ).setDataVector( actionRows, actionsListHeader );
	    	
	    	selectedIndex = Math.min( selectedIndex, actionRows.size() - 1 );
	    	
	    	if ( selectedIndex > -1 ) {
	    		actionsTable.setRowSelectionInterval( selectedIndex, selectedIndex );
	    	}

	    	manageButtonsEnablement();
    	}
    }
//
//    public void insertElements() {
//        int index = elements.getSelectedIndex();
//        int caretPos = actionsT.getCaretPosition ();
//        String str = insertElements.get(index);
//        String text = actionsT.getText ();
//        if (caretPos > 0 && text.charAt (caretPos-1) != ' ' && text.charAt (caretPos-1) != '(')
//            str = " " + str;
//        if (caretPos == text.length () || (text.charAt (caretPos) != ' ' && text.charAt (caretPos) != ')'))
//            str = str + " ";
//        actionsT.insert (str, caretPos);
//        actionsT.setCaretPosition (caretPos + str.length ());
//        actionsT.requestFocusInWindow ();
//    }

    public void closeDialog() {
//        actions.removeAllElements();
//        String[] act = actionsT.getText().split("\n");
//        for(int i=0; i<act.length; i++) {
//            if (act[0].length() > 0) {
//                actions.add(act[i]);
//            }
//        }
//        filesToInclude =  Conversion.wrapText(jtaFiles.getText());
//        codeToInclude =  Conversion.wrapText(jtaCode.getText());

        cancelled = false;
        
    	saveCurrentActionEditing();

        dispose();
    }

    /*public String getActions() {
      return signal.getText();
      }*/

    public String getGuard() {
        return guardT.getText();
    }

    public String getAfterMin() {
        return afterMinT.getText();
    }

    public String getAfterMax() {
        return afterMaxT.getText();
    }

//    public String getComputeMin() {
//        if (computeMinT == null) {
//        	return "";
//		}
//
//    	return computeMinT.getText();
//    }
//
//    public String getComputeMax() {
//		if (computeMaxT == null) {
//			return "";
//		}
//    	return computeMaxT.getText();
//    }
    
    public List<Expression> getActions() {
    	final List<Expression> actions = new ArrayList<Expression>();
    	
    	for ( final Vector<Expression> actionVect : actionRows ) {
    		actions.add( actionVect.get( 0 ) );
    	}
    	
    	return actions;
    }

	public String getProbability() {
		return probabilityT.getText();
	}

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        dispose();
    }
//
//    public String[] getFilesToInclude() {
//        return filesToInclude;
//    }
//
//    public String[] getCodeToInclude() {
//        return codeToInclude;
//    }
}
