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
* Class CommandParser
* For managing commands the C++ simulator
* Creation: 16/04/2009
* @version 1.1 16/04/2009
* @author Ludovic APVRILLE
* @see
*/

package remotesimulation;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;


public class CommandParser {
    ArrayList<SimulationCommand> commandList;
	
	public CommandParser() {
		commandList = new ArrayList<SimulationCommand>();
		fillCommandList();
	}
	
	public boolean isCommand(String cmd, String id) {
		String s = cmd.trim();
		if (cmd.equals(id)) {
			return true;
		}
		return s.startsWith(id + " ");
	}
	
	public boolean isHelpCommand(String cmd) {
		return isCommand(cmd, "help");
	}
	
	// Returns the command name for which help is required
	public String getHelpWithCommand(String cmd) {
		if (!isCommand(cmd, "help")) {
			return null;
		}
		
		String tmp = cmd.trim();
		
		if (!(tmp.startsWith("help "))) {
			return null;
		}
		
		tmp = tmp.substring(5, tmp.length()).trim();
		
		if (tmp.length() == 0) {
			return null;
		}
		
		return tmp;
		
		
	}
	
	public String getHelp(String cmd) {
		//System.out.println("calculating help on cmd");
		StringBuffer sb = new StringBuffer("");
		boolean commandFound = false;
		
		for(SimulationCommand sc: commandList) {
			if (sc.userCommand.equals(cmd) || sc.alias.equals(cmd)) {
				sb.append(sc.getSynopsis() + "\n" + sc.help);
				if (sc.hasAlias()) {
					sb.append("\nalias: " + sc.alias);
				}
				//System.out.println("Command found" + sc.help);
				commandFound = true;
			}
		}
		if (commandFound) {
			return sb.toString();
		} else {
			return "Command not found";
		}
	}
	
	public boolean isQuitCommand(String cmd) {
		return isCommand(cmd, "quit");
	}
	
	public boolean isPicoCommand(String cmd) {
		return isCommand(cmd, "pico");
	}
	
	public boolean isListCommand(String cmd) {
		return isCommand(cmd, "list");
	}
	
	public int isAValidCommand(String cmd) {
		int index = -1;
		int cpt = 0;
		
		String cmds[] = cmd.split(" ");
		//System.out.println("cmd " + cmd + " has " + cmds.length + " elements"); 
		
		for(SimulationCommand sc: commandList) {
			// Same command name?
			if (sc.userCommand.equals(cmds[0]) || sc.alias.equals(cmds[0])) {
				// Compatible arguments?
				if (sc.areParametersCompatible(cmds)) {
					index = cpt;
					break;
				} else {
					index = -2;
				}
			}
			cpt ++;
		}
		
		if (index < 0) {
			return index;
		}
		
		return index;
	}
	
	public String transformCommandFromUserToSimulator(String cmd) {
		int index = isAValidCommand(cmd);
		if (index < 0) {
			return "";
		}
		
		SimulationCommand sc = commandList.get(index);
		
		return sc.translateCommand(cmd.split(" "));
	}
	
