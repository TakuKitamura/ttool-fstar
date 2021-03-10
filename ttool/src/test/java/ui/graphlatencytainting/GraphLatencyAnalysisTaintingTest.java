package ui.graphlatencytainting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import tmltranslator.simulation.DependencyGraphTranslator;
import tmltranslator.simulation.SimulationTransaction;
import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.LatencyDetailedAnalysisMain;

public class GraphLatencyAnalysisTaintingTest extends AbstractUITest {
    // private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String simulationTracePath = INPUT_PATH + "/tainting.xml";
    private static final String modelPath = INPUT_PATH + "/GraphLatencyAnalysisTainting.xml";
    private static final String mappingDiagName = "Architecture";
    private Vector<SimulationTransaction> transFile1;
    private Vector<String> dropDown;
    private static final int operator1ID = 47;
    private static final int operator2ID = 37;
    private static String task1;
    private static String task2;
    private static DependencyGraphTranslator dgt;
    private static Object[][] allLatencies, minMaxArray, taskHWByRowDetails, detailedLatency;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;
    private HashMap<String, Integer> checkedDropDown = new HashMap<String, Integer>();

    @Before
    public void GraphLatencyAnalysis() throws InterruptedException {
        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));
        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);
        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }
        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("tainting", 6, simulationTracePath);
        LatencyDetailedAnalysisMain = new LatencyDetailedAnalysisMain(3, mainGUI, file2, false, false, 3);
        LatencyDetailedAnalysisMain.getTc().setMainGUI(mainGUI);
        LatencyDetailedAnalysisMain.latencyDetailedAnalysis(file2, panel, false, false, mainGUI);
        latencyDetailedAnalysis = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis();
        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);
            latencyDetailedAnalysis.getTc().run();
            try {
                latencyDetailedAnalysis.getT().join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dgt = latencyDetailedAnalysis.getDgraph();
        }
    }

    @Test
    public void parseFile() {
        assertNotNull(latencyDetailedAnalysis);
        int graphsize = dgt.getGraphsize();
        assertTrue(graphsize == 34);
        checkedDropDown = latencyDetailedAnalysis.getCheckedT();
        assertTrue(checkedDropDown.size() == 2);
        transFile1 = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + simulationTracePath));
        assertTrue(transFile1.size() > 0);
        for (Entry<String, Integer> cT : checkedDropDown.entrySet()) {
            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == operator1ID) {
                task1 = taskName;
            } else if (id == operator2ID) {
                task2 = taskName;
            }
        }
        allLatencies = dgt.latencyDetailedAnalysis(task1, task2, transFile1, true, false);
        assertTrue(allLatencies.length == 1);
        assertTrue(allLatencies[0][4] == Integer.valueOf(105));
        minMaxArray = dgt.latencyMinMaxAnalysis(task1, task2, transFile1);
        dgt.getRowDetailsMinMax(1);
        taskHWByRowDetails = dgt.getTasksByRowMinMax(0);
        assertTrue(minMaxArray.length > 0);
        assertTrue(taskHWByRowDetails.length == 12);
        taskHWByRowDetails = dgt.getTaskHWByRowDetailsMinMax(0);
        assertTrue(taskHWByRowDetails.length == 6);
        detailedLatency = dgt.getTaskByRowDetails(0);
        assertTrue(detailedLatency.length == 12);
        detailedLatency = dgt.getTaskHWByRowDetails(0);
        assertTrue(detailedLatency.length == 3);
    }
}