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

/**
   * Class CwmMEC, Model Extension Construct (MEC) class for Embb Component Wise Multiplication operation
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
 */
public class CwmMEC extends FepOperationMEC	{

	public CwmMEC( String _ctxName, String inSignalName, String outSignalName )	{

		name = "Component Wise Multiplication MEC";
		exec_code = TAB + "/*firm instruction*/" + CR +

			// Issue #98: Already defined
			//"int status;" + CR + TAB +
			
			"fep_set_l(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + inSignalName + "].pBuff)->num_samples);" + CR +
			"fep_set_qx(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + inSignalName + "].pBuff)->bank);" + CR +
			"fep_set_bx(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + inSignalName + "].pBuff)->base_address);" + CR +
			"fep_set_tx(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + inSignalName + "].pBuff)->data_type);" + CR +
			"/*start execution*/" + CR +
			"status = fep_do(&" + _ctxName + ");" + CR;
	
		init_code ="/***** INIT " + _ctxName.split("_ctx")[0] + " *******/" + CR +
			"void init_" + _ctxName.split("_ctx")[0] + "(void){" + CR + TAB +
			"fep_ctx_init(&" + _ctxName + ", (uintptr_t) fep_mss );" + CR + TAB +
			"// initialize context" + CR + TAB +
			"fep_set_op(&" + _ctxName + ",FEP_OP_CWM );" + CR + TAB +
			"// X vector configuration => Zk=Y[Xi]" + CR + TAB +
			
			// Issue #98: Provide default values for compilation
			"fep_set_r(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_wx(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_sx(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_nx(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_mx(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_px(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_dx(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_vrx(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_vix(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"// Y vector configuration" + CR + TAB +
			"fep_set_by(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_qy(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_my(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_ny(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_sy(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_py(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_wy(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_ty(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_vry(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_dy(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"// Z vector addressing configuration" + CR + TAB +
			"fep_set_qz(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + outSignalName + "].pBuff)->bank);" + CR + TAB +
			"fep_set_bz(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + outSignalName + "].pBuff)->base_address);" + CR + TAB +
			"fep_set_tz(&" + _ctxName + ", ((FEP_BUFFER_TYPE*)sig[" + outSignalName + "].pBuff)->data_type);" + CR + TAB +
			"fep_set_wz(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_ri(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_sz(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_nz(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"fep_set_mz(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB +
			"// Operation configuration" + CR + TAB +
			"fep_set_sma(&" + _ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR +
			"}" + CR;
		cleanup_code = "fep_ctx_cleanup(&" + _ctxName + "_ctx);";
	}

}	//End of class
