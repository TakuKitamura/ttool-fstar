package ui.totml;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import myutil.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.TMLMapping;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.AbstractUITest;
import ui.TDiagramPanel;
import ui.TMLArchiPanel;
import ui.TURTLEPanel;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RemoveAllTransactionsTests extends AbstractUITest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_PARSE_HTML = {"parseFPGA_HTML"};
    final static String EXPECTED_FILE_REMOVE_ALL_TRANS = getBaseResourcesDir() + "tmltranslator/expected/expected_remove_all_trans.txt";
    final int [] FULL_DATA_TRANSACTION = {20, 19, 476, 476};
    final int [] REMOVE_DATA_TRANSACTION = {8, 7, 476, 476};
    private String SIM_DIR;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";

    }
    public RemoveAllTransactionsTests() {
        super();
        //mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/";
    }
    @Test
    public void testRemoveAllTransactions() throws Exception {
        for (int i = 0; i < MODELS_PARSE_HTML.length; i++) {
            String s = MODELS_PARSE_HTML[i];
            SIM_DIR = DIR_GEN + s + "_rmat/";
            System.out.println("executing: checking syntax " + s);
            // select architecture tab
            mainGUI.openProjectFromFile(new File(RESOURCES_DIR + s + ".xml"));
            for(TURTLEPanel _tab : mainGUI.getTabs()) {
                if(_tab instanceof TMLArchiPanel) {
                    for (TDiagramPanel tdp : _tab.getPanels()) {
                        if (tdp instanceof TMLArchiDiagramPanel) {
                            mainGUI.selectTab(tdp);
                            break;
                        }
                    }
                    break;
                }
            }
            mainGUI.checkModelingSyntax(true);
            TMLMapping tmap = mainGUI.gtm.getTMLMapping();
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertTrue(syntax.hasErrors() == 0);
            // Generate SystemC code
            System.out.println("executing: sim code gen for " + s);
            final IDiploSimulatorCodeGenerator tml2systc;
            List<EBRDD> al = new ArrayList<EBRDD>();
            List<TEPE> alTepe = new ArrayList<TEPE>();
            tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
            tml2systc.setModelName(s);
            String error = tml2systc.generateSystemC(false, true);
            assertTrue(error == null);

            File directory = new File(SIM_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Putting sim files
            System.out.println("SIM executing: sim lib code copying for " + s);
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + "../../../../simulators/c++2/";
            boolean simFiles = SpecConfigTTool.checkAndCreateSystemCDir(SIM_DIR);

            System.out.println("SIM executing: sim lib code copying done with result " + simFiles);
            assertTrue(simFiles);

            System.out.println("SIM Saving file in: " + SIM_DIR);
            tml2systc.saveFile(SIM_DIR, "appmodel");

            // Compile it
            System.out.println("executing: compile");
            Process proc;
            BufferedReader proc_in;
            String str;
            boolean mustRecompileAll;
            Penalties penalty = new Penalties(SIM_DIR  + "src_simulator");
            int changed = penalty.handlePenalties(false);

            if (changed == 1) {
                mustRecompileAll = true;
            } else {
                mustRecompileAll = false;
            }

            if (mustRecompileAll) {
                System.out.println("executing: " + "make -C " + SIM_DIR + " clean");
                try {
                    proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + " clean");
                    proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while ((str = proc_in.readLine()) != null) {
                        // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                        System.out.println("executing: " + str);
                    }
                } catch (Exception e) {
                    // probably make is not installed
                    System.out.println("FAILED: executing: " + "make -C " + SIM_DIR + " clean");
                    return;
                }
            }

            System.out.println("executing: " + "make -C " + SIM_DIR);
            try {

                proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + "");
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing: " + "make -C " + SIM_DIR);
                return;
            }
            System.out.println("SUCCESS: executing: " + "make -C " + SIM_DIR);

            // Run the simulator
            String graphPath = SIM_DIR + "testgraph_" + s;
            try {

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                //first remove all transactions when the transacList is empty to see it crash or not
                // second, run 100 time units and then remove all transactions again
                //list 100 recent transactions on TransacList to check it is empty or not
                //run again next 100 time units
                // save trace file and check the transactions displayed on the trace.
                params[2] = "26 1;1 6 100; 26 1;22 100; 1 6 100; 7 2 " + graphPath + "_save.txt" + "; 1 0; 1 7 100 100 " + graphPath + "_save";
                proc = Runtime.getRuntime().exec(params);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                monitorError(proc);
                PrintStream out = new PrintStream(new FileOutputStream(graphPath + ".txt"));
                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                    if(str.contains("Info transaction:")){
                        out.append(str + "\n");
                    }
                }

                params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 0; 1 7 100 100 " + graphPath + "_full";
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }

            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }

            // Compare results with expected ones
            // Must load the graph
            File file = new File(graphPath + ".txt");
            assertTrue(file.length() == 0);// check transacList empty or not

            BufferedReader reader1 = new BufferedReader(new FileReader(graphPath + "_save.txt"));

            BufferedReader reader2 = new BufferedReader(new FileReader(EXPECTED_FILE_REMOVE_ALL_TRANS));

            String line1 = reader1.readLine();

            String line2 = reader2.readLine();

            boolean areEqual = true;

            int lineNum = 1;

            while (line1 != null || line2 != null) {
                if (line1 == null || line2 == null) {
                    areEqual = false;
                    break;
                } else if (!line1.equalsIgnoreCase(line2)) {
                    areEqual = false;
                    break;
                }
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                lineNum++;
            }

            if (areEqual) {
                System.out.println("Two files have same content.");
                assertTrue(areEqual);
            } else {
                System.out.println("Two files have different content. They differ at line " + lineNum);
                System.out.println("File1 has " + line1 + " and File2 has " + line2 + " at line " + lineNum);
                assertTrue(areEqual);
            }
            reader1.close();
            reader2.close();

            //compare number of states
            //Non remove trans check
            File graphFile = new File(graphPath + "_full.aut");
            String graphData = "";
            try {
                graphData = FileUtils.loadFileData(graphFile);
            } catch (Exception e) {
                assertTrue(false);
            }

            AUTGraph graph = new AUTGraph();
            graph.buildGraph(graphData);
            // States and transitions
            System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates());
            assertTrue(FULL_DATA_TRANSACTION[0] == graph.getNbOfStates());
            System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
            assertTrue(FULL_DATA_TRANSACTION[1] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of " + s + " " + minValue);
            assertTrue(FULL_DATA_TRANSACTION[2] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of " + s + " " + maxValue);
            assertTrue(FULL_DATA_TRANSACTION[3] == maxValue);

            //Remove trans check
            graphFile = new File(graphPath + "_save.aut");
            graphData = "";
            try {
                graphData = FileUtils.loadFileData(graphFile);
            } catch (Exception e) {
                assertTrue(false);
            }

            graph = new AUTGraph();
            graph.buildGraph(graphData);
            // States and transitions
            System.out.println("executing: nb states of remove transactions " + s + " " + graph.getNbOfStates());
            assertTrue(REMOVE_DATA_TRANSACTION[0] == graph.getNbOfStates());
            System.out.println("executing: nb transitions of remove transactions " + s + " " + graph.getNbOfTransitions());
            assertTrue(REMOVE_DATA_TRANSACTION[1] == graph.getNbOfTransitions());

            // Min and max cycles
            minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of remove transactions " + s + " " + minValue);
            assertTrue(REMOVE_DATA_TRANSACTION[2] == minValue);

            maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of remove transactions " + s + " " + maxValue);
            assertTrue(REMOVE_DATA_TRANSACTION[3] == maxValue);
        }
    }
}
