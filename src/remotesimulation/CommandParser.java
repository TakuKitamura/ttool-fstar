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
	
	public boolean isQuitCommand(String cmd) {
		return isCommand(cmd, "quit");
	}
	
	public boolean isPicoCommand(String cmd) {
		return isCommand(cmd, "pico");
	}
	
	public boolean isListCommand(String cmd) {
		return isCommand(cmd, "list");
	}
	
	public boolean isAValidCommand(String cmd) {
		return true;
	}
	
	public String transformCommandFromUserToSimulator(String cmd) {
		return cmd;
	}
	
	public String getCommandList() {
		StringBuffer sb = new StringBuffer("");
		for(SimulationCommand sc: commandList) {
			sb.append(sc.userCommand);
		}
		return sb.toString();
	}
	
	
	private void fillCommandList() {
		SimulationCommand sc;
		
		// kill-simulator
		sc = new SimulationCommand("kill-simulator", "0", 0, 0, 0, 0, "Terminates the remote simulator");
		commandList.add(sc);
	}
	
	
	
	
	
    
}