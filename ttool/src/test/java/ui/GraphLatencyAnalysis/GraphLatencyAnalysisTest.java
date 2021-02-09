package ui.GraphLatencyAnalysis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import simulationtraceanalysis.DependencyGraphTranslator;
import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.LatencyDetailedAnalysisMain;

public class GraphLatencyAnalysisTest extends AbstractUITest {
    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String SIMULATIONTRACE_PATH = INPUT_PATH + "/graphTestSimulationTrace.xml";
    private static final String modelPath = INPUT_PATH + "/GraphTestModel.xml";
    private static final String mappingDiagName = "Architecture2";
    private Vector<SimulationTransaction> transFile1;
    // private Vector<String> dropDown;
    private HashMap<String, Integer> checkedDropDown = new HashMap<String, Integer>();
    private static final int OPERATOR1_ID = 44;
    private static final int OPERATOR2_ID = 26;
    private static String task1;
    private static String task2;
    private static DependencyGraphTranslator dgt;
    private static Object[][] allLatencies, minMaxArray, taskHWByRowDetails, detailedLatency;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;

    @Before
    public void GraphLatencyAnalysis() throws InterruptedException {
        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));
        // mainGUI.openProjectFromFile(new File( modelPath));
        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);
        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }
        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("graphTestSimulationTrace", 6, SIMULATIONTRACE_PATH);
        LatencyDetailedAnalysisMain = new LatencyDetailedAnalysisMain(3, mainGUI, file2, false, false, 3);
        LatencyDetailedAnalysisMain.getTc().setMainGUI(mainGUI);
        LatencyDetailedAnalysisMain.latencyDetailedAnalysis(file2, panel, false, false, mainGUI);
        latencyDetailedAnalysis = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis();
        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);
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
        assertTrue(graphsize == 40);
        checkedDropDown = latencyDetailedAnalysis.getCheckedT();
        assertTrue(checkedDropDown.size() == 3);
        transFile1 = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + SIMULATIONTRACE_PATH));
        assertTrue(transFile1.size() == 175);
        for (Entry<String, Integer> cT : checkedDropDown.entrySet()) {
            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == OPERATOR1_ID) {
                task1 = taskName;
            } else if (id == OPERATOR2_ID) {
                task2 = taskName;
            }
        }
        allLatencies = dgt.latencyDetailedAnalysis(task1, task2, transFile1, false, false);
        assertTrue(allLatencies.length == 10);
        minMaxArray = dgt.latencyMinMaxAnalysis(task1, task2, transFile1);
        dgt.getRowDetailsMinMax(1);
        taskHWByRowDetails = dgt.getTasksByRowMinMax(1);
        assertTrue(minMaxArray.length > 0);
        assertTrue(taskHWByRowDetails.length == 15);
        taskHWByRowDetails = dgt.getTaskHWByRowDetailsMinMax(1);
        assertTrue(taskHWByRowDetails.length == 13);
        detailedLatency = dgt.getTaskByRowDetails(7);
        assertTrue(detailedLatency.length == 14);
        detailedLatency = dgt.getTaskHWByRowDetails(7);
        assertTrue(detailedLatency.length == 14);
    }
}