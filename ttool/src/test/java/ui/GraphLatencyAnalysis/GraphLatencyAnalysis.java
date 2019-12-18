package ui.GraphLatencyAnalysis;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ui.AbstractUITest;
import ui.TMLArchiPanel;
import ui.directedgraph.DirectedGraphTranslator;
import ui.directedgraph.JFrameLatencyDetailedAnalysis;
import ui.directedgraph.JFrameLatencyDetailedPopup;
import ui.interactivesimulation.SimulationTransaction;

public class GraphLatencyAnalysis extends AbstractUITest {

    private static final String simulationTracePath = "/ui/graphLatencyAnalysis/input/graphTest.xml";
    private static final String modelPath = "/ui/graphLatencyAnalysis/input/GraphTest.xml";
    // private static final String simulationTracePath =
    // "/home/maysam/eclipse/TTool/ttool/src/test/resources/ui/graphLatencyAnalysis/input/graphTest.xml";
    // private static final String modelPath =
    // "/home/maysam/eclipse/TTool/ttool/src/test/resources/ui/graphLatencyAnalysis/input/GraphTest.xml";

    private static final String mappingDiagName = "Architecture2";
    private Vector<SimulationTransaction> transFile1;
    private Vector<String> dropDown;

    private static final String t1 = "Application2__task4__send event: evt1(t)__44";
    private static final String t2 = "Application2__task22__read channel: comm_0(1) __26";
    private static String task1;
    private static String task2;
    private static DirectedGraphTranslator dgt;

    private static Object[][] allLatencies, minMaxArray, taskHWByRowDetails, detailedLatency;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;

    @Before
    public void GraphLatencyAnalysis() {

        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));
        // mainGUI.openProjectFromFile(new File( modelPath));

        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);

        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }

        mainGUI.checkModelingSyntax(panel, true);
        mainGUI.latencyDetailedAnalysis();

        latencyDetailedAnalysis = mainGUI.getLatencyDetailedAnalysis();
        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);
            while (latencyDetailedAnalysis.graphStatus() != Thread.State.TERMINATED) {
                dgt = latencyDetailedAnalysis.getDgraph();
            }
        }

    }

    @Test
    public void parseFile() {

        assertNotNull(latencyDetailedAnalysis);

        int graphsize = dgt.getGraphsize();

        assertTrue(graphsize == 40);

        dropDown = latencyDetailedAnalysis.loadDropDowns();

        assertTrue(dropDown.size() == 11);

        transFile1 = mainGUI.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + simulationTracePath));

        // transFile1 = mainGUI.getLatencyDetailedAnalysis() .parseFile(new File(
        // simulationTracePath));

        assertTrue(transFile1.size() == 175);

        int i = dropDown.indexOf(t1);
        int j = dropDown.indexOf(t2);

        task1 = dropDown.get(i);
        task2 = dropDown.get(j);

        allLatencies = dgt.latencyDetailedAnalysis(task1, task2, transFile1);

        assertTrue(allLatencies.length == 10);

        minMaxArray = dgt.latencyMinMaxAnalysis(task1, task2, transFile1);
        dgt.getRowDetailsMinMax(1);
        taskHWByRowDetails = dgt.getTasksByRowMinMax(1);

        assertTrue(minMaxArray.length > 0);

        assertTrue(taskHWByRowDetails.length == 12);
        taskHWByRowDetails = dgt.getTaskHWByRowDetailsMinMax(1);
        assertTrue(taskHWByRowDetails.length == 13);

        detailedLatency = dgt.getTaskByRowDetails(7);
        assertTrue(detailedLatency.length == 12);

        detailedLatency = dgt.getTaskHWByRowDetails(7);
        assertTrue(detailedLatency.length == 14);

    }

}