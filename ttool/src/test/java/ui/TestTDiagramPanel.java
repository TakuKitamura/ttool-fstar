package ui;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ui.tmldd.TMLArchiBUSNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;

public class TestTDiagramPanel extends AbstractUITest {
	static TDiagramPanel diagramPanel;
	static TGCWithInternalComponent component;
	
	public TestTDiagramPanel() {
		super();
	}
	
	@Before
    public void setUp() throws Exception {
//		mgui = new MainGUI(false, false, false, false, false, false, false, false, false, false, false, true);
//		mgui.build();	
		//PluginManager.pluginManager = new PluginManager();
		final TToolBar toolBar = new TMLArchiDiagramToolBar( mainGUI );
        diagramPanel = new TMLArchiDiagramPanel( mainGUI, toolBar );
        component = new TMLArchiBUSNode( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.setComponentPointed(null);
    }
	
	@Test
    public void testMoveUp() {
       diagramPanel.upComponent();
       diagramPanel.setComponentPointed(component);
       diagramPanel.upComponent();
       assertTrue(component.getY() == 499);
       assertTrue(component.getX() == 500);
    }
	
	@Test
    public void testMoveDown() {
       diagramPanel.downComponent();
       diagramPanel.setComponentPointed(component);
       diagramPanel.downComponent();
       assertTrue(component.getY() == 501);
       assertTrue(component.getX() == 500);
    }
	
	@Test
    public void testMoveRight() {
       diagramPanel.rightComponent();
       diagramPanel.setComponentPointed(component);
       diagramPanel.rightComponent();
       assertTrue(component.getX() == 501);
       assertTrue(component.getY() == 500);
    }
	
	@Test
    public void testMoveLeft() {
       diagramPanel.leftComponent();
       diagramPanel.setComponentPointed(component);
       diagramPanel.leftComponent();
       assertTrue(component.getX() == 499);
       assertTrue(component.getY() == 500);
    }
}
