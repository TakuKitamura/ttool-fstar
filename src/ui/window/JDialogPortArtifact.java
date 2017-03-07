/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea ENRICI
 *
 * ludovic.apvrille AT enst.fr, andrea.enrici AT nokia.com
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
 * Class JDialogTMLTaskArtifact
 * Dialog for managing artifact to map ports onto CPs
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import ui.*;
import ui.tmldd.*;
import tmltranslator.modelcompiler.*;

import myutil.*;


public class JDialogPortArtifact extends javax.swing.JDialog implements ActionListener  {
    
	private boolean regularClose;
	private boolean emptyPortsList = false;
    
    private JPanel panel2;
    private Frame frame;
    private TMLArchiPortArtifact artifact;
    private String mappedMemory = "VOID"; 

	protected JComboBox<String> mappedPortCB, memoryCB;
	protected JTextField baseAddressTF, numSamplesTF, bitsPerSymbolTF;
	protected String baseAddress, mappedPort, sampleLength, numSamples, bitsPerSymbol;
	protected String bank, dataType, symmetricalValue;
	protected JComboBox<String> dataTypeCB, bankCB, symmetricalValueCB;

	//Intl Data In
	protected JTextField widthIntl_TF, bitInOffsetIntl_TF, inputOffsetIntl_TF;
	protected String widthIntl, bitInOffsetIntl, inputOffsetIntl, packedBinaryInIntl;
	protected JComboBox<String> packedBinaryInIntl_CB;

	//Intl Data Out
	protected JTextField bitOutOffsetIntl_TF, outputOffsetIntl_TF;
	protected JComboBox<String> packedBinaryOutIntl_CB;
	protected String packedBinaryOutIntl, bitOutOffsetIntl, outputOffsetIntl;

	//Intl Perm
	protected JTextField lengthPermIntl_TF, offsetPermIntl_TF;
	protected String lengthPermIntl, offsetPermIntl;

	//Mapper Data In
	protected JTextField baseAddressDataInMapp_TF, numSamplesDataInMapp_TF, bitsPerSymbolDataInMapp_TF;
	protected String baseAddressDataInMapp, numSamplesDataInMapp, bitsPerSymbolDataInMapp, symmetricalValueDataInMapp;
	protected JComboBox<String> symmetricalValueDataInMapp_CB;
	//Mapper Data Out
	protected JTextField baseAddressDataOutMapp_TF;
	protected String baseAddressDataOutMapp;
	//Mapper LUT
	protected JTextField baseAddressLUTMapp_TF;
	protected String baseAddressLUTMapp;
	
  // Main Panel
  private JButton closeButton;
  private JButton cancelButton;

	//Code generation
	private JPanel panel3; /*panel4, panel5*/;
	private JTabbedPane tabbedPane;
	private int bufferType = 0;
	private ArrayList<String> bufferParameters;
//private String appName = "";
    
    /** Creates new form  */
    public JDialogPortArtifact(Frame _frame, String _title, TMLArchiPortArtifact _artifact, String _mappedMemory, ArrayList<String> _bufferParameters, String _mappedPort ) {
			super(_frame, _title, true);
			frame = _frame;
			artifact = _artifact;
			mappedMemory = _mappedMemory;
			bufferParameters = _bufferParameters; //contains a set of parameters that are read from the xml description. The first parameters is the buffer type
			mappedPort = _mappedPort;
			//appName = mappedPort.split("::")[0];

			TraceManager.addDev("init components");

			initComponents();

			TraceManager.addDev("pack");
			pack();
    }
    
