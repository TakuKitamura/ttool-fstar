/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT enst.fr
   andrea.enrici AT enstr.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class DoubleDmaMEC, Model Extension Construct (MEC) class for a double DMA data transfer
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;

public class DoubleDmaMEC extends CPMEC	{

	public static final String Context = "embb_dma_context";
	public static final String Ctx_cleanup = "dma_ctx_cleanup";

	public static final int MaxParameters = 12;
	public static final int destinationAddress1Index = 0;
	public static final int sourceAddress1Index = 1;
	public static final int size1Index = 2;
	public static final int counter1Index = 3;
	public static final int ID1Index = 4;
	public static final int bytesToTransfer1Index = 5;
	public static final int destinationAddress2Index = 6;
	public static final int sourceAddress2Index = 7;
	public static final int size2Index = 8;
	public static final int counter2Index = 9;
	public static final int ID12Index = 10;
	public static final int bytesToTransfer2Index = 11;

	public static final String destinationAddress1 = "destinationAddress1";
	public static final String sourceAddress1 = "sourceAddress1";
	public static final String size1 = "size1";
	public static final String counter1 = "counter1";
	public static final String ID1 = "ID1";
	public static final String bytesToTransfer1 = "bytesToTransfer1";
	public static final String destinationAddress2 = "destinationAddress2";
	public static final String sourceAddress2 = "sourceAddress2";
	public static final String size2 = "size2";
	public static final String counter2 = "counter2";
	public static final String ID12 = "ID12";
	public static final String bytesToTransfer2 = "bytesToTransfer2";

	public DoubleDmaMEC( String ctxName )	{

		node_type = "DoubleDmaMEC";
		inst_type = "VOID";
		inst_decl = "EMBB_DMA_CONTEXT";
		buff_type = "MM_BUFF_TYPE";
		buff_init = "VOID";
		exec_code = TAB + "embb_dma_start(&" + ctxName + ", /*USER TO DO: SRC_ADDRESS*/, /*USER TO DO: DST_ADDRESS*/, /*USER TO DO: NUM_SAMPLES */ );" + CR;	
		init_code = TAB + "embb_dma_ctx_init(&" + ctxName + ", /*USER TO DO: DMA_DEVICE*/, /*USER TO DO: DST_DEV*/, /*USER TO DO: SRC_DEV*/ );" + CR;
		cleanup_code = TAB + "embb_dma_ctx_cleanup(&" + ctxName + ");";
	}

	public DoubleDmaMEC( String ctxName, String destinationAddress1, String sourceAddress1, String size1, String destinationAddress2, String sourceAddress2, String size2 )	{

		node_type = "DoubleDmaMEC";
		inst_type = "VOID";
		inst_decl = "EMBB_DMA_CONTEXT";
		buff_type = "MM_BUFF_TYPE";
		buff_init = "VOID";
		exec_code = TAB + "embb_dma_start(&" + ctxName + ", (uintptr_t) " + sourceAddress1 + ", (uintptr_t) " + destinationAddress1 + ", (size_t) " + size1 + " );" + CR;	
		exec_code += TAB + "embb_dma_start(&" + ctxName + ", (uintptr_t) " + sourceAddress2 + ", (uintptr_t) " + destinationAddress2 + ", (size_t) " + size2 + " );" + CR;	
		init_code = TAB + "embb_dma_ctx_init(&" + ctxName + ", /*USER TO DO: DMA_DEVICE*/, /*USER TO DO: DST_DEV*/, /*USER TO DO: SRC_DEV*/ );" + CR;
		init_code += TAB + "embb_dma_ctx_init(&" + ctxName + ", /*USER TO DO: DMA_DEVICE*/, /*USER TO DO: DST_DEV*/, /*USER TO DO: SRC_DEV*/ );" + CR;
		cleanup_code = TAB + "embb_dma_ctx_cleanup(&" + ctxName + ");";
	}

}	//End of class
