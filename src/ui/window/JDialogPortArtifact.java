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
	protected JTextField baseAddressTF, endAddressTF, numSamplesTF, symbolBaseAddressTF, bitsPerSymbolTF;
	protected String baseAddress, endAddress, mappedPort, sampleLength, bank, dataType, numSamples, symbolBaseAddress, bitsPerSymbol;
	protected JComboBox dataTypeCB, bankCB;
	//protected Vector<String> dataTypeList = new Vector<String>();
	
  // Main Panel
  private JButton closeButton;
  private JButton cancelButton;

	//Code generation
	private JPanel panel3;
	private int bufferType;
    
    /** Creates new form  */
    public JDialogPortArtifact(Frame _frame, String _title, TMLArchiPortArtifact _artifact, String _mappedMemory, String _baseAddress, String _endAddress, String _mappedPort ) {
        super(_frame, _title, true);
        frame = _frame;
        artifact = _artifact;
				mappedMemory = _mappedMemory;
				baseAddress = _baseAddress;
				endAddress = _endAddress;
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
        panel3.setBorder(new javax.swing.border.TitledBorder("Buffer attributes"));
        panel3.setPreferredSize(new Dimension(650, 350));
        
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

		bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );

		switch( bufferType )	{
			case TMLArchiMemoryNode.FepBuffer:	
				makeFepBufferPanel( c1, c2 );
				break;
			case TMLArchiMemoryNode.MapperBuffer:	
				makeMapperBufferPanel( c1, c2 );
				break;
			case TMLArchiMemoryNode.AdaifBuffer:	
				makeAdaifBufferPanel( c1, c2 );
				break;
			case TMLArchiMemoryNode.InterleaverBuffer:	
				makeInterleaverBufferPanel( c1, c2 );
				break;
			case TMLArchiMemoryNode.MainMemoryBuffer:	
				makeMainMemoryBufferPanel( c1, c2 );
				break;
			default:	//the main memory buffer 
				makeMapperBufferPanel( c1, c2 );
				break;
		}

		// main panel;
		c0.gridheight = 10;
		c0.weighty = 1.0;
		c0.weightx = 1.0;
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c0.fill = GridBagConstraints.BOTH;
		c.add( panel2, c0 );
		c.add( panel3, c0 );

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

		//FepBuffer FepBuffer = new FepBuffer( "noName", null );

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
		panel3.add( bankCB, c1 );
		//
		dataTypeCB = new JComboBox( new Vector<String>( Arrays.asList( FepBuffer.dataTypeList ) ) );
		panel3.add( new JLabel( "Data type = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		panel3.add( dataTypeCB, c1 );
	}

	private void makeMapperBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{
		c2.anchor = GridBagConstraints.LINE_START;
		makeMainMemoryBufferPanel( c1, c2 );
	}

	private void makeAdaifBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{
		c2.anchor = GridBagConstraints.LINE_START;
		makeMainMemoryBufferPanel( c1, c2 );
	}

	private void makeInterleaverBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{

		c2.anchor = GridBagConstraints.LINE_START;
		numSamplesTF = new JTextField( numSamples, 5 );
		panel3.add( new JLabel( "Number of samples = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		numSamplesTF = new JTextField( numSamples, 5 );
		panel3.add( numSamplesTF, c1 );
		//
		baseAddressTF = new JTextField( baseAddress, 5 );
		panel3.add( new JLabel( "Base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		baseAddressTF = new JTextField( baseAddress, 5 );
		panel3.add( baseAddressTF, c1 );
		//
		bitsPerSymbolTF = new JTextField( bitsPerSymbol, 5 );
		panel3.add( new JLabel( "Number of bits/symbol = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		bitsPerSymbolTF = new JTextField( bitsPerSymbol, 5 );
		panel3.add( bitsPerSymbolTF, c1 );
		//
		symbolBaseAddressTF = new JTextField( symbolBaseAddress, 5 );
		panel3.add( new JLabel( "Symbol base address = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		symbolBaseAddressTF = new JTextField( symbolBaseAddress, 5 );
		panel3.add( symbolBaseAddressTF, c1 );
	}

	private void makeMainMemoryBufferPanel( GridBagConstraints c1, GridBagConstraints c2 )	{
		
		c2.anchor = GridBagConstraints.LINE_START;
		numSamplesTF = new JTextField( numSamples, 5 );
		panel3.add( new JLabel( "Number of samples = "),  c2 );
		c1.gridwidth = GridBagConstraints.REMAINDER;
		numSamplesTF = new JTextField( numSamples, 5 );
		panel3.add( numSamplesTF, c1 );
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
			case TMLArchiMemoryNode.FepBuffer:	
				panel3.removeAll();
				makeFepBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				break;
			case TMLArchiMemoryNode.MapperBuffer:	
				panel3.removeAll();
				makeMapperBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				break;
			case TMLArchiMemoryNode.AdaifBuffer:	
				panel3.removeAll();
				makeAdaifBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				break;
			case TMLArchiMemoryNode.InterleaverBuffer:	
				panel3.removeAll();
				makeInterleaverBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				break;
			case TMLArchiMemoryNode.MainMemoryBuffer:	
				panel3.removeAll();
				makeMainMemoryBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				break;
			default:	//the main memory buffer 
				panel3.removeAll();
				makeFepBufferPanel( c1, c2 );
				panel3.revalidate();
				panel3.repaint();
				break;
		}
	}

	private void flushBuffersStrings()	{
	
		baseAddress = "";
		endAddress = "";
		mappedPort = "";
		sampleLength = "";
		bank = "";
		dataType = "";
		numSamples = "";
		symbolBaseAddress = "";
		bitsPerSymbol = "";
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
					case TMLArchiMemoryNode.FepBuffer:	
						if( !handleClosureWhenSelectedFepBuffer() )	{
							return;
						}
						break;
					case TMLArchiMemoryNode.MapperBuffer:	
						if( !handleClosureWhenSelectedMapperBuffer() )	{
							return;
						}
						break;
					case TMLArchiMemoryNode.AdaifBuffer:	
						if( !handleClosureWhenSelectedAdaifBuffer() )	{
							return;
						}
						break;
					case TMLArchiMemoryNode.InterleaverBuffer:	
						if( !handleClosureWhenSelectedInterleaverBuffer() )	{
							return;
						}
						break;
					case TMLArchiMemoryNode.MainMemoryBuffer:	
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

		private boolean handleClosureWhenSelectedMapperBuffer()	{

			return checkBaseAddress() && checkNumSamples();
		}

		private boolean handleClosureWhenSelectedAdaifBuffer()	{

			return checkBaseAddress() && checkNumSamples();
		}

		private boolean handleClosureWhenSelectedInterleaverBuffer()	{

			return checkBaseAddress() && checkNumSamples() && checkNumBitsPerSymbol() && checkSymbolBaseAddress();
		}

		private boolean handleClosureWhenSelectedMainMemoryBuffer()	{

			return checkBaseAddress() && checkNumSamples();
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

		public String getEndAddress()	{
			return endAddress;
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

	private boolean checkEndAddress()	{

		endAddress = (String) endAddressTF.getText();
		if( endAddress.length() <= 2 && endAddress.length() > 0 )	{
			JOptionPane.showMessageDialog( frame, "Please enter a valid end address", "Badly formatted parameter",
																				JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( endAddress.length() > 2 )	{
			if( !( endAddress.substring(0,2).equals("0x") || endAddress.substring(0,2).equals("0X") ) )	{
				JOptionPane.showMessageDialog( frame, "End address must be expressed in hexadecimal", "Badly formatted parameter",
																				JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
	return true;
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
    
	private boolean checkNumSamples()	{

		String regex = "[0-9]+";
		numSamples = (String) numSamplesTF.getText();
		if( Integer.parseInt( numSamples ) == 0 )	{
			JOptionPane.showMessageDialog( frame, "The number of samples must be greater than 0", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( !numSamples.matches( regex ) )	{
			JOptionPane.showMessageDialog( frame, "The number of samples must be expressed as a natural", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		return true;
	}

	private boolean checkNumBitsPerSymbol()	{

		String regex = "[0-9]+";
		bitsPerSymbol = (String) bitsPerSymbolTF.getText();
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

		symbolBaseAddress = (String) symbolBaseAddressTF.getText();
		if( symbolBaseAddress.length() <= 2 && symbolBaseAddress.length() > 0 )	{
			JOptionPane.showMessageDialog( frame, "Please enter a valid symbol base address", "Badly formatted parameter",
																			JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		if( symbolBaseAddress.length() > 2 )	{
			if( !( symbolBaseAddress.substring(0,2).equals("0x") || symbolBaseAddress.substring(0,2).equals("0X") ) )	{
				JOptionPane.showMessageDialog( frame, "Symbol base address must be expressed in hexadecimal", "Badly formatted parameter",
																				JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
	return true;
	}

}	//End of class
