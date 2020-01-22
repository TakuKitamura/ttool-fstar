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

//import com.sun.deploy.trace.Trace;
import common.ConfigurationTTool;
import launcher.RTLLauncher;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;
import avatartranslator.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.BitSet;
import java.util.*;


/**
 * Class Set
 * Creation: 19/03/2019
 * Version 2.0 19/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class BF extends Command  {


    public BF() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "bruteforce";
    }

    public String getShortCommand() {
        return "bf";
    }

    public String getUsage() { return "bf <graphfile> <nbOfCLBs> <durationDynamicReconfiguration> <nbOfCores>"; }

    public String getDescription() { return "Bruteforcing FPGA and SW scheduling. Reserved for Development purpose"; }

    public String getExample() {
        return "bf graph.txt 100 30 2";
    }


    public  String executeCommand(String command, Interpreter interpreter) {
        int cpt = 0;
        try {

            TraceManager.addDev("BF checking attributes");

            // Check attributes
            String[] commands = command.split(" ");
            if (commands.length < 4) {
                return Interpreter.BAD;
            }

            String fileName = commands[0];

            if (fileName.length() < 1) {
                return Interpreter.BAD_FILE_NAME;
            }



            int nbOfCLBs = Integer.decode(commands[1]);
            int dynamicReconfigurationTime = Integer.decode(commands[2]);
            int nbOfCores = Integer.decode(commands[3]);


            if (nbOfCLBs < 1) {
                return Interpreter.BAD;
            }

            if (dynamicReconfigurationTime < 1) {
                return Interpreter.BAD;
            }


            if (nbOfCores < 1) {
                return Interpreter.BAD;
            }


            // Read text file
            TraceManager.addDev("Reading file");
            File f = new File(fileName);
            cpt = 1;
            int inSWTask = 0;
            int inHWTask = 0;
            Vector<BFTask> tasks = new Vector<>();
            BFTask t = null;
            int taskIndex = 0;

            try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                for(String line; (line = br.readLine()) != null; ) {
                    cpt ++;
                    if (line.length() > 0) {

                        // Graph structure
                        if (line.contains("-")) {
                            // link between tasks
                            int indexMinus = line.indexOf("-");
                            String originTaskName =  line.substring(0, indexMinus).trim();
                            String []nextTasks = line.substring(indexMinus+1, line.length()).trim().split(" ");

                            //TraceManager.addDev("origin task:" + originTaskName);
                            BFTask originTask = getBFTaskByName(tasks, originTaskName);
                            if (originTask == null) {
                                return Interpreter.BAD_FILE;
                            }

                            for(String taskName: nextTasks) {
                                //TraceManager.addDev("destination task:" + taskName);
                                BFTask destinationTask = getBFTaskByName(tasks, taskName);
                                if (destinationTask == null) {
                                    return Interpreter.BAD_FILE;
                                }
                                originTask.addNext(destinationTask);
                                destinationTask.addPrevious(originTask);
                            }


                        //Task definition
                        } else {
                            if (t == null) {
                                // new task
                                t = new BFTask(line);
                                tasks.add(t);
                                taskIndex = 1;
                            } else {
                                if (taskIndex == 1) {
                                    // hw or sw?
                                    if (line.compareTo("hw") == 0) {
                                        t.type = 0;
                                    } else {
                                        t.type = 1;
                                    }
                                } else {
                                    // extra info
                                    int info = Integer.decode(line);
                                    switch (taskIndex){
                                        case 2:
                                            if (t.isSW()) {
                                                t.timing = info;
                                                t = null;
                                            } else {
                                                t.clb = info;
                                            }
                                            break;
                                        case 3:
                                            t.dsp = info;
                                            break;
                                        case 4:
                                            t.mem = info;
                                            break;
                                        case 5:
                                            t.timing = info;
                                            t = null;
                                    }
                                }

                                if (t != null) {
                                    taskIndex ++;
                                }

                            }
                        }
                    }

                }

            }

            TraceManager.addDev("File read");

            // Print all tasks
            TraceManager.addDev("Tasks:");
            for (BFTask ta: tasks) {
                TraceManager.addDev(ta.toString());
            }

            // Generate AvatarSpec
            AvatarSpecification avspec = new AvatarSpecification(fileName, this);

            // tasks -> blocks
            for(BFTask bft: tasks) {
                bft.makeBlock(avspec);
                avspec.addBlock(bft.block);
            }

            // Clock
            TraceManager.addDev("Making clock block");
            AvatarBlock clockBlock = AvatarBlockTemplate.getClockGraphBlock("Clock", avspec, this, 1, "tick",
                    "allFinished");
            avspec.addBlock(clockBlock);

            // Main block
            TraceManager.addDev("Making main block");
            Vector<String> swTasks = new Vector<>();
            Vector<String> hwTasks = new Vector<>();
            Vector<String> hwSizes = new Vector<>();
            for(BFTask bft: tasks) {
                if (bft.isHW()) {
                    hwTasks.add(bft.name);
                    hwSizes.add(""+bft.clb);
                } else {
                    swTasks.add(bft.name);
                }
            }

            AvatarBlock mainBlock = AvatarBlockTemplate.getMainGraphBlock("Main", avspec, this,
                    swTasks, hwTasks, hwSizes, "tick",
                    "allFinished", nbOfCores, dynamicReconfigurationTime, nbOfCLBs);
            avspec.addBlock(mainBlock);

            // Relations
            TraceManager.addDev("Making relations 1");
            AvatarRelation ar = new AvatarRelation("rMainClock", clockBlock, mainBlock, this);
            ar.addSignals(clockBlock.getAvatarSignalWithName("tick"), mainBlock.getAvatarSignalWithName("tick"));
            ar.addSignals(clockBlock.getAvatarSignalWithName("allFinished"), mainBlock.getAvatarSignalWithName("allFinished"));
            avspec.addRelation(ar);

            TraceManager.addDev("Making relations 2");

            for(BFTask bft: tasks) {
                TraceManager.addDev("Dealing with btf=" + bft.name);
                // Relation between this block and the main block
                AvatarBlock b = bft.block;
                ar = new AvatarRelation("r" + bft.name + "Main", b, mainBlock, this);
                avspec.addRelation(ar);
                TraceManager.addDev("Making relations 2.1 for " + bft.name);
                AvatarSignal as1 = b.getAvatarSignalWithName("selectP");
                AvatarSignal as2 = mainBlock.getAvatarSignalWithName("select_" + bft.name);
                ar.addSignals(as1, as2);
                as1 = b.getAvatarSignalWithName("stepP");
                as2 = mainBlock.getAvatarSignalWithName("step_" + bft.name);
                ar.addSignals(as1, as2);
                TraceManager.addDev("Making relations 2.3");
                as1 = b.getAvatarSignalWithName("finishP");
                as2 = mainBlock.getAvatarSignalWithName("finished_" + bft.name);
                ar.addSignals(as1, as2);
                if (bft.isHW()) {
                    TraceManager.addDev("Making relations 2.4.1");
                    as1 = b.getAvatarSignalWithName("deactivatedP");
                    as2 = mainBlock.getAvatarSignalWithName("deactivate_" + bft.name);
                    ar.addSignals(as1, as2);
                    TraceManager.addDev("Making relations 2.4.2");
                    as1 = b.getAvatarSignalWithName("reactivatedP");
                    if (as1 == null) {
                        TraceManager.addDev("Null as1");
                    }
                    as2 = mainBlock.getAvatarSignalWithName("reactivate_" + bft.name);
                    if (as2 == null) {
                        TraceManager.addDev("Null as2");
                    }
                    ar.addSignals(as1, as2);
                    TraceManager.addDev("Done Making relations 2.4.2");
                } else {
                    TraceManager.addDev("Making relations 2.5");
                    as1 = b.getAvatarSignalWithName("preemptP");
                    as2 = mainBlock.getAvatarSignalWithName("preempt_" + bft.name);
                    ar.addSignals(as1, as2);
                }


                TraceManager.addDev("Done making relations 2.1 for " + bft.name);
                // Relation between this block and its next

            }

            TraceManager.addDev("All done :)");


            // Printing avspec
            TraceManager.addDev("\n\n**************** AVSPEC:\n" + avspec.toShortString() + "\n*********\n\n");


            // Compte RG

            // Deduce best scheduling




            return null;
        } catch (Exception e) {
            TraceManager.addDev("Exception: " + e.getMessage());
            return "BF failed";

        }

    }

    public void fillSubCommands() {

    }

    private BFTask getBFTaskByName(Vector<BFTask> list, String name) {
        for(BFTask t: list) {
            if (t.name.compareTo(name) == 0) {
                return t;
            }
        }
        return null;
    }

}
