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


package cli;

import common.ConfigurationTTool;
import launcher.RTLLauncher;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;

import java.io.File;
import java.util.BitSet;
import java.util.*;

/**
 * Class Action
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Action extends Command  {
    // Action commands
    private final static String OPEN = "open";
    private final static String START = "start";
    private final static String QUIT = "quit";
    private final static String CHECKSYNTAX = "checksyntax";


    public Action() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "action";
    }

    public String getShortCommand() {
        return "a";
    }

    public String getUsage() {
        return "action <subcommand> <options>";
    }

    public String getDescription() {
        return "Can be used to trigger an action in TTool";
    }


    /*public  String executeCommand(String command, Interpreter interpreter) {
        int index = command.indexOf(" ");
        String nextCommand;
        String args;

        if (index == -1) {
            nextCommand = command;
            args = "";
        } else {
            nextCommand = command.substring(0, index);
            args = command.substring(index+1, command.length());
        }


        // Analyzing next command
        for(Command c: subcommands) {
            if ((c.getCommand().compareTo(nextCommand) == 0) || (c.getCommand().compareTo(nextCommand) == 0)) {
                return c.executeCommand(args, interpreter);
            }
        }


        String error = Interpreter.UNKNOWN_NEXT_COMMAND + nextCommand;
        return error;

    }*/

    public void fillSubCommands() {
        // Start
        Command start = new Command() {
            public String getCommand() { return START; }
            public String getShortCommand() { return "s"; }
            public String getDescription() { return "Starting the graphical interface of TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_ALREADY_STARTED;
                }
                TraceManager.addDev("Loading images");
                IconManager.loadImg();

                TraceManager.addDev("Preparing plugins");
                PluginManager.pluginManager = new PluginManager();
                PluginManager.pluginManager.preparePlugins(ConfigurationTTool.PLUGIN_PATH, ConfigurationTTool.PLUGIN, ConfigurationTTool.PLUGIN_PKG);


                TraceManager.addDev("Starting launcher");
                Thread t = new Thread(new RTLLauncher());
                t.start();

                TraceManager.addDev("Creating main window");
                interpreter.mgui = new MainGUI(false, true, true, true,
                        true, true, true, true, true, true,
                        true, false, true);
                interpreter.mgui.build();
                interpreter.mgui.start(interpreter.showWindow());

                interpreter.setTToolStarted(true);

                return null;
            }
        };

        // Open
        Command open = new Command() {
            public String getCommand() { return OPEN; }
            public String getShortCommand() { return "o"; }
            public String getDescription() { return "Opening a model in TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.openProjectFromFile(new File(command));

                return null;
            }
        };

        // Quit
        Command quit = new Command() {
            public String getCommand() { return QUIT; }
            public String getShortCommand() { return "q"; }
            public String getDescription() { return "Closing the graphical interface of TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                interpreter.mgui.quitApplication(false, false);
                return null;
            }
        };

        // Check syntax
        Command checkSyntax = new Command() {
            public String getCommand() { return "checksyntax"; }
            public String getShortCommand() { return "cs"; }
            public String getDescription() { return "Checking the syntax of an opened model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                interpreter.mgui.checkModelingSyntax(interpreter.mgui.getCurrentTURTLEPanel(), true);
                return null;
            }
        };

        subcommands.add(start);
        subcommands.add(open);
        subcommands.add(quit);
        subcommands.add(checkSyntax);
    }
}
