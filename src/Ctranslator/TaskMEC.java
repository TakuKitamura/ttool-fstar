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
   * Class TaskMEC, Model Extension Construct (MEC) class for tasks
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package Ctranslator;

import java.util.*;
import java.nio.*;
import myutil.*;
//import Ctranslator.*;

public abstract class TaskMEC	{

	public String CR = "\n";
	public String TAB = "\t";
	public String node_type = new String();
	public String inst_type = new String();
	public String inst_decl = new String();
	public String buff_type = new String();
	public String buff_init = new String();
	public String init_code = new String();
	public String exec_code = new String();
	public String cleanup_code = new String();
	
	public String ID0 = new String();
	public String OD0 = new String();
	public String XOP = new String();
	public String BTC = new String();
	
	public TaskMEC()	{
		node_type = "1";
	}

	public String getExecCode()	{
		return exec_code;
	}

	public String getInitCode()	{
		return init_code;
	}

	public String toString()	{
		return node_type + CR + inst_decl + CR + inst_type + CR + buff_type + CR + buff_init + CR + exec_code + CR + init_code + CR + cleanup_code;
	}

	public void saveFile( String path, String filename ) throws FileException {
		
		TraceManager.addUser( "Saving C CP file in " + path + filename );
		FileUtils.saveFile( path + filename, this.toString() );
	}
}	//End of class
