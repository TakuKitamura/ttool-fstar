/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea ENRICI

ludovic.apvrille AT telecom-paristech.fr
andrea.enrici AT telecom-paristech.fr

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
* Class JDialogReferenceCP
* Dialog for mapping CPs onto the architecture
* Creation: 22/08/2014
* @version 1.0 22/08/2014
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
import ui.tmlsd.*;
import ui.tmlcp.*;
import ui.avatarbd.*;
import myutil.*;


public class JDialogReferenceCP extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
	
	private final static int STORAGE = 0;
	private final static int TRANSFER = 1;
	private final static int CONTROLLER = 2;
	private final static String EMPTY_MAPPABLE_ARCH_UNITS_LIST = "No mappable units";
	private final static String EMPTY_CPS_LIST = "No CPs to reference";
	private final static String EMPTY_INSTANCES_LIST = "No instances to map";
	
	private boolean regularClose;
	
	private Frame frame;
	private TMLArchiCPNode cp;
	protected JTextField nameOfCP;
	private String name = "";
	private LinkedList<TMLArchiNode> availableUnits;
	private Vector<String> mappedUnitsSL = new Vector<String>();
	
	private ArrayList<TMLCommunicationPatternPanel> listCPs = new ArrayList<TMLCommunicationPatternPanel>();
	private Vector<String> communicationPatternsSL = new Vector<String>();
	
	//Each entry of the array list is a hash set of strings corresponding to a given CP in listCPs
	private ArrayList<HashSet<String>> listInstancesHash = new ArrayList<HashSet<String>>();
	private HashSet<String> sdStorageInstances = new HashSet<String>();
	private HashSet<String> sdTransferInstances = new HashSet<String>();
	private HashSet<String> sdControllerInstances = new HashSet<String>();
	
	private Vector<String> mappableArchUnitsSL = new Vector<String>();
	private Vector<String> sdInstancesSL = new Vector<String>();
	
	private int indexListCPsNames = 0;
	//private int indexListInstancesNames = 0;
	//private int indexListArchUnitsNames = 0;
	
	private boolean emptyCPsList = false;
	//private boolean emptyInstancesList = false;
	//private boolean emptyArchUnitsList = false;
	
	private boolean cancelled = false;
	
	// Panel1
	private JPanel panel1;
	private JComboBox sdInstancesCB, mappableArchUnitsCB, communicationPatternsCB;
	private JButton mapButton;
	
	//Panel2
	private JPanel panel2;
	private JList listMappedUnitsJL;
	private JButton upButton;
	private JButton downButton;
	private JButton removeButton;
	
	// Main Panel
	private JButton closeButton;
	private JButton cancelButton;
	
	/** Creates new form  */
	public JDialogReferenceCP( JFrame _frame,  String _title, TMLArchiCPNode _cp, /*LinkedList<TMLArchiNode> _availableUnits,*/
		Vector<String> _mappedUnits, String _name ) {
	
	super( _frame, _title, true );
	frame = _frame;
	cp = _cp;
	name = _name;
	
	if( _mappedUnits.size() > 0 )	{
		//the validity of _mappedUnits is checked when initializing components
		mappedUnitsSL.clear();	//take into account the elements already mapped
		mappedUnitsSL.addAll( 0, _mappedUnits );
	}
	else	{
		mappedUnitsSL = new Vector<String>();
	}
	
	initComponents();
	myInitComponents();
	pack();
		}
		
		private void myInitComponents() {
			removeButton.setEnabled( false );
			upButton.setEnabled( false );
			downButton.setEnabled( false );
			if( mappableArchUnitsSL.size() > 0 )	{
				mapButton.setEnabled( true );
			}
			else	{
				mapButton.setEnabled( false );
			}
			//makeComboBoxes();
		}
		
		private void initComponents() {
			
			Container c = getContentPane();
			GridBagLayout gridbag0 = new GridBagLayout();
			GridBagLayout gridbag1 = new GridBagLayout();
			GridBagLayout gridbag2 = new GridBagLayout();
			GridBagLayout gridbag3 = new GridBagLayout();
			GridBagLayout gridbag4 = new GridBagLayout();
			GridBagConstraints c0 = new GridBagConstraints();
			GridBagConstraints c1 = new GridBagConstraints();
			GridBagConstraints c2 = new GridBagConstraints();
			GridBagConstraints c3 = new GridBagConstraints();
			GridBagConstraints c4 = new GridBagConstraints();
			
			setFont(new Font("Helvetica", Font.PLAIN, 14));
			c.setLayout(gridbag0);
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			panel1 = new JPanel();
			panel1.setLayout(gridbag1);
			panel1.setBorder(new javax.swing.border.TitledBorder("CP attributes"));
			panel1.setMinimumSize(new Dimension(325, 250));
			
			panel2 = new JPanel();
			panel2.setLayout(gridbag2);
			panel2.setBorder(new javax.swing.border.TitledBorder("Managing mapping"));
			panel2.setMinimumSize(new Dimension(325, 250));
			
			// first line panel1
			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);
			
			// second line panel1
			c1.gridwidth = 1;
			c1.gridheight = 1;
			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.anchor = GridBagConstraints.CENTER;
			c1.fill = GridBagConstraints.HORIZONTAL;
			c1.anchor = GridBagConstraints.CENTER;
			
			// third line panel1
			panel1.add(new JLabel("Name:"), c1);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			nameOfCP = new JTextField( name );
			nameOfCP.setMinimumSize( new Dimension(150, 50) );
			panel1.add( nameOfCP, c1 );
			
			//fouth line panel1
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);	//adds some vertical space in between two JLabels
			
			listCPs = cp.getTDiagramPanel().getMGUI().getAllTMLCP();
			if( listCPs.size() == 0 ) {
				communicationPatternsSL.add( EMPTY_CPS_LIST );
				emptyCPsList = true;
			}
			else {
				createListCPsNames();	//create communicationPatternsSL (ArrayList<String>) out of listCPs
				indexListCPsNames = indexOf( cp.getReference() );
			}
			
			//fifth line panel1
			panel1.add( new JLabel( "Available CPs:"), c1 );
			communicationPatternsCB = new JComboBox( communicationPatternsSL );
			communicationPatternsCB.setSelectedIndex( indexListCPsNames );
			communicationPatternsCB.addActionListener( this );
			communicationPatternsCB.setMinimumSize( new Dimension(150, 50) );
			panel1.add( communicationPatternsCB, c1 );
			
			//sixth line panel1
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);
			
			//indexListInstancesNames = 0;
			if( listCPs.size() == 0 ) {
				sdInstancesSL.add( EMPTY_INSTANCES_LIST );
				//emptyInstancesList = true;
			}
			else {
				createListOfInstances();
				if( listInstancesHash.get( indexListCPsNames ).size() > 0 )	{	//protect against the case of a CP with no SDs
					sdInstancesSL = new Vector<String>( listInstancesHash.get( indexListCPsNames ) );
				}
				else	{
					sdInstancesSL.add( EMPTY_INSTANCES_LIST );
				}
				//indexListInstancesNames = indexOf( cp.getReference() );
			}
			
			//seventh line panel1
			panel1.add( new JLabel( "Instance:" ), c1 );
			sdInstancesCB = new JComboBox( sdInstancesSL );
			TraceManager.addDev( "Size of sdInstancesSL: " + sdInstancesSL.size() );
			sdInstancesCB.setSelectedIndex( 0 );
			sdInstancesCB.addActionListener( this );
			sdInstancesCB.setMinimumSize( new Dimension(150, 50) );
			panel1.add( sdInstancesCB, c1 );
			
			//eigth line panel1
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);
			
			String selectedInstance = sdInstancesSL.get(0);//new Vector<String>( listInstancesHash.get( indexListCPsNames ) ).get(0);
			if( sdStorageInstances.contains( selectedInstance ) )	{
				mappableArchUnitsSL = makeListOfMappableArchUnits( STORAGE );
			}
			else	{
				if( sdTransferInstances.contains( selectedInstance ) )	{
					mappableArchUnitsSL = makeListOfMappableArchUnits( TRANSFER );
				}
				else	{
					if( sdControllerInstances.contains( selectedInstance ) )	{
						mappableArchUnitsSL = makeListOfMappableArchUnits( CONTROLLER );
					}
				}
			}
			
			if( mappableArchUnitsSL.size() == 0 ) {
				mappableArchUnitsSL.add( EMPTY_MAPPABLE_ARCH_UNITS_LIST );
			}
			
			//nineth line panel1
			mappableArchUnitsCB = new JComboBox( mappableArchUnitsSL );
			panel1.add( new JLabel("Available architecture units:"), c1 );
			mappableArchUnitsCB.setSelectedIndex( 0 );
			mappableArchUnitsCB.setMinimumSize( new Dimension(150, 50) );
			mappableArchUnitsCB.addActionListener( this );
			panel1.add( mappableArchUnitsCB, c1 );
			
			//tenth line panel1
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);
			
			//eleventh line panel1
			c1.gridheight = 1;
			c1.fill = GridBagConstraints.HORIZONTAL;
			mapButton = new JButton("Map");
			mapButton.addActionListener(this);
			panel1.add(mapButton, c1);
			
			// 1st line panel2
			
			//CHECK VALIDITY OF mappedUnitsSL! This code is not doing what it should, up to know
			ArrayList<Integer> indexesToRemove = new ArrayList<Integer>();
			for( int i = 0; i < mappedUnitsSL.size(); i++ )	{
				String s = mappedUnitsSL.get(i);
				String[] firstPart = s.split( " : " );
				String[] secondPart = firstPart[0].split("\\.");
				String CPName = secondPart[0];
				String instanceName = secondPart[1];
				String archUnitName = firstPart[1];
				if( doesCPexist( CPName ) || doesInstanceExist( instanceName ) || doesArchUnitExist( archUnitName ) )	{
					indexesToRemove.add(i);
				}
			}	
			for( Integer j: indexesToRemove )	{
				mappedUnitsSL.removeElementAt(j);
			}

			listMappedUnitsJL = new JList( mappedUnitsSL );
			listMappedUnitsJL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listMappedUnitsJL.addListSelectionListener(this);
			JScrollPane scrollPane = new JScrollPane( listMappedUnitsJL );
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
			
			removeButton = new JButton("Remove unit");
			removeButton.addActionListener(this);
			panel2.add(removeButton, c2);
			
			// main panel;
			c0.gridwidth = 1;
			c0.gridheight = 10;
			c0.weighty = 1.0;
			c0.weightx = 1.0;
			c0.fill = GridBagConstraints.BOTH;
			
			c.add(panel1, c0);
			c0.gridwidth = GridBagConstraints.REMAINDER; //end row
			c.add(panel2, c0);
			
			c0.gridwidth = 1;
			c0.gridheight = 1;
			c0.fill = GridBagConstraints.VERTICAL;
			closeButton = new JButton("Save and Close", IconManager.imgic25);
			//closeButton.setPreferredSize(new Dimension(600, 50));
			closeButton.addActionListener(this);
			c.add(closeButton, c0);
			c0.gridwidth = GridBagConstraints.REMAINDER; //end row
			cancelButton = new JButton("Cancel", IconManager.imgic27);
			cancelButton.addActionListener(this);
			c.add(cancelButton, c0);
			
			/*JPanel panelButton = new JPanel();
			closeButton = new JButton("Save and Close", IconManager.imgic25);
			//closeButton.setPreferredSize(new Dimension(600, 50));
			closeButton.addActionListener(this);
			panelButton.add(closeButton);
			cancelButton = new JButton("Cancel", IconManager.imgic27);
			cancelButton.addActionListener(this);
			panelButton.add(cancelButton);
			
			JPanel middlePanel = new JPanel(new BorderLayout());
			middlePanel.add(panel3, BorderLayout.NORTH);
			middlePanel.add(panel4, BorderLayout.CENTER);
			middlePanel.add(panelButton, BorderLayout.SOUTH);
			
			JPanel topPanel = new JPanel();
			topPanel.add(panel1);
			topPanel.add(panel2);
			c.setLayout(new BorderLayout());
			c.add(topPanel, BorderLayout.CENTER);
			c.add(middlePanel, BorderLayout.SOUTH);*/
			
		}
		
		private boolean doesCPexist( String CPName )	{
			return false;
		}
		
		private boolean doesInstanceExist( String instanceName )	{
			return false;
		}
		
		private boolean doesArchUnitExist( String archUnitName )	{
			return false;
		}
		
		public void	actionPerformed(ActionEvent evt)  {
			//String command = evt.getActionCommand();
			
			// Compare the action command to the known actions.
			if (evt.getSource() == closeButton)  {
				closeDialog();
			}
			else if (evt.getSource() == mapButton) {
				sdInstancesCB.removeActionListener( this );
				mapInstance();
				sdInstancesCB.addActionListener( this );
			}
			else if (evt.getSource() == cancelButton) {
				cancelDialog();
			}
			else if (evt.getSource() == removeButton) {
				sdInstancesCB.removeActionListener( this );
				removeMappedInstance();
				sdInstancesCB.addActionListener( this );
				sdInstancesCB.setSelectedIndex(0);
			}
			else if (evt.getSource() == downButton) {
				downMappedInstance();
			}
			else if (evt.getSource() == upButton) {
				upMappedInstance();
			}
			else if( evt.getSource() == sdInstancesCB )	{	//user has selected another instance
				sdInstancesCB.removeActionListener( this );
				updateMappableArchUnits();
				sdInstancesCB.addActionListener( this );
			}
			else if( evt.getSource() == communicationPatternsCB )	{	//user has selected another CP. Previous mapping will be deleted
				sdInstancesCB.removeActionListener( this );
				updateSDInstancesList();
				sdInstancesCB.addActionListener( this );
				sdInstancesCB.setSelectedIndex(0);
				updateMappableArchUnits();
				emptyMappedUnitsList();
			}
		}	//End of method
		
		private void mapInstance() {
			
			if( listInstancesHash.get( communicationPatternsCB.getSelectedIndex() ).size() > 0 )	{
				mappedUnitsSL.add( communicationPatternsCB.getSelectedItem().toString() + "." + sdInstancesCB.getSelectedItem().toString() +
													" : " + mappableArchUnitsCB.getSelectedItem().toString() );
				//remove the mapped instance from the list
				HashSet<String> SDinstancesHash = listInstancesHash.get( communicationPatternsCB.getSelectedIndex() );
				Iterator<String> i = SDinstancesHash.iterator();
				while( i.hasNext() )	{
					String element = i.next();
					//TraceManager.addDev( "Comparing " + element + " with " + sdInstancesCB.getSelectedItem().toString() );
					if( element.equals( sdInstancesCB.getSelectedItem().toString() ) )	{
						i.remove();
						//TraceManager.addDev( "Removing instance: " + element );
						break;
					}
				}
				listMappedUnitsJL.setListData( mappedUnitsSL );
				removeButton.setEnabled( true );
				if( SDinstancesHash.size() == 0 )	{	//if the last instance has just being mapped
					mapButton.setEnabled( false );
					sdInstancesSL.removeAllElements();
					sdInstancesSL.add( EMPTY_INSTANCES_LIST );
					makeSDInstancesComboBox( sdInstancesSL );
					mappableArchUnitsSL.removeAllElements();
					mappableArchUnitsSL.add( EMPTY_MAPPABLE_ARCH_UNITS_LIST );
					makeArchitectureUnitsComboBox( mappableArchUnitsSL );
					//TraceManager.addDev( "The DS after removing instance: " + sdInstancesSL.toString() );
					listInstancesHash.set( communicationPatternsCB.getSelectedIndex(), SDinstancesHash );
					//TraceManager.addDev("Nex list done");
				}
				else	{	//update the list with the removed element
					sdInstancesSL = new Vector<String>( SDinstancesHash );
					listInstancesHash.set( communicationPatternsCB.getSelectedIndex(), SDinstancesHash );
					makeSDInstancesComboBox( sdInstancesSL );
					sdInstancesCB.setSelectedIndex(0);
				}
			}
		}
		
		private void removeMappedInstance()	{

			String archUnitName, CPName, instanceName;
			String firstPart[], secondPart[];

			if( listMappedUnitsJL.getSelectedIndex() >= 0 )	{
				firstPart = mappedUnitsSL.get( listMappedUnitsJL.getSelectedIndex() ).split( " : " );
				mappedUnitsSL.removeElementAt( listMappedUnitsJL.getSelectedIndex() );
				secondPart = firstPart[0].split("\\.");
				archUnitName = firstPart[1];
				CPName = secondPart[0];
				instanceName = secondPart[1];
				// put back the instance name into the list and update the combobox
				//
				// from the CPname get the hash set
				// from listCPs get the index of the CP corresponding to CPName. then access listInstancesHash and get the hash list
				int indexCP;
				for( indexCP = 0; indexCP < listCPs.size(); indexCP++ )	{
					if( listCPs.get(indexCP).getName().equals( CPName ) )	{
						break;
					}
				}
				HashSet<String> oldList = listInstancesHash.get( indexCP );
				oldList.add( instanceName );
				listInstancesHash.set( indexCP, oldList );
				sdInstancesSL = new Vector<String>( oldList );
				makeSDInstancesComboBox( sdInstancesSL );
				listMappedUnitsJL.setListData( mappedUnitsSL );
				if( mappedUnitsSL.size() == 0 )	{
					removeButton.setEnabled( false );
				}
			}
		}
		
		private void downMappedInstance()	{
		}
		
		private void upMappedInstance()	{
		}
		
		private void updateSDInstancesList()  {
			
			if( listInstancesHash.get( communicationPatternsCB.getSelectedIndex() ).size() > 0 )  {
				makeSDInstancesComboBox( new Vector<String>( listInstancesHash.get( communicationPatternsCB.getSelectedIndex() ) ) );
			}
			else  {
				Vector<String> emptyList = new Vector<String>();
				emptyList.add( EMPTY_INSTANCES_LIST );
				makeSDInstancesComboBox( emptyList );
			}
		}
		
		private void updateMappableArchUnits()	{
			
			Vector<String> mappableArchUnits = new Vector<String>();
			String selectedInstance = sdInstancesCB.getSelectedItem().toString();
			
			if( sdStorageInstances.contains( selectedInstance ) )	{
				mappableArchUnits = makeListOfMappableArchUnits( STORAGE );
			}
			else	{
				if( sdTransferInstances.contains( selectedInstance ) )	{
					mappableArchUnits = makeListOfMappableArchUnits( TRANSFER );
				}
				else	{
					if( sdControllerInstances.contains( selectedInstance ) )	{
						mappableArchUnits = makeListOfMappableArchUnits( CONTROLLER );
					}
					else	{	//is there is no instance to map
						mappableArchUnits.add( EMPTY_MAPPABLE_ARCH_UNITS_LIST );
					}
				}
			}
			TraceManager.addDev( "Before makingArchComboBox: " + mappableArchUnits.toString() );
			makeArchitectureUnitsComboBox( mappableArchUnits );
			for( int i = 0; i < mappableArchUnitsCB.getModel().getSize(); i++ )	{
				TraceManager.addDev( "After makingArchComboBox: " + mappableArchUnitsCB.getModel().getElementAt(i) );
			}
			valueChanged( null );	//inspect the state of lists and enables/disables buttons accordingly
		}
		
		private void emptyMappedUnitsList()	{
			listMappedUnitsJL.removeAll();
			mappedUnitsSL.clear();
		}
		
		private void makeArchitectureUnitsComboBox( Vector<String> newList )	{
			
			mappableArchUnitsCB.removeAllItems();
			for( String s: newList ) {
				mappableArchUnitsCB.addItem( s );
			}
		}
		
		private void makeSDInstancesComboBox( Vector<String> newList ) {
			
			sdInstancesCB.removeAllItems();
			for( String s: newList ) {
				sdInstancesCB.addItem( s );
			}
		}
		
		public void closeDialog() {
			regularClose = true;
			cancelled = false;
			name = nameOfCP.getText();
			TraceManager.addDev( "Before closing the dialog, name is: " + name + ", " + nameOfCP.getText() );
			/*if( mappedUnitsSL.size() > 1 )	{
			JOptionPane.showMessageDialog( frame, "Only one Bus/Bridge unit can be mapped per Transfer instance",
			"Error", JOptionPane.INFORMATION_MESSAGE );
			return;
			}*/
			dispose();
		}
		
		public void cancelDialog() {
			cancelled = true;
			dispose();
		}
		
		public boolean hasBeenCancelled() {
			return cancelled;
		}
		
		public void valueChanged( ListSelectionEvent e ) {	//this methos is abstract and must be implemented
			int i = listMappedUnitsJL.getSelectedIndex() ;
			
			if( mappableArchUnitsSL.size() > 0 )	{
				if( !mappableArchUnitsSL.get(0).equals( EMPTY_MAPPABLE_ARCH_UNITS_LIST ) )	{
					mapButton.setEnabled( true );
				}
				else	{
					mapButton.setEnabled( false );
				}
			}

			if( i == -1 ) {
				removeButton.setEnabled( false );
				upButton.setEnabled( false );
				downButton.setEnabled( false );
			}
			else	{
				removeButton.setEnabled( true );
				if( i > 0 )	{
					upButton.setEnabled( true );
				}
				else {
					upButton.setEnabled( false );
				}
			}
		}
		
		public String getNodeName() {
			return name;
		}
		
		public String getCPReference() {
			if( emptyCPsList ) {
				return "";
			}
			return (String)( communicationPatternsCB.getSelectedItem() );
		}
		
		public boolean isRegularClose() {
			return regularClose;
		}
		
		public Vector<String> getMappedUnits()	{
			return mappedUnitsSL;
		}
		
		public int indexOf( String name ) {
			
			int i = 0;
			if( communicationPatternsSL.size() > 0 )	{
				for( String s : communicationPatternsSL )	{
					if( s.equals( name ) )	{
						return i;
					}
					i++;
				}
			}
			return 0;
		}
		
		private void createListCPsNames()	{
			if( listCPs.size() > 0 )	{
				for( int i = 0; i < listCPs.size(); i++ )	{
					communicationPatternsSL.add( listCPs.get(i).getName() );
				}
			}
		}
		
		private void createListOfInstances()	{
			
			//j indexes the CP and k indexes the components within a TMLSDPanel
			HashSet<String> sdInstancesNames = new HashSet<String>();
			
			if( listCPs.size() > 0 )	{
				for( int j = 0; j < listCPs.size(); j++ )	{
					Vector<TDiagramPanel> panelList = listCPs.get(j).getPanels();	//the list of AD and SD panels for a given CP
					for( TDiagramPanel panel: panelList )	{
						//TraceManager.addDev( "Into createListInstances, panel name: " + panel.getName() );
						if( panel instanceof TMLSDPanel )	{
							//TraceManager.addDev( "Found TMLSDPanel named: " + panel.getName() );
							LinkedList componentsList = panel.getComponentList();
							for( int k = 0; k < componentsList.size(); k++ )	{
								TGComponent elem = (TGComponent) componentsList.get(k);
								if( elem instanceof TMLSDInstance )	{
									//TraceManager.addDev( "Found a TMLSDInstance named: " + elem.getName() );
									sdInstancesNames.add( elem.getName() );
									if( elem instanceof TMLSDStorageInstance )	{
										sdStorageInstances.add( elem.getName() );
									}
									if( elem instanceof TMLSDTransferInstance )	{
										sdTransferInstances.add( elem.getName() );
									}
									if( elem instanceof TMLSDControllerInstance )	{
										sdControllerInstances.add( elem.getName() );
									}
								}
							}
						}
					}
					listInstancesHash.add( j, sdInstancesNames );
					sdInstancesNames = new HashSet<String>();	//better instead of using clear method
				}
			}
		}
		
		private Vector<String> makeListOfMappableArchUnits( int instanceType )	{
			
			//0 = storage, 1 = transfer, 2 = controller
			Vector<TMLArchiPanel> listArchiPanels = cp.getTDiagramPanel().getMGUI().getTMLArchiDiagramPanels();
			TDiagramPanel panel = listArchiPanels.get(0).getPanels().get(0);	//Do not manage the case with more than 1 architecture panel
			LinkedList componentList = panel.getComponentList();
			Vector<String> list = new Vector<String>();
			
			for( int k = 0; k < componentList.size(); k++ )	{
				if( componentList.get(k) instanceof TMLArchiNode )	{
					if( ( (TMLArchiNode) componentList.get(k) ).getComponentType() == instanceType )	{
						//TraceManager.addDev( "Found a CPU named: " + elem.getName() );
						list.add( ( (TMLArchiNode) componentList.get(k) ).getName() );
					}
				}
			}
			return list;
		}
		
		/*private void eliminateInstancesWithSameName()	{
		if( listInstances.size() > 0 )	{
		for( int i = 0; i < listInstances.size(); i++ )	{
		TraceManager.addDev( "Into first loop, i = " + i );
		String nameToFind = listInstances.get(i).getName();
		if( i < listInstance.size() - 1 )	{
		for( int j = i+1; j < listInstances.size(); j++ )	{
		TraceManager.addDev( "Into second loop, j = " + j + " looking for " + nameToFind );
		if( nameToFind.equals( listInstances.get(j).getName() ) )	{
		listInstances.set( j, null );
		TraceManager.addDev( "Removed instance " + nameToFind );
		}
		}
		}
		}	
		}
		}*/
		
		/*private void createListInstancesNames()	{
		if( listInstances.size() > 0 )	{
		for( int i = 0; i < listInstances.size(); i++ )	{
		listInstancesStrings.add( listInstances.get(i).getName() );
		TraceManager.addDev( "Adding TMLSDInstance " + listInstances.get(i).getName() );
		}
		}
		}*/
		
}	//End of class
