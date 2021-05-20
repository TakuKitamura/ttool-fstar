package ui.avatarmad;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.io.File;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ui.*;
import ui.avatarmad.AvatarMADAssumption;
import ui.avatarmad.AvatarMADPanel;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;

public class TestAvatarMAD extends AbstractUITest {

    private static final String PATH_TO_SOURCE_MODEL = "/ui/avatarmad/testMAD.xml";

    private static final String[] NAMES = { "Assumption_toto", "Assumption_titi", "Assumption_0", "Assumption_1" };
    private static final String[] STEREOTYPES = { "toto", "Titi", "System Assumption", "Environment Assumption" };
    private static final int[] COLORS = { -26215, -103, -8138275, -6684775 };

    public TestAvatarMAD() {
        super();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + PATH_TO_SOURCE_MODEL;
    }

    @Before
    public void setUp() {

    }

    @Test
    public void testDisableAvatarADAcceptEventAction() {
        // Loading file
        File f = new File(RESOURCES_DIR);
        assertTrue(f.exists());
        openModelFullName(RESOURCES_DIR);

        // Getting panel
        TURTLEPanel panels = (AvatarMADsPanel) (mainGUI.getTURTLEPanel(0));
        assertTrue(panels instanceof AvatarMADsPanel);

        AvatarMADsPanel madPanels = (AvatarMADsPanel) panels;

        // Selecting first panel
        TDiagramPanel tdp = madPanels.panels.get(0);
        assertTrue(tdp instanceof AvatarMADPanel);
        AvatarMADPanel panel = (AvatarMADPanel) tdp;

        // Checking assumptions
        int found = 0;
        for (TGComponent tgc : panel.getAllComponentList()) {
            if (tgc instanceof AvatarMADAssumption) {
                AvatarMADAssumption ass = (AvatarMADAssumption) tgc;
                System.out.println("Found Assumption: " + tgc.getValue());
                for (int i = 0; i < NAMES.length; i++) {
                    if (ass.getValue().compareTo(NAMES[i]) == 0) {
                        found++;
                        assertTrue(ass.getStereotype().compareTo(STEREOTYPES[i]) == 0);
                        assertTrue(ass.getColorRGB() == (COLORS[i]));
                        break;
                    }
                }
            }
        }

        assertTrue(found == 4);

    }
}
