package ui;

import myutil.TraceManager;
import org.junit.*;
import tmltranslator.compareTMLTest.CompareTML;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;


import java.io.*;

import static org.junit.Assert.*;
/*
 * #issue 82 + 186
 * author : Minh Hiep
 */
public class TDiagramPanelCloneTest extends AbstractUITest {

    static TDiagramPanel diagramPanel;
    static  TGComponent tgComponent1;
    static TGComponent tgComponent2;
    static TGComponent tgComponent3;
    static TGComponent tgComponent4;

    final static String EXPECTED_FILE1 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec1.tml";
    final static String EXPECTED_FILE2 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec2.tml";
    final static String EXPECTED_FILE3 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec3.tml";
    final static String EXPECTED_FILE4 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec4.tml";


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/ui/diagram2tml/input/CloneCompositeComponentTest.xml";
    }

    public TDiagramPanelCloneTest() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() {
        diagramPanel = null;
        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLComponentDesignPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof  TMLComponentTaskDiagramPanel) {
                        diagramPanel = tdp;
                        mainGUI.selectTab(diagramPanel);
                        break;
                    }
                }
                break;
            }
        }

        if (diagramPanel != null) {
            //TraceManager.addDev("diagram pannel is non null !!!");
            for (TGComponent tgc : diagramPanel.getAllComponentList()) {
                if (tgc.getValue().equals("CompositeComp1")) {
                    tgComponent1 = tgc;
                }

                if (tgc.getValue().equals("CompositeComp2")) {
                    tgComponent2 = tgc;
                }

                if (tgc.getValue().equals("PrimitiveComp5")) {
                    tgComponent3 = tgc;
                }

                if (tgc.getValue().equals("PrimitiveComp6")) {
                    tgComponent4 = tgc;
                }
            }
       }
    }

   @Test
    public void testCloneCompositeComponentWithNullFather() throws Exception{
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent1);
       // diagramPanel.cloneComponent(tgComponent2);
        //diagramPanel.cloneComponent(tgComponent3);
        //diagramPanel.cloneComponent(tgComponent4);
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE1);
        File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }

    @Test
    public void testCloneCompositeComponentWithNonNullFather() throws Exception {
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent2);
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE2);
        File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }

    @Test
    public void testClonePrimitiveComponentWithNonNullFather() throws Exception {
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent3);
        // mainGUI.modelChecking();
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE3);
        File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }


    @Test
    public void testClonePrimitiveComponentWithNullFather() throws Exception {
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent4);
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE4);
        File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }

}