	// Returns the list of all commands
	public String getCommandList() {
		int cpt = 0;
		StringBuffer sb = new StringBuffer("");
		for(SimulationCommand sc: commandList) {
			if (cpt == 1) {
				cpt = 0;
				sb.append("\n");
			}
			if (sc.userCommand.equals(sc.alias)) {
				sb.append(sc.userCommand + " ");
			} else {
				sb.append(sc.userCommand + "/" + sc.alias + " ");
			}
			cpt ++;
		}
		return sb.toString();
	}
	
	
	// Fill two arrays with information about commands
	private void fillCommandList() {
		SimulationCommand sc;
		int[] params;
		String[] paramNames;
		int i;
		
		
		// get-command-and-task
		params = new int[0];
		paramNames = new String[0];
		sc = new SimulationCommand("get-command-and-task", "gcat", "14", params, paramNames, "Returns the current command and task");
		commandList.add(sc);
		
		// get-simulation-time
		params = new int[0];
		paramNames = new String[0];
		sc = new SimulationCommand("get-simulation-time", "time", "13", params, paramNames, "Returns the current absolute time unit of the simulation");
		commandList.add(sc);
		
		// kill
		params = new int[0];
		paramNames = new String[0];
		sc = new SimulationCommand("kill", "kill", "0", params, paramNames, "Terminates the remote simulator");
		commandList.add(sc);
		
		// reset
		params = new int[0];
		paramNames = new String[0];
		sc = new SimulationCommand("reset", "reset", "2", params, paramNames, "Resets the remote simulator");
		commandList.add(sc);
		
		// rawcmd
		params = new int[5];
		paramNames = new String[5];
		for(i=0; i<5; i++) {
			params[i] = 4;
			paramNames[i] = "param #" + i;
		}
		sc = new SimulationCommand("raw-command", "rc", "", params, paramNames, "Sends a raw command to the remote simulator");
		commandList.add(sc);
		
		// restore-simulation-state-from-file
		params = new int[1];
		paramNames = new String[1];
		params[0] = 2;
		paramNames[0] = "File name";
		sc = new SimulationCommandSaveState("restore-simulation-state-from-file", "rssff", "9", params, paramNames, "Restores the simulation state from a file");
		commandList.add(sc);
		
		// run-to-next-breakpoint
		params = new int[0];
		paramNames = new String[0];
		sc = new SimulationCommand("run-to-next-breakpoint", "rtnb", "1 0", params, paramNames, "Runs the simulation until a breakpoint is met");
		commandList.add(sc);
		
		// run-x-time-units
		params = new int[1];
		paramNames = new String[1];
		params[0] = 1;
		paramNames[0] = "nb of time units";
		sc = new SimulationCommand("run-x-time-units", "rxtu", "1 6", params, paramNames, "Runs the simulation for x units of time");
		commandList.add(sc);
		
		// run-to-time
		params = new int[1];
		paramNames = new String[1];
		params[0] = 1;
		paramNames[0] = "x: time value";
		sc = new SimulationCommand("run-to-time", "rtt", "1 5", params, paramNames, "Runs the simulation until time x is reached");
		commandList.add(sc);
		
		// run-x-transactions
		params = new int[1];
		paramNames = new String[1];
		params[0] = 1;
		paramNames[0] = "nb of transactions";
		sc = new SimulationCommand("run-x-transactions", "rxtr", "1 2", params, paramNames, "Runs the simulation for x transactions");
		commandList.add(sc);
		
		// run-x-commands
		params = new int[1];
		paramNames = new String[1];
		params[0] = 1;
		paramNames[0] = "nb of commands";
		sc = new SimulationCommand("run-x-commands", "rxcomm", "1 4", params, paramNames, "Runs the simulation for x commands");
		commandList.add(sc);
		
		// save-trace-in-file
		params = new int[2];
		paramNames = new String[2];
		params[0] = 1;
		paramNames[0] = "File format: 0-> VCD, 1->HTML, 2->TXT";
		params[1] = 2;
		paramNames[1] = "File name";
		sc = new SimulationCommand("save-trace-in-file", "stif", "7", params, paramNames, "Saves the current trace of the simulation in a VCD, HTML or TXT file");
		commandList.add(sc);
		
		// save-simulation-state-in-file
		params = new int[1];
		paramNames = new String[1];
		params[0] = 2;
		paramNames[0] = "File name";
		sc = new SimulationCommandSaveState("save-simulation-state-in-file", "sssif", "8", params, paramNames, "Saves the current simulation state into a file");
		commandList.add(sc);
		
		// stop
		params = new int[0];
		paramNames = new String[0];
		sc = new SimulationCommand("stop", "stop", "15", params, paramNames, "Stops the currently running simulation");
		commandList.add(sc);
	}
	
	
	
	
	
    
}