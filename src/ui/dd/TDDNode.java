/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class TDDNode
 * Node. To be used in deployment diagrams.
 * Creation: 02/05/2005
 * @version 1.0 02/05/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.dd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class TDDNode extends TGCWithInternalComponent implements SwallowTGComponent {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 20;
    private int derivationy = 30;
    private String stereotype = "stereotype";
    private String nodeName = "MyNode";
    
    public TDDNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 200;
        minWidth = 150;
        minHeight = 150;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new TGConnectingPointDD(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new TGConnectingPointDD(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new TGConnectingPointDD(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new TGConnectingPointDD(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new TGConnectingPointDD(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new TGConnectingPointDD(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new TGConnectingPointDD(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new TGConnectingPointDD(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new TGConnectingPointDD(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new TGConnectingPointDD(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new TGConnectingPointDD(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new TGConnectingPointDD(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new TGConnectingPointDD(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new TGConnectingPointDD(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new TGConnectingPointDD(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new TGConnectingPointDD(this, 0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = "Node name";
        
        myImageIcon = IconManager.imgic700;
    }
    
    public void internalDrawing(Graphics g) {
        g.drawRect(x, y, width, height);
        
        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);
        
        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);
        
        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        g.drawString(ster, x + (width - w)/2, y + textY1);
        w  = g.getFontMetrics().stringWidth(nodeName);
        g.drawString(nodeName, x + (width - w)/2, y + textY2);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
            return this;
        }
        
        return null;
    }
    
    public String getStereotype() {
        return stereotype;
        
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldSte = getStereotype();
        String oldName = getNodeName();
        String[] array = new String[2];
        array[0] = getStereotype(); array[1] = getNodeName();
        
        JDialogTimeInterval jdti = new JDialogTimeInterval(frame, array, "Setting stereotype and name", "Stereotype", "Node's identifier");
        jdti.setSize(350, 250);
        GraphicLib.centerOnParent(jdti);
        jdti.show(); // blocked until dialog has been closed
        
        stereotype = array[0].trim(); nodeName = array[1].trim();
        
        if ((stereotype != null) && (nodeName != null)){
            if (!TAttribute.isAValidId(nodeName, false, false)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the node: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                nodeName = oldName;
                return false;
            }
            //makeValue();
            return true;
        }
        stereotype = oldSte;
        nodeName = oldName;
        return false;
    }
    
    
    public int getType() {
        return TGComponentManager.TDD_NODE;
    }
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof TDDArtifact) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((TDDArtifact)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
        
        // else unknown*/
        
        //add it
        addInternalComponent(tgc, 0);
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }
    
    
    public Vector getArtifactList() {
        Vector v = new Vector();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TDDArtifact) {
                v.add(tgcomponent[i]);
            }
        }
        return v;
    }
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TDDArtifact) {
                ((TDDArtifact)tgcomponent[i]).resizeWithFather();
            }
        }
        
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + nodeName);
        sb.append("\" />\n");
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
            String sstereotype = null, snodeName = null;
            
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
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            } 
                            if (snodeName != null){
                                nodeName = snodeName;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
   	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_DD;
      }
    
}
