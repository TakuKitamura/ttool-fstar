package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.*;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmldd.TMLArchiBUSNode;
import ui.tmldd.TMLArchiCPNode;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

public class CommunicationPatternRenamingHardwareTest extends AbstractUITest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_CP_HW = {"cp_rename_hw"};
    private String SIM_DIR;
    private TMLArchiCPNode tgCP;
    private TMLArchiBUSNode tgBus;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";

    }
    public CommunicationPatternRenamingHardwareTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/";
    }
    @Test
    public void testCommunicationPatternRenameHW() throws Exception {
        for (int i = 0; i < MODELS_CP_HW.length; i++) {
            String s = MODELS_CP_HW[i];
            SIM_DIR = DIR_GEN + s + "/";
            System.out.println("executing: checking syntax " + s);
            // select architecture tab
            mainGUI.openProjectFromFile(new File(RESOURCES_DIR + s + ".xml"));
            String previousMapping = "";
            String currMapping = "";
            String oldName = "";
            String newName = "";
            for(TURTLEPanel _tab : mainGUI.getTabs()) {
                if(_tab instanceof TMLArchiPanel) {
                    for (TDiagramPanel tdp : _tab.getPanels()) {
                        if (tdp instanceof TMLArchiDiagramPanel) {
                            mainGUI.selectTab(tdp);
                            for (TGComponent tg : tdp.getComponentList()) {
                                if (tg instanceof TMLArchiCPNode) {
                                    tgCP = (TMLArchiCPNode)tg;
                                    Vector <String> tempList = new Vector<>(((TMLArchiCPNode) tg).getMappedUnits());
                                    for (String mapped : tempList) {
                                        previousMapping += mapped + ", ";
                                    }
                                }

                                if (tg instanceof TMLArchiBUSNode) {
                                    tgBus = (TMLArchiBUSNode)tg;
                                    oldName = tgBus.getName();
                                    newName = tgBus.getName()+"newName";
                                    tgBus.setName(newName);
                                    ((TMLArchiDiagramPanel) tdp).handleCPOnDoubleClick(tgBus);
                                    Vector <String> tempList1 = new Vector<>(tgCP.getMappedUnits());
                                    for (String mapped : tempList1) {
                                        currMapping += mapped + ", ";
                                    }
                                }
                            }
                            boolean check = previousMapping.contains(oldName);
                            assertTrue(check);
                            if (check) System.out.println("Success: " + previousMapping + ", " + oldName);
                            check = currMapping.contains(newName);
                            assertTrue(check);
                            if (check) System.out.println("Success: " + currMapping + ", " + newName);
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
        }
    }

}
