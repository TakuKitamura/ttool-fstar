package fr.tpt.ttool.tests.util;

import junit.framework.TestCase;
import myutil.PluginManager;
import ui.MainGUI;
import ui.tmldd.TMLArchiBUSNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;

import org.junit.*;

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
       assert tgc.getY() == 499;
       assert tgc.getX() == 500;
    }
	
	@Test
    public void testMoveDown() {
       tdp.downComponent();
       tdp.setComponentPointed(tgc);
       tdp.downComponent();
       assert tgc.getY() == 501;
       assert tgc.getX() == 500;
    }
	
	@Test
    public void testMoveRight() {
       tdp.rightComponent();
       tdp.setComponentPointed(tgc);
       tdp.rightComponent();
       assert tgc.getX() == 501;
       assert tgc.getY() == 500;
    }
	
	@Test
    public void testMoveLeft() {
       tdp.leftComponent();
       tdp.setComponentPointed(tgc);
       tdp.leftComponent();
       assert tgc.getX() == 499;
       assert tgc.getY() == 500;
    }

}