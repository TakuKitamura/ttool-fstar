package ui.simulationtraceanalysis;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import tmltranslator.TMLMapping;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TMLComponentDesignPanel;
import ui.interactivesimulation.SimulationTransaction;

public class LatencyAnalysisParallelAlgorithms implements Runnable {
    private Thread t;
    // private String threadName;
    private int algorithmId;
    private JFrameLatencyDetailedAnalysis jFrameLDA;
    private JFrameCompareLatencyDetail cld;
    private TMLMapping<TGComponent> tmap;
    private List<TMLComponentDesignPanel> cpanels;
    private DirectedGraphTranslator dgraph, dgraph1, dgraph2;
    private int row, row1, row2, row3, row4, selectedIndex;
    private Object[][] dataDetailedByTask, dataDetailedByTask2;
    private latencyDetailedAnalysisMain main;
    private MainGUI mainGUI;
    private SimulationTrace selectedST;
    private boolean b;
    private boolean compare;
    private int j;
    private TMLMapping<TGComponent> map;
    private Vector<SimulationTransaction> transFile1, transFile2;
    private String task1, task2, task3, task4;

    public LatencyAnalysisParallelAlgorithms(latencyDetailedAnalysisMain latencyDetailedAnalysisMain) {
        main = latencyDetailedAnalysisMain;
    }

    public void run() {
        if (algorithmId == 1) {
            jFrameLDA.generateDirectedGraph(tmap, cpanels);
        } else if (algorithmId == 2) {
            dataDetailedByTask = dgraph.getTaskByRowDetails(row);
        } else if (algorithmId == 3) {
            dataDetailedByTask = dgraph.getTaskByRowDetailsMinMaxTaint(row);
        } else if (algorithmId == 4) {
            dgraph.getRowDetailsMinMax(row);
            dataDetailedByTask = dgraph.getTasksByRowMinMax(row);
        } else if (algorithmId == 5) {
            dataDetailedByTask = dgraph.getTaskHWByRowDetails(row);
        } else if (algorithmId == 6) {
            dataDetailedByTask = dgraph.getTaskHWByRowDetailsMinMaxTaint(row);
        } else if (algorithmId == 7) {
            dataDetailedByTask = dgraph.getTaskHWByRowDetailsMinMax(row);
        } else if (algorithmId == 8) {
            try {
                main.latencyDetailedAnalysisForXML(mainGUI, selectedST, b, compare, j);
            } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (algorithmId == 9) {
            try {
                main.compareLatencyForXML(mainGUI, selectedST, b);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (algorithmId == 10) {
            cld.generateDirectedGraph1(map, cpanels);
        } else if (algorithmId == 11) {
            try {
                jFrameLDA.preciselatencyAnalysis(row);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (algorithmId == 12) {
            jFrameLDA.showgraphFrame();
        } else if (algorithmId == 13) {
            jFrameLDA.latencyDetailedAnalysis();
        } else if (algorithmId == 14) {
            dgraph.showGraph(dgraph);
        } else if (algorithmId == 15) {
            dgraph = new DirectedGraphTranslator(jFrameLDA, cld, map, cpanels, 1);
            generateDirectedGraph2(map, cpanels);
        } else if (algorithmId == 16) {
            cld.generateDirectedGraph2(map, cpanels);
        } else if (algorithmId == 17) {
            dgraph2.showGraph(dgraph2);
        } else if (algorithmId == 18) {
            cld.latencyDetailedAnalysis(task1, task2, task3, task4, transFile1, transFile2, true, false, false);
        } else if (algorithmId == 19) {
            try {
                cld.compareLatencyInDetails(row1, row2, row3, row4, selectedIndex);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (algorithmId == 20) {
            dataDetailedByTask = dgraph1.getTaskByRowDetails(row);
            dataDetailedByTask2 = dgraph2.getTaskByRowDetails(row2);
        } else if (algorithmId == 21) {
            dgraph1.getRowDetailsMinMax(row);
            dataDetailedByTask = dgraph1.getTasksByRowMinMax(row);
            dgraph2.getRowDetailsMinMax(row2);
            dataDetailedByTask2 = dgraph2.getTasksByRowMinMax(row2);
        } else if (algorithmId == 22) {
            dataDetailedByTask = dgraph1.getTaskHWByRowDetails(row);
            dataDetailedByTask2 = dgraph2.getTaskHWByRowDetails(row2);
        } else if (algorithmId == 23) {
            dgraph1.getRowDetailsMinMax(row);
            dataDetailedByTask = dgraph1.getTaskHWByRowDetailsMinMax(row);
            dgraph2.getRowDetailsMinMax(row2);
            dataDetailedByTask2 = dgraph2.getTaskHWByRowDetailsMinMax(row2);
        }
    }

    public Object[][] getDataDetailedByTask2() {
        return dataDetailedByTask2;
    }

    private void generateDirectedGraph2(TMLMapping<TGComponent> map2, List<TMLComponentDesignPanel> cpanels2) {
        start(16);
        run();
    }

    public void start(int id) {
        algorithmId = id;
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    public Thread getT() {
        return t;
    }

    public void setDgraph2(DirectedGraphTranslator dgraph2) {
        this.dgraph2 = dgraph2;
    }

    public void setjFrameLDA(JFrameLatencyDetailedAnalysis jFrameLDA) {
        this.jFrameLDA = jFrameLDA;
    }

    public void setCld(JFrameCompareLatencyDetail cld) {
        this.cld = cld;
    }

    public TMLMapping<TGComponent> getTmap() {
        return tmap;
    }

    public void setTmap(TMLMapping<TGComponent> tmap) {
        this.tmap = tmap;
    }

    public void setRow2(int row2) {
        this.row2 = row2;
    }

    public List<TMLComponentDesignPanel> getCpanels() {
        return cpanels;
    }

    public void setCpanels(List<TMLComponentDesignPanel> cpanels) {
        this.cpanels = cpanels;
    }

    public void setDgraph(DirectedGraphTranslator dgraph) {
        this.dgraph = dgraph;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Object[][] getDataDetailedByTask() {
        return dataDetailedByTask;
    }

    public void setDataDetailedByTask(Object[][] dataDetailedByTask) {
        this.dataDetailedByTask = dataDetailedByTask;
    }

    public void setMainGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public void setSelectedST(SimulationTrace selectedST) {
        this.selectedST = selectedST;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public void setCompare(boolean compare) {
        this.compare = compare;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public void setMap(TMLMapping<TGComponent> map) {
        this.map = map;
    }

    public DirectedGraphTranslator getDgraph() {
        return dgraph;
    }

    public void setDgraph1(DirectedGraphTranslator dgraph1) {
        this.dgraph1 = dgraph1;
    }

    public void latencyDetailedAnalysis(JFrameCompareLatencyDetail jFrameCompareLatencyDetail, String task12, String task22, String task32,
            String task42, Vector<SimulationTransaction> transFile12, Vector<SimulationTransaction> transFile22) {
        cld = jFrameCompareLatencyDetail;
        task1 = task12;
        task2 = task22;
        task3 = task32;
        task4 = task42;
        transFile1 = transFile12;
        transFile2 = transFile22;
    }

    public void compareLatencyInDetails(JFrameCompareLatencyDetail jFrameCompareLatencyDetail, int row11, int row22, int row33, int row44,
            int selectedIndex1) {
        cld = jFrameCompareLatencyDetail;
        row1 = row11;
        row2 = row22;
        row3 = row33;
        row4 = row44;
        selectedIndex = selectedIndex1;
    }

    public MainGUI getMainGUI() {
        return mainGUI;
    }
}
