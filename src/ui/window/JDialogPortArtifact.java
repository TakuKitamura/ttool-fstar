/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 * Class JDialogTMLTaskArtifact
 * Dialog for managing artifacts on hw nodes
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
import java.util.*;

import ui.*;
import ui.tmldd.*;
import tmltranslator.ctranslator.*;

import myutil.*;


public class JDialogPortArtifact extends javax.swing.JDialog implements ActionListener  {
    
	private boolean regularClose;
	private boolean emptyList = false;
    
  private JPanel panel2;
  private Frame frame;
  private TMLArchiPortArtifact artifact;
  private String mappedMemory = "VOID"; 

	protected JComboBox referenceCommunicationName, priority, memoryCB;
	protected JTextField baseAddressTF, numSamplesTF, bitOffsetFirstSymbolTF, bitsPerSymbolTF;
	protected String baseAddress, mappedPort, sampleLength, numSamples, bitOffsetFirstSymbol, bitsPerSymbol;
	protected String bank, dataType, symmetricalValue;
	protected JComboBox dataTypeCB, bankCB, symmetricalValueCB;

	//Intl Data In
	protected JTextField widthIntl_TF, bitInOffsetIntl_TF, inputOffsetIntl_TF;
	protected String widthIntl, bitInOffsetIntl, inputOffsetIntl, packedBinaryInIntl;
	protected JComboBox packedBinaryInIntl_CB;

	//Intl Data Out
	protected JTextField bitOutOffsetIntl_TF, outputOffsetIntl_TF;
	protected JComboBox packedBinaryOutIntl_CB;
	protected String packedBinaryOutIntl, bitOutOffsetIntl, outputOffsetIntl;

	//Intl Perm
	protected JTextField lengthPermIntl_TF, offsetPermIntl_TF;
	protected String lengthPermIntl, offsetPermIntl;

	//Mapper Data In
	protected JTextField baseAddressDataInMapp_TF, numSamplesDataInMapp_TF, bitsPerSymbolDataInMapp_TF, bitOffsetFirstSymbolDataInMapp_TF;
	protected String baseAddressDataInMapp, numSamplesDataInMapp, bitsPerSymbolDataInMapp, bitOffsetFirstSymbolDataInMapp, symmetricalValueDataInMapp;
	protected JComboBox symmetricalValueDataInMapp_CB;
	//Mapper Data Out
	protected JTextField baseAddressDataOutMapp_TF, numSamplesDataOutMapp_TF;
	protected String baseAddressDataOutMapp, numSamplesDataOutMapp;
	//Mapper LUT
	protected JTextField numSamplesLUTMapp_TF, baseAddressLUTMapp_TF;
	protected String numSamplesLUTMapp, baseAddressLUTMapp;
	
  // Main Panel
  private JButton closeButton;
  private JButton cancelButton;

	//Code generation
	private JPanel panel3, panel4, panel5;
	private JTabbedPane tabbedPane;
	private int bufferType = 0;
	private boolean loadBufferParameters = false;
	private ArrayList<String> bufferParameters;
    
    /** Creates new form  */
    public JDialogPortArtifact(Frame _frame, String _title, TMLArchiPortArtifact _artifact, String _mappedMemory, ArrayList<String> _bufferParameters, String _mappedPort ) {
        super(_frame, _title, true);
        frame = _frame;
        artifact = _artifact;
				mappedMemory = _mappedMemory;
				bufferParameters = _bufferParameters;
				mappedPort = _mappedPort;
        
		TraceManager.addDev("init components");
		
        initComponents();
		
		TraceManager.addDev("my init components");
		
        myInitComponents();
		
		TraceManager.addDev("pack");
        pack();
    }
    
