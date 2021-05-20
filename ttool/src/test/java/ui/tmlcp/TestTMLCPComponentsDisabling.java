package ui.tmlcp;

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

public class TestTMLCPComponentsDisabling extends AbstractUITest {

  private TMLArchiDiagramPanel diagramPanel;

  public TestTMLCPComponentsDisabling() {
    super();
  }

  @Before
  public void setUp() {
    final TMLArchiDiagramToolBar toolBar = new TMLArchiDiagramToolBar(mainGUI);
    diagramPanel = new TMLArchiDiagramPanel(mainGUI, toolBar);
  }

  @Test
  public void testDisableTGConnectingPointTMLCP() {
    final CDElement element = new TGConnectingPointTMLCP(null, 0, -5, true, false, 0.5, 0.0);

    assertFalse(element.canBeDisabled());
  }

  @Test
  public void testDisableTGConnectorTMLCP() {
    final TGComponent startElement = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);

    final TGComponent refElement1 = new TMLCPRefAD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(refElement1, refElement1.getX(), refElement1.getY(), false, true);

    final TGComponent con1 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        startElement.getTGConnectingPointAtIndex(0), refElement1.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

    final TGComponent refElement2 = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(refElement2, refElement2.getX(), refElement2.getY(), false, true);

    final TGComponent con2 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        refElement1.getTGConnectingPointAtIndex(1), refElement2.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

    final TGComponent stopElement = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(stopElement, stopElement.getX(), stopElement.getY(), false, true);

    final TGComponent con3 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        refElement2.getTGConnectingPointAtIndex(1), stopElement.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

    assertTrue(con1.canBeDisabled());
    assertTrue(con2.canBeDisabled());
    assertFalse(con3.canBeDisabled());