    private void initComponents() {

        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
      //  GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Artifact attributes"));
        panel2.setPreferredSize(new Dimension(650, 350));

        panel3 = new JPanel();
        panel3.setLayout(gridbag2);
        panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: memory configuration"));
        panel3.setPreferredSize(new Dimension(650, 350));

        tabbedPane = new JTabbedPane();
//        panel4 = new JPanel();
//        panel5 = new JPanel();
        
		c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		TraceManager.addDev("Getting communications");
		//Vector<String> list = artifact.getTDiagramPanel().getMGUI().getAllTMLCommunicationNames();
		Vector<String> portsList = artifact.getTDiagramPanel().getMGUI().getAllTMLInputPorts();
   		//Vector<String> portsList = new Vector<String>();
        TraceManager.addDev( "The list of input ports is:\n" + portsList.toString() );
        if( portsList.size() == 0 ) {
            emptyPortsList = true;
            portsList.add( "No available port" );
        }

        // Build the list of available ports, if there is an application diagram
        /*if( list.size() > 0 )   {
	    	int index = 0;
            //parse each entry of list. Entry is in format AppName::chIn__chOut
            for( String s: list )	{
                TraceManager.addDev( "Testing if " + s + " contains " + appName + "...");
                if( s.contains( appName ) )	{	//build the DS for the mapped applications (filter out the case of multiple applications)
                    //TraceManager.addDev( "Parsing: " + s );
                    String[] temp1 = s.split("__");
					String[] temp2 = temp1[0].split( "::" );
    				String chOut = temp2[0] + "::" + temp1[1];
                    //TraceManager.addDev( "chOut = " + chOut );
                    String chIn = temp2[0] + "::" + temp2[1];
                    //TraceManager.addDev( "chIn = " + chIn );
                    if( !portsList.contains( chOut ) )	{
                        portsList.add( chOut );
                    }
                    if( !portsList.contains( chIn ) )	{
                        portsList.add( chIn );
                    }
		    	}
            }
		}
        else    {
            list.add( "No communication to map" );
            emptyPortsList = true;
            portsList.add( "No available port" );
        }*/
		
		TraceManager.addDev( "Got communications" );

        mappedPortCB = new JComboBox<String>( portsList );
		if( !mappedPort.equals( "VOID" ) && !mappedPort.equals( "" ) )	{
			mappedPortCB.setSelectedIndex( portsList.indexOf( mappedPort ) );
		}
		else	{
			mappedPortCB.setSelectedIndex( 0 );
		}
        panel2.add( new JLabel( "Port:" ), c2 );
		mappedPortCB.addActionListener(this);
		panel2.add( mappedPortCB, c1 );
		
		//Make the list of memories that are available for being mapped
		java.util.List<TGComponent> componentList = artifact.getTDiagramPanel().getComponentList();
		Vector<String> memoryList = new Vector<String>();
		for( int k = 0; k < componentList.size(); k++ )	{
			if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
				memoryList.add( ( (TMLArchiMemoryNode) componentList.get(k) ).getName() );
			}
		}
        if( memoryList.size() == 0 )    { // In case there are no memories in the design
            memoryList.add( "No available memory" );              
        }

		memoryCB = new JComboBox<String>( memoryList );
		if( !mappedMemory.equals( "VOID" ) && !mappedMemory.equals( "" ) )	{
			memoryCB.setSelectedIndex( memoryList.indexOf( mappedMemory ) );
		}
		else	{
			memoryCB.setSelectedIndex( 0 );
		}
		panel2.add( new JLabel( "Memory: "),  c2 );
		memoryCB.addActionListener(this);
		panel2.add( memoryCB, c1 );

