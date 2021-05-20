/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package ui.tmlcp;

import ui.*;

import java.util.Iterator;
import java.util.Vector;

/**
 * Class TMLCPPanel Panel for drawing a communication pattern Creation:
 * 17/02/2014
 * 
 * @version 1.0 17/02/2014
 * @author Ludovic APVRILLE
 */
public class TMLCPPanel extends TDiagramPanel {

  public TMLCPPanel(MainGUI mgui, TToolBar _ttb) {
    super(mgui, _ttb);
  }

  @Override
  public boolean actionOnDoubleClick(TGComponent tgc) {
    //
    /*
     * if (tgc instanceof TCDTClass) { TCDTClass t = (TCDTClass)tgc; return
     * mgui.newTClassName(t.oldValue, t.getValue()); } else if (tgc instanceof
     * TCDActivityDiagramBox) { if (tgc.getFather() instanceof TCDTClass) {
     * mgui.selectTab(tgc.getFather().getValue()); } else if (tgc.getFather()
     * instanceof TCDTObject) { TCDTObject to = (TCDTObject)(tgc.getFather());
     * TCDTClass t = to.getMasterTClass(); if (t != null) {
     * mgui.selectTab(t.getValue()); } } return false; // because no change made on
     * any diagram }
     */
    return false;
  }

  @Override
  public boolean actionOnAdd(TGComponent tgc) {
    /*
     * if (tgc instanceof TCDTClass) { TCDTClass tgcc = (TCDTClass)(tgc);
     * mgui.addTClass(tgcc.getClassName()); return true; }
     */
    return false;
  }

  @Override
  public boolean actionOnRemove(TGComponent tgc) {
    /*
     * if (tgc instanceof TCDTClass) { TCDTClass tgcc = (TCDTClass)(tgc);
     * mgui.removeTClass(tgcc.getClassName()); resetAllInstancesOf(tgcc); return
     * true; }
     */
    return false;
  }

  @Override
  public boolean actionOnValueChanged(TGComponent tgc) {
    /*
     * if (tgc instanceof TCDTClass) { return actionOnDoubleClick(tgc); }
     */
    return false;
  }

  @Override
  public String getXMLHead() { // Issue #31
    return "<CommunicationPatternDiagramPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >"; // Issue #31
  }

  @Override
  public String getXMLTail() {
    return "</CommunicationPatternDiagramPanel>";
  }

  @Override
  public String getXMLSelectedHead() {
    return "<CommunicationPatternDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel
        + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
  }

  @Override
  public String getXMLSelectedTail() {
    return "</CommunicationPatternDiagramPanelCopy>";
  }

  @Override
  public String getXMLCloneHead() {
    return "<CommunicationPatternDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0
        + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
  }

  @Override
  public String getXMLCloneTail() {
    return "</CommunicationPatternDiagramPanelCopy>";
  }

  /*
   * public TClassInterface getTClass1ToWhichIamConnected(TCDCompositionOperator
   * tcd) { TGConnectorAssociation tgca = getTGConnectorAssociationOf(tcd);
   * TGComponent tgc; if (tgca != null) { tgc =
   * getTopComponentToWhichBelongs(tgca.getTGConnectingPointP1()); if ((tgc !=
   * null) && (tgc instanceof TClassInterface)) { return (TClassInterface) tgc; }
   * } return null; }
   * 
   * public TClassInterface getTClass2ToWhichIamConnected(TCDCompositionOperator
   * tcd) { TGConnectorAssociation tgca = getTGConnectorAssociationOf(tcd);
   * TGComponent tgc; if (tgca != null) { tgc =
   * getTopComponentToWhichBelongs(tgca.getTGConnectingPointP2()); if ((tgc !=
   * null) && (tgc instanceof TClassInterface)) { return (TClassInterface) tgc; }
   * } return null; }
   * 
   * public TGConnectorAssociation
   * getTGConnectorAssociationOf(TCDCompositionOperator tcd) { int i;
   * TGConnectingPoint p1, p2; TGConnector tgco; TGConnectorAttribute tgca;
   * TGComponent tgc;
   * 
   * for(i=0; i<tcd.getNbConnectingPoint(); i++) { p1 =
   * tcd.tgconnectingPointAtIndex(i); tgco = getConnectorConnectedTo(p1); if
   * ((tgco != null) && (tgco instanceof TGConnectorAttribute)){ tgca =
   * (TGConnectorAttribute)tgco; if (p1 == tgca.getTGConnectingPointP1()) { p2 =
   * tgca.getTGConnectingPointP2(); } else { p2 = tgca.getTGConnectingPointP1(); }
   * 
   * // p2 now contains the connecting point of a association tgc =
   * getComponentToWhichBelongs(p2); if ((tgc != null) && (!p2.isFree()) && (tgc
   * instanceof TGConnectorAssociation)) { return (TGConnectorAssociation)tgc; } }
   * } return null; }
   */

