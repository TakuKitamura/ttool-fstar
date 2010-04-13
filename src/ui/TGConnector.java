/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TGConnector
 * High level view of connectors to be used in TURTLE diagrams
 * Creation: 22/12/2003
 * @version 1.0 22/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import myutil.*;

public abstract class TGConnector extends TGCWithInternalComponent {
    
    protected final static String XML_CONNECTOR_HEAD = "<CONNECTOR type=\"";
    protected final static String XML_ID = "\" id=\"";
    protected final static String XML_CONNECTOR_TAIL = "</CONNECTOR>";
    
    protected TGConnectingPoint p1, p2; // initial and destination connecting points.
    
    protected int cdx; // last mouse x position
    protected int cdy; // last mouse y position
    
    protected int popupx, popupy; //used when popupmenu is activated
    
    protected int DIST_Y = 20;
    
    protected boolean automaticDrawing = true; // Used when user select to enhance the diagram automatically
    
    // WARNING: point of connectors must be put first in the list of internal components ...
    
    public TGConnector(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        p1 = _p1;
        p2 = _p2;
        
        nbInternalTGComponent = _listPoint.size();
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        Point p;
        //System.out.println("nbInternalTGComponent" + nbInternalTGComponent);
        for(int i=0; i<nbInternalTGComponent; i++) {
            p = (Point)(_listPoint.elementAt(i));
            //System.out.println("p.x " + p.x + " p.y" + p.y + " minX" + _minX + " maxX" + _maxX);
            tgcomponent[i] = new TGCPointOfConnector(p.x, p.y, _minX, _maxX, _minY, _maxY, false, this, _tdp);
        }
        name = "connector from " + p1.getName() + " to " + p2.getName();
        
        canBeCloned = false;
        removable = true;
    }
	
	public int getIndexOfLastTGCPointOfConnector() {
		if (nbInternalTGComponent == 0) { return -1;}
		int index;
		for(index = 0; index<tgcomponent.length; index++) {
			if (!(tgcomponent[index] instanceof TGCPointOfConnector)) {
				break;
			}
		}
		index = index - 1;
		//TraceManager.addDev("Index=" + index);
		return index;
	}
	
	public boolean hasTGCPointOfConnector() {
		return (getIndexOfLastTGCPointOfConnector() != -1);
	}
	
	public boolean hasOtherInternalComponents() {
		return ((getIndexOfLastTGCPointOfConnector()+1) != getNbInternalTGComponent());
	}
	
