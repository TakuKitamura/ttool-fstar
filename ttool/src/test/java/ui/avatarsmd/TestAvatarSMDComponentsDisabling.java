package ui.avatarsmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import ui.AbstractUITest;
import ui.CDElement;
import ui.TGComponent;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;

public class TestAvatarSMDComponentsDisabling extends AbstractUITest {

	private TMLArchiDiagramPanel diagramPanel;
	
	public TestAvatarSMDComponentsDisabling() {
		super();
	}
	
	@Before
    public void setUp() {
		final TMLArchiDiagramToolBar toolBar = new TMLArchiDiagramToolBar( mainGUI );
        diagramPanel = new TMLArchiDiagramPanel( mainGUI, toolBar );
    }
	
	@Test
    public void testDisableAvatarSMDChoice() {
        final AvatarSMDChoice element = new AvatarSMDChoice( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertFalse( element.canBeDisabled() );
    }
	
	@Test
    public void testDisableAvatarSMDConnectingPoint() {
        final CDElement element = new AvatarSMDConnectingPoint( null, 0, -5, true, false, 0.5, 0.0 );
        
        assertFalse( element.canBeDisabled() );
    }
	
	@Test
    public void testDisableAvatarSMDConnector() {
        final TGComponent startElement = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );

        final TGComponent sendSignalElement = new AvatarSMDSendSignal( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( sendSignalElement, sendSignalElement.getX(), sendSignalElement.getY(), false, true );

        final AvatarSMDConnector con1 = new AvatarSMDConnector( 0, 
		        												0,
		        												diagramPanel.getMinX(),
		        												diagramPanel.getMaxX(),
		        												diagramPanel.getMinY(),
		        												diagramPanel.getMaxY(),
		        												false,
		        												null,
		        												diagramPanel,
		        												startElement.getTGConnectingPointAtIndex( 0 ),
		        												sendSignalElement.getTGConnectingPointAtIndex( 0 ),
		        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent randomElement = new AvatarSMDRandom( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( randomElement, randomElement.getX(), randomElement.getY(), false, true );
        
        final AvatarSMDConnector con2 = new AvatarSMDConnector( 0, 
																0,
																diagramPanel.getMinX(),
																diagramPanel.getMaxX(),
																diagramPanel.getMinY(),
																diagramPanel.getMaxY(),
																false,
																null,
																diagramPanel,
																sendSignalElement.getTGConnectingPointAtIndex( 1 ),
																randomElement.getTGConnectingPointAtIndex( 0 ),
																new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );

        final TGComponent stopElement = new AvatarSMDStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElement, stopElement.getX(), stopElement.getY(), false, true );
        
        final AvatarSMDConnector con3 = new AvatarSMDConnector( 0, 
																0,
																diagramPanel.getMinX(),
																diagramPanel.getMaxX(),
																diagramPanel.getMinY(),
																diagramPanel.getMaxY(),
																false,
																null,
																diagramPanel,
																randomElement.getTGConnectingPointAtIndex( 1 ),
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
        assertTrue( con1.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( con1.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertFalse( sendSignalElement.isEnabled() );
        assertTrue( sendSignalElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertFalse( con2.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( con2.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertFalse( randomElement.isEnabled() );
        assertTrue( randomElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertFalse( con3.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( con3.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( stopElement.canBeDisabled() );
        
        con1.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( sendSignalElement.isEnabled() );
        assertTrue( sendSignalElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( randomElement.isEnabled() );
        assertTrue( randomElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        
        con2.setEnabled( false );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( sendSignalElement.isEnabled() );
        assertTrue( sendSignalElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertFalse( randomElement.isEnabled() );
        assertTrue( randomElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( stopElement.canBeDisabled() );
        
        con2.setEnabled( true );
        assertTrue( startElement.isEnabled() );
        assertFalse( startElement.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( sendSignalElement.isEnabled() );
        assertTrue( sendSignalElement.canBeDisabled() );
        assertTrue( con2.isEnabled() );
        assertTrue( con2.canBeDisabled() );
        assertTrue( randomElement.isEnabled() );
        assertTrue( randomElement.canBeDisabled() );
        assertTrue( con3.isEnabled() );
        assertFalse( con3.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( stopElement.canBeDisabled() );
	}

	@Test
    public void testDisableAvatarSMDExpireTimer() {
        final CDElement element = new AvatarSMDExpireTimer( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDLibraryFunctionCall() {
        final CDElement element = new AvatarSMDLibraryFunctionCall( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDRandom() {
        final CDElement element = new AvatarSMDRandom( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDReceiveSignal() {
        final CDElement element = new AvatarSMDReceiveSignal( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDResetTimer() {
        final CDElement element = new AvatarSMDResetTimer( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDSendSignal() {
        final CDElement element = new AvatarSMDSendSignal( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDSetTimer() {
        final CDElement element = new AvatarSMDSetTimer( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertTrue( element.canBeDisabled() );
        
        element.setEnabled( false );
        assertFalse( element.isEnabled() );

        element.setEnabled( true );
        assertTrue( element.isEnabled() );
    }

	@Test
    public void testDisableAvatarSMDStartState() {
        final CDElement element = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        
        assertFalse( element.canBeDisabled() );
    }

	@Test
    public void testDisableAvatarSMDState() {
        final TGComponent startElement = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );
        final AvatarSMDState compositeState1 = new AvatarSMDState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        compositeState1.setValue( "compositeState" );
        int indexState1 = 0;
        diagramPanel.addComponent( compositeState1, compositeState1.getX(), compositeState1.getY(), false, true );
        final String testName = "test";
        
        final TGComponent con1 = new AvatarSMDConnector( 	0, 
	        												0,
	        												diagramPanel.getMinX(),
	        												diagramPanel.getMaxX(),
	        												diagramPanel.getMinY(),
	        												diagramPanel.getMaxY(),
	        												false,
	        												null,
	        												diagramPanel,
	        												startElement.getTGConnectingPointAtIndex( 0 ),
	        												compositeState1.getTGConnectingPointAtIndex( 0 ),
	        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );
        
        final TGComponent startElementState1 = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, compositeState1, diagramPanel );
        compositeState1.addInternalComponent( startElementState1, indexState1++ );

        final TGComponent timerState11 = new AvatarSMDResetTimer( 500, 500, 0, 1400, 0, 1900, true, compositeState1, diagramPanel );
        compositeState1.addInternalComponent( timerState11, indexState1++ );
        
        final AvatarSMDConnector conState11 = new AvatarSMDConnector( 	0, 
				        												0,
				        												diagramPanel.getMinX(),
				        												diagramPanel.getMaxX(),
				        												diagramPanel.getMinY(),
				        												diagramPanel.getMaxY(),
				        												false,
				        												null,
				        												diagramPanel,
				        												startElementState1.getTGConnectingPointAtIndex( 0 ),
				        												timerState11.getTGConnectingPointAtIndex( 0 ),
				        												new Vector<Point>() );
        conState11.setTransitionInfo( "false", "a = b" );
        conState11.setTransitionTime( "0", "2", "0", "10" );
        diagramPanel.addComponent( conState11, conState11.getX(), conState11.getY(), false, true );
        final AvatarSMDState compositeState2 = new AvatarSMDState( 500, 500, 0, 1400, 0, 1900, true, compositeState1, diagramPanel );
        compositeState2.setValue( testName );
        int indexState2 = 0;
        compositeState1.addInternalComponent( compositeState2, indexState1++ );
        
        final TGComponent conState12 = new AvatarSMDConnector( 	0, 
		        												0,
		        												diagramPanel.getMinX(),
		        												diagramPanel.getMaxX(),
		        												diagramPanel.getMinY(),
		        												diagramPanel.getMaxY(),
		        												false,
		        												null,
		        												diagramPanel,
		        												timerState11.getTGConnectingPointAtIndex( 1 ),
		        												compositeState2.getTGConnectingPointAtIndex( 0 ),
		        												new Vector<Point>() );
        diagramPanel.addComponent( conState12, conState12.getX(), conState12.getY(), false, true );
        
        final TGComponent startElementState2 = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, compositeState2, diagramPanel );
        compositeState2.addInternalComponent( startElementState2, indexState2++ );
        final TGComponent receiveSignalState2 = new AvatarSMDReceiveSignal( 500, 500, 0, 1400, 0, 1900, true, compositeState2, diagramPanel );
        compositeState2.addInternalComponent( receiveSignalState2, indexState2++ );

        final TGComponent conState21 = new AvatarSMDConnector( 	0, 
																0,
																diagramPanel.getMinX(),
																diagramPanel.getMaxX(),
																diagramPanel.getMinY(),
																diagramPanel.getMaxY(),
																false,
																null,
																diagramPanel,
																startElementState2.getTGConnectingPointAtIndex( 0 ),
																receiveSignalState2.getTGConnectingPointAtIndex( 0 ),
																new Vector<Point>() );
        diagramPanel.addComponent( conState21, conState21.getX(), conState21.getY(), false, true );

        final TGComponent timerState12 = new AvatarSMDResetTimer( 500, 500, 0, 1400, 0, 1900, true, compositeState1, diagramPanel );
        compositeState1.addInternalComponent( timerState12, indexState1++ );

        final TGComponent conState13 = new AvatarSMDConnector( 	0, 
																0,
																diagramPanel.getMinX(),
																diagramPanel.getMaxX(),
																diagramPanel.getMinY(),
																diagramPanel.getMaxY(),
																false,
																null,
																diagramPanel,
																receiveSignalState2.getTGConnectingPointAtIndex( 1 ),
																timerState12.getTGConnectingPointAtIndex( 0 ),
																new Vector<Point>() );
        diagramPanel.addComponent( conState13, conState13.getX(), conState13.getY(), false, true );

        final TGComponent stateTest = new AvatarSMDState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        stateTest.setValue( testName );
        diagramPanel.addComponent( stateTest, stateTest.getX(), stateTest.getY(), false, true );

        final TGComponent conStateTest = new AvatarSMDConnector( 	0,
																	0,
																	diagramPanel.getMinX(),
																	diagramPanel.getMaxX(),
																	diagramPanel.getMinY(),
																	diagramPanel.getMaxY(),
																	false,
																	null,
																	diagramPanel,
																	timerState12.getTGConnectingPointAtIndex( 1 ),
																	stateTest.getTGConnectingPointAtIndex( 0 ),
																	new Vector<Point>() );
        diagramPanel.addComponent( conStateTest, conStateTest.getX(), conStateTest.getY(), false, true );

        final TGComponent receiveSignal = new AvatarSMDReceiveSignal( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        receiveSignal.setValue( testName );
        diagramPanel.addComponent( receiveSignal, receiveSignal.getX(), receiveSignal.getY(), false, true );

        final TGComponent conReceiveSignal = new AvatarSMDConnector( 	0,
																		0,
																		diagramPanel.getMinX(),
																		diagramPanel.getMaxX(),
																		diagramPanel.getMinY(),
																		diagramPanel.getMaxY(),
																		false,
																		null,
																		diagramPanel,
																		stateTest.getTGConnectingPointAtIndex( 1 ),
																		receiveSignal.getTGConnectingPointAtIndex( 0 ),
																		new Vector<Point>() );
        diagramPanel.addComponent( conReceiveSignal, conReceiveSignal.getX(), conReceiveSignal.getY(), false, true );

        final TGComponent stateTest2 = new AvatarSMDState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        stateTest2.setValue( "test2" );
        diagramPanel.addComponent( stateTest2, stateTest2.getX(), stateTest2.getY(), false, true );

        final TGComponent conStateTest2 = new AvatarSMDConnector( 	0,
																	0,
																	diagramPanel.getMinX(),
																	diagramPanel.getMaxX(),
																	diagramPanel.getMinY(),
																	diagramPanel.getMaxY(),
																	false,
																	null,
																	diagramPanel,
																	receiveSignal.getTGConnectingPointAtIndex( 1 ),
																	stateTest2.getTGConnectingPointAtIndex( 0 ),
																	new Vector<Point>() );
        diagramPanel.addComponent( conStateTest2, conStateTest2.getX(), conStateTest2.getY(), false, true );

        final TGComponent sendSignal = new AvatarSMDSendSignal( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        sendSignal.setValue( testName );
        diagramPanel.addComponent( sendSignal, sendSignal.getX(), sendSignal.getY(), false, true );

        final TGComponent conSendSignal = new AvatarSMDConnector( 	0,
																	0,
																	diagramPanel.getMinX(),
																	diagramPanel.getMaxX(),
																	diagramPanel.getMinY(),
																	diagramPanel.getMaxY(),
																	false,
																	null,
																	diagramPanel,
																	stateTest2.getTGConnectingPointAtIndex( 1 ),
																	sendSignal.getTGConnectingPointAtIndex( 0 ),
																	new Vector<Point>() );
        diagramPanel.addComponent( conSendSignal, conSendSignal.getX(), conSendSignal.getY(), false, true );

        final TGComponent stateTest3 = new AvatarSMDState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        stateTest3.setValue( testName );
        diagramPanel.addComponent( stateTest3, stateTest3.getX(), stateTest3.getY(), false, true );

        final TGComponent conStateTest3 = new AvatarSMDConnector( 	0,
																	0,
																	diagramPanel.getMinX(),
																	diagramPanel.getMaxX(),
																	diagramPanel.getMinY(),
																	diagramPanel.getMaxY(),
																	false,
																	null,
																	diagramPanel,
																	sendSignal.getTGConnectingPointAtIndex( 1 ),
																	stateTest3.getTGConnectingPointAtIndex( 0 ),
																	new Vector<Point>() );
        diagramPanel.addComponent( conStateTest3, conStateTest3.getX(), conStateTest3.getY(), false, true );

        final TGComponent stopElement = new AvatarSMDStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElement, stopElement.getX(), stopElement.getY(), false, true );

        final TGComponent conStop = new AvatarSMDConnector( 0,
															0,
															diagramPanel.getMinX(),
															diagramPanel.getMaxX(),
															diagramPanel.getMinY(),
															diagramPanel.getMaxY(),
															false,
															null,
															diagramPanel,
															stateTest3.getTGConnectingPointAtIndex( 1 ),
															stopElement.getTGConnectingPointAtIndex( 0 ),
															new Vector<Point>() );
        diagramPanel.addComponent( conStop, conStop.getX(), conStop.getY(), false, true );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( con1.canBeDisabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( conState12.canBeDisabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertFalse( stopElement.canBeDisabled() );
        assertFalse( conStop.canBeDisabled() );
        
        compositeState1.setEnabled( false );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertFalse( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertFalse( timerState11.canBeDisabled() );
        assertFalse( timerState11.isEnabled() );
        assertFalse( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertFalse( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertFalse( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertFalse( compositeState2.canBeDisabled() );
        assertFalse( compositeState2.isEnabled() );
        assertFalse( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertFalse( receiveSignalState2.canBeDisabled() );
        assertFalse( receiveSignalState2.isEnabled() );
        assertFalse( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertFalse( timerState12.canBeDisabled() );
        assertFalse( timerState12.isEnabled() );
        assertFalse( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( stateTest.isEnabled() );
        assertFalse( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );

        compositeState1.setEnabled( true );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( receiveSignalState2.isEnabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );
        
        compositeState2.setEnabled( false );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertFalse( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertFalse( receiveSignalState2.canBeDisabled() );
        assertFalse( receiveSignalState2.isEnabled() );
        assertFalse( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertFalse( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );

        compositeState2.setEnabled( true );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( receiveSignalState2.isEnabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );
        
        stateTest.setEnabled( false );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( receiveSignalState2.isEnabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertFalse( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertFalse( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );

        stateTest.setEnabled( true );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( receiveSignalState2.isEnabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );

        
        stateTest3.setEnabled( false );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( receiveSignalState2.isEnabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertFalse( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertFalse( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );

        stateTest.setEnabled( true );
        
        assertFalse( startElement.canBeDisabled() );
        assertTrue( startElement.isEnabled() );
        assertTrue( compositeState1.canBeDisabled() );
        assertTrue( compositeState1.isEnabled() );
        assertTrue( con1.canBeDisabled() );
        assertTrue( con1.isEnabled() );
        assertFalse( startElementState1.canBeDisabled() );
        assertTrue( startElementState1.isEnabled() );
        assertTrue( timerState11.canBeDisabled() );
        assertTrue( timerState11.isEnabled() );
        assertTrue( conState11.canBeDisabled() );
        assertTrue( conState11.isEnabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( conState11.getAvatarSMDTransitionInfo().isEnabled() );
        assertTrue( compositeState2.canBeDisabled() );
        assertTrue( compositeState2.isEnabled() );
        assertTrue( conState12.canBeDisabled() );
        assertTrue( conState12.isEnabled() );
        assertFalse( startElementState2.canBeDisabled() );
        assertTrue( startElementState2.isEnabled() );
        assertTrue( receiveSignalState2.canBeDisabled() );
        assertTrue( receiveSignalState2.isEnabled() );
        assertTrue( conState21.canBeDisabled() );
        assertTrue( conState21.isEnabled() );
        assertTrue( timerState12.canBeDisabled() );
        assertTrue( timerState12.isEnabled() );
        assertTrue( conState13.canBeDisabled() );
        assertTrue( conState13.isEnabled() );
        
        assertTrue( stateTest.canBeDisabled() );
        assertTrue( stateTest.isEnabled() );
        assertTrue( conStateTest.canBeDisabled() );
        assertTrue( conStateTest.isEnabled() );
        assertTrue( receiveSignal.canBeDisabled() );
        assertTrue( receiveSignal.isEnabled() );
        assertTrue( conReceiveSignal.canBeDisabled() );
        assertTrue( conReceiveSignal.isEnabled() );
        assertTrue( stateTest2.canBeDisabled() );
        assertTrue( stateTest2.isEnabled() );
        assertTrue( conStateTest2.canBeDisabled() );
        assertTrue( conStateTest2.isEnabled() );
        assertTrue( sendSignal.canBeDisabled() );
        assertTrue( sendSignal.isEnabled() );
        assertTrue( conSendSignal.canBeDisabled() );
        assertTrue( conSendSignal.isEnabled() );
        assertTrue( stateTest3.canBeDisabled() );
        assertTrue( stateTest3.isEnabled() );
        assertTrue( conStateTest3.canBeDisabled() );
        assertTrue( conStateTest3.isEnabled() );

        assertFalse( stopElement.canBeDisabled() );
        assertTrue( stopElement.isEnabled() );
        assertFalse( conStop.canBeDisabled() );
        assertTrue( conStop.isEnabled() );
	}

	@Test
    public void testDisableAvatarSMDStopState() {
        final TGComponent startElement = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );
        final TGComponent action = new AvatarSMDSetTimer( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( action, action.getX(), action.getY(), false, true );

        final TGComponent con1 = new AvatarSMDConnector( 	0, 
	        												0,
	        												diagramPanel.getMinX(),
	        												diagramPanel.getMaxX(),
	        												diagramPanel.getMinY(),
	        												diagramPanel.getMaxY(),
	        												false,
	        												null,
	        												diagramPanel,
	        												startElement.getTGConnectingPointAtIndex( 0 ),
	        												action.getTGConnectingPointAtIndex( 0 ),
	        												new Vector<Point>() );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent stopElement = new AvatarSMDStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElement, stopElement.getX(), stopElement.getY(), false, true );

        final TGComponent con2 = new AvatarSMDConnector( 	0, 
	        												0,
	        												diagramPanel.getMinX(),
	        												diagramPanel.getMaxX(),
	        												diagramPanel.getMinY(),
	        												diagramPanel.getMaxY(),
	        												false,
	        												null,
	        												diagramPanel,
	        												action.getTGConnectingPointAtIndex( 1 ),
	        												stopElement.getTGConnectingPointAtIndex( 0 ),
	        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );
        
        assertFalse( stopElement.canBeDisabled() );
        assertFalse( con2.canBeDisabled() );
    }

	@Test
    public void testDisableAvatarSMDTransitionInfo() {
        final TGComponent startElement = new AvatarSMDStartState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( startElement, startElement.getX(), startElement.getY(), false, true );
        final TGComponent setTimer = new AvatarSMDSetTimer( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( setTimer, setTimer.getX(), setTimer.getY(), false, true );

        final AvatarSMDConnector con1 = new AvatarSMDConnector( 0, 
		        												0,
		        												diagramPanel.getMinX(),
		        												diagramPanel.getMaxX(),
		        												diagramPanel.getMinY(),
		        												diagramPanel.getMaxY(),
		        												false,
		        												null,
		        												diagramPanel,
		        												startElement.getTGConnectingPointAtIndex( 0 ),
		        												setTimer.getTGConnectingPointAtIndex( 0 ),
		        												new Vector<Point>() );
        final String guard = "false";
        final String action = "a = b";
        con1.setTransitionInfo( guard, action );
        final String minAfter = "0";
        final String maxAfter = "2";
        final String minCompute = "1";
        final String maxCompute = "10";
        con1.setTransitionTime( minAfter, maxAfter, minCompute, maxCompute );
        diagramPanel.addComponent( con1, con1.getX(), con1.getY(), false, true );

        final TGComponent stopElement = new AvatarSMDStopState( 500, 500, 0, 1400, 0, 1900, true, null, diagramPanel );
        diagramPanel.addComponent( stopElement, stopElement.getX(), stopElement.getY(), false, true );

        final AvatarSMDConnector con2 = new AvatarSMDConnector( 0, 
		        												0,
		        												diagramPanel.getMinX(),
		        												diagramPanel.getMaxX(),
		        												diagramPanel.getMinY(),
		        												diagramPanel.getMaxY(),
		        												false,
		        												null,
		        												diagramPanel,
		        												setTimer.getTGConnectingPointAtIndex( 1 ),
		        												stopElement.getTGConnectingPointAtIndex( 0 ),
		        												new Vector<Point>() );
        diagramPanel.addComponent( con2, con2.getX(), con2.getY(), false, true );
        
        assertTrue( con1.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( con2.getAvatarSMDTransitionInfo().canBeDisabled() );
        
        con1.setEnabled( false );
        assertTrue( con1.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertTrue( con1.getAvatarSMDTransitionInfo().isEnabled() );
        assertEquals( guard, con1.getEffectiveGuard() );
        assertEquals( action, con1.getEffectiveActions().get( 0 ) );
        assertEquals( minAfter, con1.getEffectiveAfterMinDelay() );
        assertEquals( maxAfter, con1.getEffectiveAfterMaxDelay() );
        assertEquals( minCompute, con1.getEffectiveComputeMinDelay() );
        assertEquals( maxCompute, con1.getEffectiveComputeMaxDelay() );

        con1.getAvatarSMDTransitionInfo().setEnabled( false );
        assertTrue( con1.getAvatarSMDTransitionInfo().canBeDisabled() );
        assertFalse( con1.getAvatarSMDTransitionInfo().isEnabled() );
        assertEquals( AvatarSMDTransitionInfo.DISABLED_GUARD_EXPR, con1.getEffectiveGuard() );
        
        for ( final String expr : con1.getEffectiveActions() ) {
            assertEquals( AvatarSMDTransitionInfo.DISABLED_ACTION_EXPR, expr );
        }

        assertEquals( AvatarSMDTransitionInfo.DISABLED_DELAY_EXPR, con1.getEffectiveAfterMinDelay() );
        assertEquals( AvatarSMDTransitionInfo.DISABLED_DELAY_EXPR, con1.getEffectiveAfterMaxDelay() );
        assertEquals( AvatarSMDTransitionInfo.DISABLED_DELAY_EXPR, con1.getEffectiveComputeMinDelay() );
        assertEquals( AvatarSMDTransitionInfo.DISABLED_DELAY_EXPR, con1.getEffectiveComputeMaxDelay() );
    }
}