    con1.setEnabled(false);
    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertFalse(refElement1.isEnabled());
    assertTrue(refElement1.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertFalse(refElement2.isEnabled());
    assertTrue(refElement2.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertTrue(stopElement.isEnabled());
    assertFalse(stopElement.canBeDisabled());

    con1.setEnabled(true);
    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertTrue(refElement1.isEnabled());
    assertTrue(refElement1.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertTrue(refElement2.isEnabled());
    assertTrue(refElement2.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertTrue(stopElement.isEnabled());

    con2.setEnabled(false);
    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertTrue(refElement1.isEnabled());
    assertTrue(refElement1.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertFalse(refElement2.isEnabled());
    assertTrue(refElement2.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertTrue(stopElement.isEnabled());
    assertFalse(stopElement.canBeDisabled());

    con2.setEnabled(true);
    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertTrue(refElement1.isEnabled());
    assertTrue(refElement1.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertTrue(refElement2.isEnabled());
    assertTrue(refElement2.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertTrue(stopElement.isEnabled());
    assertFalse(stopElement.canBeDisabled());
  }

  @Test
  public void testDisableTMLADChoice() {
    final TMLCPChoice element = new TMLCPChoice(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

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

    element.setGuard(TMLCPChoice.EMPTY_GUARD_TEXT, 0);
    guard = element.getInternalTGComponent(0);
    assertFalse(guard.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPFork() {
    final TGComponent startElement = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);

    final TMLCPFork fork = new TMLCPFork(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(fork, fork.getX(), fork.getY(), false, true);

    final TGComponent con1 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        startElement.getTGConnectingPointAtIndex(0), fork.getEnterConnectingPoint(), new Vector<Point>());
    diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

    final TGComponent elementBranch1 = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranch1, elementBranch1.getX(), elementBranch1.getY(), false, true);

    final TGComponent con2 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        fork.getExitConnectingPoints().get(0), elementBranch1.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

    final TGComponent stopElementBranch1 = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(stopElementBranch1, stopElementBranch1.getX(), stopElementBranch1.getY(), false, true);

    final TGComponent con3 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch1.getTGConnectingPointAtIndex(1), stopElementBranch1.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

    final TGComponent elementBranch2 = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranch2, elementBranch2.getX(), elementBranch2.getY(), false, true);

    final TGComponent con4 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        fork.getExitConnectingPoints().get(1), elementBranch2.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con4, con4.getX(), con4.getY(), false, true);

    final TMLCPJoin elementJoinBranch23 = new TMLCPJoin(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementJoinBranch23, elementJoinBranch23.getX(), elementJoinBranch23.getY(), false, true);

    final TGComponent con5 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch2.getTGConnectingPointAtIndex(1), elementJoinBranch23.getEnterConnectingPoints().get(0),
        new Vector<Point>());
    diagramPanel.addComponent(con5, con5.getX(), con5.getY(), false, true);

    final TGComponent elementBranch3 = new TMLCPRefAD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranch3, elementBranch3.getX(), elementBranch3.getY(), false, true);

    final TGComponent con6 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        fork.getExitConnectingPoints().get(2), elementBranch3.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con6, con6.getX(), con6.getY(), false, true);

    final TGComponent con7 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch3.getTGConnectingPointAtIndex(1), elementJoinBranch23.getEnterConnectingPoints().get(1),
        new Vector<Point>());
    diagramPanel.addComponent(con7, con7.getX(), con7.getY(), false, true);

    final TGComponent stopElementJoin = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(stopElementJoin, stopElementJoin.getX(), stopElementJoin.getY(), false, true);

    final TGComponent con8 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementJoinBranch23.getExitConnectingPoint(), stopElementJoin.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con8, con8.getX(), con8.getY(), false, true);

    assertTrue(fork.canBeDisabled());

    fork.setEnabled(false);

    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertFalse(fork.isEnabled());
    assertTrue(fork.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertFalse(elementBranch1.isEnabled());
    assertTrue(elementBranch1.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertTrue(stopElementBranch1.isEnabled());
    assertFalse(stopElementBranch1.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertFalse(elementBranch2.isEnabled());
    assertTrue(elementBranch2.canBeDisabled());
    assertTrue(con4.isEnabled());
    assertTrue(con4.canBeDisabled());
    assertTrue(elementJoinBranch23.isEnabled());
    assertFalse(elementJoinBranch23.canBeDisabled());
    assertTrue(con5.isEnabled());
    assertTrue(con5.canBeDisabled());
    assertFalse(elementBranch3.isEnabled());
    assertTrue(elementBranch3.canBeDisabled());
    assertTrue(con6.isEnabled());
    assertTrue(con6.canBeDisabled());
    assertTrue(con7.isEnabled());
    assertTrue(con7.canBeDisabled());
    assertTrue(stopElementJoin.isEnabled());
    assertFalse(stopElementJoin.canBeDisabled());
    assertTrue(con8.isEnabled());
    assertFalse(con8.canBeDisabled());

    fork.setEnabled(true);

    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertTrue(fork.isEnabled());
    assertTrue(fork.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertTrue(elementBranch1.isEnabled());
    assertTrue(elementBranch1.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertTrue(stopElementBranch1.isEnabled());
    assertFalse(stopElementBranch1.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertTrue(elementBranch2.isEnabled());
    assertTrue(elementBranch2.canBeDisabled());
    assertTrue(con4.isEnabled());
    assertTrue(con4.canBeDisabled());
    assertTrue(elementJoinBranch23.isEnabled());
    assertFalse(elementJoinBranch23.canBeDisabled());
    assertTrue(con5.isEnabled());
    assertTrue(con5.canBeDisabled());
    assertTrue(elementBranch3.isEnabled());
    assertTrue(elementBranch3.canBeDisabled());
    assertTrue(con6.isEnabled());
    assertTrue(con6.canBeDisabled());
    assertTrue(con7.isEnabled());
    assertTrue(con7.canBeDisabled());
    assertTrue(stopElementJoin.isEnabled());
    assertFalse(stopElementJoin.canBeDisabled());
    assertTrue(con8.isEnabled());
    assertFalse(con8.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPForLoop() {
    final TGComponent startElement = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(startElement, startElement.getX(), startElement.getY(), false, true);

    final TADForLoop forLoop = new TMLCPForLoop(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(forLoop, forLoop.getX(), forLoop.getY(), false, true);

    final TGComponent con1 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        startElement.getTGConnectingPointAtIndex(0), forLoop.getEnterLoopConnectingPoint(), new Vector<Point>());
    diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

    final TGComponent elementInLoop = new TMLCPRefAD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementInLoop, elementInLoop.getX(), elementInLoop.getY(), false, true);

    final TGComponent con2 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        forLoop.getInsideLoopConnectingPoint(), elementInLoop.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

    final TGComponent stopElementInLoop = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(stopElementInLoop, stopElementInLoop.getX(), stopElementInLoop.getY(), false, true);

    final TGComponent con3 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementInLoop.getTGConnectingPointAtIndex(1), stopElementInLoop.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

    final TGComponent elementExitLoop = new TMLCPRefAD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementExitLoop, elementExitLoop.getX(), elementExitLoop.getY(), false, true);

    final TGComponent con4 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel, forLoop.getExitLoopConnectingPoint(),
        elementExitLoop.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con4, con4.getX(), con4.getY(), false, true);

    final TGComponent stopElementExitLoop = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(stopElementExitLoop, stopElementExitLoop.getX(), stopElementExitLoop.getY(), false, true);

    final TGComponent con5 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementExitLoop.getTGConnectingPointAtIndex(1), stopElementExitLoop.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con5, con5.getX(), con5.getY(), false, true);

    assertTrue(forLoop.canBeDisabled());

    forLoop.setEnabled(false);
    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertFalse(forLoop.isEnabled());
    assertTrue(forLoop.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertFalse(elementInLoop.isEnabled());
    assertFalse(elementInLoop.canBeDisabled());
    assertFalse(con2.isEnabled());
    assertFalse(con2.canBeDisabled());
    assertFalse(stopElementInLoop.isEnabled());
    assertFalse(stopElementInLoop.canBeDisabled());
    assertFalse(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertTrue(elementExitLoop.isEnabled());
    assertTrue(elementExitLoop.canBeDisabled());
    assertTrue(con4.isEnabled());
    assertTrue(con4.canBeDisabled());
    assertTrue(stopElementExitLoop.isEnabled());
    assertFalse(stopElementExitLoop.canBeDisabled());
    assertTrue(con5.isEnabled());
    assertFalse(con5.canBeDisabled());

    forLoop.setEnabled(true);
    assertTrue(startElement.isEnabled());
    assertFalse(startElement.canBeDisabled());
    assertTrue(forLoop.isEnabled());
    assertTrue(forLoop.canBeDisabled());
    assertTrue(con1.isEnabled());
    assertTrue(con1.canBeDisabled());
    assertTrue(elementInLoop.isEnabled());
    assertTrue(elementInLoop.canBeDisabled());
    assertTrue(con2.isEnabled());
    assertTrue(con2.canBeDisabled());
    assertTrue(stopElementInLoop.isEnabled());
    assertFalse(stopElementInLoop.canBeDisabled());
    assertTrue(con3.isEnabled());
    assertFalse(con3.canBeDisabled());
    assertTrue(elementExitLoop.isEnabled());
    assertTrue(elementExitLoop.canBeDisabled());
    assertTrue(con4.isEnabled());
    assertTrue(con4.canBeDisabled());
    assertTrue(stopElementExitLoop.isEnabled());
    assertFalse(stopElementExitLoop.canBeDisabled());
    assertTrue(con5.isEnabled());
    assertFalse(con5.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPJoin() {
    final TGComponent startElementBranch1 = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(startElementBranch1, startElementBranch1.getX(), startElementBranch1.getY(), false, true);

    final TGComponent elementBranch1 = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranch1, elementBranch1.getX(), elementBranch1.getY(), false, true);

    final TGComponent con1 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        startElementBranch1.getTGConnectingPointAtIndex(0), elementBranch1.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con1, con1.getX(), con1.getY(), false, true);

    final TMLCPJoin join = new TMLCPJoin(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(join, join.getX(), join.getY(), false, true);

    final TGComponent con2 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch1.getTGConnectingPointAtIndex(1), join.getEnterConnectingPoints().get(0), new Vector<Point>());
    diagramPanel.addComponent(con2, con2.getX(), con2.getY(), false, true);

    final TGComponent startElementBranch2 = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(startElementBranch2, startElementBranch2.getX(), startElementBranch2.getY(), false, true);

    final TGComponent elementBranch2 = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranch2, elementBranch2.getX(), elementBranch2.getY(), false, true);

    final TGComponent con3 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        startElementBranch2.getTGConnectingPointAtIndex(0), elementBranch2.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con3, con3.getX(), con3.getY(), false, true);

    final TGComponent con4 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch2.getTGConnectingPointAtIndex(1), join.getEnterConnectingPoints().get(1), new Vector<Point>());
    diagramPanel.addComponent(con4, con4.getX(), con4.getY(), false, true);

    final TGComponent startElementBranch3 = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(startElementBranch3, startElementBranch3.getX(), startElementBranch3.getY(), false, true);

    final TGComponent elementBranch3 = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranch3, elementBranch3.getX(), elementBranch3.getY(), false, true);

    final TGComponent con5 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        startElementBranch3.getTGConnectingPointAtIndex(0), elementBranch3.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con5, con5.getX(), con5.getY(), false, true);

    final TGComponent con6 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch3.getTGConnectingPointAtIndex(1), join.getEnterConnectingPoints().get(2), new Vector<Point>());
    diagramPanel.addComponent(con6, con6.getX(), con6.getY(), false, true);

    final TGComponent elementBranchExit = new TMLCPRefAD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(elementBranchExit, elementBranchExit.getX(), elementBranchExit.getY(), false, true);

    final TGComponent con7 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel, join.getExitConnectingPoint(),
        elementBranch3.getTGConnectingPointAtIndex(0), new Vector<Point>());
    diagramPanel.addComponent(con7, con7.getX(), con7.getY(), false, true);

    final TGComponent stopElementBranchExit = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);
    diagramPanel.addComponent(stopElementBranchExit, stopElementBranchExit.getX(), stopElementBranchExit.getY(), false,
        true);

