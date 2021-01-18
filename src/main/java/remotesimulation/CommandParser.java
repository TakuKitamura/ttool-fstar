/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */


package remotesimulation;

import java.util.ArrayList;


/**
 * Class CommandParser
 * For managing commands the C++ simulator
 * Creation: 16/04/2009
 *
 * @author Ludovic APVRILLE
 * @version 1.1 16/04/2009
 */
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
        //TraceManager.addDev("calculating help on cmd");
        StringBuffer sb = new StringBuffer("");
        boolean commandFound = false;
        int i;

        for (SimulationCommand sc : commandList) {
            if (sc.userCommand.equals(cmd) || sc.alias.equals(cmd)) {
                sb.append(sc.getSynopsis() + "\n" + sc.help + "\n");
                if (sc.hasAlias()) {
                    sb.append("alias: " + sc.alias + "\n");
                }
                sb.append("code: " + sc.simulatorCommand);
                //TraceManager.addDev("Command found" + sc.help);
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
        //  TraceManager.addDev("cmd " + cmd + " has " + cmds.length + " elements");

        for (SimulationCommand sc : commandList) {
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
            cpt++;
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
        for (SimulationCommand sc : commandList) {
            if (cpt == 1) {
                cpt = 0;
                sb.append("\n");
            }
            if (sc.userCommand.equals(sc.alias)) {
                sb.append(sc.userCommand + " ");
            } else {
                sb.append(sc.userCommand + "/" + sc.alias + " ");
            }
            cpt++;
        }
        return sb.toString();
    }


    // Fill two arrays with information about commands
    private void fillCommandList() {
        SimulationCommand sc;
        int[] params;
        String[] paramNames;
        int i;

        // active-breakpoints
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "0/1 (unactive / active)";
        sc = new SimulationCommand("active-breakpoints", "ab", "20", params, paramNames, "Active / unactive breakpoints");
        commandList.add(sc);

        // add-breakpoint
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "task ID";
        params[1] = 0;
        paramNames[1] = "comamnd ID";
        sc = new SimulationCommand("add-breakpoint", "abp", "11", params, paramNames, "Set a breakpoint in task which id is the first parameter on the command provided as the second parameter");
        commandList.add(sc);

        // Get latencies
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "Checkpoint 1 id";
        params[1] = 1;
        paramNames[1] = "Checkpoint2 id";
        sc = new SimulationCommand("calculate-latencies", "cl", "23", params, paramNames, "Calculate latencies between checkpoints");
        commandList.add(sc);

        // choose-branh
        params = new int[3];
        paramNames = new String[3];
        params[0] = 1;
        paramNames[0] = "task ID";
        params[1] = 0;
        paramNames[1] = "command ID";
        params[2] = 0;
        paramNames[2] = "branch ID";
        sc = new SimulationCommand("choose-branch", "cb", "12", params, paramNames, "Chooses the branch of the given command of a task");
        commandList.add(sc);




        // get-breakpoint-list
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("get-breakpoint-list", "gbl", "18", params, paramNames, "Returns the list of breakpoints currently set");
        commandList.add(sc);

        // get-command-and-task
        params = new int[1];
        paramNames = new String[1];
        params[0] = 0;
        paramNames[0] = "Task id (or \"all\")";
        sc = new SimulationCommand("get-command-of-task", "gcot", "14", params, paramNames, "Returns the current command of the task provided as argument");
        commandList.add(sc);

        // get-benchmark
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "0: show benchmark; 1:save in file";
        params[1] = 0;
        paramNames[1] = "Name of file";
        sc = new SimulationCommand("get-benchmark", "gb", "10", params, paramNames, "Returns information on hardware nodes of the architecture");
        commandList.add(sc);

        // get-executed-operators
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("get-executed-operators", "geo", "21", params, paramNames, "Returns the list of executed operators");
        commandList.add(sc);

        // get-hash-code
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("get-hashcode", "gh", "19", params, paramNames, "Returns the hashcode of the tmap under simulation");
        commandList.add(sc);

        // get-info-on-hw
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "0: CPU; 1:Bus; 2: Mem; 3: Bridge; 4: Channel; 5: Task";
        params[1] = 1;
        paramNames[1] = "id";
        sc = new SimulationCommand("get-info-on-hw", "gioh", "4", params, paramNames, "Returns information on hardware nodes of the architecture");
        commandList.add(sc);

        // get-number-of-branches
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("get-numer-of-branches", "gnob", "17", params, paramNames, "Returns the number of branches the current command has");
        commandList.add(sc);

        // get-simulation-time
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("get-simulation-time", "time", "13", params, paramNames, "Returns the current absolute time unit of the simulation");
        commandList.add(sc);

        // get-variable-of-task
        params = new int[2];
        paramNames = new String[2];
        params[0] = 0;
        paramNames[0] = "Task id";
        params[1] = 0;
        paramNames[1] = "Variable id";
        sc = new SimulationCommand("get-variable-of-task", "gvof", "3", params, paramNames, "Returns the value of a variable a a task");
        commandList.add(sc);

        // kill
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("kill", "kill", "0", params, paramNames, "Terminates the remote simulator");
        commandList.add(sc);

        // Get transactions
        params = new int[1];
        paramNames = new String[1];
        params[0] = 2;
        paramNames[0] = "Max. nb of transactions";
        sc = new SimulationCommand("list-transactions", "lt", "22", params, paramNames, "Get the most recent transactions");
        commandList.add(sc);

        // Get transactions
        params = new int[1];
        paramNames = new String[1];
        params[0] = 2;
        paramNames[0] = "Task Name";
        sc = new SimulationCommand("list-all-transactions-of-a-task", "lat", "25", params, paramNames, "Get all transactions of Task");
        commandList.add(sc);

        // Remove all the transactions in the past
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "<YES>: 1, <NO>: 0";
        sc = new SimulationCommand("remove-all-trans", "rmat", "26", params, paramNames, "Remove all the transactions in the past");
        commandList.add(sc);

        // rm-breakpoint
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "task ID";
        params[1] = 0;
        paramNames[1] = "comamnd ID";
        sc = new SimulationCommand("rm-breakpoint", "abp", "16", params, paramNames, "Remove a breakpoint in task which id is the first parameter on the command provided as the second parameter");
        commandList.add(sc);

        // reset
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("reset", "reset", "2", params, paramNames, "Resets the remote simulator");
        commandList.add(sc);

        // rawcmd
        params = new int[5];
        paramNames = new String[5];
        for (i = 0; i < 5; i++) {
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

        // run-exploration
        params = new int[3];
        paramNames = new String[3];
        params[0] = 6;
        params[1] = 6;
        params[2] = 2;
        paramNames[0] = "Minimum number of explored commands";
        paramNames[1] = "Minimum number of explored branches";
        paramNames[1] = "File name of the resulting graph, with NO extension";
        sc = new SimulationCommand("run-exploration", "re", "1 7", params, paramNames, "Runs the simulation in exploration mode");
        commandList.add(sc);

        // run-to-next-breakpoint
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("run-to-next-breakpoint", "rtnb", "1 0", params, paramNames, "Runs the simulation until a breakpoint is met");
        commandList.add(sc);

        // run-to-next-breakpoint-max-trans
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "max nb of transactions";
        sc = new SimulationCommand("run-to-next-breakpoint-max-trans", "rtnbmt", "1 19", params, paramNames, "Runs the simulation until a breakpoint is met or max number of transactions are executed");
        commandList.add(sc);

        // run-to-next-transfer-on-bus
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "bus id";
        sc = new SimulationCommand("run-to-next-transfer-on-bus", "rtntob", "1 8", params, paramNames, "Runs to the next transfer on bus which id is provided as argument");
        commandList.add(sc);

        // run-to-time
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "x: time value";
        sc = new SimulationCommand("run-to-time", "rtt", "1 5", params, paramNames, "Runs the simulation until time x is reached");
        commandList.add(sc);

        // run-until-channel-access
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "Channel id";
        sc = new SimulationCommand("run-until-channel-access", "ruca", "1 12", params, paramNames, "Run simulation until a operation is performed on the channel which ID is provided as parameter");
        commandList.add(sc);

        // run-until-write-on-channel-access
        params = new int[1];
        paramNames = new String[1];
        params[0] = 2;
        paramNames[0] = "Channel name";
        sc = new SimulationCommand("run-until-write-on-channel-access", "ruwca", "1 17", params, paramNames, "Run simulation until a write operation is performed on the channel which channel name is provided as parameter");
        commandList.add(sc);

        // run-until-read-on-channel-access
        params = new int[1];
        paramNames = new String[1];
        params[0] = 2;
        paramNames[0] = "Channel name";
        sc = new SimulationCommand("run-until-read-on-channel-access", "rurca", "1 18", params, paramNames, "Run simulation until a read operation is performed on the channel which chanel name is provided as parameter");
        commandList.add(sc);

        // run-until-cpu-executes
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "CPU id";
        sc = new SimulationCommand("run-until-cpu-executes", "ruce", "1 9", params, paramNames, "Run simulation until CPU which ID is provided as parameter executes");
        commandList.add(sc);

        // run-until-memory-access
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "Memory id";
        sc = new SimulationCommand("run-until-memory-access", "ruma", "1 11", params, paramNames, "Run simulation until the memory which ID is provided as parameter is accessed");
        commandList.add(sc);

        // run-until-task-executes
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "Task id";
        sc = new SimulationCommand("run-until-task-executes", "rute", "1 10", params, paramNames, "Run simulation until the task which ID is provided as parameter executes");
        commandList.add(sc);

        // run-x-commands
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "nb of commands";
        sc = new SimulationCommand("run-x-commands", "rxcomm", "1 4", params, paramNames, "Runs the simulation for x commands");
        commandList.add(sc);

        // run-x-time-units
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "nb of time units";
        sc = new SimulationCommand("run-x-time-units", "rxtu", "1 6", params, paramNames, "Runs the simulation for x units of time");
        commandList.add(sc);

        // run-x-transactions
        params = new int[1];
        paramNames = new String[1];
        params[0] = 1;
        paramNames[0] = "nb of transactions";
        sc = new SimulationCommand("run-x-transactions", "rxtr", "1 2", params, paramNames, "Runs the simulation for x transactions");
        commandList.add(sc);

        // save-simulation-state-in-file
        params = new int[1];
        paramNames = new String[1];
        params[0] = 2;
        paramNames[0] = "File name";
        sc = new SimulationCommandSaveState("save-simulation-state-in-file", "sssif", "8", params, paramNames, "Saves the current simulation state into a file");
        commandList.add(sc);

        // save-trace-in-file
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "File format: 0-> VCD, 1->HTML, 2->TXT, 3->XML";
        params[1] = 2;
        paramNames[1] = "File name";
        sc = new SimulationCommand("save-trace-in-file", "stif", "7", params, paramNames, "Saves the current trace of the simulation in a VCD, HTML, TXT or XML file");
        commandList.add(sc);

        // save-timeline-trace-in-file
        params = new int[1];
        paramNames = new String[1];
        params[0] = 2;
        paramNames[0] = "Task List";
        sc = new SimulationCommand("show-timeline-trace", "stlt", "7 4", params, paramNames, "Show the current timeline diagram tracein HTML format");
        commandList.add(sc);

        // set-variable
        params = new int[3];
        paramNames = new String[3];
        params[0] = 1;
        paramNames[0] = "task ID";
        params[1] = 1;
        paramNames[1] = "variable ID";
        params[2] = 1;
        paramNames[2] = "variable value";
        sc = new SimulationCommand("set-variable", "sv", "5", params, paramNames, "Set the value of a variable");
        commandList.add(sc);

        // stop
        params = new int[0];
        paramNames = new String[0];
        sc = new SimulationCommand("stop", "stop", "15", params, paramNames, "Stops the currently running simulation");
        commandList.add(sc);

        // write-in-channel
        params = new int[2];
        paramNames = new String[2];
        params[0] = 1;
        paramNames[0] = "Channel ID";
        params[1] = 2;
        paramNames[1] = "Nb of samples";
        sc = new SimulationCommand("write-in-channel", "wic", "6", params, paramNames, "Writes y samples / events to channel / event x");
        commandList.add(sc);

        // add signals
        params = new int[3];
        paramNames = new String[3];
        params[0] = 2;
        paramNames[0] = "Channel name";
        params[1] = 2;
        paramNames[1] = "Nb of samples";
        params[2] = 2;
        paramNames[2] = "value of samples";
        sc = new SimulationCommand("add-virtual-signals", "avs", "1 16", params, paramNames, "Send virtual events to channel");
        commandList.add(sc);







    }


}
