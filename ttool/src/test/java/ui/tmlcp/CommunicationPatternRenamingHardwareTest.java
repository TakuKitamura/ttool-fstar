package ui.tmlcp;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tmltranslator.TMLMapping;
import tmltranslator.TMLSyntaxChecking;
import ui.*;
import ui.tmldd.TMLArchiBUSNode;
import ui.tmldd.TMLArchiCPNode;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiDiagramPanel;
import java.io.File;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

public class CommunicationPatternRenamingHardwareTest extends AbstractUITest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_CP_HW = {"cp_rename_hw"};
    private String SIM_DIR;
    private TMLArchiCPNode tgCP;
    private TMLArchiBUSNode tgBus;
    private TMLArchiCPUNode tgCPU;
    private TMLArchiDiagramPanel tmlap;
    final String CPU_INSTANCE = "CP.ControllerInstance : ";
    final String BUS_INSTANCE = "CP.TransferInstance : ";

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
            String initialMapping = "";
            String currMapping = "";
            for(TURTLEPanel _tab : mainGUI.getTabs()) {
                if(_tab instanceof TMLArchiPanel) {
                    for (TDiagramPanel tdp : _tab.getPanels()) {
                        if (tdp instanceof TMLArchiDiagramPanel) {
                            tmlap = (TMLArchiDiagramPanel)tdp;
                            mainGUI.selectTab(tdp);
                            for (TGComponent tg : tdp.getComponentList()) {
                                if (tg instanceof TMLArchiCPNode) {
                                    tgCP = (TMLArchiCPNode)tg;
                                    Vector <String> tempList = new Vector<>(((TMLArchiCPNode) tg).getMappedUnits());
                                    for (String mapped : tempList) {
                                        // the initial mapping of the model, including one Bus named "Bus012 and one CPU named "Src"
                                        initialMapping += mapped + ", ";
                                    }
                                }

                                if (tg instanceof TMLArchiBUSNode) {
                                    tgBus = (TMLArchiBUSNode)tg;
                                }

                                if (tg instanceof TMLArchiCPUNode) {
                                    for( TGComponent tgc : tmlap.listOfCP.keySet()) {
                                        for (int k = 0; k < tmlap.listOfCP.get(tgc).size(); k++) {
                                            if (tg == tmlap.listOfCP.get(tgc).get(k).getTGC()) tgCPU = (TMLArchiCPUNode)tg;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            //Before renaming HW, check if the current contains current Bus and CPU
            assertTrue(initialMapping.contains(BUS_INSTANCE + tgBus.getName()));// Bus with initial name
            assertTrue(initialMapping.contains(CPU_INSTANCE + tgCPU.getName()));// CPU with initial name

            //After renaming, the HWs should still appear in the CP mapping with the new name
            //Rename and check for Bus first:
            String newBusName = tgBus.getName()+"newBusName";
            tgBus.setName(newBusName);
            tmlap.handleCPOnDoubleClick(tgBus);
            Vector <String> tempList = new Vector<>(tgCP.getMappedUnits());
            for (String mapped : tempList) {
                //Current mapping, after renaming
                currMapping += mapped + ", ";
            }
            assertTrue(currMapping.contains(BUS_INSTANCE + tgBus.getName()));// The same Bus with the new name
            assertTrue(currMapping.contains(CPU_INSTANCE + tgCPU.getName()));// The same CPU with name unchanged

            //Rename and check for CPU:
            String newCPUName = tgCPU.getName()+"newCPUName";
            tgCPU.setName(newCPUName);
            tmlap.handleCPOnDoubleClick(tgCPU);
            Vector <String> tempList1 = new Vector<>(tgCP.getMappedUnits());
            currMapping = "";
            for (String mapped : tempList1) {
                //Current mapping, after renaming
                currMapping += mapped + ", ";
            }
            assertTrue(currMapping.contains(BUS_INSTANCE + tgBus.getName()));// The same Bus with the new name
            assertTrue(currMapping.contains(CPU_INSTANCE + tgCPU.getName()));// The same CPU with the new name

            mainGUI.checkModelingSyntax(true);
            TMLMapping tmap = mainGUI.gtm.getTMLMapping();
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertTrue(syntax.hasErrors() == 0);
        }
    }

}
