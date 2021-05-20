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

package ui.ncdd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.Element;

import ui.MainGUI;
import ui.TDPWithAttributes;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TToolBar;

/**
 * Class NCDiagramPanel Panel for drawing an NC diagram Creation: 18/11/2008
 * 
 * @version 1.0 18/11/2008
 * @author Ludovic APVRILLE
 */
public class NCDiagramPanel extends TDiagramPanel implements TDPWithAttributes {

  public NCDiagramPanel(MainGUI mgui, TToolBar _ttb) {
    super(mgui, _ttb);
    /*
     * TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
     * addMouseListener(tdmm); addMouseMotionListener(tdmm);
     */
  }

  @Override
  public boolean actionOnDoubleClick(TGComponent tgc) {
    //
    /*
     * if (tgc instanceof TCDTClass) { TCDTClass t = (TCDTClass)tgc; return
     * mgui.newTClassName(tp, t.oldValue, t.getValue()); } else if (tgc instanceof
     * TCDActivityDiagramBox) { if (tgc.getFather() instanceof TCDTClass) {
     * mgui.selectTab(tp, tgc.getFather().getValue()); } else if (tgc.getFather()
     * instanceof TCDTObject) { TCDTObject to = (TCDTObject)(tgc.getFather());
     * TCDTClass t = to.getMasterTClass(); if (t != null) { mgui.selectTab(tp,
     * t.getValue()); } } return false; // because no change made on any diagram }
     */
    return false;
  }

  @Override
  public boolean actionOnAdd(TGComponent tgc) {
    /*
     * if (tgc instanceof TCDTClass) { TCDTClass tgcc = (TCDTClass)(tgc); //
     * mgui.addTClass(tp, tgcc.getClassName()); return true; }
     */
    return false;
  }

  @Override
  public boolean actionOnRemove(TGComponent tgc) {
    /*
     * if (tgc instanceof TCDTClass) { TCDTClass tgcc = (TCDTClass)(tgc);
     * mgui.removeTClass(tp, tgcc.getClassName()); resetAllInstancesOf(tgcc); return
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
  public String getXMLHead() {
    return "<NCDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + " >";
  }

  @Override
  public String getXMLTail() {
    return "</NCDiagramPanel>";
  }

  @Override
  public String getXMLSelectedHead() {
    return "<NCDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\""
        + widthSel + "\" heightSel=\"" + heightSel + "\" >";
  }

  @Override
  public String getXMLSelectedTail() {
    return "</NCDiagramPanelCopy>";
  }

  @Override
  public String getXMLCloneHead() {
    return "<NCDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0
        + "\" heightSel=\"" + 0 + "\" >";
  }

  @Override
  public String getXMLCloneTail() {
    return "</NCDiagramPanelCopy>";
  }

  public String displayParam() {
    String s = " attributes=\"";
    s += getAttributeState();
    s += "\"";
    return s;
  }

  public void loadExtraParameters(Element elt) {
    String s;
    //
    try {
      s = elt.getAttribute("attributes");
      //
      int attr = Integer.decode(s).intValue();
      setAttributes(attr % 3);
    } catch (Exception e) {
      // Model was saved in an older version of TTool
      //
      setAttributes(0);
    }
  }

  /*
   * public boolean isFree(ArtifactTClassGate atg) { TGConnectorLinkNode tgco;
   * TGComponent tgc; Iterator iterator = componentList.listIterator();
   * 
   * while(iterator.hasNext()) { tgc = (TGComponent)(iterator.next()); if (tgc
   * instanceof TGConnectorLinkNode) { tgco = (TGConnectorLinkNode)tgc; if
   * (tgco.hasArtifactTClassGate(atg)) { return false; } } }
   * 
   * return true; }
   */

  public List<TGComponent> getListOfNodes() {
    List<TGComponent> ll = new LinkedList<>();

    for (TGComponent tgc : componentList) {
      if (tgc instanceof NCEqNode) {
        ll.add(tgc);
      }

      if (tgc instanceof NCSwitchNode) {
        ll.add(tgc);
      }

    }

    return ll;
  }

  public List<NCEqNode> getListOfEqNode() {
    List<NCEqNode> ll = new LinkedList<>();

    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCEqNode) {
        ll.add((NCEqNode) tgc);
      }
    }

