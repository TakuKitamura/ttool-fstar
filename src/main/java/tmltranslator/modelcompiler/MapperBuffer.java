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

import org.w3c.dom.Element;
import tmltranslator.TMLTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Class MapperBuffer Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class MapperBuffer extends Buffer {

  private static final String[] symmetricalValues = { "OFF", "ON" };

  // data in
  private static final int NUM_SAMPLES_DATAIN_INDEX = 1;

  // Issue #98: Provide default values for compilation
  private String numSamplesDataInValue = DEFAULT_NUM_VAL + USER_TO_DO;
  private static final String NUM_SAMPLES_DATAIN_TYPE = "uint16_t";

  private static final int BASE_ADDRESS_DATAIN_INDEX = 2;
  private String baseAddressDataInValue = DEFAULT_NUM_VAL + USER_TO_DO;
  private static final String BASE_ADDRESS_DATAIN_TYPE = "uint16_t";

  private static final int BITS_PER_SYMBOL_DATAIN_INDEX = 3;
  private String bitsPerSymbolDataInValue = DEFAULT_NUM_VAL + USER_TO_DO;
  private static final String BITS_PER_SYMBOL_DATAIN_TYPE = "uint16_t";

  private static int SYMMETRICAL_VALUE_DATAIN_INDEX = 4;
  private String symmetricalValueDataInValue = DEFAULT_BOOL_VAL + USER_TO_DO;
  private static final String SYMMETRICAL_VALUE_DATAIN_TYPE = BOOLEAN_TYPE;

  // data out
  private static final int BASE_ADDRESS_DATAOUT_INDEX = 5;
  private String baseAddressDataOutValue = DEFAULT_NUM_VAL + USER_TO_DO;
  private static final String BASE_ADDRESS_DATAOUT_TYPE = "uint16_t";

  // Look up table
  private static final int BASE_ADDRESS_LUT_INDEX = 6;
  private String baseAddressLUTValue = DEFAULT_NUM_VAL + USER_TO_DO;
  private static final String BASE_ADDRESS_LUT_TYPE = "uint16_t";

  // private static ArrayList<String> bufferParams;// = new ArrayList<String>();
  // //the DS that collects all the above params

  private static final int MAX_PARAMETERS = 6;

  // PANEL
  // Mapper Data In
  // private static JTextField baseAddressDataIn_TF, numSamplesDataIn_TF,
  // bitsPerSymbolDataIn_TF;
  // private static String baseAddressDataIn = "", numSamplesDataIn = "",
  // bitsPerSymbolDataIn = "", symmetricalValueDataIn = "";
  // private static JComboBox<String> symmetricalValueDataIn_CB;
  // Mapper Data Out
  // private static JTextField baseAddressDataOut_TF;
  // private static String baseAddressDataOut = "";
  // Mapper LUT
  // private static JTextField baseAddressLUT_TF;
  // private static String baseAddressLUT = "";

  public static final String DECLARATION = "struct MAPPER_BUFFER_TYPE {" + CR + TAB + NUM_SAMPLES_DATAIN_TYPE + SP
      + "num_symbols" + SC + CR + TAB + BASE_ADDRESS_DATAIN_TYPE + SP + "input_base_address" + SC + CR + TAB
      + BITS_PER_SYMBOL_DATAIN_TYPE + SP + "num_bits_per_symbol" + SC + CR + TAB + SYMMETRICAL_VALUE_DATAIN_TYPE + SP
      + "symmetrical_value" + SC + CR + TAB + BASE_ADDRESS_DATAOUT_TYPE + SP + "output_base_address" + SC + CR + TAB
      + BASE_ADDRESS_LUT_TYPE + SP + "lut_base_address" + SC + CR + "}" + SC + CR2
      + "typedef MAPPER_BUFFER_TYPE MAPPER_BUFFER_TYPE" + SC;

  private String Context = "MAPPER_CONTEXT";

  public MapperBuffer(String _name, TMLTask _task) {
    type = "MAPPER_BUFFER_TYPE";
    name = _name;
    task = _task;
  }

  @Override
  public String getInitCode() {
    StringBuffer s = new StringBuffer();
    if (bufferParameters != null) {
      retrieveBufferParameters();
    }
    s.append(TAB + name + ".num_symbols = " + "(" + NUM_SAMPLES_DATAIN_TYPE + ")" + numSamplesDataInValue + SC + CR);
    s.append(TAB + name + ".input_base_address = " + "(" + BASE_ADDRESS_DATAIN_TYPE + ")" + baseAddressDataInValue + SC
        + CR);
    s.append(TAB + name + ".num_bits_per_symbol = " + "(" + BITS_PER_SYMBOL_DATAIN_TYPE + ")" + bitsPerSymbolDataInValue
        + SC + CR);
    s.append(TAB + name + ".symmetrical_value = " + "(" + SYMMETRICAL_VALUE_DATAIN_TYPE + ")"
        + symmetricalValueDataInValue + SC + CR);
    s.append(TAB + name + ".output_base_address = " + "(" + BASE_ADDRESS_DATAOUT_TYPE + ")" + baseAddressDataOutValue
        + SC + CR);
    s.append(TAB + name + ".lut_base_address = " + "(" + BASE_ADDRESS_LUT_TYPE + ")" + baseAddressLUTValue + SC + CR);
    return s.toString();
  }

  @Override
  public String toString() {

    StringBuffer s = new StringBuffer(super.toString());
    s.append(TAB2 + "num_symbols = " + numSamplesDataInValue + SC + CR);
    s.append(TAB2 + "input_base_address = " + baseAddressDataInValue + SC + CR);
    s.append(TAB2 + "num_bits_per_symbol = " + bitsPerSymbolDataInValue + SC + CR);
    s.append(TAB2 + "symmetrical_value = " + symmetricalValueDataInValue + SC + CR);
    s.append(TAB2 + "output_base_address = " + baseAddressDataOutValue + SC + CR);
    s.append(TAB2 + "lut_base_address = " + baseAddressLUTValue + SC + CR);
    return s.toString();
  }

  private void retrieveBufferParameters() {

    if (bufferParameters.size() == MAX_PARAMETERS) {
      if (bufferParameters.get(NUM_SAMPLES_DATAIN_INDEX).length() > 0) {
        numSamplesDataInValue = bufferParameters.get(NUM_SAMPLES_DATAIN_INDEX);
      }
      if (bufferParameters.get(BASE_ADDRESS_DATAIN_INDEX).length() > 0) {
        baseAddressDataInValue = bufferParameters.get(BASE_ADDRESS_DATAIN_INDEX);
      }
      if (bufferParameters.get(BITS_PER_SYMBOL_DATAIN_INDEX).length() > 0) {
        bitsPerSymbolDataInValue = bufferParameters.get(BITS_PER_SYMBOL_DATAIN_INDEX);
      }
      if (bufferParameters.get(SYMMETRICAL_VALUE_DATAIN_INDEX).length() > 0) {
        symmetricalValueDataInValue = String.valueOf((new Vector<String>(Arrays.asList(symmetricalValues)))
            .indexOf(bufferParameters.get(SYMMETRICAL_VALUE_DATAIN_INDEX)));
      }
      if (bufferParameters.get(BASE_ADDRESS_DATAOUT_INDEX).length() > 0) {
        baseAddressDataOutValue = bufferParameters.get(BASE_ADDRESS_DATAOUT_INDEX);
      }
      if (bufferParameters.get(BASE_ADDRESS_LUT_INDEX).length() > 0) {
        baseAddressLUTValue = bufferParameters.get(BASE_ADDRESS_LUT_INDEX);
      }
    }
  }

  public String getContext() {
    return Context;
  }

  public static String appendBufferParameters(java.util.List<String> buffer) {

    StringBuffer sb = new StringBuffer();
    sb.append("\" bufferType=\"" + Integer.toString(Buffer.MAPPER_BUFFER));
    if (buffer.size() == MAX_PARAMETERS + 1) { // because the first parameter is the bufferType
      // data in
      sb.append("\" numSamplesDataIn=\"" + buffer.get(NUM_SAMPLES_DATAIN_INDEX));
      sb.append("\" baseAddressDataIn=\"" + buffer.get(BASE_ADDRESS_DATAIN_INDEX));
      sb.append("\" bitsPerSymbolDataIn=\"" + buffer.get(BITS_PER_SYMBOL_DATAIN_INDEX));
      sb.append("\" symmetricalValueDataIn=\"" + buffer.get(SYMMETRICAL_VALUE_DATAIN_INDEX));
      // data out
      sb.append("\" baseAddressDataOut=\"" + buffer.get(BASE_ADDRESS_DATAOUT_INDEX));
      // Look-up Table
      sb.append("\" baseAddressLUT=\"" + buffer.get(BASE_ADDRESS_LUT_INDEX));
    } else {
      // data in
      sb.append("\" numSamplesDataIn=\"\"" + SP);
      sb.append("baseAddressDataIn=\"\"" + SP);
      sb.append("bitsPerSymbolDataIn=\"\"" + SP);
      sb.append("symmetricalValueDataIn=\"\"" + SP);
      // data out
      sb.append("baseAddressDataOut=\"\"" + SP);
      // Look-up Table
      sb.append("baseAddressLUT=\"");
    }
    return sb.toString();
  }

  public static ArrayList<String> buildBufferParameters(Element elt) {

    ArrayList<String> buffer = new ArrayList<String>();
    buffer.add(0, Integer.toString(Buffer.MAPPER_BUFFER));
    buffer.add(NUM_SAMPLES_DATAIN_INDEX, elt.getAttribute("numSamplesDataIn"));
    buffer.add(BASE_ADDRESS_DATAIN_INDEX, elt.getAttribute("baseAddressDataIn"));
    buffer.add(BITS_PER_SYMBOL_DATAIN_INDEX, elt.getAttribute("bitsPerSymbolDataIn"));
    buffer.add(SYMMETRICAL_VALUE_DATAIN_INDEX, elt.getAttribute("symmetricalValueDataIn"));
    // data out
    buffer.add(BASE_ADDRESS_DATAOUT_INDEX, elt.getAttribute("baseAddressDataOut"));
    // Look-up Table
    buffer.add(BASE_ADDRESS_LUT_INDEX, elt.getAttribute("baseAddressLUT"));
    return buffer;
  }

  // Issue #98: Not used anymore
  // public static ArrayList<JPanel> makePanel( GridBagConstraints c1,
  // GridBagConstraints c2 ) {
  //
  // GridBagLayout gridbag2 = new GridBagLayout();
  //
  // JPanel panel3 = new JPanel();
  // panel3.setLayout(gridbag2);
  // panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: input
  // buffer configuration"));
  // panel3.setPreferredSize(new Dimension(650, 350));
  //
  // JPanel panel4 = new JPanel();
  // panel4.setLayout(gridbag2);
  // panel4.setBorder(new javax.swing.border.TitledBorder("Code generation: output
  // buffer configuration"));
  // panel4.setPreferredSize(new Dimension(650, 350));
  //
  // JPanel panel5 = new JPanel();
  // panel5.setLayout(gridbag2);
  // panel5.setBorder(new javax.swing.border.TitledBorder("Code generation: Look
  // Up Table configuration"));
  // panel5.setPreferredSize(new Dimension(650, 350));
  //
  // //Data In panel
  // c2.anchor = GridBagConstraints.LINE_START;
  // numSamplesDataIn_TF = new JTextField( numSamplesDataIn, 5 );
  // panel3.add( new JLabel( "Number of symbols = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( numSamplesDataIn_TF, c1 );
  // //
  // baseAddressDataIn_TF = new JTextField( baseAddressDataIn, 5 );
  // panel3.add( new JLabel( "Base address = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( baseAddressDataIn_TF, c1 );
  // //
  // bitsPerSymbolDataIn_TF = new JTextField( bitsPerSymbolDataIn, 5 );
  // panel3.add( new JLabel( "Number of bits/symbol = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( bitsPerSymbolDataIn_TF, c1 );
  // //
  // symmetricalValueDataIn_CB = new JComboBox<String>( new Vector<String>(
  // Arrays.asList( symmetricalValues ) ) );
  // panel3.add( new JLabel( "Symmetrical value = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( symmetricalValueDataIn_CB, c1 );
  //
  // //Data Out panel
  // baseAddressDataOut_TF = new JTextField( baseAddressDataOut, 5 );
  // panel4.add( new JLabel( "Base address = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel4.add( baseAddressDataOut_TF, c1 );
  // //
  // //Look Up Table panel
  // baseAddressLUT_TF = new JTextField( baseAddressLUT, 5 );
  // panel5.add( new JLabel( "Base address = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel5.add( baseAddressLUT_TF, c1 );
  //
  // ArrayList<JPanel> panelsList = new ArrayList<JPanel>();
  // panelsList.add(panel3);
  // panelsList.add(panel4);
  // panelsList.add(panel5);
  //
  // fillBufferParameters(); //to avoid an empty buffer of parameters if user
  // closes the window without saving
  // return panelsList;
  // }
  //
  // public static boolean closePanel( Frame frame ) {
  //
  // //check DI
  // numSamplesDataIn = numSamplesDataIn_TF.getText();
  // baseAddressDataIn = baseAddressDataIn_TF.getText();
  // bitsPerSymbolDataIn = bitsPerSymbolDataIn_TF.getText();
  // symmetricalValueDataIn = (String)symmetricalValueDataIn_CB.getSelectedItem();
  // String regex = "[0-9]+";
  //
  // if( baseAddressDataIn.length() <= 2 && baseAddressDataIn.length() > 0 ) {
  // JOptionPane.showMessageDialog( frame, "Please enter a valid base address for
  // the input buffer", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( baseAddressDataIn.length() > 2 ) {
  // if( !( baseAddressDataIn.substring(0,2).equals("0x") ||
  // baseAddressDataIn.substring(0,2).equals("0X") ) ) {
  // JOptionPane.showMessageDialog( frame, "Base address must be expressed in
  // hexadecimal", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // }
  // if( numSamplesDataIn.length() > 0 ) {
  // if( !numSamplesDataIn.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The number of bits/symbol must be
  // expressed as a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // }
  // if( bitsPerSymbolDataIn.length() > 0 ) {
  // if( !bitsPerSymbolDataIn.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The number of bits/symbol must be
  // expressed as a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // }
  //
  // //check DO
  // baseAddressDataOut = baseAddressDataOut_TF.getText();
  // if( baseAddressDataOut.length() <= 2 && baseAddressDataOut.length() > 0 ) {
  // JOptionPane.showMessageDialog( frame, "Please enter a valid base address for
  // the output buffer", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( baseAddressDataOut.length() > 2 ) {
  // if( !( baseAddressDataOut.substring(0,2).equals("0x") ||
  // baseAddressDataOut.substring(0,2).equals("0X") ) ) {
  // JOptionPane.showMessageDialog( frame, "Base address must be expressed in
  // hexadecimal", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // }
  //
  // //check LUT table
  // baseAddressLUT = baseAddressLUT_TF.getText();
  // if( baseAddressLUT.length() <= 2 && baseAddressLUT.length() > 0 ) {
  // JOptionPane.showMessageDialog( frame, "Please enter a valid LUT base
  // address", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( baseAddressLUT.length() > 2 ) {
  // if( !( baseAddressLUT.substring(0,2).equals("0x") ||
  // baseAddressLUT.substring(0,2).equals("0X") ) ) {
  // JOptionPane.showMessageDialog( frame, "Base address must be expressed in
  // hexadecimal", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // }
  //
  // fillBufferParameters();
  // return true;
  // }
  //
  // private static void fillBufferParameters() {
  //
  // if( bufferParams == null ) {
  // bufferParams = new ArrayList<String>();
  // }
  // if( bufferParams.size() > 0 ) {
  // bufferParams.set( BUFFER_TYPE_INDEX, String.valueOf( Buffer.MAPPER_BUFFER )
  // );
  // //data in
  // bufferParams.set( NUM_SAMPLES_DATAIN_INDEX, numSamplesDataIn );
  // bufferParams.set( BASE_ADDRESS_DATAIN_INDEX, baseAddressDataIn );
  // bufferParams.set( BITS_PER_SYMBOL_DATAIN_INDEX, bitsPerSymbolDataIn );
  // bufferParams.set( SYMMETRICAL_VALUE_DATAIN_INDEX, symmetricalValueDataIn );
  // //data out
  // bufferParams.set( BASE_ADDRESS_DATAOUT_INDEX, baseAddressDataOut );
  // //look-up table
  // bufferParams.set( BASE_ADDRESS_LUT_INDEX, baseAddressLUT );
  // }
  // else {
  // bufferParams.add( BUFFER_TYPE_INDEX, String.valueOf( Buffer.MAPPER_BUFFER )
  // );
  // //data in
  // bufferParams.add( NUM_SAMPLES_DATAIN_INDEX, numSamplesDataIn );
  // bufferParams.add( BASE_ADDRESS_DATAIN_INDEX, baseAddressDataIn );
  // bufferParams.add( BITS_PER_SYMBOL_DATAIN_INDEX, bitsPerSymbolDataIn );
  // bufferParams.add( SYMMETRICAL_VALUE_DATAIN_INDEX, symmetricalValueDataIn );
  // //data out
  // bufferParams.add( BASE_ADDRESS_DATAOUT_INDEX, baseAddressDataOut );
  // //look-up table
  // bufferParams.add( BASE_ADDRESS_LUT_INDEX, baseAddressLUT );
  // }
  // }
  //
  // public static ArrayList<String> getBufferParameters() {
  // return bufferParams;
  // }
  //
} // End of class