        if( (emptyPortsList) || (memoryList.size() == 0) )  {
            //the project does not contain an application diagram, or the platform diagram does not contain any memory or both
            bufferType = Buffer.ANOMALY;
        }
        else    {
            //Must distinguish between 2 cases:
            // - bufferParameters is empty because the user has just instantiated the artifact
            // - bufferParameters is not empty as the user had already done some mapping
            if( bufferParameters.size() == 0 )  {   //assign to bufferType the type of the first memory in memoryList
		        for( int k = 0; k < componentList.size(); k++ )	{
        			if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
		        		if( ((TMLArchiMemoryNode) componentList.get(k)).getName().equals( memoryList.get(0) ) ) {
				            bufferType = ((TMLArchiMemoryNode)componentList.get(k)).getBufferType();
                            break;
                        }
        			}
		        }
               // String memoryName = memoryList.get(0);
                //TraceManager.addDev( "bufferType of " + memoryName + " is " + bufferType );
            }
            else    {
                bufferType = Integer.parseInt( bufferParameters.get( Buffer.BUFFER_TYPE_INDEX ) );
            }
        }

		ArrayList<JPanel> panelsList;

		switch( bufferType )	{
			case Buffer.FEP_BUFFER:	
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
			case Buffer.INTERLEAVER_BUFFER:	
				panelsList = InterleaverBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Permutation Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.ADAIF_BUFFER:	
				panelsList = AdaifBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
			case Buffer.MAPPER_BUFFER:	
				tabbedPane.removeAll();
				panelsList = MapperBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Look Up Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.MAIN_MEMORY_BUFFER:	
				panelsList = MMBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
			default:	//the FEP buffer, arbitrary choice - Control flow goes here if there is an anomaly but no tabbedPane is added below
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
		}

		// main panel;
		c0.gridheight = 10;
		c0.weighty = 1.0;
		c0.weightx = 1.0;
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c0.fill = GridBagConstraints.BOTH;
		c.add( panel2, c0 );

        if( ( bufferType == Buffer.MAIN_MEMORY_BUFFER ) || ( bufferType == Buffer.FEP_BUFFER ) || ( bufferType == Buffer.ADAIF_BUFFER ) )    {
            panel3.setBorder( new javax.swing.border.TitledBorder( "Code generation: memory configuration" ) );
			tabbedPane.removeAll();
			tabbedPane.addTab( "Data", panel3 );
			tabbedPane.setSelectedIndex( 0 );
		}
        if( bufferType != Buffer.ANOMALY )  { //Don't add the tabbedPane is there is a bufferType anomaly
    		c.add( tabbedPane, c0 );
        }

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

	private int getBufferTypeFromSelectedMemory( String mappedMemory )	{
		
		java.util.List<TGComponent> componentList = artifact.getTDiagramPanel().getComponentList();
		//Vector<String> list = new Vector<String>();
		
		for( int k = 0; k < componentList.size(); k++ )	{
			if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
				TMLArchiMemoryNode memoryNode = (TMLArchiMemoryNode)componentList.get(k);
				if( memoryNode.getName().equals( mappedMemory ) )	{
					return memoryNode.getBufferType();
				}
			}
		}
		return 0;	//default: the main memory buffer
	}
    
  public void	actionPerformed(ActionEvent evt)  {

		if( evt.getSource() == memoryCB )	{
			updateBufferPanel();
		}
        
    String command = evt.getActionCommand();
    // Compare the action command to the known actions.
   	if (command.equals("Save and Close"))  {
    	closeDialog();
    } else if (command.equals("Cancel")) {
    	cancelDialog();
		}
	}

	private void updateBufferPanel()	{

		GridBagConstraints c1 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();

		c1.gridwidth = 1;
		c1.gridheight = 1;
		c1.weighty = 1.0;
		c1.weightx = 1.0;
		c1.fill = GridBagConstraints.HORIZONTAL;
    c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		
		//flushBuffersStrings();
		bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
		ArrayList<JPanel> panelsList;

		switch( bufferType )	{
			case Buffer.FEP_BUFFER:	
				tabbedPane.removeAll();
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
			case Buffer.MAPPER_BUFFER:	
				tabbedPane.removeAll();
				panelsList = MapperBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Look Up Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.ADAIF_BUFFER:	
				tabbedPane.removeAll();
				panelsList = AdaifBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
			case Buffer.INTERLEAVER_BUFFER:
				tabbedPane.removeAll();
				panelsList = InterleaverBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Permutation Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.MAIN_MEMORY_BUFFER:	
				tabbedPane.removeAll();
				panelsList = MMBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
			default:	//the main memory buffer 
				tabbedPane.removeAll();
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
		}
	}

    public void closeDialog() {

        regularClose = true;
				mappedMemory = (String) memoryCB.getItemAt( memoryCB.getSelectedIndex() );
				bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
				switch ( bufferType )	{
					case Buffer.FEP_BUFFER:
						if( !FepBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.MAPPER_BUFFER:	
						if( !MapperBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.ADAIF_BUFFER:	
						if( !AdaifBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.INTERLEAVER_BUFFER:	
						if( !InterleaverBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.MAIN_MEMORY_BUFFER:	
						if( !MMBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					default:	//the main memory buffer 
						if( !FepBuffer.closePanel( frame ) )	{
							return;
						}
						break;
				}
        dispose();
    }

		public String getMappedPort()	{
			return mappedPort;
		}

		public String getMappedMemory()	{
			return mappedMemory;
		}

		public String getStartAddress()	{
			return baseAddress;
		}

    public void cancelDialog() {
        dispose();
    }
    
    public boolean isRegularClose() {
        return regularClose;
    }
	
	public String getReferenceCommunicationName() {
		if (emptyPortsList) {
			return null;
		}
		String tmp = (String)( mappedPortCB.getSelectedItem() );
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        return tmp.substring(0, index);
    }
    
    public String getCommunicationName() {
        String tmp = (String)( mappedPortCB.getSelectedItem() );
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        tmp = tmp.substring(index+2, tmp.length());
		
		index =  tmp.indexOf("(");
		if (index > -1) {
			tmp = tmp.substring(0, index).trim();
		}
		return tmp;
    }
	
	 public String getTypeName() {
		String tmp = (String)( mappedPortCB.getSelectedItem() );
		int index1 = tmp.indexOf("(");
		int index2 = tmp.indexOf(")");
		if ((index1 > -1) && (index2 > index1)) {
			return tmp.substring(index1+1, index2);
		}
		return "";
	 }
	
	public int indexOf(Vector<String> _list, String name) {
		int i = 0;
		for(String s : _list) {
			if (s.equals(name)) {
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public ArrayList<String> getBufferParameters()	{

		ArrayList<String> params = new ArrayList<String>();
		params.add( String.valueOf( bufferType ) );
		switch( bufferType )	{
			case Buffer.FEP_BUFFER:
				params = FepBuffer.getBufferParameters();
				break;
			case Buffer.INTERLEAVER_BUFFER:	
				params = InterleaverBuffer.getBufferParameters();
				break;
			case Buffer.ADAIF_BUFFER:
				params = AdaifBuffer.getBufferParameters();
				break;
			case Buffer.MAPPER_BUFFER:	
				params = MapperBuffer.getBufferParameters();
				break;
			case Buffer.MAIN_MEMORY_BUFFER:	
				params = MMBuffer.getBufferParameters();
				break;
			default:	//the main memory buffer
				params = FepBuffer.getBufferParameters();
				break;
		}
		return params;
	}
//
//	private void cleanPanels()	{
//		panel3.removeAll();
//		panel4.removeAll();
//		panel5.removeAll();
//		tabbedPane.removeAll();
//	}
//
//	private void revalidateAndRepaintPanels()	{
//		panel3.revalidate();
//		panel3.repaint();
//		panel4.revalidate();
//		panel4.repaint();
//		panel5.revalidate();
//		panel5.repaint();
//	}

}	//End of class