    private void myInitComponents() {
		selectPriority();
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
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
        panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: buffer attributes"));
        panel3.setPreferredSize(new Dimension(650, 350));
				
				tabbedPane = new JTabbedPane();
		  	panel4 = new JPanel();
  			panel5 = new JPanel();
        
		c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Port:"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		TraceManager.addDev("Getting communications");
		Vector<String> list = artifact.getTDiagramPanel().getMGUI().getAllTMLCommunicationNames();
		Vector<String> portsList = new Vector<String>();
		int index = 0;
		if (list.size() == 0) {
			list.add("No communication to map");
			emptyList = true;
		} else {
			
			index = 0;//indexOf(list, artifact.getFullValue());
			//parse each entry of list. Entry is in format AppName::chIn__chOut
			for( String s: list )	{
				String[] temp1 = s.split("__");
				String[] temp2 = temp1[0].split( "::" );
				String chOut = temp2[0] + "::" + temp1[1];
				String chIn = temp2[0] + "::" + temp2[1];
				if( !portsList.contains( chOut ) )	{
					portsList.add( chOut );
				}
				if( !portsList.contains( chIn ) )	{
					portsList.add( chIn );
				}
			}
		}
		
		TraceManager.addDev("Got communications");

		
    referenceCommunicationName = new JComboBox(portsList);
		if( mappedPort.equals( "VOID" ) || mappedPort.equals( "" ) )	{
			referenceCommunicationName.setSelectedIndex( 0 );
		}
		else	{
			referenceCommunicationName.setSelectedIndex( portsList.indexOf( mappedPort ) );
		}
		referenceCommunicationName.addActionListener(this);
		panel2.add(referenceCommunicationName, c1);
		
		list = new Vector<String>();
		for(int i=0; i<11; i++) {
			list.add(""+i);
		}
		priority = new JComboBox(list);
		priority.setSelectedIndex(artifact.getPriority());
		panel2.add( new JLabel( "Priority: "),  c2 );
		panel2.add(priority, c1);
		
		//Make the list of memories
		LinkedList componentList = artifact.getTDiagramPanel().getComponentList();
		Vector<String> memoryList = new Vector<String>();
		for( int k = 0; k < componentList.size(); k++ )	{
			if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
				memoryList.add( ( (TMLArchiMemoryNode) componentList.get(k) ).getName() );
			}
		}

		memoryCB = new JComboBox( memoryList );
		if( mappedMemory.equals( "VOID" ) || mappedMemory.equals( "" ) )	{
			memoryCB.setSelectedIndex( 0 );
		}
		else	{
			memoryCB.setSelectedIndex( memoryList.indexOf( mappedMemory ) );
		}
		panel2.add( new JLabel( "Memory: "),  c2 );
		memoryCB.addActionListener(this);
		panel2.add( memoryCB, c1 );

		if( bufferParameters.size() == 0 )	{
			bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
			loadBufferParameters = false;
		}
		else	{
			bufferType = Integer.parseInt( bufferParameters.get( Buffer.bufferTypeIndex ) );
			loadBufferParameters = true;
		}

