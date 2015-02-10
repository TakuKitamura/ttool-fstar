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
   * Class DmaMEC, Model Extension Construct (MEC) class for a DMA data transfer
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package Ctranslator;

import java.util.*;
//import Ctranslator.*;

/* This is the code from Jair MEC. It refers to old drivers. So far I am just interested in the proof of concepts of generating
 * code. */
public class DmaMEC extends CPMEC	{

	public DmaMEC()	{

		node_type = "DMA";
		inst_type = "I2M";
		inst_decl = "DMA_CONTEXT";
		buff_type = "MM_BUFF_TYPE";
		buff_init = "= {/*bl*/,$OD0$_dat};";
		exec_code = "/*firm instruction*/" + CR +
			" dma_set_loc(&$XOP$, sig[$ID0$].roff * (((FEP_BUFF_TYPE*)sig[$ID0$].pBuff)->t+1) + ((FEP_BUFF_TYPE*)sig[$ID0$].pBuff)->b + ((FEP_BUFF_TYPE*)sig[$ID0$].pBuff)->q * FEP_QSIZE );" + CR +
			" dma_set_mem(&$XOP$, sig[$OD0$].woff*4 + ((MM_BUFF_TYPE*)sig[$OD0$].pBuff)->b);" + CR +
			" dma_set_bsize(&$XOP$, ((MM_BUFF_TYPE*)sig[$OD0$].pBuff)->bl );" + CR +
			"dma_start_i2m(&$XOP$);" + CR;	
			init_code = "/***** INIT $XOP$ I2M*******/" + CR +
			"void init_$XOP$(void){" + CR +
			" dma_ctx_init(&$XOP$, 0);" + CR +
			" /* initialize context*/" + CR +
			" dma_set_mem(&$XOP$, ((MM_BUFF_TYPE*)sig[$OD0$].pBuff)->b);" + CR +
			"}" + CR;
		cleanup_code = "dma_ctx_cleanup(&$XOP$);";
	}

}	//End of class
