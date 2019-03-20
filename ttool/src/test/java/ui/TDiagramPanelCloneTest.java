package ui;

import org.junit.*;
import tmltranslator.compareTMLTest.CompareTML;
import ui.tmlcompd.TMLCCompositeComponent;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;

import java.io.*;

import static org.junit.Assert.*;
/*
 * #issue 82
 * author : Minh Hiep
 */
public class TDiagramPanelCloneTest extends AbstractUITest {

    static TDiagramPanel diagramPanel;
    static  TGComponent tgComponent1;
    static TGComponent tgComponent2;
    static TGComponent tgComponent3;
    final static String EXPECTED_FILE1 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec1.tml";
    final static String EXPECTED_FILE2 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec2.tml";
    final static String EXPECTED_FILE3 = getBaseResourcesDir() + "tmltranslator/expected/expected_spec3.tml";


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
                        mainGUI.selectTab(tdp);
                        break;
                    }
                }
                break;
            }
        }

        for (TGComponent tgc : diagramPanel.getAllComponent()) {
            if (tgc.getValue().equals("CompositeComp1")) {
                tgComponent1 = tgc;
            }

            if (tgc.getValue().equals("CompositeComp2")) {
                tgComponent2 = tgc;
            }

            if (tgc.getValue().equals("PrimitiveComp5")) {
                tgComponent3 = tgc;
            }
        }
    }

   @Test
    public void testCloneCompositeComponentWithNullFather() throws Exception{
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent1);
       // mainGUI.modelChecking();
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
        // mainGUI.modelChecking();
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE2);
        File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }

    @Test
    public void testClonePrimitiveComponent() throws Exception {
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent3);
        // mainGUI.modelChecking();
        mainGUI.checkModelingSyntax(true);
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE3);
        File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
        assertTrue(compTML.compareTML(f1,f2));
    }




}