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

import avatartranslator.AvatarSpecification;
import avatartranslator.modelchecker.AvatarModelChecker;
import avatartranslator.modelcheckervalidator.ModelCheckerValidator;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.RG;
import launcher.RTLLauncher;
import myutil.*;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import ui.MainGUI;
import ui.util.IconManager;
import ui.window.JDialogSystemCGeneration;
import ui.*;
import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Class PluginAction Creation: 12/06/2020 Version 2.0 12/06/2020
 *
 * @author Ludovic APVRILLE
 */
public class PluginAction extends Command {
    // Action commands
    private final static String LIST_PLUGIN = "list";
    private final static String INFO_PLUGIN = "info";
    private final static String INFO_COMMAND_PLUGIN = "info-command";
    private final static String LOAD_PLUGIN = "load";
    private final static String EXECUTE_COMMAND_IN_PLUGIN = "exec";
    private final static String EXECUTE_RAW_COMMAND_IN_PLUGIN = "exec-raw";

    public PluginAction() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "plugin";
    }

    public String getShortCommand() {
        return "pl";
    }

    public String getUsage() {
        return "action <subcommand> <options>";
    }

    public String getDescription() {
        return "Can be used to performe actions with pluginsT";
    }

    public void fillSubCommands() {
        // List
        Command list = new Command() {
            public String getCommand() {
                return LIST_PLUGIN;
            }

            public String getShortCommand() {
                return "li";
            }

            public String getDescription() {
                return "List loaded plugins";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (PluginManager.pluginManager == null) {
                    System.out.println("No plugins");
                    return null;
                }

                StringBuffer sb = new StringBuffer("");
                for (Plugin p : PluginManager.pluginManager.plugins) {
                    sb.append(p.getName() + "\n");
                }
                System.out.println(sb.toString());

                return null;
            }
        };

        // info
        Command info = new Command() {
            public String getCommand() {
                return INFO_PLUGIN;
            }

            public String getShortCommand() {
                return "i";
            }

            public String getDescription() {
                return "Get information on a given plugin";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (PluginManager.pluginManager == null) {
                    return "No plugins";
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                System.out.println("Info on plugin:" + commands[0]);
                Plugin p = PluginManager.pluginManager.getPlugin(commands[0]);
                if (p == null) {
                    return "Unknown plugin " + commands[0];
                }
                StringBuffer sb = new StringBuffer("");
                sb.append("Package:\t" + p.getPackageName() + "\n");
                sb.append("Path:\t" + p.getPath() + "\n");
                sb.append("Has graphical component?\t");
                if (p.hasGraphicalComponent()) {
                    sb.append("true\n");
                } else {
                    sb.append("false\n");
                }
                sb.append("Has Avatar code generator?\t");
                if (p.hasAvatarCodeGenerator()) {
                    sb.append("true\n");
                } else {
                    sb.append("false\n");
                }
                sb.append("Has Diplodocus code generator?\t");
                if (p.hasDiplodocusCodeGenerator()) {
                    sb.append("true\n");
                } else {
                    sb.append("false\n");
                }
                sb.append("Has Command line interface?\t");
                if (p.hasCommandLineInterface()) {
                    sb.append("true\n");
                    sb.append("Custom commands: " + p.getCommandLineInterfaceFunctions());
                } else {
                    sb.append("false\n");
                }

                System.out.println(sb.toString());

                return null;
            }
        };

        // info on command
        Command infoCommand = new Command() {
            public String getCommand() {
                return INFO_COMMAND_PLUGIN;
            }

            public String getShortCommand() {
                return "ic";
            }

            public String getDescription() {
                return "Get information on a given command of a plugin";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (PluginManager.pluginManager == null) {
                    return "No plugins";
                }

                String[] commands = command.split(" ");
                if (commands.length < 2) {
                    return Interpreter.BAD;
                }

                Plugin p = PluginManager.pluginManager.getPlugin(commands[0]);
                if (p == null) {
                    return "No such plugin";
                }

                String s = p.getHelpOnCommandLineInterfaceFunction(commands[1]);

                if ((s == null) || (s.length() == 0)) {
                    return "No such function";
                }

                System.out.println(s);

                return null;
            }
        };

        // execute command
        Command executeCommand = new Command() {
            public String getCommand() {
                return EXECUTE_COMMAND_IN_PLUGIN;
            }

            public String getShortCommand() {
                return "e";
            }

            public String getDescription() {
                return "Execute a command. exec <pluginname> <command> [-ret variable for return value (if applicable)] "
                        + "[arg1]  [arg2] ...";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (PluginManager.pluginManager == null) {
                    return "No plugins";
                }

                String[] commands = command.split(" ");
                if (commands.length < 2) {
                    return Interpreter.BAD;
                }

                Plugin p = PluginManager.pluginManager.getPlugin(commands[0]);
                if (p == null) {
                    return "No such plugin";
                }

                String methodName = commands[1];
                if ((methodName == null) || (methodName.length() == 0)) {
                    return "No such command";
                }

                // Look for a return variable
                String retVar = null;
                int indexArg = 2;
                if (commands.length > 3) {
                    if (commands[2].compareTo("-ret") == 0) {
                        retVar = commands[3];
                        indexArg = 4;
                        TraceManager.addDev("Using variable for return:" + retVar);
                    }
                }

                String[] tab = new String[commands.length - indexArg];
                for (int i = 0; i < tab.length; i++) {
                    tab[i] = commands[i + indexArg];
                }

                TraceManager.addDev("Using " + tab.length + " arguments");

                // Start the command
                String ret = p.callCommandLineCommand(methodName, tab);

                TraceManager.addDev("Ret= " + ret);

                // Store new variable
                if ((ret != null) && (retVar != null)) {
                    interpreter.addVariable(retVar, ret);
                }

                return null;
            }
        };

        // execute raw command
        Command executeRawCommand = new Command() {
            public String getCommand() {
                return EXECUTE_RAW_COMMAND_IN_PLUGIN;
            }

            public String getShortCommand() {
                return "er";
            }

            public String getDescription() {
                return "Execute a command. execraw <pluginname> <command>  [-ret variable for return value (if applicable)]\n\t <all arguments in "
                        + "one" + " " + "string> ";

            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (PluginManager.pluginManager == null) {
                    return "No plugins";
                }

                String[] commands = command.split(" ");
                if (commands.length < 2) {
                    return Interpreter.BAD;
                }

                Plugin p = PluginManager.pluginManager.getPlugin(commands[0]);
                if (p == null) {
                    return "No such plugin";
                }
                commands[0] = "";

                String methodName = commands[1];
                if ((methodName == null) || (methodName.length() == 0)) {
                    return "No such command";
                }
                commands[1] = "";

                // Look for a return variable
                String retVar = null;
                int indexArg = 2;
                if (commands.length > 3) {
                    if (commands[2].compareTo("-ret") == 0) {
                        retVar = commands[3];
                        TraceManager.addDev("Using variable for return:" + retVar);
                        commands[2] = "";
                        commands[3] = "";
                    }
                }

                StringBuilder builder = new StringBuilder();
                for (String s : commands) {
                    builder.append(s);
                }
                String str = builder.toString().trim();
                String[] sA = new String[1];

                // Start the command
                String ret = p.callCommandLineCommand(methodName, sA);

                TraceManager.addDev("Ret= " + ret);

                // Store new variable
                if ((ret != null) && (retVar != null)) {
                    interpreter.addVariable(retVar, ret);
                }

                return null;
            }
        };

        // load
        Command load = new Command() {
            public String getCommand() {
                return LOAD_PLUGIN;
            }

            public String getShortCommand() {
                return "lo";
            }

            public String getDescription() {
                return "Load a new plugin. pl l <plugin path> <plugin name> <plugin package>";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (PluginManager.pluginManager == null) {
                    PluginManager.pluginManager = new PluginManager();
                }

                String[] commands = command.split(" ");
                if (commands.length < 2) {
                    return Interpreter.BAD;
                }

                String[] names = new String[1];
                names[0] = commands[1];
                String[] packages = new String[1];
                if (commands.length == 3) {
                    packages[0] = commands[2];
                } else {
                    packages[0] = "";
                }

                PluginManager.pluginManager.preparePlugins(commands[0], names, packages);

                return null;
            }
        };

        addAndSortSubcommand(list);
        addAndSortSubcommand(info);
        addAndSortSubcommand(executeCommand);
        addAndSortSubcommand(executeRawCommand);
        addAndSortSubcommand(infoCommand);
        addAndSortSubcommand(load);

    }

}
