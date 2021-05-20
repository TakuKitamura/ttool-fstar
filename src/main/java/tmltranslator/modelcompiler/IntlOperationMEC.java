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
 * Class IntlOperationMEC, Model Extension Construct (MEC) class for Interleaver
 * operations Creation: 05/02/2014
 * 
 * @version 1.0 05/02/2014
 * @author Andrea ENRICI
 */
public class IntlOperationMEC extends OperationMEC {

    public IntlOperationMEC(String ctxName, String ID0, String OD0) {
        name = "InterleaverOperationMEC";
        exec_code = TAB + "/*start execution*/" + CR + TAB +

        // Issue #98: Already defined
        // "int status;" + CR + TAB +

                "intl_start(&" + ctxName + ");" + CR + TAB + "status = intl_wait(&" + ctxName + ");" + CR;

        init_code = "/***** INIT " + ctxName.split("_ctx")[0] + " *******/" + CR + "void init_"
                + ctxName.split("_ctx")[0] + "(void){" + CR + TAB + "intl_ctx_init(&" + ctxName
                + ", (uintptr_t) intl_mss );" + CR + TAB + "// initialize context" + CR + TAB +

                // Issue #98: Provide default values for compilation
                "intl_set_sv(&" + ctxName + ", (uint64_t)" + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
                + "intl_set_arm(&" + ctxName + ", (uint64_t)" + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
                + "intl_set_arm(&" + ctxName + ", (uint64_t)" + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
                + "intl_set_re(&" + ctxName + ", (uint64_t)" + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
                + "intl_set_se(&" + ctxName + ", (uint64_t)" + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
                + "intl_set_fe(&" + ctxName + ", (uint64_t)" + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
                + "intl_set_pbo(&" + ctxName + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0
                + "].pBuff)->packed_binary_output_mode));" + CR + TAB + "intl_set_pbi(&" + ctxName
                + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0 + "].pBuff)->packed_binary_input_mode));" + CR + TAB
                + "intl_set_widm1(&" + ctxName + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0
                + "].pBuff)->samples_width));" + CR + TAB + "intl_set_biof(&" + ctxName
                + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0 + "].pBuff)->bit_input_offset));" + CR + TAB
                + "intl_set_boof(&" + ctxName + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0
                + "].pBuff)->bit_output_offset));" + CR + TAB + "intl_set_fz(&" + ctxName + ", (uint64_t)"
                + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB + "intl_set_fo(&" + ctxName + ", (uint64_t)"
                + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB + "intl_set_iof(&" + ctxName
                + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0 + "].pBuff)->output_offset));" + CR + TAB
                + "intl_set_oof(&" + ctxName + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0 + "].pBuff)->input_offset));"
                + CR + TAB + "intl_set_pof(&" + ctxName + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0
                + "].pBuff)->permutation_offset));" + CR + TAB + "intl_set_lenm1(&" + ctxName
                + ", (((INTERLEAVER_BUFFER_TYPE*)sig[" + ID0 + "].pBuff)->permutation_length));" + CR + TAB + "}" + CR;
        cleanup_code = "intl_ctx_cleanup(&" + ctxName + ");";
        context = "INTL_CONTEXT";
    }

} // End of class
