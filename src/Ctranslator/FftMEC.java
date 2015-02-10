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
   * Class FftMEC, Model Extension Construct (MEC) class for Embb operation Fast Fourier Transform
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package Ctranslator;

import java.util.*;
//import Ctranslator.*;

public class FftMEC extends TaskMEC	{

	public FftMEC( String XOP, String ID0, String OD0, String BTC )	{
		node_type = "FEP";
		inst_type = "FFT";
		inst_decl = "FEP_CONTEXT";
		buff_type = "FEP_BUFF_TYPE";
		buff_init = "= {/*l,b,q,t*/};";
		exec_code = "/*firm instruction*/" + CR +
			" fep_set_l(&$XOP$, ((FEP_BUFF_TYPE*)sig[$ID0$].pBuff)->l);" + CR +
			" fep_set_qx(&$XOP$,((FEP_BUFF_TYPE*)sig[$ID0$].pBuff)->q);" + CR +
			" fep_set_bx(&$XOP$, sig[$ID0$].roff + ((FEP_BUFF_TYPE*)sig[$ID0$].pBuff)->b);" + CR +
			" fep_set_qz(&$XOP$,((FEP_BUFF_TYPE*)sig[$OD0$].pBuff)->q);" + CR +
			" fep_set_bz(&$XOP$, sig[$OD0$].woff + ((FEP_BUFF_TYPE*)sig[$OD0$].pBuff)->b);" + CR +
			"/*start execution*/" + CR +
			"fep_start(&$XOP$);" + CR;
		
		init_code ="/***** INIT $XOP$ *******/" + CR +
			"void init_$XOP$(void){" + CR +
			" fep_ctx_init(&$XOP$,0);" + CR +
			" // initialize context" + CR +
			" fep_set_op(&$XOP$,FEP_OP_FT );" + CR +
			" fep_set_l(&$XOP$,((FEP_BUFF_TYPE*)sig[$OD0$].pBuff)->l);" + CR +
			" fep_set_i(&$XOP$,0);" + CR +
			" fep_set_r(&$XOP$,/*USER TODO*/);" + CR +
			" fep_set_wx(&$XOP$,/*USER TODO*/);" + CR +
			" fep_set_qz(&$XOP$,((FEP_BUFF_TYPE*)sig[$OD0$].pBuff)->q);" + CR +
			" fep_set_bz(&$XOP$,((FEP_BUFF_TYPE*)sig[$OD0$].pBuff)->b);" + CR +
			" fep_set_wz(&$XOP$,/*USER TODO*/);" + CR +
			"}" + CR;
		cleanup_code = "fep_ctx_cleanup(&$XOP$);";
	}

}	//End of class
