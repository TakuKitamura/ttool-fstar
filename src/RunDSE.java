/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

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
* Class RunDSE
* Linecommand application for translating TIF to other languages
* Creation: 24/06/2011
* @version 1.0 24/06/2011
* @author Ludovic APVRILLE
* @see
*/

import java.io.*;
import java.util.*;

//import tmltranslator.*;
//import tmltranslator.touppaal.*;
//import tmltranslator.tomappingsystemc.*;
import tmltranslator.tomappingsystemc2.*;
//import tmltranslator.toturtle.*;

import translator.*;

import dseengine.*;

import myutil.*;
//import uppaaldesc.*;

public class RunDSE  {

    /*public static int conversionType; 
	public static File inputFile;
	public static File outputFile;
	public static String outputFileName;
	public static String inputData;
	public static String outputData;
	public static TMLModeling tmlm;
	public static TMLMapping tmap;*/
	public static boolean debug = false;
	public static boolean optimize = false;
	
	
	public static void printCopyright() {
		System.out.println("RunDSE: (C) Institut Telecom / Telecom ParisTech, Ludovic Apvrille, Ludovic.Apvrille@telecom-paristech.fr");
		System.out.println("RunDSE is released under a CECILL License. See http://www.cecill.info/index.en.html");
		System.out.println("For more information on TTool related technologies, please consult http://ttool.telecom-paristech.fr");
		
		System.out.println("Enjoy!!!\n");
	}
	
	public static void printUsage() {
		System.out.println("RunDSE: usage");
		System.out.println("RunDSE [-debug] [-optimize] <.dse file>");
	}
	
	public static boolean checkArgs(String [] args) {
		return !(args.length < 1);
	}
	
	public static boolean hasDebug(String [] args) {
		if (args[0].equals("-debug")) {
			return true;
		}
		
		if (args.length > 1) {
			if (args[1].equals("-debug")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hasOptimize(String [] args) {
		if (args[0].equals("-optimize")) {
			return true;
		}
		
		if (args.length > 1) {
			if (args[1].equals("-optimize")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean analyseArgs(String [] args) {
		return true;
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
			debug = true;
			nbOfOptions ++;
		}
		if (hasOptimize(args)) {
			optimize = true;
			nbOfOptions ++;
		}
		
		if (nbOfOptions > 0) {
			//debug = true;
			tmp = new String[args.length - nbOfOptions];
			for(int i=nbOfOptions; i<args.length; i++) {
				tmp[i-nbOfOptions] = args[i];
			}
			args = tmp;
		}
		
		
		if (!analyseArgs(args)) {
			printUsage();
			return;
		}
		
		
		DSEScriptReader reader = new DSEScriptReader(args[0]);
		reader.setDebug(debug);
		reader.setOptimize(optimize);
		
		int ret = reader.execute();
		
		if (ret != DSEScriptReader.OK) {
			System.out.println("DSE: error at line " + reader.getLineOfError());
		} else {
			System.out.println("DSE was successfully executed"); 
		}
	
        
	}
	
} // Class RunDSE