    return ll;
  }

  public NCEqNode getNCENodeByName(String _name) {
    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCEqNode) {
        if (((NCEqNode) tgc).getNodeName().equals(_name)) {
          return ((NCEqNode) tgc);
        }
      }
    }

    return null;
  }

  public List<NCSwitchNode> getListOfSwitchNode() {
    List<NCSwitchNode> ll = new LinkedList<>();
    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCSwitchNode) {
        ll.add((NCSwitchNode) tgc);
      }
    }

    return ll;
  }

  public List<NCConnectorNode> getListOfLinks() {
    List<NCConnectorNode> ll = new LinkedList<>();
    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCConnectorNode) {
        ll.add((NCConnectorNode) tgc);
      }
    }

    return ll;
  }

  public List<String> getInterfaces(NCSwitchNode sw) {
    ListIterator<NCConnectorNode> iterator = getListOfLinks().listIterator();
    NCConnectorNode lk;
    TGConnectingPoint p;

    List<String> list = new ArrayList<String>();

    while (iterator.hasNext()) {
      lk = iterator.next();
      p = lk.getTGConnectingPointP1();
      if (sw.belongsToMe(p)) {
        list.add(lk.getInterfaceName());
      } else {
        p = lk.getTGConnectingPointP2();
        if (sw.belongsToMe(p)) {
          list.add(lk.getInterfaceName());
        }
      }
    }

    return list;
  }

  public List<NCRoute> getAllRoutesFor(NCSwitchNode sw, NCTrafficArtifact arti) {
    List<NCRoute> list = sw.getRoutesList();

    List<NCRoute> ret = new ArrayList<NCRoute>();

    for (NCRoute route : list) {
      if (route.traffic.equals(arti.getValue())) {
        ret.add(route);
      }
    }

    return ret;
  }

  public List<NCSwitchNode> getSwitchesOfEq(NCEqNode eq) {
    ListIterator<NCConnectorNode> iterator = getListOfLinks().listIterator();
    NCConnectorNode lk;
    TGConnectingPoint p;

    ArrayList<NCSwitchNode> list = new ArrayList<NCSwitchNode>();

    while (iterator.hasNext()) {
      lk = iterator.next();
      p = lk.getTGConnectingPointP1();
      if (eq.belongsToMe(p)) {
        list.add((NCSwitchNode) (getComponentToWhichBelongs(lk.getTGConnectingPointP2())));
      } else {
        p = lk.getTGConnectingPointP2();
        if (eq.belongsToMe(p)) {
          list.add((NCSwitchNode) (getComponentToWhichBelongs(lk.getTGConnectingPointP1())));
        }
      }
    }

    return list;
  }

  public List<NCConnectorNode> getConnectorOfEq(NCEqNode eq) {
    ListIterator<NCConnectorNode> iterator = getListOfLinks().listIterator();
    NCConnectorNode lk;
    TGConnectingPoint p;

    List<NCConnectorNode> list = new ArrayList<NCConnectorNode>();

    while (iterator.hasNext()) {
      lk = iterator.next();
      p = lk.getTGConnectingPointP1();
      if (eq.belongsToMe(p)) {
        list.add(lk);
      } else {
        p = lk.getTGConnectingPointP2();
        if (eq.belongsToMe(p)) {
          list.add(lk);
        }
      }
    }

    return list;
  }

  public List<NCTrafficArtifact> getTrafficArtifacts() {
    ListIterator<NCEqNode> iterator = getListOfEqNode().listIterator();
    NCEqNode eq;

    List<NCTrafficArtifact> list = new ArrayList<NCTrafficArtifact>();

    while (iterator.hasNext()) {
      eq = iterator.next();
      eq.addAllTrafficArtifacts(list);
    }

    return list;
  }

  public NCEqNode getNCEqNodeOf(NCTrafficArtifact arti) {
    ListIterator<NCEqNode> iterator = getListOfEqNode().listIterator();
    NCEqNode eq;

    while (iterator.hasNext()) {
      eq = iterator.next();
      if (eq.hasTraffic(arti)) {
        return eq;
      }
    }

    return null;

  }

  public List<String> getTraffics() {
    ListIterator<NCEqNode> iterator = getListOfEqNode().listIterator();
    NCEqNode eq;

    List<String> list = new ArrayList<String>();

    while (iterator.hasNext()) {
      eq = iterator.next();
      eq.addAllTraffics(list);
    }

    return list;
  }

  public boolean isALinkBetweenEquipment(NCConnectorNode nccn) {
    TGComponent tgc1 = getComponentToWhichBelongs(nccn.getTGConnectingPointP1());
    TGComponent tgc2 = getComponentToWhichBelongs(nccn.getTGConnectingPointP2());
    return (tgc1 instanceof NCEqNode) && (tgc2 instanceof NCEqNode);

  }

  public NCEqNode getEquipmentByName(String name) {
    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCEqNode) {
        if (tgc.getName().equals(name)) {
          return (NCEqNode) tgc;
        }
      }
    }

    return null;
  }

  public NCSwitchNode getSwitchByName(String name) {
    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCSwitchNode) {
        NCSwitchNode node = (NCSwitchNode) tgc;
        if (node.getNodeName().equals(name)) {
          return node;
        }
      }
    }

    return null;
  }

  public NCConnectorNode getLinkByName(String name) {
    for (TGComponent tgc : this.componentList) {
      if (tgc instanceof NCConnectorNode) {
        NCConnectorNode link = (NCConnectorNode) tgc;
        if (link.getInterfaceName().equals(name)) {
          return link;
        }
      }
    }

    return null;
  }
}
