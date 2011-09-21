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

In This Respect, The user's attention is drawn to the risks associated
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

public class DSEConfiguration implements Runnable  {
	
	private String errorMessage;
	
	private final String PATH_TO_CODE = "No directory selected for putting the generated code";
	private final String PATH_TO_RESULTS = "No directory selected for putting the results";
	private final String PATH_TO_SOURCE = "No source model selected";
	private final String NO_OUTPUT_SELECTED = "No format ofr the output has been selected";
	private final String LOAD_MAPPING_FAILED = "Loading of the mapping failed";
	private final String LOAD_TASKMODEL_FAILED = "Loading of the task model failed";
	private final String SIMULATION_COMPILATION_COMMAND_NOT_SET = "Compilation command missing";
	private final String SIMULATION_EXECUTION_COMMAND_NOT_SET = "Command to start simulation was noit set";
	private final String INVALID_ARGUMENT_NATURAL_VALUE = "The argument is execpted to a be natural value";
	
	private String simulationCompilationCommand = null;
	private String simulationExecutionCommand = null;
	
	private String pathToSimulator;
	private String pathToResults;
	
	
	private File mappingFile = null;
	private String modelPath = "";
	
	private File taskModelFile = null;
	
	private boolean outputVCD = false;
	private boolean outputHTML = false;
	private boolean outputTXT = false;
	private boolean outputXML = false;
	
	private boolean recordResults = false; 
	
	private boolean showSimulatorRawOutput = false;
	
	
	private TMLMapping tmap;
	private TMLModeling tmlm;
	
	private boolean optionChanged = true;
	
	private int simulationID = 0;
	private int resultsID = 0;
	private int dseID = 0;
	
	private int simulationExplorationMinimumCommand = 100;
	private int simulationExplorationMinimumBranch = 100;
	
	private int simulationMaxCycles = -1;
	
	private int nbOfSimulationThreads = 1;
	
	// DSE
	private int minNbOfCPUs = 1;
	private int maxNbOfCPUs = 2;
	private int minNbOfCoresPerCPU = 1;
	private int maxNbOfCoresPerCPU = 2;
	private int nbOfSimulationsPerMapping = 1;
	private TMLModeling taskModel = null;
	private Vector<TMLMapping> mappings;
	private DSEMappingSimulationResults dsemapresults;
	
	
	
	private DSESimulationResult results;
	
	private String simulationCmd;
	private int nbOfRemainingSimulation;
	private int totalNbOfSimulations;
	
	private int progression = 0;
	
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
	
