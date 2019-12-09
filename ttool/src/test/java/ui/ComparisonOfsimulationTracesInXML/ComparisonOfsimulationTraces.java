package ui.ComparisonOfsimulationTracesInXML;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.xml.sax.SAXException;

import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.interactivesimulation.JFrameCompareSimulationTraces;
import ui.interactivesimulation.JFrameShowLatencyDetails;
import ui.interactivesimulation.JPanelCompareXmlGraph;

public class ComparisonOfsimulationTraces extends AbstractUITest {

    protected static SimulationTrace selectedST1;
    protected static SimulationTrace selectedST2;

    protected static String STfilePath1;
    protected static String STfilePath2;

    private static JFrameCompareSimulationTraces cSimTrace;
    private static JPanelCompareXmlGraph newContentPane;
    private static JFrameShowLatencyDetails showLatencyDetails;
    private static final String path1 = "/ui/xmlCompare/input/simple.xml";
    private static final String path2 = "/ui/xmlCompare/input/simplifiedWithSecurity.xml";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        STfilePath1 = getBaseResourcesDir() + path1;
        STfilePath2 = getBaseResourcesDir() + path2;

        // STfilePath1 =
        // "/home/maysam/eclipse/TTool/ttool/src/test/resources/ui/xmlCompare/input/simple.xml";
        // STfilePath2 =
        // "/home/maysam/eclipse/TTool/ttool/src/test/resources/ui/xmlCompare/input/simplifiedWithSecurity.xml";
        selectedST1 = new SimulationTrace("simple.xml", 6, STfilePath1);
        selectedST2 = new SimulationTrace("simplifiedWithSecurity.xml", 6, STfilePath2);

    }

    @Test
    public void loadTest() {
        assertNotNull(selectedST1);
        assertNotNull(selectedST2);

    }

    @Test
    public void parseXMLTest() throws SAXException, IOException, ParserConfigurationException {

        cSimTrace = new JFrameCompareSimulationTraces(mainGUI, "Compare Simulation simulation", selectedST1, false);
        cSimTrace.setVisible(false);
        cSimTrace.parseXML(STfilePath1, STfilePath2);

        assertNotNull(cSimTrace.getTransFile1());
        assertNotNull(cSimTrace.getTransFile2());

        newContentPane = new JPanelCompareXmlGraph(cSimTrace.getTransFile1(), cSimTrace.getTransFile2());
        newContentPane.setVisible(false);

    }

    @Test
    public void diffTest() throws SAXException, IOException, ParserConfigurationException {

        assertTrue(newContentPane.getTable().getRowCount() > 0);

    }

    @Test
    public void latencyTest() throws SAXException, IOException, ParserConfigurationException {
        JFrameShowLatencyDetails showLatencyDetails = new JFrameShowLatencyDetails(cSimTrace.getTransFile1(), cSimTrace.getTransFile2(), "CPU1_1",
                "Request SmartCard", "CPU1_1", "Send end", false);

        showLatencyDetails.setVisible(false);

        assertTrue(showLatencyDetails.getTable11().getRowCount() > 0);

    }

}