		switch( bufferType )	{
			case Buffer.FepBuffer:	
				if( loadBufferParameters )	{
					baseAddress = bufferParameters.get( FepBuffer.baseAddressIndex );
					numSamples = bufferParameters.get( FepBuffer.numSamplesIndex );
					bank = bufferParameters.get( FepBuffer.bankIndex );
					dataType = bufferParameters.get( FepBuffer.dataTypeIndex );
				}
				makeFepBufferPanel( c1, c2 );
				break;
			case Buffer.InterleaverBuffer:	
				if( loadBufferParameters )	{
					//data in
					packedBinaryInIntl = bufferParameters.get( InterleaverBuffer.packedBinaryInIntlIndex );
					widthIntl = bufferParameters.get( InterleaverBuffer.widthIntlIndex );
					bitInOffsetIntl = bufferParameters.get( InterleaverBuffer.bitInOffsetIntlIndex );
					inputOffsetIntl = bufferParameters.get( InterleaverBuffer.inputOffsetIntlIndex );
					//data out
					packedBinaryOutIntl = bufferParameters.get( InterleaverBuffer.packedBinaryOutIntlIndex );
					bitOutOffsetIntl = bufferParameters.get( InterleaverBuffer.bitOutOffsetIntlIndex );
					outputOffsetIntl = bufferParameters.get( InterleaverBuffer.outputOffsetIntlIndex );
					//permutation table
					lengthPermIntl = bufferParameters.get( InterleaverBuffer.lengthPermIntlIndex );
					offsetPermIntl = bufferParameters.get( InterleaverBuffer.offsetPermIntlIndex );
				}
				makeInterleaverBufferPanel( c1, c2 );
				break;
			case Buffer.AdaifBuffer:	
				if( loadBufferParameters )	{
					baseAddress = bufferParameters.get( MMBuffer.baseAddressIndex );
					numSamples = bufferParameters.get( MMBuffer.numSamplesIndex );
				}
				makeAdaifBufferPanel( c1, c2 );
				break;
			case Buffer.MapperBuffer:	
				if( loadBufferParameters )	{
					baseAddress = bufferParameters.get( MapperBuffer.baseAddressIndex );
					numSamples = bufferParameters.get( MapperBuffer.numSamplesIndex );
					bitsPerSymbol = bufferParameters.get( MapperBuffer.bitsPerSymbolIndex );
					bitOffsetFirstSymbol = bufferParameters.get( MapperBuffer.bitOffsetFirstSymbolIndex );
					symmetricalValue = bufferParameters.get( MapperBuffer.symmetricalIndex );
				}
				makeMapperBufferPanel( c1, c2 );
				break;
			case Buffer.MainMemoryBuffer:	
				if( loadBufferParameters )	{
					baseAddress = bufferParameters.get( MMBuffer.baseAddressIndex );
				}
				makeMainMemoryBufferPanel( c1, c2 );
				break;
			default:	//the fep buffer 
				if( loadBufferParameters )	{
					baseAddress = bufferParameters.get( FepBuffer.baseAddressIndex );
					numSamples = bufferParameters.get( FepBuffer.numSamplesIndex );
					bank = bufferParameters.get( FepBuffer.bankIndex );
					dataType = bufferParameters.get( FepBuffer.dataTypeIndex );
				}
				makeFepBufferPanel( c1, c2 );
				break;
		}

		// main panel;
		c0.gridheight = 10;
		c0.weighty = 1.0;
		c0.weightx = 1.0;
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c0.fill = GridBagConstraints.BOTH;
		c.add( panel2, c0 );
		if( ( bufferType == Buffer.MainMemoryBuffer ) || ( bufferType == Buffer.FepBuffer ) || ( bufferType == Buffer.AdaifBuffer) )	{
      panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: buffer attributes"));
			tabbedPane.removeAll();
			tabbedPane.addTab( "Data", panel3 );
			tabbedPane.setSelectedIndex(0);
		}
		c.add( tabbedPane, c0 );

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

