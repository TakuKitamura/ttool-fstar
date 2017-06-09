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
 * Class TMLArchiConnectorNode
 * Connector used in TML Architecture diagrams
 * Creation: 02/05/2005
 * @version 1.0 02/05/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmldd;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogTMLConnectorNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public  class TMLArchiConnectorNode extends TGConnector implements WithAttributes {
    protected int arrowLength = 10;
    protected int widthValue, heightValue, maxWidthValue, h;
    public static final String NO_SPY = "Remove spy";
    public static final String ADD_SPY = "Add spy";
    protected boolean hasASpy;
    protected int priority = 0; // Between 0 and 10
    
    public TMLArchiConnectorNode(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "{info}";
        editable = true;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        JDialogTMLConnectorNode dialog = new JDialogTMLConnectorNode(frame, "Setting connector attributes", this);
		//dialog.setSize(350, 300);
        GraphicLib.centerOnParent(dialog, 350, 300 );
        dialog.setVisible( true ); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		priority = dialog.getPriority();
			
		return true;
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
	  if (hasASpy) {
	      g.drawImage(IconManager.img5200, (x1 + x2)/2, (y1 + y2)/2, null);
	  }
    	  g.drawLine(x1, y1, x2, y2);
        /*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
    }
    
    public boolean hasASpy() {
	return hasASpy;
    }
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        componentMenu.addSeparator();
        JMenuItem generate = null;
        // Should verify first whether it is connected to a formal requirement with a verify relation, or not
	if (hasASpy) {
	    generate = new JMenuItem(NO_SPY);
	} else {
	    generate = new JMenuItem(ADD_SPY);
	}
	

        generate.addActionListener(menuAL);
        componentMenu.add(generate);
    }
    public int getType() {
        return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
    }
	
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info priority=\"");
		sb.append(priority);
        sb.append("\" />\n");
	sb.append("<spy value=\"" + hasASpy + "\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
    @Override
	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            //int t1id;
			String prio;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
								prio = elt.getAttribute("priority");
								if (elt != null) {
									priority = Integer.decode(prio).intValue();
								}
                            }
			    if (elt.getTagName().equals("spy")) {
                                String tmp = elt.getAttribute("value").trim();
				//TraceManager.addDev("[DD] value=" + tmp);
				if (tmp.compareTo("true") == 0) {
				    hasASpy = true;
				}
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
	
    public boolean eventOnPopup(ActionEvent e) {
        String s = e.getActionCommand();
        TraceManager.addDev("action: " + s);
      
        if (s.indexOf(NO_SPY) > -1) {
        	hasASpy = false;
        	tdp.repaint();
        }
	
        if (s.indexOf(ADD_SPY) > -1) {
        	hasASpy = true;
        	tdp.repaint();
        } 
            
        return true;
    }
    
    public TMLArchiCPUNode getOriginNode() {
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
	}
  
}
