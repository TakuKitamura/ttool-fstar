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

import java.util.Vector;

/**
   * Class SingleDmaMEC, Model Extension Construct (MEC) class for a single DMA data transfer
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
 */
public class SingleDmaMEC extends CPMEC	{

	//private static final int MaxParameters = 3;
//	private static final int destinationAddressIndex = 0;
//	private static final int sourceAddressIndex = 1;
//	private static final int counterIndex = 2;

//	private static final String destinationAddress = "destinationAddress";
//	private static final String sourceAddress = "sourceAddress";
//	private static final String counter = "counter";

	// Issue #38: default handled in super class
	private final String memoryBaseAddress;// = "0";
	private final String dataToTransfer;// = USER_TO_DO;
	private final String dstAddress;// = USER_TO_DO;
	private final String srcAddress;// = USER_TO_DO;

	public SingleDmaMEC(	String ctxName,
							ArchUnitMEC archMEC,
							int srcMemoryType,
							int dstMemoryType,
							int transferType,
							Vector<String> attributes )	{
		super( attributes );
//		
//		switch( srcMemoryType )	{
//			case Buffer.FEP_BUFFER:
//				memoryBaseAddress = "fep_mss";
//				break;
//			case Buffer.ADAIF_BUFFER:
//				memoryBaseAddress = "adaif_mss";
//			break;
//			case Buffer.INTERLEAVER_BUFFER:
//				memoryBaseAddress = "intl_mss";
//			break;
//			case Buffer.MAPPER_BUFFER:
//				memoryBaseAddress = "mapper_mss";
//			break;
//			case Buffer.MAIN_MEMORY_BUFFER:
//				memoryBaseAddress = DEFAULT_NUM_VAL;//"0";
//			break;
//			default:
//				memoryBaseAddress = DEFAULT_NUM_VAL;//"0";
//			break;
//		}
		
		memoryBaseAddress = getMemoryBaseAddress( srcMemoryType );

		//if( attributes.size() > 0 )	{
		dataToTransfer = getAttributeValue( SAMPLES_LOAD_ATTRIBUTE_NAME, DEFAULT_NUM_VAL );//attributes.get( counterIndex );
		srcAddress = getAttributeValue( SOURCE_ADDRESS_ATTRIBUTE_NAME, DEFAULT_NUM_VAL );//attributes.get( sourceAddressIndex );
		dstAddress = getAttributeValue( DEST_ADDRESS_ATTRIBUTE_NAME, DEFAULT_NUM_VAL );//attributes.get( destinationAddressIndex );
		//}

		switch( transferType )	{
			case CPMEC.MEM_2_IP:
				exec_code = TAB + "embb_mem2ip((EMBB_CONTEXT *)&" + ctxName + ", (uintptr_t) " + memoryBaseAddress + ", " + srcAddress + ", " + dataToTransfer + " );" + CR;
				init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + ", " + "(uintptr_t) " + archMEC.getLocalMemoryPointer() + " );" + CR;
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName +");";
			break;
			case CPMEC.IP_2_MEM:
				exec_code = TAB + "embb_ip2mem( " + dstAddress + ", (EMBB_CONTEXT *)&" + ctxName + ", (uintptr_t) " + memoryBaseAddress + ", " + dataToTransfer + " );" + CR;
				init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + ", " + "(uintptr_t) " + archMEC.getLocalMemoryPointer() + " );" + CR;
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName +");";
			break;
			case CPMEC.IP_2_IP:
				exec_code = TAB + "embb_ip2ip((EMBB_CONTEXT *)&" + ctxName + "_0, (uintptr_t) " + memoryBaseAddress + ", (EMBB_CONTEXT *)&" + ctxName + "_1, (uintptr_t) " + memoryBaseAddress + ", " + dataToTransfer + " );" + CR;	
				init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + "_0, " + "(uintptr_t) " + archMEC.getLocalMemoryPointer() + " );" + CR;
				init_code += TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + "_1, " + "(uintptr_t) " + archMEC.getLocalMemoryPointer() + " );" + CR;
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName +"_0);";
				cleanup_code += TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName +"_1);";
			break;
			default:
				exec_code = TAB + "embb_mem2ip((EMBB_CONTEXT *)&" + ctxName + ", (uintptr_t) " + memoryBaseAddress + ", " + srcAddress + ", " + dataToTransfer + " );" + CR;
			break;
		}
	}
//
//	public static Vector<String> sortAttributes( Vector<String> assignedAttributes ) {
//		Vector<String> newVector = new Vector<String>( assignedAttributes );
//		
//		for( String s: assignedAttributes )	{
//			if( s.contains( destinationAddress ) )	{
//				newVector.set( destinationAddressIndex, getAttributeValue(s) );
//			}
//			
//			if( s.contains( sourceAddress ) )	{
//				newVector.set( sourceAddressIndex, getAttributeValue(s) );
//			}
//			
//			if( s.contains( counter ) )	{
//	//			try {
//					newVector.set( counterIndex, getAttributeValue(s) );
////				}
////				catch (IndexOutOfBoundsException ex) {
////					ex.printStackTrace();
////				}
//			}
//		}
//		
//		return newVector;
//	}
}	//End of class
