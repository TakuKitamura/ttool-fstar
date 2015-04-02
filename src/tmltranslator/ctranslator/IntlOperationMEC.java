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
   * Class IntlOperationMEC, Model Extension Construct (MEC) class for Interleaver operations
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;

public class IntlOperationMEC extends OperationMEC	{

	public IntlOperationMEC( String ctxName, String ID0, String OD0, String BTC )	{
		name = "InterleaverOperationMEC";
		exec_code = TAB + "/*firm instruction*/" + CR + TAB +
			"/*start execution*/" + CR + TAB +
			"intl_start(&" + ctxName + ");" + CR;
		
		init_code ="/***** INIT " + ctxName + " *******/" + CR +
			"void init_" + ctxName.split("_ctx")[0] + "(void){" + CR + TAB +
			"intl_ctx_init(&" + ctxName + ", (uintptr_t) intl_mss );" + CR + TAB +
			"// initialize context" + CR + TAB +
			"intl_set_lenm1( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->num_symbols), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_lba( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->lut_base_address), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_oba( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->output_base_address), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_iba( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->input_base_address), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_mult( /* USER TO DO */, (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_men( /* USER TO DO */, (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_sym( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->symmetrical_value), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_bpsm1( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->num_bits_per_symbol), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_m( (((INTL_BUFF_TYPE*)sig[" + ID0 + "].pBuff)->base_address), (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_n( /* USER TO DO */, (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"intl_set_s( /* USER TO DO */, (uint64_t) /* USER TODO: value */);" + CR + TAB +
			"}" + CR;
		cleanup_code = "intl_ctx_cleanup(&" + ctxName + ");";
	}

}	//End of class
