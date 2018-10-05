/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paritech.fr
 * andrea.enrici AT telecom-paristech.fr
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



import myutil.*;
import ui.*;
import cli.*;

import java.io.*;



/**
 * Class TToolCLI
 * Command line application for minimizing graphs
 * Apps of the package show, minimize and convert
 * Creation: 11/01/2017
 *
 * @author Ludovic APVRILLE
 * @version 1.10 05/10/2018
 */
public class TToolCLI implements InterpreterOutputInterface {

    public static void printCopyright() {
        System.out.println("ttool-cli: (C) Telecom ParisTech, Ludovic APVRILLE ludovic.apvrille, andrea.enrici@telecom-paristech.fr");
        System.out.println("ttool-cli is released under a CECILL License. See http://www.cecill.info/index.en.html");
        System.out.println("For more information on TTool related technologies, please consult http://ttool.telecom-paristech.fr/");
        System.out.println("Enjoy!!!\n");
    }

    public static void printUsage() {
        System.out.println("ttool-cli: usage");
        System.out.println("ttool-cli <script file>");
    }

    public static boolean checkArgs(String[] args) {
        return !(args.length < 1);
    }

    public static boolean hasDebug(String[] args) {
        for (String s : args) {
            if (s.equals("-debug")) {
                return true;
            }

        }
        return false;
    }

    public static String getInputFile(String[] args) {
        return args[args.length - 1];
    }



    public static void main(String[] args) {
        String[] tmp;

        printCopyright();

        if (!checkArgs(args)) {
            printUsage();
            return;
        }

        if (hasDebug(args)) {
            TraceManager.devPolicy = TraceManager.TO_CONSOLE;
        } else {
            TraceManager.devPolicy = TraceManager.TO_DEVNULL;
        }

        TToolCLI cli = new TToolCLI();
        cli.print("Loading script:" + getInputFile(args));
        // Load script file
        File f = new File(getInputFile(args));
        if (!FileUtils.checkFileForOpen(f)) {
            cli.printError("File " + f.getAbsolutePath() + " could not be opened.");
            cli.exit(-1);
        }
        String script = FileUtils.loadFileData(f);

        // Call Interpreter
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)cli);
        interpret.interpret();

    }


    // InterpreterOutputInterface
    public void print(String s) {
        System.out.println(s);
    }

    public void printError(String s) {
        System.out.println("\t*Fatal error*:" + s);
    }

    public void exit(int status) {
        System.out.println("Exiting. Bye.");
        System.exit(status);
    }



} // Class TToolCLI
