/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class TGConnectorLinkNode
 * Connector used in deployment diagram for connecting nodes
 * Creation: 02/05/2005
 * @version 1.0 02/05/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.dd;



import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.cd.*;
import ui.window.*;

public  class TGConnectorLinkNode extends TGConnector {
    protected int arrowLength = 10;
    protected int widthValue, heightValue, maxWidthValue, h;
    
    protected String values [];
    
    protected String delay;
    protected String lossRate;
    
    protected int implementation, oport, dport;
    
    protected VectorLRArtifactTClassGate list;
    
    public final int UDP = 1;
    public final int TCP = 2;
    public final int RMI = 3;
    
    public TGConnectorLinkNode(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "{info}";
        editable = true;
        
        delay = "";
        lossRate = "";
        implementation = 0;
        oport = 0;
        dport = 0;
        
        list = new VectorLRArtifactTClassGate();
        
        makeValue();
        
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        //System.out.println("Double click");
        String oldDelay = delay;
        String oldLossRate = lossRate;
        int oldImplementation = implementation;
        int oldOport = oport;
        int oldDport = dport;
        VectorLRArtifactTClassGate oldlist = new VectorLRArtifactTClassGate(list);
        
        updateListGateNode();
        Vector llist = leftListNotUsed();
        Vector rlist = rightListNotUsed();
        //System.out.println("Vector size: l=" + llist.size() + " r=" + rlist.size());
        //Vector v = tdp.getAllNotSelectedGatesFromNode(list);
        
        JDialogLinkNode jdln = new JDialogLinkNode(frame, delay, lossRate, implementation, oport, dport, llist, rlist, list);
        jdln.setSize(800, 600);
        GraphicLib.centerOnParent(jdln);
        jdln.show(); // blocked until dialog has been closed
        
        delay = jdln.getDelay();
        lossRate = jdln.getLossRate();
        implementation = jdln.getImplementation();
        oport = jdln.getOport();
        dport = jdln.getDport();
        if (jdln.getAssociations() != null) {
            list = new VectorLRArtifactTClassGate(jdln.getAssociations());
        } else {
            list = null;
        }
        
        if ((delay == null) || (jdln.hasCancelled())) {
            delay = oldDelay;
        }
        
        if ((lossRate == null) || (jdln.hasCancelled())){
            lossRate = oldLossRate;
        }
        
        if ((implementation == -1) || (jdln.hasCancelled())){
            implementation = oldImplementation;
        }
        
        if ((oport == -1) || (jdln.hasCancelled())){
            oport = oldOport;
        }
        
        if ((dport == -1) || (jdln.hasCancelled())){
            dport = oldDport;
        }
        
        if ((list == null) || (jdln.hasCancelled())){
            list  = oldlist;
        }
        
        if (!jdln.hasCancelled()) {
            makeValue();
        }
        
        return !jdln.hasCancelled();
    }
    
    public void makeValue() {
        value = "{ ";
        if ((delay != null) && (delay.length() > 0)) {
            value += "delay=" + delay + "\n";
        }
        if ((lossRate != null) && (lossRate.length() > 0)) {
            value += "Loss Rate=" + lossRate + "\n";
        }
        
        if (implementation != 0 ){
            switch(implementation) {
                case 1:
                    value += "Implementation = UDP\n";
                    break;
                case 2:
                    value += "Implementation = TCP\n";
                    break;
                case 3:
                    value += "Implementation = RMI\n";
                    break;
            }
        }
        
        if ((implementation > 0) && (implementation < 3)) {
            value += "Origin port = " + oport + "\n";
            value += "Destination port = " + dport + "\n";
        }
        
        value += list.toString();
        
        if (value.indexOf('\n') > -1)
            value = value.substring(0, value.length()-1);
        value += " }";
        values = Conversion.wrapText(value);
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }
        
        if (values == null) {
            makeValue();
        }
        
        h  = g.getFontMetrics().getHeight();
        if (!tdp.isScaled()) {
            maxWidthValue = 0;
            heightValue = h * (values.length);
        }
        
