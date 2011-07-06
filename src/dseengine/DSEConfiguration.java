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
* Class DSEScriptReader
* Reader of script for Design Space Exploration
* Creation: 28/06/2011
* @version 1.0 28/06/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

import java.io.*;
import java.util.*;

import tmltranslator.*;
//import tmltranslator.touppaal.*;
//import tmltranslator.tomappingsystemc.*;
import tmltranslator.tomappingsystemc2.*;
//import tmltranslator.toturtle.*;

import translator.*;

import dseengine.*;

import myutil.*;


//import uppaaldesc.*;

public class DSEConfiguration  {
	
	private String errorMessage;
	
	private final String PATH_TO_CODE = "No directory selected for putting the generated code";
	private final String PATH_TO_RESULTS = "No directory selected for putting the results";
	private final String PATH_TO_SOURCE = "No source model selected";
	private final String NO_OUTPUT_SELECTED = "No format ofr the output has been selected";
	private final String LOAD_MAPPING_FAILED = "Loading of the mapping failed";
	private final String SIMULATION_COMPILATION_COMMAND_NOT_SET = "Compilation command missing";
	private final String SIMULATION_EXECUTION_COMMAND_NOT_SET = "Command to start simulation was noit set";
	private final String INVALID_ARGUMENT_NATURAL_VALUE = "The argument is execpted to a be natural value";
	
	private String simulationCompilationCommand = null;
	private String simulationExecutionCommand = null;
	
	private String pathToSimulator;
	private String pathToResults;
	
	
	private File mappingFile = null;
	private String modelPath = "";
	
	private boolean outputVCD = false;
	private boolean outputHTML = false;
	private boolean outputTXT = false;
	
	private TMLMapping tmap;
	private TMLModeling tmlm;
	
	private boolean optionChanged = true;
	
	private int simulationID = 0;
	
	
	
	//private int nbOfSimulations;
	
