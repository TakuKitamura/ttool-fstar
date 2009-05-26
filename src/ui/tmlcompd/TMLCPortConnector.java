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
 * Class TMLCPortConnector
 * Connector used in TML Component task diagrams
 * Creation: 12/03/2008
 * @version 1.0 12/03/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcompd;


import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.cd.*;
import ui.window.*;

public  class TMLCPortConnector extends TGConnector implements ScalableTGComponent, SpecificActionAfterAdd,  SpecificActionAfterMove{
    //protected int arrowLength = 10;
    //protected int widthValue, heightValue, maxWidthValue, h;
	
	//protected int priority = 0; // Between 0 and 10
	
	    //protected double dx, dy;
		protected double oldScaleFactor;
		//protected TGConnectingPoint oldp1, oldp2;
		
    
    public TMLCPortConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "Connector between ports";
        editable = false;
		
		oldScaleFactor = tdp.getZoom();
		
		//oldp1 = null;
		//oldp2 = null;
		
		//System.out.println("Action on add from connector");
		//actionOnAdd();
		//System.out.println("Action on add done from connector");
    }
    
    /*public boolean editOndoubleClick(JFrame frame) {
        JDialogTMLConnectorNode dialog = new JDialogTMLConnectorNode(frame, "Setting connector attributes", this);
		dialog.setSize(350, 300);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		priority = dialog.getPriority();
			
		return true;
    }*/
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        //if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
			/*if ((p1 != oldp1) || (p2 != oldp2)) {
				if (tdp instanceof TMLComponentTaskDiagramPanel) {
					((TMLComponentTaskDiagramPanel)tdp).updateConnectors(this, oldp1, oldp2, p1, p2);
				}
			}*/
			
			try {
			TMLCPortConnectingPoint pt1 = (TMLCPortConnectingPoint)p1;
			TMLCPortConnectingPoint pt2 = (TMLCPortConnectingPoint)p2;
		
			
			if (!pt1.positionned) {
				pt1.positionned = true;
				if (pt1.getFather() instanceof TMLCPrimitivePort) {
					pt1.port = (TMLCPrimitivePort)(pt1.getFather());   
				}
			}
			
			if (!pt2.positionned) {
				pt2.positionned = true;
				if (pt2.getFather() instanceof TMLCPrimitivePort) {
					pt2.port = (TMLCPrimitivePort)(pt2.getFather());   
				}
			}
			
			if ((pt1.port != null) && (pt2.port != null)) {
				String name1 = pt1.port.getPortName();
				String name2 = pt2.port.getPortName();
				if (name1.equals(name2)) {
					int w = g.getFontMetrics().stringWidth(name1);
					Font fold = g.getFont();
					Font f = fold.deriveFont(Font.ITALIC, (float)(tdp.getFontSize()));
					g.setFont(f);
					g.drawString(name1, (x1 + x2 - w)/2, (y1 + y2)/2);
					g.setFont(fold);
				}
			}
			
            g.drawLine(x1, y1, x2, y2);
			return;
			} catch (Exception e) {
				//System.out.println("Exception " + e.getMessage());
			}
			g.drawLine(x1, y1, x2, y2);
			
			
        //} else {
        //    GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        //}
    }
	
	public void rescale(double scaleFactor){
		//System.out.println("Rescale connector");
		int xx, yy;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			xx = tgcomponent[i].getX();
			yy = tgcomponent[i].getY();
			//System.out.println("Internal comp xx= " + xx + "  y==" + yy);
			tgcomponent[i].dx = (tgcomponent[i].dx + xx) / oldScaleFactor * scaleFactor;
			tgcomponent[i].dy = (tgcomponent[i].dy + yy) / oldScaleFactor * scaleFactor;
			xx = (int)(tgcomponent[i].dx);
			tgcomponent[i].dx = tgcomponent[i].dx - xx;
			yy = (int)(tgcomponent[i].dy);
			tgcomponent[i].dy = tgcomponent[i].dy - yy;
			
			tgcomponent[i].setCd(xx, yy);
			
			//System.out.println("Internal comp xx= " + xx + "  y==" + yy);
        }
		
		oldScaleFactor = scaleFactor;
	}
    
    
    public int getType() {
        return TGComponentManager.CONNECTOR_PORT_TMLC;
    }
	
	public void specificActionAfterAdd() {
		//System.out.println("Specific action after add");
		((TMLComponentTaskDiagramPanel)tdp).updatePorts();
	}
	
	public void specificActionAfterMove() {
		//System.out.println("Specific action after move");
		((TMLComponentTaskDiagramPanel)tdp).updatePorts();
	}
	
	 /*public void structureChanged() {
		 System.out.println("Structure changed");
		 ((TMLComponentTaskDiagramPanel)tdp).updatePorts();
	 }*/
	
	/*protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info priority=\"");
		sb.append(priority);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }*/
	
	/*public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
			String prio;
            
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
								prio = elt.getAttribute("priority");
								if (elt != null) {
									priority = Integer.decode(prio).intValue();
								}
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }*/
	
    
    /*public TMLArchiCPUNode getOriginNode() {
        TGComponent tgc = tdp.getComponentToWhichBelongs(getTGConnectingPointP1());
        if (tgc instanceof TMLArchiCPUNode) {
            return (TMLArchiCPUNode)tgc;
        } else {
            return null;
        }
    }
    
    public TMLArchiCPUNode getDestinationNode() {
        TGComponent tgc = tdp.getComponentToWhichBelongs(getTGConnectingPointP2());
        if (tgc instanceof TMLArchiCPUNode) {
            return (TMLArchiCPUNode)tgc;
        } else {
            return null;
        }
    }
	
	public int getPriority() {
		return priority;
	}
    
	public String getAttributes() {
		return "Priority = " + priority;
	}*/
    
}
