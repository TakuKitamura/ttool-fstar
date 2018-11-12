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
import ui.*;
import ui.util.IconManager;
import java.io.*;

import java.util.*;

/**
 * Class Interpreter
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Interpreter  {
    private final static Command[] commands = {new Action(), new Set(), new Wait(), new Print()};

    // Errors
    public final static String UNKNOWN = "Unknown command";
    public final static String BAD = "Badly formatted expression";
    public final static String BAD_WAIT_VALUE = "Must provide a int value > 0";
    public final static String BAD_VAR_VALUE ="Unvalid value for variable";
    public final static String BAD_VAR_NAME ="Unvalid variable name";
    public final static String UNKNOWN_NEXT_COMMAND ="Invalid action: ";
    public final static String TTOOL_NOT_STARTED ="TTool is not yet started. Cannot execute command.";
    public final static String TTOOL_ALREADY_STARTED ="TTool is already started. Cannot execute command.";


    private String script;
    private InterpreterOutputInterface printInterface;
    private boolean show;

    // State management
    private HashMap<String, String> variables;
    private String error;
    private boolean ttoolStarted = false;
    public MainGUI mgui;


    public Interpreter(String script, InterpreterOutputInterface printInterface, boolean show) {
        this.script = script;
        this.printInterface = printInterface;
        variables = new HashMap<>();
        this.show = show;
    }

    public void interpret() {
        Scanner scanner = new Scanner(script);
        int cptLine = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            cptLine ++;

            // Comment
            if (line.startsWith("#")) {

            } else {

                // Replace all double space by one unique space
                line = Conversion.replaceAllString(line, "  ", " ").trim();

                //TraceManager.addDev("Handling line: " + line);
                // Replace variable value in the current line
                String lineWithNoVariable = removeVariablesIn(line);

                TraceManager.addDev("Handling line: " + lineWithNoVariable);

                // Analyze current line
                error = "";
                for(Command c: commands) {
                    if (lineWithNoVariable.startsWith(c.getCommand() + " ")) {
                        error = c.executeCommand( lineWithNoVariable.substring(c.getCommand().length() + 1,
                                lineWithNoVariable.length()).trim(), this);
                        break;
                    }
                    if (lineWithNoVariable.startsWith(c.getShortCommand() + " ")) {
                        error = c.executeCommand( lineWithNoVariable.substring(c.getShortCommand().length() + 1,
                                lineWithNoVariable.length()).trim(), this);
                        break;

                    }
                }


                /*if (lineWithNoVariable.startsWith(SET + " ")) {
                    success = setVariable(lineWithNoVariable.substring(SET.length() + 1, lineWithNoVariable.length()).trim());
                } else if (lineWithNoVariable.startsWith(ACTION + " ")) {
                    success = performAction(lineWithNoVariable.substring(ACTION.length() + 1, lineWithNoVariable.length()).trim());
                } else if (lineWithNoVariable.startsWith(WAIT + " ")) {
                    success = waitFor(lineWithNoVariable.substring(WAIT.length() + 1, lineWithNoVariable.length()).trim());
                } else if (lineWithNoVariable.startsWith(PRINT + " ")) {
                    success = performPrint(lineWithNoVariable.substring(PRINT.length() + 1, lineWithNoVariable.length()).trim());
                } else {
                    success = false;
                    error = UNKNOWN;

                }*/

                if (error != null) {
                    System.out.println("Error in line " + cptLine + " : " + error);
                    System.exit(-1);
                }
            }

        }
        scanner.close();
        printInterface.print("All done. See you soon.");
        printInterface.exit(1);

    }


    public void addVariable(String name, String value) {
        variables.put(name, value);
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
            ret = ret + value;
        }

        ret = ret + input;
        return ret;
    }


    public boolean exitCLI() {
        System.exit(-1);
        return true;
    }


    public boolean isTToolStarted() {
        return ttoolStarted;
    }

    public void setTToolStarted(boolean b) {
        ttoolStarted = b;
    }

    public boolean showWindow() {
        return show;
    }



    public String getHelp() {
        StringBuffer buf = new StringBuffer("");
        for(Command c:commands) {
            buf.append(c.getHelp(0) + "\n");
        }
        return buf.toString();
    }

}
