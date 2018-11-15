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




package ui.ebrdd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 * Class TGConnectorEBRDDERC
 * Basic connector with a full arrow at the end. Used in EBRDDs.
 * Creation: 09/09/2009
 * @version 1.0 09/09/2009
 * @author Ludovic APVRILLE
 */
public  class TGConnectorEBRDDERC extends TGConnector {
    protected int arrowLength = 10;
	protected boolean negation = false;
	protected static int negateSize = 10;
    
    public TGConnectorEBRDDERC(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic1052;
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }
		if (negation) {
			g.drawOval((x1+x2-negateSize)/2, (y1+y2-negateSize)/2, negateSize, negateSize);
		}
    }
    
    public int getType() {
        return TGComponentManager.CONNECTOR_EBRDD_ERC;
    }
	
	public void reverseNegation() {
		negation = !negation;
	}
	
	public boolean canBeNegated() {
		return true;
	}
	
	public boolean getNegation() {
		return negation;
	}
	
	 protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<negation value=\"" + negation);
        sb.append("\" />\n");
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
    
            String val = null;
            
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
                            if (elt.getTagName().equals("negation")) {
                                val = elt.getAttribute("value").trim();
								//
                                negation = !val.equals("false");
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







