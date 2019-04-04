package ui;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLCPrimitivePort;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;

import java.io.File;

import static org.junit.Assert.*;

public class AttachPortTest extends AbstractUITest{

    static TDiagramPanel diagramPanel;
    static TMLCPrimitiveComponent primitiveComp1;
    static TMLCPrimitiveComponent primitiveComp3;
    static TMLCPrimitiveComponent primitiveCompNullFather;
    static TMLCPrimitivePort primitivePort1;
    static TMLCPrimitivePort primitivePort2;
    static TMLCPrimitivePort primitivePort3;
    static TMLCPrimitivePort primitivePort4;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/ui/tmlcompd/input/TestPortName.xml";
    }

    public AttachPortTest() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }


    @Before
    public void setUp() throws Exception {
        diagramPanel = null;
        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLComponentDesignPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof TMLComponentTaskDiagramPanel) {
                        diagramPanel = tdp;
                        mainGUI.selectTab(tdp);
                        break;
                    }
                }
                break;
            }
        }

        if(diagramPanel != null) {
            for (TGComponent tgc : diagramPanel.getAllComponentList()) {
                if (tgc.getValue().equals("PrimitiveComp1")) {
                    primitiveComp1 = (TMLCPrimitiveComponent)tgc;
                }

                if (tgc.getValue().equals("PrimitiveComp3")) {
                    primitiveComp3 = (TMLCPrimitiveComponent)tgc;
                }

                if (tgc.getValue().equals("PrimitiveCompNullFather")) {
                    primitiveCompNullFather = (TMLCPrimitiveComponent)tgc;
                }

                if ((tgc instanceof TMLCPrimitivePort) && ((TMLCPrimitivePort)tgc).getPortName().equals("primitivePort1")) {
                    primitivePort1 = (TMLCPrimitivePort) tgc;
                }

                if ((tgc instanceof TMLCPrimitivePort) && ((TMLCPrimitivePort)tgc).getPortName().equals("primitivePort2")) {
                    primitivePort2 = (TMLCPrimitivePort) tgc;
                }

                if ((tgc instanceof TMLCPrimitivePort) && ((TMLCPrimitivePort)tgc).getPortName().equals("primitivePort3")) {
                    primitivePort3 = (TMLCPrimitivePort) tgc;
                }

                if ((tgc instanceof TMLCPrimitivePort) && ((TMLCPrimitivePort)tgc).getPortName().equals("primitivePort4")) {
                    primitivePort4 = (TMLCPrimitivePort) tgc;
                }
            }
        }
    }

    @Test
    public void NonAttachThenAttachTest(){
        //Test for cases : Non-attach --> Attach
        //Test before attaching
        assertTrue(primitivePort1.getFather() == null);
        assertTrue(primitivePort2.getFather() == null);
        assertTrue(primitivePort3.getFather() == null);

        diagramPanel.attach(primitivePort1);
        diagramPanel.attach(primitivePort2);
        diagramPanel.attach(primitivePort3);

        //Test after attaching
        assertTrue(primitivePort1.getFather() == primitiveCompNullFather);
        assertTrue(primitivePort2.getFather() == primitiveComp1);
        assertTrue(primitivePort3.getFather() == primitiveComp3);
    }

    @Test
    public void DetachThenReAttachTest(){
        //Tes for case : Attach --> Detach --> Re-attach
        //test before detaching
        assertTrue(primitivePort4.getFather() == primitiveComp3);

        diagramPanel.detach(primitivePort4);
        //test after detaching
        assertTrue(primitivePort4.getFather() == null);

        //test after attaching
        diagramPanel.attach(primitivePort4);
        assertTrue(primitivePort4.getFather() == primitiveComp3);
    }

}