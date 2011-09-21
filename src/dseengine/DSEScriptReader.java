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
* Creation: 24/06/2011
* @version 1.0 24/06/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

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

public class DSEScriptReader implements Runnable {
	public static final int SYNTAX_ERROR_IN_LINE = 3;
	public static final int OK = 1;
	public static final int FILE_ERROR = 2;
	public static final int KO = 4;
	public static final int ERROR_WHEN_RUNNING = 5;
	
	private static String[] commands = {"MappingFile", "SimulationOutputVCD", 
	"PathToSimulator", "PathToResults", //2, 3
	"RunSimulation", "ModelPath", // 4, 5
	"simulationCompilationCommand", "simulationExecutionCommand", // 6, 7
	"SimulationOutputHTML", "SimulationOutputTxt", // 8, 9
	"SimulationExplorationMinimumCommand", "SimulationExplorationMinimumBranch", // 10, 11 
	"RunExplo", "SimulationMaxCycle", //12, 13
	"RecordResults", "saveAllResults", //14, 15
	"SimulationOutputXML", "saveResultsSummary", // 16, 17
	"resetResults", "NbOfSimulationThreads", // 18, 19
	"RunParallelSimulation", "ShowSimulatorRawOutput", //20, 21
	"TaskModelFile", "MinNbOfCPUs", //22, 23
	"MaxNbOfCPUs", "NbOfSimulationsPerMapping", //24, 25
	"runDSE", "MinNbOfCoresPerCPU", // 26, 27
	"MaxbOfCoresPerCPU"//28
	};
	
	private static int step = 0;
	private static String[] steps = {"-", "\\", "|", "/"};
	private static char doneChar = '*';
	
	private static boolean linePrinted = false;
	
	private String fileName;
	private int lineOfError;
	
	private boolean debug;
	private boolean optimize;
	
	private DSEConfiguration config;

	public  DSEScriptReader(String _fileName) {
		fileName = _fileName;
	}
	
	public void setDebug(boolean _debug) {
		debug = _debug;
	}
	
	public void setOptimize(boolean _optimize) {
		optimize = _optimize;
	}
	
	
	// Return an eventual error
	// OK: all ok
	// FILE_ERROR: could not read file
	// KO:
	
	public int execute() {
		
		if (debug) {
			TraceManager.devPolicy = TraceManager.TO_CONSOLE;
		} else {
			TraceManager.devPolicy = TraceManager.TO_DEVNULL;
		}
		
		String scriptToExecute = "";
		try {
			scriptToExecute = FileUtils.loadFile(fileName); 
		} catch (FileException e) {
			return FILE_ERROR;
		}
		
		
		
		// Read the script line by line, and execute corresponding actions
		StringReader sr = new StringReader(scriptToExecute);
        BufferedReader br = new BufferedReader(sr);
        String s;
		config = new DSEConfiguration();
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
		
		int line = 0;
		int ret;
		
		try {
            while((s = br.readLine()) != null) {
				line ++;
                s = s.trim();
				if (s.startsWith("#")) {
					// Comment 
				} else {
					if (s.length() > 0) {
						TraceManager.addDev("Excuting line: " + s);
						printLine(line, s); 
						ret = executeLineOfScript(s, config);
						if (ret == SYNTAX_ERROR_IN_LINE) {
							lineOfError = line;
							return SYNTAX_ERROR_IN_LINE;
						}
					}
				}
				
            }
		} catch (Exception e) {
			TraceManager.addDev("Exception: " + e.getMessage() + " Trace:");
			e.printStackTrace();
			config = null;
			return KO;
		}
		config = null;
			return OK;
	}
	
	private int executeLineOfScript(String _line, DSEConfiguration _config) {
		int index;
		String command;
		String arguments;
		
		// Analyze the command
		index = _line.indexOf('=');
		
		if (index == -1) {
			arguments = "";
			command = _line.trim();
		} else {
			command = _line.substring(0, index).trim();
			arguments = _line.substring(index+1, _line.length()).trim();
		}
		
		TraceManager.addDev("Command=" + command +  " arguments=" + arguments);
		
		// Look for a given command
		for(int i=0; i<commands.length; i++) {
			if (command.toLowerCase().compareTo(commands[i].toLowerCase()) == 0) {
				return makeCommand(i, arguments, _config);
			}
		}
		
		TraceManager.addDev(command + " is not a valid command");
		
		// If no comamnd found -> return 
		return SYNTAX_ERROR_IN_LINE;
	}
	
