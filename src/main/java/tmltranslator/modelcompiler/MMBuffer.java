/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
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

package tmltranslator.modelcompiler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import tmltranslator.TMLTask;

/**
   * Class BaseBuffer
   * Creation: 11/02/2014
   * @version 1.0 11/02/2014
   * @author Andrea ENRICI
 */
public class MMBuffer extends Buffer	{

//	public static final int NUM_SAMPLES_INDEX = 1;
//	public static final int BASE_ADDRESS_INDEX = 2;

	protected String numSamplesValue = DEFAULT_NUM_VAL + USER_TO_DO;
	protected static final String NUM_SAMPLES_TYPE = "uint8_t";
	
	protected String baseAddressValue = DEFAULT_NUM_VAL + USER_TO_DO;
	protected static final String BASE_ADDRESS_TYPE = "uint32_t*";
	
	public static final String DECLARATION = "struct MM_BUFFER_TYPE {" + CR + TAB +
																						NUM_SAMPLES_TYPE + SP + "num_samples" + SC + CR + TAB +
																						BASE_ADDRESS_TYPE + SP + "base_address" + SC + CR + "}" + SC + CR2 +
																						"typedef MM_BUFFER_TYPE MM_BUFFER_TYPE" + SC + CR;
	
	private String Context = "embb_mainmemory_context";

	private static final int MAX_PARAMETERS = 2;
//	private static List<String> bufferParams = new ArrayList<String>();	//the DS that collects all the above params
//	private static JTextField numSamplesTF = new JTextField( "", 5 );
//	private static JTextField baseAddressTF = new JTextField( "", 5 );

	public MMBuffer( String _name, TMLTask _task )	{
		type = "MM_BUFFER_TYPE";
		name = _name;
		task = _task;
	}

	@Override public String getInitCode()	{
		StringBuffer s = new StringBuffer();
		if( bufferParameters != null )	{
			retrieveBufferParameters();
		}
		s.append( TAB + name + ".num_samples = " + "(" + NUM_SAMPLES_TYPE + ")" + numSamplesValue + SC + CR );
		s.append( TAB + name + ".base_address = " + "(" + BASE_ADDRESS_TYPE + ")" + baseAddressValue + SC + CR );
		return s.toString();
	}

	public String toString()	{

		StringBuffer s = new StringBuffer( super.toString() );
		s.append( TAB2 + "num_samples = " + numSamplesValue + SC + CR );
		s.append( TAB2 + "base_address = " + baseAddressValue + SC + CR );
		return s.toString();
	}	

	private void retrieveBufferParameters()	{

		if( bufferParameters.get( NUM_SAMPLES_INDEX ).length() > 0 )	{
			numSamplesValue = bufferParameters.get( NUM_SAMPLES_INDEX );
		}
		if( bufferParameters.get( BASE_ADDRESS_INDEX ).length() > 0 )	{
			baseAddressValue = bufferParameters.get( BASE_ADDRESS_INDEX );
		}
	}

	public String getContext()	{
		return Context;
	}

	public static List<String> buildBufferParameters( Element elt )	{

		List<String> buffer = new ArrayList<String>();
		buffer.add( 0, Integer.toString( Buffer.MAIN_MEMORY_BUFFER ) );
		buffer.add( NUM_SAMPLES_INDEX, elt.getAttribute( "numSamples" ) );
		buffer.add( BASE_ADDRESS_INDEX, elt.getAttribute( "baseAddress" ) );
		return buffer;
	}

	public static String appendBufferParameters( java.util.List<String> buffer )	{

		StringBuffer sb = new StringBuffer();
		sb.append("\" bufferType=\"" + Integer.toString( Buffer.MAIN_MEMORY_BUFFER ) );
		if( buffer.size() == MAX_PARAMETERS+1 )	{	//because the first parameter is the bufferType
			sb.append("\" numSamples=\"" + buffer.get( NUM_SAMPLES_INDEX ) );
  	  sb.append("\" baseAddress=\"" + buffer.get( BASE_ADDRESS_INDEX ) );
		}
		else	{
			sb.append("\" numSamples=\"\"" + SP );
  	  sb.append( "baseAddress=\"" );
		}
		return sb.toString();
	}

