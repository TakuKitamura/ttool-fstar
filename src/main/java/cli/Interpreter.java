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
import common.SpecConfigTTool;
import launcher.RTLLauncher;
import myutil.Conversion;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;

import java.util.*;

/**
 * Class Interpreter
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Interpreter  {
    private final static Command[] commands = {new Action()};

    // Commands
    private final static String SET = "set";
    private final static String ACTION = "action";


    // Action commands
    private final static String OPEN = "open";
    private final static String START = "start";

    // Errors
    private final static String BAD = "Badly formatted expression";
    private final static String BAD_VAR_VALUE ="Unvalid value for variable";
    private final static String BAD_VAR_NAME ="Unvalid variable name";
    private final static String UNKNOWN_NEXT_COMMAND ="Invalid action command";
    private final static String TTOOL_NOT_STARTED ="TTool is not yet started. Cannot execute command.";
    private final static String TTOOL_ALREADY_STARTED ="TTool is already started. Cannot execute command.";

    private String script;
    private InterpreterOutputInterface printInterface;
    private boolean show;

    // State management
    private HashMap<String, String> variables;
    private String error;
    private boolean ttoolStarted = false;



    public Interpreter(String script, InterpreterOutputInterface printInterface, boolean show) {
        this.script = script;
        this.printInterface = printInterface;
        variables = new HashMap<>();
        this.show = show;
    }

    public void interpret() {
        Scanner scanner = new Scanner(script);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // Replace all double space by one unique space
            line = Conversion.replaceAllString(line, "  ", " ").trim();

            // Replace variable value in the current line
            String lineWithNoVariable = removeVariablesIn(line);

            // Analyze current line
            boolean success;
            if (lineWithNoVariable.startsWith(SET + " ")) {
                success = setVariable(lineWithNoVariable.substring(SET.length() + 1, lineWithNoVariable.length()).trim());
            } else if (lineWithNoVariable.startsWith(ACTION + " ")) {
                success =  performAction(lineWithNoVariable.substring(ACTION.length() + 1, lineWithNoVariable.length()).trim());
            }
        }
        scanner.close();
        printInterface.print("All done. See you soon.");
        printInterface.exit(1);

    }

    // String with first element: name of var
    // Second elt: content of var
    private boolean setVariable(String set) {
        int index = set.indexOf(" ");
        if (index == -1) {
            error = BAD;
            return false;
        }

        String varName = set.substring(0, index);

        if (varName.length() < 1) {
            error = BAD_VAR_NAME;
            return false;
        }

        String attr = set.substring(index+1, set.length()).trim();
        if (attr.length() < 1) {
            error = BAD_VAR_VALUE;
            return false;
        }

        TraceManager.addDev("Adding variable " + varName + " with value: " + attr);
        variables.put(varName, attr);

        return true;
    }

    // String with first element: name of var
    // Second elt: content of var
    private boolean performAction(String action) {
        int index = action.indexOf(" ");
        String nextCommand;
        String args;

        if (index == -1) {
            nextCommand = action;
            args = "";
        } else {
            nextCommand = action.substring(0, index);
            args = action.substring(index+1, action.length());
        }

        // Analyzing next command
        if (nextCommand.compareTo(OPEN) == 0) {
            return openModel(args);
        } else if (nextCommand.compareTo(START) == 0) {
            return startTTool();
        }

        error = UNKNOWN_NEXT_COMMAND + nextCommand;
        return false;
    }

    private String removeVariablesIn(String input) {
        String ret = "";
        String initialLine = input;

        int index;
        while((index = input.indexOf("$")) > -1) {
            ret = ret + input.substring(0, index);
            input = input.substring(index+1, input.length());
            int indexSpace = input.indexOf(" ");
            String varName;
            if (indexSpace == -1) {
                varName = input;
                input = "";
            } else {
                varName = input.substring(0, indexSpace);
                input = input.substring(indexSpace+1, input.length());
            }

            // Identifying variable
            String value = variables.get(varName);
            if (value == null) {
                printInterface.printError("Unknown variable name:" + varName + " in " + initialLine);
                printInterface.exit(-1);
            }
        }

        ret = ret + input;
        return ret;
    }

    // Arg is the model name
    public boolean startTTool() {
        if (ttoolStarted) {
            error = TTOOL_ALREADY_STARTED;
            return false;
        }

        TraceManager.addDev("Laoding images");
        IconManager.loadImg();

        TraceManager.addDev("Preparing plugins");
        PluginManager.pluginManager = new PluginManager();
        PluginManager.pluginManager.preparePlugins(ConfigurationTTool.PLUGIN_PATH, ConfigurationTTool.PLUGIN, ConfigurationTTool.PLUGIN_PKG);


        TraceManager.addDev("Starting launcher");
        Thread t = new Thread(new RTLLauncher());
        t.start();

        TraceManager.addDev("Creating main window");
        MainGUI mainGUI = new MainGUI(false, true, true, true,
                true, true, true, true, true, true,
                true, false, true);
        mainGUI.build();
        mainGUI.start(show);


        return true;
    }

    // Arg is the model name
    public boolean openModel(String arg) {
        if (!ttoolStarted) {
            error = TTOOL_NOT_STARTED;
            return false;
        }
        return true;
    }


}
