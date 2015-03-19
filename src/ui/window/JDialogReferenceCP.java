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
import java.util.Collections;

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
	
	private ArrayList<HashSet<String>> listInstancesHash = new ArrayList<HashSet<String>>();	// the list of AVAILABLE instances
	// and array list containing the SD instances for each CP. The array list is indexed the same way as listCPs
	private ArrayList<HashSet<String>> listOfMappedInstances = new ArrayList<HashSet<String>>();
	private ArrayList<HashSet<String>> sdStorageInstances = new ArrayList<HashSet<String>>();
	private ArrayList<HashSet<String>> sdTransferInstances = new ArrayList<HashSet<String>>();
	private ArrayList<HashSet<String>> sdControllerInstances = new ArrayList<HashSet<String>>();
	
	private Vector<String> mappableArchUnitsSL;
	private Vector<String> sdInstancesSL;
	private Vector<String> cpMECsSL;
	
	private int indexListCPsNames = 0;
	
	private boolean emptyCPsList = false;
	private boolean emptyListOfMappedUnits = true;	//true if there is no mapping info
	
	private boolean cancelled = false;
	
	// Panel1
	private JPanel panel1;
	private JComboBox sdInstancesCB, mappableArchUnitsCB, communicationPatternsCB;
	private JButton mapButton;
	private JList mappableArchUnitsJL;
	private JScrollPane mappableArchUnitsSP;
	
	//Panel2
	private JPanel panel2;
	private JList listMappedUnitsJL;
	private JButton upButton;
	private JButton downButton;
	private JButton removeButton;
	private JScrollPane scrollPane;

	//Panel3, code generation
	private JPanel panel3;
	private JComboBox cpMECsCB;
	private JList cpMECsList;
	private String cpMEC;
	
	// Main Panel
	private JButton closeButton;
	private JButton cancelButton;
	
	/** Creates new form  */
	public JDialogReferenceCP( JFrame _frame,  String _title, TMLArchiCPNode _cp, Vector<String> _mappedUnits, String _name, String _cpMEC ) {
	
	super( _frame, _title, true );
	frame = _frame;
	cp = _cp;
	name = _name;
	cpMEC = _cpMEC;
	
	if( _mappedUnits.size() > 0 )	{
		//the validity of _mappedUnits is checked when initializing components
		mappedUnitsSL = new Vector<String>();	//take into account the elements already mapped
		mappedUnitsSL.addAll( 0, _mappedUnits );
		emptyListOfMappedUnits = false;
	}
	else	{
		mappedUnitsSL = new Vector<String>();
	}
	
	initComponents();
	valueChanged( null );
	//myInitComponents();
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
			panel1.setPreferredSize(new Dimension(325, 250));
			
			panel2 = new JPanel();
			panel2.setLayout(gridbag2);
			panel2.setBorder(new javax.swing.border.TitledBorder("Managing mapping"));
			panel2.setPreferredSize(new Dimension(325, 250));

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
			
			communicationPatternsSL = createListCPsNames();	//fill listCPs and return the string version of the list of all CPs
			/*if( !emptyCPsList ) {
				indexListCPsNames = indexOf( cp.getReference() );
			}*/
			
			//fifth line panel1
			panel1.add( new JLabel( "Available CPs:"), c1 );
			communicationPatternsCB = new JComboBox( communicationPatternsSL );
			if( !emptyListOfMappedUnits )	{
				communicationPatternsCB.setSelectedItem( cp.getReference() );
			}
			else	{
				communicationPatternsCB.setSelectedIndex(0);
			}
			communicationPatternsCB.addActionListener( this );
			communicationPatternsCB.setMinimumSize( new Dimension(150, 50) );
			panel1.add( communicationPatternsCB, c1 );
			
			//sixth line panel1
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);
			
			sdInstancesSL = new Vector<String>();
			// Create the array lists of HashSet listInstancesHash, sdControllerInstances, sdStorageInstances and sdTransferInstances
			createListsOfInstances();
			if( sdInstancesSL.size() == 0 )	{	//protect against the case of a CP with no SDs
				sdInstancesSL.add( EMPTY_INSTANCES_LIST );
			}
			
			//seventh line panel1
			panel1.add( new JLabel( "Instance:" ), c1 );
			sdInstancesCB = new JComboBox( sdInstancesSL );
			sdInstancesCB.setSelectedIndex( 0 );
			sdInstancesCB.addActionListener( this );
			sdInstancesCB.setMinimumSize( new Dimension(150, 50) );
			panel1.add( sdInstancesCB, c1 );
			
			//eigth line panel1
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 3;
			panel1.add(new JLabel(" "), c1);
			
			mappableArchUnitsSL = new Vector<String>();	//the string list used in the architecture units combo box

			checkValidityOfMappedUnits();	

			makeListOfMappableArchUnitsSL();
			
			//nineth line panel1
			mappableArchUnitsJL = new JList( mappableArchUnitsSL );
			mappableArchUnitsJL.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			mappableArchUnitsJL.addListSelectionListener( this );
			mappableArchUnitsSP = new JScrollPane( mappableArchUnitsJL );
			mappableArchUnitsSP.setSize( 300, 250 );
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 5;
			c1.weighty = 10.0;
			c1.weightx = 10.0;
			panel1.add( mappableArchUnitsSP, c1 );
			
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
			listMappedUnitsJL = new JList( mappedUnitsSL );
			listMappedUnitsJL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listMappedUnitsJL.addListSelectionListener(this);
			scrollPane = new JScrollPane( listMappedUnitsJL );
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

			//panel3, code generation
			panel3 = new JPanel();
			panel3.setLayout(gridbag3);
			panel3.setBorder(new javax.swing.border.TitledBorder("Code generation"));
			panel3.setPreferredSize(new Dimension(325, 250));
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
			c3.fill = GridBagConstraints.HORIZONTAL;
			c3.anchor = GridBagConstraints.CENTER;
			panel3.add( new JLabel( "Model Extension Construct:" ), c3 );
			cpMECsSL = new Vector<String>();
			cpMECsSL.add( "Memory Copy" );
			cpMECsSL.add( "Single DMA" );
			cpMECsSL.add( "Double DMA" );
			cpMECsCB = new JComboBox( cpMECsSL );
			if( cpMEC.equals( "VOID" ) || cpMEC.equals( "" ) )	{
				cpMECsCB.setSelectedIndex( 0 );
			}
			else	{
				cpMECsCB.setSelectedIndex( cpMECsSL.indexOf( cpMEC ) );
			}
			cpMECsCB.addActionListener( this );
			cpMECsCB.setMinimumSize( new Dimension(150, 50) );
			panel3.add( cpMECsCB, c3 );
			
			// main panel;
			c0.gridwidth = 1;
			c0.gridheight = 10;
			c0.weighty = 1.0;
			c0.weightx = 1.0;
			c0.fill = GridBagConstraints.BOTH;
			
			c.add(panel1, c0);
			c0.gridwidth = GridBagConstraints.REMAINDER; //end row
			c.add(panel2, c0);
			c0.gridwidth = GridBagConstraints.REMAINDER; //end row
			c.add(panel3, c0);
			
			c0.gridwidth = 1;
			c0.gridheight = 1;
			c0.fill = GridBagConstraints.VERTICAL;
			closeButton = new JButton("Save and Close", IconManager.imgic25);
			closeButton.setPreferredSize(new Dimension(600, 50));
			closeButton.addActionListener(this);
			c.add(closeButton, c0);
			c0.gridwidth = GridBagConstraints.REMAINDER; //end row
			cancelButton = new JButton("Cancel", IconManager.imgic27);
			cancelButton.setPreferredSize(new Dimension(600, 50));
			cancelButton.addActionListener(this);
			c.add(cancelButton, c0);
		}

		private void makeListOfMappableArchUnitsSL()	{

			int j = getIndexOfSelectedCP();

			if( !sdInstancesSL.get(0).equals( EMPTY_INSTANCES_LIST ) )	{
				if( sdStorageInstances.get(j).contains( sdInstancesSL.get(0) ) )	{
					mappableArchUnitsSL = makeListOfMappableArchUnits( STORAGE );
				}
				else	{
					if( sdTransferInstances.get(j).contains( sdInstancesSL.get(0) ) )	{
						mappableArchUnitsSL = makeListOfMappableArchUnits( TRANSFER );
					}
					else	{
						if( sdControllerInstances.get(j).contains( sdInstancesSL.get(0) ) )	{
							mappableArchUnitsSL = makeListOfMappableArchUnits( CONTROLLER );
						}
					}
				}
			}
			if( mappableArchUnitsSL.size() == 0 ) {
				mappableArchUnitsSL.add( EMPTY_MAPPABLE_ARCH_UNITS_LIST );
			}
		}

		private void checkValidityOfMappedUnits()	{
			
			ArrayList<String> info;	//Will contain: info[0] = CPName, info[1] = instanceName, info[2] = arcUnitName

			Iterator<String> it	= mappedUnitsSL.iterator();
			while( it.hasNext() )	{
				info = retrieveSingleInformationFromMappingString( it.next() );
				String CPname = info.get(0);
				String instanceName = info.get(1);
				if( !doesCPexist( CPname ) )	{
					it.remove();
				}
				else	{
					//if the instance exists, remove it from listInstancesHash and add it to the list of mapped instances
					if( !checkAndRemoveIfInstanceExists( CPname, instanceName ) )	{	
						it.remove();
					}
					else	{
						// a transfer instance was mapped on more than one arch unit
						for( int i = 2; i < info.size(); i++ )	{
							if( !doesArchUnitExist( info.get(i) ) )	{
								it.remove();
								restoreInstanceName( CPname, instanceName );	//put back the instance in listInstancesHash
							}
						}
					}
				}
			}
		}

		private ArrayList<String> retrieveSingleInformationFromMappingString( String s )	{

			ArrayList<String> info = new ArrayList<String>();
			String[] firstPart = s.split( " : " );
			String[] secondPart = firstPart[0].split("\\.");
			String[] otherUnits = firstPart[1].split("\\, ");
			if( otherUnits.length > 1 )	{	//a transfer instance mapped on more than one arch unit
				info.add( secondPart[0] );
				info.add( secondPart[1] );
				for( String st: otherUnits )	{
					info.add( st ); //{ CPName, instanceName, archUnitNameS };
				}
				return info;
			}
			else	{
				info.add( secondPart[0] );
				info.add( secondPart[1] );
				info.add( firstPart[1] ); //{ CPName, instanceName, archUnitName };
			}
			return info;
		}

		private void restoreInstanceName( String CPName, String instanceName )	{
			for( int i = 0; i < listCPs.size(); i++ )	{
				if( listCPs.get(i).getName().equals( CPName ) )	{
					HashSet<String> tempHash = listInstancesHash.get(i);
					tempHash.add( instanceName );
					listInstancesHash.set( i, tempHash );
					freezeSDInstancesCB();
					makeSDInstancesComboBox( new Vector<String>( tempHash ) );
					unfreezeSDInstancesCB();
					return;
				}
			}
		}
		
		private boolean doesCPexist( String CPName )	{
			
			for( String s: communicationPatternsSL )	{
				if( s.equals( CPName ) )	{
					//TraceManager.addDev( "CPName: " + CPName + " exists" );
					return true;
				}
			}
			return false;
		}
		
		private boolean checkAndRemoveIfInstanceExists( String CPname, String instanceName )	{

			for( int i = 0; i < listCPs.size(); i++ )	{
				if( listCPs.get(i).getName().equals( CPname ) )	{
					HashSet<String> tempHash = listInstancesHash.get(i);
					if( tempHash.contains( instanceName ) )	{
						tempHash.remove( instanceName );
						listInstancesHash.set( i, tempHash );
						freezeSDInstancesCB();
						if( tempHash.size() == 0 )	{
							tempHash.add( EMPTY_INSTANCES_LIST );
						}
						makeSDInstancesComboBox( new Vector<String>( tempHash ) );
						unfreezeSDInstancesCB();
						HashSet<String> oldListOfMappedInstances = listOfMappedInstances.get(i);
						oldListOfMappedInstances.remove( "VOID" );
						oldListOfMappedInstances.add( instanceName );
						listOfMappedInstances.set( i, oldListOfMappedInstances );
						return true;
					}
				}
			}
			return false;
		}
		
		private boolean doesArchUnitExist( String archUnitName )	{

			if( makeListOfMappableArchUnits( STORAGE ).contains( archUnitName ) )	{
				//TraceManager.addDev( "ArchUnit: " + archUnitName + " exists" );
				return true;
			}
			if( makeListOfMappableArchUnits( CONTROLLER ).contains( archUnitName ) )	{
				//TraceManager.addDev( "ArchUnit: " + archUnitName + " exists" );
				return true;
			}
			if( makeListOfMappableArchUnits( TRANSFER ).contains( archUnitName ) )	{
				//TraceManager.addDev( "ArchUnit: " + archUnitName + " exists" );
				return true;
			}
			return false;
		}
		
		public void	actionPerformed( ActionEvent evt )  {
			//String command = evt.getActionCommand();
			
			// Compare the action command to the known actions.
			if( evt.getSource() == closeButton )  {
				closeDialog();
			}
			else if( evt.getSource() == cancelButton ) {
				cancelDialog();
			}
			else if( evt.getSource() == downButton ) {
				downMappedInstance();
			}
			else if( evt.getSource() == upButton ) {
				upMappedInstance();
			}
			else if( evt.getSource() == mapButton ) {
				freezeSDInstancesCB();
				mapInstance();
				unfreezeSDInstancesCB();
				sdInstancesCB.setSelectedIndex(0);
				updateMappableArchUnits();
				valueChanged( null );
			}
			else if( evt.getSource() == removeButton ) {
				freezeSDInstancesCB();
				removeMappedInstance();
				sdInstancesCB.setSelectedIndex(0);
				updateMappableArchUnits();
				unfreezeSDInstancesCB();
				valueChanged( null );
			}
			else if( evt.getSource() == sdInstancesCB )	{	//user has selected another instance
				freezeSDInstancesCB();
				updateMappableArchUnits();
				unfreezeSDInstancesCB();
				valueChanged( null );
			}
			else if( evt.getSource() == communicationPatternsCB )	{	//user has selected another CP. Previous mapping will be deleted
				freezeAllComboBoxes();
				freeMappedUnits();
				updateSDInstancesList();
				updateMappableArchUnits();
				unfreezeAllComboBoxes();
				valueChanged( null );
			}
			else if( evt.getSource() == scrollPane )	{
				manageScrollPaneButtons();
			}
		}	//End of method
		
		private void mapInstance() {

			String instanceToMap = sdInstancesCB.getSelectedItem().toString();

			int j = getIndexOfSelectedCP();
			if( listInstancesHash.get( communicationPatternsCB.getSelectedIndex() ).size() > 0 )	{
				int[] indices = mappableArchUnitsJL.getSelectedIndices();
				if( indices.length > 1 )	{	//selecting more than one unit/instance
					if( sdTransferInstances.get(j).contains( instanceToMap ) )	{
						StringBuffer sb = new StringBuffer( communicationPatternsCB.getSelectedItem().toString() + "." +
																								instanceToMap + " : " );
						for( int i = 0; i < indices.length; i++ )	{
							sb.append( mappableArchUnitsSL.get( indices[i]	) + ", ") ;
						}
						mappedUnitsSL.add( sb.toString().substring( 0, sb.length() - 2 ) );
					}
					else	{	//only transfer instances can be mapped on more than one architecture unit 
            JOptionPane.showMessageDialog( frame, "More than one architecture unit selected for mapping",
																					"Error", JOptionPane.INFORMATION_MESSAGE );
            return;
					}
				}
				else	{	//selecting only one unit/instance
					mappedUnitsSL.add( communicationPatternsCB.getSelectedItem().toString() + "." + instanceToMap +
													" : " + mappableArchUnitsSL.get( mappableArchUnitsJL.getSelectedIndex() ) );
				}
				// add the mapped instance to the list of mapped instances
				HashSet<String> oldListOfMappedInstances;
				oldListOfMappedInstances = listOfMappedInstances.get(j);
				oldListOfMappedInstances.remove( "VOID" );
				oldListOfMappedInstances.add( instanceToMap );
				listOfMappedInstances.set( j, oldListOfMappedInstances );

				//remove the mapped instance from the list of available instances
				HashSet<String> SDinstancesHash = listInstancesHash.get( j );
				Iterator<String> i = SDinstancesHash.iterator();
				while( i.hasNext() )	{
					String element = i.next();
					//TraceManager.addDev( "Comparing " + element + " with " + sdInstancesCB.getSelectedItem().toString() );
					if( element.equals( instanceToMap ) )	{
						i.remove();
						//TraceManager.addDev( "Removing instance: " + element );
						break;
					}
				}

				listMappedUnitsJL.setListData( mappedUnitsSL );
				//removeButton.setEnabled( true );
				if( SDinstancesHash.size() == 0 )	{	//if the last instance has just being mapped
					//mapButton.setEnabled( false );
					sdInstancesSL.removeAllElements();
					sdInstancesSL.add( EMPTY_INSTANCES_LIST );
					freezeAllComboBoxes();
					makeSDInstancesComboBox( sdInstancesSL );
					mappableArchUnitsSL.removeAllElements();
					mappableArchUnitsSL.add( EMPTY_MAPPABLE_ARCH_UNITS_LIST );
					makeArchitectureUnitsScrollPane( mappableArchUnitsSL );
					unfreezeAllComboBoxes();
					TraceManager.addDev( "The DS after removing instance: " + SDinstancesHash.toString() );
					listInstancesHash.set( j, SDinstancesHash );
					//TraceManager.addDev("Nex list done");
				}
				else	{	//update the list with the removed element
					sdInstancesSL = new Vector<String>( SDinstancesHash );
					listInstancesHash.set( j, SDinstancesHash );
					freezeSDInstancesCB();
					makeSDInstancesComboBox( sdInstancesSL );
					unfreezeSDInstancesCB();
				}
			}
		}
		
		private void removeMappedInstance()	{

			String archUnitName, CPName, instanceName;

			if( listMappedUnitsJL.getSelectedIndex() >= 0 )	{
				ArrayList<String> info = retrieveSingleInformationFromMappingString( mappedUnitsSL.get( listMappedUnitsJL.getSelectedIndex() ) );
				mappedUnitsSL.removeElementAt( listMappedUnitsJL.getSelectedIndex() );
				CPName = info.get(0);
				instanceName = info.get(1);
				int indexCP;
				for( indexCP = 0; indexCP < listCPs.size(); indexCP++ )	{
					if( listCPs.get(indexCP).getName().equals( CPName ) )	{
						break;
					}
				}
				HashSet<String> oldListOfMappedInstances = listOfMappedInstances.get( indexCP );
				oldListOfMappedInstances.remove( instanceName );
				listOfMappedInstances.set( indexCP, oldListOfMappedInstances );
				//TraceManager.addDev( "The DS of mapped instances: " + oldListOfMappedInstances.toString() );

				HashSet<String> oldList = listInstancesHash.get( indexCP );	// it is the list of all instances for a given CP
				//TraceManager.addDev( "Adding " + instanceName + " to oldList: " + oldList.toString() );
				oldList.add( instanceName );
				listInstancesHash.set( indexCP, oldList );
				//TraceManager.addDev( "sdInstancesL: " + sdInstancesSL.toString() );
				sdInstancesSL = new Vector<String>( oldList );
				makeSDInstancesComboBox( sdInstancesSL );
				listMappedUnitsJL.setListData( mappedUnitsSL );
				if( mappedUnitsSL.size() == 0 )	{
					removeButton.setEnabled( false );
				}
			}
		}
		
		private void downMappedInstance()	{

			int index = listMappedUnitsJL.getSelectedIndex();
			if( index < (mappedUnitsSL.size() - 1 ) )	{
				Collections.swap( mappedUnitsSL, index, index + 1 );
				listMappedUnitsJL.setListData( mappedUnitsSL );
			}
		}
		
		private void upMappedInstance()	{

			int index = listMappedUnitsJL.getSelectedIndex();
			if( index > 0 )	{
				Collections.swap( mappedUnitsSL, index, index - 1 );
				listMappedUnitsJL.setListData( mappedUnitsSL );
			}
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
		
		// Updates mappableArchUnitsSL and the comboBox of the architecture units scroll pane
		private void updateMappableArchUnits()	{
			
			String selectedInstance = "";
			if( sdInstancesCB.getSelectedItem() != null )	{
				selectedInstance = sdInstancesCB.getSelectedItem().toString();
			}
			else	{
				selectedInstance = sdInstancesSL.get(0);
			}
			//TraceManager.addDev( "Selected instance: " + selectedInstance );

			//get the CP index
			int j = getIndexOfSelectedCP();

			if( sdStorageInstances.get(j).contains( selectedInstance ) )	{
				mappableArchUnitsSL = makeListOfMappableArchUnits( STORAGE );
				//TraceManager.addDev( "Found a storage instance: " + mappableArchUnitsSL.toString() );
			}
			else	{
				if( sdTransferInstances.get(j).contains( selectedInstance ) )	{
					mappableArchUnitsSL = makeListOfMappableArchUnits( TRANSFER );
					//TraceManager.addDev( "Found a transfer instance: " + mappableArchUnitsSL.toString() );
				}
				else	{
					if( sdControllerInstances.get(j).contains( selectedInstance ) )	{
						mappableArchUnitsSL = makeListOfMappableArchUnits( CONTROLLER );
						//TraceManager.addDev( "Found a controller instance: " + mappableArchUnitsSL.toString() );
					}
					else	{	//is there is no instance to map
						mappableArchUnitsSL = new Vector<String>();
						mappableArchUnitsSL.add( EMPTY_MAPPABLE_ARCH_UNITS_LIST );
						//TraceManager.addDev( "Found OTHER instance: " + mappableArchUnitsSL.toString() );
					}
				}
			}
			makeArchitectureUnitsScrollPane( mappableArchUnitsSL );
		}

		private int getIndexOfSelectedCP()	{

			if( listCPs.size() > 0 )	{
				for( int j = 0; j < listCPs.size(); j++ )	{
					if( listCPs.get(j).getName().equals( communicationPatternsCB.getSelectedItem() ) )	{
						return j;
					}
				}
			}
		return -1;
		}
		
		private void freeMappedUnits()	{

			//before eliminating the list of mapped units, put the instances back in the general data structure
			for( int i = 0; i < mappedUnitsSL.size(); i++ )	{
				ArrayList<String> info = retrieveSingleInformationFromMappingString( mappedUnitsSL.get(i) );
				restoreInstanceName( info.get(0), info.get(1) );
			}
			mappedUnitsSL.clear();
			listMappedUnitsJL.setListData( mappedUnitsSL );
		}
		
		private void makeArchitectureUnitsScrollPane( Vector<String> newList )	{
			
			mappableArchUnitsSL = new Vector<String>( newList );
			mappableArchUnitsJL.setListData( mappableArchUnitsSL );
		}
		
		private void makeSDInstancesComboBox( Vector<String> newList ) {
			
			if( ( newList.size() > 1 ) && ( newList.contains( EMPTY_INSTANCES_LIST ) ) )	{
				newList.removeElementAt( newList.indexOf( EMPTY_INSTANCES_LIST ) );
			}
			sdInstancesCB.removeAllItems();
			for( String s: newList ) {
				sdInstancesCB.addItem( s );
			}
			sdInstancesSL = new Vector<String>( newList );
		}
		
		public void closeDialog() {
			regularClose = true;
			cancelled = false;
			name = nameOfCP.getText();
			cpMEC = (String)cpMECsCB.getSelectedItem();
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
			
			//Enable or disable the mapping button. Do not use &&, || as they are short-circuit operators
			if( listCPs.size() > 0 )	{
				if( sdInstancesSL.size() > 0 )	{
					if( !sdInstancesSL.get(0).equals( EMPTY_INSTANCES_LIST ) )	{
						if( mappableArchUnitsSL.size() > 0 )	{
							if( !mappableArchUnitsSL.get(0).equals( EMPTY_MAPPABLE_ARCH_UNITS_LIST ) )	{
								mapButton.setEnabled( true );
							}
							else	{
								mapButton.setEnabled( false );
							}
						}
						else	{
							mapButton.setEnabled( false );
						}
					}
					else	{
						mapButton.setEnabled( false );
					}
				}
				else	{
					mapButton.setEnabled( false );
				}
			}
			else	{
				mapButton.setEnabled( false );
			}

		}

		public void manageScrollPaneButtons()	{

			int i = listMappedUnitsJL.getSelectedIndex() ;
			if( i == -1 ) {
				removeButton.setEnabled( false );
				upButton.setEnabled( false );
				downButton.setEnabled( false );
			}
			else	{
				removeButton.setEnabled( true );
				if( i == 0 )	{	//the first element
					upButton.setEnabled( false );
					downButton.setEnabled( true );
				}
				if( i == ( mappedUnitsSL.size()-1 ) )	{	//the last element
					upButton.setEnabled( true );
					downButton.setEnabled( false );
				}
				else	{	//the remaining cases
					upButton.setEnabled( true );
					downButton.setEnabled( true );
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
		
		private Vector<String> createListCPsNames()	{

			Vector<String> list = new Vector<String>();
			listCPs = cp.getTDiagramPanel().getMGUI().getAllTMLCP();
			if( listCPs.size() > 0 )	{
				for( int i = 0; i < listCPs.size(); i++ )	{
					list.add( listCPs.get(i).getName() );
				}
				emptyCPsList = false;
			}
			else	{
				list.add( EMPTY_CPS_LIST );
				emptyCPsList = true;
			}
			return list;
		}

		// Create the array lists of HashSet listInstancesHash, sdControllerInstances, sdStorageInstances and sdTransferInstances
		private void createListsOfInstances()	{
			
			HashSet<String> sdInstancesNames = new HashSet<String>();
			HashSet<String> sdControllerInstances_local = new HashSet<String>();
			HashSet<String> sdStorageInstances_local = new HashSet<String>();
			HashSet<String> sdTransferInstances_local = new HashSet<String>();
			HashSet<String> mappedSDInstances_local = new HashSet<String>();	//just to initialize the data structure
			
			//j indexes the CP and k indexes the components within a TMLSDPanel
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
									sdInstancesNames.add( elem.getName() );
									if( elem instanceof TMLSDStorageInstance )	{
										sdStorageInstances_local.add( elem.getName() );
									}
									if( elem instanceof TMLSDTransferInstance )	{
										sdTransferInstances_local.add( elem.getName() );
									}
									if( elem instanceof TMLSDControllerInstance )	{
										sdControllerInstances_local.add( elem.getName() );
									}
								}
							}	/* end of for over k */
						}
					}	/* end of examining all diagrams for a CP */
					for( String s: sdInstancesNames )	{
						if( listCPs.get(j).getName().equals( communicationPatternsCB.getSelectedItem() ) )	{
							TraceManager.addDev( "Found a TMLSDInstance named: " + s );
							if( !isInstanceMapped( s ) )	{
								sdInstancesSL.add( s );	//the string list displayed in the combo box
								TraceManager.addDev( "Instance " + s + " is un-mapped. Adding to SL list" );
							}
						}	
					}
					listInstancesHash.add( j, sdInstancesNames );									//for each CP the list of instances
					sdStorageInstances.add( j, sdStorageInstances_local );				//for each CP the list of storage instances
					sdTransferInstances.add( j, sdTransferInstances_local );			//for each CP the list of controller instances
					sdControllerInstances.add( j, sdControllerInstances_local );	//for each CP the list of transfer instances
					mappedSDInstances_local.add( "VOID" );
					listOfMappedInstances.add( j, mappedSDInstances_local );			//just to initialize the data structure
					TraceManager.addDev( "CP name: " + listCPs.get(j).getName() );
					TraceManager.addDev( "List of storage instances: " + sdStorageInstances.get(j).toString() );
					TraceManager.addDev( "List of transfer instances: " + sdTransferInstances.get(j).toString() );
					TraceManager.addDev( "List of controller instances: " + sdControllerInstances.get(j).toString() );
					sdInstancesNames = new HashSet<String>();	//better than using clear method
					sdStorageInstances_local = new HashSet<String>();
					sdTransferInstances_local = new HashSet<String>();
					sdControllerInstances_local = new HashSet<String>();
					mappedSDInstances_local = new HashSet<String>();
				}
			}
		}

		private boolean isInstanceMapped( String instanceName )	{

			ArrayList<String> info;
			for( String st: mappedUnitsSL )	{
				info = retrieveSingleInformationFromMappingString( st );
				if( info.get(1).equals( instanceName ) )	{
					TraceManager.addDev( "Instance " + info.get(1) + " is mapped" );
					return true;
				}
			}
			return false;
		}
		
		private Vector<String> makeListOfMappableArchUnits( int instanceType )	{
			
			//0 = storage, 1 = transfer, 2 = controller
			LinkedList componentList = cp.getTDiagramPanel().getComponentList();
			Vector<String> list = new Vector<String>();
			
			for( int k = 0; k < componentList.size(); k++ )	{
				if( componentList.get(k) instanceof TMLArchiNode )	{
					if( ( (TMLArchiNode) componentList.get(k) ).getComponentType() == instanceType )	{
						list.add( ( (TMLArchiNode) componentList.get(k) ).getName() );
					}
				}
			}
			return list;
		}

	private void freezeSDInstancesCB()	{
		sdInstancesCB.removeActionListener( this );	
	}

	private void unfreezeSDInstancesCB()	{
		sdInstancesCB.addActionListener( this );	
	}

	private void freezeAllComboBoxes()	{
		sdInstancesCB.removeActionListener( this );	
		communicationPatternsCB.removeActionListener( this );
	}

	private void unfreezeAllComboBoxes()	{
		sdInstancesCB.addActionListener( this );	
		communicationPatternsCB.addActionListener( this );
	}

	public String getCPMEC()	{
		return cpMEC;
	}
		
}	//End of class
