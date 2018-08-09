package ui.tmlad;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import ui.AbstractUITest;
import ui.CDElement;
import ui.TGComponent;
import ui.ad.TADForLoop;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;

public class TestTMLADComponentsDisabling extends AbstractUITest {

	private TMLArchiDiagramPanel diagramPanel;
	
	public TestTMLADComponentsDisabling() {
		super();
	}
	
	@Before
    public void setUp() {
		final TMLArchiDiagramToolBar toolBar = new TMLArchiDiagramToolBar( mainGUI );
        diagramPanel = new TMLArchiDiagramPanel( mainGUI, toolBar );
    }
	
	@Test
    public void testDisableTGConnectingPointTMLAD() {
        final CDElement element = new TGConnectingPointTMLAD( null, 0, -5, true, false, 0.5, 0.0 );
        
        assertFalse( element.canBeDisabled() );
    }
	
	@Test
    public void testDisableTGConnectorTMLAD() {
        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TGComponent requestElement = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestElement, requestElement.getX(), requestElement.getY(), false, true );

        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												startElement.getTGConnectingPointAtIndex( 0 ),
        												requestElement.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent actionElement = new TMLADActionState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( actionElement, actionElement.getX(), actionElement.getY(), false, true );
        
        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
														0,
														diagramPanel.getMinX(),
														diagramPanel.getMaxX(),
														diagramPanel.getMinY(),
														diagramPanel.getMaxY(),
														false,
														null,
														diagramPanel,
														requestElement.getTGConnectingPointAtIndex( 1 ),
														actionElement.getTGConnectingPointAtIndex( 0 ),
														new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElement = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElement, stopElement.getX(), stopElement.getY(), false, true );
        
        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
														0,
														diagramPanel.getMinX(),
														diagramPanel.getMaxX(),
														diagramPanel.getMinY(),
														diagramPanel.getMaxY(),
														false,
														null,
														diagramPanel,
														actionElement.getTGConnectingPointAtIndex( 1 ),
														stopElement.getTGConnectingPointAtIndex( 0 ),
														new Vector<Point>() );
        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );
        
        assertTrue( con1.canBeDisabled() );
        assertTrue( con2.canBeDisabled() );
        assertFalse( con3.canBeDisabled() );
        
        con1.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( requestElement.isEnabled() );
        assertTrue( requestElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertFalse( actionElement.isEnabled() );
        assertTrue( actionElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( stopElement.canBeDisabled() );
        
        con1.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestElement.isEnabled() );
        assertTrue( requestElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( actionElement.isEnabled() );
        assertTrue( actionElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        
        con2.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestElement.isEnabled() );
        assertTrue( requestElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertFalse( actionElement.isEnabled() );
        assertTrue( actionElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( stopElement.canBeDisabled() );
        
        con2.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestElement.isEnabled() );
        assertTrue( requestElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( actionElement.isEnabled() );
        assertTrue( actionElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( stopElement.canBeDisabled() );
	}
	
//	@Test
//    public void testDisableTGConnectorTMLADWithChoice() {
//        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
//        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );
//
//        final TGComponent requestElement = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
//        diagramPanel.addComponent( requestElement, requestElement.getX(), requestElement.getY(), false, true );
//
//        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
//        												0,
//        												diagramPanel.getMinX(),
//        												diagramPanel.getMaxX(),
//        												diagramPanel.getMinY(),
//        												diagramPanel.getMaxY(),
//        												false,
//        												null,
//        												diagramPanel,
//        												startElement.getTGConnectingPointAtIndex( 0 ),
//        												requestElement.getTGConnectingPointAtIndex( 0 ),
//        												new Vector<Point>() );
//        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );
//
//        final TGComponent actionElement = new TMLADActionState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
//        diagramPanel.addComponent( actionElement, actionElement.getX(), actionElement.getY(), false, true );
//        
//        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
//														0,
//														diagramPanel.getMinX(),
//														diagramPanel.getMaxX(),
//														diagramPanel.getMinY(),
//														diagramPanel.getMaxY(),
//														false,
//														null,
//														diagramPanel,
//														requestElement.getTGConnectingPointAtIndex( 1 ),
//														actionElement.getTGConnectingPointAtIndex( 0 ),
//														new Vector<Point>() );
//        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );
//
//        final TGComponent stopElement = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
//        diagramPanel.addComponent( stopElement, stopElement.getX(), stopElement.getY(), false, true );
//        
//        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
//														0,
//														diagramPanel.getMinX(),
//														diagramPanel.getMaxX(),
//														diagramPanel.getMinY(),
//														diagramPanel.getMaxY(),
//														false,
//														null,
//														diagramPanel,
//														actionElement.getTGConnectingPointAtIndex( 1 ),
//														stopElement.getTGConnectingPointAtIndex( 0 ),
//														new Vector<Point>() );
//        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );
//        
//        assertTrue( con1.canBeDisabled() );
//        assertTrue( con2.canBeDisabled() );
//        assertFalse( con3.canBeDisabled() );
//        
//        con1.setEnabled( false );
//        assertTrue( startElement.isEnabled() );
//        assertTrue( con1.isEnabled() );
//        assertFalse( requestElement.isEnabled() );
//        assertTrue( con2.isEnabled() );
//        assertFalse( actionElement.isEnabled() );
//        assertTrue( con3.isEnabled() );
//        assertTrue( stopElement.isEnabled() );
//        
//        con1.setEnabled( true );
//        assertTrue( startElement.isEnabled() );
//        assertTrue( con1.isEnabled() );
//        assertTrue( requestElement.isEnabled() );
//        assertTrue( con2.isEnabled() );
//        assertTrue( actionElement.isEnabled() );
//        assertTrue( con3.isEnabled() );
//        assertTrue( stopElement.isEnabled() );
//        
//        con2.setEnabled( false );
//        assertTrue( startElement.isEnabled() );
//        assertTrue( con1.isEnabled() );
//        assertTrue( requestElement.isEnabled() );
//        assertTrue( con2.isEnabled() );
//        assertFalse( actionElement.isEnabled() );
//        assertTrue( con3.isEnabled() );
//        assertTrue( stopElement.isEnabled() );
//        
//        con2.setEnabled( true );
//        assertTrue( startElement.isEnabled() );
//        assertTrue( con1.isEnabled() );
//        assertTrue( requestElement.isEnabled() );
//        assertTrue( con2.isEnabled() );
//        assertTrue( actionElement.isEnabled() );
//        assertTrue( con3.isEnabled() );
//        assertTrue( stopElement.isEnabled() );
//	}

	@Test
    public void testDisableTMLADActionState() {
        final CDElement element = new TMLADActionState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADChoice() {
        final TMLADChoice element = new TMLADChoice( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertFalse( element.canBeDisabled() );
        
        element.setGuard( Boolean.TRUE.toString(), 0 );
        TGComponent guard = element.getInternalTGComponent( 0 );
        assertTrue( guard.canBeDisabled() );
        
        guard.setEnabled( false );
        assertFalse( guard.isEnabled() );

        guard.setEnabled( true );
        assertTrue( guard.isEnabled() );

        element.setGuard( null, 0 );
        guard = element.getInternalTGComponent( 0 );
        assertFalse( guard.canBeDisabled() );

        element.setGuard( "", 0 );
        guard = element.getInternalTGComponent( 0 );
        assertFalse( guard.canBeDisabled() );

        element.setGuard( TMLADChoice.EMPTY_GUARD_TEXT, 0 );
        guard = element.getInternalTGComponent( 0 );
        assertFalse( guard.canBeDisabled() );
    }
	
	@Test
    public void testDisableTMLADDecrypt() {
        final CDElement element = new TMLADDecrypt( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADDelay() {
        final CDElement element = new TMLADDelay( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADDelayInterval() {
        final CDElement element = new TMLADDelayInterval( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADEncrypt() {
        final CDElement element = new TMLADEncrypt( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADExecC() {
        final CDElement element = new TMLADExecC( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADExecCInterval() {
        final CDElement element = new TMLADExecCInterval( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADExecI() {
        final CDElement element = new TMLADExecI( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADExecIInterval() {
        final CDElement element = new TMLADExecIInterval( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
	
	@Test
    public void testDisableTMLADForEverLoop() {
        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TADForLoop forLoop = new TMLADForEverLoop( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( forLoop, forLoop.getX(), forLoop.getY(), false, true );

        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												startElement.getTGConnectingPointAtIndex( 0 ),
        												forLoop.getEnterLoopConnectingPoint(),
        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent requestInLoop = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestInLoop, requestInLoop.getX(),requestInLoop.getY(), false, true );

        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												forLoop.getInsideLoopConnectingPoint(),
        												requestInLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElementInLoop = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementInLoop, stopElementInLoop.getX(), stopElementInLoop.getY(), false, true );

        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestInLoop.getTGConnectingPointAtIndex( 1 ),
        												stopElementInLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );
        
        assertTrue( forLoop.canBeDisabled() );
        
        forLoop.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertFalse( forLoop.isEnabled() );
        assertTrue( forLoop.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( requestInLoop.isEnabled() );
        assertFalse( requestInLoop.canBeDisabled() );
        assertFalse( con2.isEnabled() );
        assertFalse( con2.canBeDisabled() );
        assertFalse( stopElementInLoop.isEnabled() );
        assertFalse( stopElementInLoop.canBeDisabled() );
        assertFalse( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );

        forLoop.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( forLoop.isEnabled() );
        assertTrue( forLoop.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestInLoop.isEnabled() );
        assertTrue( requestInLoop.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementInLoop.isEnabled() );
        assertFalse( stopElementInLoop.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
    }
	
	@Test
    public void testDisableTMLADForLoop() {
        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TADForLoop forLoop = new TMLADForLoop( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( forLoop, forLoop.getX(), forLoop.getY(), false, true );

        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												startElement.getTGConnectingPointAtIndex( 0 ),
        												forLoop.getEnterLoopConnectingPoint(),
        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent requestInLoop = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestInLoop, requestInLoop.getX(),requestInLoop.getY(), false, true );

        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												forLoop.getInsideLoopConnectingPoint(),
        												requestInLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElementInLoop = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementInLoop, stopElementInLoop.getX(), stopElementInLoop.getY(), false, true );

        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestInLoop.getTGConnectingPointAtIndex( 1 ),
        												stopElementInLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );

        final TGComponent requestExitLoop = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestExitLoop, requestExitLoop.getX(),requestExitLoop.getY(), false, true );

        final TGComponent con4 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												forLoop.getExitLoopConnectingPoint(),
        												requestExitLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con4, con4.getX(), con4.getY(), false, true );

        final TGComponent stopElementExitLoop = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementExitLoop, stopElementExitLoop.getX(), stopElementExitLoop.getY(), false, true );

        final TGComponent con5 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestExitLoop.getTGConnectingPointAtIndex( 1 ),
        												stopElementExitLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con5, con5.getX(), con5.getY(), false, true );
        
        assertTrue( forLoop.canBeDisabled() );
        
        forLoop.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertFalse( forLoop.isEnabled() );
        assertTrue( forLoop.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( requestInLoop.isEnabled() );
        assertFalse( requestInLoop.canBeDisabled() );
        assertFalse( con2.isEnabled() );
        assertFalse( con2.canBeDisabled() );
        assertFalse( stopElementInLoop.isEnabled() );
        assertFalse( stopElementInLoop.canBeDisabled() );
        assertFalse( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( requestExitLoop.isEnabled() );
        assertTrue( requestExitLoop.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementExitLoop.isEnabled() );
        assertFalse( stopElementExitLoop.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );

        forLoop.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( forLoop.isEnabled() );
        assertTrue( forLoop.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestInLoop.isEnabled() );
        assertTrue( requestInLoop.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementInLoop.isEnabled() );
        assertFalse( stopElementInLoop.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( requestExitLoop.isEnabled() );
        assertTrue( requestExitLoop.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementExitLoop.isEnabled() );
        assertFalse( stopElementExitLoop.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );
    }
	
	@Test
    public void testDisableTMLADForStaticLoop() {
        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TADForLoop forLoop = new TMLADForStaticLoop( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( forLoop, forLoop.getX(), forLoop.getY(), false, true );

        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												startElement.getTGConnectingPointAtIndex( 0 ),
        												forLoop.getEnterLoopConnectingPoint(),
        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent requestInLoop = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestInLoop, requestInLoop.getX(),requestInLoop.getY(), false, true );

        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												forLoop.getInsideLoopConnectingPoint(),
        												requestInLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElementInLoop = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementInLoop, stopElementInLoop.getX(), stopElementInLoop.getY(), false, true );

        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestInLoop.getTGConnectingPointAtIndex( 1 ),
        												stopElementInLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );

        final TGComponent requestExitLoop = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestExitLoop, requestExitLoop.getX(),requestExitLoop.getY(), false, true );

        final TGComponent con4 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												forLoop.getExitLoopConnectingPoint(),
        												requestExitLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con4, con4.getX(), con4.getY(), false, true );

        final TGComponent stopElementExitLoop = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementExitLoop, stopElementExitLoop.getX(), stopElementExitLoop.getY(), false, true );

        final TGComponent con5 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestExitLoop.getTGConnectingPointAtIndex( 1 ),
        												stopElementExitLoop.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con5, con5.getX(), con5.getY(), false, true );
        
        assertTrue( forLoop.canBeDisabled() );
        
        forLoop.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertFalse( forLoop.isEnabled() );
        assertTrue( forLoop.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( requestInLoop.isEnabled() );
        assertFalse( requestInLoop.canBeDisabled() );
        assertFalse( con2.isEnabled() );
        assertFalse( con2.canBeDisabled() );
        assertFalse( stopElementInLoop.isEnabled() );
        assertFalse( stopElementInLoop.canBeDisabled() );
        assertFalse( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( requestExitLoop.isEnabled() );
        assertTrue( requestExitLoop.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementExitLoop.isEnabled() );
        assertFalse( stopElementExitLoop.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );

        forLoop.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( forLoop.isEnabled() );
        assertTrue( forLoop.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestInLoop.isEnabled() );
        assertTrue( requestInLoop.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementInLoop.isEnabled() );
        assertFalse( stopElementInLoop.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( requestExitLoop.isEnabled() );
        assertTrue( requestExitLoop.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementExitLoop.isEnabled() );
        assertFalse( stopElementExitLoop.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );
    }

	@Test
    public void testDisableTMLADNotifiedEvent() {
        final CDElement element = new TMLADNotifiedEvent( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADRandom() {
        final CDElement element = new TMLADRandom( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADReadChannel() {
        final CDElement element = new TMLADReadChannel( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADReadRequestArg() {
        final CDElement element = new TMLADReadRequestArg( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADSelectEvt() {
        final CDElement element = new TMLADSelectEvt( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertFalse( element.canBeDisabled() );
    }

	@Test
    public void testDisableTMLADSendEvent() {
        final CDElement element = new TMLADSendEvent( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADSendRequest() {
        final CDElement element = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADSequence() {
        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TMLADSequence sequence = new TMLADSequence( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( sequence, sequence.getX(), sequence.getY(), false, true );

        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												startElement.getTGConnectingPointAtIndex( 0 ),
        												sequence.getEnterConnectingPoint(),
        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent requestBranch1 = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestBranch1, requestBranch1.getX(),requestBranch1.getY(), false, true );

        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												sequence.getExitConnectingPoints().get( 0 ),
        												requestBranch1.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElementBranch1 = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementBranch1, stopElementBranch1.getX(), stopElementBranch1.getY(), false, true );

        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestBranch1.getTGConnectingPointAtIndex( 1 ),
        												stopElementBranch1.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );


        final TGComponent requestBranch2 = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestBranch2, requestBranch2.getX(),requestBranch2.getY(), false, true );

        final TGComponent con4 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												sequence.getExitConnectingPoints().get( 1 ),
        												requestBranch2.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con4, con4.getX(), con4.getY(), false, true );

        final TGComponent stopElementBranch2 = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementBranch2, stopElementBranch2.getX(), stopElementBranch2.getY(), false, true );

        final TGComponent con5 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestBranch2.getTGConnectingPointAtIndex( 1 ),
        												stopElementBranch2.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con5, con5.getX(), con5.getY(), false, true );

        final TGComponent requestBranch3 = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestBranch3, requestBranch3.getX(),requestBranch3.getY(), false, true );

        final TGComponent con6 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												sequence.getExitConnectingPoints().get( 2 ),
        												requestBranch3.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con6, con6.getX(), con6.getY(), false, true );

        final TGComponent stopElementBranch3 = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementBranch1, stopElementBranch3.getX(), stopElementBranch3.getY(), false, true );

        final TGComponent con7 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestBranch3.getTGConnectingPointAtIndex( 1 ),
        												stopElementBranch3.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con7, con7.getX(), con7.getY(), false, true );
        
        assertTrue( sequence.canBeDisabled() );
        
        sequence.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertFalse( sequence.isEnabled() );
        assertTrue( sequence.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( requestBranch1.isEnabled() );
        assertTrue( requestBranch1.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementBranch1.isEnabled() );
        assertFalse( stopElementBranch1.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertFalse( requestBranch2.isEnabled() );
        assertTrue( requestBranch2.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementBranch2.isEnabled() );
        assertFalse( stopElementBranch2.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );
        assertFalse( requestBranch3.isEnabled() );
        assertTrue( requestBranch3.canBeDisabled() );
        assertTrue( con6.isEnabled() );
        assertTrue( con6.canBeDisabled() );
        assertTrue( stopElementBranch3.isEnabled() );
        assertFalse( stopElementBranch3.canBeDisabled() );
        assertTrue( con7.isEnabled() );
        assertFalse( con7.canBeDisabled() );

        sequence.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( sequence.isEnabled() );
        assertTrue( sequence.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestBranch1.isEnabled() );
        assertTrue( requestBranch1.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementBranch1.isEnabled() );
        assertFalse( stopElementBranch1.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( requestBranch2.isEnabled() );
        assertTrue( requestBranch2.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementBranch2.isEnabled() );
        assertFalse( stopElementBranch2.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );
        assertTrue( requestBranch3.isEnabled() );
        assertTrue( requestBranch3.canBeDisabled() );
        assertTrue( con6.isEnabled() );
        assertTrue( con6.canBeDisabled() );
        assertTrue( stopElementBranch3.isEnabled() );
        assertFalse( stopElementBranch3.canBeDisabled() );
        assertTrue( con7.isEnabled() );
        assertFalse( con7.canBeDisabled() );
    }

	@Test
    public void testDisableTMLADStartState() {
        final CDElement element = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertFalse( element.canBeDisabled() );
    }

	@Test
    public void testDisableTMLADStopState() {
        final CDElement element = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertFalse( element.canBeDisabled() );
    }

	@Test
    public void testDisableTMLADUnorderedSequence() {
        final TGComponent startElement = new TMLADStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TMLADUnorderedSequence sequence = new TMLADUnorderedSequence( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( sequence, sequence.getX(), sequence.getY(), false, true );

        final TGComponent con1 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												startElement.getTGConnectingPointAtIndex( 0 ),
        												sequence.getEnterConnectingPoint(),
        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent requestBranch1 = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestBranch1, requestBranch1.getX(),requestBranch1.getY(), false, true );

        final TGComponent con2 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												sequence.getExitConnectingPoints().get( 0 ),
        												requestBranch1.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElementBranch1 = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementBranch1, stopElementBranch1.getX(), stopElementBranch1.getY(), false, true );

        final TGComponent con3 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestBranch1.getTGConnectingPointAtIndex( 1 ),
        												stopElementBranch1.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con3, con3.getX(), con3.getY(), false, true );


        final TGComponent requestBranch2 = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestBranch2, requestBranch2.getX(),requestBranch2.getY(), false, true );

        final TGComponent con4 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												sequence.getExitConnectingPoints().get( 1 ),
        												requestBranch2.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con4, con4.getX(), con4.getY(), false, true );

        final TGComponent stopElementBranch2 = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementBranch2, stopElementBranch2.getX(), stopElementBranch2.getY(), false, true );

        final TGComponent con5 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestBranch2.getTGConnectingPointAtIndex( 1 ),
        												stopElementBranch2.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con5, con5.getX(), con5.getY(), false, true );

        final TGComponent requestBranch3 = new TMLADSendRequest( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( requestBranch3, requestBranch3.getX(),requestBranch3.getY(), false, true );

        final TGComponent con6 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												sequence.getExitConnectingPoints().get( 2 ),
        												requestBranch3.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con6, con6.getX(), con6.getY(), false, true );

        final TGComponent stopElementBranch3 = new TMLADStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElementBranch1, stopElementBranch3.getX(), stopElementBranch3.getY(), false, true );

        final TGComponent con7 = new TGConnectorTMLAD( 	0, 
        												0,
        												diagramPanel.getMinX(),
        												diagramPanel.getMaxX(),
        												diagramPanel.getMinY(),
        												diagramPanel.getMaxY(),
        												false,
        												null,
        												diagramPanel,
        												requestBranch3.getTGConnectingPointAtIndex( 1 ),
        												stopElementBranch3.getTGConnectingPointAtIndex( 0 ),
        												new Vector<Point>() );
        diagramPanel.addComponent( con7, con7.getX(), con7.getY(), false, true );
        
        assertTrue( sequence.canBeDisabled() );
        
        sequence.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertFalse( sequence.isEnabled() );
        assertTrue( sequence.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( requestBranch1.isEnabled() );
        assertTrue( requestBranch1.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementBranch1.isEnabled() );
        assertFalse( stopElementBranch1.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertFalse( requestBranch2.isEnabled() );
        assertTrue( requestBranch2.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementBranch2.isEnabled() );
        assertFalse( stopElementBranch2.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );
        assertFalse( requestBranch3.isEnabled() );
        assertTrue( requestBranch3.canBeDisabled() );
        assertTrue( con6.isEnabled() );
        assertTrue( con6.canBeDisabled() );
        assertTrue( stopElementBranch3.isEnabled() );
        assertFalse( stopElementBranch3.canBeDisabled() );
        assertTrue( con7.isEnabled() );
        assertFalse( con7.canBeDisabled() );

        sequence.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( sequence.isEnabled() );
        assertTrue( sequence.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( requestBranch1.isEnabled() );
        assertTrue( requestBranch1.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( stopElementBranch1.isEnabled() );
        assertFalse( stopElementBranch1.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( requestBranch2.isEnabled() );
        assertTrue( requestBranch2.canBeDisabled() );
        assertTrue( con4.isEnabled() );
        assertTrue( con4.canBeDisabled() );
        assertTrue( stopElementBranch2.isEnabled() );
        assertFalse( stopElementBranch2.canBeDisabled() );
        assertTrue( con5.isEnabled() );
        assertFalse( con5.canBeDisabled() );
        assertTrue( requestBranch3.isEnabled() );
        assertTrue( requestBranch3.canBeDisabled() );
        assertTrue( con6.isEnabled() );
        assertTrue( con6.canBeDisabled() );
        assertTrue( stopElementBranch3.isEnabled() );
        assertFalse( stopElementBranch3.canBeDisabled() );
        assertTrue( con7.isEnabled() );
        assertFalse( con7.canBeDisabled() );
    }

	@Test
    public void testDisableTMLADWaitEvent() {
        final CDElement element = new TMLADWaitEvent( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableTMLADWriteChannel() {
        final CDElement element = new TMLADWriteChannel( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }
}
