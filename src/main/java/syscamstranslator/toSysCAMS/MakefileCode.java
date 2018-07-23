/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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
 */

/* this class produces the lines containing essentially the initial #includes; we include all potential components event if they are not used in the deployment diagram*/

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package syscamstranslator.toSysCAMS;

import java.util.LinkedList;

import syscamstranslator.*;

/**
 * Class MakefileCode
 * Principal code of a makefile
 * Creation: 02/06/2018
 * @version 1.0 02/06/2018
 * @author Irina Kit Yan LEE
*/

public class MakefileCode {
	static private String corpsMakefile;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	MakefileCode() {}

	public static String getMakefileCode(SysCAMSTCluster cluster) {
		if (cluster != null) {
			LinkedList<SysCAMSTBlockTDF> tdf = cluster.getBlockTDF();
			LinkedList<SysCAMSTBlockDE> de = cluster.getBlockDE();
			
			corpsMakefile = "# Compiler and linker flags" + CR + "CXXFLAGS = -g -Wall -I. $(SYSTEMC_INCLUDE_DIRS)" + CR 
					+ "LDFLAGS = $(SYSTEMC_LIBRARY_DIRS)" + CR2 + "# List of all ecutables to be compiled" + CR
					+ "EXECUTABLES = " + cluster.getClusterName() + "_tb" + CR2 + "# .PHONY targets don't generate files" + CR
					+ ".PHONY:	all clean" + CR2 + "# Default targets" + CR + "all:	$(EXECUTABLES)" + CR2;
			
			corpsMakefile = corpsMakefile + cluster.getClusterName() + "_tb: " +  cluster.getClusterName() + "_tb.cpp";
			
			for (SysCAMSTBlockTDF t : tdf) {
				corpsMakefile = corpsMakefile + " " + t.getName() + ".h";
			}
			
			for (SysCAMSTBlockDE t : de) {
				corpsMakefile = corpsMakefile + " " + t.getName() + ".h";
			}
			
			corpsMakefile = corpsMakefile + CR + "\t$(CXX) $(CXXFLAGS) $(LDFLAGS) -o $@ $< -lsystemc-ams -lsystemc | c++filt" 
			+ CR2 + "# Clean rule to delete temporary and generated files" + CR + "clean:" + CR 
			+ "\trm -rf *~ *.o *.dat *.vcd *.dSYM $(EXECUTABLES)" + CR;
		} else {
			corpsMakefile = "";
		}
		return corpsMakefile;
	}
}
