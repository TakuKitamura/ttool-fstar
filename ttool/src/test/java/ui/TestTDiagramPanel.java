package ui;

import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import ui.avatarad.AvatarADStopState;
import ui.tmlcompd.TMLCCompositeComponent;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;

public class TestTDiagramPanel extends AbstractUITest {
	static TDiagramPanel diagramPanel;
	static TGCScalableWithoutInternalComponent atomicComponent;
	static TGCScalableWithInternalComponent compositeComponent;

	public TestTDiagramPanel() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		final TToolBar toolBar = new TMLArchiDiagramToolBar( mainGUI );
		diagramPanel = new TMLArchiDiagramPanel( mainGUI, toolBar );
		atomicComponent = new AvatarADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
		diagramPanel.addBuiltComponent( atomicComponent );

		compositeComponent = new TMLCCompositeComponent( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
		diagramPanel.addBuiltComponent( compositeComponent );

		diagramPanel.setComponentPointed(null);
	}

	@Test
	public void testMoveUp() {
		final Point expectedPos = new Point( atomicComponent.getX(), atomicComponent.getY() - TDiagramPanel.MOVE_SPEED );

		diagramPanel.upComponent();
		diagramPanel.setComponentPointed(atomicComponent);
		diagramPanel.upComponent();

		checkResults( expectedPos, atomicComponent );
//		assertTrue(atomicComponent.getY() == 499);
//		assertTrue(atomicComponent.getX() == 500);
	}

	@Test
	public void testMoveDown() {
		final Point expectedPos = new Point( atomicComponent.getX(), atomicComponent.getY() + TDiagramPanel.MOVE_SPEED );

		diagramPanel.downComponent();
		diagramPanel.setComponentPointed(atomicComponent);
		diagramPanel.downComponent();

		checkResults( expectedPos, atomicComponent );
//		assertTrue(atomicComponent.getY() == 501);
//		assertTrue(atomicComponent.getX() == 500);
	}

	@Test
	public void testMoveRight() {
		final Point expectedPos = new Point( atomicComponent.getX() + TDiagramPanel.MOVE_SPEED, atomicComponent.getY() );

		diagramPanel.rightComponent();
		diagramPanel.setComponentPointed(atomicComponent);
		diagramPanel.rightComponent();

		checkResults( expectedPos, atomicComponent );
//		assertTrue(atomicComponent.getX() == 501);
//		assertTrue(atomicComponent.getY() == 500);
	}

	@Test
	public void testMoveLeft() {
		final Point expectedPos = new Point( atomicComponent.getX() - TDiagramPanel.MOVE_SPEED, atomicComponent.getY() );
		
		diagramPanel.leftComponent();
		diagramPanel.setComponentPointed(atomicComponent);
		diagramPanel.leftComponent();

		checkResults( expectedPos, atomicComponent );

//		assertTrue(atomicComponent.getX() == 499);
//		assertTrue(atomicComponent.getY() == 500);
	}

	/**
	 * Issue #81
	 */
	 @Test
	 public void testMoveAtomicComponentAfterZoom() {
		diagramPanel.setZoom( 3.0 );

		diagramPanel.setComponentPointed( atomicComponent );

		final Point expectedPos = new Point( atomicComponent.getX() + TDiagramPanel.MOVE_SPEED, atomicComponent.getY() );

		diagramPanel.rightComponent();

		checkResults( expectedPos, atomicComponent );
	 }

	 /**
	  * Issue #81
	  */
	 @Test
	 public void testMoveCompositeComponentAfterZoom() {
		 diagramPanel.setZoom( 3.0 );

		 diagramPanel.setComponentPointed( compositeComponent );

		 final Point expectedPos = new Point( compositeComponent.getX() + TDiagramPanel.MOVE_SPEED, compositeComponent.getY() );

		 diagramPanel.rightComponent();

		 checkResults( expectedPos, compositeComponent );
	 }

	 private void checkResults( 	final Point expectedPos,
			 final TGComponent component ) {
		 assertTrue( "X position is " + component.getX() + " instead of " + expectedPos.x + ".", component.getX() == expectedPos.x );
		 assertTrue( "Y position is " + component.getY() + " instead of " + expectedPos.y + ".", component.getY() == expectedPos.y );
	 }
}
