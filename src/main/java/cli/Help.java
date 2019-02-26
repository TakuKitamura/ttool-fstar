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
 * Class Set
 * Creation: 25/10/2018
 * Version 2.0 25/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Help extends Command  {


    public Help() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "help";
    }

    public String getShortCommand() {
        return "h";
    }

    public String getUsage() {
        return "help or help <command>";
    }

    public String getDescription() {
        return "Displays the general help of the help of a given command";
    }

    public String getExample() {
        return "help action";
    }


    public  String executeCommand(String command, Interpreter interpreter) {
        if (command.trim().length() == 0) {
            String s = "";
            for(Command c: Interpreter.commands) {
                s += c.getShortCommand() + "\t" + c.getCommand() + "\t" + c.getDescription() + "\n";
            }
            interpreter.print(s);
            return null;
        }


        Command c = interpreter.getCommandByName(command);

        if (c == null) {
            return Interpreter.BAD_COMMAND_NAME;
        }

        interpreter.print(c.getHelp(1));

        return null;

    }

    public void fillSubCommands() {

    }
}
