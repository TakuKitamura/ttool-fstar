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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import org.jgrapht.io.ExportException;
import myutil.FileUtils;
import tmltranslator.TMLActivity;
import tmltranslator.TMLActivityElement;
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLModeling;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.TMLTask;
import tmltranslator.dsez3engine.OptimizationResult;
import tmltranslator.simulationtraceanalysis.DependencyGraphTranslator;
import tmltranslator.simulationtraceanalysis.PlanArrays;
import ui.CheckableLatency;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TMLArchiPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.JFrameLatencyDetailedPopup;
import ui.simulationtraceanalysis.LatencyDetailedAnalysisMain;
import ui.tmlad.TMLADExecI;
import ui.tmlad.TMLActivityDiagramPanel;

/**
 * 
 * @author Maysam Zoor
 */
public class Plan extends Command {
    private final static String TABS = "tabs";
    private TMLModeling tmlm;
    private TMLMapping tmlmap;
    private static Object[][] allLatencies, minMaxArray, taskHWByRowDetails, detailedLatency;
    private HashMap<String, Integer> checkedT = new HashMap<String, Integer>();
    private Vector<SimulationTransaction> transFile1;
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;
    private DependencyGraphTranslator dgt, dgraph;;
    private String traceFile;
    private Boolean taint;
    private OptimizationResult result;
    private TMLMapping<TGComponent> tmap;
    private TMLSyntaxChecking syntax;

    public Plan() {
    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "plan";
    }

    public String getShortCommand() {
        return "plan";
    }

    public String getUsage() {
        return "plan <subcommand>";
    }

    public String getDescription() {
        return "Can be used to compute Latency";
    }

    public void fillSubCommands() {
        Command graph = new Command() {
            public String getCommand() {
                return "graph";
            }

            public String getShortCommand() {
                return "g";
            }

            public String getDescription() {
                return "Generate graph to use in latency computation";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return generateGraph(command, interpreter);
            }
        };
        Command generateGraph = new Command() {
            public String getCommand() {
                return "generateGraph";
            }

            public String getShortCommand() {
                return "generate";
            }

            public String getDescription() {
                return "Generate graph from TML mapping ";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return generateGraphFromTML(command, interpreter);
            }
        };
        Command latency = new Command() {
            public String getCommand() {
                return "latency";
            }

            public String getShortCommand() {
                return "lat";
            }

            public String getDescription() {
                return "Compute latency between two operators";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return computeLat(command, interpreter);
            }
        };
        Command listAllOp = new Command() {
            public String getCommand() {
                return "listAllOp";
            }

            public String getShortCommand() {
                return "lao";
            }

            public String getDescription() {
                return "List all operators Id that can be used for latency analysis";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return listAllOperators(command, interpreter);
            }
        };
        Command preciseLatByRow = new Command() {
            public String getCommand() {
                return "planByRow";
            }

            public String getShortCommand() {
                return "planR";
            }

            public String getDescription() {
                return "Precise latency analysis by row";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return preciseLatByRow(command, interpreter);
            }
        };
        Command exportGraphGraphml = new Command() {
            public String getCommand() {
                return "exportGraph";
            }

            public String getShortCommand() {
                return "exportG";
            }

            public String getDescription() {
                return "Export graph in Graphml format";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return exportGGraphml(command, interpreter);
            }
        };
        Command saveGraph = new Command() {
            public String getCommand() {
                return "saveGraph";
            }

            public String getShortCommand() {
                return "saveG";
            }

            public String getDescription() {
                return "Export graph in png format";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return exportGpng(command, interpreter);
            }
        };
        // show all oppl
        Command showAllOp = new Command() {
            public String getCommand() {
                return "showAllOp";
            }

            public String getShortCommand() {
                return "showAllOp";
            }

            public String getDescription() {
                return "Show all operators IDs";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return showAllOp(command, interpreter);
            }
        };
        Command computeLatencyValues = new Command() {
            public String getCommand() {
                return "computeLatencyValues";
            }

            public String getShortCommand() {
                return "computelat";
            }

            public String getDescription() {
                return "Compute latency between two operators";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return computeLatencyValues(command, interpreter);
            }
        };
        Command rowArrays = new Command() {
            public String getCommand() {
                return "rowArrays";
            }

            public String getShortCommand() {
                return "rowArrays";
            }

            public String getDescription() {
                return "Precise latency analysis by row";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                // interpreter.print("Command=" + command);
                return PlanByRow(command, interpreter);
            }
        };
        addAndSortSubcommand(graph);
        addAndSortSubcommand(latency);
        addAndSortSubcommand(listAllOp);
        addAndSortSubcommand(preciseLatByRow);
        addAndSortSubcommand(generateGraph);
        addAndSortSubcommand(exportGraphGraphml);
        addAndSortSubcommand(saveGraph);
        addAndSortSubcommand(showAllOp);
        addAndSortSubcommand(computeLatencyValues);
        addAndSortSubcommand(rowArrays);
    }

