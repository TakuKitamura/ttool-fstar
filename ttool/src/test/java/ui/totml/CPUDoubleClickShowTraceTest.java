package ui.totml;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.TMLMapping;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.simulation.SimulationTransaction;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.*;
import ui.interactivesimulation.JFrameInteractiveSimulation;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CPUDoubleClickShowTraceTest extends AbstractUITest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_CPU_SHOWTRACE = {"SmartCardProtocol"};
    private String SIM_DIR;
    static String CPP_DIR = "../../../../simulators/c++2/";
    static String mappingName = "Mapping2";
    private TMLArchiDiagramPanel currTdp;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";
    }

    public CPUDoubleClickShowTraceTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test(timeout = 300000)
    public void testCPUShowTraceOnDoubleClick() throws Exception {
        for (int i = 0; i < MODELS_CPU_SHOWTRACE.length; i++) {
            String s = MODELS_CPU_SHOWTRACE[i];
            SIM_DIR = DIR_GEN + s + "_showTrace/";
            System.out.println("executing: checking syntax " + s);
            // select architecture tab
            mainGUI.openProjectFromFile(new File(RESOURCES_DIR + s + ".xml"));
            TMLArchiPanel _tab = findArchiPanel(mappingName);
            for (TDiagramPanel tdp : _tab.getPanels()) {
                if (tdp instanceof TMLArchiDiagramPanel) {
                    mainGUI.selectTab(tdp);
                    currTdp = (TMLArchiDiagramPanel) tdp;
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
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + CPP_DIR;
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
            Penalties penalty = new Penalties(SIM_DIR + "src_simulator");
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
            // TTool/ttool/build/test_diplo_simulator/SmartCardProtocol_showTrace/
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

            // Starts simulation and connect to the server
            Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -server");
            JFrameInteractiveSimulation jfis = mainGUI.getJfis();

            if (jfis != null) {
                jfis.startSimulation();
                Thread.sleep(1000);
                jfis.sendTestCmd("time");
                Thread.sleep(50);
                jfis.sendTestCmd("get-hashcode");
                Thread.sleep(500);
                boolean hashOK = jfis.getHash();

                TraceManager.addDev("HashCode  = " + hashOK + " and Busy mode = " + ((jfis.getBusyMode() == 1) ? "READY" : "BUSY"));
                if (!hashOK || jfis.getBusyMode() != 1) {
                    TraceManager.addDev("Server is in use, please restart server and re-run the test");
                    jfis.killSimulator();
                    jfis.close();
                    return;
                }
                jfis.sendTestCmd("run-x-transactions 10"); // run 10 transactions
                Thread.sleep(50);
                jfis.sendTestCmd("lt 1000"); // update transaction list
                Thread.sleep(1000);
                for (TGComponent tg : currTdp.getComponentList()) {
                    System.out.println("tgc = " + tg.getName());
                    // get the transaction list of each CPUs on the panel, if the trans size > 0 then there will be a trace shown on double click
                    if (tg instanceof TMLArchiCPUNode) {
                        int _ID = tg.getDIPLOID();
                        TraceManager.addDev("Component ID = " + _ID);
                        List<SimulationTransaction> ts = mainGUI.getTransactions(_ID);
                        // mainGUI.getTransactions(_ID) is synchronized function, so we need to wait until data is filled.
                        //the test will fail after 5 minutes if ts is still null.
                        int maxNumberOfLoop = 0;
                        while (maxNumberOfLoop < 15 && ts == null) {
//                            TraceManager.addDev("Waiting for data " + maxNumberOfLoop);
                            ts = mainGUI.getTransactions(_ID);
                            maxNumberOfLoop ++;
                            Thread.sleep(2000);
                        }
                        if (ts != null) TraceManager.addDev("Device " + _ID + " has trans size = " + ts.size());
                        assertTrue(ts != null && ts.size() > 0);
                    }
                }
                jfis.killSimulator();
                jfis.close();
            }

            System.out.println("Test done");
        }
    }
}
