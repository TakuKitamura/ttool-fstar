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

import java.util.List;
import java.util.Vector;

/**
 * Class DoubleDmaMEC, Model Extension Construct (MEC) class for a double DMA
 * data transfer Creation: 05/02/2014
 * 
 * @version 1.0 05/02/2014
 * @author Andrea ENRICI
 */
public class DoubleDmaMEC extends CPMEC {

  // public static final int MaxParameters = 6;
  // public static final int destinationAddress1Index = 0;
  // public static final int sourceAddress1Index = 1;
  // public static final int counter1Index = 2;
  // public static final int destinationAddress2Index = 3;
  // public static final int sourceAddress2Index = 4;
  // public static final int counter2Index = 5;

  private static final String DESTINATION_ADDRESS_1 = "destinationAddress1";
  private static final String SOURCE_ADDRESS_1 = "sourceAddress1";
  private static final String COUNTER_1 = "counter1";
  private static final String DESTINATION_ADDRESS_2 = "destinationAddress2";
  private static final String SOURCE_ADDRESS_2 = "sourceAddress2";
  private static final String COUNTER_2 = "counter2";

  public static final String[] ORDERED_ATTRIBUTE_NAMES = new String[] { DESTINATION_ADDRESS_1, SOURCE_ADDRESS_1,
      COUNTER_1, DESTINATION_ADDRESS_2, SOURCE_ADDRESS_2, COUNTER_2 };

  private Vector<String> memoryBaseAddress = new Vector<String>();
  private String dataToTransfer1 = DEFAULT_NUM_VAL + USER_TO_DO;
  private String dataToTransfer2 = DEFAULT_NUM_VAL + USER_TO_DO;
  private String dstAddress1 = DEFAULT_NUM_VAL + USER_TO_DO;
  private String srcAddress1 = DEFAULT_NUM_VAL + USER_TO_DO;
  private String dstAddress2 = DEFAULT_NUM_VAL + USER_TO_DO;
  private String srcAddress2 = DEFAULT_NUM_VAL + USER_TO_DO;
  // private String memoryBaseAddress1 = USER_TO_DO;
  // private String memoryBaseAddress2 = USER_TO_DO;
  private String ctxName1 = USER_TO_DO;
  private String ctxName2 = USER_TO_DO;

