package ui.GraphLatencyAnalysis;

import static org.junit.Assert.assertFalse;
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

public class NestedStructurePLANTest extends AbstractUITest {
    private static final String mapping_Diag_Name = "Architecture";
    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String SIMULATIONTRACE_PATH_FILE = INPUT_PATH + "/loopseqTrace.xml";
    private static final String MODEL_PATH = INPUT_PATH + "/loopseqgraph.xml";
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private static DependencyGraphTranslator dgt;
    private Vector<SimulationTransaction> transFile1;
    private HashMap<String, Integer> checkedDropDown = new HashMap<String, Integer>();
    private static final int OPERATOR1_ID = 69;
    private static final int OPERATOR2_ID = 53;
    private static String task1;
    private static String task2;
    private static Object[][] allLatencies;

    @Before
    public void NestedStructurePLAN() throws InterruptedException {
        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + MODEL_PATH));
        // mainGUI.openProjectFromFile(new File(modelPath));
        final TMLArchiPanel panel = findArchiPanel(mapping_Diag_Name);
        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }
        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("graphTestSimulationTrace", 6, SIMULATIONTRACE_PATH_FILE);
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
        System.out.println("Graphsize=" + graphsize);
        assertTrue(graphsize >= 57);
        // test sequence to all its nexts
        assertTrue(dgt.edgeExists(66, 65));
        assertTrue(dgt.edgeExists(66, 63));
        // test sequence branch to another
        assertTrue(dgt.edgeExists(64, 63));
        assertTrue(dgt.edgeExists(63, 62));
        // test ordered sequence
        assertTrue(dgt.edgeExists(62, 61));
        assertTrue(dgt.edgeExists(62, 60));
        // test nested sequence to unordered sequence
        assertTrue(dgt.edgeExists(57, 60));
        assertTrue(dgt.edgeExists(68, 60));
        // test unordered sequence nexts
        assertTrue(dgt.edgeExists(61, 67));
        assertTrue(dgt.edgeExists(61, 58));
        assertTrue(dgt.edgeExists(68, 58));
        assertTrue(dgt.edgeExists(57, 67));
        // test ordered sequence nexts
        assertTrue(dgt.edgeExists(26, 25));
        assertTrue(dgt.edgeExists(26, 21));
        assertTrue(dgt.edgeExists(26, 23));
        // test ordered sequence ends
        assertTrue(dgt.edgeExists(24, 21));
        // nested seq loop
        assertTrue(dgt.edgeExists(27, 23));
        assertTrue(dgt.edgeExists(30, 36));
        assertTrue(dgt.edgeExists(29, 36));
        // inside loop only connected to loop vertex
        assertFalse(dgt.edgeExists(30, 22));
        assertFalse(dgt.edgeExists(29, 22));
        // sequence last branch end not connected to other branches
        assertFalse(dgt.edgeExists(22, 25));
        assertFalse(dgt.edgeExists(22, 21));
        // sequence branches not connected backward
        assertFalse(dgt.edgeExists(27, 25));
        // loop for ever edges+ nested loops and seq
        assertTrue(dgt.edgeExists(44, 49));
        assertTrue(dgt.edgeExists(44, 51));
        assertTrue(dgt.edgeExists(44, 53));
        assertTrue(dgt.edgeExists(43, 42));
        assertTrue(dgt.edgeExists(43, 41));
        assertTrue(dgt.edgeExists(45, 41));
        assertTrue(dgt.edgeExists(40, 49));
        assertTrue(dgt.edgeExists(48, 54));
        assertTrue(dgt.edgeExists(48, 51));
        assertTrue(dgt.edgeExists(48, 53));
        assertTrue(dgt.edgeExists(50, 49));
        assertTrue(dgt.edgeExists(50, 53));
        assertTrue(dgt.edgeExists(50, 54));
        assertTrue(dgt.edgeExists(52, 51));
        assertTrue(dgt.edgeExists(52, 49));
        assertTrue(dgt.edgeExists(52, 54));
        assertFalse(dgt.edgeExists(40, 54));
        assertFalse(dgt.edgeExists(40, 42));
        transFile1 = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + SIMULATIONTRACE_PATH_FILE));
        assertTrue(transFile1.size() == 38);
        checkedDropDown = latencyDetailedAnalysis.getCheckedT();
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
        assertTrue(allLatencies.length == 1);
        assertTrue((int) allLatencies[0][1] == 60);
        assertTrue((int) allLatencies[0][3] == 212);
        assertTrue((int) allLatencies[0][4] == 152);
    }
}
