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

import java.util.BitSet;
import java.util.*;

/**
 * Class Command
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Command implements CommandInterface {
    protected List<Command> subcommands;

    public Command() {
        subcommands = new LinkedList<Command>();
        fillSubCommands();
    }


    public  List<Command> getListOfSubCommands() {
        return subcommands;
    }
    public  String getCommand() {
        return "default";

    }
    public  String getShortCommand() {
        return getCommand();
    }
    public String getExample() {
        return "";
    }

    public String executeCommand(String command, Interpreter interpreter) {
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
            if ((c.getCommand().compareTo(nextCommand) == 0) || (c.getShortCommand().compareTo(nextCommand) == 0)) {
                return c.executeCommand(args, interpreter);
            }
        }
        /*if (nextCommand.compareTo(OPEN) == 0) {
            return openModel(args);
        } else if (nextCommand.compareTo(START) == 0) {
            return startTTool();
        } else if (nextCommand.compareTo(QUIT) == 0) {
            return exitCLI();
        }*/

        String error = Interpreter.UNKNOWN_NEXT_COMMAND + nextCommand;
        return error;
    }


    public void fillSubCommands() {

    }

    public String getUsage() {
        return "";
    }

    public String getDescription() {
        return "";
    }

    public String getHelp(int level) {
        String dec = getLevelString(level);
        /*String h = "";
        h+= getCommand() + " (" + getShortCommand() + "): " + getUsage() + "\n" + getDescription() + "\n";

        for (Command c: subcommands) {
            h+= "\t" + c.getHelp();
        }*/

        StringBuffer b = new StringBuffer(dec + "* " + getCommand() + " (" + getShortCommand() + "): " + getUsage() + "\n" + dec + getDescription() +
                "\n");
        if (getExample().length() > 0) {
            b.append(dec + "Example: " + getExample() + "\n");
        }

        subcommands.forEach( (c) -> { b.append(c.getHelp(level + 1)); });


        return b.toString();
    }

    public String getLevelString(int level) {
        String ret = "";
        while(level > 0) {
            ret += "\t";
            level --;
        }
        return ret;
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
        for (Command c: subcommands) {
            if (c.getShortCommand().startsWith(s) || c.getCommand().startsWith(s)) {
                Vector<Command> others = c.findCommands(split, index+1);
                if (others != null) {
                    couldBe.addAll(others);
                }
            }
        }

        return couldBe;
    }

}
