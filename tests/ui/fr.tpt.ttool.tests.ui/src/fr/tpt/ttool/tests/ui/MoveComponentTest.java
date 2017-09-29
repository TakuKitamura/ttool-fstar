package fr.tpt.ttool.tests.ui;

import org.junit.*;
import ui.MainGUI;
import ui.tmldd.TMLArchiBUSNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;
import junit.framework.TestCase;
import myutil.PluginManager;

public class MoveComponentTest extends TestCase {
	static MainGUI mgui;
	static TMLArchiDiagramToolBar tb;
	static TMLArchiDiagramPanel tdp;
	static TMLArchiBUSNode tgc;
	
	@Before
    public void setUp() throws Exception {
		mgui = new MainGUI(false, false, false, false, false, false, false, false, false, false, false, true);
		mgui.build();	
		PluginManager.pluginManager = new PluginManager();
		tb = new TMLArchiDiagramToolBar(mgui);
        tdp = new TMLArchiDiagramPanel(mgui, tb);
        tgc = new TMLArchiBUSNode(500, 500, 0, 1400, 0, 1900, true, null, tdp);
        tdp.setComponentPointed(null);
    }
	
	@Test
    public void testMoveUp() {
       tdp.upComponent();
       tdp.setComponentPointed(tgc);
       tdp.upComponent();
       assertTrue(tgc.getY() == 499);
       assertTrue(tgc.getX() == 500);
    }
	
	@Test
    public void testMoveDown() {
       tdp.downComponent();
       tdp.setComponentPointed(tgc);
       tdp.downComponent();
       assertTrue(tgc.getY() == 501);
       assertTrue(tgc.getX() == 500);
    }
	
	@Test
    public void testMoveRight() {
       tdp.rightComponent();
       tdp.setComponentPointed(tgc);
       tdp.rightComponent();
       assertTrue(tgc.getX() == 501);
       assertTrue(tgc.getY() == 500);
    }
	
	@Test
    public void testMoveLeft() {
       tdp.leftComponent();
       tdp.setComponentPointed(tgc);
       tdp.leftComponent();
       assertTrue(tgc.getX() == 499);
       assertTrue(tgc.getY() == 500);
    }

}