	public int setTaskModelFile(String _fileName) {
		// Trying to read the file
		taskModelFile = new File(modelPath + _fileName);
		if (!FileUtils.checkFileForOpen(taskModelFile)) {
			optionChanged = true;
			taskModelFile = null;
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
	
	public int setOutputXML(String _value) {
		if (_value.toLowerCase().compareTo("true") == 0) {
			outputXML = true;
			optionChanged = true;
			return 0;
		}
		
		if (_value.toLowerCase().compareTo("false") == 0) {
			outputXML = false;
			optionChanged = true;
			return 0;
		}
		
		return -1;
	}
	
	public int setRecordResults(String _value) {
		if (_value.toLowerCase().compareTo("true") == 0) {
			recordResults = true;
			optionChanged = true;
			return 0;
		}
		
		if (_value.toLowerCase().compareTo("false") == 0) {
			recordResults = false;
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
	
	public int setNbOfSimulationThreads(String _value) {
		try {
			nbOfSimulationThreads = Integer.decode(_value).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (nbOfSimulationThreads < 1) {
			nbOfSimulationThreads = 1;
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		return 0;
	}
	
	public int setMinNbOfCPUs(String _value) {
		try {
			minNbOfCPUs = Integer.decode(_value).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (minNbOfCPUs < 1) {
			minNbOfCPUs = 1;
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		return 0;
	}
	
	public int setMaxNbOfCPUs(String _value) {
		try {
			maxNbOfCPUs = Integer.decode(_value).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (maxNbOfCPUs < 1) {
			maxNbOfCPUs = 1;
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		return 0;
	}
	
	public int setMinNbOfCoresPerCPU(String _value) {
		try {
			minNbOfCoresPerCPU = Integer.decode(_value).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (minNbOfCoresPerCPU < 1) {
			minNbOfCoresPerCPU = 1;
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		return 0;
	}
	
	public int setMaxNbOfCoresPerCPU(String _value) {
		try {
			maxNbOfCoresPerCPU = Integer.decode(_value).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (maxNbOfCoresPerCPU < 1) {
			maxNbOfCoresPerCPU = 1;
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		return 0;
	}
	
	public int setNbOfSimulationsPerMapping(String _value) {
		try {
			nbOfSimulationsPerMapping = Integer.decode(_value).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		if (nbOfSimulationsPerMapping < 1) {
			nbOfSimulationsPerMapping = 1;
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
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
			//System.out.println("Format OK");
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
			//System.out.println("TMLModeling=" + spec);
		}
		
		return true;
	}
	
	private boolean loadTaskModel(boolean _optimize) {
		boolean ret = false;
		//System.out.println("load");
		String inputData = FileUtils.loadFileData(taskModelFile);
		TMLTextSpecification tmlts = new TMLTextSpecification("LoadedTaskModel");
		ret = tmlts.makeTMLModeling(inputData);
		TraceManager.addDev("Load of task model done");
		ArrayList<TMLError> warnings;
		
		if (!ret) {
			TraceManager.addDev("Compilation:\n" + tmlts.printSummary());
		}
		
		if (ret) {
			//System.out.println("Format OK");
			taskModel = tmlts.getTMLModeling();
			
			//System.out.println("\n\n*** TML Modeling *** \n");
			//TMLTextSpecification textspec = new TMLTextSpecification("toto");
			//String s = textspec.toTextFormat(tmlm);
			//System.out.println(s);
			
			// Checking syntax
			TraceManager.addDev("--- Checking syntax of the whole specification (TML, TARCHI, TMAP)---");
			TMLSyntaxChecking syntax = new TMLSyntaxChecking(taskModel);
			syntax.checkSyntax();
			if (syntax.hasErrors() > 0) {
				TraceManager.addDev("Printing errors:");
				TraceManager.addDev(syntax.printErrors());
				return false;
			}
			

			TraceManager.addDev("Compilation:\n" + syntax.printSummary());
		
			TraceManager.addDev("Compilation:\n" + tmlts.printSummary());
			
			
			if (_optimize) {
				warnings = tmlm.optimize();
				TraceManager.addDev(taskModel.printSummary(warnings));
			}
			//spec.toTextFormat(tmlm);
			//System.out.println("TMLModeling=" + spec);
		}
		
		return true;
	}
	
	public int setSimulationExplorationMinimumCommand(String _arguments) {
		try {
			simulationExplorationMinimumCommand = Integer.decode(_arguments).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		return 0;
	}
	
	public int setSimulationExplorationMinimumBranch(String _arguments) {
		try {
			simulationExplorationMinimumBranch = Integer.decode(_arguments).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		return 0;
	}
	
	public int setSimulationMaxCycle(String _arguments) {
		try {
			simulationMaxCycles = Integer.decode(_arguments).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		return 0;
	}
	
	public int setShowSimulatorRawOutput(String _arguments) {
		if (_arguments.toLowerCase().compareTo("true") == 0) {
			showSimulatorRawOutput = true;
			return 0;
		}
		
		if (_arguments.toLowerCase().compareTo("false") == 0) {
			showSimulatorRawOutput = false;
			return 0;
		}
		
		return -1;
	}
	
	public int checkingSimulationElements() {
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
		
		return 0;
	}
	
	public int loadingModel(boolean _debug, boolean _optimize) {
		if (optionChanged) {
			TraceManager.addDev("Loading mapping");
			if (!loadMapping(_optimize)) {
				errorMessage = LOAD_MAPPING_FAILED;
				TraceManager.addDev("Loading of the mapping failed!!!!");
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
		return 0;
	}
	
	public int loadingTaskModel(boolean _debug, boolean _optimize) {
		if (optionChanged) {
			TraceManager.addDev("Loading mapping");
			if (!loadTaskModel(_optimize)) {
				errorMessage = LOAD_TASKMODEL_FAILED;
				TraceManager.addDev("Loading of the taks model failed!!!!");
				return -1;
			}
		}
		return 0;
	}
	
	public int generateAndCompileMappingCode(TMLMapping _tmlmap, boolean _debug, boolean _optimize) {
		
		// Generating code
		TraceManager.addDev("\n\n\n**** Generating simulation code from mapping...");
		TML2MappingSystemC map = new TML2MappingSystemC(_tmlmap);
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
		
		return 0;
	}
	
	String prepareCommand() {
		String cmd;
		
		cmd = pathToSimulator + simulationExecutionCommand;
		
		Vector<String> v = new Vector<String>();
		
		if (outputVCD) {
			v.add("7 0 " + pathToResults + "output$.vcd");
		}
		if (outputHTML) {
			v.add("7 1 " + pathToResults + "output$.html");
		}
		if (outputTXT) {
			v.add("7 2 " +pathToResults + "output$.txt");
		}
		
		if (simulationMaxCycles > -1) {
			v.add("1 5 " + simulationMaxCycles);
		} else {
			v.add("1 0 ");
		}
		
		if (recordResults) {
			v.add("10 1 " + pathToResults + "benchmark$.xml");
		}
		
		if (v.size() > 0) {
			int cpt = 0;
			for (String s: v) {
				if (cpt == 0) {
					cmd += " -cmd \"";
				} else {
					cmd += ";";
				}
				cmd += s;
				cpt ++;
			}
			cmd += "\"";
		}
		
		if (outputXML) {
			cmd += " -oxml " + pathToResults + "output$.xml";
		}
		
		return cmd;
	}
	
	public String putSimulationNbInCommand(String cmd, int value) {
		String val = "" + value;
		return Conversion.replaceAllString(cmd, "$", val);
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
		
		int nbconfigured =  nbOfSimulations;
		
		// Checking simulation Elements
		int ret = checkingSimulationElements();
		if (ret != 0) {
			return ret;
		}
		
		// Preparing results
		if (recordResults) {
			if (results == null) {
				results = new DSESimulationResult();
			}
		}
		
		// Loading model
		ret = loadingModel(_debug, _optimize);
		if (ret != 0) {
			return ret;
		}
		
		// Executing the simulation
		String cmd = prepareCommand();
		String tmp;
		
		long t0 = System.currentTimeMillis();
		double t;
		double r;
		t = nbconfigured;
		
		while(nbOfSimulations >0) {
			tmp = putSimulationNbInCommand(cmd, simulationID);
			r = nbOfSimulations;
			progression = (int)(((t-r)/t)*100);
			
			makeCommand(tmp);
			
			if (recordResults) {
				if (loadSimulationResult(simulationID) <0) {
					return -1;
				}
			}
			simulationID ++;
			nbOfSimulations --;
		}
		
		long t1 = System.currentTimeMillis();
		
		if (recordResults) {
			long l0 = (int)(Math.ceil((t1 - t0) / 1000)); 
			long l1 = (t1-t0) - (l0 * 1000);
			results.addComment("#Set of " + nbconfigured + " simulations executed in " + l0 + "." + l1  + " s");
		}
		return 0;
	}
	
	public synchronized int increaseSimulationID() {
		int tmp = simulationID;
		simulationID ++;
		return tmp;
	}
	
	public int runParallelSimulation(String _arguments, boolean _debug, boolean _optimize) {
		// Checking for valid arguments
		try {
			nbOfRemainingSimulation = Integer.decode(_arguments).intValue();
		} catch (Exception e) {
			errorMessage = INVALID_ARGUMENT_NATURAL_VALUE;
			return -1;
		}
		
		int nbconfigured = nbOfRemainingSimulation;
		totalNbOfSimulations = nbconfigured;
		
		// Checking simulation Elements
		int ret = checkingSimulationElements();
		if (ret != 0) {
			return ret;
		}
		
		// Loading model
		ret = loadingModel(_debug, _optimize);
		if (ret != 0) {
			return ret;
		}
		
		// Preparing results
		if (recordResults) {
			if (results == null) {
				results = new DSESimulationResult();
			}
		}
		
		// Executing the simulation
		simulationCmd = prepareCommand();
		
		long t0 = System.currentTimeMillis();
		
		Thread [] t = null;
		int nb = nbOfSimulationThreads;
		if (nb > 1) {
			t = new Thread[nb-1];
			while(nb > 1) {
				t[nbOfSimulationThreads-nb] = new Thread(this);
				t[nbOfSimulationThreads-nb].start();
				nb --;
			}
		}
		
		run();
		
		// Must wait for all threads to terminate
		if (nbOfSimulationThreads > 1) {
			nb = nbOfSimulationThreads;
			
			while(nb > 1) {
				try {
					t[nbOfSimulationThreads-nb].join();
					nb --;
				} catch (Exception e) {
				}
			}
		}
		
		long t1 = System.currentTimeMillis();
		
		if (recordResults) {
			long l0 = (int)(Math.ceil((t1 - t0) / 1000)); 
			long l1 = (t1-t0) - (l0 * 1000);
			results.addComment("#Set of " + nbconfigured + " parallel simulations executed in " + l0 + "." + l1  + " s");
		}
		
		return 0;
	}
	
	public void run() {
		
		TraceManager.addDev("Thread thread");
		String tmp;
		int id;
		
		while(hasRemainingSimulations() > 0) {
			id = increaseSimulationID();
			tmp = putSimulationNbInCommand(simulationCmd, id); 
			makeCommand(tmp);
			
			if (recordResults) {
				if (loadSimulationResult(id) <0) {
					return;
				}
			}
		}
	}
	
	private synchronized int hasRemainingSimulations() {
		double total = (double)totalNbOfSimulations;
		double remain = (double)nbOfRemainingSimulation;
		progression = (int)(((total - remain)/total)*100);
		
		//System.out.println("progression = " + progression + " total=" + total +  " remain=" + remain);
		
		if (nbOfRemainingSimulation == 0) {
			return 0;
		}
		
		int tmp = nbOfRemainingSimulation;
		nbOfRemainingSimulation --;
		return tmp;
	}
	
	public int printAllResults(String _arguments, boolean _debug, boolean _optimize) {
		TraceManager.addDev("Printing all results");
		String sres;
		DSESimulationResult res;
		
		if (dsemapresults != null) {
			int cpt = resultsID - dsemapresults.nbOfElements();
			for(int i=0; i<dsemapresults.nbOfElements(); i++) {
				res = dsemapresults.getResults(i);
				try {
					sres =  res.getAllExplanationHeader() + "\n";
					sres += "#Mapping description: " + dsemapresults.getMapping(i).getSummaryTaskMapping() + "\n";
					sres += res.getAllComments() + "\n" + res.getAllResults();
					FileUtils.saveFile(pathToResults + "alldseresults_mapping" + cpt + ".txt", sres);
				} catch (Exception e){
					TraceManager.addDev("Error when saving results file" + e.getMessage());
					return -1;
					
				}
				cpt ++;
			}
			
			
			
			
		} else {
			
			if (results == null) {
				TraceManager.addDev("No results");
				return -1;
			}
			
			
			// Must compute results
			//results.computeResults();
			
			//TraceManager.addDev("Results: #" + resultsID + "\n" +  results.getWholeResults());
			
			// Saving to file
			try {
				TraceManager.addDev(results.getAllExplanationHeader());
				TraceManager.addDev("----\n" + results.getAllResults());
				FileUtils.saveFile(pathToResults + "allresults" + resultsID + ".txt", results.getAllExplanationHeader() + "\n" + results.getAllComments() + "\n" + results.getAllResults());
			} catch (Exception e){
				TraceManager.addDev("Error when saving results file" + e.getMessage());
				return -1;
				
			}
		}
		return 0;
	}
	
	public int printResultsSummary(String _arguments, boolean _debug, boolean _optimize) {
		TraceManager.addDev("Computing results");
		
		String sres;
		DSESimulationResult res;
		
		if (dsemapresults != null) {
			int cpt = resultsID - dsemapresults.nbOfElements();
			dsemapresults.computeSummaryResult();
			for(int i=0; i<dsemapresults.nbOfElements(); i++) {
				
				res = dsemapresults.getResults(i);
				try {
					sres =  res.getAllExplanationHeader() + "\n";
					sres += "#Mapping description: " + dsemapresults.getMapping(i).getSummaryTaskMapping() + "\n";
					sres += res.getAllComments() + "\n" + res.getWholeResults();
					FileUtils.saveFile(pathToResults + "summary_dseresults_ofmapping" + cpt + ".txt", sres);
				} catch (Exception e){
					TraceManager.addDev("Error when saving results file" + e.getMessage());
					return -1;
					
				}
				cpt ++;
			}
			StringBuffer sb = new StringBuffer("# Overall results\n");
			sb.append("#Mappings:\n" + dsemapresults.getDescriptionOfAllMappings() + "\n\n");
			
			
			sb.append("\nCPUs:\n");
			sb.append("Mapping with Highest min CPU Usage: " + dsemapresults.getMappingWithHighestMinCPUUsage() + "\n");
			sb.append("Mapping with Lowest min CPU Usage: " + dsemapresults.getMappingWithLowestMinCPUUsage() + "\n");
			
			sb.append("Mapping with Highest Average CPU Usage: " + dsemapresults.getMappingWithHighestAverageCPUUsage() + "\n");
			sb.append("Mapping with Lowest Average CPU Usage: " + dsemapresults.getMappingWithLowestAverageCPUUsage() + "\n");
			
			sb.append("Mapping with Highest max CPU Usage: " + dsemapresults.getMappingWithHighestMaxCPUUsage() + "\n");
			sb.append("Mapping with Lowest max CPU Usage: " + dsemapresults.getMappingWithLowestMaxCPUUsage() + "\n");
			
			
			sb.append("\nBus:\n");
			sb.append("Mapping with Highest min Bus Usage: " + dsemapresults.getMappingWithHighestMinBusUsage() + "\n");
			sb.append("Mapping with Lowest min Bus Usage: " + dsemapresults.getMappingWithLowestMinBusUsage() + "\n");
			
			sb.append("Mapping with Highest Average Bus Usage: " + dsemapresults.getMappingWithHighestAverageBusUsage() + "\n");
			sb.append("Mapping with Lowest Average Bus Usage: " + dsemapresults.getMappingWithLowestAverageCPUUsage() + "\n");
			
			sb.append("Mapping with Highest max Bus Usage: " + dsemapresults.getMappingWithHighestMaxBusUsage() + "\n");
			sb.append("Mapping with Lowest max Bus Usage: " + dsemapresults.getMappingWithLowestMaxBusUsage() + "\n");
			
			sb.append("\nContentions:\n");
			sb.append("Mapping with Highest min bus contention: " + dsemapresults.getMappingWithHighestMinBusContention() + "\n");
			sb.append("Mapping with Lowest min Bus contention: " + dsemapresults.getMappingWithLowestMinBusContention() + "\n");
			
			sb.append("Mapping with Highest Average Bus contention: " + dsemapresults.getMappingWithHighestAverageBusUsage() + "\n");
			sb.append("Mapping with Lowest Average Bus contention: " + dsemapresults.getMappingWithLowestAverageCPUUsage() + "\n");
			
			sb.append("Mapping with Highest max Bus contention: " + dsemapresults.getMappingWithHighestMaxBusUsage() + "\n");
			sb.append("Mapping with Lowest max Bus contention: " + dsemapresults.getMappingWithLowestMaxBusUsage() + "\n");
			
			try {
				FileUtils.saveFile(pathToResults + "Overall_results_AllMappings_From_" + resultsID + ".txt", sb.toString());
			} catch (Exception e){
					TraceManager.addDev("Error when saving results file" + e.getMessage());
					return -1;
					
				}
			return 0;
			
		} else {
			
			if (results == null) {
				TraceManager.addDev("No results");
				return -1;
			}
			
			// Must compute results
			results.computeResults();
			
			TraceManager.addDev("Results: #" + resultsID + "\n" +  results.getWholeResults());
			
			// Saving to file
			try {
				FileUtils.saveFile(pathToResults + "summary" + resultsID + ".txt", results.getExplanationHeader() + "\n" + results.getAllComments() + "\n" + results.getWholeResults());
			} catch (Exception e){
				TraceManager.addDev("Error when saving results file");
				return -1;
				
			}
			
			return 0;
		}
	}
	
	public int resetResults(String _arguments) {
		if (results == null) {
			return 0;
		}
		
		// Reinit results
		results.reset();
		
		resultsID ++;
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
		String cmd = pathToSimulator + simulationExecutionCommand + " -cmd \"1 7 " +simulationExplorationMinimumCommand + " " + simulationExplorationMinimumBranch + "\"  -gpath " + pathToResults;
		
		makeCommand(cmd);
		simulationID ++;
		
		return 0;
	}
	
	public int runDSE(String _arguments, boolean _debug, boolean _optimize) {
		int nbOfSimulations;
	
		if (nbOfSimulationsPerMapping < 1) {
			nbOfSimulationsPerMapping = 1;
		}
		
		// Checking simulation Elements
		int ret = checkingSimulationElements();
		if (ret != 0) {
			return ret;
		}
		
		// Checking simulation Elements
		ret = checkingSimulationElements();
		if (ret != 0) {
			return ret;
		}
		
		// Must generate all possible mappings.
		// First : load the task model
		if (!loadTaskModel(_optimize)) {
			TraceManager.addDev("Could not load the task model");
			return -1;
		}
		
		TraceManager.addDev("Task model loaded");
		
		mappings = generateAllMappings(taskModel);
		
		if (mappings != null) {
			TraceManager.addDev("Mapping generated");
		} else {
			TraceManager.addDev("Mapping failure");
		}
		
		int cpt = 0;
		/*for(TMLMapping tmla: mappings) {
			TraceManager.addDev("map " + cpt + ": " + tmla.getSummaryTaskMapping());
			cpt ++;
		}*/
		
		// For each maping, generate the simulation code
		cpt = 0;
		if (recordResults) {
			if (dsemapresults == null) {
				dsemapresults = new DSEMappingSimulationResults();
			}
		}
		
		for(TMLMapping tmla: mappings) {
			TraceManager.addDev("Handling mapping #" + cpt);
			progression = (int)(cpt * 100 / (mappings.size()));
			
			cpt ++;
			
			if (generateAndCompileMappingCode(tmla, _debug, _optimize)  >= 0) {
				if (recordResults) {
					results = new DSESimulationResult();
					resultsID ++;
				}
				
				//System.out.println("After Current TML Mapping: " + tmla.getSummaryTaskMapping());
				
				dsemapresults.addElement("Mapping #" + (cpt-1), results, tmla);
				nbOfSimulations = nbOfSimulationsPerMapping;
				// Executing the simulation
				String cmd = prepareCommand();
				String tmp;
				
				long t0 = System.currentTimeMillis();
				
				while(nbOfSimulations >0) {
					tmp = putSimulationNbInCommand(cmd, simulationID);
					TraceManager.addDev("Executing: " + tmp);
					makeCommand(tmp);
					
					if (recordResults) {
						if (loadSimulationResult(simulationID) <0) {
							return -1;
						}
					}
					simulationID ++;
					nbOfSimulations --;
				}
			} else {
				return -1;
			}
		}	
		return 0;
	}
	
	public void makeCommand(String cmd) {
		String str = null;
		BufferedReader proc_in, proc_err;
        //PrintStream out = null;
        
        try {
            //TraceManager.addDev("Going to start command " + cmd);
			
			ProcessBuilder pb = new ProcessBuilder(constructCommandList(cmd));
			Map<String, String> env = pb.environment();
			java.lang.Process proc = pb.start();
            
            proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            
            //et = new ErrorThread(proc_err, mpi);
            //et.start();
            
            while ((str = proc_in.readLine()) != null){
            	if (showSimulatorRawOutput) {
                System.out.println("Out " + str);
                }
                //mpi.appendOut(str+"\n");             
            }
            
            //et.stopProcess();
            
        } catch (Exception e) {
            TraceManager.addDev("Exception [" + e.getMessage() + "] occured when executing " + cmd);
        }
        TraceManager.addDev("Ending command " + cmd);
		
	}
	
	public List<String> constructCommandList(String _cmd) {
		Vector<String> list = new Vector<String>();
		_cmd = _cmd.trim();
		char c;
		String current = "";
		boolean inQuote0 = false;
		boolean inQuote1 = false;
		
		//TraceManager.addDev("Making list from command : " + _cmd);
		
		for(int i=0; i<_cmd.length(); i++) {
			c = _cmd.charAt(i);
			
			if ((c == ' ') && (!inQuote0) && (!inQuote1)){
				//TraceManager.addDev("Adding " + current);
				list.add(current);
				current = "";
			} else if (c == '\'') {
				inQuote1 = !inQuote1;
			} else if (c == '\"') {
				inQuote0 = !inQuote0;
			} else {
				current += c;
			}
			
		}
		
		if (current.length() > 0) {
			list.add(current);
			//TraceManager.addDev("Adding " + current);
		}
		
		//TraceManager.addDev("List done\n");
		
		return (List)list;
		
	}
	
	public void oldMakeCommand(String cmd) {
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
	
	public int loadSimulationResult(int id) {
		results.loadResultFromXMLFile(pathToResults + "benchmark" + id + ".xml");
		
		return 0;
	}
	
	public Vector<TMLMapping> generateAllMappings(TMLModeling _tmlm) {
		TraceManager.addDev("Generate all mappings");
		if (_tmlm == null) {
			TraceManager.addDev("Null mapping");
			return null;
		}
		
		// At least one CPU as a min, and at least one task per CPU
		int nbOfTasks = _tmlm.getTasks().size();
		TraceManager.addDev("Nb of tasks:" + nbOfTasks);
		
		if (nbOfTasks == 0) {
			return null;
		}
		
		int min = Math.max(1, minNbOfCPUs);
		int max = Math.min(nbOfTasks, maxNbOfCPUs);
		
		if (max <= min) {
			max = min + 1;
		}
		
		Vector<TMLMapping> maps = new  Vector<TMLMapping>();
		
		for(int cpt=min; cpt<max; cpt++) {
			dseID = 0;
			TraceManager.addDev("Generating mapping for nb of cpu = " + cpt);
			generateMappings(_tmlm, maps, cpt);
			TraceManager.addDev("Mappings generated for nb of cpu = " + cpt);
		}
		
		computeCoresOfMappings(maps);
		
		TraceManager.addDev("Mapping generated: " + maps.size());
		
		return maps;
	}
	
	private void generateMappings(TMLModeling _tmlm, Vector<TMLMapping> maps, int nbOfCPUs) {
		ArrayList<TMLTask> tasks = _tmlm.getTasks();
		CPUWithTasks cpus_tasks[] = new CPUWithTasks[nbOfCPUs];
		
		TraceManager.addDev("Nb of cpus = " + nbOfCPUs);
		
		
		for(int i=0; i<nbOfCPUs; i++) {
			cpus_tasks[i] = new CPUWithTasks();
		}
		
		// We first put the first task on a CPU
		TMLTask t = tasks.get(0);
		cpus_tasks[0].addTask(t);
		
		// We build a vector of remaining tasks
		Vector<TMLTask> vtasks = new Vector<TMLTask>();
		for(TMLTask task: tasks) {
			if (task != t) {
				vtasks.add(task);
			}
		}
		
		// Computing mappings
		computeMappings(vtasks, cpus_tasks, maps, _tmlm);
		

		
		TraceManager.addDev("Nb of computed mappings:" + maps.size());
	}
	
	private void computeMappings(Vector<TMLTask> remainingTasks, CPUWithTasks[] cpus_tasks,  Vector<TMLMapping> maps, TMLModeling _tmlm) {
		if (remainingTasks.size() == 0) {
			// Can generate the mapping from cpus_tasks
			makeMapping(cpus_tasks, maps, _tmlm);
			return;
		}
		
		// At least one task to map.
		// We select the first task
		TMLTask t = remainingTasks.get(0);
		remainingTasks.remove(t);
		
		TraceManager.addDev("Mapping task: " + t.getName());
		
		// Two solutions: either it is mapped on the first free CPU, or it is mapped on an already occupied CPU
		// Memo: all cpus must have at least on task at the end
		
		// Must it be mapped a free CPU?
		if (nbOfFreeCPUs(cpus_tasks) >= (remainingTasks.size()+1)) {
			// The task must be mapped on a free CPU
			// Search for the first free CPU
			TraceManager.addDev("The following task must be mapped on a free CPU: " + t.getName());
			for(int i=0; i<cpus_tasks.length; i++) {
				if (cpus_tasks[i].getNbOfTasks() == 0) {
					cpus_tasks[i].addTask(t);
					computeMappings(remainingTasks, cpus_tasks, maps, _tmlm);
					cpus_tasks[i].removeTask(t);
					remainingTasks.add(t);
					return;
				}
			}
			TraceManager.addDev("Task could not be mapped on a free CPU: " + t.getName());
		}
		
		TraceManager.addDev("Regular mapping of: " + t.getName());
		// It can be mapped on whatever CPU, until the first free one has been met (the first free CPU is inclusive)
		remainingTasks.remove(t);
		for(int i=0; i<cpus_tasks.length; i++) {
			cpus_tasks[i].addTask(t);
			TraceManager.addDev("Mapping " + t.getName() + " on CPU #" + i);
			computeMappings(remainingTasks, cpus_tasks, maps, _tmlm);
			TraceManager.addDev("Removing  " + t.getName() + " from CPU #" + i);
			cpus_tasks[i].removeTask(t);
			if (cpus_tasks[i].getNbOfTasks() == 0) {
				TraceManager.addDev("Stopping mapping since  of" + t.getName() + " since CPU #" + i +  " is free");
				remainingTasks.add(t);
				return;
			}
		}
		remainingTasks.add(t);
		
	}
	
	private void makeMapping(CPUWithTasks[] cpus_tasks,  Vector<TMLMapping> maps, TMLModeling _tmlm) {
		TMLArchitecture tmla = new TMLArchitecture();
		TMLMapping tmap = new TMLMapping(_tmlm, tmla, true);
		DIPLOElement.setGeneralID(_tmlm.computeMaxID() + 1);
		
		HwCPU cpu;
		
		for(int i=0; i<cpus_tasks.length; i++) {
			cpu = new HwCPU("CPU__" + (cpus_tasks.length + 1) + "_" + dseID + "_" + (i+1));
			tmla.addHwNode(cpu);
			for(TMLTask t: cpus_tasks[i].getTasks()) {
				tmap.addTaskToHwExecutionNode(t, cpu);
			}
		}
		dseID ++;
		
		maps.add(tmap);
	}
	
	private int nbOfFreeCPUs(CPUWithTasks[] cpus_tasks) {
		int nb = 0;
		for(int i=0; i<cpus_tasks.length; i++) {
			if (cpus_tasks[i].getNbOfTasks() == 0) {
				nb ++;
			}
		}
		return nb;
	}
	
	private void computeCoresOfMappings(Vector<TMLMapping> maps) {
	}
	
	public void resetProgression() {
		progression = 0;
	}
	
	public int getProgression() {
		return progression;
	}
	
	
	
	
} // Class DSEConfiguration

