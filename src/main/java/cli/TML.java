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
import myutil.FileUtils;
import myutil.PluginManager;
import myutil.TraceManager;
import tmltranslator.*;
import tmltranslator.dsez3engine.InputInstance;
import tmltranslator.dsez3engine.OptimizationModel;
import tmltranslator.dsez3engine.OptimizationResult;
import ui.MainGUI;
import ui.TGComponent;
import ui.TURTLEPanel;
import ui.util.IconManager;

import java.io.File;
import java.util.BitSet;
import java.util.*;

/**
 * Class TML Creation: 25/03/2019 Version 2.0 25/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class TML extends Command {
    private final static String TABS = "tabs";

    private TMLModeling tmlm;
    private TMLMapping tmlmap;

    private OptimizationResult result;

    public TML() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "tml";
    }

    public String getShortCommand() {
        return "t";
    }

    public String getUsage() {
        return "tml <subcommand>";
    }

    public String getDescription() {
        return "Can be used to manipulate TML specifications";
    }

    public void fillSubCommands() {
        Command load = new Command() {
            public String getCommand() {
                return "load";
            }

            public String getShortCommand() {
                return "l";
            }

            public String getDescription() {
                return "Lading a TML or a mapping model";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return loadSpec(command);
            }
        };
        addAndSortSubcommand(load);

        Command checkSyntax = new Command() {
            public String getCommand() {
                return "checksyntax";
            }

            public String getShortCommand() {
                return "cs";
            }

            public String getDescription() {
                return "Checking the syntax of a loaded TML specification";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return checkSyntax();
            }
        };
        addAndSortSubcommand(checkSyntax);

        Command loadz3lib = new Command() {
            public String getCommand() {
                return "loadz3lib";
            }

            public String getShortCommand() {
                return "z3lib";
            }

            public String getDescription() {
                return "Loading Z3 libs";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return loadZ3lib(command);
            }
        };
        addAndSortSubcommand(loadz3lib);

        Command z3 = new Command() {
            public String getCommand() {
                return "z3opt";
            }

            public String getShortCommand() {
                return "z3o";
            }

            public String getDescription() {
                return "Searching for an optimal mapping using Z3 (Z3 MUST be installed)";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return z3OptimalMappingAnalysis();
            }
        };
        addAndSortSubcommand(z3);

        Command z3f = new Command() {
            public String getCommand() {
                return "z3fea";
            }

            public String getShortCommand() {
                return "z3f";
            }

            public String getDescription() {
                return "Searching for a feasible mapping using Z3 (Z3 MUST be installed)";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return z3FeasibleMappingAnalysis();
            }
        };
        addAndSortSubcommand(z3f);

        Command saveResult = new Command() {
            public String getCommand() {
                return "save-result";
            }

            public String getShortCommand() {
                return "sr";
            }

            public String getDescription() {
                return "save result <file>: save results of a mapping exploration";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return saveResult(command);
            }
        };
        addAndSortSubcommand(saveResult);

        Command saveResultMapping = new Command() {
            public String getCommand() {
                return "save-result-tml-mapping";
            }

            public String getShortCommand() {
                return "srtm";
            }

            public String getDescription() {
                return "srtm <file>: save the produced results of a mapping exploration";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return saveResultTMLMapping(command);
            }
        };
        addAndSortSubcommand(saveResultMapping);

    }

    private String loadSpec(String command) {

        if (command.length() < 1) {
            return "Invalid file name";
        }

        File inputFile = new File(command);
        try {
            if (!FileUtils.checkFileForOpen(inputFile)) {
                return ("Cannot read file: " + command);
            }

            String inputData = FileUtils.loadFileData(inputFile);

            if (command.endsWith(".tmap")) {
                String path;
                int index;
                index = command.lastIndexOf(File.separatorChar);
                if (index != -1) {
                    path = command.substring(0, index + 1);
                } else {
                    path = "." + File.separatorChar;
                }
                // System.out.println("path=" + path);

                String ret = loadMapping(inputData, command, path);
                if (ret != null) {
                    return ret;
                }
            } else {
                String ret = loadTML(inputData, command, command);
                if (ret != null) {
                    return ret;
                }
            }

        } catch (Exception e) {
            return ("Exception during file loading: " + e.getMessage());

        }

        return null;
    }

    public String loadMapping(String inputData, String title, String path) {

        TMLMappingTextSpecification<Object> spec = new TMLMappingTextSpecification<>(title);
        boolean ret = spec.makeTMLMapping(inputData, path);

        if (!ret) {
            return ("Loading failed:\n" + spec.printSummary());
        }

        // System.out.println("Format OK");
        tmlmap = spec.getTMLMapping();
        tmlm = tmlmap.getTMLModeling();

        return null;
    }

    public String loadTML(String inputData, String title, String path) {
        TMLTextSpecification<Object> spec = new TMLTextSpecification<>(title, true);
        boolean ret = spec.makeTMLModeling(inputData);

        if (!ret) {
            return ("Loading failed:\n" + spec.printSummary());
        }

        // System.out.println("Format OK");
        tmlm = spec.getTMLModeling();
        tmlmap = null;

        return null;
    }

    private String checkSyntax() {

        if (tmlm == null) {
            return "No loaded TML spec";
        }

        TMLSyntaxChecking syntax;
        if (tmlmap == null) {
            syntax = new TMLSyntaxChecking(tmlm);
        } else {
            syntax = new TMLSyntaxChecking(tmlmap);
        }

        syntax.checkSyntax();

        if (syntax.hasErrors() > 0) {
            return syntax.getErrors().size() + " errors found.";
        }

        return null;
    }

    private String loadZ3lib(String lib) {
        try {
            String[] libs = lib.split(":");
            boolean setLibPath = false;

            for (int i = 0; i < libs.length; i++) {
                // get the path and set it as a property of java lib path

                String tmp = libs[i].trim();
                if (tmp.length() > 0) {
                    if (setLibPath == false) {
                        File f = new File(tmp);
                        String dir = f.getParent();
                        // TraceManager.addDev("Old library path: " +
                        // System.getProperty("java.library.path"));
                        // TraceManager.addDev("Setting java library path to " + dir);
                        // System.setProperty("java.library.path", ".:" + dir);
                        ConfigurationTTool.addToJavaLibraryPath(new File(dir));
                        // TraceManager.addDev("New library path: " +
                        // System.getProperty("java.library.path"));
                        setLibPath = true;
                    }
                    TraceManager.addDev("Loading Z3 lib: " + tmp);
                    System.load(tmp);
                    TraceManager.addDev("Loaded Z3 lib: " + tmp);
                }

            }
        } catch (UnsatisfiedLinkError e) {
            return ("Z3 libs " + ConfigurationTTool.Z3LIBS + " could not be loaded");
        }

        return null;
    }

    private String z3OptimalMappingAnalysis() {
        if (tmlmap == null) {
            return "You must load a TML Mapping first";
        }

        if (tmlm == null) {
            return "Empty task model";
        }

        @SuppressWarnings("unchecked")
        InputInstance inputInstance = new InputInstance(tmlmap.getTMLArchitecture(), (TMLModeling<TGComponent>) tmlm);
        OptimizationModel optimizationModel = new OptimizationModel(inputInstance);

        // Loading Z3 libs;
        // String error = ConfigurationTTool.loadZ3Libs();

        try {
            result = optimizationModel.findOptimizedMapping();
            // result = optimizationModel.findFeasibleMapping();
        } catch (Exception e) {
            return ("Exception during Z3 execution: Badly installed?");
        }

        return null;
    }

    private String z3FeasibleMappingAnalysis() {
        if (tmlmap == null) {
            return "You must load a TML Mapping first";
        }

        if (tmlm == null) {
            return "Empty task model";
        }

        @SuppressWarnings("unchecked")
        InputInstance inputInstance = new InputInstance(tmlmap.getTMLArchitecture(), (TMLModeling<TGComponent>) tmlm);
        OptimizationModel optimizationModel = new OptimizationModel(inputInstance);

        // Loading Z3 libs;
        // String error = ConfigurationTTool.loadZ3Libs();

        try {
            // result = optimizationModel.findOptimizedMapping();
            result = optimizationModel.findFeasibleMapping();
        } catch (Exception e) {
            return ("Exception during Z3 execution: Badly installed?");
        }

        return null;
    }

    private String saveResult(String command) {
        if (result == null) {
            return "No result to save";
        }

        if (command.length() == 0) {
            return "Must give a file as argument";
        }

        if (result.hasError()) {
            return "No result to print since the exploration encountered errors";
        }

        File fileResult = new File(command);
        try {
            boolean b = FileUtils.checkFileForSave(fileResult);
            if (!b) {
                return "Results cannot be written to " + command + ": access denied";
            }
            FileUtils.saveFile(fileResult, result.result);
        } catch (Exception e) {
            return "Exception when writing results to file: " + e.getMessage();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private String saveResultTMLMapping(String command) {
        if (result == null) {
            return "No mapping to save";
        }

        if (command.length() == 0) {
            return "Must give a file as argument";
        }

        if (result.hasError()) {
            return "No mapping to save since the mapping exploration encountered errors";
        }

        if (result.resultingMapping == null) {
            return "Empty mapping: cannot generate TML file";
        }

        File fileResult = new File(command);
        try {
            boolean b = FileUtils.checkFileForSave(fileResult);

            if (!b) {
                return "Results cannot be written to " + command + ": access denied";
            }

            TMLMappingTextSpecification spec = new TMLMappingTextSpecification("fromZ3");
            spec.toTextFormat(result.resultingMapping);
            spec.saveFile(fileResult.getAbsolutePath(), "");

        } catch (Exception e) {
            return "Exception when writing resulting mapping to file: " + e.getMessage();
        }

        return null;
    }

}