    private String generateGraph(String command, Interpreter interpreter) {
        JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
        String[] commands = command.split(" ");
        traceFile = commands[0];
        String mappingDiagName = commands[1];
        SimulationTrace file2 = new SimulationTrace("graphTestSimulationTrace", 6, traceFile);
        MainGUI mgui = interpreter.mgui;
        interpreter.print("file2=" + file2.toString());
        TMLArchiPanel panel = null;
        for (final TMLArchiPanel panel1 : mgui.getTMLArchiDiagramPanels()) {
            if (mappingDiagName.equals(mgui.getTitleAt(panel1))) {
                panel = panel1;
            }
        }
        try {
            mgui.checkModelingSyntax(panel, true);
            LatencyDetailedAnalysisMain = new LatencyDetailedAnalysisMain(3, mgui, file2, false, false, 3);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            return ("Exception during file loading: " + e.getMessage());
        }
        LatencyDetailedAnalysisMain.getTc().setMainGUI(mgui);
        // latencyDetailedAnalysisMain.setTc();
        interpreter.print("panel=" + panel);
        LatencyDetailedAnalysisMain.latencyDetailedAnalysis(file2, panel, false, false, mgui);
        latencyDetailedAnalysis = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis();
        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);
            try {
                latencyDetailedAnalysis.getT().join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // if (latencyDetailedAnalysis.graphStatus() == Thread.State.TERMINATED) {
            dgt = latencyDetailedAnalysis.getDgraph();
            interpreter.print("graph=" + dgt.getGraphsize());
            TMLActivity activity;
            for (TMLTask tmltask : latencyDetailedAnalysis.getTmap1().getTMLModeling().getTasks()) {
                int opCount = 0;
                activity = tmltask.getActivityDiagram();
                TMLActivityDiagramPanel tadp = (TMLActivityDiagramPanel) (activity.getReferenceObject());
                List<TGComponent> list = tadp.getComponentList();
                Iterator<TGComponent> iterator = list.listIterator();
                TGComponent tgc;
                opCount = 0;
                iterator = list.listIterator();
                while (iterator.hasNext()) {
                    tgc = iterator.next();
                    String compName = "";
                    if (tgc.isEnabled()) {
                        if (tgc instanceof CheckableLatency) {
                            compName = tmltask.getName() + ":" + tgc.getName();
                            compName = compName.replaceAll(" ", "");
                            if (tgc.getValue().contains("(")) {
                                compName = compName + ":" + tgc.getValue().split("\\(")[0];
                            } else {
                                if (tgc instanceof TMLADExecI) {
                                    compName = ((TMLADExecI) tgc).getDelayValue();
                                }
                            }
                            checkedT.put(compName + "__" + tgc.getDIPLOID(), tgc.getDIPLOID());
                        }
                    }
                }
            }
            return null;
        }
        return mappingDiagName;
    }

    private String computeLat(String command, Interpreter interpreter) {
        String[] commands = command.split(" ");
        String task1 = null, task2 = null;
        int operator1ID = Integer.valueOf(commands[0]);
        int operator2ID = Integer.valueOf(commands[1]);
        taint = Boolean.parseBoolean(commands[2]);
        String filename = commands[3];
        for (Entry<String, Integer> cT : checkedT.entrySet()) {
            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == operator1ID) {
                task1 = taskName;
            } else if (id == operator2ID) {
                task2 = taskName;
            }
        }
        transFile1 = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(traceFile));
        allLatencies = dgt.latencyDetailedAnalysis(task1, task2, transFile1, taint, false);
        interpreter.print("lat=" + allLatencies.length);
        if (taint) {
            minMaxArray = dgt.latencyMinMaxAnalysisTaintedData(task1, task2, transFile1);
        } else {
            minMaxArray = dgt.latencyMinMaxAnalysis(task1, task2, transFile1);
        }
        // dgt.getRowDetailsMinMax(1);
        // taskHWByRowDetails = dgt.getTasksByRowMinMax(1);
        interpreter.print("minMaxArray.length=" + minMaxArray.length);
        // interpreter.print("taskHWByRowDetails.length=" + taskHWByRowDetails.length);
        // taskHWByRowDetails = dgt.getTaskHWByRowDetailsMinMax(1);
        // interpreter.print("taskHWByRowDetails.length=" + taskHWByRowDetails.length);
        dgt.saveLAtencyValuesToXML(filename);
        return null;
    }

    private String listAllOperators(String command, Interpreter interpreter) {
        String allop = "";
        for (Entry<String, Integer> cT : checkedT.entrySet()) {
            int id = cT.getValue();
            allop = allop.concat(id + " ");
        }
        interpreter.print(allop);
        return null;
    }

    private String preciseLatByRow(String command, Interpreter interpreter) {
        String[] commands = command.split(" ");
        String i = commands[0];
        Boolean minmax = Boolean.parseBoolean(commands[1]);
        String filename = commands[2];
        interpreter.print("minmax " + minmax);
        interpreter.print("taint" + taint);
        int id = Integer.valueOf(i);
        if (!minmax) {
            // detailedLatency = dgt.getTaskByRowDetails(id);
            // interpreter.print("detailedLatency.length=" + detailedLatency.length);
            // detailedLatency = dgt.getTaskHWByRowDetails(id);
            // interpreter.print("detailedLatency.length=" + detailedLatency.length);
            try {
                new JFrameLatencyDetailedPopup(dgt, id, true, taint, LatencyDetailedAnalysisMain.getTc(), false);
                interpreter.print("dgt.getOffPathBehavior=" + dgt.getOffPath().size());
                interpreter.print("dgt.getOffPathBehaviorCausingDelay=" + dgt.getOffPathDelay().size());
                interpreter.print("dgt.getOnPath=" + dgt.getOnPath().size());
                dgt.saveDetailsToXML(filename);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                new JFrameLatencyDetailedPopup(dgt, id, false, taint, LatencyDetailedAnalysisMain.getTc(), false);
                interpreter.print("dgt.getOffPathBehavior=" + dgt.getOffPath().size());
                interpreter.print("dgt.getOffPathBehaviorCausingDelay=" + dgt.getOffPathDelay().size());
                interpreter.print("dgt.getOnPath=" + dgt.getOnPath().size());
                dgt.saveDetailsToXML(filename);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    private String PlanByRow(String command, Interpreter interpreter) {
        String[] commands = command.split(" ");
        String i = commands[0];
        Boolean minmax = Boolean.parseBoolean(commands[1]);
        String filename = commands[2];
        interpreter.print("minmax " + minmax);
        interpreter.print("taint" + taint);
        int row = Integer.valueOf(i);
        if (!minmax && !taint) {
            dgraph.getRowDetailsMinMax(row);
        }
        // detailedLatency = dgt.getTaskByRowDetails(id);
        // interpreter.print("detailedLatency.length=" + detailedLatency.length);
        // detailedLatency = dgt.getTaskHWByRowDetails(id);
        // // interpreter.print("detailedLatency.length=" + detailedLatency.length);
        PlanArrays arrays = new PlanArrays();
        arrays.fillArrays(dgraph, row, !minmax, taint);
        interpreter.print("dgraph.getOffPathBehavior=" + dgraph.getOffPath().size());
        interpreter.print("dgraph.getOffPathBehaviorCausingDelay=" + dgraph.getOffPathDelay().size());
        interpreter.print("dgraph.getOnPath=" + dgraph.getOnPath().size());
        dgraph.saveDetailsToXML(filename);
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String generateGraphFromTML(String command, Interpreter interpreter) {
        String[] commands = command.split(" ");
        String tmapLocation = commands[0];
        String[] l = commands[0].split("/");
        String loctaion = "";
        interpreter.print(l[l.length - 1]);
        String fName = l[l.length - 1].replace(".tmap", "");
        interpreter.print(fName);
        loctaion = l[0];
        for (int i = 1; i < l.length - 1; i++) {
            loctaion = loctaion + "/" + l[i];
        }
        loctaion = loctaion + "/";
        interpreter.print(loctaion);
        String spec = null;
        File f = new File(commands[0]);
        TMLMappingTextSpecification tmts = new TMLMappingTextSpecification(fName);
        interpreter.print(tmts.toString() + "tmts");
        try {
            interpreter.print(f.getPath() + "f");
            spec = FileUtils.loadFileData(f);
        } catch (Exception e) {
        }
        interpreter.print(spec.toString() + "spec");
        boolean parsed = tmts.makeTMLMapping(spec, loctaion);
        interpreter.print(tmts.toString() + "tmts");
        tmap = tmts.getTMLMapping();
        if (tmap == null) {
            interpreter.print(tmap + "is null");
        }
        syntax = new TMLSyntaxChecking(tmap);
        syntax.checkSyntax();
        dgraph = new DependencyGraphTranslator(tmap);
        dgraph.DrawDirectedGraph();
        interpreter.print("Draw done");
        return null;
    }

    private String exportGGraphml(String command, Interpreter interpreter) {
        String f = command;
        try {
            dgraph.exportGraph(f);
            interpreter.print("print done");
        } catch (ExportException e) {
            interpreter.print(e.getMessage());
        } catch (IOException e) {
            interpreter.print(e.getMessage());
        }
        return null;
    }

    private String exportGpng(String command, Interpreter interpreter) {
        String f = command;
        try {
            dgraph.showGraph(dgraph);
            dgraph.getFrame().setVisible(false);
            dgraph.exportGraphAsImage(f);
            interpreter.print("print done");
        } catch (ExportException e) {
            interpreter.print(e.getMessage());
        } catch (IOException e) {
            interpreter.print(e.getMessage());
        }
        return null;
    }

    private String showAllOp(String command, Interpreter interpreter) {
        TMLActivity activity;
        for (TMLTask tmltask : tmap.getTMLModeling().getTasks()) {
            activity = tmltask.getActivityDiagram();
            List<TMLActivityElement> list = activity.getElements();
            Iterator<TMLActivityElement> iterator = list.listIterator();
            TMLActivityElement tgc;
            iterator = list.listIterator();
            while (iterator.hasNext()) {
                tgc = iterator.next();
                String compName = "";
                compName = tmltask.getName() + ":" + tgc.getName();
                compName = compName.replaceAll(" ", "");
                checkedT.put(compName + "__" + tgc.getID(), tgc.getID());
            }
        }
        for (Entry<String, Integer> cT : checkedT.entrySet()) {
            interpreter.print(cT.getValue() + "--" + cT.getKey());
        }
        ;
        return null;
    }

    private String computeLatencyValues(String command, Interpreter interpreter) {
        String[] commands = command.split(" ");
        String task1 = null, task2 = null;
        int operator1ID = Integer.valueOf(commands[0]);
        int operator2ID = Integer.valueOf(commands[1]);
        taint = Boolean.parseBoolean(commands[2]);
        String filename = commands[3];
        traceFile = commands[4];
        interpreter.print("transFile1=" + traceFile);
        for (Entry<String, Integer> cT : checkedT.entrySet()) {
            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == operator1ID) {
                task1 = taskName;
            } else if (id == operator2ID) {
                task2 = taskName;
            }
        }
        interpreter.print("task1=" + task1);
        interpreter.print("task2=" + task2);
        interpreter.print("taint=" + taint);
        transFile1 = dgraph.parseFile(new File(traceFile));
        interpreter.print("transFile1=" + transFile1.size());
        if (task1 != null && task2 != null && transFile1 != null && taint != null) {
            allLatencies = dgraph.latencyDetailedAnalysis(task1, task2, transFile1, taint, false);
            interpreter.print("lat=" + allLatencies.length);
            if (taint) {
                minMaxArray = dgraph.latencyMinMaxAnalysisTaintedData(task1, task2, transFile1);
            } else {
                minMaxArray = dgraph.latencyMinMaxAnalysis(task1, task2, transFile1);
            }
            // dgt.getRowDetailsMinMax(1);
            // taskHWByRowDetails = dgt.getTasksByRowMinMax(1);
            interpreter.print("minMaxArray.length=" + minMaxArray.length);
            // interpreter.print("taskHWByRowDetails.length=" + taskHWByRowDetails.length);
            // taskHWByRowDetails = dgt.getTaskHWByRowDetailsMinMax(1);
            // interpreter.print("taskHWByRowDetails.length=" + taskHWByRowDetails.length);
            dgraph.saveLAtencyValuesToXML(filename);
        } else {
            interpreter.print("one of the inputs is  empty");
        }
        return null;
    }
}
