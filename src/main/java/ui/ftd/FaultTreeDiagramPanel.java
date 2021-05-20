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

package ui.ftd;

//import java.awt.*;

import ui.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

//import org.w3c.dom.*;
//import org.xml.sax.*;
//import javax.xml.parsers.*;

/**
 * Class FaultTreeDiagramPanel Panel used for drawing fault trees Creation:
 * 14/12/2017
 * 
 * @version 1.0 14/12/2017
 * @author Ludovic APVRILLE
 */
public class FaultTreeDiagramPanel extends TDiagramPanel implements TDPWithAttributes {

  public FaultTreeDiagramPanel(MainGUI mgui, TToolBar _ttb) {
    super(mgui, _ttb);
  }

  @Override
  public boolean actionOnDoubleClick(TGComponent tgc) {
    return true;
  }

  @Override
  public boolean actionOnAdd(TGComponent tgc) {
    return false;
  }

  @Override
  public boolean actionOnValueChanged(TGComponent tgc) {
    return false;
  }

  @Override
  public boolean actionOnRemove(TGComponent tgc) {
    return false;
  }

  @Override
  public String getXMLHead() {
    return "<FaultTreeDiagramPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >";
  }

  @Override
  public String getXMLTail() {
    return "</FaultTreeDiagramPanel>";
  }

  @Override
  public String getXMLSelectedHead() {
    return "<FaultTreeDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\""
        + widthSel + "\" heightSel=\"" + heightSel + "\" >";
  }

  @Override
  public String getXMLSelectedTail() {
    return "</FaultTreeDiagramPanelCopy>";
  }

  @Override
  public String getXMLCloneHead() {
    return "<FaultTreeDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0
        + "\" heightSel=\"" + 0 + "\" >";
  }

  @Override
  public String getXMLCloneTail() {
    return "</FaultTreeDiagramPanelCopy>";
  }

  public void makeGraphicalOptimizations() {
    // Segments of connector that mask components

    // Components over others

    // Position correctly guards of choice
  }

  public LinkedList<TGComponent> getAllFaults() {
    LinkedList<TGComponent> list = new LinkedList<TGComponent>();
    TGComponent tgc;

    ListIterator iterator = getComponentList().listIterator();

    while (iterator.hasNext()) {
      tgc = (TGComponent) (iterator.next());
      if (tgc instanceof FTDFault) {
        list.add(tgc);
      }
    }

    return list;

  }

  @Override
  public boolean hasAutoConnect() {
    return false;
  }

  public void setConnectorsToFront() {
    TGComponent tgc;

    //

    Iterator iterator = componentList.listIterator();

    ArrayList<TGComponent> list = new ArrayList<TGComponent>();

    while (iterator.hasNext()) {
      tgc = (TGComponent) (iterator.next());
      if (!(tgc instanceof TGConnector)) {
        list.add(tgc);
      }
    }

    //
    for (TGComponent tgc1 : list) {
      //
      componentList.remove(tgc1);
      componentList.add(tgc1);
    }
  }

}
