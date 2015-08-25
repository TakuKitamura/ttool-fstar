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
   * Class Buffer
   * Creation: 11/02/2014
   * @version 1.0 11/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;
import java.nio.*;
import org.w3c.dom.Element;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import myutil.*;
import tmltranslator.*;

public class Buffer	{

	public static final String[] memoryTypesList = { "FEP memory", "MAPPER memory", "ADAIF memory", "INTERLEAVER memory", "MAIN MEMORY memory" };
	public static final String[] onOffVector = { "ON", "OFF" };
	public static final int FepBuffer = 0;
	public static final int MapperBuffer = 1;
	public static final int AdaifBuffer = 2;
	public static final int InterleaverBuffer = 3;
	public static final int MainMemoryBuffer = 4;
	public static final int BaseBuffer = 5;

	public static final int bufferTypeIndex = 0;

	public static String CR = "\n";
	public static String CR2 = "\n\n";
	public static String TAB = "\t";
	public static String TAB2 = "\t\t";
	public static String SP = " ";
	public static String SC = ";";

	protected static String USER_TO_DO = "/* USER TODO: VALUE */";
	protected String code = "VOID";
	protected String name = "";
	protected String type = "";
	protected TMLTask task;
	protected TMLPort port;
	protected TMLCPLibArtifact artifact;
	protected String baseAddress = "/* USER TODO: VALUE */";
	protected String endAddress = "/* USER TODO: VALUE */";
	protected ArrayList<String> bufferParameters;
	
	private String Context = "";

	public Buffer()	{
		code = "struct" + SP + name + TAB + "{" + CR + "}" + SC;
	}

	public String toString()	{
		if( port != null )	{
			if( artifact != null )	{
				return "buff__" + port.getName() + " mapped onto " + artifact.getMemoryName();
			}
			else	{
				return "buff__" + port.getName();
			}
		}
		else	{
			String s = type + SP + "BUFFER" + SP + name + CR;
			return s;
		}
	}

	public String getName()	{
		return name;
	}

	public String getType()	{
		return type;
	}

	public TMLTask getTask()	{
		return task;
	}

	public void addMappingArtifact( TMLCPLibArtifact _artifact )	{
		artifact = _artifact;
	}

	public TMLCPLibArtifact getMappingArtifact()	{
		return artifact;
	}

	public void setStartAddress( String _baseAddress )	{
		baseAddress = _baseAddress;
	}

	public void setEndAddress( String _endAddress )	{
		endAddress = _endAddress;
	}

	public String getInitCode()	{
		StringBuffer s = new StringBuffer();
		s.append( TAB + name + ".baseAddress = " + baseAddress + SC + CR );
		return s.toString();
	}

	public String getContext()	{
		return Context;
	}

	public void setMappingParameters( ArrayList<String> params )	{
		bufferParameters = params;
	}

}	//End of class
