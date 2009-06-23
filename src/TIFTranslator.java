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
 * Class TIFTranslator
 * Linecommand application for translating TIF to other languages
 * Creation: 29/06/2007
 * @version 1.0 29/06/2007
 * @author Ludovic APVRILLE
 * @see
 */

import java.io.*;
 
import translator.*;
import translator.touppaal.*;
import myutil.*;
import uppaaldesc.*;

public class TIFTranslator  {
	// 0 -> RT-LOTOS
	// 1 -> LOTOS
	// 2 -> UPPAAL
	// 3 -> JAVA
    public static int conversionType; 
	public static File inputFile;
	public static File outputFile;
	public static String inputData;
	public static String outputData;
	public static TURTLEModeling tm;
	
	
	public static void printCopyright() {
		System.out.println("TIFTranslator: (C) GET/ENST, Ludovic Apvrille, Ludovic.Apvrille@enst.fr");
		System.out.println("TIFTranslator is released under a CECILL License. See http://www.cecill.info/index.en.html");
		System.out.println("For more information on TURTLE related technologies, please consult http://labsoc.comelec.enst.fr/turtle/");
		
		System.out.println("Enjoy!\n");
	}
	
	public static void printUsage() {
		System.out.println("TIFTranlator: usage");
		System.out.println("TIFTranlator <language> <inputfile> <outputfile>");
		System.out.println("<language> may be: LOTOS, RT-LOTOS, UPPAAL, JAVA");
		System.out.println("<inputfile> should be in XML/TIF format, and be readable");
		System.out.println("<outputfile> should be writeable");
	}
	
	public static boolean checkArgs(String [] args) {
		return !(args.length < 3);
	}
	
	public static boolean analyseArgs(String [] args) {
		System.out.println("Converting to " + args[0]);
		
		if (args[0].toUpperCase().equals("RT-LOTOS")) {
			conversionType = 0;
		} else if (args[0].toUpperCase().equals("LOTOS")) {
			conversionType = 1;
		} else if (args[0].toUpperCase().equals("UPPAAL")) {
			conversionType = 2;
		} else if (args[0].toUpperCase().equals("JAVA")) {
			conversionType = 3;
		} else {
			return false;
		}
		return true;
	}
	
	public static boolean prepareFiles(String args[]) {
		inputFile = new File(args[1]);  
		outputFile = new File(args[2]);
		try {
			if (!FileUtils.checkFileForOpen(inputFile)) {
				System.out.println("Cannot read file: " + args[1]);
				return false;
			}
			
			inputData = FileUtils.loadFileData(inputFile);
			
			if (!FileUtils.checkFileForSave(outputFile)) {
				System.out.println("Cannot read file: " + args[1]);
				return false;
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public static boolean loadTIF() {
		boolean ret = false;
		try {
			TIFExchange tif = new TIFExchange();
			ret = tif.loadFromXMLTIF(inputData);
			tm = tif.getTURTLEModeling();
		} catch (Exception e) {
			System.out.println("Error when loading TIF data: wrong input format");
			return false;
		}
		System.out.println("TIF format OK");
		return ret;
	}
	
	
	public static boolean convertToRTLOTOS() {
		TURTLETranslator tt = new TURTLETranslator(tm);
		outputData = tt.generateRTLOTOS();
		return (outputData != null);
	}
	
	public static boolean convertToLOTOS() {
		TURTLETranslator tt = new TURTLETranslator(tm);
		outputData = tt.generateLOTOS(false);
		return (outputData != null);
	}
	
	public static boolean convertToUPPAAL() {
		TURTLE2UPPAAL tu = new TURTLE2UPPAAL(tm);
		UPPAALSpec spec = tu.generateUPPAAL(false, 10);
		outputData = spec.makeSpec();
		return (outputData != null);
	}
	
	public static boolean convertToJava() {
		System.out.println("Java conversion not yet implemented");
		return false;
	}
	
	public static boolean saveData() {
		try {
			 FileOutputStream fos = new FileOutputStream(outputFile);
             fos.write(outputData.getBytes());
             fos.close();
		} catch (Exception e) {
			System.out.println("Error when writing output file");
			return false;
		}
		return true;
	}
	
    public static void main(String[] args) {
		printCopyright();
		
		if (!checkArgs(args)) {
			printUsage();
			return;
		}
		
		if (!analyseArgs(args)) {
			printUsage();
			return;
		}
		
		if (!prepareFiles(args)) {
			printUsage();
			return;
		}
		
		if (!loadTIF()) {
			return;
		}
		
		boolean convert = false;
		switch(conversionType) {
		case 0:
			convert = convertToRTLOTOS();
			break;
		case 1:
			convert = convertToLOTOS();
			break;
		case 2:
			convert = convertToUPPAAL();
			break;
		case 3:
			convert = convertToJava();
			break;
		}
		
		if (!convert) {
			System.out.println("Error during conversion");
			return;
		}
		
		System.out.println("Conversion done");
		
		if (!saveData()) {
			return;
		}
		
		System.out.println("Specification written in " + outputFile.getName() + ": " + outputData.length() + " bytes");
        
	}
       
} // Class TIFTranslator

