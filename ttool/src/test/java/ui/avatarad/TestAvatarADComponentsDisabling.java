package ui.avatarad;

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

public class TestAvatarADComponentsDisabling extends AbstractUITest {

    private TMLArchiDiagramPanel diagramPanel;

    public TestAvatarADComponentsDisabling() {
        super();
    }

    @Before
    public void setUp() {
        final TMLArchiDiagramToolBar toolBar = new TMLArchiDiagramToolBar(mainGUI);
        diagramPanel = new TMLArchiDiagramPanel(mainGUI, toolBar);
    }

    @Test
    public void testDisableAvatarADAcceptEventAction() {
        final CDElement element = new AvatarADAcceptEventAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertTrue(element.canBeDisabled());

        element.setEnabled(false);
        assertFalse(element.isEnabled());

        element.setEnabled(true);
        assertTrue(element.isEnabled());
    }

    @Test
    public void testDisableAvatarADAction() {
        final CDElement element = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertTrue(element.canBeDisabled());

        element.setEnabled(false);
        assertFalse(element.isEnabled());

        element.setEnabled(true);
        assertTrue(element.isEnabled());
    }

    @Test
    public void testDisableAvatarADActivity() {
        final CDElement element = new AvatarADActivity(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertTrue(element.canBeDisabled());

        element.setEnabled(false);
        assertFalse(element.isEnabled());

        element.setEnabled(true);
        assertTrue(element.isEnabled());
    }

    @Test
    public void testDisableAvatarADAssociationConnector() {
        final TGComponent startElement = new AvatarADStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);

        final TGComponent requestElement = new AvatarADSendSignalAction(500, 500, 0, 1400, 0, 1900, true, null,
                diagramPanel);
        diagramPanel.addComponent(requestElement, requestElement.getX(), requestElement.getY(), false, true);

        final TGComponent con1 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                startElement.getTGConnectingPointAtIndex(0), requestElement.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

        final TGComponent actionElement = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(actionElement, actionElement.getX(), actionElement.getY(), false, true);

        final TGComponent con2 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                requestElement.getTGConnectingPointAtIndex(1), actionElement.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

        final TGComponent stopElement = new AvatarADStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(stopElement, stopElement.getX(), stopElement.getY(), false, true);

        final TGComponent con3 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                actionElement.getTGConnectingPointAtIndex(1), stopElement.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

        assertTrue(con1.canBeDisabled());
        assertTrue(con2.canBeDisabled());
        assertFalse(con3.canBeDisabled());

        con1.setEnabled(false);
        assertTrue(startElement.isEnabled());
        assertFalse(startElement.canBeDisabled());
        assertTrue(con1.isEnabled());
        assertTrue(con1.canBeDisabled());
        assertFalse(requestElement.isEnabled());
        assertTrue(requestElement.canBeDisabled());
        assertTrue(con2.isEnabled());
        assertTrue(con2.canBeDisabled());
        assertFalse(actionElement.isEnabled());
        assertTrue(actionElement.canBeDisabled());
        assertTrue(con3.isEnabled());
        assertFalse(con3.canBeDisabled());
        assertTrue(stopElement.isEnabled());
        assertFalse(stopElement.canBeDisabled());

        con1.setEnabled(true);
        assertTrue(startElement.isEnabled());
        assertFalse(startElement.canBeDisabled());
        assertTrue(con1.isEnabled());
        assertTrue(con1.canBeDisabled());
        assertTrue(requestElement.isEnabled());
        assertTrue(requestElement.canBeDisabled());
        assertTrue(con2.isEnabled());
        assertTrue(con2.canBeDisabled());
        assertTrue(actionElement.isEnabled());
        assertTrue(actionElement.canBeDisabled());
        assertTrue(con3.isEnabled());
        assertFalse(con3.canBeDisabled());
        assertTrue(stopElement.isEnabled());

        con2.setEnabled(false);
        assertTrue(startElement.isEnabled());
        assertFalse(startElement.canBeDisabled());
        assertTrue(con1.isEnabled());
        assertTrue(con1.canBeDisabled());
        assertTrue(requestElement.isEnabled());
        assertTrue(requestElement.canBeDisabled());
        assertTrue(con2.isEnabled());
        assertTrue(con2.canBeDisabled());
        assertFalse(actionElement.isEnabled());
        assertTrue(actionElement.canBeDisabled());
        assertTrue(con3.isEnabled());
        assertTrue(stopElement.isEnabled());
        assertFalse(stopElement.canBeDisabled());

        con2.setEnabled(true);
        assertTrue(startElement.isEnabled());
        assertFalse(startElement.canBeDisabled());
        assertTrue(con1.isEnabled());
        assertTrue(con1.canBeDisabled());
        assertTrue(requestElement.isEnabled());
        assertTrue(requestElement.canBeDisabled());
        assertTrue(con2.isEnabled());
        assertTrue(con2.canBeDisabled());
        assertTrue(actionElement.isEnabled());
        assertTrue(actionElement.canBeDisabled());
        assertTrue(con3.isEnabled());
        assertFalse(con3.canBeDisabled());
        assertTrue(stopElement.isEnabled());
        assertFalse(stopElement.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADAssociationConnectorWithChoice() {
        final TGComponent startElement = new AvatarADStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);

        final TGComponent requestElement = new AvatarADSendSignalAction(500, 500, 0, 1400, 0, 1900, true, null,
                diagramPanel);
        diagramPanel.addComponent(requestElement, requestElement.getX(), requestElement.getY(), false, true);

        final TGComponent con1 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                startElement.getTGConnectingPointAtIndex(0), requestElement.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

        final TGComponent actionElement = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(actionElement, actionElement.getX(), actionElement.getY(), false, true);

        final TGComponent con2 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                requestElement.getTGConnectingPointAtIndex(1), actionElement.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

        final TGComponent stopElement = new AvatarADStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(stopElement, stopElement.getX(), stopElement.getY(), false, true);

        final TGComponent con3 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                actionElement.getTGConnectingPointAtIndex(1), stopElement.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

        assertTrue(con1.canBeDisabled());
        assertTrue(con2.canBeDisabled());
        assertFalse(con3.canBeDisabled());

        con1.setEnabled(false);
        assertTrue(startElement.isEnabled());
        assertTrue(con1.isEnabled());
        assertFalse(requestElement.isEnabled());
        assertTrue(con2.isEnabled());
        assertFalse(actionElement.isEnabled());
        assertTrue(con3.isEnabled());
        assertTrue(stopElement.isEnabled());

        con1.setEnabled(true);
        assertTrue(startElement.isEnabled());
        assertTrue(con1.isEnabled());
        assertTrue(requestElement.isEnabled());
        assertTrue(con2.isEnabled());
        assertTrue(actionElement.isEnabled());
        assertTrue(con3.isEnabled());
        assertTrue(stopElement.isEnabled());

        con2.setEnabled(false);
        assertTrue(startElement.isEnabled());
        assertTrue(con1.isEnabled());
        assertTrue(requestElement.isEnabled());
        assertTrue(con2.isEnabled());
        assertFalse(actionElement.isEnabled());
        assertTrue(con3.isEnabled());
        assertTrue(stopElement.isEnabled());

        con2.setEnabled(true);
        assertTrue(startElement.isEnabled());
        assertTrue(con1.isEnabled());
        assertTrue(requestElement.isEnabled());
        assertTrue(con2.isEnabled());
        assertTrue(actionElement.isEnabled());
        assertTrue(con3.isEnabled());
        assertTrue(stopElement.isEnabled());
    }

    @Test
    public void testDisableAvatarADChoice() {
        final AvatarADChoice element = new AvatarADChoice(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertFalse(element.canBeDisabled());

        element.setGuard(Boolean.TRUE.toString(), 0);
        TGComponent guard = element.getInternalTGComponent(0);
        assertTrue(guard.canBeDisabled());

        guard.setEnabled(false);
        assertFalse(guard.isEnabled());

        guard.setEnabled(true);
        assertTrue(guard.isEnabled());

        element.setGuard(null, 0);
        guard = element.getInternalTGComponent(0);
        assertFalse(guard.canBeDisabled());

        element.setGuard("", 0);
        guard = element.getInternalTGComponent(0);
        assertFalse(guard.canBeDisabled());

        element.setGuard(AvatarADChoice.EMPTY_GUARD_TEXT, 0);
        guard = element.getInternalTGComponent(0);
        assertFalse(guard.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADConnectingPoint() {
        final CDElement element = new AvatarADConnectingPoint(null, 0, -5, true, false, 0.5, 0.0);

        assertFalse(element.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADJunction() {
        final CDElement element = new AvatarADJunction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertTrue(element.isEnabled());
        assertFalse(element.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADParallel() {
        final TGComponent startElement = new AvatarADStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);

        final AvatarADParallel parallel = new AvatarADParallel(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(parallel, parallel.getX(), parallel.getY(), false, true);

        final TGComponent con1 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                startElement.getTGConnectingPointAtIndex(0), parallel.getEnterConnectingPoints().get(0),
                new Vector<Point>());
        diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

        final TGComponent actionBranch1 = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(actionBranch1, actionBranch1.getX(), actionBranch1.getY(), false, true);

        final TGComponent con2 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                parallel.getExitConnectingPoints().get(0), actionBranch1.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

        final TGComponent stopElementBranch1 = new AvatarADStopState(500, 500, 0, 1400, 0, 1900, true, null,
                diagramPanel);
        diagramPanel.addComponent(stopElementBranch1, stopElementBranch1.getX(), stopElementBranch1.getY(), false,
                true);

        final TGComponent con3 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                actionBranch1.getTGConnectingPointAtIndex(1), stopElementBranch1.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

        final TGComponent actionBranch2 = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(actionBranch2, actionBranch2.getX(), actionBranch2.getY(), false, true);

        final TGComponent con4 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                parallel.getExitConnectingPoints().get(1), actionBranch2.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con4, con4.getX(), con4.getY(), false, true);

        final TGComponent stopElementBranch2 = new AvatarADStopState(500, 500, 0, 1400, 0, 1900, true, null,
                diagramPanel);
        diagramPanel.addComponent(stopElementBranch2, stopElementBranch2.getX(), stopElementBranch2.getY(), false,
                true);

        final TGComponent con5 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                actionBranch2.getTGConnectingPointAtIndex(1), stopElementBranch2.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con5, con5.getX(), con5.getY(), false, true);

        final TGComponent actionBranch3 = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(actionBranch3, actionBranch3.getX(), actionBranch3.getY(), false, true);

        final TGComponent con6 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                parallel.getExitConnectingPoints().get(2), actionBranch3.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con6, con6.getX(), con6.getY(), false, true);

        final TGComponent stopElementBranch3 = new AvatarADStopState(500, 500, 0, 1400, 0, 1900, true, null,
                diagramPanel);
        diagramPanel.addComponent(stopElementBranch1, stopElementBranch3.getX(), stopElementBranch3.getY(), false,
                true);

        final TGComponent con7 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                actionBranch3.getTGConnectingPointAtIndex(1), stopElementBranch3.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con7, con7.getX(), con7.getY(), false, true);

        assertTrue(parallel.canBeDisabled());

        parallel.setEnabled(false);
        assertTrue(startElement.isEnabled());
        assertFalse(startElement.canBeDisabled());
        assertFalse(parallel.isEnabled());
        assertTrue(parallel.canBeDisabled());
        assertTrue(con1.isEnabled());
        assertTrue(con1.canBeDisabled());
        assertFalse(actionBranch1.isEnabled());
        assertTrue(actionBranch1.canBeDisabled());
        assertTrue(con2.isEnabled());
        assertTrue(con2.canBeDisabled());
        assertTrue(stopElementBranch1.isEnabled());
        assertFalse(stopElementBranch1.canBeDisabled());
        assertTrue(con3.isEnabled());
        assertFalse(con3.canBeDisabled());
        assertFalse(actionBranch2.isEnabled());
        assertTrue(actionBranch2.canBeDisabled());
        assertTrue(con4.isEnabled());
        assertTrue(con4.canBeDisabled());
        assertTrue(stopElementBranch2.isEnabled());
        assertFalse(stopElementBranch2.canBeDisabled());
        assertTrue(con5.isEnabled());
        assertFalse(con5.canBeDisabled());
        assertFalse(actionBranch3.isEnabled());
        assertTrue(actionBranch3.canBeDisabled());
        assertTrue(con6.isEnabled());
        assertTrue(con6.canBeDisabled());
        assertTrue(stopElementBranch3.isEnabled());
        assertFalse(stopElementBranch3.canBeDisabled());
        assertTrue(con7.isEnabled());
        assertFalse(con7.canBeDisabled());

        parallel.setEnabled(true);
        assertTrue(startElement.isEnabled());
        assertFalse(startElement.canBeDisabled());
        assertTrue(parallel.isEnabled());
        assertTrue(parallel.canBeDisabled());
        assertTrue(con1.isEnabled());
        assertTrue(con1.canBeDisabled());
        assertTrue(actionBranch1.isEnabled());
        assertTrue(actionBranch1.canBeDisabled());
        assertTrue(con2.isEnabled());
        assertTrue(con2.canBeDisabled());
        assertTrue(stopElementBranch1.isEnabled());
        assertFalse(stopElementBranch1.canBeDisabled());
        assertTrue(con3.isEnabled());
        assertFalse(con3.canBeDisabled());
        assertTrue(actionBranch2.isEnabled());
        assertTrue(actionBranch2.canBeDisabled());
        assertTrue(con4.isEnabled());
        assertTrue(con4.canBeDisabled());
        assertTrue(stopElementBranch2.isEnabled());
        assertFalse(stopElementBranch2.canBeDisabled());
        assertTrue(con5.isEnabled());
        assertFalse(con5.canBeDisabled());
        assertTrue(actionBranch3.isEnabled());
        assertTrue(actionBranch3.canBeDisabled());
        assertTrue(con6.isEnabled());
        assertTrue(con6.canBeDisabled());
        assertTrue(stopElementBranch3.isEnabled());
        assertFalse(stopElementBranch3.canBeDisabled());
        assertTrue(con7.isEnabled());
        assertFalse(con7.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADSendSignalAction() {
        final CDElement element = new AvatarADSendSignalAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertTrue(element.canBeDisabled());

        element.setEnabled(false);
        assertFalse(element.isEnabled());

        element.setEnabled(true);
        assertTrue(element.isEnabled());
    }

    @Test
    public void testDisableAvatarADStartState() {
        final CDElement element = new AvatarADStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertFalse(element.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADStopFlow() {
        final TGComponent startElement = new AvatarADStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);
        final TGComponent action = new AvatarADAction(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(action, action.getX(), action.getY(), false, true);

        final TGComponent con1 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                startElement.getTGConnectingPointAtIndex(0), action.getTGConnectingPointAtIndex(0),
                new Vector<Point>());
        diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

        final TGComponent stopFlow = new AvatarADStopFlow(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
        diagramPanel.addComponent(stopFlow, stopFlow.getX(), stopFlow.getY(), false, true);

        final TGComponent con2 = new AvatarADAssociationConnector(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
                diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
                action.getTGConnectingPointAtIndex(1), stopFlow.getTGConnectingPointAtIndex(0), new Vector<Point>());
        diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

        assertFalse(stopFlow.canBeDisabled());
        assertFalse(con2.canBeDisabled());
    }

    @Test
    public void testDisableAvatarADStopState() {
        final CDElement element = new AvatarADStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

        assertFalse(element.canBeDisabled());
    }
}