	private void makeFepBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{

     panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: buffer attributes"));

		c2.anchor = GridBagConstraints.LINE_START;
		numSamplesTF = new JTextField( sampleLength, 5 );
		panel3.add( new JLabel( "Number of samples = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		numSamplesTF = new JTextField( sampleLength, 5 );
		panel3.add( numSamplesTF, c1 );
		//
		baseAddressTF = new JTextField( baseAddress, 5 );
		panel3.add( new JLabel( "Base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		baseAddressTF = new JTextField( baseAddress, 5 );
		panel3.add( baseAddressTF, c1 );
		//
		bankCB = new JComboBox( new Vector<String>( Arrays.asList( FepBuffer.banksList ) ) );
		panel3.add( new JLabel( "Bank number = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		if( bank != null )	{
			bankCB.setSelectedIndex( Integer.parseInt( bank ) );
		}
		panel3.add( bankCB, c1 );
		//
		dataTypeCB = new JComboBox( new Vector<String>( Arrays.asList( FepBuffer.dataTypeList ) ) );
		panel3.add( new JLabel( "Data type = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		if( dataType != null )	{
			dataTypeCB.setSelectedItem( dataType );
		}
		panel3.add( dataTypeCB, c1 );
	}

	private void makeInterleaverBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{

		GridBagLayout gridbag2 = new GridBagLayout();

  	panel3 = new JPanel();	//data in
		panel3.setLayout(gridbag2);
		panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: input buffer attributes"));
		panel3.setPreferredSize(new Dimension(650, 350));

  	panel4 = new JPanel();	//data out
		panel4.setLayout(gridbag2);
		panel4.setBorder(new javax.swing.border.TitledBorder("Code generation: output buffer attributes"));
		panel4.setPreferredSize(new Dimension(650, 350));

  	panel5 = new JPanel();	//permutation table
		panel5.setLayout(gridbag2);
		panel5.setBorder(new javax.swing.border.TitledBorder("Code generation: Permutation Table attributes"));
		panel5.setPreferredSize(new Dimension(650, 350));
		
		//Data In panel
		c2.anchor = GridBagConstraints.LINE_START;
		packedBinaryInIntl_CB = new JComboBox( Buffer.onOffVector );
		panel3.add( new JLabel( "Packed binary input mode = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		if( packedBinaryInIntl != null )	{
			packedBinaryInIntl_CB.setSelectedItem( packedBinaryInIntl );
		}
		panel3.add( packedBinaryInIntl_CB, c1 );
		//
		widthIntl_TF = new JTextField( widthIntl, 5 );
		panel3.add( new JLabel( "Sample width = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( widthIntl_TF, c1 );
		//
		bitInOffsetIntl_TF = new JTextField( bitInOffsetIntl, 5 );
		panel3.add( new JLabel( "Bit input offset = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( bitInOffsetIntl_TF, c1 );
		//
		inputOffsetIntl_TF = new JTextField( inputOffsetIntl, 5 );
		panel3.add( new JLabel( "Offset of first input sample = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( inputOffsetIntl_TF, c1 );
		//

		//Data Out panel
		c2.anchor = GridBagConstraints.LINE_START;
		packedBinaryOutIntl_CB = new JComboBox( Buffer.onOffVector );
		panel4.add( new JLabel( "Packed binary output mode = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		if( packedBinaryOutIntl != null )	{
			packedBinaryOutIntl_CB.setSelectedItem( packedBinaryOutIntl );
		}
		panel4.add( packedBinaryOutIntl_CB, c1 );
		//
		bitOutOffsetIntl_TF = new JTextField( bitOutOffsetIntl, 5 );
		panel4.add( new JLabel( "Bit output offset = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel4.add( bitOutOffsetIntl_TF, c1 );
		//
		c2.anchor = GridBagConstraints.LINE_START;
		outputOffsetIntl_TF = new JTextField( outputOffsetIntl, 5 );
		panel4.add( new JLabel( "Offset of first output sample = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel4.add( outputOffsetIntl_TF, c1 );

		//Permutation Table panel
		c2.anchor = GridBagConstraints.LINE_START;
		offsetPermIntl_TF = new JTextField( offsetPermIntl, 5 );
		panel5.add( new JLabel( "Offset = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel5.add( offsetPermIntl_TF, c1 );
		//
		c2.anchor = GridBagConstraints.LINE_START;
		lengthPermIntl_TF = new JTextField( lengthPermIntl, 5 );
		panel5.add( new JLabel( "Length = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel5.add( lengthPermIntl_TF, c1 );

		tabbedPane.addTab( "Data In", panel3 );
		tabbedPane.addTab( "Data Out", panel4 );
		tabbedPane.addTab( "Permutation Table", panel5 );
		tabbedPane.setSelectedIndex(0);
	}

	private void makeAdaifBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{
		c2.anchor = GridBagConstraints.LINE_START;
		makeMainMemoryBufferPanel( c1, c2 );
	}

	private void makeMapperBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{

		GridBagLayout gridbag2 = new GridBagLayout();

  	panel3 = new JPanel();
		panel3.setLayout(gridbag2);
		panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: input buffer attributes"));
		panel3.setPreferredSize(new Dimension(650, 350));

  	panel4 = new JPanel();
		panel4.setLayout(gridbag2);
		panel4.setBorder(new javax.swing.border.TitledBorder("Code generation: output buffer attributes"));
		panel4.setPreferredSize(new Dimension(650, 350));

  	panel5 = new JPanel();
		panel5.setLayout(gridbag2);
		panel5.setBorder(new javax.swing.border.TitledBorder("Code generation: Look Up Table attributes"));
		panel5.setPreferredSize(new Dimension(650, 350));
		
		//Data In panel
		c2.anchor = GridBagConstraints.LINE_START;
		numSamplesDataInMapp_TF = new JTextField( numSamplesDataInMapp, 5 );
		panel3.add( new JLabel( "Number of symbols = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( numSamplesDataInMapp_TF, c1 );
		//
		baseAddressDataInMapp_TF = new JTextField( baseAddressDataInMapp, 5 );
		panel3.add( new JLabel( "Base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( baseAddressDataInMapp_TF, c1 );
		//
		bitsPerSymbolDataInMapp_TF = new JTextField( bitsPerSymbolDataInMapp, 5 );
		panel3.add( new JLabel( "Number of bits/symbol = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( bitsPerSymbolDataInMapp_TF, c1 );
		//
		bitOffsetFirstSymbolDataInMapp_TF = new JTextField( bitOffsetFirstSymbolDataInMapp, 5 );
		panel3.add( new JLabel( "Bit offset of first symbol = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( bitOffsetFirstSymbolDataInMapp_TF, c1 );
		//
		symmetricalValueDataInMapp_CB = new JComboBox( new Vector<String>( Arrays.asList( MapperBuffer.symmetricalValues ) ) );
		panel3.add( new JLabel( "Symmetrical value = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		if( symmetricalValueDataInMapp != null )	{
			symmetricalValueDataInMapp_CB.setSelectedItem( symmetricalValueDataInMapp );
		}
		panel3.add( symmetricalValueDataInMapp_CB, c1 );

		//Data Out panel
		c2.anchor = GridBagConstraints.LINE_START;
		numSamplesDataOutMapp_TF = new JTextField( numSamplesDataOutMapp, 5 );
		panel4.add( new JLabel( "Number of symbols = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel4.add( numSamplesDataOutMapp_TF, c1 );
		//
		baseAddressDataOutMapp_TF = new JTextField( baseAddressDataOutMapp, 5 );
		panel4.add( new JLabel( "Base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel4.add( baseAddressDataOutMapp_TF, c1 );
		//
		//Look Up Table panel
		c2.anchor = GridBagConstraints.LINE_START;
		numSamplesLUTMapp_TF = new JTextField( numSamplesLUTMapp, 5 );
		panel5.add( new JLabel( "Number of symbols = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel5.add( numSamplesLUTMapp_TF, c1 );
		//
		baseAddressLUTMapp_TF = new JTextField( baseAddressLUTMapp, 5 );
		panel5.add( new JLabel( "Base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel5.add( baseAddressLUTMapp_TF, c1 );
		//

		tabbedPane.addTab( "Data In", panel3 );
		tabbedPane.addTab( "Data Out", panel4 );
		tabbedPane.addTab( "Look Up Table", panel5 );
		tabbedPane.setSelectedIndex(0);
	}

	private void makeMainMemoryBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{
		
		panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: buffer attributes"));

		c2.anchor = GridBagConstraints.LINE_START;
		/*numSamplesTF = new JTextField( numSamples, 5 );
		panel3.add( new JLabel( "Number of samples = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		numSamplesTF = new JTextField( numSamples, 5 );
		panel3.add( numSamplesTF, c1 );*/
		//
		baseAddressTF = new JTextField( baseAddress, 5 );
		panel3.add( new JLabel( "Base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		baseAddressTF = new JTextField( baseAddress, 5 );
		panel3.add( baseAddressTF, c1 );
	}

		private int getBufferTypeFromSelectedMemory( String mappedMemory )	{
			
			LinkedList componentList = artifact.getTDiagramPanel().getComponentList();
			Vector<String> list = new Vector<String>();
			
			for( int k = 0; k < componentList.size(); k++ )	{
				if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
					TMLArchiMemoryNode memoryNode = (TMLArchiMemoryNode)componentList.get(k);
					TraceManager.addDev( "Comparing " + memoryNode.getName() + " with " + mappedMemory );
					if( memoryNode.getName().equals( mappedMemory ) )	{
						return memoryNode.getBufferType();
					}
				}
			}
			return 0;	//default: the main memory buffer
		}
    
    public void	actionPerformed(ActionEvent evt)  {
       /* if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
            initialValue.setEnabled(b);
            return;
        }*/
		
		if (evt.getSource() == referenceCommunicationName) {
			selectPriority();
		}
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
		
		flushBuffersStrings();
		bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );

		switch( bufferType )	{
			case Buffer.FepBuffer:	
				panel3.removeAll();
				tabbedPane.removeAll();
				makeFepBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				tabbedPane.addTab( "Data", panel3 );
				break;
			case Buffer.MapperBuffer:	
				cleanPanels();
				makeMapperBufferPanel( c1, c2 );
				revalidateAndRepaintPanels();
				tabbedPane.addTab( "Data In", panel3 );
				tabbedPane.addTab( "Data Out", panel4 );
				tabbedPane.addTab( "Look Up Table", panel5 );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.AdaifBuffer:	
				panel3.removeAll();
				tabbedPane.removeAll();
				makeAdaifBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				tabbedPane.addTab( "Data", panel3 );
				break;
			case Buffer.InterleaverBuffer:
				cleanPanels();
				makeInterleaverBufferPanel( c1, c2 );
				revalidateAndRepaintPanels();
				tabbedPane.addTab( "Data In", panel3 );
				tabbedPane.addTab( "Data Out", panel4 );
				tabbedPane.addTab( "Permutation Table", panel5 );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.MainMemoryBuffer:	
				panel3.removeAll();
				tabbedPane.removeAll();
				makeMainMemoryBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				tabbedPane.addTab( "Data", panel3 );
				break;
			default:	//the main memory buffer 
				panel3.removeAll();
				tabbedPane.removeAll();
				makeFepBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				tabbedPane.addTab( "Data", panel3 );
				break;
		}
	}

	private void flushBuffersStrings()	{
	
		baseAddress = "";
		mappedPort = "";
		sampleLength = "";
		numSamples = "";
		bitOffsetFirstSymbol = "";
		bitsPerSymbol = "";
		symmetricalValue = "";
	}
	
	
	public void selectPriority() {
		//System.out.println("Select priority");
		int index = ((TMLArchiDiagramPanel)artifact.getTDiagramPanel()).getMaxPriority((String)(referenceCommunicationName.getSelectedItem()));
		priority.setSelectedIndex(index);
	}
    
    public void closeDialog() {

        regularClose = true;
				mappedMemory = (String) memoryCB.getItemAt( memoryCB.getSelectedIndex() );
				bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
				switch ( bufferType )	{
					case Buffer.FepBuffer:	
						if( !handleClosureWhenSelectedFepBuffer() )	{
							return;
						}
						break;
					case Buffer.MapperBuffer:	
						if( !handleClosureWhenSelectedMapperBuffer() )	{
							return;
						}
						break;
					case Buffer.AdaifBuffer:	
						if( !handleClosureWhenSelectedAdaifBuffer() )	{
							return;
						}
						break;
					case Buffer.InterleaverBuffer:	
						if( !handleClosureWhenSelectedInterleaverBuffer() )	{
							return;
						}
						break;
					case Buffer.MainMemoryBuffer:	
						if( !handleClosureWhenSelectedMainMemoryBuffer() )	{
							return;
						}
						break;
					default:	//the main memory buffer 
						if( !handleClosureWhenSelectedFepBuffer() )	{
							return;
						}
						break;
				}
        dispose();
    }

		private boolean handleClosureWhenSelectedFepBuffer()	{

			return checkBaseAddress() && checkNumSamples();
		}

		private boolean handleClosureWhenSelectedInterleaverBuffer()	{

			// need to add the code to check for all 3 tabs
			return checkDI_Intl() && checkDO_Intl() && CheckPerm_Intl();
		}

		private boolean handleClosureWhenSelectedAdaifBuffer()	{

			return checkBaseAddress() && checkNumSamples();
		}

		private boolean handleClosureWhenSelectedMapperBuffer()	{
			
			// need to add the code to check for all 3 tabs
			symmetricalValue = (String)symmetricalValueCB.getSelectedItem();
			return checkBaseAddress() && checkNumSamples() && checkNumBitsPerSymbol() && checkSymbolBaseAddress();
		}

		private boolean handleClosureWhenSelectedMainMemoryBuffer()	{

			return checkBaseAddress();
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
		if (emptyList) {
			return null;
		}
		String tmp = (String)(referenceCommunicationName.getSelectedItem());
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        return tmp.substring(0, index);
    }
    
    public String getCommunicationName() {
        String tmp = (String)(referenceCommunicationName.getSelectedItem());
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        tmp = tmp.substring(index+2, tmp.length());
		
		index =  tmp.indexOf("(");
		if (index > -1) {
			tmp = tmp.substring(0, index).trim();
		}
		//System.out.println("tmp=" + tmp);
		return tmp;
    }
	
	 public String getTypeName() {
		String tmp = (String)(referenceCommunicationName.getSelectedItem());
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
	
	public int getPriority() {
		return priority.getSelectedIndex();
	}

	private boolean checkBaseAddress()	{

		baseAddress = (String) baseAddressTF.getText();
		if( baseAddress.length() <= 2 && baseAddress.length() > 0 )	{
			JOptionPane.showMessageDialog( frame, "Please enter a valid base address", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( baseAddress.length() > 2 )	{
			if( !( baseAddress.substring(0,2).equals("0x") || baseAddress.substring(0,2).equals("0X") ) )	{
				JOptionPane.showMessageDialog( frame, "Base address must be expressed in hexadecimal", "Badly formatted parameter",
																				JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
	return true;
	}
			
	private boolean checkDI_Intl()	{

	
		String regex = "[0-9]+";
		widthIntl = (String)widthIntl_TF.getText();
		bitInOffsetIntl = (String)bitInOffsetIntl_TF.getText();
		inputOffsetIntl = (String)inputOffsetIntl_TF.getText();
		packedBinaryInIntl = (String)packedBinaryInIntl_CB.getSelectedItem();

		if( !( widthIntl.length() > 0 ) )	{
			return true;
		}
		if( !widthIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The samples widthmust be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( !( bitInOffsetIntl.length() > 0 ) )	{
			return true;
		}
		if( !bitInOffsetIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The bit input offset must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( !( inputOffsetIntl.length() > 0 ) )	{
			return true;
		}
		if( !inputOffsetIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The bit intput offset must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}
	
	private boolean checkDO_Intl()	{

		//check bitOutOffset and outOffset
		packedBinaryOutIntl = (String)packedBinaryOutIntl_CB.getSelectedItem();
		bitOutOffsetIntl = 	(String)bitOutOffsetIntl_TF.getText();
		outputOffsetIntl = (String)outputOffsetIntl_TF.getText();
		String regex = "[0-9]+";

		if( !( bitOutOffsetIntl.length() > 0 ) )	{
			return true;
		}
		if( !bitOutOffsetIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The bit output offset must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		// check output offset
		if( !( outputOffsetIntl.length() > 0 ) )	{
			return true;
		}
		if( !outputOffsetIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The output offset must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}
	
	private boolean CheckPerm_Intl()	{

		String regex = "[0-9]+";
		offsetPermIntl = (String) offsetPermIntl_TF.getText();
		lengthPermIntl = (String) lengthPermIntl_TF.getText();
		//check first entry offset
		if( !( offsetPermIntl.length() > 0 ) )	{
			return true;
		}
		if( !offsetPermIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The offset must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		//check permutation table length
		if( !( lengthPermIntl.length() > 0 ) )	{
			return true;
		}
		if( !lengthPermIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The length must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( Integer.parseInt( lengthPermIntl ) == 0 )	{
			JOptionPane.showMessageDialog( frame, "The length must be greater than 0", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}

	/*private boolean checkNumSamples_Intl()	{

		String regex = "[0-9]+";
		numSamplesDataInIntl = (String) numSamplesDataInIntl_TF.getText();
		numSamplesDataOutIntl = (String) numSamplesDataOutIntl_TF.getText();

		if( !( numSamplesDataInIntl.length() > 0 ) )	{
			return true;
		}
		if( !numSamplesDataInIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The Data In number of samples must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( Integer.parseInt( numSamplesDataInIntl ) == 0 )	{
			JOptionPane.showMessageDialog( frame, "The Data In number of samples must be greater than 0", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( !( numSamplesDataOutIntl.length() > 0 ) )	{
			return true;
		}
		if( !numSamplesDataOutIntl.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The Data Out number of samples must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( Integer.parseInt( numSamplesDataOutIntl ) == 0 )	{
			JOptionPane.showMessageDialog( frame, "The Data Out number of samples must be greater than 0", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}*/

	private boolean checkNumSamples()	{

		String regex = "[0-9]+";
		numSamples = (String) numSamplesTF.getText();
		if( !( numSamples.length() > 0 ) )	{
			return true;
		}
		if( !numSamples.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The number of samples must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( Integer.parseInt( numSamples ) == 0 )	{
			JOptionPane.showMessageDialog( frame, "The number of samples must be greater than 0", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}


	private boolean checkNumBitsPerSymbol()	{

		String regex = "[0-9]+";
		bitsPerSymbol = (String) bitsPerSymbolTF.getText();
		if( !( bitsPerSymbol.length() > 0 ) )	{
			return true;
		}
		if( Integer.parseInt( bitsPerSymbol ) == 0 )	{
			JOptionPane.showMessageDialog( frame, "The number of bits/samples must be greater than 0", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( !bitsPerSymbol.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The number of bits/samples must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}
	
	private boolean checkSymbolBaseAddress()	{

		bitOffsetFirstSymbol = (String) bitOffsetFirstSymbolTF.getText();
		if( bitOffsetFirstSymbol.length() <= 2 && bitOffsetFirstSymbol.length() > 0 )	{
			JOptionPane.showMessageDialog( frame, "Please enter a valid symbol base address", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( bitOffsetFirstSymbol.length() > 2 )	{
			if( !( bitOffsetFirstSymbol.substring(0,2).equals("0x") || bitOffsetFirstSymbol.substring(0,2).equals("0X") ) )	{
				JOptionPane.showMessageDialog( frame, "Bit offset of first symbol must be expressed in hexadecimal", "Badly formatted parameter",
																				JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
	return true;
	}

	public ArrayList<String> getBufferParameters()	{

		ArrayList<String> params = new ArrayList<String>();
		params.add( String.valueOf( bufferType ) );
		switch( bufferType )	{
			case Buffer.FepBuffer:
				params.add( baseAddress );
				params.add( numSamples );
				params.add( (String)bankCB.getSelectedItem() );
				params.add( (String)dataTypeCB.getSelectedItem() );
				break;
			case Buffer.InterleaverBuffer:	
				//data in
				params.add( InterleaverBuffer.packedBinaryInIntlIndex, packedBinaryInIntl );
				params.add( InterleaverBuffer.widthIntlIndex, widthIntl );
				params.add( InterleaverBuffer.bitInOffsetIntlIndex, bitInOffsetIntl );
				params.add( InterleaverBuffer.inputOffsetIntlIndex, inputOffsetIntl );
				//data out
				params.add( InterleaverBuffer.packedBinaryOutIntlIndex, packedBinaryOutIntl );
				params.add( InterleaverBuffer.bitOutOffsetIntlIndex, bitOutOffsetIntl );
				params.add( InterleaverBuffer.outputOffsetIntlIndex, outputOffsetIntl );
				//permutation table
				params.add( InterleaverBuffer.offsetPermIntlIndex, offsetPermIntl );
				params.add( InterleaverBuffer.lengthPermIntlIndex, lengthPermIntl );
				break;
			case Buffer.AdaifBuffer:
				params.add( baseAddress );
				params.add( numSamples );
				break;
			case Buffer.MapperBuffer:	
				params.add( baseAddress );
				params.add( numSamples );
				params.add( bitsPerSymbol );
				params.add( bitOffsetFirstSymbol );
				params.add( symmetricalValue );
				break;
			case Buffer.MainMemoryBuffer:	
				params.add( baseAddress );
				break;
			default:	//the main memory buffer 
				params.add( baseAddress );
				params.add( numSamples );
				params.add( (String)bankCB.getSelectedItem() );
				params.add( (String)dataTypeCB.getSelectedItem() );
				break;
		}
		return params;
	}

	private void cleanPanels()	{
		panel3.removeAll();
		panel4.removeAll();
		panel5.removeAll();
		tabbedPane.removeAll();
	}

	private void revalidateAndRepaintPanels()	{
		panel3.revalidate();
		panel3.repaint();
		panel4.revalidate();
		panel4.repaint();
		panel5.revalidate();
		panel5.repaint();
		/*tabbedPaned.revalidate();
		tabbePaned.repaint();*/
	}

}	//End of class