  // public void makePostLoadingProcessing() throws MalformedModelingException {
  // TGComponent tgc;

  /*
   * for(int i=0; i<componentList.size(); i++) { tgc =
   * (TGComponent)(componentList.elementAt(i)); if (tgc instanceof TCDTObject) {
   * ((TCDTObject)tgc).postLoadingProcessing(); } }
   */
  // }

  public boolean isTMLCPSDCreated(String name) {
    return mgui.isTMLCPSDCreated(tp, name);
  }

  public boolean isTMLCPCreated(String name) {
    return mgui.isTMLCPCreated(tp, name);
  }

  public boolean openTMLCPSequenceDiagram(String name) {
    return mgui.openTMLCPSequenceDiagram(name);
  }

  public boolean openTMLCPDiagram(String name) {
    return mgui.openTMLCPDiagram(name);
  }

  public boolean createTMLCPSequenceDiagram(String name) {
    boolean b = mgui.createTMLCPSequenceDiagram(tp, name);
    // mgui.changeMade(mgui.getSequenceDiagramPanel(name),
    // TDiagramPanel.NEW_COMPONENT);
    return b;
  }

  public boolean createTMLCPDiagram(String name) {
    boolean b = mgui.createTMLCPDiagram(tp, name);
    // mgui.changeMade(mgui.getSequenceDiagramPanel(name),
    // TDiagramPanel.NEW_COMPONENT);
    return b;
  }

  @Override
  public void enhance() {
    //
    Vector<TGComponent> v = new Vector<>();
    Object o;
    Iterator<TGComponent> iterator = componentList.listIterator();

    while (iterator.hasNext()) {
      o = iterator.next();
      if (o instanceof TMLCPStartState) {
        enhance(v, (TMLCPStartState) o);
      }
    }

    mgui.changeMade(this, MOVE_CONNECTOR);
    repaint();
  }

  public void enhance(Vector<TGComponent> v, TGComponent tgc) {
    TGComponent tgc1;
    TGConnector tgcon;
    int i;

    if (tgc == null) {
      return;
    }

    if (v.contains(tgc)) {
      return;
    }

    v.add(tgc);

    //
    if (!(tgc instanceof TMLCPStartState)) {
      for (i = 0; i < tgc.getNbNext(); i++) {
        tgc1 = getNextTGComponent(tgc, i);
        tgcon = getNextTGConnector(tgc, i);
        if (tgcon.getAutomaticDrawing()) {
          if ((tgc1 != null) && (tgcon != null)) {
            tgcon.alignOrMakeSquareTGComponents();
          }
        }
      }
    }

    // Explore next elements
    for (i = 0; i < tgc.getNbNext(); i++) {
      tgc1 = getNextTGComponent(tgc, i);
      enhance(v, tgc1);
    }
  }

  @Override
  public boolean hasAutoConnect() {
    return true;
  }
}