	private int makeCommand(int _commandID, String _arguments, DSEConfiguration _config) {
		switch(_commandID) {
			case 0:
				return makeMappingFile(_arguments, _config);
			case 1:
				if (_config.setOutputVCD(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
				
			case 2:
				if (_config.setPathToSimulator(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
				
			case 3:
				if (_config.setPathToResults(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 4:
				if (_config.runSimulation(_arguments, debug, optimize) != 0) {
					return ERROR_WHEN_RUNNING;
				}
				return OK;
				
			case 5:
				if (_config.setModelPath(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
				
			case 6:
				if (_config.setSimulationCompilationCommand(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
				
			case 7:
				if (_config.setSimulationExecutionCommand(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 8:
				if (_config.setOutputHTML(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 9:
				if (_config.setOutputTXT(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 10:
				if (_config.setSimulationExplorationMinimumCommand(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 11:
				if (_config.setSimulationExplorationMinimumBranch(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 12:
				if (_config.runExplo(_arguments, debug, optimize) != 0) {
					return ERROR_WHEN_RUNNING;
				}
				return OK;
			case 13:
				if (_config.setSimulationMaxCycle(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 14:
				if (_config.setRecordResults(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 15:
				if (_config.printAllResults(_arguments, debug, optimize) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 16:
				if (_config.setOutputXML(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 17:
				if (_config.printResultsSummary(_arguments, debug, optimize) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 18:
				if (_config.resetResults(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 19:
				if (_config.setNbOfSimulationThreads(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 20:
				if (_config.runParallelSimulation(_arguments, debug, optimize) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 21:
				if (_config.setShowSimulatorRawOutput(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 22:
				return makeTaskModelFile(_arguments, _config);
			case 23:
				if (_config.setMinNbOfCPUs(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 24:
				if (_config.setMaxNbOfCPUs(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 25:
				if (_config.setNbOfSimulationsPerMapping(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 26:
				if (_config.runDSE(_arguments, debug, optimize) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 27:
				if (_config.setMinNbOfCoresPerCPU(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;
			case 28:
				if (_config.setMaxNbOfCoresPerCPU(_arguments) != 0) {
					return SYNTAX_ERROR_IN_LINE;
				}
				return OK;	
		}
		
		return KO;
	}
	
	private int makeMappingFile(String _arguments, DSEConfiguration _config) {
		int index = _arguments.indexOf(" ");
		if (index != -1) {
			TraceManager.addDev("\"=\" sign in argument: " + _arguments);
			return SYNTAX_ERROR_IN_LINE;
		}
		
		if (_config.setMappingFile(_arguments) <0) {
			TraceManager.addDev("Checking file for open " + _arguments + " failed");
			return SYNTAX_ERROR_IN_LINE;
		}
		
		if (debug) {
			TraceManager.addDev("Mapping file correctly set to \"" + _arguments + "\"");
		}
		
		return OK;
	}
	
	private int makeTaskModelFile(String _arguments, DSEConfiguration _config) {
		int index = _arguments.indexOf(" ");
		if (index != -1) {
			TraceManager.addDev("\"=\" sign in argument: " + _arguments);
			return SYNTAX_ERROR_IN_LINE;
		}
		
		if (_config.setTaskModelFile(_arguments) <0) {
			TraceManager.addDev("Checking file for open " + _arguments + " failed");
			return SYNTAX_ERROR_IN_LINE;
		}
		
		if (debug) {
			TraceManager.addDev("Task model file correctly set to \"" + _arguments + "\"");
		}
		
		return OK;
	}
	
	
	public int getLineOfError() {
		return lineOfError;
	}
	
	public synchronized void printLine(int _line, String _command) {
		config.resetProgression();
			
		if (linePrinted) {
			System.out.print("\r");
		} else {
			linePrinted = true;
		}
		String s = "#" + _line;
		while(s.length() < 10) {
			s += " ";
		}
	
		s += _command;
		
		if (s.length() > 60) {
			s = s.substring(0, 60);
		}
		
		while(s.length() < 60) {
			s += " ";
		}
		
		System.out.print(s);
	}
	
	public synchronized void printProgression(int _percentage) {
		
		if (!linePrinted) {
			return;
		}
		String s = ("\b\b\b\b\b\b\b\b\b\b\b\b|");
		for(int i=10; i<110; i = i +10) {
			if (i<=_percentage) {
				s += "*";
			} else {
				if ((i-10) <= _percentage) {
					s += steps[step%4];
				} else {
					s += " ";
				}
			}
		}
		s += "|";
		System.out.print(s);
		step ++;
		
	}
	
	public void run() {
		int progression;
		//System.out.println("Running progression");
		while(config != null) {
			try  {
				Thread.currentThread().sleep(50);
			} catch (InterruptedException ie) {
			}
			try {
				if (config != null) {
					progression = config.getProgression();
					//System.out.println("Printing progression= " + progression);
					printProgression(progression);
				}
			} catch (Exception e) {
			}
		}
	}
	
} // Class DSEScriptReader

