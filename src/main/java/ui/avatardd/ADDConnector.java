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




package ui.avatardd;


import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Class ADDConnectorNode
 * Connector used in TML Architecture diagrams
 * Creation: 30/06/2014
 * @version 1.0 30/06/2014
 * @author Ludovic APVRILLE
 */
public  class ADDConnector extends TGConnector  {
    public static final String NO_SPY = "Remove spy";
    public static final String ADD_SPY = "Add spy";
    
    protected int arrowLength = 10;
    protected int widthValue, heightValue, maxWidthValue, h;

    protected boolean hasASpy;
	
    
    public ADDConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "{info}";
        editable = true;
        p1 = _p1;	
        p2 = _p2;
    }
    

    public TGConnectingPoint get_p1(){
    	return p1;
	}

    public TGConnectingPoint get_p2(){
    	return p2;
	}

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        /*JDialogTMLConnectorNode dialog = new JDialogTMLConnectorNode(frame, "Setting connector attributes", this);
		dialog.setSize(350, 300);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		priority = dialog.getPriority();*/
			
		return true;
    }

    @Override
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
    	  g.drawLine(x1, y1, x2, y2);

	  if (hasASpy) {
	      g.drawImage(IconManager.img5200, (x1 + x2)/2, (y1 + y2)/2, null);
	  }
	  
        /*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
    }

    public boolean hasASpy() {
	return hasASpy;
    }

    @Override
    public int getType() {
        return TGComponentManager.ADD_CONNECTOR;
    }


    @Override
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

    @Override
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

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<spy value=\"" + hasASpy + "\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            //int t1id;
            hasASpy = false;
            String tmp = null;

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("spy")) {
                                tmp = elt.getAttribute("value").trim();
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
   
    
}
