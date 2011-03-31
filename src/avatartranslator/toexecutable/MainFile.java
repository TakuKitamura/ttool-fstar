/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
*
* ludovic.apvrille AT enst.fr
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
*
* /**
* Class MainFile
* Creation: 29/03/2011
* @version 1.1 29/03/2011
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.toexecutable;

import java.awt.*;
import java.util.*;

import myutil.*;
import avatartranslator.*;

public class MainFile {
	
	private final static String H_DEF = "#ifndef MAIN_H\n#define MAIN_H\n";
	private final static String H_END_DEF = "#endif\n";

	private final static String INCLUDE_HEADER = "#include <stdio.h>\n#include <pthread.h>\n#include <unistd.h>\n#include <stdlib.h>\n";
	private final static String LOCAL_INCLUDE_HEADER = "#include \"request.h\"\n#include \"syncchannel.h\"\n#include \"request_manager.h\"\n#include \"debug.h\""; 
	
	private final static String MAIN_DEC = "int main(int argc, char *argv[]) {\n";
	private final static String CR = "\n";
	
	private String name;
	private String hCode;
	private String beforeMainCode;
	private String mainCode;
	
	
	public MainFile(String _name) {
		name = _name;
		hCode = "";
		mainCode = "";
		beforeMainCode = "";
	}
	
	public String getName() {
		return name;
	}
	
	public void appendToHCode(String _code) {
		hCode += _code;
	}
	
	public void appendToBeforeMainCode(String _code) {
		beforeMainCode += _code;
	}
	
	public void appendToMainCode(String _code) {
		mainCode += _code;
	}
	
	public String getHeaderCode() {
		return H_DEF + hCode + H_END_DEF;
	}
	
	public String getMainCode() {
		String s = INCLUDE_HEADER + "\n" + LOCAL_INCLUDE_HEADER + CR + CR;
		s += beforeMainCode + CR;
		s += MAIN_DEC + CR + mainCode + CR + "}" + CR;
		
		return s;
		
	}
	
}