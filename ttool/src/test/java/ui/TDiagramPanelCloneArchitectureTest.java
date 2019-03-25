package ui;

import myutil.TraceManager;
import org.junit.*;
import tmltranslator.compareTMLTest.CompareTML;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.*;

import static org.junit.Assert.*;
/*
 * #issue 186
 * author : Minh Hiep
 */
public class TDiagramPanelCloneArchitectureTest extends AbstractUITest {
    
    static TDiagramPanel architecture;
    static  TGComponent cpu1;

    static TGComponent mapChannel;


    final static String EXPECTED_FILE1 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec1.tmap";
    final static String EXPECTED_FILE2 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec2.tarchi";


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/ui/diagram2tml/input/CloneCompositeComponentTest.xml";
    }

    public TDiagramPanelCloneArchitectureTest() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() {
        architecture = null;
        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLArchiPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof TMLArchiDiagramPanel) {
                        architecture = tdp;
                        mainGUI.selectTab(architecture);
                        break;
                    }
                }
                break;
            }
        }

        if (architecture != null) {
            //TraceManager.addDev("architecture tab is non null");
            for (TGComponent tgc : architecture.getAllComponentList()) {
                if (tgc.getValue().equals("Application::PrimitiveComp1")) {
                    mapChannel = tgc;
                }

                if (tgc.getName().equals("CPU1")) {
                    cpu1 = tgc;
                }
            }
        }
    }

    @Test
    public void testCloneMapInArchitecture() throws Exception {
        CompareTML compTML = new CompareTML();
        TGComponent mapTaskClone = null;
        architecture.cloneComponent(mapChannel);
        architecture.removeComponent(mapChannel);
        for (TGComponent tgc : architecture.getComponentList()) {
            if (tgc.getValue().equals("Application::PrimitiveComp1")) {
                mapTaskClone = tgc;
            }
        }

        if (mapTaskClone != null) {
            //TraceManager.addDev("mapTaskClone is non null");
            architecture.attach(mapTaskClone);
        }
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE1);
        File f2 = new File("spec.tmap");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }

    @Test
    public void testCloneNodeInArchitecture() throws Exception {
        CompareTML compTML = new CompareTML();
        architecture.cloneComponent(cpu1);
        for (TGComponent tgc : architecture.getComponentList()) {
            if (tgc.getName().equals("CPU1")) {
                tgc.setName("CPU2");
                break;
            }
        }

        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE2);
        File f2 = new File("spec.tarchi");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }


}