  public DoubleDmaMEC(String ctxName, Vector<ArchUnitMEC> archMECs, Vector<Integer> srcMemoryTypes,
      Vector<Integer> dstMemoryTypes, List<Integer> transferTypes, Vector<String> attributes) {
    super(attributes);

    // int numSrcMemories = srcMemoryTypes.size();

    for (final int memorytype : srcMemoryTypes) {
      // for( int i = 0; i < numSrcMemories; i++ ) {
      memoryBaseAddress.add(getMemoryBaseAddress(memorytype));

      // Issue #98: Moved to super class
      // switch( srcMemoryTypes.get(i).intValue() ) {
      // case Buffer.FEP_BUFFER:
      // memoryBaseAddress.add( "fep_mss" );
      // break;
      // case Buffer.ADAIF_BUFFER:
      // memoryBaseAddress.add( "adaif_mss" );
      // break;
      // case Buffer.INTERLEAVER_BUFFER:
      // memoryBaseAddress.add( "intl_mss" );
      // break;
      // case Buffer.MAPPER_BUFFER:
      // memoryBaseAddress.add( "mapper_mss" );
      // break;
      // case Buffer.MAIN_MEMORY_BUFFER:
      // memoryBaseAddress.add( "0" );
      // break;
      // default:
      // memoryBaseAddress.add( "0" );
      // break;
      // }
    }

    // int dstMemoryType = dstMemoryTypes.get(0).intValue();
    ArchUnitMEC archMEC = archMECs.get(0);

    if (attributes.size() > 0) {
      dataToTransfer1 = getAttributeValue(COUNTER_1, DEFAULT_NUM_VAL);// attributes.get( counter1Index );
      dataToTransfer2 = getAttributeValue(COUNTER_2, DEFAULT_NUM_VAL);// attributes.get( counter2Index );
      dstAddress1 = getAttributeValue(DESTINATION_ADDRESS_1, DEFAULT_NUM_VAL);// attributes.get(
                                                                              // destinationAddress1Index );
      srcAddress1 = getAttributeValue(SOURCE_ADDRESS_1, DEFAULT_NUM_VAL);// attributes.get( sourceAddress1Index );
      dstAddress2 = getAttributeValue(DESTINATION_ADDRESS_2, DEFAULT_NUM_VAL);// attributes.get(
                                                                              // destinationAddress2Index );
      srcAddress2 = getAttributeValue(SOURCE_ADDRESS_2, DEFAULT_NUM_VAL);// attributes.get( sourceAddress2Index );

      // Issue #98: Bugs in code generation. These do not depend on attribute values
      // ctxName1 = ctxName + "_1";
      // ctxName2 = ctxName + "_2";
    }

    // Issue #98: Bugs in code generation. These do not depend on attribute values
    ctxName1 = ctxName + "_0";
    ctxName2 = ctxName + "_1";

    // build the code for the first transfer type
    switch (transferTypes.get(0)) {
      case CPMEC.MEM_2_IP:
        exec_code = TAB + "embb_mem2ip((EMBB_CONTEXT *)&" + ctxName1 + ", (uintptr_t) " + memoryBaseAddress.get(0)
            + ", " + srcAddress1 + ", " + dataToTransfer1 + " );" + CR;
        init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName1 + ", " + "(uintptr_t) "
            + memoryBaseAddress.get(0) + " );" + CR;
        cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName1 + ");" + CR;
        break;
      case CPMEC.IP_2_MEM:
        exec_code = TAB + "embb_ip2mem( " + dstAddress1 + ", (EMBB_CONTEXT *)&" + ctxName1 + ", (uintptr_t) "
            + memoryBaseAddress.get(0) + ", " + dataToTransfer1 + " );" + CR;
        init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName1 + ", " + "(uintptr_t) "
            + memoryBaseAddress.get(0) + " );" + CR;
        cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName1 + ");";
        break;
      case CPMEC.IP_2_IP:
        exec_code = TAB + "embb_ip2ip((EMBB_CONTEXT *)&" + ctxName1 + ", (uintptr_t) " + memoryBaseAddress.get(0)
            + ", (EMBB_CONTEXT *)&" + ctxName1 + ", (uintptr_t) " + memoryBaseAddress.get(1) + ", " + dataToTransfer1
            + " );" + CR;
        init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName1 + "(uintptr_t) "
            + memoryBaseAddress.get(0) + " );" + CR;
        cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName1 + ");" + CR;
        break;
    }

    // build the code for the second transfer type
    switch (transferTypes.get(1)) {
      case CPMEC.MEM_2_IP:
        exec_code += TAB + "embb_mem2ip((EMBB_CONTEXT *)&" + ctxName2 + ", (uintptr_t) " + memoryBaseAddress.get(1)
            + ", " + srcAddress2 + ", " + dataToTransfer2 + " );" + CR;
        init_code += TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName2 + ", " + "(uintptr_t) "
            + memoryBaseAddress.get(1) + " );" + CR;
        cleanup_code += TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName2 + ");";
        break;
      case CPMEC.IP_2_MEM:
        exec_code += TAB + "embb_ip2mem( " + dstAddress2 + ", (EMBB_CONTEXT *)&" + ctxName2 + ", (uintptr_t) "
            + memoryBaseAddress.get(1) + ", " + dataToTransfer2 + " );" + CR;
        init_code += TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName2 + ", " + "(uintptr_t) "
            + memoryBaseAddress.get(1) + " );" + CR;
        cleanup_code += TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName2 + ");";
        break;
      case CPMEC.IP_2_IP:
        exec_code += TAB + "embb_ip2ip((EMBB_CONTEXT *)&" + ctxName2 + ", (uintptr_t) " + memoryBaseAddress.get(0)
            + ", (EMBB_CONTEXT *)&" + ctxName2 + ", (uintptr_t) " + memoryBaseAddress.get(1) + ", " + dataToTransfer1
            + " );" + CR;
        init_code += TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName2 + "(uintptr_t) "
            + memoryBaseAddress.get(1) + " );" + CR;
        cleanup_code += TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName2 + ");";
        break;
    }
  }
  //
  // public static Vector<String> sortAttributes( Vector<String>
  // assignedAttributes ) {
  //
  // Vector<String> newVector = new Vector<String>( assignedAttributes );
  // for( String s: assignedAttributes ) {
  // if( s.contains( destinationAddress1 ) ) {
  // newVector.set( destinationAddress1Index, getAttributeValue(s) );
  // }
  // if( s.contains( sourceAddress1 ) ) {
  // newVector.set( sourceAddress1Index, getAttributeValue(s) );
  // }
  // if( s.contains( counter1 ) ) {
  // newVector.set( counter1Index, getAttributeValue(s) );
  // }
  // if( s.contains( destinationAddress2 ) ) {
  // newVector.set( destinationAddress2Index, getAttributeValue(s) );
  // }
  // if( s.contains( sourceAddress2 ) ) {
  // newVector.set( sourceAddress2Index, getAttributeValue(s) );
  // }
  // if( s.contains( counter2 ) ) {
  // newVector.set( counter2Index, getAttributeValue(s) );
  // }
  // }
  // return newVector;
  // }
} // End of class