    final TGComponent con8 = new TGConnectorTMLCP(0, 0, diagramPanel.getMinX(), diagramPanel.getMaxX(),
        diagramPanel.getMinY(), diagramPanel.getMaxY(), false, null, diagramPanel,
        elementBranch3.getTGConnectingPointAtIndex(1), stopElementBranchExit.getTGConnectingPointAtIndex(0),
        new Vector<Point>());
    diagramPanel.addComponent(con8, con8.getX(), con8.getY(), false, true);

    assertTrue(join.isEnabled());
    assertFalse(join.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPRefAD() {
    final CDElement element = new TMLCPRefAD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

    assertTrue(element.canBeDisabled());

    element.setEnabled(false);

    assertFalse(element.isEnabled());
    assertTrue(element.canBeDisabled());

    element.setEnabled(true);

    assertTrue(element.isEnabled());
    assertTrue(element.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPRefSD() {
    final CDElement element = new TMLCPRefSD(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

    assertTrue(element.canBeDisabled());

    element.setEnabled(false);

    assertFalse(element.isEnabled());
    assertTrue(element.canBeDisabled());

    element.setEnabled(true);

    assertTrue(element.isEnabled());
    assertTrue(element.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPStartState() {
    final CDElement element = new TMLCPStartState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

    assertFalse(element.canBeDisabled());
  }

  @Test
  public void testDisableTMLCPStopState() {
    final CDElement element = new TMLCPStopState(500, 500, 0, 1400, 0, 1900, true, null, diagramPanel);

    assertFalse(element.canBeDisabled());
  }
}