	public  DSEConfiguration() {
		
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public int setModelPath(String _path) {
		// Trying to read the file
		modelPath = _path;
		optionChanged = true;
		
		return 0;
	}
	
	public int setMappingFile(String _fileName) {
		// Trying to read the file
		mappingFile = new File(modelPath + _fileName);
		if (!FileUtils.checkFileForOpen(mappingFile)) {
			optionChanged = true;
			mappingFile = null;
			return -1;
		}
		
		
		return 0;
	}
	
	public int setOutputVCD(String _value) {
		if (_value.toLowerCase().compareTo("true") == 0) {
			outputVCD = true;
			optionChanged = true;
			return 0;
		}
		
		if (_value.toLowerCase().compareTo("false") == 0) {
			outputVCD = false;
			optionChanged = true;
			return 0;
		}
		
		return -1;
	}
		
	public int setOutputHTML(String _value) {
		if (_value.toLowerCase().compareTo("true") == 0) {
			outputHTML = true;
			optionChanged = true;
			return 0;
		}
		
		if (_value.toLowerCase().compareTo("false") == 0) {
			outputHTML = false;
			optionChanged = true;
			return 0;
		}
		
		return -1;
	}
		
	public int setOutputTXT(String _value) {
		if (_value.toLowerCase().compareTo("true") == 0) {
			outputTXT = true;
			optionChanged = true;
			return 0;
		}
		
		if (_value.toLowerCase().compareTo("false") == 0) {
			outputTXT = false;
			optionChanged = true;
			return 0;
		}
		
		return -1;
	}
	
	public int setPathToSimulator(String _value) {
		pathToSimulator = _value;
		optionChanged = true;
		return 0;
	}
	
	public int setPathToResults(String _value) {
		pathToResults = _value;
		optionChanged = true;
		return 0;
	}
	
	public int setSimulationCompilationCommand(String _value) {
		simulationCompilationCommand = _value;
		optionChanged = true;
		return 0;
	}
		
	public int setSimulationExecutionCommand(String _value) {
		simulationExecutionCommand = _value;
		optionChanged = true;
		return 0;
	}
	
	private boolean loadMapping(boolean _optimize) {
		boolean ret = false;
		//System.out.println("load");
		String inputData = FileUtils.loadFileData(mappingFile);
		TMLMappingTextSpecification spec = new TMLMappingTextSpecification("LoadedSpecification");
		ret = spec.makeTMLMapping(inputData, modelPath);
		TraceManager.addDev("load ended");
		ArrayList<TMLError> warnings;
		
		if (!ret) {
			TraceManager.addDev("Compilation:\n" + spec.printSummary());
		}
		
		if (ret) {
			System.out.println("Format OK");
			tmap = spec.getTMLMapping(); 
			tmlm = tmap.getTMLModeling();
			
			//System.out.println("\n\n*** TML Modeling *** \n");
			//TMLTextSpecification textspec = new TMLTextSpecification("toto");
			//String s = textspec.toTextFormat(tmlm);
			//System.out.println(s);
			
			// Checking syntax
			TraceManager.addDev("--- Checking syntax of the whole specification (TML, TARCHI, TMAP)---");
			TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
			syntax.checkSyntax();
			if (syntax.hasErrors() > 0) {
				TraceManager.addDev("Printing errors:");
				TraceManager.addDev(syntax.printErrors());
				return false;
			}
			

			TraceManager.addDev("Compilation:\n" + syntax.printSummary());
		
			TraceManager.addDev("Compilation:\n" + spec.printSummary());
			
			
			if (_optimize) {
				warnings = tmlm.optimize();
				TraceManager.addDev(tmlm.printSummary(warnings));
			}
			//spec.toTextFormat(tmlm);
			System.out.println("TMLModeling=" + spec);
		}
		
		return true;
	}
	
	public int runSimulation(String _arguments, boolean _debug, boolean _optimize) {
		
		// Checking for valid arguments
		int nbOfSimulations;
		try {
			nbOfSimulations = Integer.decode(_arguments).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (pathToSimulator == null) {
			errorMessage = PATH_TO_CODE;
			return -1;
		}
		
		if (pathToResults == null) {
			errorMessage = PATH_TO_RESULTS;
			return -1;
		}
		
		if (mappingFile == null) {
			errorMessage = PATH_TO_SOURCE;
			return -1;
		}
		
		if (!outputVCD && !outputHTML && !outputTXT) {
			errorMessage = NO_OUTPUT_SELECTED;
			return -1;
		}
		
		if (simulationCompilationCommand == null) {
			errorMessage = SIMULATION_COMPILATION_COMMAND_NOT_SET;
			return -1;
		}
		
		
		if (simulationExecutionCommand == null) {
			errorMessage = SIMULATION_EXECUTION_COMMAND_NOT_SET;
			return -1;
		}
		
		
		// Loading model
		
		if (optionChanged) {
			TraceManager.addDev("Loading mapping");
			if (!loadMapping(_optimize)) {
				errorMessage = LOAD_MAPPING_FAILED;
				TraceManager.addDev("Loading of the mapping faild!!!!");
				return -1;
			}
		
			// Generating code
			TraceManager.addDev("\n\n\n**** Generating simulation code...");
			TML2MappingSystemC map = new TML2MappingSystemC(tmap);
			try {
				map.generateSystemC(_debug, _optimize);
				map.saveFile(pathToSimulator, "appmodel");
			} catch (Exception e) {
				TraceManager.addDev("SystemC generation failed: " + e + " msg=" + e.getMessage());
				e.printStackTrace();
				return -1;
			}
			
			// Compiling the code
			makeCommand(simulationCompilationCommand + " " + pathToSimulator);
			
			optionChanged = false;
		}
		
		
		// Executing the simulation
		String cmd;
		while(nbOfSimulations >0) {
			cmd = pathToSimulator + simulationExecutionCommand;
			if (outputVCD) {
				cmd += " -ovcd " + pathToResults + "output" + simulationID + ".vcd";
			}
			if (outputHTML) {
				cmd += " -ohtml " + pathToResults + "output" + simulationID + ".html";
			}
			if (outputTXT) {
				cmd += " -otxt " + pathToResults + "output" + simulationID + ".txt";
			}
			makeCommand(cmd);
			simulationID ++;
			nbOfSimulations --;
		}
		return 0;
	}
	
	public int runExplo(String _arguments, boolean _debug, boolean _optimize) {
		
		// Checking for valid arguments
		/*int nbOfSimulations;
		try {
			nbOfSimulations = Integer.decode(_arguments).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}*/
		
		if (pathToSimulator == null) {
			errorMessage = PATH_TO_CODE;
			return -1;
		}
		
		if (pathToResults == null) {
			errorMessage = PATH_TO_RESULTS;
			return -1;
		}
		
		if (mappingFile == null) {
			errorMessage = PATH_TO_SOURCE;
			return -1;
		}
		
		/*if (!outputVCD && !outputHTML && !outputTXT) {
			errorMessage = NO_OUTPUT_SELECTED;
			return -1;
		}*/
		
		if (simulationCompilationCommand == null) {
			errorMessage = SIMULATION_COMPILATION_COMMAND_NOT_SET;
			return -1;
		}
		
		
		if (simulationExecutionCommand == null) {
			errorMessage = SIMULATION_EXECUTION_COMMAND_NOT_SET;
			return -1;
		}
		
		
		// Loading model
		// Generating code
		if (optionChanged) {
			TraceManager.addDev("Loading mapping");
			if (!loadMapping(_optimize)) {
				errorMessage = LOAD_MAPPING_FAILED;
				TraceManager.addDev("Loading of the mapping faild!!!!");
				return -1;
			}
		
		
			TraceManager.addDev("\n\n\n**** Generating simulation code...");
			TML2MappingSystemC map = new TML2MappingSystemC(tmap);
			try {
				map.generateSystemC(_debug, _optimize);
				map.saveFile(pathToSimulator, "appmodel");
			} catch (Exception e) {
				TraceManager.addDev("SystemC generation failed: " + e + " msg=" + e.getMessage());
				e.printStackTrace();
				return -1;
			}
			
			// Compiling the code
			makeCommand(simulationCompilationCommand + " " + pathToSimulator);
			
			optionChanged = false;
		}
		
		
		// Executing the simulation
		String cmd = pathToSimulator + simulationExecutionCommand + " -explo -gpath " + pathToResults;
		
		makeCommand(cmd);
		simulationID ++;
		
		return 0;
	}
	
	public void makeCommand(String cmd) {
		String str = null;
		BufferedReader proc_in, proc_err;
        //PrintStream out = null;
        
        try {
            TraceManager.addDev("Going to start command " + cmd);
            
            java.lang.Process proc = Runtime.getRuntime().exec(cmd);
            
            proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            
            //et = new ErrorThread(proc_err, mpi);
            //et.start();
            
            while ((str = proc_in.readLine()) != null){    
                TraceManager.addDev("Out " + str);
                //mpi.appendOut(str+"\n");             
            }
            
            //et.stopProcess();
            
        } catch (Exception e) {
            TraceManager.addDev("Exception [" + e.getMessage() + "] occured when executing " + cmd);
        }
        TraceManager.addDev("Ending command " + cmd);
		
	}
	
	
	
	
	
} // Class DSEConfiguration

