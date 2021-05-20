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
 * Class InterleaverBuffer Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class InterleaverBuffer extends Buffer {

  // public static final String[] symmetricalValues = { "OFF", "ON" };
  // data in
  public static final int PACKED_BINARY_IN_INDEX = 1;
  public static final int WIDTH_INDEX = 2;
  public static final int BIT_IN_OFFSET_INDEX = 3;
  public static final int INPUT_OFFSET_INDEX = 4;
  // data out
  public static final int PACKED_BINARY_OUT_INDEX = 5;
  public static final int BIT_OUT_OFFSET_INDEX = 6;
  public static final int OUTPUT_OFFSET_INDEX = 7;
  // permutation table
  public static final int OFFSET_PERM_INDEX = 8;
  public static final int LENGTH_PERM_INDEX = 9;

  public String packedBinaryInValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String PACKED_BINARY_IN_TYPE = BOOLEAN_TYPE;

  public String widthValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String WIDTH_TYPE = "uint8_t";

  public String bitInOffsetValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String BIT_IN_OFFSET_TYPE = "uint8_t";

  public String inputOffsetValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String INPUT_OFFSET_TYPE = "uint16_t";

  // data out
  public String packedBinaryOutValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String PACKED_BINARY_OUT_TYPE = BOOLEAN_TYPE;

  public String bitOutOffsetValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String BIT_OUT_OFFSET_TYPE = "uint8_t";

  public String outputOffsetValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String OUTPUT_OFFSET_TYPE = "uint16_t";

  // permutation table
  public String offsetPermValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String OFFSET_PERM_TYPE = "uint16_t";

  public String lengthPermValue = DEFAULT_NUM_VAL + USER_TO_DO;
  public static String LENGTH_PERM_TYPE = "uint16_t";

  // private static ArrayList<String> bufferParams = new ArrayList<String>();
  // //the DS that collects all the above params

  private static final int MAX_PARAMETERS = 9;

  public static final String DECLARATION = "struct INTERLEAVER_BUFFER_TYPE {" + CR + TAB + PACKED_BINARY_IN_TYPE + SP
      + "packed_binary_input_mode" + SC + CR + TAB + WIDTH_TYPE + SP + "samples_width" + SC + CR + TAB
      + BIT_IN_OFFSET_TYPE + SP + "bit_input_offset" + SC + CR + TAB + INPUT_OFFSET_TYPE + SP + "input_offset" + SC + CR
      + TAB +
      // data out
      PACKED_BINARY_OUT_TYPE + SP + "packed_binary_output_mode" + SC + CR + TAB + BIT_OUT_OFFSET_TYPE + SP
      + "bit_output_offset" + SC + CR + TAB + OUTPUT_OFFSET_TYPE + SP + "output_offset" + SC + CR + TAB +
      // permutation table
      OFFSET_PERM_TYPE + SP + "permutation_offset" + SC + CR + TAB + LENGTH_PERM_TYPE + SP + "permutation_length" + SC
      + CR + "}" + SC + CR2 + "typedef INTERLEAVER_BUFFER_TYPE INTERLEAVER_BUFFER_TYPE" + SC + CR;

  private String Context = "INTL_CONTEXT";

  // PANEL
  // Intl Data In
  // private static JTextField width_TF, bitInOffset_TF, inputOffset_TF;
  // private static String width = "", bitInOffset = "", inputOffset = "",
  // packedBinaryIn = "";
  // private static JComboBox<String> packedBinaryIn_CB;

  // Data Out
  // private static JTextField bitOutOffset_TF, outputOffset_TF;
  // private static JComboBox<String> packedBinaryOut_CB;
  // private static String packedBinaryOut = "", bitOutOffset = "", outputOffset =
  // "";

  // Perm
  // private static JTextField lengthPerm_TF, offsetPerm_TF;
  // private static String lengthPerm = "", offsetPerm = "";

  public InterleaverBuffer(String _name, TMLTask _task) {
    type = "INTERLEAVER_BUFFER_TYPE";
    name = _name;
    task = _task;
  }

  @Override
  public String getInitCode() {
    StringBuffer s = new StringBuffer();
    if (bufferParameters != null) {
      retrieveBufferParameters();
    }
    s.append(TAB + name + ".packed_binary_input_mode = " + "(" + PACKED_BINARY_IN_TYPE + ")" + packedBinaryInValue + SC
        + CR);
    s.append(TAB + name + ".samples_width = " + SP + "(" + WIDTH_TYPE + ")" + widthValue + SC + CR);
    s.append(TAB + name + ".bit_input_offset = " + SP + "(" + BIT_IN_OFFSET_TYPE + ")" + bitInOffsetValue + SC + CR);
    s.append(TAB + name + ".input_offset = " + SP + "(" + INPUT_OFFSET_TYPE + ")" + inputOffsetValue + SC + CR);
    // data out
    s.append(TAB + name + ".packed_binary_output_mode = " + SP + "(" + PACKED_BINARY_OUT_TYPE + ")"
        + packedBinaryOutValue + SC + CR);
    s.append(TAB + name + ".bit_output_offset = " + SP + "(" + BIT_OUT_OFFSET_TYPE + ")" + bitOutOffsetValue + SC + CR);
    s.append(TAB + name + ".output_offset = " + SP + "(" + OUTPUT_OFFSET_TYPE + ")" + outputOffsetValue + SC + CR);
    // permutation table
    s.append(TAB + name + ".permutation_offset = " + SP + "(" + OFFSET_PERM_TYPE + ")" + offsetPermValue + SC + CR);
    s.append(TAB + name + ".permutation_length = " + SP + "(" + LENGTH_PERM_TYPE + ")" + lengthPermValue + SC + CR);
    return s.toString();
  }

  public String toString() {

    StringBuffer s = new StringBuffer(super.toString());
    s.append(TAB2 + ".packed_binary_input_mode = " + packedBinaryInValue + SC + CR);
    s.append(TAB2 + ".samples_width = " + SP + widthValue + SC + CR);
    s.append(TAB2 + ".bit_input_offset = " + SP + bitInOffsetValue + SC + CR);
    s.append(TAB2 + ".input_offset = " + SP + inputOffsetValue + SC + CR);
    // data out
    s.append(TAB2 + ".packed_binary_output_mode = " + SP + packedBinaryOutValue + SC + CR);
    s.append(TAB2 + ".bit_output_offset = " + SP + bitOutOffsetValue + SC + CR);
    s.append(TAB2 + ".output_offset = " + SP + outputOffsetValue + SC + CR);
    // permutation table
    s.append(TAB2 + ".permutation_offset = " + SP + offsetPermValue + SC + CR);
    s.append(TAB2 + ".permutation_length = " + SP + lengthPermValue + SC + CR);
    return s.toString();
  }

  private void retrieveBufferParameters() {

    if (bufferParameters.size() == MAX_PARAMETERS) {
      if (bufferParameters.get(PACKED_BINARY_IN_INDEX).length() > 0) {
        packedBinaryInValue = String.valueOf((new Vector<String>(Arrays.asList(Buffer.ON_OFF_VALUES)))
            .indexOf(bufferParameters.get(PACKED_BINARY_IN_INDEX)));
      }
      if (bufferParameters.get(WIDTH_INDEX).length() > 0) {
        widthValue = bufferParameters.get(WIDTH_INDEX);
      }
      if (bufferParameters.get(BIT_IN_OFFSET_INDEX).length() > 0) {
        bitInOffsetValue = bufferParameters.get(BIT_IN_OFFSET_INDEX);
      }
      if (bufferParameters.get(INPUT_OFFSET_INDEX).length() > 0) {
        inputOffsetValue = bufferParameters.get(INPUT_OFFSET_INDEX);
      }
      if (bufferParameters.get(PACKED_BINARY_OUT_INDEX).length() > 0) {
        packedBinaryOutValue = String.valueOf((new Vector<String>(Arrays.asList(Buffer.ON_OFF_VALUES)))
            .indexOf(bufferParameters.get(PACKED_BINARY_OUT_INDEX)));
      }
      if (bufferParameters.get(BIT_OUT_OFFSET_INDEX).length() > 0) {
        bitOutOffsetValue = bufferParameters.get(BIT_OUT_OFFSET_INDEX);
      }
      if (bufferParameters.get(OUTPUT_OFFSET_INDEX).length() > 0) {
        outputOffsetValue = bufferParameters.get(OUTPUT_OFFSET_INDEX);
      }
      if (bufferParameters.get(OFFSET_PERM_INDEX).length() > 0) {
        offsetPermValue = bufferParameters.get(OFFSET_PERM_INDEX);
      }
      if (bufferParameters.get(LENGTH_PERM_INDEX).length() > 0) {
        lengthPermValue = bufferParameters.get(LENGTH_PERM_INDEX);
      }
    }
  }

  public String getContext() {
    return Context;
  }

  public static ArrayList<String> buildBufferParameters(Element elt) {

    ArrayList<String> buffer = new ArrayList<String>();
    buffer.add(0, Integer.toString(Buffer.INTERLEAVER_BUFFER));
    buffer.add(NUM_SAMPLES_INDEX, elt.getAttribute("numSamples"));
    buffer.add(BASE_ADDRESS_INDEX, elt.getAttribute("baseAddress"));
    // data in
    buffer.add(elt.getAttribute("packedBinaryIn"));
    buffer.add(elt.getAttribute("width"));
    buffer.add(elt.getAttribute("bitInOffset"));
    buffer.add(elt.getAttribute("inputOffset"));
    // data out
    buffer.add(elt.getAttribute("packedBinaryOut"));
    buffer.add(elt.getAttribute("bitOutOffset"));
    buffer.add(elt.getAttribute("outputOffset"));
    // permutation table
    buffer.add(elt.getAttribute("offsetPerm"));
    buffer.add(elt.getAttribute("lengthPerm"));
    return buffer;
  }

  public static String appendBufferParameters(java.util.List<String> buffer) {

    StringBuffer sb = new StringBuffer();
    sb.append("\" bufferType=\"" + Integer.toString(Buffer.INTERLEAVER_BUFFER));
    if (buffer.size() == MAX_PARAMETERS + 1) { // because the first parameter is the bufferType
      // data in
      sb.append("\" packedBinaryIn=\"" + buffer.get(PACKED_BINARY_IN_INDEX));
      sb.append("\" width=\"" + buffer.get(WIDTH_INDEX));
      sb.append("\" bitInOffset=\"" + buffer.get(BIT_IN_OFFSET_INDEX));
      sb.append("\" inputOffset=\"" + buffer.get(INPUT_OFFSET_INDEX));
      // data out
      sb.append("\" packedBinaryOut=\"" + buffer.get(PACKED_BINARY_OUT_INDEX));
      sb.append("\" bitOutOffset=\"" + buffer.get(BIT_OUT_OFFSET_INDEX));
      sb.append("\" outputOffset=\"" + buffer.get(OUTPUT_OFFSET_INDEX));
      // permutation table
      sb.append("\" offsetPerm=\"" + buffer.get(OFFSET_PERM_INDEX));
      sb.append("\" lengthPerm=\"" + buffer.get(LENGTH_PERM_INDEX));
    } else {
      // data in
      // Issue #98 Missing double quote
      sb.append("\" packedBinaryIn=\"\"" + SP);
      // sb.append( "\" packedBinaryIn=\"" + SP );
      sb.append("width=\"\"" + SP);
      sb.append("bitInOffset=\"\"" + SP);
      sb.append("inputOffset=\"\"" + SP);
      // data out
      sb.append("packedBinaryOut=\"\"" + SP);
      sb.append("bitOutOffset=\"\"" + SP);
      sb.append("outputOffset=\"\"" + SP);
      // permutation table
      sb.append("offsetPerm=\"\"" + SP);
      sb.append("lengthPerm=\"");
    }

    return sb.toString();
  }

  // public static ArrayList<JPanel> makePanel( GridBagConstraints c1,
  // GridBagConstraints c2 ) {
  //
  // GridBagLayout gridbag2 = new GridBagLayout();
  //
  // JPanel panel3 = new JPanel(); //data in
  // panel3.setLayout(gridbag2);
  // panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: input
  // buffer configuration"));
  // panel3.setPreferredSize(new Dimension(650, 350));
  //
  // JPanel panel4 = new JPanel(); //data out
  // panel4.setLayout(gridbag2);
  // panel4.setBorder(new javax.swing.border.TitledBorder("Code generation: output
  // buffer configuration"));
  // panel4.setPreferredSize(new Dimension(650, 350));
  //
  // JPanel panel5 = new JPanel(); //permutation table
  // panel5.setLayout(gridbag2);
  // panel5.setBorder(new javax.swing.border.TitledBorder("Code generation:
  // Permutation Table configuration"));
  // panel5.setPreferredSize(new Dimension(650, 350));
  //
  // //Data In panel
  // c2.anchor = GridBagConstraints.LINE_START;
  // packedBinaryIn_CB = new JComboBox<String>( Buffer.onOffVector );
  // panel3.add( new JLabel( "Packed binary input mode = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( packedBinaryIn_CB, c1 );
  // //
  // width_TF = new JTextField( width, 5 );
  // panel3.add( new JLabel( "Sample width = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( width_TF, c1 );
  // //
  // bitInOffset_TF = new JTextField( bitInOffset, 5 );
  // panel3.add( new JLabel( "Bit input offset = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( bitInOffset_TF, c1 );
  // //
  // inputOffset_TF = new JTextField( inputOffset, 5 );
  // panel3.add( new JLabel( "Offset of first input sample = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel3.add( inputOffset_TF, c1 );
  // //
  //
  // //Data Out panel
  // c2.anchor = GridBagConstraints.LINE_START;
  // packedBinaryOut_CB = new JComboBox<String>( Buffer.onOffVector );
  // panel4.add( new JLabel( "Packed binary output mode = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel4.add( packedBinaryOut_CB, c1 );
  // //
  // bitOutOffset_TF = new JTextField( bitOutOffset, 5 );
  // panel4.add( new JLabel( "Bit output offset = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel4.add( bitOutOffset_TF, c1 );
  // //
  // c2.anchor = GridBagConstraints.LINE_START;
  // outputOffset_TF = new JTextField( outputOffset, 5 );
  // panel4.add( new JLabel( "Offset of first output sample = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel4.add( outputOffset_TF, c1 );
  //
  // //Permutation Table panel
  // c2.anchor = GridBagConstraints.LINE_START;
  // offsetPerm_TF = new JTextField( offsetPerm, 5 );
  // panel5.add( new JLabel( "Offset = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel5.add( offsetPerm_TF, c1 );
  // //
  // c2.anchor = GridBagConstraints.LINE_START;
  // lengthPerm_TF = new JTextField( lengthPerm, 5 );
  // panel5.add( new JLabel( "Length = "), c2 );
  // c1.gridwidth = GridBagConstraints.REMAINDER;
  // panel5.add( lengthPerm_TF, c1 );
  //
  // ArrayList<JPanel> panelsList = new ArrayList<JPanel>();
  // panelsList.add( panel3 );
  // panelsList.add( panel4 );
  // panelsList.add( panel5 );
  //
  // fillBufferParameters(); //to avoid an empty buffer of parameters if user
  // closes the window without saving
  // return panelsList;
  // }
  //
  // public static boolean closePanel( Frame frame ) {
  //
  // String regex = "[0-9]+";
  // width = width_TF.getText();
  // bitInOffset = bitInOffset_TF.getText();
  // inputOffset = inputOffset_TF.getText();
  // packedBinaryIn = (String)packedBinaryIn_CB.getSelectedItem();
  //
  // if( ( width.length() > 0 ) && !width.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The samples width must be expressed as
  // a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( ( bitInOffset.length() > 0 ) && !bitInOffset.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The bit input offset must be expressed
  // as a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( ( inputOffset.length() > 0 ) && !inputOffset.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The bit intput offset must be
  // expressed as a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  //
  // //check DO
  // packedBinaryOut = (String)packedBinaryOut_CB.getSelectedItem();
  // bitOutOffset = bitOutOffset_TF.getText();
  // outputOffset = outputOffset_TF.getText();
  //
  // if( ( bitOutOffset.length() > 0 ) && !bitOutOffset.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The bit output offset must be
  // expressed as a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // // check output offset
  // if( ( outputOffset.length() > 0 ) && !outputOffset.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The output offset must be expressed as
  // a natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  //
  // //check Permutation table
  // offsetPerm = offsetPerm_TF.getText();
  // lengthPerm = lengthPerm_TF.getText();
  // //check first entry offset
  // if( (offsetPerm.length() > 0) && !offsetPerm.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The offset must be expressed as a
  // natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( (lengthPerm.length() > 0) && !lengthPerm.matches( regex ) ) {
  // JOptionPane.showMessageDialog( frame, "The length must be expressed as a
  // natural", "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  // if( (lengthPerm.length() > 0) && (Integer.parseInt( lengthPerm ) == 0) ) {
  // JOptionPane.showMessageDialog( frame, "The length must be greater than 0",
  // "Badly formatted parameter",
  // JOptionPane.INFORMATION_MESSAGE );
  // return false;
  // }
  //
  // fillBufferParameters();
  // return true;
  // }
  //
  // private static void fillBufferParameters() {
  //
  // if( bufferParams.size() > 0 ) {
  // bufferParams.set( BUFFER_TYPE_INDEX, String.valueOf(
  // Buffer.INTERLEAVER_BUFFER ) );
  // //data in
  // bufferParams.set( PACKED_BINARY_IN_INDEX, packedBinaryIn );
  // bufferParams.set( WIDTH_INDEX, width );
  // bufferParams.set( BIT_IN_OFFSET_INDEX, bitInOffset );
  // bufferParams.set( INPUT_OFFSET_INDEX, inputOffset );
  // //data out
  // bufferParams.set( PACKED_BINARY_OUT_INDEX, packedBinaryOut );
  // bufferParams.set( BIT_OUT_OFFSET_INDEX, bitOutOffset );
  // bufferParams.set( OUTPUT_OFFSET_INDEX, outputOffset );
  // //permutation table
  // bufferParams.set( OFFSET_PERM_INDEX, offsetPerm );
  // bufferParams.set( LENGTH_PERM_INDEX, lengthPerm );
  // }
  // else {
  // bufferParams.add( BUFFER_TYPE_INDEX, String.valueOf(
  // Buffer.INTERLEAVER_BUFFER ) );
  // //data in
  // bufferParams.add( PACKED_BINARY_IN_INDEX, packedBinaryIn );
  // bufferParams.add( WIDTH_INDEX, width );
  // bufferParams.add( BIT_IN_OFFSET_INDEX, bitInOffset );
  // bufferParams.add( INPUT_OFFSET_INDEX, inputOffset );
  // //data out
  // bufferParams.add( PACKED_BINARY_OUT_INDEX, packedBinaryOut );
  // bufferParams.add( BIT_OUT_OFFSET_INDEX, bitOutOffset );
  // bufferParams.add( OUTPUT_OFFSET_INDEX, outputOffset );
  // //permutation table
  // bufferParams.add( OFFSET_PERM_INDEX, offsetPerm );
  // bufferParams.add( LENGTH_PERM_INDEX, lengthPerm );
  // }
  // }
  //
  // public static ArrayList<String> getBufferParameters() {
  // return bufferParams;
  // }
  //
} // End of class