        for (int i = 0; i<values.length; i++) {
            widthValue = g.getFontMetrics().stringWidth(values[i]);
            if (!tdp.isScaled()) {
                maxWidthValue = Math.max(maxWidthValue, widthValue);
            }
            g.drawString(values[i], ((p1.getX() + p2.getX()) / 2)-widthValue/2, ((p1.getY() + p2.getY()) / 2) - 11 - (values.length*h/2) + ((i+1)* h));
        }
    }
    
    
    public int getType() {
        return TGComponentManager.CONNECTOR_NODE_DD;
    }
    
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {
        //System.out.println("Extra");
        if (GraphicLib.isInRectangle(x1, y1, ((p1.getX() + p2.getX()) / 2)-maxWidthValue/2, ((p1.getY() + p2.getY()) / 2) - 11 - (values.length*h)/2, maxWidthValue, heightValue)) {
            return this;
        }
        return null;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info delay=\"" + delay + "\" lossRate=\"" + lossRate + "\" implementation=\"" + implementation + "\" oport=\"" + oport + "\" dport=\"" + dport);
        sb.append("\" />\n");
        
        if (list.size() > 0) {
            LRArtifactTClassGate lratg;
            for(int i=0; i<list.size(); i++) {
                lratg = list.getElementAt(i);
                sb.append("<infogate latg_art=\"" + lratg.left.art + "\" latg_tcl=\"" + lratg.left.tcl + "\" latg_gat=\"" + lratg.left.gat);
                sb.append("\" ratg_art=\"" + lratg.right.art + "\" ratg_tcl=\"" + lratg.right.tcl + "\" ratg_gat=\"" + lratg.right.gat);
                sb.append("\" />\n");
            }
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
            String sdelay = null, slossRate = null;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sdelay = elt.getAttribute("delay");
                                slossRate = elt.getAttribute("lossRate");
                                try {
                                    implementation = Integer.decode(elt.getAttribute("implementation")).intValue();
                                } catch (Exception e) {
                                    implementation = 0;
                                }
                                try {
                                    oport = Integer.decode(elt.getAttribute("oport")).intValue();
                                } catch (Exception e) {
                                    oport = 0;
                                }
                                try {
                                    dport = Integer.decode(elt.getAttribute("dport")).intValue();
                                } catch (Exception e) {
                                    dport = 0;
                                }
                            }
                            if (sdelay != null) {
                                delay = sdelay;
                            }
                            if (slossRate != null){
                                lossRate = slossRate;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
        loadExtraParamGate(nl, decX, decY, decId);
        makeValue();
    }
    
    public  VectorLRArtifactTClassGate getList() {
        return list;
    }
    
    // Remove gates no more declared
    protected void updateListGateNode() {
        LRArtifactTClassGate lratg;
        
        for(int i=0; i<list.size(); i++) {
            lratg = list.getElementAt(i);
            
            if ((!exist(lratg.left)) || (!exist(lratg.right))) {
                list.removeElementAt(i);
                i --;
            }
        }
    }
    
    public boolean exist(ArtifactTClassGate atg) {
        DesignPanel dp = tdp.getGUI().getDesignPanel(atg.art);
        if (dp == null) {
            return false;
        }
        
        TCDTClass tc = dp.getTCDTClass(atg.tcl);
        
        if (tc == null) {
            return false;
        }
        
        TAttribute ta = tc.getGateById(atg.gat);
        
        return (atg.gat != null);
    }
    
    public boolean free(ArtifactTClassGate atg) {
        if (!exist(atg)) {
            return false;
        }
        
        if (tdp instanceof TDeploymentDiagramPanel) {
            return ((TDeploymentDiagramPanel)(tdp)).isFree(atg);
        } else {
            return false;
        }
        
        
    }
    
    public boolean hasArtifactTClassGate(ArtifactTClassGate atg) {
        return list.isInList(atg);
    }
    
    public TDDNode getOriginNode() {
        TGComponent tgc = tdp.getComponentToWhichBelongs(getTGConnectingPointP1());
        if (tgc instanceof TDDNode) {
            return (TDDNode)tgc;
        } else {
            return null;
        }
    }
    
    public TDDNode getDestinationNode() {
        TGComponent tgc = tdp.getComponentToWhichBelongs(getTGConnectingPointP2());
        if (tgc instanceof TDDNode) {
            return (TDDNode)tgc;
        } else {
            return null;
        }
    }
    
    // left means origin of the link
    public Vector leftListNotUsed() {
        if (p1 == null){
            return new Vector();
        }
        
        TGComponent tgc = tdp.getTopComponentToWhichBelongs(p1);
        
        return listNotUsed(tgc);
    }
    
    public Vector rightListNotUsed() {
        if (p2 == null){
            return new Vector();
        }
        
        TGComponent tgc = tdp.getTopComponentToWhichBelongs(p2);
        
        return listNotUsed(tgc);
    }
    
    public Vector listNotUsed(TGComponent tgc) {
        //System.out.println("List not used, component=" + tgc.getName());
        Vector v = new Vector();
        if (tgc == null) {
            return v;
        }
        
        // Checks if it is a node
        if (!(tgc instanceof TDDNode)) {
            return v;
        }
        
        TDDNode tdd = (TDDNode)tgc;
        
        // list all artifacts of the node
        Vector listArtifacts = tdd.getArtifactList();
        
        //System.out.println("list artifact size=" + listArtifacts.size());
        
        // For each artifact, we check whether it is free or not -> if yes, it is added to the list
        TDDArtifact tart;
        ArtifactTClassGate atg;
        Vector listAtg;
        for(int i=0; i<listArtifacts.size(); i++) {
            tart = (TDDArtifact)(listArtifacts.elementAt(i));
            listAtg = tart.getListOfATG();
            //System.out.println("artifact=" + tart.getValue() + " nb of gates =" + listAtg.size());
            for(int j=0; j<listAtg.size(); j++) {
                atg = (ArtifactTClassGate)(listAtg.elementAt(j));
                //if (tdp.isFree(atg)) {
                    v.add(atg);
                //}
            }
            
        }
        
        return v;
    }
    
    public void loadExtraParamGate(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
            String latg_art = null, latg_tcl = null, latg_gat = null;
            String ratg_art = null, ratg_tcl = null, ratg_gat = null;
            ArtifactTClassGate atg1, atg2;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("infogate")) {
                                latg_art = elt.getAttribute("latg_art");
                                latg_tcl = elt.getAttribute("latg_tcl");
                                latg_gat = elt.getAttribute("latg_gat");
                                ratg_art = elt.getAttribute("ratg_art");
                                ratg_tcl = elt.getAttribute("ratg_tcl");
                                ratg_gat = elt.getAttribute("ratg_gat");
                            }
                            if ((latg_art != null) && (latg_tcl != null) && (latg_gat != null) && (ratg_art != null) && (ratg_tcl != null) && (ratg_gat != null)) {
                                atg1 = new ArtifactTClassGate(latg_art, latg_tcl, latg_gat);
                                atg2 = new ArtifactTClassGate(ratg_art, ratg_tcl, ratg_gat);
                                list.add(new LRArtifactTClassGate(atg1, atg2));
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
    public String getDelay() {
        return delay;
    }
    
    public int getImplementation() {
        return implementation;
    }
    
    public int getOriginPort() {
        return oport;
    }
    
    public int getDestinationPort() {
        return dport;
    }
}