	// Issue #98: Not used anymore
//	public static ArrayList<JPanel> makePanel( GridBagConstraints c1, GridBagConstraints c2 )	{
//
//		String baseAddress = "", numSamples = "";
//		GridBagLayout gridbag2 = new GridBagLayout();
//
//		JPanel panel = new JPanel();
//		panel.setLayout( gridbag2 );
//		panel.setBorder( new javax.swing.border.TitledBorder("Code generation: memory configuration"));
//		panel.setPreferredSize( new Dimension(650, 350) );
//
//		c2.anchor = GridBagConstraints.LINE_START;
//		numSamplesTF.setText( numSamples );
//		panel.add( new JLabel( "Number of samples = "),  c2 );
//		c1.gridwidth = GridBagConstraints.REMAINDER;
//		panel.add( numSamplesTF, c1 );
//		//
//		baseAddressTF.setText( baseAddress );
//		panel.add( new JLabel( "Base address = "),  c2 );
//		c1.gridwidth = GridBagConstraints.REMAINDER;
//		panel.add( baseAddressTF, c1 );
//		
//		ArrayList<JPanel> panelsList = new ArrayList<JPanel>();
//		panelsList.add( panel );
//
//		fillBufferParameters();	//to avoid an empty buffer of parameters if user closes the window without saving
//		return panelsList;
//	}
//
//	public static boolean closePanel( Frame frame )	{
//
//		String regex = "[0-9]+";
//		String baseAddress = baseAddressTF.getText();
//		String numSamples = numSamplesTF.getText();
//
//		if( baseAddress.length() <= 2 && baseAddress.length() > 0 )	{
//			JOptionPane.showMessageDialog( frame, "Please enter a valid base address", "Badly formatted parameter",
//																			JOptionPane.INFORMATION_MESSAGE );
//			return false;
//		}
//		if( baseAddress.length() > 2 )	{
//			if( !( baseAddress.substring(0,2).equals("0x") || baseAddress.substring(0,2).equals("0X") ) )	{
//				JOptionPane.showMessageDialog( frame, "Base address must be expressed in hexadecimal", "Badly formatted parameter",
//																				JOptionPane.INFORMATION_MESSAGE );
//				return false;
//			}
//		}
//		if( ( numSamples.length() > 0 ) && !numSamples.matches( regex ) )	{
//			JOptionPane.showMessageDialog( frame, "The number of samples must be expressed as a natural", "Badly formatted parameter",
//																			JOptionPane.INFORMATION_MESSAGE );
//			return false;
//		}
//		if( Integer.parseInt( numSamples ) == 0 )	{
//			JOptionPane.showMessageDialog( frame, "The number of samples must be greater than 0", "Badly formatted parameter",
//																			JOptionPane.INFORMATION_MESSAGE );
//			return false;
//		}
//
//		fillBufferParameters();
//		return true;
//	}
//
//	private static void fillBufferParameters()	{
//
//		if( bufferParams.size() > 0 ) 	{
//			bufferParams.set( BUFFER_TYPE_INDEX, String.valueOf( Buffer.MAIN_MEMORY_BUFFER ) );
//			bufferParams.set( NUM_SAMPLES_INDEX, numSamplesTF.getText() );
//			bufferParams.set( BASE_ADDRESS_INDEX, baseAddressTF.getText() );
//		}
//		else	{
//			bufferParams.add( BUFFER_TYPE_INDEX, String.valueOf( Buffer.MAIN_MEMORY_BUFFER ) );
//			bufferParams.add( NUM_SAMPLES_INDEX, numSamplesTF.getText() );
//			bufferParams.add( BASE_ADDRESS_INDEX, baseAddressTF.getText() );
//		}
//	}
//
//	public static ArrayList<String> getBufferParameters()	{
//		return bufferParams;
//	}

}	//End of class
