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
import myutil.FileUtils;
import myutil.PluginManager;
import myutil.TraceManager;
import tmltranslator.*;
import ui.MainGUI;
import ui.TURTLEPanel;
import ui.util.IconManager;

import java.io.File;
import java.util.BitSet;
import java.util.*;

/**
 * Class TML
 * Creation: 25/03/2019
 * Version 2.0 25/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class TML extends Command  {
    private final static String TABS = "tabs";

    private TMLModeling tmlm;
    private TMLMapping tmlmap;

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

    public String getUsage() { return "tml <subcommand>"; }

    public String getDescription() {
        return "Can be used to manipulate TML specifications";
    }




    public void fillSubCommands() {
        Command load = new Command() {
            public String getCommand() { return "load"; }
            public String getShortCommand() { return "l"; }
            public String getDescription() { return "Lading a TML or a mapping model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                //interpreter.print("Command=" + command);
                return loadSpec(command);
            }
        };
        subcommands.add(load);

        Command checkSyntax = new Command() {
            public String getCommand() { return "checksyntax"; }
            public String getShortCommand() { return "cs"; }
            public String getDescription() { return "Checking the syntax of a loaded TML specification"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                //interpreter.print("Command=" + command);
                return checkSyntax();
            }
        };
        subcommands.add(checkSyntax);



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
                    path = command.substring(0, index+1);
                } else {
                    path = "." + File.separatorChar;
                }
                //System.out.println("path=" + path);


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
            return ("Exception during gile loading: " + e.getMessage());

        }


        return null;
    }


    public String loadMapping(String inputData, String title, String path) {

        TMLMappingTextSpecification<Object> spec = new TMLMappingTextSpecification<>(title);
        boolean ret = spec.makeTMLMapping(inputData, path);

        if (!ret) {
            return ("Loading failed:\n" + spec.printSummary());
        }

        //System.out.println("Format OK");
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

        //System.out.println("Format OK");
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

        if (syntax.hasErrors() == 0) {
            return syntax.getErrors().size() + " errors found.";
        }

        return null;
    }


}
