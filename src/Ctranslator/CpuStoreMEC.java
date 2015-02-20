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
   * Class CpuStoreMEC, Model Extension Construct (MEC) class for Embb non-cachable CPU store operation
   * Creation: 06/02/2014
   * @version 1.0 06/02/2014
   * @author Andrea ENRICI
   * @see
   */

package Ctranslator;

import java.util.*;

public class CpuStoreMEC extends CPMEC	{

	/*protected String addr = "";
	protected String word = "";*/

	public CpuStoreMEC()	{
		node_type = "CPU";
		inst_type = "STORE";
		inst_decl = "VOID";
		buff_type = "MM_BUFF_TYPE";
		buff_init = "VOID";
		exec_code = "embb_mem_write_32( (uint32_t) /*USER TO DO: ADDRESS*/, (uint32_t) /*USER TO DO: WORD*/ );";
		init_code = "VOID";
		cleanup_code = "VOID";
	}
}	//End of class
