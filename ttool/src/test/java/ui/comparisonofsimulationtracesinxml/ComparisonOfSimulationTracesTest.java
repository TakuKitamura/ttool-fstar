package ui.comparisonofsimulationtracesinxml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import tmltranslator.simulation.CompareSimulationTrace;
import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.interactivesimulation.JFrameShowLatencyDetails;
import ui.simulationtraceanalysis.JFrameCompareSimulationTraces;

public class ComparisonOfSimulationTracesTest extends AbstractUITest {
    private static final String PATH1 = "/ui/xmlCompare/input/simple.xml";
    private static final String PATH2 = "/ui/xmlCompare/input/simplifiedWithSecurity.xml";
    private static String STfilePath1 = getBaseResourcesDir() + PATH1;
    private static String STfilePath2 = getBaseResourcesDir() + PATH2;

    private JFrameCompareSimulationTraces cSimTrace;
    private CompareSimulationTrace newContentPane;
    private JFrameShowLatencyDetails showLatencyDetails;
    private SimulationTrace selectedST1 = new SimulationTrace("", 6, STfilePath1);
    private SimulationTrace selectedST2 = new SimulationTrace("", 6, STfilePath2);

    @Test
    public void loadTest() {
        assertNotNull(selectedST1);
        assertNotNull(selectedST2);
    }

    @Test
    public void parseXMLTest() throws SAXException, IOException, ParserConfigurationException {
        cSimTrace = new JFrameCompareSimulationTraces(mainGUI, "", selectedST1, false);
        cSimTrace.setVisible(false);
        cSimTrace.parseXML(STfilePath1, STfilePath2);
        assertNotNull(cSimTrace.getTransFile1());
        assertNotNull(cSimTrace.getTransFile2());
        newContentPane = new CompareSimulationTrace();
        newContentPane.JPanelCompareXmlGraph(cSimTrace.getTransFile1(), cSimTrace.getTransFile2());
        assertTrue(newContentPane.getTable().getRowCount() > 0);
    }

}