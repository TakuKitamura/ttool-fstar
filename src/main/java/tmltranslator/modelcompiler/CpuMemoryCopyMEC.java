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
 * Class CpuMemoryCopy, Model Extension Construct (MEC) class for a Embb memory
 * copy transfer Creation: 05/02/2014
 * 
 * @version 1.0 05/02/2014
 * @author Andrea ENRICI
 */
public class CpuMemoryCopyMEC extends CPMEC {

    // Issue #38
    // private static final int MaxParameters = 3;
    // private static final int destinationAddressIndex = 0;
    // private static final int sourceAddressIndex = 1;
    // private static final int counterIndex = 2;

    // Issue #98: Undefined embb_mss address
    private final String memoryBaseAddress;// = "embb_mss";

    private final String srcAddress;// = USER_TO_DO;
    private final String dataToTransfer;// = USER_TO_DO;

    public CpuMemoryCopyMEC(final String ctxName, final Vector<String> attributes, int srcMemoryType) {
        super(attributes);

        // if( attributes.size() > 0 ) {
        srcAddress = getAttributeValue(SOURCE_ADDRESS_ATTRIBUTE_NAME, "0");// attributes.get( sourceAddressIndex );
        dataToTransfer = getAttributeValue(SAMPLES_LOAD_ATTRIBUTE_NAME, "0");// attributes.get( counterIndex );
        // apparently there is no need to use destinationAddress
        // }
        memoryBaseAddress = getMemoryBaseAddress(srcMemoryType);

        CpuMEC cpu = new CpuMEC();
        exec_code = TAB + "embb_mem2ip((EMBB_CONTEXT *)&" + ctxName + ", (uintptr_t) " + memoryBaseAddress + ", "
                + srcAddress + ", " + dataToTransfer + " );" + CR;
        init_code = TAB + cpu.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + ", " + "(uintptr_t) "
                + cpu.getLocalMemoryPointer() + " );" + CR;
        cleanup_code = TAB + cpu.getCtxCleanupCode() + "(&" + ctxName + ");";
    }

    // Issue #38
    // public static Vector<String> sortAttributes( Vector<String>
    // assignedAttributes ) {
    // temporary manual workaround
    // newVector.add("0");
    // newVector.add("0");
    // newVector.add("0");
    // for( String s: assignedAttributes ) {
    // if( s.contains( destinationAddress ) ) {
    // newVector.set( destinationAddressIndex, getAttributeValue(s) );
    // }
    // if( s.contains( sourceAddress ) ) {
    // newVector.set( sourceAddressIndex, getAttributeValue(s) );
    // }
    // if( s.contains( counter ) ) {
    // newVector.set( counterIndex, getAttributeValue(s) );
    // }
    // }
    // return newVector;
    // }
} // End of class
