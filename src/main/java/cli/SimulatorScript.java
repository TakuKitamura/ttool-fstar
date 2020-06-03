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
import launcher.LauncherException;
import launcher.RTLLauncher;
import launcher.RshClient;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.PluginManager;
import myutil.TraceManager;
import remotesimulation.RemoteConnection;
import remotesimulation.RemoteConnectionException;
import ui.MainGUI;
import ui.util.IconManager;
import avatartranslator.*;

import java.io.File;
import java.util.BitSet;
import java.util.*;


/**
 * Class Set
 * Creation: 02/06/2020
 * Version 2.0 02/06/2020
 *
 * @author Ludovic APVRILLE
 */
public class SimulatorScript extends Command  {



    public SimulatorScript() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "simulatorscript";
    }

    public String getShortCommand() {
        return "sc";
    }

    public String getUsage() { return "simulatorscript <path_to_simulator_executable> <inputFile> <outputFile>"; }

    public String getDescription() { return "Starting a simulation script test. Reserved for Development purpose"; }

    public String getExample() {
        return "simulatorscript run.x file1.txt file2.txt";
    }


    public  String executeCommand(String command, Interpreter interpreter) {
        try {
            String[] commands = command.split(" ");
            if (commands.length < 3) {
                return Interpreter.BAD;
            }
           return executeSimulatorScript(commands[0], commands[1], commands[2], interpreter);
        } catch (Exception e) {
            TraceManager.addDev("Exception: " + e.getMessage());
            return "Test failed";

        }

    }

    public void fillSubCommands() {

    }

    private String executeSimulatorScript(String simuPath, String file1, String file2, Interpreter interpreter) throws java.io.IOException, java
            .lang.InterruptedException {
        // Checking arguments
        // Test all files
        File simuFile = new File(simuPath);
        if (!simuFile.exists()) {
            return interpreter.BAD_FILE_NAME + ": " + simuPath;
        }

        File inputFile = new File(file1);
        if (!simuFile.exists()) {
            return interpreter.BAD_FILE_NAME + ": " + file1;
        }

        // If the output file does not exist, its is not important: we create it!
        File outputFile = new File(file2);


        // Starts simulation
        Process simuProcess = Runtime.getRuntime().exec(simuPath + " -server");

        // Wait for one second
        Thread.sleep(1000);


        // Connects to the simulator
        RemoteConnection rc = new RemoteConnection("localhost");
        try {
            rc.connect();
        } catch (RemoteConnectionException rce) {
            return "Could not connect";
        }


        // Opens the two files


        // Loop: as soon as there is a new input, read it, see if value change -> compute
        // simulation time. Append this simulation time to the output file
        // To compute the simulation time: simulate until read time. Erase all past transactions
        // Then wait for event1.
        // and note the time of event1.
        // Then execute until event2. Note the new time time2. Compute (time2-time1)
        // append time2-time1 to the output file

        return null;

    }




}
