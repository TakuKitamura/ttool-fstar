package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.FileUtils;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import remotesimulation.RemoteConnection;
import remotesimulation.RemoteConnectionException;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.AbstractUITest;
import ui.TDiagramPanel;
import ui.TMLArchiPanel;
import ui.TURTLEPanel;
import ui.interactivesimulation.JFrameTMLSimulationPanelTimeline;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmldd.TMLArchiDiagramPanel;


import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class TimelineDiagramTest extends AbstractUITest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_TIMELINE = {"timelineDiagram"};
    private String SIM_DIR;
    private RemoteConnection rc;
    private boolean isReady = false;
    private boolean running = true;
    private Vector<SimulationTransaction> trans;
    final static String EXPECTED_FILE_GENERATED_TIMELINE = getBaseResourcesDir() + "tmltranslator/expected/expected_get_generated_timeline.txt";
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";

    }

    public TimelineDiagramTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/";
    }

    @Test
    public void testCompareTimelineGeneratedContent() throws Exception {
        for (int i = 0; i < MODELS_TIMELINE.length; i++) {
            String s = MODELS_TIMELINE[i];
            SIM_DIR = DIR_GEN + s + "/";
            System.out.println("executing: checking syntax " + s);
            // select architecture tab
            mainGUI.openProjectFromFile(new File(RESOURCES_DIR + s + ".xml"));
            for(TURTLEPanel _tab : mainGUI.getTabs()) {
                if(_tab instanceof TMLArchiPanel) {
                    for (TDiagramPanel tdp : _tab.getPanels()) {
                        if (tdp instanceof TMLArchiDiagramPanel) {
                            mainGUI.selectTab(tdp);
                            break;
                        }
                    }
                    break;
                }
            }
            mainGUI.checkModelingSyntax(true);
            TMLMapping tmap = mainGUI.gtm.getTMLMapping();
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertTrue(syntax.hasErrors() == 0);
            // Generate SystemC code
            System.out.println("executing: sim code gen for " + s);
            final IDiploSimulatorCodeGenerator tml2systc;
            List<EBRDD> al = new ArrayList<EBRDD>();
            List<TEPE> alTepe = new ArrayList<TEPE>();
            tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
            tml2systc.setModelName(s);
            String error = tml2systc.generateSystemC(false, true);
            assertTrue(error == null);

            File directory = new File(SIM_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Putting sim files
            System.out.println("SIM executing: sim lib code copying for " + s);
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + "../../../../simulators/c++2/";
            boolean simFiles = SpecConfigTTool.checkAndCreateSystemCDir(SIM_DIR);

            System.out.println("SIM executing: sim lib code copying done with result " + simFiles);
            assertTrue(simFiles);

            System.out.println("SIM Saving file in: " + SIM_DIR);
            tml2systc.saveFile(SIM_DIR, "appmodel");

            // Compile it
            System.out.println("executing: compile");
            Process proc;
            BufferedReader proc_in;
            String str;
            boolean mustRecompileAll;
            Penalties penalty = new Penalties(SIM_DIR  + "src_simulator");
            int changed = penalty.handlePenalties(false);

            if (changed == 1) {
                mustRecompileAll = true;
            } else {
                mustRecompileAll = false;
            }

            if (mustRecompileAll) {
                System.out.println("executing: " + "make -C " + SIM_DIR + " clean");
                try {
                    proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + " clean");
                    proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while ((str = proc_in.readLine()) != null) {
                        // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                        System.out.println("executing: " + str);
                    }
                } catch (Exception e) {
                    // probably make is not installed
                    System.out.println("FAILED: executing: " + "make -C " + SIM_DIR + " clean");
                    return;
                }
            }

            System.out.println("executing: " + "make -C " + SIM_DIR);
            try {

                proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + "");
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing: " + "make -C " + SIM_DIR);
                return;
            }
            System.out.println("SUCCESS: executing: " + "make -C " + SIM_DIR);
            // Starts simulation
            Runtime.getRuntime().exec("./" + SIM_DIR + "run.x" + " -server");
            Thread.sleep(1000);
            // Connects to the simulator, incase of using terminal: "./run.x -server" to start server and "nc localhost 3490" to connect to server
            rc = new RemoteConnection("localhost");
            try {
                rc.connect();
                isReady = true;
            } catch (RemoteConnectionException rce) {
                System.out.println("Could not connect to server.");
            }

            toServer(" 1 0", rc);
            Thread.sleep(5);
            toServer("22 100", rc);
            Thread.sleep(5);
            while (running) {
                String demo = null;
                try {
                    demo = rc.readOneLine();
                } catch (RemoteConnectionException e) {
                    e.printStackTrace();
                }
                if (demo.contains("transnb nb=")) {
                    running = false;
                }
                int index0 = demo.indexOf("<transinfo");

                if ((index0 > -1)) {
                    analyzeServerAnswer(demo, index0);
                } else {
                    continue;
                }
            }

            System.out.println("Transaction list size: " + trans.size());

            JFrameTMLSimulationPanelTimeline tmlSimPanelTimeline = new JFrameTMLSimulationPanelTimeline(new Frame(), mainGUI, trans,
                    "Show Trace - Timeline");
//            System.out.println(tmlSimPanelTimeline.generateHtmlContent(0));
            File file = new File(EXPECTED_FILE_GENERATED_TIMELINE);
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            assertTrue(content.equals(tmlSimPanelTimeline.generateHtmlContent(0)));

        }
    }
    private synchronized void toServer(String s, RemoteConnection rc) throws RemoteConnectionException {
        while(!isReady) {
            TraceManager.addDev("Server not ready");
            try {
                rc.send("13");
                wait(250);
            } catch (InterruptedException ie) {

            }
        }
        rc.send(s);
    }

    private void  analyzeServerAnswer(String s, int index0) {
        String val = s.substring(index0+10).trim();
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(val);
        ArrayList<String> splited = new ArrayList<>();
        while (m.find()) {
            splited.add(m.group(1));
        }
        writeTransValue(splited);
    }

    private void writeTransValue(ArrayList<String> val) {
        if(trans == null) trans = new Vector<SimulationTransaction>();
        SimulationTransaction st = new SimulationTransaction();
        st.uniqueID = Long.valueOf(val.get(0));
        st.nodeType = val.get(1);
        st.deviceName = val.get(2);
        st.coreNumber = val.get(3);
        int index = val.get(4).indexOf(": ");
        if (index == -1){
            st.taskName = "Unknown";
            st.command = val.get(4);
        } else {
            st.taskName = val.get(4).substring(0, index).trim();
            st.command = val.get(4).substring(index+1, val.get(4).length()).trim();
        }
        st.startTime = val.get(5);
        st.endTime = val.get(6);
        st.length = val.get(7);
        st.virtualLength = val.get(8);
        st.id = val.get(9);
        st.runnableTime = val.get(10);
        st.channelName = (val.size() > 11) ? val.get(11) : "Unknown";

        trans.add(st);
    }
}
