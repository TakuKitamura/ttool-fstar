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

import myutil.Conversion;
import myutil.Terminal;
import myutil.TerminalProviderInterface;
import myutil.TraceManager;
import ui.MainGUI;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

/**
 * Class Interpreter
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Interpreter implements Runnable, TerminalProviderInterface {

    public final static Command[] commands = {new Action(), new Help(), new History(), new Print(), new Plan(), new PluginAction(), new Quit(),
            new TestSpecific(), new TML(), new Set(), new Wait(), new Robot(), new BF(), new SimulatorScript()};

    // Errors
    public final static String UNKNOWN = "Unknown command";
    public final static String BAD = "Badly formatted expression";
    public final static String BAD_WAIT_VALUE = "Must provide a int value > 0";
    public final static String BAD_VAR_VALUE = "Unvalid value for variable";
    public final static String BAD_VAR_NAME = "Unvalid variable name";
    public final static String UNKNOWN_NEXT_COMMAND = "Invalid action: ";
    public final static String TTOOL_NOT_STARTED = "TTool is not yet started. Cannot execute command.";
    public final static String TTOOL_ALREADY_STARTED = "TTool is already started. Cannot execute command.";
    public final static String BAD_COMMAND_NAME = "The provided command is invalid";
    public final static String ROBOT_EXCEPTION = "Robot could not be started";
    public final static String BAD_FILE_NAME = "Unvalid file identifier";
    public final static String BAD_FILE = "Badly formatted file";
    public final static String AVATAR_NO_SPEC = "No Avatar specification";
    public final static String NO_WINDOW = "The targeted window does not exist";


    private String script;
    private InterpreterOutputInterface printInterface;
    private boolean show;

    // State management
    private HashMap<String, String> variables;
    private String error;
    private boolean ttoolStarted = false;
    public MainGUI mgui;
    private Vector<String> formerCommands;
    private Terminal term;
    private int currentLine;


    public Interpreter(String script, InterpreterOutputInterface printInterface, boolean show) {
        this.script = script;
        this.printInterface = printInterface;
        variables = new HashMap<>();
        this.show = show;
        formerCommands = new Vector<>();
    }

    @Override
    public void run() {
        interact();
    }


    public void interact() {
        Terminal term = new Terminal();
        term.setTerminalProvider(this);

        String line;
        currentLine = 0;
        while ((line = term.getNextCommand()) != null) {
            //TraceManager.addDev("Dealing with line: " + line);
            for(String subCommand: line.split(";")) {
                //TraceManager.addDev("Executing: " + subCommand);
                executeLine(subCommand, currentLine, false);
            }
            currentLine++;
        }
    }

    public void interactIntegratedTerminal() {
        /*if (RawConsoleInput.isWindows) {
            print("In Windows");
        } else  {
            print("In Unix");
        }*/

        Scanner scanner = new Scanner(System.in);
        int cptLine = 0;
        printPrompt(cptLine);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            executeLine(line, cptLine, false);
            cptLine++;
            printPrompt(cptLine);
        }
    }

    private void printPrompt(int lineNb) {
        System.out.print("" + lineNb + " -> ");
        System.out.flush();
    }

    public void interpret() {
        Scanner scanner = new Scanner(script);
        currentLine = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            currentLine++;
            //TraceManager.addDev("Dealing with line: " + line);
            for(String subCommand: line.split(";")) {
                //TraceManager.addDev("Executing: " + subCommand);
                executeLine(subCommand, currentLine, true);
            }

        }
        scanner.close();
        printInterface.print("All done. See you soon.");
        printInterface.exit(0);

    }

    private void executeLine(String line, int cptLine, boolean exitOnError) {
        // Comment
        //TraceManager.addDev("Executing line:" + line);

        line = line.trim();
        if (line.length() == 0) {
            return;
        }

        if (line.startsWith("#")) {

        } else {

            formerCommands.add(line);

            // Replace all double space by one unique space
            line = Conversion.replaceAllString(line, "  ", " ").trim();

            //TraceManager.addDev("Handling line: " + line);
            // Replace variable value in the current line
            String lineWithNoVariable = removeVariablesIn(line).trim();

            String begOfLine = lineWithNoVariable;
            int index = lineWithNoVariable.indexOf(' ');
            if (index > -1) {
                begOfLine = begOfLine.substring(0, index).trim();
            }

            //TraceManager.addDev("Handling line: " + lineWithNoVariable);
            String[] commandInfo = lineWithNoVariable.split(" ");

            if ((commandInfo == null) || (commandInfo.length < 1)) {
                System.out.println("Empty command");
                if (exitOnError) {
                    System.exit(-1);
                }
            }


            // Analyze current line
            error = "";
            for (Command c : commands) {
                if (commandInfo[0].compareTo(c.getCommand()) == 0) {
                    error = c.executeCommand(lineWithNoVariable.substring(c.getCommand().length(),
                            lineWithNoVariable.length()).trim(), this);
                    TraceManager.addDev("Command executed");
                    break;
                }
                if (commandInfo[0].compareTo(c.getShortCommand()) == 0) {
                    error = c.executeCommand(lineWithNoVariable.substring(c.getShortCommand().length(),
                            lineWithNoVariable.length()).trim(), this);
                    TraceManager.addDev("Short Command executed");
                    break;

                }
            }

            if ((error != null) && (error.length() > 0)) {
                System.out.println("Error in line " + cptLine + " : " + error);
                if (exitOnError) {
                    System.exit(-1);
                }
            } else if ((error != null) && (error.length() == 0)) {
                System.out.println("Unknown command in line " + cptLine + " : " + commandInfo[0]);
            }
        }
    }


    public void addVariable(String name, String value) {
        variables.put(name, value);
    }

    public String getVariableValue(String name) {
        String v = variables.get(name);
        if (v == null) {
            return "";
        }
        return v;
    }

    private String removeVariablesIn(String input) {
        String ret = "";
        String initialLine = input;

        int index;
        while ((index = input.indexOf("$")) > -1) {
            ret = ret + input.substring(0, index);
            input = input.substring(index + 1, input.length());
            int indexSpace = input.indexOf(" ");
            String varName;
            if (indexSpace == -1) {
                varName = input;
                input = "";
            } else {
                varName = input.substring(0, indexSpace);
                input = input.substring(indexSpace, input.length());
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

    public void setMGUI(MainGUI mgui) {
        this.mgui = mgui;
    }

    public boolean showWindow() {
        return show;
    }

    public Command getSubCommandByName(String cmd) {
        String comm = cmd;

        int index = cmd.indexOf(" ");

        if (index > 0) {
            comm = cmd.substring(0, index);
        }

        for (Command c : commands) {
            if ((c.getShortCommand().compareTo(comm) == 0) || (c.getCommand().compareTo(comm) == 0)) {
                if (index == -1) {
                    return c;
                }
                return c.getSubCommandByName(cmd.substring(index+1, cmd.length()).trim());
            }
        }
        return null;
    }

    public String getHelp() {
        StringBuffer buf = new StringBuffer("");
        for (Command c : commands) {
            buf.append(c.getHelp(0) + "\n");
        }
        return buf.toString();
    }

    public void print(String s) {
        printInterface.print(s);
    }

    // History
    public String printAllFormerCommands() {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < formerCommands.size(); i++) {
            sb.append("" + i + "\t" + formerCommands.get(i) + "\n");
        }
        print(sb.toString());
        return null;
    }

    public String executeFormerCommand(int indexOfCommand) {
        if (indexOfCommand >= formerCommands.size() || (indexOfCommand < 0)) {
            return "Invalid command index";
        }

        String formerCommand = formerCommands.get(indexOfCommand);
        System.out.println("Executing: " + formerCommand);
        executeLine(formerCommand, currentLine, false);

        return null;
    }

    // Terminal provider interface
    public String getMidPrompt() {
        return "> ";
    }

    public boolean tabAction(String buffer) {
        // Print all possibilities from current buffer
        String buf = Conversion.replaceAllString(buffer, "  ", " ");
        String[] split = buf.split(" ");

        // From the split, determine commands already entered and completes it
        Vector<Command> listOfCommands = findCommands(split, 0);

        if (listOfCommands.size() == 0) {
            return false;
        }

        for (Command c : listOfCommands) {
            System.out.println("" + c.getCommand());
            return true;
        }

        return true;

    }

    public Vector<Command> findCommands(String[] split, int index) {
        if (split == null) {
            return null;
        }

        if (index >= split.length) {
            return null;
        }

        String s = split[index];
        Vector<Command> couldBe = new Vector<>();

        // Search of all compatible commands starting with s
        for (Command c : commands) {
            if (c.getShortCommand().startsWith(s) || c.getCommand().startsWith(s)) {
                Vector<Command> others = c.findCommands(split, index + 1);
                if (others != null) {
                    couldBe.addAll(others);
                }
            }
        }

        return couldBe;
    }


}
