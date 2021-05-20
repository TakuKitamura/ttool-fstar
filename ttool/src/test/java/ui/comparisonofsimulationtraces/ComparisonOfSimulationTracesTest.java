package ui.comparisonofsimulationtraces;

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
  protected static SimulationTrace selectedST1;
  protected static SimulationTrace selectedST2;
  protected static String STfilePath1;
  protected static String STfilePath2;
  private static JFrameCompareSimulationTraces cSimTrace;
  private static CompareSimulationTrace newContentPane;
  private static JFrameShowLatencyDetails showLatencyDetails;
  private static final String PATH1 = "/ui/xmlCompare/input/simple.xml";
  private static final String PATH2 = "/ui/xmlCompare/input/simplifiedWithSecurity.xml";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    STfilePath1 = getBaseResourcesDir() + PATH1;
    STfilePath2 = getBaseResourcesDir() + PATH2;
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
  }

  @Test
  public void diffTest() throws SAXException, IOException, ParserConfigurationException {
    newContentPane = new CompareSimulationTrace();
    newContentPane.JPanelCompareXmlGraph(cSimTrace.getTransFile1(), cSimTrace.getTransFile2());
    assertTrue(newContentPane.getTable().getRowCount() > 0);
  }

}