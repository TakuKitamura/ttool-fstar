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
 * Class GraphLib
 * Creation: 01/12/2003
 * @version 1.1 01/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;


public final class GraphicLib {
    
    // EPSILON
    private static double EPSILON = 0.00001;
	
	public static final int NORTH = 0;
	public static final int WEST = 1;
	public static final int SOUTH = 2;
	public static final int EAST = 3;
	
    
    // Arrow
    // Arrow type
    // 0 : <-->
    // 1 : -->
    // 2 : <--
    // 3 : ---
    
    // Arrow Head
    // 0 : |>
    // 1 : >
    
    private static int xPoints[] = new int[3];
    private static int yPoints[] = new int[3];
    public static int longueur = 10;
    private static double angle = 0.523598775598;
    
    public final static BasicStroke normalStroke = new BasicStroke(1.0f);
    public final static BasicStroke stroke = new BasicStroke(2.0f);
    public final static BasicStroke doubleStroke = new BasicStroke(4.0f);
    public final static BasicStroke wideStroke = new BasicStroke(8.0f);
    
    public static float dash1[] = {5.0f};
    public static BasicStroke dashed = new BasicStroke(1.0f,
    BasicStroke.CAP_BUTT,
    BasicStroke.JOIN_MITER,
    10.0f, dash1, 0.0f);
    
    public static void setNormalStroke(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(GraphicLib.normalStroke);
    }
    
    public static void setMediumStroke(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(GraphicLib.stroke);
    }
    
    public static void setHighStroke(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(GraphicLib.doubleStroke);
    }
    
    public static void setDashed() {
        
    }
    
    public static void doubleColorRect(Graphics g, int x, int y, int width, int height, Color color1, Color color2) {
        g.setColor(color1);
        g.drawLine(x, y, x +width, y);
        g.drawLine(x, y, x, y+height);
        g.setColor(color2);
        g.drawLine(x, y+height, x+width, y+height);
        g.drawLine(x+width, y, x+width, y+height);
    }
    
    public static void dashedLine(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(GraphicLib.dashed);
        g2.draw(new Line2D.Float(x1, y1, x2, y2));
        g2.setStroke(normalStroke);
    }
    
    public static void dashedRect(Graphics g, int x1, int y1, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(GraphicLib.dashed);
        g2.drawRect(x1, y1, width, height);
        g2.setStroke(normalStroke);
    }
    
    
     public static void dashedArrowWithLine(Graphics g, int type, int head, int length, int x1, int y1, int x2, int y2, boolean full) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(GraphicLib.dashed);
        g2.drawLine(x1, y1, x2, y2);
        g2.setStroke(normalStroke);
        
        // extremite 1
        if ((type == 0) || (type == 2)) {
            drawArrow(g, x1, y1, x2, y2, head, length, full);
        }
        
