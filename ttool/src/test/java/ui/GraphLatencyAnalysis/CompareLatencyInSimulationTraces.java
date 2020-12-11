package ui.GraphLatencyAnalysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.simulationtraceanalysis.DirectedGraphTranslator;
import ui.simulationtraceanalysis.JFrameCompareLatencyDetail;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.ThreadingClass;
import ui.simulationtraceanalysis.latencyDetailedAnalysisMain;

public class CompareLatencyInSimulationTraces extends AbstractUITest {

    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";

    private static final String simulationTracePathFile1 = INPUT_PATH + "/graphTestSimulationTrace.xml";

    private static final String simulationTracePathFile2 = INPUT_PATH + "/testFunc.xml";
    private static final String modelPath = INPUT_PATH + "/GraphTestModel.xml";

    private static final String mappingDiagName = "Architecture2";

    private Vector<String> checkedTransactionsFile1 = new Vector<String>();
    private Vector<String> checkedTransactionsFile2 = new Vector<String>();

    private static final int t1 = 44;
    private static final int t2 = 26;
    private static final int t3 = 40;
    private static final int t4 = 28;
    private DirectedGraphTranslator dgraph1, dgraph2;
    private static String task1, task2, task3, task4;
    private JFrameCompareLatencyDetail cld;
    private latencyDetailedAnalysisMain latencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis jFrameLatencyDetailedAnalysis;

    private Vector<SimulationTransaction> transFile1, transFile2;
    private SimulationTrace simT1, simT2;
    private File file1, file2;
    private int row, row2;

    private HashMap<String, Integer> checkedT1 = new HashMap<String, Integer>();
    private HashMap<String, Integer> checkedT2 = new HashMap<String, Integer>();

    // protected MainGUI mainGUI1 = null;

    private static Object[][] dataDetailedByTask, dataDetailedByTask2, dataHWDelayByTask, dataHWDelayByTask2;

    public CompareLatencyInSimulationTraces() {

        super();

    }

    @Before
    public void GraphLatencyAnalysis()
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, InterruptedException {

        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));

        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);

        simT1 = new SimulationTrace("graphTestSimulationTrace", 6, (getBaseResourcesDir() + simulationTracePathFile1));

        latencyDetailedAnalysisMain = new latencyDetailedAnalysisMain(3, mainGUI, simT1, false, false, 3);
        latencyDetailedAnalysisMain.setCheckedTransactionsFile(new Vector<String>());

        try {
            latencyDetailedAnalysisMain.latencyDetailedAnalysisForXML(mainGUI, simT1, false, true, 1);
            latencyDetailedAnalysisMain.setTc(new ThreadingClass("Thread-1", latencyDetailedAnalysisMain));

            cld = new JFrameCompareLatencyDetail(latencyDetailedAnalysisMain, mainGUI, checkedTransactionsFile1,
                    latencyDetailedAnalysisMain.getMap1(), latencyDetailedAnalysisMain.getCpanels1(), simT1, false,
                    latencyDetailedAnalysisMain.getTc());

            if (cld == null) {
                System.out.println("NULL Panel");
            } else {
                cld.setVisible(false);
            }

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        latencyDetailedAnalysisMain.getTc().getT().join();
        if (cld.getDgraph().getGraphsize() > 0) {
            dgraph1 = cld.getDgraph();
            checkedTransactionsFile1 = latencyDetailedAnalysisMain.getCheckedTransactionsFile();
            checkedT1 = latencyDetailedAnalysisMain.getCheckedT1();
            cld.setDgraph(null);

            latencyDetailedAnalysisMain.setCheckedTransactionsFile(new Vector<String>());
            simT2 = new SimulationTrace("graphTestSimulationTrace", 6, (getBaseResourcesDir() + simulationTracePathFile2));

            try {

                latencyDetailedAnalysisMain.latencyDetailedAnalysisForXML(mainGUI, simT2, false, true, 1);

            } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            dgraph2 = new DirectedGraphTranslator(jFrameLatencyDetailedAnalysis, cld, latencyDetailedAnalysisMain.getMap1(),
                    latencyDetailedAnalysisMain.getCpanels1(), 3);

            checkedTransactionsFile2 = latencyDetailedAnalysisMain.getCheckedTransactionsFile();
            checkedT2 = latencyDetailedAnalysisMain.getCheckedT2();

        }

    }

    @Test
    public void parseFile() {

        int graphsize = dgraph1.getGraphsize();

        assertTrue(1 > 0);

        graphsize = dgraph2.getGraphsize();

        assertTrue(graphsize > 0);

        assertTrue(checkedT1.size() == 3);

        assertTrue(checkedT2.size() == 3);

        for (Entry<String, Integer> cT : checkedT1.entrySet()) {

            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == t1) {
                task1 = taskName;

            } else if (id == t2) {
                task2 = taskName;

            }
        }

        for (Entry<String, Integer> cT : checkedT2.entrySet()) {

            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == t3) {
                task3 = taskName;

            } else if (id == t4) {
                task4 = taskName;

            }
        }

        file1 = new File(simT1.getFullPath());
        file2 = new File(simT2.getFullPath());

        transFile1 = cld.parseFile(file1);
        transFile2 = cld.parseFile(file2);

        cld.setDgraph2(dgraph2);

        cld.latencyDetailedAnalysis(task1, task2, task3, task4, transFile1, transFile2, false, false, false);

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