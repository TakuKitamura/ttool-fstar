package ui.graphlatencyanalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import org.junit.Before;
import org.junit.Test;
import myutil.TraceManager;
import tmltranslator.simulation.DependencyGraphTranslator;
import tmltranslator.simulation.SimulationTransaction;
import tmltranslator.simulation.Vertex;
import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.LatencyDetailedAnalysisMain;

public class NestedStructurePLANTest extends AbstractUITest {
    private static final String MAPPING_DIAG_NAME = "Architecture";
    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String SIMULATIONTRACE_PATH_FILE = INPUT_PATH + "/loopseqTrace.xml";
    private static final String MODEL_PATH = INPUT_PATH + "/loopseqgraph.xml";
    private static final int OPERATOR1_ID = 69;
    private static final int OPERATOR2_ID = 53;
    private static final String GRAPH = INPUT_PATH + "/seqGraph1";
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private DependencyGraphTranslator dgt;
    private Vector<SimulationTransaction> transFile1;
    private HashMap<String, Integer> checkedDropDown = new HashMap<String, Integer>();
    private String task1;
    private String task2;
    private Object[][] allLatencies;

    @Before
    public void NestedStructurePLAN() throws InterruptedException {
        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + MODEL_PATH));
        final TMLArchiPanel panel = findArchiPanel(MAPPING_DIAG_NAME);
        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }
        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("", 6, SIMULATIONTRACE_PATH_FILE);
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
                e.printStackTrace();
            }
            dgt = latencyDetailedAnalysis.getDgraph();
        }
    }

    @Test
    public void parseFile() {
        assertNotNull(latencyDetailedAnalysis);
        int graphsize = dgt.getGraphsize();
        assertTrue(graphsize >= 57);
        if (GRAPH != null) {
            try {
                Boolean test = dgt.compareWithImported(GRAPH);
                assertTrue(test);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        transFile1 = LatencyDetailedAnalysisMain.getLatencyDetailedAnalysis()
                .parseFile(new File(getBaseResourcesDir() + SIMULATIONTRACE_PATH_FILE));
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