	public int getFirstIndexOfOtherInternalComponents() {
		if (hasOtherInternalComponents()) {
			return getIndexOfLastTGCPointOfConnector() + 1;
		}
		return -1;
	}
    
    
    public void internalDrawing(Graphics g) {
        
        TGComponent p3, p4;
        
		
		if (hasTGCPointOfConnector())  {
            p3 = tgcomponent[0];
            p4 = tgcomponent[0];
            //TraceManager.addDev("p3.x " + p3.getX() + " p3.y " + p3.getY());
            drawMiddleSegment(g, p1.getX(), p1.getY(), p3.getX(), p3.getY());
            
            for(int i=0; i<getIndexOfLastTGCPointOfConnector(); i++) {
                p3 = tgcomponent[i];
                p4 = tgcomponent[i+1];
                drawMiddleSegment(g, p3.getX(), p3.getY(), p4.getX(), p4.getY());
            }
            drawLastSegment(g, p4.getX(), p4.getY(), p2.getX(), p2.getY());
        } else {
            drawLastSegment(g, p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
    }
    
    protected void drawMiddleSegment(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }
    
    protected abstract void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2);
    
    public TGConnectingPoint getTGConnectingPointP1() {
        return p1;
    }
    
    public TGConnectingPoint getTGConnectingPointP2() {
        return p2;
    }
    
    public void setP1(TGConnectingPoint p) {
        p1 = p;
    }
    
    public void setP2(TGConnectingPoint p) {
        p2 = p;
    }
    
    public boolean isP1(CDElement cd) {
        return (p1 == cd);
    }
    
    public boolean isP2(CDElement cd) {
        return (p2 == cd);
    }
    
    public boolean alignTGComponents() {
        int dist_y = p2.getY() - p1.getY();
        int dist_x = p2.getX() - p1.getX();;
        if ((dist_y < 0) ||(dist_x == 0)){
            return false;
        }
        
        TGComponent tgc;
        
        if (!(p2.getFather() instanceof TGComponent)) {
            return false;
        }
        
        if (p2.getFather() instanceof TGConnector) {
            return false;
        }
        
        tgc = ((TGComponent)(p2.getFather())).getTopFather();
        tgc.setCd(tgc.getX() - dist_x, tgc.getY());
        makeSquareWithoutMovingTGComponents();
        return true;
    }
    
    public boolean alignOrMakeSquareTGComponents() {
        int dist_y = p2.getY() - p1.getY();
        int dist_x = p2.getX() - p1.getX();;
        
        
        TGComponent tgc;
        
        if (!(p2.getFather() instanceof TGComponent)) {
            return false;
        }
        
        if (p2.getFather() instanceof TGConnector) {
            return false;
        }
        
        tgc = ((TGComponent)(p2.getFather())).getTopFather();
        if ((dist_y > 0) && (Math.abs(dist_x) < 30)){
            tgc.setCd(tgc.getX() - dist_x, tgc.getY());
        }
        makeSquareWithoutMovingTGComponents();
        return true;
    }
    
    public void makeSquareWithoutMovingTGComponents() {
        if ((p1 == null) ||(p2 == null)) {
            return;
        }
        
        int dist_y = p2.getY() - p1.getY();
        //int dist_x = p2.getX() - p1.getX();
        //TGComponent tgc;
        //TGComponent [] tgcomponentOld;
        //int nbInternalTGComponentOld;
        int i;
        
        if (dist_y > 0) {
            // algorithm 1
            // We need only two points
            // we complete to two Points if dist_x is really grater than dist_y
            
            //System.out.println("Algo1");
            
            /*if ((dist_x != 0) && ((dist_y/5) > dist_x))  {
                completePointsTo(2);
            }*/
            
            // greater than two, or equal to two
            // we cut in half The first half points to the fist cd, the others to the last
            for(i=0; i<getIndexOfLastTGCPointOfConnector()/2; i++) {
                tgcomponent[i].setCd(p1.getX(), p1.getY() + dist_y / 2);
            }
            for(i=(getIndexOfLastTGCPointOfConnector()/2); i<=getIndexOfLastTGCPointOfConnector(); i++) {
                tgcomponent[i].setCd(p2.getX(), p1.getY() + dist_y / 2);
            }
        } else {
            //System.out.println("Algo2");
            //algorithm 2: more complex
            // we need at least 4 points
            //System.out.println("Making square ...");
            int minXX = 500000, maxXX = 0, resX = 0;
            // search for the min x and maxX
            for (i=0; i<=getIndexOfLastTGCPointOfConnector(); i++) {
                minXX = Math.min(minXX, tgcomponent[i].getX());
                maxXX = Math.max(maxXX, tgcomponent[i].getX());
            }
            
            resX = 0;
            //System.out.println("p1.x = " + p1.getX() + " p2.x = " + p2.getX() + " minXX=" + minXX + "maxXX=" + maxXX);
            if (!((minXX == 500000) ||(getIndexOfLastTGCPointOfConnector() == -1))){
                /*if (Math.abs(minXX - p1.getX()) > Math.abs(maxXX - p1.getX())) {
                    resX = minXX;
                } else {
                    resX = maxXX;
                }*/
                
                
                
                /*if (p2.getX() < p1.getX()) {
                    resX = minXX;
                } else {
                    resX = maxXX;
                }*/
                
                //System.out.println("p1.x = " + p1.getX() + " p2.x = " + p2.getX() + " minXX=" + minXX + "maxXX=" + maxXX);
                if (p1.getX() < p2.getX()) {
                    if (minXX < p1.getX()) {
                        //System.out.println("min1");
                        resX = minXX;
                    } else {
                        if (maxXX > (p2.getX() + DIST_Y)) {
                            resX = maxXX;
                        }
                    }
                } else {
                    if (maxXX > p1.getX()) {
                        resX = maxXX;
                    } else {
                        if (minXX < (p1.getX() - DIST_Y)) {
                            resX = minXX;
                        } 
                    }
                }
            }
            if (resX == 0) {
                //System.out.println("setting resX");
                resX = (p2.getX() + p1.getX()) / 2;
            }
            completePointsTo(4);
			
			int lastIndex = getIndexOfLastTGCPointOfConnector() + 1;
            
            // we cut all points in four quaters
            for(i=0; i<lastIndex/4; i++) {
                tgcomponent[i].setCd(p1.getX(), p1.getY() + DIST_Y);
            }
            for(i=(lastIndex/4); i<lastIndex/2; i++) {
                tgcomponent[i].setCd(resX, p1.getY() + DIST_Y);
            }
            for(i=(lastIndex/2); i<3*lastIndex/4; i++) {
                tgcomponent[i].setCd(resX, p2.getY() - DIST_Y);
            }
            for(i=(3*lastIndex/4); i<lastIndex; i++) {
                tgcomponent[i].setCd(p2.getX(),  p2.getY() - DIST_Y);
            }
            //}
        }
    }
    
    private void completePointsTo(int desiredNbOfPoints) {
        int nbToAdd = desiredNbOfPoints - nbInternalTGComponent;
        //System.out.println("Adding a point nbToAdd = " + nbToAdd);
        for(int i=0; i<nbToAdd; i++) {
            // we create the points
            //System.out.println("Adding a point");
            addTGCPointOfConnector(p2.getX(), p2.getY());
        }
    }
    
    public int indexPointedSegment(int x1, int y1) {
        TGComponent p3, p4;
        
        if (hasTGCPointOfConnector()) {
            p3 = tgcomponent[0];
            p4 = p3;
            if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p3.getX(), p3.getY(), x1, y1)) < distanceSelected) {
                return 0;
            }
            for(int i=0; i<getIndexOfLastTGCPointOfConnector(); i++) {
                p3 = tgcomponent[i];
                p4 = tgcomponent[i+1];
                
                if ((int)(Line2D.ptSegDistSq(p3.getX(), p3.getY(), p4.getX(), p4.getY(), x1, y1)) < distanceSelected) {
                    return i+1;
                }
            }
            
