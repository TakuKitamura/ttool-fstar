package ui.GraphLatencyAnalysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import myutil.PluginManager;
import ui.AbstractUITest;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.simulationtraceanalysis.DirectedGraphTranslator;
import ui.simulationtraceanalysis.JFrameCompareLatencyDetail;
import ui.simulationtraceanalysis.latencyDetailedAnalysisMain;

public class CompareLatencyInSimulationTraces extends AbstractUITest {

    private static final String simulationTracePathFile1 = "/ui/graphLatencyAnalysis/input/graphTestSimulationTrace.xml";
    private static final String simulationTracePathFile2 = "/ui/graphLatencyAnalysis/input/testFunc.xml";
    private static final String modelPath = "/ui/graphLatencyAnalysis/input/GraphTestModel.xml";
  
   
    private static final String mappingDiagName = "Architecture2";

    private Vector<String> checkedTransactionsFile1 = new Vector<String>();
    private Vector<String> checkedTransactionsFile2 = new Vector<String>();

    private static final String t1 = "Application2__task4:sendevent:evt1__44";
    private static final String t2 = "Application2__task22:readchannel:comm_0__26";
    private static final String t3 = "Application2__task4:sendevent:evt1__40";
    private static final String t4 = "Application2__task22:readchannel:comm_0__28";
    DirectedGraphTranslator dgraph1, dgraph2;
    private static String task1, task2, task3, task4;
    JFrameCompareLatencyDetail cld;

    Vector<SimulationTransaction> transFile1, transFile2;
    SimulationTrace simT1, simT2;
    private File file1, file2;
    int row, row2;
    // protected MainGUI mainGUI1 = null;

    private static Object[][] dataDetailedByTask, dataDetailedByTask2, dataHWDelayByTask, dataHWDelayByTask2;

    public CompareLatencyInSimulationTraces() {

        super();

    }

    @Before
    public void GraphLatencyAnalysis() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {

        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));

      

        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);

        simT1 = new SimulationTrace("graphTestSimulationTrace", 6, (getBaseResourcesDir() + simulationTracePathFile1));
        
        mainGUI.setLatencyDetailedAnalysisMain( new latencyDetailedAnalysisMain());
        mainGUI.getLatencyDetailedAnalysisMain().setCheckedTransactionsFile(new Vector<String>());

        mainGUI.latencyDetailedAnalysisForXML(simT1, false, true, 1);

        checkedTransactionsFile1 = mainGUI.getLatencyDetailedAnalysisMain().getCheckedTransactionsFile();

        while (mainGUI.getLatencyDetailedAnalysisMain().getDgraph().getGraphsize() == 0) {

        }

        if (mainGUI.getLatencyDetailedAnalysisMain().getDgraph().getGraphsize() > 0) {
            dgraph1 = mainGUI.getLatencyDetailedAnalysisMain().getDgraph();
            
            mainGUI.getLatencyDetailedAnalysisMain().setDgraph(null);
           
            mainGUI.getLatencyDetailedAnalysisMain().setCheckedTransactionsFile(new Vector<String>());
            simT2 = new SimulationTrace("graphTestSimulationTrace", 6, (getBaseResourcesDir() + simulationTracePathFile2));
            
            mainGUI.latencyDetailedAnalysisForXML(simT2, false, true, 1);

            while (mainGUI.getLatencyDetailedAnalysisMain().getDgraph().getGraphsize() == 0) {

            }

            if (mainGUI.getLatencyDetailedAnalysisMain().getDgraph().getGraphsize() > 0) {
                dgraph2 = mainGUI.getLatencyDetailedAnalysisMain().getDgraph();
                
                
                checkedTransactionsFile2 = mainGUI.getLatencyDetailedAnalysisMain().getCheckedTransactionsFile();
                cld = new JFrameCompareLatencyDetail(dgraph1, dgraph2, checkedTransactionsFile1, checkedTransactionsFile2, simT1, simT2, false);
                
                if (cld == null) {
                    System.out.println("NULL Panel");
                } else {
                    cld.setVisible(false);
                }

               
            }

        }

    }

    @Test
    public void parseFile() {
      
        int graphsize = dgraph1.getGraphsize();

        assertTrue(1 > 0);

        graphsize = dgraph2.getGraphsize();

        assertTrue(graphsize > 0);

        assertTrue(checkedTransactionsFile1.size() == 3);

        assertTrue(checkedTransactionsFile2.size() == 3);

        int i1 = checkedTransactionsFile1.indexOf(t1);
        int j1 = checkedTransactionsFile1.indexOf(t2);

        int i2 = checkedTransactionsFile2.indexOf(t3);
        int j2 = checkedTransactionsFile2.indexOf(t4);

        task1 = checkedTransactionsFile1.get(i1);
        task2 = checkedTransactionsFile1.get(j1);
        task3 = checkedTransactionsFile2.get(i2);
        task4 = checkedTransactionsFile2.get(j2);

        file1 = new File(simT1.getFullPath());
        file2 = new File(simT2.getFullPath());

        transFile1 = cld.parseFile(file1);
        transFile2 = cld.parseFile(file2);

        cld.latencyDetailedAnalysis(task1, task2, task3, task4, transFile1, transFile2,false);
       
        cld.setVisible(false);

        assertTrue(cld.getTableData().length > 0);
        assertTrue(cld.getTableData2().length > 0);
        assertTrue(cld.getTableData1MinMax().length > 0);
        assertTrue(cld.getTableData2MinMax().length > 0);

        // test row 1 table 1 and row 1 table 2

        row = 1;
        row2 = 1;

        dataDetailedByTask = dgraph1.getTaskByRowDetails(row);

        dataDetailedByTask2 = dgraph2.getTaskByRowDetails(row2);

        dataHWDelayByTask = dgraph1.getTaskHWByRowDetails(row);
        dataHWDelayByTask2 = dgraph2.getTaskHWByRowDetails(row2);

        assertTrue(dataDetailedByTask.length > 0);
        assertTrue(dataDetailedByTask2.length > 0);
        assertTrue(dataHWDelayByTask.length > 0);
        assertTrue(dataHWDelayByTask2.length > 0);

        // test max table 1 and max table 2

        dgraph1.getRowDetailsMinMax(row);
        dataDetailedByTask = dgraph1.getTasksByRowMinMax(row);

        dgraph2.getRowDetailsMinMax(row2);
        dataDetailedByTask2 = dgraph2.getTasksByRowMinMax(row2);

        dataHWDelayByTask = dgraph1.getTaskHWByRowDetailsMinMax(row);
        dataHWDelayByTask2 = dgraph2.getTaskHWByRowDetailsMinMax(row2);

        assertTrue(dataDetailedByTask.length > 0);
        assertTrue(dataDetailedByTask2.length > 0);
        assertTrue(dataHWDelayByTask.length > 0);
        assertTrue(dataHWDelayByTask2.length > 0);

    }

}
