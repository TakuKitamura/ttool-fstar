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
   * Class Signal
   * Creation: 11/02/2014
   * @version 1.0 11/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;
import java.nio.*;
import myutil.*;
import tmltranslator.*;

public class Signal	{

	public String CR = "\n";
	public String SC = ";";
	
	private String status_s = "bool f" + SC + CR;
	private boolean status = false;

	private String writeOffset_s = "int woff" + SC + CR;
	private int writeOffset;

	private String readOffset_s = "int roff" + SC + CR;
	private int readOffset;

	private String buffPointer_s = "void *pBuff" + SC + CR;
	private Buffer buffPointer = null;
	
	public static final String DECLARATION = "struct SIG_TYPE	{\n\tbool f;\n\tint woff;\n\tint roff;\n\tvoid *pBuff;\n};\ntypedef struct SIG_TYPE SIG_TYPE;";

	private TMLPort port;
	private String name;
	private TMLCPLibArtifact artifact;
	
	public Signal( String _name, TMLPort _port, TMLCPLibArtifact _artifact )	{
		port = _port;
		name = _name;
		artifact = _artifact;
	}

	public Signal()	{
		port = null;
	}

	public String toString()	{
		return DECLARATION;
	}

	public String getName()	{
		return name;
	}

	public TMLCPLibArtifact getArtifact()	{
		return artifact;
	}

}	//End of class
