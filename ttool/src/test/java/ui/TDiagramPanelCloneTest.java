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
    static  TGComponent tgComponent;
    final static String PATH_TO_DIPLODOCUS = "../../modeling/DIPLODOCUS/";
    final static String ORIGIN_FILE_NAME = "CloneCompositeComponentTest";
    final static String GENERATED_FILE = "spec.tml";
    final static String EXPECTED_FILE = "test/resources/tmltranslator/expected/expected_spec.tml";


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = PATH_TO_DIPLODOCUS + ORIGIN_FILE_NAME + XML_EXT;
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

        //tgComponent = findDiagComponent(238, diagramPanel);
        for (TGComponent tgc : diagramPanel.getComponentList()) {
            if (tgc instanceof TMLCCompositeComponent) {
                tgComponent = tgc;
            }
        }
    }

    @Test
    public void testCloneComponent() throws Exception{
        CompareTML compTML = new CompareTML();
        diagramPanel.cloneComponent(tgComponent);
        mainGUI.modelChecking();
        mainGUI.generateTMLTxt();
        File f1 = new File(EXPECTED_FILE);
        File f2 = new File(GENERATED_FILE);
        assertTrue(compTML.compareTML(f1,f2));
    }
}