            if ((int)(Line2D.ptSegDistSq(p4.getX(), p4.getY(), p2.getX(), p2.getY(), x1, y1)) < distanceSelected) {
                return nbInternalTGComponent;
            }
            
        } else {
            if (p2 != null) {
                if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x1, y1)) < distanceSelected) {
                    return 0;
                }
            }
        }
        return -1;
    }
    
    public CDElement [] closerPToClickFirst(int x, int y) {
        CDElement [] cde = new CDElement[2];
        int distance1 = (int)(new Point(x, y).distance(p1.getX(), p1.getY()));
        int distance2 = (int)(new Point(x, y).distance(p2.getX(), p2.getY()));
        
        if (distance1 < distance2) {
            cde[0] = p1;
            cde[1] = p2;
        } else {
            cde[0] = p2;
            cde[1] = p1;
        }
        
        return cde;
    }
    
    public CDElement[] getPointedSegment(int x1, int y1) {
        TGComponent p3, p4;
        CDElement [] pt = new CDElement[2];
        
        if (hasTGCPointOfConnector()) {
            p3 = tgcomponent[0];
            p4 = p3;
            if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p3.getX(), p3.getY(), x1, y1)) < distanceSelected) {
                pt[0] = p1;
                pt[1] = p3;
                return pt;
            }
            for(int i=0; i<getIndexOfLastTGCPointOfConnector(); i++) {
                p3 = tgcomponent[i];
                p4 = tgcomponent[i+1];
                
                if ((int)(Line2D.ptSegDistSq(p3.getX(), p3.getY(), p4.getX(), p4.getY(), x1, y1)) < distanceSelected) {
                    pt[0] = p3;
                    pt[1] = p4;
                    return pt;
                }
            }
            
            if ((int)(Line2D.ptSegDistSq(p4.getX(), p4.getY(), p2.getX(), p2.getY(), x1, y1)) < distanceSelected) {
                pt[0] = p4;
                pt[1] = p2;
                return pt;
            }
            
        } else {
            if (p2 != null) {
                if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x1, y1)) < distanceSelected) {
                    pt[0] = p1;
                    pt[1] = p2;
                    return pt;
                }
            }
        }
        return null;
    }
    
    public boolean areAllInRectangle(int x1, int y1, int width1, int height1) {
        //System.out.println("width: " + width + " height: " + height);
        TGComponent p3, p4;
        
        if (hasTGCPointOfConnector()) {
            p3 = tgcomponent[0];
            p4 = p3;
            if (!GraphicLib.isSegmentInRectangle(p1.getX(), p1.getY(), p3.getX(), p3.getY(), x1, y1, width1, height1)) {
                return false;
            }
            
            for(int i=0; i<getIndexOfLastTGCPointOfConnector(); i++) {
                p3 = tgcomponent[i];
                p4 = tgcomponent[i+1];
                if  (!GraphicLib.isSegmentInRectangle(p3.getX(), p3.getY(), p4.getX(), p4.getY(), x1, y1, width1, height1)) {
                    return false;
                }
                
            }
            
            if  (!GraphicLib.isSegmentInRectangle(p4.getX(), p4.getY(), p2.getX(), p2.getY(), x1, y1, width1, height1)) {
                return false;
            }
            
            
        } else {
            if (p2 != null) {
                if  (!GraphicLib.isSegmentInRectangle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x1, y1, width1, height1)) {
                    return false;
                }
            }
        }
		
		if (hasOtherInternalComponents()) {
			for(int j=getFirstIndexOfOtherInternalComponents(); j<tgcomponent.length; j++) {
				if (!tgcomponent[j].isInRectangle(x1, y1, width1, height1)) {
					//TraceManager.addDev("Not in rectangle");
					return false;
				}
			}
		}
        
        return true;
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        TGComponent p3, p4;
		int i;
        
        if (hasTGCPointOfConnector()) {
            p3 = tgcomponent[0];
            p4 = p3;
            if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p3.getX(), p3.getY(), x1, y1)) < distanceSelected) {
                return this;
            }
            for(i=0; i<getIndexOfLastTGCPointOfConnector(); i++) {
                p3 = tgcomponent[i];
                p4 = tgcomponent[i+1];
                
                if ((int)(Line2D.ptSegDistSq(p3.getX(), p3.getY(), p4.getX(), p4.getY(), x1, y1)) < distanceSelected) {
                    return this;
                }
            }
            
            if ((int)(Line2D.ptSegDistSq(p4.getX(), p4.getY(), p2.getX(), p2.getY(), x1, y1)) < distanceSelected) {
                return this;
            }
            
        } else {
            if (p2 != null) {
                if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x1, y1)) < distanceSelected) {
                    return this;
                }
            }
        }
		
		if (hasOtherInternalComponents()) {
			for(i=getFirstIndexOfOtherInternalComponents(); i<tgcomponent.length; i++) {
				if (tgcomponent[i].isOnMe(x1, y1) != null) {
					return this;
				}
			}
		}
		
        return extraIsOnOnlyMe(x1, y1);
    }
    
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {
        return null;
    }
    
    public void pointHasBeenRemoved(TGCPointOfConnector tgc) {
        return;
    }
    
    
    // indexCon indicates from which points the potential connecitng point is the closer
    public void pointHasBeenAdded(TGCPointOfConnector tgc, int index, int indexCon) {
        return;
    }
    
    private boolean addTGCPointOfConnector(int x, int y) {
        CDElement [] pt = getPointedSegment(x, y);
                /*System.out.println("Two pts");
                System.out.println("p1  x=" + pt[0].x + " y=" + pt[0].y);
                System.out.println("p2  x=" + pt[1].x + " y=" + pt[1].y);*/
        if (pt != null) {
            Point p = new Point((pt[0].getX() + pt[1].getX()) / 2, (pt[0].getY() + pt[1].getY()) / 2);
            int distance1 = (int)(new Point(x, y).distance(pt[0].getX(), pt[0].getY()));
            int distance2 = (int)(new Point(x, y).distance(pt[1].getX(), pt[1].getY()));
            int index = indexPointedSegment(x, y);
            int indexCon;
            
            if (distance1 < distance2) {
                indexCon = 0;
            } else {
                indexCon = 1;
            }
            
            TGCPointOfConnector t = new TGCPointOfConnector(p.x, p.y, minX, maxX, minY, maxY, false, this, tdp);
            if (addInternalComponent(t, index) ) {
                pointHasBeenAdded(t, index, indexCon);
                return true;
            }
        }
        return false;
    }
    
    public void setAutomaticDrawing(boolean b) {
        automaticDrawing = b;
    }
    
    public boolean getAutomaticDrawing() {
        return  automaticDrawing;
    }
    
    public boolean isPtOnVerticalSegment(TGConnectingPoint tgcp) {
        int index = indexOf(tgcp);
        if (index == -1) {
            return false;
        }
        
        int p1x, p1y, p2x, p2y;
        if (index == 0) {
            p1x = p1.getX();
            p1y = p1.getY();
        } else {
            p1x = tgcomponent[index-1].getX();
            p1y = tgcomponent[index-1].getY();
        }
        
        if (index == (getNbConnectingPoint() - 1)) {
            p2x = p2.getX();
            p2y = p2.getY();
        } else {
            p2x = tgcomponent[index].getX();
            p2y = tgcomponent[index].getY();
        }
        
        if (Math.abs(p1x - p2x) < Math.abs(p1y - p2y)) {
            return true;
        }
        return false;
        
    }
    
    public StringBuffer saveInXML() {
        StringBuffer sb = new StringBuffer(XML_CONNECTOR_HEAD);
        sb.append(getType());
        sb.append(XML_ID);
        sb.append(getId());
        sb.append("\" >\n");
        sb.append(translateCDParam());
        sb.append(translateSizeParam());
        sb.append(translateNameValue());
        sb.append(translateConnectingPoints());
        sb.append(translateP1());
        sb.append(translateP2());
        sb.append(translatePoints());
        sb.append(translateAutomaticDrawing());
        sb.append(translateExtraParam());
        sb.append(XML_CONNECTOR_TAIL);
        sb.append(translateSubComponents());
        return  sb;
    }
    
    public String translateP1() {
        int id = p1.getId();
        return ("<P1  x=\"" + p1.getX() + "\" y=\"" + p1.getY() + "\" id=\"" + id + "\" />\n");
    }
    
    public String translateP2() {
        int id = p2.getId();
        return ("<P2  x=\"" + p2.getX() + "\" y=\"" + p2.getY() + "\" id=\"" + id + "\" />\n");
    }
    
    public StringBuffer translatePoints() {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<getIndexOfLastTGCPointOfConnector()+1; i++) {
            if (tgcomponent[i] instanceof TGCPointOfConnector) {
                sb.append("<Point x=\"" + tgcomponent[i].getX() + "\" y=\"" + tgcomponent[i].getY() + "\" />\n");
            }
        }
        return sb;
    }
    
    public String translateAutomaticDrawing() {
        if (automaticDrawing) {
            return ("<AutomaticDrawing  data=\"true\" />\n");
        } else {
            return ("<AutomaticDrawing  data=\"false\" />\n");
        }
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        popupx = x;
        popupy = y;
        componentMenu.addSeparator();
        JMenuItem addPoint = new JMenuItem("Add Point");;
        addPoint.addActionListener(menuAL);
        componentMenu.add(addPoint);
        JMenuItem align = new JMenuItem("Align");;
        align.addActionListener(menuAL);
        componentMenu.add(align);
        JMenuItem automatic;
        if (automaticDrawing) {
            automatic = new JMenuItem("NO automatic drawing");
        } else {
            automatic = new JMenuItem("Activate automatic drawing");
        }
		
        automatic.addActionListener(menuAL);
        componentMenu.add(automatic);
		
		JMenuItem negation;
		if (canBeNegated()) {
			if (getNegation()) {
				negation = new JMenuItem("Negation (to off)");
			} else {
				negation = new JMenuItem("Negation (to on)");
			}
			negation.addActionListener(menuAL);
			componentMenu.add(negation);
		}
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        if (e.getActionCommand().equals("Add Point")) {
            return addTGCPointOfConnector(popupx, popupy);
        } else if (e.getActionCommand().equals("Add Point")){
            return alignTGComponents();
        } else if (e.getActionCommand().equals("NO automatic drawing")){
            automaticDrawing = false;
            return true;
        } else if (e.getActionCommand().equals("Activate automatic drawing")) {
            automaticDrawing = true;
            return true;
        } else if (e.getActionCommand().startsWith("Negation ")) {
			reverseNegation();
		}
        return false;
    }
    
    public void reverse() {
		TGComponent[] tgcomponentnew = new TGComponent[nbInternalTGComponent];
		TGConnectingPoint tmp;
		
		if (hasTGCPointOfConnector()) {
			
			int i;
			
			for(i=0; i<getIndexOfLastTGCPointOfConnector()+1; i++) {
				tgcomponentnew[i] = tgcomponent[getIndexOfLastTGCPointOfConnector()-i];
			}
			
			if (hasOtherInternalComponents()) {
				for(i=getFirstIndexOfOtherInternalComponents(); i<getNbInternalTGComponent()+1; i++) {
					tgcomponentnew[i] = tgcomponent[i];
				}
			}
		}
        
        tmp = p2;
        p2 = p1;
        p1 = tmp;
        
		tgcomponent = tgcomponentnew;
    }
	
	
	// Middle of the last segment
	public void drawAttributes(Graphics g, String attr) {
        int s0=4, s1=9, s2=30, s3=10;
		int x1, y1;
		
		// Set x and y correctly -> x1 and y1
		if (!hasTGCPointOfConnector()) {
			x1 = (p1.getX() + p2.getX()) / 2;
			y1 = (p1.getY() + p2.getY()) / 2;
		} else {
			x1 = (tgcomponent[getIndexOfLastTGCPointOfConnector()].getX() + p2.getX()) / 2;
			y1 = (tgcomponent[getIndexOfLastTGCPointOfConnector()].getY() + p2.getY()) / 2;
		}
        
        g.fillOval(x1-s0, y1-s0, s1, s1);
        GraphicLib.dashedLine(g, x1, y1, x1+s2, y1);
        GraphicLib.dashedLine(g, x1+s2, y1, x1+s2+s3, y1+s3);
        
        Point p1 = drawCode(g, attr, x1+s2+s3, y1+s3, true, false, 10);
        
        int w = p1.x;
        int h = p1.y-y1+s3;
        GraphicLib.dashedRect(g, x1+s2+s3, y1+s3, w+15, h-12);
        
    }
	
	public void reverseNegation() {
	}
	
	public boolean canBeNegated() {
		return false;
	}
	
	public boolean getNegation() {
		return false;
	}
    
}
