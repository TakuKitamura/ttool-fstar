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




package ui.het;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Vector;


/**
 * Class SystemCAMSConnectingPoint
 * Definition of connecting points on which attribute connectors can be connected
 * Creation: 27/06/2017
 * @version 1.0 27/06/2017
 * @author Côme Demarigny
 */
public class CAMSBlockConnector extends  TGConnector {
    
    public CAMSBlockConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, CAMSConnectingPoint _p1, CAMSConnectingPoint _p2, Vector<Point> _listPoint){
	super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;

	name = "connector";
				_p1.setReferenceToConnector( this );
				_p2.setReferenceToConnector( this );

    }
     
    public int getType() {
        return TGComponentManager.CAMS_CONNECTOR;
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }
    }

    // protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
    //     Point p_one;
    //     boolean isp1;
    //     if (getIndexOfLastTGCPointOfConnector() == -1) {
    //         p_one = new Point(p1.getX(), p1.getY());
    //         isp1 = true;
    //     } else {
    //         TGComponent tmpc = tgcomponent[getIndexOfLastTGCPointOfConnector()];
    //         p_one = new Point(tmpc.getX(), tmpc.getY());
    //         isp1 = false;
    //     }
	
    //     //g.drawLine(x1, y1, x2, y2);
    //     Color col = g.getColor();
    //     int cz = (int)(tdp.getZoom() * c);
    //     if (isAsynchronous()) {
    //         g.setColor(Color.WHITE);
    //     }
    //     g.fillRect(x2-(cz/2), y2-(cz/2), cz, cz);
    //     g.fillRect(p1.getX()-(cz/2), p1.getY()-(cz/2), cz, cz);
    //     g.setColor(col);
    //     if (isAsynchronous()) {
    //         g.drawRect(x2-(cz/2), y2-(cz/2), cz, cz);
    //         g.drawRect(p1.getX()-(cz/2), p1.getY()-(cz/2), cz, cz);
    //         if (isBlocking()) {
    //             g.drawLine(x2-(cz/2), y2-(cz/2), x2-(cz/2)+cz, y2-(cz/2)+cz);
    //             g.drawLine(x2-(cz/2), y2-(cz/2)+cz, x2-(cz/2)+cz, y2-(cz/2));
    //             g.drawLine(p1.getX()-(cz/2), p1.getY()+(cz/2), p1.getX()+(cz/2), p1.getY()-(cz/2));
    //             g.drawLine(p1.getX()-(cz/2), p1.getY()-(cz/2), p1.getX()+(cz/2), p1.getY()+(cz/2));
    //         }
    //     }


    // 	if (!isPrivate() /*&& !isAsynchronous()*/) {
    // 	    int czz = (int)(cz*1.4);
    // 	    int x3 = p1.getX();
    // 	    int y3 = p1.getY();

    // 	    Polygon p1 = new Polygon();
    // 	    p1.addPoint(x2-(czz/2)+czz, y2-cz);
    // 	    p1.addPoint(x2+(czz/2)+czz, y2-cz);
    // 	    p1.addPoint(x2+czz, y2-(2*czz));

    // 	    Polygon p2 = new Polygon();
    // 	    p2.addPoint(x3-(czz/2)+czz, y3-cz);
    // 	    p2.addPoint(x3+(czz/2)+czz, y3-cz);
    // 	    p2.addPoint(x3+czz, y3-(2*czz));

    // 	    // Adding illuminatis sign at the end
    // 	    g.setColor(Color.WHITE);
    // 	    g.fillPolygon(p1);
    // 	    g.fillPolygon(p2);

    // 	    g.setColor(col);
    // 	    g.drawPolygon(p1);
    // 	    g.drawPolygon(p2);
    // 	    g.drawOval(x2+czz-4, y2-cz-7, 8, 6); 
    // 	    g.drawOval(x3+czz-4, y3-cz-7, 8, 6); 
    // 	    g.fillOval(x2+czz-2, y2-cz-6, 5, 4); 
    // 	    g.fillOval(x3+czz-2, y3-cz-6, 5, 4); 	    
	    
    // 	}


    //     Point p11;
    //     if (isp1) {
    //         p11 = GraphicLib.intersectionRectangleSegment(p1.getX()-(cz/2), p1.getY()-(cz/2), cz, cz, x1, y1, x2, y2);
    //     } else {
    //         p11 = new Point(p_one.x, p_one.y);
    //     }
    //     if (p11 == null) {
    //         p11 = new Point(p1.getX(), p1.getY());
    //         //System.out.println("null point");
    //     }
    //     Point p22 = GraphicLib.intersectionRectangleSegment(x2-(cz/2), y2-(cz/2), cz, cz, x1, y1, x2, y2);
    //     if (p22 == null) {
    //         p22 = new Point(p2.getX(), p2.getY());
    //         //System.out.println("null point");
    //     }

    //     g.drawLine(p11.x, p11.y, p22.x, p22.y);

    //     Font f = g.getFont();
    //     Font fold = f;
    //     f = f.deriveFont((float)fontSize);
    //     g.setFont(f);
    //     int h = - decY;
    //     int step = fontSize + 1;
    //     int w;
    //     String s;


    //     if (((g.getColor() == ColorManager.POINTER_ON_ME_0) && (tdp.getAttributeState() == tdp.PARTIAL)) || (tdp.getAttributeState() == tdp.FULL)) {
    //         // Signals at origin
    //         if (inSignalsAtOrigin.size() > 0) {
    //             //g.drawString("in:", p1.getX() + decX, p1.getY() + h);
    //             for(String iso: inSignalsAtOrigin) {
    //                 h += step;
    //                 s = getShortName(iso);
    //                 if (p1.getX() <= p2.getX()) {
    //                     g.drawString(s, p1.getX() + decX, p1.getY() + h);
    //                 } else {
    //                     w = g.getFontMetrics().stringWidth(s);
    //                     g.drawString(s, p1.getX() - decX - w, p1.getY() + h);
    //                 }
    //             }
    //         }
    //         if (outSignalsAtOrigin.size() > 0) {
    //             //h += step;
    //             //g.drawString("out:", p1.getX() + decX, p1.getY() + h);
    //             for(String oso: outSignalsAtOrigin) {
    //                 h += step;
    //                 s = getShortName(oso);
    //                 if (p1.getX() <= p2.getX()) {
    //                     g.drawString(s, p1.getX() + decX, p1.getY() + h);
    //                 } else {
    //                     w = g.getFontMetrics().stringWidth(s);
    //                     g.drawString(s, p1.getX() - decX - w, p1.getY() + h);
    //                 }
    //             }
    //         }
    //         // Signals at destination
    //         h = - decY;
    //         if (outSignalsAtDestination.size() > 0) {
    //             //h += step;
    //             //g.drawString("out:", p2.getX() + decX, p2.getY() + h);
    //             for(String osd: outSignalsAtDestination) {
    //                 h += step;
    //                 s = getShortName(osd);
    //                 if (p1.getX() > p2.getX()) {
    //                     g.drawString(s, p2.getX() + decX, p2.getY() + h);
    //                 } else {
    //                     w = g.getFontMetrics().stringWidth(s);
    //                     g.drawString(s, p2.getX() - decX - w, p2.getY() + h);
    //                 }
    //             }
    //         }
    //         if (inSignalsAtDestination.size() > 0) {
    //             //g.drawString("in:", p2.getX() + decX, p2.getY() + h);
    //             for(String isd: inSignalsAtDestination) {
    //                 h += step;
    //                 s = getShortName(isd);
    //                 if (p1.getX() > p2.getX()) {
    //                     g.drawString(s, p2.getX() + decX, p2.getY() + h);
    //                 } else {
    //                     w = g.getFontMetrics().stringWidth(s);
    //                     g.drawString(s, p2.getX() - decX - w, p2.getY() + h);
    //                 }
    //             }
    //         }
    //     }

    //     g.setFont(fold);

    //     /*if (value.length() > 0) {
    //       Font f = g.getFont();
    //       if (tdp.getZoom() < 1) {
    //       Font f0 =  f.deriveFont((float)(fontSize*tdp.getZoom()));
    //       g.setFont(f0);
    //       }
    //       g.drawString(value, x2-(cz/2), y2-(cz/2)-1);
    //       g.setFont(f);
    //       }*/

    //     // Animation?
    //     if ((TDiagramPanel.AVATAR_ANIMATE_ON) && (isAsynchronous())){
    //         //TraceManager.addDev("anim port connector: " + this);
    //         String messageInformation[] = tdp.getMGUI().hasMessageInformationForAvatarConnector(this);
    //         if (messageInformation != null) {
    //             if (messageInformation[0] != null) {
    //                 g.setColor(Color.BLUE);
    //                 g.drawString(messageInformation[0], p1.getX() + decX, p1.getY());
    //             }
    //             if (messageInformation[1] != null) {
    //                 g.setColor(Color.BLUE);
    //                 g.drawString(messageInformation[1], p2.getX() + decX, p2.getY());
    //             }
    //             g.setColor(Color.BLACK);
    //         }
    //     }
    // }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            //int t1id;
            String tmp = null;

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
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
} //class
