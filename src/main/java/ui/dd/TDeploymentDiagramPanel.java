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

package ui.dd;

import ui.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class TDeploymentDiagramPanel Panel for drawing a deployment diagram
 * Creation: 29/04/2005
 * 
 * @version 1.0 29/04/2005
 * @author Ludovic APVRILLE
 */
public class TDeploymentDiagramPanel extends TDiagramPanel {

    public TDeploymentDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        /*
         * TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
         * addMouseListener(tdmm); addMouseMotionListener(tdmm);
         */
    }

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

    public boolean actionOnAdd(TGComponent tgc) {
        /*
         * if (tgc instanceof TCDTClass) { TCDTClass tgcc = (TCDTClass)(tgc); //
         * mgui.addTClass(tp, tgcc.getClassName()); return true; }
         */
        return false;
    }

    public boolean actionOnRemove(TGComponent tgc) {
        /*
         * if (tgc instanceof TCDTClass) { TCDTClass tgcc = (TCDTClass)(tgc);
         * mgui.removeTClass(tp, tgcc.getClassName()); resetAllInstancesOf(tgcc); return
         * true; }
         */
        return false;
    }

    public boolean actionOnValueChanged(TGComponent tgc) {
        /*
         * if (tgc instanceof TCDTClass) { return actionOnDoubleClick(tgc); }
         */
        return false;
    }

    public String getXMLHead() {
        return "<TDeploymentDiagramPanel name=\"" + name + "\"" + sizeParam() + " >";
    }

    public String getXMLTail() {
        return "</TDeploymentDiagramPanel>";
    }

    public String getXMLSelectedHead() {
        return "<TDeploymentDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel
                + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    public String getXMLSelectedTail() {
        return "</TDeploymentDiagramPanelCopy>";
    }

    public String getXMLCloneHead() {
        return "<TDeploymentDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\""
                + 0 + "\" heightSel=\"" + 0 + "\" >";
    }

    public String getXMLCloneTail() {
        return "</TDeploymentDiagramPanelCopy>";
    }

    public boolean isFree(ArtifactTClassGate atg) {
        TGConnectorLinkNode tgco;
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TGConnectorLinkNode) {
                tgco = (TGConnectorLinkNode) tgc;
                if (tgco.hasArtifactTClassGate(atg)) {
                    return false;
                }
            }
        }

        return true;
    }

    public List<TDDNode> getListOfNodes() {
        List<TDDNode> ll = new LinkedList<TDDNode>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TDDNode) {
                ll.add((TDDNode) tgc);
            }
        }

        return ll;
    }

    public List<TGConnectorLinkNode> getListOfLinks() {
        List<TGConnectorLinkNode> ll = new LinkedList<TGConnectorLinkNode>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TGConnectorLinkNode) {
                ll.add((TGConnectorLinkNode) tgc);
            }
        }

        return ll;
    }

}