        // extremite 2
        if ((type == 0) || (type == 1)) {
            drawArrow(g, x2, y2, x1, y1, head, length, full);
        }
        
    }
    
    
    public static void arrowWithLine(Graphics g, int type, int head, int length, int x1, int y1, int x2, int y2, boolean full) {
     
        g.drawLine(x1, y1, x2, y2);
   
        // extremite 1
        if ((type == 0) || (type == 2)) {
            drawArrow(g, x1, y1, x2, y2, head, length, full);
        }
        
        // extremite 2
        if ((type == 0) || (type == 1)) {
            drawArrow(g, x2, y2, x1, y1, head, length, full);
        }
        
    }
    
    
    private static void  drawArrow(Graphics g, int x1, int y1, int x2, int y2, int head, int length, boolean full) {
        // changement de repere
        int x = x2 - x1;
        int y = y2 - y1;
        
        
        if (length == 0) {
            length = longueur;
        }
        
        // passage en cds polaires
        double ro = Conversion.ro(x, y);
        double alpha = Conversion.theta(x, y);
        
        // tete de la fleche
        xPoints[0] = x1;
        yPoints[0] = y1;
        xPoints[1] = (int)(longueur*Math.cos(alpha-angle) + x1);
        yPoints[1] = (int)(longueur*Math.sin(alpha-angle) + y1);
        xPoints[2] = (int)(longueur*Math.cos(alpha+angle) + x1);
        yPoints[2] = (int)(longueur*Math.sin(alpha+angle) + y1);
        
        
        if (full) {
             g.fillPolygon(xPoints, yPoints, 3);
        } else if (head == 0) {
            // head must be filled in white
            Color c = g.getColor();
            g.setColor(Color.WHITE);
            g.fillPolygon(xPoints, yPoints, 3);
            g.setColor(c);
        }
        
        if (head == 0) {
            g.drawPolygon(xPoints, yPoints, 3);
        } else {
            g.drawLine(x1, y1, xPoints[1], yPoints[1]);
            g.drawLine(x1, y1, xPoints[2], yPoints[2]);
        }
    }
    
    public static boolean isSegmentInRectangle(int x1, int y1, int x2, int y2, int x, int y, int width, int height) {
        int x11 = Math.min(x1, x2);
        int y11 = Math.min(y1, y2);
        
        int x22 = Math.max(x1, x2);
        int y22 = Math.max(y1, y2);
        
        if ((x11 < x) || (x22 > x+width)) {
            return false;
        }
        
        if ((y11 < y) || (y22 > y+height)) {
            return false;
        }
        
        return true;
        
    }
    
    
    // Says whether the point belongs to the segment
    // If no point can be found, return null
    // If lines are parallel, returns one of the point
    public static boolean pointBelongsToSegment(double x1, double y1, double x3, double y3, double x4, double y4) {
        
        //System.out.println("Point on segment : x1=" + x1 + " y1=" + y1);
        //System.out.println("x3=" + x3 + " y3=" + y3 +" x4=" + x4 + " y4=" + y4);
        double y33 = Math.min(y3, y4);
        double y44 = Math.max(y3, y4);
        
        if (x3 == x4) {
            // vertical line
            if (Math.abs(x1 - x3) < EPSILON) {
                if (((y1 >= y33) && (y44 >= y1)) || (Math.abs(y1-y44)<EPSILON) || (Math.abs(y1-y33)<EPSILON)) {
                    return true;
                }
            }
            return false;
        }
        
        //System.out.println("Toto121");
        
        double x33 = Math.min(x3, x4);
        double x44 = Math.max(x3, x4);
        if (((x1 >= x33) && (x44 >= x1)) || (Math.abs(x1-x44)<EPSILON) || (Math.abs(x1-y33)<EPSILON)) {
            double y11 = y3 + (y3-y4)*(x1-x3)/(x3-x4);
            //System.out.println("y1=" + y1 + " y11=" + y11);
            if (Math.abs(y1-y11)<EPSILON){
                //System.out.println("EPSILON");
                if (((y1 >= y33) && (y44 >= y1)) || (Math.abs(y1-y44)<EPSILON) || (Math.abs(y1-y33)<EPSILON)) {
                    //System.out.println("OK");
                    return true;
                }
            }
        }
        return false;
    }
    
    
    // returns the Point intersection between two segments
    // If no point can be found, return null
    // If segments are parallel, returns one of the point
    public static Point intersectionTwoSegments(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        int type1 = 0, type2 = 0;
        double x, y;
        //System.out.println("x1=" + x1 + " y1=" + y1 +" x2=" + x2 + " y2=" + y2);
        //System.out.println("x3=" + x3 + " y3=" + y3 +" x4=" + x4 + " y4=" + y4);
        
        if (x1 == x2) {
            if (y1 == y2) {
                // only one point is provided
                if (pointBelongsToSegment(x1, y1, x3, y3, x4, y4)) {
                    return new Point((int)x1, (int)y1);
                } else {
                    return null;
                }
            }
            // first line is vertical
            type1 = 1;
        }
        
        
        if (x3 == x4) {
            if (y3 == y4) {
                // only one point is provided
                if (pointBelongsToSegment(x3, y3, x1, y1, x2, y2)) {
                    return new Point((int)x3, (int)y3);
                } else {
                    return null;
                }
            }
            // line is vertical
            type2 = 1;
        }
        
        double y11 = Math.min(y1, y2);
        double y22 = Math.max(y1, y2);
        double y33 = Math.min(y3, y4);
        double y44 = Math.max(y3, y4);
        
        if ((type1 == 1) && (type2 == 1)) {
            // Both are vertical
            //System.out.println("Toto11");
            if (x1 != x3) {
                //System.out.println("Toto12");
                return null;
            } else {
                //System.out.println("Toto13");
                if (((y11 >= y33) && (y44 >= y11)) || (Math.abs(y11-y44)<EPSILON) || (Math.abs(y11-y33)<EPSILON)){
                    return new Point((int)x1, (int)y1);
                }
                if (((y22 >= y33) && (y44 >= y22)) || (Math.abs(y22-y44)<EPSILON) || (Math.abs(y22-y33)<EPSILON)){
                    return new Point((int)x2, (int)y2);
                }
                if (((y33 >= y11) && (y22 >= y33)) || (Math.abs(y33-y11)<EPSILON) || (Math.abs(y22-y33)<EPSILON)){
                    return new Point((int)x3, (int)y3);
                }
                if (((y44 >= y11) && (y22 >= y44))|| (Math.abs(y44-y11)<EPSILON) || (Math.abs(y22-y44)<EPSILON)) {
                    return new Point((int)x4, (int)y4);
                }
                return null;
            }
        }
        
        // at least one of them is not vertical
        if ((type1 == 1)  || (type2 == 1)){
            //System.out.println("Toto21");
            if (type1 == 1) {
                // we switch segments so that the second is vertical
                x = x1; y = y1;
                x1 = x3; y1 = y3; x3 = x; y3 = y;
                x= x2; y = y2;
                x2 = x4; y2 = y4; x4 = x; y4 = y;
            }
            
            // we are now sure the second one is vertical and not the first one
            y33 = Math.min(y3, y4);
            y44 = Math.max(y3, y4);
            
            // we calculate the point (x3, y) on the first segment
            
            y = (((y1-y2)*(x3-x1))/(x1 - x2))+y1;
            if ((y >= y33) && (y44 >= y)) {
                // the point belongs to the vertical segment
                // Does it also belongs to its original segment ?
                if (pointBelongsToSegment(x3, y, x1, y1, x2, y2)) {
                    return new Point((int)x3, (int)y);
                } else {
                    return null;
                }
            } else {
                return null;
            }
            
        }
        
        
        //System.out.println("Toto31");
        double den = ((y1-y2)/(x1-x2)) - ((y3-y4)/(x3-x4));
        if (Math.abs(den) < EPSILON) {
            //System.out.println("Toto32");
            // segments are parallel
            // common point ?
            if (pointBelongsToSegment(x1, y1, x3, y3, x4, y4)) {
                return new Point((int)x1, (int)y1);
            }
            if (pointBelongsToSegment(x2, y2, x3, y3, x4, y4)) {
                return new Point((int)x2, (int)y2);
            }
            if (pointBelongsToSegment(x3, y3, x1, y1, x2, y2)) {
                return new Point((int)x3, (int)y3);
            }
            if (pointBelongsToSegment(x4, y4, x1, y1, x2, y2)) {
                return new Point((int)x4, (int)y4);
            }
            
        } else {
            //System.out.println("Toto34");
            
            double num = y3 - y1 + (x1*(y1-y2)/(x1-x2)) - (x3*(y3-y4)/(x3-x4));
            x = num / den;
            double ya = ((y3-y4)*(x-x3))/(x3-x4) + y3;
            double yb = ((y1-y2)*(x-x1))/(x1-x2) + y1;
            //System.out.println("Toto35 x=" + x + " ya=" + ya + " yb=" + yb);
            if ((pointBelongsToSegment(x, ya, x1, y1, x2, y2)) && (pointBelongsToSegment(x, ya, x3, y3, x4, y4))) {
                return new Point((int)x, (int)ya);
            } else {
                return null;
            }
        }
        return null;
    }
    
    public static Point intersectionRectangleSegment(int x1, int y1, int width, int height, int x3, int y3, int x4, int y4) {
        Point p;
        
        // lelft
        //System.out.println("Left");
        p = intersectionTwoSegments(x1, y1, x1, y1 + height, x3, y3, x4, y4);
        if (p != null)
            return p;
        
        // upper
        //System.out.println("Upper");
        p = intersectionTwoSegments(x1, y1, x1+width, y1, x3, y3, x4, y4);
        if (p != null)
            return p;
        
        // right
        //System.out.println("Right");
        p = intersectionTwoSegments(x1+width, y1, x1+width, y1+height, x3, y3, x4, y4);
        if (p != null)
            return p;
        
        // lower
        //System.out.println("Lower");
        p = intersectionTwoSegments(x1, y1+height, x1+width, y1+height, x3, y3, x4, y4);
        if (p != null)
            return p;
        return null;
    }
    
    public static boolean isInRectangle(int x1, int y1, int x, int y, int width, int height) {
        if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return true;
        }
        return false;
    }
	
	public static Point putPointOnRectangle(int x1, int y1, int x, int y, int width, int height) {
		// Compute the four distances between the point and the four segments
		// Find the closer segment
		// Project the point on it
		// First done on x, then on y
		
		int d1, d2, d3, d4; // distance, nonclockwise, starting on the top 
		int d; // minimal distance
		Point p = new Point(); // returned point
		
		d1 = Math.abs(y-y1);
		d2 = Math.abs(x-x1);
		d3 = Math.abs(y+height-y1);
		d4 = Math.abs(x+width-x1);
		
		d = Math.min(Math.min(Math.min(d1, d2), d3), d4);
		

		if (d == d1) {
			p.x = x1;
			p.y = y;
		} else if (d == d2) {
			p.x = x;
			p.y = y1;
		} else if (d == d3) {
			p.x = x1;
			p.y = y + height;
		} else {
			p.x = x + width;
			p.y = y1;
		}
		
		return p;
		
	}
	
	// Returns the closer segment of the rectangle, from a given point (x1, y1)
	public static int getCloserOrientation (int x1, int y1, int x, int y, int width, int height) {
		// Compute the four distances between the point and the four segments
		// Find the closer segment
		// Project the point on it
		// First done on x, then on y
		
		int d1, d2, d3, d4; // distance, nonclockwise, starting on the top 
		int d; // minimal distance
		int ret; // returned orientation
		
		d1 = Math.abs(y-y1);
		d2 = Math.abs(x-x1);
		d3 = Math.abs(y+height-y1);
		d4 = Math.abs(x+width-x1);
		
		d = Math.min(Math.min(Math.min(d1, d2), d3), d4);
		
		if (d == d1) {
			ret = NORTH;
		} else if (d == d2) {
			ret = WEST;
		} else if (d == d3) {
			ret = SOUTH;
		} else {
			ret = EAST;
		}
		
		return ret;
	}
	
    
    public static void centerOnScreen(Window w) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        w.setLocation((screen.width - w.getSize().width)/2,(screen.height - w.getSize().height)/2);
    }
    
    public static void centerOnParent(Window w) {
        Window parent = w.getOwner();
        if (parent == null) {
            centerOnScreen(w);
            return;
        }
        Point p = parent.getLocation();
        w.setLocation(((parent.getSize().width - w.getSize().width)/2) + p.x, ((parent.getSize().height - w.getSize().height)/2) + p.y); 
    }
    
    // Trivial sorting algorithm as there are only a few tabs
    // maxIndex is a non valid index
    // Vector v contains elements that should be sorted the same way 
    public static void sortJTabbedPane(JTabbedPane jtp, Vector v, int beginIndex, int maxIndex) {
        //System.out.println("Sorting from " + beginIndex + " to " + maxIndex);
        if (beginIndex >= maxIndex) {
            return;
        }
        
        String s = jtp.getTitleAt(beginIndex);
        int index = beginIndex;
        // Search for the one to move
        for(int i=beginIndex+1; i<maxIndex; i++) {
            if (s.compareTo(jtp.getTitleAt(i)) > 0) {
                index = i;
                s = jtp.getTitleAt(i);
            }
        }
        
        if (index != beginIndex) {
            moveTabFromTo(jtp, v, index, beginIndex);
        }
        
        beginIndex ++;
        sortJTabbedPane(jtp, v, beginIndex, maxIndex);
    }
    
    public static void moveTabFromTo(JTabbedPane jtp, Vector v, int src, int dst) {
        
        // Get all the properties
        Component comp = jtp.getComponentAt(src);
        String label = jtp.getTitleAt(src);
        Icon icon = jtp.getIconAt(src);
        Icon iconDis = jtp.getDisabledIconAt(src);
        String tooltip = jtp.getToolTipTextAt(src);
        boolean enabled = jtp.isEnabledAt(src);
        int keycode = jtp.getMnemonicAt(src);
        int mnemonicLoc = jtp.getDisplayedMnemonicIndexAt(src);
        Color fg = jtp.getForegroundAt(src);
        Color bg = jtp.getBackgroundAt(src);
        
        // Remove the tab
        jtp.remove(src);
        
        // Add a new tab
        jtp.insertTab(label, icon, comp, tooltip, dst);
        
        // Restore all properties
        jtp.setDisabledIconAt(dst, iconDis);
        jtp.setEnabledAt(dst, enabled);
        jtp.setMnemonicAt(dst, keycode);
        jtp.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
        jtp.setForegroundAt(dst, fg);
        jtp.setBackgroundAt(dst, bg);
        
        Object o = v.elementAt(src);
        v.removeElementAt(src);
        v.insertElementAt(o, dst);
    }
	
	public static void draw3DRoundRectangle(Graphics g, int x, int y, int width, int height, int arc, Color fillColor, Color borderColor) {
		Color c = g.getColor();
		
		g.setColor(fillColor);
		g.fillRoundRect(x, y, width, height, arc, arc);
		g.setColor(fillColor.brighter());
		g.drawLine(x+1, y+(arc/2), x+1, y+height-(arc/2));
		g.drawLine(x+(arc/2), y+1, x+width-(arc/2), y+1);
		g.drawArc(x+1, y+1, arc, arc, -180, -90);
		g.drawArc(x+1, y+height-arc-1, arc, arc, 180, 45);
		g.drawArc(x+width-1-arc, y+1, arc, arc, 90, -45);
		g.setColor(fillColor.darker());
		g.drawLine(x+width-1, y+(arc/2), x+width-1, y+height-(arc/2));
		g.drawLine(x+(arc/2), y+height-1, x+width-(arc/2), y+height-1);
		g.drawArc(x+width-1-arc, y+height-1-arc, arc, arc, -90, 90);
		g.drawArc(x+1, y+height-arc-1, arc, arc, -135, 45);
		g.drawArc(x+width-1-arc, y+1, arc, arc, 45, -45);
		g.setColor(borderColor);
		g.drawRoundRect(x, y, width, height, arc, arc);
		
		
		
		g.setColor(c);
	}
}










