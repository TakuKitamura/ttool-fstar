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
import avatartranslator.directsimulation.AvatarSpecificationSimulation;
import avatartranslator.modelchecker.AvatarModelChecker;
import avatartranslator.modelchecker.CounterexampleQueryReport;
import avatartranslator.modelchecker.SpecificationActionLoop;
import avatartranslator.modelcheckervalidator.ModelCheckerValidator;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.RG;
import launcher.RTLLauncher;
import myutil.Conversion;
import myutil.FileUtils;
import myutil.PluginManager;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import ui.MainGUI;
import ui.avatarinteractivesimulation.AvatarInteractiveSimulationActions;
import ui.avatarinteractivesimulation.JFrameAvatarInteractiveSimulation;
import ui.util.IconManager;
import ui.window.JDialogSystemCGeneration;
import ui.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * Class Action
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Action extends Command {
    // Action commands
    private final static String NEW = "new";
    private final static String OPEN = "open";
    private final static String SAVE = "save";
    private final static String SET_FILE = "set-file";
    private final static String GET_FILE = "get-file";
    private final static String RESIZE = "resize";
    private final static String START = "start";
    private final static String QUIT = "quit";

    private final static String NEW_DESIGN = "new-design";
    private final static String REMOVE_CURRENT_TAB = "remove-current-tab";


    private final static String CHECKSYNTAX = "check-syntax";
    private final static String DIPLO_INTERACTIVE_SIMULATION = "diplodocus-interactive-simulation";
    private final static String DIPLO_FORMAL_VERIFICATION = "diplodocus-formal-verification";
    private final static String DIPLO_ONETRACE_SIMULATION = "diplodocus-onetrace-simulation";
    private final static String DIPLO_GENERATE_TML = "diplodocus-generate-tml";
    private final static String DIPLO_GENERATE_XML = "diplodocus-generate-xml";
    private final static String DIPLO_UPPAAL = "diplodocus-uppaal";
    private final static String DIPLO_REMOVE_NOC = "diplodocus-remove-noc";


    private final static String NAVIGATE_PANEL_TO_LEFT = "navigate-panel-to-left";
    private final static String NAVIGATE_PANEL_TO_RIGHT = "navigate-panel-to-right";

    private final static String SELECT_PANEL = "select-panel";



    private final static String AVATAR_RG_GENERATION = "avatar-rg";
    private final static String AVATAR_UPPAAL_VALIDATE = "avatar-rg-validate";
    private final static String AVATAR_SIMULATION_TO_BRK = "avatar-simulation-to-brk";
    private final static String AVATAR_SIMULATION_SELECT_TRACE = "avatar-simulation-select-trace";
    private final static String AVATAR_SIMULATION_OPEN_WINDOW = "avatar-simulation-open-window";
    private final static String AVATAR_SIMULATION_GENERIC = "avatar-simulation-generic";



    private AvatarSpecificationSimulation ass;

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
            public String getCommand() {
                return START;
            }

            public String getShortCommand() {
                return "s";
            }

            public String getDescription() {
                return "Starting the graphical interface of TTool";
            }

            public String executeCommand(String command, Interpreter interpreter) {
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

        // Resize
        Command resize = new Command() {
            public String getCommand() {
                return RESIZE;
            }

            public String getShortCommand() {
                return "r";
            }

            public String getDescription() {
                return "Resize TTool main window";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }


                String[] commands = command.split(" ");
                if (commands.length < 2) {
                    return Interpreter.BAD;
                }

                int w = Integer.decode(commands[0]);
                int h = Integer.decode(commands[1]);

                interpreter.mgui.getFrame().setMinimumSize(new Dimension(200, 200));
                interpreter.mgui.getFrame().setSize(new Dimension(w, h));

                return null;
            }
        };

        // Open
        Command open = new Command() {
            public String getCommand() {
                return OPEN;
            }

            public String getShortCommand() {
                return "o";
            }

            public String getDescription() {
                return "Opening a model in TTool";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.openProjectFromFile(new File(command));

                return null;
            }
        };

        // New
        Command newT = new Command() {
            public String getCommand() {
                return NEW;
            }

            public String getShortCommand() {
                return "n";
            }

            public String getDescription() {
                return "Creating a new model in TTool";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.newProject();

                return null;
            }
        };

        // Set-File
        Command setFile = new Command() {
            public String getCommand() {
                return SET_FILE;
            }

            public String getShortCommand() {
                return "sf";
            }

            public String getDescription() {
                return "Setting the save file of TTool";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                String fileName = commands[commands.length-1];

                interpreter.mgui.setFileName(fileName);

                return null;
            }
        };

        Command getFile = new Command() {
            public String getCommand() {
                return GET_FILE;
            }

            public String getShortCommand() {
                return "gf";
            }

            public String getDescription() {
                return "Get the name of the  model under edition TTool. If a variable is provided as argument, the " +
                        "result is saved into this variable";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");


                String fileName = interpreter.mgui.getFileName();

                if (commands.length > 0) {
                    interpreter.addVariable(commands[0], fileName);
                }

                System.out.println(fileName);

                return null;
            }
        };

        // Save
        Command save = new Command() {
            public String getCommand() {
                return SAVE;
            }

            public String getShortCommand() {
                return "sm";
            }

            public String getDescription() {
                return "Saving a model in TTool";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.saveProject();

                return null;
            }
        };

        // Quit
        Command quit = new Command() {
            public String getCommand() {
                return QUIT;
            }

            public String getShortCommand() {
                return "q";
            }

            public String getDescription() {
                return "Closing the graphical interface of TTool";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                interpreter.mgui.quitApplication(false, false);
                return null;
            }
        };

        // New (avatar) design
        Command newDesign = new Command() {
            public String getCommand() {
                return NEW_DESIGN;
            }

            public String getShortCommand() {
                return "nd";
            }

            public String getDescription() {
                return "Create a new design view";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.newDesign();

                return null;
            }
        };

        // Remove current tab
        Command removeCurrentTab = new Command() {
            public String getCommand() {
                return REMOVE_CURRENT_TAB;
            }

            public String getShortCommand() {
                return "rct";
            }

            public String getDescription() {
                return "Remove the current tab";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.removeCurrentTab();

                return null;
            }
        };

        // Check syntax
        Command checkSyntax = new Command() {
            public String getCommand() {
                return CHECKSYNTAX;
            }

            public String getShortCommand() {
                return "cs";
            }

            public String getDescription() {
                return "Checking the syntax of an opened model";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();
                if (tp == null) {
                    return "No opened panel";
                }

                interpreter.mgui.checkModelingSyntax(tp, true);
                return null;
            }
        };

        // Diplodocus interactive simulation
        Command diplodocusInteractiveSimulation = new Command() {
            public String getCommand() {
                return DIPLO_INTERACTIVE_SIMULATION;
            }

            public String getShortCommand() {
                return "dis";
            }

            public String getDescription() {
                return "Interactive simulation of a DIPLODOCUS model";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if (!(tp instanceof TMLArchiPanel)) {
                    return "Current diagram is invalid for interactive simulationn";
                }

                if (interpreter.mgui.checkModelingSyntax(tp, true)) {
                    interpreter.mgui.generateSystemC(JDialogSystemCGeneration.ANIMATION);
                }

                return null;
            }
        };

        // Diplodocus interactive simulation
        Command diplodocusFormalVerification = new Command() {
            public String getCommand() {
                return DIPLO_FORMAL_VERIFICATION;
            }

            public String getShortCommand() {
                return "dots";
            }

            public String getDescription() {
                return "Formal verification of a DIPLODOCUS mapping model";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if (!(tp instanceof TMLArchiPanel)) {
                    return "Current diagram is invalid for formal verification";
                }

                if (interpreter.mgui.checkModelingSyntax(tp, true)) {
                    interpreter.mgui.generateSystemC(JDialogSystemCGeneration.ONE_TRACE);
                }

                return null;
            }
        };

        // Diplodocus interactive simulation
        Command diplodocusOneTraceSimulation = new Command() {
            public String getCommand() {
                return DIPLO_ONETRACE_SIMULATION;
            }

            public String getShortCommand() {
                return "dots";
            }

            public String getDescription() {
                return "One-trace simulation of a DIPLODOCUS model";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TMLMapping map = interpreter.mgui.gtm.getTMLMapping();
                TMLModeling tmlm;

                if (map == null) {
                    tmlm = interpreter.mgui.gtm.getTMLModeling();
                    if (tmlm == null) {
                        return "No mapping for simulation";
                    }
                }

                interpreter.mgui.generateSystemC(JDialogSystemCGeneration.ONE_TRACE);

                return null;
            }
        };

        // Diplodocus generate TML
        Command diplodocusGenerateTML = new Command() {
            public String getCommand() {
                return DIPLO_GENERATE_TML;
            }

            public String getShortCommand() {
                return "dgtml";
            }

            public String getDescription() {
                return "Generate the TML code of a diplodocus model";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TMLMapping map = interpreter.mgui.gtm.getTMLMapping();
                TMLModeling tmlm;

                if (map == null) {
                    tmlm = interpreter.mgui.gtm.getTMLModeling();
                    if (tmlm == null) {
                        return "No model for generation";
                    }
                }

                String tmp = interpreter.mgui.generateTMLTxt();
                if (tmp == null) {
                    return "TML generation failed";
                } else {
                    return "TML spec generated in: " + tmp;
                }

                //}

                //return null;
            }
        };

        // Diplodocus generate XML
        Command diplodocusGenerateXML = new Command() {
            public String getCommand() {
                return DIPLO_GENERATE_XML;
            }

            public String getShortCommand() {
                return "dgxml";
            }

            public String getDescription() {
                return "Generate the XML of a diplodocus model.\n<variable name>: variable in which the " +
                        "XML specification is saved";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                String varName = commands[0];

                TMLMapping map = interpreter.mgui.gtm.getTMLMapping();

                if (map == null) {
                    return "No model for generation";
                }

                String tmp = map.toXML();
                if (tmp == null) {
                    return "XML generation failed";
                } else {
                    interpreter.addVariable(varName, tmp);
                    return null;
                }

                //}

                //return null;
            }
        };

        // Diplodocus uppaal
        Command diplodocusUPPAAL = new Command() {
            public String getCommand() {
                return DIPLO_UPPAAL;
            }

            public String getShortCommand() {
                return "du";
            }

            public String getDescription() {
                return "Use UPPAAL for formal verification of a DIPLO app";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TMLMapping map = interpreter.mgui.gtm.getTMLMapping();
                TMLModeling tmlm;

                if (map == null) {
                    tmlm = interpreter.mgui.gtm.getTMLModeling();
                    if (tmlm == null) {
                        return "No model for simulation";
                    }
                }

                String tmp = interpreter.mgui.generateTMLTxt();
                boolean result = interpreter.mgui.gtm.generateUPPAALFromTML(SpecConfigTTool.UPPAALCodeDirectory, false, 8, false);

                if (!result) {
                    interpreter.print("UPPAAL verification failed");
                } else {
                    interpreter.print("UPPAAL verification done");
                }

                //}

                return null;
            }
        };

        // Diplodocus remove NoC
        Command diplodocusRemoveNoC = new Command() {
            public String getCommand() {
                return DIPLO_REMOVE_NOC;
            }

            public String getShortCommand() {
                return "drn";
            }

            public String getDescription() {
                return "Remove the NoCs of a diplodocus mapping";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TMLMapping map = interpreter.mgui.gtm.getTMLMapping();
                TMLModeling tmlm;

                if (map == null) {
                    tmlm = interpreter.mgui.gtm.getTMLModeling();
                    if (tmlm == null) {
                        return "No model for simulation";
                    }
                }

                interpreter.mgui.removeNoC(true);

                return null;
            }
        };






        // PANEL manipulation
        Command selectPanel = new Command() {
            public String getCommand() {
                return SELECT_PANEL;
            }

            public String getShortCommand() {
                return "sp";
            }

            public String getDescription() {
                return "Select the edition panel with a name";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                interpreter.mgui.selectPanelByName(commands[0]);

                return null;
            }
        };


        Command movePanelToTheLeftPanel = new Command() {
            public String getCommand() {
                return NAVIGATE_PANEL_TO_LEFT;
            }

            public String getShortCommand() {
                return "nptf";
            }

            public String getDescription() {
                return "Select the edition panel on the left";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.requestMoveLeftTab(interpreter.mgui.getCurrentJTabbedPane().getSelectedIndex());

                return null;
            }
        };

        Command movePanelToTheRightPanel = new Command() {
            public String getCommand() {
                return NAVIGATE_PANEL_TO_RIGHT;
            }

            public String getShortCommand() {
                return "nptl";
            }

            public String getDescription() {
                return "Select the edition panel on the right";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }



                interpreter.mgui.requestMoveRightTab(interpreter.mgui.getCurrentJTabbedPane().getSelectedIndex());

                return null;
            }
        };

        Command generateRGFromAvatar = new Command() {
            public String getCommand() {
                return AVATAR_RG_GENERATION;
            }

            public String getShortCommand() {
                return "arg";
            }

            public String getDescription() {
                return "Generate a Reachability graph from an AVATAR model";
            }

            public String getUsage() { return "[OPTION]... [FILE]\n"
                    + "-g FILE\tcompute and save in FILE the reachability graph"
                    + "-r, -rs\treachability of selected states\n"
                    + "-ra\treachability of all states\n"
                    + "-l, ls\tliveness of all states\n"
                    + "-la\tliveness of all states\n"
                    + "-s\tsafety pragmas verification\n"
                    + "-q \"QUERY\"\tquery a safety pragma\n"
                    + "-d\tno deadlocks check\n"
                    + "-i\ttest model reinitialization\n"
                    + "-dfs\tDFS search preferred over BFS\n"
                    + "-a\tno internal actions loops check\n"
                    + "-n NUM\tmaximum states created (Only for a non verification study)\n"
                    + "-t NUM\tmaximum time (ms) (Only for a non verification study)\n"
                    + "-c\tconsider full concurrency between actions\n"
                    + "-vt FILE\tsave verification traces in FILE"
                    + "-va FILE\tsave verification traces as AUT graph in FILE";
            }

            public String getExample() {
                return "arg /tmp/mylovelyrg?.aut (\"?\" is replaced with current date and time)";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                //format: -rl -la -t 100 -g graph_path
                
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                //String graphPath = commands[commands.length - 1];
                String graphPath = "";
                String counterPath = "";
                String counterPathAUT = "";

                AvatarSpecification avspec = interpreter.mgui.gtm.getAvatarSpecification();
                if(avspec == null) {
                    TMLModeling tmlm = interpreter.mgui.gtm.getTMLModeling();
                    if (tmlm != null) {
                        boolean ret = interpreter.mgui.gtm.generateFullAvatarFromTML();
                        if (!ret) {
                            return Interpreter.AVATAR_NO_SPEC;
                        }
                        avspec = interpreter.mgui.gtm.getAvatarSpecification();
                        avspec.removeElseGuards();
                    } else {
                        return Interpreter.AVATAR_NO_SPEC;
                    }
                }

                AvatarModelChecker amc = new AvatarModelChecker(avspec);
                amc.setIgnoreEmptyTransitions(true);
                amc.setIgnoreConcurrenceBetweenInternalActions(true);
                amc.setIgnoreInternalStates(true);
                amc.setComputeRG(false);
                boolean rgGraph = false;
                boolean counterTraces = false;
                boolean counterTracesAUT = false;
                boolean reachabilityAnalysis = false;
                boolean livenessAnalysis = false;
                boolean safetyAnalysis = false;
                boolean noDeadlocks = false;
                boolean reinit = false;
                boolean actionLoop = false;
                for (int i = 0; i < commands.length; i++) {
                    //specification
                    switch (commands[i]) {
                        case "-g":
                            if (i != commands.length - 1) {
                                graphPath = commands[++i];
                                amc.setComputeRG(true);
                                rgGraph = true;
                            } else {
                                return Interpreter.BAD;
                            }
                            break;
                        case "-r":
                        case "-rs":
                            //reachability of selected states
                            amc.setReachabilityOfSelected();
                            reachabilityAnalysis = true;
                            break;
                        case "-ra":
                            //reachability of all states
                            amc.setReachabilityOfAllStates();
                            reachabilityAnalysis = true;
                            break;
                        case "-l":
                        case "-ls":
                            //liveness of selected states
                            amc.setLivenessOfSelected();
                            livenessAnalysis = true;
                            break;
                        case "-la":
                            //liveness of all states
                            amc.setLivenessOfAllStates();
                            livenessAnalysis = true;
                            break;
                        case "-s":
                            //safety
                            amc.setSafetyAnalysis();
                            safetyAnalysis = true;
                            break;
                        case "-q":
                            //query
                            StringBuilder query;
                            if (!commands[++i].startsWith("\"")) {
                                return Interpreter.BAD;
                            }
                            query = new StringBuilder(commands[i].substring(1));
                            while (!commands[++i].endsWith("\"") && i < commands.length) {
                                query.append(" ");
                                query.append(commands[i]);
                            }
                            query.append(" ");
                            query.append(commands[i].substring(0, commands[i].length() - 1));
                            //Supports multiple queries separated by a comma
                            String[] queries = query.toString().split("\\s*,\\s*");
                            for (String q : queries) {
                                if (q != "") {
                                    if (amc.addSafety(q, q) == false) {
                                        System.out.println("Query " + q + " is badly written");
                                        return Interpreter.BAD;
                                    }
                                }
                            }
                            safetyAnalysis = true;
                            break;
                        case "-d":
                            //deadlock
                            amc.setCheckNoDeadlocks(true);
                            noDeadlocks = true;
                            break;
                        case "-i":
                            //reinitialization
                            amc.setReinitAnalysis(true);
                            reinit = true;
                            break;
                        case "-dfs":
                            //internal action loops
                            amc.setSearchType(1);
                            break;
                        case "-a":
                            //internal action loops
                            amc.setInternalActionLoopAnalysis(true);
                            actionLoop = true;
                            break;
                        case "-n":
                            //state limit followed by a number
                            long states;
                            try {
                                states = Long.parseLong(commands[++i]);
                            } catch (Exception e){
                                return Interpreter.BAD;
                            }
                            amc.setStateLimitValue(states);
                            amc.setStateLimit(true);
                            break;
                        case "-t":
                            //time limit followed by a number
                            long time;
                            try {
                                time = Long.parseLong(commands[++i]);
                            } catch (Exception e){
                                return Interpreter.BAD;
                            }
                            amc.setTimeLimitValue(time);
                            amc.setTimeLimit(true);
                            break;
                        case "-c":
                            //concurrency
                            amc.setIgnoreConcurrenceBetweenInternalActions(false);
                            break;
                        case "-v":
                            if (i != commands.length - 1) {
                                counterPath = commands[++i];
                                counterTraces = true;
                            } else {
                                return Interpreter.BAD;
                            }
                            break;
                        case "-va":
                            if (i != commands.length - 1) {
                                counterPathAUT = commands[++i];
                                counterTracesAUT = true;
                            } else {
                                return Interpreter.BAD;
                            }
                            break;
                        default:
                            return Interpreter.BAD;
                    }
                }
                TraceManager.addDev("Starting model checking");
                amc.setCounterExampleTrace(counterTraces, counterTracesAUT);

                if (livenessAnalysis || safetyAnalysis || noDeadlocks) {
                    amc.startModelCheckingProperties();
                } else {
                    amc.startModelChecking();
                }
                
                System.out.println("Model checking done\nGraph: states:" + amc.getNbOfStates() +
                        " links:" + amc.getNbOfLinks() + "\n");

                if (noDeadlocks) {
                    interpreter.print("No Deadlocks:\n" + amc.deadlockToString());
                }

                if (reinit) {
                    interpreter.print("Reinitialization?:\n" + amc.reinitToString());
                }

                if (actionLoop) {
                    boolean result = amc.getInternalActionLoopsResult();
                    interpreter.print("No internal action loops?:\n" + result);
                    if (!result) {
                        ArrayList<SpecificationActionLoop> al = amc.getInternalActionLoops();
                        for (SpecificationActionLoop sal : al) {
                            if (sal.getResult()) {
                                interpreter.print(sal.toString());
                            }
                        }
                    }
                }

                if (reachabilityAnalysis) {
                    interpreter.print("Reachability Analysis:\n" + amc.reachabilityToStringGeneric());
                }
                if (livenessAnalysis) {
                    interpreter.print("Liveness Analysis:\n" + amc.livenessToString());
                }
                if (safetyAnalysis) {
                    interpreter.print("Safety Analysis:\n" + amc.safetyToString());
                }
                
                
                DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
                Date date = new Date();
                String dateAndTime = dateFormat.format(date);
                
                if (counterTraces) {
                    String trace = amc.getCounterTrace();
                    
                    String file;
                    if (counterPath.indexOf("$") != -1) {
                        file = Conversion.replaceAllChar(counterPath, '$', dateAndTime);
                    } else {
                        file = counterPath;
                    }
                    try {
                        File f = new File(file);
                        FileUtils.saveFile(file, trace);
                        System.out.println("\nCounterexample trace saved in " + file + "\n");
                    } catch (Exception e) {
                        System.out.println("\nCounterexample trace could not be saved in " + file + "\n");
                    }
                    
                    if (counterTracesAUT) {
                        if (counterPathAUT.indexOf("$") != -1) {
                            file = Conversion.replaceAllChar(counterPathAUT, '$', dateAndTime);
                        } else {
                            file = counterPathAUT;
                        }
                        List<CounterexampleQueryReport> autTraces = amc.getAUTTraces();
                        if (autTraces != null) {
                            int i = 0;
                            String autfile = FileUtils.removeFileExtension(file);
                            for (CounterexampleQueryReport tr : autTraces) {
                                String filename = autfile + "_" + i + ".aut";
                                try {
                                    RG rg = new RG(file);
                                    rg.data = tr.getReport();
                                    rg.fileName = filename;
                                    rg.name = tr.getQuery() + "_" + dateAndTime;
                                    interpreter.mgui.addRG(rg);
                                    File f = new File(filename);
                                    FileUtils.saveFile(filename, tr.getReport());
                                    System.out.println("Counterexample graph trace " + tr.getQuery() + " saved in " + filename + "\n");
                                } catch (Exception e) {
                                    System.out.println("Counterexample graph trace "+ tr.getQuery() + " could not be saved in " + filename + "\n");
                                }
                                i++;
                            }
                        }
                    }
                }

                // Saving graph
                if (rgGraph) {
                    String graphAUT = amc.toAUT();
                    String autfile;
    
                    if (graphPath.length() == 0) {
                        graphPath =  System.getProperty("user.dir") + "/" + "rg$.aut";
                    }

                    if (graphPath.indexOf("?") != -1) {
                        //System.out.println("Question mark found");
                        autfile = Conversion.replaceAllChar(graphPath, '?', dateAndTime);
                        //System.out.println("graphpath=" + graphPath);
                    } else {
                        autfile = graphPath;
                    }
                    System.out.println("graphpath=" + graphPath);
    
                    System.out.println("autfile=" + autfile);
    
                    try {
                        RG rg = new RG(autfile);
                        rg.data = graphAUT;
                        rg.fileName = autfile;
                        rg.nbOfStates = amc.getNbOfStates();
                        rg.nbOfTransitions = amc.getNbOfLinks();
                        System.out.println("Saving graph in " + autfile + "\n");
                        File f = new File(autfile);
                        rg.name = f.getName();
                        interpreter.mgui.addRG(rg);
                        FileUtils.saveFile(autfile, graphAUT);
                        System.out.println("Graph saved in " + autfile + "\n");
                    } catch (Exception e) {
                        System.out.println("Graph could not be saved in " + autfile + "\n");
                    }
                }

                return null;
            }
        };
        
        Command compareUppaal = new Command() {
            public String getCommand() {
                return AVATAR_UPPAAL_VALIDATE;
            }

            public String getShortCommand() {
                return "avg-val";
            }

            public String getDescription() {
                return "Validate the internal verification tool with uppaal";
            }

            public String getUsage() {
                return "avatar-rg-validate [OPTION]... [UPPAAL PATH]\n" + 
                        "-r, -rs\treachability of selected states\n" + 
                        "-ra\treachability of all states\n" + 
                        "-l, ls\tliveness of all states\n" + 
                        "-la\tliveness of all states\n" + 
                        "-s\tsafety pragmas verification\n" + 
                        "-d\tno deadlocks verification\n";
            }

            public String getExample() {
                return "avatar-rg-validate -ra -la -s -d /packages/uppaal/";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                
                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }
                
                //get args
                String uppaalPath = commands[commands.length - 1];

                int rStudy = 0;
                int lStudy = 0;
                boolean sStudy = false;
                boolean dStudy = false;
                for (int i = 0; i < commands.length - 1; i++) {
                    //specification
                    switch (commands[i]) {
                        case "-r":
                        case "-rs":
                            //reachability of selected states
                            rStudy = ModelCheckerValidator.STUDY_SELECTED;
                            break;
                        case "-ra":
                            //reachability of all states
                            rStudy = ModelCheckerValidator.STUDY_ALL;
                            break;
                        case "-l":
                        case "-ls":
                            //liveness of selected states
                            lStudy = ModelCheckerValidator.STUDY_SELECTED;
                            break;
                        case "-la":
                            //liveness of all states
                            lStudy = ModelCheckerValidator.STUDY_ALL;
                            break;
                        case "-s":
                            //safety
                            sStudy = true;
                            break;
                        case "-d":
                            //safety
                            dStudy = true;
                            break;
                        default:
                            return Interpreter.BAD;
                    }
                }
                
                //set configuration paths
                ConfigurationTTool.UPPAALVerifierHost = "localhost";
                ConfigurationTTool.UPPAALVerifierPath = uppaalPath + "/bin-Linux/verifyta";
                ConfigurationTTool.UPPAALCodeDirectory = "../../uppaal/";
                SpecConfigTTool.UPPAALCodeDirectory = ConfigurationTTool.UPPAALCodeDirectory;

                interpreter.mgui.gtm.generateUPPAALFromAVATAR(SpecConfigTTool.UPPAALCodeDirectory);
                
                boolean res = ModelCheckerValidator.validate(interpreter.mgui, rStudy, lStudy, sStudy, dStudy);
                
                interpreter.print("avatar-rg-validate result: " + res);
                return null;
            }
        };

        // AVATAR
        Command avatarSimulationToBrk = new Command() {
            public String getCommand() {
                return AVATAR_SIMULATION_TO_BRK;
            }

            public String getShortCommand() {
                return "astb";
            }

            public String getDescription() {
                return "Simulate an avatar design until a breakpoint or the end";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                AvatarSpecification as = interpreter.mgui.gtm.getAvatarSpecification();

                if (as == null) {
                    return "No model for simulation";
                }

                ass = new AvatarSpecificationSimulation(as, null);
                ass.runSimulationToCompletion();


                TraceManager.addUser("Simulation terminated. End time=" + ass.getClockValue());


                return null;
            }
        };

        // AVATAR
        Command avatarSimulationSelectTrace = new Command() {
            public String getCommand() {
                return AVATAR_SIMULATION_SELECT_TRACE;
            }

            public String getShortCommand() {
                return "asst";
            }

            public String getDescription() {
                return "Simulate a trace to be simulated";
            }

            public String getUsage() {
                return "avatar-simulation-select-trace [PATH_TO_TRACE]";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                String [] st = interpreter.mgui.loadSimulationTraceCSVFile(new File(commands[0]));
                if (st == null) {
                    return Interpreter.BAD_FILE_NAME;
                }

                SimulationTrace sim = new SimulationTrace(st[0], SimulationTrace.CSV_AVATAR, st[1]);
                sim.setContent(st[2]);
                interpreter.mgui.addSimulationTrace(sim);
                interpreter.mgui.setSimulationTraceSelected(sim);


                return null;
            }
        };

        // AVATAR
        Command avatarSimulationOpenWindow = new Command() {
            public String getCommand() {
                return AVATAR_SIMULATION_OPEN_WINDOW;
            }

            public String getShortCommand() {
                return "asow";
            }

            public String getDescription() {
                return "Show / hide Avatar simulation window";
            }

            public String getUsage() {
                return "avatar-simulation-open-window";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                SwingUtilities.invokeLater(()->{
                    interpreter.mgui.openCloseAvatarSimulationWindow();
                });


                return null;
            }
        };

        // AVATAR
        Command avatarSimulationGeneric = new Command() {
            public String getCommand() {
                return AVATAR_SIMULATION_GENERIC;
            }

            public String getShortCommand() {
                return "asg";
            }

            public String getDescription() {
                return "Execute a generic action in the Avatar simulation";
            }

            public String getUsage() {
                String usage =  "avatar-simulation-generic <generic-command>\n" +
                        "<generic command> are :";
                if (AvatarInteractiveSimulationActions.actions != null) {
                    for (TAction action : AvatarInteractiveSimulationActions.actions) {
                        if (action != null) {
                            usage += "\n\t" + action.ACTION_COMMAND_KEY;
                        } else {
                            usage += "Actions can be listed only once the simulation window has been started";
                            break;
                        }
                    }
                } else {
                    usage += "Actions can be listed only once the simulation window has been started";
                }
                return usage;

            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                JFrameAvatarInteractiveSimulation jfais = interpreter.mgui.getJFrameAvatarInteractiveSimulation();

                if (jfais == null) {
                    return interpreter.NO_WINDOW;
                }

                jfais.actionPerformed(commands[0], null);


                return null;
            }
        };





        Command generic = new Generic();


        addAndSortSubcommand(start);
        addAndSortSubcommand(newT);
        addAndSortSubcommand(open);
        addAndSortSubcommand(setFile);
        addAndSortSubcommand(getFile);
        addAndSortSubcommand(save);
        addAndSortSubcommand(resize);
        addAndSortSubcommand(quit);

        addAndSortSubcommand(newDesign);
        addAndSortSubcommand(removeCurrentTab);

        addAndSortSubcommand(checkSyntax);
        addAndSortSubcommand(generateRGFromAvatar);
        addAndSortSubcommand(diplodocusInteractiveSimulation);
        addAndSortSubcommand(diplodocusFormalVerification);
        addAndSortSubcommand(diplodocusOneTraceSimulation);
        addAndSortSubcommand(diplodocusGenerateTML);
        addAndSortSubcommand(diplodocusGenerateXML);
        addAndSortSubcommand(diplodocusUPPAAL);
        addAndSortSubcommand(diplodocusRemoveNoC);
        addAndSortSubcommand(movePanelToTheLeftPanel);
        addAndSortSubcommand(movePanelToTheRightPanel);
        addAndSortSubcommand(selectPanel);
        addAndSortSubcommand(compareUppaal);
        addAndSortSubcommand(avatarSimulationToBrk);
        addAndSortSubcommand(avatarSimulationSelectTrace);
        addAndSortSubcommand(avatarSimulationOpenWindow);
        addAndSortSubcommand(avatarSimulationGeneric);

        addAndSortSubcommand(generic);

    }

}
