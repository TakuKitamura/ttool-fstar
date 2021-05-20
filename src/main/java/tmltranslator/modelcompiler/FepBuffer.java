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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

import tmltranslator.TMLTask;

/**
 * Class FepBuffer Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class FepBuffer extends Buffer {

  public static final String[] DATA_TYPES = { "int8", "int16", "cpx16", "cpx32" };
  public static final String[] BANKS = { "0", "1", "2", "3" };
  // public static final int NUM_SAMPLES_INDEX = 1;
  // public static final int BASE_ADDRESS_INDEX = 2;
  public static final int BANK_INDEX = 3;
  public static final int DATA_TYPE_INDEX = 4;

  protected static final String NUM_SAMPLES_TYPE = "uint8_t";

  protected static final String BASE_ADDRESS_TYPE = "uint64_t";

  protected static final String BANK_TYPE = "uint8_t";

  protected static final String DATATYPE_TYPE = "uint8_t";

  private static final int maxParameters = 4;
  // private static ArrayList<String> bufferParams = new ArrayList<String>();
  // //the DS that collects all the above params

  public static final String DECLARATION = "struct FEP_BUFFER_TYPE {" + CR + TAB + NUM_SAMPLES_TYPE + SP + "num_samples"
      + SC + CR + TAB + BASE_ADDRESS_TYPE + SP + "base_address" + SC + CR + TAB + BANK_TYPE + SP + "bank" + SC + CR
      + TAB + DATATYPE_TYPE + SP + "data_type" + SC + CR + "}" + SC + CR2 + "typedef FEP_BUFFER_TYPE FEP_BUFFER_TYPE"
      + SC;

  protected String baseAddressValue = DEFAULT_NUM_VAL + USER_TO_DO;
  protected String numSamplesValue = DEFAULT_NUM_VAL + USER_TO_DO;
  protected String bankValue = DEFAULT_NUM_VAL + USER_TO_DO;
  protected String dataTypeValue = DEFAULT_NUM_VAL + USER_TO_DO;

  // private String Context = "FEP_CONTEXT";

  // private static JTextField numSamplesTF = new JTextField( "", 5 );
  // private static JTextField baseAddressTF = new JTextField( "", 5 );
  // private static JComboBox<String> bankCB = new JComboBox<String>( new
  // Vector<String>( Arrays.asList( banksList ) ) );
  // private static JComboBox<String> dataTypeCB = new JComboBox<String>( new
  // Vector<String>( Arrays.asList( dataTypeList ) ) );

  public FepBuffer(String _name, TMLTask _task) {
    type = "FEP_BUFFER_TYPE";
    name = _name;
    task = _task;
  }

  @Override
  public String getInitCode() {

    StringBuffer s = new StringBuffer();
    if (bufferParameters != null) {
      retrieveBufferParameters();
    }
    s.append(TAB + name + ".num_samples = " + "(" + NUM_SAMPLES_TYPE + ")" + numSamplesValue + SC + CR);
    s.append(TAB + name + ".base_address = " + "(" + BASE_ADDRESS_TYPE + ")" + baseAddressValue + SC + CR);
    s.append(TAB + name + ".bank = " + "(" + BANK_TYPE + ")" + bankValue + SC + CR);
    s.append(TAB + name + ".data_type = " + "(" + DATATYPE_TYPE + ")" + dataTypeValue + SC + CR);
    return s.toString();
  }

  public String toString() {

    StringBuffer s = new StringBuffer(super.toString());
    s.append(TAB2 + "num_samples = " + numSamplesValue + SC + CR);
    s.append(TAB2 + "base_address = " + baseAddressValue + SC + CR);
    s.append(TAB2 + "bank = " + bankValue + SC + CR);
    s.append(TAB2 + "data_type = " + dataTypeValue + SC + CR);
    return s.toString();
  }

  private void retrieveBufferParameters() {

    if (bufferParameters.size() == maxParameters) {
      if (bufferParameters.get(NUM_SAMPLES_INDEX).length() > 0) {
        numSamplesValue = bufferParameters.get(NUM_SAMPLES_INDEX);
      }
      if (bufferParameters.get(BASE_ADDRESS_INDEX).length() > 0) {
        baseAddressValue = bufferParameters.get(BASE_ADDRESS_INDEX);
      }
      if (bufferParameters.get(BANK_INDEX).length() > 0) {
        bankValue = bufferParameters.get(BANK_INDEX);
      }
      if (bufferParameters.get(DATA_TYPE_INDEX).length() > 0) {
        dataTypeValue = String
            .valueOf((new Vector<String>(Arrays.asList(DATA_TYPES))).indexOf(bufferParameters.get(DATA_TYPE_INDEX)));
      }
    }
  }
  //
  // public String getContext() {
  // return Context;
  // }

  public static String appendBufferParameters(java.util.List<String> buffer) {

    StringBuffer sb = new StringBuffer();
    sb.append("\" bufferType=\"" + Integer.toString(Buffer.FEP_BUFFER));
    if (buffer.size() == maxParameters + 1) { // because the first parameter is the bufferType
      sb.append("\" baseAddress=\"" + buffer.get(BASE_ADDRESS_INDEX));
      sb.append("\" numSamples=\"" + buffer.get(NUM_SAMPLES_INDEX));
      sb.append("\" bank=\"" + buffer.get(BANK_INDEX));
      sb.append("\" dataType=\"" + buffer.get(DATA_TYPE_INDEX));
    } else {
      sb.append("\" baseAddress=\"" + SP);
      sb.append("numSamples=\"\"" + SP);
      sb.append("bank=\"\"" + SP);
      sb.append("dataType=\"");
    }
    return sb.toString();
  }

  public static List<String> buildBufferParameters(Element elt) {

    List<String> buffer = new ArrayList<String>();
    buffer.add(0, Integer.toString(Buffer.FEP_BUFFER));
    buffer.add(NUM_SAMPLES_INDEX, elt.getAttribute("numSamples"));
    buffer.add(BASE_ADDRESS_INDEX, elt.getAttribute("baseAddress"));
    buffer.add(BANK_INDEX, elt.getAttribute("bank"));
    buffer.add(DATA_TYPE_INDEX, elt.getAttribute("dataType"));

    return buffer;
  }
  //
  // public static ArrayList<JPanel> makePanel( GridBagConstraints c1,
  // GridBagConstraints c2 ) {
  //
  // String baseAddress = "", numSamples = "";//, bank = "", dataType = "";
  // GridBagLayout gridbag2 = new GridBagLayout();
  //
  //
  // JPanel panel = new JPanel();
  // panel.setLayout( gridbag2 );
  // panel.setBorder( new javax.swing.border.TitledBorder("Code generation: memory
  // configuration"));
  // panel.setPreferredSize( new Dimension(650, 350) );
  //
  // panel.setBorder(new javax.swing.border.TitledBorder("Code generation: memory
  // configuration"));
  //
  // c2.anchor = GridBagConstraints.LINE_START;
  // numSamplesTF.setText( numSamples );
  // panel.add( new JLabel( "Number of samples = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel.add( numSamplesTF, c1 );
  // //
  // baseAddressTF.setText( baseAddress );
  // panel.add( new JLabel( "Base address = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel.add( baseAddressTF, c1 );
  // //
  // panel.add( new JLabel( "Bank number = "), c2 );
  // /*if( bank != null && !bank.equals("") ) {
  // bankCB.setSelectedIndex( Integer.parseInt( bank ) );
  // }
  // else {
  // bankCB.setSelectedIndex(0);
  // }*/
  // panel.add( bankCB, c1 );
  // //
  // panel.add( new JLabel( "Data type = "), c2 );
  // /*if( dataType != null && !dataType.equals("") ) {
  // dataTypeCB.setSelectedItem( dataType );
  // }
  // else {
  // dataTypeCB.setSelectedIndex(0);
  // }*/
  // panel.add( dataTypeCB, c1 );
  //
  // ArrayList<JPanel> panelsList = new ArrayList<JPanel>();
  // panelsList.add( panel );
  //
  // fillBufferParameters(); //to avoid an empty buffer of parameters if user
  // closes the window without saving
  // return panelsList;
  // }

  // public static boolean closePanel( Frame frame ) {
  //
  // String regex = "[0-9]+";
  // String baseAddress = baseAddressTF.getText();
  // String numSamples = numSamplesTF.getText();
  //
  // if( baseAddress.length() <= 2 && baseAddress.length() > 0 ) {
  // JOptionPane.showMessageDialog( frame, "Please enter a valid base address",
  // "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( baseAddress.length() > 2 ) {
  // if( !( baseAddress.substring(0,2).equals("0x") ||
  // baseAddress.substring(0,2).equals("0X") ) ) {
  // JOptionPane.showMessageDialog( frame, "Base address must be expressed in
  // hexadecimal", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // }
  // if( (numSamples.length() > 0) && !numSamples.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The number of samples must be
  // expressed as a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( (numSamples.length() > 0) && (Integer.parseInt( numSamples ) == 0) ) {
  // JOptionPane.showMessageDialog( frame, "The number of samples must be greater
  // than 0", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  //
  // fillBufferParameters();
  // return true;
  // }

  // private static void fillBufferParameters() {
  //
  // if( bufferParams.size() > 0 ) {
  // bufferParams.set( BUFFER_TYPE_INDEX, String.valueOf( Buffer.FEP_BUFFER ) );
  // bufferParams.set( NUM_SAMPLES_INDEX, numSamplesTF.getText() );
  // bufferParams.set( BASE_ADDRESS_INDEX, baseAddressTF.getText() );
  // bufferParams.set( BANK_INDEX, (String)bankCB.getSelectedItem() );
  // bufferParams.set( DATA_TYPE_INDEX, (String)dataTypeCB.getSelectedItem() );
  // }
  // else {
  // bufferParams.add( BUFFER_TYPE_INDEX, String.valueOf( Buffer.FEP_BUFFER ) );
  // bufferParams.add( NUM_SAMPLES_INDEX, numSamplesTF.getText() );
  // bufferParams.add( BASE_ADDRESS_INDEX, baseAddressTF.getText() );
  // bufferParams.add( BANK_INDEX, (String)bankCB.getSelectedItem() );
  // bufferParams.add( DATA_TYPE_INDEX, (String)dataTypeCB.getSelectedItem() );
  // }
  // }
  //
  // public static ArrayList<String> getBufferParameters() {
  // return bufferParams;
  // }

} // End of class
