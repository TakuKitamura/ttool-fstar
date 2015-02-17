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
   * Class MapperMEC, Model Extension Construct (MEC) class for Embb Mapper operations
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package Ctranslator;

import java.util.*;
//import Ctranslator.*;

public class InterleaverMEC extends TaskMEC	{

	public InterleaverMEC( String XOP, String ID0, String OD0, String BTC )	{

		node_type = "INTERLEAVER";
		inst_type = "INTL";
		inst_decl = "INTL_CONTEXT";
		buff_type = "INTL_BUFF_TYPE";
		buff_init = "= {/*l,b,q,t*/};";
		exec_code = "/*start execution*/" + CR +
								"intl_start(&" + XOP + ");" + CR;
	
		init_code ="/***** INIT " + XOP + " *******/" + CR +
			"void init_" + XOP + "( void )\t{" + CR + TAB +
			"intl_ctx_init(/* TODO */);" + CR + TAB +
			"intl_set_sv( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_arm( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_re( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_se( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_fe( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_pbo( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_pbi( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_widm1( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_biof( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_boof( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_fz( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_fo( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_iof( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_oof( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_pof( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"intl_set_lenm1( ((INTL_BUFF_TYPE*)sig[" + OD0 + "].pBuff)->base_address), (uint64_t)/*USER TODO*/ );" + CR + TAB +
			"}" + CR;
		cleanup_code = "intl_ctx_cleanup( /*TODO*/ );";
	}

}	//End of class
