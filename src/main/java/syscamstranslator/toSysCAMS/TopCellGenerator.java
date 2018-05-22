/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
  Daniela Genius, Lip6, UMR 7606 

  ludovic.apvrille AT enst.fr
  daniela.genius@lip6.fr

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
*/

/* Generator of the top cell for simulation with SoCLib virtual component 
   library */

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package syscamstranslator.toSysCAMS;

import syscamstranslator.*;
import syscamstranslator.toSysCAMS.*;
import ui.syscams.SysCAMSBlockTDF;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TopCellGenerator {
	// --------------- accessing Avatardd -----------------
	public static SysCAMSSpecification syscams;
	// ---------------------------------------------------



	public String VCIparameters;
	public String config;
	public String mainFile;
	public String src;
	public String top;
	public String deployinfo;
	public String deployinfo_map;
	public String deployinfo_ram;
	public String platform_desc;
	public String procinfo;
	public String nbproc;
	public final String DOTH = ".h";
	public final String DOTCPP = ".cpp";
	public final String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	public final String CR = "\n";
	public final String CR2 = "\n\n";
	public final String SCCR = ";\n";
	public final String EFCR = "}\n";
	public final String EFCR2 = "}\n\n";
	public final String EF = "}";
	public final String COTE = "";
	public final String NAME_RST = "signal_resetn";
	public final String TYPEDEF = "typedef";

	private final static String GENERATED_PATH = "generated_topcell" + File.separator;
	private boolean tracing;

	public TopCellGenerator(SysCAMSSpecification sys, boolean _tracing) {
		syscams = sys;
		tracing = _tracing;
	}

	public String generateTopCell(SysCAMSTBlockTDF tdf) {
		/* first test validity of the hardware platform */
		if (TopCellGenerator.syscams.getNbCluster() == 0) {
			System.out.println("***Warning: require at least one cluster***");
		}
		if (TopCellGenerator.syscams.getNbBlockTDF() == 0) {
			System.out.println("***Warning: require at least one TDF block***");
		}
		if (TopCellGenerator.syscams.getNbPortTDF() == 0) {
			System.out.println("***Warning: require at least one TDF port***");
		}
		if (TopCellGenerator.syscams.getNbBlockDE() == 0) {
			System.out.println("***Warning: require at least one DE block***");
		}
		if (TopCellGenerator.syscams.getNbPortDE() == 0) {
			System.out.println("***Warning: require at least one DE port***");
		}
		if (TopCellGenerator.syscams.getNbPortConverter() == 0) {
			System.out.println("***Warning: require at least one converter port***");
		}
		String top = Header.getPrimitiveHeader(tdf) + Body.getPrimitiveBody(tdf);
		return (top);
	}

	public static void saveFile(String path) {
//		try {
//			System.err.println(path + GENERATED_PATH + "top.cc");
//			FileWriter fw = new FileWriter(path + GENERATED_PATH + "/top.cc");
//			top = generateTopCell();
//			fw.write(top);
//			fw.close();
//		} catch (IOException ex) {
//		}
		saveFileBlockTDF(path);
	}

	public static void saveFileBlockTDF(String path) {
		LinkedList<SysCAMSTCluster> clusters = TopCellGenerator.syscams.getAllCluster();
		String code;
		
		for (SysCAMSTCluster c : clusters) {
			List<SysCAMSBlockTDF> tdf = c.getBlocks();
			for (SysCAMSBlockTDF t : tdf) {
				try {
					System.err.println(path + GENERATED_PATH + t.getValue() + ".h");
					FileWriter fw = new FileWriter(path + GENERATED_PATH + "/" + t.getValue() + ".h");
					code = PrimitiveCode.getPrimitiveCode(t);
					fw.write(code);
					fw.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void main (String[] args) {
		saveFile("/main/syscamstranslator/");
	}
}
