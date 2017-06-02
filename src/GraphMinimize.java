/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT telecom-paritech.fr
   andrea.enrici AT telecom-paristech.fr

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
   * Class GraphMinimize
   * Command line application for minimizing graphs
   * Apps of the package show, minimize and convert
   * Creation: 11/01/2017
   * @version 1.10 11/01/2017
   * @author Ludovic APVRILLE
   * @see
   */

import myutil.FileUtils;
import myutil.TraceManager;
import ui.graph.AUTGraph;

import java.io.File;

public class GraphMinimize {

    public static void printCopyright() {
        System.out.println("GraphMinimize: (C) Telecom ParisTech, Ludovic APVRILLE ludovic.apvrille, andrea.enrici@telecom-paristech.fr");
        System.out.println("GraphMinimize is released under a CECILL License. See http://www.cecill.info/index.en.html");
        System.out.println("For more information on TTool related technologies, please consult http://ttool.telecom-paristech.fr/");
        System.out.println("Enjoy!!!\n");
    }

    public static void printUsage() {
        System.out.println("GraphMinimize: usage");
        System.out.println("GraphShow <inputfile> <outputfile>");
	System.out.println("<options> are optional. There might be : -debug -tauonly");
        System.out.println("<inputfile> should be in AUT format, and be readable");
	System.out.println("<outputfile> will be in AUT format, and be writable");
    }

    public static boolean checkArgs(String [] args) {
        return !(args.length < 2);
    }

    public static boolean hasDebug(String [] args) {
	for (String s: args) {
	    if (s.equals("-debug")) {
		return true;
	    }
	    
	}
	return false;
    }

    public static boolean hasTauOnly(String [] args) {
	for (String s: args) {
	    if (s.equals("-tauonly")) {
		return true;
	    }
	    
	}
	return false;
    }

    public static String getInputFile(String [] args) {
	return args[args.length-2];
    }

    public static String getOutputFile(String [] args) {
	return args[args.length-1];
    }
    
    
    public static String prepareFiles(String _inputFile) {

        File inputFile = new File(_inputFile);
        try {
            if (!FileUtils.checkFileForOpen(inputFile)) {
                System.out.println("Cannot read file: " + _inputFile);
                return null;
            }

            return FileUtils.loadFileData(inputFile);
	    
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }

    }


    public static void main(String[] args) {
        String[] tmp;

        printCopyright();

        if (!checkArgs(args)) {
            printUsage();
            return;
        }

        int nbOfOptions = 0;
        if (hasDebug(args)) {
	    TraceManager.devPolicy = TraceManager.TO_CONSOLE;
        } else {
	    TraceManager.devPolicy = TraceManager.TO_DEVNULL;
	}
	

	String graphData = prepareFiles(getInputFile(args));
	
         if (graphData == null) {
            printUsage();
            return;
        }

	 AUTGraph graph = new AUTGraph();
	 graph.buildGraph(graphData);

	 System.out.println("Graph has " + graph.getNbOfStates() + " and " + graph.getNbOfTransitions() + " transitions.");

	 System.out.println("Minimizing graph");
	 graph.minimizeTau(hasTauOnly(args));
	 

	 graph.display(true);
    }

} // Class GraphShow
